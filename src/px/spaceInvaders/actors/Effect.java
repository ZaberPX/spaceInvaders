package px.spaceInvaders.actors;

import java.util.LinkedList;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.graphics.Sprite;
import px.spaceInvaders.graphics.SpriteMaster;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class Effect extends Sprite {
	
	
	LinkedList<Collideable> victims = new LinkedList<Collideable>();

    public Effect(GLAutoDrawable drawable, SpriteMaster master, String texture,
            Vector2f scale, float depth) {
        super(drawable, master, texture, scale, depth);
        // TODO Auto-generated constructor stub
    }
    
    public void update(long elapsedTime) {
    	//TODO apply damage to hit Collideables
    }
}
