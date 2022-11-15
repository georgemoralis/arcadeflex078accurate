/***************************************************************************

	Atari Basketball hardware

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class bsktball
{
	
	unsigned char *bsktball_motion;
	
	static struct tilemap *bg_tilemap;
	
	public static WriteHandlerPtr bsktball_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (videoram.read(offset)!= data)
		{
			videoram.write(offset,data);
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	} };
	
	public static GetTileInfoHandlerPtr get_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int attr = videoram.read(tile_index);
		int code = ((attr & 0x0f) << 2) | ((attr & 0x30) >> 4);
		int color = (attr & 0x40) >> 6;
		int flags = (attr & 0x80) ? TILE_FLIPX : 0;
	
		SET_TILE_INFO(0, code, color, flags)
	} };
	
	public static VideoStartHandlerPtr video_start_bsktball  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_bg_tile_info, tilemap_scan_rows, 
			TILEMAP_OPAQUE, 8, 8, 32, 32);
	
		if ( !bg_tilemap )
			return 1;
	
		return 0;
	} };
	
	static void bsktball_draw_sprites( struct mame_bitmap *bitmap )
	{
		int motion;
	
		for (motion=0;motion<16;motion++)
		{
			int pic = bsktball_motion[motion*4];
			int sy = 28*8 - bsktball_motion[motion*4 + 1];
			int sx = bsktball_motion[motion*4 + 2];
			int color = bsktball_motion[motion*4 + 3];
			int flipx = (pic & 0x80) >> 7;
	
			pic = (pic & 0x3F);
	        color = (color & 0x3F);
	
	        drawgfx(bitmap,Machine->gfx[1],
	            pic, color,
				flipx,0,sx,sy,
				Machine->visible_area,TRANSPARENCY_PEN,0);
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_bsktball  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap, Machine->visible_area, bg_tilemap, 0, 0);
		bsktball_draw_sprites(bitmap);
	} };
}
