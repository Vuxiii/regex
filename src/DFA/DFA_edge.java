package src.DFA;

/**
 * This class represents edges between DFA_states
 */
public class DFA_edge {
    char accept;
    boolean acceptAny;
    DFA_state to;
    DFA_state from;

    public DFA_edge( char c ) {
        accept = c;
        acceptAny = false;
    }

    public DFA_edge( boolean wild ) {
        accept = ' ';
        acceptAny = wild;
    }

    public DFA_state to() {
        return to;
    }

    public DFA_state from() {
        return from;
    }

    public char accept() {
        return accept;
    }

}