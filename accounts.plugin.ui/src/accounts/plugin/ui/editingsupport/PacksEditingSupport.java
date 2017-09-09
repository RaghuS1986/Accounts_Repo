package accounts.plugin.ui.editingsupport;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;

import accounts.plugin.model.classes.ItemSold;

public class PacksEditingSupport extends EditingSupport {
	private TextCellEditor textEditor;
	public PacksEditingSupport(TreeViewer viewer) {
		super(viewer);
		textEditor = new TextCellEditor(viewer.getTree());
	}

	@Override
	protected boolean canEdit(Object arg0) {
		if (arg0 instanceof ItemSold ) {
			return true;
		}
		return false;
	}

	@Override
	protected CellEditor getCellEditor(Object arg0) {
		return textEditor;
	}

	@Override
	protected Object getValue(Object arg0) {
		String packs=null;
		if (arg0 instanceof ItemSold) {
			packs=((ItemSold) arg0).getNumberOfPacks();
		}
		return packs;
	}

	@Override
	protected void setValue(Object arg0, Object arg1) {
		if (arg0 instanceof ItemSold) {
			((ItemSold) arg0).setNumberOfPacks(arg1.toString());
		}
		getViewer().update(arg0, null);
	}
}
