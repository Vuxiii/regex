package com.vuxiii.DFANFA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vuxiii.Utils.*;;

public class NFA<T> implements NameInterface {
    private boolean hasAnyEdge = false;
    private boolean hasDigitEdge = false;
    private boolean hasAlphEdge = false;
    private boolean hasEpsEdge = false;
    
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
        return hasEpsEdge;
    }

    public boolean hasAnyEdge() {
        return hasAnyEdge;
    }

    public boolean hasDigitEdge() {
        return hasDigitEdge;
    }

    public boolean hasAlphEdge() {
        return hasAlphEdge;
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
        if ( kind == EdgeKind.ALPHS )
            hasAlphEdge = true;
        else if ( kind == EdgeKind.DIGITS )
            hasDigitEdge = true;
        else if ( kind == EdgeKind.ANY )
            hasAnyEdge = true;
        else if ( kind == EdgeKind.EPSILON )
            hasEpsEdge = true;

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

        hasEpsEdge = true;

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
    
    private static <T> Set<NFA<T>> extractToStates( Set<Edge<NFA<T>>> edges ) {
        Set<NFA<T>> nfas = new HashSet<>();

        for ( Edge<NFA<T>> edge : edges ) 
            nfas.add( edge.to );
        return nfas;
    }
    
    private static <T> void addStates( Map<Set<NFA<T>>, DFA<T>> cachedStates, Map<DFA<T>, Set<NFA<T>>> DFAToNFA, LinkedList<DFA<T>> queue, DFA<T> current, Set<NFA<T>> nfas, EdgeKind kind ) {
        if ( nfas.size() > 0 ) {
            DFA<T> newState;
            if ( cachedStates.containsKey( nfas ) ) {
                // System.out.println( "State: " + genNameFromStates( NFAStates ) + " already exists" );
                newState = cachedStates.get( nfas );
            }  else {
                newState = new DFA<T>( genNameFromStates( nfas ) );
                cachedStates.put( nfas, newState );
                DFAToNFA.put( newState, nfas );
                // System.out.println( "Creating new State: " + newState.name );
                queue.add( newState ); // Might need to move this outside of this clause.
            }
            // System.out.println( "New State: " + newState.name );
            // Add the edge to it.
            current.addEdge( kind, newState ); 
        }
    }

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
            
            Map<Character, Set<NFA<T>>> reachable = new HashMap<>();
            Set<Edge<NFA<T>>> anyReachable = new HashSet<>();
            Set<Edge<NFA<T>>> digitReachable = new HashSet<>();
            Set<Edge<NFA<T>>> alphReachable = new HashSet<>();

            // System.out.println( "Current state " + name );

            for ( NFA<T> state : closure ) {
                // System.out.println( "At NFA: " + state.name );
                for ( Edge<NFA<T>> edge : state.out ) {
                    if ( edge.kind == EdgeKind.EPSILON || edge.kind == EdgeKind.STD ) continue;
                    
                    if ( edge.kind == EdgeKind.ANY ) {
                        anyReachable.add( edge );
                    } else if ( edge.kind == EdgeKind.ALPHS ) {
                        alphReachable.add( edge );
                    }  else if ( edge.kind == EdgeKind.DIGITS ) {
                        digitReachable.add( edge );
                    } 
                }
            }


            // Find all the edges reachable from this "combined" state
            for ( NFA<T> state : closure ) {
                // System.out.println( "At NFA: " + state.name );
                for ( Edge<NFA<T>> edge : state.out ) {
                    if ( edge.kind != EdgeKind.STD ) continue;
                    
                    // System.out.println( "\t" + edge );
                    // System.out.println( "\tAdding " + anyReachable
                    //                                     .parallelStream()
                    //                                     .filter( anyEdge -> !anyEdge.from.equals( edge.from ) )
                    //                                     .filter( anyEdge -> !anyEdge.to.equals( edge.from ) )
                    //                                     .collect( Collectors.toSet() ) );
                    if ( !reachable.containsKey( edge.accept ) ) {
                        Set<NFA<T>> s = new HashSet<>();
                        s.add( edge.to );
                        s.addAll( extractToStates(anyReachable
                                                        .parallelStream()
                                                        .filter( anyEdge -> !anyEdge.from.equals( edge.from ) )
                                                        .filter( anyEdge -> !anyEdge.to.equals( edge.from ) )
                                                        .collect( Collectors.toSet() ) ) );
                        if ( Character.isDigit( edge.accept ) ) {
                            s.addAll( extractToStates(digitReachable
                                                        .parallelStream()
                                                        .filter( anyEdge -> !anyEdge.from.equals( edge.from ) )
                                                        .filter( anyEdge -> !anyEdge.to.equals( edge.from ) )
                                                        .collect( Collectors.toSet() ) ) );
                        } else if ( Character.isLetter( edge.accept ) ) {
                            s.addAll( extractToStates(alphReachable
                                                        .parallelStream()
                                                        .filter( anyEdge -> !anyEdge.from.equals( edge.from ) )
                                                        .filter( anyEdge -> !anyEdge.to.equals( edge.from ) )
                                                        .collect( Collectors.toSet() ) ) );
                        }
                        reachable.put( edge.accept, s );
                    } else {
                        reachable.get( edge.accept ).add( edge.to );
                        reachable.get( edge.accept ).addAll( extractToStates(anyReachable
                                                        .parallelStream()
                                                        .filter( anyEdge -> !anyEdge.from.equals( edge.from ) )
                                                        .filter( anyEdge -> !anyEdge.to.equals( edge.from ) )
                                                        .collect( Collectors.toSet() ) ) );
                        if ( Character.isDigit( edge.accept ) ) {
                            reachable.get( edge.accept ).addAll( extractToStates(digitReachable
                                                        .parallelStream()
                                                        .filter( anyEdge -> !anyEdge.from.equals( edge.from ) )
                                                        .filter( anyEdge -> !anyEdge.to.equals( edge.from ) )
                                                        .collect( Collectors.toSet() ) ) );
                        } else if ( Character.isLetter( edge.accept ) ) {
                            reachable.get( edge.accept ).addAll( extractToStates(alphReachable
                                                        .parallelStream()
                                                        .filter( anyEdge -> !anyEdge.from.equals( edge.from ) )
                                                        .filter( anyEdge -> !anyEdge.to.equals( edge.from ) )
                                                        .collect( Collectors.toSet() ) ) );
                        }
                    }
                }
                // System.out.print( "\t\tCan go to -> " );
                // reachable.values().forEach( c -> c.forEach( (s) -> System.out.print( s.name + ", " ) ) );
                // System.out.println();
            }
            // for ( Character c : reachable.keySet() ) {
            //     System.out.println( c + " -> " + genNameFromStates( reachable.get( c ) ) ); 
            // }
            // System.out.println( "reachable: " + (reachable.values()) );

            // System.out.println( "any reachable: " + (anyReachable) );
            // System.out.println( "digit reachable: " + (digitReachable) );
            // System.out.println( "alph reachable: " + (alphReachable) );

            for ( Edge<NFA<T>> edge : anyReachable ) {
                NFA<T> from = edge.from;
                
                for ( char c : reachable.keySet() ) {
                    for ( NFA<T> st : reachable.get(c) ) {
                        if ( from.name.equals( st.name ) ) {
                            // System.out.println( "Found " + st.name );

                            reachable.get(c).add( edge.to );
                            // anyReachable.remove( edge ); // Might bug out.
                        }
                    }
                }
            } 

            // System.out.println( "any reachable: " + (anyReachable) );

            // System.out.println( reachable );

            // Step 1: Start with the ANY reachable
            //      If there is an any state, we can skip the next steps
            // Step 2: Are there any DIGIT reachable
            //      If there exists any 'digit' edges, we can ignore them in the STD step
            // Step 3: Are there any ALPH reachable
            //      If there exists any 'alph' edges, we can ignore them in the STD step
            // Step 4: Are there any STD reachable
            //      Simply add the edges to the 'to' states




            // Create all the reachable states.
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
                    queue.add( newState );
                }
                // System.out.println( "New State: " + newState.name );

                // Add the edge to it.
                current.addEdge( c, newState ); 
            }

            addStates( cachedStates, DFAToNFA, queue, current, extractToStates( anyReachable ), EdgeKind.ANY );
            addStates( cachedStates, DFAToNFA, queue, current, extractToStates( alphReachable ), EdgeKind.ALPHS );
            addStates( cachedStates, DFAToNFA, queue, current, extractToStates( digitReachable ), EdgeKind.DIGITS );


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
                    System.out.println( "Constructor for " + dfa.name() + ": " + dfa.constructor );
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

