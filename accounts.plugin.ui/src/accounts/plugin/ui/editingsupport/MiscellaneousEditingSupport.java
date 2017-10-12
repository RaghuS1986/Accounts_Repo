package accounts.plugin.ui.editingsupport;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;

import accounts.plugin.model.classes.ItemBought;
import accounts.plugin.ui.Utility;

public class MiscellaneousEditingSupport extends EditingSupport {
	private TextCellEditor textEditor;

	public MiscellaneousEditingSupport(TreeViewer viewer) {
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
		String price = null;
		if ((arg0 instanceof ItemBought)) {
			price = ((ItemBought) arg0).getMiscellaneous();
		}
		return price;
	}

	protected void setValue(Object arg0, Object arg1) {
		if ((arg0 instanceof ItemBought)) {
			ItemBought item = (ItemBought) arg0;
			int parseInt = Utility.parseInt(arg1.toString());
			item.setMiscellaneous(parseInt + "");
		}
		getViewer().update(arg0, null);
	}
}