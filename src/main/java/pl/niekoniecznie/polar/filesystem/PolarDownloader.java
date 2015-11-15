package pl.niekoniecznie.polar.filesystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class PolarDownloader {

    private final PolarFileSystem filesystem;

    private final static Logger logger = LogManager.getLogger(PolarDownloader.class);

    public PolarDownloader(final PolarFileSystem filesystem) {
        this.filesystem = filesystem;
    }

    public void download(String source, Path destination) {
        download(source, destination, source);
    }

    private void download(String source, Path destination, String root) {
        logger.trace("Downloading " + source + " to " + destination);

        if (source.endsWith("/")) {
            downloadDirectory(source, destination, root);
        } else {
            downloadFile(source, destination, root);
        }
    }

    private void downloadDirectory(String source, Path destination, String root) {
        Path directory = createPath(source, destination, root);

        logger.trace("Creating directory " + directory);

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        filesystem.list(source).forEach((child) -> {
            if (child.endsWith("/")) {
                downloadDirectory(child, destination, root);
            } else {
                downloadFile(child, destination, root);
            }
        });
    }

    private void downloadFile(String source, Path destination, String root) {
        Path file = createPath(source, destination, root);

        logger.trace("Saving file " + file);

        try {
            Files.copy(filesystem.get(source), file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path createPath(String source, Path destination, String root) {
        return Paths.get(destination.toString(), source.replaceFirst(root, "/"));
    }
}