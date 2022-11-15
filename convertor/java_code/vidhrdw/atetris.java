/***************************************************************************

	Atari Tetris hardware

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class atetris
{
	
	
	static struct tilemap *tilemap;
	
	
	/*************************************
	 *
	 *	Tilemap callback
	 *
	 *************************************/
	
	static void get_tile_info(int tile_index)
	{
		int code = videoram[tile_index * 2] | ((videoram[tile_index * 2 + 1] & 7) << 8);
		int color = (videoram[tile_index * 2 + 1] & 0xf0) >> 4;
	
		SET_TILE_INFO(0, code, color, 0);
	}
	
	
	
	/*************************************
	 *
	 *	Video RAM write
	 *
	 *************************************/
	
	WRITE_HANDLER( atetris_videoram_w )
	{
		videoram[offset] = data;
		tilemap_mark_tile_dirty(tilemap, offset / 2);
	}
	
	
	
	/*************************************
	 *
	 *	Video system start
	 *
	 *************************************/
	
	public static VideoStartHandlerPtr video_start_atetris  = new VideoStartHandlerPtr() { public int handler(){
		tilemap = tilemap_create(get_tile_info, tilemap_scan_rows, TILEMAP_OPAQUE, 8,8, 64,32);
		if (!tilemap)
			return 1;
		return 0;
	} };
	
	
	
	/*************************************
	 *
	 *	Main refresh
	 *
	 *************************************/
	
	public static VideoUpdateHandlerPtr video_update_atetris  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap, cliprect, tilemap, 0,0);
	} };
}
