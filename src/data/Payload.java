package data;


import java.io.*;

public class Payload implements Serializable {
    private int src;
    private int dst;
    private int serial;
    private byte dev_type;
    private byte cmd;
    private byte[] cmd_body;

    public Payload(int src, int dst, int serial, byte dev_type, byte cmd, byte[] cmd_body) {
        this.src = src;
        this.dst = dst;
        this.serial = serial;
        this.dev_type = dev_type;
        this.cmd = cmd;
        this.cmd_body = cmd_body;
    }

}
