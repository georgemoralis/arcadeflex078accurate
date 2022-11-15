/*****************************************************************************

		ToaPlan      game hardware from 1991 - 1994
		Raizing/8ing game hardware from 1993 onwards
		-------------------------------------------------
		Driver by: Quench and Yochizo

   Raizing games and Truxton 2 are heavily dependent on the Raine source -
   many thanks to Richard Bush and the Raine team. [Yochizo]



Supported games:

	Name		Board No	Maker			Game name
	----------------------------------------------------------------------------
	tekipaki	TP-020		Toaplan		Teki Paki
	ghox		TP-021		Toaplan		Ghox
	dogyuun		TP-022		Toaplan		Dogyuun
	kbash		TP-023		Toaplan		Knuckle Bash
	truxton2	TP-024		Toaplan		Truxton 2 / Tatsujin 2
	pipibibs	TP-025		Toaplan		Pipi & Bibis
	whoopee		TP-025		Toaplan		Whoopee
	pipibibi	bootleg?	Toaplan		Pipi & Bibis
	fixeight	TP-026		Toaplan		FixEight
	grindstm	TP-027		Toaplan		Grind Stormer  (1992)
	grindsta	TP-027		Toaplan		Grind Stormer  (1992) (older)
	vfive		TP-027		Toaplan		V-V  (V-Five)  (1993 - Japan only)
	batsugun	TP-030		Toaplan		Batsugun
	batugnsp	TP-030		Toaplan		Batsugun  (Special Version)
	snowbro2	??????		Toaplan		Snow Bros. 2 - With New Elves

	sstriker	RA-MA7893-01	Raizing		Sorcer Striker
	mahoudai	RA-MA7893-01	Raizing		Mahou Daisakusen
	kingdmgp	RA-MA9402-03	Raizing/8ing	Kingdom Grandprix
	shippumd	RA-MA9402-03	Raizing/8ing	Shippu Mahou Daisakusen
	battleg		RA9503		Raizing/8ing	Battle Garegga (Type 2)
	battlega	RA9503		Raizing/8ing	Battle Garegga
	battlegb	RA9503		Raizing/8ing	Battle Garegga (New Version?)
	batrider	RA9704		Raizing/8ing	Armed Police Batrider - Rev B (Japan - Ver. Fri Feb 13 1998)
	batridra	RA9704		Raizing/8ing	Armed Police Batrider (Japan - Ver. Mon Dec 22 1997)
	batridrk	RA9704		Raizing/8ing	Armed Police Batrider (Korea - Ver. Fri Feb 13 1998)
	bbakraid	ET68-V99	8ing		Battle Bakraid (Japan - Wed Apr 7th, 1999)
	bbkraidu	ET68-V99	8ing		Battle Bakraid - unlimited version (Japan - Tue Jun 8th, 1999)

	SET NOTES:

	sstriker - might be a bootleg / hack, the region jumper is read with this set but even when set
	           to Japan you get all english text etc which seems a little odd, but might be correct.
	kingdmgp - might be a bootleg / hack, some of the tiles needed for the credits screen have been
	           stripped out, doesn't seem very professional, since its rare and should probably only
	           have a different graphics rom its nearly impossible for us to verify, a lot of boards
	           being sold as 'Kingdom Grand Prix' are infact conversions using Neill Corlett's
	           ghastly hack

	           ** update the above two look like genuine korean release boards, raizing were probably
	              just missed a few things

 ****************************************************************************
 * Battle Garegga and Armed Police Batrider have secret characters.			*
 * Try to input the following commands to use them.							*
 * ======================================================================== *
 * Battle Garegga															*
 *		After inserting a coin (pushing a credit button), input				*
 *		UP  UP  DOWN  DOWN  LEFT  RIGHT  LEFT  RIGHT  A  B  C  START		*
 *		then you can use Mahou Daisakusen characters.						*
 *																			*
 * ------------------------------------------------------------------------ *
 * Armed Police Batrider													*
 *		After inserting a coin (pushing a credit button), input				*
 *		UP  UP  DOWN  DOWN  LEFT  RIGHT  LEFT  RIGHT  A  B  START			*
 *		then you can use Mahou Daisakusen and Battle Garegga characters.	*
 *																			*
 ****************************************************************************


 ############################################################################
 # Battle Bakraid 'Unlimited Version' has unlocking codes to gain			#
 # access to extra players and game features.								#
 # Special thanks go to the 'R8ZING Shooter Tribute' page for finding		#
 # and publishing this info.												#
 #																			#
 # ======================================================================== #
 #		PLAYER SELECT: PHASE 2												#
 # Result:	3 more fighter planes available:								#
 # Code:	UP  UP  DOWN  DOWN  LEFT  RIGHT  LEFT  RIGHT  A  B  Start		#
 # Conditions:																#
 #		1. Start from the title screen										#
 #		2. Insert Coin														#
 #		3. Watch the 20 sec. counter and enter each part of the code right	#
 #		   between the counting.											#
 # Example: 12,up,11,up,10,down,9,down,8,left,7,right,6.left,5,r..			#
 # After entering the [B] button a chime should sound. Phase 2 unlocked!	#
 #																			#
 # ------------------------------------------------------------------------ #
 #		PLAYER SELECT: PHASE 3												#
 # Result:	2 more fighter planes available:								#
 # Code:	UP  UP  DOWN  DOWN  LEFT  RIGHT  LEFT  RIGHT  B  A  Start		#
 # Conditions:																#
 #		1. Start from the title screen										#
 #		2. Insert Coin														#
 #		3. Watch the 20 sec. counter and enter each part of the code right	#
 #		   between the counting.											#
 # Important: The entering of this code has to be finished before the		#
 # counter passes 10 ! To do so, you will have to start after coin			#
 # insertion, right before it starts to count:								#
 # Example: up,19,up,18,down,17,down,16,left,15,right,14.left,..			#
 # After entering the [A] button a chime should sound. Phase 3 unlocked!	#
 #																			#
 # ------------------------------------------------------------------------ #
 #		TEAM EDIT: ENABLE													#
 # Result:	Unlocks the 'team edit' feature to select a team of different	#
 # 			ships. See selection secrets on how to enter the team edit mode.#
 # Code:	UP  DOWN  UP  DOWN  LEFT  RIGHT  LEFT  RIGHT  A  B  Start		#
 # Conditions:																#
 #		1. Start from the title screen										#
 #		2. Insert Coin														#
 #		3. Watch the 20 sec. counter and enter each part of the code right	#
 #		   between the counting.											#
 # Important: This code hast to be entered so that the counter is at 0 when	#
 # you press the final button [B]. To do so, start after second 9:			#
 # Example: 9,up,8,down,7,up,6,down,5,left,4,right,3,left,2,right,1,A,0,B	#
 # After entering the [B] button a chime should sound. Team edit unlocked!	#
 #																			#
 # ------------------------------------------------------------------------ #
 #		SPECIAL COURSE : ENABLE												#
 # Result:	Unlocks the boss mode, a game mode where you fight the bosses	#
 # 			only.															#
 # Code:	UP  DOWN  UP  DOWN  LEFT  RIGHT  LEFT  RIGHT  B  A  Start		#
 # Conditions:																#
 #		1. Start from the title screen										#
 #		2. Hold [C] button													#
 #		3. Insert Coin														#
 #		4. Watch the 20 sec. counter and enter each part of the code right	#
 #		   between the counting.											#
 #		5. Release [C] button												#
 # After entering the [A] button a chime should sound. Special course		#
 # unlocked!																#
 ############################################################################



*************** Hardware Info ***************

CPU:
 MC68000P10
 TMP68HC000N-16

Sound CPU/MCU:
 HD647180X0FS6 (Hitachi Z180 Compatible CPU with inernal ROM code)
 Z84C0006PEC (Z80)


Sound Chips:
 YM3812
 YM2151
 YM2151 + YM3014
 YM2151 + M6295
 YM2151 + M6295 + M6295
 YMZ280B-F + YAC516-E (Digital to Analog Converter)


Graphics Custom 208pin QFP:
 GP9001 L7A0498 TOA PLAN

Toaplan / Raizing / 8ing games use different revisions of the custom
Toa Plan 208 pin QFP L7A0498 GP9001 series graphics processing chip:

Fixeight				L7A0498 GP9001 TOA PLAN 9150
Grind Stormer			L7A0498 GP9001 TOA PLAN 9150
Truxton II				L7A0498 GP9001 TOA PLAN 9152
Ghox					L7A0498 GP9001 TOA PLAN 9044
Armed Police Batrider	L7A0498 GP9001 TOA PLAN NNG 9217 WK94254
Battle Garegga			L7A0498 GP9001 TOA PLAN 9236
Mahou Daisakusen		L7A0498 GP9001 TOA PLAN 9240
Battle Bakraid			L7A0498 GP9001 TOA PLAN 9335


Game status:

Teki Paki                      Working, but no sound. Missing sound MCU dump
Ghox                           Working, but no sound. Missing sound MCU dump
Dogyuun                        Working, but no sound. MCU type unknown - its a Z?80 of some sort.
Knuckle Bash                   Working, but no sound. MCU dump exists, its a Z?80 of some sort.
Truxton 2                      Working.
Pipi & Bibis                   Working.
Whoopee                        Working. Missing sound MCU dump. Using bootleg sound CPU dump for now
Pipi & Bibis (Ryouta Kikaku)   Working.
FixEight                       Not working properly. Missing background GFX (controlled by MCU). MCU type unknown - its a Z?80 of some sort.
Grind Stormer                  Working, but no sound. MCU type unknown - its a Z?80 of some sort.
VFive                          Working, but no sound. MCU type unknown - its a Z?80 of some sort.
Batsugun                       Working, but no sound and wrong GFX priorities. MCU type unknown - its a Z?80 of some sort.
Batsugun Sp'                   Working, but no sound and wrong GFX priorities. MCU type unknown - its a Z?80 of some sort.
Snow Bros. 2                   Working.
Mahou Daisakusen               Working.
Shippu Mahou Daisakusen        Working.
Battle Garegga                 Working.
Armed Police Batrider          Working.
Battle Bakraid                 Working, but sound levels/panning/fading are bad


Notes:
	See Input Port definition header below, for instructions
	  on how to enter pause/slow motion modes.
	Code at $20A26 forces territory to Japan in V-Five. Some stuff
	  NOP'd at reset vector, and Z?80 CPU post test is skipped (bootleg ?)

To Do / Unknowns:
	- Whoopee/Teki Paki sometimes tests bit 5 of the territory port
		just after testing for vblank. Why ?
	- Whoppee is currently using the sound CPU ROM (Z80) from a differnt
		(pirate ?) version of Pipi and Bibis (Ryouta Kikaku copyright).
		It really has a HD647180 CPU, and its internal ROM needs to be dumped.
	- Fix top character text layer (implement the line position table).



*****************************************************************************/


