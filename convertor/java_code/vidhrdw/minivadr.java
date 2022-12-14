/***************************************************************************

Minivader (Space Invaders's mini game)
(c)1990 Taito Corporation

Driver by Takahiro Nogi (nogi@kt.rim.or.jp) 1999/12/19 -

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class minivadr
{
	
	
	
	/*******************************************************************
	
		Palette Setting.
	
	*******************************************************************/
	
	public static PaletteInitHandlerPtr palette_init_minivadr  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		palette_set_color(0,0x00,0x00,0x00);
		palette_set_color(1,0xff,0xff,0xff);
	} };
	
	
	/*******************************************************************
	
		Draw Pixel.
	
	*******************************************************************/
	public static WriteHandlerPtr minivadr_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int i;
		int x, y;
		int color;
	
	
		videoram.write(offset,data);
	
		x = (offset % 32) * 8;
		y = (offset / 32);
	
		if (x >= Machine->visible_area.min_x &&
				x <= Machine->visible_area.max_x &&
				y >= Machine->visible_area.min_y &&
				y <= Machine->visible_area.max_y)
		{
			for (i = 0; i < 8; i++)
			{
				color = Machine->pens[((data >> i) & 0x01)];
	
				plot_pixel(tmpbitmap, x + (7 - i), y, color);
			}
		}
	} };
	
	
	public static VideoUpdateHandlerPtr video_update_minivadr  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		if (get_vh_global_attribute_changed())
		{
			int offs;
	
			/* redraw bitmap */
	
			for (offs = 0; offs < videoram_size[0]; offs++)
				minivadr_videoram_w(offs,videoram.read(offs));
		}
		copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.visible_area,TRANSPARENCY_NONE,0);
	} };
}
