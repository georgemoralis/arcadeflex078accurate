/*************************************************************************

	Atari Centipede hardware

*************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class centiped
{
	
	
	static struct tilemap *tilemap;
	UINT8 centiped_flipscreen;
	
	
	
	/*************************************
	 *
	 *	Tilemap callback
	 *
	 *************************************/
	
	public static GetTileInfoHandlerPtr centiped_get_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int data = videoram.read(tile_index);
		SET_TILE_INFO(0, (data & 0x3f) + 0x40, 0, TILE_FLIPYX(data >> 6));
	} };
	
	
	public static GetTileInfoHandlerPtr warlords_get_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int data = videoram.read(tile_index);
		int color = ((tile_index & 0x10) >> 4) | ((tile_index & 0x200) >> 8) | (centiped_flipscreen >> 5);
		SET_TILE_INFO(0, data & 0x3f, color, TILE_FLIPYX(data >> 6));
	} };
	
	
	public static GetTileInfoHandlerPtr milliped_get_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int data = videoram.read(tile_index);
		int bank = (data >> 6) & 1;
		int color = (data >> 6) & 3;
		SET_TILE_INFO(0, (data & 0x3f) + 0x40 + (bank * 0x80), color, 0);
	} };
	
	
	/*************************************
	 *
	 *	Video system start
	 *
	 *************************************/
	
	public static VideoStartHandlerPtr video_start_centiped  = new VideoStartHandlerPtr() { public int handler(){
		tilemap = tilemap_create(centiped_get_tile_info, tilemap_scan_rows, TILEMAP_OPAQUE, 8,8, 32,32);
		if (!tilemap)
			return 1;
	
		centiped_flipscreen = 0;
		return 0;
	} };
	
	
	public static VideoStartHandlerPtr video_start_warlords  = new VideoStartHandlerPtr() { public int handler(){
		tilemap = tilemap_create(warlords_get_tile_info, tilemap_scan_rows, TILEMAP_OPAQUE, 8,8, 32,32);
		if (!tilemap)
			return 1;
	
		/* we overload centiped_flipscreen here to track the cocktail/upright state */
		centiped_flipscreen = readinputport(0) & 0x80;
		return 0;
	} };
	
	
	public static VideoStartHandlerPtr video_start_milliped  = new VideoStartHandlerPtr() { public int handler(){
		tilemap = tilemap_create(milliped_get_tile_info, tilemap_scan_rows, TILEMAP_OPAQUE, 8,8, 32,32);
		if (!tilemap)
			return 1;
	
		centiped_flipscreen = 0;
		return 0;
	} };
	
	
	
	/*************************************
	 *
	 *	Video RAM writes
	 *
	 *************************************/
	
	public static WriteHandlerPtr centiped_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		videoram.write(offset,data);
		tilemap_mark_tile_dirty(tilemap, offset);
	} };
	
	
	
	/*************************************
	 *
	 *	Screen flip
	 *
	 *************************************/
	
	public static WriteHandlerPtr centiped_flip_screen_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		centiped_flipscreen = data >> 7;
	} };
	
	
	
	/*************************************
	 *
	 *	Palette init
	 *
	 *************************************/
	
	/***************************************************************************
	
		Centipede doesn't have a color PROM. Eight RAM locations control
		the color of characters and sprites. The meanings of the four bits are
		(all bits are inverted):
	
		bit 3 alternate
		      blue
		      green
		bit 0 red
	
		The alternate bit affects blue and green, not red. The way I weighted its
		effect might not be perfectly accurate, but is reasonably close.
	
	***************************************************************************/
	
	#define TOTAL_COLORS(gfxn) (Machine->gfx[gfxn]->total_colors * Machine->gfx[gfxn]->color_granularity)
	#define COLOR(gfxn,offs) (colortable[Machine->drv->gfxdecodeinfo[gfxn].color_codes_start + offs])
	
	public static PaletteInitHandlerPtr palette_init_centiped  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
	
		/* characters use colors 0-3 */
		for (i = 0; i < TOTAL_COLORS(0); i++)
			COLOR(0,i) = i;
	
		/* Centipede is unusual because the sprite color code specifies the */
		/* colors to use one by one, instead of a combination code. */
		/* bit 5-4 = color to use for pen 11 */
		/* bit 3-2 = color to use for pen 10 */
		/* bit 1-0 = color to use for pen 01 */
		/* pen 00 is transparent */
		for (i = 0; i < TOTAL_COLORS(1); i += 4)
		{
			COLOR(1,i+0) = 4;
			COLOR(1,i+1) = 4 + ((i >> 2) & 3);
			COLOR(1,i+2) = 4 + ((i >> 4) & 3);
			COLOR(1,i+3) = 4 + ((i >> 6) & 3);
		}
	} };
	
	
	public static WriteHandlerPtr centiped_paletteram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int r, g, b;
	
		paletteram.write(offset,data);
	
		r = 0xff * ((~data >> 0) & 1);
		g = 0xff * ((~data >> 1) & 1);
		b = 0xff * ((~data >> 2) & 1);
	
		if (~data & 0x08) /* alternate = 1 */
		{
			/* when blue component is not 0, decrease it. When blue component is 0, */
			/* decrease green component. */
			if (b) b = 0xc0;
			else if (g) g = 0xc0;
		}
	
		if (offset >= 4 && offset < 8)
			palette_set_color(offset - 4, r, g, b);
		else if (offset >= 12 && offset < 16)
			palette_set_color(4 + (offset - 12), r, g, b);
	} };
	
	
	
	/***************************************************************************
	
		Convert the color PROM into a more useable format.
	
		The palette PROM are connected to the RGB output this way:
	
		bit 2 -- RED
		      -- GREEN
		bit 0 -- BLUE
	
	***************************************************************************/
	
	public static PaletteInitHandlerPtr palette_init_warlords  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i, j;
	
		for (i = 0; i < Machine.drv.total_colors; i++)
		{
			int r = ((color_prom.read()>> 2) & 0x01) * 0xff;
			int g = ((color_prom.read()>> 1) & 0x01) * 0xff;
			int b = ((color_prom.read()>> 0) & 0x01) * 0xff;
	
			/* Colors 0x40-0x7f are converted to grey scale as it's used on the
			   upright version that had an overlay */
			if (i >= Machine.drv.total_colors / 2)
			{
				/* Use the standard ratios: r = 30%, g = 59%, b = 11% */
				int grey = (r * 0x4d / 0xff) + (g * 0x96 / 0xff) + (b * 0x1c / 0xff);
				r = g = b = grey;
			}
			palette_set_color(i,r,g,b);
			color_prom++;
		}
	
		for (i = 0; i < 8; i++)
			for (j = 0; j < 4; j++)
			{
				COLOR(0,i*4+j) = i*16+j;
				COLOR(1,i*4+j) = i*16+j*4;
			}
	} };
	
	
	
	/***************************************************************************
	
		Millipede doesn't have a color PROM, it uses RAM.
		The RAM seems to be conncted to the video output this way:
	
		bit 7 red
		      red
		      red
		      green
		      green
		      blue
		      blue
		bit 0 blue
	
	***************************************************************************/
	
	public static PaletteInitHandlerPtr palette_init_milliped  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
	
		/* characters use colors 0-15 */
		for (i = 0; i < TOTAL_COLORS(0); i++)
			COLOR(0,i) = i;
	
		/* Millipede is unusual because the sprite color code specifies the */
		/* colors to use one by one, instead of a combination code. */
		/* bit 7-6 = palette bank (there are 4 groups of 4 colors) */
		/* bit 5-4 = color to use for pen 11 */
		/* bit 3-2 = color to use for pen 10 */
		/* bit 1-0 = color to use for pen 01 */
		/* pen 00 is transparent */
		for (i = 0; i < TOTAL_COLORS(1); i += 4)
		{
			COLOR(1,i+0) = 16 + 4*((i >> 8) & 3);
			COLOR(1,i+1) = 16 + 4*((i >> 8) & 3) + ((i >> 2) & 3);
			COLOR(1,i+2) = 16 + 4*((i >> 8) & 3) + ((i >> 4) & 3);
			COLOR(1,i+3) = 16 + 4*((i >> 8) & 3) + ((i >> 6) & 3);
		}
	} };
	
	
	public static WriteHandlerPtr milliped_paletteram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int bit0,bit1,bit2;
		int r,g,b;
	
		paletteram.write(offset,data);
	
		/* red component */
		bit0 = (~data >> 5) & 0x01;
		bit1 = (~data >> 6) & 0x01;
		bit2 = (~data >> 7) & 0x01;
		r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
		/* green component */
		bit0 = 0;
		bit1 = (~data >> 3) & 0x01;
		bit2 = (~data >> 4) & 0x01;
		g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
		/* blue component */
		bit0 = (~data >> 0) & 0x01;
		bit1 = (~data >> 1) & 0x01;
		bit2 = (~data >> 2) & 0x01;
		b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
		palette_set_color(offset, r, g, b);
	} };
	
	
	
	/*************************************
	 *
	 *	Video update
	 *
	 *************************************/
	
	public static VideoUpdateHandlerPtr video_update_centiped  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		struct rectangle spriteclip = *cliprect;
		int offs;
	
		/* draw the background */
		tilemap_draw(bitmap, cliprect, tilemap, 0, 0);
	
		/* apply the sprite clip */
		if (centiped_flipscreen)
			spriteclip.min_x += 8;
		else
			spriteclip.max_x -= 8;
	
		/* draw the sprites */
		for (offs = 0; offs < 0x10; offs++)
		{
			int code = ((spriteram.read(offs)& 0x3e) >> 1) | ((spriteram.read(offs)& 0x01) << 6);
			int color = spriteram.read(offs + 0x30);
			int flipy = spriteram.read(offs)& 0x80;
			int x = spriteram.read(offs + 0x20);
			int y = 240 - spriteram.read(offs + 0x10);
	
			drawgfx(bitmap, Machine.gfx[1], code, color & 0x3f, centiped_flipscreen, flipy, x, y,
					&spriteclip, TRANSPARENCY_PEN, 0);
		}
	} };
	
	
	public static VideoUpdateHandlerPtr video_update_warlords  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		int upright_mode = input_port_0_r(0) & 0x80;
		int offs;
	
		/* if the cocktail/upright switch flipped, force refresh */
		if (centiped_flipscreen != upright_mode)
		{
			centiped_flipscreen = upright_mode;
			tilemap_set_flip(tilemap, upright_mode ? TILEMAP_FLIPX : 0);
			tilemap_mark_all_tiles_dirty(tilemap);
		}
	
		/* draw the background */
		tilemap_draw(bitmap, cliprect, tilemap, 0, 0);
	
		/* draw the sprites */
		for (offs = 0; offs < 0x10; offs++)
		{
			int code = spriteram.read(offs)& 0x3f;
			int flipx = spriteram.read(offs)& 0x40;
			int flipy = spriteram.read(offs)& 0x80;
			int x = spriteram.read(offs + 0x20);
			int y = 248 - spriteram.read(offs + 0x10);
	
			/* The four quadrants have different colors. This is not 100% accurate,
			   because right on the middle the sprite could actually have two or more
			   different color, but this is not noticable, as the color that
			   changes between the quadrants is mostly used on the paddle sprites */
			int color = ((y & 0x80) >> 6) | ((x & 0x80) >> 7) | (upright_mode >> 5);
	
			/* in upright mode, sprites are flipped */
			if (upright_mode)
			{
				x = 248 - x;
				flipx = NOT(flipx);
			}
	
			drawgfx(bitmap, Machine.gfx[1], code, color, flipx, flipy, x, y,
					cliprect, TRANSPARENCY_PEN, 0);
		}
	} };
}
