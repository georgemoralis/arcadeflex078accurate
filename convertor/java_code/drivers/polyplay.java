/***************************************************************************

	  Poly-Play
	  (c) 1985 by VEB Polytechnik Karl-Marx-Stadt

	  driver by Martin Buchholz (buchholz@mail.uni-greifswald.de)

	  Very special thanks to the following people, each one of them spent
	  some of their spare time to make this driver working:
	  - Juergen Oppermann and Volker Hann for electronical assistance,
	    repair work and ROM dumping.
	  - Jan-Ole Christian from the Videogamemuseum in Berlin, which houses
	    one of the last existing Poly-Play arcade automatons. He also
	    provided me with schematics and service manuals.


memory map:

0000 - 03ff OS ROM
0400 - 07ff Game ROM (used for Abfahrtslauf)
0800 - 0cff Menu Screen ROM

0d00 - 0fff work RAM

1000 - 4fff GAME ROM (pcb 2 - Abfahrtslauf          (1000 - 1bff)
                              Hirschjagd            (1c00 - 27ff)
                              Hase und Wolf         (2800 - 3fff)
                              Schmetterlingsfang    (4000 - 4fff)
5000 - 8fff GAME ROM (pcb 1 - Schiessbude           (5000 - 5fff)
                              Autorennen            (6000 - 73ff)
                              opto-akust. Merkspiel (7400 - 7fff)
                              Wasserrohrbruch       (8000 - 8fff)

e800 - ebff character ROM (chr 00..7f) 1 bit per pixel
ec00 - f7ff character RAM (chr 80..ff) 3 bit per pixel
f800 - ffff video RAM

I/O ports:

read:

83        IN1
          used as hardware random number generator

84        IN0
          bit 0 = fire button
          bit 1 = right
          bit 2 = left
          bit 3 = up
          bit 4 = down
          bit 5 = unused
          bit 6 = Summe Spiele
          bit 7 = coinage (+IRQ to make the game acknowledge it)

85        bit 0-4 = light organ (unemulated :)) )
          bit 5-7 = sound parameter (unemulated, it's very difficult to
                    figure out how those work)

86        ???

87        PIO Control register

write:
80	      Sound Channel 1
81        Sound Channel 2
82        generates 40 Hz timer for timeout in game title screens
83        generates main 75 Hz timer interrupt

The Poly-Play has a simple bookmarking system which can be activated
setting Bit 6 of PORTA (Summe Spiele) to low. It reads a double word
from 0c00 and displays it on the screen.
I currently haven't figured out how the I/O port handling for the book-
mark system works.

Uniquely the Poly-Play has a light organ which totally confuses you whilst
playing the automaton. Bits 1-5 of PORTB control the organ but it's not
emulated now. ;)

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class polyplay
{
	
	/* video hardware access */
	
	/* I/O Port handling */
	
	/* sound handling */
	void set_channel1(int active);
	void set_channel2(int active);
	static int prescale1;
	static int prescale2;
	static int channel1_active;
	static int channel1_const;
	static int channel2_active;
	static int channel2_const;
	void play_channel1(int data);
	void play_channel2(int data);
	int  polyplay_sh_start(const struct MachineSound *msound);
	void polyplay_sh_stop(void);
	void polyplay_sh_update(void);
	
	/* timer handling */
	static void timer_callback(int param);
	static void* polyplay_timer;
	
	
	/* Polyplay Sound Interface */
	static struct CustomSound_interface custom_interface =
	{
		polyplay_sh_start,
		polyplay_sh_stop,
		polyplay_sh_update
	};
	
	
	public static MachineInitHandlerPtr machine_init_polyplay  = new MachineInitHandlerPtr() { public void handler(){
		channel1_active = 0;
		channel1_const = 0;
		channel2_active = 0;
		channel2_const = 0;
	
		set_channel1(0);
		play_channel1(0);
		set_channel2(0);
		play_channel2(0);
	
		polyplay_timer = timer_alloc(timer_callback);
	} };
	
	
	public static InterruptHandlerPtr periodic_interrupt = new InterruptHandlerPtr() {public void handler(){
		cpu_set_irq_line_and_vector(0, 0, HOLD_LINE, 0x4e);
	} };
	
	
	public static InterruptHandlerPtr coin_interrupt = new InterruptHandlerPtr() {public void handler(){
		static int last = 0;
	
		if (readinputport(0) & 0x80)
		{
			last = 0;
		}
		else
		{
			if (last == 0)    /* coin inserted */
			{
				cpu_set_irq_line_and_vector(0, 0, HOLD_LINE, 0x50);
			}
	
			last = 1;
		}
	} };
	
	
	/* memory mapping */
	public static Memory_ReadAddress polyplay_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x0bff, MRA_ROM ),
		new Memory_ReadAddress( 0x0c00, 0x0fff, MRA_RAM ),
		new Memory_ReadAddress( 0x1000, 0x8fff, MRA_ROM ),
		new Memory_ReadAddress( 0xe800, 0xebff, MRA_ROM ),
		new Memory_ReadAddress( 0xec00, 0xf7ff, polyplay_characterram_r ),
		new Memory_ReadAddress( 0xf800, 0xffff, videoram_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress polyplay_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x0bff, MWA_ROM ),
		new Memory_WriteAddress( 0x0c00, 0x0fff, MWA_RAM ),
		new Memory_WriteAddress( 0x1000, 0x8fff, MWA_ROM ),
		new Memory_WriteAddress( 0xe800, 0xebff, MWA_ROM ),
		new Memory_WriteAddress( 0xec00, 0xf7ff, polyplay_characterram_w, polyplay_characterram ),
		new Memory_WriteAddress( 0xf800, 0xffff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	/* port mapping */
	public static IO_ReadPort readport_polyplay[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x84, 0x84, input_port_0_r ),
		new IO_ReadPort( 0x83, 0x83, polyplay_random_read ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport_polyplay[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x80, 0x81, polyplay_sound_channel ),
		new IO_WritePort( 0x82, 0x82, polyplay_start_timer2 ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	static InputPortHandlerPtr input_ports_polyplay = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( polyplay )
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BITX(0x40, IP_ACTIVE_LOW, IPT_SERVICE, "Bookkeeping Info", KEYCODE_F2, IP_JOY_NONE );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	INPUT_PORTS_END(); }}; 
	
	
	public static WriteHandlerPtr polyplay_sound_channel = new WriteHandlerPtr() {public void handler(int offset, int data){
		switch(offset) {
		case 0x00:
			if (channel1_const) {
				if (data <= 1) {
					set_channel1(0);
				}
				channel1_const = 0;
				play_channel1(data*prescale1);
	
			}
			else {
				prescale1 = (data & 0x20) ? 16 : 1;
				if (data & 0x04) {
					set_channel1(1);
					channel1_const = 1;
				}
				if ((data == 0x41) || (data == 0x65) || (data == 0x45)) {
					set_channel1(0);
					play_channel1(0);
				}
			}
			break;
		case 0x01:
			if (channel2_const) {
				if (data <= 1) {
					set_channel2(0);
				}
				channel2_const = 0;
				play_channel2(data*prescale2);
	
			}
			else {
				prescale2 = (data & 0x20) ? 16 : 1;
				if (data & 0x04) {
					set_channel2(1);
					channel2_const = 1;
				}
				if ((data == 0x41) || (data == 0x65) || (data == 0x45)) {
					set_channel2(0);
					play_channel2(0);
				}
			}
			break;
		}
	} };
	
	public static WriteHandlerPtr polyplay_start_timer2 = new WriteHandlerPtr() {public void handler(int offset, int data){
		if (data == 0x03)
			timer_adjust(polyplay_timer, TIME_NEVER, 0, 0);
	
		if (data == 0xb5)
			timer_adjust(polyplay_timer, TIME_IN_HZ(40), 0, TIME_IN_HZ(40));
	} };
	
	public static ReadHandlerPtr polyplay_random_read  = new ReadHandlerPtr() { public int handler(int offset){
		return rand() & 0xff;
	} };
	
	/* graphic structures */
	static GfxLayout charlayout_1_bit = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		128,	/* 128 characters */
		1,  	/* 1 bit per pixel */
		new int[] { 0 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	
	static GfxLayout charlayout_3_bit = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		128,	/* 128 characters */
		3,  	/* 3 bit per pixel */
		new int[] { 0, 128*8*8, 128*8*8 + 128*8*8 },    /* offset for each bitplane */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_CPU1, 0xe800, charlayout_1_bit, 0, 1 ),
		new GfxDecodeInfo( REGION_CPU1, 0xec00, charlayout_3_bit, 2, 1 ),
		new GfxDecodeInfo( -1 )	/* end of array */
	};
	
	
	/* the machine driver */
	
	public static MachineHandlerPtr machine_driver_polyplay = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 9830400/4)
		MDRV_CPU_MEMORY(polyplay_readmem,polyplay_writemem)
		MDRV_CPU_PORTS(readport_polyplay,writeport_polyplay)
		MDRV_CPU_PERIODIC_INT(periodic_interrupt,75)
		MDRV_CPU_VBLANK_INT(coin_interrupt,1)
	
		MDRV_FRAMES_PER_SECOND(50)
	
		MDRV_MACHINE_INIT(polyplay)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(64*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 64*8-1, 0*8, 32*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(10)
	
		MDRV_PALETTE_INIT(polyplay)
		MDRV_VIDEO_START(generic)
		MDRV_VIDEO_UPDATE(polyplay)
	
		/* sound hardware */
		MDRV_SOUND_ADD(CUSTOM, custom_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	/* ROM loading and mapping */
	static RomLoadHandlerPtr rom_polyplay = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "cpu_0000.37",       0x0000, 0x0400, CRC(87884c5f) SHA1(849c6b3f40496c694a123d6eec268a7128c037f0) )
		ROM_LOAD( "cpu_0400.36",       0x0400, 0x0400, CRC(d5c84829) SHA1(baa8790e77db66e1e543b3a0e5390cc71256de2f) )
		ROM_LOAD( "cpu_0800.35",       0x0800, 0x0400, CRC(5f36d08e) SHA1(08ecf8143e818a9844b4f168e68629d6d4481a8a) )
		ROM_LOAD( "2_-_1000.14",       0x1000, 0x0400, CRC(950dfcdb) SHA1(74170d5c99d1ea61fe37d1fe023dca96efb1ca69) )
		ROM_LOAD( "2_-_1400.10",       0x1400, 0x0400, CRC(829f74ca) SHA1(4df9d3c24e1bc4c2c953dce9530e43a00ecf67fc) )
		ROM_LOAD( "2_-_1800.6",        0x1800, 0x0400, CRC(b69306f5) SHA1(66d7c3cf76782a5b6eafa3e1513ecc9a9df0e0e1) )
		ROM_LOAD( "2_-_1c00.2",        0x1c00, 0x0400, CRC(aede2280) SHA1(0a01394ab70d07d666e955c87a08cb4d4945767e) )
		ROM_LOAD( "2_-_2000.15",       0x2000, 0x0400, CRC(6c7ad0d8) SHA1(df959d1e43fde96b5e21e3c53b397209a98ea423) )
		ROM_LOAD( "2_-_2400.11",       0x2400, 0x0400, CRC(bc7462f0) SHA1(01ca680c74b92b9ba5a85f98e0933ef1e754bfc1) )
		ROM_LOAD( "2_-_2800.7",        0x2800, 0x0400, CRC(9ccf1958) SHA1(6bdf04d7796074af7327fab6717b52736540f97c) )
		ROM_LOAD( "2_-_2c00.3",        0x2c00, 0x0400, CRC(21827930) SHA1(71d27d68f6973a59996102381f8754d9b353c65a) )
		ROM_LOAD( "2_-_3000.16",       0x3000, 0x0400, CRC(b3b3c0ec) SHA1(a94cd9794d59ea2f9ddd8bef86e6e3a269b276ad) )
		ROM_LOAD( "2_-_3400.12",       0x3400, 0x0400, CRC(bd416cd0) SHA1(57391cc4a417468455b45014969067629fd629b8) )
		ROM_LOAD( "2_-_3800.8",        0x3800, 0x0400, CRC(1c470b7c) SHA1(f7c71ee1752ecd4f30a35f14ee392b37febefb9c) )
		ROM_LOAD( "2_-_3c00.4",        0x3c00, 0x0400, CRC(b8354a19) SHA1(58ea7798ecc1be987b1217f4078c7cb366622dd3) )
		ROM_LOAD( "2_-_4000.17",       0x4000, 0x0400, CRC(1e01041e) SHA1(ff63e4bb924d1c26e445a28c5f8cbc696b4b9f5a) )
		ROM_LOAD( "2_-_4400.13",       0x4400, 0x0400, CRC(fe4d8959) SHA1(233f97956f4c819558d5d38034d92edc0e86a0de) )
		ROM_LOAD( "2_-_4800.9",        0x4800, 0x0400, CRC(c45f1d9d) SHA1(f3373f1f5a3c6099fd38e65f66e024ef042a984c) )
		ROM_LOAD( "2_-_4c00.5",        0x4c00, 0x0400, CRC(26950ad6) SHA1(881f5f0f4806ba6f21d0b28a70fc43363d51419b) )
		ROM_LOAD( "1_-_5000.30",       0x5000, 0x0400, CRC(9f5e2ba1) SHA1(58c696afbda8932f5e401b0a82b2de5cdfc2d1fb) )
		ROM_LOAD( "1_-_5400.26",       0x5400, 0x0400, CRC(b5f9a780) SHA1(eb785b7668f6af0a9df84cbd1905173869377e6c) )
		ROM_LOAD( "1_-_5800.22",       0x5800, 0x0400, CRC(d973ad12) SHA1(81cc5e19e83f2e5b10b885583c250a2ff66bafe5) )
		ROM_LOAD( "1_-_5c00.18",       0x5c00, 0x0400, CRC(9c22ea79) SHA1(e25ed745589a83e297dba936a6e5979f1b31b2d5) )
		ROM_LOAD( "1_-_6000.31",       0x6000, 0x0400, CRC(245c49ca) SHA1(12e5a032327fb45b2a240aff11b0c5d1798932f4) )
		ROM_LOAD( "1_-_6400.27",       0x6400, 0x0400, CRC(181e427e) SHA1(6b65409cd8410e632093662f5de2989dd9134620) )
		ROM_LOAD( "1_-_6800.23",       0x6800, 0x0400, CRC(8a6c1f97) SHA1(bf9d4dda8ac933a4a700f52540dcd1197f0a64eb) )
		ROM_LOAD( "1_-_6c00.19",       0x6c00, 0x0400, CRC(77901dc9) SHA1(b1132e06011aa8f7a95c43f447cd422f01139bb1) )
		ROM_LOAD( "1_-_7000.32",       0x7000, 0x0400, CRC(83ffbe57) SHA1(1e06408f7b4c9a4e5cadab58f6efbc03a5bedc1e) )
		ROM_LOAD( "1_-_7400.28",       0x7400, 0x0400, CRC(e2a66531) SHA1(1c9eb54e9c8a13f26335d8fb79fe5e39c28b3255) )
		ROM_LOAD( "1_-_7800.24",       0x7800, 0x0400, CRC(1d0803ef) SHA1(15a1996f9262f26cf531f329e086b10b3c25ce92) )
		ROM_LOAD( "1_-_7c00.20",       0x7c00, 0x0400, CRC(17dfa7e4) SHA1(afb471dc6cb2faccfb4305540f75162fcee3d622) )
		ROM_LOAD( "1_-_8000.33",       0x8000, 0x0400, CRC(6ee02375) SHA1(fbf797b655639ee442804a30fd3a06bbf261999a) )
		ROM_LOAD( "1_-_8400.29",       0x8400, 0x0400, CRC(9db09598) SHA1(8eb385542a617b23caad3ce7bbdd9714c1dd684f) )
		ROM_LOAD( "1_-_8800.25",       0x8800, 0x0400, CRC(ca2f963f) SHA1(34295f02bfd1bca141d650bbbbc1989e01c67b2f) )
		ROM_LOAD( "1_-_8c00.21",       0x8c00, 0x0400, CRC(0c7dec2d) SHA1(48d776b97c1eca851f89b0c5df4d5765d9aa0319) )
		ROM_LOAD( "char.1",            0xe800, 0x0400, CRC(5242dd6b) SHA1(ba8f317df62fe4360757333215ce3c8223c68c4e) )
	ROM_END(); }}; 
	
	
	static void timer_callback(int param)
	{
		cpu_set_irq_line_and_vector(0, 0, HOLD_LINE, 0x4c);
	}
	
	/* game driver */
	public static GameDriver driver_polyplay	   = new GameDriver("1985"	,"polyplay"	,"polyplay.java"	,rom_polyplay,null	,machine_driver_polyplay	,input_ports_polyplay	,null	,ROT0, "VEB Polytechnik Karl-Marx-Stadt", "Poly-Play" )
}
