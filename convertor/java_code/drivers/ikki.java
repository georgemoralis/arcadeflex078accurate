/*****************************************************************************

Ikki (c) 1985 Sun Electronics

	Driver by Uki

	20/Jun/2001 -

*****************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class ikki
{
	
	
	static UINT8 *ikki_sharedram;
	
	/****************************************************************************/
	
	
	public static WriteHandlerPtr ikki_sharedram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		ikki_sharedram[offset] = data;
	} };
	
	public static ReadHandlerPtr ikki_sharedram_r  = new ReadHandlerPtr() { public int handler(int offset){
		return ikki_sharedram[offset];
	} };
	
	public static ReadHandlerPtr ikki_e000_r  = new ReadHandlerPtr() { public int handler(int offset){
	/* bit1: interrupt type?, bit0: CPU2 busack? */
	
		if (cpu_getiloops() == 0)
			return 0;
		return 2;
	} };
	
	/****************************************************************************/
	
	public static Memory_ReadAddress ikki_readmem1[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x9fff, MRA_ROM ),
	
		new Memory_ReadAddress( 0xc000, 0xc7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xc800, 0xcfff, ikki_sharedram_r ),
		new Memory_ReadAddress( 0xd000, 0xd7ff, MRA_RAM ), /* videoram */
	
		new Memory_ReadAddress( 0xe000, 0xe000, ikki_e000_r ),
		new Memory_ReadAddress( 0xe001, 0xe001, input_port_0_r ), /* dsw 1 */
		new Memory_ReadAddress( 0xe002, 0xe002, input_port_1_r ), /* dsw 2 */
		new Memory_ReadAddress( 0xe003, 0xe003, input_port_4_r ), /* other inputs */
		new Memory_ReadAddress( 0xe004, 0xe004, input_port_2_r ), /* player1 */
		new Memory_ReadAddress( 0xe005, 0xe005, input_port_3_r ), /* player2 */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress ikki_writemem1[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x9fff, MWA_ROM ),
	
		new Memory_WriteAddress( 0xc000, 0xc7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xc800, 0xcfff, ikki_sharedram_w ),
		new Memory_WriteAddress( 0xd000, 0xd7ff, videoram_w, &videoram, &videoram_size ),
	
		new Memory_WriteAddress( 0xe008, 0xe008, ikki_scrn_ctrl_w ),
		new Memory_WriteAddress( 0xe009, 0xe009, MWA_NOP ), /* coin counter? */
		new Memory_WriteAddress( 0xe00a, 0xe00b, ikki_scroll_w ),
	
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress ikki_readmem2[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x5fff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xc7ff, spriteram_r ),
		new Memory_ReadAddress( 0xc800, 0xcfff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress ikki_writemem2[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x5fff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xc7ff, MWA_RAM, &spriteram, &spriteram_size ),
		new Memory_WriteAddress( 0xc800, 0xcfff, MWA_RAM, &ikki_sharedram ),
	
		new Memory_WriteAddress( 0xd801, 0xd801, SN76496_0_w ),
		new Memory_WriteAddress( 0xd802, 0xd802, SN76496_1_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	/****************************************************************************/
	
	static InputPortHandlerPtr input_ports_ikki = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( ikki )
		PORT_START();   /* dsw1 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "5" );
		PORT_DIPNAME( 0x02, 0x00, "2 Players Game" );
		PORT_DIPSETTING(    0x00, "2 Credits" );
		PORT_DIPSETTING(    0x02, "1 Credit" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, "Unknown 1-4" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0xf0, 0x00, "Coin1 / Coin2" );
		PORT_DIPSETTING(    0x00, "1C 1C / 1C 1C" );
		PORT_DIPSETTING(    0x10, "2C 1C / 2C 1C" );
		PORT_DIPSETTING(    0x20, "2C 1C / 1C 3C" );
		PORT_DIPSETTING(    0x30, "1C 1C / 1C 2C" );
		PORT_DIPSETTING(    0x40, "1C 1C / 1C 3C" );
		PORT_DIPSETTING(    0x50, "1C 1C / 1C 4C" );
		PORT_DIPSETTING(    0x60, "1C 1C / 1C 5C" );
		PORT_DIPSETTING(    0x70, "1C 1C / 1C 6C" );
		PORT_DIPSETTING(    0x80, "1C 2C / 1C 2C" );
		PORT_DIPSETTING(    0x90, "1C 2C / 1C 4C" );
		PORT_DIPSETTING(    0xa0, "1C 2C / 1C 5C" );
		PORT_DIPSETTING(    0xb0, "1C 2C / 1C 10C" );
		PORT_DIPSETTING(    0xc0, "1C 2C / 1C 11C" );
		PORT_DIPSETTING(    0xd0, "1C 2C / 1C 12C" );
		PORT_DIPSETTING(    0xe0, "1C 2C / 1C 6C" );
		PORT_DIPSETTING(    0xf0, DEF_STR( "Free_Play") );
	
		PORT_START();   /* dsw2 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x06, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "1 (Normal"));
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x04, "3" );
		PORT_DIPSETTING(    0x06, "4 (Difficult"));
		PORT_DIPNAME( 0x08, 0x00, "Unknown 2-4" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, "Unknown 2-5" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, "Unknown 2-6" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, "Unknown 2-7" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, "Freeze" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START();  /* e004 */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
	
		PORT_START();  /* e005 */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
	
		PORT_START();  /* e003 */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_SERVICE1 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
	INPUT_PORTS_END(); }}; 
	
	/****************************************************************************/
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,    /* 8*8 characters */
		2048,   /* 2048 characters */
		3,      /* 3 bits per pixel */
		new int[] {0,16384*8,16384*8*2},
		new int[] {7,6,5,4,3,2,1,0},
		new int[] {8*0, 8*1, 8*2, 8*3, 8*4, 8*5, 8*6, 8*7},
		8*8
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,32,  /* 16*32 characters */
		256,    /* 256 characters */
		3,      /* 3 bits per pixel */
		new int[] {16384*8*2,16384*8,0},
		new int[] {7,6,5,4,3,2,1,0,
			8*16+7,8*16+6,8*16+5,8*16+4,8*16+3,8*16+2,8*16+1,8*16+0},
		new int[] {8*0, 8*1, 8*2, 8*3, 8*4, 8*5, 8*6, 8*7,
		8*8,8*9,8*10,8*11,8*12,8*13,8*14,8*15,
		8*32,8*33,8*34,8*35,8*36,8*37,8*38,8*39,
		8*40,8*41,8*42,8*43,8*44,8*45,8*46,8*47},
		8*8*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX2, 0x0000, &charlayout,   512, 64 ),
		new GfxDecodeInfo( REGION_GFX1, 0x0000, &spritelayout, 0,   64 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	static struct SN76496interface sn76496_interface =
	{
		2,	/* 2 chips */
		{ 8000000/4, 8000000/2 },
		{ 75, 75 }
	};
	
	
	static MACHINE_DRIVER_START( ikki )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80,8000000/2) /* 4.000MHz */
		MDRV_CPU_MEMORY(ikki_readmem1,ikki_writemem1)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,2)
	
		MDRV_CPU_ADD(Z80,8000000/2) /* 4.000MHz */
		MDRV_CPU_MEMORY(ikki_readmem2,ikki_writemem2)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,2)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(1*8, 31*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256+1)
		MDRV_COLORTABLE_LENGTH(1024)
		
		MDRV_PALETTE_INIT(ikki)
		MDRV_VIDEO_START(generic)
		MDRV_VIDEO_UPDATE(ikki)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SN76496, sn76496_interface)
	MACHINE_DRIVER_END
	
	/****************************************************************************/
	
	static RomLoadHandlerPtr rom_ikki = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 ) /* main CPU */
		ROM_LOAD( "tvg17_1",  0x0000,  0x2000, CRC(cb28167c) SHA1(6843553faee0d3bbe432689fdf5f5454470e2b09) )
		ROM_CONTINUE(         0x8000,  0x2000 )
		ROM_LOAD( "tvg17_2",  0x2000,  0x2000, CRC(756c7450) SHA1(043e4f3085d1800b569ee397a968229d547ffbe1) )
		ROM_LOAD( "tvg17_3",  0x4000,  0x2000, CRC(91f0a8b6) SHA1(529fee561c26aa9da75ee58488070329c459540c) )
		ROM_LOAD( "tvg17_4",  0x6000,  0x2000, CRC(696fcf7d) SHA1(6affec60490012fdc762e7a104c0031d44f95bbd) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 ) /* sub CPU */
		ROM_LOAD( "tvg17_5",  0x0000,  0x2000, CRC(22bdb40e) SHA1(265801ad660a5a3fc5bb187fa92dbe6098b390f5) )
	
		ROM_REGION( 0xc000, REGION_GFX1, ROMREGION_DISPOSE ) /* sprite */
		ROM_LOAD( "tvg17_6",  0x0000,  0x4000, CRC(dc8aa269) SHA1(fd8b5c2bead52e1e136d4df4c26f136d8992d9be) )
		ROM_LOAD( "tvg17_7",  0x4000,  0x4000, CRC(0e9efeba) SHA1(d922c4276a988b78b9a2a3ca632136e64a80d995) )
		ROM_LOAD( "tvg17_8",  0x8000,  0x4000, CRC(45c9087a) SHA1(9db82fc194096588fde5048e922a654e2ad12c23) )
	
		ROM_REGION( 0xc000, REGION_GFX2, ROMREGION_DISPOSE ) /* bg */
		ROM_LOAD( "tvg17_9",  0x8000,  0x4000, CRC(c594f3c5) SHA1(6fe19d9ccbe6934a210eb2cab441cd0ba83cbcf4) )
		ROM_LOAD( "tvg17_10", 0x4000,  0x4000, CRC(2e510b4e) SHA1(c0ff4515e66ab4959b597a4d930cbbcc31c53cda) )
		ROM_LOAD( "tvg17_11", 0x0000,  0x4000, CRC(35012775) SHA1(c90386660755c85fb9f020f8161805dd02a16271) )
	
		ROM_REGION( 0x0700, REGION_PROMS, 0 ) /* color PROMs */
		ROM_LOAD( "prom17_3", 0x0000,  0x0100, CRC(dbcd3bec) SHA1(1baeec277b16c82b67e10da9d4c84cf383ef4a82) ) /* R */
		ROM_LOAD( "prom17_4", 0x0100,  0x0100, CRC(9eb7b6cf) SHA1(86451e8a510f8cfbc0be7d4e7bb1ee7dfd67f1f4) ) /* G */
		ROM_LOAD( "prom17_5", 0x0200,  0x0100, CRC(9b30a7f3) SHA1(a0aefc2c8325b95ea227e404583d14622b04a3b9) ) /* B */
		ROM_LOAD( "prom17_6", 0x0300,  0x0200, CRC(962e619d) SHA1(d2cbcd7b2f1438d1d3759cc1ef6b76b42d9952fe) ) /* sprite */
		ROM_LOAD( "prom17_7", 0x0500,  0x0200, CRC(b1f5148c) SHA1(251ddaabf65a87306970b79918849da95beb8ee7) ) /* bg */
	
		ROM_REGION( 0x0200, REGION_USER1, 0 )
		ROM_LOAD( "prom17_1", 0x0000,  0x0100, CRC(ca0af30c) SHA1(6d7cfeb16daf61c6e7f93172809b0983bf13cd6c) ) /* video attribute */
		ROM_LOAD( "prom17_2", 0x0100,  0x0100, CRC(f3c55174) SHA1(936c5432c4fccfcb2601c1e08b98d5509202fe5b) ) /* unknown */
	ROM_END(); }}; 
	
	public static GameDriver driver_ikki	   = new GameDriver("1985"	,"ikki"	,"ikki.java"	,rom_ikki,null	,machine_driver_ikki	,input_ports_ikki	,null	,ROT0, "Sun Electronics", "Ikki (Japan)" )
}
