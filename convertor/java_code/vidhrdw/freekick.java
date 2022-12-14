/* Free Kick Video Hardware */

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class freekick
{
	
	struct tilemap *freek_tilemap;
	data8_t *freek_videoram;
	
	
	public static GetTileInfoHandlerPtr get_freek_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int tileno,palno;
	
		tileno = freek_videoram[tile_index]+((freek_videoram[tile_index+0x400]&0xe0)<<3);
		palno=freek_videoram[tile_index+0x400]&0x1f;
		SET_TILE_INFO(0,tileno,palno,0)
	} };
	
	
	
	public static VideoStartHandlerPtr video_start_freekick  = new VideoStartHandlerPtr() { public int handler(){
		freek_tilemap = tilemap_create(get_freek_tile_info,tilemap_scan_rows,TILEMAP_OPAQUE, 8, 8,32,32);
		return 0;
	} };
	
	
	
	public static WriteHandlerPtr freek_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		freek_videoram[offset] = data;
		tilemap_mark_tile_dirty(freek_tilemap,offset&0x3ff);
	} };
	
	static void gigas_draw_sprites( struct mame_bitmap *bitmap, const struct rectangle *cliprect )
	{
		int offs;
	
		for (offs = 0;offs < spriteram_size;offs += 4)
		{
			int xpos = spriteram.read(offs + 3);
			int ypos = spriteram.read(offs + 2);
			int code = spriteram.read(offs + 0)|( (spriteram.read(offs + 1)&0x20) <<3 );
	
			int flipx = 0;
			int flipy = 0;
			int color = spriteram.read(offs + 1)& 0x1f;
	
			if (flip_screen_x)
			{
				xpos = 240 - xpos;
				flipx = NOT(flipx);
			}
			if (flip_screen_y)
			{
				ypos = 256 - ypos;
				flipy = NOT(flipy);
			}
	
			drawgfx(bitmap,Machine->gfx[1],
					code,
					color,
					flipx,flipy,
					xpos,240-ypos,
					cliprect,TRANSPARENCY_PEN,0);
		}
	}
	
	
	static void pbillrd_draw_sprites( struct mame_bitmap *bitmap, const struct rectangle *cliprect )
	{
		int offs;
	
		for (offs = 0;offs < spriteram_size;offs += 4)
		{
			int xpos = spriteram.read(offs + 3);
			int ypos = spriteram.read(offs + 2);
			int code = spriteram.read(offs + 0);
	
			int flipx = 0;//spriteram.read(offs + 0)& 0x80;	//?? unused ?
			int flipy = 0;//spriteram.read(offs + 0)& 0x40;
			int color = spriteram.read(offs + 1)& 0x0f;
	
			if (flip_screen_x)
			{
				xpos = 240 - xpos;
				flipx = NOT(flipx);
			}
			if (flip_screen_y)
			{
				ypos = 256 - ypos;
				flipy = NOT(flipy);
			}
	
			drawgfx(bitmap,Machine->gfx[1],
					code,
					color,
					flipx,flipy,
					xpos,240-ypos,
					cliprect,TRANSPARENCY_PEN,0);
		}
	}
	
	
	
	static void freekick_draw_sprites( struct mame_bitmap *bitmap, const struct rectangle *cliprect )
	{
		int offs;
	
		for (offs = 0;offs < spriteram_size;offs += 4)
		{
			int xpos = spriteram.read(offs + 3);
			int ypos = spriteram.read(offs + 0);
			int code = spriteram.read(offs + 1)+ ((spriteram.read(offs + 2)& 0x20) << 3);
	
			int flipx = spriteram.read(offs + 2)& 0x80;	//?? unused ?
			int flipy = spriteram.read(offs + 2)& 0x40;
			int color = spriteram.read(offs + 2)& 0x1f;
	
			if (flip_screen_x)
			{
				xpos = 240 - xpos;
				flipx = NOT(flipx);
			}
			if (flip_screen_y)
			{
				ypos = 256 - ypos;
				flipy = NOT(flipy);
			}
	
			drawgfx(bitmap,Machine->gfx[1],
					code,
					color,
					flipx,flipy,
					xpos,248-ypos,
					cliprect,TRANSPARENCY_PEN,0);
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_gigas  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap,cliprect,freek_tilemap,0,0);
		gigas_draw_sprites(bitmap,cliprect);
	} };
	
	public static VideoUpdateHandlerPtr video_update_pbillrd  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap,cliprect,freek_tilemap,0,0);
		pbillrd_draw_sprites(bitmap,cliprect);
	} };
	
	public static VideoUpdateHandlerPtr video_update_freekick  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap,cliprect,freek_tilemap,0,0);
		freekick_draw_sprites(bitmap,cliprect);
	} };
}
