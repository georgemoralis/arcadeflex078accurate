/***************************************************************************

Misc early Z80 games with simple color bitmap graphics

	- Space King 2    (c) Konami
	- Kamikaze        (c) Leijac
	- Astro Invader   (c) Stern
	- Space Intruder  (c) Shoei

Space Intruder emulation by Lee Taylor (lee@defender.demon.co.uk),
	December 1998.

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class astinvad
{
	
	
	extern extern 
	extern extern extern 
	extern extern extern extern extern extern extern 
	extern struct Samplesinterface astinvad_samples_interface;
	
	
	public static PaletteInitHandlerPtr palette_init_astinvad  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
	
		for (i = 0; i < 8; i++)
		{
			palette_set_color(i,
				(i & 1) ? 0xff : 0,
				(i & 4) ? 0xff : 0,
				(i & 2) ? 0xff : 0);
		}
	} };
	
	
	public static Memory_ReadAddress astinvad_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1bff, MRA_ROM ),
		new Memory_ReadAddress( 0x1c00, 0x3fff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress astinvad_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1bff, MWA_ROM ),
		new Memory_WriteAddress( 0x1c00, 0x1fff, MWA_RAM ),
		new Memory_WriteAddress( 0x2000, 0x3fff, astinvad_videoram_w, &videoram, &videoram_size ),
		new Memory_WriteAddress( 0x4000, 0x4fff, MWA_NOP ), /* sloppy game code writes here */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress spaceint_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x17ff, MRA_ROM ),
		new Memory_ReadAddress( 0x2000, 0x23ff, MRA_RAM ),
		new Memory_ReadAddress( 0x4000, 0x5fff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress spaceint_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x17ff, MWA_ROM ),
		new Memory_WriteAddress( 0x2000, 0x23ff, MWA_RAM ),
		new Memory_WriteAddress( 0x4000, 0x5fff, spaceint_videoram_w, &videoram, &videoram_size ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort astinvad_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x08, 0x08, input_port_0_r ),
		new IO_ReadPort( 0x09, 0x09, input_port_1_r ),
		new IO_ReadPort( 0x0a, 0x0a, input_port_2_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort astinvad_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x04, 0x04, astinvad_sound1_w ),
		new IO_WritePort( 0x05, 0x05, astinvad_sound2_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort spaceint_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, input_port_0_r ),
		new IO_ReadPort( 0x01, 0x01, input_port_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort spaceint_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x02, 0x02, spaceint_sound1_w ),
		new IO_WritePort( 0x03, 0x03, spaceint_color_w ),
		new IO_WritePort( 0x04, 0x04, spaceint_sound2_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	#define COMMON_INPUT_BITS \
		PORT_START();  \
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );\
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );\
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );\
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNUSED );\
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );\
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY );\
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );\
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );\
	
	
	static InputPortHandlerPtr input_ports_astinvad = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( astinvad )
		COMMON_INPUT_BITS
	
		PORT_START();       /* IN1 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x02, "10000" );
		PORT_DIPSETTING(    0x00, "20000" );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_DIPNAME( 0x88, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x88, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_2C") );
	
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
	
		PORT_START();       /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN3 */
		PORT_DIPNAME( 0xff, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0xff, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_kamikaze = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( kamikaze )
		COMMON_INPUT_BITS
	
		PORT_START();       /* IN1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x00, "6" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x88, 0x88, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x88, "5000" );
		PORT_DIPSETTING(    0x80, "10000" );
		PORT_DIPSETTING(    0x08, "15000" );
		PORT_DIPSETTING(    0x00, "20000" );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
	
		PORT_START();       /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN3 */
		PORT_DIPNAME( 0xff, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0xff, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_spcking2 = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( spcking2 )
		COMMON_INPUT_BITS
	
		PORT_START();       /* IN1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x08, "1000" );
		PORT_DIPSETTING(    0x00, "2000" );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0x80, 0x00, "Coin Info" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START();       /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN3 */
		PORT_DIPNAME( 0xff, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_spaceint = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( spaceint )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
	
		PORT_START();       /* IN1 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x06, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_2C") );
	
		PORT_START();       /* IN2 */
		PORT_BIT_IMPULSE( 0x01, IP_ACTIVE_HIGH, IPT_COIN1, 1 );/* causes NMI */
	
		PORT_START();       /* IN3 */
		PORT_DIPNAME( 0xff, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0xff, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	
	public static InterruptHandlerPtr spaceint_interrupt = new InterruptHandlerPtr() {public void handler(){
		if (readinputport(2) & 1)	/* coin */
			cpu_set_nmi_line(0, PULSE_LINE);
	
		cpu_set_irq_line(0, 0, HOLD_LINE);
	} };
	
	
	static MACHINE_DRIVER_START( astinvad )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 2000000)
		MDRV_CPU_MEMORY(astinvad_readmem,astinvad_writemem)
		MDRV_CPU_PORTS(astinvad_readport,astinvad_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,2)    /* two interrupts per frame */
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 4*8, 32*8-1)
		MDRV_PALETTE_LENGTH(8)
	
		MDRV_PALETTE_INIT(astinvad)
		MDRV_VIDEO_START(astinvad)
		MDRV_VIDEO_UPDATE(astinvad)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SAMPLES, astinvad_samples_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( spcking2 )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(astinvad)
	
		/* video hardware */
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
	
		MDRV_VIDEO_START( spcking2 )
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( spaceint )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 2000000)        /* 2 MHz? */
		MDRV_CPU_MEMORY(spaceint_readmem,spaceint_writemem)
		MDRV_CPU_PORTS(spaceint_readport,spaceint_writeport)
		MDRV_CPU_VBLANK_INT(spaceint_interrupt,1)
	
		MDRV_FRAMES_PER_SECOND(60)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 1*8, 31*8-1)
		MDRV_PALETTE_LENGTH(8)
	
		MDRV_PALETTE_INIT(astinvad)
		MDRV_VIDEO_START(spaceint)
		MDRV_VIDEO_UPDATE(spaceint)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SAMPLES, astinvad_samples_interface)
	MACHINE_DRIVER_END
	
	
	static RomLoadHandlerPtr rom_astinvad = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "ai_cpu_1.rom", 0x0000, 0x0400, CRC(20e3ec41) SHA1(7e77fa3c51d1e83ce91a24808301d9f1e0bed18e) )
		ROM_LOAD( "ai_cpu_2.rom", 0x0400, 0x0400, CRC(e8f1ab55) SHA1(b3e38f2d6bdb65ee7c53c8d5dd3951a3fd43c51c) )
		ROM_LOAD( "ai_cpu_3.rom", 0x0800, 0x0400, CRC(a0092553) SHA1(34fced8ce06d912980ba45fad8d80d2a2e3357b9) )
		ROM_LOAD( "ai_cpu_4.rom", 0x0c00, 0x0400, CRC(be14185c) SHA1(59ecf450682dab9840c891c18ccda1d5ec4cc954) )
		ROM_LOAD( "ai_cpu_5.rom", 0x1000, 0x0400, CRC(fee681ec) SHA1(b4b94f62e598030e6a432a0bb83d18d0e342aed9) )
		ROM_LOAD( "ai_cpu_6.rom", 0x1400, 0x0400, CRC(eb338863) SHA1(e841c6c5903dd6dee9ec2fedaff431f4a31d738a) )
		ROM_LOAD( "ai_cpu_7.rom", 0x1800, 0x0400, CRC(16dcfea4) SHA1(b6a0e206a604297f548ac4658664e98b2d04f75f) )
	
		ROM_REGION( 0x0400, REGION_PROMS, 0 )
		ROM_LOAD( "ai_vid_c.rom", 0x0000, 0x0400, CRC(b45287ff) SHA1(7e558eaf402641d7ff60171f854030219fbf9a59) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_kamikaze = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "km01",         0x0000, 0x0800, CRC(8aae7414) SHA1(91cb5c268a03960d50401000903d70dc29f904fb) )
		ROM_LOAD( "km02",         0x0800, 0x0800, CRC(6c7a2beb) SHA1(86447d077a58e8c1fc096d0d32b02d18523019a6) )
		ROM_LOAD( "km03",         0x1000, 0x0800, CRC(3e8dedb6) SHA1(19679d0e8ebe2d19dc766b12a07335b1220fb568) )
		ROM_LOAD( "km04",         0x1800, 0x0800, CRC(494e1f6d) SHA1(f9626072d80897a977c10fe9523a8b608f1f7b7c) )
	
		ROM_REGION( 0x0400, REGION_PROMS, 0 )
		ROM_LOAD( "ai_vid_c.rom", 0x0000, 0x0400, BAD_DUMP CRC(b45287ff) SHA1(7e558eaf402641d7ff60171f854030219fbf9a59)  )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_spcking2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "1.bin",        0x0000, 0x0400, CRC(716fe9e0) SHA1(d5131abf6e3e6650ff9f649a999bf1d8ae8afb78) )
		ROM_LOAD( "2.bin",        0x0400, 0x0400, CRC(6f6d4e5c) SHA1(0269c3b9da2723411c16ee13ff53e2140e49e7ff) )
		ROM_LOAD( "3.bin",        0x0800, 0x0400, CRC(2ab1c280) SHA1(62cb2445b3f859bddd5617e4ebfb37eedf8bd11e) )
		ROM_LOAD( "4.bin",        0x0c00, 0x0400, CRC(07ba1f21) SHA1(26468e142edef3475e71320292bd1817552a9218) )
		ROM_LOAD( "5.bin",        0x1000, 0x0400, CRC(b084c074) SHA1(1c7e86ae35cd69679712cd8a209b4a70a2075163) )
		ROM_LOAD( "6.bin",        0x1400, 0x0400, CRC(b53d7791) SHA1(45415bcccb03a9c61cea611df807b011e8cc0d2d) )
	
		ROM_REGION( 0x0400, REGION_PROMS, 0 )
		ROM_LOAD( "c.bin",        0x0000, 0x0400, CRC(d27fe595) SHA1(1781281110b57ab3a5eef7a3dbaa93f11c013554) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_spaceint = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "1",			  0x0000, 0x0400, CRC(184314d2) SHA1(76789780c46e19c73904b229d23c865819915558) )
		ROM_LOAD( "2",			  0x0400, 0x0400, CRC(55459aa1) SHA1(5631d8de4e41682962cde65002b0fe86f2b189f9) )
		ROM_LOAD( "3",			  0x0800, 0x0400, CRC(9d6819be) SHA1(da061b908ca6a9f3312d6adc4395a138eed473c8) )
		ROM_LOAD( "4",			  0x0c00, 0x0400, CRC(432052d4) SHA1(0c944c91cc7b1f03cd817250af13238eb62539ec) )
		ROM_LOAD( "5",			  0x1000, 0x0400, CRC(c6cfa650) SHA1(afdfaedddf6703101856944bb49ba13fc40ede39) )
		ROM_LOAD( "6",			  0x1400, 0x0400, CRC(c7ccf40f) SHA1(10efe05a4e0625ce427871fbb6e55df112fdd783) )
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 )
		ROM_LOAD( "clr",		  0x0000, 0x0100, CRC(13c1803f) SHA1(da59bf63d9e84aca32904c107674bc89974648eb) )
	ROM_END(); }}; 
	
	
	public static GameDriver driver_astinvad	   = new GameDriver("1980"	,"astinvad"	,"astinvad.java"	,rom_astinvad,null	,machine_driver_astinvad	,input_ports_astinvad	,null	,ROT270, "Stern",  "Astro Invader" )
	public static GameDriver driver_kamikaze	   = new GameDriver("1979"	,"kamikaze"	,"astinvad.java"	,rom_kamikaze,driver_astinvad	,machine_driver_astinvad	,input_ports_kamikaze	,null	,ROT270, "Leijac", "Kamikaze" )
	public static GameDriver driver_spcking2	   = new GameDriver("1979"	,"spcking2"	,"astinvad.java"	,rom_spcking2,null	,machine_driver_spcking2	,input_ports_spcking2	,null	,ROT270, "Konami", "Space King 2" )
	public static GameDriver driver_spaceint	   = new GameDriver("1980"	,"spaceint"	,"astinvad.java"	,rom_spaceint,null	,machine_driver_spaceint	,input_ports_spaceint	,null	,ROT90,  "Shoei",  "Space Intruder", GAME_WRONG_COLORS )
}
