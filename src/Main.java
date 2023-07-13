import converters.CRC8;
import converters.UrlBase64;
import data.Device;
import data.Packet;
import data.Payload;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class Main {

    private static final String SERVER_URL = "http://localhost:9998";


    private static String sendDataToNetwork(String data) {
        try {
            URL url = new URL(SERVER_URL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

            outputStream.writeBytes(data);
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();


            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            System.out.println("Code: " + responseCode);
            System.out.println(response);
            connection.disconnect();


            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static ArrayList<Packet> decodeBytesToPackets(String data) {
        ArrayList<Packet> result = new ArrayList<>();
        byte[] bytes = UrlBase64.decode(data);

        int start = 0;
        while (start < bytes.length) {
            byte length = bytes[start];
            byte[] payload = Arrays.copyOfRange(bytes, start + 1, start + length + 1);
            byte crc8 = bytes[start + length + 1];
            start = start + length + 2;
            result.add(new Packet(length, payload, crc8));
        }

        return result;
    }

    public static String encodePackets(ArrayList<Packet> data) {
        int totalSize = 0;
        for (Packet packet : data) {
            totalSize += packet.length + 2;
        }
        byte[] result = new byte[totalSize];

        int start = 0;

        for (Packet packet : data) {
            int totalLength = 2 + packet.length;

            byte[] byteArray = new byte[totalLength];

            byteArray[0] = packet.length;
            System.arraycopy(packet.payload, 0, byteArray, 1, packet.length);
            byteArray[totalLength - 1] = packet.crc8;

            System.arraycopy(byteArray, 0, result, start, packet.length + 2);
            start += packet.length + 2;

        }


        return UrlBase64.encode(result);
    }

    public static void main(String[] args) {
        String val = sendDataToNetwork("");
        ArrayList<Packet> packets = decodeBytesToPackets(val);
        for (Packet packet : packets) {
            System.out.println(packet.length);
            System.out.println(Arrays.toString(packet.payload));
            System.out.println(packet.crc8);
        }
        System.out.println(encodePackets(packets));


    }

}