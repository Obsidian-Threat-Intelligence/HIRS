package hirs.swid.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Commander is a class that handles the command line arguments for the SWID
 * Tags gateway.
 */
public class Commander {

    private static final String COMMAND_PREFIX = "-";
    private static final String FULL_COMMAND_PREFIX = "--";
    private static final String CREATE_STRING = "create";
    private static final String VERIFY_STRING = "verify";
    private static final String HELP_STRING = "help";
    private static final String PARSE_STRING = "parse";
    private static final String ATTRIBUTES_STRING = "attributes";
    private static final String KEYSTORE_STRING = "keystore";
    private static final String SHOW_CERT_STRING = "show-cert";

    private boolean hasArguments = false;
    private boolean validate = false;
    private boolean create = false;
    private boolean parse = false;
    private boolean attributesGiven = false;
    private boolean keystoreGiven = false;
    private boolean showCert = false;

    private String validateFile;
    private String createOutFile = "";
    private String parseFile;
    private String attributesFile = "";
    private String keystore = "";
    private String hashAlg = null;

    /**
     * The main constructor for the Commander class.
     *
     * @param args command line injected data.
     */
    public Commander(final String[] args) {
        hasArguments = args.length > 0;

        if (hasArguments) {
            parseArguments(args);
        } else {
            printHelp();
        }

        if (create) {
            if (hashAlg == null) {
                hashAlg = "256";
            }

            if (!getCreateOutFile().isEmpty() && !isValidPath(getCreateOutFile())) {
                printHelp(String.format("Invalid file path %s!", getCreateOutFile()));
            }
        }
    }

    /**
     * The default blank constructor.
     */
    public Commander() {

    }

    /**
     * This method is called if an empty Commander was created, and later gets
     * args. Will be used by the main constructor.
     *
     * @param args command line injected data.
     */
    public final void parseArguments(final String[] args) {
        String tempValue;

        for (int i = 0; i < args.length; i++) {
            tempValue = args[i];

            switch (tempValue) {
                case FULL_COMMAND_PREFIX + CREATE_STRING:
                case COMMAND_PREFIX + "c":
                    create = true;
                    if (i + 1 < args.length
                            && !args[i + 1].substring(0, 1).equals(COMMAND_PREFIX)) {
                        createOutFile = args[++i];
                    }
                    break;
                case FULL_COMMAND_PREFIX + ATTRIBUTES_STRING:
                case COMMAND_PREFIX + "a":
                    attributesGiven = true;
                    if (i + 1 < args.length
                            && !args[i + 1].substring(0, 1).equals(COMMAND_PREFIX)) {
                        attributesFile = args[++i];
                    }
                    break;
                case FULL_COMMAND_PREFIX + VERIFY_STRING:
                case COMMAND_PREFIX + "v":
                    validate = true;
                    validateFile = args[++i];
                    break;
                case FULL_COMMAND_PREFIX + PARSE_STRING:
                case COMMAND_PREFIX + "p":
                    parse = true;
                    parseFile = args[++i];
                    break;
                case FULL_COMMAND_PREFIX + SHOW_CERT_STRING:
                    showCert = true;
                    break;
                case FULL_COMMAND_PREFIX + HELP_STRING:
                case COMMAND_PREFIX + "h":
                default:
                    printHelp();
            }
        }
    }

    /**
     * Getter for the input validate file associated with the validate flag.
     *
     * @return string of the associated file.
     */
    public final String getValidateFile() {
        return validateFile;
    }

    /**
     * Getter for the output file for the create flag.
     *
     * @return string path of the outfile.
     */
    public final String getCreateOutFile() {
        return createOutFile;
    }

    /**
     * Getter for the property that indicates if something was given at the
     * commandline.
     *
     * @return flag for the present of arguments
     */
    public final boolean hasArguments() {
        return hasArguments;
    }

    /**
     * Getter for the validate command flag.
     *
     * @return flag indicating to validate file.
     */
    public final boolean validate() {
        return validate;
    }

    /**
     * Getter for the create command flag.
     *
     * @return flag for creating a swid tag.
     */
    public final boolean create() {
        return create;
    }

