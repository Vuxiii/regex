package com.vuxiii.NFA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.vuxiii.DFA.DFA_state;
import com.vuxiii.Utils.*;;

public class NFA_state<T> {
    public String name;
    
    public boolean isFinal = false;

    List<NFA_edge<T>> out;
    List<NFA_edge<T>> in;
    public static int c = 0;

    public Function<String, T> constructor;

    public NFA_state() {
        this( "", false );
    }

    public NFA_state( String name ) {
        this( name, false );
    }

    public NFA_state( String name, boolean isFinal ) {
        this.name = name + (c++);
        this.isFinal = isFinal;
        out = new ArrayList<>();
        in = new ArrayList<>();

        // Utils.log( "-".repeat(10));
        
    }

    public boolean canConsume( char c ) {

        boolean found = false;

        for ( NFA_edge<T> e : out ) {
            if ( e.accept == c )
                return true;
            if ( e.isEpsilon )
                found = e.to.canConsume( c );
        }

        return found;
    }

    public boolean hasEpsilonEdge() {
        for ( NFA_edge<T> e : out ) 
            if ( e.isEpsilon )
                return true;
        return false;
    }

    public NFA_state<T> registerWord( String s ) {
        NFA_state<T> start = new NFA_state<>();

        addEdge( start );
        return start._registerWord( s );
    }

    private NFA_state<T> _registerWord( String s ) {
        if ( s.length() == 0 ) return this;

        char c = s.charAt( 0 );

        NFA_state<T> newState = new NFA_state<>( "", s.length() == 1 ); 
        addEdge( c, newState );

        return newState._registerWord( s.substring( 1 ) );
    }


    public NFA_state<T> registerWord( String s, boolean shouldAccept ) { // Add should accept state.
        NFA_state<T> start = new NFA_state<>();

        addEdge( start );
        return start._registerWord( s, shouldAccept );
    }

    
    private NFA_state<T> _registerWord( String s, boolean shouldAccept ) {
        if ( s.length() == 0 ) return this;

        char c = s.charAt( 0 );

        NFA_state<T> newState = new NFA_state<>( "", s.length() == 1 ? shouldAccept : false ); 
        addEdge( c, newState );

        return newState._registerWord( s.substring( 1 ), shouldAccept );
    }

    /**
     * Adds 
     * @param c
     * @param to
     */
    public void addEdge( char c, NFA_state<T> to ) {
        // Utils.log( "Adding edge " + name + " -" + c + "> " + to.name() );
        NFA_edge<T> e = new NFA_edge<>( c );
        e.from = this;
        e.to = to;

        out.add( e );
        to.in().add( e );

    }

    /**
     * Adds 
     * @param to
     */
    public void addEdge( boolean isWild, NFA_state<T> to ) {
        // Utils.log( "Adding edge " + name + " -" + c + "> " + to.name() );
        NFA_edge<T> e = new NFA_edge<>( isWild );
        e.from = this;
        e.to = to;

        out.add( e );
        to.in().add( e );

    }

    /**
     * Adds an epsilon edge to the given state.
     * @param to The state to go to.
     */
    public void addEdge( NFA_state<T> to ) {
        // Utils.log( "Adding edge " + name + " -epsilon1> " + to.name() );

        NFA_edge<T> e = new NFA_edge<>();
        e.from = this;
        e.to = to;

        out.add( e );
        to.in().add( e );

    }


    /**
     * Consumes all edges matching c from this state.
     * @param c The char to accept.
     * @return The list of states reached by traversing the edges with c.
     */
    public List<NFA_state<T>> consume( char c ) {
        List<NFA_state<T>> list = new ArrayList<>();
        
        for ( NFA_edge<T> e : out ) {
            if ( e.accept == c ) 
                list.add( e.to );
        }

        return list;
    }

    /**
     * Consumes all epsilon edges from this state.
     * @return
     */
    public List<NFA_state<T>> consume() {

        List<NFA_state<T>> list = new ArrayList<>();
        
        for ( NFA_edge<T> e : out ) {
            if ( e.isEpsilon ) 
                list.add( e.to );
        }

        return list;
    }

    // private static record Wrapper<T>( NFA_edge<T> edge, boolean isAny ) {

    //     public boolean equals( Object other ) {
    //         if ( other == null ) return false;
    //         if ( !(other instanceof NFA_edge ) ) return false;
    //         NFA_edge<?> o = (NFA_edge<?>) other;
    //         if ( edge.isEpsilon != o.isEpsilon ) return false;
    //         if ( edge.acceptAny || o.acceptAny ) return true; // :0
    //         if ( edge.accept != o.accept ) return false;
            
