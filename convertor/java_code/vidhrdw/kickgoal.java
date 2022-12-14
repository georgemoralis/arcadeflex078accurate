/* Kick Goal - Vidhrdw */

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class kickgoal
{
	
	struct tilemap *kickgoal_fgtm, *kickgoal_bgtm, *kickgoal_bg2tm;
	
	/* FG */
	public static GetTileInfoHandlerPtr get_kickgoal_fg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int tileno = kickgoal_fgram[tile_index*2] & 0x0fff;
		int color = kickgoal_fgram[tile_index*2+1] & 0x000f;
	
		SET_TILE_INFO(0,tileno + 0x7000,color + 0x00,0)
	} };
	
	/* BG */
	public static GetTileInfoHandlerPtr get_kickgoal_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int tileno = kickgoal_bgram[tile_index*2] & 0x0fff;
		int color = kickgoal_bgram[tile_index*2+1] & 0x000f;
	
		SET_TILE_INFO(1,tileno + 0x1000,color + 0x10,0)
	} };
	
	/* BG 2 */
	public static GetTileInfoHandlerPtr get_kickgoal_bg2_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int tileno = kickgoal_bg2ram[tile_index*2] & 0x07ff;
		int color = kickgoal_bg2ram[tile_index*2+1] & 0x000f;
		int flipx = kickgoal_bg2ram[tile_index*2+1] & 0x0020;
	
		SET_TILE_INFO(2,tileno + 0x800,color + 0x20,flipx ? TILE_FLIPX : 0);
	} };
	
	
	static UINT32 tilemap_scan_kicksbg2( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows )
	{
		/* logical (col,row) -> memory offset */
		return col*8 + (row & 0x7) + ((row & 0x3c) >> 3) * 0x200;
	}
	
	static UINT32 tilemap_scan_kicksbg( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows )
	{
		/* logical (col,row) -> memory offset */
		return col*16 + (row & 0xf) + ((row & 0x70) >> 4) * 0x400;
	}
	
	static UINT32 tilemap_scan_kicksfg( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows )
	{
		/* logical (col,row) -> memory offset */
		return col*32 + (row & 0x1f) + ((row & 0x20) >> 5) * 0x800;
	}
	
	
	public static VideoStartHandlerPtr video_start_kickgoal  = new VideoStartHandlerPtr() { public int handler(){
		kickgoal_fgtm = tilemap_create(get_kickgoal_fg_tile_info,tilemap_scan_kicksfg,TILEMAP_TRANSPARENT, 8, 16,64,64);
			tilemap_set_transparent_pen(kickgoal_fgtm,15);
		kickgoal_bgtm = tilemap_create(get_kickgoal_bg_tile_info,tilemap_scan_kicksbg,TILEMAP_TRANSPARENT, 16, 32,64,64);
			tilemap_set_transparent_pen(kickgoal_bgtm,15);
		kickgoal_bg2tm = tilemap_create(get_kickgoal_bg2_tile_info,tilemap_scan_kicksbg2,TILEMAP_OPAQUE, 32, 64,64,64);
		return 0;
	} };
	
	
	
	WRITE16_HANDLER( kickgoal_fgram_w )
	{
		if (kickgoal_fgram[offset] != data)
		{
			kickgoal_fgram[offset] = data;
			tilemap_mark_tile_dirty(kickgoal_fgtm,offset/2);
		}
	}
	
	WRITE16_HANDLER( kickgoal_bgram_w )
	{
		if (kickgoal_bgram[offset] != data)
		{
			kickgoal_bgram[offset] = data;
			tilemap_mark_tile_dirty(kickgoal_bgtm,offset/2);
		}
	}
	
	WRITE16_HANDLER( kickgoal_bg2ram_w )
	{
		if (kickgoal_bg2ram[offset] != data)
		{
			kickgoal_bg2ram[offset] = data;
			tilemap_mark_tile_dirty(kickgoal_bg2tm,offset/2);
		}
	}
	
	
	
	static void draw_sprites(struct mame_bitmap *bitmap,const struct rectangle *cliprect)
	{
		const struct GfxElement *gfx = Machine->gfx[1];
		int offs;
	
		for (offs = 0;offs < spriteram_size/2;offs += 4)
		{
			int xpos = spriteram16[offs+3];
			int ypos = spriteram16[offs+0] & 0x00ff;
			int tileno = spriteram16[offs+2] & 0x0fff;
			int flipx = spriteram16[offs+1] & 0x0020;
			int color = spriteram16[offs+1] & 0x000f;
	
			if (spriteram16[offs+0] == 0x0100) break;
	
			ypos *= 2;
	
			ypos = 0x200-ypos;
	
			drawgfx(bitmap,gfx,
					tileno,
					0x30 + color,
					flipx,0,
					xpos-16+4,ypos-32,
					cliprect,TRANSPARENCY_PEN,15);
		}
	}
	
	
	public static VideoUpdateHandlerPtr video_update_kickgoal  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		/* set scroll */
		tilemap_set_scrollx( kickgoal_fgtm, 0, kickgoal_scrram[0]  );
		tilemap_set_scrolly( kickgoal_fgtm, 0, kickgoal_scrram[1]*2  );
		tilemap_set_scrollx( kickgoal_bgtm, 0, kickgoal_scrram[2]  );
		tilemap_set_scrolly( kickgoal_bgtm, 0, kickgoal_scrram[3]*2  );
		tilemap_set_scrollx( kickgoal_bg2tm, 0, kickgoal_scrram[4]  );
		tilemap_set_scrolly( kickgoal_bg2tm, 0, kickgoal_scrram[5]*2  );
	
		/* draw */
		tilemap_draw(bitmap,cliprect,kickgoal_bg2tm,0,0);
		tilemap_draw(bitmap,cliprect,kickgoal_bgtm,0,0);
	
		draw_sprites(bitmap,cliprect);
	
		tilemap_draw(bitmap,cliprect,kickgoal_fgtm,0,0);
	
		/*
		usrintf_showmessage	("Regs %04x %04x %04x %04x %04x %04x %04x %04x",
		kickgoal_scrram[0],
		kickgoal_scrram[1],
		kickgoal_scrram[2],
		kickgoal_scrram[3],
		kickgoal_scrram[4],
		kickgoal_scrram[5],
		kickgoal_scrram[6],
		kickgoal_scrram[7]);
		*/
	} };
}
