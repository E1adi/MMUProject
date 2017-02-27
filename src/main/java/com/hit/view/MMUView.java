package com.hit.view;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

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
	private TableColumn[] tableCols;
	private TableItem[] tableRows;
	private List<String> prCommands;
	private List<Integer> memoryMap;
	private List<Integer> lastMemoryMap;
	private Stack<State> undoStack;
	private Stack<State> redoStack;
	
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
		shell.setBounds((screenSize.width/2) - (width/2), (screenSize.height/2) - (height/2), width, height);    // Centering the window position
		shell.setText("MMU Simulator");
		
		
		// Page table area (Container)
		tableArea = new Composite(shell, SWT.NO_FOCUS);
		gridData = new GridData(SWT.FILL, SWT.FILL, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 1, 2);
		gridData.widthHint = (int)(width * (8.0/11));  
		gridData.heightHint = (int)(height * (4.0/6));
		tableArea.setLayoutData(gridData);
		tableArea.setLayout(new GridLayout(1, true));
		
		// Process selection area (Container)
		processSelect = new Composite(shell, SWT.NO_FOCUS);
		gridData = new GridData(SWT.FILL, SWT.FILL, ALLOW_SPAN_HORIZONAL, ALLOW_SPAN_VERTICAL, 1, 2);
		gridData.widthHint = (int)(width * (3.0/11));
		gridData.heightHint = (int)(height * (4.0/6));
		processSelect.setLayoutData(gridData);
		processSelect.setLayout(new GridLayout(1, true));
		
		
		// Buttons and statistics area (Container)
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
	     
	    
	    // Processes Selection area components
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
	    
	    
	    // Events for pressing buttons and selecting processes.
	    playButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				if(redoStack.size() > 0) {
					undoStack.push(saveState());
					doASavedStateStep(redoStack.pop());
				}
				else
					oneStep();
				enableStepBackAndReset(true);
			}
	    });
	    
	    playAllButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				while(redoStack.size() > 0) {
					undoStack.push(saveState());
					doASavedStateStep(redoStack.pop());
				}
				while(commandsIterator.hasNext()) {
					oneStep();
				}
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
	    
	    stepBackButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				redoStack.push(saveState());
				doASavedStateStep(undoStack.pop());
				enablePlay(true);
				if(undoStack.size() == 0) {
					enableStepBackAndReset(false);
				}
			}
	    });
	    
	    shell.open ();
	    shell.forceActive();
	    shell.forceFocus();

		setChanged();
		notifyObservers("Ready");
		
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
		setChanged();
		notifyObservers("Done");		
	}

	private void setPreConfiguration(List<String> commands) {				// common to reset button and setConfiguration method. reusing code.
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
		undoStack = new Stack<State>();
		redoStack = new Stack<State>();
	}
	
	public void setConfiguration(List<String> commands) {
		
		setPreConfiguration(commands);
		
		tableCols = new TableColumn[NUM_MMU_PAGES];
		tableRows = new TableItem[BYTES_IN_PAGE];
		

		if(shell.getDisplay() != null && !shell.getDisplay().isDisposed()) {			// building page table
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
	
	private void doASavedStateStep(State lastState) {				// for undo/redo executions.	
			
		List<List<String>> tableInfo = lastState.getTableInfo();
		
		lastMemoryMap.clear();
		lastMemoryMap.addAll(lastState.getLastMemoryMap());
		memoryMap.clear();
		memoryMap.addAll(lastState.getMemoryMap());
		prCommands.clear();
		prCommands.addAll(lastState.getPrCommands());
		
		pfAmount = lastState.getPfAmount();
		prAmount = lastState.getPrAmount();
		realPfAmount = lastState.getRealPfAmount();
		
		
		
		for(int i = 0; i<NUM_MMU_PAGES; i++) {
			tableCols[i].setText(tableInfo.get(i).get(0));
			for(int j=0; j<BYTES_IN_PAGE; j++) {
				tableRows[j].setText(i, tableInfo.get(i).get(j+1));
			}
		}
		
		updateFields();
		enablePlay(true);
	}
	
	private void clearAll() {					// reset all flow data
		pfAmount = 0;
		realPfAmount = 0;
		prAmount = 0;
		prCommands.clear();;
		memoryMap.clear();
		lastMemoryMap.clear();
		setPreConfiguration(this.commands);
		
		updateFields();
		
		if(shell.getDisplay() != null && !shell.getDisplay().isDisposed()) {
			for(int i = 0; i<NUM_MMU_PAGES; i++) {
				tableCols[i].setText("");
				for(int j = 0; j < BYTES_IN_PAGE; j++) {
					tableRows[j].setText(i, "");
				}
			}
		}
	}
	
	
	private State saveState() {					// Save current state
		State lastState = new State();
		
		lastState.setLastMemoryMap(lastMemoryMap);
		lastState.setMemoryMap(memoryMap);
		lastState.setPfAmount(pfAmount);
		lastState.setPrAmount(prAmount);
		lastState.setRealPfAmount(realPfAmount);
		lastState.setPrCommands(prCommands);
		lastState.setTableInfo(tableCols, tableRows);
		
		return lastState;
	}
	
	private void oneStep() {					// Do one parsing step.
		String curCommand;
		String curProcess;
		String pageId;
		String curCommandFixed;
		List<String> pageData = new LinkedList<String>();
		
		undoStack.push(saveState());
		
		while(commandsIterator.hasNext()) {
			curCommand = commandsIterator.next();
			if(curCommand.startsWith("PF:")) {			// amount of PF lines parsed from data until now.
				pfAmount++;
			}
			else if(curCommand.startsWith("PR:")) {		// saving each PR command to know which page to switch out from the table when the table is full.
				prCommands.add(curCommand);	
			}
			else if(curCommand.startsWith("GP:")) {		
				if(realPfAmount < pfAmount) {			// realPFAmount is the matching for each PF line a GP line
					realPfAmount++;
				}
				curProcess = curCommand.substring(4, curCommand.length()).split(" ")[0];	// get current GP command process number
				pageId = curCommand.split(" ")[1];											// get current GP command page id
				if(prCommands.size() > 0) {													// Checking if the first PR command match to the current GP command by matching GP page id with PR MTR
					String pr = prCommands.get(0);
					String pageToHd = pr.split(" ")[1];
					Integer pageToHdInt = Integer.parseInt(pageToHd);
					String pageToRam = pr.split(" ")[3];
					if(pageId.equals(pageToRam)) {
						if(memoryMap.contains(pageToHdInt)) {								// If it is then set the MTH page of this PR command to be switch out from the table by removing him from the memory map 
							memoryMap.remove(pageToHdInt);
						}
						prAmount++;					
						prCommands.remove(0);
					}
				}
				if(selectedProcess.contains(curProcess)) {									// if this GP command is belong to a process that i am interested in,  
					Integer pageIdInt = Integer.parseInt(pageId);
					if(!memoryMap.contains(pageIdInt)) {										// if the page id of the current GP command is not already in table, add it to memory map
						memoryMap.add(pageIdInt);						
					}
					pageData.add(pageId);
					curCommandFixed = curCommand.replaceAll("[,\\[\\]]", "");					// Reassemble page id and page data into a list of String with length of BYTES_IN_PAGE +1
					for(int i = 0; i<BYTES_IN_PAGE; i++) {
						pageData.add(curCommandFixed.split(" ")[2+i]);
					}
					updateTable(pageData);														// update table with the new page
				}
				else {updateTable(null);}													// if this GP command is  not belong to a process that i am interested in, 
				break;																			// update table with no new page to add.
			}
			else {continue;}															// if its not a GP, PR or PF command, continue to parse the next line.
		}
		if(!commandsIterator.hasNext()) {												// if this is the last line in data, disable play buttons
			enablePlay(false);
		}
		updateFields();																// update PR and PF labels 
	}

	private void updateTable(List<String> data) {				// Update table with a given new row.
		
		if(shell.getDisplay() != null && !shell.getDisplay().isDisposed()) { 
			
			Integer pagePos;
			
			// Redraw table by new RAM memory map.
			// Removing pages which are removed from memory map and left aligning all the pages that was positioned right to the removed pages.
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
				// blanking all columns that should not be in use
				for(int i = memoryMap.size(); i < NUM_MMU_PAGES; i++) {		
					tableCols[i].setText("");
					for(int j = 0; j < BYTES_IN_PAGE; j++) {
						tableRows[j].setText(i, "");
					}
				}
			}
			lastMemoryMap.clear();
			lastMemoryMap.addAll(memoryMap);		// update last memory map to be the current memory map
			
			if(data != null) {
				Iterator<String> dataIter = data.iterator();
				String pageId = dataIter.next();
				Integer pageIdInt = Integer.parseInt(pageId);
				Integer pageColIndex = memoryMap.indexOf(pageIdInt);
				String curBite;
				
				//	if there is a page to add, add it
				// in data, first string is column name, and the others string representing page data in bytes.
				tableCols[pageColIndex].setText(pageId);	
				for(int i = 0; i < BYTES_IN_PAGE; i++) {
					curBite = dataIter.next();
					tableRows[i].setText(pageColIndex, curBite);
				}
			}
		}
	}
	
	
	private void updateFields() {							// Updating statistics fields
		
		if(shell.getDisplay() != null && !shell.getDisplay().isDisposed()) { 
			pageFaultText.setText(realPfAmount.toString());
			pageReplacementText.setText(prAmount.toString());
		}
	}
	
	private void enablePlay(boolean flag) {					// Enable play and play all buttons
		if(shell.getDisplay() != null && !shell.getDisplay().isDisposed()) { 
			playButton.setEnabled(flag);
			playAllButton.setEnabled(flag);
		}
	}
	
	private void enableStepBackAndReset(boolean flag) {		// enable step back and reset
		if(shell.getDisplay() != null && !shell.getDisplay().isDisposed()) { 
			resetButton.setEnabled(flag);
			stepBackButton.setEnabled(flag);
		}
	}
	
	public void addProcesses(Integer numOfProcess) {		// add process numbers to process selection list
		if(shell.getDisplay() != null && !shell.getDisplay().isDisposed()) {
			for(Integer i=0; i < numOfProcess; i++) {
				processChoose.add("Process " + i.toString());
			}
		}
	}
	
	@Override
	public void open() {
		createAndShowGui();
	}
	
	
	// A class used to save States of running program.
	private class State {
		private List<String> prCommands;
		private List<Integer> memoryMap;
		private List<Integer> lastMemoryMap;	
		private Integer pfAmount;
		private Integer realPfAmount;
		private Integer prAmount;
		private ArrayList<List<String>> tableInfo;

		

		public ArrayList<List<String>> getTableInfo() {
			return tableInfo;
		}
		
		// read the current page table info and store it in a matrix.
		public void setTableInfo(TableColumn[] tableCols, TableItem[] tableRows) {
			for(int i=0; i<NUM_MMU_PAGES; i++) {
				(tableInfo.get(i)).add(tableCols[i].getText());
				for(int j=0; j<BYTES_IN_PAGE; j++) {
					(tableInfo.get(i)).add(tableRows[j].getText(i));
				}
			}
		}

		public State() {
			prCommands = new ArrayList<String>();
			memoryMap = new LinkedList<Integer>();
			lastMemoryMap = new LinkedList<Integer>();
			tableInfo = new ArrayList<List<String>>(NUM_MMU_PAGES);
			for(int i=0; i<NUM_MMU_PAGES; i++) {
				tableInfo.add(new LinkedList<String>());
			}
		}
		
		public List<String> getPrCommands() {
			return prCommands;
		}
		public void setPrCommands(List<String> prCommands) {
			this.prCommands.clear();
			if(prCommands != null)
				this.prCommands.addAll(prCommands);
		}
		public List<Integer> getMemoryMap() {
			return memoryMap;
		}
		public void setMemoryMap(List<Integer> memoryMap) {
			this.memoryMap.clear();
			if(memoryMap != null)
				this.memoryMap.addAll(memoryMap);
		}
		public List<Integer> getLastMemoryMap() {
			return lastMemoryMap;
		}
		public void setLastMemoryMap(List<Integer> lastMemoryMap) {
			this.lastMemoryMap.clear();
			if(lastMemoryMap != null)
				this.lastMemoryMap.addAll(lastMemoryMap);
		}
		public Integer getPfAmount() {
			return pfAmount;
		}
		public void setPfAmount(Integer pfAmount) {
			this.pfAmount = pfAmount;
		}
		public Integer getRealPfAmount() {
			return realPfAmount;
		}
		public void setRealPfAmount(Integer realPfAmount) {
			this.realPfAmount = realPfAmount;
		}
		public Integer getPrAmount() {
			return prAmount;
		}
		public void setPrAmount(Integer prAmount) {
			this.prAmount = prAmount;
		}
		
	}
}
