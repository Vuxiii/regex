package src.Regex;

import java.util.List;
import java.util.Scanner;

import src.LR.Grammar;
import src.DFA.DFA_state;
import src.NFA.NFA_state;

public class Regex {
    public DFA_state dfa;

    private NFA_state nfa = new NFA_state( "entry", false );

    // private static Grammar regexParser = null;

    public Regex() {
        dfa = NFA_state.toDFA( nfa );
    }

    public Regex( String regex ) { 
        addRegex( regex );
    }

    public Regex( List<String> regexes ) {
        for ( String regex : regexes )
            addRegex( regex );
    }



    public void addRegex( String regex ) { // Convert the regex to NFA and add it to the internal NFA
        nfa.addEdge( RegexParser.compileRegex( regex ) ); // add edge to DFA!!!!
    }


    /**
     * Precondition: The user has called the compile method before calling this method.
     * @param input The input to match.
     */
    public void match( String input ) {
        System.out.println( "\n".repeat(3) );
        System.out.println( DFA_state.getStringRepresentation(dfa) );
        Scanner in = new Scanner( input );
        DFA_state current = dfa;
        boolean foundMatch = false;
        while( in.hasNextLine() ) {
            String line = in.nextLine();

            int start = 0;
            int end = 0;
            while( start < line.length() ) {
                int i = start;
                char c = line.charAt( i );
                // System.out.println( "At i = " + i );
                while ( current.canConsume( c ) ) {
                    // System.out.println( "We can consume " + c + " from index " + i );
                    current = current.consume( c );
                    ++i;
                    foundMatch = true;
                    if ( i == line.length() ) break;
                    c = line.charAt( i );
                }
                end = i;
                if ( foundMatch ) {
                    if ( current.isFinal ) 
                        System.out.println( "Match -> " + line.substring(start, end) );
                    else
                        System.out.println( "Match not finished -> " + line.substring(start, end) );
                    current = dfa;
                    start = end;
                } else {
                    start = end + 1;
                }
                foundMatch = false;
            }

        }

        in.close();

    }

    public void compile() { // Convert the internal NFA to a DFA
        dfa = NFA_state.toDFA( nfa );
    }
}
