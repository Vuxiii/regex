package com.vuxiii.Visitor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vuxiii.DFANFA.EdgeKind;
import com.vuxiii.DFANFA.NFA;
import com.vuxiii.Regex.Token.*;
import com.vuxiii.Utils.Utils;

public class RegexConstructorVisitor<T> extends VisitorBase {
    

    public LinkedList<NFA<T>> nfaStack;
    public Map<String, Set<NFA<T>>> finalStates;

    public NFA<T> result;



    public RegexConstructorVisitor() {
        nfaStack = new LinkedList<>();
        finalStates = new HashMap<>();

        // nfaStack.add( new NFA_state( "start_regex" ) );
    }

    // public void preVisit_printme( Token token ) {
        // System.out.println( token.toString() );
    // }

    // TODO: Check if this fucks something up...
    private void addFinish( NFA<T> begin, NFA<T> end ) {
        end.isFinal = true;
        if ( !finalStates.containsKey( begin.name ) ) 
            finalStates.put( begin.name, Utils.toSet( end ) );
        else 
            finalStates.get( begin.name ).add( end );

    }

    // TODO: Check if this fucks something up...
    private void clearFinish( NFA<T> state ) {
        if ( finalStates.containsKey( state.name ) )  {
            for ( NFA<T> finals : finalStates.get( state.name ) )
                finals.isFinal = false;
        
            finalStates.put( state.name, Utils.toSet() );
        }
    }

    private Set<NFA<T>> getFinish( NFA<T> state ) {
        return finalStates.getOrDefault( state.name, Utils.toSet() );
    }

    public void visit_leaf( Token token ) {

        if ( token instanceof TokenChar ) {
            NFA<T> state = null;
            NFA<T> end = null;

            if ( ((TokenChar)token).kind == TokenCharKind.CHAR ) {
                // System.out.println( "tokenChar" );
                state = new NFA<>();
                end = state.registerWord( ((TokenChar)token).value + "", true );

                // addFinish( charState, end );
            } else if  ( ((TokenChar)token).kind == TokenCharKind.WILD ) {
                // System.out.println( "\t\tTokenRegWild" );
                state = new NFA<>();
                end = new NFA<>();
                end.isFinal = true;
                state.addEdge( EdgeKind.ANY, end );
    
            } else if ( ((TokenChar)token).kind == TokenCharKind.ALPHA ) {
                state = new NFA<>();
                end = new NFA<>();
                end.isFinal = true;
                state.addEdge( EdgeKind.ALPHS, end );
            } else if ( ((TokenChar)token).kind == TokenCharKind.DIGIT ) {
                state = new NFA<>();
                end = new NFA<>();
                end.isFinal = true;
                state.addEdge( EdgeKind.DIGITS, end );
            } else {
                System.out.println( "SOMETHING BAD HAPPEND IN visit_leaf" );
                System.exit(-1);
            }
            nfaStack.push( state );
            addFinish( state, end );

        } else if ( token instanceof TokenRegDigit ) {
            NFA<T> state = new NFA<>();;
            NFA<T> end = state.registerWord( ((TokenRegDigit)token).value + "", true );
            nfaStack.push( state );
            addFinish( state, end );
        }
    }

    public void postVisit_udtryk( Token token ) {
        if ( token instanceof TokenRegUdtryk ) {
            // System.out.println( "Implement me" );
            TokenRegUdtryk _token = (TokenRegUdtryk) token;
            if ( _token.rep == null ) return;

            System.out.println( "ØØØØØØØ" + _token.rep );

            if ( ((TokenRegRange)_token.value).kind == TokenRangeKind.INT ) { // Checkmig lige.
                 
                System.out.println( "int range" );



                System.exit(-1);
            } else if ( ((TokenRegRange)_token.value).kind == TokenRangeKind.CHAR ) {

                System.out.println( "char range" );
                System.exit(-1);
            } else {
                System.out.println( "Failure in udtryk with ranges" );
                System.exit(-1);
            }

            
        }
    }

    public void postVisit_star( Token token ) {
        if ( token instanceof TokenRegRepetition ) { // Needs expansion when more repetitions are added
            // System.out.println( "tokenRep************************'" );
            TokenRegRepetition _token = (TokenRegRepetition) token;
            if ( _token.value == null ) return;

            NFA<T> start = new NFA<>();

            NFA<T> state = nfaStack.pop();

            // System.out.println( "-------------------" );
            // System.out.println( "-------------------" );
            // System.out.println( NFA_state.getStringRepresentation( state ) );
            // System.out.println( "-------------------" );

            start.addEdge( state );
            start.isFinal = true;
            addFinish( start, start );
            
            // System.out.println( NFA_state.getStringRepresentation( start ) );
            // System.out.println( "-------------------" );
            
            // System.out.println( "Checking state" + state.name + " finishes" );
            // System.out.println( "All the finish for this state are " + getFinish( state ) );
            for ( NFA<T> finish : getFinish( state ) ) {
                // System.out.println( "Final state are: " + finish.name );
                finish.isFinal = true;
                finish.addEdge( start );
                addFinish( start, finish );
            }
            // System.out.println( NFA_state.getStringRepresentation( start ) );
            // System.out.println( "-------------------" );
            // System.out.println( nfaStack.size());
            // System.out.println( "-------------------" );

            nfaStack.push( start );

            // System.out.println( nfaStack );

            // System.exit(-1);

        }
    }

