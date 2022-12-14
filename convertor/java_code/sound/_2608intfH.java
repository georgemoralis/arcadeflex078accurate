#ifndef __2608INTF_H__
#define __2608INTF_H__

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package sound;

public class _2608intfH
{
	#ifdef BUILD_YM2608
	  void YM2608UpdateRequest(int chip);
	#endif
	
	#define   MAX_2608    (2)
	
	#ifndef VOL_YM3012
	#define YM3012_VOL(LVol,LPan,RVol,RPan) (MIXER(LVol,LPan)|(MIXER(RVol,RPan) << 16))
	#endif
	
	struct YM2608interface{
		int num;	/* total number of 8910 in the machine */
		int baseclock;
		int volumeSSG[MAX_8910]; /* for SSG sound */
		mem_read_handler portAread[MAX_8910];
		mem_read_handler portBread[MAX_8910];
		mem_write_handler portAwrite[MAX_8910];
		mem_write_handler portBwrite[MAX_8910];
		void ( *handler[MAX_8910] )( int irq );	/* IRQ handler for the YM2608 */
		int pcmrom[MAX_2608];		/* Delta-T memory region ram/rom */
		int volumeFM[MAX_2608];		/* use YM3012_VOL macro */
	};
	
	int YM2608_sh_start(const struct MachineSound *msound);
	void YM2608_sh_stop(void);
	void YM2608_sh_reset(void);
	
	/************************************************/
	/* Chip 0 functions				*/
	/************************************************/
	
	/************************************************/
	/* Chip 1 functions				*/
	/************************************************/
	
	#endif /* __2608INTF_H__ */
}
