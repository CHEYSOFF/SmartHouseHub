package data;

public enum Command {
    WHOISHERE(0x01),
    IAMHERE(0x02),
    GETSTATUS(0x03),
    STATUS(0x04),
    SETSTATUS(0x05),
    TICK(0x06);

    private int value;

    Command(int value) {
        this.value = value;
    }

    public byte getValue() {
        return (byte) value;
    }

    public static Command fromValue(byte value) {
        for (Command cmd : Command.values()) {
            if (cmd.getValue() == value) {
                return cmd;
            }
        }
        throw new IllegalArgumentException("Invalid command value: " + value);
    }
}

