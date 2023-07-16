import converters.CRC8;
import converters.UrlBase64;
import data.devices.Clock;
import data.devices.DeviceInfo;
import data.devices.SmartHub;
import data.packetdata.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SmartHubController {

    HashMap<Long, DeviceInfo> devices;

    String whoIsHere;
    SmartHub smartHubBody;
    Payload smartHubPayload;
    int serial = 1;

    public SmartHubController(int id) {
        devices = new HashMap<>();

        smartHubBody = new SmartHub();
        smartHubBody.dev_name = "HUB01";

        smartHubPayload = new Payload();
        smartHubPayload.src = new Varuint(id);
        smartHubPayload.dst = new Varuint(0x3FFF);
        smartHubPayload.serial = new Varuint(serial);
        smartHubPayload.dev_type = Device.SmartHub;
        smartHubPayload.cmd = Command.WHOISHERE;
        smartHubPayload.cmd_body = smartHubBody;

        byte[] groupedPayload = smartHubPayload.groupPayload();

        int len = groupedPayload.length;

        int totalLength = 1 + len;
        ByteBuffer buffer = ByteBuffer.allocate(totalLength);

        buffer.put((byte) len);
        buffer.put(groupedPayload);

        byte crc8 = CRC8.getCRC8(groupedPayload);

        Packet testSend = new Packet((byte) len, groupedPayload, crc8);

        whoIsHere = UrlBase64.encode(testSend.pack());
    }

    public void start() {
        String val = sendDataToNetwork(whoIsHere);

        ArrayList<Packet> packets = decodeBytesToPackets(val);
        for (Packet packet : packets) {
            if (packet.payload.cmd == Command.IAMHERE) {
                devices.put(packet.payload.src.value, packet.payload.cmd_body);
            }
        }

        while (true) {
            String str = sendDataToNetwork("");
            ArrayList<Packet> upd = decodeBytesToPackets(str);
            for (Packet packet : upd) {
                if (packet.payload.cmd == Command.IAMHERE) {
                    devices.put(packet.payload.src.value, packet.payload.cmd_body);
                }
            }
            try {
                Thread.sleep(100);
            }
            catch (Exception ignored) {

            }

        }
    }

    private static final String SERVER_URL = "http://localhost:9998";

    private static String getDataFromNetwork() {
        try {
            URL url = new URL(SERVER_URL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);

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

//            System.out.println("Code: " + responseCode);
//            System.out.println(response);
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

            System.arraycopy(byteArray, 0, result, start, byteArray.length);
            start += byteArray.length;

        }


        return UrlBase64.encode(result);
    }
}
