/***************************************************************************

						  -= Yun Sung 16 Bit Games =-

					driver by	Luca Elia (l.elia@tin.it)


	[ 2 Scrolling Layers ]

	Tiles are 16 x 16 x 8. The layout of the tilemap is a bit weird:
	16 consecutive tile codes define a vertical column.
	16 columns form a page (256 x 256).
	The tilemap is made of 4 x 4 pages (1024 x 1024)

	[ 512? Sprites ]

	Sprites are 16 x 16 x 4 in size. There's RAM for 512, but
	the game just copies 384 entries.


***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class yunsun16
{
	
	/* Variables that driver has access to: */
	
	data16_t *yunsun16_vram_0,   *yunsun16_vram_1;
	data16_t *yunsun16_scroll_0, *yunsun16_scroll_1;
	data16_t *yunsun16_priority;
	
	
	/***************************************************************************
	
	
										Tilemaps
	
	
	***************************************************************************/
	
	static struct tilemap *tilemap_0, *tilemap_1;
	
	#define TMAP_GFX			(0)
	#define TILES_PER_PAGE_X	(0x10)
	#define TILES_PER_PAGE_Y	(0x10)
	#define PAGES_PER_TMAP_X	(0x4)
	#define PAGES_PER_TMAP_Y	(0x4)
	
	UINT32 yunsun16_tilemap_scan_pages(UINT32 col,UINT32 row,UINT32 num_cols,UINT32 num_rows)
	{
		return	(row / TILES_PER_PAGE_Y) * TILES_PER_PAGE_X * TILES_PER_PAGE_Y * PAGES_PER_TMAP_X +
				(row % TILES_PER_PAGE_Y) +
	
				(col / TILES_PER_PAGE_X) * TILES_PER_PAGE_X * TILES_PER_PAGE_Y +
				(col % TILES_PER_PAGE_X) * TILES_PER_PAGE_Y;
	}
	
	public static GetTileInfoHandlerPtr get_tile_info_0 = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		data16_t code = yunsun16_vram_0[ 2 * tile_index + 0 ];
		data16_t attr = yunsun16_vram_0[ 2 * tile_index + 1 ];
		SET_TILE_INFO(
				TMAP_GFX,
				code,
				attr & 0xf,
				0)
	} };
	
	public static GetTileInfoHandlerPtr get_tile_info_1 = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		data16_t code = yunsun16_vram_1[ 2 * tile_index + 0 ];
		data16_t attr = yunsun16_vram_1[ 2 * tile_index + 1 ];
		SET_TILE_INFO(
				TMAP_GFX,
				code,
				attr & 0xf,
				0)
	} };
	
	WRITE16_HANDLER( yunsun16_vram_0_w )
	{
		data16_t old_data	=	yunsun16_vram_0[offset];
		data16_t new_data	=	COMBINE_DATA(&yunsun16_vram_0[offset]);
		if (old_data != new_data)	tilemap_mark_tile_dirty(tilemap_0,offset/2);
	}
	
	WRITE16_HANDLER( yunsun16_vram_1_w )
	{
		data16_t old_data	=	yunsun16_vram_1[offset];
		data16_t new_data	=	COMBINE_DATA(&yunsun16_vram_1[offset]);
		if (old_data != new_data)	tilemap_mark_tile_dirty(tilemap_1,offset/2);
	}
	
	
	/***************************************************************************
	
	
								Video Hardware Init
	
	
	***************************************************************************/
	
	static int sprites_scrolldx, sprites_scrolldy;
	
	public static VideoStartHandlerPtr video_start_yunsun16  = new VideoStartHandlerPtr() { public int handler(){
		tilemap_0 = tilemap_create(	get_tile_info_0,yunsun16_tilemap_scan_pages,
									TILEMAP_TRANSPARENT,
									16,16,
									TILES_PER_PAGE_X*PAGES_PER_TMAP_X,TILES_PER_PAGE_Y*PAGES_PER_TMAP_Y);
	
		tilemap_1 = tilemap_create(	get_tile_info_1,yunsun16_tilemap_scan_pages,
									TILEMAP_TRANSPARENT,
									16,16,
									TILES_PER_PAGE_X*PAGES_PER_TMAP_X,TILES_PER_PAGE_Y*PAGES_PER_TMAP_Y);
	
		if (!tilemap_0 || !tilemap_1)	return 1;
	
		sprites_scrolldx = -0x40;
		sprites_scrolldy = -0x0f;
		tilemap_set_scrolldx(tilemap_0,-0x34,0);
		tilemap_set_scrolldx(tilemap_1,-0x38,0);
	
		tilemap_set_scrolldy(tilemap_0,-0x10,0);
		tilemap_set_scrolldy(tilemap_1,-0x10,0);
	
		tilemap_set_transparent_pen(tilemap_0,0xff);
		tilemap_set_transparent_pen(tilemap_1,0xff);
		return 0;
	} };
	
	
	/***************************************************************************
	
	
									Sprites Drawing
	
	
			0.w								X
	
			2.w								Y
	
			4.w								Code
	
			6.w		fedc ba98 7--- ----
					---- ---- -6-- ----		Flip Y
					---- ---- --5- ----		Flip X
					---- ---- ---4 3210		Color
	
	
	***************************************************************************/
	
	static void yunsun16_draw_sprites(struct mame_bitmap *bitmap,const struct rectangle *cliprect)
	{
		int offs;
	
		int max_x		=	Machine->visible_area.max_x+1;
		int max_y		=	Machine->visible_area.max_y+1;
	
		int pri			=	*yunsun16_priority & 7;
		int pri_mask;
	
		switch( pri & 7 )
		{
			case 5:		pri_mask = (1<<1)|(1<<2)|(1<<3);	break;
			case 6:		pri_mask = (1<<2)|(1<<3);			break;
			case 7:
			default:	pri_mask = 0;
		}
	
		for ( offs = (spriteram_size-8)/2 ; offs >= 0; offs -= 8/2 )
		{
			int x		=	spriteram16[offs + 0];
			int y		=	spriteram16[offs + 1];
			int code	=	spriteram16[offs + 2];
			int attr	=	spriteram16[offs + 3];
			int flipx	=	attr & 0x20;
			int flipy	=	attr & 0x40;
	
	
			x	+=	sprites_scrolldx;
			y	+=	sprites_scrolldy;
	
			if (flip_screen())	// not used?
			{
				flipx = NOT(flipx);		x = max_x - x - 16;
				flipy = NOT(flipy);		y = max_y - y - 16;
			}
	
			pdrawgfx(	bitmap,Machine->gfx[1],
						code,
						attr & 0x1f,
						flipx, flipy,
						x,y,
						cliprect,TRANSPARENCY_PEN,15,
						pri_mask	);
		}
	}
	
	
	/***************************************************************************
	
	
									Screen Drawing
	
	
	***************************************************************************/
	
	
	public static VideoUpdateHandlerPtr video_update_yunsun16  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_set_scrollx(tilemap_0, 0, yunsun16_scroll_0[ 0 ]);
		tilemap_set_scrolly(tilemap_0, 0, yunsun16_scroll_0[ 1 ]);
	
		tilemap_set_scrollx(tilemap_1, 0, yunsun16_scroll_1[ 0 ]);
		tilemap_set_scrolly(tilemap_1, 0, yunsun16_scroll_1[ 1 ]);
	
	//	usrintf_showmessage("%04X", *yunsun16_priority);
	
		fillbitmap(priority_bitmap,0,cliprect);
	
		/* The color of the this layer's transparent pen goes below everything */
		tilemap_draw(bitmap,cliprect,tilemap_0, TILEMAP_IGNORE_TRANSPARENCY, 0);
	
		tilemap_draw(bitmap,cliprect,tilemap_0, 0, 1);
	
		tilemap_draw(bitmap,cliprect,tilemap_1, 0, 2);
	
		yunsun16_draw_sprites(bitmap,cliprect);
	
		/* tilemap.c only copes with screen widths which are a multiple of 8 pixels */
		if ( (Machine.drv.screen_width-1-Machine.visible_area.max_x) & 7 )
		{
			struct rectangle clip;
			clip.min_x = Machine.visible_area.max_x+1;
			clip.max_x = Machine.drv.screen_width-1;
			clip.min_y = Machine.visible_area.min_y;
			clip.max_y = Machine.visible_area.max_y;
			sect_rect(&clip,cliprect);
			fillbitmap(bitmap,Machine.pens[0],&clip);
		}
	} };
}
