package com.vuxiii.NFA;

import java.util.List;


/**
 * This class represents edges between NFA_states
 * It can either accept a char or it is a epsilon edge.
 */
public class NFA_edge<T> {
    char accept;
    boolean isEpsilon = false;
    boolean acceptAny;
    NFA_state<T> to;
    NFA_state<T> from;

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
        return "Edge " + from.name() + " -" + (acceptAny ? "any" : isEpsilon ? "epsilon" : accept) + "> " + to.name();
    }

    public boolean isEpsilon() {
        return isEpsilon;
    }

    public NFA_state<T> to() {
        return to;
    }

    public NFA_state<T> from() {
        return from;
    }

    public char accept() {
        return accept;
    }

    // public boolean equals( Object other ) {
    //     if ( other == null ) return false;
    //     if ( !(other instanceof NFA_edge ) ) return false;
    //     NFA_edge<?> o = (NFA_edge<?>) other;
    //     if ( accept != o.accept ) return false;
    //     // if ( !from.equals(o.from) ) return false; // Should acceptAny == accept??
    //     // if ( !to.equals(o.to) ) return false; // Should acceptAny == accept??
    //     if ( isEpsilon != o.isEpsilon ) return false;
    //     return true;
    // }

}
