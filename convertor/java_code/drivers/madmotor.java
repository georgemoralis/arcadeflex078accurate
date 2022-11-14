/***************************************************************************

  Mad Motor								(c) 1989 Mitchell Corporation

  But it's really a Data East game..  Bad Dudes era graphics hardware with
  Dark Seal era sound hardware.  Maybe a license for a specific territory?

  "This game is developed by Mitchell, but they entrusted PCB design and some
  routines to Data East."

  Emulation by Bryan McPhail, mish@tendril.co.uk

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class madmotor
{
	
	VIDEO_START( madmotor );
	VIDEO_UPDATE( madmotor );
	
	READ16_HANDLER( madmotor_pf1_rowscroll_r );
	WRITE16_HANDLER( madmotor_pf1_rowscroll_w );
	READ16_HANDLER( madmotor_pf1_data_r );
	READ16_HANDLER( madmotor_pf2_data_r );
	READ16_HANDLER( madmotor_pf3_data_r );
	WRITE16_HANDLER( madmotor_pf1_data_w );
	WRITE16_HANDLER( madmotor_pf2_data_w );
	WRITE16_HANDLER( madmotor_pf3_data_w );
	WRITE16_HANDLER( madmotor_pf1_control_w );
	WRITE16_HANDLER( madmotor_pf2_control_w );
	WRITE16_HANDLER( madmotor_pf3_control_w );
	extern data16_t *madmotor_pf1_rowscroll;
	extern data16_t *madmotor_pf1_data,*madmotor_pf2_data,*madmotor_pf3_data;
	
	
	/******************************************************************************/
	
	static WRITE16_HANDLER( madmotor_sound_w )
	{
		if (ACCESSING_LSB)
		{
			soundlatch_w(0,data & 0xff);
			cpu_set_irq_line(1,0,HOLD_LINE);
		}
	}
	
	
	/******************************************************************************/
	
	static MEMORY_READ16_START( madmotor_readmem )
		{ 0x000000, 0x07ffff, MRA16_ROM },
		{ 0x184000, 0x1847ff, madmotor_pf1_rowscroll_r },
		{ 0x188000, 0x189fff, madmotor_pf1_data_r },
		{ 0x198000, 0x1987ff, madmotor_pf2_data_r },
		{ 0x1a4000, 0x1a4fff, madmotor_pf3_data_r },
		{ 0x18c000, 0x18c001, MRA16_NOP },
		{ 0x19c000, 0x19c001, MRA16_NOP },
		{ 0x3e0000, 0x3e3fff, MRA16_RAM },
		{ 0x3e8000, 0x3e87ff, MRA16_RAM },
		{ 0x3f0000, 0x3f07ff, MRA16_RAM },
		{ 0x3f8002, 0x3f8003, input_port_0_word_r },
		{ 0x3f8004, 0x3f8005, input_port_1_word_r },
		{ 0x3f8006, 0x3f8007, input_port_2_word_r },
	MEMORY_END
	
	static MEMORY_WRITE16_START( madmotor_writemem )
		{ 0x000000, 0x07ffff, MWA16_ROM },
		{ 0x180000, 0x18001f, madmotor_pf1_control_w },
		{ 0x184000, 0x1847ff, madmotor_pf1_rowscroll_w, &madmotor_pf1_rowscroll },
		{ 0x188000, 0x189fff, madmotor_pf1_data_w, &madmotor_pf1_data },
		{ 0x18c000, 0x18c001, MWA16_NOP },
		{ 0x190000, 0x19001f, madmotor_pf2_control_w },
		{ 0x198000, 0x1987ff, madmotor_pf2_data_w, &madmotor_pf2_data },
		{ 0x1a0000, 0x1a001f, madmotor_pf3_control_w },
		{ 0x1a4000, 0x1a4fff, madmotor_pf3_data_w, &madmotor_pf3_data },
		{ 0x3e0000, 0x3e3fff, MWA16_RAM },
		{ 0x3e8000, 0x3e87ff, MWA16_RAM, &spriteram16, &spriteram_size },
		{ 0x3f0000, 0x3f07ff, paletteram16_xxxxBBBBGGGGRRRR_word_w, &paletteram16 },
		{ 0x3fc004, 0x3fc005, madmotor_sound_w },
	MEMORY_END
	
	/******************************************************************************/
	
	static WRITE_HANDLER( YM2151_w )
	{
		switch (offset) {
		case 0:
			YM2151_register_port_0_w(0,data);
			break;
		case 1:
			YM2151_data_port_0_w(0,data);
			break;
		}
	}
	
	static WRITE_HANDLER( YM2203_w )
	{
		switch (offset) {
		case 0:
			YM2203_control_port_0_w(0,data);
			break;
		case 1:
			YM2203_write_port_0_w(0,data);
			break;
		}
	}
	
	/* Physical memory map (21 bits) */
	static MEMORY_READ_START( sound_readmem )
		{ 0x000000, 0x00ffff, MRA_ROM },
		{ 0x100000, 0x100001, YM2203_status_port_0_r },
		{ 0x110000, 0x110001, YM2151_status_port_0_r },
		{ 0x120000, 0x120001, OKIM6295_status_0_r },
		{ 0x130000, 0x130001, OKIM6295_status_1_r },
		{ 0x140000, 0x140001, soundlatch_r },
		{ 0x1f0000, 0x1f1fff, MRA_BANK8 },
	MEMORY_END
	
	static MEMORY_WRITE_START( sound_writemem )
		{ 0x000000, 0x00ffff, MWA_ROM },
		{ 0x100000, 0x100001, YM2203_w },
		{ 0x110000, 0x110001, YM2151_w },
		{ 0x120000, 0x120001, OKIM6295_data_0_w },
		{ 0x130000, 0x130001, OKIM6295_data_1_w },
		{ 0x1f0000, 0x1f1fff, MWA_BANK8 },
		{ 0x1fec00, 0x1fec01, H6280_timer_w },
		{ 0x1ff402, 0x1ff403, H6280_irq_status_w },
	MEMORY_END
	
	/******************************************************************************/
	
	INPUT_PORTS_START( madmotor )
		PORT_START
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY )
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY )
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY )
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY )
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 )
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 )
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_UNUSED )	/* button 3 - unused */
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_START1 )
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 )
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 )
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 )
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 )
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 )
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 )
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_UNUSED )	/* button 3 - unused */
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_START2 )
	
		PORT_START
		PORT_DIPNAME( 0x0007, 0x0007, DEF_STR( Coin_A ) )
		PORT_DIPSETTING(      0x0000, DEF_STR( 3C_1C ) )
		PORT_DIPSETTING(      0x0001, DEF_STR( 2C_1C ) )
		PORT_DIPSETTING(      0x0007, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(      0x0006, DEF_STR( 1C_2C ) )
		PORT_DIPSETTING(      0x0005, DEF_STR( 1C_3C ) )
		PORT_DIPSETTING(      0x0004, DEF_STR( 1C_4C ) )
		PORT_DIPSETTING(      0x0003, DEF_STR( 1C_5C ) )
		PORT_DIPSETTING(      0x0002, DEF_STR( 1C_6C ) )
		PORT_DIPNAME( 0x0038, 0x0038, DEF_STR( Coin_B ) )
		PORT_DIPSETTING(      0x0000, DEF_STR( 3C_1C ) )
		PORT_DIPSETTING(      0x0008, DEF_STR( 2C_1C ) )
		PORT_DIPSETTING(      0x0038, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(      0x0030, DEF_STR( 1C_2C ) )
		PORT_DIPSETTING(      0x0028, DEF_STR( 1C_3C ) )
		PORT_DIPSETTING(      0x0020, DEF_STR( 1C_4C ) )
		PORT_DIPSETTING(      0x0018, DEF_STR( 1C_5C ) )
		PORT_DIPSETTING(      0x0010, DEF_STR( 1C_6C ) )
		PORT_DIPNAME( 0x0040, 0x0040, DEF_STR( Flip_Screen ) )
		PORT_DIPSETTING(      0x0040, DEF_STR( Off ) )
		PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
		PORT_DIPNAME( 0x0080, 0x0000, DEF_STR( Unknown ) )
		PORT_DIPSETTING(      0x0080, DEF_STR( Off ) )
		PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
		PORT_DIPNAME( 0x0300, 0x0300, DEF_STR( Lives ) )
		PORT_DIPSETTING(      0x0000, "2" )
		PORT_DIPSETTING(      0x0300, "3" )
		PORT_DIPSETTING(      0x0200, "4" )
		PORT_DIPSETTING(      0x0100, "5" )
		PORT_DIPNAME( 0x0c00, 0x0c00, DEF_STR( Difficulty ) )
		PORT_DIPSETTING(      0x0800, "Easy" )
		PORT_DIPSETTING(      0x0c00, "Normal" )
		PORT_DIPSETTING(      0x0400, "Hard" )
		PORT_DIPSETTING(      0x0000, "Hardest" )
		PORT_DIPNAME( 0x1000, 0x0000, DEF_STR( Unknown ) )
		PORT_DIPSETTING(      0x1000, DEF_STR( Off ) )
		PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
		PORT_DIPNAME( 0x2000, 0x0000, DEF_STR( Unknown ) )
		PORT_DIPSETTING(      0x2000, DEF_STR( Off ) )
		PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
		PORT_DIPNAME( 0x4000, 0x4000, "Allow Continue" )
		PORT_DIPSETTING(      0x0000, DEF_STR( No ) )
		PORT_DIPSETTING(      0x4000, DEF_STR( Yes ) )
		PORT_DIPNAME( 0x8000, 0x0000, DEF_STR( Demo_Sounds ) )
		PORT_DIPSETTING(      0x8000, DEF_STR( Off ) )
		PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	
		PORT_START	/* Credits */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 )
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN3 )
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_VBLANK )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN )
	INPUT_PORTS_END
	
	/******************************************************************************/
	
	static struct GfxLayout charlayout =
	{
		8,8,	/* 8*8 chars */
		4096,
		4,		/* 4 bits per pixel  */
		{ 0x18000*8, 0x8000*8, 0x10000*8, 0x00000*8 },
		{ 0, 1, 2, 3, 4, 5, 6, 7 },
		{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8	/* every char takes 8 consecutive bytes */
	};
	
	static struct GfxLayout tilelayout =
	{
		16,16,
		2048,
		4,
		{ 0x30000*8, 0x10000*8, 0x20000*8, 0x00000*8 },
		{ 16*8+0, 16*8+1, 16*8+2, 16*8+3, 16*8+4, 16*8+5, 16*8+6, 16*8+7,
				0, 1, 2, 3, 4, 5, 6, 7 },
		{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
		16*16
	};
	
	static struct GfxLayout tilelayout2 =
	{
		16,16,
		4096,
		4,
		{ 0x60000*8, 0x20000*8, 0x40000*8, 0x00000*8 },
		{ 16*8+0, 16*8+1, 16*8+2, 16*8+3, 16*8+4, 16*8+5, 16*8+6, 16*8+7,
				0, 1, 2, 3, 4, 5, 6, 7 },
		{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
		16*16
	};
	
	static struct GfxLayout spritelayout =
	{
		16,16,
		4096*2,
		4,
		{ 0xc0000*8, 0x80000*8, 0x40000*8, 0x00000*8 },
		{ 16*8+0, 16*8+1, 16*8+2, 16*8+3, 16*8+4, 16*8+5, 16*8+6, 16*8+7,
				0, 1, 2, 3, 4, 5, 6, 7 },
		{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
		16*16
	};
	
	static struct GfxDecodeInfo gfxdecodeinfo[] =
	{
		{ REGION_GFX1, 0, &charlayout,     0, 16 },	/* Characters 8x8 */
		{ REGION_GFX2, 0, &tilelayout,   512, 16 },	/* Tiles 16x16 */
		{ REGION_GFX3, 0, &tilelayout2,  768, 16 },	/* Tiles 16x16 */
		{ REGION_GFX4, 0, &spritelayout, 256, 16 },	/* Sprites 16x16 */
		{ -1 } /* end of array */
	};
	
	/******************************************************************************/
	
	static struct OKIM6295interface okim6295_interface =
	{
		2,              /* 2 chips */
		{ 7757, 15514 },/* ?? Frequency */
		{ REGION_SOUND1, REGION_SOUND2 },	/* memory regions */
		{ 50, 25 }		/* Note!  Keep chip 1 (voices) louder than chip 2 */
	};
	
	static struct YM2203interface ym2203_interface =
	{
		1,
		21470000/6,	/* ?? Audio section crystal is 21.470 MHz */
		{ YM2203_VOL(40,40) },
		{ 0 },
		{ 0 },
		{ 0 },
		{ 0 }
	};
	
	static void sound_irq(int state)
	{
		cpu_set_irq_line(1,1,state); /* IRQ 2 */
	}
	
	static struct YM2151interface ym2151_interface =
	{
		1,
		21470000/6, /* ?? Audio section crystal is 21.470 MHz */
		{ YM3012_VOL(45,MIXER_PAN_LEFT,45,MIXER_PAN_RIGHT) },
		{ sound_irq }
	};
	
	static MACHINE_DRIVER_START( madmotor )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 12000000) /* Custom chip 59, 24 MHz crystal */
		MDRV_CPU_MEMORY(madmotor_readmem,madmotor_writemem)
		MDRV_CPU_VBLANK_INT(irq6_line_hold,1)/* VBL */
	
		MDRV_CPU_ADD(H6280, 8053000/2) /* Custom chip 45, Crystal near CPU is 8.053 MHz */
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
	
		MDRV_FRAMES_PER_SECOND(58)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION) /* frames per second, vblank duration taken from Burger Time */
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 1*8, 31*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(1024)
	
		MDRV_VIDEO_START(madmotor)
		MDRV_VIDEO_UPDATE(madmotor)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2203, ym2203_interface)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(OKIM6295, okim6295_interface)
	MACHINE_DRIVER_END
	
	/******************************************************************************/
	
	ROM_START( madmotor )
		ROM_REGION( 0x80000, REGION_CPU1, 0 ) /* 68000 code */
		ROM_LOAD16_BYTE( "02", 0x00000, 0x20000, CRC(50b554e0) SHA1(e33d0ab5464ab5ff394dd630536ac83baf0aa2c9) )
		ROM_LOAD16_BYTE( "00", 0x00001, 0x20000, CRC(2d6a1b3f) SHA1(fa7058bf907becac56ed9938c5643aaefdf7a2c0) )
		ROM_LOAD16_BYTE( "03", 0x40000, 0x20000, CRC(442a0a52) SHA1(86bb5470d5653d125481250f778c632371dddad8) )
		ROM_LOAD16_BYTE( "01", 0x40001, 0x20000, CRC(e246876e) SHA1(648dca8bab001cfb42618081bbc1efa14118743e) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* Sound CPU */
		ROM_LOAD( "14",    0x00000, 0x10000, CRC(1c28a7e5) SHA1(ed30d0a5a8a079677bd34b6d98ab1b15b934b30f) )
	
		ROM_REGION( 0x020000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "04",    0x000000, 0x10000, CRC(833ca3ab) SHA1(7a3e7ebecc1596d2e487595369ad9ba54ced5bfb) )	/* chars */
		ROM_LOAD( "05",    0x010000, 0x10000, CRC(a691fbfe) SHA1(c726a4c15d599feb6883d9b643453e7028fa16d6) )
	
		ROM_REGION( 0x040000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "10",    0x000000, 0x20000, CRC(9dbf482b) SHA1(086e9170d577e502604c180f174fbce53a1e20e5) )	/* tiles */
		ROM_LOAD( "11",    0x020000, 0x20000, CRC(593c48a9) SHA1(1158888f6b836253b8ae9db9b8e352f289b2e815) )
	
		ROM_REGION( 0x080000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "06",    0x000000, 0x20000, CRC(448850e5) SHA1(6a44a42738cf6a55b4bec807e0a3939a42b36793) )	/* tiles */
		ROM_LOAD( "07",    0x020000, 0x20000, CRC(ede4d141) SHA1(7b847372bac043aa397aa5c274f90b9193de9176) )
		ROM_LOAD( "08",    0x040000, 0x20000, CRC(c380e5e5) SHA1(ec87a94e7948b84c96b1577f5a8caebc56e38a94) )
		ROM_LOAD( "09",    0x060000, 0x20000, CRC(1ee3326a) SHA1(bd03e5c4a2e7689260e6cc67288e71ef13f05a4b) )
	
		ROM_REGION( 0x100000, REGION_GFX4, ROMREGION_DISPOSE )
		ROM_LOAD( "15",    0x000000, 0x20000, CRC(90ae9f74) SHA1(806f96fd08fca1beeeaefe3c0fac1991410aa9c4) )	/* sprites */
		ROM_LOAD( "16",    0x020000, 0x20000, CRC(e96ac815) SHA1(a2b22a29ad0a4f144bb09299c454dc7a842a5318) )
		ROM_LOAD( "17",    0x040000, 0x20000, CRC(abad9a1b) SHA1(3cec6b4ef925205efe4a8fb28e08eb58e3ba4019) )
		ROM_LOAD( "18",    0x060000, 0x20000, CRC(96d8d64b) SHA1(54ce87fe2b14b574176d2a1d2b86057b9cd10883) )
		ROM_LOAD( "19",    0x080000, 0x20000, CRC(cbd8c9b8) SHA1(5e86c0298b3eea06920121eecb70e5bee705addf) )
		ROM_LOAD( "20",    0x0a0000, 0x20000, CRC(47f706a8) SHA1(bd4fe499710f8905eb4b8d1ca990f2908feb95e1) )
		ROM_LOAD( "21",    0x0c0000, 0x20000, CRC(9c72d364) SHA1(9290e463273fa1f921279f1bab808d91d3aa9648) )
		ROM_LOAD( "22",    0x0e0000, 0x20000, CRC(1e78aa60) SHA1(f5f58ee6f5efe56e72623e57ce27884551e09bd9) )
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 )	/* ADPCM samples */
		ROM_LOAD( "12",    0x00000, 0x20000, CRC(c202d200) SHA1(8470654923a0e8780dad678f5745f8e3e3be08b2) )
	
		ROM_REGION( 0x20000, REGION_SOUND2, 0 )	/* ADPCM samples */
		ROM_LOAD( "13",    0x00000, 0x20000, CRC(cc4d65e9) SHA1(b9bcaa52c570f94d2f2e5dd84c94773cc4115442) )
	ROM_END
	
	/******************************************************************************/
	
	static DRIVER_INIT( madmotor )
	{
		unsigned char *rom = memory_region(REGION_CPU1);
		int i;
	
		for (i = 0x00000;i < 0x80000;i++)
		{
			rom[i] = (rom[i] & 0xdb) | ((rom[i] & 0x04) << 3) | ((rom[i] & 0x20) >> 3);
			rom[i] = (rom[i] & 0x7e) | ((rom[i] & 0x01) << 7) | ((rom[i] & 0x80) >> 7);
		}
	}
	
	
	 /* The title screen is undated, but it's (c) 1989 Data East at 0xefa0 */
	public static GameDriver driver_madmotor	   = new GameDriver("1989"	,"madmotor"	,"madmotor.java"	,rom_madmotor,null	,machine_driver_madmotor	,input_ports_madmotor	,init_madmotor	,ROT0, "Mitchell", "Mad Motor" )
}
