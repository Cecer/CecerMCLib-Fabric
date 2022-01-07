package com.cecer1.projects.mc.cecermclib.fabric.modules.input.mouse;

public class MouseRegionHandler {

    private int minX;
    private int maxX;

    private int minY;
    private int maxY;

    public int getMinX() {
        return this.minX;
    }
    protected void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMaxX() {
        return this.maxX;
    }
    protected void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMinY() {
        return this.minY;
    }
    protected void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMaxY() {
        return this.maxY;
    }
    protected void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public MouseRegionHandler() {}
    public MouseRegionHandler(int minX, int minY, int width, int height) {
        this.setRegion(minX, minY, width, height);
    }
    public void setRegion(int minX, int minY, int width, int height) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = minX + width;
        this.maxY = minY + height;
    }

    protected boolean onMouseUp(int button) {
        return true;
    }
    protected boolean onMouseDown(int button) {
        return true;
    }

    protected void onMouseMove(int lastMouseX, int lastMouseY, int trueMouseX, int trueMouseY) { }

    public boolean onScroll(double horizontalAmount, double verticalAmount) {
        return true;
    }

    protected void onMouseEnter(int mouseTrueX, int mouseTrueY) { }
    protected void onMouseExit(int mouseTrueX, int mouseTrueY) { }

    protected boolean onClick(int button) {
        return true;
    }
}
