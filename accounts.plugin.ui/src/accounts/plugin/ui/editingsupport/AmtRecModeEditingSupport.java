package accounts.plugin.ui.editingsupport;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;

import accounts.plugin.model.classes.ItemSold;

public class AmtRecModeEditingSupport extends EditingSupport {
	  private TextCellEditor textEditor;
	  
	  public AmtRecModeEditingSupport(TreeViewer viewer)
	  {
	    super(viewer);
	    this.textEditor = new TextCellEditor(viewer.getTree());
	  }
	  
	  protected boolean canEdit(Object arg0)
	  {
	    if ((arg0 instanceof ItemSold)) {
	      return true;
	    }
	    return false;
	  }
	  
	  protected CellEditor getCellEditor(Object arg0)
	  {
	    return this.textEditor;
	  }
	  
	  protected Object getValue(Object arg0)
	  {
	    String price = null;
	    if ((arg0 instanceof ItemSold)) {
	      price = ((ItemSold)arg0).getAmtRecMode();
	    }
	    return price;
	  }
	  
	  protected void setValue(Object arg0, Object arg1)
	  {
	    try
	    {
	      if ((arg0 instanceof ItemSold)) {
	        ((ItemSold)arg0).setAmtRecMode(arg1.toString());
	      }
	      getViewer().refresh();
	      getViewer().update(arg0, null);
	    }
	    catch (NumberFormatException localNumberFormatException) {}
	  }
	}