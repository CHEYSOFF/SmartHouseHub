package data.devices;

import converters.StringBytes;

import java.io.ByteArrayOutputStream;

public class SwitchStatus extends DeviceBase{
    public byte status;

    public SwitchStatus(byte[] bytes) {
        super(new byte[0]);
        status = bytes[0];
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
