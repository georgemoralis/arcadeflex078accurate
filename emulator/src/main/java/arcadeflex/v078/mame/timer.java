/**
 * ported to v0.78
 */
package arcadeflex.v078.mame;

import static arcadeflex.v078.generic.funcPtr.*;
//mame imports
import static arcadeflex.v078.mame.driverH.*;

public class timer {

    /*TODO*////***************************************************************************
/*TODO*///
/*TODO*///  timer.c
/*TODO*///
/*TODO*///  Functions needed to generate timing and synchronization between several
/*TODO*///  CPUs.
/*TODO*///
/*TODO*///  Changes 2/27/99:
/*TODO*///  	- added some rounding to the sorting of timers so that two timers
/*TODO*///  		allocated to go off at the same time will go off in the order
/*TODO*///  		they were allocated, without concern for floating point rounding
/*TODO*///  		errors (thanks Juergen!)
/*TODO*///  	- fixed a bug where the base_time was not updated when a CPU was
/*TODO*///  		suspended, making subsequent calls to get_relative_time() return an
/*TODO*///  		incorrect time (thanks Nicola!)
/*TODO*///  	- changed suspended CPUs so that they don't eat their timeslice until
/*TODO*///  		all other CPUs have used up theirs; this allows a slave CPU to
/*TODO*///  		trigger a higher priority CPU in the middle of the timeslice
/*TODO*///  	- added the ability to call timer_reset() on a oneshot or pulse timer
/*TODO*///  		from within that timer's callback; in this case, the timer won't
/*TODO*///  		get removed (oneshot) or won't get reprimed (pulse)
/*TODO*///
/*TODO*///  Changes 12/17/99 (HJB):
/*TODO*///	- added overclocking factor and functions to set/get it at runtime.
/*TODO*///
/*TODO*///  Changes 12/23/99 (HJB):
/*TODO*///	- added burn() function pointer to tell CPU cores when we want to
/*TODO*///	  burn cycles, because the cores might need to adjust internal
/*TODO*///	  counters or timers.
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///#include "cpuintrf.h"
/*TODO*///#include "driver.h"
/*TODO*///#include "timer.h"
/*TODO*///
/*TODO*///
/*TODO*///#define MAX_TIMERS 256
/*TODO*///
/*TODO*///
/*TODO*///#define VERBOSE 0
/*TODO*///
/*TODO*///#if VERBOSE
/*TODO*///#define LOG(x)	logerror x
/*TODO*///#else
/*TODO*///#define LOG(x)
/*TODO*///#endif
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	internal timer structure
/*TODO*///-------------------------------------------------*/
/*TODO*///
    public static class mame_timer {
        /*TODO*///	struct _mame_timer *next;
/*TODO*///	struct _mame_timer *prev;
/*TODO*///	void (*callback)(int);
/*TODO*///	int callback_param;
/*TODO*///	int tag;
/*TODO*///	UINT8 enabled;
/*TODO*///	UINT8 temporary;
/*TODO*///	double period;
/*TODO*///	double start;
/*TODO*///	double expire;
    }

    /*-------------------------------------------------
	global variables
    -------------------------------------------------*/

 /* conversion constants */
    public static double[] cycles_to_sec = new double[MAX_CPU];
    public static double[] sec_to_cycles = new double[MAX_CPU];

