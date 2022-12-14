/******************************************************************************

Ikki (c) 1985 Sun Electronics

Video hardware driver by Uki

	20/Jun/2001 -

******************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class ikki
{
	
	static UINT8 ikki_flipscreen, ikki_scroll[2];
	
	public static PaletteInitHandlerPtr palette_init_ikki  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
	
		for (i = 0; i<256; i++)
		{
			int r,g,b;
	
			r = color_prom.read(0)*0x11;
			g = color_prom.read(256)*0x11;
			b = color_prom.read(2*256)*0x11;
	
			palette_set_color(i,r,g,b);
	
			color_prom++;
		}
	
		/* 256th color is not drawn on screen */
		/* this is used for special transparent function */
		palette_set_color(256,0,0,1);
	
		color_prom += 2*256;
	
		/* color_prom now points to the beginning of the lookup table */
	
		/* sprites lookup table */
		for (i=0; i<512; i++)
		{
			int d = 255-*(color_prom++);
			if ( ((i % 8) == 7) && (d == 0) )
				*(colortable++) = 256; /* special transparent */
			else
				*(colortable++) = d; /* normal color */
		}
	
		/* bg lookup table */
		for (i=0; i<512; i++)
			*(colortable++) = *(color_prom++);
	
	} };
	
	public static WriteHandlerPtr ikki_scroll_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		ikki_scroll[offset] = data;
	} };
	
	public static WriteHandlerPtr ikki_scrn_ctrl_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		ikki_flipscreen = (data >> 2) & 1;
	} };
	
	public static VideoUpdateHandlerPtr video_update_ikki  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
	
		int offs,chr,col,px,py,f,bank,d;
		data8_t *VIDEOATTR = memory_region( REGION_USER1 );
	
		f = ikki_flipscreen;
	
		/* draw bg layer */
	
		for (offs=0; offs<(videoram_size[0]/2); offs++)
		{
			int sx,sy;
	
			sx = offs / 32;
			sy = offs % 32;
	
			py = sy*8;
			px = sx*8;
	
			d = VIDEOATTR[ sx ];
	
			switch (d)
			{
				case 0x02: /* scroll area */
					px = sx*8 - ikki_scroll[1];
					if (px<0)
						px=px+8*22;
					py = (sy*8 + ~ikki_scroll[0]) & 0xff;
					break;
	
				case 0x03: /* non-scroll area */
					break;
	
				case 0x00: /* sprite disable? */
					break;
	
				case 0x0d: /* sprite disable? */
					break;
	
				case 0x0b: /* non-scroll area (?) */
					break;
	
				case 0x0e: /* unknown */
					break;
			}
	
			if (f != 0)
			{
				px = 248-px;
				py = 248-py;
			}
	
			col = videoram.read(offs*2);
			bank = (col & 0xe0) << 3;
			col = ((col & 0x1f)<<0) | ((col & 0x80) >> 2);
	
			drawgfx(bitmap,Machine.gfx[0],
				videoram.read(offs*2+1)+ bank,
				col,
				f,f,
				px,py,
				Machine.visible_area,TRANSPARENCY_NONE,0);
		}
	
	/* draw sprites */
	
		fillbitmap(tmpbitmap, Machine.pens[256], 0);
	
		/* c060 - c0ff */
		for (offs=0x00; offs<0x800; offs +=4)
		{
			chr = spriteram.read(offs+1)>> 1 ;
			col = spriteram.read(offs+2);
	
			px = spriteram.read(offs+3);
			py = spriteram.read(offs+0);
	
			chr += (col & 0x80);
			col = (col & 0x3f) >> 0 ;
	
			if (f==0)
				py = 224-py;
			else
				px = 240-px;
	
			px = px & 0xff;
			py = py & 0xff;
	
			if (px>248)
				px = px-256;
			if (py>240)
				py = py-256;
	
			drawgfx(tmpbitmap,Machine.gfx[1],
				chr,
				col,
				f,f,
				px,py,
				Machine.visible_area,TRANSPARENCY_COLOR,0);
		}
	
		copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.visible_area,TRANSPARENCY_COLOR,256);
	
	
		/* mask sprites */
	
		for (offs=0; offs<(videoram_size[0]/2); offs++)
		{
			int sx,sy;
	
			sx = offs / 32;
			sy = offs % 32;
	
			d = VIDEOATTR[ sx ];
	
			if ( (d == 0) || (d == 0x0d) )
			{
				py = sy*8;
				px = sx*8;
	
				if (f != 0)
				{
					px = 248-px;
					py = 248-py;
				}
	
				col = videoram.read(offs*2);
				bank = (col & 0xe0) << 3;
				col = ((col & 0x1f)<<0) | ((col & 0x80) >> 2);
	
				drawgfx(bitmap,Machine.gfx[0],
					videoram.read(offs*2+1)+ bank,
					col,
					f,f,
					px,py,
					Machine.visible_area,TRANSPARENCY_NONE,0);
			}
		}
	
	} };
}
