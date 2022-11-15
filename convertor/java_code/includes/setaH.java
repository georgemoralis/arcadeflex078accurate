/***************************************************************************

							-= Seta Hardware =-

***************************************************************************/

/* Variables and functions defined in drivers/seta.c */

void seta_coin_lockout_w(int data);


/* Variables and functions defined in vidhrdw/seta.c */

extern data16_t *seta_vram_0, *seta_vctrl_0;
extern data16_t *seta_vram_2, *seta_vctrl_2;
extern data16_t *seta_vregs;

extern data16_t *seta_workram; // Needed for zombraid Crosshair hack

extern int seta_tiles_offset;

WRITE16_HANDLER( twineagl_tilebank_w );

WRITE16_HANDLER( seta_vram_0_w );
WRITE16_HANDLER( seta_vram_2_w );
WRITE16_HANDLER( seta_vregs_w );





/* Variables and functions defined in vidhrdw/seta2.c */

extern data16_t *seta2_vregs;

WRITE16_HANDLER( seta2_vregs_w );



/* Variables and functions defined in sndhrdw/seta.c */
#define	__uPD71054_TIMER	1


/* Variables and functions defined in vidhrdw/ssv.c */

extern data16_t *ssv_scroll;

extern int ssv_special;

extern int ssv_tile_code[16];

extern int ssv_sprites_offsx, ssv_sprites_offsy;
extern int ssv_tilemap_offsx, ssv_tilemap_offsy;

READ16_HANDLER( ssv_vblank_r );
WRITE16_HANDLER( ssv_scroll_w );
WRITE16_HANDLER( paletteram16_xrgb_swap_word_w );
void ssv_enable_video(int enable);

