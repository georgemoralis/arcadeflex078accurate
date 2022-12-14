/***************************************************************************

Based on drivers from Juno First emulator by Chris Hardy (chrish@kcbbs.gen.nz)

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class hyperspt
{
	
	
	
	
	
	
	
	/* these routines lurk in sndhrdw/trackfld.c */
	
	
	public static WriteHandlerPtr hyperspt_coin_counter_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		coin_counter_w(offset,data);
	} };
	
	/* handle fake button for speed cheat */
	public static ReadHandlerPtr konami_IN1_r  = new ReadHandlerPtr() { public int handler(int offset){
		int res;
		static int cheat = 0;
		static int bits[] = { 0xee, 0xff, 0xbb, 0xaa };
	
		res = readinputport(1);
	
		if ((res & 0x80) == 0)
		{
			res |= 0x55;
			res &= bits[cheat];
			cheat = (cheat+1)%4;
		}
		return res;
	} };
	
	
	
	/*
	 Track'n'Field has 1k of battery backed RAM which can be erased by setting a dipswitch
	*/
	static UINT8 *nvram;
	static size_t nvram_size;
	static int we_flipped_the_switch;
	
	public static NVRAMHandlerPtr nvram_handler_hyperspt  = new NVRAMHandlerPtr() { public void handler(mame_file file, int read_or_write){
		if (read_or_write)
		{
			mame_fwrite(file,nvram,nvram_size);
	
			if (we_flipped_the_switch)
			{
				struct InputPort *in;
	
	
				/* find the dip switch which resets the high score table, and set it */
				/* back to off. */
				in = Machine->input_ports;
	
				while (in->type != IPT_END)
				{
					if (in->name != NULL && in->name != IP_NAME_DEFAULT &&
							strcmp(in->name,"World Records") == 0)
					{
						if (in->default_value == 0)
							in->default_value = in->mask;
						break;
					}
	
					in++;
				}
	
				we_flipped_the_switch = 0;
			}
		}
		else
		{
			if (file)
			{
				mame_fread(file,nvram,nvram_size);
				we_flipped_the_switch = 0;
			}
			else
			{
				struct InputPort *in;
	
	
				/* find the dip switch which resets the high score table, and set it on */
				in = Machine->input_ports;
	
				while (in->type != IPT_END)
				{
					if (in->name != NULL && in->name != IP_NAME_DEFAULT &&
							strcmp(in->name,"World Records") == 0)
					{
						if (in->default_value == in->mask)
						{
							in->default_value = 0;
							we_flipped_the_switch = 1;
						}
						break;
					}
	
					in++;
				}
			}
		}
	} };
	
	
	
	public static Memory_ReadAddress hyperspt_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x1000, 0x10ff, MRA_RAM ),
		new Memory_ReadAddress( 0x1600, 0x1600, input_port_4_r ), /* DIP 2 */
		new Memory_ReadAddress( 0x1680, 0x1680, input_port_0_r ), /* IO Coin */
	//	new Memory_ReadAddress( 0x1681, 0x1681, input_port_1_r ), /* P1 IO */
		new Memory_ReadAddress( 0x1681, 0x1681, konami_IN1_r ), /* P1 IO and handle fake button for cheating */
		new Memory_ReadAddress( 0x1682, 0x1682, input_port_2_r ), /* P2 IO */
		new Memory_ReadAddress( 0x1683, 0x1683, input_port_3_r ), /* DIP 1 */
		new Memory_ReadAddress( 0x2000, 0x3fff, MRA_RAM ),
		new Memory_ReadAddress( 0x4000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress roadf_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x1000, 0x10ff, MRA_RAM ),
		new Memory_ReadAddress( 0x1600, 0x1600, input_port_4_r ), /* DIP 2 */
		new Memory_ReadAddress( 0x1680, 0x1680, input_port_0_r ), /* IO Coin */
		new Memory_ReadAddress( 0x1681, 0x1681, input_port_1_r ), /* P1 IO */
		new Memory_ReadAddress( 0x1682, 0x1682, input_port_2_r ), /* P2 IO */
		new Memory_ReadAddress( 0x1683, 0x1683, input_port_3_r ), /* DIP 1 */
		new Memory_ReadAddress( 0x2000, 0x3fff, MRA_RAM ),
		new Memory_ReadAddress( 0x4000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x1000, 0x10bf, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0x10C0, 0x10ff, MWA_RAM, hyperspt_scroll ),	/* Scroll amount */
		new Memory_WriteAddress( 0x1400, 0x1400, watchdog_reset_w ),
		new Memory_WriteAddress( 0x1480, 0x1480, hyperspt_flipscreen_w ),
		new Memory_WriteAddress( 0x1481, 0x1481, konami_sh_irqtrigger_w ),  /* cause interrupt on audio CPU */
		new Memory_WriteAddress( 0x1483, 0x1484, hyperspt_coin_counter_w ),
		new Memory_WriteAddress( 0x1487, 0x1487, interrupt_enable_w ),  /* Interrupt enable */
		new Memory_WriteAddress( 0x1500, 0x1500, soundlatch_w ),
		new Memory_WriteAddress( 0x2000, 0x27ff, hyperspt_videoram_w, videoram ),
		new Memory_WriteAddress( 0x2800, 0x2fff, hyperspt_colorram_w, colorram ),
		new Memory_WriteAddress( 0x3000, 0x37ff, MWA_RAM ),
		new Memory_WriteAddress( 0x3800, 0x3fff, MWA_RAM, nvram, nvram_size ),
		new Memory_WriteAddress( 0x4000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x4fff, MRA_RAM ),
		new Memory_ReadAddress( 0x6000, 0x6000, soundlatch_r ),
		new Memory_ReadAddress( 0x8000, 0x8000, hyperspt_sh_timer_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x4fff, MWA_RAM ),
		new Memory_WriteAddress( 0xa000, 0xa000, VLM5030_data_w ), /* speech data */
		new Memory_WriteAddress( 0xc000, 0xdfff, hyperspt_sound_w ),	  /* speech and output control */
		new Memory_WriteAddress( 0xe000, 0xe000, DAC_0_data_w ),
		new Memory_WriteAddress( 0xe001, 0xe001, konami_SN76496_latch_w ),  /* Loads the snd command into the snd latch */
		new Memory_WriteAddress( 0xe002, 0xe002, konami_SN76496_0_w ), 	 /* This address triggers the SN chip to read the data port. */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortHandlerPtr input_ports_hyperspt = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( hyperspt )
		PORT_START(); 		/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN4 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 		/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START3 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
	//	PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
		/* Fake button to press buttons 1 and 3 impossibly fast. Handle via konami_IN1_r */
		PORT_BITX(0x80, IP_ACTIVE_LOW, IPT_BUTTON4 | IPF_CHEAT | IPF_PLAYER1, "Run Like Hell Cheat", IP_KEY_DEFAULT, IP_JOY_DEFAULT );
	
		PORT_START(); 		/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER3 /*| IPF_COCKTAIL*/ );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER3 /*| IPF_COCKTAIL*/ );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER3 /*| IPF_COCKTAIL*/ );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START4 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER4 /*| IPF_COCKTAIL*/ );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER4 /*| IPF_COCKTAIL*/ );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER4 /*| IPF_COCKTAIL*/ );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 		/* DSW0 */
		PORT_DIPNAME( 0x0f, 0x0f, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(	0x02, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(	0x05, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(	0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x04, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(	0x01, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(	0x0f, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x03, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(	0x07, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(	0x0e, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x06, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(	0x0d, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(	0x0c, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(	0x0b, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(	0x0a, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(	0x09, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0xf0, 0xf0, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(	0x20, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(	0x50, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(	0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x40, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(	0x10, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(	0xf0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x30, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(	0x70, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(	0xe0, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x60, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(	0xd0, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(	0xc0, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(	0xb0, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(	0xa0, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(	0x90, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(	0x00, "Disabled" );
	/* 0x00 disables Coin 2. It still accepts coins and makes the sound, but
	   it doesn't give you any credit */
	
		PORT_START(); 		/* DSW1 */
		PORT_DIPNAME( 0x01, 0x00, "After Last Event" );
		PORT_DIPSETTING(	0x01, "Game Over" );
		PORT_DIPSETTING(	0x00, "Game Continues" );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(	0x02, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(	0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "World Records" );
		PORT_DIPSETTING(	0x08, "Don't Erase" );
		PORT_DIPSETTING(	0x00, "Erase on Reset" );
		PORT_DIPNAME( 0xf0, 0xf0, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(	0xf0, "Easy 1" );
		PORT_DIPSETTING(	0xe0, "Easy 2" );
		PORT_DIPSETTING(	0xd0, "Easy 3" );
		PORT_DIPSETTING(	0xc0, "Easy 4" );
		PORT_DIPSETTING(	0xb0, "Normal 1" );
		PORT_DIPSETTING(	0xa0, "Normal 2" );
		PORT_DIPSETTING(	0x90, "Normal 3" );
		PORT_DIPSETTING(	0x80, "Normal 4" );
		PORT_DIPSETTING(	0x70, "Normal 5" );
		PORT_DIPSETTING(	0x60, "Normal 6" );
		PORT_DIPSETTING(	0x50, "Normal 7" );
		PORT_DIPSETTING(	0x40, "Normal 8" );
		PORT_DIPSETTING(	0x30, "Difficult 1" );
		PORT_DIPSETTING(	0x20, "Difficult 2" );
		PORT_DIPSETTING(	0x10, "Difficult 3" );
		PORT_DIPSETTING(	0x00, "Difficult 4" );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_roadf = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( roadf )
		PORT_START(); 		/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN4 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 		/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 		/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* the game doesn't boot if this is 1 */
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 		/* DSW0 */
		PORT_DIPNAME( 0x0f, 0x0f, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(	0x02, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(	0x05, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(	0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x04, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(	0x01, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(	0x0f, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x03, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(	0x07, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(	0x0e, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x06, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(	0x0d, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(	0x0c, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(	0x0b, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(	0x0a, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(	0x09, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0xf0, 0xf0, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(	0x20, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(	0x50, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(	0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(	0x40, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(	0x10, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(	0xf0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(	0x30, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(	0x70, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(	0xe0, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(	0x60, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(	0xd0, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(	0xc0, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(	0xb0, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(	0xa0, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(	0x90, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(	0x00, "Disabled" );
	/* 0x00 disables Coin 2. It still accepts coins and makes the sound, but
	   it doesn't give you any credit */
	
		PORT_START(); 		/* DSW1 */
		PORT_DIPNAME( 0x01, 0x00, "Allow Continue" );
		PORT_DIPSETTING(	0x01, DEF_STR( "No") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x06, 0x06, "Number of Opponents" );
		PORT_DIPSETTING(	0x06, "Easy" );
		PORT_DIPSETTING(	0x04, "Medium" );
		PORT_DIPSETTING(	0x02, "Hard" );
		PORT_DIPSETTING(	0x00, "Hardest" );
		PORT_DIPNAME( 0x08, 0x08, "Speed of Opponents" );
		PORT_DIPSETTING(	0x08, "Easy" );
		PORT_DIPSETTING(	0x00, "Difficult" );
		PORT_DIPNAME( 0x30, 0x30, "Fuel Consumption" );
		PORT_DIPSETTING(	0x30, "Easy" );
		PORT_DIPSETTING(	0x20, "Medium" );
		PORT_DIPSETTING(	0x10, "Hard" );
		PORT_DIPSETTING(	0x00, "Hardest" );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(	0x40, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(	0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout hyperspt_charlayout = new GfxLayout
	(
		8,8,	/* 8*8 sprites */
		1024,	/* 1024 characters */
		4,	/* 4 bits per pixel */
		new int[] { 0x4000*8+4, 0x4000*8+0, 4, 0	},
		new int[] { 0, 1, 2, 3, 8*8+0, 8*8+1, 8*8+2, 8*8+3 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		16*8	/* every sprite takes 64 consecutive bytes */
	);
	
	static GfxLayout hyperspt_spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		512,	/* 512 sprites */
		4,	/* 4 bits per pixel */
		new int[] { 0x8000*8+4, 0x8000*8+0, 4, 0 },
		new int[] { 0, 1, 2, 3, 8*8+0, 8*8+1, 8*8+2, 8*8+3,
				16*8+0, 16*8+1, 16*8+2, 16*8+3, 24*8+0, 24*8+1, 24*8+2, 24*8+3 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 ,
			32*8, 33*8, 34*8, 35*8, 36*8, 37*8, 38*8, 39*8, },
		64*8	/* every sprite takes 64 consecutive bytes */
	);
	
	static GfxDecodeInfo hyperspt_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, hyperspt_charlayout, 	  0, 16 ),
		new GfxDecodeInfo( REGION_GFX2, 0, hyperspt_spritelayout, 16*16, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	static GfxLayout roadf_charlayout = new GfxLayout
	(
		8,8,	/* 8*8 sprites */
		1536,	/* 1536 characters */
		4,	/* 4 bits per pixel */
		new int[] { 0x6000*8+4, 0x6000*8+0, 4, 0	},
		new int[] { 0, 1, 2, 3, 8*8+0, 8*8+1, 8*8+2, 8*8+3 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		16*8	/* every sprite takes 64 consecutive bytes */
	);
	
	static GfxLayout roadf_spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		256,	/* 256 sprites */
		4,	/* 4 bits per pixel */
		new int[] { 0x4000*8+4, 0x4000*8+0, 4, 0 },
		new int[] { 0, 1, 2, 3, 8*8+0, 8*8+1, 8*8+2, 8*8+3,
				16*8+0, 16*8+1, 16*8+2, 16*8+3, 24*8+0, 24*8+1, 24*8+2, 24*8+3 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 ,
			32*8, 33*8, 34*8, 35*8, 36*8, 37*8, 38*8, 39*8, },
		64*8	/* every sprite takes 64 consecutive bytes */
	);
	
	static GfxDecodeInfo roadf_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, roadf_charlayout,	   0, 16 ),
		new GfxDecodeInfo( REGION_GFX2, 0, roadf_spritelayout, 16*16, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	struct VLM5030interface hyperspt_vlm5030_interface =
	{
		3580000,	/* master clock  */
		100,		/* volume		 */
		REGION_SOUND1,	/* memory region  */
		0		   /* memory size	 */
	};
	
	
	public static MachineHandlerPtr machine_driver_hyperspt = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M6809, 2048000)		/* 1.400 MHz ??? */
		MDRV_CPU_MEMORY(hyperspt_readmem,writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80,14318180/4)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU) /* Z80 Clock is derived from a 14.31818 MHz crystal */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		MDRV_NVRAM_HANDLER(hyperspt)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(hyperspt_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(32)
		MDRV_COLORTABLE_LENGTH(16*16+16*16)
	
		MDRV_PALETTE_INIT(hyperspt)
		MDRV_VIDEO_START(hyperspt)
		MDRV_VIDEO_UPDATE(hyperspt)
	
		/* sound hardware */
		MDRV_SOUND_ADD(DAC, konami_dac_interface)
		MDRV_SOUND_ADD(SN76496, konami_sn76496_interface)
		MDRV_SOUND_ADD(VLM5030, hyperspt_vlm5030_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	public static MachineHandlerPtr machine_driver_roadf = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
		MDRV_IMPORT_FROM(hyperspt)
		MDRV_CPU_MEMORY(roadf_readmem, writemem)
		MDRV_GFXDECODE(roadf_gfxdecodeinfo)
		MDRV_VIDEO_START(roadf)
	MACHINE_DRIVER_END();
 }
};
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadHandlerPtr rom_hyperspt = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 2*0x10000, REGION_CPU1, 0 )	 /* 64k for code + 64k for decrypted opcodes */
		ROM_LOAD( "c01",          0x4000, 0x2000, CRC(0c720eeb) SHA1(cc0719db7e59c72e603ab2ca42565303bc41d281) )
		ROM_LOAD( "c02",          0x6000, 0x2000, CRC(560258e0) SHA1(788d0d3cbbd97fb54eceb3281ccf84a31e5e3e98) )
		ROM_LOAD( "c03",          0x8000, 0x2000, CRC(9b01c7e6) SHA1(0106f94b38ad62e7514e56aab35581968074bbe0) )
		ROM_LOAD( "c04",          0xa000, 0x2000, CRC(10d7e9a2) SHA1(ebf1dd7ba10179c41b42358c45e49424ce8495cd) )
		ROM_LOAD( "c05",          0xc000, 0x2000, CRC(b105a8cd) SHA1(7d77ab4d75c0bff7ac7372a5ff5fe55839b57d19) )
		ROM_LOAD( "c06",          0xe000, 0x2000, CRC(1a34a849) SHA1(daa42a959ea162ca7f098010c85a7453a8805df8) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "c10",          0x0000, 0x2000, CRC(3dc1a6ff) SHA1(1e67cac46b6c8a9a0bb1560e135983435520f1fc) )
		ROM_LOAD( "c09",          0x2000, 0x2000, CRC(9b525c3e) SHA1(d8775ec3b4f12117431a2b7c7eaa038c1255241b) )
	
		ROM_REGION( 0x08000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "c26",          0x00000, 0x2000, CRC(a6897eac) SHA1(a1dd950c29885f7bb4784fed46810ae47bff87dd) )
		ROM_LOAD( "c24",          0x02000, 0x2000, CRC(5fb230c0) SHA1(8caebf3788c1fb71c1ba72b0045503d45936d4ce) )
		ROM_LOAD( "c22",          0x04000, 0x2000, CRC(ed9271a0) SHA1(a458ad79922383f45f6522775e19cf693e226883) )
		ROM_LOAD( "c20",          0x06000, 0x2000, CRC(183f4324) SHA1(f6bcd03c25dea300876ace950f118a971557168f) )
	
		ROM_REGION( 0x10000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "c14",          0x00000, 0x2000, CRC(c72d63be) SHA1(0677b4f7196551ebc1bbbecd0e15d79f8e32857d) )
		ROM_LOAD( "c13",          0x02000, 0x2000, CRC(76565608) SHA1(418fb9a81c0583d0214afb27fea28794563b8460) )
		ROM_LOAD( "c12",          0x04000, 0x2000, CRC(74d2cc69) SHA1(684b65455217f243b3690822d445efdcb18211bb) )
		ROM_LOAD( "c11",          0x06000, 0x2000, CRC(66cbcb4d) SHA1(c4ea51a6f30d2cd0cd6e22fdadb83d889f2cc471) )
		ROM_LOAD( "c18",          0x08000, 0x2000, CRC(ed25e669) SHA1(2e306db101cd4443b0a81cecf817e5ebbdaf1bba) )
		ROM_LOAD( "c17",          0x0a000, 0x2000, CRC(b145b39f) SHA1(e696e1f9b44aa44360ea9962c4ee9b61db8e53f5) )
		ROM_LOAD( "c16",          0x0c000, 0x2000, CRC(d7ff9f2b) SHA1(b0e6a056db96027ba0c10d3ee3bfdef145a236e2) )
		ROM_LOAD( "c15",          0x0e000, 0x2000, CRC(f3d454e6) SHA1(9d04dcd1b0354e01773923295bba2602e00467f9) )
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 )
		ROM_LOAD( "c03_c27.bin",  0x0000, 0x0020, CRC(bc8a5956) SHA1(90746145d9f380c29919edea3ef7a8434c48c9d9) )
		ROM_LOAD( "j12_c28.bin",  0x0020, 0x0100, CRC(2c891d59) SHA1(79050fbe058c24349927edc7937ec68a77f450f1) )
		ROM_LOAD( "a09_c29.bin",  0x0120, 0x0100, CRC(811a3f3f) SHA1(474f03345847cd9791ff6b7161286bbfef3f990a) )
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 )	/*	64k for speech rom	  */
		ROM_LOAD( "c08",          0x0000, 0x2000, CRC(e8f8ea78) SHA1(8d37818e5a2740c96696f37996f2a3f870386690) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_hpolym84 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 2*0x10000, REGION_CPU1, 0 )	 /* 64k for code + 64k for decrypted opcodes */
		ROM_LOAD( "c01",          0x4000, 0x2000, CRC(0c720eeb) SHA1(cc0719db7e59c72e603ab2ca42565303bc41d281) )
		ROM_LOAD( "c02",          0x6000, 0x2000, CRC(560258e0) SHA1(788d0d3cbbd97fb54eceb3281ccf84a31e5e3e98) )
		ROM_LOAD( "c03",          0x8000, 0x2000, CRC(9b01c7e6) SHA1(0106f94b38ad62e7514e56aab35581968074bbe0) )
		ROM_LOAD( "330e04.bin",   0xa000, 0x2000, CRC(9c5e2934) SHA1(7d25e53ca54f6b382785888838acff27bc2c1d43) )
		ROM_LOAD( "c05",          0xc000, 0x2000, CRC(b105a8cd) SHA1(7d77ab4d75c0bff7ac7372a5ff5fe55839b57d19) )
		ROM_LOAD( "c06",          0xe000, 0x2000, CRC(1a34a849) SHA1(daa42a959ea162ca7f098010c85a7453a8805df8) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "c10",          0x0000, 0x2000, CRC(3dc1a6ff) SHA1(1e67cac46b6c8a9a0bb1560e135983435520f1fc) )
		ROM_LOAD( "c09",          0x2000, 0x2000, CRC(9b525c3e) SHA1(d8775ec3b4f12117431a2b7c7eaa038c1255241b) )
	
		ROM_REGION( 0x08000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "c26",          0x00000, 0x2000, CRC(a6897eac) SHA1(a1dd950c29885f7bb4784fed46810ae47bff87dd) )
		ROM_LOAD( "330e24.bin",   0x02000, 0x2000, CRC(f9bbfe1d) SHA1(f24a0c3e10e727e3e9fd123cda8bb557af1fea12) )
		ROM_LOAD( "c22",          0x04000, 0x2000, CRC(ed9271a0) SHA1(a458ad79922383f45f6522775e19cf693e226883) )
		ROM_LOAD( "330e20.bin",   0x06000, 0x2000, CRC(29969b92) SHA1(baf394c56b8a2855f32b9e6d7346faf50e75bcf2) )
	
		ROM_REGION( 0x10000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "c14",          0x00000, 0x2000, CRC(c72d63be) SHA1(0677b4f7196551ebc1bbbecd0e15d79f8e32857d) )
		ROM_LOAD( "c13",          0x02000, 0x2000, CRC(76565608) SHA1(418fb9a81c0583d0214afb27fea28794563b8460) )
		ROM_LOAD( "c12",          0x04000, 0x2000, CRC(74d2cc69) SHA1(684b65455217f243b3690822d445efdcb18211bb) )
		ROM_LOAD( "c11",          0x06000, 0x2000, CRC(66cbcb4d) SHA1(c4ea51a6f30d2cd0cd6e22fdadb83d889f2cc471) )
		ROM_LOAD( "c18",          0x08000, 0x2000, CRC(ed25e669) SHA1(2e306db101cd4443b0a81cecf817e5ebbdaf1bba) )
		ROM_LOAD( "c17",          0x0a000, 0x2000, CRC(b145b39f) SHA1(e696e1f9b44aa44360ea9962c4ee9b61db8e53f5) )
		ROM_LOAD( "c16",          0x0c000, 0x2000, CRC(d7ff9f2b) SHA1(b0e6a056db96027ba0c10d3ee3bfdef145a236e2) )
		ROM_LOAD( "c15",          0x0e000, 0x2000, CRC(f3d454e6) SHA1(9d04dcd1b0354e01773923295bba2602e00467f9) )
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 )
		ROM_LOAD( "c03_c27.bin",  0x0000, 0x0020, CRC(bc8a5956) SHA1(90746145d9f380c29919edea3ef7a8434c48c9d9) )
		ROM_LOAD( "j12_c28.bin",  0x0020, 0x0100, CRC(2c891d59) SHA1(79050fbe058c24349927edc7937ec68a77f450f1) )
		ROM_LOAD( "a09_c29.bin",  0x0120, 0x0100, CRC(811a3f3f) SHA1(474f03345847cd9791ff6b7161286bbfef3f990a) )
	
		ROM_REGION( 0x10000, REGION_SOUND1, 0 )	/*	64k for speech rom	  */
		ROM_LOAD( "c08",          0x0000, 0x2000, CRC(e8f8ea78) SHA1(8d37818e5a2740c96696f37996f2a3f870386690) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_roadf = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 2*0x10000, REGION_CPU1, 0 )	 /* 64k for code + 64k for decrypted opcodes */
		ROM_LOAD( "g05_g01.bin",  0x4000, 0x2000, CRC(e2492a06) SHA1(e03895b83f1529dd7bb20e1380cb60c7606db3e4) )
		ROM_LOAD( "g07_f02.bin",  0x6000, 0x2000, CRC(0bf75165) SHA1(d3d16d63ca15c8f6b05c37b4e37e41785334ffff) )
		ROM_LOAD( "g09_g03.bin",  0x8000, 0x2000, CRC(dde401f8) SHA1(aa1810290c14d15d14e2f82a6780fc82d06d437b) )
		ROM_LOAD( "g11_f04.bin",  0xA000, 0x2000, CRC(b1283c77) SHA1(3fdd8d97cdd8a0b7c12db6797ed17f730425f337) )
		ROM_LOAD( "g13_f05.bin",  0xC000, 0x2000, CRC(0ad4d796) SHA1(44335c769341b3e10bb92556c0718884fd4b5d20) )
		ROM_LOAD( "g15_f06.bin",  0xE000, 0x2000, CRC(fa42e0ed) SHA1(408d365183fd95e54695a17abbba87d729546d7c) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "a17_d10.bin",  0x0000, 0x2000, CRC(c33c927e) SHA1(f1a8522e3bfc3a07bb42408d2937a4129e4c3fee) )
	
		ROM_REGION( 0x0c000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "a14_e26.bin",  0x00000, 0x4000, CRC(f5c738e2) SHA1(9f10be775791dee9801b1167f838a9110084842d) )
		ROM_LOAD( "a12_d24.bin",  0x04000, 0x2000, CRC(2d82c930) SHA1(fea26c00ad3acb1f44a5fdc79a7dd8ddce17d317) )
		ROM_LOAD( "c14_e22.bin",  0x06000, 0x4000, CRC(fbcfbeb9) SHA1(e5a938fc2fe2378d836dfe8ba516994cd5cf0bb5) )
		ROM_LOAD( "c12_d20.bin",  0x0a000, 0x2000, CRC(5e0cf994) SHA1(c81274d809c685ccf24108f56a4fa54146d4f493) )
	
		ROM_REGION( 0x08000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "j19_e14.bin",  0x00000, 0x4000, CRC(16d2bcff) SHA1(37c63faaaca43909bfb1e2ccb370efe4b276d8a9) )
		ROM_LOAD( "g19_e18.bin",  0x04000, 0x4000, CRC(490685ff) SHA1(5ca0aa3771d60688671aae196f10f9feecb15106) )
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 )
		ROM_LOAD( "c03_c27.bin",  0x0000, 0x0020, CRC(45d5e352) SHA1(0f4d358aaffcb68193247090e82f093752730518) )
		ROM_LOAD( "j12_c28.bin",  0x0020, 0x0100, CRC(2955e01f) SHA1(b0652d177a45571edc5978143d4023e7b173b383) )
		ROM_LOAD( "a09_c29.bin",  0x0120, 0x0100, CRC(5b3b5f2a) SHA1(e83556fba6d50ad20dff6e19bd300ba0c30cc6e2) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_roadf2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 2*0x10000, REGION_CPU1, 0 )	 /* 64k for code + 64k for decrypted opcodes */
		ROM_LOAD( "5g",           0x4000, 0x2000, CRC(d8070d30) SHA1(334e4586686c29d33c3281cc446c13d2d96301dd) )
		ROM_LOAD( "6g",           0x6000, 0x2000, CRC(8b661672) SHA1(bdc983d1ad88372ea1fc8263d4c254d26079ece7) )
		ROM_LOAD( "8g",           0x8000, 0x2000, CRC(714929e8) SHA1(0176e4199a091485af30e00777678e51664dee23) )
		ROM_LOAD( "11g",          0xA000, 0x2000, CRC(0f2c6b94) SHA1(a18fe9021e464374de524454403eccc0aaf3eeb7) )
		ROM_LOAD( "g13_f05.bin",  0xC000, 0x2000, CRC(0ad4d796) SHA1(44335c769341b3e10bb92556c0718884fd4b5d20) )
		ROM_LOAD( "g15_f06.bin",  0xE000, 0x2000, CRC(fa42e0ed) SHA1(408d365183fd95e54695a17abbba87d729546d7c) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "a17_d10.bin",  0x0000, 0x2000, CRC(c33c927e) SHA1(f1a8522e3bfc3a07bb42408d2937a4129e4c3fee) )
	
		ROM_REGION( 0x0c000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "a14_e26.bin",  0x00000, 0x4000, CRC(f5c738e2) SHA1(9f10be775791dee9801b1167f838a9110084842d) )
		ROM_LOAD( "a12_d24.bin",  0x04000, 0x2000, CRC(2d82c930) SHA1(fea26c00ad3acb1f44a5fdc79a7dd8ddce17d317) )
		ROM_LOAD( "c14_e22.bin",  0x06000, 0x4000, CRC(fbcfbeb9) SHA1(e5a938fc2fe2378d836dfe8ba516994cd5cf0bb5) )
		ROM_LOAD( "c12_d20.bin",  0x0a000, 0x2000, CRC(5e0cf994) SHA1(c81274d809c685ccf24108f56a4fa54146d4f493) )
	
		ROM_REGION( 0x08000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "j19_e14.bin",  0x00000, 0x4000, CRC(16d2bcff) SHA1(37c63faaaca43909bfb1e2ccb370efe4b276d8a9) )
		ROM_LOAD( "g19_e18.bin",  0x04000, 0x4000, CRC(490685ff) SHA1(5ca0aa3771d60688671aae196f10f9feecb15106) )
	
		ROM_REGION( 0x0220, REGION_PROMS, 0 )
		ROM_LOAD( "c03_c27.bin",  0x0000, 0x0020, CRC(45d5e352) SHA1(0f4d358aaffcb68193247090e82f093752730518) )
		ROM_LOAD( "j12_c28.bin",  0x0020, 0x0100, CRC(2955e01f) SHA1(b0652d177a45571edc5978143d4023e7b173b383) )
		ROM_LOAD( "a09_c29.bin",  0x0120, 0x0100, CRC(5b3b5f2a) SHA1(e83556fba6d50ad20dff6e19bd300ba0c30cc6e2) )
	ROM_END(); }}; 
	
	
	public static DriverInitHandlerPtr init_hyperspt  = new DriverInitHandlerPtr() { public void handler(){
		konami1_decode();
	} };
	
	
	public static GameDriver driver_hyperspt	   = new GameDriver("1984"	,"hyperspt"	,"hyperspt.java"	,rom_hyperspt,null	,machine_driver_hyperspt	,input_ports_hyperspt	,init_hyperspt	,ROT0,  "Konami (Centuri license)", "Hyper Sports" )
	public static GameDriver driver_hpolym84	   = new GameDriver("1984"	,"hpolym84"	,"hyperspt.java"	,rom_hpolym84,driver_hyperspt	,machine_driver_hyperspt	,input_ports_hyperspt	,init_hyperspt	,ROT0,  "Konami", "Hyper Olympic '84" )
	public static GameDriver driver_roadf	   = new GameDriver("1984"	,"roadf"	,"hyperspt.java"	,rom_roadf,null	,machine_driver_roadf	,input_ports_roadf	,init_hyperspt	,ROT90, "Konami", "Road Fighter (set 1)" )
	public static GameDriver driver_roadf2	   = new GameDriver("1984"	,"roadf2"	,"hyperspt.java"	,rom_roadf2,driver_roadf	,machine_driver_roadf	,input_ports_roadf	,init_hyperspt	,ROT90, "Konami", "Road Fighter (set 2)" )
}
