package com.hit.view;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;


public class MMUView extends java.util.Observable implements View {

	static int	BYTES_IN_PAGE;
	static int	NUM_MMU_PAGES;
	private int pfAmount = 0;
	private int prAmount = 0;
	private Shell shell;
	private Composite tableArea;
	private Composite processSelect;
	private Composite Operations;
	private Table pageTable;
	private Label pageFaultText;
	private Label pageReplacementText;
	private Button playButton;
	private Button playAllButton;
	private org.eclipse.swt.widgets.List processChoose;
	private Button stepBackButton;
	private Button resetButton;
	private Set<Integer> selectedProcess;
	
	private void createAndShowGui() {
		
		final boolean ALLOW_SPAN_HORIZONAL = true;
		final boolean ALLOW_SPAN_VERTICAL = true;
			
		GridData gridData;
		GridLayout gridLayout;
		Display display = new Display();
		Rectangle screenSize = display.getPrimaryMonitor().getClientArea();
		shell = new Shell (display);
		GridLayout layout = new GridLayout(2, false);
		shell.setLayout(layout);
		int width = 550;
		int height = 300;
		shell.setBounds((screenSize.width/2) - (width/2), (screenSize.height/2) - (height/2), width, height);
		shell.setText("MMU Simulator");
		
		tableArea = new Composite(shell, SWT.NO_FOCUS);
		gridData = new GridData(SWT.FILL, SWT.FILL, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 1, 2);
		gridData.widthHint = (int)(width * (8.0/11));
		gridData.heightHint = (int)(height * (4.0/6));
		tableArea.setLayoutData(gridData);
		tableArea.setLayout(new GridLayout(1, true));
		
		processSelect = new Composite(shell, SWT.NO_FOCUS);
		gridData = new GridData(SWT.FILL, SWT.FILL, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 1, 2);
		gridData.widthHint = (int)(width * (3.0/11));
		gridData.heightHint = (int)(height * (4.0/6));
		processSelect.setLayoutData(gridData);
		processSelect.setLayout(new GridLayout(1, true));
		
		Operations = new Composite(shell, SWT.NO_FOCUS);
		gridData = new GridData(SWT.FILL, SWT.FILL, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 2, 1);
		gridData.widthHint = width;
		gridData.heightHint = (int)(height * (2.0/6));
		Operations.setLayoutData(gridData);
		Operations.setLayout(new GridLayout(2, false));
		
		// Table area components
	    pageTable = new Table(tableArea, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
	    pageTable.setLayoutData(new GridData(GridData.FILL_BOTH));
	    pageTable.setLinesVisible(true);
	    pageTable.setHeaderVisible(true);
	    
//	    for (Integer i = 0; i < 10; i++) {
//	    	TableColumn column = new TableColumn(pageTable, SWT.CENTER);
//	    	column.setText(i.toString());
//	    	column.setAlignment(SWT.CENTER);
//	    	column.setWidth(20);
//	    	column.pack();
//	    }
	    
	    // Processes Select area components
	    Label processesLabel = new Label(processSelect, SWT.CENTER);
	    processesLabel.setLayoutData(new GridData());
	    processesLabel.setText("Processes:");
	    processChoose = new org.eclipse.swt.widgets.List(processSelect, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
	    processChoose.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 1, 1));
		
	    
	    // Operations area components
	    // Play buttons components
	    Composite buttons = new Composite(Operations, SWT.NO_FOCUS);
	    gridData = new GridData(SWT.FILL, SWT.FILL, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1);
	    gridLayout = new GridLayout(2, false);
	    gridLayout.horizontalSpacing = 10;
	    gridLayout.verticalSpacing = 5;
	    buttons.setLayout(gridLayout);
	    buttons.setLayoutData(gridData);
	    
	    playButton = new Button(buttons, SWT.PUSH | SWT.CENTER);
	    playButton.setText("Play");
	    gridData = new GridData(SWT.LEFT, SWT.CENTER, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1);
	    gridData.widthHint = 50;
	    playButton.setEnabled(false);
	    playButton.setLayoutData(gridData);
	    
	    playAllButton = new Button(buttons, SWT.PUSH);
	    playAllButton.setText("Play All");
	    playAllButton.setEnabled(false);
	    playAllButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1));
	    
	    stepBackButton = new Button(buttons, SWT.PUSH | SWT.CENTER);
	    stepBackButton.setText("Step Back");
	    stepBackButton.setEnabled(false);
	    gridData = new GridData(SWT.LEFT, SWT.CENTER, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1);
	    gridData.widthHint = 70;
	    stepBackButton.setLayoutData(gridData);

	    resetButton = new Button(buttons, SWT.PUSH | SWT.CENTER);
	    resetButton.setText("Reset");
	    resetButton.setEnabled(false);
	    gridData = new GridData(SWT.LEFT, SWT.CENTER, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1);
	    gridData.widthHint = 50;
	    resetButton.setLayoutData(gridData);
	    
	   
	    // Statistics data area components
	    Composite statistics = new Composite(Operations, SWT.NO_FOCUS);
	    gridData = new GridData(SWT.FILL, SWT.FILL, ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1);
	    gridLayout = new GridLayout(2, false);
	    gridLayout.horizontalSpacing = 20;
	    gridLayout.verticalSpacing = 15;
	    gridLayout.marginTop += 5;
	    statistics.setLayout(gridLayout);
	    statistics.setLayoutData(gridData);  
	    
	    Label pageFault = new Label(statistics, SWT.CENTER);
	    pageFault.setText("Page Fault Amounts:");
	    gridData = new GridData(SWT.LEFT, SWT.CENTER, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1);
	    pageFault.setLayoutData(gridData);
	    pageFaultText = new Label(statistics, SWT.CENTER);
	    pageFaultText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1));
	    pageFaultText.setText(((Integer)pfAmount).toString());
	    
	    Label pageReplacement = new Label(statistics, SWT.CENTER);
	    pageReplacement.setText("Page Replacement Amounts:");
	    gridData = new GridData(SWT.LEFT, SWT.CENTER, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1);
	    pageReplacement.setLayoutData(gridData);
	    pageReplacementText = new Label(statistics, SWT.CENTER);
	    pageReplacementText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1));
	    pageReplacementText.setText(((Integer)prAmount).toString());
	    
	    
	    processChoose.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				selectedProcess = new HashSet<Integer>();
				String[] processesNumbers = processChoose.getSelection();
				for(int i=0; i<processesNumbers.length; i++) {
					selectedProcess.add(Integer.parseInt(processesNumbers[i].split(" ")[1]));
				}
				if(selectedProcess.size() > 0) {
					playButton.setEnabled(true);
					playAllButton.setEnabled(true);
				}
				else {
					playButton.setEnabled(false);
					playAllButton.setEnabled(false);
				}
				
			}
	    	
	    });
	    
		shell.open ();
		shell.setMinimized(false);

		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
		System.out.println("Thank you..");
		System.exit(0);
	}
	
	public void setConfiguration(List<String> commands) {
		// TODO
	}
	
	public void addProcesses(Integer numOfProcess) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Display.getDefault().syncExec(new Runnable(){
			@Override
			public void run() {
				if(shell.getDisplay() != null && !shell.getDisplay().isDisposed()) { 
					for(Integer i=1; i <= numOfProcess; i++) {
						processChoose.add("Process " + i.toString());
					}
				}
			}
		});
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
