package com.patrickbuf;

import java.io.IOException;
import java.nio.file.Path;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.Spec;

@Command(name = "patrickc", version = "patrickc 1.0", mixinStandardHelpOptions = true)
public class Patrickc implements Runnable {

  @ArgGroup(multiplicity = "1", exclusive = false)
  SourceCodeFilesToGenerate filesOut;

  @Spec CommandSpec spec;

  @Parameters(index = "0", description = "Pathname of the .pbdefn file to compile.")
  private Path pbdefn;

  public static void main(String[] args) {
    int exitCode = new CommandLine(new Patrickc()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public void run() {
    ParsedPbdefn parsedPbdefn = null;
    try {
      parsedPbdefn = PbdefnParser.parse(pbdefn);
    } catch (IOException | InvalidPbdefnException e) {
      System.out.println(String.format("Failed to parse pbdefn. Reason: %s", e));
      return;
    }

    ParseResult pr = spec.commandLine().getParseResult();

    if (pr.hasMatchedOption("--java_out")) {
      try {
        JavaSourceGen.generate(parsedPbdefn, filesOut.javaOut);
        System.out.println("Generated Java source file sucessfully.");
      } catch (IOException e) {
        System.out.println(String.format("Failed to generate Java source file. Reason: %s", e));
      }
    }
  }

  static class SourceCodeFilesToGenerate {
    @Option(
        names = {"--java_out"},
        description = "Generate Java source code file under this directory pathname")
    Path javaOut;
  }
}
