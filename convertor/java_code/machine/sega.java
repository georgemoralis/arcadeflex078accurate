/*************************************************************************

	Sega vector hardware

*************************************************************************/

/*
 * History:
 *
 * 980206 Cleaned up and simplified the spinner routines. BW
 *
 */

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package machine;

public class sega
{
	
	UINT8 *sega_mem;
	
	static UINT8 mult1;
	static UINT16 result;
	static UINT8 ioSwitch; /* determines whether we're reading the spinner or the buttons */
	
	public static WriteHandlerPtr sega_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int pc,off;
	
		pc = activecpu_get_previouspc();
		off = offset;
	
		/* Check if this is a valid PC (ie not a spurious stack write) */
		if (pc != -1)
		{
			int op, page;
			unsigned int bad;
	
			op = sega_mem[pc] & 0xFF;
			if (op == 0x32)
			{
				bad = sega_mem[pc+1] & 0xFF;
				page = (sega_mem[pc+2] & 0xFF) << 8;
				(*sega_decrypt)(pc,&bad);
				off = (page & 0xFF00) | (bad & 0x00FF);
			}
		}
	
	
		/* MWA_ROM */
		if		((off>=0x0000) && (off<=0xbfff))
		{
			;
		}
		/* MWA_RAM */
		else if ((off>=0xc800) && (off<=0xefff))
		{
			sega_mem[off]=data;
		}
	} };
	
	public static InterruptHandlerPtr sega_interrupt = new InterruptHandlerPtr() {public void handler(){
		if (input_port_5_r(0) & 0x01)
			cpu_set_irq_line(0, IRQ_LINE_NMI, PULSE_LINE);
		else
			cpu_set_irq_line(0, 0, HOLD_LINE);
	} };
	
	public static WriteHandlerPtr sega_mult1_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		mult1 = data;
	} };
	
	public static WriteHandlerPtr sega_mult2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		/* Curiously, the multiply is _only_ calculated by writes to this port. */
		result = mult1 * data;
	} };
	
	public static WriteHandlerPtr sega_switch_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		ioSwitch = data;
	/*	logerror("ioSwitch: %02x\n",ioSwitch); */
	} };
	
	public static WriteHandlerPtr sega_coin_counter_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		coin_counter_w(offset,data);
	} };
	
	public static ReadHandlerPtr sega_mult_r  = new ReadHandlerPtr() { public int handler(int offset){
		int c;
	
		c = result & 0xff;
		result >>= 8;
		return (c);
	} };
	
	/***************************************************************************
	
	  The Sega games store the DIP switches in a very mangled format that's
	  not directly useable by MAME.  This function mangles the DIP switches
	  into a format that can be used.
	
	  Original format:
	  Port 0 - 2-4, 2-8, 1-4, 1-8
	  Port 1 - 2-3, 2-7, 1-3, 1-7
	  Port 2 - 2-2, 2-6, 1-2, 1-6
	  Port 3 - 2-1, 2-5, 1-1, 1-5
	  MAME format:
	  Port 6 - 1-1, 1-2, 1-3, 1-4, 1-5, 1-6, 1-7, 1-8
	  Port 7 - 2-1, 2-2, 2-3, 2-4, 2-5, 2-6, 2-7, 2-8
	***************************************************************************/
	
	public static ReadHandlerPtr sega_ports_r  = new ReadHandlerPtr() { public int handler(int offset){
		int dip1, dip2;
	
		dip1 = input_port_6_r.handler(offset);
		dip2 = input_port_7_r.handler(offset);
	
		switch(offset)
		{
			case 0:
				return ((input_port_0_r.handler(0) & 0xF0) | ((dip2 & 0x08)>>3) |
					 ((dip2 & 0x80)>>6) | ((dip1 & 0x08)>>1) | ((dip1 & 0x80)>>4));
			case 1:
				return ((input_port_1_r.handler(0) & 0xF0) | ((dip2 & 0x04)>>2) |
					 ((dip2 & 0x40)>>5) | ((dip1 & 0x04)>>0) | ((dip1 & 0x40)>>3));
			case 2:
				return ((input_port_2_r.handler(0) & 0xF0) | ((dip2 & 0x02)>>1) |
					 ((dip2 & 0x20)>>4) | ((dip1 & 0x02)<<1) | ((dip1 & 0x20)>>2));
			case 3:
				return ((input_port_3_r.handler(0) & 0xF0) | ((dip2 & 0x01)>>0) |
					 ((dip2 & 0x10)>>3) | ((dip1 & 0x01)<<2) | ((dip1 & 0x10)>>1));
		}
	
		return 0;
	} };
	
	
	public static ReadHandlerPtr sega_IN4_r  = new ReadHandlerPtr() { public int handler(int offset)
	
	/*
	 * The values returned are always increasing.  That is, regardless of whether
	 * you turn the spinner left or right, the self-test should always show the
	 * number as increasing. The direction is only reflected in the least
	 * significant bit.
	 */
	
		int delta;
		static int sign;
		static int spinner;
	
		if (ioSwitch & 1) /* ioSwitch = 0x01 or 0xff */
			return readinputport (4);
	
		/* else ioSwitch = 0xfe */
	
		/* I'm sure this can be further simplified ;-) BW */
		delta = readinputport(8);
		if (delta != 0)
		{
			sign = delta >> 7;
			if (sign)
				delta = 0x80-delta;
			spinner += delta;
		} };
		return (~((spinner<<1) | sign));
	}
	
	public static ReadHandlerPtr elim4_IN4_r  = new ReadHandlerPtr() { public int handler(int offset){
		/* If the ioPort ($f8) is 0x1f, we're reading the 4 coin inputs.    */
		/* If the ioPort ($f8) is 0x1e, we're reading player 3 & 4 controls.*/
	
		if (ioSwitch == 0x1e)
			return readinputport (4);
		if (ioSwitch == 0x1f)
			return readinputport (8);
		return (0);
	} };
	
}
