/**
 * ported to v0.78
 */
package arcadeflex.v078.mame;

//mame imports
import static arcadeflex.v078.mame.commonH.*;
import static arcadeflex.v078.mame.drawgfxH.*;
import static arcadeflex.v078.mame.tilemapH.*;

public class tilemapC {

    /*TODO*////* tilemap.c
/*TODO*///
/*TODO*///	When the videoram for a tile changes, call tilemap_mark_tile_dirty
/*TODO*///	with the appropriate memory offset.
/*TODO*///
/*TODO*///	In the video driver, follow these steps:
/*TODO*///
/*TODO*///	1)	Set each tilemap's scroll registers.
/*TODO*///
/*TODO*///	2)	Call tilemap_draw to draw the tilemaps to the screen, from back to front.
/*TODO*///
/*TODO*///	Notes:
/*TODO*///	-	You can currently configure a tilemap as xscroll + scrolling columns or
/*TODO*///		yscroll + scrolling rows, but not both types of scrolling simultaneously.
/*TODO*///*/
/*TODO*///
/*TODO*///#if !defined(DECLARE) && !defined(TRANSP)
/*TODO*///
/*TODO*///#include "driver.h"
/*TODO*///#include "osinline.h"
/*TODO*///#include "tilemap.h"
/*TODO*///#include "state.h"
/*TODO*///
/*TODO*///#define SWAP(X,Y) { UINT32 temp=X; X=Y; Y=temp; }
/*TODO*///#define MAX_TILESIZE 64
/*TODO*///
/*TODO*///#define TILE_FLAG_DIRTY	(0x80)
/*TODO*///
/*TODO*///typedef enum { eWHOLLY_TRANSPARENT, eWHOLLY_OPAQUE, eMASKED } trans_t;
/*TODO*///
/*TODO*///typedef void (*tilemap_draw_func)( struct tilemap *tilemap, int xpos, int ypos, int mask, int value );
/*TODO*///
    public static class struct_tilemap {
        /*TODO*///	UINT32 (*get_memory_offset)( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows );
/*TODO*///	int *memory_offset_to_cached_indx;
/*TODO*///	UINT32 *cached_indx_to_memory_offset;
/*TODO*///	int logical_flip_to_cached_flip[4];
/*TODO*///
/*TODO*///	/* callback to interpret video RAM for the tilemap */
/*TODO*///	void (*tile_get_info)( int memory_offset );
/*TODO*///
/*TODO*///	UINT32 max_memory_offset;
/*TODO*///	UINT32 num_tiles;
/*TODO*///	UINT32 num_pens;
/*TODO*///
/*TODO*///	UINT32 num_logical_rows, num_logical_cols;
/*TODO*///	UINT32 num_cached_rows, num_cached_cols;
/*TODO*///
/*TODO*///	UINT32 logical_tile_width, logical_tile_height;
/*TODO*///	UINT32 cached_tile_width, cached_tile_height;
/*TODO*///
/*TODO*///	UINT32 cached_width, cached_height;
/*TODO*///
/*TODO*///	int dx, dx_if_flipped;
/*TODO*///	int dy, dy_if_flipped;
/*TODO*///	int scrollx_delta, scrolly_delta;
/*TODO*///
/*TODO*///	int enable;
/*TODO*///	int attributes;
/*TODO*///
/*TODO*///	int type;
/*TODO*///	int transparent_pen;
/*TODO*///	UINT32 fgmask[4], bgmask[4]; /* for TILEMAP_SPLIT */
/*TODO*///
/*TODO*///	UINT32 *pPenToPixel[4];
/*TODO*///
/*TODO*///	UINT8 (*draw_tile)( struct tilemap *tilemap, UINT32 col, UINT32 row, UINT32 flags );
/*TODO*///
/*TODO*///	int cached_scroll_rows, cached_scroll_cols;
/*TODO*///	int *cached_rowscroll, *cached_colscroll;
/*TODO*///
/*TODO*///	int logical_scroll_rows, logical_scroll_cols;
/*TODO*///	int *logical_rowscroll, *logical_colscroll;
/*TODO*///
/*TODO*///	int orientation;
/*TODO*///	int palette_offset;
/*TODO*///
/*TODO*///	UINT16 tile_depth, tile_granularity;
/*TODO*///	UINT8 *tile_dirty_map;
/*TODO*///	UINT8 all_tiles_dirty;
/*TODO*///	UINT8 all_tiles_clean;
/*TODO*///
/*TODO*///	/* cached color data */
/*TODO*///	struct mame_bitmap *pixmap;
/*TODO*///	UINT32 pixmap_pitch_line;
/*TODO*///	UINT32 pixmap_pitch_row;
/*TODO*///
/*TODO*///	struct mame_bitmap *transparency_bitmap;
/*TODO*///	UINT32 transparency_bitmap_pitch_line;
/*TODO*///	UINT32 transparency_bitmap_pitch_row;
/*TODO*///	UINT8 *transparency_data, **transparency_data_row;
/*TODO*///
/*TODO*///	struct tilemap *next; /* resource tracking */
    }

    /*TODO*///
/*TODO*///struct mame_bitmap *		priority_bitmap;
/*TODO*///UINT32					priority_bitmap_pitch_line;
/*TODO*///UINT32					priority_bitmap_pitch_row;
/*TODO*///
/*TODO*///static struct tilemap *	first_tilemap; /* resource tracking */
/*TODO*///static UINT32			screen_width, screen_height;
/*TODO*///struct tile_info		tile_info;
/*TODO*///
/*TODO*///static UINT32 g_mask32[32];
/*TODO*///
/*TODO*///typedef void (*blitmask_t)( void *dest, const void *source, const UINT8 *pMask, int mask, int value, int count, UINT8 *pri, UINT32 pcode );
/*TODO*///typedef void (*blitopaque_t)( void *dest, const void *source, int count, UINT8 *pri, UINT32 pcode );
/*TODO*///
/*TODO*////* the following parameters are constant across tilemap_draw calls */
/*TODO*///static struct
/*TODO*///{
/*TODO*///	blitmask_t draw_masked;
/*TODO*///	blitopaque_t draw_opaque;
/*TODO*///	int clip_left, clip_top, clip_right, clip_bottom;
/*TODO*///	UINT32 tilemap_priority_code;
/*TODO*///	struct mame_bitmap *	screen_bitmap;
/*TODO*///	UINT32				screen_bitmap_pitch_line;
/*TODO*///	UINT32				screen_bitmap_pitch_row;
/*TODO*///} blit;
/*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*///
/*TODO*///static int PenToPixel_Init( struct tilemap *tilemap );
/*TODO*///static void PenToPixel_Term( struct tilemap *tilemap );
/*TODO*///static int mappings_create( struct tilemap *tilemap );
/*TODO*///static void mappings_dispose( struct tilemap *tilemap );
/*TODO*///static void mappings_update( struct tilemap *tilemap );
/*TODO*///static void recalculate_scroll( struct tilemap *tilemap );
/*TODO*///
/*TODO*///static void install_draw_handlers( struct tilemap *tilemap );
/*TODO*///static void tilemap_reset(void);
/*TODO*///
/*TODO*///static void update_tile_info( struct tilemap *tilemap, UINT32 cached_indx, UINT32 cached_col, UINT32 cached_row );
/*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*///
/*TODO*///static int PenToPixel_Init( struct tilemap *tilemap )
/*TODO*///{
/*TODO*///	/*
/*TODO*///		Construct a table for all tile orientations in advance.
/*TODO*///		This simplifies drawing tiles and masks tremendously.
/*TODO*///		If performance is an issue, we can always (re)introduce
/*TODO*///		customized code for each case and forgo tables.
/*TODO*///	*/
/*TODO*///	int i,x,y,tx,ty;
/*TODO*///	UINT32 *pPenToPixel;
/*TODO*///	int lError;
/*TODO*///
/*TODO*///	lError = 0;
/*TODO*///	for( i=0; i<4; i++ )
/*TODO*///	{
/*TODO*///		pPenToPixel = malloc( tilemap->num_pens*sizeof(UINT32) );
/*TODO*///		if( pPenToPixel==NULL )
/*TODO*///		{
/*TODO*///			lError = 1;
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			tilemap->pPenToPixel[i] = pPenToPixel;
/*TODO*///			for( ty=0; ty<tilemap->cached_tile_height; ty++ )
/*TODO*///			{
/*TODO*///				for( tx=0; tx<tilemap->cached_tile_width; tx++ )
/*TODO*///				{
/*TODO*///					x = tx;
/*TODO*///					y = ty;
/*TODO*///					if( i&TILE_FLIPX ) x = tilemap->cached_tile_width-1-x;
/*TODO*///					if( i&TILE_FLIPY ) y = tilemap->cached_tile_height-1-y;
/*TODO*///					*pPenToPixel++ = x+y*MAX_TILESIZE;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	return lError;
/*TODO*///}
/*TODO*///
/*TODO*///static void PenToPixel_Term( struct tilemap *tilemap )
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	for( i=0; i<4; i++ )
/*TODO*///	{
/*TODO*///		free( tilemap->pPenToPixel[i] );
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///static void InitMask32(void)
/*TODO*///{
/*TODO*///	int i;
/*TODO*///
/*TODO*///	for (i=0;i<16;i++)
/*TODO*///	{
/*TODO*///		UINT32 p1 = (i&1) ? 0xFFFF : 0;
/*TODO*///		UINT32 p2 = (i&2) ? 0xFFFF : 0;
/*TODO*///		UINT32 p3 = (i&4) ? 0xFFFF : 0;
/*TODO*///		UINT32 p4 = (i&8) ? 0xFFFF : 0;
/*TODO*///
/*TODO*///		g_mask32[i*2] = (p2 << 16) | p1;
/*TODO*///		g_mask32[i*2+1] = (p4 << 16) | p3;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
    public static void tilemap_set_transparent_pen(struct_tilemap tilemap, int pen) {
        throw new UnsupportedOperationException("Unsupported");
        /*TODO*///	tilemap->transparent_pen = pen;
    }
    /*TODO*///
/*TODO*///void tilemap_set_transmask( struct tilemap *tilemap, int which, UINT32 fgmask, UINT32 bgmask )
/*TODO*///{
/*TODO*///	if( tilemap->fgmask[which] != fgmask || tilemap->bgmask[which] != bgmask )
/*TODO*///	{
/*TODO*///		tilemap->fgmask[which] = fgmask;
/*TODO*///		tilemap->bgmask[which] = bgmask;
/*TODO*///		tilemap_mark_all_tiles_dirty( tilemap );
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///void tilemap_set_depth( struct tilemap *tilemap, int tile_depth, int tile_granularity )
/*TODO*///{
/*TODO*///	if( tilemap->tile_dirty_map )
/*TODO*///	{
/*TODO*///		free( tilemap->tile_dirty_map);
/*TODO*///	}
/*TODO*///	tilemap->tile_dirty_map = malloc( Machine->drv->total_colors >> tile_granularity );
/*TODO*///	if( tilemap->tile_dirty_map )
/*TODO*///	{
/*TODO*///		tilemap->tile_depth = tile_depth;
/*TODO*///		tilemap->tile_granularity = tile_granularity;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*////* some common mappings */
/*TODO*///
    public static GetMemoryOffsetHandlerPtr tilemap_scan_rows = new GetMemoryOffsetHandlerPtr() {
        public int handler(int u32_col, int u32_row, int u32_num_cols, int u32_num_rows) {
            /* logical (col,row) -> memory offset */
            return u32_row * u32_num_cols + u32_col;
        }
    };

