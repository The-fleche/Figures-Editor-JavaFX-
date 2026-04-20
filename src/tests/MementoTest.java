package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

import history.Memento;
import tests.history.Element;
import tests.history.TestApp;

/**
 * {@link Memento} Test Class
 * @author davidroussel
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Memento<E>")
class MementoTest
{
	/**
	 * App to create and set {@link Memento}s
	 */
	private static TestApp app = null;

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
	 * Name of the class under test
	 */
	private final static String className = Memento.class.getSimpleName();

	/**
	 * Name of the current test
	 */
	private String testName;

	/**
	 * The list of dummies expected in Memento state
	 */
	private List<Element<String>> expectedList;

	/**
	 * List of complementary dummies to add in {@link #app}
	 */
	private List<Element<String>> complementaryList;

	/**
	 * Setup before all tests
	 */
	@BeforeAll
	static void setUpBeforeClass()
	{
		app = new TestApp();
		System.out.println("-------------------------------------------------");
		System.out.println("Start of Memento Tests");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Tear down after all tests
	 */
	@AfterAll
	static void tearDownAfterClass()
	{
		app = null;
		System.out.println("-------------------------------------------------");
		System.out.println("End of Memento Tests");
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
		expectedList = new ArrayList<>();
		complementaryList = new ArrayList<>();
		int i = 0;
		for (; i < (names.length / 2); i++)
		{
			Element<String> element = new Element<>(names[i]);
			app.add(element);
			expectedList.add(element);
		}
		for (; i < names.length; i++)
		{
			complementaryList.add(new Element<>(names[i]));
		}
	}

	/**
	 * Teardown after each test
	 */
	@AfterEach
	void tearDown()
	{
		app.clear();
		expectedList.clear();
		expectedList = null;
		complementaryList.clear();
		complementaryList = null;
	}

	/**
	 * Test method for {@link history.Memento#hashCode()}.
	 */
	@Test
	@DisplayName("hashCode()")
	@Order(4)
	public final void testHashCode()
	{
		System.out.println(testName);

		Memento<Element<String>> memento = app.createMemento();
		assertNotNull(memento, testName + " unexpected null memento");
		int providedHash = memento.hashCode();
		int expectedHash = 1;
		final int prime = 31;
		List<Element<String>> state = memento.getState();
		for (Element<String> element : state)
		{
			expectedHash += (prime * expectedHash) + element.hashCode();
		}
		assertEquals(expectedHash,
		             providedHash,
		             testName + " unexpected hash");

		Memento<Element<String>> otherMemento = app.createMemento();
		assertEquals(memento,
		             otherMemento,
		             testName + " unexpected inequality between mementos");
		assertEquals(memento.hashCode(),
		             otherMemento.hashCode(),
		             testName + " unexpected hashCode difference between mementos");

		app.add(complementaryList.get(0));
		otherMemento = app.createMemento();
		assertNotEquals(memento,
		                otherMemento,
		                testName + " unexpected equality between mementos");
		assertNotEquals(memento.hashCode(),
		                otherMemento.hashCode(),
		                testName + " unexpected hashCode equality between mementos");
	}

	/**
	 * Test method for {@link history.Memento#Memento(java.util.List)}.
	 */
	@Test
	@DisplayName("Memento(List<E>)")
	@Order(1)
	public final void testMemento()
	{
		System.out.println(testName);

		@SuppressWarnings("rawtypes")
		Constructor<Memento> constructor = null;
		Class<?>[] constructorsArgs = new Class<?>[1];
		constructorsArgs[0] = List.class;

		try
		{
			constructor = Memento.class.getConstructor(constructorsArgs);
		}
		catch (NoSuchMethodException e)
		{
			fail(testName + " copy constructor not found");
		}
		catch (SecurityException e)
		{
			fail(testName + " copy constructor security exception");
		}

		if (constructor != null)
		{
			Object instance = null;
			Object[] args = new Object[1];
			args[0] = expectedList;
			try
			{
				instance = constructor.newInstance(args);
			}
			catch (InstantiationException e)
			{
				fail(testName + " copy instanciation exception : Abstract class");
			}
			catch (IllegalAccessException e)
			{
				fail(testName + " copy constructor is inaccessible");
			}
			catch (IllegalArgumentException e)
			{
				fail(testName + " copy constructor illegal argument");
			}
			catch (InvocationTargetException e)
			{
				fail(testName + " invoked copy constructor throwed an exception");
			}
			catch (Exception e)
			{
				fail(testName + " setup failed");
			}

			assertNotNull(instance, testName + " created instance is null");

			if (!(instance instanceof Memento<?>))
			{
				fail(testName + " constructed instance is not a " + className);
			}

			Memento<?> memento  = (Memento<?>) instance;
			try
			{
				List<?> elements = memento.getState();
				assertNotNull(elements,
				              testName + " unexpected null state");
				assertNotSame(expectedList,
				              elements,
				              testName + " expected list and memento list are the same");
				assertEquals(expectedList,
				             elements,
				             testName + " unexpected Memento state content");
			}
			catch (ClassCastException e)
			{
				fail(testName + " unexpected class cast exception when "
					+ "retrieving Memento state");
			}
		}
		else
		{
			fail(testName + " null constructor");
		}
	}

	/**
	 * Test method for {@link history.Memento#getState()}.
	 */
	@Test
	@DisplayName("getState()")
	@Order(2)
	public final void testGetState()
	{
		System.out.println(testName);

		Memento<Element<String>> memento = app.createMemento();
		assertNotNull(memento, testName + " unexpected null memento");
		List<Element<String>> state = memento.getState();
		assertNotNull(state, testName + " unexpected null memento state");

		/*
		 * Memento state content should match #expectedList
		 */
		assertEquals(expectedList,
		             state,
		             testName + " state content inequality with expected");

		/*
		 * Memento's state should contains distinct elements from
		 * #expectedList with the same values in the same order.
		 */
		Iterator<Element<String>> expectedIt = expectedList.iterator();
		Iterator<Element<String>> providedIt = state.iterator();
		while (expectedIt.hasNext() && providedIt.hasNext())
		{
			Element<String> expectedElement = expectedIt.next();
			Element<String> providedElement = providedIt.next();
			assertNotSame(expectedElement,
			              providedElement,
			              testName + " expected and provided dummies are the same");
			assertEquals(expectedElement,
			             providedElement,
			             testName + " expected and provided dummies are not equal");
		}
		assertEquals(expectedIt.hasNext(),
		             providedIt.hasNext(),
		             testName + " expected list and state list don't have the same size");

		app.clear();
		memento = app.createMemento();
		state = memento.getState();
		assertTrue(state.isEmpty(),
		           testName + " unexpected non empty state");
		assertNotEquals(expectedList,
		                state,
		                testName + " unexpected equality between list and empty memento");
	}

	/**
	 * Test method for {@link history.Memento#equals(java.lang.Object)}.
	 */
	@Test
	@DisplayName("equals(Object)")
	@Order(3)
	public final void testEqualsObject()
	{
		System.out.println(testName);

		Memento<Element<String>> memento1 = app.createMemento();
		assertNotNull(memento1, testName + " unexpected null memento");

		/*
		 * memento1 is never equal to null
		 */
		boolean comparison;
		long start = System.nanoTime();
		comparison = memento1.equals(null);
		long duration1 = System.nanoTime() - start;
		assertFalse(comparison,
		            testName +  " unexpected equality with null");

		/*
		 * memento1 is equal to itself
		 */
		assertTrue(memento1.equals(memento1),
		           testName + " unexpected inequality with self");

		/*
		 * memento1 is not equal to a foreign object
		 */
		assertFalse(memento1.equals(new Object()),
		            testName + " unexpected equality with Object");

		Memento<Element<String>> memento2 = app.createMemento();
		assertNotNull(memento2, testName + " unexpected null memento");

		/*
		 * memento1 & memento2 should have same content with distinct instances
		 */
		start = System.nanoTime();
		comparison = memento1.equals(memento2);
		long duration2 = System.nanoTime() - start;
		assertEquals(memento1,
		             memento2,
		             testName + " unexpected inequality between mementos");
		assertTrue(comparison,
		           testName + " unexepcted mementos inequality");
		assertTrue(duration1 < duration2,
		           testName + " comparison with self was not shorter that "
		           	+ "comparison with equal: check \"if (this == obj)\" case in "
		           	+ "equals method");

		Iterator<Element<String>> it1 = memento1.getState().iterator();
		Iterator<Element<String>> it2 = memento2.getState().iterator();
		while (it1.hasNext() && it2.hasNext())
		{
			Element<String> element1 = it1.next();
			Element<String> element2 = it2.next();
			assertNotSame(element1,
			              element2,
			              testName + " unexpected same content in distinct mementos");
			assertEquals(element1,
			             element2,
			             testName + " unexpected different values in mementos content");
		}

		Element<String> extraElement = complementaryList.get(0);
		app.add(extraElement);
		memento2 = app.createMemento();
		assertNotNull(memento2, testName + " unexpected null memento");

		/*
		 * memento1 & memento2 should be different now
		 */
		assertNotEquals(memento1,
		                memento2,
		                testName + " unexpected equality between different mementos");
		/*
		 * memento1 & memento2 should be identical again
		 */
		app.remove(extraElement);
		memento2 = app.createMemento();
		assertNotNull(memento2, testName + " unexpected null memento");
		assertEquals(memento1,
		             memento2,
		             testName + " unexpected inequality between mementos");

		/*
		 * memento1 & memento2 should be different again
		 */
		app.clear();
		memento2 = app.createMemento();
		assertNotNull(memento2, testName + " unexpected null memento");
		assertNotEquals(memento1,
		                memento2,
		                testName + " unexpected equality empty memento");

		/*
		 * memento1 & memento2 should be finally empty and identical
		 */
		memento1 = app.createMemento();
		assertNotNull(memento1, testName + " unexpected null memento");
		assertEquals(memento1,
		             memento2,
		             testName + " unexpected inequality between empty mementos");
	}

	/**
	 * Test method for {@link history.Memento#toString()}.
	 */
	@Test
	@DisplayName("toString()")
	@Order(5)
	public final void testToString()
	{
		System.out.println(testName);

		Memento<Element<String>> memento = app.createMemento();
		List<Element<String>> state = memento.getState();

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (Iterator<Element<String>> it = state.iterator(); it.hasNext(); )
		{
			sb.append(it.next().toString());
			if (it.hasNext())
			{
				sb.append(", ");
			}
		}
		sb.append(']');
		String expected = sb.toString();
		assertEquals(expected,
		             memento.toString(),
		             testName + " unexpected String content");

		app.clear();
		memento = app.createMemento();
		assertEquals("[]",
		             memento.toString(),
		             testName + " unexpected String content");
	}
}
