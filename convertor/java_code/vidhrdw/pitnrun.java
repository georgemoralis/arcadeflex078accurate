/***************************************************************************

  - BG layer 32x128 , 8x8 tiles 4bpp , 2 palettes  (2nd is black )
  - TXT layer 32x32 , 8x8 tiles 4bpp , 2 palettes (2nd is black)
  - Sprites 16x16 3bpp, 8 palettes (0-3 are black)
  
  'Special' effects :
  
  - spotlight - gfx(BG+Sprites) outside spotlight is using black pals
                spotlight masks are taken from ROM pr8
                simulated using bitmaps and custom clipping rect 
                
  - lightning - BG color change (darkening ?) - simple analog circ.
  							simulated by additional palette

In debug build press 'w' for spotlight and 'e' for lightning
  							
***************************************************************************/
/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class pitnrun
{
	
	static int pitnrun_h_heed;
	static int pitnrun_v_heed;
	static int pitnrun_ha;
	static int pitnrun_scroll;
	static int pitnrun_char_bank;
	static int pitnrun_color_select;
	static struct mame_bitmap *tmp_bitmap[4];
	static struct tilemap *bg, *fg;
	UINT8* videoram2;
	
	
	public static GetTileInfoHandlerPtr get_tile_info1 = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code;
		code = videoram.read(tile_index);
		SET_TILE_INFO(
			0,
			code,
			0,
			0)
	} };
	
	public static GetTileInfoHandlerPtr get_tile_info2 = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int code;
		code = videoram2[tile_index];
		SET_TILE_INFO(
			1,
			code + (pitnrun_char_bank<<8),
			pitnrun_color_select&1,
			0)
	} };
	
	public static WriteHandlerPtr pitnrun_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		videoram.write(offset,data);
		tilemap_mark_all_tiles_dirty( fg );
	} };
	
	public static WriteHandlerPtr pitnrun_videoram2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		videoram2[offset] = data;
		tilemap_mark_all_tiles_dirty( bg );
	} };
	
	
	public static ReadHandlerPtr pitnrun_videoram_r  = new ReadHandlerPtr() { public int handler(int offset){
		return videoram.read(offset);
	} };
	
	public static ReadHandlerPtr pitnrun_videoram2_r  = new ReadHandlerPtr() { public int handler(int offset){
		return videoram2[offset];
	} };
	
	public static WriteHandlerPtr pitnrun_char_bank_select = new WriteHandlerPtr() {public void handler(int offset, int data){
		if(pitnrun_char_bank!=data)
		{
			tilemap_mark_all_tiles_dirty( bg );
			pitnrun_char_bank=data;
		}
	} };
	
	
	public static WriteHandlerPtr pitnrun_scroll_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		pitnrun_scroll = (pitnrun_scroll & (0xff<<((offset)?0:8))) |( data<<((offset)?8:0));
		tilemap_set_scrollx( bg, 0, pitnrun_scroll);
	} };
	
	public static WriteHandlerPtr pitnrun_ha_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		pitnrun_ha=data;
	} };
	
	public static WriteHandlerPtr pitnrun_h_heed_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		pitnrun_h_heed=data;
	} };
	
	public static WriteHandlerPtr pitnrun_v_heed_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		pitnrun_v_heed=data;
	} };
	
	public static WriteHandlerPtr pitnrun_color_select_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		pitnrun_color_select=data;
		tilemap_mark_all_tiles_dirty(ALL_TILEMAPS);
	} };
	
	void pitnrun_spotlights(void)
	{
		int x,y,i,b,datapix;
		unsigned char *ROM = memory_region(REGION_USER1);
		for(i=0;i<4;i++)
		 for(y=0;y<128;y++)
		  for(x=0;x<16;x++)
		  {
		  	datapix=ROM[128*16*i+x+y*16];
		  	for(b=0;b<8;b++)
		  	{
				plot_pixel(tmp_bitmap[i],x*8+(7-b), y,(datapix&1));
				datapix>>=1;
			}
	 	  }
	}
	
	
	public static PaletteInitHandlerPtr palette_init_pitnrun  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i;
		int bit0,bit1,bit2,r,g,b;
		for (i = 0;i < 32*3; i++)
		{
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
		
		/* fake bg palette for lightning effect*/
		for(i=2*16;i<2*16+16;i++)
		{
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
			r/=3;
			g/=3;
			b/=3;
	
			palette_set_color(i+16,(r>0xff)?0xff:r,(g>0xff)?0xff:g,(b>0xff)?0xff:b);	
		
		}
	} };
	
	public static VideoStartHandlerPtr video_start_pitnrun  = new VideoStartHandlerPtr() { public int handler(){
		fg = tilemap_create( get_tile_info1,tilemap_scan_rows,TILEMAP_TRANSPARENT,8,8,32,32 );
		bg = tilemap_create( get_tile_info2,tilemap_scan_rows,TILEMAP_OPAQUE,8,8,32*4,32 );
		tilemap_set_transparent_pen( fg, 0 );
		tmp_bitmap[0] = auto_bitmap_alloc(128,128);
		tmp_bitmap[1] = auto_bitmap_alloc(128,128);
		tmp_bitmap[2] = auto_bitmap_alloc(128,128);
		tmp_bitmap[3] = auto_bitmap_alloc(128,128);
		pitnrun_spotlights();
		return video_start_generic.handler();
	} };
	
	static void pitnrun_draw_sprites( struct mame_bitmap *bitmap, const struct rectangle *cliprect )
	{
		int sx, sy, flipx, flipy, offs,pal;
		
		for (offs = 0 ; offs < 0x100; offs+=4)
		{
			
			pal=spriteram.read(offs+2)&0x3;
		
			sy = 256-spriteram.read(offs+0)-16;
			sx = spriteram.read(offs+3);
			flipy = (spriteram.read(offs+1)&0x80)>>7;
			flipx = (spriteram.read(offs+1)&0x40)>>6;
			
			if (flip_screen_x)
			{
				sx = 256 - sx;
				flipx = NOT(flipx);
			}
			if (flip_screen_y)
			{
				sy = 240 - sy;
				flipy = NOT(flipy);
			}
			
			drawgfx(bitmap,Machine->gfx[2],
	 			(spriteram.read(offs+1)&0x3f)+((spriteram.read(offs+2)&0x80)>>1)+((spriteram.read(offs+2)&0x40)<<1),
				pal,
				flipx,flipy,
				sx,sy,
				cliprect,TRANSPARENCY_PEN,0);
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_pitnrun  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		int dx=0,dy=0;
		struct rectangle myclip=*cliprect; 
	
	#ifdef MAME_DEBUG
		if (keyboard_pressed_memory(KEYCODE_Q))
		{
			unsigned char *ROM = memory_region(REGION_CPU1);
			ROM[0x84f6]=0; /* lap 0 - normal */
		}
		
		if (keyboard_pressed_memory(KEYCODE_W))
		{
			unsigned char *ROM = memory_region(REGION_CPU1);
			ROM[0x84f6]=6; /* lap 6 = spotlight */
		}
		
		if (keyboard_pressed_memory(KEYCODE_E))
		{
			unsigned char *ROM = memory_region(REGION_CPU1);
			ROM[0x84f6]=2; /* lap 3 (trial 2)= lightnings */
			ROM[0x8102]=1;
		}
	#endif	
		
		fillbitmap(bitmap,0,cliprect);
		
		if(!(pitnrun_ha&4))
			tilemap_draw(bitmap,cliprect,bg, 0,0);
		else
		{
			dx=128-pitnrun_h_heed+((pitnrun_ha&8)<<5)+3;
			dy=128-pitnrun_v_heed+((pitnrun_ha&0x10)<<4);
			
			if (flip_screen_x)
				dx=128-dx+16;
				
			if (flip_screen_y)
				dy=128-dy;
	
			myclip.min_x=dx;
			myclip.min_y=dy;
			myclip.max_x=dx+127;
			myclip.max_y=dy+127;	
		
			
			if(myclip.min_y<cliprect.min_y)myclip.min_y=cliprect.min_y;
			if(myclip.min_x<cliprect.min_x)myclip.min_x=cliprect.min_x;
			
			if(myclip.max_y>cliprect.max_y)myclip.max_y=cliprect.max_y;
			if(myclip.max_x>cliprect.max_x)myclip.max_x=cliprect.max_x;
			
			tilemap_draw(bitmap,&myclip,bg, 0,0);
		}
		
		pitnrun_draw_sprites(bitmap,&myclip);
	
		if(pitnrun_ha&4)
			copybitmap(bitmap,tmp_bitmap[pitnrun_ha&3],flip_screen_x,flip_screen_y,dx,dy,&myclip,TRANSPARENCY_PEN, 1);
		tilemap_draw(bitmap,cliprect,fg, 0,0);
	} };	
	
	
	
	
}
