/***************************************************************************

Taito Super Speed Race video emulation

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class sspeedr
{
	
	static int toggle;
	
	static unsigned driver_horz;
	static unsigned driver_vert;
	static unsigned driver_pic;
	
	static unsigned drones_horz;
	static unsigned drones_vert[3];
	static unsigned drones_mask;
	
	static unsigned track_horz;
	static unsigned track_vert[2];
	static unsigned track_ice;
	
	
	public static WriteHandlerPtr sspeedr_driver_horz_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		driver_horz = (driver_horz & 0x100) | data;
	} };
	
	
	public static WriteHandlerPtr sspeedr_driver_horz_2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		driver_horz = (driver_horz & 0xff) | ((data & 1) << 8);
	} };
	
	
	public static WriteHandlerPtr sspeedr_driver_vert_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		driver_vert = data;
	} };
	
	
	public static WriteHandlerPtr sspeedr_driver_pic_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		driver_pic = data & 0x1f;
	} };
	
	
	public static WriteHandlerPtr sspeedr_drones_horz_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		drones_horz = (drones_horz & 0x100) | data;
	} };
	
	
	public static WriteHandlerPtr sspeedr_drones_horz_2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		drones_horz = (drones_horz & 0xff) | ((data & 1) << 8);
	} };
	
	
	public static WriteHandlerPtr sspeedr_drones_mask_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		drones_mask = data & 0x3f;
	} };
	
	
	public static WriteHandlerPtr sspeedr_drones_vert_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		drones_vert[offset] = data;
	} };
	
	
	public static WriteHandlerPtr sspeedr_track_horz_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		track_horz = (track_horz & 0x100) | data;
	} };
	
	
	public static WriteHandlerPtr sspeedr_track_horz_2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		track_horz = (track_horz & 0xff) | ((data & 1) << 8);
	} };
	
	
	public static WriteHandlerPtr sspeedr_track_vert_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		track_vert[offset] = data & 0x7f;
	} };
	
	
	public static WriteHandlerPtr sspeedr_track_ice_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		track_ice = data & 0x07;
	} };
	
	
	static void draw_track(struct mame_bitmap* bitmap)
	{
		const UINT8* p = memory_region(REGION_GFX3);
	
		int x;
		int y;
	
		for (x = 0; x < 376; x++)
		{
			unsigned counter_x = x + track_horz + 0x50;
	
			int flag = 0;
	
			if (track_ice & 2)
			{
				flag = 1;
			}
			else if (track_ice & 4)
			{
				if (track_ice & 1)
				{
					flag = (counter_x <= 0x1ff);
				}
				else
				{
					flag = (counter_x >= 0x200);
				}
			}
	
			if (counter_x >= 0x200)
			{
				counter_x -= 0x1c8;
			}
	
			y = 0;
	
			/* upper landscape */
	
			for (; y < track_vert[0]; y++)
			{
				unsigned counter_y = y - track_vert[0];
	
				int offset =
					((counter_y & 0x1f) << 3) |
					((counter_x & 0x1c) >> 2) |
					((counter_x & 0xe0) << 3);
	
				if (counter_x & 2)
				{
					plot_pixel(bitmap, x, y, p[offset] / 16);
				}
				else
				{
					plot_pixel(bitmap, x, y, p[offset] % 16);
				}
			}
	
			/* street */
	
			for (; y < 128 + track_vert[1]; y++)
			{
				plot_pixel(bitmap, x, y, flag ? 15 : 0);
			}
	
			/* lower landscape */
	
			for (; y < 248; y++)
			{
				unsigned counter_y = y - track_vert[1];
	
				int offset =
					((counter_y & 0x1f) << 3) |
					((counter_x & 0x1c) >> 2) |
					((counter_x & 0xe0) << 3);
	
				if (counter_x & 2)
				{
					plot_pixel(bitmap, x, y, p[offset] / 16);
				}
				else
				{
					plot_pixel(bitmap, x, y, p[offset] % 16);
				}
			}
		}
	}
	
	
	static void draw_drones(struct mame_bitmap* bitmap, const struct rectangle* cliprect)
	{
		static const UINT8 code[6] =
		{
			0xf, 0x4, 0x3, 0x9, 0x7, 0xc
		};
	
		int i;
	
		for (i = 0; i < 6; i++)
		{
			int x;
			int y;
	
			if ((drones_mask >> i) & 1)
			{
				continue;
			}
	
			x = (code[i] << 5) - drones_horz - 0x50;
	
			if (x <= -32)
			{
				x += 0x1c8;
			}
	
			y = 0xf0 - drones_vert[i >> 1];
	
			drawgfx(bitmap, Machine->gfx[1],
				code[i] ^ toggle,
				0,
				0, 0,
				x,
				y,
				cliprect,
				TRANSPARENCY_PEN, 0);
		}
	}
	
	
	static void draw_driver(struct mame_bitmap* bitmap, const struct rectangle* cliprect)
	{
		int x;
		int y;
	
		if (!(driver_pic & 0x10))
		{
			return;
		}
	
		x = 0x1e0 - driver_horz - 0x50;
	
		if (x <= -32)
		{
			x += 0x1c8;
		}
	
		y = 0xf0 - driver_vert;
	
		drawgfx(bitmap, Machine->gfx[0],
			driver_pic,
			0,
			0, 0,
			x,
			y,
			cliprect,
			TRANSPARENCY_PEN, 0);
	}
	
	
	public static VideoStartHandlerPtr video_start_sspeedr  = new VideoStartHandlerPtr() { public int handler(){
		toggle = 0;
	
		return 0;
	} };
	
	
	public static VideoUpdateHandlerPtr video_update_sspeedr  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		draw_track(bitmap);
	
		draw_drones(bitmap, cliprect);
	
		draw_driver(bitmap, cliprect);
	} };
	
	
	public static VideoEofHandlerPtr video_eof_sspeedr  = new VideoEofHandlerPtr() { public void handler(){
		toggle ^= 1;
	} };
}
