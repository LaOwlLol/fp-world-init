package com.fauxpas.io;

import com.fauxpas.geometry.Graph;
import com.fauxpas.geometry.HalfEdge;
import com.fauxpas.geometry.Point;
import com.fauxpas.geometry.Vertex;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GraphRenderer {
    private GraphicsContext graphicsContext;
    private double width, height;
    int siteRadius;
    int vertRadius;
    int focusedVertRadius;
    double focusedEdgeThickness;
    private double defaultLineThickness;

    public GraphRenderer(GraphicsContext _graphicsContext, double _width, double _height) {
        this.graphicsContext = _graphicsContext;
        this.defaultLineThickness = graphicsContext.getLineWidth();
        this.focusedEdgeThickness = this.defaultLineThickness + Math.max(1, this.defaultLineThickness*2);
        this.width = _width;
        this.height = _height;
        this.siteRadius = 4;
        this.vertRadius = 6;
        this.focusedVertRadius = 8;

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
        drawVertices(graph);
        drawEdges(graph);
    }

    public void drawSites(Graph graph) {
        for (Point p: graph.getSites()) {
          drawDot(p, this.siteRadius);
        }
    }

    public void drawVertices(Graph graph) {
        graphicsContext.setFill(Color.MEDIUMAQUAMARINE);
        for (Vertex v: graph.getVertices()) {
            if (v.isFocused()) {
                graphicsContext.setFill(Color.DARKBLUE);
                drawDot(v.getCoordinates(), this.focusedVertRadius);
                graphicsContext.setFill(Color.MEDIUMAQUAMARINE);
            }
            else {
                drawDot(v.getCoordinates(), this.vertRadius);
            }
        }
        graphicsContext.setFill(Color.BLACK);
    }

    public void drawEdges(Graph graph) {
        graphicsContext.setStroke(Color.CORAL);
        for (HalfEdge edge: graph.getEdges()) {
            if (edge.Origin() != null && edge.Destination() != null) {
                if (edge.isFocused()) {
                    graphicsContext.setStroke(Color.RED);
                    graphicsContext.setLineWidth(focusedEdgeThickness);
                    drawArrow(edge.Origin().getCoordinates(), edge.Destination().getCoordinates());
                    graphicsContext.setLineWidth(defaultLineThickness);
                    graphicsContext.setStroke(Color.CORAL);
                }
                else {
                    drawLine(edge.Origin().getCoordinates(), edge.Destination().getCoordinates());
                }
            }
        }
        graphicsContext.setStroke(Color.BLACK);
    }

    public void drawDot(Point p, int radius) {
        graphicsContext.fillOval(p.x()-(radius/2),
                p.y()-(radius/2),
                radius,
                radius);
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
