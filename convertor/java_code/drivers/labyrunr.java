/***************************************************************************

Labyrinth Runner (GX771) (c) 1987 Konami

similar to Fast Lane

Driver by Nicola Salmoria

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class labyrunr
{
	
	
	
	/* from vidhrdw/labyrunr.c */
	
	public static InterruptHandlerPtr labyrunr_interrupt = new InterruptHandlerPtr() {public void handler(){
		if (cpu_getiloops() == 0)
		{
			if (K007121_ctrlram[0][0x07] & 0x02)
				cpu_set_irq_line(0, HD6309_IRQ_LINE, HOLD_LINE);
		}
		else if (cpu_getiloops() % 2)
		{
			if (K007121_ctrlram[0][0x07] & 0x01)
				cpu_set_irq_line(0, IRQ_LINE_NMI, PULSE_LINE);
		}
	} };
	
	public static WriteHandlerPtr labyrunr_bankswitch_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int bankaddress;
		unsigned char *RAM = memory_region(REGION_CPU1);
	
	if (data & 0xe0) usrintf_showmessage("bankswitch %02x",data);
	
		/* bits 0-2 = bank number */
		bankaddress = 0x10000 + (data & 0x07) * 0x4000;
		cpu_setbank(1,&RAM[bankaddress]);
	
		/* bits 3 and 4 are coin counters */
		coin_counter_w(0,data & 0x08);
		coin_counter_w(1,data & 0x10);
	} };
	
	public static Memory_ReadAddress labyrunr_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0020, 0x005f, MRA_RAM ),	/* scroll registers */
		new Memory_ReadAddress( 0x0801, 0x0801, YM2203_status_port_0_r ),
		new Memory_ReadAddress( 0x0800, 0x0800, YM2203_read_port_0_r ),
		new Memory_ReadAddress( 0x0901, 0x0901, YM2203_status_port_1_r ),
		new Memory_ReadAddress( 0x0900, 0x0900, YM2203_read_port_1_r ),
		new Memory_ReadAddress( 0x0a00, 0x0a00, input_port_5_r ),
		new Memory_ReadAddress( 0x0a01, 0x0a01, input_port_4_r ),
		new Memory_ReadAddress( 0x0b00, 0x0b00, input_port_3_r ),
		new Memory_ReadAddress( 0x0d00, 0x0d1f, K051733_r ),			/* 051733 (protection) */
		new Memory_ReadAddress( 0x1000, 0x10ff, paletteram_r ),
		new Memory_ReadAddress( 0x1800, 0x1fff, MRA_RAM ),
		new Memory_ReadAddress( 0x2000, 0x3fff, MRA_RAM ),
		new Memory_ReadAddress( 0x4000, 0x7fff, MRA_BANK1 ),
		new Memory_ReadAddress( 0x8000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress labyrunr_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x0007, K007121_ctrl_0_w ),
		new Memory_WriteAddress( 0x0020, 0x005f, MWA_RAM ),	/* scroll registers */
		new Memory_WriteAddress( 0x0801, 0x0801, YM2203_control_port_0_w ),
		new Memory_WriteAddress( 0x0800, 0x0800, YM2203_write_port_0_w ),
		new Memory_WriteAddress( 0x0901, 0x0901, YM2203_control_port_1_w ),
		new Memory_WriteAddress( 0x0900, 0x0900, YM2203_write_port_1_w ),
		new Memory_WriteAddress( 0x0c00, 0x0c00, labyrunr_bankswitch_w ),
		new Memory_WriteAddress( 0x0d00, 0x0d1f, K051733_w ),				/* 051733 (protection) */
		new Memory_WriteAddress( 0x0e00, 0x0e00, watchdog_reset_w ),
		new Memory_WriteAddress( 0x1000, 0x10ff, paletteram_xBBBBBGGGGGRRRRR_swap_w, paletteram ),
		new Memory_WriteAddress( 0x1800, 0x1fff, MWA_RAM ),
		new Memory_WriteAddress( 0x2000, 0x2fff, MWA_RAM, spriteram ),	/* Sprite RAM */
		new Memory_WriteAddress( 0x3000, 0x37ff, labyrunr_vram1_w, labyrunr_videoram1 ),
		new Memory_WriteAddress( 0x3800, 0x3fff, labyrunr_vram2_w, labyrunr_videoram2 ),
		new Memory_WriteAddress( 0x4000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	/***************************************************************************
	
		Input Ports
	
	***************************************************************************/
	
	static InputPortHandlerPtr input_ports_labyrunr = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( labyrunr )
		PORT_START(); 	/* DSW #1 */
		PORT_DIPNAME( 0x0f, 0x0f, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x02, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0x0f, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x0e, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(    0x0d, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x0b, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x0a, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x09, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0xf0, 0xf0, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x20, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x50, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0xf0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(    0x70, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0xe0, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(    0xd0, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0xb0, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0xa0, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x90, DEF_STR( "1C_7C") );
	//	PORT_DIPSETTING(    0x00, "Invalid" );
	
		PORT_START(); 	/* DSW #2 */
		PORT_DIPNAME( 0x03, 0x02, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x03, "2" );
		PORT_DIPSETTING(    0x02, "3" );
		PORT_DIPSETTING(    0x01, "5" );
		PORT_DIPSETTING(    0x00, "7" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x18, 0x18, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x18, "30000 70000" );
		PORT_DIPSETTING(    0x10, "40000 80000" );
		PORT_DIPSETTING(    0x08, "40000" );
		PORT_DIPSETTING(    0x00, "50000" );
		PORT_DIPNAME( 0x60, 0x60, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x60, "Easy" );
		PORT_DIPSETTING(    0x40, "Normal" );
		PORT_DIPSETTING(    0x20, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* DSW #3 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "Upright Controls" );
		PORT_DIPSETTING(    0x02, "Single" );
		PORT_DIPSETTING(    0x00, "Dual" );
		PORT_SERVICE( 0x04, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x08, 0x08, "Continues" );
		PORT_DIPSETTING(    0x08, "3" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_BIT( 0xf0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* COINSW */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* PLAYER 1 INPUTS */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* PLAYER 2 INPUTS */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START2 );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout gfxlayout = new GfxLayout
	(
		8,8,
		0x40000/32,
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 2*4, 3*4, 0*4, 1*4, 6*4, 7*4, 4*4, 5*4 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
		32*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, gfxlayout, 0, 8*16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	/***************************************************************************
	
		Machine Driver
	
	***************************************************************************/
	
	static struct YM2203interface ym2203_interface =
	{
		2,			/* 2 chips */
		3000000,	/* 24MHz/8? */
		{ YM2203_VOL(80,40), YM2203_VOL(80,40) },
		{ input_port_0_r },
		{ input_port_1_r, input_port_2_r },
		{ 0 },
		{ 0 }
	};
	
	
	
	public static MachineHandlerPtr machine_driver_labyrunr = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(HD6309, 3000000)		/* 24MHz/8? */
		MDRV_CPU_MEMORY(labyrunr_readmem,labyrunr_writemem)
		MDRV_CPU_VBLANK_INT(labyrunr_interrupt,8)	/* 1 IRQ + 4 NMI (generated by 007121) */
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(37*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 35*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(128)
		MDRV_COLORTABLE_LENGTH(2*8*16*16)
	
		MDRV_PALETTE_INIT(labyrunr)
		MDRV_VIDEO_START(labyrunr)
		MDRV_VIDEO_UPDATE(labyrunr)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2203, ym2203_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	/***************************************************************************
	
	  Game ROMs
	
	***************************************************************************/
	
	static RomLoadHandlerPtr rom_tricktrp = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x28000, REGION_CPU1, 0 ) /* code + banked roms */
		ROM_LOAD( "771e04",     0x10000, 0x08000, CRC(ba2c7e20) SHA1(713dcc0e65bf9431f2c0df9db1210346a9476a52) )
		ROM_CONTINUE(           0x08000, 0x08000 )
		ROM_LOAD( "771e03",     0x18000, 0x10000, CRC(d0d68036) SHA1(8589ee07e229259341a4cc22bc64de8f06536472) )
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD16_BYTE( "771e01a",	0x00000, 0x10000, CRC(103ffa0d) SHA1(1949c49ca3b243e4cfb5fb19ecd3a1e1492cfddd) )	/* tiles + sprites */
		ROM_LOAD16_BYTE( "771e01c",	0x00001, 0x10000, CRC(cfec5be9) SHA1(2b6a32e2608a70c47d1ec9b4de38b5c3a0898cde) )
		ROM_LOAD16_BYTE( "771d01b",	0x20000, 0x10000, CRC(07f2a71c) SHA1(63c79e75e71539e69d4d9d35e629a6021124f6d0) )
		ROM_LOAD16_BYTE( "771d01d",	0x20001, 0x10000, CRC(f6810a49) SHA1(b40e9f0d0919188a05c1990347da8dc8ff12d65a) )
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 )
		ROM_LOAD( "771d02.08d", 0x0000, 0x0100, CRC(3d34bb5a) SHA1(3f3c845f1197457244e7c7e4f9b2a03c278613e4) )	/* sprite lookup table */
																/* there is no char lookup table */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_labyrunr = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x28000, REGION_CPU1, 0 ) /* code + banked roms */
		ROM_LOAD( "771j04.10f", 0x10000, 0x08000, CRC(354a41d0) SHA1(302e8f5c469ad3f615aeca8005ebde6b6051aaae) )
		ROM_CONTINUE(           0x08000, 0x08000 )
		ROM_LOAD( "771j03.08f", 0x18000, 0x10000, CRC(12b49044) SHA1(e9b22fb093cfb746a9767e94ef5deef98bed5b7a) )
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "771d01.14a",	0x00000, 0x40000, CRC(15c8f5f9) SHA1(e4235e1315d0331f3ce5047834a68764ed43aa4b) )	/* tiles + sprites */
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 )
		ROM_LOAD( "771d02.08d", 0x0000, 0x0100, CRC(3d34bb5a) SHA1(3f3c845f1197457244e7c7e4f9b2a03c278613e4) )	/* sprite lookup table */
																/* there is no char lookup table */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_labyrunk = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x28000, REGION_CPU1, 0 ) /* code + banked roms */
		ROM_LOAD( "771k04.10f", 0x10000, 0x08000, CRC(9816ab35) SHA1(6efb0332f4a62f20889f212682ee7225e4a182a9) )
		ROM_CONTINUE(           0x08000, 0x08000 )
		ROM_LOAD( "771k03.8f",  0x18000, 0x10000, CRC(48d732ae) SHA1(8bc7917397f32cf5f995b3763ae921725e27de05) )
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD16_BYTE( "771d01a.13a",	0x00000, 0x10000, CRC(0cd1ed1a) SHA1(eac6c106de28acc54535ae1fb99f778c1ed4013e) )	/* tiles + sprites */
		ROM_LOAD16_BYTE( "771d01c.13a",	0x00001, 0x10000, CRC(d75521fe) SHA1(72f0c4d9511bc70d77415f50be93293026305bd5) )
		ROM_LOAD16_BYTE( "771d01b",	    0x20000, 0x10000, CRC(07f2a71c) SHA1(63c79e75e71539e69d4d9d35e629a6021124f6d0) )
		ROM_LOAD16_BYTE( "771d01d",	    0x20001, 0x10000, CRC(f6810a49) SHA1(b40e9f0d0919188a05c1990347da8dc8ff12d65a) )
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 )
		ROM_LOAD( "771d02.08d", 0x0000, 0x0100, CRC(3d34bb5a) SHA1(3f3c845f1197457244e7c7e4f9b2a03c278613e4) )	/* sprite lookup table */
																/* there is no char lookup table */
	ROM_END(); }}; 
	
	
	public static GameDriver driver_tricktrp	   = new GameDriver("1987"	,"tricktrp"	,"labyrunr.java"	,rom_tricktrp,null	,machine_driver_labyrunr	,input_ports_labyrunr	,null	,ROT90, "Konami", "Trick Trap (World?)" )
	public static GameDriver driver_labyrunr	   = new GameDriver("1987"	,"labyrunr"	,"labyrunr.java"	,rom_labyrunr,driver_tricktrp	,machine_driver_labyrunr	,input_ports_labyrunr	,null	,ROT90, "Konami", "Labyrinth Runner (Japan)" )
	public static GameDriver driver_labyrunk	   = new GameDriver("1987"	,"labyrunk"	,"labyrunr.java"	,rom_labyrunk,driver_tricktrp	,machine_driver_labyrunr	,input_ports_labyrunr	,null	,ROT90, "Konami", "Labyrinth Runner (World Ver. K)" )
}
