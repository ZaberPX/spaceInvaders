package px.spaceInvaders.graphics;

import java.nio.FloatBuffer;
import java.util.LinkedList;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import px.spaceInvaders.actors.Bunker;
import px.spaceInvaders.actors.Effect;
import px.spaceInvaders.actors.Enemy;
import px.spaceInvaders.actors.Player;
import px.spaceInvaders.actors.Projectile;
import px.util.graphics.ShaderLoader;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class SpriteMaster {
    
    // ++++ ++++ Data ++++ ++++
    
    private TextureMaster textureMaster;
    private Player player;
    private Bunker[] bunkers;
    private Enemy[] enemies;
    private LinkedList<Effect> effects;
    private LinkedList<Projectile> projectiles;
    
    private int shaderProgram;
    
    private int vao;
    private int vbo;
    
    private int depthUniform;
    private int modelUniform;
    /**View transform: scales coordinates to match pixels, making the screen 1280x720 in
     * world coordinates, also moves the camera up 480 units and right 208 to put the 
     * world origin near the bottom left of the screen while stopping the Invaders from
     * going to close to the left or right edges as well as giving them 9 rows to 
     * descend before reaching the bottom to end the game, each row is 64 units apart
     * while also allowing invaders 960 units of lateral movement.*/
    private Matrix4f view;
    private int viewUniform;
    
    // ++++ ++++ Accessors ++++ ++++
    
    public int getDepthUniform() {
        return depthUniform;
    }
    
    public int getModelUniform() {
        return modelUniform;
    }
    
    public int loadTexture(GLAutoDrawable drawable, String filename) {
        return textureMaster.loadTexturePng(drawable, filename);
    }
    
    // ++++ ++++ Initialization ++++ ++++
    
    public SpriteMaster(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        int[] temp = new int[1];
        textureMaster = new TextureMaster();
        
        //Compile Shader Program
        shaderProgram = ShaderLoader.loadShaders(drawable, 
                "res/shaders/vertex.glsl", "res/shaders/fragment.glsl");
        gl.glUseProgram(shaderProgram);
        
        //Setup uniforms
        depthUniform = gl.glGetUniformLocation(shaderProgram, "depth");
        modelUniform = gl.glGetUniformLocation(shaderProgram, "model");
        viewUniform = gl.glGetUniformLocation(shaderProgram, "view");

        //Setup transforms (no Projection, this is 2D after all)
        Matrix4f scale = Matrix4f.scale(new Vector3f(1f/640f, 1f/360f, 1f), 
                new Matrix4f(), null);
        Matrix4f trans = Matrix4f.translate(new Vector3f(-480f, -208f, 0f), 
                new Matrix4f(), null);
        view = Matrix4f.mul(scale, trans, null);
        FloatBuffer viewBuffer = FloatBuffer.allocate(16);
        view.store(viewBuffer);
        viewBuffer.rewind();
        gl.glUniformMatrix4fv(viewUniform, 1, false, viewBuffer);
        
        //Setup VBO
        float[] vertices = {
                //Position         //Texcoord
                -1.0f, 1.0f, 0.0f, 0.0f, //Top Left
                -1.0f,-1.0f, 0.0f, 1.0f, //Bottom Left
                 1.0f, 1.0f, 1.0f, 0.0f, //Top Right
                 1.0f,-1.0f, 1.0f, 1.0f //Bottom Right
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
        gl.glVertexAttribPointer(positionAttribute, 3, GL4.GL_FLOAT, false, 4 * 4, 0);
        
        int texcoordAttribute = gl.glGetAttribLocation(shaderProgram, "texcoord");
        gl.glEnableVertexAttribArray(texcoordAttribute);
        gl.glVertexAttribPointer(texcoordAttribute, 2, GL4.GL_FLOAT, false, 4 * 4, 2 * 4);
        
        //TODO Remove test Sprite and Texture
        player = new Player(drawable, this, "res/textures/PlayerTank.png", 
                new Vector2f(32f, 32f), 0.5f);
        
        //Unbind Everything
        gl.glBindVertexArray(0);
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
        gl.glUseProgram(0);
    }
    
    // ++++ ++++ Disposal ++++ ++++
    
    public void dispose(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        int[] temp = new int[1];
        
        textureMaster.flush(drawable);
        gl.glDeleteProgram(shaderProgram);
        gl.glDeleteVertexArrays(1, temp, 0);
        gl.glDeleteBuffers(1, temp, 0);
    }
    
    // ++++ ++++ Game Logic ++++ ++++
    
    public void update(long elapsedTime) {
        //for (Bunker b : bunkers) {
            //b.update(elapsedTime);
        //}
        //for (Enemy e : enemies) {
            //e.update(elapsedTime);
        //}
        player.update(elapsedTime);
        //for (Projectile p : projectiles) {
            //p.update(elapsedTime);
        //}
        //for (Effect e : effects) {
            //e.update(elapsedTime);
        //}
    }
    
    // ++++ ++++ Rendering ++++ ++++
    
    public void draw(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        
        gl.glBindVertexArray(vao);
        gl.glUseProgram(shaderProgram);

        //for (Bunker b : bunkers) {
            //b.draw(drawable);
        //}
        //for (Enemy e : enemies) {
            //e.draw(drawable);
        //}
        player.draw(drawable);
        //for (Projectile p : projectiles) {
            //p.draw(drawable);
        //}
        //for (Effect e : effects) {
            //e.draw(drawable);
        //}
        
        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
    }
}
