package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Agam Gupta
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {

            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {

        try {
            File ugay = new File(name);
            return new Scanner(ugay);
        } catch (IOException excp) {
            System.out.println(name);
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine newMach = readConfig();
        String initialSet = "";
        if (_input.hasNext()) {
            initialSet = _input.nextLine();
        } else {
            throw error("nothing present in input file");
        }

        setUp(newMach, initialSet);
        while (_input.hasNext()) {
            String line = _input.nextLine().trim();
            if (line.equals("")) {
                _output.println("");
                continue;
            } else if (line.charAt(0) == '*') {
                setUp(newMach, line);
            } else {
                printMessageLine(newMach.convert(line));
            }
        }

    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {

            String alphabetString = "";
            if (_config.hasNext()) {
                alphabetString = _config.next().trim();
                _alphabet = new Alphabet(alphabetString);
            } else {
                _alphabet = new Alphabet("");
            }

            if (_alphabet.contains('*') | _alphabet.contains('(')
                    | _alphabet.contains(')')
                    | _alphabet.size() == 0) {
                throw new
                        EnigmaException("(,( or * "
                        + "present in alphabet or alphabet is empty");
            }



            int numRotors = 0;
            int numPawls = 0;
            if (_config.hasNextInt()) {
                numRotors = Integer.parseInt(_config.next());
                if (numRotors < 1) {
                    throw new
                            EnigmaException("numRotors must be greater than 0");
                }
            } else {
                throw error(" No number of rotors given");
            }

            if (_config.hasNextInt()) {
                numPawls = _config.nextInt();
                if (numPawls < 1) {
                    throw new
                            EnigmaException("numRotors must be greater than 0");
                }
            } else {
                throw new EnigmaException("No number of pawls given");
            }


            if ((numRotors <= numPawls) | (numRotors < 1) | (numPawls < 1)) {
                throw new EnigmaException(" S > P > 0 expected");
            }


            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, numPawls, allRotors);

        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String combinedRotateSet;
            Character rotorType;
            String rotorName = "";
            String permutationCycles = "";
            String notches = "";


            if (_config.hasNext()) {
                rotorName = _config.next();
            }

            combinedRotateSet = _config.next();
            rotorType = combinedRotateSet.charAt(0);


            if (combinedRotateSet.length() > 1) {
                notches = combinedRotateSet.substring(1);
            }

            while (_config.hasNext("(\\(["
                    + _alphabet.toString() + "]*\\))+")) {
                permutationCycles += _config.next() + " ";
            }

            Permutation perm = new
                    Permutation(permutationCycles.trim(), _alphabet);

            if (rotorType == 'M') {
                return new MovingRotor(rotorName, perm, notches);
            } else if (rotorType == 'N') {
                return new FixedRotor(rotorName, perm);
            } else if (rotorType == 'R') {
                return new Reflector(rotorName, perm);
            } else {
                throw new EnigmaException("not "
                        + "chosen appropriate rotor type: check readrotor");
            }

        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {

        Scanner newScan = new Scanner(settings);
        if (!newScan.next().equals("*")) {
            throw new EnigmaException(" Setting does not start with *");
        }
        String[] rotorNames = new String[M.numRotors()];
        for (int i = 0; i < M.numRotors(); i++) {
            rotorNames[i] = newScan.next();
        }

        M.insertRotors(rotorNames);

        String[] set = settings.split("\\s");
        if (set.length < rotorNames.length + 2) {
            throw new EnigmaException("cant be true");
        }

        M.setRotors(newScan.next().trim());

        for (int i = 1; i < M.numRotors(); i++) {
            if (M.getRotor(i) instanceof Reflector) {
                throw new EnigmaException(("Reflector cannot be !1 pos"));
            }
        }

        String permutationCycle = "";
        while (newScan.hasNext("\\(..\\)")) {
            permutationCycle += newScan.next();
        }
        M.setPlugboard(new Permutation(permutationCycle, _alphabet));


    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String out = "";
        out  = out + msg.charAt(0);
        msg = msg.replaceAll("\\s", "");
        for (int i = 1; i < msg.length(); i++) {
            if (i % 5 == 0) {
                out = out + " " + msg.charAt(i);
            } else {
                out = out + msg.charAt(i);
            }
        }
        _output.println(out);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;

    /** all rotors. */
    private ArrayList<Rotor> allRotors = new ArrayList<Rotor>();
}
