package src.DFA;

/**
 * This class represents edges between DFA_states
 */
public class DFA_edge {
    char accept;
    DFA_state to;
    DFA_state from;

    public DFA_edge( char c ) {
        accept = c;
    }

    public DFA_state to() {
        // TODO Auto-generated method stub
        return to;
    }

    public DFA_state from() {
        // TODO Auto-generated method stub
        return from;
    }

    public char accept() {
        // TODO Auto-generated method stub
        return accept;
    }

}