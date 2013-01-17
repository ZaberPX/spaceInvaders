package px.spaceInvaders.actors;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.graphics.SpriteMaster;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class Enemy extends Pawn implements Collideable {

    public Enemy(GLAutoDrawable drawable, SpriteMaster master, String texture,
            Vector2f scale, float depth) {
        super(drawable, master, texture, scale, depth);
        // TODO Auto-generated constructor stub
    }
    
}
