package px.spaceInvaders.actors;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.graphics.Sprite;
import px.spaceInvaders.graphics.SpriteMaster;

/**Class that encapsulates all code for player and AI driven characters in the game.
 * @author Michael Stopa */
public class Pawn extends Sprite implements Collideable {
    
    // ++++ ++++ Data ++++ ++++
    
    protected Vector2f movement = new Vector2f(0f, 0f);
    protected float speed = 500;
    
    // ++++ ++++ Initialization ++++ ++++

    public Pawn(GLAutoDrawable drawable, SpriteMaster master, String texture,
            Vector2f scale, float depth) {
        super(drawable, master, texture, scale, depth);
    }
    
    // ++++ ++++ Game Logic ++++ ++++
    
    @Override
    public void update(long elapsedTime) {
        
        super.update(elapsedTime);
        
        if (location.x < 0f) {
            location.x = 0f;
        } else if (location.x > 960f) {
            location.x = 960f;
        }
        
        //Slow down the Sprite
        //TODO base slowing on length instead of individual axes.
        float slow = (float)elapsedTime;
        if (Math.abs(displacement.x) < slow) {
            displacement.x = 0f;
        } else if (displacement.x > 0f) {
            displacement.x -= slow;
        } else {
            displacement.x += slow;
        }
        if (Math.abs(displacement.y) < slow) {
            displacement.y = 0f;
        } else if (displacement.y > 0f) {
            displacement.y -= slow;
        } else {
            displacement.y += slow;
        }
    }

    @Override
    public boolean collidesWith(Collideable other) {
        // TODO Auto-generated method stub
        return false;
    }

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
}
