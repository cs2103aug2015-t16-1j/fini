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
		boolean isOperationSuccessful;
		isOperationSuccessful = parser.parse(command);

		// Change all of these hierarchy: TODO
		rootController.updateMainDisplay(taskOrganiser.getTasks());
		rootController.updateProjectsOverviewPanel(taskOrganiser.getTasks());
		rootController.updateDisplayToUser(isOperationSuccessful);
		taskOrganiser.updateFile();
		// updateTasksOverviewPanel(taskOrganiser.getTasks());

		//      Handle Using Command Class: TODO
		//		Command newCommand = new Command(command);
		//		
		//		Command.Type commandType = newCommand.getCommandType();
		//		String commandAction = newCommand.getCommandAction();
		//		String commandArguments = newCommand.getCommandArguments();
	}

	public void setRootController(RootController rootController) {
		this.rootController = rootController;
	}
}
