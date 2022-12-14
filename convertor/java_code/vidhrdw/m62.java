/***************************************************************************

Video Hardware for Irem Games:
Battle Road, Lode Runner, Kid Niki, Spelunker

Tile/sprite priority system (for the Kung Fu Master M62 board):
- Tiles with color code >= N (where N is set by jumpers) have priority over
  sprites. Only bits 1-4 of the color code are used, bit 0 is ignored.

- Two jumpers select whether bit 5 of the sprite color code should be used
  to index the high address pin of the color PROMs, or to select high
  priority over tiles (or both, but is this used by any game?)

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class m62
{
	
	data8_t *m62_tileram;
	data8_t *m62_textram;
	data8_t *horizon_scrollram;
	
	static struct tilemap *m62_background;
	static struct tilemap *m62_foreground;
	static int flipscreen;
	static const unsigned char *sprite_height_prom;
	static int m62_background_hscroll;
	static int m62_background_vscroll;
	
	unsigned char *irem_textram;
	size_t irem_textram_size;
	
	static int kidniki_background_bank;
	static int kidniki_text_vscroll;
	
	static int spelunkr_palbank;
	
	/***************************************************************************
	
	  Convert the color PROMs into a more useable format.
	
	  Kung Fu Master has a six 256x4 palette PROMs (one per gun; three for
	  characters, three for sprites).
	  I don't know the exact values of the resistors between the RAM and the
	  RGB output. I assumed these values (the same as Commando)
	
	  bit 3 -- 220 ohm resistor  -- RED/GREEN/BLUE
	        -- 470 ohm resistor  -- RED/GREEN/BLUE
	        -- 1  kohm resistor  -- RED/GREEN/BLUE
	  bit 0 -- 2.2kohm resistor  -- RED/GREEN/BLUE
	
	***************************************************************************/
	public static PaletteInitHandlerPtr palette_init_irem  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
	
	
		for (i = 0;i < Machine.drv.total_colors;i++)
		{
			int bit0,bit1,bit2,bit3,r,g,b;
	
			/* red component */
			bit0 = (color_prom.read(0)>> 0) & 0x01;
			bit1 = (color_prom.read(0)>> 1) & 0x01;
			bit2 = (color_prom.read(0)>> 2) & 0x01;
			bit3 = (color_prom.read(0)>> 3) & 0x01;
			r =  0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
			/* green component */
			bit0 = (color_prom.read(Machine.drv.total_colors)>> 0) & 0x01;
			bit1 = (color_prom.read(Machine.drv.total_colors)>> 1) & 0x01;
			bit2 = (color_prom.read(Machine.drv.total_colors)>> 2) & 0x01;
			bit3 = (color_prom.read(Machine.drv.total_colors)>> 3) & 0x01;
			g =  0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
			/* blue component */
			bit0 = (color_prom.read(2*Machine.drv.total_colors)>> 0) & 0x01;
			bit1 = (color_prom.read(2*Machine.drv.total_colors)>> 1) & 0x01;
			bit2 = (color_prom.read(2*Machine.drv.total_colors)>> 2) & 0x01;
			bit3 = (color_prom.read(2*Machine.drv.total_colors)>> 3) & 0x01;
			b =  0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
	
			palette_set_color(i,r,g,b);
	
			color_prom++;
		}
	
		color_prom += 2*Machine.drv.total_colors;
		/* color_prom now points to the beginning of the sprite height table */
	
		sprite_height_prom = color_prom;	/* we'll need this at run time */
	} };
	
	public static PaletteInitHandlerPtr palette_init_battroad  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
	
	
		for (i = 0;i < 512;i++)
		{
			int bit0,bit1,bit2,bit3,r,g,b;
	
			/* red component */
			bit0 = (color_prom.read(0)>> 0) & 0x01;
			bit1 = (color_prom.read(0)>> 1) & 0x01;
			bit2 = (color_prom.read(0)>> 2) & 0x01;
			bit3 = (color_prom.read(0)>> 3) & 0x01;
			r =  0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
			/* green component */
			bit0 = (color_prom.read(512)>> 0) & 0x01;
			bit1 = (color_prom.read(512)>> 1) & 0x01;
			bit2 = (color_prom.read(512)>> 2) & 0x01;
			bit3 = (color_prom.read(512)>> 3) & 0x01;
			g =  0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
			/* blue component */
			bit0 = (color_prom.read(2*512)>> 0) & 0x01;
			bit1 = (color_prom.read(2*512)>> 1) & 0x01;
			bit2 = (color_prom.read(2*512)>> 2) & 0x01;
			bit3 = (color_prom.read(2*512)>> 3) & 0x01;
			b =  0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
	
			palette_set_color(i,r,g,b);
	
			color_prom++;
		}
	
		color_prom += 2*512;
		/* color_prom now points to the beginning of the character color prom */
	
		for (i = 0;i < 32;i++)
		{
			int bit0,bit1,bit2,r,g,b;
	
	
			bit0 = (color_prom.read(i)>> 0) & 0x01;
			bit1 = (color_prom.read(i)>> 1) & 0x01;
			bit2 = (color_prom.read(i)>> 2) & 0x01;
			r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
			bit0 = (color_prom.read(i)>> 3) & 0x01;
			bit1 = (color_prom.read(i)>> 4) & 0x01;
			bit2 = (color_prom.read(i)>> 5) & 0x01;
			g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
			bit0 = 0;
			bit1 = (color_prom.read(i)>> 6) & 0x01;
			bit2 = (color_prom.read(i)>> 7) & 0x01;
			b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			palette_set_color(i+512,r,g,b);
		}
	
		color_prom += 32;
		/* color_prom now points to the beginning of the sprite height table */
	
		sprite_height_prom = color_prom;	/* we'll need this at run time */
	} };
	
	public static PaletteInitHandlerPtr palette_init_spelunk2  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
	
	
		/* chars */
		for (i = 0;i < 512;i++)
		{
			int bit0,bit1,bit2,bit3,r,g,b;
	
			/* red component */
			bit0 = (color_prom.read(0)>> 0) & 0x01;
			bit1 = (color_prom.read(0)>> 1) & 0x01;
			bit2 = (color_prom.read(0)>> 2) & 0x01;
			bit3 = (color_prom.read(0)>> 3) & 0x01;
			r =  0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
			/* green component */
			bit0 = (color_prom.read(0)>> 4) & 0x01;
			bit1 = (color_prom.read(0)>> 5) & 0x01;
			bit2 = (color_prom.read(0)>> 6) & 0x01;
			bit3 = (color_prom.read(0)>> 7) & 0x01;
			g =  0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
			/* blue component */
			bit0 = (color_prom.read(2*256)>> 0) & 0x01;
			bit1 = (color_prom.read(2*256)>> 1) & 0x01;
			bit2 = (color_prom.read(2*256)>> 2) & 0x01;
			bit3 = (color_prom.read(2*256)>> 3) & 0x01;
			b =  0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
	
			palette_set_color(i,r,g,b);
	
			color_prom++;
		}
	
		color_prom += 2*256;
	
		/* sprites */
		for (i = 0;i < 256;i++)
		{
			int bit0,bit1,bit2,bit3,r,g,b;
	
			/* red component */
			bit0 = (color_prom.read(0)>> 0) & 0x01;
			bit1 = (color_prom.read(0)>> 1) & 0x01;
			bit2 = (color_prom.read(0)>> 2) & 0x01;
			bit3 = (color_prom.read(0)>> 3) & 0x01;
			r =  0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
			/* green component */
			bit0 = (color_prom.read(256)>> 0) & 0x01;
			bit1 = (color_prom.read(256)>> 1) & 0x01;
			bit2 = (color_prom.read(256)>> 2) & 0x01;
			bit3 = (color_prom.read(256)>> 3) & 0x01;
			g =  0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
			/* blue component */
			bit0 = (color_prom.read(2*256)>> 0) & 0x01;
			bit1 = (color_prom.read(2*256)>> 1) & 0x01;
			bit2 = (color_prom.read(2*256)>> 2) & 0x01;
			bit3 = (color_prom.read(2*256)>> 3) & 0x01;
			b =  0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
	
			palette_set_color(i+512,r,g,b);
	
			color_prom++;
		}
	
		color_prom += 2*256;
	
	
		/* color_prom now points to the beginning of the sprite height table */
		sprite_height_prom = color_prom;	/* we'll need this at run time */
	} };
	
	
	
	static void register_savestate(void)
	{
		state_save_register_int  ("video", 0, "flipscreen",              &flipscreen);
		state_save_register_int  ("video", 0, "kidniki_background_bank", &kidniki_background_bank);
		state_save_register_int  ("video", 0, "m62_background_hscroll", &m62_background_hscroll);
		state_save_register_int  ("video", 0, "m62_background_vscroll", &m62_background_vscroll);
		state_save_register_int  ("video", 0, "kidniki_text_vscroll",    &kidniki_text_vscroll);
		state_save_register_int  ("video", 0, "spelunkr_palbank",        &spelunkr_palbank);
		state_save_register_UINT8("video", 0, "irem_textram",            irem_textram,   irem_textram_size);
	}
	
	public static WriteHandlerPtr m62_flipscreen_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		/* screen flip is handled both by software and hardware */
		data ^= ~readinputport(4) & 1;
	
		flipscreen = data & 0x01;
		if (flipscreen)
			tilemap_set_flip(ALL_TILEMAPS, TILEMAP_FLIPX | TILEMAP_FLIPY);
		else
			tilemap_set_flip(ALL_TILEMAPS, 0);
	
		coin_counter_w(0,data & 2);
		coin_counter_w(1,data & 4);
	} };
	
	public static WriteHandlerPtr m62_hscroll_low_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		m62_background_hscroll = ( m62_background_hscroll & 0xff00 ) | data;
	} };
	
	public static WriteHandlerPtr m62_hscroll_high_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		m62_background_hscroll = ( m62_background_hscroll & 0xff ) | ( data << 8 );
	} };
	
	public static WriteHandlerPtr m62_vscroll_low_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		m62_background_vscroll = ( m62_background_vscroll & 0xff00 ) | data;
	} };
	
	public static WriteHandlerPtr m62_vscroll_high_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		m62_background_vscroll = ( m62_background_vscroll & 0xff ) | ( data << 8 );
	} };
	
	public static WriteHandlerPtr m62_tileram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		m62_tileram[ offset ] = data;
		tilemap_mark_tile_dirty( m62_background, offset >> 1 );
	} };
	
	public static WriteHandlerPtr m62_textram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		m62_textram[ offset ] = data;
		tilemap_mark_tile_dirty( m62_foreground, offset >> 1 );
	} };
	
	/***************************************************************************
	
	  Draw the game screen in the given mame_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
	static void draw_sprites(struct mame_bitmap *bitmap, int colormask, int prioritymask, int priority)
	{
		int offs;
	
		for (offs = 0;offs < spriteram_size;offs += 8)
		{
			int i,incr,code,col,flipx,flipy,sx,sy;
	
			if( ( spriteram.read(offs)& prioritymask ) == priority )
			{
				code = spriteram.read(offs+4)+ ((spriteram.read(offs+5)& 0x07) << 8);
				col = spriteram.read(offs+0)& colormask;
				sx = 256 * (spriteram.read(offs+7)& 1) + spriteram.read(offs+6),
				sy = 256+128-15 - (256 * (spriteram.read(offs+3)& 1) + spriteram.read(offs+2)),
				flipx = spriteram.read(offs+5)& 0x40;
				flipy = spriteram.read(offs+5)& 0x80;
	
				i = sprite_height_prom[(code >> 5) & 0x1f];
				if (i == 1)	/* double height */
				{
					code &= ~1;
					sy -= 16;
				}
				else if (i == 2)	/* quadruple height */
				{
					i = 3;
					code &= ~3;
					sy -= 3*16;
				}
	
				if (flipscreen)
				{
					sx = 496 - sx;
					sy = 242 - i*16 - sy;	/* sprites are slightly misplaced by the hardware */
					flipx = NOT(flipx);
					flipy = NOT(flipy);
				}
	
				if (flipy)
				{
					incr = -1;
					code += i;
				}
				else incr = 1;
	
				do
				{
					drawgfx(bitmap,Machine->gfx[1],
							code + i * incr,col,
							flipx,flipy,
							sx,sy + 16 * i,
							Machine->visible_area,TRANSPARENCY_PEN,0);
	
					i--;
				} while (i >= 0);
			}
		}
	}
	
	int m62_start( void (*tile_get_info)( int memory_offset ), int rows, int cols, int x1, int y1, int x2, int y2 )
	{
		m62_background = tilemap_create( tile_get_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, x1, y1, x2, y2 );
		if( !m62_background )
		{
			return 1;
		}
	
		m62_background_hscroll = 0;
		m62_background_vscroll = 0;
	
		register_savestate();
	
		if( rows != 0 )
		{
			tilemap_set_scroll_rows( m62_background, rows );
		}
		if( cols != 0 )
		{
			tilemap_set_scroll_cols( m62_background, cols );
		}
	
		return 0;
	}
	
	int m62_textlayer( void (*tile_get_info)( int memory_offset ), int rows, int cols, int x1, int y1, int x2, int y2 )
	{
		m62_foreground = tilemap_create( tile_get_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, x1, y1, x2, y2 );
		if( !m62_foreground )
		{
			return 1;
		}
	
		if( rows != 0 )
		{
			tilemap_set_scroll_rows( m62_foreground, rows );
		}
		if( cols != 0 )
		{
			tilemap_set_scroll_cols( m62_foreground, cols );
		}
	
		return 0;
	}
	
	public static WriteHandlerPtr kungfum_tileram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		m62_tileram[ offset ] = data;
		tilemap_mark_tile_dirty( m62_background, offset & 0x7ff );
	} };
	
	static void get_kungfum_bg_tile_info( int offs )
	{
		int code;
		int color;
		int flags;
		code = m62_tileram[ offs ];
		color = m62_tileram[ offs + 0x800 ];
		flags = 0;
		if( ( color & 0x20 ) )
		{
			flags |= TILE_FLIPX;
		}
		SET_TILE_INFO( 0, code | ( ( color & 0xc0 ) << 2 ), color & 0x1f, flags );
	
		/* is the following right? */
		if( ( offs / 64 ) < 6 || ( ( color & 0x1f ) >> 1 ) > 0x0c )
		{
			tile_info.priority = 1;
		}
		else
		{
			tile_info.priority = 0;
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_kungfum  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		int i;
		for( i = 0; i < 6; i++ )
		{
			tilemap_set_scrollx( m62_background, i, 0 );
		}
		for( i = 6; i < 32; i++ )
		{
			tilemap_set_scrollx( m62_background, i, m62_background_hscroll );
		}
		tilemap_draw( bitmap, cliprect, m62_background, 0, 0 );
		draw_sprites( bitmap, 0x1f, 0x00, 0x00 );
		tilemap_draw( bitmap, cliprect, m62_background, 1, 0 );
	} };
	
	public static VideoStartHandlerPtr video_start_kungfum  = new VideoStartHandlerPtr() { public int handler(){
		return m62_start( get_kungfum_bg_tile_info, 32, 0, 8, 8, 64, 32 );
	} };
	
	
	static void get_ldrun_bg_tile_info( int offs )
	{
		int code;
		int color;
		int flags;
		code = m62_tileram[ offs << 1 ];
		color = m62_tileram[ ( offs << 1 ) | 1 ];
		flags = 0;
		if( ( color & 0x20 ) )
		{
			flags |= TILE_FLIPX;
		}
		SET_TILE_INFO( 0, code | ( ( color & 0xc0 ) << 2 ), color & 0x1f, flags );
		if( ( ( color & 0x1f ) >> 1 ) >= 0x04 )
		{
			tile_info.priority = 1;
		}
		else
		{
			tile_info.priority = 0;
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_ldrun  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_set_scrollx( m62_background, 0, m62_background_hscroll );
		tilemap_set_scrolly( m62_background, 0, m62_background_vscroll );
	
		tilemap_draw( bitmap, cliprect, m62_background, 0, 0 );
		draw_sprites( bitmap, 0x0f, 0x10, 0x00 );
		tilemap_draw( bitmap, cliprect, m62_background, 1, 0 );
		draw_sprites( bitmap, 0x0f, 0x10, 0x10 );
	} };
	
	public static VideoStartHandlerPtr video_start_ldrun  = new VideoStartHandlerPtr() { public int handler(){
		return m62_start( get_ldrun_bg_tile_info, 1, 1, 8, 8, 64, 32 );
	} };
	
	
	static void get_battroad_bg_tile_info( int offs )
	{
		int code;
		int color;
		int flags;
		code = m62_tileram[ offs << 1 ];
		color = m62_tileram[ ( offs << 1 ) | 1 ];
		flags = 0;
		if( ( color & 0x20 ) )
		{
			flags |= TILE_FLIPX;
		}
		SET_TILE_INFO( 0, code | ( ( color & 0x40 ) << 3 ) | ( ( color & 0x10 ) << 4 ), color & 0x0f, flags );
		if( ( ( color & 0x0f ) >> 1 ) >= 0x04 )
		{
			tile_info.priority = 1;
		}
		else
		{
			tile_info.priority = 0;
		}
	}
	
	static void get_battroad_fg_tile_info( int offs )
	{
		int code;
		int color;
		code = m62_textram[ offs << 1 ];
		color = m62_textram[ ( offs << 1 ) | 1 ];
		SET_TILE_INFO( 2, code | ( ( color & 0x40 ) << 3 ) | ( ( color & 0x10 ) << 4 ), color & 0x0f, 0 );
	}
	
	public static VideoUpdateHandlerPtr video_update_battroad  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_set_scrollx( m62_background, 0, m62_background_hscroll );
		tilemap_set_scrolly( m62_background, 0, m62_background_vscroll );
		tilemap_set_scrollx( m62_foreground, 0, 128 );
		tilemap_set_scrolly( m62_foreground, 0, 0 );
		tilemap_set_transparent_pen( m62_foreground, 0 );
	
		tilemap_draw( bitmap, cliprect, m62_background, 0, 0 );
		draw_sprites( bitmap, 0x0f, 0x10, 0x00 );
		tilemap_draw( bitmap, cliprect, m62_background, 1, 0 );
		draw_sprites( bitmap, 0x0f, 0x10, 0x10 );
		tilemap_draw( bitmap, cliprect, m62_foreground, 0, 0 );
	} };
	
	public static VideoStartHandlerPtr video_start_battroad  = new VideoStartHandlerPtr() { public int handler(){
		return m62_start( get_battroad_bg_tile_info, 1, 1, 8, 8, 64, 32 ) ||
			m62_textlayer( get_battroad_fg_tile_info, 1, 1, 8, 8, 32, 32 );
	} };
	
	
	/* almost identical but scrolling background, more characters, */
	/* no char x flip, and more sprites */
	static void get_ldrun4_bg_tile_info( int offs )
	{
		int code;
		int color;
		code = m62_tileram[ offs << 1 ];
		color = m62_tileram[ ( offs << 1 ) | 1 ];
		SET_TILE_INFO( 0, code | ( ( color & 0xc0 ) << 2 ) | ( ( color & 0x20 ) << 5 ), color & 0x1f, 0 );
	}
	
	public static VideoUpdateHandlerPtr video_update_ldrun4  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_set_scrollx( m62_background, 0, m62_background_hscroll );
	
		tilemap_draw( bitmap, cliprect, m62_background, 0, 0 );
		draw_sprites( bitmap, 0x1f, 0x00, 0x00 );
	} };
	
	public static VideoStartHandlerPtr video_start_ldrun4  = new VideoStartHandlerPtr() { public int handler(){
		return m62_start( get_ldrun4_bg_tile_info, 1, 0, 8, 8, 64, 32 );
	} };
	
	
	static void get_lotlot_bg_tile_info( int offs )
	{
		int code;
		int color;
		int flags;
		code = m62_tileram[ offs << 1 ];
		color = m62_tileram[ ( offs << 1 ) | 1 ];
		flags = 0;
		if( ( color & 0x20 ) )
		{
			flags |= TILE_FLIPX;
		}
		SET_TILE_INFO( 0, code | ( ( color & 0xc0 ) << 2 ), color & 0x1f, flags );
	}
	
	static void get_lotlot_fg_tile_info( int offs )
	{
		int code;
		int color;
		code = m62_textram[ offs << 1 ];
		color = m62_textram[ ( offs << 1 ) | 1 ];
		SET_TILE_INFO( 2, code | ( ( color & 0xc0 ) << 2 ), color & 0x1f, 0 );
	}
	
	public static VideoUpdateHandlerPtr video_update_lotlot  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_set_scrollx( m62_background, 0, m62_background_hscroll - 64 );
		tilemap_set_scrolly( m62_background, 0, m62_background_vscroll + 32 );
		tilemap_set_scrollx( m62_foreground, 0, -64 );
		tilemap_set_scrolly( m62_foreground, 0, 32 );
		tilemap_set_transparent_pen( m62_foreground, 0 );
	
		tilemap_draw( bitmap, cliprect, m62_background, 0, 0 );
		draw_sprites( bitmap, 0x1f, 0x00, 0x00 );
		tilemap_draw( bitmap, cliprect, m62_foreground, 0, 0 );
	} };
	
	public static VideoStartHandlerPtr video_start_lotlot  = new VideoStartHandlerPtr() { public int handler(){
		return m62_start( get_lotlot_bg_tile_info, 1, 1, 12, 10, 32, 64 ) ||
			m62_textlayer( get_lotlot_fg_tile_info, 1, 1, 12, 10, 32, 64 );
	} };
	
	
	public static WriteHandlerPtr kidniki_text_vscroll_low_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		kidniki_text_vscroll = (kidniki_text_vscroll & 0xff00) | data;
	} };
	
	public static WriteHandlerPtr kidniki_text_vscroll_high_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		kidniki_text_vscroll = (kidniki_text_vscroll & 0xff) | (data << 8);
	} };
	
	public static WriteHandlerPtr kidniki_background_bank_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (kidniki_background_bank != (data & 1))
		{
			kidniki_background_bank = data & 1;
			memset(dirtybuffer,1,videoram_size[0]);
		}
	} };
	
	static void get_kidniki_bg_tile_info( int offs )
	{
		int code;
		int color;
		code = m62_tileram[ offs << 1 ];
		color = m62_tileram[ ( offs << 1 ) | 1 ];
		SET_TILE_INFO( 0, code | ( ( color & 0xe0 ) << 3 ) | ( kidniki_background_bank << 11 ), color & 0x1f, 0 );
	}
	
	static void get_kidniki_fg_tile_info( int offs )
	{
		int code;
		int color;
		code = m62_textram[ offs << 1 ];
		color = m62_textram[ ( offs << 1 ) | 1 ];
		SET_TILE_INFO( 2, code | ( ( color & 0xc0 ) << 2 ), color & 0x1f, 0 );
	}
	
	public static VideoUpdateHandlerPtr video_update_kidniki  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_set_scrollx( m62_background, 0, m62_background_hscroll );
		tilemap_set_scrollx( m62_foreground, 0, -64 );
		tilemap_set_scrolly( m62_foreground, 0, kidniki_text_vscroll + 128 );
		tilemap_set_transparent_pen( m62_foreground, 0 );
	
		tilemap_draw( bitmap, cliprect, m62_background, 0, 0 );
		draw_sprites( bitmap, 0x1f, 0x00, 0x00 );
		tilemap_draw( bitmap, cliprect, m62_foreground, 0, 0 );
	} };
	
	public static VideoStartHandlerPtr video_start_kidniki  = new VideoStartHandlerPtr() { public int handler(){
		return m62_start( get_kidniki_bg_tile_info, 1, 0, 8, 8, 64, 32 ) ||
			m62_textlayer( get_kidniki_fg_tile_info, 1, 1, 12, 8, 32, 64 );
	} };
	
	
	public static WriteHandlerPtr spelunkr_palbank_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (spelunkr_palbank != (data & 0x01))
		{
			spelunkr_palbank = data & 0x01;
			memset(dirtybuffer,1,videoram_size[0]);
		}
	} };
	
	static void get_spelunkr_bg_tile_info( int offs )
	{
		int code;
		int color;
		code = m62_tileram[ offs << 1 ];
		color = m62_tileram[ ( offs << 1 ) | 1 ];
		SET_TILE_INFO( 0, code | ( ( color & 0x10 ) << 4 ) | ( ( color & 0x20 ) << 6 ) | ( ( color & 0xc0 ) << 3 ), ( color & 0x1f ) | ( spelunkr_palbank << 4 ), 0 );
	}
	
	static void get_spelunkr_fg_tile_info( int offs )
	{
		int code;
		int color;
		code = m62_textram[ offs << 1 ];
		color = m62_textram[ ( offs << 1 ) | 1 ];
		SET_TILE_INFO( 2, code | ( ( color & 0x10 ) << 4 ), ( color & 0x0f ) | ( spelunkr_palbank << 4 ), 0 );
	}
	
	public static VideoUpdateHandlerPtr video_update_spelunkr  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_set_scrollx( m62_background, 0, m62_background_hscroll );
		tilemap_set_scrolly( m62_background, 0, m62_background_vscroll + 128 );
		tilemap_set_scrollx( m62_foreground, 0, -64 );
		tilemap_set_scrolly( m62_foreground, 0, 0 );
		tilemap_set_transparent_pen( m62_foreground, 0 );
	
		tilemap_draw( bitmap, cliprect, m62_background, 0, 0 );
		draw_sprites( bitmap, 0x1f, 0x00, 0x00 );
		tilemap_draw( bitmap, cliprect, m62_foreground, 0, 0 );
	} };
	
	public static VideoStartHandlerPtr video_start_spelunkr  = new VideoStartHandlerPtr() { public int handler(){
		return m62_start( get_spelunkr_bg_tile_info, 1, 1, 8, 8, 64, 64 ) ||
			m62_textlayer( get_spelunkr_fg_tile_info, 1, 1, 12, 8, 32, 32 );
	} };
	
	
	public static WriteHandlerPtr spelunk2_gfxport_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		m62_hscroll_high_w(0,(data&2)>>1);
		m62_vscroll_high_w(0,(data&1));
		if (spelunkr_palbank != ((data & 0x0c) >> 2))
		{
			spelunkr_palbank = (data & 0x0c) >> 2;
			memset(dirtybuffer,1,videoram_size[0]);
		}
	} };
	
	static void get_spelunk2_bg_tile_info( int offs )
	{
		int code;
		int color;
		code = m62_tileram[ offs << 1 ];
		color = m62_tileram[ ( offs << 1 ) | 1 ];
		SET_TILE_INFO( 0, code | ( ( color & 0xf0 ) << 4 ), ( color & 0x0f ) | ( spelunkr_palbank << 4 ), 0 );
	}
	
	public static VideoUpdateHandlerPtr video_update_spelunk2  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_set_scrollx( m62_background, 0, m62_background_hscroll );
		tilemap_set_scrolly( m62_background, 0, m62_background_vscroll + 128 );
		tilemap_set_scrollx( m62_foreground, 0, -64 );
		tilemap_set_scrolly( m62_foreground, 0, 0 );
		tilemap_set_transparent_pen( m62_foreground, 0 );
	
		tilemap_draw( bitmap, cliprect, m62_background, 0, 0 );
		draw_sprites( bitmap, 0x1f, 0x00, 0x00 );
		tilemap_draw( bitmap, cliprect, m62_foreground, 0, 0 );
	} };
	
	public static VideoStartHandlerPtr video_start_spelunk2  = new VideoStartHandlerPtr() { public int handler(){
		return m62_start( get_spelunk2_bg_tile_info, 1, 1, 8, 8, 64, 64 ) ||
			m62_textlayer( get_spelunkr_fg_tile_info, 1, 1, 12, 8, 32, 32 );
	} };
	
	
	static void get_youjyudn_bg_tile_info( int offs )
	{
		int code;
		int color;
		code = m62_tileram[ offs << 1 ];
		color = m62_tileram[ ( offs << 1 ) | 1 ];
		SET_TILE_INFO( 0, code | ( ( color & 0x60 ) << 3 ), color & 0x1f, 0 );
		if( ( ( color & 0x1f ) >> 1 ) >= 0x08 )
		{
			tile_info.priority = 1;
		}
		else
		{
			tile_info.priority = 0;
		}
	}
	
	static void get_youjyudn_fg_tile_info( int offs )
	{
		int code;
		int color;
		code = m62_textram[ offs << 1 ];
		color = m62_textram[ ( offs << 1 ) | 1 ];
		SET_TILE_INFO( 2, code | ( ( color & 0xc0 ) << 2 ), ( color & 0x0f ), 0 );
	}
	
	public static VideoUpdateHandlerPtr video_update_youjyudn  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_set_scrollx( m62_background, 0, m62_background_hscroll );
		tilemap_set_scrollx( m62_foreground, 0, -64 );
		tilemap_set_scrolly( m62_foreground, 0, 0 );
		tilemap_set_transparent_pen( m62_foreground, 0 );
	
		tilemap_draw( bitmap, cliprect, m62_background, 0, 0 );
		draw_sprites( bitmap, 0x1f, 0x00, 0x00 );
		tilemap_draw( bitmap, cliprect, m62_background, 1, 0 );
		tilemap_draw( bitmap, cliprect, m62_foreground, 0, 0 );
	} };
	
	public static VideoStartHandlerPtr video_start_youjyudn  = new VideoStartHandlerPtr() { public int handler(){
		return m62_start( get_youjyudn_bg_tile_info, 1, 0, 8, 16, 64, 16 ) ||
			m62_textlayer( get_youjyudn_fg_tile_info, 1, 1, 12, 8, 32, 32 );
	} };
	
	
	public static WriteHandlerPtr horizon_scrollram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		horizon_scrollram[ offset ] = data;
	} };
	
	static void get_horizon_bg_tile_info( int offs )
	{
		int code;
		int color;
		code = m62_tileram[ offs << 1 ];
		color = m62_tileram[ ( offs << 1 ) | 1 ];
		SET_TILE_INFO( 0, code | ( ( color & 0xc0 ) << 2 ) | ( ( color & 0x20 ) << 5 ), color & 0x1f, 0 );
		if( ( ( color & 0x1f ) >> 1 ) >= 0x08 )
		{
			tile_info.priority = 1;
		}
		else
		{
			tile_info.priority = 0;
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_horizon  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		int i;
		for( i = 0; i < 32; i++ )
		{
			tilemap_set_scrollx( m62_background, i, horizon_scrollram[ i << 1 ] | ( horizon_scrollram[ ( i << 1 ) | 1 ] << 8 ) );
		}
		tilemap_draw( bitmap, cliprect, m62_background, 0, 0 );
		draw_sprites( bitmap, 0x1f, 0x00, 0x00 );
		tilemap_draw( bitmap, cliprect, m62_background, 1, 0 );
	} };
	
	public static VideoStartHandlerPtr video_start_horizon  = new VideoStartHandlerPtr() { public int handler(){
		return m62_start( get_horizon_bg_tile_info, 32, 0, 8, 8, 64, 32 );
	} };
}
