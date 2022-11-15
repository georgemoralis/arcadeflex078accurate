/*************************************************************************

	Atari Subs hardware

*************************************************************************/

/*----------- defined in machine/subs.c -----------*/

WRITE_HANDLER( subs_steer_reset_w );
WRITE_HANDLER( subs_lamp1_w );
WRITE_HANDLER( subs_lamp2_w );
WRITE_HANDLER( subs_noise_reset_w );
WRITE_HANDLER( subs_sonar2_w );
WRITE_HANDLER( subs_sonar1_w );
WRITE_HANDLER( subs_crash_w );
WRITE_HANDLER( subs_explode_w );


/*----------- defined in vidhrdw/subs.c -----------*/


WRITE_HANDLER( subs_invert1_w );
WRITE_HANDLER( subs_invert2_w );