    /*TODO*///
/*TODO*////* list of active timers */
/*TODO*///static mame_timer timers[MAX_TIMERS];
/*TODO*///static mame_timer *timer_head;
/*TODO*///static mame_timer *timer_free_head;
/*TODO*///static mame_timer *timer_free_tail;
/*TODO*///
/*TODO*////* other internal states */
/*TODO*///static double global_offset;
/*TODO*///static mame_timer *callback_timer;
/*TODO*///static int callback_timer_modified;
/*TODO*///static double callback_timer_expire_time;
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	get_relative_time - return the current time
/*TODO*///	relative to the global_offset
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///INLINE double get_relative_time(void)
/*TODO*///{
/*TODO*///	int activecpu;
/*TODO*///
/*TODO*///	/* if we're executing as a particular CPU, use its local time as a base */
/*TODO*///	activecpu = cpu_getactivecpu();
/*TODO*///	if (activecpu >= 0)
/*TODO*///		return cpunum_get_localtime(activecpu);
/*TODO*///	
/*TODO*///	/* if we're currently in a callback, use the timer's expiration time as a base */
/*TODO*///	if (callback_timer)
/*TODO*///		return callback_timer_expire_time;
/*TODO*///	
/*TODO*///	/* otherwise, return 0 */
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_new - allocate a new timer
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///INLINE mame_timer *timer_new(void)
/*TODO*///{
/*TODO*///	mame_timer *timer;
/*TODO*///
/*TODO*///	/* remove an empty entry */
/*TODO*///	if (!timer_free_head)
/*TODO*///		return NULL;
/*TODO*///	timer = timer_free_head;
/*TODO*///	timer_free_head = timer->next;
/*TODO*///	if (!timer_free_head)
/*TODO*///		timer_free_tail = NULL;
/*TODO*///
/*TODO*///	return timer;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_list_insert - insert a new timer into
/*TODO*///	the list at the appropriate location
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///INLINE void timer_list_insert(mame_timer *timer)
/*TODO*///{
/*TODO*///	double expire = timer->enabled ? timer->expire : TIME_NEVER;
/*TODO*///	mame_timer *t, *lt = NULL;
/*TODO*///
/*TODO*///	/* sanity checks for the debug build */
/*TODO*///	#ifdef MAME_DEBUG
/*TODO*///	{
/*TODO*///		int tnum = 0;
/*TODO*///
/*TODO*///		/* loop over the timer list */
/*TODO*///		for (t = timer_head; t; t = t->next, tnum++)
/*TODO*///		{
/*TODO*///			if (t == timer)
/*TODO*///				printf("This timer is already inserted in the list!\n");
/*TODO*///			if (tnum == MAX_TIMERS-1)
/*TODO*///				printf("Timer list is full!\n");
/*TODO*///		}
/*TODO*///	}
/*TODO*///	#endif
/*TODO*///
/*TODO*///	/* loop over the timer list */
/*TODO*///	for (t = timer_head; t; lt = t, t = t->next)
/*TODO*///	{
/*TODO*///		/* if the current list entry expires after us, we should be inserted before it */
/*TODO*///		/* note that due to floating point rounding, we need to allow a bit of slop here */
/*TODO*///		/* because two equal entries -- within rounding precision -- need to sort in */
/*TODO*///		/* the order they were inserted into the list */
/*TODO*///		if ((t->expire - expire) > TIME_IN_NSEC(1))
/*TODO*///		{
/*TODO*///			/* link the new guy in before the current list entry */
/*TODO*///			timer->prev = t->prev;
/*TODO*///			timer->next = t;
/*TODO*///
/*TODO*///			if (t->prev)
/*TODO*///				t->prev->next = timer;
/*TODO*///			else
/*TODO*///				timer_head = timer;
/*TODO*///			t->prev = timer;
/*TODO*///			return;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* need to insert after the last one */
/*TODO*///	if (lt)
/*TODO*///		lt->next = timer;
/*TODO*///	else
/*TODO*///		timer_head = timer;
/*TODO*///	timer->prev = lt;
/*TODO*///	timer->next = NULL;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_list_remove - remove a timer from the
/*TODO*///	linked list
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///INLINE void timer_list_remove(mame_timer *timer)
/*TODO*///{
/*TODO*///	/* sanity checks for the debug build */
/*TODO*///	#ifdef MAME_DEBUG
/*TODO*///	{
/*TODO*///		mame_timer *t;
/*TODO*///		int tnum = 0;
/*TODO*///
/*TODO*///		/* loop over the timer list */
/*TODO*///		for (t = timer_head; t && t != timer; t = t->next, tnum++) ;
/*TODO*///		if (t == NULL)
/*TODO*///			printf ("timer not found in list");
/*TODO*///	}
/*TODO*///	#endif
/*TODO*///
/*TODO*///	/* remove it from the list */
/*TODO*///	if (timer->prev)
/*TODO*///		timer->prev->next = timer->next;
/*TODO*///	else
/*TODO*///		timer_head = timer->next;
/*TODO*///	if (timer->next)
/*TODO*///		timer->next->prev = timer->prev;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_init - initialize the timer system
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void timer_init(void)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///
/*TODO*///	/* we need to wait until the first call to timer_cyclestorun before using real CPU times */
/*TODO*///	global_offset = 0.0;
/*TODO*///	callback_timer = NULL;
/*TODO*///	callback_timer_modified = 0;
/*TODO*///
/*TODO*///	/* reset the timers */
/*TODO*///	memset(timers, 0, sizeof(timers));
/*TODO*///
/*TODO*///	/* initialize the lists */
/*TODO*///	timer_head = NULL;
/*TODO*///	timer_free_head = &timers[0];
/*TODO*///	for (i = 0; i < MAX_TIMERS-1; i++)
/*TODO*///	{
/*TODO*///		timers[i].tag = -1;
/*TODO*///		timers[i].next = &timers[i+1];
/*TODO*///	}
/*TODO*///	timers[MAX_TIMERS-1].next = NULL;
/*TODO*///	timer_free_tail = &timers[MAX_TIMERS-1];
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_free - remove all timers on the current
/*TODO*///	resource tag
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void timer_free(void)
/*TODO*///{
/*TODO*///	int tag = get_resource_tag();
/*TODO*///	mame_timer *timer, *next;
/*TODO*///
/*TODO*///	/* scan the list */
/*TODO*///	for (timer = timer_head; timer != NULL; timer = next)
/*TODO*///	{
/*TODO*///		/* prefetch the next timer in case we remove this one */
/*TODO*///		next = timer->next;
/*TODO*///
/*TODO*///		/* if this tag matches, remove it */
/*TODO*///		if (timer->tag == tag)
/*TODO*///			timer_remove(timer);
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_time_until_next_timer - return the
/*TODO*///	amount of time until the next timer fires
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///double timer_time_until_next_timer(void)
/*TODO*///{
/*TODO*///	double time = get_relative_time();
/*TODO*///	return timer_head->expire - time;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_adjust_global_time - adjust the global
/*TODO*///	time; this is also where we fire the timers
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void timer_adjust_global_time(double delta)
/*TODO*///{
/*TODO*///	mame_timer *timer;
/*TODO*///
/*TODO*///	/* add the delta to the global offset */
/*TODO*///	global_offset += delta;
/*TODO*///
/*TODO*///	/* scan the list and adjust the times */
/*TODO*///	for (timer = timer_head; timer != NULL; timer = timer->next)
/*TODO*///	{
/*TODO*///		timer->start -= delta;
/*TODO*///		timer->expire -= delta;
/*TODO*///	}
/*TODO*///
/*TODO*///	LOG(("timer_adjust_global_time: delta=%.9f head->expire=%.9f\n", delta, timer_head->expire));
/*TODO*///
/*TODO*///	/* now process any timers that are overdue */
/*TODO*///	while (timer_head->expire < TIME_IN_NSEC(1))
/*TODO*///	{
/*TODO*///		int was_enabled = timer_head->enabled;
/*TODO*///
/*TODO*///		/* if this is a one-shot timer, disable it now */
/*TODO*///		timer = timer_head;
/*TODO*///		if (timer->period == 0)
/*TODO*///			timer->enabled = 0;
/*TODO*///
/*TODO*///		/* set the global state of which callback we're in */
/*TODO*///		callback_timer_modified = 0;
/*TODO*///		callback_timer = timer;
/*TODO*///		callback_timer_expire_time = timer->expire;
/*TODO*///
/*TODO*///		/* call the callback */
/*TODO*///		if (was_enabled && timer->callback)
/*TODO*///		{
/*TODO*///			LOG(("Timer %08X fired (expire=%.9f)\n", (UINT32)timer, timer->expire));
/*TODO*///			profiler_mark(PROFILER_TIMER_CALLBACK);
/*TODO*///			(*timer->callback)(timer->callback_param);
/*TODO*///			profiler_mark(PROFILER_END);
/*TODO*///		}
/*TODO*///
/*TODO*///		/* clear the callback timer global */
/*TODO*///		callback_timer = NULL;
/*TODO*///
/*TODO*///		/* reset or remove the timer, but only if it wasn't modified during the callback */
/*TODO*///		if (!callback_timer_modified)
/*TODO*///		{
/*TODO*///			/* if the timer is temporary, remove it now */
/*TODO*///			if (timer->temporary)
/*TODO*///				timer_remove(timer);
/*TODO*///
/*TODO*///			/* otherwise, reschedule it */
/*TODO*///			else
/*TODO*///			{
/*TODO*///				timer->start = timer->expire;
/*TODO*///				timer->expire += timer->period;
/*TODO*///
/*TODO*///				timer_list_remove(timer);
/*TODO*///				timer_list_insert(timer);
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_alloc - allocate a permament timer that
/*TODO*///	isn't primed yet
/*TODO*///-------------------------------------------------*/
/*TODO*///
    public static mame_timer timer_alloc(TimerCallbackHandlerPtr callback) {
        throw new UnsupportedOperationException("Unsupported");
        /*TODO*///	double time = get_relative_time();
/*TODO*///	mame_timer *timer = timer_new();
/*TODO*///
/*TODO*///	/* fail if we can't allocate a new entry */
/*TODO*///	if (!timer)
/*TODO*///		return NULL;
/*TODO*///
/*TODO*///	/* fill in the record */
/*TODO*///	timer->callback = callback;
/*TODO*///	timer->callback_param = 0;
/*TODO*///	timer->enabled = 0;
/*TODO*///	timer->temporary = 0;
/*TODO*///	timer->tag = get_resource_tag();
/*TODO*///	timer->period = 0;
/*TODO*///
/*TODO*///	/* compute the time of the next firing and insert into the list */
/*TODO*///	timer->start = time;
/*TODO*///	timer->expire = TIME_NEVER;
/*TODO*///	timer_list_insert(timer);
/*TODO*///
/*TODO*///	/* return a handle */
/*TODO*///	return timer;
    }

