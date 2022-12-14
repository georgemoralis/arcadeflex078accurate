/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class fastfred
{
	
	
	data8_t *fastfred_videoram;
	data8_t *fastfred_spriteram;
	size_t fastfred_spriteram_size;
	data8_t *fastfred_attributesram;
	data8_t *imago_fg_videoram;
	
	
	static struct rectangle spritevisiblearea =
	{
	      2*8, 32*8-1,
	      2*8, 30*8-1
	};
	
	static struct rectangle spritevisibleareaflipx =
	{
	        0*8, 30*8-1,
	        2*8, 30*8-1
	};
	
	static data16_t charbank;
	static data8_t colorbank;
	static int flip_screen_x;
	static int flip_screen_y;
	int fastfred_hardware_type;
	static const UINT8 *fastfred_color_prom;
	static struct tilemap *bg_tilemap, *fg_tilemap;
	
	/***************************************************************************
	
	  Convert the color PROMs into a more useable format.
	
	  bit 0 -- 1  kohm resistor  -- RED/GREEN/BLUE
	        -- 470 ohm resistor  -- RED/GREEN/BLUE
	        -- 220 ohm resistor  -- RED/GREEN/BLUE
	  bit 3 -- 100 ohm resistor  -- RED/GREEN/BLUE
	
	***************************************************************************/
	
	static void set_color(pen_t pen, int i)
	{
		UINT8 r,g,b;
		int bit0, bit1, bit2, bit3;
	
		pen_t total = Machine->drv->total_colors;
	
		bit0 = (fastfred_color_prom[i + 0*total] >> 0) & 0x01;
		bit1 = (fastfred_color_prom[i + 0*total] >> 1) & 0x01;
		bit2 = (fastfred_color_prom[i + 0*total] >> 2) & 0x01;
		bit3 = (fastfred_color_prom[i + 0*total] >> 3) & 0x01;
		r = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
		bit0 = (fastfred_color_prom[i + 1*total] >> 0) & 0x01;
		bit1 = (fastfred_color_prom[i + 1*total] >> 1) & 0x01;
		bit2 = (fastfred_color_prom[i + 1*total] >> 2) & 0x01;
		bit3 = (fastfred_color_prom[i + 1*total] >> 3) & 0x01;
		g = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
		bit0 = (fastfred_color_prom[i + 2*total] >> 0) & 0x01;
		bit1 = (fastfred_color_prom[i + 2*total] >> 1) & 0x01;
		bit2 = (fastfred_color_prom[i + 2*total] >> 2) & 0x01;
		bit3 = (fastfred_color_prom[i + 2*total] >> 3) & 0x01;
		b = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
	
		palette_set_color(pen,r,g,b);
	}
	
	public static PaletteInitHandlerPtr palette_init_fastfred  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		pen_t i;
		#define TOTAL_COLORS(gfxn) (Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity)
		#define COLOR(gfxn,offs) (colortable[Machine.drv.gfxdecodeinfo[gfxn].color_codes_start + offs])
	
	
		fastfred_color_prom = color_prom;	/* we'll need this later */
	
		for (i = 0;i < Machine.drv.total_colors;i++)
		{
			set_color(i, i);
		}
	
	
		/* characters and sprites use the same palette */
		for (i = 0; i < TOTAL_COLORS(0); i++)
		{
			pen_t color;
	
			if ((i & 0x07) == 0)
				color = 0;
			else
				color = i;
	
			COLOR(0,i) = COLOR(1,i) = color;
		}
	} };
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	public static GetTileInfoHandlerPtr get_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		data8_t x = tile_index & 0x1f;
	
		data16_t code = charbank | fastfred_videoram[tile_index];
		data8_t color = colorbank | (fastfred_attributesram[2 * x + 1] & 0x07);
	
		SET_TILE_INFO(0, code, color, 0)
	} };
	
	
	
	/*************************************
	 *
	 *	Video system start
	 *
	 *************************************/
	
	public static VideoStartHandlerPtr video_start_fastfred  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_tile_info,tilemap_scan_rows,TILEMAP_OPAQUE,8,8,32,32);
	
		if (!bg_tilemap)
			return 1;
	
		tilemap_set_scroll_cols(bg_tilemap, 32);
	
		return 0;
	} };
	
	
	/*************************************
	 *
	 *	Memory handlers
	 *
	 *************************************/
	
	public static WriteHandlerPtr fastfred_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (fastfred_videoram[offset] != data)
		{
			fastfred_videoram[offset] = data;
	
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	} };
	
	
	public static WriteHandlerPtr fastfred_attributes_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (fastfred_attributesram[offset] != data)
		{
			if (offset & 0x01)
			{
				/* color change */
				int i;
	
				for (i = offset / 2; i < 0x0400; i += 32)
					tilemap_mark_tile_dirty(bg_tilemap, i);
			}
			else
			{
				/* coloumn scroll */
				tilemap_set_scrolly(bg_tilemap, offset / 2, data);
			}
	
			fastfred_attributesram[offset] = data;
		}
	} };
	
	
	public static WriteHandlerPtr fastfred_charbank1_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		data16_t new_data = (charbank & 0x0200) | ((data & 0x01) << 8);
	
		if (new_data != charbank)
		{
			tilemap_mark_all_tiles_dirty(bg_tilemap);
		
			charbank = new_data;
		}
	} };
	
	public static WriteHandlerPtr fastfred_charbank2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		data16_t new_data = (charbank & 0x0100) | ((data & 0x01) << 9);
	
		if (new_data != charbank)
		{
			tilemap_mark_all_tiles_dirty(bg_tilemap);
		
			charbank = new_data;
		}
	} };
	
	
	public static WriteHandlerPtr fastfred_colorbank1_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		data8_t new_data = (colorbank & 0x10) | ((data & 0x01) << 3);
	
		if (new_data != colorbank)
		{
			tilemap_mark_all_tiles_dirty(bg_tilemap);
	
			colorbank = new_data;
		}
	} };
	
	public static WriteHandlerPtr fastfred_colorbank2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		data8_t new_data = (colorbank & 0x08) | ((data & 0x01) << 4);
	
		if (new_data != colorbank)
		{
			tilemap_mark_all_tiles_dirty(bg_tilemap);
	
			colorbank = new_data;
		}
	} };
	
	
	public static WriteHandlerPtr fastfred_background_color_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		set_color(0, data);
	} };
	
	
	public static WriteHandlerPtr fastfred_flip_screen_x_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (flip_screen_x != (data & 0x01))
		{
			flip_screen_x = data & 0x01;
	
			tilemap_set_flip(bg_tilemap, (flip_screen_x ? TILEMAP_FLIPX : 0) | (flip_screen_y ? TILEMAP_FLIPY : 0));
		}
	} };
	
	public static WriteHandlerPtr fastfred_flip_screen_y_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (flip_screen_y != (data & 0x01))
		{
			flip_screen_y = data & 0x01;
	
			tilemap_set_flip(bg_tilemap, (flip_screen_x ? TILEMAP_FLIPX : 0) | (flip_screen_y ? TILEMAP_FLIPY : 0));
		}
	} };
	
	
	
	/*************************************
	 *
	 *	Video update
	 *
	 *************************************/
	
	static void draw_sprites(struct mame_bitmap *bitmap, const struct rectangle *cliprect)
	{
		int offs;
	
		for (offs = fastfred_spriteram_size - 4; offs >= 0; offs -= 4)
		{
			UINT8 code,sx,sy;
			int flipx,flipy;
	
			sx = fastfred_spriteram[offs + 3];
			sy = 240 - fastfred_spriteram[offs];
	
			if (fastfred_hardware_type == 3)
			{
				// Imago
	
				//fastfred_spriteram[offs + 2] & 0xf8 get only set at startup
	
				//the code is greater than 0x3f only at startup
				code  = (fastfred_spriteram[offs + 1]) & 0x3f;
	
				/* To Do: find the correct bank for sprites */
				
				flipx = 0;
				flipy = 0;
			}
			else if (fastfred_hardware_type == 2)
			{
				// Boggy 84
				code  =  fastfred_spriteram[offs + 1] & 0x7f;
				flipx =  0;
				flipy =  fastfred_spriteram[offs + 1] & 0x80;
			}
			else if (fastfred_hardware_type == 1)
			{
				// Fly-Boy/Fast Freddie/Red Robin
				code  =  fastfred_spriteram[offs + 1] & 0x7f;
				flipx =  0;
				flipy = ~fastfred_spriteram[offs + 1] & 0x80;
			}
			else
			{
				// Jump Coaster
				code  = (fastfred_spriteram[offs + 1] & 0x3f) | 0x40;
				flipx = ~fastfred_spriteram[offs + 1] & 0x40;
				flipy =  fastfred_spriteram[offs + 1] & 0x80;
			}
	
	
			if (flip_screen_x)
			{
				sx = 240 - sx;
				flipx = NOT(flipx);
			}
			if (flip_screen_y)
			{
				sy = 240 - sy;
				flipy = NOT(flipy);
			}
	
			drawgfx(bitmap,Machine->gfx[1],
					code,
					colorbank | (fastfred_spriteram[offs + 2] & 0x07),
					flipx,flipy,
					sx,sy,
					flip_screen_x ? &spritevisibleareaflipx : spritevisiblearea,TRANSPARENCY_PEN,0);
		}
	}
	
	
	public static VideoUpdateHandlerPtr video_update_fastfred  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap,cliprect,bg_tilemap,0,0);
	
		draw_sprites(bitmap, cliprect);
	} };
	
	
	public static GetTileInfoHandlerPtr imago_get_tile_info_bg = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		data8_t x = tile_index & 0x1f;
	
		data16_t code = charbank * 0x100 + fastfred_videoram[tile_index];
		data8_t color = colorbank | (fastfred_attributesram[2 * x + 1] & 0x07);
	
		SET_TILE_INFO(0, code, color, 0)
	} };
	
	public static GetTileInfoHandlerPtr imago_get_tile_info_fg = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code = imago_fg_videoram[tile_index];
		SET_TILE_INFO(2, code, 0, 0)
	} };
	
	
	public static WriteHandlerPtr imago_fg_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( imago_fg_videoram[offset] != data)
		{
			imago_fg_videoram[offset] = data;
			tilemap_mark_tile_dirty(fg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr imago_charbank_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( charbank != data )
		{
			charbank = data;
			tilemap_mark_all_tiles_dirty(bg_tilemap);
		}
	} };
	
	public static VideoStartHandlerPtr video_start_imago  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(imago_get_tile_info_bg,tilemap_scan_rows,TILEMAP_OPAQUE,8,8,32,32);
		fg_tilemap = tilemap_create(imago_get_tile_info_fg,tilemap_scan_rows,TILEMAP_TRANSPARENT,8,8,32,32);
	
		if( !bg_tilemap || !fg_tilemap )
			return 1;
	
		tilemap_set_transparent_pen(fg_tilemap, 0);
	
		return 0;
	} };
	
	public static VideoUpdateHandlerPtr video_update_imago  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap,cliprect,bg_tilemap,0,0);
	
		draw_sprites(bitmap, cliprect);
		
		tilemap_draw(bitmap,cliprect,fg_tilemap,0,0);
	} };
}
