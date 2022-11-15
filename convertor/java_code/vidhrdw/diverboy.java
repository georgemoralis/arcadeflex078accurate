/* Diver Boy - Video Hardware */

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class diverboy
{
	
	data16_t *diverboy_spriteram;
	size_t diverboy_spriteram_size;
	
	
	public static VideoStartHandlerPtr video_start_diverboy  = new VideoStartHandlerPtr() { public int handler(){
		return 0;
	} };
	
	static void diverboy_drawsprites( struct mame_bitmap *bitmap, const struct rectangle *cliprect )
	{
		data16_t *source = diverboy_spriteram;
		data16_t *finish = source + diverboy_spriteram_size/2;
	
		while (source < finish)
		{
			int xpos,ypos,number,colr,bank,flash;
	
			ypos = source[4];
			xpos = source[0];
			colr = (source[1]& 0x00f0) >> 4;
			number = source[3];
			flash = source[1] & 0x1000;
	
			colr |= ((source[1] & 0x000c) << 2);
	
			ypos = 0x100 - ypos;
	
			bank = (source[1]&0x0002) >> 1;
	
			if (!flash || (cpu_getcurrentframe() & 1))
			{
				drawgfx(bitmap,Machine->gfx[bank],
						number,
						colr,
						0,0,
						xpos,ypos,
						cliprect,(source[1] & 0x0008) ? TRANSPARENCY_NONE : TRANSPARENCY_PEN,0);
			}
	
			source+=8;
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_diverboy  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
	//	fillbitmap(bitmap,get_black_pen(),cliprect);
		diverboy_drawsprites(bitmap,cliprect);
	} };
}
