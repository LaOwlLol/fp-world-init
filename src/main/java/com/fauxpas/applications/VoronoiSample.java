package com.fauxpas.applications;

import com.fauxpas.fortunes.ajwerner.Voronoi;
import com.fauxpas.fortunes.ajwerner.VoronoiEdge;
import com.fauxpas.geometry.HalfEdge;
import com.fauxpas.geometry.Point;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class VoronoiSample extends Application {

    private final double padding;
    Voronoi voronoi;
    GraphRenderer graphRenderer;
    ArrayList<Point> sites;
    double width;
    double height;
    Button save;


    public VoronoiSample() {

        this.width = 1024;
        this.height = 768;
        this.padding = 100;
        this.sites = new ArrayList<>();


        double low = padding;
        double x_max = width - padding;
        double y_max = height - padding;

        for (int i = 0; i < 400; i++) {
            this.sites.add(new Point(ThreadLocalRandom.current().nextDouble(low, x_max),
                    ThreadLocalRandom.current().nextDouble(low, y_max)));
            //sites.add(new Point(rnd.nextDouble(), rnd.nextDouble()));
        }

        this.voronoi = new Voronoi();

    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Graph Voronoi Test");

        Group root = new Group();
        Canvas canvas = new Canvas(new Double(this.width).intValue(), new Double(this.height).intValue());
        GraphicsContext gc = canvas.getGraphicsContext2D();

        this.graphRenderer = new GraphRenderer(gc, width, height);

        save = new Button("save");
        save.setLayoutX(width- padding/2);
        save.setLayoutY(height - padding/2);
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saveGraph();
            }
        });

        root.getChildren().add(canvas);
        root.getChildren().add(save);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        voronoi.addEvents(sites);
        voronoi.init();

        AnimationTimer processor = voronoi.getProcessAnimator();
        processor.start();

        AnimationTimer animation = graphRenderer.getAnimation(voronoi.getGraph());
        animation.start();

    }

    private void saveGraph() {

        GraphFile gf = new GraphFile(System.getProperty("user.home")+"/"+"VoronoiGraphs", "newVoronoi");
        gf.write(this.voronoi.getGraph());

    }

    public void drawSweepLine(GraphicsContext gc) {
        gc.setStroke(Color.RED);
        gc.strokeLine(0, this.voronoi.getSweepLoc(), this.width,  this.voronoi.getSweepLoc());
        gc.setStroke(Color.BLACK);
    }

}
