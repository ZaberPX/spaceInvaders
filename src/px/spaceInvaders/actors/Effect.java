package px.spaceInvaders.actors;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.graphics.Animation;
import px.spaceInvaders.graphics.Sprite;
import px.spaceInvaders.graphics.SpriteMaster;

/**An animated, cosmetic effect.
 * 
 * @author Michael Stopa */
public class Effect extends Sprite {
    
    // ++++ ++++ Data ++++ ++++
	
	private Animation anim;

	// ++++ ++++ Initialization ++++ ++++
	
	/**Creates a new Effect, remember to add to SpriteMaster afterwards
	 * @param drawable Current OpenGL context
	 * @param master SpriteMaster object for administering this object.
	 * @param anim Animation used to represent this effect on screen.
	 * @param location Location of the object in world-coordinates
	 * @param size Size of the effect in world-coordinates
	 * @param depth This object's depth in the z-buffer (Right Handed, 1.0 to 1.0) */
    public Effect(GLAutoDrawable drawable, SpriteMaster master, Animation anim, 
            Vector2f location, Vector2f size, float depth) {
        super(drawable, master, null, location, size, size, depth);
        this.anim = anim;
    }
    
    // ++++ ++++ Game Loop ++++ ++++
    
    /**Updates the animation used in this effect.
     * @param drawable Current OpenGL context
     * @param elapsedTime Number of milliseconds since last update loop. */
    @Override
    public void update(GLAutoDrawable drawable, long elapsedTime) {
        super.update(drawable, elapsedTime);
        anim.update(drawable, elapsedTime);
    }
    
    // ++++ ++++ Rendering ++++ ++++
    
    /**Draw this object to the screen.
     * @param drawable Current OpenGL context */
    @Override
    public void draw(GLAutoDrawable drawable) {
        texture = anim.getCurrentTexture();
        super.draw(drawable);
    }
    
    // ++++ ++++ Accessors ++++ ++++
    
    /**Determines if this object is ready for disposal.
     * @return True if this object is ready for disposal. */
    public boolean isDead() {
        return anim.isDead();
    }
}
