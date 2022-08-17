package enigma;

import java.util.ArrayList;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Xuanyi Zhang
 */
class Alphabet {

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        for (int i = 0; i < chars.length(); i++) {
            if (_chars.contains(chars.charAt(i))) {
                throw new EnigmaException("No character may be duplicated");
            } else {
                _chars.add(chars.charAt(i));
            }
        }
        _size = _chars.size();
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {

        return _size;
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return _chars.contains(ch);
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index >= size()) {
            throw new EnigmaException("index out of bound");
        }
        return _chars.get(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        if (!_chars.contains(ch)) {
            throw new EnigmaException("character much be in the alphabet");
        }
        return _chars.indexOf(ch);
    }

    /** ArrayList containing all the characters in alphabet. */
    private ArrayList<Character> _chars = new ArrayList<Character>();
    /** size variable for the size of the alphabet. */
    private int _size;

}
