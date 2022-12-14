/***************************************************************************

  Crude Buster (World version FX)		(c) 1990 Data East Corporation
  Crude Buster (World version FU)		(c) 1990 Data East Corporation
  Crude Buster (Japanese version)		(c) 1990 Data East Corporation
  Two Crude (USA version)	    		(c) 1990 Data East USA

  The 'FX' board is filled with 'FU' roms except for the 4 program roms,
  both boards have 'export' stickers which usually indicates a World version.
  Maybe one is a UK or European version.

  Emulation by Bryan McPhail, mish@tendril.co.uk

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class cbuster
{
	
	
	WRITE16_HANDLER( twocrude_pf1_data_w );
	WRITE16_HANDLER( twocrude_pf2_data_w );
	WRITE16_HANDLER( twocrude_pf3_data_w );
	WRITE16_HANDLER( twocrude_pf4_data_w );
	WRITE16_HANDLER( twocrude_control_0_w );
	WRITE16_HANDLER( twocrude_control_1_w );
	WRITE16_HANDLER( twocrude_palette_24bit_rg_w );
	WRITE16_HANDLER( twocrude_palette_24bit_b_w );
	
	static data16_t *twocrude_ram;
	WRITE16_HANDLER( twocrude_update_sprites_w );
	static data16_t prot;
	
	/******************************************************************************/
	
	static WRITE16_HANDLER( twocrude_control_w )
	{
		switch (offset<<1) {
		case 0: /* DMA flag */
			buffer_spriteram16_w(0,0,0);
			return;
	
		case 6: /* IRQ ack */
			return;
	
	    case 2: /* Sound CPU write */
			soundlatch_w(0,data & 0xff);
			cpu_set_irq_line(1,0,HOLD_LINE);
	    	return;
	
		case 4: /* Protection, maybe this is a PAL on the board?
	
				80046 is level number
				stop at stage and enter.
				see also 8216..
	
					9a 00 = pf4 over pf3 (normal) (level 0)
					9a f1 =  (level 1 - water), pf3 over ALL sprites + pf4
					9a 80 = pf3 over pf4 (Level 2 - copter)
					9a 40 = pf3 over ALL sprites + pf4 (snow) level 3
					9a c0 = doesn't matter?
					9a ff = pf 3 over pf4
	
				I can't find a priority register, I assume it's tied to the
				protection?!
	
			*/
			if ((data&0xffff)==0x9a00) prot=0;
			if ((data&0xffff)==0xaa) prot=0x74;
			if ((data&0xffff)==0x0200) prot=0x63<<8;
			if ((data&0xffff)==0x9a) prot=0xe;
			if ((data&0xffff)==0x55) prot=0x1e;
			if ((data&0xffff)==0x0e) {prot=0x0e;twocrude_pri_w(0);} /* start */
			if ((data&0xffff)==0x00) {prot=0x0e;twocrude_pri_w(0);} /* level 0 */
			if ((data&0xffff)==0xf1) {prot=0x36;twocrude_pri_w(1);} /* level 1 */
			if ((data&0xffff)==0x80) {prot=0x2e;twocrude_pri_w(1);} /* level 2 */
			if ((data&0xffff)==0x40) {prot=0x1e;twocrude_pri_w(1);} /* level 3 */
			if ((data&0xffff)==0xc0) {prot=0x3e;twocrude_pri_w(0);} /* level 4 */
			if ((data&0xffff)==0xff) {prot=0x76;twocrude_pri_w(1);} /* level 5 */
	
			break;
		}
		logerror("Warning %04x- %02x written to control %02x\n",activecpu_get_pc(),data,offset);
	}
	
	READ16_HANDLER( twocrude_control_r )
	{
		switch (offset<<1)
		{
			case 0: /* Player 1 & Player 2 joysticks & fire buttons */
				return (readinputport(0) + (readinputport(1) << 8));
	
			case 2: /* Dip Switches */
				return (readinputport(3) + (readinputport(4) << 8));
	
			case 4: /* Protection */
				logerror("%04x : protection control read at 30c000 %d\n",activecpu_get_pc(),offset);
				return prot;
	
			case 6: /* Credits, VBL in byte 7 */
				return readinputport(2);
		}
	
		return ~0;
	}
	
	static READ16_HANDLER( twocrude_pf1_data_r ) { return twocrude_pf1_data[offset]; }
	static READ16_HANDLER( twocrude_pf2_data_r ) { return twocrude_pf2_data[offset]; }
	static READ16_HANDLER( twocrude_pf3_data_r ) { return twocrude_pf3_data[offset]; }
	static READ16_HANDLER( twocrude_pf4_data_r ) { return twocrude_pf4_data[offset]; }
	
	/******************************************************************************/
	
	static MEMORY_READ16_START( twocrude_readmem )
		{ 0x000000, 0x07ffff, MRA16_ROM },
		{ 0x080000, 0x083fff, MRA16_RAM },
	
		{ 0x0a0000, 0x0a1fff, twocrude_pf1_data_r },
		{ 0x0a2000, 0x0a2fff, twocrude_pf4_data_r },
		{ 0x0a4000, 0x0a47ff, MRA16_RAM },
		{ 0x0a6000, 0x0a67ff, MRA16_RAM },
	
		{ 0x0a8000, 0x0a8fff, twocrude_pf3_data_r },
		{ 0x0aa000, 0x0aafff, twocrude_pf2_data_r },
		{ 0x0ac000, 0x0ac7ff, MRA16_RAM },
		{ 0x0ae000, 0x0ae7ff, MRA16_RAM },
	
		{ 0x0b0000, 0x0b07ff, MRA16_RAM },
		{ 0x0b8000, 0x0b8fff, MRA16_RAM },
		{ 0x0b9000, 0x0b9fff, MRA16_RAM },
		{ 0x0bc000, 0x0bc00f, twocrude_control_r },
	MEMORY_END
	
	static MEMORY_WRITE16_START( twocrude_writemem )
		{ 0x000000, 0x07ffff, MWA16_ROM },
		{ 0x080000, 0x083fff, MWA16_RAM, &twocrude_ram },
	
		{ 0x0a0000, 0x0a1fff, twocrude_pf1_data_w, &twocrude_pf1_data },
		{ 0x0a2000, 0x0a2fff, twocrude_pf4_data_w, &twocrude_pf4_data },
		{ 0x0a4000, 0x0a47ff, MWA16_RAM, &twocrude_pf1_rowscroll },
		{ 0x0a6000, 0x0a67ff, MWA16_RAM, &twocrude_pf4_rowscroll },
	
		{ 0x0a8000, 0x0a8fff, twocrude_pf3_data_w, &twocrude_pf3_data },
		{ 0x0aa000, 0x0aafff, twocrude_pf2_data_w, &twocrude_pf2_data },
		{ 0x0ac000, 0x0ac7ff, MWA16_RAM, &twocrude_pf3_rowscroll },
		{ 0x0ae000, 0x0ae7ff, MWA16_RAM, &twocrude_pf2_rowscroll },
	
		{ 0x0b0000, 0x0b07ff, MWA16_RAM, &spriteram16, &spriteram_size },
		{ 0x0b4000, 0x0b4001, MWA16_NOP },
		{ 0x0b5000, 0x0b500f, twocrude_control_1_w },
		{ 0x0b6000, 0x0b600f, twocrude_control_0_w },
		{ 0x0b8000, 0x0b8fff, twocrude_palette_24bit_rg_w, &paletteram16 },
		{ 0x0b9000, 0x0b9fff, twocrude_palette_24bit_b_w, &paletteram16_2 },
		{ 0x0bc000, 0x0bc00f, twocrude_control_w },
	MEMORY_END
	
	/******************************************************************************/
	
	public static WriteHandlerPtr YM2151_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		switch (offset) {
		case 0:
			YM2151_register_port_0_w(0,data);
			break;
		case 1:
			YM2151_data_port_0_w(0,data);
			break;
		}
	} };
	
	public static WriteHandlerPtr YM2203_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		switch (offset) {
		case 0:
			YM2203_control_port_0_w(0,data);
			break;
		case 1:
			YM2203_write_port_0_w(0,data);
			break;
		}
	} };
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x000000, 0x00ffff, MRA_ROM ),
		new Memory_ReadAddress( 0x100000, 0x100001, YM2203_status_port_0_r ),
		new Memory_ReadAddress( 0x110000, 0x110001, YM2151_status_port_0_r ),
		new Memory_ReadAddress( 0x120000, 0x120001, OKIM6295_status_0_r ),
		new Memory_ReadAddress( 0x130000, 0x130001, OKIM6295_status_1_r ),
		new Memory_ReadAddress( 0x140000, 0x140001, soundlatch_r ),
		new Memory_ReadAddress( 0x1f0000, 0x1f1fff, MRA_BANK8 ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x000000, 0x00ffff, MWA_ROM ),
		new Memory_WriteAddress( 0x100000, 0x100001, YM2203_w ),
		new Memory_WriteAddress( 0x110000, 0x110001, YM2151_w ),
		new Memory_WriteAddress( 0x120000, 0x120001, OKIM6295_data_0_w ),
		new Memory_WriteAddress( 0x130000, 0x130001, OKIM6295_data_1_w ),
		new Memory_WriteAddress( 0x1f0000, 0x1f1fff, MWA_BANK8 ),
		new Memory_WriteAddress( 0x1fec00, 0x1fec01, H6280_timer_w ),
		new Memory_WriteAddress( 0x1ff402, 0x1ff403, H6280_irq_status_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	/******************************************************************************/
	
	static InputPortHandlerPtr input_ports_twocrude = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( twocrude )
		PORT_START(); 	/* Player 1 controls */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* Player 2 controls */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START2 );
	
		PORT_START(); 	/* Credits */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* Dip switch bank 1 */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x38, 0x38, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x38, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x28, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* Dip switch bank 2 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x01, "2" );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x02, "4" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x0c, "Normal" );
		PORT_DIPSETTING(    0x08, "Easy" );
		PORT_DIPSETTING(    0x04, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	/******************************************************************************/
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		4096,
		4,
		new int[] { 0x10000*8+8, 8, 0x10000*8, 0 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				 },
		16*8
	);
	
	static GfxLayout tilelayout = new GfxLayout
	(
		16,16,
		4096,
		4,
		new int[] { 24, 16, 8, 0 },
		new int[] { 64*8+0, 64*8+1, 64*8+2, 64*8+3, 64*8+4, 64*8+5, 64*8+6, 64*8+7,
			0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32,
				8*32, 9*32, 10*32, 11*32, 12*32, 13*32, 14*32, 15*32 },
		128*8
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,
		(4096*2)+2048,  /* Main bank + 4 extra roms */
		4,
		new int[] { 0xa0000*8+8, 0xa0000*8, 8, 0 },
		new int[] { 32*8+0, 32*8+1, 32*8+2, 32*8+3, 32*8+4, 32*8+5, 32*8+6, 32*8+7,
			0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		64*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout, 	   0, 16 ),	/* Characters 8x8 */
		new GfxDecodeInfo( REGION_GFX2, 0, tilelayout,  1024, 16 ),	/* Tiles 16x16 */
		new GfxDecodeInfo( REGION_GFX2, 0, tilelayout,   768, 16 ),	/* Tiles 16x16 */
		new GfxDecodeInfo( REGION_GFX3, 0, tilelayout,   512, 16 ),	/* Tiles 16x16 */
		new GfxDecodeInfo( REGION_GFX4, 0, spritelayout, 256, 80 ),	/* Sprites 16x16 */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	/******************************************************************************/
	
	static struct OKIM6295interface okim6295_interface =
	{
		2,              /* 2 chips */
		{ 32220000/32/132, 32220000/16/132 },/* Frequency */
		{ REGION_SOUND1, REGION_SOUND2 },
		{ 75, 60 } /* Note!  Keep chip 1 (voices) louder than chip 2 */
	};
	
	static struct YM2203interface ym2203_interface =
	{
		1,
		32220000/8, /* Accurate, audio section crystal is 32.220 MHz */
		{ YM2203_VOL(60,60) },
		{ 0 },
		{ 0 },
		{ 0 },
		{ 0 }
	};
	
	static void sound_irq(int state)
	{
		cpu_set_irq_line(1,1,state); /* IRQ 2 */
	}
	
	static struct YM2151interface ym2151_interface =
	{
		1,
		32220000/9, /* Accurate, audio section crystal is 32.220 MHz */
		{ YM3012_VOL(45,MIXER_PAN_LEFT,45,MIXER_PAN_RIGHT) },
		{ sound_irq }
	};
	
	public static MachineHandlerPtr machine_driver_twocrude = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 12000000) /* Accurate */
		MDRV_CPU_MEMORY(twocrude_readmem,twocrude_writemem)
		MDRV_CPU_VBLANK_INT(irq4_line_hold,1)/* VBL */
	
		MDRV_CPU_ADD(H6280,32220000/8)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* Accurate */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
	
		MDRV_FRAMES_PER_SECOND(58)
		MDRV_VBLANK_DURATION(529)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_NEEDS_6BITS_PER_GUN | VIDEO_BUFFERS_SPRITERAM)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 1*8, 31*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(twocrude)
		MDRV_VIDEO_UPDATE(twocrude)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2203, ym2203_interface)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(OKIM6295, okim6295_interface)
	MACHINE_DRIVER_END();
 }
};
	
	/******************************************************************************/
	
	static RomLoadHandlerPtr rom_cbuster = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 ) /* 68000 code */
	  	ROM_LOAD16_BYTE( "fx01.rom", 0x00000, 0x20000, CRC(ddae6d83) SHA1(ce3fed1393b71821730fb8d87869a89c8e07c456) )
		ROM_LOAD16_BYTE( "fx00.rom", 0x00001, 0x20000, CRC(5bc2c0de) SHA1(fa9c357ae4a5c814b7113df3b2f12982077f3e6b) )
	  	ROM_LOAD16_BYTE( "fx03.rom", 0x40000, 0x20000, CRC(c3d65bf9) SHA1(99dd650fd4b427bca25a0776fbd6221f93504106) )
	 	ROM_LOAD16_BYTE( "fx02.rom", 0x40001, 0x20000, CRC(b875266b) SHA1(a76f8e061392e17394a3f975584823ad39e0097e) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* Sound CPU */
		ROM_LOAD( "fu11-.rom",     0x00000, 0x10000, CRC(65f20f10) SHA1(cf914893edd98a0f39bbf7068a469ed7d34bd90e) )
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "fu05-.rom",     0x00000, 0x10000, CRC(8134d412) SHA1(9c70ff6f9f24ec89c0bb4645afdf2a5ca27e9a0c) ) /* Chars */
		ROM_LOAD( "fu06-.rom",     0x10000, 0x10000, CRC(2f914a45) SHA1(bb44ba4779e45ee77ef0006363df91aac1f4559a) )
	
		ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "mab-01",        0x00000, 0x80000, CRC(1080d619) SHA1(68f33a1580d33e4dd0858248c12a0a10ac117249) ) /* Tiles */
	
		ROM_REGION( 0x80000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "mab-00",        0x00000, 0x80000, CRC(660eaabd) SHA1(e3d614e13fdb9af159d9758a869d9dae3dbe14e0) ) /* Tiles */
	
		ROM_REGION( 0x180000,REGION_GFX4, ROMREGION_DISPOSE )
		ROM_LOAD( "mab-02",        0x000000, 0x80000, CRC(58b7231d) SHA1(5b51a2fa42c67f23648be205295184a1fddc00f5) ) /* Sprites */
		/* Space for extra sprites to be copied to (0x20000) */
		ROM_LOAD( "mab-03",        0x0a0000, 0x80000, CRC(76053b9d) SHA1(093cd01a13509701ec9dd1a806132600a5bd1915) )
	 	/* Space for extra sprites to be copied to (0x20000) */
		ROM_LOAD( "fu07-.rom",     0x140000, 0x10000, CRC(ca8d0bb3) SHA1(9262d6003cf0cb8c33d0f6c1d0ef35490b29f9b4) ) /* Extra sprites */
		ROM_LOAD( "fu08-.rom",     0x150000, 0x10000, CRC(c6afc5c8) SHA1(feddd546f09884c51e4d1802477de4e152a51082) )
		ROM_LOAD( "fu09-.rom",     0x160000, 0x10000, CRC(526809ca) SHA1(2cb9e7417211c1eb23d32e3fee71c5254d34a3ff) )
		ROM_LOAD( "fu10-.rom",     0x170000, 0x10000, CRC(6be6d50e) SHA1(b944db4b3a7c76190f6b40f71f033e16e7964f6a) )
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 )	/* ADPCM samples */
		ROM_LOAD( "fu12-.rom",     0x00000, 0x20000, CRC(2d1d65f2) SHA1(be3d57b9976ddf7ee6d20ee9e78fe826ee411d79) )
	
		ROM_REGION( 0x20000, REGION_SOUND2, 0 )	/* ADPCM samples */
		ROM_LOAD( "fu13-.rom",     0x00000, 0x20000, CRC(b8525622) SHA1(4a6ec5e3f64256b1383bfbab4167cbd2ec11b5c5) )
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 )
		ROM_LOAD( "mb7114h.18e",   0x0000, 0x0100, CRC(3645b70f) SHA1(7d3831867362037892b43efb007e27d3bd5f6488) )	/* Priority (not used) */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_cbusterw = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 ) /* 68000 code */
	  	ROM_LOAD16_BYTE( "fu01-.rom", 0x00000, 0x20000, CRC(0203e0f8) SHA1(7709636429f2cab43caba3422122dba970dfb50b) )
		ROM_LOAD16_BYTE( "fu00-.rom", 0x00001, 0x20000, CRC(9c58626d) SHA1(6bc950929391221755972658258937a1ef96c244) )
	  	ROM_LOAD16_BYTE( "fu03-.rom", 0x40000, 0x20000, CRC(def46956) SHA1(e1f71a440430f8f9351ee9e1826ca2d0d5a372f8) )
	 	ROM_LOAD16_BYTE( "fu02-.rom", 0x40001, 0x20000, CRC(649c3338) SHA1(06373b364283706f0b00ab6d014c674e4b9818fa) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* Sound CPU */
		ROM_LOAD( "fu11-.rom",     0x00000, 0x10000, CRC(65f20f10) SHA1(cf914893edd98a0f39bbf7068a469ed7d34bd90e) )
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "fu05-.rom",     0x00000, 0x10000, CRC(8134d412) SHA1(9c70ff6f9f24ec89c0bb4645afdf2a5ca27e9a0c) ) /* Chars */
		ROM_LOAD( "fu06-.rom",     0x10000, 0x10000, CRC(2f914a45) SHA1(bb44ba4779e45ee77ef0006363df91aac1f4559a) )
	
		ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "mab-01",        0x00000, 0x80000, CRC(1080d619) SHA1(68f33a1580d33e4dd0858248c12a0a10ac117249) ) /* Tiles */
	
		ROM_REGION( 0x80000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "mab-00",        0x00000, 0x80000, CRC(660eaabd) SHA1(e3d614e13fdb9af159d9758a869d9dae3dbe14e0) ) /* Tiles */
	
		ROM_REGION( 0x180000,REGION_GFX4, ROMREGION_DISPOSE )
		ROM_LOAD( "mab-02",        0x000000, 0x80000, CRC(58b7231d) SHA1(5b51a2fa42c67f23648be205295184a1fddc00f5) ) /* Sprites */
		/* Space for extra sprites to be copied to (0x20000) */
		ROM_LOAD( "mab-03",        0x0a0000, 0x80000, CRC(76053b9d) SHA1(093cd01a13509701ec9dd1a806132600a5bd1915) )
	 	/* Space for extra sprites to be copied to (0x20000) */
		ROM_LOAD( "fu07-.rom",     0x140000, 0x10000, CRC(ca8d0bb3) SHA1(9262d6003cf0cb8c33d0f6c1d0ef35490b29f9b4) ) /* Extra sprites */
		ROM_LOAD( "fu08-.rom",     0x150000, 0x10000, CRC(c6afc5c8) SHA1(feddd546f09884c51e4d1802477de4e152a51082) )
		ROM_LOAD( "fu09-.rom",     0x160000, 0x10000, CRC(526809ca) SHA1(2cb9e7417211c1eb23d32e3fee71c5254d34a3ff) )
		ROM_LOAD( "fu10-.rom",     0x170000, 0x10000, CRC(6be6d50e) SHA1(b944db4b3a7c76190f6b40f71f033e16e7964f6a) )
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 )	/* ADPCM samples */
		ROM_LOAD( "fu12-.rom",     0x00000, 0x20000, CRC(2d1d65f2) SHA1(be3d57b9976ddf7ee6d20ee9e78fe826ee411d79) )
	
		ROM_REGION( 0x20000, REGION_SOUND2, 0 )	/* ADPCM samples */
		ROM_LOAD( "fu13-.rom",     0x00000, 0x20000, CRC(b8525622) SHA1(4a6ec5e3f64256b1383bfbab4167cbd2ec11b5c5) )
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 )
		ROM_LOAD( "mb7114h.18e",   0x0000, 0x0100, CRC(3645b70f) SHA1(7d3831867362037892b43efb007e27d3bd5f6488) )	/* Priority (not used) */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_cbusterj = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 ) /* 68000 code */
	  	ROM_LOAD16_BYTE( "fr01-1",   0x00000, 0x20000, CRC(af3c014f) SHA1(a7724c48f73e52b19f3688a413e2ed013e226c6b) )
		ROM_LOAD16_BYTE( "fr00-1",   0x00001, 0x20000, CRC(f666ad52) SHA1(6f7325bc3bb79fd8112df677250c4bae572dfa43) )
	  	ROM_LOAD16_BYTE( "fr03",     0x40000, 0x20000, CRC(02c06118) SHA1(a251f936f80d8a9af033fe6d0d42e1e17ebbbf98) )
	 	ROM_LOAD16_BYTE( "fr02",     0x40001, 0x20000, CRC(b6c34332) SHA1(c1215c72a03b368655e20f4557475a2fc4c46c9e) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* Sound CPU */
		ROM_LOAD( "fu11-.rom",     0x00000, 0x10000, CRC(65f20f10) SHA1(cf914893edd98a0f39bbf7068a469ed7d34bd90e) )
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "fu05-.rom",     0x00000, 0x10000, CRC(8134d412) SHA1(9c70ff6f9f24ec89c0bb4645afdf2a5ca27e9a0c) ) /* Chars */
		ROM_LOAD( "fu06-.rom",     0x10000, 0x10000, CRC(2f914a45) SHA1(bb44ba4779e45ee77ef0006363df91aac1f4559a) )
	
		ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "mab-01",        0x00000, 0x80000, CRC(1080d619) SHA1(68f33a1580d33e4dd0858248c12a0a10ac117249) ) /* Tiles */
	
		ROM_REGION( 0x80000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "mab-00",        0x00000, 0x80000, CRC(660eaabd) SHA1(e3d614e13fdb9af159d9758a869d9dae3dbe14e0) ) /* Tiles */
	
		ROM_REGION( 0x180000,REGION_GFX4, ROMREGION_DISPOSE )
		ROM_LOAD( "mab-02",        0x000000, 0x80000, CRC(58b7231d) SHA1(5b51a2fa42c67f23648be205295184a1fddc00f5) ) /* Sprites */
		/* Space for extra sprites to be copied to (0x20000) */
		ROM_LOAD( "mab-03",        0x0a0000, 0x80000, CRC(76053b9d) SHA1(093cd01a13509701ec9dd1a806132600a5bd1915) )
	 	/* Space for extra sprites to be copied to (0x20000) */
		ROM_LOAD( "fr07",          0x140000, 0x10000, CRC(52c85318) SHA1(74032dac7cb7e7d3028aab4c5f5b0a4e2a7caa03) ) /* Extra sprites */
		ROM_LOAD( "fr08",          0x150000, 0x10000, CRC(ea25fbac) SHA1(d00dce24e94ffc212ab3880c00fcadb7b2116f01) )
		ROM_LOAD( "fr09",          0x160000, 0x10000, CRC(f8363424) SHA1(6a6b143a3474965ef89f75e9d7b15946ae26d0d4) )
		ROM_LOAD( "fr10",          0x170000, 0x10000, CRC(241d5760) SHA1(cd216ecf7e88939b91a6e0f02a23c8b875ac24dc) )
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 )	/* ADPCM samples */
		ROM_LOAD( "fu12-.rom",     0x00000, 0x20000, CRC(2d1d65f2) SHA1(be3d57b9976ddf7ee6d20ee9e78fe826ee411d79) )
	
		ROM_REGION( 0x20000, REGION_SOUND2, 0 )	/* ADPCM samples */
		ROM_LOAD( "fu13-.rom",     0x00000, 0x20000, CRC(b8525622) SHA1(4a6ec5e3f64256b1383bfbab4167cbd2ec11b5c5) )
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 )
		ROM_LOAD( "mb7114h.18e",   0x0000, 0x0100, CRC(3645b70f) SHA1(7d3831867362037892b43efb007e27d3bd5f6488) )	/* Priority (not used) */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_twocrude = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1, 0 ) /* 68000 code */
		ROM_LOAD16_BYTE( "ft01",     0x00000, 0x20000, CRC(08e96489) SHA1(1e75893cc086d6d6b428ca055851b51d0bc367aa) )
		ROM_LOAD16_BYTE( "ft00",     0x00001, 0x20000, CRC(6765c445) SHA1(b2bbb86414eafe32ed66f3f8ab095a2bce3a1a4b) )
		ROM_LOAD16_BYTE( "ft03",     0x40000, 0x20000, CRC(28002c99) SHA1(6397b05a1a237bb17657bee6c8185f61c60c6a2c) )
		ROM_LOAD16_BYTE( "ft02",     0x40001, 0x20000, CRC(37ea0626) SHA1(ec1822eda83829c599cad217b6d5dd34fb970101) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* Sound CPU */
		ROM_LOAD( "fu11-.rom",     0x00000, 0x10000, CRC(65f20f10) SHA1(cf914893edd98a0f39bbf7068a469ed7d34bd90e) )
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "fu05-.rom",     0x00000, 0x10000, CRC(8134d412) SHA1(9c70ff6f9f24ec89c0bb4645afdf2a5ca27e9a0c) ) /* Chars */
		ROM_LOAD( "fu06-.rom",     0x10000, 0x10000, CRC(2f914a45) SHA1(bb44ba4779e45ee77ef0006363df91aac1f4559a) )
	
		ROM_REGION( 0x80000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "mab-01",        0x00000, 0x80000, CRC(1080d619) SHA1(68f33a1580d33e4dd0858248c12a0a10ac117249) ) /* Tiles */
	
		ROM_REGION( 0x80000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "mab-00",        0x00000, 0x80000, CRC(660eaabd) SHA1(e3d614e13fdb9af159d9758a869d9dae3dbe14e0) ) /* Tiles */
	
		ROM_REGION( 0x180000,REGION_GFX4, ROMREGION_DISPOSE )
		ROM_LOAD( "mab-02",        0x000000, 0x80000, CRC(58b7231d) SHA1(5b51a2fa42c67f23648be205295184a1fddc00f5) ) /* Sprites */
		/* Space for extra sprites to be copied to (0x20000) */
		ROM_LOAD( "mab-03",        0x0a0000, 0x80000, CRC(76053b9d) SHA1(093cd01a13509701ec9dd1a806132600a5bd1915) )
	 	/* Space for extra sprites to be copied to (0x20000) */
		ROM_LOAD( "ft07",          0x140000, 0x10000, CRC(e3465c25) SHA1(5369a87847e6f881efc8460e6e8efcf8ff46e87f) )
		ROM_LOAD( "ft08",          0x150000, 0x10000, CRC(c7f1d565) SHA1(d5dc55cf879f7feaff166a6708d60ef0bf31ddf5) )
		ROM_LOAD( "ft09",          0x160000, 0x10000, CRC(6e3657b9) SHA1(7e6a140e33f9bc18e35c255680eebe152a5d8858) )
		ROM_LOAD( "ft10",          0x170000, 0x10000, CRC(cdb83560) SHA1(8b258c4436ccea5a74edff1b6219ab7a5eac0328) )
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 )	/* ADPCM samples */
		ROM_LOAD( "fu12-.rom",     0x00000, 0x20000, CRC(2d1d65f2) SHA1(be3d57b9976ddf7ee6d20ee9e78fe826ee411d79) )
	
		ROM_REGION( 0x20000, REGION_SOUND2, 0 )	/* ADPCM samples */
		ROM_LOAD( "fu13-.rom",     0x00000, 0x20000, CRC(b8525622) SHA1(4a6ec5e3f64256b1383bfbab4167cbd2ec11b5c5) )
	
		ROM_REGION( 0x0100, REGION_PROMS, 0 )
		ROM_LOAD( "mb7114h.18e",   0x0000, 0x0100, CRC(3645b70f) SHA1(7d3831867362037892b43efb007e27d3bd5f6488) )	/* Priority (not used) */
	ROM_END(); }}; 
	
	/******************************************************************************/
	
	public static DriverInitHandlerPtr init_twocrude  = new DriverInitHandlerPtr() { public void handler(){
		unsigned char *RAM = memory_region(REGION_CPU1);
		unsigned char *PTR;
		int i,j;
	
		/* Main cpu decrypt */
		for (i=0x00000; i<0x80000; i+=2) {
	#ifdef LSB_FIRST
			RAM[i+1]=(RAM[i+1] & 0xcf) | ((RAM[i+1] & 0x10) << 1) | ((RAM[i+1] & 0x20) >> 1);
			RAM[i+1]=(RAM[i+1] & 0x5f) | ((RAM[i+1] & 0x20) << 2) | ((RAM[i+1] & 0x80) >> 2);
	
			RAM[i]=(RAM[i] & 0xbd) | ((RAM[i] & 0x2) << 5) | ((RAM[i] & 0x40) >> 5);
			RAM[i]=(RAM[i] & 0xf5) | ((RAM[i] & 0x2) << 2) | ((RAM[i] & 0x8) >> 2);
	#else
			RAM[i]=(RAM[i] & 0xcf) | ((RAM[i] & 0x10) << 1) | ((RAM[i] & 0x20) >> 1);
			RAM[i]=(RAM[i] & 0x5f) | ((RAM[i] & 0x20) << 2) | ((RAM[i] & 0x80) >> 2);
	
			RAM[i+1]=(RAM[i+1] & 0xbd) | ((RAM[i+1] & 0x2) << 5) | ((RAM[i+1] & 0x40) >> 5);
			RAM[i+1]=(RAM[i+1] & 0xf5) | ((RAM[i+1] & 0x2) << 2) | ((RAM[i+1] & 0x8) >> 2);
	#endif
		}
	
		/* Rearrange the 'extra' sprite bank to be in the same format as main sprites */
		RAM = memory_region(REGION_GFX4) + 0x080000;
		PTR = memory_region(REGION_GFX4) + 0x140000;
		for (i=0; i<0x20000; i+=64) {
			for (j=0; j<16; j+=1) { /* Copy 16 lines down */
				RAM[i+      0+j*2]=PTR[i/2+      0+j]; /* Pixels 0-7 for each plane */
				RAM[i+      1+j*2]=PTR[i/2+0x10000+j];
				RAM[i+0xa0000+j*2]=PTR[i/2+0x20000+j];
				RAM[i+0xa0001+j*2]=PTR[i/2+0x30000+j];
			}
	
			for (j=0; j<16; j+=1) { /* Copy 16 lines down */
				RAM[i+   0x20+j*2]=PTR[i/2+   0x10+j]; /* Pixels 8-15 for each plane */
				RAM[i+   0x21+j*2]=PTR[i/2+0x10010+j];
				RAM[i+0xa0020+j*2]=PTR[i/2+0x20010+j];
				RAM[i+0xa0021+j*2]=PTR[i/2+0x30010+j];
			}
		}
	} };
	
	/******************************************************************************/
	
	public static GameDriver driver_cbuster	   = new GameDriver("1990"	,"cbuster"	,"cbuster.java"	,rom_cbuster,null	,machine_driver_twocrude	,input_ports_twocrude	,init_twocrude	,ROT0, "Data East Corporation", "Crude Buster (World FX version)" )
	public static GameDriver driver_cbusterw	   = new GameDriver("1990"	,"cbusterw"	,"cbuster.java"	,rom_cbusterw,driver_cbuster	,machine_driver_twocrude	,input_ports_twocrude	,init_twocrude	,ROT0, "Data East Corporation", "Crude Buster (World FU version)" )
	public static GameDriver driver_cbusterj	   = new GameDriver("1990"	,"cbusterj"	,"cbuster.java"	,rom_cbusterj,driver_cbuster	,machine_driver_twocrude	,input_ports_twocrude	,init_twocrude	,ROT0, "Data East Corporation", "Crude Buster (Japan)" )
	public static GameDriver driver_twocrude	   = new GameDriver("1990"	,"twocrude"	,"cbuster.java"	,rom_twocrude,driver_cbuster	,machine_driver_twocrude	,input_ports_twocrude	,init_twocrude	,ROT0, "Data East USA", "Two Crude (US)" )
}
