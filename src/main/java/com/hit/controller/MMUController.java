package com.hit.controller;

import java.util.Observable;
import java.util.Observer;

import com.hit.model.MMUModel;
import com.hit.model.Model;
import com.hit.util.MMULogger;
import com.hit.view.MMUView;
import com.hit.view.View;

public class MMUController extends Object implements Controller, Observer {
	
	Model model;
	View view;

	public MMUController(Model model, View view) {
		this.model = model;
		this.view = view;
	}
	
	@Override
	public void update(Observable o, Object arg1) {
		if(o == model) {
			view.open();
		}
		if(o == view) {
			model.readData();
			((MMUView)view).addProcesses(((MMUModel)model).numProcesses);
			((MMUView)view).setConfiguration(((MMUModel)model).getCommands());
		}
	}
}
