/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class holeland
{
	
	static int palette_offset;
	static struct tilemap *bg_tilemap;
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	public static GetTileInfoHandlerPtr holeland_get_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int attr = colorram.read(tile_index);
		int tile_number = videoram.read(tile_index)| ((attr & 0x03) << 8);
	
	/*if (keyboard_pressed(KEYCODE_Q) && (attr & 0x10)) tile_number = rand(); */
	/*if (keyboard_pressed(KEYCODE_W) && (attr & 0x20)) tile_number = rand(); */
	/*if (keyboard_pressed(KEYCODE_E) && (attr & 0x40)) tile_number = rand(); */
	/*if (keyboard_pressed(KEYCODE_R) && (attr & 0x80)) tile_number = rand(); */
		SET_TILE_INFO(
				0,
				tile_number,
				palette_offset + ((attr >> 4) & 0x0f),
				TILE_FLIPYX((attr >> 2) & 0x03) | TILE_SPLIT((attr >> 4) & 1))
	} };
	
	public static GetTileInfoHandlerPtr crzrally_get_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int attr = colorram.read(tile_index);
		int tile_number = videoram.read(tile_index)| ((attr & 0x03) << 8);
	
		SET_TILE_INFO(
				0,
				tile_number,
				palette_offset + ((attr >> 4) & 0x0f),
				TILE_FLIPYX((attr >> 2) & 0x03) | TILE_SPLIT((attr >> 4) & 1))
	} };
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VideoStartHandlerPtr video_start_holeland  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(holeland_get_tile_info,tilemap_scan_rows,TILEMAP_SPLIT,16,16,32,32);
	
		if (!bg_tilemap)
			return 1;
	
		tilemap_set_transmask(bg_tilemap,0,0xff,0x00); /* split type 0 is totally transparent in front half */
		tilemap_set_transmask(bg_tilemap,1,0x01,0xfe); /* split type 1 has pen 0? transparent in front half */
		return 0;
	} };
	
	public static VideoStartHandlerPtr video_start_crzrally  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(crzrally_get_tile_info,tilemap_scan_cols,TILEMAP_SPLIT,8,8,32,32);
	
		if (!bg_tilemap)
			return 1;
	
		return 0;
	} };
	
	public static WriteHandlerPtr holeland_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( videoram.read(offset)!=data )
		{
			videoram.write(offset,data);
			tilemap_mark_tile_dirty( bg_tilemap, offset );
		}
	} };
	
	public static WriteHandlerPtr holeland_colorram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( colorram.read(offset)!=data )
		{
			colorram.write(offset,data);
			tilemap_mark_tile_dirty( bg_tilemap, offset );
		}
	} };
	
	public static WriteHandlerPtr holeland_pal_offs_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		static int po[2];
		if ((data & 1) != po[offset])
		{
			po[offset] = data & 1;
			palette_offset = (po[0] + (po[1] << 1)) << 4;
			tilemap_mark_all_tiles_dirty(ALL_TILEMAPS);
		}
	} };
	
	public static WriteHandlerPtr holeland_scroll_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		tilemap_set_scrollx(bg_tilemap, 0, data);
	} };
	
	public static WriteHandlerPtr holeland_flipscreen_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (offset) flip_screen_y_set(data);
		else        flip_screen_x_set(data);
	} };
	
	
	static void holeland_draw_sprites(struct mame_bitmap *bitmap,const struct rectangle *cliprect)
	{
		int offs,code,sx,sy,color,flipx, flipy;
	
		/* Weird, sprites entries don't start on DWORD boundary */
		for (offs = 3;offs < spriteram_size - 1;offs += 4)
		{
			sy = 236 - spriteram.read(offs);
			sx = spriteram.read(offs+2);
	
			/* Bit 7 unknown */
			code = spriteram.read(offs+1)& 0x7f;
			color = palette_offset + (spriteram.read(offs+3)>> 4);
	
			/* Bit 0, 1 unknown */
			flipx = spriteram.read(offs+3)& 0x04;
			flipy = spriteram.read(offs+3)& 0x08;
	
			if (flip_screen_x)
			{
				flipx = NOT(flipx);
				sx = 240 - sx;
			}
	
			if (flip_screen_y)
			{
				flipy = NOT(flipy);
				sy = 240 - sy;
			}
	
			drawgfx(bitmap,Machine->gfx[1],
					code,
					color,
					flipx,flipy,
					2*sx,2*sy,
					cliprect,TRANSPARENCY_PEN,0);
		}
	}
	
	static void crzrally_draw_sprites(struct mame_bitmap *bitmap,const struct rectangle *cliprect)
	{
		int offs,code,sx,sy,color,flipx, flipy;
	
		/* Weird, sprites entries don't start on DWORD boundary */
		for (offs = 3;offs < spriteram_size - 1;offs += 4)
		{
			sy = 236 - spriteram.read(offs);
			sx = spriteram.read(offs+2);
	
			code = spriteram.read(offs+1)+ ((spriteram.read(offs+3)& 0x01) << 8);
			color = (spriteram.read(offs+3)>> 4) + ((spriteram.read(offs+3)& 0x01) << 4);
	
			/* Bit 1 unknown */
			flipx = spriteram.read(offs+3)& 0x04;
			flipy = spriteram.read(offs+3)& 0x08;
	
			if (flip_screen_x)
			{
				flipx = NOT(flipx);
				sx = 240 - sx;
			}
	
			if (flip_screen_y)
			{
				flipy = NOT(flipy);
				sy = 240 - sy;
			}
	
			drawgfx(bitmap,Machine->gfx[1],
					code,
					color,
					flipx,flipy,
					sx,sy,
					cliprect,TRANSPARENCY_PEN,0);
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_holeland  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
	/*tilemap_mark_all_tiles_dirty(bg_tilemap); */
		tilemap_draw(bitmap,cliprect,bg_tilemap,TILEMAP_BACK,0);
		holeland_draw_sprites(bitmap,cliprect);
		tilemap_draw(bitmap,cliprect,bg_tilemap,TILEMAP_FRONT,0);
	} };
	
	public static VideoUpdateHandlerPtr video_update_crzrally  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap,cliprect,bg_tilemap,0,0);
		crzrally_draw_sprites(bitmap,cliprect);
	} };
}
