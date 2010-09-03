/*
 * This is intellectual property. You are not allowed 
 * to use it in any way, except you have a written 
 * allowance by the owner.
 */
package volumeshadow.Shadow;

import exampleImplementation.math.Vector3f;


/**
 * @author Tim J�rgen
 */
public class Triangle {

    public Vector3f v0, v1, v2;
    public Vector3f n0, n1, n2;
    protected Edge edge0, edge1, edge2;
    public Vector3f triangleNormale = new Vector3f(); 
    
    /**
     * Creates a new triangle.
     * default for this.triangleNormale: (0, 1, 0)
     * 
     * @param v0
     * @param v1
     * @param v2
     */
    public Triangle(Vector3f v0, Vector3f v1, Vector3f v2) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        
        this.edge0 = new Edge(v0, v1);
        this.edge1 = new Edge(v1, v2);
        this.edge2 = new Edge(v2, v0);
        
        this.triangleNormale = new Vector3f(0.0f, 1.0f, 0.0f);
    }

    /**
     * Creates a new triangle. Beware: most values are not initialised.
     */
    public Triangle() {
    }
    
    /**
     * Calculates the normalized normal of this triangle. The triangleNormale will be 
     * saved in this.noramle, but also returned.
     */
    public void calcNormal() {
        Vector3f c0 = new Vector3f();
        Vector3f c1 = new Vector3f();
        Vector3f ret = new Vector3f();

        // Calculate The Vector From Point 1 To Point 0
        c0.x = v0.x - v1.x;  // Vector 1.x=Vertex[0].x-Vertex[1].x
        c0.y = v0.y - v1.y;  // Vector 1.y=Vertex[0].y-Vertex[1].y
        c0.z = v0.z - v1.z;  // Vector 1.z=Vertex[0].y-Vertex[1].z
        
        // Calculate The Vector From Point 2 To Point 1
        c1.x = v1.x - v2.x;  // Vector 2.x=Vertex[0].x-Vertex[1].x
        c1.y = v1.y - v2.y;  // Vector 2.y=Vertex[0].y-Vertex[1].y
        c1.z = v1.z - v2.z;  // Vector 2.z=Vertex[0].z-Vertex[1].z
        // Compute The Cross Product To Give Us A Surface Normal

        triangleNormale.x = c0.y * c1.z - c0.z * c1.y;  // Cross Product For Y - Z
        triangleNormale.y = c0.z * c1.x - c0.x * c1.z;  // Cross Product For X - Z
        triangleNormale.z = c0.x * c1.y - c0.y * c1.x;  // Cross Product For X - Y

        triangleNormale.y = -triangleNormale.y;
        triangleNormale.normalize();
        
        //return triangleNormale;
    }
}
