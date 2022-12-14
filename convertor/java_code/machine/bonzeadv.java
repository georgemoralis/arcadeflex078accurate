/*************************************************************************

  Bonze Adventure C-Chip
  ======================

  Based on RAINE. Improvements with a lot of help from Ruben Panossian.
  Additional thanks to Robert Gallagher and Stephane Humbert.

  - CLEV is correct except for last two lines
  - CPOS is correct but probably incomplete
  - CMAP is freely made up

*************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package machine;

public class bonzeadv
{
	
	static int current_round = 0;
	static int current_bank = 0;
	
	static UINT8 cval[26];
	static UINT8 cc_port;
	
	struct cchip_mapping
	{
		UINT16 xmin;
		UINT16 xmax;
		UINT16 ymin;
		UINT16 ymax;
		unsigned char index;
	};
	
	
	static UINT16 CLEV[][13] =
	{
	/*	  map start       player start    player y-range  player x-range  map y-range     map x-range     time   */
		{ 0x0000, 0x0018, 0x0020, 0x0030, 0x0028, 0x00D0, 0x0050, 0x0090, 0x0000, 0x0118, 0x0000, 0x0C90, 0x3800 },
		{ 0x0000, 0x0100, 0x0048, 0x0028, 0x0028, 0x0090, 0x0070, 0x00B0, 0x0000, 0x02C0, 0x0000, 0x0CA0, 0x3000 },
		{ 0x0000, 0x0518, 0x0068, 0x00B8, 0x0028, 0x00D0, 0x0068, 0x00B0, 0x02F8, 0x0518, 0x0000, 0x0EF8, 0x3000 },
		{ 0x0978, 0x0608, 0x00C8, 0x00B0, 0x0028, 0x00D0, 0x0098, 0x00C8, 0x0608, 0x06E8, 0x0000, 0x0A48, 0x2000 },
		{ 0x0410, 0x0708, 0x0070, 0x0030, 0x0028, 0x00D0, 0x0060, 0x0090, 0x0708, 0x0708, 0x0410, 0x1070, 0x3800 },
		{ 0x1288, 0x0808, 0x0099, 0x00CE, 0x0028, 0x00D0, 0x0060, 0x00C0, 0x0000, 0x0808, 0x1288, 0x1770, 0x3000 },
		{ 0x11B0, 0x0908, 0x0118, 0x0040, 0x0028, 0x00D0, 0x00B0, 0x00C0, 0x0900, 0x0910, 0x0050, 0x11B0, 0x3800 },
		{ 0x0000, 0x0808, 0x0028, 0x00B8, 0x0028, 0x00D0, 0x0070, 0x00B0, 0x0808, 0x0808, 0x0000, 0x0398, 0x1000 },
		{ 0x06F8, 0x0808, 0x0028, 0x00B8, 0x0028, 0x00D0, 0x0070, 0x00B0, 0x0808, 0x0808, 0x06F8, 0x06F8, 0x8800 },
		{ 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000 },
		{ 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x6000 }
	};
	
	static UINT16 CPOS[][4] =
	{
	/*    map start       player start   */
		{ 0x0000, 0x0018, 0x0020, 0x00A8 },
		{ 0x01E0, 0x0018, 0x0070, 0x0098 },
		{ 0x0438, 0x0018, 0x0070, 0x00A8 },
		{ 0x04A0, 0x0018, 0x0070, 0x0080 },
		{ 0x06B8, 0x0018, 0x0078, 0x0078 },
		{ 0x08C8, 0x0018, 0x0070, 0x0028 },
		{ 0x09C0, 0x0018, 0x0070, 0x00A8 },
		{ 0x0A68, 0x0018, 0x0070, 0x0058 },
		{ 0x0C40, 0x0018, 0x0070, 0x0040 },
		{ 0x0000, 0x0208, 0x0040, 0x0070 },
		{ 0x0080, 0x0218, 0x00B0, 0x0080 },
		{ 0x0450, 0x01F8, 0x0090, 0x0030 },
		{ 0x0450, 0x0218, 0x00A0, 0x00A0 },
		{ 0x07A8, 0x0218, 0x0090, 0x0060 },
		{ 0x0840, 0x0218, 0x0088, 0x0060 },
		{ 0x0958, 0x0218, 0x00A0, 0x0070 },
		{ 0x0A98, 0x0218, 0x0088, 0x0050 },
		{ 0x0C20, 0x0200, 0x00A0, 0x0028 },
		{ 0x0000, 0x0518, 0x0068, 0x00B0 },
		{ 0x00D0, 0x0518, 0x0078, 0x0060 },
		{ 0x02F0, 0x0518, 0x0078, 0x0048 },
		{ 0x0670, 0x0518, 0x0078, 0x0048 },
		{ 0x07D8, 0x0518, 0x0070, 0x0060 },
		{ 0x09E8, 0x0500, 0x0080, 0x0080 },
		{ 0x02E8, 0x04B0, 0x0080, 0x0090 },
		{ 0x0278, 0x0318, 0x0078, 0x00A8 },
		{ 0x0390, 0x0318, 0x0070, 0x00B8 },
		{ 0x0608, 0x0318, 0x0080, 0x0058 },
		{ 0x0878, 0x0318, 0x0078, 0x0098 },
		{ 0x0908, 0x0418, 0x0078, 0x0030 },
		{ 0x0B20, 0x0418, 0x0070, 0x0080 },
		{ 0x0A48, 0x0608, 0x00C0, 0x0068 },
		{ 0x0930, 0x0608, 0x00B8, 0x0080 },
		{ 0x07E8, 0x0608, 0x00B8, 0x0078 },
		{ 0x0630, 0x0608, 0x00C0, 0x0028 },
		{ 0x02C8, 0x0608, 0x00C0, 0x0090 },
		{ 0x02B0, 0x0608, 0x00B8, 0x0090 },
		{ 0x00D8, 0x0608, 0x00B8, 0x00A0 },
		{ 0x0020, 0x0610, 0x00B8, 0x00A8 },
		{ 0x0560, 0x0708, 0x0068, 0x0090 },
		{ 0x0860, 0x0708, 0x0060, 0x0090 },
		{ 0x09C0, 0x0708, 0x0068, 0x0080 },
		{ 0x0C58, 0x0708, 0x0068, 0x0070 },
		{ 0x0D80, 0x0708, 0x0070, 0x00B0 },
		{ 0x0EA8, 0x0708, 0x0070, 0x00B0 },
		{ 0x0FC0, 0x0708, 0x0080, 0x0030 },
		{ 0x1288, 0x0808, 0x0099, 0x00CE },
		{ 0x12F0, 0x0808, 0x0078, 0x0028 },
		{ 0x1488, 0x0808, 0x0080, 0x0070 },
		{ 0x1600, 0x0808, 0x0080, 0x0090 },
		{ 0x1508, 0x0708, 0x0080, 0x0090 },
		{ 0x1770, 0x0728, 0x0080, 0x0080 },
		{ 0x1770, 0x0650, 0x00D8, 0x0070 },
		{ 0x1578, 0x05D0, 0x0098, 0x0060 },
		{ 0x1508, 0x05E8, 0x0040, 0x0088 },
		{ 0x1658, 0x0528, 0x0088, 0x0088 },
		{ 0x1508, 0x04F8, 0x0080, 0x0060 },
		{ 0x1500, 0x03E8, 0x0090, 0x0058 },
		{ 0x1518, 0x0368, 0x0090, 0x0058 },
		{ 0x15A8, 0x0268, 0x0080, 0x0058 },
		{ 0x1650, 0x0250, 0x0080, 0x0058 },
		{ 0x1630, 0x02B0, 0x0088, 0x0060 },
		{ 0x16E0, 0x0398, 0x0068, 0x0068 },
		{ 0x1680, 0x0528, 0x00A8, 0x0070 },
		{ 0x1658, 0x0528, 0x0088, 0x0088 },
		{ 0x1640, 0x05E8, 0x0088, 0x0088 },
		{ 0x1770, 0x0588, 0x0090, 0x0088 },
		{ 0x1770, 0x0508, 0x0098, 0x0088 },
		{ 0x1770, 0x0450, 0x00D8, 0x0060 },
		{ 0x1770, 0x0330, 0x00C0, 0x0080 },
		{ 0x1740, 0x02A8, 0x0090, 0x0058 },
		{ 0x16C8, 0x0178, 0x0080, 0x0060 },
		{ 0x1508, 0x0208, 0x0028, 0x0060 },
		{ 0x1618, 0x0110, 0x0078, 0x0068 },
		{ 0x1770, 0x0118, 0x00C0, 0x0060 },
		{ 0x1698, 0x0000, 0x0080, 0x0050 },
		{ 0x1500, 0x0000, 0x0080, 0x0048 },
		{ 0x1140, 0x0908, 0x00B8, 0x0068 },
		{ 0x0FE8, 0x0908, 0x00B8, 0x0098 },
		{ 0x0E08, 0x0908, 0x00B8, 0x0070 },
		{ 0x0CD0, 0x0908, 0x00B8, 0x00B0 },
		{ 0x08F8, 0x0908, 0x00B0, 0x0080 },
		{ 0x07E8, 0x0908, 0x00B8, 0x00B8 },
		{ 0x0660, 0x0908, 0x00B0, 0x0098 },
		{ 0x03D0, 0x0908, 0x00B0, 0x0038 },
		{ 0x00D0, 0x0908, 0x00B0, 0x0090 },
		{ 0x0000, 0x0808, 0x0028, 0x00B8 },
		{ 0x06F8, 0x0808, 0x0028, 0x00B8 }
	};
	
	static struct cchip_mapping CMAP[] =
	{
		{ 0x0000, 0x0250, 0x0000, 0x0100, 0x00 },
		{ 0x0250, 0x04A8, 0x0000, 0x0100, 0x01 },
		{ 0x04A8, 0x0730, 0x0000, 0x0100, 0x02 },
		{ 0x0300, 0x0800, 0x0100, 0x0200, 0x03 },
		{ 0x0730, 0x0938, 0x0000, 0x0100, 0x04 },
		{ 0x0938, 0x0A30, 0x0000, 0x0100, 0x05 },
		{ 0x0A30, 0x0AD8, 0x0000, 0x0100, 0x06 },
		{ 0x0AD8, 0x0CB0, 0x0000, 0x0100, 0x07 },
		{ 0x0C40, 0x0E80, 0x0000, 0x0100, 0x08 },
		{ 0x0000, 0x0130, 0x0100, 0x0300, 0x09 },
		{ 0x0130, 0x04F0, 0x0200, 0x0300, 0x0A },
		{ 0x04F0, 0x0808, 0x0200, 0x0280, 0x0B },
		{ 0x04F0, 0x0808, 0x0280, 0x0300, 0x0C },
		{ 0x0808, 0x08C8, 0x0200, 0x0300, 0x0D },
		{ 0x08C8, 0x09F8, 0x0200, 0x0300, 0x0E },
		{ 0x09F8, 0x0B20, 0x0200, 0x0300, 0x0F },
		{ 0x0B20, 0x0CC0, 0x0200, 0x0300, 0x10 },
		{ 0x0CC0, 0x0E00, 0x0200, 0x0300, 0x11 },
		{ 0x0000, 0x0148, 0x0500, 0x0600, 0x12 },
		{ 0x0148, 0x0300, 0x0500, 0x0600, 0x13 },
		{ 0x0300, 0x06E8, 0x0500, 0x0600, 0x14 },
		{ 0x06E8, 0x0848, 0x0500, 0x0600, 0x15 },
		{ 0x0848, 0x0A68, 0x0500, 0x0600, 0x16 },
		{ 0x0A68, 0x0C00, 0x0500, 0x0600, 0x17 },
		{ 0x0200, 0x0480, 0x0400, 0x0500, 0x18 },
		{ 0x0200, 0x0400, 0x0300, 0x0400, 0x19 },
		{ 0x0400, 0x0688, 0x0300, 0x0400, 0x1A },
		{ 0x0688, 0x08F0, 0x0300, 0x0400, 0x1B },
		{ 0x08F0, 0x0A80, 0x0300, 0x0400, 0x1C },
		{ 0x0800, 0x0B90, 0x0400, 0x0500, 0x1D },
		{ 0x0B90, 0x1100, 0x0400, 0x0500, 0x1E },
		{ 0x09E8, 0x0C00, 0x0600, 0x0700, 0x1F },
		{ 0x08A0, 0x09E8, 0x0600, 0x0700, 0x20 },
		{ 0x06F0, 0x08A0, 0x0600, 0x0700, 0x21 },
		{ 0x0388, 0x06F0, 0x0600, 0x0700, 0x22 },
		{ 0x0368, 0x0388, 0x0600, 0x0700, 0x23 },
		{ 0x0190, 0x0368, 0x0600, 0x0700, 0x24 },
		{ 0x0000, 0x0190, 0x0600, 0x0700, 0x25 },
		{ 0x0000, 0x0280, 0x0700, 0x0800, 0x26 },
		{ 0x0380, 0x08C0, 0x0700, 0x0800, 0x27 },
		{ 0x08C0, 0x0A28, 0x0700, 0x0800, 0x28 },
		{ 0x0A20, 0x0CC0, 0x0700, 0x0800, 0x29 },
		{ 0x0CC0, 0x0DF0, 0x0700, 0x0800, 0x2A },
		{ 0x0DF0, 0x0F18, 0x0700, 0x0800, 0x2B },
		{ 0x0F18, 0x1040, 0x0700, 0x0800, 0x2C },
		{ 0x1040, 0x1280, 0x0700, 0x0800, 0x2D },
		{ 0x1200, 0x1368, 0x0800, 0x0900, 0x2E },
		{ 0x1368, 0x1508, 0x0800, 0x0900, 0x2F },
		{ 0x1508, 0x1658, 0x0800, 0x0900, 0x30 },
		{ 0x1658, 0x1900, 0x0800, 0x0900, 0x31 },
		{ 0x1480, 0x1620, 0x0700, 0x0800, 0x32 },
		{ 0x1620, 0x1900, 0x0700, 0x0800, 0x33 },
	
		     /* round 6 not fully mapped */
	
		{ 0x1200, 0x1580, 0x0000, 0x0100, 0x4C },
		{ 0x10A0, 0x1380, 0x0900, 0x0A00, 0x4D },
		{ 0x0EC0, 0x10A0, 0x0900, 0x0A00, 0x4E },
		{ 0x0D80, 0x0EC0, 0x0900, 0x0A00, 0x4F },
		{ 0x09A8, 0x0D80, 0x0900, 0x0A00, 0x50 },
		{ 0x08A0, 0x09A8, 0x0900, 0x0A00, 0x51 },
		{ 0x0710, 0x08A0, 0x0900, 0x0A00, 0x52 },
		{ 0x0480, 0x0710, 0x0900, 0x0A00, 0x53 },
		{ 0x0180, 0x0480, 0x0900, 0x0A00, 0x54 },
		{ 0x0000, 0x0180, 0x0900, 0x0A00, 0x55 },
		{ 0x0000, 0x0580, 0x0800, 0x0900, 0x56 },
		{ 0x0580, 0x0900, 0x0800, 0x0900, 0x57 }
	};
	
	
	static void WriteLevelData(void)
	{
		int i;
	
		for (i = 0; i < 13; i++)
		{
			UINT16 v = CLEV[current_round][i];
	
			cval[2 * i + 0] = v & 0xff;
			cval[2 * i + 1] = v >> 8;
		}
	}
	
	static void WriteRestartPos(void)
	{
		int n;
	
		int x = cval[0] + 256 * cval[1] + cval[4] + 256 * cval[5];
		int y = cval[2] + 256 * cval[3] + cval[6] + 256 * cval[7];
	
		for (n = 0; n < sizeof CMAP / sizeof CMAP[0]; n++)
		{
			if (x >= CMAP[n].xmin && x < CMAP[n].xmax &&
			    y >= CMAP[n].ymin && y < CMAP[n].ymax)
			{
				int i;
	
				for (i = 0; i < 4; i++)
				{
					UINT16 v = CPOS[CMAP[n].index][i];
	
					cval[2 * i + 0] = v & 0xff;
					cval[2 * i + 1] = v >> 8;
				}
	
				return;
			}
		}
	}
	
	
	/*************************************
	 *
	 * Writes to C-Chip - Important Bits
	 *
	 *************************************/
	
	WRITE16_HANDLER( bonzeadv_c_chip_w )
	{
		if (offset == 0x600)
		{
			current_bank = data;
		}
	
		if (current_bank == 0)
		{
			if (offset == 0x008)
			{
				cc_port = data;
	
				coin_lockout_w(1, data & 0x80);
				coin_lockout_w(0, data & 0x40);
				coin_counter_w(1, data & 0x20);
				coin_counter_w(0, data & 0x10);
			}
	
			if (offset == 0x00E && data != 0x00)
			{
				WriteRestartPos();
			}
	
			if (offset == 0x00F && data != 0x00)
			{
				WriteLevelData();
			}
	
			if (offset == 0x010)
			{
				current_round = data;
			}
	
			if (offset >= 0x011 && offset <= 0x02A)
			{
				cval[offset - 0x11] = data;
			}
		}
	}
	
	/*************************************
	 *
	 * Reads from C-Chip
	 *
	 *************************************/
	
	READ16_HANDLER( bonzeadv_c_chip_r )
	{
		/* C-chip ID */
	
		if (offset == 0x401)
		{
			return 0x01;
		}
	
		if (current_bank == 0)
		{
			switch (offset)
			{
			case 0x003: return input_port_2_word_r(offset, mem_mask);
			case 0x004: return input_port_3_word_r(offset, mem_mask);
			case 0x005: return input_port_4_word_r(offset, mem_mask);
			case 0x006: return input_port_5_word_r(offset, mem_mask);
			case 0x008: return cc_port;
			}
	
			if (offset == 0x00E)
			{
				return 0x00;	/* non-zero signals error */
			}
	
			if (offset >= 0x011 && offset <= 0x2A)
			{
				return cval[offset - 0x11];
			}
		}
	
		return 0;
	}
}
