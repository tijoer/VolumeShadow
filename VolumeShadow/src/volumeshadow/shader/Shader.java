package volumeshadow.shader;

import javax.media.opengl.GL;

/**
 * <h2>Creates a Shader</h2>
 * 
 * This creates a new shader. Just pass the filenames. Note that these can even
 * be in the current jar file.
 * Remember to catch the two exeptions.
 * 
 * <code style="white-space: pre;"> 
 * do this once:
 * try {
 *     shaderName = new Shader(gl, "/shader/paralaxMap.vert", "/shader/paralaxMap.frag");
 * } catch (CompilerException ex) {
 *     Logger.getLogger(EPParalax.class.getName()).log(Level.SEVERE, null, ex);
 * } catch (LinkerException ex) {
 *     Logger.getLogger(EPParalax.class.getName()).log(Level.SEVERE, null, ex);
 * }
 * 
 * do this every frame:
 * spotlightShader.activate();
 * spotlightShader.setUniformVar1i("Normal", 0); //for example
 * doDrawingStuffHere();
 * spotlightShader.deactivate();
 * 
 * </code>
 * @author Christoph Peuser, Tim J�rgen
 */
public class Shader {

    public int progid;
    private ShaderObject vertexShader,  fragmentShader;
    GL gl;

    public Shader(GL gl, String vertexfile, String fragmentfile) throws CompilerException, LinkerException {
        this.gl = gl;
        this.progid = gl.glCreateProgram();
        //load the two components into shader objects and compile them
        this.vertexShader = new ShaderObject(gl, GL.GL_VERTEX_SHADER, vertexfile);
        this.fragmentShader = new ShaderObject(gl, GL.GL_FRAGMENT_SHADER, fragmentfile);
        //link the two shader objects
        link();
    }

    public void activate() {
        gl.glUseProgram(progid);
    }

    public void deactivate() {
        // revert to the fixed pipeline
        gl.glUseProgram(0);
    }

    private void link() throws LinkerException {
        gl.glAttachShader(progid, vertexShader.shaderobject);
        gl.glAttachShader(progid, fragmentShader.shaderobject);
        gl.glLinkProgram(progid);
        //find out whether the programm was linked succesfully
        int[] linkerror = new int[1];
        gl.glGetProgramiv(progid, GL.GL_LINK_STATUS, linkerror, 0);
        if (linkerror[0] != GL.GL_TRUE) {
            //error while linking, throw the appropriate exception
            // first get the lenght of the error log
            gl.glGetProgramiv(progid, GL.GL_INFO_LOG_LENGTH, linkerror, 0);
            // then the log itself
            byte[] infolog = new byte[linkerror[0]];
            gl.glGetProgramInfoLog(progid, linkerror[0], null, 0, infolog, 0);

            throw new LinkerException(infolog);
        }
    }

    public void setUniformVar1i(String name, int value) {
        int uniformLocation = gl.glGetUniformLocation(progid, name);
        gl.glUniform1iARB(uniformLocation, value);
    }

    public void setUniformVar3f(String name, float value0, float value1, float value2) {
        int uniformLocation = gl.glGetUniformLocation(progid, name);
        gl.glUniform3f(uniformLocation, value0, value1, value2);
    }

    void setUniformVar4f(String name, float v0, float v1, float v2, float v3) {
        int uniformLocation = gl.glGetUniformLocation(progid, name);
        gl.glUniform4f(uniformLocation, v0, v1, v2, v3);
    }

    public void setAttrib3f(String name, float value0, float value1, float value2) {
        int attribLocation = gl.glGetAttribLocation(progid, name);
        if (attribLocation == -1) {
            System.out.println("Shader failed");
        }
        gl.glVertexAttrib3f(attribLocation, value0, value1, value2);
    }
}
