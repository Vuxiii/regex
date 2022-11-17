package src.Regex;

import java.util.List;

import src.LR.Grammar;
import src.DFA.DFA_state;
import src.NFA.NFA_state;

public class Regex {
    private DFA_state dfa;

    private NFA_state nfa = new NFA_state( "entry", false );

    private static Grammar regexParser = null;

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
        // nfa.addEdge( RegexParser.compileRegex( regex ) ); // add edge to DFA!!!!
    }



    public void match( String input ) {

    }

    public void compile() { // Convert the internal NFA to a DFA


        
    }
}
