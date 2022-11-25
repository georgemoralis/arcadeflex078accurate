package common;

/**
 *
 * @author shadow
 */
public class ptrLib {

    /**
     * Unsigned char * emulation class
     */
    public static class UBytePtr {

        public int bsize = 1;
        public char[] memory;
        public int offset;

        public UBytePtr() {
        }

        public UBytePtr(int size) {
            memory = new char[size];
            offset = 0;
        }

        public UBytePtr(char[] m, int b) {
            memory = m;
            offset = b;
        }

        public UBytePtr(UBytePtr cp) {
            memory = cp.memory;
            offset = cp.offset;
        }

        public UBytePtr(UBytePtr cp, int b) {
            memory = cp.memory;
            offset = cp.offset + b;
        }

        public char read() {
            return (char) (memory[offset] & 0xFF);
        }

        public char read(int index) {
            return (char) (memory[offset + index] & 0xFF);
        }

        public char readinc() {
            return (char) ((memory[(this.offset++)]) & 0xFF);
        }

        public void write(int value) {
            memory[offset] = (char) (value & 0xFF);
        }

        public void write(int index, int value) {
            memory[offset + index] = (char) (value & 0xFF);
        }

        public void inc() {
            offset += bsize;
        }

        public void inc(int count) {
            offset += count * bsize;
        }
    }

    public static class ShortPtr {

        public int bsize = 2;
        public byte[] memory;
        public int offset;

        public ShortPtr() {
        }

        public ShortPtr(int size) {
            memory = new byte[size * bsize];
            offset = 0;
        }

        public ShortPtr(ShortPtr cp) {
            memory = cp.memory;
            offset = cp.offset;
        }

        public ShortPtr(ShortPtr cp, int b) {
            memory = cp.memory;
            offset = cp.offset + b;
        }

        public ShortPtr(byte[] m) {
            memory = m;
            offset = 0;
        }

        public short read() {
            return (short) ((memory[offset + 1] & 0xFF) << 8 | (memory[offset] & 0xFF));
        }

        public short read(int index) {
            return (short) ((memory[offset + 1 + index * 2] & 0xFF) << 8 | (memory[offset + index * 2] & 0xFF));
        }

        public void write(short data) {
            memory[offset] = (byte) (data & 0xff);
            memory[offset + 1] = (byte) ((data >>> 8) & 0xff);
        }

        public void write(int index, short data) {
            memory[offset + index * 2] = (byte) (data & 0xff);
            memory[offset + index * 2 + 1] = (byte) ((data >>> 8) & 0xff);
        }
    }
}
