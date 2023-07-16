package data.devices;

import converters.StringBytes;
import data.packetdata.Varuint;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class EnvSensor extends DeviceBase {
    byte sensors;
    ArrayList<Trigger> triggers;

    public EnvSensor(byte[] bytes) {
        super(bytes);
        int start = dev_name.length() + 1;
        sensors = bytes[start];
        start++;
        int len = bytes[start];
        start++;
        triggers = new ArrayList<>();
        while (start < bytes.length) {
            int length = bytes[start];
            start++;
            triggers.add(new Trigger(Arrays.copyOfRange(bytes, start, start + length + 1)));
            start += length;
        }
    }

    @Override
    public byte[] encode() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(super.encode());
            outputStream.write(sensors);
            outputStream.write(triggers.size());
            for (Trigger trigger: triggers) {
                byte[] trigger_bytes = trigger.encode();
                outputStream.write((byte) trigger_bytes.length);
                outputStream.write(trigger_bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    class Trigger implements DeviceInfo {
        byte op;
        Varuint value;
        String name;

        public Trigger(byte[] bytes) {
            op = bytes[0];
            int start = 1;
            value = new Varuint(Arrays.copyOfRange(bytes, start, bytes.length));
            start += value.skipped;
            int length = bytes[start];
//            start++;
            name = StringBytes.bytesToString(Arrays.copyOfRange(bytes, start, bytes.length));

        }

        @Override
        public byte[] encode() {
            int length = name.length();
            byte[] value_bytes = value.encode();
            byte[] name_bytes = StringBytes.stringToBytes(name);
            byte[] result = new byte[1 + value_bytes.length + 1 + length];

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try {
                outputStream.write((byte) length);
                outputStream.write(op);
                outputStream.write(value_bytes);
                outputStream.write(name_bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return outputStream.toByteArray();
        }
    }

}
