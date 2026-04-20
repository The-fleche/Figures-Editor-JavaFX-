package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import figures.Ellipse;
import figures.Figure;
import figures.Mouseable;
import figures.enums.FigureType;
import figures.enums.LineType;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import logger.LoggerFactory;
import utils.ColorFactory;

/**
 * Test class for all {@link Figure}s
 * @author davidroussel
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Figures")
class FigureTest
{
	/**
	 * Different natures of figures to test
	 */
	@SuppressWarnings("unchecked")
	private static final Class<? extends Figure>[] figureTypes =
	(Class<? extends Figure>[]) new Class<?>[]
	{
		Ellipse.class,
		// TODO 202 FigureTest#figureTypes uncomment the following classes when Circle is ready
//		Circle.class,
		// TODO 302 uncomment the following when Rectangle is ready
//		Rectangle.class
	};

	/**
	 * Different natures of expected internal shapes
	 */
	@SuppressWarnings("unchecked")
	private static final Class<? extends Shape>[] shapeTypes =
	(Class<? extends Shape>[]) new Class<?>[]
	{
		javafx.scene.shape.Ellipse.class,
		javafx.scene.shape.Circle.class,
		javafx.scene.shape.Rectangle.class
	};

	/**
	 * The figure under test
	 */
	private Figure testFigure = null;

	/**
	 * The class of the figure under test
	 */
	private Class<? extends Figure> testFigureClass = null;

	/**
	 * The name of the figure under test
	 */
	private String testFigureClassName = null;

	/**
	 * Tolerance used in numerical comparisons
	 */
	private static final double tolerance = Figure.threshold;

	/**
	 * Global Logger
	 */
	private static Logger globalLogger = null;

	/**
	 * Logger to use during each test
	 */
	private Logger parentLogger;

	/**
	 * Figure class provider used for parameterized tests requiring the type of
	 * figure
	 * @return a stream of Figure Classes to use in each ParameterizedTest
	 */
	private static Stream<Class<? extends Figure>> figureClassesProvider()
	{
		return Stream.of(figureTypes);
	}

	/**
	 * Default fill color
	 */
	private final static Color DefaultFillColor = Color.WHITE;

	/**
	 * default edge color
	 */
	private final static Color DefaultEdgeColor = Color.BLACK;

	/**
	 * Default line type
	 */
	private final static LineType DefaultLineType = LineType.SOLID;

	/**
	 * Default line width
	 */
	private final static double DefaultLineWidth = 2.0;

	/**
	 * Default X location
	 */
	private final static double DefaultX = 300.0;

	/**
	 * Default Y location
	 */
	private final static double DefaultY = 500.0;

	/**
	 * Default width
	 */
	private final static double DefaultWidth = 30.0;

	/**
	 * Default height
	 */
	private final static double DefaultHeight = 20.0;

	/**
	 * Index of provided type in {@link #figureTypes}
	 * @param type the type of figure we're looking for
	 * @return the index of provided type in {@link #figureTypes}
	 */
	private static int indexOf(Class<? extends Figure> type)
	{
		int i = 0;
		boolean found = false;
		for (; i < figureTypes.length; i++)
		{
			if (type == figureTypes[i])
			{
				found = true;
				break;
			}
		}
		if (found)
		{
			return i;
		}
		else
		{
			return -1;
		}
	}

	/**
	 * Finds the next type in {@link #figureTypes}
	 * @param type the current type
	 * @return the next type
	 */
	private static Class<? extends Figure> nextType(Class<? extends Figure> type)
	{
		int i = 0;
		for (; i < figureTypes.length; i++)
		{
			if (type == figureTypes[i])
			{
				break;
			}
		}
		return figureTypes[(i+1)%figureTypes.length];
	}


	/**
	 * Creates a nw figure of the required type at specified position with evt
	 * fill and edge colors, line type and width with evt logger
	 * @param type the type of figure to create
	 * @param x the x location where to create the figure
	 * @param y the y location where to create the figure
	 * @param fillColor the fill color to use
	 * @param edgeColor the edge color to use
	 * @param lineType the line type to use
	 * @param lineWidth the line width to use
	 * @param parentLogger the parent logger (if any, or null)
	 * @return a newly created figure
	 * @throws AssertionError if type does not match an existing figure type
	 * @throws IllegalArgumentException if required figure type is not part of {@link #figureTypes}
	 */
	private static Figure constructFigure(Class<? extends Figure> type,
	                                      double x,
	                                      double y,
	                                      Color fillColor,
	                                      Color edgeColor,
	                                      LineType lineType,
	                                      Double lineWidth,
	                                      Logger parentLogger) throws AssertionError
	{
		int index = indexOf(type);
		if (index == -1)
		{
			throw new IllegalArgumentException("unexpected figure class");
		}
		FigureType figureType = class2FigureType(type);
		return figureType.getFigure(fillColor,
		                            edgeColor,
		                            lineType,
		                            lineWidth,
		                            parentLogger,
		                            x,
		                            y);
	}

	/**
	 * Setup before all tests
	 * @throws java.lang.Exception if setup fails
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception
	{
		globalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		System.out.println("-------------------------------------------------");
		System.out.println("Start of Figures Tests");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Tear down after al tests
	 * @throws java.lang.Exception if teardown fails
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception
	{
		System.out.println("-------------------------------------------------");
		System.out.println("End of Figures Tests");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Setup before each test
	 * @param testInfo Informations on the following test
	 * @throws java.lang.Exception if setup fails
	 */
	@BeforeEach
	void setUp(TestInfo testInfo) throws Exception
	{
		testFigure = null;
		testFigureClass = null;
		testFigureClassName = null;
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
			System.exit(ex.hashCode());
		}
	}

	/**
	 * Builds test instances attributes based on provided class
	 * @param type the class of the figure to build
	 */
	void setupTest(Class<? extends Figure> type)
	{
		testFigure = constructFigure(type,
		                             DefaultX,
		                             DefaultY,
		                             DefaultFillColor,
		                             DefaultEdgeColor,
		                             DefaultLineType,
		                             DefaultLineWidth,
		                             parentLogger);
		if (testFigure == null)
		{
			fail(type.getSimpleName() + " figure can't be build");
		}
		testFigureClass = testFigure.getClass();
		testFigureClassName = testFigureClass.getSimpleName();
	}

	/**
	 * Teardown after each test
	 * @throws java.lang.Exception if teardown fails
	 */
	@AfterEach
	void tearDown() throws Exception
	{
		testFigure = null;
		testFigureClass = null;
		testFigureClassName = null;
		parentLogger = null;
	}

	/**
	 * Converts Figure class to {@link FigureType}
	 * @param figureClass the class of the figure to convert
	 * @return the corresponding {@link FigureType}
	 * @throws IllegalArgumentException if the provided figure class does not match any {@link FigureType}
	 */
	private final static FigureType class2FigureType(Class<? extends Figure> figureClass)
	{
		// TODO 204 FigureTest#class2FigureType uncomment when Circle is ready
//		if (figureClass == Circle.class)
//		{
//			return FigureType.CIRCLE;
//		}
		if (figureClass == Ellipse.class)
		{
			return FigureType.ELLIPSE;
		}
		// TODO 304 FigureTest#class2Figure uncomment when Rectangle is ready
//		if (figureClass == Rectangle.class)
//		{
//			return FigureType.RECTANGLE;
//		}

		throw new IllegalArgumentException("unexpected Figure class " + figureClass.getSimpleName());
	}

	/**
	 * Test method for {@link figures.Figure} valued constructor.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("Valued Constructor(Color, Color, LineType, double, Logger, double, double)")
	@Order(2)
	public final void testFigureColorColorLineTypeDoubleLogger(Class<? extends Figure> type)
	{
		String testName = new String(type.getSimpleName() + '.' + type.getSimpleName()
		    + "(Color, Color, LineType, double, Logger, double, double)");
		System.out.println(testName);

		Constructor<? extends Figure> valuedConstructor = null;
		Class<?>[] constructorArgs = new Class<?>[]{
			Color.class,
			Color.class,
			LineType.class,
			double.class,
			Logger.class,
			double.class,
			double.class
		};

		try
		{
			valuedConstructor = type.getConstructor(constructorArgs);
		}
		catch (NoSuchMethodException e)
		{
			fail(testName + " constructor not found");
		}
		catch (SecurityException e)
		{
			fail(testName + " constructor security exception");
		}

		if (valuedConstructor != null)
		{
			for (int i = 0; i < 4; i++)
			{
				Object[] args = null;
				switch (i)
				{
					case 0:	// Nominal case : all default values provided
						args = new Object[] {
							DefaultFillColor,
							DefaultEdgeColor,
							DefaultLineType,
							DefaultLineWidth,
							parentLogger,
							DefaultX,
							DefaultY
						};
						break;
					case 1:	// Null fill color
						args = new Object[] {
							null,
							DefaultEdgeColor,
							DefaultLineType,
							DefaultLineWidth,
							parentLogger,
							DefaultX,
							DefaultY
						};
						break;
					case 2:	// Null edge color
						args = new Object[] {
							DefaultFillColor,
							null,
							DefaultLineType,
							DefaultLineWidth,
							parentLogger,
							DefaultX,
							DefaultY
						};
						break;
					case 3:	// Null fill and edge color (should trigger IllegalStateException)
						args = new Object[] {
							null,
							null,
							DefaultLineType,
							DefaultLineWidth,
							parentLogger,
							DefaultX,
							DefaultY
						};
						break;
					default:
						break;
				}
				Object instance = null;
				try
				{
					instance = valuedConstructor.newInstance(args);
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
					fail(testName + " illegal argument");
				}
				catch (IllegalStateException e)
				{
					if (i != 3)
					{
						fail(testName + " unexpected illegal state exception");
					}
					else
					{
						// This was expected, so continue
						continue;
					}
				}
				/*
				 * InvocationTargetException superseeds IllegalStateException
				 */
				catch (InvocationTargetException e)
				{
					if (i != 3)
					{
						fail(testName + " invoked constructor throwed an exception");
					}
					else
					{
						Throwable cause = e.getCause();
						assertSame(IllegalStateException.class,
						           cause.getClass(),
						           testName + " unexpected exception cause");
						// This was expected, so continue
						continue;
					}
				}

				assertNotNull(instance,
				              testName + " null instance");
				assertEquals(instance, instance, testName + " self equality failed");
				assertEquals(type,
				             instance.getClass(),
				             testName + " unexpected instance class");
				testFigure = (Figure) instance;
			}
		}
		else
		{
			fail(testName + " null constructor");
		}
	}

	/**
	 * Test method for {@link figures.Figure#Figure} copy constructor.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("Copy Constructor(Figure)")
	@Order(3)
	public final void testFigureFigure(Class<? extends Figure> type)
	{
		String testName = new String(type.getSimpleName() + '.'
		    + type.getSimpleName() + "(Figure)");
		System.out.println(testName);

		setupTest(type);

		Constructor<? extends Figure> copyConstructor = null;
		Class<?>[] constructorArgs = new Class<?>[] { Figure.class };

		try
		{
			copyConstructor = type.getConstructor(constructorArgs);
		}
		catch (NoSuchMethodException e)
		{
			fail(testName + " constructor not found");
		}
		catch (SecurityException e)
		{
			fail(testName + " constructor security exception");
		}

		if (copyConstructor != null)
		{
			Object instance = null;
			Object[] args = new Object[] { testFigure };
			try
			{
				instance = copyConstructor.newInstance(args);
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
				fail(testName + " illegal argument");
			}
			catch (InvocationTargetException e)
			{
				fail(testName + " invoked constructor throwed an exception");
			}

			assertNotNull(instance, testName + " null instance");
			assertEquals(instance,
			             instance,
			             testName + " self equality failed");
			assertEquals(type,
			             instance.getClass(),
			             testName + " unexpected instance class");
			assertNotSame(testFigure,
			              instance,
			              testName + " new instance is same as copied figure");
			assertEquals(testFigure,
			             instance,
			             testName + " equality with copied instance failed");

			Class<? extends Figure> nextType = nextType(type);
			testFigure = constructFigure(nextType,
			                             DefaultX,
			                             DefaultY,
			                             DefaultFillColor,
			                             DefaultEdgeColor,
			                             DefaultLineType,
			                             DefaultLineWidth,
			                             parentLogger);
			assertNotNull(testFigure, testName + " constructed figure is null");

			LogHandler handler = new LogHandler(2);
			parentLogger.addHandler(handler);
			args[0] = testFigure;
			try
			{
				instance = copyConstructor.newInstance(args);
				if ((nextType != type) && (nextType.getSuperclass() != type))
				{
					fail(testName + " didn't throw exception when copying from "
					    + nextType.getSimpleName());
				}
			}
			catch (IllegalArgumentException e)
			{
				// Nothing: expected
			}
			catch (InvocationTargetException e)
			{
				// Nothing: InvocationTargetException superseeds IllegalArgumentException
				assertFalse(handler.isEmpty(),
				            testName + " unexpected empty logger handler after failed copy");
				LogRecord record = handler.pop();
				assertEquals(Level.SEVERE,
				             record.getLevel(),
				             testName + " unexpected log message level after failed copy");
				assertTrue(record.getMessage().startsWith("provided figure is not a"),
				           testName + " unexpected log message start");
			}
			catch (InstantiationException e)
			{
				fail(testName + " instanciation exception : Abstract class");
			}
			catch (IllegalAccessException e)
			{
				fail(testName + " constructor is inaccessible");
			}

			args = new Object[] { null };
			final String commonPart = " after copy constructor with null arg";
			try
			{
				instance = copyConstructor.newInstance(args);
				fail(testName + " didn't throw" + commonPart);
			}
			catch (NullPointerException e)
			{
				// Supserseeded by InvocationTargetException
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
				fail(testName + " illegal argument");
			}
			catch (InvocationTargetException e)
			{
				Throwable cause = e.getCause();
				assertSame(NullPointerException.class,
				           cause.getClass(),
				           testName + " unexpected exception cause" + commonPart);
				/*
				 * The current form of the copy constructors does not allow
				 * log message issuing when provided figure to copy is null.
				 * Save this for later ...
				 */
