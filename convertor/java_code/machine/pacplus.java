/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package machine;

public class pacplus
{
	
	
	static unsigned char decrypt(int addr, unsigned char e)
	{
		static const unsigned char swap_xor_table[6][9] =
		{
			{ 7,6,5,4,3,2,1,0, 0x00 },
			{ 7,6,5,4,3,2,1,0, 0x28 },
			{ 6,1,3,2,5,7,0,4, 0x96 },
			{ 6,1,5,2,3,7,0,4, 0xbe },
			{ 0,3,7,6,4,2,1,5, 0xd5 },
			{ 0,3,4,6,7,2,1,5, 0xdd }
		};
		static const int picktable[32] =
		{
			0,2,4,2,4,0,4,2,2,0,2,2,4,0,4,2,
			2,2,4,0,4,2,4,0,0,4,0,4,4,2,4,2
		};
		unsigned int method = 0;
		const unsigned char *tbl;
	
	
		/* pick method from bits 0 2 5 7 9 of the address */
		method = picktable[
			(addr & 0x001) |
			((addr & 0x004) >> 1) |
			((addr & 0x020) >> 3) |
			((addr & 0x080) >> 4) |
			((addr & 0x200) >> 5)];
	
		/* switch method if bit 11 of the address is set */
		if ((addr & 0x800) == 0x800)
			method ^= 1;
	
		tbl = swap_xor_table[method];
		return BITSWAP8(e,tbl[0],tbl[1],tbl[2],tbl[3],tbl[4],tbl[5],tbl[6],tbl[7]) ^ tbl[8];
	}
	
	
	void pacplus_decode(void)
	{
		int i;
		unsigned char *RAM;
	
		/* CPU ROMs */
	
		RAM = memory_region(REGION_CPU1);
		for (i = 0; i < 0x4000; i++)
		{
			RAM[i] = decrypt(i,RAM[i]);
		}
	}
	
	
}
