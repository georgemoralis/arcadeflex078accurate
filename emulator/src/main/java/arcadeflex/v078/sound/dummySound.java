/**
 * ported to v0.78
 */
package arcadeflex.v078.sound;

//mame imports
import static arcadeflex.v078.mame.sndintrf.*;
import static arcadeflex.v078.mame.sndintrfH.*;

public class dummySound extends snd_interface {

    public dummySound() {
        sound_num = SOUND_DUMMY;
        name = "";
    }

    @Override
    public int chips_num(MachineSound msound) {
        return 0;
    }

    @Override
    public int chips_clock(MachineSound msound) {
        return 0;
    }

    @Override
    public int start(MachineSound msound) {
        return 0;
    }

    @Override
    public void stop() {
    }

    @Override
    public void update() {
    }

    @Override
    public void reset() {
    }
}
