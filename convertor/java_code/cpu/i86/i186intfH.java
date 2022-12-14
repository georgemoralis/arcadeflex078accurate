/* ASG 971222 -- rewrote this interface */
#ifndef __I186INTR_H_
#define __I186INTR_H_

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package cpu.i86;

public class i186intfH
{
	
	
	/* Public variables */
	#define i186_ICount i86_ICount
	
	/* Public functions */
	#define i186_init i86_init
	#define i186_reset i86_reset
	#define i186_exit i86_exit
	#define i186_get_context i86_get_context
	#define i186_set_context i86_set_context
	#define i186_get_reg i86_get_reg
	#define i186_set_reg i86_set_reg
	#define i186_set_irq_line i86_set_irq_line
	#define i186_set_irq_callback i86_set_irq_callback
	
	#ifdef MAME_DEBUG
	#endif
	
	#endif
}
