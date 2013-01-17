package px.spaceInvaders.actors;

import java.awt.event.KeyEvent;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.graphics.SpriteMaster;
import px.util.input.InputMaster;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class Player extends Pawn implements Collideable {

    public Player(GLAutoDrawable drawable, SpriteMaster master, String texture,
            Vector2f scale, float depth) {
        super(drawable, master, texture, scale, depth);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void update(long elapsedTime) {
        InputMaster in = InputMaster.getInstance();
        
        if (in.isKeyDown(KeyEvent.VK_A) || in.isKeyDown(KeyEvent.VK_LEFT)) {
            movement = Vector2f.add(movement, new Vector2f(-1f, 0f), null);
        }
        if (in.isKeyDown(KeyEvent.VK_D) || in.isKeyDown(KeyEvent.VK_RIGHT)) {
            movement = Vector2f.add(movement, new Vector2f(1f, 0f), null);
        }
        
        if (movement.lengthSquared() > 0) {
            movement = movement.normalise(null);
            movement.scale(speed);
            displacement = Vector2f.add(movement, displacement, null);
            movement = new Vector2f(0f, 0f);
        }
        
        super.update(elapsedTime);
    }
}
