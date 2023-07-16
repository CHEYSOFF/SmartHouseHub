package data.packetdata;

import java.util.ArrayList;

public class Varuint {
    final public Long value;
    final public int skipped;

    public Varuint(long x) {
        value = (long) x;
        skipped = 0;
    }

    public Varuint(byte[] bytes) {
        long value = 0;
        int bitSize = 0;
        int read;

        int index = 0;
        do {
            read = bytes[index++];
            value += ((long) read & 0x7f) << bitSize;
            bitSize += 7;
            if (bitSize >= 64) {
                throw new ArithmeticException("ULEB128 value exceeds maximum value for long type.");
            }
        } while ((read & 0x80) != 0);

        this.value = value;
        this.skipped = index;
    }

    public byte[] encode() {

        ArrayList<Byte> result = new ArrayList<>();
        long val = value;
        while (true) {
            byte b = (byte) (val & 0x7f);
            val >>= 7;
            if (val != 0) {
                result.add((byte) (b | 0x80));
            } else {
                result.add(b);
                break;
            }
        }

        byte[] ret = new byte[result.size()];
        for (int i = 0; i < result.size(); i++) {
            ret[i] = result.get(i);
        }
        return ret;
    }
}
