package accounts.plugin.ui.editingsupport;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;

import accounts.plugin.model.classes.ItemBought;

public class NoOfPocketsEditingSupport extends EditingSupport {
	private TextCellEditor textEditor;

	public NoOfPocketsEditingSupport(TreeViewer viewer) {
		super(viewer);
		this.textEditor = new TextCellEditor(viewer.getTree());
	}

	protected boolean canEdit(Object arg0) {
		if ((arg0 instanceof ItemBought)) {
			return true;
		}
		return false;
	}

	protected CellEditor getCellEditor(Object arg0) {
		return this.textEditor;
	}

	protected Object getValue(Object arg0) {
		String quantity = null;
		if ((arg0 instanceof ItemBought)) {
			quantity = ((ItemBought) arg0).getNoOfPockets();
		}
		return quantity;
	}

	protected void setValue(Object arg0, Object arg1) {
		if ((arg0 instanceof ItemBought)) {
			int noOfPock = accounts.plugin.ui.Utility.parseInt(arg1.toString());
			((ItemBought) arg0).setNoOfPockets(noOfPock + "");
			if (!((ItemBought) arg0).isUnloadingChargesChanged()) {
				((ItemBought) arg0).setUnloadingCharges((noOfPock * 10) + "");
			}
		}
		getViewer().update(arg0, null);
	}
}