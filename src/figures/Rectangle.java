package figures;

import java.util.logging.Logger;

import figures.enums.FigureType;
import figures.enums.LineType;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import utils.ColorFactory;

/**
 * Rectangle Figure containing a {@link javafx.scene.shape.Rectangle} as its
 * {@link Figure#shape}
 * @warning Since This class is also named "Rectangle", you'll need to use
 * (javafx.scene.shape.Rectangle) each time you need to acces to internal
 * {@link Figure#shape} casted as a {@link javafx.scene.shape.Rectangle}
 * @implSpec It is assumed that {@link Figure#shape} will always be non null
 * during the life cycle of an Rectangle.
 * @author davidroussel
 */
public class Rectangle extends Figure implements Mouseable
{
	/**
	 * Instances counter (to be used in {@link Figure#instanceNumber}) of each
	 * Rectangle.
	 * @implNote No need to decrease {@link Figure#instanceNumber} in
	 * #finalize()
	 */
	private static int counter = 0;

	/**
	 * Valued constructor to build a zero size Rectangle at point (x, y).
	 * Used during Rectangle construction with {@link MouseEvent}s.
	 * Calls super-constructor, sets {@link Figure#instanceNumber} then
	 * {@link #createShape(double, double)} and attach {@link Figure#shape} to
	 * {@link Figure#root}.
	 * @param fillColor the fill color (or null if there is no fill color).
	 * The fill color set in this Rectangle shall be set from {@link ColorFactory}.
	 * @param edgeColor the edge color (or null if there is no edge color)
	 * The edge color set in this Rectangle shall be set from {@link ColorFactory}.
	 * @param lineType line type (Either {@link LineType#SOLID},
	 * {@link LineType#DASHED} or {@link LineType#NONE}).
	 * @param lineWidth line width of this Rectangle.
	 * @param parentLogger a parent logger used to initialize the current logger
	 * @param x the initial x coordinate in the drawing panel where to create this Rectangle
	 * @param y the initial y coordinate in the drawing panel where to create this Rectangle
	 * @throws IllegalStateException if we try to set both fillColor and
	 * edgecolor as nulls
	 */
	public Rectangle(Color fillColor,
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
	 * Valued constructor to build an Rectangle at point (x, y) with specified
	 * width and height.
	 * Calls super-constructor, sets {@link Figure#instanceNumber} then
	 * {@link #createShape(double, double)} and attach {@link Figure#shape} to
	 * {@link Figure#root}.
	 * @param fillColor the fill color (or null if there is no fill color).
	 * The fill color set in this Rectangle shall be set from {@link ColorFactory}.
	 * @param edgeColor the edge color (or null if there is no edge color)
	 * The edge color set in this Rectangle shall be set from {@link ColorFactory}.
	 * @param lineType line type (Either {@link LineType#SOLID},
	 * {@link LineType#DASHED} or {@link LineType#NONE}).
	 * @param lineWidth line width of this Rectangle.
	 * @param parentLogger a parent logger used to initialize the current logger
	 * @param x the initial x coordinate in the drawing panel where to create this Rectangle
	 * @param y the initial y coordinate in the drawing panel where to create this Rectangle
	 * @param width the initial width of this Rectangle
	 * @param height the initial height of this Rectangle
	 * @throws IllegalStateException if we try to set both fillColor and
	 * edgecolor as nulls
	 */
	public Rectangle(Color fillColor,
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
		javafx.scene.shape.Rectangle Rectangle = (javafx.scene.shape.Rectangle) shape;
		Rectangle.setWidth(width);
		Rectangle.setHeight(height);
	}

	/**
	 * Copy constructor
	 * @param figure the figure to be copied
	 * @throws IllegalArgumentException if the provided figure is not an Rectangle
	 */
	public Rectangle(Figure figure) throws IllegalArgumentException
	{
		super(figure);
		if (!(figure instanceof Rectangle))
		{
			String message = "provided figure is not an Rectangle: "
			    + figure.getClass().getSimpleName();
			logger.severe(message);
			throw new IllegalArgumentException(message);
		}
		javafx.scene.shape.Rectangle figureRectangle = (javafx.scene.shape.Rectangle) figure.shape;
		shape = new javafx.scene.shape.Rectangle(figureRectangle.getX(),
		                                       figureRectangle.getY(),
		                                       figureRectangle.getWidth(),
		                                       figureRectangle.getHeight());
		applyParameters();
		root.getChildren().add(shape);
		setSelected(figure.selected);
	}

	/**
	 * Convenience method to get internal {@link Figure#shape} casted as a
	 * {@link javafx.scene.shape.Rectangle}
	 * @return the internal {@link Figure#shape} casted as a
	 * {@link javafx.scene.shape.Rectangle}
	 */
	private javafx.scene.shape.Rectangle getRectangleShape()
	{
		return (javafx.scene.shape.Rectangle)shape;
	}

	/**
	 * Center Point of this figure
	 * @return the center point of this figure
	 */
	@Override
	public Point2D getCenter()
	{
		javafx.scene.shape.Rectangle r = getRectangleShape();
		return new Point2D(r.getX() + r.getWidth() / 2.0, r.getY() + r.getHeight() / 2.0);
	}

	/**
	 * Width of this figure
	 * @return the width of this figure
	 */
	@Override
	public double width()
	{
		return getRectangleShape().getWidth();
	}

	/**
	 * Height of this figure
	 * @return the width of this figure
	 */
	@Override
	public double height()
	{
		return getRectangleShape().getHeight();
	}

	/**
	 * Top left corner of this figure
	 * @return the top left {@link Point2D} of this figure
	 */
	@Override
	public Point2D topLeft()
	{
		javafx.scene.shape.Rectangle r = getRectangleShape();
		return new Point2D(r.getX(), r.getY());
	}

	/**
	 * Bottom right corner of this figure
	 * @return the bottom right {@link Point2D} of this figure
	 */
	@Override
	public Point2D bottomRight()
	{
		javafx.scene.shape.Rectangle r = getRectangleShape();
		return new Point2D(r.getX() + r.getWidth(), r.getY() + r.getHeight());
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
		 * Note: since This class is also named Rectangle we need to explicitely
		 * use "new javafx.scene.shape.Rectangle(...)" here
		 */
		shape = new javafx.scene.shape.Rectangle(x, y, 0.0, 0.0);
		applyParameters();
	}

	/**
	 * Sets the last point of this figure.
	 * Sets the width/height of this Rectangle based on the distance between center and
	 * the provided point
	 * @param lastPoint the point used to set this Rectangle's dimension
	 */
	@Override
	public void setLastPoint(Point2D lastPoint)
	{
	javafx.scene.shape.Rectangle r = getRectangleShape();
	r.setWidth(Math.abs(lastPoint.getX() - r.getX()));
    r.setHeight(Math.abs(lastPoint.getY() - r.getY()));
	}

	/**
	 * Creates a copy of this Rectangle (with the same name and instance number)
	 * @return A distinct copy of this Rectangle
	 */
	@Override
	public Figure clone()
	{
		return new Rectangle(this);
	}

	/**
	 * Compare this Rectangle to another figure
	 * @return true if the other figure is also an Rectangle with the same
	 * position and size (with {@link Figure#threshold}), false otherwise.
	 * Other parameters, such as {@link Figure#fillColor},
	 * {@link Figure#edgeColor}, {@link Figure#lineType},
	 * {@link Figure#lineWidth}, and transformations
	 * are checked in {@link Figure#equals(Object)}
	 */
	@Override
	protected boolean equals(Figure figure)
	{
		if (!(figure instanceof Rectangle))
		{
			return false;
		}

		Rectangle Rectangle = (Rectangle) figure;

		if (Math.abs(getCenter().distance(Rectangle.getCenter())) > threshold)
		{
			return false;
		}

		if (Math.abs(width() - Rectangle.width()) > threshold)
		{
			return false;
		}

		if (Math.abs(height() - Rectangle.height()) > threshold)
		{
			return false;
		}

		return true;
	}

	/**
	 * Hash code for Rectangles.
	 * uses super.hashCode then appends
	 * <ul>
	 * 	<li>FigureType.Rectangle.hashCode()</li>
	 * 	<li>hashCode for width</li>
	 * 	<li>hashCode for height</li>
	 * </ul>
	 * @return a hashCode based on super.hashCode,
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + FigureType.RECTANGLE.hashCode();
		javafx.scene.shape.Rectangle Rectangle = getRectangleShape();
		long temp = Double.doubleToLongBits(Rectangle.getWidth());
		result = (result * prime) + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(Rectangle.getHeight());
		result = (result * prime) + (int) (temp ^ (temp >>> 32));
		return result;
	}
}
