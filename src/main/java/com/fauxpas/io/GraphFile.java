package com.fauxpas.io;

import com.fauxpas.geometry.Graph;
import com.fauxpas.geometry.HalfEdge;
import com.fauxpas.geometry.Point;
import com.fauxpas.geometry.Vertex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class GraphFile {

    private static final String SITES_KEY = "SITES";
    private static final String HALFEDGES_KEY = "HALFEDGES";
    private static final String NEW_LINE = "\n";
    private static final int HALFEDGES_STATE = 0;
    private static final int SITES_STATE = 1;
    private static final int FAILED_READ_PROC = -1;
    private static final int LINE_SKIP_PROC = 0;
    private static final int SUCCESS_READ_PROC = 1;

    private Charset charset;
    private Path filePath;
    private int readState;
    private boolean overWrite;

    public GraphFile(Path filePath) {
        setFilePath( filePath );
        setCharset( Charset.forName("US-ASCII") );
        setReadState(FAILED_READ_PROC);
    }

    public GraphFile(String filePath, boolean overWrite) {
        this(Paths.get(filePath));
        setOverWrite(overWrite);
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

    public boolean OverWrite() {
        return overWrite;
    }

    public void setOverWrite(boolean overWrite) {
        this.overWrite = overWrite;
    }

    private Charset getCharset() {
        return charset;
    }

    private void setCharset(Charset charset) {
        this.charset = charset;
    }

    private int getReadState() {
        return readState;
    }

    private void setReadState(int readState) {
        this.readState = readState;
    }

    public Graph read() {
        Graph graph = new Graph();

        setReadState(SUCCESS_READ_PROC);

        try (BufferedReader reader = Files.newBufferedReader(getFilePath(), getCharset())) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                //System.out.println("read:"+line);
                processLine(line, graph);
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        System.out.println("finished");
        setReadState(FAILED_READ_PROC);
        return graph;
    }

    private int processLine(String line, Graph graph) {

        if (getReadState() == FAILED_READ_PROC) {
            return FAILED_READ_PROC;
        }

        if (line.equals(HALFEDGES_KEY)) {
            setReadState(HALFEDGES_STATE);
            return SUCCESS_READ_PROC;
        }
        else if (line.equals(SITES_KEY)) {
            setReadState(SITES_STATE);
            return SUCCESS_READ_PROC;
        }

        if (getReadState() == SITES_STATE) {
            return processSite(line, graph);
        }
        else if (getReadState() == HALFEDGES_STATE) {
            return processHalfEdge(line, graph);
        }
        return LINE_SKIP_PROC;
    }

    private int processHalfEdge(String line, Graph graph) {

        try {
            String[] verts = line.split(">");
            String[] p1 = verts[0].split(",");
            String[] p2 = verts[1].split(",");

            Vertex o1 = new Vertex(new Point(Double.parseDouble(p1[0]), Double.parseDouble(p1[1])));
            HalfEdge v = new HalfEdge(o1);

            Vertex o2 = new Vertex(new Point(Double.parseDouble(p2[0]), Double.parseDouble(p2[1])));
            HalfEdge w = new HalfEdge(o2);


            o1.setIncidentHalfEdge(v);
            o2.setIncidentHalfEdge(w);

            v.setTwin(w);
            w.setTwin(v);

            graph.addHalfEdge(v);
            graph.addHalfEdge(w);
            graph.addVertex(o1);
            graph.addVertex(o2);
        }
        catch (Exception e) {
            e.printStackTrace();
            return FAILED_READ_PROC;
        }

        return SUCCESS_READ_PROC;
    }

    private int processSite(String line, Graph graph) {
        try {
            String[] coords = line.split(",");
            graph.addSite(new Point(Double.parseDouble(coords[0]), Double.parseDouble(coords[1])));
        }
        catch (Exception e) {
            e.printStackTrace();
            return FAILED_READ_PROC;
        }
        return SUCCESS_READ_PROC;
    }

    public boolean write(Graph graph) {

        StringBuilder data = new StringBuilder();

        data.append(HALFEDGES_KEY).append(NEW_LINE);
        data.append(graphEdgesToString(graph));

        data.append(SITES_KEY).append(NEW_LINE);
        data.append(graphSitesToString(graph));

        if (!checkParentDirExists()) {
            return false;
        }

        if (OverWrite()) {
            return write(data.toString(), StandardOpenOption.TRUNCATE_EXISTING);
        }
        else {
            generateAvailablePath();
            return write(data.toString(), StandardOpenOption.CREATE_NEW);
        }

    }

    private void generateAvailablePath() {
        int count = 1;
        while (Files.isRegularFile(getFilePath())) {
            incrementFilename(count);
            count++;
        }
    }

    private boolean checkParentDirExists() {
        if (!Files.isDirectory(getFilePath().getParent())) {
            System.err.format("IOException no such directory: %s", getFilePath().getParent() );
            return false;
        }
        return true;
    }

    private boolean write(String data, StandardOpenOption option) {

        try (BufferedWriter writer = Files.newBufferedWriter(
                getFilePath(),
                getCharset(), StandardOpenOption.CREATE, option)) {
            writer.write(data, 0, data.length());
        }
        catch (IOException x) {
            System.err.format("IOException: %s%n", x);
            return false;
        }

        return true;
    }

    private String graphEdgesToString(Graph graph) {
        StringBuilder data = new StringBuilder();
        for (HalfEdge h: graph.getEdges()) {
            data.append(h.Origin().getCoordinates().x()).append(",").append(h.Origin().getCoordinates().y());
            data.append(">");
            data.append(h.Destination().getCoordinates().x()).append(",").append(h.Destination().getCoordinates().y());
            data.append("\n");
        }
        return data.toString();
    }

    private String graphSitesToString(Graph graph) {
        StringBuilder data = new StringBuilder();
        for (Point s: graph.getSites()) {
            data.append(s.x()).append(",").append(s.y()).append("\n");
        }
        return data.toString();
    }

    private void incrementFilename(int count) {
        StringBuilder sb = new StringBuilder();
        if (count > 1) {
            sb.append(getFilePath().toString().substring(0, getFilePath().toString().length()-1));
        }
        else {
            sb.append(getFilePath().toString());
        }
        sb.append(count);
        setFilePath( Paths.get(sb.toString()) );
    }

}
