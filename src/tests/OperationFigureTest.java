package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import figures.Circle;
import figures.Ellipse;
import figures.Figure;
import figures.OperationFigure;
import figures.Rectangle;
import figures.enums.LineType;
import figures.enums.OperationType;
import javafx.scene.paint.Color;
import logger.LoggerFactory;

/**
 * Test class for {@link OperationFigure}s
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("OpertionFigure")
class OperationFigureTest
{
	/**
	 * The figure under test
	 */
	private OperationFigure testFigure = null;

	/**
	 * Type of operation to perform between two pre-existing figures
	 */
	private static final OperationType[] operationTypes = new OperationType[] {
		OperationType.INTERSECTION,
		OperationType.SUBTRACTION,
		OperationType.UNION
	};

	/**
	 * First circle: center = (0.0, 0.0), radius = 10.0
	 * Intersects with {@link #circle2}, {@link #ellipse1}
	 */
	private Figure circle1;

	/**
	 * Second circle: center = (19.0, 0.0), radius = 10.0
	 * Intersects with {@link #circle1}
	 */
	private Figure circle2;

	/**
	 * Third circle: center = (21.0, 0.0), radius = 10.0
	 * Does not interesects with ({@link #circle1}
	 */
	private Figure circle3;

	/**
	 * First ellipse : center = (0.0, 0.0), radiusX = 15.0, radiusY = 10.0
	 */
	private Figure ellipse1;

	/**
	 * Second ellipse : center = (0.0, 19.0), radiusX = 15.0, radiusY = 10.0
	 * Intersects with {@link #ellipse1}
	 */
	private Figure ellipse2;

	/**
	 * Third ellipse : center = (0.0, 21.0), radiusX = 15.0, radiusY = 10.0
	 * Does not intersects with {@link #ellipse1}
	 */
	private Figure ellipse3;

	/**
	 * First rectangle : topleft = (-10.0, -15.0), width = 20.0 height = 30.0
	 */
	private Figure rectangle1;

	/**
	 * Second rectangle : topleft = (-29.0, -15.0), width = 20.0 height = 30.0
	 * Interesects with {@link #rectangle1}
	 */
	private Figure rectangle2;

	/**
	 * Third rectangle : topleft = (-31.0, -15.0), width = 20.0 height = 30.0
	 * Does not interesects with {@link #rectangle1}
	 */
	private Figure rectangle3;

	/**
	 * The array containing all figures above : {@link #circle1},
	 * {@link #circle2}, {@link #circle3}, {@link #ellipse1}, {@link #ellipse2},
	 * {@link #ellipse3}, {@link #rectangle1}, {@link #rectangle2},
	 * {@link #rectangle3}
	 */
	private Figure[] figures = null;

	/**
	 * Array containing expected overlaps between {@link #figures}
	 */
	private boolean[][] overlaps = null;

	/**
	 * A Stream of operation types to provide to tests
	 * @return A Stream of operation types to provide to tests
	 */
	private static Stream<OperationType> operationTypeProvider()
	{
		return Stream.of(operationTypes);
	}

	/**
	 * Global Logger
	 */
	private static Logger globalLogger = null;

	/**
	 * Logger to use during each test
	 */
	private Logger parentLogger;

	/**
	 * Default fill color (for figure 1)
	 */
	private final static Color DefaultFillColor = Color.WHITE;

	/**
	 * Other Default fill color (for figure 2)
	 */
	private final static Color OtherFillColor = Color.BLUE;

	/**
	 * Default edge color
	 */
	private final static Color DefaultEdgeColor = Color.BLACK;

	/**
	 * Other default edge color
	 */
	private final static Color OtherEdgeColor = Color.GREEN;

	/**
	 * Default line type
	 */
	private final static LineType DefaultLineType = LineType.DASHED;

	/**
	 * Other line type
	 */
	private final static LineType OtherLineType = LineType.SOLID;

	/**
	 * Default line width
	 */
	private final static double DefaultLineWidth = 1.0;

	/**
	 * Default X location
	 */
	private final static double DefaultX = 300.0;

	/**
	 * Default Y location
	 */
	private final static double DefaultY = 500.0;

	/**
	 * Default dimension
	 */
	private final static double thirty = 30.0;

	/**
	 * Default dimension
	 */
	private final static double twenty = 20.0;

