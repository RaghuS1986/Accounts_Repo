package accounts.plugin.ui.editors.patti;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import accounts.plugin.model.classes.Date;
import accounts.plugin.model.classes.ItemBought;
import accounts.plugin.model.classes.Member;
import accounts.plugin.model.classes.Month;
import accounts.plugin.model.patti.Patti;

public class PattiTreeLabelProvider
  extends ColumnLabelProvider
{
  public String getText(Object element)
  {
    if ((element instanceof Patti)) {
      return ((Patti)element).getName();
    }
    if ((element instanceof Member)) {
      return ((Member)element).getName();
    }
    if ((element instanceof Month)) {
        return ((Month)element).getName();
      }
    if ((element instanceof Date)) {
      return ((Date)element).getName();
    }
    if ((element instanceof ItemBought)) {
      return ((ItemBought)element).getName();
    }
    return super.getText(element);
  }
}
