package figures;

import javafx.geometry.Point2D;

/**
 * Interface shared by all Figures that can be drawn using mouse events.
 * @author davidroussel
 */
public interface Mouseable {
	/**
	 * Creates actual zero size {@link javafx.scene.shape.Shape} at specified
	 * position, apply drawing parameters and add the created shape to the root
	 * group.
	 * @param x the x coordinate of the initial point where to create the new shape
	 * @param y the y coordinate of the initial point where to create the new shape
	 */
	public abstract void createShape(double x, double y);

	/**
	 * Sets the last point of this figure.
	 * This method can be implemented in subclasses to set the actual
	 * shape size by moving the mouse cursor around and/or clicking to
	 * add points.
	 * @param lastPoint the last point to set
	 */
	public abstract void setLastPoint(Point2D lastPoint);
}
