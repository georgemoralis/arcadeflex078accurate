/***************************************************************************

	Sun Electronics Kangaroo hardware

	driver by Ville Laitinen

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package includes;

public class kangarooH
{
	
	
	/*----------- defined in vidhrdw/kangaroo.c -----------*/
	
	extern UINT8 *kangaroo_video_control;
	extern UINT8 *kangaroo_bank_select;
	extern UINT8 *kangaroo_blitter;
	extern UINT8 *kangaroo_scroll;
	
	
	WRITE_HANDLER( kangaroo_blitter_w );
	WRITE_HANDLER( kangaroo_videoram_w );
	WRITE_HANDLER( kangaroo_video_control_w );
	WRITE_HANDLER( kangaroo_bank_select_w );
	WRITE_HANDLER( kangaroo_color_mask_w );
}
