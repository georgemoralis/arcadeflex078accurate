/***************************************************************************


***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class citycon
{
	
	
	
	
	
	public static ReadHandlerPtr citycon_in_r  = new ReadHandlerPtr() { public int handler(int offset){
		return readinputport(flip_screen() ? 1 : 0);
	} };
	
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_RAM ),
		new Memory_ReadAddress( 0x3000, 0x3000, citycon_in_r ),	/* player 1  2 inputs multiplexed */
		new Memory_ReadAddress( 0x3001, 0x3001, input_port_2_r ),
		new Memory_ReadAddress( 0x3002, 0x3002, input_port_3_r ),
		new Memory_ReadAddress( 0x3007, 0x3007, watchdog_reset_r ),	/* ? */
		new Memory_ReadAddress( 0x4000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x0fff, MWA_RAM ),
		new Memory_WriteAddress( 0x1000, 0x1fff, citycon_videoram_w, citycon_videoram ),
		new Memory_WriteAddress( 0x2000, 0x20ff, citycon_linecolor_w, citycon_linecolor ),
		new Memory_WriteAddress( 0x2800, 0x28ff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0x3000, 0x3000, citycon_background_w ),
		new Memory_WriteAddress( 0x3001, 0x3001, soundlatch_w ),
		new Memory_WriteAddress( 0x3002, 0x3002, soundlatch2_w ),
		new Memory_WriteAddress( 0x3004, 0x3005, MWA_RAM, citycon_scroll ),
		new Memory_WriteAddress( 0x3800, 0x3cff, paletteram_RRRRGGGGBBBBxxxx_swap_w, paletteram ),
		new Memory_WriteAddress( 0x4000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_sound[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x0fff, MRA_RAM ),
	//	new Memory_ReadAddress( 0x4002, 0x4002, AY8910_read_port_0_r ),	/* ?? */
		new Memory_ReadAddress( 0x6001, 0x6001, YM2203_read_port_0_r ),
		new Memory_ReadAddress( 0x8000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_sound[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x0fff, MWA_RAM ),
		new Memory_WriteAddress( 0x4000, 0x4000, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0x4001, 0x4001, AY8910_write_port_0_w ),
		new Memory_WriteAddress( 0x6000, 0x6000, YM2203_control_port_0_w ),
		new Memory_WriteAddress( 0x6001, 0x6001, YM2203_write_port_0_w ),
		new Memory_WriteAddress( 0x8000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortHandlerPtr input_ports_citycon = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( citycon )
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START2 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_BITX( 0,       0x03, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Infinite", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Cocktail") );
		/* the coin input must stay low for exactly 2 frames to be consistently recognized. */
		PORT_BIT_IMPULSE( 0x80, IP_ACTIVE_LOW, IPT_COIN1, 2 );
	
		PORT_START(); 
		PORT_DIPNAME( 0x07, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x07, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,2),
		5,
		new int[] { 16, 12, 8, 4, 0 },
		new int[] { 0, 1, 2, 3, RGN_FRAC(1,2)+0, RGN_FRAC(1,2)+1, RGN_FRAC(1,2)+2, RGN_FRAC(1,2)+3 },
		new int[] { 0*24, 1*24, 2*24, 3*24, 4*24, 5*24, 6*24, 7*24 },
		24*8
	);
	
	static GfxLayout tilelayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		256,	/* 256 characters */
		4,	/* 4 bits per pixel */
		new int[] { 4, 0, 0xc000*8+4, 0xc000*8+0 },
		new int[] { 0, 1, 2, 3, 256*8*8+0, 256*8*8+1, 256*8*8+2, 256*8*8+3 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		8,16,	/* 8*16 sprites */
		128,	/* 128 sprites */
		4,	/* 4 bits per pixel */
		new int[] { 4, 0, 0x2000*8+4, 0x2000*8+0 },
		new int[] { 0, 1, 2, 3, 128*16*8+0, 128*16*8+1, 128*16*8+2, 128*16*8+3 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
	            8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
		16*8	/* every sprite takes 16 consecutive bytes */
	);
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
	//	new GfxDecodeInfo( REGION_GFX1, 0x00000, charlayout, 512, 32 ),	/* colors 512-639 */
		new GfxDecodeInfo( REGION_GFX1, 0x00000, charlayout, 640, 32 ),	/* colors 512-639 */
		new GfxDecodeInfo( REGION_GFX2, 0x00000, spritelayout, 0, 16 ),	/* colors 0-255 */
		new GfxDecodeInfo( REGION_GFX2, 0x01000, spritelayout, 0, 16 ),
		new GfxDecodeInfo( REGION_GFX3, 0x00000, tilelayout, 256, 16 ),	/* colors 256-511 */
		new GfxDecodeInfo( REGION_GFX3, 0x01000, tilelayout, 256, 16 ),
		new GfxDecodeInfo( REGION_GFX3, 0x02000, tilelayout, 256, 16 ),
		new GfxDecodeInfo( REGION_GFX3, 0x03000, tilelayout, 256, 16 ),
		new GfxDecodeInfo( REGION_GFX3, 0x04000, tilelayout, 256, 16 ),
		new GfxDecodeInfo( REGION_GFX3, 0x05000, tilelayout, 256, 16 ),
		new GfxDecodeInfo( REGION_GFX3, 0x06000, tilelayout, 256, 16 ),
		new GfxDecodeInfo( REGION_GFX3, 0x07000, tilelayout, 256, 16 ),
		new GfxDecodeInfo( REGION_GFX3, 0x08000, tilelayout, 256, 16 ),
		new GfxDecodeInfo( REGION_GFX3, 0x09000, tilelayout, 256, 16 ),
		new GfxDecodeInfo( REGION_GFX3, 0x0a000, tilelayout, 256, 16 ),
		new GfxDecodeInfo( REGION_GFX3, 0x0b000, tilelayout, 256, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		1,			/* 1 chip */
		1250000,	/* 1.25 MHz */
		new int[] { MIXERG(20,MIXER_GAIN_2x,MIXER_PAN_CENTER) },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	static struct YM2203interface ym2203_interface =
	{
		1,			/* 1 chip */
		1250000,	/* 1.25 MHz */
		{ YM2203_VOL(20,MIXERG(20,MIXER_GAIN_2x,MIXER_PAN_CENTER)) },
		{ soundlatch_r },
		{ soundlatch2_r },
		{ 0 },
		{ 0 }
	};
	
	
	
	public static MachineHandlerPtr machine_driver_citycon = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M6809, 2048000)        /* 2.048 MHz ??? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(M6809, 640000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)        /* 0.640 MHz ??? */
		MDRV_CPU_MEMORY(readmem_sound,writemem_sound)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(1*8, 31*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(640+1024)	/* 640 real palette + 1024 virtual palette */
	
		MDRV_VIDEO_START(citycon)
		MDRV_VIDEO_UPDATE(citycon)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
		MDRV_SOUND_ADD(YM2203, ym2203_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadHandlerPtr rom_citycon = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "c10",          0x4000, 0x4000, CRC(ae88b53c) SHA1(dd12310bd9c9b93462446e8e0a1c853506bf3aa1) )
		ROM_LOAD( "c11",          0x8000, 0x8000, CRC(139eb1aa) SHA1(c570e8ca1499f7ea61938e78c32c1cc3050ca2b7) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "c1",           0x8000, 0x8000, CRC(1fad7589) SHA1(2e626bbbab8cffe11ee7de3e56aa1871c29d5fa9) )
	
		ROM_REGION( 0x03000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "c4",           0x00000, 0x2000, CRC(a6b32fc6) SHA1(d99d5a527440e9a91525c1084b95b213e3b760ec) )	/* Characters */
	
		ROM_REGION( 0x04000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "c12",          0x00000, 0x2000, CRC(08eaaccd) SHA1(a970381e3ba22bcdea6df2d31cd8a10c4b2bc413) )	/* Sprites    */
		ROM_LOAD( "c13",          0x02000, 0x2000, CRC(1819aafb) SHA1(8a5ffcd8866e09c5568879257384767d61796111) )
	
		ROM_REGION( 0x18000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "c9",           0x00000, 0x8000, CRC(8aeb47e6) SHA1(bb09dbe6b37e1bd02abf3024ac4d954c8f0e70f2) )	/* Background tiles */
		ROM_LOAD( "c8",           0x08000, 0x4000, CRC(0d7a1eeb) SHA1(60b8d4124ce857a248d3c41fdb050f11be58549f) )
		ROM_LOAD( "c6",           0x0c000, 0x8000, CRC(2246fe9d) SHA1(f7f8708d499bcbd1a583e1092b54425ad1105f94) )
		ROM_LOAD( "c7",           0x14000, 0x4000, CRC(e8b97de9) SHA1(f4d1b7075f47ab4522c36281b97eaa02fe383814) )
	
		ROM_REGION( 0xe000, REGION_GFX4, 0 )	/* background tilemaps */
		ROM_LOAD( "c2",           0x0000, 0x8000, CRC(f2da4f23) SHA1(5ea1a51c3ac283796f7eafb6719d88356767340d) )	/* background maps */
		ROM_LOAD( "c3",           0x8000, 0x4000, CRC(7ef3ac1b) SHA1(8a0497c4e4733f9c50d576f632210b82497a5e1c) )
		ROM_LOAD( "c5",           0xc000, 0x2000, CRC(c03d8b1b) SHA1(641c1eba334d36ea64b9293a20320b31c7c88858) )	/* color codes for the background */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_citycona = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "c10",          0x4000, 0x4000, CRC(ae88b53c) SHA1(dd12310bd9c9b93462446e8e0a1c853506bf3aa1) )
		ROM_LOAD( "c11b",         0x8000, 0x8000, CRC(d64af468) SHA1(5bb3541af3ce632e8eca313231205713d72fb9dc) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "c1",           0x8000, 0x8000, CRC(1fad7589) SHA1(2e626bbbab8cffe11ee7de3e56aa1871c29d5fa9) )
	
		ROM_REGION( 0x03000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "c4",           0x00000, 0x2000, CRC(a6b32fc6) SHA1(d99d5a527440e9a91525c1084b95b213e3b760ec) )	/* Characters */
	
		ROM_REGION( 0x04000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "c12",          0x00000, 0x2000, CRC(08eaaccd) SHA1(a970381e3ba22bcdea6df2d31cd8a10c4b2bc413) )	/* Sprites    */
		ROM_LOAD( "c13",          0x02000, 0x2000, CRC(1819aafb) SHA1(8a5ffcd8866e09c5568879257384767d61796111) )
	
		ROM_REGION( 0x18000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "c9",           0x00000, 0x8000, CRC(8aeb47e6) SHA1(bb09dbe6b37e1bd02abf3024ac4d954c8f0e70f2) )	/* Background tiles */
		ROM_LOAD( "c8",           0x08000, 0x4000, CRC(0d7a1eeb) SHA1(60b8d4124ce857a248d3c41fdb050f11be58549f) )
		ROM_LOAD( "c6",           0x0c000, 0x8000, CRC(2246fe9d) SHA1(f7f8708d499bcbd1a583e1092b54425ad1105f94) )
		ROM_LOAD( "c7",           0x14000, 0x4000, CRC(e8b97de9) SHA1(f4d1b7075f47ab4522c36281b97eaa02fe383814) )
	
		ROM_REGION( 0xe000, REGION_GFX4, 0 )	/* background tilemaps */
		ROM_LOAD( "c2",           0x0000, 0x8000, CRC(f2da4f23) SHA1(5ea1a51c3ac283796f7eafb6719d88356767340d) )	/* background maps */
		ROM_LOAD( "c3",           0x8000, 0x4000, CRC(7ef3ac1b) SHA1(8a0497c4e4733f9c50d576f632210b82497a5e1c) )
		ROM_LOAD( "c5",           0xc000, 0x2000, CRC(c03d8b1b) SHA1(641c1eba334d36ea64b9293a20320b31c7c88858) )	/* color codes for the background */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_cruisin = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "cr10",         0x4000, 0x4000, CRC(cc7c52f3) SHA1(69d76f146fb1dac62c6def3a4269012b3880f03b) )
		ROM_LOAD( "cr11",         0x8000, 0x8000, CRC(5422f276) SHA1(d384fc4f853fe79b73e939a8fc7b7af780659c5e) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "c1",           0x8000, 0x8000, CRC(1fad7589) SHA1(2e626bbbab8cffe11ee7de3e56aa1871c29d5fa9) )
	
		ROM_REGION( 0x03000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "cr4",          0x00000, 0x2000, CRC(8cd0308e) SHA1(7303b9e074bda557d64b39e04cef0f965a756be6) )	/* Characters */
	
		ROM_REGION( 0x04000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "c12",          0x00000, 0x2000, CRC(08eaaccd) SHA1(a970381e3ba22bcdea6df2d31cd8a10c4b2bc413) )	/* Sprites    */
		ROM_LOAD( "c13",          0x02000, 0x2000, CRC(1819aafb) SHA1(8a5ffcd8866e09c5568879257384767d61796111) )
	
		ROM_REGION( 0x18000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "c9",           0x00000, 0x8000, CRC(8aeb47e6) SHA1(bb09dbe6b37e1bd02abf3024ac4d954c8f0e70f2) )	/* Background tiles */
		ROM_LOAD( "c8",           0x08000, 0x4000, CRC(0d7a1eeb) SHA1(60b8d4124ce857a248d3c41fdb050f11be58549f) )
		ROM_LOAD( "c6",           0x0c000, 0x8000, CRC(2246fe9d) SHA1(f7f8708d499bcbd1a583e1092b54425ad1105f94) )
		ROM_LOAD( "c7",           0x14000, 0x4000, CRC(e8b97de9) SHA1(f4d1b7075f47ab4522c36281b97eaa02fe383814) )
	
		ROM_REGION( 0xe000, REGION_GFX4, 0 )	/* background tilemaps */
		ROM_LOAD( "c2",           0x0000, 0x8000, CRC(f2da4f23) SHA1(5ea1a51c3ac283796f7eafb6719d88356767340d) )	/* background maps */
		ROM_LOAD( "c3",           0x8000, 0x4000, CRC(7ef3ac1b) SHA1(8a0497c4e4733f9c50d576f632210b82497a5e1c) )
		ROM_LOAD( "c5",           0xc000, 0x2000, CRC(c03d8b1b) SHA1(641c1eba334d36ea64b9293a20320b31c7c88858) )	/* color codes for the background */
	ROM_END(); }}; 
	
	
	
	public static DriverInitHandlerPtr init_citycon  = new DriverInitHandlerPtr() { public void handler(){
		UINT8 *rom = memory_region(REGION_GFX1);
		int i;
	
	
		/*
		  City Connection controls the text color code for each _scanline_, not
		  for each character as happens in most games. To handle that conveniently,
		  I convert the 2bpp char data into 5bpp, and create a virtual palette so
		  characters can still be drawn in one pass.
		  */
		for (i = 0x0fff;i >= 0;i--)
		{
			int mask;
	
			rom[3*i] = rom[i];
			rom[3*i+1] = 0;
			rom[3*i+2] = 0;
			mask = rom[i] | (rom[i] << 4) | (rom[i] >> 4);
			if (i & 0x01) rom[3*i+1] |= mask & 0xf0;
			if (i & 0x02) rom[3*i+1] |= mask & 0x0f;
			if (i & 0x04) rom[3*i+2] |= mask & 0xf0;
		}
	} };
	
	
	
	public static GameDriver driver_citycon	   = new GameDriver("1985"	,"citycon"	,"citycon.java"	,rom_citycon,null	,machine_driver_citycon	,input_ports_citycon	,init_citycon	,ROT0, "Jaleco", "City Connection (set 1)" )
	public static GameDriver driver_citycona	   = new GameDriver("1985"	,"citycona"	,"citycon.java"	,rom_citycona,driver_citycon	,machine_driver_citycon	,input_ports_citycon	,init_citycon	,ROT0, "Jaleco", "City Connection (set 2)" )
	public static GameDriver driver_cruisin	   = new GameDriver("1985"	,"cruisin"	,"citycon.java"	,rom_cruisin,driver_citycon	,machine_driver_citycon	,input_ports_citycon	,init_citycon	,ROT0, "Jaleco (Kitkorp license)", "Cruisin" )
}
