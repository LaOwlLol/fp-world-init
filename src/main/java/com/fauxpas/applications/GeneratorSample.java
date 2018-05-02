package com.fauxpas.applications;

import com.fauxpas.fortunes.ajwerner.Voronoi;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GeneratorSample extends Application {

    Voronoi voronoi;
    GraphRenderer graphRenderer;
    Button save;
    TextField saveName;
    double padding;
    double width;
    double height;
    private Button regen;
    private AnimationTimer processor;
    private AnimationTimer animation;

    public GeneratorSample() {
        this.padding = 100;
        this.width = 1024;
        this.height = 768;
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Generate Voronoi Test");

        Group root = new Group();
        Canvas canvas = new Canvas(new Double(this.width).intValue(), new Double(this.height).intValue());
        GraphicsContext gc = canvas.getGraphicsContext2D();

        this.graphRenderer = new GraphRenderer(gc, width, height);

        save = new Button("save");
        save.setLayoutX(width- (padding/2));
        save.setLayoutY(height - (padding/2));
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saveGraph();
            }
        });

        saveName = new TextField();
        saveName.setPrefWidth(200);
        saveName.setLayoutX((width - (padding/2)) - 200 );
        saveName.setLayoutY(height - padding/2);
        saveName.setPromptText("voronoi");

        regen = new Button("regenerate");
        regen.setLayoutY(height - (padding/2));
        regen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                voronoi();
            }
        });

        root.getChildren().add(canvas);
        root.getChildren().add(save);
        root.getChildren().add(saveName);
        root.getChildren().add(regen);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        voronoi();
    }

    private void voronoi() {
        stopVoronoi();

        voronoi = new Voronoi(width, height, padding);
        voronoi.generateSites(400);
        voronoi.initEvents();
        voronoi.initProcessing();

        processor = voronoi.getProcessAnimator();
        processor.start();

        animation = graphRenderer.getAnimation(voronoi.getGraph());
        animation.start();
    }

    private void stopVoronoi() {
        if (animation != null) {
            animation.stop();
            animation = null;
        }
        if (processor != null) {
            processor.stop();
            processor = null;
        }
    }

    private void saveGraph() {
        GraphFile gf;
        if (saveName.getText().isEmpty()) {
            gf = new GraphFile(System.getProperty("user.home")+"/"+"VoronoiGraphs", "voronoi");
        }
        else {
            gf = new GraphFile(System.getProperty("user.home")+"/"+"VoronoiGraphs", saveName.getText());
        }
        gf.write(this.voronoi.getGraph());
    }

    public void drawSweepLine(GraphicsContext gc) {
        gc.setStroke(Color.RED);
        gc.strokeLine(0, this.voronoi.getSweepLoc(), this.width,  this.voronoi.getSweepLoc());
        gc.setStroke(Color.BLACK);
    }

}
