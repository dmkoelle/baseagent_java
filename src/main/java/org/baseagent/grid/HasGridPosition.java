package org.baseagent.grid;

public interface HasGridPosition<T> {
    void setCellX(int x);
    
    int getCellX();
    
    void setCellY(int y);
    
    int getCellY();
    
    GridLayer<T> getGridLayer();
    
    double getHeading();
}