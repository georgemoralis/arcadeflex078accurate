/***************************************************************************

							Ginga NinkyouDen
							(C) 1987 Jaleco

					driver by Luca Elia (l.elia@tin.it)

CPU   : 68000 68B09
SOUND : YM2149 Y8950(MSX AUDIO)
OSC.  : 6.000MHz 3.579545MHz

* CTC uses MB-8873E (MC-6840)

					Interesting routines (main cpu)
					-------------------------------

Interrupts: 1-7]	d17a:	clears 20018 etc.

f4b2	print string:	a1->(char)*,0x25(%) d7.w=color	a0->screen (30000)
f5d6	print 7 digit BCD number: d0.l to (a1)+ color $3000


					Interesting locations (main cpu)
					--------------------------------

20014	# of players (1-2)
20018	cleared by interrupts
2001c	credits (max 9)
20020	internal timer?
20024	initial lives
20058	current lives p1
2005c	current lives p2
20070	coins
200a4	time
200a8	energy

60008		values: 0 1 ffff
6000c		bit:	0	flip sceen? <-	70002>>14
					1	?			<-

6000e	soundlatch	<- 20038 2003c 20040


								To Do
								-----

- The sound section will benefit from proper MC6840 and YM8950 emulation

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class ginganin
{
	
	/* Variables only used here */
	
	/* Variables defined in vidhrdw */
	extern data16_t *ginganin_fgram16, *ginganin_txtram16, *ginganin_vregs16;
	
	/* Functions defined in vidhrdw */
	WRITE16_HANDLER( ginganin_fgram16_w );
	WRITE16_HANDLER( ginganin_txtram16_w );
	WRITE16_HANDLER( ginganin_vregs16_w );
	VIDEO_START( ginganin );
	VIDEO_UPDATE( ginganin );
	
	
	/*
	**
	**				Main cpu data
	**
	*/
	
	
	static MEMORY_READ16_START( readmem )
		{ 0x000000, 0x01ffff, MRA16_ROM },
		{ 0x020000, 0x023fff, MRA16_RAM },
		{ 0x030000, 0x0307ff, MRA16_RAM },
		{ 0x040000, 0x0407ff, MRA16_RAM },
		{ 0x050000, 0x0507ff, MRA16_RAM },
		{ 0x060000, 0x06000f, MRA16_RAM },
		{ 0x068000, 0x06bfff, MRA16_RAM },	/* bg lives in ROM */
		{ 0x070000, 0x070001, input_port_0_word_r }, /* controls */
		{ 0x070002, 0x070003, input_port_1_word_r }, /* DSWs */
	MEMORY_END
	
	static MEMORY_WRITE16_START( writemem )
	/* The ROM area: 10000-13fff is written with: 0000 0000 0000 0001, at startup only. Why? */
		{ 0x020000, 0x023fff, MWA16_RAM },
		{ 0x030000, 0x0307ff, ginganin_txtram16_w, &ginganin_txtram16 },
		{ 0x040000, 0x0407ff, MWA16_RAM, &spriteram16, &spriteram_size },
		{ 0x050000, 0x0507ff, paletteram16_RRRRGGGGBBBBxxxx_word_w, &paletteram16 },
		{ 0x060000, 0x06000f, ginganin_vregs16_w, &ginganin_vregs16 },
		{ 0x068000, 0x06bfff, ginganin_fgram16_w, &ginganin_fgram16 },
	MEMORY_END
	
	
	/*
	**
	**				Sound cpu data
	**
	*/
	
	/* based on snk.c: */
	
	/* Added by Takahiro Nogi. 1999/09/27 */
	static unsigned char MC6840_index0;
	static unsigned char MC6840_register0;
	static unsigned char MC6840_index1;
	static unsigned char MC6840_register1;
	static int S_TEMPO = 0;
	static int S_TEMPO_OLD = 0;
	static int MC6809_CTR = 0;
	static int MC6809_FLAG = 0;
	
	
	static WRITE_HANDLER( MC6840_control_port_0_w )
	{
		/* MC6840 Emulation by Takahiro Nogi. 1999/09/27
		(This routine hasn't been completed yet.) */
	
		MC6840_index0 = data;
	
		if (MC6840_index0 & 0x80)	/* enable timer output */
		{
			if ((MC6840_register0 != S_TEMPO) && (MC6840_register0 != 0))
			{
				S_TEMPO = MC6840_register0;
	#ifdef MAME_DEBUG
				usrintf_showmessage("I0:0x%02X R0:0x%02X I1:0x%02X R1:0x%02X", MC6840_index0, MC6840_register0, MC6840_index1, MC6840_register1);
	#endif
			}
			MC6809_FLAG = 1;
		}
		else
		{
			MC6809_FLAG = 0;
		}
	#ifdef MAME_DEBUG
		logerror("MC6840 Write:(0x%02X)0x%02X\n", MC6840_register0, data);
	#endif
	}
	
	static WRITE_HANDLER( MC6840_control_port_1_w )
	{
		/* MC6840 Emulation by Takahiro Nogi. 1999/09/27
		(This routine hasn't been completed yet.) */
	
		MC6840_index1 = data;
	}
	
	static WRITE_HANDLER( MC6840_write_port_0_w )
	{
		/* MC6840 Emulation by Takahiro Nogi. 1999/09/27
		(This routine hasn't been completed yet.) */
	
		MC6840_register0 = data;
	}
	
	static WRITE_HANDLER( MC6840_write_port_1_w )
	{
		/* MC6840 Emulation by Takahiro Nogi. 1999/09/27
		(This routine hasn't been completed yet.) */
	
		MC6840_register1 = data;
	}
	
	static MEMORY_READ_START( sound_readmem )
		{ 0x0000, 0x07ff, MRA_RAM },
		{ 0x1800, 0x1800, soundlatch_r },
		{ 0x4000, 0xffff, MRA_ROM },
	MEMORY_END
	
	
	static MEMORY_WRITE_START( sound_writemem )
		{ 0x0000, 0x07ff, MWA_RAM },
		{ 0x0800, 0x0800, MC6840_control_port_0_w },	/* Takahiro Nogi. 1999/09/27 */
		{ 0x0801, 0x0801, MC6840_control_port_1_w },	/* Takahiro Nogi. 1999/09/27 */
		{ 0x0802, 0x0802, MC6840_write_port_0_w },		/* Takahiro Nogi. 1999/09/27 */
		{ 0x0803, 0x0803, MC6840_write_port_1_w },		/* Takahiro Nogi. 1999/09/27 */
		{ 0x2000, 0x2000, Y8950_control_port_0_w },
		{ 0x2001, 0x2001, Y8950_write_port_0_w },
		{ 0x2800, 0x2800, AY8910_control_port_0_w },
		{ 0x2801, 0x2801, AY8910_write_port_0_w },
	MEMORY_END
	
	
	
	
	/*	Input Ports:	[0] Controls	[1] DSWs */
	
	INPUT_PORTS_START( ginganin )
	
		PORT_START	/* IN0 - Controls - Read from 70000.w */
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY )
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY )
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY )
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY )
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 )
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 )
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_PLAYER2 )
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_PLAYER2 )
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_PLAYER2 )
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 )
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 )
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 )
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_COIN1 )
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_START1 )
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_START2 )
	
		PORT_START	/* IN1 - DSWs - Read from 70002.w */
		PORT_DIPNAME( 0x0007, 0x0007, DEF_STR( Coin_A ) )
		PORT_DIPSETTING(	  0x0000, DEF_STR( 5C_1C ) )
		PORT_DIPSETTING(	  0x0004, DEF_STR( 4C_1C ) )
		PORT_DIPSETTING(	  0x0002, DEF_STR( 3C_1C ) )
		PORT_DIPSETTING(	  0x0006, DEF_STR( 2C_1C ) )
		PORT_DIPSETTING(	  0x0007, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(	  0x0003, DEF_STR( 1C_2C ) )
		PORT_DIPSETTING(	  0x0005, DEF_STR( 1C_3C ) )
		PORT_DIPSETTING(	  0x0001, DEF_STR( 1C_4C ) )
		PORT_DIPNAME( 0x0038, 0x0038, DEF_STR( Coin_B ) )
		PORT_DIPSETTING(	  0x0000, DEF_STR( 5C_1C ) )
		PORT_DIPSETTING(	  0x0020, DEF_STR( 4C_1C ) )
		PORT_DIPSETTING(	  0x0010, DEF_STR( 3C_1C ) )
		PORT_DIPSETTING(	  0x0030, DEF_STR( 2C_1C ) )
		PORT_DIPSETTING(	  0x0038, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(	  0x0018, DEF_STR( 1C_2C ) )
		PORT_DIPSETTING(	  0x0028, DEF_STR( 1C_3C ) )
		PORT_DIPSETTING(	  0x0008, DEF_STR( 1C_4C ) )
		PORT_BITX(	  0x0040, 0x0040, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Infinite Lives", IP_KEY_NONE, IP_JOY_NONE )
		PORT_DIPSETTING(	  0x0040, DEF_STR( Off ) )
		PORT_DIPSETTING(	  0x0000, DEF_STR( On ) )
		PORT_BITX(	  0x0080, 0x0080, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Free Play & Invulnerability", IP_KEY_NONE, IP_JOY_NONE )
		PORT_DIPSETTING(	  0x0080, DEF_STR( Off ) )
		PORT_DIPSETTING(	  0x0000, DEF_STR( On ) )
	
		PORT_DIPNAME( 0x0300, 0x0300, DEF_STR( Lives ) )
		PORT_DIPSETTING(	  0x0000, "2")
		PORT_DIPSETTING(	  0x0300, "3")
		PORT_DIPSETTING(	  0x0100, "4")
		PORT_DIPSETTING(	  0x0200, "5")
		PORT_DIPNAME( 0x0400, 0x0400, DEF_STR( Demo_Sounds ) )
		PORT_DIPSETTING(	  0x0000, DEF_STR( Off ) )
		PORT_DIPSETTING(	  0x0400, DEF_STR( On ) )
		PORT_DIPNAME( 0x0800, 0x0000, DEF_STR( Cabinet ) )
		PORT_DIPSETTING(	  0x0000, DEF_STR( Upright ) )
		PORT_DIPSETTING(	  0x0800, DEF_STR( Cocktail ) )
		PORT_DIPNAME( 0x1000, 0x1000, DEF_STR( Unknown ) )	/* probably unused */
		PORT_DIPSETTING(	  0x1000, DEF_STR( Off ) )
		PORT_DIPSETTING(	  0x0000, DEF_STR( On ) )
		PORT_DIPNAME( 0x2000, 0x2000, DEF_STR( Unknown ) )	/* it does something */
		PORT_DIPSETTING(	  0x2000, DEF_STR( Off ) )
		PORT_DIPSETTING(	  0x0000, DEF_STR( On ) )
		PORT_DIPNAME( 0x4000, 0x4000, DEF_STR( Flip_Screen ) )
		PORT_DIPSETTING(	  0x4000, DEF_STR( Off ) )
		PORT_DIPSETTING(	  0x0000, DEF_STR( On ) )
		PORT_BITX(	  0x8000, 0x8000, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Freeze", IP_KEY_NONE, IP_JOY_NONE )
		PORT_DIPSETTING(	  0x8000, DEF_STR( Off ) )
		PORT_DIPSETTING(	  0x0000, DEF_STR( On ) )
	
	INPUT_PORTS_END
	
	
	
	/*
	**
	**				Gfx data
	**
	*/
	
	
	#define layout16x16(_name_,_romsize_) \
	static struct GfxLayout _name_ =\
	{\
		16,16,\
		(_romsize_)*8/(16*16*4),\
		4,\
		{0, 1, 2, 3},\
		{0*4,1*4,2*4,3*4,4*4,5*4,6*4,7*4,\
		 0*4+32*16,1*4+32*16,2*4+32*16,3*4+32*16,4*4+32*16,5*4+32*16,6*4+32*16,7*4+32*16},\
		{0*32,1*32,2*32,3*32,4*32,5*32,6*32,7*32,\
		 8*32,9*32,10*32,11*32,12*32,13*32,14*32,15*32},\
		16*16*4\
	};
	
	#define layout8x8(_name_,_romsize_) \
	static struct GfxLayout _name_ =\
	{\
		8,8,\
		(_romsize_)*8/(8*8*4),\
		4,\
		{0, 1, 2, 3},\
		{0*4,1*4,2*4,3*4,4*4,5*4,6*4,7*4}, \
		{0*32,1*32,2*32,3*32,4*32,5*32,6*32,7*32},\
		8*8*4\
	};
	
	layout16x16(tilelayout,  0x20000)
	layout8x8  (txtlayout,	 0x04000)
	layout16x16(spritelayout,0x50000)
	
	static struct GfxDecodeInfo gfxdecodeinfo[] =
	{
		{ REGION_GFX1, 0, &tilelayout,	 256*3, 16 }, /* [0] bg */
		{ REGION_GFX2, 0, &tilelayout,	 256*2, 16 }, /* [1] fg */
		{ REGION_GFX3, 0, &txtlayout,	 256*0, 16 }, /* [2] txt */
		{ REGION_GFX4, 0, &spritelayout, 256*1, 16 }, /* [3] sprites */
		{ -1 }
	};
	
	
	
	
	/* Modified by Takahiro Nogi. 1999/09/27 */
	INTERRUPT_GEN( ginganin_sound_interrupt )
	{
		/* MC6840 Emulation by Takahiro Nogi. 1999/09/27
		(This routine hasn't been completed yet.) */
	
		if (S_TEMPO_OLD != S_TEMPO)
		{
			S_TEMPO_OLD = S_TEMPO;
			MC6809_CTR = 0;
		}
	
		if (MC6809_FLAG != 0)
		{
			if (MC6809_CTR > S_TEMPO)
			{
				MC6809_CTR = 0;
				cpu_set_irq_line(1, 0, HOLD_LINE);
			}
			else
			{
				MC6809_CTR++;
			}
		}
	}
	
	
	
	static struct AY8910interface AY8910_interface =
	{
		1,
		3579545 / 2 ,	/* ? */
		{ 10 },
		{ 0 },
		{ 0 },
		{ 0 },
		{ 0 }
	};
	
	
	/* The Y8950 is basically a YM3526 with ADPCM built in */
	static struct Y8950interface y8950_interface =
	{
		1,
		3579545,	/* ? */
		{ 100 },
		{ 0 },
		{ REGION_SOUND1 },	/* ROM region */
		{ 0 },	/* keyboarc read  */
		{ 0 },	/* keyboard write */
		{ 0 },	/* I/O read  */
		{ 0 }	/* I/O write */
	};
	
	static MACHINE_DRIVER_START( ginganin )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 6000000)	/* ? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(irq1_line_hold,1) /* ? (vectors 1-7 cointain the same address) */
	
		MDRV_CPU_ADD(M6809, 1000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* ? */ /* Takahiro Nogi. 1999/09/27 (3579545 -> 1000000) */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_VBLANK_INT(ginganin_sound_interrupt,60)	/* Takahiro Nogi. 1999/09/27 (1 -> 60) */
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(256, 256)
		MDRV_VISIBLE_AREA(0, 255, 0 + 16 , 255 - 16)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(1024)
	
		MDRV_VIDEO_START(ginganin)
		MDRV_VIDEO_UPDATE(ginganin)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, AY8910_interface)
		MDRV_SOUND_ADD(Y8950, y8950_interface)
	MACHINE_DRIVER_END
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadHandlerPtr rom_ginganin = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x20000, REGION_CPU1, 0 )	/* main cpu */
		ROM_LOAD16_BYTE( "gn_02.bin", 0x00000, 0x10000, CRC(4a4e012f) SHA1(7c94a5b6b71e037af355f3aa4623be1f585db8dc) )
		ROM_LOAD16_BYTE( "gn_01.bin", 0x00001, 0x10000, CRC(30256fcb) SHA1(dc15e0da88ae5cabe0150f7290508c3d58c06c11) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* sound cpu */
		ROM_LOAD( "gn_05.bin", 0x00000, 0x10000, CRC(e76e10e7) SHA1(b16f10a1a01b7b04221c9bf1b0d157e936bc5fb5) )
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "gn_15.bin", 0x000000, 0x10000, CRC(1b8ac9fb) SHA1(1e5ee2a565fa262f1e48c1088d84c6f42d84b4e3) )  /* bg */
		ROM_LOAD( "gn_14.bin", 0x010000, 0x10000, CRC(e73fe668) SHA1(fa39fddd7448d3fc6b539506e33b951db205afa1) )
	
		ROM_REGION( 0x20000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "gn_12.bin", 0x000000, 0x10000, CRC(c134a1e9) SHA1(8bace0f0169e61f1b7254393fa9cad6dca09c335) )  /* fg */
		ROM_LOAD( "gn_13.bin", 0x010000, 0x10000, CRC(1d3bec21) SHA1(305823c78cad9288f918178e1c24cb0459ba2a6e) )
	
		ROM_REGION( 0x04000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "gn_10.bin", 0x000000, 0x04000, CRC(ae371b2d) SHA1(d5e03b085586ed2bf40713f432bcf12e07318226) )  /* txt */
	
		ROM_REGION( 0x50000, REGION_GFX4, ROMREGION_DISPOSE )
		ROM_LOAD( "gn_06.bin", 0x000000, 0x10000, CRC(bdc65835) SHA1(53222fc3ec15e641289abb754657b0d59b88b66b) )  /* sprites */
		ROM_CONTINUE(		   0x040000, 0x10000 )
		ROM_LOAD( "gn_07.bin", 0x010000, 0x10000, CRC(c2b8eafe) SHA1(a042a200efd4e7361e9ab516085c9fc8067e28b4) )
		ROM_LOAD( "gn_08.bin", 0x020000, 0x10000, CRC(f7c73c18) SHA1(102700e2217bcd1532af56ee6a00ad608c8217db) )
		ROM_LOAD( "gn_09.bin", 0x030000, 0x10000, CRC(a5e07c3b) SHA1(cdda02cd847330575612cb33d1bb38a5d50a3e6d) )
	
		ROM_REGION( 0x08000, REGION_GFX5, 0 )	/* background tilemaps */
		ROM_LOAD( "gn_11.bin", 0x00000, 0x08000, CRC(f0d0e605) SHA1(0c541e8e036573be1d99ecb71fdb4568ca8cc269) )
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 )	/* samples */
		ROM_LOAD( "gn_04.bin", 0x00000, 0x10000, CRC(0ed9133b) SHA1(77f628e8ec28016efac2d906146865ca4ec54bd5) )
		ROM_LOAD( "gn_03.bin", 0x10000, 0x10000, CRC(f1ba222c) SHA1(780c0bd0045bac1e1bb3209576383db90504fbf3) )
	
	ROM_END(); }}; 
	
	
	
	DRIVER_INIT( ginganin )
	{
		data16_t *rom;
	
		/* main cpu patches */
		rom = (data16_t *)memory_region(REGION_CPU1);
		/* avoid writes to rom getting to the log */
		rom[0x408/2] = 0x6000;
		rom[0x40a/2] = 0x001c;
	
	
		/* sound cpu patches */
		/* let's clear the RAM: ROM starts at 0x4000 */
		memset(memory_region(REGION_CPU2),0,0x800);
	}
	
	
	public static GameDriver driver_ginganin	   = new GameDriver("1987"	,"ginganin"	,"ginganin.java"	,rom_ginganin,null	,machine_driver_ginganin	,input_ports_ginganin	,init_ginganin	,ROT0, "Jaleco", "Ginga NinkyouDen" )
	
}
