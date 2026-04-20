/**
 * Package containing all classes required to manage Undos / Redos using the
 * Memento Design Pattern.
 * <ul>
 * 	<li>{@link history.HistoryManager} manages memento recordings and settings</li>
 * 	<li>{@link history.Memento} represents the state of data model to save and / or restore</li>
 * 	<li>{@link history.Originator} represents the interface implemented by data model to save and restore {@link history.Memento}s</li>
 * </ul>
 * @author davidroussel
 */
package history;
