package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Xuanyi Zhang
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notch = notches;
    }


    @Override
    void advance() {
        int nextsetting = this.setting() + 1;
        int nextsettingmod = this.permutation().wrap(nextsetting);
        set(nextsettingmod);
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        for (int i = 0; i < _notch.length(); i++) {
            if (alphabet().toInt(_notch.charAt(i)) == this.setting()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "MovingRotor " + name();
    }

    /** Stores the notches of the rotor. */
    private String _notch;


}
