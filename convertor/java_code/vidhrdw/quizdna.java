/******************************************************************************

Quiz DNA no Hanran (c) 1992 Face
Quiz Gakuen Paradise (c) 1991 NMK
Quiz Gekiretsu Scramble (Gakuen Paradise 2) (c) 1993 Face

Video hardware
	driver by Uki

******************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class quizdna
{
	
	static data8_t *quizdna_bg_ram;
	static data8_t *quizdna_fg_ram;
	
	static struct tilemap *quizdna_bg_tilemap;
	static struct tilemap *quizdna_fg_tilemap;
	
	static UINT8 quizdna_bg_xscroll[2];
	
	static int quizdna_flipscreen = -1;
	static int quizdna_video_enable;
	
	
	public static GetTileInfoHandlerPtr get_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code = quizdna_bg_ram[tile_index*2] + quizdna_bg_ram[tile_index*2+1]*0x100 ;
		int col = quizdna_bg_ram[tile_index*2+0x1000] & 0x7f;
	
		if (code>0x7fff)
			code &= 0x83ff;
	
		SET_TILE_INFO(1, code, col, 0)
	} };
	
	public static GetTileInfoHandlerPtr get_fg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code,col,x,y;
		data8_t *FG = memory_region(REGION_USER1);
	
		x = tile_index & 0x1f;
		y = FG[(tile_index >> 5) & 0x1f] & 0x3f;
		code = y & 1;
	
		y >>= 1;
	
		col = quizdna_fg_ram[x*2 + y*0x40 + 1];
		code += (quizdna_fg_ram[x*2 + y*0x40] + (col & 0x1f) * 0x100) * 2;
		col >>= 5;
		col = (col & 3) | ((col & 4) << 1);
	
		SET_TILE_INFO(0, code, col, 0)
	} };
	
	
	public static VideoStartHandlerPtr video_start_quizdna  = new VideoStartHandlerPtr() { public int handler(){
		quizdna_bg_ram = auto_malloc(0x2000);
		quizdna_fg_ram = auto_malloc(0x1000);
	
		quizdna_bg_tilemap = tilemap_create( get_bg_tile_info,tilemap_scan_rows,TILEMAP_OPAQUE,8,8,64,32 );
		quizdna_fg_tilemap = tilemap_create( get_fg_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT,16,8,32,32 );
	
		if (!quizdna_bg_ram || !quizdna_fg_ram || !quizdna_bg_tilemap || !quizdna_fg_tilemap)
			return 1;
	
		tilemap_set_transparent_pen( quizdna_fg_tilemap,0 );
	
		return 0;
	} };
	
	public static WriteHandlerPtr quizdna_bg_ram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		data8_t *RAM = memory_region(REGION_CPU1);
		quizdna_bg_ram[offset] = data;
		RAM[0x12000+offset] = data;
	
		tilemap_mark_tile_dirty(quizdna_bg_tilemap, (offset & 0xfff) / 2 );
	} };
	
	public static WriteHandlerPtr quizdna_fg_ram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int i;
		int offs = offset & 0xfff;
		data8_t *RAM = memory_region(REGION_CPU1);
	
		RAM[0x10000+offs] = data;
		RAM[0x11000+offs] = data; /* mirror */
		quizdna_fg_ram[offs] = data;
	
		for (i=0; i<32; i++)
			tilemap_mark_tile_dirty(quizdna_fg_tilemap, ((offs/2) & 0x1f) + i*0x20 );
	} };
	
	public static WriteHandlerPtr quizdna_bg_yscroll_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		tilemap_set_scrolldy( quizdna_bg_tilemap, 255-data, 255-data+1 );
	} };
	
	public static WriteHandlerPtr quizdna_bg_xscroll_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int x;
		quizdna_bg_xscroll[offset] = data;
		x = ~(quizdna_bg_xscroll[0] + quizdna_bg_xscroll[1]*0x100) & 0x1ff;
	
		tilemap_set_scrolldx( quizdna_bg_tilemap, x+64, x-64+10 );
	} };
	
	public static WriteHandlerPtr quizdna_screen_ctrl_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int tmp = (data & 0x10) >> 4;
		quizdna_video_enable = data & 0x20;
	
		coin_counter_w(0, data & 1);
	
		if (quizdna_flipscreen == tmp)
			return;
	
		quizdna_flipscreen = tmp;
	
		flip_screen_set(tmp);
		tilemap_set_scrolldx( quizdna_fg_tilemap, 64, -64 +16);
	} };
	
	public static WriteHandlerPtr paletteram_xBGR_RRRR_GGGG_BBBB_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int r,g,b,d0,d1;
		int offs = offset & ~1;
	
		paletteram.write(offset,data);
	
		d0 = paletteram.read(offs);
		d1 = paletteram.read(offs+1);
	
		r = ((d1 << 1) & 0x1e) | ((d1 >> 4) & 1);
		g = ((d0 >> 3) & 0x1e) | ((d1 >> 5) & 1);
		b = ((d0 << 1) & 0x1e) | ((d1 >> 6) & 1);
	
		r = (r << 3) | (r >> 2);
		g = (g << 3) | (g >> 2);
		b = (b << 3) | (b >> 2);
	
		palette_set_color(offs/2,r,g,b);
	} };
	
	static void quizdna_drawsprites(struct mame_bitmap *bitmap, const struct rectangle *cliprect)
	{
		int offs;
	
		for (offs = 0; offs<spriteram_size; offs+=8)
		{
			int i;
	
			int x = spriteram.read(offs + 3)*0x100 + spriteram.read(offs + 2)+ 64 - 8;
			int y = (spriteram.read(offs + 1)& 1)*0x100 + spriteram.read(offs + 0);
			int code = (spriteram.read(offs + 5)* 0x100 + spriteram.read(offs + 4)) & 0x3fff;
			int col =  spriteram.read(offs + 6);
			int fx = col & 0x80;
			int fy = col & 0x40;
			int ysize = (spriteram.read(offs + 1)& 0xc0) >> 6;
			int dy = 0x10;
			col &= 0x1f;
	
			if (quizdna_flipscreen)
			{
				x -= 7;
				y += 1;
			}
	
			x &= 0x1ff;
			if (x>0x1f0)
				x -= 0x200;
	
			if (fy)
			{
				dy = -0x10;
				y += 0x10 * ysize;
			}
	
			if (code >= 0x2100)
				code &= 0x20ff;
	
			for (i=0; i<ysize+1; i++)
			{
				y &= 0x1ff;
	
				drawgfx(bitmap,Machine->gfx[2],
						code ^ i,
						col,
						fx,fy,
						x,y,
						cliprect,TRANSPARENCY_PEN,0);
	
				y += dy;
			}
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_quizdna  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		if (quizdna_video_enable)
		{
			tilemap_draw(bitmap, cliprect, quizdna_bg_tilemap, 0, 0);
			quizdna_drawsprites(bitmap, cliprect);
			tilemap_draw(bitmap, cliprect, quizdna_fg_tilemap, 0, 0);
		}
		else
			fillbitmap(bitmap, get_black_pen(), Machine.visible_area);
	} };
}
