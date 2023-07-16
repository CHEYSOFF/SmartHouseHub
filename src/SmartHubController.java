import converters.CRC8;
import converters.UrlBase64;
import data.devices.*;
import data.packetdata.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class SmartHubController {

    ArrayList<Payload> devices;
    SmartHub smartHubBody;
    Payload smartHubPayload;
    int serial = 1;

    int id = 1;

    public SmartHubController(int id) {
        devices = new ArrayList<>();
        this.id = id;

        smartHubBody = new SmartHub();
        smartHubBody.dev_name = "HUB01";

        smartHubPayload = new Payload();
        smartHubPayload.src = new Varuint(id);
        smartHubPayload.serial = new Varuint(serial++);
        smartHubPayload.dev_type = Device.SmartHub;
        smartHubPayload.cmd_body = smartHubBody;
    }

    public void start() {
        String val = sendDataToNetwork(WhoIsHere());

        ArrayList<Packet> packets = decodeBytesToPackets(val);
        for (Packet packet : packets) {
            if (packet.payload.cmd == Command.IAMHERE) {
                devices.add(packet.payload);
            }
        }

        int size = devices.size();
        for (int i = 0; i < size; i++) {
            Payload device = devices.get(i);
            String statusPacket = GetStatus(Math.toIntExact(device.src.value), device.dev_type);
            String response = sendDataToNetwork(statusPacket);
            ArrayList<Packet> status = decodeBytesToPackets(response);
            ApplyStatus(status);
            size = devices.size();
        }

        while (true) {
            ArrayList<Packet> status = decodeBytesToPackets(sendDataToNetwork(""));
            ApplyStatus(status);
        }


    }

    private static final String SERVER_URL = "http://localhost:9998";

    private static String sendDataToNetwork(String data) {
        System.out.println("    " + data);
        try {
            URL url = new URL(SERVER_URL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

            outputStream.writeBytes(data);
            outputStream.flush();
            outputStream.close();
            connection.setReadTimeout(300);
            connection.setConnectTimeout(300);

            try {
                int responseCode = connection.getResponseCode();
                if (responseCode == 204) {
                    System.exit(0);
                }
                if (responseCode != 200) {
                    System.exit(99);
                }

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
                System.out.println(response);
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
            if (crc8 != CRC8.getCRC8(payload)) {
                continue;
            }
            result.add(new Packet(length, payload, crc8));
        }

        return result;
    }

    private String GetPacketFromPayload(byte[] groupedPayload) {
        int len = groupedPayload.length;

        int totalLength = 1 + len;
        ByteBuffer buffer = ByteBuffer.allocate(totalLength);

        buffer.put((byte) len);
        buffer.put(groupedPayload);

        byte crc8 = CRC8.getCRC8(groupedPayload);

        Packet testSend = new Packet((byte) len, groupedPayload, crc8);

        return UrlBase64.encode(testSend.pack());
    }

    private String WhoIsHere() {
        smartHubPayload.dst = new Varuint(16383);
        smartHubPayload.serial = new Varuint(serial++);
        smartHubPayload.cmd = Command.WHOISHERE;
        smartHubPayload.dev_type = Device.SmartHub;

        return GetPacketFromPayload(smartHubPayload.groupPayload());
    }


    private String GetStatus(int id, Device type) {
        smartHubPayload.dst = new Varuint(id);
        smartHubPayload.serial = new Varuint(serial++);
        smartHubPayload.cmd = Command.GETSTATUS;
        smartHubPayload.cmd_body = new GetStatus();
        smartHubPayload.dev_type = type;
        System.out.println("GS");
        return GetPacketFromPayload(smartHubPayload.groupPayload());
    }

    private String SetStatus(int id, byte power, Device type) {
        System.out.println("SS");
        smartHubPayload.dst = new Varuint(id);
        smartHubPayload.serial = new Varuint(serial++);
        smartHubPayload.cmd = Command.SETSTATUS;
        LampSocketStatus tmp = new LampSocketStatus();
        tmp.status = power;
        smartHubPayload.cmd_body = tmp;
        smartHubPayload.dev_type = type;

        return GetPacketFromPayload(smartHubPayload.groupPayload());
    }

    private void ApplyStatus(ArrayList<Packet> status) {
        for (Packet packet : status) {
            switch (packet.payload.cmd) {
                case TICK -> {

                }
                case STATUS -> {
                    if (packet.payload.dev_type == Device.Switch) {
                        byte power = ((SwitchStatus) packet.payload.cmd_body).status;
                        Long switchId = packet.payload.src.value;
                        ArrayList<Payload> care = new ArrayList<>();
                        HashSet<String> names = new HashSet<>();
                        for (Payload device : devices) {
                            if (device.src.value.equals(switchId) && device.dev_type == Device.Switch) {
                                names.addAll(((Switch) device.cmd_body).dev_names);
                            }
                        }

                        for (Payload device : devices) {
                            if (device.dev_type == Device.Socket || device.dev_type == Device.Lamp
                                    && names.contains(((DeviceBase) device.cmd_body).dev_name)) {
                                String statusString = SetStatus(((DeviceBase) device.cmd_body).id, power, device.dev_type);
                                sendDataToNetwork(statusString);
                            }
                        }
                    }
                    else if(packet.payload.dev_type == Device.EnvSensor) {
                        ApplyTriggers(packet.payload);
                    }
//                    if (packet.payload.dev_type == Device.Lamp || packet.payload.dev_type == Device.Socket) {
//                        byte power = ((LampSocketStatus) packet.payload.cmd_body).status;
//                        String statusString = SetStatus(((DeviceBase) packet.payload.cmd_body).id, power, packet.payload.dev_type);
//                        sendDataToNetwork(statusString);
//                    }
                }
                case IAMHERE -> {
                    devices.add(packet.payload);
                }
            }
        }
    }

    private void ApplyTriggers(Payload sensor) {
        for (Payload device : devices) {
            if (device.dev_type == Device.EnvSensor && ((DeviceBase) device.cmd_body).id == sensor.src.value.intValue()) {
                ArrayList<Varuint> values = ((EnvSensorStatus) device.cmd_body).values;
            }
        }
    }

}
