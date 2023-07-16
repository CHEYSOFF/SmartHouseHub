package data.packetdata;

public enum Device {
    SmartHub(0x01),
    EnvSensor(0x02),
    Switch(0x03),
    Lamp(0x04),
    Socket(0x05),
    Clock(0x06);

    private final int value;

    Device(int value) {
        this.value = value;
    }

    public static Device fromValue(byte value) {
        for (Device type : Device.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid DeviceType value: " + value);
    }

    public byte getValue() {
        return (byte) value;
    }
}
