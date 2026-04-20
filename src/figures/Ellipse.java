package figures;

import java.util.logging.Logger;

import figures.enums.FigureType;
import figures.enums.LineType;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import utils.ColorFactory;

/**
 * Ellipse Figure containing a {@link javafx.scene.shape.Ellipse} as its
 * {@link Figure#shape}
 * @warning Since This class is also named "Ellipse", you'll need to use
 * (javafx.scene.shape.Ellipse) each time you need to acces to internal
 * {@link Figure#shape} casted as a {@link javafx.scene.shape.Ellipse}
 * @implSpec It is assumed that {@link Figure#shape} will always be non null
 * during the life cycle of an Ellipse.
 * @author davidroussel
 */
public class Ellipse extends Figure implements Mouseable
{
	/**
	 * Instances counter (to be used in {@link Figure#instanceNumber}) of each
	 * Ellipse.
	 * @implNote No need to decrease {@link Figure#instanceNumber} in
	 * #finalize()
	 */
	private static int counter = 0;

	/**
	 * Valued constructor to build azero size Ellipse at point (x, y).
	 * Used during Rectangle construction with {@link MouseEvent}s.
	 * Calls super-constructor, sets {@link Figure#instanceNumber} then
	 * {@link #createShape(double, double)} and attach {@link Figure#shape} to
	 * {@link Figure#root}.
	 * @param fillColor the fill color (or null if there is no fill color).
	 * The fill color set in this ellipse shall be set from {@link ColorFactory}.
	 * @param edgeColor the edge color (or null if there is no edge color)
	 * The edge color set in this ellipse shall be set from {@link ColorFactory}.
	 * @param lineType line type (Either {@link LineType#SOLID},
	 * {@link LineType#DASHED} or {@link LineType#NONE}).
	 * @param lineWidth line width of this ellipse.
	 * @param parentLogger a parent logger used to initialize the current logger
	 * @param x the initial x coordinate in the drawing panel where to create this ellipse
	 * @param y the initial y coordinate in the drawing panel where to create this ellipse
	 * @throws IllegalStateException if we try to set both fillColor and
	 * edgecolor as nulls
	 */
	public Ellipse(Color fillColor,
	               Color edgeColor,
	               LineType lineType,
	               double lineWidth,
	               Logger parentLogger,
	               double x,
	               double y)
	    throws IllegalStateException
	{
		super(fillColor, edgeColor, lineType, lineWidth, parentLogger);
		instanceNumber = counter++;
		createShape(x, y);
		root.getChildren().add(shape);
	}

	/**
	 * Valued constructor to build an Ellipse at point (x, y) with specified
	 * width and height.
	 * Calls super-constructor, sets {@link Figure#instanceNumber} then
	 * {@link #createShape(double, double)} and attach {@link Figure#shape} to
	 * {@link Figure#root}.
	 * @param fillColor the fill color (or null if there is no fill color).
	 * The fill color set in this ellipse shall be set from {@link ColorFactory}.
	 * @param edgeColor the edge color (or null if there is no edge color)
	 * The edge color set in this ellipse shall be set from {@link ColorFactory}.
	 * @param lineType line type (Either {@link LineType#SOLID},
	 * {@link LineType#DASHED} or {@link LineType#NONE}).
	 * @param lineWidth line width of this ellipse.
	 * @param parentLogger a parent logger used to initialize the current logger
	 * @param x the initial x coordinate in the drawing panel where to create this ellipse
	 * @param y the initial y coordinate in the drawing panel where to create this ellipse
	 * @param width the initial width of this Ellipse
	 * @param height the initial height of this Ellipse
	 * @throws IllegalStateException if we try to set both fillColor and
	 * edgecolor as nulls
	 */
	public Ellipse(Color fillColor,
	               Color edgeColor,
	               LineType lineType,
	               double lineWidth,
	               Logger parentLogger,
	               double x,
	               double y,
	               double width,
	               double height)
	    throws IllegalStateException
	{
		this(fillColor, edgeColor, lineType, lineWidth, parentLogger, x, y);
		javafx.scene.shape.Ellipse ellipse = (javafx.scene.shape.Ellipse) shape;
		ellipse.setRadiusX(width);
		ellipse.setRadiusY(height);
	}

	/**
	 * Copy constructor
	 * @param figure the figure to be copied
	 * @throws IllegalArgumentException if the provided figure is not an Ellipse
	 */
	public Ellipse(Figure figure) throws IllegalArgumentException
	{
		super(figure);
		if (!(figure instanceof Ellipse))
		{
			String message = "provided figure is not an Ellipse: "
			    + figure.getClass().getSimpleName();
			logger.severe(message);
			throw new IllegalArgumentException(message);
		}
		javafx.scene.shape.Ellipse figureEllipse = (javafx.scene.shape.Ellipse) figure.shape;
		shape = new javafx.scene.shape.Ellipse(figureEllipse.getCenterX(),
		                                       figureEllipse.getCenterY(),
		                                       figureEllipse.getRadiusX(),
		                                       figureEllipse.getRadiusY());
		applyParameters();
		root.getChildren().add(shape);
		setSelected(figure.selected);
	}

