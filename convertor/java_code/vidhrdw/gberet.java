/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class gberet
{
	
	
	
	unsigned char *gberet_videoram,*gberet_colorram;
	unsigned char *gberet_spritebank;
	unsigned char *gberet_scrollram;
	static struct tilemap *bg_tilemap;
	static int interruptenable;
	static int flipscreen;
	
	
	/***************************************************************************
	
	  Convert the color PROMs into a more useable format.
	
	  Green Beret has a 32 bytes palette PROM and two 256 bytes color lookup table
	  PROMs (one for sprites, one for characters).
	  The palette PROM is connected to the RGB output, this way:
	
	  bit 7 -- 220 ohm resistor  -- BLUE
	        -- 470 ohm resistor  -- BLUE
	        -- 220 ohm resistor  -- GREEN
	        -- 470 ohm resistor  -- GREEN
	        -- 1  kohm resistor  -- GREEN
	        -- 220 ohm resistor  -- RED
	        -- 470 ohm resistor  -- RED
	  bit 0 -- 1  kohm resistor  -- RED
	
	***************************************************************************/
	
	public static PaletteInitHandlerPtr palette_init_gberet  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
		#define TOTAL_COLORS(gfxn) (Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity)
		#define COLOR(gfxn,offs) (colortable[Machine.drv.gfxdecodeinfo[gfxn].color_codes_start + offs])
	
	
		for (i = 0;i < Machine.drv.total_colors;i++)
		{
			int bit0,bit1,bit2,r,g,b;
	
	
			bit0 = (color_prom.read()>> 0) & 0x01;
			bit1 = (color_prom.read()>> 1) & 0x01;
			bit2 = (color_prom.read()>> 2) & 0x01;
			r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
			bit0 = (color_prom.read()>> 3) & 0x01;
			bit1 = (color_prom.read()>> 4) & 0x01;
			bit2 = (color_prom.read()>> 5) & 0x01;
			g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
			bit0 = 0;
			bit1 = (color_prom.read()>> 6) & 0x01;
			bit2 = (color_prom.read()>> 7) & 0x01;
			b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			palette_set_color(i,r,g,b);
			color_prom++;
		}
	
		for (i = 0;i < TOTAL_COLORS(1);i++)
		{
			if (color_prom.read()& 0x0f) COLOR(1,i) = color_prom.read()& 0x0f;
			else COLOR(1,i) = 0;
			color_prom++;
		}
		for (i = 0;i < TOTAL_COLORS(0);i++)
		{
			COLOR(0,i) = (*(color_prom++) & 0x0f) + 0x10;
		}
	} };
	
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	public static GetTileInfoHandlerPtr get_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		unsigned char attr = gberet_colorram[tile_index];
		SET_TILE_INFO(
				0,
				gberet_videoram[tile_index] + ((attr & 0x40) << 2),
				attr & 0x0f,
				TILE_FLIPYX((attr & 0x30) >> 4))
		tile_info.priority = (attr & 0x80) >> 7;
	} };
	
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VideoStartHandlerPtr video_start_gberet  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT_COLOR,8,8,64,32);
	
		if (!bg_tilemap)
			return 0;
	
		tilemap_set_transparent_pen(bg_tilemap,0x10);
		tilemap_set_scroll_rows(bg_tilemap,32);
	
		return 0;
	} };
	
	
	
	/***************************************************************************
	
	  Memory handlers
	
	***************************************************************************/
	
	public static WriteHandlerPtr gberet_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (gberet_videoram[offset] != data)
		{
			gberet_videoram[offset] = data;
			tilemap_mark_tile_dirty(bg_tilemap,offset);
		}
	} };
	
	public static WriteHandlerPtr gberet_colorram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (gberet_colorram[offset] != data)
		{
			gberet_colorram[offset] = data;
			tilemap_mark_tile_dirty(bg_tilemap,offset);
		}
	} };
	
	public static WriteHandlerPtr gberet_e044_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		/* bit 0 enables interrupts */
		interruptenable = data & 1;
	
		/* bit 3 flips screen */
		flipscreen = data & 0x08;
		tilemap_set_flip(ALL_TILEMAPS,flipscreen ? (TILEMAP_FLIPY | TILEMAP_FLIPX) : 0);
	
		/* don't know about the other bits */
	} };
	
	public static WriteHandlerPtr gberet_scroll_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int scroll;
	
		gberet_scrollram[offset] = data;
	
		scroll = gberet_scrollram[offset & 0x1f] | (gberet_scrollram[offset | 0x20] << 8);
		tilemap_set_scrollx(bg_tilemap,offset & 0x1f,scroll);
	} };
	
	public static WriteHandlerPtr gberetb_scroll_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int scroll;
	
		scroll = data;
		if (offset) scroll |= 0x100;
	
		for (offset = 6;offset < 29;offset++)
			tilemap_set_scrollx(bg_tilemap,offset,scroll + 64-8);
	} };
	
	
	public static InterruptHandlerPtr gberet_interrupt = new InterruptHandlerPtr() {public void handler(){
		if (cpu_getiloops() == 0)
			cpu_set_irq_line(0, 0, HOLD_LINE);
		else if (cpu_getiloops() % 2)
		{
			if (interruptenable)
				cpu_set_irq_line(0, IRQ_LINE_NMI, PULSE_LINE);
		}
	} };
	
	
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	
	static void draw_sprites(struct mame_bitmap *bitmap, const struct rectangle *cliprect)
	{
		int offs;
		unsigned char *sr;
	
		if (*gberet_spritebank & 0x08)
			sr = spriteram_2;
		else sr = spriteram;
	
		for (offs = 0;offs < spriteram_size;offs += 4)
		{
			if (sr[offs+3])
			{
				int sx,sy,flipx,flipy;
	
	
				sx = sr[offs+2] - 2*(sr[offs+1] & 0x80);
				sy = sr[offs+3];
				flipx = sr[offs+1] & 0x10;
				flipy = sr[offs+1] & 0x20;
	
				if (flipscreen)
				{
					sx = 240 - sx;
					sy = 240 - sy;
					flipx = NOT(flipx);
					flipy = NOT(flipy);
				}
	
				drawgfx(bitmap,Machine->gfx[1],
						sr[offs+0] + ((sr[offs+1] & 0x40) << 2),
						sr[offs+1] & 0x0f,
						flipx,flipy,
						sx,sy,
						cliprect,TRANSPARENCY_COLOR,0);
			}
		}
	}
	
	static void draw_sprites_bootleg(struct mame_bitmap *bitmap, const struct rectangle *cliprect)
	{
		int offs;
		unsigned char *sr;
	
		sr = spriteram;
	
		for (offs = spriteram_size - 4;offs >= 0;offs -= 4)
		{
			if (sr[offs+1])
			{
				int sx,sy,flipx,flipy;
	
	
				sx = sr[offs+2] - 2*(sr[offs+3] & 0x80);
				sy = sr[offs+1];
				sy = 240 - sy;
				flipx = sr[offs+3] & 0x10;
				flipy = sr[offs+3] & 0x20;
	
				if (flipscreen)
				{
					sx = 240 - sx;
					sy = 240 - sy;
					flipx = NOT(flipx);
					flipy = NOT(flipy);
				}
	
				drawgfx(bitmap,Machine->gfx[1],
						sr[offs+0] + ((sr[offs+3] & 0x40) << 2),
						sr[offs+3] & 0x0f,
						flipx,flipy,
						sx,sy,
						cliprect,TRANSPARENCY_COLOR,0);
			}
		}
	}
	
	
	public static VideoUpdateHandlerPtr video_update_gberet  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap,cliprect,bg_tilemap,TILEMAP_IGNORE_TRANSPARENCY|0,0);
		tilemap_draw(bitmap,cliprect,bg_tilemap,TILEMAP_IGNORE_TRANSPARENCY|1,0);
		draw_sprites(bitmap,cliprect);
		tilemap_draw(bitmap,cliprect,bg_tilemap,0,0);
	} };
	
	public static VideoUpdateHandlerPtr video_update_gberetb  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap,cliprect,bg_tilemap,TILEMAP_IGNORE_TRANSPARENCY|0,0);
		tilemap_draw(bitmap,cliprect,bg_tilemap,TILEMAP_IGNORE_TRANSPARENCY|1,0);
		draw_sprites_bootleg(bitmap,cliprect);
		tilemap_draw(bitmap,cliprect,bg_tilemap,0,0);
	} };
}