    /*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_adjust - adjust the time when this
/*TODO*///	timer will fire, and whether or not it will
/*TODO*///	fire periodically
/*TODO*///-------------------------------------------------*/
/*TODO*///
    public static void timer_adjust(mame_timer which, double duration, int param, double period) {
        throw new UnsupportedOperationException("Unsupported");
        /*TODO*///	double time = get_relative_time();
/*TODO*///
/*TODO*///	/* if this is the callback timer, mark it modified */
/*TODO*///	if (which == callback_timer)
/*TODO*///		callback_timer_modified = 1;
/*TODO*///
/*TODO*///	/* compute the time of the next firing and insert into the list */
/*TODO*///	which->callback_param = param;
/*TODO*///	which->enabled = 1;
/*TODO*///
/*TODO*///	/* set the start and expire times */
/*TODO*///	which->start = time;
/*TODO*///	which->expire = time + duration;
/*TODO*///	which->period = period;
/*TODO*///
/*TODO*///	/* remove and re-insert the timer in its new order */
/*TODO*///	timer_list_remove(which);
/*TODO*///	timer_list_insert(which);
/*TODO*///
/*TODO*///	/* if this was inserted as the head, abort the current timeslice and resync */
/*TODO*///LOG(("timer_adjust %08X to expire @ %.9f\n", (UINT32)which, which->expire));
/*TODO*///	if (which == timer_head && cpu_getexecutingcpu() >= 0)
/*TODO*///		activecpu_abort_timeslice();
    }

