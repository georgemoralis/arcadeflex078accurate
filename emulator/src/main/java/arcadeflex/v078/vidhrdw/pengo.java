/*
 * ported to v0.78
 * using automatic conversion tool v0.0.5
 */
package arcadeflex.v078.vidhrdw;

//generic imports
import arcadeflex.v078.generic.funcPtr.*;
//mame imports 
import static arcadeflex.v078.mame.commonH.*;
import static arcadeflex.v078.mame.drawgfx.copybitmap;
import static arcadeflex.v078.mame.drawgfx.drawgfx;
import static arcadeflex.v078.mame.drawgfxH.*;
import static arcadeflex.v078.mame.mame.Machine;
import static arcadeflex.v078.mame.palette.*;
import static arcadeflex.v078.mame.tilemapC.*;
import static arcadeflex.v078.mame.tilemapH.*;
//vidhrdw imports
import static arcadeflex.v078.vidhrdw.generic.*;
//common imports
import static common.libc.cstring.*;
import static common.ptrLib.*;

public class pengo {

    static int gfx_bank;
    static int flipscreen;
    static int xoffsethack;
    static struct_tilemap tilemap;
    static UBytePtr sprite_bank = new UBytePtr();
    static UBytePtr tiles_bankram = new UBytePtr();

    static rectangle spritevisiblearea = new rectangle(
            2 * 8, 34 * 8 - 1,
            0 * 8, 28 * 8 - 1
    );

    /**
     * *************************************************************************
     *
     * Convert the color PROMs into a more useable format.
     *
     * Pac Man has a 32x8 palette PROM and a 256x4 color lookup table PROM.
     *
     * Pengo has a 32x8 palette PROM and a 1024x4 color lookup table PROM.
     *
     * The palette PROM is connected to the RGB output this way:
     *
     * bit 7 -- 220 ohm resistor -- BLUE -- 470 ohm resistor -- BLUE -- 220 ohm
     * resistor -- GREEN -- 470 ohm resistor -- GREEN -- 1 kohm resistor --
     * GREEN -- 220 ohm resistor -- RED -- 470 ohm resistor -- RED bit 0 -- 1
     * kohm resistor -- RED
     *
     **************************************************************************
     */
    public static PaletteInitHandlerPtr palette_init_pacman = new PaletteInitHandlerPtr() {
        public void handler(char[] colortable, UBytePtr color_prom) {
            int i;

            for (i = 0; i < Machine.drv.total_colors; i++) {
                int bit0, bit1, bit2, r, g, b;

                /* red component */
                bit0 = (color_prom.read() >> 0) & 0x01;
                bit1 = (color_prom.read() >> 1) & 0x01;
                bit2 = (color_prom.read() >> 2) & 0x01;
                r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
                /* green component */
                bit0 = (color_prom.read() >> 3) & 0x01;
                bit1 = (color_prom.read() >> 4) & 0x01;
                bit2 = (color_prom.read() >> 5) & 0x01;
                g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
                /* blue component */
                bit0 = 0;
                bit1 = (color_prom.read() >> 6) & 0x01;
                bit2 = (color_prom.read() >> 7) & 0x01;
                b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;

                palette_set_color(i, r, g, b);
                color_prom.inc();
            }

            color_prom.inc(0x10);
            /* color_prom now points to the beginning of the lookup table */

 /* character lookup table */
 /* sprites use the same color lookup table as characters */
            for (i = 0; i < Machine.gfx[0].total_colors * Machine.gfx[0].color_granularity; i++) {
                colortable[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] = (char) ((color_prom.readinc()) & 0x0f);
            }
        }
    };

