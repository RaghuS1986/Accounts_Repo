package accounts.plugin.ui.editingsupport;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import accounts.plugin.model.classes.Date;
import accounts.plugin.model.classes.ItemBought;
import accounts.plugin.model.classes.ItemSold;
import accounts.plugin.model.classes.Member;
import accounts.plugin.model.classes.ModelManager;
import accounts.plugin.ui.Utility;
import accounts.plugin.ui.dialogs.KG_SaleDialog;

public class KGEditingSupport extends EditingSupport {
	private TreeViewer viewer;
	private DialogCellEditor dialogCellEditor;
	private KG_SaleDialog dlg;

	public KGEditingSupport(TreeViewer viewer) {
		super(viewer);

		this.dialogCellEditor = new DialogCellEditor(viewer.getTree()) {
			protected Object openDialogBox(Control arg0) {
				KGEditingSupport.this.dlg = new KG_SaleDialog(Display.getCurrent().getActiveShell());
				KGEditingSupport.this.dlg.open();
				return Integer.valueOf(KGEditingSupport.this.dlg.getTotalKG());
			}
		};
		this.viewer = viewer;
	}

	protected boolean canEdit(Object arg0) {
		if ((arg0 instanceof ItemSold)) {
			return true;
		}
		return false;
	}

	protected CellEditor getCellEditor(Object arg0) {
		return this.dialogCellEditor;
	}

	protected Object getValue(Object arg0) {
		String kg = null;
		if ((arg0 instanceof ItemSold)) {
			kg = ((ItemSold) arg0).getTotalKg();
		}
		return kg;
	}

	protected void setValue(Object arg0, Object arg1) {
		if ((this.dlg == null) || (this.dlg.isShellClosed())) {
			return;
		}
		if ((arg0 instanceof ItemSold)) {
			ItemSold soldItem = (ItemSold) arg0;
			soldItem.setTotalKg(arg1.toString());
			double kg = Utility.converToDouble(soldItem.getTotalKg()).doubleValue();
			double pricePerKg = Utility.converToDouble(soldItem.getUnitPrice()).doubleValue();
			double miscExp = Double.parseDouble(soldItem.getTranportAndMisc());
			double totalPrice = kg * pricePerKg + miscExp;
			soldItem.setTotalPrice(totalPrice + "");

			double amtReceived = Utility.converToDouble(soldItem.getAmtReceived()).doubleValue();
			double preBal = Utility.converToDouble(soldItem.getPreviousBal()).doubleValue();

			TreeSelection selection = (TreeSelection) this.viewer.getSelection();
			TreePath[] paths = selection.getPaths();
			Member member = null;
			for (TreePath treePath : paths) {
				if ((treePath.getSegment(1) instanceof Member)) {
					member = (Member) treePath.getSegment(1);
					break;
				}
			}
			ItemSold previousItemSold = null;
			boolean isFirstItem = false;
			for (Member mem : ModelManager.getInstance().getModel().getMembers()) {
				if (mem.equals(member)) {
					for (Date date : mem.getDates()) {
						if (isFirstItem) {
							break;
						}
						for (ItemBought itemsBought : date.getItemsBought()) {
							if (isFirstItem) {
								break;
							}
							for (ItemSold itmSold : itemsBought.getItemsSold()) {
								if (itmSold.getPersonName().equalsIgnoreCase(soldItem.getPersonName())) {
									if (!itmSold.equals(soldItem)) {
										previousItemSold = itmSold;
									} else {
										if (previousItemSold != null) {
											break;
										}
										isFirstItem = true;
										break;
									}
								}
							}
						}
					}
				}
			}
			double balReceived = Utility
					.converToDouble(previousItemSold != null ? previousItemSold.getAmtBalance() : "0").doubleValue();
			soldItem.setAmtBalance(totalPrice + preBal - amtReceived + balReceived + "");
		}
		getViewer().update(arg0, null);
	}
}