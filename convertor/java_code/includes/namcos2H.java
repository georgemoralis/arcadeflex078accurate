/***************************************************************************

  namcos2.h

  Common functions & declarations for the Namco System 2 driver

***************************************************************************/

// #define NAMCOS2_DEBUG_MODE


/* CPU reference numbers */

#define NAMCOS2_CPU1	0
#define NAMCOS2_CPU2	1
#define NAMCOS2_CPU3	2
#define NAMCOS2_CPU4	3

#define CPU_MASTER	NAMCOS2_CPU1
#define CPU_SLAVE	NAMCOS2_CPU2
#define CPU_SOUND	NAMCOS2_CPU3
#define CPU_MCU 	NAMCOS2_CPU4

/* VIDHRDW */

/*********************************************/
/* IF GAME SPECIFIC HACKS ARE REQUIRED THEN  */
/* USE THE namcos2_gametype VARIABLE TO FIND */
/* OUT WHAT GAME IS RUNNING 				 */
/*********************************************/

enum {
	NAMCOS2_ASSAULT = 0x1000,
	NAMCOS2_ASSAULT_JP,
	NAMCOS2_ASSAULT_PLUS,
	NAMCOS2_BUBBLE_TROUBLE,
	NAMCOS2_BURNING_FORCE,
	NAMCOS2_COSMO_GANG,
	NAMCOS2_COSMO_GANG_US,
	NAMCOS2_DIRT_FOX,
	NAMCOS2_DIRT_FOX_JP,
	NAMCOS2_DRAGON_SABER,
	NAMCOS2_FINAL_LAP,
	NAMCOS2_FINAL_LAP_2,
	NAMCOS2_FINAL_LAP_3,
	NAMCOS2_FINEST_HOUR,
	NAMCOS2_FOUR_TRAX,
	NAMCOS2_GOLLY_GHOST,
	NAMCOS2_LUCKY_AND_WILD,
	NAMCOS2_MARVEL_LAND,
	NAMCOS2_METAL_HAWK,
	NAMCOS2_MIRAI_NINJA,
	NAMCOS2_ORDYNE,
	NAMCOS2_PHELIOS,
	NAMCOS2_ROLLING_THUNDER_2,
	NAMCOS2_STEEL_GUNNER,
	NAMCOS2_STEEL_GUNNER_2,
	NAMCOS2_SUPER_WSTADIUM,
	NAMCOS2_SUPER_WSTADIUM_92,
	NAMCOS2_SUPER_WSTADIUM_92T,
	NAMCOS2_SUPER_WSTADIUM_93,
	NAMCOS2_SUZUKA_8_HOURS,
	NAMCOS2_SUZUKA_8_HOURS_2,
	NAMCOS2_VALKYRIE,
	NAMCOS2_KYUUKAI_DOUCHUUKI,

	NAMCOS21_AIRCOMBAT,
	NAMCOS21_STARBLADE,
	NAMCOS21_CYBERSLED,
	NAMCOS21_SOLVALOU,
	NAMCOS21_WINRUN91,

	NAMCONB1_NEBULRAY,
	NAMCONB1_GUNBULET,
	NAMCONB1_GSLGR94U,
	NAMCONB1_SWS95,
	NAMCONB1_SWS96,
	NAMCONB1_SWS97,
	NAMCONB1_VSHOOT,

	NAMCONB2_OUTFOXIES,
	NAMCONB2_MACH_BREAKERS
};



#define NAMCOS21_NUM_COLORS 0x8000

/*********************************************/







/* MACHINE */


WRITE16_HANDLER( namcos2_gfx_ctrl_w );
READ16_HANDLER( namcos2_gfx_ctrl_r );

WRITE16_HANDLER( namcos2_sprite_ram_w );
READ16_HANDLER( namcos2_sprite_ram_r );

READ16_HANDLER( namcos2_flap_prot_r );

/**************************************************************/
/*	EEPROM memory function handlers 						  */
/**************************************************************/
#define NAMCOS2_68K_EEPROM_W	namcos2_68k_eeprom_w, &namcos2_eeprom, &namcos2_eeprom_size
#define NAMCOS2_68K_EEPROM_R	namcos2_68k_eeprom_r
WRITE16_HANDLER( namcos2_68k_eeprom_w );
READ16_HANDLER( namcos2_68k_eeprom_r );

