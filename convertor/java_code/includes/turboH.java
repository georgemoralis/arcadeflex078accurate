/*************************************************************************

	Sega Z80-3D system

*************************************************************************/

/*----------- defined in machine/turbo.c -----------*/

extern UINT8 turbo_opa, turbo_opb, turbo_opc;
extern UINT8 turbo_ipa, turbo_ipb, turbo_ipc;
extern UINT8 turbo_fbpla, turbo_fbcol;

extern UINT8 subroc3d_col, subroc3d_ply, subroc3d_chofs;

extern UINT8 buckrog_fchg, buckrog_mov, buckrog_obch;


WRITE_HANDLER( turbo_8279_w );

WRITE_HANDLER( turbo_collision_clear_w );
WRITE_HANDLER( turbo_coin_and_lamp_w );

void turbo_rom_decode(void);

void turbo_update_tachometer(void);
void turbo_update_segments(void);



/*----------- defined in vidhrdw/turbo.c -----------*/

extern UINT8 *sega_sprite_position;
extern UINT8 turbo_collision;




WRITE_HANDLER( buckrog_led_display_w );
WRITE_HANDLER( buckrog_bitmap_w );
