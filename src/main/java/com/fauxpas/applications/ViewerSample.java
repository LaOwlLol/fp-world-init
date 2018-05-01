package com.fauxpas.applications;

import com.fauxpas.geometry.Graph;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ViewerSample extends Application {

    GraphRenderer graphRenderer;
    Button load;
    double padding;
    double width;
    double height;
    private Graph graph;

    public ViewerSample() {

        this.padding = 100;
        this.width = 1024;
        this.height = 768;

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("View Voronoi Test");

        Group root = new Group();
        Canvas canvas = new Canvas(new Double(this.width).intValue(), new Double(this.height).intValue());
        GraphicsContext gc = canvas.getGraphicsContext2D();

        this.graphRenderer = new GraphRenderer(gc, width, height);

        load = new Button("load");
        load.setLayoutX(width- padding/2);
        load.setLayoutY(height - padding/2);

        load.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                loadGraph();
                load.setDisable(true);
            }
        });

        root.getChildren().add(canvas);
        root.getChildren().add(load);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    private void loadGraph() {
        GraphFile gf = new GraphFile(System.getProperty("user.home")+"/"+"VoronoiGraphs", "newVoronoi");
        this.graph = gf.read();
        AnimationTimer drawTimer = this.graphRenderer.getAnimation( this.graph );

        drawTimer.start();
    }
}
