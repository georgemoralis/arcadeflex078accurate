/*************************************************************************

	Sega g80 common sound hardware

*************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package sndhrdw;

public class segasnd
{
	
	/* SP0250-based speechboard */
	
	static UINT8 sega_speechboard_latch, sega_speechboard_t0, sega_speechboard_p2, sega_speechboard_drq;
	
	
	public static ReadHandlerPtr speechboard_t0_r  = new ReadHandlerPtr() { public int handler(int offset){
		return sega_speechboard_t0;
	} };
	
	public static ReadHandlerPtr speechboard_t1_r  = new ReadHandlerPtr() { public int handler(int offset){
		return sega_speechboard_drq;
	} };
	
	public static ReadHandlerPtr speechboard_p1_r  = new ReadHandlerPtr() { public int handler(int offset){
		return sega_speechboard_latch;
	} };
	
	public static ReadHandlerPtr speechboard_rom_r  = new ReadHandlerPtr() { public int handler(int offset){
		return memory_region(REGION_CPU2)[0x800 + 0x100*(sega_speechboard_p2 & 0x3f) + offset];
	} };
	
	public static WriteHandlerPtr speechboard_p1_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if(!(data & 0x80))
			sega_speechboard_t0 = 0;
	} };
	
	public static WriteHandlerPtr speechboard_p2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		sega_speechboard_p2 = data;
	} };
	
	static void speechboard_drq_w(int level)
	{
		sega_speechboard_drq = level == ASSERT_LINE;
	}
	
	public static WriteHandlerPtr sega_sh_speechboard_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		sega_speechboard_latch = data & 0x7f;
		cpu_set_irq_line(1, 0, data & 0x80 ? CLEAR_LINE : ASSERT_LINE);
		if(!(data & 0x80))
			sega_speechboard_t0 = 1;
	} };
	
	public static Memory_ReadAddress sega_speechboard_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x07ff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress sega_speechboard_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x07ff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort sega_speechboard_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00,     0xff,     speechboard_rom_r ),
		new IO_ReadPort( I8039_p1, I8039_p1, speechboard_p1_r ),
		new IO_ReadPort( I8039_t0, I8039_t0, speechboard_t0_r ),
		new IO_ReadPort( I8039_t1, I8039_t1, speechboard_t1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sega_speechboard_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00,     0xff,     sp0250_w ),
		new IO_WritePort( I8039_p1, I8039_p1, speechboard_p1_w ),
		new IO_WritePort( I8039_p2, I8039_p2, speechboard_p2_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	struct sp0250_interface sega_sp0250_interface =
	{
		100,
		speechboard_drq_w
	};
}
