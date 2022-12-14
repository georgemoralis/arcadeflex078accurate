/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class sauro
{
	
	UINT8 *tecfri_videoram;
	UINT8 *tecfri_colorram;
	UINT8 *tecfri_videoram2;
	UINT8 *tecfri_colorram2;
	
	static struct tilemap *bg_tilemap, *fg_tilemap;
	
	/* General */
	
	public static WriteHandlerPtr tecfri_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (tecfri_videoram[offset] != data)
		{
			tecfri_videoram[offset] = data;
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr tecfri_colorram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (tecfri_colorram[offset] != data)
		{
			tecfri_colorram[offset] = data;
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr tecfri_videoram2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (tecfri_videoram2[offset] != data)
		{
			tecfri_videoram2[offset] = data;
			tilemap_mark_tile_dirty(fg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr tecfri_colorram2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (tecfri_colorram2[offset] != data)
		{
			tecfri_colorram2[offset] = data;
			tilemap_mark_tile_dirty(fg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr tecfri_scroll_bg_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		tilemap_set_scrollx(bg_tilemap, 0, data);
	} };
	
	public static WriteHandlerPtr flip_screen_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		flip_screen_set(data);
	} };
	
	public static GetTileInfoHandlerPtr get_tile_info_bg = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code = tecfri_videoram[tile_index] + ((tecfri_colorram[tile_index] & 0x07) << 8);
		int color = (tecfri_colorram[tile_index] >> 4) & 0x0f;
		int flags = tecfri_colorram[tile_index] & 0x08 ? TILE_FLIPX : 0;
	
		SET_TILE_INFO(0, code, color, flags)
	} };
	
	public static GetTileInfoHandlerPtr get_tile_info_fg = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code = tecfri_videoram2[tile_index] + ((tecfri_colorram2[tile_index] & 0x07) << 8);
		int color = (tecfri_colorram2[tile_index] >> 4) & 0x0f;
		int flags = tecfri_colorram2[tile_index] & 0x08 ? TILE_FLIPX : 0;
	
		SET_TILE_INFO(1, code, color, flags)
	} };
	
	/* Sauro */
	
	static int scroll2_map     [8] = {2, 1, 4, 3, 6, 5, 0, 7};
	static int scroll2_map_flip[8] = {0, 7, 2, 1, 4, 3, 6, 5};
	
	public static WriteHandlerPtr sauro_scroll_fg_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int *map = (flip_screen() ? scroll2_map_flip : scroll2_map);
		int scroll = (data & 0xf8) | map[data & 7];
	
		tilemap_set_scrollx(fg_tilemap, 0, scroll);
	} };
	
	public static VideoStartHandlerPtr video_start_sauro  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_tile_info_bg, tilemap_scan_cols,
			TILEMAP_OPAQUE, 8, 8, 32, 32);
		
		if (!bg_tilemap)
			return 1;
	
		fg_tilemap = tilemap_create(get_tile_info_fg, tilemap_scan_cols,
			TILEMAP_TRANSPARENT, 8, 8, 32, 32);
		
		if (!bg_tilemap)
			return 1;
	
		tilemap_set_transparent_pen(fg_tilemap, 0);
	
		return 0;
	} };
	
	static void sauro_draw_sprites( struct mame_bitmap *bitmap )
	{
		int offs,code,sx,sy,color,flipx;
	
		for (offs = 3;offs < spriteram_size - 1;offs += 4)
		{
			sy = spriteram.read(offs);
			if (sy == 0xf8) continue;
	
			code = spriteram.read(offs+1)+ ((spriteram.read(offs+3)& 0x03) << 8);
			sx = spriteram.read(offs+2);
			sy = 236 - sy;
			color = (spriteram.read(offs+3)>> 4) & 0x0f;
	
			// I'm not really sure how this bit works
			if (spriteram.read(offs+3)& 0x08)
			{
				if (sx > 0xc0)
				{
					// Sign extend
					sx = (signed int)(signed char)sx;
				}
			}
			else
			{
				if (sx < 0x40) continue;
			}
	
			flipx = spriteram.read(offs+3)& 0x04;
	
			if (flip_screen())
			{
				flipx = NOT(flipx);
				sx = (235 - sx) & 0xff;  // The &0xff is not 100% percent correct
				sy = 240 - sy;
			}
	
			drawgfx(bitmap, Machine->gfx[2],
					code,
					color,
					flipx,flip_screen(),
					sx,sy,
					Machine->visible_area,TRANSPARENCY_PEN,0);
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_sauro  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap, Machine.visible_area, bg_tilemap, 0, 0);
		tilemap_draw(bitmap, Machine.visible_area, fg_tilemap, 0, 0);
		sauro_draw_sprites(bitmap);
	} };
	
	/* Tricky Doc */
	
	public static WriteHandlerPtr trckydoc_spriteram_mirror_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		spriteram.write(offset,data);
	} };
	
	public static VideoStartHandlerPtr video_start_trckydoc  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_tile_info_bg, tilemap_scan_cols,
			TILEMAP_OPAQUE, 8, 8, 32, 32);
		
		if (!bg_tilemap)
			return 1;
	
		return 0;
	} };
	
	static void trckydoc_draw_sprites( struct mame_bitmap *bitmap )
	{
		int offs,code,sy,color,flipx,sx;
	
		/* Weird, sprites entries don't start on DWORD boundary */
		for (offs = 3;offs < spriteram_size - 1;offs += 4)
		{
			sy = spriteram.read(offs);
	
			if(spriteram.read(offs+3)& 0x08)
			{
				/* needed by the elevator cable (2nd stage), balls bouncing (3rd stage) and maybe other things */
				sy += 6;
			}
	
			code = spriteram.read(offs+1)+ ((spriteram.read(offs+3)& 0x01) << 8);
	
			sx = spriteram.read(offs+2)-2;
			color = (spriteram.read(offs+3)>> 4) & 0x0f;
	
			sy = 236 - sy;
	
			/* similar to sauro but different bit is used .. */
			if (spriteram.read(offs+3)& 0x02)
			{
				if (sx > 0xc0)
				{
					/* Sign extend */
					sx = (signed int)(signed char)sx;
				}
			}
			else
			{
				if (sx < 0x40) continue;
			}
	
			flipx = spriteram.read(offs+3)& 0x04;
	
			if (flip_screen())
			{
				flipx = NOT(flipx);
				sx = (235 - sx) & 0xff;  /* The &0xff is not 100% percent correct */
				sy = 240 - sy;
			}
	
			drawgfx(bitmap, Machine->gfx[1],
					code,
					color,
					flipx,flip_screen(),
					sx,sy,
					Machine->visible_area,TRANSPARENCY_PEN,0);
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_trckydoc  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap, Machine.visible_area, bg_tilemap, 0, 0);
		trckydoc_draw_sprites(bitmap);
	} };
}
