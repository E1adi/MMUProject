package com.hit.view;


import java.io.PrintWriter;
import java.util.Scanner;

import com.hit.util.MMULogger;

public class CLI {

	private Scanner in;
	private PrintWriter out;
	
	public CLI(java.io.InputStream in,
			   java.io.OutputStream out) {
		this.in = new Scanner(in);
		this.out = new PrintWriter(out);
	}
	
	private String read() {
		return in.nextLine();
	}
	
	public String[] getConfiguration() {
		String userInput, requestedAlgorithm = null;
		boolean invalidInput = true;
		String[] splitedUserInput;
		Integer ramCapacity = null;
		
		write("Please write start to start or stop to exit");
		userInput = read();
		while(!userInput.toLowerCase().equals("start")) {
			if(userInput.toLowerCase().equals("stop")) {
				write("Thank you.");
				MMULogger logger = MMULogger.getInstance();
				logger.close();
				System.exit(0);
			}
			else { 
				write("Not a valid commnad, Please try again.");
			}
			userInput = read();
		}
		do {
			invalidInput = true;
			write("Please insert required algorithm and RAM capacity.");
			write("Allowed algorithms are: LRU, MFU, MRU and Second Chance.");
			userInput = read();
			splitedUserInput = userInput.split(" ");
			if(splitedUserInput[0].toLowerCase().equals("second")) {
				if(splitedUserInput.length > 3) {
					write("Invalid input, too many arguments.");
					continue;
				}
				if(splitedUserInput[1].toLowerCase().equals("chance")) {
					requestedAlgorithm = new String("Second Chance");
				}
				else {
					write("Invalid algorithm.");
					continue;
				}
			}
			else {
				if(splitedUserInput.length > 2) {
					write("Invalid input, too many arguments.");
					continue;
				}
				requestedAlgorithm = splitedUserInput[0].toUpperCase();
			}
			if(!requestedAlgorithm.equals("LRU") && 
			   !requestedAlgorithm.equals("MFU") && 
			   !requestedAlgorithm.equals("MRU") && 
			   !requestedAlgorithm.equals("Second Chance")) {
				
				write("Invalid algorithm.");
				continue;
			}
			try {
				ramCapacity = Integer.parseInt(splitedUserInput[splitedUserInput.length-1]);
				if(ramCapacity < 1) {
					write("Invalid RAM capacity.");
					continue;
				}
				invalidInput = false;
			} catch(NumberFormatException e) {
				write("Invalid RAM capacity.");
				continue;
			}
		} while (invalidInput);
		
		write("Processing...");
		return new String[]{requestedAlgorithm, ramCapacity.toString()};
	}	
	

	
	public void write(String string) {
		out.println(string);
		out.flush();
	}
	
}
