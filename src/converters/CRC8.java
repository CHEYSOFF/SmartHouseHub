package converters;

public class CRC8 {

    public static byte getCRC8(byte[] bytes) {
        byte generator = 0x1D;
        byte crc = 0; /* start with 0 so first byte can be 'xored' in */

        for (byte currByte : bytes) {
            crc ^= currByte; /* XOR-in the next input byte */

            for (int i = 0; i < 8; i++) {
                if ((crc & 0x80) != 0) {
                    crc = (byte) ((crc << 1) ^ generator);
                } else {
                    crc <<= 1;
                }
            }
        }
        return crc;
    }

}
