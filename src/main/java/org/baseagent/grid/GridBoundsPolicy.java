package org.baseagent.grid;

public interface GridBoundsPolicy {
    int boundX(int x);
    
    int boundY(int y);
    
    double boundX(double x);
    
    double boundY(double y);
}
