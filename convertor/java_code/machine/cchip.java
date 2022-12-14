/***************************************************************************

   cchip.c

This file contains routines to interface with the Taito Controller Chip
(or "Command Chip") version 1. It's currently used by Superman and Mega
Blast. [Further cchip emulation is in machine/rainbow.c, machine/volfied.c,
drivers/opwolf.c]

According to Richard Bush, the C-Chip is an encrypted Z80 which communicates
with the main board as a protection feature.


Superman (revised SJ 060601)
--------

In Superman, the C-chip's main purpose is to handle player inputs and
coins and pass commands along to the sound chip.

The 68k queries the c-chip, which passes back $100 bytes of 68k code which
are then executed in RAM. To get around this, we hack in our own code to
communicate with the sound board, since we are familiar with the interface
as it's used in Rastan and Super Space Invaders '91.

It is believed that the NOPs in the 68k code are there to supply the
necessary cycles to the cchip to switch banks.

This code requires that the player & coin inputs be in input ports 4-6.


Mega Blast
----------

C-Chip simply used as RAM, the game doesn't even bother to change banks.
It does read the chip id though. The dump is confirmed to be from an
original board.

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package machine;

public class cchip
{
	
	static int current_bank = 0;
	
	static UINT8 cc_port = 0;
	
	/* This code for sound communication is a hack, it will not be
	   identical to the code derived from the real c-chip */
	
	static UINT8 superman_code[40] =
	{
		0x48, 0xe7, 0x80, 0x80,	            /* MOVEM.L  D0/A0,-(A7)   ( Preserve Regs ) */
		0x20, 0x6d, 0x1c, 0x40,             /* MOVEA.L  ($1C40,A5),A0 ( Load sound pointer in A0 ) */
		0x30, 0x2f, 0x00, 0x0c,             /* MOVE.W   ($0C,A7),D0   ( Fetch sound number ) */
		0x10, 0x80,                         /* MOVE.B   D0,(A0)       ( Store it on sound pointer ) */
		0x52, 0x88,                         /* ADDQ.W   #1,A0         ( Increment sound pointer ) */
		0x20, 0x3c, 0x00, 0xf0, 0x1c, 0x40, /* MOVE.L   #$F01C40,D0   ( Load top of buffer in D0 ) */
		0xb1, 0xc0,                         /* CMPA.L   D0,A0         ( Are we there yet? ) */
		0x66, 0x04,                         /* BNE.S    *+$6          ( No, we arent, skip next line ) */
		0x41, 0xed, 0x1c, 0x20,             /* LEA      ($1C20,A5),A0 ( Point to the start of the buffer ) */
		0x2b, 0x48, 0x1c, 0x40,	            /* MOVE.L   A0,($1C40,A5) ( Store new sound pointer ) */
		0x4c, 0xdf, 0x01, 0x01,	            /* MOVEM.L  (A7)+, D0/A0  ( Restore Regs ) */
		0x4e, 0x75                          /* RTS                    ( Return ) */
	};
	
	public static MachineInitHandlerPtr machine_init_cchip1  = new MachineInitHandlerPtr() { public void handler(){
		state_save_register_int  ("cchip1", 0, "current_bank", &current_bank);
		state_save_register_UINT8("cchip1", 0, "cc_port",      &cc_port, 1);
	} };
	
	WRITE16_HANDLER( cchip1_word_w )
	{
		if (offset == 0x600)
		{
			current_bank = data;
		}
		else if (current_bank == 0 && offset == 0x003)
		{
			cc_port = data;
	
			coin_lockout_w(1, data & 0x08);
			coin_lockout_w(0, data & 0x04);
			coin_counter_w(1, data & 0x02);
			coin_counter_w(0, data & 0x01);
		}
		else
		{
	logerror("cchip1_w pc: %06x bank %02x offset %04x: %02x\n",activecpu_get_pc(),current_bank,offset,data);
		}
	}
	
	READ16_HANDLER( cchip1_word_r )
	{
		/* C-Chip ID */
	
		if (offset == 0x401)
		{
			return 0x01;
		}
	
		/* Check for input ports */
	
		if (current_bank == 0)
		{
			switch (offset)
			{
				case 0x000: return readinputport(4);
				case 0x001: return readinputport(5);
				case 0x002: return readinputport(6);
				case 0x003: return cc_port;
			}
		}
	
		/* Other non-standard offsets */
	
		if (current_bank == 1 && offset <= 0xff)
		{
			if (offset < 40)	/* our hack code is only 40 bytes long */
				return superman_code[offset];
			else	/* so pad with zeros */
				return 0;
		}
	
		if (current_bank == 2)
		{
			switch (offset)
			{
				case 0x000: return 0x47;
				case 0x001: return 0x57;
				case 0x002: return 0x4b;
			}
		}
	
	logerror("cchip1_r bank: %02x offset: %04x\n",current_bank,offset);
		return 0;
	}
	
	
	/* Mega Blast */
	
	data16_t *cchip_ram;
	
	WRITE16_HANDLER( cchip2_word_w )
	{
	    logerror("cchip2_w pc: %06x offset %04x: %02x\n", activecpu_get_pc(), offset, data);
	
	    COMBINE_DATA(&cchip_ram[offset]);
	}
	
	READ16_HANDLER( cchip2_word_r )
	{
		/* C-Chip ID */
	
		if (offset == 0x401)
		{
			return 0x01;
		}
	
		logerror("cchip2_r offset: %04x\n", offset);
	
		return cchip_ram[offset];
	}
	
}
