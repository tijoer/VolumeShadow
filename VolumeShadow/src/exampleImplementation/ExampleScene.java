package exampleImplementation;

import javax.media.opengl.GL;

import exampleImplementation.math.Vector3f;
import volumeshadow.Shadow.ShadowScene;
import volumeshadow.Shadow.VolumeShadowCreator;

/**
 * This is an example scene to demonstrate how the volume shadow creator works.
 * All the standard OpenGl stuff is done in <code>Main</code>. The only
 * additional thing that needs to be done in Main, is the following code:
 * <pre>
 * {@code
 * 		GLCapabilities capabilities = new GLCapabilities();
 *		capabilities.setStencilBits(8);
 *		GLCanvas canvas = new GLCanvas(capabilities);
 * }
 * </pre>
 *  
 * @author Tim Jörgen
 * 
 */
public class ExampleScene {

	private GL gl;
	private Camera camera;	// Don't look at the camera code for this example.
	private VolumeShadowCreator volumeShadowCreator;

	/**
	 * This is our scene, that implements the ShadowScene interface. Here you do
	 * the drawing of your world and your models.
	 */
	private ShadowScene shadowScene;

	public ExampleScene(GL gl) {
		this.gl = gl;
		//you don't have to look at the camera code
		this.camera = Camera.getInstance(gl, new Vector3f(0, 0, 10), new Vector3f(0, 0, 1));
		this.shadowScene = new World(gl);
	}

	/**
	 * This is called every frame. gl.glTranslatef(...) and gl.glRotatef(...)
	 * are done in the camera instance. Every view matrix transformation in
	 * shadow scene is done between gl.glPushMatrix and gl.glPopMatrix.
	 */
	public void draw() {
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		camera.rotateAccordingToCameraPosition();
		camera.translateAccordingToCameraPosition();

		volumeShadowCreator = new VolumeShadowCreator(gl, shadowScene);
		volumeShadowCreator.renderSceneWithShadow();
	}
}