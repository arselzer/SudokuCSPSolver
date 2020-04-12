import org.apache.commons.cli.*;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(
                Option.builder()
                        .argName("instance")
                        .longOpt("instance")
                        .hasArg()
                        .desc("CSP problem instance")
                        .optionalArg(false)
                        .type(String.class)
                        .build()
        );

        HelpFormatter helpFormatter = new HelpFormatter();

        CommandLineParser parser = new DefaultParser();
        CommandLine cli = null;
        try {
            cli = parser.parse( options, args);
        } catch (ParseException e) {
            System.err.println("Error: could not parse commandline arguments: " + e.getMessage());
            System.exit(1);
        }

        if (!cli.hasOption("i")) {
            helpFormatter.printHelp(args[0], options);
        }

        String filePath = cli.getOptionValue("instance");

        CSPSolver solver = new CSPSolver(Path.of(filePath));


    }
}