    /*TODO*///UINT32 tilemap_scan_rows_flip_x( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows )
/*TODO*///{
/*TODO*///	/* logical (col,row) -> memory offset */
/*TODO*///	return row*num_cols + (num_cols-col-1);
/*TODO*///}
/*TODO*///UINT32 tilemap_scan_rows_flip_y( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows )
/*TODO*///{
/*TODO*///	/* logical (col,row) -> memory offset */
/*TODO*///	return (num_rows-row-1)*num_cols + col;
/*TODO*///}
/*TODO*///UINT32 tilemap_scan_rows_flip_xy( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows )
/*TODO*///{
/*TODO*///	/* logical (col,row) -> memory offset */
/*TODO*///	return (num_rows-row-1)*num_cols + (num_cols-col-1);
/*TODO*///}
/*TODO*///
/*TODO*///UINT32 tilemap_scan_cols( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows )
/*TODO*///{
/*TODO*///	/* logical (col,row) -> memory offset */
/*TODO*///	return col*num_rows + row;
/*TODO*///}
/*TODO*///UINT32 tilemap_scan_cols_flip_x( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows )
/*TODO*///{
/*TODO*///	/* logical (col,row) -> memory offset */
/*TODO*///	return (num_cols-col-1)*num_rows + row;
/*TODO*///}
/*TODO*///UINT32 tilemap_scan_cols_flip_y( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows )
/*TODO*///{
/*TODO*///	/* logical (col,row) -> memory offset */
/*TODO*///	return col*num_rows + (num_rows-row-1);
/*TODO*///}
/*TODO*///UINT32 tilemap_scan_cols_flip_xy( UINT32 col, UINT32 row, UINT32 num_cols, UINT32 num_rows )
/*TODO*///{
/*TODO*///	/* logical (col,row) -> memory offset */
/*TODO*///	return (num_cols-col-1)*num_rows + (num_rows-row-1);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*///
/*TODO*///static int mappings_create( struct tilemap *tilemap )
/*TODO*///{
/*TODO*///	int max_memory_offset = 0;
/*TODO*///	UINT32 col,row;
/*TODO*///	UINT32 num_logical_rows = tilemap->num_logical_rows;
/*TODO*///	UINT32 num_logical_cols = tilemap->num_logical_cols;
/*TODO*///	/* count offsets (might be larger than num_tiles) */
/*TODO*///	for( row=0; row<num_logical_rows; row++ )
/*TODO*///	{
/*TODO*///		for( col=0; col<num_logical_cols; col++ )
/*TODO*///		{
/*TODO*///			UINT32 memory_offset = tilemap->get_memory_offset( col, row, num_logical_cols, num_logical_rows );
/*TODO*///			if( memory_offset>max_memory_offset ) max_memory_offset = memory_offset;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	max_memory_offset++;
/*TODO*///	tilemap->max_memory_offset = max_memory_offset;
/*TODO*///	/* logical to cached (tilemap_mark_dirty) */
/*TODO*///	tilemap->memory_offset_to_cached_indx = malloc( sizeof(int)*max_memory_offset );
/*TODO*///	if( tilemap->memory_offset_to_cached_indx )
/*TODO*///	{
/*TODO*///		/* cached to logical (get_tile_info) */
/*TODO*///		tilemap->cached_indx_to_memory_offset = malloc( sizeof(UINT32)*tilemap->num_tiles );
/*TODO*///		if( tilemap->cached_indx_to_memory_offset ) return 0; /* no error */
/*TODO*///		free( tilemap->memory_offset_to_cached_indx );
/*TODO*///	}
/*TODO*///	return -1; /* error */
/*TODO*///}
/*TODO*///
/*TODO*///static void mappings_dispose( struct tilemap *tilemap )
/*TODO*///{
/*TODO*///	free( tilemap->cached_indx_to_memory_offset );
/*TODO*///	free( tilemap->memory_offset_to_cached_indx );
/*TODO*///}
/*TODO*///
/*TODO*///static void mappings_update( struct tilemap *tilemap )
/*TODO*///{
/*TODO*///	int logical_flip;
/*TODO*///	UINT32 logical_indx, cached_indx;
/*TODO*///	UINT32 num_cached_rows = tilemap->num_cached_rows;
/*TODO*///	UINT32 num_cached_cols = tilemap->num_cached_cols;
/*TODO*///	UINT32 num_logical_rows = tilemap->num_logical_rows;
/*TODO*///	UINT32 num_logical_cols = tilemap->num_logical_cols;
/*TODO*///	for( logical_indx=0; logical_indx<tilemap->max_memory_offset; logical_indx++ )
/*TODO*///	{
/*TODO*///		tilemap->memory_offset_to_cached_indx[logical_indx] = -1;
/*TODO*///	}
/*TODO*///
/*TODO*///	for( logical_indx=0; logical_indx<tilemap->num_tiles; logical_indx++ )
/*TODO*///	{
/*TODO*///		UINT32 logical_col = logical_indx%num_logical_cols;
/*TODO*///		UINT32 logical_row = logical_indx/num_logical_cols;
/*TODO*///		int memory_offset = tilemap->get_memory_offset( logical_col, logical_row, num_logical_cols, num_logical_rows );
/*TODO*///		UINT32 cached_col = logical_col;
/*TODO*///		UINT32 cached_row = logical_row;
/*TODO*///		if( tilemap->orientation & ORIENTATION_SWAP_XY ) SWAP(cached_col,cached_row)
/*TODO*///		if( tilemap->orientation & ORIENTATION_FLIP_X ) cached_col = (num_cached_cols-1)-cached_col;
/*TODO*///		if( tilemap->orientation & ORIENTATION_FLIP_Y ) cached_row = (num_cached_rows-1)-cached_row;
/*TODO*///		cached_indx = cached_row*num_cached_cols+cached_col;
/*TODO*///		tilemap->memory_offset_to_cached_indx[memory_offset] = cached_indx;
/*TODO*///		tilemap->cached_indx_to_memory_offset[cached_indx] = memory_offset;
/*TODO*///	}
/*TODO*///	for( logical_flip = 0; logical_flip<4; logical_flip++ )
/*TODO*///	{
/*TODO*///		int cached_flip = logical_flip;
/*TODO*///		if( tilemap->attributes&TILEMAP_FLIPX ) cached_flip ^= TILE_FLIPX;
/*TODO*///		if( tilemap->attributes&TILEMAP_FLIPY ) cached_flip ^= TILE_FLIPY;
/*TODO*///#ifndef PREROTATE_GFX
/*TODO*///		if( Machine->orientation & ORIENTATION_SWAP_XY )
/*TODO*///		{
/*TODO*///			if( Machine->orientation & ORIENTATION_FLIP_X ) cached_flip ^= TILE_FLIPY;
/*TODO*///			if( Machine->orientation & ORIENTATION_FLIP_Y ) cached_flip ^= TILE_FLIPX;
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			if( Machine->orientation & ORIENTATION_FLIP_X ) cached_flip ^= TILE_FLIPX;
/*TODO*///			if( Machine->orientation & ORIENTATION_FLIP_Y ) cached_flip ^= TILE_FLIPY;
/*TODO*///		}
/*TODO*///#endif
/*TODO*///		if( tilemap->orientation & ORIENTATION_SWAP_XY )
/*TODO*///		{
/*TODO*///			cached_flip = ((cached_flip&1)<<1) | ((cached_flip&2)>>1);
/*TODO*///		}
/*TODO*///		tilemap->logical_flip_to_cached_flip[logical_flip] = cached_flip;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*///
/*TODO*///static void pio( void *dest, const void *source, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int i;
/*TODO*///
/*TODO*///	if (pcode)
/*TODO*///		for( i=0; i<count; i++ )
/*TODO*///		{
/*TODO*///			pri[i] |= pcode;
/*TODO*///		}
/*TODO*///}
/*TODO*///
/*TODO*///static void pit( void *dest, const void *source, const UINT8 *pMask, int mask, int value, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int i;
/*TODO*///
/*TODO*///	if (pcode)
/*TODO*///		for( i=0; i<count; i++ )
/*TODO*///		{
/*TODO*///			if( (pMask[i]&mask)==value )
/*TODO*///			{
/*TODO*///				pri[i] |= pcode;
/*TODO*///			}
/*TODO*///		}
/*TODO*///}
/*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*///
/*TODO*///#ifndef pdo16
/*TODO*///static void pdo16( UINT16 *dest, const UINT16 *source, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	memcpy( dest,source,count*sizeof(UINT16) );
/*TODO*///	for( i=0; i<count; i++ )
/*TODO*///	{
/*TODO*///		pri[i] |= pcode;
/*TODO*///	}
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///#ifndef pdo16pal
/*TODO*///static void pdo16pal( UINT16 *dest, const UINT16 *source, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int pal = pcode >> 16;
/*TODO*///	int i;
/*TODO*///	for( i=0; i<count; i++ )
/*TODO*///	{
/*TODO*///		dest[i] = source[i] + pal;
/*TODO*///		pri[i] |= pcode;
/*TODO*///	}
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///#ifndef pdo16np
/*TODO*///static void pdo16np( UINT16 *dest, const UINT16 *source, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	memcpy( dest,source,count*sizeof(UINT16) );
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///static void pdo15( UINT16 *dest, const UINT16 *source, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	pen_t *clut = &Machine->remapped_colortable[pcode >> 16];
/*TODO*///	for( i=0; i<count; i++ )
/*TODO*///	{
/*TODO*///		dest[i] = clut[source[i]];
/*TODO*///		pri[i] |= pcode;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///#ifndef pdo32
/*TODO*///static void pdo32( UINT32 *dest, const UINT16 *source, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	pen_t *clut = &Machine->remapped_colortable[pcode >> 16];
/*TODO*///	for( i=0; i<count; i++ )
/*TODO*///	{
/*TODO*///		dest[i] = clut[source[i]];
/*TODO*///		pri[i] |= pcode;
/*TODO*///	}
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///#ifndef npdo32
/*TODO*///static void npdo32( UINT32 *dest, const UINT16 *source, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int oddcount = count & 3;
/*TODO*///	int unrcount = count & ~3;
/*TODO*///	int i;
/*TODO*///	pen_t *clut = &Machine->remapped_colortable[pcode >> 16];
/*TODO*///	for( i=0; i<oddcount; i++ )
/*TODO*///	{
/*TODO*///		dest[i] = clut[source[i]];
/*TODO*///	}
/*TODO*///	source += count; dest += count;
/*TODO*///	for( i=-unrcount; i; i+=4 )
/*TODO*///	{
/*TODO*///		UINT32 eax, ebx;
/*TODO*///		eax = source[i  ];
/*TODO*///		ebx = source[i+1];
/*TODO*///		eax = clut[eax];
/*TODO*///		ebx = clut[ebx];
/*TODO*///		dest[i  ] = eax;
/*TODO*///		eax = source[i+2];
/*TODO*///		dest[i+1] = ebx;
/*TODO*///		ebx = source[i+3];
/*TODO*///		eax = clut[eax];
/*TODO*///		ebx = clut[ebx];
/*TODO*///		dest[i+2] = eax;
/*TODO*///		dest[i+3] = ebx;
/*TODO*///	}
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*///
/*TODO*///#ifndef pdt16
/*TODO*///static void pdt16( UINT16 *dest, const UINT16 *source, const UINT8 *pMask, int mask, int value, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int i;
/*TODO*///
/*TODO*///	for( i=0; i<count; i++ )
/*TODO*///	{
/*TODO*///		if( (pMask[i]&mask)==value )
/*TODO*///		{
/*TODO*///			dest[i] = source[i];
/*TODO*///			pri[i] |= pcode;
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///#ifndef pdt16pal
/*TODO*///static void pdt16pal( UINT16 *dest, const UINT16 *source, const UINT8 *pMask, int mask, int value, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int pal = pcode >> 16;
/*TODO*///	int i;
/*TODO*///
/*TODO*///	for( i=0; i<count; i++ )
/*TODO*///	{
/*TODO*///		if( (pMask[i]&mask)==value )
/*TODO*///		{
/*TODO*///			dest[i] = source[i] + pal;
/*TODO*///			pri[i] |= pcode;
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///#ifndef pdt16np
/*TODO*///static void pdt16np( UINT16 *dest, const UINT16 *source, const UINT8 *pMask, int mask, int value, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int i;
/*TODO*///
/*TODO*///	for( i=0; i<count; i++ )
/*TODO*///	{
/*TODO*///		if( (pMask[i]&mask)==value )
/*TODO*///			dest[i] = source[i];
/*TODO*///	}
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///static void pdt15( UINT16 *dest, const UINT16 *source, const UINT8 *pMask, int mask, int value, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	pen_t *clut = &Machine->remapped_colortable[pcode >> 16];
/*TODO*///	for( i=0; i<count; i++ )
/*TODO*///	{
/*TODO*///		if( (pMask[i]&mask)==value )
/*TODO*///		{
/*TODO*///			dest[i] = clut[source[i]];
/*TODO*///			pri[i] |= pcode;
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///#ifndef pdt32
/*TODO*///static void pdt32( UINT32 *dest, const UINT16 *source, const UINT8 *pMask, int mask, int value, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	pen_t *clut = &Machine->remapped_colortable[pcode >> 16];
/*TODO*///	for( i=0; i<count; i++ )
/*TODO*///	{
/*TODO*///		if( (pMask[i]&mask)==value )
/*TODO*///		{
/*TODO*///			dest[i] = clut[source[i]];
/*TODO*///			pri[i] |= pcode;
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///#ifndef npdt32
/*TODO*///static void npdt32( UINT32 *dest, const UINT16 *source, const UINT8 *pMask, int mask, int value, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int oddcount = count & 3;
/*TODO*///	int unrcount = count & ~3;
/*TODO*///	int i;
/*TODO*///	pen_t *clut = &Machine->remapped_colortable[pcode >> 16];
/*TODO*///
/*TODO*///	for( i=0; i<oddcount; i++ )
/*TODO*///	{
/*TODO*///		if( (pMask[i]&mask)==value ) dest[i] = clut[source[i]];
/*TODO*///	}
/*TODO*///	pMask += count, source += count; dest += count;
/*TODO*///	for( i=-unrcount; i; i+=4 )
/*TODO*///	{
/*TODO*///		if( (pMask[i  ]&mask)==value ) dest[i  ] = clut[source[i  ]];
/*TODO*///		if( (pMask[i+1]&mask)==value ) dest[i+1] = clut[source[i+1]];
/*TODO*///		if( (pMask[i+2]&mask)==value ) dest[i+2] = clut[source[i+2]];
/*TODO*///		if( (pMask[i+3]&mask)==value ) dest[i+3] = clut[source[i+3]];
/*TODO*///	}
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*///
/*TODO*///static void pbo15( UINT16 *dest, const UINT16 *source, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	pen_t *clut = &Machine->remapped_colortable[pcode >> 16];
/*TODO*///	for( i=0; i<count; i++ )
/*TODO*///	{
/*TODO*///		dest[i] = alpha_blend16(dest[i], clut[source[i]]);
/*TODO*///		pri[i] |= pcode;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///#ifndef pbo32
/*TODO*///static void pbo32( UINT32 *dest, const UINT16 *source, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	pen_t *clut = &Machine->remapped_colortable[pcode >> 16];
/*TODO*///	for( i=0; i<count; i++ )
/*TODO*///	{
/*TODO*///		dest[i] = alpha_blend32(dest[i], clut[source[i]]);
/*TODO*///		pri[i] |= pcode;
/*TODO*///	}
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///#ifndef npbo32
/*TODO*///static void npbo32( UINT32 *dest, const UINT16 *source, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int oddcount = count & 3;
/*TODO*///	int unrcount = count & ~3;
/*TODO*///	int i;
/*TODO*///	pen_t *clut = &Machine->remapped_colortable[pcode >> 16];
/*TODO*///	for( i=0; i<oddcount; i++ )
/*TODO*///	{
/*TODO*///		dest[i] = alpha_blend32(dest[i], clut[source[i]]);
/*TODO*///	}
/*TODO*///	source += count; dest += count;
/*TODO*///	for( i=-unrcount; i; i+=4 )
/*TODO*///	{
/*TODO*///		dest[i  ] = alpha_blend32(dest[i  ], clut[source[i  ]]);
/*TODO*///		dest[i+1] = alpha_blend32(dest[i+1], clut[source[i+1]]);
/*TODO*///		dest[i+2] = alpha_blend32(dest[i+2], clut[source[i+2]]);
/*TODO*///		dest[i+3] = alpha_blend32(dest[i+3], clut[source[i+3]]);
/*TODO*///	}
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*///
/*TODO*///static void pbt15( UINT16 *dest, const UINT16 *source, const UINT8 *pMask, int mask, int value, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	pen_t *clut = &Machine->remapped_colortable[pcode >> 16];
/*TODO*///	for( i=0; i<count; i++ )
/*TODO*///	{
/*TODO*///		if( (pMask[i]&mask)==value )
/*TODO*///		{
/*TODO*///			dest[i] = alpha_blend16(dest[i], clut[source[i]]);
/*TODO*///			pri[i] |= pcode;
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///#ifndef pbt32
/*TODO*///static void pbt32( UINT32 *dest, const UINT16 *source, const UINT8 *pMask, int mask, int value, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int i;
/*TODO*///	pen_t *clut = &Machine->remapped_colortable[pcode >> 16];
/*TODO*///	for( i=0; i<count; i++ )
/*TODO*///	{
/*TODO*///		if( (pMask[i]&mask)==value )
/*TODO*///		{
/*TODO*///			dest[i] = alpha_blend32(dest[i], clut[source[i]]);
/*TODO*///			pri[i] |= pcode;
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*///#ifndef npbt32
/*TODO*///static void npbt32( UINT32 *dest, const UINT16 *source, const UINT8 *pMask, int mask, int value, int count, UINT8 *pri, UINT32 pcode )
/*TODO*///{
/*TODO*///	int oddcount = count & 3;
/*TODO*///	int unrcount = count & ~3;
/*TODO*///	int i;
/*TODO*///	pen_t *clut = &Machine->remapped_colortable[pcode >> 16];
/*TODO*///
/*TODO*///	for( i=0; i<oddcount; i++ )
/*TODO*///	{
/*TODO*///		if( (pMask[i]&mask)==value ) dest[i] = alpha_blend32(dest[i], clut[source[i]]);
/*TODO*///	}
/*TODO*///	pMask += count, source += count; dest += count;
/*TODO*///	for( i=-unrcount; i; i+=4 )
/*TODO*///	{
/*TODO*///		if( (pMask[i  ]&mask)==value ) dest[i  ] = alpha_blend32(dest[i  ], clut[source[i  ]]);
/*TODO*///		if( (pMask[i+1]&mask)==value ) dest[i+1] = alpha_blend32(dest[i+1], clut[source[i+1]]);
/*TODO*///		if( (pMask[i+2]&mask)==value ) dest[i+2] = alpha_blend32(dest[i+2], clut[source[i+2]]);
/*TODO*///		if( (pMask[i+3]&mask)==value ) dest[i+3] = alpha_blend32(dest[i+3], clut[source[i+3]]);
/*TODO*///	}
/*TODO*///}
/*TODO*///#endif
/*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*///
/*TODO*///#define DEPTH 16
/*TODO*///#define DATA_TYPE UINT16
/*TODO*///#define DECLARE(function,args,body) static void function##16BPP args body
/*TODO*///#include "tilemap.c"
/*TODO*///
/*TODO*///#define DEPTH 32
/*TODO*///#define DATA_TYPE UINT32
/*TODO*///#define DECLARE(function,args,body) static void function##32BPP args body
/*TODO*///#include "tilemap.c"
/*TODO*///
/*TODO*///#define PAL_INIT const pen_t *pPalData = tile_info.pal_data
/*TODO*///#define PAL_GET(pen) pPalData[pen]
/*TODO*///#define TRANSP(f) f ## _ind
/*TODO*///#include "tilemap.c"
/*TODO*///
/*TODO*///#define PAL_INIT int palBase = tile_info.pal_data - Machine->remapped_colortable
/*TODO*///#define PAL_GET(pen) (palBase + (pen))
/*TODO*///#define TRANSP(f) f ## _raw
/*TODO*///#include "tilemap.c"
/*TODO*///
/*TODO*////*********************************************************************************/
/*TODO*///
/*TODO*///static void install_draw_handlers( struct tilemap *tilemap )
/*TODO*///{
/*TODO*///	if( Machine->game_colortable )
/*TODO*///	{
/*TODO*///		if( tilemap->type & TILEMAP_BITMASK )
/*TODO*///			tilemap->draw_tile = HandleTransparencyBitmask_ind;
/*TODO*///		else if( tilemap->type & TILEMAP_SPLIT_PENBIT )
/*TODO*///			tilemap->draw_tile = HandleTransparencyPenBit_ind;
/*TODO*///		else if( tilemap->type & TILEMAP_SPLIT )
/*TODO*///			tilemap->draw_tile = HandleTransparencyPens_ind;
/*TODO*///		else if( tilemap->type==TILEMAP_TRANSPARENT )
/*TODO*///			tilemap->draw_tile = HandleTransparencyPen_ind;
/*TODO*///		else if( tilemap->type==TILEMAP_TRANSPARENT_COLOR )
/*TODO*///			tilemap->draw_tile = HandleTransparencyColor_ind;
/*TODO*///		else
/*TODO*///			tilemap->draw_tile = HandleTransparencyNone_ind;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		if( tilemap->type & TILEMAP_BITMASK )
/*TODO*///			tilemap->draw_tile = HandleTransparencyBitmask_raw;
/*TODO*///		else if( tilemap->type & TILEMAP_SPLIT_PENBIT )
/*TODO*///			tilemap->draw_tile = HandleTransparencyPenBit_raw;
/*TODO*///		else if( tilemap->type & TILEMAP_SPLIT )
/*TODO*///			tilemap->draw_tile = HandleTransparencyPens_raw;
/*TODO*///		else if( tilemap->type==TILEMAP_TRANSPARENT )
/*TODO*///			tilemap->draw_tile = HandleTransparencyPen_raw;
/*TODO*///		else if( tilemap->type==TILEMAP_TRANSPARENT_COLOR )
/*TODO*///			tilemap->draw_tile = HandleTransparencyColor_raw;
/*TODO*///		else
/*TODO*///			tilemap->draw_tile = HandleTransparencyNone_raw;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///INLINE tilemap_draw_func pick_draw_func( struct mame_bitmap *dest )
/*TODO*///{
/*TODO*///	switch (dest ? dest->depth : Machine->scrbitmap->depth)
/*TODO*///	{
/*TODO*///		case 32:
/*TODO*///			return draw32BPP;
/*TODO*///
/*TODO*///		case 16:
/*TODO*///		case 15:
/*TODO*///			return draw16BPP;
/*TODO*///	}
/*TODO*///	exit(1);
/*TODO*///	return NULL;
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*///
/*TODO*///static void tilemap_reset(void)
/*TODO*///{
/*TODO*///	tilemap_mark_all_tiles_dirty(ALL_TILEMAPS);
/*TODO*///}
/*TODO*///
/*TODO*///int tilemap_init( void )
/*TODO*///{
/*TODO*///	screen_width	= Machine->scrbitmap->width;
/*TODO*///	screen_height	= Machine->scrbitmap->height;
/*TODO*///	first_tilemap	= NULL;
/*TODO*///
/*TODO*///	state_save_register_func_postload(tilemap_reset);
/*TODO*///	priority_bitmap = bitmap_alloc_depth( screen_width, screen_height, -8 );
/*TODO*///	if( priority_bitmap )
/*TODO*///	{
/*TODO*///		priority_bitmap_pitch_line = ((UINT8 *)priority_bitmap->line[1]) - ((UINT8 *)priority_bitmap->line[0]);
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///	InitMask32();
/*TODO*///	return -1;
/*TODO*///}
/*TODO*///
/*TODO*///void tilemap_close( void )
/*TODO*///{
/*TODO*///	struct tilemap *next;
/*TODO*///
/*TODO*///	while( first_tilemap )
/*TODO*///	{
/*TODO*///		next = first_tilemap->next;
/*TODO*///		tilemap_dispose( first_tilemap );
/*TODO*///		first_tilemap = next;
/*TODO*///	}
/*TODO*///	bitmap_free( priority_bitmap );
/*TODO*///}
/*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*///
    public static struct_tilemap tilemap_create(
            GetTileInfoHandlerPtr tile_get_info,
            GetMemoryOffsetHandlerPtr get_memory_offset,
            int type,
            int tile_width, int tile_height,
            int num_cols, int num_rows) {
        throw new UnsupportedOperationException("Unsupported");
        /*TODO*///	struct tilemap *tilemap;
/*TODO*///	UINT32 row;
/*TODO*///	int num_tiles;
/*TODO*///
/*TODO*///	tilemap = calloc( 1,sizeof( struct tilemap ) );
/*TODO*///	if( tilemap )
/*TODO*///	{
/*TODO*///		num_tiles = num_cols*num_rows;
/*TODO*///		tilemap->num_logical_cols = num_cols;
/*TODO*///		tilemap->num_logical_rows = num_rows;
/*TODO*///		tilemap->logical_tile_width = tile_width;
/*TODO*///		tilemap->logical_tile_height = tile_height;
/*TODO*///		tilemap->logical_colscroll = calloc(num_cols*tile_width,sizeof(int));
/*TODO*///		tilemap->logical_rowscroll = calloc(num_rows*tile_height,sizeof(int));
/*TODO*///		if( Machine->orientation & ORIENTATION_SWAP_XY )
/*TODO*///		{
/*TODO*///			SWAP( num_cols, num_rows )
/*TODO*///			SWAP( tile_width, tile_height )
/*TODO*///		}
/*TODO*///		tilemap->num_cached_cols = num_cols;
/*TODO*///		tilemap->num_cached_rows = num_rows;
/*TODO*///		tilemap->num_tiles = num_tiles;
/*TODO*///		tilemap->num_pens = tile_width*tile_height;
/*TODO*///		tilemap->cached_tile_width = tile_width;
/*TODO*///		tilemap->cached_tile_height = tile_height;
/*TODO*///		tilemap->cached_width = tile_width*num_cols;
/*TODO*///		tilemap->cached_height = tile_height*num_rows;
/*TODO*///		tilemap->tile_get_info = tile_get_info;
/*TODO*///		tilemap->get_memory_offset = get_memory_offset;
/*TODO*///		tilemap->orientation = Machine->orientation;
/*TODO*///
/*TODO*///		/* various defaults */
/*TODO*///		tilemap->enable = 1;
/*TODO*///		tilemap->type = type;
/*TODO*///		tilemap->logical_scroll_rows = tilemap->cached_scroll_rows = 1;
/*TODO*///		tilemap->logical_scroll_cols = tilemap->cached_scroll_cols = 1;
/*TODO*///		tilemap->transparent_pen = -1;
/*TODO*///		tilemap->tile_depth = 0;
/*TODO*///		tilemap->tile_granularity = 0;
/*TODO*///		tilemap->tile_dirty_map = 0;
/*TODO*///
/*TODO*///		tilemap->cached_rowscroll	= calloc(tilemap->cached_height,sizeof(int));
/*TODO*///		tilemap->cached_colscroll	= calloc(tilemap->cached_width, sizeof(int));
/*TODO*///
/*TODO*///		tilemap->transparency_data = malloc( num_tiles );
/*TODO*///		tilemap->transparency_data_row = malloc( sizeof(UINT8 *)*num_rows );
/*TODO*///
/*TODO*///		tilemap->pixmap = bitmap_alloc_depth( tilemap->cached_width, tilemap->cached_height, -16 );
/*TODO*///		tilemap->transparency_bitmap = bitmap_alloc_depth( tilemap->cached_width, tilemap->cached_height, -8 );
/*TODO*///
/*TODO*///		if( tilemap->logical_rowscroll && tilemap->cached_rowscroll &&
/*TODO*///			tilemap->logical_colscroll && tilemap->cached_colscroll &&
/*TODO*///			tilemap->pixmap &&
/*TODO*///			tilemap->transparency_data &&
/*TODO*///			tilemap->transparency_data_row &&
/*TODO*///			tilemap->transparency_bitmap &&
/*TODO*///			(mappings_create( tilemap )==0) )
/*TODO*///		{
/*TODO*///			tilemap->pixmap_pitch_line = tilemap->pixmap->rowpixels;
/*TODO*///			tilemap->pixmap_pitch_row = tilemap->pixmap_pitch_line*tile_height;
/*TODO*///
/*TODO*///			tilemap->transparency_bitmap_pitch_line = tilemap->transparency_bitmap->rowpixels;
/*TODO*///			tilemap->transparency_bitmap_pitch_row = tilemap->transparency_bitmap_pitch_line*tile_height;
/*TODO*///
/*TODO*///			for( row=0; row<num_rows; row++ )
/*TODO*///			{
/*TODO*///				tilemap->transparency_data_row[row] = tilemap->transparency_data+num_cols*row;
/*TODO*///			}
/*TODO*///			install_draw_handlers( tilemap );
/*TODO*///			mappings_update( tilemap );
/*TODO*///			memset( tilemap->transparency_data, TILE_FLAG_DIRTY, num_tiles );
/*TODO*///			tilemap->next = first_tilemap;
/*TODO*///			first_tilemap = tilemap;
/*TODO*///			if( PenToPixel_Init( tilemap ) == 0 )
/*TODO*///			{
/*TODO*///				recalculate_scroll(tilemap);
/*TODO*///				return tilemap;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		tilemap_dispose( tilemap );
/*TODO*///	}
/*TODO*///	return 0;
    }

