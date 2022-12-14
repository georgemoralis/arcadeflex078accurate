/* Main Event - SNK 
   driver by	David Haywood
   		Tomasz Slanina	
  
 ROM doesn't pass its internal checksum
 
   		
 Todo:
  - fix controls (now you need to press button1 + direction for punch/block ) and DIPs 
  - verify position of status bars   		
    		
*/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class mainsnk
{
	
	
	static int sound_cpu_ready;
	static int sound_command;
	static int sound_fetched; 
	
	static void init_sound( int busy_bit )
	{
		sound_cpu_ready = 1;
		sound_command = 0x00;
		sound_fetched = 1;
	}
	
	public static WriteHandlerPtr sound_command_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( sound_fetched==0 ){
			logerror("missed sound command: %02x\n", sound_command );
		}
	
		sound_fetched = 0;
		sound_command = data;
		sound_cpu_ready = 0;
		cpu_set_irq_line(1, IRQ_LINE_NMI, PULSE_LINE);
	} };
	
	public static ReadHandlerPtr sound_command_r  = new ReadHandlerPtr() { public int handler(int offset){
		sound_fetched = 1;
		return sound_command;
	} };
	
	public static ReadHandlerPtr sound_ack_r  = new ReadHandlerPtr() { public int handler(int offset){
		sound_cpu_ready = 1;
		return 0xff;
	} };
	
	public static ReadHandlerPtr mainsnk_port_0_r  = new ReadHandlerPtr() { public int handler(int offset){
		int result = input_port_0_r.handler( 0 );
		if( !sound_cpu_ready ) result |= 0x20;
		return result;
	} };
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		2, 
		2000000, 
		new int[] { 35,35 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xc000, mainsnk_port_0_r ),
		new Memory_ReadAddress( 0xC100, 0xC100, input_port_1_r ),
		new Memory_ReadAddress( 0xC200, 0xC200, input_port_2_r ),
		new Memory_ReadAddress( 0xC300, 0xC300, input_port_3_r ),
		new Memory_ReadAddress( 0xC500, 0xC500, input_port_4_r ),
		new Memory_ReadAddress( 0xd800, 0xdbff, me_bgram_r ),
		new Memory_ReadAddress( 0xdc00, 0xdfff, MRA_RAM ),
		new Memory_ReadAddress( 0xe000, 0xe3ff, MRA_RAM ),
		new Memory_ReadAddress( 0xe400, 0xe7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xe800, 0xebff, MRA_RAM ),
		new Memory_ReadAddress( 0xec00, 0xefff, MRA_RAM ),
		new Memory_ReadAddress( 0xf000, 0xf3ff, me_fgram_r ),
		new Memory_ReadAddress( 0xf400, 0xf7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xf800, 0xfbff, MRA_RAM ),
		new Memory_ReadAddress( 0xfc00, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xC600, 0xC600, me_c600_w ),
		new Memory_WriteAddress( 0xc700, 0xc700, sound_command_w ),
		new Memory_WriteAddress( 0xd800, 0xdbff, me_bgram_w, me_bgram ),
		new Memory_WriteAddress( 0xdc00, 0xdfff, MWA_RAM ),
		new Memory_WriteAddress( 0xe000, 0xe3ff, MWA_RAM ),
		new Memory_WriteAddress( 0xe400, 0xe7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xe800, 0xebff, MWA_RAM ),
		new Memory_WriteAddress( 0xec00, 0xefff, MWA_RAM ),
		new Memory_WriteAddress( 0xf000, 0xf3ff, me_fgram_w, me_fgram ),
		new Memory_WriteAddress( 0xf400, 0xf7ff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new Memory_ReadAddress( 0xa000, 0xa000, sound_command_r ),
		new Memory_ReadAddress( 0xc000, 0xc000, sound_ack_r ),
	
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x87ff, MWA_RAM ),
		new Memory_WriteAddress( 0xe000, 0xe000, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0xe001, 0xe001, AY8910_write_port_0_w ),
		new Memory_WriteAddress( 0xe002, 0xe007, snkwave_w ),
		new Memory_WriteAddress( 0xe008, 0xe008, AY8910_control_port_1_w ),
		new Memory_WriteAddress( 0xe009, 0xe009, AY8910_write_port_1_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort readport_sound[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x0000, 0x0000, MRA_NOP ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	static InputPortHandlerPtr input_ports_mainsnk = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( mainsnk )
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_COIN3 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW,  IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW,  IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* sound CPU status */
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START();  
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP);
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT );
		
		PORT_START();  
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER2 );
		
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 );/* ?? */
		
		PORT_START(); 
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x01, DEF_STR( "3C_1C") ); /* also 02,04 */
		PORT_DIPSETTING(    0x06, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x60, 0x60, "Game mode" );
		PORT_DIPSETTING(    0x60, "Demo Sounds Off" );
		PORT_DIPSETTING(    0x20, "Demo Sounds On" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, "Freeze" );
		PORT_DIPNAME( 0x08, 0x00, "SW 2-3" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, "SW 2-4" );/* $1ecf */
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, "SW 2-7" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		
	INPUT_PORTS_END(); }}; 
	
	
	static GfxLayout tile_layout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,1),
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 4, 0, 12, 8, 20, 16, 28, 24},
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
		256
	);
	
	
	static GfxLayout sprite_layout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,3),
		3,
		new int[] { RGN_FRAC(2,3),RGN_FRAC(1,3),RGN_FRAC(0,3) },  
		new int[] { 7,6,5,4,3,2,1,0, 15,14,13,12,11,10,9,8 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
			8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		256
	);
	
	static struct namco_interface snkwave_interface =
	{
		24000,	
		1,	
		8,	
		-1	
	};
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0, tile_layout,	0,  8 ),
		new GfxDecodeInfo( REGION_GFX2, 0x0, sprite_layout,	0, 16 ),
		new GfxDecodeInfo( -1 )
	};
	
	static MACHINE_DRIVER_START( mainsnk)
		MDRV_CPU_ADD(Z80, 3360000)
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80,4000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	
	 	MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
	 	MDRV_CPU_PORTS(readport_sound,0)
		MDRV_CPU_PERIODIC_INT(irq0_line_hold, 244)
	
		MDRV_FRAMES_PER_SECOND(60.606060)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(34*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 34*8-1, 0*8, 27*8-1)
		MDRV_PALETTE_LENGTH((16+2)*16)
	
		MDRV_VIDEO_START(mainsnk)
		MDRV_VIDEO_UPDATE(mainsnk)
		
		MDRV_SOUND_ADD(AY8910, ay8910_interface) 
		MDRV_SOUND_ADD(NAMCO, snkwave_interface)
	
	
	MACHINE_DRIVER_END();
 }
};
	
	
	static RomLoadHandlerPtr rom_mainsnk = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )
		ROM_LOAD( "snk.p01",      0x000000, 0x002000, CRC(00db1ca2) SHA1(efe83488cf88adc185e6024b8f6ad5f8ef7f4cfd) ) 
		ROM_LOAD( "snk.p02",      0x002000, 0x002000, CRC(df5c86b5) SHA1(e9c854524e3d8231c874314cdff321e66ec7f0c4) ) 
		ROM_LOAD( "snk.p03",      0x004000, 0x002000, CRC(5c2b7bca) SHA1(e02c72fcd029999b730abd91f07866418cfe6216) ) 
		ROM_LOAD( "snk.p04",      0x006000, 0x002000, CRC(68b4b2a1) SHA1(8f3abc826df93f0748151624066e956b9670bc9d) ) 
		ROM_LOAD( "snk.p05",      0x008000, 0x002000, CRC(580a29b4) SHA1(4a96af92d65f86aca7f3a70032b5e4dc29048483) ) 
		ROM_LOAD( "snk.p06",      0x00a000, 0x002000, CRC(5f8a60a2) SHA1(88a051e13d6b3bbd3606a4c4cc0395da07e0f109) ) 
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )
		ROM_LOAD( "snk.p07",	0x0000, 0x4000, CRC(4208391e) SHA1(d110ca4ff9d21fe7813f04ec43c2c23471c6517f) )
	
		ROM_REGION( 0x08000, REGION_GFX1, 0 )
		ROM_LOAD( "snk.p12",      0x000000, 0x002000, CRC(ecf87eb7) SHA1(83b8d19070d5930b306a0309ebba05b04c2abebf) ) 
		ROM_LOAD( "snk.p11",      0x002000, 0x002000, CRC(3f6bc5ba) SHA1(02e49f58f5d94117113b59037fa49b8897d05b4b) ) 
		ROM_LOAD( "snk.p10",      0x004000, 0x002000, CRC(b5147a96) SHA1(72641fadabd16f2de4f4cf6ff3ef07233de5ddfd) ) 
		ROM_LOAD( "snk.p09",      0x006000, 0x002000, CRC(0ebcf837) SHA1(7b93cdffd3b8d768b98bb01956114e4ff012d029) ) 
	
		ROM_REGION( 0x12000, REGION_GFX2, 0 )
		ROM_LOAD( "snk.p13",      0x000000, 0x002000, CRC(2eb624a4) SHA1(157d7beb6ff0baa9276e388774a85996dc03821d) ) 
		ROM_LOAD( "snk.p16",      0x002000, 0x002000, CRC(dc502869) SHA1(024c868e8cd74c52f4787a19b9ad292b7a9dcc1c) ) 
		ROM_LOAD( "snk.p19",      0x004000, 0x002000, CRC(58d566a1) SHA1(1451b223ddb7c975b770f28af6c41775daaf95c1) ) 
		ROM_LOAD( "snk.p14",      0x006000, 0x002000, CRC(bb927d82) SHA1(ac7ae1850cf22b73e31c92b6f598fb057470a570) ) 
		ROM_LOAD( "snk.p17",      0x008000, 0x002000, CRC(66f60c32) SHA1(7a08d0a2c1804cdaad702a23ff33128d0b6d8084) ) 
		ROM_LOAD( "snk.p20",      0x00a000, 0x002000, CRC(d12c6333) SHA1(bed1a0aedaa8f6fe9c33f49b5da00ab1c9045ddd) ) 
		ROM_LOAD( "snk.p15",      0x00c000, 0x002000, CRC(d242486d) SHA1(0c24a3fdcb604b6231b75069c99009d68023bb8f) ) 
		ROM_LOAD( "snk.p18",      0x00e000, 0x002000, CRC(838b12a3) SHA1(a3444f9b2aeef70caa93e5f642cb6c3b75e88ea4) ) 
		ROM_LOAD( "snk.p21",      0x010000, 0x002000, CRC(8961a51e) SHA1(4f9d8358bc76118c4fab631ae73a02ab5aa0c036) ) 
		
		ROM_REGION( 0x1800, REGION_PROMS, 0 )
		ROM_LOAD( "main3.bin",    0x000000, 0x000800, CRC(78b29dde) SHA1(c2f93cde6fd8bc175e9e0d38af41b7710d7f1c82) ) 
		ROM_LOAD( "main2.bin",    0x000800, 0x000800, CRC(7c314c93) SHA1(c6bd2a0eaf617448ef65dcbadced313b0d69ab88) ) 
		ROM_LOAD( "main1.bin",    0x001000, 0x000800, CRC(deb895c4) SHA1(f1281dcb3471d9627565706ff09ba72f09dc62a4) ) 
	ROM_END(); }}; 
	
	public static GameDriver driver_mainsnk	   = new GameDriver("1984"	,"mainsnk"	,"mainsnk.java"	,rom_mainsnk,null	,machine_driver_mainsnk	,input_ports_mainsnk	,null	,ROT0, "SNK", "Main Event (1984)")
	
}
