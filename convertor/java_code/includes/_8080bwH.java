/*************************************************************************

	8080bw.h

*************************************************************************/

/*----------- defined in machine/8080bw.c -----------*/

WRITE_HANDLER( c8080bw_shift_amount_w );
WRITE_HANDLER( c8080bw_shift_data_w );
READ_HANDLER( c8080bw_shift_data_r );
READ_HANDLER( c8080bw_shift_data_rev_r );
READ_HANDLER( c8080bw_shift_data_comp_r );

READ_HANDLER( boothill_shift_data_r );

READ_HANDLER( spcenctr_port_0_r );
READ_HANDLER( spcenctr_port_1_r );

READ_HANDLER( boothill_port_0_r );
READ_HANDLER( boothill_port_1_r );

READ_HANDLER( gunfight_port_0_r );
READ_HANDLER( gunfight_port_1_r );

READ_HANDLER( seawolf_port_1_r );

WRITE_HANDLER( desertgu_controller_select_w );
READ_HANDLER( desertgu_port_1_r );

/*----------- defined in sndhrdw/8080bw.c -----------*/


WRITE_HANDLER( sheriff_sh_p2_w );
READ_HANDLER( sheriff_sh_p1_r );
READ_HANDLER( sheriff_sh_p2_r );
READ_HANDLER( sheriff_sh_t0_r );
READ_HANDLER( sheriff_sh_t1_r );

WRITE_HANDLER( helifire_sh_p1_w );
WRITE_HANDLER( helifire_sh_p2_w );
READ_HANDLER( helifire_sh_p1_r );

extern struct SN76477interface invaders_sn76477_interface;
extern struct Samplesinterface invaders_samples_interface;
extern struct SN76477interface invad2ct_sn76477_interface;
extern struct Samplesinterface invad2ct_samples_interface;
extern struct DACinterface sheriff_dac_interface;
extern struct SN76477interface sheriff_sn76477_interface;
extern struct Samplesinterface boothill_samples_interface;
extern struct DACinterface schaser_dac_interface;
extern struct CustomSound_interface schaser_custom_interface;
extern struct SN76477interface schaser_sn76477_interface;
extern struct Samplesinterface seawolf_samples_interface;
extern struct discrete_sound_block polaris_sound_interface[];


/*----------- defined in vidhrdw/8080bw.c -----------*/


void c8080bw_flip_screen_w(int data);
void c8080bw_screen_red_w(int data);
void c8080bw_helifire_colors_change_w(int data);


WRITE_HANDLER( c8080bw_videoram_w );
WRITE_HANDLER( schaser_colorram_w );
READ_HANDLER( schaser_colorram_r );
WRITE_HANDLER( helifire_colorram_w );
WRITE_HANDLER( spaceint_color_w );
WRITE_HANDLER( cosmo_colorram_w );




WRITE_HANDLER( bowler_bonus_display_w );
