/***************************************************************************

	Bally/Sente SAC-1 system

    driver by Aaron Giles

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package includes;

public class balsenteH
{
	
	
	/*----------- defined in machine/balsente.c -----------*/
	
	extern UINT8 balsente_shooter;
	extern UINT8 balsente_shooter_x;
	extern UINT8 balsente_shooter_y;
	extern UINT8 balsente_adc_shift;
	extern data16_t *shrike_shared;
	
	
	void balsente_noise_gen(int chip, int count, short *buffer);
	
	WRITE_HANDLER( balsente_random_reset_w );
	
	WRITE_HANDLER( balsente_rombank_select_w );
	WRITE_HANDLER( balsente_rombank2_select_w );
	
	WRITE_HANDLER( balsente_misc_output_w );
	
	WRITE_HANDLER( balsente_m6850_w );
	
	WRITE_HANDLER( balsente_m6850_sound_w );
	
	WRITE_HANDLER( balsente_adc_select_w );
	
	WRITE_HANDLER( balsente_counter_8253_w );
	
	WRITE_HANDLER( balsente_counter_control_w );
	
	WRITE_HANDLER( balsente_chip_select_w );
	WRITE_HANDLER( balsente_dac_data_w );
	WRITE_HANDLER( balsente_register_addr_w );
	
	WRITE_HANDLER( spiker_expand_w );
	
	READ16_HANDLER( shrike_shared_68k_r );
	WRITE16_HANDLER( shrike_shared_68k_w );
	WRITE_HANDLER( shrike_shared_6809_w );
	
	
	/*----------- defined in vidhrdw/balsente.c -----------*/
	
	
	WRITE_HANDLER( balsente_videoram_w );
	WRITE_HANDLER( balsente_paletteram_w );
	WRITE_HANDLER( balsente_palette_select_w );
}
