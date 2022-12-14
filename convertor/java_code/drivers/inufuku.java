/******************************************************************************

	Video Hardware for Video System Games.

	Quiz & Variety Sukusuku Inufuku (Japan)
	(c)1998 Video System Co.,Ltd.

	Driver by Takahiro Nogi <nogi@kt.rim.or.jp> 2003/08/09 -

	based on other Video System drivers

******************************************************************************/
/******************************************************************************

Quiz & Variety Sukusuku Inufuku
(c)1998 Video System

VSBB-31-1

CPU  : MC68HC000P-16
Sound: TMPZ84C000AP-8 YM2610 YM3016
OSC  : 32.0000MHz 14.31818MHz

ROMs:
U107.BIN     - Sound Program (27C1001)

U146.BIN     - Main Programs (27C240)
U147.BIN     |
LHMN5L28.148 / (32M Mask)

Others:
93C46 (EEPROM)
UMAG1 (ALTERA MAX EPM7128ELC84-10 BG9625)
PLD00?? (ALTERA EPM7032LC44-15 BA9631)
002 (PALCE16V8-10PC)
003 (PALCE16V8-15PC)

Custom Chips:
VS920A
VS920E
VS9210
VS9108 (Fujitsu CG10103)
(blank pattern for VS9210 and VS9108)

VSBB31-ROM

ROMs:
LHMN5KU6.U53 - 32M SOP Mask ROMs
LHMN5KU8.U40 |
LHMN5KU7.U8  |
LHMN5KUB.U34 |
LHMN5KUA.U36 |
LHMN5KU9.U38 /

******************************************************************************/
/******************************************************************************

TODO:

- User must initialize NVRAM at first boot in test mode (factory settings).

- Sometimes, sounds are not played (especially SFX), but this is a bug of real machine.

- Sound Code 0x08 remains unknown.

- Priority of tests and sprites seems to be correct, but I may have mistaken.

******************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class inufuku
{
	
	
	
	data16_t *inufuku_bg_videoram;
	data16_t *inufuku_bg_rasterram;
	data16_t *inufuku_text_videoram;
	data16_t *inufuku_spriteram1;
	data16_t *inufuku_spriteram2;
	size_t inufuku_spriteram1_size;
	static unsigned short pending_command;
	
	WRITE16_HANDLER( inufuku_paletteram_w );
	READ16_HANDLER( inufuku_bg_videoram_r );
	WRITE16_HANDLER( inufuku_bg_videoram_w );
	READ16_HANDLER( inufuku_text_videoram_r );
	WRITE16_HANDLER( inufuku_text_videoram_w );
	WRITE16_HANDLER( inufuku_palettereg_w );
	WRITE16_HANDLER( inufuku_scrollreg_w );
	
	
	/******************************************************************************
	
		Sound CPU interface
	
	******************************************************************************/
	
	static WRITE16_HANDLER( inufuku_soundcommand_w )
	{
		if (ACCESSING_LSB) {
	
			/* hack... sound doesn't work otherwise */
			if (data == 0x08) return;
	
			pending_command = 1;
			soundlatch_w(0, data & 0xff);
			cpu_set_irq_line(1, IRQ_LINE_NMI, PULSE_LINE);
		}
	}
	
	public static WriteHandlerPtr pending_command_clear_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		pending_command = 0;
	} };
	
	public static WriteHandlerPtr inufuku_soundrombank_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		unsigned char *ROM = memory_region(REGION_CPU2) + 0x10000;
	
		cpu_setbank(1, ROM + (data & 0x03) * 0x8000);
	} };
	
	
	/******************************************************************************
	
		Machine initialization / Driver initialization
	
	******************************************************************************/
	
	public static MachineInitHandlerPtr machine_init_inufuku  = new MachineInitHandlerPtr() { public void handler(){
		;
	} };
	
	public static DriverInitHandlerPtr init_inufuku  = new DriverInitHandlerPtr() { public void handler(){
		pending_command = 1;
		inufuku_soundrombank_w(0, 0);
	} };
	
	
	/******************************************************************************
	
		Input/Output port interface
	
	******************************************************************************/
	
	static READ16_HANDLER( inufuku_eeprom_r )
	{
		unsigned short soundflag;
		unsigned short eeprom;
		unsigned short inputport;
	
		soundflag = pending_command ? 0x0000 : 0x0080;	// bit7
		eeprom = (EEPROM_read_bit() & 1) << 6;			// bit6
		inputport = readinputport(4) & 0xff3f;			// bit5-0
	
		return (soundflag | eeprom | inputport);
	}
	
	static WRITE16_HANDLER( inufuku_eeprom_w )
	{
		// latch the bit
		EEPROM_write_bit(data & 0x0800);
	
		// reset line asserted: reset.
		EEPROM_set_cs_line((data & 0x2000) ? CLEAR_LINE : ASSERT_LINE);
	
		// clock line asserted: write latch or select next bit to read
		EEPROM_set_clock_line((data & 0x1000) ? ASSERT_LINE : CLEAR_LINE);
	}
	
	
	/******************************************************************************
	
		Main CPU memory handlers
	
	******************************************************************************/
	
	static MEMORY_READ16_START( inufuku_readmem )
		{ 0x000000, 0x0fffff, MRA16_ROM },				// main rom
	
		{ 0x180000, 0x180001, input_port_0_word_r },
		{ 0x180002, 0x180003, input_port_1_word_r },
		{ 0x180004, 0x180005, input_port_2_word_r },
		{ 0x180006, 0x180007, input_port_3_word_r },
		{ 0x180008, 0x180009, inufuku_eeprom_r },		// eeprom + input_port_4_word_r
		{ 0x18000a, 0x18000b, input_port_5_word_r },
	
		{ 0x300000, 0x301fff, MRA16_RAM },				// palette ram
		{ 0x400000, 0x401fff, inufuku_bg_videoram_r },	// bg ram
		{ 0x402000, 0x403fff, inufuku_text_videoram_r },// text ram
		{ 0x580000, 0x580fff, MRA16_RAM },				// sprite table + sprite attribute
		{ 0x600000, 0x61ffff, MRA16_RAM },				// cell table
	
		{ 0x800000, 0xbfffff, MRA16_ROM },				// data rom
		{ 0xfd0000, 0xfdffff, MRA16_RAM },				// work ram
	MEMORY_END
	
	static MEMORY_WRITE16_START( inufuku_writemem )
		{ 0x000000, 0x0fffff, MWA16_ROM },				// main rom
	
		{ 0x100000, 0x100007, MWA16_NOP },				// ?
		{ 0x200000, 0x200001, inufuku_eeprom_w },		// eeprom
		{ 0x280000, 0x280001, inufuku_soundcommand_w },	// sound command
	
		{ 0x300000, 0x301fff, paletteram16_xGGGGGBBBBBRRRRR_word_w, &paletteram16 },		// palette ram
		{ 0x380000, 0x3801ff, MWA16_RAM, &inufuku_bg_rasterram },							// bg raster ram
		{ 0x400000, 0x401fff, inufuku_bg_videoram_w, &inufuku_bg_videoram },				// bg ram
		{ 0x402000, 0x403fff, inufuku_text_videoram_w, &inufuku_text_videoram },			// text ram
		{ 0x580000, 0x580fff, MWA16_RAM, &inufuku_spriteram1, &inufuku_spriteram1_size },	// sprite table + sprite attribute
		{ 0x600000, 0x61ffff, MWA16_RAM, &inufuku_spriteram2 },								// cell table
	
		{ 0x780000, 0x780013, inufuku_palettereg_w },	// bg & text palettebank register
		{ 0x7a0000, 0x7a0023, inufuku_scrollreg_w },	// bg & text scroll register
		{ 0x7e0000, 0x7e0001, MWA16_NOP },				// ?
	
		{ 0x800000, 0xbfffff, MWA16_ROM },				// data rom
		{ 0xfd0000, 0xfdffff, MWA16_RAM },				// work ram
	MEMORY_END
	
	
	/******************************************************************************
	
		Sound CPU memory handlers
	
	******************************************************************************/
	
	public static Memory_ReadAddress inufuku_readmem_sound[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x77ff, MRA_ROM ),
		new Memory_ReadAddress( 0x7800, 0x7fff, MRA_RAM ),
		new Memory_ReadAddress( 0x8000, 0xffff, MRA_BANK1 ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress inufuku_writemem_sound[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x77ff, MWA_ROM ),
		new Memory_WriteAddress( 0x7800, 0x7fff, MWA_RAM ),
		new Memory_WriteAddress( 0x8000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort inufuku_readport_sound[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x04, 0x04, soundlatch_r ),
		new IO_ReadPort( 0x08, 0x08, YM2610_status_port_0_A_r ),
		new IO_ReadPort( 0x09, 0x09, YM2610_read_port_0_r ),
		new IO_ReadPort( 0x0a, 0x0a, YM2610_status_port_0_B_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort inufuku_writeport_sound[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, inufuku_soundrombank_w ),
		new IO_WritePort( 0x04, 0x04, pending_command_clear_w ),
		new IO_WritePort( 0x08, 0x08, YM2610_control_port_0_A_w ),
		new IO_WritePort( 0x09, 0x09, YM2610_data_port_0_A_w ),
		new IO_WritePort( 0x0a, 0x0a, YM2610_control_port_0_B_w ),
		new IO_WritePort( 0x0b, 0x0b, YM2610_data_port_0_B_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	/******************************************************************************
	
		Port definitions
	
	******************************************************************************/
	
	static InputPortHandlerPtr input_ports_inufuku = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( inufuku )
		PORT_START(); 	// 0
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1        | IPF_PLAYER1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2        | IPF_PLAYER1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3        | IPF_PLAYER1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON4        | IPF_PLAYER1 );
	
		PORT_START(); 	// 1
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1        | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2        | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3        | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON4        | IPF_PLAYER2 );
	
		PORT_START(); 	// 2
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 );
		PORT_SERVICE_NO_TOGGLE( 0x10, IP_ACTIVE_LOW )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_TILT );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	// 3
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_PLAYER4 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_PLAYER4 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_PLAYER4 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER4 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1        | IPF_PLAYER4 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2        | IPF_PLAYER4 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3        | IPF_PLAYER4 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON4        | IPF_PLAYER4 );
	
		PORT_START(); 	// 4
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN4 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START3 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START4 );
		PORT_DIPNAME( 0x10, 0x10, "3P/4P" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SPECIAL );// pending sound command
	
		PORT_START(); 	// 5
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_PLAYER3 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_PLAYER3 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_PLAYER3 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER3 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1        | IPF_PLAYER3 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2        | IPF_PLAYER3 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3        | IPF_PLAYER3 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON4        | IPF_PLAYER3 );
	INPUT_PORTS_END(); }}; 
	
	
	/******************************************************************************
	
		Graphics definitions
	
	******************************************************************************/
	
	static GfxLayout tilelayout = new GfxLayout
	(
		8, 8,
		RGN_FRAC(1, 1),
		8,
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 1*8, 0*8, 3*8, 2*8, 5*8, 4*8, 7*8, 6*8 },
		new int[] { 0*64, 1*64, 2*64, 3*64, 4*64, 5*64, 6*64, 7*64 },
		64*8
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16, 16,
		RGN_FRAC(1, 1),
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 2*4, 3*4, 0*4, 1*4, 6*4, 7*4, 4*4, 5*4,
				10*4, 11*4, 8*4, 9*4, 14*4, 15*4, 12*4, 13*4 },
		new int[] { 0*64, 1*64, 2*64, 3*64, 4*64, 5*64, 6*64, 7*64,
				8*64, 9*64, 10*64, 11*64, 12*64, 13*64, 14*64, 15*64 },
		128*8
	);
	
	static GfxDecodeInfo inufuku_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, tilelayout,    0, 256*16 ),	// bg
		new GfxDecodeInfo( REGION_GFX2, 0, tilelayout,    0, 256*16 ),	// text
		new GfxDecodeInfo( REGION_GFX3, 0, spritelayout,  0, 256*16 ),	// sprite
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	/******************************************************************************
	
		Sound definitions
	
	******************************************************************************/
	
	static void irqhandler(int irq)
	{
		cpu_set_irq_line(1, 0, irq ? ASSERT_LINE : CLEAR_LINE);
	}
	
	static struct YM2610interface ym2610_interface =
	{
		1,
		32000000/4,							/* 8.00 MHz */
		{ 50 },
		{ 0 },
		{ 0 },
		{ 0 },
		{ 0 },
		{ irqhandler },
		{ 0 },
		{ REGION_SOUND1 },
		{ YM3012_VOL(75, MIXER_PAN_CENTER, 75, MIXER_PAN_CENTER) }
	};
	
	
	/******************************************************************************
	
		Machine driver
	
	******************************************************************************/
	
	public static MachineHandlerPtr machine_driver_inufuku = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 32000000/2)	/* 16.00 MHz */
		MDRV_CPU_MEMORY(inufuku_readmem, inufuku_writemem)
		MDRV_CPU_VBLANK_INT(irq1_line_hold, 1)
	
		MDRV_CPU_ADD(Z80, 32000000/4)		/* 8.00 MHz */
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(inufuku_readmem_sound, inufuku_writemem_sound)
		MDRV_CPU_PORTS(inufuku_readport_sound, inufuku_writeport_sound)
									/* IRQs are triggered by the YM2610 */
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(inufuku)
		MDRV_NVRAM_HANDLER(93C46)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(2048, 256)
		MDRV_VISIBLE_AREA(0, 319-1, 1, 224-1)
		MDRV_GFXDECODE(inufuku_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(4096)
	
		MDRV_VIDEO_START(inufuku)
		MDRV_VIDEO_UPDATE(inufuku)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2610, ym2610_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	/******************************************************************************
	
		ROM definitions
	
	******************************************************************************/
	
	static RomLoadHandlerPtr rom_inufuku = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x1000000, REGION_CPU1, 0 )	// main cpu + data
		ROM_LOAD16_WORD_SWAP( "u147.bin",     0x0000000, 0x080000, CRC(ab72398c) SHA1(f5dc266ffa936ea6528b46a34113f5e2f8141d71) )
		ROM_LOAD16_WORD_SWAP( "u146.bin",     0x0080000, 0x080000, CRC(e05e9bd4) SHA1(af0fdf31c2bdf851bf15c9de725dcbbb58464d54) )
		ROM_LOAD16_WORD_SWAP( "lhmn5l28.148", 0x0800000, 0x400000, CRC(802d17e7) SHA1(43b26efea65fd051c094d19784cb977ced39a1a0) )
	
		ROM_REGION( 0x0030000, REGION_CPU2, 0 )	// sound cpu
		ROM_LOAD( "u107.bin", 0x0000000, 0x020000, CRC(1744ef90) SHA1(e019f4ca83e21aa25710cc0ca40ffe765c7486c9) )
		ROM_RELOAD( 0x010000, 0x020000 )
	
		ROM_REGION( 0x0400000, REGION_GFX1, ROMREGION_DISPOSE )	// bg
		ROM_LOAD16_WORD_SWAP( "lhmn5ku8.u40", 0x0000000, 0x400000, CRC(8cbca80a) SHA1(063e9be97f5a1f021f8326f2994b51f9af5e1eaf) )
	
		ROM_REGION( 0x0400000, REGION_GFX2, ROMREGION_DISPOSE )	// text
		ROM_LOAD16_WORD_SWAP( "lhmn5ku7.u8",  0x0000000, 0x400000, CRC(a6c0f07f) SHA1(971803d1933d8296767d8766ea9f04dcd6ab065c) )
	
		ROM_REGION( 0x0c00000, REGION_GFX3, ROMREGION_DISPOSE )	// sprite
		ROM_LOAD16_WORD_SWAP( "lhmn5kub.u34", 0x0000000, 0x400000, CRC(7753a7b6) SHA1(a2e8747ce83ea5a57e2fe62f2452de355d7f48b6) )
		ROM_LOAD16_WORD_SWAP( "lhmn5kua.u36", 0x0400000, 0x400000, CRC(1ac4402a) SHA1(c15acc6fce4fe0b54e92d14c31a1bd78acf2c8fc) )
		ROM_LOAD16_WORD_SWAP( "lhmn5ku9.u38", 0x0800000, 0x400000, CRC(e4e9b1b6) SHA1(4d4ad85fbe6a442d4f8cafad748bcae4af6245b7) )
	
		ROM_REGION( 0x0400000, REGION_SOUND1, 0 )	// adpcm data
		ROM_LOAD( "lhmn5ku6.u53", 0x0000000, 0x400000, CRC(b320c5c9) SHA1(7c99da2d85597a3c008ed61a3aa5f47ad36186ec) )
	ROM_END(); }}; 
	
	
	/******************************************************************************
	
		Game drivers
	
	******************************************************************************/
	
	public static GameDriver driver_inufuku	   = new GameDriver("1998"	,"inufuku"	,"inufuku.java"	,rom_inufuku,null	,machine_driver_inufuku	,input_ports_inufuku	,init_inufuku	,ROT0, "Video System Co.", "Quiz & Variety Sukusuku Inufuku (Japan)", GAME_NO_COCKTAIL )
}
