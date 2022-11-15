
extern data16_t *f2_sprite_extension;
extern size_t f2_spriteext_size;

VIDEO_EOF( taitof2_no_buffer );
VIDEO_EOF( taitof2_full_buffer_delayed );
VIDEO_EOF( taitof2_partial_buffer_delayed );
VIDEO_EOF( taitof2_partial_buffer_delayed_thundfox );
VIDEO_EOF( taitof2_partial_buffer_delayed_qzchikyu );

VIDEO_UPDATE( taitof2 );
VIDEO_UPDATE( taitof2_pri );
VIDEO_UPDATE( taitof2_pri_roz );
VIDEO_UPDATE( ssi );
VIDEO_UPDATE( thundfox );
VIDEO_UPDATE( deadconx );
VIDEO_UPDATE( metalb );
VIDEO_UPDATE( yesnoj );

WRITE16_HANDLER( taitof2_spritebank_w );
READ16_HANDLER ( koshien_spritebank_r );
WRITE16_HANDLER( koshien_spritebank_w );
WRITE16_HANDLER( taitof2_sprite_extension_w );

extern data16_t *cchip_ram;
READ16_HANDLER ( cchip2_word_r );
WRITE16_HANDLER( cchip2_word_w );

