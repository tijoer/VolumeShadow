package exampleImplementation;

import javax.media.opengl.GL;
import exampleImplementation.math.Vector3f;
import volumeshadow.Shadow.VolumeShadowCreator;

public class ExampleScene {

	private GL gl;
	private Camera camera;
	private VolumeShadowCreator volumeShadowCreator;

	/**
	 * This is the environment. For example this could be a map created with a
	 * map editor.
	 */
	private World world;

	public ExampleScene(GL gl) {
		this.gl = gl;
        this.camera = Camera.getInstance(gl, new Vector3f(0, 0, 10), new Vector3f(0, 0, 1));
		this.world = new World(gl);
	}

	public void draw() {
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		volumeShadowCreator = new VolumeShadowCreator(world.getLightPosition(), world.occluder.trianglesList, world.occluderPosition);

		world.render(1.0f);
        gl.glLoadIdentity();
        world.renderOccluder(1.0f);
        
        gl.glClearStencil(0);
        
        
        gl.glDisable(GL.GL_CULL_FACE);
        
        
        gl.glColorMask(false, false, false, false);
        gl.glDepthMask(false);
        gl.glEnable(GL.GL_STENCIL_TEST);
        gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
        gl.glDisable(GL.GL_LIGHTING);
        gl.glStencilFunc(GL.GL_ALWAYS, 0, 0xFFFFFFF);
        gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_INCR);
        
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glFrontFace(GL.GL_CCW);

            volumeShadowCreator.drawShadowVolume(gl);
        
        gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_DECR);
        gl.glFrontFace(GL.GL_CW);

        volumeShadowCreator.drawShadowVolume(gl);
        
        gl.glFrontFace(GL.GL_CCW);
        
        gl.glDepthFunc(GL.GL_LESS);
        gl.glColorMask(true, true, true, true); // rgba
        
        gl.glDepthMask(true);
        gl.glStencilFunc(GL.GL_NOTEQUAL, 0, 0xFFFFFFFF);
        gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
        
        gl.glLoadIdentity();
        gl.glDisable(GL.GL_LIGHTING);
        gl.glDisable(gl.GL_DEPTH_TEST);
        
        world.render(0.0001f);
        gl.glCullFace(GL.GL_BACK);
        gl.glFrontFace(GL.GL_CCW);
        gl.glEnable(GL.GL_CULL_FACE);
        world.renderOccluder(1.0f);
        gl.glEnable(gl.GL_DEPTH_TEST);
        
        //gl.glDepthMask(true);
        gl.glDisable(GL.GL_STENCIL_TEST);
        gl.glDisable(GL.GL_BLEND);
        gl.glLoadIdentity();
        
	}
}