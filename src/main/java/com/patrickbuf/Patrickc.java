package com.patrickbuf;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;

@Command(name = "patrickc", version = "patrickc 1.0", mixinStandardHelpOptions = true)
public class Patrickc implements Runnable {

    static class SourceCodeFilesToGenerate {
        @Option(names = { "--java_out" }, description = "Generate Java source code file under this directory pathname")
        File java_out;
    }

    @ArgGroup(multiplicity = "1", exclusive = false)
    SourceCodeFilesToGenerate files_out;

    @Parameters(index = "0", description = "Pathname of the .pbdefn file to compile.")
    private File pbdefn;

    @Override
    public void run() {
        // TODO: Add compiler logic here
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Patrickc()).execute(args);
        System.exit(exitCode);
    }
}