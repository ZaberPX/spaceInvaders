package px.spaceInvaders.actors;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.graphics.SpriteMaster;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class Enemy extends Pawn {

    public Enemy(GLAutoDrawable drawable, SpriteMaster master, String texture,
            Vector2f location, Vector2f size, float depth) {
        super(drawable, master, texture, location, size, depth);
        // TODO Auto-generated constructor stub
    }
    
}
