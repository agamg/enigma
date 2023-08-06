package enigma;


/** Class that represents a rotating rotor in the enigma machine.
 *  @author Agam Gupta
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        notche = notches;
    }



    @Override
    void advance() {
        set(setting() + 1);
    }

    @Override
    String notches() {
        return notche;
    }

    @Override
    boolean rotates() {
        return true;
    }

    /** string for notches. */
    private String notche;
}
