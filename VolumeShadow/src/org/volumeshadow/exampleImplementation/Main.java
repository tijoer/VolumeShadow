package org.volumeshadow.exampleImplementation;

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

public class Main implements GLEventListener {

	GL gl;
	GLU glu = new GLU();
	GLUT glut = new GLUT();

	Input input;
	ExampleScene exampleScene;
	public static float rot = 0.0f;
	public static float xrot = 0.0f;
	public static float yrot = 0.0f;
	public static int lightRot;

	public static void main(String[] args) {
		Frame frame = new Frame("Volume Shadow");
		// ------
		// This needs to be done for the shadow volume creator. Without the stencil bit method it won't work
		// ------
		GLCapabilities capabilities = new GLCapabilities();
		capabilities.setStencilBits(8);
		GLCanvas canvas = new GLCanvas(capabilities);
		// ------
		// ------
		// ------

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
		gl.glEnable(GL.GL_CULL_FACE);
        //gl.glDisable(GL.GL_CULL_FACE);
        gl.glCullFace(GL.GL_BACK);
        gl.glFrontFace(GL.GL_CCW);
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glClearColor(0.3f, 0.3f, 0.5f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        gl.glEnable(GL.GL_TEXTURE_2D);
        
        gl.setSwapInterval(1);


		exampleScene = new ExampleScene(gl);
		this.input = new Input(gLAutoDrawable);	
	}

	@Override
	public void display(GLAutoDrawable arg0) {
		exampleScene.draw();
	}
}
