/**
 * ported to v0.78
 */
package arcadeflex.v078.mame;

public class ui_text {
    /*TODO*////*********************************************************************
/*TODO*///
/*TODO*///  ui_text.c
/*TODO*///
/*TODO*///  Functions used to retrieve text used by MAME, to aid in
/*TODO*///  translation.
/*TODO*///
/*TODO*///*********************************************************************/
/*TODO*///
/*TODO*///#include "driver.h"
/*TODO*///#include "ui_text.h"
/*TODO*///
/*TODO*///#ifdef MESS
/*TODO*///extern const char *mess_default_text[];
/*TODO*///#endif /* MESS */
/*TODO*///
/*TODO*///
/*TODO*///struct lang_struct lang;
/*TODO*///
/*TODO*////* All entries in this table must match the enum ordering in "ui_text.h" */
/*TODO*///static const char *mame_default_text[] =
/*TODO*///{
/*TODO*///#ifndef MESS
/*TODO*///	"MAME",
/*TODO*///#else
/*TODO*///	"MESS",
/*TODO*///#endif
/*TODO*///
/*TODO*///	/* copyright stuff */
/*TODO*///	"Usage of emulators in conjunction with ROMs you don't own is forbidden by copyright law.",
/*TODO*///	"IF YOU ARE NOT LEGALLY ENTITLED TO PLAY \"%s\" ON THIS EMULATOR, PRESS ESC.",
/*TODO*///	"Otherwise, type OK to continue",
/*TODO*///
/*TODO*///	/* misc stuff */
/*TODO*///	"Return to Main Menu",
/*TODO*///	"Return to Prior Menu",
/*TODO*///	"Press Any Key",
/*TODO*///	"On",
/*TODO*///	"Off",
/*TODO*///	"NA",
/*TODO*///	"OK",
/*TODO*///	"INVALID",
/*TODO*///	"(none)",
/*TODO*///	"CPU",
/*TODO*///	"Address",
/*TODO*///	"Value",
/*TODO*///	"Sound",
/*TODO*///	"sound",
/*TODO*///	"stereo",
/*TODO*///	"Vector Game",
/*TODO*///	"Screen Resolution",
/*TODO*///	"Text",
/*TODO*///	"Volume",
/*TODO*///	"Relative",
/*TODO*///	"ALL CHANNELS",
/*TODO*///	"Brightness",
/*TODO*///	"Gamma",
/*TODO*///	"Vector Flicker",
/*TODO*///	"Vector Intensity",
/*TODO*///	"Overclock",
/*TODO*///	"ALL CPUS",
/*TODO*///#ifndef MESS
/*TODO*///	"History not available",
/*TODO*///#else
/*TODO*///	"System Info not available",
/*TODO*///#endif
/*TODO*///
/*TODO*///	/* special characters */
/*TODO*///	"\x11",
/*TODO*///	"\x10",
/*TODO*///	"\x18",
/*TODO*///	"\x19",
/*TODO*///	"\x1a",
/*TODO*///	"\x1b",
/*TODO*///
/*TODO*///	/* known problems */
/*TODO*///#ifndef MESS
/*TODO*///	"There are known problems with this game:",
/*TODO*///#else
/*TODO*///	"There are known problems with this system",
/*TODO*///#endif
/*TODO*///	"The colors aren't 100% accurate.",
/*TODO*///	"The colors are completely wrong.",
/*TODO*///	"The video emulation isn't 100% accurate.",
/*TODO*///	"The sound emulation isn't 100% accurate.",
/*TODO*///	"The game lacks sound.",
/*TODO*///	"Screen flipping in cocktail mode is not supported.",
/*TODO*///#ifndef MESS
/*TODO*///	"THIS GAME DOESN'T WORK PROPERLY",
/*TODO*///#else
/*TODO*///	"THIS SYSTEM DOESN'T WORK PROPERLY",
/*TODO*///#endif
/*TODO*///	"The game has protection which isn't fully emulated.",
/*TODO*///	"There are working clones of this game. They are:",
/*TODO*///	"Type OK to continue",
/*TODO*///
/*TODO*///	/* main menu */
/*TODO*///	"Input (general)",
/*TODO*///	"Dip Switches",
/*TODO*///	"Analog Controls",
/*TODO*///	"Calibrate Joysticks",
/*TODO*///	"Bookkeeping Info",
/*TODO*///
/*TODO*///#ifndef MESS
/*TODO*///	"Input (this game)",
/*TODO*///	"Game Information",
/*TODO*///	"Game History",
/*TODO*///	"Reset Game",
/*TODO*///	"Return to Game",
/*TODO*///#else
/*TODO*///	"Input (this machine)",
/*TODO*///	"Machine Information",
/*TODO*///	"Machine Usage & History",
/*TODO*///	"Reset Machine",
/*TODO*///	"Return to Machine",
/*TODO*///#endif /* MESS */
/*TODO*///
/*TODO*///	"Cheat",
/*TODO*///	"Memory Card",
/*TODO*///
/*TODO*///	/* input */
/*TODO*///	"Key/Joy Speed",
/*TODO*///	"Reverse",
/*TODO*///	"Sensitivity",
/*TODO*///
/*TODO*///	/* stats */
/*TODO*///	"Tickets dispensed",
/*TODO*///	"Coin",
/*TODO*///	"(locked)",
/*TODO*///
/*TODO*///	/* memory card */
/*TODO*///	"Load Memory Card",
/*TODO*///	"Eject Memory Card",
/*TODO*///	"Create Memory Card",
/*TODO*///	"Failed To Load Memory Card!",
/*TODO*///	"Load OK!",
/*TODO*///	"Memory Card Ejected!",
/*TODO*///	"Memory Card Created OK!",
/*TODO*///	"Failed To Create Memory Card!",
/*TODO*///	"(It already exists ?)",
/*TODO*///	"DAMN!! Internal Error!",
/*TODO*///
/*TODO*///	/* cheats */
/*TODO*///	"Enable/Disable a Cheat",
/*TODO*///	"Add/Edit a Cheat",
/*TODO*///	"Start a New Cheat Search",
/*TODO*///	"Continue Search",
/*TODO*///	"View Last Results",
/*TODO*///	"Restore Previous Results",
/*TODO*///	"Configure Watchpoints",
/*TODO*///	"General Help",
/*TODO*///	"Options",
/*TODO*///	"Reload Database",
/*TODO*///	"Watchpoint",
/*TODO*///	"Disabled",
/*TODO*///	"Cheats",
/*TODO*///	"Watchpoints",
/*TODO*///	"More Info",
/*TODO*///	"More Info for",
/*TODO*///	"Name",
/*TODO*///	"Description",
/*TODO*///	"Activation Key",
/*TODO*///	"Code",
/*TODO*///	"Max",
/*TODO*///	"Set",
/*TODO*///	"Cheat conflict found: disabling",
/*TODO*///	"Help not available yet",
/*TODO*///
/*TODO*///	/* watchpoints */
/*TODO*///	"Number of bytes",
/*TODO*///	"Display Type",
/*TODO*///	"Label Type",
/*TODO*///	"Label",
/*TODO*///	"X Position",
/*TODO*///	"Y Position",
/*TODO*///	"Watch",
/*TODO*///
/*TODO*///	"Hex",
/*TODO*///	"Decimal",
/*TODO*///	"Binary",
/*TODO*///
/*TODO*///	/* searching */
/*TODO*///	"Lives (or another value)",
/*TODO*///	"Timers (+/- some value)",
/*TODO*///	"Energy (greater or less)",
/*TODO*///	"Status (bits or flags)",
/*TODO*///	"Slow But Sure (changed or not)",
/*TODO*///	"Default Search Speed",
/*TODO*///	"Fast",
/*TODO*///	"Medium",
/*TODO*///	"Slow",
/*TODO*///	"Very Slow",
/*TODO*///	"All Memory",
/*TODO*///	"Select Memory Areas",
/*TODO*///	"Matches found",
/*TODO*///	"Search not initialized",
/*TODO*///	"No previous values saved",
/*TODO*///	"Previous values already restored",
/*TODO*///	"Restoration successful",
/*TODO*///	"Select a value",
/*TODO*///	"All values saved",
/*TODO*///	"One match found - added to list",
/*TODO*///
/*TODO*///	NULL
/*TODO*///};
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///static const char **default_text[] =
/*TODO*///{
/*TODO*///	mame_default_text,
/*TODO*///#ifdef MESS
/*TODO*///	mess_default_text,
/*TODO*///#endif /* MESS */
/*TODO*///	NULL
/*TODO*///};
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///static const char **trans_text;
/*TODO*///
/*TODO*///
/*TODO*///int uistring_init (mame_file *langfile)
/*TODO*///{
/*TODO*///	/*
/*TODO*///		TODO: This routine needs to do several things:
/*TODO*///			- load an external font if needed
/*TODO*///			- determine the number of characters in the font
/*TODO*///			- deal with multibyte languages
/*TODO*///
/*TODO*///	*/
/*TODO*///
/*TODO*///	int i, j, str;
/*TODO*///	char curline[255];
/*TODO*///	char section[255] = "\0";
/*TODO*///	char *ptr;
/*TODO*///	int string_count;
/*TODO*///
/*TODO*///	/* count the total amount of strings */
/*TODO*///	string_count = 0;
/*TODO*///	for (i = 0; default_text[i]; i++)
/*TODO*///	{
/*TODO*///		for (j = 0; default_text[i][j]; j++)
/*TODO*///			string_count++;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* allocate the translated text array, and set defaults */
/*TODO*///	trans_text = auto_malloc(sizeof(const char *) * string_count);
/*TODO*///	if (!trans_text)
/*TODO*///		return 1;
/*TODO*///
/*TODO*///	/* copy in references to all of the strings */
/*TODO*///	str = 0;
/*TODO*///	for (i = 0; default_text[i]; i++)
/*TODO*///	{
/*TODO*///		for (j = 0; default_text[i][j]; j++)
/*TODO*///			trans_text[str++] = default_text[i][j];
/*TODO*///	}
/*TODO*///
/*TODO*///	memset(&lang, 0, sizeof(lang));
/*TODO*///
/*TODO*///	/* if no language file, exit */
/*TODO*///	if (!langfile)
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	while (mame_fgets (curline, sizeof(curline) / sizeof(curline[0]), langfile) != NULL)
/*TODO*///	{
/*TODO*///		/* Ignore commented and blank lines */
/*TODO*///		if (curline[0] == ';') continue;
/*TODO*///		if (curline[0] == '\n') continue;
/*TODO*///		if (curline[0] == '\r') continue;
/*TODO*///
/*TODO*///		if (curline[0] == '[')
/*TODO*///		{
/*TODO*///			ptr = strtok (&curline[1], "]");
/*TODO*///			/* Found a section, indicate as such */
/*TODO*///			strcpy (section, ptr);
/*TODO*///
/*TODO*///			/* Skip to the next line */
/*TODO*///			continue;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* Parse the LangInfo section */
/*TODO*///		if (strcmp (section, "LangInfo") == 0)
/*TODO*///		{
/*TODO*///			ptr = strtok (curline, "=");
/*TODO*///			if (strcmp (ptr, "Version") == 0)
/*TODO*///			{
/*TODO*///				ptr = strtok (NULL, "\n\r");
/*TODO*///				sscanf (ptr, "%d", &lang.version);
/*TODO*///			}
/*TODO*///			else if (strcmp (ptr, "Language") == 0)
/*TODO*///			{
/*TODO*///				ptr = strtok (NULL, "\n\r");
/*TODO*///				strcpy (lang.langname, ptr);
/*TODO*///			}
/*TODO*///			else if (strcmp (ptr, "Author") == 0)
/*TODO*///			{
/*TODO*///				ptr = strtok (NULL, "\n\r");
/*TODO*///				strcpy (lang.author, ptr);
/*TODO*///			}
/*TODO*///			else if (strcmp (ptr, "Font") == 0)
/*TODO*///			{
/*TODO*///				ptr = strtok (NULL, "\n\r");
/*TODO*///				strcpy (lang.fontname, ptr);
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* Parse the Strings section */
/*TODO*///		if (strcmp (section, "Strings") == 0)
/*TODO*///		{
/*TODO*///			/* Get all text up to the first line ending */
/*TODO*///			ptr = strtok (curline, "\n\r");
/*TODO*///
/*TODO*///			/* Find a matching default string */
/*TODO*///			str = 0;
/*TODO*///			for (i = 0; default_text[i]; i++)
/*TODO*///			{
/*TODO*///				for (j = 0; default_text[i][j]; j++)
/*TODO*///				{
/*TODO*///					if (strcmp (curline, default_text[i][j]) == 0)
/*TODO*///				{
/*TODO*///					char transline[255];
/*TODO*///
/*TODO*///					/* Found a match, read next line as the translation */
/*TODO*///					mame_fgets (transline, 255, langfile);
/*TODO*///
/*TODO*///					/* Get all text up to the first line ending */
/*TODO*///					ptr = strtok (transline, "\n\r");
/*TODO*///
/*TODO*///					/* Allocate storage and copy the string */
/*TODO*///						trans_text[str] = auto_strdup(transline);
/*TODO*///						if (!trans_text[str])
/*TODO*///							return 1;
/*TODO*///					}
/*TODO*///					str++;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* indicate success */
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///const char * ui_getstring (int string_num)
/*TODO*///{
/*TODO*///		return trans_text[string_num];
/*TODO*///}
/*TODO*///    
}
