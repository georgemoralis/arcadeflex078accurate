/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class iqblock
{
	
	
	data8_t *iqblock_bgvideoram;
	data8_t *iqblock_fgscroll;
	data8_t *iqblock_fgvideoram;
	int iqblock_videoenable;
	int iqblock_vidhrdw_type;
	
	static struct tilemap *bg_tilemap,*fg_tilemap;
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	public static GetTileInfoHandlerPtr get_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code = iqblock_bgvideoram[tile_index] + (iqblock_bgvideoram[tile_index + 0x800] << 8);
		SET_TILE_INFO(
				0,
				code &(iqblock_vidhrdw_type ? 0x1fff : 0x3fff),
				iqblock_vidhrdw_type? (2*(code >> 13)+1) : (4*(code >> 14)+3),
				0)
	} };
	
	public static GetTileInfoHandlerPtr get_fg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code = iqblock_fgvideoram[tile_index];
		SET_TILE_INFO(
				1,
				code & 0x7f,
				(code & 0x80) ? 3 : 0,
				0)
	} };
	
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VideoStartHandlerPtr video_start_iqblock  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_bg_tile_info,tilemap_scan_rows,TILEMAP_OPAQUE,     8, 8,64,32);
		fg_tilemap = tilemap_create(get_fg_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT,8,32,64, 8);
	
		if (!bg_tilemap || !fg_tilemap)
			return 1;
	
		tilemap_set_transparent_pen(fg_tilemap,0);
		tilemap_set_scroll_cols(fg_tilemap,64);
	
		return 0;
	} };
	
	
	
	/***************************************************************************
	
	  Memory handlers
	
	***************************************************************************/
	
	public static WriteHandlerPtr iqblock_fgvideoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		iqblock_fgvideoram[offset] = data;
		tilemap_mark_tile_dirty(fg_tilemap,offset);
	} };
	
	public static WriteHandlerPtr iqblock_bgvideoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		iqblock_bgvideoram[offset] = data;
		tilemap_mark_tile_dirty(bg_tilemap,offset & 0x7ff);
	} };
	
	public static ReadHandlerPtr iqblock_bgvideoram_r  = new ReadHandlerPtr() { public int handler(int offset){
		return iqblock_bgvideoram[offset];
	} };
	
	public static WriteHandlerPtr iqblock_fgscroll_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		tilemap_set_scrolly(fg_tilemap,offset,data);
	} };
	
	
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	
	public static VideoUpdateHandlerPtr video_update_iqblock  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		if (!iqblock_videoenable) return;
	
		tilemap_draw(bitmap,cliprect,bg_tilemap,0,0);
		tilemap_draw(bitmap,cliprect,fg_tilemap,0,0);
	} };
	
}
