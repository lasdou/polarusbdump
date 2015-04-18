package pl.niekoniecznie.polar.filesystem;

import java.io.IOException;
import java.nio.file.*;

/**
 * Created by ak on 18.04.15.
 */
public class PolarDataDumper {

    private final static String USER_DIRECTORY_PREFIX = "/U/0/";
    private final static String DATE_DIRECTORY_REGEXP = "/U/0/\\d{8,}/";
    private final static String TIME_DIRECTORY_REGEXP = "/U/0/(\\d{8,})/E/(\\d{6,})/";
    private final static String SAMPLES_FILE_REGEXP = "/U/0/\\d{8,}/E/\\d{6,}/SAMPLES.GZB";

    private final Path destination;

    public PolarDataDumper(final Path destination) {
        this.destination = destination;
    }

    public void dump() throws IOException {
        dumpUserDirectory(new PolarFile(USER_DIRECTORY_PREFIX));
    }

    private void dumpUserDirectory(final PolarFile directory) throws IOException {
        for (PolarFile child : directory.listFiles()) {
            if (!child.isDirectory()) {
                continue;
            }

            String path = child.getPath();

            if (!path.matches(DATE_DIRECTORY_REGEXP)) {
                continue;
            }

            dumpDateDirectory(child);
        }
    }

    private void dumpDateDirectory(final PolarFile directory) throws IOException {
        PolarFile eDirectory = new PolarFile(directory.getPath() + "E/");

        for (PolarFile child : eDirectory.listFiles()) {
            if (!child.isDirectory()) {
                continue;
            }

            String path = child.getPath();

            if (!path.matches(TIME_DIRECTORY_REGEXP)) {
                continue;
            }

            dumpTimeDirectory(child);
        }
    }

    private void dumpTimeDirectory(final PolarFile directory) throws IOException {
        boolean dump = false;

        for (PolarFile child : directory.listFiles()) {
            String path = child.getPath();

            if (path.matches(SAMPLES_FILE_REGEXP)) {
                dump = true;
                break;
            }
        }

        if (!dump) {
            return;
        }

        String input = directory.getPath();
        String output = destination.toString();
        output += input.replaceFirst(TIME_DIRECTORY_REGEXP, "/$1$2/");

        try {
            Files.createDirectory(Paths.get(output));
        } catch (FileAlreadyExistsException e) {

        }

        for (PolarFile child : directory.listFiles()) {
            if (child.isDirectory()) {
                continue;
            }

            String tmp = output + child.getPath().replaceFirst(TIME_DIRECTORY_REGEXP, "");
            System.out.println(tmp);

            PolarFileInputStream is = new PolarFileInputStream(child);
            Files.copy(is, Paths.get(tmp), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
