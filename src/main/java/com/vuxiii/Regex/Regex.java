package com.vuxiii.Regex;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

import com.vuxiii.DFANFA.DFA;
import com.vuxiii.DFANFA.MatchInfo;
import com.vuxiii.DFANFA.NFA;
import com.vuxiii.LR.Grammar;

public class Regex<T> {
    public DFA<T> dfa;
    // public Function<String, T> constructor = null;

    private NFA<T> nfa = new NFA<>( "entry", false );

    // private static Grammar regexParser = null;

    public Regex() {
        dfa = NFA.toDFA( nfa );
    }

    public Regex( String regex, Function<MatchInfo, T> constructor ) { 
        addRegex( regex, constructor, 0 );
        // this.constructor = constructor;
    }

    public Regex( String regex, Function<MatchInfo, T> constructor, int priority ) { 
        addRegex( regex, constructor, priority );
        // this.constructor = constructor;
    }


    // public Regex( String regex ) { 
    //     addRegex( regex, null );
    // }

    // public Regex( List<String> regexes ) {
    //     for ( String regex : regexes )
    //         addRegex( regex );
    // }



    public void addRegex( String regex, Function<MatchInfo, T> constructor, int priority ) { // Convert the regex to NFA and add it to the internal NFA
        NFA<T> n = RegexParser.compileRegex( regex, constructor, priority );
        nfa.addEdge( n ); // add edge to DFA!!!!
        // System.out.println( "-".repeat(10) );
        // System.out.println( NFA_state.getStringRepresentation(n) );
        // System.out.println( "-".repeat(10) );
    }

    public void addRegex( String regex, Function<MatchInfo, T> constructor ) { // Convert the regex to NFA and add it to the internal NFA
        NFA<T> n = RegexParser.compileRegex( regex, constructor, 0 );
        nfa.addEdge( n ); // add edge to DFA!!!!
        // System.out.println( "-".repeat(10) );
        // System.out.println( NFA_state.getStringRepresentation(n) );
        // System.out.println( "-".repeat(10) );
    }


    /**
     * Precondition: The user has called the compile method before calling this method.
     * @param input The input to match.
     */
    public List<T> match( String input ) {
        List<T> output = new LinkedList<>();
        // System.out.println( "\n".repeat(3) );
        // System.out.println( DFA.getStringRepresentation(dfa) );
        Scanner in = new Scanner( input );
        DFA<T> current = dfa;
        boolean foundMatch = false;
        int lineNumber = 0;
        while( in.hasNextLine() ) {
            String line = in.nextLine();
            lineNumber++;

            int start = 0;
            int end = 0;
            while( start < line.length() ) {
                // System.out.println( "LOOP" );
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
                        // System.out.println( "Match -> " + line.substring(start, end) );
                        output.add( current.constructor.apply( new MatchInfo( line.substring(start, end), lineNumber, start ) ) );
                    }else if ( RegexSettings.showMatchFailures )
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
        System.out.println( "FINAL------------------------------------" );
        System.out.println( NFA.getStringRepresentation(nfa) );
        System.out.println( "FINAL------------------------------------" );
        
        dfa = NFA.toDFA( nfa );
        // System.out.println( "Result DFA--------------------------------------------------------");
        // System.out.println( DFA_state.getStringRepresentation(dfa) );
        // System.out.println( "Result DFA--------------------------------------------------------");
    }
}
