/*###################################################################################################
**
**	TMS34010: Portable Texas Instruments TMS34010 emulator
**
**	Copyright (C) Alex Pasadyn/Zsolt Vasvari 1998
**	 Parts based on code by Aaron Giles
**
**#################################################################################################*/

#ifndef _34010OPS_H
#define _34010OPS_H

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package cpu.tms34010;

public class _34010opsH
{
	
	#ifndef INLINE
	#define INLINE static inline
	#endif
	
	/* Size of the memory buffer allocated for the shiftr register */
	#define SHIFTREG_SIZE			(8 * 512 * sizeof(UINT16))
	
	
	
	/*###################################################################################################
	**	MEMORY I/O MACROS
	**#################################################################################################*/
	
	#define TMS34010_RDMEM(A)			((unsigned)cpu_readmem29lew      (A))
	#define TMS34010_RDMEM_WORD(A)		((unsigned)cpu_readmem29lew_word (A))
	INLINE data32_t TMS34010_RDMEM_DWORD(offs_t A)
	{
		UINT32 result = cpu_readmem29lew_word(A);
		return result | (cpu_readmem29lew_word(A+2)<<16);
	}
	
	#define TMS34010_WRMEM(A,V)			(cpu_writemem29lew(A,V))
	#define TMS34010_WRMEM_WORD(A,V)	(cpu_writemem29lew_word(A,V))
	INLINE void TMS34010_WRMEM_DWORD(offs_t A,data32_t V)
	{
		cpu_writemem29lew_word(A,V);
		cpu_writemem29lew_word(A+2,V>>16);
	}
	
	
	
	/*###################################################################################################
	**	INTERNAL I/O CONSTANTS
	**#################################################################################################*/
	
	enum
	{
		REG_HESYNC = 0,
		REG_HEBLNK,
		REG_HSBLNK,
		REG_HTOTAL,
		REG_VESYNC,
		REG_VEBLNK,
		REG_VSBLNK,
		REG_VTOTAL,
		REG_DPYCTL,
		REG_DPYSTRT,
		REG_DPYINT,
		REG_CONTROL,
		REG_HSTDATA,
		REG_HSTADRL,
		REG_HSTADRH,
		REG_HSTCTLL,
	
		REG_HSTCTLH,
		REG_INTENB,
		REG_INTPEND,
		REG_CONVSP,
		REG_CONVDP,
		REG_PSIZE,
		REG_PMASK,
		REG_UNK23,
		REG_UNK24,
		REG_UNK25,
		REG_UNK26,
		REG_DPYTAP,
		REG_HCOUNT,
		REG_VCOUNT,
		REG_DPYADR,
		REG_REFCNT
	};
	
	enum
	{
		REG020_VESYNC,
		REG020_HESYNC,
		REG020_VEBLNK,
		REG020_HEBLNK,
		REG020_VSBLNK,
		REG020_HSBLNK,
		REG020_VTOTAL,
		REG020_HTOTAL,
		REG020_DPYCTL,		/* matches 010 */
		REG020_DPYSTRT,		/* matches 010 */
		REG020_DPYINT,		/* matches 010 */
		REG020_CONTROL,		/* matches 010 */
		REG020_HSTDATA,		/* matches 010 */
		REG020_HSTADRL,		/* matches 010 */
		REG020_HSTADRH,		/* matches 010 */
		REG020_HSTCTLL,		/* matches 010 */
	
		REG020_HSTCTLH,		/* matches 010 */
		REG020_INTENB,		/* matches 010 */
		REG020_INTPEND,		/* matches 010 */
		REG020_CONVSP,		/* matches 010 */
		REG020_CONVDP,		/* matches 010 */
		REG020_PSIZE,		/* matches 010 */
		REG020_PMASKL,
		REG020_PMASKH,
		REG020_CONVMP,
		REG020_CONTROL2,
		REG020_CONFIG,
		REG020_DPYTAP,		/* matches 010 */
		REG020_VCOUNT,
		REG020_HCOUNT,
		REG020_DPYADR,		/* matches 010 */
		REG020_REFADR,
	
		REG020_DPYSTL,
		REG020_DPYSTH,
		REG020_DPYNXL,
		REG020_DPYNXH,
		REG020_DINCL,
		REG020_DINCH,
		REG020_RES0,
		REG020_HESERR,
		REG020_RES1,
		REG020_RES2,
		REG020_RES3,
		REG020_RES4,
		REG020_SCOUNT,
		REG020_BSFLTST,
		REG020_DPYMSK,
		REG020_RES5,
	
		REG020_SETVCNT,
		REG020_SETHCNT,
		REG020_BSFLTDL,
		REG020_BSFLTDH,
		REG020_RES6,
		REG020_RES7,
		REG020_RES8,
		REG020_RES9,
		REG020_IHOST1L,
		REG020_IHOST1H,
		REG020_IHOST2L,
		REG020_IHOST2H,
		REG020_IHOST3L,
		REG020_IHOST3H,
		REG020_IHOST4L,
		REG020_IHOST4H
	};
	
	/* Interrupts that are generated by the processor internally */
	#define TMS34010_INT1		0x0002	/* External Interrupt 1 */
	#define TMS34010_INT2		0x0004	/* External Interrupt 2 */
	#define TMS34010_NMI         0x0100    /* NMI Interrupt */
	#define TMS34010_HI          0x0200    /* Host Interrupt */
	#define TMS34010_DI          0x0400    /* Display Interrupt */
	#define TMS34010_WV          0x0800    /* Window Violation Interrupt */
	
