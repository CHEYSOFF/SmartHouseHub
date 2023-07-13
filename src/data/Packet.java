package data;

import converters.UrlBase64;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class Packet {
     public byte length;
     public byte[] payload;
     public byte crc8;

    public Packet(byte length, byte[] payload, byte crc8) {
        this.length = length;
        this.payload = payload;
        this.crc8 = crc8;
    }


}


