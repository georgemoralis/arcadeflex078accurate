/***************************************************************************

Mosaic (c) 1990 Space

Notes:
- the ROM OK / RAM OK message in service mode is fake: ROM and RAM are not tested.

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class mosaic
{
	
	
	
	
	
	static int prot_val;
	
	public static WriteHandlerPtr protection_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if ((data & 0x80) == 0)
		{
			/* simply increment given value */
			prot_val = (data + 1) << 8;
		}
		else
		{
			static int jumptable[] =
			{
				0x02be, 0x0314, 0x0475, 0x0662, 0x0694, 0x08f3, 0x0959, 0x096f,
				0x0992, 0x09a4, 0x0a50, 0x0d69, 0x0eee, 0x0f98, 0x1040, 0x1075,
				0x10d8, 0x18b4, 0x1a27, 0x1a4a, 0x1ac6, 0x1ad1, 0x1ae2, 0x1b68,
				0x1c95, 0x1fd5, 0x20fc, 0x212d, 0x213a, 0x21b6, 0x2268, 0x22f3,
				0x231a, 0x24bb, 0x286b, 0x295f, 0x2a7f, 0x2fc6, 0x3064, 0x309f,
				0x3118, 0x31e1, 0x32d0, 0x35f7, 0x3687, 0x38ea, 0x3b86, 0x3c9a,
				0x411f, 0x473f
			};
	
			prot_val = jumptable[data & 0x7f];
		}
	} };
	
	public static ReadHandlerPtr protection_r  = new ReadHandlerPtr() { public int handler(int offset){
		int res = (prot_val >> 8) & 0xff;
	
		logerror("%06x: protection_r %02x\n",activecpu_get_pc(),res);
	
		prot_val <<= 8;
	
		return res;
	} };
	
	public static WriteHandlerPtr gfire2_protection_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		logerror("%06x: protection_w %02x\n",activecpu_get_pc(),data);
	
		switch(data)
		{
			case 0x01:
				/* written repeatedly; no effect?? */
				break;
			case 0x02:
				prot_val = 0x0a10;
				break;
			case 0x04:
				prot_val = 0x0a15;
				break;
			case 0x06:
				prot_val = 0x80e3;
				break;
			case 0x08:
				prot_val = 0x0965;
				break;
			case 0x0a:
				prot_val = 0x04b4;
				break;
		}
	} };
	
	public static ReadHandlerPtr gfire2_protection_r  = new ReadHandlerPtr() { public int handler(int offset){
		int res = prot_val & 0xff;
	
		prot_val >>= 8;
	
		return res;
	} };
	
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x00000, 0x0ffff, MRA_ROM ),
		new Memory_ReadAddress( 0x20000, 0x21fff, MRA_RAM ),
		new Memory_ReadAddress( 0x22000, 0x23fff, MRA_RAM ),
		new Memory_ReadAddress( 0x24000, 0x241ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x00000, 0x0ffff, MWA_ROM ),
		new Memory_WriteAddress( 0x20000, 0x21fff, MWA_RAM ),
		new Memory_WriteAddress( 0x22000, 0x22fff, mosaic_bgvideoram_w, mosaic_bgvideoram ),
		new Memory_WriteAddress( 0x23000, 0x23fff, mosaic_fgvideoram_w, mosaic_fgvideoram ),
		new Memory_WriteAddress( 0x24000, 0x241ff, paletteram_xRRRRRGGGGGBBBBB_w, paletteram ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress gfire2_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x00000, 0x0ffff, MRA_ROM ),
		new Memory_ReadAddress( 0x10000, 0x17fff, MRA_RAM ),
		new Memory_ReadAddress( 0x22000, 0x23fff, MRA_RAM ),
		new Memory_ReadAddress( 0x24000, 0x241ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress gfire2_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x00000, 0x0ffff, MWA_ROM ),
		new Memory_WriteAddress( 0x10000, 0x17fff, MWA_RAM ),
		new Memory_WriteAddress( 0x22000, 0x22fff, mosaic_bgvideoram_w, mosaic_bgvideoram ),
		new Memory_WriteAddress( 0x23000, 0x23fff, mosaic_fgvideoram_w, mosaic_fgvideoram ),
		new Memory_WriteAddress( 0x24000, 0x241ff, paletteram_xRRRRRGGGGGBBBBB_w, paletteram ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x30, 0x30, IORP_NOP ),	/* Z180 internal registers */
		new IO_ReadPort( 0x70, 0x70, YM2203_status_port_0_r ),
		new IO_ReadPort( 0x71, 0x71, YM2203_read_port_0_r ),
		new IO_ReadPort( 0x72, 0x72, protection_r ),
		new IO_ReadPort( 0x74, 0x74, input_port_0_r ),
		new IO_ReadPort( 0x76, 0x76, input_port_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x3f, IOWP_NOP ),	/* Z180 internal registers */
		new IO_WritePort( 0x70, 0x70, YM2203_control_port_0_w ),
		new IO_WritePort( 0x71, 0x71, YM2203_write_port_0_w ),
		new IO_WritePort( 0x72, 0x72, protection_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort gfire2_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x30, 0x30, IORP_NOP ),	/* Z180 internal registers */
		new IO_ReadPort( 0x70, 0x70, YM2203_status_port_0_r ),
		new IO_ReadPort( 0x71, 0x71, YM2203_read_port_0_r ),
		new IO_ReadPort( 0x72, 0x72, gfire2_protection_r ),
		new IO_ReadPort( 0x74, 0x74, input_port_0_r ),
		new IO_ReadPort( 0x76, 0x76, input_port_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort gfire2_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x3f, IOWP_NOP ),	/* Z180 internal registers */
		new IO_WritePort( 0x70, 0x70, YM2203_control_port_0_w ),
		new IO_WritePort( 0x71, 0x71, YM2203_write_port_0_w ),
		new IO_WritePort( 0x72, 0x72, gfire2_protection_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortHandlerPtr input_ports_mosaic = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( mosaic )
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START();       /* DSW1 */
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x40, 0x00, "Bombs" );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x40, "5" );
		PORT_DIPNAME( 0x20, 0x20, "Speed" );
		PORT_DIPSETTING(    0x20, "Low" );
		PORT_DIPSETTING(    0x00, "High" );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x02, 0x00, "Music" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x01, 0x00, "Sound" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_gfire2 = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( gfire2 )
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x80, 0x00, "Language" );
		PORT_DIPSETTING(    0x00, "English" );
		PORT_DIPSETTING(    0x80, "Korean" );
		PORT_DIPNAME( 0x60, 0x60, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x0c, "Easy" );
		PORT_DIPSETTING(    0x08, "Normal" );
		PORT_DIPSETTING(    0x04, "Hard" );
	//	PORT_DIPSETTING(    0x00, "Hard" );
		PORT_DIPNAME( 0x02, 0x02, "Bonus Time" );
		PORT_DIPSETTING(    0x00, "*2 +30" );
		PORT_DIPSETTING(    0x02, "*2 +50" );
		PORT_SERVICE( 0x01, IP_ACTIVE_LOW );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,4),
		8,
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] {	RGN_FRAC(3,4)+0, RGN_FRAC(2,4)+0, RGN_FRAC(1,4)+0, RGN_FRAC(0,4)+0,
			RGN_FRAC(3,4)+8, RGN_FRAC(2,4)+8, RGN_FRAC(1,4)+8, RGN_FRAC(0,4)+8 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		16*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout, 0, 1 ),
		new GfxDecodeInfo( REGION_GFX2, 0, charlayout, 0, 1 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static struct YM2203interface ym2203_interface =
	{
		1,
		3000000,	/* ??? */
		{ YM2203_VOL(50,50) },
		{ input_port_2_r },
		{ 0 },
		{ 0	},
		{ 0 },
		{ 0 }
	};
	
	
	
	public static MachineHandlerPtr machine_driver_mosaic = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
		MDRV_CPU_ADD_TAG("main", Z180, 7000000)	/* ??? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(8*8, 48*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_VIDEO_START(mosaic)
		MDRV_VIDEO_UPDATE(mosaic)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2203, ym2203_interface)
	MACHINE_DRIVER_END();
 }
};
	
	public static MachineHandlerPtr machine_driver_gfire2 = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
		MDRV_IMPORT_FROM(mosaic)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(gfire2_readmem,gfire2_writemem)
		MDRV_CPU_PORTS(gfire2_readport,gfire2_writeport)
	MACHINE_DRIVER_END();
 }
};
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadHandlerPtr rom_mosaic = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x100000, REGION_CPU1, 0 )	/* 1024k for Z180 address space */
		ROM_LOAD( "mosaic.9", 0x00000, 0x10000, CRC(5794dd39) SHA1(28784371f4ca561e3c0fb74d1f0a204f58ccdd3a) )
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "mosaic.1", 0x00000, 0x10000, CRC(05f4cc70) SHA1(367cfa716b5d24663efcd98a4a80bf02ef28f2f8) )
		ROM_LOAD( "mosaic.2", 0x10000, 0x10000, CRC(78907875) SHA1(073b90e0303f7812e7e8f66bb798a7734cb36bb9) )
		ROM_LOAD( "mosaic.3", 0x20000, 0x10000, CRC(f81294cd) SHA1(9bce627bbe3940769776121fb4296f92ac4c7d1a) )
		ROM_LOAD( "mosaic.4", 0x30000, 0x10000, CRC(fff72536) SHA1(4fc5d0a79128dd49275bc4c4cc2dd7c587096fd8) )
	
		ROM_REGION( 0x40000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "mosaic.5", 0x00000, 0x10000, CRC(28513fbf) SHA1(e69051206cc3df470e7b2358c51cbbed294795f5) )
		ROM_LOAD( "mosaic.6", 0x10000, 0x10000, CRC(1b8854c4) SHA1(d49df2565d9ccda403fafb9e219d3603776e3d34) )
		ROM_LOAD( "mosaic.7", 0x20000, 0x10000, CRC(35674ac2) SHA1(6422a81034b6d34aefc8ca5d2926d3d3c3d7ff77) )
		ROM_LOAD( "mosaic.8", 0x30000, 0x10000, CRC(6299c376) SHA1(eb64b20268c06c97c4201c8004a759b6de42fab6) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_mosaica = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x100000, REGION_CPU1, 0 )	/* 1024k for Z180 address space */
		ROM_LOAD( "mosaic_9.a02", 0x00000, 0x10000, CRC(ecb4f8aa) SHA1(e45c074bac92d1d079cf1bcc0a6a081beb3dbb8e) )
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "mosaic.1", 0x00000, 0x10000, CRC(05f4cc70) SHA1(367cfa716b5d24663efcd98a4a80bf02ef28f2f8) )
		ROM_LOAD( "mosaic.2", 0x10000, 0x10000, CRC(78907875) SHA1(073b90e0303f7812e7e8f66bb798a7734cb36bb9) )
		ROM_LOAD( "mosaic.3", 0x20000, 0x10000, CRC(f81294cd) SHA1(9bce627bbe3940769776121fb4296f92ac4c7d1a) )
		ROM_LOAD( "mosaic.4", 0x30000, 0x10000, CRC(fff72536) SHA1(4fc5d0a79128dd49275bc4c4cc2dd7c587096fd8) )
	
		ROM_REGION( 0x40000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "mosaic.5", 0x00000, 0x10000, CRC(28513fbf) SHA1(e69051206cc3df470e7b2358c51cbbed294795f5) )
		ROM_LOAD( "mosaic.6", 0x10000, 0x10000, CRC(1b8854c4) SHA1(d49df2565d9ccda403fafb9e219d3603776e3d34) )
		ROM_LOAD( "mosaic.7", 0x20000, 0x10000, CRC(35674ac2) SHA1(6422a81034b6d34aefc8ca5d2926d3d3c3d7ff77) )
		ROM_LOAD( "mosaic.8", 0x30000, 0x10000, CRC(6299c376) SHA1(eb64b20268c06c97c4201c8004a759b6de42fab6) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_gfire2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x100000, REGION_CPU1, 0 )	/* 1024k for Z180 address space */
		ROM_LOAD( "goldf2_i.7e",         0x00000, 0x10000, CRC(a102f7d0) SHA1(cfde51d0e9e69e9653fdfd70d4e4f4649b662005) )
	
		ROM_REGION( 0x100000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "goldf2_a.1k",         0x00000, 0x40000, CRC(1f086472) SHA1(c776a734869b6bab317627bd15457a9fb18e1159) )
		ROM_LOAD( "goldf2_b.1j",         0x40000, 0x40000, CRC(edb0d40c) SHA1(624a71b42a2e6c7c55cf455395aa0ad9b3eaeb9e) )
		ROM_LOAD( "goldf2_c.1i",         0x80000, 0x40000, CRC(d0ebd486) SHA1(ff2bfc84bc622b437913e1861f7acb373c7844c8) )
		ROM_LOAD( "goldf2_d.1h",         0xc0000, 0x40000, CRC(2b56ae2c) SHA1(667f9093ed28ba1804583fb201c7e3b37f1a9927) )
	
		ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "goldf2_e.1e",         0x00000, 0x20000, CRC(61b8accd) SHA1(d6317b8b7ab33a2a78d388b87ddb8946e6c6df29) )
		ROM_LOAD( "goldf2_f.1d",         0x20000, 0x20000, CRC(49f77e53) SHA1(6e7c8f86cb368bf1a32f02f72e7b418684c847dc) )
		ROM_LOAD( "goldf2_g.1b",         0x40000, 0x20000, CRC(aa79f3bf) SHA1(c0b62f5de7e36ce1ef1de92ee6f63d8286815566) )
		ROM_LOAD( "goldf2_h.1a",         0x60000, 0x20000, CRC(a3519259) SHA1(9e1edb50ade4a4ddcd628a897f6fa712075a888b) )
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_mosaic	   = new GameDriver("1990"	,"mosaic"	,"mosaic.java"	,rom_mosaic,null	,machine_driver_mosaic	,input_ports_mosaic	,null	,ROT0, "Space", "Mosaic" )
	public static GameDriver driver_mosaica	   = new GameDriver("1990"	,"mosaica"	,"mosaic.java"	,rom_mosaica,driver_mosaic	,machine_driver_mosaic	,input_ports_mosaic	,null	,ROT0, "Space (Fuuki license)", "Mosaic (Fuuki)" )
	public static GameDriver driver_gfire2	   = new GameDriver("1992"	,"gfire2"	,"mosaic.java"	,rom_gfire2,null	,machine_driver_gfire2	,input_ports_gfire2	,null	,ROT0, "Topis Corp", "Golden Fire II" )
}
