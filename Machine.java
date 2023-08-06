package enigma;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Agam Gupta
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _rotorList =  allRotors.stream().toList();
        _numRotor = numRotors;
        _pawls = pawls;

    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotor;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _actualRotors.get(k);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _actualRotors.clear();
        for (int i = 0; i < rotors.length; i++) {
            for (int j = 0; j < _rotorList.size(); j++) {
                if (rotors[i].equals(_rotorList.get(j).name())) {
                    _actualRotors.add(_rotorList.get(j));
                }
            }
        }

    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {

        for (int i = 1; i < numRotors(); i++) {
            _actualRotors.get(i).set(setting.charAt(i - 1));
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;

    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        ArrayList<Boolean> originalBooleans = new ArrayList<Boolean>();
        for (int i = 0; i < _actualRotors.size(); i++) {
            originalBooleans.add(_actualRotors.get(i).atNotch());
        }
        _actualRotors.get(_actualRotors.size() - 1).advance();
        for (int j = _actualRotors.size() - 2; j > 0; j--) {
            if ((originalBooleans.get(j + 1) && _actualRotors.get(j).rotates())
                    || (originalBooleans.get(j)
                    && _actualRotors.get(j - 1).rotates()
                            && _actualRotors.get(j).rotates())) {
                _actualRotors.get(j).advance();
            }
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {

        for (int i = _actualRotors.size() - 1; i >= 0; i--) {
            c = _actualRotors.get(i).convertForward(c);
        }
        for (int i = 1; i < _actualRotors.size(); i++) {
            c = _actualRotors.get(i).convertBackward(c);
        }

        return c;

    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) == ' ') {
                continue;
            } else if (!alphabet().contains(msg.charAt(i))) {
                throw new EnigmaException("bruh");
            } else {
                result += alphabet()
                        .toChar(convert(alphabet().toInt(msg.charAt(i))));
            }

        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private Alphabet _alphabet;
    /** List of all rotors. */
    private List<Rotor> _rotorList;
    /** number of rotors. */
    private int _numRotor;
    /** number of pawls. */
    private int _pawls;
    /** permutation for plugboard. */
    private Permutation _plugboard;
    /** rotors i am actually using. */
    private ArrayList<Rotor> _actualRotors = new ArrayList<>();
}
