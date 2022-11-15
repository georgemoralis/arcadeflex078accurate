/*************************************************************************

	Atari I, Robot hardware

*************************************************************************/

/*----------- defined in machine/irobot.c -----------*/

extern UINT8 irvg_clear;
extern UINT8 irobot_bufsel;
extern UINT8 irobot_alphamap;
extern UINT8 *irobot_combase;


WRITE_HANDLER( irobot_statwr_w );
WRITE_HANDLER( irobot_out0_w );
WRITE_HANDLER( irobot_rom_banksel_w );
WRITE_HANDLER( irobot_control_w );
WRITE_HANDLER( irobot_sharedmem_w );


/*----------- defined in vidhrdw/irobot.c -----------*/


WRITE_HANDLER( irobot_paletteram_w );

void irobot_poly_clear(void);
void irobot_run_video(void);