/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class toaplan2
{
	
	
	/**************** Machine stuff ******************/
	#define HD64x180 0		/* Define if CPU support is available */
	#define Zx80     0
	
	#define CPU_2_NONE		0x00
	#define CPU_2_Z80		0x5a
	#define CPU_2_HD647180	0xa5
	#define CPU_2_Zx80		0xff
	
	/************ Machine RAM related values ************/
	static data8_t *toaplan2_shared_ram;
	static data8_t *raizing_shared_ram;		/* Shared ram used in Shippumd and Mahoudai */
	static data16_t *toaplan2_shared_ram16;	/* Really 8bit RAM connected to Z180 */
	static data16_t *Zx80_shared_ram;		/* Really 8bit RAM connected to Z180 */
	static data16_t *battleg_commram16;		/* Comm ram used in Battle Garegga */
	static data16_t *raizing_cpu_comm16;	/* Raizing commands for the Z80 */
	static data8_t  raizing_cpu_reply[2];	/* Raizing replies to the 68K */
	
	/************ Video RAM related values ************/
	extern data16_t *toaplan2_txvideoram16;
	extern data16_t *toaplan2_txvideoram16_offs;
	extern data16_t *toaplan2_txscrollram16;
	extern data16_t *toaplan2_tx_gfxram16;
	size_t toaplan2_tx_vram_size;
	size_t toaplan2_tx_offs_vram_size;
	size_t toaplan2_tx_scroll_vram_size;
	size_t paletteram_size;
	
	/********** Status related values **********/
	int toaplan2_sub_cpu = 0;
	static int mcu_data = 0;
	static int video_status;
	static int prev_scanline;
	//static int prev_beampos;
	static INT8 old_p1_paddle_h;			/* For Ghox */
	static INT8 old_p1_paddle_v;
	static INT8 old_p2_paddle_h;
	static INT8 old_p2_paddle_v;
	static int current_bank = 2;			/* Z80 bank used in Battle Garegga and Batrider */
	static int raizing_Z80_busreq;
	static int bbakraid_unlimited_ver;
	
	static int current_scanline = 0;
	static int vblank_irq;
	
	/**************** Video stuff ******************/
	WRITE16_HANDLER( toaplan2_0_voffs_w );
	WRITE16_HANDLER( toaplan2_1_voffs_w );
	
	READ16_HANDLER ( toaplan2_0_videoram16_r );
	READ16_HANDLER ( toaplan2_1_videoram16_r );
	WRITE16_HANDLER( toaplan2_0_videoram16_w );
	WRITE16_HANDLER( toaplan2_1_videoram16_w );
	
	READ16_HANDLER ( toaplan2_txvideoram16_r );
	WRITE16_HANDLER( toaplan2_txvideoram16_w );
	READ16_HANDLER ( toaplan2_txvideoram16_offs_r );
	WRITE16_HANDLER( toaplan2_txvideoram16_offs_w );
	READ16_HANDLER ( toaplan2_txscrollram16_r );
	WRITE16_HANDLER( toaplan2_txscrollram16_w );
	READ16_HANDLER ( toaplan2_tx_gfxram16_r );
	WRITE16_HANDLER( toaplan2_tx_gfxram16_w );
	READ16_HANDLER ( raizing_tx_gfxram16_r );
	WRITE16_HANDLER( raizing_tx_gfxram16_w );
	
	WRITE16_HANDLER( toaplan2_0_scroll_reg_select_w );
	WRITE16_HANDLER( toaplan2_1_scroll_reg_select_w );
	WRITE16_HANDLER( toaplan2_0_scroll_reg_data_w );
	WRITE16_HANDLER( toaplan2_1_scroll_reg_data_w );
	
	WRITE16_HANDLER( batrider_objectbank_w );
	WRITE16_HANDLER( batrider_textdata_decode );
	
	
	
	/********* Video wrappers for PIPIBIBI *********/
	READ16_HANDLER ( pipibibi_videoram16_r );
	WRITE16_HANDLER( pipibibi_videoram16_w );
	READ16_HANDLER ( pipibibi_spriteram16_r );
	WRITE16_HANDLER( pipibibi_spriteram16_w );
	WRITE16_HANDLER( pipibibi_scroll_w );
	
	
	
	/***************************************************************************
	  Initialisation handlers
	***************************************************************************/
	
	
	public static MachineInitHandlerPtr machine_init_toaplan2  = new MachineInitHandlerPtr() { public void handler()/* machine_init_toaplan2(); */
	{
		mcu_data = 0;
	} };
	
	public static MachineInitHandlerPtr machine_init_ghox  = new MachineInitHandlerPtr() { public void handler(){
		old_p1_paddle_h = 0;
		old_p1_paddle_v = 0;
		old_p2_paddle_h = 0;
		old_p2_paddle_v = 0;
	
		machine_init_toaplan2();
	} };
	
	public static MachineInitHandlerPtr machine_init_batrider  = new MachineInitHandlerPtr() { public void handler(){
		current_bank = 2;
	
		machine_init_toaplan2();
	} };
	
	public static DriverInitHandlerPtr init_T2_Z80  = new DriverInitHandlerPtr() { public void handler()/* init_t2_Z80(); */
	{
		toaplan2_sub_cpu = CPU_2_Z80;
	} };
	
	public static DriverInitHandlerPtr init_T2_Z180  = new DriverInitHandlerPtr() { public void handler(){
		toaplan2_sub_cpu = CPU_2_HD647180;
	} };
	
	public static DriverInitHandlerPtr init_T2_Zx80  = new DriverInitHandlerPtr() { public void handler(){
		toaplan2_sub_cpu = CPU_2_Zx80;
	} };
	
	public static DriverInitHandlerPtr init_T2_noZ80  = new DriverInitHandlerPtr() { public void handler(){
		toaplan2_sub_cpu = CPU_2_NONE;
	} };
	
	public static DriverInitHandlerPtr init_fixeight  = new DriverInitHandlerPtr() { public void handler(){
		install_mem_read16_handler(0, 0x28f002, 0x28fbff, MRA16_RAM );
		install_mem_write16_handler(0, 0x28f002, 0x28fbff, MWA16_RAM );
	
		toaplan2_sub_cpu = CPU_2_Zx80;
	} };
	
	public static DriverInitHandlerPtr init_pipibibi  = new DriverInitHandlerPtr() { public void handler(){
		int A;
		int oldword, newword;
	
		data16_t *pipibibi_68k_rom = (data16_t *)(memory_region(REGION_CPU1));
	
		/* unscramble the 68K ROM data. */
	
		for (A = 0; A < (0x040000/2); A+=4)
		{
			newword = 0;
			oldword = pipibibi_68k_rom[A];
			newword |= ((oldword & 0x0001) << 9);
			newword |= ((oldword & 0x0002) << 14);
			newword |= ((oldword & 0x0004) << 8);
			newword |= ((oldword & 0x0018) << 1);
			newword |= ((oldword & 0x0020) << 9);
			newword |= ((oldword & 0x0040) << 7);
			newword |= ((oldword & 0x0080) << 5);
			newword |= ((oldword & 0x0100) << 3);
			newword |= ((oldword & 0x0200) >> 1);
			newword |= ((oldword & 0x0400) >> 8);
			newword |= ((oldword & 0x0800) >> 10);
			newword |= ((oldword & 0x1000) >> 12);
			newword |= ((oldword & 0x6000) >> 7);
			newword |= ((oldword & 0x8000) >> 12);
			pipibibi_68k_rom[A] = newword;
	
			newword = 0;
			oldword = pipibibi_68k_rom[A+1];
			newword |= ((oldword & 0x0001) << 8);
			newword |= ((oldword & 0x0002) << 12);
			newword |= ((oldword & 0x0004) << 5);
			newword |= ((oldword & 0x0008) << 11);
			newword |= ((oldword & 0x0010) << 2);
			newword |= ((oldword & 0x0020) << 10);
			newword |= ((oldword & 0x0040) >> 1);
			newword |= ((oldword & 0x0080) >> 7);
			newword |= ((oldword & 0x0100) >> 4);
			newword |= ((oldword & 0x0200) << 0);
			newword |= ((oldword & 0x0400) >> 7);
			newword |= ((oldword & 0x0800) >> 1);
			newword |= ((oldword & 0x1000) >> 10);
			newword |= ((oldword & 0x2000) >> 2);
			newword |= ((oldword & 0x4000) >> 13);
			newword |= ((oldword & 0x8000) >> 3);
			pipibibi_68k_rom[A+1] = newword;
	
			newword = 0;
			oldword = pipibibi_68k_rom[A+2];
			newword |= ((oldword & 0x000f) << 4);
			newword |= ((oldword & 0x00f0) >> 4);
			newword |= ((oldword & 0x0100) << 3);
			newword |= ((oldword & 0x0200) << 1);
			newword |= ((oldword & 0x0400) >> 1);
			newword |= ((oldword & 0x0800) >> 3);
			newword |= ((oldword & 0x1000) << 3);
			newword |= ((oldword & 0x2000) << 1);
			newword |= ((oldword & 0x4000) >> 1);
			newword |= ((oldword & 0x8000) >> 3);
			pipibibi_68k_rom[A+2] = newword;
	
			newword = 0;
			oldword = pipibibi_68k_rom[A+3];
			newword |= ((oldword & 0x000f) << 4);
			newword |= ((oldword & 0x00f0) >> 4);
			newword |= ((oldword & 0x0100) << 7);
			newword |= ((oldword & 0x0200) << 5);
			newword |= ((oldword & 0x0400) << 3);
			newword |= ((oldword & 0x0800) << 1);
			newword |= ((oldword & 0x1000) >> 1);
			newword |= ((oldword & 0x2000) >> 3);
			newword |= ((oldword & 0x4000) >> 5);
			newword |= ((oldword & 0x8000) >> 7);
			pipibibi_68k_rom[A+3] = newword;
		}
	
		toaplan2_sub_cpu = CPU_2_Z80;
	} };
	
	public static DriverInitHandlerPtr init_battleg  = new DriverInitHandlerPtr() { public void handler(){
		data8_t *Z80 = (data8_t *)memory_region(REGION_CPU2);
	
		/* Set Z80 bank switch */
		cpu_setbank(1, &Z80[0x10000]);		/* Default bank is 2 */
	
		toaplan2_sub_cpu = CPU_2_Z80;
	} };
	
	public static DriverInitHandlerPtr init_bbakraid  = new DriverInitHandlerPtr() { public void handler(){
		bbakraid_unlimited_ver = 1;
		toaplan2_sub_cpu = CPU_2_Z80;
	} };
	
	public static DriverInitHandlerPtr init_bbakrada  = new DriverInitHandlerPtr() { public void handler(){
		bbakraid_unlimited_ver = 0;
		toaplan2_sub_cpu = CPU_2_Z80;
	} };
	
	
	/***************************************************************************
	  Toaplan games
	***************************************************************************/
	
	READ16_HANDLER( toaplan2_inputport_0_word_r )
	{
	//	int retval = (current_scanline>255) ? 1 : 0;
		int retval = vblank_irq;
		return retval;
	}
	
	static void toaplan2_irq(int irq_line)
	{
		if (cpu_getiloops() == 0) current_scanline = 255;
	
		if(current_scanline == 245)
		{
			cpu_set_irq_line(0, irq_line, HOLD_LINE);
			vblank_irq = 1;
		}
	
		current_scanline++;
		if(current_scanline > 261)
		{
			current_scanline = 0;
			vblank_irq = 0;
		}
	}
	
	public static InterruptHandlerPtr toaplan2_vblank_irq2 = new InterruptHandlerPtr() {public void handler()toaplan2_irq(2);}
	public static InterruptHandlerPtr toaplan2_vblank_irq3 = new InterruptHandlerPtr() {public void handler()toaplan2_irq(3);}
	public static InterruptHandlerPtr toaplan2_vblank_irq4 = new InterruptHandlerPtr() {public void handler()toaplan2_irq(4);}
	
	static READ16_HANDLER( video_count_r )
	{
		/* +---------+---------+--------+---------------------------+ */
		/* | /H-Sync | /V-Sync | /Blank |       Scanline Count      | */
		/* | Bit 15  | Bit 14  | Bit 8  |  Bit 7-0 (count from #EF) | */
		/* +---------+---------+--------+---------------------------+ */
		/*************** Control Signals are active low ***************/
	
	//	static int current_beampos = 0;
	
	//	logerror("Was VC=%04x  Vbl=%02x  VS=%04x  HS=%04x - ",video_status,vblank_irq,prev_scanline,prev_beampos );
	
		video_status = 0xff00;						/* Set signals inactive */
		video_status |= (current_scanline & 0xff);	/* Scanline */
	
		if (vblank_irq) {
			video_status &= ~0x0100;
		} };
		if (prev_scanline != current_scanline) {
			video_status &= ~0x8000;				/* Activate V-Sync Clk */
		}
	//	if (current_beampos) {
	//		video_status &= ~0x4000;
	//	}
	//	current_beampos = ~current_beampos;
		prev_scanline = current_scanline;
	
	//	logerror("Now VC=%04x  Vbl=%02x  VS=%04x  HS=%04x\n",video_status,vblank_irq,cpu_getscanline(),cpu_gethorzbeampos() );
	
		return video_status;
	}
	
	public static WriteHandlerPtr toaplan2_coin_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		/* +----------------+------ Bits 7-5 not used ------+--------------+ */
		/* | Coin Lockout 2 | Coin Lockout 1 | Coin Count 2 | Coin Count 1 | */
		/* |     Bit 3      |     Bit 2      |     Bit 1    |     Bit 0    | */
	
		if (data & 0x0f)
		{
			coin_lockout_w( 0, ((data & 4) ? 0 : 1) );
			coin_lockout_w( 1, ((data & 8) ? 0 : 1) );
			coin_counter_w( 0, (data & 1) ); coin_counter_w( 0, 0 );
			coin_counter_w( 1, (data & 2) ); coin_counter_w( 1, 0 );
		}
		else
		{
			coin_lockout_global_w(1); /* Lock all coin slots */
		}
		if (data & 0xe0)
		{
			logerror("Writing unknown upper bits (%02x) to coin control\n",data);
		}
	} };
	static WRITE16_HANDLER( toaplan2_coin_word_w )
	{
		if (ACCESSING_LSB)
		{
			toaplan2_coin_w(offset, data & 0xff);
			if (toaplan2_sub_cpu == CPU_2_Z80)
			{
				if (Machine->drv->sound[1].sound_type == SOUND_OKIM6295)
				{
					OKIM6295_set_bank_base(0, (((data & 0x10) >> 4) * 0x40000));
				}
			}
		}
		if (ACCESSING_MSB && (data & 0xff00) )
		{
			logerror("Writing unknown upper MSB command (%04x) to coin control\n",data & 0xff00);
		}
	}
	
	static READ16_HANDLER( toaplan2_shared_r )
	{
		return toaplan2_shared_ram[offset] & 0xff;
	}
	
	static WRITE16_HANDLER( toaplan2_shared_w )
	{
		if (ACCESSING_LSB)
		{
			toaplan2_shared_ram[offset] = data & 0xff;
		}
	}
	
	static WRITE16_HANDLER( toaplan2_hd647180_cpu_w )
	{
		/* Command sent to secondary CPU. Support for HD647180 will be
		   required when a ROM dump becomes available for this hardware */
	
		if (ACCESSING_LSB)
		{
			if (toaplan2_sub_cpu == CPU_2_Z80)			/* Whoopee */
			{
				toaplan2_shared_ram[0] = data & 0xff;
			}
			else										/* Teki Paki */
			{
				mcu_data = data & 0xff;
				logerror("PC:%08x Writing command (%04x) to secondary CPU shared port\n",activecpu_get_previouspc(),mcu_data);
			}
		}
	}
	
	static READ16_HANDLER( c2map_port_6_r )
	{
		/* For Teki Paki hardware */
		/* bit 4 high signifies secondary CPU is ready */
		/* bit 5 is tested low before V-Blank bit ??? */
		switch (toaplan2_sub_cpu)
		{
			case CPU_2_Z80:			mcu_data = toaplan2_shared_ram[0]; break; /* Whoopee */
			case CPU_2_HD647180:	mcu_data = 0xff; break;					  /* Teki Paki */
			default:				mcu_data = 0x00; break;
		}
		if (mcu_data == 0xff) mcu_data = 0x10;
		else mcu_data = 0x00;
		return ( mcu_data | input_port_6_r(0) );
	}
	
	static READ16_HANDLER( pipibibi_z80_status_r )
	{
		return toaplan2_shared_ram[0] & 0xff;
	}
	
	static WRITE16_HANDLER( pipibibi_z80_task_w )
	{
		if (ACCESSING_LSB)
		{
			toaplan2_shared_ram[0] = data & 0xff;
		}
	}
	
	static READ16_HANDLER( ghox_p1_h_analog_r )
	{
		INT8 value, new_value;
	
		new_value = input_port_7_r(0);
		if (new_value == old_p1_paddle_h) return 0;
		value = new_value - old_p1_paddle_h;
		old_p1_paddle_h = new_value;
		return value;
	}
	
	static READ16_HANDLER( ghox_p1_v_analog_r )
	{
		INT8 new_value;
	
		new_value = input_port_9_r(0);		/* fake vertical movement */
		if (new_value == old_p1_paddle_v) return input_port_1_r(0);
		if (new_value >  old_p1_paddle_v)
		{
			old_p1_paddle_v = new_value;
			return (input_port_1_r(0) | 2);
		}
		old_p1_paddle_v = new_value;
		return (input_port_1_r(0) | 1);
	}
	
	static READ16_HANDLER( ghox_p2_h_analog_r )
	{
		INT8 value, new_value;
	
		new_value = input_port_8_r(0);
		if (new_value == old_p2_paddle_h) return 0;
		value = new_value - old_p2_paddle_h;
		old_p2_paddle_h = new_value;
		return value;
	}
	
	static READ16_HANDLER( ghox_p2_v_analog_r )
	{
		INT8 new_value;
	
		new_value = input_port_10_r(0);		/* fake vertical movement */
		if (new_value == old_p2_paddle_v) return input_port_2_r(0);
		if (new_value >  old_p2_paddle_v)
		{
			old_p2_paddle_v = new_value;
			return (input_port_2_r(0) | 2);
		}
		old_p2_paddle_v = new_value;
		return (input_port_2_r(0) | 1);
	}
	
	static READ16_HANDLER( ghox_mcu_r )
	{
		return 0xff;
	}
	
	static WRITE16_HANDLER( ghox_mcu_w )
	{
		if (ACCESSING_LSB)
		{
			mcu_data = data;
			if ((data >= 0xd0) && (data < 0xe0))
			{
				offset = ((data & 0x0f) * 2) + (0x38 / 2);
				toaplan2_shared_ram16[offset  ] = 0x0005;	/* Return address for */
				toaplan2_shared_ram16[offset-1] = 0x0056;	/*   RTS instruction */
			}
			else
			{
				logerror("PC:%08x Writing %08x to HD647180 cpu shared ram status port\n",activecpu_get_previouspc(),mcu_data);
			}
			toaplan2_shared_ram16[0x56 / 2] = 0x004e;	/* Return a RTS instruction */
			toaplan2_shared_ram16[0x58 / 2] = 0x0075;
	
			if (data == 0xd3)
			{
			toaplan2_shared_ram16[0x56 / 2] = 0x003a;	//	move.w  d1,d5
			toaplan2_shared_ram16[0x58 / 2] = 0x0001;
			toaplan2_shared_ram16[0x5a / 2] = 0x0008;	//	bclr.b  #0,d5
			toaplan2_shared_ram16[0x5c / 2] = 0x0085;
			toaplan2_shared_ram16[0x5e / 2] = 0x0000;
			toaplan2_shared_ram16[0x60 / 2] = 0x0000;
			toaplan2_shared_ram16[0x62 / 2] = 0x00cb;	//	muls.w  #3,d5
			toaplan2_shared_ram16[0x64 / 2] = 0x00fc;
			toaplan2_shared_ram16[0x66 / 2] = 0x0000;
			toaplan2_shared_ram16[0x68 / 2] = 0x0003;
			toaplan2_shared_ram16[0x6a / 2] = 0x0090;	//	sub.w   d5,d0
			toaplan2_shared_ram16[0x6c / 2] = 0x0045;
			toaplan2_shared_ram16[0x6e / 2] = 0x00e5;	//	lsl.b   #2,d1
			toaplan2_shared_ram16[0x70 / 2] = 0x0009;
			toaplan2_shared_ram16[0x72 / 2] = 0x004e;	//	rts
			toaplan2_shared_ram16[0x74 / 2] = 0x0075;
			}
		}
	}
	
	static READ16_HANDLER( ghox_shared_ram_r )
	{
		/* Ghox 68K reads data from MCU shared RAM and writes it to main RAM.
		   It then subroutine jumps to main RAM and executes this code.
		   Here, we're just returning a RTS instruction for now.
		   See above ghox_mcu_w routine.
	
		   Offset $56 and $58 are accessed from around PC:0F814
	
		   Offset $38 and $36 are accessed from around PC:0DA7C
		   Offset $3c and $3a are accessed from around PC:02E3C
		   Offset $40 and $3E are accessed from around PC:103EE
		   Offset $44 and $42 are accessed from around PC:0FB52
		   Offset $48 and $46 are accessed from around PC:06776
		*/
	
		return toaplan2_shared_ram16[offset] & 0xff;
	}
	static WRITE16_HANDLER( ghox_shared_ram_w )
	{
		if (ACCESSING_LSB)
		{
			toaplan2_shared_ram16[offset] = data & 0xff;
		}
	}
	static READ16_HANDLER( kbash_sub_cpu_r )
	{
	/*	Knuckle Bash's  68000 reads secondary CPU status via an I/O port.
		If a value of 2 is read, then secondary CPU is busy.
		Secondary CPU must report 0xff when no longer busy, to signify that it
		has passed POST.
	*/
		return 0xff;
	}
	
	static WRITE16_HANDLER( kbash_sub_cpu_w )
	{
		logerror("PC:%08x writing %04x to Zx80 secondary CPU status port %02x\n",activecpu_get_previouspc(),mcu_data,offset/2);
	}
	
	static READ16_HANDLER( shared_ram_r )
	{
	/*	Other games using a Zx80 based secondary CPU, have shared memory between
		the 68000 and the Zx80 CPU. The 68000 reads the status of the Zx80
		via a location of the shared memory.
	*/
		return toaplan2_shared_ram16[offset] & 0xff;
	}
	
	static WRITE16_HANDLER( shared_ram_w )
	{
		if (ACCESSING_LSB)
		{
			data &= 0xff;
			switch (offset * 2)
			{
				case 0x6e8:
				case 0x9e8:
				case 0x9f0:
				case 0xcf0:
				case 0xcf8:
				case 0xff8: toaplan2_shared_ram16[offset + 1] = data; /* Dogyuun */
							toaplan2_shared_ram16[offset + 2] = data; /* FixEight */
							logerror("PC:%08x Writing  (%04x) to secondary CPU\n",activecpu_get_previouspc(),data);
							if (data == 0x81) data = 0x0001;
							break;
				default:	break;
			}
			toaplan2_shared_ram16[offset] = data;
		}
	}
	
	static READ16_HANDLER( Zx80_status_port_r )
	{
	/*** Status port includes Zx80 CPU POST codes. ************
	 *** This is actually a part of the 68000/Zx80 Shared RAM */
	
		/*** Dogyuun mcu post data ***/
		if (mcu_data == 0x800000aa) mcu_data = 0xff;
		if (mcu_data == 0x00) mcu_data = 0x800000aa;
	
		/*** FixEight mcu post data ***/
		if (mcu_data == 0x8000ffaa)
		{
	#if 0 	/* check the 37B6 code */
			/* copy nvram data to shared ram after post is complete */
			fixeight_sharedram[0] = fixeight_nvram[0];	/* Dip Switch A */
			fixeight_sharedram[1] = fixeight_nvram[1];	/* Dip Switch B */
			fixeight_sharedram[2] = fixeight_nvram[2];	/* Territory */
	#endif
			/* Hack Alert ! Fixeight does not have any DSW. The main CPU has a */
			/* game keeping service mode. It writes/reads the settings to/from */
			/* these shared RAM locations. The secondary CPU reads/writes them */
			/* from/to nvram to store the settings (a 93C45 EEPROM) */
			install_mem_read16_handler (0, 0x28f002, 0x28f003, MRA16_RAM);
			install_mem_read16_handler (0, 0x28f004, 0x28f005, input_port_5_word_r);	/* Dip Switch A - Wrong !!! */
			install_mem_read16_handler (0, 0x28f006, 0x28f007, input_port_6_word_r);	/* Dip Switch B - Wrong !!! */
			install_mem_read16_handler (0, 0x28f008, 0x28f009, input_port_7_word_r);	/* Territory Jumper block - Wrong !!! */
			install_mem_read16_handler (0, 0x28f00a, 0x28fbff, MRA16_RAM);
			install_mem_write16_handler (0, 0x28f002, 0x28f003, MWA16_RAM);
			install_mem_write16_handler (0, 0x28f004, 0x28f009, MWA16_NOP);
			install_mem_write16_handler (0, 0x28f00a, 0x28fbff, MWA16_RAM);
	
			mcu_data = 0xffff;
		}
		if (mcu_data == 0xffaa) mcu_data = 0x8000ffaa;
		if (mcu_data == 0xff00) mcu_data = 0xffaa;
	
		logerror("PC:%08x reading %08x from Zx80 secondary CPU command/status port\n",activecpu_get_previouspc(),mcu_data);
		return mcu_data & 0xff;
	}
	
	static WRITE16_HANDLER( Zx80_command_port_w )
	{
		if (ACCESSING_LSB)
		{
			mcu_data = data;
		logerror("PC:%08x Writing command (%04x) to Zx80 secondary CPU command/status port\n",activecpu_get_previouspc(),mcu_data);
	}
	}
	
	static READ16_HANDLER( Zx80_sharedram_r )
	{
		return Zx80_shared_ram[offset] & 0xff;
	}
	
	static WRITE16_HANDLER( Zx80_sharedram_w )
	{
		if (ACCESSING_LSB)
		{
			Zx80_shared_ram[offset] = data & 0xff;
		}
	}
	
	static WRITE16_HANDLER( oki_bankswitch_w )
	{
		if (ACCESSING_LSB)
		{
			OKIM6295_set_bank_base(0, (data & 1) * 0x40000);
		}
	}
	
	
	
	/***************************************************************************
	  Raizing games
	***************************************************************************/
	
	static READ16_HANDLER( raizing_shared_ram_r )
	{
		return raizing_shared_ram[offset] & 0xff;
	}
	
	static WRITE16_HANDLER( raizing_shared_ram_w )
	{
		if (ACCESSING_LSB)
		{
			raizing_shared_ram[offset] = data & 0xff;
		}
	}
	
	static READ16_HANDLER( battleg_commram_r )
	{
		return battleg_commram16[offset];
	}
	
	static WRITE16_HANDLER( battleg_commram_w )
	{
		COMBINE_DATA(&battleg_commram16[offset]);
		cpu_set_irq_line(1, 0, HOLD_LINE);
		if (offset == 0) cpu_yield();	/* Command issued so switch control */
	}
	
	public static ReadHandlerPtr battleg_commram_check_r0  = new ReadHandlerPtr() { public int handler(int offset){
		data8_t *battleg_common_RAM = (data8_t *)battleg_commram16;
	
		return battleg_common_RAM[BYTE_XOR_BE(offset * 2 + 1)];
	} };
	
	public static WriteHandlerPtr battleg_commram_check_w0 = new WriteHandlerPtr() {public void handler(int offset, int data){
		data8_t *battleg_common_RAM = (data8_t *)battleg_commram16;
	
		battleg_common_RAM[BYTE_XOR_BE(0)] = data;
		cpu_yield();					/* Command issued so switch control */
	} };
	
	static READ16_HANDLER( battleg_z80check_r )
	{
		return raizing_shared_ram[offset + 0x10] & 0xff;
	}
	
	public static WriteHandlerPtr battleg_bankswitch_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		data8_t *RAM = (data8_t *)memory_region(REGION_CPU2);
		int bankaddress;
		int bank;
	
		bank = (data & 0x0f) - 10;
	
		if (bank != current_bank)
		{
			current_bank = bank;
			bankaddress = 0x10000 + 0x4000 * current_bank;
			cpu_setbank(1, &RAM[bankaddress]);
		}
	} };
	
	static void raizing_oki6295_set_bankbase( int chip, int channel, int base )
	{
		/* The OKI6295 ROM space is divided in four banks, each one independantly */
		/* controlled. The sample table at the beginning of the addressing space  */
		/* is divided in four pages as well, banked together with the sample data */
	
		data8_t *rom = (data8_t *)memory_region(REGION_SOUND1 + chip);
	
		/* copy the samples */
		memcpy(rom + channel * 0x10000, rom + 0x40000 + base, 0x10000);
	
		/* and also copy the samples address table */
		rom += channel * 0x100;
		memcpy(rom, rom + 0x40000 + base, 0x100);
	}
	
	
	public static WriteHandlerPtr raizing_okim6295_bankselect_0 = new WriteHandlerPtr() {public void handler(int offset, int data){
		raizing_oki6295_set_bankbase( 0, 0,  (data       & 0x0f) * 0x10000);
		raizing_oki6295_set_bankbase( 0, 1, ((data >> 4) & 0x0f) * 0x10000);
	} };
	
	public static WriteHandlerPtr raizing_okim6295_bankselect_1 = new WriteHandlerPtr() {public void handler(int offset, int data){
		raizing_oki6295_set_bankbase( 0, 2,  (data       & 0x0f) * 0x10000);
		raizing_oki6295_set_bankbase( 0, 3, ((data >> 4) & 0x0f) * 0x10000);
	} };
	
	public static WriteHandlerPtr raizing_okim6295_bankselect_2 = new WriteHandlerPtr() {public void handler(int offset, int data){
		raizing_oki6295_set_bankbase( 1, 0,  (data       & 0x0f) * 0x10000);
		raizing_oki6295_set_bankbase( 1, 1, ((data >> 4) & 0x0f) * 0x10000);
	} };
	
	public static WriteHandlerPtr raizing_okim6295_bankselect_3 = new WriteHandlerPtr() {public void handler(int offset, int data){
		raizing_oki6295_set_bankbase( 1, 2,  (data       & 0x0f) * 0x10000);
		raizing_oki6295_set_bankbase( 1, 3, ((data >> 4) & 0x0f) * 0x10000);
	} };
	
	public static WriteHandlerPtr batrider_bankswitch_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		data8_t *RAM = (data8_t *)memory_region(REGION_CPU2);
		int bankaddress;
		int bank;
	
		bank = data & 0x0f;
	
		if (bank != current_bank)
		{
			current_bank = bank;
			logerror("Z80 cpu set bank #%d\n", bank);
			if (bank > 1)
				bankaddress = 0x10000 + 0x4000 * (current_bank - 2);
			else
				bankaddress = 0x4000 * current_bank;
			cpu_setbank(1, &RAM[bankaddress]);
		}
	} };
	
	static READ16_HANDLER( batrider_z80_busack_r )
	{
		/* Bit 1 returns the status of BUSAK from the Z80.
		   BUSRQ is activated via bit 0x10 on the NVRAM write port.
		   These accesses are made when the 68K wants to read the Z80
		   ROM code. Failure to return the correct status incurrs a Sound Error.
		*/
	
		return raizing_Z80_busreq;			/* Loop BUSRQ to BUSAK */
	}
	static WRITE16_HANDLER( batrider_z80_busreq_w )
	{
		if (ACCESSING_LSB)
		{
			raizing_Z80_busreq = (data & 0xff);
		}
	}
	
	static READ16_HANDLER( raizing_z80rom_r )
	{
		data8_t *Z80_ROM_test = (data8_t *)memory_region(REGION_CPU2);
	
		if (offset < 0x8000)
			return Z80_ROM_test[offset] & 0xff;
	
		return Z80_ROM_test[offset + 0x8000] & 0xff;
	}
	
	
	
	/*###################### Battle Bakraid ##############################*/
	
	/* EEPROM contents with battle Bakraid Unlimited version features unlocked */
	static data8_t bbakraid_unlimited_nvram[512] = {
		0xc2,0x49,0x00,0x07,0xa1,0x20,0x2a,0x2a,0x2a,0x90,0x90,0x90,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02,0x00,0x06,0x1a,0x80,0x2a,0x2a,0x2a,0x94,
		0x94,0x94,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02,0x00,0x04,
		0x93,0xe0,0x2a,0x2a,0x2a,0x98,0x98,0x98,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x02,0x00,0x03,0x0d,0x40,0x2a,0x2a,0x2a,0x9c,0x9c,0x9c,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02,0x00,0x01,0x86,0xa0,0x2a,0x2a,
		0x2a,0xa0,0xa0,0xa0,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02,
		0x00,0x07,0xa1,0x20,0x2b,0x2b,0x2b,0x90,0x90,0x90,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x02,0x00,0x06,0x1a,0x80,0x2b,0x2b,0x2b,0x94,0x94,0x94,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02,0x00,0x04,0x93,0xe0,
		0x2b,0x2b,0x2b,0x98,0x98,0x98,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x02,0x00,0x03,0x0d,0x40,0x2b,0x2b,0x2b,0x9c,0x9c,0x9c,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02,0x00,0x01,0x86,0xa0,0x2b,0x2b,0x2b,0xa0,
		0xa0,0xa0,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02,0x00,0x07,
		0xa1,0x20,0x23,0x23,0x23,0x90,0x90,0x90,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x02,0x00,0x06,0x1a,0x80,0x23,0x23,0x23,0x94,0x94,0x94,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02,0x00,0x04,0x93,0xe0,0x23,0x23,
		0x23,0x98,0x98,0x98,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02,
		0x00,0x03,0x0d,0x40,0x23,0x23,0x23,0x9c,0x9c,0x9c,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x02,0x00,0x01,0x86,0xa0,0x23,0x23,0x23,0xa0,0xa0,0xa0,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02,0x00,0x07,0xa1,0x20,
		0x3f,0x3f,0x3f,0x90,0x90,0x90,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x02,0x00,0x06,0x1a,0x80,0x3f,0x3f,0x3f,0x94,0x94,0x94,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02,0x00,0x04,0x93,0xe0,0x3f,0x3f,0x3f,0x98,
		0x98,0x98,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02,0x00,0x03,
		0x0d,0x40,0x3f,0x3f,0x3f,0x9c,0x9c,0x9c,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x02,0x00,0x01,0x86,0xa0,0x3f,0x3f,0x3f,0xa0,0xa0,0xa0,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02,0x00,0x04,0xc2,0x49,0xc2,0x49,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x33,0xcf,0x00,0x04,0x00,0x00,0x21,0xd5,
		0x00,0x05,0x06,0xff,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0xff,0xc2,0x49
	};
	
	
	
	struct EEPROM_interface eeprom_interface_93C66 =
	{
		/* Pin 6 of the 93C66 is connected to Gnd!
		   So it's configured for 512 bytes */
	
		9,			// address bits
		8,			// data bits
		"*110",		// read			110 aaaaaaaaa
		"*101",		// write		101 aaaaaaaaa dddddddd
		"*111",		// erase		111 aaaaaaaaa
		"*10000xxxxxxx",// lock			100x 00xxxx
		"*10011xxxxxxx",// unlock		100x 11xxxx
	//	"*10001xxxx",	// write all	1 00 01xxxx dddddddd
	//	"*10010xxxx"	// erase all	1 00 10xxxx
	};
	
	
	public static NVRAMHandlerPtr nvram_handler_bbakraid  = new NVRAMHandlerPtr() { public void handler(mame_file file, int read_or_write){
		/* Pin 6 of 93C66 is connected to Gnd! */
	
		if (read_or_write)
			EEPROM_save(file);
		else
		{
			EEPROM_init(&eeprom_interface_93C66);
	
			if (file) EEPROM_load(file);
			else
			{
				if (bbakraid_unlimited_ver == 1)
					EEPROM_set_data(bbakraid_unlimited_nvram, sizeof(bbakraid_unlimited_nvram));
			}
		}
	} };
	
	static READ16_HANDLER( bbakraid_nvram_r )
	{
		/* Bit 1 returns the status of BUSAK from the Z80.
		   BUSRQ is activated via bit 0x10 on the NVRAM write port.
		   These accesses are made when the 68K wants to read the Z80
		   ROM code. Failure to return the correct status incurrs a Sound Error.
		*/
	
		int data;
		data  = ((EEPROM_read_bit() & 0x01) << 4);
		data |= ((raizing_Z80_busreq >> 4) & 0x01);	/* Loop BUSRQ to BUSAK */
	
		return data;
	}
	
	static WRITE16_HANDLER( bbakraid_nvram_w )
	{
		if (data & ~0x001f)
			logerror("CPU #0 PC:%06X - Unknown EEPROM data being written %04X\n",activecpu_get_pc(),data);
	
		if ( ACCESSING_LSB )
		{
			// chip select
			EEPROM_set_cs_line((data & 0x01) ? CLEAR_LINE : ASSERT_LINE );
	
			// latch the bit
			EEPROM_write_bit( (data & 0x04) >> 2 );
	
			// clock line asserted: write latch or select next bit to read
			EEPROM_set_clock_line((data & 0x08) ? ASSERT_LINE : CLEAR_LINE );
		}
		raizing_Z80_busreq = data & 0x10;	/* see bbakraid_nvram_r above */
	}
	
	
	/****** Battle Bakraid 68K handlers ******/
	static READ16_HANDLER ( raizing_sndcomms_r )
	{
	//	logerror("68K (PC:%06x) reading %04x from $50001%01x\n",activecpu_get_pc(),(raizing_cpu_reply[offset] & 0xff),(offset*2));
		return (raizing_cpu_reply[offset] & 0xff);
	}
	static WRITE16_HANDLER ( raizing_sndcomms_w )
	{
	//	logerror("68K (PC:%06x) writing %04x to $50001%01x\n",activecpu_get_pc(),data,((offset*2)+4));
		COMBINE_DATA(&raizing_cpu_comm16[offset]);
	
		cpu_set_nmi_line(1, ASSERT_LINE);
		cpu_yield();
	}
	
	/****** Battle Bakraid Z80 handlers ******/
	public static ReadHandlerPtr raizing_command_r  = new ReadHandlerPtr() { public int handler(int offset){
		data8_t *raizing_cpu_comm = (data8_t *)raizing_cpu_comm16;
	
		logerror("Z80 (PC:%04x) reading %02x from $48\n",activecpu_get_pc(),raizing_cpu_comm[BYTE_XOR_BE(1)]);
		return raizing_cpu_comm[BYTE_XOR_BE(1)];
	} };
	public static ReadHandlerPtr raizing_request_r  = new ReadHandlerPtr() { public int handler(int offset){
		data8_t *raizing_cpu_comm = (data8_t *)raizing_cpu_comm16;
	
		logerror("Z80 (PC:%04x) reading %02x from $4A\n",activecpu_get_pc(),raizing_cpu_comm[BYTE_XOR_BE(3)]);
		return raizing_cpu_comm[BYTE_XOR_BE(3)];
	} };
	public static WriteHandlerPtr raizing_command_ack_w = new WriteHandlerPtr() {public void handler(int offset, int data){
	//	logerror("Z80 (PC:%04x) writing %02x to $40\n",activecpu_get_pc(),data);
		raizing_cpu_reply[0] = data;
	} };
	public static WriteHandlerPtr raizing_request_ack_w = new WriteHandlerPtr() {public void handler(int offset, int data){
	//	logerror("Z80 (PC:%04x) writing %02x to $42\n",activecpu_get_pc(),data);
		raizing_cpu_reply[1] = data;
	} };
	
	
	public static WriteHandlerPtr raizing_clear_nmi_w = new WriteHandlerPtr() {public void handler(int offset, int data){
	//	logerror("Clear NMI on the Z80 (Z80 PC:%06x writing %04x)\n",activecpu_get_pc(),data);
		cpu_set_nmi_line(1, CLEAR_LINE);
		cpu_yield();
	} };
	
	static WRITE16_HANDLER ( bbakraid_trigger_z80_irq )
	{
	//	logerror("Triggering IRQ on the Z80 (PC:%06x)\n",activecpu_get_pc());
		cpu_set_irq_line(1, 0, HOLD_LINE);
		cpu_yield();
	}
	
	static void bbakraid_irqhandler (int state)
	{
		/* Not used ???  Connected to a test pin (TP082) */
		logerror("YMZ280 is generating an interrupt. State=%08x\n",state);
	}
	
	public static InterruptHandlerPtr bbakraid_snd_interrupt = new InterruptHandlerPtr() {public void handler(){
		cpu_set_irq_line(1, 0, HOLD_LINE);
	} };
	
	
	static MEMORY_READ16_START( tekipaki_readmem )
		{ 0x000000, 0x01ffff, MRA16_ROM },
		{ 0x020000, 0x03ffff, MRA16_ROM },				/* extra for Whoopee */
		{ 0x080000, 0x082fff, MRA16_RAM },
		{ 0x0c0000, 0x0c0fff, paletteram16_word_r },
		{ 0x140004, 0x140007, toaplan2_0_videoram16_r },
		{ 0x14000c, 0x14000d, toaplan2_inputport_0_word_r },	/* VBlank */
		{ 0x180000, 0x180001, input_port_4_word_r },	/* Dip Switch A */
		{ 0x180010, 0x180011, input_port_5_word_r },	/* Dip Switch B */
		{ 0x180020, 0x180021, input_port_3_word_r },	/* Coin/System inputs */
		{ 0x180030, 0x180031, c2map_port_6_r },			/* CPU 2 busy and Territory Jumper block */
		{ 0x180050, 0x180051, input_port_1_word_r },	/* Player 1 controls */
		{ 0x180060, 0x180061, input_port_2_word_r },	/* Player 2 controls */
	MEMORY_END
	
	static MEMORY_WRITE16_START( tekipaki_writemem )
		{ 0x000000, 0x01ffff, MWA16_ROM },
		{ 0x020000, 0x03ffff, MWA16_ROM },				/* extra for Whoopee */
		{ 0x080000, 0x082fff, MWA16_RAM },
		{ 0x0c0000, 0x0c0fff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
		{ 0x140000, 0x140001, toaplan2_0_voffs_w },
		{ 0x140004, 0x140007, toaplan2_0_videoram16_w },/* Tile/Sprite VideoRAM */
		{ 0x140008, 0x140009, toaplan2_0_scroll_reg_select_w },
		{ 0x14000c, 0x14000d, toaplan2_0_scroll_reg_data_w },
		{ 0x180040, 0x180041, toaplan2_coin_word_w },	/* Coin count/lock */
		{ 0x180070, 0x180071, toaplan2_hd647180_cpu_w },
	MEMORY_END
	
	static MEMORY_READ16_START( ghox_readmem )
		{ 0x000000, 0x03ffff, MRA16_ROM },
		{ 0x040000, 0x040001, ghox_p2_h_analog_r },		/* Paddle 2 */
		{ 0x080000, 0x083fff, MRA16_RAM },
		{ 0x0c0000, 0x0c0fff, paletteram16_word_r },
		{ 0x100000, 0x100001, ghox_p1_h_analog_r },		/* Paddle 1 */
		{ 0x140004, 0x140007, toaplan2_0_videoram16_r },
		{ 0x14000c, 0x14000d, toaplan2_inputport_0_word_r },	/* VBlank */
		{ 0x180000, 0x180001, ghox_mcu_r },				/* really part of shared RAM */
		{ 0x180006, 0x180007, input_port_4_word_r },	/* Dip Switch A */
		{ 0x180008, 0x180009, input_port_5_word_r },	/* Dip Switch B */
		{ 0x180010, 0x180011, input_port_3_word_r },	/* Coin/System inputs */
	//	{ 0x18000c, 0x18000d, input_port_1_word_r },	/* Player 1 controls (real) */
	//	{ 0x18000e, 0x18000f, input_port_2_word_r },	/* Player 2 controls (real) */
		{ 0x18000c, 0x18000d, ghox_p1_v_analog_r },		/* Player 1 controls */
		{ 0x18000e, 0x18000f, ghox_p2_v_analog_r },		/* Player 2 controls */
		{ 0x180500, 0x180fff, ghox_shared_ram_r },
		{ 0x18100c, 0x18100d, input_port_6_word_r },	/* Territory Jumper block */
	MEMORY_END
	
	static MEMORY_WRITE16_START( ghox_writemem )
		{ 0x000000, 0x03ffff, MWA16_ROM },
		{ 0x080000, 0x083fff, MWA16_RAM },
		{ 0x0c0000, 0x0c0fff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
		{ 0x140000, 0x140001, toaplan2_0_voffs_w },
		{ 0x140004, 0x140007, toaplan2_0_videoram16_w },/* Tile/Sprite VideoRAM */
		{ 0x140008, 0x140009, toaplan2_0_scroll_reg_select_w },
		{ 0x14000c, 0x14000d, toaplan2_0_scroll_reg_data_w },
		{ 0x180000, 0x180001, ghox_mcu_w },				/* really part of shared RAM */
		{ 0x180500, 0x180fff, ghox_shared_ram_w, &toaplan2_shared_ram16 },
		{ 0x181000, 0x181001, toaplan2_coin_word_w },
	MEMORY_END
	
	static MEMORY_READ16_START( dogyuun_readmem )
		{ 0x000000, 0x07ffff, MRA16_ROM },
		{ 0x100000, 0x103fff, MRA16_RAM },
		{ 0x200010, 0x200011, input_port_1_word_r },	/* Player 1 controls */
		{ 0x200014, 0x200015, input_port_2_word_r },	/* Player 2 controls */
		{ 0x200018, 0x200019, input_port_3_word_r },	/* Coin/System inputs */
	#if Zx80
		{ 0x21e000, 0x21fbff, shared_ram_r },			/* $21f000 status port */
		{ 0x21fc00, 0x21ffff, Zx80_sharedram_r },		/* 16-bit on 68000 side, 8-bit on Zx80 side */
	#else
		{ 0x21e000, 0x21efff, shared_ram_r },
		{ 0x21f000, 0x21f001, Zx80_status_port_r },		/* Zx80 status port */
		{ 0x21f004, 0x21f005, input_port_4_word_r },	/* Dip Switch A */
		{ 0x21f006, 0x21f007, input_port_5_word_r },	/* Dip Switch B */
		{ 0x21f008, 0x21f009, input_port_6_word_r },	/* Territory Jumper block */
		{ 0x21fc00, 0x21ffff, Zx80_sharedram_r },		/* 16-bit on 68000 side, 8-bit on Zx80 side */
	#endif
		/***** The following in 0x30000x are for video controller 1 ******/
		{ 0x300004, 0x300007, toaplan2_0_videoram16_r },/* tile layers */
		{ 0x30000c, 0x30000d, toaplan2_inputport_0_word_r },	/* VBlank */
		{ 0x400000, 0x400fff, paletteram16_word_r },
		/***** The following in 0x50000x are for video controller 2 ******/
		{ 0x500004, 0x500007, toaplan2_1_videoram16_r },/* tile layers 2 */
		{ 0x700000, 0x700001, video_count_r },			/* test bit 8 */
	MEMORY_END
	
	static MEMORY_WRITE16_START( dogyuun_writemem )
		{ 0x000000, 0x07ffff, MWA16_ROM },
		{ 0x100000, 0x103fff, MWA16_RAM },
		{ 0x200008, 0x200009, OKIM6295_data_0_lsb_w },	/// Really ?
		{ 0x20001c, 0x20001d, toaplan2_coin_word_w },
	#if Zx80
		{ 0x21e000, 0x21fbff, shared_ram_w, &toaplan2_shared_ram16 },	/* $21F000 */
		{ 0x21fc00, 0x21ffff, Zx80_sharedram_w, &Zx80_shared_ram },	/* 16-bit on 68000 side, 8-bit on Zx80 side */
	#else
		{ 0x21e000, 0x21efff, shared_ram_w, &toaplan2_shared_ram16 },
		{ 0x21f000, 0x21f001, Zx80_command_port_w },	/* Zx80 command port */
		{ 0x21fc00, 0x21ffff, Zx80_sharedram_w, &Zx80_shared_ram },	/* 16-bit on 68000 side, 8-bit on Zx80 side */
	#endif
		/***** The following in 0x30000x are for video controller 1 ******/
		{ 0x300000, 0x300001, toaplan2_0_voffs_w },		/* VideoRAM selector/offset */
		{ 0x300004, 0x300007, toaplan2_0_videoram16_w },/* Tile/Sprite VideoRAM */
		{ 0x300008, 0x300009, toaplan2_0_scroll_reg_select_w },
		{ 0x30000c, 0x30000d, toaplan2_0_scroll_reg_data_w },
		{ 0x400000, 0x400fff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
		/***** The following in 0x50000x are for video controller 2 ******/
		{ 0x500000, 0x500001, toaplan2_1_voffs_w },		/* VideoRAM selector/offset */
		{ 0x500004, 0x500007, toaplan2_1_videoram16_w },/* Tile/Sprite VideoRAM */
		{ 0x500008, 0x500009, toaplan2_1_scroll_reg_select_w },
		{ 0x50000c, 0x50000d, toaplan2_1_scroll_reg_data_w },
	MEMORY_END
	
	static MEMORY_READ16_START( kbash_readmem )
		{ 0x000000, 0x07ffff, MRA16_ROM },
		{ 0x100000, 0x103fff, MRA16_RAM },
		{ 0x200000, 0x200001, kbash_sub_cpu_r },
		{ 0x200004, 0x200005, input_port_4_word_r },	/* Dip Switch A */
		{ 0x200006, 0x200007, input_port_5_word_r },	/* Dip Switch B */
		{ 0x200008, 0x200009, input_port_6_word_r },	/* Territory Jumper block */
		{ 0x208010, 0x208011, input_port_1_word_r },	/* Player 1 controls */
		{ 0x208014, 0x208015, input_port_2_word_r },	/* Player 2 controls */
		{ 0x208018, 0x208019, input_port_3_word_r },	/* Coin/System inputs */
		{ 0x300004, 0x300007, toaplan2_0_videoram16_r },/* tile layers */
		{ 0x30000c, 0x30000d, toaplan2_inputport_0_word_r },	/* VBlank */
		{ 0x400000, 0x400fff, paletteram16_word_r },
		{ 0x700000, 0x700001, video_count_r },			/* test bit 8 */
	MEMORY_END
	
	static MEMORY_WRITE16_START( kbash_writemem )
		{ 0x000000, 0x07ffff, MWA16_ROM },
		{ 0x100000, 0x103fff, MWA16_RAM },
		{ 0x200000, 0x200003, kbash_sub_cpu_w },		/* sound number to play */
	//	{ 0x200002, 0x200003, kbash_sub_cpu_w2 },		/* ??? */
		{ 0x20801c, 0x20801d, toaplan2_coin_word_w },
		{ 0x300000, 0x300001, toaplan2_0_voffs_w },
		{ 0x300004, 0x300007, toaplan2_0_videoram16_w },
		{ 0x300008, 0x300009, toaplan2_0_scroll_reg_select_w },
		{ 0x30000c, 0x30000d, toaplan2_0_scroll_reg_data_w },
		{ 0x400000, 0x400fff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
	MEMORY_END
	
	static MEMORY_READ16_START( truxton2_readmem )
		{ 0x000000, 0x07ffff, MRA16_ROM },
		{ 0x100000, 0x10ffff, MRA16_RAM },
		{ 0x200004, 0x200007, toaplan2_0_videoram16_r },
		{ 0x20000c, 0x20000d, toaplan2_inputport_0_word_r },	/* VBlank */
		{ 0x300000, 0x300fff, paletteram16_word_r },
		{ 0x400000, 0x401fff, toaplan2_txvideoram16_r },
		{ 0x402000, 0x4021ff, toaplan2_txvideoram16_offs_r },
		{ 0x402200, 0x402fff, MRA16_RAM },
		{ 0x403000, 0x4031ff, toaplan2_txscrollram16_r },
		{ 0x403200, 0x403fff, MRA16_RAM },
		{ 0x500000, 0x50ffff, toaplan2_tx_gfxram16_r },
		{ 0x600000, 0x600001, video_count_r },
		{ 0x700000, 0x700001, input_port_4_word_r },	/* Dip Switch A */
		{ 0x700002, 0x700003, input_port_5_word_r },	/* Dip Switch B */
		{ 0x700004, 0x700005, input_port_6_word_r },	/* Territory Jumper block */
		{ 0x700006, 0x700007, input_port_1_word_r },	/* Player 1 controls */
		{ 0x700008, 0x700009, input_port_2_word_r },	/* Player 2 controls */
		{ 0x70000a, 0x70000b, input_port_3_word_r },	/* Coin/System inputs */
		{ 0x700010, 0x700011, OKIM6295_status_0_lsb_r },
		{ 0x700014, 0x700015, MRA16_NOP },
		{ 0x700016, 0x700017, YM2151_status_port_0_lsb_r },
	MEMORY_END
	
	static MEMORY_WRITE16_START( truxton2_writemem )
		{ 0x000000, 0x07ffff, MWA16_ROM },
		{ 0x100000, 0x10ffff, MWA16_RAM },
		{ 0x200000, 0x200001, toaplan2_0_voffs_w },		/* VideoRAM selector/offset */
		{ 0x200004, 0x200007, toaplan2_0_videoram16_w },
		{ 0x200008, 0x200009, toaplan2_0_scroll_reg_select_w },
		{ 0x20000c, 0x20000d, toaplan2_0_scroll_reg_data_w },
		{ 0x300000, 0x300fff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
		{ 0x400000, 0x401fff, toaplan2_txvideoram16_w, &toaplan2_txvideoram16, &toaplan2_tx_vram_size },
		{ 0x402000, 0x4021ff, toaplan2_txvideoram16_offs_w, &toaplan2_txvideoram16_offs, &toaplan2_tx_offs_vram_size },
		{ 0x402200, 0x402fff, MWA16_RAM },
		{ 0x403000, 0x4031ff, toaplan2_txscrollram16_w, &toaplan2_txscrollram16, &toaplan2_tx_scroll_vram_size },
		{ 0x403200, 0x403fff, MWA16_RAM },
		{ 0x500000, 0x50ffff, toaplan2_tx_gfxram16_w, &toaplan2_tx_gfxram16 },
		{ 0x700010, 0x700011, OKIM6295_data_0_lsb_w },
		{ 0x700014, 0x700015, YM2151_register_port_0_lsb_w },
		{ 0x700016, 0x700017, YM2151_data_port_0_lsb_w },
		{ 0x70001e, 0x70001f, toaplan2_coin_word_w },	/* Coin count/lock */
	MEMORY_END
	
	static MEMORY_READ16_START( pipibibs_readmem )
		{ 0x000000, 0x03ffff, MRA16_ROM },
		{ 0x080000, 0x082fff, MRA16_RAM },
		{ 0x0c0000, 0x0c0fff, paletteram16_word_r },
		{ 0x140004, 0x140007, toaplan2_0_videoram16_r },
		{ 0x14000c, 0x14000d, toaplan2_inputport_0_word_r },	/* VBlank */
		{ 0x190000, 0x190fff, toaplan2_shared_r },
		{ 0x19c020, 0x19c021, input_port_4_word_r },	/* Dip Switch A */
		{ 0x19c024, 0x19c025, input_port_5_word_r },	/* Dip Switch B */
		{ 0x19c028, 0x19c029, input_port_6_word_r },	/* Territory Jumper block */
		{ 0x19c02c, 0x19c02d, input_port_3_word_r },	/* Coin/System inputs */
		{ 0x19c030, 0x19c031, input_port_1_word_r },	/* Player 1 controls */
		{ 0x19c034, 0x19c035, input_port_2_word_r },	/* Player 2 controls */
	MEMORY_END
	
	static MEMORY_WRITE16_START( pipibibs_writemem )
		{ 0x000000, 0x03ffff, MWA16_ROM },
		{ 0x080000, 0x082fff, MWA16_RAM },
		{ 0x0c0000, 0x0c0fff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
		{ 0x140000, 0x140001, toaplan2_0_voffs_w },
		{ 0x140004, 0x140007, toaplan2_0_videoram16_w },/* Tile/Sprite VideoRAM */
		{ 0x140008, 0x140009, toaplan2_0_scroll_reg_select_w },
		{ 0x14000c, 0x14000d, toaplan2_0_scroll_reg_data_w },
		{ 0x190000, 0x190fff, toaplan2_shared_w },
		{ 0x19c01c, 0x19c01d, toaplan2_coin_word_w },	/* Coin count/lock */
	MEMORY_END
	
	static MEMORY_READ16_START( pipibibi_readmem )
		{ 0x000000, 0x03ffff, MRA16_ROM },
		{ 0x080000, 0x082fff, MRA16_RAM },
		{ 0x083000, 0x0837ff, pipibibi_spriteram16_r },
		{ 0x083800, 0x087fff, MRA16_RAM },
		{ 0x0c0000, 0x0c0fff, paletteram16_word_r },
		{ 0x120000, 0x120fff, MRA16_RAM },
		{ 0x180000, 0x182fff, pipibibi_videoram16_r },
		{ 0x190002, 0x190003, pipibibi_z80_status_r },	/* Z80 ready ? */
		{ 0x19c020, 0x19c021, input_port_4_word_r },	/* Dip Switch A */
		{ 0x19c024, 0x19c025, input_port_5_word_r },	/* Dip Switch B */
		{ 0x19c028, 0x19c029, input_port_6_word_r },	/* Territory Jumper block */
		{ 0x19c02c, 0x19c02d, input_port_3_word_r },	/* Coin/System inputs */
		{ 0x19c030, 0x19c031, input_port_1_word_r },	/* Player 1 controls */
		{ 0x19c034, 0x19c035, input_port_2_word_r },	/* Player 2 controls */
	MEMORY_END
	
	static MEMORY_WRITE16_START( pipibibi_writemem )
		{ 0x000000, 0x03ffff, MWA16_ROM },
		{ 0x080000, 0x082fff, MWA16_RAM },
		{ 0x083000, 0x0837ff, pipibibi_spriteram16_w },	/* SpriteRAM */
		{ 0x083800, 0x087fff, MWA16_RAM },				/* SpriteRAM (unused) */
		{ 0x0c0000, 0x0c0fff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
		{ 0x120000, 0x120fff, MWA16_RAM },				/* Copy of SpriteRAM ? */
	//	{ 0x13f000, 0x13f001, MWA16_NOP },				/* ??? */
		{ 0x180000, 0x182fff, pipibibi_videoram16_w },	/* TileRAM */
		{ 0x188000, 0x18800f, pipibibi_scroll_w },
		{ 0x190010, 0x190011, pipibibi_z80_task_w },	/* Z80 task to perform */
		{ 0x19c01c, 0x19c01d, toaplan2_coin_word_w },	/* Coin count/lock */
	MEMORY_END
	
	static MEMORY_READ16_START( fixeight_readmem )
		{ 0x000000, 0x07ffff, MRA16_ROM },
		{ 0x100000, 0x103fff, MRA16_RAM },
		{ 0x200000, 0x200001, input_port_1_word_r },	/* Player 1 controls */
		{ 0x200004, 0x200005, input_port_2_word_r },	/* Player 2 controls */
		{ 0x200008, 0x200009, input_port_3_word_r },	/* Player 3 controls */
		{ 0x200010, 0x200011, input_port_4_word_r },	/* Coin/System inputs */
		{ 0x280000, 0x28dfff, MRA16_RAM },				/* part of shared ram ? */
	#if Zx80
		{ 0x28e000, 0x28fbff, shared_ram_r },			/* $28f000 status port */
		{ 0x28fc00, 0x28ffff, Zx80_sharedram_r },		/* 16-bit on 68000 side, 8-bit on Zx80 side */
	#else
		{ 0x28e000, 0x28efff, shared_ram_r },
		{ 0x28f000, 0x28f001, Zx80_status_port_r },		/* Zx80 status port */
	//	{ 0x28f002, 0x28f003, MRA16_RAM },				/* part of shared ram */
	//	{ 0x28f004, 0x28f005, input_port_5_word_r },	/* Dip Switch A - Wrong !!! */
	//	{ 0x28f006, 0x28f007, input_port_6_word_r },	/* Dip Switch B - Wrong !!! */
	//	{ 0x28f008, 0x28f009, input_port_7_word_r },	/* Territory Jumper block - Wrong !!! */
	//	{ 0x28f002, 0x28fbff, MRA16_RAM },				/* part of shared ram */
		{ 0x28fc00, 0x28ffff, Zx80_sharedram_r },		/* 16-bit on 68000 side, 8-bit on Zx80 side */
	#endif
		{ 0x300004, 0x300007, toaplan2_0_videoram16_r },
		{ 0x30000c, 0x30000d, toaplan2_inputport_0_word_r },
		{ 0x400000, 0x400fff, paletteram16_word_r },
		{ 0x500000, 0x501fff, toaplan2_txvideoram16_r },
		{ 0x502000, 0x5021ff, toaplan2_txvideoram16_offs_r },
		{ 0x503000, 0x5031ff, toaplan2_txscrollram16_r },
		{ 0x600000, 0x60ffff, toaplan2_tx_gfxram16_r },
		{ 0x800000, 0x800001, video_count_r },
	MEMORY_END
	
	static MEMORY_WRITE16_START( fixeight_writemem )
		{ 0x000000, 0x07ffff, MWA16_ROM },
		{ 0x100000, 0x103fff, MWA16_RAM },
		{ 0x20001c, 0x20001d, toaplan2_coin_word_w },	/* Coin count/lock */
		{ 0x280000, 0x28dfff, MWA16_RAM },				/* part of shared ram ? */
	#if Zx80
		{ 0x28e000, 0x28fbff, shared_ram_w, &toaplan2_shared_ram16 },	/* $28F000 */
		{ 0x28fc00, 0x28ffff, Zx80_sharedram_w, &Zx80_shared_ram },	/* 16-bit on 68000 side, 8-bit on Zx80 side */
	#else
		{ 0x28e000, 0x28efff, shared_ram_w, &toaplan2_shared_ram16 },
		{ 0x28f000, 0x28f001, Zx80_command_port_w },	/* Zx80 command port */
	//	{ 0x28f002, 0x28f003, MWA16_RAM },				/* part of shared ram */
	//	{ 0x28f004, 0x28f009, MWA16_NOP },				/* part of shared ram */
	//	{ 0x28f002, 0x28fbff, MWA16_RAM },				/* part of shared ram */
		{ 0x28fc00, 0x28ffff, Zx80_sharedram_w, &Zx80_shared_ram },	/* 16-bit on 68000 side, 8-bit on Zx80 side */
	#endif
		{ 0x300000, 0x300001, toaplan2_0_voffs_w },		/* VideoRAM selector/offset */
		{ 0x300004, 0x300007, toaplan2_0_videoram16_w },/* Tile/Sprite VideoRAM */
		{ 0x300008, 0x300009, toaplan2_0_scroll_reg_select_w },
		{ 0x30000c, 0x30000d, toaplan2_0_scroll_reg_data_w },
		{ 0x400000, 0x400fff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
		{ 0x500000, 0x501fff, toaplan2_txvideoram16_w, &toaplan2_txvideoram16, &toaplan2_tx_vram_size },
		{ 0x502000, 0x5021ff, toaplan2_txvideoram16_offs_w, &toaplan2_txvideoram16_offs, &toaplan2_tx_offs_vram_size },
		{ 0x503000, 0x5031ff, toaplan2_txscrollram16_w, &toaplan2_txscrollram16, &toaplan2_tx_scroll_vram_size },
		{ 0x600000, 0x60ffff, toaplan2_tx_gfxram16_w, &toaplan2_tx_gfxram16 },
	MEMORY_END
	
	static MEMORY_READ16_START( vfive_readmem )
		{ 0x000000, 0x07ffff, MRA16_ROM },
		{ 0x100000, 0x103fff, MRA16_RAM },
	//	{ 0x200000, 0x20ffff, MRA16_ROM },				/* Sound ROM is here ??? */
		{ 0x200010, 0x200011, input_port_1_word_r },	/* Player 1 controls */
		{ 0x200014, 0x200015, input_port_2_word_r },	/* Player 2 controls */
		{ 0x200018, 0x200019, input_port_3_word_r },	/* Coin/System inputs */
	#if Zx80
		{ 0x21e000, 0x21fbff, shared_ram_r },			/* $21f000 status port */
		{ 0x21fc00, 0x21ffff, Zx80_sharedram_r },		/* 16-bit on 68000 side, 8-bit on Zx80 side */
	#else
		{ 0x21e000, 0x21efff, shared_ram_r },
		{ 0x21f000, 0x21f001, Zx80_status_port_r },		/* Zx80 status port */
		{ 0x21f004, 0x21f005, input_port_4_word_r },	/* Dip Switch A */
		{ 0x21f006, 0x21f007, input_port_5_word_r },	/* Dip Switch B */
		{ 0x21f008, 0x21f009, input_port_6_word_r },	/* Territory Jumper block */
		{ 0x21fc00, 0x21ffff, Zx80_sharedram_r },		/* 16-bit on 68000 side, 8-bit on Zx80 side */
	#endif
		{ 0x300004, 0x300007, toaplan2_0_videoram16_r },
		{ 0x30000c, 0x30000d, toaplan2_inputport_0_word_r },
		{ 0x400000, 0x400fff, paletteram16_word_r },
		{ 0x700000, 0x700001, video_count_r },
	MEMORY_END
	
	static MEMORY_WRITE16_START( vfive_writemem )
		{ 0x000000, 0x07ffff, MWA16_ROM },
		{ 0x100000, 0x103fff, MWA16_RAM },
	//	{ 0x200000, 0x20ffff, MWA16_ROM },				/* Sound ROM is here ??? */
		{ 0x20001c, 0x20001d, toaplan2_coin_word_w },	/* Coin count/lock */
	#if Zx80
		{ 0x21e000, 0x21fbff, shared_ram_w, &toaplan2_shared_ram16 },	/* $21F000 */
		{ 0x21fc00, 0x21ffff, Zx80_sharedram_w, &Zx80_shared_ram },	/* 16-bit on 68000 side, 8-bit on Zx80 side */
	#else
		{ 0x21e000, 0x21efff, shared_ram_w, &toaplan2_shared_ram16 },
		{ 0x21f000, 0x21f001, Zx80_command_port_w },	/* Zx80 command port */
		{ 0x21fc00, 0x21ffff, Zx80_sharedram_w, &Zx80_shared_ram },	/* 16-bit on 68000 side, 8-bit on Zx80 side */
	#endif
		{ 0x300000, 0x300001, toaplan2_0_voffs_w },		/* VideoRAM selector/offset */
		{ 0x300004, 0x300007, toaplan2_0_videoram16_w },/* Tile/Sprite VideoRAM */
		{ 0x300008, 0x300009, toaplan2_0_scroll_reg_select_w },
		{ 0x30000c, 0x30000d, toaplan2_0_scroll_reg_data_w },
		{ 0x400000, 0x400fff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
	MEMORY_END
	
	static MEMORY_READ16_START( batsugun_readmem )
		{ 0x000000, 0x07ffff, MRA16_ROM },
		{ 0x100000, 0x10ffff, MRA16_RAM },
		{ 0x200010, 0x200011, input_port_1_word_r },	/* Player 1 controls */
		{ 0x200014, 0x200015, input_port_2_word_r },	/* Player 2 controls */
		{ 0x200018, 0x200019, input_port_3_word_r },	/* Coin/System inputs */
		{ 0x210000, 0x21bbff, MRA16_RAM },
	#if Zx80
		{ 0x21e000, 0x21fbff, shared_ram_r },			/* $21f000 status port */
		{ 0x21fc00, 0x21ffff, Zx80_sharedram_r },		/* 16-bit on 68000 side, 8-bit on Zx80 side */
	#else
		{ 0x21e000, 0x21efff, shared_ram_r },
		{ 0x21f000, 0x21f001, Zx80_status_port_r },		/* Zx80 status port */
		{ 0x21f004, 0x21f005, input_port_4_word_r },	/* Dip Switch A */
		{ 0x21f006, 0x21f007, input_port_5_word_r },	/* Dip Switch B */
		{ 0x21f008, 0x21f009, input_port_6_word_r },	/* Territory Jumper block */
		{ 0x21fc00, 0x21ffff, Zx80_sharedram_r },		/* 16-bit on 68000 side, 8-bit on Zx80 side */
	#endif
		/***** The following in 0x30000x are for video controller 1 ******/
		{ 0x300004, 0x300007, toaplan2_0_videoram16_r },/* tile layers */
		{ 0x30000c, 0x30000d, toaplan2_inputport_0_word_r },	/* VBlank */
		{ 0x400000, 0x400fff, paletteram16_word_r },
		/***** The following in 0x50000x are for video controller 2 ******/
		{ 0x500004, 0x500007, toaplan2_1_videoram16_r },/* tile layers 2 */
		{ 0x700000, 0x700001, video_count_r },
	MEMORY_END
	
	static MEMORY_WRITE16_START( batsugun_writemem )
		{ 0x000000, 0x07ffff, MWA16_ROM },
		{ 0x100000, 0x10ffff, MWA16_RAM },
		{ 0x20001c, 0x20001d, toaplan2_coin_word_w },	/* Coin count/lock */
		{ 0x210000, 0x21bbff, MWA16_RAM },
	#if Zx80
		{ 0x21e000, 0x21fbff, shared_ram_w, &toaplan2_shared_ram16 },	/* $21F000 */
		{ 0x21fc00, 0x21ffff, Zx80_sharedram_w, &Zx80_shared_ram },	/* 16-bit on 68000 side, 8-bit on Zx80 side */
	#else
		{ 0x21e000, 0x21efff, shared_ram_w, &toaplan2_shared_ram16 },
		{ 0x21f000, 0x21f001, Zx80_command_port_w },	/* Zx80 command port */
		{ 0x21fc00, 0x21ffff, Zx80_sharedram_w, &Zx80_shared_ram },	/* 16-bit on 68000 side, 8-bit on Zx80 side */
	#endif
		/***** The following in 0x30000x are for video controller 1 ******/
		{ 0x300000, 0x300001, toaplan2_0_voffs_w },		/* VideoRAM selector/offset */
		{ 0x300004, 0x300007, toaplan2_0_videoram16_w },/* Tile/Sprite VideoRAM */
		{ 0x300008, 0x300009, toaplan2_0_scroll_reg_select_w },
		{ 0x30000c, 0x30000d, toaplan2_0_scroll_reg_data_w },
		{ 0x400000, 0x400fff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
		/***** The following in 0x50000x are for video controller 2 ******/
		{ 0x500000, 0x500001, toaplan2_1_voffs_w },		/* VideoRAM selector/offset */
		{ 0x500004, 0x500007, toaplan2_1_videoram16_w },/* Tile/Sprite VideoRAM */
		{ 0x500008, 0x500009, toaplan2_1_scroll_reg_select_w },
		{ 0x50000c, 0x50000d, toaplan2_1_scroll_reg_data_w },
	MEMORY_END
	
	static MEMORY_READ16_START( snowbro2_readmem )
		{ 0x000000, 0x07ffff, MRA16_ROM },
		{ 0x100000, 0x10ffff, MRA16_RAM },
		{ 0x300004, 0x300007, toaplan2_0_videoram16_r },/* tile layers */
		{ 0x30000c, 0x30000d, toaplan2_inputport_0_word_r },	/* VBlank */
		{ 0x400000, 0x400fff, paletteram16_word_r },
		{ 0x500002, 0x500003, YM2151_status_port_0_lsb_r },
		{ 0x600000, 0x600001, OKIM6295_status_0_lsb_r },
		{ 0x700000, 0x700001, input_port_8_word_r },	/* Territory Jumper block */
		{ 0x700004, 0x700005, input_port_6_word_r },	/* Dip Switch A */
		{ 0x700008, 0x700009, input_port_7_word_r },	/* Dip Switch B */
		{ 0x70000c, 0x70000d, input_port_1_word_r },	/* Player 1 controls */
		{ 0x700010, 0x700011, input_port_2_word_r },	/* Player 2 controls */
		{ 0x700014, 0x700015, input_port_3_word_r },	/* Player 3 controls */
		{ 0x700018, 0x700019, input_port_4_word_r },	/* Player 4 controls */
		{ 0x70001c, 0x70001d, input_port_5_word_r },	/* Coin/System inputs */
	MEMORY_END
	
	static MEMORY_WRITE16_START( snowbro2_writemem )
		{ 0x000000, 0x07ffff, MWA16_ROM },
		{ 0x100000, 0x10ffff, MWA16_RAM },
		{ 0x300000, 0x300001, toaplan2_0_voffs_w },		/* VideoRAM selector/offset */
		{ 0x300004, 0x300007, toaplan2_0_videoram16_w },/* Tile/Sprite VideoRAM */
		{ 0x300008, 0x300009, toaplan2_0_scroll_reg_select_w },
		{ 0x30000c, 0x30000d, toaplan2_0_scroll_reg_data_w },
		{ 0x400000, 0x400fff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
		{ 0x500000, 0x500001, YM2151_register_port_0_lsb_w },
		{ 0x500002, 0x500003, YM2151_data_port_0_lsb_w },
		{ 0x600000, 0x600001, OKIM6295_data_0_lsb_w },
		{ 0x700030, 0x700031, oki_bankswitch_w },		/* Sample bank switch */
		{ 0x700034, 0x700035, toaplan2_coin_word_w },	/* Coin count/lock */
	MEMORY_END
	
	static MEMORY_READ16_START( mahoudai_readmem )
		{ 0x000000, 0x07ffff, MRA16_ROM },
		{ 0x100000, 0x10ffff, MRA16_RAM },
		{ 0x218000, 0x21bfff, raizing_shared_ram_r },
		{ 0x21c020, 0x21c021, input_port_1_word_r },	/* Player 1 controls */
		{ 0x21c024, 0x21c025, input_port_2_word_r },	/* Player 2 controls */
		{ 0x21c028, 0x21c029, input_port_3_word_r },	/* Coin/System inputs */
		{ 0x21c02c, 0x21c02d, input_port_4_word_r },	/* Dip Switch A */
		{ 0x21c030, 0x21c031, input_port_5_word_r },	/* Dip Switch B */
		{ 0x21c034, 0x21c035, input_port_6_word_r },	/* Territory Jumper block */
		{ 0x21c03c, 0x21c03d, video_count_r },
		{ 0x300004, 0x300007, toaplan2_0_videoram16_r },/* Tile/Sprite VideoRAM */
		{ 0x30000c, 0x30000d, toaplan2_inputport_0_word_r },	/* VBlank */
		{ 0x400000, 0x400fff, paletteram16_word_r },
		{ 0x401000, 0x4017ff, MRA16_RAM },				/* Unused PaletteRAM */
		{ 0x500000, 0x501fff, toaplan2_txvideoram16_r },
		{ 0x502000, 0x5021ff, toaplan2_txvideoram16_offs_r },
		{ 0x502200, 0x502fff, MRA16_RAM },
		{ 0x503000, 0x5031ff, toaplan2_txscrollram16_r },
		{ 0x503200, 0x503fff, MRA16_RAM },
	MEMORY_END
	
	static MEMORY_WRITE16_START( mahoudai_writemem )
		{ 0x000000, 0x07ffff, MWA16_ROM },
		{ 0x100000, 0x10ffff, MWA16_RAM },
		{ 0x218000, 0x21bfff, raizing_shared_ram_w },
		{ 0x21c01c, 0x21c01d, toaplan2_coin_word_w },
		{ 0x300000, 0x300001, toaplan2_0_voffs_w },
		{ 0x300004, 0x300007, toaplan2_0_videoram16_w },
		{ 0x300008, 0x300009, toaplan2_0_scroll_reg_select_w },
		{ 0x30000c, 0x30000d, toaplan2_0_scroll_reg_data_w },
		{ 0x400000, 0x400fff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
		{ 0x401000, 0x4017ff, MWA16_RAM },
		{ 0x500000, 0x501fff, toaplan2_txvideoram16_w, &toaplan2_txvideoram16, &toaplan2_tx_vram_size },
		{ 0x502000, 0x5021ff, toaplan2_txvideoram16_offs_w, &toaplan2_txvideoram16_offs, &toaplan2_tx_offs_vram_size },
		{ 0x502200, 0x502fff, MWA16_RAM },
		{ 0x503000, 0x5031ff, toaplan2_txscrollram16_w, &toaplan2_txscrollram16, &toaplan2_tx_scroll_vram_size },
		{ 0x503200, 0x503fff, MWA16_RAM },
	MEMORY_END
	
	static MEMORY_READ16_START( shippumd_readmem )
		{ 0x000000, 0x0fffff, MRA16_ROM },
		{ 0x100000, 0x10ffff, MRA16_RAM },
		{ 0x218000, 0x21bfff, raizing_shared_ram_r },
		{ 0x21c020, 0x21c021, input_port_1_word_r },	/* Player 1 controls */
		{ 0x21c024, 0x21c025, input_port_2_word_r },	/* Player 2 controls */
		{ 0x21c028, 0x21c029, input_port_3_word_r },	/* Coin/System inputs */
		{ 0x21c02c, 0x21c02d, input_port_4_word_r },	/* Dip Switch A */
		{ 0x21c030, 0x21c031, input_port_5_word_r },	/* Dip Switch B */
		{ 0x21c034, 0x21c035, input_port_6_word_r },	/* Territory Jumper block */
		{ 0x21c03c, 0x21c03d, video_count_r },
		{ 0x300004, 0x300007, toaplan2_0_videoram16_r },/* Tile/Sprite VideoRAM */
		{ 0x30000c, 0x30000d, toaplan2_inputport_0_word_r },	/* VBlank */
		{ 0x400000, 0x400fff, paletteram16_word_r },
		{ 0x401000, 0x4017ff, MRA16_RAM },				/* Unused PaletteRAM */
		{ 0x500000, 0x501fff, toaplan2_txvideoram16_r },
		{ 0x502000, 0x5021ff, toaplan2_txvideoram16_offs_r },
		{ 0x502200, 0x502fff, MRA16_RAM },
		{ 0x503000, 0x5031ff, toaplan2_txscrollram16_r },
		{ 0x503200, 0x503fff, MRA16_RAM },
	MEMORY_END
	
	static MEMORY_WRITE16_START( shippumd_writemem )
		{ 0x000000, 0x0fffff, MWA16_ROM },
		{ 0x100000, 0x10ffff, MWA16_RAM },
		{ 0x218000, 0x21bfff, raizing_shared_ram_w },
	//	{ 0x21c008, 0x21c009, MWA16_NOP },				/* ??? */
		{ 0x21c01c, 0x21c01d, toaplan2_coin_word_w },
		{ 0x300000, 0x300001, toaplan2_0_voffs_w },
		{ 0x300004, 0x300007, toaplan2_0_videoram16_w },
		{ 0x300008, 0x300009, toaplan2_0_scroll_reg_select_w },
		{ 0x30000c, 0x30000d, toaplan2_0_scroll_reg_data_w },
		{ 0x400000, 0x400fff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
		{ 0x401000, 0x4017ff, MWA16_RAM },
		{ 0x500000, 0x501fff, toaplan2_txvideoram16_w, &toaplan2_txvideoram16, &toaplan2_tx_vram_size },
		{ 0x502000, 0x5021ff, toaplan2_txvideoram16_offs_w, &toaplan2_txvideoram16_offs, &toaplan2_tx_offs_vram_size },
		{ 0x502200, 0x502fff, MWA16_RAM },
		{ 0x503000, 0x5031ff, toaplan2_txscrollram16_w, &toaplan2_txscrollram16, &toaplan2_tx_scroll_vram_size },
		{ 0x503200, 0x503fff, MWA16_RAM },
	MEMORY_END
	
	static MEMORY_READ16_START( battleg_readmem )
		{ 0x000000, 0x0fffff, MRA16_ROM },
		{ 0x100000, 0x10ffff, MRA16_RAM },
		{ 0x218020, 0x218023, battleg_z80check_r },
		{ 0x21c020, 0x21c021, input_port_1_word_r },	/* Player 1 controls */
		{ 0x21c024, 0x21c025, input_port_2_word_r },	/* Player 2 controls */
		{ 0x21c028, 0x21c029, input_port_3_word_r },	/* Coin/System inputs */
		{ 0x21c02c, 0x21c02d, input_port_4_word_r },	/* Dip Switch A */
		{ 0x21c030, 0x21c031, input_port_5_word_r },	/* Dip Switch B */
		{ 0x21c034, 0x21c035, input_port_6_word_r },	/* Territory Jumper block */
		{ 0x21c03c, 0x21c03d, video_count_r },
		{ 0x300004, 0x300007, toaplan2_0_videoram16_r },/* Tile/Sprite VideoRAM */
		{ 0x30000c, 0x30000d, toaplan2_inputport_0_word_r },	/* VBlank */
		{ 0x400000, 0x400fff, paletteram16_word_r },
		{ 0x500000, 0x501fff, toaplan2_txvideoram16_r },
		{ 0x502000, 0x5021ff, toaplan2_txvideoram16_offs_r },
		{ 0x502200, 0x502fff, MRA16_RAM },
		{ 0x503000, 0x5031ff, toaplan2_txscrollram16_r },
		{ 0x503200, 0x503fff, MRA16_RAM },
		{ 0x600000, 0x600fff, battleg_commram_r },		/* CommRAM check */
	MEMORY_END
	
	static MEMORY_WRITE16_START( battleg_writemem )
		{ 0x000000, 0x0fffff, MWA16_ROM },
		{ 0x100000, 0x10ffff, MWA16_RAM },
		{ 0x21c01c, 0x21c01d, toaplan2_coin_word_w },
		{ 0x300000, 0x300001, toaplan2_0_voffs_w },
		{ 0x300004, 0x300007, toaplan2_0_videoram16_w },
		{ 0x300008, 0x300009, toaplan2_0_scroll_reg_select_w },
		{ 0x30000c, 0x30000d, toaplan2_0_scroll_reg_data_w },
		{ 0x400000, 0x400fff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 },
		{ 0x500000, 0x501fff, toaplan2_txvideoram16_w, &toaplan2_txvideoram16, &toaplan2_tx_vram_size },
		{ 0x502000, 0x5021ff, toaplan2_txvideoram16_offs_w, &toaplan2_txvideoram16_offs, &toaplan2_tx_offs_vram_size },
		{ 0x502200, 0x502fff, MWA16_RAM },
		{ 0x503000, 0x5031ff, toaplan2_txscrollram16_w, &toaplan2_txscrollram16, &toaplan2_tx_scroll_vram_size },
		{ 0x503200, 0x503fff, MWA16_RAM },
		{ 0x600000, 0x600fff, battleg_commram_w, &battleg_commram16 },
	MEMORY_END
	
	static MEMORY_READ16_START( batrider_readmem )
		{ 0x000000, 0x1fffff, MRA16_ROM },
		{ 0x200000, 0x201fff, toaplan2_txvideoram16_r },/* Text VideoRAM */
		{ 0x202000, 0x202fff, paletteram16_word_r },
		{ 0x203000, 0x2031ff, toaplan2_txvideoram16_offs_r },
		{ 0x203200, 0x2033ff, toaplan2_txscrollram16_r },
		{ 0x203400, 0x207fff, raizing_tx_gfxram16_r },	/* Main RAM actually */
		{ 0x208000, 0x20ffff, MRA16_RAM },
		{ 0x300000, 0x37ffff, raizing_z80rom_r },
		{ 0x400000, 0x400001, toaplan2_inputport_0_word_r },	/* VBlank */
		{ 0x400008, 0x40000b, toaplan2_0_videoram16_r },/* Tile/Sprite VideoRAM */
		{ 0x500000, 0x500001, input_port_1_word_r },
		{ 0x500002, 0x500003, input_port_2_word_r },
		{ 0x500004, 0x500005, input_port_3_word_r },
		{ 0x500006, 0x500007, video_count_r },
		{ 0x500008, 0x50000b, raizing_sndcomms_r },
		{ 0x50000c, 0x50000d, batrider_z80_busack_r },
	MEMORY_END
	
	static MEMORY_WRITE16_START( batrider_writemem )
		{ 0x000000, 0x1fffff, MWA16_ROM },
		{ 0x200000, 0x201fff, toaplan2_txvideoram16_w, &toaplan2_txvideoram16, &toaplan2_tx_vram_size },
		{ 0x202000, 0x202fff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 , &paletteram_size },
		{ 0x203000, 0x2031ff, toaplan2_txvideoram16_offs_w, &toaplan2_txvideoram16_offs, &toaplan2_tx_offs_vram_size },
		{ 0x203200, 0x2033ff, toaplan2_txscrollram16_w, &toaplan2_txscrollram16, &toaplan2_tx_scroll_vram_size },
		{ 0x203400, 0x207fff, raizing_tx_gfxram16_w },
		{ 0x208000, 0x20ffff, MWA16_RAM },
		{ 0x400000, 0x400001, toaplan2_0_scroll_reg_data_w },
		{ 0x400004, 0x400005, toaplan2_0_scroll_reg_select_w },
		{ 0x400008, 0x40000b, toaplan2_0_videoram16_w },
		{ 0x40000c, 0x40000d, toaplan2_0_voffs_w },
		{ 0x500010, 0x500011, toaplan2_coin_word_w },
		{ 0x500020, 0x500023, raizing_sndcomms_w, &raizing_cpu_comm16 },
		{ 0x500060, 0x500061, batrider_z80_busreq_w },
		{ 0x500080, 0x500081, batrider_textdata_decode },
		{ 0x5000c0, 0x5000cf, batrider_objectbank_w },
	MEMORY_END
	
	static MEMORY_READ16_START( bbakraid_readmem )
		{ 0x000000, 0x1fffff, MRA16_ROM },
		{ 0x200000, 0x201fff, toaplan2_txvideoram16_r },/* Text VideoRAM */
		{ 0x202000, 0x202fff, paletteram16_word_r },
		{ 0x203000, 0x2031ff, toaplan2_txvideoram16_offs_r },
		{ 0x203200, 0x2033ff, toaplan2_txscrollram16_r },
		{ 0x203400, 0x207fff, raizing_tx_gfxram16_r },	/* Main RAM actually */
		{ 0x208000, 0x20ffff, MRA16_RAM },
		{ 0x300000, 0x33ffff, raizing_z80rom_r },
		{ 0x400000, 0x400001, toaplan2_inputport_0_word_r },	/* VBlank */
		{ 0x400008, 0x40000b, toaplan2_0_videoram16_r },/* Tile/Sprite VideoRAM */
		{ 0x500000, 0x500001, input_port_1_word_r },
		{ 0x500002, 0x500003, input_port_2_word_r },
		{ 0x500004, 0x500005, input_port_3_word_r },
		{ 0x500006, 0x500007, video_count_r },
		{ 0x500010, 0x500013, raizing_sndcomms_r },
		{ 0x500018, 0x500019, bbakraid_nvram_r },
	MEMORY_END
	
	static MEMORY_WRITE16_START( bbakraid_writemem )
		{ 0x000000, 0x1fffff, MWA16_ROM },
		{ 0x200000, 0x201fff, toaplan2_txvideoram16_w, &toaplan2_txvideoram16, &toaplan2_tx_vram_size },
		{ 0x202000, 0x202fff, paletteram16_xBBBBBGGGGGRRRRR_word_w, &paletteram16 , &paletteram_size },
		{ 0x203000, 0x2031ff, toaplan2_txvideoram16_offs_w, &toaplan2_txvideoram16_offs, &toaplan2_tx_offs_vram_size },
		{ 0x203200, 0x2033ff, toaplan2_txscrollram16_w, &toaplan2_txscrollram16, &toaplan2_tx_scroll_vram_size },
		{ 0x203400, 0x207fff, raizing_tx_gfxram16_w },
		{ 0x208000, 0x20ffff, MWA16_RAM },
		{ 0x400000, 0x400001, toaplan2_0_scroll_reg_data_w },
		{ 0x400004, 0x400005, toaplan2_0_scroll_reg_select_w },
		{ 0x400008, 0x40000b, toaplan2_0_videoram16_w },
		{ 0x40000c, 0x40000d, toaplan2_0_voffs_w },
		{ 0x500008, 0x500009, bbakraid_trigger_z80_irq },
		{ 0x500010, 0x500011, toaplan2_coin_word_w },
		{ 0x500014, 0x500017, raizing_sndcomms_w, &raizing_cpu_comm16 },
		{ 0x50001e, 0x50001f, bbakraid_nvram_w },
		{ 0x500080, 0x500081, batrider_textdata_decode },
		{ 0x5000c0, 0x5000cf, batrider_objectbank_w },
	MEMORY_END
	
	
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new Memory_ReadAddress( 0xe000, 0xe000, YM3812_status_port_0_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x87ff, MWA_RAM, &toaplan2_shared_ram ),
		new Memory_WriteAddress( 0xe000, 0xe000, YM3812_control_port_0_w ),
		new Memory_WriteAddress( 0xe001, 0xe001, YM3812_write_port_0_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress raizing_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xdfff, MRA_RAM ),
		new Memory_ReadAddress( 0xe001, 0xe001, YM2151_status_port_0_r ),
		new Memory_ReadAddress( 0xe004, 0xe004, OKIM6295_status_0_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress raizing_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xdfff, MWA_RAM, &raizing_shared_ram ),
		new Memory_WriteAddress( 0xe000, 0xe000, YM2151_register_port_0_w ),
		new Memory_WriteAddress( 0xe001, 0xe001, YM2151_data_port_0_w ),
		new Memory_WriteAddress( 0xe004, 0xe004, OKIM6295_data_0_w ),
		new Memory_WriteAddress( 0xe00e, 0xe00e, toaplan2_coin_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress battleg_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xc000, 0xdfff, MRA_RAM ),
		new Memory_ReadAddress( 0xe001, 0xe001, YM2151_status_port_0_r ),
		new Memory_ReadAddress( 0xe004, 0xe004, OKIM6295_status_0_r ),
		new Memory_ReadAddress( 0xe01c, 0xe01d, battleg_commram_check_r0 ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress battleg_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xdfff, MWA_RAM, &raizing_shared_ram ),
		new Memory_WriteAddress( 0xe000, 0xe000, YM2151_register_port_0_w ),
		new Memory_WriteAddress( 0xe001, 0xe001, YM2151_data_port_0_w ),
		new Memory_WriteAddress( 0xe004, 0xe004, OKIM6295_data_0_w ),
		new Memory_WriteAddress( 0xe006, 0xe006, raizing_okim6295_bankselect_0 ),
		new Memory_WriteAddress( 0xe008, 0xe008, raizing_okim6295_bankselect_1 ),
		new Memory_WriteAddress( 0xe00a, 0xe00a, battleg_bankswitch_w ),
		new Memory_WriteAddress( 0xe00c, 0xe00c, battleg_commram_check_w0 ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress batrider_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xc000, 0xdfff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress batrider_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xdfff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	static PORT_READ_START( batrider_sound_readport )
		{ 0x48, 0x48, raizing_command_r },
		{ 0x4a, 0x4a, raizing_request_r },
		{ 0x81, 0x81, YM2151_status_port_0_r },
		{ 0x82, 0x82, OKIM6295_status_0_r },
		{ 0x84, 0x84, OKIM6295_status_1_r },
	PORT_END
	
	static PORT_WRITE_START( batrider_sound_writeport )
		{ 0x40, 0x40, raizing_command_ack_w },		/* Tune control */
		{ 0x42, 0x42, raizing_request_ack_w },		/* Tune to play */
		{ 0x46, 0x46, raizing_clear_nmi_w },		/* Clear the NMI state */
		{ 0x80, 0x80, YM2151_register_port_0_w },
		{ 0x81, 0x81, YM2151_data_port_0_w },
		{ 0x82, 0x82, OKIM6295_data_0_w },
		{ 0x84, 0x84, OKIM6295_data_1_w },
		{ 0x88, 0x88, batrider_bankswitch_w },
		{ 0xc0, 0xc0, raizing_okim6295_bankselect_0 },
		{ 0xc2, 0xc2, raizing_okim6295_bankselect_1 },
		{ 0xc4, 0xc4, raizing_okim6295_bankselect_2 },
		{ 0xc6, 0xc6, raizing_okim6295_bankselect_3 },
	PORT_END
	
	public static Memory_ReadAddress bbakraid_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xbfff, MRA_BANK1 ),
		new Memory_ReadAddress( 0xc000, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress bbakraid_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),	/* Only 2FFFh valid code */
		new Memory_WriteAddress( 0xc000, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	static PORT_READ_START( bbakraid_sound_readport )
		{ 0x48, 0x48, raizing_command_r },
		{ 0x4a, 0x4a, raizing_request_r },
		{ 0x81, 0x81, YMZ280B_status_0_r },
	PORT_END
	
	static PORT_WRITE_START( bbakraid_sound_writeport )
		{ 0x40, 0x40, raizing_command_ack_w },		/* Tune control */
		{ 0x42, 0x42, raizing_request_ack_w },		/* Tune to play */
		{ 0x46, 0x46, raizing_clear_nmi_w },		/* Clear the NMI state */
		{ 0x80, 0x80, YMZ280B_register_0_w },
		{ 0x81, 0x81, YMZ280B_data_0_w },
	PORT_END
	
	
	#if HD64x180
	public static Memory_ReadAddress hd647180_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xfe00, 0xffff, MRA_RAM ),			/* Internal 512 bytes of RAM */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress hd647180_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0xfe00, 0xffff, MWA_RAM ),			/* Internal 512 bytes of RAM */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	#endif
	
	
	#if Zx80
	public static Memory_ReadAddress Zx80_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x00000, 0x03fff, MRA_ROM ),
	//	new Memory_ReadAddress( 0x00000, 0x007ff, MRA_RAM ),			/* External shared RAM (Banked) */
		new Memory_ReadAddress( 0x04000, 0x04000, YM2151_status_port_0_r ),
		new Memory_ReadAddress( 0x04002, 0x04002, OKIM6295_status_0_r ),
		new Memory_ReadAddress( 0x04008, 0x04008, input_port_1_r ),
		new Memory_ReadAddress( 0x0400a, 0x0400a, input_port_2_r ),
		new Memory_ReadAddress( 0x0400c, 0x0400c, input_port_3_r ),
		new Memory_ReadAddress( 0x0fe00, 0x0ffff, MRA_RAM ),			/* Internal 512 bytes of RAM */
		new Memory_ReadAddress( 0x80000, 0x87fff, MRA_RAM ),			/* External shared RAM (ROM for KBASH) */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress Zx80_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x00000, 0x03fff, MWA_ROM, ),
	//	new Memory_WriteAddress( 0x00000, 0x007ff, MWA_RAM, ),			/* External shared RAM (Banked) */
		new Memory_WriteAddress( 0x04000, 0x04000, YM2151_register_port_0_w ),
		new Memory_WriteAddress( 0x04001, 0x04001, YM2151_data_port_0_w ),
		new Memory_WriteAddress( 0x04002, 0x04002, OKIM6295_data_0_w ),
		new Memory_WriteAddress( 0x04004, 0x04004, oki_bankswitch_w ),
		new Memory_WriteAddress( 0x0400e, 0x0400e, toaplan2_coin_w ),
		new Memory_WriteAddress( 0x0fe00, 0x0ffff, MWA_RAM ),			/* Internal 512 bytes of RAM */
		new Memory_WriteAddress( 0x80000, 0x87fff, MWA_RAM, &Zx80_sharedram ),	/* External shared RAM (ROM for KBASH) */
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	static PORT_READ_START( Zx80_readport )
		{ 0x0060, 0x0060, input_port_4_r },		/* Directly mapped I/O ports */
		{ 0x0061, 0x0061, input_port_5_r },		/* Directly mapped I/O ports */
		{ 0x0062, 0x0062, input_port_6_r },		/* Directly mapped I/O ports */
	PORT_END
	#endif
	
	
	
	/*****************************************************************************
		Input Port definitions
		Service input of the TOAPLAN2_SYSTEM_INPUTS is used as a Pause type input.
		If you press then release the following buttons, the following occurs:
		Service & P2 start            : The game will pause.
		P1 start                      : The game will continue.
		Service & P1 start & P2 start : The game will play in slow motion.
	*****************************************************************************/
	
	#define  TOAPLAN2_PLAYER_INPUT( player, button3, button4 )						\
		PORT_START(); 																	\
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | player );\
		PORT_BIT( 0x0002, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | player );\
		PORT_BIT( 0x0004, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | player );\
		PORT_BIT( 0x0008, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | player );\
		PORT_BIT( 0x0010, IP_ACTIVE_HIGH, IPT_BUTTON1 | player );				\
		PORT_BIT( 0x0020, IP_ACTIVE_HIGH, IPT_BUTTON2 | player );				\
		PORT_BIT( 0x0040, IP_ACTIVE_HIGH, button3 | player );					\
		PORT_BIT( 0x0080, IP_ACTIVE_HIGH, button4 | player );					\
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
	#define  SNOWBRO2_PLAYER_INPUT( player, button3, button4 )						\
		PORT_START(); 																	\
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | player );\
		PORT_BIT( 0x0002, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | player );\
		PORT_BIT( 0x0004, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | player );\
		PORT_BIT( 0x0008, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | player );\
		PORT_BIT( 0x0010, IP_ACTIVE_HIGH, IPT_BUTTON1 | player );				\
		PORT_BIT( 0x0020, IP_ACTIVE_HIGH, IPT_BUTTON2 | player );				\
		PORT_BIT( 0x0040, IP_ACTIVE_HIGH, button3 );								\
		PORT_BIT( 0x0080, IP_ACTIVE_HIGH, button4 );								\
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
	#define  TOAPLAN2_SYSTEM_INPUTS						\
		PORT_START(); 										\
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_SERVICE1 );
		PORT_BIT( 0x0002, IP_ACTIVE_HIGH, IPT_TILT );\
		PORT_BIT( 0x0008, IP_ACTIVE_HIGH, IPT_COIN1 );\
		PORT_BIT( 0x0010, IP_ACTIVE_HIGH, IPT_COIN2 );\
		PORT_BIT( 0x0020, IP_ACTIVE_HIGH, IPT_START1 );\
		PORT_BIT( 0x0040, IP_ACTIVE_HIGH, IPT_START2 );\
		PORT_BIT( 0xff84, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
	#define DSWA_8											\
		PORT_START(); 		/* (4) DSWA */						\
		PORT_DIPNAME( 0x01,	0x00, DEF_STR( "Unused") );		\
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );			\
		PORT_DIPSETTING(	0x01, DEF_STR( "On") );			\
		PORT_DIPNAME( 0x02,	0x00, DEF_STR( "Flip_Screen") );	\
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );			\
		PORT_DIPSETTING(	0x02, DEF_STR( "On") );			\
		PORT_SERVICE( 0x04,	IP_ACTIVE_HIGH );			\
		PORT_DIPNAME( 0x08,	0x00, DEF_STR( "Demo_Sounds") );	\
		PORT_DIPSETTING(	0x08, DEF_STR( "Off") );			\
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
	
	#define EUROPEAN_COINAGE_8							\
		PORT_DIPNAME( 0x30,	0x00, DEF_STR( "Coin_A") );	\
		PORT_DIPSETTING(	0x30, DEF_STR( "4C_1C") );	\
		PORT_DIPSETTING(	0x20, DEF_STR( "3C_1C") );	\
		PORT_DIPSETTING(	0x10, DEF_STR( "2C_1C") );	\
		PORT_DIPSETTING(	0x00, DEF_STR( "1C_1C") );	\
		PORT_DIPNAME( 0xc0,	0x00, DEF_STR( "Coin_B") );	\
		PORT_DIPSETTING(	0x00, DEF_STR( "1C_2C") );	\
		PORT_DIPSETTING(	0x40, DEF_STR( "1C_3C") );	\
		PORT_DIPSETTING(	0x80, DEF_STR( "1C_4C") );	\
		PORT_DIPSETTING(	0xc0, DEF_STR( "1C_6C") );
	
	#define NONEUROPEAN_COINAGE_8						\
		PORT_DIPNAME( 0x30,	0x00, DEF_STR( "Coin_A") );	\
		PORT_DIPSETTING(	0x20, DEF_STR( "2C_1C") );	\
		PORT_DIPSETTING(	0x00, DEF_STR( "1C_1C") );	\
		PORT_DIPSETTING(	0x30, DEF_STR( "2C_3C") );	\
		PORT_DIPSETTING(	0x10, DEF_STR( "1C_2C") );	\
		PORT_DIPNAME( 0xc0,	0x00, DEF_STR( "Coin_B") );	\
		PORT_DIPSETTING(	0x80, DEF_STR( "2C_1C") );	\
		PORT_DIPSETTING(	0x00, DEF_STR( "1C_1C") );	\
		PORT_DIPSETTING(	0xc0, DEF_STR( "2C_3C") );	\
		PORT_DIPSETTING(	0x40, DEF_STR( "1C_2C") );
	
	#define EUROPEAN_COINAGE_16								\
		PORT_DIPNAME( 0x0030,	0x0000, DEF_STR( "Coin_A") );	\
		PORT_DIPSETTING(		0x0030, DEF_STR( "4C_1C") );	\
		PORT_DIPSETTING(		0x0020, DEF_STR( "3C_1C") );	\
		PORT_DIPSETTING(		0x0010, DEF_STR( "2C_1C") );	\
		PORT_DIPSETTING(		0x0000, DEF_STR( "1C_1C") );	\
		PORT_DIPNAME( 0x00c0,	0x0000, DEF_STR( "Coin_B") );	\
		PORT_DIPSETTING(		0x0000, DEF_STR( "1C_2C") );	\
		PORT_DIPSETTING(		0x0040, DEF_STR( "1C_3C") );	\
		PORT_DIPSETTING(		0x0080, DEF_STR( "1C_4C") );	\
		PORT_DIPSETTING(		0x00c0, DEF_STR( "1C_6C") );
	
	#define NONEUROPEAN_COINAGE_16							\
		PORT_DIPNAME( 0x0030,	0x0000, DEF_STR( "Coin_A") );	\
		PORT_DIPSETTING(		0x0020, DEF_STR( "2C_1C") );	\
		PORT_DIPSETTING(		0x0000, DEF_STR( "1C_1C") );	\
		PORT_DIPSETTING(		0x0030, DEF_STR( "2C_3C") );	\
		PORT_DIPSETTING(		0x0010, DEF_STR( "1C_2C") );	\
		PORT_DIPNAME( 0xc0,		0x0000, DEF_STR( "Coin_B") );	\
		PORT_DIPSETTING(		0x0080, DEF_STR( "2C_1C") );	\
		PORT_DIPSETTING(		0x0000, DEF_STR( "1C_1C") );	\
		PORT_DIPSETTING(		0x00c0, DEF_STR( "2C_3C") );	\
		PORT_DIPSETTING(		0x0040, DEF_STR( "1C_2C") );
	
	#define DIFFICULTY_8									\
		PORT_DIPNAME( 0x03,	0x00, DEF_STR( "Difficulty") );	\
		PORT_DIPSETTING(	0x01, "Easy" );				\
		PORT_DIPSETTING(	0x00, "Medium" );			\
		PORT_DIPSETTING(	0x02, "Hard" );				\
		PORT_DIPSETTING(	0x03, "Hardest" );
	
	#define LIVES_8										\
		PORT_DIPNAME( 0x30,	0x00, DEF_STR( "Lives") );	\
		PORT_DIPSETTING(	0x30, "1" );				\
		PORT_DIPSETTING(	0x20, "2" );				\
		PORT_DIPSETTING(	0x00, "3" );				\
		PORT_DIPSETTING(	0x10, "5" );
	
	#define DIFFICULTY_16										\
		PORT_DIPNAME( 0x0003,	0x0000, DEF_STR( "Difficulty") );	\
		PORT_DIPSETTING(		0x0001, "Easy" );			\
		PORT_DIPSETTING(		0x0000, "Medium" );			\
		PORT_DIPSETTING(		0x0002, "Hard" );			\
		PORT_DIPSETTING(		0x0003, "Hardest" );
	
	#define LIVES_16										\
		PORT_DIPNAME( 0x0030,	0x0000, DEF_STR( "Lives") );	\
		PORT_DIPSETTING(		0x0030, "1" );			\
		PORT_DIPSETTING(		0x0020, "2" );			\
		PORT_DIPSETTING(		0x0000, "3" );			\
		PORT_DIPSETTING(		0x0010, "5" );
	
	
	
	
	static InputPortHandlerPtr input_ports_tekipaki = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( tekipaki )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		DSWA_8
		EUROPEAN_COINAGE_8
	//	NONEUROPEAN_COINAGE_8
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_8
		PORT_DIPNAME( 0x04,	0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x08,	0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10,	0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20,	0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40,	0x00, "Game Mode" );
		PORT_DIPSETTING(	0x00, "Normal" );
		PORT_DIPSETTING(	0x40, "Stop" );
		PORT_DIPNAME( 0x80,	0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x80, DEF_STR( "On") );
	
		PORT_START(); 		/* (6) Territory Jumper block */
		PORT_DIPNAME( 0x0f,	0x02, "Territory" );
		PORT_DIPSETTING(	0x02, "Europe" );
		PORT_DIPSETTING(	0x01, "USA" );
		PORT_DIPSETTING(	0x00, "Japan" );
		PORT_DIPSETTING(	0x03, "Hong Kong" );
		PORT_DIPSETTING(	0x05, "Taiwan" );
		PORT_DIPSETTING(	0x04, "Korea" );
		PORT_DIPSETTING(	0x07, "USA (Romstar"));
		PORT_DIPSETTING(	0x08, "Hong Kong (Honest Trading Co."));
		PORT_DIPSETTING(	0x06, "Taiwan (Spacy Co. Ltd"));
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_ghox = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( ghox )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		DSWA_8
		EUROPEAN_COINAGE_8
	//	NONEUROPEAN_COINAGE_8
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_8
		PORT_DIPNAME( 0x0c,	0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(	0x00, "100k and every 200k" );
		PORT_DIPSETTING(	0x04, "100k and every 300k" );
		PORT_DIPSETTING(	0x08, "100k only" );
		PORT_DIPSETTING(	0x0c, "None" );
		LIVES_8
		PORT_BITX(	  0x40,	0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80,	0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x80, DEF_STR( "On") );
	
		PORT_START(); 		/* (6) Territory Jumper block */
		PORT_DIPNAME( 0x0f,	0x02, "Territory" );
		PORT_DIPSETTING(	0x02, "Europe" );
		PORT_DIPSETTING(	0x01, "USA" );
		PORT_DIPSETTING(	0x00, "Japan" );
		PORT_DIPSETTING(	0x04, "Korea" );
		PORT_DIPSETTING(	0x03, "Hong Kong (Honest Trading Co."));
		PORT_DIPSETTING(	0x05, "Taiwan" );
		PORT_DIPSETTING(	0x06, "Spain & Portugal (APM Electronics SA"));
		PORT_DIPSETTING(	0x07, "Italy (Star Electronica SRL"));
		PORT_DIPSETTING(	0x08, "UK (JP Leisure Ltd"));
		PORT_DIPSETTING(	0x0a, "Europe (Nova Apparate GMBH & Co"));
		PORT_DIPSETTING(	0x0d, "Europe (Taito Corporation Japan"));
		PORT_DIPSETTING(	0x09, "USA (Romstar"));
		PORT_DIPSETTING(	0x0b, "USA (Taito America Corporation"));
		PORT_DIPSETTING(	0x0c, "USA (Taito Corporation Japan"));
		PORT_DIPSETTING(	0x0e, "Japan (Taito Corporation"));
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (7)  Paddle 1 (left-right)  read at $100000 */
		PORT_ANALOG( 0xff,	0x00, IPT_DIAL | IPF_PLAYER1, 25, 15, 0, 0xff );
	
		PORT_START(); 		/* (8)  Paddle 2 (left-right)  read at $040000 */
		PORT_ANALOG( 0xff,	0x00, IPT_DIAL | IPF_PLAYER2, 25, 15, 0, 0xff );
	
		PORT_START(); 		/* (9)  Paddle 1 (fake up-down) */
		PORT_ANALOG( 0xff,	0x00, IPT_DIAL_V | IPF_PLAYER1, 15, 0, 0, 0xff );
	
		PORT_START(); 		/* (10) Paddle 2 (fake up-down) */
		PORT_ANALOG( 0xff,	0x00, IPT_DIAL_V | IPF_PLAYER2, 15, 0, 0, 0xff );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_dogyuun = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( dogyuun )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_BUTTON3, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_BUTTON3, IPT_UNKNOWN )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		PORT_START(); 		/* (4) DSWA */
		PORT_DIPNAME( 0x0001,	0x0000, DEF_STR( "Free_Play"));
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0001, DEF_STR( "On") );
		PORT_DIPNAME( 0x0002,	0x0000, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0002, DEF_STR( "On") );
		PORT_SERVICE( 0x0004,	IP_ACTIVE_HIGH );	/* Service Mode */
		PORT_DIPNAME( 0x0008,	0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		EUROPEAN_COINAGE_16
	//	NONEUROPEAN_COINAGE_16
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_16
		PORT_DIPNAME( 0x000c,	0x0000, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(		0x0004, "200k, 400k and 600k" );
		PORT_DIPSETTING(		0x0000, "200k only" );
		PORT_DIPSETTING(		0x0008, "400k only" );
		PORT_DIPSETTING(		0x000c, "None" );
		LIVES_16
		PORT_BITX(	  0x0040,	0x0000, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0040, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080,	0x0000, "Allow Continue" );
		PORT_DIPSETTING(		0x0080, DEF_STR( "No") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Yes") );
	
		PORT_START(); 		/* (6) Territory Jumper block */
		PORT_DIPNAME( 0x0f,	0x03, "Territory" );
		PORT_DIPSETTING(	0x03, "Europe" );
		PORT_DIPSETTING(	0x01, "USA" );
		PORT_DIPSETTING(	0x00, "Japan" );
		PORT_DIPSETTING(	0x05, "Korea (Unite Trading license"));
		PORT_DIPSETTING(	0x04, "Hong Kong (Charterfield license"));
		PORT_DIPSETTING(	0x06, "Taiwan" );
		PORT_DIPSETTING(	0x08, "South East Asia (Charterfield license"));
		PORT_DIPSETTING(	0x0c, "USA (Atari Games Corp license"));
		PORT_DIPSETTING(	0x0f, "Japan (Taito Corp license"));
	/*	Duplicate settings
		PORT_DIPSETTING(	0x0b, "Europe" );
		PORT_DIPSETTING(	0x07, "USA" );
		PORT_DIPSETTING(	0x0a, "Korea (Unite Trading license"));
		PORT_DIPSETTING(	0x09, "Hong Kong (Charterfield license"));
		PORT_DIPSETTING(	0x0b, "Taiwan" );
		PORT_DIPSETTING(	0x0d, "South East Asia (Charterfield license"));
		PORT_DIPSETTING(	0x0c, "USA (Atari Games Corp license"));
	*/
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* bit 0x10 sound ready */
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_kbash = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( kbash )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_BUTTON3, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_BUTTON3, IPT_UNKNOWN )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		PORT_START(); 		/* (4) DSWA */
		PORT_DIPNAME( 0x0001,	0x0000, "Continue Mode" );
		PORT_DIPSETTING(		0x0000, "Normal" );
		PORT_DIPSETTING(		0x0001, "Discount" );
		PORT_DIPNAME( 0x0002,	0x0000, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0002, DEF_STR( "On") );
		PORT_SERVICE( 0x0004,	IP_ACTIVE_HIGH );	/* Service Mode */
		PORT_DIPNAME( 0x0008,	0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		EUROPEAN_COINAGE_16
	//	NONEUROPEAN_COINAGE_16
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_16
		PORT_DIPNAME( 0x000c,	0x0000, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(		0x0000, "100k and every 400k" );
		PORT_DIPSETTING(		0x0004, "100k only" );
		PORT_DIPSETTING(		0x0008, "200k only" );
		PORT_DIPSETTING(		0x000c, "None" );
		/* Lives are different in this game */
		PORT_DIPNAME( 0x0030,	0x0000, DEF_STR( "Lives") );
		PORT_DIPSETTING(		0x0030, "1" );
		PORT_DIPSETTING(		0x0000, "2" );
		PORT_DIPSETTING(		0x0020, "3" );
		PORT_DIPSETTING(		0x0010, "4" );
		PORT_BITX(	  0x0040,	0x0000, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0040, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080,	0x0000, "Allow Continue" );
		PORT_DIPSETTING(		0x0080, DEF_STR( "No") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Yes") );
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (6) Territory Jumper block */
		PORT_DIPNAME( 0x000f,	0x000a, "Territory" );
		PORT_DIPSETTING(		0x000a, "Europe" );
		PORT_DIPSETTING(		0x0009, "USA" );
		PORT_DIPSETTING(		0x0000, "Japan" );
		PORT_DIPSETTING(		0x0003, "Korea" );
		PORT_DIPSETTING(		0x0004, "Hong Kong" );
		PORT_DIPSETTING(		0x0007, "Taiwan" );
		PORT_DIPSETTING(		0x0006, "South East Asia" );
		PORT_DIPSETTING(		0x0002, "Europe, USA (Atari License"));
		PORT_DIPSETTING(		0x0001, "USA, Europe (Atari License"));
		PORT_BIT( 0xfff0, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_truxton2 = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( truxton2 )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_UNKNOWN, IPT_BUTTON4 )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_UNKNOWN, IPT_BUTTON4 )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		PORT_START(); 		/* (4) DSWA */
		PORT_DIPNAME( 0x0001,	0x0000, "Rapid Fire" );
		PORT_DIPSETTING(		0x0001, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0002,	0x0000, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0002, DEF_STR( "On") );
		PORT_SERVICE( 0x0004,	IP_ACTIVE_HIGH );	/* Service Mode */
		PORT_DIPNAME( 0x0008,	0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		EUROPEAN_COINAGE_16
	//	NONEUROPEAN_COINAGE_16
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_16
		PORT_DIPNAME( 0x000c,	0x0000, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(		0x0000, "70k and 200k" );
		PORT_DIPSETTING(		0x0004, "100k and 250k" );
		PORT_DIPSETTING(		0x0008, "100k only" );
		PORT_DIPSETTING(		0x000c, "200k only" );
		/* Lives are different in this game */
		PORT_DIPNAME( 0x0030,	0x0000, DEF_STR( "Lives") );
		PORT_DIPSETTING(		0x0030, "2" );
		PORT_DIPSETTING(		0x0000, "3" );
		PORT_DIPSETTING(		0x0020, "4" );
		PORT_DIPSETTING(		0x0010, "5" );
		PORT_BITX(	  0x0040,	0x0000, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0040, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080,	0x0000, "Allow Continue" );
		PORT_DIPSETTING(		0x0080, DEF_STR( "No") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Yes") );
	
		PORT_START(); 		/* (6) Territory Jumper block */
		PORT_DIPNAME( 0x07,	0x02, "Territory" );
		PORT_DIPSETTING(	0x02, "Europe" );
		PORT_DIPSETTING(	0x01, "USA" );
		PORT_DIPSETTING(	0x00, "Japan" );
		PORT_DIPSETTING(	0x03, "Hong Kong" );
		PORT_DIPSETTING(	0x05, "Taiwan" );
		PORT_DIPSETTING(	0x06, "Asia" );
		PORT_DIPSETTING(	0x04, "Korea" );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_pipibibs = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( pipibibs )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		DSWA_8
		EUROPEAN_COINAGE_8
	//	NONEUROPEAN_COINAGE_8
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_8
		PORT_DIPNAME( 0x0c,	0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(	0x04, "150k and every 200k" );
		PORT_DIPSETTING(	0x00, "200k and every 300k" );
		PORT_DIPSETTING(	0x08, "200k only" );
		PORT_DIPSETTING(	0x0c, "None" );
		LIVES_8
		PORT_BITX(	  0x40,	0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80,	0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x80, DEF_STR( "On") );
	
		PORT_START(); 		/* (6) Territory Jumper block */
		PORT_DIPNAME( 0x07,	0x06, "Territory" );
		PORT_DIPSETTING(	0x06, "Europe" );
		PORT_DIPSETTING(	0x04, "USA" );
		PORT_DIPSETTING(	0x00, "Japan" );
		PORT_DIPSETTING(	0x02, "Hong Kong (Honest Trading Co."));
		PORT_DIPSETTING(	0x03, "Taiwan" );
		PORT_DIPSETTING(	0x01, "Asia" );
		PORT_DIPSETTING(	0x07, "Europe (Nova Apparate GMBH & Co"));
		PORT_DIPSETTING(	0x05, "USA (Romstar"));
		PORT_DIPNAME( 0x08,	0x00, "Nudity" );
		PORT_DIPSETTING(	0x08, "Low" );
		PORT_DIPSETTING(	0x00, "High, but censored" );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_whoopee = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( whoopee )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		DSWA_8
	//	EUROPEAN_COINAGE_8
		NONEUROPEAN_COINAGE_8
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_8
		PORT_DIPNAME( 0x0c,	0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(	0x04, "150k and every 200k" );
		PORT_DIPSETTING(	0x00, "200k and every 300k" );
		PORT_DIPSETTING(	0x08, "200k only" );
		PORT_DIPSETTING(	0x0c, "None" );
		LIVES_8
		PORT_BITX(	  0x40,	0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80,	0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x80, DEF_STR( "On") );
	
		PORT_START(); 		/* (6) Territory Jumper block */
		PORT_DIPNAME( 0x07,	0x00, "Territory" );
		PORT_DIPSETTING(	0x06, "Europe" );
		PORT_DIPSETTING(	0x04, "USA" );
		PORT_DIPSETTING(	0x00, "Japan" );
		PORT_DIPSETTING(	0x02, "Hong Kong (Honest Trading Co."));
		PORT_DIPSETTING(	0x03, "Taiwan" );
		PORT_DIPSETTING(	0x01, "Asia" );
		PORT_DIPSETTING(	0x07, "Europe (Nova Apparate GMBH & Co"));
		PORT_DIPSETTING(	0x05, "USA (Romstar"));
		PORT_DIPNAME( 0x08,	0x08, "Nudity" );
		PORT_DIPSETTING(	0x08, "Low" );
		PORT_DIPSETTING(	0x00, "High, but censored" );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* bit 0x10 sound ready */
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_pipibibi = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( pipibibi )
		PORT_START(); 		/* (0) VBlank */
	//	PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );	/* This video HW */
	//	PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );	/* doesnt wait for VBlank */
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		PORT_START(); 		/* (4) DSWA */
		PORT_DIPNAME( 0x01,	0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x01, DEF_STR( "On") );
		/* This video HW doesn't support flip screen */
	//	PORT_DIPNAME( 0x02,	0x00, DEF_STR( "Flip_Screen") );
	//	PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
	//	PORT_DIPSETTING(	0x02, DEF_STR( "On") );
		PORT_SERVICE( 0x04,	IP_ACTIVE_HIGH );	/* Service Mode */
		PORT_DIPNAME( 0x08,	0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(	0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
	//	EUROPEAN_COINAGE_8
		NONEUROPEAN_COINAGE_8
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_8
		PORT_DIPNAME( 0x0c,	0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(	0x04, "150k and every 200k" );
		PORT_DIPSETTING(	0x00, "200k and every 300k" );
		PORT_DIPSETTING(	0x08, "200k only" );
		PORT_DIPSETTING(	0x0c, "None" );
		LIVES_8
		PORT_BITX(	  0x40,	0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80,	0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(	0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x80, DEF_STR( "On") );
	
		PORT_START(); 		/* (6) Territory Jumper block */
		PORT_DIPNAME( 0x07,	0x05, "Territory" );
		PORT_DIPSETTING(	0x07, "World (Ryouta Kikaku"));
		PORT_DIPSETTING(	0x00, "Japan (Ryouta Kikaku"));
		PORT_DIPSETTING(	0x02, "World" );
		PORT_DIPSETTING(	0x05, "Europe" );
		PORT_DIPSETTING(	0x04, "USA" );
		PORT_DIPSETTING(	0x01, "Hong Kong (Honest Trading Co."));
		PORT_DIPSETTING(	0x06, "Spain & Portugal (APM Electronics SA"));
	//	PORT_DIPSETTING(	0x03, "World" );
		PORT_DIPNAME( 0x08,	0x00, "Nudity" );
		PORT_DIPSETTING(	0x08, "Low" );
		PORT_DIPSETTING(	0x00, "High, but censored" );
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_fixeight = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( fixeight )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_UNKNOWN, IPT_UNKNOWN )
	
		SNOWBRO2_PLAYER_INPUT( IPF_PLAYER3, IPT_START3, IPT_UNKNOWN )
	
		PORT_START(); 		/* service input is a push-button marked 'Test SW' */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_COIN3 );
		PORT_BIT( 0x0002, IP_ACTIVE_HIGH, IPT_TILT );
		PORT_BITX(0x0004, IP_ACTIVE_HIGH, IPT_SERVICE, DEF_STR( "Service_Mode") ); KEYCODE_F2, IP_JOY_NONE )
		PORT_BIT( 0x0008, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x0010, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x0020, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x0040, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0xff80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
	#if 0
		PORT_START(); 		/* Fake input, to display message */
		PORT_DIPNAME( 0x00,	0x00, "    Press service button" );
		PORT_DIPSETTING(	0x00, "" );
		PORT_DIPNAME( 0x00,	0x00, "  for game keeping options" );
		PORT_DIPSETTING(	0x00, "" );
		PORT_DIPNAME( 0x00,	0x00, "" );
		PORT_DIPSETTING(	0x00, "" );
	#endif
	
		PORT_START(); 		/* (4) DSWA */
		PORT_DIPNAME( 0x0001,	0x0000, "Maximum Players" );
		PORT_DIPSETTING(		0x0000, "2" );
		PORT_DIPSETTING(		0x0001, "3" );
		PORT_DIPNAME( 0x0002,	0x0000, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0002, DEF_STR( "On") );
		PORT_DIPNAME( 0x0004,	0x0004, "Shooting style" );
		PORT_DIPSETTING(		0x0004, "Semi-auto" );
		PORT_DIPSETTING(		0x0000, "Fully-auto" );
		PORT_DIPNAME( 0x0008,	0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		EUROPEAN_COINAGE_16
	//	NONEUROPEAN_COINAGE_16
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_16
		PORT_DIPNAME( 0x000c,	0x0000, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(		0x0004, "300k and every 300k" );
		PORT_DIPSETTING(		0x0008, "300k only" );
		PORT_DIPSETTING(		0x0000, "500k and every 500k" );
		PORT_DIPSETTING(		0x000c, "None" );
		LIVES_16
		PORT_BITX(	  0x0040,	0x0000, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0040, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080,	0x0000, "Allow Continue" );
		PORT_DIPSETTING(		0x0080, DEF_STR( "No") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Yes") );
	
		PORT_START(); 		/* (6) Territory Jumper block */
		PORT_DIPNAME( 0x0f,	0x09, "Territory" );
		PORT_DIPSETTING(	0x09, "Europe" );
		PORT_DIPSETTING(	0x08, "Europe (Taito Corp"));
		PORT_DIPSETTING(	0x0b, "USA" );
		PORT_DIPSETTING(	0x0a, "USA (Taito America Corp"));
		PORT_DIPSETTING(	0x0e, "Japan" );
		PORT_DIPSETTING(	0x0f, "Japan (Taito corp"));
		PORT_DIPSETTING(	0x01, "Korea" );
		PORT_DIPSETTING(	0x00, "Korea (Taito Corp"));
		PORT_DIPSETTING(	0x03, "Hong Kong" );
		PORT_DIPSETTING(	0x02, "Hong Kong (Taito Corp"));
		PORT_DIPSETTING(	0x05, "Taiwan" );
		PORT_DIPSETTING(	0x04, "Taiwan (Taito corp"));
		PORT_DIPSETTING(	0x07, "South East Asia" );
		PORT_DIPSETTING(	0x06, "South East Asia (Taito corp"));
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_grindstm = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( grindstm )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		PORT_START(); 		/* (4) DSWA */
		PORT_DIPNAME( 0x0001,	0x0000, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Upright") );
		PORT_DIPSETTING(		0x0001, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x0002,	0x0000, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0002, DEF_STR( "On") );
		PORT_SERVICE( 0x0004,	IP_ACTIVE_HIGH );	/* Service Mode */
		PORT_DIPNAME( 0x0008,	0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		EUROPEAN_COINAGE_16
	//	NONEUROPEAN_COINAGE_16
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_16
		PORT_DIPNAME( 0x000c,	0x0000, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(		0x0008, "200k only" );
		PORT_DIPSETTING(		0x0004, "300k and every 800k" );
		PORT_DIPSETTING(		0x0000, "300k and 800k" );
		PORT_DIPSETTING(		0x000c, "None" );
		LIVES_16
		PORT_BITX(	  0x0040,	0x0000, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0040, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080,	0x0000, "Allow Continue" );
		PORT_DIPSETTING(		0x0080, DEF_STR( "No") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Yes") );
	
		PORT_START(); 		/* (6) Territory Jumper block */
		PORT_DIPNAME( 0x0f,	0x08, "Territory" );
		PORT_DIPSETTING(	0x08, "Europe" );
		PORT_DIPSETTING(	0x0b, "USA" );
		PORT_DIPSETTING(	0x01, "Korea" );
		PORT_DIPSETTING(	0x03, "Hong Kong" );
		PORT_DIPSETTING(	0x05, "Taiwan" );
		PORT_DIPSETTING(	0x07, "South East Asia" );
		PORT_DIPSETTING(	0x0a, "USA (American Sammy Corporation License"));
		PORT_DIPSETTING(	0x00, "Korea (Unite Trading License"));
		PORT_DIPSETTING(	0x02, "Hong Kong (Charterfield License"));
		PORT_DIPSETTING(	0x04, "Taiwan (Anomoto International Inc License"));
		PORT_DIPSETTING(	0x06, "South East Asia (Charterfield License"));
	/*	Duplicate settings
		PORT_DIPSETTING(	0x09, "Europe" );
		PORT_DIPSETTING(	0x0d, "USA" );
		PORT_DIPSETTING(	0x0e, "Korea" );
		PORT_DIPSETTING(	0x0f, "Korea" );
		PORT_DIPSETTING(	0x0c, "USA (American Sammy Corporation License"));
	*/
		PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* bit 0x10 sound ready */
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_vfive = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( vfive )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		PORT_START(); 		/* (4) DSWA */
		PORT_DIPNAME( 0x0001,	0x0000, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Upright") );
		PORT_DIPSETTING(		0x0001, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x0002,	0x0000, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0002, DEF_STR( "On") );
		PORT_SERVICE( 0x0004,	IP_ACTIVE_HIGH );	/* Service Mode */
		PORT_DIPNAME( 0x0008,	0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		NONEUROPEAN_COINAGE_16
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_16
		PORT_DIPNAME( 0x000c,	0x0000, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(		0x0008, "200k only" );
		PORT_DIPSETTING(		0x0004, "300k and every 800k" );
		PORT_DIPSETTING(		0x0000, "300k and 800k" );
		PORT_DIPSETTING(		0x000c, "None" );
		LIVES_16
		PORT_BITX(	  0x0040,	0x0000, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0040, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080,	0x0000, "Allow Continue" );
		PORT_DIPSETTING(		0x0080, DEF_STR( "No") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Yes") );
	
		PORT_START(); 		/* (6) Territory Jumper block */
		/* Territory is forced to Japan in this set. */
		PORT_BIT( 0xff, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* bit 0x10 sound ready */
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_batsugun = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( batsugun )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		PORT_START(); 		/* (4) DSWA */
		PORT_DIPNAME( 0x0001,	0x0000, "Continue Mode" );
		PORT_DIPSETTING(		0x0000, "Normal" );
		PORT_DIPSETTING(		0x0001, "Discount" );
		PORT_DIPNAME( 0x0002,	0x0000, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0002, DEF_STR( "On") );
		PORT_SERVICE( 0x0004,	IP_ACTIVE_HIGH );	/* Service Mode */
		PORT_DIPNAME( 0x0008,	0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		EUROPEAN_COINAGE_16
	//	NONEUROPEAN_COINAGE_16
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_16
		PORT_DIPNAME( 0x000c,	0x0000, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(		0x0004, "500k and every 600k" );
		PORT_DIPSETTING(		0x0000, "1000k only" );
		PORT_DIPSETTING(		0x0008, "1500k only" );
		PORT_DIPSETTING(		0x000c, "None" );
		LIVES_16
		PORT_BITX(	  0x0040,	0x0000, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0040, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080,	0x0000, "Allow Continue" );
		PORT_DIPSETTING(		0x0080, DEF_STR( "No") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Yes") );
	
		PORT_START(); 		/* (6) Territory Jumper block */
		PORT_DIPNAME( 0x000f,	0x0009, "Territory" );
		PORT_DIPSETTING(		0x0009, "Europe" );
		PORT_DIPSETTING(		0x000b, "USA" );
		PORT_DIPSETTING(		0x000e, "Japan" );
	//	PORT_DIPSETTING(		0x000f, "Japan" );
		PORT_DIPSETTING(		0x0001, "Korea" );
		PORT_DIPSETTING(		0x0003, "Hong Kong" );
		PORT_DIPSETTING(		0x0005, "Taiwan" );
		PORT_DIPSETTING(		0x0007, "South East Asia" );
		PORT_DIPSETTING(		0x0008, "Europe (Taito Corp License"));
		PORT_DIPSETTING(		0x000a, "USA (Taito Corp License"));
		PORT_DIPSETTING(		0x000c, "Japan (Taito Corp License"));
	//	PORT_DIPSETTING(		0x000d, "Japan (Taito Corp License"));
		PORT_DIPSETTING(		0x0000, "Korea (Unite Trading License"));
		PORT_DIPSETTING(		0x0002, "Hong Kong (Taito Corp License"));
		PORT_DIPSETTING(		0x0004, "Taiwan (Taito Corp License"));
		PORT_DIPSETTING(		0x0006, "South East Asia (Taito Corp License"));
		PORT_BIT( 0xfff0, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* bit 0x10 sound ready */
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_snowbro2 = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( snowbro2 )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_UNKNOWN, IPT_UNKNOWN )
	
		SNOWBRO2_PLAYER_INPUT( IPF_PLAYER3, IPT_START3, IPT_UNKNOWN )
	
		SNOWBRO2_PLAYER_INPUT( IPF_PLAYER4, IPT_START4, IPT_UNKNOWN )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		PORT_START(); 		/* (6) DSWA */
		PORT_DIPNAME( 0x0001,	0x0000, "Continue Mode" );
		PORT_DIPSETTING(		0x0000, "Normal" );
		PORT_DIPSETTING(		0x0001, "Discount" );
		PORT_DIPNAME( 0x0002,	0x0000, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0002, DEF_STR( "On") );
		PORT_SERVICE( 0x0004,	IP_ACTIVE_HIGH );	/* Service Mode */
		PORT_DIPNAME( 0x0008,	0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		NONEUROPEAN_COINAGE_16
		/*  The following are listed in service mode for European territory,
			but are not actually used in game play. */
	//	EUROPEAN_COINAGE_16
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (7) DSWB */
		DIFFICULTY_16
		PORT_DIPNAME( 0x000c,	0x0000, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(		0x0004, "100k and every 500k" );
		PORT_DIPSETTING(		0x0000, "100k only" );
		PORT_DIPSETTING(		0x0008, "200k only" );
		PORT_DIPSETTING(		0x000c, "None" );
		/* Lives have one different value */
		PORT_DIPNAME( 0x0030,	0x0000, DEF_STR( "Lives") );
		PORT_DIPSETTING(		0x0030, "1" );
		PORT_DIPSETTING(		0x0020, "2" );
		PORT_DIPSETTING(		0x0000, "3" );
		PORT_DIPSETTING(		0x0010, "4" );
		PORT_BITX(	  0x0040,	0x0000, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0040, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080,	0x0000, "Maximum Players" );
		PORT_DIPSETTING(		0x0080, "2" );
		PORT_DIPSETTING(		0x0000, "4" );
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (8) Territory Jumper block */
		PORT_DIPNAME( 0x1c00,	0x0800, "Territory" );
		PORT_DIPSETTING(		0x0800, "Europe" );
		PORT_DIPSETTING(		0x0400, "USA" );
		PORT_DIPSETTING(		0x0000, "Japan" );
		PORT_DIPSETTING(		0x0c00, "Korea" );
		PORT_DIPSETTING(		0x1000, "Hong Kong" );
		PORT_DIPSETTING(		0x1400, "Taiwan" );
		PORT_DIPSETTING(		0x1800, "South East Asia" );
		PORT_DIPSETTING(		0x1c00, DEF_STR( "Unused") );
		PORT_DIPNAME( 0x2000,	0x0000, "Show All Rights Reserved" );
		PORT_DIPSETTING(		0x0000, DEF_STR( "No") );
		PORT_DIPSETTING(		0x2000, DEF_STR( "Yes") );
		PORT_BIT( 0xc3ff, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_sstriker = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( sstriker )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		PORT_START(); 		/* (4) DSWA */
		PORT_DIPNAME( 0x0001,	0x0000, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0001, DEF_STR( "On") );
		PORT_DIPNAME( 0x0002,	0x0000, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0002, DEF_STR( "On") );
		PORT_SERVICE( 0x0004,	IP_ACTIVE_HIGH );	/* Service Mode */
		PORT_DIPNAME( 0x0008,	0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		NONEUROPEAN_COINAGE_16
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_16
		PORT_DIPNAME( 0x000c,	0x0000, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(		0x0004, "200k and 500k" );
		PORT_DIPSETTING(		0x0000, "Every 300k" );
		PORT_DIPSETTING(		0x0008, "200k only" );
		PORT_DIPSETTING(		0x000c, "None" );
		LIVES_16
		PORT_BITX(	  0x0040,	0x0000, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0040, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080,	0x0000, "Allow Continue" );
		PORT_DIPSETTING(		0x0080, DEF_STR( "No") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Yes") );
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (6) Territory Jumper block */
		PORT_DIPNAME( 0x000e,	0x0004, "Territory" );
		PORT_DIPSETTING(		0x0004, "Europe" );
		PORT_DIPSETTING(		0x0002, "USA" );
		PORT_DIPSETTING(		0x0000, "Japan" );
		PORT_DIPSETTING(		0x0006, "South East Asia" );
		PORT_DIPSETTING(		0x0008, "China" );
		PORT_DIPSETTING(		0x000a, "Korea" );
		PORT_DIPSETTING(		0x000c, "Hong Kong" );
		PORT_DIPSETTING(		0x000e, "Taiwan" );
	
		PORT_BIT( 0xfff1, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_mahoudai = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( mahoudai )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		PORT_START(); 		/* (4) DSWA */
		PORT_DIPNAME( 0x0001,	0x0000, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0001, DEF_STR( "On") );
		PORT_DIPNAME( 0x0002,	0x0000, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0002, DEF_STR( "On") );
		PORT_SERVICE( 0x0004,	IP_ACTIVE_HIGH );	/* Service Mode */
		PORT_DIPNAME( 0x0008,	0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		NONEUROPEAN_COINAGE_16
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_16
		PORT_DIPNAME( 0x000c,	0x0000, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(		0x0004, "200k and 500k" );
		PORT_DIPSETTING(		0x0000, "Every 300k" );
		PORT_DIPSETTING(		0x0008, "200k only" );
		PORT_DIPSETTING(		0x000c, "None" );
		LIVES_16
		PORT_BITX(	  0x0040,	0x0000, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0040, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080,	0x0000, "Allow Continue" );
		PORT_DIPSETTING(		0x0080, DEF_STR( "No") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Yes") );
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (6) Territory Jumper block */
		/* not used, it seems. This setting forces Japan for Territory */
		PORT_BIT( 0xffff, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_kingdmgp = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( kingdmgp )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		PORT_START(); 		/* (4) DSWA */
		PORT_DIPNAME( 0x0001,	0x0000, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0001, DEF_STR( "On") );
		PORT_DIPNAME( 0x0002,	0x0000, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0002, DEF_STR( "On") );
		PORT_SERVICE( 0x0004,	IP_ACTIVE_HIGH );	/* Service Mode */
		PORT_DIPNAME( 0x0008,	0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		NONEUROPEAN_COINAGE_16
		/*  When Territory is set to Europe, the Coin A and B have
			different values */
	//	EUROPEAN_COINAGE_16
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_16
		PORT_DIPNAME( 0x000c,	0x0000, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(		0x0004, "200k and 500k" );
		PORT_DIPSETTING(		0x0000, "Every 300k" );
		PORT_DIPSETTING(		0x0008, "200k only" );
		PORT_DIPSETTING(		0x000c, "None" );
		LIVES_16
		PORT_BITX(	  0x0040,	0x0000, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0040, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080,	0x0000, "Allow Continue" );
		PORT_DIPSETTING(		0x0080, DEF_STR( "No") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Yes") );
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (6) Territory Jumper block */
		/* Title screen is wrong when set to Japan for Territory */
		PORT_DIPNAME( 0x000e,	0x0004, "Territory" );
		PORT_DIPSETTING(		0x0004, "Europe" );
		PORT_DIPSETTING(		0x0002, "USA" );
	//	PORT_DIPSETTING(		0x0000, "Japan" );
		PORT_DIPSETTING(		0x0006, "South East Asia" );
		PORT_DIPSETTING(		0x0008, "China" );
		PORT_DIPSETTING(		0x000a, "Korea" );
		PORT_DIPSETTING(		0x000c, "Hong Kong" );
		PORT_DIPSETTING(		0x000e, "Taiwan" );
		PORT_BIT( 0xfff1, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_shippumd = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( shippumd )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_UNKNOWN, IPT_UNKNOWN )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		PORT_START(); 		/* (4) DSWA */
		PORT_DIPNAME( 0x0001,	0x0000, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0001, DEF_STR( "On") );
		PORT_DIPNAME( 0x0002,	0x0000, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0002, DEF_STR( "On") );
		PORT_SERVICE( 0x0004,	IP_ACTIVE_HIGH );	/* Service Mode */
		PORT_DIPNAME( 0x0008,	0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		NONEUROPEAN_COINAGE_16
		/*  When Territory is set to Europe, the Coin A and B have
			different values */
	//	EUROPEAN_COINAGE_16
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_16
		PORT_DIPNAME( 0x000c,	0x0000, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(		0x0004, "200k and 500k" );
		PORT_DIPSETTING(		0x0000, "Every 300k" );
		PORT_DIPSETTING(		0x0008, "200k only" );
		PORT_DIPSETTING(		0x000c, "None" );
		LIVES_16
		PORT_BITX(	  0x0040,	0x0000, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0040, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080,	0x0000, "Allow Continue" );
		PORT_DIPSETTING(		0x0080, DEF_STR( "No") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Yes") );
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (6) Territory Jumper block */
		 /* Title screen is corrupt for anything but Japan setting so this forces it to Japan */
		PORT_BIT( 0xffff, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_battleg = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( battleg )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_BUTTON3, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_BUTTON3, IPT_UNKNOWN )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		PORT_START(); 		/* (4) DSWA */
		PORT_SERVICE( 0x0001,	IP_ACTIVE_HIGH );	/* Service Mode */
		PORT_DIPNAME( 0x0002,	0x0000, "Credits to Start" );
		PORT_DIPSETTING(		0x0000, "1" );
		PORT_DIPSETTING(		0x0002, "2" );
		PORT_DIPNAME( 0x001c,	0x0000, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(		0x0018, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(		0x0014, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(		0x0010, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(		0x0004, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(		0x000c, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(		0x001c, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x00e0,	0x0000, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(		0x00c0, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(		0x00a0, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(		0x0080, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(		0x0020, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(		0x0040, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(		0x0060, DEF_STR( "1C_4C") );
	//	PORT_DIPSETTING(		0x00e0, DEF_STR( "1C_1C") );
		/*  When Coin_A is set to Free_Play, Coin_A becomes Coin_A and Coin_B,
			and Coin_B becomes the following dips */
	//	PORT_DIPNAME( 0x0020,	0x0000, "Joystick Mode" );
	//	PORT_DIPSETTING(		0x0000, "90 degrees ACW" );
	//	PORT_DIPSETTING(		0x0020, "Normal" );
	//	PORT_DIPNAME( 0x0040,	0x0000, "Effect" );
	//	PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
	//	PORT_DIPSETTING(		0x0040, DEF_STR( "On") );
	//	PORT_DIPNAME( 0x0080,	0x0000, "Music" );
	//	PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
	//	PORT_DIPSETTING(		0x0080, DEF_STR( "On") );
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_16
		PORT_DIPNAME( 0x0004,	0x0000, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0004, DEF_STR( "On") );
		PORT_DIPNAME( 0x0008,	0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0070,	0x0000, DEF_STR( "Lives") );
		PORT_DIPSETTING(		0x0030, "1" );
		PORT_DIPSETTING(		0x0020, "2" );
		PORT_DIPSETTING(		0x0000, "3" );
		PORT_DIPSETTING(		0x0010, "4" );
		PORT_DIPSETTING(		0x0040, "5" );
		PORT_DIPSETTING(		0x0050, "6" );
		PORT_BITX( 0,			0x0060, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Infinite", 0, 0 );
	//	PORT_BITX( 0,			0x0070, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Invulnerability", 0, 0 );
		PORT_DIPNAME( 0x0080,	0x0000, DEF_STR( "Bonus_Life") );
		/* Bonus_Life for Non European territories */
	//	PORT_DIPSETTING(		0x0000, "Every 1000k" );
	//	PORT_DIPSETTING(		0x0080, "1000k and 2000k" );
		/* Bonus_Life values for European territories */
		PORT_DIPSETTING(		0x0080, "Every 2000k" );
		PORT_DIPSETTING(		0x0000, "None" );
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (6) DSWC / Territory Jumper block */
		PORT_DIPNAME( 0x0004,	0x0000, "Allow Continue" );
		PORT_DIPSETTING(		0x0004, DEF_STR( "No") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x0008,	0x0000, "Stage Edit" );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "On") );
		PORT_DIPNAME( 0x0003,	0x0001, "Territory" );
		PORT_DIPSETTING(		0x0001, "Denmark (German Tuning license"));
		/* These Settings End Up Reporting ROM-0 as BAD */
	//	PORT_DIPSETTING(		0x0002, "USA (Fabtek license"));
	//	PORT_DIPSETTING(		0x0000, "Japan" );
		PORT_DIPSETTING(		0x0003, "China" );
		PORT_BIT( 0xfff0, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_battlega = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( battlega )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_BUTTON3, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_BUTTON3, IPT_UNKNOWN )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		PORT_START(); 		/* (4) DSWA */
		PORT_SERVICE( 0x0001,	IP_ACTIVE_HIGH );	/* Service Mode */
		PORT_DIPNAME( 0x0002,	0x0000, "Credits to Start" );
		PORT_DIPSETTING(		0x0000, "1" );
		PORT_DIPSETTING(		0x0002, "2" );
		PORT_DIPNAME( 0x001c,	0x0000, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(		0x0018, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(		0x0014, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(		0x0010, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(		0x0004, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(		0x000c, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(		0x001c, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x00e0,	0x0000, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(		0x00c0, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(		0x00a0, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(		0x0080, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(		0x0020, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(		0x0040, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(		0x0060, DEF_STR( "1C_4C") );
	//	PORT_DIPSETTING(		0x00e0, DEF_STR( "1C_1C") );
		/*  When Coin_A is set to Free_Play, Coin_A becomes Coin_A and Coin_B,
			and Coin_B becomes the following dips */
	//	PORT_DIPNAME( 0x0020,	0x0000, "Joystick Mode" );
	//	PORT_DIPSETTING(		0x0000, "90 degrees ACW" );
	//	PORT_DIPSETTING(		0x0020, "Normal" );
	//	PORT_DIPNAME( 0x0040,	0x0000, "Effect" );
	//	PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
	//	PORT_DIPSETTING(		0x0040, DEF_STR( "On") );
	//	PORT_DIPNAME( 0x0080,	0x0000, "Music" );
	//	PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
	//	PORT_DIPSETTING(		0x0080, DEF_STR( "On") );
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_16
		PORT_DIPNAME( 0x0004,	0x0000, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0004, DEF_STR( "On") );
		PORT_DIPNAME( 0x0008,	0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0070,	0x0000, DEF_STR( "Lives") );
		PORT_DIPSETTING(		0x0030, "1" );
		PORT_DIPSETTING(		0x0020, "2" );
		PORT_DIPSETTING(		0x0000, "3" );
		PORT_DIPSETTING(		0x0010, "4" );
		PORT_DIPSETTING(		0x0040, "5" );
		PORT_DIPSETTING(		0x0050, "6" );
		PORT_BITX( 0,			0x0060, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Infinite", 0, 0 );
	//	PORT_BITX( 0,			0x0070, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Invulnerability", 0, 0 );
		PORT_DIPNAME( 0x0080,	0x0000, DEF_STR( "Bonus_Life") );
		/* Bonus_Life for Non European territories */
	//	PORT_DIPSETTING(		0x0000, "Every 1000k" );
	//	PORT_DIPSETTING(		0x0080, "1000k and 2000k" );
		/* Bonus_Life values for European territories */
		PORT_DIPSETTING(		0x0080, "Every 2000k" );
		PORT_DIPSETTING(		0x0000, "None" );
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (6) DSWC / Territory Jumper block */
		PORT_DIPNAME( 0x0004,	0x0000, "Allow Continue" );
		PORT_DIPSETTING(		0x0004, DEF_STR( "No") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x0008,	0x0000, "Stage Edit" );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "On") );
		PORT_DIPNAME( 0x0003,	0x0001, "Territory" );
		PORT_DIPSETTING(		0x0001, "Europe (German Tuning license"));
		PORT_DIPSETTING(		0x0002, "USA (Fabtek license"));
		PORT_DIPSETTING(		0x0000, "Japan" );
		PORT_DIPSETTING(		0x0003, "Asia" );
		PORT_BIT( 0xfff0, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_battlegb = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( battlegb )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER1, IPT_BUTTON3, IPT_UNKNOWN )
	
		TOAPLAN2_PLAYER_INPUT( IPF_PLAYER2, IPT_BUTTON3, IPT_UNKNOWN )
	
		TOAPLAN2_SYSTEM_INPUTS
	
		PORT_START(); 		/* (4) DSWA */
		PORT_SERVICE( 0x0001,	IP_ACTIVE_HIGH );	/* Service Mode */
		PORT_DIPNAME( 0x0002,	0x0000, "Credits to Start" );
		PORT_DIPSETTING(		0x0000, "1" );
		PORT_DIPSETTING(		0x0002, "2" );
		PORT_DIPNAME( 0x001c,	0x0000, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(		0x0018, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(		0x0014, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(		0x0010, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(		0x0004, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(		0x000c, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(		0x001c, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x00e0,	0x0000, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(		0x00c0, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(		0x00a0, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(		0x0080, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(		0x0020, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(		0x0040, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(		0x0060, DEF_STR( "1C_4C") );
	//	PORT_DIPSETTING(		0x00e0, DEF_STR( "1C_1C") );
		/*  When Coin_A is set to Free_Play, Coin_A becomes Coin_A and Coin_B,
			and Coin_B becomes the following dips */
	//	PORT_DIPNAME( 0x0020,	0x0000, "Joystick Mode" );
	//	PORT_DIPSETTING(		0x0000, "90 degrees ACW" );
	//	PORT_DIPSETTING(		0x0020, "Normal" );
	//	PORT_DIPNAME( 0x0040,	0x0000, "Effect" );
	//	PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
	//	PORT_DIPSETTING(		0x0040, DEF_STR( "On") );
	//	PORT_DIPNAME( 0x0080,	0x0000, "Music" );
	//	PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
	//	PORT_DIPSETTING(		0x0080, DEF_STR( "On") );
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (5) DSWB */
		DIFFICULTY_16
		PORT_DIPNAME( 0x0004,	0x0000, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0004, DEF_STR( "On") );
		PORT_DIPNAME( 0x0008,	0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0070,	0x0000, DEF_STR( "Lives") );
		PORT_DIPSETTING(		0x0030, "1" );
		PORT_DIPSETTING(		0x0020, "2" );
		PORT_DIPSETTING(		0x0000, "3" );
		PORT_DIPSETTING(		0x0010, "4" );
		PORT_DIPSETTING(		0x0040, "5" );
		PORT_DIPSETTING(		0x0050, "6" );
		PORT_BITX( 0,			0x0060, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Infinite", 0, 0 );
	//	PORT_BITX( 0,			0x0070, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Invulnerability", 0, 0 );
		PORT_DIPNAME( 0x0080,	0x0000, DEF_STR( "Bonus_Life") );
		/* Bonus_Life for Non European territories */
	//	PORT_DIPSETTING(		0x0000, "Every 1000k" );
	//	PORT_DIPSETTING(		0x0080, "1000k and 2000k" );
		/* Bonus_Life values for European territories */
		PORT_DIPSETTING(		0x0080, "Every 2000k" );
		PORT_DIPSETTING(		0x0000, "None" );
		PORT_BIT( 0xff00, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (6) DSWC / Territory Jumper block */
		PORT_DIPNAME( 0x0004,	0x0000, "Allow Continue" );
		PORT_DIPSETTING(		0x0004, DEF_STR( "No") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x0008,	0x0000, "Stage Edit" );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "On") );
		PORT_DIPNAME( 0x0003,	0x0001, "Territory" );
		PORT_DIPSETTING(		0x0001, "Austria (German Tuning license"));
		/* These Settings End Up Reporting ROM-0 as BAD */
	//	PORT_DIPSETTING(		0x0002, "USA (Fabtek license"));
	//	PORT_DIPSETTING(		0x0000, "Japan" );
		PORT_DIPSETTING(		0x0003, "Hong Kong" );
		PORT_BIT( 0xfff0, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_batrider = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( batrider )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (1) Player Inputs */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0002, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0004, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0008, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0010, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x0020, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x0040, IP_ACTIVE_HIGH, IPT_BUTTON3 | IPF_PLAYER1 );
		PORT_BIT( 0x0100, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0200, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0400, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0800, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x1000, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x2000, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x4000, IP_ACTIVE_HIGH, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x8080, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (2) Coin/System and DSWC */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_COIN3 );
		PORT_BIT( 0x0002, IP_ACTIVE_HIGH, IPT_TILT );
		PORT_BIT( 0x0004, IP_ACTIVE_HIGH, IPT_SERVICE1 );
		PORT_BIT( 0x0008, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x0010, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x0020, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x0040, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_DIPNAME( 0x0100,	0x0000, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0100, DEF_STR( "On") );
		PORT_DIPNAME( 0x0200,	0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(		0x0200, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0400,	0x0000, "Stage Edit" );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0400, DEF_STR( "On") );
		PORT_DIPNAME( 0x0800,	0x0000, "Allow Continue" );
		PORT_DIPSETTING(		0x0800, DEF_STR( "No") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Yes") );
		PORT_BITX(	  0x1000,	0x0000, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x1000, DEF_STR( "On") );
		/*  These Dips are showed only when Coin_A is set to Free_Play.
			They are the last 3 Unused dips. Seems to be debug options */
	//	PORT_DIPNAME( 0x2000,	0x0000, "Guest Player" );
	//	PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
	//	PORT_DIPSETTING(		0x2000, DEF_STR( "On") );
	//	PORT_DIPNAME( 0x4000,	0x0000, "Player Select" );
	//	PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
	//	PORT_DIPSETTING(		0x4000, DEF_STR( "On") );
	//	PORT_DIPNAME( 0x8000,	0x0000, "Special Course" );
	//	PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
	//	PORT_DIPSETTING(		0x8000, DEF_STR( "On") );
		PORT_DIPNAME( 0x2000,	0x0000, DEF_STR( "Unused") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x2000, DEF_STR( "On") );
		PORT_DIPNAME( 0x4000,	0x0000, DEF_STR( "Unused") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x4000, DEF_STR( "On") );
		PORT_DIPNAME( 0x8000,	0x0000, DEF_STR( "Unused") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x8000, DEF_STR( "On") );
	
		PORT_START(); 		/* (3) DSWA and DSWB */
		PORT_SERVICE( 0x0001,	IP_ACTIVE_HIGH );	/* Service Mode */
		PORT_DIPNAME( 0x0002,	0x0000, "Credits to Start" );
		PORT_DIPSETTING(		0x0000, "1" );
		PORT_DIPSETTING(		0x0002, "2" );
		/* When Coin_A is set to Free_Play, dip 0x0002 becomes: */
	//	PORT_DIPNAME( 0x0002,	0x0000, "Joystick Mode" );
	//	PORT_DIPSETTING(		0x0000, "Normal" );
	//	PORT_DIPSETTING(		0x0002, "90 degrees ACW" );
		PORT_DIPNAME( 0x001c,	0x0000, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(		0x0018, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(		0x0014, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(		0x0010, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(		0x0004, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(		0x000c, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(		0x001c, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x00e0,	0x0000, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(		0x00c0, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(		0x00a0, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(		0x0080, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(		0x0020, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(		0x0040, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(		0x0060, DEF_STR( "1C_4C") );
	//	PORT_DIPSETTING(		0x00e0, DEF_STR( "1C_1C") );
		/* Coin_B becomes the following dips, when Coin_A is set to Free_Play */
	//	PORT_DIPNAME( 0x0020,	0x0000, "Hit Score" );
	//	PORT_DIPSETTING(		0x0020, DEF_STR( "Off") );
	//	PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
	//	PORT_DIPNAME( 0x0040,	0x0000, "Sound Effect" );
	//	PORT_DIPSETTING(		0x0040, DEF_STR( "Off") );
	//	PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
	//	PORT_DIPNAME( 0x0080,	0x0000, "Music" );
	//	PORT_DIPSETTING(		0x0080, DEF_STR( "Off") );
	//	PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0300,	0x0000, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(		0x0100, "Easy" );
		PORT_DIPSETTING(		0x0000, "Medium" );
		PORT_DIPSETTING(		0x0200, "Hard" );
		PORT_DIPSETTING(		0x0300, "Hardest" );
		PORT_DIPNAME( 0x0c00,	0x0000, "Timer" );
		PORT_DIPSETTING(		0x0400, "Easy" );
		PORT_DIPSETTING(		0x0000, "Medium" );
		PORT_DIPSETTING(		0x0800, "Hard" );
		PORT_DIPSETTING(		0x0c00, "Hardest" );
		PORT_DIPNAME( 0x3000,	0x0000, DEF_STR( "Lives") );
		PORT_DIPSETTING(		0x3000, "1" );
		PORT_DIPSETTING(		0x2000, "2" );
		PORT_DIPSETTING(		0x0000, "3" );
		PORT_DIPSETTING(		0x1000, "4" );
		PORT_DIPNAME( 0xc000,	0x0000, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(		0x4000, "Every 1000k" );
		PORT_DIPSETTING(		0x0000, "Every 1500k" );
		PORT_DIPSETTING(		0x8000, "Every 2000k" );
		PORT_DIPSETTING(		0xc000, "None" );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_bbakraid = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( bbakraid )
		PORT_START(); 		/* (0) VBlank */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0xfffe, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (1) Player Inputs */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0002, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0004, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0008, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0010, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x0020, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x0040, IP_ACTIVE_HIGH, IPT_BUTTON3 | IPF_PLAYER1 );
		PORT_BIT( 0x0100, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0200, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0400, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0800, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x1000, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x2000, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x4000, IP_ACTIVE_HIGH, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x8080, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 		/* (2) Coin/System and DSW-3 */
		PORT_BIT( 0x0001, IP_ACTIVE_HIGH, IPT_COIN3 );
		PORT_BIT( 0x0002, IP_ACTIVE_HIGH, IPT_TILT );
		PORT_BIT( 0x0004, IP_ACTIVE_HIGH, IPT_SERVICE1 );
		PORT_BIT( 0x0008, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x0010, IP_ACTIVE_HIGH, IPT_COIN2 );
		PORT_BIT( 0x0020, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x0040, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_DIPNAME( 0x0100,	0x0000, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0100, DEF_STR( "On") );
		PORT_DIPNAME( 0x0200,	0x0000, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(		0x0200, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0400,	0x0000, "Stage Edit" );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0400, DEF_STR( "On") );
		PORT_DIPNAME( 0x0800,	0x0000, "Allow Continue" );
		PORT_DIPSETTING(		0x0800, DEF_STR( "No") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Yes") );
		PORT_BITX(	  0x1000,	0x0000, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x1000, DEF_STR( "On") );
		PORT_DIPNAME( 0x2000,	0x0000, "Save Scores" );
		PORT_DIPSETTING(		0x2000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x4000,	0x0000, DEF_STR( "Unused") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x4000, DEF_STR( "On") );
		PORT_DIPNAME( 0x8000,	0x0000, DEF_STR( "Unused") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(		0x8000, DEF_STR( "On") );
	
		PORT_START(); 		/* (3) DSWA and DSWB */
		PORT_SERVICE( 0x0001,	IP_ACTIVE_HIGH );	/* Service Mode */
		PORT_DIPNAME( 0x0002,	0x0000, "Credits to Start" );
		PORT_DIPSETTING(		0x0000, "1" );
		PORT_DIPSETTING(		0x0002, "2" );
		/* When Coin_A is set to Free_Play, dip 0x0002 becomes: */
	//	PORT_DIPNAME( 0x0002,	0x0000, "Joystick Mode" );
	//	PORT_DIPSETTING(		0x0000, "Normal" );
	//	PORT_DIPSETTING(		0x0002, "90 degrees ACW" );
		PORT_DIPNAME( 0x001c,	0x0000, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(		0x0018, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(		0x0014, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(		0x0010, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(		0x0004, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(		0x0008, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(		0x000c, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(		0x001c, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x00e0,	0x0000, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(		0x00c0, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(		0x00a0, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(		0x0080, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(		0x0000, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(		0x0020, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(		0x0040, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(		0x0060, DEF_STR( "1C_4C") );
	//	PORT_DIPSETTING(		0x00e0, DEF_STR( "1C_1C") );
		/* Coin_B becomes the following dips, when Coin_A is set to Free_Play */
		/* Coin_B slot also behaves in freeplay mode when Coin_A is in freeplay */
	//	PORT_DIPNAME( 0x0020,	0x0000, "Hit Score" );
	//	PORT_DIPSETTING(		0x0020, DEF_STR( "Off") );
	//	PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
	//	PORT_DIPNAME( 0x0040,	0x0000, "Sound Effect" );
	//	PORT_DIPSETTING(		0x0040, DEF_STR( "Off") );
	//	PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
	//	PORT_DIPNAME( 0x0080,	0x0000, "Music" );
	//	PORT_DIPSETTING(		0x0080, DEF_STR( "Off") );
	//	PORT_DIPSETTING(		0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0300,	0x0000, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(		0x0100, "Easy" );
		PORT_DIPSETTING(		0x0000, "Medium" );
		PORT_DIPSETTING(		0x0200, "Hard" );
		PORT_DIPSETTING(		0x0300, "Hardest" );
		PORT_DIPNAME( 0x0c00,	0x0000, "Timer" );
		PORT_DIPSETTING(		0x0400, "Low" );
		PORT_DIPSETTING(		0x0000, "Medium" );
		PORT_DIPSETTING(		0x0800, "High" );
		PORT_DIPSETTING(		0x0c00, "Highest" );
		PORT_DIPNAME( 0x3000,	0x0000, DEF_STR( "Lives") );
		PORT_DIPSETTING(		0x3000, "1" );
		PORT_DIPSETTING(		0x2000, "2" );
		PORT_DIPSETTING(		0x0000, "3" );
		PORT_DIPSETTING(		0x1000, "4" );
		PORT_DIPNAME( 0xc000,	0x0000, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(		0x0000, "Every 200k" );
		PORT_DIPSETTING(		0x4000, "Every 300k" );
		PORT_DIPSETTING(		0x8000, "Every 400k" );
		PORT_DIPSETTING(		0xc000, "None" );
	INPUT_PORTS_END(); }}; 
	
	
	
	static struct GfxLayout tilelayout =
	{
		16,16,	/* 16x16 */
		RGN_FRAC(1,2),	/* Number of tiles */
		4,		/* 4 bits per pixel */
		{ RGN_FRAC(1,2)+8, RGN_FRAC(1,2), 8, 0 },
		{ 0, 1, 2, 3, 4, 5, 6, 7,
			8*16+0, 8*16+1, 8*16+2, 8*16+3, 8*16+4, 8*16+5, 8*16+6, 8*16+7 },
		{ 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
			16*16, 17*16, 18*16, 19*16, 20*16, 21*16, 22*16, 23*16 },
		8*4*16
	};
	
	static struct GfxLayout spritelayout =
	{
		8,8,	/* 8x8 */
		RGN_FRAC(1,2),	/* Number of 8x8 sprites */
		4,		/* 4 bits per pixel */
		{ RGN_FRAC(1,2)+8, RGN_FRAC(1,2), 8, 0 },
		{ 0, 1, 2, 3, 4, 5, 6, 7 },
		{ 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		8*16
	};
	
	static struct GfxLayout raizing_textlayout =
	{
		8,8,	/* 8x8 characters */
		1024,	/* 1024 characters */
		4,		/* 4 bits per pixel */
		{ 0, 1, 2, 3 },
		{ 0, 4, 8, 12, 16, 20, 24, 28 },
		{ 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
		8*32
	};
	
	#ifdef LSB_FIRST
	static struct GfxLayout truxton2_tx_tilelayout =
	{
		8,8,	/* 8x8 characters */
		1024,	/* 1024 characters */
		4,		/* 4 bits per pixel */
		{ 0, 1, 2, 3 },
		{ 0, 4, 16, 20, 32, 36, 48, 52 },
		{ 0*64, 1*64, 2*64, 3*64, 4*64, 5*64, 6*64, 7*64 },
		8*64
	};
	#else
	static struct GfxLayout truxton2_tx_tilelayout =
	{
		8,8,	/* 8x8 characters */
		1024,	/* 1024 characters */
		4,		/* 4 bits per pixel */
		{ 0, 1, 2, 3 },
		{ 0+8, 4+8, 16+8, 20+8, 32+8, 36+8, 48+8, 52+8 },
		{ 0*64, 1*64, 2*64, 3*64, 4*64, 5*64, 6*64, 7*64 },
		8*64
	};
	#endif
	
	#ifdef LSB_FIRST
	static struct GfxLayout batrider_tx_tilelayout =
	{
		8,8,	/* 8x8 characters */
		1024,	/* 1024 characters */
		4,		/* 4 bits per pixel */
		{ 0, 1, 2, 3 },
		{ 0, 4, 8, 12, 16, 20, 24, 28 },
		{ 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
		8*32
	};
	#else
	static struct GfxLayout batrider_tx_tilelayout =
	{
		8,8,	/* 8x8 characters */
		1024,	/* 1024 characters */
		4,		/* 4 bits per pixel */
		{ 0, 1, 2, 3 },
		{ 8, 12, 0, 4, 24, 28, 16, 20 },
		{ 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
		8*32
	};
	#endif
	
	static struct GfxDecodeInfo gfxdecodeinfo[] =
	{
		{ REGION_GFX1, 0, &tilelayout,   0, 128 },
		{ REGION_GFX1, 0, &spritelayout, 0,  64 },
		{ -1 } /* end of array */
	};
	
	static struct GfxDecodeInfo gfxdecodeinfo_2[] =
	{
		{ REGION_GFX1, 0, &tilelayout,   0, 128 },
		{ REGION_GFX1, 0, &spritelayout, 0,  64 },
		{ REGION_GFX2, 0, &tilelayout,   0, 128 },
		{ REGION_GFX2, 0, &spritelayout, 0,  64 },
		{ -1 } /* end of array */
	};
	
	static struct GfxDecodeInfo truxton2_gfxdecodeinfo[] =
	{
		{ REGION_GFX1, 0,       &tilelayout            , 0, 128 },
		{ REGION_GFX1, 0,       &spritelayout          , 0,  64 },
	//	{ REGION_CPU1, 0x40000, &truxton2_tx_tilelayout, 0, 128 },	/* Truxton 2 */
	//	{ REGION_CPU1, 0x68000, &truxton2_tx_tilelayout, 0, 128 },	/* Fix Eight */
		{ 0, 0, &truxton2_tx_tilelayout,  0, 128 },
		{ -1 } /* end of array */
	};
	
	static struct GfxDecodeInfo raizing_gfxdecodeinfo[] =
	{
		{ REGION_GFX1, 0, &tilelayout,         0, 128 },
		{ REGION_GFX1, 0, &spritelayout,       0,  64 },
		{ REGION_GFX2, 0, &raizing_textlayout, 0, 128 },		/* Extra-text layer */
		{ -1 } /* end of array */
	};
	
	/* This is wrong a bit. Text layer is dynamically changed. */
	static struct GfxDecodeInfo batrider_gfxdecodeinfo[] =
	{
		{ REGION_GFX1, 0, &tilelayout,             0, 128 },
		{ REGION_GFX1, 0, &spritelayout,           0,  64 },
		{ 0,           0, &batrider_tx_tilelayout, 0,  16 },
		{ -1 } /* end of array */
	};
	
	
	static void irqhandler(int linestate)
	{
		cpu_set_irq_line(1,0,linestate);
	}
	
	static struct YM3812interface ym3812_interface =
	{
		1,				/* 1 chip  */
		27000000/8,		/* 3.375MHz , 27MHz Oscillator */
		{ 100 },		/* volume */
		{ irqhandler },
	};
	
	static struct YM2151interface ym2151_interface =
	{
		1,				/* 1 chip */
		27000000/8,		/* 3.375MHz , 27MHz Oscillator */
		{ YM3012_VOL(25,MIXER_PAN_LEFT,25,MIXER_PAN_RIGHT) },
		{ 0 }
	};
	
	static struct YM2151interface raizing_ym2151_interface =
	{
		1,				/* 1 chip */
		32000000/8,		/* 4.00MHz , 32MHz Oscillator */
		{ YM3012_VOL(25,MIXER_PAN_LEFT,25,MIXER_PAN_RIGHT) },
		{ 0 }
	};
	
	
	static struct OKIM6295interface okim6295_interface =
	{
		1,						/* 1 chip */
		{ 27000000/10/132 },	/* frequency (Hz). 2.7MHz to 6295 (using B mode) */
		{ REGION_SOUND1 },		/* memory region */
		{ 25 }
	};
	
	static struct OKIM6295interface raizing_okim6295_interface =
	{
		1,						/* 1 chip */
		{ 32000000/32/132 },	/* frequency (Hz) 1MHz to 6295 (using B mode) */
		{ REGION_SOUND1 },		/* memory region */
		{ 25 }
	};
	
	static struct OKIM6295interface battleg_okim6295_interface =
	{
		1,						/* 1 chip */
		{ 32000000/16/132 },	/* frequency (Hz). 2MHz to 6295 (using B mode) */
		{ REGION_SOUND1 },		/* memory region */
		{ 25 }
	};
	
	static struct OKIM6295interface batrider_okim6295_interface =
	{
		2,										/* 2 chips */
		{ 32000000/10/132, 32000000/10/165 },	/* frequency (Hz). 3.2MHz to two 6295 (using B mode / A mode) */
		{ REGION_SOUND1, REGION_SOUND2 },		/* memory region */
		{ 25, 25 }
	};
	
	static struct YMZ280Binterface ymz280b_interface =
	{
		1,
		{ 16934400 },
		{ REGION_SOUND1 },
		{ YM3012_VOL(100,MIXER_PAN_LEFT,100,MIXER_PAN_RIGHT) },
		{ bbakraid_irqhandler }
	};
	
	
	
	static MACHINE_DRIVER_START( tekipaki )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 10000000)			/* 10MHz Oscillator */
		MDRV_CPU_MEMORY(tekipaki_readmem,tekipaki_writemem)
		MDRV_CPU_VBLANK_INT(toaplan2_vblank_irq4,262)
	
	#if HD64x180
		MDRV_CPU_ADD(Z180, 10000000)			/* HD647180 CPU actually */
		MDRV_CPU_MEMORY(hd647180_readmem,hd647180_writemem)
	#endif
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(toaplan2)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(32*16, 32*16)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(toaplan2_0)
		MDRV_VIDEO_EOF(toaplan2_0)
		MDRV_VIDEO_UPDATE(toaplan2_0)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM3812, ym3812_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( ghox )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 10000000)			/* 10MHz Oscillator */
		MDRV_CPU_MEMORY(ghox_readmem,ghox_writemem)
		MDRV_CPU_VBLANK_INT(toaplan2_vblank_irq4,262)
	
	#if HD64x180
		MDRV_CPU_ADD(Z180, 10000000)			/* HD647180 CPU actually */
		MDRV_CPU_MEMORY(hd647180_readmem,hd647180_writemem)
	#endif
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(ghox)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(32*16, 32*16)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(toaplan2_0)
		MDRV_VIDEO_EOF(toaplan2_0)
		MDRV_VIDEO_UPDATE(toaplan2_0)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( dogyuun )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 16000000)			/* 16MHz Oscillator */
		MDRV_CPU_MEMORY(dogyuun_readmem,dogyuun_writemem)
		MDRV_CPU_VBLANK_INT(toaplan2_vblank_irq4,262)
	
	#if Zx80
		MDRV_CPU_ADD(Z180, 16000000)			/* Z?80 type Toaplan marked CPU ??? */
		MDRV_CPU_MEMORY(Zx80_readmem,Zx80_writemem)
		MDRV_CPU_PORTS(Zx80_readport,0)
	#endif
	
		MDRV_FRAMES_PER_SECOND( (27000000.0 / 4) / (432 * 263) )
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(toaplan2)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(32*16, 32*16)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(gfxdecodeinfo_2)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(toaplan2_1)
		MDRV_VIDEO_EOF(toaplan2_1)
		MDRV_VIDEO_UPDATE(dogyuun_1)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(OKIM6295, okim6295_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( kbash )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 16000000)			/* 16MHz Oscillator */
		MDRV_CPU_MEMORY(kbash_readmem,kbash_writemem)
		MDRV_CPU_VBLANK_INT(toaplan2_vblank_irq4,262)
	
	#if Zx80
		MDRV_CPU_ADD(Z180, 16000000)			/* Z?80 type Toaplan marked CPU ??? */
		MDRV_CPU_MEMORY(Zx80_readmem,Zx80_writemem)
		MDRV_CPU_PORTS(Zx80_readport,0)
	#endif
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(toaplan2)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(32*16, 32*16)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(toaplan2_0)
		MDRV_VIDEO_EOF(toaplan2_0)
		MDRV_VIDEO_UPDATE(toaplan2_0)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(OKIM6295, okim6295_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( truxton2 )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 16000000)			/* 16MHz Oscillator */
		MDRV_CPU_MEMORY(truxton2_readmem,truxton2_writemem)
		MDRV_CPU_VBLANK_INT(toaplan2_vblank_irq2,262)
	
		MDRV_FRAMES_PER_SECOND( (27000000.0 / 4) / (432 * 263) )
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(toaplan2)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(32*16, 32*16)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(truxton2_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(truxton2_0)
		MDRV_VIDEO_EOF(toaplan2_0)
		MDRV_VIDEO_UPDATE(truxton2_0)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(OKIM6295, okim6295_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( pipibibs )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 10000000)			/* 10MHz Oscillator */
		MDRV_CPU_MEMORY(pipibibs_readmem,pipibibs_writemem)
		MDRV_CPU_VBLANK_INT(toaplan2_vblank_irq4,262)
	
		MDRV_CPU_ADD(Z80,27000000/8)			/* ??? 3.37MHz , 27MHz Oscillator */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(10)
	
		MDRV_MACHINE_INIT(toaplan2)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(32*16, 32*16)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(toaplan2_0)
		MDRV_VIDEO_EOF(toaplan2_0)
		MDRV_VIDEO_UPDATE(toaplan2_0)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM3812, ym3812_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( whoopee )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 10000000)			/* 10MHz Oscillator */
		MDRV_CPU_MEMORY(tekipaki_readmem,tekipaki_writemem)
		MDRV_CPU_VBLANK_INT(toaplan2_vblank_irq4,262)
	
		MDRV_CPU_ADD(Z80, 27000000/8)			/* This should be a HD647180 */
												/* Change this to 10MHz when HD647180 gets dumped. 10MHz Oscillator */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(10)
	
		MDRV_MACHINE_INIT(toaplan2)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(32*16, 32*16)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(toaplan2_0)
		MDRV_VIDEO_EOF(toaplan2_0)
		MDRV_VIDEO_UPDATE(toaplan2_0)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM3812, ym3812_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( pipibibi )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 10000000)			/* 10MHz Oscillator */
		MDRV_CPU_MEMORY(pipibibi_readmem,pipibibi_writemem)
		MDRV_CPU_VBLANK_INT(toaplan2_vblank_irq4,262)
	
		MDRV_CPU_ADD(Z80,27000000/8)			/* ??? 3.37MHz */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(10)
	
		MDRV_MACHINE_INIT(toaplan2)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(32*16, 32*16)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(toaplan2_0)
		MDRV_VIDEO_EOF(toaplan2_0)
		MDRV_VIDEO_UPDATE(toaplan2_0)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YM3812, ym3812_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( fixeight )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 16000000)			/* 16MHz Oscillator */
		MDRV_CPU_MEMORY(fixeight_readmem,fixeight_writemem)
		MDRV_CPU_VBLANK_INT(toaplan2_vblank_irq4,262)
	
	#if Zx80
		MDRV_CPU_ADD(Z180, 16000000)			/* Z?80 type Toaplan marked CPU ??? */
		MDRV_CPU_MEMORY(Zx80_readmem,Zx80_writemem)
		MDRV_CPU_PORTS(Zx80_readport,0)
	#endif
	
		MDRV_FRAMES_PER_SECOND( (27000000.0 / 4) / (432 * 263) )
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(toaplan2)
	///	MDRV_NVRAM_HANDLER(fixeight)		/* See 37B6 code */
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(32*16, 32*16)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(truxton2_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(truxton2_0)
		MDRV_VIDEO_EOF(toaplan2_0)
		MDRV_VIDEO_UPDATE(truxton2_0)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(OKIM6295, okim6295_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( vfive )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 10000000)			/* 10MHz Oscillator */
		MDRV_CPU_MEMORY(vfive_readmem,vfive_writemem)
		MDRV_CPU_VBLANK_INT(toaplan2_vblank_irq4,262)
	
	#if Zx80
		MDRV_CPU_ADD(Z180, 10000000)			/* Z?80 type Toaplan marked CPU ??? */
		MDRV_CPU_MEMORY(Zx80_readmem,Zx80_writemem)
		MDRV_CPU_PORTS(Zx80_readport,0)
	#endif
	
		MDRV_FRAMES_PER_SECOND( (27000000.0 / 4) / (432 * 263) )
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(toaplan2)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(32*16, 32*16)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(toaplan2_0)
		MDRV_VIDEO_EOF(toaplan2_0)
		MDRV_VIDEO_UPDATE(toaplan2_0)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( batsugun )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000,32000000/2)			/* 16MHz , 32MHz Oscillator */
		MDRV_CPU_MEMORY(batsugun_readmem,batsugun_writemem)
		MDRV_CPU_VBLANK_INT(toaplan2_vblank_irq4,262)
	
	#if Zx80
		MDRV_CPU_ADD(Z180, 32000000/2)			/* Z?80 type Toaplan marked CPU ??? */
		MDRV_CPU_MEMORY(Zx80_readmem,Zx80_writemem)
		MDRV_CPU_PORTS(Zx80_readport,0)
	#endif
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(toaplan2)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(32*16, 32*16)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(gfxdecodeinfo_2)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(toaplan2_1)
		MDRV_VIDEO_EOF(toaplan2_1)
		MDRV_VIDEO_UPDATE(batsugun_1)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(OKIM6295, okim6295_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( snowbro2 )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000, 16000000)
		MDRV_CPU_MEMORY(snowbro2_readmem,snowbro2_writemem)
		MDRV_CPU_VBLANK_INT(toaplan2_vblank_irq4,262)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(toaplan2)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(32*16, 32*16)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(toaplan2_0)
		MDRV_VIDEO_EOF(toaplan2_0)
		MDRV_VIDEO_UPDATE(toaplan2_0)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(OKIM6295, okim6295_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( mahoudai )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000,32000000/2)			/* 16MHz , 32MHz Oscillator */
		MDRV_CPU_MEMORY(mahoudai_readmem,mahoudai_writemem)
		MDRV_CPU_VBLANK_INT(toaplan2_vblank_irq4,262)
	
		MDRV_CPU_ADD(Z80,32000000/8)			/* 4MHz , 32MHz Oscillator */
		MDRV_CPU_MEMORY(raizing_sound_readmem,raizing_sound_writemem)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(10)
	
		MDRV_MACHINE_INIT(toaplan2)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(32*16, 32*16)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(raizing_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(battleg_0)
		MDRV_VIDEO_EOF(toaplan2_0)
		MDRV_VIDEO_UPDATE(mahoudai_0)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(OKIM6295, raizing_okim6295_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( shippumd )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000,32000000/2)			/* 16MHz , 32MHz Oscillator */
		MDRV_CPU_MEMORY(shippumd_readmem,shippumd_writemem)
		MDRV_CPU_VBLANK_INT(toaplan2_vblank_irq4,262)
	
		MDRV_CPU_ADD(Z80,32000000/8)			/* 4MHz , 32MHz Oscillator */
		MDRV_CPU_MEMORY(raizing_sound_readmem,raizing_sound_writemem)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(10)
	
		MDRV_MACHINE_INIT(toaplan2)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(32*16, 32*16)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(raizing_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(battleg_0)
		MDRV_VIDEO_EOF(toaplan2_0)
		MDRV_VIDEO_UPDATE(truxton2_0)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, ym2151_interface)
		MDRV_SOUND_ADD(OKIM6295, raizing_okim6295_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( battleg )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000,32000000/2)			/* 16MHz , 32MHz Oscillator */
		MDRV_CPU_MEMORY(battleg_readmem,battleg_writemem)
		MDRV_CPU_VBLANK_INT(toaplan2_vblank_irq4,262)
	
		MDRV_CPU_ADD(Z80,32000000/8)			/* 4MHz , 32MHz Oscillator */
		MDRV_CPU_MEMORY(battleg_sound_readmem,battleg_sound_writemem)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(10)
	
		MDRV_MACHINE_INIT(toaplan2)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(32*16, 32*16)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(raizing_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(battleg_0)
		MDRV_VIDEO_EOF(toaplan2_0)
		MDRV_VIDEO_UPDATE(truxton2_0)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, raizing_ym2151_interface)
		MDRV_SOUND_ADD(OKIM6295, battleg_okim6295_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( batrider )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000,32000000/2)			/* 16MHz , 32MHz Oscillator */
		MDRV_CPU_MEMORY(batrider_readmem,batrider_writemem)
		MDRV_CPU_VBLANK_INT(toaplan2_vblank_irq2,262)
	
		MDRV_CPU_ADD(Z80,32000000/8)			/* 4MHz , 32MHz Oscillator */
		MDRV_CPU_MEMORY(batrider_sound_readmem,batrider_sound_writemem)
		MDRV_CPU_PORTS(batrider_sound_readport,batrider_sound_writeport)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(10)
	
		MDRV_MACHINE_INIT(batrider)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(32*16, 32*16)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(batrider_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(batrider_0)
		MDRV_VIDEO_EOF(batrider_0)
		MDRV_VIDEO_UPDATE(batrider_0)
	
		/* sound hardware */
		MDRV_SOUND_ATTRIBUTES(SOUND_SUPPORTS_STEREO)
		MDRV_SOUND_ADD(YM2151, raizing_ym2151_interface)
		MDRV_SOUND_ADD(OKIM6295, batrider_okim6295_interface)
	MACHINE_DRIVER_END
	
	static MACHINE_DRIVER_START( bbakraid )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M68000,32000000/2)
		MDRV_CPU_MEMORY(bbakraid_readmem,bbakraid_writemem)
		MDRV_CPU_VBLANK_INT(toaplan2_vblank_irq3,262)
	
		MDRV_CPU_ADD(Z80,32000000/4)
		MDRV_CPU_MEMORY(bbakraid_sound_readmem,bbakraid_sound_writemem)
		MDRV_CPU_PORTS(bbakraid_sound_readport,bbakraid_sound_writeport)
		MDRV_CPU_PERIODIC_INT(bbakraid_snd_interrupt, 388)
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
		MDRV_INTERLEAVE(262)
	
		MDRV_MACHINE_INIT(toaplan2)
		MDRV_NVRAM_HANDLER(bbakraid)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_UPDATE_BEFORE_VBLANK)
		MDRV_SCREEN_SIZE(32*16, 32*16)
		MDRV_VISIBLE_AREA(0, 319, 0, 239)
		MDRV_GFXDECODE(batrider_gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(2048)
	
		MDRV_VIDEO_START(batrider_0)
		MDRV_VIDEO_UPDATE(batrider_0)
	
		/* sound hardware */
		MDRV_SOUND_ADD(YMZ280B, ymz280b_interface)
	MACHINE_DRIVER_END
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	/* -------------------------- Toaplan games ------------------------- */
	static RomLoadHandlerPtr rom_tekipaki = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x020000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_BYTE( "tp020-1.bin", 0x000000, 0x010000, CRC(d8420bd5) SHA1(30c1ad9e053cd7e79adb42aa428ebee28e144755) )
		ROM_LOAD16_BYTE( "tp020-2.bin", 0x000001, 0x010000, CRC(7222de8e) SHA1(8352ae23efc24a2e20cc24b6d37cb8fc6b1a730c) )
	
	#if HD64x180
		ROM_REGION( 0x10000, REGION_CPU2, 0 )			/* Sound HD647180 code */
		/* sound CPU is a HD647180 (Z180) with internal ROM - not yet supported */
		ROM_LOAD( "hd647180.020", 0x00000, 0x08000, NO_DUMP )
	#endif
	
		ROM_REGION( 0x100000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "tp020-4.bin", 0x000000, 0x080000, CRC(3ebbe41e) SHA1(cea196c5f83e1a23d5b538a0db9bbbffa7af5118) )
		ROM_LOAD( "tp020-3.bin", 0x080000, 0x080000, CRC(2d5e2201) SHA1(5846c844eedd48305c1c67dc645b6e070b3f5b98) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_ghox = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x040000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_BYTE( "tp021-01.u10", 0x000000, 0x020000, CRC(9e56ac67) SHA1(daf241d9e55a6e60fc004ed61f787641595b1e62) )
		ROM_LOAD16_BYTE( "tp021-02.u11", 0x000001, 0x020000, CRC(15cac60f) SHA1(6efa3a50a5dfe6ef4072738d6a7d0d95dca8a675) )
	
	#if HD64x180
		ROM_REGION( 0x10000, REGION_CPU2, 0 )			/* Sound HD647180 code */
		/* sound CPU is a HD647180 (Z180) with internal ROM - not yet supported */
		ROM_LOAD( "hd647180.021", 0x00000, 0x08000, NO_DUMP )
	#endif
	
		ROM_REGION( 0x100000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "tp021-03.u36", 0x000000, 0x080000, CRC(a15d8e9d) SHA1(640a33997bdce8e84bea6a944139716379839037) )
		ROM_LOAD( "tp021-04.u37", 0x080000, 0x080000, CRC(26ed1c9a) SHA1(37da8af86ea24327444c2d4ad3dfbd936208d43d) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_dogyuun = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x080000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_WORD( "tp022_01.r16", 0x000000, 0x080000, CRC(72f18907) SHA1(9d1b3800764a63e046644c78a2e0339280e038cb) )
	
		/* Secondary CPU is a Toaplan marked chip, (TS-002-MACH  TOA PLAN) */
		/* Its a Z?80 of some sort - 94 pin chip. */
	#if Zx80
		ROM_REGION( 0x10000, REGION_CPU2, 0 )			/* Secondary CPU code */
		/* Secondary CPU is a Toaplan marked chip ??? */
	//	ROM_LOAD( "tp022.mcu", 0x00000, 0x08000, NO_DUMP )
	#endif
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD16_WORD_SWAP( "tp022_3.w92", 0x000000, 0x100000, CRC(191b595f) SHA1(89344946daa18087cc83f92027cf5da659b1c7a5) )
		ROM_LOAD16_WORD_SWAP( "tp022_4.w93", 0x100000, 0x100000, CRC(d58d29ca) SHA1(90d142fef37764ef817347a2bed77892a288a077) )
	
		ROM_REGION( 0x400000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD16_WORD_SWAP( "tp022_5.w16", 0x000000, 0x200000, CRC(d4c1db45) SHA1(f5655467149ba737128c2f54c9c6cdaca6e4c35c) )
		ROM_LOAD16_WORD_SWAP( "tp022_6.w17", 0x200000, 0x200000, CRC(d48dc74f) SHA1(081b5a00a2ff2bd82b98b30aab3cb5b6ae1014d5) )
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 )		/* ADPCM Samples */
		ROM_LOAD( "tp022_2.w30", 0x00000, 0x40000, CRC(043271b3) SHA1(c7eaa929e55dd956579b824ea9d20a1d0129a925) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_kbash = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x080000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_WORD_SWAP( "kbash01.bin", 0x000000, 0x080000, CRC(2965f81d) SHA1(46f2df30fa92c80ba5a37f75e756424e15534784) )
	
		/* Secondary CPU is a Toaplan marked chip, (TS-004-Dash  TOA PLAN) */
		/* Its a Z?80 of some sort - 94 pin chip. */
	#if Zx80
		ROM_REGION( 0x88000, REGION_CPU2, 0 )			/* Sound Z?80 code */
		ROM_LOAD( "kbash02.bin", 0x80000, 0x08000, CRC(4cd882a1) SHA1(7199a5c384918f775f0815e09c46b2a58141814a) )
	#else
		ROM_REGION( 0x08000, REGION_USER1, 0 )
		ROM_LOAD( "kbash02.bin", 0x00200, 0x07e00, CRC(4cd882a1) SHA1(7199a5c384918f775f0815e09c46b2a58141814a) )
		ROM_CONTINUE(			 0x00000, 0x00200 )
	#endif
	
		ROM_REGION( 0x800000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "kbash03.bin", 0x000000, 0x200000, CRC(32ad508b) SHA1(e473489beaf649d3e5236770eb043327e309850c) )
		ROM_LOAD( "kbash05.bin", 0x200000, 0x200000, CRC(b84c90eb) SHA1(17a1531d884d9a9696d1b25d65f9155f02396e0e) )
		ROM_LOAD( "kbash04.bin", 0x400000, 0x200000, CRC(e493c077) SHA1(0edcfb70483ad07206695d9283031b85cd198a36) )
		ROM_LOAD( "kbash06.bin", 0x600000, 0x200000, CRC(9084b50a) SHA1(03b58278619524d2f09a4b1c152d5e057e792a56) )
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 )		/* ADPCM Samples */
		ROM_LOAD( "kbash07.bin", 0x00000, 0x40000, CRC(3732318f) SHA1(f0768459f5ad2dee53d408a0a5ae3a314864e667) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_truxton2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x080000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_WORD( "tp024_1.bin", 0x000000, 0x080000, CRC(f5cfe6ee) SHA1(30979888a4cd6500244117748f28386a7e20a169) )
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "tp024_4.bin", 0x000000, 0x100000, CRC(805c449e) SHA1(fdf985344145bd320b88b9b0c25e73066c9b2ada) )
		ROM_LOAD( "tp024_3.bin", 0x100000, 0x100000, CRC(47587164) SHA1(bac493e2d5507286b984957b289c929335d27eaa) )
	
		ROM_REGION( 0x80000, REGION_SOUND1, 0 )			/* ADPCM Samples */
		ROM_LOAD( "tp024_2.bin", 0x00000, 0x80000, CRC(f2f6cae4) SHA1(bb4e8c36531bed97ced4696ca12fd40ede2531aa) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_pipibibs = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x040000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_BYTE( "tp025-1.bin", 0x000000, 0x020000, CRC(b2ea8659) SHA1(400431b656dbfbd5a9bc5961c3ea04c4d38b6f77) )
		ROM_LOAD16_BYTE( "tp025-2.bin", 0x000001, 0x020000, CRC(dc53b939) SHA1(e4de371f97ba7c350273ad43b7f58ff31672a269) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )			/* Sound Z80 code */
		ROM_LOAD( "tp025-5.bin", 0x0000, 0x8000, CRC(bf8ffde5) SHA1(79c09cc9a0ea979f5af5a7e5ad671ea486f5f43e) )
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "tp025-4.bin", 0x000000, 0x100000, CRC(ab97f744) SHA1(c1620e614345dbd5c6567e4cb6f55c61b900d0ee) )
		ROM_LOAD( "tp025-3.bin", 0x100000, 0x100000, CRC(7b16101e) SHA1(ae0119bbfa0937d18c4fbb0a3ef7cdc3b9fa6b56) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_whoopee = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x040000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_BYTE( "whoopee.1", 0x000000, 0x020000, CRC(28882e7e) SHA1(8fcd278a7d005eb81cd9e461139c0c0f756a4fa4) )
		ROM_LOAD16_BYTE( "whoopee.2", 0x000001, 0x020000, CRC(6796f133) SHA1(d4e657be260ba3fd3f0556ade617882513b52685) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )			/* Sound Z80 code */
		/* sound CPU is a HD647180 (Z180) with internal ROM - not yet supported */
		/* use the Z80 version from the bootleg Pipi & Bibis set for now */
		ROM_LOAD( "hd647180.025", 0x00000, 0x08000, BAD_DUMP CRC(101c0358) SHA1(162e02d00b7bdcdd3b48a0cd0527b7428435ec50)  )
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "tp025-4.bin", 0x000000, 0x100000, CRC(ab97f744) SHA1(c1620e614345dbd5c6567e4cb6f55c61b900d0ee) )
		ROM_LOAD( "tp025-3.bin", 0x100000, 0x100000, CRC(7b16101e) SHA1(ae0119bbfa0937d18c4fbb0a3ef7cdc3b9fa6b56) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_pipibibi = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x040000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_BYTE( "ppbb06.bin", 0x000000, 0x020000, CRC(14c92515) SHA1(2d7f7c89272bb2a8115f163ad651bef3bca5107e) )
		ROM_LOAD16_BYTE( "ppbb05.bin", 0x000001, 0x020000, CRC(3d51133c) SHA1(d7bd94ad11e9aeb5a5165c5ac6f71950849bcd2f) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )			/* Sound Z80 code */
		ROM_LOAD( "ppbb08.bin", 0x0000, 0x8000, CRC(101c0358) SHA1(162e02d00b7bdcdd3b48a0cd0527b7428435ec50) )
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
		/* GFX data differs slightly from Toaplan boards ??? */
		ROM_LOAD16_BYTE( "ppbb01.bin", 0x000000, 0x080000, CRC(0fcae44b) SHA1(ac72bc79e3a5d0a81647c312d310d00ace017272) )
		ROM_LOAD16_BYTE( "ppbb02.bin", 0x000001, 0x080000, CRC(8bfcdf87) SHA1(4537a7d646d3014f069c6fd0be457bb32e2f18ac) )
		ROM_LOAD16_BYTE( "ppbb03.bin", 0x100000, 0x080000, CRC(abdd2b8b) SHA1(a4246dd63515f01d1227c9a9e16d9f1c739ee39e) )
		ROM_LOAD16_BYTE( "ppbb04.bin", 0x100001, 0x080000, CRC(70faa734) SHA1(4448f4dbded56c142e57293d371e0a422c3a667e) )
	
		ROM_REGION( 0x8000, REGION_USER1, 0 )			/* ??? Some sort of table */
		ROM_LOAD( "ppbb07.bin", 0x0000, 0x8000, CRC(456dd16e) SHA1(84779ee64d3ea33ba1ba4dee39b504a81c6811a1) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_fixeight = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x080000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_WORD_SWAP( "tp-026-1", 0x000000, 0x080000, CRC(f7b1746a) )
	
	#if Zx80
		ROM_REGION( 0x10000, REGION_CPU2, 0 )			/* Secondary CPU code */
		/* Secondary CPU is a Toaplan marked chip, (TS-001-Turbo  TOA PLAN) */
		/* Its a Z?80 of some sort - 94 pin chip. */
	//	ROM_LOAD( "tp-026.mcu", 0x0000, 0x8000, NO_DUMP )
	#endif
	
		ROM_REGION( 0x400000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "tp-026-3", 0x000000, 0x200000, CRC(e5578d98) )
		ROM_LOAD( "tp-026-4", 0x200000, 0x200000, CRC(b760cb53) )
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 )			/* ADPCM Samples */
		ROM_LOAD( "tp-026-2", 0x00000, 0x40000, CRC(85063f1f) )
	
		ROM_REGION( 0x80, REGION_USER1, 0 )
		/* Serial EEPROM (93C45) connected to Secondary CPU */
		ROM_LOAD( "93c45.u21", 0x00, 0x80, CRC(40d75df0) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_grindstm = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x080000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_WORD_SWAP( "01.bin", 0x000000, 0x080000, CRC(4923f790) SHA1(1c2d66b432d190d0fb6ac7ca0ec0687aea3ccbf4) )
	
	#if Zx80
		ROM_REGION( 0x10000, REGION_CPU2, 0 )			/* Sound CPU code */
		/* Secondary CPU is a Toaplan marked chip, (TS-007-Spy  TOA PLAN) */
		/* Its a Z?80 of some sort - 94 pin chip. */
	//	ROM_LOAD( "tp027.mcu", 0x8000, 0x8000, NO_DUMP )
	#endif
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "tp027_02.bin", 0x000000, 0x100000, CRC(877b45e8) SHA1(b3ed8d8dbbe51a1919afc55d619d2b6771971493) )
		ROM_LOAD( "tp027_03.bin", 0x100000, 0x100000, CRC(b1fc6362) SHA1(5e97e3cce31be57689d394a50178cda4d80cce5f) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_grindsta = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x080000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_WORD_SWAP( "tp027-01.rom", 0x000000, 0x080000, CRC(8d8c0392) SHA1(824dde274c8bef8a87c54d8ccdda7f0feb8d11e1) )
	
	#if Zx80
		ROM_REGION( 0x10000, REGION_CPU2, 0 )			/* Sound CPU code */
		/* Secondary CPU is a Toaplan marked chip, (TS-007-Spy  TOA PLAN) */
		/* Its a Z?80 of some sort - 94 pin chip. */
	//	ROM_LOAD( "tp027.mcu", 0x8000, 0x8000, NO_DUMP )
	#endif
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "tp027_02.bin", 0x000000, 0x100000, CRC(877b45e8) SHA1(b3ed8d8dbbe51a1919afc55d619d2b6771971493) )
		ROM_LOAD( "tp027_03.bin", 0x100000, 0x100000, CRC(b1fc6362) SHA1(5e97e3cce31be57689d394a50178cda4d80cce5f) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_vfive = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x080000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_WORD_SWAP( "tp027_01.bin", 0x000000, 0x080000, CRC(731d50f4) SHA1(794255d0a809cda9170f5bac473df9d7f0efdac8) )
	
	#if Zx80
		ROM_REGION( 0x10000, REGION_CPU2, 0 )			/* Sound CPU code */
		/* Secondary CPU is a Toaplan marked chip, (TS-007-Spy  TOA PLAN) */
		/* Its a Z?80 of some sort - 94 pin chip. */
	//	ROM_LOAD( "tp027.mcu", 0x8000, 0x8000, NO_DUMP )
	#endif
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "tp027_02.bin", 0x000000, 0x100000, CRC(877b45e8) SHA1(b3ed8d8dbbe51a1919afc55d619d2b6771971493) )
		ROM_LOAD( "tp027_03.bin", 0x100000, 0x100000, CRC(b1fc6362) SHA1(5e97e3cce31be57689d394a50178cda4d80cce5f) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_batsugun = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x080000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_WORD_SWAP( "tp030_01.bin", 0x000000, 0x080000, CRC(3873d7dd) SHA1(baf6187d7d554cfcf4a86b63f07fc30df7ef84c9) )
	
	#if Zx80
		ROM_REGION( 0x10000, REGION_CPU2, 0 )			/* Sound CPU code */
		/* Secondary CPU is a Toaplan marked chip, (TS-007-Spy  TOA PLAN) */
		/* Its a Z?80 of some sort - 94 pin chip. */
	//	ROM_LOAD( "tp030.mcu", 0x8000, 0x8000, NO_DUMP )
	#endif
	
		ROM_REGION( 0x400000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "tp030_3l.bin", 0x000000, 0x100000, CRC(3024b793) SHA1(e161db940f069279356fca2c5bf2753f07773705) )
		ROM_LOAD( "tp030_3h.bin", 0x100000, 0x100000, CRC(ed75730b) SHA1(341f0f728144a049486d996c9bb14078578c6879) )
		ROM_LOAD( "tp030_4l.bin", 0x200000, 0x100000, CRC(fedb9861) SHA1(4b0917056bd359b21935358c6bcc729262be6417) )
		ROM_LOAD( "tp030_4h.bin", 0x300000, 0x100000, CRC(d482948b) SHA1(31be7dc5cff072403b783bf203b9805ffcad7284) )
	
		ROM_REGION( 0x200000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "tp030_5.bin",  0x000000, 0x100000, CRC(bcf5ba05) SHA1(40f98888a29cdd30cda5dfb60fdc667c69b0fdb0) )
		ROM_LOAD( "tp030_6.bin",  0x100000, 0x100000, CRC(0666fecd) SHA1(aa8f921fc51590b5b05bbe0b0ad0cce5ff359c64) )
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 )			/* ADPCM Samples */
		ROM_LOAD( "tp030_2.bin", 0x00000, 0x40000, CRC(276146f5) SHA1(bf11d1f6782cefcad77d52af4f7e6054a8f93440) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_batugnsp = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x080000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_WORD_SWAP( "tp030-sp.u69", 0x000000, 0x080000, CRC(8072a0cd) SHA1(3a0a9cdf894926a16800c4882a2b00383d981367) )
	
	#if Zx80
		ROM_REGION( 0x10000, REGION_CPU2, 0 )			/* Sound CPU code */
		/* Secondary CPU is a Toaplan marked chip, (TS-007-Spy  TOA PLAN) */
		/* Its a Z?80 of some sort - 94 pin chip. */
	//	ROM_LOAD( "tp030.mcu", 0x8000, 0x8000, NO_DUMP )
	#endif
	
		ROM_REGION( 0x400000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "tp030_3l.bin", 0x000000, 0x100000, CRC(3024b793) SHA1(e161db940f069279356fca2c5bf2753f07773705) )
		ROM_LOAD( "tp030_3h.bin", 0x100000, 0x100000, CRC(ed75730b) SHA1(341f0f728144a049486d996c9bb14078578c6879) )
		ROM_LOAD( "tp030_4l.bin", 0x200000, 0x100000, CRC(fedb9861) SHA1(4b0917056bd359b21935358c6bcc729262be6417) )
		ROM_LOAD( "tp030_4h.bin", 0x300000, 0x100000, CRC(d482948b) SHA1(31be7dc5cff072403b783bf203b9805ffcad7284) )
	
		ROM_REGION( 0x200000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "tp030_5.bin",  0x000000, 0x100000, CRC(bcf5ba05) SHA1(40f98888a29cdd30cda5dfb60fdc667c69b0fdb0) )
		ROM_LOAD( "tp030_6.bin",  0x100000, 0x100000, CRC(0666fecd) SHA1(aa8f921fc51590b5b05bbe0b0ad0cce5ff359c64) )
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 )			/* ADPCM Samples */
		ROM_LOAD( "tp030_2.bin", 0x00000, 0x40000, CRC(276146f5) SHA1(bf11d1f6782cefcad77d52af4f7e6054a8f93440) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_snowbro2 = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x080000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_WORD_SWAP( "pro-4", 0x000000, 0x080000, CRC(4c7ee341) SHA1(ad46c605a38565d0148daac301be4e4b72302fe7) )
	
		ROM_REGION( 0x300000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "rom2-l", 0x000000, 0x100000, CRC(e9d366a9) SHA1(e87e3966fce3395324b90db6c134b3345104c04b) )
		ROM_LOAD( "rom2-h", 0x100000, 0x080000, CRC(9aab7a62) SHA1(611f6a15fdbac5d3063426a365538c1482e996bf) )
		ROM_LOAD( "rom3-l", 0x180000, 0x100000, CRC(eb06e332) SHA1(7cd597bfffc153d178530c0f0903bebd751c9dd1) )
		ROM_LOAD( "rom3-h", 0x280000, 0x080000, CRC(df4a952a) SHA1(b76af61c8437caca573ff1312832898666a611aa) )
	
		ROM_REGION( 0x80000, REGION_SOUND1, 0 )			/* ADPCM Samples */
		ROM_LOAD( "rom4", 0x00000, 0x80000, CRC(638f341e) SHA1(aa3fca25f099339ece1878ea730c5e9f18ec4823) )
	ROM_END(); }}; 
	
	/* -------------------------- Raizing games ------------------------- */
	
	static RomLoadHandlerPtr rom_sstriker = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x080000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_WORD_SWAP( "ra-ma-01.01", 0x000000, 0x080000, CRC(92259f84) SHA1(127e62e407d95efd360bfe2cac9577f326abf6ef) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )			/* Sound Z80 code */
		ROM_LOAD( "ra_ma_01.02", 0x00000, 0x10000, CRC(eabfa46d) SHA1(402c99ebf88f9025f74f0a28ced22b7882a65eb3) )
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "ra_ma_01.03",  0x000000, 0x100000, CRC(54e2bd95) SHA1(341359dd46152615675bb90e8a184216c8feebff) )
		ROM_LOAD( "ra_ma_01.04",  0x100000, 0x100000, CRC(21cd378f) SHA1(e1695bccec949d18b1c03e9c42dca384554b0d7c) )
	
		ROM_REGION( 0x008000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "ra-ma-01.05",  0x000000, 0x008000, CRC(88b58841) SHA1(1d16b538c11a291bd1f46a510bfbd6259b45a0b5) )
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 )			/* ADPCM Samples */
		ROM_LOAD( "ra_ma_01.06", 0x00000, 0x40000, CRC(6edb2ab8) SHA1(e3032e8eda2686f30df4b7a088c5a4d4d45782ed) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_mahoudai = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x080000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_WORD_SWAP( "ra_ma_01.01", 0x000000, 0x080000, CRC(970ccc5c) SHA1(c87cab83bde0284e631f02e50068407fee81d941) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )			/* Sound Z80 code */
		ROM_LOAD( "ra_ma_01.02", 0x00000, 0x10000, CRC(eabfa46d) SHA1(402c99ebf88f9025f74f0a28ced22b7882a65eb3) )
	
		ROM_REGION( 0x200000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "ra_ma_01.03",  0x000000, 0x100000, CRC(54e2bd95) SHA1(341359dd46152615675bb90e8a184216c8feebff) )
		ROM_LOAD( "ra_ma_01.04",  0x100000, 0x100000, CRC(21cd378f) SHA1(e1695bccec949d18b1c03e9c42dca384554b0d7c) )
	
		ROM_REGION( 0x008000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "ra_ma_01.05",  0x000000, 0x008000, CRC(c00d1e80) SHA1(53e64c4c0c6309130b37597d13b44a9e95b717d8) )
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 )			/* ADPCM Samples */
		ROM_LOAD( "ra_ma_01.06", 0x00000, 0x40000, CRC(6edb2ab8) SHA1(e3032e8eda2686f30df4b7a088c5a4d4d45782ed) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_kingdmgp = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x100000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_BYTE( "ma02rom1.bin", 0x000000, 0x080000, CRC(a678b149) SHA1(8c1a631e023dbba0a3fa6cd1b7d10dec1663213a) )
		ROM_LOAD16_BYTE( "ma02rom0.bin", 0x000001, 0x080000, CRC(f226a212) SHA1(526acf3d05fdc88054a772fbea3de2af532bf3d2) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )			/* Sound Z80 code */
		ROM_LOAD( "ma02rom2.bin", 0x00000, 0x10000, CRC(dde8a57e) SHA1(f522a3f17e229c71512464349760a9e27778bf6a) )
	
		ROM_REGION( 0x400000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "ma02rom3.bin",  0x000000, 0x200000, CRC(0e797142) SHA1(a480ccd151e49b886d3175a6deff56e1f2c26c3e) )
		ROM_LOAD( "ma02rom4.bin",  0x200000, 0x200000, CRC(72a6fa53) SHA1(ce92e65205b84361cfb90305a61e9541b5c4dc2f) )
	
		ROM_REGION( 0x008000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "ma02rom5.eng",  0x000000, 0x008000, CRC(8c28460b) SHA1(0aed170762f6044896a7e608df60bbd37c583a71) )
	
		ROM_REGION( 0x80000, REGION_SOUND1, 0 )			/* ADPCM Samples */
		ROM_LOAD( "ma02rom6.bin", 0x00000, 0x80000, CRC(199e7cae) SHA1(0f5e13cc8ec42c80bb4bbff90aba29cdb15213d4) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_shippumd = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x100000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_BYTE( "ma02rom1.bin", 0x000000, 0x080000, CRC(a678b149) SHA1(8c1a631e023dbba0a3fa6cd1b7d10dec1663213a) )
		ROM_LOAD16_BYTE( "ma02rom0.bin", 0x000001, 0x080000, CRC(f226a212) SHA1(526acf3d05fdc88054a772fbea3de2af532bf3d2) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )			/* Sound Z80 code */
		ROM_LOAD( "ma02rom2.bin", 0x00000, 0x10000, CRC(dde8a57e) SHA1(f522a3f17e229c71512464349760a9e27778bf6a) )
	
		ROM_REGION( 0x400000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "ma02rom3.bin",  0x000000, 0x200000, CRC(0e797142) SHA1(a480ccd151e49b886d3175a6deff56e1f2c26c3e) )
		ROM_LOAD( "ma02rom4.bin",  0x200000, 0x200000, CRC(72a6fa53) SHA1(ce92e65205b84361cfb90305a61e9541b5c4dc2f) )
	
		ROM_REGION( 0x008000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "ma02rom5.bin",  0x000000, 0x008000, CRC(116ae559) SHA1(4cc2d2a23cc0aefd457111b7990e47184e79204c) )
	
		ROM_REGION( 0x80000, REGION_SOUND1, 0 )			/* ADPCM Samples */
		ROM_LOAD( "ma02rom6.bin", 0x00000, 0x80000, CRC(199e7cae) SHA1(0f5e13cc8ec42c80bb4bbff90aba29cdb15213d4) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_battleg = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x100000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_BYTE( "u123", 0x000000, 0x080000, CRC(88a4e66a) SHA1(ca97e564eed0c5e028b937312e55da56400d5c8c) )
		ROM_LOAD16_BYTE( "u65",  0x000001, 0x080000, CRC(5dea32a3) SHA1(59df6689e3eb5ea9e49a758604d21a64c65ca14d) )
	
		ROM_REGION( 0x28000, REGION_CPU2, 0 )			/* Sound Z80 code + bank */
		ROM_LOAD( "snd.bin", 0x00000, 0x08000, CRC(68632952) SHA1(fb834db83157948e2b420b6051102a9c6ac3969b) )
		ROM_CONTINUE(        0x10000, 0x18000 )
	
		ROM_REGION( 0x800000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "rom4.bin",  0x000000, 0x200000, CRC(b333d81f) SHA1(5481465f1304334fd55798be2f44324c57c2dbcb) )
		ROM_LOAD( "rom3.bin",  0x200000, 0x200000, CRC(51b9ebfb) SHA1(30e0c326f5175aa436df8dba08f6f4e08130b92f) )
		ROM_LOAD( "rom2.bin",  0x400000, 0x200000, CRC(b330e5e2) SHA1(5d48e9d56f99d093b6390e0af1609fd796df2d35) )
		ROM_LOAD( "rom1.bin",  0x600000, 0x200000, CRC(7eafdd70) SHA1(7c8da8e86c3f9491719b1d7d5d285568d7614f38) )
	
		ROM_REGION( 0x010000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "text.u81", 0x00000, 0x08000, CRC(e67fd534) SHA1(987d0edffc2c243a13d4567319ea3d185eaadbf8) )
	
		ROM_REGION( 0x140000, REGION_SOUND1, 0 )		/* ADPCM Samples */
		ROM_LOAD( "rom5.bin", 0x040000, 0x100000, CRC(f6d49863) SHA1(3a3c354852adad06e8a051511abfab7606bce382) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_battlega = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x100000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_BYTE( "prg0.bin", 0x000000, 0x080000, CRC(f80c2fc2) SHA1(a9aac5c7f5439b6fe8d1b3db1fb02a27cc28fdf6) )
		ROM_LOAD16_BYTE( "prg1.bin", 0x000001, 0x080000, CRC(2ccfdd1e) SHA1(7a9f11f851854f3f8389b9c3c0906ebb8dc28712) )
	
		ROM_REGION( 0x28000, REGION_CPU2, 0 )			/* Sound Z80 code + bank */
		ROM_LOAD( "snd.bin", 0x00000, 0x08000, CRC(68632952) SHA1(fb834db83157948e2b420b6051102a9c6ac3969b) )
		ROM_CONTINUE(        0x10000, 0x18000 )
	
		ROM_REGION( 0x800000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "rom4.bin",  0x000000, 0x200000, CRC(b333d81f) SHA1(5481465f1304334fd55798be2f44324c57c2dbcb) )
		ROM_LOAD( "rom3.bin",  0x200000, 0x200000, CRC(51b9ebfb) SHA1(30e0c326f5175aa436df8dba08f6f4e08130b92f) )
		ROM_LOAD( "rom2.bin",  0x400000, 0x200000, CRC(b330e5e2) SHA1(5d48e9d56f99d093b6390e0af1609fd796df2d35) )
		ROM_LOAD( "rom1.bin",  0x600000, 0x200000, CRC(7eafdd70) SHA1(7c8da8e86c3f9491719b1d7d5d285568d7614f38) )
	
		ROM_REGION( 0x010000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "text.u81", 0x00000, 0x08000, CRC(e67fd534) SHA1(987d0edffc2c243a13d4567319ea3d185eaadbf8) )
	
		ROM_REGION( 0x140000, REGION_SOUND1, 0 )		/* ADPCM Samples */
		ROM_LOAD( "rom5.bin", 0x040000, 0x100000, CRC(f6d49863) SHA1(3a3c354852adad06e8a051511abfab7606bce382) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_battlegb = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x100000, REGION_CPU1, 0 )			/* Main 68K code */
		ROM_LOAD16_BYTE( "prg_0.bin", 0x000000, 0x080000, CRC(951ecc07) SHA1(a82e4b59e4a974566e59f3ab2fbae1aec7d88a2b) )
		ROM_LOAD16_BYTE( "prg_1.bin", 0x000001, 0x080000, CRC(729a60c6) SHA1(cb6f5d138bb82c32910f42d8ee16fa573a23cef3) )
	
		ROM_REGION( 0x28000, REGION_CPU2, 0 )			/* Sound Z80 code + bank */
		ROM_LOAD( "snd.bin", 0x00000, 0x08000, CRC(68632952) SHA1(fb834db83157948e2b420b6051102a9c6ac3969b) )
		ROM_CONTINUE(        0x10000, 0x18000 )
	
		ROM_REGION( 0x800000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "rom4.bin",  0x000000, 0x200000, CRC(b333d81f) SHA1(5481465f1304334fd55798be2f44324c57c2dbcb) )
		ROM_LOAD( "rom3.bin",  0x200000, 0x200000, CRC(51b9ebfb) SHA1(30e0c326f5175aa436df8dba08f6f4e08130b92f) )
		ROM_LOAD( "rom2.bin",  0x400000, 0x200000, CRC(b330e5e2) SHA1(5d48e9d56f99d093b6390e0af1609fd796df2d35) )
		ROM_LOAD( "rom1.bin",  0x600000, 0x200000, CRC(7eafdd70) SHA1(7c8da8e86c3f9491719b1d7d5d285568d7614f38) )
	
		ROM_REGION( 0x010000, REGION_GFX2, ROMREGION_DISPOSE )
		ROM_LOAD( "text.u81", 0x00000, 0x08000, CRC(e67fd534) SHA1(987d0edffc2c243a13d4567319ea3d185eaadbf8) )
	
		ROM_REGION( 0x140000, REGION_SOUND1, 0 )		/* ADPCM Samples */
		ROM_LOAD( "rom5.bin", 0x040000, 0x100000, CRC(f6d49863) SHA1(3a3c354852adad06e8a051511abfab7606bce382) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_batrider = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x200000, REGION_CPU1, 0 )			/* Main 68k code */
		ROM_LOAD16_BYTE( "prg0b.u22", 0x000000, 0x080000, CRC(4f3fc729) SHA1(b32d51c254741b82171a86c271679522a7aefd34) )
		ROM_LOAD16_BYTE( "prg1b.u23", 0x000001, 0x080000, CRC(8e70b492) SHA1(f84f2039826ae815afb058d71c1dbd190f9d524d) )
		ROM_LOAD16_BYTE( "prg2.u21" , 0x100000, 0x080000, CRC(bdaa5fbf) SHA1(abd72ac633c0c8e7b4b1d7902c0d6e014ba995fe) )
		ROM_LOAD16_BYTE( "prg3.u24" , 0x100001, 0x080000, CRC(7aa9f941) SHA1(99bdbad7a96d461073b06a53c50fc57c2fd6fc6d) )
	
		ROM_REGION( 0x48000, REGION_CPU2, 0 )			/* Sound Z80 code + bank */
		ROM_LOAD( "snd.u77", 0x00000, 0x08000, CRC(56682696) SHA1(a372450d9a6d535123dfc31d8116074b168ab646) )
		ROM_CONTINUE(        0x10000, 0x38000 )
	
		ROM_REGION( 0x1000000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "rom-1.bin", 0x000000, 0x400000, CRC(0df69ca2) SHA1(49670347ebd7e1067ff988cf842b275b7ee7b5f7) )
		ROM_LOAD( "rom-3.bin", 0x400000, 0x400000, CRC(60167d38) SHA1(fd2429808c59ef51fd5f5db84ea89a8dc504186e) )
		ROM_LOAD( "rom-2.bin", 0x800000, 0x400000, CRC(1bfea593) SHA1(ce06dc3097ae56b0df56d104bbf7efc9b5d968d4) )
		ROM_LOAD( "rom-4.bin", 0xc00000, 0x400000, CRC(bee03c94) SHA1(5bc1e6769c42857c03456426b502fcb86a114f19) )
	
		ROM_REGION( 0x140000, REGION_SOUND1, 0 )		/* ADPCM Samples 1 */
		ROM_LOAD( "rom-5.bin", 0x040000, 0x100000, CRC(4274daf6) SHA1(85557b4707d529e5914f03c7a856864f5c24950e) )
	
		ROM_REGION( 0x140000, REGION_SOUND2, 0 )		/* ADPCM Samples 2 */
		ROM_LOAD( "rom-6.bin", 0x040000, 0x100000, CRC(2a1c2426) SHA1(8abc3688ffc5ebb94b8d5118d4fa0908f07fe791) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_batridra = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x200000, REGION_CPU1, 0 )			/* Main 68k code */
		ROM_LOAD16_BYTE( "prg0.bin", 0x000000, 0x080000, CRC(f93ea27c) SHA1(41023c2ee1efd70b5aa9c70e1ddd9e5c3d51d68a) )
		ROM_LOAD16_BYTE( "prg1.bin", 0x000001, 0x080000, CRC(8ae7f592) SHA1(8a20ebf85eca621f578d2302c3a3988647b077a7) )
		ROM_LOAD16_BYTE( "prg2.u21", 0x100000, 0x080000, CRC(bdaa5fbf) SHA1(abd72ac633c0c8e7b4b1d7902c0d6e014ba995fe) )
		ROM_LOAD16_BYTE( "prg3.u24", 0x100001, 0x080000, CRC(7aa9f941) SHA1(99bdbad7a96d461073b06a53c50fc57c2fd6fc6d) )
	
		ROM_REGION( 0x48000, REGION_CPU2, 0 )			/* Sound Z80 code + bank */
		ROM_LOAD( "snd.u77", 0x00000, 0x08000, CRC(56682696) SHA1(a372450d9a6d535123dfc31d8116074b168ab646) )
		ROM_CONTINUE(        0x10000, 0x38000 )
	
		ROM_REGION( 0x1000000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "rom-1.bin", 0x000000, 0x400000, CRC(0df69ca2) SHA1(49670347ebd7e1067ff988cf842b275b7ee7b5f7) )
		ROM_LOAD( "rom-3.bin", 0x400000, 0x400000, CRC(60167d38) SHA1(fd2429808c59ef51fd5f5db84ea89a8dc504186e) )
		ROM_LOAD( "rom-2.bin", 0x800000, 0x400000, CRC(1bfea593) SHA1(ce06dc3097ae56b0df56d104bbf7efc9b5d968d4) )
		ROM_LOAD( "rom-4.bin", 0xc00000, 0x400000, CRC(bee03c94) SHA1(5bc1e6769c42857c03456426b502fcb86a114f19) )
	
		ROM_REGION( 0x140000, REGION_SOUND1, 0 )		/* ADPCM Samples 1 */
		ROM_LOAD( "rom-5.bin", 0x040000, 0x100000, CRC(4274daf6) SHA1(85557b4707d529e5914f03c7a856864f5c24950e) )
	
		ROM_REGION( 0x140000, REGION_SOUND2, 0 )		/* ADPCM Samples 2 */
		ROM_LOAD( "rom-6.bin", 0x040000, 0x100000, CRC(2a1c2426) SHA1(8abc3688ffc5ebb94b8d5118d4fa0908f07fe791) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_batridrk = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x200000, REGION_CPU1, 0 )			/* Main 68k code */
		ROM_LOAD16_BYTE( "prg0.u22", 0x000000, 0x080000, CRC(d9d8c907) SHA1(69c197f2a41f288913f042de9eb8274c0df3ac27) )
		ROM_LOAD16_BYTE( "prg1.u23", 0x000001, 0x080000, CRC(8e70b492) SHA1(f84f2039826ae815afb058d71c1dbd190f9d524d) )
		ROM_LOAD16_BYTE( "prg2.u21", 0x100000, 0x080000, CRC(bdaa5fbf) SHA1(abd72ac633c0c8e7b4b1d7902c0d6e014ba995fe) )
		ROM_LOAD16_BYTE( "prg3.u24", 0x100001, 0x080000, CRC(7aa9f941) SHA1(99bdbad7a96d461073b06a53c50fc57c2fd6fc6d) )
	
		ROM_REGION( 0x48000, REGION_CPU2, 0 )			/* Sound Z80 code + bank */
		ROM_LOAD( "snd.u77", 0x00000, 0x08000, CRC(56682696) SHA1(a372450d9a6d535123dfc31d8116074b168ab646) )
		ROM_CONTINUE(        0x10000, 0x38000 )
	
		ROM_REGION( 0x1000000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "rom-1.bin", 0x000000, 0x400000, CRC(0df69ca2) SHA1(49670347ebd7e1067ff988cf842b275b7ee7b5f7) )
		ROM_LOAD( "rom-3.bin", 0x400000, 0x400000, CRC(60167d38) SHA1(fd2429808c59ef51fd5f5db84ea89a8dc504186e) )
		ROM_LOAD( "rom-2.bin", 0x800000, 0x400000, CRC(1bfea593) SHA1(ce06dc3097ae56b0df56d104bbf7efc9b5d968d4) )
		ROM_LOAD( "rom-4.bin", 0xc00000, 0x400000, CRC(bee03c94) SHA1(5bc1e6769c42857c03456426b502fcb86a114f19) )
	
		ROM_REGION( 0x140000, REGION_SOUND1, 0 )		/* ADPCM Samples 1 */
		ROM_LOAD( "rom-5.bin", 0x040000, 0x100000, CRC(4274daf6) SHA1(85557b4707d529e5914f03c7a856864f5c24950e) )
	
		ROM_REGION( 0x140000, REGION_SOUND2, 0 )		/* ADPCM Samples 2 */
		ROM_LOAD( "rom-6.bin", 0x040000, 0x100000, CRC(2a1c2426) SHA1(8abc3688ffc5ebb94b8d5118d4fa0908f07fe791) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_bbakraid = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x200000, REGION_CPU1, 0 )			/* Main 68k code */
		ROM_LOAD16_BYTE( "prg0u022.bin", 0x000000, 0x080000, CRC(0dd59512) SHA1(c6a4e6aa49c6ac3b04ae62a0a4cc8084ae048381) )
		ROM_LOAD16_BYTE( "prg1u023.bin", 0x000001, 0x080000, CRC(fecde223) SHA1(eb5ac0eda49b4b0f3d25d8a8bb356e77a453d3a7) )
		ROM_LOAD16_BYTE( "prg2u021.bin", 0x100000, 0x080000, CRC(ffba8656) SHA1(6526bb65fad3384de3f301a7d1095cbf03757433) )
		ROM_LOAD16_BYTE( "prg3u024.bin", 0x100001, 0x080000, CRC(834b8ad6) SHA1(0dd6223bb0749819ad29811eeb04fd08d937abb0) )
	
		ROM_REGION( 0x28000, REGION_CPU2, 0 )			/* Sound Z80 code */
		ROM_LOAD( "sndu0720.bin", 0x00000, 0x08000, CRC(e62ab246) SHA1(00d23689dd423ecd4024c58b5903d16e890f1dff) )
		ROM_CONTINUE(             0x10000, 0x18000 )
	
		ROM_REGION( 0x1000000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "gfxu0510.bin", 0x000000, 0x400000, CRC(9cca3446) SHA1(1123f8b8bfbe59a2c572cdf61f1ad27ff37f0f0d) )
		ROM_LOAD( "gfxu0512.bin", 0x400000, 0x400000, CRC(a2a281d5) SHA1(d9a6623f9433ad682223f9780c26cd1523ebc5c5) )
		ROM_LOAD( "gfxu0511.bin", 0x800000, 0x400000, CRC(e16472c0) SHA1(6068d679a8b3b65e05acd58a7ce9ead90177049f) )
		ROM_LOAD( "gfxu0513.bin", 0xc00000, 0x400000, CRC(8bb635a0) SHA1(9064f1a2d8bb88ddbca702fb8556d0dfe6a5cadc) )
	
		ROM_REGION( 0x0c00000, REGION_SOUND1, 0 )	/* YMZ280B Samples */
		ROM_LOAD( "rom6.829", 0x000000, 0x400000, CRC(8848b4a0) SHA1(e0dce136c5d5a4c1a92b863e57848cd5927d06f1) )
		ROM_LOAD( "rom7.830", 0x400000, 0x400000, CRC(d6224267) SHA1(5c9b7b13effbef9f707811f84bfe50ca85e605e3) )
		ROM_LOAD( "rom8.831", 0x800000, 0x400000, CRC(a101dfb0) SHA1(4b729b0d562e09df35438e9e6b457b8de2690a6e) )
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_bbakradu = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 0x200000, REGION_CPU1, 0 )			/* Main 68k code */
		ROM_LOAD16_BYTE( "prg0u022.new", 0x000000, 0x080000, CRC(fa8d38d3) SHA1(aba91d87a8a62d3fe1139b4437b16e2f844264ad) )
		ROM_LOAD16_BYTE( "prg1u023.new", 0x000001, 0x080000, CRC(4ae9aa64) SHA1(45fdf72141c4c9f24a38d4218c65874799b9c868) )
		ROM_LOAD16_BYTE( "prg2u021.bin", 0x100000, 0x080000, CRC(ffba8656) SHA1(6526bb65fad3384de3f301a7d1095cbf03757433) )
		ROM_LOAD16_BYTE( "prg3u024.bin", 0x100001, 0x080000, CRC(834b8ad6) SHA1(0dd6223bb0749819ad29811eeb04fd08d937abb0) )
	
		ROM_REGION( 0x28000, REGION_CPU2, 0 )			/* Sound Z80 code */
		ROM_LOAD( "sndu0720.bin", 0x00000, 0x08000, CRC(e62ab246) SHA1(00d23689dd423ecd4024c58b5903d16e890f1dff) )
		ROM_CONTINUE(             0x10000, 0x18000 )
	
		ROM_REGION( 0x1000000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "gfxu0510.bin", 0x000000, 0x400000, CRC(9cca3446) SHA1(1123f8b8bfbe59a2c572cdf61f1ad27ff37f0f0d) )
		ROM_LOAD( "gfxu0512.bin", 0x400000, 0x400000, CRC(a2a281d5) SHA1(d9a6623f9433ad682223f9780c26cd1523ebc5c5) )
		ROM_LOAD( "gfxu0511.bin", 0x800000, 0x400000, CRC(e16472c0) SHA1(6068d679a8b3b65e05acd58a7ce9ead90177049f) )
		ROM_LOAD( "gfxu0513.bin", 0xc00000, 0x400000, CRC(8bb635a0) SHA1(9064f1a2d8bb88ddbca702fb8556d0dfe6a5cadc) )
	
		ROM_REGION( 0x0c00000, REGION_SOUND1, 0 )	/* YMZ280B Samples */
		ROM_LOAD( "rom6.829", 0x000000, 0x400000, CRC(8848b4a0) SHA1(e0dce136c5d5a4c1a92b863e57848cd5927d06f1) )
		ROM_LOAD( "rom7.830", 0x400000, 0x400000, CRC(d6224267) SHA1(5c9b7b13effbef9f707811f84bfe50ca85e605e3) )
		ROM_LOAD( "rom8.831", 0x800000, 0x400000, CRC(a101dfb0) SHA1(4b729b0d562e09df35438e9e6b457b8de2690a6e) )
	ROM_END(); }}; 
	
	
	
	/* The following is in order of Toaplan Board/game numbers */
	/* See list at top of file */
	/* Whoopee machine to be changed to Teki Paki when (if) HD647180 is dumped */
	/* Whoopee  init   to be changed to T2_Z180   when (if) HD647180 is dumped */
	
	/*   ( YEAR  NAME      PARENT    MACHINE   INPUT     INIT      MONITOR COMPANY    FULLNAME     FLAGS ) */
	public static GameDriver driver_tekipaki	   = new GameDriver("1991"	,"tekipaki"	,"toaplan2.java"	,rom_tekipaki,null	,machine_driver_tekipaki	,input_ports_tekipaki	,init_T2_Z180	,ROT0,   "Toaplan", "Teki Paki", GAME_NO_SOUND )
	public static GameDriver driver_ghox	   = new GameDriver("1991"	,"ghox"	,"toaplan2.java"	,rom_ghox,null	,machine_driver_ghox	,input_ports_ghox	,init_T2_Z180	,ROT270, "Toaplan", "Ghox", GAME_NO_SOUND )
	public static GameDriver driver_dogyuun	   = new GameDriver("1992"	,"dogyuun"	,"toaplan2.java"	,rom_dogyuun,null	,machine_driver_dogyuun	,input_ports_dogyuun	,init_T2_Zx80	,ROT270, "Toaplan", "Dogyuun", GAME_NO_SOUND )
	public static GameDriver driver_kbash	   = new GameDriver("1993"	,"kbash"	,"toaplan2.java"	,rom_kbash,null	,machine_driver_kbash	,input_ports_kbash	,init_T2_Zx80	,ROT0,   "Toaplan", "Knuckle Bash", GAME_NO_SOUND )
	public static GameDriver driver_truxton2	   = new GameDriver("1992"	,"truxton2"	,"toaplan2.java"	,rom_truxton2,null	,machine_driver_truxton2	,input_ports_truxton2	,init_T2_noZ80	,ROT270, "Toaplan", "Truxton II / Tatsujin II / Tatsujin Oh (Japan)" )
	public static GameDriver driver_pipibibs	   = new GameDriver("1991"	,"pipibibs"	,"toaplan2.java"	,rom_pipibibs,null	,machine_driver_pipibibs	,input_ports_pipibibs	,init_T2_Z80	,ROT0,   "Toaplan", "Pipi & Bibis / Whoopee!!" )
	public static GameDriver driver_whoopee	   = new GameDriver("1991"	,"whoopee"	,"toaplan2.java"	,rom_whoopee,driver_pipibibs	,machine_driver_whoopee	,input_ports_whoopee	,init_T2_Z80	,ROT0,   "Toaplan", "Whoopee!! / Pipi & Bibis" )
	public static GameDriver driver_pipibibi	   = new GameDriver("1991"	,"pipibibi"	,"toaplan2.java"	,rom_pipibibi,driver_pipibibs	,machine_driver_pipibibi	,input_ports_pipibibi	,init_pipibibi	,ROT0,   "[Toaplan] Ryouta Kikaku", "Pipi & Bibis / Whoopee!! (bootleg ?)" )
	public static GameDriver driver_fixeight	   = new GameDriver("1992"	,"fixeight"	,"toaplan2.java"	,rom_fixeight,null	,machine_driver_fixeight	,input_ports_fixeight	,init_fixeight	,ROT270, "Toaplan", "FixEight", GAME_NOT_WORKING )
	public static GameDriver driver_grindstm	   = new GameDriver("1992"	,"grindstm"	,"toaplan2.java"	,rom_grindstm,driver_vfive	,machine_driver_vfive	,input_ports_grindstm	,init_T2_Zx80	,ROT270, "Toaplan", "Grind Stormer", GAME_NO_SOUND )
	public static GameDriver driver_grindsta	   = new GameDriver("1992"	,"grindsta"	,"toaplan2.java"	,rom_grindsta,driver_vfive	,machine_driver_vfive	,input_ports_grindstm	,init_T2_Zx80	,ROT270, "Toaplan", "Grind Stormer (older set)", GAME_NO_SOUND )
	public static GameDriver driver_vfive	   = new GameDriver("1993"	,"vfive"	,"toaplan2.java"	,rom_vfive,null	,machine_driver_vfive	,input_ports_vfive	,init_T2_Zx80	,ROT270, "Toaplan", "V-Five (Japan)", GAME_NO_SOUND )
	public static GameDriver driver_batsugun	   = new GameDriver("1993"	,"batsugun"	,"toaplan2.java"	,rom_batsugun,null	,machine_driver_batsugun	,input_ports_batsugun	,init_T2_Zx80	,ROT270, "Toaplan", "Batsugun", GAME_NO_SOUND | GAME_IMPERFECT_GRAPHICS )
	public static GameDriver driver_batugnsp	   = new GameDriver("1993"	,"batugnsp"	,"toaplan2.java"	,rom_batugnsp,driver_batsugun	,machine_driver_batsugun	,input_ports_batsugun	,init_T2_Zx80	,ROT270, "Toaplan", "Batsugun (Special Ver.)", GAME_NO_SOUND | GAME_IMPERFECT_GRAPHICS )
	public static GameDriver driver_snowbro2	   = new GameDriver("1994"	,"snowbro2"	,"toaplan2.java"	,rom_snowbro2,null	,machine_driver_snowbro2	,input_ports_snowbro2	,init_T2_noZ80	,ROT0,   "[Toaplan] Hanafram", "Snow Bros. 2 - With New Elves / Otenki Paradise" )
	public static GameDriver driver_mahoudai	   = new GameDriver("1993"	,"mahoudai"	,"toaplan2.java"	,rom_mahoudai,null	,machine_driver_mahoudai	,input_ports_mahoudai	,init_T2_Z80	,ROT270, "Raizing (Able license)", "Mahou Daisakusen (Japan)" )
	public static GameDriver driver_sstriker	   = new GameDriver("1993"	,"sstriker"	,"toaplan2.java"	,rom_sstriker,driver_mahoudai	,machine_driver_mahoudai	,input_ports_sstriker	,init_T2_Z80	,ROT270, "Raizing", "Sorcer Striker (World)" ) // from korean board
	public static GameDriver driver_shippumd	   = new GameDriver("1994"	,"shippumd"	,"toaplan2.java"	,rom_shippumd,null	,machine_driver_shippumd	,input_ports_shippumd	,init_T2_Z80	,ROT270, "Raizing / Eighting", "Shippu Mahou Daisakusen (Japan)" )
	public static GameDriver driver_kingdmgp	   = new GameDriver("1994"	,"kingdmgp"	,"toaplan2.java"	,rom_kingdmgp,driver_shippumd	,machine_driver_shippumd	,input_ports_kingdmgp	,init_T2_Z80	,ROT270, "Raizing / Eighting", "Kingdom Grandprix (World)" ) // from korean board, missing letters on credits screen but this is correct
	public static GameDriver driver_battleg	   = new GameDriver("1996"	,"battleg"	,"toaplan2.java"	,rom_battleg,null	,machine_driver_battleg	,input_ports_battleg	,init_battleg	,ROT270, "Raizing / Eighting", "Battle Garegga - Type 2 (Denmark / China) (Tue Apr 2 1996)" )
	public static GameDriver driver_battlega	   = new GameDriver("1996"	,"battlega"	,"toaplan2.java"	,rom_battlega,driver_battleg	,machine_driver_battleg	,input_ports_battlega	,init_battleg	,ROT270, "Raizing / Eighting", "Battle Garegga (Europe / USA / Japan / Asia) (Sat Feb 3 1996)" )
	public static GameDriver driver_battlegb	   = new GameDriver("1996"	,"battlegb"	,"toaplan2.java"	,rom_battlegb,driver_battleg	,machine_driver_battleg	,input_ports_battlegb	,init_battleg	,ROT270, "Raizing / Eighting", "Battle Garegga (Austria / Hong Kong) (Sat Mar 2 1996)" ) // displays New Version when set to HK
	public static GameDriver driver_batrider	   = new GameDriver("1998"	,"batrider"	,"toaplan2.java"	,rom_batrider,null	,machine_driver_batrider	,input_ports_batrider	,init_battleg	,ROT270, "Raizing / Eighting", "Armed Police Batrider (Japan, version B)" )
	public static GameDriver driver_batridra	   = new GameDriver("1998"	,"batridra"	,"toaplan2.java"	,rom_batridra,driver_batrider	,machine_driver_batrider	,input_ports_batrider	,init_battleg	,ROT270, "Raizing / Eighting", "Armed Police Batrider (Japan, version A)" )
	public static GameDriver driver_batridrk	   = new GameDriver("1998"	,"batridrk"	,"toaplan2.java"	,rom_batridrk,driver_batrider	,machine_driver_batrider	,input_ports_batrider	,init_battleg	,ROT270, "Raizing / Eighting", "Armed Police Batrider (Korea, version B)" )
	public static GameDriver driver_bbakraid	   = new GameDriver("1999"	,"bbakraid"	,"toaplan2.java"	,rom_bbakraid,null	,machine_driver_bbakraid	,input_ports_bbakraid	,init_bbakraid	,ROT270, "Eighting", "Battle Bakraid (Japan) (Wed Apr 7 1999)")
	public static GameDriver driver_bbakradu	   = new GameDriver("1999"	,"bbakradu"	,"toaplan2.java"	,rom_bbakradu,driver_bbakraid	,machine_driver_bbakraid	,input_ports_bbakraid	,init_bbakrada	,ROT270, "Eighting", "Battle Bakraid - Unlimited version (Japan) (Tue Jun 8 1999)")
}
