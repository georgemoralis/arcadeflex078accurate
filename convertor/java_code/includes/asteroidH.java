/*************************************************************************

	Atari Asteroids hardware

*************************************************************************/

/*----------- defined in machine/asteroid.c -----------*/



WRITE_HANDLER( asteroid_bank_switch_w );
WRITE_HANDLER( astdelux_bank_switch_w );
WRITE_HANDLER( astdelux_led_w );




/*----------- defined in sndhrdw/asteroid.c -----------*/

extern struct discrete_sound_block asteroid_sound_interface[];
extern struct discrete_sound_block astdelux_sound_interface[];

WRITE_HANDLER( asteroid_explode_w );
WRITE_HANDLER( asteroid_thump_w );
WRITE_HANDLER( asteroid_sounds_w );
WRITE_HANDLER( asteroid_noise_reset_w );
WRITE_HANDLER( astdelux_sounds_w );


/*----------- defined in sndhrdw/llander.c -----------*/

extern struct discrete_sound_block llander_sound_interface[];

WRITE_HANDLER( llander_snd_reset_w );
WRITE_HANDLER( llander_sounds_w );
