/***************************************************************************

Ashita no Joe (Success Joe) [Wave]

driver by David Haywood and bits from Pierpaolo Prazzoli

todo:
- sound
- frequencies
- 1 unused rom

there is an english logo in the roms, needs different program roms?

Tow sub-boards:

Upper:

 Program
  ROMS 1 to 8 (ST M27512)
  Standard Motorola MC68000P8
  8.0000 MHz osc.
  PALs W9011A (AMD PALCE16V8H) + W9011B (MMI PAL 16L88CN)

 Sound
  ROM 9 (ST M27256)
  Standard Zilog Z80 (Z0840004PSC)
  Yamaha YM2203C

 GFX?
  Mask ROMs 401, 402 & 403 (Hitachi HN62414 Mask ROMs)

Lower:

 GFX?
  Mask ROMs 404, 405, 406, 407, 408 & 409 (Hitachi HN62414 Mask ROMs)
  PAL W90120R2 (MMI PAL 16L88CN)
  EPL (Ricoh EPL16P8BP, not dumped)
  13.3330 MHz osc.

Dips:

 Two banks (* = default)
  A
                                    1   2   3   4   5   6   7   8
   Game Style      * Table          ON                      OFF OFF
                   Upright          OFF                     OFF OFF
   Secreen Reverse * Usual              OFF                 OFF OFF
                   Reverse              ON                  OFF OFF
   Test Mode       * Game mode              OFF             OFF OFF
                   Test Mode                ON              OFF OFF
   Demo Sound      * Yes                        OFF         OFF OFF
                   No                           ON          OFF OFF
   Play Fee - Coin * 1 Coin 1 Play                  OFF OFF OFF OFF
                   1 Coin 2 Play                    ON  OFF OFF OFF
                   2 Coin 1 Play                    OFF ON  OFF OFF
                   2 Coin 3 Play                    ON  ON  OFF OFF

  B
                                    1   2   3   4   5   6   7   8
   Difficulty      * Rank B         OFF OFF OFF OFF OFF OFF OFF OFF
                   Rank A           ON  OFF OFF OFF OFF OFF OFF OFF
                   Rank C           OFF ON  OFF OFF OFF OFF OFF OFF
                   Rank D           ON  ON  OFF OFF OFF OFF OFF OFF

   Easy (A) -> Difficult (D)

Game is controled with 4-direction lever and two buttons
Coin B is not used

*************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class ashnojoe
{
	
	
	
	
	static READ16_HANDLER(fake_4a00a_r)
	{
		//if it returns 1 there's no sound. is it used to sync the game and sound?
		//or just a debug enable/disble register?
		return 0;
		return 1;
	}
	
	static WRITE16_HANDLER( ashnojoe_soundlatch_w )
	{
		if(ACCESSING_LSB)
		{
			soundlatch_w(0,data & 0xff);
			//needed?
			cpu_set_irq_line(1,0,HOLD_LINE);
		}
	}
	
	static MEMORY_READ16_START( ashnojoe_readmem )
		{ 0x000000, 0x01ffff, MRA16_ROM },
		{ 0x040000, 0x043fff, MRA16_RAM },
		{ 0x044000, 0x048fff, MRA16_RAM },
		{ 0x049000, 0x049fff, MRA16_RAM },
		{ 0x04a000, 0x04a001, input_port_0_word_r }, // p1 inputs, coins
		{ 0x04a002, 0x04a003, input_port_1_word_r }, // p2 inputs
		{ 0x04a004, 0x04a005, input_port_2_word_r }, // dipswitches
		{ 0x04a00a, 0x04a00b, fake_4a00a_r }, // ??
		{ 0x04c000, 0x04ffff, MRA16_RAM },
		{ 0x080000, 0x0bffff, MRA16_ROM },
	MEMORY_END
	
	static MEMORY_WRITE16_START( ashnojoe_writemem )
		{ 0x000000, 0x01ffff, MWA16_ROM },
		{ 0x040000, 0x041fff, ashnojoe_tileram3_w, &ashnojoetileram16_3 },
		{ 0x042000, 0x043fff, ashnojoe_tileram4_w, &ashnojoetileram16_4 },
		{ 0x044000, 0x044fff, ashnojoe_tileram5_w, &ashnojoetileram16_5 },
		{ 0x045000, 0x045fff, ashnojoe_tileram2_w, &ashnojoetileram16_2 },
		{ 0x046000, 0x046fff, ashnojoe_tileram6_w, &ashnojoetileram16_6 },
		{ 0x047000, 0x047fff, ashnojoe_tileram7_w, &ashnojoetileram16_7 },
		{ 0x048000, 0x048fff, ashnojoe_tileram_w,  &ashnojoetileram16 },
		{ 0x049000, 0x049fff, paletteram16_xRRRRRGGGGGBBBBB_word_w, &paletteram16 },
		{ 0x04a006, 0x04a007, MWA16_RAM, &ashnojoe_tilemap_reg },
		{ 0x04a008, 0x04a009, ashnojoe_soundlatch_w },
		{ 0x04a010, 0x04a019, joe_tilemaps_xscroll_w },
		{ 0x04a020, 0x04a029, joe_tilemaps_yscroll_w },
		{ 0x04c000, 0x04ffff, MWA16_RAM },
		{ 0x080000, 0x0bffff, MWA16_ROM },
	MEMORY_END
	
	public static ReadHandlerPtr fake_6_r  = new ReadHandlerPtr() { public int handler(int offset){
		// if it returns 0 the cpu doesn't read from port $4 ?
		int ret = 0;
		ret ^= 1;
		return ret;
		return 1;
		return 0;
		return rand();
	} };
	
	public static WriteHandlerPtr adpcm_data_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		MSM5205_data_w(0, data & 0xf);
		MSM5205_data_w(0, data>>4);
	} };
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x5fff, MRA_ROM ),
		new Memory_ReadAddress( 0x6000, 0x7fff, MRA_RAM ),
		new Memory_ReadAddress( 0x8000, 0xffff, MRA_BANK4 ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x5fff, MWA_ROM ),
		new Memory_WriteAddress( 0x6000, 0x7fff, MWA_RAM ),
		new Memory_WriteAddress( 0x8000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, YM2203_status_port_0_r ),
		new IO_ReadPort( 0x01, 0x01, YM2203_read_port_0_r ),
		new IO_ReadPort( 0x04, 0x04, soundlatch_r ), //PC: 15D -> cp $7f
		new IO_ReadPort( 0x06, 0x06, fake_6_r/*soundlatch_r */), //PC: 14A -> and $1
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, YM2203_control_port_0_w ),
		new IO_WritePort( 0x01, 0x01, YM2203_write_port_0_w ),
		new IO_WritePort( 0x02, 0x02, adpcm_data_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	static InputPortHandlerPtr input_ports_ashnojoe = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( ashnojoe )
		PORT_START(); 	/* player 1 16-bit */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0002, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0004, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0008, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0010, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x0020, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x0040, IP_ACTIVE_HIGH, IPT_UNKNOWN ); // anything else and the controls don't work
		PORT_BIT( 0x0080, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_BIT( 0x0100, IP_ACTIVE_HIGH, IPT_SERVICE1 );
		PORT_BIT( 0x0200, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x0400, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x0800, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x1000, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x2000, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x4000, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x8000, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* player 2 16-bit */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0002, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0004, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0008, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0010, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x0020, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x0040, IP_ACTIVE_HIGH, IPT_UNKNOWN ); // anything else and the controls don't work
		PORT_BIT( 0x0080, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		/* unused ? */
		PORT_BIT( 0x0100, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x0200, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x0400, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x0800, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x1000, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x2000, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x4000, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x8000, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* 16-bit */
		PORT_DIPNAME( 0x0001, 0x0000, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Upright") );
		PORT_DIPSETTING(      0x0001, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x0002, 0x0000, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0002, DEF_STR( "On") );
		PORT_SERVICE( 0x0004, IP_ACTIVE_HIGH );
		PORT_DIPNAME( 0x0008, 0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(      0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0030, 0x0000, DEF_STR( "Coinage") );
		PORT_DIPSETTING(      0x0020, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x0030, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(      0x0010, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x0040, 0x0000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0040, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080, 0x0000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0080, DEF_STR( "On") );
		PORT_DIPNAME( 0x0300, 0x0000, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(      0x0100, "Easy" );
		PORT_DIPSETTING(      0x0000, "Normal" );
		PORT_DIPSETTING(      0x0200, "Medium" );
		PORT_DIPSETTING(      0x0300, "Hard" );
		PORT_DIPNAME( 0x0400, 0x0000, "Number of controller" );
		PORT_DIPSETTING(      0x0000, "2" );
		PORT_DIPSETTING(      0x0400, "1" );
		PORT_DIPNAME( 0x0800, 0x0000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0800, DEF_STR( "On") );
		PORT_DIPNAME( 0x1000, 0x0000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x1000, DEF_STR( "On") );
		PORT_DIPNAME( 0x2000, 0x0000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x2000, DEF_STR( "On") );
		PORT_DIPNAME( 0x4000, 0x0000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x4000, DEF_STR( "On") );
		PORT_DIPNAME( 0x8000, 0x0000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x8000, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static GfxLayout tiles8x8_layout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,1),
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 0, 4, 8, 12, 16, 20, 24, 28 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
		32*8
	);
	
	static GfxLayout tiles16x16_layout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,1),
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 0, 4, 8, 12, 16, 20, 24, 28,
		 32,36,40, 44, 48, 52, 56, 60 },
		new int[] { 0*64, 1*64, 2*64, 3*64, 4*64, 5*64, 6*64, 7*64,
		  8*64, 9*64,10*64,11*64,12*64,13*64,14*64,15*64 },
		16*64
	);
	
	static GfxDecodeInfo ashnojoe_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, tiles8x8_layout, 0, 0x100 ),
		new GfxDecodeInfo( REGION_GFX2, 0, tiles8x8_layout, 0, 0x100 ),
		new GfxDecodeInfo( REGION_GFX3, 0, tiles8x8_layout, 0, 0x100 ),
		new GfxDecodeInfo( REGION_GFX4, 0, tiles16x16_layout, 0, 0x100 ),
		new GfxDecodeInfo( REGION_GFX5, 0, tiles16x16_layout, 0, 0x100 ),
		new GfxDecodeInfo( -1 )
	};
	
	static void irqhandler(int irq)
	{
		cpu_set_irq_line(1,0,irq ? ASSERT_LINE : CLEAR_LINE);
	}
	
	public static WriteHandlerPtr writeA = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (data == 0xff) return;	// this gets called at 8910 startup with 0xff before the 5205 exists, causing a crash
	
		MSM5205_reset_w(0, !(data & 0x01));
	} };
	
	public static WriteHandlerPtr writeB = new WriteHandlerPtr() {public void handler(int offset, int data){
		cpu_setbank(4, memory_region(REGION_SOUND1) + ((data & 0xf) * 0x8000));
	} };
	
	static void ashnojoe_adpcm_int (int data)
	{
		cpu_set_nmi_line(1, PULSE_LINE);
	}
	
	static struct MSM5205interface msm5205_interface =
	{
		1,			/* 1 chip */
		384000, 		/* 384KHz */
		{ ashnojoe_adpcm_int},	/* interrupt function */
		{ MSM5205_S48_4B},	/* 4KHz 4-bit */
		{ 50 }			/* volume */
	};
	
	static struct YM2203interface ym2203_interface =
	{
		1,
		3000000,					/* ?? */
		{ YM2203_VOL(100,100) },
		{ 0 },
		{ 0 },
		{ writeA },
		{ writeB },
		{ irqhandler }
	};
	
	public static DriverInitHandlerPtr init_ashnojoe  = new DriverInitHandlerPtr() { public void handler(){
		cpu_setbank(4, memory_region(REGION_SOUND1));
	} };
	
	public static MachineHandlerPtr machine_driver_ashnojoe = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 8000000)	/* 8 MHz? */
		MDRV_CPU_MEMORY(ashnojoe_readmem,ashnojoe_writemem)
		MDRV_CPU_VBLANK_INT(irq1_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 4000000)	/* 4 MHz ??? */
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(sound_readport,sound_writeport)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(512, 512)
		MDRV_VISIBLE_AREA(14*8, 50*8-1, 3*8, 29*8-1)
	
		MDRV_GFXDECODE(ashnojoe_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(0x1000/2)
	
		MDRV_VIDEO_START(ashnojoe)
		MDRV_VIDEO_UPDATE(ashnojoe)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2203, ym2203_interface)
		MDRV_SOUND_ADD(MSM5205, msm5205_interface)
	MACHINE_DRIVER_END();
 }
};
	
	static RomLoadHandlerPtr rom_ashnojoe = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0xc0000, REGION_CPU1, 0 )     /* 68000 code */
		ROM_LOAD16_BYTE( "5.bin", 0x00000, 0x10000, CRC(c61e1569) SHA1(422c18f5810539b5a9e3a9bd4e3b4d70bde8d1d5) )
		ROM_LOAD16_BYTE( "6.bin", 0x00001, 0x10000, CRC(c0a16338) SHA1(fb127b9d38f2c9807b6e23ff71935fc8a22a2e8f) )
		ROM_LOAD16_WORD_SWAP( "sj201-nw.bin", 0x80000, 0x40000, CRC(5a64ca42) SHA1(660b8bca21ef3c2230adce7cb7e7d1f018714f23) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 32k for Z80 code */
		ROM_LOAD( "9.bin", 0x0000, 0x8000, CRC(8767e212) SHA1(13bf927febedff9d7d164fbf0da7fb3a588c2a94) )
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "8.bin", 0x00000, 0x10000, CRC(9bcb160e) SHA1(1677048e5ce26562ff7ba36fcc2d0ed5a652b91e) )
		ROM_LOAD( "7.bin", 0x10000, 0x10000, CRC(7e1efc42) SHA1(e3c282072fdaa0b98c2a1bf25fd02c680d9ca4d7) )
	
		ROM_REGION( 0x20000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "4.bin", 0x00000, 0x10000, CRC(aa6336d3) SHA1(43f70cc3223f11d7929dd44b0edf0a31f5fe41c3) )
		ROM_LOAD( "3.bin", 0x10000, 0x10000, CRC(7e2d86b5) SHA1(8b8d1b9240a700e29afc109eddf6e58a0a7666a4) )
	
		ROM_REGION( 0x20000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "2.bin", 0x00000, 0x10000, CRC(c3254938) SHA1(fd57163f740cd4fdecca94cced91314c289741ae) )
		ROM_LOAD( "1.bin", 0x10000, 0x10000, CRC(1bf585f0) SHA1(4003941636e7fded95e880109c3c9dd1d8f28b07) )
	
		ROM_REGION( 0x100000, REGION_GFX4, ROMREGION_DISPOSE )
		ROM_LOAD16_WORD_SWAP( "sj402-nw.bin", 0x000000, 0x80000, CRC(b6d33d06) SHA1(688ccf467a5112ec522811894e2626ab5f155903) )
		ROM_LOAD16_WORD_SWAP( "sj403-nw.bin", 0x080000, 0x80000, CRC(07143f56) SHA1(1b953c8826d3993a486eed6b9d94d37145fd2e79) )
	
		ROM_REGION( 0x300000, REGION_GFX5, ROMREGION_DISPOSE )
		ROM_LOAD16_WORD_SWAP( "sj404-nw.bin", 0x000000, 0x80000, CRC(8f134128) SHA1(026a6076d54cd5f1d06b29c51031cb79a6b2c11d) )
		ROM_LOAD16_WORD_SWAP( "sj405-nw.bin", 0x080000, 0x80000, CRC(6fd81699) SHA1(8a4f9e47dd39b4b0213c3682da2221ca53bba658) )
		ROM_LOAD16_WORD_SWAP( "sj406-nw.bin", 0x100000, 0x80000, CRC(634e33e6) SHA1(1d6a72a4ca80cd1c1fd6ce9359c304b45091cdfe) )
		ROM_LOAD16_WORD_SWAP( "sj407-nw.bin", 0x180000, 0x80000, CRC(5c66ff06) SHA1(9923ba00679e1b47b5da63c1a13e0f8dd4c78bb5) )
		ROM_LOAD16_WORD_SWAP( "sj408-nw.bin", 0x200000, 0x80000, CRC(6a3b1ea1) SHA1(e39a6e52d930f291bf237cf9db3d4b3d2fad53e0) )
		ROM_LOAD16_WORD_SWAP( "sj409-nw.bin", 0x280000, 0x80000, CRC(d8764213) SHA1(89eadefb956863216c8e3d0380394aba35e8c856) )
	
		ROM_REGION( 0x80000, REGION_SOUND1, 0 )   /* samples? */
		ROM_LOAD( "sj401-nw.bin", 0x00000, 0x80000, CRC(25dfab59) SHA1(7d50159204ba05323a2442778f35192e66117dda) )
	ROM_END(); }}; 
	
	public static GameDriver driver_ashnojoe	   = new GameDriver("1990"	,"ashnojoe"	,"ashnojoe.java"	,rom_ashnojoe,null	,machine_driver_ashnojoe	,input_ports_ashnojoe	,init_ashnojoe	,ROT0, "WAVE / Taito Corporation", "Ashita no Joe (Japan)", GAME_IMPERFECT_SOUND )
}
