package px.spaceInvaders.actors;

import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Vector2f;

import px.spaceInvaders.graphics.Animation;
import px.spaceInvaders.graphics.Sprite;
import px.spaceInvaders.graphics.SpriteMaster;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class Effect extends Sprite {
	
	Animation anim;

    public Effect(GLAutoDrawable drawable, SpriteMaster master, Animation anim, 
            Vector2f location, Vector2f size, float depth) {
        super(drawable, master, null, location, size, size, depth);
        this.anim = anim;
    }
    
    @Override
    public void update(GLAutoDrawable drawable, long elapsedTime) {
        super.update(drawable, elapsedTime);
        anim.update(drawable, elapsedTime);
    }
    
    @Override
    public void draw(GLAutoDrawable drawable) {
        texture = anim.getCurrentTexture();
        super.draw(drawable);
    }
    
    public boolean isDead() {
        return anim.isDead();
    }
}
