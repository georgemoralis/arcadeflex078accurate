/***************************************************************************

	Meadows S2650 driver

****************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class meadows
{
	
	/* some constants to make life easier */
	#define SPR_ADJUST_X    -18
	#define SPR_ADJUST_Y    -14
	
	
	static struct tilemap *bg_tilemap;
	
	
	/*************************************
	 *
	 *	Tilemap callbacks
	 *
	 *************************************/
	
	static void get_tile_info(int tile_index)
	{
		SET_TILE_INFO(0, videoram[tile_index] & 0x7f, 0, 0);
	}
	
	
	
	/*************************************
	 *
	 *	Video startup
	 *
	 *************************************/
	
	public static VideoStartHandlerPtr video_start_meadows  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_tile_info, tilemap_scan_rows, TILEMAP_OPAQUE, 8,8, 32,30);
		if (!bg_tilemap)
			return 1;
		return 0;
	} };
	
	
	
	/*************************************
	 *
	 *	Video RAM write
	 *
	 *************************************/
	
	WRITE_HANDLER( meadows_videoram_w )
	{
		videoram[offset] = data;
		tilemap_mark_tile_dirty(bg_tilemap, offset);
	}
	
	
	
	/*************************************
	 *
	 *	Sprite RAM write
	 *
	 *************************************/
	
	WRITE_HANDLER( meadows_spriteram_w )
	{
		if (spriteram[offset] != data)
			force_partial_update(cpu_getscanline());
		spriteram[offset] = data;
	}
	
	
	
	/*************************************
	 *
	 *	Sprite rendering
	 *
	 *************************************/
	
	static void draw_sprites(struct mame_bitmap *bitmap, const struct rectangle *clip)
	{
		int i;
	
		for (i = 0; i < 4; i++)
		{
			int x = spriteram[i+0] + SPR_ADJUST_X;
			int y = spriteram[i+4] + SPR_ADJUST_Y;
			int code = spriteram[i+8] & 0x0f; 		/* bit #0 .. #3 select sprite */
	/*		int bank = (spriteram[i+8] >> 4) & 1; 	   bit #4 selects prom ???    */
			int bank = i;							/* that fixes it for now :-/ */
			int flip = spriteram[i+8] >> 5;			/* bit #5 flip vertical flag */
	
			drawgfx(bitmap, Machine->gfx[bank + 1], code, 0, flip, 0, x, y, clip, TRANSPARENCY_PEN, 0);
		}
	}
	
	
	
	/*************************************
	 *
	 *	Primary video update
	 *
	 *************************************/
	
	public static VideoUpdateHandlerPtr video_update_meadows  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		/* draw the background */
		tilemap_draw(bitmap, cliprect, bg_tilemap, 0, 0);
	
		/* draw the sprites */
		if (Machine->gfx[1])
			draw_sprites(bitmap, cliprect);
	} };
}
