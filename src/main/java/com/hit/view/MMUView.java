package com.hit.view;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.hit.util.MMULogger;


public class MMUView extends java.util.Observable implements View {

	static int	BYTES_IN_PAGE;
	static int	NUM_MMU_PAGES;
	private Integer pfAmount;
	private Integer realPfAmount;
	private Integer prAmount;
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
	private Set<String> selectedProcess;
	private Iterator<String> commandsIterator;
	private List<String> commands;
	TableColumn[] tableCols;
	private TableItem[] tableRows;
	private MMULogger logger = MMULogger.getInstance();
	private List<String> prCommands;
	private List<Integer> memoryMap;
	private List<Integer> lastMemoryMap;
	
	
	public MMUView() {
		prCommands = new ArrayList<String>();
		pfAmount = 0;
		realPfAmount = 0;
		prAmount = 0;
		memoryMap = new LinkedList<Integer>();
		lastMemoryMap = new LinkedList<Integer>();
		
	}
	
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
		int width = 600;
		int height = 350;
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
	    pageTable = new Table(tableArea, SWT.MULTI | SWT.BORDER );
	    pageTable.setLayoutData(new GridData(GridData.FILL_BOTH));
	    pageTable.setLinesVisible(true);
	    pageTable.setHeaderVisible(true);
	     
	    
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
	    gridData = new GridData(SWT.LEFT, SWT.CENTER, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1);
	    gridData.widthHint = 50;
	    pageFaultText.setLayoutData(gridData);
	    pageFaultText.setText(((Integer)pfAmount).toString());
	    
	    Label pageReplacement = new Label(statistics, SWT.CENTER);
	    pageReplacement.setText("Page Replacement Amounts:");
	    gridData = new GridData(SWT.LEFT, SWT.CENTER, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1);
	    pageReplacement.setLayoutData(gridData);
	    pageReplacementText = new Label(statistics, SWT.CENTER);
	    gridData = new GridData(SWT.LEFT, SWT.CENTER, !ALLOW_SPAN_HORIZONAL, !ALLOW_SPAN_VERTICAL, 1, 1);
	    gridData.widthHint = 50;
	    pageReplacementText.setLayoutData(gridData);
	    pageReplacementText.setText(((Integer)prAmount).toString());
	    
