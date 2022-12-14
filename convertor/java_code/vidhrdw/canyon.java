/***************************************************************************

Atari Canyon Bomber video emulation

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class canyon
{
	
	static struct tilemap *tilemap;
	
	UINT8* canyon_videoram;
	
	
	public static WriteHandlerPtr canyon_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (canyon_videoram[offset] != data)
		{
			tilemap_mark_tile_dirty(tilemap, offset);
		}
	
		canyon_videoram[offset] = data;
	} };
	
	
	public static GetTileInfoHandlerPtr get_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		UINT8 code = canyon_videoram[tile_index];
	
		SET_TILE_INFO(0, code & 0x3f, code >> 7, 0)
	} };
	
	
	public static VideoStartHandlerPtr video_start_canyon  = new VideoStartHandlerPtr() { public int handler(){
		tilemap = tilemap_create(get_bg_tile_info, tilemap_scan_rows,
			TILEMAP_OPAQUE, 8, 8, 32, 32);
	
		return (tilemap == NULL) ? 1 : 0;
	} };
	
	
	static void canyon_draw_sprites(struct mame_bitmap *bitmap, const struct rectangle* cliprect)
	{
		int i;
	
		for (i = 0; i < 2; i++)
		{
			int x = canyon_videoram[0x3d0 + 2 * i + 0x1];
			int y = canyon_videoram[0x3d0 + 2 * i + 0x8];
			int c = canyon_videoram[0x3d0 + 2 * i + 0x9];
	
			drawgfx(bitmap, Machine->gfx[1],
				c >> 3,
				i,
				!(c & 0x80), 0,
				224 - x,
				240 - y,
				cliprect,
				TRANSPARENCY_PEN, 0);
		}
	}
	
	
	static void canyon_draw_bombs(struct mame_bitmap *bitmap, const struct rectangle* cliprect)
	{
		int i;
	
		for (i = 0; i < 2; i++)
		{
			int sx = 254 - canyon_videoram[0x3d0 + 2 * i + 0x5];
			int sy = 246 - canyon_videoram[0x3d0 + 2 * i + 0xc];
	
			struct rectangle rect;
	
			rect.min_x = sx;
			rect.min_y = sy;
			rect.max_x = sx + 1;
			rect.max_y = sy + 1;
	
			if (rect.min_x < cliprect->min_x) rect.min_x = cliprect->min_x;
			if (rect.min_y < cliprect->min_y) rect.min_y = cliprect->min_y;
			if (rect.max_x > cliprect->max_x) rect.max_x = cliprect->max_x;
			if (rect.max_y > cliprect->max_y) rect.max_y = cliprect->max_y;
	
			fillbitmap(bitmap, i, &rect);
		}
	}
	
	
	public static VideoUpdateHandlerPtr video_update_canyon  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap, cliprect, tilemap, 0, 0);
	
		canyon_draw_sprites(bitmap, cliprect);
	
		canyon_draw_bombs(bitmap, cliprect);
	} };
}
