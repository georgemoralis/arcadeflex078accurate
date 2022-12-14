/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package sndhrdw;

public class rastan
{
	
	/* Game writes here to set ADPCM ROM address */
	public static WriteHandlerPtr rastan_adpcm_trigger_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		UINT8 *rom = memory_region(REGION_SOUND1);
		int len = memory_region_length(REGION_SOUND1);
		int start = data << 8;
		int end;
	
		/* look for end of sample */
		end = (start + 3) & ~3;
		while (end < len && *((UINT32 *)(&rom[end])) != 0x08080808)
			end += 4;
	
		ADPCM_play(0,start,(end-start)*2);
	} };
	
	/* Game writes here to START ADPCM_voice playing */
	public static WriteHandlerPtr rastan_c000_w = new WriteHandlerPtr() {public void handler(int offset, int data){
	} };
	
	/* Game writes here to STOP ADPCM_voice playing */
	public static WriteHandlerPtr rastan_d000_w = new WriteHandlerPtr() {public void handler(int offset, int data){
	#if 0
		if (Machine->samples == 0) return;
		if (data==0)
			mixer_stop_sample(channel);
	#endif
	} };
}
