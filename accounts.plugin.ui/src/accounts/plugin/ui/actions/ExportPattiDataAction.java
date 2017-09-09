package accounts.plugin.ui.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import accounts.plugin.model.classes.ItemBought;
import accounts.plugin.ui.Utility;

public class ExportPattiDataAction extends Action {
	private TreeViewer treeViewer;

	public ExportPattiDataAction(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	public void run() {
		IStructuredSelection selection = (IStructuredSelection) this.treeViewer.getSelection();
		final Object firstElement = selection.getFirstElement();
		if ((firstElement instanceof ItemBought)) {
			final ItemBought itemBought = (ItemBought) firstElement;
			Dialog dialog = new Dialog(this.treeViewer.getControl().getShell()) {
				protected Control createDialogArea(Composite parent) {
					Composite composite = new Composite(parent, 0);
					composite.setLayout(new GridLayout());
					composite.setLayoutData(new GridData(4, 4, true, false));

					Label label = new Label(composite, 0);
					label.setText(
							"Export Data file will be created under C:/temp with a file name starts with Vendor Name and time stamp appended to it.");
					return parent;
				}

				protected void okPressed() {
					new File("C://temp").mkdir();
					String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
					try {
						File file = new File(
								"C://temp//" + ((ItemBought) firstElement).getVendor() + "_" + timeStamp + ".doc");
						file.createNewFile();

						BufferedWriter bw = null;
						FileWriter fw = null;
						try {
							List<String> strings = new ArrayList<>();
							Date date = new Date();
							strings.add(date.toString());
							strings.add("---------------------------------------------------------------");
							strings.add("Vendor Name\t\t- " + itemBought.getVendor());
							
							String noOfPockets = itemBought.getNoOfPockets();
							if (noOfPockets != null && noOfPockets.trim().length() > 0
									&& Utility.parseInt(noOfPockets) > 0) {
								strings.add("No of Pockets\t- " + noOfPockets);
							}
							
							String totalInKg = itemBought.getTotalInKg();
						
							if (totalInKg != null && totalInKg.trim().length() > 0
									&& Utility.parseInt(totalInKg) > 0) {
								strings.add("Total in KGs\t- " + totalInKg);
							}
							
							
							String ratePerKg = itemBought.getRatePerKg();
							if (ratePerKg != null && ratePerKg.trim().length() > 0
									&& Utility.parseInt(ratePerKg) > 0) {
								strings.add("Rate Per KG\t\t- " + ratePerKg);
							}
							

							double totalKg = Utility.converToDouble(totalInKg).doubleValue();
							double unitPrice = Utility.converToDouble(ratePerKg).doubleValue();
							double actualRate = totalKg * unitPrice;
							if (actualRate>1) {
								strings.add("Actual Rate\t\t- " + actualRate);
							}

							double totalComm = actualRate * 0.1D;
							strings.add("Commission\t\t- " + totalComm);
							strings.add("Miscellaneous\t- " + itemBought.getMiscellaneous());
							strings.add("Unloading Charges\t- " + itemBought.getUnloadingCharges());

							double mis = Utility.converToDouble(itemBought.getMiscellaneous()).doubleValue();
							double unl = Utility.converToDouble(itemBought.getUnloadingCharges()).doubleValue();
							double finalTotal = actualRate - (totalComm + mis + unl);
							strings.add("Final Total\t\t- " + finalTotal);
							fw = new FileWriter(file);
							bw = new BufferedWriter(fw);
							for (String string : strings) {
								bw.write(string);
								bw.newLine();
							}
						} catch (IOException e) {
							e.printStackTrace();
							try {
								if (bw != null) {
									bw.close();
								}
								fw.close();
							} catch (IOException localIOException1) {
							}
						} finally {
							try {
								if (bw != null) {
									bw.close();
								}
								if (fw != null) {
									fw.close();
								}
							} catch (IOException localIOException2) {
							}
						}
						try {
							if (bw != null) {
								bw.close();
							}
							if (fw != null) {
								fw.close();
							}
						} catch (IOException localIOException3) {
						}
						super.okPressed();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				protected void configureShell(Shell newShell) {
					super.configureShell(newShell);
					newShell.setText("Export Data");
				}
			};
			dialog.open();
		}
	}
}
