package com.fauxpas.io;

import com.fauxpas.geometry.Graph;
import com.fauxpas.geometry.HalfEdge;
import com.fauxpas.geometry.Point;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GraphRenderer {
    private GraphicsContext graphicsContext;
    private double width, height;
    int pointRadius;

    public GraphRenderer(GraphicsContext _graphicsContext, double _width, double _height) {
        this.graphicsContext = _graphicsContext;
        this.width = _width;
        this.height = _height;
        this.pointRadius = 5;
    }

    public AnimationTimer getAnimation(Graph graph) {
         return new AnimationTimer() {
            private long count = 0;

            @Override
            public void handle(long now) {
                if (count%10 == 0) {
                    graphicsContext.clearRect(0, 0, width, height);
                    drawGraph(graph);
                }
                count++;
            }
        };
    }

    public void drawGraph(Graph graph) {
        drawSites(graph);
        drawEdges(graph);
    }

    public void drawSites(Graph graph) {
        for (Point _v: graph.getSites()) {
            graphicsContext.fillOval(_v.x()-(this.pointRadius/2),
                    _v.y()-(this.pointRadius/2),
                    this.pointRadius,
                    this.pointRadius);
        }
    }

    public void drawEdges(Graph graph) {
        graphicsContext.setStroke(Color.CORNFLOWERBLUE);
        for (HalfEdge edge: graph.getEdges()) {
            if (edge.Origin() != null && edge.Destination() != null) {
                drawArrow(edge.Origin().getCoordinates(), edge.Destination().getCoordinates());
            }
        }
        graphicsContext.setStroke(Color.BLACK);
    }

    public void drawLine(Point p1, Point p2) {
        graphicsContext.strokeLine(p1.x(), p1.y(), p2.x(),  p2.y());
    }

    private void drawArrow(Point tail, Point tip)
    {
        int barb = 4;
        double phi = Math.toRadians(40);
        double dy = tip.y() - tail.y();
        double dx = tip.x() - tail.x();
        double theta = Math.atan2(dy, dx);
        double x, y, rho = theta + phi;
        drawLine(tail, tip);
        for(int j = 0; j < 2; j++)
        {
            x = tip.x() - barb * Math.cos(rho);
            y = tip.y() - barb * Math.sin(rho);
            drawLine(tip, new Point(x,y));
            rho = theta - phi;
        }
    }

}
