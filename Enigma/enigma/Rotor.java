package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Xuanyi Zhang
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _setting = 0;
        _ringstellung = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {

        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {

        return false;
    }

    /** Return my current setting. */
    int setting() {

        return _setting;
    }

    /** Return my current ring setting. */
    int rsetting() {

        return _ringstellung;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {

        _setting = permutation().wrap(posn);
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {

        _setting = alphabet().toInt(cposn);
    }
    /** Set ringsetting() to character POSN. */
    void rset(char posn) {
        _ringstellung = alphabet().toInt(posn);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        _contact = permutation().wrap(p + (setting() - rsetting()));
        _convert = permutation().permute(_contact);
        _output = permutation().wrap(_convert - (setting() - rsetting()));
        return _output;
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        _contact = permutation().wrap(e + (setting() - rsetting()));
        _convert = permutation().invert(_contact);
        _output = permutation().wrap(_convert - (setting() - rsetting()));
        return _output;
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;
    /** My rotor setting. */
    private int _setting;
    /** The contact point from which the input gets in. */
    private int _contact;
    /** The int that the input gets converted to. */
    private int _convert;
    /** The output from where the permuted number moves on. */
    private int _output;
    /** The output from where the permuted number moves on. */
    private int _ringstellung;



}
