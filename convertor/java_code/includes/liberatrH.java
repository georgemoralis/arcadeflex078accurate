/*************************************************************************

	Atari Liberator hardware

*************************************************************************/

/*----------- defined in vidhrdw/liberatr.c -----------*/

extern UINT8 *liberatr_base_ram;
extern UINT8 *liberatr_planet_frame;
extern UINT8 *liberatr_planet_select;
extern UINT8 *liberatr_x;
extern UINT8 *liberatr_y;
extern UINT8 *liberatr_bitmapram;


WRITE_HANDLER( liberatr_colorram_w ) ;
WRITE_HANDLER( liberatr_bitmap_w );
WRITE_HANDLER( liberatr_bitmap_xy_w );
