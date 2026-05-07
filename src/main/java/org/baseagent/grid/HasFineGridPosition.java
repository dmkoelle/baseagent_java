package org.baseagent.grid;

public interface HasFineGridPosition extends HasGridPosition {
    void setFineX(double x);
    
    double getFineX();
    
    void setFineY(double y);
    
    double getFineY();
}
