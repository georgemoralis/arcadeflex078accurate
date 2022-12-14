/*
Jumping Cross - (c) 1984 SNK
driver by Tomasz Slanina

Based on marvin's maze driver

Todo:
- problems with sprites - strange movement in attract mode,
- $c800-$d7ff - unknown read (related with ^^^ )
- verify dipswitches
- verify colors (is palette banking correct ?)
- unused tileset (almost identical to txt layer tiles ,  few (3?) chars are different)
- cocktail mdoe/screen flipping

Could be bad dump ('final' romset is made of two sets marked as 'bad' )

*/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class jcross
{
	
	data8_t *jcr_textram;
	
	
	
	
	
	static int sound_cpu_busy=0;
	
	data8_t *jcr_sharedram;
	public static ReadHandlerPtr sharedram_r  = new ReadHandlerPtr() { public int handler(int offset)return jcr_sharedram[offset];}
	public static WriteHandlerPtr sharedram_w = new WriteHandlerPtr() {public void handler(int offset, int data)jcr_sharedram[offset]=data;}
	
	static struct namco_interface snkwave_interface =
	{
		24000,
		1,
		8,
		-1
	};
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		2,
		2000000,
		new int[] { 35,35 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	public static WriteHandlerPtr sound_command_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		sound_cpu_busy = 0x20;
		soundlatch_w.handler(0, data);
		cpu_set_irq_line(2, IRQ_LINE_NMI, PULSE_LINE);
	} };
	
	public static ReadHandlerPtr sound_command_r  = new ReadHandlerPtr() { public int handler(int offset){
		sound_cpu_busy = 0;
		return(soundlatch_r(0));
	} };
	
	public static ReadHandlerPtr sound_nmi_ack_r  = new ReadHandlerPtr() { public int handler(int offset){
		cpu_set_nmi_line(2, CLEAR_LINE);
		return 0;
	} };
	
	public static ReadHandlerPtr jcross_port_0_r  = new ReadHandlerPtr() { public int handler(int offset){
		return(input_port_0_r.handler(0) | sound_cpu_busy);
	} };
	
	public static WriteHandlerPtr jcross_vregs0_w = new WriteHandlerPtr() {public void handler(int offset, int data)cross_vregs[0]=data;}
	public static WriteHandlerPtr jcross_vregs1_w = new WriteHandlerPtr() {public void handler(int offset, int data)cross_vregs[1]=data;}
	public static WriteHandlerPtr jcross_vregs2_w = new WriteHandlerPtr() {public void handler(int offset, int data)cross_vregs[2]=data;}
	public static WriteHandlerPtr jcross_vregs3_w = new WriteHandlerPtr() {public void handler(int offset, int data)cross_vregs[3]=data;}
	public static WriteHandlerPtr jcross_vregs4_w = new WriteHandlerPtr() {public void handler(int offset, int data)cross_vregs[4]=data;}
	
	
	public static Memory_ReadAddress readmem_sound[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000,0x87ff,MRA_RAM ),
		new Memory_ReadAddress( 0xa000, 0xa000, sound_command_r ),
		new Memory_ReadAddress( 0xc000, 0xc000, sound_nmi_ack_r ),
			new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_sound[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM, namco_wavedata ),
		new Memory_WriteAddress( 0x8000, 0x87ff,MWA_RAM ),
		new Memory_WriteAddress( 0xe000, 0xe000, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0xe001, 0xe001, AY8910_write_port_0_w ),
		new Memory_WriteAddress( 0xe002, 0xe007, snkwave_w ),
		new Memory_WriteAddress( 0xe008, 0xe008, AY8910_control_port_1_w ),
		new Memory_WriteAddress( 0xe009, 0xe009, AY8910_write_port_1_w ),
	
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_CPUA[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x9fff, MRA_ROM ),
		new Memory_ReadAddress( 0xa000, 0xa000, jcross_port_0_r ),
		new Memory_ReadAddress( 0xa100, 0xa100, input_port_1_r ),
		new Memory_ReadAddress( 0xa200, 0xa200, input_port_2_r ),
		new Memory_ReadAddress( 0xa400, 0xa400, input_port_3_r ),
		new Memory_ReadAddress( 0xa500, 0xa500, input_port_4_r ),
		new Memory_ReadAddress( 0xa700, 0xa700, snk_cpuB_nmi_trigger_r  ),
		new Memory_ReadAddress( 0xd800, 0xdfff, sharedram_r ),
		new Memory_ReadAddress( 0xf000, 0xf3ff, jcross_text_ram_r ),
	  	new Memory_ReadAddress( 0xe000, 0xefff, jcross_background_ram_r ),
		new Memory_ReadAddress( 0xf400, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_CPUA[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x9fff, MWA_ROM ),
		new Memory_WriteAddress( 0xa300, 0xa300, sound_command_w ),
		new Memory_WriteAddress( 0xa600, 0xa600, jcross_palettebank_w ),
		new Memory_WriteAddress( 0xa700, 0xa700, snk_cpuA_nmi_ack_w),
		new Memory_WriteAddress( 0xd300, 0xd300, jcross_vregs0_w),
		new Memory_WriteAddress( 0xd400, 0xd400, jcross_vregs1_w),
		new Memory_WriteAddress( 0xd500, 0xd500, jcross_vregs2_w),
		new Memory_WriteAddress( 0xd600, 0xd600, jcross_vregs3_w),
		new Memory_WriteAddress( 0xd700, 0xd700, jcross_vregs4_w),
	 	new Memory_WriteAddress( 0xd800, 0xdfff, sharedram_w, jcr_sharedram ),
		new Memory_WriteAddress( 0xe000, 0xefff, jcross_background_ram_w, videoram ),
		new Memory_WriteAddress( 0xf000, 0xf3ff, jcross_text_ram_w, jcr_textram),
		new Memory_WriteAddress( 0xf400, 0xffff, MWA_RAM ),
	
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_CPUB[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xa700, 0xa700, snk_cpuA_nmi_trigger_r ),
	  	new Memory_ReadAddress( 0xc000, 0xc7ff, sharedram_r ),
		new Memory_ReadAddress( 0xc800, 0xd7ff, jcross_background_ram_r ), /* unknown ??? */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_CPUB[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0xa700, 0xa700, snk_cpuB_nmi_ack_w  ),
	  	new Memory_WriteAddress( 0xc000, 0xc7ff, sharedram_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort readport_sound[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x0000, 0x0000, MRA_NOP ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	
	static InputPortHandlerPtr input_ports_jcross = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( jcross )
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_COIN3 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW,  IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW,  IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* sound CPU status */
	
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON1 );
	
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0x01, 0x01, "Unknown SW 1-0" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
	
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x04, "3" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x38, 0x38, DEF_STR( "Coinage") );
		/* PORT_DIPSETTING(    0x10,  ); ???? 'insert more coin'*/
		PORT_DIPSETTING(    0x28, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x38, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
	
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Bonus_Life") );/* not verified */
		PORT_DIPSETTING(    0xc0, "20000 60000" );
		PORT_DIPSETTING(    0x80, "40000 90000" );
		PORT_DIPSETTING(    0x40, "50000 120000" );
		PORT_DIPSETTING(    0x00, "None" );
	
		PORT_START(); 	/* DSW2 */
		PORT_DIPNAME( 0x01, 0x00, "Bonus Life Occurence" );* not verified */
		PORT_DIPSETTING(    0x01, "1st, 2nd, then every 2nd" );
		PORT_DIPSETTING(    0x00, "1st and 2nd only" );
		PORT_DIPNAME( 0x06, 0x04, DEF_STR( "Difficulty") ); /* scrolling speed */
		PORT_DIPSETTING(    0x06, "Easy" );
		PORT_DIPSETTING(    0x04, "Normal" );
		PORT_DIPSETTING(    0x02, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x18, 0x10, "Game mode" );
		PORT_DIPSETTING(    0x18, "Demo Sounds Off" );
		PORT_DIPSETTING(    0x10, "Demo Sounds On" );
		PORT_BITX(0,        0x08, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Infinite Lives", IP_JOY_NONE, IP_KEY_NONE );
		PORT_DIPSETTING(    0x00, "Freeze" );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Unknown SW 2-6" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "BG Collisions" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
	INPUT_PORTS_END(); }}; 
	
	
	
	/***************************************************************************
	**
	**	Graphics Layout
	**
	***************************************************************************/
	
	static GfxLayout sprite_layout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,3),
		3,
		new int[] { 0,0x2000*8,0x4000*8 },
		new int[] {
			7,6,5,4,3,2,1,0,15,14,13,12,11,10,9,8
		},
		new int[] {
			0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
			8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16
		},
		256
	);
	
	static GfxLayout tile_layout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,1),
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 4, 0, 12, 8, 20, 16, 28, 24},
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
		256
	);
	
	static GfxDecodeInfo jcross_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, tile_layout,	0x080, 8  ),
		new GfxDecodeInfo( REGION_GFX2, 0, tile_layout,	0x110, 1  ),
		new GfxDecodeInfo( REGION_GFX3, 0, tile_layout,	0x100, 1  ),
		new GfxDecodeInfo( REGION_GFX4, 0, sprite_layout,	0x000, 16 ), /* sprites */
		new GfxDecodeInfo( -1 )
	};
	
	
	/***************************************************************************
	**
	**	Machine Driver
	**
	***************************************************************************/
	
	public static MachineHandlerPtr machine_driver_jcross = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		MDRV_CPU_ADD(Z80, 3360000)
		MDRV_CPU_MEMORY(readmem_CPUA,writemem_CPUA)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 3360000)
		MDRV_CPU_MEMORY(readmem_CPUB,writemem_CPUB)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 4000000)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(readmem_sound,writemem_sound)
		MDRV_CPU_PORTS(readport_sound,0)
		MDRV_CPU_PERIODIC_INT(irq0_line_hold, 244)
	
		MDRV_FRAMES_PER_SECOND(61)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(100)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(512, 512)
		MDRV_VISIBLE_AREA(0, 255,0, 223)
		MDRV_GFXDECODE(jcross_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH((16+2)*16)
		MDRV_VIDEO_START(jcross)
		MDRV_VIDEO_UPDATE(jcross)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
		MDRV_SOUND_ADD(NAMCO, snkwave_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	static RomLoadHandlerPtr rom_jcross = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )
		ROM_LOAD( "jcrossa0.10b",  0x0000, 0x2000, CRC(0e79bbcd) SHA1(7088a8effd30080529b797991e24e9807bf90475) )
		ROM_LOAD( "jcrossa1.12b",  0x2000, 0x2000, CRC(999b2bcc) SHA1(e5d13c9c11a82cedee15777341e6424639ecf2f5) )
		ROM_LOAD( "jcrossa2.13b",  0x4000, 0x2000, CRC(ac89e49c) SHA1(9b9a0eec8ad341ce7af58bffe55f10bec696af62) )
		ROM_LOAD( "jcrossa3.14b",  0x6000, 0x2000, CRC(4fd7848d) SHA1(870aea0b8e027616814df87afd24418fd140f736) )
		ROM_LOAD( "jcrossa4.15b",  0x8000, 0x2000, CRC(8500575d) SHA1(b8751b86508de484f2eb8a6702c63a47ec882036) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )
		ROM_LOAD( "jcrossb0.15a",  0x0000, 0x2000, CRC(77ed51e7) SHA1(56b457846f71f442da6f99889231d4b71d5fcb6c) )
		ROM_LOAD( "jcrossb1.14a",  0x2000, 0x2000, CRC(23cf0f70) SHA1(f258e899f332a026eeb0db92330fd60c478218af) )
		ROM_LOAD( "jcrossb2.13a",  0x4000, 0x2000, CRC(5bed3118) SHA1(f105ca55223a4bfbc8e2d61c365c76cf2153254c) )
		ROM_LOAD( "jcrossb3.12a",  0x6000, 0x2000, CRC(cd75dc95) SHA1(ef03d2b0f66f30fad5132e7b6aee9ec978650b53) )
	
		ROM_REGION( 0x10000, REGION_CPU3, 0 )
		ROM_LOAD( "jcrosss0.f1",   0x0000, 0x2000, CRC(9ae8ea93) SHA1(1d824302305a41bf5c354c36e2e11981d1aa5ea4) )
		ROM_LOAD( "jcrosss1.h2",   0x2000, 0x2000, CRC(83785601) SHA1(cd3d484ef5464090c4b543b1edbbedcc52b15071) )
	
		ROM_REGION( 0x2000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "jcrosss.d2",    0x0000, 0x2000, CRC(3ebb5beb) SHA1(de0a1f0fdb5b08b76dab9fa64d9ae3047c4ff84b) )
	
		ROM_REGION( 0x2000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "jcrossb1.a2",   0x0000, 0x2000, CRC(ea3dfbc9) SHA1(eee56acd1c9dbc6c3ecdee4ffe860273e65cc09b) )
	
		ROM_REGION( 0x2000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "jcrossb4.10a",  0x0000, 0x2000, CRC(08ad93fe) SHA1(04baf2d9735b0d794b114abeced5a6b899958ce7) )
	
		ROM_REGION( 0x6000, REGION_GFX4, ROMREGION_DISPOSE )
		ROM_LOAD( "jcrossf2.j2",    0x0000, 0x2000, CRC(42a12b9d) SHA1(9f2bdb1f84f444442282cf0fc1f7b3c7f9a9bf48) )
		ROM_LOAD( "jcrossf1.k2",    0x2000, 0x2000, CRC(70d219bf) SHA1(9ff9f88221edd141e8204ac810434b4290db7cff) )
		ROM_LOAD( "jcrossf0.l2",  0x4000, 0x2000, CRC(4532509b) SHA1(c99f87e2b06b94d815e6099bccb2aee0edf8c98d) )
	
		ROM_REGION( 0x0c00, REGION_PROMS, 0 )
		ROM_LOAD( "jcrossp2.j7",  0x000, 0x400, CRC(b72a96a5) SHA1(20d40e4b6a2652e61dc3ad0c4afaec04e3c7cf74) )
		ROM_LOAD( "jcrossp1.j8",  0x400, 0x400, CRC(35650448) SHA1(17e4a661ff304c093bb0253efceaf4e9b2498924) )
		ROM_LOAD( "jcrossp0.j9",  0x800, 0x400, CRC(99f54d48) SHA1(9bd20eaa9706d28eaca9f5e195204d89e302272f) )
	ROM_END(); }}; 
	
	public static GameDriver driver_jcross	   = new GameDriver("1984"	,"jcross"	,"jcross.java"	,rom_jcross,null	,machine_driver_jcross	,input_ports_jcross	,null	,ROT270,   "SNK", "Jumping Cross",GAME_NO_COCKTAIL|GAME_IMPERFECT_GRAPHICS)
	
	
}
