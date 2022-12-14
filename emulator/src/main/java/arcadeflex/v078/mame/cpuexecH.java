/*
 * ported to v0.78
 * 
 */
package arcadeflex.v078.mame;

//generic imports
import static arcadeflex.v078.generic.funcPtr.*;

public class cpuexecH {
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	cpuexec.h
/*TODO*///
/*TODO*///	Core multi-CPU execution engine.
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///#ifndef CPUEXEC_H
/*TODO*///#define CPUEXEC_H
/*TODO*///
/*TODO*///#include "osd_cpu.h"
/*TODO*///#include "memory.h"
/*TODO*///#include "timer.h"
/*TODO*///
/*TODO*///#ifdef __cplusplus
/*TODO*///extern "C" {
/*TODO*///#endif
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*************************************
/*TODO*/// *
/*TODO*/// *	CPU cycle timing arrays
/*TODO*/// *
/*TODO*/// *************************************/
/*TODO*///
/*TODO*///extern double cycles_to_sec[];
/*TODO*///extern double sec_to_cycles[];
/*TODO*///
/*TODO*///
/*TODO*///
   /**
     * ***********************************
     *
     * CPU description for drivers
     *
     ************************************
     */
    public static class MachineCPU {

        public MachineCPU(int cpu_type, int cpu_flags, int cpu_clock, Object memory_read, Object memory_write, Object port_read, Object port_write, InterruptHandlerPtr vblank_interrupt, int vblank_interrupts_per_frame, InterruptHandlerPtr timed_interrupt, int timed_interrupts_per_second, Object reset_param, String tag) {
            this.cpu_type = cpu_type;
            this.cpu_flags = cpu_flags;
            this.cpu_clock = cpu_clock;
            this.memory_read = memory_read;
            this.memory_write = memory_write;
            this.port_read = port_read;
            this.port_write = port_write;
            this.vblank_interrupt = vblank_interrupt;
            this.vblank_interrupts_per_frame = vblank_interrupts_per_frame;
            this.timed_interrupt = timed_interrupt;
            this.timed_interrupts_per_second = timed_interrupts_per_second;
            this.reset_param = reset_param;
            this.tag = tag;
        }

        public MachineCPU() {
            this(0, 0, 0, null, null, null, null, null, 0, null, 0, null, null);
        }

