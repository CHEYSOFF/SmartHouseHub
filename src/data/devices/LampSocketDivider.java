package data.devices;

import data.packetdata.Device;

public abstract class LampSocketDivider extends DeviceBase {
    public Device type = null;

    public LampSocketDivider(byte[] bytes, Device t) {
        super(bytes);
        type = t;
    }

    public Boolean isLamp() {
        return type == Device.Lamp;
    };

}