package px.spaceInvaders.actors;

import java.awt.event.KeyEvent;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.core.InputMaster;
import px.spaceInvaders.graphics.SpriteMaster;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class Player extends Pawn {
    
    // ++++ ++++ Data ++++ ++++
    
    protected Vector2f movement = new Vector2f(0f, 0f);
    protected float accel = 50f;
    protected float speed = 500f;
    
    protected int gunCooldown = 0;
    protected int gunCooldownMax = 200;
    
    // ++++ ++++ Initialization ++++ ++++

    public Player(GLAutoDrawable drawable, SpriteMaster master) {
        super(drawable, master, "res/textures/PlayerTank.png", new Vector2f(0, -64f), 
                new Vector2f(50f, 50f), new Vector2f(64f, 64f), 0.5f, 100);
        // TODO Auto-generated constructor stub
    }
    
    // ++++ ++++ Game Logic ++++ ++++
    
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
        if (in.isKeyUp(KeyEvent.VK_SHIFT)) {
            //TODO: Shoot missile.
            System.out.println("Missile Fired!");
        }
        if (in.isKeyUp(KeyEvent.VK_ESCAPE)) {
            //TODO: Pause Menu
            System.exit(0);
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
    
    /**Fires a projectile up at the aliens. */
    public void shoot(GLAutoDrawable drawable) {
        if (gunCooldown < 1) {
            master.getProjectiles().add(new Projectile(drawable, master, this, 
                    new Vector2f(this.location.x + 4f, this.location.y + 32f), 
                    "res/textures/Bullet.png", new Vector2f(6f, 6f), 
                    new Vector2f(8f, 32f), 0.6f, 800f,  new Vector2f(0f, 1f), 
                    new Vector2f(0, -32), 5000));
            gunCooldown = gunCooldownMax;
        }
    }
}
