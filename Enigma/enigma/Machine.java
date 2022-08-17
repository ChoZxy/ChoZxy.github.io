package enigma;

import java.util.Collection;
import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Xuanyi Zhang
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numrotors = numRotors;
        _numpawls = pawls;
        _allrotors = allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numrotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {

        return _numpawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        ArrayList<String> allnames = new ArrayList<>();
        _Rotors = new ArrayList<>();
        for (Rotor r : _allrotors) {
            allnames.add(r.name());
        }
        if (_numrotors != rotors.length) {
            throw new EnigmaException("Slots not equal to numrotors");
        }
        for (String s : rotors) {
            for (Rotor r : _allrotors) {
                if (!allnames.contains(s)) {
                    throw new EnigmaException("rotor not available");
                } else if (_Rotors.contains(s)) {
                    throw new EnigmaException("Duplicate rotors");
                } else if (s.equals(r.name())) {
                    _Rotors.add(r);
                }
            }

        }
        for (int i = 0; i < _Rotors.size(); i++) {
            _Rotors.get(i).set(0);
        }
        if (!_Rotors.get(0).reflecting()) {
            throw new EnigmaException("Rotor 1 must be reflector");
        }


    }
    /** Set my rotors' ring settings according to RSETTING.  */
    void setRings(String rsetting) {
        if (rsetting.length() != _numrotors - 1) {
            throw new EnigmaException("wrong rsetting number");
        }
        for (int i = 0; i < rsetting.length(); i++) {
            if (!_alphabet.contains(rsetting.charAt(i))) {
                throw new EnigmaException("rsetting not in alphabet");
            } else {
                _Rotors.get(i + 1).rset(rsetting.charAt(i));
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _numrotors - 1) {
            throw new EnigmaException("wrong setting number");
        }
        for (int i = 0; i < setting.length(); i++) {
            if (!_alphabet.contains(setting.charAt(i))) {
                throw new EnigmaException("setting not in alphabet");
            } else {
                _Rotors.get(i + 1).set(setting.charAt(i));
            }

        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {

        if (c > _alphabet.size() || c < 0) {
            throw new EnigmaException("bad input character");
        }

        Boolean[] advancecheck = new Boolean[_numrotors];
        for (int k = 0; k < advancecheck.length; k++) {
            advancecheck[k] = false;
        }
        for (int i = 1; i < _numrotors - 1; i++) {
            if (_Rotors.get(i + 1).atNotch()) {
                advancecheck[i] = true;
            }
        }
        for (int n = 2; n < _numrotors - 1; n++) {
            if (_Rotors.get(n - 1).rotates() && _Rotors.get(n).atNotch()) {
                advancecheck[n] = true;
            }
        }
        advancecheck[_numrotors - 1] = true;
        for (int m = 0; m < advancecheck.length; m++) {
            if (advancecheck[m]) {
                _Rotors.get(m).advance();
            }
        }
        int inputint = c;
        inputint = _plugboard.permute(inputint);
        for (int i = _Rotors.size() - 1; i >= 0; i--) {
            inputint = _Rotors.get(i).convertForward(inputint);
        }
        for (int j = 1; j < _Rotors.size(); j++) {
            inputint = _Rotors.get(j).convertBackward(inputint);
        }
        inputint = _plugboard.invert(inputint);
        return inputint;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {

        char[] msgchar = new char[msg.length()];
        for (int i = 0; i < msg.length(); i++) {
            if (!_alphabet.contains(msg.charAt(i)) && msg.charAt(i) != ' ') {
                throw new EnigmaException("bad message");
            } else {
                msgchar[i] = msg.charAt(i);
            }
        }
        char[] msgout = new char[msgchar.length];
        for (int j = 0; j < msgchar.length; j++) {
            if (msgchar[j] != ' ') {
                int msgint = _alphabet.toInt(msgchar[j]);
                msgout[j] = _alphabet.toChar(convert(msgint));
            } else {
                msgout[j] = ' ';
            }

        }
        return new String(msgout);
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** The number of rotors for the machine. */
    private int _numrotors;
    /** The number of pawls for the machine. */
    private int _numpawls;
    /** The collection of all rotors according to configuration file. */
    private Collection<Rotor> _allrotors;
    /** The rotors that are present in my machine. */
    private ArrayList<Rotor> _Rotors = new ArrayList<>();
    /** The plugboard in my machine. */
    private Permutation _plugboard;
}
