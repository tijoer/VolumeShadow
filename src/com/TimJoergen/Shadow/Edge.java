package com.TimJoergen.Shadow;

import com.TimJoergen.Vector3f;

/**
 * This class represents an edge between two vertices. It has a hash code and
 * the generall contract of this class is;
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
    
    @Override
    public int hashCode() {
        return (int)(v0.x + 100*v0.y + 1000*v0.z + v1.x + 100*v1.y + 1000*v1.z);
    }

    @Override
    public boolean equals(Object obj) {
        Edge edge = (Edge) obj;
        
        if(     edge.v0.x == this.v0.x &&
                edge.v0.y == this.v0.y &&
                edge.v0.z == this.v0.z &&
                edge.v1.x == this.v1.x &&
                edge.v1.y == this.v1.y &&
                edge.v1.z == this.v1.z)
            return true;
        
        if(     edge.v0.x == this.v1.x &&
                edge.v0.y == this.v1.y &&
                edge.v0.z == this.v1.z &&
                edge.v1.x == this.v0.x &&
                edge.v1.y == this.v0.y &&
                edge.v1.z == this.v0.z)
            return true;

        
        return false;
    }
}
