package org.baseagent.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

import org.baseagent.grid.GridPosition;
import org.baseagent.grid.HasGridPosition;
import org.baseagent.util.BaseAgentMath;

public class GridNetworkMath<T, R> {
    
    public static<T> Optional<HasGridPosition> getPossibleGridPositionForNode(Node<T> node) {
        HasGridPosition pos = null;
        if (node.getObject() instanceof HasGridPosition) {
            pos = (HasGridPosition)node.getObject();
        }
        
        else if (node instanceof HasGridPosition) {
            pos = (HasGridPosition)node;
        }
        
        else if (node.getPayload().containsKey("X") && (node.getPayload().containsKey("Y"))) {
            pos = new GridPosition((int)node.getPayload().get("X"), (int)node.getPayload().get("Y"));
        }
        
        if (pos != null) return Optional.of(pos);
        return Optional.empty();
    }
    
    
    /**
     * Finds the node with the nearest GridPosition to a given GridPosition
     * @param targetPosition The target GridPosition to find the nearest node for
     * @return The Node with the closest GridPosition, or null if no nodes exist
     */
    public static<T, R> Node<T> findNearestNodeToPosition(Network<T, R> network, GridPosition targetPosition) {
        if (network.getNodes().size() == 0 || targetPosition == null) {
            return null;
        }
        
        Node<T> nearestNode = null;
        double minDistance = Double.POSITIVE_INFINITY;
        
        for (Node<T> node : network.getNodes()) {
            Optional<HasGridPosition> posop = getPossibleGridPositionForNode(node);
            if (posop.isPresent()) {
            
                double distance = BaseAgentMath.distance(targetPosition, posop.get());
            
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestNode = node;
                }
            }
        }
        
        return nearestNode;
    }
}
