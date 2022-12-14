/****************************************************************************
 *
 * geebee.c
 *
 * system driver
 * juergen buchmueller <pullmoll@t-online.de>, jan 2000
 *
 * memory map (preliminary)
 * 0000-0fff ROM1 / ROM0
 * 1000-1fff ROM2
 * 2000-2fff VRAM
 * 3000-3fff CGROM
 * 4000-4fff RAM
 * 5000-5fff IN
 *			 A1 A0
 *			  0  0	  SW0
 *					  D0 COIN1
 *					  D1 COIN2
 *					  D2 START1
 *					  D3 START2
 *					  D4 BUTTON1
 *					  D5 TEST MODE
 *			  0  1	  SW1
 *					  - not used in Gee Bee
 *					  - digital joystick left/right and button in
 *						Kaitei Tagara Sagashi (two in Cocktail mode)
 *			  1  0	  DSW2
 *					  D0	cabinet: 0= upright  1= table
 *					  D1	balls:	 0= 3		 1= 5
 *					  D2-D3 coinage: 0=1c/1c 1=1c/2c 2=2c/1c 3=free play
 *					  D4-D5 bonus:	 0=none, 1=40k	 2=70k	 3=100k
 *			  1  1	  VOLIN
 *					  D0-D7 vcount where paddle starts (note: rotated 90 deg!)
 *					  - not used(?) in Kaitei Tagara Sagashi
 * 6000-6fff OUT6
 *			 A1 A0
 *			  0  0	  BALL H
 *			  0  1	  BALL V
 *			  1  0	  n/c
 *			  1  1	  SOUND
 *					  D3 D2 D1 D0	   sound
 *					   x  0  0	0  PURE TONE 4V (2000Hz)
 *					   x  0  0	1  PURE TONE 8V (1000Hz)
 *					   x  0  1	0  PURE TONE 16V (500Hz)
 *					   x  0  1	1  PURE TONE 32V (250Hz)
 *					   x  1  0	0  TONE1 (!1V && !16V)
 *					   x  1  0	1  TONE2 (!2V && !32V)
 *					   x  1  1	0  TONE3 (!4V && !64V)
 *					   x  1  1	1  NOISE
 *					   0  x  x	x  DECAY
 *					   1  x  x	x  FULL VOLUME
 * 7000-7fff OUT7
 *			 A2 A1 A0
 *			  0  0	0 LAMP 1
 *			  0  0	1 LAMP 2
 *			  0  1	0 LAMP 3
 *			  0  1	1 COUNTER
 *			  1  0	0 LOCK OUT COIL
 *			  1  0	1 BGW
 *			  1  1	0 BALL ON
 *			  1  1	1 INV
 * 8000-ffff INTA (read FF)
 *
 * TODO:
 * add second controller for cocktail mode and two players?
 *
 ****************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class geebee
{
	
	/* from machine/geebee.c */
	
	/* from vidhrdw/geebee.c */
	
	
	/* from sndhrdw/geebee.c */
	
	
	/*******************************************************
	 *
	 * Gee Bee overlay
	 *
	 *******************************************************/
	
	#define PINK1	MAKE_ARGB(0x04,0xa0,0x00,0xe0)
	#define PINK2 	MAKE_ARGB(0x04,0xe0,0x00,0xf0)
	#define ORANGE	MAKE_ARGB(0x04,0xff,0xd0,0x00)
	#define BLUE	MAKE_ARGB(0x04,0x00,0x00,0xff)
	
	OVERLAY_START( geebee_overlay )
		OVERLAY_RECT(  1*8,  0*8,  4*8, 28*8, PINK2 )
		OVERLAY_RECT(  4*8,  0*8,  5*8,  4*8, PINK1 )
		OVERLAY_RECT(  4*8, 24*8,  5*8, 28*8, PINK1 )
		OVERLAY_RECT(  4*8,  4*8,  5*8, 24*8, ORANGE )
		OVERLAY_RECT(  5*8,  0*8, 28*8,  1*8, PINK1 )
		OVERLAY_RECT(  5*8, 27*8, 28*8, 28*8, PINK1 )
		OVERLAY_RECT(  5*8,  1*8, 28*8,  4*8, BLUE )
		OVERLAY_RECT(  5*8, 24*8, 28*8, 27*8, BLUE )
		OVERLAY_RECT( 12*8, 13*8, 13*8, 15*8, BLUE )
		OVERLAY_RECT( 21*8, 10*8, 23*8, 12*8, BLUE )
		OVERLAY_RECT( 21*8, 16*8, 23*8, 18*8, BLUE )
		OVERLAY_RECT( 28*8,  0*8, 29*8, 28*8, PINK2 )
		OVERLAY_RECT( 29*8,  0*8, 32*8, 28*8, PINK1 )
	OVERLAY_END
	
	
	
	/*******************************************************
	 *
	 * memory regions
	 *
	 *******************************************************/
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),	/* GeeBee uses only the first 4K */
		new Memory_ReadAddress( 0x2000, 0x23ff, MRA_RAM ),
		new Memory_ReadAddress( 0x3000, 0x37ff, MRA_ROM ),	/* GeeBee uses only the first 1K */
		new Memory_ReadAddress( 0x4000, 0x40ff, MRA_RAM ),
		new Memory_ReadAddress( 0x5000, 0x5fff, geebee_in_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_navalone[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x2000, 0x23ff, MRA_RAM ),
		new Memory_ReadAddress( 0x3000, 0x37ff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x40ff, MRA_RAM ),
		new Memory_ReadAddress( 0x5000, 0x5fff, navalone_in_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0x2000, 0x23ff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x2400, 0x27ff, videoram_w ), /* mirror used in kaitei */
		new Memory_WriteAddress( 0x3000, 0x37ff, MWA_ROM ),
	    new Memory_WriteAddress( 0x4000, 0x40ff, MWA_RAM ),
		new Memory_WriteAddress( 0x6000, 0x6fff, geebee_out6_w ),
		new Memory_WriteAddress( 0x7000, 0x7fff, geebee_out7_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x50, 0x5f, geebee_in_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort readport_navalone[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x50, 0x5f, navalone_in_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x60, 0x6f, geebee_out6_w ),
		new IO_WritePort( 0x70, 0x7f, geebee_out7_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	static InputPortHandlerPtr input_ports_geebee = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( geebee )
		PORT_START(); 		/* IN0 SW0 */
		PORT_BIT	( 0x01, IP_ACTIVE_LOW, IPT_COIN1   );
		PORT_BIT	( 0x02, IP_ACTIVE_LOW, IPT_COIN2   );
		PORT_BIT	( 0x04, IP_ACTIVE_LOW, IPT_START1  );
		PORT_BIT	( 0x08, IP_ACTIVE_LOW, IPT_START2  );
		PORT_BIT	( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_SERVICE( 0x20, IP_ACTIVE_LOW );
		PORT_BIT	( 0xc0, 0x00, IPT_UNUSED );
	
		PORT_START();       /* IN1 SW1 */
		PORT_BIT	( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT	( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT	( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT	( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT	( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT	( 0x20, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT	( 0xc0, 0x00, IPT_UNUSED );
	
		PORT_START();       /* IN2 DSW2 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail"));
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "Free_Play") );
		/* Bonus Life moved to two inputs to allow changing 3/5 lives mode separately */
		PORT_BIT	( 0x30, 0x00, IPT_UNUSED );
		PORT_BIT	( 0xc0, 0x00, IPT_UNUSED );
	
		PORT_START(); 		/* IN3 VOLIN */
		PORT_ANALOG( 0xff, 0x58, IPT_PADDLE | IPF_REVERSE, 30, 15, 0x10, 0xa0 );
	
		PORT_START(); 		/* IN4 FAKE for 3 lives */
		PORT_BIT	( 0x0f, 0x00, IPT_UNUSED );
		PORT_DIPNAME( 0x30, 0x00, "Bonus Life (3 lives"));
		PORT_DIPSETTING(    0x10, "40k 80k" );
		PORT_DIPSETTING(    0x20, "70k 140k" );
		PORT_DIPSETTING(    0x30, "100k 200k" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_BIT	( 0xc0, 0x00, IPT_UNUSED );
	
		PORT_START(); 		/* IN5 FAKE for 5 lives */
		PORT_BIT	( 0x0f, 0x00, IPT_UNUSED );
		PORT_DIPNAME( 0x30, 0x00, "Bonus Life (5 lives"));
		PORT_DIPSETTING(    0x10, "60k 120k" );
		PORT_DIPSETTING(    0x20, "100k 200k" );
		PORT_DIPSETTING(    0x30, "150k 300k" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_BIT	( 0xc0, 0x00, IPT_UNUSED );
	
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_navalone = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( navalone )
		PORT_START(); 		/* IN0 SW0 */
		PORT_BIT	( 0x01, IP_ACTIVE_LOW, IPT_COIN1   );
		PORT_BIT	( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT	( 0x04, IP_ACTIVE_LOW, IPT_START1  );
		PORT_BIT	( 0x08, IP_ACTIVE_LOW, IPT_START2  );
		PORT_BIT	( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT	( 0x20, IP_ACTIVE_LOW, IPT_COIN2   );
		PORT_BIT	( 0xc0, 0x00, IPT_UNUSED );
	
		PORT_START();       /* IN1 SW1 */
		PORT_BIT	( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT	( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT	( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT	( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT	( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT	( 0x20, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT	( 0xc0, 0x00, IPT_UNUSED );
	
		PORT_START();       /* IN2 DSW2 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail"));
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(	0x00, "2" );
		PORT_DIPSETTING(	0x02, "3" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
	    PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
	    PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
	    PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x08, DEF_STR( "On") );
	    PORT_DIPNAME( 0x38, 0x10, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0x30, DEF_STR( "2C_1C") );
	    PORT_DIPSETTING(    0x10, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x20, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Free_Play") );
	    PORT_BIT    ( 0xc0, 0x00, IPT_UNUSED );
	
		PORT_START(); 		/* IN3 VOLIN */
		PORT_BIT	( 0xff, 0x58, IPT_UNUSED );
	
		PORT_START(); 		/* IN4 two-way digital joystick */
		PORT_BIT	( 0x01, 0x00, IPT_JOYSTICK_LEFT );
		PORT_BIT	( 0x02, 0x00, IPT_JOYSTICK_RIGHT );
	
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_kaitei = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( kaitei )
		PORT_START(); 		/* IN0 SW0 */
		PORT_BIT	( 0x01, IP_ACTIVE_LOW,	IPT_COIN1 );
		PORT_BIT	( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT	( 0x04, IP_ACTIVE_LOW,	IPT_START1 );
		PORT_BIT	( 0x08, IP_ACTIVE_LOW,	IPT_START2 );
		PORT_BIT	( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	    PORT_BIT    ( 0x20, IP_ACTIVE_LOW,  IPT_UNKNOWN );
		PORT_BIT	( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT	( 0x80, IP_ACTIVE_LOW,	IPT_UNKNOWN );
	
		PORT_START();       /* IN1 SW1 */
		PORT_BIT	( 0x01, IP_ACTIVE_LOW,	IPT_JOYSTICK_RIGHT );
		PORT_BIT	( 0x02, IP_ACTIVE_LOW,	IPT_JOYSTICK_LEFT );
		PORT_BIT	( 0x04, IP_ACTIVE_LOW,	IPT_BUTTON1 );
		PORT_BIT	( 0x08, IP_ACTIVE_LOW,	IPT_JOYSTICK_RIGHT | IPF_COCKTAIL );
		PORT_BIT	( 0x10, IP_ACTIVE_LOW,	IPT_JOYSTICK_LEFT | IPF_COCKTAIL );
		PORT_BIT	( 0x20, IP_ACTIVE_LOW,	IPT_BUTTON1 | IPF_COCKTAIL );
	    PORT_BIT    ( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT	( 0x80, IP_ACTIVE_LOW,	IPT_UNKNOWN );
	
		PORT_START();       /* IN2 DSW2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(	0x01, DEF_STR( "Upright") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Cocktail"));
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Unknown") );
	    PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x02, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Lives") );
		PORT_DIPSETTING(	0x04, "5" );
	    PORT_DIPSETTING(    0x00, "7" );
	    PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
	    PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
	    PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unknown") );
	    PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x20, DEF_STR( "On") );
	    PORT_BIT    ( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT	( 0x80, IP_ACTIVE_LOW,	IPT_UNKNOWN );
	
		PORT_START(); 		/* IN3 VOLIN */
		PORT_BIT	( 0x01, IP_ACTIVE_LOW,	IPT_UNKNOWN );
		PORT_BIT	( 0x02, IP_ACTIVE_LOW,	IPT_UNKNOWN );
		PORT_BIT	( 0x04, IP_ACTIVE_LOW,	IPT_UNKNOWN );
	    PORT_BIT    ( 0x08, IP_ACTIVE_LOW,  IPT_UNKNOWN );
	    PORT_BIT    ( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	    PORT_BIT    ( 0x20, IP_ACTIVE_LOW,  IPT_UNKNOWN );
	    PORT_BIT    ( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT	( 0x80, IP_ACTIVE_LOW,	IPT_UNKNOWN );
	
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_kaitein = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( kaitein )
		PORT_START(); 		/* IN0 SW0 */
		PORT_BIT	( 0x01, IP_ACTIVE_LOW,	IPT_COIN1 );
		PORT_BIT	( 0x02, IP_ACTIVE_LOW,	IPT_COIN2 );
		PORT_BIT	( 0x04, IP_ACTIVE_LOW,	IPT_START1 );
		PORT_BIT	( 0x08, IP_ACTIVE_LOW,	IPT_START2 );
		PORT_BIT	( 0x10, IP_ACTIVE_LOW,	IPT_BUTTON1 );
		PORT_BIT	( 0x20, IP_ACTIVE_LOW,	IPT_UNKNOWN );
		PORT_BIT	( 0x40, IP_ACTIVE_LOW,	IPT_UNKNOWN );
		PORT_BIT	( 0x80, IP_ACTIVE_LOW,	IPT_UNKNOWN );
	
		PORT_START();       /* IN1 SW1 */
		PORT_BIT	( 0x01, IP_ACTIVE_LOW,	IPT_UNKNOWN );
		PORT_BIT	( 0x02, IP_ACTIVE_LOW,	IPT_UNKNOWN );
		PORT_BIT	( 0x04, IP_ACTIVE_LOW,	IPT_UNKNOWN );
	    PORT_BIT    ( 0x08, IP_ACTIVE_LOW,  IPT_UNKNOWN );
		PORT_BIT	( 0x10, IP_ACTIVE_LOW,	IPT_UNKNOWN );
	    PORT_BIT    ( 0x20, IP_ACTIVE_LOW,  IPT_UNKNOWN );
		PORT_BIT	( 0x40, IP_ACTIVE_LOW,	IPT_UNKNOWN );
	    PORT_BIT    ( 0x80, IP_ACTIVE_LOW,  IPT_UNKNOWN );
	
	    PORT_START();       /* IN2 DSW2 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(	0x00, "2" );
		PORT_DIPSETTING(	0x01, "3" );
		PORT_DIPSETTING(	0x02, "4" );
		PORT_DIPSETTING(	0x03, "5" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
	    PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x04, DEF_STR( "On") );
	    PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
	    PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x30, 0x10, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0x30, DEF_STR( "2C_1C") );
	    PORT_DIPSETTING(    0x10, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x20, DEF_STR( "1C_2C") );
	    PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_BIT	( 0x40, IP_ACTIVE_LOW,	IPT_UNKNOWN );
		PORT_BIT	( 0x80, IP_ACTIVE_LOW,	IPT_UNKNOWN );
	
		PORT_START(); 		/* IN3 VOLIN */
		PORT_BIT	( 0xff, 0x58, IPT_UNUSED );
	
		PORT_START(); 		/* IN4 two-way digital joystick */
		PORT_BIT	( 0x01, 0x00, IPT_JOYSTICK_LEFT );
		PORT_BIT	( 0x02, 0x00, IPT_JOYSTICK_RIGHT );
	
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_sos = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( sos )
	    PORT_START();       /* IN0 SW0 */
	    PORT_BIT    ( 0x01, IP_ACTIVE_LOW, IPT_COIN1   );
	    PORT_BIT    ( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );
	    PORT_BIT    ( 0x04, IP_ACTIVE_LOW, IPT_START1  );
	    PORT_BIT    ( 0x08, IP_ACTIVE_LOW, IPT_START2  );
	    PORT_BIT    ( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
	    PORT_BIT    ( 0x20, IP_ACTIVE_LOW, IPT_COIN2   );
	    PORT_BIT    ( 0xc0, 0x00, IPT_UNUSED );
	
	    PORT_START();       /* IN1 SW1 */
	    PORT_BIT    ( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
	    PORT_BIT    ( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );
	    PORT_BIT    ( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
	    PORT_BIT    ( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
	    PORT_BIT    ( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
	    PORT_BIT    ( 0x20, IP_ACTIVE_LOW, IPT_UNUSED );
	    PORT_BIT    ( 0xc0, 0x00, IPT_UNUSED );
	
	    PORT_START();       /* IN2 DSW2 */
	    PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Cabinet") );
	    PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
	    PORT_DIPSETTING(    0x01, DEF_STR( "Cocktail"));
	    PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Lives") );
	    PORT_DIPSETTING(    0x00, "2" );
	    PORT_DIPSETTING(    0x02, "3" );
	    PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
	    PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
	    PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x38, 0x08, DEF_STR( "Coinage") );
		PORT_DIPSETTING(	0x18, DEF_STR( "2C_1C") );
	    PORT_DIPSETTING(    0x08, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x30, DEF_STR( "1C_2C") );
	    PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
	    PORT_BIT    ( 0xc0, 0x00, IPT_UNUSED );
	
	    PORT_START();       /* IN3 VOLIN */
		PORT_ANALOG( 0xff, 0x58, IPT_PADDLE | IPF_REVERSE, 30, 15, 0x10, 0xa0 );
	
	INPUT_PORTS_END(); }}; 
	
	static GfxLayout charlayout_1k = new GfxLayout
	(
		8, 8,							   /* 8x8 pixels */
		128,							   /* 128 codes */
		1,								   /* 1 bit per pixel */
		new int[] {0},							   /* no bitplanes */
		/* x offsets */
		new int[] {0,1,2,3,4,5,6,7},
		/* y offsets */
	    new int[] {0*8,1*8,2*8,3*8,4*8,5*8,6*8,7*8},
		8 * 8							   /* eight bytes per code */
	);
	
	static GfxDecodeInfo gfxdecodeinfo_1k[] =
	{
		new GfxDecodeInfo( REGION_CPU1, 0x3000, charlayout_1k, 0, 4 ),
		new GfxDecodeInfo(-1)							   /* end of array */
	};
	
	static GfxLayout charlayout_2k = new GfxLayout
	(
	    8, 8,                              /* 8x8 pixels */
		256,							   /* 256 codes */
	    1,                                 /* 1 bit per pixel */
	    new int[] {0},                               /* no bitplanes */
	    /* x offsets */
	    new int[] {0,1,2,3,4,5,6,7},
	    /* y offsets */
	    new int[] {0*8,1*8,2*8,3*8,4*8,5*8,6*8,7*8},
	    8 * 8                              /* eight bytes per code */
	);
	
	static GfxDecodeInfo gfxdecodeinfo_2k[] =
	{
		new GfxDecodeInfo( REGION_CPU1, 0x3000, charlayout_2k, 0, 4 ),
		new GfxDecodeInfo(-1)							   /* end of array */
	};
	
	static struct CustomSound_interface custom_interface =
	{
		geebee_sh_start,
		geebee_sh_stop,
		geebee_sh_update
	};
	
	public static MachineHandlerPtr machine_driver_geebee = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(8080,18432000/9) 		/* 18.432 MHz / 9 */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_pulse,1)	/* one interrupt per frame */
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(34*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 34*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo_1k)
		MDRV_PALETTE_LENGTH(3)
		MDRV_COLORTABLE_LENGTH(4*2)
	
		MDRV_PALETTE_INIT(geebee)
		MDRV_VIDEO_START(geebee)
		MDRV_VIDEO_UPDATE(geebee)
	
		/* sound hardware */
		MDRV_SOUND_ADD(CUSTOM, custom_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	public static MachineHandlerPtr machine_driver_navalone = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(8080,18432000/9) 		/* 18.432 MHz / 9 */
		MDRV_CPU_MEMORY(readmem_navalone,writemem)
		MDRV_CPU_PORTS(readport_navalone,writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_pulse,1)	/* one interrupt per frame */
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(34*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 34*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo_2k)
		MDRV_PALETTE_LENGTH(3)
		MDRV_COLORTABLE_LENGTH(4*2)
	
		MDRV_PALETTE_INIT(navalone)
		MDRV_VIDEO_START(navalone)
		MDRV_VIDEO_UPDATE(geebee)
	
		/* sound hardware */
		MDRV_SOUND_ADD(CUSTOM, custom_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	public static MachineHandlerPtr machine_driver_kaitei = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(8080,18432000/9) 		/* 18.432 MHz / 9 */
		MDRV_CPU_MEMORY(readmem_navalone,writemem)
		MDRV_CPU_PORTS(readport_navalone,writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)	/* one interrupt per frame */
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(34*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 34*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo_2k)
		MDRV_PALETTE_LENGTH(3)
		MDRV_COLORTABLE_LENGTH(4*2)
	
		MDRV_PALETTE_INIT(navalone)
		MDRV_VIDEO_START(kaitei)
		MDRV_VIDEO_UPDATE(geebee)
	
		/* sound hardware */
		MDRV_SOUND_ADD(CUSTOM, custom_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	public static MachineHandlerPtr machine_driver_sos = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(8080,18432000/9) 		/* 18.432 MHz / 9 */
		MDRV_CPU_MEMORY(readmem_navalone,writemem)
		MDRV_CPU_PORTS(readport_navalone,writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_pulse,1)	/* one interrupt per frame */
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(34*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 34*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo_2k)
		MDRV_PALETTE_LENGTH(3)
		MDRV_COLORTABLE_LENGTH(4*2)
	
		MDRV_PALETTE_INIT(navalone)
		MDRV_VIDEO_START(sos)
		MDRV_VIDEO_UPDATE(geebee)
	
		/* sound hardware */
		MDRV_SOUND_ADD(CUSTOM, custom_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	
	static RomLoadHandlerPtr rom_geebee = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )
		ROM_LOAD( "geebee.1k",      0x0000, 0x1000, CRC(8a5577e0) SHA1(356d33e19c6b4f519816ee4b65ff9b59d6c1b565) )
		ROM_LOAD( "geebee.3a",      0x3000, 0x0400, CRC(f257b21b) SHA1(c788fd923438f1bffbff9ff3cd4c5c8b547c0c14) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_geebeeg = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )
		ROM_LOAD( "geebee.1k",      0x0000, 0x1000, CRC(8a5577e0) SHA1(356d33e19c6b4f519816ee4b65ff9b59d6c1b565) )
		ROM_LOAD( "geebeeg.3a",     0x3000, 0x0400, CRC(a45932ba) SHA1(48f70742c42a9377f31fac3a1e43123751e57656) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_navalone = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )
		ROM_LOAD( "navalone.p1",    0x0000, 0x0800, CRC(5a32016b) SHA1(d856d069eba470a81341de0bf47eca2a629a69a6) )
		ROM_LOAD( "navalone.p2",    0x0800, 0x0800, CRC(b1c86fe3) SHA1(0293b742806c1517cb126443701115a3427fc60a) )
		ROM_LOAD( "navalone.chr",   0x3000, 0x0800, CRC(b26c6170) SHA1(ae0aec2b60e1fd3b212e311afb1c588b2b286433) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_kaitei = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )
		ROM_LOAD( "kaitei_7.1k",    0x0000, 0x0800, CRC(32f70d48) SHA1(c5ae606df1d0e513daea909f5474309a176096c1) )
		ROM_RELOAD( 				0x0800, 0x0800 )
	    ROM_LOAD( "kaitei_1.1m",    0x1000, 0x0400, CRC(9a7ab3b9) SHA1(94a82ba66e51c8203ec61c9320edbddbb6462d33) )
		ROM_LOAD( "kaitei_2.1p",    0x1400, 0x0400, CRC(5eeb0fff) SHA1(91cb84a9af8e4df4e6c896e7655199328b7da30b) )
		ROM_LOAD( "kaitei_3.1s",    0x1800, 0x0400, CRC(5dff4df7) SHA1(c179c93a559a0d18db3092c842634de02f3f03ea) )
		ROM_LOAD( "kaitei_4.1t",    0x1c00, 0x0400, CRC(e5f303d6) SHA1(6dd57e0b17f51d101c6c5dbfeadb7418098cc440) )
		ROM_LOAD( "kaitei_5.bin",   0x3000, 0x0400, CRC(60fdb795) SHA1(723e635eed9937a28bee0b7978413984651ee87f) )
		ROM_LOAD( "kaitei_6.bin",   0x3400, 0x0400, CRC(21399ace) SHA1(0ad49be2c9bdab2f9dc41c7348d1d4b4b769e3c4) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_kaitein = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )
		ROM_LOAD( "kaitein.p1",     0x0000, 0x0800, CRC(d88e10ae) SHA1(76d6cd46b6e59e528e7a8fff9965375a1446a91d) )
		ROM_LOAD( "kaitein.p2",     0x0800, 0x0800, CRC(aa9b5763) SHA1(64a6c8f25b0510841dcce0b57505731aa0deeda7) )
		ROM_LOAD( "kaitein.chr",    0x3000, 0x0800, CRC(3125af4d) SHA1(9e6b161636665ee48d6bde2d5fc412fde382c687) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_sos = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )
		ROM_LOAD( "sos.p1",         0x0000, 0x0800, CRC(f70bdafb) SHA1(e71d552ccc9adad48225bdb4d62c31c5741a3e95) )
		ROM_LOAD( "sos.p2",         0x0800, 0x0800, CRC(58e9c480) SHA1(0eeb5982183d0e9f9dbae04839b604a0c22b420e) )
		ROM_LOAD( "sos.chr",        0x3000, 0x0800, CRC(66f983e4) SHA1(b3cf8bff4ac6b554d3fc06eeb8227b3b2a0dd554) )
	ROM_END(); }}; 
	
	
	
	public static DriverInitHandlerPtr init_geebee  = new DriverInitHandlerPtr() { public void handler(){
		artwork_set_overlay(geebee_overlay);
	} };
	
	
	public static GameDriver driver_geebee	   = new GameDriver("1978"	,"geebee"	,"geebee.java"	,rom_geebee,null	,machine_driver_geebee	,input_ports_geebee	,init_geebee	,ROT90, "Namco", "Gee Bee" )
	public static GameDriver driver_geebeeg	   = new GameDriver("1978"	,"geebeeg"	,"geebee.java"	,rom_geebeeg,driver_geebee	,machine_driver_geebee	,input_ports_geebee	,init_geebee	,ROT90, "[Namco] (Gremlin license)", "Gee Bee (Gremlin)" )
	public static GameDriver driver_navalone	   = new GameDriver("1980"	,"navalone"	,"geebee.java"	,rom_navalone,null	,machine_driver_navalone	,input_ports_navalone	,null	,ROT90, "Namco", "Navalone", GAME_IMPERFECT_SOUND )
	public static GameDriver driver_kaitei	   = new GameDriver("1980"	,"kaitei"	,"geebee.java"	,rom_kaitei,null	,machine_driver_kaitei	,input_ports_kaitei	,null	,ROT90, "K.K. Tokki", "Kaitei Takara Sagashi" )
	public static GameDriver driver_kaitein	   = new GameDriver("1980"	,"kaitein"	,"geebee.java"	,rom_kaitein,driver_kaitei	,machine_driver_kaitei	,input_ports_kaitein	,null	,ROT90, "Namco", "Kaitei Takara Sagashi (Namco)" )
	public static GameDriver driver_sos	   = new GameDriver("1980"	,"sos"	,"geebee.java"	,rom_sos,null	,machine_driver_sos	,input_ports_sos	,null	,ROT90, "Namco", "SOS", GAME_IMPERFECT_SOUND )
	
}
