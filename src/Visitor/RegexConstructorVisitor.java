package src.Visitor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import src.NFA.NFA_state;
import src.Regex.Token.*;
import src.Utils.Utils;

public class RegexConstructorVisitor extends VisitorBase {
    

    public LinkedList<NFA_state> nfaStack;
    public Map<String, Set<NFA_state>> finalStates;

    public NFA_state result;



    public RegexConstructorVisitor() {
        nfaStack = new LinkedList<>();
        finalStates = new HashMap<>();

        // nfaStack.add( new NFA_state( "start_regex" ) );
    }

    // public void preVisit_printme( Token token ) {
        // System.out.println( token.toString() );
    // }

    // TODO: Check if this fucks something up...
    private void addFinish( NFA_state begin, NFA_state end ) {
        end.isFinal = true;
        if ( !finalStates.containsKey( begin.name ) ) 
            finalStates.put( begin.name, Utils.toSet( end ) );
        else 
            finalStates.get( begin.name ).add( end );

    }

    // TODO: Check if this fucks something up...
    private void clearFinish( NFA_state state ) {
        if ( finalStates.containsKey( state.name ) )  {
            for ( NFA_state finals : finalStates.get( state.name ) )
                finals.isFinal = false;
        
            finalStates.put( state.name, Utils.toSet() );
        }
    }

    private Set<NFA_state> getFinish( NFA_state state ) {
        return finalStates.getOrDefault( state.name, Utils.toSet() );
    }

    public void visit_leaf( Token token ) {

        if ( token instanceof TokenChar ) {
            NFA_state state = null;
            NFA_state end = null;

            if ( ((TokenChar)token).kind == TokenCharKind.CHAR ) {
                // System.out.println( "tokenChar" );
                state = new NFA_state();
                end = state.registerWord( ((TokenChar)token).value + "", true );

                // addFinish( charState, end );
            } else if  ( ((TokenChar)token).kind == TokenCharKind.WILD ) {
                // System.out.println( "\t\tTokenRegWild" );
                state = new NFA_state();
                end = new NFA_state();
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
        } else if ( token instanceof TokenRegRepetition ) { // Needs expansion when more repetitions are added
            // System.out.println( "tokenRep" );
            TokenRegRepetition _token = (TokenRegRepetition) token;

            NFA_state start = new NFA_state();

            NFA_state state = nfaStack.pop();

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
            for ( NFA_state finish : getFinish( state ) ) {
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


        } else if ( token instanceof TokenRegUdtryk ) {
            
            // System.out.println( "tokenUdtryk" );


        } 
        // System.out.println( nfaStack.size() );
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
                NFA_state right = nfaStack.pop();
                NFA_state left = nfaStack.pop();

                NFA_state concat = new NFA_state();

                concat.addEdge(left);
                for ( NFA_state leftFinish : getFinish( left ) ) {
                    // System.out.println( "finish.....");
                    leftFinish.addEdge(right);
                    leftFinish.isFinal = false;
                }

                clearFinish( left );
                for ( NFA_state newFinish : getFinish( right ) ) {
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
                NFA_state right = nfaStack.pop();
                NFA_state left = nfaStack.pop();

                NFA_state union = new NFA_state();
                union.addEdge( left );
                union.addEdge( right );
                nfaStack.push( union );

                for ( NFA_state newFinish : getFinish( left ) ) {
                    addFinish( union, newFinish );
                }

                for ( NFA_state newFinish : getFinish( right ) ) {
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
                // NFA_state charState = nfaStack.pop(); 

            }
        }
    }

    public void postVisit_range( Token token ) {
        if ( token instanceof TokenRegRange ) {
            // System.out.println( "TokenRegRange" );
            TokenRegRange _token = (TokenRegRange) token;
            if ( _token.right == null ) {
                if ( _token.kind == TokenRangeKind.INT ) {
                    // System.out.println( "*****************************************************************'" );
                    // System.out.println( "In tokenRangeInt" );
                    
                    int reps = ((TokenRegDigit)_token.left).value;
                    // System.out.println( "REPS: " + reps );
                    
                    NFA_state anchor = null;

                    NFA_state stateToRepeat = nfaStack.pop();
                    
                    
                    NFA_state start = new NFA_state( "RangeStart" );
                    nfaStack.push( start );

                    for ( int i = 0; i < reps; ++i ) {
                        // System.out.println( "Rep: " + i );
                        
                        NFA_state current = stateToRepeat.copy();
                        


                        start.addEdge( current );

                        anchor = new NFA_state( "Anchor" );
                        

                        for ( NFA_state finish : NFA_state.collectFinals( current ) ) {
                            // System.out.println( "akjshdaiuhdasd" );
                            finish.addEdge( anchor );
                            finish.isFinal = false;
                        }

                        // clearFinish( current );
                        
                        // addFinish( newStart, anchor );
                        // System.out.println( NFA_state.getStringRepresentation( start ) );


                        start = anchor;
                     
                    }
                    addFinish( nfaStack.peek(), anchor );
                    // System.out.println( "-------------------------");
                    // System.out.println( NFA_state.getStringRepresentation( nfaStack.peek() ) );
                    // System.out.println( "-------------------------");
                    // System.out.println( "-------------------------");


                    // Single value;
                    // state = new NFA_state();
                    // end = state.registerWord( ((TokenChar)token).value + "", true );

                    
                    // addFinish( start, end );

                    // System.out.println( NFA_state.getStringRepresentation( start ));


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
            NFA_state tok = nfaStack.pop();
            // System.out.println( "***************" );
            // System.out.println( NFA_state.getStringRepresentation(tok) );
            // System.out.println( "***************" );
            // result = NFA_state.toDFA( tok );
            result = tok;
            // System.out.println( "asd " + result.name );


        } 
    }

}
