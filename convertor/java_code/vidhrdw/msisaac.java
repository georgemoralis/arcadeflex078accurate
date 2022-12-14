/*
*	Video Driver for Metal Soldier Isaac II (1985)
*/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class msisaac
{
	
	/*
	*	variables
	*/
	unsigned char *msisaac_videoram;
	unsigned char *msisaac_videoram2;
	
	static int textbank1, bg2_textbank;
	
	static struct tilemap *background, *background2, *foreground;
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	public static GetTileInfoHandlerPtr get_fg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int tile_number = videoram.read(tile_index);
		SET_TILE_INFO(	0,
				tile_number,
				0x10,
				0)
	} };
	
	public static GetTileInfoHandlerPtr get_bg2_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int tile_number = msisaac_videoram2[tile_index];
	
		/* graphics 0 or 1 */
		int gfx_b = (bg2_textbank>>3) & 1;
	
		SET_TILE_INFO(	gfx_b,
				tile_number,
				0x20,
				0)
	} };
	
	public static GetTileInfoHandlerPtr get_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int tile_number = msisaac_videoram[tile_index];
		SET_TILE_INFO(	1,
				0x100+tile_number,
				0x30,
				0)
	} };
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VideoStartHandlerPtr video_start_msisaac  = new VideoStartHandlerPtr() { public int handler(){
		background  = tilemap_create(get_bg_tile_info, tilemap_scan_rows,TILEMAP_OPAQUE,     8,8,32,32);
		background2 = tilemap_create(get_bg2_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT,8,8,32,32);
		foreground  = tilemap_create(get_fg_tile_info, tilemap_scan_rows,TILEMAP_TRANSPARENT,8,8,32,32);
	
		if (!background || !foreground)
			return 1;
	
		tilemap_set_transparent_pen(background2,0);
		tilemap_set_transparent_pen(foreground,0);
	
		return 0;
	} };
	
	
	/***************************************************************************
	
	  Memory handlers
	
	***************************************************************************/
	
	public static WriteHandlerPtr msisaac_fg_scrolly_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		tilemap_set_scrolly( foreground, 0, data );
	} };
	
	public static WriteHandlerPtr msisaac_fg_scrollx_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		tilemap_set_scrollx( foreground, 0, 9+data );
	} };
	
	public static WriteHandlerPtr msisaac_bg2_scrolly_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		tilemap_set_scrolly( background2, 0, data );
	} };
	
	public static WriteHandlerPtr msisaac_bg2_scrollx_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		tilemap_set_scrollx( background2, 0, 9+2+data );
	} };
	
	public static WriteHandlerPtr msisaac_bg_scrolly_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		tilemap_set_scrolly( background, 0, data );
	} };
	
	public static WriteHandlerPtr msisaac_bg_scrollx_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		tilemap_set_scrollx( background, 0, 9+4+data );
	} };
	
	
	public static WriteHandlerPtr msisaac_textbank1_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( textbank1!=data )
		{
			textbank1 = data;
			tilemap_mark_all_tiles_dirty( foreground );
		}
	} };
	
	public static WriteHandlerPtr msisaac_bg2_textbank_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( bg2_textbank != data )
		{
			bg2_textbank = data;
			tilemap_mark_all_tiles_dirty( background2 );
	
			//check if we are correct on this one
			if ((data!=8) && (data!=0))
			{
				logerror("bg2 control=%2x\n",data);
			}
		}
	} };
	
	public static WriteHandlerPtr msisaac_bg_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( msisaac_videoram[offset]!=data )
		{
			msisaac_videoram[offset]=data;
			tilemap_mark_tile_dirty(background,offset);
		}
	} };
	
	public static WriteHandlerPtr msisaac_bg2_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( msisaac_videoram2[offset]!=data )
		{
			msisaac_videoram2[offset]=data;
			tilemap_mark_tile_dirty(background2,offset);
		}
	} };
	
	public static WriteHandlerPtr msisaac_fg_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( videoram.read(offset)!=data )
		{
			videoram.write(offset,data);
			tilemap_mark_tile_dirty(foreground,offset);
		}
	} };
	
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	static void draw_sprites( struct mame_bitmap *bitmap, const struct rectangle *cliprect )
	{
		const unsigned char *source = spriteram+32*4-4;
		const unsigned char *finish = spriteram; /* ? */
	
		while( source>=finish )
		{
			int sx 				= source[0];
			int sy 				= 240-source[1]-1;
			int attributes		= source[2];
			int sprite_number	= source[3];
	
			int color = (attributes>>4) & 0xf;
			int flipx = (attributes&0x1);
			int flipy = (attributes&0x2);
	
			struct GfxElement *gfx = Machine->gfx[2];
	
			if (attributes&4)
			{
				//color = rand()&15;
				gfx = Machine->gfx[3];
			}
	
			if (attributes&8)	/* double size sprite */
			{
				switch(attributes&3)
				{
				case 0: /* flipx==0 && flipy==0 */
					drawgfx(bitmap,gfx,
						sprite_number+1,color,
						flipx,flipy,
						sx,sy-16,cliprect,TRANSPARENCY_PEN,0 );
					drawgfx(bitmap,gfx,
						sprite_number,color,
						flipx,flipy,
						sx,sy,cliprect,TRANSPARENCY_PEN,0 );
					break;
				case 1: /* flipx==1 && flipy==0 */
					drawgfx(bitmap,gfx,
						sprite_number+1,color,
						flipx,flipy,
						sx,sy-16,cliprect,TRANSPARENCY_PEN,0 );
					drawgfx(bitmap,gfx,
						sprite_number,color,
						flipx,flipy,
						sx,sy,cliprect,TRANSPARENCY_PEN,0 );
					break;
				case 2: /* flipx==0 && flipy==1 */
					drawgfx(bitmap,gfx,
						sprite_number,color,
						flipx,flipy,
						sx,sy-16,cliprect,TRANSPARENCY_PEN,0 );
					drawgfx(bitmap,gfx,
						sprite_number+1,color,
						flipx,flipy,
						sx,sy,cliprect,TRANSPARENCY_PEN,0 );
					break;
				case 3: /* flipx==1 && flipy==1 */
					drawgfx(bitmap,gfx,
						sprite_number,color,
						flipx,flipy,
						sx,sy-16,cliprect,TRANSPARENCY_PEN,0 );
					drawgfx(bitmap,gfx,
						sprite_number+1,color,
						flipx,flipy,
						sx,sy,cliprect,TRANSPARENCY_PEN,0 );
					break;
				}
			}
			else
			{
				drawgfx(bitmap,gfx,
					sprite_number,
					color,
					flipx,flipy,
					sx,sy,
					cliprect,TRANSPARENCY_PEN,0 );
			}
			source -= 4;
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_msisaac  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap,cliprect,background, 0,0);
		tilemap_draw(bitmap,cliprect,background2,0,0);
		draw_sprites(bitmap,cliprect);
		tilemap_draw(bitmap,cliprect,foreground, 0,0);
	} };
	
}
