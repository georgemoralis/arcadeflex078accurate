/***************************************************************************

Fire Trap memory map

driver by Nicola Salmoria

Z80:
0000-7fff ROM
8000-bfff Banked ROM (4 banks)
c000-cfff RAM
d000-d7ff bg #1 video/color RAM (alternating pages 0x100 long)
d000-dfff bg #2 video/color RAM (alternating pages 0x100 long)
e000-e3ff fg video RAM
e400-e7ff fg color RAM
e800-e97f sprites RAM

memory mapped ports:
read:
f010      IN0
f011      IN1
f012      IN2
f013      DSW0
f014      DSW1
f015      from pin 10 of 8751 controller
f016      from port #1 of 8751 controller

write:
f000      IRQ acknowledge
f001      sound command (also causes NMI on sound CPU)
f002      ROM bank selection
f003      flip screen
f004      NMI disable
f005      to port #2 of 8751 controller (signal on P3.2)
f008-f009 bg #1 x scroll
f00a-f00b bg #1 y scroll
f00c-f00d bg #2 x scroll
f00e-f00f bg #2 y scroll

interrupts:
VBlank triggers NMI.
the 8751 triggers IRQ

6502:
0000-07ff RAM
4000-7fff Banked ROM (2 banks)
8000-ffff ROM

read:
3400      command from the main cpu

write:
1000-1001 YM3526
2000      ADPCM data for the MSM5205 chip
2400      bit 0 = to sound chip MSM5205 (1 = play sample); bit 1 = IRQ enable
2800      ROM bank select

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class firetrap
{
	
	
	
	
	
	
	static int firetrap_irq_enable = 0;
	static int firetrap_nmi_enable;
	
	public static WriteHandlerPtr firetrap_nmi_disable_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		firetrap_nmi_enable=~data & 1;
	} };
	
	public static WriteHandlerPtr firetrap_bankselect_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int bankaddress;
		unsigned char *RAM = memory_region(REGION_CPU1);
	
		bankaddress = 0x10000 + (data & 0x03) * 0x4000;
		cpu_setbank(1,&RAM[bankaddress]);
	} };
	
	public static ReadHandlerPtr firetrap_8751_bootleg_r  = new ReadHandlerPtr() { public int handler(int offset){
		/* Check for coin insertion */
		/* the following only works in the bootleg version, which doesn't have an */
		/* 8751 - the real thing is much more complicated than that. */
		if ((readinputport(2) & 0x70) != 0x70) return 0xff;
		return 0;
	} };
	
	static int i8751_return,i8751_current_command;
	
	public static MachineInitHandlerPtr machine_init_firetrap  = new MachineInitHandlerPtr() { public void handler(){
		i8751_current_command=0;
	} };
	
	public static ReadHandlerPtr firetrap_8751_r  = new ReadHandlerPtr() { public int handler(int offset){
		//logerror("PC:%04x read from 8751\n",activecpu_get_pc());
		return i8751_return;
	} };
	
	public static WriteHandlerPtr firetrap_8751_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		static int i8751_init_ptr=0;
		static const data8_t i8751_init_data[]={
			0xf5,0xd5,0xdd,0x21,0x05,0xc1,0x87,0x5f,0x87,0x83,0x5f,0x16,0x00,0xdd,0x19,0xd1,
			0xf1,0xc9,0xf5,0xd5,0xfd,0x21,0x2f,0xc1,0x87,0x5f,0x16,0x00,0xfd,0x19,0xd1,0xf1,
			0xc9,0xe3,0xd5,0xc5,0xf5,0xdd,0xe5,0xfd,0xe5,0xe9,0xe1,0xfd,0xe1,0xdd,0xe1,0xf1,
			0xc1,0xd1,0xe3,0xc9,0xf5,0xc5,0xe5,0xdd,0xe5,0xc5,0x78,0xe6,0x0f,0x47,0x79,0x48,
			0x06,0x00,0xdd,0x21,0x00,0xd0,0xdd,0x09,0xe6,0x0f,0x6f,0x26,0x00,0x29,0x29,0x29,
			0x29,0xeb,0xdd,0x19,0xc1,0x78,0xe6,0xf0,0x28,0x05,0x11,0x00,0x02,0xdd,0x19,0x79,
			0xe6,0xf0,0x28,0x05,0x11,0x00,0x04,0xdd,0x19,0xdd,0x5e,0x00,0x01,0x00,0x01,0xdd,
			0x09,0xdd,0x56,0x00,0xdd,0xe1,0xe1,0xc1,0xf1,0xc9,0xf5,0x3e,0x01,0x32,0x04,0xf0,
			0xf1,0xc9,0xf5,0x3e,0x00,0x32,0x04,0xf0,0xf1,0xc9,0xf5,0xd5,0xdd,0x21,0x05,0xc1,
			0x87,0x5f,0x87,0x83,0x5f,0x16,0x00,0xdd,0x19,0xd1,0xf1,0xc9,0xf5,0xd5,0xfd,0x21,
			0x2f,0xc1,0x87,0x5f,0x16,0x00,0xfd,0x19,0xd1,0xf1,0xc9,0xe3,0xd5,0xc5,0xf5,0xdd,
			0xe5,0xfd,0xe5,0xe9,0xe1,0xfd,0xe1,0xdd,0xe1,0xf1,0xc1,0xd1,0xe3,0xc9,0xf5,0xc5,
			0xe5,0xdd,0xe5,0xc5,0x78,0xe6,0x0f,0x47,0x79,0x48,0x06,0x00,0xdd,0x21,0x00,0xd0,
			0xdd,0x09,0xe6,0x0f,0x6f,0x26,0x00,0x29,0x29,0x29,0x29,0xeb,0xdd,0x19,0xc1,0x78,
			0xe6,0xf0,0x28,0x05,0x11,0x00,0x02,0xdd,0x19,0x79,0xe6,0xf0,0x28,0x05,0x11,0x00,
			0x04,0xdd,0x19,0xdd,0x5e,0x00,0x01,0x00,0x01,0xdd,0x09,0xdd,0x56,0x00,0xdd,0x00
		};
		static const int i8751_coin_data[]={ 0x00, 0xb7 };
		static const int i8751_36_data[]={ 0x00, 0xbc };
	
		/* End of command - important to note, as coin input is supressed while commands are pending */
		if (data==0x26) {
			i8751_current_command=0;
			i8751_return=0xff; /* This value is XOR'd and must equal 0 */
			cpu_set_irq_line_and_vector(0,0,HOLD_LINE,0xff);
			return;
		}
	
		/* Init sequence command */
		else if (data==0x13) {
			if (!i8751_current_command)
				i8751_init_ptr=0;
			i8751_return=i8751_init_data[i8751_init_ptr++];
		}
	
		/* Used to calculate a jump address when coins are inserted */
		else if (data==0xbd) {
			if (!i8751_current_command)
				i8751_init_ptr=0;
			i8751_return=i8751_coin_data[i8751_init_ptr++];
		}
	
		else if (data==0x36) {
			if (!i8751_current_command)
				i8751_init_ptr=0;
			i8751_return=i8751_36_data[i8751_init_ptr++];
		}
	
		/* Static value commands */
		else if (data==0x14)
			i8751_return=1;
		else if (data==0x02)
			i8751_return=0;
		else if (data==0x72)
			i8751_return=3;
		else if (data==0x69)
			i8751_return=2;
		else if (data==0xcb)
			i8751_return=0;
		else if (data==0x49)
			i8751_return=1;
		else if (data==0x17)
			i8751_return=2;
		else if (data==0x88)
			i8751_return=3;
		else {
			i8751_return=0xff;
			logerror("%04x: Unknown i8751 command %02x!\n",activecpu_get_pc(),data);
		}
	
		/* Signal main cpu task is complete */
		cpu_set_irq_line_and_vector(0,0,HOLD_LINE,0xff);
		i8751_current_command=data;
	} };
	
	public static WriteHandlerPtr firetrap_sound_command_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		soundlatch_w.handler(offset,data);
		cpu_set_irq_line(1,IRQ_LINE_NMI,PULSE_LINE);
	} };
	
	public static WriteHandlerPtr firetrap_sound_2400_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		MSM5205_reset_w(offset,~data & 0x01);
		firetrap_irq_enable = data & 0x02;
	} };
	
	public static WriteHandlerPtr firetrap_sound_bankselect_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		int bankaddress;
		unsigned char *RAM = memory_region(REGION_CPU2);
	
		bankaddress = 0x10000 + (data & 0x01) * 0x4000;
		cpu_setbank(2,&RAM[bankaddress]);
	} };
	
	static int msm5205next;
	
	static void firetrap_adpcm_int (int data)
	{
		static int toggle=0;
	
		MSM5205_data_w (0,msm5205next>>4);
		msm5205next<<=4;
	
		toggle ^= 1;
		if (firetrap_irq_enable && toggle)
			cpu_set_irq_line (1, M6502_IRQ_LINE, HOLD_LINE);
	}
	
	public static WriteHandlerPtr firetrap_adpcm_data_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		msm5205next = data;
	} };
	
	public static WriteHandlerPtr flip_screen_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		flip_screen_set(data);
	} };
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xc000, 0xe97f, MRA_RAM ),
		new Memory_ReadAddress( 0xf010, 0xf010, input_port_0_r ),
		new Memory_ReadAddress( 0xf011, 0xf011, input_port_1_r ),
		new Memory_ReadAddress( 0xf012, 0xf012, input_port_2_r ),
		new Memory_ReadAddress( 0xf013, 0xf013, input_port_3_r ),
		new Memory_ReadAddress( 0xf014, 0xf014, input_port_4_r ),
		new Memory_ReadAddress( 0xf016, 0xf016, firetrap_8751_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xcfff, MWA_RAM ),
		new Memory_WriteAddress( 0xd000, 0xd7ff, firetrap_bg1videoram_w, firetrap_bg1videoram ),
		new Memory_WriteAddress( 0xd800, 0xdfff, firetrap_bg2videoram_w, firetrap_bg2videoram ),
		new Memory_WriteAddress( 0xe000, 0xe7ff, firetrap_fgvideoram_w,  firetrap_fgvideoram ),
		new Memory_WriteAddress( 0xe800, 0xe97f, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0xf000, 0xf000, MWA_NOP ),	/* IRQ acknowledge */
		new Memory_WriteAddress( 0xf001, 0xf001, firetrap_sound_command_w ),
		new Memory_WriteAddress( 0xf002, 0xf002, firetrap_bankselect_w ),
		new Memory_WriteAddress( 0xf003, 0xf003, flip_screen_w ),
		new Memory_WriteAddress( 0xf004, 0xf004, firetrap_nmi_disable_w ),
		new Memory_WriteAddress( 0xf005, 0xf005, firetrap_8751_w ),
		new Memory_WriteAddress( 0xf008, 0xf009, firetrap_bg1_scrollx_w ),
		new Memory_WriteAddress( 0xf00a, 0xf00b, firetrap_bg1_scrolly_w ),
		new Memory_WriteAddress( 0xf00c, 0xf00d, firetrap_bg2_scrollx_w ),
		new Memory_WriteAddress( 0xf00e, 0xf00f, firetrap_bg2_scrolly_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_bootleg[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xc000, 0xe97f, MRA_RAM ),
		new Memory_ReadAddress( 0xf010, 0xf010, input_port_0_r ),
		new Memory_ReadAddress( 0xf011, 0xf011, input_port_1_r ),
		new Memory_ReadAddress( 0xf012, 0xf012, input_port_2_r ),
		new Memory_ReadAddress( 0xf013, 0xf013, input_port_3_r ),
		new Memory_ReadAddress( 0xf014, 0xf014, input_port_4_r ),
		new Memory_ReadAddress( 0xf016, 0xf016, firetrap_8751_bootleg_r ),
		new Memory_ReadAddress( 0xf800, 0xf8ff, MRA_ROM ),	/* extra ROM in the bootleg with unprotection code */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_bootleg[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xcfff, MWA_RAM ),
		new Memory_WriteAddress( 0xd000, 0xd7ff, firetrap_bg1videoram_w, firetrap_bg1videoram ),
		new Memory_WriteAddress( 0xd800, 0xdfff, firetrap_bg2videoram_w, firetrap_bg2videoram ),
		new Memory_WriteAddress( 0xe000, 0xe7ff, firetrap_fgvideoram_w,  firetrap_fgvideoram ),
		new Memory_WriteAddress( 0xe800, 0xe97f, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0xf000, 0xf000, MWA_NOP ),	/* IRQ acknowledge */
		new Memory_WriteAddress( 0xf001, 0xf001, firetrap_sound_command_w ),
		new Memory_WriteAddress( 0xf002, 0xf002, firetrap_bankselect_w ),
		new Memory_WriteAddress( 0xf003, 0xf003, flip_screen_w ),
		new Memory_WriteAddress( 0xf004, 0xf004, firetrap_nmi_disable_w ),
		new Memory_WriteAddress( 0xf005, 0xf005, MWA_NOP ),
		new Memory_WriteAddress( 0xf008, 0xf009, firetrap_bg1_scrollx_w ),
		new Memory_WriteAddress( 0xf00a, 0xf00b, firetrap_bg1_scrolly_w ),
		new Memory_WriteAddress( 0xf00c, 0xf00d, firetrap_bg2_scrollx_w ),
		new Memory_WriteAddress( 0xf00e, 0xf00f, firetrap_bg2_scrolly_w ),
		new Memory_WriteAddress( 0xf800, 0xf8ff, MWA_ROM ),	/* extra ROM in the bootleg with unprotection code */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x07ff, MRA_RAM ),
		new Memory_ReadAddress( 0x3400, 0x3400, soundlatch_r ),
		new Memory_ReadAddress( 0x4000, 0x7fff, MRA_BANK2 ),
		new Memory_ReadAddress( 0x8000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x07ff, MWA_RAM ),
		new Memory_WriteAddress( 0x1000, 0x1000, YM3526_control_port_0_w ),
		new Memory_WriteAddress( 0x1001, 0x1001, YM3526_write_port_0_w ),
		new Memory_WriteAddress( 0x2000, 0x2000, firetrap_adpcm_data_w ),	/* ADPCM data for the MSM5205 chip */
		new Memory_WriteAddress( 0x2400, 0x2400, firetrap_sound_2400_w ),
		new Memory_WriteAddress( 0x2800, 0x2800, firetrap_sound_bankselect_w ),
		new Memory_WriteAddress( 0x4000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortHandlerPtr input_ports_firetrap = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( firetrap )
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_UP | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_DOWN | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_LEFT | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_RIGHT | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_UP | IPF_4WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_DOWN | IPF_4WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_LEFT | IPF_4WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_RIGHT | IPF_4WAY );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_UP | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_DOWN | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_LEFT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_UP | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_DOWN | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_LEFT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_RIGHT | IPF_4WAY | IPF_COCKTAIL );
	
		PORT_START();       /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_VBLANK );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Coin_A") );
	//	PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0x01, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0x02, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x18, 0x18, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x02, "Easy" );
		PORT_DIPSETTING(    0x03, "Normal" );
		PORT_DIPSETTING(    0x01, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x0c, "3" );
		PORT_DIPSETTING(    0x08, "4" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x30, "50000 70000" );
		PORT_DIPSETTING(    0x20, "60000 80000" );
		PORT_DIPSETTING(    0x10, "80000 100000" );
		PORT_DIPSETTING(    0x00, "50000" );
		PORT_DIPNAME( 0x40, 0x40, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Yes") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START();       /* Connected to i8751 directly */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE1 );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_firetpbl = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( firetpbl )
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_UP | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_DOWN | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_LEFT | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_RIGHT | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_UP | IPF_4WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_DOWN | IPF_4WAY );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_LEFT | IPF_4WAY );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_RIGHT | IPF_4WAY );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_UP | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_DOWN | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_LEFT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_UP | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_DOWN | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_LEFT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_RIGHT | IPF_4WAY | IPF_COCKTAIL );
	
		PORT_START();       /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_COIN3 );/* bootleg only */
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );/* bootleg only */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );/* bootleg only */
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_VBLANK );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Coin_A") );
	//	PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0x01, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(    0x02, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x18, 0x18, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x02, "Easy" );
		PORT_DIPSETTING(    0x03, "Normal" );
		PORT_DIPSETTING(    0x01, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x0c, "3" );
		PORT_DIPSETTING(    0x08, "4" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x30, "50000 70000" );
		PORT_DIPSETTING(    0x20, "60000 80000" );
		PORT_DIPSETTING(    0x10, "80000 100000" );
		PORT_DIPSETTING(    0x00, "50000" );
		PORT_DIPNAME( 0x40, 0x40, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Yes") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	INPUT_PORTS_END(); }}; 
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,2),
		2,
		new int[] { 0, 4 },
		new int[] { 3, 2, 1, 0, RGN_FRAC(1,2)+3, RGN_FRAC(1,2)+2, RGN_FRAC(1,2)+1, RGN_FRAC(1,2)+0 },
		new int[] { 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		8*8
	);
	static GfxLayout tilelayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,4),
		4,
		new int[] { 0, 4, RGN_FRAC(1,2)+0, RGN_FRAC(1,2)+4 },
		new int[] { 3, 2, 1, 0, RGN_FRAC(1,4)+3, RGN_FRAC(1,4)+2, RGN_FRAC(1,4)+1, RGN_FRAC(1,4)+0,
				16*8+3, 16*8+2, 16*8+1, 16*8+0, RGN_FRAC(1,4)+16*8+3, RGN_FRAC(1,4)+16*8+2, RGN_FRAC(1,4)+16*8+1, RGN_FRAC(1,4)+16*8+0 },
		new int[] { 15*8, 14*8, 13*8, 12*8, 11*8, 10*8, 9*8, 8*8,
				7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		32*8
	);
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,4),
		4,
		new int[] { RGN_FRAC(0,4), RGN_FRAC(1,4), RGN_FRAC(2,4), RGN_FRAC(3,4) },
		new int[] { 7, 6, 5, 4, 3, 2, 1, 0,
				16*8+7, 16*8+6, 16*8+5, 16*8+4, 16*8+3, 16*8+2, 16*8+1, 16*8+0 },
		new int[] { 15*8, 14*8, 13*8, 12*8, 11*8, 10*8, 9*8, 8*8,
				7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		32*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,   0x00, 16 ),	/* colors 0x00-0x3f */
		new GfxDecodeInfo( REGION_GFX2, 0, tilelayout,   0x80,  4 ),	/* colors 0x80-0xbf */
		new GfxDecodeInfo( REGION_GFX3, 0, tilelayout,   0xc0,  4 ),	/* colors 0xc0-0xff */
		new GfxDecodeInfo( REGION_GFX4, 0, spritelayout, 0x40,  4 ),	/* colors 0x40-0x7f */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static struct YM3526interface ym3526_interface =
	{
		1,			/* 1 chip */
		3000000,	/* 3.000000 MHz ? */
		{ 100 }		/* volume */
	};
	
	static struct MSM5205interface msm5205_interface =
	{
		1,					/* 1 chip             */
		384000,				/* 384KHz ?           */
		{ firetrap_adpcm_int },/* interrupt function */
		{ MSM5205_S48_4B},	/* 8KHz ?             */
		{ 30 }
	};
	
	public static InterruptHandlerPtr firetrap = new InterruptHandlerPtr() {public void handler(){
		static int latch=0;
		static int coin_command_pending=0;
	
		/* Check for coin IRQ */
		if (cpu_getiloops()) {
			if ((readinputport(5) & 0x7) != 0x7 && !latch) {
				coin_command_pending=~readinputport(5);
				latch=1;
			}
			if ((readinputport(5) & 0x7) == 0x7)
				latch=0;
	
			/* Make sure coin IRQ's aren't generated when another command is pending, the main cpu
				definitely doesn't expect them as it locks out the coin routine */
			if (coin_command_pending && !i8751_current_command) {
				i8751_return=coin_command_pending;
				cpu_set_irq_line_and_vector(0,0,HOLD_LINE,0xff);
				coin_command_pending=0;
			}
		}
	
		if (firetrap_nmi_enable && !cpu_getiloops())
			cpu_set_irq_line (0, IRQ_LINE_NMI, PULSE_LINE);
	} };
	
	public static InterruptHandlerPtr bootleg = new InterruptHandlerPtr() {public void handler(){
		if (firetrap_nmi_enable)
			cpu_set_irq_line (0, IRQ_LINE_NMI, PULSE_LINE);
	} };
	
	public static MachineHandlerPtr machine_driver_firetrap = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 6000000)	/* 6 MHz */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(firetrap,2)
	
		MDRV_CPU_ADD(M6502,3072000/2)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 1.536 MHz? */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
								/* IRQs are caused by the ADPCM chip */
								/* NMIs are caused by the main CPU */
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_MACHINE_INIT(firetrap)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 1*8, 31*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_PALETTE_INIT(firetrap)
		MDRV_VIDEO_START(firetrap)
		MDRV_VIDEO_UPDATE(firetrap)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM3526, ym3526_interface)
		MDRV_SOUND_ADD(MSM5205, msm5205_interface)
	MACHINE_DRIVER_END();
 }
};
	
	public static MachineHandlerPtr machine_driver_firetpbl = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 6000000)	/* 6 MHz */
		MDRV_CPU_MEMORY(readmem_bootleg,writemem_bootleg)
		MDRV_CPU_VBLANK_INT(bootleg,1)
	
		MDRV_CPU_ADD(M6502,3072000/2)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 1.536 MHz? */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
								/* IRQs are caused by the ADPCM chip */
								/* NMIs are caused by the main CPU */
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 1*8, 31*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_PALETTE_INIT(firetrap)
		MDRV_VIDEO_START(firetrap)
		MDRV_VIDEO_UPDATE(firetrap)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM3526, ym3526_interface)
		MDRV_SOUND_ADD(MSM5205, msm5205_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadHandlerPtr rom_firetrap = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x20000, REGION_CPU1, 0 )	/* 64k for code + 64k for banked ROMs */
		ROM_LOAD( "di02.bin",     0x00000, 0x8000, CRC(3d1e4bf7) SHA1(ee903b469619f49edb1727fb545c9a6085f50746) )
		ROM_LOAD( "di01.bin",     0x10000, 0x8000, CRC(9bbae38b) SHA1(dc1d3ed5da71bfb104fd54fc70c56833f31d281f) )
		ROM_LOAD( "di00.bin",     0x18000, 0x8000, CRC(d0dad7de) SHA1(8783ebf6ddfef32f6036913d403f76c1545b813d) )
	
		ROM_REGION( 0x18000, REGION_CPU2, 0 )	/* 64k for the sound CPU + 32k for banked ROMs */
		ROM_LOAD( "di17.bin",     0x08000, 0x8000, CRC(8605f6b9) SHA1(4fba88f34afd91d2cbc578b3b70f5399b8844390) )
		ROM_LOAD( "di18.bin",     0x10000, 0x8000, CRC(49508c93) SHA1(3812b0b1a33a1506d2896d2b676ed6aabb29dac0) )
	
		/* there's also a protected 8751 microcontroller with ROM onboard */
	
		ROM_REGION( 0x02000, REGION_GFX1, ROMREGION_DISPOSE )	/* characters */
		ROM_LOAD( "di03.bin",     0x00000, 0x2000, CRC(46721930) SHA1(a605fe993166e95c1602a35b548649ceae77bff2) )
	
		ROM_REGION( 0x20000, REGION_GFX2, ROMREGION_DISPOSE )	/* tiles */
		ROM_LOAD( "di06.bin",     0x00000, 0x2000, CRC(441d9154) SHA1(340804e82d4aba8e9fcdd08cce0cfecefd2f77a9) )
		ROM_CONTINUE(             0x08000, 0x2000 )
		ROM_CONTINUE(             0x02000, 0x2000 )
		ROM_CONTINUE(             0x0a000, 0x2000 )
		ROM_LOAD( "di04.bin",     0x04000, 0x2000, CRC(8e6e7eec) SHA1(9cff147702620987346449e2f83ef9b2efef7798) )
		ROM_CONTINUE(             0x0c000, 0x2000 )
		ROM_CONTINUE(             0x06000, 0x2000 )
		ROM_CONTINUE(             0x0e000, 0x2000 )
		ROM_LOAD( "di07.bin",     0x10000, 0x2000, CRC(ef0a7e23) SHA1(7c67ac27e6bde0f4943e8bed9898e730ae7ddd75) )
		ROM_CONTINUE(             0x18000, 0x2000 )
		ROM_CONTINUE(             0x12000, 0x2000 )
		ROM_CONTINUE(             0x1a000, 0x2000 )
		ROM_LOAD( "di05.bin",     0x14000, 0x2000, CRC(ec080082) SHA1(3b034496bfa2aba9ed58ceba670d0364a9c2211d) )
		ROM_CONTINUE(             0x1c000, 0x2000 )
		ROM_CONTINUE(             0x16000, 0x2000 )
		ROM_CONTINUE(             0x1e000, 0x2000 )
	
		ROM_REGION( 0x20000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "di09.bin",     0x00000, 0x2000, CRC(d11e28e8) SHA1(3e91764f74d551e0984bac92daeab4e094e8dc13) )
		ROM_CONTINUE(             0x08000, 0x2000 )
		ROM_CONTINUE(             0x02000, 0x2000 )
		ROM_CONTINUE(             0x0a000, 0x2000 )
		ROM_LOAD( "di08.bin",     0x04000, 0x2000, CRC(c32a21d8) SHA1(01898abf24aa40b13939afed96c990f430eb3bf1) )
		ROM_CONTINUE(             0x0c000, 0x2000 )
		ROM_CONTINUE(             0x06000, 0x2000 )
		ROM_CONTINUE(             0x0e000, 0x2000 )
		ROM_LOAD( "di11.bin",     0x10000, 0x2000, CRC(6424d5c3) SHA1(9ad6cfe6effca795709f90839a338f2a9148128f) )
		ROM_CONTINUE(             0x18000, 0x2000 )
		ROM_CONTINUE(             0x12000, 0x2000 )
		ROM_CONTINUE(             0x1a000, 0x2000 )
		ROM_LOAD( "di10.bin",     0x14000, 0x2000, CRC(9b89300a) SHA1(5575daa226188cb1ea7d7a23f4966252bfb748e0) )
		ROM_CONTINUE(             0x1c000, 0x2000 )
		ROM_CONTINUE(             0x16000, 0x2000 )
		ROM_CONTINUE(             0x1e000, 0x2000 )
	
		ROM_REGION( 0x20000, REGION_GFX4, ROMREGION_DISPOSE )	/* sprites */
		ROM_LOAD( "di16.bin",     0x00000, 0x8000, CRC(0de055d7) SHA1(ef763237c317545520c659f438b572b11c342d5a) )
		ROM_LOAD( "di13.bin",     0x08000, 0x8000, CRC(869219da) SHA1(9ab2439d6d1c62fce24c4f78ac7887f34c86cd75) )
		ROM_LOAD( "di14.bin",     0x10000, 0x8000, CRC(6b65812e) SHA1(209e07b2fced6b033c6d5398a998374588a35f46) )
		ROM_LOAD( "di15.bin",     0x18000, 0x8000, CRC(3e27f77d) SHA1(9ceccb1f56a8d0e05f6dea45d102690a1370624e) )
	
		ROM_REGION( 0x0200, REGION_PROMS, 0 )
		ROM_LOAD( "firetrap.3b",  0x0000,  0x0100, CRC(8bb45337) SHA1(deaf6ea53eb3955230db1fdcb870079758a0c996) ) /* palette red and green component */
		ROM_LOAD( "firetrap.4b",  0x0100,  0x0100, CRC(d5abfc64) SHA1(6c808c1d6087804214dc29d35280f42382c40b18) ) /* palette blue component */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_firetpbl = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x28000, REGION_CPU1, 0 )	/* 64k for code + 96k for banked ROMs */
		ROM_LOAD( "ft0d.bin",     0x00000, 0x8000, CRC(793ef849) SHA1(5a2c587370733d43484ba0a38a357260cdde8357) )
		ROM_LOAD( "ft0a.bin",     0x08000, 0x8000, CRC(613313ee) SHA1(54e386b2b1faada3441e3e0bb7822a63eab36930) )	/* unprotection code */
		ROM_LOAD( "ft0c.bin",     0x10000, 0x8000, CRC(5c8a0562) SHA1(856766851faa4353445d944b7705e348fd1379e4) )
		ROM_LOAD( "ft0b.bin",     0x18000, 0x8000, CRC(f2412fe8) SHA1(28a9143e36c31fe34f40888dc848aed3d572d801) )
	
		ROM_REGION( 0x18000, REGION_CPU2, 0 )	/* 64k for the sound CPU + 32k for banked ROMs */
		ROM_LOAD( "di17.bin",     0x08000, 0x8000, CRC(8605f6b9) SHA1(4fba88f34afd91d2cbc578b3b70f5399b8844390) )
		ROM_LOAD( "di18.bin",     0x10000, 0x8000, CRC(49508c93) SHA1(3812b0b1a33a1506d2896d2b676ed6aabb29dac0) )
	
		ROM_REGION( 0x02000, REGION_GFX1, ROMREGION_DISPOSE )	/* characters */
		ROM_LOAD( "ft0e.bin",     0x00000, 0x2000, CRC(a584fc16) SHA1(6ac3692a14cb7c70799c23f8f6726fa5be1ac0d8) )
	
		ROM_REGION( 0x20000, REGION_GFX2, ROMREGION_DISPOSE )	/* tiles */
		ROM_LOAD( "di06.bin",     0x00000, 0x2000, CRC(441d9154) SHA1(340804e82d4aba8e9fcdd08cce0cfecefd2f77a9) )
		ROM_CONTINUE(             0x08000, 0x2000 )
		ROM_CONTINUE(             0x02000, 0x2000 )
		ROM_CONTINUE(             0x0a000, 0x2000 )
		ROM_LOAD( "di04.bin",     0x04000, 0x2000, CRC(8e6e7eec) SHA1(9cff147702620987346449e2f83ef9b2efef7798) )
		ROM_CONTINUE(             0x0c000, 0x2000 )
		ROM_CONTINUE(             0x06000, 0x2000 )
		ROM_CONTINUE(             0x0e000, 0x2000 )
		ROM_LOAD( "di07.bin",     0x10000, 0x2000, CRC(ef0a7e23) SHA1(7c67ac27e6bde0f4943e8bed9898e730ae7ddd75) )
		ROM_CONTINUE(             0x18000, 0x2000 )
		ROM_CONTINUE(             0x12000, 0x2000 )
		ROM_CONTINUE(             0x1a000, 0x2000 )
		ROM_LOAD( "di05.bin",     0x14000, 0x2000, CRC(ec080082) SHA1(3b034496bfa2aba9ed58ceba670d0364a9c2211d) )
		ROM_CONTINUE(             0x1c000, 0x2000 )
		ROM_CONTINUE(             0x16000, 0x2000 )
		ROM_CONTINUE(             0x1e000, 0x2000 )
	
		ROM_REGION( 0x20000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "di09.bin",     0x00000, 0x2000, CRC(d11e28e8) SHA1(3e91764f74d551e0984bac92daeab4e094e8dc13) )
		ROM_CONTINUE(             0x08000, 0x2000 )
		ROM_CONTINUE(             0x02000, 0x2000 )
		ROM_CONTINUE(             0x0a000, 0x2000 )
		ROM_LOAD( "di08.bin",     0x04000, 0x2000, CRC(c32a21d8) SHA1(01898abf24aa40b13939afed96c990f430eb3bf1) )
		ROM_CONTINUE(             0x0c000, 0x2000 )
		ROM_CONTINUE(             0x06000, 0x2000 )
		ROM_CONTINUE(             0x0e000, 0x2000 )
		ROM_LOAD( "di11.bin",     0x10000, 0x2000, CRC(6424d5c3) SHA1(9ad6cfe6effca795709f90839a338f2a9148128f) )
		ROM_CONTINUE(             0x18000, 0x2000 )
		ROM_CONTINUE(             0x12000, 0x2000 )
		ROM_CONTINUE(             0x1a000, 0x2000 )
		ROM_LOAD( "di10.bin",     0x14000, 0x2000, CRC(9b89300a) SHA1(5575daa226188cb1ea7d7a23f4966252bfb748e0) )
		ROM_CONTINUE(             0x1c000, 0x2000 )
		ROM_CONTINUE(             0x16000, 0x2000 )
		ROM_CONTINUE(             0x1e000, 0x2000 )
	
		ROM_REGION( 0x20000, REGION_GFX4, ROMREGION_DISPOSE )	/* sprites */
		ROM_LOAD( "di16.bin",     0x00000, 0x8000, CRC(0de055d7) SHA1(ef763237c317545520c659f438b572b11c342d5a) )
		ROM_LOAD( "di13.bin",     0x08000, 0x8000, CRC(869219da) SHA1(9ab2439d6d1c62fce24c4f78ac7887f34c86cd75) )
		ROM_LOAD( "di14.bin",     0x10000, 0x8000, CRC(6b65812e) SHA1(209e07b2fced6b033c6d5398a998374588a35f46) )
		ROM_LOAD( "di15.bin",     0x18000, 0x8000, CRC(3e27f77d) SHA1(9ceccb1f56a8d0e05f6dea45d102690a1370624e) )
	
		ROM_REGION( 0x0200, REGION_PROMS, 0 )
		ROM_LOAD( "firetrap.3b",  0x0000,  0x0100, CRC(8bb45337) SHA1(deaf6ea53eb3955230db1fdcb870079758a0c996) ) /* palette red and green component */
		ROM_LOAD( "firetrap.4b",  0x0100,  0x0100, CRC(d5abfc64) SHA1(6c808c1d6087804214dc29d35280f42382c40b18) ) /* palette blue component */
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_firetrap	   = new GameDriver("1986"	,"firetrap"	,"firetrap.java"	,rom_firetrap,null	,machine_driver_firetrap	,input_ports_firetrap	,null	,ROT90, "Data East USA", "Fire Trap (US)" )
	public static GameDriver driver_firetpbl	   = new GameDriver("1986"	,"firetpbl"	,"firetrap.java"	,rom_firetpbl,driver_firetrap	,machine_driver_firetpbl	,input_ports_firetpbl	,null	,ROT90, "bootleg", "Fire Trap (Japan bootleg)" )
}
