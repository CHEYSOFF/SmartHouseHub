package data.devices;

import converters.StringBytes;

public abstract class DeviceBase implements DeviceInfo {
    String dev_name;

    public DeviceBase(byte[] bytes) {
        dev_name = StringBytes.bytesToString(bytes);
    }

    public byte[] encode() {
        return StringBytes.stringToBytes(dev_name);
    }
}
