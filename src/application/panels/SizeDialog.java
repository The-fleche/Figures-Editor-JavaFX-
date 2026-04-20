package application.panels;

import java.io.IOException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.Callback;

/**
 * A simple {@link Dialog} providing a {@link Number} values for history size,
 * with {@link DialogPane} UI defined in external SizeDialogPane.fxml.
 * Can be used to change {@link history.HistoryManager} history size.
 * @param <T> The type of {@link Number} managed by this size dialog
 * @author davidroussel
 * Code Example:
 * {@code
 * 	Dialog<Integer> sizeDialog = new SizeDialog<Integer>(...);
 * 	Optional<Integer> result = sizeDialog.showAndWait();
 * 	if (result.isPresent())
 * 	{
 * 		...
 *  }
 * }
 */
public class SizeDialog<T extends Number> extends Dialog<T>
{

	/**
	 * The value Property that will be bound to UI spinner and used to return
	 * the value provided by this dialog.
	 */
	private ObjectProperty<T> valueProperty;

	/**
	 * Spinner holding the values
	 */
	@FXML
	private Spinner<T> valueSpinner;

	/**
	 * Valued constructor
	 * @param value the current value to set in {@link #valueSpinner}
	 * @param minValue  the minimum value to set in the {@link #valueSpinner}
	 * @param maxValue the maximum value to set in the {@link #valueSpinner}
	 * @param stepValue the step value to set in the {@link #valueSpinner}
	 * @param title the title to set on this dialog
	 */
	@SuppressWarnings("unchecked") // Because of SpinnerValueFactory<T> casts
	public SizeDialog(T value, T minValue, T maxValue, T stepValue, String title)
	{
		super();
		valueProperty = new SimpleObjectProperty<T>(value);
		setTitle(title);
		DialogPane pane = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SizeDialogPane.fxml"));
        loader.setController(this);

        try
		{
            pane = loader.<DialogPane>load();
        }
		catch (IOException exc)
		{
        	System.err.println("Can't load FXML file " + exc.getMessage());
			System.exit(exc.hashCode());
        }

		setDialogPane(pane);

		T pValue = valueProperty.get();
		if (pValue instanceof Integer)
		{
			valueSpinner.setValueFactory((SpinnerValueFactory<T>)
				new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue.intValue(),
				                                                   maxValue.intValue(),
				                                                   value.intValue(),
				                                                   stepValue.intValue()));
		}
		else if (pValue instanceof Double)
		{
			valueSpinner.setValueFactory((SpinnerValueFactory<T>)
				new SpinnerValueFactory.DoubleSpinnerValueFactory(minValue.doubleValue(),
				                                                  maxValue.doubleValue(),
				                                                  value.doubleValue(),
				                                                  stepValue.doubleValue()));
		}
		else
		{
			throw new ClassCastException("Unmanaged type "
			    + pValue.getClass().getSimpleName());
		}
		valueProperty.bind(valueSpinner.valueProperty());

		/*
		 * Set result converter to return #valueProperty's value if
		 * ButtonType.OK is pressed and null otherwise
		 */
		setResultConverter(new Callback<ButtonType, T>()
		{
			@Override
			public T call(ButtonType param)
			{
				if (param == ButtonType.OK)
				{
					return valueProperty.get();
				}
				else
				{
					return null;
				}
			}
		});
	}
}
