package data.devices;

import data.Command;
import data.Device;

import java.io.ByteArrayOutputStream;

public class LampSocketStatus extends LampSocketStatusDivider {
    byte status;

    public LampSocketStatus(byte[] bytes, Device t, Command c) {
        super(bytes, t, c);
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
