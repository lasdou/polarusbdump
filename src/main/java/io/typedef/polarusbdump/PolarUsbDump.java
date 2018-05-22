package io.typedef.polarusbdump;

import com.codeminders.hidapi.ClassPathLibraryLoader;
import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDManager;
import io.typedef.polar.io.PolarFileSystem;
import io.typedef.polar.io.PolarService;
import io.typedef.polar.stream.PolarStream;
import io.typedef.polarusbdump.downloader.DirectoryDownloader;
import io.typedef.polarusbdump.downloader.DirectoryFilter;
import io.typedef.polarusbdump.downloader.FileDownloader;
import io.typedef.polarusbdump.downloader.FileFilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PolarUsbDump {

    private final static int POLAR_VENDOR_ID = 0x0da4;
    private final static int POLAR_PRODUCT_ID = 0x0009;

    public static void main(String[] args) throws IOException {
        ClassPathLibraryLoader.loadNativeHIDLibrary();
        System.out.println("[+] polarusbdump started");

        HIDDevice hid = null;

        try {
            hid = HIDManager.getInstance().openById(POLAR_VENDOR_ID, POLAR_PRODUCT_ID, null);
            System.out.println("[+] found " + hid.getProductString() + ":" + hid.getSerialNumberString());

            PolarService service = new PolarService(hid);
            PolarFileSystem filesystem = new PolarFileSystem(service);

            Path target = Paths.get(System.getProperty("user.home"), ".polar/backup/", hid.getSerialNumberString());
            System.out.println("[+] dumping into " + target);

            if (!Files.exists(target)) {
                Files.createDirectories(target);
            }

            long count = PolarStream.stream(filesystem)
                .filter(new DirectoryFilter(target))
                .filter(new FileFilter(target))
                .peek(new DirectoryDownloader(target))
                .peek(new FileDownloader(target, filesystem))
                .count();

            System.out.println("[+] " + count + " entries dumped");
        } finally {
            if (hid != null) {
                hid.close();
            }
        }

        System.exit(0);
    }
}
