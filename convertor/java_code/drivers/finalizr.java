/***************************************************************************

Finalizer (GX523) (c) 1985 Konami

driver by Nicola Salmoria

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package drivers;

public class finalizr
{
	
	
	void konami1_decode(void);
	
	static unsigned char *finalizr_interrupt_enable;
	
	int finalizr_T1_line;
	
	
	
	
	public static InterruptHandlerPtr finalizr_interrupt = new InterruptHandlerPtr() {public void handler(){
		if (cpu_getiloops() == 0)
		{
			if (*finalizr_interrupt_enable & 2)
				cpu_set_irq_line(0, M6809_IRQ_LINE, HOLD_LINE);
		}
		else if (cpu_getiloops() % 2)
		{
			if (*finalizr_interrupt_enable & 1)
				cpu_set_irq_line(0, IRQ_LINE_NMI, PULSE_LINE);
		}
	} };
	
	public static WriteHandlerPtr finalizr_coin_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		coin_counter_w(0,data & 0x01);
		coin_counter_w(1,data & 0x02);
	} };
	
	public static WriteHandlerPtr finalizr_i8039_irq_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		cpu_set_irq_line(1, 0, ASSERT_LINE);
	} };
	
	public static WriteHandlerPtr i8039_irqen_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		/*  bit 0x80 goes active low, indicating that the
			external IRQ being serviced is complete
			bit 0x40 goes active high to enable the DAC ?
		*/
	
		if ((data & 0x80) == 0)
			cpu_set_irq_line(1, 0, CLEAR_LINE);
	} };
	
	public static ReadHandlerPtr i8039_T1_r  = new ReadHandlerPtr() { public int handler(int offset){
		/*  I suspect the clock-out from the I8039 T0 line should be connected
			here (See the i8039_T0_w handler below).
			The frequency of this clock cannot be greater than I8039 CLKIN / 45
			Accounting for the I8039 input clock, and internal/external divisors
			the frequency here should be 192KHz (I8039 CLKIN / 48)
	
			Here we apply a positive edge every 3.2 reads, to simulate 192KHz
			based on the I8039 main xtal clock input frequency of 9.216MHz
		*/
	
		finalizr_T1_line++;
		if ((finalizr_T1_line % 3) == 0)
		{
			if (finalizr_T1_line == 15)
			{
				finalizr_T1_line = -1;
				return 0;
			}
			return 1;
		}
		return 0;
	} };
	
	public static WriteHandlerPtr i8039_T0_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		/*	This becomes a clock output at a frequency of 3.072MHz (derived
			by internally dividing the main xtal clock input by a factor of 3).
			This output is divided by a factor of 16, then used as a 192KHz
			input clock to the T1 input line.
			The I8039 core currently doesn't support clock out on this pin.
		*/
	} };
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0800, 0x0800, input_port_5_r ),
		new Memory_ReadAddress( 0x0808, 0x0808, input_port_4_r ),
		new Memory_ReadAddress( 0x0810, 0x0810, input_port_0_r ),
		new Memory_ReadAddress( 0x0811, 0x0811, input_port_1_r ),
		new Memory_ReadAddress( 0x0812, 0x0812, input_port_2_r ),
		new Memory_ReadAddress( 0x0813, 0x0813, input_port_3_r ),
		new Memory_ReadAddress( 0x2000, 0x2fff, MRA_RAM ),
		new Memory_ReadAddress( 0x3000, 0x3fff, MRA_RAM ),
		new Memory_ReadAddress( 0x4000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0001, 0x0001, MWA_RAM, finalizr_scroll ),
		new Memory_WriteAddress( 0x0003, 0x0003, finalizr_videoctrl_w ),
		new Memory_WriteAddress( 0x0004, 0x0004, MWA_RAM, finalizr_interrupt_enable ),
	//	new Memory_WriteAddress( 0x0020, 0x003f, MWA_RAM, finalizr_scroll ),
		new Memory_WriteAddress( 0x0818, 0x0818, watchdog_reset_w ),
		new Memory_WriteAddress( 0x0819, 0x0819, finalizr_coin_w ),
		new Memory_WriteAddress( 0x081a, 0x081a, SN76496_0_w ),	/* This address triggers the SN chip to read the data port. */
		new Memory_WriteAddress( 0x081b, 0x081b, MWA_NOP ),		/* Loads the snd command into the snd latch */
		new Memory_WriteAddress( 0x081c, 0x081c, finalizr_i8039_irq_w ),	/* custom sound chip */
		new Memory_WriteAddress( 0x081d, 0x081d, soundlatch_w ),			/* custom sound chip */
		new Memory_WriteAddress( 0x2000, 0x23ff, colorram_w, colorram ),
		new Memory_WriteAddress( 0x2400, 0x27ff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x2800, 0x2bff, MWA_RAM, finalizr_colorram2 ),
		new Memory_WriteAddress( 0x2c00, 0x2fff, MWA_RAM, finalizr_videoram2 ),
		new Memory_WriteAddress( 0x3000, 0x31ff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0x3200, 0x37ff, MWA_RAM ),
		new Memory_WriteAddress( 0x3800, 0x39ff, MWA_RAM, spriteram_2 ),
		new Memory_WriteAddress( 0x3a00, 0x3fff, MWA_RAM ),
		new Memory_WriteAddress( 0x4000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress i8039_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x0fff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress i8039_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x0fff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort i8039_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0xff, soundlatch_r ),
		new IO_ReadPort( I8039_t1, I8039_t1, i8039_T1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort i8039_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( I8039_p1, I8039_p1, DAC_0_data_w ),
		new IO_WritePort( I8039_p2, I8039_p2, i8039_irqen_w ),
		new IO_WritePort( I8039_t0, I8039_t0, i8039_T0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortHandlerPtr input_ports_finalizr = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( finalizr )
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_VBLANK );
	
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x0f, 0x0f, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x02, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0x0f, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x0e, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(    0x0d, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x0b, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x0a, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x09, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0xf0, 0xf0, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x20, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x50, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0xf0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(    0x70, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0xe0, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(    0xd0, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0xb0, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0xa0, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x90, DEF_STR( "1C_7C") );
	/* 	PORT_DIPSETTING(    0x00, "Invalid" );*/
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x03, 0x02, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x03, "2" );
		PORT_DIPSETTING(    0x02, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x00, "7" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x18, 0x18, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x18, "30000 150000" );
		PORT_DIPSETTING(    0x10, "50000 300000" );
		PORT_DIPSETTING(    0x08, "30000" );
		PORT_DIPSETTING(    0x00, "50000" );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "Controls" );
		PORT_DIPSETTING(    0x02, "Single" );
		PORT_DIPSETTING(    0x00, "Dual" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BIT( 0xf0, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	static InputPortHandlerPtr input_ports_finalizb = new InputPortHandlerPtr(){ public void handler() { INPUT_PORTS_START( finalizb )
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_VBLANK );
	
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x0f, 0x0f, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x02, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0x0f, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x0e, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(    0x0d, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x0b, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x0a, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x09, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0xf0, 0xf0, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x20, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x50, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0xf0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(    0x70, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0xe0, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(    0xd0, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0xb0, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0xa0, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x90, DEF_STR( "1C_7C") );
	/* 	PORT_DIPSETTING(    0x00, "Invalid" );*/
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x03, 0x02, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x03, "2" );
		PORT_DIPSETTING(    0x02, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x00, "7" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x18, 0x18, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x18, "20000 100000" );
		PORT_DIPSETTING(    0x10, "30000 150000" );
		PORT_DIPSETTING(    0x08, "20000" );
		PORT_DIPSETTING(    0x00, "30000" );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* DSW */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "Controls" );
		PORT_DIPSETTING(    0x02, "Single" );
		PORT_DIPSETTING(    0x00, "Dual" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BIT( 0xf0, IP_ACTIVE_LOW, IPT_UNKNOWN );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,1),
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 0*4, 1*4, 2*4, 3*4, 4*4, 5*4, 6*4, 7*4 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
		32*8
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,1),
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 0*4, 1*4, 2*4, 3*4, 4*4, 5*4, 6*4, 7*4,
				32*8+0*4, 32*8+1*4, 32*8+2*4, 32*8+3*4, 32*8+4*4, 32*8+5*4, 32*8+6*4, 32*8+7*4 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32,
				16*32, 17*32, 18*32, 19*32, 20*32, 21*32, 22*32, 23*32 },
		32*32
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,        0, 16 ),
		new GfxDecodeInfo( REGION_GFX1, 0, spritelayout,  16*16, 16 ),
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,    16*16, 16 ),  /* to handle 8x8 sprites */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static struct SN76496interface sn76496_interface =
	{
		1,	/* 1 chip */
		{ 18432000/12 },	/* ?? */
		{ 75 }
	};
	
	static DACinterface dac_interface = new DACinterface
	(
		1,
		new int[] { 65 }
	);
	
	
	
	public static MachineHandlerPtr machine_driver_finalizr = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M6809,18432000/6)	/* ??? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(finalizr_interrupt,16)	/* 1 IRQ + 8 NMI (generated by a custom IC) */
	
		MDRV_CPU_ADD(I8039,(18432000/2)/I8039_CLOCK_DIVIDER)	/* 9.216MHz clkin ?? */
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* DAC */
		MDRV_CPU_MEMORY(i8039_readmem,i8039_writemem)
		MDRV_CPU_PORTS(i8039_readport,i8039_writeport)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_REAL_60HZ_VBLANK_DURATION)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(36*8, 32*8)
		MDRV_VISIBLE_AREA(1*8, 35*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(32)
		MDRV_COLORTABLE_LENGTH(2*16*16)
	
		MDRV_PALETTE_INIT(finalizr)
		MDRV_VIDEO_START(finalizr)
		MDRV_VIDEO_UPDATE(finalizr)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SN76496, sn76496_interface)
		MDRV_SOUND_ADD(DAC, dac_interface)
	MACHINE_DRIVER_END();
 }
};
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadHandlerPtr rom_finalizr = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 2*0x10000, REGION_CPU1, 0 )	/* 64k for code + 64k for decrypted opcodes */
		ROM_LOAD( "523k01.9c",    0x4000, 0x4000, CRC(716633cb) SHA1(9c21e608b6a73967688fa6aeb5995c20c1b48c74) )
		ROM_LOAD( "523k02.12c",   0x8000, 0x4000, CRC(1bccc696) SHA1(3c29f4a030e76660b5a25347e042e344b0653343) )
		ROM_LOAD( "523k03.13c",   0xc000, 0x4000, CRC(c48927c6) SHA1(9cf6b285034670370ba0246c33e1fe0a057457e7) )
	
		ROM_REGION( 0x1000, REGION_CPU2, 0 )	/* 8039 */
		ROM_LOAD( "d8749hd.bin",  0x0000, 0x0800, CRC(978dfc33) SHA1(13d24ce577b88bf6ec2e970d36dc67a7ec691c55) )	/* this comes from the bootleg, */
																/* the original has a custom IC */
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD16_BYTE( "523h04.5e",    0x00000, 0x4000, CRC(c056d710) SHA1(3fe0ab7ef3bce7298c2a073d0985c33f9dc40062) )
		ROM_LOAD16_BYTE( "523h07.5f",    0x00001, 0x4000, CRC(50e512ba) SHA1(f916afb9df1872f9de571d20b9045b20d9172eaa) )
		ROM_LOAD16_BYTE( "523h05.6e",    0x08000, 0x4000, CRC(ae0d0f76) SHA1(6dd0119e4ba7ebb32ba1ca6395f80d18f1617ce8) )
		ROM_LOAD16_BYTE( "523h08.6f",    0x08001, 0x4000, CRC(79f44e17) SHA1(cb32edc4df9f2209f13fc258fec4e67ee91badef) )
		ROM_LOAD16_BYTE( "523h06.7e",    0x10000, 0x4000, CRC(d2db9689) SHA1(ceb5913716b4da2ddff2e837ddaa04d91e52f9e1) )
		ROM_LOAD16_BYTE( "523h09.7f",    0x10001, 0x4000, CRC(8896dc85) SHA1(91493c6b69655de482f0c2a0cb3662fc0d1b6e45) )
		/* 18000-1ffff empty */
	
		ROM_REGION( 0x0240, REGION_PROMS, 0 )
		ROM_LOAD( "523h10.2f",    0x0000, 0x0020, CRC(ec15dd15) SHA1(710384b154a9363fdc88edffda252f1d60e000dc) ) /* palette */
		ROM_LOAD( "523h11.3f",    0x0020, 0x0020, CRC(54be2e83) SHA1(3200abc7f2238d62d7204ef57a6daa2df150538d) ) /* palette */
		ROM_LOAD( "523h12.10f",   0x0040, 0x0100, CRC(53166a2a) SHA1(6cdde206036df7176679711f7888d72acee27c8f) ) /* sprites */
		ROM_LOAD( "523h13.11f",   0x0140, 0x0100, CRC(4e0647a0) SHA1(fb87f878456b8b76bb2c028cb890d2a5c1c3e388) ) /* characters */
	ROM_END(); }}; 
	
	static RomLoadHandlerPtr rom_finalizb = new RomLoadHandlerPtr(){ public void handler(){ 
		ROM_REGION( 2*0x10000, REGION_CPU1, 0 )	/* 64k for code + 64k for decrypted opcodes */
		ROM_LOAD( "finalizr.5",   0x4000, 0x8000, CRC(a55e3f14) SHA1(47f6da214b36cc56be547fa4313afcc5572508a2) )
		ROM_LOAD( "finalizr.6",   0xc000, 0x4000, CRC(ce177f6e) SHA1(034cbe0c1e2baf9577741b3c222a8b4a8ac8c919) )
	
		ROM_REGION( 0x1000, REGION_CPU2, 0 )	/* 8039 */
		ROM_LOAD( "d8749hd.bin",  0x0000, 0x0800, CRC(978dfc33) SHA1(13d24ce577b88bf6ec2e970d36dc67a7ec691c55) )
	
		ROM_REGION( 0x20000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD16_BYTE( "523h04.5e",    0x00000, 0x4000, CRC(c056d710) SHA1(3fe0ab7ef3bce7298c2a073d0985c33f9dc40062) )
		ROM_LOAD16_BYTE( "523h07.5f",    0x00001, 0x4000, CRC(50e512ba) SHA1(f916afb9df1872f9de571d20b9045b20d9172eaa) )
		ROM_LOAD16_BYTE( "523h05.6e",    0x08000, 0x4000, CRC(ae0d0f76) SHA1(6dd0119e4ba7ebb32ba1ca6395f80d18f1617ce8) )
		ROM_LOAD16_BYTE( "523h08.6f",    0x08001, 0x4000, CRC(79f44e17) SHA1(cb32edc4df9f2209f13fc258fec4e67ee91badef) )
		ROM_LOAD16_BYTE( "523h06.7e",    0x10000, 0x4000, CRC(d2db9689) SHA1(ceb5913716b4da2ddff2e837ddaa04d91e52f9e1) )
		ROM_LOAD16_BYTE( "523h09.7f",    0x10001, 0x4000, CRC(8896dc85) SHA1(91493c6b69655de482f0c2a0cb3662fc0d1b6e45) )
		/* 18000-1ffff empty */
	
		ROM_REGION( 0x0240, REGION_PROMS, 0 )
		ROM_LOAD( "523h10.2f",    0x0000, 0x0020, CRC(ec15dd15) SHA1(710384b154a9363fdc88edffda252f1d60e000dc) ) /* palette */
		ROM_LOAD( "523h11.3f",    0x0020, 0x0020, CRC(54be2e83) SHA1(3200abc7f2238d62d7204ef57a6daa2df150538d) ) /* palette */
		ROM_LOAD( "523h12.10f",   0x0040, 0x0100, CRC(53166a2a) SHA1(6cdde206036df7176679711f7888d72acee27c8f) ) /* sprites */
		ROM_LOAD( "523h13.11f",   0x0140, 0x0100, CRC(4e0647a0) SHA1(fb87f878456b8b76bb2c028cb890d2a5c1c3e388) ) /* characters */
	ROM_END(); }}; 
	
	
	public static DriverInitHandlerPtr init_finalizr  = new DriverInitHandlerPtr() { public void handler(){
		konami1_decode();
	} };
	
	
	public static GameDriver driver_finalizr	   = new GameDriver("1985"	,"finalizr"	,"finalizr.java"	,rom_finalizr,null	,machine_driver_finalizr	,input_ports_finalizr	,init_finalizr	,ROT90, "Konami", "Finalizer - Super Transformation", GAME_IMPERFECT_SOUND | GAME_NO_COCKTAIL )
	public static GameDriver driver_finalizb	   = new GameDriver("1985"	,"finalizb"	,"finalizr.java"	,rom_finalizb,driver_finalizr	,machine_driver_finalizr	,input_ports_finalizb	,init_finalizr	,ROT90, "bootleg", "Finalizer - Super Transformation (bootleg)", GAME_IMPERFECT_SOUND | GAME_NO_COCKTAIL )
}
