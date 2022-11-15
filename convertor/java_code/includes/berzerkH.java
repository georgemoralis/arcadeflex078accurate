/* defined in machine/berzerk.c */

WRITE_HANDLER( berzerk_irq_enable_w );
WRITE_HANDLER( berzerk_nmi_enable_w );
WRITE_HANDLER( berzerk_nmi_disable_w );


/* defined in vidrhdw/berzerk.c */

extern data8_t *berzerk_magicram;

WRITE_HANDLER( berzerk_videoram_w );
WRITE_HANDLER( berzerk_colorram_w );
WRITE_HANDLER( berzerk_magicram_w );
WRITE_HANDLER( berzerk_magicram_control_w );


/* defined in sndhrdw/berzerk.c */

extern struct Samplesinterface berzerk_samples_interface;
extern struct CustomSound_interface berzerk_custom_interface;
WRITE_HANDLER( berzerk_sound_control_a_w );
WRITE_HANDLER( berzerk_sound_control_b_w );
