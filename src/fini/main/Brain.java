package fini.main;

import fini.main.model.FiniParser;
import fini.main.model.Storage;
import fini.main.view.RootController;

public class Brain {
	private static Brain brain;
	private RootController rootController;
	
	private Storage taskOrganiser;
	private FiniParser parser;
	
	private Brain() {
		parser = FiniParser.getInstance();
		taskOrganiser = Storage.getInstance();
	}
	
	public static Brain getInstance() {
		if (brain == null) {
			brain = new Brain();
		}
		return brain;
	}
	
	public void executeCommand(String command) {
		
	}
	
	public void setRootController(RootController rootController) {
		this.rootController = rootController;
	}
}
