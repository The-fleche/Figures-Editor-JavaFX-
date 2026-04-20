package application.cells;

import java.util.logging.Level;
import java.util.logging.Logger;

import figures.Figure;
import figures.enums.FigureType;
import javafx.scene.image.Image;
import logger.LoggerFactory;
import utils.IconFactory;

/**
 * Icon Sub-Factory dedicated to Figure Types icons.
 * Suitable for {@link FigureCellController}s and other controllers
 * @author davidroussel
 */
public class FigureIconsFactory
{
	/**
	 * Circle figure icon
	 */
	private static Image circleIcon = IconFactory.getIcon("Circle");

	/**
	 * Ellipse figure icon
	 */
	private static Image ellipseIcon = IconFactory.getIcon("Ellipse");

	/**
	 * Rectangle figure icon
	 */
	private static Image rectangleIcon = IconFactory.getIcon("Rectangle");

	/**
	 * Intersection Operation figure icon
	 */
	private static Image intersectionIcon = IconFactory.getIcon("intersect");

	/**
	 * Subtract Operation figure icon
	 */
	private static Image subctractIcon = IconFactory.getIcon("subtract");

	/**
	 * Union Operation figure icon
	 */
	private static Image unionIcon = IconFactory.getIcon("merge");

	/**
	 * Logger to use
	 */
	private static Logger logger = LoggerFactory.getParentLogger(FigureIconsFactory.class,
	                                                             IconFactory.getLogger(),
	                                                             (IconFactory.getLogger() == null ?
	                                                              Level.INFO : null)); // null level to inherits parent logger's level

	/**
	 * Retrieve icon from Figure instance
	 * @param figure the figure to retrieve icon for
	 * @return the corresponding icon image
	 * @throws NullPointerException if provided figure is null and {@link FigureType}
	 * can't be determined
	 */
	public static Image getIconFromInstance(Figure figure)
	{
		if (figure == null)
		{
			String message = "null instance";
			logger.severe(message);
			throw new NullPointerException(message);
		}

		return getIconFromType(FigureType.fromFigure(figure));
	}

	/**
	 * Retrieve icon image from figure Type
	 * @param type the type of figure
	 * @return the corresponding icon image
	 * @throws IllegalArgumentException if type is out of {@link FigureType} enum values
	 */
	public static Image getIconFromType(FigureType type) throws IllegalArgumentException
	{
		switch (type)
		{
			case CIRCLE:
			{
				return circleIcon;
			}
			case ELLIPSE:
			{
				return ellipseIcon;
			}
			case RECTANGLE:
			{
				return rectangleIcon;
			}
			case INTERSECTION:
			{
				return intersectionIcon;
			}
			case SUBTRACTION:
			{
				return subctractIcon;
			}
			case UNION:
			{
				return unionIcon;
			}
			default:
				String message = "Unexpected value: " + type.toString();
				logger.severe(message);
				throw new IllegalArgumentException(message);
		}
	}
}
