/*****************************************************************************

	Irem M90/M97 system games:

	Hasamu							1991 M90
	Bomberman						1992 M90
	Bomberman World / Atomic Punk	1992 M97
	Quiz F-1 1,2finish				1992 M97
	Risky Challenge / Gussun Oyoyo	1993 M97
	Shisensho II					1993 M97


	Uses M72 sound hardware.

	Emulation by Bryan McPhail, mish@tendril.co.uk, thanks to Chris Hardy!

Notes:

- Samples are not played in bbmanw/atompunk.

- Not sure about the clock speeds. In hasamu and quizf1 service mode, the
  selection moves too fast with the clock set at 16 MHz. It's still fast at
  8 MHz, but at least it's usable.

*****************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class m90
{
	
	static int bankaddress;
	
	
	
	/***************************************************************************/
	
	static void set_m90_bank(void)
	{
		data8_t *rom = memory_region(REGION_USER1);
	
		if (!rom)
			usrintf_showmessage("bankswitch with no banked ROM!");
		else
			cpu_setbank(1,rom + bankaddress);
	}
	
	/***************************************************************************/
	
	public static WriteHandlerPtr m90_coincounter_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (offset==0)
		{
			coin_counter_w(0,data & 0x01);
			coin_counter_w(1,data & 0x02);
	
			if (data&0xfe) logerror("Coin counter %02x\n",data);
		}
	} };
	
	public static WriteHandlerPtr quizf1_bankswitch_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (offset == 0)
		{
			bankaddress = 0x10000 * (data & 0x0f);
			set_m90_bank();
		}
	} };
	
	/***************************************************************************/
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x00000, 0x7ffff, MRA_ROM ),
		new Memory_ReadAddress( 0x80000, 0x8ffff, MRA_BANK1 ),	/* Quiz F1 only */
		new Memory_ReadAddress( 0xa0000, 0xa3fff, MRA_RAM ),
		new Memory_ReadAddress( 0xd0000, 0xdffff, MRA_RAM ),
		new Memory_ReadAddress( 0xe0000, 0xe03ff, paletteram_r ),
		new Memory_ReadAddress( 0xffff0, 0xfffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x00000, 0x7ffff, MWA_ROM ),
		new Memory_WriteAddress( 0x80000, 0x8ffff, MWA_ROM ),	/* Quiz F1 only */
		new Memory_WriteAddress( 0xa0000, 0xa3fff, MWA_RAM ),
		new Memory_WriteAddress( 0xd0000, 0xdffff, m90_video_w, m90_video_data ),
		new Memory_WriteAddress( 0xe0000, 0xe03ff, paletteram_xBBBBBGGGGGRRRRR_w, paletteram ),
		new Memory_WriteAddress( 0xffff0, 0xfffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress bootleg_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x00000, 0x3ffff, MRA_ROM ),
		new Memory_ReadAddress( 0x60000, 0x60fff, MRA_RAM ),
		new Memory_ReadAddress( 0xa0000, 0xa3fff, MRA_RAM ),
		new Memory_ReadAddress( 0xd0000, 0xdffff, MRA_RAM ),
		new Memory_ReadAddress( 0xe0000, 0xe03ff, paletteram_r ),
		new Memory_ReadAddress( 0xffff0, 0xfffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress bootleg_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x00000, 0x3ffff, MWA_ROM ),
		new Memory_WriteAddress( 0x6000e, 0x60fff, MWA_RAM, spriteram ),
		new Memory_WriteAddress( 0xa0000, 0xa3fff, MWA_RAM ),
	//	new Memory_WriteAddress( 0xd0000, 0xdffff, m90_bootleg_video_w, m90_video_data ),
		new Memory_WriteAddress( 0xe0000, 0xe03ff, paletteram_xBBBBBGGGGGRRRRR_w, paletteram ),
		new Memory_WriteAddress( 0xffff0, 0xfffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, input_port_0_r ), /* Player 1 */
		new IO_ReadPort( 0x01, 0x01, input_port_1_r ), /* Player 2 */
		new IO_ReadPort( 0x02, 0x02, input_port_2_r ), /* Coins */
		new IO_ReadPort( 0x03, 0x03, MRA_NOP ),		/* Unused?  High byte of above */
		new IO_ReadPort( 0x04, 0x04, input_port_3_r ), /* Dip 1 */
		new IO_ReadPort( 0x05, 0x05, input_port_4_r ), /* Dip 2 */
		new IO_ReadPort( 0x06, 0x06, input_port_5_r ), /* Player 3 */
		new IO_ReadPort( 0x07, 0x07, input_port_6_r ), /* Player 4 */
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x01, m72_sound_command_w ),
		new IO_WritePort( 0x02, 0x03, m90_coincounter_w ),
		new IO_WritePort( 0x04, 0x05, quizf1_bankswitch_w ),
		new IO_WritePort( 0x80, 0x8f, m90_video_control_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	/*****************************************************************************/
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x01, 0x01, YM2151_status_port_0_r ),
		new IO_ReadPort( 0x80, 0x80, soundlatch_r ),
		new IO_ReadPort( 0x84, 0x84, m72_sample_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, YM2151_register_port_0_w ),
		new IO_WritePort( 0x01, 0x01, YM2151_data_port_0_w ),
		new IO_WritePort( 0x80, 0x81, rtype2_sample_addr_w ),
		new IO_WritePort( 0x82, 0x82, m72_sample_w ),
		new IO_WritePort( 0x83, 0x83, m72_sound_irq_ack_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort bbmanw_sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x41, 0x41, YM2151_status_port_0_r ),
		new IO_ReadPort( 0x42, 0x42, soundlatch_r ),
	//	new IO_ReadPort( 0x41, 0x41, m72_sample_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort bbmanw_sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x40, 0x40, YM2151_register_port_0_w ),
		new IO_WritePort( 0x41, 0x41, YM2151_data_port_0_w ),
		new IO_WritePort( 0x42, 0x42, m72_sound_irq_ack_w ),
	//	new IO_WritePort( 0x40, 0x41, rtype2_sample_addr_w ),
	//	new IO_WritePort( 0x42, 0x42, m72_sample_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	/*****************************************************************************/
	
	
	static InputPortHandlerPtr input_ports_hasamu = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( hasamu )
		IREM_JOYSTICK_1_2(1)
		IREM_JOYSTICK_1_2(2)
		IREM_COINS
	
		PORT_START(); 	/* Dip switch bank 1 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
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
		PORT_DIPNAME( 0x20, 0x20, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START(); 	/* Dip switch bank 2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "Coin Mode" );
		PORT_DIPSETTING(    0x08, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		/* Coin Mode 1 */
		IREM_COIN_MODE_1_NEW
		/* Coin Mode 2, not supported yet */
	//	IREM_COIN_MODE_2
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_dynablst = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( dynablst )
		IREM_JOYSTICK_1_2(1)
		IREM_JOYSTICK_1_2(2)
		IREM_COINS
	
		PORT_START(); 	/* Dip switch bank 1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x08, "Easy" );
		PORT_DIPSETTING(    0x0c, "Medium" );
		PORT_DIPSETTING(    0x04, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );	/* Manual says "NOT USE" */
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START(); 	/* Dip switch bank 2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x06, 0x06, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x04, "2 Player Upright" );
		PORT_DIPSETTING(    0x06, "4 Player Upright A" );/* Seperate Coin Slots */
		PORT_DIPSETTING(    0x02, "4 Player Upright B" );/* Shared Coin Slots */
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );  /* This setting shows screen with offset, no cocktail support :-( */
		PORT_DIPNAME( 0x08, 0x08, "Coin Mode" );
		PORT_DIPSETTING(    0x08, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		/* Coin Mode 1 */
		IREM_COIN_MODE_1_NEW
		/* Coin Mode 2, not supported yet */
	//	IREM_COIN_MODE_2
	
		IREM_JOYSTICK_3_4(3)
		IREM_JOYSTICK_3_4(4)
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_bombrman = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( bombrman ) /* Does not appear to support 4 players or cocktail mode */
		IREM_JOYSTICK_1_2(1)
		IREM_JOYSTICK_1_2(2)
		IREM_COINS
	
		PORT_START(); 	/* Dip switch bank 1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x08, "Easy" );
		PORT_DIPSETTING(    0x0c, "Medium" );
		PORT_DIPSETTING(    0x04, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );	/* Manual says "NOT USE" */
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START(); 	/* Dip switch bank 2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );	/* Manual says "NOT USE" */
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );	/* Manual says "NOT USE" */
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "Coin Mode" );
		PORT_DIPSETTING(    0x08, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		/* Coin Mode 1 */
		IREM_COIN_MODE_1_NEW
		/* Coin Mode 2, not supported yet */
	//	IREM_COIN_MODE_2
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_bbmanw = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( bbmanw )
		IREM_JOYSTICK_1_2(1)
		IREM_JOYSTICK_1_2(2)
		IREM_COINS
	
		PORT_START(); 	/* Dip switch bank 1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x08, "Easy" );
		PORT_DIPSETTING(    0x0c, "Medium" );
		PORT_DIPSETTING(    0x04, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START(); 	/* Dip switch bank 2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x06, 0x06, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x04, "2 Player" );
		PORT_DIPSETTING(    0x06, "4 Player Seprate Coins" );	/* Each player has a seperate Coin Slot */
		PORT_DIPSETTING(    0x02, "4 Player Shared Coins" );	/* All 4 players Share coin 1&2 */
		PORT_DIPSETTING(    0x00, "4 Player 1&2 3&4 Share Coins" );/* Players 1&2 share coin 1&2, Players 3&4 share coin 3&4 */
		PORT_DIPNAME( 0x08, 0x08, "Coin Mode" );
		PORT_DIPSETTING(    0x08, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		/* Coin Mode 1 */
		IREM_COIN_MODE_1_NEW
		/* Coin Mode 2, not supported yet */
	//	IREM_COIN_MODE_2
	
		IREM_JOYSTICK_3_4(3)
		IREM_JOYSTICK_3_4(4)
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_quizf1 = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( quizf1 )
		PORT_START(); 
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON4 | IPF_PLAYER1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_START(); 
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON4 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		IREM_COINS
	
		PORT_START(); 	/* Dip switch bank 1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") ); /* Probably difficulty */
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, "Input Device" );/* input related (joystick/buttons select?) */
		PORT_DIPSETTING(    0x20, "Joystick" );
		PORT_DIPSETTING(    0x00, "Buttons" );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START(); 	/* Dip switch bank 2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "Coin Mode" );
		PORT_DIPSETTING(    0x08, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		/* Coin Mode 1 */
		IREM_COIN_MODE_1_NEW
		/* Coin Mode 2, not supported yet */
	//	IREM_COIN_MODE_2
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_m97 = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( m97 )
		IREM_JOYSTICK_1_2(1)
		IREM_JOYSTICK_1_2(2)
		IREM_COINS
	
		PORT_START(); 	/* Dip switch bank 1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") ); /* Probably difficulty */
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
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x80, IP_ACTIVE_LOW );
	
		PORT_START(); 	/* Dip switch bank 2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "Coin Mode" );
		PORT_DIPSETTING(    0x08, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		/* Coin Mode 1 */
		IREM_COIN_MODE_1_NEW
		/* Coin Mode 2, not supported yet */
	//	IREM_COIN_MODE_2
	INPUT_PORTS_END(); }}; 
	
	/*****************************************************************************/
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,4),
		4,
		new int[] { RGN_FRAC(3,4), RGN_FRAC(2,4), RGN_FRAC(1,4), RGN_FRAC(0,4) },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,4),
		4,
		new int[] { RGN_FRAC(3,4), RGN_FRAC(2,4), RGN_FRAC(1,4), RGN_FRAC(0,4) },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
			16*8+0, 16*8+1, 16*8+2, 16*8+3, 16*8+4, 16*8+5, 16*8+6, 16*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
		32*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,     0, 16 ),
		new GfxDecodeInfo( REGION_GFX1, 0, spritelayout, 256, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	/*****************************************************************************/
	
	static struct YM2151interface ym2151_interface =
	{
		1,			/* 1 chip */
		3579545,	/* 3.579545 MHz */
		{ YM3012_VOL(90,MIXER_PAN_LEFT,90,MIXER_PAN_RIGHT) },
		{ m72_ym2151_irq_handler },
		{ 0 }
	};
	
	static DACinterface dac_interface = new DACinterface
	(
		1,	/* 1 channel */
		new int[] { 60 }
	);
	
	public static InterruptHandlerPtr m90_interrupt = new InterruptHandlerPtr() {public void handler(){
		cpu_set_irq_line_and_vector(0, 0, HOLD_LINE, 0x60/4);
	} };
	
	
	
	public static MachineHandlerPtr machine_driver_m90 = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(V30,32000000/4)	/* 8 MHz ??????? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(m90_interrupt,1)
	
		MDRV_CPU_ADD(Z80, 3579545)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 3.579545 MHz */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(sound_readport,sound_writeport)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,128)	/* clocked by V1? (Vigilante) */
									/* IRQs are generated by main Z80 and YM2151 */
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(m72_sound)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(64*8, 64*8)
		MDRV_VISIBLE_AREA(6*8, 54*8-1, 17*8, 47*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(512)
	
		MDRV_VIDEO_START(m90)
		MDRV_VIDEO_UPDATE(m90)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END();
 }
};
	
	public static MachineHandlerPtr machine_driver_quizf1 = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		MDRV_IMPORT_FROM( m90 )
		MDRV_VISIBLE_AREA(6*8, 54*8-1, 17*8-8, 47*8-1+8)
	
	MACHINE_DRIVER_END();
 }
};
	
	
	public static MachineHandlerPtr machine_driver_bombrman = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(V30,32000000/4)	/* 8 MHz ??????? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(m90_interrupt,1)
	
		MDRV_CPU_ADD(Z80, 3579545)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 3.579545 MHz */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(sound_readport,sound_writeport)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,128)	/* clocked by V1? (Vigilante) */
									/* IRQs are generated by main Z80 and YM2151 */
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
		MDRV_MACHINE_INIT(m72_sound)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(64*8, 64*8)
		MDRV_VISIBLE_AREA(10*8, 50*8-1, 17*8, 47*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(512)
	
		MDRV_VIDEO_START(m90)
		MDRV_VIDEO_UPDATE(m90)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	public static MachineHandlerPtr machine_driver_bbmanw = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(V30,32000000/4)	/* 8 MHz ??????? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(m90_interrupt,1)
	
		MDRV_CPU_ADD(Z80, 3579545)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 3.579545 MHz */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(bbmanw_sound_readport,bbmanw_sound_writeport)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,128)	/* clocked by V1? (Vigilante) */
									/* IRQs are generated by main Z80 and YM2151 */
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
		MDRV_MACHINE_INIT(m72_sound)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(64*8, 64*8)
		MDRV_VISIBLE_AREA(10*8, 50*8-1, 17*8, 47*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(512)
	
		MDRV_VIDEO_START(m90)
		MDRV_VIDEO_UPDATE(m90)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	public static MachineHandlerPtr machine_driver_bootleg = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(V30,32000000/4)	/* 16 MHz */
		MDRV_CPU_MEMORY(bootleg_readmem,bootleg_writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(m90_interrupt,1)
	
		MDRV_CPU_ADD(Z80, 3579545)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 3.579545 MHz */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(sound_readport,sound_writeport)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,128)	/* clocked by V1? (Vigilante) */
									/* IRQs are generated by main Z80 and YM2151 */
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
		MDRV_MACHINE_INIT(m72_sound)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(320, 240)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(512)
	
		MDRV_VIDEO_START(m90)
		MDRV_VIDEO_UPDATE(m90)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END();
 }
};
	
	/***************************************************************************/
	
	#define CODE_SIZE 0x100000
	
	static RomLoadHandlerPtr rom_hasamu = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 )
		ROM_LOAD16_BYTE( "hasc-p1.bin",  0x00001, 0x20000, CRC(53df9834) SHA1(2e7e38157a497e3def69c4abcae5803f71a098da) )
		ROM_LOAD16_BYTE( "hasc-p0.bin",  0x00000, 0x20000, CRC(dff0ba6e) SHA1(83e20b3ae10b57c1e58d3d44bfca2ffd5f142056) )
		ROM_COPY( REGION_CPU1, 0x3fff0,  0xffff0, 0x10 )	/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "hasc-sp.bin",    0x0000, 0x10000, CRC(259b1687) SHA1(39c3a89b1d0f5fec2a94a3048cc4639fe96820e2) )
	
		ROM_REGION( 0x80000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "hasc-c0.bin",    0x000000, 0x20000, CRC(dd5a2174) SHA1(c28499419f961d126a838dd1390db74c1475ee02) )
		ROM_LOAD( "hasc-c1.bin",    0x020000, 0x20000, CRC(76b8217c) SHA1(8b21562875d856a1ce4863f325d049090f5716ae) )
		ROM_LOAD( "hasc-c2.bin",    0x040000, 0x20000, CRC(d90f9a68) SHA1(c9eab3e87dd5d3eb88461be493d88f5482c9e257) )
		ROM_LOAD( "hasc-c3.bin",    0x060000, 0x20000, CRC(6cfe0d39) SHA1(104feeacbbc86b168c41cd37fc5797781d9b5a0f) )
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 )	/* samples */
		/* No samples */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_bombrman = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 )
		ROM_LOAD16_BYTE( "bbm-p1.bin",   0x00001, 0x20000, CRC(982bd166) SHA1(ed67393ec319127616bff5fa3b7f84e8ac8e1d93) )
		ROM_LOAD16_BYTE( "bbm-p0.bin",   0x00000, 0x20000, CRC(0a20afcc) SHA1(a42b7458938300b0c84c820c1ea627aed9080f1b) )
		ROM_COPY( REGION_CPU1, 0x3fff0,  0xffff0, 0x10 )	/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "bbm-sp.bin",    0x0000, 0x10000, CRC(251090cd) SHA1(9245072c1afbfa3e4a1d1549942765d58bd78ed3) )
	
		ROM_REGION( 0x100000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "bbm-c0.bin",    0x000000, 0x40000, CRC(695d2019) SHA1(3537e9fb0e7dc13d6113b4af71cba3c73392335a) )
		ROM_LOAD( "bbm-c1.bin",    0x040000, 0x40000, CRC(4c7c8bbc) SHA1(31ab5557d96c4184a9c02ed1c309f3070d148e25) )
		ROM_LOAD( "bbm-c2.bin",    0x080000, 0x40000, CRC(0700d406) SHA1(0d43a31a726b0de0004beef41307de2508106b69) )
		ROM_LOAD( "bbm-c3.bin",    0x0c0000, 0x40000, CRC(3c3613af) SHA1(f9554a73e95102333e449f6e81f2bb817ec00881) )
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 )	/* samples */
		ROM_LOAD( "bbm-v0.bin",    0x0000, 0x20000, CRC(0fa803fe) SHA1(d2ac1e624de38bed385442ceae09a76f203fa084) )
	ROM_END(); }}; 
	
	
	static RomLoadHandlerPtr rom_dynablst = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 )
		ROM_LOAD16_BYTE( "bbm-cp1e.d10",   0x00001, 0x20000, CRC(27667681) SHA1(7d5f762026ea01817a65ea13b4b5793640e3e8fd) )
		ROM_LOAD16_BYTE( "bbm-cp0e.bin",   0x00000, 0x20000, CRC(95db7a67) SHA1(1a224d73615a60530cbcc54fdbb526e8d5a6c555) )
		ROM_COPY( REGION_CPU1, 0x3fff0,  0xffff0, 0x10 )	/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "bbm-sp.bin",    0x0000, 0x10000, CRC(251090cd) SHA1(9245072c1afbfa3e4a1d1549942765d58bd78ed3) )
	
		ROM_REGION( 0x100000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "bbm-c0.bin",    0x000000, 0x40000, CRC(695d2019) SHA1(3537e9fb0e7dc13d6113b4af71cba3c73392335a) )
		ROM_LOAD( "bbm-c1.bin",    0x040000, 0x40000, CRC(4c7c8bbc) SHA1(31ab5557d96c4184a9c02ed1c309f3070d148e25) )
		ROM_LOAD( "bbm-c2.bin",    0x080000, 0x40000, CRC(0700d406) SHA1(0d43a31a726b0de0004beef41307de2508106b69) )
		ROM_LOAD( "bbm-c3.bin",    0x0c0000, 0x40000, CRC(3c3613af) SHA1(f9554a73e95102333e449f6e81f2bb817ec00881) )
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 )	/* samples */
		ROM_LOAD( "bbm-v0.bin",    0x0000, 0x20000, CRC(0fa803fe) SHA1(d2ac1e624de38bed385442ceae09a76f203fa084) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_dynablsb = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x100000, REGION_CPU1, 0 )
		ROM_LOAD16_BYTE( "db2-26.bin",   0x00001, 0x20000, CRC(a78c72f8) SHA1(e3ed1bce0278bada6357b5d0823511fa0241f3cd) )
		ROM_LOAD16_BYTE( "db3-25.bin",   0x00000, 0x20000, CRC(bf3137c3) SHA1(64bbca4b3a509b552ee8a19b3b50fe6638fd90e2) )
		ROM_COPY( REGION_CPU1, 0x3fff0,  0xffff0, 0x10 )	/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "db1-17.bin",    0x0000, 0x10000, CRC(e693c32f) SHA1(b6f228d26318718eedae765de9479706a3e4c38d) )
	
		ROM_REGION( 0x100000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "bbm-c0.bin",    0x000000, 0x40000, CRC(695d2019) SHA1(3537e9fb0e7dc13d6113b4af71cba3c73392335a) )
		ROM_LOAD( "bbm-c1.bin",    0x040000, 0x40000, CRC(4c7c8bbc) SHA1(31ab5557d96c4184a9c02ed1c309f3070d148e25) )
		ROM_LOAD( "bbm-c2.bin",    0x080000, 0x40000, CRC(0700d406) SHA1(0d43a31a726b0de0004beef41307de2508106b69) )
		ROM_LOAD( "bbm-c3.bin",    0x0c0000, 0x40000, CRC(3c3613af) SHA1(f9554a73e95102333e449f6e81f2bb817ec00881) )
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 )	/* samples */
		/* Does this have a sample rom? */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_bbmanw = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 )
		ROM_LOAD16_BYTE( "db_h0-b.rom",  0x00001, 0x40000, CRC(567d3709) SHA1(1447fc68798589a8757ee2d133d053b80f052113) )
		ROM_LOAD16_BYTE( "db_l0-b.rom",  0x00000, 0x40000, CRC(e762c22b) SHA1(b389a65adf1348e6529a992d9b68178d7503238e) )
		ROM_COPY( REGION_CPU1, 0x7fff0,  0xffff0, 0x10 )	/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "db_sp.rom",    0x0000, 0x10000, CRC(6bc1689e) SHA1(099c275632965e19eb6131863f69d2afa9916e90) )
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "bbm2_c0.bin",  0x000000, 0x40000, CRC(e7ce058a) SHA1(f2336718ecbce4771f27abcdc4d28fe91c702a9e) )
		ROM_LOAD( "bbm2_c1.bin",  0x080000, 0x40000, CRC(636a78a9) SHA1(98562ea056e5bd36c1a094ae6f267367236d166f) )
		ROM_LOAD( "bbm2_c2.bin",  0x100000, 0x40000, CRC(9ac2142f) SHA1(744fe1acae2fcba0051c303b644081546b4aed9e) )
		ROM_LOAD( "bbm2_c3.bin",  0x180000, 0x40000, CRC(47af1750) SHA1(dce176a6ca95852208b6eba7fb88a0d96467c34b) )
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 )
		ROM_LOAD( "db_w04m.rom",    0x0000, 0x20000, CRC(4ad889ed) SHA1(b685892a2348f17f89c6d6ce91216f6cf1e33751) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_bbmanwj = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 )
		ROM_LOAD16_BYTE( "bbm2_h0.bin",  0x00001, 0x40000, CRC(e1407b91) SHA1(6c94afc6b1d2a469295890ee5dd9d9d5a02ae5c4) )
		ROM_LOAD16_BYTE( "bbm2_l0.bin",  0x00000, 0x40000, CRC(20873b49) SHA1(30ae595f7961cd56f2506608ae76973b2d0e73ca) )
		ROM_COPY( REGION_CPU1, 0x7fff0,  0xffff0, 0x10 )	/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "bbm2sp-b.bin", 0x0000, 0x10000, CRC(b8d8108c) SHA1(ef4fb46d843819c273db2083754eb312f5abd44e) )
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "bbm2_c0.bin",  0x000000, 0x40000, CRC(e7ce058a) SHA1(f2336718ecbce4771f27abcdc4d28fe91c702a9e) )
		ROM_LOAD( "bbm2_c1.bin",  0x080000, 0x40000, CRC(636a78a9) SHA1(98562ea056e5bd36c1a094ae6f267367236d166f) )
		ROM_LOAD( "bbm2_c2.bin",  0x100000, 0x40000, CRC(9ac2142f) SHA1(744fe1acae2fcba0051c303b644081546b4aed9e) )
		ROM_LOAD( "bbm2_c3.bin",  0x180000, 0x40000, CRC(47af1750) SHA1(dce176a6ca95852208b6eba7fb88a0d96467c34b) )
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 )	/* samples */
		ROM_LOAD( "bbm2_vo.bin",  0x0000, 0x20000, CRC(0ae655ff) SHA1(78752182662fd8f5b55bbbc2787c9f2b04096ea1) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_atompunk = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 )
		ROM_LOAD16_BYTE( "bm2-ho-a.9f",  0x00001, 0x40000, CRC(7d858682) SHA1(03580e2903becb69766023585c6ecffbb8e0b9c5) )
		ROM_LOAD16_BYTE( "bm2-lo-a.9k",  0x00000, 0x40000, CRC(c7568031) SHA1(ff4d0809260a088f530098a0173eec16fa6396f1) )
		ROM_COPY( REGION_CPU1, 0x7fff0,  0xffff0, 0x10 )	/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "db_sp.rom",             0x0000, 0x10000, CRC(6bc1689e) SHA1(099c275632965e19eb6131863f69d2afa9916e90) )
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "bbm2_c0.bin",  0x000000, 0x40000, CRC(e7ce058a) SHA1(f2336718ecbce4771f27abcdc4d28fe91c702a9e) )
		ROM_LOAD( "bbm2_c1.bin",  0x080000, 0x40000, CRC(636a78a9) SHA1(98562ea056e5bd36c1a094ae6f267367236d166f) )
		ROM_LOAD( "bbm2_c2.bin",  0x100000, 0x40000, CRC(9ac2142f) SHA1(744fe1acae2fcba0051c303b644081546b4aed9e) )
		ROM_LOAD( "bbm2_c3.bin",  0x180000, 0x40000, CRC(47af1750) SHA1(dce176a6ca95852208b6eba7fb88a0d96467c34b) )
	
		ROM_REGION( 0x20000, REGION_SOUND1, 0 )	/* samples */
		ROM_LOAD( "db_w04m.rom",           0x0000, 0x20000, CRC(4ad889ed) SHA1(b685892a2348f17f89c6d6ce91216f6cf1e33751) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_quizf1 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 )
		ROM_LOAD16_BYTE( "qf1-h0-.77",   0x000001, 0x40000, CRC(280e3049) SHA1(3b1f303d803f844fd260ed93e4d12a72876e4dbe) )
		ROM_LOAD16_BYTE( "qf1-l0-.79",   0x000000, 0x40000, CRC(94588a6f) SHA1(ee912739c7719fc2b099da0c63f7473eedcfc718) )
		ROM_COPY( REGION_CPU1, 0x7fff0,  0xffff0, 0x10 )	/* start vector */
	
		ROM_REGION( 0x100000, REGION_USER1, 0 )
		ROM_LOAD16_BYTE( "qf1-h1-.78",   0x000001, 0x80000, CRC(c6c2eb2b) SHA1(83de08b0c72da8c3e4786063802d83cb1015032a) )	/* banked at 80000-8FFFF */
		ROM_LOAD16_BYTE( "qf1-l1-.80",   0x000000, 0x80000, CRC(3132c144) SHA1(de3ae35cdfbb1231cab343142ac700df00f9b77a) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "qf1-sp-.33",   0x0000, 0x10000, CRC(0664fa9f) SHA1(db003beb4f8461bf4411efa8df9f700770fb153b) )
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "qf1-c0-.81",   0x000000, 0x80000, CRC(c26b521e) SHA1(eb5d33a21d1f82e361e0c0945abcf42562c32f03) )
		ROM_LOAD( "qf1-c1-.82",   0x080000, 0x80000, CRC(db9d7394) SHA1(06b41288c41df8ae0cafb53e77b519d0419cf1d9) )
		ROM_LOAD( "qf1-c2-.83",   0x100000, 0x80000, CRC(0b1460ae) SHA1(c6394e6bb2a4e3722c20d9f291cb6ba7aad5766d) )
		ROM_LOAD( "qf1-c3-.84",   0x180000, 0x80000, CRC(2d32ff37) SHA1(f414f6bad1ffc4396fd757155e602bdefdc99408) )
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 )	/* samples */
		ROM_LOAD( "qf1-v0-.30",   0x0000, 0x40000, CRC(b8d16e7c) SHA1(28a20afb171dc68848f9fe793f53571d4c7502dd) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_riskchal = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 )
		ROM_LOAD16_BYTE( "rc_h0.rom",    0x00001, 0x40000, CRC(4c9b5344) SHA1(61e26950a672c6404e2386acdd098536b61b9933) )
		ROM_LOAD16_BYTE( "rc_l0.rom",    0x00000, 0x40000, CRC(0455895a) SHA1(1072b8d280f7ccc48cd8fbd81323e1f8c8d0db95) )
		ROM_COPY( REGION_CPU1, 0x7fff0,  0xffff0, 0x10 )	/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "rc_sp.rom",    0x0000, 0x10000, CRC(bb80094e) SHA1(1c62e702c395b7ebb666a79af1912b270d5f95aa) )
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "rc_c0.rom",    0x000000, 0x80000, CRC(84d0b907) SHA1(a686ccd67d068e5e4ba41bb8b73fdc1cad8eb5ee) )
		ROM_LOAD( "rc_c1.rom",    0x080000, 0x80000, CRC(cb3784ef) SHA1(51b8cdc35c8f3b452939ab6023a15f1c7e1a4423) )
		ROM_LOAD( "rc_c2.rom",    0x100000, 0x80000, CRC(687164d7) SHA1(0f0beb0a85ae5ae4434d1e45a27bbe67f5ee378a) )
		ROM_LOAD( "rc_c3.rom",    0x180000, 0x80000, CRC(c86be6af) SHA1(c8a66b8b38a62e3eebb4a0e65a85e20f91182097) )
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 )	/* samples */
		ROM_LOAD( "rc_v0.rom",    0x0000, 0x40000, CRC(cddac360) SHA1(a3b18325991473c6d54b778a02bed86180aad37c) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_gussun = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 )
		ROM_LOAD16_BYTE( "l4_h0.rom",    0x00001, 0x40000, CRC(9d585e61) SHA1(e108a9dc2dc1b75c1439271a2391f943c3a53fe1) )
		ROM_LOAD16_BYTE( "l4_l0.rom",    0x00000, 0x40000, CRC(c7b4c519) SHA1(44887ccf54f5e507d2db4f09a7c2b7b9ea217058) )
		ROM_COPY( REGION_CPU1, 0x7fff0,  0xffff0, 0x10 )	/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "rc_sp.rom",    0x0000, 0x10000, CRC(bb80094e) SHA1(1c62e702c395b7ebb666a79af1912b270d5f95aa) )
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "rc_c0.rom",    0x000000, 0x80000, CRC(84d0b907) SHA1(a686ccd67d068e5e4ba41bb8b73fdc1cad8eb5ee) )
		ROM_LOAD( "rc_c1.rom",    0x080000, 0x80000, CRC(cb3784ef) SHA1(51b8cdc35c8f3b452939ab6023a15f1c7e1a4423) )
		ROM_LOAD( "rc_c2.rom",    0x100000, 0x80000, CRC(687164d7) SHA1(0f0beb0a85ae5ae4434d1e45a27bbe67f5ee378a) )
		ROM_LOAD( "rc_c3.rom",    0x180000, 0x80000, CRC(c86be6af) SHA1(c8a66b8b38a62e3eebb4a0e65a85e20f91182097) )
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 )	/* samples */
		ROM_LOAD( "rc_v0.rom",    0x0000, 0x40000, CRC(cddac360) SHA1(a3b18325991473c6d54b778a02bed86180aad37c) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_shisen2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE * 2, REGION_CPU1, 0 )
		ROM_LOAD16_BYTE( "sis2-ho-.rom", 0x00001, 0x40000, CRC(6fae0aea) SHA1(7ebecbfdb17e15b8c0ebd293cd42a618c596782e) )
		ROM_LOAD16_BYTE( "sis2-lo-.rom", 0x00000, 0x40000, CRC(2af25182) SHA1(ec6dcc3913e1b7e7a3958b78610e83f51c404e07) )
		ROM_COPY( REGION_CPU1, 0x7fff0,  0xffff0, 0x10 )	/* start vector */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "sis2-sp-.rom", 0x0000, 0x10000, CRC(6fc0ff3a) SHA1(2b8c648c1fb5d516552fc260b8f18ffd56bbe062) )
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "ic81.rom",     0x000000, 0x80000, CRC(5a7cb88f) SHA1(ce3befcd956b803655b261c2ece911f444aa3a13) )
		ROM_LOAD( "ic82.rom",     0x080000, 0x80000, CRC(54a7852c) SHA1(887e7543f09d00323ce1986e72c5613dde1dc6cc) )
		ROM_LOAD( "ic83.rom",     0x100000, 0x80000, CRC(2bd65dc6) SHA1(b50dec707ea5a71972df0a8dc47141d75e8f874e) )
		ROM_LOAD( "ic84.rom",     0x180000, 0x80000, CRC(876d5fdb) SHA1(723c58268be60f4973e914df238b264708d3f1e3) )
	ROM_END(); }}; 
	
	
	
	public static DriverInitHandlerPtr init_hasamu  = new DriverInitHandlerPtr() { public void handler(){
		irem_cpu_decrypt(0,gunforce_decryption_table);
	} };
	
	public static DriverInitHandlerPtr init_bombrman  = new DriverInitHandlerPtr() { public void handler(){
		irem_cpu_decrypt(0,bomberman_decryption_table);
	} };
	
	/* Bomberman World executes encrypted code from RAM! */
	public static WriteHandlerPtr bbmanw_ram_write = new WriteHandlerPtr() {public void handler(int offset, int data){
		unsigned char *RAM = memory_region(REGION_CPU1);
		RAM[0x0a0c00+offset]=data;
		RAM[0x1a0c00+offset]=dynablaster_decryption_table[data];
	} };
	
	public static DriverInitHandlerPtr init_bbmanw  = new DriverInitHandlerPtr() { public void handler(){
		irem_cpu_decrypt(0,dynablaster_decryption_table);
	
		install_mem_write_handler(0, 0xa0c00, 0xa0cff, bbmanw_ram_write);
	} };
	
	public static DriverInitHandlerPtr init_quizf1  = new DriverInitHandlerPtr() { public void handler(){
		irem_cpu_decrypt(0,lethalth_decryption_table);
	
		bankaddress = 0;
		set_m90_bank();
	
		state_save_register_int("main", 0, "bankaddress", &bankaddress);
		state_save_register_func_postload(set_m90_bank);
	} };
	
	public static DriverInitHandlerPtr init_riskchal  = new DriverInitHandlerPtr() { public void handler(){
		irem_cpu_decrypt(0,gussun_decryption_table);
	} };
	
	public static DriverInitHandlerPtr init_shisen2  = new DriverInitHandlerPtr() { public void handler(){
		irem_cpu_decrypt(0,shisen2_decryption_table);
	} };
	
	
	
	public static GameDriver driver_hasamu	   = new GameDriver("1991"	,"hasamu"	,"m90.java"	,rom_hasamu,null	,machine_driver_m90	,input_ports_hasamu	,init_hasamu	,ROT0, "Irem", "Hasamu (Japan)", GAME_NO_COCKTAIL )
	public static GameDriver driver_bbmanw	   = new GameDriver("1991"	,"bbmanw"	,"m90.java"	,rom_bbmanw,null	,machine_driver_bbmanw	,input_ports_bbmanw	,init_bbmanw	,ROT0, "Irem", "Bomber Man World (World)", GAME_IMPERFECT_SOUND | GAME_NO_COCKTAIL )
	public static GameDriver driver_bbmanwj	   = new GameDriver("1991"	,"bbmanwj"	,"m90.java"	,rom_bbmanwj,driver_bbmanw	,machine_driver_bombrman	,input_ports_bbmanw	,init_bbmanw	,ROT0, "Irem", "Bomber Man World (Japan)", GAME_NO_COCKTAIL )
	public static GameDriver driver_dynablst	   = new GameDriver("1992"	,"dynablst"	,"m90.java"	,rom_dynablst,null	,machine_driver_bombrman	,input_ports_dynablst	,init_bombrman	,ROT0, "Irem (licensed from Hudson Soft)", "Dynablaster (World)", GAME_NO_COCKTAIL )
	public static GameDriver driver_bombrman	   = new GameDriver("1992"	,"bombrman"	,"m90.java"	,rom_bombrman,driver_dynablst	,machine_driver_bombrman	,input_ports_bombrman	,init_bombrman	,ROT0, "Irem (licensed from Hudson Soft)", "Bomberman (Japan)", GAME_NO_COCKTAIL )
	public static GameDriver driver_dynablsb	   = new GameDriver("1992"	,"dynablsb"	,"m90.java"	,rom_dynablsb,driver_dynablst	,machine_driver_bootleg	,input_ports_bombrman	,null	,ROT0, "bootleg", "Dynablaster (bootleg)", GAME_NOT_WORKING | GAME_NO_COCKTAIL )
	public static GameDriver driver_atompunk	   = new GameDriver("1992"	,"atompunk"	,"m90.java"	,rom_atompunk,driver_bbmanw	,machine_driver_bbmanw	,input_ports_bbmanw	,init_bbmanw	,ROT0, "Irem America", "New Atomic Punk - Global Quest (US)", GAME_IMPERFECT_SOUND | GAME_NO_COCKTAIL )
	public static GameDriver driver_quizf1	   = new GameDriver("1992"	,"quizf1"	,"m90.java"	,rom_quizf1,null	,machine_driver_quizf1	,input_ports_quizf1	,init_quizf1	,ROT0, "Irem", "Quiz F-1 1,2finish", GAME_UNEMULATED_PROTECTION | GAME_IMPERFECT_GRAPHICS | GAME_NO_COCKTAIL )
	public static GameDriver driver_riskchal	   = new GameDriver("1993"	,"riskchal"	,"m90.java"	,rom_riskchal,null	,machine_driver_m90	,input_ports_m97	,init_riskchal	,ROT0, "Irem", "Risky Challenge", GAME_NOT_WORKING | GAME_NO_COCKTAIL )
	public static GameDriver driver_gussun	   = new GameDriver("1993"	,"gussun"	,"m90.java"	,rom_gussun,driver_riskchal	,machine_driver_m90	,input_ports_m97	,init_riskchal	,ROT0, "Irem", "Gussun Oyoyo (Japan)", GAME_NOT_WORKING | GAME_NO_COCKTAIL )
	public static GameDriver driver_shisen2	   = new GameDriver("1993"	,"shisen2"	,"m90.java"	,rom_shisen2,null	,machine_driver_m90	,input_ports_m97	,init_shisen2	,ROT0, "Tamtex", "Shisensho II", GAME_NOT_WORKING | GAME_NO_COCKTAIL )
}
