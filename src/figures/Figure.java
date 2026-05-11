package figures;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import figures.enums.LineType;
import history.Prototype;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import logger.LoggerFactory;
import utils.ColorFactory;

/**
 * Abstract Base class for all figures.
 * Implements:
 * <ul>
 * <li>{@link Prototype}<Figure> in order to publicly clone itself (useful in
 * undo / redo transactions)</li>
 * </ul>
 * Contains:
 * <ul>
 * 	<li>a {@link #root} {@link Group} containing both the {@link #shape} to draw
 * 	and the {@link #selectionRectangle} indicating if this figure is selected.
 * 	The {@link #root} can be added to the drawing {@link Pane}</li>
 * 	<li>a {@link #shape} {@link Shape} representing this figure to draw</li>
 * 	<li>a {@link #selectionRectangle} {@link Rectangle} representing this
 * 	figure's selection state</li>
 * 	<li>a optional {@link #fillColor} {@link Color} to apply on the
 * 	{@link #shape} (if {@link #fillColor} is empty, then {@link #edgeColor} shold
 * 	not be empty)</li>
 * 	<li>an optional {@link #edgeColor} {@link Color} to apply on the
 * 	{@link #shape} (if {@link #edgeColor} is empty then {@link #fillColor} should
 * 	not be empty)</li>
 * 	<li>a {@link #lineType} {@link LineType} to select the kind of edge to apply
 * 	on the {@link #shape}</li>
 * 	<li>a {@link #lineWidth} to select the edge width of the {@link #shape}</li>
 * 	<li>a {@link #selected} flag indicating this figure is selected (also
 * 	triggering the addition of {@link #selectionRectangle} to the
 * 	{@link #root})</li>
 * 	<li>a {@link #logger} {@link Logger} to issue messages</li>
 * 	<li>a {@link #threshold} used to compare numeric values or distances in
 * 	sub-classes {@link #equals(Figure)} methods</li>
 * </ul>
 * @author davidroussel
 */
public abstract class Figure implements Prototype<Figure>
{
	/**
	 * Our figures are represented in JavaFX {@link javafx.scene.Scene} graph by
	 * {@link #shape} and also (whenever a figure
	 * is selected) by {@link #selectionRectangle} indicating this shape is
	 * currently selected. Both of them need to stick togeteher through all
	 * transformations (translate, scale, rotate). The simplest way do to it is
	 * to group both of them in a {@link Group} node that can be added to JavaFX
	 * Scene graph. When a figure is selected {@link #selectionRectangle} is
	 * added to the group, and removed when the figure is not selected anymore.
	 */
	protected Group root;

	/**
	 * The shape to draw for this figure (contained in {@link #root})
	 * @implNote All subclasses must enforce non null {@link #shape}
	 * @implNote {@link Shape} and all other {@link javafx.scene.Node}s do not
	 * implement {@link Object#equals(Object)} and {@link Object#hashCode()}
	 * methods and are therefore non comparable in terms of equality
	 */
	protected Shape shape;

	/**
	 * The JavaFX {@link Rectangle} representing the current selection state of
	 * this figure (contained in {@link #root}).
	 */
	protected Rectangle selectionRectangle;

	/**
	 * The optional fill color for this figure.
	 * If this figure has no fill Color then this optional is empty.
	 * However if this fill Color is empty then the {@link #edgeColor} shall not
	 * be empty as well (and vice versa).
	 * @implSpec {@link #fillColor} can NOT be null.
	 */
	protected Optional<Color> fillColor;

	/**
	 * The optional edge color for this figure.
	 * If this figure has no edge Color then this optional is empty.
	 * However if this edge Color is empty then the {@link #fillColor} shall not
	 * be empty as well (and vice versa)
	 * @implSpec {@link #edgeColor} can NOT be null.
	 */
	protected Optional<Color> edgeColor;

	/**
	 * The line type of the edge of this figure {@link LineType#SOLID},
	 * {@link LineType#DASHED} or {@link LineType#NONE}
	 */
	protected LineType lineType;

