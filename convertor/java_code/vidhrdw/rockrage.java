/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class rockrage
{
	
	static int layer_colorbase[2];
	static int rockrage_vreg;
	
	public static PaletteInitHandlerPtr palette_init_rockrage  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
		#define TOTAL_COLORS(gfxn) (Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity)
		#define COLOR(gfxn,offs) (colortable[Machine.drv.gfxdecodeinfo[gfxn].color_codes_start + offs])
	
		/* build the lookup table for sprites. Palette is dynamic. */
		for (i = 0;i < TOTAL_COLORS(0)/2; i++){
			COLOR(0,i) = 0x00 + (color_prom.read(i)& 0x0f);
			COLOR(0,(TOTAL_COLORS(0)/2)+i) = 0x10 + (color_prom.read(0x100+i)& 0x0f);
		}
	} };
	
	/***************************************************************************
	
	  Callback for the K007342
	
	***************************************************************************/
	
	static void tile_callback(int layer, int bank, int *code, int *color)
	{
		if (layer == 1)
			*code |= ((*color & 0x40) << 2) | ((bank & 0x01) << 9);
		else
			*code |= ((*color & 0x40) << 2) | ((bank & 0x03) << 10) | ((rockrage_vreg & 0x04) << 7) | ((rockrage_vreg & 0x08) << 9);
		*color = layer_colorbase[layer] + (*color & 0x0f);
	}
	
	/***************************************************************************
	
	  Callback for the K007420
	
	***************************************************************************/
	
	static void sprite_callback(int *code,int *color)
	{
		*code |= ((*color & 0x40) << 2) | ((*color & 0x80) << 1)*((rockrage_vreg & 0x03) << 1);
		*code = (*code << 2) | ((*color & 0x30) >> 4);
		*color = 0;
	}
	
	
	public static WriteHandlerPtr rockrage_vreg_w = new WriteHandlerPtr() {public void handler(int offset, int data)
		/* bits 4-7: unused */
		/* bit 3: bit 4 of bank # (layer 0) */
		/* bit 2: bit 1 of bank # (layer 0) */
		/* bits 0-1: sprite bank select */
	
		if ((data & 0x0c) != (rockrage_vreg & 0x0c))
			tilemap_mark_all_tiles_dirty(ALL_TILEMAPS);
	
		rockrage_vreg = data;
	}
	
	/***************************************************************************
	
		Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VideoStartHandlerPtr video_start_rockrage  = new VideoStartHandlerPtr() { public int handler(){
		layer_colorbase[0] = 0x00;
		layer_colorbase[1] = 0x10;
	
		if (K007342_vh_start(0,tile_callback))
			return 1;
	
		if (K007420_vh_start(1,sprite_callback))
			return 1;
	
		K007420_set_banklimit(0x3ff); // bladestl and battlnts may also need this
	
		return 0;
	} };
	
	
	/***************************************************************************
	
	  Screen Refresh
	
	***************************************************************************/
	
	public static VideoUpdateHandlerPtr video_update_rockrage  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		K007342_tilemap_update();
	
		K007342_tilemap_draw( bitmap,cliprect, 0, TILEMAP_IGNORE_TRANSPARENCY ,0);
		K007420_sprites_draw( bitmap,cliprect );
		K007342_tilemap_draw( bitmap,cliprect, 0, 1 | TILEMAP_IGNORE_TRANSPARENCY ,0);
		K007342_tilemap_draw( bitmap,cliprect, 1, 0 ,0);
		K007342_tilemap_draw( bitmap,cliprect, 1, 1 ,0);
	} };
}