    /*TODO*///
/*TODO*///void tilemap_dispose( struct tilemap *tilemap )
/*TODO*///{
/*TODO*///	struct tilemap *prev;
/*TODO*///
/*TODO*///	if( tilemap==first_tilemap )
/*TODO*///	{
/*TODO*///		first_tilemap = tilemap->next;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		prev = first_tilemap;
/*TODO*///		while( prev && prev->next != tilemap ) prev = prev->next;
/*TODO*///		if( prev ) prev->next =tilemap->next;
/*TODO*///	}
/*TODO*///	PenToPixel_Term( tilemap );
/*TODO*///	free( tilemap->logical_rowscroll );
/*TODO*///	free( tilemap->cached_rowscroll );
/*TODO*///	free( tilemap->logical_colscroll );
/*TODO*///	free( tilemap->cached_colscroll );
/*TODO*///	free( tilemap->transparency_data );
/*TODO*///	free( tilemap->transparency_data_row );
/*TODO*///	bitmap_free( tilemap->transparency_bitmap );
/*TODO*///	bitmap_free( tilemap->pixmap );
/*TODO*///	mappings_dispose( tilemap );
/*TODO*///	free( tilemap );
/*TODO*///}
/*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*///
/*TODO*///void tilemap_set_enable( struct tilemap *tilemap, int enable )
/*TODO*///{
/*TODO*///	tilemap->enable = enable?1:0;
/*TODO*///}
/*TODO*///
/*TODO*///
    public static void tilemap_set_flip(struct_tilemap tilemap, int attributes) {
        throw new UnsupportedOperationException("Unsupported");
        /*TODO*///	if( tilemap==ALL_TILEMAPS )
/*TODO*///	{
/*TODO*///		tilemap = first_tilemap;
/*TODO*///		while( tilemap )
/*TODO*///		{
/*TODO*///			tilemap_set_flip( tilemap, attributes );
/*TODO*///			tilemap = tilemap->next;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	else if( tilemap->attributes!=attributes )
/*TODO*///	{
/*TODO*///		tilemap->attributes = attributes;
/*TODO*///		tilemap->orientation = Machine->orientation;
/*TODO*///		if( attributes&TILEMAP_FLIPY )
/*TODO*///		{
/*TODO*///			tilemap->orientation ^= ORIENTATION_FLIP_Y;
/*TODO*///		}
/*TODO*///
/*TODO*///		if( attributes&TILEMAP_FLIPX )
/*TODO*///		{
/*TODO*///			tilemap->orientation ^= ORIENTATION_FLIP_X;
/*TODO*///		}
/*TODO*///
/*TODO*///		mappings_update( tilemap );
/*TODO*///		recalculate_scroll( tilemap );
/*TODO*///		tilemap_mark_all_tiles_dirty( tilemap );
/*TODO*///	}
    }

    /**
     * ********************************************************************************
     */
    public static void tilemap_set_scroll_cols(struct_tilemap tilemap, int n) {
        throw new UnsupportedOperationException("Unsupported");
        /*TODO*///	tilemap->logical_scroll_cols = n;
/*TODO*///	if( tilemap->orientation & ORIENTATION_SWAP_XY )
/*TODO*///	{
/*TODO*///		tilemap->cached_scroll_rows = n;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		tilemap->cached_scroll_cols = n;
/*TODO*///	}
    }

    /*TODO*///
/*TODO*///void tilemap_set_scroll_rows( struct tilemap *tilemap, int n )
/*TODO*///{
/*TODO*///	tilemap->logical_scroll_rows = n;
/*TODO*///	if( tilemap->orientation & ORIENTATION_SWAP_XY )
/*TODO*///	{
/*TODO*///		tilemap->cached_scroll_cols = n;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		tilemap->cached_scroll_rows = n;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*///
    public static void tilemap_mark_tile_dirty(struct_tilemap tilemap, int memory_offset) {
        throw new UnsupportedOperationException("Unsupported");
        /*TODO*///	if( memory_offset<tilemap->max_memory_offset )
/*TODO*///	{
/*TODO*///		int cached_indx = tilemap->memory_offset_to_cached_indx[memory_offset];
/*TODO*///		if( cached_indx>=0 )
/*TODO*///		{
/*TODO*///			tilemap->transparency_data[cached_indx] = TILE_FLAG_DIRTY;
/*TODO*///			tilemap->all_tiles_clean = 0;
/*TODO*///		}
/*TODO*///	}
    }

    public static void tilemap_mark_all_tiles_dirty(struct_tilemap tilemap) {
        throw new UnsupportedOperationException("Unsupported");
        /*TODO*///	if( tilemap==ALL_TILEMAPS )
/*TODO*///	{
/*TODO*///		tilemap = first_tilemap;
/*TODO*///		while( tilemap )
/*TODO*///		{
/*TODO*///			tilemap_mark_all_tiles_dirty( tilemap );
/*TODO*///			tilemap = tilemap->next;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		tilemap->all_tiles_dirty = 1;
/*TODO*///		tilemap->all_tiles_clean = 0;
/*TODO*///	}
    }

