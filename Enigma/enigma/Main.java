package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Xuanyi Zhang
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
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
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
        Machine mymachine = readConfig();
        if (!_input.hasNext()) {
            throw new EnigmaException("Empty input");
        }
        if (!_input.hasNext("\\*")) {
            throw new EnigmaException("input needs to start with a setting");
        }
        while (_input.hasNextLine()) {
            String outputmessage = "";
            String inputline = _input.nextLine();
            if (inputline.contains("*")) {
                char[] inputsetupclean = new char[inputline.length() - 1];
                for (int i = 0; i < inputsetupclean.length; i++) {
                    inputsetupclean[i] = inputline.charAt(i + 1);
                }
                String inputclean = new String(inputsetupclean);
                setUp(mymachine, inputclean);
            } else if (inputline.equals("")) {
                _output.println();
            } else {
                outputmessage = mymachine.convert(inputline);
                printMessageLine(outputmessage);
            }
        }

    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        int numrotors = 0;
        int numpawls = 0;
        Collection<Rotor> allrotors = new ArrayList<>();
        try {
            if (_config.hasNext()) {
                _alphabet = new Alphabet(_config.nextLine());
            } else {
                throw new EnigmaException("no alphabet");
            }
            if (_config.hasNextInt()) {
                numrotors = _config.nextInt();
            } else {
                throw new EnigmaException("wrong numrotor format");
            }
            if (_config.hasNextInt()) {
                numpawls = _config.nextInt();
            } else {
                throw new EnigmaException("wrong numpawls format");
            }
            while (_config.hasNext()) {

                allrotors.add(readRotor());
            }


            return new Machine(_alphabet, numrotors, numpawls, allrotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    public Rotor readRotor() {
        String cycles;
        String name;
        String typennotch;
        Rotor thisrotor;

        try {
            if (!_config.hasNextLine()) {
                throw new EnigmaException("no more rotors to read");
            } else {

                name = _config.next();
                typennotch = _config.next();
                cycles = _config.nextLine();
                while (_config.hasNext("\\(.+\\)")) {
                    cycles = cycles + _config.nextLine();
                }

            }


            if (typennotch.charAt(0) == 'M') {
                char[] cnotches = new char[typennotch.length() - 1];
                for (int i = 1; i < typennotch.length(); i++) {
                    cnotches[i - 1] = typennotch.charAt(i);
                }
                String snotches = new String(cnotches);
                thisrotor = new MovingRotor(name,
                        new Permutation(cycles, _alphabet), snotches);
            } else if (typennotch.charAt(0) == 'N') {
                thisrotor = new FixedRotor(name,
                        new Permutation(cycles, _alphabet));
            } else {
                thisrotor = new Reflector(name,
                        new Permutation(cycles, _alphabet));
            }


            return thisrotor;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner thesettings = new Scanner(settings);
        ArrayList<String> rotorsnsetting = new ArrayList<>();
        if (!thesettings.hasNextLine()) {
            throw new EnigmaException("settings file empty");
        }
        while (!thesettings.hasNext("\\([A-Z]+\\)")
                && thesettings.hasNext()) {
            String temp = thesettings.next();
            rotorsnsetting.add(temp);
        }
        if ((rotorsnsetting.get(rotorsnsetting.size() - 1).length()
                == rotorsnsetting.get(rotorsnsetting.size() - 2).length())
                && !(rotorsnsetting.get(rotorsnsetting.size()
                - 1).equals("AAAZ"))) {
            rsetting = rotorsnsetting.get(rotorsnsetting.size() - 1);
            rotorsnsetting.remove(rotorsnsetting.size() - 1);
        }
        if (rotorsnsetting.size() < M.numRotors() + 1) {
            throw new EnigmaException("rotor size doesn't match or "
                    + "missing initial settings");
        }
        if (rotorsnsetting.size() > M.numRotors() + 1) {
            throw new EnigmaException("too many rotors in settings");
        }
        String[] mrotors = new String[rotorsnsetting.size() - 1];
        for (int i = 0; i < rotorsnsetting.size() - 1; i++) {
            mrotors[i] = rotorsnsetting.get(i);
        }
        M.insertRotors(mrotors);
        if (!rsetting.isEmpty()) {
            M.setRings(rsetting);
        }

        M.setRotors(rotorsnsetting.get(rotorsnsetting.size() - 1));
        if (thesettings.hasNext()) {
            String plugboardcycles = thesettings.nextLine();
            Permutation plugboardsetting = new Permutation(plugboardcycles,
                    _alphabet);
            M.setPlugboard(plugboardsetting);
        } else {
            Permutation noplugboard = new Permutation("", _alphabet);
            M.setPlugboard(noplugboard);
        }

    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {

        Scanner inputscanner = new Scanner(msg);
        String output = "";
        if (!inputscanner.hasNext()) {
            _output.println(output);
        }
        int count = 0;
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) != ' ') {
                output += msg.charAt(i);
                count += 1;
            }
            if (count == 5) {
                output += " ";
                count = 0;
            }
        }
        _output.println(output);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
    /** Rsetting string if there is one. */
    private String rsetting = new String();
}
