package accounts.plugin.ui.dialogs;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import accounts.plugin.model.classes.ModelManager;

public class FileCreationDialog extends Dialog {
	private IProject iProject;
	private Text txt;
	private Button accDataButton;

	public FileCreationDialog(Shell parent, IProject project) {
		super(parent);
		iProject = project;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label label = new Label(composite, SWT.NONE);
		label.setText("Create a data file ");
		txt = new Text(composite, SWT.BORDER);
		txt.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		txt.setToolTipText(
				"Enter a name or a name with path. Ex: just entering xyz willl create a file under the project. Just entering abc\\xyz will create a folder abc under project and create a file xyz under abc");
		txt.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				if (txt.getText() != null && txt.getText().trim().length() > 0) {
					getOKButton().setEnabled(true);
				} else {
					getOKButton().setEnabled(false);
				}

			}
		});
		accDataButton = new Button(composite, 16);
		accDataButton.setLayoutData(new GridData(16384, 16777216, false, false, 1, 1));
		accDataButton.setText("Accounts Data");
		Button button = new Button(composite, 16);
		button.setLayoutData(new GridData(16384, 16777216, false, false, 1, 1));
		button.setText("Patti Data");
		return parent;
	}

	@Override
	protected void okPressed() {
		try {
			if (txt.getText() != null && txt.getText().trim().length() > 0) {
				String fileExtn = this.accDataButton.getSelection() ? ".data" : ".patti";
				File file = new File(this.iProject.getLocation().toOSString() + "\\" + this.txt.getText() + fileExtn);
				if (file.exists()) {
					if (MessageDialog.open(MessageDialog.QUESTION_WITH_CANCEL, new Shell(Display.getDefault()),
							"File override", "Do you want to override file @" + file.getPath(), SWT.NONE)) {
						if (accDataButton.getSelection()) {
							ModelManager.getInstance().createInitialModelFile(file);
						} else {
							ModelManager.getInstance().createInitialPattiModelFile(file);
						}

					}
				} else {
					new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\"))).mkdirs();
					if (accDataButton.getSelection()) {
						ModelManager.getInstance().createInitialModelFile(file);
					} else {
						ModelManager.getInstance().createInitialPattiModelFile(file);
					}
				}
			}
			iProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.okPressed();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("New Data file");
	}

}