//	/**
//	 * Tolerance used in numerical comparisons
//	 */
//	private static final double tolerance = Figure.threshold;

	/**
	 * Converts operation type to String.
	 * Differs from {@link OperationType#toString()}.
	 * @param opType the operation type to convert
	 * @return a string representing this operation
	 */
	private final static String operationType2String(OperationType opType)
	{
		switch (opType)
		{
			case INTERSECTION:
				return "intersection";
			case SUBTRACTION:
				return "subtract";
			case UNION:
				return "union";
		}
		return null;
	}

	/**
	 * Setup before all tests
	 * @throws java.lang.Exception If setup before all tests fails
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception
	{
		globalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		System.out.println("-------------------------------------------------");
		System.out.println("Start of OperationFigure Tests");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Tear down after all tests
	 * @throws java.lang.Exception If teardown after all tests fails
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception
	{
		System.out.println("-------------------------------------------------");
		System.out.println("End of OperationFigure Tests");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Setup before each test
	 * @throws java.lang.Exception If setup before each test fails
	 */
	@BeforeEach
	void setUp() throws Exception
	{
		testFigure = null;
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
		double radius = 10.0;
		double radiusMin = (2 * radius) - (2.0 * DefaultLineWidth);
		double radiusMax = (2 * radius) + (2.0 * DefaultLineWidth);
		circle1 = new Circle(DefaultFillColor,
		                     DefaultEdgeColor,
		                     DefaultLineType,
		                     DefaultLineWidth,
		                     parentLogger,
		                     DefaultX,
		                     DefaultY,
		                     radius);
		circle2 = new Circle(OtherFillColor,
		                     OtherEdgeColor,
		                     OtherLineType,
		                     DefaultLineWidth,
		                     parentLogger,
		                     DefaultX + radiusMin,
		                     DefaultY,
		                     radius);
		circle3 = new Circle(DefaultFillColor,
		                     DefaultEdgeColor,
		                     DefaultLineType,
		                     DefaultLineWidth,
		                     parentLogger,
		                     DefaultX + radiusMax,
		                     DefaultY,
		                     radius);
		double radiusX = 15.0;
		double radiusY = 10.0;
		radiusMin = (2.0 * radiusY) - (2.0 * DefaultLineWidth);
		radiusMax = (2.0 * radiusY) + (2.0 * DefaultLineWidth);
		ellipse1 = new Ellipse(DefaultFillColor,
		                       DefaultEdgeColor,
		                       DefaultLineType,
		                       DefaultLineWidth,
		                       parentLogger,
		                       DefaultX,
		                       DefaultY,
		                       radiusX,
		                       radiusY);
		ellipse2 = new Ellipse(OtherFillColor,
		                       OtherEdgeColor,
		                       OtherLineType,
		                       DefaultLineWidth,
		                       parentLogger,
		                       DefaultX,
		                       DefaultY + radiusMin,
		                       radiusX,
		                       radiusY);
		ellipse3 = new Ellipse(DefaultFillColor,
		                       DefaultEdgeColor,
		                       DefaultLineType,
		                       DefaultLineWidth,
		                       parentLogger,
		                       DefaultX,
		                       DefaultY + radiusMax,
		                       radiusX,
		                       radiusY);
		radiusX = twenty;
		radiusY = thirty;
		double radiusY2 = radiusY / 2.0;
		double radiusX2 = radiusX / 2.0;
		rectangle1 = new Rectangle(DefaultFillColor,
		                           DefaultEdgeColor,
		                           DefaultLineType,
		                           DefaultLineWidth,
		                           parentLogger,
		                           DefaultX - radiusX2,
		                           DefaultY - radiusY2,
		                           radiusX,
		                           radiusY);
		rectangle2 = new Rectangle(OtherFillColor,
		                           OtherEdgeColor,
		                           OtherLineType,
		                           DefaultLineWidth,
		                           parentLogger,
		                           (DefaultX - radiusX) + (2.0 * DefaultLineWidth),
		                           DefaultY - radiusY2,
		                           radiusX,
		                           radiusY);
		rectangle3 = new Rectangle(DefaultFillColor,
		                           DefaultEdgeColor,
		                           DefaultLineType,
		                           DefaultLineWidth,
		                           parentLogger,
		                           DefaultX - radiusX - (2.0 * DefaultLineWidth),
		                           DefaultY - radiusY2,
		                           radiusX,
		                           radiusY);
		/*
		 * figures array
		 */
		figures = new Figure[] {
			circle1,
			circle2,
			circle3,
			ellipse1,
			ellipse2,
			ellipse3,
			rectangle1,
			rectangle2,
			rectangle3
		};

		/*
		 * figures overlap matrix
		 * assumes Figure#overlaps works fine
		 */
		overlaps = new boolean[figures.length][figures.length];
		for (int i = 0; i < figures.length; i++)
		{
			for (int j = 0; j < figures.length; j++)
			{
				overlaps[i][j] = figures[i].overlaps(figures[j]);
			}
		}
	}

	/**
	 * Tear down after each test
	 * @throws java.lang.Exception If teardown after each test fails
	 */
	@AfterEach
	void tearDown() throws Exception
	{
		testFigure = null;
		rectangle3 = null;
		rectangle2 = null;
		rectangle1 = null;
		ellipse3 = null;
		ellipse2 = null;
		ellipse1 = null;
		circle3 = null;
		circle2 = null;
		circle1 = null;
		figures = null;
		overlaps = null;
	}

	/**
	 * Test method for {@link figures.OperationFigure#OperationFigure(figures.Figure, figures.Figure, figures.enums.OperationType, java.util.logging.Logger)}.
	 * @param opType The type of operation to perform
	 * @param info Test informations
	 */
	@ParameterizedTest(name = "OperationFigure(Figure, Figure, {arguments})")
	@MethodSource("operationTypeProvider")
	@DisplayName("OperationFigure(Figure, Figure, OperationType)")
	@Order(1)
	final void testOperationFigureFigureFigureOperationTypeLogger(OperationType opType,
	                                                              TestInfo info)
	{
		String operationName = operationType2String(opType);
		String testName = info.getDisplayName();
		testName.replace("OperationType", operationName);
		System.out.println(testName);

		/*
		 * If either figure 1 or figure 2 is null,
		 * throw NullPointerException
		 */
		assertThrows(NullPointerException.class, () -> {
			testFigure = new OperationFigure(figures[0],
			                                 null,
			                                 opType,
			                                 parentLogger);
		}, testName + "didn't threw exception");
		assertThrows(NullPointerException.class, () -> {
			testFigure = new OperationFigure(null,
			                                 figures[0],
			                                 opType,
			                                 parentLogger);
		}, testName + "didn't threw exception");


		for (int i = 0; i < figures.length; i++)
		{
			final Figure fi = figures[i];
			for (int j = 0; j < figures.length; j++)
			{
				final Figure fj = figures[j];
				boolean overlaped = overlaps[i][j];
				List<Figure> components = List.of(fi, fj);
				if ((i == j) || (!overlaped))
				{
					assertThrows(IllegalArgumentException.class, () -> {
						testFigure = new OperationFigure(fi,
						                                 fj,
						                                 opType,
						                                 parentLogger);
					},
					             testName + " Creating from figure[" + i
					                 + "] and figure[" + j
					                 + "] didn't threw exception");
				}
				else
				{
					try
					{
						/*
						 * OperationFigure(Figure, Figure, ...)
						 * is supposed to create clones of provided figures
						 */
						testFigure = new OperationFigure(fi,
						                                 fj,
						                                 opType,
						                                 parentLogger);
						assertNotNull(testFigure,
						              testName + " unexpected null instance");
						List<Figure> internals = testFigure.getFigures();
						Iterator<Figure> itc = components.iterator();
						Iterator<Figure> iti = internals.iterator();
						while (itc.hasNext() && iti.hasNext())
						{
							Figure componentFigure = itc.next();
							Figure internalFigure = iti.next();
							assertEquals(componentFigure,
							             internalFigure,
							             testName + "unexpected internal "
							             	+ "figure inequality");
							assertNotSame(componentFigure,
							              internalFigure,
							              testName + " unexpected same internal"
							              	+ " figure");
						}
						assertEquals(itc.hasNext(),
						             iti.hasNext(),
						             testName + " components and internals have"
						             	+ " different sizes");

						assertEquals(opType,
						             testFigure.getOperationType(),
						             testName + " unexpected figures "
						             	+ "operation type");
					}
					catch (IllegalArgumentException e)
					{
						fail(testName + " " + e.getLocalizedMessage());
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link figures.OperationFigure#OperationFigure(figures.Figure)}.
	 * @param info Test informations
	 */
	@Test
	@DisplayName("OperationFigure(Figure)")
	@Order(3)
	final void testOperationFigureFigure(TestInfo info)
	{
		String testName = info.getDisplayName();
		System.out.println(testName);

		/*
		 * If provided figure is null
		 * throw NullPointerException
		 */
		assertThrows(NullPointerException.class, () -> {
			testFigure = new OperationFigure(null);
		}, testName + "didn't threw exception");

		/*
		 * If provided figure is not an OperationFigure
		 * throw IllegalArgumentException
		 */
		assertThrows(IllegalArgumentException.class, () -> {
			testFigure = new OperationFigure(circle1);
		}, testName + "didn't threw exception");

		for (int i = 0; i < figures.length; i++)
		{
			final Figure fi = figures[i];
			for (int j = 0; j < figures.length; j++)
			{
				final Figure fj = figures[j];
				if ((i == j) || !overlaps[i][j])
				{
					continue;
				}
				for (OperationType opType : OperationType.all())
				{
					try
					{
						OperationFigure opFig = new OperationFigure(fi,
						                                            fj,
						                                            opType,
						                                            parentLogger);
						assertNotNull(opFig, testName + " unexpected null "
							+ "instance");

						testFigure = new OperationFigure(opFig);
						assertNotNull(testFigure, testName + " unexpected "
							+ "null instance");

						/*
						 * Copied figures should be equal
						 */
						assertEquals(opFig,
						             testFigure,
						             testName + " unexpected non equality");

						/*
						 * Internal figures should be identical but
						 * distincts
						 */
						List<Figure> list1 = opFig.getFigures();
						List<Figure> list2 = testFigure.getFigures();
						Iterator<Figure> it1 = list1.iterator();
						Iterator<Figure> it2 = list2.iterator();
						while (it1.hasNext() && it2.hasNext())
						{
							Figure f1 = it1.next();
							Figure f2 = it2.next();
							assertNotSame(f1,
							              f2,
							              testName + " unexpected same"
							              	+ " internal figure");
							assertEquals(f1,
							             f2,
							             testName + " unexpected internal "
							             	+ "figures inequality");
						}
						assertEquals(it1.hasNext(),
						             it2.hasNext(),
						             testName + " unexpected internal "
						             	+ "figures number");

					}
					catch (IllegalArgumentException e)
					{
						fail(testName + " " + e.getLocalizedMessage());
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link figures.OperationFigure#hashCode()}.
	 * @param info Test informations
	 */
	@Test
	@DisplayName("hashCode()")
	@Order(7)
	final void testHashCode(TestInfo info)
	{
		String testName = info.getDisplayName();
		System.out.println(testName);

		for (int i = 0; i < figures.length; i++)
		{
			final Figure fi = figures[i];
			final int fiHash = fi.hashCode();
			for (int j = 0; j < figures.length; j++)
			{
				final Figure fj = figures[j];
				final int fjHash = fj.hashCode();
				if ((i == j) || !overlaps[i][j])
				{
					continue;
				}
				for (OperationType opType : OperationType.all())
				{
					try
					{
						testFigure = new OperationFigure(fi,
						                                 fj,
						                                 opType,
						                                 parentLogger);
						assertNotNull(testFigure,
						              testName + " unexpected null instance");

						final int opHash =
						    testFigure.getOperationType().hashCode();
						final int prime = 31;
						int superHash = 1;
						Color fillColor = fi.getFillColor();
						superHash = (prime * superHash)
						    + ((fillColor == null) ? 0 : fillColor.hashCode());
						Color edgeColor = fi.getEdgeColor();
						superHash = (prime * superHash)
						    + ((edgeColor == null) ? 0 : edgeColor.hashCode());
						LineType lineType = fi.getLineType();
						superHash = (prime * superHash)
						    + ((lineType == null) ? 0 : lineType.hashCode());
						double lineWidth = fi.getLineWidth();
						long temp;
						temp = Double.doubleToLongBits(lineWidth);
						superHash =
						    (prime * superHash) + (int) (temp ^ (temp >>> 32));
						temp = Double.doubleToLongBits(0.0);
						superHash =
						    (prime * superHash) + (int) (temp ^ (temp >>> 32));
						temp = Double.doubleToLongBits(0.0);
						superHash =
						    (prime * superHash) + (int) (temp ^ (temp >>> 32));
						temp = Double.doubleToLongBits(0.0);
						superHash =
						    (prime * superHash) + (int) (temp ^ (temp >>> 32));
						temp = Double.doubleToLongBits(1.0);
						superHash =
						    (prime * superHash) + (int) (temp ^ (temp >>> 32));
						temp = Double.doubleToLongBits(1.0);
						superHash =
						    (prime * superHash) + (int) (temp ^ (temp >>> 32));
						int expectedHash = superHash;
						expectedHash = (expectedHash * prime) + fiHash;
						expectedHash = (expectedHash * prime) + fjHash;
						expectedHash = (expectedHash * prime) + opHash;

						assertEquals(expectedHash,
						             testFigure.hashCode(),
						             testName + " unexpected hash code");
					}
					catch (IllegalArgumentException e)
					{
						fail(testName + " " + e.getLocalizedMessage());
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link figures.OperationFigure#clone()}.
	 * @param info Test informations
	 */
	@Test
	@DisplayName("clone()")
	@Order(4)
	final void testClone(TestInfo info)
	{
		String testName = info.getDisplayName();
		System.out.println(testName);

		for (int i = 0; i < figures.length; i++)
		{
			final Figure fi = figures[i];
			for (int j = 0; j < figures.length; j++)
			{
				final Figure fj = figures[j];
				if ((i == j) || !overlaps[i][j])
				{
					continue;
				}
				for (OperationType opType : OperationType.all())
				{
					try
					{
						OperationFigure opFig =
						    new OperationFigure(fi,
						                        fj,
						                        opType,
						                        parentLogger);
						assertNotNull(opFig,
						              testName + " unexpected null instance");

						testFigure = (OperationFigure)opFig.clone();
						assertNotNull(testFigure,
						              testName + " unexpected null instance");
						assertNotSame(opFig,
						              testFigure,
						              testName + " clone is cloned");
						assertEquals(opFig,
						             testFigure,
						             testName + " unexpected clone inequality");
					}
					catch (IllegalArgumentException e)
					{
						fail(testName + " " + e.getLocalizedMessage());
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link figures.OperationFigure#toString()}.
	 * @param info Test informations
	 */
	@Test
	@DisplayName("toString()")
	@Order(6)
	final void testToString(TestInfo info)
	{
		String testName = info.getDisplayName();
		System.out.println(testName);

		for (int i = 0; i < figures.length; i++)
		{
			final Figure fi = figures[i];
			for (int j = 0; j < figures.length; j++)
			{
				final Figure fj = figures[j];
				if ((i == j) || !overlaps[i][j])
				{
					continue;
				}
				for (OperationType opType : OperationType.all())
				{
					try
					{
						testFigure = new OperationFigure(fi,
						                                 fj,
						                                 opType,
						                                 parentLogger);
						assertNotNull(testFigure,
						              testName + " unexpected null instance");

						StringBuilder sb = new StringBuilder();
						sb.append(fi.toString());
						sb.append(opType.toString());
						sb.append(fj.toString());

						assertEquals(sb.toString(),
						             testFigure.toString(),
						             testName + " unexpected toString()");
					}
					catch (IllegalArgumentException e)
					{
						fail(testName + " " + e.getLocalizedMessage());
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link figures.OperationFigure#getFigures()}.
	 * @param info Test informations
	 */
	@Test
	@DisplayName("getFigures()")
	@Order(2)
	final void testGetFigures(TestInfo info)
	{
		String testName = info.getDisplayName();
		System.out.println(testName);

		for (int i = 0; i < figures.length; i++)
		{
			final Figure fi = figures[i];
			for (int j = 0; j < figures.length; j++)
			{
				final Figure fj = figures[j].clone();
				if ((i == j) || !overlaps[i][j])
				{
					continue;
				}
				for (OperationType opType : OperationType.all())
				{
					try
					{
						List<Figure> components = List.of(fi, fj);
						testFigure = new OperationFigure(fi,
						                                 fj,
						                                 opType,
						                                 parentLogger);
						assertNotNull(testFigure,
						              testName + " unexpected null instance");
						List<Figure> internals = testFigure.getFigures();
						assertNotNull(internals,
						              testName + " unexpected null internals "
						              	+ "list");
						/*
						 * In OperationFigure constructor, I chose to
						 * create clones of provided figures so internal
						 * figures should be equal but distinct
						 * Note : We could have made the opposite choice
						 */
						Iterator<Figure> itc = components.iterator();
						Iterator<Figure> iti = internals.iterator();
						while (itc.hasNext() && iti.hasNext())
						{
							Figure componentFigure = itc.next();
							Figure internalFigure = iti.next();
							assertEquals(componentFigure,
							             internalFigure,
							             testName + " unexpected internal"
							             	+ " inequality");
							assertNotSame(componentFigure,
							              internalFigure,
							              testName + " unexpected same internal"
							              	+ " figure");
						}
						assertEquals(itc.hasNext(),
						             iti.hasNext(),
						             testName + " unexpected iterator status");

					}
					catch (IllegalArgumentException e)
					{
						fail(testName + " " + e.getLocalizedMessage());
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link figures.OperationFigure#getOperationType()}.
	 * @param info Test informations
	 */
	@Test
	@DisplayName("getOperationType()")
	@Order(5)
	final void testGetOperationType(TestInfo info)
	{
		String testName = info.getDisplayName();
		System.out.println(testName);

		for (int i = 0; i < figures.length; i++)
		{
			final Figure fi = figures[i];
			for (int j = 0; j < figures.length; j++)
			{
				final Figure fj = figures[j];
				if ((i == j) || !overlaps[i][j])
				{
					continue;
				}
				for (OperationType opType : OperationType.all())
				{
					try
					{
						testFigure = new OperationFigure(fi,
						                                 fj,
						                                 opType,
						                                 parentLogger);
						assertNotNull(testFigure,
						              testName + " unexpected null instance");
						assertSame(opType,
						           testFigure.getOperationType(),
						           testName + " unexpected operation type");
					}
					catch (IllegalArgumentException e)
					{
						fail(testName + " " + e.getLocalizedMessage());
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link java.lang.Object#equals(java.lang.Object)}.
	 * @param info Test informations
	 */
	@Test
	@Order(8)
	final void testEqualsObject(TestInfo info)
	{
		String testName = info.getDisplayName();
		System.out.println(testName);

		for (int i = 0; i < figures.length; i++)
		{
			final Figure fi = figures[i];
			for (int j = 0; j < figures.length; j++)
			{
				final Figure fj = figures[j];
				if ((i == j) || !overlaps[i][j])
				{
					continue;
				}
				for (OperationType opType : OperationType.all())
				{
					try
					{
						Figure opFig =
						    new OperationFigure(fi, fj, opType, parentLogger);
						assertNotNull(opFig,
						              testName + " unexpected null instance");
						testFigure = (OperationFigure) opFig.clone();
						assertEquals(opFig,
						             testFigure,
						             testName + " unexpected inequality");

						/*
						 * Inequality to null
						 */
						assertFalse(testFigure.equals(null),
						            testName + " unexpected equality to null");

						/*
						 * Equality to this
						 */
						assertTrue(testFigure.equals(testFigure),
						           testName + " unexpected inequality to this");

						/*
						 * Transformed figure is no more equal
						 */
						testFigure.setTranslate(1.0, 2.0);
						assertNotEquals(opFig,
						                testFigure,
						                testName + " unexpected equality");

						testFigure = (OperationFigure) opFig.clone();
						testFigure.setRotate(45.0);
						assertNotEquals(opFig,
						                testFigure,
						                testName + " unexpected equality");

						testFigure = (OperationFigure) opFig.clone();
						testFigure.setScale(1.5, 0.75);
						assertNotEquals(opFig,
						                testFigure,
						                testName + " unexpected equality");
						/*
						 * Figure with other drawing params is no more equal
						 */
						testFigure = (OperationFigure) opFig.clone();
						testFigure.setFillColor(Color.ALICEBLUE);
						assertNotEquals(opFig,
						                testFigure,
						                testName + " unexpected equality");

						testFigure = (OperationFigure) opFig.clone();
						testFigure.setEdgeColor(Color.ALICEBLUE);
						assertNotEquals(opFig,
						                testFigure,
						                testName + " unexpected equality");

						testFigure = (OperationFigure) opFig.clone();
						testFigure.setLineType(LineType.NONE);
						assertNotEquals(opFig,
						                testFigure,
						                testName + " unexpected equality");

						testFigure = (OperationFigure) opFig.clone();
						testFigure.setLineWidth(DefaultLineWidth * 4.0);
						assertNotEquals(opFig,
						                testFigure,
						                testName + " unexpected equality");

					}
					catch (IllegalArgumentException e)
					{
						fail(testName + " " + e.getLocalizedMessage());
					}
				}
			}
		}
	}
}
