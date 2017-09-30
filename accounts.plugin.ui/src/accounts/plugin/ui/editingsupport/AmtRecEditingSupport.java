package accounts.plugin.ui.editingsupport;

import java.util.ArrayList;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;

import accounts.plugin.model.classes.Date;
import accounts.plugin.model.classes.ItemBought;
import accounts.plugin.model.classes.ItemSold;
import accounts.plugin.model.classes.Member;
import accounts.plugin.model.classes.ModelManager;
import accounts.plugin.model.classes.Month;
import accounts.plugin.ui.Utility;

public class AmtRecEditingSupport extends EditingSupport {
	private TextCellEditor textEditor;
	private TreeViewer viewer;

	public AmtRecEditingSupport(TreeViewer viewer) {
		super(viewer);
		this.textEditor = new TextCellEditor(viewer.getTree());
		this.viewer = viewer;
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
			price = ((ItemSold) arg0).getAmtReceived();
		}
		return price;
	}

	protected void setValue(Object arg0, Object arg1) {
		try {
			ItemSold itemSold = (ItemSold) arg0;
			itemSold.setAmtReceived(arg1.toString());

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
			boolean previousItemFound = false;
			for (Member mem : ModelManager.getInstance().getModel().getMembers()) {
				if (previousItemFound) {
					break;
				}
				if (mem.equals(member)) {
					for (Month mon : mem.getMonths()) {
						for (Date date : mon.getDates()) {
							if (previousItemFound) {
								break;
							}
							for (ItemBought itemsBought : date.getItemsBought()) {
								for (ItemSold itmSold : itemsBought.getItemsSold()) {
									if (itmSold.getPersonName().equalsIgnoreCase(itemSold.getPersonName())) {
										if ((!itmSold.equals(itemSold)) && (!previousItemFound)) {
											previousItemSold = itmSold;
										} else {
											previousItemFound = true;
											break;
										}
									}
								}
							}
						}
					}
				}
			}
			double received = Utility.converToDouble(itemSold.getAmtReceived()).doubleValue();
			double total = Utility.converToDouble(itemSold.getTotalPrice()).doubleValue();
			double preBal = Utility.converToDouble(itemSold.getPreviousBal()).doubleValue();
			double amtBal = Utility.converToDouble(previousItemSold != null ? previousItemSold.getAmtBalance() : "0")
					.doubleValue();
			itemSold.setAmtBalance(total + preBal - received + amtBal + "");

			java.util.List<ItemSold> itemSolds = new ArrayList<>();
			boolean itemFound = false;
			for (Member mem : ModelManager.getInstance().getModel().getMembers()) {
				if (mem.equals(member)) {
					for (Month mon : mem.getMonths()) {
						for (Date date : mon.getDates()) {
							for (ItemBought itemsBought : date.getItemsBought()) {
								for (ItemSold itmSold : itemsBought.getItemsSold()) {
									if (itmSold.getPersonName().equalsIgnoreCase(itemSold.getPersonName())) {
										if (itmSold.equals(itemSold) && !itemFound) {
											itemFound = true;
											continue;
										} else if (itemFound) {
											itemSolds.add(itmSold);
										}
									}
								}

							}
						}
					}
				}
			}
			for (ItemSold itm : itemSolds) {
				received = Utility.converToDouble(itm.getAmtReceived()).doubleValue();
				total = Utility.converToDouble(itm.getTotalPrice()).doubleValue();
				preBal = Utility.converToDouble(itm.getPreviousBal()).doubleValue();
				amtBal = Utility.converToDouble(itemSold.getAmtBalance()).doubleValue();
				itm.setAmtBalance(total + preBal - received + amtBal + "");
			}
			ItemSold previousItem = null;
			for (int i = 0; i < itemSolds.size(); i++) {
				if (i == 0) {
					previousItem = itemSold;
				} else {
					previousItem = (ItemSold) itemSolds.get(i - 1);
				}
				received = Utility.converToDouble(((ItemSold) itemSolds.get(i)).getAmtReceived()).doubleValue();
				total = Utility.converToDouble(((ItemSold) itemSolds.get(i)).getTotalPrice()).doubleValue();
				preBal = Utility.converToDouble(((ItemSold) itemSolds.get(i)).getPreviousBal()).doubleValue();
				amtBal = Utility.converToDouble(previousItem.getAmtBalance()).doubleValue();
				((ItemSold) itemSolds.get(i)).setAmtBalance(total + preBal - received + amtBal + "");
			}
			getViewer().refresh();
			getViewer().update(arg0, null);
		} catch (NumberFormatException localNumberFormatException) {
		}
	}
}