	    processChoose.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				selectedProcess = new HashSet<String>();
				String[] processesNumbers = processChoose.getSelection();
				for(int i=0; i<processesNumbers.length; i++) {
					selectedProcess.add(processesNumbers[i].split(" ")[1]);
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
	    
	    playButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
		    	oneStep();
		    	enableStepBackAndReset(true);
			}
	    });
	    
	    playAllButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
		    	allSteps();
		    	enableStepBackAndReset(true);
			}
	    });
	    
	    resetButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
		    	clearAll();
		    	enableStepBackAndReset(false);
		    	enablePlay(true);
			}
	    });
	    
	    shell.open ();
		shell.setMinimized(false);
		setChanged();
		notifyObservers();

		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
		
		logger.close();
		System.out.println("Thank you..");
		System.exit(0);
	}

	private void clearAll() {
		pfAmount = 0;
		realPfAmount = 0;
		prAmount = 0;
		prCommands.clear();;
		memoryMap.clear();
		lastMemoryMap.clear();
		setPreConfiguration(this.commands);
		
		updateFields();
		
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				if(shell.getDisplay() != null && !shell.getDisplay().isDisposed()) {
					for(int i = 0; i<NUM_MMU_PAGES; i++) {
						tableCols[i].setText("");
						for(int j = 0; j < BYTES_IN_PAGE; j++) {
							tableRows[j].setText(i, "");
						}
					}
				}
			}
		});
	}
	
	private void setPreConfiguration(List<String> commands) {
		this.commands = commands;
		commandsIterator = this.commands.iterator();
		String command;
		String subCommand;
		command = commandsIterator.next();
		NUM_MMU_PAGES = Integer.parseInt(command.split("RC:")[1]);
		while (commandsIterator.hasNext()) {
			command = commandsIterator.next();
			if(command.startsWith("GP:")) {
				subCommand = command.substring(command.indexOf("["), command.indexOf("]"));
				BYTES_IN_PAGE = (subCommand.split(" ")).length;
				break;
			}
		}		
		commandsIterator = this.commands.iterator();
	}
	
	public void setConfiguration(List<String> commands) {
		
		setPreConfiguration(commands);
		
		tableCols = new TableColumn[NUM_MMU_PAGES];
		tableRows = new TableItem[BYTES_IN_PAGE];
				
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				if(shell.getDisplay() != null && !shell.getDisplay().isDisposed()) {
				    for(int i = 0; i<NUM_MMU_PAGES; i++) {
				    	tableCols[i] = new TableColumn(pageTable, SWT.CENTER);
				    	tableCols[i].setText("");
				    	tableCols[i].setWidth(50);
				    }
				    
				    for(int i = 0; i<BYTES_IN_PAGE; i++) {	    	
				    	tableRows[i] = new TableItem(pageTable, SWT.CENTER);
				    }
				}
			}
			
		});
	}
	
	
	private void updateFields() {
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				if(shell.getDisplay() != null && !shell.getDisplay().isDisposed()) { 
					pageFaultText.setText(realPfAmount.toString());
					pageReplacementText.setText(prAmount.toString());
				}
			}
		});
	}
	
	private void updateTable(List<String> data) {
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				if(shell.getDisplay() != null && !shell.getDisplay().isDisposed()) { 

					Integer pagePos;
				
					if(lastMemoryMap.size() > 0) {
						for(Integer pageId: memoryMap) {
							pagePos = memoryMap.indexOf(pageId);
							if(lastMemoryMap.contains(pageId)) {
								Integer pageLastPos = lastMemoryMap.indexOf(pageId);
								if(pagePos != pageLastPos) {
									tableCols[pagePos].setText(pageId.toString());
									for(int i=0; i<BYTES_IN_PAGE; i++) {
										tableRows[i].setText(pagePos, tableRows[i].getText(pageLastPos));
									}
								}
							}
						}
						for(int i = memoryMap.size(); i < NUM_MMU_PAGES; i++) {
							tableCols[i].setText("");
							for(int j = 0; j < BYTES_IN_PAGE; j++) {
								tableRows[j].setText(i, "");
							}
						}
					}
					lastMemoryMap.clear();
					lastMemoryMap.addAll(memoryMap);
					
					if(data != null) {
						Iterator<String> dataIter = data.iterator();
						String pageId = dataIter.next();
						Integer pageIdInt = Integer.parseInt(pageId);
						Integer pageColIndex = memoryMap.indexOf(pageIdInt);
						String curBite;
						
						tableCols[pageColIndex].setText(pageId);
						for(int i = 0; i < BYTES_IN_PAGE; i++) {
							curBite = dataIter.next();
							tableRows[i].setText(pageColIndex, curBite);
						}
					}
				}
			}
		});
	}
	
	private void allSteps() {
		while(commandsIterator.hasNext()) {
//			Display.getDefault().
			oneStep();
		}
	}
	
	
	private void oneStep() {
		String curCommand;
		String curProcess;
		String pageId;
		String curCommandFixed;
		List<String> pageData = new LinkedList<String>();

		while(commandsIterator.hasNext()) {
			curCommand = commandsIterator.next();
			if(curCommand.startsWith("PF:")) {
				pfAmount++;
			}
			else if(curCommand.startsWith("PR:")) {
				prCommands.add(curCommand);	
			}
			else if(curCommand.startsWith("GP:")) {
				if(realPfAmount < pfAmount) {
					realPfAmount++;
				}
				curProcess = curCommand.substring(4, curCommand.length()).split(" ")[0];
				pageId = curCommand.split(" ")[1];
				if(prCommands.size() > 0) {
					String pr = prCommands.get(0);
					String pageToHd = pr.split(" ")[1];
					Integer pageToHdInt = Integer.parseInt(pageToHd);
					String pageToRam = pr.split(" ")[3];
					if(pageId.equals(pageToRam)) {
						if(memoryMap.contains(pageToHdInt)) {
							memoryMap.remove(pageToHdInt);
						}
						prAmount++;
						prCommands.remove(0);
					}
				}
				if(selectedProcess.contains(curProcess)) {
					Integer pageIdInt = Integer.parseInt(pageId);
					if(!memoryMap.contains(pageIdInt)) {
						memoryMap.add(pageIdInt);						
					}
					pageData.add(pageId);
					curCommandFixed = curCommand.replaceAll("[,\\[\\]]", "");
					for(int i = 0; i<BYTES_IN_PAGE; i++) {
						pageData.add(curCommandFixed.split(" ")[2+i]);
					}
					updateTable(pageData);
				}
				else {updateTable(null);}
				break;
			}
			else {continue;}
		}
		if(!commandsIterator.hasNext()) {
			enablePlay(false);
		}
		updateFields();
	}

	
	private void enablePlay(boolean flag) {
		Display.getDefault().syncExec(new Runnable(){
			@Override
			public void run() {
				if(shell.getDisplay() != null && !shell.getDisplay().isDisposed()) { 
					playButton.setEnabled(flag);
					playAllButton.setEnabled(flag);
				}
			}
		});
	}
	
	private void enableStepBackAndReset(boolean flag) {
		Display.getDefault().syncExec(new Runnable(){
			@Override
			public void run() {
				if(shell.getDisplay() != null && !shell.getDisplay().isDisposed()) { 
					resetButton.setEnabled(flag);
					stepBackButton.setEnabled(flag);
				}
			}
		});
	}
	
	public void addProcesses(Integer numOfProcess) {
		Display.getDefault().syncExec(new Runnable(){
			@Override
			public void run() {
				if(shell.getDisplay() != null && !shell.getDisplay().isDisposed()) { 
					for(Integer i=0; i < numOfProcess; i++) {
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