//				assertFalse(handler.isEmpty(),
//				            testName + " unexpected empty log handler" + commonPart);
//				LogRecord record = handler.pop();
//				assertEquals(Level.SEVERE,
//				             record.getLevel(),
//				             testName + " unexpected log message level" + commonPart);
			}
			parentLogger.removeHandler(handler);
		}
		else
		{
			fail(testName + " null constructor");
		}
	}

	/**
	 * Test method for {@link figures.Figure#getShape()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("getShape()")
	@Order(4)
	public final void testGetShape(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + '.' + "getShape()");
		System.out.println(testName);

		Shape shape = testFigure.getShape();

		/*
		 * Assert internal shape is not null
		 */
		assertNotNull(shape, testName + " null shape");

		/*
		 * Assert the type of the internal shape
		 */
		int classIndex = indexOf(type);
		Class<? extends Shape> shapeType = shape.getClass();
		assertEquals(shapeTypes[classIndex],
		             shapeType,
		             "unexpected internal shape type " + shapeType.getSimpleName());

		/*
		 * Assert shape attributes
		 * 	- fill
		 * 	- edge
		 * 	- edge type
		 * 	- edge width
		 */
		assertEquals(DefaultFillColor,
		             shape.getFill(),
		             testName + " unexpected fill");
		assertEquals(DefaultEdgeColor,
		             shape.getStroke(),
		             testName + " unexpected stroke");
		assertEquals(DefaultLineWidth,
		             shape.getStrokeWidth(),
		             tolerance,
		             testName + " unexpected line width");
		switch (testFigure.getLineType())
		{
			case NONE:
			case SOLID:
				assertTrue(shape.getStrokeDashArray().isEmpty(),
				           testName + " Dash array not empty on SOLID line");
				break;
			case DASHED:
				assertFalse(shape.getStrokeDashArray().isEmpty(),
				            testName + " Dash array empty on DASHED line");
				break;
			default:
				fail("unexpected line type");
				break;
		}
		assertEquals(StrokeLineJoin.ROUND,
		             shape.getStrokeLineJoin(),
		             testName + " unexpected StrokeLineJoin");
		assertEquals(StrokeLineCap.ROUND,
		             shape.getStrokeLineCap(),
		             testName + " unexpected StrokeLineCap");
	}

	/**
	 * Test method for {@link figures.Figure#getSelectionRectangle()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("getSelectionRectangle()")
	@Order(5)
	public final void testGetSelectionRectangle(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + '.' + "getSelectionRectangle()");
		System.out.println(testName);

		/*
		 * Null selectionRectangle on non selected figure
		 */
		assertNull(testFigure.getSelectionRectangle(),
		           testName + " selection rectangle is not null on unselected figure");

		/*
		 * Non null selectionRectangle on selected figure
		 */
		testFigure.setSelected(true);
		javafx.scene.shape.Rectangle selectionRectangle = testFigure.getSelectionRectangle();
		assertNotNull(selectionRectangle,
		              testName + " selection rectangle is null on selected figure");

		/*
		 * Assert selection rectangle dimensions
		 */
		assertEquals(testFigure.width(),
		             selectionRectangle.getWidth(),
		             testName + " unexpected selection rectangle width");
		assertEquals(testFigure.height(),
		             selectionRectangle.getHeight(),
		             testName + " unexpected selection rectangle height");

		/*
		 * Assert selection rectangle stroke & fill
		 */
		assertEquals(Color.TRANSPARENT,
		             selectionRectangle.getFill(),
		             testName + " unexpected fill for selection rectangle");
		assertTrue(ColorFactory.hasColor(Color.TRANSPARENT),
		           testName + " color factory doesn't have this color yet ==> "
		           	+ "use ColorFactory.getColor(color) to set colors anywhere");
		assertFalse(selectionRectangle.getStrokeDashArray().isEmpty(),
		            testName + " unexpected stroke dash array for selection rectangle");
	}

	/**
	 * Test method for {@link figures.Figure#getRoot()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("getRoot()")
	@Order(6)
	public final void testGetRoot(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + '.' + "getRoot()");
		System.out.println(testName);

		Group rootGroup = testFigure.getRoot();

		/*
		 * Assert Root group is not null
		 */
		assertNotNull(rootGroup, testName + " root group is null");
		/*
		 * Assert Root group has only one children which is a shape
		 */
		ObservableList<Node> children = rootGroup.getChildren();
		assertEquals(1, children.size(), testName + " size is note one");
		Node firstChildren = children.get(0);
		assertTrue(firstChildren instanceof Shape,
		           testName + " first children class is not a Shape");
	}

	/**
	 * Test method for {@link figures.Figure#hasFillColor()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("hasFillColor()")
	@Order(7)
	public final void testHasFillColor(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + '.' + "hasFillColor()");
		System.out.println(testName);

		/*
		 * Default created instance has DefaultFillColor
		 */
		assertTrue(testFigure.hasFillColor(),
		           testName + " default created figure doesn't have fill color");

		/*
		 * Newly created figure has no fill color
		 */
		testFigure = constructFigure(type,
		                             DefaultX,
		                             DefaultY,
		                             null,
		                             DefaultEdgeColor,
		                             DefaultLineType,
		                             DefaultLineWidth,
		                             parentLogger);
		assertNotNull(testFigure, testName + " constructed figure is null");
		assertFalse(testFigure.hasFillColor(),
		            testName + " newly created figure have fill color");
	}

	/**
	 * Test method for {@link figures.Figure#getFillColor()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("getFillColor()")
	@Order(8)
	public final void testGetFillColor(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + "." + "getFillColor()");
		System.out.println(testName);

		Color providedColor = testFigure.getFillColor();

		assertEquals(DefaultFillColor,
		             providedColor,
		             testName + " unexpected fill color");
		if (providedColor != null)
		{
			assertTrue(ColorFactory.hasColor(providedColor),
			           testName + " color factory doesn't have this color yet ==> "
			           	+ "use ColorFactory.getColor(color) to set colors anywhere");
		}

		testFigure = constructFigure(type,
		                             DefaultX,
		                             DefaultY,
		                             null,
		                             DefaultEdgeColor,
		                             DefaultLineType,
		                             DefaultLineWidth,
		                             parentLogger);
		assertNotNull(testFigure, testName + " constructed figure is null");
		assertNull(testFigure.getFillColor(),
		           testName + " unexpected non null fill color");
	}

	/**
	 * Test method for {@link figures.Figure#setFillColor(javafx.scene.paint.Color)}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("setFillColor(Color)")
	@Order(9)
	public final void testSetFillColor(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".setFillColor(Color)");
		System.out.println(testName);

		Color colorToSet = Color.AQUA;
		testFigure.setFillColor(colorToSet);

		Color providedColor = testFigure.getFillColor();

		assertEquals(colorToSet,
		             providedColor,
		             testName +  " unexpected fill color");
		if (providedColor != null)
		{
			assertTrue(ColorFactory.hasColor(providedColor),
			           testName + " color factory doesn't have this color yet ==> "
			           	+ "use ColorFactory.getColor(color) to set colors anywhere");
		}

		testFigure.setFillColor(null);
		assertNull(testFigure.getFillColor(),
		           testName + " unexpected non null fill color");
	}

	/**
	 * Test method for {@link figures.Figure#hasEdgeColor()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("hasEdgeColor()")
	@Order(10)
	public final void testHasEdgeColor(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".hasEdgeColor()");
		System.out.println(testName);

		/*
		 * Default created instance has DefaultEdgeColor
		 */
		assertTrue(testFigure.hasEdgeColor(),
		           testName + " default created figure doesn't have edge color");

		/*
		 * Newly created figure has no fill color
		 */
		testFigure = constructFigure(type,
		                             DefaultX,
		                             DefaultY,
		                             DefaultFillColor,
		                             null,
		                             DefaultLineType,
		                             DefaultLineWidth,
		                             parentLogger);
		assertNotNull(testFigure, testName + " constructed figure is null");
		assertFalse(testFigure.hasEdgeColor(),
		            testName + " newly created figure has edge color");
	}

	/**
	 * Test method for {@link figures.Figure#getEdgeColor()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("getEdgeColor()")
	@Order(11)
	public final void testGetEdgeColor(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".getEdgeColor()");
		System.out.println(testName);
		Color providedColor = testFigure.getEdgeColor();

		assertEquals(DefaultEdgeColor,
		             providedColor,
		             testName + " unexpected edge color");
		if (providedColor != null)
		{
			assertTrue(ColorFactory.hasColor(providedColor),
			           testName + " color factory doesn't have this color yet ==> "
			           	+ "use ColorFactory.getColor(color) to set colors anywhere");
		}

		testFigure = constructFigure(type,
		                             DefaultX,
		                             DefaultY,
		                             DefaultFillColor,
		                             null,
		                             DefaultLineType,
		                             DefaultLineWidth,
		                             parentLogger);
		assertNotNull(testFigure, testName + " constructed figure is null");
		assertNull(testFigure.getEdgeColor(),
		           testName + " unexpected non null edge color");
	}

	/**
	 * Test method for {@link figures.Figure#setEdgeColor(javafx.scene.paint.Color)}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("setEdgeColor(Color)")
	@Order(12)
	public final void testSetEdgeColor(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".setEdgeColor(Color)");
		System.out.println(testName);

		Color colorToSet = Color.AQUA;
		testFigure.setEdgeColor(colorToSet);

		Color providedColor = testFigure.getEdgeColor();
		assertEquals(colorToSet,
		             providedColor,
		             testName +  " unexpected edge color");
		if (providedColor != null)
		{
			assertTrue(ColorFactory.hasColor(providedColor),
			           testName + " color factory doesn't have this color yet ==> "
			           	+ "use ColorFactory.getColor(color) to set colors anywhere");
		}

		testFigure.setEdgeColor(null);
		assertNull(testFigure.getEdgeColor(),
		           testName + " unexpected non null edge color");
	}

	/**
	 * Test method for {@link figures.Figure#getLineType()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("getLineType()")
	@Order(13)
	public final void testGetLineType(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".getLineType()");
		System.out.println(testName);

		assertEquals(DefaultLineType,
		             testFigure.getLineType(),
		             testName + " unexpected line type");

		for (LineType lineType : LineType.all())
		{
			testFigure = constructFigure(type,
			                             DefaultX,
			                             DefaultY,
			                             DefaultFillColor,
			                             DefaultEdgeColor,
			                             lineType,
			                             DefaultLineWidth,
			                             parentLogger);
			assertNotNull(testFigure, testName + " constructed figure is null");
			assertEquals(lineType,
			             testFigure.getLineType(),
			             testName + " unexpected line type");
		}
	}

	/**
	 * Test method for {@link figures.Figure#setLineType(figures.enums.LineType)}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("setLineType(LineType)")
	@Order(14)
	public final void testSetLineType(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".setLineType(LineType)");
		System.out.println(testName);

		for (LineType lineType : LineType.all())
		{
			testFigure.setLineType(lineType);
			assertEquals(lineType,
			             testFigure.getLineType(),
			             testName + " unexpected line type");
			/*
			 * When line type is NONE, internal shape's stroke color is
			 * supposed to be Color.TRANSPARENT
			 */
			if (lineType == LineType.NONE)
			{
				Shape shape = testFigure.getShape();
				assertEquals(Color.TRANSPARENT,
				             shape.getStroke(),
				             testName + " unexpected internal shape stroke with line type = NONE");
			}
		}
	}

	/**
	 * Test method for {@link figures.Figure#getLineWidth()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("getLineWidth()")
	@Order(15)
	public final void testGetLineWidth(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".getLineWidth()");
		System.out.println(testName);

		assertEquals(DefaultLineWidth,
		             testFigure.getLineWidth(),
		             testName + " unexpected line width");

		for (double width = 0.0; width < 32.0; width++)
		{
			testFigure = constructFigure(type,
			                             DefaultX,
			                             DefaultY,
			                             DefaultFillColor,
			                             DefaultEdgeColor,
			                             DefaultLineType,
			                             width,
			                             parentLogger);
			assertNotNull(testFigure, testName + " constructed figure is null");
			assertEquals(width,
			             testFigure.getLineWidth(),
			             tolerance,
			             testName + " unexpected line width");
		}
	}

	/**
	 * Test method for {@link figures.Figure#setLineWidth(double)}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("setLineWidth(double)")
	@Order(16)
	public final void testSetLineWidth(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".setLineWidth(double)");
		System.out.println(testName);

		for (double width = 0.0; width < 32.0; width++)
		{
			testFigure.setLineWidth(width);
			assertEquals(width,
			             testFigure.getLineWidth(),
			             tolerance,
			             testName + " unexpected line width");
		}
	}

	/**
	 * Test method for {@link figures.Figure#getInstanceNumber()}.
	 * @param type the class of {@link Figure} to create
	 * @implNote Since this class might not be the first class to run among
	 * other test classes we can NOT assume the initial instance number will
	 * be 0. We can only assume the next instance of this type of figure
	 * will have the next instance number
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("getInstanceNumber()")
	@Order(1)
	public final void testGetInstanceNumber(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".getInstanceNumber()");
		System.out.println(testName);

		int initialInstanceNumber = testFigure.getInstanceNumber();

		/*
		 * Assert copied instance has also number 0
		 */
		Constructor<? extends Figure> copyConstructor = null;
		Class<?>[] constructorArgs = new Class<?>[] { Figure.class };
		try
		{
			copyConstructor = type.getConstructor(constructorArgs);
		}
		catch (NoSuchMethodException e)
		{
			fail(testName + " constructor not found");
		}
		catch (SecurityException e)
		{
			fail(testName + " constructor security exception");
		}

		if (copyConstructor != null)
		{
			Object instance = null;
			Object[] args = new Object[] { testFigure };
			try
			{
				instance = copyConstructor.newInstance(args);
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
				fail(testName + " illegal argument");
			}
			catch (InvocationTargetException e)
			{
				fail(testName + " invoked constructor throwed an exception");
			}

			assertNotNull(instance, testName + " null instance");
			Figure figure = (Figure) instance;
			assertEquals(initialInstanceNumber,
			             figure.getInstanceNumber(),
			             testName + " unexpected instance number on copied figure");
		}
		else
		{
			fail(testName + " null constructor");
		}

		/*
		 * Assert next instance will have the following instance number
		 */
		testFigure = constructFigure(type,
		                             DefaultX,
		                             DefaultY,
		                             DefaultFillColor,
		                             DefaultEdgeColor,
		                             DefaultLineType,
		                             DefaultLineWidth,
		                             parentLogger);
		assertEquals(++initialInstanceNumber,
		             testFigure.getInstanceNumber(),
		             testName + " unexpected instance number");

		if (figureTypes.length > 1)
		{
			/*
			 * Assert next instance (of this specific type) will have the following
			 * instance number after instanciating all other possibles figures.
			 * Because some figures might inherit each-other instance number
			 * shall be consistent on a per class base
			 */
			Figure[] otherFigures = new Figure[figureTypes.length];
			Figure otherFigure = null;
			int i = 0;
			for (Class<? extends Figure> otherType : figureTypes)
			{
				Figure newFigure = constructFigure(otherType,
				                                    DefaultX,
				                                    DefaultY,
				                                    DefaultFillColor,
				                                    DefaultEdgeColor,
				                                    DefaultLineType,
				                                    DefaultLineWidth,
				                                    parentLogger);
				if (otherType == type)
				{
					otherFigure = newFigure;
				}
				otherFigures[i++] = newFigure;
			}

			assertNotNull(otherFigure,
			              testName + " unexpected null figure of the same type");
			assertEquals(++initialInstanceNumber,
			             otherFigure.getInstanceNumber(),
			             testName + " unexpected instance number");
		}
	}

	/**
	 * Test method for {@link figures.Figure#isSelected()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("isSelected()")
	@Order(17)
	public final void testIsSelected(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".isSelected()");
		System.out.println(testName);

		/*
		 * Default created figure is NOT selected
		 */
		assertFalse(testFigure.isSelected(),
		            testName + " unexpected selected state");
	}

	/**
	 * Test method for {@link figures.Figure#setSelected(boolean)}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("setSelected(boolean)")
	@Order(18)
	public final void testSetSelected(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".setSelected(boolean)");
		System.out.println(testName);
		/*
		 * Default created figure is NOT selected
		 */
		assertFalse(testFigure.isSelected(),
		            testName + " unexpected selected state");

		/*
		 * Assert there is no selection rectangle
		 */
		ObservableList<Node> rootChildren = testFigure.getRoot().getChildren();
		assertEquals(1,
		             rootChildren.size(),
		             testName + " unexpected unselected figure's root Group size");
		assertSame(testFigure.getShape(),
		           rootChildren.get(0),
		           testName + " unexpected first child in unselected figure's group");
		assertNull(testFigure.getSelectionRectangle(),
		           testName + " unexpected non null selection rectangle in unselected figure");

		/*
		 * Assert figure is selected
		 */
		testFigure.setSelected(true);
		assertTrue(testFigure.isSelected(),
		           testName + " unexpected selected state");

		/*
		 * Assert there is a selection rectangle
		 */
		rootChildren = testFigure.getRoot().getChildren();
		assertEquals(2,
		             rootChildren.size(),
		             testName + " unexpected selected figure's root Group size");
		assertSame(testFigure.getShape(),
		           rootChildren.get(0),
		           testName + " unexpected first child in unselected figure's group");
		assertSame(testFigure.getSelectionRectangle(),
		           rootChildren.get(1),
		           testName + " unexpected selection rectangle in selected figure");

		/*
		 * Assert multiple selections doesn't create multiple selection
		 * rectangles
		 */
		testFigure.setSelected(false);
		assertFalse(testFigure.isSelected(),
		            testName + " unexpected selected state");
		assertEquals(1,
		             rootChildren.size(),
		             testName + " unexpected unselected figure's root Group size");
		assertSame(testFigure.getShape(),
		           rootChildren.get(0),
		           testName + " unexpected first child in unselected figure's group");
		assertNull(testFigure.getSelectionRectangle(),
		           testName + " unexpected non null selection rectangle in unselected figure");
	}

	/**
	 * Test method for {@link figures.Figure#getCenter()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("getCenter()")
	@Order(20)
	public final void testGetCenter(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".getCenter()");
		System.out.println(testName);

		Point2D[] testPoints = new Point2D[] {
			new Point2D(DefaultX, DefaultY),
			new Point2D(DefaultX + DefaultWidth, DefaultY + DefaultHeight),
			new Point2D(DefaultX, DefaultY + DefaultHeight + DefaultHeight)
		};

		/*
		 * Assert default constructed center
		 */
		Point2D center = testFigure.getCenter();
		assertEquals(testPoints[0].getX(),
		             center.getX(),
		             tolerance,
		             testName + " unexpected center X");
		assertEquals(testPoints[0].getY(),
		             center.getY(),
		             tolerance,
		             testName + " unexpected center Y");

		/*
		 * Move center by moving last point
		 */
		Point2D midPoint = center.midpoint(testPoints[1]);
		if (testFigure instanceof Mouseable)
		{
			((Mouseable) testFigure).setLastPoint(testPoints[1]);
		}
		center = testFigure.getCenter();
		FigureType figureType = class2FigureType(type);
		switch (figureType)
		{
			case CIRCLE:
			case ELLIPSE:
				assertEquals(testPoints[0].getX(),
				             center.getX(),
				             tolerance,
				             testName + " unexpected center X");
				assertEquals(testPoints[0].getY(),
				             center.getY(),
				             tolerance,
				             testName + " unexpected center Y");
				break;
			case RECTANGLE:
				assertEquals(midPoint.getX(),
				             center.getX(),
				             tolerance,
				             testName + " unexpected center X");
				assertEquals(midPoint.getY(),
				             center.getY(),
				             tolerance,
				             testName + " unexpected center Y");
				break;
			default:
				fail(testName + " not yet implemented");
				break;
		}
	}

	/**
	 * Test method for {@link figures.Figure#width()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("width()")
	@Order(21)
	public final void testWidth(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".width()");
		System.out.println(testName);

		assertEquals(DefaultLineWidth,
		             testFigure.width(),
		             DefaultLineWidth,
		             testName + " unexpected width");
		if (testFigure instanceof Mouseable)
		{
			((Mouseable) testFigure)
			    .setLastPoint(new Point2D(DefaultX + DefaultWidth, DefaultY));
		}

		FigureType figureType = class2FigureType(type);
		switch (figureType)
		{
			case CIRCLE:
			case ELLIPSE:
				assertEquals(DefaultWidth * 2.0,
				             testFigure.width(),
				             DefaultLineWidth,
				             testName + " unexpected width");
				break;
			case RECTANGLE:
				assertEquals(DefaultWidth,
				             testFigure.width(),
				             DefaultLineWidth + 1.0,
				             testName + " unexpected width");
				break;
			default:
				fail(testName + " not yet implemented");
				break;
		}
	}

	/**
	 * Test method for {@link figures.Figure#height()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("height()")
	@Order(22)
	public final void testHeight(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".height()");
		System.out.println(testName);

		assertEquals(DefaultLineWidth,
		             testFigure.height(),
		             DefaultLineWidth,
		             testName + " unexpected height");

		if (testFigure instanceof Mouseable)
		{
			((Mouseable) testFigure).setLastPoint(new Point2D(DefaultX,
		                                    DefaultY + DefaultHeight));
		}

		FigureType figureType = class2FigureType(type);
		switch (figureType)
		{
			case CIRCLE:
			case ELLIPSE:
				assertEquals(DefaultHeight * 2.0,
				             testFigure.height(),
				             DefaultLineWidth,
				             testName + " unexpected height");
				break;
			case RECTANGLE:
				assertEquals(DefaultHeight,
				             testFigure.height(),
				             DefaultLineWidth + 1.0,
				             testName + " unexpected height");
				break;
			default:
				fail(testName + " not yet implemented");
				break;
		}
	}

	/**
	 * Test method for {@link figures.Figure#topLeft()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("topLeft()")
	@Order(23)
	public final void testTopLeft(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".topLeft()");
		System.out.println(testName);
		FigureType figureType = class2FigureType(type);

		/*
		 * Default constructed top left is (DefaultX, DefaultY)
		 */
		Point2D initialTopLeft = testFigure.topLeft();
		assertEquals(DefaultX,
		             initialTopLeft.getX(),
		             DefaultLineWidth,
		             testName + " unexpected top left X");
		assertEquals(DefaultY,
		             initialTopLeft.getY(),
		             DefaultLineWidth,
		             testName + " unexpected top left Y");
		/*
		 * Move last point to up and left changes top left
		 * except for Rectangle & RoundedRectangle
		 */
		Point2D lastPoint = new Point2D(DefaultX - (2.0 * DefaultWidth),
		                                DefaultY - (2.0 * DefaultHeight));
		if (testFigure instanceof Mouseable)
		{
			((Mouseable) testFigure).setLastPoint(lastPoint);
		}
		Point2D newTopLeft = testFigure.topLeft();

		if ((figureType == FigureType.RECTANGLE))
		{
			assertEquals(initialTopLeft.getX(),
			             newTopLeft.getX(),
			             DefaultLineWidth,
			             testName + " unexpected top left X");
			assertEquals(initialTopLeft.getY(),
			             newTopLeft.getY(),
			             DefaultLineWidth,
			             testName + " unexpected top left Y");
		}
		else
		{
			assertNotEquals(initialTopLeft.getX(),
			                newTopLeft.getX(),
			                testName + " unexpected top left X");
			assertNotEquals(initialTopLeft.getY(),
			                newTopLeft.getY(),
			                testName + " unexpected top left Y");
		}

		/*
		 * Move last point to right and down changes top left with
		 * Circle, NGon & Star
		 */
		lastPoint = new Point2D(DefaultX + (2.0 * DefaultWidth),
		                        DefaultY + (2.0 * DefaultHeight));
		if (testFigure instanceof Mouseable)
		{
			((Mouseable) testFigure).setLastPoint(lastPoint);
		}
		newTopLeft = testFigure.topLeft();

		if ((figureType == FigureType.CIRCLE) ||
			(figureType == FigureType.ELLIPSE))
		{
			assertNotEquals(initialTopLeft.getX(),
			                newTopLeft.getX(),
			                testName + " unexpected top left X");
			assertNotEquals(initialTopLeft.getY(),
			                newTopLeft.getY(),
			                testName + " unexpected top left Y");
		}
		else
		{
			assertEquals(initialTopLeft.getX(),
			             newTopLeft.getX(),
			             DefaultLineWidth,
			             testName + " unexpected top left X");
			assertEquals(initialTopLeft.getY(),
			             newTopLeft.getY(),
			             DefaultLineWidth,
			             testName + " unexpected top left Y");
		}
	}

	/**
	 * Test method for {@link figures.Figure#bottomRight()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("bottomRight()")
	@Order(24)
	public final void testBottomRight(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".bottomRight()");
		System.out.println(testName);

		Point2D initialBottomRight = testFigure.bottomRight();
		assertEquals(DefaultX,
		             initialBottomRight.getX(),
		             DefaultLineWidth,
		             testName + " unexpected bottom right X");
		assertEquals(DefaultY,
		             initialBottomRight.getY(),
		             DefaultLineWidth,
		             testName + " unexpected bottom right Y");

		/*
		 * Move last point right & down always changes bottom right
		 */
		Point2D lastPoint = new Point2D(DefaultX + (DefaultWidth * 2.0),
		                                DefaultY + (DefaultHeight * 2.0));
		if (testFigure instanceof Mouseable)
		{
			((Mouseable) testFigure).setLastPoint(lastPoint);
		}
		Point2D newBottomRight = testFigure.bottomRight();
		assertNotEquals(initialBottomRight.getX(),
		                newBottomRight.getX(),
		                testName + " unchanged bottom right X");
		assertNotEquals(initialBottomRight.getY(),
		                newBottomRight.getY(),
		                testName + " unchanged bottom right Y");
	}

	/**
	 * Test method for {@link figures.Mouseable#createShape(double, double)}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("createShape(double, double)")
	@Order(25)
	public final void testCreateShape(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".createShape(double, double)");
		System.out.println(testName);

		/*
		 * Default created shape is created at (DefaultX, DefaultY)
		 */
		Point2D center = testFigure.getCenter();
		assertEquals(DefaultX,
		             center.getX(),
		             DefaultLineWidth,
		             testName + " unexpected figure center X");
		assertEquals(DefaultY,
		             center.getY(),
		             DefaultLineWidth,
		             testName + " unexpected figure center Y");

		/*
		 * Default created shape is created with width & height 0.0
		 */
		assertEquals(0.0,
		             testFigure.width(),
		             DefaultLineWidth + 2.0,
		             testName + " unexpected figure width");
		assertEquals(0.0,
		             testFigure.height(),
		             DefaultLineWidth + 2.0,
		             testName + " unexpected figure height");
	}

	/**
	 * Test method for {@link figures.Mouseable#setLastPoint(javafx.geometry.Point2D)}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("setLastPoint(Point2D)")
	@Order(19)
	public final void testSetLastPoint(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".setLastPoint(Point2D)");
		System.out.println(testName);
		FigureType figureType = class2FigureType(type);

		Point2D point = new Point2D(DefaultX, DefaultY);
		double initialWidth = testFigure.width();
		double initialHeight = testFigure.height();
		Point2D initialCenter = testFigure.getCenter();
		Point2D initialTopLeft = testFigure.topLeft();
		Point2D initialBottomRight = testFigure.bottomRight();

		/*
		 * Move last point to right
		 * 	- Center shouldn't change for Circle, Ellipse, NGon, Star
		 * 	- Width should change
		 * 	- Height should change for Circle, NGon, Star
		 * 	- Top Left should change for Circle, Ellipse, NGon, Star
		 * 	- Bottom Right should change
		 */
		point = point.add(DefaultWidth * 2.0, 0.0);
		if (testFigure instanceof Mouseable)
		{
			((Mouseable) testFigure).setLastPoint(point);
		}
		double newWidth = testFigure.width();
		double newHeight = testFigure.height();
		Point2D newCenter = testFigure.getCenter();
		Point2D newTopLeft = testFigure.topLeft();
		Point2D newBottomRight = testFigure.bottomRight();

		if ((figureType == FigureType.CIRCLE) ||
			(figureType == FigureType.ELLIPSE))
		{
			// unchanged center
			assertEquals(initialCenter,
			             newCenter,
			             testName + " center changed");
			// changed top left
			assertTrue(initialTopLeft.distance(newTopLeft) > DefaultLineWidth,
			           testName + " unexpected top left");
		}
		else
		{
			// changed center
			assertNotEquals(initialCenter,
			                newCenter,
			                testName + " center didn't changed");
			// unchanged top left
			assertTrue(initialTopLeft.distance(newTopLeft) < DefaultLineWidth,
			           testName + " unexpected top left");

		}

		// changed width
		assertNotEquals(initialWidth,
		                newWidth,
		                testName + " width didn't changed");

		if ((figureType == FigureType.CIRCLE))
		{
			// changed height
			assertNotEquals(initialHeight,
			                newHeight,
			                testName + " unexpected height");
		}
		else
		{
			// unchanged height
			assertEquals(initialHeight,
			             newHeight,
			             DefaultLineWidth,
			             testName + " unexpected height");
		}

		// changed bottom right
		assertNotEquals(initialBottomRight,
		                newBottomRight,
		                testName + " bottom right didn't changed");

		/*
		 * Set Last Point down
		 * 	- Center shouldn't change for Circle, Ellipse, NGon, Star
		 * 	- Height should change
		 * 	- Width should change for Circle, NGon, Star
		 * 	- Top Left should change for Circle, Ellipse, NGon, Star
		 * 	- Bottom Right should change
		 */
		point = point.add(-DefaultWidth * 2.0, DefaultHeight * 2.0);
		if (testFigure instanceof Mouseable)
		{
			((Mouseable) testFigure).setLastPoint(point);
		}
		newWidth = testFigure.width();
		newHeight = testFigure.height();
		newCenter = testFigure.getCenter();
		newTopLeft = testFigure.topLeft();
		newBottomRight = testFigure.bottomRight();
		if ((figureType == FigureType.CIRCLE) ||
			(figureType == FigureType.ELLIPSE))
		{
			// unchanged center
			assertEquals(initialCenter,
			             newCenter,
			             testName + " center changed");
			// changed top left
			assertTrue(initialTopLeft.distance(newTopLeft) > DefaultLineWidth,
			           testName + " unexpected top left");
		}
		else
		{
			// changed center
			assertNotEquals(initialCenter,
			                newCenter,
			                testName + " center didn't changed");
			// unchanged top left
			assertTrue(initialTopLeft.distance(newTopLeft) < DefaultLineWidth,
			           testName + " unexpected top left");
		}

		// changed height
		assertNotEquals(initialHeight,
		                newHeight,
		                testName + " height didn't changed");

		if ((figureType == FigureType.CIRCLE))
		{
			// changed width
			assertNotEquals(initialWidth,
			                newWidth,
			                testName + " unexpected width");
		}
		else
		{
			// unchanged width
			assertEquals(initialWidth,
			             newWidth,
			             DefaultLineWidth,
			             testName + " unexpected width");
		}

		// changed bottom right
		assertNotEquals(initialBottomRight,
		                newBottomRight,
		                testName + " bottom right didn't changed");
	}

	/**
	 * Test method for {@link figures.Figure}'s applyParameters().
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("applyParameters()")
	@Order(26)
	public final void testApplyParameters(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".applyParameters()");
		System.out.println(testName);

		Figure[] testFigures = new Figure[] {
			testFigure,							// 0 default values
			constructFigure(type,				// 1 null fill color
			                DefaultX,
			                DefaultY,
			                null,
			                DefaultEdgeColor,
			                DefaultLineType,
			                DefaultLineWidth,
			                parentLogger),
			constructFigure(type,				// 2 null edge color
			                DefaultX,
			                DefaultY,
			                DefaultFillColor,
			                null,
			                DefaultLineType,
			                DefaultLineWidth,
			                parentLogger),
			constructFigure(type,				// 3 null fill color, dashed line
			                DefaultX,
			                DefaultY,
			                null,
			                DefaultEdgeColor,
			                LineType.DASHED,
			                DefaultLineWidth,
			                parentLogger),
			constructFigure(type,				// 4 null fill color, no line
			                DefaultX,
			                DefaultY,
			                null,
			                DefaultEdgeColor,
			                LineType.NONE,
			                DefaultLineWidth,
			                parentLogger),
			constructFigure(type,				// 5 null edge color, no line
			                DefaultX,
			                DefaultY,
			                DefaultFillColor,
			                null,
			                LineType.NONE,
			                DefaultLineWidth,
			                parentLogger),
		};

		for (int i = 0; i < testFigures.length; i++)
		{
			Figure figure = testFigures[i];
			Shape shape = figure.getShape();

			assertNotNull(shape,
			              testName + " null figure");

			Color expectedFill = figure.getFillColor() != null ?
				DefaultFillColor : Color.TRANSPARENT;
			Color providedFill = (Color) shape.getFill();
			assertEquals(expectedFill,
			             providedFill,
			             testName + " unexpected fill at " + i);
			if (providedFill != null)
			{
				assertTrue(ColorFactory.hasColor(providedFill),
				           testName + " color factory doesn't have this color yet ==> "
				           	+ "use ColorFactory.getColor(color) to set colors anywhere");
			}
			/*
			 * If edge color is null then internal shape's color shall be transparent
			 * If line type is none, then internal shape's color shall be transparent
			 */
			Color expectedStroke = figure.getEdgeColor() != null ?
				(figure.getLineType() == LineType.NONE ?
					Color.TRANSPARENT :
					DefaultEdgeColor) :
				Color.TRANSPARENT;
			Color providedStroke = (Color) shape.getStroke();
			assertEquals(expectedStroke,
			             providedStroke,
			             testName + " unexpected stroke at " + i);
			if (providedStroke != null)
			{
				assertTrue(ColorFactory.hasColor(providedStroke),
				           testName + " color factory doesn't have this color yet ==> "
				           	+ "use ColorFactory.getColor(color) to set colors anywhere");
			}
			assertEquals(DefaultLineWidth,
			             shape.getStrokeWidth(),
			             testName + " unexpected stroke width at " + i);
			assertEquals(StrokeLineJoin.ROUND,
			             shape.getStrokeLineJoin(),
			             testName + " unexpected stroke line join at " + i);
			assertEquals(StrokeLineCap.ROUND,
			             shape.getStrokeLineCap(),
			             testName +  " unexpected stroke line cap at " + i);
			if (figure.getLineType() == LineType.DASHED)
			{
				assertFalse(shape.getStrokeDashArray().isEmpty(),
				            testName + " unexpected empty dash array at " + i);
			}
		}
	}

	/**
	 * Test method for {@link figures.Figure#updateSelectionFrame()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("updateSelectionFrame()")
	@Order(27)
	public final void testUpdateSelectionFrame(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".updateSelectionFrame()");
		System.out.println(testName);

		/*
		 * Default constructed figure is unselected so updateSelectionFrame
		 * should not do anything
		 */
		try
		{
			testFigure.updateSelectionFrame();
		}
		catch (NullPointerException e)
		{
			fail(testName + " unexpected NullPointerException on unselected figure");
		}

		testFigure.setSelected(true);
		try
		{
			testFigure.updateSelectionFrame();
			Point2D topLeftPoint = testFigure.topLeft();
			javafx.scene.shape.Rectangle selectionRectangle = testFigure.getSelectionRectangle();
			assertNotNull(selectionRectangle,
			              testName + " unexpected null selection rectangle");
			assertEquals(topLeftPoint.getX(),
			             selectionRectangle.getX(),
			             testName  +  " unexpected selection rectangle X");
			assertEquals(topLeftPoint.getY(),
			             selectionRectangle.getY(),
			             testName  +  " unexpected selection rectangle Y");
			assertEquals(testFigure.width(),
			             selectionRectangle.getWidth(),
			             DefaultLineWidth,
			             testName + " unexpected selection rectangle width");
			assertEquals(testFigure.height(),
			             selectionRectangle.getHeight(),
			             DefaultLineWidth,
			             testName + " unexpected selection rectangle height");
		}
		catch (NullPointerException e)
		{
			fail(testName + " unexpected NullPointerException on selected figure");
		}
	}

	/**
	 * Test method for {@link figures.Figure#setTranslate(double, double)}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("setTranslate(double, double)")
	@Order(28)
	public final void testSetTranslate(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".setTranslate(double, double)");
		System.out.println(testName);

		Figure translated = testFigure.setTranslate(DefaultWidth, DefaultHeight);

		assertSame(testFigure,
		           translated,
		           testName + " unexpected translated instance");
		Group root = testFigure.getRoot();
		assertNotNull(root,
		              testName + " unexpected null figure root");
		assertEquals(DefaultWidth,
		             root.getTranslateX(),
		             testName + " unexpected X translate");
		assertEquals(DefaultHeight,
		             root.getTranslateY(),
		             testName + " unexpected Y translate");
	}

	/**
	 * Test method for {@link figures.Figure#setRotate(double)}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("setRotate(double)")
	@Order(29)
	public final void testSetRotate(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".setRotate(double)");
		System.out.println(testName);

		final double angle = 47.5;
		Figure rotated = testFigure.setRotate(angle);

		assertSame(testFigure,
		           rotated,
		           testName + " unexpected rotated instance");
		Group root = testFigure.getRoot();
		assertNotNull(root,
		              testName + " unexpected null figure root");
		assertEquals(angle,
		             root.getRotate(),
		             testName + " unexpected rotation");
	}

	/**
	 * Test method for {@link figures.Figure#setScale(double, double)}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("setScale(double, double)")
	@Order(30)
	public final void testSetScale(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".setScale(double, double)");
		System.out.println(testName);

		final double scaleX = 2.89;
		final double scaleY = 0.89;

		Figure scaled = testFigure.setScale(scaleX, scaleY);

		assertSame(testFigure,
		           scaled,
		           testName + " unexpected scaled instance");
		Group root = testFigure.getRoot();
		assertNotNull(root,
		              testName + " unexpected null figure root");
		assertEquals(scaleX,
		             root.getScaleX(),
		             testName + " unexpected X scale");
		assertEquals(scaleY,
		             root.getScaleY(),
		             testName + " unexpected Y scale");
	}

	/**
	 * Test method for {@link figures.Figure#overlaps(Figure)}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("overlaps(Figure)")
	@Order(31)
	public final void testOverlaps(Class<? extends Figure> type)
	{
		setupTest(type);
		Mouseable mouseableTestFigure = (Mouseable) testFigure;
		mouseableTestFigure.setLastPoint(new Point2D(DefaultX + DefaultWidth,
		                                             DefaultY + DefaultHeight));

		String testName = new String(testFigureClassName + ".overlaps(Figure)");
		System.out.println(testName);

		double tinyAmount = DefaultWidth / 100.0;
		Figure otherFigure = testFigure.clone();
		otherFigure.setTranslate(DefaultWidth - tinyAmount, 0.0);
		assertTrue(testFigure.overlaps(otherFigure),
		           testName + " unexpected non overlaping figures");
		assertTrue(otherFigure.overlaps(testFigure),
		           testName + " unexpected non overlaping figures");

	}

	/**
	 * Test method for {@link figures.Figure#clone()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("clone()")
	@Order(32)
	public final void testClone(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".clone()");
		System.out.println(testName);

		Figure cloned = null;

		try
		{
			cloned = testFigure.clone();
		}
		catch (IllegalStateException e)
		{
			/*
			 * When Figure / Circle are not completed yet both fill & edge
			 * color might be null triggering an IllegalStateException
			 */
			fail(testName + ": " + e.getLocalizedMessage());
		}

		assertNotSame(testFigure,
		              cloned,
		              testName +  " cloned figure is the same as original");
		assertEquals(testFigure,
		             cloned,
		             testName +  " cloned figure not equal to original");
		assertEquals(testFigure.hashCode(),
		             cloned.hashCode(),
		             testName +  " unexpected clone hashCode value");
	}

	/**
	 * Test method for {@link figures.Figure#equals(java.lang.Object)}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("equals(Object)")
	@Order(33)
	public final void testEqualsObject(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".equals(Object)");
		System.out.println(testName);

		/*
		 * equals(null) is always false
		 */
		assertFalse(testFigure.equals(null),
		            testName + " unexpected result with equals(null)");

		/*
		 * self equals is always true
		 */
		long start = System.nanoTime();
		boolean result1 = testFigure.equals(testFigure);
		long duration1 = System.nanoTime() - start;
		assertTrue(result1,
		            testName + " unexpected result with equals(self)");

		/*
		 * equals to clone is true
		 */
		Figure cloned = testFigure.clone();
		start = System.nanoTime();
		boolean result2 = testFigure.equals(cloned);
		long duration2 = System.nanoTime() - start;
		assertTrue(result2,
		            testName + " unexpected result with equals(cloned)");

		/*
		 * Assert comparison with self is always faster than comparison
		 * with cloned iff (obj == this) case has been checked in equals
		 */
		assertTrue(duration1 < duration2,
		           testName + " unexpected duration with equals(self): "
		           	+ "use \"if (obj == this)\" case in equals");

		/*
		 * equals to other figure of the same type
		 */
		Figure changed = testFigure.clone();
		changed.setFillColor(Color.RED);
		assertFalse(testFigure.equals(changed),
		            testName + " unexpected result with equals(changed Fill Color)");
		changed.setFillColor(null);
		assertFalse(testFigure.equals(changed),
		            testName + " unexpected result with equals(changed Fill Color null)");
		changed.setFillColor(DefaultFillColor);
		assertTrue(testFigure.equals(changed),
		           testName + " unexpected result with equals(unchanged Fill Color)");

		changed.setEdgeColor(Color.RED);
		assertFalse(testFigure.equals(changed),
		            testName + " unexpected result with equals(changed Edge color)");
		changed.setEdgeColor(null);
		assertFalse(testFigure.equals(changed),
		            testName + " unexpected result with equals(changed Edge color null)");
		changed.setEdgeColor(DefaultEdgeColor);
		assertTrue(testFigure.equals(changed),
		           testName + " unexpected result with equals(unchanged Edge color)");

		changed.setLineType(LineType.NONE);
		assertFalse(testFigure.equals(changed),
		            testName + " unexpected result with equals(changed Line type)");
		changed.setLineType(DefaultLineType);
		assertTrue(testFigure.equals(changed),
		           testName + " unexpected result with equals(unchanged Line type)");

		changed.setLineWidth(DefaultLineWidth + 2.34);
		assertFalse(testFigure.equals(changed),
		            testName + " unexpected result with equals(changed Line width)");
		changed.setLineWidth(DefaultLineWidth);
		assertTrue(testFigure.equals(changed),
		           testName + " unexpected result with equals(unchanged Line width)");

		/*
		 * NOTE : à commenter s'il n'y a pas de méthodes
		 * 	- translate
		 * 	- rotate
		 * 	- scale
		 */
		changed.setTranslate(DefaultWidth, 0.0);
		assertFalse(testFigure.equals(changed),
		            testName + " unexpected result with equals(changed translate X)");
		changed.setTranslate(0.0, 0.0);
		assertTrue(testFigure.equals(changed),
		           testName + " unexpected result with equals(unchanged translate X)");
		changed.setTranslate(0.0, DefaultHeight);
		assertFalse(testFigure.equals(changed),
		            testName + " unexpected result with equals(changed translate Y)");
		changed.setTranslate(0.0, 0.0);
		assertTrue(testFigure.equals(changed),
		           testName + " unexpected result with equals(unchanged translate Y)");

		changed.setRotate(45.3);
		assertFalse(testFigure.equals(changed),
		            testName + " unexpected result with equals(changed rotate)");
		changed.setRotate(0.0);
		assertTrue(testFigure.equals(changed),
		           testName + " unexpected result with equals(unchanged rotate)");

		changed.setScale(DefaultWidth, 1.0);
		assertFalse(testFigure.equals(changed),
		            testName + " unexpected result with equals(changed scale X)");
		changed.setScale(1.0, 1.0);
		assertTrue(testFigure.equals(changed),
		           testName + " unexpected result with equals(unchanged scale X)");
		changed.setScale(1.0, DefaultHeight);
		assertFalse(testFigure.equals(changed),
		            testName + " unexpected result with equals(changed scale Y)");
		changed.setScale(1.0, 1.0);
		assertTrue(testFigure.equals(changed),
		           testName + " unexpected result with equals(unchanged scale Y)");

		/*
		 * assert not equals to other figure
		 * iff there is at least 2 (or more) classes left in #figureTypes
		 */
		if (figureTypes.length > 1)
		{
			Class<? extends Figure> nextType = nextType(type);
			Figure otherFigure = constructFigure(nextType,
			                                     DefaultX,
			                                     DefaultY,
			                                     DefaultFillColor,
			                                     DefaultEdgeColor,
			                                     DefaultLineType,
			                                     DefaultLineWidth,
			                                     parentLogger);
			assertNotNull(otherFigure, testName + " constructed figure is null");
			assertFalse(testFigure.equals(otherFigure),
			            testName + " unexpected result with equals(other type figure)");
		}

		/*
		 * setLastPoint changes in figures
		 * CIRCLE: sets radius
		 * ELLIPSE: sets radiusX & radiusY
		 * RECTANGLE: sets width & height
		 */
