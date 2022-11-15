/*************************************************************************

	Atari Missile Command hardware

*************************************************************************/

/*----------- defined in machine/missile.c -----------*/

WRITE_HANDLER( missile_w );


/*----------- defined in vidhrdw/missile.c -----------*/

extern unsigned char *missile_video2ram;


WRITE_HANDLER( missile_video_3rd_bit_w );
WRITE_HANDLER( missile_video2_w );

WRITE_HANDLER( missile_video_w );
WRITE_HANDLER( missile_video_mult_w );
WRITE_HANDLER( missile_palette_w );
