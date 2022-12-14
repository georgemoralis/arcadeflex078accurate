/****************************************************************************/
/*                                                                          */
/*  8080bw.c                                                                */
/*                                                                          */
/*  Michael Strutts, Nicola Salmoria, Tormod Tjaberg, Mirko Buffoni         */
/*  Lee Taylor, Valerio Verrando, Marco Cassili, Zsolt Vasvari and others   */
/*                                                                          */
/*                                                                          */
/*  Notes:                                                                  */
/*  -----                                                                   */
/*                                                                          */
/*  - "The Amazing Maze Game" on title screen, but manual, flyer,           */
/*    cabinet side art all call it just "Amazing Maze"                      */
/*                                                                          */
/*  - Desert Gun is also known as Road Runner                               */
/*                                                                          */
/*  - Space Invaders Deluxe still says Space Invaders Part II,              */
/*    because according to KLOV, Midway was only allowed to make minor      */
/*    modifications of the Taito code.  Read all about it here:             */
/*    http://www.klov.com/S/Space_Invaders_Deluxe.html                      */
/*                                                                          */
/*                                                                          */
/*  To Do:                                                                  */
/*  -----                                                                   */
/*                                                                          */
/*  - Space Invaders Deluxe: overlay                                        */
/*                                                                          */
/*  - Space Encounters: 'trench' circuit                                    */
/*                                                                          */
/*  - Phantom II: verify clouds                                             */
/*                                                                          */
/*  - Helifire: analog wave and star background                             */
/*                                                                          */
/*  - Sheriff: color PROM                                           		*/
/*                                                                          */
/*                                                                          */
/*  Games confirmed not use an overlay (pure black and white):              */
/*  ---------------------------------------------------------               */
/*                                                                          */
/*  - 4 Player Bowling                                                      */
/*                                                                          */
/****************************************************************************/
/*                                                                          */
/* Change Log                                                               */
/*                                                                          */
/* 26 May 2001 - Following were renamed                                     */
/* galxwars -> galxwart - Galaxy Wars (c)1979 Taito, possible bootleg       */
/* spaceatt -> spaceat2 - Space Attack Part II                              */
/*                                                                          */
/* 26 May 2001 - Following were added                                       */
/* galxwars - Galaxy Wars (set 1) (c)1979 Universal                         */
/* galxwar2 - Galaxy Wars (set 2) (c)1979 Universal                         */
/* jspectr2 - Jatre Specter (set 2) (c)1979 Jatre                           */
/* ozmawar2 - Ozma Wars (set 2) (c)1979 SNK, on Taito 3 Colour Invaders BD  */
/* spaceatt - Space Attack (c)1978 Video Game GMBH                          */
/* sstrangr - Space Stranger (c)1978 Yachiyo Electronics, Ltd.              */
/*                                                                          */
/* 26 May 2001 - galxwars input port changed slightly so the new sets work  */
/*                                                                          */
/* ------------------------------------------------------------------------ */
/*                                                                          */
/* 30 July 2001 - sstrngr2 Added (c)1979 Yachiyo, Colour version of Space   */
/*                Stranger, board has Stranger 2 written on it              */
/****************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class _8080bw
{
	
	
	
	public static PaletteInitHandlerPtr palette_init_8080bw  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		palette_set_color(0,0x00,0x00,0x00); /* black */
		palette_set_color(1,0xff,0xff,0xff); /* white */
	} };
	
	
	public static Memory_ReadAddress c8080bw_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x2000, 0x3fff, MRA_RAM ),
		new Memory_ReadAddress( 0x4000, 0x63ff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress c8080bw_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0x2000, 0x3fff, c8080bw_videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x4000, 0x63ff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort c8080bw_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, input_port_0_r ),
		new IO_ReadPort( 0x01, 0x01, input_port_1_r ),
		new IO_ReadPort( 0x02, 0x02, input_port_2_r ),
		new IO_ReadPort( 0x03, 0x03, c8080bw_shift_data_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_0_3[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, c8080bw_shift_amount_w ),
		new IO_WritePort( 0x03, 0x03, c8080bw_shift_data_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_1_2[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x01, 0x01, c8080bw_shift_amount_w ),
		new IO_WritePort( 0x02, 0x02, c8080bw_shift_data_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_2_3[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x02, 0x02, c8080bw_shift_amount_w ),
		new IO_WritePort( 0x03, 0x03, c8080bw_shift_data_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_2_4[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x02, 0x02, c8080bw_shift_amount_w ),
		new IO_WritePort( 0x04, 0x04, c8080bw_shift_data_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_4_3[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x03, 0x03, c8080bw_shift_data_w ),
		new IO_WritePort( 0x04, 0x04, c8080bw_shift_amount_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static MachineHandlerPtr machine_driver_8080bw = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main",8080,2000000)        /* 2 MHz? */
		MDRV_CPU_MEMORY(c8080bw_readmem,c8080bw_writemem)
		MDRV_CPU_PORTS(c8080bw_readport,writeport_2_4)
		MDRV_CPU_VBLANK_INT(c8080bw_interrupt,2)    /* two interrupts per frame */
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 4*8, 32*8-1)
		MDRV_PALETTE_LENGTH(2)
		MDRV_PALETTE_INIT(8080bw)
		MDRV_VIDEO_START(generic_bitmapped)
		MDRV_VIDEO_UPDATE(8080bw)
	
		/* sound hardware */
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "Space Invaders"                             */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_invaders = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( invaders )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* must be ACTIVE_HIGH Super Invaders */
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_TILT );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x08, "1000" );
		PORT_DIPSETTING(    0x00, "1500" );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_DIPNAME( 0x80, 0x00, "Coin Info" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_invaders = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_MACHINE_INIT(invaders)
	
		/* video hardware */
		MDRV_VISIBLE_AREA(1*8, 31*8-1, 4*8, 32*8-1)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SAMPLES, invaders_samples_interface)
		MDRV_SOUND_ADD(SN76477, invaders_sn76477_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Space Invaders TV Version (Taito)                   */
	/*                                                     */
	/*LT 24-12-1998                                        */
	
	/*******************************************************/
	
	/* same as Invaders with a test mode switch */
	
	static InputPortHandlerPtr input_ports_sitv = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( sitv )
		PORT_START(); 		/* TEST MODE */
		PORT_SERVICE( 0x01, IP_ACTIVE_LOW );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_UNKNOWN );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_TILT );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x08, "1000" );
		PORT_DIPSETTING(    0x00, "1500" );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_DIPNAME( 0x80, 0x00, "Coin Info" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	
	/*******************************************************/
	/*                                                     */
	
	/* Midway "Space Invaders Part II"                     */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_invadpt2 = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( invadpt2 )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW,  IPT_UNKNOWN );/* otherwise high score entry ends right away */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_UNKNOWN );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_TILT );
		PORT_DIPNAME( 0x08, 0x00, "High Score Preset Mode" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_DIPNAME( 0x80, 0x00, "Coin Info" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	
	/* same as regular invaders, but with a color board added */
	
	public static MachineHandlerPtr machine_driver_invadpt2 = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(invaders)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(8)
		MDRV_PALETTE_INIT(invadpt2)
	MACHINE_DRIVER_END();
 }
};
	
	/*******************************************************/
	/*                                                     */
	/* Cosmo                                               */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_cosmo = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( cosmo )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_UNKNOWN );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "5" );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_UNKNOWN );/* must be HIGH normally or the joystick won't work */
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	public static Memory_ReadAddress cosmo_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x2000, 0x3fff, MRA_RAM ),
		new Memory_ReadAddress( 0x4000, 0x57ff, MRA_ROM ),
		new Memory_ReadAddress( 0x5c00, 0x5fff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress cosmo_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0x2000, 0x3fff, c8080bw_videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x4000, 0x57ff, MWA_ROM ),
		new Memory_WriteAddress( 0x5c00, 0x5fff, cosmo_colorram_w, colorram ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort cosmo_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, input_port_0_r ),
		new IO_ReadPort( 0x01, 0x01, input_port_1_r ),
		new IO_ReadPort( 0x02, 0x02, input_port_2_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	/* at least one of these IOWP_NOPs must be sound related */
	public static IO_WritePort cosmo_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, IOWP_NOP ),
		new IO_WritePort( 0x01, 0x01, IOWP_NOP ),
		new IO_WritePort( 0x02, 0x02, IOWP_NOP ),
		new IO_WritePort( 0x06, 0x06, watchdog_reset_w ),
		new IO_WritePort( 0x07, 0x07, IOWP_NOP ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
		
	public static MachineHandlerPtr machine_driver_cosmo = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
		/* basic machine hardware */
		MDRV_IMPORT_FROM(invaders)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(cosmo_readmem, cosmo_writemem)
		MDRV_CPU_PORTS(cosmo_readport, cosmo_writeport)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(8)
		MDRV_PALETTE_INIT(cosmo)
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* ?????? "Super Earth Invasion"                       */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_earthinv = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( earthinv )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_UNKNOWN );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPNAME( 0x02, 0x02, "Pence Coinage" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_COIN2 );/* Pence Coin */
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") ); /* Not bonus */
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
	
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, "2C/1C 50p/3C (+ Bonus Life"));
		PORT_DIPSETTING(    0x80, "1C/1C 50p/5C" );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	
	/*******************************************************/
	/*                                                     */
	/* ?????? "Space Attack II"                            */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_spaceatt = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( spaceatt )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_TILT );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x08, "1000" );
		PORT_DIPSETTING(    0x00, "1500" );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
	
		PORT_START(); 		/* Dummy port for cocktail mode (not used) */
		PORT_BIT( 0xff, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	
	/*******************************************************/
	/*                                                     */
	/* Zenitone Microsec "Invaders Revenge"                */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_invrvnge = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( invrvnge )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_UNKNOWN );
	
		PORT_START();       /* DSW0 */
	
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_TILT );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	
	public static MachineHandlerPtr machine_driver_invrvnge = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
	
		/* video hardware */
		MDRV_VISIBLE_AREA(1*8, 31*8-1, 4*8, 32*8-1)
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "Space Invaders II Cocktail"                 */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_invad2ct = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( invad2ct )
		PORT_START(); 
		PORT_SERVICE(0x01, IP_ACTIVE_LOW);			  /* dip 8 */
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* tied to pull-down */
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN ); /* tied to pull-up */
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* tied to pull-down */
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN ); /* tied to pull-up */
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN ); /* tied to pull-up */
	    PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN ); /* labelled reset but tied to pull-up */
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN ); /* tied to pull-up */
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* tied to pull-down */
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_UNKNOWN ); /* tied to pull-up */
	
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coinage") ); /* dips 4 & 3 */
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "2C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );  /* tied to pull-up */
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") ); /* dip 2 */
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Bonus_Life") ); /* dip 1 */
		PORT_DIPSETTING(    0x80, "1500" );
		PORT_DIPSETTING(    0x00, "2000" );
	
		PORT_START(); 		/* Dummy port for cocktail mode (not used) */
		PORT_BIT( 0xff, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_invad2ct = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_MACHINE_INIT(invad2ct)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SAMPLES, invad2ct_samples_interface)
		MDRV_SOUND_ADD(SN76477, invad2ct_sn76477_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Yachiro "Space Strangers"                           */
	/*                                                     */
	/*******************************************************/
	
	public static IO_ReadPort sstrangr_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x41, 0x41, input_port_2_r ),
		new IO_ReadPort( 0x42, 0x42, input_port_1_r ),
		new IO_ReadPort( 0x44, 0x44, input_port_4_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sstrangr_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		/* no shifter circuit */
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	static InputPortHandlerPtr input_ports_sstrangr = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( sstrangr )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_SERVICE( 0x08, IP_ACTIVE_HIGH );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x01, "Extra Play" );
		PORT_DIPSETTING(    0x00, "Never" );
		PORT_DIPSETTING(    0x01, "3000" );
		PORT_DIPSETTING(    0x02, "4000" );
		PORT_DIPSETTING(    0x03, "5000" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "4" );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x08, "1000" );
		PORT_DIPSETTING(    0x00, "2000" );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );/* Must be ACTIVE_LOW for game to boot */
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
	
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	
		PORT_START();       /* External switches */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_DIPNAME( 0x02, 0x00, "Player's Bullet Speed" );
		PORT_DIPSETTING(    0x00, "Slow" );
		PORT_BITX(0,  0x02, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Fast", IP_KEY_NONE, IP_JOY_NONE );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_sstrangr = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(sstrangr_readport,sstrangr_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,2)
		MDRV_MACHINE_INIT(sstrangr)
	
		/* video hardware */
		MDRV_VISIBLE_AREA(1*8, 31*8-1, 4*8, 32*8-1)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SAMPLES, invaders_samples_interface)
		MDRV_SOUND_ADD(SN76477, invaders_sn76477_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Yachiro "Space Strangers 2"                         */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_sstrngr2 = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( sstrngr2 )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_SERVICE( 0x08, IP_ACTIVE_HIGH );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x01, "Extra Play" );
		PORT_DIPSETTING(    0x00, "Never" );
		PORT_DIPSETTING(    0x01, "3000" );
		PORT_DIPSETTING(    0x02, "4000" );
		PORT_DIPSETTING(    0x03, "5000" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x04, "4" );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x08, "1000" );
		PORT_DIPSETTING(    0x00, "2000" );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR(Coinage);
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	
		PORT_START();       /* External switches */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_DIPNAME( 0x02, 0x00, "Player's Bullet Speed" );
		PORT_DIPSETTING(    0x00, "Slow" );
		PORT_BITX(0,  0x02, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Fast", IP_KEY_NONE, IP_JOY_NONE );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_sstrngr2 = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(sstrangr_readport,sstrangr_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,2)
		MDRV_MACHINE_INIT(sstrangr)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(8)
		MDRV_PALETTE_INIT(invadpt2)
		MDRV_VISIBLE_AREA(1*8, 31*8-1, 4*8, 32*8-1)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SAMPLES, invaders_samples_interface)
		MDRV_SOUND_ADD(SN76477, invaders_sn76477_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Taito "Space Laser"                                 */
	
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_spclaser = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( spclaser )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		/*PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );/ /*This is not 2 Player*/
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		/*PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
	    PORT_DIPNAME( 0x80, 0x00, DEF_STR(Coinage);
	    PORT_DIPSETTING(    0x00, "1 Coin/1 or 2 Players" );
	    PORT_DIPSETTING(    0x80, "1 Coin/1 Player  2 Coins/2 Players" );  Irrelevant, causes bugs*/
	
		PORT_START(); 		/* Dummy port for cocktail mode (not used) */
		PORT_BIT( 0xff, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	
	/*******************************************************/
	/*                                                     */
	/* Space War Part 3                                    */
	/*                                                     */
	/* Added 21/11/1999 By LT                              */
	/* Thanks to Peter Fyfe for machine info               */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_spacewr3 = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( spacewr3 )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_UNKNOWN );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x08, "1000" );
		PORT_DIPSETTING(    0x00, "1500" );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_DIPNAME( 0x80, 0x00, "Coin Info" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	
	/*******************************************************/
	/*                                                     */
	/* Taito "Galaxy Wars"                                 */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_galxwars = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( galxwars )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );/* must be IP_ACTIVE_LOW for Universal Sets */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_UNKNOWN );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x01, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x02, "4" );
		PORT_DIPSETTING(    0x03, "5" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "3000" );
		PORT_DIPSETTING(    0x08, "5000" );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY| IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY| IPF_PLAYER2 );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	
	/*******************************************************/
	/*                                                     */
	/* Taito "Lunar Rescue"                                */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_lrescue = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( lrescue )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_UNKNOWN );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
	
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_TILT );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	
	/*******************************************************/
	/*                                                     */
	/* Universal "Cosmic Monsters"                         */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_cosmicmo = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( cosmicmo )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_UNKNOWN );
	
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x01, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x02, "4" );
		PORT_DIPSETTING(    0x03, "5" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	
	
	/*******************************************************/
	/*                                                     */
	/* Nichibutsu "Rolling Crash"                          */
	/*                                                     */
	/*******************************************************/
	
	public static Memory_ReadAddress rollingc_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x2000, 0x3fff, MRA_RAM ),
	//  new Memory_ReadAddress( 0x2000, 0x2002, MRA_RAM ),
	//  new Memory_ReadAddress( 0x2003, 0x2003, hack ),
		new Memory_ReadAddress( 0x4000, 0x5fff, MRA_ROM ),
		new Memory_ReadAddress( 0xa000, 0xbfff, schaser_colorram_r ),
		new Memory_ReadAddress( 0xe400, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress rollingc_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0x2000, 0x3fff, c8080bw_videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x4000, 0x5fff, MWA_ROM ),
		new Memory_WriteAddress( 0xa000, 0xbfff, schaser_colorram_w, colorram ),
		new Memory_WriteAddress( 0xe400, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	static InputPortHandlerPtr input_ports_rollingc = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( rollingc )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT );/* Game Select */
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT );/* Game Select */
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_TILT );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_DIPNAME( 0x80, 0x00, "Coin Info" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_rollingc = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(rollingc_readmem,rollingc_writemem)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(8)
		MDRV_PALETTE_INIT(invadpt2)
	
		/* sound hardware */
	MACHINE_DRIVER_END();
 }
};
	
	
	
	/*********************************************************/
	/*                                                       */
	/* Nintendo "Sheriff"                                    */
	/*                                                       */
	/* The only difference between Sheriff and Bandido,      */
	/* beside the copyright notice is the adjustable coinage */
	/* in Bandido.											 */
	/*                                                       */
	/*********************************************************/
	
	public static Memory_ReadAddress sheriff_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x27ff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x7fff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sheriff_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x27ff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x5fff, c8080bw_videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x6000, 0x7fff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort sheriff_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, input_port_0_r ),
		new IO_ReadPort( 0x01, 0x01, input_port_1_r ),
		new IO_ReadPort( 0x02, 0x02, input_port_2_r ),
		new IO_ReadPort( 0x03, 0x03, c8080bw_shift_data_r ),
		new IO_ReadPort( 0x04, 0x04, input_port_3_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress sheriff_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x03ff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	public static Memory_WriteAddress sheriff_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x03ff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort sheriff_sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( I8039_p1, I8039_p1, sheriff_sh_p1_r ),
		new IO_ReadPort( I8039_p2, I8039_p2, sheriff_sh_p2_r ),
		new IO_ReadPort( I8039_t0, I8039_t0, sheriff_sh_t0_r ),
		new IO_ReadPort( I8039_t1, I8039_t1, sheriff_sh_t1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sheriff_sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( I8039_p2, I8039_p2, sheriff_sh_p2_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	/* All of the controls/dips for cocktail mode are as per the schematic */
	/* BUT a coffee table version was never manufactured and support was   */
	/* probably never completed.                                           */
	/* e.g. cocktail players button will give 6 credits!                   */
	
	static InputPortHandlerPtr input_ports_sheriff = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( sheriff )
		PORT_START();       /* 00 Main Controls */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_RIGHT  | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_LEFT   | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_UP     | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_DOWN   | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_RIGHT | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_LEFT  | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_UP    | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_DOWN  | IPF_8WAY );
	
		PORT_START();       /* 01 Player 2 Controls */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_RIGHT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_LEFT   | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_UP     | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_DOWN   | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_DOWN  | IPF_8WAY | IPF_COCKTAIL );
	
		PORT_START();       /* 02 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );          /* Marked for   */
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );          /* Expansion    */
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );          /* on Schematic */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_COIN1 );
	
		PORT_START();       /* 04 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
	
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_bandido = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( bandido )
		PORT_START();       /* 00 Main Controls */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_RIGHT  | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_LEFT   | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_UP     | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_DOWN   | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_RIGHT | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_LEFT  | IPF_8WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_UP    | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_DOWN  | IPF_8WAY );
	
		PORT_START();       /* 01 Player 2 Controls */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_RIGHT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_LEFT   | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_UP     | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_DOWN   | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_DOWN  | IPF_8WAY | IPF_COCKTAIL );
	
		PORT_START();       /* 02 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );          /* Marked for   */
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );          /* Expansion    */
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );          /* on Schematic */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_COIN1 );
	
		PORT_START();       /* 04 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_sheriff = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_REPLACE("main",8080,20160000/8)        /* 2.52 MHz */
		MDRV_CPU_MEMORY(sheriff_readmem,sheriff_writemem)
		MDRV_CPU_PORTS(sheriff_readport,writeport_2_3)
	
		MDRV_CPU_ADD(I8035,6000000/15)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
	
		MDRV_CPU_MEMORY(sheriff_sound_readmem,sheriff_sound_writemem)
		MDRV_CPU_PORTS(sheriff_sound_readport,sheriff_sound_writeport)
	
		MDRV_MACHINE_INIT(sheriff)
	
		/* video hardware */
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
	
		/* sound hardware */
		MDRV_SOUND_ADD(DAC, sheriff_dac_interface)
		MDRV_SOUND_ADD(SN76477, sheriff_sn76477_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "Space Encounters"                           */
	/*                                                     */
	/*******************************************************/
	
	public static IO_ReadPort spcenctr_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, spcenctr_port_0_r ), /* These 2 ports use Gray's binary encoding */
		new IO_ReadPort( 0x01, 0x01, spcenctr_port_1_r ),
		new IO_ReadPort( 0x02, 0x02, input_port_2_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	
	static InputPortHandlerPtr input_ports_spcenctr = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( spcenctr )
		PORT_START();       /* IN0 */
		PORT_ANALOG( 0x3f, 0x1f, IPT_AD_STICK_X | IPF_REVERSE, 10, 10, 0, 0x3f);/* 6 bit horiz encoder - Gray's binary */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 );   /* fire */
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START();       /* IN1 */
		PORT_ANALOG( 0x3f, 0x1f, IPT_AD_STICK_Y, 10, 10, 0, 0x3f);/* 6 bit vert encoder - Gray's binary */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START();       /* IN2 Dips & Coins */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "2000 4000 8000" );
		PORT_DIPSETTING(    0x01, "3000 6000 12000" );
		PORT_DIPSETTING(    0x02, "4000 8000 16000" );
		PORT_DIPSETTING(    0x03, "5000 10000 20000" );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x30, 0x00, "Bonus/Test Mode" );
		PORT_DIPSETTING(    0x00, "Bonus On" );
		PORT_DIPSETTING(    0x30, "Bonus Off" );
	
		PORT_DIPSETTING(    0x20, "Cross Hatch" );
		PORT_DIPSETTING(    0x10, "Test Mode" );
		PORT_DIPNAME( 0xc0, 0x00, "Time" );
		PORT_DIPSETTING(    0x00, "45" );
		PORT_DIPSETTING(    0x40, "60" );
		PORT_DIPSETTING(    0x80, "75" );
		PORT_DIPSETTING(    0xc0, "90" );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_spcenctr = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(spcenctr_readport,0)
	
		/* sound hardware */
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "Gun Fight"                                  */
	/*                                                     */
	/*******************************************************/
	
	public static IO_ReadPort gunfight_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, input_port_0_r ),
		new IO_ReadPort( 0x01, 0x01, input_port_1_r ),
		new IO_ReadPort( 0x02, 0x02, input_port_2_r ),
		new IO_ReadPort( 0x03, 0x03, boothill_shift_data_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	static InputPortHandlerPtr input_ports_gunfight = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( gunfight )
	    /* Gun position uses bits 4-6, handled using fake paddles */
		PORT_START();       /* IN0 - Player 2 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );       /* Move Man */
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );             /* Fire */
	
		PORT_START();       /* IN1 - Player 1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );             /* Move Man */
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_BUTTON1 );                   /* Fire */
	
	#ifdef NOTDEF
		PORT_START();       /* IN2 Dips & Coins */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START1 );
		PORT_DIPNAME( 0x0C, 0x00, "Plays" );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x04, "2" );
		PORT_DIPSETTING(    0x08, "3" );
		PORT_DIPSETTING(    0x0C, "4" );
		PORT_DIPNAME( 0x30, 0x00, "Time" );/* These are correct */
		PORT_DIPSETTING(    0x00, "60" );
		PORT_DIPSETTING(    0x10, "70" );
		PORT_DIPSETTING(    0x20, "80" );
		PORT_DIPSETTING(    0x30, "90" );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, "1 Coin/1 Player" );
		PORT_DIPSETTING(    0x40, "1 Coin/2 Players" );
		PORT_DIPSETTING(    0x80, "1 Coin/3 Players" );
		PORT_DIPSETTING(    0xc0, "1 Coin/4 Players" );
	#endif
	
		PORT_START();       /* IN2 Dips & Coins */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, "1 Coin" );
		PORT_DIPSETTING(    0x01, "2 Coins" );
		PORT_DIPSETTING(    0x02, "3 Coins" );
		PORT_DIPSETTING(    0x03, "4 Coins" );
		PORT_DIPNAME( 0x0C, 0x00, "Plays" );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x04, "2" );
		PORT_DIPSETTING(    0x08, "3" );
		PORT_DIPSETTING(    0x0C, "4" );
		PORT_DIPNAME( 0x30, 0x00, "Time" );/* These are correct */
		PORT_DIPSETTING(    0x00, "60" );
		PORT_DIPSETTING(    0x10, "70" );
		PORT_DIPSETTING(    0x20, "80" );
		PORT_DIPSETTING(    0x30, "90" );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_START1 );
	
		PORT_START();                                                                                           /* Player 2 Gun */
		PORT_ANALOGX( 0xff, 0x00, IPT_PADDLE | IPF_PLAYER2, 50, 10, 1, 255, KEYCODE_H, KEYCODE_Y, IP_JOY_NONE, IP_JOY_NONE );
	
		PORT_START();                                                                                           /* Player 1 Gun */
		PORT_ANALOGX( 0xff, 0x00, IPT_PADDLE, 50, 10, 1, 255, KEYCODE_Z, KEYCODE_A, IP_JOY_NONE, IP_JOY_NONE );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_gunfight = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(gunfight_readport,writeport_2_4)
		MDRV_MACHINE_INIT(gunfight)
	
		/* sound hardware */
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "M-4"                                        */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_m4 = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( m4 )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_LOW,  IPT_JOYSTICK_UP   | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW,  IPT_UNUSED );
		PORT_BIT( 0x08, IP_ACTIVE_LOW,  IPT_JOYSTICK_DOWN | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW,  IPT_BUTTON1 | IPF_PLAYER2 );/* left trigger */
		PORT_BIT( 0x20, IP_ACTIVE_LOW,  IPT_BUTTON2 | IPF_PLAYER2 );/* left reload */
		PORT_BIT( 0x40, IP_ACTIVE_LOW,  IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_UNUSED );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW,  IPT_JOYSTICK_UP   | IPF_2WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW,  IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW,  IPT_JOYSTICK_DOWN | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW,  IPT_BUTTON1 );/* right trigger */
		PORT_BIT( 0x20, IP_ACTIVE_LOW,  IPT_BUTTON2 );/* right reload */
		PORT_BIT( 0x40, IP_ACTIVE_LOW,  IPT_START2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_UNUSED );
	
		PORT_START();       /* IN2 Dips & Coins */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "2C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x0c, 0x0c, "Time" );
		PORT_DIPSETTING(    0x00, "60" );
		PORT_DIPSETTING(    0x04, "70" );
		PORT_DIPSETTING(    0x08, "80" );
		PORT_DIPSETTING(    0x0C, "90" );
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_m4 = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(gunfight_readport,writeport_1_2)
	
		/* sound hardware */
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "Boot Hill"                                  */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_boothill = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( boothill )
	    /* Gun position uses bits 4-6, handled using fake paddles */
		PORT_START();       /* IN0 - Player 2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );       /* Move Man */
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );/* Fire */
	
		PORT_START();       /* IN1 - Player 1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );/* Move Man */
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 );/* Fire */
	
		PORT_START();       /* IN2 Dips & Coins */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
	//	PORT_DIPSETTING(    0x03, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x0c, 0x00, "Time" );
		PORT_DIPSETTING(    0x00, "64" );
		PORT_DIPSETTING(    0x04, "74" );
		PORT_DIPSETTING(    0x08, "84" );
		PORT_DIPSETTING(    0x0C, "94" );
		PORT_SERVICE( 0x10, IP_ACTIVE_HIGH );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START2 );
	
		PORT_START();                                                                                           /* Player 2 Gun */
		PORT_ANALOGX( 0xff, 0x00, IPT_PADDLE | IPF_PLAYER2, 50, 10, 1, 255, KEYCODE_X, KEYCODE_S, IP_JOY_NONE, IP_JOY_NONE );
	
		PORT_START();                                                                                           /* Player 1 Gun */
		PORT_ANALOGX( 0xff, 0x00, IPT_PADDLE, 50, 10, 1, 255, KEYCODE_Z, KEYCODE_A, IP_JOY_NONE, IP_JOY_NONE );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_boothill = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(gunfight_readport,writeport_1_2)
		MDRV_MACHINE_INIT(boothill)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SAMPLES, boothill_samples_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Taito "Space Chaser"                                */
	/*                                                     */
	/*******************************************************/
	
	public static Memory_ReadAddress schaser_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x2000, 0x3fff, MRA_RAM ),
		new Memory_ReadAddress( 0x4000, 0x5fff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xdfff, schaser_colorram_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress schaser_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0x2000, 0x3fff, c8080bw_videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x4000, 0x5fff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xdfff, schaser_colorram_w, colorram ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	static InputPortHandlerPtr input_ports_schaser = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( schaser )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
	
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_COIN1 );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Easy" );
		PORT_DIPSETTING(    0x08, "Hard" );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_TILT );
		PORT_DIPNAME( 0x40, 0x00, "Number of Controllers" );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x40, "2" );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_schaser = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_REPLACE("main",8080,1996800)        /* 19.968MHz / 10 */
		MDRV_CPU_MEMORY(schaser_readmem,schaser_writemem)
		MDRV_MACHINE_INIT(schaser)
	
	
		/* video hardware */
	
		MDRV_PALETTE_LENGTH(8)
		MDRV_PALETTE_INIT(invadpt2)
		MDRV_VISIBLE_AREA(0*8, 31*8-1, 4*8, 32*8-1)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SN76477, schaser_sn76477_interface)
		MDRV_SOUND_ADD(DAC, schaser_dac_interface)
		MDRV_SOUND_ADD(CUSTOM, schaser_custom_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Taito "Space Chaser" (CV version)                   */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_schasrcv = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( schasrcv )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_PLAYER2 );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Easy" );
		PORT_DIPSETTING(    0x08, "Hard" );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
	
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_PLAYER2 );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	/*******************************************************/
	/*                                                     */
	/* Taito "Staright Flush"                              */
	/*                                                     */
	/*******************************************************/
	
	static int sfl_int=0;
	
	public static ReadHandlerPtr sfl_input_r  = new ReadHandlerPtr() { public int handler(int offset){
		sfl_int^=0x80;//vblank flag ?
		return sfl_int|input_port_1_r.handler(0);
	} };
	
	public static Memory_ReadAddress sflush_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_RAM), //?
		new Memory_ReadAddress( 0x4000, 0x5fff, MRA_RAM),
		new Memory_ReadAddress( 0xa000, 0xbfff, schaser_colorram_r),
		new Memory_ReadAddress( 0x8008, 0x8008, input_port_2_r),
		new Memory_ReadAddress( 0x8009, 0x8009, c8080bw_shift_data_r ),
		new Memory_ReadAddress( 0x800a, 0x800a, sfl_input_r ),
		new Memory_ReadAddress( 0x800b, 0x800b, input_port_0_r ),
		new Memory_ReadAddress( 0xd800, 0xffff, MRA_ROM ),
	
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sflush_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress(0x0000, 0x1fff, MWA_RAM),
		new Memory_WriteAddress(0x4000, 0x5fff, c8080bw_videoram_w, videoram, videoram_size),
		new Memory_WriteAddress(0x8018, 0x8018, c8080bw_shift_data_w ),
		new Memory_WriteAddress(0x8019, 0x8019, c8080bw_shift_amount_w ),
		new Memory_WriteAddress(0x801a, 0x801a, MWA_NOP ),
		new Memory_WriteAddress(0x801c, 0x801c, MWA_NOP ),
		new Memory_WriteAddress(0x801d, 0x801d, MWA_NOP ),
		new Memory_WriteAddress(0xa000, 0xbfff, schaser_colorram_w, colorram),
		new Memory_WriteAddress(0xd800, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static MachineHandlerPtr machine_driver_sflush = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_REPLACE("main",M6800,2000000)        /* ?? */
		MDRV_CPU_MEMORY(sflush_readmem,sflush_writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_pulse,2)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(8)
		MDRV_PALETTE_INIT(sflush)
		MDRV_VISIBLE_AREA(0*8, 31*8-1, 4*8, 30*8-1)
	
	MACHINE_DRIVER_END();
 }
};
	
	static InputPortHandlerPtr input_ports_sflush = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( sflush )
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_TILT  );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
	
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x08, 0x00, "Hiscore" );
		PORT_DIPSETTING(    0x00, "0" );
		PORT_DIPSETTING(    0x08, "30 000" );
		PORT_DIPNAME( 0x40, 0x00, "Coinage Display" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_ANALOG( 0xff, 0x6a, IPT_PADDLE, 30, 30, 0x16,0xbf);
	INPUT_PORTS_END(); }}; 
	
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "Clowns"                                     */
	/*                                                     */
	/*******************************************************/
	
	/*
	 * Clowns (EPROM version)
	 */
	static InputPortHandlerPtr input_ports_clowns = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( clowns )
		PORT_START();       /* IN0 */
		PORT_ANALOG( 0xff, 0x7f, IPT_PADDLE, 100, 10, 0x01, 0xfe);
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();       /* IN2 Dips & Coins */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x03, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, "Balloon Resets" );
		PORT_DIPSETTING(    0x00, "Each row" );
		PORT_DIPSETTING(    0x10, "All rows" );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "3000" );
		PORT_DIPSETTING(    0x20, "4000" );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x40, "4" );
		PORT_SERVICE( 0x80, IP_ACTIVE_HIGH );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortHandlerPtr input_ports_clowns1 = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( clowns1 )
		PORT_START();       /* IN0 */
		PORT_ANALOG( 0xff, 0x7f, IPT_PADDLE, 100, 10, 0x01, 0xfe);
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();       /* IN2 Dips & Coins */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x03, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, "Balloon Resets" );
		PORT_DIPSETTING(    0x00, "Each row" );
		PORT_DIPSETTING(    0x10, "All rows" );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "3000" );
		PORT_DIPSETTING(    0x20, "4000" );
		PORT_DIPNAME( 0x40, 0x00, "Lives? (Possible ROM bug"));
		PORT_DIPSETTING(    0x00, "2 Lives" );
		PORT_DIPSETTING(    0x40, "Input Test ?" );
		PORT_SERVICE( 0x80, IP_ACTIVE_HIGH );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_clowns = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(c8080bw_readport,writeport_1_2)
		MDRV_MACHINE_INIT(clowns)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SAMPLES, circus_samples_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "Guided Missile"                             */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_gmissile = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( gmissile )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 );
	
		PORT_START();       /* IN2 Dips & Coins */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_2C") );
	
		PORT_DIPNAME( 0x0c, 0x0c, "Time" );
		PORT_DIPSETTING(    0x00, "60" );
		PORT_DIPSETTING(    0x08, "70" );
		PORT_DIPSETTING(    0x04, "80" );
		PORT_DIPSETTING(    0x0c, "90" );
		PORT_DIPNAME( 0x30, 0x00, "Extra Play" );
		PORT_DIPSETTING(    0x00, "500" );
		PORT_DIPSETTING(    0x20, "700" );
		PORT_DIPSETTING(    0x10, "1000" );
		PORT_DIPSETTING(    0x30, "None" );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	INPUT_PORTS_END(); }}; 
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "280 ZZZAP"                                  */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_280zzzap = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( 280zzzap )
		PORT_START();       /* IN0 */
		PORT_ANALOG( 0x0f, 0x00, IPT_PEDAL, 100, 64, 0x00, 0x0f );/* accelerator */
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_TOGGLE ); /* shift */
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_START1 );
	
		PORT_START();       /* IN1 - Steering Wheel */
		PORT_ANALOG( 0xff, 0x7f, IPT_PADDLE | IPF_REVERSE, 100, 10, 0x01, 0xfe);
	
		PORT_START();       /* IN2 Dips & Coins */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x0c, 0x00, "Time" );
		PORT_DIPSETTING(    0x0c, "60" );
		PORT_DIPSETTING(    0x00, "80" );
		PORT_DIPSETTING(    0x08, "99" );
		PORT_DIPSETTING(    0x04, "Test Mode" );
		PORT_DIPNAME( 0x30, 0x00, "Extended Time" );
		PORT_DIPSETTING(    0x00, "Score >= 2.5" );
		PORT_DIPSETTING(    0x10, "Score >= 2" );
		PORT_DIPSETTING(    0x20, "None" );
	/* 0x30 same as 0x20 */
		PORT_DIPNAME( 0xc0, 0x00, "Language");
		PORT_DIPSETTING(    0x00, "English" );
		PORT_DIPSETTING(    0x40, "German" );
		PORT_DIPSETTING(    0x80, "French" );
		PORT_DIPSETTING(    0xc0, "Spanish" );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_280zzzap = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(c8080bw_readport,writeport_4_3)
	
		/* sound hardware */
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Taito "Lupin III"                                   */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_lupin3 = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( lupin3 )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* selects color mode (dynamic vs. static) */
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* something has to do with sound */
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_TILT );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL);
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_4WAY );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x08, 0x00, "Bags to Collect" );
		PORT_DIPSETTING(    0x08, "2" );
		PORT_DIPSETTING(    0x00, "8" );
		PORT_DIPNAME( 0x10, 0x00, "Language" );
		PORT_DIPSETTING(    0x00, "English" );
		PORT_DIPSETTING(    0x10, "Japanese" );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH,  IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH,  IPT_UNUSED );
		PORT_BITX(0x80,     0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_lupin3 = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(schaser_readmem,schaser_writemem)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(8)
		MDRV_PALETTE_INIT(invadpt2)
		MDRV_VISIBLE_AREA(1*8, 31*8-1, 4*8, 32*8-1)
	
		/* sound hardware */
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Nintendo "Heli Fire"                                */
	/*                                                     */
	/*******************************************************/
	
	public static Memory_ReadAddress helifire_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x27ff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x7fff, MRA_RAM ),
		new Memory_ReadAddress( 0xc000, 0xddff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress helifire_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x27ff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x5fff, c8080bw_videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x6000, 0x7fff, MWA_RAM ),
		new Memory_WriteAddress( 0xc000, 0xdfff, helifire_colorram_w, colorram ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress helifire_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x03ff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress helifire_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x03ff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort helifire_sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( I8039_p1, I8039_p1, helifire_sh_p1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort helifire_sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( I8039_p1, I8039_p1, helifire_sh_p1_w ), /* DAC data */
		new IO_WritePort( I8039_p2, I8039_p2, helifire_sh_p2_w ), /* bit7: DAC vref control, other bits: analog sounds */
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	static InputPortHandlerPtr input_ports_helifire = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( helifire )
		PORT_START();       /* 00 Main Controls */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY  );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* 01 Player 2 Controls */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* Start and Coin Buttons */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );/* Marked for   */
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );/* Expansion    */
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );/* on Schematic */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_COIN1 );
	
		PORT_START();       /* DSW */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Bonus_Life") );
	
		PORT_DIPSETTING(    0x0c, "5000" );
		PORT_DIPSETTING(    0x04, "6000" );
		PORT_DIPSETTING(    0x08, "8000" );
		PORT_DIPSETTING(    0x00, "10000" );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x10, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
	
		/* potentiometers */
		PORT_START(); 	/* 04 */ /* VR1 sun glow brightness */
		PORT_DIPNAME( 0x7f, 0x50, "VR1 sun glow brightness" );
		PORT_DIPSETTING(    0x00, "00" );
		PORT_DIPSETTING(    0x10, "10" );
		PORT_DIPSETTING(    0x20, "20" );
		PORT_DIPSETTING(    0x30, "30" );
		PORT_DIPSETTING(    0x40, "40" );
		PORT_DIPSETTING(    0x50, "50" );
		PORT_DIPSETTING(    0x60, "60" );
		PORT_DIPSETTING(    0x70, "70" );
		PORT_DIPSETTING(    0x7f, "7f" );
	
		PORT_START(); 	/* 05 */ /* VR2 sea brightness */
		PORT_DIPNAME( 0x7f, 0x00, "VR2 sea brightness" );
		PORT_DIPSETTING(    0x00, "00" );
		PORT_DIPSETTING(    0x10, "10" );
		PORT_DIPSETTING(    0x20, "20" );
		PORT_DIPSETTING(    0x30, "30" );
		PORT_DIPSETTING(    0x40, "40" );
		PORT_DIPSETTING(    0x50, "50" );
		PORT_DIPSETTING(    0x60, "60" );
		PORT_DIPSETTING(    0x70, "70" );
		PORT_DIPSETTING(    0x7f, "7f" );
	
		PORT_START(); 	/* 06 */ /* VR3 height of the sea (surface level) */
		PORT_DIPNAME( 0x0f, 0x04, "VR3 height of the sea" );
		PORT_DIPSETTING(    0x00, "00" );
		PORT_DIPSETTING(    0x01, "01" );
		PORT_DIPSETTING(    0x02, "02" );
		PORT_DIPSETTING(    0x03, "03" );
		PORT_DIPSETTING(    0x04, "04" );
		PORT_DIPSETTING(    0x05, "05" );
		PORT_DIPSETTING(    0x06, "06" );
		PORT_DIPSETTING(    0x07, "07" );
		PORT_DIPSETTING(    0x08, "08" );
		PORT_DIPSETTING(    0x09, "09" );
		PORT_DIPSETTING(    0x0a, "10" );
		PORT_DIPSETTING(    0x0b, "11" );
		PORT_DIPSETTING(    0x0c, "12" );
		PORT_DIPSETTING(    0x0d, "13" );
		PORT_DIPSETTING(    0x0e, "14" );
		PORT_DIPSETTING(    0x0f, "15" );
	
		PORT_START(); 	/* VR4 height of the waves */
		PORT_DIPNAME( 0x07, 0x04, "VR4 height of the waves" );
		PORT_DIPSETTING(    0x00, "00" );
		PORT_DIPSETTING(    0x01, "01" );
		PORT_DIPSETTING(    0x02, "02" );
		PORT_DIPSETTING(    0x03, "03" );
		PORT_DIPSETTING(    0x04, "04" );
		PORT_DIPSETTING(    0x05, "05" );
		PORT_DIPSETTING(    0x06, "06" );
		PORT_DIPSETTING(    0x07, "07" );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_helifire = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_REPLACE("main",8080,20160000/8)        /* 2.52 MHz */
		MDRV_CPU_MEMORY(helifire_readmem,helifire_writemem)
		MDRV_CPU_PORTS(sheriff_readport,writeport_2_3)
		MDRV_MACHINE_INIT(helifire)
	
		MDRV_CPU_ADD(I8035,6000000/15)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(helifire_sound_readmem,helifire_sound_writemem)
		MDRV_CPU_PORTS(helifire_sound_readport,helifire_sound_writeport)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(8+4*256) /* 8 standard, 2*256 for shades of blue and red without the green star, 2*256 for the shades of blue and red with the green star - used for analog background emulation */
		MDRV_PALETTE_INIT(helifire)
		MDRV_VISIBLE_AREA(1*8, 32*8-1, 2*8, 30*8-1)	/* VB lasts for 32 lines: from line 0xf0 to 0xff,0x00 to 0x0f */
		MDRV_VIDEO_EOF (helifire)
	
		/* sound hardware */
		MDRV_SOUND_ADD(DAC, sheriff_dac_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Nintendo "Space Fever (Color)"                      */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_spacefev = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( spacefev )
		PORT_START();       /* 00 Main Controls */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_COIN1 );
	
		PORT_START();       /* 01 Player 2 Controls */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_BITX(0x08, 0x00, 0, "Start Game A", KEYCODE_Q, IP_JOY_NONE );
		PORT_BITX(0x10, 0x00, 0, "Start Game B", KEYCODE_W, IP_JOY_NONE );
		PORT_BITX(0x20, 0x00, 0, "Start Game C", KEYCODE_E, IP_JOY_NONE );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );/* If on low the game doesn't start */
	
		PORT_START();       /* DSW */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
	//	PORT_DIPNAME( 0xfc, 0x00, DEF_STR( "Unknown") );
	//	PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	//	PORT_DIPSETTING(    0xfc, DEF_STR( "Off") );
	INPUT_PORTS_END(); }}; 
	
	
	/*******************************************************/
	/*                                                     */
	/* Taito "Polaris"                                     */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_polaris = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( polaris )
	
		PORT_START();       /* IN0 */
		PORT_DIPNAME( 0x01, 0x00, "Not Used" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x00, "Not Used" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_TILT );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		/* 0x04 should be Cabinet - Upright/Cocktail,
		   but until the coctail hack is changed,
		   this will have to do. */
		PORT_DIPNAME( 0x04, 0x00, "Number of Controls" );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x04, "2" );
		PORT_DIPNAME( 0x08, 0x00, "Invincible Test" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		/* The Demo Sounds dip switch does function.
		 * It allows the sonar sounds to play in demo mode. */
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, "Not Used" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, "Not Used" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, "High Score Preset Mode" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_polaris = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_REPLACE("main",8080,1996800)        /* 19.968MHz / 10 */
		MDRV_CPU_MEMORY(schaser_readmem,schaser_writemem)
	
		MDRV_CPU_PORTS(c8080bw_readport,writeport_0_3)
		MDRV_CPU_VBLANK_INT(polaris_interrupt,2)
		MDRV_MACHINE_INIT(polaris)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(8)
		MDRV_PALETTE_INIT(invadpt2)
		MDRV_VISIBLE_AREA(1*8, 31*8-1, 4*8, 32*8-1)
	
		/* sound hardware */
		MDRV_SOUND_ADD_TAG("disc", DISCRETE, polaris_sound_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "Laguna Racer"                               */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_lagunar = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( lagunar )
		PORT_START();       /* IN0 */
		PORT_ANALOG( 0x0f, 0x00, IPT_PEDAL, 100, 64, 0x00, 0x0f );/* accelerator */
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_TOGGLE ); /* shift */
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_START1 );
	
		PORT_START();       /* IN1 - Steering Wheel */
		PORT_ANALOG( 0xff, 0x7f, IPT_PADDLE | IPF_REVERSE, 100, 10, 0x01, 0xfe);
	
		PORT_START();       /* IN2 Dips & Coins */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x0c, 0x0c, "Time" );
		PORT_DIPSETTING(    0x00, "45" );
		PORT_DIPSETTING(    0x04, "60" );
		PORT_DIPSETTING(    0x08, "75" );
		PORT_DIPSETTING(    0x0c, "90" );
		PORT_DIPNAME( 0x30, 0x00, "Extended Time" );
		PORT_DIPSETTING(    0x00, "350" );
		PORT_DIPSETTING(    0x10, "400" );
		PORT_DIPSETTING(    0x20, "450" );
		PORT_DIPSETTING(    0x30, "500" );
		PORT_DIPNAME( 0xc0, 0x00, "Test Modes");
		PORT_DIPSETTING(    0x00, "Play Mode" );
		PORT_DIPSETTING(    0x40, "RAM/ROM" );
		PORT_DIPSETTING(    0x80, "Steering" );
		PORT_DIPSETTING(    0xc0, "No Extended Play" );
	INPUT_PORTS_END(); }}; 
	
	
	
	/*******************************************************/
	
	/*                                                     */
	/* Midway "Phantom II"                                 */
	/*                                                     */
	/*******************************************************/
	
	public static PaletteInitHandlerPtr palette_init_phantom2  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		palette_set_color(0,0x00,0x00,0x00); /* black */
		palette_set_color(1,0xff,0xff,0xff); /* white */
		palette_set_color(2,0xc0,0xc0,0xc0); /* grey */
	} };
	
	
	static InputPortHandlerPtr input_ports_phantom2 = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( phantom2 )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START();       /* IN2 Dips & Coins */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x06, 0x06, "Time" );
		PORT_DIPSETTING(    0x00, "45sec 20sec 20" );
		PORT_DIPSETTING(    0x02, "60sec 25sec 25" );
		PORT_DIPSETTING(    0x04, "75sec 30sec 30" );
		PORT_DIPSETTING(    0x06, "90sec 35sec 35" );
		PORT_SERVICE( 0x20, IP_ACTIVE_LOW );
	
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_phantom2 = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(gunfight_readport,writeport_1_2)
		MDRV_CPU_VBLANK_INT(phantom2_interrupt,2)
		MDRV_MACHINE_INIT(phantom2)
	
		/* video hardware */
		MDRV_VISIBLE_AREA(1*8, 31*8-1, 4*8, 32*8-1)
	
		MDRV_PALETTE_LENGTH(3)
		MDRV_PALETTE_INIT(phantom2)
	
		/* sound hardware */
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "Dog Patch"                                  */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_dogpatch = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( dogpatch )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START2 );
		PORT_ANALOG( 0x38, 0x1f, IPT_AD_STICK_X |IPF_PLAYER2, 25, 10, 0x05, 0x48);
		/* 6 bit horiz encoder - Gray's binary? */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON2 );
	
		PORT_START();       /* IN1 */
		PORT_ANALOG( 0x3f, 0x1f, IPT_AD_STICK_X, 25, 10, 0x01, 0x3e);/* 6 bit horiz encoder - Gray's binary? */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 );
	
		PORT_START();       /* IN2 Dips & Coins */
		PORT_DIPNAME( 0x03, 0x00, "# Cans" );
		PORT_DIPSETTING(    0x03, "10" );
		PORT_DIPSETTING(    0x02, "15" );
		PORT_DIPSETTING(    0x01, "20" );
		PORT_DIPSETTING(    0x00, "25" );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "2C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x10, 0x00, "Extended Play" );
		PORT_DIPSETTING(    0x10, "3 extra cans" );
		PORT_DIPSETTING(    0x00, "5 extra cans" );
		PORT_SERVICE( 0x20, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0xc0, 0x00, "Extended Play" );
		PORT_DIPSETTING(    0xc0, "150 Pts" );
		PORT_DIPSETTING(    0x80, "175 Pts" );
		PORT_DIPSETTING(    0x40, "225 Pts" );
		PORT_DIPSETTING(    0x00, "275 Pts" );
	INPUT_PORTS_END(); }}; 
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "4 Player Bowling"                           */
	
	/*                                                     */
	/*******************************************************/
	
	public static IO_ReadPort bowler_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x01, 0x01, c8080bw_shift_data_comp_r ),
		new IO_ReadPort( 0x02, 0x02, input_port_0_r ),				/* dip switch */
		new IO_ReadPort( 0x04, 0x04, input_port_1_r ),				/* coins / switches */
		new IO_ReadPort( 0x05, 0x05, input_port_2_r ),				/* ball vert */
		new IO_ReadPort( 0x06, 0x06, input_port_3_r ),				/* ball horz */
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	static InputPortHandlerPtr input_ports_bowler = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( bowler )
		PORT_START();       /* IN2 */
		PORT_DIPNAME( 0x03, 0x00, "Language" );
		PORT_DIPSETTING(    0x00, "English" );
		PORT_DIPSETTING(    0x01, "French" );
		PORT_DIPSETTING(    0x02, "German" );
	  /*PORT_DIPSETTING(    0x03, "German" );/
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x10, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* effects button 1 */
		PORT_SERVICE( 0x80, IP_ACTIVE_HIGH );
	
		PORT_START();       /* IN4 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN5 */
		PORT_ANALOG( 0xff, 0, IPT_TRACKBALL_Y | IPF_REVERSE, 10, 10, 0, 0);
	
		PORT_START();       /* IN6 */
		PORT_ANALOG( 0xff, 0, IPT_TRACKBALL_X, 10, 10, 0, 0);
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_bowler = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(bowler_readport,writeport_1_2)
		MDRV_MACHINE_INIT(bowler)
	
		/* video hardware */
		MDRV_SCREEN_SIZE(35*8, 32*8)	/* Extra 3 lines for the bonus display */
		MDRV_VISIBLE_AREA(0*8, 35*8-1, 4*8, 32*8-1)
	
		/* sound hardware */
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "Shuffleboard"                               */
	/*                                                     */
	/*******************************************************/
	
	public static IO_ReadPort shuffle_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x01, 0x01, c8080bw_shift_data_r ),
		new IO_ReadPort( 0x02, 0x02, input_port_0_r ),				/* dip switch */
		new IO_ReadPort( 0x04, 0x04, input_port_1_r ),				/* coins / switches */
		new IO_ReadPort( 0x05, 0x05, input_port_2_r ),				/* ball vert */
		new IO_ReadPort( 0x06, 0x06, input_port_3_r ),				/* ball horz */
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	static InputPortHandlerPtr input_ports_shuffle = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( shuffle )
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, "Language" );
		PORT_DIPSETTING(    0x00, "English" );
		PORT_DIPSETTING(    0x01, "French" );
		PORT_DIPSETTING(    0x02, "German" );
	  /*PORT_DIPSETTING(    0x03, "German" );/
		PORT_DIPNAME( 0x0c, 0x04, "Points to Win" );
		PORT_DIPSETTING(    0x00, "25" );
		PORT_DIPSETTING(    0x04, "35" );
		PORT_DIPSETTING(    0x08, "40" );
		PORT_DIPSETTING(    0x0c, "50" );
		PORT_DIPNAME( 0x30, 0x10, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x30, "2 Coins/1 Player  4 Coins/2 Players" );
		PORT_DIPSETTING(    0x20, "2 Coins/1 or 2 Players" );
		PORT_DIPSETTING(    0x10, "1 Coin/1 Player  2 Coins/2 Players" );
		PORT_DIPSETTING(    0x00, "1 Coin/1 or 2 Players" );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") );	/* time limit? */
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BITX(0x08, IP_ACTIVE_LOW, IPT_BUTTON1, "Game Select", IP_KEY_DEFAULT, IP_JOY_DEFAULT );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();       /* IN2 */
		PORT_ANALOG( 0xff, 0, IPT_TRACKBALL_Y, 10, 10, 0, 0);
	
		PORT_START();       /* IN3 */
		PORT_ANALOG( 0xff, 0, IPT_TRACKBALL_X | IPF_REVERSE, 10, 10, 0, 0);
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_shuffle = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(shuffle_readport,writeport_1_2)
	
		/* sound hardware */
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "Sea Wolf"                                   */
	/*                                                     */
	/*******************************************************/
	
	public static IO_ReadPort seawolf_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, c8080bw_shift_data_rev_r ),
		new IO_ReadPort( 0x01, 0x01, input_port_0_r ),
		new IO_ReadPort( 0x02, 0x02, input_port_1_r ),
		new IO_ReadPort( 0x03, 0x03, c8080bw_shift_data_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	static InputPortHandlerPtr input_ports_seawolf = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( seawolf )
		PORT_START();       /* IN0 */
		PORT_ANALOG( 0x1f, 0x01, IPT_PADDLE, 20, 5, 0, 0x1f);
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_DIPNAME( 0xc0, 0x00, "Time" );
		PORT_DIPSETTING(    0x00, "61" );
		PORT_DIPSETTING(    0x40, "71" );
		PORT_DIPSETTING(    0x80, "81" );
		PORT_DIPSETTING(    0xc0, "91" );
	
		PORT_START();       /* IN1 Dips & Coins */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_2C") );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_TILT );// Reset High Scores
		PORT_DIPNAME( 0xe0, 0x20, "Extended Play" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPSETTING(    0x20, "2000" );
		PORT_DIPSETTING(    0x40, "3000" );
		PORT_DIPSETTING(    0x60, "4000" );
		PORT_DIPSETTING(    0x80, "5000" );
		PORT_DIPSETTING(    0xa0, "6000" );
		PORT_DIPSETTING(    0xc0, "7000" );
		PORT_DIPSETTING(    0xe0, "Test Mode" );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_seawolf = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(seawolf_readport,writeport_4_3)
		MDRV_MACHINE_INIT(seawolf)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SAMPLES, seawolf_samples_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "Blue Shark"                                 */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_blueshrk = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( blueshrk )
		PORT_START();       /* IN0 */
		PORT_ANALOG( 0x7f, 0x45, IPT_PADDLE, 100, 10, 0xf, 0x7f);
	
		PORT_START();       /* IN1 Dips & Coins */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_DIPNAME( 0x60, 0x20, "Replay" );
		PORT_DIPSETTING(    0x20, "14000" );
		PORT_DIPSETTING(    0x40, "18000" );
		PORT_DIPSETTING(    0x60, "22000" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_blueshrk = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(seawolf_readport,writeport_1_2)
	
		/* sound hardware */
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "Desert Gun"                                 */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_desertgu = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( desertgu )
		PORT_START();       /* IN0 */
		PORT_ANALOG( 0x7f, 0x55, IPT_LIGHTGUN_X, 70, 10, 0xf, 0x7f);
	
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x00, "Time" );
		PORT_DIPSETTING(    0x00, "40" );
		PORT_DIPSETTING(    0x01, "50" );
		PORT_DIPSETTING(    0x02, "60" );
		PORT_DIPSETTING(    0x03, "70" );
		PORT_DIPNAME( 0x0c, 0x00, "Language" );
		PORT_DIPSETTING(    0x00, "English" );
		PORT_DIPSETTING(    0x04, "German" );
		PORT_DIPSETTING(    0x08, "French" );
		PORT_DIPSETTING(    0x0c, "Norwegian?" );
		PORT_DIPNAME( 0x30, 0x00, "Extended Play" );
		PORT_DIPSETTING(    0x00, "5000" );
		PORT_DIPSETTING(    0x10, "7000" );
		PORT_DIPSETTING(    0x20, "9000" );
		PORT_DIPSETTING(    0x30, "Test Mode" );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 );
	
		PORT_START();       /* IN2 */
		PORT_ANALOG( 0x7f, 0x45, IPT_LIGHTGUN_Y, 70, 10, 0xf, 0x7f);
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_desertgu = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(seawolf_readport,writeport_1_2)
		MDRV_MACHINE_INIT(desertgu)
	
		/* sound hardware */
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "Extra Innings"                              */
	/*                                                     */
	/*******************************************************/
	
	/*
	 * The cocktail version has independent bat, pitch, and field controls
	 * while the upright version ties the pairs of inputs together through
	 * jumpers in the wiring harness.
	 */
	static InputPortHandlerPtr input_ports_einnings = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( einnings )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );		/* home bat */
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_PLAYER2 );/* home fielders left */
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER2 );/* home fielders right */
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_PLAYER2 );	/* home pitch left */
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_PLAYER2 );/* home pitch right */
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );		/* home pitch slow */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2);		/* home pitch fast */
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 );	/* vistor bat */
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT );/* vistor fielders left */
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT );/* visitor fielders right */
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP );/* visitor pitch left */
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN );/* visitor pitch right */
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );	/* visitor pitch slow */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 );	/* visitor pitch fast */
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START2 );
	
		PORT_START();       /* IN2 Dips & Coins */
		PORT_DIPNAME( 0x07, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x02, "2C/1 In (1 or 2 Players"));
		PORT_DIPSETTING(    0x03, "2C/1 In 4C/3 In (1 or 2 Pls"));
		PORT_DIPSETTING(    0x00, "1 Coin/1 Inning (1 or 2 Pls"));
		PORT_DIPSETTING(    0x01, "1C/1 In 2C/3 In (1 or 2 Pls"));
		PORT_DIPSETTING(    0x04, "1C/1Pl 2C/2Pl 4C/3Inn" );
		PORT_DIPSETTING(    0x05, "2C/1Pl 4C/2Pl 8C/3Inn" );
	/* 0x06 and 0x07 same as 0x00 */
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_SERVICE( 0x40, IP_ACTIVE_LOW );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	INPUT_PORTS_END(); }}; 
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "Amazing Maze"                               */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_maze = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( maze )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_PLAYER2 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_COIN1  );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN);
		PORT_SERVICE( 0x80, IP_ACTIVE_HIGH );
	
		PORT_START();       /* DSW0 - Never read (?) */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x02, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
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
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "Tornado Baseball"                           */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_tornbase = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( tornbase )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON3 | IPF_PLAYER1);
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN);
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON3 | IPF_PLAYER2);
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_PLAYER1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_PLAYER1);
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN);
	
		PORT_START();       /* DSW0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW,  IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW,  IPT_UNKNOWN );
		PORT_DIPNAME( 0x78, 0x40, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x18, "4 Coins/1 Inning 32/9" );
		PORT_DIPSETTING(    0x10, "3 Coins/1 Inning 24/9" );
		PORT_DIPSETTING(    0x38, "4 Coins/2 Innings 16/9" );
		PORT_DIPSETTING(    0x08, "2 Coins/1 Inning 16/9" );
		PORT_DIPSETTING(    0x30, "3 Coins/2 Innings 12/9" );
		PORT_DIPSETTING(    0x28, "2 Coins/2 Innings 8/9" );
		PORT_DIPSETTING(    0x00, "1 Coin/1 Inning 8/9" );
		PORT_DIPSETTING(    0x58, "4 Coins/4 Innings 8/9" );
		PORT_DIPSETTING(    0x50, "3 Coins/4 Innings 6/9" );
		PORT_DIPSETTING(    0x48, "2 Coins/4 Innings 4/9" );
		PORT_DIPSETTING(    0x20, "1 Coin/2 Innings 4/9" );
		PORT_DIPSETTING(    0x40, "1 Coin/4 Innings 2/9" );
		PORT_DIPSETTING(    0x78, "4 Coins/9 Innings" );
		PORT_DIPSETTING(    0x70, "3 Coins/9 Innings" );
		PORT_DIPSETTING(    0x68, "2 Coins/9 Innings" );
		PORT_DIPSETTING(    0x60, "1 Coin/9 Innings" );
		PORT_SERVICE( 0x80, IP_ACTIVE_HIGH );
	INPUT_PORTS_END(); }}; 
	
	
	/*******************************************************/
	/*                                                     */
	/* Midway "Checkmate"                                  */
	/*                                                     */
	/*******************************************************/
	
	public static IO_ReadPort checkmat_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, input_port_0_r ),
		new IO_ReadPort( 0x01, 0x01, input_port_1_r ),
		new IO_ReadPort( 0x02, 0x02, input_port_2_r ),
		new IO_ReadPort( 0x03, 0x03, input_port_3_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	
	static InputPortHandlerPtr input_ports_checkmat = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( checkmat )
		PORT_START();       /* IN0  */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_PLAYER2 );
	
		PORT_START();       /* IN1  */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_PLAYER3 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_PLAYER3 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_PLAYER3 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_PLAYER3 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_PLAYER4 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_PLAYER4 );
	
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_PLAYER4 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_PLAYER4 );
	
		PORT_START();       /* IN2 Dips & Coins */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, "1 Coin/1 or 2 Players" );
		PORT_DIPSETTING(    0x01, "1 Coin/1 to 4 Players" );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x0c, 0x00, "Rounds" );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x04, "3" );
		PORT_DIPSETTING(    0x08, "4" );
		PORT_DIPSETTING(    0x0c, "5" );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x60, 0x00, "Language?" );
		PORT_DIPSETTING(    0x00, "English?" );
		PORT_DIPSETTING(    0x20, "German?" );
		PORT_DIPSETTING(    0x40, "French?" );
		PORT_DIPSETTING(    0x60, "Spanish?" );
		PORT_SERVICE( 0x80, IP_ACTIVE_HIGH );
	
		PORT_START();        /* IN3  */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START3 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START4 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_COIN1 );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_checkmat = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_PORTS(checkmat_readport,0)
	
		/* sound hardware */
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Taito "Ozma Wars"                                   */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_ozmawars = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( ozmawars )
		PORT_START(); 		/* IN0 */
	
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, "Energy" );
		PORT_DIPSETTING(    0x00, "15000" );
		PORT_DIPSETTING(    0x01, "20000" );
		PORT_DIPSETTING(    0x02, "25000" );
		PORT_DIPSETTING(    0x03, "35000" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, "Bonus Energy" );
		PORT_DIPSETTING(    0x00, "15000" );
		PORT_DIPSETTING(    0x08, "10000" );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_spaceph = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( spaceph )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_DIPNAME( 0x03, 0x00, "Energy" );
		PORT_DIPSETTING(    0x00, "15000" );
		PORT_DIPSETTING(    0x01, "20000" );
		PORT_DIPSETTING(    0x02, "25000" );
		PORT_DIPSETTING(    0x03, "35000" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, "Bonus Energy" );
		PORT_DIPSETTING(    0x08, "10000" );
		PORT_DIPSETTING(    0x00, "15000" );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* Fire */
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* Left */
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* Right */
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
	INPUT_PORTS_END(); }}; 
	
	
	
	/*******************************************************/
	/*                                                     */
	/* Emag "Super Invaders"                               */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_sinvemag = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( sinvemag )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_UNKNOWN );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x08, "1000" );
		PORT_DIPSETTING(    0x00, "1500" );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_DIPNAME( 0x80, 0x00, "Coin Info" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	
	
	/*******************************************************/
	/*                                                     */
	/* Jatre Specter (Taito?)                              */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_jspecter = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( jspecter )
	
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x80, "Easy" );
		PORT_DIPSETTING(    0x00, "Hard" );
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x08, "1000" );
		PORT_DIPSETTING(    0x00, "1500" );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_DIPNAME( 0x80, 0x00, "Coin Info" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	
	/*******************************************************/
	/*                                                     */
	/* Taito "Balloon Bomber"                              */
	/*                                                     */
	/*******************************************************/
	
	static InputPortHandlerPtr input_ports_ballbomb = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( ballbomb )
		PORT_START(); 		/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_TILT );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	public static MachineHandlerPtr machine_driver_ballbomb = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
		MDRV_MACHINE_INIT(ballbomb)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(8)
		MDRV_PALETTE_INIT(invadpt2)
		MDRV_VISIBLE_AREA(1*8, 31*8-1, 4*8, 32*8-1)
	
		/* sound hardware */
	MACHINE_DRIVER_END();
 }
};
	
	
	/*******************************************************/
	/*                                                     */
	/* Yosaku To Donbee                                    */
	/*                                                     */
	/*******************************************************/
	
	public static MachineHandlerPtr machine_driver_yosakdon = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(8080bw)
		MDRV_CPU_MODIFY("main")
	
		/* video hardware */
		MDRV_VISIBLE_AREA(1*8, 31*8-1, 4*8, 32*8-1)
	
		/* sound hardware */
	MACHINE_DRIVER_END();
 }
};
	
	
	static InputPortHandlerPtr input_ports_spceking = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( spceking )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW,  IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW,  IPT_UNKNOWN );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "6" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, "High Score Preset Mode" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_PLAYER2 );
		PORT_DIPNAME( 0x80, 0x00, "Coin Info" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 		/* Dummy port for cocktail mode */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	
	
	
	static RomLoadHandlerPtr rom_invaders = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "invaders.h",   0x0000, 0x0800, CRC(734f5ad8) SHA1(ff6200af4c9110d8181249cbcef1a8a40fa40b7f) )
		ROM_LOAD( "invaders.g",   0x0800, 0x0800, CRC(6bfaca4a) SHA1(16f48649b531bdef8c2d1446c429b5f414524350) )
		ROM_LOAD( "invaders.f",   0x1000, 0x0800, CRC(0ccead96) SHA1(537aef03468f63c5b9e11dd61e253f7ae17d9743) )
		ROM_LOAD( "invaders.e",   0x1800, 0x0800, CRC(14e538b0) SHA1(1d6ca0c99f9df71e2990b610deb9d7da0125e2d8) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_earthinv = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )             /* 64k for code */
		ROM_LOAD( "earthinv.h",   0x0000, 0x0800, CRC(58a750c8) SHA1(90bfa4ea06f38e67fe4286d37d151632439249d2) )
		ROM_LOAD( "earthinv.g",   0x0800, 0x0800, CRC(b91742f1) SHA1(8d9ca92405fbaf1d5a7138d400986616378d061e) )
		ROM_LOAD( "earthinv.f",   0x1000, 0x0800, CRC(4acbbc60) SHA1(b8c1efb4251a1e690ff6936ec956d6f66136a085) )
		ROM_LOAD( "earthinv.e",   0x1800, 0x0800, CRC(df397b12) SHA1(e7e8c080cb6baf342ec637532e05d38129ae73cf) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_spaceatt = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "h",            0x0000, 0x0400, CRC(d0c32d72) SHA1(b3bd950b1ba940fbeb5d95e55113ed8f4c311434) )
		ROM_LOAD( "sv02.bin",     0x0400, 0x0400, CRC(0e159534) SHA1(94b2015a9d38ca738705b8d024a79fd2f9855b98) )
		ROM_LOAD( "f",            0x0800, 0x0400, CRC(483e651e) SHA1(ae795ee3bc53ac3936f6cf2c72cca7a890783513) )
		ROM_LOAD( "c",            0x1400, 0x0400, CRC(1293b826) SHA1(165cd5d08a19eadbe954145b12807f10df9e691a) )
		ROM_LOAD( "b",            0x1800, 0x0400, CRC(6fc782aa) SHA1(0275adbeec455e146f4443b0b836b1171436b79b) )
		ROM_LOAD( "a",            0x1c00, 0x0400, CRC(211ac4a3) SHA1(e08e90a4e77cfa30400626a484c9f37c87ea13f9) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_spaceat2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "spaceatt.h",   0x0000, 0x0800, CRC(a31d0756) SHA1(2b76929654ed0b180091348546dac29fc6e5438e) )
		ROM_LOAD( "spaceatt.g",   0x0800, 0x0800, CRC(f41241f7) SHA1(d93cead75922510075433849c4f7099279eafc18) )
		ROM_LOAD( "spaceatt.f",   0x1000, 0x0800, CRC(4c060223) SHA1(957e75a978aa600627399061cae0a6525e92ad11) )
		ROM_LOAD( "spaceatt.e",   0x1800, 0x0800, CRC(7cf6f604) SHA1(469557de15178c4b2d686e5724e1006f7c20d7a4) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_sinvzen = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "1.bin",        0x0000, 0x0400, CRC(9b0da779) SHA1(a52ccdb252eb69c497aa5eafb35d7f25a311b44e) )
		ROM_LOAD( "2.bin",        0x0400, 0x0400, CRC(9858ccab) SHA1(5ad8e5ef0d95779f0e513634b97bc330c9269ce4) )
		ROM_LOAD( "3.bin",        0x0800, 0x0400, CRC(a1cc38b5) SHA1(45fc9466b548d511b8174f6f3a4783164dd59489) )
		ROM_LOAD( "4.bin",        0x0c00, 0x0400, CRC(1f2db7a8) SHA1(354ad155743f724f2bebcab422f1ef96cb57c683) )
		ROM_LOAD( "5.bin",        0x1000, 0x0400, CRC(9b505fcd) SHA1(7461b7087d31dbe09f7b3078584ccaa2c9122c95) )
		ROM_LOAD( "6.bin",        0x1400, 0x0400, CRC(de0ca0ae) SHA1(a15d1218361839a2a2bf8da3f78d81621251fe1c) )
		ROM_LOAD( "7.bin",        0x1800, 0x0400, CRC(25a296f6) SHA1(37df98384c1513f0e33a350dfcaa99655f91c9ba) )
		ROM_LOAD( "8.bin",        0x1c00, 0x0400, CRC(f4bc4a98) SHA1(bff3806750a3695a136f398c7dbb69a0b7daa88a) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_sinvemag = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "sv0h.bin",     0x0000, 0x0400, CRC(86bb8cb6) SHA1(a75648e7f2446c756d86624b15d387d25ce47b66) )
		ROM_LOAD( "emag_si.b",    0x0400, 0x0400, CRC(febe6d1a) SHA1(e1c3a24b4fa5862107ada1f9d7249466e8c3f06a) )
		ROM_LOAD( "emag_si.c",    0x0800, 0x0400, CRC(aafb24f7) SHA1(6718cdfae09f77d735be5145b9d202a73d8ed9db) )
		ROM_LOAD( "emag_si.d",    0x1400, 0x0400, CRC(68c4b9da) SHA1(8953dc0427b09b71bd763e65caa7deaca09a15da) )
		ROM_LOAD( "emag_si.e",    0x1800, 0x0400, CRC(c4e80586) SHA1(3d427d5a2eea3c911ec7bd055e06e6747ce5e84d) )
		ROM_LOAD( "emag_si.f",    0x1c00, 0x0400, CRC(077f5ef2) SHA1(625de6839073ac4c904f949efc1b2e0afea5d676) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_tst_invd = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "test.h",   0x0000, 0x0800, CRC(f86a2eea) SHA1(4a72ff01f3e6d16bbe9bf7f123cd98895bfbed9a) )   /*  The Test ROM */
		ROM_LOAD( "invaders.g",   0x0800, 0x0800, CRC(6bfaca4a) SHA1(16f48649b531bdef8c2d1446c429b5f414524350) )
		ROM_LOAD( "invaders.f",   0x1000, 0x0800, CRC(0ccead96) SHA1(537aef03468f63c5b9e11dd61e253f7ae17d9743) )
		ROM_LOAD( "invaders.e",   0x1800, 0x0800, CRC(14e538b0) SHA1(1d6ca0c99f9df71e2990b610deb9d7da0125e2d8) )
	ROM_END(); }}; 
	
	
	static RomLoadHandlerPtr rom_alieninv = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "1h.bin",       0x0000, 0x0800, CRC(c46df7f4) SHA1(eec34b3d5585bae03c7b80585daaa05ddfcc2164) )
		ROM_LOAD( "1g.bin",       0x0800, 0x0800, CRC(4b1112d6) SHA1(b693667656e5d8f44eeb2ea730f4d4db436da579) )
		ROM_LOAD( "1f.bin",       0x1000, 0x0800, CRC(adca18a5) SHA1(7e02651692113db31fd469868ae5ffdb0f941ecf) )
		ROM_LOAD( "1e.bin",       0x1800, 0x0800, CRC(0449cb52) SHA1(8adcb7cd4492fa6649d9ee81172d8dff56621d64) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_sitv = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "tv0h.s1",      0x0000, 0x0800, CRC(fef18aad) SHA1(043edeefe6a6d4934bd384eafea19326de1dbeec) )
		ROM_LOAD( "tv02.rp1",     0x0800, 0x0800, CRC(3c759a90) SHA1(d847d592dee592b1d3a575c21d89eaf3f7f6ae1b) )
		ROM_LOAD( "tv03.n1",      0x1000, 0x0800, CRC(0ad3657f) SHA1(a501f316535c50f7d7a20ef8e6dede1526a3f2a8) )
		ROM_LOAD( "tv04.m1",      0x1800, 0x0800, CRC(cd2c67f6) SHA1(60f9d8fe2d36ff589277b607f07c1edc917c755c) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_sicv = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "cv17.bin",     0x0000, 0x0800, CRC(3dfbe9e6) SHA1(26487df7fa0bbd0b9b7f74347c4b9318b0a73b89) )
		ROM_LOAD( "cv18.bin",     0x0800, 0x0800, CRC(bc3c82bf) SHA1(33e39fc97bd46699be1f9b9741a86f433efdc911) )
		ROM_LOAD( "cv19.bin",     0x1000, 0x0800, CRC(d202b41c) SHA1(868fe938ef768655c894ec95b7d9a81bf21f69ca) )
		ROM_LOAD( "cv20.bin",     0x1800, 0x0800, CRC(c74ee7b6) SHA1(4f52db274a2d4433ab67c099ee805e8eb8516c0f) )
	
		ROM_REGION( 0x0800, REGION_PROMS, 0 )		/* color maps player 1/player 2 */
		ROM_LOAD( "cv01_1.bin",   0x0000, 0x0400, CRC(aac24f34) SHA1(ad110e776547fb48baac568bb50d61854537ca34) )
		ROM_LOAD( "cv02_2.bin",   0x0400, 0x0400, CRC(2bdf83a0) SHA1(01ffbd43964c41987e7d44816271308f9a70802b) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_sisv = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "sv0h.bin",     0x0000, 0x0400, CRC(86bb8cb6) SHA1(a75648e7f2446c756d86624b15d387d25ce47b66) )
		ROM_LOAD( "sv02.bin",     0x0400, 0x0400, CRC(0e159534) SHA1(94b2015a9d38ca738705b8d024a79fd2f9855b98) )
		ROM_LOAD( "invaders.g",   0x0800, 0x0800, CRC(6bfaca4a) SHA1(16f48649b531bdef8c2d1446c429b5f414524350) )
		ROM_LOAD( "invaders.f",   0x1000, 0x0800, CRC(0ccead96) SHA1(537aef03468f63c5b9e11dd61e253f7ae17d9743) )
		ROM_LOAD( "tv04.m1",      0x1800, 0x0800, CRC(cd2c67f6) SHA1(60f9d8fe2d36ff589277b607f07c1edc917c755c) )
	
		ROM_REGION( 0x0800, REGION_PROMS, 0 )		/* color maps player 1/player 2 */
		ROM_LOAD( "cv01_1.bin",   0x0000, 0x0400, CRC(aac24f34) SHA1(ad110e776547fb48baac568bb50d61854537ca34) )
		ROM_LOAD( "cv02_2.bin",   0x0400, 0x0400, CRC(2bdf83a0) SHA1(01ffbd43964c41987e7d44816271308f9a70802b) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_sisv2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "sv0h.bin",     0x0000, 0x0400, CRC(86bb8cb6) SHA1(a75648e7f2446c756d86624b15d387d25ce47b66) )
		ROM_LOAD( "emag_si.b",    0x0400, 0x0400, CRC(febe6d1a) SHA1(e1c3a24b4fa5862107ada1f9d7249466e8c3f06a) )
		ROM_LOAD( "sv12",         0x0800, 0x0400, CRC(a08e7202) SHA1(de9f7c851d1b894915e720cfc5d794cdb31752f6) )
		ROM_LOAD( "invaders.f",   0x1000, 0x0800, CRC(0ccead96) SHA1(537aef03468f63c5b9e11dd61e253f7ae17d9743) )
		ROM_LOAD( "sv13",         0x1800, 0x0400, CRC(a9011634) SHA1(1f1369ecb02078042cfdf17a497b8dda6dd23793) )
		ROM_LOAD( "sv14",         0x1c00, 0x0400, CRC(58730370) SHA1(13dc806bcecd2d6089a85dd710ac2869413f7475) )
	
		ROM_REGION( 0x0800, REGION_PROMS, 0 )		/* color maps player 1/player 2 */
		ROM_LOAD( "cv01_1.bin",   0x0000, 0x0400, CRC(aac24f34) SHA1(ad110e776547fb48baac568bb50d61854537ca34) )
		ROM_LOAD( "cv02_2.bin",   0x0400, 0x0400, CRC(2bdf83a0) SHA1(01ffbd43964c41987e7d44816271308f9a70802b) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_spceking = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "invaders.h",   0x0000, 0x0800, CRC(734f5ad8) SHA1(ff6200af4c9110d8181249cbcef1a8a40fa40b7f) )
		ROM_LOAD( "spcekng2",     0x0800, 0x0800, CRC(96dcdd42) SHA1(e18d7ffca92e863ef40e235b2be973d8c5879fdb) )
		ROM_LOAD( "spcekng3",     0x1000, 0x0800, CRC(95fc96ad) SHA1(38175edad0e538a1561cec8f7613f15ae274dd14) )
		ROM_LOAD( "spcekng4",     0x1800, 0x0800, CRC(54170ada) SHA1(1e8b3774355ec0d448f04805a917f4c1fe64bceb) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_spcewars = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "sanritsu.1",   0x0000, 0x0400, CRC(ca331679) SHA1(5c362c3d1c721d293bcddbef4033533769c8f0e0) )
		ROM_LOAD( "sanritsu.2",   0x0400, 0x0400, CRC(48dc791c) SHA1(91a98205c83ca38961e6ba2ac43a41e6e8bc2675) )
		ROM_LOAD( "ic35.bin",     0x0800, 0x0800, CRC(40c2d55b) SHA1(b641b63046d242ad23911143ed840011fc98eaff) )
		ROM_LOAD( "sanritsu.5",   0x1000, 0x0400, CRC(77475431) SHA1(15a04a2655847ee462be65d1065d643c872bb47c) )
		ROM_LOAD( "sanritsu.6",   0x1400, 0x0400, CRC(392ef82c) SHA1(77c98c11ee727ed3ed6e118f13d97aabdb555540) )
		ROM_LOAD( "sanritsu.7",   0x1800, 0x0400, CRC(b3a93df8) SHA1(3afc96814149d4d5343fe06eac09f808384d02c4) )
		ROM_LOAD( "sanritsu.8",   0x1c00, 0x0400, CRC(64fdc3e1) SHA1(c3c278bc236ced7fc85e1a9b018e80be6ab33402) )
		ROM_LOAD( "sanritsu.9",   0x4000, 0x0400, CRC(b2f29601) SHA1(ce855e312f50df7a74682974803cb4f9b2d184f3) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_spacewr3 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "ic36.bin",     0x0000, 0x0800, CRC(9e30f88a) SHA1(314dfb2920d9b43b977cc19e40ac315e6933c3b9) )
		ROM_LOAD( "ic35.bin",     0x0800, 0x0800, CRC(40c2d55b) SHA1(b641b63046d242ad23911143ed840011fc98eaff) )
		ROM_LOAD( "ic34.bin",     0x1000, 0x0800, CRC(b435f021) SHA1(2d0d813b99d571b53770fa878a1f82ca67827caa) )
		ROM_LOAD( "ic33.bin",     0x1800, 0x0800, CRC(cbdc6fe8) SHA1(63038ea09d320c54e3d1cf7f043c17bba71bf13c) )
		ROM_LOAD( "ic32.bin",     0x4000, 0x0800, CRC(1e5a753c) SHA1(5b7cd7b347203f4edf816f02c366bd3b1b9517c4) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_invaderl = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "c01",          0x0000, 0x0400, CRC(499f253a) SHA1(e13353194277f5d35e92db9b11912b5f392f51b7) )
		ROM_LOAD( "c02",          0x0400, 0x0400, CRC(2d0b2e1f) SHA1(2e0262d9dba607824fcd720d2995531649bdd03d) )
		ROM_LOAD( "c03",          0x0800, 0x0400, CRC(03033dc2) SHA1(87d7838e6a6542c2c5510af593df45137cb397c6) )
		ROM_LOAD( "c07",          0x1000, 0x0400, CRC(5a7bbf1f) SHA1(659f2a8c646660d316d6e70f1d9548375f1da63f) )
		ROM_LOAD( "c04",          0x1400, 0x0400, CRC(455b1fa7) SHA1(668800a0a3ba18d8b54c2aa4dfd4bd01a667d679) )
		ROM_LOAD( "c05",          0x1800, 0x0400, CRC(40cbef75) SHA1(15994ed8bb8ab8faed6198926873851062c9d95f) )
		ROM_LOAD( "sv06.bin",     0x1c00, 0x0400, CRC(2c68e0b4) SHA1(a5e5357120102ad32792bf3ef6362f45b7ba7070) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_jspecter = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "3305.u6",      0x0000, 0x1000, CRC(ab211a4f) SHA1(d675ed29c3479d7318f8559bd56dd619cf631b6a) )
		ROM_LOAD( "3306.u7",      0x1400, 0x1000, CRC(0df142a7) SHA1(2f1c32d6fe7eafb7808fef0bdeb69b4909427417) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_jspectr2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "unksi.b2",     0x0000, 0x1000, CRC(0584b6c4) SHA1(c130021b878bde2beda4a189f71bbfed61088535) )
		ROM_LOAD( "unksi.a2",     0x1400, 0x1000, CRC(58095955) SHA1(545df3bb9ee4ff09f491d7a4b704e31aa311a8d7) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_invadpt2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "pv.01",        0x0000, 0x0800, CRC(7288a511) SHA1(ff617872784c28ed03591aefa9f0519e5651701f) )
		ROM_LOAD( "pv.02",        0x0800, 0x0800, CRC(097dd8d5) SHA1(8d68654d54d075c0f0d7f63c87ff4551ce8b7fbf) )
		ROM_LOAD( "pv.03",        0x1000, 0x0800, CRC(1766337e) SHA1(ea959bf06c9930d83a07559e191a28641efb07ac) )
		ROM_LOAD( "pv.04",        0x1800, 0x0800, CRC(8f0e62e0) SHA1(a967b155f15f8432222fcc78b23121b00c405c5c) )
		ROM_LOAD( "pv.05",        0x4000, 0x0800, CRC(19b505e9) SHA1(6a31a37586782ce421a7d2cffd8f958c00b7b415) )
	
		ROM_REGION( 0x0800, REGION_PROMS, 0 )		/* color maps player 1/player 2 */
		ROM_LOAD( "pv06_1.bin",   0x0000, 0x0400, CRC(a732810b) SHA1(a5fabffa73ca740909e23b9530936f9274dff356) )
		ROM_LOAD( "pv07_2.bin",   0x0400, 0x0400, CRC(2c5b91cb) SHA1(7fa4d4aef85473b1b4f18734230c164e72be44e7) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_invaddlx = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "invdelux.h",   0x0000, 0x0800, CRC(e690818f) SHA1(0860fb03a64d34a9704a1459a5e96929eafd39c7) )
		ROM_LOAD( "invdelux.g",   0x0800, 0x0800, CRC(4268c12d) SHA1(df02419f01cf0874afd1f1aa16276751acd0604a) )
		ROM_LOAD( "invdelux.f",   0x1000, 0x0800, CRC(f4aa1880) SHA1(995d77b67cb4f2f3781c2c8747cb058b7c1b3412) )
		ROM_LOAD( "invdelux.e",   0x1800, 0x0800, CRC(408849c1) SHA1(f717e81017047497a2e9f33f0aafecfec5a2ed7d) )
		ROM_LOAD( "invdelux.d",   0x4000, 0x0800, CRC(e8d5afcd) SHA1(91fde9a9e7c3dd53aac4770bd169721a79b41ed1) )
	
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_moonbase = new RomLoadHandlerPtr(){ public void handler(){ 
	
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	   /* 64k for code */
		ROM_LOAD( "pv.01",        0x0000, 0x0800, CRC(7288a511) SHA1(ff617872784c28ed03591aefa9f0519e5651701f) )
		ROM_LOAD( "pv.02",        0x0800, 0x0800, CRC(097dd8d5) SHA1(8d68654d54d075c0f0d7f63c87ff4551ce8b7fbf) )
		ROM_LOAD( "ze3-5.bin",    0x1000, 0x0400, CRC(2b105ed3) SHA1(fa0767089b3aaec25be39e950e7163ecbdc2f39f) )
		ROM_LOAD( "ze3-6.bin",    0x1400, 0x0400, CRC(cb3d6dcb) SHA1(b4923b12a141c76b7d50274f19a3224db26a5669) )
		ROM_LOAD( "ze3-7.bin",    0x1800, 0x0400, CRC(774b52c9) SHA1(ddbbba874ac069fb930b364a890c45675ec389f7) )
		ROM_LOAD( "ze3-8.bin",    0x1c00, 0x0400, CRC(e88ea83b) SHA1(ef05be4783c860369ee5ecd4844837207e99ad9f) )
		ROM_LOAD( "ze3-9.bin",    0x4000, 0x0400, CRC(2dd5adfa) SHA1(62cb98cad1e48de0e0cbf30392d35834b38dadbd) )
		ROM_LOAD( "ze3-10.bin",   0x4400, 0x0400, CRC(1e7c22a4) SHA1(b34173375494ffbf5400dd4014a683a9807f4f08) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_invad2ct = new RomLoadHandlerPtr(){ public void handler(){ 
	    ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
	    ROM_LOAD( "invad2ct.h",   0x0000, 0x0800, CRC(51d02a71) SHA1(2fa82ddc2702a72de0a9559ec244b70ab3db3f18) )
	    ROM_LOAD( "invad2ct.g",   0x0800, 0x0800, CRC(533ac770) SHA1(edb65c289027432dad7861a7d6abbda9223c13b1) )
	    ROM_LOAD( "invad2ct.f",   0x1000, 0x0800, CRC(d1799f39) SHA1(f7f1ba34d57f9883241ba3ef90e34ed20dfb8003) )
	    ROM_LOAD( "invad2ct.e",   0x1800, 0x0800, CRC(291c1418) SHA1(0d9f7973ed81d28c43ef8b96f1180d6629871785) )
	    ROM_LOAD( "invad2ct.b",   0x5000, 0x0800, CRC(8d9a07c4) SHA1(4acbe15185d958b5589508dc0ea3a615fbe3bcca) )
	    ROM_LOAD( "invad2ct.a",   0x5800, 0x0800, CRC(efdabb03) SHA1(33f4cf249e88e2b7154350e54c479eb4fa86f26f) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_invrvnge = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "invrvnge.h",   0x0000, 0x0800, CRC(aca41bbb) SHA1(ca71f792abd6d9a44d15b19d2ccf678e82ccba4f) )
		ROM_LOAD( "invrvnge.g",   0x0800, 0x0800, CRC(cfe89dad) SHA1(218b6a0b636c49c4cdc3667e8b1387ef0e257115) )
		ROM_LOAD( "invrvnge.f",   0x1000, 0x0800, CRC(e350de2c) SHA1(e845565e2f96f9dec3242ec5ab75910a515428c9) )
		ROM_LOAD( "invrvnge.e",   0x1800, 0x0800, CRC(1ec8dfc8) SHA1(fc8fbe1161958f57c9f4ccbcab8a769184b1c562) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_invrvnga = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "5m.bin",       0x0000, 0x0800, CRC(b145cb71) SHA1(127eb11de7ab9835f06510fb12838c0b728c0d42) )
		ROM_LOAD( "5n.bin",       0x0800, 0x0800, CRC(660e8af3) SHA1(bd52eadf4ee3d717fd5bd7206e1e87d729250c92) )
		ROM_LOAD( "5p.bin",       0x1000, 0x0800, CRC(6ec5a9ad) SHA1(d1e84d2d60c6128c092f2cd20a2b87216df3034b) )
		ROM_LOAD( "5r.bin",       0x1800, 0x0800, CRC(74516811) SHA1(0f595c7b0fae5f3f83fdd1ffed5a408ee77c9438) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_spclaser = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "la01",         0x0000, 0x0800, CRC(bedc0078) SHA1(a5bb0cbbb8e3f27d03beb8101b2be1111d73689d) )
		ROM_LOAD( "spcewarl.2",   0x0800, 0x0800, CRC(43bc65c5) SHA1(5f9827c02c2d221e1607359c840374ff7fb92fbf) )
		ROM_LOAD( "la03",         0x1000, 0x0800, CRC(1083e9cc) SHA1(7ad45c6230c9e02fcf51e3414c15e2237eebbd7a) )
		ROM_LOAD( "la04",         0x1800, 0x0800, CRC(5116b234) SHA1(b165b2574cbcb26a5bb43f91df5f8be5f111f486) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_laser = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "1.u36",        0x0000, 0x0800, CRC(b44e2c41) SHA1(00e0b2e088495d6f3bc175e8a53dcb3686ea8484) )
		ROM_LOAD( "2.u35",        0x0800, 0x0800, CRC(9876f331) SHA1(14e36b26d186d9a195492834ef989ed5664d7b65) )
		ROM_LOAD( "3.u34",        0x1000, 0x0800, CRC(ed79000b) SHA1(bfe0407e833ce61aa909f5f1f93c3fc1d46605e9) )
		ROM_LOAD( "4.u33",        0x1800, 0x0800, CRC(10a160a1) SHA1(e2d4208af11b65fc42d2856e57ee3c196f89d360) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_spcewarl = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "spcewarl.1",   0x0000, 0x0800, CRC(1fcd34d2) SHA1(674139944e0d842a85bd21b326bd735e15453038) )
		ROM_LOAD( "spcewarl.2",   0x0800, 0x0800, CRC(43bc65c5) SHA1(5f9827c02c2d221e1607359c840374ff7fb92fbf) )
		ROM_LOAD( "spcewarl.3",   0x1000, 0x0800, CRC(7820df3a) SHA1(53315857f4282c68624b338b068d80ee6828af4c) )
		ROM_LOAD( "spcewarl.4",   0x1800, 0x0800, CRC(adc05b8d) SHA1(c4acf75537c0662a4785d5d6a90643239a54bf43) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_galxwars = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "univgw3.0",    0x0000, 0x0400, CRC(937796f4) SHA1(88e9494cc532498e51e3a68fa1122c40f22b27dd) )
		ROM_LOAD( "univgw4.1",    0x0400, 0x0400, CRC(4b86e7a6) SHA1(167f9f7491a2de39d08e3e6f7057cc75b36c9340) )
		ROM_LOAD( "univgw5.2",    0x0800, 0x0400, CRC(47a187cd) SHA1(640c896ba25f34d323624005bd676257ad17b687) )
		ROM_LOAD( "univgw6.3",    0x0c00, 0x0400, CRC(7b7d22ff) SHA1(74364cf2b04dcfbbc8e0131fa12c0e574f693d34) )
		ROM_LOAD( "univgw1.4",    0x4000, 0x0400, CRC(0871156e) SHA1(3726d0bfe153a0afc62ea56737662074986064b0) )
		ROM_LOAD( "univgw2.5",    0x4400, 0x0400, CRC(6036d7bf) SHA1(36c2ad2ffdb47bbecc40fd67ced6ab51a5cd2f3e) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_galxwar2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "3192.h6",      0x0000, 0x1000, CRC(bde6860b) SHA1(e04b8add32d8f7ea588fae6d6a387f1d40495f1b) )
		ROM_LOAD( "3193.h7",      0x4000, 0x1000, CRC(a17cd507) SHA1(554ab0e8bdc0e7af4a30b0ddc8aa053c8e70255c) ) /* 2nd half unused */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_galxwart = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "galxwars.0",   0x0000, 0x0400, CRC(608bfe7f) SHA1(a41a40a2f0a1bb61a70b9ff8a7da925ab1db7f74) )
		ROM_LOAD( "galxwars.1",   0x0400, 0x0400, CRC(a810b258) SHA1(030a72fffcf240f643bc3006028cb4883cf58bbc) )
		ROM_LOAD( "galxwars.2",   0x0800, 0x0400, CRC(74f31781) SHA1(1de70e8ebbb26eea20ffedb7bd0ca051a67f45e7) )
		ROM_LOAD( "galxwars.3",   0x0c00, 0x0400, CRC(c88f886c) SHA1(4d705fbb97e3868c3f6c90c5e5753ad17cfbf5d6) )
		ROM_LOAD( "galxwars.4",   0x4000, 0x0400, CRC(ae4fe8fb) SHA1(494f44167dc84e4515b769c12f6e24419461dce4) )
		ROM_LOAD( "galxwars.5",   0x4400, 0x0400, CRC(37708a35) SHA1(df6fd521ddfa146ef93e390e47741bdbfda1e7ba) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_starw = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "roma",         0x0000, 0x0400, CRC(60e8993c) SHA1(0bdf163ff0f2e6a8771987d4e7ac604c45af21b8) )
		ROM_LOAD( "romb",         0x0400, 0x0400, CRC(b8060773) SHA1(92aa358c338ef8f5773bccada8988d068764e7ea) )
		ROM_LOAD( "romc",         0x0800, 0x0400, CRC(307ce6b8) SHA1(f4b6f54db3d2377ec27d62d33fa1c4946559a092) )
		ROM_LOAD( "romd",         0x1400, 0x0400, CRC(2b0d0a88) SHA1(d079d12b6d4136519ded32415d668a02147b7601) )
		ROM_LOAD( "rome",         0x1800, 0x0400, CRC(5b1c3ad0) SHA1(edb42eec59c3dd7e274e2ea08fed0f3e8fc72e9e) )
		ROM_LOAD( "romf",         0x1c00, 0x0400, CRC(c8e42d3d) SHA1(841b27af251b9c3a964972e864fb7c88acc742e0) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_lrescue = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "lrescue.1",    0x0000, 0x0800, CRC(2bbc4778) SHA1(0167f1ac1501ab0b4c4e555023fa5efed59d56ae) )
		ROM_LOAD( "lrescue.2",    0x0800, 0x0800, CRC(49e79706) SHA1(bed675bb97d59ae0132c007ccead0d096ed2ddf1) )
		ROM_LOAD( "lrescue.3",    0x1000, 0x0800, CRC(1ac969be) SHA1(67ac47f45b9fa5c530bf6047bb7d5776b52847be) )
		ROM_LOAD( "lrescue.4",    0x1800, 0x0800, CRC(782fee3c) SHA1(668295e9d6d99084bb4e7c5491f00fe75f4f5a88) )
		ROM_LOAD( "lrescue.5",    0x4000, 0x0800, CRC(58fde8bc) SHA1(663665ac5254204c1eba18357d9867034eae55eb) )
		ROM_LOAD( "lrescue.6",    0x4800, 0x0800, CRC(bfb0f65d) SHA1(ea0943d764a16094b6e2289f62ef117c9f838c98) )
	
		ROM_REGION( 0x0800, REGION_PROMS, 0 )		/* color map */
		ROM_LOAD( "7643-1.cpu",   0x0000, 0x0400, CRC(8b2e38de) SHA1(d6a757be31c3a179d31bd3709e71f9e38ec632e9) )
		ROM_RELOAD(  			  0x0400, 0x0400 )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_grescue = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "lrescue.1",    0x0000, 0x0800, CRC(2bbc4778) SHA1(0167f1ac1501ab0b4c4e555023fa5efed59d56ae) )
		ROM_LOAD( "lrescue.2",    0x0800, 0x0800, CRC(49e79706) SHA1(bed675bb97d59ae0132c007ccead0d096ed2ddf1) )
		ROM_LOAD( "lrescue.3",    0x1000, 0x0800, CRC(1ac969be) SHA1(67ac47f45b9fa5c530bf6047bb7d5776b52847be) )
		ROM_LOAD( "grescue.4",    0x1800, 0x0800, CRC(ca412991) SHA1(41b59f338a6c246e0942a8bfa3c0bca2c24c7f81) )
		ROM_LOAD( "grescue.5",    0x4000, 0x0800, CRC(a419a4d6) SHA1(8eeeb31cbebffc98d2c6c5b964f9b320fcf303d2) )
		ROM_LOAD( "lrescue.6",    0x4800, 0x0800, CRC(bfb0f65d) SHA1(ea0943d764a16094b6e2289f62ef117c9f838c98) )
	
		ROM_REGION( 0x0800, REGION_PROMS, 0 )		/* color map */
		ROM_LOAD( "7643-1.cpu",   0x0000, 0x0400, CRC(8b2e38de) SHA1(d6a757be31c3a179d31bd3709e71f9e38ec632e9) )
		ROM_RELOAD(  			  0x0400, 0x0400 )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_desterth = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "36_h.bin",     0x0000, 0x0800, CRC(f86923e5) SHA1(d19935ba3d2c1c2553b3779f1a7ad8856c003dae) )
		ROM_LOAD( "35_g.bin",     0x0800, 0x0800, CRC(797f440d) SHA1(a96917f2296ae467acc795eacc1533a2a2d2f401) )
		ROM_LOAD( "34_f.bin",     0x1000, 0x0800, CRC(993d0846) SHA1(6be0c45add41fa7e43cac96c776cd0ebb45ade7b) )
		ROM_LOAD( "33_e.bin",     0x1800, 0x0800, CRC(8d155fc5) SHA1(1ef5e62d71abbf870c027fa1e477121ff124b8da) )
		ROM_LOAD( "32_d.bin",     0x4000, 0x0800, CRC(3f531b6f) SHA1(2fc1f4912688986650e20a050a5d63ddecd4267e) )
		ROM_LOAD( "31_c.bin",     0x4800, 0x0800, CRC(ab019c30) SHA1(33931510a722168bcf7c30d22eac9345576b6631) )
		ROM_LOAD( "42_b.bin",     0x5000, 0x0800, CRC(ed9dbac6) SHA1(4553f445ac32ebb1be490b02df4924f76557e8f9) )
	
		ROM_REGION( 0x0800, REGION_PROMS, 0 )		/* color map */
		ROM_LOAD( "7643-1.cpu",   0x0000, 0x0400, CRC(8b2e38de) SHA1(d6a757be31c3a179d31bd3709e71f9e38ec632e9) )
		ROM_RELOAD(  			  0x0400, 0x0400 )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_cosmo = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "1.36",         0x0000, 0x0800, CRC(445c9a98) SHA1(89bce80a061e9c12544231f970d9dec801eb1b94) ) 
		ROM_LOAD( "2.35",         0x0800, 0x0800, CRC(df3eb731) SHA1(fb90c1d0f2518195dd49062c9f0fd890536d89f4) ) 
		ROM_LOAD( "3.34",         0x1000, 0x0800, CRC(772c813f) SHA1(a1c0d857c660fb0b838dd0466af7bf5d73bcd55d) ) 
		ROM_LOAD( "4.33",         0x1800, 0x0800, CRC(279f66e6) SHA1(8ce71c08cca0bdde2f2e0ef21622731c4610c030) ) 
	
		ROM_LOAD( "5.32",         0x4000, 0x0800, CRC(cefb18df) SHA1(bb500cf3f7d1a54045a165d3613a92ab3f11d3e8) ) 
		ROM_LOAD( "6.31",         0x4800, 0x0800, CRC(b037f6c4) SHA1(b9a42948052b8cda8d2e4575e59909589f4e7a8d) ) 
		ROM_LOAD( "7.42",         0x5000, 0x0800, CRC(c3831ea2) SHA1(8c67ef0312656ef0eeff34b8463376c736bd8ea1) ) 
		
		ROM_REGION( 0x1000, REGION_PROMS, 0 )		/* color map */
		ROM_LOAD( "n-1.7d",       0x0800, 0x0800, CRC(bd8576f1) SHA1(aa5fe0a4d024f21a3bca7a6b3f5022779af6f3f4) ) 
		ROM_LOAD( "n-2.6e",       0x0000, 0x0800, CRC(48f1ade5) SHA1(a1b45f82f3649cde8ae6a2ef494a3a6cdb5e65d0) ) 
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_cosmicmo = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "cosmicmo.1",   0x0000, 0x0400, CRC(d6e4e5da) SHA1(8b4275a3c71ac3fa80d17237dc04de5f586645f4) )
		ROM_LOAD( "cosmicmo.2",   0x0400, 0x0400, CRC(8f7988e6) SHA1(b6a01d5dcab013350f8f7f3e3ebfc986bb939fe0) )
		ROM_LOAD( "cosmicmo.3",   0x0800, 0x0400, CRC(2d2e9dc8) SHA1(dd3da4fc752e003e5e7c64bf189288133aed545b) )
		ROM_LOAD( "cosmicmo.4",   0x0c00, 0x0400, CRC(26cae456) SHA1(2f2262340c10e5c29d71317f6eb8072c26655563) )
		ROM_LOAD( "cosmicmo.5",   0x4000, 0x0400, CRC(b13f228e) SHA1(a0de05aa36435e72c77f5333f3ad964ec448a8f0) )
		ROM_LOAD( "cosmicmo.6",   0x4400, 0x0400, CRC(4ae1b9c4) SHA1(8eed87eebe68caa775fa679363b0fe3728d98c34) )
		ROM_LOAD( "cosmicmo.7",   0x4800, 0x0400, CRC(6a13b15b) SHA1(dc03a6c3e938cfd08d16bd1660899f951ba72ea2) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_superinv = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )             /* 64k for code */
		ROM_LOAD( "00",           0x0000, 0x0400, CRC(7a9b4485) SHA1(dde918ec106971972bf7c7e5085c1262522f7e35) )
		ROM_LOAD( "01",           0x0400, 0x0400, CRC(7c86620d) SHA1(9e92ec0aa4eee96a7fa115a14a611c488d13b9dd) )
		ROM_LOAD( "02",           0x0800, 0x0400, CRC(ccaf38f6) SHA1(8eb0456e8abdba0d1dda20a335a9ecbe7c38f9ed) )
		ROM_LOAD( "03",           0x1400, 0x0400, CRC(8ec9eae2) SHA1(48d7a7dc61e0417ca4093e5c2a36efd96e359233) )
		ROM_LOAD( "04",           0x1800, 0x0400, CRC(68719b30) SHA1(2084bd63cd61ef1d2497c32112cdb42b7b582da4) )
		ROM_LOAD( "05",           0x1c00, 0x0400, CRC(8abe2466) SHA1(17494b1e5db207e37a7d28d7c89cbc5f36b7aefc) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_rollingc = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "rc01.bin",     0x0000, 0x0400, CRC(66fa50bf) SHA1(7451d4ff8d3b351a324aaecdbdc5b46672f5fdd0) )
		ROM_LOAD( "rc02.bin",     0x0400, 0x0400, CRC(61c06ae4) SHA1(7685c806e20e4a4a0508a547ac08ca8f6d75bb79) )
		ROM_LOAD( "rc03.bin",     0x0800, 0x0400, CRC(77e39fa0) SHA1(16bf88af1b97c5a2a81e105af08b8d9d1f10dcc8) )
		ROM_LOAD( "rc04.bin",     0x0c00, 0x0400, CRC(3fdfd0f3) SHA1(4c5e7136a766f3f16399e61eaaa0e00ef6b619f7) )
		ROM_LOAD( "rc05.bin",     0x1000, 0x0400, CRC(c26a8f5b) SHA1(f7a541999cfe04c6d6927d285484f0f81857e04a) )
		ROM_LOAD( "rc06.bin",     0x1400, 0x0400, CRC(0b98dbe5) SHA1(33cedab82ddccb4caaf681fce553b5230a8d6f92) )
		ROM_LOAD( "rc07.bin",     0x1800, 0x0400, CRC(6242145c) SHA1(b01bb02835dda89dc02604ec52e423167183e8c9) )
		ROM_LOAD( "rc08.bin",     0x1c00, 0x0400, CRC(d23c2ef1) SHA1(909e3d53291dbd219f4f9e0047c65317b9f6d5bd) )
	
		ROM_LOAD( "rc09.bin",     0x4000, 0x0800, CRC(2e2c5b95) SHA1(33f4e2789d67e355ccd99d2c0d07301ec2bd3bc1) )
		ROM_LOAD( "rc10.bin",     0x4800, 0x0800, CRC(ef94c502) SHA1(07c0504b2ebce0fa6e53e6957e7b6c0e9caab430) )
		ROM_LOAD( "rc11.bin",     0x5000, 0x0800, CRC(a3164b18) SHA1(7270af25fa4171f86476f5dc409e658da7fba7fc) )
		ROM_LOAD( "rc12.bin",     0x5800, 0x0800, CRC(2052f6d9) SHA1(036702fc40cf133eb374ed674695d7c6c79e8311) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_boothill = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "romh.cpu",     0x0000, 0x0800, CRC(1615d077) SHA1(e59a26c2f2fc67ab24301e22d2e3f33043acdf72) )
		ROM_LOAD( "romg.cpu",     0x0800, 0x0800, CRC(65a90420) SHA1(9f36c44b5ae5b912cdbbeb9ff11a42221b8362d2) )
		ROM_LOAD( "romf.cpu",     0x1000, 0x0800, CRC(3fdafd79) SHA1(b18e8ac9df40c4687ac1acd5174eb99f2ef60081) )
		ROM_LOAD( "rome.cpu",     0x1800, 0x0800, CRC(374529f4) SHA1(18c57b79df0c66052eef40a694779a5ade15d0e0) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_schaser = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "rt13.bin",     0x0000, 0x0400, CRC(0dfbde68) SHA1(7367b138ad8448aba9222fed632a892df65cecbd) )
		ROM_LOAD( "rt14.bin",     0x0400, 0x0400, CRC(5a508a25) SHA1(c681d0bbf49317e79b596fb094e66b8912f0e409) )
		ROM_LOAD( "rt15.bin",     0x0800, 0x0400, CRC(2ac43a93) SHA1(d364f0940681a888c0147e06bcb01f8a0d4a24c8) )
		ROM_LOAD( "rt16.bin",     0x0c00, 0x0400, CRC(f5583afc) SHA1(5e8edb43ccb138fd47ac8f3da1af79b4444a4a82) )
		ROM_LOAD( "rt17.bin",     0x1000, 0x0400, CRC(51cf1155) SHA1(fd8c82d951602fd7e0ada65fc7cdee9f277c70db) )
		ROM_LOAD( "rt18.bin",     0x1400, 0x0400, CRC(3f0fc73a) SHA1(b801c3f1e8e6e41c564432db7c5891f6b27293b2) )
		ROM_LOAD( "rt19.bin",     0x1800, 0x0400, CRC(b66ea369) SHA1(d277f572f9c7c4301518546cf60671a6539326ee) )
		ROM_LOAD( "rt20.bin",     0x1c00, 0x0400, CRC(e3a7466a) SHA1(2378970f38b0cec066ef853a6540500e468e4ab4) )
		ROM_LOAD( "rt21.bin",     0x4000, 0x0400, CRC(b368ac98) SHA1(6860efe0496955db67611183be0efecda92c9c98) )
		ROM_LOAD( "rt22.bin",     0x4400, 0x0400, CRC(6e060dfb) SHA1(614e2ecf676c3ea2f9ea869125cfffef2f713684) )
	
		ROM_REGION( 0x0400, REGION_PROMS, 0 )		/* background color map */
		ROM_LOAD( "rt06.ic2",     0x0000, 0x0400, CRC(950cf973) SHA1(d22df09b325835a0057ccd0d54f827b374254ac6) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_sflush = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )
		ROM_LOAD( "taitofr.005",        0xd800, 0x800, CRC(c4f08f9f) SHA1(997f216f5244942fc1a19f5c1988adbfadc301fc) )
		ROM_LOAD( "taitofr.004",        0xe000, 0x800, CRC(87a754a5) SHA1(07c0e2c3cb7aa0086d8f4dd202a452bc6c20d4ee) )
		ROM_LOAD( "taitofr.003",        0xe800, 0x800, CRC(5b12847f) SHA1(4b62342723dd49a387fae6637c331d7c853712a3) )
		ROM_LOAD( "taitofr.002",        0xf000, 0x800, CRC(291c9b1f) SHA1(7e5b3e1605581abf3d8165f4de9d4e32a5ee3bb0) )
		ROM_LOAD( "taitofr.001",        0xf800, 0x800, CRC(55d688c6) SHA1(574a3a2ca73cabb4b8f3444aa4464e6d64daa3ad) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_schasrcv = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "1",     		  0x0000, 0x0400, CRC(bec2b16b) SHA1(c62210ecb64d7c38e5b63481d7fe04eb59bb1068) )
		ROM_LOAD( "2",     		  0x0400, 0x0400, CRC(9d25e608) SHA1(4cc52a93a3ab96a0ec1d07593e17832fa59b30a1) )
		ROM_LOAD( "3",     		  0x0800, 0x0400, CRC(113d0635) SHA1(ab5e98d0b5fc37d7d69bb5c541681a0f66460440) )
		ROM_LOAD( "4",     		  0x0c00, 0x0400, CRC(f3a43c8d) SHA1(29a7a8b7d1de763a255cfec79157fd95e7bff551) )
		ROM_LOAD( "5",     		  0x1000, 0x0400, CRC(47c84f23) SHA1(61b475fa92b8335f8edd3a128d8ac8561658e464) )
		ROM_LOAD( "6",     		  0x1400, 0x0400, CRC(02ff2199) SHA1(e12c235b2064cb4bb426145172e523256e3c6358) )
		ROM_LOAD( "7",     		  0x1800, 0x0400, CRC(87d06b88) SHA1(2d743161f85e47cb8ee2a600cbee790b1ad7ad99) )
		ROM_LOAD( "8",     		  0x1c00, 0x0400, CRC(6dfaad08) SHA1(2184c4e2f4b6bffdc4fe13e178134331fcd43253) )
		ROM_LOAD( "9",     		  0x4000, 0x0400, CRC(3d1a2ae3) SHA1(672ad6590aebdfebc2748455fa638107f3934c41) )
		ROM_LOAD( "10",    		  0x4400, 0x0400, CRC(037edb99) SHA1(f2fc5e61f962666e7f6bb81753ac24ea0b97e581) )
	
		ROM_REGION( 0x0800, REGION_PROMS, 0 )		/* color maps player 1/player 2 (not used, but they were on the board) */
		ROM_LOAD( "cv01",         0x0000, 0x0400, CRC(037e16ac) SHA1(d585030aaff428330c91ae94d7cd5c96ebdd67dd) )
		ROM_LOAD( "cv02",         0x0400, 0x0400, CRC(8263da38) SHA1(2e7c769d129e6f8a1a31eba1e02777bb94ac32b2) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_spcenctr = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "4m33.cpu",     0x0000, 0x0800, CRC(7458b2db) SHA1(c4f41efb8a35fd8bebc75bff0111476affe2b34d) )
		ROM_LOAD( "4m32.cpu",     0x0800, 0x0800, CRC(1b873788) SHA1(6cdf0d602a65c7efcf8abe149c6172b4c7ab87a1) )
		ROM_LOAD( "4m31.cpu",     0x1000, 0x0800, CRC(d4319c91) SHA1(30830595c220f490fe150ad018fbf4671bb71e02) )
		ROM_LOAD( "4m30.cpu",     0x1800, 0x0800, CRC(9b9a1a45) SHA1(8023a05c13e8b541f9e2fe4d389e6a2dcd4766ea) )
		ROM_LOAD( "4m29.cpu",     0x4000, 0x0800, CRC(294d52ce) SHA1(0ee63413c5caf60d45ae8bef08f6c07099d30f79) )
		ROM_LOAD( "4m28.cpu",     0x4800, 0x0800, CRC(ce44c923) SHA1(9d35908de3194c5fe6fc8495ae413fa722018744) )
		ROM_LOAD( "4m27.cpu",     0x5000, 0x0800, CRC(098070ab) SHA1(72ae344591df0174353dc2e3d22daf5a70e2261f) )
		ROM_LOAD( "4m26.cpu",     0x5800, 0x0800, CRC(7f1d1f44) SHA1(2f4951171a55e7ac072742fa24eceeee6aca7e39) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_clowns = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "h2.cpu",       0x0000, 0x0400, CRC(ff4432eb) SHA1(997aee1e3669daa1d8169b4e103d04baaab8ea8d) )
		ROM_LOAD( "g2.cpu",       0x0400, 0x0400, CRC(676c934b) SHA1(72b681ca9ef23d820fdd297cc417932aecc9677b) )
		ROM_LOAD( "f2.cpu",       0x0800, 0x0400, CRC(00757962) SHA1(ef39211493393e97284a08eea63be0757643ac88) )
		ROM_LOAD( "e2.cpu",       0x0c00, 0x0400, CRC(9e506a36) SHA1(8aad486a72d148d8b03e7bec4c12abd14e425c5f) )
		ROM_LOAD( "d2.cpu",       0x1000, 0x0400, CRC(d61b5b47) SHA1(6051c0a2e81d6e975e82c2d48d0e52dc0d4723e3) )
		ROM_LOAD( "c2.cpu",       0x1400, 0x0400, CRC(154d129a) SHA1(61eebb319ee3a6be598b764b295c18a93a953c1e) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_clowns1 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "clownsv1.h",   0x0000, 0x0400, CRC(5560c951) SHA1(b6972e1918604263579de577ec58fa6a91e8ff3e) )
		ROM_LOAD( "clownsv1.g",   0x0400, 0x0400, CRC(6a571d66) SHA1(e825f95863e901a1b648c74bb47098c8e74f179b) )
		ROM_LOAD( "clownsv1.f",   0x0800, 0x0400, CRC(a2d56cea) SHA1(61bc07e6a24a1980216453b4dd2688695193a4ae) )
		ROM_LOAD( "clownsv1.e",   0x0c00, 0x0400, CRC(bbd606f6) SHA1(1cbaa21d9834c8d76cf335fd118851591e815c86) )
		ROM_LOAD( "clownsv1.d",   0x1000, 0x0400, CRC(37b6ff0e) SHA1(bf83bebb6c14b3663ca86a180f9ae3cddb84e571) )
		ROM_LOAD( "clownsv1.c",   0x1400, 0x0400, CRC(12968e52) SHA1(71e4f09d30b992a4ac44b0e88e83b4f8a0f63caa) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_gmissile = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "gm_623.h",     0x0000, 0x0800, CRC(a3ebb792) SHA1(30d9613de849c1a868056c5e28cf2a8608b63e88) )
		ROM_LOAD( "gm_623.g",     0x0800, 0x0800, CRC(a5e740bb) SHA1(963c0984953eb58fe7eab84fabb724ec6e29e706) )
		ROM_LOAD( "gm_623.f",     0x1000, 0x0800, CRC(da381025) SHA1(c9d0511567ed571b424459896ce7de0326850388) )
		ROM_LOAD( "gm_623.e",     0x1800, 0x0800, CRC(f350146b) SHA1(a07000a979b1a735754eca623cc880988924877f) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_seawolf = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "sw0041.h",     0x0000, 0x0400, CRC(8f597323) SHA1(b538277d3a633dd8a3179cff202f18d322e6fe17) )
		ROM_LOAD( "sw0042.g",     0x0400, 0x0400, CRC(db980974) SHA1(cc2a99b18695f61e0540c9f6bf8fe3b391dde4a0) )
		ROM_LOAD( "sw0043.f",     0x0800, 0x0400, CRC(e6ffa008) SHA1(385198434b08fe4651ad2c920d44fb49cfe0bc33) )
		ROM_LOAD( "sw0044.e",     0x0c00, 0x0400, CRC(c3557d6a) SHA1(bd345dd72fed8ce15da76c381782b025f71b006f) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_gunfight = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "7609h.bin",    0x0000, 0x0400, CRC(0b117d73) SHA1(99d01313e251818d336281700e206d9003c71dae) )
		ROM_LOAD( "7609g.bin",    0x0400, 0x0400, CRC(57bc3159) SHA1(c177e3f72db9af17ab99b2481448ca26318184b9) )
		ROM_LOAD( "7609f.bin",    0x0800, 0x0400, CRC(8049a6bd) SHA1(215b068663e431582591001cbe028929fa96d49f) )
		ROM_LOAD( "7609e.bin",    0x0c00, 0x0400, CRC(773264e2) SHA1(de3f2e6841122bbe6e2fda5b87d37842c072289a) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_280zzzap = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "zzzaph",       0x0000, 0x0400, CRC(1fa86e1c) SHA1(b9cf16eb037ada73631ed24297e9e3b3bf6ab3cd) )
		ROM_LOAD( "zzzapg",       0x0400, 0x0400, CRC(9639bc6b) SHA1(b2e2497e421e79a411d07ebf2eed2bb8dc227003) )
		ROM_LOAD( "zzzapf",       0x0800, 0x0400, CRC(adc6ede1) SHA1(206bf2575696c4b14437f3db37a215ba33211943) )
		ROM_LOAD( "zzzape",       0x0c00, 0x0400, CRC(472493d6) SHA1(ae5cf4481ee4b78ca0d2f4d560d295e922aa04a7) )
		ROM_LOAD( "zzzapd",       0x1000, 0x0400, CRC(4c240ee1) SHA1(972475f80253bb0d24773a10aec26a12f28e7c23) )
		ROM_LOAD( "zzzapc",       0x1400, 0x0400, CRC(6e85aeaf) SHA1(ffa6bb84ef1f7c2d72fd26c24bd33aa014aeab7e) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_lupin3 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "lp12.bin",     0x0000, 0x0800, CRC(68a7f47a) SHA1(dce99b3810331d7603fa468f1dea984e571f709b) )
		ROM_LOAD( "lp13.bin",     0x0800, 0x0800, CRC(cae9a17b) SHA1(a333ba7db45325996e3254ab36162bb7577e8a38) )
		ROM_LOAD( "lp14.bin",     0x1000, 0x0800, CRC(3553b9e4) SHA1(6affb5b6caf08f365c0dce669e44046295c3df91) )
		ROM_LOAD( "lp15.bin",     0x1800, 0x0800, CRC(acbeef64) SHA1(50d78cdc9938285b6bf9fa81fa0f6c30b23e0756) )
		ROM_LOAD( "lp16.bin",     0x4000, 0x0800, CRC(19fcdc54) SHA1(2f18ee8158321fff68886ffe793724001e8b18c2) )
		ROM_LOAD( "lp17.bin",     0x4800, 0x0800, CRC(66289ab2) SHA1(fc9b4a7b7a08d43f34beaf1a8e68ed0ff6148534) )
		ROM_LOAD( "lp18.bin",     0x5000, 0x0800, CRC(2f07b4ba) SHA1(982e4c437b39b45e23d15af1b2fc8c7aa3034559) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_polaris = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "ps-01",        0x0000, 0x0800, CRC(c04ce5a9) SHA1(62cc9b3b682ebecfb7600393862c65e26ff5263f) )
		ROM_LOAD( "ps-09",        0x0800, 0x0800, CRC(9a5c8cb2) SHA1(7a8c5d74f8b431072d9476d3ef65a3fe1d639813) )
		ROM_LOAD( "ps-08",        0x1000, 0x0800, CRC(8680d7ea) SHA1(7fd4b8a415666c36842fed80d2798b48f8b29d0d) )
		ROM_LOAD( "ps-04",        0x1800, 0x0800, CRC(65694948) SHA1(de92a7f3e3ef732b573254baa60df60f8e068a5d) )
		ROM_LOAD( "ps-05",        0x4000, 0x0800, CRC(772e31f3) SHA1(fa0b866b6df1a9217e286ca880b3bb3fb0644bf3) )
		ROM_LOAD( "ps-10",        0x4800, 0x0800, CRC(3df77bac) SHA1(b3275c34b8d42df83df2c404c5b7d220aae651fa) )
	
		ROM_REGION( 0x0400, REGION_PROMS, 0 )		/* background color map */
		ROM_LOAD( "ps07",         0x0000, 0x0400, CRC(164aa05d) SHA1(41c699ce45c76a60c71294f25d8df6c6e6c1280a) )
	
		ROM_REGION( 0x0100, REGION_USER1, 0 )		/* cloud graphics */
		ROM_LOAD( "mb7052.2c",    0x0000, 0x0100, CRC(2953253b) SHA1(2fb851bc9652ca4e51d473b484ede6dab05f1b51) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_polarisa = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "ps01-1",       0x0000, 0x0800, CRC(7d41007c) SHA1(168f002fe997aac6e4141292de826d389859bb04) )
		ROM_LOAD( "ps-09",        0x0800, 0x0800, CRC(9a5c8cb2) SHA1(7a8c5d74f8b431072d9476d3ef65a3fe1d639813) )
		ROM_LOAD( "ps03-1",       0x1000, 0x0800, CRC(21f32415) SHA1(6ac9ae9b55e342729fe260147021ed3911a24dc2) )
		ROM_LOAD( "ps-04",        0x1800, 0x0800, CRC(65694948) SHA1(de92a7f3e3ef732b573254baa60df60f8e068a5d) )
		ROM_LOAD( "ps-05",        0x4000, 0x0800, CRC(772e31f3) SHA1(fa0b866b6df1a9217e286ca880b3bb3fb0644bf3) )
		ROM_LOAD( "ps-10",        0x4800, 0x0800, CRC(3df77bac) SHA1(b3275c34b8d42df83df2c404c5b7d220aae651fa) )
		ROM_LOAD( "ps26",         0x5000, 0x0800, CRC(9d5c3d50) SHA1(a6acf9ca6e807625156cb1759269014d5830a44f) )
	
		ROM_REGION( 0x0400, REGION_PROMS, 0 )		/* background color map */
		ROM_LOAD( "ps07",         0x0000, 0x0400, CRC(164aa05d) SHA1(41c699ce45c76a60c71294f25d8df6c6e6c1280a) )
	
		ROM_REGION( 0x0100, REGION_USER1, 0 )		/* cloud graphics */
		ROM_LOAD( "mb7052.2c",    0x0000, 0x0100, CRC(2953253b) SHA1(2fb851bc9652ca4e51d473b484ede6dab05f1b51) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_lagunar = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "lagunar.h",    0x0000, 0x0800, CRC(0cd5a280) SHA1(89a744c912070f11b0b90b0cc92061e238b00b64) )
		ROM_LOAD( "lagunar.g",    0x0800, 0x0800, CRC(824cd6f5) SHA1(a74f6983787cf040eab6f19de2669c019962b9cb) )
		ROM_LOAD( "lagunar.f",    0x1000, 0x0800, CRC(62692ca7) SHA1(d62051bd1b45ca6e60df83942ff26a64ae25a97b) )
		ROM_LOAD( "lagunar.e",    0x1800, 0x0800, CRC(20e098ed) SHA1(e0c52c013f5e93794b363d7762ce0f34ba98c660) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_m4 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "m4.h",         0x0000, 0x0800, CRC(9ee2a0b5) SHA1(b81b4001c90ac6db25edd838652c42913022d9a9) )
		ROM_LOAD( "m4.g",         0x0800, 0x0800, CRC(0e84b9cb) SHA1(a7b74851979aaaa16496e506c487a18df14ab6dc) )
		ROM_LOAD( "m4.f",         0x1000, 0x0800, CRC(9ded9956) SHA1(449204a50efd3345cde815ca5f1fb596843a30ac) )
		ROM_LOAD( "m4.e",         0x1800, 0x0800, CRC(b6983238) SHA1(3f3b99b33135e144c111d2ebaac8f9433c269bc5) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_phantom2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "phantom2.h",   0x0000, 0x0800, CRC(0e3c2439) SHA1(450182e590845c651530b2c84e1f11fe2451dcf6) )
		ROM_LOAD( "phantom2.g",   0x0800, 0x0800, CRC(e8df3e52) SHA1(833925e44e686df4d4056bce4c0ffae3269d57df) )
		ROM_LOAD( "phantom2.f",   0x1000, 0x0800, CRC(30e83c6d) SHA1(fe34a3e4519a7e5ffe66e76fe974049988656b71) )
		ROM_LOAD( "phantom2.e",   0x1800, 0x0800, CRC(8c641cac) SHA1(c4986daacb7ed9efed59b022c6101240b0eddcdc) )
	
		ROM_REGION( 0x0800, REGION_PROMS, 0 )	   /* cloud graphics */
		ROM_LOAD( "p2clouds",     0x0000, 0x0800, CRC(dcdd2927) SHA1(d8d42c6594e36c12b40ee6342a9ad01a8bbdef75) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_dogpatch = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "dogpatch.h",   0x0000, 0x0800, CRC(74ebdf4d) SHA1(6b31f9563b0f79fe9128ee83e85a3e2f90d7985b) )
		ROM_LOAD( "dogpatch.g",   0x0800, 0x0800, CRC(ac246f70) SHA1(7ee356c3218558a78ee0ff495f9f51ef88cac951) )
		ROM_LOAD( "dogpatch.f",   0x1000, 0x0800, CRC(a975b011) SHA1(fb807d9eefde7177d7fd7ab06fc2dbdc58ae6fcb) )
		ROM_LOAD( "dogpatch.e",   0x1800, 0x0800, CRC(c12b1f60) SHA1(f0504e16d2ce60a0fb3fc2af8c323bfca0143818) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_bowler = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "h.cpu",        0x0000, 0x0800, CRC(74c29b93) SHA1(9cbd5b7b8a4c889406b6bc065360f74c036320b2) )
		ROM_LOAD( "g.cpu",        0x0800, 0x0800, CRC(ca26d8b4) SHA1(cf18991cde8044a961cf556f18c6eb60a7ade595) )
		ROM_LOAD( "f.cpu",        0x1000, 0x0800, CRC(ba8a0bfa) SHA1(bb017ddac58d031b249596b70ab1068cd1bad499) )
		ROM_LOAD( "e.cpu",        0x1800, 0x0800, CRC(4da65a40) SHA1(7795d59870fa722da89888e72152145662554080) )
		ROM_LOAD( "d.cpu",        0x4000, 0x0800, CRC(e7dbc9d9) SHA1(05049a69ee588de85db86df188e7670778b77e90) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_shuffle = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "shuffle.h",    0x0000, 0x0800, CRC(0d422a18) SHA1(909c5b9e3c1194abd101cbf993a2ed7c8fbeb5d0) )
		ROM_LOAD( "shuffle.g",    0x0800, 0x0800, CRC(7db7fcf9) SHA1(f41b568f2340e5307a7a45658946cfd4cf4056bf) )
		ROM_LOAD( "shuffle.f",    0x1000, 0x0800, CRC(cd04d848) SHA1(f0f7e9bc483f08934d5c29568b4a7fe084623031) )
		ROM_LOAD( "shuffle.e",    0x1800, 0x0800, CRC(2c118357) SHA1(178db02aaa70963dd8dbcb9b8651209913c539af) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_blueshrk = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "blueshrk.h",   0x0000, 0x0800, CRC(4ff94187) SHA1(7cb80e2ccc34983bfd688c549ffc032d6dacf880) )
		ROM_LOAD( "blueshrk.g",   0x0800, 0x0800, CRC(e49368fd) SHA1(2495ba48532bb714361e4f0e94c9317161c6c77f) )
		ROM_LOAD( "blueshrk.f",   0x1000, 0x0800, CRC(86cca79d) SHA1(7b4633fb8033ee2c0e692135c383ebf57deef0e5) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_einnings = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "ei.h",         0x0000, 0x0800, CRC(eff9c7af) SHA1(316fffc972bd9935ead5ee4fd629bddc8a8ed5ce) )
		ROM_LOAD( "ei.g",         0x0800, 0x0800, CRC(5d1e66cb) SHA1(a5475362e12b7c251a05d67c2fd070cf7d333ad0) )
		ROM_LOAD( "ei.f",         0x1000, 0x0800, CRC(ed96785d) SHA1(d5557620227fcf6f30dcf6c8f5edd760d77d30ae) )
		ROM_LOAD( "ei.e",         0x1800, 0x0800, CRC(ad096a5d) SHA1(81d48302a0e039b8601a6aed7276e966592af693) )
		ROM_LOAD( "ei.b",         0x5000, 0x0800, CRC(56b407d4) SHA1(95e4be5b2f28192df85c6118079de2e68838b67c) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_dplay = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "dplay619.h",   0x0000, 0x0800, CRC(6680669b) SHA1(49ad2333f81613c2f27231de60b415cbc254546a) )
		ROM_LOAD( "dplay619.g",   0x0800, 0x0800, CRC(0eec7e01) SHA1(2661e77061119d7d95d498807bd29d2630c6b6ab) )
		ROM_LOAD( "dplay619.f",   0x1000, 0x0800, CRC(3af4b719) SHA1(3122138ac36b1a129226836ddf1916d763d73e10) )
		ROM_LOAD( "dplay619.e",   0x1800, 0x0800, CRC(65cab4fc) SHA1(1ce7cb832e95e4a6d0005bf730eec39225b2e960) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_maze = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "invaders.h",   0x0000, 0x0800, CRC(f2860cff) SHA1(62b3fd3d04bf9c5dd9b50964374fb884dc0ab79c) )
		ROM_LOAD( "invaders.g",   0x0800, 0x0800, CRC(65fad839) SHA1(893f0a7621e7df19f777be991faff0db4a9ad571) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_tornbase = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "tb.h",         0x0000, 0x0800, CRC(653f4797) SHA1(feb4c802aa3e0c2a66823cd032496cca5742c883) )
		ROM_LOAD( "tb.g",         0x0800, 0x0800, BAD_DUMP CRC(33468006) SHA1(cc54da39ef14df6fa5e4e10a4798158a9a7f867e)  )	/* this ROM fails the test */
		ROM_LOAD( "tb.f",         0x1000, 0x0800, CRC(215e070c) SHA1(425915b37e5315f9216707de0850290145f69a30) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_checkmat = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "checkmat.h",   0x0000, 0x0400, CRC(3481a6d1) SHA1(f758599d6393398a6a8e6e7399dc1a3862604f65) )
		ROM_LOAD( "checkmat.g",   0x0400, 0x0400, CRC(df5fa551) SHA1(484ff9bfb95166ba09f34c753a7908a73de3cc7d) )
		ROM_LOAD( "checkmat.f",   0x0800, 0x0400, CRC(25586406) SHA1(39e0cf502735819a7e1d933e3686945fcfae21af) )
		ROM_LOAD( "checkmat.e",   0x0c00, 0x0400, CRC(59330d84) SHA1(453f95dd31968d439339c41e625481170437eb0f) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_desertgu = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "desertgu.h",   0x0000, 0x0800, CRC(c0030d7c) SHA1(4d0a3a59d4f8181c6e30966a6b1d19ba5b29c398) )
		ROM_LOAD( "desertgu.g",   0x0800, 0x0800, CRC(1ddde10b) SHA1(8fb8e85844a8ec6c0722883013ecdd4eeaeb08c1) )
		ROM_LOAD( "desertgu.f",   0x1000, 0x0800, CRC(808e46f1) SHA1(1cc4e9b0aa7e9546c133bd40d40ede6f2fbe93ba) )
		ROM_LOAD( "desertgu.e",   0x1800, 0x0800, CRC(ac64dc62) SHA1(202433dfb174901bd3b91e843d9d697a8333ef9e) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_ozmawars = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "mw01",         0x0000, 0x0800, CRC(31f4397d) SHA1(bba9765aadd608d19e2515a5edf8e0eceb70916a) )
		ROM_LOAD( "mw02",         0x0800, 0x0800, CRC(d8e77c62) SHA1(84fc81cf9a924ecbb13a008cd7435b7d465bddf6) )
		ROM_LOAD( "mw03",         0x1000, 0x0800, CRC(3bfa418f) SHA1(7318878202322a2263551ca463e4c70943401f68) )
		ROM_LOAD( "mw04",         0x1800, 0x0800, CRC(e190ce6c) SHA1(120898e9a683f5ce874c6fde761570a26de2fa8c) )
		ROM_LOAD( "mw05",         0x4000, 0x0800, CRC(3bc7d4c7) SHA1(b084f8cd2ce0f502c2e915da3eceffcbb448e9c0) )
		ROM_LOAD( "mw06",         0x4800, 0x0800, CRC(99ca2eae) SHA1(8d0f220f68043eff0c85d2de7bee7fd4365fb51c) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_ozmawar2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "mw01",         0x0000, 0x0800, CRC(31f4397d) SHA1(bba9765aadd608d19e2515a5edf8e0eceb70916a) )
		ROM_LOAD( "mw02",         0x0800, 0x0800, CRC(d8e77c62) SHA1(84fc81cf9a924ecbb13a008cd7435b7d465bddf6) )
		ROM_LOAD( "oz5",          0x1000, 0x0400, CRC(5597bf52) SHA1(626c7348365ed974d416485d94d057745b5d9b96) )
		ROM_LOAD( "oz6",          0x1400, 0x0400, CRC(19b43578) SHA1(3609b7c77f5ee6f10f302892f56fcc8375577f20) )
		ROM_LOAD( "oz7",          0x1800, 0x0400, CRC(a285bfde) SHA1(ed7a9fce4d887d3b5d596645893ea87c0bafda02) )
		ROM_LOAD( "oz8",          0x1c00, 0x0400, CRC(ae59a629) SHA1(0c9ea67dc35f93ec65ec91e1dab2e4b6212428bf) )
		ROM_LOAD( "mw05",         0x4000, 0x0800, CRC(3bc7d4c7) SHA1(b084f8cd2ce0f502c2e915da3eceffcbb448e9c0) )
		ROM_LOAD( "oz11",         0x4800, 0x0400, CRC(660e934c) SHA1(1d50ae3a9de041b908e256892203ce1738d588f6) )
		ROM_LOAD( "oz12",         0x4c00, 0x0400, CRC(8b969f61) SHA1(6d12cacc73c31a897812ccd8de24725ee56dd975) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_solfight = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "solfight.m",   0x0000, 0x0800, CRC(a4f2814e) SHA1(e2437e3543dcc97eeaea32babcd4aec6455581ac) )
		ROM_LOAD( "solfight.n",   0x0800, 0x0800, CRC(5657ec07) SHA1(9a2fb398841160f59483bb70060caba37addb8a4) )
		ROM_LOAD( "solfight.p",   0x1000, 0x0800, CRC(ef9ce96d) SHA1(96867b4f2d72f3a8827b1eb3a0748922eaa8d608) )
		ROM_LOAD( "solfight.r",   0x1800, 0x0800, CRC(4f1ef540) SHA1(a798e57959e72bfb554dd2fed0e37027312f9ed3) )
		ROM_LOAD( "mw05",         0x4000, 0x0800, CRC(3bc7d4c7) SHA1(b084f8cd2ce0f502c2e915da3eceffcbb448e9c0) )
		ROM_LOAD( "solfight.t",   0x4800, 0x0800, CRC(3b6fb206) SHA1(db631f4a0bd5344d130ff8d723d949e9914b6f92) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_spaceph = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "sv01.bin",     0x0000, 0x0400, CRC(de84771d) SHA1(13a7e5eedb826cca4d59634d38db9fcf5e65b732) )
		ROM_LOAD( "sv02.bin",     0x0400, 0x0400, CRC(957fc661) SHA1(ac0edc901d8033619f62967f8eaf53a02947e109) )
		ROM_LOAD( "sv03.bin",     0x0800, 0x0400, CRC(dbda38b9) SHA1(73a277616a0c236b07c9ffa66f16a27a78c12d70) )
		ROM_LOAD( "sv04.bin",     0x0c00, 0x0400, CRC(f51544a5) SHA1(368411a2dadaebcbb4d5b6cf6c2beec036ce817f) )
		ROM_LOAD( "sv05.bin",     0x1000, 0x0400, CRC(98d02683) SHA1(f13958df8d385f532e993e4c34569d992904a4ed) )
		ROM_LOAD( "sv06.bin",     0x1400, 0x0400, CRC(4ec390fd) SHA1(ade23efde5d55d282fbb28a5f8a1346601501b79) )
		ROM_LOAD( "sv07.bin",     0x1800, 0x0400, CRC(170862fd) SHA1(ac64a97b1510ca81d4ef3a5fcf45b7e6c7414914) )
		ROM_LOAD( "sv08.bin",     0x1c00, 0x0400, CRC(511b12cf) SHA1(08ba43024c8574ded11aa457eca24b72984f5ea9) )
		ROM_LOAD( "sv09.bin",     0x4000, 0x0400, CRC(af1cd1af) SHA1(286d77e8556e475b291a3b1a53acaca8b7dc3678) )
		ROM_LOAD( "sv10.bin",     0x4400, 0x0400, CRC(31b7692e) SHA1(043880750d134d04311eab55e30ee223977d3d17) )
		ROM_LOAD( "sv11.bin",     0x4800, 0x0400, CRC(50257351) SHA1(5c3eb29f36f04b7fb8f0351ccf9c8cfc7587f927) )
		ROM_LOAD( "sv12.bin",     0x4c00, 0x0400, CRC(a2a3366a) SHA1(87032787450216d378406122effa95ea01145bf7) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_ballbomb = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "tn01",         0x0000, 0x0800, CRC(551585b5) SHA1(7c17b046bdfca6ab107b7e68ba9bde6ca590c3d4) )
		ROM_LOAD( "tn02",         0x0800, 0x0800, CRC(7e1f734f) SHA1(a15656818cd730d9bc98d00ff1e7fe3f860bd624) )
		ROM_LOAD( "tn03",         0x1000, 0x0800, CRC(d93e20bc) SHA1(2bf72f813750cef8fad572a18fb8e9fd5bf38804) )
		ROM_LOAD( "tn04",         0x1800, 0x0800, CRC(d0689a22) SHA1(1f6b258431b7eb878853ff979e4d97a05fb6b797) )
		ROM_LOAD( "tn05-1",       0x4000, 0x0800, CRC(5d5e94f1) SHA1(b9f8ba38161ef4f0940c274e9d93fed4bb7db017) )
	
		ROM_REGION( 0x0800, REGION_PROMS, 0 )		/* color maps player 1/player 2 */
		ROM_LOAD( "tn06",         0x0000, 0x0400, CRC(7ec554c4) SHA1(b638605ba2043fdca4c5e18755fa5fa81ed3db07) )
		ROM_LOAD( "tn07",         0x0400, 0x0400, CRC(deb0ac82) SHA1(839581c4e58cb7b0c2c14cf4f239220017cc26eb) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_yosakdon = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "yd1.bin", 	  0x0000, 0x0400, CRC(607899c9) SHA1(219c0c99894715818606fba49cc75517f6f43e0c) )
		ROM_LOAD( "yd2.bin", 	  0x0400, 0x0400, CRC(78336df4) SHA1(b0b6254568d191d2d0b9c9280a3ccf2417ef3f38) )
		ROM_LOAD( "yd3.bin", 	  0x0800, 0x0400, CRC(c5af6d52) SHA1(c40af79fe060562c64fc316881b7d0348e11ee3f) )
		ROM_LOAD( "yd4.bin", 	  0x0c00, 0x0400, CRC(dca8064f) SHA1(77a58137cc7f0b5fbe0e9e8deb9c5be88b1ebbcf) )
		ROM_LOAD( "yd5.bin", 	  0x1400, 0x0400, CRC(38804ff1) SHA1(9b7527b9d2b106355f0c8df46666b1e3f286b2e3) )
		ROM_LOAD( "yd6.bin", 	  0x1800, 0x0400, CRC(988d2362) SHA1(deaf864b4e287cbc2585c2a11343b1ae82e15463) )
		ROM_LOAD( "yd7.bin", 	  0x1c00, 0x0400, CRC(2744e68b) SHA1(5ad5a7a615d36f57b6d560425e035c15e25e9005) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_sheriff = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )             /* 64k for code */
		ROM_LOAD( "f1",           0x0000, 0x0400, CRC(e79df6e8) SHA1(908176de9bfc3d48e2da9af6ba7ebdee698ec2de) )
		ROM_LOAD( "f2",           0x0400, 0x0400, CRC(da67721a) SHA1(ee6a5fb98da1d1fcfad0ef27af300473a637f578) )
		ROM_LOAD( "g1",           0x0800, 0x0400, CRC(3fb7888e) SHA1(2c2d6b27d577d5ccf759e451e53c2e3314af40f6) )
		ROM_LOAD( "g2",           0x0c00, 0x0400, CRC(585fcfee) SHA1(82f2abc14f893c092b80da45fc297fa5fb0890b5) )
		ROM_LOAD( "h1",           0x1000, 0x0400, CRC(e59eab52) SHA1(aa87710237dd48d1831f1b307d547b1b0707cd4e) )
		ROM_LOAD( "h2",           0x1400, 0x0400, CRC(79e69a6a) SHA1(1780ce77d7d9ddbf4aceabe0fcf079339837bbe1) )
		ROM_LOAD( "i1",           0x1800, 0x0400, CRC(dda7d1e8) SHA1(bd2a7388e81c71922b2e97d68be71359a75e8d37) )
		ROM_LOAD( "i2",           0x1c00, 0x0400, CRC(5c5f3f86) SHA1(25c64ccb7d0e136f67d6e1da7927ae6d89e0ceb9) )
		ROM_LOAD( "j1",           0x2000, 0x0400, CRC(0aa8b79a) SHA1(aed139e8c8ba912823c57fe4cc7231b2d638f479) )
	
		ROM_REGION( 0x1000, REGION_CPU2, 0 )	/* Sound 8035 + 76477 Sound Generator */
		ROM_LOAD( "basnd.u2",     0x0000, 0x0400, CRC(75731745) SHA1(538a63c9c60f1886fca4caf3eb1e0bada2d3f162) )
	
		ROM_REGION( 0x0800, REGION_PROMS, 0 )	/* color maps player 1/player 2 (missing) */
		ROM_LOAD( "sheriff.cl1",  0x0000, 0x0400, NO_DUMP )	/* no idea about the # of */
		ROM_LOAD( "sheriff.cl2",  0x0400, 0x0400, NO_DUMP )  /* PROMs or the size */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_bandido = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )             /* 64k for code */
		ROM_LOAD( "baf1-3",       0x0000, 0x0400, CRC(aec94829) SHA1(aa6d241670ea061bac4a71dff82dfa832095eae6) )
		ROM_LOAD( "f2",           0x0400, 0x0400, CRC(da67721a) SHA1(ee6a5fb98da1d1fcfad0ef27af300473a637f578) )
		ROM_LOAD( "g1",           0x0800, 0x0400, CRC(3fb7888e) SHA1(2c2d6b27d577d5ccf759e451e53c2e3314af40f6) )
		ROM_LOAD( "g2",           0x0c00, 0x0400, CRC(585fcfee) SHA1(82f2abc14f893c092b80da45fc297fa5fb0890b5) )
		ROM_LOAD( "bah1-1",       0x1000, 0x0400, CRC(5cb63677) SHA1(59a8e5f8b134bf44d3e5a1105a9346f0c5f9378e) )
		ROM_LOAD( "h2",           0x1400, 0x0400, CRC(79e69a6a) SHA1(1780ce77d7d9ddbf4aceabe0fcf079339837bbe1) )
		ROM_LOAD( "i1",           0x1800, 0x0400, CRC(dda7d1e8) SHA1(bd2a7388e81c71922b2e97d68be71359a75e8d37) )
		ROM_LOAD( "i2",           0x1c00, 0x0400, CRC(5c5f3f86) SHA1(25c64ccb7d0e136f67d6e1da7927ae6d89e0ceb9) )
		ROM_LOAD( "j1",           0x2000, 0x0400, CRC(0aa8b79a) SHA1(aed139e8c8ba912823c57fe4cc7231b2d638f479) )
		ROM_LOAD( "baj2-2",       0x2400, 0x0400, CRC(a10b848a) SHA1(c045f1f6a11cbf49a1bae06c701b659d587292a3) )
	
		ROM_REGION( 0x1000, REGION_CPU2, 0 )	/* Sound 8035 + 76477 Sound Generator */
		ROM_LOAD( "basnd.u2",     0x0000, 0x0400, CRC(75731745) SHA1(538a63c9c60f1886fca4caf3eb1e0bada2d3f162) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_helifire = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )             /* 64k for code */
		ROM_LOAD( "tub.f1b",      0x0000, 0x0400, CRC(032f89ca) SHA1(63b0310875ed78a6385e44eea781ddcc4a63557c) )
		ROM_LOAD( "tub.f2b",      0x0400, 0x0400, CRC(2774e70f) SHA1(98d845e80db61799493dbebe8db801567277432c) )
		ROM_LOAD( "tub.g1b",      0x0800, 0x0400, CRC(b5ad6e8a) SHA1(1eb4931e85bd6a559e85a2b978d383216d3988a7) )
		ROM_LOAD( "tub.g2b",      0x0c00, 0x0400, CRC(5e015bf4) SHA1(60f5a9707c8655e54a8381afd764856fb25c29f1) )
		ROM_LOAD( "tub.h1b",      0x1000, 0x0400, CRC(23bb4e5a) SHA1(b59bc0adff3635aca1def2b1997f7edc6ca7e8ee) )
		ROM_LOAD( "tub.h2b",      0x1400, 0x0400, CRC(358227c6) SHA1(d7bd678ef1737edc6aa609e43e3ae96a8d61dc15) )
		ROM_LOAD( "tub.i1b",      0x1800, 0x0400, CRC(0c679f44) SHA1(cbe31dbe5f2c5f11a637cb3bde4e059c310d0e76) )
		ROM_LOAD( "tub.i2b",      0x1c00, 0x0400, CRC(d8b7a398) SHA1(3ddfeac39147d5df6096f525f7ef67abef32a28b) )
		ROM_LOAD( "tub.j1b",      0x2000, 0x0400, CRC(98ef24db) SHA1(70ad8dd6e1e8f4bf4ce431737ca1856eecc03d53) )
		ROM_LOAD( "tub.j2b",      0x2400, 0x0400, CRC(5e2b5877) SHA1(f7c747e8a1d9fe2dda71ee6304636cf3cdf727a7) )
	
		ROM_REGION( 0x1000, REGION_CPU2, 0 )	/* Sound 8035 */
		ROM_LOAD( "tub.snd",      0x0000, 0x0400, CRC(9d77a31f) SHA1(36db9b5087b6661de88042854874bc247c92d985) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_helifira = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )             /* 64k for code */
		ROM_LOAD( "f1a.bin",      0x0000, 0x0400, CRC(92c9d6c1) SHA1(860a7b3980e9e11d48769fad347c965e04ed3f89) )
		ROM_LOAD( "f2a.bin",      0x0400, 0x0400, CRC(a264dde8) SHA1(48f972ad5af6c2ab61117f60d9244df6df6d313c) )
		ROM_LOAD( "tub.g1b",      0x0800, 0x0400, CRC(b5ad6e8a) SHA1(1eb4931e85bd6a559e85a2b978d383216d3988a7) )
		ROM_LOAD( "g2a.bin",      0x0c00, 0x0400, CRC(a987ebcd) SHA1(46726293c308c18b28941809419ba4c2ffc8084f) )
		ROM_LOAD( "h1a.bin",      0x1000, 0x0400, CRC(25abcaf0) SHA1(a14c795de1fc283405f71bb83f4ac5c98fd406cb) )
		ROM_LOAD( "tub.h2b",      0x1400, 0x0400, CRC(358227c6) SHA1(d7bd678ef1737edc6aa609e43e3ae96a8d61dc15) )
		ROM_LOAD( "tub.i1b",      0x1800, 0x0400, CRC(0c679f44) SHA1(cbe31dbe5f2c5f11a637cb3bde4e059c310d0e76) )
		ROM_LOAD( "i2a.bin",      0x1c00, 0x0400, CRC(296610fd) SHA1(f1ab379983e45f3cd718dd82962c609297b4dcb8) )
		ROM_LOAD( "tub.j1b",      0x2000, 0x0400, CRC(98ef24db) SHA1(70ad8dd6e1e8f4bf4ce431737ca1856eecc03d53) )
		ROM_LOAD( "tub.j2b",      0x2400, 0x0400, CRC(5e2b5877) SHA1(f7c747e8a1d9fe2dda71ee6304636cf3cdf727a7) )
	
		ROM_REGION( 0x1000, REGION_CPU2, 0 )	/* Sound 8035 + 76477 Sound Generator */
		ROM_LOAD( "tub.snd",      0x0000, 0x0400, CRC(9d77a31f) SHA1(36db9b5087b6661de88042854874bc247c92d985) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_spacefev = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )             /* 64k for code */
		ROM_LOAD( "f1.bin",       0x0000, 0x0400, CRC(35f295bd) SHA1(34d1df25fcdea598ca1191cecc2125e6f63dbce3) )
		ROM_LOAD( "f2.bin",       0x0400, 0x0400, CRC(0c633f4c) SHA1(a551ddbf21670fb1f000404b92da87a97f7ba157) )
		ROM_LOAD( "g1.bin",       0x0800, 0x0400, CRC(f3d851cb) SHA1(535c52a56e54a064aa3d1c48a129f714234a1007) )
		ROM_LOAD( "g2.bin",       0x0c00, 0x0400, CRC(1faef63a) SHA1(68e1bfc45587bfb1ee2eb477b60efd4f69dffd2c) )
		ROM_LOAD( "h1.bin",       0x1000, 0x0400, CRC(b365389d) SHA1(e681f2c5e37cc07912915ef74184ff9336309de3) )
		ROM_LOAD( "h2.bin",       0x1400, 0x0400, CRC(a163e800) SHA1(e8817f3e17f099a0dc66213d2d3d3fdeb117b10e) )
		ROM_LOAD( "i1.bin",       0x1800, 0x0400, CRC(00027be2) SHA1(551a779a2e5a6455b7a348d246731c094e0ec709) )
	
		ROM_REGION( 0x1000, REGION_CPU2, 0 )	/* Sound 8035 + 76477 Sound Generator */
		ROM_LOAD( "ss3.ic2",      0x0000, 0x0400, CRC(95c2c1ee) SHA1(42a3a382fc7d2782052372d71f6d0e8a153e74d0) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )	/* ? */
		ROM_LOAD( "f5.bpr",       0x0000, 0x0020, CRC(c5914ec1) SHA1(198875fcab36d09c8726bb21e2fdff9882f6721a) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_sfeverbw = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )             /* 64k for code */
		ROM_LOAD( "spacefev.f1",  0x0000, 0x0400, CRC(b8887351) SHA1(ccd49937f1cbd7a157b3715474ccc3e8fdcea2b2) )
		ROM_LOAD( "spacefev.f2",  0x0400, 0x0400, CRC(cda933a7) SHA1(a0447c8c98e24674081c9bf4b1ef07dc186c6e2b) )
		ROM_LOAD( "spacefev.g1",  0x0800, 0x0400, CRC(de17578a) SHA1(d9d5dbf38331f212d2a566c60756a788e169104d) )
		ROM_LOAD( "spacefev.g2",  0x0c00, 0x0400, CRC(f1a90948) SHA1(850f27b42ca12bcba4aa95a1ad3e66206fa63554) )
		ROM_LOAD( "spacefev.h1",  0x1000, 0x0400, CRC(eefb4273) SHA1(853a62976a406516f10ac68dc2859399b8b7aae8) )
		ROM_LOAD( "spacefev.h2",  0x1400, 0x0400, CRC(e91703e8) SHA1(f58606b0c7d945e94c3fccc7ebe17ca25675e6a0) )
		ROM_LOAD( "spacefev.i1",  0x1800, 0x0400, CRC(41e18df9) SHA1(2212c836313775e7c507a875672c0b3635825e02) )
		ROM_LOAD( "spacefev.i2",  0x1c00, 0x0400, CRC(eff9f82d) SHA1(5004e52dfa652ceefca9ed4210c0fa8f0591dc08) )
	
		ROM_REGION( 0x1000, REGION_CPU2, 0 )	/* Sound 8035 + 76477 Sound Generator */
		ROM_LOAD( "ss3.ic2",      0x0000, 0x0400, CRC(95c2c1ee) SHA1(42a3a382fc7d2782052372d71f6d0e8a153e74d0) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_sstrangr = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "hss-01.58",     0x0000, 0x0400, CRC(feec7600) SHA1(787a6be4e24ce931e7678e777699b9f6789bc199) )
		ROM_LOAD( "hss-02.59",     0x0400, 0x0400, CRC(7281ff0b) SHA1(56649d1362be1b9f517cb8616cbf9e4f955e9a2d) )
		ROM_LOAD( "hss-03.60",     0x0800, 0x0400, CRC(a09ec572) SHA1(9c4ad811a6c0460403f9cdc9fe5381c460249ff5) )
		ROM_LOAD( "hss-04.61",     0x0c00, 0x0400, CRC(ec411aca) SHA1(b72eb6f7c3d69e2829280d1ab982099f6eff0bde) )
		ROM_LOAD( "hss-05.62",     0x1000, 0x0400, CRC(7b1b81dd) SHA1(3fa6e244e203fb75f92b19db7b4b18645b3f66a3) )
		ROM_LOAD( "hss-06.63",     0x1400, 0x0400, CRC(de383625) SHA1(7ec0d7171e771c4b43e026f3f50a88d8ab2236bb) )
		ROM_LOAD( "hss-07.64",     0x1800, 0x0400, CRC(2e41d0f0) SHA1(bba720b0c5a7bd47abb8bc8498a989e17dc52428) )
		ROM_LOAD( "hss-08.65",     0x1c00, 0x0400, CRC(bd14d0b0) SHA1(9665f639afef9c1291f2efc054216ff44c595b45) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_sstrngr2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code */
		ROM_LOAD( "4764.09",      0x0000, 0x2000, CRC(d88f86cc) SHA1(9f284ee50caf3c64bd04a79a798de620348881bc) )
		ROM_LOAD( "2708.10",      0x6000, 0x0400, CRC(eba304c1) SHA1(3fa6fbb29fa46c146283f69a712bfc51cbb2a43c) )
	
		ROM_REGION( 0x0400, REGION_PROMS, 0 )		/* color maps player 1/player 2 */
		ROM_LOAD( "2708.15",      0x0000, 0x0400, CRC(c176a89d) SHA1(955dd540dc3787091c3f34ae122a13e6b7523414) )
	ROM_END(); }}; 
	
	
	/* Midway games */
	
	/* board #            rom       parent    machine   inp       init (overlay/color hardware setup) */
	
	/* 596 */ public static GameDriver driver_seawolf	   = new GameDriver("1976"	,"seawolf"	,"_8080bw.java"	,rom_seawolf,null	,machine_driver_seawolf	,input_ports_seawolf	,init_seawolf	,ROT0,   "Midway", "Sea Wolf", GAME_IMPERFECT_SOUND )
	/* 597 */ public static GameDriver driver_gunfight	   = new GameDriver("1975"	,"gunfight"	,"_8080bw.java"	,rom_gunfight,null	,machine_driver_gunfight	,input_ports_gunfight	,init_gunfight	,ROT0,   "Midway", "Gun Fight", GAME_NO_SOUND )
	/* 605 */ public static GameDriver driver_tornbase	   = new GameDriver("1976"	,"tornbase"	,"_8080bw.java"	,rom_tornbase,null	,machine_driver_8080bw	,input_ports_tornbase	,init_8080bw	,ROT0,   "Midway", "Tornado Baseball", GAME_NO_SOUND )
	/* 610 */ public static GameDriver driver_280zzzap	   = new GameDriver("1976"	,"280zzzap"	,"_8080bw.java"	,rom_280zzzap,null	,machine_driver_280zzzap	,input_ports_280zzzap	,init_8080bw	,ROT0,   "Midway", "Datsun 280 Zzzap", GAME_NO_SOUND )
	/* 611 */ public static GameDriver driver_maze	   = new GameDriver("1976"	,"maze"	,"_8080bw.java"	,rom_maze,null	,machine_driver_8080bw	,input_ports_maze	,init_8080bw	,ROT0,   "Midway", "Amazing Maze", GAME_NO_SOUND )
	/* 612 */ public static GameDriver driver_boothill	   = new GameDriver("1977"	,"boothill"	,"_8080bw.java"	,rom_boothill,null	,machine_driver_boothill	,input_ports_boothill	,init_8080bw	,ROT0,   "Midway", "Boot Hill" )
	/* 615 */ public static GameDriver driver_checkmat	   = new GameDriver("1977"	,"checkmat"	,"_8080bw.java"	,rom_checkmat,null	,machine_driver_checkmat	,input_ports_checkmat	,init_8080bw	,ROT0,   "Midway", "Checkmate", GAME_NO_SOUND )
	/* 618 */ public static GameDriver driver_desertgu	   = new GameDriver("1977"	,"desertgu"	,"_8080bw.java"	,rom_desertgu,null	,machine_driver_desertgu	,input_ports_desertgu	,init_desertgu	,ROT0,   "Midway", "Desert Gun", GAME_NO_SOUND )
	/* 619 */ public static GameDriver driver_dplay	   = new GameDriver("1977"	,"dplay"	,"_8080bw.java"	,rom_dplay,driver_einnings	,machine_driver_m4	,input_ports_einnings	,init_8080bw	,ROT0,   "Midway", "Double Play", GAME_NO_SOUND )
	/* 622 */ public static GameDriver driver_lagunar	   = new GameDriver("1977"	,"lagunar"	,"_8080bw.java"	,rom_lagunar,null	,machine_driver_280zzzap	,input_ports_lagunar	,init_8080bw	,ROT90,  "Midway", "Laguna Racer", GAME_NO_SOUND )
	/* 623 */ public static GameDriver driver_gmissile	   = new GameDriver("1977"	,"gmissile"	,"_8080bw.java"	,rom_gmissile,null	,machine_driver_m4	,input_ports_gmissile	,init_8080bw	,ROT0,   "Midway", "Guided Missile", GAME_NO_SOUND )
	/* 626 */ public static GameDriver driver_m4	   = new GameDriver("1977"	,"m4"	,"_8080bw.java"	,rom_m4,null	,machine_driver_m4	,input_ports_m4	,init_8080bw	,ROT0,   "Midway", "M-4", GAME_NO_SOUND )
	/* 630 */ public static GameDriver driver_clowns	   = new GameDriver("1978"	,"clowns"	,"_8080bw.java"	,rom_clowns,null	,machine_driver_clowns	,input_ports_clowns	,init_8080bw	,ROT0,   "Midway", "Clowns (rev. 2)", GAME_IMPERFECT_SOUND )
	/* 630 */ public static GameDriver driver_clowns1	   = new GameDriver("1978"	,"clowns1"	,"_8080bw.java"	,rom_clowns1,driver_clowns	,machine_driver_clowns	,input_ports_clowns1	,init_8080bw	,ROT0,   "Midway", "Clowns (rev. 1)", GAME_IMPERFECT_SOUND )
	/* 640    																		"Midway", "Space Walk" */
	/* 642 */ public static GameDriver driver_einnings	   = new GameDriver("1978"	,"einnings"	,"_8080bw.java"	,rom_einnings,null	,machine_driver_m4	,input_ports_einnings	,init_8080bw	,ROT0,   "Midway", "Extra Inning", GAME_NO_SOUND )
	/* 643 */ public static GameDriver driver_shuffle	   = new GameDriver("1978"	,"shuffle"	,"_8080bw.java"	,rom_shuffle,null	,machine_driver_shuffle	,input_ports_shuffle	,init_8080bw	,ROT90,  "Midway", "Shuffleboard", GAME_NO_SOUND )
	/* 644 */ public static GameDriver driver_dogpatch	   = new GameDriver("1977"	,"dogpatch"	,"_8080bw.java"	,rom_dogpatch,null	,machine_driver_clowns	,input_ports_dogpatch	,init_8080bw	,ROT0,   "Midway", "Dog Patch", GAME_NO_SOUND )
	/* 645 */ public static GameDriver driver_spcenctr	   = new GameDriver("1980"	,"spcenctr"	,"_8080bw.java"	,rom_spcenctr,null	,machine_driver_spcenctr	,input_ports_spcenctr	,init_8080bw	,ROT0,   "Midway", "Space Encounters", GAME_NO_SOUND )
	/* 652 */ public static GameDriver driver_phantom2	   = new GameDriver("1979"	,"phantom2"	,"_8080bw.java"	,rom_phantom2,null	,machine_driver_phantom2	,input_ports_phantom2	,init_phantom2	,ROT0,   "Midway", "Phantom II", GAME_NO_SOUND )
	/* 730 */ public static GameDriver driver_bowler	   = new GameDriver("1978"	,"bowler"	,"_8080bw.java"	,rom_bowler,null	,machine_driver_bowler	,input_ports_bowler	,init_bowler	,ROT90,  "Midway", "4 Player Bowling Alley", GAME_NO_SOUND )
	/* 739 */ public static GameDriver driver_invaders	   = new GameDriver("1978"	,"invaders"	,"_8080bw.java"	,rom_invaders,null	,machine_driver_invaders	,input_ports_invaders	,init_invaders	,ROT270, "Midway", "Space Invaders" )
	/* 742 */ public static GameDriver driver_blueshrk	   = new GameDriver("1978"	,"blueshrk"	,"_8080bw.java"	,rom_blueshrk,null	,machine_driver_blueshrk	,input_ports_blueshrk	,init_blueshrk	,ROT0,   "Midway", "Blue Shark", GAME_NO_SOUND )
	/* 851 */ public static GameDriver driver_invad2ct	   = new GameDriver("1980"	,"invad2ct"	,"_8080bw.java"	,rom_invad2ct,null	,machine_driver_invad2ct	,input_ports_invad2ct	,init_invad2ct	,ROT90,  "Midway", "Space Invaders II (Midway, cocktail)" )
	/* 852 */ public static GameDriver driver_invaddlx	   = new GameDriver("1980"	,"invaddlx"	,"_8080bw.java"	,rom_invaddlx,driver_invadpt2	,machine_driver_invaders	,input_ports_invadpt2	,init_invaddlx	,ROT270, "Midway", "Space Invaders Deluxe" )
	/* 870    																		"Midway", "Space Invaders Deluxe (cocktail) "*/
	
	/* Taito games */
	
		  public static GameDriver driver_sitv	   = new GameDriver("1978"	,"sitv"	,"_8080bw.java"	,rom_sitv,driver_invaders	,machine_driver_invaders	,input_ports_sitv	,init_invaders	,ROT270, "Taito", "Space Invaders (TV Version)" )
		  public static GameDriver driver_sicv	   = new GameDriver("1979"	,"sicv"	,"_8080bw.java"	,rom_sicv,driver_invaders	,machine_driver_invadpt2	,input_ports_invaders	,init_invadpt2	,ROT270, "Taito", "Space Invaders (CV Version)" )
		  public static GameDriver driver_sisv	   = new GameDriver("1978"	,"sisv"	,"_8080bw.java"	,rom_sisv,driver_invaders	,machine_driver_invadpt2	,input_ports_invaders	,init_invadpt2	,ROT270, "Taito", "Space Invaders (SV Version)" )
		  public static GameDriver driver_sisv2	   = new GameDriver("1978"	,"sisv2"	,"_8080bw.java"	,rom_sisv2,driver_invaders	,machine_driver_invadpt2	,input_ports_invaders	,init_invadpt2	,ROT270, "Taito", "Space Invaders (SV Version 2)" )
		  public static GameDriver driver_galxwars	   = new GameDriver("1979"	,"galxwars"	,"_8080bw.java"	,rom_galxwars,null	,machine_driver_invaders	,input_ports_galxwars	,init_invaders	,ROT270, "Universal", "Galaxy Wars (Universal set 1)" )
		  public static GameDriver driver_galxwar2	   = new GameDriver("1979"	,"galxwar2"	,"_8080bw.java"	,rom_galxwar2,driver_galxwars	,machine_driver_invaders	,input_ports_galxwars	,init_invaders	,ROT270, "Universal", "Galaxy Wars (Universal set 2)" )
		  public static GameDriver driver_galxwart	   = new GameDriver("1979"	,"galxwart"	,"_8080bw.java"	,rom_galxwart,driver_galxwars	,machine_driver_invaders	,input_ports_galxwars	,init_invaders	,ROT270,	"Taito?", "Galaxy Wars (Taito?)" ) /* Copyright Not Displayed */
		  public static GameDriver driver_starw	   = new GameDriver("1979"	,"starw"	,"_8080bw.java"	,rom_starw,driver_galxwars	,machine_driver_invaders	,input_ports_galxwars	,init_invaders	,ROT270, "bootleg", "Star Wars" )
		  public static GameDriver driver_lrescue	   = new GameDriver("1979"	,"lrescue"	,"_8080bw.java"	,rom_lrescue,null	,machine_driver_invadpt2	,input_ports_lrescue	,init_invadpt2	,ROT270, "Taito", "Lunar Rescue" )
		  public static GameDriver driver_grescue	   = new GameDriver("1979"	,"grescue"	,"_8080bw.java"	,rom_grescue,driver_lrescue	,machine_driver_invadpt2	,input_ports_lrescue	,init_invadpt2	,ROT270, "Taito (Universal license?)", "Galaxy Rescue" )
		  public static GameDriver driver_desterth	   = new GameDriver("1979"	,"desterth"	,"_8080bw.java"	,rom_desterth,driver_lrescue	,machine_driver_invadpt2	,input_ports_invrvnge	,init_invadpt2	,ROT270, "bootleg", "Destination Earth" )
		  public static GameDriver driver_invadpt2	   = new GameDriver("1979"	,"invadpt2"	,"_8080bw.java"	,rom_invadpt2,null	,machine_driver_invadpt2	,input_ports_invadpt2	,init_invadpt2	,ROT270, "Taito", "Space Invaders Part II (Taito)" )
		  public static GameDriver driver_cosmo	   = new GameDriver("1979"	,"cosmo"	,"_8080bw.java"	,rom_cosmo,null	,machine_driver_cosmo	,input_ports_cosmo	,init_cosmo	,ROT90,  "bootleg", "Cosmo", GAME_NO_SOUND )
		  public static GameDriver driver_schaser	   = new GameDriver("1979"	,"schaser"	,"_8080bw.java"	,rom_schaser,null	,machine_driver_schaser	,input_ports_schaser	,init_schaser	,ROT270, "Taito", "Space Chaser", GAME_IMPERFECT_SOUND | GAME_IMPERFECT_COLORS )
		  public static GameDriver driver_schasrcv	   = new GameDriver("1979"	,"schasrcv"	,"_8080bw.java"	,rom_schasrcv,driver_schaser	,machine_driver_lupin3	,input_ports_schasrcv	,init_schaser	,ROT270, "Taito", "Space Chaser (CV version)", GAME_NO_SOUND | GAME_IMPERFECT_COLORS | GAME_NO_COCKTAIL )
		  public static GameDriver driver_sflush	   = new GameDriver("1979"	,"sflush"	,"_8080bw.java"	,rom_sflush,null	,machine_driver_sflush	,input_ports_sflush	,init_rollingc	,ROT270, "Taito", "Straight Flush",GAME_NO_SOUND| GAME_IMPERFECT_COLORS | GAME_NO_COCKTAIL)
		  public static GameDriver driver_lupin3	   = new GameDriver("1980"	,"lupin3"	,"_8080bw.java"	,rom_lupin3,null	,machine_driver_lupin3	,input_ports_lupin3	,init_lupin3	,ROT270, "Taito", "Lupin III", GAME_NO_SOUND | GAME_NO_COCKTAIL )
		  public static GameDriver driver_polaris	   = new GameDriver("1980"	,"polaris"	,"_8080bw.java"	,rom_polaris,null	,machine_driver_polaris	,input_ports_polaris	,init_polaris	,ROT270, "Taito", "Polaris (set 1)", GAME_IMPERFECT_SOUND )
		  public static GameDriver driver_polarisa	   = new GameDriver("1980"	,"polarisa"	,"_8080bw.java"	,rom_polarisa,driver_polaris	,machine_driver_polaris	,input_ports_polaris	,init_polaris	,ROT270, "Taito", "Polaris (set 2)", GAME_IMPERFECT_SOUND )
		  public static GameDriver driver_ballbomb	   = new GameDriver("1980"	,"ballbomb"	,"_8080bw.java"	,rom_ballbomb,null	,machine_driver_ballbomb	,input_ports_ballbomb	,init_invadpt2	,ROT270, "Taito", "Balloon Bomber", GAME_NO_SOUND | GAME_IMPERFECT_GRAPHICS )	/* missing clouds and blue background */
	
	/* Nintendo games */
	
		  public static GameDriver driver_sheriff	   = new GameDriver("1979"	,"sheriff"	,"_8080bw.java"	,rom_sheriff,null	,machine_driver_sheriff	,input_ports_sheriff	,init_8080bw	,ROT270, "Nintendo", "Sheriff", GAME_IMPERFECT_SOUND | GAME_WRONG_COLORS )
		  public static GameDriver driver_spacefev	   = new GameDriver("1979"	,"spacefev"	,"_8080bw.java"	,rom_spacefev,null	,machine_driver_sheriff	,input_ports_spacefev	,init_8080bw	,ROT270, "Nintendo", "Space Fever (color)", GAME_IMPERFECT_SOUND )
		  public static GameDriver driver_sfeverbw	   = new GameDriver("1979"	,"sfeverbw"	,"_8080bw.java"	,rom_sfeverbw,driver_spacefev	,machine_driver_sheriff	,input_ports_spacefev	,init_8080bw	,ROT270, "Nintendo", "Space Fever (black and white)", GAME_IMPERFECT_SOUND )
		  public static GameDriver driver_bandido	   = new GameDriver("1980"	,"bandido"	,"_8080bw.java"	,rom_bandido,driver_sheriff	,machine_driver_sheriff	,input_ports_bandido	,init_bandido	,ROT270, "Exidy", "Bandido", GAME_IMPERFECT_SOUND )
		  public static GameDriver driver_helifire	   = new GameDriver("1980"	,"helifire"	,"_8080bw.java"	,rom_helifire,null	,machine_driver_helifire	,input_ports_helifire	,init_helifire	,ROT270, "Nintendo", "HeliFire (revision B)", GAME_IMPERFECT_SOUND )
		  public static GameDriver driver_helifira	   = new GameDriver("1980"	,"helifira"	,"_8080bw.java"	,rom_helifira,driver_helifire	,machine_driver_helifire	,input_ports_helifire	,init_helifire	,ROT270, "Nintendo", "HeliFire (revision A)", GAME_IMPERFECT_SOUND )
	
	/* Misc. manufacturers */
	
		  public static GameDriver driver_earthinv	   = new GameDriver("1980"	,"earthinv"	,"_8080bw.java"	,rom_earthinv,driver_invaders	,machine_driver_invaders	,input_ports_earthinv	,init_invaders	,ROT270, "bootleg", "Super Earth Invasion" )
		  public static GameDriver driver_spaceatt	   = new GameDriver("1978"	,"spaceatt"	,"_8080bw.java"	,rom_spaceatt,driver_invaders	,machine_driver_invaders	,input_ports_invaders	,init_invaders	,ROT270, "Video Games GMBH", "Space Attack" )
		  public static GameDriver driver_spaceat2	   = new GameDriver("1980"	,"spaceat2"	,"_8080bw.java"	,rom_spaceat2,driver_invaders	,machine_driver_invaders	,input_ports_spaceatt	,init_invaders	,ROT270, "Zenitone-Microsec Ltd", "Space Attack II" )
		  public static GameDriver driver_sinvzen	   = new GameDriver("19??"	,"sinvzen"	,"_8080bw.java"	,rom_sinvzen,driver_invaders	,machine_driver_invaders	,input_ports_spaceatt	,init_invaders	,ROT270, "Zenitone-Microsec Ltd", "Super Invaders (Zenitone-Microsec)" )
		  public static GameDriver driver_sinvemag	   = new GameDriver("19??"	,"sinvemag"	,"_8080bw.java"	,rom_sinvemag,driver_invaders	,machine_driver_invaders	,input_ports_sinvemag	,init_invaders	,ROT270, "bootleg", "Super Invaders (EMAG)" )
		  public static GameDriver driver_tst_invd	   = new GameDriver("19??"	,"tst_invd"	,"_8080bw.java"	,rom_tst_invd,driver_invaders	,machine_driver_invaders	,input_ports_invaders	,init_invaders	,ROT0,   "Test ROM", "Space Invaders Test ROM" )
		  public static GameDriver driver_alieninv	   = new GameDriver("19??"	,"alieninv"	,"_8080bw.java"	,rom_alieninv,driver_invaders	,machine_driver_invaders	,input_ports_earthinv	,init_invaders	,ROT270, "bootleg", "Alien Invasion Part II" )
		  public static GameDriver driver_spceking	   = new GameDriver("1978"	,"spceking"	,"_8080bw.java"	,rom_spceking,driver_invaders	,machine_driver_invaders	,input_ports_spceking	,init_invaders	,ROT270, "Leijac (Konami)","Space King" )
		  public static GameDriver driver_spcewars	   = new GameDriver("1978"	,"spcewars"	,"_8080bw.java"	,rom_spcewars,driver_invaders	,machine_driver_invaders	,input_ports_invadpt2	,init_invaders	,ROT270, "Sanritsu", "Space War (Sanritsu)" )
		  public static GameDriver driver_spacewr3	   = new GameDriver("1978"	,"spacewr3"	,"_8080bw.java"	,rom_spacewr3,driver_invaders	,machine_driver_invaders	,input_ports_spacewr3	,init_invaders	,ROT270, "bootleg", "Space War Part 3" )
		  public static GameDriver driver_invaderl	   = new GameDriver("1978"	,"invaderl"	,"_8080bw.java"	,rom_invaderl,driver_invaders	,machine_driver_invaders	,input_ports_invaders	,init_invaders	,ROT270, "bootleg", "Space Invaders (Logitec)" )
		  public static GameDriver driver_jspecter	   = new GameDriver("1979"	,"jspecter"	,"_8080bw.java"	,rom_jspecter,driver_invaders	,machine_driver_invaders	,input_ports_jspecter	,init_invaders	,ROT270, "Jatre", "Jatre Specter (set 1)" )
		  public static GameDriver driver_jspectr2	   = new GameDriver("1979"	,"jspectr2"	,"_8080bw.java"	,rom_jspectr2,driver_invaders	,machine_driver_invaders	,input_ports_jspecter	,init_invaders	,ROT270, "Jatre", "Jatre Specter (set 2)" )
		  public static GameDriver driver_cosmicmo	   = new GameDriver("1979"	,"cosmicmo"	,"_8080bw.java"	,rom_cosmicmo,driver_invaders	,machine_driver_invaders	,input_ports_cosmicmo	,init_invaders	,ROT270, "Universal", "Cosmic Monsters" )
		  public static GameDriver driver_superinv	   = new GameDriver("19??"	,"superinv"	,"_8080bw.java"	,rom_superinv,driver_invaders	,machine_driver_invaders	,input_ports_invaders	,init_invaders	,ROT270, "bootleg", "Super Invaders" )
		  public static GameDriver driver_sstrangr	   = new GameDriver("1978"	,"sstrangr"	,"_8080bw.java"	,rom_sstrangr,null	,machine_driver_sstrangr	,input_ports_sstrangr	,init_8080bw	,ROT270,	"Yachiyo Electronics, Ltd.", "Space Stranger" )
		  public static GameDriver driver_sstrngr2	   = new GameDriver("1979"	,"sstrngr2"	,"_8080bw.java"	,rom_sstrngr2,null	,machine_driver_sstrngr2	,input_ports_sstrngr2	,init_sstrngr2	,ROT270, "Yachiyo Electronics, Ltd.", "Space Stranger 2" )
		  public static GameDriver driver_moonbase	   = new GameDriver("1978"	,"moonbase"	,"_8080bw.java"	,rom_moonbase,driver_invadpt2	,machine_driver_invaders	,input_ports_invadpt2	,init_invaddlx	,ROT270, "Nichibutsu", "Moon Base" )
		  public static GameDriver driver_invrvnge	   = new GameDriver("19??"	,"invrvnge"	,"_8080bw.java"	,rom_invrvnge,null	,machine_driver_invrvnge	,input_ports_invrvnge	,init_invrvnge	,ROT270, "Zenitone Microsec Ltd.", "Invader's Revenge",  GAME_NO_SOUND )
		  public static GameDriver driver_invrvnga	   = new GameDriver("19??"	,"invrvnga"	,"_8080bw.java"	,rom_invrvnga,driver_invrvnge	,machine_driver_invrvnge	,input_ports_invrvnge	,init_invrvnge	,ROT270, "Zenitone Microsec Ltd. (Dutchford license)", "Invader's Revenge (Dutchford)", GAME_NO_SOUND )
		  public static GameDriver driver_spclaser	   = new GameDriver("1980"	,"spclaser"	,"_8080bw.java"	,rom_spclaser,null	,machine_driver_invaders	,input_ports_spclaser	,init_invaddlx	,ROT270, "GamePlan (Taito)", "Space Laser" )
	
		  public static GameDriver driver_laser	   = new GameDriver("1980"	,"laser"	,"_8080bw.java"	,rom_laser,driver_spclaser	,machine_driver_invaders	,input_ports_spclaser	,init_invaddlx	,ROT270, "<unknown>", "Laser" )
		  public static GameDriver driver_spcewarl	   = new GameDriver("1979"	,"spcewarl"	,"_8080bw.java"	,rom_spcewarl,driver_spclaser	,machine_driver_invaders	,input_ports_spclaser	,init_invaddlx	,ROT270, "Leijac (Konami)","Space War (Leijac)" )
		  public static GameDriver driver_rollingc	   = new GameDriver("1979"	,"rollingc"	,"_8080bw.java"	,rom_rollingc,null	,machine_driver_rollingc	,input_ports_rollingc	,init_rollingc	,ROT270, "Nichibutsu", "Rolling Crash / Moon Base", GAME_NO_SOUND )
		  public static GameDriver driver_ozmawars	   = new GameDriver("1979"	,"ozmawars"	,"_8080bw.java"	,rom_ozmawars,null	,machine_driver_invaders	,input_ports_ozmawars	,init_8080bw	,ROT270, "SNK", "Ozma Wars (set 1)" )
		  public static GameDriver driver_ozmawar2	   = new GameDriver("1979"	,"ozmawar2"	,"_8080bw.java"	,rom_ozmawar2,driver_ozmawars	,machine_driver_invaders	,input_ports_ozmawars	,init_8080bw	,ROT270, "SNK", "Ozma Wars (set 2)" ) /* Uses Taito's three board colour version of Space Invaders PCB */
		  public static GameDriver driver_solfight	   = new GameDriver("1979"	,"solfight"	,"_8080bw.java"	,rom_solfight,driver_ozmawars	,machine_driver_invaders	,input_ports_ozmawars	,init_8080bw	,ROT270, "bootleg", "Solar Fight" )
		  public static GameDriver driver_spaceph	   = new GameDriver("1979"	,"spaceph"	,"_8080bw.java"	,rom_spaceph,driver_ozmawars	,machine_driver_invaders	,input_ports_spaceph	,init_8080bw	,ROT270, "Zilec Games", "Space Phantoms" )
		  public static GameDriver driver_yosakdon	   = new GameDriver("1979"	,"yosakdon"	,"_8080bw.java"	,rom_yosakdon,null	,machine_driver_yosakdon	,input_ports_lrescue	,init_8080bw	,ROT270, "bootleg", "Yosaku To Donbee (bootleg)", GAME_NO_SOUND )
}
