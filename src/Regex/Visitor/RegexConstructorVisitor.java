package src.Regex.Visitor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import src.Regex.NFA.NFA_state;
import src.Regex.Regex.Token.*;
import src.Regex.Utils.Utils;

public class RegexConstructorVisitor<T> extends VisitorBase {
    

    public LinkedList<NFA_state<T>> nfaStack;
    public Map<String, Set<NFA_state<T>>> finalStates;

    public NFA_state<T> result;



    public RegexConstructorVisitor() {
        nfaStack = new LinkedList<>();
        finalStates = new HashMap<>();

        // nfaStack.add( new NFA_state( "start_regex" ) );
    }

    // public void preVisit_printme( Token token ) {
        // System.out.println( token.toString() );
    // }

    // TODO: Check if this fucks something up...
    private void addFinish( NFA_state<T> begin, NFA_state<T> end ) {
        end.isFinal = true;
        if ( !finalStates.containsKey( begin.name ) ) 
            finalStates.put( begin.name, Utils.toSet( end ) );
        else 
            finalStates.get( begin.name ).add( end );

    }

    // TODO: Check if this fucks something up...
    private void clearFinish( NFA_state<T> state ) {
        if ( finalStates.containsKey( state.name ) )  {
            for ( NFA_state<T> finals : finalStates.get( state.name ) )
                finals.isFinal = false;
        
            finalStates.put( state.name, Utils.toSet() );
        }
    }

    private Set<NFA_state<T>> getFinish( NFA_state<T> state ) {
        return finalStates.getOrDefault( state.name, Utils.toSet() );
    }

    public void visit_leaf( Token token ) {

        if ( token instanceof TokenChar ) {
            NFA_state<T> state = null;
            NFA_state<T> end = null;

            if ( ((TokenChar)token).kind == TokenCharKind.CHAR ) {
                // System.out.println( "tokenChar" );
                state = new NFA_state<>();
                end = state.registerWord( ((TokenChar)token).value + "", true );

                // addFinish( charState, end );
            } else if  ( ((TokenChar)token).kind == TokenCharKind.WILD ) {
                // System.out.println( "\t\tTokenRegWild" );
                state = new NFA_state<>();
                end = new NFA_state<>();
                end.isFinal = true;
                state.addEdge( true, end );
    
            } else {
                // System.out.println( "SOMETHING BAD HAPPEND IN visit_leaf" );
                System.exit(-1);
            }
            nfaStack.push( state );
            addFinish( state, end );

        } else if ( token instanceof TokenRegExp ) {
            // System.out.println( "tokenExp" );
            // TokenRegExp _token = (TokenRegExp) token;
            // if ( _token.right == null ) {
                // DO nothing?
            // } else {
            
            // }
        } else if ( token instanceof TokenRegUdtryk ) {
            
            // System.out.println( "tokenUdtryk" );


        } 
        // System.out.println( nfaStack.size() );
    }

    public void postVisit_star( Token token ) {
        if ( token instanceof TokenRegRepetition ) { // Needs expansion when more repetitions are added
            // System.out.println( "tokenRep************************'" );
            TokenRegRepetition _token = (TokenRegRepetition) token;

            NFA_state<T> start = new NFA_state<>();

            NFA_state<T> state = nfaStack.pop();

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
            for ( NFA_state<T> finish : getFinish( state ) ) {
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
                NFA_state<T> right = nfaStack.pop();
                NFA_state<T> left = nfaStack.pop();

                NFA_state<T> concat = new NFA_state<>();

                concat.addEdge(left);
                for ( NFA_state<T> leftFinish : getFinish( left ) ) {
                    // System.out.println( "finish.....");
                    leftFinish.addEdge(right);
                    leftFinish.isFinal = false;
                }

                clearFinish( left );
                for ( NFA_state<T> newFinish : getFinish( right ) ) {
                    addFinish( concat, newFinish );
                }

                nfaStack.push( concat );

                // System.out.println( NFA_state.getStringRepresentation( concat ) );

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
                NFA_state<T> right = nfaStack.pop();
                NFA_state<T> left = nfaStack.pop();

                NFA_state<T> union = new NFA_state<>();
                union.addEdge( left );
                union.addEdge( right );
                nfaStack.push( union );

                for ( NFA_state<T> newFinish : getFinish( left ) ) {
                    addFinish( union, newFinish );
                }

                for ( NFA_state<T> newFinish : getFinish( right ) ) {
                    addFinish( union, newFinish );
                }

                // System.out.println( "Union" + union.name + " has finish states:");
                // System.out.println( getFinish( union ));


                clearFinish( left );
                clearFinish( right );

                // System.out.println( NFA_state.getStringRepresentation( union ) );
            }
        }
    }

    public void postVisit_symbol( Token token ) {
        if ( token instanceof TokenRegSymbol ) {
            // System.out.println( "tokenSym" );
            TokenRegSymbol _token = (TokenRegSymbol) token;
            // sym -> ( nExp )
            if ( _token.value instanceof TokenRegExp ) {
                // Do nothing. Because of the parenthisese

            // sym -> tChar
            } else {
                // Also do nothing... right??
                // NFA_state<T> charState = nfaStack.pop(); 

            }
        }
    }

    public void postVisit_range( Token token ) {
        if ( token instanceof TokenRegRange ) {
            // System.out.println( "TokenRegRange" );
            TokenRegRange _token = (TokenRegRange) token;
            if ( _token.right == null ) {
                if ( _token.kind == TokenRangeKind.INT ) {
                    
                    int reps = ((TokenRegDigit)_token.left).value;
                    
                    NFA_state<T> anchor = null;

                    NFA_state<T> stateToRepeat = nfaStack.pop();
                    
                    
                    NFA_state<T> start = new NFA_state<>( "RangeStart" );
                    nfaStack.push( start );

                    for ( int i = 0; i < reps; ++i ) {
                        NFA_state<T> current = stateToRepeat.copy();
                    
                        start.addEdge( current );

                        anchor = new NFA_state<>( "Anchor" );
                        

                        for ( NFA_state<T> finish : NFA_state.collectFinals( current ) ) {
                            finish.addEdge( anchor );
                            finish.isFinal = false;
                        }

                        start = anchor; 
                    }
                    addFinish( nfaStack.peek(), anchor );

                } else if ( _token.kind == TokenRangeKind.CHAR ) {
                    // System.out.println( "FAILURE");
                    System.exit( -1 );
                    
                } else {
                    // System.out.println( "FAILURE");
                    System.exit( -1 );
                }
            } else {
                if ( _token.kind == TokenRangeKind.INT ) {
                    // System.out.println( "FAILURE");
                    System.exit( -1 );

                } else if ( _token.kind == TokenRangeKind.CHAR ) {
                    // System.out.println( "FAILURE");
                    System.exit( -1 );
                    
                } else {
                    // System.out.println( "FAILURE");
                    System.exit( -1 );
                }
            }
            

        } 
    }

    public void postVisit_root( Token token ) {
        // System.out.println( nfaStack.size() );
        if ( token instanceof TokenRoot ) {
            // System.out.println( "tokenRoot" );
            // TokenRoot _token = (TokenRoot) token;
            // Convert to dfa. or not.
            NFA_state<T> tok = nfaStack.pop();
            // System.out.println( "***************" );
            // System.out.println( NFA_state.getStringRepresentation(tok) );
            // System.out.println( "***************" );
            // result = NFA_state.toDFA( tok );
            result = tok;
            // System.out.println( "asd " + result.name );


        } 
    }

}
