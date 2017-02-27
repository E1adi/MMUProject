package com.hit.view;


import java.io.PrintWriter;
import java.util.Scanner;


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
		
		write("Please write start to begin or stop to exit");
		userInput = read();
		while(!userInput.toLowerCase().equals("start")) {
			if(userInput.toLowerCase().equals("stop")) {				// Typed stop
				write("Thank you.");
				return null;
			}
			else { 														// Typed something which is not start or stop
				write("Not a valid commnad, available commands are (start/stop).");
			}
			userInput = read();
		}
		do {
			invalidInput = true;
			write("Please insert required algorithm and RAM capacity.");
			write("Allowed algorithms are: LRU, MFU and Second Chance.");
			userInput = read();
			splitedUserInput = userInput.split(" ");
			if(splitedUserInput[0].toLowerCase().equals("second")) {			// first word is second
				if(splitedUserInput.length > 3) {								// but too much word (if second was the first word so word count need to be 3)
					write("Invalid input, too many arguments.");
					continue;
				}
				if(splitedUserInput[1].toLowerCase().equals("chance")) {		// second word is chance
					requestedAlgorithm = new String("Second Chance");
				}
				else {
					write("Invalid algorithm.");								// first word is second, second word is not chance
					continue;
				}
			}
			else {
				if(splitedUserInput.length > 2) {								// if first word is not second, then the word count must be 2
					write("Invalid input, too many arguments.");
					continue;
				}
				requestedAlgorithm = splitedUserInput[0].toUpperCase();
			}
			if(!requestedAlgorithm.equals("LRU") && 							// ensuring it is one of the possible algorithms
			   !requestedAlgorithm.equals("MFU") && 
			   !requestedAlgorithm.equals("MRU") && 
			   !requestedAlgorithm.equals("Second Chance")) {
				
				write("Invalid algorithm.");
				continue;
			}
			try {
				ramCapacity = Integer.parseInt(splitedUserInput[splitedUserInput.length-1]);
				if(ramCapacity < 1) {
					write("Invalid RAM capacity.");												// inserted ram capacity is zero or below
					continue;
				}
				invalidInput = false;
			} catch(NumberFormatException e) {
				write("Invalid RAM capacity. (Please try to use numbers).");					// inserted ram capacity cannot be translated to a number
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
