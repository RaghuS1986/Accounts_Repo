package accounts.plugin.ui.editors;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import accounts.plugin.model.classes.Accounts;
import accounts.plugin.model.classes.Date;
import accounts.plugin.model.classes.ItemBought;
import accounts.plugin.model.classes.Member;
import accounts.plugin.model.classes.Month;

public class AccountsContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getChildren(Object arg0) {
		if (arg0 instanceof Accounts) {
			return ((Accounts) arg0).getMembers().toArray();
		}
		else if (arg0 instanceof Member) {
			return ((Member) arg0).getMonths().toArray();
		}else if (arg0 instanceof Month) {
			return ((Month) arg0).getDates().toArray();
		}else if (arg0 instanceof Date) {
			return ((Date) arg0).getItemsBought().toArray();
		}else if (arg0 instanceof ItemBought) {
			return ((ItemBought) arg0).getItemsSold().toArray();
		}
		
		return null;
	}

	@Override
	public Object[] getElements(Object arg0) {
		if (arg0 instanceof List<?>) {
			return ((List<?>) arg0).toArray();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object arg0) {
		return null;
	}

	@Override
	public boolean hasChildren(Object arg0) {
		if (arg0 instanceof Accounts ||arg0 instanceof Member ||arg0 instanceof Month||arg0 instanceof Date ||arg0 instanceof ItemBought) {
			return true;
		}
		return false;
	}

}