        public static MachineCPU[] create(int n) {
            MachineCPU[] a = new MachineCPU[n];
            for (int k = 0; k < n; k++) {
                a[k] = new MachineCPU();
            }
            return a;
        }
        public int cpu_type;/* index for the CPU type */
        public int cpu_flags;/* flags; see #defines below */
        public int cpu_clock;/* in Hertz */
        public Object memory_read;/* struct Memory_ReadAddress */
        public Object memory_write;/* struct Memory_WriteAddress */
        public Object port_read;
        public Object port_write;
        public InterruptHandlerPtr vblank_interrupt;/* for interrupts tied to VBLANK */
        public int vblank_interrupts_per_frame;/* usually 1 */
        public InterruptHandlerPtr timed_interrupt;/* for interrupts not tied to VBLANK */
        public int timed_interrupts_per_second;
        public Object reset_param;/* parameter for cpu_reset */
        public String tag;
    }
/*TODO*///
/*TODO*///
/*TODO*////*************************************
/*TODO*/// *
/*TODO*/// *	CPU flag constants
/*TODO*/// *
/*TODO*/// *************************************/
/*TODO*///
/*TODO*///enum
/*TODO*///{
/*TODO*///	/* set this if the CPU is used as a slave for audio. It will not be emulated if */
/*TODO*///	/* sound is disabled, therefore speeding up a lot the emulation. */
/*TODO*///	CPU_AUDIO_CPU = 0x0002,
/*TODO*///
/*TODO*///	/* the Z80 can be wired to use 16 bit addressing for I/O ports */
/*TODO*///	CPU_16BIT_PORT = 0x0001
/*TODO*///};
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*************************************
/*TODO*/// *
/*TODO*/// *	Core CPU execution
/*TODO*/// *
/*TODO*/// *************************************/
/*TODO*///
/*TODO*////* Prepare CPUs for execution */
/*TODO*///int cpu_init(void);
/*TODO*///
/*TODO*////* Run CPUs until the user quits */
/*TODO*///void cpu_run(void);
/*TODO*///
/*TODO*////* Clean up after quitting */
/*TODO*///void cpu_exit(void);
/*TODO*///
/*TODO*////* Force a reset after the current timeslice */
/*TODO*///void machine_reset(void);
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*************************************
/*TODO*/// *
/*TODO*/// *	Save/restore
/*TODO*/// *
/*TODO*/// *************************************/
/*TODO*///
/*TODO*////* Load or save the game state */
/*TODO*///enum
/*TODO*///{
/*TODO*///	LOADSAVE_NONE,
/*TODO*///	LOADSAVE_SAVE,
/*TODO*///	LOADSAVE_LOAD
/*TODO*///};
/*TODO*///void cpu_loadsave_schedule(int type, char id);
/*TODO*///void cpu_loadsave_schedule_file(int type, const char *name);
/*TODO*///void cpu_loadsave_reset(void);
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*************************************
/*TODO*/// *
/*TODO*/// *	Optional watchdog
/*TODO*/// *
/*TODO*/// *************************************/
/*TODO*///
/*TODO*////* 8-bit watchdog read/write handlers */
/*TODO*///WRITE_HANDLER( watchdog_reset_w );
/*TODO*///READ_HANDLER( watchdog_reset_r );
/*TODO*///
/*TODO*////* 16-bit watchdog read/write handlers */
/*TODO*///WRITE16_HANDLER( watchdog_reset16_w );
/*TODO*///READ16_HANDLER( watchdog_reset16_r );
/*TODO*///
/*TODO*////* 32-bit watchdog read/write handlers */
/*TODO*///WRITE32_HANDLER( watchdog_reset32_w );
/*TODO*///READ32_HANDLER( watchdog_reset32_r );
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*************************************
/*TODO*/// *
/*TODO*/// *	CPU halt/reset lines
/*TODO*/// *
/*TODO*/// *************************************/
/*TODO*///
/*TODO*////* Set the logical state (ASSERT_LINE/CLEAR_LINE) of the RESET line on a CPU */
/*TODO*///void cpunum_set_reset_line(int cpunum, int state);
/*TODO*///
/*TODO*////* Set the logical state (ASSERT_LINE/CLEAR_LINE) of the HALT line on a CPU */
/*TODO*///void cpunum_set_halt_line(int cpunum, int state);
/*TODO*///
/*TODO*////* Backwards compatibility */
/*TODO*///#define cpu_set_reset_line 		cpunum_set_reset_line
/*TODO*///#define cpu_set_halt_line 		cpunum_set_halt_line
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*************************************
/*TODO*/// *
/*TODO*/// *	CPU scheduling
/*TODO*/// *
/*TODO*/// *************************************/
/*TODO*///
/*TODO*////* Suspension reasons */
/*TODO*///enum
/*TODO*///{
/*TODO*///	SUSPEND_REASON_HALT 	= 0x0001,
/*TODO*///	SUSPEND_REASON_RESET 	= 0x0002,
/*TODO*///	SUSPEND_REASON_SPIN 	= 0x0004,
/*TODO*///	SUSPEND_REASON_TRIGGER 	= 0x0008,
/*TODO*///	SUSPEND_REASON_DISABLE 	= 0x0010,
/*TODO*///	SUSPEND_ANY_REASON 		= ~0
/*TODO*///};
/*TODO*///
/*TODO*////* Suspend the given CPU for a specific reason */
/*TODO*///void cpunum_suspend(int cpunum, int reason, int eatcycles);
/*TODO*///
/*TODO*////* Suspend the given CPU for a specific reason */
/*TODO*///void cpunum_resume(int cpunum, int reason);
/*TODO*///
/*TODO*////* Returns true if the given CPU is suspended for any of the given reasons */
/*TODO*///int cpunum_is_suspended(int cpunum, int reason);
/*TODO*///
/*TODO*////* Aborts the timeslice for the active CPU */
/*TODO*///void activecpu_abort_timeslice(void);
/*TODO*///
/*TODO*////* Returns the current local time for a CPU, relative to the current timeslice */
/*TODO*///double cpunum_get_localtime(int cpunum);
/*TODO*///
/*TODO*////* Returns the current scaling factor for a CPU's clock speed */
/*TODO*///double cpunum_get_clockscale(int cpunum);
/*TODO*///
/*TODO*////* Sets the current scaling factor for a CPU's clock speed */
/*TODO*///void cpunum_set_clockscale(int cpunum, double clockscale);
/*TODO*///
/*TODO*////* Temporarily boosts the interleave factor */
/*TODO*///void cpu_boost_interleave(double timeslice_time, double boost_duration);
/*TODO*///
/*TODO*////* Backwards compatibility */
/*TODO*///#define timer_suspendcpu(cpunum, suspend, reason)	do { if (suspend) cpunum_suspend(cpunum, reason, 1); else cpunum_resume(cpunum, reason); } while (0)
/*TODO*///#define timer_holdcpu(cpunum, suspend, reason)		do { if (suspend) cpunum_suspend(cpunum, reason, 0); else cpunum_resume(cpunum, reason); } while (0)
/*TODO*///#define cpu_getstatus(cpunum)						(!cpunum_is_suspended(cpunum, SUSPEND_REASON_HALT | SUSPEND_REASON_RESET | SUSPEND_REASON_DISABLE))
/*TODO*///#define timer_get_overclock(cpunum)					cpunum_get_clockscale(cpunum)
/*TODO*///#define timer_set_overclock(cpunum, overclock)		cpunum_set_clockscale(cpunum, overclock)
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*************************************
/*TODO*/// *
/*TODO*/// *	Timing helpers
/*TODO*/// *
/*TODO*/// *************************************/
/*TODO*///
/*TODO*////* Returns the number of cycles run so far this timeslice */
/*TODO*///int cycles_currently_ran(void);
/*TODO*///
/*TODO*////* Returns the number of cycles left to run in this timeslice */
/*TODO*///int cycles_left_to_run(void);
/*TODO*///
/*TODO*////* Returns the total number of CPU cycles */
/*TODO*///UINT32 activecpu_gettotalcycles(void);
/*TODO*///UINT64 activecpu_gettotalcycles64(void);
/*TODO*///
/*TODO*////* Returns the total number of CPU cycles for a given CPU */
/*TODO*///UINT32 cpunum_gettotalcycles(int cpunum);
/*TODO*///UINT64 cpunum_gettotalcycles64(int cpunum);
/*TODO*///
/*TODO*////* Returns the number of CPU cycles before the next interrupt handler call */
/*TODO*///int activecpu_geticount(void);
/*TODO*///
/*TODO*////* Safely eats cycles so we don't cross a timeslice boundary */
/*TODO*///void activecpu_eat_cycles(int cycles);
/*TODO*///
/*TODO*////* Scales a given value by the ratio of fcount / fperiod */
/*TODO*///int cpu_scalebyfcount(int value);
/*TODO*///
/*TODO*////* Backwards compatibility */
/*TODO*///#define cpu_gettotalcycles cpunum_gettotalcycles
/*TODO*///#define cpu_gettotalcycles64 cpunum_gettotalcycles64
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*************************************
/*TODO*/// *
/*TODO*/// *	Video timing
/*TODO*/// *
/*TODO*/// *************************************/
/*TODO*///
/*TODO*////* Initialize the refresh timer */
/*TODO*///void cpu_init_refresh_timer(void);
/*TODO*///
/*TODO*////* Recomputes the scanling timing after, e.g., a visible area change */
/*TODO*///void cpu_compute_scanline_timing(void);
/*TODO*///
/*TODO*////* Returns the number of the video frame we are currently playing */
/*TODO*///int cpu_getcurrentframe(void);
/*TODO*///
/*TODO*////* Returns the current scanline number */
/*TODO*///int cpu_getscanline(void);
/*TODO*///
/*TODO*////* Returns the amount of time until a given scanline */
/*TODO*///double cpu_getscanlinetime(int scanline);
/*TODO*///
/*TODO*////* Returns the duration of a single scanline */
/*TODO*///double cpu_getscanlineperiod(void);
/*TODO*///
/*TODO*////* Returns the current horizontal beam position in pixels */
/*TODO*///int cpu_gethorzbeampos(void);
/*TODO*///
/*TODO*////* Returns the current VBLANK state */
/*TODO*///int cpu_getvblank(void);
/*TODO*///
/*TODO*////* Returns the number of the video frame we are currently playing */
/*TODO*///int cpu_getcurrentframe(void);
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*************************************
/*TODO*/// *
/*TODO*/// *	Synchronization
/*TODO*/// *
/*TODO*/// *************************************/
/*TODO*///
/*TODO*////* generate a trigger now */
/*TODO*///void cpu_trigger(int trigger);
/*TODO*///
/*TODO*////* generate a trigger after a specific period of time */
/*TODO*///void cpu_triggertime(double duration, int trigger);
/*TODO*///
/*TODO*////* generate a trigger corresponding to an interrupt on the given CPU */
/*TODO*///void cpu_triggerint(int cpunum);
/*TODO*///
/*TODO*////* burn CPU cycles until a timer trigger */
/*TODO*///void cpu_spinuntil_trigger(int trigger);
/*TODO*///
/*TODO*////* yield our timeslice until a timer trigger */
/*TODO*///void cpu_yielduntil_trigger(int trigger);
/*TODO*///
/*TODO*////* burn CPU cycles until the next interrupt */
/*TODO*///void cpu_spinuntil_int(void);
/*TODO*///
/*TODO*////* yield our timeslice until the next interrupt */
/*TODO*///void cpu_yielduntil_int(void);
/*TODO*///
/*TODO*////* burn CPU cycles until our timeslice is up */
/*TODO*///void cpu_spin(void);
/*TODO*///
/*TODO*////* yield our current timeslice */
/*TODO*///void cpu_yield(void);
/*TODO*///
/*TODO*////* burn CPU cycles for a specific period of time */
/*TODO*///void cpu_spinuntil_time(double duration);
/*TODO*///
/*TODO*////* yield our timeslice for a specific period of time */
/*TODO*///void cpu_yielduntil_time(double duration);
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*************************************
/*TODO*/// *
/*TODO*/// *	Core timing
/*TODO*/// *
/*TODO*/// *************************************/
/*TODO*///
/*TODO*////* Returns the number of times the interrupt handler will be called before
/*TODO*///   the end of the current video frame. This is can be useful to interrupt
/*TODO*///   handlers to synchronize their operation. If you call this from outside
/*TODO*///   an interrupt handler, add 1 to the result, i.e. if it returns 0, it means
/*TODO*///   that the interrupt handler will be called once. */
/*TODO*///int cpu_getiloops(void);
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*************************************
/*TODO*/// *
/*TODO*/// *	Z80 daisy chain
/*TODO*/// *
/*TODO*/// *************************************/
/*TODO*///
/*TODO*////* fix me - where should this stuff go? */
/*TODO*///
/*TODO*////* daisy-chain link */
/*TODO*///typedef struct
/*TODO*///{
/*TODO*///	void (*reset)(int); 			/* reset callback	  */
/*TODO*///	int  (*interrupt_entry)(int);	/* entry callback	  */
/*TODO*///	void (*interrupt_reti)(int);	/* reti callback	  */
/*TODO*///	int irq_param;					/* callback paramater */
/*TODO*///} Z80_DaisyChain;
/*TODO*///
/*TODO*///#define Z80_MAXDAISY	4		/* maximum of daisy chan device */
/*TODO*///
/*TODO*///#define Z80_INT_REQ 	0x01	/* interrupt request mask		*/
/*TODO*///#define Z80_INT_IEO 	0x02	/* interrupt disable mask(IEO)	*/
/*TODO*///
/*TODO*///#define Z80_VECTOR(device,state) (((device)<<8)|(state))
/*TODO*///
/*TODO*///
/*TODO*///#ifdef __cplusplus
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///#endif	/* CPUEXEC_H */
/*TODO*///    
}
