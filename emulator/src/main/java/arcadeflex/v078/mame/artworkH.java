/**
 * ported to v0.78
 */
package arcadeflex.v078.mame;

public class artworkH {
    /*TODO*////*********************************************************************
/*TODO*///
/*TODO*///	artwork.h
/*TODO*///
/*TODO*///	Second generation artwork implementation.
/*TODO*///
/*TODO*///*********************************************************************/
/*TODO*///
/*TODO*///#ifndef ARTWORK_H
/*TODO*///#define ARTWORK_H
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Constants
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*////* the various types of built-in overlay primitives */
/*TODO*///#define OVERLAY_TYPE_END			0
/*TODO*///#define OVERLAY_TYPE_RECTANGLE		1
/*TODO*///#define OVERLAY_TYPE_DISK			2
/*TODO*///
/*TODO*////* flags for the primitives */
/*TODO*///#define OVERLAY_FLAG_NOBLEND		0x10
/*TODO*///#define OVERLAY_FLAG_MASK			(OVERLAY_FLAG_NOBLEND)
/*TODO*///
/*TODO*////* the tag assigned to all the internal overlays */
/*TODO*///#define OVERLAY_TAG					"overlay"
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Macros
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///#define OVERLAY_START(name)	\
/*TODO*///	static const struct overlay_piece name[] = {
/*TODO*///
/*TODO*///#define OVERLAY_END \
/*TODO*///	{ OVERLAY_TYPE_END } };
/*TODO*///
/*TODO*///#define OVERLAY_RECT(l,t,r,b,c) \
/*TODO*///	{ OVERLAY_TYPE_RECTANGLE, (c), (l), (t), (r), (b) },
/*TODO*///
/*TODO*///#define OVERLAY_DISK(x,y,r,c) \
/*TODO*///	{ OVERLAY_TYPE_DISK, (c), (x), (y), (r), 0 },
/*TODO*///
/*TODO*///#define OVERLAY_DISK_NOBLEND(x,y,r,c) \
/*TODO*///	{ OVERLAY_TYPE_DISK | OVERLAY_FLAG_NOBLEND, (c), (x), (y), (r), 0 },
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Type definitions
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///struct artwork_callbacks
/*TODO*///{
/*TODO*///	/* provides an additional way to activate artwork system; can be NULL */
/*TODO*///	int (*activate_artwork)(struct osd_create_params *params);
/*TODO*///
/*TODO*///	/* function to load an artwork file for a particular driver */
/*TODO*///	mame_file *(*load_artwork)(const struct GameDriver **driver);
/*TODO*///};
/*TODO*///
/*TODO*///struct overlay_piece
/*TODO*///{
/*TODO*///	UINT8 type;
/*TODO*///	rgb_t color;
/*TODO*///	float left, top, right, bottom;
/*TODO*///};
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*////***************************************************************************
/*TODO*///
/*TODO*///	Prototypes
/*TODO*///
/*TODO*///***************************************************************************/
/*TODO*///
/*TODO*///int artwork_create_display(struct osd_create_params *params, UINT32 *rgb_components, const struct artwork_callbacks *callbacks);
/*TODO*///void artwork_update_video_and_audio(struct mame_display *display);
/*TODO*///void artwork_override_screenshot_params(struct mame_bitmap **bitmap, struct rectangle *rect, UINT32 *rgb_components);
/*TODO*///
/*TODO*///struct mame_bitmap *artwork_get_ui_bitmap(void);
/*TODO*///void artwork_mark_ui_dirty(int minx, int miny, int maxx, int maxy);
/*TODO*///void artwork_get_screensize(int *width, int *height);
/*TODO*///void artwork_enable(int enable);
/*TODO*///
/*TODO*///void artwork_set_overlay(const struct overlay_piece *overlist);
/*TODO*///void artwork_show(const char *tag, int show);
/*TODO*///
/*TODO*///mame_file *artwork_load_artwork_file(const struct GameDriver **driver);
/*TODO*///
/*TODO*///#endif /* ARTWORK_H */
/*TODO*///
/*TODO*///    
}
