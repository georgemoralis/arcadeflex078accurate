/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class blktiger
{
	
	unsigned char *blktiger_txvideoram;
	
	#define BGRAM_BANK_SIZE 0x1000
	#define BGRAM_BANKS 4
	
	static int blktiger_scroll_bank;
	static unsigned char *scroll_ram;
	static int screen_layout;
	static int chon,objon,bgon;
	
	static struct tilemap *tx_tilemap,*bg_tilemap8x4,*bg_tilemap4x8;
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	static UINT32 bg8x4_scan(UINT32 col,UINT32 row,UINT32 num_cols,UINT32 num_rows)
	{
		/* logical (col,row) -> memory offset */
		return (col & 0x0f) + ((row & 0x0f) << 4) + ((col & 0x70) << 4) + ((row & 0x30) << 7);
	}
	
	static UINT32 bg4x8_scan(UINT32 col,UINT32 row,UINT32 num_cols,UINT32 num_rows)
	{
		/* logical (col,row) -> memory offset */
		return (col & 0x0f) + ((row & 0x0f) << 4) + ((col & 0x30) << 4) + ((row & 0x70) << 6);
	}
	
	public static GetTileInfoHandlerPtr get_bg_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		/* the tile priority table is a guess compiled by looking at the game. It
		   was not derived from a PROM so it could be wrong. */
		static int split_table[16] =
		{
			3,0,2,2,	/* the fourth could be 1 instead of 2 */
			0,1,0,0,
			0,0,0,0,
			0,0,0,0
		};
		unsigned char attr = scroll_ram[2*tile_index + 1];
		int color = (attr & 0x78) >> 3;
		SET_TILE_INFO(
				1,
				scroll_ram[2*tile_index] + ((attr & 0x07) << 8),
				color,
				TILE_SPLIT(split_table[color]) | ((attr & 0x80) ? TILE_FLIPX : 0))
	} };
	
	public static GetTileInfoHandlerPtr get_tx_tile_info = new GetTileInfoHandlerPtr() { public void handler(int tile_index) 
	{
		unsigned char attr = blktiger_txvideoram[tile_index + 0x400];
		SET_TILE_INFO(
				0,
				blktiger_txvideoram[tile_index] + ((attr & 0xe0) << 3),
				attr & 0x1f,
				0)
	} };
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VideoStartHandlerPtr video_start_blktiger  = new VideoStartHandlerPtr() { public int handler(){
		scroll_ram = auto_malloc(BGRAM_BANK_SIZE * BGRAM_BANKS);
	
		tx_tilemap =    tilemap_create(get_tx_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT,8,8,32,32);
		bg_tilemap8x4 = tilemap_create(get_bg_tile_info,bg8x4_scan,       TILEMAP_SPLIT,   16,16,128,64);
		bg_tilemap4x8 = tilemap_create(get_bg_tile_info,bg4x8_scan,       TILEMAP_SPLIT,   16,16,64,128);
	
		if (!scroll_ram || !tx_tilemap || !bg_tilemap8x4 || !bg_tilemap4x8)
			return 1;
	
		tilemap_set_transparent_pen(tx_tilemap,3);
	
		tilemap_set_transmask(bg_tilemap8x4,0,0xffff,0x8000);	/* split type 0 is totally transparent in front half */
		tilemap_set_transmask(bg_tilemap8x4,1,0xfff0,0x800f);	/* split type 1 has pens 4-15 transparent in front half */
		tilemap_set_transmask(bg_tilemap8x4,2,0xff00,0x80ff);	/* split type 1 has pens 8-15 transparent in front half */
		tilemap_set_transmask(bg_tilemap8x4,3,0xf000,0x8fff);	/* split type 1 has pens 12-15 transparent in front half */
		tilemap_set_transmask(bg_tilemap4x8,0,0xffff,0x8000);
		tilemap_set_transmask(bg_tilemap4x8,1,0xfff0,0x800f);
		tilemap_set_transmask(bg_tilemap4x8,2,0xff00,0x80ff);
		tilemap_set_transmask(bg_tilemap4x8,3,0xf000,0x8fff);
	
		return 0;
	} };
	
	
	
	/***************************************************************************
	
	  Memory handlers
	
	***************************************************************************/
	
	public static WriteHandlerPtr blktiger_txvideoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (blktiger_txvideoram[offset] != data)
		{
			blktiger_txvideoram[offset] = data;
			tilemap_mark_tile_dirty(tx_tilemap,offset & 0x3ff);
		}
	} };
	
	public static ReadHandlerPtr blktiger_bgvideoram_r  = new ReadHandlerPtr() { public int handler(int offset){
		return scroll_ram[offset + blktiger_scroll_bank];
	} };
	
	public static WriteHandlerPtr blktiger_bgvideoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		offset += blktiger_scroll_bank;
	
		if (scroll_ram[offset] != data)
		{
			scroll_ram[offset] = data;
			tilemap_mark_tile_dirty(bg_tilemap8x4,offset/2);
			tilemap_mark_tile_dirty(bg_tilemap4x8,offset/2);
		}
	} };
	
	public static WriteHandlerPtr blktiger_bgvideoram_bank_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		blktiger_scroll_bank = (data % BGRAM_BANKS) * BGRAM_BANK_SIZE;
	} };
	
	
	public static WriteHandlerPtr blktiger_scrolly_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		static unsigned char scroll[2];
		int scrolly;
	
		scroll[offset] = data;
		scrolly = scroll[0] | (scroll[1] << 8);
		tilemap_set_scrolly(bg_tilemap8x4,0,scrolly);
		tilemap_set_scrolly(bg_tilemap4x8,0,scrolly);
	} };
	
	public static WriteHandlerPtr blktiger_scrollx_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		static unsigned char scroll[2];
		int scrollx;
	
		scroll[offset] = data;
		scrollx = scroll[0] | (scroll[1] << 8);
		tilemap_set_scrollx(bg_tilemap8x4,0,scrollx);
		tilemap_set_scrollx(bg_tilemap4x8,0,scrollx);
	} };
	
	
	public static WriteHandlerPtr blktiger_video_control_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		/* bits 0 and 1 are coin counters */
		coin_counter_w(0,data & 1);
		coin_counter_w(1,data & 2);
	
		/* bit 5 resets the sound CPU */
		cpu_set_reset_line(1,(data & 0x20) ? ASSERT_LINE : CLEAR_LINE);
	
		/* bit 6 flips screen */
		flip_screen_set(data & 0x40);
	
		/* bit 7 enables characters? Just a guess */
		chon = ~data & 0x80;
	} };
	
	public static WriteHandlerPtr blktiger_video_enable_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		/* not sure which is which, but I think that bit 1 and 2 enable background and sprites */
		/* bit 1 enables bg ? */
		bgon = ~data & 0x02;
	
		/* bit 2 enables sprites ? */
		objon = ~data & 0x04;
	} };
	
	public static WriteHandlerPtr blktiger_screen_layout_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		screen_layout = data;
		tilemap_set_enable(bg_tilemap8x4, screen_layout);
		tilemap_set_enable(bg_tilemap4x8,!screen_layout);
	} };
	
	
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	
	static void draw_sprites(struct mame_bitmap *bitmap, const struct rectangle *cliprect)
	{
		int offs;
	
		/* Draw the sprites. */
		for (offs = spriteram_size - 4;offs >= 0;offs -= 4)
		{
			int attr = buffered_spriteram[offs+1];
			int sx = buffered_spriteram[offs + 3] - ((attr & 0x10) << 4);
			int sy = buffered_spriteram[offs + 2];
			int code = buffered_spriteram[offs] | ((attr & 0xe0) << 3);
			int color = attr & 0x07;
			int flipx = attr & 0x08;
	
			if (flip_screen())
			{
				sx = 240 - sx;
				sy = 240 - sy;
				flipx = NOT(flipx);
			}
	
			drawgfx(bitmap,Machine->gfx[2],
					code,
					color,
					flipx,flip_screen(),
					sx,sy,
					cliprect,TRANSPARENCY_PEN,15);
		}
	}
	
	public static VideoUpdateHandlerPtr video_update_blktiger  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		fillbitmap(bitmap,Machine.pens[1023],cliprect);
	
		if (bgon)
			tilemap_draw(bitmap,cliprect,screen_layout ? bg_tilemap8x4 : bg_tilemap4x8,TILEMAP_BACK,0);
	
		if (objon)
			draw_sprites(bitmap,cliprect);
	
		if (bgon)
			tilemap_draw(bitmap,cliprect,screen_layout ? bg_tilemap8x4 : bg_tilemap4x8,TILEMAP_FRONT,0);
	
		if (chon)
			tilemap_draw(bitmap,cliprect,tx_tilemap,0,0);
	} };
	
	public static VideoEofHandlerPtr video_eof_blktiger  = new VideoEofHandlerPtr() { public void handler(){
		buffer_spriteram_w(0,0);
	} };
}
