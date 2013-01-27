package px.spaceInvaders.graphics;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class SplashScreen {
    
    // ++++ ++++ Data ++++ ++++
    
    private int vao;
    private int vbo;
    private int tex;
    
    private int positionAttribute;
    private int texcoordAttribute;
    
    private int tintUniform;
    
    private int shaderProgram;
    
    // ++++ ++++ Initialization ++++ ++++
    
    public SplashScreen(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        int[] temp = new int[1];
        
        //Create Vertex Array Object
        gl.glGenVertexArrays(1, temp, 0);
        vao = temp[0];
        gl.glBindVertexArray(vao);
        
        //Compile unique Shader Program
        shaderProgram = ShaderLoader.loadShaders(drawable, 
                "res/shaders/vertexPlain.glsl", "res/shaders/fragment.glsl");
        gl.glUseProgram(shaderProgram);
        
        tintUniform = gl.glGetUniformLocation(shaderProgram, "tint");
        
        float[] vertices = {
                //Position         //Texcoord
                -1.0f, 1.0f, -0.99f, 0.0f, 0.0f, //Top Left
                -1.0f,-1.0f, -0.99f, 0.0f, 1.0f, //Bottom Left
                 1.0f, 1.0f, -0.99f, 1.0f, 0.0f, //Top Right
                 1.0f,-1.0f, -0.99f, 1.0f, 1.0f //Bottom Right
        };
        
        //Store verts in a Vertex Buffer Object
        gl.glGenBuffers(1, temp, 0);
        vbo = temp[0];
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, vertices.length * 4,
                FloatBuffer.wrap(vertices), GL4.GL_STATIC_DRAW);
        
        //Setup vertex shader attribute pointers.
        positionAttribute = gl.glGetAttribLocation(shaderProgram, "position");
        gl.glEnableVertexAttribArray(positionAttribute);
        gl.glVertexAttribPointer(positionAttribute, 3, GL4.GL_FLOAT, false, 5 * 4, 0);
        
        texcoordAttribute = gl.glGetAttribLocation(shaderProgram, "texcoord");
        gl.glEnableVertexAttribArray(texcoordAttribute);
        gl.glVertexAttribPointer(texcoordAttribute, 2, GL4.GL_FLOAT, false, 5 * 4, 3 * 4);
        
        //Setup texture
        gl.glGenTextures(1, temp, 0);
        tex = temp[0];
        ByteBuffer texBuffer = null;
        int width = 0;
        int height = 0;
        gl.glBindTexture(GL4.GL_TEXTURE_2D, tex);
        try {
            InputStream in = new FileInputStream("res/textures/SplashScreen.png");
            PNGDecoder decoder = new PNGDecoder(in);
            
            width = decoder.getWidth();
            height = decoder.getHeight();
            
            texBuffer = ByteBuffer.allocateDirect(4 * width * height);
            decoder.decode(texBuffer, width * 4, Format.RGBA);
            texBuffer.flip();
        } catch (IOException e) {
            //TODO handle this a little more elgantly
            e.printStackTrace();
            System.exit(-1);
        }
        gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, width, height, 0, 
                GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, texBuffer);
        //Set Texture Parameters
        gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
        gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
        gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, 
                GL4.GL_LINEAR);
        gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, 
                GL4.GL_LINEAR);
        
        //Unbind VAO
        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
    }
    
    // ++++ ++++ Clean up ++++ ++++
    
    public void dispose(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        int[] temp = new int[1];
        
        gl.glDeleteProgram(shaderProgram);
        temp[0] = vbo;
        gl.glDeleteBuffers(1, temp, 0);
        temp[0] = tex;
        gl.glDeleteTextures(1, temp, 0);
        temp[0] = vao;
        gl.glDeleteVertexArrays(1, temp, 0);
    }
    
    // ++++ ++++ Rendering ++++ ++++
    
    public void draw(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();

        gl.glBindVertexArray(vao);
        gl.glUseProgram(shaderProgram);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, tex);
        gl.glUniform4f(tintUniform, 1f, 1f, 1f, 1f);
        
        gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, 4);
        
        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
    }
}
