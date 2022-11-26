/*
 * ported to v0.78
 * using automatic conversion tool v0.0.5
 */
package arcadeflex.v078.vidhrdw;

//generic imports
import arcadeflex.v078.generic.funcPtr.*;
//mame imports 
import static arcadeflex.v078.mame.commonH.*;
import static arcadeflex.v078.mame.drawgfx.*;
import static arcadeflex.v078.mame.drawgfxH.*;
import static arcadeflex.v078.mame.mame.Machine;
import static arcadeflex.v078.mame.palette.*;
import static arcadeflex.v078.mame.tilemapC.*;
import static arcadeflex.v078.mame.tilemapH.*;
//vidhrdw imports
import static arcadeflex.v078.vidhrdw.generic.*;
//common imports
import static common.ptrLib.*;

public class angelkds {

    static struct_tilemap tx_tilemap, bgbot_tilemap, bgtop_tilemap;

    static UBytePtr angelkds_txvideoram = new UBytePtr();
    static UBytePtr angelkds_bgbotvideoram = new UBytePtr();
    static UBytePtr angelkds_bgtopvideoram = new UBytePtr();

    /**
     * * Text Layer Tilemap
     *
     */
    static int angelkds_txbank;

    public static GetTileInfoHandlerPtr get_tx_tile_info = new GetTileInfoHandlerPtr() {
        public void handler(int tile_index) {
            int tileno;

            tileno = angelkds_txvideoram.read(tile_index) + (angelkds_txbank * 0x100);

            SET_TILE_INFO(0, tileno, 0, 0);
        }
    };

