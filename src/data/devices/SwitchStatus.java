package data.devices;

import converters.StringBytes;

import java.io.ByteArrayOutputStream;

public class SwitchStatus extends DeviceBase{
    byte status;

    public SwitchStatus(byte[] bytes) {
        super(bytes);
        int start = dev_name.length() + 1;
        status = bytes[start];
    }

    @Override
    public byte[] encode() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(super.encode());
            outputStream.write(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }
}
