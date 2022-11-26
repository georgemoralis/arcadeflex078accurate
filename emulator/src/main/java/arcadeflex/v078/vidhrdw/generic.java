/**
 * ported to v0.78
 */
package arcadeflex.v078.vidhrdw;

//generic imports
import static arcadeflex.v078.generic.funcPtr.*;
//mame imports
import static arcadeflex.v078.mame.drawgfx.*;
import static arcadeflex.v078.mame.drawgfxH.*;
import static arcadeflex.v078.mame.common.*;
import static arcadeflex.v078.mame.commonH.*;
import static arcadeflex.v078.mame.mame.*;
import static arcadeflex.v078.mame.tilemapC.*;
import static arcadeflex.v078.mame.tilemapH.*;
//platform imports
import static arcadeflex.v078.platform.config.*;
//common imports
import static common.ptrLib.*;
import static common.libc.cstring.*;

public class generic {

    public static UBytePtr videoram = new UBytePtr();
    /*TODO*///data16_t *videoram16;
/*TODO*///data32_t *videoram32;
    public static int[] videoram_size = new int[1];
    public static UBytePtr colorram = new UBytePtr();
    /*TODO*///data16_t *colorram16;
/*TODO*///data32_t *colorram32;
    public static UBytePtr spriteram = new UBytePtr();
    /* not used in this module... */
 /*TODO*///data16_t *spriteram16;		/* ... */
/*TODO*///data32_t *spriteram32;		/* ... */
    public static UBytePtr spriteram_2 = new UBytePtr();
    /*TODO*///data16_t *spriteram16_2;
/*TODO*///data32_t *spriteram32_2;
    public static UBytePtr spriteram_3 = new UBytePtr();
    /*TODO*///data16_t *spriteram16_3;
/*TODO*///data32_t *spriteram32_3;
    public static UBytePtr buffered_spriteram = new UBytePtr();
    /*TODO*///data16_t *buffered_spriteram16;
/*TODO*///data32_t *buffered_spriteram32;
    public static UBytePtr buffered_spriteram_2 = new UBytePtr();
    /*TODO*///data16_t *buffered_spriteram16_2;
/*TODO*///data32_t *buffered_spriteram32_2;
    public static int[] spriteram_size = new int[1];
    /* ... here just for convenience */
    public static int[] spriteram_2_size = new int[1];
    public static int[] spriteram_3_size = new int[1];
    public static char[] /*data8_t * */ dirtybuffer;
    /*TODO*///data16_t *dirtybuffer16;
/*TODO*///data32_t *dirtybuffer32;
    public static mame_bitmap tmpbitmap;

    public static int[] flip_screen_x = new int[1];
    public static int[] flip_screen_y = new int[1];
    static int global_attribute_changed;

    public static int flip_screen() {
        return flip_screen_x[0];
    }

    /*TODO*///void video_generic_postload(void)
/*TODO*///{
/*TODO*///	memset(dirtybuffer,1,videoram_size);
/*TODO*///}
    /**
     * *************************************************************************
     *
     * Start the video hardware emulation.
     *
     **************************************************************************
     */
    public static VideoStartHandlerPtr video_start_generic = new VideoStartHandlerPtr() {
        public int handler() {
            dirtybuffer = null;
            tmpbitmap = null;

            if (videoram_size[0] == 0) {
                logerror("Error: video_start_generic() called but videoram_size not initialized\n");
                return 1;
            }

            if ((dirtybuffer = new char[videoram_size[0]]) == null) {
                return 1;
            }
            memset(dirtybuffer, 1, videoram_size[0]);

            if ((tmpbitmap = auto_bitmap_alloc(Machine.drv.screen_width, Machine.drv.screen_height)) == null) {
                return 1;
            }

            /*TODO*///	state_save_register_func_postload(video_generic_postload);
            return 0;
        }
    };

    public static VideoStartHandlerPtr video_start_generic_bitmapped = new VideoStartHandlerPtr() {
        public int handler() {
            if ((tmpbitmap = auto_bitmap_alloc(Machine.drv.screen_width, Machine.drv.screen_height)) == null) {
                return 1;
            }

            return 0;
        }
    };

    /**
     * *************************************************************************
     *
     * Draw the game screen in the given mame_bitmap. To be used by bitmapped
     * games not using sprites.
     *
     **************************************************************************
     */
    public static VideoUpdateHandlerPtr video_update_generic_bitmapped = new VideoUpdateHandlerPtr() {
        public void handler(mame_bitmap bitmap, rectangle cliprect) {
            copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.visible_area, TRANSPARENCY_NONE, 0);
        }
    };

    public static ReadHandlerPtr videoram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return videoram.read(offset);
        }
    };

    public static ReadHandlerPtr colorram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return colorram.read(offset);
        }
    };

    public static WriteHandlerPtr videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (videoram.read(offset) != data) {
                dirtybuffer[offset] = 1;

                videoram.write(offset, data);
            }
        }
    };

    public static WriteHandlerPtr colorram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (colorram.read(offset) != data) {
                dirtybuffer[offset] = 1;

                colorram.write(offset, data);
            }
        }
    };

    public static ReadHandlerPtr spriteram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return spriteram.read(offset);
        }
    };

    public static WriteHandlerPtr spriteram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            spriteram.write(offset, data);
        }
    };

    /*TODO*///
