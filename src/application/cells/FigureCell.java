package application.cells;

import figures.Figure;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Custom cell to display {@link Figure}s in a {@link javafx.scene.control.ListView}.
 * Example:
 * {@code myListView.setCellFactory(listView -> new FigureCell());}
 * @author davidroussel
 */
public class FigureCell extends CustomCell<Figure>
{
	/**
	 * Default constructor
	 * Loads FXML file FigureCell.fxml to layout the cell and binds controller
	 * @implSpec The controller specified in FigureCell.fxml is a {@link FigureCellController}
	 */
	public FigureCell()
	{
		/*
		 * Instanciates
		 * - UI
		 * - The controller registered in FigureCell.fxml: FigureCellController
		 */
		super("FigureCell.fxml");
	}

	/**
	 * The cell factory for this type of cells
	 */
	private static Callback<ListView<Figure>, ListCell<Figure>> cellFactory = null;

	/**
	 * Get Cell factory for this type of cells
	 * @return a new Cell Factory for this type of cells
	 */
	public static Callback<ListView<Figure>, ListCell<Figure>> getCellFactory()
	{
		if (cellFactory == null)
		{
			cellFactory = new Callback<ListView<Figure>, ListCell<Figure>>()
			{
				@Override
				public ListCell<Figure> call(ListView<Figure> param)
				{
					return new FigureCell();
				}
			};
		}
		return cellFactory;
	}
}
