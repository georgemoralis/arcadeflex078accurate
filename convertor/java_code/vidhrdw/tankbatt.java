/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class tankbatt
{
	
	UINT8 *tankbatt_bulletsram;
	size_t tankbatt_bulletsram_size;
	
	static struct tilemap *bg_tilemap;
	
	/***************************************************************************
	
	  Convert the color PROMs into a more useable format.
	
	***************************************************************************/
	public static PaletteInitHandlerPtr palette_init_tankbatt  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
		#define TOTAL_COLORS(gfxn) (Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity)
		#define COLOR(gfxn,offs) (colortable[Machine.drv.gfxdecodeinfo[gfxn].color_codes_start + offs])
	
		#define RES_1	0xc0 /* this is a guess */
		#define RES_2	0x3f /* this is a guess */
	
		/* Stick black in there */
		palette_set_color(0,0,0,0);
	
		/* ? Skip the first byte ? */
		color_prom++;
	
		for (i = 1;i < Machine.drv.total_colors;i++)
		{
			int bit0, bit1, bit2, bit3, r, g, b;
	
			bit0 = (color_prom.read()>> 0) & 0x01; /* intensity */
			bit1 = (color_prom.read()>> 1) & 0x01; /* red */
			bit2 = (color_prom.read()>> 2) & 0x01; /* green */
			bit3 = (color_prom.read()>> 3) & 0x01; /* blue */
	
			/* red component */
			r = RES_1 * bit1;
			if (bit1) r += RES_2 * bit0;
	
			/* green component */
			g = RES_1 * bit2;
			if (bit2) g += RES_2 * bit0;
	
			/* blue component */
			b = RES_1 * bit3;
			if (bit3) b += RES_2 * bit0;
	
			palette_set_color(i,r,g,b);
			color_prom += 4;
		}
	
		for (i = 0;i < 128;i++)
		{
			colortable[i++] = 0;
			colortable[i] = (i/2) + 1;
		}
	} };
	
	public static WriteHandlerPtr tankbatt_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (videoram.read(offset)!= data)
		{
			videoram.write(offset,data);
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	} };
	
	public static GetTileInfoHandlerPtr get_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code = videoram.read(tile_index);
		int color = videoram.read(tile_index)>> 2;
	
		SET_TILE_INFO(0, code, color, 0)
	} };
	
	public static VideoStartHandlerPtr video_start_tankbatt  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_bg_tile_info, tilemap_scan_rows,
			TILEMAP_OPAQUE, 8, 8, 32, 32);
	
		if ( !bg_tilemap )
			return 1;
	
		return 0;
	} };
	
	static void tankbatt_draw_bullets( struct mame_bitmap *bitmap )
	{
		int offs;
	
		for (offs = 0;offs < tankbatt_bulletsram_size;offs += 2)
		{
			int color = 63;	/* cyan, same color as the tanks */
			int x = tankbatt_bulletsram[offs + 1];
			int y = 255 - tankbatt_bulletsram[offs] - 2;
	
			drawgfx(bitmap,Machine->gfx[1],
				0,	/* this is just a square, generated by the hardware */
				color,
				0,0,
				x,y,
				Machine->visible_area,TRANSPARENCY_NONE,0);
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_tankbatt  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap, Machine.visible_area, bg_tilemap, 0, 0);
		tankbatt_draw_bullets(bitmap);
	} };
}
