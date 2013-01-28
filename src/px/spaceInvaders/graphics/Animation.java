package px.spaceInvaders.graphics;

import javax.media.opengl.GLAutoDrawable;

/**Class holds references to all texture frames within an animation, must remember to
 * call update on animation object every frame in an object implementing this class
 * directly.
 * <br>
 * Animation will run through only once, needs to be disposed of afterwards.
 * 
 * @author Michael Stopa */
public class Animation {
    
    // ++++ ++++ Data ++++ ++++
    
    private int[] frames;
    private int duration;
    private int durationMax;
    private int currentFrame;
    
    // ++++ ++++ Initialization ++++ ++++
    
    /**Create a new Animation object.
     * @param drawable Current OpenGL Context
     * @param master SpriteMaster class that will administer this object.
     * @param filenames An array of filename strings to each frame in the animation.
     * @param duration Amount of milliseconds each frame will be displayed for. */
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
    
    /**plays the animation forward one update-loop's worth of time.
     * @param drawable Current OpenGL context
     * @param elapsedTime Amount of milliseconds since last update loop. */
    public void update(GLAutoDrawable drawable, long elapsedTime) {
        duration += elapsedTime;
        while (currentFrame < frames.length && duration > durationMax) {
            duration -= durationMax;
            currentFrame++;
        }
    }
    
    // ++++ ++++ Accessors ++++ ++++
    
    /**Call to retrieve current texture reference during a draw call.
     * @return OpenGL reference to current Texture in this animation */
    public int getCurrentTexture() {
        if (currentFrame > 0 && currentFrame < frames.length){ 
            return frames[currentFrame];
        } else {
            return 0;
        }
    }
    
    /**Determines whether this object is ready for disposal.
     * @return True if this animation is ready to be disposed of. */
    public boolean isDead() {
        return (currentFrame >= frames.length);
    }
}
