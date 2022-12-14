 /**************************************************************************\
 *				Texas Instruments TMS320x25 DSP Disassembler				*
 *																			*
 *				   Copyright (C) 2001-2002+ Tony La Porta					*
 *						Written for the MAME project.						*
 *																			*
 *		Notes : Data is expected to be read from source file as MSB first.	*
 *				This is a word based microcontroller, with addressing		*
 *					architecture based on the Harvard addressing scheme.	*
 *																			*
 \**************************************************************************/

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package cpu.tms32025;

public class dis32025
{
	
	
	
	unsigned char *Buffer;
	
	
	int main(int argc,char *argv[])
	{
		int  length=0, length_to_dump=0, offset=0, disasm_words=0;
		int  filelength=0, bytes_read;
		int  Counter=0;
	
		FILE *F;
		char *String_Output;
	
		if(argc<2)
		{
			printf("\n");
			printf("TMS32025 Disassembler 1.1 by Tony La Porta (C)2001-2002+\n\n");
			printf("Usage: dis32025 <input-file> [ <start-addr> [ <num-of-addr> ] ]\n");
			printf("                <input-file>  source file data must be MSB first\n");
			printf("                <start-addr>  starting address to disassemble from (decimal)\n");
			printf("                <num-of-addr> number of addresses to disassemble (decimal)\n");
			printf("                              Preceed values with 0x if HEX values preffered\n");
			exit(1);
		}
	
		if(!(F=fopen(argv[1],"rb")))
		{
			printf("\n%s: Can't open file %s\n",argv[0],argv[1]);
			exit(2);
		}
		argv++; argc--;
		if (argv[1])
		{
			offset = strtol(argv[1],NULL,0);
			argv++; argc--;
		}
		if (argv[1])
		{
			length = strtol(argv[1],NULL,0);
			argv++; argc--;
		}
	
		fseek(F,0, SEEK_END);
		filelength = ftell(F);
	
		length *= 2;
	
		if ((length > (filelength - (offset*2))) || (length == 0)) length = filelength - (offset*2);
		printf("Length=%04Xh(words)  Offset=$%04Xh  filelength=%04Xh(words) %04Xh(bytes)\n",length/2,offset,filelength/2,filelength);
		length_to_dump = length;
		printf("Starting from %d, dumping %d opcodes (word size)\n",offset,length/2);
		Buffer = calloc((filelength+1),sizeof(char));
		if (Buffer==NULL)
		{
			printf("Out of Memory !!!");
			fclose(F);
			exit(3);
		}
		String_Output = calloc(80,sizeof(char));
		if (String_Output==NULL)
		{
			printf("Out of Memory !!!");
			free(Buffer);
			fclose(F);
			exit(4);
		}
	
		if (fseek(F,0,SEEK_SET) != 0)
		{
			printf("Error seeking to beginning of file\n");
			free(String_Output);
			free(Buffer);
			fclose(F);
			exit(5);
		}
	
		Counter = offset;
		bytes_read = fread(Buffer,sizeof(char),filelength,F);
		if (bytes_read >= length)
		{
			for (; length > 0; length -= (disasm_words*2))
			{
				int ii;
				disasm_words = Dasm32025(String_Output,Counter);
				printf("$%04lX: ",Counter);
				for (ii = 0; ii < disasm_words; ii++)
				{
					if (((Counter*2) + ii) > filelength)	/* Past end of length to dump ? */
					{
						sprintf(String_Output,"???? dw %02.2X%02.2Xh (Past end of disassembly !)",Buffer[((Counter-1)*2)],Buffer[((Counter-1)*2)+1]);
					}
					else
					{
						printf("%02.2x%02.2x ",Buffer[(Counter*2)],Buffer[(Counter*2) + 1]);
					}
					Counter++ ;
				}
				for (; ii < 4; ii++)
				{
					printf("   ");
				}
				printf("\t%s\n",String_Output);
			}
		}
		else
		{
			printf("ERROR length to dump was %d ", length_to_dump/2);
			printf(", but bytes read from file were %d\n", bytes_read/2);
			free(String_Output);
			free(Buffer);
			fclose(F);
			exit(7);
		}
		free(String_Output);
		free(Buffer);
		fclose(F);
		return(0);
	}
}
