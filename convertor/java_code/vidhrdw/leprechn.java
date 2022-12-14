/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class leprechn
{
	
	
	static data8_t x,y,color;
	static data8_t graphics_command;
	static int pending;
	
	
	/* RGBI palette. Is it correct? */
	public static PaletteInitHandlerPtr palette_init_leprechn  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
	
		for (i = 0; i < 16; i++)
		{
			int bk = (i & 8) ? 0x40 : 0x00;
			int r = (i & 1) ? 0xff : bk;
			int g = (i & 2) ? 0xff : bk;
			int b = (i & 4) ? 0xff : bk;
	
			palette_set_color(i,r,g,b);
		}
	} };
	
	
	
	public static VideoStartHandlerPtr video_start_leprechn  = new VideoStartHandlerPtr() { public int handler(){
		videoram_size[0] = Machine.drv.screen_width * Machine.drv.screen_height;
	
		/* allocate our own dirty buffer */
		videoram = auto_malloc(videoram_size[0]);
		if (!videoram)
			return 1;
	
		return video_start_generic_bitmapped();
	} };
	
	
	public static WriteHandlerPtr leprechn_graphics_command_w = new WriteHandlerPtr() {public void handler(int offset, int data){
	    graphics_command = data;
	} };
	
	
	static void clear_screen_done_callback(int param)
	{
		// Indicate that the we are done
		via_0_ca1_w(0, 0);
	}
	
	
	public static WriteHandlerPtr leprechn_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int sx,sy;
	
		if (pending)
		{
			plot_pixel(tmpbitmap, x, y, Machine->pens[color]);
	        videoram.write(y * Machine->drv->screen_width + x,color);
	
	        pending = 0;
	   	}
	
	    switch (graphics_command)
	    {
	    case 0x00:	// Move and plot
	
	        color = data & 0x0f;
	
			if (data & 0x10)
			{
				if (data & 0x40)
					x--;
				else
					x++;
			}
	
			if (data & 0x20)
			{
				if (data & 0x80)
					y--;
				else
					y++;
			}
	
			pending = 1;
	
			break;
	
	    case 0x08:  // X position write
	        x = data;
	        break;
	
	    case 0x10:  // Y position write
	        y = data;
	        break;
	
	    case 0x18:  // Clear bitmap
	
			// Indicate that the we are busy
			via_0_ca1_w(0, 1);
	
	        memset(videoram, data, videoram_size[0]);
	
	        for (sx = 0; sx < Machine->drv->screen_width; sx++)
	        {
		        for (sy = 0; sy < Machine->drv->screen_height; sy++)
		        {
					plot_pixel(tmpbitmap, sx, sy, Machine->pens[data]);
				}
			}
	
			// Set a timer for an arbitrarily short period.
			// The real time it takes to clear to screen is not
			// significant for the software.
			timer_set(TIME_NOW, 0, clear_screen_done_callback);
	
	        break;
	
		default:
		    // Doesn't seem to happen.
		    logerror("Unknown Graphics Command #%2X at %04X\n", graphics_command, activecpu_get_pc());
	    }
	} };
	
	
	public static ReadHandlerPtr leprechn_videoram_r  = new ReadHandlerPtr() { public int handler(int offset){
	    return videoram.read(y * Machine->drv->screen_width + x);
	} };
}
