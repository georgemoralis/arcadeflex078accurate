/*************************************************************************

	Jaleco Exerion

*************************************************************************/

/*----------- defined in vidhrdw/exerion.c -----------*/

PALETTE_INIT( exerion );

WRITE_HANDLER( exerion_videoreg_w );
WRITE_HANDLER( exerion_video_latch_w );
READ_HANDLER( exerion_video_timing_r );

extern UINT8 exerion_cocktail_flip;
