/*************************************************************************

	Atari Return of the Jedi hardware

*************************************************************************/

/*----------- defined in vidhrdw/jedi.c -----------*/

extern UINT8 *jedi_PIXIRAM;
extern UINT8 *jedi_backgroundram;
extern size_t jedi_backgroundram_size;


WRITE_HANDLER( jedi_alpha_banksel_w );
WRITE_HANDLER( jedi_paletteram_w );
WRITE_HANDLER( jedi_backgroundram_w );
WRITE_HANDLER( jedi_vscroll_w );
WRITE_HANDLER( jedi_hscroll_w );
WRITE_HANDLER( jedi_video_off_w );
WRITE_HANDLER( jedi_PIXIRAM_w );
