package fini.main;

import fini.main.model.FiniParser;
import fini.main.model.Storage;

public class Brain {
	private static Brain brain;
	
	private Storage taskOrganiser;
	private FiniParser parser;
	
	private Brain() {
		parser = FiniParser.getInstance();
		taskOrganiser = Storage.getInstance();
	}
	
	public void executeCommand(String command) {
		
	}
}
