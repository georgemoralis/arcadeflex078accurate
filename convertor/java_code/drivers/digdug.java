/***************************************************************************

Dig Dug

driver by Aaron Giles


-----------+---+-----------------+-------------------------
   hex     |r/w| D D D D D D D D |
 location  |   | 7 6 5 4 3 2 1 0 | function
-----------+---+-----------------+-------------------------
0000-3FFF  | R | D D D D D D D D | CPU 1 rom (16k)
0000-1FFF  | R | D D D D D D D D | CPU 2 rom (8k)
0000-0FFF  | R | D D D D D D D D | CPU 3 rom (4k)
-----------+---+-----------------+-------------------------
6800-680F  | W | - - - - D D D D | Audio control
6810-681F  | W | - - - - D D D D | Audio control
-----------+---+-----------------+-------------------------
6820       | W | - - - - - - - D | 0 = Reset IRQ1(latched)
6821       | W | - - - - - - - D | 0 = Reset IRQ2(latched)
6822       | W | - - - - - - - D | 0 = Reset NMI3(latched)
6823       | W | - - - - - - - D | 0 = Reset #2,#3 CPU
6825       | W | - - - - - - - D | custom 53 mode1
6826       | W | - - - - - - - D | custom 53 mode2
6827       | W | - - - - - - - D | custom 53 mode3
-----------+---+-----------------+-------------------------
6830       | W |                 | watchdog reset
-----------+---+-----------------+-------------------------
7000       |R/W| D D D D D D D D | custom 06 Data
7100       |R/W| D D D D D D D D | custom 06 Command
-----------+---+-----------------+-------------------------
8000-87FF  |R/W| D D D D D D D D | 2k playfeild RAM
-----------+---+-----------------+-------------------------
8B80-8BFF  |R/W| D D D D D D D D | 1k sprite RAM (PIC,COL)
9380-93FF  |R/W| D D D D D D D D | 1k sprite RAM (VPOS,HPOS)
9B80-9BFF  |R/W| D D D D D D D D | 1k sprite RAM (FLIP)
-----------+---+-----------------+-------------------------
A000       | W | - - - - - - - D | playfield select
A001       | W | - - - - - - - D | playfield select
A002       | W | - - - - - - - D | Alpha color select
A003       | W | - - - - - - - D | playfield enable
A004       | W | - - - - - - - D | playfield color select
A005       | W | - - - - - - - D | playfield color select
A007       | W | - - - - - - - D | flip video
-----------+---+-----------------+-------------------------
B800-B83F  | W | D D D D D D D D | write EAROM addr,  data
B800       | R | D D D D D D D D | read  EAROM data
B840       | W |         D D D D | write EAROM control
-----------+---+-----------------+-------------------------

Dig Dug memory map (preliminary)

CPU #1:
0000-3fff ROM
CPU #2:
0000-1fff ROM
CPU #3:
0000-0fff ROM
ALL CPUS:
8000-83ff Video RAM
8400-87ff Color RAM
8b80-8bff sprite code/color
9380-93ff sprite position
9b80-9bff sprite control
8800-9fff RAM

read:
6800-6807 dip switches (only bits 0 and 1 are used - bit 0 is DSW1, bit 1 is DSW2)
          dsw1:
            bit 6-7 lives
            bit 3-5 bonus
            bit 0-2 coins per play
		  dsw2: (bootleg version, the original version is slightly different)
		    bit 7 cocktail/upright (1 = upright)
            bit 6 ?
            bit 5 RACK TEST
            bit 4 pause (0 = paused, 1 = not paused)
            bit 3 ?
            bit 2 ?
            bit 0-1 difficulty
7000-     custom IO chip return values
7100      custom IO chip status ($10 = command executed)

write:
6805      sound voice 1 waveform (nibble)
6811-6813 sound voice 1 frequency (nibble)
6815      sound voice 1 volume (nibble)
680a      sound voice 2 waveform (nibble)
6816-6818 sound voice 2 frequency (nibble)
681a      sound voice 2 volume (nibble)
680f      sound voice 3 waveform (nibble)
681b-681d sound voice 3 frequency (nibble)
681f      sound voice 3 volume (nibble)
6820      cpu #1 irq acknowledge/enable
6821      cpu #2 irq acknowledge/enable
6822      cpu #3 nmi acknowledge/enable
6823      if 0, halt CPU #2 and #3
6830      Watchdog reset?
7000-     custom IO chip parameters
7100      custom IO chip command (see machine/galaga.c for more details)
a000-a002 starfield scroll direction/speed (only bit 0 is significant)
a003-a005 starfield blink?
a007      flip screen

Interrupts:
CPU #1 IRQ mode 1
       NMI is triggered by the custom IO chip to signal the CPU to read/write
	       parameters
CPU #2 IRQ mode 1
CPU #3 NMI (@120Hz)

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class digdug
{
	
	
	
	
	
	
	public static Memory_ReadAddress readmem_cpu1[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x7000, 0x700f, digdug_customio_data_r ),
		new Memory_ReadAddress( 0x7100, 0x7100, digdug_customio_r ),
		new Memory_ReadAddress( 0x8000, 0x9fff, digdug_sharedram_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_cpu2[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x9fff, digdug_sharedram_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_cpu3[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x0fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x9fff, digdug_sharedram_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_cpu1[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x6820, 0x6820, digdug_interrupt_enable_1_w ),
		new Memory_WriteAddress( 0x6821, 0x6821, digdug_interrupt_enable_2_w ),
		new Memory_WriteAddress( 0x6822, 0x6822, digdug_interrupt_enable_3_w ),
		new Memory_WriteAddress( 0x6823, 0x6823, digdug_halt_w ),
	        new Memory_WriteAddress( 0xa007, 0xa007, digdug_flipscreen_w ),
		new Memory_WriteAddress( 0x6825, 0x6827, MWA_NOP ),
		new Memory_WriteAddress( 0x6830, 0x6830, watchdog_reset_w ),
		new Memory_WriteAddress( 0x7000, 0x700f, digdug_customio_data_w ),
		new Memory_WriteAddress( 0x7100, 0x7100, digdug_customio_w ),
		new Memory_WriteAddress( 0x8000, 0x9fff, digdug_sharedram_w, digdug_sharedram ),
		new Memory_WriteAddress( 0x8000, 0x83ff, MWA_RAM, videoram, videoram_size ),   /* dirtybuffer[] handling is not needed because */
		new Memory_WriteAddress( 0x8400, 0x87ff, MWA_RAM ),	                          /* characters are redrawn every frame */
		new Memory_WriteAddress( 0x8b80, 0x8bff, MWA_RAM, spriteram, spriteram_size ), /* these three are here just to initialize */
		new Memory_WriteAddress( 0x9380, 0x93ff, MWA_RAM, spriteram_2 ),	          /* the pointers. The actual writes are */
		new Memory_WriteAddress( 0x9b80, 0x9bff, MWA_RAM, spriteram_3 ),                /* handled by digdug_sharedram_w() */
		new Memory_WriteAddress( 0xa000, 0xa00f, digdug_vh_latch_w, digdug_vlatches ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_cpu2[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0x6821, 0x6821, digdug_interrupt_enable_2_w ),
		new Memory_WriteAddress( 0x6830, 0x6830, watchdog_reset_w ),
		new Memory_WriteAddress( 0x8000, 0x9fff, digdug_sharedram_w ),
		new Memory_WriteAddress( 0xa000, 0xa00f, digdug_vh_latch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_cpu3[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x0fff, MWA_ROM ),
		new Memory_WriteAddress( 0x6800, 0x681f, pengo_sound_w, pengo_soundregs ),
		new Memory_WriteAddress( 0x6822, 0x6822, digdug_interrupt_enable_3_w ),
		new Memory_WriteAddress( 0x8000, 0x9fff, digdug_sharedram_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	/* input from the outside world */
	static InputPortHandlerPtr input_ports_digdug = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( digdug )
		PORT_START(); 	/* DSW0 */
		PORT_DIPNAME( 0x07, 0x01, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x07, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_7C") );
		/* TODO: bonus scores are different for 5 lives */
		PORT_DIPNAME( 0x38, 0x18, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x20, "10k 40k 40k" );
		PORT_DIPSETTING(    0x10, "10k 50k 50k" );
		PORT_DIPSETTING(    0x30, "20k 60k 60k" );
		PORT_DIPSETTING(    0x08, "20k 70k 70k" );
		PORT_DIPSETTING(    0x28, "10k 40k" );
		PORT_DIPSETTING(    0x18, "20k 60k" );
		PORT_DIPSETTING(    0x38, "10k" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPNAME( 0xc0, 0x80, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x40, "2" );
		PORT_DIPSETTING(    0x80, "3" );
		PORT_DIPSETTING(    0xc0, "5" );
	
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x20, 0x20, "Freeze" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, "Allow Continue" );
		PORT_DIPSETTING(    0x08, DEF_STR( "No") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Easy" );
		PORT_DIPSETTING(    0x02, "Medium" );
		PORT_DIPSETTING(    0x01, "Hard" );
		PORT_DIPSETTING(    0x03, "Hardest" );
	
		PORT_START(); 	/* FAKE */
		/* The player inputs are not memory mapped, they are handled by an I/O chip. */
		/* These fake input ports are read by digdug_customio_data_r() */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY );
		PORT_BIT_IMPULSE( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1, 1 );
		PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_BUTTON1, 0, IP_KEY_PREVIOUS, IP_JOY_PREVIOUS );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* FAKE */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT_IMPULSE( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL, 1 );
		PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL, 0, IP_KEY_PREVIOUS, IP_JOY_PREVIOUS );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* FAKE */
		PORT_BIT_IMPULSE( 0x01, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_BIT_IMPULSE( 0x02, IP_ACTIVE_LOW, IPT_COIN2, 1 );
		PORT_BIT( 0x0c, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT_IMPULSE( 0x10, IP_ACTIVE_LOW, IPT_START1, 1 );
		PORT_BIT_IMPULSE( 0x20, IP_ACTIVE_LOW, IPT_START2, 1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	INPUT_PORTS_END(); }}; 
	
	static GfxLayout charlayout1 = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		128,	/* 128 characters */
		1,		/* 1 bit per pixel */
		new int[] { 0 },	/* one bitplane */
		new int[] { 7, 6, 5, 4, 3, 2, 1, 0 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	
	static GfxLayout charlayout2 = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		256,	/* 256 characters */
		2,	/* 2 bits per pixel */
		new int[] { 0, 4 },      /* the two bitplanes for 4 pixels are packed into one byte */
		new int[] { 8*8+0, 8*8+1, 8*8+2, 8*8+3, 0, 1, 2, 3 },   /* bits are packed in groups of four */
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },   /* characters are rotated 90 degrees */
		16*8	       /* every char takes 16 bytes */
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	        /* 16*16 sprites */
		256,	        /* 256 sprites */
		2,	        /* 2 bits per pixel */
		new int[] { 0, 4 },	/* the two bitplanes for 4 pixels are packed into one byte */
		new int[] { 0, 1, 2, 3, 8*8, 8*8+1, 8*8+2, 8*8+3, 16*8+0, 16*8+1, 16*8+2, 16*8+3,
				24*8+0, 24*8+1, 24*8+2, 24*8+3 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				32*8, 33*8, 34*8, 35*8, 36*8, 37*8, 38*8, 39*8 },
		64*8	/* every sprite takes 64 bytes */
	);
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout1,            0,  8 ),
		new GfxDecodeInfo( REGION_GFX2, 0, spritelayout,         8*2, 64 ),
		new GfxDecodeInfo( REGION_GFX3, 0, charlayout2,   64*4 + 8*2, 64 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	static struct namco_interface namco_interface =
	{
		3072000/32,	/* sample rate */
		3,			/* number of voices */
		100,		/* playback volume */
		REGION_SOUND1	/* memory region */
	};
	
	
	
	public static MachineHandlerPtr machine_driver_digdug = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 3125000)	/* 3.125 MHz */
		MDRV_CPU_MEMORY(readmem_cpu1,writemem_cpu1)
		MDRV_CPU_VBLANK_INT(digdug_interrupt_1,1)
	
		MDRV_CPU_ADD(Z80, 3125000)	/* 3.125 MHz */
		MDRV_CPU_MEMORY(readmem_cpu2,writemem_cpu2)
		MDRV_CPU_VBLANK_INT(digdug_interrupt_2,1)
	
		MDRV_CPU_ADD(Z80, 3125000)	/* 3.125 MHz */
		MDRV_CPU_MEMORY(readmem_cpu3,writemem_cpu3)
		MDRV_CPU_VBLANK_INT(digdug_interrupt_3,2)
	
		MDRV_FRAMES_PER_SECOND(60.606060)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(100)	/* 100 CPU slices per frame - an high value to ensure proper */
								/* synchronization of the CPUs */
		MDRV_MACHINE_INIT(digdig)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(36*8, 28*8)
		MDRV_VISIBLE_AREA(0*8, 36*8-1, 0*8, 28*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(32)
		MDRV_COLORTABLE_LENGTH(8*2+64*4+64*4)
	
		MDRV_PALETTE_INIT(digdug)
		MDRV_VIDEO_START(digdug)
		MDRV_VIDEO_UPDATE(digdug)
	
		/* sound hardware */
		MDRV_SOUND_ADD(NAMCO, namco_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadHandlerPtr rom_digdug = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code for the first CPU  */
		ROM_LOAD( "136007.101",   0x0000, 0x1000, CRC(b9198079) SHA1(1d3fe04020f584ed250e32fdc6f6a3b769342884) )
		ROM_LOAD( "136007.102",   0x1000, 0x1000, CRC(b2acbe49) SHA1(c8f713e8cfa70d3bc64d3002ff7bffc65ee138e2) )
		ROM_LOAD( "136007.103",   0x2000, 0x1000, CRC(d6407b49) SHA1(0e71a8f02778286488865e20439776dbb2a8ec78) )
		ROM_LOAD( "dd1.4b",       0x3000, 0x1000, CRC(f4cebc16) SHA1(19b568f92069a1cfe1c07287408efe3b0e253375) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the second CPU */
		ROM_LOAD( "dd1.5b",       0x0000, 0x1000, CRC(370ef9b4) SHA1(746b1fa15f5f2cfd69d8b5a7d6fb8c770abc3b4d) )
		ROM_LOAD( "dd1.6b",       0x1000, 0x1000, CRC(361eeb71) SHA1(372c97c666411c3590d790213ae6fa1ccb5ffa1c) )
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 )	/* 64k for the third CPU  */
		ROM_LOAD( "136007.107",   0x0000, 0x1000, CRC(a41bce72) SHA1(2b9b74f56aa7939d9d47cf29497ae11f10d78598) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "dd1.9",        0x0000, 0x0800, CRC(f14a6fe1) SHA1(0aa63300c2cb887196de590aceb98f3cf06fead4) )
	
		ROM_REGION( 0x4000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "136007.116",   0x0000, 0x1000, CRC(e22957c8) SHA1(4700c63f4f680cb8ab8c44e6f3e1712aabd5daa4) )
		ROM_LOAD( "dd1.14",       0x1000, 0x1000, CRC(2829ec99) SHA1(3e435c1afb2e44487cd7ba28a93ada2e5ccbb86d) )
		ROM_LOAD( "136007.118",   0x2000, 0x1000, CRC(458499e9) SHA1(578bd839f9218c3cf4feee1223a461144e455df8) )
		ROM_LOAD( "136007.119",   0x3000, 0x1000, CRC(c58252a0) SHA1(bd79e39e8a572d2b5c205e6de27ca23e43ec9f51) )
	
		ROM_REGION( 0x1000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "dd1.11",       0x0000, 0x1000, CRC(7b383983) SHA1(57f1e8f5171d13f9f76bd091d81b4423b59f6b42) )
	
		ROM_REGION( 0x1000, REGION_GFX4, 0 ) /* 4k for the playfield graphics */
		ROM_LOAD( "dd1.10b",      0x0000, 0x1000, CRC(2cf399c2) SHA1(317c48818992f757b1bd0e3997fa99937f81b52c) )
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 )
		ROM_LOAD( "digdug.5n",    0x0000, 0x0020, CRC(4cb9da99) SHA1(91a5852a15d4672c29fdcbae75921794651f960c) )
		ROM_LOAD( "digdug.1c",    0x0020, 0x0100, CRC(00c7c419) SHA1(7ea149e8eb36920c3b84984b5ce623729d492fd3) )
		ROM_LOAD( "digdug.2n",    0x0120, 0x0100, CRC(e9b3e08e) SHA1(a294cc4da846eb702d61678396bfcbc87d30ea95) )
	
		ROM_REGION( 0x0100, REGION_SOUND1, 0 )	/* sound prom */
		ROM_LOAD( "digdug.spr",   0x0000, 0x0100, CRC(7a2815b4) SHA1(085ada18c498fdb18ecedef0ea8fe9217edb7b46) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_digdugb = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 ) /* 64k for code for the first CPU  */
		ROM_LOAD( "dd1a.1",       0x0000, 0x1000, CRC(a80ec984) SHA1(86689980410b9429cd7582c7a76342721c87d030) )
		ROM_LOAD( "dd1a.2",       0x1000, 0x1000, CRC(559f00bd) SHA1(fde17785df21956d6fd06bcfe675c392dadb1524) )
		ROM_LOAD( "dd1a.3",       0x2000, 0x1000, CRC(8cbc6fe1) SHA1(57b8a5777f8bb9773caf0cafe5408c8b9768cb25) )
		ROM_LOAD( "dd1a.4",       0x3000, 0x1000, CRC(d066f830) SHA1(b0a615fe4a5c8742c1e4ef234ef34c369d2723b9) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 ) /* 64k for the second CPU */
		ROM_LOAD( "dd1a.5",       0x0000, 0x1000, CRC(6687933b) SHA1(c16144de7633595ddc1450ddce379f48e7b2195a) )
		ROM_LOAD( "dd1a.6",       0x1000, 0x1000, CRC(843d857f) SHA1(89b2ead7e478e119d33bfd67376cdf28f83de67a) )
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 ) /* 64k for the third CPU  */
		ROM_LOAD( "136007.107",   0x0000, 0x1000, CRC(a41bce72) SHA1(2b9b74f56aa7939d9d47cf29497ae11f10d78598) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "dd1.9",        0x0000, 0x0800, CRC(f14a6fe1) SHA1(0aa63300c2cb887196de590aceb98f3cf06fead4) )
	
		ROM_REGION( 0x4000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "136007.116",   0x0000, 0x1000, CRC(e22957c8) SHA1(4700c63f4f680cb8ab8c44e6f3e1712aabd5daa4) )
		ROM_LOAD( "dd1.14",       0x1000, 0x1000, CRC(2829ec99) SHA1(3e435c1afb2e44487cd7ba28a93ada2e5ccbb86d) )
		ROM_LOAD( "136007.118",   0x2000, 0x1000, CRC(458499e9) SHA1(578bd839f9218c3cf4feee1223a461144e455df8) )
		ROM_LOAD( "136007.119",   0x3000, 0x1000, CRC(c58252a0) SHA1(bd79e39e8a572d2b5c205e6de27ca23e43ec9f51) )
	
		ROM_REGION( 0x1000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "dd1.11",       0x0000, 0x1000, CRC(7b383983) SHA1(57f1e8f5171d13f9f76bd091d81b4423b59f6b42) )
	
		ROM_REGION( 0x1000, REGION_GFX4, 0 ) /* 4k for the playfield graphics */
		ROM_LOAD( "dd1.10b",      0x0000, 0x1000, CRC(2cf399c2) SHA1(317c48818992f757b1bd0e3997fa99937f81b52c) )
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 )
		ROM_LOAD( "digdug.5n",    0x0000, 0x0020, CRC(4cb9da99) SHA1(91a5852a15d4672c29fdcbae75921794651f960c) )
		ROM_LOAD( "digdug.1c",    0x0020, 0x0100, CRC(00c7c419) SHA1(7ea149e8eb36920c3b84984b5ce623729d492fd3) )
		ROM_LOAD( "digdug.2n",    0x0120, 0x0100, CRC(e9b3e08e) SHA1(a294cc4da846eb702d61678396bfcbc87d30ea95) )
	
		ROM_REGION( 0x0100, REGION_SOUND1, 0 )	/* sound prom */
		ROM_LOAD( "digdug.spr",   0x0000, 0x0100, CRC(7a2815b4) SHA1(085ada18c498fdb18ecedef0ea8fe9217edb7b46) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_digdugat = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code for the first CPU  */
		ROM_LOAD( "201.007",      0x0000, 0x1000, CRC(23d0b1a4) SHA1(a118d55e03a9ccf069f37c7bac2c9044dccd1f5e) )
		ROM_LOAD( "202.007",      0x1000, 0x1000, CRC(5453dc1f) SHA1(8be091dd53e9b44e80e1ac9b1751efbe832db78d) )
		ROM_LOAD( "203.007",      0x2000, 0x1000, CRC(c9077dfa) SHA1(611b3e1b575a51639530917366557773534c80aa) )
		ROM_LOAD( "204.007",      0x3000, 0x1000, CRC(a8fc8eac) SHA1(7a24197f4ec5989bc4d635b27b6578f4d62cb5f4) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the second CPU */
		ROM_LOAD( "205.007",      0x0000, 0x1000, CRC(5ba385c5) SHA1(f4577bddff74a14b13b212f5553fa13fe9ae4bcc) )
		ROM_LOAD( "206.007",      0x1000, 0x1000, CRC(382b4011) SHA1(2b79ddcf48177c99b5fa1f957374f4baa2bec143) )
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 )	/* 64k for the third CPU  */
		ROM_LOAD( "136007.107",   0x0000, 0x1000, CRC(a41bce72) SHA1(2b9b74f56aa7939d9d47cf29497ae11f10d78598) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "136007.108",   0x0000, 0x0800, CRC(3d24a3af) SHA1(857ae93e2a41258a129dcecbaed2df359540b735) )
	
		ROM_REGION( 0x4000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "136007.116",   0x0000, 0x1000, CRC(e22957c8) SHA1(4700c63f4f680cb8ab8c44e6f3e1712aabd5daa4) )
		ROM_LOAD( "136007.117",   0x1000, 0x1000, CRC(a3bbfd85) SHA1(2105455762e0de120f2d943f9010a7d06c6b6448) )
		ROM_LOAD( "136007.118",   0x2000, 0x1000, CRC(458499e9) SHA1(578bd839f9218c3cf4feee1223a461144e455df8) )
		ROM_LOAD( "136007.119",   0x3000, 0x1000, CRC(c58252a0) SHA1(bd79e39e8a572d2b5c205e6de27ca23e43ec9f51) )
	
		ROM_REGION( 0x1000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "136007.115",   0x0000, 0x1000, CRC(754539be) SHA1(466ae754eb4721df8814d4d33a31d867507d45b3) )
	
		ROM_REGION( 0x1000, REGION_GFX4, 0 )	/* 4k for the playfield graphics */
		ROM_LOAD( "136007.114",   0x0000, 0x1000, CRC(d6822397) SHA1(055ca6514141323f1e6dfcf91451507c04114d41) )
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 )
		ROM_LOAD( "digdug.5n",    0x0000, 0x0020, CRC(4cb9da99) SHA1(91a5852a15d4672c29fdcbae75921794651f960c) )
		ROM_LOAD( "digdug.1c",    0x0020, 0x0100, CRC(00c7c419) SHA1(7ea149e8eb36920c3b84984b5ce623729d492fd3) )
		ROM_LOAD( "digdug.2n",    0x0120, 0x0100, CRC(e9b3e08e) SHA1(a294cc4da846eb702d61678396bfcbc87d30ea95) )
	
		ROM_REGION( 0x0100, REGION_SOUND1, 0 )	/* sound prom */
		ROM_LOAD( "digdug.spr",   0x0000, 0x0100, CRC(7a2815b4) SHA1(085ada18c498fdb18ecedef0ea8fe9217edb7b46) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_digduga1 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code for the first CPU  */
		ROM_LOAD( "136007.101",   0x0000, 0x1000, CRC(b9198079) SHA1(1d3fe04020f584ed250e32fdc6f6a3b769342884) )
		ROM_LOAD( "136007.102",   0x1000, 0x1000, CRC(b2acbe49) SHA1(c8f713e8cfa70d3bc64d3002ff7bffc65ee138e2) )
		ROM_LOAD( "136007.103",   0x2000, 0x1000, CRC(d6407b49) SHA1(0e71a8f02778286488865e20439776dbb2a8ec78) )
		ROM_LOAD( "136007.104",   0x3000, 0x1000, CRC(b3ad42c3) SHA1(83ea80f0dd42ec1cb62e6ed45d5dda43ed21f567) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the second CPU */
		ROM_LOAD( "136007.105",   0x0000, 0x1000, CRC(0a2aef4a) SHA1(ef40974fde8e8c305059e1dd03ea811a6aaca737) )
		ROM_LOAD( "136007.106",   0x1000, 0x1000, CRC(a2876d6e) SHA1(08e8ac50918ae32dd6fb34e65534652beb0395b2) )
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 )	/* 64k for the third CPU  */
		ROM_LOAD( "136007.107",   0x0000, 0x1000, CRC(a41bce72) SHA1(2b9b74f56aa7939d9d47cf29497ae11f10d78598) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "136007.108",   0x0000, 0x0800, CRC(3d24a3af) SHA1(857ae93e2a41258a129dcecbaed2df359540b735) )
	
		ROM_REGION( 0x4000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "136007.116",   0x0000, 0x1000, CRC(e22957c8) SHA1(4700c63f4f680cb8ab8c44e6f3e1712aabd5daa4) )
		ROM_LOAD( "136007.117",   0x1000, 0x1000, CRC(a3bbfd85) SHA1(2105455762e0de120f2d943f9010a7d06c6b6448) )
		ROM_LOAD( "136007.118",   0x2000, 0x1000, CRC(458499e9) SHA1(578bd839f9218c3cf4feee1223a461144e455df8) )
		ROM_LOAD( "136007.119",   0x3000, 0x1000, CRC(c58252a0) SHA1(bd79e39e8a572d2b5c205e6de27ca23e43ec9f51) )
	
		ROM_REGION( 0x1000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "136007.115",   0x0000, 0x1000, CRC(754539be) SHA1(466ae754eb4721df8814d4d33a31d867507d45b3) )
	
		ROM_REGION( 0x1000, REGION_GFX4, 0 )	/* 4k for the playfield graphics */
		ROM_LOAD( "136007.114",   0x0000, 0x1000, CRC(d6822397) SHA1(055ca6514141323f1e6dfcf91451507c04114d41) )
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 )
		ROM_LOAD( "digdug.5n",    0x0000, 0x0020, CRC(4cb9da99) SHA1(91a5852a15d4672c29fdcbae75921794651f960c) )
		ROM_LOAD( "digdug.1c",    0x0020, 0x0100, CRC(00c7c419) SHA1(7ea149e8eb36920c3b84984b5ce623729d492fd3) )
		ROM_LOAD( "digdug.2n",    0x0120, 0x0100, CRC(e9b3e08e) SHA1(a294cc4da846eb702d61678396bfcbc87d30ea95) )
	
		ROM_REGION( 0x0100, REGION_SOUND1, 0 )	/* sound prom */
		ROM_LOAD( "digdug.spr",   0x0000, 0x0100, CRC(7a2815b4) SHA1(085ada18c498fdb18ecedef0ea8fe9217edb7b46) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_dzigzag = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code for the first CPU  */
		ROM_LOAD( "136007.101",   0x0000, 0x1000, CRC(b9198079) SHA1(1d3fe04020f584ed250e32fdc6f6a3b769342884) )
		ROM_LOAD( "136007.102",   0x1000, 0x1000, CRC(b2acbe49) SHA1(c8f713e8cfa70d3bc64d3002ff7bffc65ee138e2) )
		ROM_LOAD( "136007.103",   0x2000, 0x1000, CRC(d6407b49) SHA1(0e71a8f02778286488865e20439776dbb2a8ec78) )
		ROM_LOAD( "zigzag4",      0x3000, 0x1000, CRC(da20d2f6) SHA1(4eafe5ee917060d01d9df92d678c455edbbf27a6) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the second CPU */
		ROM_LOAD( "zigzag5",      0x0000, 0x2000, CRC(f803c748) SHA1(a4c7dde0b794366cbfd03f339de980a6575a42fc) )
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 )	/* 64k for the third CPU  */
		ROM_LOAD( "136007.107",   0x0000, 0x1000, CRC(a41bce72) SHA1(2b9b74f56aa7939d9d47cf29497ae11f10d78598) )
	
		ROM_REGION( 0x10000, REGION_CPU4, 0 )	/* 64k for a Z80 which emulates the custom I/O chip (not used) */
		ROM_LOAD( "zigzag7",      0x0000, 0x1000, CRC(24c3510c) SHA1(3214a16f697f88d23f3441e58c56110930d7c341) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "zigzag8",      0x0000, 0x0800, CRC(86120541) SHA1(c974441ee0421a38c25bc7c3edbc6b510b7df473) )
	
		ROM_REGION( 0x4000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "136007.116",   0x0000, 0x1000, CRC(e22957c8) SHA1(4700c63f4f680cb8ab8c44e6f3e1712aabd5daa4) )
		ROM_LOAD( "zigzag12",     0x1000, 0x1000, CRC(386a0956) SHA1(79f5d6af1fdc467a503216a588cb03535c823a40) )
		ROM_LOAD( "zigzag13",     0x2000, 0x1000, CRC(69f6e395) SHA1(10a7518e963f2cecb494d77137e01a068116e20b) )
		ROM_LOAD( "136007.119",   0x3000, 0x1000, CRC(c58252a0) SHA1(bd79e39e8a572d2b5c205e6de27ca23e43ec9f51) )
	
		ROM_REGION( 0x1000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "dd1.11",       0x0000, 0x1000, CRC(7b383983) SHA1(57f1e8f5171d13f9f76bd091d81b4423b59f6b42) )
	
		ROM_REGION( 0x1000, REGION_GFX4, 0 ) /* 4k for the playfield graphics */
		ROM_LOAD( "dd1.10b",      0x0000, 0x1000, CRC(2cf399c2) SHA1(317c48818992f757b1bd0e3997fa99937f81b52c) )
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 )
		ROM_LOAD( "digdug.5n",    0x0000, 0x0020, CRC(4cb9da99) SHA1(91a5852a15d4672c29fdcbae75921794651f960c) )
		ROM_LOAD( "digdug.1c",    0x0020, 0x0100, CRC(00c7c419) SHA1(7ea149e8eb36920c3b84984b5ce623729d492fd3) )
		ROM_LOAD( "digdug.2n",    0x0120, 0x0100, CRC(e9b3e08e) SHA1(a294cc4da846eb702d61678396bfcbc87d30ea95) )
	
		ROM_REGION( 0x0100, REGION_SOUND1, 0 )	/* sound prom */
		ROM_LOAD( "digdug.spr",   0x0000, 0x0100, CRC(7a2815b4) SHA1(085ada18c498fdb18ecedef0ea8fe9217edb7b46) )
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_digdug	   = new GameDriver("1982"	,"digdug"	,"digdug.java"	,rom_digdug,null	,machine_driver_digdug	,input_ports_digdug	,null	,ROT90, "Namco", "Dig Dug (set 1)" )
	public static GameDriver driver_digdugb	   = new GameDriver("1982"	,"digdugb"	,"digdug.java"	,rom_digdugb,driver_digdug	,machine_driver_digdug	,input_ports_digdug	,null	,ROT90, "Namco", "Dig Dug (set 2)" )
	public static GameDriver driver_digdugat	   = new GameDriver("1982"	,"digdugat"	,"digdug.java"	,rom_digdugat,driver_digdug	,machine_driver_digdug	,input_ports_digdug	,null	,ROT90, "[Namco] (Atari license)", "Dig Dug (Atari, rev 2)" )
	public static GameDriver driver_digduga1	   = new GameDriver("1982"	,"digduga1"	,"digdug.java"	,rom_digduga1,driver_digdug	,machine_driver_digdug	,input_ports_digdug	,null	,ROT90, "[Namco] (Atari license)", "Dig Dug (Atari, rev 1)" )
	public static GameDriver driver_dzigzag	   = new GameDriver("1982"	,"dzigzag"	,"digdug.java"	,rom_dzigzag,driver_digdug	,machine_driver_digdug	,input_ports_digdug	,null	,ROT90, "bootleg", "Zig Zag (Dig Dug hardware)" )
}
