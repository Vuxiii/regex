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

    public boolean canConsume( char c ) {
        if ( kind == EdgeKind.ANY && c != '\n' && c != ' ' ) return true;
        if ( kind == EdgeKind.DIGITS && Character.isDigit(c) ) return true;
        if ( kind == EdgeKind.ALPHS && Character.isLetter(c) ) return true;
        if ( kind == EdgeKind.STD && c == accept ) return true;
        return false;
    } 

    public String toString() {
        return "Edge " + from.name() + " -" + ( kind == EdgeKind.ANY ? "any" 
                                                : kind == EdgeKind.EPSILON ? "epsilon" 
                                                : kind == EdgeKind.ALPHS ? "alphs"
                                                : kind == EdgeKind.DIGITS ? "digits"
                                                : kind == EdgeKind.STD ? accept : "UNKNOWN" ) + "> " + to.name();
    }

}