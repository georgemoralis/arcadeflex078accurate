/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class blockade
{
	
	static struct tilemap *bg_tilemap;
	
	WRITE_HANDLER( blockade_videoram_w )
	{
		if (videoram[offset] != data)
		{
			videoram[offset] = data;
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	
		if (input_port_3_r(0) & 0x80)
		{
			logerror("blockade_videoram_w: scanline %d\n", cpu_getscanline());
			cpu_spinuntil_int();
		}
	}
	
	static void get_bg_tile_info(int tile_index)
	{
		int code = videoram[tile_index];
	
		SET_TILE_INFO(0, code, 0, 0)
	}
	
	public static VideoStartHandlerPtr video_start_blockade  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_bg_tile_info, tilemap_scan_rows,
			TILEMAP_OPAQUE, 8, 8, 32, 32);
	
		if ( !bg_tilemap )
			return 1;
	
		return 0;
	} };
	
	public static VideoUpdateHandlerPtr video_update_blockade  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap, &Machine->visible_area, bg_tilemap, 0, 0);
	} };
}
