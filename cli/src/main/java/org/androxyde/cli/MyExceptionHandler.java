package org.androxyde.cli;

import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParseResult;

public class MyExceptionHandler implements IExecutionExceptionHandler {

	@Override
	public int handleExecutionException(Exception ex, CommandLine commandLine, ParseResult parseResult)
			throws Exception {
		// TODO Auto-generated method stub
		return commandLine.getCommandSpec().exitCodeOnExecutionException();
	}

}
