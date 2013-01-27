package px.spaceInvaders.core;

import javax.media.opengl.awt.GLCanvas;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class GameCore {
    
    // ++++ ++++ Enumeration ++++ ++++
    
    public enum Mode {
        IN_GAME,
        PAUSED,
        MAIN_MENU,
        GAME_OVER
    }
    
    // ++++ ++++ Data ++++ ++++
    
    public static GameCore instance;
    
    private Mode mode;
    private Frame frame;
    
    /**
     * @param args Command-line Arguments */
    public static void main(String[] args) {
        instance = new GameCore();
    }
    
    // ++++ ++++ Initialization ++++ ++++
    
    public GameCore() {
        frame = new Frame();
        frame.setVisible(true);
        FrameControl.centerFrame(frame);
        
        mode = Mode.MAIN_MENU;
    }
    
    // ++++ ++++ Accessors ++++ ++++
    
    public GLCanvas getCanvas() {
        return frame.getCanvas();
    }
    
    public Mode getMode() {
        return mode;
    }
    
    // ++++ ++++ Mutators ++++ ++++
    
    public void setMode(Mode mode) {
        this.mode = mode;
    }
}
