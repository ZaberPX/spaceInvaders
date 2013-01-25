package px.spaceInvaders.actors;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.graphics.SpriteMaster;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class Enemy extends Pawn {
    
    // ++++ ++++ Constants ++++ ++++
    
    public static final float BASE_SPEED = 40f;
    public static final float BASE_ACCEL = 4f;
    /**How far the enemy will move across before flipping its x direction.*/
    public static final float COLUMN_WIDTH = 256f;
    public static final Vector2f BASE_MOVEMENT = 
            (Vector2f) new Vector2f(4f, -1f).normalise();
    
    // ++++ ++++ Data ++++ ++++
    
    protected Vector2f movement;
    protected float startingX;
    protected float direction;
    
    // ++++ ++++ Initialization ++++ ++++

    public Enemy(GLAutoDrawable drawable, SpriteMaster master, String texture,
            Vector2f location, Vector2f hitSize, Vector2f drawSize, float depth,
            int health) {
        super(drawable, master, texture, location, hitSize, drawSize, depth, health);
        startingX = location.x;
        direction = 1f;
    }
    
    // ++++ ++++ Game Logic ++++ ++++
    
    @Override
    public void update(GLAutoDrawable drawable, long elapsedTime) {
        
        movement = (Vector2f) new Vector2f(BASE_MOVEMENT.x * direction, BASE_MOVEMENT.y)
                .scale(BASE_ACCEL + (float) master.getWave() / 10f);
        displacement = Vector2f.add(movement, displacement, null);
        if (displacement.length() > BASE_SPEED + master.getWave()) {
            displacement.normalise().scale(BASE_SPEED + master.getWave());
        }
        
        super.update(drawable, elapsedTime);
        
        if (location.x < startingX && direction != 1f) {
            direction = 1f;
        } else if (location.x > startingX + COLUMN_WIDTH && direction != -1f) {
            direction = -1f;
        }
    }
    
    @Override
    public void dealDamage(int damage) {
        super.dealDamage(damage);
        //TODO: if dead, spawn effect, give points, chance to drop powerup.
    }
}
