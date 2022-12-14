/**
 * @file vidhrdw/djboy.c
 *
 * video hardware for DJ Boy
 */
/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class djboy
{
	
	static data8_t djboy_videoreg, djboy_scrollx, djboy_scrolly;
	static struct tilemap *background;
	
	void djboy_set_videoreg( data8_t data )
	{
		djboy_videoreg = data;
	}
	
	public static WriteHandlerPtr djboy_scrollx_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		djboy_scrollx = data;
	} };
	
	public static WriteHandlerPtr djboy_scrolly_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		djboy_scrolly = data;
	} };
	
	public static GetTileInfoHandlerPtr get_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		unsigned char attr;
		attr = videoram.read(tile_index + 0x400);
		SET_TILE_INFO(
				2,
				videoram.read(tile_index)+ ((attr & 0x0f) << 8),
				(attr >> 4),
				0)
	} };
	
	public static WriteHandlerPtr djboy_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( videoram.read(offset)!= data)
		{
			videoram.write(offset,data);
			tilemap_mark_tile_dirty( background, offset & 0x7ff);
		}
	} };
	
	public static VideoStartHandlerPtr video_start_djboy  = new VideoStartHandlerPtr() { public int handler(){
		background = tilemap_create(get_bg_tile_info,tilemap_scan_rows,TILEMAP_OPAQUE,16,16,64,32);
		if( background )
		{
			return 0;
		}
		return -1;
	} };
	
	static void
	draw_sprites( struct mame_bitmap *bitmap,const struct rectangle *cliprect )
	{
		int page;
		for( page=0; page<2; page++ )
		{
			const data8_t *pSource = &spriteram.read(page*0x800);
			int sx = 0;
			int sy = 0;
			int offs;
			for ( offs = 0 ; offs < 0x100 ; offs++)
			{
				int attr	=	pSource[offs + 0x300];
				int x		=	pSource[offs + 0x400] - ((attr << 8) & 0x100);
				int y		=	pSource[offs + 0x500] - ((attr << 7) & 0x100);
				int gfx		=	pSource[offs + 0x700];
				int code	=	pSource[offs + 0x600] + ((gfx & 0x3f) << 8);
				int flipx	=	gfx & 0x80;
				int flipy	=	gfx & 0x40;
				if( attr & 0x04 )
				{
					sx += x;
					sy += y;
				}
				else
				{
					sx  = x;
					sy  = y;
				}
				drawgfx(
					bitmap,Machine->gfx[1],
					code,
					attr >> 4,
					flipx, flipy,
					sx,sy,
					cliprect,TRANSPARENCY_PEN,0);
			} /* next tile */
		} /* next page */
	} /* draw_sprites */
	
	public static WriteHandlerPtr djboy_paletteram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int r,g,b;
		int val;
	
		paletteram.write(offset,data);
		offset &= ~1;
		val = (paletteram.read(offset)<<8) | paletteram.read(offset+1);
	
		r = (val >> 8) & 0xf;
		g = (val >> 4) & 0xf;
		b = (val >> 0) & 0xf;
	
		palette_set_color(
			offset/2,
			(r * 0xff) / 0xf,
			(g * 0xff) / 0xf,
			(b * 0xff) / 0xf );
	} };
	
	public static VideoUpdateHandlerPtr video_update_djboy  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		/**
		 * xx------ msb x
		 * --x----- msb y
		 * ---x---- flipscreen
		 * ----xxxx ROM bank
		 */
		int flip = djboy_videoreg&0x10;
		int scroll;
	
		tilemap_set_flip( ALL_TILEMAPS, flip?(TILEMAP_FLIPX|TILEMAP_FLIPY):0 );
	
		scroll = djboy_scrollx | ((djboy_videoreg&0xc0)<<2);
		tilemap_set_scrollx( background, 0, scroll );
	
		scroll = djboy_scrolly | ((djboy_videoreg&0x20)<<3);
		tilemap_set_scrolly( background, 0, scroll );	
	
		tilemap_draw( bitmap, cliprect,background,0,0 );
		draw_sprites( bitmap, cliprect );
	} };
}
