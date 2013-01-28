package px.spaceInvaders.graphics;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Random;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import px.spaceInvaders.actors.Effect;
import px.spaceInvaders.actors.Enemy;
import px.spaceInvaders.actors.Player;
import px.spaceInvaders.actors.Projectile;
import px.spaceInvaders.core.GameCore;
import px.spaceInvaders.core.GameCore.Mode;

/**Master Game-State object holds all actors and manages their rendering and updating.
 * @author Michael Stopa */
public class SpriteMaster {
    
    // ++++ ++++ Data ++++ ++++
    
    //Game data
    public static Random random;
    public TextRenderer textRenderer;
    private int wave;
    private int score;
    private TextureMaster textureMaster;
    private Player player;
    private LinkedList<Enemy> enemies;
    private LinkedList<Enemy> enemiesDisposal;
    private LinkedList<Effect> effects;
    private LinkedList<Effect> effectsDisposal;
    private LinkedList<Projectile> projectiles;
    private LinkedList<Projectile> projectilesDisposal;
    
    /**The tracker is an invisisible entity that is supposed to travel in time with the
     * invaders to determine when a new== wave should spawn, it does so by simulating the
     * time it would take for a spawned invader to change direction form hitting the
     * edge of its column. */
    private float trackerPosition;
    private float trackerDisplacement;
    private float trackerDirection;
    private float trackerColumnWidth;
    
    //OpenGL variables
    private int shaderProgram;
    private int vao;
    private int vbo;
    private int depthUniform;
    private int modelUniform;
    private int tintUniform;
    /**View transform: scales coordinates to match pixels, making the screen 1280x720 in
     * world coordinates, also moves the camera up 480 units and right 208 to put the 
     * world origin near the bottom left of the screen while stopping the Invaders from
     * going to close to the left or right edges as well as giving them 9 rows to 
     * descend before reaching the bottom to end the game, each row is 64 units apart
     * while also allowing invaders 960 units of lateral movement.*/
    private Matrix4f view;
    private int viewUniform;
    
    // ++++ ++++ Initialization ++++ ++++
    
    /**Creates a new SpriteMaster object.
     * @param drawable Current OpenGL context. */
    public SpriteMaster(GLAutoDrawable drawable) {
        if (random == null) {
            random = new Random();
        }
        
        // OpenGL Initialization
        
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
        tintUniform = gl.glGetUniformLocation(shaderProgram, "tint");

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
                -0.5f, 0.5f, 0.0f, 0.0f, //Top Left
                -0.5f,-0.5f, 0.0f, 1.0f, //Bottom Left
                 0.5f, 0.5f, 1.0f, 0.0f, //Top Right
                 0.5f,-0.5f, 1.0f, 1.0f //Bottom Right
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
        
        //Setup TextRenderer
        textRenderer = new TextRenderer(drawable, this, "res/text/Text.png");
    }
    
    // ++++ ++++ Game Control ++++ ++++
    
    /**Initializes the game-state for a new game.
     * @param drawable Current OpenGL context. */
    public void init(GLAutoDrawable drawable) {
        
        score = 0;
        wave = 0;
        player = new Player(drawable, this);
        enemies = new LinkedList<Enemy>();
        effects = new LinkedList<Effect>();
        projectiles = new LinkedList<Projectile>();
        
        //Tracker initiation
        trackerColumnWidth = (float) Math.sqrt(Math.pow(Enemy.COLUMN_WIDTH, 2) 
                + Math.pow(Enemy.COLUMN_WIDTH * 0.25f, 2));
        trackerPosition = 0f;
        trackerDirection = 1f;
        trackerDisplacement = 0f;

        spawnWave(drawable);
    }
    
