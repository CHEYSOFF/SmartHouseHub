package data;


import data.devices.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Payload implements Serializable {


    public Varuint src;
    public Varuint dst;
    public Varuint serial;
    public Device dev_type;
    public Command cmd;
    public DeviceInfo cmd_body;

    public Payload() {

    }

    public Payload(byte[] bytes) {
        int start = 0;
        this.src = new Varuint(Arrays.copyOfRange(bytes, start, bytes.length));
        start += this.src.skipped;
        this.dst = new Varuint(Arrays.copyOfRange(bytes, start, bytes.length));
        start += this.dst.skipped;
        this.serial = new Varuint(Arrays.copyOfRange(bytes, start, bytes.length));
        start += this.serial.skipped;

        this.dev_type = Device.fromValue(bytes[start]);
        start++;
        this.cmd = Command.fromValue(bytes[start]);
        start++;
//        this.cmd_body = Arrays.copyOfRange(bytes, start, bytes.length);

        byte[] left = Arrays.copyOfRange(bytes, start, bytes.length);

        switch (cmd) {
            case WHOISHERE, IAMHERE -> {
                if (dev_type == Device.SmartHub) {
                    cmd_body = new SmartHub(left);
                } else if (dev_type == Device.EnvSensor) {
                    cmd_body = new EnvSensor(left);
                } else if (dev_type == Device.Switch) {
                    cmd_body = new Switch(left);
                }
                else if (dev_type == Device.Lamp || dev_type == Device.Socket) {
                    cmd_body = new LampSocket(bytes, dev_type);
                }
            }
            case STATUS -> {
                if (dev_type == Device.EnvSensor) {
                    cmd_body = new EnvSensorStatus(left);
                } else if (dev_type == Device.Switch) {
                    cmd_body = new SwitchStatus(left);
                }
                else if (dev_type == Device.Lamp || dev_type == Device.Socket) {
                    cmd_body = new LampSocketStatus(bytes, dev_type, cmd);
                }
            }
            case SETSTATUS -> {
                if (dev_type == Device.Lamp || dev_type == Device.Socket) {
                    cmd_body = new LampSocketStatus(bytes, dev_type, cmd);
                }
            }
            case TICK -> {
                cmd_body = new Clock(left);
            }
        }
    }

    public byte[] groupPayload() {
        ArrayList<byte[]> res = new ArrayList<>();
        res.add(src.encode());
        res.add(dst.encode());
        res.add(serial.encode());
        res.add(new byte[]{this.dev_type.getValue()});
        res.add(new byte[]{this.cmd.getValue()});
        res.add(cmd_body.encode());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (byte[] byteArray : res) {
            try {
                outputStream.write(byteArray);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return outputStream.toByteArray();
    }
}
