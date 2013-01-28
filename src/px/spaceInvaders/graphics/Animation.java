package px.spaceInvaders.graphics;

import javax.media.opengl.GLAutoDrawable;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class Animation {
    
    // ++++ ++++ Data ++++ ++++
    
    private int[] frames;
    private int duration;
    private int durationMax;
    private int currentFrame;
    
    // ++++ ++++ Initialization ++++ ++++
    
    public Animation(GLAutoDrawable drawable, SpriteMaster master, String[] filenames, 
            int duration) {
        this.duration = duration;
        durationMax = duration;
        currentFrame = 0;
        frames = new int[filenames.length];
        for (int i = 0; i < filenames.length; i++) {
            frames[i] = master.loadTexture(drawable, filenames[i]);
        }
    }
    
    // ++++ ++++ Game Loop ++++ ++++
    
    public void update(GLAutoDrawable drawable, long elapsedTime) {
        duration += elapsedTime;
        while (currentFrame < frames.length && duration > durationMax) {
            duration -= durationMax;
            currentFrame++;
        }
    }
    
    // ++++ ++++ Accessors ++++ ++++
    
    public int getCurrentTexture() {
        if (currentFrame > 0 && currentFrame < frames.length){ 
            return frames[currentFrame];
        } else {
            return 0;
        }
    }
    
    public boolean isDead() {
        return (currentFrame >= frames.length);
    }
}
