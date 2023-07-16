package data.devices;

import data.packetdata.Device;

public class LampSocket extends LampSocketDivider{

    public LampSocket(byte[] bytes, Device t) {
        super(bytes, t);
    }

    @Override
    public byte[] encode() {
        return super.encode();
    }


}
