package exampleImplementation;

import javax.media.opengl.GL;

import com.sun.opengl.util.GLUT;

public class World {
	GL gl;
	GLUT glut = new GLUT();
	private Textures textures;
	
	World(GL gl) {
		this.gl = gl;
		this.textures = Textures.getInstance();
		textures.load("base_tex", "data/textures/rockwall_colormap.jpg");
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
	
	public void render() {
		gl.glPushMatrix();
		gl.glRotatef(Main.xrot, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(Main.yrot, 0.0f, 1.0f, 0.0f);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glEnable(GL.GL_LIGHTING);
		drawCube(1.5f);
		gl.glPopMatrix();
	}
}
