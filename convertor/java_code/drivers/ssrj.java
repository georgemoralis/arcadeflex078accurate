/***********************************
 Super Speed Race Jr (c) 1985 Taito
 driver by  Tomasz Slanina


 TODO:
 - colors (missing proms?)
 - dips
 - proper video hw emulation
 - controls (is there START button ?)

HW info :

	0000-7fff ROM
	c000-dfff VRAM ( 4 tilemaps (4 x $800) )
	e000-e7ff RAM
	e800-efff SCROLL RAM
	f003      ??
  f400-f401 AY 8910
  fc00      ??
  f800      ??

 Scroll RAM contains x and y offsets for each tileline,
 as well as other data (priroities ? additional flags ?)
 All moving obejcts (cars, etc) are displayed on tilemap 3.

 ------------------------------------
 Cheat :  $e210 - timer

************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class ssrj
{
	
	
	
	
	static int oldport=0x80;
	
	public static MachineInitHandlerPtr machine_init_ssrj  = new MachineInitHandlerPtr() { public void handler(){
		unsigned char *rom = memory_region(REGION_CPU1);
		memset(&rom[0xc000],0,0x3fff); /* req for some control types */
		oldport=0x80;
	} };
	
	public static ReadHandlerPtr ssrj_wheel_r  = new ReadHandlerPtr() { public int handler(int offset){
		int port= input_port_1_r.handler(0) -0x80;
		int retval=port-oldport;
		oldport=port;
		return retval;
	} };
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xc7ff, ssrj_vram1_r),
		new Memory_ReadAddress( 0xc800, 0xcfff, ssrj_vram2_r),
		new Memory_ReadAddress( 0xd000, 0xd7ff, MRA_RAM),
		new Memory_ReadAddress( 0xd800, 0xdfff, ssrj_vram4_r),
		new Memory_ReadAddress( 0xe000, 0xe7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xe800, 0xefff, MRA_RAM ),
		new Memory_ReadAddress( 0xf000, 0xf000, input_port_0_r),
		new Memory_ReadAddress( 0xf001, 0xf001, ssrj_wheel_r ),
		new Memory_ReadAddress( 0xf002, 0xf002, input_port_2_r),
		new Memory_ReadAddress( 0xf401, 0xf401 ,AY8910_read_port_0_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xc7ff, ssrj_vram1_w,ssrj_vram1 ),
		new Memory_WriteAddress( 0xc800, 0xcfff, ssrj_vram2_w,ssrj_vram2 ),
		new Memory_WriteAddress( 0xd000, 0xd7ff, MWA_RAM,ssrj_vram3 ),
		new Memory_WriteAddress( 0xd800, 0xdfff, ssrj_vram4_w,ssrj_vram4 ),
		new Memory_WriteAddress( 0xe000, 0xe7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xe800, 0xefff, MWA_RAM,ssrj_scrollram ),
		new Memory_WriteAddress( 0xf003, 0xf003, MWA_NOP ), /* unknown */
		new Memory_WriteAddress( 0xf401, 0xf401, AY8910_write_port_0_w  ),
		new Memory_WriteAddress( 0xf400, 0xf400, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0xfc00, 0xfc00, MWA_NOP ), /* unknown */
		new Memory_WriteAddress( 0xf800, 0xf800, MWA_NOP ), /* wheel ? */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	static InputPortHandlerPtr input_ports_ssrj = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( ssrj )
	
	PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_TILT );
		PORT_ANALOG( 0xe0, 0x00, IPT_PEDAL, 50, 0x20, 0, 0xe0 );
	
	 PORT_START(); 
		PORT_ANALOG( 0xff, 0x80, IPT_DIAL | IPF_REVERSE , 50, 4, 0x00, 0xff);
	
	
	 PORT_START(); 
	
	 PORT_BIT( 0xf, IP_ACTIVE_LOW, IPT_BUTTON2  ); /* code @ $eef  , tested when controls = type4 */
	
	 PORT_DIPNAME(0x30, 0x00, DEF_STR( "Difficulty") ); /* ??? code @ $62c */
	 PORT_DIPSETTING(   0x10, "Easy" );
	 PORT_DIPSETTING(   0x00, "Normal" );
	 PORT_DIPSETTING(   0x20, "Difficult" );
	 PORT_DIPSETTING(   0x30, "Very Difficult" );
	
	 PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Free_Play") );
	 PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
	 PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
	 PORT_DIPNAME( 0x80, 0x80, "No Hit" );
	 PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
	 PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
	 PORT_START(); 
	
		PORT_DIPNAME( 0x07, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x07, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0x08, 0x08, "Freeze" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_DIPNAME( 0x030, 0x000, "Controls" );/* 'press button to start' message, and wait for button2 */
		PORT_DIPSETTING(    0x00, "Type 1" );
		PORT_DIPSETTING(    0x10, "Type 2" );
		PORT_DIPSETTING(    0x20, "Type 3" );
		PORT_DIPSETTING(    0x30, "Type 4" );
	
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );/* sometimes hangs after game over ($69b) */
	
	
	INPUT_PORTS_END(); }}; 
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		RGN_FRAC(1,3),	/* 1024 characters */
		3,	/* 3 bits per pixel */
		new int[] { 0, RGN_FRAC(2,3), RGN_FRAC(1,3) },	/* the bitplanes are separated */
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,     0, 8*4 ),
		new GfxDecodeInfo( -1 )	/* end of array */
	};
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		1,
		8000000/5,	/* guess */
		new int[] { 30,},
		new ReadHandlerPtr[] { 0 }, /* not used ? */
		new ReadHandlerPtr[] {input_port_3_r},
		new WriteHandlerPtr[] { 0 }, /* ? */
		new WriteHandlerPtr[] { 0 }
	);
	
	
	public static MachineHandlerPtr machine_driver_ssrj = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80,8000000/2)
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(2*8, 30*8-1, 3*8, 32*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(128)
		MDRV_PALETTE_INIT(ssrj)
	
		MDRV_VIDEO_START(ssrj)
		MDRV_VIDEO_UPDATE(ssrj)
		MDRV_ASPECT_RATIO(3,4)
	
		MDRV_MACHINE_INIT(ssrj)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	MACHINE_DRIVER_END();
 }
};
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadHandlerPtr rom_ssrj = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )
		ROM_LOAD( "a40-01.bin",   0x0000, 0x4000, CRC(1ff7dbff) SHA1(a9e676ee087141d62f880cd98e7748db1e6e9461) )
		ROM_LOAD( "a40-02.bin",   0x4000, 0x4000, CRC(bbb36f9f) SHA1(9f85bac639d18ee932273a6c00b36ac969e69bb8) )
	
		ROM_REGION( 0x6000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "a40-03.bin",   0x0000, 0x2000, CRC(3753182a) SHA1(3eda34f967563b11416344da87b7be46cbecff2b) )
		ROM_LOAD( "a40-04.bin",   0x2000, 0x2000, CRC(96471816) SHA1(e24b690085602b8bde079e596c2879deab128c83) )
		ROM_LOAD( "a40-05.bin",   0x4000, 0x2000, CRC(dce9169e) SHA1(2cdda1453b2913fad931788e1db0bc01ce923a04) )
	
		ROM_REGION( 0x100, REGION_PROMS, 0 )
		ROM_LOAD( "proms",  0x0000, 0x0100, NO_DUMP )
	
	ROM_END(); }}; 
	
	public static GameDriver driver_ssrj	   = new GameDriver("1985"	,"ssrj"	,"ssrj.java"	,rom_ssrj,null	,machine_driver_ssrj	,input_ports_ssrj	,null	,ORIENTATION_FLIP_X, "Taito Corporation", "Super Speed Race Junior (Japan)",GAME_WRONG_COLORS|GAME_IMPERFECT_GRAPHICS )
}
