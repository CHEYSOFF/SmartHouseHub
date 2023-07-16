package data.devices;

import data.Command;
import data.Device;

import java.io.ByteArrayOutputStream;

public class LampSocketStatusDivider extends LampSocketDivider{

    public Command command = null;
    public LampSocketStatusDivider(byte[] bytes, Device t, Command c) {
        super(bytes, t);
        command = c;
    }

    public Boolean isSet() {
        return command == Command.SETSTATUS;
    }
}
