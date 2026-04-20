/**
 *
 */
package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import history.HistoryManager;
import history.Memento;
import history.Originator;
import logger.LoggerFactory;
import tests.history.Element;
import tests.history.TestApp;

/**
 * {@link HistoryManager} test class
 * @author davidroussel
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("HistoryManager<E>")
class HistoryManagerTest
{
	/**
	 * App to create and set {@link Memento}s
	 */
	private static TestApp app = null;

	/**
	 * Global Logger
	 */
	private static Logger globalLogger = null;

	/**
	 * Logger to use during each test
	 */
	private Logger parentLogger;

	/**
	 * names to fill {@link #app} :
	 * "Lorem ipsum dolor sit amet"
	 */
	private static final String[] names = new String[] {
		"Lorem",
		"ipsum",
		"dolor",
		"sit",
		"amet",
		"consectetur",
		"adipiscing",	// end of #expectedList content
		"elit",
		"sed",
		"do",
		"eiusmod",
		"tempor",
		"incididunt",
		"ut",
		"labore"
	};

	/**
	 * Size the {@link #initialList}
	 */
	private final static int initialSize = names.length / 2;

	/**
	 * Size of {@link #secondaryList}
	 */
	private final static int secondarySize = names.length - initialSize;

	/**
	 * Initial history size smaller than {@link #secondarySize} so that
	 * successives additions from {@link #secondaryList} saturates the
	 * {@link #manager}
	 */
	private final static int historySize = secondarySize - 1;

	/**
	 * Name of the class under test
	 */
	private final static String className = HistoryManager.class.getSimpleName();

	/**
	 * Name of the current test
	 */
	private String testName;

	/**
	 * The initial list of dummies expected in Memento state
	 */
	private List<Element<String>> initialList;

	/**
	 * The complementary list of dummies to add to {@link #app} after setup
	 */
	private List<Element<String>> secondaryList;

	/**
	 * The history manger to test.
	 * Instanciated for each test
	 */
	private HistoryManager<Element<String>> manager;

	/**
	 * Setup before all tests
	 */
	@BeforeAll
	static void setUpBeforeClass()
	{
		globalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		app = new TestApp();
		System.out.println("-------------------------------------------------");
		System.out.println("Start of HistoryManager Tests");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Tear down after all tests
	 */
	@AfterAll
	static void tearDownAfterClass()
	{
		app = null;
		globalLogger = null;
		System.out.println("-------------------------------------------------");
		System.out.println("End of HistoryManager Tests");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Setup before each test
	 * @param testInfo Informations on the following test
	 */
	@BeforeEach
	void setUp(TestInfo testInfo)
	{
		testName = className + '.' + testInfo.getDisplayName();
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
		initialList = new ArrayList<>();
		secondaryList = new ArrayList<>();
		int i = 0;
		for (; i < (names.length / 2); i++)
		{
			Element<String> element = new Element<>(names[i]);
			app.add(element);
			initialList.add(element);
		}
		for (; i < names.length; i++)
		{
			secondaryList.add(new Element<>(names[i]));
		}

		// Do Not create #manager if test is constructor test
		if (!testInfo.getDisplayName().equals("HistoryManager(Originator<E>, int, Logger)"))
		{
			/*
			 * app initially contains names.length / 2 elts
			 * manager is initially empty
			 */

			manager = new HistoryManager<>(app, historySize, parentLogger);
		}
	}

	/**
	 * Tear down after each test
	 */
	@AfterEach
	void tearDown()
	{
		app.clear();
		testName = null;
		parentLogger = null;
		initialList.clear();
		initialList = null;
		secondaryList.clear();
		secondaryList = null;
		manager = null;
	}

	/**
	 * Test method for {@link history.HistoryManager#HistoryManager(history.Originator, int, java.util.logging.Logger)}.
	 */
	@Test
	@DisplayName("HistoryManager(Originator<E>, int, Logger)")
	@Order(1)
	public final void testHistoryManager()
	{
		System.out.println(testName);

		assertNull(manager, testName + " unexpected non null history manager");

		@SuppressWarnings("rawtypes")
		Constructor<HistoryManager> constructor = null;
		Class<?>[] constructorsArgs = new Class<?>[3];
		constructorsArgs[0] = Originator.class;
		constructorsArgs[1] = int.class;
		constructorsArgs[2] = Logger.class;

		try
		{
			constructor = HistoryManager.class.getConstructor(constructorsArgs);
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
			args[0] = app;
			args[1] = historySize;
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
			assertEquals(HistoryManager.class,
			             instance.getClass(),
			             testName + " unexpected instance class "
			             + instance.getClass().getSimpleName());
			HistoryManager<?> managerInstance = (HistoryManager<?>) instance;

			assertEquals(historySize,
			             managerInstance.size(),
			             testName + " unexpected size on manager instance");
			assertEquals(0,
			             managerInstance.undoSize(),
			             testName + " unexpected undo size on manager instance");
			assertEquals(0,
			             managerInstance.redoSize(),
			             testName + " unexpected redo size on manager instance");
		}
		else
		{
			fail(testName + " null constructor");
		}
	}

	/**
	 * Test method for {@link history.HistoryManager#undoSize()}.
	 */
	@Test
	@DisplayName("undoSize()")
	@Order(2)
	public final void testUndoSize()
	{
		System.out.println(testName);

		assertNotNull(manager, testName + " unexpected null history manager");

		/*
		 * Initial undo size is 0
		 */
		assertEquals(0,
		             manager.undoSize(),
		             testName + " unexpected initial undo size");

		int expectedUndoSize = 1;
		/*
		 * first record
		 */
		manager.record();
		assertEquals(expectedUndoSize,
		             manager.undoSize(),
		             testName + " unexpected first undo size");

		/*
		 * Assert we can't push the same state twice
		 * AND that a warning message has been issued
		 */
		LogHandler handler = new LogHandler(2);
		parentLogger.addHandler(handler);
		manager.record();
		String commonPart = " after recording same state";
		assertEquals(expectedUndoSize,
		             manager.undoSize(),
		             testName + " unexpected undo size" + commonPart);
		assertFalse(handler.isEmpty(),
		            testName + " unexpected empty log messages handler" + commonPart);
		assertEquals(Level.WARNING,
		             handler.pop().getLevel(),
		             testName + " unexpected log message level" + commonPart);
		/*
		 * growing undo size up to #historySize
		 */
		int count = 0;
		for (Element<String> element : secondaryList)
		{
			count++;
			app.add(element);
			manager.record();
			final int nextExpectedUndoSize = expectedUndoSize + 1;
			expectedUndoSize = nextExpectedUndoSize < historySize ?
				nextExpectedUndoSize : historySize;
			assertEquals(expectedUndoSize,
			             manager.undoSize(),
			             testName + " unexpected undo size after adding "
			                 + count + " mementos");
		}

		/*
		 * assert decreasing undo size with undos
		 */
		while (manager.undoSize() > 0)
		{
			manager.undo();
			assertEquals(--expectedUndoSize,
			             manager.undoSize(),
			             testName + " unexpected undo size after undo");
		}

		/*
		 * assert increasing undo size with redos
		 */
		while (manager.redoSize() > 0)
		{
			manager.redo();
			assertEquals(++expectedUndoSize,
			             manager.undoSize(),
			             testName + " unexpected undo size after redo");
		}
		parentLogger.removeHandler(handler);
	}

	/**
	 * Test method for {@link history.HistoryManager#redoSize()}.
	 */
	@Test
	@DisplayName("redoSize()")
	@Order(3)
	public final void testRedoSize()
	{
		System.out.println(testName);

		assertNotNull(manager, testName + " unexpected null history manager");

		/*
		 * assert initial redo size is O
		 */
		int expectedRedoSize = 0;
		assertEquals(expectedRedoSize,
		             manager.redoSize(),
		             testName + " unexpected initial redo size");

		/*
		 * fill undo stack so we can empty it later and fill redo stack
		 */
		for (Element<String> element : secondaryList)
		{
			manager.record();
			app.add(element);
		}

		/*
		 * then perform multiple undos to
		 * 	- empty undo stack
		 * 	- fill up redo stack
		 */
		while (manager.undoSize() > 0)
		{
			manager.undo();
			assertEquals(++expectedRedoSize,
			             manager.redoSize(),
			             testName + " unexpected redo size after undo");
		}

		/*
		 * then perform multiple redos to
		 * 	- empty redo stack
		 * 	- fill up undo stack
		 */
		while (manager.redoSize() > 0)
		{
			manager.redo();
			assertEquals(--expectedRedoSize,
			             manager.redoSize(),
			             testName + " unexpected redo size after redo");
		}
	}

	/**
	 * Test method for {@link history.HistoryManager#size()}.
	 */
	@Test
	@DisplayName("size()")
	@Order(4)
	public final void testSize()
	{
		System.out.println(testName);

		assertNotNull(manager, testName + " unexpected null history manager");
		assertEquals(historySize,
		             manager.size(),
		             testName + " unexpected history manager size");
	}

	/**
	 * Test method for {@link history.HistoryManager#setSize(int)}.
	 */
	@Test
	@DisplayName("setSize(int)")
	@Order(5)
	public final void testSetSize()
	{
		System.out.println(testName);

		assertNotNull(manager, testName + " unexpected null history manager");
		assertEquals(historySize,
		             manager.size(),
		             testName + " unexpected history manager size");

		/*
		 * fill up undo stack
		 */
		for (Element<String> element : secondaryList)
		{
			app.add(element);
			manager.record();
		}

		/*
		 * Now set a smaller size to evt trim undo & redo stack sizes
		 */
		int expectedUndoSize = manager.undoSize() - 2;
		manager.setSize(expectedUndoSize);

		/*
		 * assert undo size has changed
		 */
		assertEquals(expectedUndoSize,
		             manager.undoSize(),
		             testName + " unexpected undoSize after smaller setSize");
		assertEquals(expectedUndoSize,
		             manager.size(),
		             testName + " unexpected size after setSize");

		/*
		 * fill up redo stack
		 */
		while (manager.undoSize() > 0)
		{
			manager.undo();
		}

		/*
		 * Now set a smaller size to evt trim undo & redo stack sizes
		 */
		int expectedRedoSize = manager.redoSize() - 2;
		manager.setSize(expectedRedoSize);

		/*
		 * assert redo size has changed
		 */
		assertEquals(expectedRedoSize,
		             manager.redoSize(),
		             testName + " unexpected redoSize after smaller setSize");
		assertEquals(expectedRedoSize,
		             manager.size(),
		             testName + " unexpected size after setSize");
	}

	/**
	 * Test method for {@link history.HistoryManager#record()}.
	 */
	@Test
	@DisplayName("record()")
	@Order(6)
	public final void testRecord()
	{
		System.out.println(testName);

		assertNotNull(manager, testName + " unexpected null history manager");

		/*
		 * assert initial undo size is zero
		 */
		int expectedUndoSize = 0;
		assertEquals(expectedUndoSize,
		             manager.undoSize(),
		             testName + " unexpected initial undo size");

		/*
		 * successive records (up to #historySize)
		 */
		final int expectedRedoSize = 0;
		for (Element<String> element : secondaryList)
		{
			manager.record();
			app.add(element);
			expectedUndoSize = (expectedUndoSize + 1) < historySize ? (expectedUndoSize + 1) : historySize;
			assertEquals(expectedUndoSize,
			             manager.undoSize(),
			             testName + " unexpected undo size after record");
			assertEquals(expectedRedoSize,
			             manager.redoSize(),
			             testName + " unexpected redo size after record");
		}

		/*
		 * Assert we can't record the same state twice
		 */
		LogHandler handler = new LogHandler(1);
		parentLogger.addHandler(handler);

		expectedUndoSize = manager.undoSize();
		manager.record();
		manager.record();

		parentLogger.removeHandler(handler);

		String commonPart = " after same state record";
		assertEquals(expectedUndoSize,
		             manager.undoSize(),
		             testName + " unexpected undo size" + commonPart);
		assertFalse(handler.isEmpty(),
		            testName + " unexpected empty log message handler" + commonPart);
		assertEquals(Level.WARNING,
		             handler.pop().getLevel(),
		             testName +  "unexpected message level" + commonPart);

		/*
		 * record results are assessed in #testUndo
		 */
	}

	/**
	 * Test method for {@link history.HistoryManager#undo()}.
	 */
	@Test
	@DisplayName("undo()")
	@Order(7)
	public final void testUndo()
	{
		System.out.println(testName);

		assertNotNull(manager, testName + " unexpected null history manager");

		/*
		 * assert initial undo size is 0
		 */
		assertEquals(0,
		             manager.undoSize(),
		             testName + " unexected initial undo size");

		/*
		 * assert initial undo does nothing on app state
		 * but creates a elt on redo stack
		 */
		List<Element<String>> initialState = new ArrayList<>(app.getState());
		manager.undo();
		assertEquals(initialState,
		             app.getState(),
		             testName + " unexpected app state after undo on empty undo stack");
		assertEquals(1,
		             manager.redoSize(),
		             testName + " unexpected redo size after undo on empty undo stack");

		/*
		 * recreates manager to initial state
		 */
		manager = new HistoryManager<>(app, historySize, parentLogger);

		/*
		 * fill undo stack
		 */
		Stack<List<Element<String>>> expectedStates = new Stack<>();
		for (Element<String> element : secondaryList)
		{
			manager.record();
			expectedStates.push(new ArrayList<>(app.getState()));
			app.add(element);
		}

		/*
		 * assert undo size cant exceed #historySize
		 */
		assertEquals(historySize,
		             manager.undoSize(),
		             testName + " unexpected undo size");
		/*
		 * assert expectedStates has the right size
		 */
		assertEquals(secondarySize,
		             expectedStates.size(),
		             testName + " unexpected pushed states size");
		/*
		 * assert app is in a new state
		 */
		assertNotEquals(expectedStates.peek(),
		                app.getState(),
		                testName + " unexpected app unchanged state");

		/*
		 * Then check undo(ed) states
		 */
		for (int i = 0; i < secondarySize; i++)
		{
			List<Element<String>> expectedState = expectedStates.pop();
			manager.undo();
			List<Element<String>> providedState = new ArrayList<>(app.getState());
			if (i < historySize)
			{
				/*
				 * assert app state matches the expected state
				 */
				assertEquals(expectedState,
				             providedState,
				             testName + " unexpected different provided state at step " + i);
				/*
				 * assert undo size decreases
				 */
				assertEquals(historySize - i - 1,
				             manager.undoSize(),
				             testName + " unexpected undo size after undo");
				/*
				 * assert redo size increases
				 */
				assertEquals(i + 1,
				             manager.redoSize(),
				             testName + " unexpected redo size after undo");
			}
			else
			{
				/*
				 * assert provided state doesn't match expected one anymore
				 */
				assertNotEquals(expectedState,
				                providedState,
				                testName + " unexpected same provided state at step " + i);
				/*
				 * assert undo size remains 0
				 */
				assertEquals(0,
				             manager.undoSize(),
				             testName + " unexpected undo size after emptying undo stack");

				/*
				 * assert redo size remains historySize
				 */
				assertEquals(historySize,
				             manager.redoSize(),
				             testName + " unexpected redo size after emptying undo stack");
			}
		}

		/*
		 * assert ultimate undo doesn't change app state
		 * but creates a new redo (redo stack is already full and won't change
		 * size)
		 */
		List<Element<String>> ultimateState = new ArrayList<>(app.getState());
		manager.undo();
		assertEquals(ultimateState,
		             app.getState(),
		             testName + " unexpected app state after ultimate undo");
		assertEquals(historySize,
		             manager.redoSize(),
		             testName + " unexpected redo size after ultimate undo");
	}

	/**
	 * Test method for {@link history.HistoryManager#cancel()}.
	 */
	@Test
	@DisplayName("cancel()")
	@Order(8)
	public final void testCancel()
	{
		System.out.println(testName);

		assertNotNull(manager, testName + " unexpected null history manager");

		/*
		 * assert initial undo size is 0
		 */
		assertEquals(0,
		             manager.undoSize(),
		             testName + " unexpected initial undo size");

		int expectedUndoSize = 0;
		int expectedRedoSize = 0;
		/*
		 * multiple record / cancel sequence
		 */
		for (Iterator<Element<String>> it = secondaryList.iterator(); it.hasNext();)
		{
			manager.record();
			app.add(it.next());
			List<Element<String>> expectedState = new ArrayList<>(app.getState());
			manager.cancel();
			assertEquals(expectedUndoSize,
			             manager.undoSize(),
			             testName + " unexpected undo size after cancel");
			assertEquals(expectedRedoSize,
			             manager.redoSize(),
			             testName + " unexpected redo size after cancel");
			assertEquals(expectedState,
			             app.getState(),
			             testName + " unexpected app state after cancel");
		}

		/*
		 * multiple records
		 */
		var it = secondaryList.iterator();
		while (manager.undoSize() < historySize)
		{
			manager.record();
			app.add(it.next());
			assertEquals(++expectedUndoSize,
			             manager.undoSize(),
			             testName + " unexpected undo size while recording");
		}

		/*
		 * multiple cancels
		 */
		while (manager.undoSize() > 0)
		{
			manager.cancel();
			assertEquals(--expectedUndoSize,
			             manager.undoSize(),
			             testName + " unexpected undo size after cancelling");
		}

		/*
		 * ultimate cancel on empty undo does nothing
		 */
		List<Element<String>> state = new ArrayList<>(app.getState());
		manager.cancel();
		assertEquals(state,
		             app.getState(),
		             testName + " unexpected new app state after cancel on empty "
		             	+ "undo stack");
	}

	/**
	 * Test method for {@link history.HistoryManager#redo()}.
	 */
	@Test
	@DisplayName("redo()")
	@Order(9)
	public final void testRedo()
	{
		System.out.println(testName);

		assertNotNull(manager, testName + " unexpected null history manager");

		/*
		 * initial redo does nothing on app state
		 * but creates an undo state
		 */
		List<Element<String>> initialState = new ArrayList<>(app.getState());
		manager.redo();
		assertEquals(initialState,
		             app.getState(),
		             testName + " unexpected state after initial redo");
		assertEquals(1,
		             manager.undoSize(),
		             testName + " unexpected undo state after initial redo");

		/*
		 * fill undo stack with records
		 * Note: the first record of the loop should trigger a warning message
		 * indicating same content
		 */
		int count = 0;
		LogHandler handler = new LogHandler(1);
		parentLogger.addHandler(handler);
		for (Element<String> element : secondaryList)
		{
			manager.record();
			if (count == 0)
			{
				String commonPart = " after recording the same state";
				assertFalse(handler.isEmpty(),
				            testName + " unexpected empty log message handler" + commonPart);
				assertEquals(Level.WARNING,
				             handler.pop().getLevel(),
				             testName + " unexpected log message level" + commonPart);
				parentLogger.removeHandler(handler);
			}
			app.add(element);
			count++;
		}

		/*
		 * then fill up redo stack with undos
		 */
		Stack<List<Element<String>>> expectedStates = new Stack<>();
		int expectedRedoSize = 0;
		while (manager.undoSize() > 0)
		{
			expectedStates.push(new ArrayList<>(app.getState()));
			manager.undo();
			assertEquals(++expectedRedoSize,
			             manager.redoSize());
		}

		/*
		 * assert expected redo size
		 */
		assertEquals(historySize,
		             manager.redoSize(),
		             testName + " unexpected redo size after emptying undo stack");
		/*
		 * assert app is in a new state
		 */
		assertNotEquals(expectedStates.peek(),
		                app.getState(),
		                testName + " unexpected app unchanged state");

		/*
		 * then check redo(ed) states
		 */
		int expectedUndoSize = 0;
		while (manager.redoSize() > 0)
		{
			List<Element<String>> expectedState = expectedStates.pop();
			manager.redo();
			List<Element<String>> providedState = new ArrayList<>(app.getState());

			/*
			 * assert app state matches the expected state
			 */
			assertEquals(expectedState,
			             providedState,
			             testName + " unexpected different provided state after redo");
			/*
			 * assert redo size decreases
			 */
			assertEquals(--expectedRedoSize,
			             manager.redoSize(),
			             testName + " unexpected redo size after redo");

			/*
			 * assert undo size increases
			 */
			assertEquals(++expectedUndoSize,
			             manager.undoSize(),
			             testName + " unexpected undo size after redo");
		}
		/*
		 * assert ultimate redo doesn't change app state
		 * but creates a new undo state (undo stack is already full)
		 */
		List<Element<String>> ultimateState = new ArrayList<>(app.getState());
		manager.redo();
		assertEquals(ultimateState,
		             app.getState(),
		             testName + " unexpected app state after redo on empty redo stack");
		assertEquals(historySize,
		             manager.undoSize(),
		             testName + " unexpected undo size after ultimate redo");
	}

	/**
	 * Test method for {@link history.HistoryManager#toString()}.
	 */
	@Test
	@DisplayName("toString()")
	@Order(10)
	public final void testToString()
	{
		System.out.println(testName);

		assertNotNull(manager, testName + " unexpected null history manager");
		/*
		 * expected String e.g. [<size>] :\nUndo{<undo stack content>},\nRedo{<redo stack content>}
		 * can be divided into 8 components
		 * 	- 0: super.toString() <==> Object.toString()
		 * 	- 1: "["
		 * 	- 2: <size>
		 * 	- 3: "] :\nUndo = {"
		 * 	- 4: <undo stack content>
		 * 	- 5: "},\nRedo = {"
		 * 	- 6: <redo stack content>
		 * 	- 7: "}"
		 */
		String[] components = new String[8];
		// Object.toString();
		components[0] = HistoryManager.class.getName() + "@" + Integer.toHexString(manager.hashCode());
		components[1] = "[";
		components[2] = String.valueOf(historySize);
		components[3] = "] :\nUndo = {";
		components[4] = "";
		components[5] = "},\nRedo = {";
		components[6] = "";
		components[7] = "}";
		String expectedString = mergeComponents(components);

		/*
		 * assert initial string
		 */
		assertEquals(expectedString,
		             manager.toString(),
		             testName + " unexpected initial toString");

		/*
		 * change size
		 */
		int newHistorySize = 3;
		components[2] = String.valueOf(newHistorySize);
		expectedString = mergeComponents(components);
		manager.setSize(newHistorySize);
		assertEquals(expectedString,
		             manager.toString(),
		             testName + " unexpected initial toString");

		/*
		 * fill undo stack with records
		 */
		List<String> undoContents = new ArrayList<>();
		for (int i = 0; i < newHistorySize; i++)
		{
			manager.record();
			String undoContent = commaSeparatedString(app.getState());
			undoContents.add(undoContent);
			if (i > 0)
			{
				components[4] = '[' + undoContent + "], " + components[4];
			}
			else
			{
				components[4] = '[' + undoContent + ']';
			}
			assertEquals(mergeComponents(components),
			             manager.toString(),
			             testName + " unexpected toString after record[" + i + ']');
			app.add(secondaryList.get(i));
		}



		/*
		 * fill redo stack with undos
		 */
		for (int i = 0; i < newHistorySize; i++)
		{

			/*
			 * Redo content
			 */
			String redoContent = commaSeparatedString(app.getState());
			manager.undo();
			if (i > 0)
			{
				components[6] = '[' + redoContent + "], " + components[6];
			}
			else
			{
				components[6] = '[' + redoContent + ']';
			}

			/*
			 * Undo content
			 */
			undoContents.remove(undoContents.size() - 1);
			Stack<String> undoContentStack = new Stack<>();
			for (String s : undoContents)
			{
				undoContentStack.push(s);
			}
			StringBuilder sb = new StringBuilder();
			while (!undoContentStack.isEmpty())
			{
				sb.append('[');
				sb.append(undoContentStack.pop());
				if (undoContentStack.isEmpty())
				{
					sb.append(']');
				}
				else
				{
					sb.append("], ");
				}
			}
			components[4] = sb.toString();

			assertEquals(mergeComponents(components),
			             manager.toString(),
			             testName + " unexpected toString after undo[" + i + ']');
		}
	}

	/**
	 * Utility method to build a comma separated String of the provided collection
	 * framed by brackets: [elt1, elt2, ..., eltn]
	 * @param <T> the type of elements in the collection
	 * @param elts the collection used to build the String
	 * @return a comma separated string of all elements in the provided collection
	 */
	private final static <T> String commaSeparatedString(Collection<T> elts)
	{
		StringBuilder sb = new StringBuilder();
		for (Iterator<T> it = elts.iterator(); it.hasNext();)
		{
			sb.append(it.next().toString());
			if (it.hasNext())
			{
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	/**
	 * Merge all string contained in provided String array into a single String
	 * @param components the array of strings to merge
	 * @return the merged String
	 */
	private final static String mergeComponents(String[] components)
	{
		StringBuilder sb = new StringBuilder();
		for (String component : components)
		{
			sb.append(component);
		}
		return sb.toString();
	}
}
