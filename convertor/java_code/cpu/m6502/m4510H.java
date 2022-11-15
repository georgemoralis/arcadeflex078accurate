/*****************************************************************************
 *
 *	 m4510.c
 *	 Portable 4510 emulator V1.0beta
 *
 *	 Copyright (c) 2000 Peter Trauner, all rights reserved.
 *
 *	 - This source code is released as freeware for non-commercial purposes.
 *	 - You are free to use and redistribute this code in modified or
 *	   unmodified form, provided you list me in the credits.
 *	 - If you modify this source code, you must add a notice to each modified
 *	   source file that it has been changed.  If you're a nice person, you
 *	   will clearly mark each change too.  :)
 *	 - If you wish to use this for commercial purposes, please contact me at
 *	   pullmoll@t-online.de
 *	 - The author of this copywritten work reserves the right to change the
 *	   terms of its usage and license at any time, including retroactively
 *	 - This entire notice must remain in the source code.
 *
 *****************************************************************************/

#ifndef _M4510_H
#define _M4510_H

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package cpu.m6502;

public class m4510H
{
	
	#ifdef RUNTIME_LOADER
	# ifdef __cplusplus
		# else
		# endif
	#endif
	
	
	enum {
		M4510_PC=1, M4510_S, M4510_P, M4510_A, M4510_X, M4510_Y,
		M4510_Z, M4510_B, M4510_EA, M4510_ZP,
		M4510_NMI_STATE, M4510_IRQ_STATE,
		M4510_MEM_LOW,M4510_MEM_HIGH,
		M4510_MEM0, M4510_MEM1, M4510_MEM2, M4510_MEM3,
		M4510_MEM4, M4510_MEM5, M4510_MEM6, M4510_MEM7
	};
	
	#define M4510_IRQ_LINE					M6502_IRQ_LINE
	
	
	
	#ifdef MAME_DEBUG
	#endif
	
	#endif
	
	
}
