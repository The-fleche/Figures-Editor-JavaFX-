package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.cells.FigureCell;
import application.panels.InfoPanelController;
import figures.Drawing;
import figures.Ellipse;
import figures.Circle;
import figures.Rectangle;
import figures.Figure;
import figures.enums.FigureType;
import figures.enums.LineType;
import figures.enums.OperationType;
import history.FxHistoryManager;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logger.LoggerFactory;
import tools.AbstractTool;
import tools.CursorTool;
import tools.SelectionTool;
import tools.TransformTool;
import utils.ColorFactory;
import utils.IconFactory;

/**
 * Controller associated with EditorFrame.fxml
 * Contains:
 * <ul>
 * 	<li>FXML UI elements that need to be referenced in business logic</li>
 * 	<li>onXXXX() callback methods handling UI requests</li>
 * </ul>
 * @author davidroussel
 * @see Initializable so it can initialize FXML related attributes.
 */
public class Controller implements Initializable
{
	// -------------------------------------------------------------------------
	// internal attributes
	// -------------------------------------------------------------------------
	/**
	 * Logger to show debug message or only log them in a file
	 */
	protected Logger logger = null;

	/**
	 * Reference to parent stage so it can be quickly closed on quit
	 * Initialized through {@link #setParentStage(Stage)} in
	 * {@link Main#start(Stage)}
	 */
	private Stage parentStage = null;

	/**
	 * Drawing model containing {@link Figure}s
	 * <ul>
	 * 	<li>drawn in {@link #drawingPane}</li>
	 * 	<li>showed in {@link #figuresListView}</li>
	 * </ul>
	 */
	private Drawing drawingModel = null;

	/**
	 * History Manager to manage Undo / Redos on {@link #drawingModel}
	 */
	private FxHistoryManager<Figure> historyManager = null;

	/**
	 * Current Tool attached to {@link #drawingPane} and {@link #drawingModel}
	 */
	private AbstractTool<Pane> currentTool = null;

	/**
	 * Tool to move / rotate / scale figures (in edit mode only)
	 */
	private AbstractTool<Pane> transformTool = null;

	/**
	 * CursorTool to update mouse coordinates in {@link #drawingPane} using
	 * {@link #cursorXLabel} and {@link #cursorYLabel} at the bottom right of
	 * the scene.
	 */
	private CursorTool cursorTool = null;

	// -------------------------------------------------------------------------
	// FXML identified attributes (with fx:id)
	// -------------------------------------------------------------------------

	/**
	 * Application Drawing Pane
	 */
	@FXML
	private Pane drawingPane;

	/**
	 * Toolbar "MoveDown" Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 */
	@FXML
	private Button undoButton;

	/**
	 * Toolbar "Redo" Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 */
	@FXML
	private Button redoButton;

	/**
	 * Toolbar "Clear" Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 */
	@FXML
	private Button clearButton;

	/**
	 * Toolbar "Edit" Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 * @see #editToggleImageView
	 * @implNote Should be bound to {@link #toggleEditCheckMenuItem}h
	 */
	@FXML
	private ToggleButton editToggleButton;

	/**
	 * Figures check menu item to toggle edition
	 * @implNote Should be bound to {@link #editToggleButton}
	 */
	@FXML
	private CheckMenuItem toggleEditCheckMenuItem;

	/**
	 * Image contained if {@link #editToggleButton} so it can be changed
	 * when the button is toggled
	 * @see #editIcon
	 * @see #createIcon
	 * @see #onToggleEditAction(ActionEvent)
	 */
	@FXML
	private ImageView editToggleImageView;

	/**
	 * "MoveDown" Image to display in Creation mode
	 * @see #editToggleImageView
	 * @see #onToggleEditAction(ActionEvent)
	 */
	private final static Image editIcon = IconFactory.getIcon("create_new");

	/**
	 * "Create" Image to display in MoveDown mode
	 * @see #editToggleImageView
	 * @see #onToggleEditAction(ActionEvent)
	 */
	private final static Image createIcon = IconFactory.getIcon("edit");

	/**
	 * Toolbar "Delete (selected)" Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 */
	@FXML
	private Button deleteButton;

	/**
	 * Toolbar "Move Up" Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 */
	@FXML
	private Button moveUpButton;

	/**
	 * Toolbar "Move Down" Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 */
	@FXML
	private Button moveDownButton;

	/**
	 * Toolbar "Move on Top" Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 */
	@FXML
	private Button moveTopButton;

	/**
	 * Toolbar Move to Bottom Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 */
	@FXML
	private Button moveBottomButton;

	/**
	 * Toolbar "Quit" Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 */
	@FXML
	private Button quitButton;

	/**
	 * Shape types Combobox
	 */
	@FXML
	private ComboBox<FigureType> shapeTypeComboBox;

	/**
	 * CheckBox indicating Fill Color should be taken into account
	 */
	@FXML
	private CheckBox useFillColor;

	/**
	 * Color Picker to chose Fill Color
	 */
	@FXML
	private ColorPicker fillColorPicker;

	/**
	 * CheckBox indicating Edge Color should be taken into account
	 */
	@FXML
	private CheckBox useEdgeColor;

	/**
	 * Color Picker to chose Edge Color
	 */
	@FXML
	private ColorPicker edgeColorPicker;

	/**
	 * Line types Combobox
	 */
	@FXML
	private ComboBox<LineType> lineTypeCombobox;

	/**
	 * Line width Spinner
	 */
	@FXML
	private Spinner<Double> lineWidthSpinner;

	/**
	 * Figures ListView
	 */
	@FXML
	private ListView<Figure> figuresListView;

	/**
	 * Label to display info messages (such as tips on how to proceed)
	 */
	@FXML
	private Label messagesLabel;

	/**
	 * Label showing cursor X coordinate in {@link #drawingPane}
	 */
	@FXML
	private Label cursorXLabel;

