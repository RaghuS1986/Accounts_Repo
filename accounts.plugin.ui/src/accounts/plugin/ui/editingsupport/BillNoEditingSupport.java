package accounts.plugin.ui.editingsupport;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;

import accounts.plugin.model.classes.Date;
import accounts.plugin.model.classes.ItemBought;
import accounts.plugin.model.classes.ItemSold;
import accounts.plugin.ui.Utility;

public class BillNoEditingSupport extends EditingSupport {
	private TextCellEditor textEditor;
	private TreeViewer viewer;

	public BillNoEditingSupport(TreeViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		textEditor = new TextCellEditor(viewer.getTree());
	}

	@Override
	protected boolean canEdit(Object arg0) {
		boolean canEdit = false;
		if (arg0 instanceof ItemSold) {
			if (((ItemSold) arg0).getTotalPrice()!=null) {
				Double converToDouble = Utility.converToDouble(((ItemSold) arg0).getTotalPrice());
				if (converToDouble==0.0) {
					return false;
				}
			}

			TreeSelection selection = (TreeSelection) this.viewer.getSelection();
			TreePath[] paths = selection.getPaths();
			Date date = null;
			for (TreePath treePath : paths) {
				if ((treePath.getSegment(4) instanceof Date)) {
					date = (Date) treePath.getSegment(4);
					break;
				}
			}
			boolean billNoEnterd = false;
			for (ItemBought btItm : date.getItemsBought()) {
				if (billNoEnterd) {
					break;
				}
				for (ItemSold sldItm : btItm.getItemsSold()) {
					if (!sldItm.equals(arg0) && sldItm.getPersonName().equals(((ItemSold) arg0).getPersonName())
							&& sldItm.getBillNo() != null && !sldItm.getBillNo().trim().isEmpty()) {
						billNoEnterd = true;
						break;
					}
				}
			}
			canEdit = !billNoEnterd;
		}
		return canEdit;
	}

	@Override
	protected CellEditor getCellEditor(Object arg0) {
		return textEditor;
	}

	@Override
	protected Object getValue(Object arg0) {
		String billNo = null;
		if (arg0 instanceof ItemSold) {
			billNo = ((ItemSold) arg0).getBillNo();
		}
		return billNo;
	}

	@Override
	protected void setValue(Object arg0, Object arg1) {
		if (arg0 instanceof ItemSold) {
			((ItemSold) arg0).setBillNo(arg1.toString());
		}
		getViewer().update(arg0, null);
	}
}