    /**
     * Getter for the hash algorithm to be used for hash functions.
     *
     * @return string of the algorithm
     */
    public final String getHashAlg() {
        return hashAlg;
    }

    /**
     * Getter for the parse command flag.
     *
     * @return flag to parse swid
     */
    public final boolean parse() {
        return parse;
    }

    /**
     * Getter for the file to be parsed by the parse command flag.
     *
     * @return string of path to parse
     */
    public final String getParseFile() {
        return parseFile;
    }

    /**
     * Getter for the attributes file given flag.
     *
     * @return flag for the attributes
     */
    public boolean isAttributesGiven() {
        return attributesGiven;
    }

    /**
     * Getter for the file containing attribute key-value pairs.
     *
     * @return string path of file containing attributes
     */
    public String getAttributesFile() {
        return attributesFile;
    }

    /**
     * Getter for the keystore given flag.
     *
     * @return flag for the presence of the keystore.
     */
    public boolean isKeystoreGiven() {
        return keystoreGiven;
    }

    /**
     * Getter for the keystore used for digital signatures.
     *
     * @return string of the path to the keystore.
     */
    public String getKeystore() {
        return keystore;
    }

    /**
     * Getter for boolean to show certificate data or not.
     *
     * @return flag to display the certificate
     */
    public boolean isShowCert() {
        return showCert;
    }

    /**
     * Default no parameter help method.
     */
    private void printHelp() {
        printHelp(null);
    }

    /**
     * This method is used to inform the user of the allowed functionality of
     * the program.
     */
    @SuppressFBWarnings("DM_EXIT")
    private void printHelp(final String message) {
        StringBuilder sb = new StringBuilder();

        if (message != null && !message.isEmpty()) {
            sb.append(String.format("ERROR: %s%n%n", message));
        }
        sb.append("Usage: HIRS_SwidTag\n");
        sb.append("   -c, --create <file>\t\tCreate a base rim and write to\n"
                + "   \t\t\t\tthe given file. If no file is given the default is\n"
                + "   \t\t\t\tgenerated_swidTag.swidtag\n\n");
        sb.append("   -a, --attributes <file>\tSpecify the JSON file that contains\n"
                + "   \t\t\t\tthe xml attributes to add to the RIM\n\n");
        sb.append("   -v, --verify\t\t\tTakes the provided input file and\n"
                + "   \t\t\t\tvalidates it against the schema at\n"
                + "   \t\t\t\thttp://standards.iso.org/iso/19770/-2/2015/schema.xsd\n\n");
        sb.append("   -p, --parse <file>\t\tParse the given swidtag's payload\n\n");
/*
        sb.append("   -k, --keystore <file>\tSpecify the keystore and its location to use\n"
                + "   \t\t\t\tfor digital signatures\n");
 */
        sb.append("   --show-cert\t\t\tPrint the certificate in the signature block of\n"
                + "   \t\t\t\tthe base RIM\n\n");
        sb.append("   -h, --help, <no args>\tPrints this command help information.\n");
        sb.append("   \t\t\t\tListing no command arguments will also\n"
                + "   \t\t\t\tprint this help text.\n\n");
        sb.append("Example commands: \n"
                + "   Create a base rim from the default attribute file and write the rim\n"
                + "   to generated_swidTag.swidtag:\n\n"
                + "   \t\tjava -jar tcg_rim_tool-1.0.jar -c\n\n"
                + "   Create a base rim from the values in config.json and write the rim\n"
                + "   to base_rim.swidtag:\n\n"
                + "   \t\tjava -jar tcg_rim_tool-1.0.jar -c base_rim.swidtag -a config.json\n\n"
                + "   ");

        System.out.println(sb.toString());
        System.exit(1);
    }

    /**
     * Checks that the file given to create a new swidtag is a valid path.
     *
     * @param filepath string of the location of the file.
     * @return flag to indiate file exist
     */
    public static boolean isValidPath(final String filepath) {
        boolean result = false;
        try {
            System.out.println("Checking for a valid creation path...");
            File file = new File(filepath);
            result = file.createNewFile();
        } catch (IOException | InvalidPathException | NullPointerException ex) {
            result = false;
        }
        return result;
    }
}
