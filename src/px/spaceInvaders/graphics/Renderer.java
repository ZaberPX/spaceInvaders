package px.spaceInvaders.graphics;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import com.jogamp.newt.event.KeyEvent;

import px.spaceInvaders.actors.Effect;
import px.spaceInvaders.core.GameCore;
import px.spaceInvaders.core.GameCore.Mode;
import px.spaceInvaders.core.InputMaster;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class Renderer implements GLEventListener {
    
    // ++++ ++++ Data ++++ ++++
    
    private SplashScreen splash;
    private SpriteMaster spriteMaster;
    private long lastTime;
    
    // ++++ ++++ Accessors ++++ ++++
    
    public SpriteMaster getSpriteMaster() {
        return spriteMaster;
    }
    
    // ++++ ++++ OpenGL Core Functions ++++ ++++
    
    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        
        //OpenGL Configuration
        gl.glClearColor(0.0f, 0.0f, 0.1f, 0.0f);
        
        gl.glEnable(GL4.GL_DEPTH_TEST);
        gl.glDepthFunc(GL4.GL_LEQUAL);
        
        gl.glEnable(GL4.GL_BLEND);
        gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
        
        gl.glEnable(GL4.GL_CULL_FACE);
        gl.glCullFace(GL4.GL_BACK);
        
        //Init Splash Screen
        splash = new SplashScreen(drawable);
        //Init SpriteMaster (Game State Object)
        spriteMaster = new SpriteMaster(drawable);
        
        //Final Setup
        lastTime = System.currentTimeMillis();
    }
    
    @Override
    public void dispose(GLAutoDrawable drawable) {
        splash.dispose(drawable);
    	spriteMaster.dispose(drawable);
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        InputMaster in = InputMaster.getInstance();
        GameCore.Mode mode = GameCore.instance.getMode();
        
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
        
        splash.draw(drawable);
        
        long time = System.currentTimeMillis();
        long elapsedTime = time - lastTime;
        
        //In case your system doesn't have Alt-F4 bound to exit
        if (in.isKeyUp(KeyEvent.VK_F4) && in.isKeyDown(KeyEvent.VK_ALT)) {
            System.exit(0);
        }
        
        //Update Game State
        if (mode != GameCore.Mode.MAIN_MENU) {
            spriteMaster.draw(drawable);
            spriteMaster.update(drawable, elapsedTime);
            
            if (mode == Mode.IN_GAME && spriteMaster.getPlayer().getHealth() < 1) {
                GameCore.instance.setMode(Mode.GAME_OVER);
                //Spawn explosion
                Animation a = new Animation(drawable, spriteMaster, new String[] {
                        "res/textures/effects/Explosion0.png",
                        "res/textures/effects/Explosion1.png",
                        "res/textures/effects/Explosion2.png",
                        "res/textures/effects/Explosion3.png",
                        "res/textures/effects/Explosion4.png"
                }, 150);
                Effect ef = new Effect(drawable, spriteMaster, a, 
                        spriteMaster.getPlayer().getLocation(), 
                        new Vector2f(128f, 128f), 0.65f);
                spriteMaster.getEffects().add(ef);
            }
        }
        
        //All this really shouldn't be hard-coded, oh well.
        if (mode == Mode.MAIN_MENU) {
            if (in.isKeyUp(KeyEvent.VK_ENTER)) {
                spriteMaster.init(drawable);
                GameCore.instance.setMode(Mode.IN_GAME);
            }
            spriteMaster.textRenderer.drawString(drawable, "INVADERS FROM SPACE!", 
                    new Vector2f(480f, 340f), 0.9f, TextRenderer.Align.CENTER, 
                    new Vector2f(32f, 64f), new Vector4f(0.6f, 1.0f, 1.0f, 1.0f));
            spriteMaster.textRenderer.drawString(drawable, 
                    "A video game by Michael Stopa, written in Java and OpenGL 4", 
                    new Vector2f(480f, 240f), 0.9f, TextRenderer.Align.CENTER, 
                    new Vector2f(8f, 16f), new Vector4f(0.6f, 1.0f, 1.0f, 1.0f));
            spriteMaster.textRenderer.drawString(drawable, 
                    "Uses lwjgl-util and TWL's PNGDecoder Libraries, with JOGL " +
                    "bindings from Jogamp", 
                    new Vector2f(480f, 220f), 0.9f, TextRenderer.Align.CENTER, 
                    new Vector2f(8f, 16f), new Vector4f(0.6f, 1.0f, 1.0f, 1.0f));
            spriteMaster.textRenderer.drawString(drawable, 
                    "Press <ENTER> to start a new game", 
                    new Vector2f(0f, 120f), 0.9f, TextRenderer.Align.LEFT, 
                    new Vector2f(12f, 24f), new Vector4f(0.6f, 1.0f, 0.7f, 1.0f));
            drawControls(drawable);
        } else if (mode == Mode.PAUSED) {
            if (in.isKeyUp(KeyEvent.VK_ENTER)) {
                spriteMaster.init(drawable);
                GameCore.instance.setMode(Mode.IN_GAME);
            }
            if (in.isKeyUp(KeyEvent.VK_ESCAPE)) {
                GameCore.instance.setMode(Mode.IN_GAME);
            }
            spriteMaster.textRenderer.drawString(drawable, "INVADERS FROM SPACE!", 
                    new Vector2f(480f, 440f), 0.9f, TextRenderer.Align.CENTER, 
                    new Vector2f(16f, 32f), new Vector4f(0.6f, 1.0f, 1.0f, 1.0f));
            spriteMaster.textRenderer.drawString(drawable, 
                    "Press <ESCAPE> to resume the current game", 
                    new Vector2f(0f, 120f), 0.9f, TextRenderer.Align.LEFT, 
                    new Vector2f(12f, 24f), new Vector4f(0.6f, 1.0f, 0.7f, 1.0f));
            spriteMaster.textRenderer.drawString(drawable, 
                    "Press <ENTER> to start a new game", 
                    new Vector2f(0f, 90f), 0.9f, TextRenderer.Align.LEFT, 
                    new Vector2f(12f, 24f), new Vector4f(0.6f, 1.0f, 0.7f, 1.0f));
            drawControls(drawable);
        } else if (mode == Mode.GAME_OVER) {
            if (in.isKeyUp(KeyEvent.VK_ENTER)) {
                spriteMaster.init(drawable);
                GameCore.instance.setMode(Mode.IN_GAME);
            }
            if (in.isKeyUp(KeyEvent.VK_ESCAPE)) {
                GameCore.instance.setMode(Mode.MAIN_MENU);
            }
            spriteMaster.textRenderer.drawString(drawable, "INVADERS FROM SPACE!", 
                    new Vector2f(480f, 440f), 0.9f, TextRenderer.Align.CENTER, 
                    new Vector2f(16f, 32f), new Vector4f(0.6f, 1.0f, 1.0f, 1.0f));
            spriteMaster.textRenderer.drawString(drawable, 
                    "Final Score: " + spriteMaster.getScore(), 
                    new Vector2f(480f, 240f), 0.9f, TextRenderer.Align.CENTER, 
                    new Vector2f(32f, 64f), new Vector4f(0.6f, 1.0f, 1.0f, 1.0f));
            spriteMaster.textRenderer.drawString(drawable, 
                    "Press <ESCAPE> to return to the main menu.", 
                    new Vector2f(480f, 30f), 0.9f, TextRenderer.Align.CENTER, 
                    new Vector2f(12f, 24f), new Vector4f(0.6f, 1.0f, 0.7f, 1.0f));
            spriteMaster.textRenderer.drawString(drawable, 
                    "Press <ENTER> to start a new game", 
                    new Vector2f(480f, 0f), 0.9f, TextRenderer.Align.CENTER, 
                    new Vector2f(12f, 24f), new Vector4f(0.6f, 1.0f, 0.7f, 1.0f));
            spriteMaster.textRenderer.drawString(drawable, 
                    "Press <Alt + F4>, or close the window to quit", 
                    new Vector2f(480f, -30f), 0.9f, TextRenderer.Align.CENTER, 
                    new Vector2f(12f, 24f), new Vector4f(0.6f, 1.0f, 0.7f, 1.0f));
        } else if (mode == Mode.IN_GAME) {
            if (in.isKeyUp(KeyEvent.VK_ESCAPE)) {
                GameCore.instance.setMode(Mode.PAUSED);
            }
        }
        
        in.update();
        
        lastTime = time;
    }
    
    private void drawControls(GLAutoDrawable drawable) {
        spriteMaster.textRenderer.drawString(drawable, "CONTROLS:", 
                new Vector2f(580f, 150f), 0.9f, TextRenderer.Align.LEFT, 
                new Vector2f(12f, 24f), new Vector4f(0.6f, 1.0f, 0.7f, 1.0f));
        spriteMaster.textRenderer.drawString(drawable, 
                "Use <A> and <D> or Left & Right arrow keys to move", 
                new Vector2f(580f, 120f), 0.9f, TextRenderer.Align.LEFT, 
                new Vector2f(8f, 16f), new Vector4f(0.6f, 1.0f, 0.7f, 1.0f));
        spriteMaster.textRenderer.drawString(drawable, 
                "Hold down <Space> to open fire", 
                new Vector2f(580f, 100f), 0.9f, TextRenderer.Align.LEFT, 
                new Vector2f(8f, 16f), new Vector4f(0.6f, 1.0f, 0.7f, 1.0f));
        spriteMaster.textRenderer.drawString(drawable, 
                "Press <Escape> to Pause", 
                new Vector2f(580f, 80f), 0.9f, TextRenderer.Align.LEFT, 
                new Vector2f(8f, 16f), new Vector4f(0.6f, 1.0f, 0.7f, 1.0f));
        spriteMaster.textRenderer.drawString(drawable, 
                "Press <Alt + F4>, or close the window at any time to quit", 
                new Vector2f(580f, 60f), 0.9f, TextRenderer.Align.LEFT, 
                new Vector2f(8f, 16f), new Vector4f(0.6f, 1.0f, 0.7f, 1.0f));
        spriteMaster.textRenderer.drawString(drawable, 
                "PS. Don't let the invaders land!", 
                new Vector2f(580f, 30f), 0.9f, TextRenderer.Align.LEFT, 
                new Vector2f(8f, 16f), new Vector4f(0.6f, 1.0f, 0.7f, 1.0f));
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height) {
        //As the game is locked to 720p, I don't really need to worry about this.
    }
}
