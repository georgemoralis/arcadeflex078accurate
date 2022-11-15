/*************************************************************************

	Sega System C/C2 Driver

**************************************************************************/

/*----------- defined in vidhrdw/segac2.c -----------*/

extern UINT8		segac2_vdp_regs[];
extern int			segac2_bg_palbase;
extern int			segac2_sp_palbase;
extern int			segac2_palbank;
extern UINT16		scanbase;





void	segac2_enable_display(int enable);

READ16_HANDLER ( segac2_vdp_r );
WRITE16_HANDLER( segac2_vdp_w );