	/**
	 * The line width of the edge of this figure
	 */
	protected double lineWidth;

	/**
	 * Instance number of this figure.
	 * To be set in every sub-classes where the first instance of each figure
	 * is numbered 0 and subsequent instances uses growing numbers
	 */
	protected int instanceNumber;

	/**
	 * Flag indicating this figure is currently selected
	 */
	protected boolean selected;

	/**
	 * Logger to display messages
	 */
	protected Logger logger;

	/**
	 * Minimum threshold to compare figures attributes such as distances, width,
	 * heights, etc. to be used in {@link #equals(Figure)} methods of sub-classes.
	 */
	public final static double threshold = 1e-6;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Valued constructor (to be called in subclasses)
	 * @param fillColor the fill color (or null if there is no fill color).
	 * The fill color set in this figure shall be set from {@link ColorFactory}.
	 * If provided fill color is null then internal {@link #shape} fill shall be
	 * set to {@link Color#TRANSPARENT}.
	 * @param edgeColor the edge color (or null if there is no edge color)
	 * The edge color set in this figure shall be set from {@link ColorFactory}.
	 * If provided edgeColor is null {@link #shape} stroke color can still be
	 * set to {@link Color#TRANSPARENT}.
	 * @param lineType line type (Either {@link LineType#SOLID},
	 * {@link LineType#DASHED} or {@link LineType#NONE}).
	 * @param lineWidth line width of this figure.
	 * @param parentLogger a parent logger used to initialize the current logger
	 * @throws IllegalStateException if we try to set both fillColor and
	 * edgecolor as nulls
	 */
	protected Figure(Color fillColor,
	                 Color edgeColor,
	                 LineType lineType,
	                 double lineWidth,
	                 Logger parentLogger)
	    throws IllegalStateException
	{
		logger = LoggerFactory.getParentLogger(getClass(),
		                                       parentLogger,
		                                       (parentLogger == null ?
		                                    	Level.INFO : null)); // null level to inherit parent logger's level

		this.fillColor = (fillColor != null ?
			Optional.of(ColorFactory.getColor(fillColor)) :
			Optional.empty());
		if (!this.fillColor.isPresent() && (edgeColor == null))
		{
			String message = "both fill & edge are null";
//			logger.severe(message);
			throw new IllegalStateException(message);
		}
		this.edgeColor = (edgeColor != null ?
			Optional.of(ColorFactory.getColor(edgeColor)) :
			Optional.empty());
		this.lineType = lineType;
		this.lineWidth = Math.abs(lineWidth);

		root = new Group();
		shape = null;	// Must be set in sub-classes and added to #root
		selectionRectangle = null;	// Set in #setSelected
		// instanceNumber shall be set in sub-classes constructors
		selected = false;
	}

	/**
	 * Copy constructor (to be called in subclasses)
	 * Creates a distinct copy of the provided figure (with same
	 * {@link #instanceNumber})
	 * @param figure the figure to copy
	 * @implSpec CAUTION figure.shape can NOT be directly copied to this.shape
	 * as this.shape should be added to {@link #root}'s children
	 * {@link Group#getChildren()} which will change this.shape's parent and
	 * therefore mess up the currently rendered JavaFX scene graph leading
	 * to the disappeanace of this node from the scene! This figure needs a
	 * DISTINCT instance of #shape containing the same characteristics as
	 * figure.shape which can only be performed in sub-classes copy construtors.
	 * @throws NullPointerException if either provided figure or provided
	 * figure's shape is null
	 */
	protected Figure(Figure figure) throws NullPointerException
	{
		/*
		 * If the provided figure is null this will trigger a NullPointerException
		 * Note: the provided figure is supposed to be in a consistant state:
		 * Meaning it can not have both edge and fill colors as nulls.
		 */
		this(figure.getFillColor(),
		     figure.getEdgeColor(),
		     figure.lineType,
		     figure.lineWidth,
		     figure.logger);
		instanceNumber = figure.instanceNumber;
		if (figure.shape == null)
		{
			String message = "null provided shape";
			logger.severe(message);
			throw new NullPointerException(message);
		}

		/*
		 * If the copied figure has gone through translation, rotation
		 * and scale these need to be transfered to this new group
		 */
		root.setTranslateX(figure.root.getTranslateX());
		root.setTranslateY(figure.root.getTranslateY());
		root.setRotate(figure.root.getRotate());
		root.setScaleX(figure.root.getScaleX());
		root.setScaleY(figure.root.getScaleY());

		/*
		 * CAUTION : figure.shape can NOT be directly transfered to this.shape
		 * such as shape = figure.shape;
		 * As this.shape will be added to this #root's children which will change
		 * the transfered shape's parent and mess up the current JavaFX scene
		 * graph.
		 * So this.shape need to be a DISTINCT shape from figure.shape with the
		 * same characteristics but it can't be done in Abstract class Figure.
		 * This needs to be performed in every sub-classes copy constructors
		 * Then the newly created shape needs to be
		 * 	- added to root's children
		 * 	- #setSelected with figure.selected state
		 */
	}

	// -------------------------------------------------------------------------
	// Accessors and Mutators
	// -------------------------------------------------------------------------

	/**
	 * Internal {@link #shape} accessor
	 * @return the JavaFX shape of this figure
	 * @implNote Migh be replaced by getGroup
	 */
	public Shape getShape()
	{
		return shape;
	}

	/**
	 * Internal {@link #selectionRectangle} accessor
	 * @return the JavaFX selection rectangle of this figure
	 * @implNote Migh be replaced by getGroup
	 */
	public Rectangle getSelectionRectangle()
	{
		return selectionRectangle;
	}

	/**
	 * Internal {@link #root} {@link Group} accessor
	 * @return the group of this figure containing the {@link #shape} and
	 * eventually the {@link #selectionRectangle} of this figure whenever this
	 * figure is selected
	 */
	public Group getRoot()
	{
		return root;
	}

	/**
	 * Indicates if this figure has a fill color
	 * @return true if {@link #fillColor} has data
	 */
	public boolean hasFillColor()
	{
		// DONE 001 Figure#hasFillColor ...
		return fillColor.isPresent();
	}

	/**
	 * Fill Color accessor
	 * @return the fillColor or null if there is no fill color
	 */
	public Color getFillColor()
	{
		// DONE 002 Figure#getFillColor
		return fillColor.orElse(null);
	}

	/**
	 * Fill color mutator.
	 * Sets both {@link #fillColor} and {@link #shape} with
	 * {@link Shape#setFill(javafx.scene.paint.Paint)}
	 * @param fillColor the new fillColor to set. If provided fillColor is null
	 * then the {@link #fillColor} shall be set to empty
	 * and internal shape fill to {@link Color#TRANSPARENT}
	 * @implNote Only one of {@link #fillColor} and {@link #edgeColor} can be
	 * empty
	 * @throws IllegalStateException if we try to set a null fillColor when
	 * internal {@link #edgeColor} is already empty
	 */
	public void setFillColor(Color fillColor) throws IllegalStateException
	{
		// DONE 003 Figure#setFillColor ...
		Color colorToSet = Color.TRANSPARENT;
		
		if (fillColor == null && edgeColor.isEmpty()) {
			throw new IllegalStateException();
		}
		
		if (fillColor != null) {
			this.fillColor = Optional.of(ColorFactory.getColor(fillColor));
		} else {
			this.fillColor = Optional.empty();
		}

		if (shape != null)
		{
			colorToSet = this.fillColor.orElse(Color.TRANSPARENT);
			shape.setFill(colorToSet);
		}
		else
		{
			logger.warning("can't set Fill Color: null shape");
		}
	}

	/**
	 * Indicates if this figure has an edge color
	 * @return true if {@link #edgeColor} has data
	 */
	public boolean hasEdgeColor()
	{
		// DONE 004 Figure#hasEdgeColor ...
		return edgeColor.isPresent();
	}

	/**
	 * Edge color accessor
	 * @return the edgeColor or null if there is no edge color
	 */
	public Color getEdgeColor()
	{
		// DONE 005 Figure#getEdgeColor ...
		return edgeColor.orElse(null);
	}

	/**
	 * Edge color mutator
	 * Sets both {@link #edgeColor} and {@link #shape} with
	 * {@link Shape#setStroke(javafx.scene.paint.Paint)}
	 * @param edgeColor the new edgeColor to set. If provided edgeColor is null
	 * then the {@link #edgeColor} shall be set to empty and internal shape
	 * color to {@link Color#TRANSPARENT}.
	 * @implNote Only one of {@link #edgeColor} and {@link #fillColor} can be
	 * empty.
	 * @throws IllegalStateException if we try to set a null edgeColor when
	 * internal {@link #fillColor} is already empty
	 */
	public void setEdgeColor(Color edgeColor) throws IllegalStateException
	{
		// DONE 006 Figure#setEdgeColor ...
		Color colorToSet = Color.TRANSPARENT;

		if (edgeColor == null && fillColor.isEmpty()) {
			throw new IllegalStateException("setEdgeColor : edgeColor and fillColor can't be null at the same time");
		}

		if (edgeColor != null) {
			this.edgeColor = Optional.of(ColorFactory.getColor(edgeColor));
		} else {
			this.edgeColor = Optional.empty();
		}

		if (shape != null)
		{
			colorToSet = this.edgeColor.orElse(Color.TRANSPARENT);
			shape.setStroke(colorToSet);
		}
		else
		{
			logger.warning("can't set Edge Color: null shape");
		}
	}

	/**
	 * Get the line type
	 * @return the lineType
	 */
	public LineType getLineType()
	{
		return lineType;
	}

	/**
	 * Line Type setter.
	 * Sets both {@link #lineType} and {@link #shape} with
	 * {@link Shape#getStrokeDashArray()}
	 * @param lineType the lineType to set
	 */
	public void setLineType(LineType lineType)
	{
		this.lineType = lineType;

		/*
		 * DONE 007 Figure#setLineType ...
		 * 	- if NONE set internal shape stroke to Color#TRANSPARENT
		 * 	- if DASHED or SOLID then clears internal shape StrokeDashArray
		 * 	- if DASHED then setup internal shape StrokeDashArray
		 */
		if (shape != null)
		{
			ObservableList<Double> dashArray = shape.getStrokeDashArray();
			switch(lineType)
			{
				case NONE:
					shape.setStroke(Color.TRANSPARENT);
					break;
				case SOLID:
					shape.setStroke(edgeColor.orElse(Color.TRANSPARENT));
					break;
				case DASHED:
					shape.setStroke(edgeColor.orElse(Color.TRANSPARENT));
					dashArray.addAll(lineWidth, lineWidth);
					break;
			}
		}
		else
		{
			logger.warning("Cant't set line type: null shape");
		}
	}

	/**
	 * Line width
	 * @return the lineWidth
	 */
	public double getLineWidth()
	{
		return lineWidth;
	}

	/**
	 * Line width setter
	 * @param lineWidth the lineWidth to set
	 */
	public void setLineWidth(double lineWidth)
	{
		this.lineWidth = lineWidth;

		// DONE 008 Figure#setLineWidth ...
		if (shape != null)
		{
			shape.setStrokeWidth(this.lineWidth);
			if (lineType == LineType.DASHED) {
				setLineType(LineType.DASHED);
			}
		}
		else
		{
			logger.warning("can't set Line Width: null shape");
		}
	}

	/**
	 * Figure's instance number
	 * @return the instanceNumber
	 */
	public int getInstanceNumber()
	{
		return instanceNumber;
	}

	/**
	 * Indicates if this figure is currently selected
	 * @return the selected status of this figure
	 */
	public boolean isSelected()
	{
		return selected;
	}

	/**
	 * Sets the selection status of this figure (IFF the provided new selected
	 * state differs from internal selected state)
	 * @param selected the selected status to set
	 */
	public void setSelected(boolean selected)
	{
		// logger.info(this + " set selected = " + (selected ? "true" : "false"));
		if (this.selected == selected)
		{
			return;
		}
		this.selected = selected;
		if (shape != null)
		{
			/*
			 * DONE 009 Figure#setSelected ...
			 * 	- if selected then add a new JavaFX Rectangle to #root: #selectionRectangle
			 * 		- location from topLeftPoint
			 * 		- size from width() & height()
			 * 		- dashed gray stroke
			 * 		- transparent fill
			 * 	- if not selected then remove #selectionRectangle from #root
			 */
			if (selected)	// #selectionRectangle should be created
			{
				selectionRectangle = new Rectangle();
				selectionRectangle.setFill(ColorFactory.getColor(Color.TRANSPARENT));
				selectionRectangle.setStroke(Color.GRAY);
				selectionRectangle.getStrokeDashArray().addAll(5.0, 5.0);
				updateSelectionFrame();
				root.getChildren().add(selectionRectangle);
			}
			else			// #selectionRectangle should be destroyed
			{
				if (selectionRectangle != null) {
					root.getChildren().remove(selectionRectangle);
					selectionRectangle = null;
            	}
			}
		}
		else
		{
			logger.warning("can't change selected state: null shape");
		}
	}

	/**
	 * Center Point of this figure (default implementation)
	 * @return the center point of this figure
	 */
	public Point2D getCenter()
	{
		final Bounds bounds = shape.getBoundsInParent();
		return new Point2D(bounds.getCenterX(), bounds.getCenterY());
	}

	/**
	 * Width of this figure (default implementation)
	 * @return the width of this figure
	 */
	public double width()
	{
		final Bounds bounds = shape.getBoundsInParent();
		return bounds.getWidth();
	}

	/**
	 * Height of this figure (default implementation)
	 * @return the width of this figure
	 */
	public double height()
	{
		final Bounds bounds = shape.getBoundsInParent();
		return bounds.getHeight();
	}

	/**
	 * Top left corner of this figure (default implementation)
	 * @return the top left {@link Point2D} of this figure
	 * @apiNote This default implementation doesn't work for Rectangles
	 */
	public Point2D topLeft()
	{
		final Bounds bounds = shape.getBoundsInParent();
		return new Point2D(bounds.getMinX(),
		                   bounds.getMinY());
	}

	/**
	 * Bottom right corner of this figure (default implementation)
	 * @return the bottom right {@link Point2D} of this figure
	 * @apiNote This default implementation doesn't work for Rectangles
	 */
	public Point2D bottomRight()
	{
		final Bounds bounds = shape.getBoundsInParent();
		return new Point2D(bounds.getMaxX(),
		                   bounds.getMaxY());
	}

	// -------------------------------------------------------------------------
	// Operations on Figures
	// -------------------------------------------------------------------------

	/**
	 * Apply drawing parameters directly on {@link #shape} without changing
	 * {@link #fillColor}, {@link #edgeColor}, {@link #lineType} and
	 * {@link #lineWidth}.
	 * @param fillColor the fill color to apply (shall never be null)
	 * @param edgeColor the edge color to apply (shall never be null)
	 * @param lineType the line type to apply
	 * @param lineWidth the line width to apply
	 * @throws NullPointerException if either provided fillColor or edgeColor is null
	 * @throws IllegalStateException if internal #shape is null
	 * @throws IllegalStateException if lineWidth is negative.
	 */
	protected void applyParameters(Color fillColor,
	                               Color edgeColor,
	                               LineType lineType,
	                               double lineWidth) throws IllegalStateException
	{
		if (shape == null)
		{
			throw new IllegalStateException("null #shape");
		}
		if (fillColor == null)
		{
			throw new IllegalStateException("provided fill color is null");
		}
		if (edgeColor == null)
		{
			throw new IllegalStateException("provided edge color is null");
		}
		if (lineWidth < 0.0)
		{
			throw new IllegalStateException("provided line width is negative or null");
		}

		/*
		 * Set #shape's
		 * 	- Fill based on provided fillColor
		 * 	- Stroke
		 * 	- StrokeLineJoin
		 * 	- StrokeLineCap
		 * 	- StrokeDashArray
		 * 	- StrokeWidth
		 */
		Color transparent = Color.TRANSPARENT;
		shape.setFill(fillColor);
		shape.setStroke(lineType == LineType.NONE ? transparent : edgeColor);
		shape.setStrokeLineJoin(StrokeLineJoin.ROUND);
		shape.setStrokeLineCap(StrokeLineCap.ROUND);
		ObservableList<Double> strokeDashArray = shape.getStrokeDashArray();
		switch (lineType)
		{
			case SOLID:
			case NONE:
				strokeDashArray.clear();
				break;
			case DASHED:
				strokeDashArray.clear();
				strokeDashArray.addAll(lineWidth, lineWidth);
			default:
				break;
		}
		shape.setStrokeWidth(lineWidth);
	}

	/**
	 * Apply Fill and Edge Color, LineType and Line width to {@link #shape}
	 * @see #applyParameters(Color, Color, LineType, double)
	 */
	protected void applyParameters()
	{
		/*
		 * NOTE:
		 * if edgeColor is empty then
		 * 	- lineType is supposed to be LineType.NONE
		 */
		applyParameters(fillColor.orElse(Color.TRANSPARENT),
		                edgeColor.orElse(Color.TRANSPARENT),
		                lineType,
		                lineWidth);
	}

	/**
	 * Update {@link #selectionRectangle} iff non null
	 * @see #topLeft()
	 * @see #width()
	 * @see #height()
	 */
	public void updateSelectionFrame()
	{
		if (selectionRectangle != null)
		{
			Point2D topLeftPoint = topLeft();
			selectionRectangle.setX(topLeftPoint.getX());
			selectionRectangle.setY(topLeftPoint.getY());
			selectionRectangle.setWidth(width());
			selectionRectangle.setHeight(height());
		}
	}

	/**
	 * Set figure's translation to (dx, dy)
	 * @param x the x translation value
	 * @param y the y translation value
	 * @return a reference to the translated figure
	 */
	public Figure setTranslate(double x, double y)
	{
		root.setTranslateX(x);
		root.setTranslateY(y);
		return this;
	}

	/**
	 * Set figure's rotation to (angle)
	 * @param angle the angle to rotate figure about
	 * @return a reference to the rotated figure
	 */
	public Figure setRotate(double angle)
	{
		root.setRotate(angle);
		return this;
	}

	/**
	 * Set figure's scale to (sx, sy)
	 * @param sx the x scale value
	 * @param sy the y scale value
	 * @return a reference to the scaled figure
	 */
	public Figure setScale(double sx, double sy)
	{
		root.setScaleX(sx);
		root.setScaleY(sy);
		return this;
	}

	/**
	 * Checks if the bounds of the current figure overlap the bounds of the
	 * provided figure.
	 * @param figure the figure to check
	 * @return true if the bounds of the current figure overlaps the bounds
	 * of the provided figure
	 * @implSpec Use {@link #root}'s bounds in parent to check for intersections
	 * @see javafx.scene.Node#getBoundsInParent()
	 * @see Bounds#intersects(Bounds)
	 */
	public boolean overlaps(Figure figure)
	{
		// DONE 010 Figure#overlaps ...
		if (figure == null) return false;
		
		Bounds myBounds = this.root.getBoundsInParent();
		Bounds otherBounds = figure.getRoot().getBoundsInParent();
		
		return myBounds.intersects(otherBounds);
	}

	// -------------------------------------------------------------------------
	// Prototype<Figure> methods implementation
	// -------------------------------------------------------------------------
	/**
	 * Creates a copy of this figure (with the same name and instance number)
	 * @return A distinct copy of this figure
	 * @implNote Since this class is abstract, this method has to be implemented
	 * in sub-classes
	 * @implSpec this method is required in order to create copies of figures
	 * to create a copy of the current {@link Drawing} state in
	 * {@link history.HistoryManager}
	 */
	@Override
	public abstract Figure clone();

	// -------------------------------------------------------------------------
	// Object methods overloads (to be used in sub-classes)
	// -------------------------------------------------------------------------
	/**
	 * Compares this figure with another one.
	 * @param figure the other figure to compare with
	 * @return true if the other figure is not null, has the same class
	 * and features the same content (except for {@link #fillColor},
	 * {@link #edgeColor}, {@link #lineType} and {@link #lineWidth} which are
	 * checked in {@link #equals(Object)} and {@link #instanceNumber} and
	 * {@link #selected} which are not taken into account)
	 */
	protected abstract boolean equals(Figure figure);

	/**
	 * Base algorithm to compare with another object
	 * @return true if the other figure is not null, has the same class
	 * and features the same {@link #fillColor}, {@link #edgeColor},
	 * {@link #lineType} and {@link #lineWidth} (± {@link #threshold})
	 * and {@link #equals(Figure)} returns true.
	 * @see #equals(Figure)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}

		if (obj == this)
		{
			return true;
		}
		/*
		 * DONE 011 Figure#equals(Object) ...
		 * - Compares
		 * 	- Class
		 * 	- equals(Figure)
		 * 	- fillColor
		 * 	- edgeColor
		 * 	- lineType
		 * 	- lineWidth
		 * 	- root's
		 * 		- translateX
		 * 		- translateY
		 * 		- Rotate
		 * 		- scaleX
		 * 		- scaleY
		 * Number attributes should be compared up to "threshold" precision
		 * Object attributes should be compared using equals(Object) method
		 */
		if (obj.getClass() != this.getClass()) {
			return false;
		}
		// on cast l'obj à comparer en figure vu qu'il est de la même classe
		Figure other = (Figure) obj;

		if (!this.fillColor.equals(other.fillColor)) return false;
		if (!this.edgeColor.equals(other.edgeColor)) return false;
		if (this.lineType != other.lineType) return false;
 
		if(Math.abs(this.lineWidth - other.lineWidth) > threshold) return false; 
		

		if(Math.abs(this.root.getTranslateX() - other.root.getTranslateX()) > threshold) return false; 
		if(Math.abs(this.root.getTranslateY() - other.root.getTranslateY()) > threshold) return false; 
		if(Math.abs(this.root.getRotate() - other.root.getRotate()) > threshold) return false; 
		if(Math.abs(this.root.getScaleX() - other.root.getScaleX()) > threshold) return false; 
		if(Math.abs(this.root.getScaleY() - other.root.getScaleY()) > threshold) return false; 

		return equals(other);
	}

	/**
	 * <b>Partial</b> hashCode based solely on {@link #fillColor},
	 * {@link #edgeColor}, {@link #lineType}, {@link #lineWidth} and
	 * {@link #root} transformations.
	 * Meaning this method can NOT be used alone to compute figure hashCode: It
	 * has to be used in sub-classes overloads of this method.
	 * @return a <b>Partial</b> hashCode based on the fields declared in this
	 * class
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((fillColor == null) ? 0 : fillColor.hashCode());
		result = (prime * result) + ((edgeColor == null) ? 0 : edgeColor.hashCode());
		result = (prime * result) + ((lineType == null) ? 0 : lineType.hashCode());
		long temp;
		temp = Double.doubleToLongBits(lineWidth);
		result = (prime * result) + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(root.getTranslateX());
		result = (prime * result) + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(root.getTranslateY());
		result = (prime * result) + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(root.getRotate());
		result = (prime * result) + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(root.getScaleX());
		result = (prime * result) + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(root.getScaleY());
		result = (prime * result) + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * String representation of this figure.
	 * @return a new String containg the actual class name and instance number
	 * separated by a single space (e.g. "Circle 2")
	 */
	@Override
	public String toString()
	{
		return new String(getClass().getSimpleName() + " " + instanceNumber);
	}
}
