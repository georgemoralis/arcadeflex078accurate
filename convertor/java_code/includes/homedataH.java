extern data8_t *homedata_vreg;
extern int homedata_visible_page;
extern int homedata_priority;
extern data8_t reikaids_which;

WRITE_HANDLER( mrokumei_videoram_w );
WRITE_HANDLER( reikaids_videoram_w );
WRITE_HANDLER( pteacher_videoram_w );
WRITE_HANDLER( reikaids_gfx_bank_w );
WRITE_HANDLER( pteacher_gfx_bank_w );
WRITE_HANDLER( homedata_blitter_param_w );
WRITE_HANDLER( mrokumei_blitter_bank_w );
WRITE_HANDLER( reikaids_blitter_bank_w );
WRITE_HANDLER( pteacher_blitter_bank_w );
WRITE_HANDLER( mrokumei_blitter_start_w );
WRITE_HANDLER( reikaids_blitter_start_w );
WRITE_HANDLER( pteacher_blitter_start_w );


