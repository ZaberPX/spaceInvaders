package px.spaceInvaders.actors;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.graphics.Sprite;
import px.spaceInvaders.graphics.SpriteMaster;

/**Class that encapsulates all code for player and AI driven characters in the game.
 * @author Michael Stopa */
public class Pawn extends Sprite implements Collideable {
    
    // ++++ ++++ Initialization ++++ ++++

    public Pawn(GLAutoDrawable drawable, SpriteMaster master, String texture,
            Vector2f scale, float depth) {
        super(drawable, master, texture, scale, depth);
    }
    
    // ++++ ++++ Game Logic ++++ ++++
    
    @Override
    public void update(GLAutoDrawable drawable, long elapsedTime) {
        
        super.update(drawable, elapsedTime);
        
        if (location.x < 0f) {
            location.x = 0f;
            displacement.x = 0f;
        } else if (location.x > 960f) {
            location.x = 960f;
            displacement.x = 0f;
        }
    }

    @Override
    public boolean collidesWith(Collideable other) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Vector2f getScale() {
        return scale;
    }

    @Override
    public Vector2f getLocation() {
        return location;
    }
}
