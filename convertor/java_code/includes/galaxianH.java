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











/* defined in machine/scramble.c */








#define galaxian_coin_counter_0_w galaxian_coin_counter_w










/* defined in sndhrdw/galaxian.c */
extern struct CustomSound_interface galaxian_custom_interface;


/* defined in sndhrdw/scramble.c */
void scramble_sh_init(void);
void sfx_sh_init(void);




