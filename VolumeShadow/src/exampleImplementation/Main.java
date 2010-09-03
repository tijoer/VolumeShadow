package exampleImplementation;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.GLUT;

import exampleImplementation.math.Vector3f;
import volumeshadow.Shadow.ShadowProcessor;

public class Main implements GLEventListener {

	GL gl;
	GLU glu = new GLU();
	GLUT glut = new GLUT();
	public static float rot = 0.0f;

	Textures textures;
	Camera camera;
	Input input;
	Tools tools;
	ShadowProcessor findSilhouette;

	ArraySphere sphere;
	Vector3f shadowLightPos;
	Cube cube;

	public static void main(String[] args) {
		Frame frame = new Frame("Volume Shadow");
		GLCapabilities capabilities = new GLCapabilities();
		capabilities.setStencilBits(8);
		GLCanvas canvas = new GLCanvas(capabilities);
		// GLCanvas canvas = new GLCanvas();

		canvas.addGLEventListener(new Main());
		frame.add(canvas);
		frame.setSize(1024, 768);
		final Animator animator = new Animator(canvas);
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// Run this on another thread than the AWT event queue to
				// make sure the call to Animator.stop() completes before
				// exiting
				new Thread(new Runnable() {

					public void run() {
						animator.stop();
						System.exit(0);
					}
				}).start();
			}
		});
		// Center frame
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		animator.start();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL gl = drawable.getGL();

		if (height <= 0) { // avoid a divide by zero error
			height = 1;
		}
		final float h = (float) width / (float) height;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, h, 5.00, 5000.0);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void init(GLAutoDrawable gLAutoDrawable) {
		// Use debug pipeline
		// drawable.setGL(new DebugGL(drawable.getGL()));
		gl = gLAutoDrawable.getGL();

		// Setup the drawing area and shading mode
		gl.glDisable(GL.GL_CULL_FACE);
		gl.glCullFace(GL.GL_BACK);
		gl.glFrontFace(GL.GL_CCW);
		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glEnable(GL.GL_TEXTURE_2D);

		gl.setSwapInterval(1);

		this.textures = Textures.getInstance();
		textures.load("base_tex", "data/textures/rockwall_colormap.jpg");

		this.camera = Camera.getInstance(gl, glu, new Vector3f(0.0f, 90.0f,
				1500.0f), new Vector3f(-1.0f, 0.0f, 0.0f));
		Camera.xyAngle = 0.0f;
		Camera.xzAngle = 0.0f;

		this.input = new Input();
		gLAutoDrawable.addKeyListener(input);
		gLAutoDrawable.addMouseListener(input);
		gLAutoDrawable.addMouseMotionListener(input);
		this.gl = gLAutoDrawable.getGL();

		this.tools = new Tools(gl);
		sphere = new ArraySphere(gl, 100.0f, 3);
		sphere.translocate(0.0f, 200.0f, 500.0f);
		
		cube = new Cube(70.0f);
	}

	private void drawQuad(float size) {
		Vector3f v0 = new Vector3f(-size, -size, 0.0f);
		Vector3f v1 = new Vector3f(size, -size, 0.0f);
		Vector3f v2 = new Vector3f(size, size, 0.0f);
		Vector3f v3 = new Vector3f(-size, size, 0.0f);

		Vector3f normale = new Vector3f();
		Vector3f tangent = new Vector3f();
		Vector3f binormal = new Vector3f();

		Vector3f.FindInvTBN(v0, v1, v2, 0.0f, 0f, 1f, 0f, 1f, 1f, normale,
				binormal, tangent);
		
		gl.glBegin(GL.GL_QUADS);
		gl.glNormal3f(normale.x, normale.y, normale.z);
		gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 0.0f, 0.0f);
		gl.glVertex3f(v0.x, v0.y, v0.z);
		gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 1.0f, 0.0f);
		gl.glVertex3f(v1.x, v1.y, v1.z);
		gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 1.0f, 1.0f);
		gl.glVertex3f(v2.x, v2.y, v2.z);
		gl.glMultiTexCoord2f(GL.GL_TEXTURE0, 0.0f, 1.0f);
		gl.glVertex3f(v3.x, v3.y, v3.z);
		gl.glEnd();
	}

	public void renderOcclouder(float lightFactor) {
		camera.rotateAccordingToCameraPosition();
		camera.translateAccordingToCameraPosition();

		// shadow object
		gl.glDisable(GL.GL_TEXTURE_2D);

		gl.glCullFace(GL.GL_BACK);
		gl.glFrontFace(GL.GL_CCW);
		gl.glEnable(GL.GL_CULL_FACE);
		
		cube.draw(gl);
	}

	public void renderScene(float lightFactor) {
		camera.rotateAccordingToCameraPosition();
		camera.translateAccordingToCameraPosition();

		gl.glPushMatrix();
		shadowLightPos = new Vector3f((float) Math.sin(rot * 0.1f) * 100.2f, (float) -Math.sin(rot * 0.15f) * 100.2f, 1000.0f);
		gl.glTranslatef(shadowLightPos.x, shadowLightPos.y, shadowLightPos.z);
		float[] shadowLight_diffuse = {1.0f * lightFactor, 0.0f * lightFactor, 0.0f * lightFactor, 1.0f};
		float[] shadowLight_ambient = {0.0f * lightFactor, 0.0f * lightFactor,	0.0f * lightFactor, 1.0f};
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, shadowLightPos.asFloatArray(), 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, shadowLight_diffuse, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, shadowLight_ambient, 0);
		 
		//sphere at light position
		gl.glDisable(GL.GL_TEXTURE_2D);
		//gl.glDisable(GL.GL_LIGHTING);
		gl.glColor3f(shadowLight_diffuse[0], shadowLight_diffuse[1], shadowLight_diffuse[2]);
		glut.glutSolidSphere(4.5f, 10, 10);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glPopMatrix();

		gl.glActiveTexture(GL.GL_TEXTURE0);
		//gl.glEnable(GL.GL_LIGHTING);
		textures.select("base_tex");
		drawQuad(700.0f);
		gl.glLoadIdentity();
	}

	/**
	 * OpenGL entry Point is here.
	 */
	public void display(GLAutoDrawable drawable) {
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		renderScene(1.0f);
		gl.glLoadIdentity();
		renderOcclouder(1.0f);
		findSilhouette = new ShadowProcessor(shadowLightPos,
				cube.triangleList);

		gl.glClearStencil(0);

		if (Presentation.drawShadowLines)
			findSilhouette.drawLightToSilhouetteLines(gl);
		if (Presentation.drawShadowVolume)
			findSilhouette.drawShadowVolume(gl);
		gl.glDisable(GL.GL_CULL_FACE);
		if (Presentation.drawSilhouette)
			findSilhouette.drawShadowLines(gl);

		gl.glColorMask(false, false, false, false);
		gl.glDepthMask(false);
		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
		gl.glDisable(GL.GL_LIGHTING);
		gl.glStencilFunc(GL.GL_ALWAYS, 0, 0xFFFFFFF);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_INCR);

		gl.glEnable(GL.GL_CULL_FACE);
		gl.glFrontFace(GL.GL_CCW);

		findSilhouette.drawShadowVolume(gl);

		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_DECR);
		gl.glFrontFace(GL.GL_CW);
		findSilhouette.drawShadowVolume(gl);

		gl.glFrontFace(GL.GL_CCW);

		gl.glDepthFunc(GL.GL_LESS);
		gl.glColorMask(true, true, true, true); // rgba

		gl.glDepthMask(true);
		gl.glStencilFunc(GL.GL_NOTEQUAL, 0, 0xFFFFFFFF);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);

		gl.glLoadIdentity();
		gl.glDisable(GL.GL_LIGHTING);
		gl.glDisable(GL.GL_DEPTH_TEST);

		renderScene(0.5f);
		renderOcclouder(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST);

		// gl.glDepthMask(true);
		gl.glDisable(GL.GL_STENCIL_TEST);
		gl.glDisable(GL.GL_BLEND);
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}
}
