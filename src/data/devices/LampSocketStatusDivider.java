package data.devices;

import data.packetdata.Command;
import data.packetdata.Device;

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