    //         return true;
    //     }
    // }

    public static <T> DFA_state<T> toDFA( NFA_state<T> begin ) {

        System.out.println( getStringRepresentation(begin));

        Map<Set<NFA_state<T>>, DFA_state<T>> cachedStates = new HashMap<>();
        Map<DFA_state<T>, Set<NFA_state<T>>> DFAToNFA = new HashMap<>();

        LinkedList<DFA_state<T>> queue = new LinkedList<>();
        Set<NFA_state<T>> closure = computeEpsilonClosure( Set.of( begin ) );
        String name = genNameFromStates( closure );
        DFA_state<T> dfaBegin = new DFA_state<>( name );

        DFAToNFA.put( dfaBegin, closure );

        // DFA_state current = dfaBegin;
        queue.add( dfaBegin );
        do {
            DFA_state<T> current = queue.pop();
            System.out.println( "Visiting DFA " + current.name );
            
            // Map<NFA_edge<T>, Set<NFA_state<T>>> reachable = new HashMap<>();
            Map<Character, Set<NFA_state<T>>> reachable = new HashMap<>();
            // Map<Wrapper<T>, Set<NFA_state<T>>> reachable = new HashMap<>();

            // Add all the edges reachable from this "combined" state
            for ( NFA_state<T> state : closure ) {
                System.out.println( "At NFA: " + state.name );
                for ( NFA_edge<T> edge : state.out ) {
                    if ( edge.isEpsilon ) continue;
                    
                    System.out.println( "\tChecking edge: " + edge );
                    if ( edge.acceptAny ) {
                        // Add to all nodes in the alfabet
                    } else if ( !reachable.containsKey( edge.accept ) ) {
                        Set<NFA_state<T>> s = new HashSet<>();
                        s.add( edge.to );
                        reachable.put( edge.accept, s );
                    } else {
                        reachable.get( edge.accept ).add( edge.to );
                    }
                }
                System.out.print( "\t\tCan go to -> " );
                reachable.values().forEach( c -> c.forEach( (s) -> System.out.print( s.name + ", " ) ) );
                System.out.println();
            }

            System.out.println( reachable );

            // Create all these states.
            for ( Character c : reachable.keySet() ) {
                System.out.println( "At charachter: " + c);
                Set<NFA_state<T>> NFAStates = reachable.get( c );
                System.out.println( NFAStates );
                DFA_state<T> newState;
                // Check if it has already been made
                if ( cachedStates.containsKey( NFAStates ) ) {
                    // System.out.println( "State: " + genNameFromStates( NFAStates ) + " already exists" );
                    newState = cachedStates.get( NFAStates );
                }  else {
                    newState = new DFA_state<>( genNameFromStates( NFAStates ) );
                    cachedStates.put( NFAStates, newState );
                    DFAToNFA.put( newState, NFAStates );
                    // System.out.println( "Creating new State: " + newState.name );
                    queue.add( newState ); // Might need to move this outside of this clause.
                }
                System.out.println( "New State: " + newState.name );

                // Add the edge to it.
                // if ( c.acceptAny )
                //     current.addEdge( (boolean) c.acceptAny, newState );
                // else
                //     current.addEdge( c.accept, newState );


                // I need to somehow mark it as an accept any edge.
                current.addEdge( c, newState );

                
            }
            // System.out.println( "Queue:" );
            // queue.forEach( nfa -> System.out.println( "\t" + nfa.name ) );
            current = queue.peek();
            if ( current == null ) break; // lmao...
            closure = computeEpsilonClosure( DFAToNFA.get( current ) );
            name = genNameFromStates( closure );

            DFAToNFA.put( current, closure ); // :0

            // System.out.println( "Closure:\n" + closure.toString() );

            // System.out.println( "ASDASDASD " + current.name );

        } while ( queue.size() > 0 );

        // System.out.println( "---------------------**********************'" );
        // Set the ending states
        // System.out.println( DFAToNFA );
        // System.out.println( "Setting finish states for DFA" );
        for ( DFA_state<T> dfa : DFAToNFA.keySet() ) {
            // System.out.println( "checking DFA state" + dfa.name );
            for ( NFA_state<T> nfa : DFAToNFA.get( dfa ) ) {
                if ( nfa.isFinal ) {
                    dfa.isFinal = true;
                    dfa.constructor = nfa.constructor;
                    // System.out.println( "Constructor for " + dfa.name() + ": " + dfa.constructor );
                    break;
                }
            }
        }
        // System.out.println( "END finish states for DFA" );

        return dfaBegin;
    }

