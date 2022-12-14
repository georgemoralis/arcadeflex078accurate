/***************************************************************************


 ***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class ttmahjng
{
	
	
	
	
	static int psel;
	public static WriteHandlerPtr input_port_matrix_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		psel = data;
	} };
	
	public static ReadHandlerPtr input_port_matrix_r  = new ReadHandlerPtr() { public int handler(int offset){
		int	cdata;
	
		cdata = 0;
		switch (psel)
		{
			case	1:
				cdata = readinputport(2);
				break;
			case	2:
				cdata = readinputport(3);
				break;
			case	4:
				cdata = readinputport(4);
				break;
			case	8:
				cdata = readinputport(5);
				break;
			default:
				break;
		}
		return cdata;
	} };
	
	
	public static Memory_ReadAddress cpu1_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x43ff, ttmahjng_sharedram_r ),
		new Memory_ReadAddress( 0x4800, 0x4800, input_port_0_r ),
		new Memory_ReadAddress( 0x5000, 0x5000, input_port_1_r ),
		new Memory_ReadAddress( 0x5800, 0x5800, input_port_matrix_r ),
		new Memory_ReadAddress( 0x7838, 0x7838, MRA_NOP ),
		new Memory_ReadAddress( 0x7859, 0x7859, MRA_NOP ),
		new Memory_ReadAddress( 0x8000, 0xbfff, ttmahjng_videoram1_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress cpu1_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x43ff, ttmahjng_sharedram_w, ttmahjng_sharedram ),
		new Memory_WriteAddress( 0x4800, 0x4800, ttmahjng_out0_w ),
		new Memory_WriteAddress( 0x5000, 0x5000, ttmahjng_out1_w ),
		new Memory_WriteAddress( 0x5800, 0x5800, input_port_matrix_w ),
		new Memory_WriteAddress( 0x5f3e, 0x5f3e, MWA_NOP ),
		new Memory_WriteAddress( 0x6800, 0x6800, AY8910_write_port_0_w ),
		new Memory_WriteAddress( 0x6900, 0x6900, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0x8000, 0xbfff, ttmahjng_videoram1_w, ttmahjng_videoram1, ttmahjng_videoram_size ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress cpu2_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x43ff, ttmahjng_sharedram_r ),
		new Memory_ReadAddress( 0x8000, 0xbfff, ttmahjng_videoram2_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress cpu2_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x43ff, ttmahjng_sharedram_w ),
		new Memory_WriteAddress( 0x8000, 0xbfff, ttmahjng_videoram2_w, ttmahjng_videoram2 ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	static InputPortHandlerPtr input_ports_ttmahjng = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( ttmahjng )
		PORT_START();       /* IN0 */
		PORT_DIPNAME( 0x01, 0x00, "Unknown 01" );
		PORT_DIPSETTING(    0x00, "00" );
		PORT_DIPSETTING(    0x01, "01" );
		PORT_DIPNAME( 0x02, 0x00, "Unknown 02" );
		PORT_DIPSETTING(    0x00, "00" );
		PORT_DIPSETTING(    0x02, "02" );
		PORT_DIPNAME( 0x04, 0x00, "Unknown 04" );
		PORT_DIPSETTING(    0x00, "00" );
		PORT_DIPSETTING(    0x04, "04" );
		PORT_DIPNAME( 0x08, 0x00, "Unknown 08" );
		PORT_DIPSETTING(    0x00, "00" );
		PORT_DIPSETTING(    0x08, "08" );
		PORT_DIPNAME( 0x10, 0x00, "Unknown 10" );
		PORT_DIPSETTING(    0x00, "00" );
		PORT_DIPSETTING(    0x10, "10" );
		PORT_DIPNAME( 0x20, 0x00, "Unknown 20" );
		PORT_DIPSETTING(    0x00, "00" );
		PORT_DIPSETTING(    0x20, "20" );
		PORT_DIPNAME( 0x40, 0x00, "Unknown 40" );
		PORT_DIPSETTING(    0x00, "00" );
		PORT_DIPSETTING(    0x40, "40" );
		PORT_DIPNAME( 0x80, 0x00, "Unknown 80" );
		PORT_DIPSETTING(    0x00, "00" );
		PORT_DIPSETTING(    0x80, "80" );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START();       /* IN2 */
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 A", KEYCODE_A, IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 E", KEYCODE_E, IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 I", KEYCODE_I, IP_JOY_NONE );
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 M", KEYCODE_M, IP_JOY_NONE );
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 Kan", KEYCODE_LCONTROL, IP_JOY_NONE );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START1 );	// START2?
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();       /* IN3 */
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 B", KEYCODE_B, IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 F", KEYCODE_F, IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 J", KEYCODE_J, IP_JOY_NONE );
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 N", KEYCODE_N, IP_JOY_NONE );
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 Reach", KEYCODE_LSHIFT, IP_JOY_NONE );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START2 );	// START1?
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();       /* IN4 */
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 C", KEYCODE_C, IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 G", KEYCODE_G, IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 K", KEYCODE_K, IP_JOY_NONE );
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 Chi", KEYCODE_Z, IP_JOY_NONE );
		PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "P1 Ron", KEYCODE_SPACE, IP_JOY_NONE );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();       /* IN5 */
		PORT_BITX(0x01, IP_ACTIVE_LOW, 0, "P1 D", KEYCODE_D, IP_JOY_NONE );
		PORT_BITX(0x02, IP_ACTIVE_LOW, 0, "P1 H", KEYCODE_H, IP_JOY_NONE );
		PORT_BITX(0x04, IP_ACTIVE_LOW, 0, "P1 L", KEYCODE_L, IP_JOY_NONE );
		PORT_BITX(0x08, IP_ACTIVE_LOW, 0, "P1 Pon", KEYCODE_LALT, IP_JOY_NONE );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		1,			/* 1 chip */
		10000000/8, 		/* 10MHz / 8 = 1.25MHz */
		new int[] { 50 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	
	public static MachineHandlerPtr machine_driver_ttmahjng = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80,2500000)
		MDRV_CPU_FLAGS(CPU_16BIT_PORT)	/* 10MHz / 4 = 2.5MHz */
		MDRV_CPU_MEMORY(cpu1_readmem,cpu1_writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 2500000)	/* 10MHz / 4 = 2.5MHz */
		MDRV_CPU_MEMORY(cpu2_readmem,cpu2_writemem)
	
		MDRV_FRAMES_PER_SECOND(57)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(256, 256)
		MDRV_VISIBLE_AREA(0, 256-1, 0, 256-1)
		MDRV_PALETTE_LENGTH(8)
	
		MDRV_PALETTE_INIT(ttmahjng)
		MDRV_VIDEO_START(ttmahjng)
		MDRV_VIDEO_UPDATE(ttmahjng)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	MACHINE_DRIVER_END();
 }
};
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadHandlerPtr rom_ttmahjng = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "ju04", 0x0000, 0x1000, CRC(fe7c693a) SHA1(be0630557e0bcd9ec2e9542cc4a4d947889ec57a) )
		ROM_LOAD( "ju05", 0x1000, 0x1000, CRC(985723d3) SHA1(9d7499c48cfc242875a95d01459b8f3252ea41bc) )
		ROM_LOAD( "ju06", 0x2000, 0x1000, CRC(2cd69bc8) SHA1(a0a55c972291d043da9f76faf551dba790d5d103) )
		ROM_LOAD( "ju07", 0x3000, 0x1000, CRC(30e8ec63) SHA1(9c6a2b5e436b5e469c15f04c557839b6f07eb22e) )
	
		ROM_REGION( 0x0200, REGION_PROMS, 0 ) /* color proms */
		/* The upper 128 bytes are 0's, used by the hardware to blank the display */
		ROM_LOAD( "ju03", 0x0000, 0x0100, CRC(27d47624) SHA1(ee04ce8043216be8b91413b546479419fca2b917) )
		ROM_LOAD( "ju09", 0x0100, 0x0100, CRC(27d47624) SHA1(ee04ce8043216be8b91413b546479419fca2b917) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for the second CPU */
		ROM_LOAD( "ju01", 0x0000, 0x0800, CRC(0f05ca3c) SHA1(6af547b2ec4f69069b4ad62d96d109ec0105dd8b) )
		ROM_LOAD( "ju02", 0x0800, 0x0800, CRC(c1ffeceb) SHA1(18cf337ef2c9b51f1e9e4f08743225755c4ff420) )
		ROM_LOAD( "ju08", 0x1000, 0x0800, CRC(2dcc76b5) SHA1(1732bcf5492dda34425681e7f28775ad7a5e04af) )
	ROM_END(); }}; 
	
	
	public static GameDriver driver_ttmahjng	   = new GameDriver("1981"	,"ttmahjng"	,"ttmahjng.java"	,rom_ttmahjng,null	,machine_driver_ttmahjng	,input_ports_ttmahjng	,null	,ROT0, "Taito", "Mahjong" )
}
