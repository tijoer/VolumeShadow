package com.TimJoergen;

import com.TimJoergen.Shadow.Triangle;
import javax.media.opengl.GL;
import java.nio.FloatBuffer;
import com.sun.opengl.util.BufferUtil;
import java.util.ArrayList;

public class ArraySphere {

    private GL gl;
    private float[] verticesArray;
    private FloatBuffer vertices;
    private FloatBuffer normals;
    private FloatBuffer texcoords;
    private int valueCount;
    private int firstfree;
    
    public ArrayList<Vector3f> verticesList = new ArrayList<Vector3f>();
    public ArrayList<Triangle> trianglesList = new ArrayList<Triangle>();
    public ArrayList<Vector3f> normalsList = new ArrayList<Vector3f>();
    
    public ArraySphere(GL gl, float radius, int steps) {
        this.gl = gl;
        //our arrays needs to hold  8 * 4^steps * 3 * 3 values
        valueCount = 8 * (int) Math.pow(4, steps) * 3 * 3;
        verticesArray = new float[valueCount];
        vertices = BufferUtil.newFloatBuffer(valueCount);
        normals = BufferUtil.newFloatBuffer(valueCount);
        //there are only two texture coordinates per vertex
        texcoords = BufferUtil.newFloatBuffer(8 * (int) Math.pow(4, steps) * 3 * 2);
        

        float[] baseObj = {
            //001 100 010
            0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            //100 00-1 010
            1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 1.0f, 0.0f,
            //00-1 -100 010
            0.0f, 0.0f, -1.0f,
            -1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            //-100 001 010
            -1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f,
            //-100 0-10 001
            -1.0f, 0.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, 0.0f, 1.0f,
            //001 0-10 100
            0.0f, 0.0f, 1.0f,
            0.0f, -1.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            //100 0-10 00-1
            1.0f, 0.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, 0.0f, -1.0f,
            //00-1 0-10 -100
            0.0f, 0.0f, -1.0f,
            0.0f, -1.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
        };
        //copy the base object
        for (int i = 0; i < 72; i++) {
            verticesArray[i] = baseObj[i];
        }
        firstfree = 72;
        //subdivide
        for (int i = 0; i < steps; i++) {
            subdivide();
            normto(1.0f);
        }
        //calculate normals and texture coordinates
        calcNormals();
        calcTexCoords();
        normto(radius);
        //wrap the array into a buffer
        vertices.put(verticesArray, 0, valueCount);
        vertices.rewind();
        for(int i=0; i<verticesArray.length; i+=3) {
            Vector3f v0 = new Vector3f(verticesArray[i+0], verticesArray[i+1], verticesArray[i+2]+1);
            verticesList.add(v0);
        }
        for(int i=0; i<verticesList.size(); i++) {
            Vector3f v0 = verticesList.get(i);
            Vector3f v1;
            if(i!=verticesList.size()-1) {
                v1 = verticesList.get(i+1);
            } else {
                v1 = verticesList.get(0);
            }
            
            Vector3f normale = new Vector3f();
            normale = v0.vectorProduct(v1);
            
            normalsList.add(normale);
        }
        for(int i=0; i<verticesList.size(); i+=3) {
            Triangle tri = new Triangle(verticesList.get(i),
                    verticesList.get(i+1),
                    verticesList.get(i+2));
            this.trianglesList.add(tri);
        }
        verticesArray = null;
    }

    public void draw() {
//        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
//        gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
//        gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
//
//        gl.glVertexPointer(3, GL.GL_FLOAT, 0, vertices);
//        gl.glNormalPointer(GL.GL_FLOAT, 0, normals);
//        gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, texcoords);
//
//        //anzahl der vertices, nicht anzahl der werte....
//        gl.glDrawArrays(GL.GL_TRIANGLES, 0, valueCount / 3);
//
//        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
//        gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
//        gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
        gl.glBegin(GL.GL_TRIANGLES);
        for(int i=0; i<trianglesList.size(); i++) {
            trianglesList.get(i).calcNormal();
            gl.glNormal3fv(trianglesList.get(i).triangleNormale.asFloatArray(), 0);
            gl.glVertex3fv(trianglesList.get(i).v0.asFloatArray(), 0);
            gl.glVertex3fv(trianglesList.get(i).v1.asFloatArray(), 0);
            gl.glVertex3fv(trianglesList.get(i).v2.asFloatArray(), 0);
        }
        gl.glEnd();
    }

