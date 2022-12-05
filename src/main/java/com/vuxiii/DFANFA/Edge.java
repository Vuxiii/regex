package com.vuxiii.DFANFA;

/**
 * This class represents edges between DFA_states
 */
public class Edge<StateType extends NameInterface> {
    char accept;
    
    EdgeKind kind;

    StateType to;
    StateType from;

    public Edge( char c ) {
        accept = c;
        kind = EdgeKind.STD;
    }

    public Edge( EdgeKind kind ) {
        accept = ' ';
        this.kind = kind;
    }

    public StateType to() {
        return to;
    }

    public StateType from() {
        return from;
    }

    public char accept() {
        return accept;
    }

    public String toString() {
        return "Edge " + from.name() + " -" + ( kind == EdgeKind.ANY ? "any" 
                                                : kind == EdgeKind.EPSILON ? "epsilon" 
                                                : kind == EdgeKind.ALPHS ? "alphs"
                                                : kind == EdgeKind.DIGITS ? "digits"
                                                : kind == EdgeKind.STD ? accept : "UNKNOWN" ) + "> " + to.name();
    }

}