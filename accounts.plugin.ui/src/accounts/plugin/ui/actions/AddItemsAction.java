package accounts.plugin.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import accounts.plugin.model.classes.Date;
import accounts.plugin.model.classes.ItemBought;
import accounts.plugin.model.classes.Member;
import accounts.plugin.model.classes.ModelManager;
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
		Member mem=null;
		for (TreePath treePath : paths) {
			if ((treePath.getSegment(1) instanceof Member)) {
				mem = (Member) treePath.getSegment(1);
				break;
			}
		}
		Object firstElement = selection.getFirstElement();
		if ((firstElement instanceof Date)) {
			InputDialog dialog = new InputDialog(Display.getCurrent().getActiveShell(), "Add Item",
					"Enter the name of the item", "", null);
			if ((dialog.open() == 0) && (dialog.getValue().trim().length() > 0)) {
				ItemBought itemBought = new ItemBought(dialog.getValue(),mem.getName());
				List<ItemBought> list =ModelManager.getInstance().getMapOfItemsBt().get(mem);
				if (list==null) {
					List<ItemBought> listOfItmsBt = new ArrayList();
					listOfItmsBt.add(itemBought);
					ModelManager.getInstance().getMapOfItemsBt().put(mem, listOfItmsBt);
				}else {
					list.add(itemBought);
				}
				((Date) firstElement).getItemsBought().add(itemBought);
			}
		}
		this.editorIf.setInputToView();
		treeViewer.expandToLevel(paths[0], 1);
	}
}
