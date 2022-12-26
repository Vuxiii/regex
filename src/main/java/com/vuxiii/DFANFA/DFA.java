package com.vuxiii.DFANFA;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class DFA<T> implements NameInterface {
    private static int c = 0;
    public String name;

    public Function<String, T> constructor;

    public boolean isFinal = false;
    List<Edge<DFA<T>>> out;
    List<Edge<DFA<T>>> in;

    public DFA( String name ) {
        c++;
        this.name = name;
        out = new ArrayList<>();
    }

    public DFA( String name, boolean isFinal ) {
        this( name );
        this.isFinal = isFinal;
    }

    public DFA<T> registerWord( String word ) {
        if ( word.length() == 0 ) return this;

        char c = word.charAt( 0 );

        DFA<T> newState = null;

        for ( Edge<DFA<T>> e : out ) {
            if ( e.accept() == c ) {
                newState = e.to();
            }
        }

        if ( newState == null ) {
            newState = new DFA<>( "State: " + (DFA.c+1), word.length() == 1 ); 
            addEdge( c, newState );
        }

        return newState.registerWord( word.substring( 1 ) );
    }

    public void addEdge( char c, DFA<T> to ) {
        Edge<DFA<T>> e = new Edge<>( c );
        e.to = to;
        out.add( e );
    }

    public void addEdge( EdgeKind kind , DFA<T> to ) {
        Edge<DFA<T>> e = new Edge<>( kind );
        e.to = to;
        out.add( e );
    }

    public boolean canConsume( char c )  {
        for ( Edge<DFA<T>> e : out )
            if ( e.canConsume( c ) )
                return true;
        return false;
    }

    public DFA<T> consume( char c ) {
        // First we need to go through the stds.
        // Then we can check for DIGIT, ALPH and ANY. (In order of precedence)
        Edge<DFA<T>> acceptEdge = null;
        for ( Edge<DFA<T>> e : out ) {
            if ( e.canConsume( c )  ) {
                acceptEdge = e;
                break;
            }
        }
        if ( acceptEdge == null ) return null;

        return acceptEdge.to();
    }

    /**
     * Precondition: Can consume the word
     * @param s The string to be consumed
     * @return The last state
     */
    public DFA<T> consumeWord( String s ) {
        DFA<T> current = this;
        for ( char c : s.toCharArray() )
            current = current.consume( c );
        return current;
    }

    // public DFA_state copy() {

    //     DFA_state q = new DFA_state( name, isFinal );
    //     for ( Edge e : out() ) {
    //         DFA_state s = e.to().copy();
    //         Edge ecopy = new Edge( e.accept() );
    //         ecopy.from = q;
    //         ecopy.to = s;
    //         s.out.add( ecopy );
    //     }

    //     return q;
    // }

    public String toString() {
        String s = isFinal ? "[[" + name + "]]" : "[" + name + "]";
        s += "\t";
        if ( out.size() > 0 ) {
            s += "Can go to the following states:\n";
            for ( Edge<DFA<T>> e : out ) {
                s += "\t[" + e.accept() + "] -> " + e.to().name() + "\n";
            }
        } else {
            s += "This state has no outgoing edges.";
        }

        return s;
    }

    public static <T> List<DFA<T>> collectStates( DFA<T> dfa ) {
        List<DFA<T>> q = new LinkedList<>();
        _collectStates( dfa, q );

        return q;
    }

    private static <T> void _collectStates( DFA<T> dfa, List<DFA<T>> q ) {
        if ( q.contains( dfa ) ) return;
        q.add( dfa );
        for ( Edge<DFA<T>> e : dfa.out() )
            _collectStates( e.to(), q );
    }

    public static<T> String getStringRepresentation( DFA<T> nfa ) {
        String s = "";
        List<DFA<T>> states = DFA.collectStates( nfa );
        for ( DFA<T> state : states ) {
            s += ( "State " + state.name() + "\n" );
            s += ( "\tisFinal = " + state.isFinal() );
            if ( state.isFinal() )
                s += " () -> " + state.constructor + "\n";
            else
                s += "\n";
            s += ( "\tout edges:" + "\n" );
            for ( Edge<DFA<T>> e : state.out() ) {
                s += ( "\t\t[" + (e.kind == EdgeKind.STD ? e.accept() : e.kind ) + " -> State " + e.to().name() + "]" + "\n" );
            }
        }

        return s;
    }

    public String name() {
        return name;
    }

    public List<Edge<DFA<T>>> in() {
        return in;
    }

    public List<Edge<DFA<T>>> out() {
        return out;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
