/*************************************************************************

	Exidy 440 hardware

*************************************************************************/

/*----------- defined in drivers/exidy440.c -----------*/

extern UINT8 exidy440_bank;
extern UINT8 exidy440_topsecret;


/*----------- defined in sndhrdw/exidy440.c -----------*/

extern UINT8 exidy440_sound_command;
extern UINT8 exidy440_sound_command_ack;
extern UINT8 *exidy440_m6844_data;
extern UINT8 *exidy440_sound_banks;
extern UINT8 *exidy440_sound_volume;

int exidy440_sh_start(const struct MachineSound *msound);
void exidy440_sh_stop(void);
void exidy440_sh_update(void);



/*----------- defined in vidhrdw/exidy440.c -----------*/

extern UINT8 *spriteram;
extern UINT8 *exidy440_imageram;
extern UINT8 *exidy440_scanline;
extern UINT8 exidy440_firq_vblank;
extern UINT8 exidy440_firq_beam;
extern UINT8 topsecex_yscroll;



