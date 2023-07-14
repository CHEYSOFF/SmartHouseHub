package data;

import converters.UrlBase64;

import java.io.*;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class Packet {
     public byte length;
     public Payload payload;
     public byte crc8;

    public Packet(byte length, byte[] payload, byte crc8) {
        this.length = length;
        this.payload = new Payload(payload);
        this.crc8 = crc8;
    }

    public byte[] pack() {
        int totalLength = 2 + length; // 2 bytes for length and 1 byte for crc8
        ByteBuffer buffer = ByteBuffer.allocate(totalLength);

        buffer.put(length);
        buffer.put(payload.groupPayload());
        buffer.put(crc8);

        return buffer.array();
    }

}


