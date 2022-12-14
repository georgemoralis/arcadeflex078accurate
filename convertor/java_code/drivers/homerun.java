/*
 Moero Pro Yakyuu Homerun - (c) 1988 Jaleco
 Driver by Tomasz Slanina
	
 *weird* hardware - based on NES version
 (gfx bank changed in the middle of screen, 
  sprites in NES format etc)
  
Todo :
 - voice ( unemulated D7756C )	
 - controls/dips
 - better emulation of gfx bank switching  
 - is there 2 player mode ?

*/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class homerun
{
	
	
	
	public static WriteHandlerPtr pa_w = new WriteHandlerPtr() {public void handler(int offset, int data)omerun_xpa=data;}
	public static WriteHandlerPtr pb_w = new WriteHandlerPtr() {public void handler(int offset, int data)omerun_xpb=data;}
	public static WriteHandlerPtr pc_w = new WriteHandlerPtr() {public void handler(int offset, int data)omerun_xpc=data;}
	
	static ppi8255_interface ppi8255_intf =
	{
		1, 		
		{ 0 } };,
		{ 0  },
		{ 0  },
		{ pa_w },
		{ pb_w },
		{ pc_w },
	};
	
	
	public static MachineInitHandlerPtr machine_init_homerun  = new MachineInitHandlerPtr() { public void handler(){
		ppi8255_init(&ppi8255_intf);
	} };
	
	static GfxLayout gfxlayout = new GfxLayout
	(
	   8,8,
	   RGN_FRAC(1,1),
	   2,
	   new int[] { 8*8,0},
	   new int[] { 0, 1, 2, 3, 4, 5, 6, 7},
	   new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8},
	   8*8*2
	);
	
	
	
	static GfxLayout spritelayout = new GfxLayout
	(
	   16,16,
	   RGN_FRAC(1,1),
	   2,
	   new int[] { 8*8,0},
	   new int[] { 0, 1, 2, 3, 4, 5, 6, 7,0+8*8*2,1+8*8*2,2+8*8*2,3+8*8*2,4+8*8*2,5+8*8*2,6+8*8*2,7+8*8*2},
	   new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8, 0*8+2*8*8*2,1*8+2*8*8*2,2*8+2*8*8*2,3*8+2*8*8*2,4*8+2*8*8*2,5*8+2*8*8*2,6*8+2*8*8*2,7*8+2*8*8*2},
	   8*8*2*4
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, gfxlayout,   0, 16 ), 
		new GfxDecodeInfo( REGION_GFX2, 0, spritelayout,   0, 16 ), 
		new GfxDecodeInfo( -1 )
	};
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),		
		new Memory_ReadAddress( 0x4000, 0x7fff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xc000, 0xdfff, MRA_RAM ),		
		
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),		
		new Memory_WriteAddress( 0x8000, 0x9fff, homerun_videoram_w, homerun_videoram ),
		new Memory_WriteAddress( 0xa000, 0xa0ff, MWA_RAM, spriteram,  spriteram_size ),	
		new Memory_WriteAddress( 0xb000, 0xb0ff, homerun_color_w),
		new Memory_WriteAddress( 0xc000, 0xdfff, MWA_RAM ),		
	
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static ReadHandlerPtr homerun_40_r  = new ReadHandlerPtr() { public int handler(int offset){
		if(cpu_getscanline()>116)
			return input_port_0_r.handler(0)|0x40;
		else
			return input_port_0_r.handler(0);
	} };
	
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort(0x30, 0x33, ppi8255_0_r ),
		new IO_ReadPort(0x40, 0x40, homerun_40_r  ),
		new IO_ReadPort(0x50, 0x50, input_port_2_r ),
		new IO_ReadPort(0x60, 0x60, input_port_1_r ),
		new IO_ReadPort(0x70, 0x70, YM2203_status_port_0_r),
		new IO_ReadPort(0x71, 0x71, YM2203_read_port_0_r),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x10, 0x10, IOWP_NOP ), /* ?? */
		new IO_WritePort( 0x20, 0x20, IOWP_NOP ), /* ?? */
		new IO_WritePort( 0x30, 0x33, ppi8255_0_w ),
		new IO_WritePort( 0x70, 0x70, YM2203_control_port_0_w ),
		new IO_WritePort( 0x71, 0x71, YM2203_write_port_0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	static struct YM2203interface ym2203_interface =
	{
		1,		
		6000000/2,    	
		{ YM2203_VOL(50,50), },
		{ input_port_3_r  },
		{ 0 },
		{ 0 },
		{ homerun_banking_w },
		{ 0 }
	};
	
	
	static InputPortHandlerPtr input_ports_homerun = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( homerun )
		PORT_START(); 
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_COIN1  );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x80, DEF_STR( "On") );
		
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_START1   );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_BUTTON1 );
			
		PORT_START(); 
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN2  );
			
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x02, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(      0x00, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(      0x01, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x03, DEF_STR( "Free_Play") ); 
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(      0x80, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x00, DEF_STR( "1C_2C") );
		
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_homerun = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
		MDRV_CPU_ADD(Z80, 5000000)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
		
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER )
		MDRV_SCREEN_SIZE(256, 256)
		MDRV_VISIBLE_AREA(0, 256-1, 0, 256-25)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(16*4)
	
		MDRV_VIDEO_START(homerun)
		MDRV_VIDEO_UPDATE(homerun)
		MDRV_SOUND_ADD(YM2203, ym2203_interface)
		MDRV_MACHINE_INIT(homerun)
		
	MACHINE_DRIVER_END();
 }
};
	
	static RomLoadHandlerPtr rom_homerun = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 )	  
		ROM_LOAD( "homerun.43",        0x0000, 0x4000, CRC(e759e476) SHA1(ad4f356ff26209033320a3e6353e4d4d9beb59c1) )
		ROM_CONTINUE(        0x10000,0x1c000) 
		
		ROM_REGION( 0x010000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "homerun.60",  0x00000, 0x10000, CRC(69a720d1) SHA1(0f0a4877578f358e9e829ece8c31e23f01adcf83) ) 
	
		ROM_REGION( 0x020000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "homerun.120",  0x00000, 0x20000, CRC(52f0709b) SHA1(19e675bcccadb774f60ec5929fc1fb5cf0d3f617) ) 
		
		ROM_REGION( 0x01000, REGION_SOUND1, 0 )
		ROM_LOAD( "homerun.snd",  0x00000, 0x1000, NO_DUMP ) /* D7756C internal rom */
		
	ROM_END(); }}; 
	
	public static GameDriver driver_homerun	   = new GameDriver("1988"	,"homerun"	,"homerun.java"	,rom_homerun,null	,machine_driver_homerun	,input_ports_homerun	,null	,ROT0, "Jaleco", "Moero Pro Yakyuu Homerun",GAME_IMPERFECT_GRAPHICS)
	
}
