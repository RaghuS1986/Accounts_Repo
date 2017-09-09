package accounts.plugin.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import accounts.plugin.ui.dialogs.FileCreationDialog;

public class CreateAccountData implements IObjectActionDelegate {
	private IProject project;

	public void run(IAction arg0) {
		FileCreationDialog creationDialog = new FileCreationDialog(new Shell(Display.getDefault()), this.project);
		creationDialog.open();
	}

	public void selectionChanged(IAction arg0, ISelection arg1) {
		if ((arg1 instanceof IStructuredSelection)) {
			Object firstElement = ((IStructuredSelection) arg1).getFirstElement();
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (IProject iProject : projects) {
				if (iProject.equals(firstElement)) {
					this.project = iProject;
					break;
				}
			}
		}
	}

	public void setActivePart(IAction arg0, IWorkbenchPart arg1) {
	}
}
