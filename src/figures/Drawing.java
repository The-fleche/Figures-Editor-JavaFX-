package figures;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import figures.enums.FigureType;
import figures.enums.LineType;
import history.Memento;
import history.Originator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import logger.LoggerFactory;

/**
 * Drawing class contains our Data which mainly consists in a list of
 * {@link Figure}s
 * This data model is two folds:
 * <ol>
 * 	<li>It behaves as an {@link ObservableList} of {@link Figure}s.
 * 	<ul>
 * 		<li>Such a list can be bound to a ListView with
 * 		{@link javafx.scene.control.ListView#setItems(ObservableList)}</li>
 * 		<li>Any changes in this list will be notified to all
 * 		{@link javafx.collections.ListChangeListener}s registered in this list
 * 		with {@link ObservableList#addListener(javafx.collections.ListChangeListener)}
 * 		</li>
 * 	</ul>
 * 	<li>It also behaves as a {@link javafx.collections.ListChangeListener} so
 * 	that it can react to any selection changes in the {@link ListView} used to
 * 	display this model by registering itself as a listener of its selection model
 * 	with {@link ListView#getSelectionModel()}</li>
 * 	</li>
 * 	<li>It contains a reference to a {@link Pane}: {@link #root} node in which all
 * {@link Figure#shape}s can be added or removed in order to be drawn as part of
 * JavaFX Scene Graph</li>
 * </ol>
 * It is also an {@link Originator} of {@link Figure}s able to produce a
 * {@link history.Memento} of its {@link Figure}s saving its current state in
 * order to restore such a state later on.
 * @author davidroussel
 * @see javafx.collections.ObservableList
 * @see javafx.collections.ModifiableObservableListBase
 * @see history.Originator
 */
