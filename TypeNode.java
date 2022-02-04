import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.*;

public class TypeNode {
    private String name;
    private Set<String> types;
    public TypeNode(String name) {
        this.name = name;
        this.types = new HashSet<String>();
    }
    public TypeNode(String name, String type) {
        this.name = name;
        this.types = new HashSet<String>();
        this.types.add(type);
    }
    public void addType(String type) {
        this.types.add(type);
    }
    public String getName() {
        return this.name;
    }
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (this==obj) { return true; }
        if (!(obj instanceof TypeNode)) { return false; }
        TypeNode other = (TypeNode)obj;
        return other.getName() == this.name;
    }
    @Override
    public String toString()
    {
        return this.name;
    }

}

// Java program to implement Graph
// with the help of Generics
 
class Graph<T> {
 
    // We use Hashmap to store the edges in the graph
    private Map<T, Set<T> > map = new HashMap<>();
 
    // This function adds a new vertex to the graph
    public void addVertex(T s)
    {
        if (!map.containsKey(s))
            map.put(s, new HashSet<T>());
    }
 
    // This function adds the edge
    // between source to destination
    public void addEdge(T source,
                        T destination,
                        boolean bidirectional)
    {
        if (!map.containsKey(source))
            addVertex(source);
        if (!map.containsKey(destination))
            addVertex(destination);
 
       map.get(source).add(destination);
        
        if (bidirectional == true) {
            map.get(destination).add(source);
        }
    }

    public Set<T> getEdges(T node)
    {
        if (!map.containsKey(node))
            return null;
        return map.get(node);
    }

    public Set<T> getNodes()
    {
        return map.keySet();
    }
 
    // This function gives the count of vertices
    public void getVertexCount()
    {
        System.out.println("The graph has "
                           + map.keySet().size()
                           + " vertex");
    }
 
    // This function gives the count of edges
    public void getEdgesCount(boolean bidirection)
    {
        int count = 0;
        for (T v : map.keySet()) {
            count += map.get(v).size();
        }
        if (bidirection == true) {
            count = count / 2;
        }
        System.out.println("The graph has "
                           + count
                           + " edges.");
    }
 
    // This function gives whether
    // a vertex is present or not.
    public void hasVertex(T s)
    {
        if (map.containsKey(s)) {
            System.out.println("The graph contains "
                               + s + " as a vertex.");
        }
        else {
            System.out.println("The graph does not contain "
                               + s + " as a vertex.");
        }
    }
 
    // This function gives whether an edge is present or not.
    public void hasEdge(T s, T d)
    {
        if (map.get(s).contains(d)) {
            System.out.println("The graph has an edge between "
                               + s + " and " + d + ".");
        }
        else {
            System.out.println("The graph has no edge between "
                               + s + " and " + d + ".");
        }
    }
 
    // Prints the adjancency list of each vertex.
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
 
        for (T v : map.keySet()) {
            builder.append(v.toString() + ": ");
            for (T w : map.get(v)) {
                builder.append(w.toString() + " ");
            }
            builder.append("\n");
        }
 
        return (builder.toString());
    }
}