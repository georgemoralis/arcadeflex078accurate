/*
**	Video Driver for Taito Samurai (1985)
*/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class tsamurai
{
	
	
	/*
	** variables
	*/
	unsigned char *tsamurai_videoram;
	static int bgcolor;
	static int textbank1, textbank2;
	
	static struct tilemap *background, *foreground;
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	public static GetTileInfoHandlerPtr get_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		unsigned char attributes = tsamurai_videoram[2*tile_index+1];
		int tile_number = tsamurai_videoram[2*tile_index];
		tile_number += (( attributes & 0xc0 ) >> 6 ) * 256;	 /* legacy */
		tile_number += (( attributes & 0x20 ) >> 5 ) * 1024; /* Mission 660 add-on*/
		SET_TILE_INFO(
				0,
				tile_number,
				attributes & 0x1f,
				0)
	} };
	
	public static GetTileInfoHandlerPtr get_fg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int tile_number = videoram.read(tile_index);
		if (textbank1 & 0x01) tile_number += 256; /* legacy */
		if (textbank2 & 0x01) tile_number += 512; /* Mission 660 add-on */
		SET_TILE_INFO(
				1,
				tile_number,
				colorram.read(((tile_index&0x1f)*2)+1)& 0x1f,
				0)
	} };
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VideoStartHandlerPtr video_start_tsamurai  = new VideoStartHandlerPtr() { public int handler(){
		background = tilemap_create(get_bg_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT,8,8,32,32);
		foreground = tilemap_create(get_fg_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT,8,8,32,32);
	
		if (!background || !foreground)
			return 1;
	
		tilemap_set_transparent_pen(background,0);
		tilemap_set_transparent_pen(foreground,0);
	
		return 0;
	} };
	
	
	/***************************************************************************
	
	  Memory handlers
	
	***************************************************************************/
	
	public static WriteHandlerPtr tsamurai_scrolly_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		tilemap_set_scrolly( background, 0, data );
	} };
	
	public static WriteHandlerPtr tsamurai_scrollx_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		tilemap_set_scrollx( background, 0, data );
	} };
	
	public static WriteHandlerPtr tsamurai_bgcolor_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		bgcolor = data;
	} };
	
	public static WriteHandlerPtr tsamurai_textbank1_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( textbank1!=data )
		{
			textbank1 = data;
			tilemap_mark_all_tiles_dirty( foreground );
		}
	} };
	
	public static WriteHandlerPtr tsamurai_textbank2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( textbank2!=data )
		{
			textbank2 = data;
			tilemap_mark_all_tiles_dirty( foreground );
		}
	} };
	
	public static WriteHandlerPtr tsamurai_bg_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( tsamurai_videoram[offset]!=data )
		{
			tsamurai_videoram[offset]=data;
			offset = offset/2;
			tilemap_mark_tile_dirty(background,offset);
		}
	} };
	public static WriteHandlerPtr tsamurai_fg_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( videoram.read(offset)!=data )
		{
			videoram.write(offset,data);
			tilemap_mark_tile_dirty(foreground,offset);
		}
	} };
	public static WriteHandlerPtr tsamurai_fg_colorram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( colorram.read(offset)!=data )
		{
			colorram.write(offset,data);
			if (offset & 1)
			{
				int col = offset/2;
				int row;
				for (row = 0;row < 32;row++)
					tilemap_mark_tile_dirty(foreground,32*row+col);
			}
		}
	} };
	
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	
	static void draw_sprites( struct mame_bitmap *bitmap, const struct rectangle *cliprect )
	{
		struct GfxElement *gfx = Machine->gfx[2];
		const unsigned char *source = spriteram+32*4-4;
		const unsigned char *finish = spriteram; /* ? */
		static int flicker;
		flicker = 1-flicker;
	
		while( source>=finish )
		{
			int attributes = source[2]; /* bit 0x10 is usually, but not always set */
	
			int sx = source[3] - 16;
			int sy = 240-source[0];
			int sprite_number = source[1];
			int color = attributes&0x1f;
	
	#if 0
			/* VS Gong Fight */
			if (attributes == 0xe)
				attributes = 4;
			if (attributes > 7 || attributes < 4 || attributes == 5 )
				attributes = 6;
			color = attributes&0x1f;
	#endif
	
	#if 0
			/* Nunchakun */
			color = 0x2d - (attributes&0x1f);
	#endif
	
			if( sy<-16 ) sy += 256;
	
			/* 240-source[0] seems nice,but some dangling sprites appear on the left      */
			/* side in Mission 660.Setting it to 242 fixes it,but will break other games. */
			/* So I'm using this specific check. -kal 11 jul 2002 */
	//		if(sprite_type == 1) sy=sy+2;
	
			if( flip_screen() )
			{
				drawgfx( bitmap,gfx,
					sprite_number&0x7f,
					color,
					1,(sprite_number&0x80)?0:1,
					256-32-sx,256-32-sy,
					cliprect,TRANSPARENCY_PEN,0 );
			}
			else
			{
				drawgfx( bitmap,gfx,
					sprite_number&0x7f,
					color,
					0,sprite_number&0x80,
					sx,sy,
					cliprect,TRANSPARENCY_PEN,0 );
			}
	
			source -= 4;
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_tsamurai  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		int i;
	
	/* Do the column scroll used for the "660" logo on the title screen */
		tilemap_set_scroll_cols(foreground, 32);
		for (i = 0 ; i < 32 ; i++)
		{
			tilemap_set_scrolly(foreground, i, colorram.read(i*2));
		}
	/* end of column scroll code */
	
		/*
			This following isn't particularly efficient.  We'd be better off to
			dynamically change every 8th palette to the background color, so we
			could draw the background as an opaque tilemap.
	
			Note that the background color register isn't well understood
			(screenshots would be helpful)
		*/
		fillbitmap(bitmap,Machine->pens[bgcolor],cliprect);
		tilemap_draw(bitmap,cliprect,background,0,0);
		draw_sprites(bitmap,cliprect);
		tilemap_draw(bitmap,cliprect,foreground,0,0);
	} };
	
	/***************************************************************************
	
	VS Gong Fight runs on older hardware
	
	***************************************************************************/
	
	int vsgongf_color;
	
	public static WriteHandlerPtr vsgongf_color_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( vsgongf_color != data )
		{
			vsgongf_color = data;
			tilemap_mark_all_tiles_dirty( foreground );
		}
	} };
	
	
	public static GetTileInfoHandlerPtr get_vsgongf_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int tile_number = videoram.read(tile_index);
		int color = vsgongf_color&0x1f;
		if( textbank1 ) tile_number += 0x100;
		SET_TILE_INFO(
				1,
				tile_number,
				color,
				0)
	} };
	
	public static VideoStartHandlerPtr video_start_vsgongf  = new VideoStartHandlerPtr() { public int handler(){
		foreground = tilemap_create(get_vsgongf_tile_info,tilemap_scan_rows,TILEMAP_OPAQUE,8,8,32,32);
		if (!foreground) return 1;
		return 0;
	} };
	
	public static VideoUpdateHandlerPtr video_update_vsgongf  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		#ifdef MAME_DEBUG
		static int k;
		if( keyboard_pressed( KEYCODE_Q ) ){
			while( keyboard_pressed( KEYCODE_Q ) ){
				k++;
				vsgongf_color = k;
				tilemap_mark_all_tiles_dirty( foreground );
				}
		}
		#endif
	
		tilemap_draw(bitmap,cliprect,foreground,0,0);
		draw_sprites(bitmap,cliprect);
	} };
}
