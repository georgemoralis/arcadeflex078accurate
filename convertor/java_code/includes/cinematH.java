/*************************************************************************

	Cinematronics vector hardware

*************************************************************************/

/*----------- defined in sndhrdw/cinemat.c -----------*/

typedef void (*cinemat_sound_handler_proc)(UINT8, UINT8);


READ16_HANDLER( cinemat_output_port_r );
WRITE16_HANDLER( cinemat_output_port_w );


void starcas_sound_w(UINT8 sound_val, UINT8 bits_changed);
void warrior_sound_w(UINT8 sound_val, UINT8 bits_changed);
void armora_sound_w(UINT8 sound_val, UINT8 bits_changed);
void ripoff_sound_w(UINT8 sound_val, UINT8 bits_changed);
void solarq_sound_w(UINT8 sound_val, UINT8 bits_changed);
void spacewar_sound_w(UINT8 sound_val, UINT8 bits_changed);
void demon_sound_w(UINT8 sound_val, UINT8 bits_changed);
void armora_sound_w(UINT8 sound_val, UINT8 bits_changed);
void sundance_sound_w(UINT8 sound_val, UINT8 bits_changed);

MACHINE_DRIVER_EXTERN( demon_sound );


/*----------- defined in vidhrdw/cinemat.c -----------*/

void CinemaVectorData(int fromx, int fromy, int tox, int toy, int color);


