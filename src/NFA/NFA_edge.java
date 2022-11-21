package src.NFA;

import java.util.List;


/**
 * This class represents edges between NFA_states
 * It can either accept a char or it is a epsilon edge.
 */
public class NFA_edge {
    char accept;
    boolean isEpsilon = false;
    boolean acceptAny;
    NFA_state to;
    NFA_state from;

    public NFA_edge( char c ) {
        accept = c;
        acceptAny = false;
    }

    public NFA_edge( boolean isWild ) {
        accept = ' ';
        isEpsilon = false;
        acceptAny = isWild;
    }

    public NFA_edge() {
        accept = ' ';
        this.isEpsilon = true;
        acceptAny = false;
    }

    public String toString() {
        return "Edge " + from.name() + " -" + (isEpsilon ? "epsilon" : accept) + "> " + to.name();
    }

    public boolean isEpsilon() {
        return isEpsilon;
    }

    public NFA_state to() {
        return to;
    }

    public NFA_state from() {
        return from;
    }

    public char accept() {
        return accept;
    }

}
