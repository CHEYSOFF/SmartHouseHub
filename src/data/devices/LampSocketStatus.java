package data.devices;

import data.packetdata.Command;
import data.packetdata.Device;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class LampSocketStatus extends LampSocketStatusDivider {
    public byte status;

    public LampSocketStatus() {
        super();
    }

    public LampSocketStatus(byte[] bytes, Device t, Command c) {
        super(new byte[0], t, c);
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
