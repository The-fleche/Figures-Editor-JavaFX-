package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;

import figures.Drawing;
import figures.Figure;
import figures.Mouseable;
import figures.enums.FigureType;
import figures.enums.LineType;
import history.Memento;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import logger.LoggerFactory;

/**
 * Test class for {@link Drawing}
 * @author davidroussel
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Drawing")
class DrawingTest
{
	/**
	 * Default {@link FigureType} in UI
	 */
	private static final FigureType DefaultFigureType = FigureType.CIRCLE;

	/**
	 * Default Fill Color in UI
	 */
	private static final Color DefaultFillColor = Color.LIGHTGREY;

	/**
	 * Default Edge Color in UI
	 */
	private static final Color DefaultEdgeColor = Color.BLACK;

	/**
	 * Default Use of Fill Color in UI
	 */
	private static final boolean DefaultUseFillColor = true;

	/**
	 * Default Use of Edge Color in UI
	 */
	private static final boolean DefaultUseEdgeColor = true;

	/**
	 * Default {@link LineType} in UI
	 */
	private static final LineType DefaultLineType = LineType.SOLID;

	/**
	 * Default Line Width in UI
	 */
	private static final double DefaultLineWidth = 2.0;

	/**
	 * Minimum line width
	 */
	private static final double MinLineWidth = 1.0;

	/**
	 * Maximum line width
	 */
	private static final double MaxLineWidth = 32.0;

	/**
	 * Step between line widths
	 */
	private static final double StepLineWidth = 1.0;

	/**
	 * Drawing pane width
	 */
	private static final double drawingWidth = 600.0;

	/**
	 * Drawing pane height
	 */
	private static final double drawingHeight = 600.0;

	/**
	 * Test Colors
	 */
	private static final Color[] TestColors = new Color[] {
		Color.RED,
		Color.GREEN,
		Color.BLUE,
		Color.CYAN,
		Color.MAGENTA,
		Color.YELLOW,
		Color.WHITE,
		Color.BLACK,
		Color.TRANSPARENT
	};

	/**
	 * Test Figure types
	 */
	private static final FigureType[] TestFigureTypes = new FigureType[] {
		FigureType.CIRCLE,
		FigureType.ELLIPSE,
		FigureType.RECTANGLE,
	};

	/**
	 * Test Line types
	 */
	private static final LineType[] TestLineTypes = new LineType[] {
		LineType.SOLID,
		LineType.DASHED,
		LineType.NONE
	};

//	/**
//	 * Expected Selection rectangle Stroke Dash Array
//	 */
//	private static final Double[] SelectedStrokeDashArray = new Double[] {5.0, 5.0};
//
//	/**
//	 * Expected selection rectangle fill color
//	 */
//	private static final Color SelectedFillColor = Color.TRANSPARENT;
//
//	/**
//	 * Expected selection rectangle edge color
//	 */
//	private static final Color SelectedEdgeColor = Color.GRAY;

