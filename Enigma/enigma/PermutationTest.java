package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/**
 * The suite of all JUnit tests for the Permutation class. For the purposes of
 * this lab (in order to test) this is an abstract class, but in proj1, it will
 * be a concrete class. If you want to copy your tests for proj1, you can make
 * this class concrete by removing the 4 abstract keywords and implementing the
 * 3 abstract methods.
 *
 *  @author Xuanyi Zhang
 */
public class PermutationTest {

    /**
     * For this lab, you must use this to get a new Permutation,
     * the equivalent to:
     * new Permutation(cycles, alphabet)
     *
     * @return a Permutation with cycles as its cycles and alphabet as
     * its alphabet
     * @see Permutation for description of the Permutation conctructor
     */
    Permutation getNewPermutation(String cycles, Alphabet alphabet) {
        return new Permutation(cycles, alphabet);
    }

    /**
     * For this lab, you must use this to get a new Alphabet,
     * the equivalent to:
     * new Alphabet(chars)
     *
     * @return an Alphabet with chars as its characters
     * @see Alphabet for description of the Alphabet constructor
     */
    Alphabet getNewAlphabet(String chars) {
        return new Alphabet(chars);
    }

    /**
     * For this lab, you must use this to get a new Alphabet,
     * the equivalent to:
     * new Alphabet()
     *
     * @return a default Alphabet with characters ABCD...Z
     * @see Alphabet for description of the Alphabet constructor
     */
    Alphabet getNewAlphabet() {
        return new Alphabet();
    }

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /** Check that PERM has an ALPHABET whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha,
                           Permutation perm, Alphabet alpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.toInt(c), ei = alpha.toInt(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        Alphabet alpha = getNewAlphabet();
        Permutation perm = getNewPermutation("", alpha);
        checkPerm("identity", UPPER_STRING, UPPER_STRING, perm, alpha);
    }

    Permutation p1 = getNewPermutation("(BC)(FEGD)", getNewAlphabet("ABCDEFG"));
    Permutation p2 = getNewPermutation("(BCA)(FEGD)",
            getNewAlphabet("ABCDEFG"));


    @Test
    public void testInvertChar() {
        assertEquals('A', p1.invert('A'));
        assertEquals('E', p1.invert('G'));
        assertEquals('D', p1.invert('F'));
    }

    @Test
    public void testInvertInt() {
        assertEquals(0, p1.invert(0));
        assertEquals(2, p1.invert(1));
        assertEquals(4, p1.invert(-1));
        assertEquals(6, p1.invert(10));
    }

    @Test
    public void testPermuteChar() {
        assertEquals('F', p1.permute('D'));
        assertEquals('A', p1.permute('A'));
        assertEquals('C', p1.permute('B'));
    }

    @Test
    public void testPermuteInt() {
        assertEquals(0, p1.permute(0));
        assertEquals(2, p1.permute(1));
        assertEquals(3, p1.permute(-1));
        assertEquals(5, p1.permute(10));
    }

    @Test
    public void testSize() {
        assertEquals(7, p1.size());
    }

    @Test
    public void testAlpha() {
        Alphabet alpha = getNewAlphabet("ABCDEFG");
        Permutation p3 = getNewPermutation("", alpha);
        Alphabet alphaful = getNewAlphabet();
        Permutation p4 = getNewPermutation("", alphaful);

        assertEquals(alpha, p3.alphabet());
        assertEquals(alphaful, p4.alphabet());

    }

    @Test
    public void testDerangement() {
        assertFalse(p1.derangement());
        assertTrue(p2.derangement());
    }
}
