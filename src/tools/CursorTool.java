package tools;

import java.util.logging.Logger;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * A Tool listening to {@link MouseEvent#MOUSE_MOVED} and
 * {@link MouseEvent#MOUSE_EXITED} mouse events to update cursor position in
 * a {@link Pane} using 2 {@link Label}s.
 * @author davidroussel
 */
public class CursorTool extends AbstractTool<Pane>
{
	/**
	 * Label to show {@link MouseEvent#MOUSE_MOVED} x coordinates
	 */
	private Label xLabel;
	/**
	 * Label to show {@link MouseEvent#MOUSE_MOVED} y coordinates
	 */
	private Label yLabel;

	/**
	 * Default constructor.
	 * Initialize all attributes to their default values. This requires this
	 * tool to be setup later with
	 * {@link AbstractTool#setup(javafx.scene.Node, int, boolean, boolean, Logger)}
	 */
	public CursorTool()
	{
		super();
		xLabel = null;
		yLabel = null;
	}

	/**
	 * Setup Tool's root, labels and parent logger
	 * @param root the panel where to intercept mouse events
	 * @param xLabel The X Label where to show events X coordinates
	 * @param yLabel the Y Label where to show events Y coordinates
	 * @param parentLogger the parent logger
	 */
	public void setup(Pane root, Label xLabel, Label yLabel, Logger parentLogger)
	{
		super.setup(root,			// Root node
		            (MOVED|EXITED),	// Captured events
		            true,			// Capture events as an event filter
		            true,			// Consume captured events (so they don't bubble up) --> false may also be alright
		            parentLogger);
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		if ((this.xLabel == null) || (this.yLabel == null))
		{
			logger.severe("null display labels");
		}
	}

	/**
	 * Handle mouse moved events by updating {@link #xLabel} and {@link #yLabel}
	 * with events X & Y coordinates
	 * @param event the {@link MouseEvent#MOUSE_MOVED} event to process
	 */
	@Override
	public void mouseMoved(MouseEvent event)
	{
		if (xLabel != null)
		{
			xLabel.setText(String.format("%4.0f", event.getX()));
		}
		if (yLabel != null)
		{
			yLabel.setText(String.format("%4.0f", event.getY()));
		}
	}

	/**
	 * Handle mouse exited events by clearing {@link #xLabel} and {@link #yLabel}
	 * @param event the {@link MouseEvent#MOUSE_EXITED} event to process
	 */
	@Override
	public void mouseExited(MouseEvent event)
	{
		if (xLabel != null)
		{
			xLabel.setText("");
		}
		if (yLabel != null)
		{
			yLabel.setText("");
		}
	}
}
