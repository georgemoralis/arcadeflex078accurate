#ifndef YM2203INTF_H
#define YM2203INTF_H

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package sound;

public class _2203intfH
{
	
	#define MAX_2203 4
	
	#define YM2203interface AY8910interface
	
	/* volume level for YM2203 */
	#define YM2203_VOL(FM_VOLUME,SSG_VOLUME) (((FM_VOLUME)<<16)+(SSG_VOLUME))
	
	
	
	
	
	
	int YM2203_sh_start(const struct MachineSound *msound);
	void YM2203_sh_stop(void);
	void YM2203_sh_reset(void);
	
	void YM2203UpdateRequest(int chip);
	
	#endif
}
