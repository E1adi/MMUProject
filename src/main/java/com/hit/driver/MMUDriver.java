package com.hit.driver;

import java.lang.reflect.InvocationTargetException;
import com.hit.view.CLI;
import com.hit.view.MMUView;
import com.hit.controller.MMUController;
import com.hit.model.MMUModel;


public class MMUDriver {

//	Throws:
//		java.lang.InterruptedException
//		java.lang.reflect.InvocationTargetException
	public static void main(String[] args) 
					 throws InterruptedException,
							InvocationTargetException {
		
		CLI cli = new CLI(System.in, System.out);
		String[] configuration;
		
		while ((configuration = cli.getConfiguration()) != null) {
			
			MMUModel model = new MMUModel(configuration);
			MMUView view = new MMUView();
			MMUController controller = new MMUController(model, view);
			model.addObserver(controller);
			view.addObserver(controller);
			model.start();
		}
	}
}
