package data.devices;

import data.Varuint;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class Clock extends DeviceBase {
    public Varuint time;

    public Clock(byte[] bytes) {
        super(bytes);
        int start = dev_name.length() + 1;
        time = new Varuint(Arrays.copyOfRange(bytes, start, bytes.length));
    }

    @Override
    public byte[] encode() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(super.encode());
            outputStream.write(time.encode());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }
}
