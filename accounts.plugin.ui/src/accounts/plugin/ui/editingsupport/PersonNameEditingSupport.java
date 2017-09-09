package accounts.plugin.ui.editingsupport;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;

import accounts.plugin.model.classes.ItemSold;

public class PersonNameEditingSupport extends EditingSupport {
	private TextCellEditor textEditor;

	public PersonNameEditingSupport(TreeViewer viewer) {
		super(viewer);
		textEditor = new TextCellEditor(viewer.getTree());
	}

	@Override
	protected boolean canEdit(Object arg0) {
		if (arg0 instanceof ItemSold) {
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
		String name = null;
		if (arg0 instanceof ItemSold) {
			name = ((ItemSold) arg0).getPersonName();
		}
		return name;
	}

	@Override
	protected void setValue(Object arg0, Object arg1) {
		if (arg0 instanceof ItemSold) {
			((ItemSold) arg0).setPersonName(arg1.toString());
		}
		getViewer().update(arg0, null);

	}

}
