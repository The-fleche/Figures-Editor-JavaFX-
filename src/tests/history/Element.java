package tests.history;

import history.Prototype;

/**
 * A dummy Element cloneable data class
 * @author davidroussel
 * @param <T> the content type of the element
 */
public class Element<T> implements Prototype<Element<T>>
{
	/**
	 * the content of the element
	 */
	private T content = null;

	/**
	 * Valued constructor
	 * @param content the name of the element
	 */
	public Element(T content)
	{
		this.content = content;
	}

	/**
	 * Copy constructor
	 * @param element the element to copy
	 */
	public Element(Element<T> element)
	{
		this(element.content);
	}

	@Override
	public Element<T> clone()
	{
		return new Element<>(this);
	}

	/**
	 * Element content accessor
	 * @return the content of the Element
	 */
	public T getContent()
	{
		return content;
	}

	/**
	 * Element content setter
	 * @param content the new content to set
	 */
	public void content(T content)
	{
		this.content = content;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		return (prime * result) + (content == null ? 0 : content.hashCode());
	}

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

		if (getClass() != obj.getClass())
		{
			return false;
		}

		Element<?> dum = (Element<?>) obj;
		return content == null ? dum.content == null : content.equals(dum.content);
	}

	@Override
	public String toString()
	{
		return content.toString();
	}
}
