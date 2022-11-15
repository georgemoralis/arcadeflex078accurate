/*************************************************************************

	Namco PuckMan

**************************************************************************/

/*----------- defined in machine/pacplus.c -----------*/

void pacplus_decode(void);


/*----------- defined in machine/jumpshot.c -----------*/

void jumpshot_decode(void);


/*----------- defined in machine/theglobp.c -----------*/

READ_HANDLER( theglobp_decrypt_rom );


/*----------- defined in machine/mspacman.c -----------*/

WRITE_HANDLER( mspacman_activate_rom );

/*----------- defined in machine/acitya.c -------------*/

READ_HANDLER( acitya_decrypt_rom );
