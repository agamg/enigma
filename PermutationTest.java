package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);

            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                    e, perm.permute(c));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                    ci, perm.invert(ei));
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));

        }
    }

    /* ***** TESTS ***** */
    @Test
    public void check() {
        Alphabet a = new Alphabet();
        Permutation p = new Permutation("(ABCD)", a);
    }

    @Test
    public void testInvertChar() {
        Alphabet a = new Alphabet("ABCD");
        Permutation p = new Permutation("(BACD)", a);

        assertEquals('B', p.invert('A'));
        assertEquals('D', p.invert('B'));
        assertEquals('C', p.permute('A'));
        assertEquals('B', p.permute('D'));


        assertEquals(0, p.invert(2));
        assertEquals(3, p.invert(1));
        assertEquals(1, p.permute(3));
        assertEquals(3, p.permute(2));
        assertEquals(4, p.size());

        Alphabet b = new Alphabet();
        Permutation g = new Permutation("(BACD)", b);
        assertEquals(26, g.size());


    }

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

}
