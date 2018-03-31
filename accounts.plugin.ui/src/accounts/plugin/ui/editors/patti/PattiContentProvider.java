package accounts.plugin.ui.editors.patti;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import accounts.plugin.model.classes.Date;
import accounts.plugin.model.classes.Member;
import accounts.plugin.model.classes.Month;
import accounts.plugin.model.classes.Year;
import accounts.plugin.model.patti.Patti;

public class PattiContentProvider implements ITreeContentProvider {
	public Object[] getChildren(Object arg0) {
		if ((arg0 instanceof Patti)) {
			return ((Patti) arg0).getMembers().toArray();
		}
		if ((arg0 instanceof Member)) {
			return ((Member) arg0).getYears().toArray();
		}
		if ((arg0 instanceof Year)) {
			return ((Year) arg0).getMonths().toArray();
		}
		if ((arg0 instanceof Month)) {
			return ((Month) arg0).getDates().toArray();
		}
		if ((arg0 instanceof Date)) {
			return ((Date) arg0).getItemsBought().toArray();
		}
		return null;
	}

	public Object[] getElements(Object arg0) {
		if ((arg0 instanceof List)) {
			return ((List) arg0).toArray();
		}
		return new Object[0];
	}

	public Object getParent(Object arg0) {
		return null;
	}

	public boolean hasChildren(Object arg0) {
		if (((arg0 instanceof Patti)) || ((arg0 instanceof Member)) ||((arg0 instanceof Year)) || ((arg0 instanceof Month))
				|| ((arg0 instanceof Date))) {
			return true;
		}
		return false;
	}
}
