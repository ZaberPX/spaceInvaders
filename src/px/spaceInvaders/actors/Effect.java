package px.spaceInvaders.actors;

import java.util.LinkedList;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.graphics.Sprite;
import px.spaceInvaders.graphics.SpriteMaster;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class Effect extends Sprite {
	
	LinkedList<Sprite> victims = new LinkedList<Sprite>();

    public Effect(GLAutoDrawable drawable, SpriteMaster master, String texture,
            Vector2f location, Vector2f size, float depth) {
        super(drawable, master, texture, location, size, depth);
        // TODO Auto-generated constructor stub
    }
    
    public void update(GLAutoDrawable drawable, long elapsedTime) {
        super.update(drawable, elapsedTime);
    	//TODO apply damage to hit Collideables
    }
}
