/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package volumeshadow;

import java.util.ArrayList;
import javax.media.opengl.GL;
import volumeshadow.Shadow.Triangle;

/**
 *
 * @author Tim J�rgen
 */
public class Cube {
    
    ArrayList<Triangle> triangleList = new ArrayList<Triangle>();

    public Cube(float size) {
        Vector3f v0 = new Vector3f(-size+0.01f, -size+0.09f, -size + 300);
        Vector3f v1 = new Vector3f(size+0.02f, -size+0.10f, -size + 300);
        Vector3f v2 = new Vector3f(size+0.03f, size+0.11f, -size + 300);
        Vector3f v3 = new Vector3f(-size+0.04f, size+0.12f, -size + 300);
        Vector3f v4 = new Vector3f(-size+0.05f, -size+0.012f, size + 300);
        Vector3f v5 = new Vector3f(size+0.06f, -size+0.013f, size + 300);
        Vector3f v6 = new Vector3f(size+0.07f, size+0.014f, size + 300);
        Vector3f v7 = new Vector3f(-size+0.08f, size+0.015f, size + 300);
        
        //vorne
        Triangle triangle0 = new Triangle(v2, v1, v0);
        Triangle triangle1 = new Triangle(v0, v3, v2);
        
        //rechts
        Triangle triangle2 = new Triangle(v5, v1, v2);
        Triangle triangle3 = new Triangle(v2, v6, v5);
        
        //hinten
        Triangle triangle4 = new Triangle(v4, v5, v6);
        Triangle triangle5 = new Triangle(v4, v6, v7);
        
        //links
        Triangle triangle6 = new Triangle(v0, v4, v7);
        Triangle triangle7 = new Triangle(v7, v3, v0);
        
        //unten
        Triangle triangle8 = new Triangle(v4, v0, v1);
        Triangle triangle9 = new Triangle(v1, v5, v4);
        
        //oben
        Triangle triangle10 = new Triangle(v2, v3, v7);
        Triangle triangle11 = new Triangle(v7, v6, v2);
        
        this.triangleList.add(triangle0);
        this.triangleList.add(triangle1);
        this.triangleList.add(triangle2);
        this.triangleList.add(triangle3);
        this.triangleList.add(triangle4);
        this.triangleList.add(triangle5);
        this.triangleList.add(triangle6);
        this.triangleList.add(triangle7);
        this.triangleList.add(triangle8);
        this.triangleList.add(triangle9);
        this.triangleList.add(triangle10);
        this.triangleList.add(triangle11);
    }

    public void draw(GL gl) {
        gl.glCullFace(GL.GL_BACK);
        gl.glFrontFace(GL.GL_CCW);
        Triangle tri;
        gl.glBegin(GL.GL_TRIANGLES);
        for (int i = 0; i < triangleList.size(); i++) {
            tri = triangleList.get(i);
            gl.glVertex3fv(tri.v0.asFloatArray(), 0);
            gl.glVertex3fv(tri.v1.asFloatArray(), 0);
            gl.glVertex3fv(tri.v2.asFloatArray(), 0);
        }
        gl.glEnd();
    }
}
