package tests.history;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import history.Memento;
import history.Originator;

/**
 * A dummy App managing {@link Element}s
 * @author davidroussel
 */
public class TestApp implements Originator<Element<String>>
{
	/**
	 * The state of the app to be saved in {@link Memento}s
	 */
	private List<Element<String>> state = null;

	/**
	 * Default constructor.
	 * Creates an empty list of dummies
	 */
	public TestApp()
	{
		state = new ArrayList<>();
	}

	/**
	 * Liste size
	 * @return the size of the list
	 */
	public int size()
	{
		return state.size();
	}

	/**
	 * Checks if the provided element is part of the list
	 * @param element the element to search for
	 * @return true if the provided element is part of the list
	 */
	public boolean contains(Element<String> element)
	{
		for (Iterator<Element<String>> it = state.iterator(); it.hasNext();)
		{
			if (it.next().equals(element))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds a element to the list
	 * @param element the new element to add
	 * @throws NullPointerException if the element to add is null
	 */
	public void add(Element<String> element) throws NullPointerException
	{
		if (element == null)
		{
			throw new NullPointerException("add(null)");
		}
		state.add(element);
	}

	/**
	 * Removes the provided element (iff found)
	 * @param element the element to remove from the list
	 * @return true if the provided element was found and removed
	 */
	public boolean remove(Element<String> element)
	{
		for (Iterator<Element<String>> it = state.iterator(); it.hasNext();)
		{
			if (it.next().equals(element))
			{
				it.remove();
				return true;
			}
		}
		return false;
	}

	/**
	 * Clears list
	 */
	public void clear()
	{
		state.clear();
	}

	/**
	 * Get current state
	 * @return the current state (for comparison purposes)
	 */
	public List<Element<String>> getState()
	{
		return state;
	}

	/**
	 * Creates Memento of current state
	 * @return a memento containing the current state
	 */
	@Override
	public Memento<Element<String>> createMemento()
	{
		return new Memento<>(state);
	}

	/**
	 * Sets current state using provided memento
	 * @param memento the memento providing the currennt state to set
	 */
	@Override
	public void setMemento(Memento<Element<String>> memento)
	{
		state.clear();
		for (Element<String> element : memento.getState())
		{
			add(element);
		}
	}

	/**
	 * String representation of the object
	 * @return a ne String representing this object and showing the content
	 * of its current state as "<ClassName>: {<elt1>, ..., <eltn>}"
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName() + ": ");
		sb.append('{');
		for (Iterator<Element<String>> it = state.iterator(); it.hasNext();)
		{
			sb.append(it.next());
			if (it.hasNext())
			{
				sb.append(", ");
			}
		}
		sb.append('}');
		return sb.toString();
	}
}
