import converters.CRC8;
import converters.UrlBase64;
import data.*;
import data.devices.Clock;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
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

            byte[] byteArray = packet.pack();

            System.arraycopy(byteArray, 0, result, start, result.length);
            start += byteArray.length;

        }


        return UrlBase64.encode(result);
    }

    public static void main(String[] args) {
//        Payload testPayload = new Payload();
//        testPayload.src = new Varuint(1);
//        testPayload.dst = new Varuint(0x3FFF);
//        testPayload.serial = new Varuint(1);
//        testPayload.dev_type = Device.SmartHub;
//        testPayload.cmd = Command.WHOISHERE;
//        testPayload.cmd_body = new byte[]{5, 72, 85, 66, 48, 49};
//
//        byte[] groupedPayload = testPayload.groupPayload();
//
//        int len = groupedPayload.length;
//
//        int totalLength = 1 + len; // 2 bytes for length and 1 byte for crc8
//        ByteBuffer buffer = ByteBuffer.allocate(totalLength);
//
//        buffer.put((byte) len);
//        buffer.put(groupedPayload);
//
//        byte crc8 = CRC8.getCRC8(groupedPayload);
//
//        Packet testSend = new Packet((byte) len, groupedPayload, crc8);
//
//        String tmp = UrlBase64.encode(testSend.pack());
//        System.out.println(tmp);
//
//        String val = sendDataToNetwork(tmp);


        String val = sendDataToNetwork("");
        ArrayList<Packet> packets = decodeBytesToPackets(val);
        for (Packet packet : packets) {
            System.out.println(packet.length);
//            System.out.println(Arrays.toString(packet.payload));
//            for (byte b: packet.payload) {
//                System.out.print(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
//            }
            System.out.println(packet.payload.src.value);
            System.out.println(packet.payload.dst.value);
            if (packet.payload.cmd == Command.TICK) {
                Clock tmp = (Clock) packet.payload.cmd_body;
                System.out.println(tmp.time.value);
            }
            System.out.println(packet.crc8);
        }
        System.out.println(encodePackets(packets));

//        String b = "0010110011000001";
//        short a = Short.parseShort(b, 2);
//        ByteBuffer bytes = ByteBuffer.allocate(2).putShort(a);
//
//        byte[] array = bytes.array();
//
//        System.out.println(CRC8.getCRC8(array));

    }

}