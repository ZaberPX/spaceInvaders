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
    public Vector2f getCollisionSize() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Vector2f getLocation() {
        // TODO Auto-generated method stub
        return null;
    }
    
    // ++++ ++++ Game Logic ++++ ++++

    @Override
    public boolean collidesWith(Collideable other) {
        // TODO Auto-generated method stub
        return false;
    }
    
    // ++++ ++++ Rendering ++++ ++++
    
    
}