	/**
	 * Convenience method to get internal {@link Figure#shape} casted as a
	 * {@link javafx.scene.shape.Ellipse}
	 * @return the internal {@link Figure#shape} casted as a
	 * {@link javafx.scene.shape.Ellipse}
	 */
	private javafx.scene.shape.Ellipse getEllipseShape()
	{
		return (javafx.scene.shape.Ellipse)shape;
	}

	/**
	 * Center Point of this figure
	 * @return the center point of this figure
	 */
	@Override
	public Point2D getCenter()
	{
		javafx.scene.shape.Ellipse ellipseShape = getEllipseShape();
		return new Point2D(ellipseShape.getCenterX(), ellipseShape.getCenterY());
	}

	/**
	 * Width of this figure
	 * @return the width of this figure
	 */
	@Override
	public double width()
	{
		return getEllipseShape().getRadiusX() * 2.0;
	}

	/**
	 * Height of this figure
	 * @return the width of this figure
	 */
	@Override
	public double height()
	{
		return getEllipseShape().getRadiusY() * 2.0;
	}

	/**
	 * Top left corner of this figure
	 * @return the top left {@link Point2D} of this figure
	 */
	@Override
	public Point2D topLeft()
	{
		javafx.scene.shape.Ellipse ellipseShape = getEllipseShape();
		double halfWidth = ellipseShape.getRadiusX();
		double halfHeight = ellipseShape.getRadiusY();
		Point2D center = getCenter();

		return new Point2D(center.getX() - halfWidth,
		                   center.getY() - halfHeight);
	}

	/**
	 * Bottom right corner of this figure
	 * @return the bottom right {@link Point2D} of this figure
	 */
	@Override
	public Point2D bottomRight()
	{
		javafx.scene.shape.Ellipse ellipseShape = getEllipseShape();
		double halfWidth = ellipseShape.getRadiusX();
		double halfHeight = ellipseShape.getRadiusY();
		Point2D center = getCenter();

		return new Point2D(center.getX() + halfWidth,
		                   center.getY() + halfHeight);
	}

	/**
	 * Creates actual zero size {@link javafx.scene.shape.Shape} at specified
	 * position, apply drawing parameters and add the created shape to
	 * {@link #root}.
	 * @param x the x coordinate of the initial point where to create the new
	 * shape
	 * @param y the y coordinate of the initial point where to create the new
	 * shape
	 * @post a new {@link #shape} has been created with a new
	 * {@link #instanceNumber} with {@link #fillColor}, {@link #edgeColor},
	 * {@link #lineType} & {@link #lineWidth} applied with
	 * {@link Figure#applyParameters()}
	 */
	@Override
	public void createShape(double x, double y)
	{
		/*
		 * Note: since This class is also named Ellipse we need to explicitely
		 * use "new javafx.scene.shape.Ellipse(...)" here
		 */
		shape = new javafx.scene.shape.Ellipse(x, y, 0.0, 0.0);
		applyParameters();
	}

	/**
	 * Sets the last point of this figure.
	 * Sets the radius of this Ellipse based on the distance between center and
	 * the provided point
	 * @param lastPoint the point used to set this Ellipse's radius
	 */
	@Override
	public void setLastPoint(Point2D lastPoint)
	{
		Point2D center = getCenter();
		double dx = Math.abs(lastPoint.getX() - center.getX());
		double dy = Math.abs(lastPoint.getY() - center.getY());
		javafx.scene.shape.Ellipse ellipseShape = getEllipseShape();
		ellipseShape.setRadiusX(dx);
		ellipseShape.setRadiusY(dy);
	}

	/**
	 * Creates a copy of this ellipse (with the same name and instance number)
	 * @return A distinct copy of this ellipse
	 */
	@Override
	public Figure clone()
	{
		return new Ellipse(this);
	}

	/**
	 * Compare this ellipse to another figure
	 * @return true if the other figure is also an Ellipse with the same
	 * position and size (with {@link Figure#threshold}), false otherwise.
	 * Other parameters, such as {@link Figure#fillColor},
	 * {@link Figure#edgeColor}, {@link Figure#lineType},
	 * {@link Figure#lineWidth}, and transformations
	 * are checked in {@link Figure#equals(Object)}
	 */
	@Override
	protected boolean equals(Figure figure)
	{
		if (!(figure instanceof Ellipse))
		{
			return false;
		}

		Ellipse ellipse = (Ellipse) figure;

		if (Math.abs(getCenter().distance(ellipse.getCenter())) > threshold)
		{
			return false;
		}

		if (Math.abs(width() - ellipse.width()) > threshold)
		{
			return false;
		}

		if (Math.abs(height() - ellipse.height()) > threshold)
		{
			return false;
		}

		return true;
	}

	/**
	 * Hash code for Ellipses.
	 * uses super.hashCode then appends
	 * <ul>
	 * 	<li>FigureType.ELLIPSE.hashCode()</li>
	 * 	<li>hashCode for radiusX</li>
	 * 	<li>hashCode for radiusY</li>
	 * </ul>
	 * @return a hashCode based on super.hashCode,
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + FigureType.ELLIPSE.hashCode();
		javafx.scene.shape.Ellipse ellipse = getEllipseShape();
		long temp = Double.doubleToLongBits(ellipse.getRadiusX());
		result = (result * prime) + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(ellipse.getRadiusY());
		result = (result * prime) + (int) (temp ^ (temp >>> 32));
		return result;
	}
}
