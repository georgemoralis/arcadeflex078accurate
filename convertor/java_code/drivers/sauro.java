/***************************************************************************

Sauro
-----

driver by Zsolt Vasvari

Main CPU
--------

Memory mapped:

0000-dfff	ROM
e000-e7ff	RAM
e800-ebff	Sprite RAM
f000-fbff	Background Video RAM
f400-ffff	Background Color RAM
f800-fbff	Foreground Video RAM
fc00-ffff	Foreground Color RAM

Ports:

00		R	DSW #1
20		R	DSW #2
40		R	Input Ports Player 1
60		R   Input Ports Player 2
80		 W  Sound Commnand
c0		 W  Flip Screen
c1		 W  ???
c2-c4	 W  ???
c6-c7	 W  ??? (Loads the sound latch?)
c8		 W	???
c9		 W	???
ca-cd	 W  ???
ce		 W  ???
e0		 W	Watchdog


Sound CPU
---------

Memory mapped:

0000-7fff		ROM
8000-87ff		RAM
a000	     W  ADPCM trigger
c000-c001	 W	YM3812
e000		R   Sound latch
e000-e006	 W  ???
e00e-e00f	 W  ???


TODO
----

- The readme claims there is a GI-SP0256A-AL ADPCM on the PCB. Needs to be
  emulated.

- Verify all clock speeds

- I'm only using colors 0-15. The other 3 banks are mostly the same, but,
  for example, the color that's used to paint the gradients of the sky (color 2)
  is different, so there might be a palette select. I don't see anything
  obviously wrong the way it is right now. It matches the screen shots found
  on the Spanish Dump site.

- What do the rest of the ports in the range c0-ce do?

Tricky Doc
----------

Addition by Reip

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class sauro
{
	
	
	
	
	
	
	public static WriteHandlerPtr sauro_sound_command_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		data |= 0x80;
		soundlatch_w.handler(offset, data);
	} };
	
	public static ReadHandlerPtr sauro_sound_command_r  = new ReadHandlerPtr() { public int handler(int offset){
		int ret	= soundlatch_r(offset);
		soundlatch_clear_w(offset, 0);
		return ret;
	} };
	
	public static WriteHandlerPtr sauro_coin1_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		coin_counter_w(0, data);
		coin_counter_w(0, 0); // to get the coin counter working in sauro, as it doesn't write 0
	} };
	
	public static WriteHandlerPtr sauro_coin2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		coin_counter_w(1, data);
		coin_counter_w(1, 0); // to get the coin counter working in sauro, as it doesn't write 0
	} };
	
	public static Memory_ReadAddress sauro_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xdfff, MRA_ROM ),
		new Memory_ReadAddress( 0xe000, 0xebff, MRA_RAM ),
		new Memory_ReadAddress( 0xf000, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sauro_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xdfff, MWA_ROM ),
		new Memory_WriteAddress( 0xe000, 0xe7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xe800, 0xebff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0xf000, 0xf3ff, tecfri_videoram_w, tecfri_videoram ),
		new Memory_WriteAddress( 0xf400, 0xf7ff, tecfri_colorram_w, tecfri_colorram ),
		new Memory_WriteAddress( 0xf800, 0xfbff, tecfri_videoram2_w, tecfri_videoram2 ),
		new Memory_WriteAddress( 0xfc00, 0xffff, tecfri_colorram2_w, tecfri_colorram2 ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort sauro_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, input_port_2_r ),
		new IO_ReadPort( 0x20, 0x20, input_port_3_r ),
		new IO_ReadPort( 0x40, 0x40, input_port_0_r ),
		new IO_ReadPort( 0x60, 0x60, input_port_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sauro_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0xa0, 0xa0, tecfri_scroll_bg_w, ),
		new IO_WritePort( 0xa1, 0xa1, sauro_scroll_fg_w, ),
		new IO_WritePort( 0x80, 0x80, sauro_sound_command_w, ),
		new IO_WritePort( 0xc0, 0xc0, flip_screen_w, ),
		new IO_WritePort( 0xc1, 0xc2, MWA_NOP ),
		new IO_WritePort( 0xc3, 0xc3, sauro_coin1_w ),
		new IO_WritePort( 0xc4, 0xc4, MWA_NOP ),
		new IO_WritePort( 0xc5, 0xc5, sauro_coin2_w ),
		new IO_WritePort( 0xc6, 0xce, MWA_NOP ),
		new IO_WritePort( 0xe0, 0xe0, watchdog_reset_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress sauro_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new Memory_ReadAddress( 0xe000, 0xe000, sauro_sound_command_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sauro_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x8000, 0x87ff, MWA_RAM ),
		new Memory_WriteAddress( 0xc000, 0xc000, YM3812_control_port_0_w ),
		new Memory_WriteAddress( 0xc001, 0xc001, YM3812_write_port_0_w ),
	//	new Memory_WriteAddress( 0xa000, 0xa000, ADPCM_trigger ),
		new Memory_WriteAddress( 0xe000, 0xe006, MWA_NOP ),
		new Memory_WriteAddress( 0xe00e, 0xe00f, MWA_NOP ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress trckydoc_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xdfff, MRA_ROM ),
		new Memory_ReadAddress( 0xe000, 0xe7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xf800, 0xf800, input_port_2_r ),
		new Memory_ReadAddress( 0xf808, 0xf808, input_port_3_r ),
		new Memory_ReadAddress( 0xf810, 0xf810, input_port_0_r ),
		new Memory_ReadAddress( 0xf818, 0xf818, input_port_1_r ),
		new Memory_ReadAddress( 0xf828, 0xf828, watchdog_reset_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress trckydoc_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xdfff, MWA_ROM ),
		new Memory_WriteAddress( 0xe000, 0xe7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xe800, 0xebff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0xec00, 0xefff, trckydoc_spriteram_mirror_w ), // it clears sprites from the screen by writing here to set some of the attributes
		new Memory_WriteAddress( 0xf000, 0xf3ff, tecfri_videoram_w, tecfri_videoram ),
		new Memory_WriteAddress( 0xf400, 0xf7ff, tecfri_colorram_w, tecfri_colorram ),
		new Memory_WriteAddress( 0xf820, 0xf820, YM3812_control_port_0_w ),
		new Memory_WriteAddress( 0xf821, 0xf821, YM3812_write_port_0_w ),
		new Memory_WriteAddress( 0xf830, 0xf830, tecfri_scroll_bg_w ),
		new Memory_WriteAddress( 0xf838, 0xf838, MWA_NOP ),
		new Memory_WriteAddress( 0xf839, 0xf839, flip_screen_w ),
		new Memory_WriteAddress( 0xf83a, 0xf83a, sauro_coin1_w ),
		new Memory_WriteAddress( 0xf83b, 0xf83b, sauro_coin2_w ),
		new Memory_WriteAddress( 0xf83c, 0xf83c, watchdog_reset_w ),
		new Memory_WriteAddress( 0xf83f, 0xf83f, MWA_NOP ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	static InputPortHandlerPtr input_ports_tecfri = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( tecfri )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_COCKTAIL | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_COCKTAIL | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_COCKTAIL | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_COCKTAIL | IPF_8WAY );
	
		PORT_START(); 
		PORT_SERVICE( 0x01, IP_ACTIVE_HIGH );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x30, 0x20, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x30, "Very Easy" );
		PORT_DIPSETTING(    0x20, "Easy" );
		PORT_DIPSETTING(    0x10, "Hard" );                      /* This crashes test mode!!! */
		PORT_DIPSETTING(    0x00, "Very Hard" );
		PORT_DIPNAME( 0x40, 0x40, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x80, 0x00, "Freeze" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_5C") );
		PORT_DIPNAME( 0x30, 0x20, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x30, "2" );
		PORT_DIPSETTING(    0x20, "3" );
		PORT_DIPSETTING(    0x10, "4" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 chars */
	    2048,   /* 2048 characters */
	    4,      /* 4 bits per pixel */
	    new int[] { 0,1,2,3 },  /* The 4 planes are packed together */
	    new int[] { 0*4, 1*4, 2*4, 3*4, 4*4, 5*4, 6*4, 7*4},
	    new int[] { 0*4*8, 1*4*8, 2*4*8, 3*4*8, 4*4*8, 5*4*8, 6*4*8, 7*4*8},
	    8*8*4     /* every char takes 32 consecutive bytes */
	);
	
	static GfxLayout trckydoc_spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
	    512,	/* 512 sprites */
	    4,      /* 4 bits per pixel */
	    new int[] { 0,1,2,3 },  /* The 4 planes are packed together */
	    new int[] { 1*4, 0*4, 3*4, 2*4, 5*4, 4*4, 7*4, 6*4, 9*4, 8*4, 11*4, 10*4, 13*4, 12*4, 15*4, 14*4},
	    new int[] { RGN_FRAC(3,4)+0*4*16, RGN_FRAC(2,4)+0*4*16, RGN_FRAC(1,4)+0*4*16, RGN_FRAC(0,4)+0*4*16,
	      RGN_FRAC(3,4)+1*4*16, RGN_FRAC(2,4)+1*4*16, RGN_FRAC(1,4)+1*4*16, RGN_FRAC(0,4)+1*4*16,
	      RGN_FRAC(3,4)+2*4*16, RGN_FRAC(2,4)+2*4*16, RGN_FRAC(1,4)+2*4*16, RGN_FRAC(0,4)+2*4*16,
	      RGN_FRAC(3,4)+3*4*16, RGN_FRAC(2,4)+3*4*16, RGN_FRAC(1,4)+3*4*16, RGN_FRAC(0,4)+3*4*16, },
	    16*16     /* every sprite takes 32 consecutive bytes */
	);
	
	static GfxLayout sauro_spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
	    1024,	/* 1024 sprites */
	    4,      /* 4 bits per pixel */
	    new int[] { 0,1,2,3 },  /* The 4 planes are packed together */
	    new int[] { 1*4, 0*4, 3*4, 2*4, 5*4, 4*4, 7*4, 6*4, 9*4, 8*4, 11*4, 10*4, 13*4, 12*4, 15*4, 14*4},
	    new int[] { RGN_FRAC(3,4)+0*4*16, RGN_FRAC(2,4)+0*4*16, RGN_FRAC(1,4)+0*4*16, RGN_FRAC(0,4)+0*4*16,
	      RGN_FRAC(3,4)+1*4*16, RGN_FRAC(2,4)+1*4*16, RGN_FRAC(1,4)+1*4*16, RGN_FRAC(0,4)+1*4*16,
	      RGN_FRAC(3,4)+2*4*16, RGN_FRAC(2,4)+2*4*16, RGN_FRAC(1,4)+2*4*16, RGN_FRAC(0,4)+2*4*16,
	      RGN_FRAC(3,4)+3*4*16, RGN_FRAC(2,4)+3*4*16, RGN_FRAC(1,4)+3*4*16, RGN_FRAC(0,4)+3*4*16, },
	    16*16     /* every sprite takes 32 consecutive bytes */
	);
	
	static GfxDecodeInfo sauro_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout, 0, 64 ),
		new GfxDecodeInfo( REGION_GFX2, 0, charlayout, 0, 64 ),
		new GfxDecodeInfo( REGION_GFX3, 0, sauro_spritelayout, 0, 64 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static GfxDecodeInfo trckydoc_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout, 0, 64 ),
		new GfxDecodeInfo( REGION_GFX2, 0, trckydoc_spritelayout, 0, 64 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	public static InterruptHandlerPtr sauro_interrupt = new InterruptHandlerPtr() {public void handler(){
		cpu_set_irq_line(1, IRQ_LINE_NMI, PULSE_LINE);
		cpu_set_irq_line(1, 0, HOLD_LINE);
	} };
	
	static struct YM3526interface ym3812_interface =
	{
		1,			/* 1 chip (no more supported) */
		3600000,	/* 3.600000 MHz ? */
		{ 100 } 	/* volume */
	};
	
	public static MachineHandlerPtr machine_driver_tecfri = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main", Z80, 4000000)        // 4 MHz???
		MDRV_CPU_VBLANK_INT(irq0_line_hold, 1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(5000)  // frames per second, vblank duration (otherwise sprites lag)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32 * 8, 32 * 8)
		MDRV_VISIBLE_AREA(1 * 8, 31 * 8 - 1, 2 * 8, 30 * 8 - 1)
		MDRV_PALETTE_LENGTH(1024)
		MDRV_PALETTE_INIT(RRRR_GGGG_BBBB)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM3812, ym3812_interface)
	MACHINE_DRIVER_END();
 }
};
	
	public static MachineHandlerPtr machine_driver_trckydoc = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
		MDRV_IMPORT_FROM(tecfri)
	
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(trckydoc_readmem, trckydoc_writemem )
	
		MDRV_GFXDECODE(trckydoc_gfxdecodeinfo)
	
		MDRV_VIDEO_START(trckydoc)
		MDRV_VIDEO_UPDATE(trckydoc)
	MACHINE_DRIVER_END();
 }
};
	
	public static MachineHandlerPtr machine_driver_sauro = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
		MDRV_IMPORT_FROM(tecfri)
	
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(sauro_readmem, sauro_writemem)
		MDRV_CPU_PORTS(sauro_readport, sauro_writeport)
	
		MDRV_CPU_ADD(Z80, 4000000)	// 4 MHz?
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(sauro_sound_readmem, sauro_sound_writemem)
		MDRV_CPU_VBLANK_INT(sauro_interrupt, 8) // ?
	
		MDRV_GFXDECODE(sauro_gfxdecodeinfo)
	
		MDRV_VIDEO_START(sauro)
		MDRV_VIDEO_UPDATE(sauro)
	MACHINE_DRIVER_END();
 }
};
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadHandlerPtr rom_sauro = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )          /* 64k for code */
		ROM_LOAD( "sauro-2.bin",     0x00000, 0x8000, CRC(19f8de25) SHA1(52eea7c0416ab0a8dbb3d1664b2f57ab7a405a67) )
		ROM_LOAD( "sauro-1.bin",     0x08000, 0x8000, CRC(0f8b876f) SHA1(6e61a8934a2cc3c80c1f47dd59aa43aaeec12f75) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )          /* 64k for sound CPU */
		ROM_LOAD( "sauro-3.bin",     0x00000, 0x8000, CRC(0d501e1b) SHA1(20a56ff30d4fa5d2f483a449703b49153839f6bc) )
	
		ROM_REGION( 0x10000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "sauro-6.bin",     0x00000, 0x8000, CRC(4b77cb0f) SHA1(7b9cb2dca561d81390106c1a5c0533dcecaf6f1a) )
		ROM_LOAD( "sauro-7.bin",     0x08000, 0x8000, CRC(187da060) SHA1(1df156e58379bb39acade02aabab6ff1cb7cc288) )
	
		ROM_REGION( 0x10000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "sauro-4.bin",     0x00000, 0x8000, CRC(9b617cda) SHA1(ce26b84ad5ecd6185ae218520e9972645bbf09ad) )
		ROM_LOAD( "sauro-5.bin",     0x08000, 0x8000, CRC(a6e2640d) SHA1(346ffcf62e27ce8134f4e5e0dbcf11f110e19e04) )
	
		ROM_REGION( 0x20000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "sauro-8.bin",     0x00000, 0x8000, CRC(e08b5d5e) SHA1(eaaeaa08b19c034ab2a2140f887edffca5f441b9) )
		ROM_LOAD( "sauro-9.bin",     0x08000, 0x8000, CRC(7c707195) SHA1(0529f6808b0cec3e12ca51bee189841d21577786) )
		ROM_LOAD( "sauro-10.bin",    0x10000, 0x8000, CRC(c93380d1) SHA1(fc9655cc94c2d2058f83eb341be7e7856a08194f) )
		ROM_LOAD( "sauro-11.bin",    0x18000, 0x8000, CRC(f47982a8) SHA1(cbaeac272c015d9439f151cfb3449082f11a57a1) )
	
		ROM_REGION( 0x0c00, REGION_PROMS, 0 )
		ROM_LOAD( "82s137-3.bin",    0x0000, 0x0400, CRC(d52c4cd0) SHA1(27d6126b46616c06b55d8018c97f6c3d7805ae9e) )  /* Red component */
		ROM_LOAD( "82s137-2.bin",    0x0400, 0x0400, CRC(c3e96d5d) SHA1(3f6f21526a4357e4a9a9d56a6f4ef5911af2d120) )  /* Green component */
		ROM_LOAD( "82s137-1.bin",    0x0800, 0x0400, CRC(bdfcf00c) SHA1(9faf4d7f8959b64faa535c9945eec59c774a3760) )  /* Blue component */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_trckydoc = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )          /* 64k for code */
		ROM_LOAD( "trckydoc.d9",  0x0000,  0x8000, CRC(c6242fc3) SHA1(c8a6f6abe8b51061a113ed75fead0479df68ec40) )
		ROM_LOAD( "trckydoc.b9",  0x8000,  0x8000, CRC(8645c840) SHA1(79c2acfc1aeafbe94afd9d230200bd7cdd7bcd1b) )
	
		ROM_REGION( 0x10000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "trckydoc.e6",     0x00000, 0x8000, CRC(ec326392) SHA1(e6954fecc501a821caa21e67597914519fbbe58f) )
		ROM_LOAD( "trckydoc.g6",     0x08000, 0x8000, CRC(6a65c088) SHA1(4a70c104809d86b4eef6cc0df9452966fe7c9859) )
	
		ROM_REGION( 0x10000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "trckydoc.h1",    0x00000, 0x4000, CRC(8b73cbf3) SHA1(d10f79a38c1596c90bac9cf4c64ba38ae6ecd8cb) )
		ROM_LOAD( "trckydoc.e1",    0x04000, 0x4000, CRC(841be98e) SHA1(82da07490b73edcbffc3b9247205aab3a1f7d7ad) )
		ROM_LOAD( "trckydoc.c1",    0x08000, 0x4000, CRC(1d25574b) SHA1(924e4376a7fe6cdfff0fa6045aaa3f7c0633d275) )
		ROM_LOAD( "trckydoc.a1",    0x0c000, 0x4000, CRC(436c59ba) SHA1(2aa9c155c432a3c81420520c53bb944dcc613a94) )
	
		ROM_REGION( 0x0c00, REGION_PROMS, 0 ) // colour proms
		ROM_LOAD( "tdclr3.prm",    0x0000, 0x0100, CRC(671d0140) SHA1(7d5fcd9589c46590b0a240cac428f993201bec2a) )
		ROM_LOAD( "tdclr2.prm",    0x0400, 0x0100, CRC(874f9050) SHA1(db40d68f5166657fce0eadcd82143112b0388894) )
		ROM_LOAD( "tdclr1.prm",    0x0800, 0x0100, CRC(57f127b0) SHA1(3d2b18a7a31933579f06d92fa0cc3f0e1fe8b98a) )
	
		ROM_REGION( 0x0200, REGION_USER1, 0 ) // unknown
		ROM_LOAD( "tdprm.prm",    0x0000, 0x0200,  CRC(5261bc11) SHA1(1cc7a9a7376e65f4587b75ef9382049458656372) )
	ROM_END(); }}; 
	
	public static DriverInitHandlerPtr init_tecfri  = new DriverInitHandlerPtr() { public void handler(){
		/* This game doesn't like all memory to be initialized to zero, it won't
		   initialize the high scores */
	
		UINT8 *RAM = memory_region(REGION_CPU1);
	
		memset(&RAM[0xe000], 0, 0x100);
		RAM[0xe000] = 1;
	} };
	
	public static GameDriver driver_sauro	   = new GameDriver("1987"	,"sauro"	,"sauro.java"	,rom_sauro,null	,machine_driver_sauro	,input_ports_tecfri	,init_tecfri	,ROT0, "Tecfri", "Sauro", GAME_IMPERFECT_SOUND )
	public static GameDriver driver_trckydoc	   = new GameDriver("1987"	,"trckydoc"	,"sauro.java"	,rom_trckydoc,null	,machine_driver_trckydoc	,input_ports_tecfri	,init_tecfri	,ROT0, "Tecfri", "Tricky Doc" )
}
