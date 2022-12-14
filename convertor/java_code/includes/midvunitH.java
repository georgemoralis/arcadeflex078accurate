/*************************************************************************

	Driver for Midway V-Unit games

**************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package includes;

public class midvunitH
{
	
	
	/*----------- defined in vidhrdw/midvunit.c -----------*/
	
	
	WRITE32_HANDLER( midvunit_dma_queue_w );
	READ32_HANDLER( midvunit_dma_queue_entries_r );
	READ32_HANDLER( midvunit_dma_trigger_r );
	
	WRITE32_HANDLER( midvunit_page_control_w );
	READ32_HANDLER( midvunit_page_control_r );
	
	WRITE32_HANDLER( midvunit_video_control_w );
	READ32_HANDLER( midvunit_scanline_r );
	
	WRITE32_HANDLER( midvunit_videoram_w );
	READ32_HANDLER( midvunit_videoram_r );
	
	WRITE32_HANDLER( midvunit_paletteram_w );
	
	WRITE32_HANDLER( midvunit_textureram_w );
	READ32_HANDLER( midvunit_textureram_r );
	
	}
