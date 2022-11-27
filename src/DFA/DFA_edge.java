package src.DFA;

/**
 * This class represents edges between DFA_states
 */
public class DFA_edge<T> {
    char accept;
    boolean acceptAny;
    DFA_state<T> to;
    DFA_state<T> from;

    public DFA_edge( char c ) {
        accept = c;
        acceptAny = false;
    }

    public DFA_edge( boolean wild ) {
        accept = ' ';
        acceptAny = wild;
    }

    public DFA_state<T> to() {
        return to;
    }

    public DFA_state<T> from() {
        return from;
    }

    public char accept() {
        return accept;
    }

}