/***************************************************************************

Hot Pinball
Gals Pinball

driver by Nicola Salmoria

Notes:
- to start a 2 or more players game, press the start button multiple times
- the sprite graphics contain a "(c) Tecmo", and the sprite system is
  indeed similar to other Tecmo games like Ninja Gaiden.


TODO:
- scrolling is wrong.
- sprite/tile priority might be wrong. There is an unknown bit in the fg
  tilemap, marking some tiles that I'm not currently drawing.
- coin insertion is not recognized consistenly.
- almost all dip switches unknown

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class galspnbl
{
	
	
	
	WRITE16_HANDLER( galspnbl_bgvideoram_w );
	WRITE16_HANDLER( galspnbl_scroll_w );
	
	
	static WRITE16_HANDLER( soundcommand_w )
	{
		if (ACCESSING_LSB)
		{
			soundlatch_w(offset,data & 0xff);
			cpu_set_irq_line(1,IRQ_LINE_NMI,PULSE_LINE);
		}
	}
	
	
	static MEMORY_READ16_START( readmem )
		{ 0x000000, 0x3fffff, MRA16_ROM },
		{ 0x700000, 0x703fff, MRA16_RAM },	/* galspnbl */
		{ 0x708000, 0x70ffff, MRA16_RAM },	/* galspnbl */
		{ 0x800000, 0x803fff, MRA16_RAM },	/* hotpinbl */
		{ 0x808000, 0x80ffff, MRA16_RAM },	/* hotpinbl */
		{ 0x880000, 0x880fff, MRA16_RAM },
		{ 0x900000, 0x900fff, MRA16_RAM },
		{ 0x904000, 0x904fff, MRA16_RAM },
		{ 0x980000, 0x9bffff, MRA16_RAM },
		{ 0xa80000, 0xa80001, input_port_0_word_r },
		{ 0xa80010, 0xa80011, input_port_1_word_r },
		{ 0xa80020, 0xa80021, input_port_2_word_r },
		{ 0xa80030, 0xa80031, input_port_3_word_r },
		{ 0xa80040, 0xa80041, input_port_4_word_r },
	MEMORY_END
	
	static MEMORY_WRITE16_START( writemem )
		{ 0x000000, 0x3fffff, MWA16_ROM },
		{ 0x700000, 0x703fff, MWA16_RAM },	/* galspnbl work RAM */
		{ 0x708000, 0x70ffff, MWA16_RAM },	/* galspnbl work RAM, bitmaps are decompressed here */
		{ 0x800000, 0x803fff, MWA16_RAM },	/* hotpinbl work RAM */
		{ 0x808000, 0x80ffff, MWA16_RAM },	/* hotpinbl work RAM, bitmaps are decompressed here */
		{ 0x880000, 0x880fff, MWA16_RAM, &spriteram16, &spriteram_size },
	{ 0x8ff400, 0x8fffff, MWA16_NOP },	/* ??? */
		{ 0x900000, 0x900fff, MWA16_RAM, &galspnbl_colorram },
	{ 0x901000, 0x903fff, MWA16_NOP },	/* ??? */
		{ 0x904000, 0x904fff, MWA16_RAM, &galspnbl_videoram },
	{ 0x905000, 0x907fff, MWA16_NOP },	/* ??? */
		{ 0x980000, 0x9bffff, galspnbl_bgvideoram_w, &galspnbl_bgvideoram },
	{ 0xa00000, 0xa00fff, MWA16_NOP },	/* more palette ? */
		{ 0xa01000, 0xa017ff, paletteram16_xxxxBBBBGGGGRRRR_word_w, &paletteram16 },
	{ 0xa01800, 0xa027ff, MWA16_NOP },	/* more palette ? */
		{ 0xa80010, 0xa80011, soundcommand_w },
		{ 0xa80020, 0xa80021, MWA16_NOP },	/* could be watchdog, but causes resets when picture is shown */
		{ 0xa80030, 0xa80031, MWA16_NOP },	/* irq ack? */
		{ 0xa80050, 0xa80051, galspnbl_scroll_w },
	MEMORY_END
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xf7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xf800, 0xf800, OKIM6295_status_0_r ),
		new Memory_ReadAddress( 0xfc00, 0xfc00, MRA_NOP ),	/* irq ack ?? */
		new Memory_ReadAddress( 0xfc20, 0xfc20, soundlatch_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xf7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xf800, 0xf800, OKIM6295_data_0_w ),
		new Memory_WriteAddress( 0xf810, 0xf810, YM3812_control_port_0_w ),
		new Memory_WriteAddress( 0xf811, 0xf811, YM3812_write_port_0_w ),
		new Memory_WriteAddress( 0xfc00, 0xfc00, MWA_NOP ),	/* irq ack ?? */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	
	static InputPortHandlerPtr input_ports_hotpinbl = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( hotpinbl )
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON3 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON4 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Slide Show" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_galspnbl = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( galspnbl )
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON3 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON4 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Slide Show" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout tilelayout = new GfxLayout
	(
		16,8,
		RGN_FRAC(1,2),
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 0*4, 1*4, RGN_FRAC(1,2)+0*4, RGN_FRAC(1,2)+1*4, 2*4, 3*4, RGN_FRAC(1,2)+2*4, RGN_FRAC(1,2)+3*4,
				16*8+0*4, 16*8+1*4, 16*8+RGN_FRAC(1,2)+0*4, 16*8+RGN_FRAC(1,2)+1*4, 16*8+2*4, 16*8+3*4, 16*8+RGN_FRAC(1,2)+2*4, 16*8+RGN_FRAC(1,2)+3*4 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		32*8
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,2),
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 0, 4, RGN_FRAC(1,2)+0, RGN_FRAC(1,2)+4, 8+0, 8+4, 8+RGN_FRAC(1,2)+0, 8+RGN_FRAC(1,2)+4 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		16*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, tilelayout,   512, 16 ),
		new GfxDecodeInfo( REGION_GFX2, 0, spritelayout,   0, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static void irqhandler(int linestate)
	{
		cpu_set_irq_line(1,0,linestate);
	}
	
	static struct YM3812interface ym3812_interface =
	{
		1,			/* 1 chip */
		3579545,	/* 3.579545 MHz ? */
		{ 100 },	/* volume */
		{ irqhandler },
	};
	
	static struct OKIM6295interface okim6295_interface =
	{
		1,					/* 1 chip */
		{ 8000 },			/* 8000Hz frequency? */
		{ REGION_SOUND1 },	/* memory region */
		{ 50 }
	};
	
	
	
	public static MachineHandlerPtr machine_driver_hotpinbl = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 10000000)	/* 10 MHz ??? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(irq3_line_hold,1)/* also has vector for 6, but it does nothing */
	
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 4 MHz ??? */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
									/* NMI is caused by the main CPU */
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_PIXEL_ASPECT_RATIO_1_2)
		MDRV_SCREEN_SIZE(512, 256)
		MDRV_VISIBLE_AREA(0, 512-1, 16, 240-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(1024 + 32768)
		MDRV_COLORTABLE_LENGTH(1024)
	
		MDRV_PALETTE_INIT(galspnbl)
		MDRV_VIDEO_START(generic_bitmapped)
		MDRV_VIDEO_UPDATE(galspnbl)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM3812, ym3812_interface)
		MDRV_SOUND_ADD(OKIM6295, okim6295_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadHandlerPtr rom_galspnbl = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x400000, REGION_CPU1, 0 )	/* 68000 code */
		ROM_LOAD16_BYTE( "7.rom",        0x000000, 0x80000, CRC(ce0189bf) SHA1(06d8cc6f5b819fe2ca536ce553db6986547a15ba) )
		ROM_LOAD16_BYTE( "3.rom",        0x000001, 0x80000, CRC(9b0a8744) SHA1(ac80f22b8b2f4c559c225bf203af698bf59699e7) )
		ROM_LOAD16_BYTE( "8.rom",        0x100000, 0x80000, CRC(eee2f087) SHA1(37285ae7b49c9d20ad92b3971db89ba593975154) )
		ROM_LOAD16_BYTE( "4.rom",        0x100001, 0x80000, CRC(56298489) SHA1(6dce296d752fa5579e8a3c1db3e563c4f98b6bae) )
		ROM_LOAD16_BYTE( "9.rom",        0x200000, 0x80000, CRC(d9e4964c) SHA1(0bb63384ed57a6ee1d45d1f2c181172a3f0b39df) )
		ROM_LOAD16_BYTE( "5.rom",        0x200001, 0x80000, CRC(a5e71ee4) SHA1(f41ea4ab07be263157d6c1f4a96ef45c9bccfe39) )
		ROM_LOAD16_BYTE( "10.rom",       0x300000, 0x80000, CRC(3a20e1e5) SHA1(850be621547bc9c7519055211392f2684e440462) )
		ROM_LOAD16_BYTE( "6.rom",        0x300001, 0x80000, CRC(94927d20) SHA1(0ea1a179956ad9a93a99cccec92f0490044ad1d3) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* Z80 code */
		ROM_LOAD( "2.rom",        0x0000, 0x10000, CRC(fae688a7) SHA1(e1ef7abd18f6a820d1a7f0ceb9a9b1a2c7de41f0) )
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "17.rom",       0x00000, 0x40000, CRC(7d435701) SHA1(f6a2241c95f101d09b18f550689d125abd3ea9c4) )
		ROM_LOAD( "18.rom",       0x40000, 0x40000, CRC(136adaac) SHA1(5f5e70a66d256cad9fcdbc5a7fff035f9a3279b9) )
	
		ROM_REGION( 0x40000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "15.rom",       0x00000, 0x20000, CRC(4beb840d) SHA1(351cd8da361a55794595d2cf7b0fed9233d0a5a0) )
		ROM_LOAD( "16.rom",       0x20000, 0x20000, CRC(93d3c610) SHA1(0cf1f311ec2646a436c37e121634731646c06437) )
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 )	/* OKIM6295 samples */
		ROM_LOAD( "1.rom",        0x00000, 0x40000, CRC(93c06d3d) SHA1(8620d274ca7824e7e72a1ad1da3eaa804d550653) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_hotpinbl = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x400000, REGION_CPU1, 0 )	/* 68000 code */
		ROM_LOAD16_BYTE( "hp_07.bin",    0x000000, 0x80000, CRC(978cc13e) SHA1(0060aaf7259fdeeacb07e9ced01bdf69c27bdfb6) )
		ROM_LOAD16_BYTE( "hp_03.bin",    0x000001, 0x80000, CRC(68388726) SHA1(d8dca9050403be70097a0f833ba189bd2fa87e80) )
		ROM_LOAD16_BYTE( "hp_08.bin",    0x100000, 0x80000, CRC(bd16be12) SHA1(36e64705efba8ecdc96a62f55d68e959022fb98f) )
		ROM_LOAD16_BYTE( "hp_04.bin",    0x100001, 0x80000, CRC(655b0cf0) SHA1(4348d774155c61b1a29c2618f472a0032f4be9c0) )
		ROM_LOAD16_BYTE( "hp_09.bin",    0x200000, 0x80000, CRC(a6368624) SHA1(fa0b3dc4eb33a4b2d00e30676e59feec2fa64c3d) )
		ROM_LOAD16_BYTE( "hp_05.bin",    0x200001, 0x80000, CRC(48efd028) SHA1(8c968c4a73069a8f64e7bc339bab0eeb5254c580) )
		ROM_LOAD16_BYTE( "hp_10.bin",    0x300000, 0x80000, CRC(a5c63e34) SHA1(de27cfe20e09e8c13ee28d6eb42bfab1ebe33149) )
		ROM_LOAD16_BYTE( "hp_06.bin",    0x300001, 0x80000, CRC(513eda91) SHA1(14a43c00ad1f55bff525a14cd53913dd78e80f0c) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* Z80 code */
		ROM_LOAD( "hp_02.bin",    0x0000, 0x10000, CRC(82698269) SHA1(5e27e89f1bdd7c3793d40867c50981f5fac0a7fb) )
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "hp_13.bin",    0x00000, 0x40000, CRC(d53b64b9) SHA1(347b6ec908e23f848e98eed46fb34b49b728556b) )
		ROM_LOAD( "hp_14.bin",    0x40000, 0x40000, CRC(2fe3fcee) SHA1(29f96aa3dded6cb0b2fe3d9507fb5638e9778ef3) )
	
		ROM_REGION( 0x40000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "hp_11.bin",    0x00000, 0x20000, CRC(deecd7f1) SHA1(752c944d941bfe8f21d32881f32676999ebc5a7f) )
		ROM_LOAD( "hp_12.bin",    0x20000, 0x20000, CRC(5fd603c2) SHA1(864686cd1ba5beb6cebfd394b60620106c929abd) )
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 )	/* OKIM6295 samples */
		ROM_LOAD( "hp_01.bin",    0x00000, 0x40000, CRC(93c06d3d) SHA1(8620d274ca7824e7e72a1ad1da3eaa804d550653) )
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_hotpinbl	   = new GameDriver("1995"	,"hotpinbl"	,"galspnbl.java"	,rom_hotpinbl,null	,machine_driver_hotpinbl	,input_ports_hotpinbl	,null	,ROT90, "Comad & New Japan System", "Hot Pinball", GAME_NO_COCKTAIL )
	public static GameDriver driver_galspnbl	   = new GameDriver("1996"	,"galspnbl"	,"galspnbl.java"	,rom_galspnbl,null	,machine_driver_hotpinbl	,input_ports_galspnbl	,null	,ROT90, "Comad", "Gals Pinball", GAME_NO_COCKTAIL )
}