	/* IO registers accessor */
	#define IOREG(reg)					(state.IOregs[reg])
	#define SMART_IOREG(reg)			(state.IOregs[state.is_34020 ? REG020_##reg : REG_##reg])
	#define PBH 						(IOREG(REG_CONTROL) & 0x0100)
	#define PBV 						(IOREG(REG_CONTROL) & 0x0200)
	
	
	
	/*###################################################################################################
	**	FIELD WRITE MACROS
	**#################################################################################################*/
	
	#define WFIELDMAC(MASK,MAX) 														\
		UINT32 shift = offset & 0x0f;     												\
		UINT32 masked_data = data & (MASK);												\
		UINT32 old;				   														\
																						\
		offset = TOBYTE(offset & 0xfffffff0);											\
																						\
		if (shift >= MAX)																\
		{																				\
			old = (UINT32)TMS34010_RDMEM_DWORD(offset) & ~((MASK) << shift); 			\
			TMS34010_WRMEM_DWORD(offset, (masked_data << shift) | old);					\
		}																				\
		else																			\
		{																				\
			old = (UINT32)TMS34010_RDMEM_WORD(offset) & ~((MASK) << shift); 			\
			TMS34010_WRMEM_WORD(offset, ((masked_data & (MASK)) << shift) | old);		\
		}																				\
	
	#define WFIELDMAC_BIG(MASK,MAX)														\
		UINT32 shift = offset & 0x0f;     												\
		UINT32 masked_data = data & (MASK);												\
		UINT32 old;				   														\
																						\
		offset = TOBYTE(offset & 0xfffffff0);											\
																						\
		old = (UINT32)TMS34010_RDMEM_DWORD(offset) & ~(UINT32)((MASK) << shift);		\
		TMS34010_WRMEM_DWORD(offset, (UINT32)(masked_data << shift) | old);				\
		if (shift >= MAX)																\
		{																				\
			shift = 32 - shift;															\
			old = (UINT32)TMS34010_RDMEM_WORD(offset + 4) & ~((MASK) >> shift);			\
			TMS34010_WRMEM_WORD(offset, (masked_data >> shift) | old);					\
		}																				\
	
	#define WFIELDMAC_8																	\
		if (offset & 0x07)																\
		{																				\
			WFIELDMAC(0xff,9);															\
		}																				\
		else																			\
			TMS34010_WRMEM(TOBYTE(offset), data);										\
	
	#define RFIELDMAC_8																	\
		if (offset & 0x07)																\
		{																				\
			RFIELDMAC(0xff,9);															\
		}																				\
		else																			\
			return TMS34010_RDMEM(TOBYTE(offset));										\
	
	#define WFIELDMAC_32																\
		if (offset & 0x0f)																\
		{																				\
			UINT32 shift = offset&0x0f;													\
			UINT32 old;																	\
			UINT32 hiword;																\
			offset &= 0xfffffff0;														\
			old =    ((UINT32) TMS34010_RDMEM_DWORD (TOBYTE(offset     ))&(0xffffffff>>(0x20-shift)));	\
			hiword = ((UINT32) TMS34010_RDMEM_DWORD (TOBYTE(offset+0x20))&(0xffffffff<<shift));		\
			TMS34010_WRMEM_DWORD(TOBYTE(offset     ),(data<<      shift) |old);			\
			TMS34010_WRMEM_DWORD(TOBYTE(offset+0x20),(data>>(0x20-shift))|hiword);		\
		}																				\
		else																			\
			TMS34010_WRMEM_DWORD(TOBYTE(offset),data);									\
	
	
	
	/*###################################################################################################
	**	FIELD READ MACROS
	**#################################################################################################*/
	
	#define RFIELDMAC(MASK,MAX)															\
		UINT32 shift = offset & 0x0f;													\
		offset = TOBYTE(offset & 0xfffffff0);											\
																						\
		if (shift >= MAX)																\
			ret = (TMS34010_RDMEM_DWORD(offset) >> shift) & (MASK);						\
		else																			\
			ret = (TMS34010_RDMEM_WORD(offset) >> shift) & (MASK);						\
	
	#define RFIELDMAC_BIG(MASK,MAX)														\
		UINT32 shift = offset & 0x0f;													\
		offset = TOBYTE(offset & 0xfffffff0);											\
																						\
		ret = (UINT32)TMS34010_RDMEM_DWORD(offset) >> shift;							\
		if (shift >= MAX)																\
			ret |= (TMS34010_RDMEM_WORD(offset + 4) << (32 - shift));					\
		ret &= MASK;																	\
	
	#define RFIELDMAC_32																\
		if (offset&0x0f)																\
		{																				\
			UINT32 shift = offset&0x0f;													\
			offset &= 0xfffffff0;														\
			return (((UINT32)TMS34010_RDMEM_DWORD (TOBYTE(offset     ))>>      shift) |	\
				            (TMS34010_RDMEM_DWORD (TOBYTE(offset+0x20))<<(0x20-shift)));\
		}																				\
		else																			\
			return TMS34010_RDMEM_DWORD(TOBYTE(offset));								\
	
	
	
	#endif /* _34010OPS_H */
}
