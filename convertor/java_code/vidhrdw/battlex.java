/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class battlex
{
	
	static int battlex_scroll_lsb;
	static int battlex_scroll_msb;
	
	static struct tilemap *bg_tilemap;
	
	public static PaletteInitHandlerPtr palette_init_battlex  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i,col;
	
		for (col = 0;col < 8;col++)
		{
			for (i = 0;i < 16;i++)
			{
				int data = i | col;
				int g = ((data & 1) >> 0) * 0xff;
				int b = ((data & 2) >> 1) * 0xff;
				int r = ((data & 4) >> 2) * 0xff;
	
	#if 0
				/* from Tim's shots, bit 3 seems to have no effect (see e.g. Laser Ship on title screen) */
				if (i & 8)
				{
					r /= 2;
					g /= 2;
					b /= 2;
				}
	#endif
	
				palette_set_color(i + 16 * col,r,g,b);
			}
		}
	} };
	
	public static WriteHandlerPtr battlex_palette_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int g = ((data & 1) >> 0) * 0xff;
		int b = ((data & 2) >> 1) * 0xff;
		int r = ((data & 4) >> 2) * 0xff;
	
		palette_set_color(16*8 + offset,r,g,b);
	} };
	
	public static WriteHandlerPtr battlex_scroll_x_lsb_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		battlex_scroll_lsb = data;
	} };
	
	public static WriteHandlerPtr battlex_scroll_x_msb_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		battlex_scroll_msb = data;
	} };
	
	public static WriteHandlerPtr battlex_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (videoram.read(offset)!= data)
		{
			videoram.write(offset,data);
			tilemap_mark_tile_dirty(bg_tilemap, offset / 2);
		}
	} };
	
	public static WriteHandlerPtr battlex_flipscreen_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		/* bit 4 is used, but for what? */
	
		/* bit 7 is flip screen */
	
		if (flip_screen() != (data & 0x80))
		{
			flip_screen_set(data & 0x80);
			tilemap_mark_all_tiles_dirty(ALL_TILEMAPS);
		}
	} };
	
	public static GetTileInfoHandlerPtr get_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int tile = videoram.read(tile_index*2)| (((videoram.read(tile_index*2+1)& 0x01)) << 8);
		int color = (videoram.read(tile_index*2+1)& 0x0e) >> 1;
	
		SET_TILE_INFO(0,tile,color,0)
	} };
	
	public static VideoStartHandlerPtr video_start_battlex  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_bg_tile_info, tilemap_scan_rows,
			TILEMAP_OPAQUE, 8, 8, 64, 32);
	
		if ( !bg_tilemap )
			return 1;
	
		return 0;
	} };
	
	static void battlex_drawsprites( struct mame_bitmap *bitmap, const struct rectangle *cliprect )
	{
		const struct GfxElement *gfx = Machine->gfx[1];
		UINT8 *source = spriteram;
		UINT8 *finish = spriteram + 0x200;
	
		while( source<finish )
		{
			int sx = (source[0] & 0x7f) * 2 - (source[0] & 0x80) * 2;
			int sy = source[3];
			int tile = source[2] & 0x7f;
			int color = source[1] & 0x07;	/* bits 3,4,5 also used during explosions */
			int flipy = source[1] & 0x80;
			int flipx = source[1] & 0x40;
	
			if (flip_screen())
			{
				sx = 240 - sx;
				sy = 240 - sy;
				flipx = NOT(flipx);
				flipy = NOT(flipy);
			}
	
			drawgfx(bitmap,gfx,tile,color,flipx,flipy,sx,sy,cliprect,TRANSPARENCY_PEN,0);
	
			source += 4;
		}
	
	}
	
	public static VideoUpdateHandlerPtr video_update_battlex  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_set_scrollx(bg_tilemap, 0, battlex_scroll_lsb | (battlex_scroll_msb << 8));
		tilemap_draw(bitmap, Machine.visible_area, bg_tilemap, 0, 0);
		battlex_drawsprites(bitmap, Machine.visible_area);
	} };
}
