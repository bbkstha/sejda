/*
 * Created on Jul 4, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.cli;

import java.io.PrintStream;

import org.sejda.core.exception.SejdaRuntimeException;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;

/**
 * Main entry point for the sejda command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class SejdaConsoleMain {
    /**
     * Executable binary name
     */
    public static final String NAME = "sejda-console";

    public static void main(String[] args) {
        new SejdaConsoleMain().execute(args);
    }

    /**
     * Executes the sejda command line interface, using specified parameters as input
     * 
     * @param args
     *            command line arguments as strings
     */
    public void execute(final String[] args) {
        try {
            final SejdaConsoleArguments arguments = new SejdaConsoleArguments(args);
            final SejdaCli<GeneralCliArguments> generalCli = SejdaCli.newGeneralOptionsCli(arguments);

            // no command specified, print general Help
            if (!generalCli.getParsedArguments().isCommand()) {
                println(generalCli.getHelpMessage());
                return;
            }

            String commandName = generalCli.getParsedArguments().getCommand();
            SejdaCli<? extends CommandCliArguments> commandCli = SejdaCli.newCommandOptions(arguments, commandName);

            // print command specific help
            if (generalCli.getParsedArguments().isHelp()) {
                println(commandCli.getHelpMessage());
                return;
            }

            // execute command
            getTaskExecutionFacade().executeCommand(commandCli.getParsedArguments(), commandName);
        } catch (ArgumentValidationException e) {
            printError(e);
            println(e.getMessage());
        } catch (SejdaRuntimeException e) {
            printError(e);
            println(e.getMessage());
        }
    }

    /**
     * Prints the specified error to default err print stream
     * 
     * @param e
     */
    private void printError(Exception e) {
        e.printStackTrace(getDefaultErrorPrintStream());
    }

    private PrintStream getDefaultErrorPrintStream() {
        return System.err;
    }

    private PrintStream getDefaultOutPrintStream() {
        return System.out;
    }

    /**
     * Prints the specified line to the default out print stream
     * 
     * @param line
     */
    private void println(String line) {
        getDefaultOutPrintStream().println(line);
    }

    private final CommandExecutionService taskExecutionFacade = new DefaultCommandExecutionService();

    CommandExecutionService getTaskExecutionFacade() {
        return taskExecutionFacade;
    }
};