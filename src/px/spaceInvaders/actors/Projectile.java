package px.spaceInvaders.actors;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.graphics.Sprite;
import px.spaceInvaders.graphics.SpriteMaster;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class Projectile extends Sprite implements Collideable {
    
    // ++++ ++++ Data ++++ ++++
    
    private Sprite parent;
    private Vector2f direction;
    private float speed;
    protected int lifetime;
    
    // ++++ ++++ Initialization ++++ ++++

    public Projectile(GLAutoDrawable drawable, SpriteMaster master, Sprite parent, 
            Vector2f location, String texture, Vector2f scale, float depth, float speed,
            Vector2f direction, Vector2f offset, int lifetime) {
        super(drawable, master, texture, scale, depth);
        this.parent = parent;
        this.location = location;
        this.direction = direction.normalise(null);
        this.speed = speed;
        this.offset = offset;
        this.lifetime = lifetime;
    }
    
    // ++++ ++++ Accessors ++++ ++++

    @Override
    public Vector2f getScale() {
        return scale;
    }

    @Override
    public Vector2f getLocation() {
        return location;
    }
    
    /**@return Number of milliseconds before this object is to be disposed. */
    public int getLifetime() {
        return lifetime;
    }
    
    // ++++ ++++ Game Logic ++++ ++++
    
    @Override
    public void update(GLAutoDrawable drawable, long elapsedTime) {
        
        if (speed > 0) {
            displacement = (Vector2f) new Vector2f(direction).scale(speed);
        }
    	checkCollision();
    	
    	lifetime -= elapsedTime;
    	
        super.update(drawable, elapsedTime);
    }
    
    public void checkCollision() {
        if (parent instanceof Player) {
        	//TODO check against enemies and their projectiles
        } else if (parent instanceof Enemy) {
        	//TODO check against the player and the bunkers
        }
    }

    /**Performs a simple rectangle overlap check to determine if two collideable objects
     * intersect with each other.
     * @param other Object being compared with.
     * @return True if this object is currently intersecting the other object. */
    @Override
    public boolean collidesWith(Collideable other) {
    	Vector2f thisTopLeft = Vector2f.sub(location, 
    	        (Vector2f) new Vector2f(scale).scale(0.5f), null);
    	Vector2f thisBottomRight = Vector2f.add(location, 
    	        (Vector2f) new Vector2f(scale).scale(0.5f), null);
    	Vector2f otherTopLeft = Vector2f.sub(other.getLocation(), 
    	        (Vector2f) new Vector2f(other.getScale()).scale(0.5f), null);
    	Vector2f otherBottomRight = Vector2f.add(other.getLocation(), 
    			(Vector2f) new Vector2f(other.getScale()).scale(0.5f), null);
    	
    	//Axis-aligned rectnagle test URL:
    	//http://leetcode.com/2011/05/determine-if-two-rectangles-overlap.html
    	if (!(thisBottomRight.y < otherTopLeft.y || thisTopLeft.y > otherBottomRight.y || 
    	        thisBottomRight.x < otherTopLeft.x || thisTopLeft.x > otherBottomRight.x)) {
    	    return true;
    	} else {
    	    return false; 
    	}
    }
    
    /**TODO: removes this projectile and spawns an explosion. */
    public void detonate() {
        
    }
}
