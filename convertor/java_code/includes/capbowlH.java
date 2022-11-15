/*************************************************************************

	Coors Light Bowling/Bowl-O-Rama hardware

*************************************************************************/

/*----------- defined in vidhrdw/capbowl.c -----------*/



extern unsigned char *capbowl_rowaddress;

WRITE_HANDLER( capbowl_rom_select_w );


WRITE_HANDLER( bowlrama_turbo_w );

WRITE_HANDLER( capbowl_tms34061_w );