//	/**
//	 * Default delay (in ms) to show changes in UI
//	 * @deprecated Not used anymore
//	 */
//	private static final int DefaultDelay = 100;

	/**
	 * Default number of figures to add in a model
	 */
	private static final int DefaultNumberOfFigures = 100;

	/**
	 * Global Logger
	 */
	private static Logger globalLogger = null;

	/**
	 * Swing Frame to contain {@link #panel}
	 */
	private static JFrame frame = null;

	/**
	 * JFX Panel to initialize JavaFX runtime
	 */
	private static JFXPanel panel = null;

	/**
	 * Figures combobox
	 */
	private static ComboBox<FigureType> figuresComboBox;

	/**
	 * Checkbox to use fill Color or not
	 */
	private static CheckBox useFillColor;

	/**
	 * Fill color picker
	 */
	private static ColorPicker fillColorPicker;

	/**
	 * Checkbox to use edge Color or not
	 */
	private static CheckBox useEdgeColor;

	/**
	 * Edge color picker
	 */
	private static ColorPicker edgeColorPicker;

	/**
	 * Line Type combobox
	 */
	private static ComboBox<LineType> lineTypeComboBox;

	/**
	 * Line width spinner
	 */
	private static Spinner<Double> lineWidthSpinner;

	/**
	 * Figures list view
	 */
	private static ListView<Figure> figuresListView;

	/**
	 * Drawing Pane
	 */
	private static Pane drawingPane;

	/**
	 * Flag indicating {@link #frame} has been constructed
	 */
	private static Condition frameReady = new Condition(false);

	/**
	 * Flag indicating {@link #panel} has been constructed
	 */
	private static Condition panelReady = new Condition(false);

	/**
	 * Flag indicating current test operations have been executed on
	 * JavaFX Thread
	 */
	private static Condition executed = new Condition(false);

	/**
	 * The name of the class under test (for messages)
	 */
	private static final String TestClassName = Drawing.class.getSimpleName();

	/**
	 * Logger to use during each test
	 */
	private Logger parentLogger = null;

	/**
	 * The drawing model to test
	 */
	private Drawing testModel = null;

	/**
	 * Name of the current test
	 */
	private String testName = null;

	/**
	 * Builds the JavaFX Panel containg the UI
	 * @param panel the pane containing the UI
	 */
	private static void initJavaFXUI(JFXPanel panel)
	{
		figuresComboBox = new ComboBox<>();
		figuresComboBox.getItems().addAll(FigureType.all());
		figuresComboBox.setValue(DefaultFigureType);
		useFillColor = new CheckBox("Use Fill Color");
		useFillColor.setSelected(DefaultUseFillColor);
		fillColorPicker = new ColorPicker(DefaultFillColor);
		useEdgeColor = new CheckBox("Use Edge Color");
		useEdgeColor.setSelected(DefaultUseEdgeColor);
		edgeColorPicker = new ColorPicker(DefaultEdgeColor);
		lineTypeComboBox = new ComboBox<>();
		lineTypeComboBox.getItems().addAll(LineType.all());
		lineTypeComboBox.setValue(DefaultLineType);
		lineWidthSpinner = new Spinner<>(new SpinnerValueFactory.DoubleSpinnerValueFactory(MinLineWidth,
		                                                                                   MaxLineWidth,
		                                                                                   DefaultLineWidth,
		                                                                                   StepLineWidth));
		figuresListView = new ListView<>();
		drawingPane = new Pane();
		drawingPane.setStyle("-fx-background-color: white;");
		drawingPane.setPrefSize(drawingWidth,drawingHeight);
		BorderPane pane = new BorderPane();
		VBox vbox = new VBox(figuresComboBox,
		                     useFillColor,
		                     fillColorPicker,
		                     useEdgeColor,
		                     edgeColorPicker,
		                     lineTypeComboBox,
		                     lineWidthSpinner,
		                     figuresListView);
		pane.setLeft(vbox);
		pane.setCenter(drawingPane);
		Scene scene = new Scene(pane, 700, 600, true, SceneAntialiasing.BALANCED);
		panel.setScene(scene);
	}

	/**
	 * Reset JavaFX UI to default values before next test
	 */
	private void resetJavaFXUI()
	{
		// Wait a bit so that we can see the change before resetting
//		try
//		{
//			Thread.sleep(DefaultDelay);
//		}
//		catch (InterruptedException e)
//		{
//			// Nothing, we don't care
//		}

		Platform.runLater(() -> {
			figuresListView.setItems(null);
			if (testModel != null)
			{
				testModel.unBindProperties();
				testModel.clear();
				figuresListView.getSelectionModel().getSelectedItems().removeListener(testModel);
			}
			figuresComboBox.setValue(DefaultFigureType);
			useFillColor.setSelected(DefaultUseFillColor);
			fillColorPicker.setValue(DefaultFillColor);
			useEdgeColor.setSelected(DefaultUseEdgeColor);
			edgeColorPicker.setValue(DefaultEdgeColor);
			lineTypeComboBox.setValue(DefaultLineType);
			lineWidthSpinner.getValueFactory().setValue(DefaultLineWidth);
			drawingPane.getChildren().clear();
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);
	}

	/**
	 * Launch the UI in A Swing Frame
	 */
	private static void initSWINGUI()
	{
        // This method is invoked on Swing thread
        frame = new JFrame("FX");
        panel = new JFXPanel();
        frame.setBounds(0, 0, 700, 600);
        frame.add(panel);
        frame.setVisible(true);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initJavaFXUI(panel);
                synchronized (panelReady)
				{
                	panelReady.setValue(true);
                	panelReady.notify();
				}
            }
        });
		/*
		 * wait to be notified of JavaFX thread completed setup
		 */
        synchronized (panelReady)
		{
			while (!panelReady.getValue())
			{
				try
				{
					panelReady.wait();
				}
				catch (IllegalMonitorStateException e)
				{
					fail(e.getLocalizedMessage());
				}
				catch (InterruptedException e)
				{
					fail(e.getLocalizedMessage());
				}
			}
		}
		assertNotNull(panel, "null JFXPanel");
	}

	/**
	 * Binds all JavaFX UI elements properties to {@link #testModel}
	 */
	private void bindJavaFXUIProperties()
	{
		executed.setValue(false);
		Platform.runLater(() -> {
			if (testModel != null)
			{
				Drawing.bindProperty(figuresComboBox.valueProperty(), testModel.figureTypeProperty());
				Drawing.bindProperty(useFillColor.selectedProperty(), testModel.hasFillColorProperty());
				Drawing.bindProperty(fillColorPicker.valueProperty(), testModel.fillColorProperty());
				Drawing.bindProperty(useEdgeColor.selectedProperty(), testModel.hasEdgeColorProperty());
				Drawing.bindProperty(edgeColorPicker.valueProperty(), testModel.edgeColorProperty());
				Drawing.bindProperty(lineTypeComboBox.valueProperty(), testModel.lineTypeProperty());
				Drawing.bindProperty(lineWidthSpinner.valueProperty(), testModel.lineWidthProperty());
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);
	}

	/**
	 * Setup before all tests
	 */
	@BeforeAll
	static void setUpBeforeClass()
	{
		globalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
            	initSWINGUI();
            	synchronized (frameReady)
				{
            		frameReady.setValue(true);
                	frameReady.notify();
				}
            }
        });
		/*
		 * wait to be notified of Swing thread complete setup
		 */
        synchronized (frameReady)
		{
        	while (!frameReady.getValue())
        	{
                try
        		{
                	frameReady.wait();
        		}
        		catch (IllegalMonitorStateException e)
        		{
        			fail(e.getLocalizedMessage());
        		}
        		catch (InterruptedException e)
        		{
        			fail(e.getLocalizedMessage());
        		}
        	}
		}
		assertNotNull(frame, "frame is null");

		System.out.println("-------------------------------------------------");
		System.out.println("Start of Drawing Tests");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Tear down after all tests
	 */
	@AfterAll
	static void tearDownAfterClass()
	{
		if (frame != null)
		{
			frame.dispose();
		}
		figuresListView = null;
		lineWidthSpinner = null;
		lineTypeComboBox = null;
		edgeColorPicker = null;
		fillColorPicker = null;
		figuresComboBox = null;
		panel = null;
		frame = null;
		panelReady.setValue(false);
		frameReady.setValue(false);

		System.out.println("-------------------------------------------------");
		System.out.println("End of Drawing Tests");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Setup before each test
	 * @param testInfo to extract test {@link DisplayName}
	 */
	@BeforeEach
	void setUp(TestInfo testInfo)
	{
		testName = testInfo.getDisplayName();
		parentLogger = null;
		Class<?> runningClass = getClass();
		try
		{
			parentLogger = LoggerFactory.getLogger(runningClass,
			                                       true,
			                                       null,
			                                       false,
			                                       globalLogger,
			                                       Level.WARNING);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			fail(ex.getLocalizedMessage());
		}
		assertNotNull(drawingPane, "null drawingPane");
		assertNotNull(figuresListView, "null Figures ListView");
		executed.setValue(false);
		if (!testName.equals("Drawing(javafx.scene.layout.Pane, javafx.scene.control.ListView, java.util.logging.Logger)"))
		{
			testModel = new Drawing(drawingPane, figuresListView, parentLogger);
			figuresListView.setItems(testModel);
		}
	}

	/**
	 * Tear down after each test
	 */
	@AfterEach
	void tearDown()
	{
		resetJavaFXUI();
		if (testModel != null)
		{
			testModel = null;
		}
		parentLogger = null;
		testName = null;
		executed.setValue(false);
	}

	/**
	 * Test method for {@link figures.Drawing#size()}.
	 */
	@Test
	@DisplayName("size()")
	@Order(9)
	public final void testSize()
	{
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();
		int expectedSize = 0;
		assertEquals(expectedSize,
		             testModel.size(),
		             testName + " unexpected initial size");
		/*
		 * Add a bunch of figures (with eventual doubles)
		 */
		boolean[] firstAdded = new boolean[DefaultNumberOfFigures];
		boolean[] secondAdded = new boolean[DefaultNumberOfFigures];
		Figure[] addedFigures = new Figure[DefaultNumberOfFigures];
		Platform.runLater(() -> {
			Random rand = new Random();
			for (int i = 0; i < DefaultNumberOfFigures; i++)
			{
				setRandomParamsInUI();
				double x = rand.nextDouble(drawingWidth);
				double y = rand.nextDouble(drawingHeight);
				Figure figure = testModel.initiateFigure(x, y);
				if (figure != null)
				{
					addedFigures[i] = figure;
					firstAdded[i] = testModel.add(figure);
					try
					{
						secondAdded[i] = testModel.add(figure);
					}
					catch (IllegalArgumentException e)
					{
						/*
						 * Expected: Adding an already contained element should
						 * trigger an IllegalArgumentException
						 */
						secondAdded[i] = false;
					}
				}
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		/*
		 * Then assert the number of figures in the model is the one expected
		 */
		expectedSize = DefaultNumberOfFigures;
		assertEquals(expectedSize,
		             testModel.size(),
		             testName + " unexpected number of figures");

		/*
		 * Assert we can add figures for the first time and not a second time
		 */
		for (int i = 0; i < DefaultNumberOfFigures; i++)
		{
			assertTrue(firstAdded[i],
			           testName + " unexpected first figure add failed[" + i + "]");
			assertFalse(secondAdded[i],
			            testName + " unexpected second figure add succeeded[" + i + "]");
			assertEquals(addedFigures[i],
			             testModel.get(i),
			             testName + " unexpected model figure at index " + i);
		}
	}

	/**
	 * Test method for {@link figures.Drawing#Drawing(javafx.scene.layout.Pane, javafx.scene.control.ListView, java.util.logging.Logger)}.
	 */
	@Test
	@DisplayName("Drawing(javafx.scene.layout.Pane, javafx.scene.control.ListView, java.util.logging.Logger)")
	@Order(1)
	public final void testDrawing()
	{
		System.out.println(TestClassName + '.' + testName);
		assertNull(testModel, testName + " unexpected non null model");

		Constructor<Drawing> constructor = null;
		Class<?>[] constructorsArgs = new Class<?>[3];
		constructorsArgs[0] = Pane.class;
		constructorsArgs[1] = ListView.class;
		constructorsArgs[2] = Logger.class;

		try
		{
			constructor = Drawing.class.getConstructor(constructorsArgs);
		}
		catch (NoSuchMethodException e)
		{
			fail(testName + " constructor not found");
		}
		catch (SecurityException e)
		{
			fail(testName + " constructor security exception");
		}

		if (constructor != null)
		{
			Object instance = null;
			Object[] args = new Object[3];
			args[0] = drawingPane;
			args[1] = figuresListView;
			args[2] = parentLogger;
			try
			{
				instance = constructor.newInstance(args);
			}
			catch (InstantiationException e)
			{
				fail(testName + " instanciation exception : Abstract class");
			}
			catch (IllegalAccessException e)
			{
				fail(testName + " constructor is inaccessible");
			}
			catch (IllegalArgumentException e)
			{
				fail(testName + " constructor illegal argument");
			}
			catch (InvocationTargetException e)
			{
				fail(testName + " invoked constructor throwed an exception");
			}
			catch (Exception e)
			{
				fail(testName + " setup failed");
			}

			assertNotNull(instance, testName + " unexpected null instance");
			assertEquals(Drawing.class,
			             instance.getClass(),
			             testName + " unexpected instance class "
			             + instance.getClass().getSimpleName());
			Drawing drawingInstance = (Drawing) instance;
			assertEquals(0,
			             drawingInstance.size(),
			             testName + " unexpected size on model instance");
		}
		else
		{
			fail(testName + " null constructor");
		}
	}

	/**
	 * Test method for {@link figures.Drawing#figureTypeProperty()}.
	 * Dependencies
	 * 	- {@link #testGetFigureType()}
	 */
	@Test
	@DisplayName("figureTypeProperty()")
	@Order(2)
	public final void testFigureTypeProperty()
	{
		System.out.println(TestClassName + '.' + testName);
		LogHandler handler = new LogHandler(2);
		parentLogger.addHandler(handler);
		FigureType initialUIValue = figuresComboBox.getValue();
		FigureType initialModelValue = testModel.getFigureType();
		assertNotEquals(initialUIValue,
		                initialModelValue,
		                testName + " initial Model & UI values are the same");
		assertFalse(handler.isEmpty(),
		            testName + " Logger handler has no messages after getting value from unbound property");
		LogRecord log = handler.pop();
		assertEquals(Level.WARNING,
		             log.getLevel(),
		             testName +  " unexpected log level after getting value from unbound property");
		assertEquals("Figure Type Property is not bound yet",
		             log.getMessage(),
		             testName + " unexpected log message after getting value from unbound property");
		parentLogger.removeHandler(handler);

		/*
		 * Unbound property
		 */
		ObjectProperty<FigureType> property = testModel.figureTypeProperty();
		assertEquals(initialModelValue,
		             property.get(),
		             testName + " unexpected property value from unbound property");

		/*
		 * Bind property
		 */
		Platform.runLater(() -> {
			Drawing.bindProperty(figuresComboBox.valueProperty(), testModel.figureTypeProperty());
			figuresComboBox.setValue(FigureType.ELLIPSE);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertNotEquals(initialModelValue,
		                testModel.getFigureType(),
		                testName + " unexpected figure type after ComboBox changed : old value unchanged");
		assertEquals(figuresComboBox.getValue(),
		             testModel.getFigureType(),
		             testName + " unexpected figure type after ComboBox changed");
	}

	/**
	 * Test method for {@link figures.Drawing#hasFillColorProperty()}.
	 * Dependencies:
	 * 	- {@link #testHasFill()}
	 */
	@Test
	@DisplayName("hasFillColorProperty()")
	@Order(3)
	public final void testHasFillColorProperty()
	{
		System.out.println(TestClassName + '.' + testName);
		LogHandler handler = new LogHandler(2);
		parentLogger.addHandler(handler);
		boolean initialUIValue = useFillColor.isSelected();
		boolean initialModelValue = testModel.hasFill();
		assertNotEquals(initialUIValue,
		                initialModelValue,
		                testName + " initial Model & UI values are the same");
		assertFalse(handler.isEmpty(),
		            testName + " Logger handler has no messages after getting value from unbound property");
		LogRecord log = handler.pop();
		assertEquals(Level.WARNING,
		             log.getLevel(),
		             testName +  " unexpected log level after getting value from unbound property");
		assertEquals("Has Fill Property is not bound yet",
		             log.getMessage(),
		             testName + " unexpected log message after getting value from unbound property");
		parentLogger.removeHandler(handler);

		/*
		 * Unbound property
		 */
		BooleanProperty property = testModel.hasFillColorProperty();
		assertEquals(initialModelValue,
		             property.get(),
		             testName + " unexpected property value from unbound property");

		/*
		 * Bind property
		 */
		Platform.runLater(() -> {
			Drawing.bindProperty(useFillColor.selectedProperty(), testModel.hasFillColorProperty());
			useFillColor.setSelected(!initialUIValue);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertEquals(useFillColor.isSelected(),
		             testModel.hasFill(),
		             testName + " unexpected use fill color after CheckBox changed");
	}

	/**
	 * Test method for {@link figures.Drawing#fillColorProperty()}.
	 */
	@Test
	@DisplayName("fillColorProperty()")
	@Order(4)
	public final void testFillColorProperty()
	{
		System.out.println(TestClassName + '.' + testName);
		LogHandler handler = new LogHandler(2);
		parentLogger.addHandler(handler);
		Color initialUIValue = fillColorPicker.getValue();
		Color initialModelValue = testModel.getFillColor();
		assertNotEquals(initialUIValue,
		                initialModelValue,
		                testName + " initial Model & UI values are the same");
		assertFalse(handler.isEmpty(),
		            testName + " Logger handler has no messages after getting value from unbound property");
		LogRecord log = handler.pop();
		assertEquals(Level.WARNING,
		             log.getLevel(),
		             testName +  " unexpected log level after getting value from unbound property");
		assertEquals("Fill Color property is not bound yet",
		             log.getMessage(),
		             testName + " unexpected log message after getting value from unbound property");
		parentLogger.removeHandler(handler);

		/*
		 * Unbound property
		 */
		ObjectProperty<Color> property = testModel.fillColorProperty();
		assertEquals(initialModelValue,
		             property.get(),
		             testName + " unexpected property value from unbound property");

		/*
		 * Bind property
		 */
		Platform.runLater(() -> {
			Drawing.bindProperty(fillColorPicker.valueProperty(), testModel.fillColorProperty());
			fillColorPicker.setValue(Color.RED);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertEquals(fillColorPicker.getValue(),
		             testModel.getFillColor(),
		             testName + " unexpected fill color after ColorPicker changed");
	}

	/**
	 * Test method for {@link figures.Drawing#hasEdgeColorProperty()}.
	 */
	@Test
	@DisplayName("hasEdgeColorProperty()")
	@Order(5)
	public final void testHasEdgeColorProperty()
	{
		System.out.println(TestClassName + '.' + testName);
		LogHandler handler = new LogHandler(2);
		parentLogger.addHandler(handler);
		boolean initialUIValue = useEdgeColor.isSelected();
		boolean initialModelValue = testModel.hasEdge();
		assertNotEquals(initialUIValue,
		                initialModelValue,
		                testName + " initial Model & UI values are the same");
		assertFalse(handler.isEmpty(),
		            testName + " Logger handler has no messages after getting value from unbound property");
		LogRecord log = handler.pop();
		assertEquals(Level.WARNING,
		             log.getLevel(),
		             testName +  " unexpected log level after getting value from unbound property");
		assertEquals("Has Edge Property is not bound yet",
		             log.getMessage(),
		             testName + " unexpected log message after getting value from unbound property");
		parentLogger.removeHandler(handler);

		/*
		 * Unbound property
		 */
		BooleanProperty property = testModel.hasEdgeColorProperty();
		assertEquals(initialModelValue,
		             property.get(),
		             testName + " unexpected property value from unbound property");

		/*
		 * Bind property
		 */
		Platform.runLater(() -> {
			Drawing.bindProperty(useEdgeColor.selectedProperty(), testModel.hasEdgeColorProperty());
			useEdgeColor.setSelected(!initialUIValue);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertEquals(useEdgeColor.isSelected(),
		             testModel.hasEdge(),
		             testName + " unexpected use edge color after CheckBox changed");
	}

	/**
	 * Test method for {@link figures.Drawing#edgeColorProperty()}.
	 */
	@Test
	@DisplayName("edgeColorProperty()")
	@Order(6)
	public final void testEdgeColorProperty()
	{
		System.out.println(TestClassName + '.' + testName);
		LogHandler handler = new LogHandler(2);
		parentLogger.addHandler(handler);
		Color initialUIValue = edgeColorPicker.getValue();
		Color initialModelValue = testModel.getEdgeColor();
		assertNotEquals(initialUIValue,
		                initialModelValue,
		                testName + " initial Model & UI values are the same");
		assertFalse(handler.isEmpty(),
		            testName + " Logger handler has no messages after getting value from unbound property");
		LogRecord log = handler.pop();
		assertEquals(Level.WARNING,
		             log.getLevel(),
		             testName +  " unexpected log level after getting value from unbound property");
		assertEquals("Edge Color property is not bound yet",
		             log.getMessage(),
		             testName + " unexpected log message after getting value from unbound property");
		parentLogger.removeHandler(handler);

		/*
		 * Unbound property
		 */
		ObjectProperty<Color> property = testModel.edgeColorProperty();
		assertEquals(initialModelValue,
		             property.get(),
		             testName + " unexpected property value from unbound property");

		/*
		 * Bind property
		 */
		Platform.runLater(() -> {
			Drawing.bindProperty(edgeColorPicker.valueProperty(), testModel.edgeColorProperty());
			edgeColorPicker.setValue(Color.RED);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertEquals(edgeColorPicker.getValue(),
		             testModel.getEdgeColor(),
		             testName + " unexpected edge color after ColorPicker changed");
	}

	/**
	 * Test method for {@link figures.Drawing#lineTypeProperty()}.
	 */
	@Test
	@DisplayName("lineTypeProperty()")
	@Order(7)
	public final void testLineTypeProperty()
	{
		System.out.println(TestClassName + '.' + testName);
		LogHandler handler = new LogHandler(2);
		parentLogger.addHandler(handler);
		LineType initialUIValue = lineTypeComboBox.getValue();
		LineType initialModelValue = testModel.getLineType();
		assertNotEquals(initialUIValue,
		                initialModelValue,
		                testName + " initial Model & UI values are the same");
		assertFalse(handler.isEmpty(),
		            testName + " Logger handler has no messages after getting value from unbound property");
		LogRecord log = handler.pop();
		assertEquals(Level.WARNING,
		             log.getLevel(),
		             testName +  " unexpected log level after getting value from unbound property");
		assertEquals("Line Type property is not bound yet",
		             log.getMessage(),
		             testName + " unexpected log message after getting value from unbound property");
		parentLogger.removeHandler(handler);

		/*
		 * Unbound property
		 */
		ObjectProperty<LineType> property = testModel.lineTypeProperty();
		assertEquals(initialModelValue,
		             property.get(),
		             testName + " unexpected property value from unbound property");

		/*
		 * Bind property
		 */
		Platform.runLater(() -> {
			Drawing.bindProperty(lineTypeComboBox.valueProperty(), testModel.lineTypeProperty());
			lineTypeComboBox.setValue(LineType.DASHED);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertNotEquals(initialModelValue,
		                testModel.getLineType(),
		                testName + " unexpected figure type after ComboBox changed : old value unchanged");
		assertEquals(lineTypeComboBox.getValue(),
		             testModel.getLineType(),
		             testName + " unexpected line type after ComboBox changed");
	}

	/**
	 * Test method for {@link figures.Drawing#lineWidthProperty()}.
	 */
	@Test
	@DisplayName("lineWidthProperty()")
	@Order(8)
	public final void testLineWidthProperty()
	{
		System.out.println(TestClassName + '.' + testName);
		LogHandler handler = new LogHandler(2);
		parentLogger.addHandler(handler);
		double initialUIValue = lineWidthSpinner.getValue();
		double initialModelValue = testModel.getLineWidth();
		assertNotEquals(initialUIValue,
		                initialModelValue,
		                testName + " initial Model & UI values are the same");
		assertFalse(handler.isEmpty(),
		            testName + " Logger handler has no messages after getting value from unbound property");
		LogRecord log = handler.pop();
		assertEquals(Level.WARNING,
		             log.getLevel(),
		             testName +  " unexpected log level after getting value from unbound property");
		assertEquals("Line Width property is not bound yet",
		             log.getMessage(),
		             testName + " unexpected log message after getting value from unbound property");
		parentLogger.removeHandler(handler);

		/*
		 * Unbound property
		 */
		ObjectProperty<Double> property = testModel.lineWidthProperty();
		assertEquals(initialModelValue,
		             property.get(),
		             testName + " unexpected property value from unbound property");

		/*
		 * Bind property
		 */
		Platform.runLater(() -> {
			Drawing.bindProperty(lineWidthSpinner.valueProperty(), testModel.lineWidthProperty());
			lineWidthSpinner.getValueFactory().setValue(16.0);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertNotEquals(initialModelValue,
		                testModel.getLineWidth(),
		                testName + " unexpected line width after Spinner changed : old value unchanged");
		assertEquals(lineWidthSpinner.getValue(),
		             testModel.getLineWidth(),
		             testName + " unexpected line width after Spinner changed");
	}

	/**
	 * Test method for {@link figures.Drawing#getFigureType()}.
	 */
	@Test
	@DisplayName("getFigureType()")
	@Order(10)
	public final void testGetFigureType()
	{
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();
		FigureType initialUIValue = figuresComboBox.getValue();
		FigureType initialModelValue = testModel.getFigureType();
		assertEquals(initialUIValue,
		             initialModelValue,
		             testName + " initial Model & UI values are not the same");
		Collection<FigureType> expectedValues = FigureType.all();

		for (FigureType expectedValue : expectedValues)
		{
			Platform.runLater(() -> {
				figuresComboBox.setValue(expectedValue);
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			assertEquals(expectedValue,
			             testModel.getFigureType(),
			             testName + " unexpected figure type");
		}
	}

	/**
	 * Test method for {@link figures.Drawing#hasFill()}.
	 */
	@Test
	@DisplayName("hasFill()")
	@Order(11)
	public final void testHasFill()
	{
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();
		boolean initialUIValue = useFillColor.isSelected();
		boolean initialModelValue = testModel.hasFill();
		assertEquals(initialUIValue,
		             initialModelValue,
		             testName + " initial Model & UI values are not the same");
		final boolean[] expectedValues = new boolean[]{
			true,
			false
		};
		for (boolean expectedValue : expectedValues)
		{
			Platform.runLater(() -> {
				useFillColor.setSelected(expectedValue);
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			assertEquals(expectedValue,
			             testModel.hasFill(),
			             testName + " unexpected use fill color");
		}
	}

	/**
	 * Test method for {@link figures.Drawing#getFillColor()}.
	 */
	@Test
	@DisplayName("getFillColor()")
	@Order(12)
	public final void testGetFillColor()
	{
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();
		Color initialUIValue = fillColorPicker.getValue();
		Color initialModelValue = testModel.getFillColor();
		assertEquals(initialUIValue,
		             initialModelValue,
		             testName + " initial Model & UI values are not the same");

		for (Color expectedValue : TestColors)
		{
			Platform.runLater(() -> {
				fillColorPicker.setValue(expectedValue);
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			assertEquals(expectedValue,
			             testModel.getFillColor(),
			             testName + " unexpected fill color");
		}
	}

	/**
	 * Test method for {@link figures.Drawing#hasEdge()}.
	 */
	@Test
	@DisplayName("hasEdge()")
	@Order(13)
	public final void testHasEdge()
	{
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();
		boolean initialUIValue = useEdgeColor.isSelected();
		boolean initialModelValue = testModel.hasEdge();
		assertEquals(initialUIValue,
		             initialModelValue,
		             testName + " initial Model & UI values are not the same");
		final boolean[] expectedValues = new boolean[]{
			true,
			false
		};
		for (boolean expectedValue : expectedValues)
		{
			Platform.runLater(() -> {
				useEdgeColor.setSelected(expectedValue);
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			assertEquals(expectedValue,
			             testModel.hasEdge(),
			             testName + " unexpected use edge color");
		}
	}

	/**
	 * Test method for {@link figures.Drawing#getEdgeColor()}.
	 */
	@Test
	@DisplayName("getEdgeColor()")
	@Order(14)
	public final void testGetEdgeColor()
	{
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();
		Color initialUIValue = edgeColorPicker.getValue();
		Color initialModelValue = testModel.getEdgeColor();
		assertEquals(initialUIValue,
		             initialModelValue,
		             testName + " initial Model & UI values are not the same");

		for (Color expectedValue : TestColors)
		{
			Platform.runLater(() -> {
				edgeColorPicker.setValue(expectedValue);
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			assertEquals(expectedValue,
			             testModel.getEdgeColor(),
			             testName + " unexpected fill color");
		}
	}

	/**
	 * Test method for {@link figures.Drawing#getLineType()}.
	 */
	@Test
	@DisplayName("getLineType()")
	@Order(15)
	public final void testGetLineType()
	{
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();
		LineType initialUIValue = lineTypeComboBox.getValue();
		LineType initialModelValue = testModel.getLineType();
		assertEquals(initialUIValue,
		             initialModelValue,
		             testName + " initial Model & UI values are not the same");

		for (LineType expectedValue : TestLineTypes)
		{
			Platform.runLater(() -> {
				lineTypeComboBox.setValue(expectedValue);
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			assertEquals(expectedValue,
			             testModel.getLineType(),
			             testName + " unexpected line type");
		}
	}

	/**
	 * Test method for {@link figures.Drawing#getLineWidth()}.
	 */
	@Test
	@DisplayName("getLineWidth()")
	@Order(16)
	public final void testGetLineWidth()
	{
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();
		double initialUIValue = lineWidthSpinner.getValue();
		double initialModelValue = testModel.getLineWidth();
		assertEquals(initialUIValue,
		             initialModelValue,
		             testName + " initial Model & UI values are not the same");
		final double overflow = 2.0;
		final double min = MinLineWidth - overflow;
		final double max = MaxLineWidth + overflow;
		for (double expectedValue = min; expectedValue < max; expectedValue += 1.0)
		{
			final double valueToSet = expectedValue;
			Platform.runLater(() -> {
				lineWidthSpinner.getValueFactory().setValue(valueToSet);
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			if ((expectedValue < MinLineWidth) || (expectedValue > MaxLineWidth))
			{
				assertNotEquals(expectedValue,
				                testModel.getLineWidth(),
				                testName + " unexpected out of bounds line width");
			}
			else
			{
				assertEquals(expectedValue,
				             testModel.getLineWidth(),
				             testName + " unexpected line width");
			}
		}
	}

	/**
	 * Test method for {@link figures.Drawing#getRoot()}.
	 */
	@Test
	@DisplayName("getRoot()")
	@Order(17)
	public final void testGetRoot()
	{
		System.out.println(TestClassName + '.' + testName);
		assertEquals(drawingPane,
		             testModel.getRoot(),
		             testName + " unexpected model root drawing pane");
	}

	/**
	 * Test method for {@link figures.Drawing#initiateFigure(double, double)}.
	 */
	@Test
	@DisplayName("initiateFigure(double, double)")
	@Order(18)
	public final void testInitiateFigure()
	{
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();
		double tolerance = 0.01;
		Random rand = new Random();

		for (DrawingParams params : allDrawingParams())
		{
			Platform.runLater(() -> {
				if (figuresComboBox.getValue() != params.figureType())
				{
					figuresComboBox.setValue(params.figureType());
				}
				if (!fillColorPicker.getValue().equals(params.fillColor()))
				{
					fillColorPicker.setValue(params.fillColor());
				}
				if (!edgeColorPicker.getValue().equals(params.edgeColor()))
				{
					edgeColorPicker.setValue(params.edgeColor());
				}
				if (lineTypeComboBox.getValue() != params.lineType())
				{
					lineTypeComboBox.setValue(params.lineType());
				}
				if (lineWidthSpinner.getValue() != params.lineWidth())
				{
					lineWidthSpinner.getValueFactory().setValue(params.lineWidth());
				}
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			double x = rand.nextDouble(drawingWidth);
			double y = rand.nextDouble(drawingHeight);

			Figure figure = testModel.initiateFigure(x,y);

			assertEquals(params.figureType(),
			             FigureType.fromFigure(figure),
			             testName + " unexpected figure type");
			assertEquals(params.fillColor(),
			             figure.getFillColor(),
			             testName + " unexpected figure fill color");
			assertEquals(params.edgeColor(),
			             figure.getEdgeColor(),
			             testName + " unexpected figure edge color");
			assertEquals(params.lineType(),
			             figure.getLineType(),
			             testName + " unexpected figure edge type");
			assertEquals(params.lineWidth(),
			             figure.getLineWidth(),
			             testName + " unexpected figure edge width");
			Point2D center = figure.getCenter();
			assertEquals(x,
			             center.getX(),
			             tolerance,
			             testName + " unexpected figure center X");
			assertEquals(y,
			             center.getY(),
			             tolerance,
			             testName + " unexpected figure center X");
		}
	}

	/**
	 * Test method for {@link figures.Drawing#fromShape(javafx.scene.shape.Shape)}.
	 */
	@Test
	@DisplayName("fromShape(javafx.scene.shape.Shape)")
	@Order(19)
	public final void testFromShape()
	{
		/*
		 * Scenario: Create a bunch of figures, add them to the model
		 * (which should also) add them to the drawing Pane and then
		 * search in the model for each shape found in the the #drawingPane
		 */
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();
		Random rand = new Random();

		/*
		 * Create a bunch of figures and add them to the model
		 */
		Platform.runLater(() -> {
			for (DrawingParams params : someDrawingParams())
			{
				if (figuresComboBox.getValue() != params.figureType())
				{
					figuresComboBox.setValue(params.figureType());
				}
				if (!fillColorPicker.getValue().equals(params.fillColor()))
				{
					fillColorPicker.setValue(params.fillColor());
				}
				if (!edgeColorPicker.getValue().equals(params.edgeColor()))
				{
					edgeColorPicker.setValue(params.edgeColor());
				}
				if (lineTypeComboBox.getValue() != params.lineType())
				{
					lineTypeComboBox.setValue(params.lineType());
				}
				if (lineWidthSpinner.getValue() != params.lineWidth())
				{
					lineWidthSpinner.getValueFactory().setValue(params.lineWidth());
				}
				double x = rand.nextDouble(drawingWidth);
				double y = rand.nextDouble(drawingHeight);
				Figure figure = testModel.initiateFigure(x, y);
				if (figure != null)
				{
					/*
					 * Some figure might be selected
					 */
					if ((x > (drawingWidth * 0.9)) && (y > (drawingHeight * 0.9)))
					{
						figure.setSelected(true);
					}
					testModel.add(figure);
				}
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		/*
		 * Then get all Shape children of #drawingPane and search them
		 * in #testModel
		 */
		int index = 0;
		for (Node outerNode : drawingPane.getChildren())
		{
			if (outerNode instanceof Group group)
			{
				for (Node innerNode : group.getChildren())
				{
					if (innerNode instanceof Shape shape)
					{
						/*
						 * We should find a corresponding Figure from either
						 * the shape itself or its selectionRectangle
						 */
						Figure figure = testModel.fromShape(shape);
						if (figure != null)
						{
							assertEquals(index,
							             testModel.indexOf(figure),
							             testName + " unexpected index for found figure");
							assertTrue((shape == figure.getShape()) ||
							           (shape == figure.getSelectionRectangle()),
							           testName + " found shape is neither shape nor selectionRectangle");
						}
						else
						{
							fail(testName + " unexpected Shape in drawingPane: " + shape);
						}
					}
					else
					{
						fail(testName + " Group[" +  index + "] content is not a Shape");
					}
				}
			}
			else
			{
				fail(testName + " root element found at index " + index + " is not a Group");
			}
			index++;
		}
		/*
		 * Add some more shapes in (cleaned) #drawingPane without adding them
		 * in the model and assert they can't be found in the model
		 */
		Platform.runLater(() -> {
			drawingPane.getChildren().clear();
			for (int i = 0; i < DefaultNumberOfFigures; i++)
			{
				setRandomParamsInUI();
				double x = rand.nextDouble(drawingWidth);
				double y = rand.nextDouble(drawingHeight);
				Figure figure = testModel.initiateFigure(x, y);
				if (figure != null)
				{
					drawingPane.getChildren().add(figure.getShape());
				}
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		for (Node node : drawingPane.getChildren())
		{
			assertTrue(node instanceof Shape,
			           testName + " unexpected non Shape node in drawing pane");
			Shape shape = (Shape) node;
			assertNull(testModel.fromShape(shape),
			           testName + " unexpected non null shape found in model");
		}

		/*
		 * Reset model & drawing Pane to consistent state
		 */
		Platform.runLater(() -> {
			testModel.refresh();
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);
	}

	/**
	 * Test method for {@link figures.Drawing#clearSelection()}.
	 */
	@Test
	@DisplayName("clearSelection()")
	@Order(20)
	public final void testClearSelection()
	{
		/*
		 * Add a bunch of figures, some of it selected then clear selection
		 * an checks there is no figures selected anymore
		 */
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();
		Random rand = new Random();

		/*
		 * Create a bunch of figures and add them to the model
		 */
		Platform.runLater(() -> {
			for (int i = 0; i < DefaultNumberOfFigures; i++)
			{
				setRandomParamsInUI();

				double x = rand.nextDouble(drawingWidth);
				double y = rand.nextDouble(drawingHeight);
				Figure figure = testModel.initiateFigure(x, y);
				if (figure != null)
				{
					if (figure instanceof Mouseable)
					{
						((Mouseable) figure).setLastPoint(new Point2D(x + 10.0, y + 20.0));
					}
					/*
					 * Some figure might be selected
					 */
					if ((x > (drawingWidth * 0.75)) && (y > (drawingHeight * 0.75)))
					{
						figure.setSelected(true);
					}
					testModel.add(figure);
					if (figure.isSelected())
					{
						testModel.updateSelection(testModel.size() - 1, true);
					}
				}
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		/*
		 * Finds the number of selected figures in the model
		 */
		int modelCount = 0;
		for (Figure figure : testModel)
		{
			if (figure.isSelected())
			{
				modelCount++;
			}
		}
		assertTrue(modelCount > 0,
		           testName + " unexpected model selected count");
		int listViewCount = figuresListView.getSelectionModel().getSelectedItems().size();
		assertTrue(listViewCount > 0,
		           testName + " unexpected ListView selected count");
		assertEquals(listViewCount,
		             modelCount,
		             testName + " discrepancy between model and ListView selected selected count");

		/*
		 * Clears selection and assert there is no more selected figures
		 */
		executed.setValue(false);
		Platform.runLater(() -> {
			testModel.clearSelection();
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		modelCount = 0;
		for (Figure figure : testModel)
		{
			if (figure.isSelected())
			{
				modelCount++;
			}
		}
		assertEquals(0,
		             modelCount,
		             testName + " unexpected selected count in model after clear");
		assertEquals(0,
		             figuresListView.getSelectionModel().getSelectedItems().size(),
		             testName + " unexpected selected count in ListView after clear");
	}

	/**
	 * Test method for {@link figures.Drawing#updateSelection(int, boolean)}.
	 */
	@Test
	@DisplayName("updateSelection(int, boolean)")
	@Order(21)
	public final void testUpdateSelection()
	{
		/*
		 * Add a bunch of figures (some selected, others not selected)
		 * upateSelection on each of them and check #figuresListView correctly
		 * reflects the selected figures in the model.
		 */
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();
		Random rand = new Random();

		/*
		 * Create a bunch of figures and add them to the model
		 * About 1/8 of the added figures might be selected
		 */
		Platform.runLater(() -> {
			for (int i = 0; i < DefaultNumberOfFigures; i++)
			{
				setRandomParamsInUI();

				double x = rand.nextDouble(drawingWidth);
				double y = rand.nextDouble(drawingHeight);
				Figure figure = testModel.initiateFigure(x, y);
				if (figure != null)
				{
					/*
					 * Some figure might be selected
					 */
					if ((x > (drawingWidth * 0.75)) && (y > (drawingHeight * 0.75)))
					{
						figure.setSelected(true);
					}
					testModel.add(figure);
					if (figure.isSelected())
					{
						testModel.updateSelection(testModel.size() - 1, true);
					}
				}
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		/*
		 * Assert that the list of selected figures indices provided by #figuresListView
		 * corresponds to the list of selected figures indices in the model
		 */
		List<Integer> selectedInModel = new ArrayList<>();
		for (int i = 0; i < testModel.size(); i++)
		{
			if (testModel.get(i).isSelected())
			{
				selectedInModel.add(i);
			}
		}
		List<Integer> selectedInView = figuresListView.getSelectionModel().getSelectedIndices();
		assertFalse(selectedInView.isEmpty(),
		            testName + " unexpected empty selection in view");
		assertEquals(selectedInModel,
		             selectedInView,
		             testName + " unexpected difference between selected items in model and selected items in view");

		/*
		 * Assert selected figures in model have a selection rectangle whereas
		 * non selected figures have a null selection rectangle
		 */
		for (Figure figure : testModel)
		{
			boolean selected = figure.isSelected();
			javafx.scene.shape.Rectangle selectionRectangle = figure.getSelectionRectangle();
			int expectedRootSize = selected ? 2 : 1;
			assertEquals(expectedRootSize,
			             figure.getRoot().getChildren().size(),
			             testName + " unexpected figure's root size in figure " + figure);
			if (selected)
			{
				assertNotNull(selectionRectangle,
				              testName + " unexpected null selection rectangle in selected figure");
			}
			else
			{
				assertNull(selectionRectangle,
				           testName + " unexpected non null selection rectangle in unselected figure");
			}
		}

		/*
		 * Assert selected figures in #drawingPane have a selection rectangle
		 */

		/*
		 * Then changes selection and assert again
		 */
		final int size = testModel.size();
		final int numberOfChanges = size / 2;
		int[] changedIndices = new int[numberOfChanges];
		for (int i = 0; i < changedIndices.length; i++)
		{
			changedIndices[i] = rand.nextInt(size);
		}

		executed.setValue(false);
		Platform.runLater(() -> {
			for (int i = 0; i < changedIndices.length; i++)
			{
				int j = changedIndices[i];
				Figure figure = testModel.get(j);
				boolean newState = !figure.isSelected();
				/*
				 * update selection will change selection state in
				 * #figuresListView which will in trigger the
				 * Drawing#onChanged callback to actually changes
				 * figures selected status
				 */
				testModel.updateSelection(j, newState);
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		selectedInModel.clear();
		for (int i = 0; i < testModel.size(); i++)
		{
			if (testModel.get(i).isSelected())
			{
				selectedInModel.add(i);
			}
		}
		selectedInView = figuresListView.getSelectionModel().getSelectedIndices();
		assertEquals(selectedInModel,
		             selectedInView,
		             testName + " unexpected difference between selected items in model and selected items in view");
	}

	/**
	 * Test method for {@link figures.Drawing#refresh()}.
	 */
	@Test
	@DisplayName("refresh()")
	@Order(22)
	public final void testRefresh()
	{
		/*
		 * Add a bunch of figures then
		 * artifically clears all nodes in #drawingPane and assert there is no
		 * children in #drawingPane then refresh the model to re-add figures in
		 * drawing pane and assert all figures actually are in #drawingPane
		 */
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();
		addFiguresToModel(DefaultNumberOfFigures, testName);

		List<Node> previousPaneChildren = new ArrayList<>(drawingPane.getChildren());

		Platform.runLater(() -> {
			drawingPane.getChildren().clear();
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);
		assertEquals(0,
		             drawingPane.getChildren().size(),
		             testName + " unexpected non empty drawing Pane after clear");

		Platform.runLater(() -> {
			testModel.refresh();
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		ObservableList<Node> newPaneChildren = drawingPane.getChildren();
		assertEquals(previousPaneChildren.size(),
		             newPaneChildren.size(),
		             testName + " unexpected drawing Pane children size after refresh");
		assertEquals(previousPaneChildren,
		             newPaneChildren,
		             testName + " unexpected drawing Pane children after refresh");
	}

	// /**
	//  * Test method for {@link figures.Drawing#refresh(java.util.function.Predicate)}.
	//  */
	// @Test
	// @DisplayName("refresh(java.util.function.Predicate)")
	// @Order(23)
	// public final void testRefreshPredicateOfFigure()
	// {
	// 	/*
	// 	 * Creates a bunch of figures in the model
	// 	 * Then refresh the model with a filter preserving only Circles
	// 	 * Assert there is only Circles remaining in the drawingPane
	// 	 */
	// 	System.out.println(TestClassName + '.' + testName);
	// 	bindJavaFXUIProperties();
	// 	addFiguresToModel(DefaultNumberOfFigures, testName);

	// 	/*
	// 	 * Builds the list of circles in the model we might find in the
	// 	 * #drawingPane when filtered
	// 	 */
	// 	List<Node> remainingCircles = new ArrayList<>();
	// 	for (Figure figure : testModel)
	// 	{
	// 		if (figure.getClass() == Circle.class)
	// 		{
	// 			remainingCircles.add(figure.getRoot());
	// 		}
	// 	}
	// 	assertFalse(remainingCircles.isEmpty(),
	// 	            testName + " unexpected empty list of remaining Circles");

	// 	Platform.runLater(() -> {
	// 		testModel.refresh((Figure figure) -> {
	// 			return figure.getClass() == Circle.class;
	// 		});
	// 		notifyCondition(executed);
	// 	});
	// 	waitOnCondition(executed, testName);

	// 	/*
	// 	 * Asserts the nodes remaining in #drawingPane corresponds to
	// 	 * remainingCircles
	// 	 */
	// 	ObservableList<Node> paneChildren = drawingPane.getChildren();
	// 	assertEquals(remainingCircles.size(),
	// 	             paneChildren.size(),
	// 	             testName + " unexpected pane children size after filtering");
	// 	assertEquals(remainingCircles,
	// 	             paneChildren,
	// 	             testName + " unexpected pane children after filtering");

	// 	/*
	// 	 * Reset to consistent state
	// 	 */
	// 	Platform.runLater(() -> {
	// 		testModel.refresh();
	// 		notifyCondition(executed);
	// 	});
	// 	waitOnCondition(executed, testName);
	// }

	/**
	 * Test method for {@link figures.Drawing#get(int)}.
	 */
	@Test
	@DisplayName("get(int)")
	@Order(24)
	public final void testGetInt()
	{
		/*
		 * Adds a bunch of figures to the model while keeping trace of
		 * added figures in separate collection
		 * Then assert the model contains the same thing as the separate
		 * collection
		 */
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();

		/*
		 * assert wa can't get invalid index whithout triggering an
		 * IndexOutOfBoundsException
		 */
		assertThrows(IndexOutOfBoundsException.class, () -> {
			testModel.get(DefaultNumberOfFigures);
		}, testName + " getting ouf of bound index figure suceeded");

		/*
		 * Create a bunch of figures and add them to the model
		 */
		List<Figure> expectedFigures = addFiguresToModel(DefaultNumberOfFigures, testName);

		for (int i = 0; i < DefaultNumberOfFigures; i++)
		{
			assertEquals(expectedFigures.get(i),
			             testModel.get(i),
			             testName + " unexpected model figure at index " + i);
		}

		assertThrows(IndexOutOfBoundsException.class, () -> {
			testModel.get(-1);
		}, testName + " getting ouf of bound -1 figure suceeded");
		assertThrows(IndexOutOfBoundsException.class, () -> {
			testModel.get(DefaultNumberOfFigures);
		}, testName + " getting ouf of bound " + DefaultNumberOfFigures + " figure suceeded");
	}

	/**
	 * Test method for {@link figures.Drawing#add(Figure)}.
	 */
	@Test
	@DisplayName("add(Figure)")
	@Order(25)
	public final void testAddFigure()
	{
		/*
		 * Adds a bunch of figures to the model and
		 * Assert that
		 * 	- adding a null figure will trigger a NullPointerException
		 * 	- adding a figure for the second time will trigger a IllegalArgumentException
		 */
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();

		assertThrows(NullPointerException.class, () -> {
			testModel.add(null);
		}, testName + " add(null) didn't throw NullPointerException");

		/*
		 * Add a bunch of figures (with eventual doubles)
		 */
		boolean[] firstAddition = new boolean[DefaultNumberOfFigures];
		boolean[] secondAddition = new boolean[DefaultNumberOfFigures];
		Figure[] addedFigures = new Figure[DefaultNumberOfFigures];
		LogHandler handler = new LogHandler(DefaultNumberOfFigures);
		parentLogger.addHandler(handler);
		Platform.runLater(() -> {
			Random rand = new Random();
			for (int i = 0; i < DefaultNumberOfFigures; i++)
			{
				setRandomParamsInUI();
				double x = rand.nextDouble(drawingWidth);
				double y = rand.nextDouble(drawingHeight);
				Figure figure = testModel.initiateFigure(x, y);
				if (figure != null)
				{
					addedFigures[i] = figure;
					firstAddition[i] = testModel.add(figure);
					try
					{
						secondAddition[i] = testModel.add(figure);
					}
					catch (IllegalArgumentException e)
					{
						/*
						 * Expected: Adding an already contained element should
						 * trigger an IllegalArgumentException
						 */
						secondAddition[i] = false;
					}
				}
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);
		parentLogger.removeHandler(handler);

		assertEquals(DefaultNumberOfFigures,
		             testModel.size(),
		             testName + " unexpected number of figures in model");
		List<Node> paneChildren = drawingPane.getChildren();
		assertEquals(DefaultNumberOfFigures,
		             paneChildren.size(),
		             testName + " unexpected number of figures in drawing Pane");
		/*
		 * Assert a warning message has been issued for each figure that has
		 * been added twice
		 */
		assertFalse(handler.isEmpty(),
		            testName + " unexpected empty log handler after multiple additions");
		assertEquals(DefaultNumberOfFigures,
		             handler.size(),
		             testName +  "unexpected log handler size");
		for (int i = 0; i < DefaultNumberOfFigures; i++)
		{
			LogRecord record = handler.pop();
			assertEquals(Level.SEVERE,
			             record.getLevel());
		}

		/*
		 * Assert we can add figures for the first time and not a second time
		 */
		for (int i = 0; i < DefaultNumberOfFigures; i++)
		{
			assertTrue(firstAddition[i],
			           testName + " unexpected first figure add failed[" + i + "]");
			assertFalse(secondAddition[i],
			            testName + " unexpected second figure add succeeded[" + i + "]");
			Figure modelFigure = testModel.get(i);
			assertEquals(addedFigures[i],
			             modelFigure,
			             testName + " unexpected model figure at index " + i);
			Node node = paneChildren.get(i);
			assertSame(Group.class,
			           node.getClass(),
			           testName + " unexpected node class in drawing Pane at index " + i);
			Group group = (Group) node;
			List<Node> groupChildren = group.getChildren();
			assertEquals(1,
			             groupChildren.size(),
			             testName + " unexpected Group size in drawing Pane at index " + i);
			assertSame(modelFigure.getShape(),
			           groupChildren.get(0),
			           testName + " shape in model and drawing Pane are not the same");
		}

		for (int i = 0; i < DefaultNumberOfFigures; i++)
		{
			final Figure figure = addedFigures[i];
			assertThrows(IllegalArgumentException.class, () -> {
				testModel.add(figure);
			}, testName + " add(already contained figure) succeeded");
		}
	}

	/**
	 * Test method for {@link figures.Drawing#add(int, Figure)}.
	 */
	@Test
	@DisplayName("add(int, Figure)")
	@Order(26)
	public final void testAddIntFigure()
	{
		/*
		 * Add a bunch of figures using the add(Figure) method
		 * Then add a new bunch of figures using the add(int, Figure) method,
		 * preferably using shuffled valid indices.
		 * Valid indices are >= 0 && <= size()
		 * Then check invalid indices can't be used
		 * Then check the same figures can't be added twice not matter the index
		 * Then check null figures can't be added no matter the index
		 */
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();

		assertThrows(NullPointerException.class, () -> {
			testModel.add(0, null);
		}, testName + " add(0, null) didn't throw NullPointerException");

		/*
		 * First fill the model with DefaultNumberOfFigures using add(Figure)
		 * method
		 */
		List<Figure> addedFigures = addFiguresToModel(DefaultNumberOfFigures, testName);
		Random rand = new Random();
		Platform.runLater(() -> {
			/*
			 * Then adds additional DefaultNumberOfFigures figures at random
			 * indices using add(int, Figure)
			 */
			for (int i = 0; i < DefaultNumberOfFigures; i++)
			{
				setRandomParamsInUI();
				double x = rand.nextDouble(drawingWidth);
				double y = rand.nextDouble(drawingHeight);
				Figure figure = testModel.initiateFigure(x, y);
				int j = rand.nextInt(testModel.size());
				addedFigures.add(j, figure);
				testModel.add(j, figure);
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertEquals(DefaultNumberOfFigures * 2,
		             testModel.size(),
		             testName + " unexpected model size");
		List<Node> paneChildren = drawingPane.getChildren();
		assertEquals(DefaultNumberOfFigures * 2,
		             paneChildren.size(),
		             testName + " unexpected drawing Pane children size");

		/*
		 * Assert figures have been inserted in expected places
		 */
		for (int i = 0; i < (DefaultNumberOfFigures * 2); i++)
		{
			Figure modelFigure = testModel.get(i);
			assertEquals(addedFigures.get(i),
			             modelFigure,
			             testName + " unexpected model figure at index " + i);
			Node node = paneChildren.get(i);
			assertSame(Group.class,
			           node.getClass(),
			           testName + " unexpected node class in drawing Pane at index " + i);
			Group group = (Group) node;
			List<Node> groupChildren = group.getChildren();
			assertEquals(1,
			             groupChildren.size(),
			             testName + " unexpected Group size in drawing Pane at index " + i);
			assertSame(modelFigure.getShape(),
			           groupChildren.get(0),
			           testName + " shape in model and drawing Pane are not the same");
		}

		/*
		 * Assert we can't add the same figure twice
		 */
		for (int i = 0; i < DefaultNumberOfFigures; i++)
		{
			final Figure figure = addedFigures.get(i);
			assertThrows(IllegalArgumentException.class, () -> {
				testModel.add(rand.nextInt(testModel.size()), figure);
			}, testName + " add(index, already contained figure) succeeded");
		}

		/*
		 * Assert we can still add at bounds indices
		 */
		int[] indices = new int[] {
			0,
			(testModel.size()+1)
		};
		for (int i : indices)
		{
			executed.setValue(false);
			final double x = rand.nextDouble(drawingWidth);
			final double y = rand.nextDouble(drawingHeight);
			Figure figure = testModel.initiateFigure(x, y);
			Platform.runLater(() -> {
				setRandomParamsInUI();
				try
				{
					testModel.add(i, figure);
				}
				catch (IndexOutOfBoundsException e)
				{
					fail(testName + " failed to add figure at index" + i);
				}
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			assertEquals(figure,
			             testModel.get(i),
			             testName + " unexpected figure at index " + i);
			paneChildren = drawingPane.getChildren();
			assertEquals(testModel.size(),
			             paneChildren.size(),
			             testName + " unexpected drawign Pane children size");
			Node node = paneChildren.get(i);
			assertSame(Group.class,
			           node.getClass(),
			           testName + " unexpected node class in drawing Pane at index " + i);
			Group group = (Group) node;
			List<Node> groupChildren = group.getChildren();
			assertEquals(1,
			             groupChildren.size(),
			             testName + " unexpected Group size in drawing Pane at index " + i);
			assertSame(figure.getShape(),
			           groupChildren.get(0),
			           testName + " shape in model and drawing Pane are not the same");
		}

		/*
		 * Assert we can't add at invalid indexes
		 */
		indices = new int[] {
			-1,
			(testModel.size()+1)
		};
		for (int i : indices)
		{
			Figure figure;
			executed.setValue(false);
			Platform.runLater(() -> {
				setRandomParamsInUI();
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			final double x = rand.nextDouble(drawingWidth);
			final double y = rand.nextDouble(drawingHeight);
			figure = testModel.initiateFigure(x, y);

			assertThrows(IndexOutOfBoundsException.class, () -> {
				testModel.add(i, figure);
			}, testName + " addition at index " + i + " unexpectedly succeded");
		}
	}


	/**
	 * Test method for {@link figures.Drawing#set(int, Figure)}.
	 */
	@Test
	@DisplayName("set(int, Figure)")
	@Order(27)
	public final void testSetIntFigure()
	{
		/*
		 * Add a bunch of figures using the add(Figure) method
		 * Then add a new bunch of figures using the add(int, Figure) method,
		 * preferably using shuffled valid indices.
		 * Valid indices are >= 0 && <= size()
		 * Then check invalid indices can't be used
		 * Then check the same figures can't be added twice not matter the index
		 * Then check null figures can't be added no matter the index
		 */
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();

		assertThrows(NullPointerException.class, () -> {
			testModel.set(0, null);
		}, testName + " set(0, null) didn't throw NullPointerException");

		/*
		 * First fill the model with DefaultNumberOfFigures using add(Figure)
		 * method
		 */
		List<Figure> addedFigures = addFiguresToModel(DefaultNumberOfFigures, testName);
		List<Integer> shuffledIndices = shuffleIndicesOf(DefaultNumberOfFigures);
		Random rand = new Random();
		Platform.runLater(() -> {
			/*
			 * Then replace DefaultNumberOfFigures figures at shuffled
			 * indices using set(int, Figure)
			 */
			for (int i = 0; i < DefaultNumberOfFigures; i++)
			{
				setRandomParamsInUI();
				double x = rand.nextDouble(drawingWidth);
				double y = rand.nextDouble(drawingHeight);
				Figure figure = testModel.initiateFigure(x, y);
				int j = shuffledIndices.get(i);
				addedFigures.set(j, figure);
				testModel.set(j, figure);
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertEquals(DefaultNumberOfFigures,
		             testModel.size(),
		             testName + " unexpected model size");
		List<Node> paneChildren = drawingPane.getChildren();
		assertEquals(testModel.size(),
		             paneChildren.size(),
		             testName + " unexpected drawing Pane children size");

		/*
		 * Assert figures have been inserted in expected places
		 */
		for (int i = 0; i < DefaultNumberOfFigures; i++)
		{
			assertEquals(addedFigures.get(i),
			             testModel.get(i),
			             testName + " unexpected model figure at index " + i);
			Node node = paneChildren.get(i);
			assertSame(Group.class,
			           node.getClass(),
			           testName + " unexpected node class in drawing Pane at index " + i);
			Group group = (Group) node;
			assertEquals(1,
			             group.getChildren().size(),
			             testName + " unexpected Group size in drawing Pane at index " + i);
			assertSame(testModel.get(i).getShape(),
			           group.getChildren().get(0),
			           testName + " shape in model and drawing Pane are not the same");
		}

		/*
		 * Assert we can't add the same figure twice
		 */
		for (int i = 0; i < DefaultNumberOfFigures; i++)
		{
			final Figure figure = addedFigures.get(i);
			assertThrows(IllegalArgumentException.class, () -> {
				testModel.add(rand.nextInt(testModel.size()), figure);
			}, testName + " add(index, already contained figure) succeeded");
		}

		/*
		 * Assert we can still add at bounds indices
		 */
		int[] indices = new int[] {
			0,
			(testModel.size()+1)
		};
		for (int i : indices)
		{
			executed.setValue(false);
			final double x = rand.nextDouble(drawingWidth);
			final double y = rand.nextDouble(drawingHeight);
			Figure figure = testModel.initiateFigure(x, y);
			Platform.runLater(() -> {
				setRandomParamsInUI();
				try
				{
					testModel.add(i, figure);
				}
				catch (IndexOutOfBoundsException e)
				{
					fail(testName + " failed to add figure at index" + i);
				}
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			assertEquals(figure,
			             testModel.get(i),
			             testName + " unexpected figure at index " + i);
		}

		/*
		 * Assert we can't add at invalid indexes
		 */
		indices = new int[] {
			-1,
			(testModel.size()+1)
		};
		for (int i : indices)
		{
			Figure figure;
			executed.setValue(false);
			Platform.runLater(() -> {
				setRandomParamsInUI();
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			final double x = rand.nextDouble(drawingWidth);
			final double y = rand.nextDouble(drawingHeight);
			figure = testModel.initiateFigure(x, y);

			assertThrows(IndexOutOfBoundsException.class, () -> {
				testModel.add(i, figure);
			}, testName + " addition at index " + i + " unexpectedly succeded");
		}
	}

	/**
	 * Test method for {@link figures.Drawing#remove(Object)}.
	 */
	@Test
	@DisplayName("remove(Object)")
	@Order(28)
	public final void testRemoveObject()
	{
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();

		/*
		 * Add a bunch of figures then
		 * 	- try to remove uncontained figures (should fail)
		 * 	- try to remove other objects (should fail)
		 * 	- try to remove null object (should fail)
		 * 	- try to remove contained figures (should succeed)
		 */
		Figure[] addedFigures = new Figure[DefaultNumberOfFigures];
		Platform.runLater(() -> {
			Random rand = new Random();
			for (int i = 0; i < DefaultNumberOfFigures; i++)
			{
				setRandomParamsInUI();
				double x = rand.nextDouble(drawingWidth);
				double y = rand.nextDouble(drawingHeight);
				Figure figure = testModel.initiateFigure(x, y);
				if (figure != null)
				{
					addedFigures[i] = figure;
					testModel.add(figure);
				}
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		/*
		 * Assert we can't remove null figure
		 */
		boolean[] removedAt = new boolean[DefaultNumberOfFigures];
		executed.setValue(false);
		Platform.runLater(() -> {
			removedAt[0] = testModel.remove(null);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertFalse(removedAt[0],
		            testName + " unexpected removed null object in model");

		/*
		 * Assert we can't remove not contained figures
		 */
		executed.setValue(false);
		Platform.runLater(() -> {
			Random rand = new Random();
			for (int i = 0; i < DefaultNumberOfFigures; i++)
			{
				setRandomParamsInUI();
				double x = rand.nextDouble(drawingWidth);
				double y = rand.nextDouble(drawingHeight);
				Figure figure = testModel.initiateFigure(x, y);
				removedAt[i] = testModel.remove(figure);
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		for (int i = 0; i < DefaultNumberOfFigures; i++)
		{
			assertFalse(removedAt[i],
			            testName + " unexpectedly removed figure at index " + i);
		}

		/*
		 * Assert we can remove existing figures in the model
		 */
		List<Integer> shuffledIndices = shuffleIndicesOf(DefaultNumberOfFigures);
		executed.setValue(false);
		Platform.runLater(() -> {
			for (int i = 0; i < DefaultNumberOfFigures; i++)
			{
				int j = shuffledIndices.get(i);
				removedAt[j] = testModel.remove(addedFigures[j]);
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertTrue(testModel.isEmpty(),
		           testName + " unexpected non empty model");
		assertTrue(drawingPane.getChildren().isEmpty(),
		           testName + " unexpected non empty drawing Pane");
		for (int i = 0; i < DefaultNumberOfFigures; i++)
		{
			assertTrue(removedAt[i],
			           testName + " unexpectedly failed to remove figure at index " + i);
		}
	}

	/**
	 * Test method for {@link figures.Drawing#remove(int)}.
	 */
	@Test
	@DisplayName("remove(int)")
	@Order(29)
	public final void testRemoveInt()
	{
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();
		/*
		 * Add a bunch of figures then
		 * 	- try to remove invalid indices (should fail)
		 * 	- try to remove valid indices (should succeed)
		 */
		List<Figure> addedFigures = new ArrayList<>(DefaultNumberOfFigures);
		Platform.runLater(() -> {
			Random rand = new Random();
			for (int i = 0; i < DefaultNumberOfFigures; i++)
			{
				setRandomParamsInUI();
				double x = rand.nextDouble(drawingWidth);
				double y = rand.nextDouble(drawingHeight);
				Figure figure = testModel.initiateFigure(x, y);
				if (figure != null)
				{
					addedFigures.add(figure);
					testModel.add(figure);
				}
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		Figure[] removedAt = new Figure[DefaultNumberOfFigures];

		/*
		 * Assert we can't remove invalid index
		 */
		int[] invalidIndices = new int[] {
			-1,
			DefaultNumberOfFigures
		};
		executed.setValue(false);
		Platform.runLater(() -> {
			for (int i = 0; i < invalidIndices.length; i++)
			{
				try
				{
					removedAt[i] = testModel.remove(invalidIndices[i]);
				}
				catch (IndexOutOfBoundsException e)
				{
					// Expected !
					removedAt[i] = null;
				}
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		for (int i = 0; i < invalidIndices.length; i++)
		{
			assertNull(removedAt[i],
			           testName + " unexpectedly non null removed figure at index " + i);
		}

		/*
		 * Assert we can remove figures at valid indices
		 */
		List<Integer> shuffledIndices = shuffleIndicesOf(DefaultNumberOfFigures);
		executed.setValue(false);
		Platform.runLater(() -> {
			for (int i = 0; i < DefaultNumberOfFigures; i++)
			{
				int j = shuffledIndices.get(i);
				if (j < addedFigures.size())
				{
					int k = testModel.indexOf(addedFigures.get(j));
					try
					{
						removedAt[j] = testModel.remove(k);

					}
					catch (IndexOutOfBoundsException e)
					{
						// Unexpected!
						removedAt[j] = null;
					}
				}
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertTrue(testModel.isEmpty(),
		           testName + " unexpected non empty model");
		assertTrue(drawingPane.getChildren().isEmpty(),
		           testName + " unexpected non empty drawing Pane");
		for (int i = 0; i < DefaultNumberOfFigures; i++)
		{
			assertNotNull(removedAt[i],
			              testName + " unexpected null removed figure at index " + i);
			assertEquals(addedFigures.get(i),
			             removedAt[i],
			             testName + " unexpected frmoved figure a index " + i);
		}
	}

	/**
	 * Test method for {@link figures.Drawing#clear()}.
	 */
	@Test
	@DisplayName("clear()")
	@Order(30)
	public final void testClear()
	{
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();
		/*
		 * Add a bunch of figures, then clears the model and
		 * 	- Assert the model is empty as well as the drawingPane
		 */
		Platform.runLater(() -> {
			Random rand = new Random();
			for (int i = 0; i < DefaultNumberOfFigures; i++)
			{
				setRandomParamsInUI();
				double x = rand.nextDouble(drawingWidth);
				double y = rand.nextDouble(drawingHeight);
				Figure figure = testModel.initiateFigure(x, y);
				if (figure != null)
				{
					testModel.add(figure);
				}
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertFalse(testModel.isEmpty(),
		            testName + " unexpected empty model before clear");
		assertFalse(drawingPane.getChildren().isEmpty(),
		            testName + " unexpected empty drawing Pane before clear");

		executed.setValue(false);
		Platform.runLater(() -> {
			testModel.clear();
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertTrue(testModel.isEmpty(),
		           testName + " unexpected non empty model after clear");
		assertTrue(drawingPane.getChildren().isEmpty(),
		           testName + " unpexected non empty drawing Pane after clear");
	}

	// /**
	//  * Test method for {@link figures.Drawing#filtered(java.util.function.Predicate)}.
	//  */
	// @Test
	// @DisplayName("filtered(java.util.function.Predicate)")
	// @Order(31)
	// public final void testFiltered()
	// {
	// 	/*
	// 	 * Add a bunch of figures then
	// 	 * Filters the model with a filter preserving only Circles
	// 	 * Set this filtered content as #figuresListView content
	// 	 * Assert #figuresListView items are correct
	// 	 * Assert #drawingPane content does not contains anything else than Circles
	// 	 */
	// 	System.out.println(TestClassName + '.' + testName);
	// 	bindJavaFXUIProperties();
	// 	Platform.runLater(() -> {
	// 		Random rand = new Random();
	// 		// Add at least one Circle
	// 		figuresComboBox.setValue(FigureType.CIRCLE);
	// 		double x = rand.nextDouble(drawingWidth);
	// 		double y = rand.nextDouble(drawingHeight);
	// 		Figure figure = testModel.initiateFigure(x, y);
	// 		testModel.add(figure);
	// 		for (int i = 0; i < DefaultNumberOfFigures; i++)
	// 		{
	// 			setRandomParamsInUI();
	// 			x = rand.nextDouble(drawingWidth);
	// 			y = rand.nextDouble(drawingHeight);
	// 			figure = testModel.initiateFigure(x, y);
	// 			testModel.add(figure);
	// 		}
	// 		notifyCondition(executed);
	// 	});
	// 	waitOnCondition(executed, testName);

	// 	/*
	// 	 * Builds the list of Circles in the model
	// 	 */
	// 	List<Figure> modelCircles = new ArrayList<>();
	// 	for (Figure figure : testModel)
	// 	{
	// 		if (figure instanceof Circle)
	// 		{
	// 			modelCircles.add(figure);
	// 		}
	// 	}
	// 	System.out.println(testName + " " + modelCircles.size() + " circles");
	// 	assertFalse(modelCircles.isEmpty(),
	// 	            testName + " unexpected no circles in the model");

	// 	executed.setValue(false);
	// 	Platform.runLater(() -> {
	// 		figuresListView.setItems(testModel.filtered((Figure figure) -> {
	// 			return figure instanceof Circle;
	// 		}));
	// 		notifyCondition(executed);
	// 	});
	// 	waitOnCondition(executed, testName);

	// 	assertEquals(modelCircles,
	// 	             figuresListView.getItems(),
	// 	             testName + " unexpected list of Figure after filtering in ListView");

	// 	ObservableList<Node> paneChildren = drawingPane.getChildren();
	// 	assertEquals(modelCircles.size(),
	// 	             paneChildren.size(),
	// 	             testName + " unexpected drawing Pane children size");
	// 	for (int i = 0; i < modelCircles.size(); i++)
	// 	{
	// 		Node node = paneChildren.get(i);
	// 		assertSame(Group.class,
	// 		           node.getClass(),
	// 		           testName + " unexpected node class in drawing Pane at index " + i);
	// 		Group group = (Group) node;
	// 		List<Node> groupChildren = group.getChildren();
	// 		assertEquals(1,
	// 		             groupChildren.size(),
	// 		             testName + " unexpected Group size in drawing Pane at index " + i);
	// 		assertSame(modelCircles.get(i).getShape(),
	// 		           groupChildren.get(0),
	// 		           testName + " shape in model and drawing Pane are not the same");
	// 	}
	// 	/*
	// 	 * Reset UI to full testModel
	// 	 */
	// 	executed.setValue(false);
	// 	Platform.runLater(() -> {
	// 		figuresListView.setItems(testModel);
	// 		testModel.refresh();
	// 		notifyCondition(executed);
	// 	});
	// 	waitOnCondition(executed, testName);
	// }

	/**
	 * Test method for {@link figures.Drawing#createMemento()}.
	 */
	@Test
	@DisplayName("createMemento()")
	@Order(32)
	public final void testCreateMemento()
	{
		/*
		 * Add a bunch of figures then
		 * Creates a memento of the current state
		 * 	- the memento contains the same figures as the current state, in
		 * 	the same order but with different instances
		 */
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();
		addFiguresToModel(DefaultNumberOfFigures, testName);

		Memento<Figure> memento = testModel.createMemento();
		assertEquals(testModel.size(),
		             memento.getState().size(),
		             testName + " unexpected Memento state size");
		Iterator<Figure> modelIt = testModel.iterator();
		Iterator<Figure> mementoIt = memento.getState().iterator();
		int index = 0;

		while (modelIt.hasNext() && mementoIt.hasNext())
		{
			Figure modelFigure = modelIt.next();
			Figure mementoFigure = mementoIt.next();

			assertNotSame(modelFigure,
			              mementoFigure,
			              testName
			                  + " unexpected same figure in model and memento at index "
			                  + index);
			assertEquals(modelFigure,
			             mementoFigure,
			             testName
			                 + " unexpected inequality between model and memento figures at index "
			                 + index);
			index++;
		}
		assertEquals(modelIt.hasNext(),
		             mementoIt.hasNext(),
		             testName + " unexpected memento iterator state at the end of model iteration");
	}

	/**
	 * Test method for {@link figures.Drawing#setMemento(history.Memento)}.
	 */
	@Test
	@DisplayName("setMemento(history.Memento)")
	@Order(33)
	public final void testSetMemento()
	{
		/*
		 * Add a bunch of figures
		 * Creates a memento
		 * Then Clears the model
		 * Set the previously saved memento as new state
		 * Compare new state content with memento content
		 */
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();
		addFiguresToModel(DefaultNumberOfFigures, testName);

		Memento<Figure> memento = testModel.createMemento();
		assertEquals(testModel.size(),
		             memento.getState().size(),
		             testName + " unexpected Memento state size");

		Platform.runLater(() -> {
			testModel.clear();
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertEquals(0,
		             testModel.size(),
		             testName + " unexpected non empty model after clear");

		Platform.runLater(() -> {
			testModel.setMemento(memento);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertEquals(memento.getState().size(),
		             testModel.size(),
		             testName + " unexpected model size after setMemento");

		Iterator<Figure> mementoIt = memento.getState().iterator();
		Iterator<Figure> modelIt = testModel.iterator();
		int index = 0;
		while (mementoIt.hasNext() && modelIt.hasNext())
		{
			assertSame(mementoIt.next(),
			           modelIt.next(),
			           testName + " unexpected model figure compared to memento "
			           	+ "figure at index " + index);
			index++;
		}
		assertEquals(mementoIt.hasNext(),
		             modelIt.hasNext(),
		             testName + " unexpected model iterator state after memento "
		             	+ "iteration");
	}

	/**
	 * Test method for {@link figures.Drawing#onChanged(javafx.collections.ListChangeListener.Change)}.
	 */
	@Test
	@DisplayName("onChanged(javafx.collections.ListChangeListener.Change)")
	@Order(34)
	public final void testOnChanged()
	{
		/*
		 * Add a bunch of figures (all unselected)
		 * Then changes selection on some of them ONLY through
		 * #figuresListView's selection model
		 * assert onChanged has been called to actually change
		 * selected figures in the model
		 */
		System.out.println(TestClassName + '.' + testName);
		bindJavaFXUIProperties();
		Random rand = new Random();

		/*
		 * Create a bunch of figures and add them to the model
		 */
		Platform.runLater(() -> {
			for (int i = 0; i < DefaultNumberOfFigures; i++)
			{
				setRandomParamsInUI();

				double x = rand.nextDouble(drawingWidth);
				double y = rand.nextDouble(drawingHeight);
				Figure figure = testModel.initiateFigure(x, y);
				if (figure != null)
				{
					testModel.add(figure);
				}
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		/*
		 * Then select a bunch of figures and
		 * assert selected figures from #figuresListView are the same as
		 * selected figures from model
		 */
		final int size = testModel.size();
		final int numberOfChanges = size / 2;
		int[] changedIndices = new int[numberOfChanges];
		MultipleSelectionModel<Figure> selectionModel = figuresListView.getSelectionModel();
		for (int i = 0; i < changedIndices.length; i++)
		{
			changedIndices[i] = rand.nextInt(size);
		}

		Platform.runLater(() -> {
			for (int i = 0; i < changedIndices.length; i++)
			{
				selectionModel.select(i);
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		List<Integer> selectedInView = selectionModel.getSelectedIndices();
		assertFalse(selectedInView.isEmpty(),
		            testName + " unexpected empty selection in view");

		List<Integer> selectedInModel = new ArrayList<>();
		for (int i = 0; i < testModel.size(); i++)
		{
			Figure figure = testModel.get(i);
			if (figure.isSelected())
			{
				selectedInModel.add(i);
			}
		}
		assertEquals(selectedInView,
		             selectedInModel,
		             testName + " unexpected difference between selected items in view and selected items in model");

		Platform.runLater(() -> {
			selectionModel.clearSelection();
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertTrue(selectionModel.getSelectedIndices().isEmpty(),
		           testName + " unexpected non empty selection in view");

		selectedInModel.clear();
		for (int i = 0; i < testModel.size(); i++)
		{
			if (testModel.get(i).isSelected())
			{
				selectedInModel.add(i);
			}
		}
		assertTrue(selectedInModel.isEmpty(),
		           testName + " unexpected non empty selection in model");
	}

	/**
	 * Wait on provided condition to be notified (only if this condition is
	 * false). And reset the condition value to false before returning
	 * @param condition the condition to wait on
	 * @param testName the name of the test this method is used in
	 */
	private void waitOnCondition(Condition condition, String testName)
	{
		synchronized (condition)
		{
			if (!condition.getValue())
			{
				try
				{
					condition.wait();
				}
				catch (IllegalMonitorStateException e)
				{
					fail(testName + " " + e.getLocalizedMessage());
				}
				catch (InterruptedException e)
				{
					fail(testName + " " + e.getLocalizedMessage());
				}
			}
		}
		condition.setValue(false);
	}

	/**
	 * Sets condition's value to true and notifies any waiting thread on
	 * condition
	 * @param condition the condition to update and notify on
	 */
	private void notifyCondition(Condition condition)
	{
		synchronized (condition)
		{
			condition.setValue(true);
			condition.notify();
		}
	}

	/**
	 * Adds random figures to model and assert the number of figures in the
	 * model and in the {@value #drawingPane} are equal to the provided number
	 * of figures to add.
	 * @param number the number of random figures to add to the model
	 * @param testName the name of the test to used this method
	 * @return The list of added figures for later comparison
	 * @implNote {@link #bindJavaFXUIProperties()} should be called before this
	 * method is called.
	 */
	private List<Figure> addFiguresToModel(int number, String testName)
	{
		List<Figure> addedFigures = new ArrayList<>(number);
		executed.setValue(false);
		Platform.runLater(() -> {
			Random rand = new Random();
			for (int i = 0; i < number; i++)
			{
				setRandomParamsInUI();
				double x = rand.nextDouble(drawingWidth);
				double y = rand.nextDouble(drawingHeight);
				Figure figure = testModel.initiateFigure(x, y);
				if (figure != null)
				{
					addedFigures.add(figure);
					testModel.add(figure);
				}
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertEquals(number,
		             testModel.size(),
		             testName + " unexpected number of figures in model");
		assertEquals(number,
		             drawingPane.getChildren().size(),
		             testName + " unexpected number of figures in drawing Pane");

		return addedFigures;
	}

	/**
	 * Generates a grawing sequence of [0..number] integer indices a shuffles
	 * this list.
	 * @param number the number of elements in the list
	 * @return a list containing shuffled unique elements between 0 and number - 1
	 */
	private static List<Integer> shuffleIndicesOf(int number)
	{
		List<Integer> shuffledIndices = new ArrayList<>(number);
		for (int i = 0; i < number; i++)
		{
			shuffledIndices.add(i);
		}
		Collections.shuffle(shuffledIndices);
		return shuffledIndices;
	}

	/**
	 * Generates a collection of drawing params with a fair amount of all
	 * possible drawing parameters
	 * @return a collection of drawing params
	 */
	private static Collection<DrawingParams> allDrawingParams()
	{
		Collection<DrawingParams> list = new ArrayList<>(TestFigureTypes.length
		    * TestColors.length * TestColors.length * TestLineTypes.length
		    * (int) (MaxLineWidth - MinLineWidth));
		for (FigureType figureType : TestFigureTypes)
		{
			for (Color fillColor : TestColors)
			{
				for (Color edgeColor : TestColors)
				{
					for (LineType lineType : TestLineTypes)
					{
						for (double width = MinLineWidth; width < MaxLineWidth; width++)
						{
							list.add(new DrawingParams(figureType,
							                           fillColor,
							                           edgeColor,
							                           lineType,
							                           width));
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * Generates a collection of drawing params with a small amount of all
	 * possible drawing parameters
	 * @return a collection of drawing params
	 */
	private static Collection<DrawingParams> someDrawingParams()
	{
		Color[] subTestColors = new Color[3];
		for (int i = 0; i < Math.min(3, TestColors.length); i++)
		{
			subTestColors[i] = TestColors[i];
		}
		Collection<DrawingParams> list =
		    new ArrayList<>(TestFigureTypes.length * subTestColors.length
		        * subTestColors.length * TestLineTypes.length * 5);
		for (FigureType figureType : TestFigureTypes)
		{
			for (Color fillColor : subTestColors)
			{
				for (Color edgeColor : subTestColors)
				{
					for (LineType lineType : TestLineTypes)
					{
						for (double width = 4.0; width < 9.0; width++)
						{
							list.add(new DrawingParams(figureType,
							                           fillColor,
							                           edgeColor,
							                           lineType,
							                           width));
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * Builds random drawing parameters
	 * @return a record containing random drawing parameters
	 */
	private static DrawingParams randomParams()
	{
		Random rand = new Random();
		FigureType figureType = TestFigureTypes[rand.nextInt(TestFigureTypes.length)];
		Color fillColor = TestColors[rand.nextInt(TestColors.length)];
		Color edgeColor = TestColors[rand.nextInt(TestColors.length)];
		LineType lineType = TestLineTypes[rand.nextInt(TestLineTypes.length)];
		double lineWidth = MinLineWidth + rand.nextInt((int)(MaxLineWidth - MinLineWidth));
		return new DrawingParams(figureType,
		                         fillColor,
		                         edgeColor,
		                         lineType,
		                         lineWidth);
	}

	/**
	 * Sets Random drawing parameters in UI assuming {@link #figuresComboBox},
	 * {@link #fillColorPicker},
	 * {@link #edgeColorPicker},
	 * {@link #lineTypeComboBox} and
	 * {@link #lineWidthSpinner} are not null
	 */
	private static void setRandomParamsInUI()
	{
		DrawingParams params = randomParams();
		if (figuresComboBox.getValue() != params.figureType())
		{
			figuresComboBox.setValue(params.figureType());
		}
		if (!fillColorPicker.getValue().equals(params.fillColor()))
		{
			fillColorPicker.setValue(params.fillColor());
		}
		if (!edgeColorPicker.getValue().equals(params.edgeColor()))
		{
			edgeColorPicker.setValue(params.edgeColor());
		}
		if (lineTypeComboBox.getValue() != params.lineType())
		{
			lineTypeComboBox.setValue(params.lineType());
		}
		if (lineWidthSpinner.getValue() != params.lineWidth())
		{
			lineWidthSpinner.getValueFactory().setValue(params.lineWidth());
		}
	}

	/**
	 * Internal condition used to wait and notify on either
	 * <ul>
	 * <li>{@link #frame} to be built on SWING thread</li>
	 * <li>{@link #panel} to be built on JavaFX thread</li>
	 * <li>Or any JavaFX operation that need to be performed on the JavaFX
	 * Thread</li>
	 * </ul>
	 * @author davidroussel
	 */
	private static class Condition
	{
		/**
		 * condition value
		 */
		private boolean value;

		/**
		 * Valued constructor
		 * @param value the initial value of the condition
		 */
		public Condition(boolean value)
		{
			this.value = value;
		}

		/**
		 * Value setter
		 * @param value the value to set
		 */
		synchronized public void setValue(boolean value)
		{
			this.value = value;
		}

		/**
		 * Value getter
		 * @return the current value
		 */
		public boolean getValue()
		{
			return value;
		}
	}

	/**
	 * Record to store drawing parameters.
	 * @param figureType the figure type to draw
	 * @param fillColor the fill color to draw
	 * @param edgeColor the edge color to draw
	 * @param lineType the line type to draw
	 * @param lineWidth the line width to draw
	 */
	private static record DrawingParams(FigureType figureType,
	                                    Color fillColor,
	                                    Color edgeColor,
	                                    LineType lineType,
	                                    double lineWidth)
	{
	}
}
