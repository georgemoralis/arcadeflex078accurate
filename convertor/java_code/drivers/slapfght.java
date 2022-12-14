/***************************************************************************

Slap Fight driver by K.Wilkins Jan 1998

Slap Fight - Taito

The three drivers provided are identical, only the 1st CPU EPROM is different
which shows up in the boot message, one if Japanese domestic and the other
is English. The proms which MAY be the original slapfight ones currently
give a hardware error and fail to boot.

slapfigh - Arcade ROMs from Japan http://home.onestop.net/j_rom/
slapboot - Unknown source
slpboota - ROMS Dumped by KW 29/12/97 from unmarked Slap Fight board (bootleg?)

PCB Details from slpboota boardset:

Upper PCB (Sound board)
---------
Z80A CPU
Toshiba TMM2016BP-10 (2KB SRAM)
sf_s05 (Fujitsu MBM2764-25 8KB EPROM) - Sound CPU Code

Yamaha YM2149F (Qty 2 - Pin compatible with AY-3-8190)
Hitachi SRAM - HM6464 (8KB - Qty 4)

sf_s01 (OKI M27256-N 32KB PROM)              Sprite Data (16x16 4bpp)
sf_s02 (OKI M27256-N 32KB PROM)              Sprite Data
sf_s03 (OKI M27256-N 32KB PROM)              Sprite Data
sf_s04 (OKI M27256-N 32KB PROM)              Sprite Data


Lower PCB
---------
Z80B CPU
12MHz Xtal
Toshiba TMM2016BP-10 (2KB SRAM - Total Qty 6 = 2+2+1+1)

sf_s10 (Fujitsu MBM2764-25 8KB EPROM)        Font/Character Data (8x8 2bpp)
sf_s11 (Fujitsu MBM2764-25 8KB EPROM)

sf_s06 (OKI M27256-N 32KB PROM)              Tile Data (8x8 4bpp)
sf_s07 (OKI M27256-N 32KB PROM)              Tile Data
sf_s08 (OKI M27256-N 32KB PROM)              Tile Data
sf_s09 (OKI M27256-N 32KB PROM)              Tile Data

sf_s16 (Fujitsu MBM2764-25 8KB EPROM)        Colour Tables (512B used?)

sf_sH  (OKI M27256-N 32KB PROM)              Level Maps ???

sf_s19 (NEC S27128 16KB EPROM)               CPU Code $0000-$3fff
sf_s20 (Mitsubishi M5L27128K 16KB EPROM)     CPU Code $4000-$7fff


Main CPU Memory Map
-------------------

$0000-$3fff    ROM (SF_S19)
$4000-$7fff    ROM (SF_S20)
$8000-$bfff    ROM (SF_SH) - This is a 32K ROM - Paged ????? How ????

$c000-$c7ff    2K RAM
$c800-$cfff    READ:Unknown H/W  WRITE:Unknown H/W (Upper PCB)
$d000-$d7ff    Background RAM1
$d800-$dfff    Background RAM2
$e000-$e7ff    Sprite RAM
$e800-$efff    READ:Unknown H/W  WRITE:Unknown H/W
$f000-$f7ff    READ:SF_S16       WRITE:Character RAM
$f800-$ffff    READ:Unknown H/W  WRITE:Attribute RAM

$c800-$cfff    Appears to be RAM BUT 1st 0x10 bytes are swapped with
               the sound CPU and visversa for READ OPERATIONS


Write I/O MAP
-------------
Addr    Address based write                     Data based write

$00     Reset sound CPU
$01     Clear sound CPU reset
$02
$03
$04
$05
$06     Clear/Disable Hardware interrupt
$07     Enable Hardware interrupt
$08     LOW Bank select for SF_SH               X axis character scroll reg
$09     HIGH Bank select for SF_SH              X axis pixel scroll reg
$0a
$0b
$0c
$0e
$0f

Read I/O Map
------------

$00     Status regsiter - cycle 0xc7, 0x55, 0x00  (Thanks to Dave Spicer for the info)


Known Info
----------

2K Character RAM at write only address $f000-$f7fff looks to be organised
64x32 chars with the screen rotated thru 90 degrees clockwise. There
appears to be some kind of attribute(?) RAM above at $f800-$ffff organised
in the same manner.

From the look of data in the buffer it is arranged thus: 37x32 (HxW) which
would make the overall frame buffer 296x256.

Print function maybe around $09a2 based on info from log file.

$e000 looks like sprite ram, setup routines at $0008.


Sound System CPU Details
------------------------

Memory Map
$0000-$1fff  ROM(SF_S5)
$a080        AY-3-8910(PSG1) Register address
$a081        AY-3-8910(PSG1) Read register
$a082        AY-3-8910(PSG1) Write register
$a090        AY-3-8910(PSG2) Register address
$a091        AY-3-8910(PSG2) Read register
$a092        AY-3-8910(PSG2) Write register
$c800-$cfff  RAM(2K)

Strangely the RAM hardware registers seem to be overlaid at $c800
$00a6 routine here reads I/O ports and stores in, its not a straight
copy, the data is mangled before storage:
PSG1-E -> $c808
PSG1-F -> $c80b
PSG2-E -> $c809
PSG2-F -> $c80a - DIP Switch Bank 2 (Test mode is here)

-------------------------------GET STAR------------------------------------
		following info by Luca Elia (l.elia@tin.it)

				Interesting locations
				---------------------

c803	credits
c806	used as a watchdog: main cpu reads then writes FF.
	If FF was read, jp 0000h. Sound cpu zeroes it.

c807(1p)	left	7			c809	DSW1(cpl'd)
c808(2p)	down	6			c80a	DSW2(cpl'd)
active_H	right	5			c80b	ip 1(cpl'd)
		up	4
		0	3
		0	2
		but2	1
		but1	0

c21d(main)	1p lives

Main cpu writes to unmapped ports 0e,0f,05,03 at startup.
Before playing, f1 is written to e802 and 00 to port 03.
If flip screen dsw is on, ff is written to e802 an 00 to port 02, instead.

				Interesting routines (main cpu)
				-------------------------------
4a3	wait A irq's
432	init the Ath sprite
569	reads a sequence from e803
607	prints the Ath string (FF terminated). String info is stored at
	65bc in the form of: attribute, dest. address, string address (5 bytes)
b73	checks lives. If zero, writes 0 to port 04 then jp 0000h.
	Before that, sets I to FF as a flag, for the startup ram check
	routine, to not alter the credit counter.
1523	put name in hi-scores?

-------------------------------Performan-----------------------------------
                 Interesting RAM locations (Main CPU).
                 -------------------------------------

$8056            Hero counter
$8057            Level counter
$8006 - $8035    High score table
$8609 - $860f    High score characters to display to screen for highest score


***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class slapfght
{
	
	/* VIDHRDW */
	
	/* MACHINE */
	
	
	/* MCU */
	
	
	
	
	
	public static Memory_ReadAddress perfrman_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new Memory_ReadAddress( 0x8800, 0x880f, slapfight_dpram_r ),
		new Memory_ReadAddress( 0x8810, 0x8fff, MRA_BANK1 ),
		new Memory_ReadAddress( 0x9000, 0x97ff, MRA_RAM ),
		new Memory_ReadAddress( 0x9800, 0x9fff, MRA_RAM ),
		new Memory_ReadAddress( 0xa000, 0xa7ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress perfrman_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x87ff, MWA_RAM ),
		new Memory_WriteAddress( 0x8800, 0x880f, slapfight_dpram_w, slapfight_dpram, slapfight_dpram_size ),
		new Memory_WriteAddress( 0x8810, 0x8fff, MWA_BANK1 ),	/* Shared RAM with sound CPU */
		new Memory_WriteAddress( 0x9000, 0x97ff, slapfight_videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x9800, 0x9fff, slapfight_colorram_w, colorram ),
		new Memory_WriteAddress( 0xa000, 0xa7ff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress tigerh_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xc7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xc800, 0xc80f, slapfight_dpram_r ),
		new Memory_ReadAddress( 0xc810, 0xcfff, MRA_RAM ),
		new Memory_ReadAddress( 0xd000, 0xd7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xd800, 0xdfff, MRA_RAM ),
		new Memory_ReadAddress( 0xf000, 0xf7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xc000, 0xc7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xc800, 0xc80f, slapfight_dpram_r ),
		new Memory_ReadAddress( 0xc810, 0xcfff, MRA_RAM ),
		new Memory_ReadAddress( 0xd000, 0xd7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xd800, 0xdfff, MRA_RAM ),
		new Memory_ReadAddress( 0xe000, 0xe7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xe803, 0xe803, getstar_e803_r ),
		new Memory_ReadAddress( 0xf000, 0xf7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xc7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xc800, 0xc80f, slapfight_dpram_w, slapfight_dpram, slapfight_dpram_size ),
		new Memory_WriteAddress( 0xc810, 0xcfff, MWA_RAM ),
		new Memory_WriteAddress( 0xd000, 0xd7ff, slapfight_videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0xd800, 0xdfff, slapfight_colorram_w, colorram ),
		new Memory_WriteAddress( 0xe000, 0xe7ff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0xe800, 0xe800, MWA_RAM, slapfight_scrollx_lo ),
		new Memory_WriteAddress( 0xe801, 0xe801, MWA_RAM, slapfight_scrollx_hi ),
		new Memory_WriteAddress( 0xe802, 0xe802, MWA_RAM, slapfight_scrolly ),
		new Memory_WriteAddress( 0xf000, 0xf7ff, slapfight_fixram_w, slapfight_videoram, slapfight_videoram_size ),
		new Memory_WriteAddress( 0xf800, 0xffff, slapfight_fixcol_w, slapfight_colorram ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress slapbtuk_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xc7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xc800, 0xc80f, slapfight_dpram_w, slapfight_dpram, slapfight_dpram_size ),
		new Memory_WriteAddress( 0xc810, 0xcfff, MWA_RAM ),
		new Memory_WriteAddress( 0xd000, 0xd7ff, slapfight_videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0xd800, 0xdfff, slapfight_colorram_w, colorram ),
		new Memory_WriteAddress( 0xe000, 0xe7ff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0xe800, 0xe800, MWA_RAM, slapfight_scrollx_hi ),
		new Memory_WriteAddress( 0xe802, 0xe802, MWA_RAM, slapfight_scrolly ),
		new Memory_WriteAddress( 0xe803, 0xe803, MWA_RAM, slapfight_scrollx_lo ),
		new Memory_WriteAddress( 0xf000, 0xf7ff, slapfight_fixram_w, slapfight_videoram, slapfight_videoram_size ),
		new Memory_WriteAddress( 0xf800, 0xffff, slapfight_fixcol_w, slapfight_colorram ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, slapfight_port_00_r ),	/* status register */
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static ReadHandlerPtr tigerh_status_r  = new ReadHandlerPtr() { public int handler(int offset){
		return (slapfight_port_00_r(0)&0xf9)| ((tigerh_mcu_status_r(0)));
	} };
	
	public static IO_ReadPort tigerh_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, tigerh_status_r ),	/* status register */
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress m68705_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x0000, tigerh_68705_portA_r ),
		new Memory_ReadAddress( 0x0001, 0x0001, tigerh_68705_portB_r ),
		new Memory_ReadAddress( 0x0002, 0x0002, tigerh_68705_portC_r ),
		new Memory_ReadAddress( 0x0010, 0x007f, MRA_RAM ),
		new Memory_ReadAddress( 0x0080, 0x07ff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress m68705_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x0000, tigerh_68705_portA_w ),
		new Memory_WriteAddress( 0x0001, 0x0001, tigerh_68705_portB_w ),
		new Memory_WriteAddress( 0x0002, 0x0002, tigerh_68705_portC_w ),
		new Memory_WriteAddress( 0x0004, 0x0004, tigerh_68705_ddrA_w ),
		new Memory_WriteAddress( 0x0005, 0x0005, tigerh_68705_ddrB_w ),
		new Memory_WriteAddress( 0x0006, 0x0006, tigerh_68705_ddrC_w ),
		new Memory_WriteAddress( 0x0010, 0x007f, MWA_RAM ),
		new Memory_WriteAddress( 0x0080, 0x07ff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort tigerh_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, slapfight_port_00_w ),
		new IO_WritePort( 0x01, 0x01, slapfight_port_01_w ),
		new IO_WritePort( 0x02, 0x03, slapfight_flipscreen_w ),
		new IO_WritePort( 0x06, 0x06, slapfight_port_06_w ),
		new IO_WritePort( 0x07, 0x07, slapfight_port_07_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, slapfight_port_00_w ),
		new IO_WritePort( 0x01, 0x01, slapfight_port_01_w ),
		new IO_WritePort( 0x02, 0x03, slapfight_flipscreen_w ),
	//	new IO_WritePort( 0x04, 0x04, getstar_port_04_w   ),
		new IO_WritePort( 0x06, 0x06, slapfight_port_06_w ),
		new IO_WritePort( 0x07, 0x07, slapfight_port_07_w ),
		new IO_WritePort( 0x08, 0x08, slapfight_port_08_w ),	/* select bank 0 */
		new IO_WritePort( 0x09, 0x09, slapfight_port_09_w ),	/* select bank 1 */
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress perfrman_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8800, 0x880f, slapfight_dpram_r ),
		new Memory_ReadAddress( 0x8810, 0x8fff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xa081, 0xa081, AY8910_read_port_0_r ),
		new Memory_ReadAddress( 0xa091, 0xa091, AY8910_read_port_1_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress perfrman_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8800, 0x880f, slapfight_dpram_w ),
		new Memory_WriteAddress( 0x8810, 0x8fff, MWA_BANK1 ),	/* Shared RAM with main CPU */
		new Memory_WriteAddress( 0xa080, 0xa080, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0xa082, 0xa082, AY8910_write_port_0_w ),
		new Memory_WriteAddress( 0xa090, 0xa090, AY8910_control_port_1_w ),
		new Memory_WriteAddress( 0xa092, 0xa092, AY8910_write_port_1_w ),
		new Memory_WriteAddress( 0xa0e0, 0xa0e0, getstar_sh_intenable_w ), /* maybe a0f0 also -LE */
	//	new Memory_WriteAddress( 0xa0f0, 0xa0f0, MWA_NOP ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new Memory_ReadAddress( 0xa081, 0xa081, AY8910_read_port_0_r ),
		new Memory_ReadAddress( 0xa091, 0xa091, AY8910_read_port_1_r ),
		new Memory_ReadAddress( 0xc800, 0xc80f, slapfight_dpram_r ),
		new Memory_ReadAddress( 0xc810, 0xcfff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new Memory_WriteAddress( 0xa080, 0xa080, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0xa082, 0xa082, AY8910_write_port_0_w ),
		new Memory_WriteAddress( 0xa090, 0xa090, AY8910_control_port_1_w ),
		new Memory_WriteAddress( 0xa092, 0xa092, AY8910_write_port_1_w ),
		new Memory_WriteAddress( 0xa0e0, 0xa0e0, getstar_sh_intenable_w ), /* maybe a0f0 also -LE */
		new Memory_WriteAddress( 0xc800, 0xc80f, slapfight_dpram_w ),
		new Memory_WriteAddress( 0xc810, 0xcfff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortHandlerPtr input_ports_perfrman = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( perfrman )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START();   /* DSW1 */
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BITX(    0x40, 0x40, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Dipswitch Test", KEYCODE_F2, IP_JOY_NONE );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		/* Actually, the following DIPSW doesnt seem to do anything */
		PORT_BITX(    0x20, 0x20, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Screen Test", KEYCODE_F1, IP_JOY_NONE );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x01, DEF_STR( "3C_1C") );
	//	PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
	
		PORT_START();   /* DSW2 */
		PORT_DIPNAME( 0xf0, 0x70, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0xb0, "20k, then each 100k" );
		PORT_DIPSETTING(    0xa0, "40k, then each 100k" );
		PORT_DIPSETTING(    0x90, "60k, then each 100k" );
		PORT_DIPSETTING(    0x70, "20k, then each 200k" );
		PORT_DIPSETTING(    0x60, "40k, then each 200k" );
		PORT_DIPSETTING(    0x50, "60k, then each 200k" );
		PORT_DIPSETTING(    0x30, "20k, then each 300k" );
		PORT_DIPSETTING(    0x20, "40k, then each 300k" );
		PORT_DIPSETTING(    0x10, "60k, then each 300k" );
		PORT_DIPSETTING(    0xf0, "20k" );
		PORT_DIPSETTING(    0xe0, "40k" );
		PORT_DIPSETTING(    0xd0, "60k" );
		PORT_DIPSETTING(    0xc0, "None" );
		PORT_DIPNAME( 0x0c, 0x0c, "Game Level" );
		PORT_DIPSETTING(    0x0c, "0" );
		PORT_DIPSETTING(    0x08, "1" );
		PORT_DIPSETTING(    0x04, "2" );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x01, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x02, "5" );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_tigerh = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( tigerh )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START();   /* DSW1 */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x01, DEF_STR( "3C_1C") );
	//	PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BITX(    0x40, 0x40, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Dipswitch Test", KEYCODE_F2, IP_JOY_NONE );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Player Speed" );
		PORT_DIPSETTING(    0x80, "Normal" );
		PORT_DIPSETTING(    0x00, "Fast" );
	
		PORT_START();   /* DSW2 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x01, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x0c, "Easy" );
		PORT_DIPSETTING(    0x08, "Medium" );
		PORT_DIPSETTING(    0x04, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x10, "20000 80000" );
		PORT_DIPSETTING(    0x00, "50000 120000" );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_slapfigh = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( slapfigh )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START();   /* DSW1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x02, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_BITX(    0x20, 0x20, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Screen Test", KEYCODE_F1, IP_JOY_NONE );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Cocktail") );
	
		PORT_START();   /* DSW2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BITX(    0x02, 0x02, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Dipswitch Test", KEYCODE_F2, IP_JOY_NONE );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x08, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x0c, "3" );
		PORT_DIPSETTING(    0x04, "5" );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x30, "30000 100000" );
		PORT_DIPSETTING(    0x10, "50000 200000" );
		PORT_DIPSETTING(    0x20, "50000" );
		PORT_DIPSETTING(    0x00, "100000" );
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x40, "Easy" );
		PORT_DIPSETTING(    0xc0, "Medium" );
		PORT_DIPSETTING(    0x80, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
	INPUT_PORTS_END(); }}; 
	
	
	static InputPortHandlerPtr input_ports_getstar = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( getstar )
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START();   /* DSW1 */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x01, DEF_STR( "3C_1C") );
	//	PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BITX(    0x40, 0x40, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Dipswitch Test", KEYCODE_F2, IP_JOY_NONE );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START();   /* DSW2 */
		PORT_DIPNAME( 0x03, 0x02, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x02, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_BITX( 0,       0x03, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "240", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x0c, "Easy" );
		PORT_DIPSETTING(    0x08, "Medium" );
		PORT_DIPSETTING(    0x04, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x10, "30000 100000" );
		PORT_DIPSETTING(    0x00, "50000 150000" );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,			/* 8*8 characters */
		RGN_FRAC(1,2),	/* 1024 characters */
		2,				/* 2 bits per pixel */
		new int[] { RGN_FRAC(0,2), RGN_FRAC(1,2) },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8     /* every char takes 8 consecutive bytes */
	);
	
	static GfxLayout tilelayout = new GfxLayout
	(
		8,8,			/* 8*8 tiles */
		RGN_FRAC(1,4),	/* 2048/4096 tiles */
		4,				/* 4 bits per pixel */
		new int[] { RGN_FRAC(0,4), RGN_FRAC(1,4), RGN_FRAC(2,4), RGN_FRAC(3,4) },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8    /* every tile takes 8 consecutive bytes */
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,			/* 16*16 sprites */
		RGN_FRAC(1,4),	/* 512/1024 sprites */
		4,				/* 4 bits per pixel */
		new int[] { RGN_FRAC(0,4), RGN_FRAC(1,4), RGN_FRAC(2,4), RGN_FRAC(3,4) },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8,
				9, 10 ,11, 12, 13, 14, 15 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		32*8    /* every sprite takes 64 consecutive bytes */
	);
	
	static GfxLayout perfrman_charlayout = new GfxLayout
	(
		8,8,			/* 8*8 characters */
		RGN_FRAC(1,3),	/* 1024 characters */
		3,				/* 3 bits per pixel */
		new int[] { RGN_FRAC(0,3), RGN_FRAC(1,3), RGN_FRAC(2,3) },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8     /* every char takes 8 consecutive bytes */
	);
	
	static GfxLayout perfrman_spritelayout = new GfxLayout
	(
		16,16,			/* 16*16 sprites */
		RGN_FRAC(1,3),	/* 256 sprites */
		3,				/* 3 bits per pixel */
		new int[] { RGN_FRAC(0,3), RGN_FRAC(1,3), RGN_FRAC(2,3) },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8,
				9, 10 ,11, 12, 13, 14, 15 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		32*8
	);
	
	
	static GfxDecodeInfo perfrman_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, perfrman_charlayout,     0, 16 ),
		new GfxDecodeInfo( REGION_GFX2, 0, perfrman_spritelayout, 128, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,   0,  64 ),
		new GfxDecodeInfo( REGION_GFX2, 0, tilelayout,   0,  16 ),
		new GfxDecodeInfo( REGION_GFX3, 0, spritelayout, 0,  16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		2,			/* 2 chips */
		1500000,	/* 1.5 MHz ? */
		new int[] { 25, 25 },
		new ReadHandlerPtr[] { input_port_0_r, input_port_2_r },
		new ReadHandlerPtr[] { input_port_1_r, input_port_3_r },
		new WriteHandlerPtr[] { 0, 0 },
		new WriteHandlerPtr[] { 0, 0 }
	);
	
	static AY8910interface perfrman_ay8910_interface = new AY8910interface
	(
		2,				/* 2 chips */
		16000000/8,		/* 2MHz ???, 16MHz Oscillator */
		new ReadHandlerPtr[] { 25, 25 },
		new ReadHandlerPtr[] { input_port_0_r, input_port_2_r },
		new WriteHandlerPtr[] { input_port_1_r, input_port_3_r },
		new WriteHandlerPtr[] { 0, 0 },
		{ 0, 0 }
	);
	
	static public static VideoEofHandlerPtr video_eof_perfrman  = new VideoEofHandlerPtr() { public void handler(){
		buffer_spriteram_w(0,0);
	} };
	
	public static MachineHandlerPtr machine_driver_perfrman = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80,16000000/4)			/* 4MHz ???, 16MHz Oscillator */
		MDRV_CPU_MEMORY(perfrman_readmem,perfrman_writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80,16000000/8)			/* 2MHz ???, 16MHz Oscillator */
		MDRV_CPU_MEMORY(perfrman_sound_readmem,perfrman_sound_writemem)
		MDRV_CPU_VBLANK_INT(getstar_interrupt,6)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(10)		/* 10 CPU slices per frame - enough for the sound CPU to read all commands */
	
		MDRV_MACHINE_INIT(slapfight)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_BUFFERS_SPRITERAM)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(1*8, 34*8-1, 2*8, 32*8-1)
		MDRV_GFXDECODE(perfrman_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_PALETTE_INIT(RRRR_GGGG_BBBB)
		MDRV_VIDEO_START(perfrman)
		MDRV_VIDEO_EOF(perfrman)
		MDRV_VIDEO_UPDATE(perfrman)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, perfrman_ay8910_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	public static MachineHandlerPtr machine_driver_tigerhb = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 6000000)
		MDRV_CPU_MEMORY(tigerh_readmem,writemem)
		MDRV_CPU_PORTS(readport,tigerh_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 6000000)
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,6)    /* ??? */
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(10)	/* 10 CPU slices per frame - enough for the sound CPU to read all commands */
	
		MDRV_MACHINE_INIT(slapfight)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_BUFFERS_SPRITERAM)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(1*8, 36*8-1, 2*8, 32*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_PALETTE_INIT(RRRR_GGGG_BBBB)
		MDRV_VIDEO_START(slapfight)
		MDRV_VIDEO_EOF(perfrman)
		MDRV_VIDEO_UPDATE(slapfight)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	MACHINE_DRIVER_END();
 }
};
	
	public static MachineHandlerPtr machine_driver_tigerh = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 6000000)
		MDRV_CPU_MEMORY(tigerh_readmem,writemem)
		MDRV_CPU_PORTS(tigerh_readport,tigerh_writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 6000000)
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_VBLANK_INT(nmi_line_pulse,6)    /* ??? */
	
		MDRV_CPU_ADD(M68705,4000000/2)
		MDRV_CPU_MEMORY(m68705_readmem,m68705_writemem)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(10)	/* 10 CPU slices per frame - enough for the sound CPU to read all commands */
	
		MDRV_MACHINE_INIT(slapfight)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_BUFFERS_SPRITERAM)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(1*8, 36*8-1, 2*8, 32*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_PALETTE_INIT(RRRR_GGGG_BBBB)
		MDRV_VIDEO_START(slapfight)
		MDRV_VIDEO_EOF(perfrman)
		MDRV_VIDEO_UPDATE(slapfight)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	
	MACHINE_DRIVER_END();
 }
};
	
	public static MachineHandlerPtr machine_driver_slapfigh = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main",Z80, 6000000)
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_CPU_ADD(Z80, 6000000)
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_VBLANK_INT(getstar_interrupt, 3)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(10)	/* 10 CPU slices per frame - enough for the sound CPU to read all commands */
	
		MDRV_MACHINE_INIT(slapfight)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_BUFFERS_SPRITERAM)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(1*8, 36*8-1, 2*8, 32*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(256)
	
		MDRV_PALETTE_INIT(RRRR_GGGG_BBBB)
		MDRV_VIDEO_START(slapfight)
		MDRV_VIDEO_EOF(perfrman)
		MDRV_VIDEO_UPDATE(slapfight)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	/* identical to slapfigh_ but writemem has different scroll registers */
	public static MachineHandlerPtr machine_driver_slapbtuk = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(slapfigh)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(readmem,slapbtuk_writemem)
	MACHINE_DRIVER_END();
 }
};
	
	
	static RomLoadHandlerPtr rom_perfrman = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )				 /* Main CPU code */
		ROM_LOAD( "ci07.0",    0x00000, 0x4000, CRC(7ad32eea) SHA1(e5b29793e9c8c5c9322ca2af468a9810a598c0ae) )
		ROM_LOAD( "ci08.1",    0x04000, 0x4000, CRC(90a02d5f) SHA1(9f2d2ce70a5bc96fc9d268e2b24533f73361225c) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )				 /* Sound CPU code */
		ROM_LOAD( "ci06.4",    0x0000, 0x2000, CRC(df891ad0) SHA1(0d33e7d0562831382f48d1588ef20a1bc73be71a) )
	
		ROM_REGION( 0x6000, REGION_GFX1, ROMREGION_DISPOSE ) /* Tiles */
		ROM_LOAD( "ci02.7",     0x0000, 0x2000, CRC(8efa960a) SHA1(d547ea23f2dd622500bf3f38cd9aca4e80aa27ca) )
		ROM_LOAD( "ci01.6",     0x2000, 0x2000, CRC(2e8e69df) SHA1(183c1868f0c94a2a82709f9c38020ee81c283051) )
		ROM_LOAD( "ci00.5",     0x4000, 0x2000, CRC(79e191f8) SHA1(3a755857dab147b73761aebfcf931dc3c87286a4) )
	
		ROM_REGION( 0x6000, REGION_GFX2, ROMREGION_DISPOSE ) /* Sprites */
		ROM_LOAD( "ci05.10",    0x0000, 0x2000, CRC(809a4ccc) SHA1(bca5a27abe205a65e1160d0c0c61e9831a949acc) )
		ROM_LOAD( "ci04.9",     0x2000, 0x2000, CRC(026f27b3) SHA1(a222d31368fa5117824f5a14a1e52f01326e1f63) )
		ROM_LOAD( "ci03.8",     0x4000, 0x2000, CRC(6410d9eb) SHA1(7e57de9255cbcacb4610cabb1364e2a4933ec12b) )
	
		ROM_REGION( 0x300, REGION_PROMS, 0 )				 /* Color BPROMs */
		ROM_LOAD( "ci14.16",    0x000, 0x0100, CRC(515f8a3b) SHA1(a99d4c119f6c4c6cd1b3fd208eadfb69ef7e8e2d) )
		ROM_LOAD( "ci13.15",    0x100, 0x0100, CRC(a9a397eb) SHA1(a84cf23efa0cf3e97b8dd1fff868c85d7eda1253) )
		ROM_LOAD( "ci12.14",    0x200, 0x0100, CRC(67f86e3d) SHA1(b1240212ea91cf451dbd7c6e2bfccbac76568cf6) )
	
		ROM_REGION( 0x220, REGION_USER1, 0 )
		ROM_LOAD( "ci11.11",    0x000, 0x0100, CRC(d492e6c2) SHA1(5789adda3a63ef8656ebd012416fcf3f991241fe) )
		ROM_LOAD( "ci10.12",    0x100, 0x0100, CRC(59490887) SHA1(c894edecbcfc67972ad893cd7c8197d07862a20a) )
		ROM_LOAD( "ci09.13",    0x200, 0x0020, CRC(aa0ca5a5) SHA1(4c45be71658f40ebb05634febba5822f1a8a7f79) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_perfrmau = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )				 /* Main CPU code */
		ROM_LOAD( "ci07.0",    0x00000, 0x4000, CRC(7ad32eea) SHA1(e5b29793e9c8c5c9322ca2af468a9810a598c0ae) )
		ROM_LOAD( "ci108r5.1", 0x04000, 0x4000, CRC(9d373efa) SHA1(b1d87e033ee3c50cfc56db05891b00b7bc236733) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )				 /* Sound CPU code */
		ROM_LOAD( "ci06.4",    0x0000, 0x2000, CRC(df891ad0) SHA1(0d33e7d0562831382f48d1588ef20a1bc73be71a) )
	
		ROM_REGION( 0x6000, REGION_GFX1, ROMREGION_DISPOSE ) /* Tiles */
		ROM_LOAD( "ci02.7",     0x0000, 0x2000, CRC(8efa960a) SHA1(d547ea23f2dd622500bf3f38cd9aca4e80aa27ca) )
		ROM_LOAD( "ci01.6",     0x2000, 0x2000, CRC(2e8e69df) SHA1(183c1868f0c94a2a82709f9c38020ee81c283051) )
		ROM_LOAD( "ci00.5",     0x4000, 0x2000, CRC(79e191f8) SHA1(3a755857dab147b73761aebfcf931dc3c87286a4) )
	
		ROM_REGION( 0x6000, REGION_GFX2, ROMREGION_DISPOSE ) /* Sprites */
		ROM_LOAD( "ci05.10",    0x0000, 0x2000, CRC(809a4ccc) SHA1(bca5a27abe205a65e1160d0c0c61e9831a949acc) )
		ROM_LOAD( "ci04.9",     0x2000, 0x2000, CRC(026f27b3) SHA1(a222d31368fa5117824f5a14a1e52f01326e1f63) )
		ROM_LOAD( "ci03.8",     0x4000, 0x2000, CRC(6410d9eb) SHA1(7e57de9255cbcacb4610cabb1364e2a4933ec12b) )
	
		ROM_REGION( 0x300, REGION_PROMS, 0 )				 /* Color BPROMs */
		ROM_LOAD( "ci14.16",    0x000, 0x0100, CRC(515f8a3b) SHA1(a99d4c119f6c4c6cd1b3fd208eadfb69ef7e8e2d) )
		ROM_LOAD( "ci13.15",    0x100, 0x0100, CRC(a9a397eb) SHA1(a84cf23efa0cf3e97b8dd1fff868c85d7eda1253) )
		ROM_LOAD( "ci12.14",    0x200, 0x0100, CRC(67f86e3d) SHA1(b1240212ea91cf451dbd7c6e2bfccbac76568cf6) )
	
		ROM_REGION( 0x220, REGION_USER1, 0 )
		ROM_LOAD( "ci11.11",    0x000, 0x0100, CRC(d492e6c2) SHA1(5789adda3a63ef8656ebd012416fcf3f991241fe) )
		ROM_LOAD( "ci10.12",    0x100, 0x0100, CRC(59490887) SHA1(c894edecbcfc67972ad893cd7c8197d07862a20a) )
		ROM_LOAD( "ci09r1.13",  0x200, 0x0020, CRC(d9e92f6f) SHA1(7dc2939267b7d2b1eeeca906cc6151fab2cf1cc4) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_tigerh = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )
		ROM_LOAD( "0.4",          0x00000, 0x4000, CRC(4be73246) SHA1(a6f6a36fa7e3d269b87b777c0975b210d8b53483) )
		ROM_LOAD( "1.4",          0x04000, 0x4000, CRC(aad04867) SHA1(5e9ff3c982afe104428e936ef417de2d238dc033) )
		ROM_LOAD( "2.4",          0x08000, 0x4000, CRC(4843f15c) SHA1(c0c145c9df9d6273171ac64fb7396e65a786f67c) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for the audio CPU */
		ROM_LOAD( "a47_03.bin",   0x0000,  0x2000, CRC(d105260f) SHA1(f6a0e393e29354bb37fb723828f3267d030a45ea) )
	
		ROM_REGION( 0x0800, REGION_CPU3, 0 )
		ROM_LOAD( "a47_14.6a",   0x0000, 0x0800, CRC(4042489f) SHA1(b977e0821b6b1aa5a0a0f349cd78150af1a231df) )
	
		ROM_REGION( 0x04000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "a47_05.bin",   0x00000, 0x2000, CRC(c5325b49) SHA1(6df9051e7545dcac4995340f80957510457aaf64) )  /* Chars */
		ROM_LOAD( "a47_04.bin",   0x02000, 0x2000, CRC(cd59628e) SHA1(7be6479f20eb51b79b93e6fd65ab219096d54984) )
	
		ROM_REGION( 0x10000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "a47_09.bin",   0x00000, 0x4000, CRC(31fae8a8) SHA1(ef8c23776431f00a74b25c5800755b6fa8d585ec) )  /* Tiles */
		ROM_LOAD( "a47_08.bin",   0x04000, 0x4000, CRC(e539af2b) SHA1(0c8369a0fac1cbe40c07b51e16e8f8a9b8ed03b8) )
		ROM_LOAD( "a47_07.bin",   0x08000, 0x4000, CRC(02fdd429) SHA1(fa392f2e57cfb6af4c124e0c151a4652f83e5577) )
		ROM_LOAD( "a47_06.bin",   0x0c000, 0x4000, CRC(11fbcc8c) SHA1(b4fdb9ee00b749e1a54cfc0cdf55cc5e9bee3662) )
	
		ROM_REGION( 0x10000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "a47_13.bin",   0x00000, 0x4000, CRC(739a7e7e) SHA1(5fee71d9e1540903a6cf7bcaab30acaa088d35ed) )  /* Sprites */
		ROM_LOAD( "a47_12.bin",   0x04000, 0x4000, CRC(c064ecdb) SHA1(fa8d712e2b2bda78b9375d96c93a4d7549c94075) )
		ROM_LOAD( "a47_11.bin",   0x08000, 0x4000, CRC(744fae9b) SHA1(b324350469c51043e1d90ce58808d966467435b9) )
		ROM_LOAD( "a47_10.bin",   0x0c000, 0x4000, CRC(e1cf844e) SHA1(eeb8eff09f96c693e147d155a8c0a87416d64603) )
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 )
		ROM_LOAD( "82s129.12q",   0x0000,  0x0100, CRC(2c69350d) SHA1(658bf63c6d1e718f99494cd1c9346c3622913beb) )
		ROM_LOAD( "82s129.12m",   0x0100,  0x0100, CRC(7142e972) SHA1(4a854c2fdd006077aecb695832110ae6bf5819c1) )
		ROM_LOAD( "82s129.12n",   0x0200,  0x0100, CRC(25f273f2) SHA1(2c696745f42fa09b64295a39536aeba08ab58d67) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_tigerh2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )
		ROM_LOAD( "b0.5",         0x00000, 0x4000, CRC(6ae7e13c) SHA1(47ef34635f8648e883a850293d92a46e95976a50) )
		ROM_LOAD( "a47_01.bin",   0x04000, 0x4000, CRC(65df2152) SHA1(8e1516905a4af379cb0d0b9d42ff1cc3179c3589) )
		ROM_LOAD( "a47_02.bin",   0x08000, 0x4000, CRC(633d324b) SHA1(70a17d17ebe003bfb2246e92e925a343a92553e5) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for the audio CPU */
		ROM_LOAD( "a47_03.bin",   0x0000,  0x2000, CRC(d105260f) SHA1(f6a0e393e29354bb37fb723828f3267d030a45ea) )
	
		ROM_REGION( 0x0800, REGION_CPU3, 0 )
		/* is this the right mcu for this set? the mcu handling code in the roms seems patched and it doesn't
		   work correctly */
		ROM_LOAD( "a47_14.6a",   0x0000, 0x0800, CRC(4042489f) SHA1(b977e0821b6b1aa5a0a0f349cd78150af1a231df) )
	
		ROM_REGION( 0x04000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "a47_05.bin",   0x00000, 0x2000, CRC(c5325b49) SHA1(6df9051e7545dcac4995340f80957510457aaf64) )  /* Chars */
		ROM_LOAD( "a47_04.bin",   0x02000, 0x2000, CRC(cd59628e) SHA1(7be6479f20eb51b79b93e6fd65ab219096d54984) )
	
		ROM_REGION( 0x10000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "a47_09.bin",   0x00000, 0x4000, CRC(31fae8a8) SHA1(ef8c23776431f00a74b25c5800755b6fa8d585ec) )  /* Tiles */
		ROM_LOAD( "a47_08.bin",   0x04000, 0x4000, CRC(e539af2b) SHA1(0c8369a0fac1cbe40c07b51e16e8f8a9b8ed03b8) )
		ROM_LOAD( "a47_07.bin",   0x08000, 0x4000, CRC(02fdd429) SHA1(fa392f2e57cfb6af4c124e0c151a4652f83e5577) )
		ROM_LOAD( "a47_06.bin",   0x0c000, 0x4000, CRC(11fbcc8c) SHA1(b4fdb9ee00b749e1a54cfc0cdf55cc5e9bee3662) )
	
		ROM_REGION( 0x10000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "a47_13.bin",   0x00000, 0x4000, CRC(739a7e7e) SHA1(5fee71d9e1540903a6cf7bcaab30acaa088d35ed) )  /* Sprites */
		ROM_LOAD( "a47_12.bin",   0x04000, 0x4000, CRC(c064ecdb) SHA1(fa8d712e2b2bda78b9375d96c93a4d7549c94075) )
		ROM_LOAD( "a47_11.bin",   0x08000, 0x4000, CRC(744fae9b) SHA1(b324350469c51043e1d90ce58808d966467435b9) )
		ROM_LOAD( "a47_10.bin",   0x0c000, 0x4000, CRC(e1cf844e) SHA1(eeb8eff09f96c693e147d155a8c0a87416d64603) )
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 )
		ROM_LOAD( "82s129.12q",   0x0000,  0x0100, CRC(2c69350d) SHA1(658bf63c6d1e718f99494cd1c9346c3622913beb) )
		ROM_LOAD( "82s129.12m",   0x0100,  0x0100, CRC(7142e972) SHA1(4a854c2fdd006077aecb695832110ae6bf5819c1) )
		ROM_LOAD( "82s129.12n",   0x0200,  0x0100, CRC(25f273f2) SHA1(2c696745f42fa09b64295a39536aeba08ab58d67) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_tigerhj = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )
		ROM_LOAD( "a47_00.bin",   0x00000, 0x4000, CRC(cbdbe3cc) SHA1(5badf76cdf4a7f0ae9e85ee602420ba5c128efef) )
		ROM_LOAD( "a47_01.bin",   0x04000, 0x4000, CRC(65df2152) SHA1(8e1516905a4af379cb0d0b9d42ff1cc3179c3589) )
		ROM_LOAD( "a47_02.bin",   0x08000, 0x4000, CRC(633d324b) SHA1(70a17d17ebe003bfb2246e92e925a343a92553e5) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for the audio CPU */
		ROM_LOAD( "a47_03.bin",   0x0000,  0x2000, CRC(d105260f) SHA1(f6a0e393e29354bb37fb723828f3267d030a45ea) )
	
		ROM_REGION( 0x0800, REGION_CPU3, 0 )
		ROM_LOAD( "a47_14.6a",   0x0000, 0x0800, CRC(4042489f) SHA1(b977e0821b6b1aa5a0a0f349cd78150af1a231df) )
	
		ROM_REGION( 0x04000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "a47_05.bin",   0x00000, 0x2000, CRC(c5325b49) SHA1(6df9051e7545dcac4995340f80957510457aaf64) )  /* Chars */
		ROM_LOAD( "a47_04.bin",   0x02000, 0x2000, CRC(cd59628e) SHA1(7be6479f20eb51b79b93e6fd65ab219096d54984) )
	
		ROM_REGION( 0x10000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "a47_09.bin",   0x00000, 0x4000, CRC(31fae8a8) SHA1(ef8c23776431f00a74b25c5800755b6fa8d585ec) )  /* Tiles */
		ROM_LOAD( "a47_08.bin",   0x04000, 0x4000, CRC(e539af2b) SHA1(0c8369a0fac1cbe40c07b51e16e8f8a9b8ed03b8) )
		ROM_LOAD( "a47_07.bin",   0x08000, 0x4000, CRC(02fdd429) SHA1(fa392f2e57cfb6af4c124e0c151a4652f83e5577) )
		ROM_LOAD( "a47_06.bin",   0x0c000, 0x4000, CRC(11fbcc8c) SHA1(b4fdb9ee00b749e1a54cfc0cdf55cc5e9bee3662) )
	
		ROM_REGION( 0x10000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "a47_13.bin",   0x00000, 0x4000, CRC(739a7e7e) SHA1(5fee71d9e1540903a6cf7bcaab30acaa088d35ed) )  /* Sprites */
		ROM_LOAD( "a47_12.bin",   0x04000, 0x4000, CRC(c064ecdb) SHA1(fa8d712e2b2bda78b9375d96c93a4d7549c94075) )
		ROM_LOAD( "a47_11.bin",   0x08000, 0x4000, CRC(744fae9b) SHA1(b324350469c51043e1d90ce58808d966467435b9) )
		ROM_LOAD( "a47_10.bin",   0x0c000, 0x4000, CRC(e1cf844e) SHA1(eeb8eff09f96c693e147d155a8c0a87416d64603) )
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 )
		ROM_LOAD( "82s129.12q",   0x0000,  0x0100, CRC(2c69350d) SHA1(658bf63c6d1e718f99494cd1c9346c3622913beb) )
		ROM_LOAD( "82s129.12m",   0x0100,  0x0100, CRC(7142e972) SHA1(4a854c2fdd006077aecb695832110ae6bf5819c1) )
		ROM_LOAD( "82s129.12n",   0x0200,  0x0100, CRC(25f273f2) SHA1(2c696745f42fa09b64295a39536aeba08ab58d67) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_tigerhb1 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )
		ROM_LOAD( "14",           0x00000, 0x4000, CRC(ca59dd73) SHA1(c07961fcc209ec10ace3830d79c8ccc1cfda9765) )
		ROM_LOAD( "13",           0x04000, 0x4000, CRC(38bd54db) SHA1(75e999f606c410d7481bc4d29c4b523d45847649) )
		ROM_LOAD( "a47_02.bin",   0x08000, 0x4000, CRC(633d324b) SHA1(70a17d17ebe003bfb2246e92e925a343a92553e5) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for the audio CPU */
		ROM_LOAD( "a47_03.bin",   0x0000,  0x2000, CRC(d105260f) SHA1(f6a0e393e29354bb37fb723828f3267d030a45ea) )
	
		ROM_REGION( 0x04000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "a47_05.bin",   0x00000, 0x2000, CRC(c5325b49) SHA1(6df9051e7545dcac4995340f80957510457aaf64) )  /* Chars */
		ROM_LOAD( "a47_04.bin",   0x02000, 0x2000, CRC(cd59628e) SHA1(7be6479f20eb51b79b93e6fd65ab219096d54984) )
	
		ROM_REGION( 0x10000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "a47_09.bin",   0x00000, 0x4000, CRC(31fae8a8) SHA1(ef8c23776431f00a74b25c5800755b6fa8d585ec) )  /* Tiles */
		ROM_LOAD( "a47_08.bin",   0x04000, 0x4000, CRC(e539af2b) SHA1(0c8369a0fac1cbe40c07b51e16e8f8a9b8ed03b8) )
		ROM_LOAD( "a47_07.bin",   0x08000, 0x4000, CRC(02fdd429) SHA1(fa392f2e57cfb6af4c124e0c151a4652f83e5577) )
		ROM_LOAD( "a47_06.bin",   0x0c000, 0x4000, CRC(11fbcc8c) SHA1(b4fdb9ee00b749e1a54cfc0cdf55cc5e9bee3662) )
	
		ROM_REGION( 0x10000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "a47_13.bin",   0x00000, 0x4000, CRC(739a7e7e) SHA1(5fee71d9e1540903a6cf7bcaab30acaa088d35ed) )  /* Sprites */
		ROM_LOAD( "a47_12.bin",   0x04000, 0x4000, CRC(c064ecdb) SHA1(fa8d712e2b2bda78b9375d96c93a4d7549c94075) )
		ROM_LOAD( "a47_11.bin",   0x08000, 0x4000, CRC(744fae9b) SHA1(b324350469c51043e1d90ce58808d966467435b9) )
		ROM_LOAD( "a47_10.bin",   0x0c000, 0x4000, CRC(e1cf844e) SHA1(eeb8eff09f96c693e147d155a8c0a87416d64603) )
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 )
		ROM_LOAD( "82s129.12q",   0x0000,  0x0100, CRC(2c69350d) SHA1(658bf63c6d1e718f99494cd1c9346c3622913beb) )
		ROM_LOAD( "82s129.12m",   0x0100,  0x0100, CRC(7142e972) SHA1(4a854c2fdd006077aecb695832110ae6bf5819c1) )
		ROM_LOAD( "82s129.12n",   0x0200,  0x0100, CRC(25f273f2) SHA1(2c696745f42fa09b64295a39536aeba08ab58d67) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_tigerhb2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )
		ROM_LOAD( "rom00_09.bin", 0x00000, 0x4000, CRC(ef738c68) SHA1(c78c802d885b7f7c5e312ec079d52b8817590735) )
		ROM_LOAD( "a47_01.bin",   0x04000, 0x4000, CRC(65df2152) SHA1(8e1516905a4af379cb0d0b9d42ff1cc3179c3589) )
		ROM_LOAD( "rom02_07.bin", 0x08000, 0x4000, CRC(36e250b9) SHA1(79bd86bde81981e4d0dbee420bc0a10c80b5241e) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for the audio CPU */
		ROM_LOAD( "a47_03.bin",   0x0000,  0x2000, CRC(d105260f) SHA1(f6a0e393e29354bb37fb723828f3267d030a45ea) )
	
		ROM_REGION( 0x04000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "a47_05.bin",   0x00000, 0x2000, CRC(c5325b49) SHA1(6df9051e7545dcac4995340f80957510457aaf64) )  /* Chars */
		ROM_LOAD( "a47_04.bin",   0x02000, 0x2000, CRC(cd59628e) SHA1(7be6479f20eb51b79b93e6fd65ab219096d54984) )
	
		ROM_REGION( 0x10000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "a47_09.bin",   0x00000, 0x4000, CRC(31fae8a8) SHA1(ef8c23776431f00a74b25c5800755b6fa8d585ec) )  /* Tiles */
		ROM_LOAD( "a47_08.bin",   0x04000, 0x4000, CRC(e539af2b) SHA1(0c8369a0fac1cbe40c07b51e16e8f8a9b8ed03b8) )
		ROM_LOAD( "a47_07.bin",   0x08000, 0x4000, CRC(02fdd429) SHA1(fa392f2e57cfb6af4c124e0c151a4652f83e5577) )
		ROM_LOAD( "a47_06.bin",   0x0c000, 0x4000, CRC(11fbcc8c) SHA1(b4fdb9ee00b749e1a54cfc0cdf55cc5e9bee3662) )
	
		ROM_REGION( 0x10000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "a47_13.bin",   0x00000, 0x4000, CRC(739a7e7e) SHA1(5fee71d9e1540903a6cf7bcaab30acaa088d35ed) )  /* Sprites */
		ROM_LOAD( "a47_12.bin",   0x04000, 0x4000, CRC(c064ecdb) SHA1(fa8d712e2b2bda78b9375d96c93a4d7549c94075) )
		ROM_LOAD( "a47_11.bin",   0x08000, 0x4000, CRC(744fae9b) SHA1(b324350469c51043e1d90ce58808d966467435b9) )
		ROM_LOAD( "a47_10.bin",   0x0c000, 0x4000, CRC(e1cf844e) SHA1(eeb8eff09f96c693e147d155a8c0a87416d64603) )
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 )
		ROM_LOAD( "82s129.12q",   0x0000,  0x0100, CRC(2c69350d) SHA1(658bf63c6d1e718f99494cd1c9346c3622913beb) )
		ROM_LOAD( "82s129.12m",   0x0100,  0x0100, CRC(7142e972) SHA1(4a854c2fdd006077aecb695832110ae6bf5819c1) )
		ROM_LOAD( "82s129.12n",   0x0200,  0x0100, CRC(25f273f2) SHA1(2c696745f42fa09b64295a39536aeba08ab58d67) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_slapfigh = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x18000, REGION_CPU1, 0 )
		ROM_LOAD( "sf_r19.bin",   0x00000, 0x8000, CRC(674c0e0f) SHA1(69fc17881c89cc5e82b0fefec49c4116054f9e3b) )
		ROM_LOAD( "sf_rh.bin",    0x10000, 0x8000, CRC(3c42e4a7) SHA1(8e4da1e6e73603e484ba4f5609ac9ea92999a526) )	/* banked at 8000 */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for the audio CPU */
		ROM_LOAD( "sf_r05.bin",   0x0000,  0x2000, CRC(87f4705a) SHA1(a90d5644ce268f3321047a4f96df96ac294d2f1b) )
	
		ROM_REGION( 0x0800, REGION_CPU3, 0 )	/* 2k for the microcontroller */
		ROM_LOAD( "68705.bin",    0x0000,  0x0800, NO_DUMP )
	
		ROM_REGION( 0x04000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "sf_r11.bin",   0x00000, 0x2000, CRC(2ac7b943) SHA1(d0c3560bb1f0c2647aeff807cb4b09450237b955) )  /* Chars */
		ROM_LOAD( "sf_r10.bin",   0x02000, 0x2000, CRC(33cadc93) SHA1(59ffc206c62a651d2ac0ef52f519dd56edf2c021) )
	
		ROM_REGION( 0x20000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "sf_r06.bin",   0x00000, 0x8000, CRC(b6358305) SHA1(c7bb4236a75ec6b88f011bc30f8fb9a718e2ca3e) )  /* Tiles */
		ROM_LOAD( "sf_r09.bin",   0x08000, 0x8000, CRC(e92d9d60) SHA1(2554617e0e6615ca8c85a49299a4a0e762478339) )
		ROM_LOAD( "sf_r08.bin",   0x10000, 0x8000, CRC(5faeeea3) SHA1(696fba24bcf1f3a7e914a4403854da5eededaf7f) )
		ROM_LOAD( "sf_r07.bin",   0x18000, 0x8000, CRC(974e2ea9) SHA1(3840550fc3a833828dad8f3e300d2ea583d69ce7) )
	
		ROM_REGION( 0x20000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "sf_r03.bin",   0x00000, 0x8000, CRC(8545d397) SHA1(9a1fd5bfd8fb830b8e46643c08eef32ba968fc23) )  /* Sprites */
		ROM_LOAD( "sf_r01.bin",   0x08000, 0x8000, CRC(b1b7b925) SHA1(199b0b52bbeb384211171eca5c50a1c0ebf6826f) )
		ROM_LOAD( "sf_r04.bin",   0x10000, 0x8000, CRC(422d946b) SHA1(c251ef9597a11ec8de39be4fcbddaba84e649ef2) )
		ROM_LOAD( "sf_r02.bin",   0x18000, 0x8000, CRC(587113ae) SHA1(90abe961494a1af7c87693a419fbabf7a58a5dee) )
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 )
		ROM_LOAD( "sf_col21.bin", 0x0000,  0x0100, CRC(a0efaf99) SHA1(5df01663480acad1f89abab8662d437617a66d1c) )
		ROM_LOAD( "sf_col20.bin", 0x0100,  0x0100, CRC(a56d57e5) SHA1(bfbd0db52b23fe1b4994e05103be3d412c1c013e) )
		ROM_LOAD( "sf_col19.bin", 0x0200,  0x0100, CRC(5cbf9fbf) SHA1(abfa58fa4e44ebc56f2e0fac9bcc36164c845fa3) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_slapbtjp = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x18000, REGION_CPU1, 0 )
		ROM_LOAD( "sf_r19jb.bin", 0x00000, 0x8000, CRC(9a7ac8b3) SHA1(01fbad9b4fc80f2406eff18db20e196e212d0c17) )
		ROM_LOAD( "sf_rh.bin",    0x10000, 0x8000, CRC(3c42e4a7) SHA1(8e4da1e6e73603e484ba4f5609ac9ea92999a526) )	/* banked at 8000 */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for the audio CPU */
		ROM_LOAD( "sf_r05.bin",   0x0000,  0x2000, CRC(87f4705a) SHA1(a90d5644ce268f3321047a4f96df96ac294d2f1b) )
	
		ROM_REGION( 0x04000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "sf_r11.bin",   0x00000, 0x2000, CRC(2ac7b943) SHA1(d0c3560bb1f0c2647aeff807cb4b09450237b955) )  /* Chars */
		ROM_LOAD( "sf_r10.bin",   0x02000, 0x2000, CRC(33cadc93) SHA1(59ffc206c62a651d2ac0ef52f519dd56edf2c021) )
	
		ROM_REGION( 0x20000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "sf_r06.bin",   0x00000, 0x8000, CRC(b6358305) SHA1(c7bb4236a75ec6b88f011bc30f8fb9a718e2ca3e) )  /* Tiles */
		ROM_LOAD( "sf_r09.bin",   0x08000, 0x8000, CRC(e92d9d60) SHA1(2554617e0e6615ca8c85a49299a4a0e762478339) )
		ROM_LOAD( "sf_r08.bin",   0x10000, 0x8000, CRC(5faeeea3) SHA1(696fba24bcf1f3a7e914a4403854da5eededaf7f) )
		ROM_LOAD( "sf_r07.bin",   0x18000, 0x8000, CRC(974e2ea9) SHA1(3840550fc3a833828dad8f3e300d2ea583d69ce7) )
	
		ROM_REGION( 0x20000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "sf_r03.bin",   0x00000, 0x8000, CRC(8545d397) SHA1(9a1fd5bfd8fb830b8e46643c08eef32ba968fc23) )  /* Sprites */
		ROM_LOAD( "sf_r01.bin",   0x08000, 0x8000, CRC(b1b7b925) SHA1(199b0b52bbeb384211171eca5c50a1c0ebf6826f) )
		ROM_LOAD( "sf_r04.bin",   0x10000, 0x8000, CRC(422d946b) SHA1(c251ef9597a11ec8de39be4fcbddaba84e649ef2) )
		ROM_LOAD( "sf_r02.bin",   0x18000, 0x8000, CRC(587113ae) SHA1(90abe961494a1af7c87693a419fbabf7a58a5dee) )
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 )
		ROM_LOAD( "sf_col21.bin", 0x0000,  0x0100, CRC(a0efaf99) SHA1(5df01663480acad1f89abab8662d437617a66d1c) )
		ROM_LOAD( "sf_col20.bin", 0x0100,  0x0100, CRC(a56d57e5) SHA1(bfbd0db52b23fe1b4994e05103be3d412c1c013e) )
		ROM_LOAD( "sf_col19.bin", 0x0200,  0x0100, CRC(5cbf9fbf) SHA1(abfa58fa4e44ebc56f2e0fac9bcc36164c845fa3) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_slapbtuk = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x18000, REGION_CPU1, 0 )
		ROM_LOAD( "sf_r19eb.bin", 0x00000, 0x4000, CRC(2efe47af) SHA1(69ce3e83a0d8fa5ee4737c741d31cf32db6b9919) )
		ROM_LOAD( "sf_r20eb.bin", 0x04000, 0x4000, CRC(f42c7951) SHA1(d76e7a72f6ced67b550ba68cd42987f7111f5468) )
		ROM_LOAD( "sf_rh.bin",    0x10000, 0x8000, CRC(3c42e4a7) SHA1(8e4da1e6e73603e484ba4f5609ac9ea92999a526) )	/* banked at 8000 */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for the audio CPU */
		ROM_LOAD( "sf_r05.bin",   0x0000,  0x2000, CRC(87f4705a) SHA1(a90d5644ce268f3321047a4f96df96ac294d2f1b) )
	
		ROM_REGION( 0x04000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "sf_r11.bin",   0x00000, 0x2000, CRC(2ac7b943) SHA1(d0c3560bb1f0c2647aeff807cb4b09450237b955) )  /* Chars */
		ROM_LOAD( "sf_r10.bin",   0x02000, 0x2000, CRC(33cadc93) SHA1(59ffc206c62a651d2ac0ef52f519dd56edf2c021) )
	
		ROM_REGION( 0x20000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "sf_r06.bin",   0x00000, 0x8000, CRC(b6358305) SHA1(c7bb4236a75ec6b88f011bc30f8fb9a718e2ca3e) )  /* Tiles */
		ROM_LOAD( "sf_r09.bin",   0x08000, 0x8000, CRC(e92d9d60) SHA1(2554617e0e6615ca8c85a49299a4a0e762478339) )
		ROM_LOAD( "sf_r08.bin",   0x10000, 0x8000, CRC(5faeeea3) SHA1(696fba24bcf1f3a7e914a4403854da5eededaf7f) )
		ROM_LOAD( "sf_r07.bin",   0x18000, 0x8000, CRC(974e2ea9) SHA1(3840550fc3a833828dad8f3e300d2ea583d69ce7) )
	
		ROM_REGION( 0x20000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "sf_r03.bin",   0x00000, 0x8000, CRC(8545d397) SHA1(9a1fd5bfd8fb830b8e46643c08eef32ba968fc23) )  /* Sprites */
		ROM_LOAD( "sf_r01.bin",   0x08000, 0x8000, CRC(b1b7b925) SHA1(199b0b52bbeb384211171eca5c50a1c0ebf6826f) )
		ROM_LOAD( "sf_r04.bin",   0x10000, 0x8000, CRC(422d946b) SHA1(c251ef9597a11ec8de39be4fcbddaba84e649ef2) )
		ROM_LOAD( "sf_r02.bin",   0x18000, 0x8000, CRC(587113ae) SHA1(90abe961494a1af7c87693a419fbabf7a58a5dee) )
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 )
		ROM_LOAD( "sf_col21.bin", 0x0000,  0x0100, CRC(a0efaf99) SHA1(5df01663480acad1f89abab8662d437617a66d1c) )
		ROM_LOAD( "sf_col20.bin", 0x0100,  0x0100, CRC(a56d57e5) SHA1(bfbd0db52b23fe1b4994e05103be3d412c1c013e) )
		ROM_LOAD( "sf_col19.bin", 0x0200,  0x0100, CRC(5cbf9fbf) SHA1(abfa58fa4e44ebc56f2e0fac9bcc36164c845fa3) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_alcon = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x18000, REGION_CPU1, 0 )
		ROM_LOAD( "00",           0x00000, 0x8000, CRC(2ba82d60) SHA1(b37659aa18a3f96a3cc7fa93db2439f36487b8c8) )
		ROM_LOAD( "01",           0x10000, 0x8000, CRC(18bb2f12) SHA1(7c16d4bbb8b5e22f227aff170e5e6326c5968968) )	/* banked at 8000 */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )     /* 64k for the audio CPU */
		ROM_LOAD( "sf_r05.bin",   0x0000,  0x2000, CRC(87f4705a) SHA1(a90d5644ce268f3321047a4f96df96ac294d2f1b) )
	
		ROM_REGION( 0x0800, REGION_CPU3, 0 )	/* 2k for the microcontroller */
		ROM_LOAD( "68705.bin",    0x0000,  0x0800, NO_DUMP )
	
		ROM_REGION( 0x04000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "04",           0x00000, 0x2000, CRC(31003483) SHA1(7014ceb6313ac5a3d2dcb735643dfd8bfabaa185) )  /* Chars */
		ROM_LOAD( "03",           0x02000, 0x2000, CRC(404152c0) SHA1(d05bc9baa1f336475fffc2f19f1018e9f0547f10) )
	
		ROM_REGION( 0x20000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "sf_r06.bin",   0x00000, 0x8000, CRC(b6358305) SHA1(c7bb4236a75ec6b88f011bc30f8fb9a718e2ca3e) )  /* Tiles */
		ROM_LOAD( "sf_r09.bin",   0x08000, 0x8000, CRC(e92d9d60) SHA1(2554617e0e6615ca8c85a49299a4a0e762478339) )
		ROM_LOAD( "sf_r08.bin",   0x10000, 0x8000, CRC(5faeeea3) SHA1(696fba24bcf1f3a7e914a4403854da5eededaf7f) )
		ROM_LOAD( "sf_r07.bin",   0x18000, 0x8000, CRC(974e2ea9) SHA1(3840550fc3a833828dad8f3e300d2ea583d69ce7) )
	
		ROM_REGION( 0x20000, REGION_GFX3, ROMREGION_DISPOSE )
		ROM_LOAD( "sf_r03.bin",   0x00000, 0x8000, CRC(8545d397) SHA1(9a1fd5bfd8fb830b8e46643c08eef32ba968fc23) )  /* Sprites */
		ROM_LOAD( "sf_r01.bin",   0x08000, 0x8000, CRC(b1b7b925) SHA1(199b0b52bbeb384211171eca5c50a1c0ebf6826f) )
		ROM_LOAD( "sf_r04.bin",   0x10000, 0x8000, CRC(422d946b) SHA1(c251ef9597a11ec8de39be4fcbddaba84e649ef2) )
		ROM_LOAD( "sf_r02.bin",   0x18000, 0x8000, CRC(587113ae) SHA1(90abe961494a1af7c87693a419fbabf7a58a5dee) )
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 )
		ROM_LOAD( "sf_col21.bin", 0x0000,  0x0100, CRC(a0efaf99) SHA1(5df01663480acad1f89abab8662d437617a66d1c) )
		ROM_LOAD( "sf_col20.bin", 0x0100,  0x0100, CRC(a56d57e5) SHA1(bfbd0db52b23fe1b4994e05103be3d412c1c013e) )
		ROM_LOAD( "sf_col19.bin", 0x0200,  0x0100, CRC(5cbf9fbf) SHA1(abfa58fa4e44ebc56f2e0fac9bcc36164c845fa3) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_getstar = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x18000, REGION_CPU1, 0 )		/* Region 0 - main cpu code */
		ROM_LOAD( "rom0",         0x00000, 0x4000, CRC(6a8bdc6c) SHA1(c923bca539bd2eb9a34cb9c7a67a199e28bc081a) )
		ROM_LOAD( "rom1",         0x04000, 0x4000, CRC(ebe8db3c) SHA1(9046d6e63c33fc9cbd48b90dcbcc0badf1d3b9ba) )
		ROM_LOAD( "rom2",         0x10000, 0x8000, CRC(343e8415) SHA1(00b98055277a0ddfb7d0bda6537df10a4049533e) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )		/* Region 3 - sound cpu code */
		ROM_LOAD( "a68-03",       0x0000,  0x2000, CRC(18daa44c) SHA1(1a3d22a186c591321d1b836ee30d89fba4771122) )
	
		ROM_REGION( 0x0800, REGION_CPU3, 0 )	/* 2k for the microcontroller */
		ROM_LOAD( "68705.bin",    0x0000,  0x0800, NO_DUMP )
	
		ROM_REGION( 0x04000, REGION_GFX1, ROMREGION_DISPOSE )	/* Region 1 - temporary for gfx */
		ROM_LOAD( "a68_05-1",     0x00000, 0x2000, CRC(06f60107) SHA1(c5dcf0c7a5863ea960ee747d2d7ec7ac8bb7d3af) )  /* Chars */
		ROM_LOAD( "a68_04-1",     0x02000, 0x2000, CRC(1fc8f277) SHA1(59dc1a0fad23b1e98abca3d0b1685b9d2939b059) )
	
		ROM_REGION( 0x20000, REGION_GFX2, ROMREGION_DISPOSE )	/* Region 1 - temporary for gfx */
		ROM_LOAD( "a68_09",       0x00000, 0x8000, CRC(a293cc2e) SHA1(a2c2598e92982d13b51cbb6efb4b963142233433) )  /* Tiles */
		ROM_LOAD( "a68_08",       0x08000, 0x8000, CRC(37662375) SHA1(46ba8a3f0b553d476ecf431d0d20556896b4ca43) )
		ROM_LOAD( "a68_07",       0x10000, 0x8000, CRC(cf1a964c) SHA1(e9223c8d4f3bdafed193a1ded63e377f16f45e17) )
		ROM_LOAD( "a68_06",       0x18000, 0x8000, CRC(05f9eb9a) SHA1(a71640a63b259799086d361ef293aa26cec46a0c) )
	
		ROM_REGION( 0x20000, REGION_GFX3, ROMREGION_DISPOSE )	/* Region 1 - temporary for gfx */
		ROM_LOAD( "a68-13",       0x00000, 0x8000, CRC(643fb282) SHA1(d904d3c27c2b56341929c5eed4ea97e948c53c34) )  /* Sprites */
		ROM_LOAD( "a68-12",       0x08000, 0x8000, CRC(11f74e32) SHA1(02d8b4cc679f45a02c4989f2b62cde91b7418235) )
		ROM_LOAD( "a68-11",       0x10000, 0x8000, CRC(f24158cf) SHA1(db4c6b68a488b0798ea5f793ac8ced283a8ecab2) )
		ROM_LOAD( "a68-10",       0x18000, 0x8000, CRC(83161ed0) SHA1(a6aa28f22f487dc3a2ec07935e6d42bcdd1eff81) )
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 )
		ROM_LOAD( "rom21",        0x0000,  0x0100, CRC(d6360b4d) SHA1(3e64548c82a3378fc091e104cdc2b0c7e592fc44) )
		ROM_LOAD( "rom20",        0x0100,  0x0100, CRC(4ca01887) SHA1(2892c89d5e60f1d10593adffff55c1a9654e8209) )
		ROM_LOAD( "rom19",        0x0200,  0x0100, CRC(513224f0) SHA1(15b34612206138f6fc5f7478925b1fff2ed56aa8) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_getstarj = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x18000, REGION_CPU1, 0 )		/* Region 0 - main cpu code */
		ROM_LOAD( "a68_00.bin",   0x00000, 0x4000, CRC(ad1a0143) SHA1(0d9adeb12bd4d5ad11e5bada0cd7498bc565c1db) )
		ROM_LOAD( "a68_01.bin",   0x04000, 0x4000, CRC(3426eb7c) SHA1(e91db45a650a1bfefd7c12c7553b647bc916c7c8) )
		ROM_LOAD( "a68_02.bin",   0x10000, 0x8000, CRC(3567da17) SHA1(29d698606d0bd30abfc3171d79bfad95b0de89fc) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )		/* Region 3 - sound cpu code */
		ROM_LOAD( "a68-03",       0x00000, 0x2000, CRC(18daa44c) SHA1(1a3d22a186c591321d1b836ee30d89fba4771122) )
	
		ROM_REGION( 0x0800, REGION_CPU3, 0 )	/* 2k for the microcontroller */
		ROM_LOAD( "68705.bin",    0x0000,  0x0800, NO_DUMP )
	
		ROM_REGION( 0x04000, REGION_GFX1, ROMREGION_DISPOSE )	/* Region 1 - temporary for gfx */
		ROM_LOAD( "a68_05.bin",   0x00000, 0x2000, CRC(e3d409e7) SHA1(0b6be4767f110729f4dd1a472ef8d9a0c718b684) )  /* Chars */
		ROM_LOAD( "a68_04.bin",   0x02000, 0x2000, CRC(6e5ac9d4) SHA1(74f90b7a1ceb3b1c2fd92dff100d92dea0155530) )
	
		ROM_REGION( 0x20000, REGION_GFX2, ROMREGION_DISPOSE )	/* Region 1 - temporary for gfx */
		ROM_LOAD( "a68_09",       0x00000, 0x8000, CRC(a293cc2e) SHA1(a2c2598e92982d13b51cbb6efb4b963142233433) )  /* Tiles */
		ROM_LOAD( "a68_08",       0x08000, 0x8000, CRC(37662375) SHA1(46ba8a3f0b553d476ecf431d0d20556896b4ca43) )
		ROM_LOAD( "a68_07",       0x10000, 0x8000, CRC(cf1a964c) SHA1(e9223c8d4f3bdafed193a1ded63e377f16f45e17) )
		ROM_LOAD( "a68_06",       0x18000, 0x8000, CRC(05f9eb9a) SHA1(a71640a63b259799086d361ef293aa26cec46a0c) )
	
		ROM_REGION( 0x20000, REGION_GFX3, ROMREGION_DISPOSE )	/* Region 1 - temporary for gfx */
		ROM_LOAD( "a68-13",       0x00000, 0x8000, CRC(643fb282) SHA1(d904d3c27c2b56341929c5eed4ea97e948c53c34) )  /* Sprites */
		ROM_LOAD( "a68-12",       0x08000, 0x8000, CRC(11f74e32) SHA1(02d8b4cc679f45a02c4989f2b62cde91b7418235) )
		ROM_LOAD( "a68-11",       0x10000, 0x8000, CRC(f24158cf) SHA1(db4c6b68a488b0798ea5f793ac8ced283a8ecab2) )
		ROM_LOAD( "a68-10",       0x18000, 0x8000, CRC(83161ed0) SHA1(a6aa28f22f487dc3a2ec07935e6d42bcdd1eff81) )
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 )
		ROM_LOAD( "rom21",        0x0000, 0x0100, CRC(d6360b4d) SHA1(3e64548c82a3378fc091e104cdc2b0c7e592fc44) )
		ROM_LOAD( "rom20",        0x0100, 0x0100, CRC(4ca01887) SHA1(2892c89d5e60f1d10593adffff55c1a9654e8209) )
		ROM_LOAD( "rom19",        0x0200, 0x0100, CRC(513224f0) SHA1(15b34612206138f6fc5f7478925b1fff2ed56aa8) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_getstarb = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x18000, REGION_CPU1, 0 )		/* Region 0 - main cpu code */
		ROM_LOAD( "gs_14.rom",    0x00000, 0x4000, CRC(1a57a920) SHA1(b1e9d5b29c0e3632eec3ad1ee51bf3392e4b816d) )
		ROM_LOAD( "gs_13.rom",    0x04000, 0x4000, CRC(805f8e77) SHA1(c3ad6eae842d2d10f716998d5a803038fa7b338f) )
		ROM_LOAD( "a68_02.bin",   0x10000, 0x8000, CRC(3567da17) SHA1(29d698606d0bd30abfc3171d79bfad95b0de89fc) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )		/* Region 3 - sound cpu code */
		ROM_LOAD( "a68-03",       0x0000, 0x2000, CRC(18daa44c) SHA1(1a3d22a186c591321d1b836ee30d89fba4771122) )
	
		ROM_REGION( 0x04000, REGION_GFX1, ROMREGION_DISPOSE )	/* Region 1 - temporary for gfx */
		ROM_LOAD( "a68_05.bin",   0x00000, 0x2000, CRC(e3d409e7) SHA1(0b6be4767f110729f4dd1a472ef8d9a0c718b684) )  /* Chars */
		ROM_LOAD( "a68_04.bin",   0x02000, 0x2000, CRC(6e5ac9d4) SHA1(74f90b7a1ceb3b1c2fd92dff100d92dea0155530) )
	
		ROM_REGION( 0x20000, REGION_GFX2, ROMREGION_DISPOSE )	/* Region 1 - temporary for gfx */
		ROM_LOAD( "a68_09",       0x00000, 0x8000, CRC(a293cc2e) SHA1(a2c2598e92982d13b51cbb6efb4b963142233433) )  /* Tiles */
		ROM_LOAD( "a68_08",       0x08000, 0x8000, CRC(37662375) SHA1(46ba8a3f0b553d476ecf431d0d20556896b4ca43) )
		ROM_LOAD( "a68_07",       0x10000, 0x8000, CRC(cf1a964c) SHA1(e9223c8d4f3bdafed193a1ded63e377f16f45e17) )
		ROM_LOAD( "a68_06",       0x18000, 0x8000, CRC(05f9eb9a) SHA1(a71640a63b259799086d361ef293aa26cec46a0c) )
	
		ROM_REGION( 0x20000, REGION_GFX3, ROMREGION_DISPOSE )	/* Region 1 - temporary for gfx */
		ROM_LOAD( "a68-13",       0x00000, 0x8000, CRC(643fb282) SHA1(d904d3c27c2b56341929c5eed4ea97e948c53c34) )  /* Sprites */
		ROM_LOAD( "a68-12",       0x08000, 0x8000, CRC(11f74e32) SHA1(02d8b4cc679f45a02c4989f2b62cde91b7418235) )
		ROM_LOAD( "a68-11",       0x10000, 0x8000, CRC(f24158cf) SHA1(db4c6b68a488b0798ea5f793ac8ced283a8ecab2) )
		ROM_LOAD( "a68-10",       0x18000, 0x8000, CRC(83161ed0) SHA1(a6aa28f22f487dc3a2ec07935e6d42bcdd1eff81) )
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 )
		ROM_LOAD( "rom21",        0x0000, 0x0100, CRC(d6360b4d) SHA1(3e64548c82a3378fc091e104cdc2b0c7e592fc44) )
		ROM_LOAD( "rom20",        0x0100, 0x0100, CRC(4ca01887) SHA1(2892c89d5e60f1d10593adffff55c1a9654e8209) )
		ROM_LOAD( "rom19",        0x0200, 0x0100, CRC(513224f0) SHA1(15b34612206138f6fc5f7478925b1fff2ed56aa8) )
	ROM_END(); }}; 
	
	public static DriverInitHandlerPtr init_tigerh  = new DriverInitHandlerPtr() { public void handler(){
		install_mem_read_handler(0,  0xe803, 0xe803, tigerh_mcu_r );
		install_mem_write_handler(0, 0xe803, 0xe803, tigerh_mcu_w  );
	
	} };
	
	
	
	/*   ( YEAR  NAME      PARENT    MACHINE   INPUT     INIT    MONITOR COMPANY    FULLNAME     FLAGS ) */
	public static GameDriver driver_perfrman	   = new GameDriver("1985"	,"perfrman"	,"slapfght.java"	,rom_perfrman,null	,machine_driver_perfrman	,input_ports_perfrman	,null	,ROT270, "[Toaplan] Data East Corporation","Performan (Japan)" )
	public static GameDriver driver_perfrmau	   = new GameDriver("1985"	,"perfrmau"	,"slapfght.java"	,rom_perfrmau,driver_perfrman	,machine_driver_perfrman	,input_ports_perfrman	,null	,ROT270, "[Toaplan] Data East USA",        "Performan (US)" )
	public static GameDriver driver_tigerh	   = new GameDriver("1985"	,"tigerh"	,"slapfght.java"	,rom_tigerh,null	,machine_driver_tigerh	,input_ports_tigerh	,init_tigerh	,ROT270, "Taito America Corp.", "Tiger Heli (US)", GAME_NO_COCKTAIL )
	public static GameDriver driver_tigerh2	   = new GameDriver("1985"	,"tigerh2"	,"slapfght.java"	,rom_tigerh2,driver_tigerh	,machine_driver_tigerh	,input_ports_tigerh	,init_tigerh	,ROT270, "Taito Corp.",         "Tiger Heli (Japan set 1)", GAME_NOT_WORKING | GAME_NO_COCKTAIL )
	public static GameDriver driver_tigerhj	   = new GameDriver("1985"	,"tigerhj"	,"slapfght.java"	,rom_tigerhj,driver_tigerh	,machine_driver_tigerh	,input_ports_tigerh	,init_tigerh	,ROT270, "Taito Corp.",         "Tiger Heli (Japan set 2)", GAME_NO_COCKTAIL )
	public static GameDriver driver_tigerhb1	   = new GameDriver("1985"	,"tigerhb1"	,"slapfght.java"	,rom_tigerhb1,driver_tigerh	,machine_driver_tigerhb	,input_ports_tigerh	,null	,ROT270, "bootleg",             "Tiger Heli (bootleg set 1)" )
	public static GameDriver driver_tigerhb2	   = new GameDriver("1985"	,"tigerhb2"	,"slapfght.java"	,rom_tigerhb2,driver_tigerh	,machine_driver_tigerhb	,input_ports_tigerh	,null	,ROT270, "bootleg",             "Tiger Heli (bootleg set 2)", GAME_NO_COCKTAIL )
	public static GameDriver driver_slapfigh	   = new GameDriver("1986"	,"slapfigh"	,"slapfght.java"	,rom_slapfigh,null	,machine_driver_slapfigh	,input_ports_slapfigh	,null	,ROT270, "Taito",    "Slap Fight", GAME_NOT_WORKING | GAME_NO_COCKTAIL )
	public static GameDriver driver_slapbtjp	   = new GameDriver("1986"	,"slapbtjp"	,"slapfght.java"	,rom_slapbtjp,driver_slapfigh	,machine_driver_slapfigh	,input_ports_slapfigh	,null	,ROT270, "bootleg",  "Slap Fight (Japan bootleg)", GAME_NO_COCKTAIL )
	public static GameDriver driver_slapbtuk	   = new GameDriver("1986"	,"slapbtuk"	,"slapfght.java"	,rom_slapbtuk,driver_slapfigh	,machine_driver_slapbtuk	,input_ports_slapfigh	,null	,ROT270, "bootleg",  "Slap Fight (English bootleg)", GAME_NO_COCKTAIL )
	public static GameDriver driver_alcon	   = new GameDriver("1986"	,"alcon"	,"slapfght.java"	,rom_alcon,driver_slapfigh	,machine_driver_slapfigh	,input_ports_slapfigh	,null	,ROT270, "<unknown>","Alcon", GAME_NOT_WORKING | GAME_NO_COCKTAIL )
	public static GameDriver driver_getstar	   = new GameDriver("1986"	,"getstar"	,"slapfght.java"	,rom_getstar,null	,machine_driver_slapfigh	,input_ports_getstar	,null	,ROT0,   "Taito",  "Guardian", GAME_NOT_WORKING | GAME_NO_COCKTAIL )
	public static GameDriver driver_getstarj	   = new GameDriver("1986"	,"getstarj"	,"slapfght.java"	,rom_getstarj,driver_getstar	,machine_driver_slapfigh	,input_ports_getstar	,null	,ROT0,   "Taito",  "Get Star (Japan)", GAME_NOT_WORKING | GAME_NO_COCKTAIL )
	public static GameDriver driver_getstarb	   = new GameDriver("1986"	,"getstarb"	,"slapfght.java"	,rom_getstarb,driver_getstar	,machine_driver_slapfigh	,input_ports_getstar	,null	,ROT0,   "bootleg","Get Star (bootleg)", GAME_NO_COCKTAIL )
}