public class Drawing extends ModifiableObservableListBase<Figure>
    implements Originator<Figure>, ListChangeListener<Figure>
{
	/**
	 * The root node which will be parent to all Drawing's {@link Figure#shape}.
	 * Drawing figures will only consists in adding or removing
	 * {@link Figure#shape}s to this {@link Pane}
	 * @implSpec the index of each {@link Figure#shape} in {@link #root} should be
	 * equal to the index of each {@link Figure} in {@link #figures}
	 */
	private Pane root = null;

	/**
	 * the {@link ListView} used to show this model
	 */
	private ListView<Figure> view = null;

	/**
	 * List of figures to draw (internal synchronized figures container).
	 * In order for this class to behave as a
	 * {@link ModifiableObservableListBase},
	 * access to {@link #figures} shall be restricted to
	 * {@link #doAdd(int, Figure)},
	 * {@link #doSet(int, Figure)} and {@link #doRemove(int)} methods which take
	 * care of {@link #figures} management. Adding or removing {@link Figure}s
	 * to or from the {@link Drawing} Model are normally performed with
	 * {@link #add(Figure)}, or {@link #remove(Object)} and {@link #remove(int)}
	 * methods.
	 * @see #doAdd(int, Figure)
	 * @see #doSet(int, Figure)
	 * @see #doRemove(int)
	 * @implSpec the index of each {@link Figure} in {@link #figures} should be
	 * equal to the index of each {@link Figure#shape} in {@link #root}
	 */
	private List<Figure> figures = null;

	/**
	 * Current {@link FigureType} property to apply on new {@link Figure}s.
	 * To be bound from {@link javafx.scene.control.ComboBox#valueProperty()} for instance.
	 */
	private ObjectProperty<FigureType> figureTypeProperty = null;

	/**
	 * Property indicating {@link #fillColorProperty} should be used
	 */
	private BooleanProperty hasFillColorProperty = null;

	/**
	 * Current Fill Color property to apply on new {@link Figure}s.
	 * To be bound from {@link javafx.scene.control.ColorPicker#valueProperty()}
	 */
	private ObjectProperty<Color> fillColorProperty = null;

	/**
	 * Property indicating {@link #edgeColorProperty} should be used
	 */
	private BooleanProperty hasEdgeColorProperty = null;

	/**
	 * Current Stroke Color property to apply on new {@link Figure}s.
	 * To be bound from {@link javafx.scene.control.ColorPicker#valueProperty()}
	 */
	private ObjectProperty<Color> edgeColorProperty = null;

	/**
	 * Current {@link LineType} property to apply on new {@link Figure}s.
	 * To be bound from {@link javafx.scene.control.ComboBox#valueProperty()} of {@link LineType}
	 */
	private ObjectProperty<LineType> lineTypeProperty = null;

	/**
	 * Current Line width property to apply on new {@link Figure}s.
	 * To be bound from {@link javafx.scene.control.Spinner#getValueFactory()} which provides a
	 * {@link ObjectProperty} of {@link javafx.scene.control.SpinnerValueFactory}, the value property
	 * can then be obtained with {@link javafx.scene.control.SpinnerValueFactory#valueProperty()}.
	 */
	private ObjectProperty<Double> lineWidthProperty = null;

	/**
	 * Logger to display messages
	 */
	private Logger logger;

	/**
	 * Constructor
	 * @param root The {@link Pane} to draw all {@link Figure#shape}s in
	 * @param view The {@link ListView} showing this list so we can manage its
	 * {@link javafx.scene.control.SelectionModel} when figures are selected or
	 * deselected by clicking in the {@link #root} Pane.
	 * @param parentLogger a parent logger used to initialize the current logger
	 * @throws NullPointerException if provided {@link #root} or {@link #view}
	 * is null
	 */
	public Drawing(Pane root,
	               ListView<Figure> view,
	               Logger parentLogger) throws NullPointerException
	{
		logger = LoggerFactory.getParentLogger(getClass(),
		                                       parentLogger,
		                                       (parentLogger == null ?
		                                    	Level.INFO : null)); // null level to inherit parent logger's level
		if (root == null)
		{
			String message = getClass() + "(null root)";
			logger.severe(message);
			throw new NullPointerException(message);
		}

		this.root = root;
		figures = new Vector<Figure>();

		if (view == null)
		{
			String message = getClass() + "(null view)";
			logger.severe(message);
			throw new NullPointerException(message);
		}

		/*
		 * Setup view to
		 * 	- Allow multiple selections on #figuresListView using
		 * 	view.getSelectionModel()
		 * 	- Registering this as a ListChangeListener to the view so it can
		 * 	react on selection changes with #onChanged method using
		 * 	view.getSelectionModel().getSelectedItems()
		 */
		this.view = view;
		this.view.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.view.getSelectionModel().getSelectedItems().addListener(this);

		/*
		 * Simple properties that will be bound later to UI elements properties
		 * so that changes in UI Properties can be directly reflected here
		 * e.g.:
		 * 	- SimpleObjectProperty<Type>
		 * 	- SimpleBooleanProperty
		 */
		figureTypeProperty = new SimpleObjectProperty<FigureType>();
		hasFillColorProperty = new SimpleBooleanProperty();
		fillColorProperty = new SimpleObjectProperty<Color>();
		hasEdgeColorProperty = new SimpleBooleanProperty();
		edgeColorProperty = new SimpleObjectProperty<Color>();
		lineTypeProperty = new SimpleObjectProperty<LineType>();
		lineWidthProperty = new SimpleObjectProperty<Double>();
		lineWidthProperty.set(1.0);

		logger.info("Drawing model created");
	}

	/**
	 * Cleanup Drawing model before destruction
	 */
	@Override
	protected void finalize()
	{
		unBindProperties();
	}

	// ------------------------------------------------------------------------
	// Properties bindings
	//	- figureTypeProperty
	//	- useFillColorProperty
	//	- fillColorProperty
	//	- useEdgeColorProperty
	//	- edgeColorProperty
	//	- lineTypeProperty
	//	- lineWidthProperty
	// ------------------------------------------------------------------------

	/**
	 * Binds source property to target property (generic implementation) so that
	 * changes in source property can be reflected (unidirectionnaly) into
	 * target property.
	 * @param <T> The type of content of the property to bind
	 * @param source the source property to bind to
	 * @param target the target property to be bound
	 */
	public static <T> void bindProperty(ReadOnlyProperty<T> source,
	                                     Property<T> target)
	{
		if (target.isBound())
		{
			target.unbind();
		}
		target.bind(source);
	}

	/**
	 * Unbind {@link #figureTypeProperty}, {@link #hasFillColorProperty},
	 * {@link #fillColorProperty}, {@link #hasEdgeColorProperty},
	 * {@link #edgeColorProperty}, {@link #lineTypeProperty} and
	 * {@link #lineWidthProperty} in this model
	 */
	public void unBindProperties()
	{
		figureTypeProperty.unbind();
		hasFillColorProperty.unbind();
		fillColorProperty.unbind();
		hasEdgeColorProperty.unbind();
		edgeColorProperty.unbind();
		lineTypeProperty.unbind();
		lineWidthProperty.unbind();
	}

	// ------------------------------------------------------------------------
	// Properties accessors
	// Note: there are no Propertis mutators since these properties should be
	// unidrirectionnaly bound from UI elements
	// ------------------------------------------------------------------------
	/**
	 * Current {@link FigureType} for next {@link Figure}.
	 * A Warning message is issued if {@link #figureTypeProperty} has not been
	 * bound yet.
	 * @return the current {@link FigureType} for next {@link Figure}
	 */
	public FigureType getFigureType()
	{
		if (!figureTypeProperty.isBound())
		{
			logger.warning("Figure Type Property is not bound yet");
		}
		return figureTypeProperty.get();
	}

	/**
	 * Figure Type property accessor
	 * @return the figure type preperty
	 */
	public final ObjectProperty<FigureType> figureTypeProperty()
	{
		return figureTypeProperty;
	}

	/**
	 * Current Fill usage for next {@link Figure}.
	 * A Warning message is issued if {@link #hasFillColorProperty} has not been
	 * bound yet.
	 * @return true if Fill Color should ne used on next {@link Figure}, false otherwise
	 */
	public boolean hasFill()
	{
		if (!hasFillColorProperty.isBound())
		{
			logger.warning("Has Fill Property is not bound yet");
		}
		return hasFillColorProperty.get();
	}

	/**
	 * Has fill color property accessor
	 * @return the has fill color property
	 */
	public final BooleanProperty hasFillColorProperty()
	{
		return hasFillColorProperty;
	}

	/**
	 * Current Fill color for next {@link Figure}.
	 * A Warning message is issued if {@link #fillColorProperty} has not been
	 * bound yet.
	 * @return the current fill color for next {@link Figure}
	 */
	public Color getFillColor()
	{
		if (!fillColorProperty.isBound())
		{
			logger.warning("Fill Color property is not bound yet");
		}
		return fillColorProperty.get();
	}

	/**
	 * Fill color property accessor
	 * @return the fill color property
	 */
	public final ObjectProperty<Color> fillColorProperty()
	{
		return fillColorProperty;
	}

	/**
	 * Current Edge usage for next {@link Figure}.
	 * A Warning message is issued if {@link #hasEdgeColorProperty} has not been
	 * bound yet.
	 * @return true if Fill Color should ne used on next {@link Figure}, false otherwise
	 */
	public boolean hasEdge()
	{
		if (!hasEdgeColorProperty.isBound())
		{
			logger.warning("Has Edge Property is not bound yet");
		}
		return hasEdgeColorProperty.get();
	}

	/**
	 * Has Edge color property accessor
	 * @return the has edge color property
	 */
	public final BooleanProperty hasEdgeColorProperty()
	{
		return hasEdgeColorProperty;
	}

	/**
	 * Current Edge color for next {@link Figure}.
	 * A Warning message is issued if {@link #edgeColorProperty} has not been
	 * bound yet.
	 * @return the current edge color for next {@link Figure}
	 */
	public Color getEdgeColor()
	{
		if (!edgeColorProperty.isBound())
		{
			logger.warning("Edge Color property is not bound yet");
		}
		return edgeColorProperty.get();
	}

	/**
	 * Edge color property accessor
	 * @return the edge color property accessor
	 */
	public final ObjectProperty<Color> edgeColorProperty()
	{
		return edgeColorProperty;
	}

	/**
	 * Current Edge {@link LineType} for next {@link Figure}.
	 * A Warning message is issued if {@link #lineTypeProperty} has not been
	 * bound yet.
	 * @return the current line type for next {@link Figure}
	 */
	public LineType getLineType()
	{
		if (!lineTypeProperty.isBound())
		{
			logger.warning("Line Type property is not bound yet");
		}
		return lineTypeProperty.get();
	}

	/**
	 * Line type property accessor
	 * @return the line type property
	 */
	public final ObjectProperty<LineType> lineTypeProperty()
	{
		return lineTypeProperty;
	}

	/**
	 * Current Edge width for next {@link Figure}.
	 * A Warning message is issued if {@link #lineWidthProperty} has not been
	 * bound yet.
	 * @return the current line width for next {@link Figure}
	 */
	public double getLineWidth()
	{
		if (!lineWidthProperty.isBound())
		{
			logger.warning("Line Width property is not bound yet");
		}
		return lineWidthProperty.get();
	}

	/**
	 * Line width property accessor
	 * @return the lne width property
	 */
	public final ObjectProperty<Double> lineWidthProperty()
	{
		return lineWidthProperty;
	}

	/**
	 * Get the root {@link Pane} to draw in
	 * @return the {@link #root} Pane
	 */
	public Pane getRoot()
	{
		return root;
	}

	// -------------------------------------------------------------------------
	// Figure's creation and manipulations
	// -------------------------------------------------------------------------
	/**
	 * Initiates a new figure at the specified position using current drawing
	 * parameters.
	 * The kind of {@link Figure} to create will be determined by the current
	 * {@link #figureTypeProperty}
	 * @param x the x coordinate where to create the new {@link Figure}
	 * @param y the y coordinate where to create the new {@link Figure}
	 * @return a new {@link Figure} created at point (x, y)
	 * @implNote The newly created figure shall not be added right away to
	 * {@link #figures}, we should wait for the
	 * {@link tools.creation.RectangularShapeCreationTool} to finish updating
	 * the newly created figure to decide whether the newly created figure
	 * should be added to {@link #figures} or not.
	 * @see FigureType#getFigure(Color, Color, LineType, double, Logger, double, double)
	 */
	public Figure initiateFigure(double x, double y)
	{
		FigureType figureType = figureTypeProperty.get();

		/*
		 * TODO 101 Drawing#initiateFigure ...
		 * Calls FigureType#getFigure with the appropriate arguments to get a
		 * newly created figure at location (x, y)
		 */
		return null;
	}

	/**
	 * Retrieve {@link Figure} from a {@link Shape}
	 * @param shape the shape contained in searched Figure
	 * @return The Figure containing the provided {@link Shape} or null if there
	 * is not such figure (when the shape has not yet been added to the
	 * {@link Drawing} model for instance).
	 * @apiNote this method will be used find the corresponding Figure when
	 * clicking on a Shape in the root drawing Pane.
	 */
	public Figure fromShape(Shape shape)
	{
		/*
		 * TODO 102 Drawing#fromShape
		 * 	- finds the figure in #figures containing this shape
		 * 	- or finds the group index of root.getChildren() containing this shape since it should be the same as in #figures
		 * CAUTION root.getChildren() might only contains Groups, so we have to investigate
		 * each group's children to find the corresponding shape
		 * NOTE : This also works for Figure#selectionRectangle since its a
		 * Shape in a Group at the right index
		 */
		return null;
	}

	/**
	 * Clears all selected elements of {@link #view} (iff non null)
	 * @post All figures in {@link #figures} are de-selected
	 */
	public void clearSelection()
	{
		// TODO 103 Drawing#clearSelection ...
		if (view == null)
		{
			logger.severe("null view");
			return;
		}
	}

	/**
	 * Updates the selection model of the {@link #view} whenever a figure becomes
	 * selected or deselected in the Drawing Area without {@link #view} knowing it.
	 * Updating selection in the {@link #view} will trigger
	 * {@link #onChanged(javafx.collections.ListChangeListener.Change)} which
	 * actually changes selection on figures.
	 * @param index the index of the selected or deselected figure
	 * @param selected the selected state to apply to selected index
	 */
	public void updateSelection(int index, boolean selected)
	{
		if (view == null)
		{
			logger.severe("Can't change null view");
			return;
		}
		if ((index < 0) || (index >= size()))
		{
			logger.severe("invalid index " + index + " outside bounds[0.."
			    + (size() - 1) + "]");
			return;
		}

		logger.info("Modify selection model at index " + index + " with "
		    + (selected ? "true" : "false"));

		/*
		 * TODO 104 Drawing#updateSelection ...
		 * Get #view selection model and either
		 * 	- select(index) or
		 * 	- clearSelection(index)
		 */
	}

	/**
	 * Refresh all JavaFX elements in {@link #root} by clearing all elements
	 * and re-adding each {@link Figure#root} in {@link #root}
	 */
	public void refresh()
	{
		// TODO 105 Drawing#refresh() ...
		ObservableList<Node> children = root.getChildren();
	}

	// ------------------------------------------------------------------------
	// ModifiableObservableListBase<Figure> methods implementation
	// ------------------------------------------------------------------------
	/**
	 * Figure getter from index.
	 * @param index the index of the figure to get
	 * @throws IndexOutOfBoundsException if index is invalid
	 */
	@Override
	public Figure get(int index) throws IndexOutOfBoundsException
	{
		return figures.get(index);
	}

	/**
	 * Number of figures in the collection
	 * @return the number of figures in the collection
	 */
	@Override
	public int size()
	{
		return figures.size();
	}

	/**
	 * Adds the {@code element} to {@link #figures} and {@code element}'s shape
	 * to {@link #root} at the position of {@code index}.
	 * @param index the position where to add the element
	 * @param element the element that will be added. If element is null it
	 * shall not be added. If element is already present in {@link #figures} it
	 * shall not be added.
	 * @throws ClassCastException if the type of the specified element is
	 * incompatible with this list
	 * @throws NullPointerException if the specified arguments contain one or
	 * more null elements
	 * @throws IllegalArgumentException if some property of this element
	 * prevents it from being added to this list
	 * @throws IndexOutOfBoundsException if the index is out of range
	 * {@code (index < 0 || index > size())}
	 * @implNote This method is used in
	 * {@link ModifiableObservableListBase#add(Object)} method which also
	 * notifies any {@link javafx.collections.ListChangeListener} of this
	 * observable list.
	 * @warning since doAdd is called by the add template method it always return
	 * true wich is not the expected behavior as we want to
	 */
	@Override
	protected void doAdd(int index, Figure element)
	{
		if (element == null)
		{
			String message = "null figure";
			logger.severe(message);
			throw new NullPointerException(message);
		}

		if (figures.contains(element))
		{
			String message = "figure already contained";
			logger.severe(message);
			throw new IllegalArgumentException(message);
		}

		if ((index < 0) || (index > figures.size()))
		{
			String message = "invalid index " + index;
			logger.severe(message);
			throw new IndexOutOfBoundsException(message);
		}

		logger.info("adding figure " + element + " at index " + index + " in "
		    + figures + " and " + root.getChildren());

		/*
		 * Adds figure to figures
		 */
		figures.add(index, element);
		/*
		 * Adds figure's root to drawing Pane
		 */
		root.getChildren().add(index, element.getRoot());
	}

	/**
	 * Sets the {@code element} in {@link #figures} at the position of
	 * {@code index}.
	 * @param index the position where to set the element
	 * @param element the element that will be set at the specified position. If
	 * element is null it shall not be set. If element is already present in
	 * {@link #figures} at this position, it shall not be added.
	 * @return the old element at the specified position
	 * @throws ClassCastException if the type of the specified element is
	 * incompatible with this list
	 * @throws NullPointerException if the specified arguments contain one or
	 * more null elements
	 * @throws IllegalArgumentException if some property of this element
	 * prevents it from being added to this list
	 * @throws IndexOutOfBoundsException if the index is out of range
	 * {@code (index < 0 || index >= size())}
	 * @implNote This method is used in
	 * {@link ModifiableObservableListBase#set(int, Object)} method which also
	 * notifies any {@link javafx.collections.ListChangeListener} of this
	 * observable list.
	 */
	@Override
	protected Figure doSet(int index, Figure element) throws NullPointerException
	{
		if (element == null)
		{
			String message = "null element at index " + index;
			logger.severe(message);
			throw new NullPointerException(message);
		}

		if (contains(element))
		{
			String message = "duplicate element";
			logger.severe(message);
			throw new IllegalArgumentException(message);
		}

		if ((index < 0) || (index >= figures.size()))
		{
			String message = "invalid index " + index;
			logger.severe(message);
			throw new IndexOutOfBoundsException(message);
		}

		Figure oldFigure = figures.set(index, element);
		root.getChildren().set(index, element.getRoot());
		return oldFigure;
	}

	/**
	 * Removes the element at position of {@code index} from {@link #figures}
	 * @param index the index of the removed element
	 * @return the removed element
	 * @throws IndexOutOfBoundsException if the index is out of range
	 * {@code (index < 0 || index >= size())}
	 * @implNote This method is used in
	 * {@link ModifiableObservableListBase#remove(int)} method which also
	 * notifies any {@link javafx.collections.ListChangeListener} of this
	 * observable list.
	 */
	@Override
	protected Figure doRemove(int index)
	{
		if ((index < 0) || (index >= figures.size()))
		{
			String message = "invalid index " + index;
			logger.severe(message);
			throw new IndexOutOfBoundsException(message);
		}

		Figure removedFigure = figures.remove(index);
		Node removedNode = root.getChildren().remove(index);
		if (removedNode == null)
		{
			logger.severe("null removed Node");
		}
		return removedFigure;
	}

	// ------------------------------------------------------------------------
	// Originator<Figure> methods implementation
	// ------------------------------------------------------------------------
	/**
	 * Creates a new {@link Memento} containing the current {@link #figures}
	 * @return a new {@link Memento} containing the current {@link #figures}
	 */
	@Override
	public Memento<Figure> createMemento()
	{
		return new Memento<Figure>(figures);
	}

	/**
	 * Replace the current {@link #figures} with the state contained in the provided
	 * {@link Memento}
	 * @param memento the new state to set.
	 * @post the state contained in the provided memento has replaced the current
	 * {@link #figures}, if and only if the provided memento was not null
	 */
	@Override
	public void setMemento(Memento<Figure> memento)
	{
		if (memento == null)
		{
			return;
		}

		List<Figure> savedFigures = memento.getState();
		logger.info(savedFigures.toString());

		clear();
		addAll(savedFigures);
//		refresh(); // might not be necessary
		for (int i = 0; i < figures.size(); i++)
		{
			Figure figure = get(i);
			updateSelection(i, figure.isSelected());
		}
	}

	// ------------------------------------------------------------------------
	// ListChangeListener<Figure> methods implementation
	// ------------------------------------------------------------------------

	/**
	 * List change listener method used to react to selection changes in a
	 * {@link ListView}. Since this can be shown in a {@link ListView}, we
	 * should act on any selection changes by selecting / deselecting figures
	 * in {@link #figures} using either
	 * ListChangeListener.Change#getList() and compare this selection to
	 * {@link #figures} looking for selected {@link Figure}s if one of
	 * {@link #figures} selection should be changed, then change it.
	 * @param change an object representing the changes performed in
	 */
	@Override
	public void onChanged(Change<? extends Figure> change)
	{
		logger.info("List Change Listener triggered with change=" + change);
		while (change.next())
		{
			// Selected Figures in #listView
			List<? extends Figure> selection = change.getList();

			/*
			 * TODO 106 Drawing#onChanged ...
			 * Changes #figures figure selected states according to selection
			 */
			// Deselect all figures
			// Select all figures in the selection list
		}
	}
}
