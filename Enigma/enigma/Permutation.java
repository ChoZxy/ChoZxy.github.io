package enigma;
import java.util.HashMap;
import java.util.Map.Entry;
import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Xuanyi Zhang
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        if (cycles.equals("")) {
            _Cycles = new HashMap<Character, Character>();
        } else {
            String cleancycles = cycles;
            cleancycles = cleancycles.replace(" ", "");
            cleancycles = cleancycles.replace("(", "");
            String cleancyclescopy = cleancycles.replace(")", "");
            String[] indicycles = cleancycles.split("\\)", 0);
            for (String a:indicycles) {
                addCycle(a);
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycle = cycle;
        if (cycle.length() > 1) {
            for (int i = 0; i < cycle.length() - 1; i++) {
                _Cycles.put(_cycle.charAt(i), _cycle.charAt(i + 1));
            }
            _Cycles.put(_cycle.charAt(cycle.length() - 1), _cycle.charAt(0));
        } else {
            _Cycles.put(_cycle.charAt(0), _cycle.charAt(0));
        }


    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int result;
        Character rchar;
        Character from = _alphabet.toChar(wrap(p));
        if (_Cycles.containsKey(from)) {
            rchar = _Cycles.get(from);
            result = _alphabet.toInt(rchar);
        } else {
            result = p;
        }
        return result;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int result = c;
        char charind;
        char frominv = _alphabet.toChar(wrap(c));

        for (Entry<Character, Character> entry:_Cycles.entrySet()) {
            if (entry.getValue() == frominv) {
                charind = entry.getKey();
                result = _alphabet.toInt(charind);
                break;
            }
        }

        return result;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (_Cycles.containsKey(p)) {
            return _Cycles.get(p);
        } else {
            return p;
        }
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        char charinv = c;
        for (Entry<Character, Character> entry:_Cycles.entrySet()) {
            if (entry.getValue() == c) {
                charinv = entry.getKey();
                break;
            }
        }


        return charinv;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _alphabet.size(); i++) {
            if (permute(i) == i) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /** HashMap which keys are the input character
     * that maps to the values which are the output character in the wire. */
    private HashMap<Character, Character> _Cycles =
            new HashMap<Character, Character>();
    /** The individual permutation cycle in string format. */
    private String _cycle;
}
