/***************************************************************************

TODO:
- The starfield is taken from Galaxien. The Galaga one is probably different,
  unfortunately it is generated by a custom IC :-(


Galaga memory map (preliminary)

CPU #1:
0000-3fff ROM
CPU #2:
0000-1fff ROM
CPU #3:
0000-1fff ROM
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
a000-a001 starfield scroll speed (only bit 0 is significant)
a002      starfield scroll direction (0 = backwards) (only bit 0 is significant)
a003-a004 starfield blink
a005      starfield enable
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
 * using automatic conversion tool v0.0.5
 */ 
package arcadeflex.v078.drivers;

public class galaga
{
	
	
	
	
	
	
	
	
	public static WriteHandlerPtr flip_screen_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		flip_screen_set(data);
	} };
	
	
	public static Memory_ReadAddress readmem_cpu1[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x8000, 0x9fff, galaga_sharedram_r ),
		new Memory_ReadAddress( 0x6800, 0x6807, galaga_dsw_r ),
		new Memory_ReadAddress( 0x7000, 0x700f, galaga_customio_data_r ),
		new Memory_ReadAddress( 0x7100, 0x7100, galaga_customio_r ),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_cpu2[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x8000, 0x9fff, galaga_sharedram_r ),
		new Memory_ReadAddress( 0x6800, 0x6807, galaga_dsw_r ),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_cpu3[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x8000, 0x9fff, galaga_sharedram_r ),
		new Memory_ReadAddress( 0x6800, 0x6807, galaga_dsw_r ),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_cpu1[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x8000, 0x9fff, galaga_sharedram_w, galaga_sharedram ),
		new Memory_WriteAddress( 0x6830, 0x6830, MWA_NOP ),
		new Memory_WriteAddress( 0x7000, 0x700f, galaga_customio_data_w ),
		new Memory_WriteAddress( 0x7100, 0x7100, galaga_customio_w ),
		new Memory_WriteAddress( 0xa000, 0xa005, MWA_RAM, galaga_starcontrol ),
		new Memory_WriteAddress( 0x6820, 0x6820, galaga_interrupt_enable_1_w ),
		new Memory_WriteAddress( 0x6822, 0x6822, galaga_interrupt_enable_3_w ),
		new Memory_WriteAddress( 0x6823, 0x6823, galaga_halt_w ),
		new Memory_WriteAddress( 0xa007, 0xa007, flip_screen_w ),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8b80, 0x8bff, MWA_RAM, spriteram, spriteram_size ),       /* these three are here just to initialize */
		new Memory_WriteAddress( 0x9380, 0x93ff, MWA_RAM, spriteram_2 ),      /* the pointers. The actual writes are */
		new Memory_WriteAddress( 0x9b80, 0x9bff, MWA_RAM, spriteram_3 ),      /* handled by galaga_sharedram_w() */
		new Memory_WriteAddress( 0x8000, 0x83ff, MWA_RAM, videoram, videoram_size ), /* dirtybuffer[] handling is not needed because */
		new Memory_WriteAddress( 0x8400, 0x87ff, MWA_RAM, colorram ), /* characters are redrawn every frame */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_cpu2[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x8000, 0x9fff, galaga_sharedram_w ),
		new Memory_WriteAddress( 0x6821, 0x6821, galaga_interrupt_enable_2_w ),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_cpu3[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x8000, 0x9fff, galaga_sharedram_w ),
		new Memory_WriteAddress( 0x6800, 0x681f, pengo_sound_w, pengo_soundregs ),
		new Memory_WriteAddress( 0x6822, 0x6822, galaga_interrupt_enable_3_w ),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortHandlerPtr input_ports_galaga = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( galaga )
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		/* TODO: bonus scores are different for 5 lives */
		PORT_DIPNAME( 0x38, 0x10, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x20, "20K 60K 60K" );
		PORT_DIPSETTING(    0x18, "20K 60K" );
		PORT_DIPSETTING(    0x10, "20K 70K 70K" );
		PORT_DIPSETTING(    0x30, "20K 80K 80K" );
		PORT_DIPSETTING(    0x38, "30K 80K" );
		PORT_DIPSETTING(    0x08, "30K 100K 100K" );
		PORT_DIPSETTING(    0x28, "30K 120K 120K" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPNAME( 0xc0, 0x80, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x80, "3" );
		PORT_DIPSETTING(    0x40, "4" );
		PORT_DIPSETTING(    0xc0, "5" );
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x01, 0x01, "2 Credits Game" );
		PORT_DIPSETTING(    0x00, "1 Player" );
		PORT_DIPSETTING(    0x01, "2 Players" );
		PORT_DIPNAME( 0x06, 0x06, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x06, "Easy" );
		PORT_DIPSETTING(    0x00, "Medium" );
		PORT_DIPSETTING(    0x02, "Hard" );
		PORT_DIPSETTING(    0x04, "Hardest" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Freeze" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BITX(    0x20, 0x20, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Rack Test", KEYCODE_F1, IP_JOY_NONE );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
	
		PORT_START();       /* FAKE */
		/* The player inputs are not memory mapped, they are handled by an I/O chip. */
		/* These fake input ports are read by galaga_customio_data_r() */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY );
		PORT_BIT_IMPULSE( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1, 1 );
		PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_BUTTON1, 0, IP_KEY_PREVIOUS, IP_JOY_PREVIOUS );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();       /* FAKE */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT_IMPULSE( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL, 1 );
		PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL, 0, IP_KEY_PREVIOUS, IP_JOY_PREVIOUS );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();       /* FAKE */
		/* the button here is used to trigger the sound in the test screen */
		PORT_BITX(0x03, IP_ACTIVE_LOW, IPT_BUTTON1,     0, IP_KEY_DEFAULT, IP_JOY_DEFAULT );
		PORT_BIT_IMPULSE( 0x04, IP_ACTIVE_LOW, IPT_START1, 1 );
		PORT_BIT_IMPULSE( 0x08, IP_ACTIVE_LOW, IPT_START2, 1 );
		PORT_BIT_IMPULSE( 0x10, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_BIT_IMPULSE( 0x20, IP_ACTIVE_LOW, IPT_COIN2, 1 );
		PORT_BIT_IMPULSE( 0x40, IP_ACTIVE_LOW, IPT_COIN3, 1 );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	INPUT_PORTS_END(); }}; 
	
	/* same as galaga, dip switches are slightly different */
	static InputPortHandlerPtr input_ports_galaganm = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( galaganm )
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		/* TODO: bonus scores are different for 5 lives */
		PORT_DIPNAME( 0x38, 0x10, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x20, "20K 60K 60K" );
		PORT_DIPSETTING(    0x18, "20K 60K" );
		PORT_DIPSETTING(    0x10, "20K 70K 70K" );
		PORT_DIPSETTING(    0x30, "20K 80K 80K" );
		PORT_DIPSETTING(    0x38, "30K 80K" );
		PORT_DIPSETTING(    0x08, "30K 100K 100K" );
		PORT_DIPSETTING(    0x28, "30K 120K 120K" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPNAME( 0xc0, 0x80, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x80, "3" );
		PORT_DIPSETTING(    0x40, "4" );
		PORT_DIPSETTING(    0xc0, "5" );
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x03, "Easy" );
		PORT_DIPSETTING(    0x00, "Medium" );
		PORT_DIPSETTING(    0x01, "Hard" );
		PORT_DIPSETTING(    0x02, "Hardest" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Freeze" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BITX(    0x20, 0x20, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Rack Test", KEYCODE_F1, IP_JOY_NONE );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
	
		PORT_START();       /* FAKE */
		/* The player inputs are not memory mapped, they are handled by an I/O chip. */
		/* These fake input ports are read by galaga_customio_data_r() */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY );
		PORT_BIT_IMPULSE( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1, 1 );
		PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_BUTTON1, 0, IP_KEY_PREVIOUS, IP_JOY_PREVIOUS );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();       /* FAKE */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT_IMPULSE( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL, 1 );
		PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL, 0, IP_KEY_PREVIOUS, IP_JOY_PREVIOUS );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START();       /* FAKE */
		/* the button here is used to trigger the sound in the test screen */
		PORT_BITX(0x03, IP_ACTIVE_LOW, IPT_BUTTON1,     0, IP_KEY_DEFAULT, IP_JOY_DEFAULT );
		PORT_BIT_IMPULSE( 0x04, IP_ACTIVE_LOW, IPT_START1, 1 );
		PORT_BIT_IMPULSE( 0x08, IP_ACTIVE_LOW, IPT_START2, 1 );
		PORT_BIT_IMPULSE( 0x10, IP_ACTIVE_LOW, IPT_COIN1, 1 );
		PORT_BIT_IMPULSE( 0x20, IP_ACTIVE_LOW, IPT_COIN2, 1 );
		PORT_BIT_IMPULSE( 0x40, IP_ACTIVE_LOW, IPT_COIN3, 1 );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,           /* 8*8 characters */
		RGN_FRAC(1,1), /* 128 characters */
		2,             /* 2 bits per pixel */
		new int[] { 0, 4 },       /* the two bitplanes for 4 pixels are packed into one byte */
		new int[] { 8*8+0, 8*8+1, 8*8+2, 8*8+3, 0, 1, 2, 3 },   /* bits are packed in groups of four */
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },   /* characters are rotated 90 degrees */
		16*8           /* every char takes 16 bytes */
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,          /* 16*16 sprites */
		128,            /* 128 sprites */
		2,              /* 2 bits per pixel */
		new int[] { 0, 4 },       /* the two bitplanes for 4 pixels are packed into one byte */
		new int[] { 0, 1, 2, 3, 8*8, 8*8+1, 8*8+2, 8*8+3, 16*8+0, 16*8+1, 16*8+2, 16*8+3,
				24*8+0, 24*8+1, 24*8+2, 24*8+3 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				32*8, 33*8, 34*8, 35*8, 36*8, 37*8, 38*8, 39*8 },
		64*8    /* every sprite takes 64 bytes */
	);
	
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,       0, 32 ),
		new GfxDecodeInfo( REGION_GFX2, 0, spritelayout,  32*4, 32 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static struct namco_interface namco_interface =
	{
		3072000/32,	/* sample rate */
		3,			/* number of voices */
		100,		/* playback volume */
		REGION_SOUND1	/* memory region */
	};
	
	static const char *galaga_sample_names[] =
	{
		"*galaga",
		"bang.wav",
		"init.wav",
		0       /* end of array */
	};
	
	static Samplesinterface samples_interface = new Samplesinterface
	(
		1,	/* one channel */
		80,	/* volume */
		galaga_sample_names
	);
	
	
	static MACHINE_DRIVER_START( galaga )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 3125000)        /* 3.125 MHz */
		MDRV_CPU_MEMORY(readmem_cpu1,writemem_cpu1)
		MDRV_CPU_VBLANK_INT(galaga_interrupt_1,1)
	
		MDRV_CPU_ADD(Z80, 3125000)        /* 3.125 MHz */
		MDRV_CPU_MEMORY(readmem_cpu2,writemem_cpu2)
		MDRV_CPU_VBLANK_INT(galaga_interrupt_2,1)
	
		MDRV_CPU_ADD(Z80, 3125000)        /* 3.125 MHz */
		MDRV_CPU_MEMORY(readmem_cpu3,writemem_cpu3)
		MDRV_CPU_VBLANK_INT(galaga_interrupt_3,2)
	
		MDRV_FRAMES_PER_SECOND(60.606060)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(99)	/* 99 CPU slices per frame - with 100, galagab2 hangs on coin insertion */
	
		MDRV_MACHINE_INIT(galaga)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(36*8, 28*8)
		MDRV_VISIBLE_AREA(0*8, 36*8-1, 0*8, 28*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(32+64)
		MDRV_COLORTABLE_LENGTH(64*4)  /* 32 for the characters, 64 for the stars */
	
		MDRV_PALETTE_INIT(galaga)
		MDRV_VIDEO_START(galaga)
		MDRV_VIDEO_UPDATE(galaga)
	
		/* sound hardware */
		MDRV_SOUND_ADD(NAMCO, namco_interface)
		MDRV_SOUND_ADD(SAMPLES, samples_interface)
	MACHINE_DRIVER_END
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadHandlerPtr rom_galaga = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code for the first CPU  */
		ROM_LOAD( "04m_g01.bin",  0x0000, 0x1000, CRC(a3a0f743) SHA1(6907773db7c002ecde5e41853603d53387c5c7cd) )
		ROM_LOAD( "04k_g02.bin",  0x1000, 0x1000, CRC(43bb0d5c) SHA1(666975aed5ce84f09794c54b550d64d95ab311f0) )
		ROM_LOAD( "04j_g03.bin",  0x2000, 0x1000, CRC(753ce503) SHA1(481f443aea3ed3504ec2f3a6bfcf3cd47e2f8f81) )
		ROM_LOAD( "04h_g04.bin",  0x3000, 0x1000, CRC(83874442) SHA1(366cb0dbd31b787e64f88d182108b670d03b393e) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for the second CPU */
		ROM_LOAD( "04e_g05.bin",  0x0000, 0x1000, CRC(3102fccd) SHA1(d29b68d6aab3217fa2106b3507b9273ff3f927bf) )
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 )     /* 64k for the third CPU  */
		ROM_LOAD( "04d_g06.bin",  0x0000, 0x1000, CRC(8995088d) SHA1(d6cb439de0718826d1a0363c9d77de8740b18ecf) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "07m_g08.bin",  0x0000, 0x1000, CRC(58b2f47c) SHA1(62f1279a784ab2f8218c4137c7accda00e6a3490) )
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "07e_g10.bin",  0x0000, 0x1000, CRC(ad447c80) SHA1(e697c180178cabd1d32483c5d8889a40633f7857) )
		ROM_LOAD( "07h_g09.bin",  0x1000, 0x1000, CRC(dd6f1afc) SHA1(c340ed8c25e0979629a9a1730edc762bd72d0cff) )
	
		ROM_REGION( 0x0320, REGION_PROMS, 0 )
		ROM_LOAD( "5n.bin",       0x0000, 0x0020, CRC(54603c6b) SHA1(1a6dea13b4af155d9cb5b999a75d4f1eb9c71346) )	/* palette */
		ROM_LOAD( "2n.bin",       0x0020, 0x0100, CRC(a547d33b) SHA1(7323084320bb61ae1530d916f5edd8835d4d2461) )	/* char lookup table */
		ROM_LOAD( "1c.bin",       0x0120, 0x0100, CRC(b6f585fb) SHA1(dd10147c4f05fede7ae6e7a760681700a660e87e) )	/* sprite lookup table */
		ROM_LOAD( "5c.bin",       0x0220, 0x0100, CRC(8bd565f6) SHA1(bedba65816abfc2ebeacac6ee335ca6f136e3e3d) )	/* unknown */
	
		ROM_REGION( 0x0100, REGION_SOUND1, 0 )	/* sound prom */
		ROM_LOAD( "1d.bin",       0x0000, 0x0100, CRC(86d92b24) SHA1(6bef9102b97c83025a2cf84e89d95f2d44c3d2ed) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_galagamw = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code for the first CPU  */
		ROM_LOAD( "3200a.bin",    0x0000, 0x1000, CRC(3ef0b053) SHA1(0c04a362b737998c0952a753fb3fd8c8a17e9b46) )
		ROM_LOAD( "3300b.bin",    0x1000, 0x1000, CRC(1b280831) SHA1(f7ea12e61929717ebe43a4198a97f109845a2c62) )
		ROM_LOAD( "3400c.bin",    0x2000, 0x1000, CRC(16233d33) SHA1(a7eb799be5e23058754a92b15e6527bfbb47a354) )
		ROM_LOAD( "3500d.bin",    0x3000, 0x1000, CRC(0aaf5c23) SHA1(3f4b0bb960bf002261e9c1278c88f594c6aa8ab6) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for the second CPU */
		ROM_LOAD( "3600e.bin",    0x0000, 0x1000, CRC(bc556e76) SHA1(0d3d68243c4571d985b4d8f7e0ea9f6fcffa2116) )
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 )     /* 64k for the third CPU  */
		ROM_LOAD( "3700g.bin",    0x0000, 0x1000, CRC(b07f0aa4) SHA1(7528644a8480d0be2d0d37069515ed319e94778f) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "07m_g08.bin",  0x0000, 0x1000, CRC(58b2f47c) SHA1(62f1279a784ab2f8218c4137c7accda00e6a3490) )
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "07e_g10.bin",  0x0000, 0x1000, CRC(ad447c80) SHA1(e697c180178cabd1d32483c5d8889a40633f7857) )
		ROM_LOAD( "07h_g09.bin",  0x1000, 0x1000, CRC(dd6f1afc) SHA1(c340ed8c25e0979629a9a1730edc762bd72d0cff) )
	
		ROM_REGION( 0x0320, REGION_PROMS, 0 )
		ROM_LOAD( "5n.bin",       0x0000, 0x0020, CRC(54603c6b) SHA1(1a6dea13b4af155d9cb5b999a75d4f1eb9c71346) )	/* palette */
		ROM_LOAD( "2n.bin",       0x0020, 0x0100, CRC(a547d33b) SHA1(7323084320bb61ae1530d916f5edd8835d4d2461) )	/* char lookup table */
		ROM_LOAD( "1c.bin",       0x0120, 0x0100, CRC(b6f585fb) SHA1(dd10147c4f05fede7ae6e7a760681700a660e87e) )	/* sprite lookup table */
		ROM_LOAD( "5c.bin",       0x0220, 0x0100, CRC(8bd565f6) SHA1(bedba65816abfc2ebeacac6ee335ca6f136e3e3d) )	/* unknown */
	
		ROM_REGION( 0x0100, REGION_SOUND1, 0 )	/* sound prom */
		ROM_LOAD( "1d.bin",       0x0000, 0x0100, CRC(86d92b24) SHA1(6bef9102b97c83025a2cf84e89d95f2d44c3d2ed) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_galagads = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code for the first CPU  */
		ROM_LOAD( "3200a.bin",    0x0000, 0x1000, CRC(3ef0b053) SHA1(0c04a362b737998c0952a753fb3fd8c8a17e9b46) )
		ROM_LOAD( "3300b.bin",    0x1000, 0x1000, CRC(1b280831) SHA1(f7ea12e61929717ebe43a4198a97f109845a2c62) )
		ROM_LOAD( "3400c.bin",    0x2000, 0x1000, CRC(16233d33) SHA1(a7eb799be5e23058754a92b15e6527bfbb47a354) )
		ROM_LOAD( "3500d.bin",    0x3000, 0x1000, CRC(0aaf5c23) SHA1(3f4b0bb960bf002261e9c1278c88f594c6aa8ab6) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for the second CPU */
		ROM_LOAD( "3600fast.bin", 0x0000, 0x1000, CRC(23d586e5) SHA1(43346c69385e9091e64cff6c027ac2689cafcbb9) )
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 )     /* 64k for the third CPU  */
		ROM_LOAD( "3700g.bin",    0x0000, 0x1000, CRC(b07f0aa4) SHA1(7528644a8480d0be2d0d37069515ed319e94778f) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "07m_g08.bin",  0x0000, 0x1000, CRC(58b2f47c) SHA1(62f1279a784ab2f8218c4137c7accda00e6a3490) )
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "07e_g10.bin",  0x0000, 0x1000, CRC(ad447c80) SHA1(e697c180178cabd1d32483c5d8889a40633f7857) )
		ROM_LOAD( "07h_g09.bin",  0x1000, 0x1000, CRC(dd6f1afc) SHA1(c340ed8c25e0979629a9a1730edc762bd72d0cff) )
	
		ROM_REGION( 0x0320, REGION_PROMS, 0 )
		ROM_LOAD( "5n.bin",       0x0000, 0x0020, CRC(54603c6b) SHA1(1a6dea13b4af155d9cb5b999a75d4f1eb9c71346) )	/* palette */
		ROM_LOAD( "2n.bin",       0x0020, 0x0100, CRC(a547d33b) SHA1(7323084320bb61ae1530d916f5edd8835d4d2461) )	/* char lookup table */
		ROM_LOAD( "1c.bin",       0x0120, 0x0100, CRC(b6f585fb) SHA1(dd10147c4f05fede7ae6e7a760681700a660e87e) )	/* sprite lookup table */
		ROM_LOAD( "5c.bin",       0x0220, 0x0100, CRC(8bd565f6) SHA1(bedba65816abfc2ebeacac6ee335ca6f136e3e3d) )	/* unknown */
	
		ROM_REGION( 0x0100, REGION_SOUND1, 0 )	/* sound prom */
		ROM_LOAD( "1d.bin",       0x0000, 0x0100, CRC(86d92b24) SHA1(6bef9102b97c83025a2cf84e89d95f2d44c3d2ed) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_gallag = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code for the first CPU  */
		ROM_LOAD( "04m_g01.bin",  0x0000, 0x1000, CRC(a3a0f743) SHA1(6907773db7c002ecde5e41853603d53387c5c7cd) )
		ROM_LOAD( "gallag.2",     0x1000, 0x1000, CRC(5eda60a7) SHA1(853d7b974dd04abd7af3a8ba2681dfabce4dce18) )
		ROM_LOAD( "04j_g03.bin",  0x2000, 0x1000, CRC(753ce503) SHA1(481f443aea3ed3504ec2f3a6bfcf3cd47e2f8f81) )
		ROM_LOAD( "04h_g04.bin",  0x3000, 0x1000, CRC(83874442) SHA1(366cb0dbd31b787e64f88d182108b670d03b393e) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for the second CPU */
		ROM_LOAD( "04e_g05.bin",  0x0000, 0x1000, CRC(3102fccd) SHA1(d29b68d6aab3217fa2106b3507b9273ff3f927bf) )
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 )     /* 64k for the third CPU  */
		ROM_LOAD( "04d_g06.bin",  0x0000, 0x1000, CRC(8995088d) SHA1(d6cb439de0718826d1a0363c9d77de8740b18ecf) )
	
		ROM_REGION( 0x10000, REGION_CPU4, 0 )	/* 64k for a Z80 which emulates the custom I/O chip (not used) */
		ROM_LOAD( "gallag.6",     0x0000, 0x1000, CRC(001b70bc) SHA1(b465eee91e75257b7b049d49c0064ab5fd66c576) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "gallag.8",     0x0000, 0x1000, CRC(169a98a4) SHA1(edbeb11076061e744ea88d9899dbdfe0964c7e78) )
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "07e_g10.bin",  0x0000, 0x1000, CRC(ad447c80) SHA1(e697c180178cabd1d32483c5d8889a40633f7857) )
		ROM_LOAD( "07h_g09.bin",  0x1000, 0x1000, CRC(dd6f1afc) SHA1(c340ed8c25e0979629a9a1730edc762bd72d0cff) )
	
		ROM_REGION( 0x0320, REGION_PROMS, 0 )
		ROM_LOAD( "5n.bin",       0x0000, 0x0020, CRC(54603c6b) SHA1(1a6dea13b4af155d9cb5b999a75d4f1eb9c71346) )	/* palette */
		ROM_LOAD( "2n.bin",       0x0020, 0x0100, CRC(a547d33b) SHA1(7323084320bb61ae1530d916f5edd8835d4d2461) )	/* char lookup table */
		ROM_LOAD( "1c.bin",       0x0120, 0x0100, CRC(b6f585fb) SHA1(dd10147c4f05fede7ae6e7a760681700a660e87e) )	/* sprite lookup table */
		ROM_LOAD( "5c.bin",       0x0220, 0x0100, CRC(8bd565f6) SHA1(bedba65816abfc2ebeacac6ee335ca6f136e3e3d) )	/* unknown */
	
		ROM_REGION( 0x0100, REGION_SOUND1, 0 )	/* sound prom */
		ROM_LOAD( "1d.bin",       0x0000, 0x0100, CRC(86d92b24) SHA1(6bef9102b97c83025a2cf84e89d95f2d44c3d2ed) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_galagab2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code for the first CPU  */
		ROM_LOAD( "g1",           0x0000, 0x1000, CRC(ab036c9f) SHA1(ca7f5da42d4e76fd89bb0b35198a23c01462fbfe) )
		ROM_LOAD( "g2",           0x1000, 0x1000, CRC(d9232240) SHA1(ab202aa259c3d332ef13dfb8fc8580ce2a5a253d) )
		ROM_LOAD( "04j_g03.bin",  0x2000, 0x1000, CRC(753ce503) SHA1(481f443aea3ed3504ec2f3a6bfcf3cd47e2f8f81) )
		ROM_LOAD( "g4",           0x3000, 0x1000, CRC(499fcc76) SHA1(ddb8b121903646c320939c7d13f4aa4ebb130378) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for the second CPU */
		ROM_LOAD( "04e_g05.bin",  0x0000, 0x1000, CRC(3102fccd) SHA1(d29b68d6aab3217fa2106b3507b9273ff3f927bf) )
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 )     /* 64k for the third CPU  */
		ROM_LOAD( "04d_g06.bin",  0x0000, 0x1000, CRC(8995088d) SHA1(d6cb439de0718826d1a0363c9d77de8740b18ecf) )
	
		ROM_REGION( 0x10000, REGION_CPU4, 0 )	/* 64k for a Z80 which emulates the custom I/O chip (not used) */
		ROM_LOAD( "10h_g07.bin",  0x0000, 0x1000, CRC(035e300c) SHA1(cfda2467e71c27381b7150ff8fc7b69d61df123a) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "gallag.8",     0x0000, 0x1000, CRC(169a98a4) SHA1(edbeb11076061e744ea88d9899dbdfe0964c7e78) )
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "07e_g10.bin",  0x0000, 0x1000, CRC(ad447c80) SHA1(e697c180178cabd1d32483c5d8889a40633f7857) )
		ROM_LOAD( "07h_g09.bin",  0x1000, 0x1000, CRC(dd6f1afc) SHA1(c340ed8c25e0979629a9a1730edc762bd72d0cff) )
	
		ROM_REGION( 0x0320, REGION_PROMS, 0 )
		ROM_LOAD( "5n.bin",       0x0000, 0x0020, CRC(54603c6b) SHA1(1a6dea13b4af155d9cb5b999a75d4f1eb9c71346) )	/* palette */
		ROM_LOAD( "2n.bin",       0x0020, 0x0100, CRC(a547d33b) SHA1(7323084320bb61ae1530d916f5edd8835d4d2461) )	/* char lookup table */
		ROM_LOAD( "1c.bin",       0x0120, 0x0100, CRC(b6f585fb) SHA1(dd10147c4f05fede7ae6e7a760681700a660e87e) )	/* sprite lookup table */
		ROM_LOAD( "5c.bin",       0x0220, 0x0100, CRC(8bd565f6) SHA1(bedba65816abfc2ebeacac6ee335ca6f136e3e3d) )	/* unknown */
	
		ROM_REGION( 0x0100, REGION_SOUND1, 0 )	/* sound prom */
		ROM_LOAD( "1d.bin",       0x0000, 0x0100, CRC(86d92b24) SHA1(6bef9102b97c83025a2cf84e89d95f2d44c3d2ed) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_galaga84 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code for the first CPU  */
		ROM_LOAD( "g1",           0x0000, 0x1000, CRC(ab036c9f) SHA1(ca7f5da42d4e76fd89bb0b35198a23c01462fbfe) )
		ROM_LOAD( "gal84_u2",     0x1000, 0x1000, CRC(4d832a30) SHA1(88ee11df88cf08005efccd6305f87fb3e2797db6) )
		ROM_LOAD( "04j_g03.bin",  0x2000, 0x1000, CRC(753ce503) SHA1(481f443aea3ed3504ec2f3a6bfcf3cd47e2f8f81) )
		ROM_LOAD( "g4",           0x3000, 0x1000, CRC(499fcc76) SHA1(ddb8b121903646c320939c7d13f4aa4ebb130378) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for the second CPU */
		ROM_LOAD( "gal84_u5",     0x0000, 0x1000, CRC(bb5caae3) SHA1(e957a581463caac27bc37ca2e2a90f27e4f62b6f) )
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 )     /* 64k for the third CPU  */
		ROM_LOAD( "04d_g06.bin",  0x0000, 0x1000, CRC(8995088d) SHA1(d6cb439de0718826d1a0363c9d77de8740b18ecf) )
	
		ROM_REGION( 0x10000, REGION_CPU4, 0 )	/* 64k for a Z80 which emulates the custom I/O chip (not used) */
		ROM_LOAD( "10h_g07.bin",  0x0000, 0x1000, CRC(035e300c) SHA1(cfda2467e71c27381b7150ff8fc7b69d61df123a) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "07m_g08.bin",  0x0000, 0x1000, CRC(58b2f47c) SHA1(62f1279a784ab2f8218c4137c7accda00e6a3490) )
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "gal84u4d",     0x0000, 0x1000, CRC(22e339d5) SHA1(9ac2887ede802d28daa4ad0a0a54bcf7b1155a2e) )
		ROM_LOAD( "gal84u4e",     0x1000, 0x1000, CRC(60dcf940) SHA1(6530aa5b4afef4a8422ece76a93d0c5b1d93355e) )
	
		ROM_REGION( 0x0320, REGION_PROMS, 0 )
		ROM_LOAD( "5n.bin",       0x0000, 0x0020, CRC(54603c6b) SHA1(1a6dea13b4af155d9cb5b999a75d4f1eb9c71346) )	/* palette */
		ROM_LOAD( "2n.bin",       0x0020, 0x0100, CRC(a547d33b) SHA1(7323084320bb61ae1530d916f5edd8835d4d2461) )	/* char lookup table */
		ROM_LOAD( "1c.bin",       0x0120, 0x0100, CRC(b6f585fb) SHA1(dd10147c4f05fede7ae6e7a760681700a660e87e) )	/* sprite lookup table */
		ROM_LOAD( "5c.bin",       0x0220, 0x0100, CRC(8bd565f6) SHA1(bedba65816abfc2ebeacac6ee335ca6f136e3e3d) )	/* unknown */
	
		ROM_REGION( 0x0100, REGION_SOUND1, 0 )	/* sound prom */
		ROM_LOAD( "1d.bin",       0x0000, 0x0100, CRC(86d92b24) SHA1(6bef9102b97c83025a2cf84e89d95f2d44c3d2ed) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_nebulbee = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code for the first CPU  */
		ROM_LOAD( "nebulbee.01",  0x0000, 0x1000, CRC(f405f2c4) SHA1(9249afeffd8df0f24539ea9b4f88c23a6ad58d8c) )
		ROM_LOAD( "nebulbee.02",  0x1000, 0x1000, CRC(31022b60) SHA1(90e64afb4128c6dfeeee89635ea9f97a34f70f5f) )
		ROM_LOAD( "04j_g03.bin",  0x2000, 0x1000, CRC(753ce503) SHA1(481f443aea3ed3504ec2f3a6bfcf3cd47e2f8f81) )
		ROM_LOAD( "nebulbee.04",  0x3000, 0x1000, CRC(d76788a5) SHA1(adcb83cf64951d86c701a99b410e9230912f8a48) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for the second CPU */
		ROM_LOAD( "04e_g05.bin",  0x0000, 0x1000, CRC(3102fccd) SHA1(d29b68d6aab3217fa2106b3507b9273ff3f927bf) )
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 )     /* 64k for the third CPU  */
		ROM_LOAD( "04d_g06.bin",  0x0000, 0x1000, CRC(8995088d) SHA1(d6cb439de0718826d1a0363c9d77de8740b18ecf) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "07m_g08.bin",  0x0000, 0x1000, CRC(58b2f47c) SHA1(62f1279a784ab2f8218c4137c7accda00e6a3490) )
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "07e_g10.bin",  0x0000, 0x1000, CRC(ad447c80) SHA1(e697c180178cabd1d32483c5d8889a40633f7857) )
		ROM_LOAD( "07h_g09.bin",  0x1000, 0x1000, CRC(dd6f1afc) SHA1(c340ed8c25e0979629a9a1730edc762bd72d0cff) )
	
		ROM_REGION( 0x0320, REGION_PROMS, 0 )
		ROM_LOAD( "5n.bin",       0x0000, 0x0020, CRC(54603c6b) SHA1(1a6dea13b4af155d9cb5b999a75d4f1eb9c71346) )	/* palette */
		ROM_LOAD( "2n.bin",       0x0020, 0x0100, CRC(a547d33b) SHA1(7323084320bb61ae1530d916f5edd8835d4d2461) )	/* char lookup table */
		ROM_LOAD( "1c.bin",       0x0120, 0x0100, CRC(b6f585fb) SHA1(dd10147c4f05fede7ae6e7a760681700a660e87e) )	/* sprite lookup table */
		ROM_LOAD( "5c.bin",       0x0220, 0x0100, CRC(8bd565f6) SHA1(bedba65816abfc2ebeacac6ee335ca6f136e3e3d) )	/* unknown */
	
		ROM_REGION( 0x0100, REGION_SOUND1, 0 )	/* sound prom */
		ROM_LOAD( "1d.bin",       0x0000, 0x0100, CRC(86d92b24) SHA1(6bef9102b97c83025a2cf84e89d95f2d44c3d2ed) )
	ROM_END(); }}; 
	
	/*
	
	Gatsbee (Galaga mod/bootleg)
	
	This game runs on modified bootleg Galaga hardware (blue board with PCB numbers DG-09-02 and DG-07-02)
	
	ROM8: is a 2764. pins 1, 26, 27, 28 tied together.
	      pin2 out of socket, has wire that is tied to pin 4 of a LS259 that sits on top of the main Z80
	      CPU located at 5B/6B
	
	Z80: There are 2 logic chips sitting on top of it which are wired up to the Z80 and to each other.
	     Looks like this....
	     |-------------------|
	     |  LS32   LS259     <
	     |-------------------|
	
	
	Bend all the legs outwards.
	Line up the LS259 so pin 16 is in line with Z80 pin 11
	Line up the LS32 so pin 7 is in line with Z80 pin 29
	Atach the 2 chips to the top of the Z80 with some glue
	Connect like this....
	
	LS32 pin 1 tied to Z80 pin 22
	LS32 pin 2 tied to Z80 pin 19
	LS32 pin 3,4 tied together
	LS32 pin 5 tied to Z80 pin 4
	LS32 pin 6 tied to pin 10 LS32
	LS32 pin 7 tied to Z80 pin 29 (GND)
	LS32 pin 8 tied to LS259 pin 14
	LS32 pin 9 tied to Z80 pin 5
	LS32 pins 11, 12, 13 have NC
	LS32 pin 14 tied to Z80 pin 11 (+5V)
	
	LS259 pin 1 tied to Z80 pin 30
	LS259 pin 2 tied to Z80 pin 31
	LS259 pin 3 tied to Z80 pin 32
	LS259 pin 4 to ROM 8 (as above)
	LS259 pins 5, 6, 7 have NC
	LS259 pin 8 tied to Z80 pin 29 (GND)
	LS259 pins 9, 10, 11, 12 have NC
	LS259 pin 13 tied to Z80 pin 14
	LS259 pin 15 tied to Z80 pin 26
	LS259 pin 16 tied to Z80 pin 11
	
	
	*/
	
	static RomLoadHandlerPtr rom_gatsbee = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )     /* 64k for code for the first CPU  */
		ROM_LOAD( "1.4b",	0x0000, 0x1000, CRC(9fb8e28b) SHA1(7171e3fb37b0d6cc8f7a023c1775080d5986de99) )
		ROM_LOAD( "2.4c",	0x1000, 0x1000, CRC(bf6cb840) SHA1(5763140d32d35a38cdcb49e6de1fd5b07a9e8cc2) )
		ROM_LOAD( "3.4d",	0x2000, 0x1000, CRC(3604e2dd) SHA1(1736cf8497f7ac28e92ca94fa137c144353dc192) )
		ROM_LOAD( "4.4e",	0x3000, 0x1000, CRC(bf9f613b) SHA1(41c852fc77f0f35bf48a5b81a19234ed99871c89) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for the second CPU */
		ROM_LOAD( "04e_g05.bin",  0x0000, 0x1000, CRC(3102fccd) SHA1(d29b68d6aab3217fa2106b3507b9273ff3f927bf) )
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 )     /* 64k for the third CPU  */
		ROM_LOAD( "04d_g06.bin",  0x0000, 0x1000, CRC(8995088d) SHA1(d6cb439de0718826d1a0363c9d77de8740b18ecf) )
	
		ROM_REGION( 0x10000, REGION_CPU4, 0 )	/* 64k for a Z80 which emulates the custom I/O chip (not used) */
		ROM_LOAD( "gallag.6",     0x0000, 0x1000, CRC(001b70bc) SHA1(b465eee91e75257b7b049d49c0064ab5fd66c576) )
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "8.5r",  0x0000, 0x2000, CRC(b324f650) SHA1(7bcb254f7cf03bd84291b9fdc27b8962b3e12aa4) )
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "gal84u4d",     0x0000, 0x1000, CRC(22e339d5) SHA1(9ac2887ede802d28daa4ad0a0a54bcf7b1155a2e) )
		ROM_LOAD( "gal84u4e",     0x1000, 0x1000, CRC(60dcf940) SHA1(6530aa5b4afef4a8422ece76a93d0c5b1d93355e) )
	
		ROM_REGION( 0x0320, REGION_PROMS, 0 )
		ROM_LOAD( "5n.bin",       0x0000, 0x0020, CRC(54603c6b) SHA1(1a6dea13b4af155d9cb5b999a75d4f1eb9c71346) )	/* palette */
		ROM_LOAD( "2n.bin",       0x0020, 0x0100, CRC(a547d33b) SHA1(7323084320bb61ae1530d916f5edd8835d4d2461) )	/* char lookup table */
		ROM_LOAD( "1c.bin",       0x0120, 0x0100, CRC(b6f585fb) SHA1(dd10147c4f05fede7ae6e7a760681700a660e87e) )	/* sprite lookup table */
		ROM_LOAD( "5c.bin",       0x0220, 0x0100, CRC(8bd565f6) SHA1(bedba65816abfc2ebeacac6ee335ca6f136e3e3d) )	/* unknown */
	
		ROM_REGION( 0x0100, REGION_SOUND1, 0 )	/* sound prom */
		ROM_LOAD( "1d.bin",       0x0000, 0x0100, CRC(86d92b24) SHA1(6bef9102b97c83025a2cf84e89d95f2d44c3d2ed) )
	ROM_END(); }}; 
	
	public static DriverInitHandlerPtr init_gatsbee  = new DriverInitHandlerPtr() { public void handler(){
		install_mem_write_handler(0, 0x1000, 0x1000, gatsbee_bank_w);
	} };
	
	GAMEX( 1981, galaga,   0,      galaga, galaganm, 0, ROT90, "Namco", "Galaga (Namco)", GAME_IMPERFECT_GRAPHICS )
	GAMEX( 1981, galagamw, galaga, galaga, galaga,   0, ROT90, "[Namco] (Midway license)", "Galaga (Midway)", GAME_IMPERFECT_GRAPHICS )
	GAMEX( 1981, galagads, galaga, galaga, galaga,   0, ROT90, "hack", "Galaga (fast shoot)", GAME_IMPERFECT_GRAPHICS )
	GAMEX( 1982, gallag,   galaga, galaga, galaganm, 0, ROT90, "bootleg", "Gallag", GAME_IMPERFECT_GRAPHICS )
	GAMEX( 1981, galagab2, galaga, galaga, galaganm, 0, ROT90, "bootleg", "Galaga (bootleg)", GAME_IMPERFECT_GRAPHICS )
	GAMEX( 1984, galaga84, galaga, galaga, galaganm, 0, ROT90, "hack", "Galaga '84", GAME_IMPERFECT_GRAPHICS )
	GAMEX( 1984, nebulbee, galaga, galaga, galaganm, 0, ROT90, "hack", "Nebulous Bee", GAME_IMPERFECT_GRAPHICS )
	GAMEX( 1984, gatsbee,  galaga, galaga, galaganm, gatsbee, ROT90, "Uchida", "Gatsbee", GAME_IMPERFECT_GRAPHICS )
}