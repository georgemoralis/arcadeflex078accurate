/*
 * ported to v0.78
 * using automatic conversion tool v0.01
 */
package arcadeflex.v078.vidhrdw;

//generic imports
import static arcadeflex.v078.generic.funcPtr.*;
//mame imports
import static arcadeflex.v078.mame.drawgfxH.*;
import static arcadeflex.v078.mame.drawgfx.*;
import static arcadeflex.v078.mame.commonH.*;
import static arcadeflex.v078.mame.palette.*;
import static arcadeflex.v078.mame.mame.*;
//vidhrdw imports
import static arcadeflex.v078.vidhrdw.generic.*;
//common imports
import static common.ptrLib.*;

public class minivadr {

    /**
     * *****************************************************************
     *
     * Palette Setting.
     *
     ******************************************************************
     */
    public static PaletteInitHandlerPtr palette_init_minivadr = new PaletteInitHandlerPtr() {
        public void handler(char[] colortable, UBytePtr color_prom) {
            palette_set_color(0, 0x00, 0x00, 0x00);
            palette_set_color(1, 0xff, 0xff, 0xff);
        }
    };

    /**
     * *****************************************************************
     *
     * Draw Pixel.
     *
     ******************************************************************
     */
    public static WriteHandlerPtr minivadr_videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int i;
            int x, y;
            int color;

            videoram.write(offset, data);

            x = (offset % 32) * 8;
            y = (offset / 32);

            if (x >= Machine.visible_area.min_x
                    && x <= Machine.visible_area.max_x
                    && y >= Machine.visible_area.min_y
                    && y <= Machine.visible_area.max_y) {
                for (i = 0; i < 8; i++) {
                    color = Machine.pens[((data >> i) & 0x01)];

                    plot_pixel.handler(tmpbitmap, x + (7 - i), y, color);
                }
            }
        }
    };

    public static VideoUpdateHandlerPtr video_update_minivadr = new VideoUpdateHandlerPtr() {
        public void handler(mame_bitmap bitmap, rectangle cliprect) {
            if (get_vh_global_attribute_changed() != 0) {
                int offs;

                /* redraw bitmap */
                for (offs = 0; offs < videoram_size[0]; offs++) {
                    minivadr_videoram_w.handler(offs, videoram.read(offs));
                }
            }
            copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.visible_area, TRANSPARENCY_NONE, 0);
        }
    };
}
