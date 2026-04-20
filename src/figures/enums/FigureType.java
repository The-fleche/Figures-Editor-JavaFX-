package figures.enums;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import figures.Drawing;
import figures.Ellipse;
import figures.Figure;
import history.HistoryManager;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import tools.creation.AbstractCreationTool;
import tools.creation.RectangularShapeCreationTool;

/**
 * Enum type for all different kind of figures.
 * This enum also contains factory methods to:
 * <ul>
 * 	<li>build a new figure with {@link #getFigure(Color, Color, LineType, double, Logger, double, double)}</li>
 * 	<li>build a new CreationListener for a kinfd of figure with
 * 	{@link #getCreationTool(Pane, Drawing, Label, HistoryManager, Logger)}</li>
 * </ul>
 * @author davidroussel
 */
public enum FigureType
{
	/**
	 * Circle figure type
	 */
	CIRCLE,
	/**
	 * Ellipse figure type
	 */
	ELLIPSE,
	/**
	 * Rectangle figure type
	 */
	RECTANGLE,
	/**
	 * When the figure results from intersecting two figures
	 */
	INTERSECTION,
	/**
	 * When the figure results from subtraction one figure to another
	 */
	SUBTRACTION,
	/**
	 * When the figure results from the union of two figures
	 */
	UNION;

	/**
	 * Get an instance of the {@link Figure} corresponding to this enum's
	 * value
	 * @param fillColor the fill color to set on the new figure
	 * @param edgeColor the edge color to set on the new figure
	 * @param lineType the line type to set on the new figure
	 * @param lineWidth the line width to set on the new figure
	 * @param parentLogger the parent logger to set on the new figure (parent
	 * logger can also be used to issue debug messages in this method if needed)
	 * @param x the initial x coordinate where to create the new figure
	 * @param y the initial y coordinate where to create the new figure
	 * @return a new instance of Figure's children class corresponding to this
	 * enum's value
	 * @throws AssertionError if this enum has unexpected value
	 */
	public Figure getFigure(Color fillColor,
	                        Color edgeColor,
	                        LineType lineType,
	                        double lineWidth,
	                        Logger parentLogger,
	                        double x,
	                        double y)
	    throws AssertionError
	{
		switch (this)
		{
			case CIRCLE:
				// TODO 203 FigureType#getFigure CIRLCE case ...
				// Change this
				return null;
				// to this when Circle is ready
//				return new Circle(fillColor,
//				                  edgeColor,
//				                  lineType,
//				                  lineWidth,
//				                  parentLogger,
//				                  x,
//				                  y);
			case ELLIPSE:
				return new Ellipse(fillColor,
				                   edgeColor,
				                   lineType,
				                   lineWidth,
				                   parentLogger,
				                   x,
				                   y);
			case RECTANGLE:
				// TODO 303 FigureType#getFigure RECTANGLE case ...
				// Change this
				return null;
				// to this when Rectangle is ready
//				return new Rectangle(fillColor,
//				                     edgeColor,
//				                     lineType,
//				                     lineWidth,
//				                     parentLogger,
//				                     x,
//				                     y);
			/**
			 * All other cases INTERSECTION, SUBTRACTION, UNION are created by
			 * operating on two existing figures and have different constructors
			 */
			case INTERSECTION:
			case SUBTRACTION:
			case UNION:
				return null;
			default:
				break;
		}

		String message = "Unexpected value " + toString();
		parentLogger.severe(message);
		throw new AssertionError(message);
	}

