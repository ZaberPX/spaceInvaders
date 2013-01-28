package px.spaceInvaders.actors;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.graphics.Animation;
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
    	checkCollision(drawable);
    	
    	lifetime -= elapsedTime;
    	
        super.update(drawable, elapsedTime);
    }
    
    public void checkCollision(GLAutoDrawable drawable) {
        for (Projectile p: master.getProjectiles()) {
            if (this.collidesWith(p)) {
                detonate(drawable);
                p.detonate(drawable);
            }
        }
        if (parent instanceof Player) {
        	for (Enemy e: master.getEnemies()) {
        	    if (this.collidesWith(e)) {
        	        if (e.getHealth() > 0) {
        	            detonate(drawable);
        	            e.dealDamage(80);
        	            if (e.getHealth() <= 0) {
        	                master.addScore(1);
        	                //Spawn explosion
        	                Animation a = new Animation(drawable, master, new String[] {
        	                        "res/textures/effects/Explosion0.png",
                                    "res/textures/effects/Explosion1.png",
                                    "res/textures/effects/Explosion2.png",
                                    "res/textures/effects/Explosion3.png",
                                    "res/textures/effects/Explosion4.png"
        	                }, 100);
        	                Effect ef = new Effect(drawable, master, a, e.getLocation(), 
        	                        new Vector2f(64f, 64f), 0.65f);
        	                master.getEffects().add(ef);
        	            }
        	        }
        	    }
        	}
        } else if (parent instanceof Enemy) {
            if (this.collidesWith(master.getPlayer())) {
                detonate(drawable);
                master.getPlayer().dealDamage(1);
            }
        }
    }
    
    /**removes this projectile and spawns an explosion. */
    public void detonate(GLAutoDrawable drawable) {
        //System.out.println("Hit on Enemy");
        if (parent instanceof Player) {
            Animation a = new Animation(drawable, master, new String[] {
                    "res/textures/effects/Flash0.png",
                    "res/textures/effects/Flash1.png",
                    "res/textures/effects/Flash2.png"
            }, 100);
            Effect e = new Effect(drawable, master, a, location, new Vector2f(32f, 32f), 
                    0.65f);
            master.getEffects().add(e);
        } else if (parent instanceof Enemy) {
            Animation a = new Animation(drawable, master, new String[] {
                    "res/textures/effects/FlashEnemy0.png",
                    "res/textures/effects/FlashEnemy1.png",
                    "res/textures/effects/FlashEnemy2.png"
            }, 100);
            Effect e = new Effect(drawable, master, a, location, new Vector2f(32f, 32f), 
                    0.65f);
            master.getEffects().add(e);
        }
        lifetime = 0;//Flag for disposal.
    }
    
    @Override
    public boolean collidesWith(Sprite other) {
        if (lifetime <= 0) {
            return false;
        } else {
            return super.collidesWith(other);
        }
    }
}
