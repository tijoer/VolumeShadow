package volumeshadow.Shadow;

import exampleImplementation.math.Edge;
import java.util.ArrayList;
import java.util.HashMap;
import javax.media.opengl.GL;

import exampleImplementation.math.Triangle;
import exampleImplementation.math.Vector3f;

/**
 * This class does all the calculations that are needed for a shadow volume
 * based shadow. Note that the shadow volume is _closed_, so it can be used
 * either for z-pass or z-fail (or anything else).
 * 
 * <h3>Contract for the model data</h3>
 * <ul>
 * <li>every triangle shall only be inserted once</li>
 * <li>two vertices do not share the same position</li>
 * </ul>
 * <p>
 * If two vertices share the same position the result is undefined. If the
 * z-fail method is used the shadow will probably have some errors, if z-pass is
 * used it can still work, but most likely won't either.
 * </p>
 * 
 * @author Tim Jörgen
 */
public class VolumeShadowCreator {

	private HashMap<Edge, Edge> edgeMap;
	private Vector3f lightSource;
	private Vector3f occluderPosition;
	private float infinity = 100.0f;
	private ArrayList<Triangle> frontCap = new ArrayList<Triangle>();

	/**
	 * Creates a new shadow processor.
	 * 
	 * @param lightSource
	 *            the position of the light source
	 * @param model
	 *            the model data
	 * @param occluderPosition
	 */
	public VolumeShadowCreator(Vector3f lightSource, ArrayList<Triangle> model,
			Vector3f occluderPosition) {
		this.lightSource = lightSource;
		this.occluderPosition = occluderPosition;
		edgeMap = new HashMap<Edge, Edge>(model.size() / 3); // model.size()/4 is a heuristic value based on guess
		Triangle triangle;

		// Each edge is inserted into a hash Map. When the edge is found again
		// it is removed. So only the silhouette edges will be in the hash map
		// when this algorithm is finished.
		int numOfEdges = 0;
		int removedEdges = 0;
		Edge edge0, edge1, edge2;
		for (int i = 0; i < model.size(); i++) {
			triangle = model.get(i);
			triangle.calculateNormal();
			edge0 = new Edge(triangle.get(0), triangle.get(1));
			edge1 = new Edge(triangle.get(1), triangle.get(2));
			edge2 = new Edge(triangle.get(2), triangle.get(0));
			
			
			if (lightSource.dot(triangle.getNormal()) > 0) {
				numOfEdges += 3;
				frontCap.add(triangle);
				if (edgeMap.containsKey(edge0)) {
					edgeMap.remove(edge0);
					removedEdges++;
				} else {
					edgeMap.put(edge0, edge0);
				}

				if (edgeMap.containsKey(edge1)) {
					edgeMap.remove(edge1);
					removedEdges++;
				} else {
					edgeMap.put(edge1, edge1);
				}

				if (edgeMap.containsKey(edge2)) {
					edgeMap.remove(edge2);
					removedEdges++;
				} else {
					edgeMap.put(edge2, edge2);
				}
			}
		}
	}

	/**
	 * Draws lines from the light Source to the silhouette of the object. This
	 * is very useful for debug purposes.
	 * 
	 * @param gl
	 */
	public void drawLightToSilhouetteLines(GL gl) {
		ArrayList<Edge> edgeList = new ArrayList<Edge>();
		edgeList.addAll(edgeMap.values());
		gl.glPushMatrix();
		gl.glDisable(GL.GL_LIGHTING);
		gl.glBegin(GL.GL_LINES);
		Edge edge;
		for (int i = 0; i < edgeList.size(); i++) {
			edge = edgeList.get(i);
			gl.glVertex3fv(lightSource.toArray(null), 0);
			gl.glVertex3f(edge.v0.x + occluderPosition.x, edge.v0.y
					+ occluderPosition.y, edge.v0.z + occluderPosition.z);

			gl.glVertex3fv(lightSource.toArray(null), 0);
			gl.glVertex3f(edge.v1.x + occluderPosition.x, edge.v1.y
					+ occluderPosition.y, edge.v1.z + occluderPosition.z);
		}
		gl.glEnd();
		gl.glEnable(GL.GL_LIGHTING);
		gl.glPopMatrix();
	}