	/**
	 * Get the Creation Tool for this particular type of Figure
	 * @param rootPane The {@link Pane} to draw in
	 * @param model the {@link Drawing} model containing figures
	 * @param tipLabel The tip {@link Label} to show in UI
	 * @param manager the history manager to provide to the returned
	 * {@link RectangularShapeCreationTool}
	 * in order to record current state before adding new Figure.
	 * @param parentLogger the parent logger to set on the new figure (parent
	 * logger can also be used to issue debug messages in this method if needed)
	 * @return a new {@link RectangularShapeCreationTool} instance adapted to
	 * this kind of Figure
	 * @throws AssertionError Whenever the value of this enum is unknown
	 */
	public AbstractCreationTool getCreationTool(Pane rootPane,
	                                            Drawing model,
	                                            Label tipLabel,
	                                            HistoryManager<Figure> manager,
	                                            Logger parentLogger)
		    throws AssertionError
	{
		switch (this)
		{
			case CIRCLE:
			case ELLIPSE:
			case RECTANGLE:
				/*
				 * Circles, Ellipses and Rectangle all require 2 steps creation
				 * tool:
				 * 	- 1 Click to initiate figure of size 0 at clicked point
				 * 	- 2 Drag mouse to set figure's size
				 * 	- and Release to end figure
				 */
				return new RectangularShapeCreationTool(rootPane,
				                                        model,
				                                        tipLabel,
				                                        manager,
				                                        parentLogger);
				/*
				 * Other figuers might require more steps:
				 * 	- Rounded Rectangles
				 * 	- Polygons
				 * 	- NGons
				 * 	- Stars
				 */
			default:
				break;
		}

		String message = "Unexpected value " + toString();
		parentLogger.severe(message);
		throw new AssertionError(message);
	}

	/*
	 * Note: Enum.class provides default final hashCode and equals methods,
	 * so it's no use trying to overload them
	 */

	/**
	 * String representation
	 * @return A string representation of this enum
	 * @throws AssertionError if this value is outside enum values
	 */
	@Override
	public String toString() throws AssertionError
	{
		switch (this)
		{
			case CIRCLE:
				return new String("Circle");
			case ELLIPSE:
				return new String("Ellipse");
			case RECTANGLE:
				return new String("Rectangle");
			default:
				break;
		}

		throw new AssertionError(getClass().getSimpleName()
		    + ".toString() unknown assertion: " + this);
	}

	/**
	 * Converts Figure to FigureType
	 * @param f the figure to investigate
	 * @return the type of figure corresponding to provided figure
	 * @throws AssertionError if {@link FigureType} can't be determined from
	 * provided {@link Figure}
	 */
	public static FigureType fromFigure(Figure f)
	{
		Class<? extends Figure> type = f.getClass();

		// TODO 205 FigureType#fromFigure uncomment when Circle is ready
//		if (type == Circle.class)
//		{
//			return CIRCLE;
//		}
		if (type == Ellipse.class)
		{
			return ELLIPSE;
		}
		// TODO 305 FigureType#fromFigure uncomment when Rectangle is ready
//		if (type == Rectangle.class)
//		{
//			return RECTANGLE;
//		}

		// TODO 701 FigureType#fromFigure uncomment when OperationFigure is ready
//		if (type == OperationFigure.class)
//		{
//			OperationFigure opFigure = (OperationFigure) f;
//			OperationType opType = opFigure.getOperationType();
//			switch (opType)
//			{
//				case INTERSECTION:
//					return FigureType.INTERSECTION;
//				case SUBTRACTION:
//					return FigureType.SUBTRACTION;
//				case UNION:
//					return FigureType.UNION;
//			}
//		}

		throw new AssertionError(FigureType.class.getSimpleName()
		    + ".fromFigure(" + f.getClass().getSimpleName()
		    + ") unknown Figure class: " + type);
	}

	/**
	 * Creates a collection of all possible Figure types.
	 * Can be used to fill a {@link javafx.scene.control.ComboBox}
	 * @return a collection of all possible Figure types
	 * @see application.Controller#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	public static Collection<FigureType> all()
	{
		Collection<FigureType> list = new ArrayList<>();
		list.add(CIRCLE);
		list.add(ELLIPSE);
		list.add(RECTANGLE);
		return list;
	}
}