    public static PaletteInitHandlerPtr palette_init_pengo = new PaletteInitHandlerPtr() {
        public void handler(char[] colortable, UBytePtr color_prom) {
            int i;

            for (i = 0; i < Machine.drv.total_colors; i++) {
                int bit0, bit1, bit2, r, g, b;

                /* red component */
                bit0 = (color_prom.read() >> 0) & 0x01;
                bit1 = (color_prom.read() >> 1) & 0x01;
                bit2 = (color_prom.read() >> 2) & 0x01;
                r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
                /* green component */
                bit0 = (color_prom.read() >> 3) & 0x01;
                bit1 = (color_prom.read() >> 4) & 0x01;
                bit2 = (color_prom.read() >> 5) & 0x01;
                g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
                /* blue component */
                bit0 = 0;
                bit1 = (color_prom.read() >> 6) & 0x01;
                bit2 = (color_prom.read() >> 7) & 0x01;
                b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;

                palette_set_color(i, r, g, b);
                color_prom.inc();
            }

            /* color_prom now points to the beginning of the lookup table */
 /* character lookup table */
 /* sprites use the same color lookup table as characters */
            for (i = 0; i < Machine.gfx[0].total_colors * Machine.gfx[0].color_granularity; i++) {
                colortable[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] = (char) ((color_prom.readinc()) & 0x0f);
            }

            color_prom.inc(0x80);

            /* second bank character lookup table */
 /* sprites use the same color lookup table as characters */
            for (i = 0; i < Machine.gfx[2].total_colors * Machine.gfx[2].color_granularity; i++) {
                if (color_prom.read() != 0) {
                    colortable[Machine.drv.gfxdecodeinfo[2].color_codes_start + i] = (char) ((color_prom.read() & 0x0f) + 0x10);
                    /* second palette bank */
                } else {
                    colortable[Machine.drv.gfxdecodeinfo[2].color_codes_start + i] = 0;
                    /* preserve transparency */
                }

                color_prom.inc();
            }
        }
    };

    /**
     * *************************************************************************
     *
     * Start the video hardware emulation.
     *
     **************************************************************************
     */
    public static VideoStartHandlerPtr video_start_pengo = new VideoStartHandlerPtr() {
        public int handler() {
            gfx_bank = 0;
            xoffsethack = 0;

            return video_start_generic.handler();
        }
    };

    public static VideoStartHandlerPtr video_start_pacman = new VideoStartHandlerPtr() {
        public int handler() {
            gfx_bank = 0;
            /* In the Pac Man based games (NOT Pengo) the first two sprites must be offset */
 /* one pixel to the left to get a more correct placement */
            xoffsethack = 1;

            return video_start_generic.handler();
        }
    };

