package px.spaceInvaders.graphics;

import java.nio.FloatBuffer;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**TODO Class Description and all Methods
 * @author Michael Stopa */
public class Sprite {
    
    // ++++ ++++ Data ++++ ++++
    
    protected SpriteMaster master;
    
    //Game Logic
    protected Vector2f displacement = new Vector2f(0f,0f);
    protected Vector2f location = new Vector2f(0f,0f);
    
    //Rendering
    protected int texture;
    protected Vector2f offset;
    /**Size of the sprite in world coordinates (typically pixels) */
    protected Vector2f scale;
    protected Matrix4f scaleMatrix;
    protected int modelUniform;
    protected float depth;
    protected int depthUniform;
    
    // ++++ ++++ Initialization ++++ ++++
    
    public Sprite(GLAutoDrawable drawable, SpriteMaster master, 
            String texture, Vector2f scale, float depth) {
        this.master = master;
        this.texture = master.loadTexture(drawable, texture);
        this.scale = scale;
        scaleMatrix = Matrix4f.scale(new Vector3f(scale.x, scale.y, 1.0f), 
                new Matrix4f(), null);
        this.location = new Vector2f();
        this.modelUniform = master.getModelUniform();
        this.depth = depth;
        this.depthUniform = master.getDepthUniform();
    }
    
    // ++++ ++++ Game Logic ++++ ++++
    
    /**
     * @param elapsedTime Number of milliseconds elapsed since last update cycle. */
    public void update(GLAutoDrawable drawable, long elapsedTime) {
    	/*TODO Idea: reorganize Sprites to have current and last location, render based on
    	 * "current" location edited during the game loop.*/
        location = Vector2f.add((Vector2f) 
                new Vector2f(displacement).scale((float)elapsedTime / 1000f), 
                location, null);
    }
    
    // ++++ ++++ Rendering ++++ ++++
    
    public void draw(GLAutoDrawable drawable) {
        //TODO if currentAnimation is not null, draw animation instead.
        GL4 gl = drawable.getGL().getGL4();
        
        gl.glBindTexture(GL4.GL_TEXTURE_2D, texture);
        gl.glUniform1f(depthUniform, depth);
        
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
}
