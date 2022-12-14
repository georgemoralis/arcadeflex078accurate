/***************************************************************************

	Kitco Crowns Golf hardware

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class crgolf
{
	
	
	/* globals */
	data8_t *crgolf_color_select;
	data8_t *crgolf_screen_flip;
	data8_t *crgolf_screen_select;
	data8_t *crgolf_screenb_enable;
	data8_t *crgolf_screena_enable;
	
	
	/* local variables */
	static struct mame_bitmap *screena;
	static struct mame_bitmap *screenb;
	static struct mame_bitmap *highbit;
	
	
	
	/*************************************
	 *
	 *	Video RAM writes
	 *
	 *************************************/
	
	public static WriteHandlerPtr crgolf_videoram_bit0_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		struct mame_bitmap *screen = (*crgolf_screen_select & 1) ? screenb : screena;
		int x = (offset % 32) * 8;
		int y = offset / 32;
		UINT16 *dest = (UINT16 *)screen->base + screen->rowpixels * y + x;
	
		dest[0] = (dest[0] & ~0x01) | ((data >> 7) & 0x01);
		dest[1] = (dest[1] & ~0x01) | ((data >> 6) & 0x01);
		dest[2] = (dest[2] & ~0x01) | ((data >> 5) & 0x01);
		dest[3] = (dest[3] & ~0x01) | ((data >> 4) & 0x01);
		dest[4] = (dest[4] & ~0x01) | ((data >> 3) & 0x01);
		dest[5] = (dest[5] & ~0x01) | ((data >> 2) & 0x01);
		dest[6] = (dest[6] & ~0x01) | ((data >> 1) & 0x01);
		dest[7] = (dest[7] & ~0x01) | ((data >> 0) & 0x01);
	} };
	
	
	public static WriteHandlerPtr crgolf_videoram_bit1_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		struct mame_bitmap *screen = (*crgolf_screen_select & 1) ? screenb : screena;
		int x = (offset % 32) * 8;
		int y = offset / 32;
		UINT16 *dest = (UINT16 *)screen->base + screen->rowpixels * y + x;
	
		dest[0] = (dest[0] & ~0x02) | ((data >> 6) & 0x02);
		dest[1] = (dest[1] & ~0x02) | ((data >> 5) & 0x02);
		dest[2] = (dest[2] & ~0x02) | ((data >> 4) & 0x02);
		dest[3] = (dest[3] & ~0x02) | ((data >> 3) & 0x02);
		dest[4] = (dest[4] & ~0x02) | ((data >> 2) & 0x02);
		dest[5] = (dest[5] & ~0x02) | ((data >> 1) & 0x02);
		dest[6] = (dest[6] & ~0x02) | ((data >> 0) & 0x02);
		dest[7] = (dest[7] & ~0x02) | ((data << 1) & 0x02);
	} };
	
	
	public static WriteHandlerPtr crgolf_videoram_bit2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		struct mame_bitmap *screen = (*crgolf_screen_select & 1) ? screenb : screena;
		int x = (offset % 32) * 8;
		int y = offset / 32;
		UINT16 *dest = (UINT16 *)screen->base + screen->rowpixels * y + x;
	
		dest[0] = (dest[0] & ~0x04) | ((data >> 5) & 0x04);
		dest[1] = (dest[1] & ~0x04) | ((data >> 4) & 0x04);
		dest[2] = (dest[2] & ~0x04) | ((data >> 3) & 0x04);
		dest[3] = (dest[3] & ~0x04) | ((data >> 2) & 0x04);
		dest[4] = (dest[4] & ~0x04) | ((data >> 1) & 0x04);
		dest[5] = (dest[5] & ~0x04) | ((data >> 0) & 0x04);
		dest[6] = (dest[6] & ~0x04) | ((data << 1) & 0x04);
		dest[7] = (dest[7] & ~0x04) | ((data << 2) & 0x04);
	} };
	
	
	
	/*************************************
	 *
	 *	Video RAM reads
	 *
	 *************************************/
	
	public static ReadHandlerPtr crgolf_videoram_bit0_r  = new ReadHandlerPtr() { public int handler(int offset){
		struct mame_bitmap *screen = (*crgolf_screen_select & 1) ? screenb : screena;
		int x = (offset % 32) * 8;
		int y = offset / 32;
		UINT16 *source = (UINT16 *)screen->base + screen->rowpixels * y + x;
	
		return	((source[0] & 0x01) << 7) |
				((source[1] & 0x01) << 6) |
				((source[2] & 0x01) << 5) |
				((source[3] & 0x01) << 4) |
				((source[4] & 0x01) << 3) |
				((source[5] & 0x01) << 2) |
				((source[6] & 0x01) << 1) |
				((source[7] & 0x01) << 0);
	} };
	
	
	public static ReadHandlerPtr crgolf_videoram_bit1_r  = new ReadHandlerPtr() { public int handler(int offset){
		struct mame_bitmap *screen = (*crgolf_screen_select & 1) ? screenb : screena;
		int x = (offset % 32) * 8;
		int y = offset / 32;
		UINT16 *source = (UINT16 *)screen->base + screen->rowpixels * y + x;
	
		return	((source[0] & 0x02) << 6) |
				((source[1] & 0x02) << 5) |
				((source[2] & 0x02) << 4) |
				((source[3] & 0x02) << 3) |
				((source[4] & 0x02) << 2) |
				((source[5] & 0x02) << 1) |
				((source[6] & 0x02) << 0) |
				((source[7] & 0x02) >> 1);
	} };
	
	
	public static ReadHandlerPtr crgolf_videoram_bit2_r  = new ReadHandlerPtr() { public int handler(int offset){
		struct mame_bitmap *screen = (*crgolf_screen_select & 1) ? screenb : screena;
		int x = (offset % 32) * 8;
		int y = offset / 32;
		UINT16 *source = (UINT16 *)screen->base + screen->rowpixels * y + x;
	
		return	((source[0] & 0x04) << 5) |
				((source[1] & 0x04) << 4) |
				((source[2] & 0x04) << 3) |
				((source[3] & 0x04) << 2) |
				((source[4] & 0x04) << 1) |
				((source[5] & 0x04) << 0) |
				((source[6] & 0x04) >> 1) |
				((source[7] & 0x04) >> 2);
	} };
	
	
	
	/*************************************
	 *
	 *	Color PROM decoding
	 *
	 *************************************/
	
	public static PaletteInitHandlerPtr palette_init_crgolf  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
	
		for (i = 0; i < 32; i++)
		{
			int bit0, bit1, bit2, r, g, b;
	
			/* red component */
			bit0 = (color_prom.read()>> 0) & 0x01;
			bit1 = (color_prom.read()>> 1) & 0x01;
			bit2 = (color_prom.read()>> 2) & 0x01;
			r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			/* green component */
			bit0 = (color_prom.read()>> 3) & 0x01;
			bit1 = (color_prom.read()>> 4) & 0x01;
			bit2 = (color_prom.read()>> 5) & 0x01;
			g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
			/* blue component */
			bit0 = (color_prom.read()>> 6) & 0x01;
			bit1 = (color_prom.read()>> 7) & 0x01;
			b = 0x4f * bit0 + 0xa8 * bit1;
	
			palette_set_color(i, r, g, b);
			color_prom++;
		}
	} };
	
	
	
	/*************************************
	 *
	 *	Video startup
	 *
	 *************************************/
	
	public static VideoStartHandlerPtr video_start_crgolf  = new VideoStartHandlerPtr() { public int handler(){
		/* allocate temporary bitmaps */
		screena = auto_bitmap_alloc(256, 256);
		screenb = auto_bitmap_alloc(256, 256);
		highbit = auto_bitmap_alloc(256, 256);
		if (!screena || !screenb || !highbit)
			return 1;
	
		/* initialize the "high bit" bitmap */
		fillbitmap(screena, 0, NULL);
		fillbitmap(screenb, 8, NULL);
		fillbitmap(highbit, 16, NULL);
	
		return 0;
	} };
	
	
	
	/*************************************
	 *
	 *	Video update
	 *
	 *************************************/
	
	public static VideoUpdateHandlerPtr video_update_crgolf  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		int flip = *crgolf_screen_flip & 1;
	
		/* draw screen b if enabled */
		if (~*crgolf_screenb_enable & 1)
			copybitmap(bitmap, screenb, flip, flip, 0, 0, cliprect, TRANSPARENCY_NONE, 0);
		else
			fillbitmap(bitmap, 8, cliprect);
	
		/* draw screen a if enabled */
		if (~*crgolf_screena_enable & 1)
			copybitmap(bitmap, screena, flip, flip, 0, 0, cliprect, TRANSPARENCY_PEN, 0);
	
		/* apply the color select bit */
		if (*crgolf_color_select)
			copybitmap(bitmap, highbit, 0, 0, 0, 0, cliprect, TRANSPARENCY_BLEND, 0);
	} };
}
