/***************************************************************************

Atari Drag Race video emulation

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class dragrace
{
	
	UINT8* dragrace_playfield_ram;
	UINT8* dragrace_position_ram;
	
	static struct tilemap* tilemap;
	
	
	static UINT32 get_memory_offset(UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows)
	{
		return num_cols * row + col;
	}
	
	
	public static GetTileInfoHandlerPtr get_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		UINT8 code = dragrace_playfield_ram[tile_index];
	
		int num = 0;
		int col = 0;
	
		num = code & 0x1f;
	
		if ((code & 0xc0) == 0x40)
		{
			num |= 0x20;
		}
	
		switch (code & 0xA0)
		{
		case 0x00:
			col = 0;
			break;
		case 0x20:
			col = 1;
			break;
		case 0x80:
			col = (code & 0x40) ? 1 : 0;
			break;
		case 0xA0:
			col = (code & 0x40) ? 3 : 2;
			break;
		}
	
		SET_TILE_INFO(((code & 0xA0) == 0x80) ? 1 : 0, num, col, 0)
	} };
	
	
	public static VideoStartHandlerPtr video_start_dragrace  = new VideoStartHandlerPtr() { public int handler(){
		tilemap = tilemap_create(
			get_tile_info, get_memory_offset, TILEMAP_OPAQUE, 16, 16, 16, 16);
	
		return (tilemap == NULL) ? 1 : 0;
	} };
	
	
	public static VideoUpdateHandlerPtr video_update_dragrace  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		int y;
	
		tilemap_mark_all_tiles_dirty(tilemap);
	
		for (y = 0; y < 256; y += 4)
		{
			struct rectangle rect = *cliprect;
	
			int xl = dragrace_position_ram[y + 0] & 15;
			int xh = dragrace_position_ram[y + 1] & 15;
			int yl = dragrace_position_ram[y + 2] & 15;
			int yh = dragrace_position_ram[y + 3] & 15;
	
			tilemap_set_scrollx(tilemap, 0, 16 * xh + xl - 8);
			tilemap_set_scrolly(tilemap, 0, 16 * yh + yl);
	
			if (rect.min_y < y + 0) rect.min_y = y + 0;
			if (rect.max_y > y + 3) rect.max_y = y + 3;
	
			tilemap_draw(bitmap, &rect, tilemap, 0, 0);
		}
	} };
}
