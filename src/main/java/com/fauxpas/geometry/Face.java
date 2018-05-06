package com.fauxpas.geometry;

import java.util.LinkedHashSet;

public class Face {

    private Point site;
    private LinkedHashSet<HalfEdge> innerComponents;
    private HalfEdge outerComponent;
    private boolean focused;

    public LinkedHashSet<HalfEdge> InnerComponents() {
        return innerComponents;
    }

    public void addInnerComponents(HalfEdge e) {
        this.innerComponents.add(e);
    }

    public HalfEdge OuterComponent() {
        return outerComponent;
    }

    public void setOuterComponent(HalfEdge outerComponent) {
        this.outerComponent = outerComponent;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public Point getSite() {
        return site;
    }

    public void setSite(Point site) {
        this.site = site;
    }

    public Face() {
        this.innerComponents = new LinkedHashSet<>();
        focused = false;
    }

    public Face(Point p) {
        this();
        this.setSite(p);
    }

}
