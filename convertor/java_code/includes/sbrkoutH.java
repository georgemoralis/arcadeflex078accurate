/*************************************************************************

	Atari Super Breakout hardware

*************************************************************************/

/*----------- defined in machine/sbrkout.c -----------*/

WRITE_HANDLER( sbrkout_serve_led_w );
WRITE_HANDLER( sbrkout_start_1_led_w );
WRITE_HANDLER( sbrkout_start_2_led_w );


/*----------- defined in vidhrdw/sbrkout.c -----------*/

extern unsigned char *sbrkout_horiz_ram;
extern unsigned char *sbrkout_vert_ram;

