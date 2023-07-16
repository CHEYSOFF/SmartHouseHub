package data.devices;

import data.packetdata.Varuint;

public class Clock implements DeviceInfo {
    public Varuint time;

    public Clock(byte[] bytes) {
        time = new Varuint(bytes);
    }

    @Override
    public byte[] encode() {
        return time.encode();
    }
}
