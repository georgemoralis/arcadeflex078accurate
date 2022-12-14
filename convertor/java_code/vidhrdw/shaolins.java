/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class shaolins
{
	
	
	static int palettebank;
	static struct tilemap *bg_tilemap;
	
	/***************************************************************************
	
	  Convert the color PROMs into a more useable format.
	
	  Shao-lin's Road has three 256x4 palette PROMs (one per gun) and two 256x4
	  lookup table PROMs (one for characters, one for sprites).
	  I don't know for sure how the palette PROMs are connected to the RGB
	  output, but it's probably the usual:
	
	  bit 3 -- 220 ohm resistor  -- RED/GREEN/BLUE
	        -- 470 ohm resistor  -- RED/GREEN/BLUE
	        -- 1  kohm resistor  -- RED/GREEN/BLUE
	  bit 0 -- 2.2kohm resistor  -- RED/GREEN/BLUE
	
	***************************************************************************/
	public static PaletteInitHandlerPtr palette_init_shaolins  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
		#define TOTAL_COLORS(gfxn) (Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity)
		#define COLOR(gfxn,offs) (colortable[Machine.drv.gfxdecodeinfo[gfxn].color_codes_start + offs])
	
	
		for (i = 0;i < Machine.drv.total_colors;i++)
		{
			int bit0,bit1,bit2,bit3,r,g,b;
	
	
			bit0 = (color_prom.read(0)>> 0) & 0x01;
			bit1 = (color_prom.read(0)>> 1) & 0x01;
			bit2 = (color_prom.read(0)>> 2) & 0x01;
			bit3 = (color_prom.read(0)>> 3) & 0x01;
			r = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
			bit0 = (color_prom.read(Machine.drv.total_colors)>> 0) & 0x01;
			bit1 = (color_prom.read(Machine.drv.total_colors)>> 1) & 0x01;
			bit2 = (color_prom.read(Machine.drv.total_colors)>> 2) & 0x01;
			bit3 = (color_prom.read(Machine.drv.total_colors)>> 3) & 0x01;
			g = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
			bit0 = (color_prom.read(2*Machine.drv.total_colors)>> 0) & 0x01;
			bit1 = (color_prom.read(2*Machine.drv.total_colors)>> 1) & 0x01;
			bit2 = (color_prom.read(2*Machine.drv.total_colors)>> 2) & 0x01;
			bit3 = (color_prom.read(2*Machine.drv.total_colors)>> 3) & 0x01;
			b = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
	
			palette_set_color(i,r,g,b);
			color_prom++;
		}
	
		color_prom += 2*Machine.drv.total_colors;
		/* color_prom now points to the beginning of the character lookup table */
	
	
		/* there are eight 32 colors palette banks; sprites use colors 0-15 and */
		/* characters 16-31 of each bank. */
		for (i = 0;i < TOTAL_COLORS(0)/8;i++)
		{
			int j;
	
	
			for (j = 0;j < 8;j++)
				COLOR(0,i + j * TOTAL_COLORS(0)/8) = (color_prom.read()& 0x0f) + 32 * j + 16;
	
			color_prom++;
		}
	
		for (i = 0;i < TOTAL_COLORS(1)/8;i++)
		{
			int j;
	
	
			for (j = 0;j < 8;j++)
			{
				/* preserve transparency */
				if ((color_prom.read()& 0x0f) == 0) COLOR(1,i + j * TOTAL_COLORS(1)/8) = 0;
				else COLOR(1,i + j * TOTAL_COLORS(1)/8) = (color_prom.read()& 0x0f) + 32 * j;
			}
	
			color_prom++;
		}
	} };
	
	public static WriteHandlerPtr shaolins_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (videoram.read(offset)!= data)
		{
			videoram.write(offset,data);
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr shaolins_colorram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (colorram.read(offset)!= data)
		{
			colorram.write(offset,data);
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr shaolins_palettebank_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (palettebank != (data & 0x07))
		{
			palettebank = data & 0x07;
			tilemap_mark_all_tiles_dirty(ALL_TILEMAPS);
		}
	} };
	
	public static WriteHandlerPtr shaolins_scroll_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int col;
	
		for (col = 4; col < 32; col++)
		{
			tilemap_set_scrolly(bg_tilemap, col, data + 1);
		}
	} };
	
	public static WriteHandlerPtr shaolins_nmi_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		shaolins_nmi_enable = data;
	
		if (flip_screen() != (data & 0x01))
		{
			flip_screen_set(data & 0x01);
			tilemap_mark_all_tiles_dirty(ALL_TILEMAPS);
		}
	} };
	
	public static GetTileInfoHandlerPtr get_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int attr = colorram.read(tile_index);
		int code = videoram.read(tile_index)+ ((attr & 0x40) << 2);
		int color = (attr & 0x0f) + 16 * palettebank;
		int flags = (attr & 0x20) ? TILE_FLIPY : 0;
	
		SET_TILE_INFO(0, code, color, flags)
	} };
	
	public static VideoStartHandlerPtr video_start_shaolins  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_bg_tile_info, tilemap_scan_rows, 
			TILEMAP_OPAQUE, 8, 8, 32, 32);
	
		if ( !bg_tilemap )
			return 1;
	
		tilemap_set_scroll_cols(bg_tilemap, 32);
	
		return 0;
	} };
	
	static void shaolins_draw_sprites( struct mame_bitmap *bitmap )
	{
		int offs;
	
		for (offs = spriteram_size-32; offs >= 0; offs-=32 ) /* max 24 sprites */
		{
			if (spriteram.read(offs)&& spriteram.read(offs + 6)) /* stop rogue sprites on high score screen */
			{
				int code = spriteram.read(offs + 8);
				int color = (spriteram.read(offs + 9)& 0x0f) + 16 * palettebank;
				int flipx = !(spriteram.read(offs + 9)& 0x40);
				int flipy = spriteram.read(offs + 9)& 0x80;
				int sx = 240 - spriteram.read(offs + 6);
				int sy = 248 - spriteram.read(offs + 4);
	
				if (flip_screen())
				{
					sx = 240 - sx;
					sy = 248 - sy;
					flipx = NOT(flipx);
					flipy = NOT(flipy);
				}
	
				drawgfx(bitmap, Machine->gfx[1],
					code, color,
					flipx, flipy,
					sx, sy,
					Machine->visible_area,
					TRANSPARENCY_COLOR, 0);
					/* transparency_color, otherwise sprites in test mode are not visible */
			}
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_shaolins  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap, Machine.visible_area, bg_tilemap, 0, 0);
		shaolins_draw_sprites(bitmap);
	} };
}
