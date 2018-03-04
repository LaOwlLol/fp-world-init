package com.fauxpas.applications;

import java.util.List;

import com.fauxpas.geometry.Point;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;


import com.fauxpas.geometry.Graph;
import com.fauxpas.geometry.GNode;

public class BasicGraph extends Application {

	Graph basicGraph;
	double width;
	double height;
	int pointRadius;

	public BasicGraph() {

		this.width = 600;
		this.height = 500;
		this.pointRadius = 5;

		this.basicGraph = new Graph();
		for (int i = 0; i < 5; ++i) {
			this.basicGraph.addVertex(new GNode(Math.random() * this.width , Math.random() * this.height) );
		}

		this.basicGraph.addHalfEdge(this.basicGraph.getVertices().get(0), this.basicGraph.getVertices().get(1));
		this.basicGraph.addHalfEdge(this.basicGraph.getVertices().get(1), this.basicGraph.getVertices().get(2));
		this.basicGraph.addHalfEdge(this.basicGraph.getVertices().get(2), this.basicGraph.getVertices().get(3));
		this.basicGraph.addHalfEdge(this.basicGraph.getVertices().get(3), this.basicGraph.getVertices().get(4));

		this.basicGraph.addHalfEdge(this.basicGraph.getVertices().get(4), this.basicGraph.getVertices().get(3));
		this.basicGraph.addHalfEdge(this.basicGraph.getVertices().get(3), this.basicGraph.getVertices().get(2));
		this.basicGraph.addHalfEdge(this.basicGraph.getVertices().get(2), this.basicGraph.getVertices().get(1));
		this.basicGraph.addHalfEdge(this.basicGraph.getVertices().get(1), this.basicGraph.getVertices().get(0));

		this.basicGraph.addHalfEdge(this.basicGraph.getVertices().get(4), this.basicGraph.getVertices().get(0));

	}

	public static void main(String[] args) {
        launch(args);
    }

	 @Override
    public void start(Stage primaryStage) {
    	primaryStage.setTitle("Graph Operations Test");
    	Group root = new Group();
        Canvas canvas = new Canvas(new Double(this.width).intValue(), new Double(this.height).intValue());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawGraph(gc);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public void drawGraph(GraphicsContext gc) {
    	drawVertices(gc);
    	drawEdges(gc);
    }

    public void drawVertices(GraphicsContext gc) {
		int i = 0;
    	for (GNode _v: this.basicGraph.getVertices()) {
    		//System.out.println("point at: "+_v.location().x()+" "+_v.location().y());
    		//gc.fillOval(_v.location().x()-(this.pointRadius/2), _v.location().y()-(this.pointRadius/2), this.pointRadius, this.pointRadius);
    		gc.fillText(Integer.toString(i), _v.location().x()-(this.pointRadius/2), _v.location().y()-(this.pointRadius/2));
    		++i;
    	}
    }

	public void drawEdges(GraphicsContext gc) {
		for (List<GNode> edge: this.basicGraph.getEdges()) {
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