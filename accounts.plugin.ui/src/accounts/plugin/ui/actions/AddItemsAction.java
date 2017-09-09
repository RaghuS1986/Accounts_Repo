package accounts.plugin.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import accounts.plugin.model.classes.Date;
import accounts.plugin.model.classes.ItemBought;
import accounts.plugin.ui.editors.EditorInterface;

public class AddItemsAction extends Action {
	private TreeViewer treeViewer;
	private EditorInterface editorIf;

	public AddItemsAction(TreeViewer treeViewer, EditorInterface editorIf) {
		this.treeViewer = treeViewer;
		this.editorIf = editorIf;
	}

	public void run() {
		IStructuredSelection selection = (IStructuredSelection) this.treeViewer.getSelection();
		TreeSelection sel = (TreeSelection) this.treeViewer.getSelection();
		TreePath[] paths = sel.getPaths();
		Object firstElement = selection.getFirstElement();
		if ((firstElement instanceof Date)) {
			InputDialog dialog = new InputDialog(Display.getCurrent().getActiveShell(), "Add Item",
					"Enter the name of the item", "", null);
			if ((dialog.open() == 0) && (dialog.getValue().trim().length() > 0)) {
				((Date) firstElement).getItemsBought().add(new ItemBought(dialog.getValue()));
			}
		}
		this.editorIf.setInputToView();
		treeViewer.expandToLevel(paths[0], 1);
	}
}
