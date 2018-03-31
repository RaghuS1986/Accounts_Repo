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
import accounts.plugin.model.classes.Member;
import accounts.plugin.model.classes.ModelManager;
import accounts.plugin.model.classes.Month;
import accounts.plugin.model.classes.Year;
import accounts.plugin.model.patti.Patti;
import accounts.plugin.ui.editors.EditorInterface;

public class RemoveYearAction extends Action {
	private TreeViewer treeViewer;
	private EditorInterface editorIf;
	private boolean isPatti = false;

	public RemoveYearAction(TreeViewer treeViewer, EditorInterface editorIf, boolean isPatti) {
		this.treeViewer = treeViewer;
		this.editorIf = editorIf;
		this.isPatti = isPatti;
	}

	public void run() {
		IStructuredSelection selection = (IStructuredSelection) this.treeViewer.getSelection();
		TreeSelection sel = (TreeSelection) this.treeViewer.getSelection();
		TreePath[] paths = sel.getPaths();
		Object firstElement = selection.getFirstElement();
		if ((firstElement instanceof Year)) {
			boolean delete = MessageDialog.openConfirm(this.treeViewer.getControl().getShell(), "Delete Year",
					"Do you want to remove the Year?");
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
						Iterator<Year> iterator = mem.getYears().iterator();
						while (iterator.hasNext()) {
							Year yr = (Year) iterator.next();
							if (firstElement.equals(yr)) {
								iterator.remove();
								break;
						}
					}
				}
			}
		}
		this.editorIf.setInputToView();
		treeViewer.expandToLevel(paths[0], 1);
	}
}