/**************************************************************/
/*	Shared video memory function handlers					  */
/**************************************************************/
WRITE16_HANDLER( namcos2_68k_vram_w );
READ16_HANDLER( namcos2_68k_vram_r );


READ16_HANDLER( namcos2_68k_vram_ctrl_r );
WRITE16_HANDLER( namcos2_68k_vram_ctrl_w );

/**************************************************************/
/*	Shared video palette function handlers					  */
/**************************************************************/
READ16_HANDLER( namcos2_68k_video_palette_r );
WRITE16_HANDLER( namcos2_68k_video_palette_w );

#define VIRTUAL_PALETTE_BANKS 30


/**************************************************************/
/*	Shared data ROM memory handlerhandlers					  */
/**************************************************************/
READ16_HANDLER( namcos2_68k_data_rom_r );


/**************************************************************/
/* Shared serial communications processory (CPU5 ????)		  */
/**************************************************************/
READ16_HANDLER( namcos2_68k_serial_comms_ram_r );
WRITE16_HANDLER( namcos2_68k_serial_comms_ram_w );
READ16_HANDLER( namcos2_68k_serial_comms_ctrl_r );
WRITE16_HANDLER( namcos2_68k_serial_comms_ctrl_w );




/**************************************************************/
/* Shared protection/random number generator				  */
/**************************************************************/
READ16_HANDLER( namcos2_68k_key_r );
WRITE16_HANDLER( namcos2_68k_key_w );

/**************************************************************/
/* Non-shared memory custom IO device - IRQ/Inputs/Outputs	 */
/**************************************************************/

#define NAMCOS2_C148_0			0		/* 0x1c0000 */
#define NAMCOS2_C148_1			1		/* 0x1c2000 */
#define NAMCOS2_C148_2			2		/* 0x1c4000 */
#define NAMCOS2_C148_CPUIRQ 	3		/* 0x1c6000 */
#define NAMCOS2_C148_EXIRQ		4		/* 0x1c8000 */
#define NAMCOS2_C148_POSIRQ 	5		/* 0x1ca000 */
#define NAMCOS2_C148_SERIRQ 	6		/* 0x1cc000 */
#define NAMCOS2_C148_VBLANKIRQ	7		/* 0x1ce000 */


WRITE16_HANDLER( namcos2_68k_master_C148_w );
READ16_HANDLER( namcos2_68k_master_C148_r );
void namcos2_68k_master_posirq( int moog );

WRITE16_HANDLER( namcos2_68k_slave_C148_w );
READ16_HANDLER( namcos2_68k_slave_C148_r );
void namcos2_68k_slave_posirq( int moog );


/**************************************************************/
/* MASTER CPU RAM MEMORY									  */
/**************************************************************/


#define NAMCOS2_68K_MASTER_RAM_W	MWA16_BANK3, &namcos2_68k_master_ram
#define NAMCOS2_68K_MASTER_RAM_R	MRA16_BANK3


/**************************************************************/
/* SLAVE CPU RAM MEMORY 									  */
/**************************************************************/


#define NAMCOS2_68K_SLAVE_RAM_W 	MWA16_BANK4, &namcos2_68k_slave_ram
#define NAMCOS2_68K_SLAVE_RAM_R 	MRA16_BANK4


/**************************************************************/
/*	ROZ - Rotate & Zoom memory function handlers			  */
/**************************************************************/

WRITE16_HANDLER( namcos2_68k_roz_ctrl_w );
READ16_HANDLER( namcos2_68k_roz_ctrl_r );

WRITE16_HANDLER( namcos2_68k_roz_ram_w );
READ16_HANDLER( namcos2_68k_roz_ram_r );

/**************************************************************/
/*															  */
/**************************************************************/
#define BANKED_SOUND_ROM_R		MRA_BANK6
#define CPU3_ROM1				6			/* Bank number */



/**************************************************************/
/* Sound CPU support handlers - 6809						  */
/**************************************************************/



/**************************************************************/
/* MCU Specific support handlers - HD63705					  */
/**************************************************************/