    /**Spawns a new wave of up to 11 Invaders at the top of the screen.
     * @param drawable Current OpenGl context */
    protected void spawnWave(GLAutoDrawable drawable) {
        for (int i = 0; i < 11; i++) {
            int result = random.nextInt(4);
            switch (result) {
            case 0:
                enemies.add(new Enemy(drawable, this, "res/textures/EnemyBomber.png", 
                        new Vector2f(32f + 64f * i, 512f), new Vector2f(50f, 20f), 
                        new Vector2f(64f, 64f), 0.5f, 100 + wave));
                break;
            case 1:
                enemies.add(new Enemy(drawable, this, "res/textures/EnemyFighter.png", 
                        new Vector2f(32f + 64f * i, 512f), new Vector2f(50f, 10f), 
                        new Vector2f(64f, 64f), 0.5f, 100 + wave));
                break;
            case 2:
                enemies.add(new Enemy(drawable, this, "res/textures/EnemyGunship.png", 
                        new Vector2f(32f + 64f * i, 512f), new Vector2f(50f, 10f), 
                        new Vector2f(64f, 64f), 0.5f, 100 + wave));
                break;
            }
        }
        wave++;
    }
    
    // ++++ ++++ Disposal ++++ ++++
    
    /**Cleans up all non-garbage-collected resources.
     * @param drawable Current OpenGL context. */
    public void dispose(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        int[] temp = new int[1];
        
        textureMaster.flush(drawable);
        gl.glDeleteProgram(shaderProgram);
        gl.glDeleteVertexArrays(1, temp, 0);
        gl.glDeleteBuffers(1, temp, 0);
    }
    
    // ++++ ++++ Rendering ++++ ++++
    
    /**Draws all objects administered by this object.
     * @param drawable Current OpenGL context. */
    public void draw(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        
        gl.glBindVertexArray(vao);
        gl.glUseProgram(shaderProgram);
        gl.glUniform4f(tintUniform, 1f, 1f, 1f, 1f);

        for (Enemy e : enemies) {
            e.draw(drawable);
        }
        player.draw(drawable);
        for (Projectile p : projectiles) {
            p.draw(drawable);
        }
        for (Effect e : effects) {
            e.draw(drawable);
        }
        
        /*/Debugging code, draws the tracker.
        drawQuad(drawable, player.getTexture(), 0.9f, new Vector2f(16, 16), 
                new Vector2f(trackerPosition, 0f));//*/
        
        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
        
        textRenderer.drawString(drawable, "Score: " + score, new Vector2f(-120f, -128f), 
                0.9f, TextRenderer.Align.LEFT, new Vector2f(8f, 16f), 
                new Vector4f(0.6f, 1.0f, 0.7f, 1.0f));
        textRenderer.drawString(drawable, "Health: " + player.getHealth(), 
                new Vector2f(435f, -128f), 0.9f, TextRenderer.Align.LEFT, 
                new Vector2f(8f, 16f), new Vector4f(0.6f, 1.0f, 0.7f, 1.0f));
        textRenderer.drawString(drawable, "Wave: " + wave, new Vector2f(1010f, -128f), 
                0.9f, TextRenderer.Align.LEFT, new Vector2f(8f, 16f), 
                new Vector4f(0.6f, 1.0f, 0.7f, 1.0f));
    }
    
