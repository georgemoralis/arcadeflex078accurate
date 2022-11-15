/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class exctsccr
{
	
	static int gfx_bank;
	
	static struct tilemap *bg_tilemap;
	
	/***************************************************************************
	
	  Convert the color PROMs into a more useable format.
	
	***************************************************************************/
	public static PaletteInitHandlerPtr palette_init_exctsccr  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i,idx;
		#define TOTAL_COLORS(gfxn) (Machine->gfx[gfxn]->total_colors * Machine->gfx[gfxn]->color_granularity)
		#define COLOR(gfxn,offs) (colortable[Machine->drv->gfxdecodeinfo[gfxn].color_codes_start + offs])
	
		for (i = 0;i < Machine->drv->total_colors;i++)
		{
			int bit0,bit1,bit2,r,g,b;
	
			bit0 = (color_prom.read(i)>> 0) & 0x01;
			bit1 = (color_prom.read(i)>> 1) & 0x01;
			bit2 = (color_prom.read(i)>> 2) & 0x01;
			r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
			bit0 = (color_prom.read(i)>> 3) & 0x01;
			bit1 = (color_prom.read(i)>> 4) & 0x01;
			bit2 = (color_prom.read(i)>> 5) & 0x01;
			g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
			bit0 = 0;
			bit1 = (color_prom.read(i)>> 6) & 0x01;
			bit2 = (color_prom.read(i)>> 7) & 0x01;
			b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			palette_set_color(i,r,g,b);
		}
	
		color_prom += Machine->drv->total_colors;
	
		/* characters */
		idx = 0;
		for (i = 0;i < 32;i++)
		{
			COLOR(0,idx++) = color_prom.read(256+0+(i*4));
			COLOR(0,idx++) = color_prom.read(256+1+(i*4));
			COLOR(0,idx++) = color_prom.read(256+2+(i*4));
			COLOR(0,idx++) = color_prom.read(256+3+(i*4));
			COLOR(0,idx++) = color_prom.read(256+128+0+(i*4));
			COLOR(0,idx++) = color_prom.read(256+128+1+(i*4));
			COLOR(0,idx++) = color_prom.read(256+128+2+(i*4));
			COLOR(0,idx++) = color_prom.read(256+128+3+(i*4));
		}
	
		/* sprites */
	
		idx=0;
	
		for (i = 0;i < 15*16;i++)
		{
			if ( (i%16) < 8 )
			{
				COLOR(2,idx) = color_prom.read(i)+16;
				idx++;
			}
		}
		for (i = 15*16;i < 16*16;i++)
		{
			if ( (i%16) > 7 )
			{
				COLOR(2,idx) = color_prom.read(i)+16;
				idx++;
			}
		}
		for (i = 16;i < 32;i++)
		{
			COLOR(2,idx++) = color_prom.read(256+0+(i*4))+16;
			COLOR(2,idx++) = color_prom.read(256+1+(i*4))+16;
			COLOR(2,idx++) = color_prom.read(256+2+(i*4))+16;
			COLOR(2,idx++) = color_prom.read(256+3+(i*4))+16;
			COLOR(2,idx++) = color_prom.read(256+128+0+(i*4))+16;
			COLOR(2,idx++) = color_prom.read(256+128+1+(i*4))+16;
			COLOR(2,idx++) = color_prom.read(256+128+2+(i*4))+16;
			COLOR(2,idx++) = color_prom.read(256+128+3+(i*4))+16;
		}
	
		/* Patch for goalkeeper */
		COLOR(2,29*8+7) = 16;
	
	} };
	
	static void exctsccr_fm_callback( int param )
	{
		cpu_set_irq_line_and_vector( 1, 0, HOLD_LINE, 0xff );
	}
	
	public static WriteHandlerPtr exctsccr_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (videoram.read(offset)!= data)
		{
			videoram.write(offset,data);
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr exctsccr_colorram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (colorram.read(offset)!= data)
		{
			colorram.write(offset,data);
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr exctsccr_gfx_bank_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (gfx_bank != (data & 0x01))
		{
			gfx_bank = data & 0x01;
			tilemap_mark_all_tiles_dirty(ALL_TILEMAPS);
		}
	} };
	
	public static WriteHandlerPtr exctsccr_flipscreen_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (flip_screen() != data)
		{
			flip_screen_set(data);
			tilemap_mark_all_tiles_dirty(ALL_TILEMAPS);
		}
	} };
	
	public static GetTileInfoHandlerPtr get_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code = videoram.read(tile_index);
		int color = colorram.read(tile_index)& 0x1f;
	
		SET_TILE_INFO(gfx_bank, code, color, 0)
	} };
	
	public static VideoStartHandlerPtr video_start_exctsccr  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_bg_tile_info, tilemap_scan_rows, 
			TILEMAP_OPAQUE, 8, 8, 32, 32);
	
		if ( !bg_tilemap )
			return 1;
	
		timer_pulse( TIME_IN_HZ( 75.0 ), 0, exctsccr_fm_callback ); /* updates fm */
	
		return 0;
	} };
	
	static void exctsccr_draw_sprites( struct mame_bitmap *bitmap ) {
		int offs;
		UINT8 *OBJ1, *OBJ2;
	
		OBJ1 = videoram;
		OBJ2 = &(spriteram.read(0x20));
	
		for ( offs = 0x0e; offs >= 0; offs -= 2 ) {
			int sx,sy,code,bank,flipx,flipy,color;
	
			sx = 256 - OBJ2[offs+1];
			sy = OBJ2[offs] - 16;
	
			code = ( OBJ1[offs] >> 2 ) & 0x3f;
			flipx = ( OBJ1[offs] ) & 0x01;
			flipy = ( OBJ1[offs] ) & 0x02;
			color = ( OBJ1[offs+1] ) & 0x1f;
			bank = 2;
			bank += ( ( OBJ1[offs+1] >> 4 ) & 1 );
	
			drawgfx(bitmap,Machine->gfx[bank],
					code,
					color,
					flipx, flipy,
					sx,sy,
					&Machine->visible_area,
					TRANSPARENCY_PEN,0);
		}
	
		OBJ1 = &(memory_region(REGION_CPU1)[0x8800]);
		OBJ2 = spriteram;
	
		for ( offs = 0x0e; offs >= 0; offs -= 2 ) {
			int sx,sy,code,bank,flipx,flipy,color;
	
			sx = 256 - OBJ2[offs+1];
			sy = OBJ2[offs] - 16;
	
			code = ( OBJ1[offs] >> 2 ) & 0x3f;
			flipx = ( OBJ1[offs] ) & 0x01;
			flipy = ( OBJ1[offs] ) & 0x02;
			color = ( OBJ1[offs+1] ) & 0x1f;
			bank = 3;
	
			if ( color == 0 )
				continue;
	
			if ( color < 0x10 )
				bank++;
	
			if ( color > 0x10 && color < 0x17 )
			{
				drawgfx(bitmap,Machine->gfx[4],
					code,
					0x0e,
					flipx, flipy,
					sx,sy,
					&Machine->visible_area,
					TRANSPARENCY_PEN,0);
	
				color += 6;
			}
			if ( color==0x1d && gfx_bank==1 )
			{
				drawgfx(bitmap,Machine->gfx[3],
					code,
					color,
					flipx, flipy,
					sx,sy,
					&Machine->visible_area,
					TRANSPARENCY_PEN,0);
				drawgfx(bitmap,Machine->gfx[4],
					code,
					color,
					flipx, flipy,
					sx,sy,
					&Machine->visible_area,
					TRANSPARENCY_COLOR, 16);
	
			} else
			{
			drawgfx(bitmap,Machine->gfx[bank],
					code,
					color,
					flipx, flipy,
					sx,sy,
					&Machine->visible_area,
					TRANSPARENCY_PEN,0);
			}
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_exctsccr  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap, &Machine->visible_area, bg_tilemap, 0, 0);
		exctsccr_draw_sprites( bitmap );
	} };
}
