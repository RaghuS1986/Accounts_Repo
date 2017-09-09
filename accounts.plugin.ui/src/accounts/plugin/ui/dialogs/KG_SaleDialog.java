package accounts.plugin.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class KG_SaleDialog extends Dialog {

	Text kgField = null;
	Text counter = null;
	private int finalCount = 0;
	private boolean shellClosed = false;

	public KG_SaleDialog(Shell parentShell) {
		super(parentShell);
	}

	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		Label lbl = new Label(container, 0);
		lbl.setText("Count");
		counter = new Text(container, SWT.BORDER);
		counter.setEditable(false);

		lbl = new Label(container, 0);
		lbl.setLayoutData(new GridData(4, 1024, true, false, 3, 1));
		lbl.setText("Enter KG's");

		this.kgField = new Text(container, 2624);
		GridData gd = new GridData(4, 4, false, false, 2, 2);
		gd.widthHint = 100;
		gd.minimumHeight = 50;
		gd.heightHint = 50;
		gd.grabExcessVerticalSpace = true;
		this.kgField.setLayoutData(gd);
		kgField.setFocus();
		kgField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				if (kgField != null && kgField.getText().length() > 0) {
					counter.setText(kgField.getText().split(",").length + "");
				}
			}
		});
		return container;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Enter KG's");
	}

	protected void okPressed() {
		if ((this.kgField.getText() != null) && (this.kgField.getText().length() > 0)) {
			String[] split = this.kgField.getText().split(",");
			for (String string : split) {
				int parseInt = accounts.plugin.ui.Utility.parseInt(string);
				this.finalCount += parseInt;
			}
		}
		super.okPressed();
	}

	protected Point getInitialSize() {
		return new Point(450, 391);
	}

	public int getTotalKG() {
		return this.finalCount;
	}

	public boolean isShellClosed() {
		return this.shellClosed;
	}

	protected void handleShellCloseEvent() {
		this.shellClosed = true;
		super.handleShellCloseEvent();
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, 0, IDialogConstants.OK_LABEL, true);
	}
}
