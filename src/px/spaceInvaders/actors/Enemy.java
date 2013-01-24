package px.spaceInvaders.actors;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.graphics.SpriteMaster;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class Enemy extends Pawn {

    public Enemy(GLAutoDrawable drawable, SpriteMaster master, String texture,
            Vector2f location, Vector2f hitSize, Vector2f drawSize, float depth,
            int health) {
        super(drawable, master, texture, location, hitSize, drawSize, depth, health);
        // TODO Auto-generated constructor stub
    }
    
}
