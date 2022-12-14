/* Flower Video Hardware */

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class flower
{
	
	static struct tilemap *flower_bg0_tilemap, *flower_bg1_tilemap;
	
	static void flower_drawtextlayer( struct mame_bitmap *bitmap, const struct rectangle *cliprect )
	{
		int offs,sx,sy;
	
		unsigned char *vr = memory_region(REGION_CPU1);
	
		for (offs = 0;offs < 0x400;offs++)
		{
			sx = offs%32;
			sy = offs/32-2;
	
			if(sy<0)
			{
				int tt;
				tt=sx;
				sx=34+sy;
				sy=tt-2;
			}
	
			drawgfx(bitmap,Machine->gfx[0],
					vr[0xe000+offs],
					0,
					0,0,
					8*sx,8*sy,
					cliprect,TRANSPARENCY_PEN,0);
		}
	
	}
	
	static void flower_drawsprites( struct mame_bitmap *bitmap, const struct rectangle *cliprect )
	{
		unsigned char *floweram = memory_region(REGION_CPU1);
	
		const struct GfxElement *gfx = Machine->gfx[1];
		data8_t *source = &floweram[0xde00]+0x200;
		data8_t *finish = source - 0x200;
	
		source -= 8;
	
		while( source>=finish )
		{
			int xblock,yblock;
			int sy = 256-32-source[0]+1;
			int	sx = source[4]-55;
			int code = source[1] & 0x3f;
	
			int flipy = source[1] & 0x80; // wrong? sunflower needs it, ship afterwards breaks with it
			int flipx = source[1] & 0x40;
	
			int size = source[3];
	
			int xsize = ((size & 0x08)>>3);
			int ysize = ((size & 0x80)>>7);
	
			xsize++;
			ysize++;
	
			if (ysize==2) sy-=16;
	
			code |= ((source[2] & 0x01) << 6);
			code |= ((source[2] & 0x08) << 4);
	
	
			for (xblock = 0; xblock<xsize; xblock++)
			{
				for (yblock = 0; yblock<ysize; yblock++)
				{
					drawgfxzoom(bitmap,gfx,
							code+yblock+(8*xblock),
							0,
							flipx,flipy,
							sx+16*xblock,sy+16*yblock,
							cliprect,TRANSPARENCY_PEN,0,
							((size&7)+1)<<13,((size&0x70)+0x10)<<9);
				}
			}
			source -= 8;
		}
	
	}
	
	
	public static GetTileInfoHandlerPtr get_bg0_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code = (flower_sharedram[0x3000+tile_index]);
	//	int attr = (flower_sharedram[0x3100+tile_index]);
	
		SET_TILE_INFO(
				2,
				code,
				0,
				0)
	} };
	
	public static GetTileInfoHandlerPtr get_bg1_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code = (flower_sharedram[0x3800+tile_index]);
	//	int attr = (flower_sharedram[0x3900+tile_index]);
	
		SET_TILE_INFO(
				2,
				code,
				0,
				0)
	} };
	
	public static VideoStartHandlerPtr video_start_flower  = new VideoStartHandlerPtr() { public int handler(){
		flower_bg0_tilemap = tilemap_create(get_bg0_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT,16,16,16, 16);
		flower_bg1_tilemap = tilemap_create(get_bg1_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT,16,16,16, 16);
	
		tilemap_set_transparent_pen(flower_bg0_tilemap,0);
		tilemap_set_transparent_pen(flower_bg1_tilemap,0);
	
		return 0;
	
	} };
	
	public static VideoUpdateHandlerPtr video_update_flower  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		fillbitmap(bitmap, get_black_pen(), cliprect);
	
		tilemap_set_scrolly(flower_bg0_tilemap,0, flower_sharedram[0x3200]+16);
		tilemap_set_scrolly(flower_bg1_tilemap,0, flower_sharedram[0x3a00]+16);
	
		tilemap_draw(bitmap,cliprect,flower_bg0_tilemap,0,0);
		tilemap_draw(bitmap,cliprect,flower_bg1_tilemap,0,0);
	
	
		flower_drawsprites(bitmap,cliprect);
	
		flower_drawtextlayer(bitmap,cliprect);
	} };
	
	
	public static ReadHandlerPtr flower_sharedram_r  = new ReadHandlerPtr() { public int handler(int offset) return flower_sharedram[offset]; }
	
	public static WriteHandlerPtr flower_sharedram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		flower_sharedram[offset]=data;
	
		if ((offset >= 0x3000) && (offset <= 0x31ff)) // bg0 layer
		{
			tilemap_mark_tile_dirty(flower_bg0_tilemap,offset&0x1ff);
		}
	
		if ((offset >= 0x3800) && (offset <= 0x39ff)) // bg1 layer
		{
			tilemap_mark_tile_dirty(flower_bg1_tilemap,offset&0x1ff);
		}
	} };
}
