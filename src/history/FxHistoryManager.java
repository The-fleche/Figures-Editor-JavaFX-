package history;

import java.util.logging.Logger;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * JavaFX flavoured History Manager.
 * Behaves like a regular {@link HistoryManager} but features extra
 * properties indicating undo stack and redo stack are empty which can be
 * bound to {@link javafx.scene.Node#disabledProperty()} in the UI.
 * @author davidroussel
 * @param <E> The type of objects to store in the {@link Memento}s of this
 * history manager
 */
public class FxHistoryManager<E extends Prototype<E>> extends HistoryManager<E>
{
	/**
	 * Boolean property indicating {@link HistoryManager#undoStack} is empty
	 */
	private BooleanProperty undoIsEmpty;

	/**
	 * Boolean property indicating {@link HistoryManager#redoStack} is empty
	 */
	private BooleanProperty redoIsEmpty;

	/**
	 * Undo / Redo manager constructor
	 * @param origin the {@link Originator} to manage
	 * @param size the maximum number of states to save in {@link #undoStack} &
	 * @param parentLogger the Parent Logger to issue messages
	 * {@link #redoStack}
	 */
	public FxHistoryManager(Originator<E> origin, int size, Logger parentLogger)
	{
		super(origin, size, parentLogger);
		undoIsEmpty = new SimpleBooleanProperty(true);
		redoIsEmpty = new SimpleBooleanProperty(true);
	}

	/**
	 * {@link #undoIsEmpty} property getter
	 * @return the undoIsEmpty
	 */
	public BooleanProperty getUndoIsEmptyProperty()
	{
		return undoIsEmpty;
	}

	/**
	 * {@link #redoIsEmpty} property getter
	 * @return the redoIsEmpty
	 */
	public BooleanProperty getRedoIsEmptyProperty()
	{
		return redoIsEmpty;
	}

	/**
	 * Push a new {@link Memento} (iff non null and not similar to last pushed
	 * Memento) to the {@link #undoStack} and changes {@link #undoIsEmpty} if
	 * undo stack is no more empty.
	 * @param state the {@link Memento} to push
	 * @return true if the {@link Memento} was non null, different from the
	 * last pushed {@link Memento} and was pushed to {@link #undoStack},
	 * false otherwise.
	 * @see HistoryManager#pushUndo(Memento)
	 */
	@Override
	protected boolean pushUndo(Memento<E> state)
	{
		boolean result = super.pushUndo(state);
		if (!undoStack.isEmpty())
		{
			if (undoIsEmpty.get())
			{
				undoIsEmpty.set(false);
			}
		}
		return result;
	}

	/**
	 * Pops the last element pushed into {@link #undoStack} and changes
	 * {@link #undoIsEmpty} if undo stack is empty.
	 * @return the state on top of the {@link #undoStack} or null if there was
	 * no states in {@link #undoStack}
	 * @see HistoryManager#popUndo()
	 */
	@Override
	protected Memento<E> popUndo()
	{
		Memento<E> result = super.popUndo();
		if (undoStack.isEmpty())
		{
			if (!undoIsEmpty.get())
			{
				undoIsEmpty.set(true);
			}
		}
		return result;
	}

	/**
	 * Push the provided state into {@link #redoStack} iff the provided state is
	 * not null and different from last pushed state. If the resulting number of
	 * states is > to {@link #size}, then the first pushed state into
	 * {@link #redoStack} is removed. Then updates {@link #redoIsEmpty}.
	 * @param state the state to push into {@link #redoStack}
	 * @return true if the provided state has been added to {@link #redoStack},
	 * false otherwise
	 * @see HistoryManager#pushRedo(Memento)
	 */
	@Override
	protected boolean pushRedo(Memento<E> state)
	{
		boolean result = super.pushRedo(state);
		if (!redoStack.isEmpty())
		{
			if (redoIsEmpty.get())
			{
				redoIsEmpty.set(false);
			}
		}
		return result;
	}

	/**
	 * Pops the last element pushed into {@link #redoStack} and changes
	 * {@link #redoIsEmpty} if redo stack is empty
	 * @return the state on top of the {@link #redoStack} or null if there was
	 * no states in {@link #redoStack}
	 * @see HistoryManager#popRedo()
	 */
	@Override
	protected Memento<E> popRedo()
	{
		Memento<E> result = super.popRedo();
		if (redoStack.isEmpty())
		{
			if (!redoIsEmpty.get())
			{
				redoIsEmpty.set(true);
			}
		}
		return result;
	}
}
