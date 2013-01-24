package px.spaceInvaders.actors;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.graphics.Sprite;
import px.spaceInvaders.graphics.SpriteMaster;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class Projectile extends Sprite {
    
    // ++++ ++++ Data ++++ ++++
    
    private Sprite parent;
    private Vector2f direction;
    private float speed;
    protected int lifetime;
    
    // ++++ ++++ Initialization ++++ ++++

    public Projectile(GLAutoDrawable drawable, SpriteMaster master, Sprite parent, 
            Vector2f location, String texture, Vector2f hitSize, Vector2f drawSize, 
            float depth, float speed, Vector2f direction, Vector2f offset, int lifetime) {
        super(drawable, master, texture, location, hitSize, drawSize, depth);
        this.parent = parent;
        this.direction = direction.normalise(null);
        this.speed = speed;
        this.offset = offset;
        this.lifetime = lifetime;
    }
    
    // ++++ ++++ Accessors ++++ ++++
    
    /**@return Number of milliseconds before this object is to be disposed. */
    public int getLifetime() {
        return lifetime;
    }
    
    // ++++ ++++ Game Logic ++++ ++++
    
    @Override
    public void update(GLAutoDrawable drawable, long elapsedTime) {
        
        if (speed > 0) {
            displacement = (Vector2f) new Vector2f(direction).scale(speed);
        }
    	checkCollision();
    	
    	lifetime -= elapsedTime;
    	
        super.update(drawable, elapsedTime);
    }
    
    public void checkCollision() {
        for (Projectile p: master.getProjectiles()) {
            if (this.collidesWith(p)) {
                detonate();
            }
        }
        if (parent instanceof Player) {
        	for (Enemy e: master.getEnemies()) {
        	    if (this.collidesWith(e)) {
        	        detonate();
        	        e.dealDamage(10);
        	    }
        	}
        } else if (parent instanceof Enemy) {
            for (Bunker b: master.getBunkers()) {
                if (this.collidesWith(b)) {
                    detonate();
                }
            }
            if (this.collidesWith(master.getPlayer())) {
                detonate();
            }
        }
    }
    
    /**TODO: removes this projectile and spawns an explosion. */
    public void detonate() {
        //System.out.println("Hit on Enemy");
        //TODO: Spawn an explosion effect to actually deal the damage.
    }
}
