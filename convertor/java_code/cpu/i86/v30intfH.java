#ifndef __V30INTRF_H_
#define __V30INTRF_H_

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package cpu.i86;

public class v30intfH
{
	
	
	/* Public variables */
	#define v30_ICount i86_ICount
	
	/* Public functions */
	#define v30_exit i86_exit
	#define v30_get_context i86_get_context
	#define v30_set_context i86_set_context
	#define v30_get_reg i86_get_reg
	#define v30_set_reg i86_set_reg
	#define v30_set_irq_line i86_set_irq_line
	#define v30_set_irq_callback i86_set_irq_callback
	
	#ifdef MAME_DEBUG
	#endif
	
	#endif
}
