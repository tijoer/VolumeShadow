package exampleImplementation;

import java.util.ArrayList;
import javax.media.opengl.GL;
import com.sun.opengl.util.GLUT;

import volumeshadow.Shadow.ShadowScene;
import exampleImplementation.math.Triangle;
import exampleImplementation.math.Vector3f;

public class World implements ShadowScene {
	GL gl;
	GLUT glut = new GLUT();
	private Textures textures;
	private Vector3f lightAmbient = new Vector3f(0.15f, 0.15f, 0.15f);
	private Vector3f lightDiffuse = new Vector3f(1.0f, 1.0f, 1.0f);
	private Vector3f lightPosition = new Vector3f(2.7f, 0.7f, 9.0f);
	/**
	 * This is our occluder. This object will create the shadow that is casted
	 * upon the environment.
	 */
	public ArraySphere occluder;
	public Vector3f occluderPosition = new Vector3f(0.0f, 0.0f, 3.0f);
	
	World(GL gl) {
		this.gl = gl;
		this.textures = Textures.getInstance();
		textures.load("base_tex", "data/textures/rockwall_colormap.jpg");
		this.occluder = new ArraySphere(gl, 0.3f, 4);
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

	public Vector3f getLightPosition() {
		return this.lightPosition;
	}

	@Override
	public ArrayList<Triangle> getOccluderVertexData() {
		return this.occluder.trianglesList;
	}

	@Override
	public Vector3f getOccluderPosition() {
		return this.occluderPosition;
	}

	@Override
	public void renderWorld(float lightFactor) {
		lightAmbient = new Vector3f(0.15f, 0.15f, 0.15f);
		lightDiffuse = new Vector3f(lightFactor, lightFactor, lightFactor);
		lightPosition = new Vector3f(2.7f, 0.7f, 9.0f);

		gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, lightAmbient.toArray(null), 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, lightDiffuse.toArray(null), 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPosition.toArray(null), 0);
		gl.glEnable(GL.GL_LIGHT0);

		gl.glPushMatrix();
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glDisable(GL.GL_LIGHTING);
		gl.glColor3f(lightDiffuse.x, lightDiffuse.y, lightDiffuse.z);
		gl.glTranslatef(lightPosition.x, lightPosition.y, lightPosition.z);
		glut.glutSolidSphere(0.03f, 10, 10);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(Main.xrot, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(Main.yrot, 0.0f, 1.0f, 0.0f);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glEnable(GL.GL_LIGHTING);
		drawCube(1.5f);
		gl.glPopMatrix();
	}

	@Override
	public void renderOccluder(float lightFactor) {
		gl.glPushMatrix();
		gl.glTranslatef(occluderPosition.x, occluderPosition.y,	occluderPosition.z);
		occluder.render();
		gl.glPopMatrix();	
	}
}
