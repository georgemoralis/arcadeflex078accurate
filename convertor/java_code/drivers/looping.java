/*
To Do:
- get sound working
- map and test any remaining input ports

Looping
(C)1981 Venture Line

	Main CPU
		TMS9995

	COP420 Microcontroller
		manages CPU communnication?

	Sound CPU
		TMS9980
		AY-3-8910
		TMS5220 (SPEECH)

---------------------------------------------------------------

Sky Bumper
(C)1982 Venture Line

	This is a ROM swap for Looping.  There are two 6116's on
	the CPU board, where there is only one on Looping.

---------------------------------------------------------------

Super Tank
(C)19?? Venture Line

Runs on simpler hardware; not yet emulated.

===============================================================

LOOPING CHIP PLACEMENT

THERE ARE AT LEAST TWO VERSIONS OF THIS GAME
VERSION NUMBERS FOR THIS PURPOSE ARE CHOSEN AT RANDOM

IC NAME   POSITION   BOARD  TYPE   IC NAME  POSITION  TYPE
VER-1                         VER-2
---------------------------------------------------------------
LOS-2-7   13A        I/O    2532    SAME    13A       2532
LOS-1-1-2 11A         "      "      SAME    11A        "
LOS-3-1   13C         "      "      I-O-V2  13C        "

VLI1      2A         ROM    2764    VLI-7-1 2A         "
VLI3      5A          "      "      VLI-7-2 4A         "
VLI9-5    8A          "      "      VLI-4-3 5A         "
L056-6    9A          "      "      VLI-8-4 7A         "
                      "             LO56-5  8A         "
                      "             LO56-6  9A         "
                      "             VLI-8-7 10A        "
                  ON RIBBON CABLE   18S030  11B				color prom?
                     REAR BD      LOG.1-9-3 6A        2716	tiles
                                  LOG.3     8A         "	tiles
*/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class looping
{
	
	static struct tilemap *tilemap;
	
	public static PaletteInitHandlerPtr palette_init_looping  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
		for (i = 0;i < 0x20;i++)
		{
			int bit0,bit1,bit2,r,g,b;
	
			/* red component */
			bit0 = (color_prom.read()>> 0) & 0x01;
			bit1 = (color_prom.read()>> 1) & 0x01;
			bit2 = (color_prom.read()>> 2) & 0x01;
			r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
			/* green component */
			bit0 = (color_prom.read()>> 3) & 0x01;
			bit1 = (color_prom.read()>> 4) & 0x01;
			bit2 = (color_prom.read()>> 5) & 0x01;
			g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
			/* blue component */
			bit0 = (color_prom.read()>> 6) & 0x01;
			bit1 = (color_prom.read()>> 7) & 0x01;
			b = 0x4f * bit0 + 0xa8 * bit1;
	
			palette_set_color(i,r,g,b);
			color_prom++;
		}
	} };
	
	static void get_tile_info( int offset )
	{
		int tile_number = videoram.read(offset);
		int color = colorram.read((offset&0x1f)*2+1)&0x7;
		SET_TILE_INFO(
				0,
				tile_number,
				color,
				0)
	}
	
	public static WriteHandlerPtr looping_flip_screen_x_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		flip_screen_x_set(~data & 0x01);
	} };
	
	public static WriteHandlerPtr looping_flip_screen_y_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		flip_screen_y_set(~data & 0x01);
	} };
	
	public static WriteHandlerPtr looping_colorram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int i,offs;
		if( colorram.read(offset)!=data )
		{
			colorram.write(offset,data);
			if( offset&1 )
			{
				/* odd bytes are column color attribute */
				offs = (offset/2);
				/* mark the whole column dirty */
				for( i=0; i<0x20; i++ )
				{
					tilemap_mark_tile_dirty( tilemap, offs );
					offs += 0x20;
				}
			}
			else
			{
				/* even bytes are column scroll */
				tilemap_set_scrolly( tilemap,offset/2,data );
			}
		}
	} };
	
	public static VideoStartHandlerPtr video_start_looping  = new VideoStartHandlerPtr() { public int handler(){
		tilemap = tilemap_create( get_tile_info,tilemap_scan_rows,TILEMAP_OPAQUE,8,8,32,32 );
		if( tilemap )
		{
			tilemap_set_scroll_cols( tilemap, 0x20 );
			return 0;
		}
		return -1;
	} };
	
	public static WriteHandlerPtr looping_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( videoram.read(offset)!=data )
		{
			videoram.write(offset,data);
			tilemap_mark_tile_dirty( tilemap, offset );
		}
	} };
	
	static void draw_sprites( struct mame_bitmap *bitmap, const struct rectangle *cliprect )
	{
		const UINT8 *source = spriteram;
		const UINT8 *finish = source + 0x10*4; /* ? */
	
		UINT8 sx, sy;
		int flipx, flipy, code, color;
	
		while( source < finish )
		{
			sx = source[3];
			sy = source[0];
			flipx = source[1] & 0x40;
			flipy = source[1] & 0x80;
			code  = source[1] & 0x3f;
			color = source[2];
	
			if (flip_screen_x)
			{
				sx = 240 - sx;
				flipx = NOT(flipx);
			}
	
			if (flip_screen_y)
			{
				flipy = NOT(flipy);
			}
			else
			{
				sy = 240 - sy;
			}
	
			drawgfx( bitmap, Machine->gfx[1],
					code, color,
					flipx, flipy,
					sx, sy,
					Machine->visible_area,
					TRANSPARENCY_PEN, 0 );
	
			source += 4;
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_looping  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw( bitmap,cliprect,tilemap,0,0 );
		draw_sprites( bitmap,cliprect );
	} };
	
	public static WriteHandlerPtr looping_intack = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (data==0)
		{
			cpu_irq_line_vector_w(0, 0, 4);
			cpu_set_irq_line(0, 0, CLEAR_LINE);
		}
	} };
	
	public static InterruptHandlerPtr looping_interrupt = new InterruptHandlerPtr() {public void handler(){
		cpu_irq_line_vector_w(0, 0, 4);
		cpu_set_irq_line(0, 0, ASSERT_LINE);
	} };
	
	/****** sound *******/
	
	public static WriteHandlerPtr looping_soundlatch_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		soundlatch_w.handler(offset, data);
		cpu_irq_line_vector_w(1, 0, 4);
		cpu_set_irq_line(1, 0, ASSERT_LINE);
	} };
	
	public static WriteHandlerPtr looping_souint_clr = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (data==0)
		{
			cpu_irq_line_vector_w(1, 0, 4);
			cpu_set_irq_line(1, 0, CLEAR_LINE);
		}
	} };
	
	void looping_spcint(int state)
	{
		cpu_irq_line_vector_w(1, 0, 6);
		cpu_set_irq_line(1, 0, state);
	}
	
	public static WriteHandlerPtr looping_sound_sw = new WriteHandlerPtr() {public void handler(int offset, int data){
		/* this can be improved by adding the missing
		   signals for decay etc. (see schematics) */
		static int r[8];
		r[offset]=data^1;
		DAC_data_w(0, ((r[1]<<7) + (r[2]<<6))*r[6]);
	} };
	
	public static Memory_ReadAddress looping_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
	/*	new Memory_ReadAddress( 0x9000, 0x9fff, MRA_RAM ), videoram is write only? */
		new Memory_ReadAddress( 0xe000, 0xefff, MRA_RAM ),
		new Memory_ReadAddress( 0xf800, 0xf800, input_port_0_r ),	/* inp */
		new Memory_ReadAddress( 0xf801, 0xf801, input_port_1_r ),
		new Memory_ReadAddress( 0xf802, 0xf802, input_port_2_r ),	/* dsw */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress looping_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x9000, 0x93ff, looping_videoram_w, videoram ),
		new Memory_WriteAddress( 0x9800, 0x983f, looping_colorram_w, colorram ),
		new Memory_WriteAddress( 0x9840, 0x987f, MWA_RAM, spriteram ),
		new Memory_WriteAddress( 0xe000, 0xefff, MWA_RAM ),
		new Memory_WriteAddress( 0xb006, 0xb006, looping_flip_screen_x_w ),
		new Memory_WriteAddress( 0xb007, 0xb007, looping_flip_screen_y_w ),
		new Memory_WriteAddress( 0xf801, 0xf801, looping_soundlatch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	static PORT_WRITE_START( looping_writeport)
		{ 0x000, 0x000, MWA_NOP },
		{ 0x406, 0x406, looping_intack },
		{ 0x407, 0x407, watchdog_reset_w },
	PORT_END
	
	public static Memory_ReadAddress looping_io_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x37ff, MRA_ROM ),
		new Memory_ReadAddress( 0x3800, 0x3bff, MRA_RAM ),
		new Memory_ReadAddress( 0x3c00, 0x3c00, AY8910_read_port_0_r ),
		new Memory_ReadAddress( 0x3e02, 0x3e02, tms5220_status_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress looping_io_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x37ff, MWA_ROM ),
		new Memory_WriteAddress( 0x3800, 0x3bff, MWA_RAM ),
		new Memory_WriteAddress( 0x3c00, 0x3c00, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0x3c02, 0x3c02, AY8910_write_port_0_w ),
		new Memory_WriteAddress( 0x3e00, 0x3e00, tms5220_data_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	static PORT_WRITE_START( looping_io_writeport)
		{ 0x000, 0x000, looping_souint_clr },
		{ 0x001, 0x007, looping_sound_sw },
	PORT_END
	
	static GfxLayout tile_layout = new GfxLayout
	(
		8,8,		/* 8*8 characters */
		0x100,		/* number of characters */
		2,			/* 2 bits per pixel */
		new int[] { 0,0x800*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8
	);
	
	static GfxLayout sprite_layout = new GfxLayout
	(
		16,16,		/* 8*8 characters */
		0x40,		/* number of characters */
		2,			/* 2 bits per pixel */
		new int[] { 0,0x800*8 },
		new int[] {
			0, 1, 2, 3, 4, 5, 6, 7,
			64+0, 64+1, 64+2, 64+3, 64+4, 64+5, 64+6, 64+7
		},
		new int[] {
			0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
			128+0*8, 128+1*8, 128+2*8, 128+3*8, 128+4*8, 128+5*8, 128+6*8, 128+7*8
		},
		8*8*4
	);
	
	static GfxDecodeInfo looping_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, tile_layout,		0, 8 ),
		new GfxDecodeInfo( REGION_GFX1, 0, sprite_layout,	0, 8 ),
		new GfxDecodeInfo( -1 )
	};
	
	static struct TMS5220interface tms5220_interface =
	{
		640000,         /* clock speed (80*samplerate) */
		50,             /* volume */
		looping_spcint  /* IRQ handler */
	};
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		1,
		2000000,
		new int[] { 20 },
		new ReadHandlerPtr[] { soundlatch_r },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	static DACinterface dac_interface = new DACinterface
	(
		1,
		new int[] { 30 }
	);
	
	public static MachineHandlerPtr machine_driver_looping = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(TMS9995, 3000000) /* ? */
		MDRV_CPU_MEMORY(looping_readmem,looping_writemem)
		MDRV_CPU_PORTS(0,looping_writeport)
		MDRV_CPU_VBLANK_INT(looping_interrupt,1)
	
		MDRV_CPU_ADD(TMS9980, 2000000) // ?
		MDRV_CPU_MEMORY(looping_io_readmem,looping_io_writemem)
		MDRV_CPU_PORTS(0,looping_io_writeport)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(2500)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(looping_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(32)
		MDRV_COLORTABLE_LENGTH(32)
	
		MDRV_PALETTE_INIT(looping)
		MDRV_VIDEO_START(looping)
		MDRV_VIDEO_UPDATE(looping)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
		MDRV_SOUND_ADD(TMS5220, tms5220_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	
	static InputPortHandlerPtr input_ports_looping = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( looping )
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 );/* shoot */
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );/* accel? */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START();  /* cocktail? */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x18, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x0e, 0x02, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x0a, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x0e, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(    0x00, "1 Coin/10 Credits" );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );		// Check code at 0x2c00
		PORT_DIPSETTING(	0x00, DEF_STR( "No") );
		PORT_DIPSETTING(	0x10, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x20, "5" );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	/* Same as 'looping' but additional "Infinite Lives" Dip Switch */
	static InputPortHandlerPtr input_ports_skybump = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( skybump )
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 );/* shoot */
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );/* accel? */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START();  /* cocktail? */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x18, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x0e, 0x02, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x0a, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x0e, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(    0x00, "1 Coin/10 Credits" );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );		// Check code at 0x2c00
		PORT_DIPSETTING(	0x00, DEF_STR( "No") );
		PORT_DIPSETTING(	0x10, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x60, 0x40, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x40, "3" );
		PORT_DIPSETTING(    0x60, "5" );
		PORT_BITX( 0,       0x00, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Infinite", IP_KEY_NONE, IP_JOY_NONE );
	//	PORT_BITX( 0,       0x20, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Infinite", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
	INPUT_PORTS_END(); }}; 
	
	static RomLoadHandlerPtr rom_loopinga = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 ) /* 64k for TMS9995 code */
		ROM_LOAD( "vli3.5a",		0x0000, 0x2000, CRC(1ac3ccdf) SHA1(9d1cde8bd4d0f12eaf06225b3ecc4a5c3e4f0c11) )
		ROM_LOAD( "vli-4-3",		0x2000, 0x1000, CRC(f32cae2b) SHA1(2c6ef82af438e588b56fd58b95cf969c97bb9a66) )
		ROM_LOAD( "vli-8-4",		0x3000, 0x1000, CRC(611e1dbf) SHA1(0ab6669f1dec30c3f7bca49e158e4790a78fa308) )
		ROM_LOAD( "l056-6.9a",		0x4000, 0x2000, CRC(548afa52) SHA1(0b88ac7394feede023519c585a4084591eb9661a) )
		ROM_LOAD( "vli9-5.8a",		0x6000, 0x2000, CRC(5d122f86) SHA1(d1c66b890142bb4d4648f3edec6567f58107dbf0) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 ) /* 64k for TMS9980 code */
		ROM_LOAD( "i-o-v2.13c",		0x0000, 0x0800, CRC(09765ebe) SHA1(93b035c3a94f2f6d5e463256e26b600a4dd5d3ea) )
	    ROM_LOAD( "i-o.13a",		0x0800, 0x1000, CRC(1de29f25) SHA1(535acb132266d6137b0610ee9a9b946459ae44af) ) /* speech */
		ROM_LOAD( "i-o.11a",		0x2800, 0x1000, CRC(61c74c79) SHA1(9f34d18a919446dd76857b851cea23fc1526f3c2) )
	
		ROM_REGION( 0x1000, REGION_CPU3, 0 ) /* COP420 microcontroller code */
		ROM_LOAD( "cop.bin",		0x0000, 0x1000, CRC(bbfd26d5) SHA1(5f78b32b6e7c003841ef5b635084db2cdfebf0e1) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "log1-9-3.6a",	0x0000, 0x800, CRC(c434c14c) SHA1(3669aaf7adc6b250378bcf62eb8e7058f55476ef) )
		ROM_LOAD( "log2.8a",		0x0800, 0x800, CRC(ef3284ac) SHA1(8719c9df8c972a56c306b3c707aaa53092ffa2d6) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 ) /* color prom */
		ROM_LOAD( "18s030.11b",		0x0000, 0x0020, CRC(6a0c7d87) SHA1(140335d85c67c75b65689d4e76d29863c209cf32) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_looping = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 ) /* 64k for TMS9995 code */
		ROM_LOAD( "vli3.5a",		0x0000, 0x2000, CRC(1ac3ccdf) SHA1(9d1cde8bd4d0f12eaf06225b3ecc4a5c3e4f0c11) )
		ROM_LOAD( "vli1.2a",		0x2000, 0x2000, CRC(97755fd4) SHA1(4a6ef02b0128cd516ff95083a7caaad8f3756f09) )
		ROM_LOAD( "l056-6.9a",		0x4000, 0x2000, CRC(548afa52) SHA1(0b88ac7394feede023519c585a4084591eb9661a) )
		ROM_LOAD( "vli9-5.8a",		0x6000, 0x2000, CRC(5d122f86) SHA1(d1c66b890142bb4d4648f3edec6567f58107dbf0) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 ) /* 64k for TMS9980 code */
		ROM_LOAD( "i-o.13c",		0x0000, 0x0800, CRC(21e9350c) SHA1(f30a180309e373a17569351944f5e7982c3b3f9d) )
		ROM_LOAD( "i-o.13a",		0x0800, 0x1000, CRC(1de29f25) SHA1(535acb132266d6137b0610ee9a9b946459ae44af) )
		ROM_LOAD( "i-o.11a",		0x2800, 0x1000, CRC(61c74c79) SHA1(9f34d18a919446dd76857b851cea23fc1526f3c2) ) /* speech */
	
		ROM_REGION( 0x1000, REGION_CPU3, 0 ) /* COP420 microcontroller code */
		ROM_LOAD( "cop.bin",		0x0000, 0x1000, CRC(bbfd26d5) SHA1(5f78b32b6e7c003841ef5b635084db2cdfebf0e1) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "log1-9-3.6a",	0x0000, 0x800, CRC(c434c14c) SHA1(3669aaf7adc6b250378bcf62eb8e7058f55476ef) )
		ROM_LOAD( "log2.8a",		0x0800, 0x800, CRC(ef3284ac) SHA1(8719c9df8c972a56c306b3c707aaa53092ffa2d6) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 ) /* color prom */
		ROM_LOAD( "18s030.11b",		0x0000, 0x0020, CRC(6a0c7d87) SHA1(140335d85c67c75b65689d4e76d29863c209cf32) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_skybump = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 ) /* 64k for TMS9995 code */
		ROM_LOAD( "cpu.5a",			0x0000, 0x2000, CRC(dca38df0) SHA1(86abe04cbabf81399f842f53668fe7a3f7ed3757) )
		ROM_LOAD( "cpu.2a",			0x2000, 0x2000, CRC(6bcc211a) SHA1(245ebae3934df9c3920743a941546d96bb2e7c03) )
		ROM_LOAD( "cpu.9a",			0x4000, 0x2000, CRC(c7a50797) SHA1(60aa0a28ba970f12d0a0e538ae1c6807d105855c) )
		ROM_LOAD( "cpu.8a",			0x6000, 0x2000, CRC(a718c6f2) SHA1(19afa8c353829232cb96c27b87f13b43166ab6fc) )
	
	    ROM_REGION( 0x10000, REGION_CPU2, 0 ) /* 64k for TMS9980 code */
		ROM_LOAD( "snd.13c",		0x0000, 0x0800, CRC(21e9350c) SHA1(f30a180309e373a17569351944f5e7982c3b3f9d) )
		ROM_LOAD( "snd.13a",		0x0800, 0x1000, CRC(1de29f25) SHA1(535acb132266d6137b0610ee9a9b946459ae44af) )
		ROM_LOAD( "snd.11a",		0x2800, 0x1000, CRC(61c74c79) SHA1(9f34d18a919446dd76857b851cea23fc1526f3c2) )
	
		ROM_REGION( 0x1000, REGION_CPU3, 0 ) /* COP420 microcontroller code */
		ROM_LOAD( "cop.bin",		0x0000, 0x1000, CRC(bbfd26d5) SHA1(5f78b32b6e7c003841ef5b635084db2cdfebf0e1) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "vid.6a",			0x0000, 0x800, CRC(12ebbe74) SHA1(0f87c81a45d1bf3b8c6a70ee5e1a014069f67755) )
		ROM_LOAD( "vid.8a",			0x0800, 0x800, CRC(459ccc55) SHA1(747f6789605b48be9e22f779f9e3f6c98ad4e594) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 ) /* color prom */
		ROM_LOAD( "vid.clr",		0x0000, 0x0020, CRC(6a0c7d87) SHA1(140335d85c67c75b65689d4e76d29863c209cf32) )
	ROM_END(); }}; 
	
	public static DriverInitHandlerPtr init_looping  = new DriverInitHandlerPtr() { public void handler()
		/* unscramble the TMS9995 ROMs */
		UINT8 *pMem = memory_region( REGION_CPU1 );
		UINT8 raw,code;
		int i;
		for( i=0; i<0x8000; i++ )
		{
			raw = pMem[i];
			code = 0;
			if( raw&0x01 ) code |= 0x80;
			if( raw&0x02 ) code |= 0x40;
			if( raw&0x04 ) code |= 0x20;
			if( raw&0x08 ) code |= 0x10;
			if( raw&0x10 ) code |= 0x08;
			if( raw&0x20 ) code |= 0x04;
			if( raw&0x40 ) code |= 0x02;
			if( raw&0x80 ) code |= 0x01;
			pMem[i] = code;
		} };
	}
	
	/*          rom       parent    machine   inp       init */
	public static GameDriver driver_looping	   = new GameDriver("1982"	,"looping"	,"looping.java"	,rom_looping,null	,machine_driver_looping	,input_ports_looping	,init_looping	,ROT90, "Venture Line", "Looping (set 1)" )
	public static GameDriver driver_loopinga	   = new GameDriver("1982"	,"loopinga"	,"looping.java"	,rom_loopinga,driver_looping	,machine_driver_looping	,input_ports_looping	,init_looping	,ROT90, "Venture Line", "Looping (set 2)" )
	public static GameDriver driver_skybump	   = new GameDriver("1982"	,"skybump"	,"looping.java"	,rom_skybump,null	,machine_driver_looping	,input_ports_skybump	,init_looping	,ROT90, "Venture Line", "Sky Bumper" )
	
}
