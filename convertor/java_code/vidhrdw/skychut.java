/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

  (c) 12/2/1998 Lee Taylor

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class skychut
{
	
	
	
	UINT8 *iremm15_chargen;
	static int bottomline;
	
	
	public static WriteHandlerPtr skychut_colorram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (colorram.read(offset)!= data)
		{
			dirtybuffer[offset] = 1;
	
			colorram.write(offset,data);
		}
	} };
	
	public static WriteHandlerPtr skychut_ctrl_w = new WriteHandlerPtr() {public void handler(int offset, int data){
	//usrintf_showmessage("%02x",data);
	
		/* I have NO IDEA if this is correct or not */
		bottomline = ~data & 0x20;
	} };
	
	
	/***************************************************************************
	
	  Draw the game screen in the given mame_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
	public static VideoUpdateHandlerPtr video_update_skychut  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		int offs;
		if (get_vh_global_attribute_changed())
			memset (dirtybuffer, 1, videoram_size[0]);
	
		fillbitmap(bitmap,Machine.pens[7],cliprect);
	
		for (offs = 0;offs < 0x400;offs++)
		{
			int mask=iremm15_chargen[offs];
			int x = offs / 256;
			int y = offs % 256;
			int col = 0;
	
			switch (x)
			{
				case 0: x = 4*8;  col = 3; break;
				case 1: x = 26*8; col = 3; break;
				case 2: x = 7*8;  col = 5; break;
				case 3: x = 6*8;  col = 5; break;
			}
	
			if (x >= cliprect.min_x && x+7 <= cliprect.max_x
					&& y >= cliprect.min_y && y <= cliprect.max_y)
			{
				if (mask&0x80) plot_pixel(bitmap,x+0,y,col);
				if (mask&0x40) plot_pixel(bitmap,x+1,y,col);
				if (mask&0x20) plot_pixel(bitmap,x+2,y,col);
				if (mask&0x10) plot_pixel(bitmap,x+3,y,col);
				if (mask&0x08) plot_pixel(bitmap,x+4,y,col);
				if (mask&0x04) plot_pixel(bitmap,x+5,y,col);
				if (mask&0x02) plot_pixel(bitmap,x+6,y,col);
				if (mask&0x01) plot_pixel(bitmap,x+7,y,col);
			}
		}
	
		if (bottomline)
		{
			int y;
	
			for (y = cliprect.min_y;y <= cliprect.max_y;y++)
			{
				plot_pixel(bitmap,16,y,0);
			}
		}
	
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			int sx,sy;
	
	
			dirtybuffer[offs] = 0;
	
			sx = 31 - offs / 32;
			sy = offs % 32;
	
			drawgfx(bitmap,Machine.gfx[0],
					videoram.read(offs),
					colorram.read(offs),
					0,0,
					8*sx,8*sy,
					cliprect,TRANSPARENCY_PEN,0);
		}
	} };
	
	
	static void iremm15_drawgfx(struct mame_bitmap *bitmap, int ch,
								INT16 color, INT16 back, int x, int y)
	{
		UINT8 mask;
		int i;
	
		for (i=0; i<8; i++, y++) {
			mask=iremm15_chargen[ch*8+i];
			plot_pixel(bitmap,x+0,y,mask&0x80?color:back);
			plot_pixel(bitmap,x+1,y,mask&0x40?color:back);
			plot_pixel(bitmap,x+2,y,mask&0x20?color:back);
			plot_pixel(bitmap,x+3,y,mask&0x10?color:back);
			plot_pixel(bitmap,x+4,y,mask&0x08?color:back);
			plot_pixel(bitmap,x+5,y,mask&0x04?color:back);
			plot_pixel(bitmap,x+6,y,mask&0x02?color:back);
			plot_pixel(bitmap,x+7,y,mask&0x01?color:back);
		}
	}
	
	/***************************************************************************
	
	  Draw the game screen in the given mame_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
	public static VideoUpdateHandlerPtr video_update_iremm15  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		int offs;
		if (get_vh_global_attribute_changed())
			memset (dirtybuffer, 1, videoram_size[0]);
	
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			if (dirtybuffer[offs])
			{
				int sx,sy;
	
	
				dirtybuffer[offs] = 0;
	
				sx = 31 - offs / 32;
				sy = offs % 32;
	
				iremm15_drawgfx(tmpbitmap,
								videoram.read(offs),
								Machine.pens[colorram.read(offs)& 7],
								Machine.pens[7], // space beam not color 0
								8*sx,8*sy);
			}
		}
	
		copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.visible_area,TRANSPARENCY_NONE,0);
	} };
	
}
