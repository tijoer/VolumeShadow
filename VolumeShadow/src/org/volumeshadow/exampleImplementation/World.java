package org.volumeshadow.exampleImplementation;

import java.util.ArrayList;
import javax.media.opengl.GL;

import org.volumeshadow.ShadowScene;
import org.volumeshadow.math.Triangle;
import org.volumeshadow.math.Vector3f;

import com.sun.opengl.util.GLUT;


/**
 * This is our example scene. You can do everything you want here, as long as
 * you implement the <code>ShadowScene</code> interface correctly.
 * 
 * @author Tim Jörgen
 */
public class World implements ShadowScene {
	GL gl;
	GLUT glut = new GLUT();
	private Textures textures;
	/**
	 * This is our occluder. This object will create the shadow that is casted
	 * upon the environment. For example this could also be your player model. I
	 * choose an basic sphere. You need direct access to the vertex data in each
	 * frame.
	 */
	private ArraySphere occluder;
	/**
	 * This is the position of your model (the position that is passed to
	 * gl.glTranslatef(...). You can also pass (0.0f, 0.0f, 0.0f) if you don't
	 * translate your model.
	 */
	private Vector3f occluderPosition = new Vector3f(0.0f, -0.5f, -5.0f);
	/**
	 * This is the position of the light, that casts the shadow.
	 */
	private Vector3f lightPosition = new Vector3f(0.0f, 0.0f, 0.1f); 

	World(GL gl) {
		this.gl = gl;
		this.textures = Textures.getInstance();
		textures.load("base_tex", "data/textures/rockwall_colormap.jpg");
		this.occluder = new ArraySphere(gl, 0.3f, 2);
	}

	public void drawCube(float size) {
		gl.glBegin(GL.GL_QUADS);

		// Front Face
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-size, -size, size);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(size, -size, size);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(size, size, size);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-size, size, size);

		// Back Face
		gl.glNormal3f(0.0f, 0.0f, -1.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-size, -size, -size);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(-size, size, -size);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(size, size, -size);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(size, -size, -size);

		// Top Face
		gl.glNormal3f(0.0f, 1.0f, 0.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-size, size, -size);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-size, size, size);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(size, size, size);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(size, size, -size);

		// Bottom Face
		gl.glNormal3f(0.0f, -1.0f, 0.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(-size, -size, -size);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(size, -size, -size);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(size, -size, size);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-size, -size, size);

		// Right face
		gl.glNormal3f(1.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(size, -size, -size);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(size, size, -size);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(size, size, size);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(size, -size, size);

		// Left Face
		gl.glNormal3f(-1.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-size, -size, -size);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-size, -size, size);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(-size, size, size);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-size, size, -size);
		gl.glEnd();
	}

	/**
	 * This returns the position of the light.
	 */
	@Override
	public Vector3f getLightPosition() {
		return this.lightPosition;
	}

	/**
	 * This returns the vertex data of your occluder.
	 */
	@Override
	public ArrayList<Triangle> getOccluderVertexData() {
		return this.occluder.trianglesList;
	}

	/**
	 * This returns your occluder position.
	 */
	@Override
	public Vector3f getOccluderPosition() {
		return this.occluderPosition;
	}

	/**
	 * This renders your scene, but WITHOUT the occluder. It is important to
	 * separate this, so that self shadowing of the occluders works.
	 */
	@Override
	public void renderWorld(boolean renderWithLight) {
		gl.glDisable(GL.GL_LIGHTING);
		
		float lightFactor;
		if(renderWithLight) {
			lightFactor = 0.7f;
		} else {
			lightFactor = 0.3f;
		}
		
		// notice how the translation to draw the sphere at the light position
		// is done between a matrix push and pop
		gl.glPushMatrix();
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glColor3f(lightFactor, lightFactor, lightFactor);
		gl.glTranslatef(lightPosition.x, lightPosition.y, lightPosition.z);
		glut.glutSolidSphere(0.03f, 10, 10);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(0, 0, -10);
		//If you press 'q' Main.xrot will change. To show a bit of animation in the scene.
		gl.glRotatef(Main.xrot, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(Main.yrot, 0.0f, 1.0f, 0.0f);
		gl.glEnable(GL.GL_TEXTURE_2D);
		drawCube(1.5f);
		gl.glPopMatrix();
	}

	/**
	 * This renders the occluder.
	 */
	@Override
	public void renderOccluder(boolean renderWithLight) {
		gl.glPushMatrix();
		gl.glTranslatef(occluderPosition.x, occluderPosition.y,	occluderPosition.z);
		occluder.render();
		gl.glPopMatrix();
	}
}
