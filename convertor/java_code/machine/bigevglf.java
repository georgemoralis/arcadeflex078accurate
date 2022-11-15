/***************************************************************************

  machine.c

  Functions to emulate general aspects of the machine (RAM, ROM, interrupts,
  I/O ports)

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package machine;

public class bigevglf
{
	
	
	static unsigned char from_mcu;
	static int mcu_sent = 0,main_sent = 0;
	
	
	static unsigned char portA_in,portA_out,ddrA;
	static unsigned char portB_in,portB_out,ddrB;
	static unsigned char portC_in,portC_out,ddrC;
	
	public static ReadHandlerPtr bigevglf_68705_portA_r  = new ReadHandlerPtr() { public int handler(int offset){
		return (portA_out & ddrA) | (portA_in & ~ddrA);
	} };
	
	WRITE_HANDLER( bigevglf_68705_portA_w )
	{
		portA_out = data;
	}
	
	WRITE_HANDLER( bigevglf_68705_ddrA_w )
	{
		ddrA = data;
		
	}
	
	public static ReadHandlerPtr bigevglf_68705_portB_r  = new ReadHandlerPtr() { public int handler(int offset){
		return (portB_out & ddrB) | (portB_in & ~ddrB);
	} };
	
	WRITE_HANDLER( bigevglf_68705_portB_w )
	{
	
		if ((ddrB & 0x02) && (~portB_out & 0x02) && (data & 0x02)) /* positive going transition of the clock */
		{
			cpu_set_irq_line(3,0,CLEAR_LINE);
			main_sent = 0;
	
		}
		if ((ddrB & 0x04) && (~portB_out & 0x04) && (data & 0x04) ) /* positive going transition of the clock */
		{
			from_mcu = portA_out;
			mcu_sent = 0;
		}
	
		portB_out = data;
	}
	
	WRITE_HANDLER( bigevglf_68705_ddrB_w )
	{
		ddrB = data;
	}
	
	public static ReadHandlerPtr bigevglf_68705_portC_r  = new ReadHandlerPtr() { public int handler(int offset){
		portC_in = 0;
		if (main_sent) portC_in |= 0x01;
		if (mcu_sent)  portC_in |= 0x02;
	
		return (portC_out & ddrC) | (portC_in & ~ddrC);
	} };
	
	WRITE_HANDLER( bigevglf_68705_portC_w )
	{
		portC_out = data;
	}
	
	WRITE_HANDLER( bigevglf_68705_ddrC_w )
	{
		ddrC = data;
	}
	
	WRITE_HANDLER( bigevglf_mcu_w )
	{
		portA_in = data;
		main_sent = 1;
		cpu_set_irq_line(3,0,ASSERT_LINE);
	}
	
	
	public static ReadHandlerPtr bigevglf_mcu_r  = new ReadHandlerPtr() { public int handler(int offset){
		mcu_sent = 1;
		return from_mcu;
	} };
	
	public static ReadHandlerPtr bigevglf_mcu_status_r  = new ReadHandlerPtr() { public int handler(int offset){
		int res = 0;
	
		if (!main_sent) res |= 0x08;
		if (!mcu_sent) res |= 0x10;
	
		return res;
	} };
	
}