    /*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_pulse - allocate a pulse timer, which
/*TODO*///	repeatedly calls the callback using the given
/*TODO*///	period
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void timer_pulse(double period, int param, void (*callback)(int))
/*TODO*///{
/*TODO*///	mame_timer *timer = timer_alloc(callback);
/*TODO*///
/*TODO*///	/* fail if we can't allocate */
/*TODO*///	if (!timer)
/*TODO*///		return;
/*TODO*///
/*TODO*///	/* adjust to our liking */
/*TODO*///	timer_adjust(timer, period, param, period);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_set - allocate a one-shot timer, which
/*TODO*///	calls the callback after the given duration
/*TODO*///-------------------------------------------------*/
/*TODO*///
    public static void timer_set(double duration, int param, TimerCallbackHandlerPtr callback) {
        /*TODO*///	mame_timer *timer = timer_alloc(callback);
/*TODO*///
/*TODO*///	/* fail if we can't allocate */
/*TODO*///	if (!timer)
/*TODO*///		return;
/*TODO*///
/*TODO*///	/* mark the timer temporary */
/*TODO*///	timer->temporary = 1;
/*TODO*///
/*TODO*///	/* adjust to our liking */
/*TODO*///	timer_adjust(timer, duration, param, 0);
    }

    /*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_reset - reset the timing on a timer
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void timer_reset(mame_timer *which, double duration)
/*TODO*///{
/*TODO*///	/* adjust the timer */
/*TODO*///	timer_adjust(which, duration, which->callback_param, which->period);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_remove - remove a timer from the system
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void timer_remove(mame_timer *which)
/*TODO*///{
/*TODO*///	/* error if this is an inactive timer */
/*TODO*///	if (which->tag == -1)
/*TODO*///	{
/*TODO*///		logerror("timer_remove: removed an inactive timer!\n");
/*TODO*///		return;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* remove it from the list */
/*TODO*///	timer_list_remove(which);
/*TODO*///
/*TODO*///	/* mark it as dead */
/*TODO*///	which->tag = -1;
/*TODO*///
/*TODO*///	/* free it up by adding it back to the free list */
/*TODO*///	if (timer_free_tail)
/*TODO*///		timer_free_tail->next = which;
/*TODO*///	else
/*TODO*///		timer_free_head = which;
/*TODO*///	which->next = NULL;
/*TODO*///	timer_free_tail = which;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_enable - enable/disable a timer
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///int timer_enable(mame_timer *which, int enable)
/*TODO*///{
/*TODO*///	int old;
/*TODO*///
/*TODO*///	/* set the enable flag */
/*TODO*///	old = which->enabled;
/*TODO*///	which->enabled = enable;
/*TODO*///
/*TODO*///	/* remove the timer and insert back into the list */
/*TODO*///	timer_list_remove(which);
/*TODO*///	timer_list_insert(which);
/*TODO*///
/*TODO*///	return old;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_timeelapsed - return the time since the
/*TODO*///	last trigger
/*TODO*///-------------------------------------------------*/
/*TODO*///
    public static double timer_timeelapsed(mame_timer which) {
        throw new UnsupportedOperationException("Unsupported");
        /*TODO*///	double time = get_relative_time();
/*TODO*///	return time - which->start;
    }
    /*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_timeleft - return the time until the
/*TODO*///	next trigger
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///double timer_timeleft(mame_timer *which)
/*TODO*///{
/*TODO*///	double time = get_relative_time();
/*TODO*///	return which->expire - time;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_get_time - return the current time
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///double timer_get_time(void)
/*TODO*///{
/*TODO*///	return global_offset + get_relative_time();
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_starttime - return the time when this
/*TODO*///	timer started counting
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///double timer_starttime(mame_timer *which)
/*TODO*///{
/*TODO*///	return global_offset + which->start;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	timer_firetime - return the time when this
/*TODO*///	timer will fire next
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///double timer_firetime(mame_timer *which)
/*TODO*///{
/*TODO*///	return global_offset + which->expire;
/*TODO*///}
/*TODO*///
}
