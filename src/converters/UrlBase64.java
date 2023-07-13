package converters;
import java.util.Arrays;
import java.util.Base64;

public class UrlBase64 {
    public static byte[] decode(String bytes) {
        return Base64.getUrlDecoder().decode(bytes);
    }

    public static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