//		final FigureType figureType = class2FigureType(type);
		double dx = 37.0;
		double dy = 45.0;
		if (changed instanceof Mouseable)
		{
			((Mouseable) changed).setLastPoint(new Point2D(DefaultX + dx, DefaultY + dy));
		}

		assertNotEquals(testFigure,
		                changed,
		                testName + " unexpected equality with changed last point");
		if (changed instanceof Mouseable)
		{
			((Mouseable) changed).setLastPoint(new Point2D(DefaultX, DefaultY));
		}
		assertEquals(testFigure,
		             changed,
		             testName + " unexpected hashCode inequality with unchanged last point");

	}

	/**
	 * Test method for {@link figures.Figure#hashCode()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("hashCode()")
	@Order(34)
	public final void testHashCode(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".hashCode()");
		System.out.println(testName);

		int expectedHash = testFigure.hashCode();

		/*
		 * Cloned figure has same hashcode
		 */
		Figure cloned = null;
		try
		{
			cloned = testFigure.clone();
		}
		catch (IllegalStateException e)
		{
			/*
			 * When Figure / Circle are not completed yet both fill & edge
			 * color might be null triggering an IllegalStateException
			 */
			fail(testName + ": " + e.getLocalizedMessage());
		}

		assertEquals(expectedHash,
		             cloned.hashCode(),
		             testName + " unexpected cloned hashcode");
		/*
		 * Other default constructed figure type won't have the same hashcode
		 */
		for (int i = 0; i < figureTypes.length; i++)
		{
			Figure otherFigure = constructFigure(figureTypes[i],
			                                     DefaultX,
			                                     DefaultY,
			                                     DefaultFillColor,
			                                     DefaultEdgeColor,
			                                     DefaultLineType,
			                                     DefaultLineWidth,
			                                     parentLogger);
			if (type != figureTypes[i])
			{
				assertNotEquals(expectedHash,
				                otherFigure.hashCode(),
				                testName + " unexpected hashcode equality with default constructed " +
				                figureTypes[i].getSimpleName());
			}
			else
			{
				assertEquals(expectedHash,
				             otherFigure.hashCode(),
				             testName + " unexpected hashcode inequality with default constructed");
			}
		}


		/*
		 * Assert any change in any parameter alters the hashCode and
		 * conversly cancelling a change should restore the previous hashCode
		 */
		Figure changed = testFigure.clone();
		changed.setFillColor(Color.RED);
		assertNotEquals(expectedHash,
		                changed.hashCode(),
		                testName + " unexpected hashCode equality with changed Fill");
		changed.setFillColor(null);
		assertNotEquals(expectedHash,
		                changed.hashCode(),
		                testName + " unexpected hashCode equality with changed Fill(null)");
		changed.setFillColor(DefaultFillColor);
		assertEquals(expectedHash,
		             changed.hashCode(),
		             testName + " unexpected hashCode inequality with unchanged Fill");

		changed.setEdgeColor(Color.RED);
		assertNotEquals(expectedHash,
		                changed.hashCode(),
		                testName + " unexpected hashCode equality with changed Edge");
		changed.setEdgeColor(null);
		assertNotEquals(expectedHash,
		                changed.hashCode(),
		                testName + " unexpected hashCode equality with changed Edge(null)");
		changed.setEdgeColor(DefaultEdgeColor);
		assertEquals(expectedHash,
		             changed.hashCode(),
		             testName + " unexpected hashCode inequality with unchanged Edge");

		changed.setLineType(LineType.DASHED);
		assertNotEquals(expectedHash,
		                changed.hashCode(),
		                testName + " unexpected hashCode equality with changed Line type");
		changed.setLineType(DefaultLineType);
		assertEquals(expectedHash,
		             changed.hashCode(),
		             testName + " unexpected hashCode inequality with unchanged Line type");

		changed.setLineWidth(7);
		assertNotEquals(expectedHash,
		                changed.hashCode(),
		                testName + " unexpected hashCode equality with changed Line width");
		changed.setLineWidth(DefaultLineWidth);
		assertEquals(expectedHash,
		             changed.hashCode(),
		             testName + " unexpected hashCode inequality with unchanged Line width");

		changed.setTranslate(DefaultX, DefaultY);
		assertNotEquals(expectedHash,
		                changed.hashCode(),
		                testName + " unexpected hashCode equality with changed translation");
		changed.setTranslate(0.0, 0.0);
		assertEquals(expectedHash,
		             changed.hashCode(),
		             testName + " unexpected hashCode inequality with unchanged translation");

		changed.setRotate(45.0);
		assertNotEquals(expectedHash,
		                changed.hashCode(),
		                testName + " unexpected hashCode equality with changed rotation");
		changed.setRotate(0.0);
		assertEquals(expectedHash,
		             changed.hashCode(),
		             testName + " unexpected hashCode inequality with unchanged rotation");

		changed.setScale(2.37, 1.05);
		assertNotEquals(expectedHash,
		                changed.hashCode(),
		                testName + " unexpected hashCode equality with changed scale");
		changed.setScale(1.0, 1.0);
		assertEquals(expectedHash,
		             changed.hashCode(),
		             testName + " unexpected hashCode inequality with unchanged scale");

		/*
		 * setLastPoint changes in figures
		 * CIRCLE: sets radius
		 * ELLIPSE: sets radiusX & radiusY
		 * RECTANGLE: sets width & height
		 */
