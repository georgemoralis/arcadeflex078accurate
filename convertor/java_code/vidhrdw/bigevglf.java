/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/
/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class bigevglf
{
	
	
	UINT8 *beg_spriteram1;
	UINT8 *beg_spriteram2;
	
	
	static UINT32 vidram_bank = 0;
	static UINT32 plane_selected = 0;
	static UINT32 plane_visible = 0;
	static UINT8 *vidram;
	
	
	static struct mame_bitmap *tmp_bitmap[4];
	
	public static WriteHandlerPtr beg_palette_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int color;
	
		paletteram.write(offset,data);
		color = paletteram.read(offset&0x3ff)| (paletteram.read(0x400+(offset&0x3ff))<< 8);
		palette_set_color(offset&0x3ff, color&0xf0, (color&0xf)<<4, (color&0xf00)>>4);
	} };
	
	public static WriteHandlerPtr beg_gfxcontrol_w = new WriteHandlerPtr() {public void handler(int offset, int data){
	/* bits used: 0,1,2,3
	 0 and 2 select plane,
	 1 and 3 select visible plane,
	*/
		plane_selected=((data & 4)>>1) | (data&1);
		plane_visible =((data & 8)>>2) | ((data&2)>>1);
	} };
	
	public static WriteHandlerPtr bigevglf_vidram_addr_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		vidram_bank = (data & 0xff) * 0x100;
	} };
	
	public static WriteHandlerPtr bigevglf_vidram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		UINT32 x,y,o;
		o = vidram_bank + offset;
		vidram[ o+0x10000*plane_selected ] = data;
		y = o >>8;
		x = (o & 255);
		plot_pixel(tmp_bitmap[plane_selected],x,y,data );
	} };
	
	public static ReadHandlerPtr bigevglf_vidram_r  = new ReadHandlerPtr() { public int handler(int offset){
		return vidram[ 0x10000 * plane_selected + vidram_bank + offset];
	} };
	
	public static VideoStartHandlerPtr video_start_bigevglf  = new VideoStartHandlerPtr() { public int handler(){
		tmp_bitmap[0] = auto_bitmap_alloc(Machine.drv.screen_width,Machine.drv.screen_height);
		tmp_bitmap[1] = auto_bitmap_alloc(Machine.drv.screen_width,Machine.drv.screen_height);
		tmp_bitmap[2] = auto_bitmap_alloc(Machine.drv.screen_width,Machine.drv.screen_height);
		tmp_bitmap[3] = auto_bitmap_alloc(Machine.drv.screen_width,Machine.drv.screen_height);
		vidram = auto_malloc(0x100*0x100 * 4);
		return 0;
	} };
	
	void beg_draw_sprites(struct mame_bitmap *bitmap, const struct rectangle *cliprect)
	{
		int i,j;
		for (i = 0xc0-4; i >= 0; i-=4)
		{
			int code,sx,sy;
			code = beg_spriteram2[i+1];
			sx = beg_spriteram2[i+3];
			sy = 200-beg_spriteram2[i];
			for(j=0;j<16;j++)
				drawgfx(bitmap,Machine->gfx[0],
					beg_spriteram1[(code<<4)+j]+((beg_spriteram1[0x400+(code<<4)+j]&0xf)<<8),
					beg_spriteram2[i+2] & 0xf,
					0,0,
					sx+((j&1)<<3),sy+((j>>1)<<3),
					cliprect,TRANSPARENCY_PEN,0);
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_bigevglf  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		copybitmap(bitmap,tmp_bitmap[ plane_visible ],0,0,0,0,cliprect,TRANSPARENCY_NONE, 0);
		beg_draw_sprites(bitmap,cliprect);
	} };
}
