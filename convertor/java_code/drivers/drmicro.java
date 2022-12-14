/*****************************************************************************

Dr. Micro (c) 1983 Sanritsu

		driver by Uki

Quite similar to Appoooh

*****************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class drmicro
{
	
	#define MCLK 18432000
	
	
	
	
	
	/****************************************************************************/
	
	static int drmicro_nmi_enable;
	
	public static InterruptHandlerPtr drmicro_interrupt = new InterruptHandlerPtr() {public void handler(){
		if (drmicro_nmi_enable)
			 cpu_set_nmi_line(0, PULSE_LINE);
	} };
	
	public static WriteHandlerPtr nmi_enable_w = new WriteHandlerPtr() {public void handler(int offset, int data){	// bit2,3 unknown
		drmicro_nmi_enable = data & 1;
		drmicro_flip_w(data & 2);
	} };
	
	/****************************************************************************/
	
	static int pcm_adr;
	
	static void pcm_w(int irq)
	{
		data8_t *PCM = memory_region(REGION_SOUND1);
	
		int data = PCM[pcm_adr / 2];
	
		if (data != 0x70) // ??
		{
			if (~pcm_adr & 1)
				data >>= 4;
	
			MSM5205_data_w(0, data & 0x0f);
			MSM5205_reset_w(0, 0);
	
			pcm_adr = (pcm_adr + 1) & 0x7fff;
		}
		else
			MSM5205_reset_w(0, 1);
	}
	
	public static WriteHandlerPtr pcm_set_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		pcm_adr = ((data & 0x3f) << 9);
		pcm_w(0);
	} };
	
	/****************************************************************************/
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xdfff, MRA_RAM ),
		new Memory_ReadAddress( 0xe000, 0xefff, drmicro_videoram_r ),
		new Memory_ReadAddress( 0xf000, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xdfff, MWA_RAM ),
		new Memory_WriteAddress( 0xe000, 0xefff, drmicro_videoram_w ),
		new Memory_WriteAddress( 0xf000, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, input_port_0_r ),
		new IO_ReadPort( 0x01, 0x01, input_port_1_r ),
		new IO_ReadPort( 0x03, 0x03, input_port_2_r ),
		new IO_ReadPort( 0x04, 0x04, input_port_3_r ),
		new IO_ReadPort( 0x05, 0x05, IORP_NOP ), // unused?
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, SN76496_0_w ),
		new IO_WritePort( 0x01, 0x01, SN76496_1_w ),
		new IO_WritePort( 0x02, 0x02, SN76496_2_w ),
		new IO_WritePort( 0x03, 0x03, pcm_set_w ),
		new IO_WritePort( 0x04, 0x04, nmi_enable_w ),
		new IO_WritePort( 0x05, 0x05, IOWP_NOP ), // watchdog?
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	/****************************************************************************/
	
	static InputPortHandlerPtr input_ports_drmicro = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( drmicro )
		PORT_START();  // 1P (0)
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_SERVICE1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
	
		PORT_START();  // 2P (1)
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_PLAYER2 );
	
		PORT_START();  // DSW1 (2)
		PORT_DIPNAME( 0x03, 0x01, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x02, "4" );
		PORT_DIPSETTING(    0x03, "5" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x18, 0x08, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "30000 100000" );
		PORT_DIPSETTING(    0x08, "50000 150000" );
		PORT_DIPSETTING(    0x10, "70000 200000" );
		PORT_DIPSETTING(    0x18, "100000 300000" );
		PORT_SERVICE( 0x20, IP_ACTIVE_HIGH );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START();  // DSW2 (3)
		PORT_DIPNAME( 0x07, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x07, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_5C") );
		PORT_BIT( 0xf8, IP_ACTIVE_HIGH, IPT_UNKNOWN );// 4-8
	
	INPUT_PORTS_END(); }}; 
	
	/****************************************************************************/
	
	static GfxLayout spritelayout4 = new GfxLayout
	(
		16,16,
		0x100,
		2,
		new int[] {0,0x2000*8},
		new int[] {STEP8(7,-1),STEP8(71,-1)},
		new int[] {STEP8(0,8),STEP8(128,8)},
		8*8*4
	);
	
	static GfxLayout spritelayout8 = new GfxLayout
	(
		16,16,
		0x100,
		3,
		new int[] {0x2000*16,0x2000*8,0},
		new int[] {STEP8(7,-1),STEP8(71,-1)},
		new int[] {STEP8(0,8),STEP8(128,8)},
		8*8*4
	);
	
	static GfxLayout charlayout4 = new GfxLayout
	(
		8,8,
		0x400,
		2,
		new int[] {0,0x2000*8},
		new int[] {STEP8(7,-1)},
		new int[] {STEP8(0,8)},
		8*8*1
	);
	
	static GfxLayout charlayout8 = new GfxLayout
	(
		8,8,
		0x400,
		3,
		new int[] {0x2000*16,0x2000*8,0},
		new int[] {STEP8(7,-1)},
		new int[] {STEP8(0,8)},
		8*8*1
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, charlayout4,     0, 64 ), /* tiles */
		new GfxDecodeInfo( REGION_GFX2, 0x0000, charlayout8,   256, 32 ), /* tiles */
		new GfxDecodeInfo( REGION_GFX1, 0x0000, spritelayout4,   0, 64 ), /* sprites */
		new GfxDecodeInfo( REGION_GFX2, 0x0000, spritelayout8, 256, 32 ), /* sprites */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static struct SN76496interface sn76496_interface =
	{
		3,							/* 3 chips */
		{ MCLK/4, MCLK/4, MCLK/4 }, /* 4.608MHz? */
		{ 50, 50, 50 }				/* volume */
	};
	
	static struct MSM5205interface msm5205_interface =
	{
		1,					/* 1 chip */
		384000,				/* 384 KHz */
		{ pcm_w },			/* IRQ handler */
		{ MSM5205_S64_4B },	/* 6 KHz */
		{ 75 }				/* volume */
	};
	
	/****************************************************************************/
	
	public static MachineHandlerPtr machine_driver_drmicro = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80,MCLK/6)	/* 3.072MHz? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(drmicro_interrupt,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(1)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(32)
		MDRV_COLORTABLE_LENGTH(512)
	
		MDRV_PALETTE_INIT(drmicro)
		MDRV_VIDEO_START(drmicro)
		MDRV_VIDEO_UPDATE(drmicro)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SN76496, sn76496_interface)
		MDRV_SOUND_ADD(MSM5205, msm5205_interface)
	MACHINE_DRIVER_END();
 }
};
	
	/****************************************************************************/
	
	static RomLoadHandlerPtr rom_drmicro = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 ) // CPU
		ROM_LOAD( "dm-00.13b", 0x0000,  0x2000, CRC(270f2145) SHA1(1557428387e2c0f711c676a13a763c8d48aa497b) )
		ROM_LOAD( "dm-01.14b", 0x2000,  0x2000, CRC(bba30c80) SHA1(a084429fad58fa6348936084652235d5f55e3b89) )
		ROM_LOAD( "dm-02.15b", 0x4000,  0x2000, CRC(d9e4ca6b) SHA1(9fb6d1d6b45628891deae389cf1d142332b110ba) )
		ROM_LOAD( "dm-03.13d", 0x6000,  0x2000, CRC(b7bcb45b) SHA1(61035afc642bac2e1c56c36c188bed4e1949523f) )
		ROM_LOAD( "dm-04.14d", 0x8000,  0x2000, CRC(071db054) SHA1(75929b7692bebf2246fa84581b6d1eedb02c9aba) )
		ROM_LOAD( "dm-05.15d", 0xa000,  0x2000, CRC(f41b8d8a) SHA1(802830f3f0362ec3df257f31dc22390e8ae4207c) )
	
		ROM_REGION( 0x04000, REGION_GFX1, ROMREGION_DISPOSE ) // gfx 1
		ROM_LOAD( "dm-23.5l",  0x0000,  0x2000, CRC(279a76b8) SHA1(635650621bdce5873bb5faf64f8352149314e784) )
		ROM_LOAD( "dm-24.5n",  0x2000,  0x2000, CRC(ee8ed1ec) SHA1(7afc05c73186af9fe3d3f3ce13412c8ee560b146) )
	
		ROM_REGION( 0x06000, REGION_GFX2, ROMREGION_DISPOSE ) // gfx 2
		ROM_LOAD( "dm-20.4a",  0x0000,  0x2000, CRC(6f5dbf22) SHA1(41ef084336e2ebb1016b28505dcb43483e37a0de) )
		ROM_LOAD( "dm-21.4c",  0x2000,  0x2000, CRC(8b17ff47) SHA1(5bcc14489ea1d4f1fe8e51c24a72a8e787ab8159) )
		ROM_LOAD( "dm-22.4d",  0x4000,  0x2000, CRC(84daf771) SHA1(d187debcca59ceab6cd696be246370120ee575c6) )
	
		ROM_REGION( 0x04000, REGION_SOUND1, 0 ) // samples
		ROM_LOAD( "dm-40.12m",  0x0000,  0x2000, CRC(3d080af9) SHA1(f9527fae69fe3ca0762024ac4a44b1f02fbee66a) )
		ROM_LOAD( "dm-41.13m",  0x2000,  0x2000, CRC(ddd7bda2) SHA1(bbe9276cb47fa3e82081d592522640e04b4a9223) )
	
		ROM_REGION( 0x00220, REGION_PROMS, 0 ) // PROMs
		ROM_LOAD( "dm-62.9h", 0x0000,  0x0020, CRC(e3e36eaf) SHA1(5954400190e587a20cad60f5829f4bddc85ea526) )
		ROM_LOAD( "dm-61.4m", 0x0020,  0x0100, CRC(0dd8e365) SHA1(cbd43a2d4af053860932af32ca5e13bef728e38a) )
		ROM_LOAD( "dm-60.6e", 0x0120,  0x0100, CRC(540a3953) SHA1(bc65388a1019dadf8c71705e234763f5c735e282) )
	ROM_END(); }}; 
	
	public static GameDriver driver_drmicro	   = new GameDriver("1983"	,"drmicro"	,"drmicro.java"	,rom_drmicro,null	,machine_driver_drmicro	,input_ports_drmicro	,null	,ROT270, "Sanritsu", "Dr. Micro" )
}
