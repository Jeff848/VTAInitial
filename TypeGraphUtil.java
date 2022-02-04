import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Iterator;
import java.util.*;

public class TypeGraphUtil {

    public static boolean constraintsNotFulfilled(Graph<String> g, Map<String, Set<String>> nodeToValue, Set<String> startingNodes) {
        System.out.println("In constraints not fulfilled");
        Set<String> visited = new HashSet<>();
        Stack<String> stack = new Stack<String>();
        for(String str: startingNodes) {
            stack.push(str);
        } 
       
        while(!stack.empty()) {
            String current = stack.pop();
            visited.add(current);
            if(constraintsNotPropagated(g, nodeToValue, current)) {
                return true;
            }
            if(g.getEdges(current) != null) {
                for(String neighbor : g.getEdges(current)) {
                    if(!visited.contains(neighbor)) {
                        stack.push(neighbor);
                    }
                }
            }
        }
        return false;
    }

    public static void propagateConstraints(Graph<String> g, Map<String, Set<String>> nodeToValue) {
        Set<String> nodes = g.getNodes();
        for(String node : nodes) {
            Set<String> neighbors = g.getEdges(node);
            Set<String> nodeValues = nodeToValue.get(node);
            if(neighbors==null || nodeValues==null) {
                continue;
            }
            for(String neighbor : neighbors) {
                if(nodeToValue.get(neighbor) == null)
                    nodeToValue.put(neighbor, new HashSet<String>());
                nodeToValue.get(neighbor).addAll(nodeValues);
            }
        }
    }

    public static boolean constraintsNotPropagated(Graph<String> g, Map<String, Set<String>> nodeToValue, String node) {
        Set<String> nodeValues = nodeToValue.get(node);
        Set<String> neighbors = g.getEdges(node);
        if(neighbors != null && nodeValues != null) {
            for(String nodeVal : nodeValues) {
                for(String neighbor : neighbors) {
                    if(nodeToValue.get(neighbor) == null || !nodeToValue.get(neighbor).contains(nodeVal)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}