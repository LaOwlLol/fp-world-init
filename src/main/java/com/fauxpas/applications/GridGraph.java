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

public class GridGraph extends Application {

	Graph basicGraph;
	double width;
	double height;
    double cellWidth;
    double cellHeight;
    int horzCellCount;
    int vertCellCount;
	int pointRadius;

	public GridGraph() {

		this.width = 600;
		this.height = 500;
		this.pointRadius = 5;
        this.horzCellCount = 30;
        this.vertCellCount = 25;
        this.cellHeight = this.height/this.vertCellCount;
        this.cellWidth = this.width/this.horzCellCount;

		this.basicGraph = new Graph();
		
        for (int y = 0; y <= this.vertCellCount; ++y) {
            for (int x = 0; x <= this.horzCellCount; ++x) {
			     this.basicGraph.addVertex(new GNode( (x * this.cellWidth)+5, (y * this.cellWidth)+5 ) );
            }
		}

        List<GNode> verticies = this.basicGraph.getVertices();

        for (int i = 1; i <= this.horzCellCount; ++i) {
            this.basicGraph.addEdge(verticies.get(i-1), verticies.get(i));
        }

        for (int i = this.horzCellCount+1; i < (this.horzCellCount+1)*(this.vertCellCount+1); ++i) {
            if ( i % (this.horzCellCount+1) != 0) {
                this.basicGraph.addEdge(verticies.get(i-1), verticies.get(i));
            }
            this.basicGraph.addEdge(verticies.get(i-(this.horzCellCount+1)), verticies.get(i));
        }
	
	}

	public static void main(String[] args) {
        launch(args);
    }

	 @Override
    public void start(Stage primaryStage) {
    	primaryStage.setTitle("Graph Operations Test");
    	Group root = new Group();
        Canvas canvas = new Canvas(new Double(this.width+10).intValue(), new Double(this.height+10).intValue());
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
    		gc.fillOval(_v.location().x()-(this.pointRadius/2), _v.location().y()-(this.pointRadius/2), this.pointRadius, this.pointRadius);
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