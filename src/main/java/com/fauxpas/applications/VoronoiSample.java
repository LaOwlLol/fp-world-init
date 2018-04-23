package com.fauxpas.applications;

import com.fauxpas.fortunes.ajwerner.Voronoi;
import com.fauxpas.geometry.HalfEdge;
import com.fauxpas.geometry.Point;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class VoronoiSample extends Application {

    Voronoi voronoi;
    ArrayList<Point> sites;
    double width;
    double height;
    int pointRadius;

    public VoronoiSample() {

        this.width = 600;
        this.height = 600;
        this.pointRadius = 5;
        this.sites = new ArrayList<>();


        int low = -10;
        int high = 10;

        for (int i = 0; i < 100; i++) {
            this.sites.add(new Point(ThreadLocalRandom.current().nextDouble(low, high),
                    ThreadLocalRandom.current().nextDouble(low, high)));
            //sites.add(new Point(rnd.nextDouble(), rnd.nextDouble()));
        }

        this.voronoi = new Voronoi();

    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Graph Voronoi Test");
        Group root = new Group();
        Canvas canvas = new Canvas(new Double(this.width).intValue(), new Double(this.height).intValue());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        //this.fa.processGraph();



        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        AnimationTimer timer = new AnimationTimer() {

            private long count = 0;

            @Override
            public void handle(long now) {
                if (count % 100 == 0){
                    GraphicsContext gc = canvas.getGraphicsContext2D();

                    gc.clearRect(0, 0, width, height);


                    drawGraph(gc);
                }
                count++;
            }
        };
        timer.start();
    }

    public void drawGraph(GraphicsContext gc) {
        drawVertices(gc);
        //drawEdges(gc);
        //drawSweepLine(gc);
    }

    public void drawVertices(GraphicsContext gc) {

        for (Point _v: this.sites) {
            gc.fillOval(_v.x()-(this.pointRadius/2),
                    _v.y()-(this.pointRadius/2),
                    this.pointRadius,
                    this.pointRadius);
        }
        gc.setFill(Color.BLUE);
        /*for (Point _v: this.voronoi.getVertices()) {
            gc.fillOval(_v.x()-(this.pointRadius/2),
                    _v.y()-(this.pointRadius/2),
                    this.pointRadius,
                    this.pointRadius);
        }*/
        gc.setFill(Color.BLACK);
    }

    public void drawEdges(GraphicsContext gc) {
        for (HalfEdge edge: this.voronoi.getEdges()) {
            drawArrow(gc, edge.Origin().getCoordinates() , edge.Destination().getCoordinates() );
        }
    }

    public void drawSweepLine(GraphicsContext gc) {
        gc.setStroke(Color.RED);
        gc.strokeLine(0, this.voronoi.getSweepLoc(), this.width,  this.voronoi.getSweepLoc());
        gc.setStroke(Color.BLACK);
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
