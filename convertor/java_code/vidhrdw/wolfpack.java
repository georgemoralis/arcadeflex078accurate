/***************************************************************************

Atari Wolf Pack (prototype) video emulation

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class wolfpack
{
	
	int wolfpack_collision;
	
	UINT8* wolfpack_alpha_num_ram;
	
	static unsigned current_index;
	
	static UINT8 wolfpack_video_invert;
	static UINT8 wolfpack_ship_reflect;
	static UINT8 wolfpack_pt_pos_select;
	static UINT8 wolfpack_pt_horz;
	static UINT8 wolfpack_pt_pic;
	static UINT8 wolfpack_ship_h;
	static UINT8 wolfpack_torpedo_pic;
	static UINT8 wolfpack_ship_size;
	static UINT8 wolfpack_ship_h_precess;
	static UINT8 wolfpack_ship_pic;
	static UINT8 wolfpack_torpedo_h;
	static UINT8 wolfpack_torpedo_v;
	
	static UINT8* LFSR;
	
	static struct mame_bitmap* helper;
	
	
	public static WriteHandlerPtr wolfpack_ship_size_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		UINT8 color;
	
		color = 0x48;
	
		if (data & 0x10) color += 0x13;
		if (data & 0x20) color += 0x22;
		if (data & 0x40) color += 0x3A;
		if (data & 0x80) color += 0x48;
	
		palette_set_color(3,
			color,
			color,
			color);
	
		palette_set_color(7,
			color < 0xb8 ? color + 0x48 : 0xff,
			color < 0xb8 ? color + 0x48 : 0xff,
			color < 0xb8 ? color + 0x48 : 0xff);
	
		wolfpack_ship_size = data >> 2;
	} };
	
	
	public static WriteHandlerPtr wolfpack_video_invert_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		wolfpack_video_invert = data & 1;
	} };
	public static WriteHandlerPtr wolfpack_ship_reflect_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		wolfpack_ship_reflect = data & 1;
	} };
	public static WriteHandlerPtr wolfpack_pt_pos_select_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		wolfpack_pt_pos_select = data & 1;
	} };
	public static WriteHandlerPtr wolfpack_pt_horz_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		wolfpack_pt_horz = data;
	} };
	public static WriteHandlerPtr wolfpack_pt_pic_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		wolfpack_pt_pic = data & 0x3f;
	} };
	public static WriteHandlerPtr wolfpack_ship_h_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		wolfpack_ship_h = data;
	} };
	public static WriteHandlerPtr wolfpack_torpedo_pic_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		wolfpack_torpedo_pic = data;
	} };
	public static WriteHandlerPtr wolfpack_ship_h_precess_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		wolfpack_ship_h_precess = data & 0x3f;
	} };
	public static WriteHandlerPtr wolfpack_ship_pic_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		wolfpack_ship_pic = data & 0x0f;
	} };
	public static WriteHandlerPtr wolfpack_torpedo_h_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		wolfpack_torpedo_h = data;
	} };
	public static WriteHandlerPtr wolfpack_torpedo_v_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		wolfpack_torpedo_v = data;
	} };
	
	
	public static VideoStartHandlerPtr video_start_wolfpack  = new VideoStartHandlerPtr() { public int handler(){
		UINT16 val = 0;
	
		int i;
	
		if ((LFSR = auto_malloc(0x8000)) == NULL)
		{
			return 1;
		}
	
		if ((helper = auto_bitmap_alloc(Machine.drv.screen_width, Machine.drv.screen_height)) == NULL)
		{
			return 1;
		}
	
		for (i = 0; i < 0x8000; i++)
		{
			int bit =
				(val >> 0x0) ^
				(val >> 0xE) ^ 1;
	
			val = (val << 1) | (bit & 1);
	
			LFSR[i] = (val & 0xc00) == 0xc00;
		}
	
		current_index = 0x80;
	
		return 0;
	} };
	
	
	static void draw_ship(struct mame_bitmap* bitmap, const struct rectangle* cliprect)
	{
		static const UINT32 scaler[] =
		{
			0x00000, 0x00500, 0x00A00, 0x01000,
			0x01000, 0x01200, 0x01500, 0x01800,
			0x01800, 0x01D00, 0x02200, 0x02800,
			0x02800, 0x02800, 0x02800, 0x02800,
			0x02800, 0x03000, 0x03800, 0x04000,
			0x04000, 0x04500, 0x04A00, 0x05000,
			0x05000, 0x05500, 0x05A00, 0x06000,
			0x06000, 0x06A00, 0x07500, 0x08000,
			0x08000, 0x08A00, 0x09500, 0x0A000,
			0x0A000, 0x0B000, 0x0C000, 0x0D000,
			0x0D000, 0x0E000, 0x0F000, 0x10000,
			0x10000, 0x11A00, 0x13500, 0x15000,
			0x15000, 0x17500, 0x19A00, 0x1C000,
			0x1C000, 0x1EA00, 0x21500, 0x24000,
			0x24000, 0x26A00, 0x29500, 0x2C000,
			0x2C000, 0x2FA00, 0x33500, 0x37000
		};
	
		int chop = (scaler[wolfpack_ship_size] * wolfpack_ship_h_precess) >> 16;
	
		drawgfxzoom(bitmap, Machine->gfx[1],
			wolfpack_ship_pic,
			0,
			wolfpack_ship_reflect, 0,
			2 * (wolfpack_ship_h - chop),
			128,
			cliprect,
			TRANSPARENCY_PEN, 0,
			2 * scaler[wolfpack_ship_size], scaler[wolfpack_ship_size]);
	}
	
	
	static void draw_torpedo(struct mame_bitmap* bitmap, const struct rectangle* cliprect)
	{
		int count = 0;
	
		int x;
		int y;
	
		drawgfx(bitmap, Machine->gfx[3],
			wolfpack_torpedo_pic,
			0,
			0, 0,
			2 * (244 - wolfpack_torpedo_h),
			224 - wolfpack_torpedo_v,
			cliprect,
			TRANSPARENCY_PEN, 0);
	
		for (y = 16; y < 224 - wolfpack_torpedo_v; y++)
		{
			int x1;
			int x2;
	
			if (y % 16 == 1)
			{
				count = (count - 1) & 7;
			}
	
			x1 = 248 - wolfpack_torpedo_h - count;
			x2 = 248 - wolfpack_torpedo_h + count;
	
			for (x = 2 * x1; x < 2 * x2; x++)
			{
				if (LFSR[(current_index + 0x300 * y + x) % 0x8000])
				{
					plot_pixel(bitmap, x, y, 1);
				}
			}
		}
	}
	
	
	static void draw_pt(struct mame_bitmap* bitmap, const struct rectangle* cliprect)
	{
		struct rectangle rect = *cliprect;
	
		if (!(wolfpack_pt_pic & 0x20))
		{
			rect.min_x = 256;
		}
		if (!(wolfpack_pt_pic & 0x10))
		{
			rect.max_x = 255;
		}
	
		drawgfx(bitmap, Machine->gfx[2],
			wolfpack_pt_pic,
			0,
			0, 0,
			2 * wolfpack_pt_horz,
			wolfpack_pt_pos_select ? 0x70 : 0xA0,
			&rect,
			TRANSPARENCY_PEN, 0);
	
		drawgfx(bitmap, Machine->gfx[2],
			wolfpack_pt_pic,
			0,
			0, 0,
			2 * wolfpack_pt_horz - 512,
			wolfpack_pt_pos_select ? 0x70 : 0xA0,
			&rect,
			TRANSPARENCY_PEN, 0);
	}
	
	
	static void draw_water(struct mame_bitmap* bitmap, const struct rectangle* cliprect)
	{
		struct rectangle rect = *cliprect;
	
		int x;
		int y;
	
		if (rect.max_y > 127)
		{
			rect.max_y = 127;
		}
	
		for (y = rect.min_y; y <= rect.max_y; y++)
		{
			UINT16* p = bitmap->line[y];
	
			for (x = rect.min_x; x <= rect.max_x; x++)
			{
				p[x] |= 4;
			}
		}
	}
	
	
	public static VideoUpdateHandlerPtr video_update_wolfpack  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		int i;
		int j;
	
		fillbitmap(bitmap, wolfpack_video_invert, cliprect);
	
		for (i = 0; i < 8; i++)
		{
			for (j = 0; j < 32; j++)
			{
				int code = wolfpack_alpha_num_ram[32 * i + j];
	
				drawgfx(bitmap, Machine.gfx[0],
					code,
					wolfpack_video_invert,
					0, 0,
					16 * j,
					192 + 8 * i,
					cliprect,
					TRANSPARENCY_NONE, 0);
			}
		}
	
		draw_pt(bitmap, cliprect);
		draw_ship(bitmap, cliprect);
		draw_torpedo(bitmap, cliprect);
		draw_water(bitmap, cliprect);
	} };
	
	
	public static VideoEofHandlerPtr video_eof_wolfpack  = new VideoEofHandlerPtr() { public void handler(){
		struct rectangle rect;
	
		int x;
		int y;
	
		rect.min_x = 0;
		rect.min_y = 0;
		rect.max_x = helper->width - 1;
		rect.max_y = helper->height - 1;
	
		fillbitmap(helper, 0, &rect);
	
		draw_ship(helper, &rect);
	
		for (y = 128; y < 224 - wolfpack_torpedo_v; y++)
		{
			int x1 = 248 - wolfpack_torpedo_h - 1;
			int x2 = 248 - wolfpack_torpedo_h + 1;
	
			for (x = 2 * x1; x < 2 * x2; x++)
			{
				if (x < 0 || x >= helper->width)
					continue;
				if (y < 0 || y >= helper->height)
					continue;
	
				if (read_pixel(helper, x, y))
				{
					wolfpack_collision = 1;
				}
			}
		}
	
		current_index += 0x300 * 262;
	} };
}
