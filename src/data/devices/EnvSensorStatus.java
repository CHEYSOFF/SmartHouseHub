package data.devices;

import data.packetdata.Varuint;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class EnvSensorStatus extends DeviceBase {
    ArrayList<Varuint> values;

    public EnvSensorStatus(byte[] bytes) {
        super(bytes);
        int start = dev_name.length() + 1;
        values = new ArrayList<>();
        int length = Byte.toUnsignedInt(bytes[start]);
        start++;
        while (start < bytes.length) {
            Varuint cur = new Varuint(Arrays.copyOfRange(bytes, start, bytes.length));
            values.add(cur);
            start += cur.skipped;
        }
    }

    @Override
    public byte[] encode() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(super.encode());
            outputStream.write(values.size());
            for (Varuint num : values) {
                outputStream.write(num.encode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }
}
