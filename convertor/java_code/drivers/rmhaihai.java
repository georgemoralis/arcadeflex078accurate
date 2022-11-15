/***************************************************************************

Real Mahjong Haihai                (c)1985 Alba
Real Mahjong Haihai Jinji Idou Hen (c)1986 Alba
Real Mahjong Haihai Seichouhen     (c)1986 Visco

CPU:	Z80
Sound:	AY-3-8910
        M5205
OSC:	20.000MHz

driver by Nicola Salmoria

TODO:
- input handling is not well understood... it might well be handled by a
  protection device. I think it is, because rmhaijin and rmhaisei do additional
  checks which are obfuscated in a way that would make sense only for
  protection (in rmhaisei the failure is more explicit, in rmhaijin it's
  deviously delayed to a later part of the game).
  In themj the checks are patched out, maybe it's a bootleg?

- there probably is an area of NVRAM, because credits don't go away after
  a reset.

- some unknown reads and writes.

- visible area uncertain.

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class rmhaihai
{
	
	static int gfxbank;
	static struct tilemap *bg_tilemap;
	
	public static WriteHandlerPtr rmhaihai_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (videoram[offset] != data)
		{
			videoram[offset] = data;
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	} };
	
	public static WriteHandlerPtr rmhaihai_colorram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (colorram[offset] != data)
		{
			colorram[offset] = data;
			tilemap_mark_tile_dirty(bg_tilemap, offset);
		}
	} };
	
	static void get_bg_tile_info(int tile_index)
	{
		int attr = colorram[tile_index];
		int code = videoram[tile_index] + (gfxbank << 12) + ((attr & 0x07) << 8) + ((attr & 0x80) << 4);
		int color = (gfxbank << 5) + (attr >> 3);
	
		SET_TILE_INFO(0, code, color, 0)
	}
	
	public static VideoStartHandlerPtr video_start_rmhaihai  = new VideoStartHandlerPtr() { public int handler(){
		bg_tilemap = tilemap_create(get_bg_tile_info, tilemap_scan_rows, 
			TILEMAP_OPAQUE, 8, 8, 64, 32);
	
		if ( !bg_tilemap )
			return 1;
	
		return 0;
	} };
	
	public static VideoUpdateHandlerPtr video_update_rmhaihai  = new VideoUpdateHandlerPtr() { public void handler(mame_bitmap bitmap, rectangle cliprect){
		tilemap_draw(bitmap, &Machine->visible_area, bg_tilemap, 0, 0);
	} };
	
	
	
	static int keyboard_cmd;
	
	public static ReadHandlerPtr keyboard_r  = new ReadHandlerPtr() { public int handler(int offset){
	logerror("%04x: keyboard_r\n",activecpu_get_pc());
		switch(activecpu_get_pc())
		{
			/* read keyboard */
			case 0x0aba:	// rmhaihai, rmhaisei
			case 0x0b2a:	// rmhaihib
			case 0x0ab4:	// rmhaijin
			case 0x0aea:	// themj
			{
				int i;
	
				for (i = 0;i < 31;i++)
				{
					if (readinputport(2 + i/16) & (1<<(i&15))) return i+1;
				}
				if (readinputport(3) & 0x8000) return 0x80;	// coin
				return 0;
			}
			case 0x5c7b:	// rmhaihai, rmhaisei, rmhaijin
			case 0x5950:	// rmhaihib
			case 0x5bf3:	// themj, but the test is NOPed out!
				return 0xcc;	/* keyboard_cmd = 0xcb */
	
	
			case 0x13a:	// additional checks done by rmhaijin
				if (keyboard_cmd == 0x3b) return 0xdd;
				if (keyboard_cmd == 0x85) return 0xdc;
				if (keyboard_cmd == 0xf2) return 0xd6;
				if (keyboard_cmd == 0xc1) return 0x8f;
				if (keyboard_cmd == 0xd0) return 0x08;
				return 0;
	
			case 0x140:	// additional checks done by rmhaisei
			case 0x155:	// additional checks done by themj, but they are patched out!
				if (keyboard_cmd == 0x11) return 0x57;
				if (keyboard_cmd == 0x3e) return 0xda;
				if (keyboard_cmd == 0x48) return 0x74;
				if (keyboard_cmd == 0x5d) return 0x46;
				if (keyboard_cmd == 0xd0) return 0x08;
				return 0;
		}
	
		/* there are many more reads whose function is unknown, returning 0 seems fine */
		return 0;
	} };
	
	public static WriteHandlerPtr keyboard_w = new WriteHandlerPtr() {public void handler(int offset, int data){
	logerror("%04x: keyboard_w %02x\n",activecpu_get_pc(),data);
		keyboard_cmd = data;
	} };
	
	public static ReadHandlerPtr samples_r  = new ReadHandlerPtr() { public int handler(int offset){
		return memory_region(REGION_SOUND1)[offset];
	} };
	
	public static WriteHandlerPtr adpcm_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		MSM5205_data_w(0,data);         /* bit0..3  */
		MSM5205_reset_w(0,(data>>5)&1); /* bit 5    */
		MSM5205_vclk_w (0,(data>>4)&1); /* bit4     */
	} };
	
	public static WriteHandlerPtr ctrl_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		flip_screen_set(data & 0x01);
	
		// (data & 0x02) is switched on and off in service mode
	
		coin_lockout_w(0, ~data & 0x04);
		coin_counter_w(0, data & 0x08);
	
		// (data & 0x10) is medal in service mode
	
		gfxbank = (data & 0x40) >> 6;	/* rmhaisei only */
	} };
	
	public static WriteHandlerPtr themj_rombank_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		data8_t *rom = memory_region(REGION_CPU1) + 0x10000;
		int bank = data & 0x03;
	logerror("banksw %d\n",bank);
		cpu_setbank(1, rom + bank*0x4000);
		cpu_setbank(2, rom + bank*0x4000 + 0x2000);
	} };
	
	public static MachineInitHandlerPtr machine_init_themj  = new MachineInitHandlerPtr() { public void handler(){
		themj_rombank_w(0,0);
	} };
	
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x9fff, MRA_ROM ),
		new Memory_ReadAddress( 0xa000, 0xa7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xa800, 0xb7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xc000, 0xdfff, MRA_ROM ),
		new Memory_ReadAddress( 0xe000, 0xffff, MRA_ROM ),	/* rmhaisei only */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress themj_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x9fff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xa000, 0xa7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xa800, 0xb7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xc000, 0xdfff, MRA_BANK2 ),
		new Memory_ReadAddress( 0xe000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x9fff, MWA_ROM ),
		new Memory_WriteAddress( 0xa000, 0xa7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xa800, 0xafff, rmhaihai_colorram_w, &colorram ),
		new Memory_WriteAddress( 0xb000, 0xb7ff, rmhaihai_videoram_w, &videoram ),
		new Memory_WriteAddress( 0xb83c, 0xb83c, MWA_NOP ),	// ??
		new Memory_WriteAddress( 0xbc00, 0xbc00, MWA_NOP ),	// ??
		new Memory_WriteAddress( 0xc000, 0xdfff, MWA_ROM ),
		new Memory_WriteAddress( 0xe000, 0xffff, MWA_ROM ),	/* rmhaisei only */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x0000, 0x7fff, samples_r ),
		new IO_ReadPort( 0x8000, 0x8000, keyboard_r ),
		new IO_ReadPort( 0x8001, 0x8001, IORP_NOP ),	// ??
		new IO_ReadPort( 0x8020, 0x8020, AY8910_read_port_0_r ),
	MEMORY_END
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x8000, 0x8000, IOWP_NOP ),	// ??
		new IO_WritePort( 0x8001, 0x8001, keyboard_w ),
		new IO_WritePort( 0x8020, 0x8020, AY8910_control_port_0_w ),
		new IO_WritePort( 0x8021, 0x8021, AY8910_write_port_0_w ),
		new IO_WritePort( 0x8040, 0x8040, adpcm_w ),
		new IO_WritePort( 0x8060, 0x8060, ctrl_w ),
		new IO_WritePort( 0x8080, 0x8080, IOWP_NOP ),	// ??
		new IO_WritePort( 0xbc04, 0xbc04, IOWP_NOP ),	// ??
		new IO_WritePort( 0xbc0c, 0xbc0c, IOWP_NOP ),	// ??
	MEMORY_END
	
	
	public static IO_WritePort themj_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x8000, 0x8000, IOWP_NOP ),	// ??
		new IO_WritePort( 0x8001, 0x8001, keyboard_w ),
		new IO_WritePort( 0x8020, 0x8020, AY8910_control_port_0_w ),
		new IO_WritePort( 0x8021, 0x8021, AY8910_write_port_0_w ),
		new IO_WritePort( 0x8040, 0x8040, adpcm_w ),
		new IO_WritePort( 0x8060, 0x8060, ctrl_w ),
		new IO_WritePort( 0x8080, 0x8080, IOWP_NOP ),	// ??
		new IO_WritePort( 0x80a0, 0x80a0, themj_rombank_w ),
		new IO_WritePort( 0xbc04, 0xbc04, IOWP_NOP ),	// ??
		new IO_WritePort( 0xbc0c, 0xbc0c, IOWP_NOP ),	// ??
	MEMORY_END
	
	static InputPortHandlerPtr input_ports_rmhaihai = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( rmhaihai )
		PORT_START();   /* dsw2 */
		PORT_DIPNAME( 0x01, 0x01, "Unknown 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0xfe, 0xfe, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0xfe, "1 (Easy"));
		PORT_DIPSETTING(    0x7e, "2" );
		PORT_DIPSETTING(    0xbe, "3" );
		PORT_DIPSETTING(    0xde, "4" );
		PORT_DIPSETTING(    0xee, "5" );
		PORT_DIPSETTING(    0xf6, "6" );
		PORT_DIPSETTING(    0xfa, "7" );
		PORT_DIPSETTING(    0xfc, "8 (Difficult"));
	
	    PORT_START();   /* dsw1 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x02, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x00, "A 2/1 B 1/2" );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x04, "A 1/2 B 2/1" );
		PORT_DIPSETTING(    0x08, "A 1/3 B 3/1" );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x80, 0x80, "Medal" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	// fake, handled by keyboard_r()
		PORT_BITX(0x0001, IP_ACTIVE_HIGH, 0, "P1 Small",  KEYCODE_BACKSPACE, JOYCODE_NONE );
		PORT_BITX(0x0002, IP_ACTIVE_HIGH, 0, "P1 Double", KEYCODE_RSHIFT,    JOYCODE_NONE );
		PORT_BITX(0x0004, IP_ACTIVE_HIGH, 0, "P1 Big",    KEYCODE_ENTER,     JOYCODE_NONE );
		PORT_BITX(0x0008, IP_ACTIVE_HIGH, 0, "P1 Take",   KEYCODE_RCONTROL,  JOYCODE_NONE );
		PORT_BITX(0x0010, IP_ACTIVE_HIGH, 0, "P1 Flip",   KEYCODE_X,         JOYCODE_NONE );
		PORT_BITX(0x0020, IP_ACTIVE_HIGH, 0, "P1 Last",   KEYCODE_RALT,      JOYCODE_NONE );
		PORT_BIT( 0x0040, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BITX(0x0080, IP_ACTIVE_HIGH, 0, "P1 K",      KEYCODE_K,     JOYCODE_NONE );
		PORT_BITX(0x0100, IP_ACTIVE_HIGH, 0, "P1 Ron",    KEYCODE_Z,     JOYCODE_NONE );
		PORT_BITX(0x0200, IP_ACTIVE_HIGH, 0, "P1 G",      KEYCODE_G,     JOYCODE_NONE );
		PORT_BITX(0x0400, IP_ACTIVE_HIGH, 0, "P1 Chi",    KEYCODE_SPACE, JOYCODE_NONE );
		PORT_BITX(0x0800, IP_ACTIVE_HIGH, 0, "P1 C",      KEYCODE_C,     JOYCODE_NONE );
		PORT_BIT( 0x1000, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BITX(0x2000, IP_ACTIVE_HIGH, 0, "P1 L",      KEYCODE_L,     JOYCODE_NONE );
		PORT_BIT( 0x4000, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BITX(0x8000, IP_ACTIVE_HIGH, 0, "P1 H",      KEYCODE_H,     JOYCODE_NONE );
	
		PORT_START(); 	// fake, handled by keyboard_r()
		PORT_BITX(0x0001, IP_ACTIVE_HIGH, 0, "P1 Pon",   KEYCODE_LALT,     JOYCODE_NONE );
		PORT_BITX(0x0002, IP_ACTIVE_HIGH, 0, "P1 D",     KEYCODE_D,        JOYCODE_NONE );
		PORT_BIT( 0x0004, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BITX(0x0008, IP_ACTIVE_HIGH, 0, "P1 I",     KEYCODE_I,        JOYCODE_NONE );
		PORT_BITX(0x0010, IP_ACTIVE_HIGH, 0, "P1 Kan",   KEYCODE_LCONTROL, JOYCODE_NONE );
		PORT_BITX(0x0020, IP_ACTIVE_HIGH, 0, "P1 E",     KEYCODE_E,        JOYCODE_NONE );
		PORT_BITX(0x0040, IP_ACTIVE_HIGH, 0, "P1 M",     KEYCODE_M,        JOYCODE_NONE );
		PORT_BITX(0x0080, IP_ACTIVE_HIGH, 0, "P1 A",     KEYCODE_A,        JOYCODE_NONE );
		PORT_BITX(0x0100, IP_ACTIVE_HIGH, 0, "P1 Bet",   KEYCODE_RCONTROL, JOYCODE_NONE );
		PORT_BITX(0x0200, IP_ACTIVE_HIGH, 0, "P1 J",     KEYCODE_J,        JOYCODE_NONE );
		PORT_BITX(0x0400, IP_ACTIVE_HIGH, 0, "P1 Reach", KEYCODE_LSHIFT,   JOYCODE_NONE );
		PORT_BITX(0x0800, IP_ACTIVE_HIGH, 0, "P1 F",     KEYCODE_F,        JOYCODE_NONE );
		PORT_BITX(0x1000, IP_ACTIVE_HIGH, 0, "P1 N",     KEYCODE_N,        JOYCODE_NONE );
		PORT_BITX(0x2000, IP_ACTIVE_HIGH, 0, "P1 B",     KEYCODE_B,        JOYCODE_NONE );
		PORT_BIT( 0x4000, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT_IMPULSE( 0x8000, IP_ACTIVE_HIGH, IPT_COIN1, 1 );
	
		PORT_START(); 	// fake, handled by keyboard_r()
		PORT_BITX(0x0001, IP_ACTIVE_HIGH, 0, "P2 Small",  KEYCODE_BACKSPACE, JOYCODE_NONE );
		PORT_BITX(0x0002, IP_ACTIVE_HIGH, 0, "P2 Double", KEYCODE_RSHIFT,    JOYCODE_NONE );
		PORT_BITX(0x0004, IP_ACTIVE_HIGH, 0, "P2 Big",    KEYCODE_ENTER,     JOYCODE_NONE );
		PORT_BITX(0x0008, IP_ACTIVE_HIGH, 0, "P2 Take",   KEYCODE_RCONTROL,  JOYCODE_NONE );
		PORT_BITX(0x0010, IP_ACTIVE_HIGH, 0, "P2 Flip",   KEYCODE_X,         JOYCODE_NONE );
		PORT_BITX(0x0020, IP_ACTIVE_HIGH, 0, "P2 Last",   KEYCODE_RALT,      JOYCODE_NONE );
		PORT_BIT( 0x0040, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BITX(0x0080, IP_ACTIVE_HIGH, 0, "P2 K",      KEYCODE_K,     JOYCODE_NONE );
		PORT_BITX(0x0100, IP_ACTIVE_HIGH, 0, "P2 Ron",    KEYCODE_Z,     JOYCODE_NONE );
		PORT_BITX(0x0200, IP_ACTIVE_HIGH, 0, "P2 G",      KEYCODE_G,     JOYCODE_NONE );
		PORT_BITX(0x0400, IP_ACTIVE_HIGH, 0, "P2 Chi",    KEYCODE_SPACE, JOYCODE_NONE );
		PORT_BITX(0x0800, IP_ACTIVE_HIGH, 0, "P2 C",      KEYCODE_C,     JOYCODE_NONE );
		PORT_BIT( 0x1000, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BITX(0x2000, IP_ACTIVE_HIGH, 0, "P2 L",      KEYCODE_L,     JOYCODE_NONE );
		PORT_BIT( 0x4000, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BITX(0x8000, IP_ACTIVE_HIGH, 0, "P2 H",      KEYCODE_H,     JOYCODE_NONE );
	
		PORT_START(); 	// fake, handled by keyboard_r()
		PORT_BITX(0x0001, IP_ACTIVE_HIGH, 0, "P2 Pon",   KEYCODE_LALT,     JOYCODE_NONE );
		PORT_BITX(0x0002, IP_ACTIVE_HIGH, 0, "P2 D",     KEYCODE_D,        JOYCODE_NONE );
		PORT_BIT( 0x0004, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BITX(0x0008, IP_ACTIVE_HIGH, 0, "P2 I",     KEYCODE_I,        JOYCODE_NONE );
		PORT_BITX(0x0010, IP_ACTIVE_HIGH, 0, "P2 Kan",   KEYCODE_LCONTROL, JOYCODE_NONE );
		PORT_BITX(0x0020, IP_ACTIVE_HIGH, 0, "P2 E",     KEYCODE_E,        JOYCODE_NONE );
		PORT_BITX(0x0040, IP_ACTIVE_HIGH, 0, "P2 M",     KEYCODE_M,        JOYCODE_NONE );
		PORT_BITX(0x0080, IP_ACTIVE_HIGH, 0, "P2 A",     KEYCODE_A,        JOYCODE_NONE );
		PORT_BITX(0x0100, IP_ACTIVE_HIGH, 0, "P2 Bet",   KEYCODE_RCONTROL, JOYCODE_NONE );
		PORT_BITX(0x0200, IP_ACTIVE_HIGH, 0, "P2 J",     KEYCODE_J,        JOYCODE_NONE );
		PORT_BITX(0x0400, IP_ACTIVE_HIGH, 0, "P2 Reach", KEYCODE_LSHIFT,   JOYCODE_NONE );
		PORT_BITX(0x0800, IP_ACTIVE_HIGH, 0, "P2 F",     KEYCODE_F,        JOYCODE_NONE );
		PORT_BITX(0x1000, IP_ACTIVE_HIGH, 0, "P2 N",     KEYCODE_N,        JOYCODE_NONE );
		PORT_BITX(0x2000, IP_ACTIVE_HIGH, 0, "P2 B",     KEYCODE_B,        JOYCODE_NONE );
		PORT_BIT( 0x4000, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT_IMPULSE( 0x8000, IP_ACTIVE_HIGH, IPT_COIN2, 1 );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_rmhaihib = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( rmhaihib )
		PORT_START();   /* dsw2 */
		PORT_DIPNAME( 0x01, 0x01, "Unknown 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "Unknown 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "Gal Bonus Bet" );
		PORT_DIPSETTING(    0x04, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPNAME( 0x18, 0x18, "Gal Bonus" );
		PORT_DIPSETTING(    0x18, "8" );
		PORT_DIPSETTING(    0x10, "16" );
		PORT_DIPSETTING(    0x08, "24" );
		PORT_DIPSETTING(    0x00, "32" );
		PORT_DIPNAME( 0xe0, 0xe0, "Pay Setting" );
		PORT_DIPSETTING(    0xe0, "90%" );
		PORT_DIPSETTING(    0xc0, "80%" );
		PORT_DIPSETTING(    0xa0, "70%" );
		PORT_DIPSETTING(    0x80, "60%" );
		PORT_DIPSETTING(    0x60, "50%" );
		PORT_DIPSETTING(    0x40, "40%" );
		PORT_DIPSETTING(    0x20, "30%" );
		PORT_DIPSETTING(    0x00, "20%" );
	
		PORT_START();   /* dsw1 */
		PORT_DIPNAME( 0x03, 0x03, "Bet Max" );
		PORT_DIPSETTING(    0x01, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPSETTING(    0x03, "10" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, "A 1/1 B 1/10" );
		PORT_DIPSETTING(    0x08, "A 1/1 B 1/5" );
		PORT_DIPSETTING(    0x00, "A 1/1 B 5/1" );
		PORT_DIPSETTING(    0x0c, "A 1/1 B 10/1" );
		PORT_DIPNAME( 0x10, 0x10, "Unknown 1-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x80, 0x80, "Unknown 1-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	// fake, handled by keyboard_r()
		PORT_BITX(0x0001, IP_ACTIVE_HIGH, 0, "P1 Small",  KEYCODE_BACKSPACE, JOYCODE_NONE );
		PORT_BITX(0x0002, IP_ACTIVE_HIGH, 0, "P1 Double", KEYCODE_RSHIFT,    JOYCODE_NONE );
		PORT_BITX(0x0004, IP_ACTIVE_HIGH, 0, "P1 Big",    KEYCODE_ENTER,     JOYCODE_NONE );
		PORT_BITX(0x0008, IP_ACTIVE_HIGH, 0, "P1 Take",   KEYCODE_RCONTROL,  JOYCODE_NONE );
		PORT_BITX(0x0010, IP_ACTIVE_HIGH, 0, "P1 Flip",   KEYCODE_X,         JOYCODE_NONE );
		PORT_BITX(0x0020, IP_ACTIVE_HIGH, 0, "P1 Last",   KEYCODE_RALT,      JOYCODE_NONE );
		PORT_BIT( 0x0040, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BITX(0x0080, IP_ACTIVE_HIGH, 0, "P1 K",      KEYCODE_K,     JOYCODE_NONE );
		PORT_BITX(0x0100, IP_ACTIVE_HIGH, 0, "P1 Ron",    KEYCODE_Z,     JOYCODE_NONE );
		PORT_BITX(0x0200, IP_ACTIVE_HIGH, 0, "P1 G",      KEYCODE_G,     JOYCODE_NONE );
		PORT_BITX(0x0400, IP_ACTIVE_HIGH, 0, "P1 Chi",    KEYCODE_SPACE, JOYCODE_NONE );
		PORT_BITX(0x0800, IP_ACTIVE_HIGH, 0, "P1 C",      KEYCODE_C,     JOYCODE_NONE );
		PORT_BIT( 0x1000, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BITX(0x2000, IP_ACTIVE_HIGH, 0, "P1 L",      KEYCODE_L,     JOYCODE_NONE );
		PORT_BIT( 0x4000, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BITX(0x8000, IP_ACTIVE_HIGH, 0, "P1 H",      KEYCODE_H,     JOYCODE_NONE );
	
		PORT_START(); 	// fake, handled by keyboard_r()
		PORT_BITX(0x0001, IP_ACTIVE_HIGH, 0, "P1 Pon",   KEYCODE_LALT,     JOYCODE_NONE );
		PORT_BITX(0x0002, IP_ACTIVE_HIGH, 0, "P1 D",     KEYCODE_D,        JOYCODE_NONE );
		PORT_BIT( 0x0004, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BITX(0x0008, IP_ACTIVE_HIGH, 0, "P1 I",     KEYCODE_I,        JOYCODE_NONE );
		PORT_BITX(0x0010, IP_ACTIVE_HIGH, 0, "P1 Kan",   KEYCODE_LCONTROL, JOYCODE_NONE );
		PORT_BITX(0x0020, IP_ACTIVE_HIGH, 0, "P1 E",     KEYCODE_E,        JOYCODE_NONE );
		PORT_BITX(0x0040, IP_ACTIVE_HIGH, 0, "P1 M",     KEYCODE_M,        JOYCODE_NONE );
		PORT_BITX(0x0080, IP_ACTIVE_HIGH, 0, "P1 A",     KEYCODE_A,        JOYCODE_NONE );
		PORT_BITX(0x0100, IP_ACTIVE_HIGH, 0, "P1 Bet",   KEYCODE_RCONTROL, JOYCODE_NONE );
		PORT_BITX(0x0200, IP_ACTIVE_HIGH, 0, "P1 J",     KEYCODE_J,        JOYCODE_NONE );
		PORT_BITX(0x0400, IP_ACTIVE_HIGH, 0, "P1 Reach", KEYCODE_LSHIFT,   JOYCODE_NONE );
		PORT_BITX(0x0800, IP_ACTIVE_HIGH, 0, "P1 F",     KEYCODE_F,        JOYCODE_NONE );
		PORT_BITX(0x1000, IP_ACTIVE_HIGH, 0, "P1 N",     KEYCODE_N,        JOYCODE_NONE );
		PORT_BITX(0x2000, IP_ACTIVE_HIGH, 0, "P1 B",     KEYCODE_B,        JOYCODE_NONE );
		PORT_BIT( 0x4000, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT_IMPULSE( 0x8000, IP_ACTIVE_HIGH, IPT_COIN1, 1 );
	
		PORT_START(); 	// fake, handled by keyboard_r()
		PORT_BITX(0x0001, IP_ACTIVE_HIGH, 0, "P2 Small",  KEYCODE_BACKSPACE, JOYCODE_NONE );
		PORT_BITX(0x0002, IP_ACTIVE_HIGH, 0, "P2 Double", KEYCODE_RSHIFT,    JOYCODE_NONE );
		PORT_BITX(0x0004, IP_ACTIVE_HIGH, 0, "P2 Big",    KEYCODE_ENTER,     JOYCODE_NONE );
		PORT_BITX(0x0008, IP_ACTIVE_HIGH, 0, "P2 Take",   KEYCODE_RCONTROL,  JOYCODE_NONE );
		PORT_BITX(0x0010, IP_ACTIVE_HIGH, 0, "P2 Flip",   KEYCODE_X,         JOYCODE_NONE );
		PORT_BITX(0x0020, IP_ACTIVE_HIGH, 0, "P2 Last",   KEYCODE_RALT,      JOYCODE_NONE );
		PORT_BIT( 0x0040, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BITX(0x0080, IP_ACTIVE_HIGH, 0, "P2 K",      KEYCODE_K,     JOYCODE_NONE );
		PORT_BITX(0x0100, IP_ACTIVE_HIGH, 0, "P2 Ron",    KEYCODE_Z,     JOYCODE_NONE );
		PORT_BITX(0x0200, IP_ACTIVE_HIGH, 0, "P2 G",      KEYCODE_G,     JOYCODE_NONE );
		PORT_BITX(0x0400, IP_ACTIVE_HIGH, 0, "P2 Chi",    KEYCODE_SPACE, JOYCODE_NONE );
		PORT_BITX(0x0800, IP_ACTIVE_HIGH, 0, "P2 C",      KEYCODE_C,     JOYCODE_NONE );
		PORT_BIT( 0x1000, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BITX(0x2000, IP_ACTIVE_HIGH, 0, "P2 L",      KEYCODE_L,     JOYCODE_NONE );
		PORT_BIT( 0x4000, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BITX(0x8000, IP_ACTIVE_HIGH, 0, "P2 H",      KEYCODE_H,     JOYCODE_NONE );
	
		PORT_START(); 	// fake, handled by keyboard_r()
		PORT_BITX(0x0001, IP_ACTIVE_HIGH, 0, "P2 Pon",   KEYCODE_LALT,     JOYCODE_NONE );
		PORT_BITX(0x0002, IP_ACTIVE_HIGH, 0, "P2 D",     KEYCODE_D,        JOYCODE_NONE );
		PORT_BIT( 0x0004, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BITX(0x0008, IP_ACTIVE_HIGH, 0, "P2 I",     KEYCODE_I,        JOYCODE_NONE );
		PORT_BITX(0x0010, IP_ACTIVE_HIGH, 0, "P2 Kan",   KEYCODE_LCONTROL, JOYCODE_NONE );
		PORT_BITX(0x0020, IP_ACTIVE_HIGH, 0, "P2 E",     KEYCODE_E,        JOYCODE_NONE );
		PORT_BITX(0x0040, IP_ACTIVE_HIGH, 0, "P2 M",     KEYCODE_M,        JOYCODE_NONE );
		PORT_BITX(0x0080, IP_ACTIVE_HIGH, 0, "P2 A",     KEYCODE_A,        JOYCODE_NONE );
		PORT_BITX(0x0100, IP_ACTIVE_HIGH, 0, "P2 Bet",   KEYCODE_RCONTROL, JOYCODE_NONE );
		PORT_BITX(0x0200, IP_ACTIVE_HIGH, 0, "P2 J",     KEYCODE_J,        JOYCODE_NONE );
		PORT_BITX(0x0400, IP_ACTIVE_HIGH, 0, "P2 Reach", KEYCODE_LSHIFT,   JOYCODE_NONE );
		PORT_BITX(0x0800, IP_ACTIVE_HIGH, 0, "P2 F",     KEYCODE_F,        JOYCODE_NONE );
		PORT_BITX(0x1000, IP_ACTIVE_HIGH, 0, "P2 N",     KEYCODE_N,        JOYCODE_NONE );
		PORT_BITX(0x2000, IP_ACTIVE_HIGH, 0, "P2 B",     KEYCODE_B,        JOYCODE_NONE );
		PORT_BIT( 0x4000, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT_IMPULSE( 0x8000, IP_ACTIVE_HIGH, IPT_COIN2, 1 );
	
	//	PORT_START();  // 11
	//	PORT_BITX(    0x01, IP_ACTIVE_LOW, 0, "Pay Out", KEYCODE_3, JOYCODE_NONE );
	//	PORT_BIT(     0x02, IP_ACTIVE_LOW, IPT_SERVICE4 );/* RAM clear */
	//	PORT_SERVICE( 0x04, IP_ACTIVE_LOW );
	//	PORT_BIT(     0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );/* Analyzer */
	//	PORT_BIT(     0xF0, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,2),
		3,
		new int[] { RGN_FRAC(1,2)+4, 0, 4 },
		new int[] { 0, 1, 2, 3, 8*8+0, 8*8+1, 8*8+2, 8*8+3 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		16*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo1[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, &charlayout, 0, 32 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static GfxDecodeInfo gfxdecodeinfo2[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, &charlayout, 0, 64 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		1,	/* 1 chip */
		20000000/16,	/* 1.25 MHz ??? */
		new int[] { 30 },
		new ReadHandlerPtr[] { input_port_0_r },
		new ReadHandlerPtr[] { input_port_1_r },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	static struct MSM5205interface msm5205_interface =
	{
		1,					/* 1 chip             */
		500000,				/* 500KHz ?? (I don't know what I'm doing, really) */
		{ 0 },				/* interrupt function */
		{ MSM5205_SEX_4B },	/* vclk input mode    */
		{ MIXERG(100,MIXER_GAIN_2x,MIXER_PAN_CENTER) }
	};
	
	
	
	static MACHINE_DRIVER_START( rmhaihai )
	
		/* basic machine hardware */
		MDRV_CPU_ADD_TAG("main",Z80,20000000/4)	/* 5 MHz ??? */
		MDRV_CPU_FLAGS(CPU_16BIT_PORT)
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_PORTS(readport,writeport)
		MDRV_CPU_VBLANK_INT(irq0_line_hold,1)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER|VIDEO_PIXEL_ASPECT_RATIO_1_2)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(4*8, 60*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo1)
		MDRV_PALETTE_LENGTH(0x100)
	
		MDRV_PALETTE_INIT(RRRR_GGGG_BBBB)
		MDRV_VIDEO_START(rmhaihai)
		MDRV_VIDEO_UPDATE(rmhaihai)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, ay8910_interface)
		MDRV_SOUND_ADD(MSM5205, msm5205_interface)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( rmhaisei )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(rmhaihai)
	
		/* video hardware */
		MDRV_GFXDECODE(gfxdecodeinfo2)
		MDRV_PALETTE_LENGTH(0x200)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( themj )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(rmhaihai)
	
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(themj_readmem,writemem)
		MDRV_CPU_PORTS(readport,themj_writeport)
	
		MDRV_MACHINE_INIT(themj)
	
		/* video hardware */
		MDRV_GFXDECODE(gfxdecodeinfo2)
		MDRV_PALETTE_LENGTH(0x200)
	MACHINE_DRIVER_END
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadHandlerPtr rom_rmhaihai = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "s3-6.11g",     0x00000, 0x2000, CRC(e7af7ba2) SHA1(1b0f87a16006a96e5b59e055966addac3e2ca926) )
		ROM_CONTINUE(             0x06000, 0x2000 )
		ROM_LOAD( "s3-4.8g",      0x04000, 0x2000, CRC(f849e75c) SHA1(4636bcaa7cddb9bc012212098a25f3c57cfc6b51) )
		ROM_CONTINUE(             0x02000, 0x2000 )
		ROM_LOAD( "s3-2.6g",      0x08000, 0x2000, CRC(d614532b) SHA1(99911c679ff6f990ae493bfc0b71a2fff0ef1796) )
		ROM_CONTINUE(             0x0c000, 0x2000 )
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "s0-10.8a",     0x00000, 0x4000, CRC(797c63d1) SHA1(2ff9c3c61b28c34de97c0117b7eadb409d79df46) )
		ROM_LOAD( "s0-9.7a",      0x04000, 0x4000, CRC(b2526747) SHA1(73d0a19a5bb83e8977e94a47abbb65f9c7788c78) )
		ROM_LOAD( "s0-8.6a",      0x08000, 0x4000, CRC(146eaa31) SHA1(0e38aab52ff9bf0d42fea24caeee6ca90d63ace2) )
		ROM_LOAD( "s1-7.5a",      0x0c000, 0x4000, CRC(be59e742) SHA1(19d253f72f760f6350f76b313cf8aca7e3f90e8d) )
		ROM_LOAD( "s0-12.11a",    0x10000, 0x4000, CRC(e4229389) SHA1(b14d7855b66fe03c1485cb735cb20f59f19f248f) )
		ROM_LOAD( "s1-11.10a",    0x14000, 0x4000, CRC(029ef909) SHA1(fd867b8e1ccd5b88f18409ff17939ec8420c6131) )
		/* 0x18000-0x1ffff empty space filled by the init function */
	
		ROM_REGION( 0x0300, REGION_PROMS, ROMREGION_DISPOSE )
		ROM_LOAD( "s2.13b",       0x0000, 0x0100, CRC(911d32a5) SHA1(36f2b62009918862c13f3eda05a21403b4d9607f) )
		ROM_LOAD( "s1.13a",       0x0100, 0x0100, CRC(e9be978a) SHA1(50c7ca7a7496cb6fe5e8ce0db693ccb82dbbb8c6) )
		ROM_LOAD( "s3.13c",       0x0200, 0x0100, CRC(609775a6) SHA1(70a787aec0852e106216a4ca9891d36aef60b189) )
	
		ROM_REGION( 0x8000, REGION_SOUND1, 0 )	/* ADPCM samples, read directly by the main CPU */
		ROM_LOAD( "s0-1.5g",      0x00000, 0x8000, CRC(65e55b7e) SHA1(3852fb3b37eccdcddff05d8ef4a742fcb8b63473) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_rmhaihib = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "s-30-6.11g",   0x00000,  0x2000, CRC(f3e13cc8) SHA1(7eb9b17ea9efb5b2891ec40a9ff9744e84c0511c) )
		ROM_CONTINUE(             0x06000,  0x2000 )
		ROM_LOAD( "s-30-4.8g",    0x04000,  0x2000, CRC(f6642584) SHA1(5160baf267fd5dd8385ea5a9ff82e9c220fee342) )
		ROM_CONTINUE(             0x02000,  0x2000 )
		ROM_LOAD( "s-30-2.6g",    0x08000,  0x2000, CRC(e5959703) SHA1(15552d90296d0b6790642f554d08e79e827a16ee) )
		ROM_CONTINUE(             0x0c000,  0x2000 )
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "s0-10.8a",     0x00000, 0x4000, CRC(797c63d1) SHA1(2ff9c3c61b28c34de97c0117b7eadb409d79df46) )
		ROM_LOAD( "s0-9.7a",      0x04000, 0x4000, CRC(b2526747) SHA1(73d0a19a5bb83e8977e94a47abbb65f9c7788c78) )
		ROM_LOAD( "s0-8.6a",      0x08000, 0x4000, CRC(146eaa31) SHA1(0e38aab52ff9bf0d42fea24caeee6ca90d63ace2) )
		ROM_LOAD( "s1-7.5a",      0x0c000, 0x4000, CRC(be59e742) SHA1(19d253f72f760f6350f76b313cf8aca7e3f90e8d) )
		ROM_LOAD( "s0-12.11a",    0x10000, 0x4000, CRC(e4229389) SHA1(b14d7855b66fe03c1485cb735cb20f59f19f248f) )
		ROM_LOAD( "s1-11.10a",    0x14000, 0x4000, CRC(029ef909) SHA1(fd867b8e1ccd5b88f18409ff17939ec8420c6131) )
		/* 0x18000-0x1ffff empty space filled by the init function */
	
		ROM_REGION( 0x0300, REGION_PROMS, ROMREGION_DISPOSE )
		ROM_LOAD( "s2.13b",       0x0000, 0x0100, CRC(911d32a5) SHA1(36f2b62009918862c13f3eda05a21403b4d9607f) )
		ROM_LOAD( "s1.13a",       0x0100, 0x0100, CRC(e9be978a) SHA1(50c7ca7a7496cb6fe5e8ce0db693ccb82dbbb8c6) )
		ROM_LOAD( "s3.13c",       0x0200, 0x0100, CRC(609775a6) SHA1(70a787aec0852e106216a4ca9891d36aef60b189) )
	
		ROM_REGION( 0x8000, REGION_SOUND1, 0 )	/* ADPCM samples, read directly by the main CPU */
		ROM_LOAD( "s0-1.5g",      0x00000, 0x8000, CRC(65e55b7e) SHA1(3852fb3b37eccdcddff05d8ef4a742fcb8b63473) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_rmhaijin = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "s-4-6.11g",    0x00000, 0x2000, CRC(474c9ace) SHA1(9161a5c64054f079d57676f3d7f61ca149018f61) )
		ROM_CONTINUE(             0x06000, 0x2000 )
		ROM_LOAD( "s-4-4.8g",     0x04000, 0x2000, CRC(c76ab584) SHA1(7d76fa6166108d6a511d5311c0d34b55364afec1) )
		ROM_CONTINUE(             0x02000, 0x2000 )
		ROM_LOAD( "s-4-2.6g",     0x08000, 0x2000, CRC(77b16f5b) SHA1(5e91b6b34ab8196a246c428b98f47a5b167dca76) )
		ROM_CONTINUE(             0x0c000, 0x2000 )
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "s-1-10.8a",    0x00000, 0x4000, CRC(797c63d1) SHA1(2ff9c3c61b28c34de97c0117b7eadb409d79df46) )
		ROM_LOAD( "s-1-9.7a",     0x04000, 0x4000, CRC(5d3793d4) SHA1(43665d44ab2db42a28243c269ca451c90fe60abc) )
		ROM_LOAD( "s-1-8.6a",     0x08000, 0x4000, CRC(6fcd990b) SHA1(c7e35c6d9d75cd743d23a78de5dab63e034e33a8) )
		ROM_LOAD( "s-2-7.5a",     0x0c000, 0x4000, CRC(e92658bd) SHA1(db4b55bb10c38357729bb0f59a9ff66f4b81a220) )
		ROM_LOAD( "s-1-12.11a",   0x10000, 0x4000, CRC(7502a191) SHA1(e3543a2cf78d4046a580d972f68a4f10aa066144) )
		ROM_LOAD( "s-2-11.10a",   0x14000, 0x4000, CRC(9ebbc607) SHA1(8ab707f2a197772bae94e9129eb3f40d408c88bf) )
		/* 0x18000-0x1ffff empty space filled by the init function */
	
		ROM_REGION( 0x0300, REGION_PROMS, ROMREGION_DISPOSE )
		ROM_LOAD( "s5.13b",       0x0000, 0x0100, CRC(153aa7bf) SHA1(945db334e27be431a34670b2d94de639f67038d1) )
		ROM_LOAD( "s4.13a",       0x0100, 0x0100, CRC(5d643e6e) SHA1(df34be9d4cb0129069c2ed40c916c84674b62bb3) )
		ROM_LOAD( "s6.13c",       0x0200, 0x0100, CRC(fd6ff344) SHA1(cd00985f8bbff1ab5a149a00320d861ac8655bf8) )
	
		ROM_REGION( 0x8000, REGION_SOUND1, 0 )	/* ADPCM samples, read directly by the main CPU */
		ROM_LOAD( "s-0-1.5g",     0x00000, 0x8000, CRC(65e55b7e) SHA1(3852fb3b37eccdcddff05d8ef4a742fcb8b63473) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_rmhaisei = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "sei-11.h11",   0x00000, 0x2000, CRC(7c35692b) SHA1(8890ca90ae84c63bfd2b4857bbdd02bd9a2f29a9) )
		ROM_CONTINUE(             0x06000, 0x2000 )
		ROM_LOAD( "sei-10.h8",    0x04000, 0x2000, CRC(cbd58124) SHA1(562eb13c2dc441294b1b7dafe37ac27a9b7bba2b) )
		ROM_CONTINUE(             0x02000, 0x2000 )
		ROM_LOAD( "sei-8.h6",     0x08000, 0x2000, CRC(8c8dc2fd) SHA1(7744ff7d4ad6888256c43a33dfd7f5c0d5be5815) )
		ROM_CONTINUE(             0x0c000, 0x2000 )
		ROM_LOAD( "sei-9.h7",     0x0e000, 0x2000, CRC(9132368d) SHA1(ca0924399cdd1554fc0407719c74d492743db156) )
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "sei-4.a8",     0x00000, 0x8000, CRC(6a0234bf) SHA1(ad6642aa6fca84a22625265a7c82f50e307ba2f9) )
		ROM_LOAD( "sei-3.a7",     0x08000, 0x8000, CRC(c48bc39f) SHA1(de5aca9f72b437b7e7559bbd4b22c1b3ab70e450) )
		ROM_LOAD( "sei-2.a6",     0x10000, 0x8000, CRC(e479ba47) SHA1(b2bda054cd70181e223fe33d63924b029d196676) )
		ROM_LOAD( "sei-1.a5",     0x18000, 0x8000, CRC(fe6555f8) SHA1(b3201f465f9e897ec5805512e3ff488ef77f2f25) )
		ROM_LOAD( "sei-6.a11",    0x20000, 0x8000, CRC(86f1b462) SHA1(ccabbdca44840de5f9b8f6af24117e545b8f1ef7) )
		ROM_LOAD( "sei-5.a9",     0x28000, 0x8000, CRC(8bf780bc) SHA1(5ef72ee3f45f1cdde06131797faf26a9776f6a13) )
		/* 0x30000-0x3ffff empty space filled by the init function */
	
		ROM_REGION( 0x0600, REGION_PROMS, ROMREGION_DISPOSE )
		ROM_LOAD( "2.bpr",        0x0000, 0x0200, CRC(9ad2afcd) SHA1(6cd4cd5f693ee882a98598e8f86ee2baf3b105bf) )
		ROM_LOAD( "1.bpr",        0x0200, 0x0200, CRC(9b036f82) SHA1(4b14084e5a6674e69bd4bbc3a483c277bfc73808) )
		ROM_LOAD( "3.bpr",        0x0400, 0x0200, CRC(0fa1a50a) SHA1(9e8a2c9554a61bfdacb434f8c22c1085b1c93aa1) )
	
		ROM_REGION( 0x8000, REGION_SOUND1, 0 )	/* ADPCM samples, read directly by the main CPU */
		ROM_LOAD( "sei-7.h5",     0x00000, 0x8000, CRC(3e412c1a) SHA1(bc5e324ea26b8dd1e37c4e8b0d7ba712c1222bc7) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_themj = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x20000, REGION_CPU1, 0 ) /* CPU */
		ROM_LOAD( "t7.bin",       0x00000,  0x02000, CRC(a58563c3) SHA1(53faeb66606214eb97ef8ff9affe68705e18a0b3) )
		ROM_CONTINUE(             0x06000,  0x02000 )
		ROM_LOAD( "t8.bin",       0x04000,  0x02000, CRC(bdf29475) SHA1(6296561da9c3a299d69bba8a98362c40b677ea9a) )
		ROM_CONTINUE(             0x02000,  0x02000 )
		ROM_LOAD( "t9.bin",       0x0e000,  0x02000, CRC(d5537d03) SHA1(ba27e83fcc9b6962373e2f723fc681481ec76864) )
		ROM_LOAD( "no1.bin",      0x10000,  0x10000, CRC(a67dd977) SHA1(835648c5df51053c883d90d7309e53232b945ceb) ) /* banked */
	
		ROM_REGION( 0x40000, REGION_GFX1, ROMREGION_DISPOSE ) /* gfx */
		ROM_LOAD( "t3.bin",       0x00000,  0x8000, CRC(f0735c62) SHA1(5ff0da7fc72512797ec59ee57467fa81abcfdb8b) )
		ROM_LOAD( "t4.bin",       0x08000,  0x8000, CRC(952227fa) SHA1(7c2b5fe18bbaa482d93ab99a8f886838b596df8d) )
		ROM_LOAD( "t5.bin",       0x10000,  0x8000, CRC(3deea9b4) SHA1(e445b545a8d293f6a5724e6c484cb1062c631bcc) )
		ROM_LOAD( "t6.bin",       0x18000,  0x8000, CRC(47717958) SHA1(b25a9bd72bf5aa024ce2631440bb2ad762544e54) )
		ROM_LOAD( "t1.bin",       0x20000,  0x8000, CRC(9b9a458e) SHA1(91146bd3ed7ed016c90ae5c3e3510d0d8d216ba5) )
		ROM_LOAD( "t2.bin",       0x28000,  0x8000, CRC(4702375f) SHA1(9e824007e3e26ad6fb2ccbbcf35aa7cfdf5c469e) )
		/* 0x30000-0x3ffff empty space filled by the init function */
	
		ROM_REGION( 0x0600, REGION_PROMS, ROMREGION_DISPOSE )
		ROM_LOAD( "5.bin",        0x0000,  0x0200, CRC(062fb055) SHA1(20a6d236e3ab1df8c471cccca31ec05442595c82) )
		ROM_LOAD( "4.bin",        0x0200,  0x0200, CRC(9f81a6d7) SHA1(2735815c0c922d0c81559d792fcaa39bd9615536) )
		ROM_LOAD( "6.bin",        0x0400,  0x0200, CRC(61373ec7) SHA1(73861914aae29e3996f9991f324c358a29c46969) )
	
		ROM_REGION( 0x8000, REGION_SOUND1, 0 )	/* ADPCM samples, read directly by the main CPU */
		ROM_LOAD( "t0.bin",       0x00000,  0x8000, CRC(3e412c1a) SHA1(bc5e324ea26b8dd1e37c4e8b0d7ba712c1222bc7) )
	ROM_END(); }}; 
	
	
	public static DriverInitHandlerPtr init_rmhaihai  = new DriverInitHandlerPtr() { public void handler(){
		data8_t *rom = memory_region(REGION_GFX1);
		int size = memory_region_length(REGION_GFX1);
		int a,b;
	
		size /= 2;
		rom += size;
	
		/* unpack the high bit of gfx */
		for (b = size - 0x4000;b >= 0;b -= 0x4000)
		{
			if (b) memcpy(rom + b,rom + b/2,0x2000);
	
			for (a = 0;a < 0x2000;a++)
			{
				rom[a + b + 0x2000] = rom[a + b] >> 4;
			}
		}
	} };
	
	
	public static GameDriver driver_rmhaihai	   = new GameDriver("1985"	,"rmhaihai"	,"rmhaihai.java"	,rom_rmhaihai,null	,machine_driver_rmhaihai	,input_ports_rmhaihai	,init_rmhaihai	,ROT0, "Alba",  "Real Mahjong Haihai (Japan)" )
	public static GameDriver driver_rmhaihib	   = new GameDriver("1985"	,"rmhaihib"	,"rmhaihai.java"	,rom_rmhaihib,driver_rmhaihai	,machine_driver_rmhaihai	,input_ports_rmhaihib	,init_rmhaihai	,ROT0, "Alba",  "Real Mahjong Haihai [BET] (Japan)" )
	public static GameDriver driver_rmhaijin	   = new GameDriver("1986"	,"rmhaijin"	,"rmhaihai.java"	,rom_rmhaijin,null	,machine_driver_rmhaihai	,input_ports_rmhaihai	,init_rmhaihai	,ROT0, "Alba",  "Real Mahjong Haihai Jinji Idou Hen (Japan)" )
	public static GameDriver driver_rmhaisei	   = new GameDriver("1986"	,"rmhaisei"	,"rmhaihai.java"	,rom_rmhaisei,null	,machine_driver_rmhaisei	,input_ports_rmhaihai	,init_rmhaihai	,ROT0, "Visco", "Real Mahjong Haihai Seichouhen (Japan)" )
	public static GameDriver driver_themj	   = new GameDriver("1987"	,"themj"	,"rmhaihai.java"	,rom_themj,null	,machine_driver_themj	,input_ports_rmhaihai	,init_rmhaihai	,ROT0, "Visco", "The Mah-jong (Japan)" )
}