    public void subdivide() {
        int currentlength = firstfree;
        for (int i = 0; i < currentlength - 8; i += 9) {
            //these 9 values represent the three vertices making up one triangle
            float x1 = verticesArray[i];
            float y1 = verticesArray[i + 1];
            float z1 = verticesArray[i + 2];
            float x2 = verticesArray[i + 3];
            float y2 = verticesArray[i + 4];
            float z2 = verticesArray[i + 5];
            float x3 = verticesArray[i + 6];
            float y3 = verticesArray[i + 7];
            float z3 = verticesArray[i + 8];
            //calculate the points between every combination of two of these vertices
            float x12 = (x1 + x2) / 2.0f;
            float y12 = (y1 + y2) / 2.0f;
            float z12 = (z1 + z2) / 2.0f;
            float x23 = (x2 + x3) / 2.0f;
            float y23 = (y2 + y3) / 2.0f;
            float z23 = (z2 + z3) / 2.0f;
            float x31 = (x3 + x1) / 2.0f;
            float y31 = (y3 + y1) / 2.0f;
            float z31 = (z3 + z1) / 2.0f;
            //write the new values back into the array, thereby dividing the original triangle into 4 triangles
            //first overwrite the original triangle
            //keep i - i+2 unmodified
            verticesArray[i + 3] = x12;
            verticesArray[i + 4] = y12;
            verticesArray[i + 5] = z12;
            verticesArray[i + 6] = x31;
            verticesArray[i + 7] = y31;
            verticesArray[i + 8] = z31;
            //append the other three triangles
            verticesArray[firstfree++] = x2;
            verticesArray[firstfree++] = y2;
            verticesArray[firstfree++] = z2;
            verticesArray[firstfree++] = x23;
            verticesArray[firstfree++] = y23;
            verticesArray[firstfree++] = z23;
            verticesArray[firstfree++] = x12;
            verticesArray[firstfree++] = y12;
            verticesArray[firstfree++] = z12;

            verticesArray[firstfree++] = x3;
            verticesArray[firstfree++] = y3;
            verticesArray[firstfree++] = z3;
            verticesArray[firstfree++] = x31;
            verticesArray[firstfree++] = y31;
            verticesArray[firstfree++] = z31;
            verticesArray[firstfree++] = x23;
            verticesArray[firstfree++] = y23;
            verticesArray[firstfree++] = z23;

            verticesArray[firstfree++] = x31;
            verticesArray[firstfree++] = y31;
            verticesArray[firstfree++] = z31;
            verticesArray[firstfree++] = x12;
            verticesArray[firstfree++] = y12;
            verticesArray[firstfree++] = z12;
            verticesArray[firstfree++] = x23;
            verticesArray[firstfree++] = y23;
            verticesArray[firstfree++] = z23;
        }
    }

    public void normto(float scale) {
        for (int i = 0; i < verticesArray.length - 2; i += 3) {
            //get three consecutive values (representing one vertex)
            float x = verticesArray[i];
            float y = verticesArray[i + 1];
            float z = verticesArray[i + 2];
            //norm to 'scale'
            float length = (float) Math.sqrt((x * x) + (y * y) + (z * z));
            if (length != scale) {
                verticesArray[i] = (x / length) * scale;
                verticesArray[i + 1] = (y / length) * scale;
                verticesArray[i + 2] = (z / length) * scale;
            }
        }
    }

    public void calcNormals() {
        //we assume that the vertices are still scaled to a radius of 1
        //normto(1.0f);
        normals.put(verticesArray, 0, valueCount);
        normals.rewind();
    //normto(scale);
    }

    private void calcTexCoords() {
        for (int i = 0; i < verticesArray.length - 2; i += 3) {
            //longitude first
            texcoords.put(longitude(verticesArray[i], verticesArray[i + 2]));
            texcoords.put(latitude(verticesArray[i + 1]));
        }
        texcoords.rewind();
    }

    /** Calculates the latitude (distance from the poles/the equator).
     * 0.0f being the south pole, 0.5f the equator, 1.0f the north pole
     * @param y The y-Coordinate of the Vertex
     * @return The latitude scaled to fit into (0,1)
     */
    private float latitude(float y) {
        //return (float)Math.asin(-y);
        return (float) (Math.sin(-y / 2) + 0.5f);
    }

    /** Calculates the longitude (distance from the meridian).
     * 0.0f being the bering strait, 0.5f the meridian, 1.0f the bering strait again
     * @param x The x-Coordinate of the Vertex
     * @param z The z Coordinate of the Vertex
     * @return The longitude scaled to fit into (0,1)
     */
    private float longitude(float x, float z) {
        //return (float)(Math.atan2(z, -x));
        return (float) (Math.atan2(z, -x) / (2 * Math.PI)) + 0.5f;
    }
    
    /**
     * This permanently translocates the scene. The vertexData will be changed.
     */
    public void translocate(float x, float y, float z) {
        Triangle triangle;
        for (int i = 0; i < this.trianglesList.size(); i++) {
            triangle = this.trianglesList.get(i);
            triangle.v0.x += x;
            triangle.v0.y += y;
            triangle.v0.z += z;

            triangle.v1.x += x;
            triangle.v1.y += y;
            triangle.v1.z += z;

            triangle.v2.x += x;
            triangle.v2.y += y;
            triangle.v2.z += z;
        }
    }
}
