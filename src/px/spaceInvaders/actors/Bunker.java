package px.spaceInvaders.actors;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.graphics.Sprite;
import px.spaceInvaders.graphics.SpriteMaster;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class Bunker extends Sprite implements Collideable {
    
    public Bunker(GLAutoDrawable drawable, SpriteMaster master, String texture,
            Vector2f scale, float depth) {
        super(drawable, master, texture, scale, depth);
        // TODO Auto-generated constructor stub
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
