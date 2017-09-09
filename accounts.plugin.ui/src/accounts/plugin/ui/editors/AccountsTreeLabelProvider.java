package accounts.plugin.ui.editors;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import accounts.plugin.model.classes.Accounts;
import accounts.plugin.model.classes.Date;
import accounts.plugin.model.classes.ItemBought;
import accounts.plugin.model.classes.ItemSold;
import accounts.plugin.model.classes.Member;

public class AccountsTreeLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof Accounts) {
			return ((Accounts) element).getName();
		} else if (element instanceof Member) {
			return ((Member) element).getName();
		} else if (element instanceof Date) {
			return ((Date) element).getName();
		} else if (element instanceof ItemBought) {
			return ((ItemBought) element).getName();
		} else if (element instanceof ItemSold) {
			return ((ItemSold) element).getPersonName();
		}
		return super.getText(element);
	}
}
