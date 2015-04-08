package pl.niekoniecznie.polar.service;

import pl.niekoniecznie.polar.usb.USBPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ak on 05.04.15.
 */
public class PolarResponse {

    private byte lastSequenceNumber;
    private List<Byte> body = new ArrayList<>();

    public USBPacket getPacket() {
        byte[] data = new byte[1];
        data[0] = lastSequenceNumber;

        USBPacket result0 = new USBPacket();

        result0.setByte0((byte) 0x01);
        result0.setBoolean0(true);
        result0.setData(data);

        return result0;
    }

    public void append(USBPacket packet) {
        byte[] data = packet.getData();
        lastSequenceNumber = data[0];

        for (int i = 1; i < data.length; i++) {
            body.add(data[i]);
        }
    }

    public List<Byte> getBody() {
        return body.subList(2, body.size() - 1);
    }
}