package enigma;

import static enigma.EnigmaException.*;


import java.util.HashMap;
import java.util.Map;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Agam Gupta
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    private Map<Character, Character> permutation = new HashMap<>();
    /** inversing hashmap. */
    private Map<Character, Character> invPermutation = new HashMap<>();


    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        int j = 0;
        int waste = 0;
        for (int i = 0; i < cycles.length(); i++) {
            if (cycles.charAt(i) == '(') {
                j = i + 1;
            } else if (i < cycles.length() - 1 && cycles.charAt(i + 1) == ')') {

                permutation.put(cycles.charAt(i), cycles.charAt(j));

            } else if (_alphabet.contains(cycles.charAt(i))) {
                permutation.put(cycles.charAt(i), cycles.charAt(i + 1));
            }

        }
        for (int k = 0; k < _alphabet.size(); k++) {
            if (cycles.indexOf(_alphabet.toChar(k)) > -1) {
                waste++;
            }   else {
                permutation.put(_alphabet.toChar
                    (k), _alphabet.toChar(k));
                count++;
            }
        }

        String inverseCycles = "";
        for (int i = cycles.length() - 1; i > -1; i--) {
            inverseCycles = inverseCycles + cycles.charAt(i);
        }


        int l = 0;
        for (int i = 0; i < inverseCycles.length(); i++) {

            if (inverseCycles.charAt(i) == ')') {
                l = i + 1;
            } else if (i < inverseCycles.length() - 1
                    && inverseCycles.charAt(i + 1) == '(') {

                invPermutation.put(inverseCycles.charAt(i),
                        inverseCycles.charAt(l));
            } else if (_alphabet.contains(inverseCycles.charAt(i))) {
                invPermutation.put(inverseCycles.charAt(i),
                        inverseCycles.charAt(i + 1));
            }
        }


        for (int k = 0; k < _alphabet.size(); k++) {
            if (inverseCycles.indexOf(_alphabet.toChar(k)) > -1) {
                waste++;
            }   else {
                invPermutation.put(
                    _alphabet.toChar(k), _alphabet.toChar(k));
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {

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
        return _alphabet.toInt(permutation.
                get(_alphabet.toChar(p % size())));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        return _alphabet.toInt(invPermutation.
                get(_alphabet.toChar(c % size())));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return permutation.get(p);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return invPermutation.get(c);

    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        if (count > 0) {
            return false;
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /** counting for derangement. */
    private int count = 0;
}