	/**
	 * Label showing cursor Y coordinate in {@link #drawingPane}
	 */
	@FXML
	private Label cursorYLabel;

	/**
	 * The Info Panel loaded from ./panels/InfoPanel.fxml
	 */
	@FXML
	private GridPane infoPanel;

	/**
	 * The Controller of {@link #infoPanel}
	 */
	@FXML
	private InfoPanelController infoPanelController;

	/**
	 * Menu item to trigger intersect operation between two selected and
	 * overlapping shapes
	 */
	@FXML
	private MenuItem intersectMenuItem;

	/**
	 * Menu item to trigger subtract operation between two selected and
	 * overlapping shapes
	 */
	@FXML
	private MenuItem subtractMenuItem;

	/**
	 * Menu item to trigger union operation between two selected and
	 * overlapping shapes
	 */
	@FXML
	private MenuItem unionMenuItem;

	/**
	 * Menu item to trigger union operation between two selected and
	 * overlapping shapes
	 */
	@FXML
	private MenuItem splitMenuItem;

	/**
	 * Menu item to trigger intersect operation between two selected and
	 * overlapping shapes
	 */
	@FXML
	private MenuItem intersectContextMenuItem;

	/**
	 * Menu item to trigger subtract operation between two selected and
	 * overlapping shapes
	 */
	@FXML
	private MenuItem subtractContextMenuItem;

	/**
	 * Context Menu item to trigger union operation between two selected and
	 * overlapping shapes
	 */
	@FXML
	private MenuItem unionContextMenuItem;

	/**
	 * Context Menu item to trigger split of all selected FigureOperations
	 */
	@FXML
	private MenuItem splitContextMenuItem;

	// -------------------------------------------------------------------------
	// Other FXML attributes
	// -------------------------------------------------------------------------

	/**
	 * List of buttons with display style that can change.
	 * These buttons are:
	 * <ul>
	 * 	<li>{@link #undoButton}</li>
	 * 	<li>{@link #redoButton}</li>
	 * 	<li>{@link #clearButton}</li>
	 * 	<li>{@link #editToggleButton}</li>
	 * 	<li>{@link #deleteButton}</li>
	 * 	<li>{@link #moveUpButton}</li>
	 * 	<li>{@link #moveDownButton}</li>
	 * 	<li>{@link #moveTopButton}</li>
	 * 	<li>{@link #moveBottomButton}</li>
	 * 	<li>{@link #quitButton}</li>
	 * </ul>
	 */
	private List<Labeled> styleableButtons;

	/**
	 * Default constructor.
	 * Initialize all non FXML attributes
	 */
	public Controller()
	{
		// --------------------------------------------------------------------
		// Initialize own attributes
		// --------------------------------------------------------------------
		/*
		 * Can't get parent logger now, so standalone logger.
		 * Parent logger will be set in Main.
		 */
		logger = LoggerFactory.getParentLogger(getClass(), null, Level.INFO);

		cursorTool = new CursorTool();
	}

	/**
	 * Controller initialization to initialize FXML related attributes.
	 * @param location The location used to resolve relative paths for the root
	 * object, or null if the location is not known.
	 * @param resources Resource Bundle containing translations resources for
	 * the UI (or null)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		/*
		 * TODO 501a Controller#initialize: Complete EditorFrame.fxml fx:id
		 * For each @FXML annotated attribute in this class there should be
		 * an fx:id in the Code tab of the corresponding widget in
		 * EditorFrame.fxml
		 */
		Map<String, Control> controlMap = new HashMap<>();
		controlMap.put("undoButton", undoButton);
		controlMap.put("redoButton", redoButton);
		controlMap.put("clearButton", clearButton);
		controlMap.put("editToggleButton", editToggleButton);
		controlMap.put("deleteButton", deleteButton);
		controlMap.put("moveUpButton", moveUpButton);
		controlMap.put("moveDownButton", moveDownButton);
		controlMap.put("moveTopButton", moveTopButton);
		controlMap.put("shapeTypeComboBox", shapeTypeComboBox);
		controlMap.put("useFillColor", useFillColor);
		controlMap.put("fillColorPicker", fillColorPicker);
		controlMap.put("useEdgeColor", useEdgeColor);
		controlMap.put("edgeColorPicker", edgeColorPicker);
		controlMap.put("lineTypeCombobox", lineTypeCombobox);
		controlMap.put("lineWidthSpinner", lineWidthSpinner);
		controlMap.put("figuresListView", figuresListView);
		for (String name : controlMap.keySet())
		{
			Control control = controlMap.get(name);
			if (control == null)
			{
				logger.severe("Control " + name + " is null, please edit "
					+ "EditorFrame.fxml so set fx:id");
			}
		}

		Map<String, MenuItem> menusMap = new HashMap<>();
		menusMap.put("intersectMenuItem", intersectMenuItem);
		menusMap.put("subtractMenuItem", subtractMenuItem);
		menusMap.put("unionMenuItem", unionMenuItem);
		menusMap.put("splitMenuItem", splitMenuItem);
		menusMap.put("intersectContextMenuItem", intersectContextMenuItem);
		menusMap.put("subtractContextMenuItem", subtractContextMenuItem);
		menusMap.put("unionContextMenuItem", unionContextMenuItem);
		menusMap.put("splitContextMenuItem", splitContextMenuItem);
		for (String name : menusMap.keySet())
		{
			MenuItem menuItem = menusMap.get(name);
			if (menuItem == null)
			{
				logger.severe("MenuItem " + name + " is null, please edit "
					+ "EditorFrame.fxml so set fx:id");
			}
		}

		/*
		 * TODO 501b Controller#initialize: Complete EditorFrame.fxml onAction
		 * Each widget triggering an action should have its "onAction" field
		 * filled with the corresponding "onXXXAction" callback in the Code tab
		 */

