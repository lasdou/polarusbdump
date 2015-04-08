package pl.niekoniecznie.polar.service;

import pl.niekoniecznie.polar.usb.USBPacket;

import java.nio.charset.Charset;

/**
 * Created by ak on 05.04.15.
 */
public class PolarRequest {

    private String url;

    public PolarRequest(String url) {
        this.url = url;
    }

    public USBPacket getPacket() {
        byte[] data = new byte[url.length() + 8];
        Integer length1 = url.length() + 4;
        Integer length2 = url.length();

        data[0] = 0x00;
        data[1] = length1.byteValue();
        data[2] = 0x00;
        data[3] = 0x08;
        data[4] = 0x00;
        data[5] = 0x12;
        data[6] = length2.byteValue();

        byte[] tmp = url.getBytes(Charset.forName("UTF-8"));

        for (int i = 0; i < length2; i++) {
            data[7 + i] = tmp[i];
        }

        data[data.length - 1] = 0x00;

        USBPacket result0 = new USBPacket();

        result0.setByte0((byte) 0x01);
        result0.setData(data);

        return result0;
    }
}