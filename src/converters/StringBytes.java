package converters;

public class StringBytes {
    static public String bytesToString(byte[] bytes) {
        if (bytes.length == 0) {
            return "";
        }
        int length = Byte.toUnsignedInt(bytes[0]);

        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < length + 1; i++) {
            builder.append((char) bytes[i]);
        }

        return builder.toString();
    }

    static public byte[] stringToBytes(String str) {
        if (str.length() == 0) {
            return new byte[0];
        }
        int length = str.length();
        byte[] result = new byte[length + 1];
        result[0] = (byte) length;
        for (int i = 0; i < length; i++) {
            result[i + 1] = (byte) str.charAt(i);
        }
        return result;
    }
}
