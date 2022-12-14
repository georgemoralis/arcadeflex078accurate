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

public class docastle
{
	
	
	
	static unsigned char buffer0[9],buffer1[9];
	
	
	
	public static ReadHandlerPtr docastle_shared0_r  = new ReadHandlerPtr() { public int handler(int offset){
		if (offset == 8) logerror("CPU #0 shared0r  clock = %d\n",activecpu_gettotalcycles());
	
		/* this shouldn't be done, however it's the only way I've found */
		/* to make dip switches work in Do Run Run. */
		if (offset == 8)
		{
			cpu_set_irq_line(1,IRQ_LINE_NMI,PULSE_LINE);
			cpu_spinuntil_trigger(500);
		}
	
		return buffer0[offset];
	} };
	
	
	public static ReadHandlerPtr docastle_shared1_r  = new ReadHandlerPtr() { public int handler(int offset){
		if (offset == 8) logerror("CPU #1 shared1r  clock = %d\n",activecpu_gettotalcycles());
		return buffer1[offset];
	} };
	
	
	public static WriteHandlerPtr docastle_shared0_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (offset == 8) logerror("CPU #1 shared0w %02x %02x %02x %02x %02x %02x %02x %02x %02x clock = %d\n",
			buffer0[0],buffer0[1],buffer0[2],buffer0[3],buffer0[4],buffer0[5],buffer0[6],buffer0[7],data,activecpu_gettotalcycles());
	
		buffer0[offset] = data;
	
		if (offset == 8)
			/* awake the master CPU */
			cpu_trigger(500);
	} };
	
	
	public static WriteHandlerPtr docastle_shared1_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		buffer1[offset] = data;
	
		if (offset == 8)
		{
			logerror("CPU #0 shared1w %02x %02x %02x %02x %02x %02x %02x %02x %02x clock = %d\n",
					buffer1[0],buffer1[1],buffer1[2],buffer1[3],buffer1[4],buffer1[5],buffer1[6],buffer1[7],data,activecpu_gettotalcycles());
	
			/* freeze execution of the master CPU until the slave has used the shared memory */
			cpu_spinuntil_trigger(500);
		}
	} };
	
	
	
	public static WriteHandlerPtr docastle_nmitrigger_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		cpu_set_irq_line(1,IRQ_LINE_NMI,PULSE_LINE);
	} };
}
