package px.spaceInvaders.actors;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.graphics.Sprite;
import px.spaceInvaders.graphics.SpriteMaster;

/**Class that encapsulates all code for player and AI driven characters in the game.
 * @author Michael Stopa */
public class Pawn extends Sprite {
    
    // ++++ ++++ Data ++++ ++++
    
    protected int maxHealth;
    protected int health;
    
    // ++++ ++++ Initialization ++++ ++++

    public Pawn(GLAutoDrawable drawable, SpriteMaster master, String texture,
            Vector2f location, Vector2f hitSize, Vector2f drawSize, float depth, 
            int health) {
        super(drawable, master, texture, location, hitSize, drawSize, depth);
        this.maxHealth = health;
        this.health = maxHealth;
    }
    
    // ++++ ++++ Game Logic ++++ ++++
    
    public void dealDamage(int damage) {
        health -= damage;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }
    
    @Override
    public void update(GLAutoDrawable drawable, long elapsedTime) {
        
        super.update(drawable, elapsedTime);
        
        if (location.x < 0f) {
            location.x = 0f;
            displacement.x = 0f;
        } else if (location.x > 960f) {
            location.x = 960f;
            displacement.x = 0f;
        }
    }
}
