/***************************************************************************

	Atari Clay Shoot hardware

	driver by Zsolt Vasvari

****************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package machine;

public class clayshoo
{
	
	
	static data8_t input_port_select;
	static data8_t analog_port_val;
	
	
	/*************************************
	 *
	 *	Digital control handling functions
	 *
	 *************************************/
	
	public static WriteHandlerPtr input_port_select_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		input_port_select = data;
	} };
	
	
	static data8_t difficulty_input_port_r(int bit)
	{
		data8_t ret = 0;
	
		/* read fake port and remap the buttons to 2 bits */
		data8_t	raw = readinputport(6);
	
		if (raw & (1 << (bit + 1)))
			ret = 0x03;		/* expert */
		else if (raw & (1 << (bit + 2)))
			ret = 0x01;		/* pro */
		else
			ret = 0x00;		/* amateur otherwise */
	
		return ret;
	}
	
	
	public static ReadHandlerPtr input_port_r  = new ReadHandlerPtr() { public int handler(int offset){
		data8_t ret = 0;
	
	
		switch (input_port_select)
		{
		case 0x01:	ret = readinputport(0); break;
		case 0x02:	ret = readinputport(1); break;
		case 0x04:	ret = (readinputport(2) & 0xf0) |
						   difficulty_input_port_r(0) |
						  (difficulty_input_port_r(3) << 2); break;
		case 0x08:	ret = readinputport(3); break;
		case 0x10:
		case 0x20:	break;	/* these two are not really used */
		default: logerror("Unexcepted port read: %02X\n", input_port_select);
		}
	
		return ret;
	} };
	
	
	static ppi8255_interface ppi8255_intf =
	{
		2, 							/* 2 chips */
		{ 0, 0 },					/* Port A read */
		{ 0, input_port_r },		/* Port B read */
		{ 0, 0 },					/* Port C read */
		{ 0, input_port_select_w },	/* Port A write */
		{ 0, 0 },					/* Port B write */
		{ 0, 0 /* sound effects */},/* Port C write */
	};
	
	
	public static MachineInitHandlerPtr machine_init_clayshoo  = new MachineInitHandlerPtr() { public void handler(){
		ppi8255_init(&ppi8255_intf);
	} };
	
	
	
	/*************************************
	 *
	 *	Analog control handling functions
	 *
	 *************************************/
	
	static void reset_analog_bit(int bit)
	{
		analog_port_val &= ~bit;
	}
	
	
	static double compute_duration(int analog_pos)
	{
		/* the 58 comes from the length of the loop used to
		   read the analog position */
		return TIME_IN_CYCLES(58*analog_pos, 0);
	}
	
	
	public static WriteHandlerPtr clayshoo_analog_reset_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		/* reset the analog value, and start two times that will fire
		   off in a short period proportional to the position of the
		   analog control and set the appropriate bit. */
	
		analog_port_val = 0xff;
	
		timer_set(compute_duration(readinputport(4)), 0x02, reset_analog_bit);
		timer_set(compute_duration(readinputport(5)), 0x01, reset_analog_bit);
	} };
	
	
	public static ReadHandlerPtr clayshoo_analog_r  = new ReadHandlerPtr() { public int handler(int offset){
		return analog_port_val;
	} };
}