//		final FigureType figureType = class2FigureType(type);
		double dx = 37.0;
		double dy = 45.0;
		if (changed instanceof Mouseable)
		{
			((Mouseable) changed).setLastPoint(new Point2D(DefaultX + dx, DefaultY + dy));
		}
		assertNotEquals(expectedHash,
		                changed.hashCode(),
		                testName + " unexpected hashCode equality with changed last point");
		if (changed instanceof Mouseable)
		{
			((Mouseable) changed).setLastPoint(new Point2D(DefaultX, DefaultY));
		}
		assertEquals(expectedHash,
		             changed.hashCode(),
		             testName + " unexpected hashCode inequality with unchanged last point");

		/*
		 * There might be aditionnal parameters atering hashCode values depending
		 * on the figure:
		 * 	- RoundedRectangle: Arc accessible through setArc
		 * 	- Polygon: adding a new point
		 */
	}

	/**
	 * Test method for {@link figures.Figure#toString()}.
	 * @param type the class of {@link Figure} to create
	 */
	@ParameterizedTest
	@MethodSource("figureClassesProvider")
	@DisplayName("toString()")
	@Order(35)
	public final void testToString(Class<? extends Figure> type)
	{
		setupTest(type);
		String testName = new String(testFigureClassName + ".toString()");
		System.out.println(testName);

		String expected = new String(type.getSimpleName() + " " + testFigure.getInstanceNumber());

		assertEquals(expected,
		             testFigure.toString(),
		             testName + " unexpected toString()");
	}
}
