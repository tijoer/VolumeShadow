package exampleImplementation;

import javax.media.opengl.GL;
import com.sun.opengl.util.GLUT;

import exampleImplementation.math.Vector3f;

import volumeshadow.Shadow.VolumeShadowCreator;

public class ExampleScene {

	private GL gl;
	private GLUT glut = new GLUT();
	private Camera camera;
	private VolumeShadowCreator volumeShadowCreator;

	/**
	 * This is the environment. For example this could be a map created with a
	 * map editor.
	 */
	private World world;
	/**
	 * This is our occluder. This object will create the shadow that is casted
	 * upon the environment.
	 */
	private ArraySphere occluder;

	public ExampleScene(GL gl) {
		this.gl = gl;
		this.camera = Camera.getInstance(gl, new Vector3f(0, 0, 10),
				new Vector3f(0, 0, 1));
		this.world = new World(gl);
		this.occluder = new ArraySphere(gl, 0.3f, 4);
	}

	public void draw() {
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		camera.rotateAccordingToCameraPosition();
		camera.translateAccordingToCameraPosition();

		Vector3f lightAmbient = new Vector3f(0.15f, 0.15f, 0.15f);
		Vector3f lightDiffuse = new Vector3f(1.0f, 1.0f, 1.0f);
		Vector3f lightPosition = new Vector3f(2.7f, 0.7f, 9.0f);

		gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, lightAmbient.toArray(null),
				0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, lightDiffuse.toArray(null),
				0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION,
				lightPosition.toArray(null), 0);
		gl.glEnable(GL.GL_LIGHT0);

		gl.glPushMatrix();
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glDisable(GL.GL_LIGHTING);
		gl.glColor3f(lightDiffuse.x, lightDiffuse.y, lightDiffuse.z);
		gl.glTranslatef(lightPosition.x, lightPosition.y, lightPosition.z);
		glut.glutSolidSphere(0.03f, 10, 10);
		gl.glPopMatrix();

		world.render();

		final Vector3f occluderPosition = new Vector3f(0.0f, 0.0f, 3.0f);
		gl.glPushMatrix();
		volumeShadowCreator = new VolumeShadowCreator(lightPosition,
				occluder.trianglesList, occluderPosition);
		volumeShadowCreator.drawLightToSilhouetteLines(gl);
		volumeShadowCreator.drawShadowVolume(gl);

		gl.glTranslatef(occluderPosition.x, occluderPosition.y,
				occluderPosition.z);
		occluder.render();
		gl.glPopMatrix();
	}
}

// public void renderOcclouder(float lightFactor) {
// camera.rotateAccordingToCameraPosition();
// camera.translateAccordingToCameraPosition();
//
// // shadow object
// gl.glDisable(GL.GL_TEXTURE_2D);
//
// gl.glCullFace(GL.GL_BACK);
// gl.glFrontFace(GL.GL_CCW);
// gl.glEnable(GL.GL_CULL_FACE);
//
// cube.draw(gl);
// }

// public void renderScene(float lightFactor) {
// camera.rotateAccordingToCameraPosition();
// camera.translateAccordingToCameraPosition();
//
// gl.glPushMatrix();
// shadowLightPos = new Vector3f((float) Math.sin(rot * 0.1f) * 100.2f,
// (float) -Math.sin(rot * 0.15f) * 100.2f, 1000.0f);
// gl.glTranslatef(shadowLightPos.x, shadowLightPos.y, shadowLightPos.z);
// float[] shadowLight_diffuse = {1.0f * lightFactor, 0.0f * lightFactor,
// 0.0f * lightFactor, 1.0f};
// float[] shadowLight_ambient = {0.0f * lightFactor, 0.0f * lightFactor,
// 0.0f * lightFactor, 1.0f};
// gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, shadowLightPos.asFloatArray(),
// 0);
// gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, shadowLight_diffuse, 0);
// gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, shadowLight_ambient, 0);
//
// //sphere at light position
// gl.glDisable(GL.GL_TEXTURE_2D);
// //gl.glDisable(GL.GL_LIGHTING);
// gl.glColor3f(shadowLight_diffuse[0], shadowLight_diffuse[1],
// shadowLight_diffuse[2]);
// glut.glutSolidSphere(4.5f, 10, 10);
// gl.glEnable(GL.GL_TEXTURE_2D);
// gl.glPopMatrix();
//
// gl.glActiveTexture(GL.GL_TEXTURE0);
// //gl.glEnable(GL.GL_LIGHTING);
// textures.select("base_tex");
// drawQuad(700.0f);
// gl.glLoadIdentity();
// }
//
// /**
// * OpenGL entry Point is here.
// */
// public void display(GLAutoDrawable drawable) {
// gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
// gl.glLoadIdentity();
//
// renderScene(1.0f);
// gl.glLoadIdentity();
// renderOcclouder(1.0f);
// findSilhouette = new ShadowProcessor(shadowLightPos,
// cube.triangleList);
//
// gl.glClearStencil(0);
//
// if (Presentation.drawShadowLines)
// findSilhouette.drawLightToSilhouetteLines(gl);
// if (Presentation.drawShadowVolume)
// findSilhouette.drawShadowVolume(gl);
// gl.glDisable(GL.GL_CULL_FACE);
// if (Presentation.drawSilhouette)
// findSilhouette.drawShadowLines(gl);
//
// gl.glColorMask(false, false, false, false);
// gl.glDepthMask(false);
// gl.glEnable(GL.GL_STENCIL_TEST);
// gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
// gl.glDisable(GL.GL_LIGHTING);
// gl.glStencilFunc(GL.GL_ALWAYS, 0, 0xFFFFFFF);
// gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_INCR);
//
// gl.glEnable(GL.GL_CULL_FACE);
// gl.glFrontFace(GL.GL_CCW);
//
// findSilhouette.drawShadowVolume(gl);
//
// gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_DECR);
// gl.glFrontFace(GL.GL_CW);
// findSilhouette.drawShadowVolume(gl);
//
// gl.glFrontFace(GL.GL_CCW);
//
// gl.glDepthFunc(GL.GL_LESS);
// gl.glColorMask(true, true, true, true); // rgba
//
// gl.glDepthMask(true);
// gl.glStencilFunc(GL.GL_NOTEQUAL, 0, 0xFFFFFFFF);
// gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
//
// gl.glLoadIdentity();
// gl.glDisable(GL.GL_LIGHTING);
// gl.glDisable(GL.GL_DEPTH_TEST);
//
// renderScene(0.5f);
// renderOcclouder(1.0f);
// gl.glEnable(GL.GL_DEPTH_TEST);
//
// // gl.glDepthMask(true);
// gl.glDisable(GL.GL_STENCIL_TEST);
// gl.glDisable(GL.GL_BLEND);
// }