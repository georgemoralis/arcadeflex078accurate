/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class aeroboto
{
	
	
	// how the starfield ROM is interpreted: 0=256x256x1 linear bitmap, 1=8x8x1x1024 tilemap
	#define STARS_LAYOUT 1
	
	// scroll speed of the stars: 1=normal, 2=half, 3=one-third...etc.(possitive integers only)
	#define SCROLL_SPEED 1
	
	
	data8_t *aeroboto_videoram;
	data8_t *aeroboto_hscroll, *aeroboto_vscroll, *aeroboto_tilecolor;
	data8_t *aeroboto_starx, *aeroboto_stary, *aeroboto_bgcolor;
	
	static int aeroboto_charbank, aeroboto_starsoff;
	
	static struct tilemap *bg_tilemap;
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	public static GetTileInfoHandlerPtr get_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		unsigned char code = aeroboto_videoram[tile_index];
		SET_TILE_INFO(
				0,
				code + (aeroboto_charbank << 8),
				aeroboto_tilecolor[code],
				(aeroboto_tilecolor[code] >= 0x33) ? 0 : TILE_IGNORE_TRANSPARENCY)
	} };
	// transparency should only affect tiles with color 0x33 or higher
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VideoStartHandlerPtr video_start_aeroboto  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT,8,8,32,64);
	
		if (!bg_tilemap)
			return 1;
	
		tilemap_set_transparent_pen(bg_tilemap,0);
	
		tilemap_set_scroll_rows(bg_tilemap,64);
	
		#if STARS_LAYOUT
		{
			data8_t *rom, *temp;
			int i, length;
	
			rom = memory_region(REGION_GFX2);
			length = memory_region_length(REGION_GFX2);
			temp = auto_malloc(length);
			memcpy(temp, rom, length);
	
			for (i=0; i<length; i++) rom[(i&~0xff)+(i<<5&0xe0)+(i>>3&0x1f)] = temp[i];
	
			free(temp);
		}
		#endif
	
		return 0;
	} };
	
	
	
	/***************************************************************************
	
	  Memory handlers
	
	***************************************************************************/
	
	public static ReadHandlerPtr aeroboto_in0_r  = new ReadHandlerPtr() { public int handler(int offset){
		return readinputport(flip_screen() ? 1 : 0);
	} };
	
	public static WriteHandlerPtr aeroboto_3000_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		/* bit 0 selects both flip screen and player1/player2 controls */
		flip_screen_set(data & 0x01);
	
		/* bit 1 = char bank select */
		if (aeroboto_charbank != ((data & 0x02) >> 1))
		{
			tilemap_mark_all_tiles_dirty(bg_tilemap);
			aeroboto_charbank = (data & 0x02) >> 1;
		}
	
		/* bit 2 = disable star field? */
		aeroboto_starsoff = data & 0x4;
	} };
	
	public static WriteHandlerPtr aeroboto_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (aeroboto_videoram[offset] != data)
		{
			aeroboto_videoram[offset] = data;
			tilemap_mark_tile_dirty(bg_tilemap,offset);
		}
	} };
	
	public static WriteHandlerPtr aeroboto_tilecolor_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (aeroboto_tilecolor[offset] != data)
		{
			aeroboto_tilecolor[offset] = data;
			tilemap_mark_all_tiles_dirty(bg_tilemap);
		}
	} };
	
	
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	
	static void draw_sprites(struct mame_bitmap *bitmap, const struct rectangle *cliprect)
	{
		int offs;
	
		for (offs = 0;offs < spriteram_size;offs += 4)
		{
			int x = spriteram.read(offs+3);
			int y = 240 - spriteram.read(offs);
	
			if (flip_screen())
			{
				x = 248 - x;
				y = 240 - y;
			}
	
			drawgfx(bitmap, Machine->gfx[1],
					spriteram.read(offs+1),
					spriteram.read(offs+2)& 0x07,
					flip_screen(), flip_screen(),
					((x + 8) & 0xff) - 8, y,
					cliprect, TRANSPARENCY_PEN, 0);
		}
	}
	
	
	public static VideoUpdateHandlerPtr video_update_aeroboto  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		static struct rectangle splitrect1 = { 0, 255, 0, 39 };
		static struct rectangle splitrect2 = { 0, 255, 40, 255 };
		static int sx=0, sy=0;
		static data8_t ox=0, oy=0;
		data8_t *src_base, *src_colptr, *src_rowptr;
		int src_offsx, src_colmask, sky_color, star_color, x, y, i, j, pen;
	
		sky_color = star_color = *aeroboto_bgcolor << 2;
	
		// the star field is supposed to be seen through tile pen 0 when active
		if (!aeroboto_starsoff)
		{
			if (star_color < 0xd0) { star_color = 0xd0; sky_color = 0; }
			star_color += 2;
	
			fillbitmap(bitmap, sky_color, cliprect);
	
			// actual scroll speed is unknown but it can be adjusted by changing the SCROLL_SPEED constant
			sx += (char)(*aeroboto_starx - ox);
			ox = *aeroboto_starx;
			x = sx / SCROLL_SPEED;
	
			if (*aeroboto_vscroll != 0xff) sy += (char)(*aeroboto_stary - oy);
			oy = *aeroboto_stary;
			y = sy / SCROLL_SPEED;
	
			src_base = memory_region(REGION_GFX2);
	
			for (i=0; i<256; i++)
			{
				src_offsx = (x + i) & 0xff;
				src_colmask = 1 << (src_offsx & 7);
				src_offsx >>= 3;
				src_colptr = src_base + src_offsx;
				pen = star_color + ((i + 8) >> 4 & 1);
	
				for (j=0; j<256; j++)
				{
					src_rowptr = src_colptr + (((y + j) & 0xff) << 5 );
					if (!((unsigned)*src_rowptr & src_colmask)) plot_pixel(bitmap, i, j, pen);
				}
			}
		}
		else
		{
			sx = ox = *aeroboto_starx;
			sy = oy = *aeroboto_stary;
			fillbitmap(bitmap, sky_color, cliprect);
		}
	
		for (y = 0;y < 64; y++)
			tilemap_set_scrollx(bg_tilemap,y,aeroboto_hscroll[y]);
	
		// the playfield is part of a splitscreen and should not overlap with status display
		tilemap_set_scrolly(bg_tilemap,0,*aeroboto_vscroll);
		tilemap_draw(bitmap,&splitrect2,bg_tilemap,0,0);
	
		draw_sprites(bitmap,cliprect);
	
		// the status display behaves more closely to a 40-line splitscreen than an overlay
		tilemap_set_scrolly(bg_tilemap,0,0);
		tilemap_draw(bitmap,&splitrect1,bg_tilemap,0,0);
	} };
}