    /*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*///
/*TODO*///static void update_tile_info( struct tilemap *tilemap, UINT32 cached_indx, UINT32 col, UINT32 row )
/*TODO*///{
/*TODO*///	UINT32 x0;
/*TODO*///	UINT32 y0;
/*TODO*///	UINT32 memory_offset;
/*TODO*///	UINT32 flags;
/*TODO*///
/*TODO*///profiler_mark(PROFILER_TILEMAP_UPDATE);
/*TODO*///
/*TODO*///	memory_offset = tilemap->cached_indx_to_memory_offset[cached_indx];
/*TODO*///	tilemap->tile_get_info( memory_offset );
/*TODO*///	flags = tile_info.flags;
/*TODO*///	flags = (flags&0xfc)|tilemap->logical_flip_to_cached_flip[flags&0x3];
/*TODO*///	x0 = tilemap->cached_tile_width*col;
/*TODO*///	y0 = tilemap->cached_tile_height*row;
/*TODO*///
/*TODO*///	tilemap->transparency_data[cached_indx] = tilemap->draw_tile(tilemap,x0,y0,flags );
/*TODO*///
/*TODO*///profiler_mark(PROFILER_END);
/*TODO*///}
/*TODO*///
/*TODO*///struct mame_bitmap *tilemap_get_pixmap( struct tilemap * tilemap )
/*TODO*///{
/*TODO*///	UINT32 cached_indx = 0;
/*TODO*///	UINT32 row,col;
/*TODO*///
/*TODO*///	if (tilemap->all_tiles_clean == 0)
/*TODO*///	{
/*TODO*///profiler_mark(PROFILER_TILEMAP_DRAW);
/*TODO*///
/*TODO*///		/* if the whole map is dirty, mark it as such */
/*TODO*///		if (tilemap->all_tiles_dirty)
/*TODO*///		{
/*TODO*///			memset( tilemap->transparency_data, TILE_FLAG_DIRTY, tilemap->num_tiles );
/*TODO*///			tilemap->all_tiles_dirty = 0;
/*TODO*///		}
/*TODO*///
/*TODO*///		memset( &tile_info, 0x00, sizeof(tile_info) ); /* initialize defaults */
/*TODO*///
/*TODO*///		/* walk over cached rows/cols (better to walk screen coords) */
/*TODO*///		for( row=0; row<tilemap->num_cached_rows; row++ )
/*TODO*///		{
/*TODO*///			for( col=0; col<tilemap->num_cached_cols; col++ )
/*TODO*///			{
/*TODO*///				if( tilemap->transparency_data[cached_indx] == TILE_FLAG_DIRTY )
/*TODO*///				{
/*TODO*///					update_tile_info( tilemap, cached_indx, col, row );
/*TODO*///				}
/*TODO*///				cached_indx++;
/*TODO*///			} /* next col */
/*TODO*///		} /* next row */
/*TODO*///
/*TODO*///		tilemap->all_tiles_clean = 1;
/*TODO*///
/*TODO*///profiler_mark(PROFILER_END);
/*TODO*///	}
/*TODO*///
/*TODO*///	return tilemap->pixmap;
/*TODO*///}
/*TODO*///
/*TODO*///struct mame_bitmap *tilemap_get_transparency_bitmap( struct tilemap * tilemap )
/*TODO*///{
/*TODO*///	return tilemap->transparency_bitmap;
/*TODO*///}
/*TODO*///
/*TODO*///UINT8 *tilemap_get_transparency_data( struct tilemap * tilemap ) //*
/*TODO*///{
/*TODO*///	return tilemap->transparency_data;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*///
/*TODO*///static void
/*TODO*///recalculate_scroll( struct tilemap *tilemap )
/*TODO*///{
/*TODO*///	int i;
/*TODO*///
/*TODO*///	tilemap->scrollx_delta = (tilemap->attributes & TILEMAP_FLIPX )?tilemap->dx_if_flipped:tilemap->dx;
/*TODO*///	tilemap->scrolly_delta = (tilemap->attributes & TILEMAP_FLIPY )?tilemap->dy_if_flipped:tilemap->dy;
/*TODO*///
/*TODO*///	for( i=0; i<tilemap->logical_scroll_rows; i++ )
/*TODO*///	{
/*TODO*///		tilemap_set_scrollx( tilemap, i, tilemap->logical_rowscroll[i] );
/*TODO*///	}
/*TODO*///	for( i=0; i<tilemap->logical_scroll_cols; i++ )
/*TODO*///	{
/*TODO*///		tilemap_set_scrolly( tilemap, i, tilemap->logical_colscroll[i] );
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///void
/*TODO*///tilemap_set_scrolldx( struct tilemap *tilemap, int dx, int dx_if_flipped )
/*TODO*///{
/*TODO*///	tilemap->dx = dx;
/*TODO*///	tilemap->dx_if_flipped = dx_if_flipped;
/*TODO*///	recalculate_scroll( tilemap );
/*TODO*///}
/*TODO*///
/*TODO*///void
/*TODO*///tilemap_set_scrolldy( struct tilemap *tilemap, int dy, int dy_if_flipped )
/*TODO*///{
/*TODO*///	tilemap->dy = dy;
/*TODO*///	tilemap->dy_if_flipped = dy_if_flipped;
/*TODO*///	recalculate_scroll( tilemap );
/*TODO*///}
/*TODO*///
    public static void tilemap_set_scrollx(struct_tilemap tilemap, int which, int value) {
        throw new UnsupportedOperationException("Unsupported");
        /*TODO*///	tilemap->logical_rowscroll[which] = value;
/*TODO*///	value = tilemap->scrollx_delta-value; /* adjust */
/*TODO*///
/*TODO*///	if( tilemap->orientation & ORIENTATION_SWAP_XY )
/*TODO*///	{
/*TODO*///		/* if xy are swapped, we are actually panning the screen bitmap vertically */
/*TODO*///		if( tilemap->orientation & ORIENTATION_FLIP_X )
/*TODO*///		{
/*TODO*///			/* adjust affected col */
/*TODO*///			which = tilemap->cached_scroll_cols-1 - which;
/*TODO*///		}
/*TODO*///		if( tilemap->orientation & ORIENTATION_FLIP_Y )
/*TODO*///		{
/*TODO*///			/* adjust scroll amount */
/*TODO*///			value = screen_height-tilemap->cached_height-value;
/*TODO*///		}
/*TODO*///		tilemap->cached_colscroll[which] = value;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		if( tilemap->orientation & ORIENTATION_FLIP_Y )
/*TODO*///		{
/*TODO*///			/* adjust affected row */
/*TODO*///			which = tilemap->cached_scroll_rows-1 - which;
/*TODO*///		}
/*TODO*///		if( tilemap->orientation & ORIENTATION_FLIP_X )
/*TODO*///		{
/*TODO*///			/* adjust scroll amount */
/*TODO*///			value = screen_width-tilemap->cached_width-value;
/*TODO*///		}
/*TODO*///		tilemap->cached_rowscroll[which] = value;
/*TODO*///	}
    }

    public static void tilemap_set_scrolly(struct_tilemap tilemap, int which, int value) {
        throw new UnsupportedOperationException("Unsupported");
        /*TODO*///	tilemap->logical_colscroll[which] = value;
/*TODO*///	value = tilemap->scrolly_delta - value; /* adjust */
/*TODO*///
/*TODO*///	if( tilemap->orientation & ORIENTATION_SWAP_XY )
/*TODO*///	{
/*TODO*///		/* if xy are swapped, we are actually panning the screen bitmap horizontally */
/*TODO*///		if( tilemap->orientation & ORIENTATION_FLIP_Y )
/*TODO*///		{
/*TODO*///			/* adjust affected row */
/*TODO*///			which = tilemap->cached_scroll_rows-1 - which;
/*TODO*///		}
/*TODO*///		if( tilemap->orientation & ORIENTATION_FLIP_X )
/*TODO*///		{
/*TODO*///			/* adjust scroll amount */
/*TODO*///			value = screen_width-tilemap->cached_width-value;
/*TODO*///		}
/*TODO*///		tilemap->cached_rowscroll[which] = value;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		if( tilemap->orientation & ORIENTATION_FLIP_X )
/*TODO*///		{
/*TODO*///			/* adjust affected col */
/*TODO*///			which = tilemap->cached_scroll_cols-1 - which;
/*TODO*///		}
/*TODO*///		if( tilemap->orientation & ORIENTATION_FLIP_Y )
/*TODO*///		{
/*TODO*///			/* adjust scroll amount */
/*TODO*///			value = screen_height-tilemap->cached_height-value;
/*TODO*///		}
/*TODO*///		tilemap->cached_colscroll[which] = value;
/*TODO*///	}
    }

