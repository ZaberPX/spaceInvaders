package px.spaceInvaders.graphics;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import px.util.input.InputMaster;

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
        
        //Init Splash Screen
        splash = new SplashScreen(drawable);
        //Init SpriteMaster Object
        spriteMaster = new SpriteMaster(drawable);
        
        //OpenGL Configuration
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        gl.glEnable(GL4.GL_DEPTH_TEST);
        gl.glDepthFunc(GL4.GL_LEQUAL);
        
        gl.glEnable(GL4.GL_BLEND);
        gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
        
        gl.glEnable(GL4.GL_CULL_FACE);
        gl.glCullFace(GL4.GL_BACK);
        
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
        
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
        
        splash.draw(drawable);
        
        spriteMaster.draw(drawable);
        
        long time = System.currentTimeMillis();
        long elapsedTime = time - lastTime;
        
        spriteMaster.update(drawable, elapsedTime);
        InputMaster.getInstance().update();
        
        lastTime = time;
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height) {
        //TODO Reshape Projection matrix for resizing screen.
        //Then again: I haven't set up for resizing this time around, may not need proj
    }
}
