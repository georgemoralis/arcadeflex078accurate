/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package machine;

public class lsasquad
{
	
	
	/* coin inputs are inverted in storming */
	int lsasquad_invertcoin;
	
	
	/***************************************************************************
	
	 main <-> sound CPU communication
	
	***************************************************************************/
	
	static int sound_nmi_enable,pending_nmi,sound_pending,sound_cmd,sound_result;
	
	static void nmi_callback(int param)
	{
		if (sound_nmi_enable) cpu_set_irq_line(1,IRQ_LINE_NMI,PULSE_LINE);
		else pending_nmi = 1;
	}
	
	public static WriteHandlerPtr lsasquad_sh_nmi_disable_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		sound_nmi_enable = 0;
	} };
	
	public static WriteHandlerPtr lsasquad_sh_nmi_enable_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		sound_nmi_enable = 1;
		if (pending_nmi)
		{
			cpu_set_irq_line(1,IRQ_LINE_NMI,PULSE_LINE);
			pending_nmi = 0;
		}
	} };
	
	public static WriteHandlerPtr lsasquad_sound_command_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		sound_pending |= 0x01;
		sound_cmd = data;
	//logerror("%04x: sound cmd %02x\n",activecpu_get_pc(),data);
		timer_set(TIME_NOW,data,nmi_callback);
	} };
	
	public static ReadHandlerPtr lsasquad_sh_sound_command_r  = new ReadHandlerPtr() { public int handler(int offset){
		sound_pending &= ~0x01;
	//logerror("%04x: read sound cmd %02x\n",activecpu_get_pc(),sound_cmd);
		return sound_cmd;
	} };
	
	public static WriteHandlerPtr lsasquad_sh_result_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		sound_pending |= 0x02;
	//logerror("%04x: sound res %02x\n",activecpu_get_pc(),data);
		sound_result = data;
	} };
	
	public static ReadHandlerPtr lsasquad_sound_result_r  = new ReadHandlerPtr() { public int handler(int offset){
		sound_pending &= ~0x02;
	//logerror("%04x: read sound res %02x\n",activecpu_get_pc(),sound_result);
		return sound_result;
	} };
	
	public static ReadHandlerPtr lsasquad_sound_status_r  = new ReadHandlerPtr() { public int handler(int offset){
		/* bit 0: message pending for sound cpu */
		/* bit 1: message pending for main cpu */
		return sound_pending;
	} };
	
	
	
	/***************************************************************************
	
	 LSA Squad 68705 protection interface
	
	 The following is ENTIRELY GUESSWORK!!!
	
	***************************************************************************/
	
	static unsigned char from_main,from_mcu;
	static int mcu_sent = 0,main_sent = 0;
	
	static unsigned char portA_in,portA_out,ddrA;
	
	public static ReadHandlerPtr lsasquad_68705_portA_r  = new ReadHandlerPtr() { public int handler(int offset){
	//logerror("%04x: 68705 port A read %02x\n",activecpu_get_pc(),portA_in);
		return (portA_out & ddrA) | (portA_in & ~ddrA);
	} };
	
	public static WriteHandlerPtr lsasquad_68705_portA_w = new WriteHandlerPtr() {public void handler(int offset, int data){
	//logerror("%04x: 68705 port A write %02x\n",activecpu_get_pc(),data);
		portA_out = data;
	} };
	
	public static WriteHandlerPtr lsasquad_68705_ddrA_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		ddrA = data;
	} };
	
	
	
	/*
	 *  Port B connections:
	 *
	 *  all bits are logical 1 when read (+5V pullup)
	 *
	 *  1   W  when 1->0, enables latch which brings the command from main CPU (read from port A)
	 *  2   W  when 0->1, copies port A to the latch for the main CPU
	 */
	
	static unsigned char portB_in,portB_out,ddrB;
	
	public static ReadHandlerPtr lsasquad_68705_portB_r  = new ReadHandlerPtr() { public int handler(int offset){
		return (portB_out & ddrB) | (portB_in & ~ddrB);
	} };
	
	public static WriteHandlerPtr lsasquad_68705_portB_w = new WriteHandlerPtr() {public void handler(int offset, int data){
	//logerror("%04x: 68705 port B write %02x\n",activecpu_get_pc(),data);
	
		if ((ddrB & 0x02) && (~data & 0x02) && (portB_out & 0x02))
		{
			portA_in = from_main;
			if (main_sent) cpu_set_irq_line(2,0,CLEAR_LINE);
			main_sent = 0;
	//logerror("read command %02x from main cpu\n",portA_in);
		}
		if ((ddrB & 0x04) && (data & 0x04) && (~portB_out & 0x04))
		{
	//logerror("send command %02x to main cpu\n",portA_out);
			from_mcu = portA_out;
			mcu_sent = 1;
		}
	
		portB_out = data;
	} };
	
	public static WriteHandlerPtr lsasquad_68705_ddrB_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		ddrB = data;
	} };
	
	public static WriteHandlerPtr lsasquad_mcu_w = new WriteHandlerPtr() {public void handler(int offset, int data){
	//logerror("%04x: mcu_w %02x\n",activecpu_get_pc(),data);
		from_main = data;
		main_sent = 1;
		cpu_set_irq_line(2,0,ASSERT_LINE);
	} };
	
	public static ReadHandlerPtr lsasquad_mcu_r  = new ReadHandlerPtr() { public int handler(int offset){
	//logerror("%04x: mcu_r %02x\n",activecpu_get_pc(),from_mcu);
		mcu_sent = 0;
		return from_mcu;
	} };
	
	public static ReadHandlerPtr lsasquad_mcu_status_r  = new ReadHandlerPtr() { public int handler(int offset){
		int res = input_port_3_r.handler(0);
	
		/* bit 0 = when 1, mcu is ready to receive data from main cpu */
		/* bit 1 = when 0, mcu has sent data to the main cpu */
	//logerror("%04x: mcu_status_r\n",activecpu_get_pc());
		if (!main_sent) res |= 0x01;
		if (!mcu_sent) res |= 0x02;
	
		return res ^ lsasquad_invertcoin;
	} };
}
