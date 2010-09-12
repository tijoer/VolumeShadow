/*
 * This is intellectual property. You are not allowed 
 * to use it in any way, except you have a written 
 * allowance by the owner.
 */
package exampleImplementation;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <h2>Texture Abstraction</h2>
 * 
 * This is the texture abstraction. Just pass a filename and a new name for the
 * texture and it will be created using some default values that should fit for
 * most cases. For more technical details regarding the texture values just look
 * at the source code, it is rather simple.
 * 
 * If you want, you can not simply pass a filename, but you can pass a 
 * bufferedImage as well. This comes in useful for procedural created textures.
 * 
 * The name for the texture has to be unique. 
 * 
 * This is a <b>singleton</b> class.
 * 
 * @author Tim Jörgen
 */
public class Textures {

    private static Textures instance = null;
    private ArrayList<ManagedTexture> textureList;
    public TextureCoords coords;

    private class ManagedTexture {

        public String id;
        public Texture texture;
    }

    /**
     * Private. Use  getInstance instead. This is a singleton pattern.
     */
    private Textures() {
        this.textureList = new ArrayList<ManagedTexture>();
    }

    /**
     * @return An instance of the texture abstraction. (This class)
     * @author Tim J�rgen
     */
    public static Textures getInstance() {
        if (instance == null) {
            instance = new Textures();
        }
        return instance;
    }

    /**
     * Creates a new texture. 
     * 
     * @param id unique id to access the texture, after it is created
     * @param bufferedImage
     */
    public void load(String id, BufferedImage bufferedImage) {
        //check if texture already exists
        for (int i = 0; i < this.textureList.size(); i++) {
            if (this.textureList.get(i).id.equals(id)) {
                return;
            }
        }

        Texture texture;
        texture = TextureIO.newTexture(bufferedImage, true);

        ManagedTexture managedTexture = new ManagedTexture();
        managedTexture.id = id;
        managedTexture.texture = texture;
        this.textureList.add(managedTexture);

        System.out.println("Generating " + id + " finished");
    }

    /**
     * @param id eine beliebige id um wieder auf die textur zuzugreifen
     * @param filename der dateiname
     */
    public void load(String id, String filename) {
        //check if texture already exists
        for (int i = 0; i < this.textureList.size(); i++) {
            if (this.textureList.get(i).id.equals(id)) {
                return;
            }
        }

        try {
            File file = null;
            Texture texture;
            //file = new File(filename);

            //URL url = //this.getClass().getResource(filename);
            file = new File(filename);
            
            texture = TextureIO.newTexture(file, false);

            ManagedTexture managedTexture = new ManagedTexture();
            managedTexture.id = id;
            managedTexture.texture = texture;
            this.textureList.add(managedTexture);

            System.out.println("Loading " + filename + " finished");
        } catch (IOException ex) {
            System.out.println("Epic Fail...  asz3i0vdflmnbre3");
            Logger.getLogger(Textures.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates a new texture. 
     * 
     * @param id unique id to access the texture, after it is created
     */
    public void load(String id, int[] texture) {
        ByteBuffer tex = ByteBuffer.allocateDirect(texture.length * 4);
        //FIXME: gibt das die sortierung der Übergebenen oder die der gespeicherten werte an?
        tex.order(ByteOrder.BIG_ENDIAN);
        for (int v : texture) {
            tex.putInt(v);
        }
        tex.rewind();

        byte[] foo = new byte[texture.length];

        int i = 0;
        while (tex.hasRemaining()) {
            foo[i] = tex.get();
        }

    /*try {
    //Texture newTexture = TextureIO.newTexture(foo, true, null);
    } catch (IOException ex) {
    Logger.getLogger(Textures.class.getName()).log(Level.SEVERE, null, ex);
    } catch (GLException ex) {
    Logger.getLogger(Textures.class.getName()).log(Level.SEVERE, null, ex);
    }*/
    }

    /**
     * Activates and binds a texture. If the texture was not found an error will
     * be displayed (id and the code location, that called this method)
     * 
     * @param id the id of the texture, that should be activated
     */
    public void select(String id) {
        //look for the texture
        for (int i = 0; i < textureList.size(); i++) {
            if (textureList.get(i).id.equals(id)) {
                this.textureList.get(i).texture.enable();
                this.textureList.get(i).texture.bind();
                this.coords = this.textureList.get(i).texture.getImageTexCoords();
                return;
            }
        }

        //if not found: print error message and some stack trace infos
        System.out.println("[ERROR] Couldn't activate texture. Id: " + id);
        StackTraceElement[] stackTraceElements = new Exception().getStackTrace();
        System.out.println("        " + stackTraceElements[1].getClassName() +
                "." + stackTraceElements[1].getMethodName() +
                "   Line " + stackTraceElements[1].getLineNumber());
    }

    /**
     * Use this to change some texture values.
     * 
     * @param id
     * @param parameterName
     * @param params
     * @param offset
     */
    public void setTextureParameter(String id, int parameterName, float[] params, int offset) {
        for (int i = 0; i < textureList.size(); i++) {
            if (textureList.get(i).id.equals(id)) {
                textureList.get(i).texture.setTexParameterfv(parameterName, params, offset);
            }
        }
    }

    /**
     * Returns the height of the Texture. If the texture was not found -1 will be
     * returned.
     * 
     * @param id the id of the texture
     * @return height in pixels, -1 on error
     */
    public int getTextureHeight(String id) {
        Texture texture;
        for (int i = 0; i < textureList.size(); i++) {
            if (textureList.get(i).id.equals(id)) {
                return this.textureList.get(i).texture.getHeight();
            }
        }
        return -1;
    }
}
