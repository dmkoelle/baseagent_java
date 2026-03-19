package org.baseagent.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.baseagent.grid.GridPosition;
import org.baseagent.grid.HasGridPosition;
import org.baseagent.util.BaseAgentMath;

public class GridNetworkMath<T, R> {

    public static <T> Optional<HasGridPosition> getPossibleGridPositionForNode(Node<T> node) {
        HasGridPosition pos = null;
        if (node.getObject() instanceof HasGridPosition) {
            pos = (HasGridPosition) node.getObject();
        }

        else if (node instanceof HasGridPosition) {
            pos = (HasGridPosition) node;
        }

        else if (node.getPayload().containsKey("X") && (node.getPayload().containsKey("Y"))) {
            pos = new GridPosition((int) node.getPayload().get("X"), (int) node.getPayload().get("Y"));
        }

        if (pos != null)
            return Optional.of(pos);
        return Optional.empty();
    }

    /**
     * Gets the weight of an edge between two nodes
     */
    private static <T> double getEdgeWeight(Node<T> fromNode, Node<T> toNode) {
        Optional<HasGridPosition> posop1 = GridNetworkMath.getPossibleGridPositionForNode(fromNode);
        Optional<HasGridPosition> posop2 = GridNetworkMath.getPossibleGridPositionForNode(toNode);
        if (posop1.isPresent() && posop2.isPresent()) {
            double distance = BaseAgentMath.distance(posop1.get(), posop2.get());
            // TODO: Insert more complex ways to incorporate Edge properties in this
            // calculation
            return distance;
        }
        return 1.0;
    }

    /**
     * Finds the node with the nearest GridPosition to a given GridPosition
     * 
     * @param targetPosition The target GridPosition to find the nearest node for
     * @return The Node with the closest GridPosition, or null if no nodes exist
     */
    public static <T, R> Node<T> findNearestNodeToPosition(Network<T, R> network, GridPosition targetPosition) {
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

    public static <T> List<HasGridPosition> flattenToGridPositions(List<Node<T>> nodesThatHaveGridPositions) {
        List<HasGridPosition> retVal = new ArrayList<>();
        for (Node<T> node : nodesThatHaveGridPositions) {
            Optional<HasGridPosition> posop = GridNetworkMath.getPossibleGridPositionForNode(node);
            if (posop.isPresent())
                retVal.add(posop.get());
        }
        return retVal;
    }

    public static <T> double calculateDistanceAlongPath(List<Node<T>> nodes) {
        double distance = 0.0;
        for (int i = 0; i < nodes.size() - 1; i++) {
            distance += getEdgeWeight(nodes.get(i), nodes.get(i + 1));
        }
        return distance;
    }
}
