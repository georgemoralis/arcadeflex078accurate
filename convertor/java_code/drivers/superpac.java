/***************************************************************************

Super Pac-Man memory map (preliminary)

driver by Aaron Giles

CPU #1:
0000-03ff video RAM
0400-07ff color RAM
0800-0f7f RAM
0f80-0fff sprite data 1 (sprite number & color)
1000-177f RAM
1780-17ff sprite data 2 (x, y position)
1800-1f7f RAM
1f80-1fff sprite data 3 (high bit of y, flip flags, double-size flags)
2000      flip screen
4040-43ff RAM shared with CPU #2
4800-480f custom I/O chip #1
4810-481f custom I/O chip #2
5000      reset CPU #2
5002-5003 IRQ enable
5008-5009 sound enable
500a-500b CPU #2 enable
8000      watchdog timer
c000-ffff ROM

CPU #2:
0000-0040 sound registers
0040-03ff RAM shared with CPU #1
f000-ffff ROM

Interrupts:
CPU #1 IRQ generated by VBLANK
CPU #2 uses no interrupts

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class superpac
{
	
	
	extern unsigned char *superpac_sharedram;
	extern unsigned char *superpac_customio_1,*superpac_customio_2;
	WRITE_HANDLER( superpac_sharedram_w );
	WRITE_HANDLER( superpac_interrupt_enable_w );
	WRITE_HANDLER( superpac_cpu_enable_w );
	WRITE_HANDLER( superpac_reset_2_w );
	
	WRITE_HANDLER( superpac_flipscreen_w );
	
	
	
	/*************************************
	 *
	 *	Main CPU memory handlers
	 *
	 *************************************/
	
	static MEMORY_READ_START( readmem_cpu1 )
		{ 0x0000, 0x1fff, MRA_RAM },
		{ 0x2000, 0x2000, superpac_flipscreen_r },
		{ 0x4040, 0x43ff, superpac_sharedram_r },	/* Pac'n Pal only */
		{ 0x4800, 0x480f, superpac_customio_1_r },
		{ 0x4810, 0x481f, superpac_customio_2_r },
		{ 0xa000, 0xffff, MRA_ROM },
	MEMORY_END
	
	
	static MEMORY_WRITE_START( writemem_cpu1 )
		{ 0x0000, 0x03ff, videoram_w, &videoram, &videoram_size },
		{ 0x0400, 0x07ff, colorram_w, &colorram },
		{ 0x0800, 0x0f7f, MWA_RAM },
		{ 0x0f80, 0x0fff, MWA_RAM, &spriteram, &spriteram_size },
		{ 0x1000, 0x177f, MWA_RAM },
		{ 0x1780, 0x17ff, MWA_RAM, &spriteram_2 },
		{ 0x1800, 0x1f7f, MWA_RAM },
		{ 0x1f80, 0x1fff, MWA_RAM, &spriteram_3 },
		{ 0x2000, 0x2000, superpac_flipscreen_w },
		{ 0x4040, 0x43ff, superpac_sharedram_w, &superpac_sharedram },
		{ 0x4800, 0x480f, MWA_RAM, &superpac_customio_1 },
		{ 0x4810, 0x481f, MWA_RAM, &superpac_customio_2 },
		{ 0x5000, 0x5000, superpac_reset_2_w },
		{ 0x5002, 0x5003, superpac_interrupt_enable_w },
		{ 0x5008, 0x5009, mappy_sound_enable_w },
		{ 0x500a, 0x500b, superpac_cpu_enable_w },
		{ 0x8000, 0x8000, watchdog_reset_w },
		{ 0xa000, 0xffff, MWA_ROM },
	MEMORY_END
	
	
	
	/*************************************
	 *
	 *	Sound CPU memory handlers
	 *
	 *************************************/
	
	static MEMORY_READ_START( readmem_cpu2 )
		{ 0x0040, 0x03ff, superpac_sharedram_r },
		{ 0xf000, 0xffff, MRA_ROM },
	MEMORY_END
	
	
	static MEMORY_WRITE_START( writemem_cpu2 )
		{ 0x0000, 0x003f, mappy_sound_w, &mappy_soundregs },
		{ 0x0040, 0x03ff, superpac_sharedram_w },
		{ 0x2000, 0x2001, superpac_interrupt_enable_w },
		{ 0x2006, 0x2007, mappy_sound_enable_w },
		{ 0xf000, 0xffff, MWA_ROM },
	MEMORY_END
	
	
	
	/*************************************
	 *
	 *	Input ports
	 *
	 *************************************/
	
	static InputPortHandlerPtr input_ports_superpac = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( superpac )
		PORT_START(); 	/* DSW0 */
		PORT_DIPNAME( 0x0f, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Rank 0-Normal" );
		PORT_DIPSETTING(    0x01, "Rank 1-Easiest" );
		PORT_DIPSETTING(    0x02, "Rank 2" );
		PORT_DIPSETTING(    0x03, "Rank 3" );
		PORT_DIPSETTING(    0x04, "Rank 4" );
		PORT_DIPSETTING(    0x05, "Rank 5" );
		PORT_DIPSETTING(    0x06, "Rank 6-Medium" );
		PORT_DIPSETTING(    0x07, "Rank 7" );
		PORT_DIPSETTING(    0x08, "Rank 8-Default" );
		PORT_DIPSETTING(    0x09, "Rank 9" );
		PORT_DIPSETTING(    0x0a, "Rank A" );
		PORT_DIPSETTING(    0x0b, "Rank B-Hardest" );
		PORT_DIPSETTING(    0x0c, "Rank C-Easy Auto" );
		PORT_DIPSETTING(    0x0d, "Rank D-Auto" );
		PORT_DIPSETTING(    0x0e, "Rank E-Auto" );
		PORT_DIPSETTING(    0x0f, "Rank F-Hard Auto" );
		PORT_DIPNAME( 0x30, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x20, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, "Freeze" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0x07, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x07, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_7C") );
		PORT_DIPNAME( 0x38, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x38, "None" );
		PORT_DIPSETTING(    0x30, "30k" );
		PORT_DIPSETTING(    0x08, "30k 80k" );
		PORT_DIPSETTING(    0x00, "30k 100k" );
		PORT_DIPSETTING(    0x10, "30k 120k" );
		PORT_DIPSETTING(    0x18, "30k 80k 80k" );
		PORT_DIPSETTING(    0x20, "30k 100k 100k" );
		PORT_DIPSETTING(    0x28, "30k 120k 120k" );
	/* TODO: bonus scores for 5 lives */
	/* 	PORT_DIPNAME( 0x38, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x38, "None" );
		PORT_DIPSETTING(    0x28, "30k" );
		PORT_DIPSETTING(    0x30, "40k" );
		PORT_DIPSETTING(    0x00, "30k 100k" );
		PORT_DIPSETTING(    0x08, "30k 120k" );
		PORT_DIPSETTING(    0x10, "40k 120k" );
		PORT_DIPSETTING(    0x18, "30k 100k 100k" );
		PORT_DIPSETTING(    0x20, "40k 120k 120k" );*/
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x40, "1" );
		PORT_DIPSETTING(    0x80, "2" );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0xc0, "5" );
	
		PORT_START(); 	/* FAKE */
		/* The player inputs are not memory mapped, they are handled by an I/O chip. */
		/* These fake input ports are read by mappy_customio_data_r() */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_4WAY );
		PORT_BIT_IMPULSE( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1, 1 );
		PORT_BITX(0x20, IP_ACTIVE_HIGH, IPT_BUTTON1, 0, IP_KEY_PREVIOUS, IP_JOY_PREVIOUS );
		PORT_BIT( 0xc0, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* FAKE */
		PORT_BIT_IMPULSE( 0x01, IP_ACTIVE_HIGH, IPT_COIN1, 1 );
		PORT_BIT_IMPULSE( 0x02, IP_ACTIVE_HIGH, IPT_COIN2, 1 );
		PORT_BIT( 0x0c, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT_IMPULSE( 0x10, IP_ACTIVE_HIGH, IPT_START1, 1 );
		PORT_BIT_IMPULSE( 0x20, IP_ACTIVE_HIGH, IPT_START2, 1 );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Cocktail") );
		PORT_SERVICE( 0x80, IP_ACTIVE_HIGH );
	
		PORT_START(); 	/* FAKE */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT_IMPULSE( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL, 1 );
		PORT_BITX(0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL, 0, IP_KEY_PREVIOUS, IP_JOY_PREVIOUS );
		PORT_BIT( 0xc0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortHandlerPtr input_ports_pacnpal = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( pacnpal )
		PORT_START(); 	/* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x0c, 0x00, "Rank" );
		PORT_DIPSETTING(    0x00, "A" );
		PORT_DIPSETTING(    0x04, "B" );
		PORT_DIPSETTING(    0x08, "C" );
		PORT_DIPSETTING(    0x0c, "D" );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0x07, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x07, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_7C") );
		PORT_DIPNAME( 0x38, 0x18, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPSETTING(    0x38, "30k" );
		PORT_DIPSETTING(    0x18, "20k 70k" );
		PORT_DIPSETTING(    0x20, "30k 70k" );
		PORT_DIPSETTING(    0x28, "30k 80k" );
		PORT_DIPSETTING(    0x30, "30k 100k" );
		PORT_DIPSETTING(    0x08, "20k 70k 70k" );
		PORT_DIPSETTING(    0x10, "30k 80k 80k" );
		/* TODO: bonus scores are different for 5 lives */
	/* 	PORT_DIPNAME( 0x38, 0x18, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPSETTING(    0x30, "30k" );
		PORT_DIPSETTING(    0x38, "40k" );
		PORT_DIPSETTING(    0x18, "30k 80k" );
		PORT_DIPSETTING(    0x20, "30k 100k" );
		PORT_DIPSETTING(    0x28, "40k 120k" );
		PORT_DIPSETTING(    0x08, "30k 80k 80k" );
		PORT_DIPSETTING(    0x10, "40k 100k 100k" );*/
		PORT_DIPNAME( 0xc0, 0x80, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x40, "2" );
		PORT_DIPSETTING(    0x80, "3" );
		PORT_DIPSETTING(    0xc0, "5" );
	
		PORT_START(); 	/* FAKE */
		/* The player inputs are not memory mapped, they are handled by an I/O chip. */
		/* These fake input ports are read by mappy_customio_data_r() */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_4WAY );
		PORT_BIT_IMPULSE( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1, 2 );
		PORT_BIT_IMPULSE( 0x20, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL, 2 );
		PORT_BIT( 0xc0, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START(); 	/* FAKE */
		PORT_BIT_IMPULSE( 0x01, IP_ACTIVE_HIGH, IPT_COIN1, 2 );
		PORT_BIT_IMPULSE( 0x02, IP_ACTIVE_HIGH, IPT_COIN2, 2 );
		PORT_BIT( 0x0c, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT_IMPULSE( 0x10, IP_ACTIVE_HIGH, IPT_START1, 2 );
		PORT_BIT_IMPULSE( 0x20, IP_ACTIVE_HIGH, IPT_START2, 2 );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Cocktail") );
		PORT_SERVICE( 0x80, IP_ACTIVE_HIGH );
	
		PORT_START(); 	/* FAKE */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	
	/*************************************
	 *
	 *	Graphics layouts
	 *
	 *************************************/
	
	static struct GfxLayout charlayout =
	{
		8,8,
		RGN_FRAC(1,1),
		2,
		{ 0, 4 },
		{ 8*8+0, 8*8+1, 8*8+2, 8*8+3, 0, 1, 2, 3 },
		{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		16*8
	};
	
	
	static struct GfxLayout spritelayout =
	{
		16,16,
		RGN_FRAC(1,1),
		2,
		{ 0, 4 },
		{ 0, 1, 2, 3, 8*8, 8*8+1, 8*8+2, 8*8+3,
				16*8+0, 16*8+1, 16*8+2, 16*8+3, 24*8+0, 24*8+1, 24*8+2, 24*8+3 },
		{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				32*8, 33*8, 34*8, 35*8, 36*8, 37*8, 38*8, 39*8 },
		64*8
	};
	
	
	static struct GfxDecodeInfo gfxdecodeinfo[] =
	{
		{ REGION_GFX1, 0, &charlayout,      0, 64 },
		{ REGION_GFX2, 0, &spritelayout, 64*4, 64 },
		{ -1 }
	};
	
	
	
	/*************************************
	 *
	 *	Sound interfaces
	 *
	 *************************************/
	
	static struct namco_interface namco_interface =
	{
		24000,	/* sample rate */
		8,		/* number of voices */
		100,	/* playback volume */
		REGION_SOUND1	/* memory region */
	};
	
	
	
	/*************************************
	 *
	 *	Machine driver
	 *
	 *************************************/
	
	static MACHINE_DRIVER_START( superpac )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M6809, 18432000/12)	/* 1.536 MHz */
		MDRV_CPU_MEMORY(readmem_cpu1,writemem_cpu1)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(M6809, 18432000/12)	/* 1.536 MHz */
		MDRV_CPU_MEMORY(readmem_cpu2,writemem_cpu2)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_FRAMES_PER_SECOND(60.606060)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(100)
	
		MDRV_MACHINE_INIT(superpac)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(36*8, 28*8)
		MDRV_VISIBLE_AREA(0*8, 36*8-1, 0*8, 28*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(32)
		MDRV_COLORTABLE_LENGTH(4*(64+64))
	
		MDRV_PALETTE_INIT(superpac)
		MDRV_VIDEO_START(generic)
		MDRV_VIDEO_UPDATE(superpac)
	
		/* sound hardware */
		MDRV_SOUND_ADD(NAMCO, namco_interface)
	MACHINE_DRIVER_END
	
	
	
	/*************************************
	 *
	 *	ROM definitions
	 *
	 *************************************/
	
	static RomLoadHandlerPtr rom_superpac = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "sp1.2",        0xc000, 0x2000, CRC(4bb33d9c) SHA1(dd87f71b4db090a32a6b791079eedd17580cc741) )
		ROM_LOAD( "sp1.1",        0xe000, 0x2000, CRC(846fbb4a) SHA1(f6bf90281986b9b7a3ef1dbbeddb722182e84d7c) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the second CPU */
		ROM_LOAD( "spc-3.1k",     0xf000, 0x1000, CRC(04445ddb) SHA1(ce7d14963d5ddaefdeaf433a6f82c43cd1611d9b) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "sp1.6",        0x0000, 0x1000, CRC(91c5935c) SHA1(10579edabc26a0910253fab7d41b4c19ecdaaa09) )
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "spv-2.3f",     0x0000, 0x2000, CRC(670a42f2) SHA1(9171922df07e31fd1dc415766f7d2cc50a9d10dc) )
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 )
		ROM_LOAD( "superpac.4c",  0x0000, 0x0020, CRC(9ce22c46) SHA1(d97f53ef4c5ef26659a22ed0de4ce7ef3758c924) ) /* palette */
		ROM_LOAD( "superpac.4e",  0x0020, 0x0100, CRC(1253c5c1) SHA1(df46a90170e9761d45c90fbd04ef2aa1e8c9944b) ) /* chars */
		ROM_LOAD( "superpac.3l",  0x0120, 0x0100, CRC(d4d7026f) SHA1(a486573437c54bfb503424574ad82655491e85e1) ) /* sprites */
	
		ROM_REGION( 0x0100, REGION_SOUND1, 0 )	/* sound prom */
		ROM_LOAD( "superpac.3m",  0x0000, 0x0100, CRC(ad43688f) SHA1(072f427453efb1dda8147da61804fff06e1bc4d5) )
	ROM_END(); }}; 
	
	
	static RomLoadHandlerPtr rom_superpcm = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "spc-2.1c",     0xc000, 0x2000, CRC(1a38c30e) SHA1(ae0ee9f3df0991a80698fe745a7a853a4bb60710) )
		ROM_LOAD( "spc-1.1b",     0xe000, 0x2000, CRC(730e95a9) SHA1(ca73c8bcb03c2f5c05968c707a5d3f7f9956b886) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the second CPU */
		ROM_LOAD( "spc-3.1k",     0xf000, 0x1000, CRC(04445ddb) SHA1(ce7d14963d5ddaefdeaf433a6f82c43cd1611d9b) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "spv-1.3c",     0x0000, 0x1000, CRC(78337e74) SHA1(11222adb55e6bce508896ccb1f6dbab0c1d44e5b) )
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "spv-2.3f",     0x0000, 0x2000, CRC(670a42f2) SHA1(9171922df07e31fd1dc415766f7d2cc50a9d10dc) )
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 )
		ROM_LOAD( "superpac.4c",  0x0000, 0x0020, CRC(9ce22c46) SHA1(d97f53ef4c5ef26659a22ed0de4ce7ef3758c924) ) /* palette */
		ROM_LOAD( "superpac.4e",  0x0020, 0x0100, CRC(1253c5c1) SHA1(df46a90170e9761d45c90fbd04ef2aa1e8c9944b) ) /* chars */
		ROM_LOAD( "superpac.3l",  0x0120, 0x0100, CRC(d4d7026f) SHA1(a486573437c54bfb503424574ad82655491e85e1) ) /* sprites */
	
		ROM_REGION( 0x0100, REGION_SOUND1, 0 )	/* sound prom */
		ROM_LOAD( "superpac.3m",  0x0000, 0x0100, CRC(ad43688f) SHA1(072f427453efb1dda8147da61804fff06e1bc4d5) )
	ROM_END(); }}; 
	
	
	static RomLoadHandlerPtr rom_pacnpal = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "pap13b.cpu",   0xa000, 0x2000, CRC(ed64a565) SHA1(b16930981490d97486d4df96acbb3d1cddbd3a80) )
		ROM_LOAD( "pap12b.cpu",   0xc000, 0x2000, CRC(15308bcf) SHA1(334603f8904f8968d05edc420b5f9e3b483ee86d) )
		ROM_LOAD( "pap11b.cpu",   0xe000, 0x2000, CRC(3cac401c) SHA1(38a14228469fa4a20cbc5d862198dc901842682e) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the second CPU */
		ROM_LOAD( "pap14.cpu",    0xf000, 0x1000, CRC(330e20de) SHA1(5b23e5dcc38dc644a36efc8b03eba34cea540bea) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "pap16.cpu",    0x0000, 0x1000, CRC(a36b96cb) SHA1(e0a11b5a43cbf756ddb045c743973d0a55dbb979) )
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "pap15.vid",    0x0000, 0x2000, CRC(fb6f56e3) SHA1(fd10d2ee49b4e059e9ef6046bc86d97e3185164d) )
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 )
		ROM_LOAD( "papi6.vid",    0x0000, 0x0020, CRC(52634b41) SHA1(dfb109c8e2c62ae1612ba0e3272468d152123842) ) /* palette */
		ROM_LOAD( "papi5.vid",    0x0020, 0x0100, CRC(ac46203c) SHA1(3f47f1991aab9640c0d5f70fad85d20d6cf2ea3d) ) /* chars */
		ROM_LOAD( "papi4.vid",    0x0120, 0x0100, CRC(686bde84) SHA1(541d08b43dbfb789c2867955635d2c9e051fedd9) ) /* sprites */
	
		ROM_REGION( 0x0100, REGION_SOUND1, 0 )	/* sound prom */
		ROM_LOAD( "papi3.cpu",    0x0000, 0x0100, CRC(83c31a98) SHA1(8f1219a6c2b565ae9d8f72a9c277dc4bd38ec40f) )
	ROM_END(); }}; 
	
	
	static RomLoadHandlerPtr rom_pacnpal2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "pap1_3.1d",    0xa000, 0x2000, CRC(d7ec2719) SHA1(b633a5360a199d528bcef209c06a21f266525769) )
		ROM_LOAD( "pap1_2.1c",    0xc000, 0x2000, CRC(0245396e) SHA1(7e8467e317879621a7b31bc922b5187f20fcea78) )
		ROM_LOAD( "pap1_1.1b",    0xe000, 0x2000, CRC(7f046b58) SHA1(2024019e5fafb698bb5775075c9b88c5ed35f7ba) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the second CPU */
		ROM_LOAD( "pap14.cpu",    0xf000, 0x1000, CRC(330e20de) SHA1(5b23e5dcc38dc644a36efc8b03eba34cea540bea) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "pap16.cpu",    0x0000, 0x1000, CRC(a36b96cb) SHA1(e0a11b5a43cbf756ddb045c743973d0a55dbb979) )
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "pap15.vid",    0x0000, 0x2000, CRC(fb6f56e3) SHA1(fd10d2ee49b4e059e9ef6046bc86d97e3185164d) )
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 )
		ROM_LOAD( "papi6.vid",    0x0000, 0x0020, CRC(52634b41) SHA1(dfb109c8e2c62ae1612ba0e3272468d152123842) ) /* palette */
		ROM_LOAD( "papi5.vid",    0x0020, 0x0100, CRC(ac46203c) SHA1(3f47f1991aab9640c0d5f70fad85d20d6cf2ea3d) ) /* chars */
		ROM_LOAD( "papi4.vid",    0x0120, 0x0100, CRC(686bde84) SHA1(541d08b43dbfb789c2867955635d2c9e051fedd9) ) /* sprites */
	
		ROM_REGION( 0x0100, REGION_SOUND1, 0 )	/* sound prom */
		ROM_LOAD( "papi3.cpu",    0x0000, 0x0100, CRC(83c31a98) SHA1(8f1219a6c2b565ae9d8f72a9c277dc4bd38ec40f) )
	ROM_END(); }}; 
	
	
	static RomLoadHandlerPtr rom_pacnchmp = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "pap3.1d",      0xa000, 0x2000, CRC(20a07d3d) SHA1(2135ad154b575a73cfb1b0f0f282dfc013672aec) )
		ROM_LOAD( "pap3.1c",      0xc000, 0x2000, CRC(505bae56) SHA1(590ce9f0e92115a71eb76b71ab4eac16ffa2a28e) )
		ROM_LOAD( "pap11b.cpu",   0xe000, 0x2000, CRC(3cac401c) SHA1(38a14228469fa4a20cbc5d862198dc901842682e) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the second CPU */
		ROM_LOAD( "pap14.cpu",    0xf000, 0x1000, CRC(330e20de) SHA1(5b23e5dcc38dc644a36efc8b03eba34cea540bea) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "pap2.3c",      0x0000, 0x1000, CRC(93d15c30) SHA1(5da4120b680726c83a651b445254604cbf7cc883) )
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "pap2.3f",      0x0000, 0x2000, CRC(39f44aa4) SHA1(0696539cb2c7fcda2f6c295c7d65678dac18950b) )
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 )
		ROM_LOAD( "papi6.vid",    0x0000, 0x0020, BAD_DUMP CRC(52634b41) SHA1(dfb109c8e2c62ae1612ba0e3272468d152123842)  ) /* palette */
		ROM_LOAD( "papi5.vid",    0x0020, 0x0100, BAD_DUMP CRC(ac46203c) SHA1(3f47f1991aab9640c0d5f70fad85d20d6cf2ea3d)  ) /* chars */
		ROM_LOAD( "papi4.vid",    0x0120, 0x0100, BAD_DUMP CRC(686bde84) SHA1(541d08b43dbfb789c2867955635d2c9e051fedd9)  ) /* sprites */
	
		ROM_REGION( 0x0100, REGION_SOUND1, 0 )	/* sound prom */
		ROM_LOAD( "papi3.cpu",    0x0000, 0x0100, CRC(83c31a98) SHA1(8f1219a6c2b565ae9d8f72a9c277dc4bd38ec40f) )
	ROM_END(); }}; 
	
	
	
	/*************************************
	 *
	 *	Game drivers
	 *
	 *************************************/
	
	public static GameDriver driver_superpac	   = new GameDriver("1982"	,"superpac"	,"superpac.java"	,rom_superpac,null	,machine_driver_superpac	,input_ports_superpac	,null	,ROT90, "Namco", "Super Pac-Man" )
	public static GameDriver driver_superpcm	   = new GameDriver("1982"	,"superpcm"	,"superpac.java"	,rom_superpcm,driver_superpac	,machine_driver_superpac	,input_ports_superpac	,null	,ROT90, "[Namco] (Bally Midway license)", "Super Pac-Man (Midway)" )
	public static GameDriver driver_pacnpal	   = new GameDriver("1983"	,"pacnpal"	,"superpac.java"	,rom_pacnpal,null	,machine_driver_superpac	,input_ports_pacnpal	,null	,ROT90, "Namco", "Pac & Pal" )
	public static GameDriver driver_pacnpal2	   = new GameDriver("1983"	,"pacnpal2"	,"superpac.java"	,rom_pacnpal2,driver_pacnpal	,machine_driver_superpac	,input_ports_pacnpal	,null	,ROT90, "Namco", "Pac & Pal (older)" )
	public static GameDriver driver_pacnchmp	   = new GameDriver("1983"	,"pacnchmp"	,"superpac.java"	,rom_pacnchmp,driver_pacnpal	,machine_driver_superpac	,input_ports_pacnpal	,null	,ROT90, "Namco", "Pac-Man & Chomp Chomp", GAME_IMPERFECT_COLORS )
}
