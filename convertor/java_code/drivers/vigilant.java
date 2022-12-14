/***************************************************************************

  Vigilante

If you have any questions about how this driver works, don't hesitate to
ask.  - Mike Balfour (mab22@po.cwru.edu)

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class vigilant
{
	
	/* vidhrdw/vigilant.c */
	
	
	public static WriteHandlerPtr vigilant_bank_select_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int bankaddress;
		unsigned char *RAM = memory_region(REGION_CPU1);
	
		bankaddress = 0x10000 + (data & 0x07) * 0x4000;
		cpu_setbank(1,&RAM[bankaddress]);
	} };
	
	/***************************************************************************
	 vigilant_out2_w
	 **************************************************************************/
	public static WriteHandlerPtr vigilant_out2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		/* D0 = FILP = Flip screen? */
		/* D1 = COA1 = Coin Counter A? */
		/* D2 = COB1 = Coin Counter B? */
	
		/* The hardware has both coin counters hooked up to a single meter. */
		coin_counter_w(0,data & 0x02);
		coin_counter_w(1,data & 0x04);
	} };
	
	public static WriteHandlerPtr kikcubic_coin_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		/* bits 0 is flip screen */
	
		/* bit 1 is used but unknown */
	
		/* bits 4/5 are coin counters */
		coin_counter_w(0,data & 0x10);
		coin_counter_w(1,data & 0x20);
	} };
	
	
	
	public static Memory_ReadAddress vigilant_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xc020, 0xc0df, MRA_RAM ),
		new Memory_ReadAddress( 0xc800, 0xcfff, MRA_RAM ),
		new Memory_ReadAddress( 0xd000, 0xdfff, videoram_r ),
		new Memory_ReadAddress( 0xe000, 0xefff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress vigilant_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc020, 0xc0df, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0xc800, 0xcfff, vigilant_paletteram_w, paletteram ),
		new Memory_WriteAddress( 0xd000, 0xdfff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0xe000, 0xefff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort vigilant_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, input_port_0_r ),
		new IO_ReadPort( 0x01, 0x01, input_port_1_r ),
		new IO_ReadPort( 0x02, 0x02, input_port_2_r ),
		new IO_ReadPort( 0x03, 0x03, input_port_3_r ),
		new IO_ReadPort( 0x04, 0x04, input_port_4_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort vigilant_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, m72_sound_command_w ),  /* SD */
		new IO_WritePort( 0x01, 0x01, vigilant_out2_w ), /* OUT2 */
		new IO_WritePort( 0x04, 0x04, vigilant_bank_select_w ), /* PBANK */
		new IO_WritePort( 0x80, 0x81, vigilant_horiz_scroll_w ), /* HSPL, HSPH */
		new IO_WritePort( 0x82, 0x83, vigilant_rear_horiz_scroll_w ), /* RHSPL, RHSPH */
		new IO_WritePort( 0x84, 0x84, vigilant_rear_color_w ), /* RCOD */
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress kikcubic_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xc000, 0xc0ff, MRA_RAM ),
		new Memory_ReadAddress( 0xc800, 0xcaff, MRA_RAM ),
		new Memory_ReadAddress( 0xd000, 0xdfff, videoram_r ),
		new Memory_ReadAddress( 0xe000, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress kikcubic_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xc0ff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0xc800, 0xcaff, vigilant_paletteram_w, paletteram ),
		new Memory_WriteAddress( 0xd000, 0xdfff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0xe000, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort kikcubic_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, input_port_3_r ),
		new IO_ReadPort( 0x01, 0x01, input_port_4_r ),
		new IO_ReadPort( 0x02, 0x02, input_port_0_r ),
		new IO_ReadPort( 0x03, 0x03, input_port_1_r ),
		new IO_ReadPort( 0x04, 0x04, input_port_2_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort kikcubic_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, kikcubic_coin_w ),	/* also flip screen, and...? */
		new IO_WritePort( 0x04, 0x04, vigilant_bank_select_w ),
		new IO_WritePort( 0x06, 0x06, m72_sound_command_w ),
	//	new IO_WritePort( 0x07, 0x07, IOWP_NOP ),	/* ?? */
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x01, 0x01, YM2151_status_port_0_r ),
		new IO_ReadPort( 0x80, 0x80, soundlatch_r ),	/* SDRE */
		new IO_ReadPort( 0x84, 0x84, m72_sample_r ),	/* S ROM C */
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, YM2151_register_port_0_w ),
		new IO_WritePort( 0x01, 0x01, YM2151_data_port_0_w ),
		new IO_WritePort( 0x80, 0x81, vigilant_sample_addr_w ),	/* STL / STH */
		new IO_WritePort( 0x82, 0x82, m72_sample_w ),			/* COUNT UP */
		new IO_WritePort( 0x83, 0x83, m72_sound_irq_ack_w ),	/* IRQ clear */
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	static InputPortHandlerPtr input_ports_vigilant = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( vigilant )
		PORT_START(); 
		PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT(0xF0, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT );
		PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT );
		PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN );
		PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP );
		PORT_BIT(0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT(0x20, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT(0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT(0x80, IP_ACTIVE_LOW, IPT_BUTTON2 );
	
		PORT_START(); 
		PORT_BIT(0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_COCKTAIL );
		PORT_BIT(0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_COCKTAIL );
		PORT_BIT(0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_COCKTAIL );
		PORT_BIT(0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_COCKTAIL );
		PORT_BIT(0x10, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT(0x20, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT(0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT(0x80, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
	
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(	0x02, "2" );
		PORT_DIPSETTING(	0x03, "3" );
		PORT_DIPSETTING(	0x01, "4" );
		PORT_DIPSETTING(	0x00, "5" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(	0x04, "Normal" );
		PORT_DIPSETTING(	0x00, "Hard" );
		PORT_DIPNAME( 0x08, 0x08, "Decrease of Energy" );
		PORT_DIPSETTING(	0x08, "Slow" );
		PORT_DIPSETTING(	0x00, "Fast" );
		/* TODO: support the different settings which happen in Coin Mode 2 */
		PORT_DIPNAME( 0xf0, 0xf0, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0xa0, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(	0xb0, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(	0xc0, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(	0xd0, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(	0x10, DEF_STR( "8C_3C") );
		PORT_DIPSETTING(	0xe0, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x20, DEF_STR( "5C_3C") );
		PORT_DIPSETTING(	0x30, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(	0xf0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x40, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(	0x90, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x80, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(	0x70, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(	0x60, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(	0x50, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Free_Play") );
	
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(	0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Cocktail") );
	/* This activates a different coin mode. Look at the dip switch setting schematic */
		PORT_DIPNAME( 0x04, 0x04, "Coin Mode" );
		PORT_DIPSETTING(	0x04, "Mode 1" );
		PORT_DIPSETTING(	0x00, "Mode 2" );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Allow Continue" );
		PORT_DIPSETTING(	0x00, DEF_STR( "No") );
		PORT_DIPSETTING(	0x10, DEF_STR( "Yes") );
		/* In stop mode, press 2 to stop and 1 to restart */
		PORT_BITX   ( 0x20, 0x20, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Stop Mode", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BITX(    0x40, 0x40, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(	0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_kikcubic = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( kikcubic )
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT_IMPULSE( 0x40, IP_ACTIVE_LOW, IPT_COIN3, 19 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
	
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x02, "Easy" );
		PORT_DIPSETTING(    0x03, "Medium" );
		PORT_DIPSETTING(    0x01, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x08, "1" );
		PORT_DIPSETTING(    0x04, "2" );
		PORT_DIPSETTING(    0x0c, "3" );
		PORT_DIPSETTING(    0x00, "4" );
		/* TODO: support the different settings which happen in Coin Mode 2 */
		PORT_DIPNAME( 0xf0, 0xf0, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0xa0, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(	0xb0, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(	0xc0, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(	0xd0, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(	0xe0, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0xf0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x70, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x60, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(	0x50, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(	0x40, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(	0x30, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Free_Play") );
	//	PORT_DIPSETTING(	0x10, "Undefined" );
	//	PORT_DIPSETTING(	0x20, "Undefined" );
	//	PORT_DIPSETTING(	0x80, "Undefined" );
	//	PORT_DIPSETTING(	0x90, "Undefined" );
	
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(	0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Cocktail") );
	/* This activates a different coin mode. Look at the dip switch setting schematic */
		PORT_DIPNAME( 0x04, 0x04, "Coin Mode" );
		PORT_DIPSETTING(	0x04, "Mode 1" );
		PORT_DIPSETTING(	0x00, "Mode 2" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(	0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
		PORT_BITX(    0x10, 0x10, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, "Level Select" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Player Adding" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout text_layout = new GfxLayout
	(
		8,8, /* tile size */
		4096, /* number of tiles */
		4, /* bits per pixel */
		new int[] {64*1024*8,64*1024*8+4,0,4}, /* plane offsets */
		new int[] { 0,1,2,3, 64+0,64+1,64+2,64+3 }, /* x offsets */
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 }, /* y offsets */
		128
	);
	
	static GfxLayout sprite_layout = new GfxLayout
	(
		16,16,	/* tile size */
		4096,	/* number of sprites ($1000) */
		4,		/* bits per pixel */
		new int[] {0x40000*8,0x40000*8+4,0,4}, /* plane offsets */
		new int[] { /* x offsets */
			0x00*8+0,0x00*8+1,0x00*8+2,0x00*8+3,
			0x10*8+0,0x10*8+1,0x10*8+2,0x10*8+3,
			0x20*8+0,0x20*8+1,0x20*8+2,0x20*8+3,
			0x30*8+0,0x30*8+1,0x30*8+2,0x30*8+3
		},
		new int[] { /* y offsets */
			0x00*8, 0x01*8, 0x02*8, 0x03*8,
			0x04*8, 0x05*8, 0x06*8, 0x07*8,
			0x08*8, 0x09*8, 0x0A*8, 0x0B*8,
			0x0C*8, 0x0D*8, 0x0E*8, 0x0F*8
		},
		0x40*8
	);
	
	static GfxLayout back_layout = new GfxLayout
	(
		32,1, /* tile size */
		3*512*8, /* number of tiles */
		4, /* bits per pixel */
		new int[] {0,2,4,6}, /* plane offsets */
		new int[] { 0*8+1, 0*8,  1*8+1, 1*8, 2*8+1, 2*8, 3*8+1, 3*8, 4*8+1, 4*8, 5*8+1, 5*8,
		6*8+1, 6*8, 7*8+1, 7*8, 8*8+1, 8*8, 9*8+1, 9*8, 10*8+1, 10*8, 11*8+1, 11*8,
		12*8+1, 12*8, 13*8+1, 13*8, 14*8+1, 14*8, 15*8+1, 15*8 }, /* x offsets */
		new int[] { 0 }, /* y offsets */
		16*8
	);
	
	static GfxDecodeInfo vigilant_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, text_layout,   256, 16 ),	/* colors 256-511 */
		new GfxDecodeInfo( REGION_GFX2, 0, sprite_layout,   0, 16 ),	/* colors   0-255 */
		new GfxDecodeInfo( REGION_GFX3, 0, back_layout,   512,  2 ),	/* actually the background uses colors */
														/* 256-511, but giving it exclusive */
														/* pens we can handle it more easily. */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static GfxDecodeInfo kikcubic_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, text_layout,   0, 16 ),
		new GfxDecodeInfo( REGION_GFX2, 0, sprite_layout, 0, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static struct YM2151interface ym2151_interface =
	{
		1,			/* 1 chip */
		3579645,	/* 3.579645 MHz */
		{ YM3012_VOL(55,MIXER_PAN_LEFT,55,MIXER_PAN_RIGHT) },
		{ m72_ym2151_irq_handler },
		{ 0 }
	};
	
	static DACinterface dac_interface = new DACinterface
	(
		1,
		new int[] { 100 }
	);
	
	
	
	public static MachineHandlerPtr machine_driver_vigilant = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 3579645)		   /* 3.579645 MHz */
		MDRV_CPU_MEMORY(vigilant_readmem,vigilant_writemem)
		MDRV_CPU_PORTS(vigilant_readport,vigilant_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 3579645)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)		   /* 3.579645 MHz */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(sound_readport,sound_writeport)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,128)	/* clocked by V1 */
									/* IRQs are generated by main Z80 and YM2151 */
		MDRV_FRAMES_PER_SECOND(55)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_MACHINE_INIT(m72_sound)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(16*8, (64-16)*8-1, 0*8, 32*8-1 )
		MDRV_GFXDECODE(vigilant_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(512+32)	/* 512 real palette, 32 virtual palette */
	
		MDRV_VIDEO_START(vigilant)
		MDRV_VIDEO_UPDATE(vigilant)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	public static MachineHandlerPtr machine_driver_kikcubic = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 3579645)		   /* 3.579645 MHz */
		MDRV_CPU_MEMORY(kikcubic_readmem,kikcubic_writemem)
		MDRV_CPU_PORTS(kikcubic_readport,kikcubic_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 3579645)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)		   /* 3.579645 MHz */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(sound_readport,sound_writeport)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,128)	/* clocked by V1 */
									/* IRQs are generated by main Z80 and YM2151 */
		MDRV_FRAMES_PER_SECOND(55)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_MACHINE_INIT(m72_sound)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(8*8, (64-8)*8-1, 0*8, 32*8-1 )
		MDRV_GFXDECODE(kikcubic_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_VIDEO_START(vigilant)
		MDRV_VIDEO_UPDATE(kikcubic)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	
	/***************************************************************************
	
	  Game ROMs
	
	***************************************************************************/
	
	static RomLoadHandlerPtr rom_vigilant = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 ) /* 64k for code + 128k for bankswitching */
		ROM_LOAD( "g07_c03.bin",  0x00000, 0x08000, CRC(9dcca081) SHA1(6d086b70e6bf1fbafa746ef5c82334645f199be9) )
		ROM_LOAD( "j07_c04.bin",  0x10000, 0x10000, CRC(e0159105) SHA1(da6d74ec075863c67c0ce21b07a54029d138f688) )
		/* 0x20000-0x2ffff empty */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 ) /* 64k for sound */
		ROM_LOAD( "g05_c02.bin",  0x00000, 0x10000, CRC(10582b2d) SHA1(6e7e5f07c49b347b427572efeb180c89f49bf2c7) )
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "f05_c08.bin",  0x00000, 0x10000, CRC(01579d20) SHA1(e58d8ca0ea0ac9d77225bf55faa499d1565924f9) )
		ROM_LOAD( "h05_c09.bin",  0x10000, 0x10000, CRC(4f5872f0) SHA1(6af21ba1c94097eecce30585983b4b07528c8635) )
	
		ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "n07_c12.bin",  0x00000, 0x10000, CRC(10af8eb2) SHA1(664b178b248babc43a9af0fe140fe57bc7367762) )
		ROM_LOAD( "k07_c10.bin",  0x10000, 0x10000, CRC(9576f304) SHA1(0ec2a7d3d82208e2a9a4ef9ab2824e6fe26ebbe5) )
		ROM_LOAD( "o07_c13.bin",  0x20000, 0x10000, CRC(b1d9d4dc) SHA1(1aacf6b0ff8d102880d3dce3b55cd1488edb90cf) )
		ROM_LOAD( "l07_c11.bin",  0x30000, 0x10000, CRC(4598be4a) SHA1(6b68ec94bdee0e58133a8d3891054ef44a8ff0e5) )
		ROM_LOAD( "t07_c16.bin",  0x40000, 0x10000, CRC(f5425e42) SHA1(c401263b6a266d3e9cd23133f1d823fb4b095e3d) )
		ROM_LOAD( "p07_c14.bin",  0x50000, 0x10000, CRC(cb50a17c) SHA1(eb15704f715b6475ae7096f8d82f1b20f8277c71) )
		ROM_LOAD( "v07_c17.bin",  0x60000, 0x10000, CRC(959ba3c7) SHA1(dcd2a885ae7b61210cbd55a38ccbe91c73d071b0) )
		ROM_LOAD( "s07_c15.bin",  0x70000, 0x10000, CRC(7f2e91c5) SHA1(27dcc9b696834897c36c0b7a1c6202d93f41ad8d) )
	
		ROM_REGION( 0x30000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "d01_c05.bin",  0x00000, 0x10000, CRC(81b1ee5c) SHA1(2014165ec71f089fecb5a3e60b939cc0f565d7f1) )
		ROM_LOAD( "e01_c06.bin",  0x10000, 0x10000, CRC(d0d33673) SHA1(39761d97a71deaf7f17233d5bd5a55dbb1e6b30e) )
		ROM_LOAD( "f01_c07.bin",  0x20000, 0x10000, CRC(aae81695) SHA1(ca8e136eca3543b27f3a61b105d4a280711cd6ea) )
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 ) /* samples */
		ROM_LOAD( "d04_c01.bin",  0x00000, 0x10000, CRC(9b85101d) SHA1(6b8a0f33b9b66bb968f7b61e49d19a6afad8db95) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_vigilntu = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 ) /* 64k for code + 128k for bankswitching */
		ROM_LOAD( "a-8h",  0x00000, 0x08000, CRC(8d15109e) SHA1(9ef57047a0b53cd0143a260193b33e3d5680ca71) )
		ROM_LOAD( "a-8l",  0x10000, 0x10000, CRC(7f95799b) SHA1(a371671c3c26976314aaac4e410bff0f13a8a085) )
		/* 0x20000-0x2ffff empty */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 ) /* 64k for sound */
		ROM_LOAD( "g05_c02.bin",  0x00000, 0x10000, CRC(10582b2d) SHA1(6e7e5f07c49b347b427572efeb180c89f49bf2c7) )
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "f05_c08.bin",  0x00000, 0x10000, CRC(01579d20) SHA1(e58d8ca0ea0ac9d77225bf55faa499d1565924f9) )
		ROM_LOAD( "h05_c09.bin",  0x10000, 0x10000, CRC(4f5872f0) SHA1(6af21ba1c94097eecce30585983b4b07528c8635) )
	
		ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "n07_c12.bin",  0x00000, 0x10000, CRC(10af8eb2) SHA1(664b178b248babc43a9af0fe140fe57bc7367762) )
		ROM_LOAD( "k07_c10.bin",  0x10000, 0x10000, CRC(9576f304) SHA1(0ec2a7d3d82208e2a9a4ef9ab2824e6fe26ebbe5) )
		ROM_LOAD( "o07_c13.bin",  0x20000, 0x10000, CRC(b1d9d4dc) SHA1(1aacf6b0ff8d102880d3dce3b55cd1488edb90cf) )
		ROM_LOAD( "l07_c11.bin",  0x30000, 0x10000, CRC(4598be4a) SHA1(6b68ec94bdee0e58133a8d3891054ef44a8ff0e5) )
		ROM_LOAD( "t07_c16.bin",  0x40000, 0x10000, CRC(f5425e42) SHA1(c401263b6a266d3e9cd23133f1d823fb4b095e3d) )
		ROM_LOAD( "p07_c14.bin",  0x50000, 0x10000, CRC(cb50a17c) SHA1(eb15704f715b6475ae7096f8d82f1b20f8277c71) )
		ROM_LOAD( "v07_c17.bin",  0x60000, 0x10000, CRC(959ba3c7) SHA1(dcd2a885ae7b61210cbd55a38ccbe91c73d071b0) )
		ROM_LOAD( "s07_c15.bin",  0x70000, 0x10000, CRC(7f2e91c5) SHA1(27dcc9b696834897c36c0b7a1c6202d93f41ad8d) )
	
		ROM_REGION( 0x30000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "d01_c05.bin",  0x00000, 0x10000, CRC(81b1ee5c) SHA1(2014165ec71f089fecb5a3e60b939cc0f565d7f1) )
		ROM_LOAD( "e01_c06.bin",  0x10000, 0x10000, CRC(d0d33673) SHA1(39761d97a71deaf7f17233d5bd5a55dbb1e6b30e) )
		ROM_LOAD( "f01_c07.bin",  0x20000, 0x10000, CRC(aae81695) SHA1(ca8e136eca3543b27f3a61b105d4a280711cd6ea) )
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 ) /* samples */
		ROM_LOAD( "d04_c01.bin",  0x00000, 0x10000, CRC(9b85101d) SHA1(6b8a0f33b9b66bb968f7b61e49d19a6afad8db95) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_vigilntj = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 ) /* 64k for code + 128k for bankswitching */
		ROM_LOAD( "vg_a-8h.rom",  0x00000, 0x08000, CRC(ba848713) SHA1(b357cbf404fb1874d555797ed9fb37f946cc4340) )
		ROM_LOAD( "vg_a-8l.rom",  0x10000, 0x10000, CRC(3b12b1d8) SHA1(2f9207f8d8ec41ea1b8f5bf3c69a97d1d09f6c3f) )
		/* 0x20000-0x2ffff empty */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 ) /* 64k for sound */
		ROM_LOAD( "g05_c02.bin",  0x00000, 0x10000, CRC(10582b2d) SHA1(6e7e5f07c49b347b427572efeb180c89f49bf2c7) )
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "f05_c08.bin",  0x00000, 0x10000, CRC(01579d20) SHA1(e58d8ca0ea0ac9d77225bf55faa499d1565924f9) )
		ROM_LOAD( "h05_c09.bin",  0x10000, 0x10000, CRC(4f5872f0) SHA1(6af21ba1c94097eecce30585983b4b07528c8635) )
	
		ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "n07_c12.bin",  0x00000, 0x10000, CRC(10af8eb2) SHA1(664b178b248babc43a9af0fe140fe57bc7367762) )
		ROM_LOAD( "k07_c10.bin",  0x10000, 0x10000, CRC(9576f304) SHA1(0ec2a7d3d82208e2a9a4ef9ab2824e6fe26ebbe5) )
		ROM_LOAD( "o07_c13.bin",  0x20000, 0x10000, CRC(b1d9d4dc) SHA1(1aacf6b0ff8d102880d3dce3b55cd1488edb90cf) )
		ROM_LOAD( "l07_c11.bin",  0x30000, 0x10000, CRC(4598be4a) SHA1(6b68ec94bdee0e58133a8d3891054ef44a8ff0e5) )
		ROM_LOAD( "t07_c16.bin",  0x40000, 0x10000, CRC(f5425e42) SHA1(c401263b6a266d3e9cd23133f1d823fb4b095e3d) )
		ROM_LOAD( "p07_c14.bin",  0x50000, 0x10000, CRC(cb50a17c) SHA1(eb15704f715b6475ae7096f8d82f1b20f8277c71) )
		ROM_LOAD( "v07_c17.bin",  0x60000, 0x10000, CRC(959ba3c7) SHA1(dcd2a885ae7b61210cbd55a38ccbe91c73d071b0) )
		ROM_LOAD( "s07_c15.bin",  0x70000, 0x10000, CRC(7f2e91c5) SHA1(27dcc9b696834897c36c0b7a1c6202d93f41ad8d) )
	
		ROM_REGION( 0x30000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "d01_c05.bin",  0x00000, 0x10000, CRC(81b1ee5c) SHA1(2014165ec71f089fecb5a3e60b939cc0f565d7f1) )
		ROM_LOAD( "e01_c06.bin",  0x10000, 0x10000, CRC(d0d33673) SHA1(39761d97a71deaf7f17233d5bd5a55dbb1e6b30e) )
		ROM_LOAD( "f01_c07.bin",  0x20000, 0x10000, CRC(aae81695) SHA1(ca8e136eca3543b27f3a61b105d4a280711cd6ea) )
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 ) /* samples */
		ROM_LOAD( "d04_c01.bin",  0x00000, 0x10000, CRC(9b85101d) SHA1(6b8a0f33b9b66bb968f7b61e49d19a6afad8db95) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_kikcubic = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x30000, REGION_CPU1, 0 ) /* 64k for code + 128k for bankswitching */
		ROM_LOAD( "mqj-p0",       0x00000, 0x08000, CRC(9cef394a) SHA1(be9cc78420b4c35f8f9523b529bd56315749762c) )
		ROM_LOAD( "mqj-b0",       0x10000, 0x10000, CRC(d9bcf4cd) SHA1(f1f1cb8609343dae8637f115e5c96fd88a00f5eb) )
		ROM_LOAD( "mqj-b1",       0x20000, 0x10000, CRC(54a0abe1) SHA1(0fb1d050c1e299394609214c903bcf4cf11329ff) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 ) /* 64k for sound */
		ROM_LOAD( "mqj-sp",       0x00000, 0x10000, CRC(bbcf3582) SHA1(4a5b9d4161b26e3ca400573fa78268893e42d5db) )
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "mqj-c0",       0x00000, 0x10000, CRC(975585c5) SHA1(eb8245e458a5d4880add5b4a305a4468fa8f6491) )
		ROM_LOAD( "mqj-c1",       0x10000, 0x10000, CRC(49d9936d) SHA1(c4169ddd481c19e8e24457e2fe011db1b34db6d3) )
	
		ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "mqj-00",       0x00000, 0x40000, CRC(7fb0c58f) SHA1(f70ff39e2d648606686c87cf1a7a3ffb46c2656a) )
		ROM_LOAD( "mqj-10",       0x40000, 0x40000, CRC(3a189205) SHA1(063d664d4cf709931b5e3a5b6eb7c75bcd57b518) )
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 ) /* samples */
		ROM_LOAD( "mqj-v0",       0x00000, 0x10000, CRC(54762956) SHA1(f08e983af28b16d27505d465ca64e7c7a93373a4) )
	
		ROM_REGION( 0x0140, REGION_PROMS, 0 )
		ROM_LOAD( "8d",           0x0000, 0x0100, CRC(7379bb12) SHA1(cf0c4e27911505f937004ea5eac1154956ec5d3b) )	/* unknown (timing?) */
		ROM_LOAD( "6h",           0x0100, 0x0020, CRC(face0cbb) SHA1(c56aea3b7aaabbd4ff1b4546fcad94f51b473cde) )	/* unknown (bad read?) */
		ROM_LOAD( "7s",           0x0120, 0x0020, CRC(face0cbb) SHA1(c56aea3b7aaabbd4ff1b4546fcad94f51b473cde) )	/* unknown (bad read?) */
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_vigilant	   = new GameDriver("1988"	,"vigilant"	,"vigilant.java"	,rom_vigilant,null	,machine_driver_vigilant	,input_ports_vigilant	,null	,ROT0, "Irem", "Vigilante (World)", GAME_NO_COCKTAIL )
	public static GameDriver driver_vigilntu	   = new GameDriver("1988"	,"vigilntu"	,"vigilant.java"	,rom_vigilntu,driver_vigilant	,machine_driver_vigilant	,input_ports_vigilant	,null	,ROT0, "Irem (Data East USA license)", "Vigilante (US)", GAME_NO_COCKTAIL )
	public static GameDriver driver_vigilntj	   = new GameDriver("1988"	,"vigilntj"	,"vigilant.java"	,rom_vigilntj,driver_vigilant	,machine_driver_vigilant	,input_ports_vigilant	,null	,ROT0, "Irem", "Vigilante (Japan)", GAME_NO_COCKTAIL )
	public static GameDriver driver_kikcubic	   = new GameDriver("1988"	,"kikcubic"	,"vigilant.java"	,rom_kikcubic,null	,machine_driver_kikcubic	,input_ports_kikcubic	,null	,ROT0, "Irem", "Meikyu Jima (Japan)", GAME_NO_COCKTAIL )	/* English title is Kickle Cubicle */
}
