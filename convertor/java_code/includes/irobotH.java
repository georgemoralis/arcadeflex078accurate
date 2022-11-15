/*************************************************************************

	Atari I, Robot hardware

*************************************************************************/

/*----------- defined in machine/irobot.c -----------*/

extern UINT8 irvg_clear;
extern UINT8 irobot_bufsel;
extern UINT8 irobot_alphamap;
extern UINT8 *irobot_combase;




/*----------- defined in vidhrdw/irobot.c -----------*/



void irobot_poly_clear(void);
void irobot_run_video(void);
