package px.spaceInvaders.graphics;

import java.nio.FloatBuffer;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
class TextRenderer {
    
    // ++++ ++++ Enum ++++ ++++
    
    public enum Align {
        LEFT,
        CENTER,
        RIGHT
    }
    
    // ++++ ++++ Data ++++ ++++
    
    protected SpriteMaster master;
    protected int texture;
    
    //OpenGL Storage
    int vao;
    int vbo;
    int shaderProgram;
    int depthUniform;
    int modelUniform;
    int viewUniform;
    int offsetUniform;
    int colorUniform;
    
    // ++++ ++++ Initialization ++++ ++++
    
    public TextRenderer(GLAutoDrawable drawable, SpriteMaster master, 
            String textMapFilename) {
        
        this.master = master;
        texture = master.loadTexture(drawable, textMapFilename);
        
        //OpenGL Setup
        
        GL4 gl = drawable.getGL().getGL4();
        int[] temp = new int[1];
        
        //Compile Shader Program
        shaderProgram = ShaderLoader.loadShaders(drawable, 
                "res/shaders/vertexText.glsl", "res/shaders/fragmentText.glsl");
        gl.glUseProgram(shaderProgram);
        
        //Setup uniforms
        depthUniform = gl.glGetUniformLocation(shaderProgram, "depth");
        modelUniform = gl.glGetUniformLocation(shaderProgram, "model");
        viewUniform = gl.glGetUniformLocation(shaderProgram, "view");
        offsetUniform = gl.glGetUniformLocation(shaderProgram, "offset");
        colorUniform = gl.glGetUniformLocation(shaderProgram, "color");

        Matrix4f view = master.getView();
        FloatBuffer viewBuffer = FloatBuffer.allocate(16);
        view.store(viewBuffer);
        viewBuffer.rewind();
        gl.glUniformMatrix4fv(viewUniform, 1, false, viewBuffer);
        
        //Setup VBO
        float[] vertices = {
                //Position   /Texcoord
                -0.5f, 0.5f, 0.0f,   0.0f,   //Top Left
                -0.5f,-0.5f, 0.0f,   0.125f, //Bottom Left
                 0.5f, 0.5f, 0.125f, 0.0f,   //Top Right
                 0.5f,-0.5f, 0.125f, 0.125f  //Bottom Right
        };
        gl.glGenBuffers(1, temp, 0);
        vbo = temp[0];
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, vertices.length * 4, 
                FloatBuffer.wrap(vertices), GL4.GL_STATIC_DRAW);
        
        //Setup VAO
        gl.glGenVertexArrays(1, temp, 0);
        vao = temp[0];
        gl.glBindVertexArray(vao);
        
        //Setup vertex shader attribute pointers.
        int positionAttribute = gl.glGetAttribLocation(shaderProgram, "position");
        gl.glEnableVertexAttribArray(positionAttribute);
        gl.glVertexAttribPointer(positionAttribute, 2, GL4.GL_FLOAT, false, 4 * 4, 0);
        
        int texcoordAttribute = gl.glGetAttribLocation(shaderProgram, "texcoord");
        gl.glEnableVertexAttribArray(texcoordAttribute);
        gl.glVertexAttribPointer(texcoordAttribute, 2, GL4.GL_FLOAT, false, 4 * 4, 2 * 4);
        