    public static<T> List<NFA_state<T>> collectFinals( NFA_state<T> start ) {
        List<NFA_state<T>> li = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        _collectFinals( start, li, visited );

        return li;
    }

    private static<T> void _collectFinals( NFA_state<T> state, List<NFA_state<T>> li, Set<String> visited ) {
        if ( visited.contains( state.name ) ) return;
        visited.add( state.name );
        if ( state.isFinal ) li.add( state );
        for ( NFA_edge<T> e : state.out )
            _collectFinals( e.to, li, visited );
    }

    private static<T> String genNameFromStates( Set<NFA_state<T>> states ) {
        if ( states.size() == 0 ) return "";
        // System.out.println("GetNameFromStates");
        // states.forEach( state -> System.out.println( "\tNFA: " + state.name ) );
        String name = "";
        for ( NFA_state<T> state : states ) name += state.name() + ",";
        name = name.substring(0, name.length() - 1);
        return name;
    }

    private static<T> Set<NFA_state<T>> computeEpsilonClosure( Set<NFA_state<T>> nfas  ) {
        LinkedList<NFA_state<T>> input = new LinkedList<>();
        Set<NFA_state<T>> output = new HashSet<>();
        Set<String> visited = new HashSet<>();
        input.addAll( nfas );
        output.addAll( nfas );

        while ( input.size() > 0 ) {
            NFA_state<T> state = input.pop();
            
            if ( visited.contains( state.name() ) ) continue;
            
            visited.add( state.name() );
            
            for ( NFA_edge<T> edge : state.out() ) {
                if ( !edge.isEpsilon() ) continue;

                output.add( edge.to() );   
                input.add( edge.to() );
            }
        }
        // System.out.println( "-.-.-.-.-.-.-.-.-.-" );

        // System.out.println( output );
        // System.out.println( "-.-.-.-.-.-.-.-.-.-" );

        return output;
    }

    public static<T> List<NFA_state<T>> collectStates( NFA_state<T> nfa ) {
        List<NFA_state<T>> q = new LinkedList<>();
        _collectStates( nfa, q );

        return q;
    }

    private static<T> void _collectStates( NFA_state<T> nfa, List<NFA_state<T>> q ) {
        if ( q.contains( nfa ) ) return;
        q.add( nfa );
        for ( NFA_edge<T> e : nfa.out() )
            _collectStates( e.to(), q );
    }

    public NFA_state<T> copy() {
        
        Map<NFA_state<T>, NFA_state<T>> visited = new HashMap<>();
        NFA_state<T> q = _copy( visited );

        return q;
    }
 
   // TODO: check if this works correctly
    private NFA_state<T> _copy( Map<NFA_state<T>, NFA_state<T>> visited ) {
        if ( visited.containsKey( this ) ) return visited.get( this );
        
        NFA_state<T> q = new NFA_state<>( name + "CP", isFinal );
        visited.put( this, q );
        
        for ( NFA_edge<T> e : out ) {
            NFA_edge<T> ne = new NFA_edge<>( e.accept );
            ne.isEpsilon = e.isEpsilon;
            ne.acceptAny = e.acceptAny;
            ne.from = this;
            
            NFA_state<T> nto = e.to._copy( visited );
            
            ne.to = nto;

            q.out.add( ne );
            nto.in.add( ne );
        }


        return q;
    }

    public static<T> String getStringRepresentation( NFA_state<T> nfa ) {
        String s = "";
        List<NFA_state<T>> states = NFA_state.collectStates( nfa );
        for ( NFA_state<T> state : states ) {
            s += ( "State " + state.name + "\n" );
            s += ( "\tisFinal = " + state.isFinal + "\n" );
            s += ( "\tout edges:" + "\n" );
            for ( NFA_edge<T> e : state.out ) {
                s += ( "\t\t[" + (e.isEpsilon ? "epsilon" : e.accept) + " -> State " + e.to.name + "]" + "\n" );
            }
        }

        return s;
    }

    public String toString() {
        return name();
    }

    public String name() {
        return name;
    }

    public List<NFA_edge<T>> in() {
        return in;
    }

    public List<NFA_edge<T>> out() {
        return out;
    }

    public boolean isFinal() {
        return isFinal;
    }
    
}

