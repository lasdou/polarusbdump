package pl.niekoniecznie.polar.io;

public class PolarService {

    private final PolarDevice device;

    public PolarService(final PolarDevice device) {
        this.device = device;
    }

    public synchronized PolarResponse doRequest(PolarRequest request) {
        device.write(request.getPacket());

        PolarResponse response = new PolarResponse();

        while (true) {
            PolarPacket packet = device.read();
            response.append(packet);

            if (packet.isContinuation()) {
                device.write(response.getPacket());
            } else {
                break;
            }
        }

        return response;
    }
}