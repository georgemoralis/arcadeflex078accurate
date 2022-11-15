/*************************************************************************

	Sega G-80 raster hardware

*************************************************************************/

/*----------- defined in machine/segar.c -----------*/

extern UINT8 *segar_mem;
extern void (*sega_decrypt)(int,unsigned int *);

void sega_security(int chip);


/*----------- defined in sndhrdw/segar.c -----------*/

 
   
/* temporary speech handling through samples */
int astrob_speech_sh_start(const struct MachineSound *msound);
void astrob_speech_sh_update(void);

/* sample names */
extern const char *astrob_sample_names[];
extern const char *s005_sample_names[];
extern const char *monsterb_sample_names[];
extern const char *spaceod_sample_names[];


/*----------- defined in vidhrdw/segar.c -----------*/

extern UINT8 *segar_characterram;
extern UINT8 *segar_characterram2;
extern UINT8 *segar_mem_colortable;
extern UINT8 *segar_mem_bcolortable;










