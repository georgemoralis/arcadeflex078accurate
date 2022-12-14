/***************************************************************************

Jack the Giant Killer memory map (preliminary)

driver by Brad Oliver


Main CPU
--------
0000-3fff  ROM
4000-5fff  RAM
b000-b07f  sprite ram
b400       command for sound CPU
b500-b505  input ports
b506	   screen flip off
b507	   screen flip on
b600-b61f  palette ram
b800-bbff  video ram
bc00-bfff  color ram
c000-ffff  More ROM

Sound CPU (appears to run in interrupt mode 1)
---------
0000-0fff  ROM
1000-1fff  ROM (Zzyzzyxx only)
4000-43ff  RAM
6000-6fff  R/C filter ???

I/O
---
0x40: Read - ay-8910 port 0
      Write - ay-8910 write
0x80: Write - ay-8910 control

The 2 ay-8910 read ports are responsible for reading the sound commands.

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class jack
{
	
	
	
	
	static int timer_rate;
	
	public static ReadHandlerPtr timer_r  = new ReadHandlerPtr() { public int handler(int offset){
		/* wrong! there should be no need for timer_rate, the same function */
		/* should work for both games */
		return activecpu_gettotalcycles() / timer_rate;
	} };
	
	
	public static WriteHandlerPtr jack_sh_command_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		soundlatch_w.handler(0,data);
		cpu_set_irq_line(1, 0, HOLD_LINE);
	} };
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x5fff, MRA_RAM ),
		new Memory_ReadAddress( 0xb000, 0xb07f, MRA_RAM ),
		new Memory_ReadAddress( 0xb500, 0xb500, input_port_0_r ),
		new Memory_ReadAddress( 0xb501, 0xb501, input_port_1_r ),
		new Memory_ReadAddress( 0xb502, 0xb502, input_port_2_r ),
		new Memory_ReadAddress( 0xb503, 0xb503, input_port_3_r ),
		new Memory_ReadAddress( 0xb504, 0xb504, input_port_4_r ),
		new Memory_ReadAddress( 0xb505, 0xb505, input_port_5_r ),
		new Memory_ReadAddress( 0xb506, 0xb507, jack_flipscreen_r ),
		new Memory_ReadAddress( 0xb800, 0xbfff, MRA_RAM ),
		new Memory_ReadAddress( 0xc000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x5fff, MWA_RAM ),
		new Memory_WriteAddress( 0xb000, 0xb07f, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0xb400, 0xb400, jack_sh_command_w ),
		new Memory_WriteAddress( 0xb506, 0xb507, jack_flipscreen_w ),
		new Memory_WriteAddress( 0xb600, 0xb61f, jack_paletteram_w, paletteram ),
		new Memory_WriteAddress( 0xb800, 0xbbff, jack_videoram_w, videoram ),
		new Memory_WriteAddress( 0xbc00, 0xbfff, jack_colorram_w, colorram ),
		new Memory_WriteAddress( 0xc000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x43ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x43ff, MWA_RAM ),
		new Memory_WriteAddress( 0x6000, 0x6fff, MWA_NOP ),  /* R/C filter ??? */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x40, 0x40, AY8910_read_port_0_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x80, 0x80, AY8910_control_port_0_w ),
		new IO_WritePort( 0x40, 0x40, AY8910_write_port_0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	static InputPortHandlerPtr input_ports_jack = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( jack )
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x08, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x10, "5" );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "Every 10000" );
		PORT_DIPSETTING(    0x20, "10000 Only" );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Start on Level 1" );
		PORT_DIPSETTING(    0x40, "Start on Level 13" );
		PORT_DIPNAME( 0x80, 0x00, "Bullets per Bean Collected" );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x80, "2" );
	
		PORT_START();       /* DSW2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_BIT( 0x1e, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_SERVICE( 0x20, IP_ACTIVE_HIGH );
		PORT_BITX (   0x40, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_BITX (   0x80, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "255 Lives", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START();       /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x1c, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN3 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
	
		PORT_START();       /* IN4 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0xfc, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN5 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0xfc, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	/* Same as 'jack', but different coinage */
	static InputPortHandlerPtr input_ports_jack2 = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( jack2 )
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x03, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x08, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x10, "5" );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "Every 10000" );
		PORT_DIPSETTING(    0x20, "10000 Only" );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Start on Level 1" );
		PORT_DIPSETTING(    0x40, "Start on Level 13" );
		PORT_DIPNAME( 0x80, 0x00, "Bullets per Bean Collected" );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x80, "2" );
	
		PORT_START();       /* DSW2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_BIT( 0x1e, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_SERVICE( 0x20, IP_ACTIVE_HIGH );
		PORT_BITX (   0x40, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_BITX (   0x80, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "255 Lives", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START();       /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x1c, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN3 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
	
		PORT_START();       /* IN4 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0xfc, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN5 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0xfc, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	/* Same as 'jack', but another different coinage */
	static InputPortHandlerPtr input_ports_jack3 = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( jack3 )
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_5C") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x10, "5" );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "Every 10000" );
		PORT_DIPSETTING(    0x20, "10000 Only" );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Start on Level 1" );
		PORT_DIPSETTING(    0x40, "Start on Level 13" );
		PORT_DIPNAME( 0x80, 0x00, "Bullets per Bean Collected" );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x80, "2" );
	
		PORT_START();       /* DSW2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_BIT( 0x1e, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_SERVICE( 0x20, IP_ACTIVE_HIGH );
		PORT_BITX (   0x40, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_BITX (   0x80, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "255 Lives", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START();       /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x1c, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN3 */
	
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
	
		PORT_START();       /* IN4 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0xfc, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN5 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0xfc, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	/* Same as 'jack', but different "Bullets per Bean Collected" and "Difficulty" Dip Switches */
	static InputPortHandlerPtr input_ports_treahunt = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( treahunt )
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x08, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x10, "5" );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "Every 10000" );
		PORT_DIPSETTING(    0x20, "10000 Only" );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Start on Level 1" );
		PORT_DIPSETTING(    0x40, "Start on Level 6" );
		PORT_DIPNAME( 0x80, 0x00, "Bullets per Bean Collected" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPSETTING(    0x80, "20" );
	
		PORT_START();       /* DSW2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_BIT( 0x1e, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_SERVICE( 0x20, IP_ACTIVE_HIGH );
		PORT_BITX (   0x40, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_BITX (   0x80, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "255 Lives", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START();       /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x1c, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN3 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
	
		PORT_START();       /* IN4 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0xfc, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN5 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0xfc, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_zzyzzyxx = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( zzyzzyxx )
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x04, "2" );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPNAME( 0x08, 0x00, "2 Credits on Reset" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_SERVICE( 0x40, IP_ACTIVE_HIGH );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START();       /* DSW2 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x02, "None" );
		PORT_DIPSETTING(    0x00, "10000 50000" );
		PORT_DIPSETTING(    0x01, "25000 100000" );
		PORT_DIPSETTING(    0x03, "100000 300000" );
		PORT_DIPNAME( 0x04, 0x04, "2nd Bonus Given" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x08, 0x00, "Starting Laps" );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x08, "3" );
		PORT_DIPNAME( 0x10, 0x00, "Difficulty of Pleasing Lola" );
		PORT_DIPSETTING(    0x00, "Easy" );
		PORT_DIPSETTING(    0x10, "Hard" );
		PORT_DIPNAME( 0x20, 0x00, "Show Intermissions" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Yes") );
		PORT_DIPNAME( 0xc0, 0x40, "Extra Lives" );
		PORT_DIPSETTING(    0x00, "3 under 4000 pts" );
		PORT_DIPSETTING(    0x80, "5 under 4000 pts" );
		PORT_DIPSETTING(    0x40, "None" );		// 3 under 0 pts
	//	PORT_DIPSETTING(    0xc0, "None" );		// 5 under 0 pts
	
		PORT_START();       /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0xfe, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0xfe, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN3 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_2WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_2WAY );
		PORT_BIT( 0x0c, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0xc0, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN4 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0xfe, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN5 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0xfe, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_freeze = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( freeze )
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_SERVICE( 0x02, IP_ACTIVE_HIGH );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Easy" );
		PORT_DIPSETTING(    0x04, "Hard" );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x08, "5" );
		PORT_DIPNAME( 0x30, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "10000" );
		PORT_DIPSETTING(    0x10, "10000 40000" );
		PORT_DIPSETTING(    0x20, "10000 60000" );
		PORT_DIPSETTING(    0x30, "20000 100000" );
		PORT_DIPNAME( 0xc0, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "Free_Play") );
	
		PORT_START();       /* DSW2 */
		/* probably unused */
		PORT_BIT( 0xff, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN3 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN4 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START();       /* IN5 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_sucasino = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( sucasino )
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_4C") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x70, 0x00, DEF_STR( "Unknown") );			// Check code at 0xf700
		PORT_DIPSETTING(    0x00, "0" );
		PORT_DIPSETTING(    0x10, "1" );
		PORT_DIPSETTING(    0x20, "2" );
		PORT_DIPSETTING(    0x30, "3" );
		PORT_DIPSETTING(    0x40, "4" );
		PORT_DIPSETTING(    0x50, "5" );
		PORT_DIPSETTING(    0x60, "6" );
		PORT_DIPSETTING(    0x70, "7" );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* DSW2 */
		PORT_BIT( 0xff, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x3c, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN3 */
		PORT_BIT( 0x03, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY );
		PORT_BIT( 0x30, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_2WAY | IPF_COCKTAIL );
	
		PORT_START();       /* IN4 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0xfe, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN5 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0xfe, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_tripool = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( tripool )
		PORT_START(); 	/* DSW 1 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* DSW 2 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START();       /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_BUTTON3 );// select game 1
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_BUTTON4 );// select game 2
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON5 );// select game 3
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN3 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
	
		PORT_START();       /* IN4 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON2 );// not needed?
		PORT_BIT( 0xfc, IP_ACTIVE_HIGH, IPT_UNUSED );
	
		PORT_START();       /* IN5 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL ); // not needed?
		PORT_BIT( 0xfc, IP_ACTIVE_HIGH, IPT_UNUSED );
	INPUT_PORTS_END(); }}; 
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		1024,	/* 1024 characters */
		2,	/* 2 bits per pixel */
		new int[] { 0, 1024*8*8 },	/* the two bitplanes are seperated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8	/* every char takes 16 bytes */
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout, 0, 8 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		1,	/* 1 chip */
		18000000/12,	/* 1.5 MHz */
		new int[] { 100 },
		new ReadHandlerPtr[] { soundlatch_r },
		new ReadHandlerPtr[] { timer_r },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	
	public static MachineHandlerPtr machine_driver_jack = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main", Z80, 18000000/6)	/* 3 MHz */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1) /* jack needs 1 or its too fast */
	
		MDRV_CPU_ADD(Z80,18000000/12)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 1.5 MHz */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(sound_readport,sound_writeport)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(32)
	
		MDRV_VIDEO_START(jack)
		MDRV_VIDEO_UPDATE(jack)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	MACHINE_DRIVER_END();
 }
};
	
	public static MachineHandlerPtr machine_driver_tripool = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(jack)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_VBLANK_INT(irq0_line_hold,2) /* tripool needs 2 or the palette is broken */
	MACHINE_DRIVER_END();
 }
};
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadHandlerPtr rom_jack = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "j8",           0x0000, 0x1000, CRC(c8e73998) SHA1(1332c8dee99d07cc2823797ecc3551d720428b36) )
		ROM_LOAD( "jgk.j6",       0x1000, 0x1000, CRC(36d7810e) SHA1(b8757222586eb6aa31fc3b1d1fd00ddb1c68cb0b) )
		ROM_LOAD( "jgk.j7",       0x2000, 0x1000, CRC(b15ff3ee) SHA1(fa99b4c2d96fb355ff8ba12c2f40ee4d00bb04da) )
		ROM_LOAD( "jgk.j5",       0x3000, 0x1000, CRC(4a63d242) SHA1(afecfb515144963eb819a58ef3b368c20e6fc4ff) )
		ROM_LOAD( "jgk.j3",       0xc000, 0x1000, CRC(605514a8) SHA1(74769053a977cea0324b1198e582f8e712af9a22) )
		ROM_LOAD( "jgk.j4",       0xd000, 0x1000, CRC(bce489b7) SHA1(8c1bb82f38f1757b08c99230454a6e7eca8709f3) )
		ROM_LOAD( "jgk.j2",       0xe000, 0x1000, CRC(db21bd55) SHA1(5518c34d381129c7940de85c476639cafd0e5025) )
		ROM_LOAD( "jgk.j1",       0xf000, 0x1000, CRC(49fffe31) SHA1(b5a0a7d021c8001368bb5d3b41a728734eb50ac5) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "jgk.j9",       0x0000, 0x1000, CRC(c2dc1e00) SHA1(57e8abf5a5eb3f5a22e206ee2562b64ea0ba2d05) )
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "jgk.j12",      0x0000, 0x1000, CRC(ce726df0) SHA1(d0b83c5ceb558dafb6387445d5cfb4668f2f4386) )
		ROM_LOAD( "jgk.j13",      0x1000, 0x1000, CRC(6aec2c8d) SHA1(f81c44e79e18a864abfeb8769f012a6e93679164) )
		ROM_LOAD( "jgk.j11",      0x2000, 0x1000, CRC(fd14c525) SHA1(5e6a8274d008c5dd276aaf85f7f943810b5ac987) )
		ROM_LOAD( "jgk.j10",      0x3000, 0x1000, CRC(eab890b2) SHA1(a5b83dff6bc6fd51f80db136fad8075262720f01) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_jack2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "jgk.j8",       0x0000, 0x1000, CRC(fe229e20) SHA1(191cfb7bb08d46cab713e23abd69f27db1685346) )
		ROM_LOAD( "jgk.j6",       0x1000, 0x1000, CRC(36d7810e) SHA1(b8757222586eb6aa31fc3b1d1fd00ddb1c68cb0b) )
		ROM_LOAD( "jgk.j7",       0x2000, 0x1000, CRC(b15ff3ee) SHA1(fa99b4c2d96fb355ff8ba12c2f40ee4d00bb04da) )
		ROM_LOAD( "jgk.j5",       0x3000, 0x1000, CRC(4a63d242) SHA1(afecfb515144963eb819a58ef3b368c20e6fc4ff) )
		ROM_LOAD( "jgk.j3",       0xc000, 0x1000, CRC(605514a8) SHA1(74769053a977cea0324b1198e582f8e712af9a22) )
		ROM_LOAD( "jgk.j4",       0xd000, 0x1000, CRC(bce489b7) SHA1(8c1bb82f38f1757b08c99230454a6e7eca8709f3) )
		ROM_LOAD( "jgk.j2",       0xe000, 0x1000, CRC(db21bd55) SHA1(5518c34d381129c7940de85c476639cafd0e5025) )
		ROM_LOAD( "jgk.j1",       0xf000, 0x1000, CRC(49fffe31) SHA1(b5a0a7d021c8001368bb5d3b41a728734eb50ac5) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "jgk.j9",       0x0000, 0x1000, CRC(c2dc1e00) SHA1(57e8abf5a5eb3f5a22e206ee2562b64ea0ba2d05) )
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "jgk.j12",      0x0000, 0x1000, CRC(ce726df0) SHA1(d0b83c5ceb558dafb6387445d5cfb4668f2f4386) )
		ROM_LOAD( "jgk.j13",      0x1000, 0x1000, CRC(6aec2c8d) SHA1(f81c44e79e18a864abfeb8769f012a6e93679164) )
		ROM_LOAD( "jgk.j11",      0x2000, 0x1000, CRC(fd14c525) SHA1(5e6a8274d008c5dd276aaf85f7f943810b5ac987) )
		ROM_LOAD( "jgk.j10",      0x3000, 0x1000, CRC(eab890b2) SHA1(a5b83dff6bc6fd51f80db136fad8075262720f01) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_jack3 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "jack8",        0x0000, 0x1000, CRC(632151d2) SHA1(080f29818d537474c821b9920427bda47f5a7254) )
		ROM_LOAD( "jack6",        0x1000, 0x1000, CRC(f94f80d9) SHA1(2301e6d0b814bf897e5c8ed43a342e3213be0a27) )
		ROM_LOAD( "jack7",        0x2000, 0x1000, CRC(c830ff1e) SHA1(f85b8bf39600212846f0b68012fbdb6b5fd3ad5c) )
		ROM_LOAD( "jack5",        0x3000, 0x1000, CRC(8dea17e7) SHA1(7e70bce78eaa40963ba981c9e7926ee0529898dd) )
		ROM_LOAD( "jgk.j3",       0xc000, 0x1000, CRC(605514a8) SHA1(74769053a977cea0324b1198e582f8e712af9a22) )
		ROM_LOAD( "jgk.j4",       0xd000, 0x1000, CRC(bce489b7) SHA1(8c1bb82f38f1757b08c99230454a6e7eca8709f3) )
		ROM_LOAD( "jgk.j2",       0xe000, 0x1000, CRC(db21bd55) SHA1(5518c34d381129c7940de85c476639cafd0e5025) )
		ROM_LOAD( "jack1",        0xf000, 0x1000, CRC(7e75ea3d) SHA1(9f3b998a8a494d67e3aa8933eb113fa2d2adae61) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "jgk.j9",       0x0000, 0x1000, CRC(c2dc1e00) SHA1(57e8abf5a5eb3f5a22e206ee2562b64ea0ba2d05) )
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "jack12",       0x0000, 0x1000, CRC(80320647) SHA1(5e39891033e23256456aad1a3f53cd1e516de51d) )
		ROM_LOAD( "jgk.j13",      0x1000, 0x1000, CRC(6aec2c8d) SHA1(f81c44e79e18a864abfeb8769f012a6e93679164) )
		ROM_LOAD( "jgk.j11",      0x2000, 0x1000, CRC(fd14c525) SHA1(5e6a8274d008c5dd276aaf85f7f943810b5ac987) )
		ROM_LOAD( "jgk.j10",      0x3000, 0x1000, CRC(eab890b2) SHA1(a5b83dff6bc6fd51f80db136fad8075262720f01) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_treahunt = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 2*0x10000, REGION_CPU1, 0 )	/* 64k for code + 64k for decrypted opcodes */
		ROM_LOAD( "thunt-1.f2",   0x0000, 0x1000, CRC(0b35858c) SHA1(b8f80c69fcbce71e1b85c8f39599f8bebfeb2585) )
		ROM_LOAD( "thunt-2.f3",   0x1000, 0x1000, CRC(67305a51) SHA1(c00b9592c4e146892313e8d32261338957a6a04a) )
		ROM_LOAD( "thunt-3.4f",   0x2000, 0x1000, CRC(d7a969c3) SHA1(7edcbc90836e32aff4a26b0c55a76bbc9bb488fe) )
		ROM_LOAD( "thunt-4.6f",   0x3000, 0x1000, CRC(2483f14d) SHA1(ffb7965433b0caaaae74e8eca19633fcecbdb4f8) )
		ROM_LOAD( "thunt-5.7f",   0xc000, 0x1000, CRC(c69d5e21) SHA1(27b734b2997bc95d04c79b992969db19b743b086) )
		ROM_LOAD( "thunt-6.7e",   0xd000, 0x1000, CRC(11bf3d49) SHA1(6c566aa81568985662461df7bd2386ee72ee3ba7) )
		ROM_LOAD( "thunt-7.6e",   0xe000, 0x1000, CRC(7c2d6279) SHA1(b3dd9875faf9cd91034193794a7b187d79741353) )
		ROM_LOAD( "thunt-8.4e",   0xf000, 0x1000, CRC(f73b86fb) SHA1(7fd4d0876ffee74ec73def085fc845535bb7e451) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "jgk.j9",       0x0000, 0x1000, CRC(c2dc1e00) SHA1(57e8abf5a5eb3f5a22e206ee2562b64ea0ba2d05) )
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "thunt-13.a4",  0x0000, 0x1000, CRC(e03f1f09) SHA1(546b270aeeb2d35b718ddd6f15829d4cbe0f7ef6) )
		ROM_LOAD( "thunt-12.a3",  0x1000, 0x1000, CRC(da4ee9eb) SHA1(e01c9cfa426d2b94e6bc976622b888b2ca224771) )
		ROM_LOAD( "thunt-10.a1",  0x2000, 0x1000, CRC(51ec7934) SHA1(f39d99c356d8d9960022fa2c068b5f7206404d85) )
		ROM_LOAD( "thunt-11.a2",  0x3000, 0x1000, CRC(f9781143) SHA1(f168648a78240fdf02063d39f324838f4dfe9a56) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_zzyzzyxx = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "a.2f",         0x0000, 0x1000, CRC(a9102e34) SHA1(80d71df7d235980603f35aa3f474aaf58fb39946) )
		ROM_LOAD( "zzyzzyxx.b",   0x1000, 0x1000, CRC(efa9d4c6) SHA1(aaa66723fed87f1134b59634050d1eb6a83c8159) )
		ROM_LOAD( "zzyzzyxx.c",   0x2000, 0x1000, CRC(b0a365b1) SHA1(67e3c2bab8b2b35c42a986b0ace120724008f555) )
		ROM_LOAD( "zzyzzyxx.d",   0x3000, 0x1000, CRC(5ed6dd9a) SHA1(1279cee868eacefdc26524f2effa7b35f24ec30d) )
		ROM_LOAD( "zzyzzyxx.e",   0xc000, 0x1000, CRC(5966fdbf) SHA1(c1476db9e8508cb71684b568a19ae32c8c0e012a) )
		ROM_LOAD( "f.7e",         0xd000, 0x1000, CRC(12f24c68) SHA1(6d4181d3f044de491d810a3406e9d253d2c669d6) )
		ROM_LOAD( "g.6e",         0xe000, 0x1000, CRC(408f2326) SHA1(fe45084ed50701577eade2da8f4f787ee41d7acf) )
		ROM_LOAD( "h.4e",         0xf000, 0x1000, CRC(f8bbabe0) SHA1(59b2223219712f8a572b2cfbbc14f80ec2b32aae) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "i.5a",         0x0000, 0x1000, CRC(c7742460) SHA1(1dbf0f5be1e2666feef83f256e2993a6c23d7cfc) )
		ROM_LOAD( "j.6a",         0x1000, 0x1000, CRC(72166ccd) SHA1(4f4efcd8ed7f729f4630446607b0e9c93098aa3a) )
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "n.1c",         0x0000, 0x1000, CRC(4f64538d) SHA1(1d48f12ff0d1c5604d19338b26e800a91f1be9c1) )
		ROM_LOAD( "m.1d",         0x1000, 0x1000, CRC(217b1402) SHA1(b842b2bde8ff5be6b240ccfb35c7a9f701dab5f4) )
		ROM_LOAD( "k.1b",         0x2000, 0x1000, CRC(b8b2b8cc) SHA1(e149fc91043f3233e10c81358b8624a4bc0baf4e) )
		ROM_LOAD( "l.1a",         0x3000, 0x1000, CRC(ab421a83) SHA1(1cc3e1bcf9e90ffbf7bfeeb0caa8a4f63b34146a) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_zzyzzyx2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "a.2f",         0x0000, 0x1000, CRC(a9102e34) SHA1(80d71df7d235980603f35aa3f474aaf58fb39946) )
		ROM_LOAD( "b.3f",         0x1000, 0x1000, CRC(4277beab) SHA1(269338a165286ed44b0fad1873e409f847b8d476) )
		ROM_LOAD( "c.4f",         0x2000, 0x1000, CRC(72ac99e1) SHA1(66b99a0271ae31cf109749159ddd1652b804f077) )
		ROM_LOAD( "d.6f",         0x3000, 0x1000, CRC(7c7eec2b) SHA1(fa62950d9db718069905331140e129711c707775) )
		ROM_LOAD( "e.7f",         0xc000, 0x1000, CRC(cffc4a68) SHA1(95b13cbf9dc2196844038ce23ddfc33fecc9caef) )
		ROM_LOAD( "f.7e",         0xd000, 0x1000, CRC(12f24c68) SHA1(6d4181d3f044de491d810a3406e9d253d2c669d6) )
		ROM_LOAD( "g.6e",         0xe000, 0x1000, CRC(408f2326) SHA1(fe45084ed50701577eade2da8f4f787ee41d7acf) )
		ROM_LOAD( "h.4e",         0xf000, 0x1000, CRC(f8bbabe0) SHA1(59b2223219712f8a572b2cfbbc14f80ec2b32aae) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "i.5a",         0x0000, 0x1000, CRC(c7742460) SHA1(1dbf0f5be1e2666feef83f256e2993a6c23d7cfc) )
		ROM_LOAD( "j.6a",         0x1000, 0x1000, CRC(72166ccd) SHA1(4f4efcd8ed7f729f4630446607b0e9c93098aa3a) )
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "n.1c",         0x0000, 0x1000, CRC(4f64538d) SHA1(1d48f12ff0d1c5604d19338b26e800a91f1be9c1) )
		ROM_LOAD( "m.1d",         0x1000, 0x1000, CRC(217b1402) SHA1(b842b2bde8ff5be6b240ccfb35c7a9f701dab5f4) )
		ROM_LOAD( "k.1b",         0x2000, 0x1000, CRC(b8b2b8cc) SHA1(e149fc91043f3233e10c81358b8624a4bc0baf4e) )
		ROM_LOAD( "l.1a",         0x3000, 0x1000, CRC(ab421a83) SHA1(1cc3e1bcf9e90ffbf7bfeeb0caa8a4f63b34146a) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_brix = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "a",            0x0000, 0x1000, CRC(050e0d70) SHA1(f5e0ed0845443701233de194d9ce24ec35e03a27) )
		ROM_LOAD( "b",            0x1000, 0x1000, CRC(668118ae) SHA1(688d6f79d30186bade15dbb1f08e8b25cbefa852) )
		ROM_LOAD( "c",            0x2000, 0x1000, CRC(ff5ed6cf) SHA1(b6309ed322c2bb12626dfaca705e296723ee7e47) )
		ROM_LOAD( "d",            0x3000, 0x1000, CRC(c3ae45a9) SHA1(879f0a495d9de855ffcbb0907b9b733ca626a7ef) )
		ROM_LOAD( "e",            0xc000, 0x1000, CRC(def99fa9) SHA1(e28d32934e1ad31595ec6097befd8518178c9d51) )
		ROM_LOAD( "f",            0xd000, 0x1000, CRC(dde717ed) SHA1(cf9063aa25faf2027770a4b27831e2e20d1801a0) )
		ROM_LOAD( "g",            0xe000, 0x1000, CRC(adca02d8) SHA1(75703a6f6d8b5eeb609ed5829d12b97b62309ba4) )
		ROM_LOAD( "h",            0xf000, 0x1000, CRC(bc3b878c) SHA1(91a5daa90a4c46a354f4ef64730b4a0a8348b6a0) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "i.5a",         0x0000, 0x1000, CRC(c7742460) SHA1(1dbf0f5be1e2666feef83f256e2993a6c23d7cfc) )
		ROM_LOAD( "j.6a",         0x1000, 0x1000, CRC(72166ccd) SHA1(4f4efcd8ed7f729f4630446607b0e9c93098aa3a) )
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "n",            0x0000, 0x1000, CRC(8064910e) SHA1(331048e30604ef2a0ae0d7ee5ca5c230b601aec7) )
		ROM_LOAD( "m.1d",         0x1000, 0x1000, CRC(217b1402) SHA1(b842b2bde8ff5be6b240ccfb35c7a9f701dab5f4) )
		ROM_LOAD( "k",            0x2000, 0x1000, CRC(c7d7e2a0) SHA1(9790e78abf4f57ddfcef8e5632699152f9440a67) )
		ROM_LOAD( "l.1a",         0x3000, 0x1000, CRC(ab421a83) SHA1(1cc3e1bcf9e90ffbf7bfeeb0caa8a4f63b34146a) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_freeze = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "freeze.f2",    0x0000, 0x1000, CRC(0a431665) SHA1(57b7fc72c7e3b0d09b4a0676a4e7094657e2b742) )
		ROM_LOAD( "freeze.f3",    0x1000, 0x1000, CRC(1189b8ad) SHA1(8feb9387783e63a98efb60778fdf9eb9d5392cd9) )
		ROM_LOAD( "freeze.f4",    0x2000, 0x1000, CRC(10c4a5ea) SHA1(9ace2cff0280f10b03752568258b2e3a13ac964f) )
		ROM_LOAD( "freeze.f5",    0x3000, 0x1000, CRC(16024c53) SHA1(354b91ad880ce0ea0f1481c3aea91570d05797c7) )
		ROM_LOAD( "freeze.f7",    0xc000, 0x1000, CRC(ea0b0765) SHA1(17923177d31ab4ca9f9bba1fc95fff825d8113e3) )
		ROM_LOAD( "freeze.e7",    0xd000, 0x1000, CRC(1155c00b) SHA1(734eb7cc77432f7112e6032a298f8d38152a0717) )
		ROM_LOAD( "freeze.e5",    0xe000, 0x1000, CRC(95c18d75) SHA1(02c8b9738049f61d1d34053f508b26ee588b2025) )
		ROM_LOAD( "freeze.e4",    0xf000, 0x1000, CRC(7e8f5afc) SHA1(5694982671ef5c7564f216150825f4e81c4ba617) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "freeze.a1",    0x0000, 0x1000, CRC(7771f5b9) SHA1(48715945f67a0d736c86d1fdd738964c6cf74c35) )
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "freeze.5a",    0x0000, 0x1000, CRC(6c8a98a0) SHA1(358a88377a227566962251c2a6ad7aea52ae1d17) )
		ROM_LOAD( "freeze.3a",    0x1000, 0x1000, CRC(6d2125e4) SHA1(6c3a12af512a1243b73759a758da8329bca38833) )
		ROM_LOAD( "freeze.1a",    0x2000, 0x1000, CRC(3a7f2fa9) SHA1(5f0811ea4e61b9918de2d16ffcfa4a02af833613) )
		ROM_LOAD( "freeze.2a",    0x3000, 0x1000, CRC(dd70ddd6) SHA1(d03cac0b4248da5d49ffac6ee57a3f8dd368731b) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_sucasino = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "1",       	  0x0000, 0x1000, CRC(e116e979) SHA1(99b0c783ace93e643738a1a924cafb690d2c1127) )
		ROM_LOAD( "2",      	  0x1000, 0x1000, CRC(2a2635f5) SHA1(e3b70942adc4eab81000287c8da67d3732ddda70) )
		ROM_LOAD( "3",       	  0x2000, 0x1000, CRC(69864d90) SHA1(244eaf4079b90f367c671e00e8081d885f26e26d) )
		ROM_LOAD( "4",       	  0x3000, 0x1000, CRC(174c9373) SHA1(070175bf1b7b14f34549d03a8288c8ff1f2f4eaa) )
		ROM_LOAD( "5",       	  0xc000, 0x1000, CRC(115bcb1e) SHA1(9b50e1dcb77db1b60ab5fd7d9843261e25580647) )
		ROM_LOAD( "6",       	  0xd000, 0x1000, CRC(434caa17) SHA1(2f537063db14cfdfb771dece2ea33841c874c708) )
		ROM_LOAD( "7",       	  0xe000, 0x1000, CRC(67c68b82) SHA1(b5d3977bf1f1337a96ae7bb60fe11e6ca9e87485) )
		ROM_LOAD( "8",       	  0xf000, 0x1000, CRC(f5b63006) SHA1(a069fb9b9b6d47ac3f0fbbd9b2c89da31d6b1202) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "9",       	  0x0000, 0x1000, CRC(67cf8aec) SHA1(95be671d5f7526610b175fc4121459e0ffc3649b) )
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "11",      	  0x0000, 0x1000, CRC(f92c4c5b) SHA1(a415c8f55d1792e79d05ece223ef423f8578f896) )
		/* 1000-1fff empty */
		ROM_LOAD( "10",      	  0x2000, 0x1000, CRC(3b0783ce) SHA1(880f258351a8b0d76abe433cc77d95b991ae1adc) )
		/* 3000-3fff empty */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_tripool = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code + 64k for decrypted opcodes */
		ROM_LOAD( "tri73a.bin",   0x0000, 0x1000, CRC(96893aa7) SHA1(ea1dc5824d89c1bb131850625a65d018a9127179) )
		ROM_LOAD( "tri62a.bin",   0x2000, 0x1000, CRC(3299dc65) SHA1(8f93247e2f49be6b601006be62f4ad539ec899fe) )
		ROM_LOAD( "tri52b.bin",   0x3000, 0x1000, CRC(27ef765e) SHA1(2a18a9b74fd4d9f3a724270cd3a98adbfdf22a5e) )
		ROM_LOAD( "tri33c.bin",   0xc000, 0x1000, CRC(d7ef061d) SHA1(3ea3a136ecb3b5753a1dd929212b93ad8c7e9157) )
		ROM_LOAD( "tri45c.bin",   0xd000, 0x1000, CRC(51b813b1) SHA1(11ace37869a44a8c4bec76f19815a7f2fcc1d23e) )
		ROM_LOAD( "tri25d.bin",   0xe000, 0x1000, CRC(8e64512d) SHA1(c4983db1e8143dc90f9a8c99bdbb73dc31529a6c) )
		ROM_LOAD( "tri13d.bin",   0xf000, 0x1000, CRC(ad268e9b) SHA1(5d8d9b1c57b332b5a28b01d6a4f4885239d80b00) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "trisnd.bin",       0x0000, 0x1000, CRC(945c4b8b) SHA1(f574de1633e7dd71d29c0bcdbc6fa675d1a3f7d1) )
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "tri93a.bin",   0x2000, 0x1000, CRC(35213782) SHA1(05d5a67ffa3d26377c54777917d3ba51677ebd28) )
		ROM_LOAD( "tri105a.bin",  0x0000, 0x1000, CRC(366a753c) SHA1(30fa8d80e42287e3e8677aefd15beab384265728) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_tripoola = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code + 64k for decrypted opcodes */
		ROM_LOAD( "tri73a.bin",   0x0000, 0x1000, CRC(96893aa7) SHA1(ea1dc5824d89c1bb131850625a65d018a9127179) )
		ROM_LOAD( "tri62a.bin",   0x2000, 0x1000, CRC(3299dc65) SHA1(8f93247e2f49be6b601006be62f4ad539ec899fe) )
		ROM_LOAD( "tri52b.bin",   0x3000, 0x1000, CRC(27ef765e) SHA1(2a18a9b74fd4d9f3a724270cd3a98adbfdf22a5e) )
		ROM_LOAD( "tri33c.bin",   0xc000, 0x1000, CRC(d7ef061d) SHA1(3ea3a136ecb3b5753a1dd929212b93ad8c7e9157) )
		ROM_LOAD( "tri45c.bin",   0xd000, 0x1000, CRC(51b813b1) SHA1(11ace37869a44a8c4bec76f19815a7f2fcc1d23e) )
		ROM_LOAD( "tri25d.bin",   0xe000, 0x1000, CRC(8e64512d) SHA1(c4983db1e8143dc90f9a8c99bdbb73dc31529a6c) )
		ROM_LOAD( "tp1ckt",       0xf000, 0x1000, CRC(72ec43a3) SHA1(a4f5b20872e41845340db627321e0dbcad4b964e) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "trisnd.bin",       0x0000, 0x1000, CRC(945c4b8b) SHA1(f574de1633e7dd71d29c0bcdbc6fa675d1a3f7d1) )
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "tri93a.bin",   0x2000, 0x1000, CRC(35213782) SHA1(05d5a67ffa3d26377c54777917d3ba51677ebd28) )
		ROM_LOAD( "tri105a.bin",  0x0000, 0x1000, CRC(366a753c) SHA1(30fa8d80e42287e3e8677aefd15beab384265728) )
	ROM_END(); }}; 
	
	static void treahunt_decode(void)
	{
		int A;
		unsigned char *rom = memory_region(REGION_CPU1);
		int diff = memory_region_length(REGION_CPU1) / 2;
		int data;
	
	
		memory_set_opcode_base(0,rom+diff);
	
		/* Thanks to Mike Balfour for helping out with the decryption */
		for (A = 0; A < 0x4000; A++)
		{
			data = rom[A];
	
			if (A & 0x1000)
			{
				/* unencrypted = D0 D2 D5 D1 D3 D6 D4 D7 */
				rom[A+diff] =
					 ((data & 0x01) << 7) |
					 ((data & 0x02) << 3) |
					 ((data & 0x04) << 4) |
					  (data & 0x28) |
					 ((data & 0x10) >> 3) |
					 ((data & 0x40) >> 4) |
					 ((data & 0x80) >> 7);
	
				if ((A & 0x04) == 0)
				/* unencrypted = !D0 D2 D5 D1 D3 D6 D4 !D7 */
					rom[A+diff] ^= 0x81;
			}
			else
			{
				/* unencrypted = !D7 D2 D5 D1 D3 D6 D4 !D0 */
				rom[A+diff] =
						(~data & 0x81) |
						((data & 0x02) << 3) |
						((data & 0x04) << 4) |
						 (data & 0x28) |
						((data & 0x10) >> 3) |
						((data & 0x40) >> 4);
			}
		}
	}
	
	public static DriverInitHandlerPtr init_jack  = new DriverInitHandlerPtr() { public void handler(){
		timer_rate = 128;
	} };
	
	public static DriverInitHandlerPtr init_treahunt  = new DriverInitHandlerPtr() { public void handler(){
		timer_rate = 128;
		treahunt_decode();
	} };
	
	public static DriverInitHandlerPtr init_zzyzzyxx  = new DriverInitHandlerPtr() { public void handler(){
		timer_rate = 16;
	} };
	
	
	
	public static GameDriver driver_jack	   = new GameDriver("1982"	,"jack"	,"jack.java"	,rom_jack,null	,machine_driver_jack	,input_ports_jack	,init_jack	,ROT90, "Cinematronics", "Jack the Giantkiller (set 1)" )
	public static GameDriver driver_jack2	   = new GameDriver("1982"	,"jack2"	,"jack.java"	,rom_jack2,driver_jack	,machine_driver_jack	,input_ports_jack2	,init_jack	,ROT90, "Cinematronics", "Jack the Giantkiller (set 2)" )
	public static GameDriver driver_jack3	   = new GameDriver("1982"	,"jack3"	,"jack.java"	,rom_jack3,driver_jack	,machine_driver_jack	,input_ports_jack3	,init_jack	,ROT90, "Cinematronics", "Jack the Giantkiller (set 3)" )
	public static GameDriver driver_treahunt	   = new GameDriver("1982"	,"treahunt"	,"jack.java"	,rom_treahunt,driver_jack	,machine_driver_jack	,input_ports_treahunt	,init_treahunt	,ROT90, "Hara Industries", "Treasure Hunt (Japan?)" )
	public static GameDriver driver_zzyzzyxx	   = new GameDriver("1982"	,"zzyzzyxx"	,"jack.java"	,rom_zzyzzyxx,null	,machine_driver_jack	,input_ports_zzyzzyxx	,init_zzyzzyxx	,ROT90, "Cinematronics + Advanced Microcomputer Systems", "Zzyzzyxx (set 1)" )
	public static GameDriver driver_zzyzzyx2	   = new GameDriver("1982"	,"zzyzzyx2"	,"jack.java"	,rom_zzyzzyx2,driver_zzyzzyxx	,machine_driver_jack	,input_ports_zzyzzyxx	,init_zzyzzyxx	,ROT90, "Cinematronics + Advanced Microcomputer Systems", "Zzyzzyxx (set 2)" )
	public static GameDriver driver_brix	   = new GameDriver("1982"	,"brix"	,"jack.java"	,rom_brix,driver_zzyzzyxx	,machine_driver_jack	,input_ports_zzyzzyxx	,init_zzyzzyxx	,ROT90, "Cinematronics + Advanced Microcomputer Systems", "Brix" )
	public static GameDriver driver_freeze	   = new GameDriver("1984"	,"freeze"	,"jack.java"	,rom_freeze,null	,machine_driver_jack	,input_ports_freeze	,init_jack	,ROT90, "Cinematronics", "Freeze" )
	public static GameDriver driver_sucasino	   = new GameDriver("1984"	,"sucasino"	,"jack.java"	,rom_sucasino,null	,machine_driver_jack	,input_ports_sucasino	,init_jack	,ROT90, "Data Amusement", "Super Casino" )
	public static GameDriver driver_tripool	   = new GameDriver("1981"	,"tripool"	,"jack.java"	,rom_tripool,null	,machine_driver_tripool	,input_ports_tripool	,init_jack	,ROT90, "Noma (Casino Tech license)", "Tri-Pool (Casino Tech)" )
	public static GameDriver driver_tripoola	   = new GameDriver("1981"	,"tripoola"	,"jack.java"	,rom_tripoola,driver_tripool	,machine_driver_tripool	,input_ports_tripool	,init_jack	,ROT90, "Noma (Costal Games license)", "Tri-Pool (Costal Games)" )
}
