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
import accounts.plugin.model.classes.Member;
import accounts.plugin.model.classes.ModelManager;
import accounts.plugin.ui.Utility;

public class UnitPriceEditingSupport extends EditingSupport{
	  private TextCellEditor textEditor;
	  private TreeViewer viewer;
	  
	  public UnitPriceEditingSupport(TreeViewer viewer)
	  {
	    super(viewer);
	    this.textEditor = new TextCellEditor(viewer.getTree());
	    this.viewer = viewer;
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
	      price = ((ItemSold)arg0).getUnitPrice();
	    }
	    return price;
	  }
	  
	  protected void setValue(Object arg0, Object arg1)
	  {
	    if ((arg0 instanceof ItemSold))
	    {
	      ItemSold soldItem = (ItemSold)arg0;
	      soldItem.setUnitPrice(arg1.toString());
	      double kg = Utility.converToDouble(soldItem.getTotalKg()).doubleValue();
	      double pricePerKg = Utility.converToDouble(soldItem.getUnitPrice()).doubleValue();
	      double tranMis = Utility.converToDouble(soldItem.getTranportAndMisc()).doubleValue();
	      double preBal = Utility.converToDouble(soldItem.getPreviousBal()).doubleValue();
	      double received = Utility.converToDouble(soldItem.getAmtReceived()).doubleValue();
	      double totalPrice = kg * pricePerKg + tranMis;
	      soldItem.setTotalPrice(totalPrice+"");
	      
	      TreeSelection selection = (TreeSelection)this.viewer.getSelection();
	      TreePath[] paths = selection.getPaths();
	      Member member = null;
	      for (TreePath treePath : paths) {
	        if ((treePath.getSegment(1) instanceof Member))
	        {
	          member = (Member)treePath.getSegment(1);
	          break;
	        }
	      }
	      ItemSold previousItemSold = null;
	      boolean isFirstItem = false;
	      for (Member mem : ModelManager.getInstance().getModel().getMembers()) {
	        if (mem.equals(member)) {
	          for (Date date : mem.getDates())
	          {
	            if (isFirstItem) {
	              break;
	            }
	            for (ItemBought itemsBought : date.getItemsBought())
	            {
	              if (isFirstItem) {
	                break;
	              }
	              for (ItemSold itmSold : itemsBought.getItemsSold()) {
	                if (itmSold.getPersonName().equalsIgnoreCase(soldItem.getPersonName())) {
	                  if (!itmSold.equals(soldItem))
	                  {
	                    previousItemSold = itmSold;
	                  }
	                  else
	                  {
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
	      double balReceived = Utility.converToDouble(previousItemSold != null ? previousItemSold.getAmtBalance() : "0").doubleValue();
	      soldItem.setAmtBalance(totalPrice + preBal - received + balReceived+"");
	    }
	    getViewer().update(arg0, null);
	  }
	}