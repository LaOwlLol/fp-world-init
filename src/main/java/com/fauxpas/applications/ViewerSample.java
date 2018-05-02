package com.fauxpas.applications;

import com.fauxpas.geometry.Graph;
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

public class ViewerSample extends Application {

    GraphRenderer graphRenderer;
    Button load;
    double padding;
    double width;
    double height;
    private Graph graph;
    private TextField saveName;
    private AnimationTimer drawTimer;

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

        root.getChildren().add(canvas);
        root.getChildren().add(load);
        root.getChildren().add(saveName);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

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
