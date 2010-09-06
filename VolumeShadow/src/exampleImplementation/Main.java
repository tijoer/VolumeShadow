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

	Input input;
	Vector3f shadowLightPos;
	ExampleScene exampleScene;
	public static float rot = 0.0f;
	public static float xrot = 0.0f;
	public static float yrot = 0.0f;
	public static int lightRot;

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
		glu.gluPerspective(45.0f, h, 0.1f, 5000.0);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
		System.out.println("display changed");
	}

	public void init(GLAutoDrawable gLAutoDrawable) {
		this.gl = gLAutoDrawable.getGL();
		gl = gLAutoDrawable.getGL();

		// Setup the drawing area and shading mode
		gl.glDisable(GL.GL_CULL_FACE);
		gl.glCullFace(GL.GL_BACK);
		gl.glFrontFace(GL.GL_CCW);
		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glEnable(GL.GL_LIGHTING);
		gl.glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
		gl.glClearDepth(1.0f);
		gl.setSwapInterval(1);

		exampleScene = new ExampleScene(gl);
		this.input = new Input(gLAutoDrawable);	
	}

	@Override
	public void display(GLAutoDrawable arg0) {
		exampleScene.draw();
	}
}
