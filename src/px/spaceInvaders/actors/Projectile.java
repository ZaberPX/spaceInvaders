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
    private Vector2f movement;
    
    private Vector2f offset;
    
    // ++++ ++++ Initialization ++++ ++++

    public Projectile(GLAutoDrawable drawable, SpriteMaster master,
            String texture, Vector2f scale, float depth, Sprite parent, float speed,
            Vector2f direction, Vector2f offset) {
        super(drawable, master, texture, scale, depth);
        this.parent = parent;
        movement = (Vector2f) direction.normalise(null).scale(speed);
        this.offset = offset;
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
    
    // ++++ ++++ Game Logic ++++ ++++
    
    @Override
    public void update(long elapsedTime) {
    	checkCollision();
    	
        super.update(elapsedTime);
    }
    
    public void checkCollision() {
        if (parent instanceof Player) {
        	//TODO check against enemies and their projectiles
        } else if (parent instanceof Enemy) {
        	//TODO check against the player and the bunkers
        }
    }

    @Override
    public boolean collidesWith(Collideable other) {
    	Vector2f thisTopLeft = Vector2f.sub(location, scale, null);
    	Vector2f thisBottomRight = Vector2f.add(location, scale, null);
    	Vector2f otherTopLeft = Vector2f.sub(other.getLocation(), other.getScale(), null);
    	Vector2f otherBottomRight = Vector2f.add(other.getLocation(), 
    			other.getScale(), null);
    	
        return false;
    }
    
    // ++++ ++++ Rendering ++++ ++++
    
    @Override
    public void draw(GLAutoDrawable drawable) {
    	
    }
}
