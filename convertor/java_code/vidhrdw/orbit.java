/***************************************************************************

Atari Orbit video emulation

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class orbit
{
	
	UINT8* orbit_playfield_ram;
	UINT8* orbit_sprite_ram;
	
	static struct tilemap* tilemap;
	
	static int orbit_flip_screen;
	
	
	public static WriteHandlerPtr orbit_playfield_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		tilemap_mark_tile_dirty(tilemap, offset);
	
		orbit_playfield_ram[offset] = data;
	} };
	
	
	public static WriteHandlerPtr orbit_sprite_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		orbit_sprite_ram[offset] = data;
	} };
	
	
	static UINT32 get_memory_offset(UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows)
	{
		return num_cols * row + col;
	}
	
	
	public static GetTileInfoHandlerPtr get_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		UINT8 code = orbit_playfield_ram[tile_index];
	
		int flags = 0;
	
		if (code & 0x40)
		{
			flags |= TILE_FLIPX;
		}
		if (orbit_flip_screen)
		{
			flags |= TILE_FLIPY;
		}
	
		SET_TILE_INFO(3, code & 0x3f, 0, flags)
	} };
	
	
	public static VideoStartHandlerPtr video_start_orbit  = new VideoStartHandlerPtr() { public int handler(){
		tilemap = tilemap_create(get_tile_info, get_memory_offset, 0, 16, 16, 32, 30);
	
		if (tilemap == NULL)
			return 1;
	
		return 0;
	} };
	
	
	static void orbit_draw_sprites(struct mame_bitmap* bitmap, const struct rectangle* cliprect)
	{
		const UINT8* p = orbit_sprite_ram;
	
		int i;
	
		for (i = 0; i < 16; i++)
		{
			int code = *p++;
			int vpos = *p++;
			int hpos = *p++;
			int flag = *p++;
	
			int layout =
				((flag & 0xc0) == 0x80) ? 1 :
				((flag & 0xc0) == 0xc0) ? 2 : 0;
	
			int flip_x = code & 0x40;
			int flip_y = code & 0x80;
	
			int zoom_x = 0x10000;
			int zoom_y = 0x10000;
	
			code &= 0x3f;
	
			if (flag & 1)
			{
				code |= 0x40;
			}
			if (flag & 2)
			{
				zoom_x *= 2;
			}
	
			vpos = 240 - vpos;
	
			hpos <<= 1;
			vpos <<= 1;
	
			drawgfxzoom(bitmap, Machine->gfx[layout], code, 0, flip_x, flip_y,
				hpos, vpos, cliprect, TRANSPARENCY_PEN, 0, zoom_x, zoom_y);
		}
	}
	
	
	public static VideoUpdateHandlerPtr video_update_orbit  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		orbit_flip_screen = readinputport(3) & 8;
	
		tilemap_draw(bitmap, cliprect, tilemap, 0, 0);
	
		orbit_draw_sprites(bitmap, cliprect);
	} };
}