	/**
	 * Draw not the shadow volume, but a wireframe of it
	 * 
	 * @param gl
	 *            the GL context
	 */
	public void drawShadowLines(GL gl) {
		int dawingMode = GL.GL_LINES;
		gl.glColor3f(1.0f, 0.0f, 0.0f);
		gl.glLineWidth(1.0f);
		drawShadowData(gl, dawingMode);
		gl.glLineWidth(1.0f);
	}

	/**
	 * Draw the solid shadow Volume.
	 * 
	 * @param gl
	 *            the GL context
	 */
	public void drawShadowVolume(GL gl) {
		int dawingMode = GL.GL_QUADS;
		drawShadowData(gl, dawingMode);
	}

	/**
	 * Draws the volume in the given drawing mode.
	 * 
	 * @param gl
	 *            the GL context
	 * @param drawingMode
	 *            The OpenGl drawing Mode. Like GL_LINES or GL_QUADS...
	 */
	private void drawShadowData(GL gl, int drawingMode) {
		ArrayList<Edge> edgeList = new ArrayList<Edge>();
		edgeList.addAll(edgeMap.values());
		Vector3f v0, v1, v2, v3;
		Vector3f bcv0, bcv1, bcv2;
		
		Edge edge;
		
		gl.glDisable(GL.GL_LIGHTING);
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glBegin(drawingMode);
		for (int i = 0; i < edgeList.size(); i++) {
			edge = edgeList.get(i);
			v0 = edge.v0.add(occluderPosition);
			v1 = edge.v1.add(occluderPosition);
			
			v2 = new Vector3f(v0.add(v0.subtract(lightSource).normalizeLocal().multLocal(this.infinity)));
			v3 = new Vector3f(v1.add(v1.subtract(lightSource).normalizeLocal().multLocal(this.infinity)));
			
			gl.glColor3f(1.0f, 0.0f, 0.0f);
			
			gl.glVertex3fv(v1.toArray(null), 0);
			gl.glVertex3fv(v0.toArray(null), 0);
			
			gl.glVertex3fv(v2.toArray(null), 0);
			gl.glVertex3fv(v3.toArray(null), 0);
		}
		gl.glEnd();

		// as the shadow volume is currently not closed, we draw the front cap
		// the part of the model, that is faced towards the light, extrude it to
		// infinity and draw it again
		gl.glColor3f(0.0f, 1.0f, 0.0f);
		gl.glBegin(GL.GL_TRIANGLES);
		for (int i = 0; i < frontCap.size(); i++) {
			Triangle tri = frontCap.get(i);
			v0 = tri.get(0).add(occluderPosition);
			v1 = tri.get(1).add(occluderPosition);
			v2 = tri.get(2).add(occluderPosition);
			
			// draw front cap
//			gl.glColor3f(0.0f, 1.0f, 0.0f);
//			gl.glVertex3fv(v0.toArray(null), 0);
//			gl.glVertex3fv(v1.toArray(null), 0);
//			gl.glVertex3fv(v2.toArray(null), 0);

			// draw back cap (extruded front cap)
			bcv0 = v0.add(v0.subtract(lightSource).normalizeLocal().multLocal(this.infinity));
			bcv1 = v1.add(v1.subtract(lightSource).normalizeLocal().multLocal(this.infinity));
			bcv2 = v2.add(v2.subtract(lightSource).normalizeLocal().multLocal(this.infinity));

			// CW not CCW, as this is the projected back side
			gl.glColor3f(0.0f, 0.0f, 1.0f);
			gl.glVertex3fv(bcv2.toArray(null), 0);
			gl.glVertex3fv(bcv1.toArray(null), 0);
			gl.glVertex3fv(bcv0.toArray(null), 0);

		}
		gl.glEnd();
	}

	/**
	 * Sets the size of the shadow volume. Default is 1000 Units. This should be
	 * a value that is still in your view frame but otherwise very large.
	 * 
	 * @param infinity
	 *            the value you want to use
	 */
	public void setShadowVolumeLength(float infinity) {
		this.infinity = infinity;
	}
}
