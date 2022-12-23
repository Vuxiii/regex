package com.vuxiii.DFANFA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.vuxiii.Utils.*;;

public class NFA<T> implements NameInterface {
    public String name;
    
    public boolean isFinal = false;

    List<Edge<NFA<T>>> out;
    List<Edge<NFA<T>>> in;
    public static int c = 0;

    public Function<String, T> constructor;

    public NFA() {
        this( "", false );
    }

    public NFA( String name ) {
        this( name, false );
    }

    public NFA( String name, boolean isFinal ) {
        this.name = name + (c++);
        this.isFinal = isFinal;
        out = new ArrayList<>();
        in = new ArrayList<>();

        // Utils.log( "-".repeat(10));
        
    }

    public boolean canConsume( char c ) {

        boolean found = false;

        for ( Edge<NFA<T>> e : out ) {
            if ( e.kind == EdgeKind.EPSILON )
                found = e.to.canConsume( c );
            else if ( e.canConsume( c ) )
                return true;
            
        }

        return found;
    }

    public boolean hasEpsilonEdge() {
        for ( Edge<NFA<T>> e : out ) 
            if ( e.kind == EdgeKind.EPSILON )
                return true;
        return false;
    }

    public NFA<T> registerWord( String s ) {
        NFA<T> start = new NFA<>();

        addEdge( start );
        return start._registerWord( s );
    }

    private NFA<T> _registerWord( String s ) {
        if ( s.length() == 0 ) return this;

        char c = s.charAt( 0 );

        NFA<T> newState = new NFA<>( "", s.length() == 1 ); 
        addEdge( c, newState );

        return newState._registerWord( s.substring( 1 ) );
    }


    public NFA<T> registerWord( String s, boolean shouldAccept ) { // Add should accept state.
        NFA<T> start = new NFA<>();

        addEdge( start );
        return start._registerWord( s, shouldAccept );
    }

    
    private NFA<T> _registerWord( String s, boolean shouldAccept ) {
        if ( s.length() == 0 ) return this;

        char c = s.charAt( 0 );

        NFA<T> newState = new NFA<>( "", s.length() == 1 ? shouldAccept : false ); 
        addEdge( c, newState );

        return newState._registerWord( s.substring( 1 ), shouldAccept );
    }

    /**
     * Adds 
     * @param c
     * @param to
     */
    public void addEdge( char c, NFA<T> to ) {
        // Utils.log( "Adding edge " + name + " -" + c + "> " + to.name() );
        Edge<NFA<T>> e = new Edge<>( c );
        e.from = this;
        e.to = to;

        out.add( e );
        to.in().add( e );

    }

    /**
     * Adds 
     * @param to
     */
    public void addEdge( EdgeKind kind, NFA<T> to ) {
        // Utils.log( "Adding edge " + name + " -" + c + "> " + to.name() );
        Edge<NFA<T>> e = new Edge<>( kind );
        e.from = this;
        e.to = to;

        out.add( e );
        to.in().add( e );

    }