    public static WriteHandlerPtr pengo_gfxbank_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* the Pengo hardware can set independently the palette bank, color lookup */
 /* table, and chars/sprites. However the game always set them together (and */
 /* the only place where this is used is the intro screen) so I don't bother */
 /* emulating the whole thing. */
            if (gfx_bank != (data & 1)) {
                gfx_bank = data & 1;
                memset(dirtybuffer, 1, videoram_size[0]);
            }
        }
    };

    public static WriteHandlerPtr pengo_flipscreen_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (flipscreen != (data & 1)) {
                flipscreen = data & 1;
                memset(dirtybuffer, 1, videoram_size[0]);
            }
        }
    };

    /**
     * *************************************************************************
     *
     * Draw the game screen in the given mame_bitmap. Do NOT call
     * osd_update_display() from this function, it will be called by the main
     * emulation engine.
     *
     **************************************************************************
     */
    public static VideoUpdateHandlerPtr video_update_pengo = new VideoUpdateHandlerPtr() {
        public void handler(mame_bitmap bitmap, rectangle cliprect) {
            rectangle spriteclip = new rectangle(spritevisiblearea);
            int offs;

            sect_rect(spriteclip, cliprect);

            for (offs = videoram_size[0] - 1; offs > 0; offs--) {
                if (dirtybuffer[offs] != 0) {
                    int mx, my, sx, sy;

                    dirtybuffer[offs] = 0;
                    mx = offs % 32;
                    my = offs / 32;

                    if (my < 2) {
                        if (mx < 2 || mx >= 30) {
                            continue;
                            /* not visible */
                        }
                        sx = my + 34;
                        sy = mx - 2;
                    } else if (my >= 30) {
                        if (mx < 2 || mx >= 30) {
                            continue;
                            /* not visible */
                        }
                        sx = my - 30;
                        sy = mx - 2;
                    } else {
                        sx = mx + 2;
                        sy = my - 2;
                    }

                    if (flipscreen != 0) {
                        sx = 35 - sx;
                        sy = 27 - sy;
                    }

                    drawgfx(tmpbitmap, Machine.gfx[gfx_bank * 2],
                            videoram.read(offs),
                            colorram.read(offs) & 0x1f,
                            flipscreen, flipscreen,
                            sx * 8, sy * 8,
                            Machine.visible_area, TRANSPARENCY_NONE, 0);
                }
            }

            copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, cliprect, TRANSPARENCY_NONE, 0);

            if (spriteram_size[0] != 0) {
                /* Draw the sprites. Note that it is important to draw them exactly in this */
 /* order, to have the correct priorities. */
                for (offs = spriteram_size[0] - 2; offs > 2 * 2; offs -= 2) {
                    int sx, sy;

                    sx = 272 - spriteram_2.read(offs + 1);
                    sy = spriteram_2.read(offs) - 31;

                    drawgfx(bitmap, Machine.gfx[gfx_bank * 2 + 1],
                            spriteram.read(offs) >> 2,
                            spriteram.read(offs + 1) & 0x1f,
                            spriteram.read(offs) & 1, spriteram.read(offs) & 2,
                            sx, sy,
                            spriteclip, TRANSPARENCY_COLOR, 0);

                    /* also plot the sprite with wraparound (tunnel in Crush Roller) */
                    drawgfx(bitmap, Machine.gfx[gfx_bank * 2 + 1],
                            spriteram.read(offs) >> 2,
                            spriteram.read(offs + 1) & 0x1f,
                            spriteram.read(offs) & 1, spriteram.read(offs) & 2,
                            sx - 256, sy,
                            spriteclip, TRANSPARENCY_COLOR, 0);
                }
                /* In the Pac Man based games (NOT Pengo) the first two sprites must be offset */
 /* one pixel to the left to get a more correct placement */
                for (offs = 2 * 2; offs >= 0; offs -= 2) {
                    int sx, sy;

                    sx = 272 - spriteram_2.read(offs + 1);
                    sy = spriteram_2.read(offs) - 31;

                    drawgfx(bitmap, Machine.gfx[gfx_bank * 2 + 1],
                            spriteram.read(offs) >> 2,
                            spriteram.read(offs + 1) & 0x1f,
                            spriteram.read(offs) & 1, spriteram.read(offs) & 2,
                            sx, sy + xoffsethack,
                            spriteclip, TRANSPARENCY_COLOR, 0);

                    /* also plot the sprite with wraparound (tunnel in Crush Roller) */
                    drawgfx(bitmap, Machine.gfx[gfx_bank * 2 + 1],
                            spriteram.read(offs) >> 2,
                            spriteram.read(offs + 1) & 0x1f,
                            spriteram.read(offs) & 2, spriteram.read(offs) & 1,
                            sx - 256, sy + xoffsethack,
                            spriteclip, TRANSPARENCY_COLOR, 0);
                }
            }
        }
    };

    public static WriteHandlerPtr vanvan_bgcolor_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if ((data & 1) != 0) {
                palette_set_color(0, 0xaa, 0xaa, 0xaa);
            } else {
                palette_set_color(0, 0x00, 0x00, 0x00);
            }
        }
    };

    public static VideoUpdateHandlerPtr video_update_vanvan = new VideoUpdateHandlerPtr() {
        public void handler(mame_bitmap bitmap, rectangle cliprect) {
            rectangle spriteclip = new rectangle(spritevisiblearea);
            int offs;

            sect_rect(spriteclip, cliprect);

            for (offs = videoram_size[0] - 1; offs > 0; offs--) {
                if (dirtybuffer[offs] != 0) {
                    int mx, my, sx, sy;

                    dirtybuffer[offs] = 0;
                    mx = offs % 32;
                    my = offs / 32;

                    if (my < 2) {
                        if (mx < 2 || mx >= 30) {
                            continue;
                            /* not visible */
                        }
                        sx = my + 34;
                        sy = mx - 2;
                    } else if (my >= 30) {
                        if (mx < 2 || mx >= 30) {
                            continue;
                            /* not visible */
                        }
                        sx = my - 30;
                        sy = mx - 2;
                    } else {
                        sx = mx + 2;
                        sy = my - 2;
                    }

                    if (flipscreen != 0) {
                        sx = 35 - sx;
                        sy = 27 - sy;
                    }

                    drawgfx(tmpbitmap, Machine.gfx[gfx_bank * 2],
                            videoram.read(offs),
                            colorram.read(offs) & 0x1f,
                            flipscreen, flipscreen,
                            sx * 8, sy * 8,
                            Machine.visible_area, TRANSPARENCY_NONE, 0);
                }
            }

            copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, cliprect, TRANSPARENCY_NONE, 0);

            /* Draw the sprites. Note that it is important to draw them exactly in this */
 /* order, to have the correct priorities. */
            for (offs = spriteram_size[0] - 2; offs >= 0; offs -= 2) {
                int sx, sy;

                sx = 272 - spriteram_2.read(offs + 1);
                sy = spriteram_2.read(offs) - 31;

                drawgfx(bitmap, Machine.gfx[gfx_bank * 2 + 1],
                        spriteram.read(offs) >> 2,
                        spriteram.read(offs + 1) & 0x1f,
                        spriteram.read(offs) & 1, spriteram.read(offs) & 2,
                        sx, sy,
                        spriteclip, TRANSPARENCY_PEN, 0);

                /* also plot the sprite with wraparound (tunnel in Crush Roller) */
                drawgfx(bitmap, Machine.gfx[gfx_bank * 2 + 1],
                        spriteram.read(offs) >> 2,
                        spriteram.read(offs + 1) & 0x1f,
                        spriteram.read(offs) & 1, spriteram.read(offs) & 2,
                        sx - 256, sy,
                        spriteclip, TRANSPARENCY_PEN, 0);
            }
        }
    };

    public static GetTileInfoHandlerPtr get_tile_info = new GetTileInfoHandlerPtr() {
        public void handler(int tile_index) {
            int colbank, code, attr;

            colbank = tiles_bankram.read(tile_index & 0x1f) & 0x3;

            code = videoram.read(tile_index) + (colbank << 8);
            attr = colorram.read(tile_index & 0x1f);

            /* remove when we have proms dumps for it */
            if (strcmp(Machine.gamedrv.name, "8bpm")==0) {
                attr = 1;
            }

            SET_TILE_INFO(0, code, attr & 0x1f, 0);
        }
    };

    public static WriteHandlerPtr s2650games_videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            videoram.write(offset, data);
            tilemap_mark_tile_dirty(tilemap, offset);
        }
    };

    public static WriteHandlerPtr s2650games_colorram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int i;
            colorram.write(offset & 0x1f, data);
            for (i = offset; i < 0x0400; i += 32) {
                tilemap_mark_tile_dirty(tilemap, i);
            }
        }
    };

    public static WriteHandlerPtr s2650games_scroll_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            tilemap_set_scrolly(tilemap, offset, data);
        }
    };

    public static WriteHandlerPtr s2650games_tilesbank_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            tiles_bankram.write(offset, data);
            tilemap_mark_all_tiles_dirty(tilemap);
        }
    };

    public static WriteHandlerPtr s2650games_flipscreen_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            flip_screen_set(data);
        }
    };

    public static VideoStartHandlerPtr video_start_s2650games = new VideoStartHandlerPtr() {
        public int handler() {
            xoffsethack = 1;

            tilemap = tilemap_create(get_tile_info, tilemap_scan_rows, TILEMAP_OPAQUE, 8, 8, 32, 32);

            if (tilemap == null) {
                return 1;
            }

            colorram = new UBytePtr(0x20);

            tilemap_set_scroll_cols(tilemap, 32);

            return 0;
        }
    };

    public static VideoUpdateHandlerPtr video_update_s2650games = new VideoUpdateHandlerPtr() {
        public void handler(mame_bitmap bitmap, rectangle cliprect) {
            int offs;

            tilemap_draw(bitmap, cliprect, tilemap, 0, 0);

            for (offs = spriteram_size[0] - 2; offs > 2 * 2; offs -= 2) {
                int sx, sy;

                sx = 255 - spriteram_2.read(offs + 1);
                sy = spriteram_2.read(offs) - 15;

                drawgfx(bitmap, Machine.gfx[1],
                        (spriteram.read(offs) >> 2) | ((sprite_bank.read(offs) & 3) << 6),
                        spriteram.read(offs + 1) & 0x1f,
                        spriteram.read(offs) & 1, spriteram.read(offs) & 2,
                        sx, sy,
                        cliprect, TRANSPARENCY_COLOR, 0);
            }
            /* In the Pac Man based games (NOT Pengo) the first two sprites must be offset */
 /* one pixel to the left to get a more correct placement */
            for (offs = 2 * 2; offs >= 0; offs -= 2) {
                int sx, sy;

                sx = 255 - spriteram_2.read(offs + 1);
                sy = spriteram_2.read(offs) - 15;

                drawgfx(bitmap, Machine.gfx[1],
                        (spriteram.read(offs) >> 2) | ((sprite_bank.read(offs) & 3) << 6),
                        spriteram.read(offs + 1) & 0x1f,
                        spriteram.read(offs) & 1, spriteram.read(offs) & 2,
                        sx, sy + xoffsethack,
                        cliprect, TRANSPARENCY_COLOR, 0);
            }
        }
    };
}
