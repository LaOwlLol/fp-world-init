package com.fauxpas.applications;

import java.util.List;
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

		this.basicGraph.addEdge(this.basicGraph.getVertices().get(0), this.basicGraph.getVertices().get(4));
		this.basicGraph.addEdge(this.basicGraph.getVertices().get(1), this.basicGraph.getVertices().get(4));
		this.basicGraph.addEdge(this.basicGraph.getVertices().get(2), this.basicGraph.getVertices().get(3));
		this.basicGraph.addEdge(this.basicGraph.getVertices().get(3), this.basicGraph.getVertices().get(0));
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
    	for (GNode _v: this.basicGraph.getVertices()) {
    		//System.out.println("point at: "+_v.location().x()+" "+_v.location().y());
    		gc.fillOval(_v.location().x()-this.pointRadius, _v.location().y()-this.pointRadius, this.pointRadius, this.pointRadius);
    	}
    }

    public void drawEdges(GraphicsContext gc) {
    	for (List<GNode> edge: this.basicGraph.getEdges()) {
    		gc.strokeLine(edge.get(0).location().x(),
    			edge.get(0).location().y(), 
    			edge.get(1).location().x(), 
    			edge.get(1).location().y());
    	}
    }

}