/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class mrjong
{
	
	static struct tilemap *bg_tilemap;
	
	/***************************************************************************
	
	  Convert the color PROMs. (from vidhrdw/penco.c)
	
	***************************************************************************/
	public static PaletteInitHandlerPtr palette_init_mrjong  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
		#define TOTAL_COLORS(gfxn) (Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity)
		#define COLOR(gfxn, offs) (colortable[Machine.drv.gfxdecodeinfo[gfxn].color_codes_start + (offs)])
	
		for (i = 0; i < Machine.drv.total_colors; i++)
		{
			int bit0, bit1, bit2, r, g, b;
	
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
			bit0 = 0;
			bit1 = (color_prom.read()>> 6) & 0x01;
			bit2 = (color_prom.read()>> 7) & 0x01;
			b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			palette_set_color(i,r,g,b);
			color_prom++;
		}
	
		color_prom += 0x10;
		/* color_prom now points to the beginning of the lookup table */
	
		/* character lookup table */
		/* sprites use the same color lookup table as characters */
		for (i = 0; i < TOTAL_COLORS(0); i++)
			COLOR(0, i) = *(color_prom++) & 0x0f;
	} };
	
	
	/***************************************************************************
	
	  Display control parameter.
	
	***************************************************************************/
	public static WriteHandlerPtr mrjong_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (videoram.read(offset)!= data)
		{
			videoram.write(offset,data);
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr mrjong_colorram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (colorram.read(offset)!= data)
		{
			colorram.write(offset,data);
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr mrjong_flipscreen_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (flip_screen() != (data & 0x01))
		{
			flip_screen_set(data & 0x01);
			tilemap_mark_all_tiles_dirty(ALL_TILEMAPS);
		}
	} };
	
	public static GetTileInfoHandlerPtr get_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code = videoram.read(tile_index)| ((colorram.read(tile_index)& 0x20) << 3);
		int color = colorram.read(tile_index)& 0x1f;
		int flags = ((colorram.read(tile_index)& 0x40) ? TILE_FLIPX : 0) | ((colorram.read(tile_index)& 0x80) ? TILE_FLIPY : 0);
	
		SET_TILE_INFO(0, code, color, flags)
	} };
	
	public static VideoStartHandlerPtr video_start_mrjong  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_bg_tile_info, tilemap_scan_rows_flip_xy,
			TILEMAP_OPAQUE, 8, 8, 32, 32);
	
		if ( !bg_tilemap )
			return 1;
	
		return 0;
	} };
	
	static void mrjong_draw_sprites( struct mame_bitmap *bitmap )
	{
		int offs;
	
		for (offs = (spriteram_size - 4); offs >= 0; offs -= 4)
		{
			int sprt;
			int color;
			int sx, sy;
			int flipx, flipy;
	
			sprt = (((spriteram.read(offs + 1)>> 2) & 0x3f) | ((spriteram.read(offs + 3)& 0x20) << 1));
			flipx = (spriteram.read(offs + 1)& 0x01) >> 0;
			flipy = (spriteram.read(offs + 1)& 0x02) >> 1;
			color = (spriteram.read(offs + 3)& 0x1f);
	
			sx = 224 - spriteram.read(offs + 2);
			sy = spriteram.read(offs + 0);
			if (flip_screen())
			{
				sx = 208 - sx;
				sy = 240 - sy;
				flipx = NOT(flipx);
				flipy = NOT(flipy);
			}
	
			drawgfx(bitmap, Machine->gfx[1],
					sprt,
					color,
					flipx, flipy,
					sx, sy,
					Machine->visible_area, TRANSPARENCY_PEN, 0);
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_mrjong  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap, Machine.visible_area, bg_tilemap, 0, 0);
		mrjong_draw_sprites(bitmap);
	} };
}
