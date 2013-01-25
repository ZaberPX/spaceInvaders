package px.spaceInvaders.graphics;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Random;

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

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class SpriteMaster {
    
    // ++++ ++++ Data ++++ ++++
    
    //Game data
    public static Random random;
    private int waves;
    private int score;
    private TextureMaster textureMaster;
    private Player player;
    private Bunker[] bunkers;
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
    /**View transform: scales coordinates to match pixels, making the screen 1280x720 in
     * world coordinates, also moves the camera up 480 units and right 208 to put the 
     * world origin near the bottom left of the screen while stopping the Invaders from
     * going to close to the left or right edges as well as giving them 9 rows to 
     * descend before reaching the bottom to end the game, each row is 64 units apart
     * while also allowing invaders 960 units of lateral movement.*/
    private Matrix4f view;
    private int viewUniform;
    
    // ++++ ++++ Accessors ++++ ++++
    
    public int getWaves() {
        return waves;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getDepthUniform() {
        return depthUniform;
    }
    
    public int getModelUniform() {
        return modelUniform;
    }

    public Matrix4f getView() {
        return view;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Bunker[] getBunkers() {
    	return bunkers;
    }
    
    public LinkedList<Enemy> getEnemies() {
    	return enemies;
    }
    
    public LinkedList<Projectile> getProjectiles() {
    	return projectiles;
    }
    
    public LinkedList<Effect> getEffects() {
        return effects;
    }
    
    public int loadTexture(GLAutoDrawable drawable, String filename) {
        return textureMaster.loadTexturePng(drawable, filename);
    }
    
    // ++++ ++++ Initialization ++++ ++++
    
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
        gl.glVertexAttribPointer(positionAttribute, 3, GL4.GL_FLOAT, false, 4 * 4, 0);
        
        int texcoordAttribute = gl.glGetAttribLocation(shaderProgram, "texcoord");
        gl.glEnableVertexAttribArray(texcoordAttribute);
        gl.glVertexAttribPointer(texcoordAttribute, 2, GL4.GL_FLOAT, false, 4 * 4, 2 * 4);
        
        //Unbind Everything
        gl.glBindVertexArray(0);
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
        gl.glUseProgram(0);
        
        init(drawable);
    }
    
    // ++++ ++++ Game Control ++++ ++++
    
    public void init(GLAutoDrawable drawable) {
        
        score = 0;
        waves = 0;
        player = new Player(drawable, this);
        bunkers = new Bunker[4];
        //TODO init bunkers 0-3
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
    
    public void spawnWave(GLAutoDrawable drawable) {
        for (int i = 0; i < 11; i++) {
            int result = random.nextInt(4);
            switch (result) {
            case 0:
                enemies.add(new Enemy(drawable, this, "res/textures/EnemyBomber.png", 
                        new Vector2f(32f + 64f * i, 512f), new Vector2f(50f, 50f), 
                        new Vector2f(64f, 64f), 0.5f, 100 + waves));
                break;
            case 1:
                enemies.add(new Enemy(drawable, this, "res/textures/EnemyFighter.png", 
                        new Vector2f(32f + 64f * i, 512f), new Vector2f(50f, 50f), 
                        new Vector2f(64f, 64f), 0.5f, 100 + waves));
                break;
            case 2:
                enemies.add(new Enemy(drawable, this, "res/textures/EnemyGunship.png", 
                        new Vector2f(32f + 64f * i, 512f), new Vector2f(50f, 50f), 
                        new Vector2f(64f, 64f), 0.5f, 100 + waves));
                break;
            }
        }
        waves++;
        score++;
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
    
    // ++++ ++++ Rendering ++++ ++++
    
    public void draw(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        
        gl.glBindVertexArray(vao);
        gl.glUseProgram(shaderProgram);

//        for (Bunker b : bunkers) {
//            b.draw(drawable);
//        }
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
    }
    
    public void drawQuad(GLAutoDrawable drawable, int texture, float depth, 
            Vector2f size, Vector2f location) {
        GL4 gl = drawable.getGL().getGL4();
        
        gl.glBindTexture(GL4.GL_TEXTURE_2D, texture);
        gl.glUniform1f(depthUniform, depth);
        
        //Setup Model matrix
        //Scale
        Matrix4f scaleMatrix = Matrix4f.scale(new Vector3f(size.x, size.y, 1.0f), 
                new Matrix4f(), null);
        Matrix4f model = Matrix4f.mul(scaleMatrix, new Matrix4f(), null);
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
    
    public void update(GLAutoDrawable drawable, long elapsedTime) {
        //TODO: Store static reference to GLAutoDrawable, updated every update loop?
        
        enemiesDisposal = new LinkedList<Enemy>();
        effectsDisposal = new LinkedList<Effect>();
        projectilesDisposal = new LinkedList<Projectile>();
        
        //Update tracker
        float movement = trackerDirection * (Enemy.BASE_ACCEL + waves / 10f);
        trackerDisplacement += movement;
        if (Math.abs(trackerDisplacement) > Enemy.BASE_SPEED + waves) {
            trackerDisplacement = Math.signum(trackerDisplacement) 
                    * (Enemy.BASE_SPEED + waves);
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
        //TODO Commented out until we actually have bunkers.
//        for (Bunker b : bunkers) {
//            b.update(drawable, elapsedTime);
//        }
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
        for (Effect e : effects) {
            e.update(drawable, elapsedTime);
            //Check for death based on lifetime.
        }
        
        //Dispose of dead objects
        for (Enemy e : enemiesDisposal) {
            enemies.remove(e);
            score++;
        }
        for (Effect e : effectsDisposal) {
            effects.remove(e);
        }
        for (Projectile p : projectilesDisposal) {
            projectiles.remove(p);
        }
    }
}
