package com.fauxpas.applications;

import com.fauxpas.geometry.Face;
import com.fauxpas.geometry.Graph;
import com.fauxpas.geometry.HalfEdge;
import com.fauxpas.geometry.Vertex;
import com.fauxpas.io.GraphFile;
import com.fauxpas.io.GraphRenderer;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ViewerSample extends Application {

    GraphRenderer graphRenderer;
    Button load;
    double padding;
    double width;
    double height;
    private Graph graph;
    private TextField saveName;
    private AnimationTimer drawTimer;
    private Vertex currentVert;
    private Button nextVert;
    private boolean incomingToggle;
    private Button inOut;
    private Button clearVert;
    private Face currentFace;
    private Button nextFace;
    private Button clearFace;

    public ViewerSample() {
        this.padding = 100;
        this.width = 1024;
        this.height = 768;
        this.incomingToggle = false;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("View Voronoi Test");

        Group root = new Group();
        Canvas canvas = new Canvas(new Double(this.width).intValue(), new Double(this.height).intValue());
        GraphicsContext gc = canvas.getGraphicsContext2D();

        this.graphRenderer = new GraphRenderer(gc, width, height);

        saveName = new TextField();
        saveName.setPrefWidth(200);
        saveName.setLayoutX((width - (padding/2)) - 200 );
        saveName.setLayoutY(height - padding/2);
        saveName.setPromptText("voronoi");

        load = new Button("load");
        load.setLayoutX(width- padding/2);
        load.setLayoutY(height - padding/2);
        load.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                loadGraph();
            }
        });

        currentVert = null;
        nextVert = new Button("Vert->");
        nextVert.setLayoutY(height - (padding/2));
        nextVert.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (graph != null) {
                    toggleVertFocus(currentVert, false);
                    currentVert = getNthVert(ThreadLocalRandom.current().nextInt(0, graph.getVertices().size()));
                    toggleVertFocus(currentVert, true);
                }
            }
        });

        inOut = new Button("I/O");
        inOut.setLayoutX((padding/2)+20);
        inOut.setLayoutY(height - (padding/2));
        inOut.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                toggleVertFocus(currentVert, false);
                incomingToggle = !incomingToggle;
                toggleVertFocus(currentVert, true);
            }
        });

        clearVert = new Button("clear");
        clearVert.setLayoutX(padding+20);
        clearVert.setLayoutY(height - (padding/2));
        clearVert.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                toggleVertFocus(currentVert, false);
                currentVert = null;
            }
        });

        currentFace = null;
        nextFace = new Button("Face->");
        nextFace.setLayoutY(height - (padding/2)+30);
        nextFace.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (graph != null) {
                    toggleFaceFocus(currentFace, false);
                    currentFace = getNthFace(ThreadLocalRandom.current().nextInt(0, graph.getFaces().size()));
                    toggleFaceFocus(currentFace, true);
                }
            }
        });

        clearFace = new Button("clear");
        clearFace.setLayoutX(padding-25);
        clearFace.setLayoutY(height - (padding/2)+30);
        clearFace.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                toggleFaceFocus(currentFace, false);
                currentFace = null;
            }
        });

        root.getChildren().add(canvas);
        root.getChildren().add(load);
        root.getChildren().add(saveName);
        root.getChildren().add(nextVert);
        root.getChildren().add(inOut);
        root.getChildren().add(clearVert);
        root.getChildren().add(nextFace);
        root.getChildren().add(clearFace);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    private Vertex getNthVert(int n) {
        if (graph == null) {
            return null;
        }

        return graph.getVertex(n);
    }

    private void toggleVertFocus(Vertex v, boolean focus) {
        if (graph == null) {
            return;
        }

        if (v != null) {
            v.setFocused(focus);
            if (incomingToggle) {
                for (HalfEdge h: graph.incomingHalfEdges(v)) {
                    h.setFocused(focus);
                }
            }
            else {
                for (HalfEdge h : graph.outgoingHalfEdges(v)) {
                    h.setFocused(focus);
                }
            }
        }
    }

    private Face getNthFace(int n)  {
        if (graph == null) {
            return null;
        }

        return graph.getFace(n);
    }

    private void toggleFaceFocus(Face f, boolean focus) {
        if (graph == null) {
            return;
        }

        if (f != null) {
            for (HalfEdge e: f.InnerComponents()) {
                e.setFocused(focus);
            }
        }
    }

    private void stopDraw() {
        if (drawTimer != null) {
            drawTimer.stop();
            drawTimer = null;
        }
    }

    private void draw() {
        drawTimer = this.graphRenderer.getAnimation( this.graph );
        drawTimer.start();
    }

    private void loadGraph() {
        stopDraw();

        GraphFile gf;
        if (saveName.getText().isEmpty()) {
            gf = new GraphFile(System.getProperty("user.home")+"/"+"VoronoiGraphs", "voronoi");
        }
        else {
            gf = new GraphFile(System.getProperty("user.home")+"/"+"VoronoiGraphs", saveName.getText());
        }
        this.graph = gf.read();

        draw();
    }
}
