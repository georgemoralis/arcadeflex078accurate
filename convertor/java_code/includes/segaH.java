/*************************************************************************

	Sega vector hardware

*************************************************************************/

/*----------- defined in machine/sega.c -----------*/

extern UINT8 *sega_mem;
extern void sega_security(int chip);






/*----------- defined in sndhrdw/sega.c -----------*/


int tacscan_sh_start(const struct MachineSound *msound);
void tacscan_sh_update(void);



/*----------- defined in vidhrdw/sega.c -----------*/

