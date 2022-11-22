package src.LR;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import src.Regex.Token.Token;
import src.Utils.TablePrinter;
import src.Utils.Utils;

public class ParsingStep {
    
    public static int count = 0;
    private int id;

    private final LinkedList<Token> input;

    private final LinkedList<Token> tempInput;

    private final LinkedList<ParserState> stack;

    private final LinkedList<LRRule> reduceStack;

    private final ParseTable table;

    private Token output = null;

    public boolean isFinished = false;

    /**
     * Constructs a single step in the Parsingsteps
     * @param input The list of input tokens to parse
     * @param tempInput The tokens already evaluated
     * @param stack The list of actions taken
     * @param reduceStack The list of reduce that have been used
     * @param table The ParsingTable for the given Grammar
     */
    public ParsingStep( List<Token> input, List<Token> tempInput, List<ParserState> stack, LinkedList<LRRule> reduceStack, ParseTable table ) {
        this.stack = new LinkedList<>();
        this.input = new LinkedList<>();
        this.reduceStack = new LinkedList<>();
        this.tempInput = new LinkedList<>();

        this.table = table;

        this.stack.addAll( stack );
        this.input.addAll( input );
        this.reduceStack.addAll( reduceStack );
        this.tempInput.addAll( tempInput );

        id = count++;
    }

    /**
     * When parsing is done, this method returns the result
     * @return The Token computed from the parsingsteps
     */
    public Token getResult() {
        return inputPeek();
    }

    /**
     * This method takes a step in parsing
     * @return The next ParsingStep
     */
    public ParsingStep step() {
        // for ( ParserState state : table.states ) {
        //     System.out.println( "State: " + state.current_state.id );
        //     System.out.println( state.accepter.keySet() );
        // }
        ParserState currentState = stackPeek();

        Token element = inputPeek();

        // Utils.log( "We are currently in state: " + currentState.current_state.id );
        // Utils.log( "We are currently in state: " + table.states.get( currentState.current_state.id ).current_state.id );
        // System.out.println( currentState.equals(table.states.get( currentState.current_state.id )));
        // System.out.println( currentState == table.states.get( currentState.current_state.id ));

        // System.out.println( currentState );
        // System.out.println( table.states.get( currentState.current_state.id ));

        

        // Utils.log( element + " -> term: " + element.getTerm() );

        ParseAction action = table.getAction( currentState.current_state.id, element.getTerm() );
        // Utils.log( action );

        if ( action instanceof ParseShift ) {
            // Utils.log( "In Shift" );
            ParseShift act = (ParseShift) action;
            // Utils.log( "id: " + act.id() );
            ParsingStep nextStep = new ParsingStep( input, tempInput, stack, reduceStack, table );
            
            // System.out.println( currentState.current_state );

            // nextStep.stack.add( table.states.get( currentState.current_state.id ).eat( element ) );
            nextStep.stack.add( currentState.eat( element ) );
            nextStep.tempInput.add( nextStep.inputPop() );

            return nextStep;

        } else if ( action instanceof ParseGoto ) {
            // Utils.log( "In Goto" );

            ParseGoto act = (ParseGoto) action;

            ParsingStep nextStep = new ParsingStep( input, tempInput, stack, reduceStack, table );

            // nextStep.stack.add( table.states.get( act.id ) ); // currentState.eat( element )?
            nextStep.stack.add( currentState.eat( element ) );
            nextStep.tempInput.add( nextStep.inputPop() ); // ?????

            return nextStep;


        } else if ( action instanceof ParseReduce ) {
            // Utils.log( "In Reduce" );
            
            ParseReduce act = (ParseReduce) action;

            ParsingStep nextStep = new ParsingStep( input, tempInput, stack, reduceStack, table );
            
            int size = act.rule.size();
            // Utils.log( "Rule size is: " + size );

            LinkedList<Token> tokenParams = new LinkedList<>();
            for ( int i = 0; i < size; ++i ) {
                nextStep.stackPop();
                tokenParams.addFirst( nextStep.tempInput.removeLast() );
                // tokenParams.addFirst( nextStep.tempInputPop() );
            }
            nextStep.reduceStack.add( act.rule );
            nextStep.output = act.reduce( tokenParams );
            nextStep.input.addFirst( nextStep.output );

            return nextStep;


        } else if ( action instanceof ParseAccept ) {
            // Utils.log( "In Accept" );
            
            ParseAccept act = (ParseAccept) action;

            ParsingStep nextStep = new ParsingStep( input, tempInput, stack, reduceStack, table );
            
            nextStep.tempInput.add( nextStep.inputPop() );

            int size = act.rule.size();
            // Utils.log( "Rule size is: " + size );
            // System.out.println( nextStep.tempInput.size() );
            // System.out.println( nextStep.tempInput );
            LinkedList<Token> tokenParams = new LinkedList<>();
            for ( int i = 0; i < size; ++i ) {
                nextStep.stackPop();
                tokenParams.addFirst( nextStep.tempInput.removeLast() );
                // tokenParams.addFirst( nextStep.tempInputPop() );
            }
            nextStep.reduceStack.add( act.rule );
            nextStep.output = act.reduce( tokenParams );
            nextStep.input.addFirst( nextStep.output );


            nextStep.isFinished = true; 

            return nextStep; // Output is in "nextStep.output"


        } else if ( action instanceof ParseError ) {
            Utils.log( "We encountered a parse error" );
            Utils.log( action.toString() );

            System.exit(-1);
        } else {
            Utils.log( "Wtf just happend....." );
            System.exit(-1);
        }

        // Utils.log( action.toString() );

        return null;
    }

    private Token tempInputPop() {
        return tempInput.removeFirst();
    }

    private Token tempInputPeek() {
        return tempInput.peekFirst();
    }

    private Token inputPop() {
        return input.removeFirst();
    }

    private Token inputPeek() {
        return input.peekFirst();
    }

    private ParserState stackPop() {
        return stack.removeLast();
    }

    private ParserState stackPeek() {
        return table.states.get( stack.peekLast().current_state.id );
    }

    public String toString() {

        TablePrinter tp = new TablePrinter();


        tp.addTitle( "ParsingStep: " + id );

        String[] row = new String[] { "Input", "tempInput", "stack", "reduceStack" };
        tp.push( row );

        row = new String[4];

        row[0] = input.toString();
        row[1] = tempInput.toString();
        row[2] = "[";
        for ( ParserState state : stack ) 
            row[2] += state.current_state.id + ", ";
        
        if ( row[2].length() > 1 )
            row[2] = row[2].substring(0, row[2].length() - 2 );
        row[2] += "]";

        if ( reduceStack.size() == 0 )
            row[3] = "[]";
        else {
            row[3] = "[";
            for ( LRRule rule : reduceStack ) 
                row[3] += rule.id + ", ";
            row[3] = row[3].substring(0, row[3].length() - 2 ) + "]";
        }
        tp.push( row );

        return tp.compute();
    }
}
