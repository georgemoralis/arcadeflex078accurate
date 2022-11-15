/***************************************************************************

	Taito Qix hardware

	driver by John Butler, Ed Mueller, Aaron Giles

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package includes;

public class qixH
{
	
	
	/*----------- defined in machine/qix.c -----------*/
	
	extern UINT8 *qix_sharedram;
	extern UINT8 *qix_68705_port_out;
	extern UINT8 *qix_68705_ddr;
	
	
	WRITE_HANDLER( qix_sharedram_w );
	
	WRITE_HANDLER( zoo_bankswitch_w );
	
	WRITE_HANDLER( qix_data_firq_w );
	WRITE_HANDLER( qix_data_firq_ack_w );
	
	WRITE_HANDLER( qix_video_firq_w );
	WRITE_HANDLER( qix_video_firq_ack_w );
	
	WRITE_HANDLER( qix_68705_portA_w );
	WRITE_HANDLER( qix_68705_portB_w );
	WRITE_HANDLER( qix_68705_portC_w );
	
	WRITE_HANDLER( qix_pia_0_w );
	WRITE_HANDLER( zookeep_pia_0_w );
	WRITE_HANDLER( zookeep_pia_2_w );
	
	
	/*----------- defined in vidhrdw/qix.c -----------*/
	
	extern UINT8 *qix_videoaddress;
	extern UINT8 qix_cocktail_flip;
	
	
	void qix_scanline_callback(int scanline);
	
	WRITE_HANDLER( qix_videoram_w );
	WRITE_HANDLER( qix_addresslatch_w );
	WRITE_HANDLER( slither_vram_mask_w );
	WRITE_HANDLER( qix_paletteram_w );
	WRITE_HANDLER( qix_palettebank_w );
	
	WRITE_HANDLER( qix_data_io_w );
	WRITE_HANDLER( qix_sound_io_w );
}
