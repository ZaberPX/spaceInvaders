package px.spaceInvaders.core;

import javax.media.opengl.awt.GLCanvas;

import px.util.FrameControl;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class GameCore {
    
    // ++++ ++++ Data ++++ ++++
    
    public static GameCore instance;
    
    private Mode mode;
    private Frame frame;
    
    // ++++ ++++ Accessors ++++ ++++
    
    public GLCanvas getCanvas() {
        return frame.getCanvas();
    }
    
    public Mode getMode() {
        return mode;
    }
    
    /**
     * @param args Command-line Arguments */
    public static void main(String[] args) {
        instance = new GameCore();
    }
    
    // ++++ ++++ Enumeration ++++ ++++
    
    public enum Mode {
        IN_GAME,
        PAUSED,
        LOADING,
        IN_MENU
    }
    
    // ++++ ++++ Initialization ++++ ++++
    
    public GameCore() {
        frame = new Frame();
        frame.setVisible(true);
        FrameControl.centerFrame(frame);
        
        mode = Mode.LOADING;
    }
}
