/***************************************************************************

X-Men

driver by Nicola Salmoria

***************************************************************************/
/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class xmen
{
	
	
	
	/***************************************************************************
	
	  EEPROM
	
	***************************************************************************/
	
	static int init_eeprom_count;
	
	
	static struct EEPROM_interface eeprom_interface =
	{
		7,				/* address bits */
		8,				/* data bits */
		"011000",		/*  read command */
		"011100",		/* write command */
		0,				/* erase command */
		"0100000000000",/* lock command */
		"0100110000000" /* unlock command */
	};
	
	public static NVRAMHandlerPtr nvram_handler_xmen  = new NVRAMHandlerPtr() { public void handler(mame_file file, int read_or_write){
		if (read_or_write)
			EEPROM_save(file);
		else
		{
			EEPROM_init(&eeprom_interface);
	
			if (file)
			{
				init_eeprom_count = 0;
				EEPROM_load(file);
			}
			else
				init_eeprom_count = 10;
		}
	} };
	
	static READ16_HANDLER( eeprom_r )
	{
		int res;
	
	logerror("%06x eeprom_r\n",activecpu_get_pc());
		/* bit 6 is EEPROM data */
		/* bit 7 is EEPROM ready */
		/* bit 14 is service button */
		res = (EEPROM_read_bit() << 6) | input_port_2_word_r(0,0);
		if (init_eeprom_count)
		{
			init_eeprom_count--;
			res &= 0xbfff;
		}
		return res;
	}
	
	static WRITE16_HANDLER( eeprom_w )
	{
	logerror("%06x: write %04x to 108000\n",activecpu_get_pc(),data);
		if (ACCESSING_LSB)
		{
			/* bit 0 = coin counter */
			coin_counter_w(0,data & 0x01);
	
			/* bit 2 is data */
			/* bit 3 is clock (active high) */
			/* bit 4 is cs (active low) */
			EEPROM_write_bit(data & 0x04);
			EEPROM_set_cs_line((data & 0x10) ? CLEAR_LINE : ASSERT_LINE);
			EEPROM_set_clock_line((data & 0x08) ? ASSERT_LINE : CLEAR_LINE);
		}
		if (ACCESSING_MSB)
		{
			/* bit 8 = enable sprite ROM reading */
			K053246_set_OBJCHA_line((data & 0x0100) ? ASSERT_LINE : CLEAR_LINE);
			/* bit 9 = enable char ROM reading through the video RAM */
			K052109_set_RMRD_line((data & 0x0200) ? ASSERT_LINE : CLEAR_LINE);
		}
	}
	
	static READ16_HANDLER( sound_status_r )
	{
		return soundlatch2_r(0);
	}
	
	static WRITE16_HANDLER( sound_cmd_w )
	{
		if (ACCESSING_LSB) {
			data &= 0xff;
			soundlatch_w(0, data);
			if(!Machine->sample_rate)
				if(data == 0xfc || data == 0xfe)
					soundlatch2_w(0, 0x7f);
		}
	}
	
	static WRITE16_HANDLER( sound_irq_w )
	{
		cpu_set_irq_line(1, 0, HOLD_LINE);
	}
	
	static WRITE16_HANDLER( xmen_18fa00_w )
	{
		if(ACCESSING_LSB) {
			/* bit 2 is interrupt enable */
			interrupt_enable_w(0,data & 0x04);
		}
	}
	
	static data8_t sound_curbank;
	
	static void sound_reset_bank(void)
	{
		cpu_setbank(4, memory_region(REGION_CPU2) + 0x10000 + (sound_curbank & 0x07) * 0x4000);
	}
	
	public static WriteHandlerPtr sound_bankswitch_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		sound_curbank = data;
		sound_reset_bank();
	} };
	
	
	static MEMORY_READ16_START( readmem )
		{ 0x000000, 0x03ffff, MRA16_ROM },
		{ 0x080000, 0x0fffff, MRA16_ROM },
		{ 0x100000, 0x100fff, K053247_word_r },
		{ 0x101000, 0x101fff, MRA16_RAM },
		{ 0x104000, 0x104fff, MRA16_RAM },
		{ 0x108054, 0x108055, sound_status_r },
		{ 0x10a000, 0x10a001, input_port_0_word_r },
		{ 0x10a002, 0x10a003, input_port_1_word_r },
		{ 0x10a004, 0x10a005, eeprom_r },
		{ 0x10a00c, 0x10a00d, K053246_word_r },
		{ 0x110000, 0x113fff, MRA16_RAM },	/* main RAM */
		{ 0x18c000, 0x197fff, K052109_lsb_r },
	MEMORY_END
	
	static MEMORY_WRITE16_START( writemem )
		{ 0x000000, 0x03ffff, MWA16_ROM },
		{ 0x080000, 0x0fffff, MWA16_ROM },
		{ 0x100000, 0x100fff, K053247_word_w },
		{ 0x101000, 0x101fff, MWA16_RAM },
		{ 0x104000, 0x104fff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
		{ 0x108000, 0x108001, eeprom_w },
		{ 0x108020, 0x108027, K053246_word_w },
		{ 0x10804c, 0x10804d, sound_cmd_w },
		{ 0x10804e, 0x10804f, sound_irq_w },
		{ 0x108060, 0x10807f, K053251_lsb_w },
		{ 0x10a000, 0x10a001, watchdog_reset16_w },
		{ 0x110000, 0x113fff, MWA16_RAM },	/* main RAM */
		{ 0x18fa00, 0x18fa01, xmen_18fa00_w },
		{ 0x18c000, 0x197fff, K052109_lsb_w },
	MEMORY_END
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK4 ),
		new Memory_ReadAddress( 0xc000, 0xdfff, MRA_RAM ),
		new Memory_ReadAddress( 0xe000, 0xe22f, K054539_0_r ),
		new Memory_ReadAddress( 0xec01, 0xec01, YM2151_status_port_0_r ),
		new Memory_ReadAddress( 0xf002, 0xf002, soundlatch_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xdfff, MWA_RAM ),
		new Memory_WriteAddress( 0xe000, 0xe22f, K054539_0_w ),
		new Memory_WriteAddress( 0xe800, 0xe800, YM2151_register_port_0_w ),
		new Memory_WriteAddress( 0xec01, 0xec01, YM2151_data_port_0_w ),
		new Memory_WriteAddress( 0xf000, 0xf000, soundlatch2_w ),
		new Memory_WriteAddress( 0xf800, 0xf800, sound_bankswitch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortHandlerPtr input_ports_xmen = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( xmen )
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER4 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER4 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER4 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER4 );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER4 );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER4 );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER4 );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_COIN4 );
	
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1 );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER3 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER3 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER3 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER3 );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER3 );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER3 );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER3 );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_COIN3 );
	
		PORT_START(); 	/* COIN  EEPROM and service */
		PORT_BIT( 0x003f, IP_ACTIVE_LOW, IPT_UNKNOWN );/* unused? */
		PORT_BIT( 0x0040, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* EEPROM data */
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNKNOWN );/* EEPROM status - always 1 */
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_START3 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_START4 );
		PORT_BIT( 0x3000, IP_ACTIVE_LOW, IPT_UNKNOWN );/* unused? */
		PORT_BITX(0x4000, IP_ACTIVE_LOW, IPT_SERVICE, DEF_STR( "Service_Mode") ); KEYCODE_F2, IP_JOY_NONE )
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_UNKNOWN );/* unused? */
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_xmen2p = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( xmen2p )
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_COIN2 );
	/*
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER4 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER4 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER4 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER4 );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER4 );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER4 );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER4 );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_COIN4 );
	*/
	
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1 );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_COIN1 );
	/*
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER3 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER3 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER3 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER3 );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER3 );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER3 );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER3 );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_COIN3 );
	*/
	
		PORT_START(); 	/* COIN  EEPROM and service */
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_SERVICE2 );
		PORT_BIT( 0x003c, IP_ACTIVE_LOW, IPT_UNKNOWN );/* unused? */
		PORT_BIT( 0x0040, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* EEPROM data */
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNKNOWN );/* EEPROM status - always 1 */
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x3000, IP_ACTIVE_LOW, IPT_UNKNOWN );/* unused? */
		PORT_BITX(0x4000, IP_ACTIVE_LOW, IPT_SERVICE, DEF_STR( "Service_Mode") ); KEYCODE_F2, IP_JOY_NONE )
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_UNKNOWN );/* unused? */
	INPUT_PORTS_END(); }}; 
	
	
	
	static struct YM2151interface ym2151_interface =
	{
		1,			/* 1 chip */
		4000000,	/* 4 MHz? (hand tuned) */
		{ YM3012_VOL(20,MIXER_PAN_LEFT,20,MIXER_PAN_RIGHT) },
		{ 0 }
	};
	
	static struct K054539interface k054539_interface =
	{
		1,			/* 1 chip */
		48000,
		{ REGION_SOUND1 },
		{ { 80, 80 } },
		{ 0 }		/* The YM does not seem to be connected to the 539 analog input */
	};
	
	
	
	public static InterruptHandlerPtr xmen_interrupt = new InterruptHandlerPtr() {public void handler(){
		if (cpu_getiloops() == 0) irq5_line_hold();
		else irq3_line_hold();
	} };
	
	public static MachineHandlerPtr machine_driver_xmen = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 16000000)	/* ? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(xmen_interrupt,2)
	
		MDRV_CPU_ADD(Z80,2*3579545)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* ????? */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		MDRV_NVRAM_HANDLER(xmen)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_HAS_SHADOWS)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(14*8, (64-14)*8-1, 2*8, 30*8-1 )
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(xmen)
		MDRV_VIDEO_UPDATE(xmen)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(K054539, k054539_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadHandlerPtr rom_xmen = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x100000, REGION_CPU1, 0 )
		ROM_LOAD16_BYTE( "065ubb04.10d",  0x00000, 0x20000, CRC(f896c93b) SHA1(0bee89fe4d36a9b2ded864770198eb2df6903580) )
		ROM_LOAD16_BYTE( "065ubb05.10f",  0x00001, 0x20000, CRC(e02e5d64) SHA1(9838c1cf9862db3ca70a23ef5f3c5883729c4e0c) )
		ROM_LOAD16_BYTE( "xmen17g.bin",   0x80000, 0x40000, CRC(b31dc44c) SHA1(4bdac05826b4d6d4fe46686ede5190e2f73eefc5) )
		ROM_LOAD16_BYTE( "xmen17j.bin",   0x80001, 0x40000, CRC(13842fe6) SHA1(b61f094eb94336edb8708d3437ead9b853b2d6e6) )
	
		ROM_REGION( 0x30000, REGION_CPU2, 0 )		/* 64k+128k fpr sound cpu */
		ROM_LOAD( "065-a01.6f",   0x00000, 0x20000, CRC(147d3a4d) SHA1(a14409fe991e803b9e7812303e3a9ebd857d8b01) )
		ROM_RELOAD(               0x10000, 0x20000 )
	
		ROM_REGION( 0x200000, REGION_GFX1, 0 )	/* graphics (addressable by the main CPU) */
		ROM_LOAD( "xmen1l.bin",   0x000000, 0x100000, CRC(6b649aca) SHA1(2595f314517738e8614facf578cc951a6c36a180) )	/* tiles */
		ROM_LOAD( "xmen1h.bin",   0x100000, 0x100000, CRC(c5dc8fc4) SHA1(9887cb002c8b72be7ce933cb397f00cdc5506c8c) )
	
		ROM_REGION( 0x400000, REGION_GFX2, 0 )	/* graphics (addressable by the main CPU) */
		ROM_LOAD( "xmen12l.bin",  0x000000, 0x100000, CRC(ea05d52f) SHA1(7f2c14f907355856fb94e3a67b73aa1919776835) )	/* sprites */
		ROM_LOAD( "xmen17l.bin",  0x100000, 0x100000, CRC(96b91802) SHA1(641943557b59b91f0edd49ec8a73cef7d9268b32) )
		ROM_LOAD( "xmen22h.bin",  0x200000, 0x100000, CRC(321ed07a) SHA1(5b00ed676daeea974bdce6701667cfe573099dad) )
		ROM_LOAD( "xmen22l.bin",  0x300000, 0x100000, CRC(46da948e) SHA1(168ac9178ee5bad5931557fb549e1237971d7839) )
	
		ROM_REGION( 0x200000, REGION_SOUND1, 0 )	/* samples for the 054539 */
		ROM_LOAD( "xmenc25.bin",  0x000000, 0x200000, CRC(5adbcee0) SHA1(435feda697193bc51db80eba46be474cbbc1de4b) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_xmen6p = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x100000, REGION_CPU1, 0 )
		ROM_LOAD16_BYTE( "065ucb04.10d",  0x00000, 0x20000, CRC(0f09b8e0) SHA1(79f4d86d8ec45b39e34ddf45860bea0c74dae183) )
		ROM_LOAD16_BYTE( "065ucb05.10f",  0x00001, 0x20000, CRC(867becbf) SHA1(3f81f4dbd289f98b78d7821a8925598c771f01ef) )
		ROM_LOAD16_BYTE( "xmen17g.bin",   0x80000, 0x40000, CRC(b31dc44c) SHA1(4bdac05826b4d6d4fe46686ede5190e2f73eefc5) )
		ROM_LOAD16_BYTE( "xmen17j.bin",   0x80001, 0x40000, CRC(13842fe6) SHA1(b61f094eb94336edb8708d3437ead9b853b2d6e6) )
	
		ROM_REGION( 0x30000, REGION_CPU2, 0 )		/* 64k+128k fpr sound cpu */
		ROM_LOAD( "065-a01.6f",   0x00000, 0x20000, CRC(147d3a4d) SHA1(a14409fe991e803b9e7812303e3a9ebd857d8b01) )
		ROM_RELOAD(               0x10000, 0x20000 )
	
		ROM_REGION( 0x200000, REGION_GFX1, 0 )	/* graphics (addressable by the main CPU) */
		ROM_LOAD( "xmen1l.bin",   0x000000, 0x100000, CRC(6b649aca) SHA1(2595f314517738e8614facf578cc951a6c36a180) )	/* tiles */
		ROM_LOAD( "xmen1h.bin",   0x100000, 0x100000, CRC(c5dc8fc4) SHA1(9887cb002c8b72be7ce933cb397f00cdc5506c8c) )
	
		ROM_REGION( 0x400000, REGION_GFX2, 0 )	/* graphics (addressable by the main CPU) */
		ROM_LOAD( "xmen12l.bin",  0x000000, 0x100000, CRC(ea05d52f) SHA1(7f2c14f907355856fb94e3a67b73aa1919776835) )	/* sprites */
		ROM_LOAD( "xmen17l.bin",  0x100000, 0x100000, CRC(96b91802) SHA1(641943557b59b91f0edd49ec8a73cef7d9268b32) )
		ROM_LOAD( "xmen22h.bin",  0x200000, 0x100000, CRC(321ed07a) SHA1(5b00ed676daeea974bdce6701667cfe573099dad) )
		ROM_LOAD( "xmen22l.bin",  0x300000, 0x100000, CRC(46da948e) SHA1(168ac9178ee5bad5931557fb549e1237971d7839) )
	
		ROM_REGION( 0x200000, REGION_SOUND1, 0 )	/* samples for the 054539 */
		ROM_LOAD( "xmenc25.bin",  0x000000, 0x200000, CRC(5adbcee0) SHA1(435feda697193bc51db80eba46be474cbbc1de4b) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_xmen2p = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x100000, REGION_CPU1, 0 )
		ROM_LOAD16_BYTE( "065aaa04.10d",  0x00000, 0x20000, CRC(7f8b27c2) SHA1(052db1f47671564a440544a41fc397a19d1aff3a) )
		ROM_LOAD16_BYTE( "065aaa04.10f",  0x00001, 0x20000, CRC(841ed636) SHA1(33f96022ce3dae9b49eb51fd4e8f7387a1777002) )
		ROM_LOAD16_BYTE( "xmen17g.bin",   0x80000, 0x40000, CRC(b31dc44c) SHA1(4bdac05826b4d6d4fe46686ede5190e2f73eefc5) )
		ROM_LOAD16_BYTE( "xmen17j.bin",   0x80001, 0x40000, CRC(13842fe6) SHA1(b61f094eb94336edb8708d3437ead9b853b2d6e6) )
	
		ROM_REGION( 0x30000, REGION_CPU2, 0 )		/* 64k+128k fpr sound cpu */
		ROM_LOAD( "065-a01.6f",   0x00000, 0x20000, CRC(147d3a4d) SHA1(a14409fe991e803b9e7812303e3a9ebd857d8b01) )
		ROM_RELOAD(               0x10000, 0x20000 )
	
		ROM_REGION( 0x200000, REGION_GFX1, 0 )	/* graphics (addressable by the main CPU) */
		ROM_LOAD( "xmen1l.bin",   0x000000, 0x100000, CRC(6b649aca) SHA1(2595f314517738e8614facf578cc951a6c36a180) )	/* tiles */
		ROM_LOAD( "xmen1h.bin",   0x100000, 0x100000, CRC(c5dc8fc4) SHA1(9887cb002c8b72be7ce933cb397f00cdc5506c8c) )
	
		ROM_REGION( 0x400000, REGION_GFX2, 0 )	/* graphics (addressable by the main CPU) */
		ROM_LOAD( "xmen12l.bin",  0x000000, 0x100000, CRC(ea05d52f) SHA1(7f2c14f907355856fb94e3a67b73aa1919776835) )	/* sprites */
		ROM_LOAD( "xmen17l.bin",  0x100000, 0x100000, CRC(96b91802) SHA1(641943557b59b91f0edd49ec8a73cef7d9268b32) )
		ROM_LOAD( "xmen22h.bin",  0x200000, 0x100000, CRC(321ed07a) SHA1(5b00ed676daeea974bdce6701667cfe573099dad) )
		ROM_LOAD( "xmen22l.bin",  0x300000, 0x100000, CRC(46da948e) SHA1(168ac9178ee5bad5931557fb549e1237971d7839) )
	
		ROM_REGION( 0x200000, REGION_SOUND1, 0 )	/* samples for the 054539 */
		ROM_LOAD( "xmenc25.bin",  0x000000, 0x200000, CRC(5adbcee0) SHA1(435feda697193bc51db80eba46be474cbbc1de4b) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_xmen2pj = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x100000, REGION_CPU1, 0 )
		ROM_LOAD16_BYTE( "065jaa04.10d",  0x00000, 0x20000, CRC(66746339) SHA1(8cc5f5deb4178b0444ffc5974940a30cb003114e) )
		ROM_LOAD16_BYTE( "065jaa05.10f",  0x00001, 0x20000, CRC(1215b706) SHA1(b746dedab9c509b5cd941f0f4ddd3709e8a58cce) )
		ROM_LOAD16_BYTE( "xmen17g.bin",   0x80000, 0x40000, CRC(b31dc44c) SHA1(4bdac05826b4d6d4fe46686ede5190e2f73eefc5) )
		ROM_LOAD16_BYTE( "xmen17j.bin",   0x80001, 0x40000, CRC(13842fe6) SHA1(b61f094eb94336edb8708d3437ead9b853b2d6e6) )
	
		ROM_REGION( 0x30000, REGION_CPU2, 0 )		/* 64k+128k fpr sound cpu */
		ROM_LOAD( "065-a01.6f",   0x00000, 0x20000, CRC(147d3a4d) SHA1(a14409fe991e803b9e7812303e3a9ebd857d8b01) )
		ROM_RELOAD(               0x10000, 0x20000 )
	
		ROM_REGION( 0x200000, REGION_GFX1, 0 )	/* graphics (addressable by the main CPU) */
		ROM_LOAD( "xmen1l.bin",   0x000000, 0x100000, CRC(6b649aca) SHA1(2595f314517738e8614facf578cc951a6c36a180) )	/* tiles */
		ROM_LOAD( "xmen1h.bin",   0x100000, 0x100000, CRC(c5dc8fc4) SHA1(9887cb002c8b72be7ce933cb397f00cdc5506c8c) )
	
		ROM_REGION( 0x400000, REGION_GFX2, 0 )	/* graphics (addressable by the main CPU) */
		ROM_LOAD( "xmen12l.bin",  0x000000, 0x100000, CRC(ea05d52f) SHA1(7f2c14f907355856fb94e3a67b73aa1919776835) )	/* sprites */
		ROM_LOAD( "xmen17l.bin",  0x100000, 0x100000, CRC(96b91802) SHA1(641943557b59b91f0edd49ec8a73cef7d9268b32) )
		ROM_LOAD( "xmen22h.bin",  0x200000, 0x100000, CRC(321ed07a) SHA1(5b00ed676daeea974bdce6701667cfe573099dad) )
		ROM_LOAD( "xmen22l.bin",  0x300000, 0x100000, CRC(46da948e) SHA1(168ac9178ee5bad5931557fb549e1237971d7839) )
	
		ROM_REGION( 0x200000, REGION_SOUND1, 0 )	/* samples for the 054539 */
		ROM_LOAD( "xmenc25.bin",  0x000000, 0x200000, CRC(5adbcee0) SHA1(435feda697193bc51db80eba46be474cbbc1de4b) )
	ROM_END(); }}; 
	
	
	
	public static DriverInitHandlerPtr init_xmen  = new DriverInitHandlerPtr() { public void handler(){
		konami_rom_deinterleave_2(REGION_GFX1);
		konami_rom_deinterleave_4(REGION_GFX2);
	
		state_save_register_UINT8("main", 0, "sound bank", &sound_curbank, 1);
		state_save_register_func_postload(sound_reset_bank);
	} };
	
	public static DriverInitHandlerPtr init_xmen6p  = new DriverInitHandlerPtr() { public void handler(){
		data16_t *rom = (data16_t *)memory_region(REGION_CPU1);
	
		rom[0x21a6/2] = 0x4e71;
		rom[0x21a8/2] = 0x4e71;
		rom[0x21aa/2] = 0x4e71;
	
		init_xmen();
	} };
	
	
	
	public static GameDriver driver_xmen	   = new GameDriver("1992"	,"xmen"	,"xmen.java"	,rom_xmen,null	,machine_driver_xmen	,input_ports_xmen	,init_xmen	,ROT0, "Konami", "X-Men (US 4 Players)" )
	public static GameDriver driver_xmen2p	   = new GameDriver("1992"	,"xmen2p"	,"xmen.java"	,rom_xmen2p,driver_xmen	,machine_driver_xmen	,input_ports_xmen2p	,init_xmen	,ROT0, "Konami", "X-Men (World 2 Players)" )
	public static GameDriver driver_xmen2pj	   = new GameDriver("1992"	,"xmen2pj"	,"xmen.java"	,rom_xmen2pj,driver_xmen	,machine_driver_xmen	,input_ports_xmen2p	,init_xmen	,ROT0, "Konami", "X-Men (Japan 2 Players)" )
	public static GameDriver driver_xmen6p	   = new GameDriver("1992"	,"xmen6p"	,"xmen.java"	,rom_xmen6p,driver_xmen	,machine_driver_xmen	,input_ports_xmen	,init_xmen6p	,ROT0, "Konami", "X-Men (US 6 Players)", GAME_NOT_WORKING )
}
