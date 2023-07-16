package data.devices;

import converters.StringBytes;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class Switch extends DeviceBase {
    public ArrayList<String> dev_names;

    public Switch(byte[] bytes) {
        super(bytes);
        int start = dev_name.length() + 1;
        dev_names = new ArrayList<>();
        int length = bytes[start];
        start++;
        while (start < bytes.length) {
            int strLen = bytes[start];
            dev_names.add(StringBytes.bytesToString(Arrays.copyOfRange(bytes, start, bytes.length)));
            start += strLen + 1;
        }
    }


    @Override
    public byte[] encode() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(super.encode());
            outputStream.write(dev_names.size());
            for (String str : dev_names) {
                outputStream.write(StringBytes.stringToBytes(str));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }
}