/*TODO*///READ16_HANDLER( spriteram16_r )
/*TODO*///{
/*TODO*///	return spriteram16[offset];
/*TODO*///}
/*TODO*///
/*TODO*///WRITE16_HANDLER( spriteram16_w )
/*TODO*///{
/*TODO*///	COMBINE_DATA(spriteram16+offset);
/*TODO*///}
/*TODO*///
    public static ReadHandlerPtr spriteram_2_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return spriteram_2.read(offset);
        }
    };

    public static WriteHandlerPtr spriteram_2_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            spriteram_2.write(offset, data);
        }
    };

    public static WriteHandlerPtr buffer_spriteram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            memcpy(buffered_spriteram, spriteram, spriteram_size[0]);
        }
    };

    /*TODO*///
/*TODO*///WRITE16_HANDLER( buffer_spriteram16_w )
/*TODO*///{
/*TODO*///	memcpy(buffered_spriteram16,spriteram16,spriteram_size);
/*TODO*///}
/*TODO*///
/*TODO*///WRITE32_HANDLER( buffer_spriteram32_w )
/*TODO*///{
/*TODO*///	memcpy(buffered_spriteram32,spriteram32,spriteram_size);
/*TODO*///}
/*TODO*///
    public static WriteHandlerPtr buffer_spriteram_2_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            memcpy(buffered_spriteram_2, spriteram_2, spriteram_2_size[0]);
        }
    };

    /*TODO*///
/*TODO*///WRITE16_HANDLER( buffer_spriteram16_2_w )
/*TODO*///{
/*TODO*///	memcpy(buffered_spriteram16_2,spriteram16_2,spriteram_2_size);
/*TODO*///}
/*TODO*///
/*TODO*///WRITE32_HANDLER( buffer_spriteram32_2_w )
/*TODO*///{
/*TODO*///	memcpy(buffered_spriteram32_2,spriteram32_2,spriteram_2_size);
/*TODO*///}
/*TODO*///
    public static void buffer_spriteram(UBytePtr ptr, int length) {
        memcpy(buffered_spriteram, ptr, length);
    }

    public static void buffer_spriteram_2(UBytePtr ptr, int length) {
        memcpy(buffered_spriteram_2, ptr, length);
    }

    /**
     * *************************************************************************
     *
     * Global video attribute handling code
     *
     **************************************************************************
     */

    /*-------------------------------------------------
	updateflip - handle global flipping
    -------------------------------------------------*/
    static void updateflip() {
        int min_x, max_x, min_y, max_y;

        tilemap_set_flip(ALL_TILEMAPS, (TILEMAP_FLIPX & flip_screen_x[0]) | (TILEMAP_FLIPY & flip_screen_y[0]));

        min_x = Machine.drv.default_visible_area.min_x;
        max_x = Machine.drv.default_visible_area.max_x;
        min_y = Machine.drv.default_visible_area.min_y;
        max_y = Machine.drv.default_visible_area.max_y;

        if (flip_screen_x[0] != 0) {
            int temp;

            temp = Machine.drv.screen_width - min_x - 1;
            min_x = Machine.drv.screen_width - max_x - 1;
            max_x = temp;
        }
        if (flip_screen_y[0] != 0) {
            int temp;

            temp = Machine.drv.screen_height - min_y - 1;
            min_y = Machine.drv.screen_height - max_y - 1;
            max_y = temp;
        }

        set_visible_area(min_x, max_x, min_y, max_y);
    }

    /*-------------------------------------------------
	flip_screen_set - set global flip
    -------------------------------------------------*/
    public static void flip_screen_set(int on) {
        flip_screen_x_set(on);
        flip_screen_y_set(on);
    }


    /*-------------------------------------------------
	flip_screen_x_set - set global horizontal flip
    -------------------------------------------------*/
    public static void flip_screen_x_set(int on) {
        if (on != 0) {
            on = ~0;
        }
        if (flip_screen_x[0] != on) {
            set_vh_global_attribute(flip_screen_x, on);
            updateflip();
        }
    }


    /*-------------------------------------------------
	flip_screen_y_set - set global vertical flip
    -------------------------------------------------*/
    public static void flip_screen_y_set(int on) {
        if (on != 0) {
            on = ~0;
        }
        if (flip_screen_y[0] != on) {
            set_vh_global_attribute(flip_screen_y, on);
            updateflip();
        }
    }


    /*-------------------------------------------------
	set_vh_global_attribute - set an arbitrary
	global video attribute
    -------------------------------------------------*/
    public static void set_vh_global_attribute(int[] addr, int data) {
        throw new UnsupportedOperationException("Unsupported");
        /*TODO*///	if (!addr || *addr != data)
/*TODO*///	{
/*TODO*///		global_attribute_changed = 1;
/*TODO*///		if (addr)
/*TODO*///			*addr = data;
/*TODO*///	}
    }


    /*-------------------------------------------------
	get_vh_global_attribute - set an arbitrary
	global video attribute
    -------------------------------------------------*/
    public static int get_vh_global_attribute_changed() {
        int result = global_attribute_changed;
        global_attribute_changed = 0;
        return result;
    }

}