    public static WriteHandlerPtr angelkds_txvideoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (angelkds_txvideoram.read(offset) != data) {
                angelkds_txvideoram.write(offset, data);
                tilemap_mark_tile_dirty(tx_tilemap, offset);
            }
        }
    };

    public static WriteHandlerPtr angelkds_txbank_write = new WriteHandlerPtr() {
        public void handler(int offset, int data) {

            if (angelkds_txbank != data) {
                angelkds_txbank = data;
                tilemap_mark_all_tiles_dirty(tx_tilemap);
            }

        }
    };

    /**
     * * Top Half Background Tilemap
     *
     */
    static int angelkds_bgtopbank;

    public static GetTileInfoHandlerPtr get_bgtop_tile_info = new GetTileInfoHandlerPtr() {
        public void handler(int tile_index) {
            int tileno;

            tileno = angelkds_bgtopvideoram.read(tile_index);

            tileno += angelkds_bgtopbank * 0x100;
            SET_TILE_INFO(1, tileno, 0, 0);
        }
    };

    public static WriteHandlerPtr angelkds_bgtopvideoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (angelkds_bgtopvideoram.read(offset) != data) {
                angelkds_bgtopvideoram.write(offset, data);
                tilemap_mark_tile_dirty(bgtop_tilemap, offset);
            }
        }
    };

    public static WriteHandlerPtr angelkds_bgtopbank_write = new WriteHandlerPtr() {
        public void handler(int offset, int data) {

            if (angelkds_bgtopbank != data) {
                angelkds_bgtopbank = data;
                tilemap_mark_all_tiles_dirty(bgtop_tilemap);
            };

        }
    };

    public static WriteHandlerPtr angelkds_bgtopscroll_write = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            tilemap_set_scrollx(bgtop_tilemap, 0, data);
        }
    };

    /**
     * * Bottom Half Background Tilemap
     *
     */
    static int angelkds_bgbotbank;

    public static GetTileInfoHandlerPtr get_bgbot_tile_info = new GetTileInfoHandlerPtr() {
        public void handler(int tile_index) {
            int tileno;

            tileno = angelkds_bgbotvideoram.read(tile_index);

            tileno += angelkds_bgbotbank * 0x100;
            SET_TILE_INFO(1, tileno, 1, 0);
        }
    };

    public static WriteHandlerPtr angelkds_bgbotvideoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (angelkds_bgbotvideoram.read(offset) != data) {
                angelkds_bgbotvideoram.write(offset, data);
                tilemap_mark_tile_dirty(bgbot_tilemap, offset);
            }
        }
    };

    public static WriteHandlerPtr angelkds_bgbotbank_write = new WriteHandlerPtr() {
        public void handler(int offset, int data) {

            if (angelkds_bgbotbank != data) {
                angelkds_bgbotbank = data;
                tilemap_mark_all_tiles_dirty(bgbot_tilemap);
            }

        }
    };

    public static WriteHandlerPtr angelkds_bgbotscroll_write = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            tilemap_set_scrollx(bgbot_tilemap, 0, data);
        }
    };

    static char/*UINT8*/ angelkds_layer_ctrl;

    public static WriteHandlerPtr angelkds_layer_ctrl_write = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            angelkds_layer_ctrl = (char) (data & 0xFF);
        }
    };

    /**
     * * Sprites
     *
     * the sprites are similar to the tilemaps in the sense that there is a
     * split down the middle of the screen
     *
     */
    static void draw_sprites(mame_bitmap bitmap, rectangle cliprect, int enable_n) {
        UBytePtr source = new UBytePtr(spriteram);
        UBytePtr finish = new UBytePtr(source, 0x0100);
        GfxElement gfx = Machine.gfx[2];

        while (source.offset < finish.offset) {
            /*
	
		nnnn nnnn - EeFf B?cc - yyyy yyyy - xxxx xxxx
	
		n = sprite number
		E = Sprite Enabled in Top Half of Screen
		e = Sprite Enabled in Bottom Half of Screen
		F = Flip Y
		f = Flip X
		B = Tile Bank
		? = unknown, nothing / unused? recheck
		c = color
		y = Y position
		x = X position
	
             */

            char tile_no = source.read(0);
            int attr = source.read(1);
            int ypos = source.read(2);
            int xpos = source.read(3);

            int enable = attr & 0xc0;
            int flipx = (attr & 0x10) >> 4;
            int flipy = (attr & 0x20) >> 5;
            int bank = attr & 0x08;
            int color = attr & 0x03;

            if (bank != 0) {
                tile_no += 0x100;
            }

            ypos = 0xff - ypos;

            if ((enable & enable_n) != 0) {
                drawgfx(
                        bitmap,
                        gfx,
                        tile_no,
                        color * 4,
                        flipx, flipy,
                        xpos, ypos,
                        cliprect,
                        TRANSPARENCY_PEN, 15
                );
                /* wraparound */
                if (xpos > 240) {
                    drawgfx(
                            bitmap,
                            gfx,
                            tile_no,
                            color * 4,
                            flipx, flipy,
                            xpos - 256, ypos,
                            cliprect,
                            TRANSPARENCY_PEN, 15
                    );
                }
                /* wraparound */
                if (ypos > 240) {
                    drawgfx(
                            bitmap,
                            gfx,
                            tile_no,
                            color * 4,
                            flipx, flipy,
                            xpos, ypos - 256,
                            cliprect,
                            TRANSPARENCY_PEN, 15
                    );
                    /* wraparound */
                    if (xpos > 240) {
                        drawgfx(
                                bitmap,
                                gfx,
                                tile_no,
                                color * 4,
                                flipx, flipy,
                                xpos - 256, ypos - 256,
                                cliprect,
                                TRANSPARENCY_PEN, 15
                        );
                    }
                }

            }

            source.inc(0x04);

        }

    }

    /**
     * * Palette Handling
     *
     * 4 bits of Red, 4 bits of Green, 4 bits of Blue
     *
     */
    public static WriteHandlerPtr angelkds_paletteram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int no, r, g, b;

            paletteram.write(offset, data);

            no = offset & 0xff;

            g = (paletteram.read(no) & 0xf0) << 0;

            r = (paletteram.read(no) & 0x0f) << 4;

            b = (paletteram.read(no + 0x100) & 0x0f) << 4;

            palette_set_color(no, r, g, b);
        }
    };

    /**
     * * Video Start & Update
     *
     */
    public static VideoStartHandlerPtr video_start_angelkds = new VideoStartHandlerPtr() {
        public int handler() {

            tx_tilemap = tilemap_create(get_tx_tile_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, 8, 8, 32, 32);
            tilemap_set_transparent_pen(tx_tilemap, 0);

            bgbot_tilemap = tilemap_create(get_bgbot_tile_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, 8, 8, 32, 32);
            tilemap_set_transparent_pen(bgbot_tilemap, 15);

            bgtop_tilemap = tilemap_create(get_bgtop_tile_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, 8, 8, 32, 32);
            tilemap_set_transparent_pen(bgtop_tilemap, 15);

            return 0;
        }
    };

    /* enable bits are uncertain */
    public static VideoUpdateHandlerPtr video_update_angelkds = new VideoUpdateHandlerPtr() {
        public void handler(mame_bitmap bitmap, rectangle cliprect) {
            rectangle clip = new rectangle();

            fillbitmap(bitmap, 0x3f, cliprect);
            /* is there a register controling the colour?, we currently use the last colour of the tx palette */

 /* draw top of screen */
            clip.min_x = 8 * 0;
            clip.max_x = 8 * 16 - 1;
            clip.min_y = Machine.visible_area.min_y;
            clip.max_y = Machine.visible_area.max_y;
            if ((angelkds_layer_ctrl & 0x80) == 0x00) {
                tilemap_draw(bitmap, clip, bgtop_tilemap, 0, 0);
            }
            draw_sprites(bitmap, clip, 0x80);
            if ((angelkds_layer_ctrl & 0x20) == 0x00) {
                tilemap_draw(bitmap, clip, tx_tilemap, 0, 0);
            }

            /* draw bottom of screen */
            clip.min_x = 8 * 16;
            clip.max_x = 8 * 32 - 1;
            clip.min_y = Machine.visible_area.min_y;
            clip.max_y = Machine.visible_area.max_y;
            if ((angelkds_layer_ctrl & 0x40) == 0x00) {
                tilemap_draw(bitmap, clip, bgbot_tilemap, 0, 0);
            }
            draw_sprites(bitmap, clip, 0x40);
            if ((angelkds_layer_ctrl & 0x20) == 0x00) {
                tilemap_draw(bitmap, clip, tx_tilemap, 0, 0);
            }
        }
    };
}
