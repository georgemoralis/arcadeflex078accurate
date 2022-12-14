/***************************************************************************

Atari Sprint 4 video emulation

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class sprint4
{
	
	UINT8* sprint4_video_ram;
	
	int sprint4_collision[4];
	
	static struct tilemap* tilemap;
	static struct mame_bitmap* helper;
	
	
	public static GetTileInfoHandlerPtr get_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		UINT8 code = sprint4_video_ram[tile_index];
	
		if ((code & 0x30) == 0x30)
		{
			SET_TILE_INFO(0, code & ~0x40, (code >> 6) ^ 3, 0)
		}
		else
		{
			SET_TILE_INFO(0, code, 4, 0)
		}
	} };
	
	
	public static VideoStartHandlerPtr video_start_sprint4  = new VideoStartHandlerPtr() { public int handler(){
		helper = auto_bitmap_alloc(Machine.drv.screen_width, Machine.drv.screen_height);
	
		if (helper == NULL)
		{
			return 1;
		}
	
		tilemap = tilemap_create(get_tile_info, tilemap_scan_rows, TILEMAP_OPAQUE, 8, 8, 32, 32);
	
		if (tilemap == NULL)
		{
			return 1;
		}
	
		return 0;
	} };
	
	
	public static WriteHandlerPtr sprint4_video_ram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (data != sprint4_video_ram[offset])
		{
			tilemap_mark_tile_dirty(tilemap, offset);
		}
	
		sprint4_video_ram[offset] = data;
	} };
	
	
	public static VideoUpdateHandlerPtr video_update_sprint4  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		int i;
	
		tilemap_draw(bitmap, cliprect, tilemap, 0, 0);
	
		for (i = 0; i < 4; i++)
		{
			int bank = 0;
	
			UINT8 hrz = sprint4_video_ram[0x390 + 2 * i + 0];
			UINT8 col = sprint4_video_ram[0x390 + 2 * i + 1];
			UINT8 vrt = sprint4_video_ram[0x398 + 2 * i + 0];
			UINT8 rot = sprint4_video_ram[0x398 + 2 * i + 1];
	
			if (i & 1)
			{
				bank = 32;
			}
	
			drawgfx(bitmap, Machine.gfx[1],
				(rot >> 3) | bank,
				(col & 0x80) ? 4 : i,
				0, 0,
				hrz - 15,
				vrt - 15,
				cliprect, TRANSPARENCY_PEN, 0);
		}
	
	} };
	
	
	public static VideoEofHandlerPtr video_eof_sprint4  = new VideoEofHandlerPtr() { public void handler(){
		int i;
	
		/* check for sprite-playfield collisions */
	
		for (i = 0; i < 4; i++)
		{
			struct rectangle rect;
	
			int x;
			int y;
	
			int bank = 0;
	
			UINT8 hrz = sprint4_video_ram[0x390 + 2 * i + 0];
			UINT8 vrt = sprint4_video_ram[0x398 + 2 * i + 0];
			UINT8 rot = sprint4_video_ram[0x398 + 2 * i + 1];
	
			rect.min_x = hrz - 15;
			rect.min_y = vrt - 15;
			rect.max_x = hrz - 15 + Machine->gfx[1]->width - 1;
			rect.max_y = vrt - 15 + Machine->gfx[1]->height - 1;
	
			if (rect.min_x < Machine->visible_area.min_x)
				rect.min_x = Machine->visible_area.min_x;
			if (rect.min_y < Machine->visible_area.min_y)
				rect.min_y = Machine->visible_area.min_y;
			if (rect.max_x > Machine->visible_area.max_x)
				rect.max_x = Machine->visible_area.max_x;
			if (rect.max_y > Machine->visible_area.max_y)
				rect.max_y = Machine->visible_area.max_y;
	
			tilemap_draw(helper, &rect, tilemap, 0, 0);
	
			if (i & 1)
			{
				bank = 32;
			}
	
			drawgfx(helper, Machine->gfx[1],
				(rot >> 3) | bank,
				4,
				0, 0,
				hrz - 15,
				vrt - 15,
				&rect, TRANSPARENCY_PEN, 1);
	
			for (y = rect.min_y; y <= rect.max_y; y++)
			{
				for (x = rect.min_x; x <= rect.max_x; x++)
				{
					if (read_pixel(helper, x, y) != 0)
					{
						sprint4_collision[i] = 1;
					}
				}
			}
		}
	} };
}