		/*
		 * DONE 502 Controller#initialize: setting up #drawingModel
		 * with
		 * 	- #drawingPane
		 * 	- #figuresListView
		 * 	- #logger
		 */
		drawingModel = new Drawing(drawingPane, figuresListView, logger);

		/*
		 * TODO 503 Controller#initialize: Drawing properties bindings
		 * Binds properties of UI elements to #drawingModel so that changes
		 * in those UI elements will directly be reflected in properties of the
		 * #drawingModel without callbacks:
		 * 	- #shapeTypeComboBox --> Drawing#figureTypeProperty
		 * 	- #useFillColor --> Drawing#hasFillColorProperty
		 * 	- #fillColorPicker --> Drawing#fillColorProperty
		 * 	- #useEdgeColor --> Drawing#hasEdgeColorProperty
		 * 	- #edgeColorPicker --> Drawing#edgeColorProperty
		 * 	- #lineTypeCombobox --> Drawing#lineTypeProperty
		 * 	- #lineWidthSpinner --> Drawing#lineWidthProperty
		 * Use Drawing#bindProperty(source, destination)
		 */
		Drawing.bindProperty(shapeTypeComboBox.valueProperty(), drawingModel.figureTypeProperty());

		/*
		 * DONE 504 Controller#initialize: Setting up #historyManager
		 * with
		 * 	- #drawingModel
		 * 	- 32 Undo / Redo steps (changeable in #onSetHistorySizeAction)
		 * 	- #logger
		 */
		historyManager = new FxHistoryManager<Figure>(drawingModel, 32, logger);

		/*
		 * TODO 505 Binds #historyManager properties
		 * bind #undoIsEmpty and #redoIsEmpty properties
		 * to UI #undoButton's and #redoButton's #disable properties so that
		 * buttons can reflect internal history manager state
		 */

		// --------------------------------------------------------------------
		// Initialize FXML related attributes
		// --------------------------------------------------------------------
		/*
		 * Bind bidirectionally #editToggleButton & #toggleEditCheckMenuItem
		 */
		editToggleButton.selectedProperty().bindBidirectional(toggleEditCheckMenuItem.selectedProperty());

		/*
		 * DONE 505 Controller#initialize Setup #shapeTypeComboBox
		 * with
		 * 	- items as all FigureTypes
		 * 	- current value as FigureType.ELLIPSE
		 */
		shapeTypeComboBox.getItems().addAll(FigureType.all());
		shapeTypeComboBox.setValue(FigureType.ELLIPSE);

		/*
		 * TODO 901 Controller#initialize: Customized FigureType Cells
		 * If you have provided a FigureTypeCell with its controller, then setup
		 * 	- ButtonCell as new FigureTypeCell()
		 * 	- CellFactory as combobox -> new FigureTypeCell()
		 */
//		Callback<ListView<FigureType>, ListCell<FigureType>> figureTypeCellFactory = FigureTypeCell.getCellFactory();
//		shapeTypeComboBox.setButtonCell(figureTypeCellFactory.call(null));
//		shapeTypeComboBox.setCellFactory(figureTypeCellFactory);

		/*
		 * TODO 506 Controller#initialize Setup #lineTypeCombobox
		 * with
		 * 	- items as all LineTypes
		 * 	- value as LineType.SOLID
		 */

		/*
		 * TODO 902 Controller#initialize: Customized LineType Cells
		 * If you have provided a LineTypeCell CustomCell and its controller
		 * then setup
		 * 	- ButtonCell as new LineTypeCell()
		 * 	- CellFactory as combobox -> new LineTypeCell()
		 */
//		Callback<ListView<LineType>, ListCell<LineType>> lineTypeCellFactory = LineTypeCell.getCellFactory();
//		lineTypeCombobox.setButtonCell(lineTypeCellFactory.call(null));
//		lineTypeCombobox.setCellFactory(lineTypeCellFactory);


		/*
		 * TODO 507 Controller#initialize: Setup #useFillColor, #useEdgeColor, #fillColorPicker and #edgeColorPicker
		 * in a consistent state :
		 * 	- if #useFillColor is deselected
		 * 		- #fillColorPicker should be disabled
		 * 		- #useEdgeColor should be selected
		 *	- if #useEdgeColor is deselected
		 *		- #edgeColorPicker should be disabled
		 *		- #useFillColor should be selected
		 * dynamic check should be performed in #onCheckColorsConsistencyAction
		 */

