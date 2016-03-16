package pl.niekoniecznie.p2e;

import com.codeminders.hidapi.ClassPathLibraryLoader;
import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.niekoniecznie.polar.io.PolarEntry;
import pl.niekoniecznie.polar.io.PolarFileSystem;
import pl.niekoniecznie.polar.io.PolarService;
import pl.niekoniecznie.polar.stream.PolarStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Polar2Endomondo {

    private final static int POLAR_VENDOR_ID = 0x0da4;
    private final static int POLAR_PRODUCT_ID = 0x0008;

    private final static Logger logger = LogManager.getLogger(Polar2Endomondo.class);

    public static void main(String[] args) throws IOException {
        ClassPathLibraryLoader.loadNativeHIDLibrary();

        HIDDevice hid = HIDManager.getInstance().openById(POLAR_VENDOR_ID, POLAR_PRODUCT_ID, null);
        logger.trace("Device " + hid.getProductString() + " " + hid.getSerialNumberString() + " found");

        PolarService service = new PolarService(hid);
        PolarFileSystem filesystem = new PolarFileSystem(service);

        Path backupDirectory = Paths.get(System.getProperty("user.home"), ".polar/backup/", hid.getSerialNumberString());
        logger.trace("Backup directory set to " + backupDirectory);

        if (!Files.exists(backupDirectory)) {
            Files.createDirectories(backupDirectory);
        }

        long downloaded = PolarStream
            .stream(filesystem)
            .filter(new DirectoryFilter(backupDirectory))
            .filter(new FileFilter(backupDirectory))
            .peek(new DirectoryDownloader(backupDirectory))
            .count();

        System.out.println("Downloaded " + downloaded + " files and directories.");

        hid.close();
        System.exit(0);
    }
}
