/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package sndhrdw;

public class irem
{
	
	
	
	public static WriteHandlerPtr irem_sound_cmd_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if ((data & 0x80) == 0)
			soundlatch_w.handler(0,data & 0x7f);
		else
			cpu_set_irq_line(1,0,HOLD_LINE);
	} };
	
	
	static int port1,port2;
	
	public static WriteHandlerPtr irem_port1_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		port1 = data;
	} };
	
	public static WriteHandlerPtr irem_port2_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		/* write latch */
		if ((port2 & 0x01) && !(data & 0x01))
		{
			/* control or data port? */
			if (port2 & 0x04)
			{
				/* PSG 0 or 1? */
				if (port2 & 0x08)
					AY8910_control_port_0_w.handler(0,port1);
				if (port2 & 0x10)
					AY8910_control_port_1_w.handler(0,port1);
			}
			else
			{
				/* PSG 0 or 1? */
				if (port2 & 0x08)
					AY8910_write_port_0_w.handler(0,port1);
				if (port2 & 0x10)
					AY8910_write_port_1_w.handler(0,port1);
			}
		}
		port2 = data;
	} };
	
	
	public static ReadHandlerPtr irem_port1_r  = new ReadHandlerPtr() { public int handler(int offset){
		/* PSG 0 or 1? */
		if (port2 & 0x08)
			return AY8910_read_port_0_r.handler(0);
		if (port2 & 0x10)
			return AY8910_read_port_1_r.handler(0);
		return 0xff;
	} };
	
	public static ReadHandlerPtr irem_port2_r  = new ReadHandlerPtr() { public int handler(int offset){
		return 0;
	} };
	
	
	
	public static WriteHandlerPtr irem_msm5205_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		/* bits 2-4 select MSM5205 clock & 3b/4b playback mode */
		MSM5205_playmode_w(0,(data >> 2) & 7);
		MSM5205_playmode_w(1,((data >> 2) & 4) | 3);	/* always in slave mode */
	
		/* bits 0 and 1 reset the two chips */
		MSM5205_reset_w(0,data & 1);
		MSM5205_reset_w(1,data & 2);
	} };
	
	public static WriteHandlerPtr irem_adpcm_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		MSM5205_data_w(offset,data);
	} };
	
	static void irem_adpcm_int(int data)
	{
		cpu_set_nmi_line(1,PULSE_LINE);
	
		/* the first MSM5205 clocks the second */
		MSM5205_vclk_w(1,1);
		MSM5205_vclk_w(1,0);
	}
	
	public static WriteHandlerPtr irem_analog_w = new WriteHandlerPtr() {public void handler(int offset, int data){
	#ifdef MAME_DEBUG
	if (data&0x0f) usrintf_showmessage("analog sound %x",data&0x0f);
	#endif
	} };
	
	
	static AY8910interface irem_ay8910_interface = new AY8910interface
	(
		2,	/* 2 chips */
		3579545/4,
		new int[] { 20, 20 },
		new ReadHandlerPtr[] { soundlatch_r, 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0, irem_analog_w },
		new WriteHandlerPtr[] { irem_msm5205_w, 0 }
	);
	
	struct MSM5205interface irem_msm5205_interface =
	{
		2,					/* 2 chips            */
		384000,				/* 384KHz             */
		{ irem_adpcm_int, 0 },/* interrupt function */
		{ MSM5205_S96_4B,MSM5205_SEX_4B },	/* default to 4KHz, but can be changed at run time */
		{ 100, 100 }
	};
	
	
	
	public static Memory_ReadAddress irem_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x001f, m6803_internal_registers_r ),
		new Memory_ReadAddress( 0x0080, 0x00ff, MRA_RAM ),
		new Memory_ReadAddress( 0x4000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress irem_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x001f, m6803_internal_registers_w ),
		new Memory_WriteAddress( 0x0080, 0x00ff, MWA_RAM ),
		new Memory_WriteAddress( 0x0800, 0x0800, MWA_NOP ),    /* IACK */
		new Memory_WriteAddress( 0x0801, 0x0802, irem_adpcm_w ),
		new Memory_WriteAddress( 0x9000, 0x9000, MWA_NOP ),    /* IACK */
		new Memory_WriteAddress( 0x4000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort irem_sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( M6803_PORT1, M6803_PORT1, irem_port1_r ),
		new IO_ReadPort( M6803_PORT2, M6803_PORT2, irem_port2_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort irem_sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( M6803_PORT1, M6803_PORT1, irem_port1_w ),
		new IO_WritePort( M6803_PORT2, M6803_PORT2, irem_port2_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static MachineHandlerPtr machine_driver_irem_audio = new MachineHandlerPtr() {
        public void handler(InternalMachineDriver machine) { 
	MACHINE_DRIVER_START(machine);
	
		/* basic machine hardware */
		MDRV_CPU_ADD(M6803, 3579545/4)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)
		MDRV_CPU_MEMORY(irem_sound_readmem,irem_sound_writemem)
		MDRV_CPU_PORTS(irem_sound_readport,irem_sound_writeport)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, irem_ay8910_interface)
		MDRV_SOUND_ADD(MSM5205, irem_msm5205_interface)
	MACHINE_DRIVER_END();
 }
};
}
