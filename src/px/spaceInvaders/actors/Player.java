package px.spaceInvaders.actors;

import java.awt.event.KeyEvent;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.core.InputMaster;
import px.spaceInvaders.graphics.SpriteMaster;

/**A playable character.
 * @author Michael Stopa */
public class Player extends Pawn {
    
    // ++++ ++++ Data ++++ ++++
    
    protected Vector2f movement = new Vector2f(0f, 0f);
    protected float accel = 50f;
    protected float speed = 500f;
    
    protected int gunCooldown = 0;
    protected int gunCooldownMax = 200;
    
    // ++++ ++++ Initialization ++++ ++++

    /**Creates a new player object, automatically starts at (480, -64), with a collision
     * size of (50, 50), and a size on screen of (64, 64), drawn at z-depth 0.5f and
     *  100 points of health. Uses res/textures/PlayerTank.png as it's texture.
     * @param drawable
     * @param master SpriteMaster object administering this object. */
    public Player(GLAutoDrawable drawable, SpriteMaster master) {
        super(drawable, master, "res/textures/PlayerTank.png", new Vector2f(480f, -64f), 
                new Vector2f(50f, 50f), new Vector2f(64f, 64f), 0.5f, 100);
    }
    
    // ++++ ++++ Game Logic ++++ ++++
    
    /**Processes input from the keyboard to move this object in addition to other
     * game-state updates.
     * @param drawable Current OpenGL context
     * @param elapsedTime Number of milliseconds since last update cycle. */
    @Override
    public void update(GLAutoDrawable drawable, long elapsedTime) {
        InputMaster in = InputMaster.getInstance();
        movement = new Vector2f(0f, 0f);
        
        if (gunCooldown > 0) {
            gunCooldown -= elapsedTime;
        }
        
        if (in.isKeyDown(KeyEvent.VK_A) || in.isKeyDown(KeyEvent.VK_LEFT)) {
            movement = Vector2f.add(movement, new Vector2f(-1f, 0f), null);
        }
        if (in.isKeyDown(KeyEvent.VK_D) || in.isKeyDown(KeyEvent.VK_RIGHT)) {
            movement = Vector2f.add(movement, new Vector2f(1f, 0f), null);
        }
        if (in.isKeyDown(KeyEvent.VK_SPACE)) {
            shoot(drawable);
        }
        
        if (movement.lengthSquared() > 0) {
            movement = movement.normalise(null);
            movement.scale(accel);
            displacement = Vector2f.add(movement, displacement, null);
            if (displacement.length() > speed) {
                displacement.normalise().scale(speed);
            }
        }
        
        super.update(drawable, elapsedTime);
        
        if (location.x < 0f) {
            location.x = 0f;
            displacement.x = 0f;
        } else if (location.x > 960f) {
            location.x = 960f;
            displacement.x = 0f;
        }
        
        //Slow down the Sprite
        float slow = (float)elapsedTime * 2f;
        if (Math.abs(displacement.x) < slow) {
            displacement.x = 0f;
        } else if (displacement.x > 0f) {
            displacement.x -= slow;
        } else {
            displacement.x += slow;
        }
    }
    
    /**Draws this object to the screen. Will not draw if health has dropped below 1.
     * @param drawable Current OpenGL context */
    @Override
    public void draw(GLAutoDrawable drawable) {
        if (health > 0) {
            super.draw(drawable);
        }
    }
    
    /**Fires a projectile up at the aliens.
     * @param drawable Current OpenGL context. */
    public void shoot(GLAutoDrawable drawable) {
        if (gunCooldown < 1) {
            master.getProjectiles().add(new Projectile(drawable, master, this, 
                    new Vector2f(this.location.x - 4f, this.location.y + 32f), 
                    "res/textures/Bullet.png", new Vector2f(6f, 6f), 
                    new Vector2f(8f, 32f), 0.6f, 800f,  new Vector2f(0f, 1f), 
                    new Vector2f(0f, -32f), 5000));
            gunCooldown = gunCooldownMax;
        }
    }
}