        //Unbind Everything
        gl.glBindVertexArray(0);
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
        gl.glUseProgram(0);
    }
    
    // ++++ ++++ Disposal ++++ ++++
    
    public void dispose(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        int[] temp = new int[1];
        
        gl.glDeleteProgram(shaderProgram);
        gl.glDeleteVertexArrays(1, temp, 0);
        gl.glDeleteBuffers(1, temp, 0);
    }
    
    // ++++ ++++ Rendering ++++ ++++
    
    public void drawString(GLAutoDrawable drawable, String string, Vector2f location, 
            float depth, Align align, Vector2f characterSize, Vector4f color) {
        if (characterSize == null) {
            characterSize = new Vector2f(8f, 16f);
        }
        
        GL4 gl = drawable.getGL().getGL4();
        
        gl.glBindVertexArray(vao);
        gl.glUseProgram(shaderProgram);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, texture);
        gl.glUniform1f(depthUniform, depth);
        
        char[] chars = new char[string.length()];
        string.getChars(0, string.length(), chars, 0);
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            Vector2f o = getCharacter(c);
            gl.glUniform2f(offsetUniform, o.x, o.y);
            
            if (c == ' ') {
                gl.glUniform4f(colorUniform, color.x, color.y, color.z, 0f);
            } else {
                gl.glUniform4f(colorUniform, color.x, color.y, color.z, color.w);
            }
            
            //Setup Model matrix
            //Scale
            Matrix4f model = Matrix4f.scale(
                    new Vector3f(characterSize.x, characterSize.y, 1.0f), 
                    new Matrix4f(), null);
            //Location
            Vector2f charLocation = 
                    new Vector2f(location.x + i * characterSize.x, location.y);
            switch (align) {
            case LEFT:
                charLocation.x += characterSize.x / 2;
                break;
            case CENTER:
                charLocation.x -= characterSize.x * (((float) chars.length) / 2f);
                break;
            case RIGHT:
                charLocation.x -= characterSize.x * (((float) chars.length) + 0.5f);
                break;
            }
            Matrix4f loc = Matrix4f.translate(charLocation, new Matrix4f(), null);
            model = Matrix4f.mul(loc, model, null);
            
            FloatBuffer buffer = FloatBuffer.allocate(16);
            model.store(buffer);
            buffer.rewind();
            gl.glUniformMatrix4fv(modelUniform, 1, false, buffer);
            gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, 4);
        }

        gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
        gl.glBindVertexArray(master.getVao());
        gl.glUseProgram(master.getShaderProgram());
    }
    
    /**As above, but white text with characters 16 points tall and 8 wide.
     * @param drawable OpenGL drawable context.
     * @param string String of text to render. */
    public void drawString(GLAutoDrawable drawable, String string, Vector2f location, 
            float depth, Align align) {
        drawString(drawable, string, location, depth, align, null, 
                new Vector4f(1f, 1f, 1f, 1f));
    }
    
    /**As above, but white text, center aligned with characters 16 points tall and 8 wide.
     * @param drawable OpenGL drawable context.
     * @param string String of text to render. */
    public void drawString(GLAutoDrawable drawable, String string, Vector2f location, 
            float depth) {
        drawString(drawable, string, location, depth, Align.CENTER, null, 
                new Vector4f(1f, 1f, 1f, 1f));
    }
    
    /**Retrieves the location of an ASCII character's glyph on the bitmap
     * @param c Character you want to render.
     * @return Location of the provided character's glyph on the bitmap, or a box 
     * if the provided character isn't available. */
    protected Vector2f getCharacter(char c) {
        switch (c) {
        case '0':
            return new Vector2f(0f,     0f);
        case '1':
            return new Vector2f(0.125f, 0f);
        case '2':
            return new Vector2f(0.25f,  0f);
        case '3':
            return new Vector2f(0.375f, 0f);
        case '4':
            return new Vector2f(0.5f,   0f);
        case '5':
            return new Vector2f(0.625f, 0f);
        case '6':
            return new Vector2f(0.75f,  0f);
        case '7':
            return new Vector2f(0.875f, 0f);
        case '8':
            return new Vector2f(0f,     0.125f);
        case '9':
            return new Vector2f(0.125f, 0.125f);
        case 'A':
        case 'a':
            return new Vector2f(0.25f,  0.125f);
        case 'B':
        case 'b':
            return new Vector2f(0.375f, 0.125f);
        case 'C':
        case 'c':
            return new Vector2f(0.5f,   0.125f);
        case 'D':
        case 'd':
            return new Vector2f(0.625f, 0.125f);
        case 'E':
        case 'e':
            return new Vector2f(0.75f,  0.125f);
        case 'F':
        case 'f':
            return new Vector2f(0.875f, 0.125f);
        case 'G':
        case 'g':
            return new Vector2f(0f,     0.25f);
        case 'H':
        case 'h':
            return new Vector2f(0.125f, 0.25f);
        case 'I':
        case 'i':
            return new Vector2f(0.25f,  0.25f);
        case 'J':
        case 'j':
            return new Vector2f(0.375f, 0.25f);
        case 'K':
        case 'k':
            return new Vector2f(0.5f,   0.25f);
        case 'L':
        case 'l':
            return new Vector2f(0.625f, 0.25f);
        case 'M':
        case 'm':
            return new Vector2f(0.75f,  0.25f);
        case 'N':
        case 'n':
            return new Vector2f(0.875f, 0.25f);
        case 'O':
        case 'o':
            return new Vector2f(0f,     0.375f);
        case 'P':
        case 'p':
            return new Vector2f(0.125f, 0.375f);
        case 'Q':
        case 'q':
            return new Vector2f(0.25f,  0.375f);
        case 'R':
        case 'r':
            return new Vector2f(0.375f, 0.375f);
        case 'S':
        case 's':
            return new Vector2f(0.5f,   0.375f);
        case 'T':
        case 't':
            return new Vector2f(0.625f, 0.375f);
        case 'U':
        case 'u':
            return new Vector2f(0.75f,  0.375f);
        case 'V':
        case 'v':
            return new Vector2f(0.875f, 0.375f);
        case 'W':
        case 'w':
            return new Vector2f(0f,     0.5f);
        case 'X':
        case 'x':
            return new Vector2f(0.125f, 0.5f);
        case 'Y':
        case 'y':
            return new Vector2f(0.25f,  0.5f);
        case 'Z':
        case 'z':
            return new Vector2f(0.375f, 0.5f);
        case '~':
            return new Vector2f(0.5f,   0.5f);
        case '`':
            return new Vector2f(0.625f, 0.5f);
        case '!':
            return new Vector2f(0.75f,  0.5f);
        case '@':
            return new Vector2f(0.875f, 0.5f);
        case '#':
            return new Vector2f(0f,     0.625f);
        case '$':
            return new Vector2f(0.125f, 0.625f);
        case '%':
            return new Vector2f(0.25f,  0.625f);
        case '^':
            return new Vector2f(0.375f, 0.625f);
        case '&':
            return new Vector2f(0.5f,   0.625f);
        case '*':
            return new Vector2f(0.625f, 0.625f);
        case '(':
        case '{':
        case '[':
            return new Vector2f(0.75f,  0.625f);
        case ')':
        case '}':
        case ']':
            return new Vector2f(0.875f, 0.625f);
        case '-':
            return new Vector2f(0f,     0.75f);
        case '_':
            return new Vector2f(0.125f, 0.75f);
        case '=':
            return new Vector2f(0.25f,  0.75f);
        case '+':
            return new Vector2f(0.375f, 0.75f);
        case ',':
            return new Vector2f(0.5f,   0.75f);
        case '.':
            return new Vector2f(0.625f, 0.75f);
        case '<':
            return new Vector2f(0.75f,  0.75f);
        case '>':
            return new Vector2f(0.875f, 0.75f);
        case ':':
            return new Vector2f(0f,     0.875f);
        case ';':
            return new Vector2f(0.125f, 0.875f);
        case '\'':
            return new Vector2f(0.25f,  0.875f);
        case '\"':
            return new Vector2f(0.375f, 0.875f);
        case '?':
            return new Vector2f(0.5f,   0.875f);
        case '/':
            return new Vector2f(0.625f, 0.875f);
        case '\\':
            return new Vector2f(0.75f,  0.875f);
        default:
            return new Vector2f(0.875f, 0.875f);
        }
    }
}
