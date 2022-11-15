/*************************************************************************

	Atari Centipede hardware

*************************************************************************/

/*----------- defined in vidhrdw/centiped.c -----------*/

extern UINT8 centiped_flipscreen;

PALETTE_INIT( centiped );
PALETTE_INIT( milliped );
PALETTE_INIT( warlords );


VIDEO_UPDATE( centiped );
VIDEO_UPDATE( warlords );
VIDEO_UPDATE( qwakprot );

WRITE_HANDLER( centiped_paletteram_w );
WRITE_HANDLER( milliped_paletteram_w );
WRITE_HANDLER( qwakprot_paletteram_w );

WRITE_HANDLER( centiped_videoram_w );
WRITE_HANDLER( centiped_flip_screen_w );

