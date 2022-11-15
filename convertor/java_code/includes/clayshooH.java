/***************************************************************************

	Atari Clay Shoot hardware

	driver by Zsolt Vasvari

****************************************************************************/

/* defined in machine/clayshoo.c */

WRITE_HANDLER( clayshoo_analog_reset_w );
READ_HANDLER( clayshoo_analog_r );


/* defined in vidhrdw/clayshoo.c */

WRITE_HANDLER( clayshoo_videoram_w );