    public void postVisit_concat( Token token ) {
        if ( token instanceof TokenRegConcat ) {
            // System.out.println( "tokenConcat" );
            TokenRegConcat _token = (TokenRegConcat) token;
            if ( _token.right == null ) {
                // Do nothing?

                // System.out.println( "In do nothing...");
                // System.out.println( _token );
            } else {
                // System.out.println( "We have a left and a right!!!!" );
                NFA<T> right = nfaStack.pop();
                NFA<T> left = nfaStack.pop();

                NFA<T> concat = new NFA<>();

                concat.addEdge( EdgeKind.EPSILON, left );
                for ( NFA<T> leftFinish : NFA.collectFinals( left ) ) {
                    // System.out.println( "finish.....");
                    leftFinish.addEdge( EdgeKind.EPSILON, right);
                    leftFinish.isFinal = false;
                }

                clearFinish( left );
                for ( NFA<T> newFinish : NFA.collectFinals( right ) ) {
                    addFinish( concat, newFinish );
                }

                nfaStack.push( concat );

                // System.out.println("concat".repeat(5));
                // System.out.println( NFA.getStringRepresentation( concat ) );
                // System.out.println("concat".repeat(5));

            }
        }
    }

    public void postVisit_union( Token token ) {
        if ( token instanceof TokenRegUnion ) {
            // System.out.println( "tokenUnion" );
            TokenRegUnion _token = (TokenRegUnion) token;
            if ( _token.right == null ) {
                // Do nothing?
                // System.out.println( "In do nothing...");
            } else {
                // System.out.println( "Not in do nothing...");
                NFA<T> right = nfaStack.pop();
                NFA<T> left = nfaStack.pop();

                NFA<T> union = new NFA<>( "union" );
                union.addEdge( left );
                union.addEdge( right );
                nfaStack.push( union );

                // System.out.println( NFA.getStringRepresentation(union));
                // System.out.println( NFA.collectFinals( union ));
                for ( NFA<T> newFinish : NFA.collectFinals( left ) ) {
                    addFinish( union, newFinish );
                }

                for ( NFA<T> newFinish : NFA.collectFinals( right ) ) {
                    addFinish( union, newFinish );
                }


                // System.out.println( NFA.getStringRepresentation(union));

                // clearFinish( left );
                // clearFinish( right );

            }
        }
    }

    private void repeat( NFA<T> stateToRepeat, int reps ) {
        NFA<T> anchor = null;

        NFA<T> start = new NFA<>( "RangeStart" );
        nfaStack.push( start );
        // start.isFinal = false;

        for ( int i = 0; i < reps; ++i ) {
            NFA<T> current = stateToRepeat.copy();
        
            start.addEdge( current );

            anchor = new NFA<>( "Anchor" );
            

            for ( NFA<T> finish : NFA.collectFinals( current ) ) {
                finish.addEdge( anchor );
                finish.isFinal = false;
            }

            start = anchor; 
        }
        clearFinish(nfaStack.peek());
        addFinish( nfaStack.peek(), anchor );
    }

    public void postVisit_IntRange( Token token ) {
        if ( token instanceof TokenRegIntRange ) {
            // System.exit(-1);
            // System.out.println( "TokenRegRange" );

            TokenRegIntRange range = (TokenRegIntRange) token;
            
            if ( range.right == null ) {
                int reps = range.left.value;
                if ( reps <= 0 ) {
                    System.out.println( "Reps should be above 0" );
                    System.exit(-1);
                }
                repeat( nfaStack.pop(), reps );
            } else {
                // System.out.println( nfaStack );
                // TokenRegIntRange range = (TokenRegIntRange) _token.range;
                NFA<T> stateToRepeat = nfaStack.pop();
                int reps = range.right.value - range.left.value;
                if ( range.left.value <= 0 || range.left.value >= range.right.value ) {
                    System.out.println( "Invalid range: " + range.left.value + " to " + range.right.value );
                    System.exit(-1);
                }
                repeat( stateToRepeat, range.left.value );
                
                NFA<T> anchor = null;
                Set<NFA<T>> start = Utils.toSet( NFA.collectFinals( nfaStack.peek() ) );
                // System.out.println( start );
                
                for ( int i = 0; i < reps; ++i ) {
                    NFA<T> current = stateToRepeat.copy();
                    
                    start.forEach( startState -> startState.addEdge( current ) );

                    anchor = new NFA<>( "Anchor" );
                    
                    
                    start.addAll( NFA.collectFinals( current ) );
                    for ( NFA<T> finish : start ) {
                        finish.addEdge( anchor );
                        addFinish( nfaStack.peek(), anchor );
                    }
                    // start.add( anchor );
                    // System.out.println( "--------------------------------------------");
                    // System.out.println( start );
                    // System.exit(-1);
                }
                // System.out.println( NFA.getStringRepresentation(nfaStack.peek()));
            }

            

        } 
    }

    public void postVisit_root( Token token ) {
        // System.out.println( nfaStack.size() );
        if ( token instanceof TokenRoot ) {
            // System.out.println( "tokenRoot" );
            // TokenRoot _token = (TokenRoot) token;
            // Convert to dfa. or not.
            NFA<T> tok = nfaStack.pop();
            System.out.println( "root***************" );
            System.out.println( NFA.getStringRepresentation(tok) );
            System.out.println( "root***************" );
            // System.out.println( nfaStack.size() );
            // result = NFA_state.toDFA( tok );
            result = tok;
            // System.out.println( "asd " + result.name );


        } 
    }

}
