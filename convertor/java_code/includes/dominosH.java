/*************************************************************************

	Atari Dominos hardware

*************************************************************************/

/*----------- defined in machine/dominos.c -----------*/

WRITE_HANDLER( dominos_attract_w );
WRITE_HANDLER( dominos_tumble_w );
WRITE_HANDLER( dominos_lamp2_w );
WRITE_HANDLER( dominos_lamp1_w );

void dominos_ac_signal_flip(int dummy);

/*----------- defined in vidhrdw/dominos.c -----------*/


extern unsigned char *dominos_sound_ram;
