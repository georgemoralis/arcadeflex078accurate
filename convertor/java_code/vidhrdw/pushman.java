/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class pushman
{
	
	static struct tilemap *bg_tilemap,*tx_tilemap;
	static data16_t control[2];
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	static UINT32 background_scan_rows(UINT32 col,UINT32 row,UINT32 num_cols,UINT32 num_rows)
	{
		/* logical (col,row) -> memory offset */
		return ((col & 0x7)) + ((7-(row & 0x7)) << 3) + ((col & 0x78) <<3) + ((0x38-(row&0x38))<<7);
	}
	
	public static GetTileInfoHandlerPtr get_back_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		unsigned char *bgMap = memory_region(REGION_GFX4);
		int tile;
	
		tile=bgMap[tile_index<<1]+(bgMap[(tile_index<<1)+1]<<8);
		SET_TILE_INFO(
				2,
				(tile&0xff)|((tile&0x4000)>>6),
				(tile>>8)&0xf,
				(tile&0x2000)?TILE_FLIPX:0)
	} };
	
	public static GetTileInfoHandlerPtr get_text_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int tile = videoram16[tile_index];
		SET_TILE_INFO(
				0,
				(tile&0xff)|((tile&0xc000)>>6)|((tile&0x2000)>>3),
				(tile>>8)&0xf,
				(tile&0x1000)?TILE_FLIPY:0)	/* not used? from Tiger Road */
	} };
	
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VideoStartHandlerPtr video_start_pushman  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_back_tile_info,background_scan_rows,TILEMAP_OPAQUE,     32,32,128,64);
		tx_tilemap = tilemap_create(get_text_tile_info,tilemap_scan_rows,   TILEMAP_TRANSPARENT, 8, 8, 32,32);
	
		if (!tx_tilemap || !bg_tilemap)
			return 1;
	
		tilemap_set_transparent_pen(tx_tilemap,3);
	
		return 0;
	} };
	
	
	
	/***************************************************************************
	
	  Memory handlers
	
	***************************************************************************/
	
	WRITE16_HANDLER( pushman_scroll_w )
	{
		COMBINE_DATA(&control[offset]);
	}
	
	WRITE16_HANDLER( pushman_videoram_w )
	{
		int oldword = videoram16[offset];
	
		COMBINE_DATA(&videoram16[offset]);
		if (oldword != videoram16[offset])
			tilemap_mark_tile_dirty(tx_tilemap,offset);
	}
	
	
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	
	static void draw_sprites(struct mame_bitmap *bitmap,const struct rectangle *cliprect)
	{
		int offs,x,y,color,sprite,flipx,flipy;
	
		for (offs = 0x0800-4;offs >=0;offs -= 4)
		{
			/* Don't draw empty sprite table entries */
			x = spriteram16[offs+3]&0x1ff;
			if (x==0x180) continue;
			if (x>0xff) x=0-(0x200-x);
			y = 240-spriteram16[offs+2];
			color = ((spriteram16[offs+1]>>2)&0xf);
			sprite = spriteram16[offs]&0x7ff;
			/* ElSemi - Sprite flip info */
			flipx=spriteram16[offs+1]&2;
			flipy=spriteram16[offs+1]&1;	/* flip y untested */
	
			drawgfx(bitmap,Machine->gfx[1],
					sprite,
	                color,flipx,flipy,x,y,
					cliprect,TRANSPARENCY_PEN,15);
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_pushman  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		/* Setup the tilemaps */
		tilemap_set_scrollx( bg_tilemap,0, control[0] );
		tilemap_set_scrolly( bg_tilemap,0, 0xf00-control[1] );
	
		tilemap_draw(bitmap,cliprect,bg_tilemap,0,0);
		draw_sprites(bitmap,cliprect);
		tilemap_draw(bitmap,cliprect,tx_tilemap,0,0);
	} };
}
