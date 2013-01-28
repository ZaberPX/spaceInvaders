package px.spaceInvaders.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**Delightful little class that captures keyboard input and allows polling in Java. Only
 * supports key codes up to the value of the MAX_KEY_VALUE constant.
 * <br>
 * Based on:http://gpsnippets.blogspot.com.au/2008/03/keyboard-input-polling-system-in-java.html
 * @author Michael Stopa */
public class InputMaster implements KeyListener {
    
    // ++++ ++++ Constants ++++ ++++
    
    /**Keys with a KeyEvent.VK_* keycode higher than this constant are not tracked
     * by the poller. */
    public static final int MAX_KEY_VALUE = 525;
    
    // ++++ ++++ Data ++++ ++++
    
    private static InputMaster instance = null;
    
    private int[] keys = new int[MAX_KEY_VALUE];
    private String keyCache = "";
    
    private boolean[] keyStateUp = new boolean[MAX_KEY_VALUE];
    private boolean[] keyStateDown = new boolean[MAX_KEY_VALUE];
    
    private boolean keyPressed = false;
    private boolean keyReleased = false;
    
    // ++++ ++++ Initialization ++++ ++++
    
    /**Empty Constructor, doesn't actually do anything. */
    protected InputMaster() {
        
    }
    
    // ++++ ++++ Game Logic ++++ ++++
    
    /**Updates the state of the InputMaster.
     * <br>
     * Only resets keyStateUp because you don't want keys to be showing as up forever
     * which is what will happen if unless the array is cleared. */
    public void update() {
        //clear out key up states
        keyStateUp = new boolean[MAX_KEY_VALUE];
        keyReleased = false;
        if (keyCache.length() > 1024) {
            keyCache = "";
        }
    }
    /**Returns true if the key (0-{@link KEY_MAX_VALUE}) is being pressed. Use the 
     * keycodes from {@link KeyEvent} to check specific keys.
     * @param key KeyEvent.VK_Key code of the key being checked.
     * @return True if the specified key is currently being pressed. */
    public boolean isKeyDown(int key) {
        return keyStateDown[key];
    }
    
    /**Returns true if the key (0-{@link KEY_MAX_VALUE}) is not being pressed. Use the 
     * keycodes from {@link KeyEvent} to check specific keys.
     * @param key KeyEvent.VK_Key code of the key being checked.
     * @return True if the specified key is currently not being pressed. */
    public boolean isKeyUp(int key) {
        return keyStateUp[key];
    }
    
    /**A check to see if any key has been pressed at all in the last update cycle.
     * @return True if one or more keys have been pressed since the last call to 
     * {@link InputMaster#update()}. */
    public boolean isAnyKeyDown() {
        return keyPressed;
    }
    
    /**A check to see if any key has been released at all in the last update cycle.
     * @return True if one or more keys have been released since the last call to
     * {@link InputMaster#update()}. */
    public boolean isAnyKeyUp() {
        return keyReleased;
    }
    
    // ++++ ++++ Event Handling ++++ ++++

    /**Processes a received keyPressed event.
     * @param e Event calling this method. */
    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println("InputMaster: A key has been pressed, code: " + e.getKeyCode());
        if (e.getKeyCode() >= 0 && e.getKeyCode() < MAX_KEY_VALUE) {
            keys[e.getKeyCode()] = (int) System.currentTimeMillis();
            keyStateDown[e.getKeyCode()] = true;
            keyStateUp[e.getKeyCode()] = false;
            keyPressed = true;
            keyReleased = false;
        }
    }
    
    /**Processes a received keyReleased event.
     * @param e Event calling this method. */
    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("InputMaster: A key has been released code: " + e.getKeyCode());
        keys[e.getKeyCode()] = 0;
        keyStateUp[e.getKeyCode()] = true;
        keyStateDown[e.getKeyCode()] = false;
        keyPressed = false;
        keyReleased = true;
    }
    
    /**Processes a received keyTyped event.
     * @param e Event calling this method. */
    @Override
    public void keyTyped(KeyEvent e) {
        keyCache += e.getKeyChar();
    }
    
    // ++++ ++++ Accessors ++++ ++++
    
    /**Returns a reference to the current InputMaster singleton instance, use this method
     * instead of manually constructing a new instance. Automatically creates an instance
     * of InputMaster if one does not currently exist.
     * @return The currently initialized instance of InputMaster. */
    public static InputMaster getInstance() {
        if (instance == null) {
            instance = new InputMaster();
        }
        
        return instance;
    }
}

