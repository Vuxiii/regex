package src.NFA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import src.DFA.DFA_state;
import src.Utils.Utils;

public class NFA_state {
    public String name;
    
    public boolean isFinal = false;

    List<NFA_edge> out;
    List<NFA_edge> in;
    public static int c = 0;

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

        for ( NFA_edge e : out ) {
            if ( e.accept == c )
                return true;
            if ( e.isEpsilon )
                found = e.to.canConsume( c );
        }

        return found;
    }

    public boolean hasEpsilonEdge() {
        for ( NFA_edge e : out ) 
            if ( e.isEpsilon )
                return true;
        return false;
    }

    public NFA_state registerWord( String s ) {
        NFA_state start = new NFA_state();

        addEdge( start );
        return start._registerWord( s );
    }

    private NFA_state _registerWord( String s ) {
        if ( s.length() == 0 ) return this;

        char c = s.charAt( 0 );

        NFA_state newState = new NFA_state( "", s.length() == 1 ); 
        addEdge( c, newState );

        return newState._registerWord( s.substring( 1 ) );
    }


    public NFA_state registerWord( String s, boolean shouldAccept ) { // Add should accept state.
        NFA_state start = new NFA_state();

        addEdge( start );
        return start._registerWord( s, shouldAccept );
    }

    
    private NFA_state _registerWord( String s, boolean shouldAccept ) {
        if ( s.length() == 0 ) return this;

        char c = s.charAt( 0 );

        NFA_state newState = new NFA_state( "", s.length() == 1 ? shouldAccept : false ); 
        addEdge( c, newState );

        return newState._registerWord( s.substring( 1 ), shouldAccept );
    }

    /**
     * Adds 
     * @param c
     * @param to
     */
    public void addEdge( char c, NFA_state to ) {
        // Utils.log( "Adding edge " + name + " -" + c + "> " + to.name() );
        NFA_edge e = new NFA_edge( c );
        e.from = this;
        e.to = to;

        out.add( e );
        to.in().add( e );

    }

    /**
     * Adds 
     * @param to
     */
    public void addEdge( boolean isWild, NFA_state to ) {
        // Utils.log( "Adding edge " + name + " -" + c + "> " + to.name() );
        NFA_edge e = new NFA_edge( isWild );
        e.from = this;
        e.to = to;

        out.add( e );
        to.in().add( e );

    }

    /**
     * Adds an epsilon edge to the given state.
     * @param to The state to go to.
     */
    public void addEdge( NFA_state to ) {
        // Utils.log( "Adding edge " + name + " -epsilon1> " + to.name() );

        NFA_edge e = new NFA_edge();
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
    public List<NFA_state> consume( char c ) {
        List<NFA_state> list = new ArrayList<>();
        
        for ( NFA_edge e : out ) {
            if ( e.accept == c ) 
                list.add( e.to );
        }

        return list;
    }

    /**
     * Consumes all epsilon edges from this state.
     * @return
     */
    public List<NFA_state> consume() {

        List<NFA_state> list = new ArrayList<>();
        
        for ( NFA_edge e : out ) {
            if ( e.isEpsilon ) 
                list.add( e.to );
        }

        return list;
    }


    public static DFA_state toDFA( NFA_state begin ) {

        Map<Set<NFA_state>, DFA_state> cachedStates = new HashMap<>();
        Map<DFA_state, Set<NFA_state>> DFAToNFA = new HashMap<>();

        LinkedList<DFA_state> queue = new LinkedList<>();
        Set<NFA_state> closure = computeEpsilonClosure( Set.of( begin ) );
        String name = genNameFromStates( closure );
        DFA_state dfaBegin = new DFA_state( name );

        DFAToNFA.put( dfaBegin, closure );

        // DFA_state current = dfaBegin;
        queue.add( dfaBegin );
        do {
            DFA_state current = queue.pop();
            // System.out.println( "Visiting DFA " + current.name );
            Map<NFA_edge, Set<NFA_state>> reachable = new HashMap<>();

            // Add all the edges reachable from this "combined" state
            for ( NFA_state state : closure ) {
                // System.out.println( "At NFA: " + state.name );
                for ( NFA_edge edge : state.out ) {
                    if ( edge.isEpsilon ) continue;
                    
                    // System.out.println( "\tChecking edge: " + edge );
                    if ( !reachable.containsKey( edge ) ) {
                        Set<NFA_state> s = new HashSet<>();
                        s.add( edge.to );
                        reachable.put( edge, s );
                    } else {
                        reachable.get( edge ).add( edge.to );
                    }
                }
                // System.out.print( "\t\tCan go to -> " );
                // reachable.values().forEach( c -> c.forEach( (s) -> System.out.print( s.name + ", " ) ) );
                // System.out.println();
            }

            // Create all these states.
            for ( NFA_edge c : reachable.keySet() ) {
                // System.out.println( "At charachter: " + c);
                Set<NFA_state> NFAStates = reachable.get( c );
                DFA_state newState;
                // Check if it has already been made
                if ( cachedStates.containsKey( NFAStates ) ) {
                    // System.out.println( "State: " + genNameFromStates( NFAStates ) + " already exists" );
                    newState = cachedStates.get( NFAStates );
                }  else {
                    newState = new DFA_state( genNameFromStates( NFAStates ) );
                    cachedStates.put( NFAStates, newState );
                    DFAToNFA.put( newState, NFAStates );
                    // System.out.println( "Creating new State: " + newState.name );
                    queue.add( newState ); // Might need to move this outside of this clause.
                }

                // Add the edge to it.
                if ( c.acceptAny )
                    current.addEdge( (boolean) c.acceptAny, newState );
                else
                    current.addEdge( c.accept, newState );

                
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
        for ( DFA_state dfa : DFAToNFA.keySet() ) {
            // System.out.println( "checking DFA state" + dfa.name );
            for ( NFA_state nfa : DFAToNFA.get( dfa ) ) {
                if ( nfa.isFinal ) {
                    dfa.isFinal = true;
                    break;
                }
            }
        }
        // System.out.println( "END finish states for DFA" );

        return dfaBegin;
    }

    public static List<NFA_state> collectFinals( NFA_state start ) {
        List<NFA_state> li = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        _collectFinals( start, li, visited );

        return li;
    }

    private static void _collectFinals( NFA_state state, List<NFA_state> li, Set<String> visited ) {
        if ( visited.contains( state.name ) ) return;
        visited.add( state.name );
        if ( state.isFinal ) li.add( state );
        for ( NFA_edge e : state.out )
            _collectFinals( e.to, li, visited );
    }

    private static String genNameFromStates( Set<NFA_state> states ) {
        if ( states.size() == 0 ) return "";
        // System.out.println("GetNameFromStates");
        // states.forEach( state -> System.out.println( "\tNFA: " + state.name ) );
        String name = "";
        for ( NFA_state state : states ) name += state.name() + ",";
        name = name.substring(0, name.length() - 1);
        return name;
    }

    private static Set<NFA_state> computeEpsilonClosure( Set<NFA_state> nfas  ) {
        LinkedList<NFA_state> input = new LinkedList<>();
        Set<NFA_state> output = new HashSet<>();
        Set<String> visited = new HashSet<>();
        input.addAll( nfas );
        output.addAll( nfas );

        while ( input.size() > 0 ) {
            NFA_state state = input.pop();
            
            if ( visited.contains( state.name() ) ) continue;
            
            visited.add( state.name() );
            
            for ( NFA_edge edge : state.out() ) {
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

    public static List<NFA_state> collectStates( NFA_state nfa ) {
        List<NFA_state> q = new LinkedList<>();
        _collectStates( nfa, q );

        return q;
    }

    private static void _collectStates( NFA_state nfa, List<NFA_state> q ) {
        if ( q.contains( nfa ) ) return;
        q.add( nfa );
        for ( NFA_edge e : nfa.out() )
            _collectStates( e.to(), q );
    }

    public NFA_state copy() {
        
        Map<NFA_state, NFA_state> visited = new HashMap<>();
        NFA_state q = _copy( visited );

        return q;
    }
 
   // TODO: check if this works correctly
    private NFA_state _copy( Map<NFA_state, NFA_state> visited ) {
        if ( visited.containsKey( this ) ) return visited.get( this );
        
        NFA_state q = new NFA_state( name + "CP", isFinal );
        visited.put( this, q );
        
        for ( NFA_edge e : out ) {
            NFA_edge ne = new NFA_edge( e.accept );
            ne.isEpsilon = e.isEpsilon;
            ne.acceptAny = e.acceptAny;
            ne.from = this;
            
            NFA_state nto = e.to._copy( visited );
            
            ne.to = nto;

            q.out.add( ne );
            nto.in.add( ne );
        }


        return q;
    }

    public static String getStringRepresentation( NFA_state nfa ) {
        String s = "";
        List<NFA_state> states = NFA_state.collectStates( nfa );
        for ( NFA_state state : states ) {
            s += ( "State " + state.name + "\n" );
            s += ( "\tisFinal = " + state.isFinal + "\n" );
            s += ( "\tout edges:" + "\n" );
            for ( NFA_edge e : state.out ) {
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

    public List<NFA_edge> in() {
        return in;
    }

    public List<NFA_edge> out() {
        return out;
    }

    public boolean isFinal() {
        return isFinal;
    }
    
}

