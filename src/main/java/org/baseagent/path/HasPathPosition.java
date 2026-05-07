package org.baseagent.path;

public interface HasPathPosition {
    Segment getCurrentSegment();

    double getHeading();

    void setNextSegment(Segment segment);

    Segment getNextSegment();

    Segment deterimeNextSegment(Intersection intersection);
}
