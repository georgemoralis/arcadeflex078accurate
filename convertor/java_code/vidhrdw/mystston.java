/***************************************************************************

	vidhrdw.c

	Functions to emulate the video hardware of the machine.

	There are only a few differences between the video hardware of Mysterious
	Stones and Mat Mania. The tile bank select bit is different and the sprite
	selection seems to be different as well. Additionally, the palette is stored
	differently. I'm also not sure that the 2nd tile page is really used in
	Mysterious Stones.

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class mystston
{
	
	
	UINT8 *mystston_videoram2;
	
	static int mystston_fgcolor, mystston_bgpage;
	
	static struct tilemap *fg_tilemap, *bg_tilemap;
	
	/***************************************************************************
	
	  Convert the color PROMs into a more useable format.
	
	  Mysterious Stones has both palette RAM and a PROM. The PROM is used for
	  text.
	
	***************************************************************************/
	
	public static PaletteInitHandlerPtr palette_init_mystston  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
	
		for (i = 0; i < 32; i++)
		{
			int bit0, bit1, bit2, r, g, b;
	
			// red component
	
			bit0 = (color_prom.read()>> 0) & 0x01;
			bit1 = (color_prom.read()>> 1) & 0x01;
			bit2 = (color_prom.read()>> 2) & 0x01;
	
			r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			// green component
	
			bit0 = (color_prom.read()>> 3) & 0x01;
			bit1 = (color_prom.read()>> 4) & 0x01;
			bit2 = (color_prom.read()>> 5) & 0x01;
	
			g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			// blue component
	
			bit0 = 0;
			bit1 = (color_prom.read()>> 6) & 0x01;
			bit2 = (color_prom.read()>> 7) & 0x01;
	
			b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			palette_set_color(i + 24, r, g, b);	// first 24 colors are from RAM
	
			color_prom++;
		}
	} };
	
	public static WriteHandlerPtr mystston_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (videoram.read(offset)!= data)
		{
			videoram.write(offset,data);
			tilemap_mark_tile_dirty(fg_tilemap, offset & 0x3ff);
		}
	} };
	
	public static WriteHandlerPtr mystston_videoram2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (videoram.read(offset)!= data)
		{
			mystston_videoram2[offset] = data;
			tilemap_mark_tile_dirty(bg_tilemap, offset & 0x1ff);
		}
	} };
	
	public static WriteHandlerPtr mystston_scroll_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		tilemap_set_scrolly(bg_tilemap, 0, data);
	} };
	
	public static WriteHandlerPtr mystston_control_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		// bits 0 and 1 are foreground text color
		if (mystston_fgcolor != ((data & 0x01) << 1) + ((data & 0x02) >> 1))
		{
			mystston_fgcolor = ((data & 0x01) << 1) + ((data & 0x02) >> 1);
			tilemap_mark_all_tiles_dirty(fg_tilemap);
		}
	
		// bit 2 is background page select
		mystston_bgpage = (data & 0x04) ? 1:0;
	
		// bits 4 and 5 are coin counters in flipped order
		coin_counter_w(0, data & 0x20);
		coin_counter_w(1, data & 0x10);
	
		// bit 7 is screen flip
		flip_screen_set((data & 0x80) ^ ((readinputport(3) & 0x20) ? 0x80:0));
	} };
	
	public static GetTileInfoHandlerPtr get_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code = mystston_videoram2[tile_index] + ((mystston_videoram2[tile_index + 0x200] & 0x01) << 8);
		int flags = (tile_index & 0x10) ? TILE_FLIPY : 0;
	
		SET_TILE_INFO(1, code, 0, flags)
	} };
	
	public static GetTileInfoHandlerPtr get_fg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code = videoram.read(tile_index)+ ((videoram.read(tile_index + 0x400)& 0x07) << 8);
		int color = mystston_fgcolor;
		
		SET_TILE_INFO(0, code, color, 0)
	} };
	
	public static VideoStartHandlerPtr video_start_mystston  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_bg_tile_info, tilemap_scan_cols_flip_x, 
			TILEMAP_OPAQUE, 16, 16, 16, 32);
	
		if ( !bg_tilemap )
			return 1;
	
		fg_tilemap = tilemap_create(get_fg_tile_info, tilemap_scan_cols_flip_x, 
			TILEMAP_TRANSPARENT, 8, 8, 32, 32);
	
		if ( !fg_tilemap )
			return 1;
	
		tilemap_set_transparent_pen(fg_tilemap, 0);
	
		return 0;
	} };
	
	static void draw_sprites( struct mame_bitmap *bitmap, const struct rectangle *cliprect )
	{
		int offs;
	
		for (offs = 0; offs < spriteram_size; offs += 4)
		{
			int attr = spriteram.read(offs);
	
			if (attr & 0x01)
			{
				int code = spriteram.read(offs + 1)+ ((attr & 0x10) << 4);
				int color = (attr & 0x08) >> 3;
				int flipx = attr & 0x04;
				int flipy = attr & 0x02;
				int sx = 240 - spriteram.read(offs + 3);
				int sy = (240 - spriteram.read(offs + 2)) & 0xff;
	
				if (flip_screen())
				{
					sx = 240 - sx;
					sy = 240 - sy;
					flipx = NOT(flipx);
					flipy = NOT(flipy);
				}
	
				drawgfx(bitmap,Machine->gfx[2],	code, color, flipx, flipy,
					sx, sy, cliprect, TRANSPARENCY_PEN, 0);
			}
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_mystston  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap, cliprect, bg_tilemap, 0, 0);
		draw_sprites(bitmap, cliprect);
		tilemap_draw(bitmap, cliprect, fg_tilemap, 0, 0);
	} };
}
