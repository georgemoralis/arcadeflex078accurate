/***************************************************************************

  Galaxian hardware family

  This include file is used by the following drivers:
    - galaxian.c
    - scramble.c
    - scobra.c
    - frogger.c
    - amidar.c

***************************************************************************/

/* defined in drivers/galaxian.c */
extern struct GfxDecodeInfo galaxian_gfxdecodeinfo[];
MACHINE_DRIVER_EXTERN(galaxian_base);


/* defined in drivers/scobra.c */
extern struct AY8910interface scobra_ay8910_interface;
extern const struct Memory_ReadAddress scobra_sound_readmem[];
extern const struct Memory_WriteAddress scobra_sound_writemem[];
extern const struct IO_ReadPort scobra_sound_readport[];
extern const struct IO_WritePort scobra_sound_writeport[];


/* defined in drivers/frogger.c */
extern struct AY8910interface frogger_ay8910_interface;
extern const struct Memory_ReadAddress frogger_sound_readmem[];
extern const struct Memory_WriteAddress frogger_sound_writemem[];
extern const struct IO_ReadPort frogger_sound_readport[];
extern const struct IO_WritePort frogger_sound_writeport[];


/* defined in vidhrdw/galaxian.c */
extern data8_t *galaxian_videoram;
extern data8_t *galaxian_spriteram;
extern data8_t *galaxian_spriteram2;
extern data8_t *galaxian_attributesram;
extern data8_t *galaxian_bulletsram;
extern data8_t *rockclim_videoram;

extern size_t galaxian_spriteram_size;
extern size_t galaxian_spriteram2_size;
extern size_t galaxian_bulletsram_size;


WRITE_HANDLER( galaxian_videoram_w );

WRITE_HANDLER( rockclim_videoram_w );
WRITE_HANDLER( rockclim_scroll_w );


WRITE_HANDLER( galaxian_attributesram_w );

WRITE_HANDLER( galaxian_stars_enable_w );
WRITE_HANDLER( scramble_background_enable_w );
WRITE_HANDLER( scramble_background_red_w );
WRITE_HANDLER( scramble_background_green_w );
WRITE_HANDLER( scramble_background_blue_w );
WRITE_HANDLER( hotshock_flip_screen_w );
WRITE_HANDLER( darkplnt_bullet_color_w );



WRITE_HANDLER( galaxian_gfxbank_w );
WRITE_HANDLER( galaxian_nmi_enable_w );
WRITE_HANDLER( galaxian_flip_screen_x_w );
WRITE_HANDLER( galaxian_flip_screen_y_w );
WRITE_HANDLER( gteikob2_flip_screen_x_w );
WRITE_HANDLER( gteikob2_flip_screen_y_w );


/* defined in machine/scramble.c */


WRITE_HANDLER(scobra_type2_ppi8255_0_w);
WRITE_HANDLER(scobra_type2_ppi8255_1_w);

WRITE_HANDLER(hustler_ppi8255_0_w);
WRITE_HANDLER(hustler_ppi8255_1_w);

WRITE_HANDLER(amidar_ppi8255_0_w);
WRITE_HANDLER(amidar_ppi8255_1_w);

WRITE_HANDLER(frogger_ppi8255_0_w);
WRITE_HANDLER(frogger_ppi8255_1_w);

WRITE_HANDLER(mars_ppi8255_0_w);
WRITE_HANDLER(mars_ppi8255_1_w);


WRITE_HANDLER( galaxian_coin_lockout_w );
WRITE_HANDLER( galaxian_coin_counter_w );
#define galaxian_coin_counter_0_w galaxian_coin_counter_w
WRITE_HANDLER( galaxian_coin_counter_1_w );
WRITE_HANDLER( galaxian_coin_counter_2_w );
WRITE_HANDLER( galaxian_leds_w );



WRITE_HANDLER( kingball_speech_dip_w );
WRITE_HANDLER( kingball_sound1_w );
WRITE_HANDLER( kingball_sound2_w );

WRITE_HANDLER( _4in1_bank_w );

WRITE_HANDLER( hunchbks_mirror_w );

WRITE_HANDLER( zigzag_sillyprotection_w );




/* defined in sndhrdw/galaxian.c */
extern struct CustomSound_interface galaxian_custom_interface;
WRITE_HANDLER( galaxian_pitch_w );
WRITE_HANDLER( galaxian_vol_w );
WRITE_HANDLER( galaxian_noise_enable_w );
WRITE_HANDLER( galaxian_background_enable_w );
WRITE_HANDLER( galaxian_shoot_enable_w );
WRITE_HANDLER( galaxian_lfo_freq_w );


/* defined in sndhrdw/scramble.c */
void scramble_sh_init(void);
void sfx_sh_init(void);

WRITE_HANDLER( scramble_filter_w );
WRITE_HANDLER( frogger_filter_w );


WRITE_HANDLER( scramble_sh_irqtrigger_w );
WRITE_HANDLER( sfx_sh_irqtrigger_w );
WRITE_HANDLER( mrkougar_sh_irqtrigger_w );
WRITE_HANDLER( froggrmc_sh_irqtrigger_w );
WRITE_HANDLER( hotshock_sh_irqtrigger_w );
WRITE_HANDLER( explorer_sh_irqtrigger_w  );

WRITE_HANDLER( zigzag_8910_latch_w );
WRITE_HANDLER( zigzag_8910_data_trigger_w );
WRITE_HANDLER( zigzag_8910_control_trigger_w );