		/*
		 * DONE 508 Controller#initialize: Setup #lineWidthSpinner
		 * with an new SpinnerValueFactory
		 * 	- ranging from 1.0 to 32.0
		 * 	- current value 2.0
		 * 	- step value 1.0
		 * see SpinnerValueFactory
		 */
		lineWidthSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 32.0, 2.0, 1.0));
		lineWidthSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
			onLineWidthChanged(observable, oldValue, newValue);
		});

		/*
		 * DONE 509 Controller#initialize: Setup #figuresListView with
		 * 	- content from #drawingModel
		 * 	- CellFactory returning a new FigureCell
		 * Note: #figuresListView has already been set up in Drawing constructor
		 * with:
		 * 	- multiple selections
		 * 	- #drawingModel as a ListChangeListener to #figuresListView
		 */
		figuresListView.setItems(drawingModel);
		figuresListView.setCellFactory(FigureCell.getCellFactory());

		/*
		 * Setup #messagesLabel
		 * with empty or null message
		 */
		messagesLabel.setText(null);

		/*
		 * TODO 510 Controller#initialize bind #editToggleButton selected
		 * property to edit buttons disable property so they can be
		 * deactivated when not in edit mode
		 */
		BooleanBinding notEdit = editToggleButton.selectedProperty().not();

		/*
		 * Create #styleableButtons so they can be style updated in
		 * 	- #onDisplayButtonsWithGraphicsOnlyAction
		 * 	- #onDisplayButtonsWithTextOnlyAction
		 * 	- #onDisplayButtonsWithTextAndGraphicsAction
		 */
		styleableButtons = new ArrayList<Labeled>();
		styleableButtons.add(undoButton);
		styleableButtons.add(redoButton);
		styleableButtons.add(clearButton);
		styleableButtons.add(editToggleButton);
		styleableButtons.add(deleteButton);
		styleableButtons.add(moveUpButton);
		styleableButtons.add(moveDownButton);
		styleableButtons.add(moveTopButton);
		styleableButtons.add(moveBottomButton);
		styleableButtons.add(quitButton);

		/*
		 * Setup #infoPanelController with
		 * 	- #drawingPane
		 * 	- #drawingModel
		 * 	- #logger
		 */
		infoPanelController.setup(drawingPane, drawingModel, logger);

		/*
		 * Setup #cursorTool with
		 * 	- #drawingPane
		 * 	- #cursorXLabel
		 * 	- #cursorYLabel
		 * 	- #logger
		 */
		cursorTool.setup(drawingPane, cursorXLabel, cursorYLabel, logger);

		/*
		 * Create current tool (by default: creation tool)
		 */
		setTools(false);
	}

	/**
	 * Sets parent logger
	 * @param logger the new parent logger
	 */
	public void setParentLogger(Logger logger)
	{
		this.logger.setParent(logger);
	}

	/**
	 * Set parent stage (so it can be closed on quit)
	 * @param stage the new parent stage to set
	 */
	public void setParentStage(Stage stage)
	{
		parentStage = stage;
	}

	/**
	 * Action to Undo the last operation
	 * @param event event associated with this action
	 */
	@FXML
	public void onUndoAction(ActionEvent event)
	{
		logger.info("Undo Action triggered");
		historyManager.undo();
	}

	/**
	 * Action to Redo the last operation
	 * @param event event associated with this action
	 */
	@FXML
	public void onRedoAction(ActionEvent event)
	{
		logger.info("Redo Action triggered");
		historyManager.redo();
	}

	/**
	 * Action to Clear all figures in {@link #drawingModel}
	 * @param event event associated with this action
	 */
	@FXML
	public void onClearAction(ActionEvent event)
	{
		logger.info("Clear Action triggered");
		// TODO 511 Controller#onClearAction
	}

	/**
	 * Action to toggle Edit mode (Create mode <--> Edit mode)
	 * @param event event associated with this action
	 * @implSpec {@link #editToggleImageView} can be changed with either
	 * {@link #editIcon} {@link #createIcon} depending on
	 * {@link #editToggleButton} selected state
	 * @see #applyOnOffIcons(Toggle, ImageView, Image, Image)
	 */
	@FXML
	public void onToggleEditAction(ActionEvent event)
	{
		logger.info("Edit Action triggered");
		// Object source = event.getSource();
		boolean selected = editToggleButton.isSelected();
		/*
		 * Toggle #editToggleButton Icon
		 */
		applyOnOffIcons(editToggleButton, editToggleImageView, editIcon, createIcon);

		/*
		 * Update infoPanelController
		 */
		infoPanelController.setInfoLabel(selected ? messagesLabel : null);

		/*
		 * Set Tools according to selected
		 */
		setTools(selected);
	}

	/**
	 * Creates the appropriate tool(s) to listen to {@link MouseEvent}s on the
	 * {@link #drawingPane} depending on editMode
	 * <ul>
	 * 	<li>if editMode is on then register edit tools (selection, move, rotate, scale)</li>
	 * 	<li>if editMode is off the register creation tool (to create new figures)</li>
	 * </ul>
	 * @param editMode the mode to be selected
	 */
	protected void setTools(boolean editMode)
	{
		if (currentTool != null)
		{
			currentTool.unregister();
		}
		if (transformTool != null)
		{
			transformTool.unregister();
		}

		if (editMode)
		{
			/*
			 * Edit mode: edit figures in #drawingPane using
			 * a SelectionTool and a TransformTool
			 */
			currentTool = new SelectionTool(drawingPane, drawingModel, logger);
			transformTool = new TransformTool(drawingPane,
			                                  drawingModel,
			                                  messagesLabel,
			                                  historyManager,
			                                  logger);
		}
		else
		{
			transformTool = null;
			/*
			 * Creation mode: create figures according to FigureType of #drawingModel
			 * using the Creation Tool provided by FigureType#getCreationTool
			 */
			currentTool = drawingModel.getFigureType().getCreationTool(drawingPane,
			                                                           drawingModel,
			                                                           messagesLabel,
			                                                           historyManager,
			                                                           logger);
		}
		logger.info("Current tool = " + currentTool);
	}

	/**
	 * Action to delete selected figures (in {@link #drawingModel})
	 * @param event event associated with this action
	 */
	@FXML
	public void onDeleteSelectedAction(ActionEvent event)
	{
		logger.info("Delete Selected Action triggered");
		/*
		 * TODO 512 Controller#onDeleteSelectedAction ...
		 * 	- Create Memento
		 * 	- Retrieve Selected figures in #view and delete them in reverse
		 * 	order to preserve valid indices provided by the selected list
		 * 	- If no figures have been removed then cancel memento
		 * 	- clears selection in #figuresListView since selected figures have
		 * 	been removed
		 */

		/*
		 * If no figures have been cleared, cancel memento
		 */

		/*
		 * Clears selected
		 */
	}

	/**
	 * Action to Move selected figures Up (down in {@link #drawingModel})
	 * @param event event associated with this action
	 */
	@FXML
	public void onMoveUpAction(ActionEvent event)
	{
		logger.info("MoveUp Action triggered");
		/*
		 * TODO 513 Controller#onMoveUpAction ...
		 * Record history
		 * Retrieve selected figures indices and move theses figures down in
		 * the list one step starting from last one in order to preserve valid
		 * indices
		 * CAUTION: selected indices list might change during operations so
		 * the initial selected indices list should be detached from the one
		 * obtained by #getSelectedFiguresIndices.
		 * Incidentally a new selected indices list should be set at the end of
		 * operations so that initially selected figures remain selected at the
		 * end of operations.
		 * If no selection has been moved cancel history record
		 */
	}

	/**
	 * Action to Move selected figures Down (up in {@link #drawingModel})
	 * @param event event associated with this action
	 */
	@FXML
	public void onMoveDownAction(ActionEvent event)
	{
		logger.info("MoveDown Action triggered");
		/*
		 * TODO 514 Controller#onMoveDownAction ...
		 * Record history
		 * Retrieve selected figures indices and move theses figures up in
		 * the list one step starting from first one in order to preserve valid
		 * indices
		 * CAUTION: selected indices list might change during operations so
		 * the initial selected indices list should be detached from the one
		 * obtained by #getSelectedFiguresIndices.
		 * Incidentally a new selected indices list should be set at the end of
		 * operations so that initially selected figures remain selected at the
		 * end of operations
		 * If no selection has been moved cancel history record
		 */
	}

	/**
	 * Action to Move selected figures on Top (after all other figures in {@link #drawingModel})
	 * @param event event associated with this action
	 */
	@FXML
	public void onMoveTopAction(ActionEvent event)
	{
		logger.info("MoveTop Action triggered");
		/*
		 * TODO 515 Controller#onMoveTopAction ...
		 * Record history
		 * Retrieve selected figures indices and move theses figures down to the
		 * bottom of the list starting from last selected in order to preserve
		 * others indices.
		 * The next moved figure will be moved before the previous one and so on.
		 * CAUTION: selected indices list might change during operations so
		 * the initial selected indices list should be detached from the one
		 * obtained by #getSelectedFiguresIndices.
		 * Incidentally a new selected indices list should be set at the end of
		 * operations so that initially selected figures remain selected at the
		 * end of operations
		 * If no selection has been moved cancel history record
		 */
	}

	/**
	 * Action to Move selected figures to Bottom (before all other figures in {@link #drawingModel})
	 * @param event event associated with this action
	 */
	@FXML
	public void onMoveBottomAction(ActionEvent event)
	{
		logger.info("MoveBottom Action triggered");
		/*
		 * TODO 516 Controller#onMoveBottomAction ...
		 * Record history
		 * Retrieve selected figures indices and move theses figures up to the
		 * top of the list.
		 * The next moved figure will be moved after the previous one and so on.
		 * CAUTION: selected indices list might change during operations so
		 * the initial selected indices list should be detached from the one
		 * obtained by #getSelectedFiguresIndices.
		 * Incidentally a new selected indices list should be set at the end of
		 * operations so that initially selected figures remain selected at the
		 * end of operations
		 * If no selection has been moved cancel history record
		 */
	}

	/**
	 * Action to quit the application
	 * @param event event associated with this action
	 */
	@FXML
	public void onQuitAction(ActionEvent event)
	{
		quitActionImpl(event);
	}

	/**
	 * Implementation of the quit logic.
	 * Closes the stage.
	 * @param event the event passed to this callback (either {@link ActionEvent}
	 * or {@link WindowEvent} depending on what triggered this action).
	 */
	protected void quitActionImpl(Event event)
	{
		/*
		 * 	- closes the stage by
		 * 		- getting the stage from source if event is a WindowEvent
		 * 		- getting the stage from #parentStage or otherwise if event is
		 * 		an ActionEvent
		 */
		logger.info("Quit action triggered");

		Object source = event.getSource();
		Stage stage = null;

		if (event instanceof WindowEvent)
		{
			// Stage is the source
			stage = (Stage) source;
		}
		else if (event instanceof ActionEvent)
		{
			if (parentStage != null)
			{
				// We already have a registered stage
				stage = parentStage;
			}
			else
			{
				// Search for the stage
				if (source instanceof Button)
				{
					Button sourceButton = (Button) source;
					stage = (Stage) sourceButton.getScene().getWindow();
				}
				else
				{
					logger.warning("Unable to get Stage to close from: "
					    + source.getClass().getSimpleName());
				}
			}
		}
		else
		{
			logger.warning("unknown event source: " + event.getSource());
		}

		if (stage != null)
		{
			stage.close();
		}
		else
		{
			logger.warning("Window not closed");
		}
	}

	/**
	 * Action to show buttons with Graphics only
	 * @param event event associated with this action
	 */
	@FXML
	public void onDisplayButtonsWithGraphicsOnlyAction(ActionEvent event)
	{
		logger.info("Display Buttons with Graphics only action triggered");
		styleableButtons.forEach((Labeled labeled) -> {
			labeled.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		});
	}

	/**
	 * Action to show buttons with Text and Graphics
	 * @param event event associated with this action
	 */
	@FXML
	public void onDisplayButtonsWithTextAndGraphicsAction(ActionEvent event)
	{
		logger.info("Display Buttons with Text and Graphics action triggered");
		styleableButtons.forEach((Labeled labeled) -> {
			labeled.setContentDisplay(ContentDisplay.LEFT);
		});
	}

	/**
	 * Action to show buttons with Text only
	 * @param event event associated with this action
	 */
	@FXML
	public void onDisplayButtonsWithTextOnlyAction(ActionEvent event)
	{
		logger.info("Display Buttons with Text only action triggered");
		styleableButtons.forEach((Labeled labeled) -> {
			labeled.setContentDisplay(ContentDisplay.TEXT_ONLY);
		});
	}

	/**
	 * Action to set {@link #logger} level to {@link Level#INFO}
	 * @param event event associated with this action
	 */
	@FXML
	public void onSetLoggerLevelUpToInfoAction(ActionEvent event)
	{
		logger.info("Set Logger level up to INFO");
		setLoggerLevel(Level.INFO);
	}

	/**
	 * Action to set {@link #logger} level to {@link Level#WARNING}
	 * @param event event associated with this action
	 */
	@FXML
	public void onSetLoggerLevelUpToWarningAction(ActionEvent event)
	{
		logger.info("Set Logger level up to WARNING");
		setLoggerLevel(Level.WARNING);
	}

	/**
	 * Action to set {@link #logger} level to {@link Level#SEVERE}
	 * @param event event associated with this action
	 */
	@FXML
	public void onSetLoggerLevelUpToSevereAction(ActionEvent event)
	{
		logger.info("Set Logger level up to SEVERE");
		setLoggerLevel(Level.SEVERE);
	}

	/**
	 * Action to set {@link #logger} level to {@link Level#OFF}
	 * @param event event associated with this action
	 */
	@FXML
	public void onSetLoggerLevelOffAction(ActionEvent event)
	{
		logger.info("Set Logger level to OFF");
		setLoggerLevel(Level.OFF);
	}

	/**
	 * Set {@link #logger} level
	 * @param level the level to set on {@link #logger}
	 */
	private void setLoggerLevel(Level level)
	{
		if (logger != null)
		{
			logger.setLevel(level);;
		}
	}

	/**
	 * Action to set the number of Undo / Redos in {@link #historyManager}
	 * @param event event associated with this action
	 */
	@FXML
	public void onSetHistorySizeAction(ActionEvent event)
	{
		logger.info("Set History Size Action triggered");
		// TODO 517 Controller#onSetHistorySizeAction ...
		// Create and call SizeDialog to retrieve result
		Optional<Integer> result = Optional.empty();
		if (result.isPresent())
		{
			Integer value = result.get();
			logger.info("Set History Size to " + value);
			historyManager.setSize(value);
		}
	}

	/**
	 * Action called whenever the {@link #shapeTypeComboBox} changes value
	 * to ensure {@link #currentTool} is set to the right creation tool (iff
	 * edit mode is off, based on {@link #editToggleButton} selected status).
	 * @param event the event to process
	 */
	@FXML
	public void onShapeChangedAction(ActionEvent event)
	{
		Object source = event.getSource();
		if (!(source instanceof ComboBox<?>))
		{
			logger.warning("Shape type changed from unknown source : " + source);
		}

		@SuppressWarnings("unchecked")
		ComboBox<FigureType> box = (ComboBox<FigureType>) source;
		if (box != shapeTypeComboBox)
		{
			logger.warning("Shape type changed from unknown Combobox : " + box);
		}
		setTools(editToggleButton.isSelected());
		logger.info("Shape type changed action triggered with "
		    + box.getValue().toString() + "--> current tool = "
		    + currentTool.getClass().getSimpleName());
	}

	/**
	 * Action to call whenever {@link #fillColorPicker} changes value
	 * to apply the currently selected fill color to all selected figures
	 * @param event the event to process
	 */
	@FXML
	public void onFillColorChangedAction(ActionEvent event)
	{
		Object source = event.getSource();
		ColorPicker picker = null;
		if (!(source instanceof ColorPicker))
		{
			logger.warning("Fill color changed from unknown source : " + source);
		}
		else
		{
			picker = (ColorPicker) source;
		}
		if (picker != null)
		{
			logger.info("Fill color changed to " + picker.getValue());
		}
		Color color = null;
		if (picker != null)
		{
			color = picker.getValue();
			logger.info("Fill color changed to " + color.toString());
		}

		/*
		 * fillColorPicker's color property is already bound to
		 * Drawing's fillColor property
		 * TODO 518 Controller#onFillColorChangedAction
		 * Apply the selected fill color on all selected figures
		 */
	}

	/**
	 * Action to call whenever {@link #edgeColorPicker} changes value
	 * to apply the currently selected edge color to all selected figures
	 * @param event the event to process
	 */
	@FXML
	public void onEdgeColorChangedAction(ActionEvent event)
	{
		Object source = event.getSource();
		ColorPicker picker = null;
		if (!(source instanceof ColorPicker))
		{
			logger.warning("Fill color changed from unknown source : " + source);
		}
		else
		{
			picker = (ColorPicker) source;
		}
		Color color = null;
		if (picker != null)
		{
			color = picker.getValue();
			logger.info("Edge color changed to " + color.toString());
		}

		/*
		 * edgeColorPicker's color property is already bound to
		 * Drawing's edgeColor property
		 * TODO 519 Controller#onEdgeColorChangedAction
		 * Apply the selected edge color on all selected figures
		 */
	}

	/**
	 * Action called When {@link #useFillColor} or {@link #useEdgeColor} is
	 * changed to check there is at least one {@link Color}, either Fill color
	 * or Edge Color selected to be applied on next Figure. And also reflects
	 * {@link #useFillColor} and {@link #useEdgeColor} on
	 * {@link #fillColorPicker} and {@link #edgeColorPicker} disabled property
	 * @param event event associated with this action
	 */
	@FXML
	public void onApplyColorUsageAction(ActionEvent event)
	{
		logger.info("Check Colors consistency action triggered");
		Object source = event.getSource();
		if (!(source instanceof CheckBox))
		{
			logger.warning("source " + source.toString() + " is not a CheckBox");
			return;
		}

		CheckBox checkBox = (CheckBox) source;
		boolean selected = checkBox.isSelected();

		if (checkBox == useFillColor)
		{
			/*
			 * TODO 520 Controller#onApplyColorUsageAction: fill color case
			 * - if !selected
			 * 	- if useEdgeColor is also unselected then select it
			 * 		- set disabled state of edgeColorPicker to false
			 * 		- apply edge color on all selected figures
			 * 	- set fillColorPicker disabled state to true
			 * - if selected
			 * 	- just set disabled state of fillColorPicker to false
			 * - in all cases
			 * 	apply fill color (evt null) to all selected figures
			 */
			logger.info("Use Fill color changed to " + (selected ? "En" : "Di")
			    + "abled");
			return;
		}

		if (checkBox == useEdgeColor)
		{
			/**
			 * TODO 521 Controller#onApplyColorUsageAction: edge color case
			 * - if !selected
			 * 	- if useFillColor is not selected
			 * 		- set useFillColor to selected
			 * 		- set fillColorPicker disabled state to false
			 * 		- apply fill color on all selected figures
			 * 	- set disabled state of edgeColorPicker to true
			 * - if selected
			 * 	- set edgeColorPicker disabled state to false
			 * - in all cases apply edge color (evt null) to all selected figures
			 */
			logger.info("Use Edge color changed to " + (selected ? "En" : "Di")
			    + "abled");
			return;
		}

		logger.warning("event source is neither useFillColor nor useEdgeColor: "
		    + checkBox.toString());
	}

	/**
	 * Apply provided fill color on selected figures
	 * @param color the color to apply (might be null)
	 */
	private void applyFillColor(Color color)
	{
		List<Figure> selectedFigures = getSelectedFigures();
		if (selectedFigures.isEmpty())
		{
			return;
		}
		for (Figure figure : selectedFigures)
		{
			logger.info("Apply fill "
			    + (color == null ? "no color" : color.toString()) + " to "
			    + figure.toString());
			figure.setFillColor(color);
		}
	}

	/**
	 * Apply provided edge color on selected figures
	 * @param color the color to apply (might be null)
	 */
	private void applyEdgeColor(Color color)
	{
		List<Figure> selectedFigures = getSelectedFigures();
		if (selectedFigures.isEmpty())
		{
			return;
		}
		for (Figure figure : selectedFigures)
		{
			logger.info("Apply edge "
			    + (color == null ? "no color" : color.toString()) + " to "
			    + figure.toString());
			figure.setEdgeColor(color);
		}
	}

	/**
	 * Action triggered whenever {@link #lineTypeCombobox} changes value
	 * to apply the currently selected line type to all selected figures
	 * @param event the event to process
	 */
	@FXML
	public void onLineTypeChanged(ActionEvent event)
	{
		Object source = event.getSource();
		if (source != lineTypeCombobox)
		{
			logger.warning("Unknown line type source");
			return;
		}
		LineType type = lineTypeCombobox.getValue();
		logger.info("Line type changed to " + type.toString());
		/*
		 * TODO 522 Apply line type on all selected figures
		 */
	}

	/**
	 * Change listener method for listening to {@link #lineWidthSpinner} changes
	 * @param observable the {@link #lineWidthSpinner} observable values
	 * @param oldValue The previous value
	 * @param newValue The new Value
	 * @implNote This listener needs to be registered to
	 * {@link #lineWidthSpinner}'s valueProperty in
	 * {@link #initialize(URL, ResourceBundle)} method.
	 */
	public void onLineWidthChanged(ObservableValue<? extends Double> observable,
	                               Double oldValue,
	                               Double newValue)
	{
		logger.info("Changing line width from " + oldValue + " to " + newValue);
		List<Figure> selectedFigures = getSelectedFigures();
		/*
		 * TODO 523 Apply line width on all selected figures
		 */
	}

	/**
	 * Operation triggered to produce a composite figure resulting of
	 * figures operations such as intersection, subtract or union
	 * @param event the event to process
	 */
	public void onCreateFigureOperation(ActionEvent event)
	{
		/*
		 * Find source and operation type
		 */
		Object source = event.getSource();
		OperationType operation = OperationType.UNION;
		if ((source == intersectMenuItem) || (source == intersectContextMenuItem))
		{
			operation = OperationType.INTERSECTION;
		}
		if ((source == subtractMenuItem) || (source == subtractContextMenuItem))
		{
			operation = OperationType.SUBTRACTION;
		}
		if ((source == unionMenuItem) ||(source == unionContextMenuItem))
		{
			operation = OperationType.UNION;
		}
		logger.info("Create Figure Operation: " + operation.toString());

		// TODO 702 Controller#onCreateFigureOperation Complete when OperationFigure is ready

		/*
		 * Finds overlapping two selected figures
		 */

		/*
		 * Create operation figure from overlapping figures
		 */

		/*
		 * - record state
		 * - remove overlapping figures from Drawing
		 * - add OperationFigure to Drawing
		 * - Clear selection as figures have changed
		 */
	}

	/**
	 * Operation triggered to split all OperationFigures found in selected
	 * figures into their initial components
	 * @param event the event to process
	 */
	public void onSplitFigureOperation(ActionEvent event)
	{
		logger.info("Split operation triggered");
		List<Figure> selectedFigures  = getSelectedFigures();
		historyManager.record();

		// TODO 703 Controller#onSplitFigureOperation Complete when OperationFigure is ready

		/*
		 * - For each selected figures
		 *  - Create a list of figures to remove
		 * 	- if current figure is an OperationFigure
		 * 		- Split into its originating figures
		 * 		- add these figures to Drawing
		 * 		- record split figure into list of figures to remove
		 * - For each figure to remove : remove it from Drawing
		 * - If figures have changed clear selection
		 */
	}

	/**
	 * Action called from {@link #figuresListView}'s context menu to clear
	 * current list selection
	 * @param event the event to process
	 */
	@FXML
	public void onClearSelectionAction(ActionEvent event)
	{
		logger.info("Clear selection action triggered");
		// TODO 524 Controller#onClearSelectionAction ...
	}

	/**
	 * Action called from {@link #figuresListView}'s context menu to invert
	 * current list selection
	 * @param event the event to process
	 */
	public void onInvertSelection(ActionEvent event)
	{
		logger.info("Invert selection action triggered");
		/*
		 * TODO 525 Controller#onInvertSelection ...
		 */
	}

	/**
	 * A Demo action to draw all kinds of figures in the {@link #drawingPane}
	 * in order to prove these figures have been implemented.
	 * @param event the event to process [unused]
	 * @see Figure#setRotate(double) : Figure extra method is required
	 */
	@FXML
	public void onDemoAction(ActionEvent event)
	{
		logger.info("Demo action triggered");
		Color fill1 = ColorFactory.getColor(Color.web("#8FAADC"));
		Color edge1 = ColorFactory.getColor(Color.web("#1F4E79"));
		Color fill2 = ColorFactory.getColor(Color.web("#A9D18E"));
		Color edge2 = ColorFactory.getColor(Color.web("#548235"));
		Color fill3 = ColorFactory.getColor(Color.web("#FFD966"));
		double size = 150.0;
		double spacing = size * 0.8;
		double thickLine = 5.0;
		double thinLine = 2.0;
		double angle = 15.0;
		double x = spacing;
		double y = spacing;

		// DONE 201 Controller#onDemoAction Uncomment the following when Circle is ready
		historyManager.record();
		drawingModel.add(new Circle(fill1,
		                            null,
		                            LineType.NONE,
		                            1.0,
		                            logger,
		                            x,
		                            y,
		                            size * 0.75));
		historyManager.record();
		drawingModel.add(new Circle(fill2,
		                            edge1,
		                            LineType.DASHED,
		                            thickLine,
		                            logger,
		                            x,
		                            y,
		                            size * 0.50));
		historyManager.record();
		drawingModel.add(new Circle(fill3,
		                            edge2,
		                            LineType.SOLID,
		                            thinLine,
		                            logger,
		                            x,
		                            y,
		                            size * 0.25));

		x += (spacing * 2.0);

		historyManager.record();
		drawingModel.add(new Ellipse(fill1,
		                             null,
		                             LineType.NONE,
		                             1.0,
		                             logger,
		                             x,
		                             y,
		                             size * 0.75,
		                             size * 0.50).setRotate(angle));
		historyManager.record();
		drawingModel.add(new Ellipse(fill2,
		                             edge1,
		                             LineType.DASHED,
		                             thickLine,
		                             logger,
		                             x,
		                             y,
		                             size * 0.50,
		                             size * 0.33).setRotate(-angle));
		historyManager.record();
		drawingModel.add(new Ellipse(fill3,
		                             edge2,
		                             LineType.SOLID,
		                             thinLine,
		                             logger,
		                             x,
		                             y,
		                             size * 0.25,
		                             size * 0.16));

		x = spacing;
		y += (spacing * 2.0);

		// DONE 301 Controller#onDemoAction Uncomment the following when Rectangle is ready
		historyManager.record();
		drawingModel.add(new Rectangle(fill1,
		                               null,
		                               LineType.NONE,
		                               1.0,
		                               logger,
		                               x - ((size * 1.5) / 2.0),
		                               y - (size / 2.0),
		                               size * 1.5,
		                               size).setRotate(angle));
		historyManager.record();
		drawingModel.add(new Rectangle(fill2,
		                               edge1,
		                               LineType.DASHED,
		                               thickLine,
		                               logger,
		                               x - (size / 2.0),
		                               y - ((size * 0.75) / 2.0),
		                               size,
		                               size * 0.75).setRotate(-angle));
		historyManager.record();
		drawingModel.add(new Rectangle(fill3,
		                               edge2,
		                               LineType.SOLID,
		                               thinLine,
		                               logger,
		                               x - ((size * 0.5) / 2.0),
		                               y - ((size * 0.33) / 2.0),
		                               size * 0.5,
		                               size * 0.33));
	}

	/**
	 * Apply On/Off Icon on provided {@link ImageView} depending on the state
	 * of a {@link Toggle}able node (typically a {@link ToggleButton})
	 * @param toggle the toggle (button) containing the image view
	 * @param view the image view to change with new icon
	 * @param onIcon the image to apply when toggle is selected
	 * @param offIcon the image to apply when toggle is deselected
	 */
	private static void applyOnOffIcons(Toggle toggle,
	                                    ImageView view,
	                                    Image onIcon,
	                                    Image offIcon)
	{
		Image icon = toggle.isSelected() ? onIcon : offIcon;
		view.setImage(icon);
	}

	/**
	 * Retrieve selected figures in {@link #figuresListView}
	 * @return the list of selected figures in {@link #figuresListView}
	 */
	private List<Figure> getSelectedFigures()
	{
		return figuresListView.getSelectionModel().getSelectedItems();
	}

	/**
	 * Retrieve selected figures indices in {@link #figuresListView}
	 * @return the list of selected figures indices in {@link #figuresListView}
	 * @see #onMoveUpAction(ActionEvent)
	 * @see #onMoveDownAction(ActionEvent)
	 * @see #onMoveTopAction(ActionEvent)
	 * @see #onMoveBottomAction(ActionEvent)
	 */
	private List<Integer> getSelectedFiguresIndices()
	{
		return figuresListView.getSelectionModel().getSelectedIndices();
	}

	/**
	 * Setup a new selected indices list on {@link #figuresListView} after
	 * moving figures up or down so that initial selected figures remain
	 * selected after operations.
	 * @param indices the new selected indices to set in {@link #figuresListView}
	 * @see #onMoveUpAction(ActionEvent)
	 * @see #onMoveDownAction(ActionEvent)
	 * @see #onMoveTopAction(ActionEvent)
	 * @see #onMoveBottomAction(ActionEvent)
	 */
	private void setSelectedFiguresIndices(List<Integer> indices)
	{
		if (!indices.isEmpty())
		{
			figuresListView.getSelectionModel().clearSelection();
			for (Integer i : indices)
			{
				figuresListView.getSelectionModel().select(i.intValue());
			}
		}
	}
}
