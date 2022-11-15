/***************************************************************************

	Videa Gridlee hardware

    driver by Aaron Giles

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package includes;

public class gridleeH
{
	
	
	/*----------- defined in sndhrdw/gridlee.c -----------*/
	
	WRITE_HANDLER( gridlee_sound_w );
	int gridlee_sh_start(const struct MachineSound *msound);
	
	
	/*----------- defined in vidhrdw/gridlee.c -----------*/
	
	/* video driver data & functions */
	extern UINT8 gridlee_cocktail_flip;
	
	
	WRITE_HANDLER( gridlee_cocktail_flip_w );
	WRITE_HANDLER( gridlee_videoram_w );
	WRITE_HANDLER( gridlee_palette_select_w );
}
