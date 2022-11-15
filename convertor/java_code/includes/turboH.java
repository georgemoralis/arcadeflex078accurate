/*************************************************************************

	Sega Z80-3D system

*************************************************************************/

/*----------- defined in machine/turbo.c -----------*/

extern UINT8 turbo_opa, turbo_opb, turbo_opc;
extern UINT8 turbo_ipa, turbo_ipb, turbo_ipc;
extern UINT8 turbo_fbpla, turbo_fbcol;

extern UINT8 subroc3d_col, subroc3d_ply, subroc3d_chofs;

extern UINT8 buckrog_fchg, buckrog_mov, buckrog_obch;




void turbo_rom_decode(void);

void turbo_update_tachometer(void);
void turbo_update_segments(void);



/*----------- defined in vidhrdw/turbo.c -----------*/

extern UINT8 *sega_sprite_position;
extern UINT8 turbo_collision;




