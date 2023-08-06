package enigma;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Agam Gupta
 */
class Alphabet {

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */

    Alphabet(String chars) {

        alphabetString = chars;
        for (int i = 0; i < chars.length(); i++) {
            map.put(chars.charAt(i), i);
        }
    }



    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return map.size();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {

        return map.containsKey(ch);
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size().
     * @return*/
    char toChar(int index) {

        for (Entry<Character, Integer> entry: map.entrySet()) {
            if (entry.getValue() == index) {
                return entry.getKey();
            }
        }
        Character never = 'p';
        return never;

    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        return map.get(ch);
    }

    /** alphabetString containing string of alphabets. */
    private String alphabetString;

    public String toString() {
        return alphabetString;
    }

    /** hashmap from alphabet to integer. */
    private Map<Character, Integer> map = new HashMap<>();




}
