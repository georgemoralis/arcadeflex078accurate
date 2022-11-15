/*******************************************************************************

Dr. Micro (c) 1983 Sanritsu

Video hardware
		driver by Uki

*******************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class drmicro
{
	
	static int flipscreen;
	
	static data8_t *drmicro_videoram;
	static struct tilemap *drmicro_bg1;
	static struct tilemap *drmicro_bg2;
	
	/****************************************************************************/
	
	void drmicro_flip_w( int flip )
	{
		flipscreen = flip ? 1 : 0;
		flip_screen_set(flip);
	}
	
	public static WriteHandlerPtr drmicro_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		drmicro_videoram[offset] = data;
	
		if (offset<0x800)
			tilemap_mark_tile_dirty(drmicro_bg2,(offset & 0x3ff));
		else
			tilemap_mark_tile_dirty(drmicro_bg1,(offset & 0x3ff));
	} };
	
	public static ReadHandlerPtr drmicro_videoram_r  = new ReadHandlerPtr() { public int handler(int offset){
		return drmicro_videoram[offset];
	} };
	
	/****************************************************************************/
	
	public static GetTileInfoHandlerPtr get_bg1_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code,col,flags;
	
		code = drmicro_videoram[tile_index + 0x0800];
		col  = drmicro_videoram[tile_index + 0x0c00];
	
		code += (col & 0xc0) << 2;
		flags = ((col & 0x20) ? TILEMAP_FLIPY : 0) | ((col & 0x10) ? TILEMAP_FLIPX : 0);
		col &= 0x0f;
	
		SET_TILE_INFO( 0, code, col, flags)
	} };
	
	public static GetTileInfoHandlerPtr get_bg2_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code,col,flags;
	
		code = drmicro_videoram[tile_index + 0x0000];
		col  = drmicro_videoram[tile_index + 0x0400];
	
		code += (col & 0xc0) << 2;
		flags = ((col & 0x20) ? TILEMAP_FLIPY : 0) | ((col & 0x10) ? TILEMAP_FLIPX : 0);
		col &= 0x0f;
	
		SET_TILE_INFO( 1, code, col, flags)
	} };
	
	/****************************************************************************/
	
	public static PaletteInitHandlerPtr palette_init_drmicro  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
	
		for (i = 0;i < Machine->drv->total_colors;i++)
		{
			int bit0,bit1,bit2,r,g,b;
	
			bit0 = (*color_prom >> 0) & 0x01;
			bit1 = (*color_prom >> 1) & 0x01;
			bit2 = (*color_prom >> 2) & 0x01;
			r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
			bit0 = (*color_prom >> 3) & 0x01;
			bit1 = (*color_prom >> 4) & 0x01;
			bit2 = (*color_prom >> 5) & 0x01;
			g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
			bit0 = 0;
			bit1 = (*color_prom >> 6) & 0x01;
			bit2 = (*color_prom >> 7) & 0x01;
			b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			palette_set_color(i,r,g,b);
			color_prom++;
		}
	
		for (i=0; i<Machine->drv->color_table_len; i++)
			colortable[i] = color_prom[i] & 0x0f;
	} };
	
	public static VideoStartHandlerPtr video_start_drmicro  = new VideoStartHandlerPtr() { public int handler(){
		drmicro_videoram = auto_malloc(0x1000);
	
		drmicro_bg1 = tilemap_create(get_bg1_tile_info, tilemap_scan_rows,TILEMAP_OPAQUE, 8,8,32,32);
		drmicro_bg2 = tilemap_create(get_bg2_tile_info, tilemap_scan_rows,TILEMAP_TRANSPARENT, 8,8,32,32);
	
		if (!drmicro_videoram || !drmicro_bg1 || !drmicro_bg2)
			return 1;
	
		tilemap_set_transparent_pen(drmicro_bg2,0);
	
		return 0;
	} };
	
	public static VideoUpdateHandlerPtr video_update_drmicro  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		int offs,adr,g;
		int chr,col,attr;
		int x,y,fx,fy;
	
		tilemap_draw(bitmap, cliprect, drmicro_bg1, 0, 0);
		tilemap_draw(bitmap, cliprect, drmicro_bg2, 0, 0);
	
		/* draw sprites */
	
		for (g=0;g<2;g++)
		{
			adr = 0x800*g;
	
			for (offs=0x00; offs<0x20; offs +=4)
			{
				x =    drmicro_videoram[offs + adr + 3];
				y =    drmicro_videoram[offs + adr + 0];
				attr = drmicro_videoram[offs + adr + 2];
				chr =  drmicro_videoram[offs + adr + 1];
	
				fx = (chr & 0x01) ^ flipscreen;
				fy = ((chr & 0x02) >> 1) ^ flipscreen;
	
				chr =  (chr >> 2) | (attr & 0xc0);
	
				col = (attr & 0x0f) + 0x00;
	
				if (!flipscreen)
					y = (240-y) & 0xff;
				else
					x = (240-x) & 0xff;
	
				drawgfx(bitmap,Machine->gfx[3-g],
						chr,
						col,
						fx,fy,
						x,y,
						cliprect,TRANSPARENCY_PEN,0);
	
				if (x>240)
				{
					drawgfx(bitmap,Machine->gfx[3-g],
							chr,
							col,
							fx,fy,
							x-256,y,
							cliprect,TRANSPARENCY_PEN,0);
				}
			}
		}
	} };
	
}
