/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package includes;

public class namcos22H
{
	
	{
		NAMCOS22_AIR_COMBAT22,
		NAMCOS22_ALPINE_RACER,
		NAMCOS22_CYBER_COMMANDO,
		NAMCOS22_CYBER_CYCLES,
		NAMCOS22_PROP_CYCLE,
		NAMCOS22_RAVE_RACER,
		NAMCOS22_RIDGE_RACER,
		NAMCOS22_TIME_CRISIS,
		NAMCOS22_VICTORY_LAP,
		NAMCOS22_ACE_DRIVER
	} namcos22_gametype;
	
	#define NAMCOS22_NUM_ROWS 30
	#define NAMCOS22_NUM_COLS 40
	#define NAMCOS22_ALPHA_GFX 1
	
	#define NAMCOS22_PALETTE_SIZE 0x8000
	
	
	READ32_HANDLER( namcos22_cgram_r );
	WRITE32_HANDLER( namcos22_cgram_w );
	
	READ32_HANDLER( namcos22_paletteram_r );
	WRITE32_HANDLER( namcos22_paletteram_w );
	
	READ32_HANDLER( namcos22_textram_r );
	WRITE32_HANDLER( namcos22_textram_w );
	
	READ32_HANDLER( namcos22_gamma_r );
	WRITE32_HANDLER( namcos22_gamma_w );
	
	READ32_HANDLER( namcos22_dspram_r );
	WRITE32_HANDLER( namcos22_dspram_w );
	
	
	}
