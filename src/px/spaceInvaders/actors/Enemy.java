package px.spaceInvaders.actors;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.graphics.Animation;
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
    public static final int GUN_COOLDOWN_MAX = 200;
    
    // ++++ ++++ Data ++++ ++++
    
    protected Vector2f movement;
    protected float startingX;
    protected float direction;

    protected int gunCooldown = GUN_COOLDOWN_MAX;
    
    // ++++ ++++ Initialization ++++ ++++

    public Enemy(GLAutoDrawable drawable, SpriteMaster master, String texture,
            Vector2f location, Vector2f hitSize, Vector2f drawSize, float depth,
            int health) {
        super(drawable, master, texture, location, hitSize, drawSize, depth, health);
        startingX = location.x;
        direction = 1f;
        Animation a = new Animation(drawable, master, new String[] {
                "res/textures/effects/TeleportIn0.png",
                "res/textures/effects/TeleportIn1.png",
                "res/textures/effects/TeleportIn2.png"
        }, 100);
        Effect e = new Effect(drawable, master, a, location, new Vector2f(64f, 64f), 
                0.65f);
        master.getEffects().add(e);
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
        
        gunCooldown -= elapsedTime;
        if (gunCooldown < 0) {
            if (SpriteMaster.random.nextInt(50) == 0) {
                master.getProjectiles().add(new Projectile(drawable, master, this, 
                        new Vector2f(this.location.x, this.location.y - 32f), 
                        "res/textures/BulletEnemy.png", new Vector2f(6f, 6f), 
                        new Vector2f(8f, 32f), 0.6f, 400f,  new Vector2f(0f, -1f), 
                        new Vector2f(0f, 32f), 5000));
            }
            gunCooldown = GUN_COOLDOWN_MAX;
        }
        
        super.update(drawable, elapsedTime);
        
        if (location.x < startingX && direction != 1f) {
            direction = 1f;
        } else if (location.x > startingX + COLUMN_WIDTH && direction != -1f) {
            direction = -1f;
        }
        
        if (location.y < -32f) {
            //Spawn Invasion Effect
            master.getPlayer().dealDamage(5);
            health = 0;
            Animation a = new Animation(drawable, master, new String[] {
                    "res/textures/effects/TeleportOut0.png",
                    "res/textures/effects/TeleportOut1.png",
                    "res/textures/effects/TeleportOut2.png"
            }, 100);
            Effect e = new Effect(drawable, master, a, location, new Vector2f(64f, 64f), 
                    0.65f);
            master.getEffects().add(e);
        }
    }
    
    @Override
    public void dealDamage(int damage) {
        super.dealDamage(damage);
    }
}
