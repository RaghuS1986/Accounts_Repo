package accounts.plugin.ui.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;

import accounts.plugin.model.classes.Accounts;
import accounts.plugin.model.classes.Date;
import accounts.plugin.model.classes.ItemBought;
import accounts.plugin.model.classes.Member;
import accounts.plugin.model.classes.ModelManager;
import accounts.plugin.model.classes.Month;
import accounts.plugin.model.patti.Patti;
import accounts.plugin.ui.editors.EditorInterface;

public class RemoveItemsBought extends Action {
	private TreeViewer treeViewer;
	private EditorInterface editorIf;
	private boolean isPatti = false;

	public RemoveItemsBought(TreeViewer treeViewer, EditorInterface editorIf, boolean isPatti) {
		this.treeViewer = treeViewer;
		this.editorIf = editorIf;
		this.isPatti = isPatti;
	}

	public void run() {
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		Object firstElement = selection.getFirstElement();
		TreeSelection sel = (TreeSelection) this.treeViewer.getSelection();
		TreePath[] paths = sel.getPaths();
		if (firstElement instanceof ItemBought) {
			boolean delete = MessageDialog.openConfirm(treeViewer.getControl().getShell(), "Delete Item Bought",
					"Do you want to remove the Item bought?");
			if (delete) {

				Accounts model = ModelManager.getInstance().getModel();
				Patti patti = ModelManager.getInstance().getPattiModel();
				List<Member> members = null;
				if (this.isPatti) {
					members = patti.getMembers();
				} else {
					members = model.getMembers();
				}
				for (Member mem : members) {
					for (Month mon : mem.getMonths()) {
						for (Date date : mon.getDates()) {
							Iterator<ItemBought> iterator = date.getItemsBought().iterator();
							while (iterator.hasNext()) {
								ItemBought itemBought = (ItemBought) iterator.next();
								if (firstElement.equals(itemBought)) {
									ModelManager.getInstance().getMapOfItemsBt().get(mem).remove(itemBought);
									iterator.remove();
									break;
								}
							}
						}
					}
				}
			}
		}
		editorIf.setInputToView();
		treeViewer.expandToLevel(paths[0], 1);
	}
}
