/***************************************************************************

Dottori Kun (Head On's mini game)
(c)1990 SEGA

Driver by Takahiro Nogi (nogi@kt.rim.or.jp) 1999/12/15 -

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class dotrikun
{
	
	
	
	/*******************************************************************
	
		Palette Setting.
	
	*******************************************************************/
	public static WriteHandlerPtr dotrikun_color_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int r, g, b;
	
		r = ((data & 0x08) ? 0xff : 0x00);
		g = ((data & 0x10) ? 0xff : 0x00);
		b = ((data & 0x20) ? 0xff : 0x00);
		palette_set_color(0, r, g, b);		// BG color
	
		r = ((data & 0x01) ? 0xff : 0x00);
		g = ((data & 0x02) ? 0xff : 0x00);
		b = ((data & 0x04) ? 0xff : 0x00);
		palette_set_color(1, r, g, b);		// DOT color
	} };
	
	
	/*******************************************************************
	
		Draw Pixel.
	
	*******************************************************************/
	public static WriteHandlerPtr dotrikun_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int i;
		int x, y;
		int color;
	
	
		videoram.write(offset,data);
	
		x = 2 * (((offset % 16) * 8));
		y = 2 * ((offset / 16));
	
		if (x >= Machine->visible_area.min_x &&
				x <= Machine->visible_area.max_x &&
				y >= Machine->visible_area.min_y &&
				y <= Machine->visible_area.max_y)
		{
			for (i = 0; i < 8; i++)
			{
				color = Machine->pens[((data >> i) & 0x01)];
	
				/* I think the video hardware doubles pixels, screen would be too small otherwise */
				plot_pixel(tmpbitmap, x + 2*(7 - i),   y,   color);
				plot_pixel(tmpbitmap, x + 2*(7 - i)+1, y,   color);
				plot_pixel(tmpbitmap, x + 2*(7 - i),   y+1, color);
				plot_pixel(tmpbitmap, x + 2*(7 - i)+1, y+1, color);
			}
		}
	} };
	
	
	public static VideoUpdateHandlerPtr video_update_dotrikun  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		if (get_vh_global_attribute_changed())
		{
			int offs;
	
			/* redraw bitmap */
	
			for (offs = 0; offs < videoram_size[0]; offs++)
				dotrikun_videoram_w(offs,videoram.read(offs));
		}
		copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.visible_area,TRANSPARENCY_NONE,0);
	} };
}
