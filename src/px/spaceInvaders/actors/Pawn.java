package px.spaceInvaders.actors;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.graphics.Sprite;
import px.spaceInvaders.graphics.SpriteMaster;

/**Class that encapsulates all code for player and AI driven characters in the game.
 * @author Michael Stopa */
public abstract class Pawn extends Sprite {
    
    // ++++ ++++ Data ++++ ++++
    
    protected int maxHealth;
    protected int health;
    
    // ++++ ++++ Accessors ++++ ++++
    
    /** @return Number of hit-points this object has remaining. */
    public int getHealth() {
        return health;
    }
    
    // ++++ ++++ Initialization ++++ ++++

    /**Constructor for Pawn objects.
     * @param drawable Current OpenGL context.
     * @param master SpriteMaster object administering this object.
     * @param texture Filename of the texture used to draw this object to the screen.
     * @param location Location of this object on the screen.
     * @param hitSize Size of this object's collision box.
     * @param drawSize Size of this object when drawn onto the screen.
     * @param depth The Pawn's z-depth (RHS 1.0 to -1.0)
     * @param health The pawn's hit points, the character is dead when this hits zero. */
    public Pawn(GLAutoDrawable drawable, SpriteMaster master, String texture,
            Vector2f location, Vector2f hitSize, Vector2f drawSize, float depth, 
            int health) {
        super(drawable, master, texture, location, hitSize, drawSize, depth);
        this.maxHealth = health;
        this.health = maxHealth;
    }
    
    // ++++ ++++ Game Logic ++++ ++++
    
    /**Damages or heals the Pawn, healing will not bring a Pawn above max health.
     * @param damage Positive values damage, negative heal. */
    public void dealDamage(int damage) {
        health -= damage;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }
}
