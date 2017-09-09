//package accounts.plugin.ui.editingsupport;
//
//import org.eclipse.jface.viewers.CellEditor;
//import org.eclipse.jface.viewers.EditingSupport;
//import org.eclipse.jface.viewers.TextCellEditor;
//import org.eclipse.jface.viewers.TreeViewer;
//
//import accounts.plugin.model.classes.ItemSold;
//
//public class AmtBalEditingSupport extends EditingSupport {
//
//	private TextCellEditor textEditor;
//	public AmtBalEditingSupport(TreeViewer viewer) {
//		super(viewer);
//		textEditor = new TextCellEditor(viewer.getTree());
//	}
//
//	@Override
//	protected boolean canEdit(Object arg0) {
//		if (arg0 instanceof ItemSold) {
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	protected CellEditor getCellEditor(Object arg0) {
//		return textEditor;
//	}
//
//	@Override
//	protected Object getValue(Object arg0) {
//		String price=null;
//		 if (arg0 instanceof ItemSold) {
//			price=((ItemSold) arg0).getAmtBalance();
//		}
//		return price;
//	}
//
//	@Override
//	protected void setValue(Object arg0, Object arg1) {
//		 if (arg0 instanceof ItemSold) {
//			((ItemSold) arg0).setAmtBalance(arg1.toString());
//		}
//		getViewer().update(arg0, null);
//	}
//
//}
