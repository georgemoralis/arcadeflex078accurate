/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class bogeyman
{
	
	UINT8 *bogeyman_videoram2, *bogeyman_colorram2;
	
	static struct tilemap *bg_tilemap, *fg_tilemap;
	
	public static PaletteInitHandlerPtr palette_init_bogeyman  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
	
		/* first 16 colors are RAM */
	
		for (i = 0;i < 256;i++)
		{
			int bit0,bit1,bit2,r,g,b;
	
			/* red component */
			bit0 = (color_prom.read(0)>> 0) & 0x01;
			bit1 = (color_prom.read(0)>> 1) & 0x01;
			bit2 = (color_prom.read(0)>> 2) & 0x01;
			r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			/* green component */
			bit0 = (color_prom.read(0)>> 3) & 0x01;
			bit1 = (color_prom.read(256)>> 0) & 0x01;
			bit2 = (color_prom.read(256)>> 1) & 0x01;
			g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			/* blue component */
			bit0 = 0;
			bit1 = (color_prom.read(256)>> 2) & 0x01;
			bit2 = (color_prom.read(256)>> 3) & 0x01;
			b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			palette_set_color(i+16,r,g,b);
			color_prom++;
		}
	} };
	
	public static WriteHandlerPtr bogeyman_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (videoram.read(offset)!= data)
		{
			videoram.write(offset,data);
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr bogeyman_colorram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (colorram.read(offset)!= data)
		{
			colorram.write(offset,data);
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr bogeyman_videoram2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (bogeyman_videoram2[offset] != data)
		{
			bogeyman_videoram2[offset] = data;
			tilemap_mark_tile_dirty(fg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr bogeyman_colorram2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (bogeyman_colorram2[offset] != data)
		{
			bogeyman_colorram2[offset] = data;
			tilemap_mark_tile_dirty(fg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr bogeyman_paletteram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		/* RGB output is inverted */
		paletteram_BBGGGRRR_w(offset, ~data);
	} };
	
	public static GetTileInfoHandlerPtr get_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int attr = colorram.read(tile_index);
		int gfxbank = ((((attr & 0x01) << 8) + videoram.read(tile_index)) / 0x80) + 3;
		int code = videoram.read(tile_index)& 0x7f;
		int color = (attr >> 1) & 0x07;
	
		SET_TILE_INFO(gfxbank, code, color, 0)
	} };
	
	public static GetTileInfoHandlerPtr get_fg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int attr = bogeyman_colorram2[tile_index];
		int tile = bogeyman_videoram2[tile_index] | ((attr & 0x03) << 8);
		int gfxbank = tile / 0x200;
		int code = tile & 0x1ff;
	
		SET_TILE_INFO(gfxbank, code, 0, 0)
	} };
	
	public static VideoStartHandlerPtr video_start_bogeyman  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_bg_tile_info, tilemap_scan_rows,
			TILEMAP_OPAQUE, 16, 16, 16, 16);
	
		if ( !bg_tilemap )
			return 1;
	
		fg_tilemap = tilemap_create(get_fg_tile_info, tilemap_scan_rows,
			TILEMAP_TRANSPARENT, 8, 8, 32, 32);
	
		if ( !fg_tilemap )
			return 1;
	
		tilemap_set_transparent_pen(fg_tilemap, 0);
	
		return 0;
	} };
	
	static void bogeyman_draw_sprites( struct mame_bitmap *bitmap )
	{
		int offs;
	
		for (offs = 0; offs < spriteram_size; offs += 4)
		{
			int attr = spriteram.read(offs);
	
			if (attr & 0x01)
			{
				int code = spriteram.read(offs + 1)+ ((attr & 0x40) << 2);
				int color = (attr & 0x08) >> 3;
				int flipx = !(attr & 0x04);
				int flipy = attr & 0x02;
				int sx = spriteram.read(offs + 3);
				int sy = (240 - spriteram.read(offs + 2)) & 0xff;
				int multi = attr & 0x10;
	
				if (multi) sy -= 16;
	
				if (flip_screen())
				{
					sx = 240 - sx;
					sy = 240 - sy;
					flipx = NOT(flipx);
					flipy = NOT(flipy);
				}
	
				drawgfx(bitmap, Machine->gfx[2],
					code, color,
					flipx, flipy,
					sx, sy,
					Machine->visible_area,
					TRANSPARENCY_PEN, 0);
	
				if (multi)
				{
					drawgfx(bitmap,Machine->gfx[2],
						code + 1, color,
						flipx, flipy,
						sx, sy + (flip_screen() ? -16 : 16),
						Machine->visible_area,
						TRANSPARENCY_PEN, 0);
				}
			}
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_bogeyman  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap, Machine.visible_area, bg_tilemap, 0, 0);
		bogeyman_draw_sprites(bitmap);
		tilemap_draw(bitmap, Machine.visible_area, fg_tilemap, 0, 0);
	} };
}
