package accounts.plugin.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import accounts.plugin.model.classes.Member;
import accounts.plugin.model.classes.Year;
import accounts.plugin.ui.editors.EditorInterface;

public class AddYearAction extends Action {

	private TreeViewer treeViewer;
	  private EditorInterface editorIf;
	  
	  public AddYearAction(TreeViewer treeViewer, EditorInterface editorPart)
	  {
	    this.treeViewer = treeViewer;
	    this.editorIf = editorPart;
	  }
	  
	  public void run()
	  {
	    IStructuredSelection selection = (IStructuredSelection)this.treeViewer.getSelection();
	    
	    TreeSelection sel = (TreeSelection) this.treeViewer.getSelection();
		TreePath[] paths = sel.getPaths();
	    Object firstElement = selection.getFirstElement();
	    if (firstElement instanceof Member)
	    {
	      InputDialog dialog = new InputDialog(Display.getCurrent().getActiveShell(), "Add Year", 
	        "Enter the financial year name", "", null);
	      if ((dialog.open() == 0) && (dialog.getValue().trim().length() > 0)) {
	        if ((firstElement instanceof Member)) {
	          ((Member)firstElement).getYears().add(new Year(dialog.getValue()));
	        }
	      }
	    }
	    this.editorIf.setInputToView();
	    treeViewer.expandToLevel(paths[0], 1);
	  }
	}
