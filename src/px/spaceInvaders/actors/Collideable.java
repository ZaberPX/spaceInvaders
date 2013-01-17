package px.spaceInvaders.actors;

import org.lwjgl.util.vector.Vector2f;

/**Interface for enabling circular collision detection between actors in the game.
 * @author Michael Stopa */
public interface Collideable {
    
    public boolean collidesWith(Collideable other);
    
    public Vector2f getCollisionSize();
    public Vector2f getLocation();
}