    /*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*///
/*TODO*///void tilemap_set_palette_offset( struct tilemap *tilemap, int offset )
/*TODO*///{
/*TODO*///	tilemap->palette_offset = offset;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*///
    public static void tilemap_draw(mame_bitmap dest, rectangle cliprect, struct_tilemap tilemap, int/*UINT32*/ flags, int/*UINT32*/ priority) {
        throw new UnsupportedOperationException("Unsupported");
        /*TODO*///	tilemap_draw_func drawfunc = pick_draw_func(dest);
/*TODO*///	int xpos,ypos,mask,value;
/*TODO*///	int rows, cols;
/*TODO*///	const int *rowscroll, *colscroll;
/*TODO*///	int left, right, top, bottom;
/*TODO*///
/*TODO*///profiler_mark(PROFILER_TILEMAP_DRAW);
/*TODO*///	if( tilemap->enable )
/*TODO*///	{
/*TODO*///		/* scroll registers */
/*TODO*///		rows		= tilemap->cached_scroll_rows;
/*TODO*///		cols		= tilemap->cached_scroll_cols;
/*TODO*///		rowscroll	= tilemap->cached_rowscroll;
/*TODO*///		colscroll	= tilemap->cached_colscroll;
/*TODO*///
/*TODO*///		/* clipping */
/*TODO*///		if( cliprect )
/*TODO*///		{
/*TODO*///			left	= cliprect->min_x;
/*TODO*///			top		= cliprect->min_y;
/*TODO*///			right	= cliprect->max_x+1;
/*TODO*///			bottom	= cliprect->max_y+1;
/*TODO*///
/*TODO*///			if( Machine->orientation & ORIENTATION_SWAP_XY )
/*TODO*///			{
/*TODO*///				SWAP(left,top)
/*TODO*///				SWAP(right,bottom)
/*TODO*///			}
/*TODO*///
/*TODO*///			if( Machine->orientation & ORIENTATION_FLIP_X )
/*TODO*///			{
/*TODO*///				SWAP(left,right)
/*TODO*///				left	= screen_width-left;
/*TODO*///				right	= screen_width-right;
/*TODO*///			}
/*TODO*///
/*TODO*///			if( Machine->orientation & ORIENTATION_FLIP_Y )
/*TODO*///			{
/*TODO*///				SWAP(top,bottom)
/*TODO*///				top		= screen_height-top;
/*TODO*///				bottom	= screen_height-bottom;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			left	= 0;
/*TODO*///			top		= 0;
/*TODO*///			right	= tilemap->cached_width;
/*TODO*///			bottom	= tilemap->cached_height;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* tile priority */
/*TODO*///		mask		= TILE_FLAG_TILE_PRIORITY;
/*TODO*///		value		= TILE_FLAG_TILE_PRIORITY&flags;
/*TODO*///
/*TODO*///		/* initialize defaults */
/*TODO*///		memset( &tile_info, 0x00, sizeof(tile_info) );
/*TODO*///
/*TODO*///		/* if the whole map is dirty, mark it as such */
/*TODO*///		if (tilemap->all_tiles_dirty)
/*TODO*///		{
/*TODO*///			memset( tilemap->transparency_data, TILE_FLAG_DIRTY, tilemap->num_tiles );
/*TODO*///			tilemap->all_tiles_dirty = 0;
/*TODO*///		}
/*TODO*///
/*TODO*///		/* priority_bitmap_pitch_row is tilemap-specific */
/*TODO*///		priority_bitmap_pitch_row = priority_bitmap_pitch_line*tilemap->cached_tile_height;
/*TODO*///
/*TODO*///		blit.screen_bitmap = dest;
/*TODO*///		if( dest == NULL )
/*TODO*///		{
/*TODO*///			blit.draw_masked = (blitmask_t)pit;
/*TODO*///			blit.draw_opaque = (blitopaque_t)pio;
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			blit.screen_bitmap_pitch_line = ((UINT8 *)dest->line[1]) - ((UINT8 *)dest->line[0]);
/*TODO*///			switch( dest->depth )
/*TODO*///			{
/*TODO*///			case 32:
/*TODO*///				if (priority)
/*TODO*///				{
/*TODO*///					if( flags&TILEMAP_ALPHA )
/*TODO*///					{
/*TODO*///						blit.draw_masked = (blitmask_t)pbt32;
/*TODO*///						blit.draw_opaque = (blitopaque_t)pbo32;
/*TODO*///					}
/*TODO*///					else
/*TODO*///					{
/*TODO*///						blit.draw_masked = (blitmask_t)pdt32;
/*TODO*///						blit.draw_opaque = (blitopaque_t)pdo32;
/*TODO*///					}
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					//* AAT APR2003: added 32-bit no-priority counterpart
/*TODO*///					if( flags&TILEMAP_ALPHA )
/*TODO*///					{
/*TODO*///						blit.draw_masked = (blitmask_t)npbt32;
/*TODO*///						blit.draw_opaque = (blitopaque_t)npbo32;
/*TODO*///					}
/*TODO*///					else
/*TODO*///					{
/*TODO*///						blit.draw_masked = (blitmask_t)npdt32;
/*TODO*///						blit.draw_opaque = (blitopaque_t)npdo32;
/*TODO*///					}
/*TODO*///				}
/*TODO*///				blit.screen_bitmap_pitch_line /= 4;
/*TODO*///				break;
/*TODO*///			case 15:
/*TODO*///				if( flags&TILEMAP_ALPHA )
/*TODO*///				{
/*TODO*///					blit.draw_masked = (blitmask_t)pbt15;
/*TODO*///					blit.draw_opaque = (blitopaque_t)pbo15;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					blit.draw_masked = (blitmask_t)pdt15;
/*TODO*///					blit.draw_opaque = (blitopaque_t)pdo15;
/*TODO*///				}
/*TODO*///				blit.screen_bitmap_pitch_line /= 2;
/*TODO*///				break;
/*TODO*///
/*TODO*///			case 16:
/*TODO*///				if (tilemap->palette_offset)
/*TODO*///				{
/*TODO*///					blit.draw_masked = (blitmask_t)pdt16pal;
/*TODO*///					blit.draw_opaque = (blitopaque_t)pdo16pal;
/*TODO*///				}
/*TODO*///				else if (priority)
/*TODO*///				{
/*TODO*///					blit.draw_masked = (blitmask_t)pdt16;
/*TODO*///					blit.draw_opaque = (blitopaque_t)pdo16;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					blit.draw_masked = (blitmask_t)pdt16np;
/*TODO*///					blit.draw_opaque = (blitopaque_t)pdo16np;
/*TODO*///				}
/*TODO*///				blit.screen_bitmap_pitch_line /= 2;
/*TODO*///				break;
/*TODO*///
/*TODO*///			default:
/*TODO*///				exit(1);
/*TODO*///				break;
/*TODO*///			}
/*TODO*///			blit.screen_bitmap_pitch_row = blit.screen_bitmap_pitch_line*tilemap->cached_tile_height;
/*TODO*///		} /* dest == bitmap */
/*TODO*///
/*TODO*///		if( !(tilemap->type==TILEMAP_OPAQUE || (flags&TILEMAP_IGNORE_TRANSPARENCY)) )
/*TODO*///		{
/*TODO*///			if( flags&TILEMAP_BACK )
/*TODO*///			{
/*TODO*///				mask	|= TILE_FLAG_BG_OPAQUE;
/*TODO*///				value	|= TILE_FLAG_BG_OPAQUE;
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				mask	|= TILE_FLAG_FG_OPAQUE;
/*TODO*///				value	|= TILE_FLAG_FG_OPAQUE;
/*TODO*///			}
/*TODO*///		}
/*TODO*///
/*TODO*///		blit.tilemap_priority_code = (priority & 0xffff) | (tilemap->palette_offset << 16);
/*TODO*///
/*TODO*///		if( rows == 1 && cols == 1 )
/*TODO*///		{ /* XY scrolling playfield */
/*TODO*///			int scrollx = rowscroll[0];
/*TODO*///			int scrolly = colscroll[0];
/*TODO*///
/*TODO*///			if( scrollx < 0 )
/*TODO*///			{
/*TODO*///				scrollx = tilemap->cached_width - (-scrollx) % tilemap->cached_width;
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				scrollx = scrollx % tilemap->cached_width;
/*TODO*///			}
/*TODO*///
/*TODO*///			if( scrolly < 0 )
/*TODO*///			{
/*TODO*///				scrolly = tilemap->cached_height - (-scrolly) % tilemap->cached_height;
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				scrolly = scrolly % tilemap->cached_height;
/*TODO*///			}
/*TODO*///
/*TODO*///	 		blit.clip_left		= left;
/*TODO*///	 		blit.clip_top		= top;
/*TODO*///	 		blit.clip_right		= right;
/*TODO*///	 		blit.clip_bottom	= bottom;
/*TODO*///
/*TODO*///			for(
/*TODO*///				ypos = scrolly - tilemap->cached_height;
/*TODO*///				ypos < blit.clip_bottom;
/*TODO*///				ypos += tilemap->cached_height )
/*TODO*///			{
/*TODO*///				for(
/*TODO*///					xpos = scrollx - tilemap->cached_width;
/*TODO*///					xpos < blit.clip_right;
/*TODO*///					xpos += tilemap->cached_width )
/*TODO*///				{
/*TODO*///					drawfunc( tilemap, xpos, ypos, mask, value );
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else if( rows == 1 )
/*TODO*///		{ /* scrolling columns + horizontal scroll */
/*TODO*///			int col = 0;
/*TODO*///			int colwidth = tilemap->cached_width / cols;
/*TODO*///			int scrollx = rowscroll[0];
/*TODO*///
/*TODO*///			if( scrollx < 0 )
/*TODO*///			{
/*TODO*///				scrollx = tilemap->cached_width - (-scrollx) % tilemap->cached_width;
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				scrollx = scrollx % tilemap->cached_width;
/*TODO*///			}
/*TODO*///
/*TODO*///			blit.clip_top		= top;
/*TODO*///			blit.clip_bottom	= bottom;
/*TODO*///
/*TODO*///			while( col < cols )
/*TODO*///			{
/*TODO*///				int cons	= 1;
/*TODO*///				int scrolly	= colscroll[col];
/*TODO*///
/*TODO*///	 			/* count consecutive columns scrolled by the same amount */
/*TODO*///				if( scrolly != TILE_LINE_DISABLED )
/*TODO*///				{
/*TODO*///					while( col + cons < cols &&	colscroll[col + cons] == scrolly ) cons++;
/*TODO*///
/*TODO*///					if( scrolly < 0 )
/*TODO*///					{
/*TODO*///						scrolly = tilemap->cached_height - (-scrolly) % tilemap->cached_height;
/*TODO*///					}
/*TODO*///					else
/*TODO*///					{
/*TODO*///						scrolly %= tilemap->cached_height;
/*TODO*///					}
/*TODO*///
/*TODO*///					blit.clip_left = col * colwidth + scrollx;
/*TODO*///					if (blit.clip_left < left) blit.clip_left = left;
/*TODO*///					blit.clip_right = (col + cons) * colwidth + scrollx;
/*TODO*///					if (blit.clip_right > right) blit.clip_right = right;
/*TODO*///
/*TODO*///					for(
/*TODO*///						ypos = scrolly - tilemap->cached_height;
/*TODO*///						ypos < blit.clip_bottom;
/*TODO*///						ypos += tilemap->cached_height )
/*TODO*///					{
/*TODO*///						drawfunc( tilemap, scrollx, ypos, mask, value );
/*TODO*///					}
/*TODO*///
/*TODO*///					blit.clip_left = col * colwidth + scrollx - tilemap->cached_width;
/*TODO*///					if (blit.clip_left < left) blit.clip_left = left;
/*TODO*///					blit.clip_right = (col + cons) * colwidth + scrollx - tilemap->cached_width;
/*TODO*///					if (blit.clip_right > right) blit.clip_right = right;
/*TODO*///
/*TODO*///					for(
/*TODO*///						ypos = scrolly - tilemap->cached_height;
/*TODO*///						ypos < blit.clip_bottom;
/*TODO*///						ypos += tilemap->cached_height )
/*TODO*///					{
/*TODO*///						drawfunc( tilemap, scrollx - tilemap->cached_width, ypos, mask, value );
/*TODO*///					}
/*TODO*///				}
/*TODO*///				col += cons;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else if( cols == 1 )
/*TODO*///		{ /* scrolling rows + vertical scroll */
/*TODO*///			int row = 0;
/*TODO*///			int rowheight = tilemap->cached_height / rows;
/*TODO*///			int scrolly = colscroll[0];
/*TODO*///			if( scrolly < 0 )
/*TODO*///			{
/*TODO*///				scrolly = tilemap->cached_height - (-scrolly) % tilemap->cached_height;
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				scrolly = scrolly % tilemap->cached_height;
/*TODO*///			}
/*TODO*///			blit.clip_left = left;
/*TODO*///			blit.clip_right = right;
/*TODO*///			while( row < rows )
/*TODO*///			{
/*TODO*///				int cons = 1;
/*TODO*///				int scrollx = rowscroll[row];
/*TODO*///				/* count consecutive rows scrolled by the same amount */
/*TODO*///				if( scrollx != TILE_LINE_DISABLED )
/*TODO*///				{
/*TODO*///					while( row + cons < rows &&	rowscroll[row + cons] == scrollx ) cons++;
/*TODO*///					if( scrollx < 0)
/*TODO*///					{
/*TODO*///						scrollx = tilemap->cached_width - (-scrollx) % tilemap->cached_width;
/*TODO*///					}
/*TODO*///					else
/*TODO*///					{
/*TODO*///						scrollx %= tilemap->cached_width;
/*TODO*///					}
/*TODO*///					blit.clip_top = row * rowheight + scrolly;
/*TODO*///					if (blit.clip_top < top) blit.clip_top = top;
/*TODO*///					blit.clip_bottom = (row + cons) * rowheight + scrolly;
/*TODO*///					if (blit.clip_bottom > bottom) blit.clip_bottom = bottom;
/*TODO*///					for(
/*TODO*///						xpos = scrollx - tilemap->cached_width;
/*TODO*///						xpos < blit.clip_right;
/*TODO*///						xpos += tilemap->cached_width )
/*TODO*///					{
/*TODO*///						drawfunc( tilemap, xpos, scrolly, mask, value );
/*TODO*///					}
/*TODO*///					blit.clip_top = row * rowheight + scrolly - tilemap->cached_height;
/*TODO*///					if (blit.clip_top < top) blit.clip_top = top;
/*TODO*///					blit.clip_bottom = (row + cons) * rowheight + scrolly - tilemap->cached_height;
/*TODO*///					if (blit.clip_bottom > bottom) blit.clip_bottom = bottom;
/*TODO*///					for(
/*TODO*///						xpos = scrollx - tilemap->cached_width;
/*TODO*///						xpos < blit.clip_right;
/*TODO*///						xpos += tilemap->cached_width )
/*TODO*///					{
/*TODO*///						drawfunc( tilemap, xpos, scrolly - tilemap->cached_height, mask, value );
/*TODO*///					}
/*TODO*///				}
/*TODO*///				row += cons;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///profiler_mark(PROFILER_END);
    }
    /*TODO*///
/*TODO*////* notes:
/*TODO*///   - startx and starty MUST be UINT32 for calculations to work correctly
/*TODO*///   - srcbitmap->width and height are assumed to be a power of 2 to speed up wraparound
/*TODO*///   */
/*TODO*///void tilemap_draw_roz( struct mame_bitmap *dest,const struct rectangle *cliprect,struct tilemap *tilemap,
/*TODO*///		UINT32 startx,UINT32 starty,int incxx,int incxy,int incyx,int incyy,
/*TODO*///		int wraparound,
/*TODO*///		UINT32 flags, UINT32 priority )
/*TODO*///{
/*TODO*///	if( (incxx == 1<<16) && !incxy & !incyx && (incyy == 1<<16) && wraparound )
/*TODO*///	{
/*TODO*///		tilemap_set_scrollx( tilemap, 0, startx >> 16 );
/*TODO*///		tilemap_set_scrolly( tilemap, 0, starty >> 16 );
/*TODO*///		tilemap_draw( dest, cliprect, tilemap, flags, priority );
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		int mask,value;
/*TODO*///
/*TODO*///profiler_mark(PROFILER_TILEMAP_DRAW_ROZ);
/*TODO*///		if( tilemap->enable )
/*TODO*///		{
/*TODO*///			/* tile priority */
/*TODO*///			mask		= TILE_FLAG_TILE_PRIORITY;
/*TODO*///			value		= TILE_FLAG_TILE_PRIORITY&flags;
/*TODO*///
/*TODO*///			tilemap_get_pixmap( tilemap ); /* force update */
/*TODO*///
/*TODO*///			if( !(tilemap->type==TILEMAP_OPAQUE || (flags&TILEMAP_IGNORE_TRANSPARENCY)) )
/*TODO*///			{
/*TODO*///				if( flags&TILEMAP_BACK )
/*TODO*///				{
/*TODO*///					mask	|= TILE_FLAG_BG_OPAQUE;
/*TODO*///					value	|= TILE_FLAG_BG_OPAQUE;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					mask	|= TILE_FLAG_FG_OPAQUE;
/*TODO*///					value	|= TILE_FLAG_FG_OPAQUE;
/*TODO*///				}
/*TODO*///			}
/*TODO*///
/*TODO*///			switch( dest->depth )
/*TODO*///			{
/*TODO*///
/*TODO*///			case 32:
/*TODO*///				copyroz_core32BPP(dest,tilemap,startx,starty,incxx,incxy,incyx,incyy,
/*TODO*///					wraparound,cliprect,mask,value,priority,tilemap->palette_offset);
/*TODO*///				break;
/*TODO*///
/*TODO*///			case 15:
/*TODO*///			case 16:
/*TODO*///				copyroz_core16BPP(dest,tilemap,startx,starty,incxx,incxy,incyx,incyy,
/*TODO*///					wraparound,cliprect,mask,value,priority,tilemap->palette_offset);
/*TODO*///				break;
/*TODO*///
/*TODO*///			default:
/*TODO*///				exit(1);
/*TODO*///			}
/*TODO*///		} /* tilemap->enable */
/*TODO*///profiler_mark(PROFILER_END);
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///UINT32 tilemap_count( void )
/*TODO*///{
/*TODO*///	UINT32 count = 0;
/*TODO*///	struct tilemap *tilemap = first_tilemap;
/*TODO*///	while( tilemap )
/*TODO*///	{
/*TODO*///		count++;
/*TODO*///		tilemap = tilemap->next;
/*TODO*///	}
/*TODO*///	return count;
/*TODO*///}
/*TODO*///
/*TODO*///static struct tilemap *tilemap_nb_find( int number )
/*TODO*///{
/*TODO*///	int count = 0;
/*TODO*///	struct tilemap *tilemap;
/*TODO*///
/*TODO*///	tilemap = first_tilemap;
/*TODO*///	while( tilemap )
/*TODO*///	{
/*TODO*///		count++;
/*TODO*///		tilemap = tilemap->next;
/*TODO*///	}
/*TODO*///
/*TODO*///	number = (count-1)-number;
/*TODO*///
/*TODO*///	tilemap = first_tilemap;
/*TODO*///	while( number-- )
/*TODO*///	{
/*TODO*///		tilemap = tilemap->next;
/*TODO*///	}
/*TODO*///	return tilemap;
/*TODO*///}
/*TODO*///
/*TODO*///void tilemap_nb_size( UINT32 number, UINT32 *width, UINT32 *height )
/*TODO*///{
/*TODO*///	struct tilemap *tilemap = tilemap_nb_find( number );
/*TODO*///	*width  = tilemap->cached_width;
/*TODO*///	*height = tilemap->cached_height;
/*TODO*///}
/*TODO*///
/*TODO*///void tilemap_nb_draw( struct mame_bitmap *dest, UINT32 number, UINT32 scrollx, UINT32 scrolly )
/*TODO*///{
/*TODO*///	tilemap_draw_func drawfunc = pick_draw_func(dest);
/*TODO*///	int xpos,ypos;
/*TODO*///	struct tilemap *tilemap = tilemap_nb_find( number );
/*TODO*///
/*TODO*///	blit.screen_bitmap = dest;
/*TODO*///	blit.screen_bitmap_pitch_line = ((UINT8 *)dest->line[1]) - ((UINT8 *)dest->line[0]);
/*TODO*///	switch( dest->depth )
/*TODO*///	{
/*TODO*///	case 32:
/*TODO*///		blit.draw_opaque = (blitopaque_t)pdo32;
/*TODO*///		blit.screen_bitmap_pitch_line /= 4;
/*TODO*///		break;
/*TODO*///
/*TODO*///	case 15:
/*TODO*///		blit.draw_opaque = (blitopaque_t)pdo15;
/*TODO*///		blit.screen_bitmap_pitch_line /= 2;
/*TODO*///		break;
/*TODO*///
/*TODO*///	case 16:
/*TODO*///		blit.draw_opaque = (blitopaque_t)pdo16pal;
/*TODO*///		blit.screen_bitmap_pitch_line /= 2;
/*TODO*///		break;
/*TODO*///
/*TODO*///	default:
/*TODO*///		exit(1);
/*TODO*///		break;
/*TODO*///	}
/*TODO*///	priority_bitmap_pitch_row = priority_bitmap_pitch_line*tilemap->cached_tile_height;
/*TODO*///	blit.screen_bitmap_pitch_row = blit.screen_bitmap_pitch_line*tilemap->cached_tile_height;
/*TODO*///	blit.tilemap_priority_code = (tilemap->palette_offset << 16);
/*TODO*///	scrollx = tilemap->cached_width  - scrollx % tilemap->cached_width;
/*TODO*///	scrolly = tilemap->cached_height - scrolly % tilemap->cached_height;
/*TODO*///
/*TODO*///	blit.clip_left		= 0;
/*TODO*///	blit.clip_top		= 0;
/*TODO*///	blit.clip_right		= (dest->width < tilemap->cached_width) ? dest->width : tilemap->cached_width;
/*TODO*///	blit.clip_bottom	= (dest->height < tilemap->cached_height) ? dest->height : tilemap->cached_height;
/*TODO*///
/*TODO*///	for(
/*TODO*///		ypos = scrolly - tilemap->cached_height;
/*TODO*///		ypos < blit.clip_bottom;
/*TODO*///		ypos += tilemap->cached_height )
/*TODO*///	{
/*TODO*///		for(
/*TODO*///			xpos = scrollx - tilemap->cached_width;
/*TODO*///			xpos < blit.clip_right;
/*TODO*///			xpos += tilemap->cached_width )
/*TODO*///		{
/*TODO*///			drawfunc( tilemap, xpos, ypos, 0, 0 );
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*////***********************************************************************************/
/*TODO*///
/*TODO*///#endif // !DECLARE && !TRANSP
/*TODO*///
/*TODO*///#ifdef DECLARE
/*TODO*///
/*TODO*///DECLARE(copyroz_core,(struct mame_bitmap *bitmap,struct tilemap *tilemap,
/*TODO*///		UINT32 startx,UINT32 starty,int incxx,int incxy,int incyx,int incyy,int wraparound,
/*TODO*///		const struct rectangle *clip,
/*TODO*///		int mask,int value,
/*TODO*///		UINT32 priority,UINT32 palette_offset),
/*TODO*///{
/*TODO*///	UINT32 cx;
/*TODO*///	UINT32 cy;
/*TODO*///	int x;
/*TODO*///	int sx;
/*TODO*///	int sy;
/*TODO*///	int ex;
/*TODO*///	int ey;
/*TODO*///	struct mame_bitmap *srcbitmap = tilemap->pixmap;
/*TODO*///	struct mame_bitmap *transparency_bitmap = tilemap->transparency_bitmap;
/*TODO*///	const int xmask = srcbitmap->width-1;
/*TODO*///	const int ymask = srcbitmap->height-1;
/*TODO*///	const int widthshifted = srcbitmap->width << 16;
/*TODO*///	const int heightshifted = srcbitmap->height << 16;
/*TODO*///	DATA_TYPE *dest;
/*TODO*///	UINT8 *pri;
/*TODO*///	const UINT16 *src;
/*TODO*///	const UINT8 *pMask;
/*TODO*///
/*TODO*///	if (clip)
/*TODO*///	{
/*TODO*///		startx += clip->min_x * incxx + clip->min_y * incyx;
/*TODO*///		starty += clip->min_x * incxy + clip->min_y * incyy;
/*TODO*///
/*TODO*///		sx = clip->min_x;
/*TODO*///		sy = clip->min_y;
/*TODO*///		ex = clip->max_x;
/*TODO*///		ey = clip->max_y;
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		sx = 0;
/*TODO*///		sy = 0;
/*TODO*///		ex = bitmap->width-1;
/*TODO*///		ey = bitmap->height-1;
/*TODO*///	}
/*TODO*///
/*TODO*///
/*TODO*///	if (Machine->orientation & ORIENTATION_SWAP_XY)
/*TODO*///	{
/*TODO*///		int t;
/*TODO*///
/*TODO*///		t = startx; startx = starty; starty = t;
/*TODO*///		t = sx; sx = sy; sy = t;
/*TODO*///		t = ex; ex = ey; ey = t;
/*TODO*///		t = incxx; incxx = incyy; incyy = t;
/*TODO*///		t = incxy; incxy = incyx; incyx = t;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (Machine->orientation & ORIENTATION_FLIP_X)
/*TODO*///	{
/*TODO*///		int w = ex - sx;
/*TODO*///
/*TODO*///		incxy = -incxy;
/*TODO*///		incyx = -incyx;
/*TODO*///		startx = widthshifted - startx - 1;
/*TODO*///		startx -= incxx * w;
/*TODO*///		starty -= incxy * w;
/*TODO*///
/*TODO*///		w = sx;
/*TODO*///		sx = bitmap->width-1 - ex;
/*TODO*///		ex = bitmap->width-1 - w;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (Machine->orientation & ORIENTATION_FLIP_Y)
/*TODO*///	{
/*TODO*///		int h = ey - sy;
/*TODO*///
/*TODO*///		incxy = -incxy;
/*TODO*///		incyx = -incyx;
/*TODO*///		starty = heightshifted - starty - 1;
/*TODO*///		startx -= incyx * h;
/*TODO*///		starty -= incyy * h;
/*TODO*///
/*TODO*///		h = sy;
/*TODO*///		sy = bitmap->height-1 - ey;
/*TODO*///		ey = bitmap->height-1 - h;
/*TODO*///	}
/*TODO*///
/*TODO*///	if (incxy == 0 && incyx == 0 && !wraparound)
/*TODO*///	{
/*TODO*///		/* optimized loop for the not rotated case */
/*TODO*///
/*TODO*///		if (incxx == 0x10000)
/*TODO*///		{
/*TODO*///			/* optimized loop for the not zoomed case */
/*TODO*///
/*TODO*///			/* startx is unsigned */
/*TODO*///			startx = ((INT32)startx) >> 16;
/*TODO*///
/*TODO*///			if (startx >= srcbitmap->width)
/*TODO*///			{
/*TODO*///				sx += -startx;
/*TODO*///				startx = 0;
/*TODO*///			}
/*TODO*///
/*TODO*///			if (sx <= ex)
/*TODO*///			{
/*TODO*///				while (sy <= ey)
/*TODO*///				{
/*TODO*///					if (starty < heightshifted)
/*TODO*///					{
/*TODO*///						x = sx;
/*TODO*///						cx = startx;
/*TODO*///						cy = starty >> 16;
/*TODO*///						dest = ((DATA_TYPE *)bitmap->line[sy]) + sx;
/*TODO*///
/*TODO*///						pri = ((UINT8 *)priority_bitmap->line[sy]) + sx;
/*TODO*///						src = (UINT16 *)srcbitmap->line[cy];
/*TODO*///						pMask = (UINT8 *)transparency_bitmap->line[cy];
/*TODO*///
/*TODO*///						while (x <= ex && cx < srcbitmap->width)
/*TODO*///						{
/*TODO*///							if ( (pMask[cx]&mask) == value )
/*TODO*///							{
/*TODO*///								*dest = src[cx]+palette_offset;
/*TODO*///								*pri |= priority;
/*TODO*///							}
/*TODO*///							cx++;
/*TODO*///							x++;
/*TODO*///							dest++;
/*TODO*///							pri++;
/*TODO*///						}
/*TODO*///					}
/*TODO*///					starty += incyy;
/*TODO*///					sy++;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			while (startx >= widthshifted && sx <= ex)
/*TODO*///			{
/*TODO*///				startx += incxx;
/*TODO*///				sx++;
/*TODO*///			}
/*TODO*///
/*TODO*///			if (sx <= ex)
/*TODO*///			{
/*TODO*///				while (sy <= ey)
/*TODO*///				{
/*TODO*///					if (starty < heightshifted)
/*TODO*///					{
/*TODO*///						x = sx;
/*TODO*///						cx = startx;
/*TODO*///						cy = starty >> 16;
/*TODO*///						dest = ((DATA_TYPE *)bitmap->line[sy]) + sx;
/*TODO*///
/*TODO*///						pri = ((UINT8 *)priority_bitmap->line[sy]) + sx;
/*TODO*///						src = (UINT16 *)srcbitmap->line[cy];
/*TODO*///						pMask = (UINT8 *)transparency_bitmap->line[cy];
/*TODO*///						while (x <= ex && cx < widthshifted)
/*TODO*///						{
/*TODO*///							if ( (pMask[cx>>16]&mask) == value )
/*TODO*///							{
/*TODO*///								*dest = src[cx >> 16]+palette_offset;
/*TODO*///								*pri |= priority;
/*TODO*///							}
/*TODO*///							cx += incxx;
/*TODO*///							x++;
/*TODO*///							dest++;
/*TODO*///							pri++;
/*TODO*///						}
/*TODO*///					}
/*TODO*///					starty += incyy;
/*TODO*///					sy++;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		if (wraparound)
/*TODO*///		{
/*TODO*///			/* plot with wraparound */
/*TODO*///			while (sy <= ey)
/*TODO*///			{
/*TODO*///				x = sx;
/*TODO*///				cx = startx;
/*TODO*///				cy = starty;
/*TODO*///				dest = ((DATA_TYPE *)bitmap->line[sy]) + sx;
/*TODO*///				pri = ((UINT8 *)priority_bitmap->line[sy]) + sx;
/*TODO*///				while (x <= ex)
/*TODO*///				{
/*TODO*///					if( (((UINT8 *)transparency_bitmap->line[(cy>>16)&ymask])[(cx>>16)&xmask]&mask) == value )
/*TODO*///					{
/*TODO*///						*dest = ((UINT16 *)srcbitmap->line[(cy >> 16) & ymask])[(cx >> 16) & xmask]+palette_offset;
/*TODO*///						*pri |= priority;
/*TODO*///					}
/*TODO*///					cx += incxx;
/*TODO*///					cy += incxy;
/*TODO*///					x++;
/*TODO*///					dest++;
/*TODO*///					pri++;
/*TODO*///				}
/*TODO*///				startx += incyx;
/*TODO*///				starty += incyy;
/*TODO*///				sy++;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			while (sy <= ey)
/*TODO*///			{
/*TODO*///				x = sx;
/*TODO*///				cx = startx;
/*TODO*///				cy = starty;
/*TODO*///				dest = ((DATA_TYPE *)bitmap->line[sy]) + sx;
/*TODO*///				pri = ((UINT8 *)priority_bitmap->line[sy]) + sx;
/*TODO*///				while (x <= ex)
/*TODO*///				{
/*TODO*///					if (cx < widthshifted && cy < heightshifted)
/*TODO*///					{
/*TODO*///						if( (((UINT8 *)transparency_bitmap->line[cy>>16])[cx>>16]&mask)==value )
/*TODO*///						{
/*TODO*///							*dest = ((UINT16 *)srcbitmap->line[cy >> 16])[cx >> 16]+palette_offset;
/*TODO*///							*pri |= priority;
/*TODO*///						}
/*TODO*///					}
/*TODO*///					cx += incxx;
/*TODO*///					cy += incxy;
/*TODO*///					x++;
/*TODO*///					dest++;
/*TODO*///					pri++;
/*TODO*///				}
/*TODO*///				startx += incyx;
/*TODO*///				starty += incyy;
/*TODO*///				sy++;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///})
/*TODO*///
/*TODO*///#ifndef osd_pend
/*TODO*///#define osd_pend() do { } while (0)
/*TODO*///#endif
/*TODO*///
/*TODO*///DECLARE( draw, (struct tilemap *tilemap, int xpos, int ypos, int mask, int value ),
/*TODO*///{
/*TODO*///	trans_t transPrev;
/*TODO*///	trans_t transCur;
/*TODO*///	const UINT8 *pTrans;
/*TODO*///	UINT32 cached_indx;
/*TODO*///	struct mame_bitmap *screen = blit.screen_bitmap;
/*TODO*///	int tilemap_priority_code = blit.tilemap_priority_code;
/*TODO*///	int x1 = xpos;
/*TODO*///	int y1 = ypos;
/*TODO*///	int x2 = xpos+tilemap->cached_width;
/*TODO*///	int y2 = ypos+tilemap->cached_height;
/*TODO*///	DATA_TYPE *dest_baseaddr = NULL;
/*TODO*///	DATA_TYPE *dest_next;
/*TODO*///	int dy;
/*TODO*///	int count;
/*TODO*///	const UINT16 *source0;
/*TODO*///	DATA_TYPE *dest0;
/*TODO*///	UINT8 *pmap0;
/*TODO*///	int i;
/*TODO*///	int row;
/*TODO*///	int x_start;
/*TODO*///	int x_end;
/*TODO*///	int column;
/*TODO*///	int c1; /* leftmost visible column in source tilemap */
/*TODO*///	int c2; /* rightmost visible column in source tilemap */
/*TODO*///	int y; /* current screen line to render */
/*TODO*///	int y_next;
/*TODO*///	UINT8 *priority_bitmap_baseaddr;
/*TODO*///	UINT8 *priority_bitmap_next;
/*TODO*///	const UINT16 *source_baseaddr;
/*TODO*///	const UINT16 *source_next;
/*TODO*///	const UINT8 *mask0;
/*TODO*///	const UINT8 *mask_baseaddr;
/*TODO*///	const UINT8 *mask_next;
/*TODO*///
/*TODO*///	/* clip source coordinates */
/*TODO*///	if( x1<blit.clip_left ) x1 = blit.clip_left;
/*TODO*///	if( x2>blit.clip_right ) x2 = blit.clip_right;
/*TODO*///	if( y1<blit.clip_top ) y1 = blit.clip_top;
/*TODO*///	if( y2>blit.clip_bottom ) y2 = blit.clip_bottom;
/*TODO*///
/*TODO*///	if( x1<x2 && y1<y2 ) /* do nothing if totally clipped */
/*TODO*///	{
/*TODO*///		priority_bitmap_baseaddr = xpos + (UINT8 *)priority_bitmap->line[y1];
/*TODO*///		if( screen )
/*TODO*///		{
/*TODO*///			dest_baseaddr = xpos + (DATA_TYPE *)screen->line[y1];
/*TODO*///		}
/*TODO*///
/*TODO*///		/* convert screen coordinates to source tilemap coordinates */
/*TODO*///		x1 -= xpos;
/*TODO*///		y1 -= ypos;
/*TODO*///		x2 -= xpos;
/*TODO*///		y2 -= ypos;
/*TODO*///
/*TODO*///		source_baseaddr = (UINT16 *)tilemap->pixmap->line[y1];
/*TODO*///		mask_baseaddr = tilemap->transparency_bitmap->line[y1];
/*TODO*///
/*TODO*///		c1 = x1/tilemap->cached_tile_width; /* round down */
/*TODO*///		c2 = (x2+tilemap->cached_tile_width-1)/tilemap->cached_tile_width; /* round up */
/*TODO*///
/*TODO*///		y = y1;
/*TODO*///		y_next = tilemap->cached_tile_height*(y1/tilemap->cached_tile_height) + tilemap->cached_tile_height;
/*TODO*///		if( y_next>y2 ) y_next = y2;
/*TODO*///
/*TODO*///		dy = y_next-y;
/*TODO*///		dest_next = dest_baseaddr + dy*blit.screen_bitmap_pitch_line;
/*TODO*///		priority_bitmap_next = priority_bitmap_baseaddr + dy*priority_bitmap_pitch_line;
/*TODO*///		source_next = source_baseaddr + dy*tilemap->pixmap_pitch_line;
/*TODO*///		mask_next = mask_baseaddr + dy*tilemap->transparency_bitmap_pitch_line;
/*TODO*///		for(;;)
/*TODO*///		{
/*TODO*///			row = y/tilemap->cached_tile_height;
/*TODO*///			x_start = x1;
/*TODO*///
/*TODO*///			transPrev = eWHOLLY_TRANSPARENT;
/*TODO*///			pTrans = mask_baseaddr + x_start;
/*TODO*///
/*TODO*///			cached_indx = row*tilemap->num_cached_cols + c1;
/*TODO*///			for( column=c1; column<=c2; column++ )
/*TODO*///			{
/*TODO*///				if( column == c2 )
/*TODO*///				{
/*TODO*///					transCur = eWHOLLY_TRANSPARENT;
/*TODO*///					goto L_Skip;
/*TODO*///				}
/*TODO*///
/*TODO*///				if( tilemap->transparency_data[cached_indx]==TILE_FLAG_DIRTY )
/*TODO*///				{
/*TODO*///					update_tile_info( tilemap, cached_indx, column, row );
/*TODO*///				}
/*TODO*///
/*TODO*///				if( (tilemap->transparency_data[cached_indx]&mask)!=0 )
/*TODO*///				{
/*TODO*///					transCur = eMASKED;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					transCur = (((*pTrans)&mask) == value)?eWHOLLY_OPAQUE:eWHOLLY_TRANSPARENT;
/*TODO*///				}
/*TODO*///				pTrans += tilemap->cached_tile_width;
/*TODO*///
/*TODO*///			L_Skip:
/*TODO*///				if( transCur!=transPrev )
/*TODO*///				{
/*TODO*///					x_end = column*tilemap->cached_tile_width;
/*TODO*///					if( x_end<x1 ) x_end = x1;
/*TODO*///					if( x_end>x2 ) x_end = x2;
/*TODO*///
/*TODO*///					if( transPrev != eWHOLLY_TRANSPARENT )
/*TODO*///					{
/*TODO*///						count = x_end - x_start;
/*TODO*///						source0 = source_baseaddr + x_start;
/*TODO*///						dest0 = dest_baseaddr + x_start;
/*TODO*///						pmap0 = priority_bitmap_baseaddr + x_start;
/*TODO*///
/*TODO*///						if( transPrev == eWHOLLY_OPAQUE )
/*TODO*///						{
/*TODO*///							i = y;
/*TODO*///							for(;;)
/*TODO*///							{
/*TODO*///								blit.draw_opaque( dest0, source0, count, pmap0, tilemap_priority_code );
/*TODO*///								if( ++i == y_next ) break;
/*TODO*///
/*TODO*///								dest0 += blit.screen_bitmap_pitch_line;
/*TODO*///								source0 += tilemap->pixmap_pitch_line;
/*TODO*///								pmap0 += priority_bitmap_pitch_line;
/*TODO*///							}
/*TODO*///						} /* transPrev == eWHOLLY_OPAQUE */
/*TODO*///						else /* transPrev == eMASKED */
/*TODO*///						{
/*TODO*///							mask0 = mask_baseaddr + x_start;
/*TODO*///							i = y;
/*TODO*///							for(;;)
/*TODO*///							{
/*TODO*///								blit.draw_masked( dest0, source0, mask0, mask, value, count, pmap0, tilemap_priority_code );
/*TODO*///								if( ++i == y_next ) break;
/*TODO*///
/*TODO*///								dest0 += blit.screen_bitmap_pitch_line;
/*TODO*///								source0 += tilemap->pixmap_pitch_line;
/*TODO*///								mask0 += tilemap->transparency_bitmap_pitch_line;
/*TODO*///								pmap0 += priority_bitmap_pitch_line;
/*TODO*///							}
/*TODO*///						} /* transPrev == eMASKED */
/*TODO*///					} /* transPrev != eWHOLLY_TRANSPARENT */
/*TODO*///					x_start = x_end;
/*TODO*///					transPrev = transCur;
/*TODO*///				}
/*TODO*///				cached_indx++;
/*TODO*///			}
/*TODO*///			if( y_next==y2 ) break; /* we are done! */
/*TODO*///
/*TODO*///			priority_bitmap_baseaddr = priority_bitmap_next;
/*TODO*///			dest_baseaddr = dest_next;
/*TODO*///			source_baseaddr = source_next;
/*TODO*///			mask_baseaddr = mask_next;
/*TODO*///			y = y_next;
/*TODO*///			y_next += tilemap->cached_tile_height;
/*TODO*///
/*TODO*///			if( y_next>=y2 )
/*TODO*///			{
/*TODO*///				y_next = y2;
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				dest_next += blit.screen_bitmap_pitch_row;
/*TODO*///				priority_bitmap_next += priority_bitmap_pitch_row;
/*TODO*///				source_next += tilemap->pixmap_pitch_row;
/*TODO*///				mask_next += tilemap->transparency_bitmap_pitch_row;
/*TODO*///			}
/*TODO*///		} /* process next row */
/*TODO*///	} /* not totally clipped */
/*TODO*///
/*TODO*///	osd_pend();
/*TODO*///})
/*TODO*///
/*TODO*///#undef DATA_TYPE
/*TODO*///#undef DEPTH
/*TODO*///#undef DECLARE
/*TODO*///#endif /* DECLARE */
/*TODO*///
/*TODO*///#ifdef TRANSP
/*TODO*////*************************************************************************************************/
/*TODO*///
/*TODO*////* Each of the following routines draws pixmap and transarency data for a single tile.
/*TODO*/// *
/*TODO*/// * This function returns a per-tile code.  Each bit of this code is 0 if the corresponding
/*TODO*/// * bit is zero in every byte of transparency data in the tile, or 1 if that bit is not
/*TODO*/// * consistant within the tile.
/*TODO*/// *
/*TODO*/// * This precomputed value allows us for any particular tile and mask, to determine if all pixels
/*TODO*/// * in that tile have the same masked transparency value.
/*TODO*/// */
/*TODO*///
/*TODO*///static UINT8 TRANSP(HandleTransparencyBitmask)(struct tilemap *tilemap, UINT32 x0, UINT32 y0, UINT32 flags)
/*TODO*///{
/*TODO*///	UINT32 tile_width = tilemap->cached_tile_width;
/*TODO*///	UINT32 tile_height = tilemap->cached_tile_height;
/*TODO*///	struct mame_bitmap *pixmap = tilemap->pixmap;
/*TODO*///	struct mame_bitmap *transparency_bitmap = tilemap->transparency_bitmap;
/*TODO*///	int pitch = tile_width + tile_info.skip;
/*TODO*///	PAL_INIT;
/*TODO*///	UINT32 *pPenToPixel;
/*TODO*///	const UINT8 *pPenData = tile_info.pen_data;
/*TODO*///	const UINT8 *pSource;
/*TODO*///	UINT32 code_transparent = tile_info.priority;
/*TODO*///	UINT32 code_opaque = code_transparent | TILE_FLAG_FG_OPAQUE;
/*TODO*///	UINT32 tx;
/*TODO*///	UINT32 ty;
/*TODO*///	UINT32 data;
/*TODO*///	UINT32 yx;
/*TODO*///	UINT32 x;
/*TODO*///	UINT32 y;
/*TODO*///	UINT32 pen;
/*TODO*///	UINT8 *pBitmask = tile_info.mask_data;
/*TODO*///	UINT32 bitoffs;
/*TODO*///	int bWhollyOpaque;
/*TODO*///	int bWhollyTransparent;
/*TODO*///	int bDontIgnoreTransparency = !(flags&TILE_IGNORE_TRANSPARENCY);
/*TODO*///
/*TODO*///	bWhollyOpaque = 1;
/*TODO*///	bWhollyTransparent = 1;
/*TODO*///
/*TODO*///	pPenToPixel = tilemap->pPenToPixel[flags&(TILE_FLIPY|TILE_FLIPX)];
/*TODO*///
/*TODO*///	if( flags&TILE_4BPP )
/*TODO*///	{
/*TODO*///		for( ty=tile_height; ty!=0; ty-- )
/*TODO*///		{
/*TODO*///			pSource = pPenData;
/*TODO*///			for( tx=tile_width/2; tx!=0; tx-- )
/*TODO*///			{
/*TODO*///				data = *pSource++;
/*TODO*///
/*TODO*///				pen = data&0xf;
/*TODO*///				yx = *pPenToPixel++;
/*TODO*///				x = x0+(yx%MAX_TILESIZE);
/*TODO*///				y = y0+(yx/MAX_TILESIZE);
/*TODO*///				*(x+(UINT16 *)pixmap->line[y]) = PAL_GET(pen);
/*TODO*///
/*TODO*///				pen = data>>4;
/*TODO*///				yx = *pPenToPixel++;
/*TODO*///				x = x0+(yx%MAX_TILESIZE);
/*TODO*///				y = y0+(yx/MAX_TILESIZE);
/*TODO*///				*(x+(UINT16 *)pixmap->line[y]) = PAL_GET(pen);
/*TODO*///			}
/*TODO*///			pPenData += pitch/2;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		for( ty=tile_height; ty!=0; ty-- )
/*TODO*///		{
/*TODO*///			pSource = pPenData;
/*TODO*///			for( tx=tile_width; tx!=0; tx-- )
/*TODO*///			{
/*TODO*///				pen = *pSource++;
/*TODO*///				yx = *pPenToPixel++;
/*TODO*///				x = x0+(yx%MAX_TILESIZE);
/*TODO*///				y = y0+(yx/MAX_TILESIZE);
/*TODO*///				*(x+(UINT16 *)pixmap->line[y]) = PAL_GET(pen);
/*TODO*///			}
/*TODO*///			pPenData += pitch;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	pPenToPixel = tilemap->pPenToPixel[flags&(TILE_FLIPY|TILE_FLIPX)];
/*TODO*///	bitoffs = 0;
/*TODO*///	for( ty=tile_height; ty!=0; ty-- )
/*TODO*///	{
/*TODO*///		for( tx=tile_width; tx!=0; tx-- )
/*TODO*///		{
/*TODO*///			yx = *pPenToPixel++;
/*TODO*///			x = x0+(yx%MAX_TILESIZE);
/*TODO*///			y = y0+(yx/MAX_TILESIZE);
/*TODO*///			if( bDontIgnoreTransparency && (pBitmask[bitoffs/8]&(0x80>>(bitoffs&7))) == 0 )
/*TODO*///			{
/*TODO*///				((UINT8 *)transparency_bitmap->line[y])[x] = code_transparent;
/*TODO*///				bWhollyOpaque = 0;
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				((UINT8 *)transparency_bitmap->line[y])[x] = code_opaque;
/*TODO*///				bWhollyTransparent = 0;
/*TODO*///			}
/*TODO*///			bitoffs++;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	return (bWhollyOpaque || bWhollyTransparent)?0:TILE_FLAG_FG_OPAQUE;
/*TODO*///}
/*TODO*///
/*TODO*///static UINT8 TRANSP(HandleTransparencyColor)(struct tilemap *tilemap, UINT32 x0, UINT32 y0, UINT32 flags)
/*TODO*///{
/*TODO*///	UINT32 tile_width = tilemap->cached_tile_width;
/*TODO*///	UINT32 tile_height = tilemap->cached_tile_height;
/*TODO*///	struct mame_bitmap *pixmap = tilemap->pixmap;
/*TODO*///	struct mame_bitmap *transparency_bitmap = tilemap->transparency_bitmap;
/*TODO*///	int pitch = tile_width + tile_info.skip;
/*TODO*///	PAL_INIT;
/*TODO*///	UINT32 *pPenToPixel = tilemap->pPenToPixel[flags&(TILE_FLIPY|TILE_FLIPX)];
/*TODO*///	const UINT8 *pPenData = tile_info.pen_data;
/*TODO*///	const UINT8 *pSource;
/*TODO*///	UINT32 code_transparent = tile_info.priority;
/*TODO*///	UINT32 code_opaque = code_transparent | TILE_FLAG_FG_OPAQUE;
/*TODO*///	UINT32 tx;
/*TODO*///	UINT32 ty;
/*TODO*///	UINT32 data;
/*TODO*///	UINT32 yx;
/*TODO*///	UINT32 x;
/*TODO*///	UINT32 y;
/*TODO*///	UINT32 pen;
/*TODO*///	UINT32 transparent_color = tilemap->transparent_pen;
/*TODO*///	int bWhollyOpaque;
/*TODO*///	int bWhollyTransparent;
/*TODO*///
/*TODO*///	bWhollyOpaque = 1;
/*TODO*///	bWhollyTransparent = 1;
/*TODO*///
/*TODO*///	if( flags&TILE_4BPP )
/*TODO*///	{
/*TODO*///		for( ty=tile_height; ty!=0; ty-- )
/*TODO*///		{
/*TODO*///			pSource = pPenData;
/*TODO*///			for( tx=tile_width/2; tx!=0; tx-- )
/*TODO*///			{
/*TODO*///				data = *pSource++;
/*TODO*///
/*TODO*///				pen = data&0xf;
/*TODO*///				yx = *pPenToPixel++;
/*TODO*///				x = x0+(yx%MAX_TILESIZE);
/*TODO*///				y = y0+(yx/MAX_TILESIZE);
/*TODO*///				*(x+(UINT16 *)pixmap->line[y]) = PAL_GET(pen);
/*TODO*///				if( PAL_GET(pen)==transparent_color )
/*TODO*///				{
/*TODO*///					((UINT8 *)transparency_bitmap->line[y])[x] = code_transparent;
/*TODO*///					bWhollyOpaque = 0;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					((UINT8 *)transparency_bitmap->line[y])[x] = code_opaque;
/*TODO*///					bWhollyTransparent = 0;
/*TODO*///				}
/*TODO*///
/*TODO*///				pen = data>>4;
/*TODO*///				yx = *pPenToPixel++;
/*TODO*///				x = x0+(yx%MAX_TILESIZE);
/*TODO*///				y = y0+(yx/MAX_TILESIZE);
/*TODO*///				*(x+(UINT16 *)pixmap->line[y]) = PAL_GET(pen);
/*TODO*///				if( PAL_GET(pen)==transparent_color )
/*TODO*///				{
/*TODO*///					((UINT8 *)transparency_bitmap->line[y])[x] = code_transparent;
/*TODO*///					bWhollyOpaque = 0;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					((UINT8 *)transparency_bitmap->line[y])[x] = code_opaque;
/*TODO*///					bWhollyTransparent = 0;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			pPenData += pitch/2;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		for( ty=tile_height; ty!=0; ty-- )
/*TODO*///		{
/*TODO*///			pSource = pPenData;
/*TODO*///			for( tx=tile_width; tx!=0; tx-- )
/*TODO*///			{
/*TODO*///				pen = *pSource++;
/*TODO*///				yx = *pPenToPixel++;
/*TODO*///				x = x0+(yx%MAX_TILESIZE);
/*TODO*///				y = y0+(yx/MAX_TILESIZE);
/*TODO*///				*(x+(UINT16 *)pixmap->line[y]) = PAL_GET(pen);
/*TODO*///				if( PAL_GET(pen)==transparent_color )
/*TODO*///				{
/*TODO*///					((UINT8 *)transparency_bitmap->line[y])[x] = code_transparent;
/*TODO*///					bWhollyOpaque = 0;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					((UINT8 *)transparency_bitmap->line[y])[x] = code_opaque;
/*TODO*///					bWhollyTransparent = 0;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			pPenData += pitch;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	return (bWhollyOpaque || bWhollyTransparent)?0:TILE_FLAG_FG_OPAQUE;
/*TODO*///}
/*TODO*///
/*TODO*///static UINT8 TRANSP(HandleTransparencyPen)(struct tilemap *tilemap, UINT32 x0, UINT32 y0, UINT32 flags)
/*TODO*///{
/*TODO*///	UINT32 tile_width = tilemap->cached_tile_width;
/*TODO*///	UINT32 tile_height = tilemap->cached_tile_height;
/*TODO*///	struct mame_bitmap *pixmap = tilemap->pixmap;
/*TODO*///	struct mame_bitmap *transparency_bitmap = tilemap->transparency_bitmap;
/*TODO*///	int pitch = tile_width + tile_info.skip;
/*TODO*///	PAL_INIT;
/*TODO*///	UINT32 *pPenToPixel = tilemap->pPenToPixel[flags&(TILE_FLIPY|TILE_FLIPX)];
/*TODO*///	const UINT8 *pPenData = tile_info.pen_data;
/*TODO*///	const UINT8 *pSource;
/*TODO*///	UINT32 code_transparent = tile_info.priority;
/*TODO*///	UINT32 code_opaque = code_transparent | TILE_FLAG_FG_OPAQUE;
/*TODO*///	UINT32 tx;
/*TODO*///	UINT32 ty;
/*TODO*///	UINT32 data;
/*TODO*///	UINT32 yx;
/*TODO*///	UINT32 x;
/*TODO*///	UINT32 y;
/*TODO*///	UINT32 pen;
/*TODO*///	UINT32 transparent_pen = tilemap->transparent_pen;
/*TODO*///	int bWhollyOpaque;
/*TODO*///	int bWhollyTransparent;
/*TODO*///
/*TODO*///	bWhollyOpaque = 1;
/*TODO*///	bWhollyTransparent = 1;
/*TODO*///
/*TODO*///	if( flags&TILE_IGNORE_TRANSPARENCY )
/*TODO*///	{
/*TODO*///		transparent_pen = ~0;
/*TODO*///	}
/*TODO*///
/*TODO*///	if( flags&TILE_4BPP )
/*TODO*///	{
/*TODO*///		for( ty=tile_height; ty!=0; ty-- )
/*TODO*///		{
/*TODO*///			pSource = pPenData;
/*TODO*///			for( tx=tile_width/2; tx!=0; tx-- )
/*TODO*///			{
/*TODO*///				data = *pSource++;
/*TODO*///
/*TODO*///				pen = data&0xf;
/*TODO*///				yx = *pPenToPixel++;
/*TODO*///				x = x0+(yx%MAX_TILESIZE);
/*TODO*///				y = y0+(yx/MAX_TILESIZE);
/*TODO*///				*(x+(UINT16 *)pixmap->line[y]) = PAL_GET(pen);
/*TODO*///				if( pen==transparent_pen )
/*TODO*///				{
/*TODO*///					((UINT8 *)transparency_bitmap->line[y])[x] = code_transparent;
/*TODO*///					bWhollyOpaque = 0;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					((UINT8 *)transparency_bitmap->line[y])[x] = code_opaque;
/*TODO*///					bWhollyTransparent = 0;
/*TODO*///				}
/*TODO*///
/*TODO*///				pen = data>>4;
/*TODO*///				yx = *pPenToPixel++;
/*TODO*///				x = x0+(yx%MAX_TILESIZE);
/*TODO*///				y = y0+(yx/MAX_TILESIZE);
/*TODO*///				*(x+(UINT16 *)pixmap->line[y]) = PAL_GET(pen);
/*TODO*///				((UINT8 *)transparency_bitmap->line[y])[x] = (pen==transparent_pen)?code_transparent:code_opaque;
/*TODO*///			}
/*TODO*///			pPenData += pitch/2;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		for( ty=tile_height; ty!=0; ty-- )
/*TODO*///		{
/*TODO*///			pSource = pPenData;
/*TODO*///			for( tx=tile_width; tx!=0; tx-- )
/*TODO*///			{
/*TODO*///				pen = *pSource++;
/*TODO*///				yx = *pPenToPixel++;
/*TODO*///				x = x0+(yx%MAX_TILESIZE);
/*TODO*///				y = y0+(yx/MAX_TILESIZE);
/*TODO*///				*(x+(UINT16 *)pixmap->line[y]) = PAL_GET(pen);
/*TODO*///				if( pen==transparent_pen )
/*TODO*///				{
/*TODO*///					((UINT8 *)transparency_bitmap->line[y])[x] = code_transparent;
/*TODO*///					bWhollyOpaque = 0;
/*TODO*///
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					((UINT8 *)transparency_bitmap->line[y])[x] = code_opaque;
/*TODO*///					bWhollyTransparent = 0;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			pPenData += pitch;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	return (bWhollyOpaque || bWhollyTransparent)?0:TILE_FLAG_FG_OPAQUE;
/*TODO*///}
/*TODO*///
/*TODO*///static UINT8 TRANSP(HandleTransparencyPenBit)(struct tilemap *tilemap, UINT32 x0, UINT32 y0, UINT32 flags)
/*TODO*///{
/*TODO*///	UINT32 tile_width = tilemap->cached_tile_width;
/*TODO*///	UINT32 tile_height = tilemap->cached_tile_height;
/*TODO*///	struct mame_bitmap *pixmap = tilemap->pixmap;
/*TODO*///	struct mame_bitmap *transparency_bitmap = tilemap->transparency_bitmap;
/*TODO*///	int pitch = tile_width + tile_info.skip;
/*TODO*///	PAL_INIT;
/*TODO*///	UINT32 *pPenToPixel = tilemap->pPenToPixel[flags&(TILE_FLIPY|TILE_FLIPX)];
/*TODO*///	const UINT8 *pPenData = tile_info.pen_data;
/*TODO*///	const UINT8 *pSource;
/*TODO*///	UINT32 tx;
/*TODO*///	UINT32 ty;
/*TODO*///	UINT32 data;
/*TODO*///	UINT32 yx;
/*TODO*///	UINT32 x;
/*TODO*///	UINT32 y;
/*TODO*///	UINT32 pen;
/*TODO*///	UINT32 penbit = tilemap->transparent_pen;
/*TODO*///	UINT32 code_front = tile_info.priority | TILE_FLAG_FG_OPAQUE;
/*TODO*///	UINT32 code_back = tile_info.priority | TILE_FLAG_BG_OPAQUE;
/*TODO*///	int code;
/*TODO*///	int and_flags = ~0;
/*TODO*///	int or_flags = 0;
/*TODO*///
/*TODO*///	if( flags&TILE_4BPP )
/*TODO*///	{
/*TODO*///		for( ty=tile_height; ty!=0; ty-- )
/*TODO*///		{
/*TODO*///			pSource = pPenData;
/*TODO*///			for( tx=tile_width/2; tx!=0; tx-- )
/*TODO*///			{
/*TODO*///				data = *pSource++;
/*TODO*///
/*TODO*///				pen = data&0xf;
/*TODO*///				yx = *pPenToPixel++;
/*TODO*///				x = x0+(yx%MAX_TILESIZE);
/*TODO*///				y = y0+(yx/MAX_TILESIZE);
/*TODO*///				*(x+(UINT16 *)pixmap->line[y]) = PAL_GET(pen);
/*TODO*///				code = ((pen&penbit)==penbit)?code_front:code_back;
/*TODO*///				and_flags &= code;
/*TODO*///				or_flags |= code;
/*TODO*///				((UINT8 *)transparency_bitmap->line[y])[x] = code;
/*TODO*///
/*TODO*///				pen = data>>4;
/*TODO*///				yx = *pPenToPixel++;
/*TODO*///				x = x0+(yx%MAX_TILESIZE);
/*TODO*///				y = y0+(yx/MAX_TILESIZE);
/*TODO*///				*(x+(UINT16 *)pixmap->line[y]) = PAL_GET(pen);
/*TODO*///				code = ((pen&penbit)==penbit)?code_front:code_back;
/*TODO*///				and_flags &= code;
/*TODO*///				or_flags |= code;
/*TODO*///				((UINT8 *)transparency_bitmap->line[y])[x] = code;
/*TODO*///			}
/*TODO*///			pPenData += pitch/2;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		for( ty=tile_height; ty!=0; ty-- )
/*TODO*///		{
/*TODO*///			pSource = pPenData;
/*TODO*///			for( tx=tile_width; tx!=0; tx-- )
/*TODO*///			{
/*TODO*///				pen = *pSource++;
/*TODO*///				yx = *pPenToPixel++;
/*TODO*///				x = x0+(yx%MAX_TILESIZE);
/*TODO*///				y = y0+(yx/MAX_TILESIZE);
/*TODO*///				*(x+(UINT16 *)pixmap->line[y]) = PAL_GET(pen);
/*TODO*///				code = ((pen&penbit)==penbit)?code_front:code_back;
/*TODO*///				and_flags &= code;
/*TODO*///				or_flags |= code;
/*TODO*///				((UINT8 *)transparency_bitmap->line[y])[x] = code;
/*TODO*///			}
/*TODO*///			pPenData += pitch;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	return or_flags ^ and_flags;
/*TODO*///}
/*TODO*///
/*TODO*///static UINT8 TRANSP(HandleTransparencyPens)(struct tilemap *tilemap, UINT32 x0, UINT32 y0, UINT32 flags)
/*TODO*///{
/*TODO*///	UINT32 tile_width = tilemap->cached_tile_width;
/*TODO*///	UINT32 tile_height = tilemap->cached_tile_height;
/*TODO*///	struct mame_bitmap *pixmap = tilemap->pixmap;
/*TODO*///	struct mame_bitmap *transparency_bitmap = tilemap->transparency_bitmap;
/*TODO*///	int pitch = tile_width + tile_info.skip;
/*TODO*///	PAL_INIT;
/*TODO*///	UINT32 *pPenToPixel = tilemap->pPenToPixel[flags&(TILE_FLIPY|TILE_FLIPX)];
/*TODO*///	const UINT8 *pPenData = tile_info.pen_data;
/*TODO*///	const UINT8 *pSource;
/*TODO*///	UINT32 code_transparent = tile_info.priority;
/*TODO*///	UINT32 tx;
/*TODO*///	UINT32 ty;
/*TODO*///	UINT32 data;
/*TODO*///	UINT32 yx;
/*TODO*///	UINT32 x;
/*TODO*///	UINT32 y;
/*TODO*///	UINT32 pen;
/*TODO*///	UINT32 fgmask = tilemap->fgmask[(flags>>TILE_SPLIT_OFFSET)&3];
/*TODO*///	UINT32 bgmask = tilemap->bgmask[(flags>>TILE_SPLIT_OFFSET)&3];
/*TODO*///	UINT32 code;
/*TODO*///	int and_flags = ~0;
/*TODO*///	int or_flags = 0;
/*TODO*///
/*TODO*///	if( flags&TILE_4BPP )
/*TODO*///	{
/*TODO*///		for( ty=tile_height; ty!=0; ty-- )
/*TODO*///		{
/*TODO*///			pSource = pPenData;
/*TODO*///			for( tx=tile_width/2; tx!=0; tx-- )
/*TODO*///			{
/*TODO*///				data = *pSource++;
/*TODO*///
/*TODO*///				pen = data&0xf;
/*TODO*///				yx = *pPenToPixel++;
/*TODO*///				x = x0+(yx%MAX_TILESIZE);
/*TODO*///				y = y0+(yx/MAX_TILESIZE);
/*TODO*///				*(x+(UINT16 *)pixmap->line[y]) = PAL_GET(pen);
/*TODO*///				code = code_transparent;
/*TODO*///				if( !((1<<pen)&fgmask) ) code |= TILE_FLAG_FG_OPAQUE;
/*TODO*///				if( !((1<<pen)&bgmask) ) code |= TILE_FLAG_BG_OPAQUE;
/*TODO*///				and_flags &= code;
/*TODO*///				or_flags |= code;
/*TODO*///				((UINT8 *)transparency_bitmap->line[y])[x] = code;
/*TODO*///
/*TODO*///				pen = data>>4;
/*TODO*///				yx = *pPenToPixel++;
/*TODO*///				x = x0+(yx%MAX_TILESIZE);
/*TODO*///				y = y0+(yx/MAX_TILESIZE);
/*TODO*///				*(x+(UINT16 *)pixmap->line[y]) = PAL_GET(pen);
/*TODO*///				code = code_transparent;
/*TODO*///				if( !((1<<pen)&fgmask) ) code |= TILE_FLAG_FG_OPAQUE;
/*TODO*///				if( !((1<<pen)&bgmask) ) code |= TILE_FLAG_BG_OPAQUE;
/*TODO*///				and_flags &= code;
/*TODO*///				or_flags |= code;
/*TODO*///				((UINT8 *)transparency_bitmap->line[y])[x] = code;
/*TODO*///			}
/*TODO*///			pPenData += pitch/2;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		for( ty=tile_height; ty!=0; ty-- )
/*TODO*///		{
/*TODO*///			pSource = pPenData;
/*TODO*///			for( tx=tile_width; tx!=0; tx-- )
/*TODO*///			{
/*TODO*///				pen = *pSource++;
/*TODO*///				yx = *pPenToPixel++;
/*TODO*///				x = x0+(yx%MAX_TILESIZE);
/*TODO*///				y = y0+(yx/MAX_TILESIZE);
/*TODO*///				*(x+(UINT16 *)pixmap->line[y]) = PAL_GET(pen);
/*TODO*///				code = code_transparent;
/*TODO*///				if( !((1<<pen)&fgmask) ) code |= TILE_FLAG_FG_OPAQUE;
/*TODO*///				if( !((1<<pen)&bgmask) ) code |= TILE_FLAG_BG_OPAQUE;
/*TODO*///				and_flags &= code;
/*TODO*///				or_flags |= code;
/*TODO*///				((UINT8 *)transparency_bitmap->line[y])[x] = code;
/*TODO*///			}
/*TODO*///			pPenData += pitch;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	return and_flags ^ or_flags;
/*TODO*///}
/*TODO*///
/*TODO*///static UINT8 TRANSP(HandleTransparencyNone)(struct tilemap *tilemap, UINT32 x0, UINT32 y0, UINT32 flags)
/*TODO*///{
/*TODO*///	UINT32 tile_width = tilemap->cached_tile_width;
/*TODO*///	UINT32 tile_height = tilemap->cached_tile_height;
/*TODO*///	struct mame_bitmap *pixmap = tilemap->pixmap;
/*TODO*///	struct mame_bitmap *transparency_bitmap = tilemap->transparency_bitmap;
/*TODO*///	int pitch = tile_width + tile_info.skip;
/*TODO*///	PAL_INIT;
/*TODO*///	UINT32 *pPenToPixel = tilemap->pPenToPixel[flags&(TILE_FLIPY|TILE_FLIPX)];
/*TODO*///	const UINT8 *pPenData = tile_info.pen_data;
/*TODO*///	const UINT8 *pSource;
/*TODO*///	UINT32 code_opaque = tile_info.priority;
/*TODO*///	UINT32 tx;
/*TODO*///	UINT32 ty;
/*TODO*///	UINT32 data;
/*TODO*///	UINT32 yx;
/*TODO*///	UINT32 x;
/*TODO*///	UINT32 y;
/*TODO*///	UINT32 pen;
/*TODO*///
/*TODO*///	if( flags&TILE_4BPP )
/*TODO*///	{
/*TODO*///		for( ty=tile_height; ty!=0; ty-- )
/*TODO*///		{
/*TODO*///			pSource = pPenData;
/*TODO*///			for( tx=tile_width/2; tx!=0; tx-- )
/*TODO*///			{
/*TODO*///				data = *pSource++;
/*TODO*///
/*TODO*///				pen = data&0xf;
/*TODO*///				yx = *pPenToPixel++;
/*TODO*///				x = x0+(yx%MAX_TILESIZE);
/*TODO*///				y = y0+(yx/MAX_TILESIZE);
/*TODO*///				*(x+(UINT16 *)pixmap->line[y]) = PAL_GET(pen);
/*TODO*///				((UINT8 *)transparency_bitmap->line[y])[x] = code_opaque;
/*TODO*///
/*TODO*///				pen = data>>4;
/*TODO*///				yx = *pPenToPixel++;
/*TODO*///				x = x0+(yx%MAX_TILESIZE);
/*TODO*///				y = y0+(yx/MAX_TILESIZE);
/*TODO*///				*(x+(UINT16 *)pixmap->line[y]) = PAL_GET(pen);
/*TODO*///				((UINT8 *)transparency_bitmap->line[y])[x] = code_opaque;
/*TODO*///			}
/*TODO*///			pPenData += pitch/2;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{
/*TODO*///		for( ty=tile_height; ty!=0; ty-- )
/*TODO*///		{
/*TODO*///			pSource = pPenData;
/*TODO*///			for( tx=tile_width; tx!=0; tx-- )
/*TODO*///			{
/*TODO*///				pen = *pSource++;
/*TODO*///				yx = *pPenToPixel++;
/*TODO*///				x = x0+(yx%MAX_TILESIZE);
/*TODO*///				y = y0+(yx/MAX_TILESIZE);
/*TODO*///				*(x+(UINT16 *)pixmap->line[y]) = PAL_GET(pen);
/*TODO*///				((UINT8 *)transparency_bitmap->line[y])[x] = code_opaque;
/*TODO*///			}
/*TODO*///			pPenData += pitch;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///#undef TRANSP
/*TODO*///#undef PAL_INIT
/*TODO*///#undef PAL_GET
/*TODO*///#endif // TRANSP
/*TODO*///
/*TODO*///
}
