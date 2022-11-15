/*************************************************************************

	8080bw.h

*************************************************************************/

/*----------- defined in machine/8080bw.c -----------*/

WRITE_HANDLER( c8080bw_shift_amount_w );
WRITE_HANDLER( c8080bw_shift_data_w );






WRITE_HANDLER( desertgu_controller_select_w );

/*----------- defined in sndhrdw/8080bw.c -----------*/


WRITE_HANDLER( sheriff_sh_p2_w );

WRITE_HANDLER( helifire_sh_p1_w );
WRITE_HANDLER( helifire_sh_p2_w );

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
WRITE_HANDLER( helifire_colorram_w );
WRITE_HANDLER( spaceint_color_w );
WRITE_HANDLER( cosmo_colorram_w );




WRITE_HANDLER( bowler_bonus_display_w );
