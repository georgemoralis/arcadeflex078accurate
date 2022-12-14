/**************************************************************************

  Last Duel 			          - Capcom, 1988
  LED Storm 			          - Capcom, 1988
  Mad Gear                        - Capcom, 1989

  Emulation by Bryan McPhail, mish@tendril.co.uk

  Trivia ;)  The Mad Gear pcb has an unused pad on the board for an i8751
microcontroller.

TODO:
- The seem to be minor priority issues in Mad Gear, but the game might just
  be like that. The priority PROM is missing.
- visible area might be wrong

**************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class lastduel
{
	
	WRITE16_HANDLER( lastduel_vram_w );
	WRITE16_HANDLER( lastduel_flip_w );
	WRITE16_HANDLER( lastduel_scroll1_w );
	WRITE16_HANDLER( lastduel_scroll2_w );
	WRITE16_HANDLER( madgear_scroll1_w );
	WRITE16_HANDLER( madgear_scroll2_w );
	WRITE16_HANDLER( lastduel_scroll_w );
	
	
	/******************************************************************************/
	
	static WRITE16_HANDLER( lastduel_sound_w )
	{
		if (ACCESSING_LSB)
			soundlatch_w(0,data & 0xff);
	}
	
	/******************************************************************************/
	
	static MEMORY_READ16_START( lastduel_readmem )
		{ 0x000000, 0x05ffff, MRA16_ROM },
		{ 0xfc0800, 0xfc0fff, MRA16_RAM },
		{ 0xfc4000, 0xfc4001, input_port_0_word_r },
		{ 0xfc4002, 0xfc4003, input_port_1_word_r },
		{ 0xfc4004, 0xfc4005, input_port_2_word_r },
		{ 0xfc4006, 0xfc4007, input_port_3_word_r },
		{ 0xfcc000, 0xfcdfff, MRA16_RAM },
		{ 0xfd0000, 0xfd3fff, MRA16_RAM },
		{ 0xfd4000, 0xfd7fff, MRA16_RAM },
		{ 0xfd8000, 0xfd87ff, MRA16_RAM },
		{ 0xfe0000, 0xffffff, MRA16_RAM },
	MEMORY_END
	
	static MEMORY_WRITE16_START( lastduel_writemem )
		{ 0x000000, 0x05ffff, MWA16_ROM },
		{ 0xfc0000, 0xfc0003, MWA16_NOP }, /* Written rarely */
		{ 0xfc0800, 0xfc0fff, MWA16_RAM, &spriteram16, &spriteram_size },
		{ 0xfc4000, 0xfc4001, lastduel_flip_w },
		{ 0xfc4002, 0xfc4003, lastduel_sound_w },
		{ 0xfc8000, 0xfc8007, lastduel_scroll_w },
		{ 0xfcc000, 0xfcdfff, lastduel_vram_w, &lastduel_vram },
		{ 0xfd0000, 0xfd3fff, lastduel_scroll1_w, &lastduel_scroll1 },
		{ 0xfd4000, 0xfd7fff, lastduel_scroll2_w, &lastduel_scroll2 },
		{ 0xfd8000, 0xfd87ff, paletteram16_RRRRGGGGBBBBIIII_word_w, &paletteram16 },
		{ 0xfe0000, 0xffffff, MWA16_RAM },
	MEMORY_END
	
	static MEMORY_READ16_START( madgear_readmem )
		{ 0x000000, 0x07ffff, MRA16_ROM },
		{ 0xfc1800, 0xfc1fff, MRA16_RAM },
		{ 0xfc4000, 0xfc4001, input_port_0_word_r },
		{ 0xfc4002, 0xfc4003, input_port_1_word_r },
		{ 0xfc4004, 0xfc4005, input_port_2_word_r },
		{ 0xfc4006, 0xfc4007, input_port_3_word_r },
		{ 0xfc8000, 0xfc9fff, MRA16_RAM },
		{ 0xfcc000, 0xfcc7ff, MRA16_RAM },
		{ 0xfd4000, 0xfd7fff, MRA16_RAM },
		{ 0xfd8000, 0xfdffff, MRA16_RAM },
		{ 0xff0000, 0xffffff, MRA16_RAM },
	MEMORY_END
	
	static MEMORY_WRITE16_START( madgear_writemem )
		{ 0x000000, 0x07ffff, MWA16_ROM },
		{ 0xfc1800, 0xfc1fff, MWA16_RAM, &spriteram16, &spriteram_size },
		{ 0xfc4000, 0xfc4001, lastduel_flip_w },
		{ 0xfc4002, 0xfc4003, lastduel_sound_w },
		{ 0xfc8000, 0xfc9fff, lastduel_vram_w, &lastduel_vram },
		{ 0xfcc000, 0xfcc7ff, paletteram16_RRRRGGGGBBBBIIII_word_w, &paletteram16 },
		{ 0xfd0000, 0xfd0007, lastduel_scroll_w },
		{ 0xfd4000, 0xfd7fff, madgear_scroll1_w, &lastduel_scroll1 },
		{ 0xfd8000, 0xfdffff, madgear_scroll2_w, &lastduel_scroll2 },
		{ 0xff0000, 0xffffff, MWA16_RAM },
	MEMORY_END
	
	/******************************************************************************/
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xdfff, MRA_ROM ),
		new Memory_ReadAddress( 0xe000, 0xe7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xe800, 0xe800, YM2203_status_port_0_r ),
		new Memory_ReadAddress( 0xf000, 0xf000, YM2203_status_port_1_r ),
		new Memory_ReadAddress( 0xf800, 0xf800, soundlatch_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xdfff, MWA_ROM ),
		new Memory_WriteAddress( 0xe000, 0xe7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xe800, 0xe800, YM2203_control_port_0_w ),
		new Memory_WriteAddress( 0xe801, 0xe801, YM2203_write_port_0_w ),
		new Memory_WriteAddress( 0xf000, 0xf000, YM2203_control_port_1_w ),
		new Memory_WriteAddress( 0xf001, 0xf001, YM2203_write_port_1_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static WriteHandlerPtr mg_bankswitch_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int bankaddress;
		unsigned char *RAM = memory_region(REGION_CPU2);
	
		bankaddress = 0x10000 + (data & 0x01) * 0x4000;
		cpu_setbank(3,&RAM[bankaddress]);
	} };
	
	public static Memory_ReadAddress mg_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xcfff, MRA_BANK3 ),
		new Memory_ReadAddress( 0xd000, 0xd7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xf000, 0xf000, YM2203_status_port_0_r ),
		new Memory_ReadAddress( 0xf002, 0xf002, YM2203_status_port_1_r ),
		new Memory_ReadAddress( 0xf006, 0xf006, soundlatch_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress mg_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xcfff, MWA_ROM ),
		new Memory_WriteAddress( 0xd000, 0xd7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xf000, 0xf000, YM2203_control_port_0_w ),
		new Memory_WriteAddress( 0xf001, 0xf001, YM2203_write_port_0_w ),
		new Memory_WriteAddress( 0xf002, 0xf002, YM2203_control_port_1_w ),
		new Memory_WriteAddress( 0xf003, 0xf003, YM2203_write_port_1_w ),
		new Memory_WriteAddress( 0xf004, 0xf004, OKIM6295_data_0_w ),
		new Memory_WriteAddress( 0xf00a, 0xf00a, mg_bankswitch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	/******************************************************************************/
	
	static GfxLayout sprite_layout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,4),
		4,
		new int[] { RGN_FRAC(0,4), RGN_FRAC(1,4), RGN_FRAC(2,4), RGN_FRAC(3,4) },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
				16*8+0, 16*8+1, 16*8+2, 16*8+3, 16*8+4, 16*8+5, 16*8+6, 16*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
		32*8
	);
	
	static GfxLayout text_layout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,1),
		2,
		new int[] { 4, 0 },
		new int[] { 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		16*8
	);
	
	static GfxLayout tile_layout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,2),
		4,
		new int[] { 4, 0, RGN_FRAC(1,2)+4, RGN_FRAC(1,2)+0, },
		new int[] { 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3,
				16*16+0, 16*16+1, 16*16+2, 16*16+3, 16*16+8+0, 16*16+8+1, 16*16+8+2, 16*16+8+3 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		32*16
	);
	
	static GfxLayout madgear_tile = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,1),
		4,
		new int[] { 3*4, 2*4, 1*4, 0*4 },
		new int[] { 0, 1, 2, 3, 16+0, 16+1, 16+2, 16+3,
				32*16+0, 32*16+1, 32*16+2, 32*16+3, 32*16+16+0, 32*16+16+1, 32*16+16+2, 32*16+16+3 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32,
				8*32, 9*32, 10*32, 11*32, 12*32, 13*32, 14*32, 15*32 },
		64*16
	);
	
	static GfxLayout madgear_tile2 = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,1),
		4,
		new int[] { 1*4, 3*4, 0*4, 2*4 },
		new int[] { 0, 1, 2, 3, 16+0, 16+1, 16+2, 16+3,
				32*16+0, 32*16+1, 32*16+2, 32*16+3, 32*16+16+0, 32*16+16+1, 32*16+16+2, 32*16+16+3 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32,
				8*32, 9*32, 10*32, 11*32, 12*32, 13*32, 14*32, 15*32 },
		64*16
	);
	
	static GfxDecodeInfo lastduel_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0,sprite_layout, 0x200, 16 ),	/* colors 0x200-0x2ff */
		new GfxDecodeInfo( REGION_GFX2, 0,text_layout,   0x300, 16 ),	/* colors 0x300-0x33f */
		new GfxDecodeInfo( REGION_GFX3, 0,tile_layout,   0x000, 16 ),	/* colors 0x000-0x0ff */
		new GfxDecodeInfo( REGION_GFX4, 0,tile_layout,   0x100, 16 ),	/* colors 0x100-0x1ff */
		new GfxDecodeInfo( -1 )
	};
	
	static GfxDecodeInfo madgear_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0,sprite_layout, 0x200, 16 ),	/* colors 0x200-0x2ff */
		new GfxDecodeInfo( REGION_GFX2, 0,text_layout,   0x300, 16 ),	/* colors 0x300-0x33f */
		new GfxDecodeInfo( REGION_GFX3, 0,madgear_tile,  0x000, 16 ),	/* colors 0x000-0x0ff */
		new GfxDecodeInfo( REGION_GFX4, 0,madgear_tile2, 0x100, 16 ),	/* colors 0x100-0x1ff */
		new GfxDecodeInfo( -1 )
	};
	
	/******************************************************************************/
	
	/* handler called by the 2203 emulator when the internal timers cause an IRQ */
	static void irqhandler(int irq)
	{
		cpu_set_irq_line(1,0,irq ? ASSERT_LINE : CLEAR_LINE);
	}
	
	static struct OKIM6295interface okim6295_interface =
	{
		1,              	/* 1 chip */
		{ 7759 },           /* 7759Hz frequency */
		{ REGION_SOUND1 },	/* memory region 3 */
		{ 98 }
	};
	
	static struct YM2203interface ym2203_interface =
	{
		2,			/* 2 chips */
		3579545, /* Accurate */
		{ YM2203_VOL(40,40), YM2203_VOL(40,40) },
		{ 0 },
		{ 0 },
		{ 0 },
		{ 0 },
		{ irqhandler }
	};
	
	public static InterruptHandlerPtr lastduel_interrupt = new InterruptHandlerPtr() {public void handler(){
		if (cpu_getiloops() == 0) cpu_set_irq_line(0, 2, HOLD_LINE); /* VBL */
		else cpu_set_irq_line(0, 4, HOLD_LINE); /* Controls */
	} };
	
	public static InterruptHandlerPtr madgear_interrupt = new InterruptHandlerPtr() {public void handler(){
		if (cpu_getiloops() == 0) cpu_set_irq_line(0, 5, HOLD_LINE); /* VBL */
		else cpu_set_irq_line(0, 6, HOLD_LINE); /* Controls */
	} };
	
	public static MachineHandlerPtr machine_driver_lastduel = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 10000000) /* Could be 8 MHz */
		MDRV_CPU_MEMORY(lastduel_readmem,lastduel_writemem)
		MDRV_CPU_VBLANK_INT(lastduel_interrupt,3)	/* 1 for vbl, 2 for control reads?? */
	
		MDRV_CPU_ADD(Z80, 3579545)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU) /* Accurate */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_NEEDS_6BITS_PER_GUN | VIDEO_UPDATE_BEFORE_VBLANK | VIDEO_BUFFERS_SPRITERAM)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(8*8, (64-8)*8-1, 1*8, 31*8-1 )
		MDRV_GFXDECODE(lastduel_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(1024)
	
		MDRV_VIDEO_START(lastduel)
		MDRV_VIDEO_EOF(lastduel)
		MDRV_VIDEO_UPDATE(lastduel)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2203, ym2203_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	public static MachineHandlerPtr machine_driver_madgear = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 10000000) /* Accurate */
		MDRV_CPU_MEMORY(madgear_readmem,madgear_writemem)
		MDRV_CPU_VBLANK_INT(madgear_interrupt,3)	/* 1 for vbl, 2 for control reads?? */
	
		MDRV_CPU_ADD(Z80, 3579545)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU) /* Accurate */
		MDRV_CPU_MEMORY(mg_sound_readmem,mg_sound_writemem)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_NEEDS_6BITS_PER_GUN | VIDEO_UPDATE_BEFORE_VBLANK | VIDEO_BUFFERS_SPRITERAM)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(8*8, (64-8)*8-1, 1*8, 31*8-1 )
		MDRV_GFXDECODE(madgear_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(1024)
	
		MDRV_VIDEO_START(madgear)
		MDRV_VIDEO_EOF(lastduel)
		MDRV_VIDEO_UPDATE(lastduel)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2203, ym2203_interface)
		MDRV_SOUND_ADD(OKIM6295, okim6295_interface)
	MACHINE_DRIVER_END();
 }
};
	
	/******************************************************************************/
	
	static InputPortHandlerPtr input_ports_lastduel = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( lastduel )
		PORT_START(); 
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_SERVICE( 0x08, IP_ACTIVE_LOW );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START(); 
		PORT_DIPNAME( 0x0007, 0x0007, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x0001, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x0002, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x0007, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x0006, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0x0005, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(      0x0004, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(      0x0003, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x0038, 0x0038, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x0008, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x0010, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x0038, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x0030, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0x0028, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(      0x0020, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(      0x0018, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x0040, 0x0040, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0040, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080, 0x0080, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0080, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0300, 0x0300, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(      0x0200, "Easy" );
		PORT_DIPSETTING(      0x0300, "Normal" );
		PORT_DIPSETTING(      0x0100, "Difficult" );
		PORT_DIPSETTING(      0x0000, "Very Difficult" );
		PORT_DIPNAME( 0x0400, 0x0400, DEF_STR( "Unknown") );	/* Could be cabinet type? */
		PORT_DIPSETTING(      0x0400, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0800, 0x0800, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0800, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x3000, 0x3000, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(      0x2000, "20000 60000 80000" );
		PORT_DIPSETTING(      0x3000, "30000 80000 80000" );
		PORT_DIPSETTING(      0x1000, "40000 80000 80000" );
		PORT_DIPSETTING(      0x0000, "40000 80000 100000" );
		PORT_DIPNAME( 0x4000, 0x4000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x4000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x8000, 0x8000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x8000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x02, "4" );
		PORT_DIPSETTING(    0x01, "6" );
		PORT_DIPSETTING(    0x00, "8" );
		PORT_DIPNAME( 0x04, 0x04, "Type" );
		PORT_DIPSETTING(    0x04, "Car" );
		PORT_DIPSETTING(    0x00, "Plane" );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_madgear = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( madgear )
		PORT_START(); 
		PORT_DIPNAME( 0x0001, 0x0001, "Allow Continue" );
		PORT_DIPSETTING(      0x0000, DEF_STR( "No") );
		PORT_DIPSETTING(      0x0001, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x0002, 0x0002, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(      0x0002, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x000c, 0x000c, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(      0x0008, "Easy" );
		PORT_DIPSETTING(      0x000c, "Normal" );
		PORT_DIPSETTING(      0x0004, "Difficult" );
		PORT_DIPSETTING(      0x0000, "Very Difficult" );
		PORT_DIPNAME( 0x0030, 0x0030, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(      0x0030, "Upright One Player" );
		PORT_DIPSETTING(      0x0000, "Upright Two Players" );
		PORT_DIPSETTING(      0x0010, DEF_STR( "Cocktail") );
	/* 	PORT_DIPSETTING(      0x0020, "Upright One Player" );*/
		PORT_DIPNAME( 0x0040, 0x0040, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0040, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080, 0x0080, "Background Music" );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0080, DEF_STR( "On") );
		PORT_DIPNAME( 0x0100, 0x0100, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0100, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0200, 0x0200, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0200, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0400, 0x0400, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0400, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0800, 0x0800, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0800, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x1000, 0x1000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x1000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x2000, 0x2000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x2000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x4000, 0x4000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x4000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_SERVICE( 0x8000, IP_ACTIVE_LOW );
	
		PORT_START();  /* Dip switch C, free play is COIN B all off, COIN A all on */
		PORT_DIPNAME( 0x0f00, 0x0f00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(      0x0200, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(      0x0400, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(      0x0500, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x0700, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x0100, DEF_STR( "8C_3C") );
		PORT_DIPSETTING(      0x0900, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x0300, DEF_STR( "5C_3C") );
		PORT_DIPSETTING(      0x0600, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(      0x0f00, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(      0x0000, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x0800, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(      0x0e00, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0x0d00, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(      0x0c00, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(      0x0b00, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(      0x0a00, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0xf000, 0xf000, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(      0x2000, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(      0x4000, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(      0x5000, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x7000, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x1000, DEF_STR( "8C_3C") );
		PORT_DIPSETTING(      0x9000, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x3000, DEF_STR( "5C_3C") );
		PORT_DIPSETTING(      0x6000, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(      0xf000, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x8000, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(      0xe000, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0xd000, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(      0xc000, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(      0xb000, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(      0xa000, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Free_Play") );
	
		PORT_START(); 
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
	
		PORT_START(); 
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_SERVICE1 );
	INPUT_PORTS_END(); }}; 
	
	/******************************************************************************/
	
	static RomLoadHandlerPtr rom_lastduel = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x60000, REGION_CPU1, 0 )	/* 68000 code */
		ROM_LOAD16_BYTE( "ldu-06.rom",   0x00000, 0x20000, CRC(4228a00b) SHA1(8c23f74f682ba2074da9f3306600c881ce41e50f) )
		ROM_LOAD16_BYTE( "ldu-05.rom",   0x00001, 0x20000, CRC(7260434f) SHA1(55eeb12977efb3c6afd86d68612782ba526c9055) )
		ROM_LOAD16_BYTE( "ldu-04.rom",   0x40000, 0x10000, CRC(429fb964) SHA1(78769b05e62c190d846dd08214427d1abbbe2bba) )
		ROM_LOAD16_BYTE( "ldu-03.rom",   0x40001, 0x10000, CRC(5aa4df72) SHA1(9e7315b793f09c8b422bad1ce776588e3a48d80c) )
	
		ROM_REGION( 0x10000 , REGION_CPU2, 0 ) /* audio CPU */
		ROM_LOAD( "ld_02.bin",    0x0000, 0x10000, CRC(91834d0c) SHA1(aaa63b8470fc19b82c25028ab27675a7837ab9a1) )
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "ld_09.bin",    0x000000, 0x10000, CRC(f8fd5243) SHA1(fad80d8959f50a83eb2e47788a8183284d19bea6) ) /* sprites */
		ROM_LOAD( "ld_10.bin",    0x010000, 0x10000, CRC(b49ad746) SHA1(4e609982d60155b0df13a156c37bdf2a25626632) )
		ROM_LOAD( "ld_11.bin",    0x020000, 0x10000, CRC(1a0d180e) SHA1(a68a7f5d00da99a8068876fd2d61c726047aca80) )
		ROM_LOAD( "ld_12.bin",    0x030000, 0x10000, CRC(b2745e26) SHA1(b511631fe4e21f3d2dc7440b3f69cd5edb43d20e) )
		ROM_LOAD( "ld_15.bin",    0x040000, 0x10000, CRC(96b13bbc) SHA1(f2df8d4f11e9192063063ff2e9e4fe76971c5b24) )
		ROM_LOAD( "ld_16.bin",    0x050000, 0x10000, CRC(9d80f7e6) SHA1(ce7c10eba6a9f6a1fad655c7de6b487aef6d7d64) )
		ROM_LOAD( "ld_13.bin",    0x060000, 0x10000, CRC(a1a598ac) SHA1(a0d24d9125cd502b57adf9167cb61e8864d521ce) )
		ROM_LOAD( "ld_14.bin",    0x070000, 0x10000, CRC(edf515cc) SHA1(8dc68d1d4e480afe9614ea85e2eced3fd3917484) )
	
		ROM_REGION( 0x08000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "ld_01.bin",    0x000000, 0x08000, CRC(ad3c6f87) SHA1(1a5ef003c0eb641484921dc0c11450c53ee315f5) ) /* 8x8 text */
	
		ROM_REGION( 0x40000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "ld_17.bin",    0x000000, 0x10000, CRC(7188bfdd) SHA1(26c47af6abb4e6f5e11e2dd6b56113a54c0e6269) ) /* tiles */
		ROM_LOAD( "ld_18.bin",    0x010000, 0x10000, CRC(a62af66a) SHA1(240dafcb03011cf51bfe9d01bec4aceac64d5760) )
		ROM_LOAD( "ld_19.bin",    0x020000, 0x10000, CRC(4b762e50) SHA1(95b3413f67d2e9ebea2a8331945a572a3d824cc1) )
		ROM_LOAD( "ld_20.bin",    0x030000, 0x10000, CRC(b140188e) SHA1(491af082789a11c809c2798da6ae5e52a2b1d986) )
	
		ROM_REGION( 0x80000, REGION_GFX4, ROMREGION_DISPOSE )
		ROM_LOAD( "ld_28.bin",    0x000000, 0x10000, CRC(06778248) SHA1(09663db5f07961a432feb4f82847a2f9741b34ad) ) /* tiles */
		ROM_LOAD( "ld_26.bin",    0x010000, 0x10000, CRC(b0edac81) SHA1(2ba1f864b7f8047b20206063d4e9956ef1d1ad34) )
		ROM_LOAD( "ld_24.bin",    0x020000, 0x10000, CRC(66eac4df) SHA1(b2604f6fd443071deb2729f4381e6fe3a2069a33) )
		ROM_LOAD( "ld_22.bin",    0x030000, 0x10000, CRC(f80f8812) SHA1(2483b272b51ab15c47eb0b48df68b7c3b05d4d35) )
		ROM_LOAD( "ld_27.bin",    0x040000, 0x10000, CRC(48c78675) SHA1(27b03cd1a5335b60953e5dc4888264598e63c147) )
		ROM_LOAD( "ld_25.bin",    0x050000, 0x10000, CRC(c541ae9a) SHA1(b1d6acab76cba77ea6b9fe6fc770b6a6d6960a77) )
		ROM_LOAD( "ld_23.bin",    0x060000, 0x10000, CRC(d817332c) SHA1(c1c3d70a42eb01237bcbe8e274f7022e74c8c715) )
		ROM_LOAD( "ld_21.bin",    0x070000, 0x10000, CRC(b74f0c0e) SHA1(866e3c65fd5dd7099423baefd09eb2b7da7e8392) )
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 )
		ROM_LOAD( "63s141.3d",    0x0000, 0x0100, CRC(729a1ddc) SHA1(eb1d48785a0f187a4cb9c164e6c82481268b3174) )	/* priority (not used) */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_lstduela = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x60000, REGION_CPU1, 0 )	/* 68000 code */
		ROM_LOAD16_BYTE( "06",   0x00000, 0x20000, CRC(0e71acaf) SHA1(e804c77bfd768ae2fc1917bcec1fd0ec7418b780) )
		ROM_LOAD16_BYTE( "05",   0x00001, 0x20000, CRC(47a85bea) SHA1(9d6b2a4e27c84ffce8ed58aa1b314c67c7314932) )
		ROM_LOAD16_BYTE( "04",   0x40000, 0x10000, CRC(aa4bf001) SHA1(3f14b174016c6fa4c82011d3d0f1c957096d6d93) )
		ROM_LOAD16_BYTE( "03",   0x40001, 0x10000, CRC(bbaac8ab) SHA1(3c5773e39e7a96ef62da7b846ce4099222b3e66b) )
	
		ROM_REGION( 0x10000 , REGION_CPU2, 0 ) /* audio CPU */
		ROM_LOAD( "ld_02.bin",    0x0000, 0x10000, CRC(91834d0c) SHA1(aaa63b8470fc19b82c25028ab27675a7837ab9a1) )
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "ld_09.bin",    0x000000, 0x10000, CRC(f8fd5243) SHA1(fad80d8959f50a83eb2e47788a8183284d19bea6) ) /* sprites */
		ROM_LOAD( "ld_10.bin",    0x010000, 0x10000, CRC(b49ad746) SHA1(4e609982d60155b0df13a156c37bdf2a25626632) )
		ROM_LOAD( "ld_11.bin",    0x020000, 0x10000, CRC(1a0d180e) SHA1(a68a7f5d00da99a8068876fd2d61c726047aca80) )
		ROM_LOAD( "ld_12.bin",    0x030000, 0x10000, CRC(b2745e26) SHA1(b511631fe4e21f3d2dc7440b3f69cd5edb43d20e) )
		ROM_LOAD( "ld_15.bin",    0x040000, 0x10000, CRC(96b13bbc) SHA1(f2df8d4f11e9192063063ff2e9e4fe76971c5b24) )
		ROM_LOAD( "ld_16.bin",    0x050000, 0x10000, CRC(9d80f7e6) SHA1(ce7c10eba6a9f6a1fad655c7de6b487aef6d7d64) )
		ROM_LOAD( "ld_13.bin",    0x060000, 0x10000, CRC(a1a598ac) SHA1(a0d24d9125cd502b57adf9167cb61e8864d521ce) )
		ROM_LOAD( "ld_14.bin",    0x070000, 0x10000, CRC(edf515cc) SHA1(8dc68d1d4e480afe9614ea85e2eced3fd3917484) )
	
		ROM_REGION( 0x08000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "ld_01.bin",    0x000000, 0x08000, CRC(ad3c6f87) SHA1(1a5ef003c0eb641484921dc0c11450c53ee315f5) ) /* 8x8 text */
	
		ROM_REGION( 0x40000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "ld_17.bin",    0x000000, 0x10000, CRC(7188bfdd) SHA1(26c47af6abb4e6f5e11e2dd6b56113a54c0e6269) ) /* tiles */
		ROM_LOAD( "ld_18.bin",    0x010000, 0x10000, CRC(a62af66a) SHA1(240dafcb03011cf51bfe9d01bec4aceac64d5760) )
		ROM_LOAD( "ld_19.bin",    0x020000, 0x10000, CRC(4b762e50) SHA1(95b3413f67d2e9ebea2a8331945a572a3d824cc1) )
		ROM_LOAD( "ld_20.bin",    0x030000, 0x10000, CRC(b140188e) SHA1(491af082789a11c809c2798da6ae5e52a2b1d986) )
	
		ROM_REGION( 0x80000, REGION_GFX4, ROMREGION_DISPOSE )
		ROM_LOAD( "ld_28.bin",    0x000000, 0x10000, CRC(06778248) SHA1(09663db5f07961a432feb4f82847a2f9741b34ad) ) /* tiles */
		ROM_LOAD( "ld_26.bin",    0x010000, 0x10000, CRC(b0edac81) SHA1(2ba1f864b7f8047b20206063d4e9956ef1d1ad34) )
		ROM_LOAD( "ld_24.bin",    0x020000, 0x10000, CRC(66eac4df) SHA1(b2604f6fd443071deb2729f4381e6fe3a2069a33) )
		ROM_LOAD( "ld_22.bin",    0x030000, 0x10000, CRC(f80f8812) SHA1(2483b272b51ab15c47eb0b48df68b7c3b05d4d35) )
		ROM_LOAD( "ld_27.bin",    0x040000, 0x10000, CRC(48c78675) SHA1(27b03cd1a5335b60953e5dc4888264598e63c147) )
		ROM_LOAD( "ld_25.bin",    0x050000, 0x10000, CRC(c541ae9a) SHA1(b1d6acab76cba77ea6b9fe6fc770b6a6d6960a77) )
		ROM_LOAD( "ld_23.bin",    0x060000, 0x10000, CRC(d817332c) SHA1(c1c3d70a42eb01237bcbe8e274f7022e74c8c715) )
		ROM_LOAD( "ld_21.bin",    0x070000, 0x10000, CRC(b74f0c0e) SHA1(866e3c65fd5dd7099423baefd09eb2b7da7e8392) )
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 )
		ROM_LOAD( "63s141.3d",    0x0000, 0x0100, CRC(729a1ddc) SHA1(eb1d48785a0f187a4cb9c164e6c82481268b3174) )	/* priority (not used) */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_lstduelb = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x60000, REGION_CPU1, 0 )	/* 68000 code */
		ROM_LOAD16_BYTE( "ld_08.bin",    0x00000, 0x10000, CRC(43811a96) SHA1(79db50c941d8845f1642f2257c610768172923a3) )
		ROM_LOAD16_BYTE( "ld_07.bin",    0x00001, 0x10000, CRC(63c30946) SHA1(cab7374839a68483b3f94821144546cc3eb1528e) )
		ROM_LOAD16_BYTE( "ld_04.bin",    0x20000, 0x10000, CRC(46a4e0f8) SHA1(7d5fac209357090c5faeee3834c19f1d8125aac5) )
		ROM_LOAD16_BYTE( "ld_03.bin",    0x20001, 0x10000, CRC(8d5f204a) SHA1(0415b8a836a62aee1f430bc124996cb8c12ed5cf) )
		ROM_LOAD16_BYTE( "ldu-04.rom",   0x40000, 0x10000, CRC(429fb964) SHA1(78769b05e62c190d846dd08214427d1abbbe2bba) )
		ROM_LOAD16_BYTE( "ldu-03.rom",   0x40001, 0x10000, CRC(5aa4df72) SHA1(9e7315b793f09c8b422bad1ce776588e3a48d80c) )
	
		ROM_REGION( 0x10000 , REGION_CPU2, 0 ) /* audio CPU */
		ROM_LOAD( "ld_02.bin",    0x0000, 0x10000, CRC(91834d0c) SHA1(aaa63b8470fc19b82c25028ab27675a7837ab9a1) )
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "ld_09.bin",    0x000000, 0x10000, CRC(f8fd5243) SHA1(fad80d8959f50a83eb2e47788a8183284d19bea6) ) /* sprites */
		ROM_LOAD( "ld_10.bin",    0x010000, 0x10000, CRC(b49ad746) SHA1(4e609982d60155b0df13a156c37bdf2a25626632) )
		ROM_LOAD( "ld_11.bin",    0x020000, 0x10000, CRC(1a0d180e) SHA1(a68a7f5d00da99a8068876fd2d61c726047aca80) )
		ROM_LOAD( "ld_12.bin",    0x030000, 0x10000, CRC(b2745e26) SHA1(b511631fe4e21f3d2dc7440b3f69cd5edb43d20e) )
		ROM_LOAD( "ld_15.bin",    0x040000, 0x10000, CRC(96b13bbc) SHA1(f2df8d4f11e9192063063ff2e9e4fe76971c5b24) )
		ROM_LOAD( "ld_16.bin",    0x050000, 0x10000, CRC(9d80f7e6) SHA1(ce7c10eba6a9f6a1fad655c7de6b487aef6d7d64) )
		ROM_LOAD( "ld_13.bin",    0x060000, 0x10000, CRC(a1a598ac) SHA1(a0d24d9125cd502b57adf9167cb61e8864d521ce) )
		ROM_LOAD( "ld_14.bin",    0x070000, 0x10000, CRC(edf515cc) SHA1(8dc68d1d4e480afe9614ea85e2eced3fd3917484) )
	
		ROM_REGION( 0x08000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "ld_01.bin",    0x000000, 0x08000, CRC(ad3c6f87) SHA1(1a5ef003c0eb641484921dc0c11450c53ee315f5) ) /* 8x8 text */
	
		ROM_REGION( 0x40000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "ld_17.bin",    0x000000, 0x10000, CRC(7188bfdd) SHA1(26c47af6abb4e6f5e11e2dd6b56113a54c0e6269) ) /* tiles */
		ROM_LOAD( "ld_18.bin",    0x010000, 0x10000, CRC(a62af66a) SHA1(240dafcb03011cf51bfe9d01bec4aceac64d5760) )
		ROM_LOAD( "ld_19.bin",    0x020000, 0x10000, CRC(4b762e50) SHA1(95b3413f67d2e9ebea2a8331945a572a3d824cc1) )
		ROM_LOAD( "ld_20.bin",    0x030000, 0x10000, CRC(b140188e) SHA1(491af082789a11c809c2798da6ae5e52a2b1d986) )
	
		ROM_REGION( 0x80000, REGION_GFX4, ROMREGION_DISPOSE )
		ROM_LOAD( "ld_28.bin",    0x000000, 0x10000, CRC(06778248) SHA1(09663db5f07961a432feb4f82847a2f9741b34ad) ) /* tiles */
		ROM_LOAD( "ld_26.bin",    0x010000, 0x10000, CRC(b0edac81) SHA1(2ba1f864b7f8047b20206063d4e9956ef1d1ad34) )
		ROM_LOAD( "ld_24.bin",    0x020000, 0x10000, CRC(66eac4df) SHA1(b2604f6fd443071deb2729f4381e6fe3a2069a33) )
		ROM_LOAD( "ld_22.bin",    0x030000, 0x10000, CRC(f80f8812) SHA1(2483b272b51ab15c47eb0b48df68b7c3b05d4d35) )
		ROM_LOAD( "ld_27.bin",    0x040000, 0x10000, CRC(48c78675) SHA1(27b03cd1a5335b60953e5dc4888264598e63c147) )
		ROM_LOAD( "ld_25.bin",    0x050000, 0x10000, CRC(c541ae9a) SHA1(b1d6acab76cba77ea6b9fe6fc770b6a6d6960a77) )
		ROM_LOAD( "ld_23.bin",    0x060000, 0x10000, CRC(d817332c) SHA1(c1c3d70a42eb01237bcbe8e274f7022e74c8c715) )
		ROM_LOAD( "ld_21.bin",    0x070000, 0x10000, CRC(b74f0c0e) SHA1(866e3c65fd5dd7099423baefd09eb2b7da7e8392) )
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 )
		ROM_LOAD( "63s141.3d",    0x0000, 0x0100, CRC(729a1ddc) SHA1(eb1d48785a0f187a4cb9c164e6c82481268b3174) )	/* priority (not used) */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_madgear = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 )	/* 256K for 68000 code */
		ROM_LOAD16_BYTE( "mg_04.rom",    0x00000, 0x20000, CRC(b112257d) SHA1(4acfd8ba0fe8d68ca7c9b0fde2b13ce0c9104258) )
		ROM_LOAD16_BYTE( "mg_03.rom",    0x00001, 0x20000, CRC(b2672465) SHA1(96d10046e67181160daebb2b07c867c08f8600dc) )
		ROM_LOAD16_BYTE( "mg_02.rom",    0x40000, 0x20000, CRC(9f5ebe16) SHA1(2183cb807157d48204d8d4d4b7555c9a7772ddfd) )
		ROM_LOAD16_BYTE( "mg_01.rom",    0x40001, 0x20000, CRC(1cea2af0) SHA1(9f4642ed2d21fa525e9fecaac6235a3653df3030) )
	
		ROM_REGION( 0x18000 , REGION_CPU2, 0 ) /* audio CPU */
		ROM_LOAD( "mg_05.rom",    0x00000,  0x08000, CRC(2fbfc945) SHA1(8066516dcf9261abee1edd103bdbe0cc18913ed3) )
		ROM_CONTINUE(             0x10000,  0x08000 )
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "mg_m11.rom",   0x000000, 0x10000, CRC(ee319a64) SHA1(ce8d65fdac3ec1009b22764807c03dd96b340660) )	/* Interleaved sprites */
		ROM_LOAD( "mg_m07.rom",   0x010000, 0x10000, CRC(e5c0b211) SHA1(dc4a92061c686a9d211a7b95aab2e41219508d67) )
		ROM_LOAD( "mg_m12.rom",   0x020000, 0x10000, CRC(887ef120) SHA1(9d57b497334d64df9a4ab7f15824dcc6a333f73d) )
		ROM_LOAD( "mg_m08.rom",   0x030000, 0x10000, CRC(59709aa3) SHA1(384641da58c8b5198ad4fa51cd5fd9a628bcb888) )
		ROM_LOAD( "mg_m13.rom",   0x040000, 0x10000, CRC(eae07db4) SHA1(59c4ff48d906b2bb101fbebe06383940fdff064f) )
		ROM_LOAD( "mg_m09.rom",   0x050000, 0x10000, CRC(40ee83eb) SHA1(35e11fcb3b75ada99df23715ecb955bd40e10da8) )
		ROM_LOAD( "mg_m14.rom",   0x060000, 0x10000, CRC(21e5424c) SHA1(2f7c5d974c847bb14eaf278545bca653919110ba) )
		ROM_LOAD( "mg_m10.rom",   0x070000, 0x10000, CRC(b64afb54) SHA1(5fdd4f67e6b7440448adf395b61c79b79b4f86e7) )
	
		ROM_REGION( 0x08000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "mg_06.rom",    0x000000, 0x08000, CRC(382ee59b) SHA1(a1da439f0585f5cafe2fb7024f1ae0527e34cd92) )	/* 8x8 text */
	
		ROM_REGION( 0x40000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "ls-12",        0x000000, 0x40000, CRC(6c1b2c6c) SHA1(18f22129f13c6bfa7e285f0e09a35644272f6ecb) )
	
		ROM_REGION( 0x80000, REGION_GFX4, ROMREGION_DISPOSE )
		ROM_LOAD( "ls-11",        0x000000, 0x80000, CRC(6bf81c64) SHA1(2289978c6bdb6e4f86e7094e861df147e757e249) )
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 ) /* ADPCM */
		ROM_LOAD( "ls-06",        0x00000, 0x20000, CRC(88d39a5b) SHA1(8fb2d1d26e2ffb93dfc9cf8f23bb81eb64496c2b) )
		ROM_LOAD( "ls-05",        0x20000, 0x20000, CRC(b06e03b5) SHA1(7d17e5cfb57866c60146bea1a4535e961c73327c) )
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 )
		ROM_LOAD( "prom",         0x0000, 0x0100, NO_DUMP )	/* priority (not used) */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_madgearj = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 )	/* 256K for 68000 code */
		ROM_LOAD16_BYTE( "mdj_04.rom",   0x00000, 0x20000, CRC(9ebbebb1) SHA1(84a2b146c10c1635b11c3af0242fd4680994eb5a) )
		ROM_LOAD16_BYTE( "mdj_03.rom",   0x00001, 0x20000, CRC(a5579c2d) SHA1(789dcb1cdf5cae20ab497c75460ad98c33d1a046) )
		ROM_LOAD16_BYTE( "mg_02.rom",    0x40000, 0x20000, CRC(9f5ebe16) SHA1(2183cb807157d48204d8d4d4b7555c9a7772ddfd) )
		ROM_LOAD16_BYTE( "mg_01.rom",    0x40001, 0x20000, CRC(1cea2af0) SHA1(9f4642ed2d21fa525e9fecaac6235a3653df3030) )
	
		ROM_REGION(  0x18000 , REGION_CPU2, 0 ) /* audio CPU */
		ROM_LOAD( "mg_05.rom",    0x00000,  0x08000, CRC(2fbfc945) SHA1(8066516dcf9261abee1edd103bdbe0cc18913ed3) )
		ROM_CONTINUE(             0x10000,  0x08000 )
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "mg_m11.rom",   0x000000, 0x10000, CRC(ee319a64) SHA1(ce8d65fdac3ec1009b22764807c03dd96b340660) )	/* Interleaved sprites */
		ROM_LOAD( "mg_m07.rom",   0x010000, 0x10000, CRC(e5c0b211) SHA1(dc4a92061c686a9d211a7b95aab2e41219508d67) )
		ROM_LOAD( "mg_m12.rom",   0x020000, 0x10000, CRC(887ef120) SHA1(9d57b497334d64df9a4ab7f15824dcc6a333f73d) )
		ROM_LOAD( "mg_m08.rom",   0x030000, 0x10000, CRC(59709aa3) SHA1(384641da58c8b5198ad4fa51cd5fd9a628bcb888) )
		ROM_LOAD( "mg_m13.rom",   0x040000, 0x10000, CRC(eae07db4) SHA1(59c4ff48d906b2bb101fbebe06383940fdff064f) )
		ROM_LOAD( "mg_m09.rom",   0x050000, 0x10000, CRC(40ee83eb) SHA1(35e11fcb3b75ada99df23715ecb955bd40e10da8) )
		ROM_LOAD( "mg_m14.rom",   0x060000, 0x10000, CRC(21e5424c) SHA1(2f7c5d974c847bb14eaf278545bca653919110ba) )
		ROM_LOAD( "mg_m10.rom",   0x070000, 0x10000, CRC(b64afb54) SHA1(5fdd4f67e6b7440448adf395b61c79b79b4f86e7) )
	
		ROM_REGION( 0x08000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "mg_06.rom",    0x000000, 0x08000, CRC(382ee59b) SHA1(a1da439f0585f5cafe2fb7024f1ae0527e34cd92) )	/* 8x8 text */
	
		ROM_REGION( 0x40000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "ls-12",        0x000000, 0x40000, CRC(6c1b2c6c) SHA1(18f22129f13c6bfa7e285f0e09a35644272f6ecb) )
	
		ROM_REGION( 0x80000, REGION_GFX4, ROMREGION_DISPOSE )
		ROM_LOAD( "ls-11",        0x000000, 0x80000, CRC(6bf81c64) SHA1(2289978c6bdb6e4f86e7094e861df147e757e249) )
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 ) /* ADPCM */
		ROM_LOAD( "ls-06",        0x00000, 0x20000, CRC(88d39a5b) SHA1(8fb2d1d26e2ffb93dfc9cf8f23bb81eb64496c2b) )
		ROM_LOAD( "ls-05",        0x20000, 0x20000, CRC(b06e03b5) SHA1(7d17e5cfb57866c60146bea1a4535e961c73327c) )
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 )
		ROM_LOAD( "prom",         0x0000, 0x0100, NO_DUMP )	/* priority (not used) */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_ledstorm = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 )	/* 256K for 68000 code */
		ROM_LOAD16_BYTE( "mdu.04",    0x00000, 0x20000, CRC(7f7f8329) SHA1(9b7ecb7f5cc3f2c80e05da3b9055e2fbd64bf0ce) )
		ROM_LOAD16_BYTE( "mdu.03",    0x00001, 0x20000, CRC(11fa542f) SHA1(1cedfc471058e0d0502a1eeafcab479dca4fea41) )
		ROM_LOAD16_BYTE( "mg_02.rom", 0x40000, 0x20000, CRC(9f5ebe16) SHA1(2183cb807157d48204d8d4d4b7555c9a7772ddfd) )
		ROM_LOAD16_BYTE( "mg_01.rom", 0x40001, 0x20000, CRC(1cea2af0) SHA1(9f4642ed2d21fa525e9fecaac6235a3653df3030) )
	
		ROM_REGION(  0x18000 , REGION_CPU2, 0 ) /* audio CPU */
		ROM_LOAD( "mg_05.rom",    0x00000,  0x08000, CRC(2fbfc945) SHA1(8066516dcf9261abee1edd103bdbe0cc18913ed3) )
		ROM_CONTINUE(             0x10000,  0x08000 )
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "mg_m11.rom",   0x000000, 0x10000, CRC(ee319a64) SHA1(ce8d65fdac3ec1009b22764807c03dd96b340660) )	/* Interleaved sprites */
		ROM_LOAD( "07",           0x010000, 0x10000, CRC(7152b212) SHA1(b021496e8b3c22c018907e6e374a7401d3843570) )
		ROM_LOAD( "mg_m12.rom",   0x020000, 0x10000, CRC(887ef120) SHA1(9d57b497334d64df9a4ab7f15824dcc6a333f73d) )
		ROM_LOAD( "08",           0x030000, 0x10000, CRC(72e5d525) SHA1(209def4206e9b66be9879f0105d3f04980f156da) )
		ROM_LOAD( "mg_m13.rom",   0x040000, 0x10000, CRC(eae07db4) SHA1(59c4ff48d906b2bb101fbebe06383940fdff064f) )
		ROM_LOAD( "09",           0x050000, 0x10000, CRC(7b5175cb) SHA1(8d8d4953dd787308bed75345af6789899d2afded) )
		ROM_LOAD( "mg_m14.rom",   0x060000, 0x10000, CRC(21e5424c) SHA1(2f7c5d974c847bb14eaf278545bca653919110ba) )
		ROM_LOAD( "10",           0x070000, 0x10000, CRC(6db7ca64) SHA1(389cc93b9bfe2824a0de9796e79c6d452d09567e) )
	
		ROM_REGION( 0x08000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "06",           0x000000, 0x08000, CRC(54bfdc02) SHA1(480ef755425aed9e0149bdb90bf30ddaef2be192) )	/* 8x8 text */
	
		ROM_REGION( 0x40000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "ls-12",        0x000000, 0x40000, CRC(6c1b2c6c) SHA1(18f22129f13c6bfa7e285f0e09a35644272f6ecb) )
	
		ROM_REGION( 0x80000, REGION_GFX4, ROMREGION_DISPOSE )
		ROM_LOAD( "ls-11",        0x000000, 0x80000, CRC(6bf81c64) SHA1(2289978c6bdb6e4f86e7094e861df147e757e249) )
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 ) /* ADPCM */
		ROM_LOAD( "ls-06",        0x00000, 0x20000, CRC(88d39a5b) SHA1(8fb2d1d26e2ffb93dfc9cf8f23bb81eb64496c2b) )
		ROM_LOAD( "ls-05",        0x20000, 0x20000, CRC(b06e03b5) SHA1(7d17e5cfb57866c60146bea1a4535e961c73327c) )
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 )
		ROM_LOAD( "prom",         0x0000, 0x0100, NO_DUMP )	/* priority (not used) */
	ROM_END(); }}; 
	
	/******************************************************************************/
	
	public static GameDriver driver_lastduel	   = new GameDriver("1988"	,"lastduel"	,"lastduel.java"	,rom_lastduel,null	,machine_driver_lastduel	,input_ports_lastduel	,null	,ROT270, "Capcom", "Last Duel (US set 1)" )
	public static GameDriver driver_lstduela	   = new GameDriver("1988"	,"lstduela"	,"lastduel.java"	,rom_lstduela,driver_lastduel	,machine_driver_lastduel	,input_ports_lastduel	,null	,ROT270, "Capcom", "Last Duel (US set 2)" )
	public static GameDriver driver_lstduelb	   = new GameDriver("1988"	,"lstduelb"	,"lastduel.java"	,rom_lstduelb,driver_lastduel	,machine_driver_lastduel	,input_ports_lastduel	,null	,ROT270, "bootleg", "Last Duel (bootleg)" )
	public static GameDriver driver_madgear	   = new GameDriver("1989"	,"madgear"	,"lastduel.java"	,rom_madgear,null	,machine_driver_madgear	,input_ports_madgear	,null	,ROT270, "Capcom", "Mad Gear (US)" )
	public static GameDriver driver_madgearj	   = new GameDriver("1989"	,"madgearj"	,"lastduel.java"	,rom_madgearj,driver_madgear	,machine_driver_madgear	,input_ports_madgear	,null	,ROT270, "Capcom", "Mad Gear (Japan)" )
	public static GameDriver driver_ledstorm	   = new GameDriver("1988"	,"ledstorm"	,"lastduel.java"	,rom_ledstorm,driver_madgear	,machine_driver_madgear	,input_ports_madgear	,null	,ROT270, "Capcom", "Led Storm (US)" )
}
