package org.volumeshadow.math;

/**
 * This class represents an edge between two vertices. It has a hash code and
 * the general contract of this class is;
 * edge0 == edge1 when
 * edge.v0 == edge0.v0 OR edge.v0 == edge0.v1
 * (same for edge0.v1)
 * 
 * @author Tim Jörgen
 */
public class Edge {

    public Vector3f v0,  v1;

    public Edge(Vector3f v0, Vector3f v1) {
        this.v0 = v0;
        this.v1 = v1;
    }
    
    /**
     * Returns the hashcode of this class. The hashcode ist v0 XOR v1.
     */
    @Override
    public int hashCode() {
        return v0.hashCode() ^ v1.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        Edge edge = (Edge) obj;
        
        if(v0.equals(edge.v0) && v1.equals(edge.v1)) {
        	return true;
        }
        if(v0.equals(edge.v1) && v1.equals(edge.v0)) {
        	return true;
        }
        
        return false;
    }
}