package com.fauxpas.applications;

import com.fauxpas.fortunes.ajwerner.Voronoi;
import com.fauxpas.fortunes.ajwerner.VoronoiEdge;
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

    private final double padding;
    Voronoi voronoi;
    ArrayList<Point> sites;
    double width;
    double height;
    int pointRadius;

    public VoronoiSample() {

        this.width = 1920;
        this.height = 1080;
        this.padding = 100;
        this.pointRadius = 5;
        this.sites = new ArrayList<>();

        double low = padding;
        double x_max = width - padding;
        double y_max = height - padding;

        for (int i = 0; i < 4000; i++) {
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

        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        voronoi.addEvents(sites);
        voronoi.init();


        AnimationTimer processor = new AnimationTimer() {

            @Override
            public void handle(long now) {

                if (voronoi.hasNextEvent()) {
                    voronoi.processNextEvent();
                }
                else if (!voronoi.isFinal()) {
                    voronoi.finishBreakPoints();
                    this.stop();
                }

            }
        };
        processor.start();

        AnimationTimer draw = new AnimationTimer() {

            private long count = 0;

            @Override
            public void handle(long now) {
                if (count%100 == 0) {
                    GraphicsContext gc = canvas.getGraphicsContext2D();

                    gc.clearRect(0, 0, width, height);
                    drawGraph(gc);

                }

                count++;
            }
        };
        draw.start();


    }

    public void drawGraph(GraphicsContext gc) {
        drawVertices(gc);
        //drawEdgeList(gc);
        drawEdges(gc);
        drawSweepLine(gc);
    }

    public void drawVertices(GraphicsContext gc) {

        for (Point _v: this.sites) {
            gc.fillOval(_v.x()-(this.pointRadius/2),
                    _v.y()-(this.pointRadius/2),
                    this.pointRadius,
                    this.pointRadius);
        }
        /*gc.setFill(Color.BLUE);
        for (Point _v: this.voronoi.getVertices()) {
            gc.fillOval(_v.x()-(this.pointRadius/2),
                    _v.y()-(this.pointRadius/2),
                    this.pointRadius,
                    this.pointRadius);
        }
        gc.setFill(Color.BLACK);*/
    }

    public void drawEdgeList(GraphicsContext gc) {
        gc.setStroke(Color.DARKGREEN);
        for (VoronoiEdge edge: this.voronoi.getEdgeList()) {
            if (edge.getP1() != null && edge.getP2() != null) {
                drawLine(gc, edge.getP1(), edge.getP2());
            }
        }
        gc.setStroke(Color.BLACK);
    }

    public void drawEdges(GraphicsContext gc) {
        gc.setStroke(Color.CORNFLOWERBLUE);
        for (HalfEdge edge: this.voronoi.getEdges()) {
            if (edge.Origin() != null && edge.Destination() != null) {
                drawArrow(gc, edge.Origin().getCoordinates(), edge.Destination().getCoordinates());
            }
        }
        gc.setStroke(Color.BLACK);
    }

    public void drawLine(GraphicsContext gc, Point p1, Point p2) {
        gc.strokeLine(p1.x(), p1.y(), p2.x(),  p2.y());
    }

    public void drawSweepLine(GraphicsContext gc) {
        gc.setStroke(Color.RED);
        gc.strokeLine(0, this.voronoi.getSweepLoc(), this.width,  this.voronoi.getSweepLoc());
        gc.setStroke(Color.BLACK);
    }

    private void drawArrow(GraphicsContext gc, Point tail, Point tip)
    {
        int barb = 4;
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
