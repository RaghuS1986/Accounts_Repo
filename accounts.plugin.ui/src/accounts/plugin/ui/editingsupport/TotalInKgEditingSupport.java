package accounts.plugin.ui.editingsupport;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import accounts.plugin.model.classes.ItemBought;
import accounts.plugin.ui.dialogs.KG_PurchaseDialog;

public class TotalInKgEditingSupport extends EditingSupport {
	private DialogCellEditor dialogCellEditor;
	private KG_PurchaseDialog dlg;

	public TotalInKgEditingSupport(TreeViewer viewer) {
		super(viewer);

		this.dialogCellEditor = new DialogCellEditor(viewer.getTree()) {
			protected Object openDialogBox(Control arg0) {
				TotalInKgEditingSupport.this.dlg = new KG_PurchaseDialog(Display.getCurrent().getActiveShell());
				TotalInKgEditingSupport.this.dlg.open();
				return Integer.valueOf(TotalInKgEditingSupport.this.dlg.getTotalKG());
			}
		};
	}

	protected boolean canEdit(Object arg0) {
		if ((arg0 instanceof ItemBought)) {
			return true;
		}
		return false;
	}

	protected CellEditor getCellEditor(Object arg0) {
		return this.dialogCellEditor;
	}

	protected Object getValue(Object arg0) {
		String percentageValue = null;
		if ((arg0 instanceof ItemBought)) {
			percentageValue = ((ItemBought) arg0).getTotalInKg();
		}
		return percentageValue;
	}

	protected void setValue(Object arg0, Object arg1) {
		if ((this.dlg == null) || (this.dlg.isShellClosed())) {
			return;
		}
		if ((arg0 instanceof ItemBought)) {
			ItemBought item = (ItemBought) arg0;
			item.setTotalInKg(arg1.toString());
		}
		getViewer().update(arg0, null);
	}
}
