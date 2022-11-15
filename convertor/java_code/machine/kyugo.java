/***************************************************************************

	Kyugo hardware games

***************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package machine;

public class kyugo
{
	
	
	data8_t *kyugo_sharedram;
	
	
	/*************************************
	 *
	 *	Machine initialization
	 *
	 *************************************/
	
	public static MachineInitHandlerPtr machine_init_kyugo  = new MachineInitHandlerPtr() { public void handler(){
		// must start with interrupts and sub CPU disabled
		cpu_interrupt_enable(0, 0);
		kyugo_sub_cpu_control_w(0, 0);
	} };
	
	
	WRITE_HANDLER( kyugo_sub_cpu_control_w )
	{
		cpu_set_halt_line(1, data ? CLEAR_LINE : ASSERT_LINE);
	}
	
	
	/*************************************
	 *
	 *	Shared RAM handlers
	 *
	 *************************************/
	
	WRITE_HANDLER( kyugo_sharedram_w )
	{
		kyugo_sharedram[offset] = data;
	}
	
	
	READ_HANDLER( kyugo_sharedram_r )
	{
		return kyugo_sharedram[offset];
	}
}
