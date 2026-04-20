package figures.enums;

import java.util.ArrayList;
import java.util.Collection;

import javafx.scene.shape.Shape;

/**
 * The type of operation (intersection, subtraction or union) between two
 * {@link Shape}s
 * @author davidroussel
 */
public enum OperationType
{
	/**
	 * Intersection between two shapes
	 * @see Shape#intersect(Shape, Shape)
	 */
	INTERSECTION,
	/**
	 * Subtraction between two shapes
	 * @see Shape#subtract(Shape, Shape)
	 */
	SUBTRACTION,
	/**
	 * Union between two shapes
	 */
	UNION;

	/**
	 * Possible number of operation types
	 */
	public static final int nbOperationTypes = 3;

	/*
	 * Note: Enum.class provides default final hashCode and equals methods,
	 * so it's no use trying to overload them
	 */

	/**
	 * Convertion from integer index
	 * @param i the index
	 * @return the corresponding enum value.
	 * If i is outside of the range [0..nbOperationTypes[,
	 * {@link #UNION} is returned
	 */
	public static OperationType fromInteger(int i)
	{
		switch (i)
		{
			case 0:
				return INTERSECTION;
			case 1:
				return SUBTRACTION;
			case 2:
			default:
				return UNION;
		}
	}

	/**
	 * Conversion to integer
	 * @return the index of this enum value
	 * @throws AssertionError if this enum has unexpected value
	 */
	public int intValue() throws AssertionError
	{
		switch (this)
		{
			case INTERSECTION:
				return 0;
			case SUBTRACTION:
				return 1;
			case UNION:
				return 2;
		}

		throw new AssertionError("OperationType unknown assertion: " + this);
	}

	/**
	 * String representation of this enum
	 * @return a new String representing this value
	 * @throws AssertionError if this enum has unexpected value
	 */
	@Override
	public String toString() throws AssertionError
	{
		switch (this)
		{
			case INTERSECTION:
				return new String(" ∩ ");
			case SUBTRACTION:
				return new String(" - ");
			case UNION:
				return new String(" ∪ ");
		}

		throw new AssertionError("OperationType Unknown assertion " + this);
	}

	/**
	 * Array of all strings containing types names
	 * @return an array of strings containing all types names
	 * @apiNote To use in Combobox creation
	 */
	public static String[] stringValues()
	{
		OperationType[] values = OperationType.values();
		String[] stringValues = new String[values.length];
		for (int i = 0; i < values.length; i++)
		{
			stringValues[i] = values[i].toString();
		}

		return stringValues;
	}

	/**
	 * Creates a collection of all possible Line types.
	 * Usefull to fill a {@link javafx.scene.control.ComboBox}
	 * Can be used to fill a {@link javafx.scene.control.ComboBox}<OperationType>
	 * @return a collection of all possible Line types
	 * @see application.Controller#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	public static Collection<OperationType> all()
	{
		Collection<OperationType> list = new ArrayList<>();
		list.add(INTERSECTION);
		list.add(SUBTRACTION);
		list.add(UNION);
		return list;
	}

	/**
	 * Create the composed shape by applying the corresponding opreation on
	 * the two provided shape
	 * @param s1 the first shape receiving the operation
	 * @param s2 the second shape applying the operation
	 * @return a new shape resulting from the operation
	 */
	public Shape operate(Shape s1, Shape s2)
	{
		switch (this)
		{
			case INTERSECTION:
				return Shape.intersect(s1, s2);
			case SUBTRACTION:
				return Shape.subtract(s1, s2);
			case UNION:
				return Shape.union(s1, s2);
		}
		throw new AssertionError("OperationType Unknown assertion " + this);
	}
}
