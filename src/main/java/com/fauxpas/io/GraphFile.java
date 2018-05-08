package com.fauxpas.io;

import com.fauxpas.geometry.*;

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
    private static final String COORD_DELIM = ",";
    private static final String TOKEN_DELIM = ">";
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
            String[] verts = line.split(TOKEN_DELIM);
            String[] p1 = verts[0].split(COORD_DELIM);
            String[] p2 = verts[1].split(COORD_DELIM);


            Vertex o1 = graph.getVertex(new Point(Double.parseDouble(p1[0]), Double.parseDouble(p1[1])));
            HalfEdge v = new HalfEdge(o1);

            Vertex o2 = graph.getVertex(new Point(Double.parseDouble(p2[0]), Double.parseDouble(p2[1])));
            HalfEdge w = new HalfEdge(o2);

            o1.setIncidentHalfEdge(v);
            o2.setIncidentHalfEdge(w);

            v.setTwin(w);
            w.setTwin(v);

            graph.addHalfEdge(v);
            graph.addHalfEdge(w);
            graph.addVertex(o1);
            graph.addVertex(o2);

            if (verts.length > 2) {
                String[] p3 = verts[2].split(COORD_DELIM);
                Point s1 = new Point(Double.parseDouble(p3[0]), Double.parseDouble(p3[1]));
                Face f1 = graph.getFace(s1);
                graph.addFace(f1);
                v.setIncidentFace(f1);
                f1.addInnerComponents(v);

                if (verts.length > 3) {
                    String[] p4 = verts[3].split(COORD_DELIM);
                    Point s2 = new Point(Double.parseDouble(p4[0]), Double.parseDouble(p4[1]));
                    Face f2 = graph.getFace(s2);
                    graph.addFace(f2);
                    w.setIncidentFace(f2);
                    f2.addInnerComponents(w);
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return FAILED_READ_PROC;
        }

        return SUCCESS_READ_PROC;
    }

    private int processSite(String line, Graph graph) {
        try {
            String[] coords = line.split(COORD_DELIM);
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
            data.append(h.Origin().getCoordinates().x()).append(COORD_DELIM).append(h.Origin().getCoordinates().y());
            data.append(TOKEN_DELIM);
            data.append(h.Destination().getCoordinates().x()).append(COORD_DELIM).append(h.Destination().getCoordinates().y());
            if (h.hasIncidentFace()) {
                data.append(TOKEN_DELIM);
                data.append(h.IncidentFace().getSite().x()).append(COORD_DELIM).append(h.IncidentFace().getSite().y());
                if (h.hasTwin()) {
                    if (h.Twin().hasIncidentFace()) {
                        data.append(TOKEN_DELIM);
                        data.append(h.Twin().IncidentFace().getSite().x()).append(COORD_DELIM).append(h.Twin().IncidentFace().getSite().y());
                    }
                }
            }
            data.append(NEW_LINE);
        }
        return data.toString();
    }

    private String graphSitesToString(Graph graph) {
        StringBuilder data = new StringBuilder();
        for (Point s: graph.getSites()) {
            data.append(s.x()).append(COORD_DELIM).append(s.y()).append(NEW_LINE);
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
