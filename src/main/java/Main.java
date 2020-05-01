import org.apache.commons.cli.*;

import java.io.File;

public class Main {
    private static HelpFormatter helpFormatter;
    private static Options options;

    private static void printUsage() {
        helpFormatter.printHelp("java -jar " + System.getProperty("sun.java.command"), options);
        System.exit(1);
    }

    public static void main(String[] args) throws Exception {
        options = new Options();
        options.addOption(
                Option.builder()
                        .argName("i")
                        .longOpt("instance")
                        .hasArg()
                        .desc("CSP problem instance")
                        .optionalArg(false)
                        .type(String.class)
                        .build()
        );

        options.addOption(
                Option.builder()
                        .argName("b")
                        .longOpt("benchmark")
                        .hasArg()
                        .desc("Folder with instances to run benchmarks on")
                        .optionalArg(false)
                        .type(String.class)
                        .build()
        );

        options.addOption(
                Option.builder()
                        .argName("m")
                        .longOpt("method")
                        .hasArg()
                        .desc("One of the following methods: [BT|FC|FC-DO]")
                        .optionalArg(false)
                        .type(String.class)
                        .build()
        );


        helpFormatter = new HelpFormatter();

        CommandLineParser parser = new DefaultParser();
        CommandLine cli = null;
        try {
            cli = parser.parse( options, args);
        } catch (ParseException e) {
            System.err.println("Error: could not parse commandline arguments: " + e.getMessage());
            System.exit(1);
        }

        if(cli.getOptions().length == 2 && cli.hasOption("instance") && cli.hasOption("method")) {
            String filePath = cli.getOptionValue("instance");
            ICSPSolver solver = new SudokuCSPSolver(new File(filePath));

            String method = cli.getOptionValue("method");
            switch (method) {
                case "BT":
                    solver.solveUsingBacktracking(true);
                    break;
                case "FC":
                    solver.solveUsingForwardChecking(true);
                    break;
                case "FC-DO":
                    solver.solveUsingForwardCheckingDynamicallyOrdered(true);
                    break;
                default:
                    System.err.print("Error: one of [BT|FC|FC-DO] must be provided as a method!");
                    System.exit(1);
                    break;
            }
        }else if(cli.getOptions().length == 1 && cli.hasOption("benchmark")) {
            String folderPath = cli.getOptionValue("benchmark");
            File folder = new File(folderPath);
            File[] files = folder.listFiles();

            if(files != null){
                System.out.println("file_name,backtracking_ms,forward_checking_ms,FC_DO_ms");
                for (File file : files) {
                    if(file != null && file.isFile()) {
                        ICSPSolver solver = new SudokuCSPSolver(file);
                        solver.benchmark();
                    }
                }
            }else {
                // User didn't provide a proper directory
                System.err.println("Error: Invalid dir provided!");
                System.exit(1);
            }
        }else {
            printUsage();
        }

    }
}
