/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class labyrunr
{
	
	unsigned char *labyrunr_videoram1,*labyrunr_videoram2;
	static struct tilemap *layer0, *layer1;
	static struct rectangle clip0, clip1;
	
	
	public static PaletteInitHandlerPtr palette_init_labyrunr  = new PaletteInitHandlerPtr() { public void handler(char[] colortable, UBytePtr color_prom){
		int i,pal;
	
		for (pal = 0;pal < 8;pal++)
		{
			if (pal & 1)	/* chars, no lookup table */
			{
				for (i = 0;i < 256;i++)
					*(colortable++) = 16 * pal + (i & 0x0f);
			}
			else	/* sprites */
			{
				for (i = 0;i < 256;i++)
					if (color_prom[i] == 0)
						*(colortable++) = 0;
					else
						*(colortable++) = 16 * pal + color_prom.read(i);
			}
		}
	} };
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	public static GetTileInfoHandlerPtr get_tile_info0 = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int attr = labyrunr_videoram1[tile_index];
		int code = labyrunr_videoram1[tile_index + 0x400];
		int bit0 = (K007121_ctrlram[0][0x05] >> 0) & 0x03;
		int bit1 = (K007121_ctrlram[0][0x05] >> 2) & 0x03;
		int bit2 = (K007121_ctrlram[0][0x05] >> 4) & 0x03;
		int bit3 = (K007121_ctrlram[0][0x05] >> 6) & 0x03;
		int bank = ((attr & 0x80) >> 7) |
				((attr >> (bit0+2)) & 0x02) |
				((attr >> (bit1+1)) & 0x04) |
				((attr >> (bit2  )) & 0x08) |
				((attr >> (bit3-1)) & 0x10) |
				((K007121_ctrlram[0][0x03] & 0x01) << 5);
		int mask = (K007121_ctrlram[0][0x04] & 0xf0) >> 4;
	
		bank = (bank & ~(mask << 1)) | ((K007121_ctrlram[0][0x04] & mask) << 1);
	
		SET_TILE_INFO(
				0,
				code+bank*256,
				((K007121_ctrlram[0][6]&0x30)*2+16)+(attr&7),
				0)
	} };
	
	public static GetTileInfoHandlerPtr get_tile_info1 = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		int attr = labyrunr_videoram2[tile_index];
		int code = labyrunr_videoram2[tile_index + 0x400];
		int bit0 = (K007121_ctrlram[0][0x05] >> 0) & 0x03;
		int bit1 = (K007121_ctrlram[0][0x05] >> 2) & 0x03;
		int bit2 = (K007121_ctrlram[0][0x05] >> 4) & 0x03;
		int bit3 = (K007121_ctrlram[0][0x05] >> 6) & 0x03;
		int bank = ((attr & 0x80) >> 7) |
				((attr >> (bit0+2)) & 0x02) |
				((attr >> (bit1+1)) & 0x04) |
				((attr >> (bit2  )) & 0x08) |
				((attr >> (bit3-1)) & 0x10) |
				((K007121_ctrlram[0][0x03] & 0x01) << 5);
		int mask = (K007121_ctrlram[0][0x04] & 0xf0) >> 4;
	
		bank = (bank & ~(mask << 1)) | ((K007121_ctrlram[0][0x04] & mask) << 1);
	
		SET_TILE_INFO(
				0,
				code+bank*256,
				((K007121_ctrlram[0][6]&0x30)*2+16)+(attr&7),
				0)
	} };
	
	
	/***************************************************************************
	
		Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VideoStartHandlerPtr video_start_labyrunr  = new VideoStartHandlerPtr() { public int handler(){
		layer0 = tilemap_create(get_tile_info0,tilemap_scan_rows,TILEMAP_OPAQUE,8,8,32,32);
		layer1 = tilemap_create(get_tile_info1,tilemap_scan_rows,TILEMAP_OPAQUE,8,8,32,32);
	
		if (!layer0 || !layer1)
			return 1;
	
		clip0 = Machine.visible_area;
		clip0.min_x += 40;
	
		clip1 = Machine.visible_area;
		clip1.max_x = 39;
		clip1.min_x = 0;
	
		return 0;
	} };
	
	
	
	/***************************************************************************
	
	  Memory Handlers
	
	***************************************************************************/
	
	public static WriteHandlerPtr labyrunr_vram1_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (labyrunr_videoram1[offset] != data)
		{
			labyrunr_videoram1[offset] = data;
			tilemap_mark_tile_dirty(layer0,offset & 0x3ff);
		}
	} };
	
	public static WriteHandlerPtr labyrunr_vram2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (labyrunr_videoram2[offset] != data)
		{
			labyrunr_videoram2[offset] = data;
			tilemap_mark_tile_dirty(layer1,offset & 0x3ff);
		}
	} };
	
	
	
	/***************************************************************************
	
	  Screen Refresh
	
	***************************************************************************/
	
	public static VideoUpdateHandlerPtr video_update_labyrunr  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		struct rectangle finalclip0 = clip0, finalclip1 = clip1;
	
		sect_rect(&finalclip0, cliprect);
		sect_rect(&finalclip1, cliprect);
	
		tilemap_set_scrollx(layer0,0,K007121_ctrlram[0][0x00] - 40);
		tilemap_set_scrolly(layer0,0,K007121_ctrlram[0][0x02]);
	
		tilemap_draw(bitmap,&finalclip0,layer0,0,0);
		K007121_sprites_draw(0,bitmap,cliprect,spriteram,(K007121_ctrlram[0][6]&0x30)*2,40,0,-1);
		tilemap_draw(bitmap,&finalclip1,layer1,0,0);
	} };
}
