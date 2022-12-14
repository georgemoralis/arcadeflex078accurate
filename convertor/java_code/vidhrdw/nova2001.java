/*******************************************************************************

     Nova 2001 - Video Description:
     --------------------------------------------------------------------------

     Foreground Playfield Chars (static)
        character code index from first set of chars
     Foreground Playfield color modifier RAM
        Char colors are normally taken from the first set of 16,
        This is the pen for "color 1" for this tile (from the first 16 pens)

     Background Playfield Chars (scrolling)
        character code index from second set of chars
     Foreground Playfield color modifier RAM
        Char colors are normally taken from the second set of 16,
        This is the pen for "color 1" for this tile (from the second 16 pens)
     (Scrolling in controlled via the 8910 A and B port outputs)

     Sprite memory is made of 32 byte records:

        Sprite+0, 0x80 = Sprite Bank
        Sprite+0, 0x7f = Sprite Character Code
        Sprite+1, 0xff = X location
        Sprite+2, 0xff = Y location
        Sprite+3, 0x20 = Y Flip
        Sprite+3, 0x10 = X Flip
        Sprite+3, 0x0f = pen for "color 1" taken from the first 16 colors
	Sprite+3, 0x80
	Sprite+3, 0x40

        All the rest are unknown and/or uneccessary.

*******************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class nova2001
{
	
	UINT8 *nova2001_videoram2, *nova2001_colorram2;
	
	static struct tilemap *bg_tilemap, *fg_tilemap;
	
	public static PaletteInitHandlerPtr palette_init_nova2001  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i,j;
	
	
		for (i = 0;i < Machine.drv.total_colors;i++)
		{
			int intensity,r,g,b;
	
	
			intensity = (color_prom.read()>> 0) & 0x03;
			/* red component */
			r = (((color_prom.read()>> 0) & 0x0c) | intensity) * 0x11;
			/* green component */
			g = (((color_prom.read()>> 2) & 0x0c) | intensity) * 0x11;
			/* blue component */
			b = (((color_prom.read()>> 4) & 0x0c) | intensity) * 0x11;
	
			palette_set_color(i,r,g,b);
			color_prom++;
		}
	
		/* Color #1 is used for palette animation.          */
	
		/* To handle this, color entries 0-15 are based on  */
		/* the primary 16 colors, while color entries 16-31 */
		/* are based on the secondary set.                  */
	
		/* The only difference among 0-15 and 16-31 is that */
		/* color #1 changes each time */
	
		for (i = 0;i < 16;i++)
		{
			for (j = 0;j < 16;j++)
			{
				if (j == 1)
				{
					colortable[16*i+1] = i;
					colortable[16*i+16*16+1] = i+16;
				}
				else
				{
					colortable[16*i+j] = j;
					colortable[16*i+16*16+j] = j+16;
				}
			}
		}
	} };
	
	public static WriteHandlerPtr nova2001_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (videoram.read(offset)!= data)
		{
			videoram.write(offset,data);
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr nova2001_colorram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (colorram.read(offset)!= data)
		{
			colorram.write(offset,data);
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr nova2001_videoram2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (nova2001_videoram2[offset] != data)
		{
			nova2001_videoram2[offset] = data;
			tilemap_mark_tile_dirty(fg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr nova2001_colorram2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (nova2001_colorram2[offset] != data)
		{
			nova2001_colorram2[offset] = data;
			tilemap_mark_tile_dirty(fg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr nova2001_scroll_x_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		tilemap_set_scrollx(bg_tilemap, 0, data - (flip_screen() ? 0 : 7));
	} };
	
	public static WriteHandlerPtr nova2001_scroll_y_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		tilemap_set_scrolly(bg_tilemap, 0, data);
	} };
	
	public static WriteHandlerPtr nova2001_flipscreen_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (flip_screen() != (~data & 0x01))
		{
			flip_screen_set(~data & 0x01);
			tilemap_mark_all_tiles_dirty(ALL_TILEMAPS);
		}
	} };
	
	public static GetTileInfoHandlerPtr get_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code = videoram.read(tile_index);
		int color = colorram.read(tile_index)& 0x0f;
	
		SET_TILE_INFO(1, code, color, 0)
	} };
	
	public static GetTileInfoHandlerPtr get_fg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code = nova2001_videoram2[tile_index];
		int color = nova2001_colorram2[tile_index] & 0x0f;
	
		SET_TILE_INFO(0, code, color, 0)
	} };
	
	public static VideoStartHandlerPtr video_start_nova2001  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_bg_tile_info, tilemap_scan_rows, 
			TILEMAP_OPAQUE, 8, 8, 32, 32);
	
		if ( !bg_tilemap )
			return 1;
	
		fg_tilemap = tilemap_create(get_fg_tile_info, tilemap_scan_rows, 
			TILEMAP_TRANSPARENT, 8, 8, 32, 32);
	
		if ( !fg_tilemap )
			return 1;
	
		tilemap_set_transparent_pen(fg_tilemap, 0);
	
		return 0;
	} };
	
	static void nova2001_draw_sprites( struct mame_bitmap *bitmap )
	{
		int offs;
	
		for (offs = 0;offs < spriteram_size;offs += 32)
		{
			int flipx = spriteram.read(offs+3)& 0x10;
			int flipy = spriteram.read(offs+3)& 0x20;
			int sx = spriteram.read(offs+1);
			int sy = spriteram.read(offs+2);
	
			if (flip_screen())
			{
				sx = 240 - sx;
				sy = 240 - sy;
				flipx = NOT(flipx);
				flipy = NOT(flipy);
			}
	
			drawgfx(bitmap,Machine->gfx[2 + ((spriteram.read(offs+0)& 0x80) >> 7)],
				spriteram.read(offs+0)& 0x7f,
				spriteram.read(offs+3)& 0x0f,
				flipx,flipy,
				sx,sy,
				Machine->visible_area,TRANSPARENCY_PEN,0);
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_nova2001  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap, Machine.visible_area, bg_tilemap, 0, 0);
		nova2001_draw_sprites(bitmap);
		tilemap_draw(bitmap, Machine.visible_area, fg_tilemap, 0, 0);
	} };
}
