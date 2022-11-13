/**
 * ported to 0.78
 */
package arcadeflex.v078.platform;

//mame imports
import static arcadeflex.v078.mame.mame.*;
//platform imports
import static arcadeflex.v078.platform.config.*;

public class osdepend {

    /*TODO*/////============================================================
/*TODO*/////
/*TODO*/////	winmain.c - Win32 main program
/*TODO*/////
/*TODO*/////============================================================
/*TODO*///
/*TODO*///// standard windows headers
/*TODO*///#define WIN32_LEAN_AND_MEAN
/*TODO*///#include <windows.h>
/*TODO*///#include <winnt.h>
/*TODO*///#include <mmsystem.h>
/*TODO*///#include <shellapi.h>
/*TODO*///
/*TODO*///// standard includes
/*TODO*///#include <time.h>
/*TODO*///#include <ctype.h>
/*TODO*///
/*TODO*///// MAME headers
/*TODO*///#include "driver.h"
/*TODO*///#include "window.h"
/*TODO*///#include "input.h"
/*TODO*///#include "config.h"
/*TODO*///
/*TODO*///
/*TODO*///#define ENABLE_PROFILER		0
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	TYPE DEFINITIONS
/*TODO*/////============================================================
/*TODO*///
/*TODO*///#define MAX_SYMBOLS		65536
/*TODO*///
/*TODO*///struct map_entry
/*TODO*///{
/*TODO*///	UINT32 start;
/*TODO*///	UINT32 end;
/*TODO*///	UINT32 hits;
/*TODO*///	char *name;
/*TODO*///};
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	GLOBAL VARIABLES
/*TODO*/////============================================================
/*TODO*///
/*TODO*///int verbose;
/*TODO*///
/*TODO*///// this line prevents globbing on the command line
/*TODO*///int _CRT_glob = 0;
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	LOCAL VARIABLES
/*TODO*/////============================================================
/*TODO*///
/*TODO*///static char mapfile_name[MAX_PATH];
/*TODO*///static LPTOP_LEVEL_EXCEPTION_FILTER pass_thru_filter;
/*TODO*///
/*TODO*///static struct map_entry symbol_map[MAX_SYMBOLS];
/*TODO*///static int map_entries;
/*TODO*///
/*TODO*///#if ENABLE_PROFILER
/*TODO*///static HANDLE profiler_thread;
/*TODO*///static DWORD profiler_thread_id;
/*TODO*///static volatile UINT8 profiler_thread_exit;
/*TODO*///#endif
/*TODO*///
/*TODO*///#ifndef MESS
/*TODO*///static const char helpfile[] = "docs\\windows.txt";
/*TODO*///#else
/*TODO*///static const char helpfile[] = "mess.chm";
/*TODO*///#endif
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	PROTOTYPES
/*TODO*/////============================================================
/*TODO*///
/*TODO*///static LONG CALLBACK exception_filter(struct _EXCEPTION_POINTERS *info);
/*TODO*///static const char *lookup_symbol(UINT32 address);
/*TODO*///static int get_code_base_size(UINT32 *base, UINT32 *size);
/*TODO*///static void parse_map_file(void);
/*TODO*///
/*TODO*///#if ENABLE_PROFILER
/*TODO*///static void output_symbol_list(FILE *f);
/*TODO*///static void increment_bucket(UINT32 addr);
/*TODO*///static void start_profiler(void);
/*TODO*///static void stop_profiler(void);
/*TODO*///#endif
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	main
/*TODO*/////============================================================
/*TODO*///
/*TODO*///#ifdef WINUI
/*TODO*///#define main main_
/*TODO*///#endif
/*TODO*///
    public static int main(int argc, String[] argv) {
        int game_index;
        /*TODO*///	char *ext;
        int res = 0;
        /*TODO*///
/*TODO*///#if 1
/*TODO*/// #ifndef WINUI
/*TODO*///	STARTUPINFO startup_info = { sizeof(STARTUPINFO) };
/*TODO*///	GetStartupInfo(&startup_info);
/*TODO*///
/*TODO*///	// try to determine if MAME was simply double-clicked
                if(argc <=1)
                {
                    throw new UnsupportedOperationException("Unsupported");
                }
/*TODO*///	if (argc <= 1 &&
/*TODO*///		startup_info.dwFlags &&
/*TODO*///		!(startup_info.dwFlags & STARTF_USESTDHANDLES))
/*TODO*///	{
/*TODO*///		char message_text[1024] = "";
/*TODO*///		int button;
/*TODO*///		FILE* fp;
/*TODO*///
/*TODO*///  #ifndef MESS
/*TODO*///		sprintf(message_text, APPLONGNAME " v%s - Multiple Arcade Machine Emulator\n"
/*TODO*///							  "Copyright (C) 1997-2003 by Nicola Salmoria and the MAME Team\n"
/*TODO*///							  "\n"
/*TODO*///							  APPLONGNAME " is a console application, you should launch it from a command prompt.\n"
/*TODO*///							  "\n"
/*TODO*///							  "Usage:\tMAME gamename [options]\n"
/*TODO*///							  "\n"
/*TODO*///							  "\tMAME -list\t\tfor a brief list of supported games\n"
/*TODO*///							  "\tMAME -listfull\t\tfor a full list of supported games\n"
/*TODO*///							  "\tMAME -showusage\t\tfor a brief list of options\n"
/*TODO*///							  "\tMAME -showconfig\t\tfor a list of configuration options\n"
/*TODO*///							  "\tMAME -createconfig\tto create a mame.ini\n"
/*TODO*///							  "\n"
/*TODO*///							  "Please consult the documentation for more information.\n"
/*TODO*///							  "\n"
/*TODO*///							  "Would you like to open the documentation now?"
/*TODO*///							  , build_version);
/*TODO*///  #else
/*TODO*///		sprintf(message_text, APPLONGNAME " is a console application, you should launch it from a command prompt.\n"
/*TODO*///							  "\n"
/*TODO*///							  "Please consult the documentation for more information.\n"
/*TODO*///							  "\n"
/*TODO*///							  "Would you like to open the documentation now?");
/*TODO*///  #endif
/*TODO*///
/*TODO*///		// pop up a messagebox with some information
/*TODO*///		button = MessageBox(NULL, message_text, APPLONGNAME " usage information...", MB_YESNO | MB_ICONASTERISK);
/*TODO*///
/*TODO*///		if (button == IDYES)
/*TODO*///		{
/*TODO*///			// check if windows.txt exists
/*TODO*///			fp = fopen(helpfile, "r");
/*TODO*///			if (fp) {
/*TODO*///				fclose(fp);
/*TODO*///
/*TODO*///				// if so, open it with the default application
/*TODO*///				ShellExecute(NULL, "open", helpfile, NULL, NULL, SW_SHOWNORMAL);
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				// if not, inform the user
/*TODO*///				MessageBox(NULL, "Couldn't find the documentation.", "Error...", MB_OK | MB_ICONERROR);
/*TODO*///			}
/*TODO*///		}
/*TODO*///		return 1;
/*TODO*///	}
/*TODO*/// #endif
/*TODO*///#endif
/*TODO*///
/*TODO*///	// parse the map file, if present
/*TODO*///	strcpy(mapfile_name, argv[0]);
/*TODO*///	ext = strchr(mapfile_name, '.');
/*TODO*///	if (ext)
/*TODO*///		strcpy(ext, ".map");
/*TODO*///	else
/*TODO*///		strcat(mapfile_name, ".map");
/*TODO*///	parse_map_file();
/*TODO*///
/*TODO*///	// set up exception handling
/*TODO*///	pass_thru_filter = SetUnhandledExceptionFilter(exception_filter);
/*TODO*///
/*TODO*///	// parse config and cmdline options
        game_index = cli_frontend_init(argc, argv);
        /*TODO*///
/*TODO*///	// remember the initial LED states and init keyboard handle
/*TODO*///	start_led();
/*TODO*///
        // have we decided on a game?
        if (game_index != -1) {
            /*TODO*///		TIMECAPS caps;
/*TODO*///		MMRESULT result;
/*TODO*///
/*TODO*///		// crank up the multimedia timer resolution to its max
/*TODO*///		// this gives the system much finer timeslices
/*TODO*///		result = timeGetDevCaps(&caps, sizeof(caps));
/*TODO*///		if (result == TIMERR_NOERROR)
/*TODO*///			timeBeginPeriod(caps.wPeriodMin);
/*TODO*///
/*TODO*///#if ENABLE_PROFILER
/*TODO*///		start_profiler();
/*TODO*///#endif
/*TODO*///
            // run the game
            res = run_game(game_index);

            /*TODO*///#if ENABLE_PROFILER
/*TODO*///		stop_profiler();
/*TODO*///#endif
/*TODO*///
/*TODO*///		// restore the timer resolution
/*TODO*///		if (result == TIMERR_NOERROR)
/*TODO*///			timeEndPeriod(caps.wPeriodMin);
        }

        /*TODO*///	// restore the original LED state and close keyboard handle
/*TODO*///	stop_led();
/*TODO*///
/*TODO*///	win_process_events();
/*TODO*///
/*TODO*///	// close errorlog, input and playback
/*TODO*///	cli_frontend_exit();
/*TODO*///
/*TODO*///#if ENABLE_PROFILER
/*TODO*///	output_symbol_list(stderr);
/*TODO*///#endif
/*TODO*///
        return res;
    }
    /*TODO*///
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	osd_init
/*TODO*/////============================================================
/*TODO*///
/*TODO*///int osd_init(void)
/*TODO*///{
/*TODO*///	extern int win_init_input(void);
/*TODO*///	int result;
/*TODO*///
/*TODO*///	result = win_init_window();
/*TODO*///	if (result == 0)
/*TODO*///		result = win_init_input();
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	osd_exit
/*TODO*/////============================================================
/*TODO*///
/*TODO*///void osd_exit(void)
/*TODO*///{
/*TODO*///	extern void win_shutdown_input(void);
/*TODO*///	win_shutdown_input();
/*TODO*///	osd_set_leds(0);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	exception_filter
/*TODO*/////============================================================
/*TODO*///
/*TODO*///static LONG CALLBACK exception_filter(struct _EXCEPTION_POINTERS *info)
/*TODO*///{
/*TODO*///	static const struct
/*TODO*///	{
/*TODO*///		DWORD code;
/*TODO*///		const char *string;
/*TODO*///	} exception_table[] =
/*TODO*///	{
/*TODO*///		{ EXCEPTION_ACCESS_VIOLATION,		"ACCESS VIOLATION" },
/*TODO*///		{ EXCEPTION_DATATYPE_MISALIGNMENT,	"DATATYPE MISALIGNMENT" },
/*TODO*///		{ EXCEPTION_BREAKPOINT, 			"BREAKPOINT" },
/*TODO*///		{ EXCEPTION_SINGLE_STEP,			"SINGLE STEP" },
/*TODO*///		{ EXCEPTION_ARRAY_BOUNDS_EXCEEDED,	"ARRAY BOUNDS EXCEEDED" },
/*TODO*///		{ EXCEPTION_FLT_DENORMAL_OPERAND,	"FLOAT DENORMAL OPERAND" },
/*TODO*///		{ EXCEPTION_FLT_DIVIDE_BY_ZERO,		"FLOAT DIVIDE BY ZERO" },
/*TODO*///		{ EXCEPTION_FLT_INEXACT_RESULT,		"FLOAT INEXACT RESULT" },
/*TODO*///		{ EXCEPTION_FLT_INVALID_OPERATION,	"FLOAT INVALID OPERATION" },
/*TODO*///		{ EXCEPTION_FLT_OVERFLOW,			"FLOAT OVERFLOW" },
/*TODO*///		{ EXCEPTION_FLT_STACK_CHECK,		"FLOAT STACK CHECK" },
/*TODO*///		{ EXCEPTION_FLT_UNDERFLOW,			"FLOAT UNDERFLOW" },
/*TODO*///		{ EXCEPTION_INT_DIVIDE_BY_ZERO,		"INTEGER DIVIDE BY ZERO" },
/*TODO*///		{ EXCEPTION_INT_OVERFLOW, 			"INTEGER OVERFLOW" },
/*TODO*///		{ EXCEPTION_PRIV_INSTRUCTION, 		"PRIVILEGED INSTRUCTION" },
/*TODO*///		{ EXCEPTION_IN_PAGE_ERROR, 			"IN PAGE ERROR" },
/*TODO*///		{ EXCEPTION_ILLEGAL_INSTRUCTION, 	"ILLEGAL INSTRUCTION" },
/*TODO*///		{ EXCEPTION_NONCONTINUABLE_EXCEPTION,"NONCONTINUABLE EXCEPTION" },
/*TODO*///		{ EXCEPTION_STACK_OVERFLOW, 		"STACK OVERFLOW" },
/*TODO*///		{ EXCEPTION_INVALID_DISPOSITION, 	"INVALID DISPOSITION" },
/*TODO*///		{ EXCEPTION_GUARD_PAGE, 			"GUARD PAGE VIOLATION" },
/*TODO*///		{ EXCEPTION_INVALID_HANDLE, 		"INVALID HANDLE" },
/*TODO*///		{ 0,								"UNKNOWN EXCEPTION" }
/*TODO*///	};
/*TODO*///	static int already_hit = 0;
/*TODO*///	UINT32 code_start, code_size;
/*TODO*///	int i;
/*TODO*///
/*TODO*///	// if we're hitting this recursively, just exit
/*TODO*///	if (already_hit)
/*TODO*///		return EXCEPTION_EXECUTE_HANDLER;
/*TODO*///	already_hit = 1;
/*TODO*///
/*TODO*///	// find our man
/*TODO*///	for (i = 0; exception_table[i].code != 0; i++)
/*TODO*///		if (info->ExceptionRecord->ExceptionCode == exception_table[i].code)
/*TODO*///			break;
/*TODO*///
/*TODO*///	// print the exception type and address
/*TODO*///	fprintf(stderr, "\n-----------------------------------------------------\n");
/*TODO*///	fprintf(stderr, "Exception at EIP=%08X%s: %s\n", (UINT32)info->ExceptionRecord->ExceptionAddress,
/*TODO*///			lookup_symbol((UINT32)info->ExceptionRecord->ExceptionAddress), exception_table[i].string);
/*TODO*///
/*TODO*///	// for access violations, print more info
/*TODO*///	if (info->ExceptionRecord->ExceptionCode == EXCEPTION_ACCESS_VIOLATION)
/*TODO*///		fprintf(stderr, "While attempting to %s memory at %08X\n",
/*TODO*///				info->ExceptionRecord->ExceptionInformation[0] ? "write" : "read",
/*TODO*///				(UINT32)info->ExceptionRecord->ExceptionInformation[1]);
/*TODO*///
/*TODO*///	// print the state of the CPU
/*TODO*///	fprintf(stderr, "-----------------------------------------------------\n");
/*TODO*///	fprintf(stderr, "EAX=%08X EBX=%08X ECX=%08X EDX=%08X\n",
/*TODO*///			(UINT32)info->ContextRecord->Eax,
/*TODO*///			(UINT32)info->ContextRecord->Ebx,
/*TODO*///			(UINT32)info->ContextRecord->Ecx,
/*TODO*///			(UINT32)info->ContextRecord->Edx);
/*TODO*///	fprintf(stderr, "ESI=%08X EDI=%08X EBP=%08X ESP=%08X\n",
/*TODO*///			(UINT32)info->ContextRecord->Esi,
/*TODO*///			(UINT32)info->ContextRecord->Edi,
/*TODO*///			(UINT32)info->ContextRecord->Ebp,
/*TODO*///			(UINT32)info->ContextRecord->Esp);
/*TODO*///
/*TODO*///	// crawl the stack for a while
/*TODO*///	if (get_code_base_size(&code_start, &code_size))
/*TODO*///	{
/*TODO*///		char prev_symbol[1024], curr_symbol[1024];
/*TODO*///		UINT32 last_call = (UINT32)info->ExceptionRecord->ExceptionAddress;
/*TODO*///		UINT32 esp_start = info->ContextRecord->Esp;
/*TODO*///		UINT32 esp_end = (esp_start | 0xffff) + 1;
/*TODO*///		UINT32 esp;
/*TODO*///
/*TODO*///		// reprint the actual exception address
/*TODO*///		fprintf(stderr, "-----------------------------------------------------\n");
/*TODO*///		fprintf(stderr, "Stack crawl:\n");
/*TODO*///		fprintf(stderr, "exception-> %08X%s\n", last_call, strcpy(prev_symbol, lookup_symbol(last_call)));
/*TODO*///
/*TODO*///		// crawl the stack until we hit the next 64k boundary
/*TODO*///		for (esp = esp_start; esp < esp_end; esp += 4)
/*TODO*///		{
/*TODO*///			UINT32 stack_val = *(UINT32 *)esp;
/*TODO*///
/*TODO*///			// if the value on the stack points within the code block, check it out
/*TODO*///			if (stack_val >= code_start && stack_val < code_start + code_size)
/*TODO*///			{
/*TODO*///				UINT8 *return_addr = (UINT8 *)stack_val;
/*TODO*///				UINT32 call_target = 0;
/*TODO*///
/*TODO*///				// make sure the code that we think got us here is actually a CALL instruction
/*TODO*///				if (return_addr[-5] == 0xe8)
/*TODO*///					call_target = stack_val - 5 + *(INT32 *)&return_addr[-4];
/*TODO*///				if ((return_addr[-2] == 0xff && (return_addr[-1] & 0x38) == 0x10) ||
/*TODO*///					(return_addr[-3] == 0xff && (return_addr[-2] & 0x38) == 0x10) ||
/*TODO*///					(return_addr[-4] == 0xff && (return_addr[-3] & 0x38) == 0x10) ||
/*TODO*///					(return_addr[-5] == 0xff && (return_addr[-4] & 0x38) == 0x10) ||
/*TODO*///					(return_addr[-6] == 0xff && (return_addr[-5] & 0x38) == 0x10) ||
/*TODO*///					(return_addr[-7] == 0xff && (return_addr[-6] & 0x38) == 0x10))
/*TODO*///					call_target = 1;
/*TODO*///
/*TODO*///				// make sure it points somewhere a little before the last call
/*TODO*///				if (call_target == 1 || (call_target < last_call && call_target >= last_call - 0x1000))
/*TODO*///				{
/*TODO*///					char *stop_compare = strchr(prev_symbol, '+');
/*TODO*///
/*TODO*///					// don't print duplicate hits in the same routine
/*TODO*///					strcpy(curr_symbol, lookup_symbol(stack_val));
/*TODO*///					if (stop_compare == NULL || strncmp(curr_symbol, prev_symbol, stop_compare - prev_symbol))
/*TODO*///					{
/*TODO*///						strcpy(prev_symbol, curr_symbol);
/*TODO*///						fprintf(stderr, "  %08X: %08X%s\n", esp, stack_val, curr_symbol);
/*TODO*///						last_call = stack_val;
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	logerror("shutting down after exception\n");
/*TODO*///
/*TODO*///	cli_frontend_exit();
/*TODO*///
/*TODO*///	// exit
/*TODO*///	return EXCEPTION_EXECUTE_HANDLER;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	lookup_symbol
/*TODO*/////============================================================
/*TODO*///
/*TODO*///static const char *lookup_symbol(UINT32 address)
/*TODO*///{
/*TODO*///	static char buffer[1024];
/*TODO*///	FILE *	map = fopen(mapfile_name, "r");
/*TODO*///	char	symbol[1024], best_symbol[1024];
/*TODO*///	UINT32	addr, best_addr = 0;
/*TODO*///	char	line[1024];
/*TODO*///
/*TODO*///	// if no file, return nothing
/*TODO*///	if (map == NULL)
/*TODO*///		return "";
/*TODO*///
/*TODO*///	// reset the bests
/*TODO*///	*best_symbol = 0;
/*TODO*///	best_addr = 0;
/*TODO*///
/*TODO*///	// parse the file, looking for map entries
/*TODO*///	while (fgets(line, sizeof(line) - 1, map))
/*TODO*///		if (!strncmp(line, "                0x", 18))
/*TODO*///			if (sscanf(line, "                0x%08x %s", &addr, symbol) == 2)
/*TODO*///				if (addr <= address && addr > best_addr)
/*TODO*///				{
/*TODO*///					best_addr = addr;
/*TODO*///					strcpy(best_symbol, symbol);
/*TODO*///				}
/*TODO*///
/*TODO*///	// create the final result
/*TODO*///	if (address - best_addr > 0x10000)
/*TODO*///		return "";
/*TODO*///	sprintf(buffer, " (%s+0x%04x)", best_symbol, address - best_addr);
/*TODO*///	return buffer;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	get_code_base_size
/*TODO*/////============================================================
/*TODO*///
/*TODO*///static int get_code_base_size(UINT32 *base, UINT32 *size)
/*TODO*///{
/*TODO*///	FILE *	map = fopen(mapfile_name, "r");
/*TODO*///	char	line[1024];
/*TODO*///
/*TODO*///	// if no file, return nothing
/*TODO*///	if (map == NULL)
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	// parse the file, looking for .text entry
/*TODO*///	while (fgets(line, sizeof(line) - 1, map))
/*TODO*///		if (!strncmp(line, ".text           0x", 18))
/*TODO*///			if (sscanf(line, ".text           0x%08x 0x%x", base, size) == 2)
/*TODO*///				return 1;
/*TODO*///
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	compare_base
/*TODO*/////	compare_hits -- qsort callbacks to sort on
/*TODO*/////============================================================
/*TODO*///
/*TODO*///static int CLIB_DECL compare_start(const void *item1, const void *item2)
/*TODO*///{
/*TODO*///	return ((const struct map_entry *)item1)->start - ((const struct map_entry *)item2)->start;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///#if ENABLE_PROFILER
/*TODO*///static int compare_hits(const void *item1, const void *item2)
/*TODO*///{
/*TODO*///	return ((const struct map_entry *)item2)->hits - ((const struct map_entry *)item1)->hits;
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	parse_map_file
/*TODO*/////============================================================
/*TODO*///
/*TODO*///static void parse_map_file(void)
/*TODO*///{
/*TODO*///	int got_text = 0;
/*TODO*///	char line[1024];
/*TODO*///	FILE *map;
/*TODO*///	int i;
/*TODO*///
/*TODO*///	// open the map file
/*TODO*///	map = fopen(mapfile_name, "r");
/*TODO*///	if (!map)
/*TODO*///		return;
/*TODO*///
/*TODO*///	// parse out the various symbols into map entries
/*TODO*///	map_entries = 0;
/*TODO*///	while (fgets(line, sizeof(line) - 1, map))
/*TODO*///	{
/*TODO*///		/* look for the code boundaries */
/*TODO*///		if (!got_text && !strncmp(line, ".text           0x", 18))
/*TODO*///		{
/*TODO*///			UINT32 base, size;
/*TODO*///			if (sscanf(line, ".text           0x%08x 0x%x", &base, &size) == 2)
/*TODO*///			{
/*TODO*///				symbol_map[map_entries].start = base;
/*TODO*///				symbol_map[map_entries].name = strcpy(malloc(strlen("Code start") + 1), "Code start");
/*TODO*///				map_entries++;
/*TODO*///				symbol_map[map_entries].start = base + size;
/*TODO*///				symbol_map[map_entries].name = strcpy(malloc(strlen("Other") + 1), "Other");
/*TODO*///				map_entries++;
/*TODO*///				got_text = 1;
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* look for symbols */
/*TODO*///		else if (!strncmp(line, "                0x", 18))
/*TODO*///		{
/*TODO*///			char symbol[1024];
/*TODO*///			UINT32 addr;
/*TODO*///			if (sscanf(line, "                0x%08x %s", &addr, symbol) == 2)
/*TODO*///			{
/*TODO*///				symbol_map[map_entries].start = addr;
/*TODO*///				symbol_map[map_entries].name = strcpy(malloc(strlen(symbol) + 1), symbol);
/*TODO*///				map_entries++;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* add a symbol for end-of-memory */
/*TODO*///	symbol_map[map_entries].start = ~0;
/*TODO*///	symbol_map[map_entries].name = strcpy(malloc(strlen("<end>") + 1), "<end>");
/*TODO*///
/*TODO*///	/* close the file */
/*TODO*///	fclose(map);
/*TODO*///
/*TODO*///	/* sort by address */
/*TODO*///	qsort(symbol_map, map_entries, sizeof(symbol_map[0]), compare_start);
/*TODO*///
/*TODO*///	/* fill in the end of each bucket */
/*TODO*///	for (i = 0; i < map_entries; i++)
/*TODO*///		symbol_map[i].end = symbol_map[i+1].start ? (symbol_map[i+1].start - 1) : 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///#if ENABLE_PROFILER
/*TODO*/////============================================================
/*TODO*/////	output_symbol_list
/*TODO*/////============================================================
/*TODO*///
/*TODO*///static void output_symbol_list(FILE *f)
/*TODO*///{
/*TODO*///	struct map_entry *entry;
/*TODO*///	int i;
/*TODO*///
/*TODO*///	/* sort by hits */
/*TODO*///	qsort(symbol_map, map_entries, sizeof(symbol_map[0]), compare_hits);
/*TODO*///
/*TODO*///	for (i = 0, entry = symbol_map; i < map_entries; i++, entry++)
/*TODO*///		if (entry->hits > 0)
/*TODO*///			fprintf(f, "%10d  %08X-%08X  %s\n", entry->hits, entry->start, entry->end, entry->name);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	increment_bucket
/*TODO*/////============================================================
/*TODO*///
/*TODO*///static void increment_bucket(UINT32 addr)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///
/*TODO*///	for (i = 0; i < map_entries; i++)
/*TODO*///		if (addr <= symbol_map[i].end)
/*TODO*///		{
/*TODO*///			symbol_map[i].hits++;
/*TODO*///			return;
/*TODO*///		}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	profiler_thread
/*TODO*/////============================================================
/*TODO*///
/*TODO*///static DWORD WINAPI profiler_thread_entry(LPVOID lpParameter)
/*TODO*///{
/*TODO*///	HANDLE mainThread = (HANDLE)lpParameter;
/*TODO*///	CONTEXT context;
/*TODO*///
/*TODO*///	/* loop until done */
/*TODO*///	memset(&context, 0, sizeof(context));
/*TODO*///	while (!profiler_thread_exit)
/*TODO*///	{
/*TODO*///		/* pause the main thread and get its context */
/*TODO*///		SuspendThread(mainThread);
/*TODO*///		context.ContextFlags = CONTEXT_FULL;
/*TODO*///		GetThreadContext(mainThread, &context);
/*TODO*///		ResumeThread(mainThread);
/*TODO*///
/*TODO*///		/* add to the bucket */
/*TODO*///		increment_bucket(context.Eip);
/*TODO*///
/*TODO*///		/* sleep */
/*TODO*///		Sleep(1);
/*TODO*///	}
/*TODO*///
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	start_profiler
/*TODO*/////============================================================
/*TODO*///
/*TODO*///static void start_profiler(void)
/*TODO*///{
/*TODO*///	HANDLE currentThread;
/*TODO*///
/*TODO*///	/* do the dance to get a handle to ourself */
/*TODO*///	if (!DuplicateHandle(GetCurrentProcess(), GetCurrentThread(), GetCurrentProcess(), &currentThread,
/*TODO*///			THREAD_GET_CONTEXT | THREAD_SUSPEND_RESUME | THREAD_QUERY_INFORMATION, FALSE, 0))
/*TODO*///	{
/*TODO*///		fprintf(stderr, "Failed to get thread handle for main thread\n");
/*TODO*///		exit(1);
/*TODO*///	}
/*TODO*///
/*TODO*///	profiler_thread_exit = 0;
/*TODO*///
/*TODO*///	/* start the thread */
/*TODO*///	profiler_thread = CreateThread(NULL, 0, profiler_thread_entry, (LPVOID)currentThread, 0, &profiler_thread_id);
/*TODO*///	if (!profiler_thread)
/*TODO*///		fprintf(stderr, "Failed to create profiler thread\n");
/*TODO*///
/*TODO*///	/* max out the priority */
/*TODO*///	SetThreadPriority(profiler_thread, THREAD_PRIORITY_TIME_CRITICAL);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*/////============================================================
/*TODO*/////	stop_profiler
/*TODO*/////============================================================
/*TODO*///
/*TODO*///static void stop_profiler(void)
/*TODO*///{
/*TODO*///	profiler_thread_exit = 1;
/*TODO*///	WaitForSingleObject(profiler_thread, 2000);
/*TODO*///}
/*TODO*///#endif
/*TODO*///    
}
