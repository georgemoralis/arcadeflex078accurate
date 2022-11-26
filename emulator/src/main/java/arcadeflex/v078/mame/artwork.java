/**
 * ported to v0.78
 */
package arcadeflex.v078.mame;

public class artwork {
    /*TODO*///
/*TODO*////*********************************************************************
/*TODO*///
/*TODO*///	artwork.c
/*TODO*///
/*TODO*///	Second generation artwork implementation.
/*TODO*///
/*TODO*///	Still to do:
/*TODO*///		- tinting
/*TODO*///		- mechanism to disable built-in artwork
/*TODO*///
/*TODO*///	Longer term:
/*TODO*///		- struct mame_layer
/*TODO*///		  {
/*TODO*///		  	struct mame_bitmap *bitmap;
/*TODO*///		  	int rectcount;
/*TODO*///		  	struct rectangle rectlist[MAX_RECTS];
/*TODO*///		  }
/*TODO*///		- add 4 mame_layers for backdrop, overlay, bezel, ui to mame_display
/*TODO*///		- some mechanism to let the OSD do the blending
/*TODO*///		- MMX optimized implementations
/*TODO*///
/*TODO*///**********************************************************************
/*TODO*///
/*TODO*///	This file represents the second attempt at providing external
/*TODO*///	artwork support. Some parts of this code are based on the
/*TODO*///	original version, by Mike Balfour and Mathis Rosenhauer.
/*TODO*///
/*TODO*///	The goal: to provide artwork support with minimal knowledge of
/*TODO*///	the game drivers. The previous implementation required the
/*TODO*///	game drivers to allocate extra pens, extend the screen bitmap,
/*TODO*///	and handle a lot of the mundane details by hand. This is no
/*TODO*///	longer the case.
/*TODO*///
/*TODO*///	The key to all this is the .art file. A .art file is just a
/*TODO*///	text file describing all the artwork needed for a particular
/*TODO*///	game. It lives either in the $ARTWORK/gamename/ directory
/*TODO*///	or in the $ARTWORK/gamename.zip file, and is called
/*TODO*///	gamename.art.
/*TODO*///
/*TODO*///**********************************************************************
/*TODO*///
/*TODO*///	THE ART FILE
/*TODO*///
/*TODO*///	The .art file is very simply formatted. It consists of any
/*TODO*///	number of entries that look like this:
/*TODO*///
/*TODO*///	[artname]:
/*TODO*///		file       = [filename]
/*TODO*///		alphafile  = [alphafilename]
/*TODO*///		layer      = [backdrop|overlay|bezel|marquee|panel|side|flyer]
/*TODO*///		position   = [left],[top],[right],[bottom]
/*TODO*///		priority   = [priority]
/*TODO*///		visible    = [visible]
/*TODO*///		alpha      = [alpha]
/*TODO*///		brightness = [brightness]
/*TODO*///
/*TODO*///	Comments in the .art file follow standard C++ comment format,
/*TODO*///	starting with a double-slash //. C-style comments are not
/*TODO*///	recognized.
/*TODO*///
/*TODO*///	Fields are:
/*TODO*///
/*TODO*///	[artname] - name that is used to reference this piece of
/*TODO*///		artwork in the game driver. Game drivers can show/hide
/*TODO*///		pieces of artwork. It is permissible to use the same
/*TODO*///		name for multiple pieces; in that case, a show/hide
/*TODO*///		command from the game will affect all pieces with that
/*TODO*///		name. This field is required.
/*TODO*///
/*TODO*///	file - name of the PNG file containing the main artwork.
/*TODO*///		This file should live in the same directory as the .art
/*TODO*///		file itself. Most PNG formats are supported. If the
/*TODO*///		PNG file does not have an alpha channel or transparent
/*TODO*///		colors, it will be loaded fully opaque. This field is
/*TODO*///		required.
/*TODO*///
/*TODO*///	alphafile - name of a PNG file containing the alpha channel.
/*TODO*///		Like the main file, this file should live in the same
/*TODO*///		directory as the .art file. The alphafile must have the
/*TODO*///		exact same dimensions as the main art file in order to
/*TODO*///		be valid. When loaded, the brightness of each pixel in
/*TODO*///		the alphafile controls the alpha channel for the
/*TODO*///		corresponding pixel in the main art.
/*TODO*///
/*TODO*///	layer - classifies this piece of artwork into one of several
/*TODO*///		predefined categories. Command line options can control
/*TODO*///		which categories of artwork are actually displayed. The
/*TODO*///		layer is also used to group the artwork for rendering
/*TODO*///		(see discussion of rendering below.) This field is
/*TODO*///		required.
/*TODO*///
/*TODO*///	position - specifies the position of this piece of artwork
/*TODO*///		relative to the game bitmap. See the section on
/*TODO*///		positioning, below, for the precise details. This field
/*TODO*///		is required.
/*TODO*///
/*TODO*///	priority - specifies the front-to-back ordering of this
/*TODO*///		piece of art. The various artwork pieces are assembled
/*TODO*///		from the bottom up, lowest priority to highest priority.
/*TODO*///		If you want a piece of artwork to appear behind another
/*TODO*///		piece of artwork, use a lower priority. The default
/*TODO*///		priority is 0.
/*TODO*///
/*TODO*///	visible - sets the initial visible state. By default, all
/*TODO*///		artwork is visible. The driver code can change this state
/*TODO*///		at runtime.
/*TODO*///
/*TODO*///	alpha - specifies a global, additional alpha value for the
/*TODO*///		entire piece of artwork. This alpha value is multiplied
/*TODO*///		by the per-pixel alpha value for the loaded artwork.
/*TODO*///		The default value is 1.0, which has no net effect on the
/*TODO*///		loaded alpha. An alpha of 0.0 will make the entire piece
/*TODO*///		of artwork fully transparent.
/*TODO*///
/*TODO*///	brightness - specifies a global brightness adjustment factor
/*TODO*///		for the entire piece of artwork. The red, green, and blue
/*TODO*///		components of every pixel are multiplied by this value
/*TODO*///		when the image is loaded. The default value is 1.0, which
/*TODO*///		has no net effect on the loaded artwork. A brightness
/*TODO*///		value of 0.0 will produce an entirely black image.
/*TODO*///
/*TODO*///	Once the .art file is loaded, the artwork is categories into
/*TODO*///	three groups: backdrops, overlays, and everything else. Each
/*TODO*///	of these groups is handled in its own way.
/*TODO*///
/*TODO*///**********************************************************************
/*TODO*///
/*TODO*///	BLENDING
/*TODO*///
/*TODO*///	Conceptually, here is how it all fits together:
/*TODO*///
/*TODO*///	1. A combined backdrop bitmap is assembled. This consists of
/*TODO*///	taking an opaque black bitmap, and alpha blending all the
/*TODO*///	backdrop graphics, in order from lowest priority to highest,
/*TODO*///	into it.
/*TODO*///
/*TODO*///	2. A combined overlay bitmap is assembled. This consists of
/*TODO*///	taking a translucent white overlay and performing a CMY blend
/*TODO*///	of all the overlay graphics, in order from lowest priority to
/*TODO*///	highest, into it.
/*TODO*///
/*TODO*///	3. A combined bezel bitmap is assembled. This consists of
/*TODO*///	taking a fully transparent bitmap, and alpha blending all the
/*TODO*///	bezel, marquee, panel, side, and flyer graphics, in order from
/*TODO*///	lowest to highest, into it.
/*TODO*///
/*TODO*///	4. Depending on the user configurable artwork scale setting,
/*TODO*///	the game bitmap is potentially expanded 2x.
/*TODO*///
/*TODO*///	5. The combined overlay bitmap is applied to the game bitmap,
/*TODO*///	by using the brightness of the game pixel to control the
/*TODO*///	brightness of the corresponding overlay bitmap pixel, as
/*TODO*///	follows:
/*TODO*///
/*TODO*///		RGB[mix1] = (RGB[overlay] * A[overlay]) +
/*TODO*///				(RGB[overlay] - RGB[overlay] * A[overlay]) * Y[game];
/*TODO*///
/*TODO*///	where
/*TODO*///
/*TODO*///		RGB[mix1] -> RGB components of final mixed bitmap
/*TODO*///		A[overlay] -> alpha value of combined overlay
/*TODO*///		RGB[overlay] -> RGB components of combined overlay
/*TODO*///		Y[game] -> brightness of game pixel
/*TODO*///
/*TODO*///	6. The result of the overlay + game blending is then added to
/*TODO*///	the backdrop, as follows:
/*TODO*///
/*TODO*///		RGB[mix2] = RGB[mix1] + RGB[backdrop]
/*TODO*///
/*TODO*///	where
/*TODO*///
/*TODO*///		RGB[mix2] -> RGB components of final mixed bitmap
/*TODO*///		RGB[mix1] -> RGB components of game + overlay mixing
/*TODO*///		RGB[backdrop] -> RGB components of combined backdrop graphics
/*TODO*///
/*TODO*///	7. The combined bezel bitmap is alpha blended against the
/*TODO*///	result of the previous operation, as follows:
/*TODO*///
/*TODO*///		RGB[final] = (RGB[mix2] * (1 - A[bezel])) + (RGB[bezel] * A[bezel])
/*TODO*///
/*TODO*///	where
/*TODO*///
/*TODO*///		RGB[final] -> RGB components of final bitmap
/*TODO*///		A[bezel] -> alpha value of combined bezel
/*TODO*///		RGB[bezel] -> RGB components of combined bezel
/*TODO*///		RGB[mix2] -> RGB components of game + overlay + backdrop mixing
/*TODO*///
/*TODO*///**********************************************************************
/*TODO*///
/*TODO*///	POSITIONING
/*TODO*///
/*TODO*///	The positioning of the artwork is a little tricky.
/*TODO*///	Conceptually, the game bitmap occupies the space from (0,0)
/*TODO*///	to (1,1). If you have a piece of artwork that exactly covers
/*TODO*///	the game area, then it too should stretch from (0,0) to (1,1).
/*TODO*///	However, most of the time, this is not the case.
/*TODO*///
/*TODO*///	For example, if you have, say, the Spy Hunter bezel at the
/*TODO*///	bottom of the screen, then you will want to specify the top
/*TODO*///	of the artwork at 1.0 and the bottom at something larger, maybe
/*TODO*///	1.25. The nice thing about the new artwork system is that it
/*TODO*///	will automatically stretch the bitmaps out to accomodate areas
/*TODO*///	beyond the game bitmap, and will still keep the proper aspect
/*TODO*///	ratio.
/*TODO*///
/*TODO*///	Another common example is a backdrop that extends beyond all
/*TODO*///	four corners of the game bitmap. Here is how you would handle
/*TODO*///	that, in detail:
/*TODO*///
/*TODO*///	Let's say you have some artwork like this:
/*TODO*///
/*TODO*///	 <============ 883 pixels ===============>
/*TODO*///
/*TODO*///	(1)-------------------------------------(2)  ^
/*TODO*///	 |                  ^                    |   |
/*TODO*///	 |              26 pixels                |   |
/*TODO*///	 |                  v                    |   |
/*TODO*///	 |     (5)-----------------------(6)     |   |
/*TODO*///	 |      |                         |      |   |
/*TODO*///	 |      |                         |      |   |
/*TODO*///	 |      |                         |      |   |
/*TODO*///	 |<---->|                         |      |   |
/*TODO*///	 |  97  |      Game screen        |      |  768
/*TODO*///	 |pixels|       700 x 500         |      | pixels
/*TODO*///	 |      |                         |<---->|   |
/*TODO*///	 |      |                         |  86  |   |
/*TODO*///	 |      |                         |pixels|   |
/*TODO*///	 |      |                         |      |   |
/*TODO*///	 |      |                         |      |   |
/*TODO*///	 |     (7)-----------------------(8)     |   |
/*TODO*///	 |                  ^                    |   |
/*TODO*///	 |              42 pixels                |   |
/*TODO*///	 |                  v                    |   |
/*TODO*///	(3)-------------------------------------(4)  v
/*TODO*///
/*TODO*///	If you're looking at the raw coordinates as might seem
/*TODO*///	logical, you would imagine that they come out like this:
/*TODO*///
/*TODO*///		(1) is at (0,0)
/*TODO*///		(2) is at (883,0)
/*TODO*///		(3) is at (0,768)
/*TODO*///		(4) is at (883,768)
/*TODO*///
/*TODO*///		(5) is at (97,26)
/*TODO*///		(6) is at (797,26)
/*TODO*///		(7) is at (97,526)
/*TODO*///		(8) is at (797,526)
/*TODO*///
/*TODO*///	The first thing you need to do is adjust the coordinates
/*TODO*///	so that the upper left corner of the game screen (point 5)
/*TODO*///	is at (0,0). To do that, you need to subtract 97 from
/*TODO*///	each X coordinate and 26 from each Y coordinate:
/*TODO*///
/*TODO*///		(1) is at (0-97,0-26)     -> (-97,-26)
/*TODO*///		(2) is at (883-97,0-26)   -> (786,-26)
/*TODO*///		(3) is at (0-97,768-26)   -> (-97,742)
/*TODO*///		(4) is at (883-97,768-26) -> (883,742)
/*TODO*///
/*TODO*///		(5) is at (97-97,26-26)   -> (0,0)
/*TODO*///		(6) is at (797-97,26-26)  -> (700,0)
/*TODO*///		(7) is at (97-97,526-26)  -> (0,500)
/*TODO*///		(8) is at (797-97,526-26) -> (700,500)
/*TODO*///
/*TODO*///	The final thing you need to do is make it so the bottom
/*TODO*///	right corner of the image (point 8) is at (1.0,1.0). To do
/*TODO*///	that, you need to divide each coordinate by the width
/*TODO*///	or height of the image
/*TODO*///
/*TODO*///		(1) is at (-97/700,-26/500)  -> (-0.13857,-0.052)
/*TODO*///		(2) is at (786/700,-26/500)  -> (1.122857,-0.052)
/*TODO*///		(3) is at (-97/700,742/500)  -> (-0.13857, 1.484)
/*TODO*///		(4) is at (883/700,742/500)  -> (1.122857, 1.484)
/*TODO*///
/*TODO*///		(5) is at (0/700,0/500)      -> (0.0,0.0)
/*TODO*///		(6) is at (700/700,0/500)    -> (1.0,0.0)
/*TODO*///		(7) is at (0/700,500/500)    -> (0.0,1.0)
/*TODO*///		(8) is at (700/700,500/500)  -> (1.0,1.0)
/*TODO*///
/*TODO*///	Alternately, you can also provide pixel coordinates, but it will
/*TODO*///	still be relative to the game's native resolution. So, if
/*TODO*///	the game normally runs at 256x224, you'll need to compute
/*TODO*///	the division factor so that the bottom right corner of the
/*TODO*///	game (point 8) ends up at (256,224) instead of (1.0,1.0).
/*TODO*///
/*TODO*///	Basically, if you have the original coordinates shown
/*TODO*///	right below the image, you can compute the values needed by
/*TODO*///	doing this for X coordinates:
/*TODO*///
/*TODO*///		(X coordinate on artwork) - (X coordinate of game's upper-left)
/*TODO*///		---------------------------------------------------------------
/*TODO*///		           (width of game in artwork pixels)
/*TODO*///
/*TODO*///	And this for Y coordinates:
/*TODO*///
/*TODO*///		(Y coordinate on artwork) - (Y coordinate of game's upper-left)
/*TODO*///		---------------------------------------------------------------
/*TODO*///			       (height of game in artwork pixels)
/*TODO*///
/*TODO*///*********************************************************************/
/*TODO*///
/*TODO*///
/*TODO*///#include "driver.h"
/*TODO*///#include "png.h"
/*TODO*///#include "artwork.h"
/*TODO*///#include "vidhrdw/vector.h"
/*TODO*///#include <ctype.h>
/*TODO*///#include <math.h>
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Constants & macros
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*////* maxima */
/*TODO*///#define MAX_PIECES				1024
/*TODO*///#define MAX_HINTS_PER_SCANLINE	4
/*TODO*///
/*TODO*////* fixed-point fraction helpers */
/*TODO*///#define FRAC_BITS				24
/*TODO*///#define FRAC_ONE				(1 << FRAC_BITS)
/*TODO*///#define FRAC_MASK				(FRAC_ONE - 1)
/*TODO*///#define FRAC_HALF				(FRAC_ONE / 2)
/*TODO*///
/*TODO*////* layer types */
/*TODO*///enum
/*TODO*///{
/*TODO*///	LAYER_UNKNOWN,
/*TODO*///	LAYER_BACKDROP,
/*TODO*///	LAYER_OVERLAY,
/*TODO*///	LAYER_BEZEL,
/*TODO*///	LAYER_MARQUEE,
/*TODO*///	LAYER_PANEL,
/*TODO*///	LAYER_SIDE,
/*TODO*///	LAYER_FLYER
/*TODO*///};
/*TODO*///
/*TODO*////* UI transparency hack */
/*TODO*///#define UI_TRANSPARENT_COLOR16	0xfffe
/*TODO*///#define UI_TRANSPARENT_COLOR32	0xfffffffe
/*TODO*///
/*TODO*////* assemble ARGB components in the platform's preferred format */
/*TODO*///#define ASSEMBLE_ARGB(a,r,g,b)	(((a) << ashift) | ((r) << rshift) | ((g) << gshift) | ((b) << bshift))
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Type definitions
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///struct artwork_piece
/*TODO*///{
/*TODO*///	/* linkage */
/*TODO*///	struct artwork_piece *	next;
/*TODO*///
/*TODO*///	/* raw data from the .art file */
/*TODO*///	UINT8					layer;
/*TODO*///	UINT8					has_alpha;
/*TODO*///	int						priority;
/*TODO*///	float					alpha;
/*TODO*///	float					brightness;
/*TODO*///	float					top;
/*TODO*///	float					left;
/*TODO*///	float					bottom;
/*TODO*///	float					right;
/*TODO*///	char *					tag;
/*TODO*///	char *					filename;
/*TODO*///	char *					alpha_filename;
/*TODO*///
/*TODO*///	/* bitmaps */
/*TODO*///	struct mame_bitmap *	rawbitmap;
/*TODO*///	struct mame_bitmap *	prebitmap;
/*TODO*///	struct mame_bitmap *	yrgbbitmap;
/*TODO*///	UINT32 *				scanlinehint;
/*TODO*///	UINT8					blendflags;
/*TODO*///
/*TODO*///	/* derived/dynamic data */
/*TODO*///	int						intersects_game;
/*TODO*///	int						visible;
/*TODO*///	struct rectangle		bounds;
/*TODO*///};
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Local variables
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///static UINT8 rshift, gshift, bshift, ashift;
/*TODO*///static UINT32 nonalpha_mask;
/*TODO*///static UINT32 transparent_color;
/*TODO*///
/*TODO*///static struct artwork_piece *artwork_list;
/*TODO*///static int num_underlays, num_overlays, num_bezels;
/*TODO*///static int num_pieces;
/*TODO*///
/*TODO*///static struct mame_bitmap *underlay, *overlay, *overlay_yrgb, *bezel, *final;
/*TODO*///static struct rectangle underlay_invalid, overlay_invalid, bezel_invalid;
/*TODO*///static struct rectangle gamerect, screenrect;
/*TODO*///static int gamescale;
/*TODO*///
/*TODO*///static struct mame_bitmap *uioverlay;
/*TODO*///static UINT32 *uioverlayhint;
/*TODO*///static struct rectangle uibounds, last_uibounds;
/*TODO*///
/*TODO*///static UINT32 *palette_lookup;
/*TODO*///
/*TODO*///static int original_attributes;
/*TODO*///static UINT8 global_artwork_enable;
/*TODO*///
/*TODO*///static const struct overlay_piece *overlay_list;
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Prototypes
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///static int artwork_prep(void);
/*TODO*///static int artwork_load(const struct GameDriver *gamename, int width, int height, const struct artwork_callbacks *callbacks);
/*TODO*///static int compute_rgb_components(int depth, UINT32 rgb_components[3], UINT32 rgb32_components[3]);
/*TODO*///static int load_bitmap(const char *gamename, struct artwork_piece *piece);
/*TODO*///static int load_alpha_bitmap(const char *gamename, struct artwork_piece *piece, const struct png_info *original);
/*TODO*///static int scale_bitmap(struct artwork_piece *piece, int newwidth, int newheight);
/*TODO*///static void trim_bitmap(struct artwork_piece *piece);
/*TODO*///static int parse_art_file(mame_file *file);
/*TODO*///static int validate_pieces(void);
/*TODO*///static void sort_pieces(void);
/*TODO*///static void update_palette_lookup(struct mame_display *display);
/*TODO*///static int update_layers(void);
/*TODO*///static void render_game_bitmap(struct mame_bitmap *bitmap, const rgb_t *palette, struct mame_display *display);
/*TODO*///static void render_game_bitmap_underlay(struct mame_bitmap *bitmap, const rgb_t *palette, struct mame_display *display);
/*TODO*///static void render_game_bitmap_overlay(struct mame_bitmap *bitmap, const rgb_t *palette, struct mame_display *display);
/*TODO*///static void render_game_bitmap_underlay_overlay(struct mame_bitmap *bitmap, const rgb_t *palette, struct mame_display *display);
/*TODO*///static void render_ui_overlay(struct mame_bitmap *bitmap, UINT32 *dirty, const rgb_t *palette, struct mame_display *display);
/*TODO*///static void erase_rect(struct mame_bitmap *bitmap, const struct rectangle *bounds, UINT32 color);
/*TODO*///static void alpha_blend_intersecting_rect(struct mame_bitmap *dstbitmap, const struct rectangle *dstbounds, struct mame_bitmap *srcbitmap, const struct rectangle *srcbounds, const UINT32 *hintlist);
/*TODO*///static void add_intersecting_rect(struct mame_bitmap *dstbitmap, const struct rectangle *dstbounds, struct mame_bitmap *srcbitmap, const struct rectangle *srcbounds);
/*TODO*///static void cmy_blend_intersecting_rect(struct mame_bitmap *dstbitmap, struct mame_bitmap *dstyrgbbitmap, const struct rectangle *dstbounds, struct mame_bitmap *srcbitmap, struct mame_bitmap *srcyrgbbitmap, const struct rectangle *srcbounds, UINT8 blendflags);
/*TODO*///static int generate_overlay(const struct overlay_piece *list, int width, int height);
/*TODO*///static void add_range_to_hint(UINT32 *hintbase, int scanline, int startx, int endx);
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///#if 0
/*TODO*///#pragma mark INLINES
/*TODO*///#endif
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	union_rect - compute the union of two rects
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///INLINE void union_rect(struct rectangle *dst, const struct rectangle *src)
/*TODO*///{
/*TODO*///	if (dst->max_x == 0)
/*TODO*///		*dst = *src;
/*TODO*///	else if (src->max_x != 0)
/*TODO*///	{
/*TODO*///		dst->min_x = (src->min_x < dst->min_x) ? src->min_x : dst->min_x;
/*TODO*///		dst->max_x = (src->max_x > dst->max_x) ? src->max_x : dst->max_x;
/*TODO*///		dst->min_y = (src->min_y < dst->min_y) ? src->min_y : dst->min_y;
/*TODO*///		dst->max_y = (src->max_y > dst->max_y) ? src->max_y : dst->max_y;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	compute_brightness - compute the effective
/*TODO*///	brightness for an RGB pixel
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///INLINE UINT8 compute_brightness(rgb_t rgb)
/*TODO*///{
/*TODO*///	return (RGB_RED(rgb) * 222 + RGB_GREEN(rgb) * 707 + RGB_BLUE(rgb) * 71) / 1000;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	compute_pre_pixel - compute a premultiplied
/*TODO*///	pixel
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///INLINE UINT32 compute_pre_pixel(UINT8 a, UINT8 r, UINT8 g, UINT8 b)
/*TODO*///{
/*TODO*///	/* premultiply the RGB components with the pixel's alpha */
/*TODO*///	r = (r * a) / 0xff;
/*TODO*///	g = (g * a) / 0xff;
/*TODO*///	b = (b * a) / 0xff;
/*TODO*///
/*TODO*///	/* compute the inverted alpha */
/*TODO*///	a = 0xff - a;
/*TODO*///	return ASSEMBLE_ARGB(a,r,g,b);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	compute_yrgb_pixel - compute a YRGB pixel
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///INLINE UINT32 compute_yrgb_pixel(UINT8 a, UINT8 r, UINT8 g, UINT8 b)
/*TODO*///{
/*TODO*///	/* compute the premultiplied brightness */
/*TODO*///	int bright = (r * 222 + g * 707 + b * 71) / 1000;
/*TODO*///	bright = (bright * a) >> 8;
/*TODO*///
/*TODO*///	/* now assemble */
/*TODO*///	return MAKE_ARGB(bright,r,g,b);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	add_and_clamp - add two pixels and clamp
/*TODO*///	each component to the max
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///INLINE UINT32 add_and_clamp(UINT32 game, UINT32 underpix)
/*TODO*///{
/*TODO*///	UINT32 temp1 = game + underpix;
/*TODO*///	UINT32 temp2 = game ^ underpix ^ temp1;
/*TODO*///
/*TODO*///	/* handle overflow (carry out of top component */
/*TODO*///	if (temp1 < game)
/*TODO*///		temp1 |= 0xff000000;
/*TODO*///
/*TODO*///	/* handle carry out of next component */
/*TODO*///	if (temp2 & 0x01000000)
/*TODO*///	{
/*TODO*///		temp1 -= 0x01000000;
/*TODO*///		temp1 |= 0x00ff0000;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* handle carry out of next component */
/*TODO*///	if (temp2 & 0x00010000)
/*TODO*///	{
/*TODO*///		temp1 -= 0x00010000;
/*TODO*///		temp1 |= 0x0000ff00;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* handle carry out of final component */
/*TODO*///	if (temp2 & 0x00000100)
/*TODO*///	{
/*TODO*///		temp1 -= 0x00000100;
/*TODO*///		temp1 |= 0x000000ff;
/*TODO*///	}
/*TODO*///	return temp1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	blend_over - blend two pixels with overlay
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///INLINE UINT32 blend_over(UINT32 game, UINT32 pre, UINT32 yrgb)
/*TODO*///{
/*TODO*///	/* case 1: no game pixels; just return the premultiplied pixel */
/*TODO*///	if ((game & nonalpha_mask) == 0)
/*TODO*///		return pre;
/*TODO*///
/*TODO*///	/* case 2: apply the effect */
/*TODO*///	else
/*TODO*///	{
/*TODO*///		UINT8 bright = RGB_GREEN(game);
/*TODO*///		UINT8 r, g, b;
/*TODO*///
/*TODO*///		yrgb -= pre;
/*TODO*///		r = (RGB_RED(yrgb) * bright) / 256;
/*TODO*///		g = (RGB_GREEN(yrgb) * bright) / 256;
/*TODO*///		b = (RGB_BLUE(yrgb) * bright) / 256;
/*TODO*///		return pre + ASSEMBLE_ARGB(0,r,g,b);
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///#if 0
/*TODO*///#pragma mark -
/*TODO*///#pragma mark OSD FRONTENDS
/*TODO*///#endif
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	artwork_create_display - tweak the display
/*TODO*///	parameters based on artwork, and call through
/*TODO*///	to osd_create_display
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///int artwork_create_display(struct osd_create_params *params, UINT32 *rgb_components, const struct artwork_callbacks *callbacks)
/*TODO*///{
/*TODO*///	int original_width = params->width;
/*TODO*///	int original_height = params->height;
/*TODO*///	int original_depth = params->depth;
/*TODO*///	double min_x, min_y, max_x, max_y;
/*TODO*///	UINT32 rgb32_components[3];
/*TODO*///	struct artwork_piece *piece;
/*TODO*///
/*TODO*///	/* reset UI */
/*TODO*///	uioverlay = NULL;
/*TODO*///	uioverlayhint = NULL;
/*TODO*///
/*TODO*///	/* first load the artwork; if none, quit now */
/*TODO*///	artwork_list = NULL;
/*TODO*///	if (!artwork_load(Machine->gamedrv, original_width, original_height, callbacks))
/*TODO*///		return 1;
/*TODO*///	if (!artwork_list && (!callbacks->activate_artwork || !callbacks->activate_artwork(params)))
/*TODO*///		return osd_create_display(params, rgb_components);
/*TODO*///
/*TODO*///	/* determine the game bitmap scale factor */
/*TODO*///	gamescale = options.artwork_res;
/*TODO*///	if (gamescale < 1 || (params->video_attributes & VIDEO_TYPE_VECTOR))
/*TODO*///		gamescale = 1;
/*TODO*///	else if (gamescale > 2)
/*TODO*///		gamescale = 2;
/*TODO*///
/*TODO*///	/* compute the extent of all the artwork */
/*TODO*///	min_x = min_y = 0.0;
/*TODO*///	max_x = max_y = 1.0;
/*TODO*///	if (!options.artwork_crop)
/*TODO*///		for (piece = artwork_list; piece; piece = piece->next)
/*TODO*///		{
/*TODO*///			/* compute the outermost bounds */
/*TODO*///			if (piece->left < min_x) min_x = piece->left;
/*TODO*///			if (piece->right > max_x) max_x = piece->right;
/*TODO*///			if (piece->top < min_y) min_y = piece->top;
/*TODO*///			if (piece->bottom > max_y) max_y = piece->bottom;
/*TODO*///		}
/*TODO*///
/*TODO*///	/* now compute the altered width/height and the new aspect ratio */
/*TODO*///	params->width = (int)((max_x - min_x) * (double)(original_width * gamescale) + 0.5);
/*TODO*///	params->height = (int)((max_y - min_y) * (double)(original_height * gamescale) + 0.5);
/*TODO*///	params->aspect_x = (int)((double)params->aspect_x * 100. * (max_x - min_x));
/*TODO*///	params->aspect_y = (int)((double)params->aspect_y * 100. * (max_y - min_y));
/*TODO*///
/*TODO*///	/* vector games need to fit inside the original bounds, so scale back down */
/*TODO*///	if (params->video_attributes & VIDEO_TYPE_VECTOR)
/*TODO*///	{
/*TODO*///		/* shrink the width/height if over */
/*TODO*///		if (params->width > original_width)
/*TODO*///		{
/*TODO*///			params->width = original_width;
/*TODO*///			params->height = original_width * params->aspect_y / params->aspect_x;
/*TODO*///		}
/*TODO*///		if (params->height > original_height)
/*TODO*///		{
/*TODO*///			params->height = original_height;
/*TODO*///			params->width = original_height * params->aspect_x / params->aspect_y;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* compute the new raw width/height and update the vector info */
/*TODO*///		original_width = (int)((double)params->width / (max_x - min_x));
/*TODO*///		original_height = (int)((double)params->height / (max_y - min_y));
/*TODO*///		options.vector_width = original_width;
/*TODO*///		options.vector_height = original_height;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* adjust the parameters */
/*TODO*///	original_attributes = params->video_attributes;
/*TODO*///	params->video_attributes |= VIDEO_RGB_DIRECT | VIDEO_NEEDS_6BITS_PER_GUN;
/*TODO*///	params->depth = 32;
/*TODO*///
/*TODO*///	/* allocate memory for the bitmaps */
/*TODO*///	underlay = auto_bitmap_alloc_depth(params->width, params->height, 32);
/*TODO*///	overlay = auto_bitmap_alloc_depth(params->width, params->height, 32);
/*TODO*///	overlay_yrgb = auto_bitmap_alloc_depth(params->width, params->height, 32);
/*TODO*///	bezel = auto_bitmap_alloc_depth(params->width, params->height, 32);
/*TODO*///	final = auto_bitmap_alloc_depth(params->width, params->height, 32);
/*TODO*///	if (!final || !overlay || !overlay_yrgb || !underlay || !bezel)
/*TODO*///		return 1;
/*TODO*///
/*TODO*///	/* allocate the UI overlay */
/*TODO*///	uioverlay = auto_bitmap_alloc_depth(params->width, params->height, Machine->color_depth);
/*TODO*///	if (uioverlay)
/*TODO*///		uioverlayhint = auto_malloc(uioverlay->height * MAX_HINTS_PER_SCANLINE * sizeof(uioverlayhint[0]));
/*TODO*///	if (!uioverlay || !uioverlayhint)
/*TODO*///		return 1;
/*TODO*///	fillbitmap(uioverlay, (Machine->color_depth == 32) ? UI_TRANSPARENT_COLOR32 : UI_TRANSPARENT_COLOR16, NULL);
/*TODO*///	memset(uioverlayhint, 0, uioverlay->height * MAX_HINTS_PER_SCANLINE * sizeof(uioverlayhint[0]));
/*TODO*///
/*TODO*///	/* compute the screen rect */
/*TODO*///	screenrect.min_x = screenrect.min_y = 0;
/*TODO*///	screenrect.max_x = params->width - 1;
/*TODO*///	screenrect.max_y = params->height - 1;
/*TODO*///
/*TODO*///	/* compute the game rect */
/*TODO*///	gamerect.min_x = (int)(-min_x * (double)(original_width * gamescale) + 0.5);
/*TODO*///	gamerect.min_y = (int)(-min_y * (double)(original_height * gamescale) + 0.5);
/*TODO*///	gamerect.max_x = gamerect.min_x + original_width * gamescale - 1;
/*TODO*///	gamerect.max_y = gamerect.min_y + original_height * gamescale - 1;
/*TODO*///
/*TODO*///	/* now try to create the display */
/*TODO*///	if (osd_create_display(params, rgb32_components))
/*TODO*///		return 1;
/*TODO*///
/*TODO*///	/* fill in our own RGB components */
/*TODO*///	if (compute_rgb_components(original_depth, rgb_components, rgb32_components))
/*TODO*///	{
/*TODO*///		osd_close_display();
/*TODO*///		return 1;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* now compute all the artwork pieces' coordinates */
/*TODO*///	for (piece = artwork_list; piece; piece = piece->next)
/*TODO*///	{
/*TODO*///		piece->bounds.min_x = (int)((piece->left - min_x) * (double)(original_width * gamescale) + 0.5);
/*TODO*///		piece->bounds.min_y = (int)((piece->top - min_y) * (double)(original_height * gamescale) + 0.5);
/*TODO*///		piece->bounds.max_x = (int)((piece->right - min_x) * (double)(original_width * gamescale) + 0.5) - 1;
/*TODO*///		piece->bounds.max_y = (int)((piece->bottom - min_y) * (double)(original_height * gamescale) + 0.5) - 1;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* now do the final prep on the artwork */
/*TODO*///	return artwork_prep();
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	artwork_update_video_and_audio - update the
/*TODO*///	screen, adjusting for artwork
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void artwork_update_video_and_audio(struct mame_display *display)
/*TODO*///{
/*TODO*///	static struct rectangle ui_changed_bounds;
/*TODO*///	static int ui_changed;
/*TODO*///	int artwork_changed = 0, ui_visible = 0;
/*TODO*///
/*TODO*///	/* do nothing if no artwork */
/*TODO*///	if (!artwork_list && !uioverlay)
/*TODO*///	{
/*TODO*///		osd_update_video_and_audio(display);
/*TODO*///		return;
/*TODO*///	}
/*TODO*///
/*TODO*///	profiler_mark(PROFILER_ARTWORK);
/*TODO*///
/*TODO*///	/* update the palette */
/*TODO*///	if (display->changed_flags & GAME_PALETTE_CHANGED)
/*TODO*///		update_palette_lookup(display);
/*TODO*///
/*TODO*///	/* process the artwork and UI only if we're not frameskipping */
/*TODO*///	if (display->changed_flags & GAME_BITMAP_CHANGED)
/*TODO*///	{
/*TODO*///		/* see if there's any UI to display this frame */
/*TODO*///		ui_visible = (uibounds.max_x != 0);
/*TODO*///
/*TODO*///		/* if the UI bounds changed, refresh everything */
/*TODO*///		if (last_uibounds.min_x != uibounds.min_x || last_uibounds.min_y != uibounds.min_y ||
/*TODO*///			last_uibounds.max_x != uibounds.max_x || last_uibounds.max_y != uibounds.max_y)
/*TODO*///		{
/*TODO*///			/* compute the union of the two rects */
/*TODO*///			ui_changed_bounds = last_uibounds;
/*TODO*///			union_rect(&ui_changed_bounds, &uibounds);
/*TODO*///			last_uibounds = uibounds;
/*TODO*///
/*TODO*///			/* track changes for a few frames */
/*TODO*///			ui_changed = 3;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* if we have changed pending, mark the artwork dirty */
/*TODO*///		if (ui_changed)
/*TODO*///		{
/*TODO*///			union_rect(&underlay_invalid, &ui_changed_bounds);
/*TODO*///			union_rect(&overlay_invalid, &ui_changed_bounds);
/*TODO*///			union_rect(&bezel_invalid, &ui_changed_bounds);
/*TODO*///			ui_changed--;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* artwork disabled case */
/*TODO*///		if (!global_artwork_enable)
/*TODO*///		{
/*TODO*///			fillbitmap(final, MAKE_ARGB(0,0,0,0), NULL);
/*TODO*///			union_rect(&underlay_invalid, &screenrect);
/*TODO*///			union_rect(&overlay_invalid, &screenrect);
/*TODO*///			union_rect(&bezel_invalid, &screenrect);
/*TODO*///			render_game_bitmap(display->game_bitmap, palette_lookup, display);
/*TODO*///		}
/*TODO*///
/*TODO*///		/* artwork enabled */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			/* update the underlay and overlay */
/*TODO*///			artwork_changed = update_layers();
/*TODO*///
/*TODO*///			/* render to the final bitmap */
/*TODO*///			if (num_underlays && num_overlays)
/*TODO*///				render_game_bitmap_underlay_overlay(display->game_bitmap, palette_lookup, display);
/*TODO*///			else if (num_underlays)
/*TODO*///				render_game_bitmap_underlay(display->game_bitmap, palette_lookup, display);
/*TODO*///			else if (num_overlays)
/*TODO*///				render_game_bitmap_overlay(display->game_bitmap, palette_lookup, display);
/*TODO*///			else
/*TODO*///				render_game_bitmap(display->game_bitmap, palette_lookup, display);
/*TODO*///
/*TODO*///			/* apply the bezel */
/*TODO*///			if (num_bezels)
/*TODO*///			{
/*TODO*///				struct artwork_piece *piece;
/*TODO*///				for (piece = artwork_list; piece; piece = piece->next)
/*TODO*///					if (piece->layer >= LAYER_BEZEL && piece->intersects_game)
/*TODO*///						alpha_blend_intersecting_rect(final, &gamerect, piece->prebitmap, &piece->bounds, piece->scanlinehint);
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* add UI */
/*TODO*///		if (ui_visible)
/*TODO*///			render_ui_overlay(uioverlay, uioverlayhint, palette_lookup, display);
/*TODO*///
/*TODO*///		/* if artwork changed, or there's UI, we can't use dirty pixels */
/*TODO*///		if (artwork_changed || ui_changed || ui_visible)
/*TODO*///		{
/*TODO*///			display->changed_flags &= ~VECTOR_PIXELS_CHANGED;
/*TODO*///			display->vector_dirty_pixels = NULL;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	profiler_mark(PROFILER_END);
/*TODO*///
/*TODO*///	/* blit the union of the game/screen rect and the UI bounds */
/*TODO*///	display->game_bitmap_update = (artwork_changed || ui_changed) ? screenrect : gamerect;
/*TODO*///	union_rect(&display->game_bitmap_update, &uibounds);
/*TODO*///
/*TODO*///	/* force the visible area constant */
/*TODO*///	display->game_visible_area = screenrect;
/*TODO*///	display->game_bitmap = final;
/*TODO*///	osd_update_video_and_audio(display);
/*TODO*///
/*TODO*///	/* reset the UI bounds (but only if we rendered the UI) */
/*TODO*///	if (display->changed_flags & GAME_BITMAP_CHANGED)
/*TODO*///		uibounds.max_x = 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	artwork_override_screenshot_params - override
/*TODO*///	certain parameters when saving a screenshot
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void artwork_override_screenshot_params(struct mame_bitmap **bitmap, struct rectangle *rect, UINT32 *rgb_components)
/*TODO*///{
/*TODO*///	if ((*bitmap == Machine->scrbitmap || *bitmap == uioverlay) && artwork_list)
/*TODO*///	{
/*TODO*///		*rect = screenrect;
/*TODO*///
/*TODO*///		/* snapshots require correct direct_rgb_components */
/*TODO*///		rgb_components[0] = 0xff << rshift;
/*TODO*///		rgb_components[1] = 0xff << gshift;
/*TODO*///		rgb_components[2] = 0xff << bshift;
/*TODO*///		*bitmap = final;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	artwork_get_ui_bitmap - get the UI bitmap
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///struct mame_bitmap *artwork_get_ui_bitmap(void)
/*TODO*///{
/*TODO*///	return uioverlay ? uioverlay : Machine->scrbitmap;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	artwork_mark_ui_dirty - mark a portion of the
/*TODO*///	UI bitmap dirty
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void artwork_mark_ui_dirty(int minx, int miny, int maxx, int maxy)
/*TODO*///{
/*TODO*///	/* add to the UI overlay hint if it exists */
/*TODO*///	if (uioverlayhint)
/*TODO*///	{
/*TODO*///		struct rectangle rect;
/*TODO*///		int y;
/*TODO*///
/*TODO*///		/* clip to visible */
/*TODO*///		if (minx < 0)
/*TODO*///			minx = 0;
/*TODO*///		if (maxx >= uioverlay->width)
/*TODO*///			maxx = uioverlay->width - 1;
/*TODO*///		if (miny < 0)
/*TODO*///			miny = 0;
/*TODO*///		if (maxy >= uioverlay->height)
/*TODO*///			maxy = uioverlay->height - 1;
/*TODO*///
/*TODO*///		/* update the global rect */
/*TODO*///		rect.min_x = minx;
/*TODO*///		rect.max_x = maxx;
/*TODO*///		rect.min_y = miny;
/*TODO*///		rect.max_y = maxy;
/*TODO*///		union_rect(&uibounds, &rect);
/*TODO*///
/*TODO*///		/* add hints for each scanline */
/*TODO*///		if (minx <= maxx)
/*TODO*///			for (y = miny; y <= maxy; y++)
/*TODO*///				add_range_to_hint(uioverlayhint, y, minx, maxx);
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	artwork_get_screensize - get the real screen
/*TODO*///	size
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void artwork_get_screensize(int *width, int *height)
/*TODO*///{
/*TODO*///	if (artwork_list)
/*TODO*///	{
/*TODO*///		*width = screenrect.max_x - screenrect.min_x + 1;
/*TODO*///		*height = screenrect.max_y - screenrect.min_y + 1;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		*width = Machine->drv->screen_width;
/*TODO*///		*height = Machine->drv->screen_height;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	artwork_enable - globally enable/disable
/*TODO*///	artwork
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void artwork_enable(int enable)
/*TODO*///{
/*TODO*///	global_artwork_enable = enable;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	artwork_set_overlay - set the hard-coded
/*TODO*///	overlay for this game
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void artwork_set_overlay(const struct overlay_piece *overlist)
/*TODO*///{
/*TODO*///	overlay_list = overlist;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	artwork_show - show/hide a tagged piece of art
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///void artwork_show(const char *tag, int show)
/*TODO*///{
/*TODO*///	struct artwork_piece *piece;
/*TODO*///
/*TODO*///	/* find all the pieces that match the tag */
/*TODO*///	for (piece = artwork_list; piece; piece = piece->next)
/*TODO*///		if (piece->tag && !strcmp(piece->tag, tag))
/*TODO*///		{
/*TODO*///			/* if the state is changing, invalidate that area */
/*TODO*///			if (piece->visible != show)
/*TODO*///			{
/*TODO*///				piece->visible = show;
/*TODO*///
/*TODO*///				/* backdrop */
/*TODO*///				if (piece->layer == LAYER_BACKDROP)
/*TODO*///					union_rect(&underlay_invalid, &piece->bounds);
/*TODO*///
/*TODO*///				/* overlay */
/*TODO*///				else if (piece->layer == LAYER_OVERLAY)
/*TODO*///					union_rect(&overlay_invalid, &piece->bounds);
/*TODO*///
/*TODO*///				/* bezel */
/*TODO*///				else if (piece->layer >= LAYER_BEZEL)
/*TODO*///					union_rect(&bezel_invalid, &piece->bounds);
/*TODO*///			}
/*TODO*///		}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///#if 0
/*TODO*///#pragma mark -
/*TODO*///#pragma mark LAYER PROCESSING
/*TODO*///#endif
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	update_layers - update any dirty areas of
/*TODO*///	the layers
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int update_layers(void)
/*TODO*///{
/*TODO*///	struct artwork_piece *piece = artwork_list;
/*TODO*///	struct rectangle combined;
/*TODO*///	int changed = 0;
/*TODO*///
/*TODO*///	/* update the underlays */
/*TODO*///	if (underlay_invalid.max_x != 0)
/*TODO*///	{
/*TODO*///		sect_rect(&underlay_invalid, &screenrect);
/*TODO*///		erase_rect(underlay, &underlay_invalid, 0);
/*TODO*///		for (piece = artwork_list; piece; piece = piece->next)
/*TODO*///			if (piece->layer == LAYER_BACKDROP && piece->visible && piece->prebitmap)
/*TODO*///				alpha_blend_intersecting_rect(underlay, &underlay_invalid, piece->prebitmap, &piece->bounds, piece->scanlinehint);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* update the overlays */
/*TODO*///	if (overlay_invalid.max_x != 0)
/*TODO*///	{
/*TODO*///		sect_rect(&overlay_invalid, &screenrect);
/*TODO*///		erase_rect(overlay, &overlay_invalid, transparent_color);
/*TODO*///		erase_rect(overlay_yrgb, &overlay_invalid, ASSEMBLE_ARGB(0,0xff,0xff,0xff));
/*TODO*///		for (piece = artwork_list; piece; piece = piece->next)
/*TODO*///			if (piece->layer == LAYER_OVERLAY && piece->visible && piece->prebitmap)
/*TODO*///				cmy_blend_intersecting_rect(overlay, overlay_yrgb, &overlay_invalid, piece->prebitmap, piece->yrgbbitmap, &piece->bounds, piece->blendflags);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* update the bezels */
/*TODO*///	if (bezel_invalid.max_x != 0)
/*TODO*///	{
/*TODO*///		sect_rect(&bezel_invalid, &screenrect);
/*TODO*///		erase_rect(bezel, &bezel_invalid, transparent_color);
/*TODO*///		for (piece = artwork_list; piece; piece = piece->next)
/*TODO*///			if (piece->layer >= LAYER_BEZEL && piece->visible && piece->prebitmap)
/*TODO*///				alpha_blend_intersecting_rect(bezel, &bezel_invalid, piece->prebitmap, &piece->bounds, piece->scanlinehint);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* combine the invalid rects */
/*TODO*///	combined = underlay_invalid;
/*TODO*///	union_rect(&combined, &overlay_invalid);
/*TODO*///	union_rect(&combined, &bezel_invalid);
/*TODO*///	if (combined.max_x != 0)
/*TODO*///	{
/*TODO*///		/* blend into the final bitmap */
/*TODO*///		erase_rect(final, &combined, 0);
/*TODO*///		alpha_blend_intersecting_rect(final, &combined, underlay, &screenrect, NULL);
/*TODO*///		add_intersecting_rect(final, &combined, overlay, &screenrect);
/*TODO*///		alpha_blend_intersecting_rect(final, &combined, bezel, &screenrect, NULL);
/*TODO*///		changed = 1;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* reset the invalid rects */
/*TODO*///	underlay_invalid.max_x = 0;
/*TODO*///	overlay_invalid.max_x = 0;
/*TODO*///	bezel_invalid.max_x = 0;
/*TODO*///	return changed;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	erase_rect - erase the given bounds of a 32bpp
/*TODO*///	bitmap
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void erase_rect(struct mame_bitmap *bitmap, const struct rectangle *bounds, UINT32 color)
/*TODO*///{
/*TODO*///	int x, y;
/*TODO*///
/*TODO*///	/* loop over rows */
/*TODO*///	for (y = bounds->min_y; y <= bounds->max_y; y++)
/*TODO*///	{
/*TODO*///		UINT32 *dest = (UINT32 *)bitmap->base + y * bitmap->rowpixels;
/*TODO*///		for (x = bounds->min_x; x <= bounds->max_x; x++)
/*TODO*///			dest[x] = color;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	alpha_blend_intersecting_rect - alpha blend an
/*TODO*///	artwork piece into a bitmap
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void alpha_blend_intersecting_rect(struct mame_bitmap *dstbitmap, const struct rectangle *dstbounds, struct mame_bitmap *srcbitmap, const struct rectangle *srcbounds, const UINT32 *hintlist)
/*TODO*///{
/*TODO*///	struct rectangle sect = *srcbounds;
/*TODO*///	UINT32 dummy_range[2];
/*TODO*///	int lclip, rclip;
/*TODO*///	int x, y, h;
/*TODO*///
/*TODO*///	/* compute the intersection */
/*TODO*///	sect_rect(&sect, dstbounds);
/*TODO*///
/*TODO*///	/* compute the source-relative left/right clip */
/*TODO*///	lclip = sect.min_x - srcbounds->min_x;
/*TODO*///	rclip = sect.max_x - srcbounds->min_x;
/*TODO*///
/*TODO*///	/* set up a dummy range */
/*TODO*///	dummy_range[0] = srcbitmap->width - 1;
/*TODO*///	dummy_range[1] = 0;
/*TODO*///
/*TODO*///	/* adjust the hintlist for the starting offset */
/*TODO*///	if (hintlist)
/*TODO*///		hintlist -= srcbounds->min_y * MAX_HINTS_PER_SCANLINE;
/*TODO*///
/*TODO*///	/* loop over rows */
/*TODO*///	for (y = sect.min_y; y <= sect.max_y; y++)
/*TODO*///	{
/*TODO*///		UINT32 *src = (UINT32 *)srcbitmap->base + (y - srcbounds->min_y) * srcbitmap->rowpixels;
/*TODO*///		UINT32 *dest = (UINT32 *)dstbitmap->base + y * dstbitmap->rowpixels + srcbounds->min_x;
/*TODO*///		const UINT32 *hint = hintlist ? &hintlist[y * MAX_HINTS_PER_SCANLINE] : &dummy_range[0];
/*TODO*///
/*TODO*///		/* loop over hints */
/*TODO*///		for (h = 0; h < MAX_HINTS_PER_SCANLINE && hint[h] != 0; h++)
/*TODO*///		{
/*TODO*///			int start = hint[h] >> 16;
/*TODO*///			int stop = hint[h] & 0xffff;
/*TODO*///
/*TODO*///			/* clip to the sect rect */
/*TODO*///			if (start < lclip)
/*TODO*///				start = lclip;
/*TODO*///			else if (start > rclip)
/*TODO*///				continue;
/*TODO*///			if (stop > rclip)
/*TODO*///				stop = rclip;
/*TODO*///			else if (stop < lclip)
/*TODO*///				continue;
/*TODO*///
/*TODO*///			/* loop over columns */
/*TODO*///			for (x = start; x <= stop; x++)
/*TODO*///			{
/*TODO*///				/* we don't bother optimizing for transparent here because we hope that the */
/*TODO*///				/* hints have removed most of the need */
/*TODO*///				UINT32 pix = src[x];
/*TODO*///				UINT32 dpix = dest[x];
/*TODO*///				int alpha = (pix >> ashift) & 0xff;
/*TODO*///
/*TODO*///				/* alpha is inverted, so alpha 0 means fully opaque */
/*TODO*///				if (alpha == 0)
/*TODO*///					dest[x] = pix;
/*TODO*///
/*TODO*///				/* otherwise, we do a proper blend */
/*TODO*///				else
/*TODO*///				{
/*TODO*///					int r = ((pix >> rshift) & 0xff) + ((alpha * ((dpix >> rshift) & 0xff)) >> 8);
/*TODO*///					int g = ((pix >> gshift) & 0xff) + ((alpha * ((dpix >> gshift) & 0xff)) >> 8);
/*TODO*///					int b = ((pix >> bshift) & 0xff) + ((alpha * ((dpix >> bshift) & 0xff)) >> 8);
/*TODO*///
/*TODO*///					/* add the alpha values in inverted space (looks weird but is correct) */
/*TODO*///					int a = alpha + ((dpix >> ashift) & 0xff) - 0xff;
/*TODO*///					if (a < 0) a = 0;
/*TODO*///					dest[x] = ASSEMBLE_ARGB(a,r,g,b);
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	add_intersecting_rect - add a
/*TODO*///	artwork piece into a bitmap
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void add_intersecting_rect(struct mame_bitmap *dstbitmap, const struct rectangle *dstbounds, struct mame_bitmap *srcbitmap, const struct rectangle *srcbounds)
/*TODO*///{
/*TODO*///	struct rectangle sect = *srcbounds;
/*TODO*///	int x, y, width;
/*TODO*///
/*TODO*///	/* compute the intersection and resulting width */
/*TODO*///	sect_rect(&sect, dstbounds);
/*TODO*///	width = sect.max_x - sect.min_x + 1;
/*TODO*///
/*TODO*///	/* loop over rows */
/*TODO*///	for (y = sect.min_y; y <= sect.max_y; y++)
/*TODO*///	{
/*TODO*///		UINT32 *src = (UINT32 *)srcbitmap->base + (y - srcbounds->min_y) * srcbitmap->rowpixels + (sect.min_x - srcbounds->min_x);
/*TODO*///		UINT32 *dest = (UINT32 *)dstbitmap->base + y * dstbitmap->rowpixels + sect.min_x;
/*TODO*///
/*TODO*///		/* loop over columns */
/*TODO*///		for (x = 0; x < width; x++)
/*TODO*///		{
/*TODO*///			UINT32 pix = src[x];
/*TODO*///
/*TODO*///			/* just add and clamp */
/*TODO*///			if (pix != transparent_color)
/*TODO*///				dest[x] = add_and_clamp(pix, dest[x]);
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	cmy_blend_intersecting_rect - CMY blend an
/*TODO*///	artwork piece into a bitmap
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void cmy_blend_intersecting_rect(
/*TODO*///	struct mame_bitmap *dstprebitmap, struct mame_bitmap *dstyrgbbitmap, const struct rectangle *dstbounds,
/*TODO*///	struct mame_bitmap *srcprebitmap, struct mame_bitmap *srcyrgbbitmap, const struct rectangle *srcbounds,
/*TODO*///	UINT8 blendflags)
/*TODO*///{
/*TODO*///	struct rectangle sect = *srcbounds;
/*TODO*///	int x, y, width;
/*TODO*///
/*TODO*///	/* compute the intersection and resulting width */
/*TODO*///	sect_rect(&sect, dstbounds);
/*TODO*///	width = sect.max_x - sect.min_x + 1;
/*TODO*///
/*TODO*///	/* loop over rows */
/*TODO*///	for (y = sect.min_y; y <= sect.max_y; y++)
/*TODO*///	{
/*TODO*///		UINT32 *srcpre = (UINT32 *)srcprebitmap->base + (y - srcbounds->min_y) * srcprebitmap->rowpixels + (sect.min_x - srcbounds->min_x);
/*TODO*///		UINT32 *srcyrgb = (UINT32 *)srcyrgbbitmap->base + (y - srcbounds->min_y) * srcyrgbbitmap->rowpixels + (sect.min_x - srcbounds->min_x);
/*TODO*///		UINT32 *destpre = (UINT32 *)dstprebitmap->base + y * dstprebitmap->rowpixels + sect.min_x;
/*TODO*///		UINT32 *destyrgb = (UINT32 *)dstyrgbbitmap->base + y * dstyrgbbitmap->rowpixels + sect.min_x;
/*TODO*///
/*TODO*///		/* loop over columns */
/*TODO*///		for (x = 0; x < width; x++)
/*TODO*///		{
/*TODO*///			UINT32 spre = srcpre[x];
/*TODO*///			UINT32 dpre = destpre[x];
/*TODO*///			UINT32 syrgb = srcyrgb[x];
/*TODO*///			UINT32 dyrgb = destyrgb[x];
/*TODO*///
/*TODO*///			/* handle "non-blending" mode */
/*TODO*///			if (blendflags & OVERLAY_FLAG_NOBLEND)
/*TODO*///			{
/*TODO*///				if ((spre & nonalpha_mask) && spre >= dpre)
/*TODO*///				{
/*TODO*///					destpre[x] = spre;
/*TODO*///					destyrgb[x] = syrgb;
/*TODO*///				}
/*TODO*///			}
/*TODO*///
/*TODO*///			/* simple copy if nothing at the dest */
/*TODO*///			else if (dpre == transparent_color && dyrgb == 0)
/*TODO*///			{
/*TODO*///				destpre[x] = spre;
/*TODO*///				destyrgb[x] = syrgb;
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				/* subtract CMY and alpha from each pixel */
/*TODO*///				int sc = (~syrgb >> rshift) & 0xff;
/*TODO*///				int sm = (~syrgb >> gshift) & 0xff;
/*TODO*///				int sy = (~syrgb >> bshift) & 0xff;
/*TODO*///				int sa = (~spre >> ashift) & 0xff;
/*TODO*///				int dc = (~dyrgb >> rshift) & 0xff;
/*TODO*///				int dm = (~dyrgb >> gshift) & 0xff;
/*TODO*///				int dy = (~dyrgb >> bshift) & 0xff;
/*TODO*///				int da = (~dpre >> ashift) & 0xff;
/*TODO*///				int dr, dg, db;
/*TODO*///				int max;
/*TODO*///
/*TODO*///				/* add and clamp the alphas */
/*TODO*///				da += sa;
/*TODO*///				if (da > 0xff) da = 0xff;
/*TODO*///
/*TODO*///				/* add the CMY */
/*TODO*///				dc += sc;
/*TODO*///				dm += sm;
/*TODO*///				dy += sy;
/*TODO*///
/*TODO*///				/* compute the maximum intensity */
/*TODO*///				max = (dc > dm) ? dc : dm;
/*TODO*///				max = (dy > max) ? dy : max;
/*TODO*///
/*TODO*///				/* if that's out of range, scale by it */
/*TODO*///				if (max > 0xff)
/*TODO*///				{
/*TODO*///					dc = (dc * 0xff) / max;
/*TODO*///					dm = (dm * 0xff) / max;
/*TODO*///					dy = (dy * 0xff) / max;
/*TODO*///				}
/*TODO*///
/*TODO*///				/* convert back to RGB */
/*TODO*///				dr = dc ^ 0xff;
/*TODO*///				dg = dm ^ 0xff;
/*TODO*///				db = dy ^ 0xff;
/*TODO*///
/*TODO*///				/* recompute the two pixels */
/*TODO*///				destpre[x] = compute_pre_pixel(da,dr,dg,db);
/*TODO*///				destyrgb[x] = compute_yrgb_pixel(da,dr,dg,db);
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///#if 0
/*TODO*///#pragma mark -
/*TODO*///#pragma mark GAME BITMAP PROCESSING
/*TODO*///#endif
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	update_palette - update any dirty palette
/*TODO*///	entries
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void update_palette_lookup(struct mame_display *display)
/*TODO*///{
/*TODO*///	int i, j;
/*TODO*///
/*TODO*///	/* loop over dirty colors in batches of 32 */
/*TODO*///	for (i = 0; i < display->game_palette_entries; i += 32)
/*TODO*///	{
/*TODO*///		UINT32 dirtyflags = display->game_palette_dirty[i / 32];
/*TODO*///		if (dirtyflags)
/*TODO*///		{
/*TODO*///			display->game_palette_dirty[i / 32] = 0;
/*TODO*///
/*TODO*///			/* loop over all 32 bits and update dirty entries */
/*TODO*///			for (j = 0; j < 32; j++, dirtyflags >>= 1)
/*TODO*///				if (dirtyflags & 1)
/*TODO*///				{
/*TODO*///					/* extract the RGB values */
/*TODO*///					rgb_t rgbvalue = display->game_palette[i + j];
/*TODO*///					int r = RGB_RED(rgbvalue);
/*TODO*///					int g = RGB_GREEN(rgbvalue);
/*TODO*///					int b = RGB_BLUE(rgbvalue);
/*TODO*///
/*TODO*///					/* update the lookup table */
/*TODO*///					palette_lookup[i + j] = ASSEMBLE_ARGB(0, r, g, b);
/*TODO*///				}
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	render_game_bitmap - render the game bitmap
/*TODO*///	raw
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///#define PIXEL(x,y,srcdstbase,srcdstrpix,bits)	(*((UINT##bits *)srcdstbase##base + (y) * srcdstrpix##rowpixels + (x)))
/*TODO*///
/*TODO*///static void render_game_bitmap(struct mame_bitmap *bitmap, const rgb_t *palette, struct mame_display *display)
/*TODO*///{
/*TODO*///	int srcrowpixels = bitmap->rowpixels;
/*TODO*///	int dstrowpixels = final->rowpixels;
/*TODO*///	void *srcbase, *dstbase;
/*TODO*///	int width, height;
/*TODO*///	int x, y;
/*TODO*///
/*TODO*///	/* compute common parameters */
/*TODO*///	width = Machine->absolute_visible_area.max_x - Machine->absolute_visible_area.min_x + 1;
/*TODO*///	height = Machine->absolute_visible_area.max_y - Machine->absolute_visible_area.min_y + 1;
/*TODO*///	srcbase = (UINT8 *)bitmap->base + Machine->absolute_visible_area.min_y * bitmap->rowbytes;
/*TODO*///	dstbase = (UINT8 *)final->base + gamerect.min_y * final->rowbytes + gamerect.min_x * sizeof(UINT32);
/*TODO*///
/*TODO*///	/* vector case */
/*TODO*///	if (display->changed_flags & VECTOR_PIXELS_CHANGED)
/*TODO*///	{
/*TODO*///		vector_pixel_t offset = VECTOR_PIXEL(gamerect.min_x, gamerect.min_y);
/*TODO*///		vector_pixel_t *list = display->vector_dirty_pixels;
/*TODO*///
/*TODO*///		/* 16/15bpp case */
/*TODO*///		if (bitmap->depth != 32)
/*TODO*///		{
/*TODO*///			while (*list != VECTOR_PIXEL_END)
/*TODO*///			{
/*TODO*///				vector_pixel_t coords = *list;
/*TODO*///				x = VECTOR_PIXEL_X(coords);
/*TODO*///				y = VECTOR_PIXEL_Y(coords);
/*TODO*///				*list++ = coords + offset;
/*TODO*///				PIXEL(x,y,dst,dst,32) = palette[PIXEL(x,y,src,src,16)];
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* 32bpp case */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			while (*list != VECTOR_PIXEL_END)
/*TODO*///			{
/*TODO*///				vector_pixel_t coords = *list;
/*TODO*///				x = VECTOR_PIXEL_X(coords);
/*TODO*///				y = VECTOR_PIXEL_Y(coords);
/*TODO*///				*list++ = coords + offset;
/*TODO*///				PIXEL(x,y,dst,dst,32) = PIXEL(x,y,src,src,32);
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* 1x scale */
/*TODO*///	else if (gamescale == 1)
/*TODO*///	{
/*TODO*///		/* 16/15bpp case */
/*TODO*///		if (bitmap->depth != 32)
/*TODO*///		{
/*TODO*///			for (y = 0; y < height; y++)
/*TODO*///			{
/*TODO*///				UINT16 *src = (UINT16 *)srcbase + y * srcrowpixels + Machine->absolute_visible_area.min_x;
/*TODO*///				UINT32 *dst = (UINT32 *)dstbase + y * dstrowpixels;
/*TODO*///				for (x = 0; x < width; x++)
/*TODO*///					*dst++ = palette[*src++];
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* 32bpp case */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			for (y = 0; y < height; y++)
/*TODO*///			{
/*TODO*///				UINT32 *src = (UINT32 *)srcbase + y * srcrowpixels + Machine->absolute_visible_area.min_x;
/*TODO*///				UINT32 *dst = (UINT32 *)dstbase + y * dstrowpixels;
/*TODO*///				for (x = 0; x < width; x++)
/*TODO*///					*dst++ = *src++;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* 2x scale */
/*TODO*///	else if (gamescale == 2)
/*TODO*///	{
/*TODO*///		/* 16/15bpp case */
/*TODO*///		if (bitmap->depth != 32)
/*TODO*///		{
/*TODO*///			for (y = 0; y < height; y++)
/*TODO*///			{
/*TODO*///				UINT16 *src = (UINT16 *)srcbase + y * srcrowpixels + Machine->absolute_visible_area.min_x;
/*TODO*///				UINT32 *dst = (UINT32 *)dstbase + y * 2 * dstrowpixels;
/*TODO*///				for (x = 0; x < width; x++)
/*TODO*///				{
/*TODO*///					UINT32 val = palette[*src++];
/*TODO*///					dst[0] = val;
/*TODO*///					dst[1] = val;
/*TODO*///					dst[dstrowpixels] = val;
/*TODO*///					dst[dstrowpixels + 1] = val;
/*TODO*///					dst += 2;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* 32bpp case */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			for (y = 0; y < height; y++)
/*TODO*///			{
/*TODO*///				UINT32 *src = (UINT32 *)srcbase + y * srcrowpixels + Machine->absolute_visible_area.min_x;
/*TODO*///				UINT32 *dst = (UINT32 *)dstbase + y * 2 * dstrowpixels;
/*TODO*///				for (x = 0; x < width; x++)
/*TODO*///				{
/*TODO*///					UINT32 val = *src++;
/*TODO*///					dst[0] = val;
/*TODO*///					dst[1] = val;
/*TODO*///					dst[dstrowpixels] = val;
/*TODO*///					dst[dstrowpixels + 1] = val;
/*TODO*///					dst += 2;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	render_game_bitmap_underlay - render the game
/*TODO*///	bitmap on top of an underlay
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void render_game_bitmap_underlay(struct mame_bitmap *bitmap, const rgb_t *palette, struct mame_display *display)
/*TODO*///{
/*TODO*///	int srcrowpixels = bitmap->rowpixels;
/*TODO*///	int dstrowpixels = final->rowpixels;
/*TODO*///	void *srcbase, *dstbase, *undbase;
/*TODO*///	int width, height;
/*TODO*///	int x, y;
/*TODO*///
/*TODO*///	/* compute common parameters */
/*TODO*///	width = Machine->absolute_visible_area.max_x - Machine->absolute_visible_area.min_x + 1;
/*TODO*///	height = Machine->absolute_visible_area.max_y - Machine->absolute_visible_area.min_y + 1;
/*TODO*///	srcbase = (UINT8 *)bitmap->base + Machine->absolute_visible_area.min_y * bitmap->rowbytes;
/*TODO*///	dstbase = (UINT8 *)final->base + gamerect.min_y * final->rowbytes + gamerect.min_x * sizeof(UINT32);
/*TODO*///	undbase = (UINT8 *)underlay->base + gamerect.min_y * underlay->rowbytes + gamerect.min_x * sizeof(UINT32);
/*TODO*///
/*TODO*///	/* vector case */
/*TODO*///	if (display->changed_flags & VECTOR_PIXELS_CHANGED)
/*TODO*///	{
/*TODO*///		vector_pixel_t offset = VECTOR_PIXEL(gamerect.min_x, gamerect.min_y);
/*TODO*///		vector_pixel_t *list = display->vector_dirty_pixels;
/*TODO*///
/*TODO*///		/* 16/15bpp case */
/*TODO*///		if (bitmap->depth != 32)
/*TODO*///		{
/*TODO*///			while (*list != VECTOR_PIXEL_END)
/*TODO*///			{
/*TODO*///				vector_pixel_t coords = *list;
/*TODO*///				x = VECTOR_PIXEL_X(coords);
/*TODO*///				y = VECTOR_PIXEL_Y(coords);
/*TODO*///				*list++ = coords + offset;
/*TODO*///				PIXEL(x,y,dst,dst,32) = add_and_clamp(palette[PIXEL(x,y,src,src,16)], PIXEL(x,y,und,dst,32));
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* 32bpp case */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			while (*list != VECTOR_PIXEL_END)
/*TODO*///			{
/*TODO*///				vector_pixel_t coords = *list;
/*TODO*///				x = VECTOR_PIXEL_X(coords);
/*TODO*///				y = VECTOR_PIXEL_Y(coords);
/*TODO*///				*list++ = coords + offset;
/*TODO*///				PIXEL(x,y,dst,dst,32) = add_and_clamp(PIXEL(x,y,src,src,32), PIXEL(x,y,und,dst,32));
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* 1x scale */
/*TODO*///	else if (gamescale == 1)
/*TODO*///	{
/*TODO*///		/* 16/15bpp case */
/*TODO*///		if (bitmap->depth != 32)
/*TODO*///		{
/*TODO*///			for (y = 0; y < height; y++)
/*TODO*///			{
/*TODO*///				UINT16 *src = (UINT16 *)srcbase + y * srcrowpixels + Machine->absolute_visible_area.min_x;
/*TODO*///				UINT32 *dst = (UINT32 *)dstbase + y * dstrowpixels;
/*TODO*///				UINT32 *und = (UINT32 *)undbase + y * dstrowpixels;
/*TODO*///				for (x = 0; x < width; x++)
/*TODO*///					*dst++ = add_and_clamp(palette[*src++], *und++);
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* 32bpp case */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			for (y = 0; y < height; y++)
/*TODO*///			{
/*TODO*///				UINT32 *src = (UINT32 *)srcbase + y * srcrowpixels + Machine->absolute_visible_area.min_x;
/*TODO*///				UINT32 *dst = (UINT32 *)dstbase + y * dstrowpixels;
/*TODO*///				UINT32 *und = (UINT32 *)undbase + y * dstrowpixels;
/*TODO*///				for (x = 0; x < width; x++)
/*TODO*///					*dst++ = add_and_clamp(*src++, *und++);
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* 2x scale */
/*TODO*///	else if (gamescale == 2)
/*TODO*///	{
/*TODO*///		/* 16/15bpp case */
/*TODO*///		if (bitmap->depth != 32)
/*TODO*///		{
/*TODO*///			for (y = 0; y < height; y++)
/*TODO*///			{
/*TODO*///				UINT16 *src = (UINT16 *)srcbase + y * srcrowpixels + Machine->absolute_visible_area.min_x;
/*TODO*///				UINT32 *dst = (UINT32 *)dstbase + y * 2 * dstrowpixels;
/*TODO*///				UINT32 *und = (UINT32 *)undbase + y * 2 * dstrowpixels;
/*TODO*///				for (x = 0; x < width; x++)
/*TODO*///				{
/*TODO*///					UINT32 val = palette[*src++];
/*TODO*///					dst[0] = add_and_clamp(val, und[0]);
/*TODO*///					dst[1] = add_and_clamp(val, und[1]);
/*TODO*///					dst[dstrowpixels] = add_and_clamp(val, und[dstrowpixels]);
/*TODO*///					dst[dstrowpixels + 1] = add_and_clamp(val, und[dstrowpixels + 1]);
/*TODO*///					dst += 2;
/*TODO*///					und += 2;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* 32bpp case */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			for (y = 0; y < height; y++)
/*TODO*///			{
/*TODO*///				UINT32 *src = (UINT32 *)srcbase + y * srcrowpixels + Machine->absolute_visible_area.min_x;
/*TODO*///				UINT32 *dst = (UINT32 *)dstbase + y * 2 * dstrowpixels;
/*TODO*///				UINT32 *und = (UINT32 *)undbase + y * 2 * dstrowpixels;
/*TODO*///				for (x = 0; x < width; x++)
/*TODO*///				{
/*TODO*///					UINT32 val = *src++;
/*TODO*///					dst[0] = add_and_clamp(val, und[0]);
/*TODO*///					dst[1] = add_and_clamp(val, und[1]);
/*TODO*///					dst[dstrowpixels] = add_and_clamp(val, und[dstrowpixels]);
/*TODO*///					dst[dstrowpixels + 1] = add_and_clamp(val, und[dstrowpixels + 1]);
/*TODO*///					dst += 2;
/*TODO*///					und += 2;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	render_game_bitmap_overlay - render the game
/*TODO*///	bitmap blended with an overlay
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void render_game_bitmap_overlay(struct mame_bitmap *bitmap, const rgb_t *palette, struct mame_display *display)
/*TODO*///{
/*TODO*///	int srcrowpixels = bitmap->rowpixels;
/*TODO*///	int dstrowpixels = final->rowpixels;
/*TODO*///	void *srcbase, *dstbase, *overbase, *overyrgbbase;
/*TODO*///	int width, height;
/*TODO*///	int x, y;
/*TODO*///
/*TODO*///	/* compute common parameters */
/*TODO*///	width = Machine->absolute_visible_area.max_x - Machine->absolute_visible_area.min_x + 1;
/*TODO*///	height = Machine->absolute_visible_area.max_y - Machine->absolute_visible_area.min_y + 1;
/*TODO*///	srcbase = (UINT8 *)bitmap->base + Machine->absolute_visible_area.min_y * bitmap->rowbytes;
/*TODO*///	dstbase = (UINT8 *)final->base + gamerect.min_y * final->rowbytes + gamerect.min_x * sizeof(UINT32);
/*TODO*///	overbase = (UINT8 *)overlay->base + gamerect.min_y * overlay->rowbytes + gamerect.min_x * sizeof(UINT32);
/*TODO*///	overyrgbbase = (UINT8 *)overlay_yrgb->base + gamerect.min_y * overlay_yrgb->rowbytes + gamerect.min_x * sizeof(UINT32);
/*TODO*///
/*TODO*///	/* vector case */
/*TODO*///	if (display->changed_flags & VECTOR_PIXELS_CHANGED)
/*TODO*///	{
/*TODO*///		vector_pixel_t offset = VECTOR_PIXEL(gamerect.min_x, gamerect.min_y);
/*TODO*///		vector_pixel_t *list = display->vector_dirty_pixels;
/*TODO*///
/*TODO*///		/* 16/15bpp case */
/*TODO*///		if (bitmap->depth != 32)
/*TODO*///		{
/*TODO*///			while (*list != VECTOR_PIXEL_END)
/*TODO*///			{
/*TODO*///				vector_pixel_t coords = *list;
/*TODO*///				x = VECTOR_PIXEL_X(coords);
/*TODO*///				y = VECTOR_PIXEL_Y(coords);
/*TODO*///				*list++ = coords + offset;
/*TODO*///				PIXEL(x,y,dst,dst,32) = blend_over(palette[PIXEL(x,y,src,src,16)], PIXEL(x,y,over,dst,32), PIXEL(x,y,overyrgb,dst,32));
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* 32bpp case */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			while (*list != VECTOR_PIXEL_END)
/*TODO*///			{
/*TODO*///				vector_pixel_t coords = *list;
/*TODO*///				x = VECTOR_PIXEL_X(coords);
/*TODO*///				y = VECTOR_PIXEL_Y(coords);
/*TODO*///				*list++ = coords + offset;
/*TODO*///				PIXEL(x,y,dst,dst,32) = blend_over(PIXEL(x,y,src,src,32), PIXEL(x,y,over,dst,32), PIXEL(x,y,overyrgb,dst,32));
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* 1x scale */
/*TODO*///	else if (gamescale == 1)
/*TODO*///	{
/*TODO*///		/* 16/15bpp case */
/*TODO*///		if (bitmap->depth != 32)
/*TODO*///		{
/*TODO*///			for (y = 0; y < height; y++)
/*TODO*///			{
/*TODO*///				UINT16 *src = (UINT16 *)srcbase + y * srcrowpixels + Machine->absolute_visible_area.min_x;
/*TODO*///				UINT32 *dst = (UINT32 *)dstbase + y * dstrowpixels;
/*TODO*///				UINT32 *over = (UINT32 *)overbase + y * dstrowpixels;
/*TODO*///				UINT32 *overyrgb = (UINT32 *)overyrgbbase + y * dstrowpixels;
/*TODO*///				for (x = 0; x < width; x++)
/*TODO*///					*dst++ = blend_over(palette[*src++], *over++, *overyrgb++);
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* 32bpp case */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			for (y = 0; y < height; y++)
/*TODO*///			{
/*TODO*///				UINT32 *src = (UINT32 *)srcbase + y * srcrowpixels + Machine->absolute_visible_area.min_x;
/*TODO*///				UINT32 *dst = (UINT32 *)dstbase + y * dstrowpixels;
/*TODO*///				UINT32 *over = (UINT32 *)overbase + y * dstrowpixels;
/*TODO*///				UINT32 *overyrgb = (UINT32 *)overyrgbbase + y * dstrowpixels;
/*TODO*///				for (x = 0; x < width; x++)
/*TODO*///					*dst++ = blend_over(*src++, *over++, *overyrgb++);
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* 2x scale */
/*TODO*///	else if (gamescale == 2)
/*TODO*///	{
/*TODO*///		/* 16/15bpp case */
/*TODO*///		if (bitmap->depth != 32)
/*TODO*///		{
/*TODO*///			for (y = 0; y < height; y++)
/*TODO*///			{
/*TODO*///				UINT16 *src = (UINT16 *)srcbase + y * srcrowpixels + Machine->absolute_visible_area.min_x;
/*TODO*///				UINT32 *dst = (UINT32 *)dstbase + y * 2 * dstrowpixels;
/*TODO*///				UINT32 *over = (UINT32 *)overbase + y * 2 * dstrowpixels;
/*TODO*///				UINT32 *overyrgb = (UINT32 *)overyrgbbase + y * 2 * dstrowpixels;
/*TODO*///				for (x = 0; x < width; x++)
/*TODO*///				{
/*TODO*///					UINT32 val = palette[*src++];
/*TODO*///					dst[0] = blend_over(val, over[0], overyrgb[0]);
/*TODO*///					dst[1] = blend_over(val, over[1], overyrgb[1]);
/*TODO*///					dst[dstrowpixels] = blend_over(val, over[dstrowpixels], overyrgb[dstrowpixels]);
/*TODO*///					dst[dstrowpixels + 1] = blend_over(val, over[dstrowpixels + 1], overyrgb[dstrowpixels + 1]);
/*TODO*///					dst += 2;
/*TODO*///					over += 2;
/*TODO*///					overyrgb += 2;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* 32bpp case */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			for (y = 0; y < height; y++)
/*TODO*///			{
/*TODO*///				UINT32 *src = (UINT32 *)srcbase + y * srcrowpixels + Machine->absolute_visible_area.min_x;
/*TODO*///				UINT32 *dst = (UINT32 *)dstbase + y * 2 * dstrowpixels;
/*TODO*///				UINT32 *over = (UINT32 *)overbase + y * 2 * dstrowpixels;
/*TODO*///				UINT32 *overyrgb = (UINT32 *)overyrgbbase + y * 2 * dstrowpixels;
/*TODO*///				for (x = 0; x < width; x++)
/*TODO*///				{
/*TODO*///					UINT32 val = *src++;
/*TODO*///					dst[0] = blend_over(val, over[0], overyrgb[0]);
/*TODO*///					dst[1] = blend_over(val, over[1], overyrgb[1]);
/*TODO*///					dst[dstrowpixels] = blend_over(val, over[dstrowpixels], overyrgb[dstrowpixels]);
/*TODO*///					dst[dstrowpixels + 1] = blend_over(val, over[dstrowpixels + 1], overyrgb[dstrowpixels + 1]);
/*TODO*///					dst += 2;
/*TODO*///					over += 2;
/*TODO*///					overyrgb += 2;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	render_game_bitmap_underlay_overlay - render
/*TODO*///	the game bitmap blended with an overlay and
/*TODO*///	added to an underlay
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void render_game_bitmap_underlay_overlay(struct mame_bitmap *bitmap, const rgb_t *palette, struct mame_display *display)
/*TODO*///{
/*TODO*///	int srcrowpixels = bitmap->rowpixels;
/*TODO*///	int dstrowpixels = final->rowpixels;
/*TODO*///	void *srcbase, *dstbase, *undbase, *overbase, *overyrgbbase;
/*TODO*///	int width, height;
/*TODO*///	int x, y;
/*TODO*///
/*TODO*///	/* compute common parameters */
/*TODO*///	width = Machine->absolute_visible_area.max_x - Machine->absolute_visible_area.min_x + 1;
/*TODO*///	height = Machine->absolute_visible_area.max_y - Machine->absolute_visible_area.min_y + 1;
/*TODO*///	srcbase = (UINT8 *)bitmap->base + Machine->absolute_visible_area.min_y * bitmap->rowbytes;
/*TODO*///	dstbase = (UINT8 *)final->base + gamerect.min_y * final->rowbytes + gamerect.min_x * sizeof(UINT32);
/*TODO*///	undbase = (UINT8 *)underlay->base + gamerect.min_y * underlay->rowbytes + gamerect.min_x * sizeof(UINT32);
/*TODO*///	overbase = (UINT8 *)overlay->base + gamerect.min_y * overlay->rowbytes + gamerect.min_x * sizeof(UINT32);
/*TODO*///	overyrgbbase = (UINT8 *)overlay_yrgb->base + gamerect.min_y * overlay_yrgb->rowbytes + gamerect.min_x * sizeof(UINT32);
/*TODO*///
/*TODO*///	/* vector case */
/*TODO*///	if (display->changed_flags & VECTOR_PIXELS_CHANGED)
/*TODO*///	{
/*TODO*///		vector_pixel_t offset = VECTOR_PIXEL(gamerect.min_x, gamerect.min_y);
/*TODO*///		vector_pixel_t *list = display->vector_dirty_pixels;
/*TODO*///
/*TODO*///		/* 16/15bpp case */
/*TODO*///		if (bitmap->depth != 32)
/*TODO*///		{
/*TODO*///			while (*list != VECTOR_PIXEL_END)
/*TODO*///			{
/*TODO*///				vector_pixel_t coords = *list;
/*TODO*///				x = VECTOR_PIXEL_X(coords);
/*TODO*///				y = VECTOR_PIXEL_Y(coords);
/*TODO*///				*list++ = coords + offset;
/*TODO*///				PIXEL(x,y,dst,dst,32) = add_and_clamp(blend_over(palette[PIXEL(x,y,src,src,16)], PIXEL(x,y,over,dst,32), PIXEL(x,y,overyrgb,dst,32)), PIXEL(x,y,und,dst,32));
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* 32bpp case */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			while (*list != VECTOR_PIXEL_END)
/*TODO*///			{
/*TODO*///				vector_pixel_t coords = *list;
/*TODO*///				x = VECTOR_PIXEL_X(coords);
/*TODO*///				y = VECTOR_PIXEL_Y(coords);
/*TODO*///				*list++ = coords + offset;
/*TODO*///				PIXEL(x,y,dst,dst,32) = add_and_clamp(blend_over(PIXEL(x,y,src,src,32), PIXEL(x,y,over,dst,32), PIXEL(x,y,overyrgb,dst,32)), PIXEL(x,y,und,dst,32));
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* 1x scale */
/*TODO*///	else if (gamescale == 1)
/*TODO*///	{
/*TODO*///		/* 16/15bpp case */
/*TODO*///		if (bitmap->depth != 32)
/*TODO*///		{
/*TODO*///			for (y = 0; y < height; y++)
/*TODO*///			{
/*TODO*///				UINT16 *src = (UINT16 *)srcbase + y * srcrowpixels + Machine->absolute_visible_area.min_x;
/*TODO*///				UINT32 *dst = (UINT32 *)dstbase + y * dstrowpixels;
/*TODO*///				UINT32 *und = (UINT32 *)undbase + y * dstrowpixels;
/*TODO*///				UINT32 *over = (UINT32 *)overbase + y * dstrowpixels;
/*TODO*///				UINT32 *overyrgb = (UINT32 *)overyrgbbase + y * dstrowpixels;
/*TODO*///				for (x = 0; x < width; x++)
/*TODO*///					*dst++ = add_and_clamp(blend_over(palette[*src++], *over++, *overyrgb++), *und++);
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* 32bpp case */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			for (y = 0; y < height; y++)
/*TODO*///			{
/*TODO*///				UINT32 *src = (UINT32 *)srcbase + y * srcrowpixels + Machine->absolute_visible_area.min_x;
/*TODO*///				UINT32 *dst = (UINT32 *)dstbase + y * dstrowpixels;
/*TODO*///				UINT32 *und = (UINT32 *)undbase + y * dstrowpixels;
/*TODO*///				UINT32 *over = (UINT32 *)overbase + y * dstrowpixels;
/*TODO*///				UINT32 *overyrgb = (UINT32 *)overyrgbbase + y * dstrowpixels;
/*TODO*///				for (x = 0; x < width; x++)
/*TODO*///					*dst++ = add_and_clamp(blend_over(*src++, *over++, *overyrgb++), *und++);
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* 2x scale */
/*TODO*///	else if (gamescale == 2)
/*TODO*///	{
/*TODO*///		/* 16/15bpp case */
/*TODO*///		if (bitmap->depth != 32)
/*TODO*///		{
/*TODO*///			for (y = 0; y < height; y++)
/*TODO*///			{
/*TODO*///				UINT16 *src = (UINT16 *)srcbase + y * srcrowpixels + Machine->absolute_visible_area.min_x;
/*TODO*///				UINT32 *dst = (UINT32 *)dstbase + y * 2 * dstrowpixels;
/*TODO*///				UINT32 *und = (UINT32 *)undbase + y * 2 * dstrowpixels;
/*TODO*///				UINT32 *over = (UINT32 *)overbase + y * 2 * dstrowpixels;
/*TODO*///				UINT32 *overyrgb = (UINT32 *)overyrgbbase + y * 2 * dstrowpixels;
/*TODO*///				for (x = 0; x < width; x++)
/*TODO*///				{
/*TODO*///					UINT32 val = palette[*src++];
/*TODO*///					dst[0] = add_and_clamp(blend_over(val, over[0], overyrgb[0]), und[0]);
/*TODO*///					dst[1] = add_and_clamp(blend_over(val, over[1], overyrgb[1]), und[1]);
/*TODO*///					dst[dstrowpixels] = add_and_clamp(blend_over(val, over[dstrowpixels], overyrgb[dstrowpixels]), und[dstrowpixels]);
/*TODO*///					dst[dstrowpixels + 1] = add_and_clamp(blend_over(val, over[dstrowpixels + 1], overyrgb[dstrowpixels + 1]), und[dstrowpixels + 1]);
/*TODO*///					dst += 2;
/*TODO*///					und += 2;
/*TODO*///					over += 2;
/*TODO*///					overyrgb += 2;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* 32bpp case */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			for (y = 0; y < height; y++)
/*TODO*///			{
/*TODO*///				UINT32 *src = (UINT32 *)srcbase + y * srcrowpixels + Machine->absolute_visible_area.min_x;
/*TODO*///				UINT32 *dst = (UINT32 *)dstbase + y * 2 * dstrowpixels;
/*TODO*///				UINT32 *und = (UINT32 *)undbase + y * 2 * dstrowpixels;
/*TODO*///				UINT32 *over = (UINT32 *)overbase + y * 2 * dstrowpixels;
/*TODO*///				UINT32 *overyrgb = (UINT32 *)overyrgbbase + y * 2 * dstrowpixels;
/*TODO*///				for (x = 0; x < width; x++)
/*TODO*///				{
/*TODO*///					UINT32 val = *src++;
/*TODO*///					dst[0] = add_and_clamp(blend_over(val, over[0], overyrgb[0]), und[0]);
/*TODO*///					dst[1] = add_and_clamp(blend_over(val, over[1], overyrgb[1]), und[1]);
/*TODO*///					dst[dstrowpixels] = add_and_clamp(blend_over(val, over[dstrowpixels], overyrgb[dstrowpixels]), und[dstrowpixels]);
/*TODO*///					dst[dstrowpixels + 1] = add_and_clamp(blend_over(val, over[dstrowpixels + 1], overyrgb[dstrowpixels + 1]), und[dstrowpixels + 1]);
/*TODO*///					dst += 2;
/*TODO*///					und += 2;
/*TODO*///					over += 2;
/*TODO*///					overyrgb += 2;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	render_ui_overlay - render the UI overlay
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void render_ui_overlay(struct mame_bitmap *bitmap, UINT32 *dirty, const rgb_t *palette, struct mame_display *display)
/*TODO*///{
/*TODO*///	int srcrowpixels = bitmap->rowpixels;
/*TODO*///	int dstrowpixels = final->rowpixels;
/*TODO*///	void *srcbase, *dstbase;
/*TODO*///	int width, height;
/*TODO*///	int x, y, h;
/*TODO*///
/*TODO*///	/* compute common parameters */
/*TODO*///	width = bitmap->width;
/*TODO*///	height = bitmap->height;
/*TODO*///	srcbase = bitmap->base;
/*TODO*///	dstbase = final->base;
/*TODO*///
/*TODO*///	/* 16/15bpp case */
/*TODO*///	if (bitmap->depth != 32)
/*TODO*///	{
/*TODO*///		for (y = 0; y < height; y++)
/*TODO*///		{
/*TODO*///			UINT16 *src = (UINT16 *)srcbase + y * srcrowpixels;
/*TODO*///			UINT32 *dst = (UINT32 *)dstbase + y * dstrowpixels;
/*TODO*///			UINT32 *hint = &dirty[y * MAX_HINTS_PER_SCANLINE];
/*TODO*///			for (h = 0; h < MAX_HINTS_PER_SCANLINE && hint[h] != 0; h++)
/*TODO*///			{
/*TODO*///				int start = hint[h] >> 16;
/*TODO*///				int stop = hint[h] & 0xffff;
/*TODO*///				hint[h] = 0;
/*TODO*///
/*TODO*///				for (x = start; x <= stop; x++)
/*TODO*///				{
/*TODO*///					int pix = src[x];
/*TODO*///					if (pix != UI_TRANSPARENT_COLOR16)
/*TODO*///					{
/*TODO*///						dst[x] = palette[pix];
/*TODO*///						src[x] = UI_TRANSPARENT_COLOR16;
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* 32bpp case */
/*TODO*///	else
/*TODO*///	{
/*TODO*///		for (y = 0; y < height; y++)
/*TODO*///		{
/*TODO*///			UINT32 *src = (UINT32 *)srcbase + y * srcrowpixels;
/*TODO*///			UINT32 *dst = (UINT32 *)dstbase + y * dstrowpixels;
/*TODO*///			UINT32 *hint = &dirty[y * MAX_HINTS_PER_SCANLINE];
/*TODO*///			for (h = 0; h < MAX_HINTS_PER_SCANLINE && hint[h] != 0; h++)
/*TODO*///			{
/*TODO*///				int start = hint[h] >> 16;
/*TODO*///				int stop = hint[h] & 0xffff;
/*TODO*///				hint[h] = 0;
/*TODO*///
/*TODO*///				for (x = start; x <= stop; x++)
/*TODO*///				{
/*TODO*///					int pix = src[x];
/*TODO*///					if (pix != UI_TRANSPARENT_COLOR32)
/*TODO*///					{
/*TODO*///						dst[x] = pix;
/*TODO*///						src[x] = UI_TRANSPARENT_COLOR32;
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	artwork_load_artwork_file - default MAME way
/*TODO*///	to locate an artwork file
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///mame_file *artwork_load_artwork_file(const struct GameDriver **driver)
/*TODO*///{
/*TODO*///	char filename[100];
/*TODO*///	mame_file *artfile = NULL;
/*TODO*///
/*TODO*///	while (*driver)
/*TODO*///	{
/*TODO*///		if ((*driver)->name)
/*TODO*///		{
/*TODO*///			sprintf(filename, "%s.art", (*driver)->name);
/*TODO*///			artfile = mame_fopen((*driver)->name, filename, FILETYPE_ARTWORK, 0);
/*TODO*///			if (artfile)
/*TODO*///				break;
/*TODO*///		}
/*TODO*///		*driver = (*driver)->clone_of;
/*TODO*///	}
/*TODO*///	return artfile;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///#if 0
/*TODO*///#pragma mark -
/*TODO*///#pragma mark BITMAP LOADING/MANIPULATING
/*TODO*///#endif
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	artwork_load - locate the .art file and
/*TODO*///	read all the bitmaps
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int artwork_load(const struct GameDriver *driver, int width, int height, const struct artwork_callbacks *callbacks)
/*TODO*///{
/*TODO*///	const struct overlay_piece *list = overlay_list;
/*TODO*///	struct artwork_piece *piece;
/*TODO*///	mame_file *artfile;
/*TODO*///	int result;
/*TODO*///
/*TODO*///	/* reset the list of artwork */
/*TODO*///	num_pieces = 0;
/*TODO*///	num_underlays = 0;
/*TODO*///	num_overlays = 0;
/*TODO*///	num_bezels = 0;
/*TODO*///	overlay_list = NULL;
/*TODO*///
/*TODO*///	/* if the user turned artwork off, bail */
/*TODO*///	if (!options.use_artwork)
/*TODO*///		return 1;
/*TODO*///
/*TODO*///	/* first process any hard-coded overlays */
/*TODO*///	if (list && !generate_overlay(list, width, height))
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	/* attempt to open the .ART file; if none, that's okay */
/*TODO*///	artfile = callbacks->load_artwork(&driver);
/*TODO*///	if (!artfile && !list)
/*TODO*///		return 1;
/*TODO*///
/*TODO*///	/* parse the file into pieces */
/*TODO*///	if (artfile)
/*TODO*///	{
/*TODO*///		result = parse_art_file(artfile);
/*TODO*///		mame_fclose(artfile);
/*TODO*///		if (!result)
/*TODO*///			return 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* sort the pieces */
/*TODO*///	sort_pieces();
/*TODO*///
/*TODO*///	/* now read the artwork files */
/*TODO*///	for (piece = artwork_list; piece; piece = piece->next)
/*TODO*///	{
/*TODO*///		/* convert from pixel coordinates if necessary */
/*TODO*///		if (fabs(piece->left) > 4.0 || fabs(piece->right) > 4.0 ||
/*TODO*///			fabs(piece->top) > 4.0 || fabs(piece->bottom) > 4.0)
/*TODO*///		{
/*TODO*///			piece->left /= (double)width;
/*TODO*///			piece->right /= (double)width;
/*TODO*///			piece->top /= (double)height;
/*TODO*///			piece->bottom /= (double)height;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* assign to one of the categories */
/*TODO*///		if (piece->layer == LAYER_BACKDROP)
/*TODO*///			num_underlays++;
/*TODO*///		else if (piece->layer == LAYER_OVERLAY)
/*TODO*///			num_overlays++;
/*TODO*///		else if (piece->layer >= LAYER_BEZEL)
/*TODO*///			num_bezels++;
/*TODO*///
/*TODO*///		/* load the graphics */
/*TODO*///		if (driver)
/*TODO*///			load_bitmap(driver->name, piece);
/*TODO*///	}
/*TODO*///// debugging
/*TODO*/////	fprintf(stderr, "backdrops=%d overlays=%d bezels=%d\n", num_underlays, num_overlays, num_bezels);
/*TODO*///
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	open_and_read_png - open a PNG file, read it
/*TODO*///	in, and verify that we can do something with
/*TODO*///	it
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int open_and_read_png(const char *gamename, const char *filename, struct png_info *png)
/*TODO*///{
/*TODO*///	int result;
/*TODO*///	mame_file *file;
/*TODO*///
/*TODO*///	/* open the file */
/*TODO*///	file = mame_fopen(gamename, filename, FILETYPE_ARTWORK, 0);
/*TODO*///	if (!file)
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	/* read the PNG data */
/*TODO*///	result = png_read_file(file, png);
/*TODO*///	mame_fclose(file);
/*TODO*///	if (!result)
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	/* verify we can handle this PNG */
/*TODO*///	if (png->bit_depth > 8)
/*TODO*///	{
/*TODO*///		logerror("Unsupported bit depth %d (8 bit max)\n", png->bit_depth);
/*TODO*///		free(png->image);
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///	if (png->interlace_method != 0)
/*TODO*///	{
/*TODO*///		logerror("Interlace unsupported\n");
/*TODO*///		free(png->image);
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///	if (png->color_type != 0 && png->color_type != 3 && png->color_type != 2 && png->color_type != 6)
/*TODO*///	{
/*TODO*///		logerror("Unsupported color type %d\n", png->color_type);
/*TODO*///		free(png->image);
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* if less than 8 bits, upsample */
/*TODO*///	png_expand_buffer_8bit(png);
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	load_bitmap - load the artwork into a bitmap
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int load_bitmap(const char *gamename, struct artwork_piece *piece)
/*TODO*///{
/*TODO*///	struct png_info png;
/*TODO*///	UINT8 *src;
/*TODO*///	int x, y;
/*TODO*///
/*TODO*///	/* if we already have a bitmap, don't bother trying to read a file */
/*TODO*///	if (piece->rawbitmap)
/*TODO*///		return 1;
/*TODO*///
/*TODO*///	/* open and read the main png file */
/*TODO*///	if (!open_and_read_png(gamename, piece->filename, &png))
/*TODO*///	{
/*TODO*///		logerror("Can't load PNG file: %s\n", piece->filename);
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* allocate the rawbitmap and erase it */
/*TODO*///	piece->rawbitmap = auto_bitmap_alloc_depth(png.width, png.height, 32);
/*TODO*///	if (!piece->rawbitmap)
/*TODO*///		return 0;
/*TODO*///	fillbitmap(piece->rawbitmap, 0, NULL);
/*TODO*///
/*TODO*///	/* handle 8bpp palettized case */
/*TODO*///	if (png.color_type == 3)
/*TODO*///	{
/*TODO*///		/* loop over width/height */
/*TODO*///		src = png.image;
/*TODO*///		for (y = 0; y < png.height; y++)
/*TODO*///			for (x = 0; x < png.width; x++, src++)
/*TODO*///			{
/*TODO*///				/* determine alpha */
/*TODO*///				UINT8 alpha = (*src < png.num_trans) ? png.trans[*src] : 0xff;
/*TODO*///				if (alpha != 0xff)
/*TODO*///					piece->has_alpha = 1;
/*TODO*///
/*TODO*///				/* expand to 32bpp */
/*TODO*///				plot_pixel(piece->rawbitmap, x, y, MAKE_ARGB(alpha, png.palette[*src * 3], png.palette[*src * 3 + 1], png.palette[*src * 3 + 2]));
/*TODO*///			}
/*TODO*///
/*TODO*///		/* free memory for the palette */
/*TODO*///		free(png.palette);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* handle 8bpp grayscale case */
/*TODO*///	else if (png.color_type == 0)
/*TODO*///	{
/*TODO*///		/* loop over width/height */
/*TODO*///		src = png.image;
/*TODO*///		for (y = 0; y < png.height; y++)
/*TODO*///			for (x = 0; x < png.width; x++, src++)
/*TODO*///				plot_pixel(piece->rawbitmap, x, y, MAKE_ARGB(0xff, *src, *src, *src));
/*TODO*///	}
/*TODO*///
/*TODO*///	/* handle 32bpp non-alpha case */
/*TODO*///	else if (png.color_type == 2)
/*TODO*///	{
/*TODO*///		/* loop over width/height */
/*TODO*///		src = png.image;
/*TODO*///		for (y = 0; y < png.height; y++)
/*TODO*///			for (x = 0; x < png.width; x++, src += 3)
/*TODO*///				plot_pixel(piece->rawbitmap, x, y, MAKE_ARGB(0xff, src[0], src[1], src[2]));
/*TODO*///	}
/*TODO*///
/*TODO*///	/* handle 32bpp alpha case */
/*TODO*///	else
/*TODO*///	{
/*TODO*///		/* loop over width/height */
/*TODO*///		src = png.image;
/*TODO*///		for (y = 0; y < png.height; y++)
/*TODO*///			for (x = 0; x < png.width; x++, src += 4)
/*TODO*///				plot_pixel(piece->rawbitmap, x, y, MAKE_ARGB(src[3], src[0], src[1], src[2]));
/*TODO*///		piece->has_alpha = 1;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* free the raw image data and return after loading any alpha map */
/*TODO*///	free(png.image);
/*TODO*///	return load_alpha_bitmap(gamename, piece, &png);
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	load_alpha_bitmap - load the external alpha
/*TODO*///	mask
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int load_alpha_bitmap(const char *gamename, struct artwork_piece *piece, const struct png_info *original)
/*TODO*///{
/*TODO*///	struct png_info png;
/*TODO*///	UINT8 *src;
/*TODO*///	int x, y;
/*TODO*///
/*TODO*///	/* if no file, we succeeded */
/*TODO*///	if (!piece->alpha_filename)
/*TODO*///		return 1;
/*TODO*///
/*TODO*///	/* open and read the alpha png file */
/*TODO*///	if (!open_and_read_png(gamename, piece->alpha_filename, &png))
/*TODO*///	{
/*TODO*///		logerror("Can't load PNG file: %s\n", piece->alpha_filename);
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* must be the same size */
/*TODO*///	if (png.height != original->height || png.width != original->width)
/*TODO*///	{
/*TODO*///		logerror("Alpha PNG must match original's dimensions: %s\n", piece->alpha_filename);
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* okay, we have alpha */
/*TODO*///	piece->has_alpha = 1;
/*TODO*///
/*TODO*///	/* handle 8bpp palettized case */
/*TODO*///	if (png.color_type == 3)
/*TODO*///	{
/*TODO*///		/* loop over width/height */
/*TODO*///		src = png.image;
/*TODO*///		for (y = 0; y < png.height; y++)
/*TODO*///			for (x = 0; x < png.width; x++, src++)
/*TODO*///			{
/*TODO*///				rgb_t pixel = read_pixel(piece->rawbitmap, x, y);
/*TODO*///				UINT8 alpha = compute_brightness(MAKE_RGB(png.palette[*src * 3], png.palette[*src * 3 + 1], png.palette[*src * 3 + 2]));
/*TODO*///				plot_pixel(piece->rawbitmap, x, y, MAKE_ARGB(alpha, RGB_RED(pixel), RGB_GREEN(pixel), RGB_BLUE(pixel)));
/*TODO*///			}
/*TODO*///
/*TODO*///		/* free memory for the palette */
/*TODO*///		free(png.palette);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* handle 8bpp grayscale case */
/*TODO*///	else if (png.color_type == 0)
/*TODO*///	{
/*TODO*///		/* loop over width/height */
/*TODO*///		src = png.image;
/*TODO*///		for (y = 0; y < png.height; y++)
/*TODO*///			for (x = 0; x < png.width; x++, src++)
/*TODO*///			{
/*TODO*///				rgb_t pixel = read_pixel(piece->rawbitmap, x, y);
/*TODO*///				plot_pixel(piece->rawbitmap, x, y, MAKE_ARGB(*src, RGB_RED(pixel), RGB_GREEN(pixel), RGB_BLUE(pixel)));
/*TODO*///			}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* handle 32bpp non-alpha case */
/*TODO*///	else if (png.color_type == 2)
/*TODO*///	{
/*TODO*///		/* loop over width/height */
/*TODO*///		src = png.image;
/*TODO*///		for (y = 0; y < png.height; y++)
/*TODO*///			for (x = 0; x < png.width; x++, src += 3)
/*TODO*///			{
/*TODO*///				rgb_t pixel = read_pixel(piece->rawbitmap, x, y);
/*TODO*///				UINT8 alpha = compute_brightness(MAKE_RGB(src[0], src[1], src[2]));
/*TODO*///				plot_pixel(piece->rawbitmap, x, y, MAKE_ARGB(alpha, RGB_RED(pixel), RGB_GREEN(pixel), RGB_BLUE(pixel)));
/*TODO*///			}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* handle 32bpp alpha case */
/*TODO*///	else
/*TODO*///	{
/*TODO*///		/* loop over width/height */
/*TODO*///		src = png.image;
/*TODO*///		for (y = 0; y < png.height; y++)
/*TODO*///			for (x = 0; x < png.width; x++, src += 4)
/*TODO*///			{
/*TODO*///				rgb_t pixel = read_pixel(piece->rawbitmap, x, y);
/*TODO*///				UINT8 alpha = compute_brightness(MAKE_RGB(src[0], src[1], src[2]));
/*TODO*///				plot_pixel(piece->rawbitmap, x, y, MAKE_ARGB(alpha, RGB_RED(pixel), RGB_GREEN(pixel), RGB_BLUE(pixel)));
/*TODO*///			}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* free the raw image data */
/*TODO*///	free(png.image);
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	artwork_prep - prepare the artwork
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int artwork_prep(void)
/*TODO*///{
/*TODO*///	struct artwork_piece *piece;
/*TODO*///
/*TODO*///	/* mark everything dirty */
/*TODO*///	underlay_invalid = screenrect;
/*TODO*///	overlay_invalid = screenrect;
/*TODO*///	bezel_invalid = screenrect;
/*TODO*///
/*TODO*///	/* loop through all the pieces, generating the scaled bitmaps */
/*TODO*///	for (piece = artwork_list; piece; piece = piece->next)
/*TODO*///	{
/*TODO*///		/* scale to the artwork's intended dimensions */
/*TODO*///		if (!scale_bitmap(piece, piece->bounds.max_x - piece->bounds.min_x + 1, piece->bounds.max_y - piece->bounds.min_y + 1))
/*TODO*///			return 1;
/*TODO*///
/*TODO*///		/* trim the bitmap down if transparent */
/*TODO*///		trim_bitmap(piece);
/*TODO*///
/*TODO*///		/* do we intersect the game rect? */
/*TODO*///		piece->intersects_game = 0;
/*TODO*///		if (piece->bounds.max_x > gamerect.min_x && piece->bounds.min_x < gamerect.max_x &&
/*TODO*///			piece->bounds.max_y > gamerect.min_y && piece->bounds.min_y < gamerect.max_y)
/*TODO*///			piece->intersects_game = 1;
/*TODO*///	}
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	scale_bitmap - scale the bitmap for a
/*TODO*///	given piece of artwork
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int scale_bitmap(struct artwork_piece *piece, int newwidth, int newheight)
/*TODO*///{
/*TODO*///	UINT32 sx, sxfrac, sxstep, sy, syfrac, systep;
/*TODO*///	UINT32 global_brightness, global_alpha;
/*TODO*///	int x, y;
/*TODO*///
/*TODO*///	/* skip if no bitmap */
/*TODO*///	if (!piece->rawbitmap)
/*TODO*///		return 1;
/*TODO*///
/*TODO*///	/* allocate two new bitmaps */
/*TODO*///	piece->prebitmap = auto_bitmap_alloc_depth(newwidth, newheight, -32);
/*TODO*///	piece->yrgbbitmap = auto_bitmap_alloc_depth(newwidth, newheight, -32);
/*TODO*///	if (!piece->prebitmap || !piece->yrgbbitmap)
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	/* also allocate memory for the scanline hints */
/*TODO*///	piece->scanlinehint = auto_malloc(newheight * MAX_HINTS_PER_SCANLINE * sizeof(piece->scanlinehint[0]));
/*TODO*///	if (!piece->scanlinehint)
/*TODO*///		return 0;
/*TODO*///	memset(piece->scanlinehint, 0, newheight * MAX_HINTS_PER_SCANLINE * sizeof(piece->scanlinehint[0]));
/*TODO*///
/*TODO*///	/* convert global brightness and alpha to fixed point */
/*TODO*///	global_brightness = (int)(piece->brightness * 65536.0);
/*TODO*///	global_alpha = (int)(piece->alpha * 65536.0);
/*TODO*///
/*TODO*///	/* compute the step values */
/*TODO*///	sxstep = (UINT32)((double)piece->rawbitmap->width * (double)(1 << 24) / (double)newwidth);
/*TODO*///	systep = (UINT32)((double)piece->rawbitmap->height * (double)(1 << 24) / (double)newheight);
/*TODO*///	sxfrac = (sxstep / 2) & FRAC_MASK;
/*TODO*///	sx = (sxstep / 2) >> FRAC_BITS;
/*TODO*///	syfrac = (systep / 2) & FRAC_MASK;
/*TODO*///	sy = (systep / 2) >> FRAC_BITS;
/*TODO*///
/*TODO*///	/* now do the scaling, using 4-point sampling */
/*TODO*///	for (y = 0; y < newheight; y++)
/*TODO*///	{
/*TODO*///		int prevstate = 0, statex = 0;
/*TODO*///
/*TODO*///		sxfrac = (sxstep / 2) & FRAC_MASK;
/*TODO*///		sx = (sxstep / 2) >> FRAC_BITS;
/*TODO*///
/*TODO*///		/* loop over columns */
/*TODO*///		for (x = 0; x < newwidth; x++)
/*TODO*///		{
/*TODO*///			rgb_t pix1, pix2, pix3, pix4;
/*TODO*///			int xweight, dx;
/*TODO*///			int yweight, dy;
/*TODO*///			int r, g, b, a;
/*TODO*///			int newstate;
/*TODO*///
/*TODO*///			/* determine the weights and which pixels to fetch */
/*TODO*///			if (sxfrac <= FRAC_HALF)
/*TODO*///			{
/*TODO*///				dx = -1;
/*TODO*///				xweight = FRAC_HALF - sxfrac;
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				dx = 0;
/*TODO*///				xweight = FRAC_ONE - (sxfrac - FRAC_HALF);
/*TODO*///			}
/*TODO*///
/*TODO*///			if (syfrac <= FRAC_HALF)
/*TODO*///			{
/*TODO*///				dy = -1;
/*TODO*///				yweight = FRAC_HALF - syfrac;
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				dy = 0;
/*TODO*///				yweight = FRAC_ONE - (syfrac - FRAC_HALF);
/*TODO*///			}
/*TODO*///
/*TODO*///			/* reduce the resolution down to 8 bits for scaling */
/*TODO*///			xweight >>= FRAC_BITS - 8;
/*TODO*///			yweight >>= FRAC_BITS - 8;
/*TODO*///
/*TODO*///			/* fetch the pixels */
/*TODO*///			pix1 = *((UINT32 *)piece->rawbitmap->base + (sy + dy + 0) * piece->rawbitmap->rowpixels + (sx + dx + 0));
/*TODO*///			pix2 = *((UINT32 *)piece->rawbitmap->base + (sy + dy + 0) * piece->rawbitmap->rowpixels + (sx + dx + 1));
/*TODO*///			pix3 = *((UINT32 *)piece->rawbitmap->base + (sy + dy + 1) * piece->rawbitmap->rowpixels + (sx + dx + 0));
/*TODO*///			pix4 = *((UINT32 *)piece->rawbitmap->base + (sy + dy + 1) * piece->rawbitmap->rowpixels + (sx + dx + 1));
/*TODO*///
/*TODO*///			/* blend red */
/*TODO*///			r = xweight * yweight * RGB_RED(pix1);
/*TODO*///			r += (0x100 - xweight) * yweight * RGB_RED(pix2);
/*TODO*///			r += xweight * (0x100 - yweight) * RGB_RED(pix3);
/*TODO*///			r += (0x100 - xweight) * (0x100 - yweight) * RGB_RED(pix4);
/*TODO*///			r >>= 16;
/*TODO*///			r = (r * global_brightness) >> 16;
/*TODO*///			if (r > 0xff) r = 0xff;
/*TODO*///
/*TODO*///			/* blend green */
/*TODO*///			g = xweight * yweight * RGB_GREEN(pix1);
/*TODO*///			g += (0x100 - xweight) * yweight * RGB_GREEN(pix2);
/*TODO*///			g += xweight * (0x100 - yweight) * RGB_GREEN(pix3);
/*TODO*///			g += (0x100 - xweight) * (0x100 - yweight) * RGB_GREEN(pix4);
/*TODO*///			g >>= 16;
/*TODO*///			g = (g * global_brightness) >> 16;
/*TODO*///			if (g > 0xff) g = 0xff;
/*TODO*///
/*TODO*///			/* blend blue */
/*TODO*///			b = xweight * yweight * RGB_BLUE(pix1);
/*TODO*///			b += (0x100 - xweight) * yweight * RGB_BLUE(pix2);
/*TODO*///			b += xweight * (0x100 - yweight) * RGB_BLUE(pix3);
/*TODO*///			b += (0x100 - xweight) * (0x100 - yweight) * RGB_BLUE(pix4);
/*TODO*///			b >>= 16;
/*TODO*///			b = (b * global_brightness) >> 16;
/*TODO*///			if (b > 0xff) b = 0xff;
/*TODO*///
/*TODO*///			/* blend alpha */
/*TODO*///			a = xweight * yweight * RGB_ALPHA(pix1);
/*TODO*///			a += (0x100 - xweight) * yweight * RGB_ALPHA(pix2);
/*TODO*///			a += xweight * (0x100 - yweight) * RGB_ALPHA(pix3);
/*TODO*///			a += (0x100 - xweight) * (0x100 - yweight) * RGB_ALPHA(pix4);
/*TODO*///			a >>= 16;
/*TODO*///			a = (a * global_alpha) >> 16;
/*TODO*///			if (a > 0xff) a = 0xff;
/*TODO*///
/*TODO*///			/* compute the two pixel types */
/*TODO*///			*((UINT32 *)piece->prebitmap->base + y * piece->prebitmap->rowpixels + x) = compute_pre_pixel(a,r,g,b);
/*TODO*///			*((UINT32 *)piece->yrgbbitmap->base + y * piece->yrgbbitmap->rowpixels + x) = compute_yrgb_pixel(a,r,g,b);
/*TODO*///
/*TODO*///			/* advance pointers */
/*TODO*///			sxfrac += sxstep;
/*TODO*///			sx += sxfrac >> FRAC_BITS;
/*TODO*///			sxfrac &= FRAC_MASK;
/*TODO*///
/*TODO*///			/* look for state changes */
/*TODO*///			newstate = (a != 0);
/*TODO*///			if (newstate != prevstate)
/*TODO*///			{
/*TODO*///				prevstate = newstate;
/*TODO*///
/*TODO*///				/* if starting a new run of non-transparent pixels, remember the start point */
/*TODO*///				if (newstate)
/*TODO*///					statex = x;
/*TODO*///
/*TODO*///				/* otherwise, add the current run */
/*TODO*///				else
/*TODO*///					add_range_to_hint(piece->scanlinehint, y, statex, x - 1);
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		/* add the final range */
/*TODO*///		if (prevstate)
/*TODO*///			add_range_to_hint(piece->scanlinehint, y, statex, x - 1);
/*TODO*///
/*TODO*///		/* advance pointers */
/*TODO*///		syfrac += systep;
/*TODO*///		sy += syfrac >> FRAC_BITS;
/*TODO*///		syfrac &= FRAC_MASK;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* guess it worked! */
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	trim_bitmap - remove any transparent borders
/*TODO*///	from a scaled image
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void trim_bitmap(struct artwork_piece *piece)
/*TODO*///{
/*TODO*///	UINT32 *hintbase = piece->scanlinehint;
/*TODO*///	int top, bottom, left, right;
/*TODO*///	int x, y, height, width;
/*TODO*///
/*TODO*///	/* skip if no bitmap */
/*TODO*///	if (!piece->rawbitmap)
/*TODO*///		return;
/*TODO*///
/*TODO*///	/* don't trim overlay bitmaps */
/*TODO*///	if (piece->layer == LAYER_OVERLAY)
/*TODO*///		return;
/*TODO*///
/*TODO*///	/* scan from the top down, looking for empty rows */
/*TODO*///	height = piece->prebitmap->height;
/*TODO*///	width = piece->prebitmap->width;
/*TODO*///	for (top = 0; top < height; top++)
/*TODO*///		if (hintbase[top * MAX_HINTS_PER_SCANLINE] != 0)
/*TODO*///			break;
/*TODO*///
/*TODO*///	/* scan from the bottom up, looking for empty rows */
/*TODO*///	for (bottom = height - 1; bottom >= top; bottom--)
/*TODO*///		if (hintbase[bottom * MAX_HINTS_PER_SCANLINE] != 0)
/*TODO*///			break;
/*TODO*///
/*TODO*///	/* now find the min/max */
/*TODO*///	left = width - 1;
/*TODO*///	right = 0;
/*TODO*///	for (y = top; y <= bottom; y++)
/*TODO*///	{
/*TODO*///		const UINT32 *hintdata = &hintbase[y * MAX_HINTS_PER_SCANLINE];
/*TODO*///
/*TODO*///		/* check the minimum against the left */
/*TODO*///		if (hintdata[0] && (hintdata[0] >> 16) < left)
/*TODO*///			left = hintdata[0] >> 16;
/*TODO*///
/*TODO*///		/* find the maximum */
/*TODO*///		for (x = 0; x < MAX_HINTS_PER_SCANLINE; x++)
/*TODO*///			if (hintdata[x] && (hintdata[x] & 0xffff) > right)
/*TODO*///				right = hintdata[x] & 0xffff;
/*TODO*///	}
/*TODO*///
/*TODO*///	logerror("Trimming bitmap from (%d,%d)-(%d,%d) to (%d,%d)-(%d,%d)\n",
/*TODO*///			piece->bounds.min_x, piece->bounds.min_y, piece->bounds.max_x, piece->bounds.max_y,
/*TODO*///			piece->bounds.min_x + left, piece->bounds.min_y + top, piece->bounds.min_x + right, piece->bounds.min_y + bottom);
/*TODO*///
/*TODO*///	/* skip if all is normal */
/*TODO*///	if (left == 0 && top == 0 && right == width - 1 && bottom == height - 1)
/*TODO*///		return;
/*TODO*///
/*TODO*///	/* now shift the bitmap data */
/*TODO*///	for (y = top; y <= bottom; y++)
/*TODO*///	{
/*TODO*///		UINT32 *hintsrc = &hintbase[y * MAX_HINTS_PER_SCANLINE];
/*TODO*///		UINT32 *hintdst = &hintbase[(y - top) * MAX_HINTS_PER_SCANLINE];
/*TODO*///		UINT32 *dst1 = (UINT32 *)piece->prebitmap->base + (y - top) * piece->prebitmap->rowpixels;
/*TODO*///		UINT32 *dst2 = (UINT32 *)piece->yrgbbitmap->base + (y - top) * piece->yrgbbitmap->rowpixels;
/*TODO*///		UINT32 *src1 = (UINT32 *)piece->prebitmap->base + y * piece->prebitmap->rowpixels + left;
/*TODO*///		UINT32 *src2 = (UINT32 *)piece->yrgbbitmap->base + y * piece->yrgbbitmap->rowpixels + left;
/*TODO*///
/*TODO*///		memmove(dst1, src1, (right - left + 1) * sizeof(UINT32));
/*TODO*///		memmove(dst2, src2, (right - left + 1) * sizeof(UINT32));
/*TODO*///
/*TODO*///		/* adjust the hints */
/*TODO*///		for (x = 0; x < MAX_HINTS_PER_SCANLINE; x++)
/*TODO*///		{
/*TODO*///			UINT32 data = hintsrc[x];
/*TODO*///			if (data)
/*TODO*///				data -= (left << 16) | left;
/*TODO*///			hintdst[x] = data;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* and adjust the info */
/*TODO*///	piece->bounds.max_x = piece->bounds.min_x + right;
/*TODO*///	piece->bounds.min_x += left;
/*TODO*///	piece->bounds.max_y = piece->bounds.min_y + bottom;
/*TODO*///	piece->bounds.min_y += top;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///#if 0
/*TODO*///#pragma mark -
/*TODO*///#pragma mark PIECE LIST MANAGEMENT
/*TODO*///#endif
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	create_new_piece - allocate a new piece
/*TODO*///	entry
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static struct artwork_piece *create_new_piece(const char *tag)
/*TODO*///{
/*TODO*///	/* allocate a new piece */
/*TODO*///	struct artwork_piece *newpiece = auto_malloc(sizeof(struct artwork_piece));
/*TODO*///	if (!newpiece)
/*TODO*///		return NULL;
/*TODO*///	num_pieces++;
/*TODO*///
/*TODO*///	/* initialize to default values */
/*TODO*///	memset(newpiece, 0, sizeof(*newpiece));
/*TODO*///	newpiece->layer = LAYER_UNKNOWN;
/*TODO*///	newpiece->has_alpha = 0;
/*TODO*///	newpiece->priority = 0;
/*TODO*///	newpiece->alpha = 1.0;
/*TODO*///	newpiece->brightness = 1.0;
/*TODO*///	newpiece->filename = NULL;
/*TODO*///	newpiece->alpha_filename = NULL;
/*TODO*///	newpiece->intersects_game = 0;
/*TODO*///	newpiece->visible = 1;
/*TODO*///
/*TODO*///	/* allocate space for the filename */
/*TODO*///	newpiece->tag = auto_malloc(strlen(tag) + 1);
/*TODO*///	if (!newpiece->tag)
/*TODO*///		return NULL;
/*TODO*///	strcpy(newpiece->tag, tag);
/*TODO*///
/*TODO*///	/* link into the list */
/*TODO*///	newpiece->next = artwork_list;
/*TODO*///	artwork_list = newpiece;
/*TODO*///	return newpiece;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	artwork_sort_compare - qsort compare function
/*TODO*///	to sort pieces by priority
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int CLIB_DECL artwork_sort_compare(const void *item1, const void *item2)
/*TODO*///{
/*TODO*///	const struct artwork_piece *piece1 = *((const struct artwork_piece **)item1);
/*TODO*///	const struct artwork_piece *piece2 = *((const struct artwork_piece **)item2);
/*TODO*///	if (piece1->layer < piece2->layer)
/*TODO*///		return -1;
/*TODO*///	else if (piece1->layer > piece2->layer)
/*TODO*///		return 1;
/*TODO*///	else if (piece1->priority < piece2->priority)
/*TODO*///		return -1;
/*TODO*///	else if (piece1->priority > piece2->priority)
/*TODO*///		return 1;
/*TODO*///	else
/*TODO*///		return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	sort_pieces - sort the pieces by priority
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void sort_pieces(void)
/*TODO*///{
/*TODO*///	struct artwork_piece *array[MAX_PIECES];
/*TODO*///	struct artwork_piece *piece;
/*TODO*///	int i = 0;
/*TODO*///
/*TODO*///	/* copy the list into the array, filtering as we go */
/*TODO*///	for (piece = artwork_list; piece; piece = piece->next)
/*TODO*///	{
/*TODO*///		switch (piece->layer)
/*TODO*///		{
/*TODO*///			case LAYER_BACKDROP:
/*TODO*///				if (options.use_artwork & ARTWORK_USE_BACKDROPS)
/*TODO*///					array[i++] = piece;
/*TODO*///				break;
/*TODO*///
/*TODO*///			case LAYER_OVERLAY:
/*TODO*///				if (options.use_artwork & ARTWORK_USE_OVERLAYS)
/*TODO*///					array[i++] = piece;
/*TODO*///				break;
/*TODO*///
/*TODO*///			default:
/*TODO*///				if (options.use_artwork & ARTWORK_USE_BEZELS)
/*TODO*///					array[i++] = piece;
/*TODO*///				break;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	num_pieces = i;
/*TODO*///	if (num_pieces == 0)
/*TODO*///	{
/*TODO*///		artwork_list = NULL;
/*TODO*///		return;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* now sort it */
/*TODO*///	if (num_pieces > 1)
/*TODO*///		qsort(array, num_pieces, sizeof(array[0]), artwork_sort_compare);
/*TODO*///
/*TODO*///	/* now reassemble the list */
/*TODO*///	artwork_list = piece = array[0];
/*TODO*///	for (i = 1; i < num_pieces; i++)
/*TODO*///	{
/*TODO*///		piece->next = array[i];
/*TODO*///		piece = piece->next;
/*TODO*///	}
/*TODO*///	piece->next = NULL;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	validate_pieces - make sure we got valid data
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int validate_pieces(void)
/*TODO*///{
/*TODO*///	struct artwork_piece *piece;
/*TODO*///
/*TODO*///	/* verify each one */
/*TODO*///	for (piece = artwork_list; piece; piece = piece->next)
/*TODO*///	{
/*TODO*///		/* make sure we have a filename */
/*TODO*///		if ((!piece->filename || strlen(piece->filename) == 0) && !piece->rawbitmap)
/*TODO*///		{
/*TODO*///			logerror("Artwork piece '%s' has no file!\n", piece->tag);
/*TODO*///			return 0;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* make sure we have a layer */
/*TODO*///		if (piece->layer == LAYER_UNKNOWN)
/*TODO*///		{
/*TODO*///			logerror("Artwork piece '%s' has no layer!\n", piece->tag);
/*TODO*///			return 0;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* make sure we have a position */
/*TODO*///		if (piece->left == 0 && piece->right == 0)
/*TODO*///		{
/*TODO*///			logerror("Artwork piece '%s' has no position!\n", piece->tag);
/*TODO*///			return 0;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* make sure the position is valid */
/*TODO*///		if (piece->left >= piece->right || piece->top >= piece->bottom)
/*TODO*///		{
/*TODO*///			logerror("Artwork piece '%s' has invalid position data!\n", piece->tag);
/*TODO*///			return 0;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///#if 0
/*TODO*///#pragma mark -
/*TODO*///#pragma mark OVERLAY GENERATION
/*TODO*///#endif
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	generate_rect_piece - generate a rectangular
/*TODO*///	overlay piece
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int generate_rect_piece(struct artwork_piece *piece, const struct overlay_piece *data, int width, int height)
/*TODO*///{
/*TODO*///	int gfxwidth, gfxheight;
/*TODO*///
/*TODO*///	/* extract coordinates */
/*TODO*///	piece->top = data->top;
/*TODO*///	piece->left = data->left;
/*TODO*///	piece->bottom = data->bottom;
/*TODO*///	piece->right = data->right;
/*TODO*///
/*TODO*///	/* convert from pixel coordinates if necessary */
/*TODO*///	if (fabs(piece->left) > 4.0 || fabs(piece->right) > 4.0 ||
/*TODO*///		fabs(piece->top) > 4.0 || fabs(piece->bottom) > 4.0)
/*TODO*///	{
/*TODO*///		piece->left /= (double)width;
/*TODO*///		piece->right /= (double)width;
/*TODO*///		piece->top /= (double)height;
/*TODO*///		piece->bottom /= (double)height;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* compute the effective width/height */
/*TODO*///	gfxwidth = (int)((piece->right - piece->left) * (double)width * 2.0 + 0.5);
/*TODO*///	gfxheight = (int)((piece->bottom - piece->top) * (double)height * 2.0 + 0.5);
/*TODO*///
/*TODO*///	/* allocate a source bitmap 2x the game bitmap's size */
/*TODO*///	piece->rawbitmap = auto_bitmap_alloc_depth(gfxwidth, gfxheight, 32);
/*TODO*///	if (!piece->rawbitmap)
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	/* fill the bitmap */
/*TODO*///	fillbitmap(piece->rawbitmap, data->color, NULL);
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	generate_disk_piece - generate a disk-shaped
/*TODO*///	overlay piece
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void render_disk(struct mame_bitmap *bitmap, int r, UINT32 color)
/*TODO*///{
/*TODO*///	int xc = bitmap->width / 2, yc = bitmap->height / 2;
/*TODO*///	int x = 0, twox = 0;
/*TODO*///	int y = r;
/*TODO*///	int twoy = r+r;
/*TODO*///	int p = 1 - r;
/*TODO*///	int i;
/*TODO*///
/*TODO*///	while (x < y)
/*TODO*///	{
/*TODO*///		x++;
/*TODO*///		twox +=2;
/*TODO*///		if (p < 0)
/*TODO*///			p += twox + 1;
/*TODO*///		else
/*TODO*///		{
/*TODO*///			y--;
/*TODO*///			twoy -= 2;
/*TODO*///			p += twox - twoy + 1;
/*TODO*///		}
/*TODO*///
/*TODO*///		for (i = 0; i < twox; i++)
/*TODO*///		{
/*TODO*///			plot_pixel(bitmap, xc-x+i, yc-y, color);
/*TODO*///			plot_pixel(bitmap, xc-x+i, yc+y-1, color);
/*TODO*///		}
/*TODO*///
/*TODO*///		for (i = 0; i < twoy; i++)
/*TODO*///		{
/*TODO*///			plot_pixel(bitmap, xc-y+i, yc-x, color);
/*TODO*///			plot_pixel(bitmap, xc-y+i, yc+x-1, color);
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static int generate_disk_piece(struct artwork_piece *piece, const struct overlay_piece *data, int width, int height)
/*TODO*///{
/*TODO*///	double x = data->left, y = data->top, r = data->right;
/*TODO*///	struct rectangle temprect;
/*TODO*///	int gfxwidth, gfxradius;
/*TODO*///
/*TODO*///	/* convert from pixel coordinates if necessary */
/*TODO*///	if (fabs(x) > 4.0 || fabs(y) > 4.0 || fabs(r) > 4.0)
/*TODO*///	{
/*TODO*///		x /= (double)width;
/*TODO*///		y /= (double)height;
/*TODO*///		r /= (double)width;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* generate coordinates */
/*TODO*///	piece->top = y - r * (double)width / (double)height;
/*TODO*///	piece->left = x - r;
/*TODO*///	piece->bottom = y + r * (double)width / (double)height;
/*TODO*///	piece->right = x + r;
/*TODO*///
/*TODO*///	/* compute the effective width/height */
/*TODO*///	gfxwidth = (int)((piece->right - piece->left) * (double)width * 2.0 + 0.5);
/*TODO*///	gfxradius = (int)(r * (double)width * 2.0 + 0.5);
/*TODO*///
/*TODO*///	/* allocate a source bitmap 2x the game bitmap's size */
/*TODO*///	piece->rawbitmap = auto_bitmap_alloc_depth(gfxwidth, gfxwidth, 32);
/*TODO*///	if (!piece->rawbitmap)
/*TODO*///		return 0;
/*TODO*///
/*TODO*///	/* fill the bitmap with white */
/*TODO*///	temprect.min_x = temprect.min_y = 0;
/*TODO*///	temprect.max_x = piece->rawbitmap->width - 1;
/*TODO*///	temprect.max_y = piece->rawbitmap->height - 1;
/*TODO*///	erase_rect(piece->rawbitmap, &temprect, MAKE_ARGB(0,0xff,0xff,0xff));
/*TODO*///
/*TODO*///	/* now render the disk */
/*TODO*///	render_disk(piece->rawbitmap, gfxradius, data->color);
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	generate_overlay - generate an overlay with
/*TODO*///	the given pieces
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int generate_overlay(const struct overlay_piece *list, int width, int height)
/*TODO*///{
/*TODO*///	struct artwork_piece *piece;
/*TODO*///	int priority = 0;
/*TODO*///
/*TODO*///	/* loop until done */
/*TODO*///	while (list->type != OVERLAY_TYPE_END)
/*TODO*///	{
/*TODO*///		/* first create a new piece to use */
/*TODO*///		piece = create_new_piece(OVERLAY_TAG);
/*TODO*///		if (!piece)
/*TODO*///			return 0;
/*TODO*///
/*TODO*///		/* fill in the basics */
/*TODO*///		piece->has_alpha = 1;
/*TODO*///		piece->layer = LAYER_OVERLAY;
/*TODO*///		piece->priority = priority++;
/*TODO*///		piece->blendflags = list->type & OVERLAY_FLAG_MASK;
/*TODO*///
/*TODO*///		/* switch off the type */
/*TODO*///		switch (list->type & ~OVERLAY_FLAG_MASK)
/*TODO*///		{
/*TODO*///			case OVERLAY_TYPE_RECTANGLE:
/*TODO*///				if (!generate_rect_piece(piece, list, width, height))
/*TODO*///					return 0;
/*TODO*///				break;
/*TODO*///
/*TODO*///			case OVERLAY_TYPE_DISK:
/*TODO*///				if (!generate_disk_piece(piece, list, width, height))
/*TODO*///					return 0;
/*TODO*///				break;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* next */
/*TODO*///		list++;
/*TODO*///	}
/*TODO*///
/*TODO*///	return 1;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///#if 0
/*TODO*///#pragma mark -
/*TODO*///#pragma mark ART FILE PARSING
/*TODO*///#endif
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	strip_space - strip leading/trailing spaces
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static char *strip_space(char *string)
/*TODO*///{
/*TODO*///	char *start, *end;
/*TODO*///
/*TODO*///	/* skip over leading space */
/*TODO*///	for (start = string; *start && isspace(*start); start++) ;
/*TODO*///
/*TODO*///	/* NULL terminate over trailing space */
/*TODO*///	for (end = start + strlen(start) - 1; end > start && isspace(*end); end--) *end = 0;
/*TODO*///	return start;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	parse_tag_value - parse a tag/value pair
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int parse_tag_value(struct artwork_piece *piece, const char *tag, const char *value)
/*TODO*///{
/*TODO*///	/* handle the various tags */
/*TODO*///	if (!strcmp(tag, "layer"))
/*TODO*///	{
/*TODO*///		if (!strcmp(value, "backdrop"))
/*TODO*///			piece->layer = LAYER_BACKDROP;
/*TODO*///		else if (!strcmp(value, "overlay"))
/*TODO*///			piece->layer = LAYER_OVERLAY;
/*TODO*///		else if (!strcmp(value, "bezel"))
/*TODO*///			piece->layer = LAYER_BEZEL;
/*TODO*///		else if (!strcmp(value, "marquee"))
/*TODO*///			piece->layer = LAYER_MARQUEE;
/*TODO*///		else if (!strcmp(value, "panel"))
/*TODO*///			piece->layer = LAYER_PANEL;
/*TODO*///		else if (!strcmp(value, "side"))
/*TODO*///			piece->layer = LAYER_SIDE;
/*TODO*///		else if (!strcmp(value, "flyer"))
/*TODO*///			piece->layer = LAYER_FLYER;
/*TODO*///		else
/*TODO*///			return 0;
/*TODO*///		return 1;
/*TODO*///	}
/*TODO*///	else if (!strcmp(tag, "priority"))
/*TODO*///	{
/*TODO*///		return (sscanf(value, "%d", &piece->priority) == 1);
/*TODO*///	}
/*TODO*///	else if (!strcmp(tag, "visible"))
/*TODO*///	{
/*TODO*///		return (sscanf(value, "%d", &piece->visible) == 1);
/*TODO*///	}
/*TODO*///	else if (!strcmp(tag, "alpha"))
/*TODO*///	{
/*TODO*///		return (sscanf(value, "%f", &piece->alpha) == 1);
/*TODO*///	}
/*TODO*///	else if (!strcmp(tag, "brightness"))
/*TODO*///	{
/*TODO*///		return (sscanf(value, "%f", &piece->brightness) == 1);
/*TODO*///	}
/*TODO*///	else if (!strcmp(tag, "position"))
/*TODO*///	{
/*TODO*///		return (sscanf(value, "%f,%f,%f,%f", &piece->left, &piece->top, &piece->right, &piece->bottom) == 4);
/*TODO*///	}
/*TODO*///	else if (!strcmp(tag, "file"))
/*TODO*///	{
/*TODO*///		piece->filename = auto_malloc(strlen(value) + 1);
/*TODO*///		if (piece->filename)
/*TODO*///			strcpy(piece->filename, value);
/*TODO*///		return (piece->filename != NULL);
/*TODO*///	}
/*TODO*///	else if (!strcmp(tag, "alphafile"))
/*TODO*///	{
/*TODO*///		piece->alpha_filename = auto_malloc(strlen(value) + 1);
/*TODO*///		if (piece->alpha_filename)
/*TODO*///			strcpy(piece->alpha_filename, value);
/*TODO*///		return (piece->alpha_filename != NULL);
/*TODO*///	}
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	parse_art_file - parse a .art file
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int parse_art_file(mame_file *file)
/*TODO*///{
/*TODO*///	struct artwork_piece *current = NULL;
/*TODO*///	char *tag, *value, *p;
/*TODO*///	char buffer[1000];
/*TODO*///
/*TODO*///	/* loop until we run out of lines */
/*TODO*///	while (mame_fgets(buffer, sizeof(buffer), file))
/*TODO*///	{
/*TODO*///		/* strip off any comments */
/*TODO*///		p = strstr(buffer, "//");
/*TODO*///		if (p)
/*TODO*///			*p = 0;
/*TODO*///
/*TODO*///		/* strip off leading/trailing spaces */
/*TODO*///		tag = strip_space(buffer);
/*TODO*///
/*TODO*///		/* anything left? */
/*TODO*///		if (tag[0] == 0)
/*TODO*///			continue;
/*TODO*///
/*TODO*///		/* is this the start of a new entry? */
/*TODO*///		if (tag[strlen(tag) - 1] == ':')
/*TODO*///		{
/*TODO*///			/* strip the space off the rest */
/*TODO*///			tag[strlen(tag) - 1] = 0;
/*TODO*///			tag = strip_space(tag);
/*TODO*///
/*TODO*///			/* create an entry for the new piece */
/*TODO*///			current = create_new_piece(tag);
/*TODO*///			if (!current)
/*TODO*///				return 0;
/*TODO*///			continue;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* is this a tag/value pair? */
/*TODO*///		value = strchr(tag, '=');
/*TODO*///		if (value)
/*TODO*///		{
/*TODO*///			/* strip spaces off of both parts */
/*TODO*///			*value++ = 0;
/*TODO*///			tag = strip_space(tag);
/*TODO*///			value = strip_space(value);
/*TODO*///
/*TODO*///			/* convert both strings to lowercase */
/*TODO*///			for (p = tag; *p; p++) *p = tolower(*p);
/*TODO*///			for (p = value; *p; p++) *p = tolower(*p);
/*TODO*///
/*TODO*///			/* now parse the result */
/*TODO*///			if (current && parse_tag_value(current, tag, value))
/*TODO*///				continue;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* what the heck is it? */
/*TODO*///		logerror("Invalid line in .ART file:\n%s\n", buffer);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* validate the artwork */
/*TODO*///	return validate_pieces();
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///#if 0
/*TODO*///#pragma mark -
/*TODO*///#pragma mark MISC UTILITIES
/*TODO*///#endif
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	compute_rgb_components - compute the RGB
/*TODO*///	components
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static int compute_rgb_components(int depth, UINT32 rgb_components[3], UINT32 rgb32_components[3])
/*TODO*///{
/*TODO*///	UINT32 temp;
/*TODO*///	int r, g, b;
/*TODO*///
/*TODO*///	/* first convert the RGB components we got back into shifts */
/*TODO*///	for (temp = rgb32_components[0], rshift = 0; !(temp & 1); temp >>= 1)
/*TODO*///		rshift++;
/*TODO*///	for (temp = rgb32_components[1], gshift = 0; !(temp & 1); temp >>= 1)
/*TODO*///		gshift++;
/*TODO*///	for (temp = rgb32_components[2], bshift = 0; !(temp & 1); temp >>= 1)
/*TODO*///		bshift++;
/*TODO*///
/*TODO*///	/* compute the alpha shift for the leftover byte */
/*TODO*///	nonalpha_mask = rgb32_components[0] | rgb32_components[1] | rgb32_components[2];
/*TODO*///	temp = ~nonalpha_mask;
/*TODO*///	for (ashift = 0; !(temp & 1); temp >>= 1)
/*TODO*///		ashift++;
/*TODO*///
/*TODO*///	/* compute a transparent color; this is in the premultiplied space, so alpha is inverted */
/*TODO*///	transparent_color = ASSEMBLE_ARGB(0xff,0x00,0x00,0x00);
/*TODO*///
/*TODO*///	/* allocate a palette lookup */
/*TODO*///	palette_lookup = auto_malloc(65536 * sizeof(palette_lookup[0]));
/*TODO*///	if (!palette_lookup)
/*TODO*///		return 1;
/*TODO*///
/*TODO*///	/* switch off the depth */
/*TODO*///	switch (depth)
/*TODO*///	{
/*TODO*///		case 16:
/*TODO*///			/* do nothing */
/*TODO*///			break;
/*TODO*///
/*TODO*///		case 32:
/*TODO*///			/* copy original components */
/*TODO*///			memcpy(rgb_components, rgb32_components, sizeof(rgb_components));
/*TODO*///			break;
/*TODO*///
/*TODO*///		case 15:
/*TODO*///			/* make up components */
/*TODO*///			rgb_components[0] = 0x7c00;
/*TODO*///			rgb_components[1] = 0x03e0;
/*TODO*///			rgb_components[2] = 0x001f;
/*TODO*///
/*TODO*///			/* now build up the palette */
/*TODO*///			for (r = 0; r < 32; r++)
/*TODO*///				for (g = 0; g < 32; g++)
/*TODO*///					for (b = 0; b < 32; b++)
/*TODO*///					{
/*TODO*///						int rr = (r << 3) | (r >> 2);
/*TODO*///						int gg = (g << 3) | (g >> 2);
/*TODO*///						int bb = (b << 3) | (b >> 2);
/*TODO*///						palette_lookup[(r << 10) | (g << 5) | b] = ASSEMBLE_ARGB(0, rr, gg, bb);
/*TODO*///					}
/*TODO*///			break;
/*TODO*///	}
/*TODO*///
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////*-------------------------------------------------
/*TODO*///	add_range_to_hint - add a given range to the
/*TODO*///	hint record for the specified scanline
/*TODO*///-------------------------------------------------*/
/*TODO*///
/*TODO*///static void add_range_to_hint(UINT32 *hintbase, int scanline, int startx, int endx)
/*TODO*///{
/*TODO*///	int closestdiff = 100000, closestindex = -1;
/*TODO*///	int i;
/*TODO*///
/*TODO*///	/* first address the correct hint */
/*TODO*///	hintbase += scanline * MAX_HINTS_PER_SCANLINE;
/*TODO*///
/*TODO*///	/* first, look for an intersection */
/*TODO*///	for (i = 0; i < MAX_HINTS_PER_SCANLINE; i++)
/*TODO*///	{
/*TODO*///		UINT32 hint = hintbase[i];
/*TODO*///		int hintstart = hint >> 16;
/*TODO*///		int hintend = hint & 0xffff;
/*TODO*///		int diff;
/*TODO*///
/*TODO*///		/* stop if we hit a 0 */
/*TODO*///		if (hint == 0)
/*TODO*///			break;
/*TODO*///
/*TODO*///		/* do we intersect? */
/*TODO*///		if (startx <= hintend && endx >= hintstart)
/*TODO*///		{
/*TODO*///			closestindex = i;
/*TODO*///			goto intersect;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* see how close we are to this entry */
/*TODO*///		if (hintend < startx)
/*TODO*///			diff = startx - hintend;
/*TODO*///		else
/*TODO*///			diff = hintstart - endx;
/*TODO*///
/*TODO*///		/* if this is the closest, remember it */
/*TODO*///		if (diff < closestdiff)
/*TODO*///		{
/*TODO*///			closestdiff = diff;
/*TODO*///			closestindex = i;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* okay, we didn't find an intersection; do we have room to add? */
/*TODO*///	if (i < MAX_HINTS_PER_SCANLINE)
/*TODO*///	{
/*TODO*///		UINT32 newhint = (startx << 16) | endx;
/*TODO*///
/*TODO*///		/* if there's nothing there yet, just assign to the first entry */
/*TODO*///		if (i == 0)
/*TODO*///			hintbase[0] = newhint;
/*TODO*///
/*TODO*///		/* otherwise, shuffle the existing entries to make room for us */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			/* determine our new index */
/*TODO*///			i = closestindex;
/*TODO*///			if (hintbase[i] < newhint)
/*TODO*///				i++;
/*TODO*///
/*TODO*///			/* shift things over */
/*TODO*///			if (i < MAX_HINTS_PER_SCANLINE - 1)
/*TODO*///				memmove(&hintbase[i+1], &hintbase[i], (MAX_HINTS_PER_SCANLINE - (i+1)) * sizeof(hintbase[0]));
/*TODO*///			hintbase[i] = newhint;
/*TODO*///		}
/*TODO*///		return;
/*TODO*///	}
/*TODO*///
/*TODO*///intersect:
/*TODO*///	/* intersect with the closest entry */
/*TODO*///	{
/*TODO*///		UINT32 hint = hintbase[closestindex];
/*TODO*///		int hintstart = hint >> 16;
/*TODO*///		int hintend = hint & 0xffff;
/*TODO*///
/*TODO*///		/* compute the intersection */
/*TODO*///		if (startx < hintstart)
/*TODO*///			hintstart = startx;
/*TODO*///		if (endx > hintend)
/*TODO*///			hintend = endx;
/*TODO*///		hintbase[closestindex] = (hintstart << 16) | hintend;
/*TODO*///	}
/*TODO*///}
/*TODO*///    
}