    /**
     * Adds an epsilon edge to the given state.
     * @param to The state to go to.
     */
    public void addEdge( NFA<T> to ) {
        // Utils.log( "Adding edge " + name + " -epsilon1> " + to.name() );

        Edge<NFA<T>> e = new Edge<>( EdgeKind.EPSILON );
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
    public List<NFA<T>> consume( char c ) {
        List<NFA<T>> list = new ArrayList<>();
        
        for ( Edge<NFA<T>> e : out ) {
            if ( e.kind != EdgeKind.EPSILON && e.canConsume( c ) ) 
                list.add( e.to );
        }

        return list;
    }

    /**
     * Consumes all epsilon edges from this state.
     * @return
     */
    public List<NFA<T>> consume() {

        List<NFA<T>> list = new ArrayList<>();
        
        for ( Edge<NFA<T>> e : out ) {
            if ( e.kind == EdgeKind.EPSILON ) 
                list.add( e.to );
        }

        return list;
    }

    // private static record Wrapper<T>( Edge<NFA_state<T>> edge, boolean isAny ) {

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

    public static <T> DFA<T> toDFA( NFA<T> begin ) {

        // System.out.println( getStringRepresentation(begin));

        Map<Set<NFA<T>>, DFA<T>> cachedStates = new HashMap<>();
        Map<DFA<T>, Set<NFA<T>>> DFAToNFA = new HashMap<>();

        LinkedList<DFA<T>> queue = new LinkedList<>();
        Set<NFA<T>> closure = computeEpsilonClosure( Set.of( begin ) );
        String name = genNameFromStates( closure );
        DFA<T> dfaBegin = new DFA<>( name );

        DFAToNFA.put( dfaBegin, closure );

        // DFA_state<T> current = dfaBegin;
        queue.add( dfaBegin );
        do {
            DFA<T> current = queue.pop();
            // System.out.println( "Visiting DFA " + current.name );
            
            // Map<Edge<NFA_state<T>>, Set<NFA_state<T>>> reachable = new HashMap<>();
            Map<Character, Set<NFA<T>>> reachable = new HashMap<>();
            Set<NFA<T>> anyReachable = new HashSet<>();
            Set<NFA<T>> digitReachable = new HashSet<>();
            Set<NFA<T>> alphReachable = new HashSet<>();
            // Map<Wrapper<T>, Set<NFA_state<T>>> reachable = new HashMap<>();

            // Add all the edges reachable from this "combined" state
            for ( NFA<T> state : closure ) {
                // System.out.println( "At NFA: " + state.name );
                for ( Edge<NFA<T>> edge : state.out ) {
                    if ( edge.kind == EdgeKind.EPSILON ) continue;
                    
                    // System.out.println( "\tChecking edge: " + edge );
                    if ( edge.kind == EdgeKind.ANY ) {
                        // Add to all nodes in the alfabet
                        anyReachable.add( edge.to );
                    } else if ( edge.kind == EdgeKind.ALPHS ) {
                        alphReachable.add( edge.to );
                    }  else if ( edge.kind == EdgeKind.DIGITS ) {
                        digitReachable.add( edge.to );
                    } else if ( !reachable.containsKey( edge.accept ) ) {
                        Set<NFA<T>> s = new HashSet<>();
                        s.add( edge.to );
                        reachable.put( edge.accept, s );
                    } else {
                        reachable.get( edge.accept ).add( edge.to );
                    }
                }
                // System.out.print( "\t\tCan go to -> " );
                // reachable.values().forEach( c -> c.forEach( (s) -> System.out.print( s.name + ", " ) ) );
                // System.out.println();
            }

            // System.out.println( reachable );

            // Create all these states.
            for ( Character c : reachable.keySet() ) {
                // System.out.println( "At charachter: " + c);
                Set<NFA<T>> NFAStates = reachable.get( c );
                // System.out.println( NFAStates );
                DFA<T> newState;
                // Check if it has already been made
                if ( cachedStates.containsKey( NFAStates ) ) {
                    // System.out.println( "State: " + genNameFromStates( NFAStates ) + " already exists" );
                    newState = cachedStates.get( NFAStates );
                }  else {
                    newState = new DFA<T>( genNameFromStates( NFAStates ) );
                    cachedStates.put( NFAStates, newState );
                    DFAToNFA.put( newState, NFAStates );
                    // System.out.println( "Creating new State: " + newState.name );
                    queue.add( newState ); // Might need to move this outside of this clause.
                }
                // System.out.println( "New State: " + newState.name );

                // Add the edge to it.
                current.addEdge( c, newState ); 
            }

            // Refactor the reachables.... Duplicate code

            if ( anyReachable.size() > 0 ) {
                DFA<T> newState;
                if ( cachedStates.containsKey( anyReachable ) ) {
                    // System.out.println( "State: " + genNameFromStates( NFAStates ) + " already exists" );
                    newState = cachedStates.get( anyReachable );
                }  else {
                    newState = new DFA<T>( genNameFromStates( anyReachable ) );
                    cachedStates.put( anyReachable, newState );
                    DFAToNFA.put( newState, anyReachable );
                    // System.out.println( "Creating new State: " + newState.name );
                    queue.add( newState ); // Might need to move this outside of this clause.
                }
                // System.out.println( "New State: " + newState.name );
                // Add the edge to it.
                current.addEdge( EdgeKind.ANY, newState ); 
            }

            if ( alphReachable.size() > 0 ) {
                DFA<T> newState;
                if ( cachedStates.containsKey( alphReachable ) ) {
                    // System.out.println( "State: " + genNameFromStates( NFAStates ) + " already exists" );
                    newState = cachedStates.get( alphReachable );
                }  else {
                    newState = new DFA<T>( genNameFromStates( alphReachable ) );
                    cachedStates.put( alphReachable, newState );
                    DFAToNFA.put( newState, alphReachable );
                    // System.out.println( "Creating new State: " + newState.name );
                    queue.add( newState ); // Might need to move this outside of this clause.
                }
                // System.out.println( "New State: " + newState.name );
                // Add the edge to it.
                current.addEdge( EdgeKind.ALPHS, newState ); 
            }

            if ( digitReachable.size() > 0 ) {
                DFA<T> newState;
                if ( cachedStates.containsKey( digitReachable ) ) {
                    // System.out.println( "State: " + genNameFromStates( NFAStates ) + " already exists" );
                    newState = cachedStates.get( digitReachable );
                }  else {
                    newState = new DFA<T>( genNameFromStates( digitReachable ) );
                    cachedStates.put( digitReachable, newState );
                    DFAToNFA.put( newState, digitReachable );
                    // System.out.println( "Creating new State: " + newState.name );
                    queue.add( newState ); // Might need to move this outside of this clause.
                }
                // System.out.println( "New State: " + newState.name );
                // Add the edge to it.
                current.addEdge( EdgeKind.DIGITS, newState ); 
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
        // DFAToNFA.forEach( (dfa, nfa) -> {
        //     System.out.println( "DFA: " + dfa.name ); 
        //     nfa.forEach( n -> System.out.println( "\tNFA: " + n.name + " " + n.isFinal ));
        // });
        // System.out.println( "Setting finish states for DFA" );
        for ( DFA<T> dfa : DFAToNFA.keySet() ) {
            // System.out.println( "checking DFA state" + dfa.name );
            for ( NFA<T> nfa : DFAToNFA.get( dfa ) ) {
                if ( nfa.isFinal ) {
                    // System.out.println( "FOUND FINAL" );
                    // System.out.println( nfa.name );
                    // System.out.println( dfa.name );
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

    public static<T> List<NFA<T>> collectFinals( NFA<T> start ) {
        List<NFA<T>> li = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        _collectFinals( start, li, visited );

        return li;
    }

    private static<T> void _collectFinals( NFA<T> state, List<NFA<T>> li, Set<String> visited ) {
        if ( visited.contains( state.name ) ) return;
        visited.add( state.name );
        if ( state.isFinal ) li.add( state );
        for ( Edge<NFA<T>> e : state.out )
            _collectFinals( e.to, li, visited );
    }

    private static<T> String genNameFromStates( Set<NFA<T>> states ) {
        if ( states.size() == 0 ) return "";
        // System.out.println("GetNameFromStates");
        // states.forEach( state -> System.out.println( "\tNFA: " + state.name ) );
        String name = "";
        for ( NFA<T> state : states ) name += state.name() + ",";
        name = name.substring(0, name.length() - 1);
        return name;
    }

    private static<T> Set<NFA<T>> computeEpsilonClosure( Set<NFA<T>> nfas  ) {
        LinkedList<NFA<T>> input = new LinkedList<>();
        Set<NFA<T>> output = new HashSet<>();
        Set<String> visited = new HashSet<>();
        input.addAll( nfas );
        output.addAll( nfas );

        while ( input.size() > 0 ) {
            NFA<T> state = input.pop();
            
            if ( visited.contains( state.name() ) ) continue;
            
            visited.add( state.name() );
            
            for ( Edge<NFA<T>> edge : state.out() ) {
                if ( edge.kind != EdgeKind.EPSILON ) continue;

                output.add( edge.to() );   
                input.add( edge.to() );
            }
        }
        // System.out.println( "-.-.-.-.-.-.-.-.-.-" );

        // System.out.println( output );
        // System.out.println( "-.-.-.-.-.-.-.-.-.-" );

        return output;
    }

    public static<T> List<NFA<T>> collectStates( NFA<T> nfa ) {
        List<NFA<T>> q = new LinkedList<>();
        _collectStates( nfa, q );

        return q;
    }

    private static<T> void _collectStates( NFA<T> nfa, List<NFA<T>> q ) {
        if ( q.contains( nfa ) ) return;
        q.add( nfa );
        for ( Edge<NFA<T>> e : nfa.out() )
            _collectStates( e.to(), q );
    }

    public NFA<T> copy() {
        
        Map<NFA<T>, NFA<T>> visited = new HashMap<>();
        NFA<T> q = _copy( visited );

        return q;
    }
 
   // TODO: check if this works correctly
    private NFA<T> _copy( Map<NFA<T>, NFA<T>> visited ) {
        if ( visited.containsKey( this ) ) return visited.get( this );
        
        NFA<T> q = new NFA<>( name + "CP", isFinal );
        visited.put( this, q );
        
        for ( Edge<NFA<T>> e : out ) {
            Edge<NFA<T>> ne = new Edge<>( e.accept );
            ne.kind = e.kind;
            ne.from = this;
            
            NFA<T> nto = e.to._copy( visited );
            
            ne.to = nto;

            q.out.add( ne );
            nto.in.add( ne );
        }


        return q;
    }

    public static<T> String getStringRepresentation( NFA<T> nfa ) {
        String s = "";
        List<NFA<T>> states = NFA.collectStates( nfa );
        for ( NFA<T> state : states ) {
            s += ( "State " + state.name + "\n" );
            s += ( "\tisFinal = " + state.isFinal + "\n" );
            s += ( "\tout edges:" + "\n" );
            for ( Edge<NFA<T>> e : state.out ) {
                s += ( "\t\t[" + (e.kind == EdgeKind.STD ? e.accept : e.kind ) + " -> State " + e.to.name + "]" + "\n" );
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

    public List<Edge<NFA<T>>> in() {
        return in;
    }

    public List<Edge<NFA<T>>> out() {
        return out;
    }

    public boolean isFinal() {
        return isFinal;
    }
    
}

