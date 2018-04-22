package com.fauxpas.geometry.topology;

import java.util.ArrayList;

public class Face {

    private ArrayList<HalfEdge> innerComponents;
    private HalfEdge outerComponent;

    public ArrayList<HalfEdge> InnerComponents() {
        return innerComponents;
    }

    public void setInnerComponents(ArrayList<HalfEdge> innerComponents) {
        this.innerComponents = innerComponents;
    }

    public HalfEdge OuterComponent() {
        return outerComponent;
    }

    public void setOuterComponent(HalfEdge outerComponent) {
        this.outerComponent = outerComponent;
    }

    public Face() {
        this.innerComponents = new ArrayList<HalfEdge>();
    }

}
