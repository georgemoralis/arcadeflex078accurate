/***************************************************************************

	Atari "Stella on Steroids" hardware

****************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package vidhrdw;

public class beathead
{
	
	
	
	/*************************************
	 *
	 *	Globals we own
	 *
	 *************************************/
	
	data32_t *	beathead_vram_bulk_latch;
	data32_t *	beathead_palette_select;
	
	
	
	/*************************************
	 *
	 *	Statics
	 *
	 *************************************/
	
	static offs_t				scanline_offset[240];
	static UINT8				scanline_palette[240];
	
	static int					current_scanline;
	static data32_t				finescroll;
	static offs_t				vram_latch_offset;
	
	static offs_t				hsyncram_offset;
	static offs_t				hsyncram_start;
	static UINT8 *				hsyncram;
	
	
	
	/*************************************
	 *
	 *	Video start/stop
	 *
	 *************************************/
	
	public static VideoStartHandlerPtr video_start_beathead  = new VideoStartHandlerPtr() { public int handler(){
		hsyncram = auto_malloc(0x800);
		if (!hsyncram)
			return 1;
		return 0;
	} };
	
	
	
	/*************************************
	 *
	 *	VRAM handling
	 *
	 *************************************/
	
	WRITE32_HANDLER( beathead_vram_transparent_w )
	{
		/* writes to this area appear to handle transparency */
		if (!(data & 0x000000ff)) mem_mask |= 0x000000ff;
		if (!(data & 0x0000ff00)) mem_mask |= 0x0000ff00;
		if (!(data & 0x00ff0000)) mem_mask |= 0x00ff0000;
		if (!(data & 0xff000000)) mem_mask |= 0xff000000;
		COMBINE_DATA(&videoram32[offset]);
	}
	
	
	WRITE32_HANDLER( beathead_vram_bulk_w )
	{
		/* it appears that writes to this area pass in a mask for 4 words in VRAM */
		/* allowing them to be filled from a preset latch */
		offset &= ~3;
		data = data & ~mem_mask & 0x0f0f0f0f;
	
		/* for now, just handle the bulk fill case; the others we'll catch later */
		if (data == 0x0f0f0f0f)
			videoram32[offset+0] =
			videoram32[offset+1] =
			videoram32[offset+2] =
			videoram32[offset+3] = *beathead_vram_bulk_latch;
		else
			logerror("Detected bulk VRAM write with mask %08x\n", data);
	}
	
	
	WRITE32_HANDLER( beathead_vram_latch_w )
	{
		/* latch the address */
		vram_latch_offset = (4 * offset) & 0x7ffff;
	}
	
	
	WRITE32_HANDLER( beathead_vram_copy_w )
	{
		/* copy from VRAM to VRAM, for 1024 bytes */
		offs_t dest_offset = (4 * offset) & 0x7ffff;
		memcpy(&videoram32[dest_offset / 4], &videoram32[vram_latch_offset / 4], 0x400);
	}
	
	
	
	/*************************************
	 *
	 *	Scroll offset handling
	 *
	 *************************************/
	
	WRITE32_HANDLER( beathead_finescroll_w )
	{
		data32_t oldword = finescroll;
		data32_t newword = COMBINE_DATA(&finescroll);
	
		/* if VBLANK is going off on a non-zero scanline, suspend time */
		if ((oldword & 8) && !(newword & 8) && current_scanline != 0)
		{
			logerror("Suspending time! (scanline = %d)\n", current_scanline);
			cpu_set_halt_line(0, ASSERT_LINE);
		}
	}
	
	
	
	/*************************************
	 *
	 *	Palette handling
	 *
	 *************************************/
	
	WRITE32_HANDLER( beathead_palette_w )
	{
		int newword = COMBINE_DATA(&paletteram32[offset]);
		int r = ((newword >> 9) & 0x3e) | ((newword >> 15) & 0x01);
		int g = ((newword >> 4) & 0x3e) | ((newword >> 15) & 0x01);
		int b = ((newword << 1) & 0x3e) | ((newword >> 15) & 0x01);
	
		r = (r << 2) | (r >> 4);
		g = (g << 2) | (g >> 4);
		b = (b << 2) | (b >> 4);
	
		palette_set_color(offset, r, g, b);
	}
	
	
	
	/*************************************
	 *
	 *	HSYNC RAM handling
	 *
	 *************************************/
	
	READ32_HANDLER( beathead_hsync_ram_r )
	{
		/* offset 0 is probably write-only */
		if (offset == 0)
			logerror("%08X:Unexpected HSYNC RAM read at offset 0\n", activecpu_get_previouspc());
	
		/* offset 1 reads the data */
		else
			return hsyncram[hsyncram_offset];
	
		return 0;
	}
	
	WRITE32_HANDLER( beathead_hsync_ram_w )
	{
		/* offset 0 selects the address, and can specify the start address */
		if (offset == 0)
		{
			COMBINE_DATA(&hsyncram_offset);
			if (hsyncram_offset & 0x800)
				hsyncram_start = hsyncram_offset & 0x7ff;
		}
	
		/* offset 1 writes the data */
		else
			COMBINE_DATA(&hsyncram[hsyncram_offset]);
	}
	
	
	
	/*************************************
	 *
	 *	Scanline updater
	 *
	 *************************************/
	
	void beathead_scanline_update(int scanline)
	{
		/* remember the current scanline */
		current_scanline = scanline;
	
		/* grab the latch for the previous scanline */
		scanline--;
		if (scanline < 0 || scanline >= 240)
			return;
	
		/* cache the offset and palette for this scanline */
		scanline_offset[scanline] = (finescroll & 8) ? ~0 : vram_latch_offset + (finescroll & 3);
		scanline_palette[scanline] = *beathead_palette_select & 0x7f;
	}
	
	
	
	/*************************************
	 *
	 *	Main screen refresher
	 *
	 *************************************/
	
	public static VideoUpdateHandlerPtr video_update_beathead  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		int x, y;
	
		/* generate the final screen */
		for (y = cliprect.min_y; y <= cliprect.max_y; y++)
		{
			offs_t src = scanline_offset[y] + cliprect.min_x;
			UINT8 scanline[336];
	
			/* unswizzle the scanline first */
			for (x = cliprect.min_x; x <= cliprect.max_x; x++)
				scanline[x] = ((data8_t *)videoram32)[BYTE4_XOR_LE(src++)];
	
			/* then draw it */
			draw_scanline8(bitmap, cliprect.min_x, y, cliprect.max_x - cliprect.min_x + 1, &scanline[cliprect.min_x], Machine.pens[scanline_palette[y] * 256], -1);
		}
	} };
}
