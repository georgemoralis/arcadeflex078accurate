/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class news
{
	
	
	data8_t *news_fgram;
	data8_t *news_bgram;
	
	static int bgpic;
	static struct tilemap *fg_tilemap, *bg_tilemap;
	
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	public static GetTileInfoHandlerPtr get_fg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code = (news_fgram[tile_index*2] << 8) | news_fgram[tile_index*2+1];
		SET_TILE_INFO(
				0,
				code & 0x0fff,
				(code & 0xf000) >> 12,
				0)
	} };
	
	public static GetTileInfoHandlerPtr get_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code = (news_bgram[tile_index*2] << 8) | news_bgram[tile_index*2+1];
		int color = (code & 0xf000) >> 12;
	
		code &= 0x0fff;
		if ((code & 0x0e00) == 0x0e00) code = (code & 0x1ff) | (bgpic << 9);
	
		SET_TILE_INFO(
				0,
				code,
				color,
				0)
	} };
	
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VideoStartHandlerPtr video_start_news  = new VideoStartHandlerPtr() { public int handler(){
	
		fg_tilemap = tilemap_create(get_fg_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT,8,8,32, 32);
		tilemap_set_transparent_pen(fg_tilemap,0);
	
		bg_tilemap = tilemap_create(get_bg_tile_info,tilemap_scan_rows,TILEMAP_OPAQUE,8,8,32, 32);
	
		return 0;
	} };
	
	
	
	/***************************************************************************
	
	  Memory handlers
	
	***************************************************************************/
	
	public static WriteHandlerPtr news_fgram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		news_fgram[offset] = data;
		tilemap_mark_tile_dirty(fg_tilemap,offset/2);
	} };
	
	public static WriteHandlerPtr news_bgram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		news_bgram[offset] = data;
		tilemap_mark_tile_dirty(bg_tilemap,offset/2);
	} };
	
	public static WriteHandlerPtr news_bgpic_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (bgpic != data)
		{
			bgpic = data;
			tilemap_mark_all_tiles_dirty(bg_tilemap);
		}
	} };
	
	
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	
	public static VideoUpdateHandlerPtr video_update_news  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap,cliprect,bg_tilemap,0,0);
		tilemap_draw(bitmap,cliprect,fg_tilemap,0,0);
	} };
}
