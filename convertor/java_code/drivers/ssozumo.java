/***************************************************************************

Syusse Oozumou
(c) 1984 Technos Japan (Licensed by Data East)

Driver by Takahiro Nogi (nogi@kt.rim.or.jp) 1999/10/04

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class ssozumo
{
	
	
	
	
	
	public static InterruptHandlerPtr ssozumo_interrupt = new InterruptHandlerPtr() {public void handler(){
		static int coin;
	
		if ((readinputport(0) & 0xc0) != 0xc0)
		{
			if (coin == 0)
			{
				coin = 1;
				nmi_line_pulse();
				return;
			}
		}
		else coin = 0;
	
		irq0_line_hold();
	} };
	
	
	public static WriteHandlerPtr ssozumo_sh_command_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		soundlatch_w.handler(offset, data);
		cpu_set_irq_line(1, M6502_IRQ_LINE, HOLD_LINE);
	} };
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x077f, MRA_RAM ),
	
		new Memory_ReadAddress( 0x2000, 0x27ff, MRA_RAM ),
		new Memory_ReadAddress( 0x3000, 0x31ff, MRA_RAM ),
	
		new Memory_ReadAddress( 0x4000, 0x4000, input_port_0_r ),
		new Memory_ReadAddress( 0x4010, 0x4010, input_port_1_r ),
		new Memory_ReadAddress( 0x4020, 0x4020, input_port_2_r ),
		new Memory_ReadAddress( 0x4030, 0x4030, input_port_3_r ),
	
		new Memory_ReadAddress( 0x6000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x077f, MWA_RAM ),
	
		new Memory_WriteAddress( 0x0780, 0x07ff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0x2000, 0x23ff, ssozumo_videoram2_w, ssozumo_videoram2 ),
		new Memory_WriteAddress( 0x2400, 0x27ff, ssozumo_colorram2_w, ssozumo_colorram2 ),
		new Memory_WriteAddress( 0x3000, 0x31ff, ssozumo_videoram_w, videoram ),
		new Memory_WriteAddress( 0x3200, 0x33ff, ssozumo_colorram_w, colorram ),
		new Memory_WriteAddress( 0x3400, 0x35ff, MWA_RAM ),
		new Memory_WriteAddress( 0x3600, 0x37ff, MWA_RAM ),
	
		new Memory_WriteAddress( 0x4000, 0x4000, ssozumo_flipscreen_w ),
		new Memory_WriteAddress( 0x4010, 0x4010, ssozumo_sh_command_w ),
		new Memory_WriteAddress( 0x4020, 0x4020, ssozumo_scroll_w ),
	//	new Memory_WriteAddress( 0x4030, 0x4030, MWA_RAM ),
		new Memory_WriteAddress( 0x4050, 0x407f, ssozumo_paletteram_w, paletteram ),
	
		new Memory_WriteAddress( 0x6000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x01ff, MRA_RAM ),
		new Memory_ReadAddress( 0x2007, 0x2007, soundlatch_r ),
		new Memory_ReadAddress( 0x4000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x01ff, MWA_RAM ),
		new Memory_WriteAddress( 0x2000, 0x2000, AY8910_write_port_0_w ),
		new Memory_WriteAddress( 0x2001, 0x2001, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0x2002, 0x2002, AY8910_write_port_1_w ),
		new Memory_WriteAddress( 0x2003, 0x2003, AY8910_control_port_1_w ),
		new Memory_WriteAddress( 0x2004, 0x2004, DAC_0_signed_data_w ),
		new Memory_WriteAddress( 0x2005, 0x2005, interrupt_enable_w ),
		new Memory_WriteAddress( 0x4000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	static InputPortHandlerPtr input_ports_ssozumo = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( ssozumo )
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START2 );
	
		PORT_START();       /* DSW2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x01, "Normal" );
		PORT_DIPSETTING(    0x00, "Hard" );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x40, 0x00, "Controls" );
		PORT_DIPSETTING(    0x00, "Single" );
		PORT_DIPSETTING(    0x40, "Dual" );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_VBLANK );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,		/* 8*8 characters */
		1024,		/* 1024 characters */
		3,		/* 3 bits per pixel */
		new int[] { 2*1024*8*8, 1024*8*8, 0 },	/* the bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8		/* every char takes 8 consecutive bytes */
	);
	
	
	static GfxLayout tilelayout = new GfxLayout
	(
		16,16,	/* 16*16 tiles */
		256,	/* 256 tiles */
		3,		/* 3 bits per pixel */
		new int[] { 2*256*16*16, 256*16*16, 0 },	/* the bitplanes are separated */
		new int[] { 16*8 + 0, 16*8 + 1, 16*8 + 2, 16*8 + 3, 16*8 + 4, 16*8 + 5, 16*8 + 6, 16*8 + 7,
				0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
		32*8		/* every tile takes 16 consecutive bytes */
	);
	
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,		/* 16*16 sprites */
		1280,		/* 1280 sprites */
		3,		/* 3 bits per pixel */
		new int[] { 2*1280*16*16, 1280*16*16, 0 },	/* the bitplanes are separated */
		new int[] { 16*8 + 0, 16*8 + 1, 16*8 + 2, 16*8 + 3, 16*8 + 4, 16*8 + 5, 16*8 + 6, 16*8 + 7,
				0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
		32*8		/* every sprite takes 16 consecutive bytes */
	);
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,     0, 4 ),
		new GfxDecodeInfo( REGION_GFX2, 0, tilelayout,   4*8, 4 ),
		new GfxDecodeInfo( REGION_GFX3, 0, spritelayout, 8*8, 2 ),
		new GfxDecodeInfo( -1 )		/* end of array */
	};
	
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		2,		/* 2 chips */
		1500000,	/* 1.5 MHz?????? */
		new int[] { 30, 30 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	
	static DACinterface dac_interface = new DACinterface
	(
		1,
		new int[] { 30 }
	);
	
	
	public static MachineHandlerPtr machine_driver_ssozumo = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M6502, 1200000)	/* 1.2 MHz ???? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(ssozumo_interrupt,1)
	
		MDRV_CPU_ADD(M6502, 975000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU) 		/* 975 kHz ?? */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,16)	/* IRQs are triggered by the main CPU */
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8 - 1, 1*8, 31*8 - 1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(64 + 16)
		MDRV_COLORTABLE_LENGTH(64 + 16)
	
		MDRV_PALETTE_INIT(ssozumo)
		MDRV_VIDEO_START(ssozumo)
		MDRV_VIDEO_UPDATE(ssozumo)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	
	static RomLoadHandlerPtr rom_ssozumo = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )
		/* Main Program ROMs */
		ROM_LOAD( "ic61.g01",	0x06000, 0x2000, CRC(86968f46) SHA1(6acd111b71fbb4ef00ae03be4fb93d305a6564e7) )	// m1
		ROM_LOAD( "ic60.g11",	0x08000, 0x2000, CRC(1a5143dd) SHA1(19e36afcd0827f14f4360b55d952cc1af38327fd) )	// m2
		ROM_LOAD( "ic59.g21",	0x0a000, 0x2000, CRC(d3df04d7) SHA1(a95cff7f67ad2a3dbf7147018889a0de3f9fcbac) )	// m3
		ROM_LOAD( "ic58.g31",	0x0c000, 0x2000, CRC(0ee43a78) SHA1(383a29a2dfdbd600dacf3885039759efab718a45) )	// m4
		ROM_LOAD( "ic57.g41",	0x0e000, 0x2000, CRC(ac77aa4c) SHA1(36ee826327e4433bcdcb8d770fc6176f53d3eed0) )	// m5
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )
		/* Sound Program & Voice Sample ROMs*/
		ROM_LOAD( "ic47.g50",	0x04000, 0x2000, CRC(b64ec829) SHA1(684f1c37c05fc3812f11e040fb96789c8abb987f) )	// a1
		ROM_LOAD( "ic46.g60",	0x06000, 0x2000, CRC(630d7380) SHA1(aab3f034417a9712c8fa922946eda02751c9e319) )	// a2
		ROM_LOAD( "ic45.g70",	0x08000, 0x2000, CRC(1854b657) SHA1(c4f3c24a2b03bdf4d9fd80d6df944a157f98e617) )	// a3
		ROM_LOAD( "ic44.g80",	0x0a000, 0x2000, CRC(40b9a0da) SHA1(ef51977d23e14fb638b26afcb2617933446d8143) )	// a4
		ROM_LOAD( "ic43.g90",	0x0c000, 0x2000, CRC(20262064) SHA1(2845efa458f4fd873b8559489bcee4b9d8e437c1) )	// a5
		ROM_LOAD( "ic42.ga0",	0x0e000, 0x2000, CRC(98d7e998) SHA1(16bb3315db7d52531a3297e1255478aa1ebc32c2) )	// a6
	
		ROM_REGION( 0x06000, REGION_GFX1, ROMREGION_DISPOSE )
		/* Character ROMs */
		ROM_LOAD( "ic22.gq0",	0x00000, 0x2000, CRC(b4c7e612) SHA1(2d4f6f79b65aa27e00f173777959ec07e81ff15e) )	// c1
		ROM_LOAD( "ic23.gr0",	0x02000, 0x2000, CRC(90bb9fda) SHA1(9c065a54330133e5afadcb2ae29add5e1005d977) )	// c2
		ROM_LOAD( "ic21.gs0",	0x04000, 0x2000, CRC(d8cd5c78) SHA1(f1567850db649d2b7a029a5f71bbade25bb0393f) )	// c3
	
		ROM_REGION( 0x06000, REGION_GFX2, ROMREGION_DISPOSE )
		/* tile set ROMs */
		ROM_LOAD( "ic69.gt0",	0x00000, 0x2000, CRC(771116ca) SHA1(2d1c656315f57e1a142725e2d2034543cb3917ea) )	// t1
		ROM_LOAD( "ic59.gu0",	0x02000, 0x2000, CRC(68035bfd) SHA1(da535ff6860f71c1780d4d9dfd1944e355234c5b) )	// t2
		ROM_LOAD( "ic81.gv0",	0x04000, 0x2000, CRC(cdda1f9f) SHA1(d1f1b3e0578fd991c74d4a85313c5d37f08f1eee) )	// t3
	
		ROM_REGION( 0x1e000, REGION_GFX3, ROMREGION_DISPOSE )
		/* sprites ROMs */
		ROM_LOAD( "ic06.gg0",	0x00000, 0x2000, CRC(d2342c50) SHA1(f502b716d659d9fd3119dbb454296fe9e280fa5d) )	// s1a
		ROM_LOAD( "ic05.gh0",	0x02000, 0x2000, CRC(14a3cb10) SHA1(7b6d63f43ebbe3c3aea7f2e04789cdb78cdd8495) )	// s1b
		ROM_LOAD( "ic04.gi0",	0x04000, 0x2000, CRC(169276c1) SHA1(7f0b54425e0f82f7fcc892d7b8e7719087060d2a) )	// s1c
		ROM_LOAD( "ic03.gj0",	0x06000, 0x2000, CRC(e71b9f28) SHA1(1f4f1a4d44fecb212778bb191e14bbfdc41556a5) )	// s1d
		ROM_LOAD( "ic02.gk0",	0x08000, 0x2000, CRC(6e94773c) SHA1(c3a1b950c1abce7103e6a0c19b5bc47a46612b05) )	// s1e
		ROM_LOAD( "ic29.gl0",	0x0a000, 0x2000, CRC(40f67cc4) SHA1(fb6cfa9c9665c719926fc6ef050682f040852840) )	// s2a
		ROM_LOAD( "ic28.gm0",	0x0c000, 0x2000, CRC(8c97b1a2) SHA1(72ca28959b532f98e0836a9650bb3dd3fdfa755a) )	// s2b
		ROM_LOAD( "ic27.gn0",	0x0e000, 0x2000, CRC(be8bb3dd) SHA1(d032591e73b09e2f076a18298d606edf16998a64) )	// s2c
		ROM_LOAD( "ic26.go0",	0x10000, 0x2000, CRC(9c098a2c) SHA1(d2093f1a4f4b3bf3bbff0adea5bd910993ed4704) )	// s2d
		ROM_LOAD( "ic25.gp0",	0x12000, 0x2000, CRC(f73f8a76) SHA1(13652779d3d30de0b4136eb3f43ee5429861bf35) )	// s2e
		ROM_LOAD( "ic44.gb0",	0x14000, 0x2000, CRC(cdd7f2eb) SHA1(57cf788804f9d2a1283032c25b608ac45064eddb) )	// s3a
		ROM_LOAD( "ic43.gc0",	0x16000, 0x2000, CRC(7b4c632e) SHA1(2acb0f2213928b97fdf239fbabc6d24329cbdd7a) )	// s3b
		ROM_LOAD( "ic42.gd0",	0x18000, 0x2000, CRC(cd1c8fe6) SHA1(ac085a0e8e228ea6bfbe86f209be08221bb066ee) )	// s3c
		ROM_LOAD( "ic41.ge0",	0x1a000, 0x2000, CRC(935578d0) SHA1(e9a9f439e0781627df076c454b16f5796ac991bc) )	// s3d
		ROM_LOAD( "ic40.gf0",	0x1c000, 0x2000, CRC(5a3bf1ba) SHA1(6beebb7ac9c8baa3bbb5b0ebf6a6da768e52d1d3) )	// s3e
	
		ROM_REGION( 0x0080, REGION_PROMS, 0 )
		ROM_LOAD( "ic33.gz0",	0x00000, 0x0020, CRC(523d29ad) SHA1(48d0ae83a07e4409a1def56772c5156e8d505749) )	/* char palette red and green components */
		ROM_LOAD( "ic30.gz2",	0x00020, 0x0020, CRC(0de202e1) SHA1(ca1aa66c1d3d4724d322ec0346860c37729ddaed) )	/* tile palette red and green components */
		ROM_LOAD( "ic32.gz1",	0x00040, 0x0020, CRC(6fbff4d2) SHA1(b2cd38fa8e9a74539b96d6e8e0375fff2dd77a20) )	/* char palette blue component */
		ROM_LOAD( "ic31.gz3",	0x00060, 0x0020, CRC(18e7fe63) SHA1(b0834b94b22ead765ddac5591ab1dc66ec20f17f) )	/* tile palette blue component */
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_ssozumo	   = new GameDriver("1984"	,"ssozumo"	,"ssozumo.java"	,rom_ssozumo,null	,machine_driver_ssozumo	,input_ports_ssozumo	,null	,ROT270, "Technos", "Syusse Oozumou (Japan)" )
}
