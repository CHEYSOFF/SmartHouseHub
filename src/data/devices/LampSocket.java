package data.devices;

import data.Device;

import java.io.ByteArrayOutputStream;

public class LampSocket extends LampSocketDivider{

    public LampSocket(byte[] bytes, Device t) {
        super(bytes, t);
    }

    @Override
    public byte[] encode() {
        return super.encode();
    }


}
