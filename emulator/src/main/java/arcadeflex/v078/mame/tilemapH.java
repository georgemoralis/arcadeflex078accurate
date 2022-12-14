/**
 * ported to v0.78
 */
package arcadeflex.v078.mame;

import arcadeflex.v078.mame.tilemapC.struct_tilemap;

public class tilemapH {

    public static abstract interface GetTileInfoHandlerPtr {

        public abstract void handler(int memory_offset);
    }

    public static abstract interface GetMemoryOffsetHandlerPtr {

        public abstract /*UINT32*/ int handler(int u32_col, int u32_row, int u32_num_cols, int u32_num_rows);
    }

    public static struct_tilemap ALL_TILEMAPS = null;

    public static final int TILEMAP_OPAQUE = 0x00;
    public static final int TILEMAP_TRANSPARENT = 0x01;

    /*TODO*///#define TILEMAP_SPLIT					0x02
/*TODO*///#define TILEMAP_BITMASK					0x04
/*TODO*///#define TILEMAP_TRANSPARENT_COLOR		0x08
/*TODO*///
/*TODO*////* Set transparency_pen to a mask.  pen&mask determines whether each pixel is in front or back half */
/*TODO*///#define TILEMAP_SPLIT_PENBIT			0x10
/*TODO*////*
/*TODO*///	TILEMAP_SPLIT should be used if the pixels from a single tile
/*TODO*///	can appear in more than one plane.
/*TODO*///
/*TODO*///	TILEMAP_BITMASK is used by Namco System1, Namco System2, NamcoNA1/2, Namco NB1
/*TODO*///*/
/*TODO*///
/*TODO*///#define TILEMAP_IGNORE_TRANSPARENCY		0x10
/*TODO*///#define TILEMAP_BACK					0x20
/*TODO*///#define TILEMAP_FRONT					0x40
/*TODO*///#define TILEMAP_ALPHA					0x80
/*TODO*///
/*TODO*////*
/*TODO*///	when rendering a split layer, pass TILEMAP_FRONT or TILEMAP_BACK or'd with the
/*TODO*///	tile_priority value to specify the part to draw.
/*TODO*///
/*TODO*///	when rendering a layer in alpha mode, the priority parameter
/*TODO*///	becomes the alpha parameter (0..255).  Split mode is still
/*TODO*///	available in alpha mode, ignore_transparency isn't.
/*TODO*///*/
/*TODO*///
/*TODO*///extern struct tile_info
/*TODO*///{
/*TODO*///	/*
/*TODO*///		you must set tile_info.pen_data, tile_info.pal_data and tile_info.pen_usage
/*TODO*///		in the callback.  You can use the SET_TILE_INFO() macro below to do this.
/*TODO*///		tile_info.flags and tile_info.priority will be automatically preset to 0,
/*TODO*///		games that don't need them don't need to explicitly set them to 0
/*TODO*///	*/
/*TODO*///	const UINT8 *pen_data;
/*TODO*///	const pen_t *pal_data;
/*TODO*///	UINT32 flags;
/*TODO*///	int skip;
/*TODO*///	UINT32 tile_number;		/* needed for tilemap_mark_gfxdata_dirty */
/*TODO*///	UINT32 pen_usage;		/* TBR */
/*TODO*///	UINT32 priority;		/* tile priority */
/*TODO*///	UINT8 *mask_data;		/* for TILEMAP_BITMASK */
/*TODO*///} tile_info;
/*TODO*///
    public static void SET_TILE_INFO(int GFX, int CODE, int COLOR, int FLAGS) {
        throw new UnsupportedOperationException("Unsupported");
        /*TODO*///	const struct GfxElement *gfx = Machine->gfx[(GFX)]; \
/*TODO*///	int _code = (CODE) % gfx->total_elements; \
/*TODO*///	tile_info.tile_number = _code; \
/*TODO*///	tile_info.pen_data = gfx->gfxdata + _code*gfx->char_modulo; \
/*TODO*///	tile_info.pal_data = &gfx->colortable[gfx->color_granularity * (COLOR)]; \
/*TODO*///	tile_info.pen_usage = gfx->pen_usage?gfx->pen_usage[_code]:0; \
/*TODO*///	tile_info.flags = FLAGS; \
/*TODO*///	if (gfx->flags & GFX_PACKED) tile_info.flags |= TILE_4BPP; \
    }
    /*TODO*///
/*TODO*////* tile flags, set by get_tile_info callback */
/*TODO*////* TILE_IGNORE_TRANSPARENCY is used if you need an opaque tile in a transparent layer. */
/*TODO*///#define TILE_FLIPX					0x01
/*TODO*///#define TILE_FLIPY					0x02
/*TODO*///#define TILE_IGNORE_TRANSPARENCY	0x08
/*TODO*///#define TILE_4BPP					0x10
/*TODO*////*		TILE_SPLIT					0x60 */
/*TODO*///
/*TODO*////* TILE_SPLIT is for use with TILEMAP_SPLIT layers.  It selects transparency type. */
/*TODO*///#define TILE_SPLIT_OFFSET			5
/*TODO*///#define TILE_SPLIT(T)				((T)<<TILE_SPLIT_OFFSET)
/*TODO*///
/*TODO*///#define TILE_FLIPYX(YX)				(YX)
/*TODO*///#define TILE_FLIPXY(XY)				((((XY)>>1)|((XY)<<1))&3)
/*TODO*////*
/*TODO*///	TILE_FLIPYX is a shortcut that can be used by approx 80% of games,
/*TODO*///	since yflip frequently occurs one bit higher than xflip within a
/*TODO*///	tile attributes byte.
/*TODO*///*/
/*TODO*///
/*TODO*///#define TILE_LINE_DISABLED 0x80000000
/*TODO*///
/*TODO*///extern struct mame_bitmap *priority_bitmap;
/*TODO*///
/*TODO*////* don't call these from drivers - they are called from mame.c */
/*TODO*///int tilemap_init( void );
/*TODO*///void tilemap_close( void );
/*TODO*///void tilemap_dispose( struct tilemap *tilemap );
/*TODO*///
/*TODO*///struct tilemap *tilemap_create(
/*TODO*///	void (*tile_get_info)( int memory_offset ),
/*TODO*///	UINT32 (*get_memory_offset)( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows ),
/*TODO*///	int type,
/*TODO*///	int tile_width, int tile_height,
/*TODO*///	int num_cols, int num_rows );
/*TODO*///
/*TODO*///void tilemap_set_transparent_pen( struct tilemap *tilemap, int pen );
/*TODO*///void tilemap_set_transmask( struct tilemap *tilemap, int which, UINT32 fgmask, UINT32 bgmask );
/*TODO*///void tilemap_set_depth( struct tilemap *tilemap, int tile_depth, int tile_granularity );
/*TODO*///
/*TODO*///void tilemap_mark_tile_dirty( struct tilemap *tilemap, int memory_offset );
/*TODO*///void tilemap_mark_all_tiles_dirty( struct tilemap *tilemap );
/*TODO*///void tilemap_mark_gfxdata_dirty( struct tilemap *tilemap, UINT8 *dirty_array ); /* TBA */
/*TODO*///
/*TODO*///void tilemap_set_scroll_rows( struct tilemap *tilemap, int scroll_rows ); /* default: 1 */
/*TODO*///void tilemap_set_scrolldx( struct tilemap *tilemap, int dx, int dx_if_flipped );
/*TODO*///void tilemap_set_scrollx( struct tilemap *tilemap, int row, int value );
/*TODO*///
/*TODO*///void tilemap_set_scroll_cols( struct tilemap *tilemap, int scroll_cols ); /* default: 1 */
/*TODO*///void tilemap_set_scrolldy( struct tilemap *tilemap, int dy, int dy_if_flipped );
/*TODO*///void tilemap_set_scrolly( struct tilemap *tilemap, int col, int value );
/*TODO*///
/*TODO*///void tilemap_set_palette_offset( struct tilemap *tilemap, int offset );
/*TODO*///
    public static final int TILEMAP_FLIPX = 0x1;
    public static final int TILEMAP_FLIPY = 0x2;
    /*TODO*///void tilemap_set_flip( struct tilemap *tilemap, int attributes );
/*TODO*///void tilemap_set_enable( struct tilemap *tilemap, int enable );
/*TODO*///
/*TODO*///void tilemap_draw( struct mame_bitmap *dest, const struct rectangle *cliprect, struct tilemap *tilemap, UINT32 flags, UINT32 priority );
/*TODO*///
/*TODO*///void tilemap_draw_roz(struct mame_bitmap *dest,const struct rectangle *cliprect,struct tilemap *tilemap,
/*TODO*///		UINT32 startx,UINT32 starty,int incxx,int incxy,int incyx,int incyy,
/*TODO*///		int wraparound,
/*TODO*///		UINT32 flags, UINT32 priority );
/*TODO*///
/*TODO*////* ----xxxx tile priority
/*TODO*/// * ---x---- opaque in foreground
/*TODO*/// * --x----- opaque in background
/*TODO*/// * -x------ reserved
/*TODO*/// * x------- tile-is-dirty
/*TODO*/// */
/*TODO*///#define TILE_FLAG_TILE_PRIORITY	(0x0f)
/*TODO*///#define TILE_FLAG_FG_OPAQUE		(0x10)
/*TODO*///#define TILE_FLAG_BG_OPAQUE		(0x20)
/*TODO*///
/*TODO*///struct mame_bitmap *tilemap_get_pixmap( struct tilemap * tilemap );
/*TODO*///struct mame_bitmap *tilemap_get_transparency_bitmap( struct tilemap * tilemap );
/*TODO*///UINT8 *tilemap_get_transparency_data( struct tilemap * tilemap );  //*
/*TODO*///
/*TODO*////*********************************************************************/
/*TODO*///
/*TODO*///UINT32 tilemap_scan_cols( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows );
/*TODO*///UINT32 tilemap_scan_cols_flip_x( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows );
/*TODO*///UINT32 tilemap_scan_cols_flip_y( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows );
/*TODO*///UINT32 tilemap_scan_cols_flip_xy( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows );
/*TODO*///
/*TODO*///UINT32 tilemap_scan_rows( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows );
/*TODO*///UINT32 tilemap_scan_rows_flip_x( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows );
/*TODO*///UINT32 tilemap_scan_rows_flip_y( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows );
/*TODO*///UINT32 tilemap_scan_rows_flip_xy( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows );
/*TODO*///
/*TODO*////* For showcharset()'s sake */
/*TODO*///UINT32 tilemap_count( void );
/*TODO*///void tilemap_nb_size( UINT32 number, UINT32 *width, UINT32 *height );
/*TODO*///void tilemap_nb_draw( struct mame_bitmap *dest, UINT32 number, UINT32 scrollx, UINT32 scrolly );
/*TODO*///
/*TODO*///#endif
/*TODO*///    
}
