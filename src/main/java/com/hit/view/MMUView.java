package com.hit.view;


import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;


public class MMUView extends java.util.Observable implements View {

	static int	BYTES_IN_PAGE;
	static int	NUM_MMU_PAGES;
	private Composite tableArea;
	private Composite pageStatistic;
	private Composite playButtons;
	private Composite processSelect;

	
	private void createAndShowGui() {
		
		final boolean ALLOW_SPAN_HORIZONAL = true;
		final boolean ALLOW_SPAN_VERTICAL = true;
		
		Display display = new Display ();
		
		Rectangle screenSize = display.getPrimaryMonitor().getClientArea();
		Shell shell = new Shell (display);
		GridLayout layout = new GridLayout(7, true);
		shell.setLayout(layout);
		int width = 1000;
		int height = 500;
		shell.setBounds((screenSize.width/2) - (width/2), (screenSize.height/2) - (height/2), width, height);
		shell.setText("MMU Simulator");
		
		
		tableArea = new Composite(shell, SWT.NO_FOCUS);
		pageStatistic = new Composite(shell, SWT.NO_FOCUS);
		playButtons = new Composite(shell, SWT.NO_FOCUS);
		processSelect = new Composite(shell, SWT.NO_FOCUS);
		
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 5, 3);
		gridData.heightHint = 200;
		tableArea.setLayoutData(gridData);
		tableArea.setLayout (new FillLayout ());
		gridData = new GridData(SWT.FILL, SWT.FILL, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 2, 3);
		gridData.heightHint = 200;
		pageStatistic.setLayoutData(gridData);
		pageStatistic.setLayout(new GridLayout(2, false));
		gridData = new GridData(SWT.FILL, SWT.FILL, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 5, 1);
		gridData.heightHint = 100;
		playButtons.setLayoutData(gridData);
		playButtons.setLayout(new GridLayout(8, true));
		gridData = new GridData(SWT.FILL, SWT.FILL, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 2, 1);
		gridData.heightHint = 100;
		processSelect.setLayoutData(gridData);
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.marginTop = 20;
		processSelect.setLayout(rowLayout);
		
		
		// Table area components
	    Table pageTable = new Table(tableArea, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
	    pageTable.setLinesVisible(true);
	    pageTable.setHeaderVisible(true);
	    
	    for (Integer i = 0; i < 30; i++) {
	    	TableColumn column = new TableColumn(pageTable, SWT.CENTER);
	    	column.setText(i.toString());
	    	column.setAlignment(SWT.CENTER);
	    	column.setWidth(20);
	    	column.pack();
	    }
	    
	     
	    // Page statistics components
	    Label pageFault = new Label(pageStatistic, SWT.CENTER);
	    pageFault.setText("Page Fault Amounts:");
	    pageFault.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 1, 1));
	    Text pageFaultText = new Text(pageStatistic, SWT.CENTER | SWT.READ_ONLY | SWT.BORDER);
	    pageFaultText.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 1, 1));

	    Label pageReplacement = new Label(pageStatistic, SWT.CENTER); 
	    pageReplacement.setText("Page Replacement Amounts:");
		pageReplacement.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 1, 1));
	    Text pageReplacementText = new Text(pageStatistic, SWT.CENTER | SWT.READ_ONLY | SWT.BORDER);
	    pageReplacementText.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 1, 1));
	    
	    
	    // Play buttons components
		Button playButton = new Button(playButtons, SWT.PUSH | SWT.CENTER);
		playButton.setText("Play");
		playButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 1, 1));
		
		Button playAllButton = new Button(playButtons, SWT.PUSH);
		playAllButton.setText("Play All");
		playAllButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 1, 1));
		
		
		// Process Select area components
		Label processes = new Label(processSelect, SWT.CENTER);
		processes.setText("Processes:");
		org.eclipse.swt.widgets.List processChoose = new org.eclipse.swt.widgets.List(processSelect, SWT.MULTI | SWT.BORDER);
	   
		shell.open ();
		shell.setMinimized(false);

		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
	
	public void setConfiguration(List<String> commands) {
		// TODO
	}
	
	@Override
	public void open() {
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
		  @Override
		  public void run()
		  {
		     createAndShowGui();
		  }
		});
	}

}
