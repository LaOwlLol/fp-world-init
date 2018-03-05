package com.fauxpas.applications;

import com.fauxpas.fortunes.FortuneAlgorithm;
import com.fauxpas.geometry.GNode;
import com.fauxpas.geometry.Graph;
import com.fauxpas.geometry.Point;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

import java.util.List;

public class Voronoi extends Application {

    FortuneAlgorithm fa;
    double width;
    double height;
    int pointRadius;

    public Voronoi() {

        this.width = 600;
        this.height = 500;
        this.pointRadius = 5;

        this.fa = new FortuneAlgorithm(50, this.width, this.height);

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Graph Voronoi Test");
        Group root = new Group();
        Canvas canvas = new Canvas(new Double(this.width).intValue(), new Double(this.height).intValue());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        this.fa.printEvents();
        //drawGraph(gc);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public void drawGraph(GraphicsContext gc) {
        drawVertices(gc);
        drawEdges(gc);
    }

    public void drawVertices(GraphicsContext gc) {
        for (GNode _v: this.fa.getVertices()) {
            gc.fillOval(_v.location().x()-(this.pointRadius/2),
                    _v.location().y()-(this.pointRadius/2),
                    this.pointRadius,
                    this.pointRadius);
        }
    }

    public void drawEdges(GraphicsContext gc) {
        for (List<GNode> edge: this.fa.getEdges()) {
            drawArrow(gc,edge.get(0).location(), edge.get(1).location());
        }
    }

    private void drawArrow(GraphicsContext gc, Point tail, Point tip)
    {
        int barb = 6;
        double phi = Math.toRadians(40);
        double dy = tip.y() - tail.y();
        double dx = tip.x() - tail.x();
        double theta = Math.atan2(dy, dx);
        double x, y, rho = theta + phi;
        gc.strokeLine(tail.x(), tail.y(), tip.x(), tip.y());
        for(int j = 0; j < 2; j++)
        {
            x = tip.x() - barb * Math.cos(rho);
            y = tip.y() - barb * Math.sin(rho);
            gc.strokeLine(tip.x(), tip.y(), x, y);
            rho = theta - phi;
        }
    }

}
