package src.Regex;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

import src.LR.Grammar;
import src.DFA.DFA_state;
import src.NFA.NFA_state;

public class Regex<T> {
    public DFA_state<T> dfa;
    // public Function<String, T> constructor = null;

    private NFA_state<T> nfa = new NFA_state<>( "entry", false );

    // private static Grammar regexParser = null;

    public Regex() {
        dfa = NFA_state.toDFA( nfa );
    }

    public Regex( String regex, Function<String, T> constructor ) { 
        addRegex( regex, constructor );
        // this.constructor = constructor;
    }


    // public Regex( String regex ) { 
    //     addRegex( regex, null );
    // }

    // public Regex( List<String> regexes ) {
    //     for ( String regex : regexes )
    //         addRegex( regex );
    // }



    public void addRegex( String regex, Function<String, T> constructor ) { // Convert the regex to NFA and add it to the internal NFA
        nfa.addEdge( RegexParser.compileRegex( regex, constructor ) ); // add edge to DFA!!!!
    }


    /**
     * Precondition: The user has called the compile method before calling this method.
     * @param input The input to match.
     */
    public List<T> match( String input ) {
        List<T> output = new LinkedList<>();
        System.out.println( "\n".repeat(3) );
        System.out.println( DFA_state.getStringRepresentation(dfa) );
        Scanner in = new Scanner( input );
        DFA_state<T> current = dfa;
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
                    if ( current.isFinal ) {
                        System.out.println( "Match -> " + line.substring(start, end) );
                        output.add( current.constructor.apply( line.substring(start, end) ) ); // TODO: CHANGE ME
                    }else
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
        return output;
    }

    public void compile() { // Convert the internal NFA to a DFA
        dfa = NFA_state.toDFA( nfa );
    }
}
