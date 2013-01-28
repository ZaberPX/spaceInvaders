package px.spaceInvaders.graphics;

import java.nio.FloatBuffer;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**Base class for all visible objects in the game.
 * @author Michael Stopa */
public abstract class Sprite {
    
    // ++++ ++++ Data ++++ ++++
    
    protected SpriteMaster master;
    
    //Game Logic
    protected Vector2f displacement = new Vector2f(0f,0f);
    protected Vector2f location = new Vector2f(0f,0f);
    
    //Rendering
    protected int texture;
    /**Size of the sprite's collision box. */
    protected Vector2f hitSize;
    /**Offset of the sprite's visual representation (doesn't affect collision). */
    protected Vector2f offset;
    /**Size of the sprite in world coordinates (typically pixels) */
    protected Vector2f drawSize;
    protected Matrix4f scaleMatrix;
    protected int modelUniform;
    protected Vector4f tint = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    protected int tintUniform;
    protected float depth;
    protected int depthUniform;
    
    // ++++ ++++ Initialization ++++ ++++
    
    /**Base constructor handles texture loading and basic setup.
     * @param drawable Current OpenGL context.
     * @param master SpriteMaster object administering this one.
     * @param texture Filename of the iamge used to represent the sprite in game.
     * @param location Initial location of the sprite in world-coordinates.
     * @param hitSize Size of this object's collision box.
     * @param drawSize Size of this object when draw to the screen.
     * @param depth The object's z-depth (RHS 1.0 to -1.0) */
    public Sprite(GLAutoDrawable drawable, SpriteMaster master, String texture, 
            Vector2f location, Vector2f hitSize, Vector2f drawSize, float depth) {
        this.master = master;
        if (texture != null) {
            this.texture = master.loadTexture(drawable, texture);
        }
        this.location = location;
        this.hitSize = hitSize;
        this.drawSize = drawSize;
        scaleMatrix = Matrix4f.scale(new Vector3f(drawSize.x, drawSize.y, 1.0f), 
                new Matrix4f(), null);
        this.location = location;
        this.modelUniform = master.getModelUniform();
        this.tintUniform = master.getTintUniform();
        this.depth = depth;
        this.depthUniform = master.getDepthUniform();
    }
    
    // ++++ ++++ Game Logic ++++ ++++
    
    /**Updates' the sprite's position in the world based on it's current displacement.
     * @param drawable Current OpenGL context.
     * @param elapsedTime Number of milliseconds elapsed since last update cycle. */
    public void update(GLAutoDrawable drawable, long elapsedTime) {
        location = Vector2f.add((Vector2f) 
                new Vector2f(displacement).scale((float)elapsedTime / 1000f), 
                location, null);
    }
    
    // ++++ Collision Detection ++++

    /**Performs a simple rectangle overlap check to determine if two collideable objects
     * intersect with each other.
     * @param other Object being compared with.
     * @return True if this object is currently intersecting the other object. */
    public boolean collidesWith(Sprite other) {
        //Don't hit yourself, silly.
        if (this == other) {
            return false;
        }
        if (this.getHitSize() == null || other.getHitSize() == null) {
            return false;
        }
        
        Vector2f thisTopLeft = Vector2f.sub(location, 
                (Vector2f) new Vector2f(hitSize).scale(0.5f), null);
        Vector2f thisBottomRight = Vector2f.add(location, 
                (Vector2f) new Vector2f(hitSize).scale(0.5f), null);
        Vector2f otherTopLeft = Vector2f.sub(other.getLocation(), 
                (Vector2f) new Vector2f(other.getHitSize()).scale(0.5f), null);
        Vector2f otherBottomRight = Vector2f.add(other.getLocation(), 
                (Vector2f) new Vector2f(other.getHitSize()).scale(0.5f), null);
        
        //Axis-aligned rectnagle test URL:
        //http://leetcode.com/2011/05/determine-if-two-rectangles-overlap.html
        if (!(thisBottomRight.y < otherTopLeft.y || thisTopLeft.y > otherBottomRight.y || 
                thisBottomRight.x < otherTopLeft.x || thisTopLeft.x > otherBottomRight.x)) {
            return true;
        } else {
            return false; 
        }
    }
    
    // ++++ ++++ Rendering ++++ ++++
    
    /**Draws the object to the screen.
     * @param drawable Current OpenGl context. */
    public void draw(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        
        gl.glBindTexture(GL4.GL_TEXTURE_2D, texture);
        gl.glUniform1f(depthUniform, depth);
        gl.glUniform4f(tintUniform, tint.x, tint.y, tint.z, tint.w);
        
        //Setup Model matrix
        //Scale
        Matrix4f model = Matrix4f.mul(scaleMatrix, new Matrix4f(), null);
        //Offset
        if (offset != null) {
            Matrix4f off = Matrix4f.translate(offset, new Matrix4f(), null);
            model = Matrix4f.mul(off, model, null);
        }
        //Location
        Matrix4f loc = Matrix4f.translate(location, new Matrix4f(), null);
        model = Matrix4f.mul(loc, model, null);
        
        FloatBuffer buffer = FloatBuffer.allocate(16);
        model.store(buffer);
        buffer.rewind();
        gl.glUniformMatrix4fv(modelUniform, 1, false, buffer);
        gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, 4);
        
        /*/Debugging code
        Matrix4f view = master.getView();
        Matrix4f modelview = Matrix4f.mul(view, model, null);
        Vector4f result = Matrix4f.transform(modelview, 
                new Vector4f(location.x, location.y, depth, 1f), null);
        System.out.println("Vector: " + location.x + ", " + location.y + ", " + depth + 
                ", 1.0 Transformed: " + result.toString());
        System.out.println("Model:\n" + model.toString());
        System.out.println("View:\n" + view.toString());
        System.out.println("ModelView:\n" + modelview.toString());
        System.out.println("Breakpoint!");//*/
    }
    
    // ++++ ++++ Accessors ++++ ++++
    
    /**@return OpenGL reference number for this object's texture. */
    public int getTexture() {
        return texture;
    }

    /**@return Size of this object's collision box in world-coordinates */
    public Vector2f getHitSize() {
        return hitSize;
    }

    /**@return Current location of this object in world coordinates. */
    public Vector2f getLocation() {
        return location;
    }
}
