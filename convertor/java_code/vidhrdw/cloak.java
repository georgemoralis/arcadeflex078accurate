/***************************************************************************

	Atari Cloak & Dagger hardware

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class cloak
{
	
	static struct mame_bitmap *tmpbitmap2;
	static UINT8 x,y,bmap;
	static UINT8 *tmpvideoram,*tmpvideoram2;
	
	static struct tilemap *bg_tilemap;
	
	/***************************************************************************
	
	  CLOAK & DAGGER uses RAM to dynamically
	  create the palette. The resolution is 9 bit (3 bits per gun). The palette
	  contains 64 entries, but it is accessed through a memory windows 128 bytes
	  long: writing to the first 64 bytes sets the msb of the red component to 0,
	  while writing to the last 64 bytes sets it to 1.
	
	  Colors 0-15  Character mapped graphics
	  Colors 16-31 Bitmapped graphics (maybe 8 colors per bitmap?)
	  Colors 32-47 Sprites
	  Colors 48-63 not used
	
	  These are the exact resistor values from the schematics:
	
	  bit 8 -- diode |< -- pullup 1 kohm -- 2.2 kohm resistor -- pulldown 100 pf -- RED
			-- diode |< -- pullup 1 kohm -- 4.7 kohm resistor -- pulldown 100 pf -- RED
			-- diode |< -- pullup 1 kohm -- 10  kohm resistor -- pulldown 100 pf -- RED
			-- diode |< -- pullup 1 kohm -- 2.2 kohm resistor -- pulldown 100 pf -- GREEN
			-- diode |< -- pullup 1 kohm -- 4.7 kohm resistor -- pulldown 100 pf -- GREEN
			-- diode |< -- pullup 1 kohm -- 10  kohm resistor -- pulldown 100 pf -- GREEN
			-- diode |< -- pullup 1 kohm -- 2.2 kohm resistor -- pulldown 100 pf -- BLUE
			-- diode |< -- pullup 1 kohm -- 4.7 kohm resistor -- pulldown 100 pf -- BLUE
	  bit 0 -- diode |< -- pullup 1 kohm -- 10  kohm resistor -- pulldown 100 pf -- BLUE
	
	***************************************************************************/
	public static WriteHandlerPtr cloak_paletteram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int r,g,b;
		int bit0,bit1,bit2;
	
		/* a write to offset 64-127 means to set the msb of the red component */
		int color = data | ((offset & 0x40) << 2);
	
		r = (~color & 0x1c0) >> 6;
		g = (~color & 0x038) >> 3;
		b = (~color & 0x007);
	
		// the following is WRONG! fix it
	
		bit0 = (r >> 0) & 0x01;
		bit1 = (r >> 1) & 0x01;
		bit2 = (r >> 2) & 0x01;
		r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
		bit0 = (g >> 0) & 0x01;
		bit1 = (g >> 1) & 0x01;
		bit2 = (g >> 2) & 0x01;
		g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
		bit0 = (b >> 0) & 0x01;
		bit1 = (b >> 1) & 0x01;
		bit2 = (b >> 2) & 0x01;
		b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
		palette_set_color(offset & 0x3f,r,g,b);
	} };
	
	public static WriteHandlerPtr cloak_clearbmp_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		bmap = data & 0x01;
	
		if (data & 0x02)	/* clear */
		{
			if (bmap)
			{
				fillbitmap(tmpbitmap, Machine->pens[16], Machine->visible_area);
				memset(tmpvideoram, 0, 256*256);
			}
			else
			{
				fillbitmap(tmpbitmap2, Machine->pens[16], Machine->visible_area);
				memset(tmpvideoram2, 0, 256*256);
			}
		}
	} };
	
	static void adjust_xy(int offset)
	{
		switch(offset)
		{
			case 0x00:  x--; y++; break;
			case 0x01:       y--; break;
			case 0x02:  x--;      break;
			case 0x04:  x++; y++; break;
			case 0x05:  	 y++; break;
			case 0x06:  x++;      break;
		}
	}
	
	public static ReadHandlerPtr graph_processor_r  = new ReadHandlerPtr() { public int handler(int offset){
		int ret;
	
		if (bmap)
		{
			ret = tmpvideoram2[y * 256 + x];
		}
	 	else
		{
			ret = tmpvideoram[y * 256 + x];
		}
	
		adjust_xy(offset);
	
		return ret;
	} };
	
	public static WriteHandlerPtr graph_processor_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int color;
	
		switch (offset)
		{
			case 0x03: x = data; break;
			case 0x07: y = data; break;
			default:
				color = data & 0x07;
	
				if (bmap)
				{
					plot_pixel(tmpbitmap, (x-6)&0xff, y, Machine->pens[16 + color]);
					tmpvideoram[y*256+x] = color;
				}
				else
				{
					plot_pixel(tmpbitmap2, (x-6)&0xff, y, Machine->pens[16 + color]);
					tmpvideoram2[y*256+x] = color;
				}
	
				adjust_xy(offset);
				break;
			}
	} };
	
	public static WriteHandlerPtr cloak_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (videoram.read(offset)!= data)
		{
			videoram.write(offset,data);
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr cloak_flipscreen_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		flip_screen_set(data & 0x80);
	} };
	
	public static GetTileInfoHandlerPtr get_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code = videoram.read(tile_index);
	
		SET_TILE_INFO(0, code, 0, 0)
	} };
	
	public static VideoStartHandlerPtr video_start_cloak  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_bg_tile_info, tilemap_scan_rows,
			TILEMAP_OPAQUE, 8, 8, 32, 32);
	
		if ( !bg_tilemap )
			return 1;
	
		if ((tmpbitmap = auto_bitmap_alloc(Machine.drv.screen_width,Machine.drv.screen_height)) == 0)
			return 1;
	
		if ((tmpbitmap2 = auto_bitmap_alloc(Machine.drv.screen_width,Machine.drv.screen_height)) == 0)
			return 1;
	
		if ((tmpvideoram = auto_malloc(256*256)) == 0)
			return 1;
	
		if ((tmpvideoram2 = auto_malloc(256*256)) == 0)
			return 1;
	
		return 0;
	} };
	
	#if 0
	static void refresh_bitmaps(void)
	{
		int lx,ly;
	
		for (ly = 0; ly < 256; ly++)
		{
			for (lx = 0; lx < 256; lx++)
			{
				plot_pixel(tmpbitmap,  (lx-6)&0xff, ly, Machine->pens[16 + tmpvideoram[ly*256+lx]]);
				plot_pixel(tmpbitmap2, (lx-6)&0xff, ly, Machine->pens[16 + tmpvideoram2[ly*256+lx]]);
			}
		}
	}
	#endif
	
	static void cloak_draw_sprites( struct mame_bitmap *bitmap, const struct rectangle *cliprect )
	{
		int offs;
	
		for (offs = (spriteram_size / 4) - 1; offs >= 0; offs--)
		{
			int code = spriteram.read(offs + 64)& 0x7f;
			int flipx = spriteram.read(offs + 64)& 0x80;
			int flipy = 0;
			int sx = spriteram.read(offs + 192);
			int sy = 240 - spriteram.read(offs);
	
			if (flip_screen())
			{
				sx -= 9;
				sy = 240 - sy;
				flipx = NOT(flipx);
				flipy = NOT(flipy);
			}
	
			drawgfx(bitmap, Machine->gfx[1], code, 0, flipx, flipy,
				sx, sy,	cliprect, TRANSPARENCY_PEN, 0);
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_cloak  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap, cliprect, bg_tilemap, 0, 0);
		copybitmap(bitmap, (bmap ? tmpbitmap2 : tmpbitmap),flip_screen(),flip_screen(),0,0,cliprect,TRANSPARENCY_COLOR,16);
		cloak_draw_sprites(bitmap, cliprect);
	} };
}
