package com.fauxpas.io;

import com.fauxpas.geometry.Graph;
import com.fauxpas.geometry.HalfEdge;
import com.fauxpas.geometry.Point;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class GraphFile {

    private static final String SITE_KEY = "SITES";
    private Path filePath;
    private boolean overWrite;

    public GraphFile(Path filePath) {
        this.filePath = filePath;
    }

    public GraphFile(String filePath, boolean overWrite) {
        this(Paths.get(filePath));
        this.overWrite = overWrite;
    }

    public GraphFile(String filePath) {
        this(filePath, false);
    }

    public GraphFile(String dir, String file, boolean overWrite) {
        this(new StringBuilder().append(dir).append("/").append(file).toString(), overWrite);
    }

    public GraphFile(String dir, String file) {
        this(dir, file, false);
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public boolean isOverWrite() {
        return overWrite;
    }

    public void setOverWrite(boolean overWrite) {
        this.overWrite = overWrite;
    }

    public boolean write(Graph graph) {

        StringBuilder data = new StringBuilder();

        for (HalfEdge h: graph.getEdges()) {
            data.append(h.Origin().getCoordinates().x()).append(",").append(h.Origin().getCoordinates().y());
            data.append(">");
            data.append(h.Destination().getCoordinates().x()).append(",").append(h.Destination().getCoordinates().y());
            data.append("\n");
        }

        data.append(SITE_KEY+"\n");
        for (Point s: graph.getSites()) {
            data.append(s.x()).append(",").append(s.y()).append("\n");
        }

        if (!Files.isDirectory(filePath.getParent())) {
            System.err.format("IOException no such directory: %s", filePath.getParent() );
            return false;
        }

        if (overWrite) {
            return write(data.toString(), StandardOpenOption.TRUNCATE_EXISTING);
        }
        else {
            int count = 1;
            while (Files.isRegularFile(filePath)) {
                incrementFilename(count);
                count++;
            }
            return write(data.toString(), StandardOpenOption.CREATE_NEW);
        }

    }

    private boolean write(String data, StandardOpenOption option) {

        Charset charset = Charset.forName("US-ASCII");

        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath,
                charset, StandardOpenOption.CREATE, option)) {
            writer.write(data, 0, data.length());
        }
        catch (IOException x) {
            System.err.format("IOException: %s%n", x);
            return false;
        }

        return true;
    }

    private void incrementFilename(int count) {
        StringBuilder sb = new StringBuilder();
        if (count > 1) {
            sb.append(this.filePath.toString().substring(0, filePath.toString().length()-1));
        }
        else {
            sb.append(this.filePath.toString());
        }
        sb.append(count);
        this.filePath = Paths.get(sb.toString());
    }


}
