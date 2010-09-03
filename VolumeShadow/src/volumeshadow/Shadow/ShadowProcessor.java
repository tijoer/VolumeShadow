package volumeshadow.Shadow;

import java.util.ArrayList;
import java.util.HashMap;
import javax.media.opengl.GL;

import exampleImplementation.math.Vector3f;

/**
 * This class does all the calculations that are needed for a shadow volume 
 * based shadow. Note that the shadow volume is _closed_, so it can be used
 * either for z-pass or z-fail (or anything else).
 *
 * <h3>Contract for the model data </h3>
 *   <ul>
 *      <li>every triangle shall only be inserted once</li>
 *      <li>two vertices do not share the same position</li>
 *  </ul>
 *  <p>If two vertices share the same position the result is undefined. If
 *      the z-fail method is used the shadow will probably have some errors,
 *      if z-pass is used it can still work, but most likely won't either.</p>
 * 
 * @author Tim Jörgen
 */
public class ShadowProcessor {

    private HashMap<Edge, Edge> edgeMap;
    private Vector3f lightSource;
    private float infinity = 1000.0f;
    private ArrayList<Triangle> frontCap = new ArrayList<Triangle>();

    /**
     * Creates a new shadow processor.
     * 
     * @param lightSource the position of the light source
     * @param model the model data
     */
    public ShadowProcessor(Vector3f lightSource, ArrayList<Triangle> model) {
        this.lightSource = lightSource;
        edgeMap = new HashMap<Edge, Edge>(model.size()/3); //model.size()/4 is a 
                                                    //heuristic value based on guess
        Triangle triangle;

        // Each edge is inserted into a hash Map. When the edge is found again
        // it is removed. So only the silhouette edges will be in the hash map
        // when this algorithm is finished.
        int numOfEdges = 0;
        int removedEdges = 0;
        for (int i = 0; i < model.size(); i++) {
            triangle = model.get(i);
            triangle.calcNormal();
            if (lightSource.dotProduct(triangle.triangleNormale) > 0) {
                numOfEdges += 3;
                frontCap.add(triangle);
                if (edgeMap.containsKey(triangle.edge0)) {
                    edgeMap.remove(triangle.edge0);
                    removedEdges++;
                } else {
                    edgeMap.put(triangle.edge0, triangle.edge0);
                }

                if (edgeMap.containsKey(triangle.edge1)) {
                    edgeMap.remove(triangle.edge1);
                    removedEdges++;
                } else {
                    edgeMap.put(triangle.edge1, triangle.edge1);
                }

                if (edgeMap.containsKey(triangle.edge2)) {
                    edgeMap.remove(triangle.edge2);
                    removedEdges++;
                } else {
                    edgeMap.put(triangle.edge2, triangle.edge2);
                }
            }
        }
    }

    /**
     * Draws lines from the light Source to the silhouette of the object.
     * This is very useful for debug purposes.
     * 
     * @param gl
     */
    public void drawLightToSilhouetteLines(GL gl) {
        ArrayList<Edge> edgeList = new ArrayList<Edge>();
        edgeList.addAll(edgeMap.values());
        gl.glDisable(GL.GL_LIGHTING);
        gl.glBegin(GL.GL_LINES);
        for (int i = 0; i < edgeList.size(); i++) {
            gl.glVertex3fv(lightSource.asFloatArray(), 0);
            gl.glVertex3fv(edgeList.get(i).v0.asFloatArray(), 0);

            gl.glVertex3fv(lightSource.asFloatArray(), 0);
            gl.glVertex3fv(edgeList.get(i).v1.asFloatArray(), 0);
        }
        gl.glEnd();
        gl.glEnable(GL.GL_LIGHTING);
    }

    /**
     * Draw not the shadow volume, but a wireframe of it
     * 
     * @param gl the gl context
     */
    public void drawShadowLines(GL gl) {
        int dawingMode = GL.GL_LINES;
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glLineWidth(20.0f);
        drawShadowData(gl, dawingMode);
        gl.glLineWidth(1.0f);
    }

    /**
     * Draw the solid shadow Volume.
     * 
     * @param gl the gl context
     */
    public void drawShadowVolume(GL gl) {
        int dawingMode = GL.GL_QUADS;
        drawShadowData(gl, dawingMode);
    }

    /**
     * Draws the volume in the given drawing mode.
     * 
     * @param gl the gl context
     * @param drawingMode The OpenGl drawing Mode. Like GL_LINES or GL_QUADS...
     */
    private void drawShadowData(GL gl, int drawingMode) {
        ArrayList<Edge> edgeList = new ArrayList<Edge>();
        Vector3f v0 = new Vector3f(), v1 = new Vector3f(), v2 = new Vector3f();
        edgeList.addAll(edgeMap.values());
        gl.glDisable(GL.GL_LIGHTING);
        gl.glBegin(drawingMode);
        for (int i = 0; i < edgeList.size(); i++) {
            v0 = Vector3f.sub(edgeList.get(i).v0, lightSource);
            //v0.normalize();
            v0.scale(infinity);
            v1 = Vector3f.sub(edgeList.get(i).v1, lightSource);
            //v1.normalize();
            v1.scale(infinity);

            gl.glVertex3fv(edgeList.get(i).v1.asFloatArray(), 0);
            gl.glVertex3fv(edgeList.get(i).v0.asFloatArray(), 0);
            gl.glVertex3fv(v0.asFloatArray(), 0);
            gl.glVertex3fv(v1.asFloatArray(), 0);
        }
        Triangle tri;
        gl.glEnd();

        //as the shadow volume is currently not closed, we draw the front cap
        //the part of the model, that is faced towards the light, extrude it to
        //infinity and draw it again
        gl.glBegin(GL.GL_TRIANGLES);
        for (int i = 0; i < frontCap.size(); i++) {
            tri = frontCap.get(i);
            //draw front cap
            gl.glVertex3fv(tri.v0.asFloatArray(), 0);
            gl.glVertex3fv(tri.v1.asFloatArray(), 0);
            gl.glVertex3fv(tri.v2.asFloatArray(), 0);

            //draw bac cap (extruded front cap)
            v0 = Vector3f.sub(frontCap.get(i).v0, lightSource);
            v1 = Vector3f.sub(frontCap.get(i).v1, lightSource);
            v2 = Vector3f.sub(frontCap.get(i).v2, lightSource);
            v0.normalize();
            v1.normalize();
            v2.normalize();
            v0.scale(infinity);
            v1.scale(infinity);
            v2.scale(infinity);

            //CW not CCW, as this is the projected back side
            gl.glVertex3fv(v2.asFloatArray(), 0);
            gl.glVertex3fv(v1.asFloatArray(), 0);
            gl.glVertex3fv(v0.asFloatArray(), 0);

        }
        gl.glEnd();
        gl.glEnable(GL.GL_LIGHTING);
    }

    /**
     * Sets the size of the shadow volume. Default is 1000 Units. This should be
     * a value that is still in your view frame but otherwise very large.
     * 
     * @param infinity the value you want to use
     */
    public void setShadowVolumeLength(float infinity) {
        this.infinity = infinity;
    }
}