    /**Allows drawing of a simple textured quad to the screen.
     * @param drawable Current OpenGl context
     * @param texture OpenGL reference to the texture used on the quad.
     * @param depth Z-depth to  draw at (RHS 1.0 to -1.0)
     * @param size Size of quad to draw, in world coordinates.
     * @param location Location in world coordiantes to draw to. */
    public void drawQuad(GLAutoDrawable drawable, int texture, float depth, 
            Vector2f size, Vector2f location) {
        GL4 gl = drawable.getGL().getGL4();
        
        gl.glBindTexture(GL4.GL_TEXTURE_2D, texture);
        gl.glUniform1f(depthUniform, depth);
        
        //Setup Model matrix
        //Scale
        Matrix4f model = Matrix4f.scale(new Vector3f(size.x, size.y, 1.0f), 
                new Matrix4f(), null);
        //Location
        Matrix4f loc = Matrix4f.translate(location, new Matrix4f(), null);
        model = Matrix4f.mul(loc, model, null);
        
        FloatBuffer buffer = FloatBuffer.allocate(16);
        model.store(buffer);
        buffer.rewind();
        gl.glUniformMatrix4fv(modelUniform, 1, false, buffer);
        gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, 4);
    }
    
    // ++++ ++++ Game Logic ++++ ++++
    
    /**Updates the state of all objects in the game.
     * @param drawable Current OpenGL context
     * @param elapsedTime Number of milliseconds since last update loop. */
    public void update(GLAutoDrawable drawable, long elapsedTime) {
        effectsDisposal = new LinkedList<Effect>();
        
        if (GameCore.instance.getMode() == Mode.IN_GAME) {
            enemiesDisposal = new LinkedList<Enemy>();
            projectilesDisposal = new LinkedList<Projectile>();
            
            //Update tracker
            float movement = trackerDirection * (Enemy.BASE_ACCEL + wave / 10f);
            trackerDisplacement += movement;
            if (Math.abs(trackerDisplacement) > Enemy.BASE_SPEED + wave) {
                trackerDisplacement = Math.signum(trackerDisplacement) 
                        * (Enemy.BASE_SPEED + wave);
            }
            trackerPosition += trackerDisplacement * (elapsedTime / 1000f);
            if (trackerPosition < 0 && trackerDirection != 1f) {
                trackerDirection = 1f;
                spawnWave(drawable);
            }
            if (trackerPosition > trackerColumnWidth && trackerDirection != -1f) {
                trackerDirection = -1f;
                spawnWave(drawable);
            }
            
            //Update actors
            for (Enemy e : enemies) {
                e.update(drawable, elapsedTime);
                if (e.getHealth() <= 0) {
                    enemiesDisposal.add(e);
                }
            }
            player.update(drawable, elapsedTime);
            for (Projectile p : projectiles) {
                p.update(drawable, elapsedTime);
                if (p.getLifetime() <= 0) {
                    projectilesDisposal.add(p);
                }
                
            }
            
            //Dispose of dead objects
            for (Enemy e : enemiesDisposal) {
                enemies.remove(e);
            }
            for (Projectile p : projectilesDisposal) {
                projectiles.remove(p);
            }
        }
        
        for (Effect e : effects) {
            e.update(drawable, elapsedTime);
            if (e.isDead()) {
                effectsDisposal.add(e);
            }
        }
        for (Effect e : effectsDisposal) {
            effects.remove(e);
        }
    }
    
    // ++++ ++++ Accessors ++++ ++++
    
    /**@return Number of enemy waves spawned since the game started. */
    public int getWave() {
        return wave;
    }
    
    /**@return Number of invaders destroyed by the player. */
    public int getScore() {
        return score;
    }
    
    /**@return OpenGl reference to the Depth uniform in the basic shader program. */
    public int getDepthUniform() {
        return depthUniform;
    }
    
    /**@return OpenGl reference to the Model uniform in the basic shader program. */
    public int getModelUniform() {
        return modelUniform;
    }
    
    /**@return OpenGl reference to the Depth uniform in the basic shader program. */
    public int getTintUniform() {
        return tintUniform;
    }

    /**@return The current view matirx transform. */
    public Matrix4f getView() {
        return view;
    }
    
    /**@return Current player object. */
    public Player getPlayer() {
        return player;
    }
    
    /**@return List of all Enemy objects in the game. */
    public LinkedList<Enemy> getEnemies() {
        return enemies;
    }
    
    /**@return List of all Projectile objects in the game. */
    public LinkedList<Projectile> getProjectiles() {
        return projectiles;
    }
    
    /**@return List of all Effect objects in the game. */
    public LinkedList<Effect> getEffects() {
        return effects;
    }
    
    /**@return Returns the OpenGL reference to the Vertex Array Object used to draw all
     * sprites in the game. */
    public int getVao() {
        return vao;
    }
    
    /**@return Returns the OpenGl reference to the Shader Program used to draw all sprites
     * in the game. */
    public int getShaderProgram() {
        return shaderProgram;
    }
    
    /**Handles loading of textures from PNG files through the SpriteMaster's 
     * TextureMaster.
     * @param drawable Current OpenGL context.
     * @param filename Filename of the PNG file to load.
     * @return OpenGL reference to the newly generated texture object. */
    public int loadTexture(GLAutoDrawable drawable, String filename) {
        return textureMaster.loadTexturePng(drawable, filename);
    }
    
    // ++++ ++++ Mutators ++++ ++++

    /**Allows adding and subtracting to the player's score during the game.
     * @param points Points to add or remove from the player's score. */
    public void addScore(int points) {
        score += points;
    }
}
