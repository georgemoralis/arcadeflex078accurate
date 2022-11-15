/*************************************************************************

	Cinemat/Leland driver

*************************************************************************/

#define LELAND_BATTERY_RAM_SIZE 0x4000
#define ATAXX_EXTRA_TRAM_SIZE 0x800


/*----------- defined in machine/leland.c -----------*/

#define SERIAL_TYPE_NONE		0
#define SERIAL_TYPE_ADD			1
#define SERIAL_TYPE_ADD_XOR		2
#define SERIAL_TYPE_ENCRYPT		3
#define SERIAL_TYPE_ENCRYPT_XOR	4

extern UINT8 leland_dac_control;
extern void (*leland_update_master_bank)(void);


extern UINT8 *alleymas_kludge_mem;
WRITE_HANDLER( alleymas_joystick_kludge );





WRITE_HANDLER( indyheat_analog_w );



WRITE_HANDLER( leland_master_alt_bankswitch_w );
void cerberus_bankswitch(void);
void mayhem_bankswitch(void);
void dangerz_bankswitch(void);
void basebal2_bankswitch(void);
void redline_bankswitch(void);
void viper_bankswitch(void);
void offroad_bankswitch(void);
void ataxx_bankswitch(void);

void leland_init_eeprom(UINT8 default_val, const UINT16 *data, UINT8 serial_offset, UINT8 serial_type);
void ataxx_init_eeprom(UINT8 default_val, const UINT16 *data, UINT8 serial_offset);

WRITE_HANDLER( ataxx_eeprom_w );

WRITE_HANDLER( leland_battery_ram_w );
WRITE_HANDLER( ataxx_battery_ram_w );

WRITE_HANDLER( leland_master_analog_key_w );

WRITE_HANDLER( leland_master_output_w );
WRITE_HANDLER( ataxx_master_output_w );

WRITE_HANDLER( leland_gated_paletteram_w );
WRITE_HANDLER( ataxx_paletteram_and_misc_w );

WRITE_HANDLER( leland_sound_port_w );

WRITE_HANDLER( leland_slave_small_banksw_w );
WRITE_HANDLER( leland_slave_large_banksw_w );
WRITE_HANDLER( ataxx_slave_banksw_w );


void leland_rotate_memory(int cpunum);


/*----------- defined in sndhrdw/leland.c -----------*/

int leland_sh_start(const struct MachineSound *msound);
void leland_sh_stop(void);
void leland_dac_update(int dacnum, UINT8 sample);

int leland_i186_sh_start(const struct MachineSound *msound);
int redline_i186_sh_start(const struct MachineSound *msound);

void leland_i186_sound_init(void);


void leland_i86_optimize_address(offs_t offset);

WRITE_HANDLER( leland_i86_control_w );
WRITE_HANDLER( leland_i86_command_lo_w );
WRITE_HANDLER( leland_i86_command_hi_w );
WRITE_HANDLER( ataxx_i86_control_w );

extern const struct Memory_ReadAddress leland_i86_readmem[];
extern const struct Memory_WriteAddress leland_i86_writemem[];

extern const struct IO_ReadPort leland_i86_readport[];

extern const struct IO_WritePort redline_i86_writeport[];
extern const struct IO_WritePort leland_i86_writeport[];
extern const struct IO_WritePort ataxx_i86_writeport[];


/*----------- defined in vidhrdw/leland.c -----------*/

extern UINT8 *ataxx_qram;
extern UINT8 leland_last_scanline_int;


WRITE_HANDLER( leland_gfx_port_w );

WRITE_HANDLER( leland_master_video_addr_w );
WRITE_HANDLER( leland_mvram_port_w );

WRITE_HANDLER( leland_slave_video_addr_w );
WRITE_HANDLER( leland_svram_port_w );

WRITE_HANDLER( ataxx_mvram_port_w );
WRITE_HANDLER( ataxx_svram_port_w );

