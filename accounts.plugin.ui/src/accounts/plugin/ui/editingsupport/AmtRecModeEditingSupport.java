package accounts.plugin.ui.editingsupport;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;

import accounts.plugin.model.classes.ItemSold;
import accounts.plugin.model.classes.Member;
import accounts.plugin.model.classes.ModelManager;

public class AmtRecModeEditingSupport extends EditingSupport {
	private TextCellEditor textEditor;

	public AmtRecModeEditingSupport(TreeViewer viewer) {
		super(viewer);
		this.textEditor = new TextCellEditor(viewer.getTree());
	}

	protected boolean canEdit(Object arg0) {
		if ((arg0 instanceof ItemSold)) {
			return true;
		}
		return false;
	}

	protected CellEditor getCellEditor(Object arg0) {
		return this.textEditor;
	}

	protected Object getValue(Object arg0) {
		String price = null;
		if ((arg0 instanceof ItemSold)) {
			price = ((ItemSold) arg0).getAmtRecMode();
		}
		return price;
	}

	protected void setValue(Object arg0, Object arg1) {
		try {
			if ((arg0 instanceof ItemSold)) {
				((ItemSold) arg0).setAmtRecMode(arg1.toString());
				Member member = null;
				for (Member mem : ModelManager.getInstance().getModel().getMembers()) {
					if (mem.getName().equals(((ItemSold) arg0).getSoldUnderMember())) {
						member = mem;
						break;
					}
				}
				List<ItemSold> list = ModelManager.getInstance().getMapOfItemsSold().get(member);
				if (list == null) {
					if (((ItemSold) arg0).getAmtRecMode().startsWith("CASH") || ((ItemSold) arg0).getAmtRecMode().startsWith("ACC")) {
						List<ItemSold> listOfItmsSold = new ArrayList();
						listOfItmsSold.add((ItemSold) arg0);
						ModelManager.getInstance().getMapOfItemsSold().put(member, listOfItmsSold);
					}
				} else {
					if ((((ItemSold) arg0).getAmtRecMode().startsWith("CASH") || ((ItemSold) arg0).getAmtRecMode().startsWith("ACC"))) {
						if (!list.contains(arg0)) {
							list.add((ItemSold) arg0);
						}
					}else {
						list.remove((ItemSold) arg0);
					}
				}
			}
			getViewer().refresh();
			getViewer().update(arg0, null);
		} catch (NumberFormatException localNumberFormatException) {
		}
	}
}