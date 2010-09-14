package org.volumeshadow;

import java.util.ArrayList;

import org.volumeshadow.math.Triangle;
import org.volumeshadow.math.Vector3f;



/**
 * <h1>ShadowScene</h1>
 * 
 * <p>
 * Your scene needs to implement this interface in order to use the
 * <code>VolumeShadowCreator</code> class.
 * </p>
 * 
 * @author Tim Jörgen
 */
public interface ShadowScene {
	/**
	 * Returns the vertex data of your occluder. This is the model that casts
	 * the shadow. For example a player model in a game.
	 * 
	 * @return the vertex data of your occluder
	 */
	public ArrayList<Triangle> getOccluderVertexData();

	/**
	 * This is the position of your occluder. Normally this is what you pass to
	 * glTranslatef.
	 * 
	 * @return the position of your occluder
	 */
	public Vector3f getOccluderPosition();

	/**
	 * The position of your light source, that casts the shadow.
	 * 
	 * @return the position of your light source
	 */
	public Vector3f getLightPosition();

	/**
	 * This renders your world WITHOUT the occluder.
	 * 
	 * @param renderWithLight
	 *            The volume shadow creator will pass two different light
	 *            settings. If it is set to true you should render your scene
	 *            with light, if it is false, you should render your scene with
	 *            light off.
	 */
	public void renderWorld(boolean renderWithLight);
	
	/**
	 * This renders your occluder. 
	 * @param renderWithLight
	 *            The volume shadow creator will pass two different light
	 *            settings. If it is set to true you should render your scene
	 *            with light, if it is false, you should render your scene with
	 *            light off.
	 */
	public void renderOccluder(boolean renderWithLight);
}
