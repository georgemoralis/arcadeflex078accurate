/****************************************************************************

Sega "Space Tactics" Driver

Frank Palazzolo (palazzol@home.com)

Master processor - Intel 8080A

Memory Map:

0000-2fff ROM                       R
4000-47ff RAM                       R/W
5000-5fff switches/status           R
6000-6fff dips sw                   R
6000-600f Coin rjct/palette select  W
6010-601f sound triggers            W
6020-602f lamp latch                W
6030-603f speed latch               W
6040-605f shot related              W
6060-606f score display             W
60a0-60e0 sound triggers2           W
7000-7fff RNG/swit                  R     LS Nibble are a VBlank counter
								          used as a RNG
8000-8fff swit/stat                 R
8000-8fff offset RAM                W
9000-9fff V pos reg.                R     Reads counter from an encoder wheel
a000-afff H pos reg.                R     Reads counter from an encoder wheel
b000-bfff VRAM B                    R/W   alphanumerics, bases, barrier,
                                          enemy bombs
d000-dfff VRAM D                    R/W   furthest aliens (scrolling)
e000-efff VRAM E                    R/W   middle aliens (scrolling)
f000-ffff VRAM F                    R/W   closest aliens (scrolling)

--------------------------------------------------------------------

At this time, emulation is missing:

Lamps (Credit, Barrier, and 5 lamps for firing from the bases)
Sound (all discrete and a 76477)
Verify Color PROM resistor values (Last 8 colors)
Verify Bar graph displays

****************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class stactics
{
	
	/* Defined in machine/stactics.c */
	
	/* Defined in vidhrdw/stactics.c */
	
	
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
	    new Memory_ReadAddress( 0x0000, 0x2fff, MRA_ROM ),
	    new Memory_ReadAddress( 0x4000, 0x47ff, MRA_RAM ),
	    new Memory_ReadAddress( 0x5000, 0x5fff, input_port_0_r, ),
	    new Memory_ReadAddress( 0x6000, 0x6fff, input_port_1_r, ),
	    new Memory_ReadAddress( 0x7000, 0x7fff, stactics_port_2_r, ),
	    new Memory_ReadAddress( 0x8000, 0x8fff, stactics_port_3_r ),
	    new Memory_ReadAddress( 0x9000, 0x9fff, stactics_vert_pos_r ),
	    new Memory_ReadAddress( 0xa000, 0xafff, stactics_horiz_pos_r ),
	
	    new Memory_ReadAddress( 0xb000, 0xb3ff, MRA_RAM ),
	    new Memory_ReadAddress( 0xb800, 0xbfff, MRA_RAM ),
	
	    new Memory_ReadAddress( 0xd000, 0xd3ff, MRA_RAM ),
	    new Memory_ReadAddress( 0xd600, 0xd7ff, MRA_RAM ),   /* Used as scratch RAM, high scores, etc. */
	    new Memory_ReadAddress( 0xd800, 0xdfff, MRA_RAM ),
	
	    new Memory_ReadAddress( 0xe000, 0xe3ff, MRA_RAM ),
	    new Memory_ReadAddress( 0xe600, 0xe7ff, MRA_RAM ),   /* Used as scratch RAM, high scores, etc. */
	    new Memory_ReadAddress( 0xe800, 0xefff, MRA_RAM ),
	
	    new Memory_ReadAddress( 0xf000, 0xf3ff, MRA_RAM ),
	    new Memory_ReadAddress( 0xf600, 0xf7ff, MRA_RAM ),   /* Used as scratch RAM, high scores, etc. */
	    new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
	
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
	    new Memory_WriteAddress( 0x4000, 0x47ff, MWA_RAM ),
	    new Memory_WriteAddress( 0x6000, 0x6001, stactics_coin_lockout_w ),
	    new Memory_WriteAddress( 0x6006, 0x6007, stactics_palette_w ),
	    /* new Memory_WriteAddress( 0x6010, 0x601f, stactics_sound_w ), */
	    new Memory_WriteAddress( 0x6016, 0x6016, MWA_RAM, stactics_motor_on ),  /* Note: This overlaps rocket sound */
	    /* new Memory_WriteAddress( 0x6020, 0x602f, stactics_lamp_latch_w ), */
	    new Memory_WriteAddress( 0x6030, 0x603f, stactics_speed_latch_w ),
	    new Memory_WriteAddress( 0x6040, 0x604f, stactics_shot_trigger_w ),
	    new Memory_WriteAddress( 0x6050, 0x605f, stactics_shot_flag_clear_w ),
	    new Memory_WriteAddress( 0x6060, 0x606f, MWA_RAM, stactics_display_buffer ),
	    /* new Memory_WriteAddress( 0x60a0, 0x60ef, stactics_sound2_w ), */
	
	    new Memory_WriteAddress( 0x8000, 0x8fff, stactics_scroll_ram_w, stactics_scroll_ram ),
	
	    new Memory_WriteAddress( 0xb000, 0xb3ff, stactics_videoram_b_w, stactics_videoram_b, videoram_size ),
	    new Memory_WriteAddress( 0xb400, 0xb7ff, MWA_RAM ),   /* Unused, but initialized */
	    new Memory_WriteAddress( 0xb800, 0xbfff, stactics_chardata_b_w, stactics_chardata_b ),
	
	    new Memory_WriteAddress( 0xc000, 0xcfff, MWA_NOP ), /* according to the schematics, nothing is mapped here */
	                                 /* but, the game still tries to clear this out         */
	
	    new Memory_WriteAddress( 0xd000, 0xd3ff, stactics_videoram_d_w, stactics_videoram_d ),
	    new Memory_WriteAddress( 0xd400, 0xd7ff, MWA_RAM ),   /* Used as scratch RAM, high scores, etc. */
	    new Memory_WriteAddress( 0xd800, 0xdfff, stactics_chardata_d_w, stactics_chardata_d ),
	
	    new Memory_WriteAddress( 0xe000, 0xe3ff, stactics_videoram_e_w, stactics_videoram_e ),
	    new Memory_WriteAddress( 0xe400, 0xe7ff, MWA_RAM ),   /* Used as scratch RAM, high scores, etc. */
	    new Memory_WriteAddress( 0xe800, 0xefff, stactics_chardata_e_w, stactics_chardata_e ),
	
	    new Memory_WriteAddress( 0xf000, 0xf3ff, stactics_videoram_f_w, stactics_videoram_f ),
	    new Memory_WriteAddress( 0xf400, 0xf7ff, MWA_RAM ),   /* Used as scratch RAM, high scores, etc. */
	    new Memory_WriteAddress( 0xf800, 0xffff, stactics_chardata_f_w, stactics_chardata_f ),
	
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	static InputPortHandlerPtr input_ports_stactics = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( stactics )
	
	    PORT_START();   /* 	IN0 */
	    /*PORT_BIT (0x80, IP_ACTIVE_HIGH, IPT_UNUSED );Motor status. see stactics_port_0_r */
	    PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 );
	    PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START1 );
	    PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON3 );
	    PORT_BIT (0x08, IP_ACTIVE_LOW, IPT_BUTTON4 );
	    PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON5 );
	    PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON6 );
	    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON7 );
	
	    PORT_START();   /* IN1 */
	    PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Coin_B") );
	    PORT_DIPSETTING(    0x01, DEF_STR( "4C_1C") );
	    PORT_DIPSETTING(    0x05, DEF_STR( "2C_1C") );
	    PORT_DIPSETTING(    0x07, DEF_STR( "1C_1C") );
	    PORT_DIPSETTING(    0x06, DEF_STR( "1C_2C") );
	    PORT_DIPSETTING(    0x04, DEF_STR( "1C_3C") );
	    PORT_DIPSETTING(    0x02, DEF_STR( "1C_4C") );
	    PORT_DIPSETTING(    0x00, DEF_STR( "1C_5C") );
	    PORT_DIPSETTING(    0x03, DEF_STR( "1C_6C") );
	    PORT_DIPNAME( 0x38, 0x38, DEF_STR( "Coin_A") );
	    PORT_DIPSETTING(    0x08, DEF_STR( "4C_1C") );
	    PORT_DIPSETTING(    0x28, DEF_STR( "2C_1C") );
	    PORT_DIPSETTING(    0x38, DEF_STR( "1C_1C") );
	    PORT_DIPSETTING(    0x30, DEF_STR( "1C_2C") );
	    PORT_DIPSETTING(    0x20, DEF_STR( "1C_3C") );
	    PORT_DIPSETTING(    0x10, DEF_STR( "1C_4C") );
	    PORT_DIPSETTING(    0x00, DEF_STR( "1C_5C") );
	    PORT_DIPSETTING(    0x18, DEF_STR( "1C_6C") );
	    PORT_DIPNAME( 0x40, 0x00, "High Score Initial Entry" );
	    PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
	    PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	    PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Demo_Sounds") );
	    PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
	    PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
	    PORT_START();   /* IN2 */
	    /* This is accessed by stactics_port_2_r() */
	    /*PORT_BIT (0x0f, IP_ACTIVE_HIGH, IPT_UNUSED );Random number generator */
	    PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_COIN1 );
	    PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN2 );
	    PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Free_Play") );
	    PORT_DIPSETTING( 0x40, DEF_STR( "Off") );
	    PORT_DIPSETTING( 0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
	    PORT_START();   /* IN3 */
	    /* This is accessed by stactics_port_3_r() */
	    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 );
	    /* PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNUSED );*/
	    PORT_DIPNAME( 0x04, 0x04, "Number of Barriers" );
	    PORT_DIPSETTING(    0x04, "4" );
	    PORT_DIPSETTING(    0x00, "6" );
	    PORT_DIPNAME( 0x08, 0x08, "Bonus Barriers" );
	    PORT_DIPSETTING(    0x08, "1" );
	    PORT_DIPSETTING(    0x00, "2" );
	    PORT_DIPNAME( 0x10, 0x00, "Extended Play" );
	    PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
	    PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	    PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY );
	    PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
	    /* PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );*/
	
		PORT_START(); 	/* FAKE */
	    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY );
	    PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY );
	INPUT_PORTS_END(); }}; 
	
	/* For the character graphics */
	
	static GfxLayout gfxlayout = new GfxLayout
	(
	    8,8,
	    256,
	    1,     /* 1 bit per pixel */
	    new int[] { 0 },
	    new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
	    new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
	    8*8
	);
	
	/* For the LED Fire Beam (made up) */
	
	static GfxLayout firelayout = new GfxLayout
	(
	    16,9,
	    256,
	    1,     /* 1 bit per pixel */
	    new int[] { 0 },
	    new int[] { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7 },
	    new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8, 8*8 },
	    8*9
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
	    new GfxDecodeInfo( 0, 0, gfxlayout,  0,      64*4 ),    /* Dynamically decoded from RAM */
	    new GfxDecodeInfo( 0, 0, gfxlayout,  1*2*16, 64*4 ),    /* Dynamically decoded from RAM */
	    new GfxDecodeInfo( 0, 0, gfxlayout,  2*2*16, 64*4 ),    /* Dynamically decoded from RAM */
	    new GfxDecodeInfo( 0, 0, gfxlayout,  3*2*16, 64*4 ),    /* Dynamically decoded from RAM */
	    new GfxDecodeInfo( 0, 0, firelayout, 0, 64*4 ),         /* LED Fire beam (synthesized gfx) */
	    new GfxDecodeInfo( 0, 0, gfxlayout,  0, 64*4 ),         /* LED and Misc. Display characters */
	    new GfxDecodeInfo( -1 )
	};
	
	public static MachineHandlerPtr machine_driver_stactics = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(8080, 1933560)
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(stactics_interrupt,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 0*8, 32*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(16)
		MDRV_COLORTABLE_LENGTH(16*4*4*2)
	
		MDRV_PALETTE_INIT(stactics)
		MDRV_VIDEO_START(stactics)
		MDRV_VIDEO_UPDATE(stactics)
	
		/* sound hardware */
	MACHINE_DRIVER_END();
 }
};
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadHandlerPtr rom_stactics = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 ) /* 64k for code */
		ROM_LOAD( "epr-218x",     0x0000, 0x0800, CRC(b1186ad2) SHA1(88929a183ac0499619b3e07241f3b5a0c89bdab1) )
		ROM_LOAD( "epr-219x",     0x0800, 0x0800, CRC(3b86036d) SHA1(6ad5e14dcfdbc6d2a0a32ae7f18ce41ab4b51eec) )
		ROM_LOAD( "epr-220x",     0x1000, 0x0800, CRC(c58702da) SHA1(93936c46810722d435f9ddb0641defb741743dee) )
		ROM_LOAD( "epr-221x",     0x1800, 0x0800, CRC(e327639e) SHA1(024929b65c71eaeb6d234a14d7535a7d5b98b8d3) )
		ROM_LOAD( "epr-222y",     0x2000, 0x0800, CRC(24dd2bcc) SHA1(f77c59beccc1a77e3bfc2928ff532d6e221ff42d) )
		ROM_LOAD( "epr-223x",     0x2800, 0x0800, CRC(7fef0940) SHA1(5b2af55f75ef0130f9202b6a916a96dbd601fcfa) )
	
		ROM_REGION( 0x1060, REGION_GFX1, ROMREGION_DISPOSE )	/* gfx decoded in vh_start */
		ROM_LOAD( "epr-217",      0x0000, 0x0800, CRC(38259f5f) SHA1(1f4182ffc2d78fca22711526bb2ae2cfe040173c) )      /* LED fire beam data      */
		ROM_LOAD( "pr55",         0x0800, 0x0800, CRC(f162673b) SHA1(83743780b6c1f8014df24fa0650000b7cb137d92) )      /* timing PROM (unused)    */
		ROM_LOAD( "pr65",         0x1000, 0x0020, CRC(a1506b9d) SHA1(037c3db2ea40eca459e8acba9d1506dd28d72d10) )      /* timing PROM (unused)    */
		ROM_LOAD( "pr66",         0x1020, 0x0020, CRC(78dcf300) SHA1(37034cc0cfa4a8ec47937a2a34b77ec56b387a9b) )      /* timing PROM (unused)    */
		ROM_LOAD( "pr67",         0x1040, 0x0020, CRC(b27874e7) SHA1(c24bc78c4b2ae01aaed5d994ce2e7c5e0f2eece8) )      /* LED timing ROM (unused) */
	
		ROM_REGION( 0x0800, REGION_PROMS, 0 )
		ROM_LOAD( "pr54",         0x0000, 0x0800, CRC(9640bd6e) SHA1(dd12952a6591f2056ac1b5688dca0a3a2ef69f2d) )         /* color/priority prom */
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_stactics	   = new GameDriver("1981"	,"stactics"	,"stactics.java"	,rom_stactics,null	,machine_driver_stactics	,input_ports_stactics	,null	,ROT0, "Sega", "Space Tactics", GAME_NO_SOUND )
	
}
