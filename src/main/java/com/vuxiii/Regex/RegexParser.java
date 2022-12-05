package com.vuxiii.Regex;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import com.vuxiii.DFANFA.DFA;
import com.vuxiii.DFANFA.NFA;
import com.vuxiii.LR.Grammar;
import com.vuxiii.LR.LRParser;
import com.vuxiii.LR.ParseTable;
import com.vuxiii.LR.ParsingStep;
import com.vuxiii.LR.Records.LRRule;
import com.vuxiii.LR.Records.NonTerminal;
import com.vuxiii.LR.Records.ParserState;
import com.vuxiii.LR.Records.Terminal;
import com.vuxiii.Regex.Token.*;
import com.vuxiii.Utils.Utils;
import com.vuxiii.Visitor.LeafVisitor;
import com.vuxiii.Visitor.RegexConstructorVisitor;

public class RegexParser {
    private static boolean firstSetup = true;
    private static Grammar g;
    private static ParseTable table;

    private static NonTerminal nS;
    private static NonTerminal nExp;
    private static NonTerminal nConcat;
    private static NonTerminal nUnion;
    private static NonTerminal nRepetition;
    private static NonTerminal nOneMore;
    private static NonTerminal nZeroOne;
    private static NonTerminal nStar;
    private static NonTerminal nRange;
    private static NonTerminal nIntRange;
    private static NonTerminal nCharRange;
    private static NonTerminal nIntNumber;
    private static NonTerminal nUdtryk;
    private static NonTerminal nSymbol;

    private static Terminal tDollar;
    private static Terminal tPlus;
    private static Terminal tStar;
    private static Terminal tUnion;
    private static Terminal tEpsilon;
    private static Terminal tLCurl;
    private static Terminal tNumber;
    private static Terminal tRCurl;
    private static Terminal tComma;
    private static Terminal tLBracket;
    private static Terminal tRBracket;
    private static Terminal tDash;
    private static Terminal tAlpha;
    private static Terminal tDigit;
    private static Terminal tChar;
    // private static Terminal tWild;
    private static Terminal tConcat;
    private static Terminal tLParen;
    private static Terminal tRParen;

    // private static DFA_state regexDFA;

    public static<T> NFA<T> compileRegex( String regex, Function<String, T> constructor ) { 
        if ( firstSetup )
            setup();
        firstSetup = false;

        // Convert the input into tokens...
        LinkedList<Token> tokens = convertInputToTokens( regex );


        // Parse the AST.

        Token AST = getAST( tokens ); // parse step

        // Parse AST.

        RegexConstructorVisitor<T> reg = new RegexConstructorVisitor<>();
        AST.accept( reg );

        // System.out.println( DFA_state.getStringRepresentation( reg.result ) );

        NFA<T> nfa = reg.result;
        NFA.collectFinals(nfa).forEach( n -> n.constructor = constructor );;
        // nfa.constructor = constructor;
        // Return the result.
        return nfa;
        // return NFA_state.toDFA(nfa);
        // return null;
    }

    private static Token getAST( List<Token> tokens ) {
        List<ParserState> stack = new LinkedList<>();
        stack.add( table.getStartState() );
        ParsingStep currentStep = new ParsingStep(tokens, new LinkedList<>(), stack, new LinkedList<>(), table );

        while ( !currentStep.isFinished ) {
            // Utils.log( currentStep );
            currentStep = currentStep.step();
        }
        // System.out.println( currentStep );
        Token AST = currentStep.getResult();

        System.out.println( "Final result is: " + AST );
        return AST;
    }

    private static LinkedList<Token> convertInputToTokens( String regex ) {

        LinkedList<Token> tokens = new LinkedList<>();

        TokenLParen lparen = new TokenLParen( tLParen );
        TokenRParen rparen = new TokenRParen( tRParen );
        TokenLCurl lcurl = new TokenLCurl( tLCurl );
        TokenRCurl rcurl = new TokenRCurl( tRCurl );
        TokenRegOperator union = new TokenRegOperator( tUnion );
        TokenRegStar star = new TokenRegStar( tStar );
        TokenRegDash dash = new TokenRegDash( tDash );
        // TokenRegDigit digit = new TokenRegDigit( tDigit );
        // TokenRegAlpha alpha = new TokenRegAlpha( tAlpha );
        TokenRegOperator concat = new TokenRegOperator( tConcat );
        TokenEOP tOEP = new TokenEOP( tDollar );
        // System.out.println( regex );
        boolean addConcatToken = false;
        for ( int i = 0; i < regex.length(); ++i ) {
            char c = regex.charAt( i );
            // System.out.println( "at char: " + c);
            switch (c) {
                case '(':{
                    tokens.add( lparen );
                    addConcatToken = false;
                } break;
                case ')':{
                    tokens.add( rparen );
                    addConcatToken = true;
                } break;
                case '{':{
                    tokens.add( lcurl );
                    addConcatToken = false;
                } break;
                case '}':{
                    tokens.add( rcurl );
                    addConcatToken = true;
                } break;
                case '|':{
                    tokens.add( union );
                    addConcatToken = false;
                } break;
                case '*':{
                    tokens.add( star );
                    addConcatToken = true;
                } break;
                case '-':{
                    // if ( addConcatToken ) {
                    //     tokens.add( concat );
                    //     addConcatToken = false;
                    // }
                    tokens.add( dash );
                    addConcatToken = false;
                } break;
                case '[':{ // Check for fancy stuff like [:digit:], [:alpha:]
                    if ( addConcatToken ) {
                        tokens.add( concat );
                        addConcatToken = false;
                    }
                    if ( regex.substring(i).startsWith("[:digit:]") ) {
                        tokens.add( new TokenChar( tChar, TokenCharKind.DIGIT ) );
                        i += "[:digit:]".length(); // -1?
                    
                    } else if ( regex.substring(i).startsWith("[:alpha:]") ) {
                        tokens.add( new TokenChar( tChar, TokenCharKind.ALPHA ) );
                        i += "[:alpha:]".length(); // -1?
                    }
                    addConcatToken = true;
                } break;
                case '.': {
                    // System.out.println( "FOUND WILD" );
                    if ( addConcatToken ) {
                        tokens.add( concat );
                        addConcatToken = false;
                    }
                    tokens.add( new TokenChar( tChar, TokenCharKind.WILD ) );

                    addConcatToken = true;
                } break;
                case '\\':{
                    c = regex.charAt( ++i );
                }
                default: {
                    if ( addConcatToken ) {
                        tokens.add( concat );
                        addConcatToken = false;
                    }
                    // if ( Character.isDigit(c) ) { // Also check for negative numbers (maybe?)
                    //     int end = i;
                    //     char otherC = regex.charAt(end);
                    //     while ( Character.isDigit( otherC ) ) {
                    //         end++;
                    //         if ( end == regex.length() ) break;
                    //         otherC = regex.charAt(end);
                    //     }
                    //     tokens.add( new TokenRegDigit( Integer.parseInt( regex.substring( i, end ) ), tNumber ) );
                    //     i = --end;
                    // } else {
                        tokens.add( new TokenChar( c, tChar, TokenCharKind.CHAR ) );
                    // }
                    addConcatToken = true;
                    
                } break;
            }
        }
        tokens.add( tOEP );

        Utils.log( "Input tokens:" );
        Utils.log( tokens );

        return tokens;
    
    }

    private static void setup() { // define the regex grammar
        firstSetup = false;
        g = new Grammar();

        nS = new NonTerminal( "S", true );
        nExp = new NonTerminal( "Exp" );
        nConcat = new NonTerminal( "Concat" );
        nUnion = new NonTerminal( "Union" );
        nRepetition = new NonTerminal( "Repetition" );
        nOneMore = new NonTerminal( "OneMore" );
        nZeroOne = new NonTerminal( "ZeroOne" );
        nStar = new NonTerminal( "Star" );
        nRange = new NonTerminal( "Range" );
        nIntRange = new NonTerminal( "IntRange" );
        nCharRange = new NonTerminal( "CharRange" );
        nIntNumber = new NonTerminal( "IntNumber" );
        nUdtryk = new NonTerminal( "Udtryk" );
        nSymbol = new NonTerminal( "Symbol" );

        tDollar = new Terminal( "$" );
        tDollar.is_EOP = true;
        tPlus = new Terminal( "+" );
        tStar = new Terminal( "*" );
        tUnion = new Terminal( "|" );
        // Terminal tWild = new Terminal( "." );
        tEpsilon = new Terminal();
        tLCurl = new Terminal( "{" );
        tNumber = new Terminal( ":digit:" );
        tRCurl = new Terminal( "}" );
        tComma = new Terminal( "," );
        tLBracket = new Terminal( "[" );
        tRBracket = new Terminal( "]" );
        tDash = new Terminal( "-" );
        tAlpha = new Terminal( ":alpha:" ); 
        tDigit = new Terminal( ":digit:" ); 
        tChar = new Terminal( ":char:" );
        // tWild = new Terminal( ":wild:" ); //??????
        tConcat = new Terminal( "->" ); // debug.
        tLParen = new Terminal( "(" ); // debug.
        tRParen = new Terminal( ")" ); // debug.


        // LRRule rule0 = g.add_rule( nS, List.of( nExp, tDollar ) );
        
        // LRRule rule1  = g.add_rule( nExp, List.of( nUnion, nExp ) );
        // LRRule rule2  = g.add_rule( nExp, List.of( nUnion, nRepetition ) );
        // LRRule rule3  = g.add_rule( nExp, List.of( nUnion ) );
        // LRRule rule4  = g.add_rule( nConcat, List.of( nUdtryk, tConcat, nConcat ) );
        // LRRule rule5  = g.add_rule( nConcat, List.of( nUdtryk ) );
        // LRRule rule4  = g.add_rule( nConcat, List.of( nSymbol, nConcat ) );
        // LRRule rule5  = g.add_rule( nConcat, List.of( nSymbol ) );
        // LRRule rule6  = g.add_rule( nUnion, List.of( nConcat, tUnion, nUnion ) );
        // LRRule rule7  = g.add_rule( nUnion, List.of( nConcat ) );
        // LRRule rule8  = g.add_rule( nRepetition, List.of( nStar ) );
        // LRRule rule9  = g.add_rule( nRepetition, List.of( nOneMore ) );
        // LRRule rule10 = g.add_rule( nRepetition, List.of( nZeroOne ) );
        // LRRule rule11 = g.add_rule( nRepetition, List.of( tLCurl, tNumber, tRCurl ) );
        // LRRule rule12 = g.add_rule( nRepetition, List.of( tLCurl, tNumber, tComma, tNumber, tRCurl ) );
        // LRRule rule13 = g.add_rule( nOneMore, List.of( nConcat, nStar ) );
        // LRRule rule14 = g.add_rule( nZeroOne, List.of( nConcat ) );
        // LRRule rule15 = g.add_rule( nZeroOne, List.of( tEpsilon ) );
        // LRRule rule16 = g.add_rule( nStar, List.of( tStar ) );
        
        // LRRule rule17 = g.add_rule( nRange, List.of( tLBracket, nUdtryk, tDash, nUdtryk, tRBracket ) );
        // LRRule rule18 = g.add_rule( nRange, List.of( tNumber ) );
        // LRRule rule19 = g.add_rule( nRange, List.of( tAlphs ) );
        
        // LRRule rule19 = g.add_rule( nUdtryk, List.of( nSymbol, nRepetition ) );
        // LRRule rule20 = g.add_rule( nUdtryk, List.of( nSymbol ) );
        // LRRule rule211 = g.add_rule( nSymbol, List.of( tLParen, nExp, tRParen ) );
        // LRRule rule21 = g.add_rule( nSymbol, List.of( tChar ) );



        g.addRuleWithReduceFunction( nS, List.of( nExp, tDollar ), (tokens) -> {
            TokenRegExp left = (TokenRegExp) tokens.get(0);
            TokenEOP right = (TokenEOP) tokens.get(1);
            return new TokenRoot( left, right, nS );
        } );

        g.addRuleWithReduceFunction( nExp, List.of( nUnion ), (tokens) -> {
            TokenRegUnion token = (TokenRegUnion) tokens.get(0);
            return new TokenRegExp( token, nExp );
        } );

        g.addRuleWithReduceFunction( nUnion, List.of( nConcat, tUnion, nUnion ), (tokens) -> {
            TokenRegConcat left = (TokenRegConcat) tokens.get(0);
            TokenRegUnion right = (TokenRegUnion) tokens.get(2);
            TokenOperator operator = (TokenOperator) tokens.get(1);
            return new TokenRegUnion( left, right, operator, nUnion );
        } );

        g.addRuleWithReduceFunction( nUnion, List.of( nConcat ), (tokens) -> {
            TokenRegConcat token = (TokenRegConcat) tokens.get(0);
            return new TokenRegUnion( token, nUnion );
        } );

        g.addRuleWithReduceFunction( nConcat, List.of( nUdtryk, tConcat, nConcat ), (tokens) -> {
            TokenRegUdtryk left = (TokenRegUdtryk) tokens.get(0);
            TokenRegConcat right = (TokenRegConcat) tokens.get(2);
            TokenOperator operator = (TokenOperator) tokens.get(1);
            return new TokenRegConcat( left, right, operator, nConcat );
        } );

        g.addRuleWithReduceFunction( nConcat, List.of( nUdtryk ), (tokens) -> {
            TokenRegUdtryk token = (TokenRegUdtryk) tokens.get(0);
            return new TokenRegConcat( token, nConcat );
        } );

        g.addRuleWithReduceFunction( nUdtryk, List.of( nSymbol, nRepetition ), (tokens) -> {
            TokenRegSymbol left = (TokenRegSymbol) tokens.get(0);
            TokenRegRepetition right = (TokenRegRepetition) tokens.get(1);
            return new TokenRegUdtryk( left, right, nUdtryk );
        } );

        g.addRuleWithReduceFunction( nUdtryk, List.of( nSymbol ), (tokens) -> {
            TokenRegSymbol token = (TokenRegSymbol) tokens.get(0);
            return new TokenRegUdtryk( token, nUdtryk );
        } );

        g.addRuleWithReduceFunction( nRepetition, List.of( nStar ), (tokens) -> {
            TokenRegStar token = (TokenRegStar) tokens.get(0);
            return new TokenRegRepetition( token, nRepetition );
        } );

        // TEST
        g.addRuleWithReduceFunction( nRepetition, List.of( tLCurl, nRange, tRCurl ), (tokens) -> {
            // System.out.println( "In reduce with { RANGE }" );
            TokenLCurl lcurl = (TokenLCurl) tokens.get(0);
            TokenRegRange token = (TokenRegRange) tokens.get(1);
            TokenRCurl rcurl = (TokenRCurl) tokens.get(2);
            return new TokenRegRepetition( lcurl, token, rcurl, nRepetition );
        } );

        g.addRuleWithReduceFunction( nRange, List.of( nIntRange ), (tokens) -> {
            TokenRegIntRange digits = (TokenRegIntRange) tokens.get(0);

            return new TokenRegRange( digits, TokenRangeKind.INT, nRange );
        } );

        g.addRuleWithReduceFunction( nRange, List.of( nCharRange ), (tokens) -> {
            TokenRegCharRange digits = (TokenRegCharRange) tokens.get(0);
            
            return new TokenRegRange( digits, TokenRangeKind.CHAR, nRange );
        } );

        g.addRuleWithReduceFunction( nIntRange, List.of( nIntNumber ), (tokens) -> {
            TokenRegIntNumber left = (TokenRegIntNumber) tokens.get(0);
            
            return new TokenRegIntRange(left, nIntRange);
        } );

        g.addRuleWithReduceFunction( nIntRange, List.of( nIntNumber, tDash, nIntNumber ), (tokens) -> {
            TokenRegIntNumber left = (TokenRegIntNumber) tokens.get(0);
            TokenRegIntNumber right = (TokenRegIntNumber) tokens.get(2);
            
            return new TokenRegIntRange(left, right, nIntRange);
        } );

        g.addRuleWithReduceFunction( nIntNumber, List.of( tChar ), (tokens) -> {
            TokenChar digit1 = (TokenChar) tokens.get(0);
            if ( !Character.isDigit( digit1.value ) ) {
                System.out.println( "Parsing error. Expected number, got " + digit1.value );
                System.exit(-1);
            }
            // TokenRegDash dash = (TokenRegDash) tokens.get(1);
            // TokenChar digit2 = (TokenChar) tokens.get(2);
            return new TokenRegIntNumber( Integer.valueOf( digit1.value + "" ), nIntNumber );
        } );

        g.addRuleWithReduceFunction( nIntNumber, List.of( tChar, tConcat, nIntNumber ), (tokens) -> {
            TokenChar digit1 = (TokenChar) tokens.get(0);
            if ( !Character.isDigit( digit1.value ) ) {
                System.out.println( "Parsing error. Expected number, got " + digit1.value );
                System.exit(-1);
            }
            TokenRegIntNumber digit2 = (TokenRegIntNumber) tokens.get(2);

            char leftValue = digit1.value;
            int rightValue = digit2.value;
            int value = Integer.valueOf( leftValue + ("" + rightValue) );

            return new TokenRegIntNumber( value, nIntNumber );
        } );

        g.addRuleWithReduceFunction( nCharRange, List.of( tChar ), (tokens) -> {
            TokenChar token = (TokenChar) tokens.get(0);
            if ( !Character.isLetter( token.value ) ) {
                System.out.println( "Parsing error. Expected letter, got " + token.value );
                System.exit(-1);
            }
            return new TokenRegCharRange( token, nCharRange );
        } );

        g.addRuleWithReduceFunction( nCharRange, List.of( tChar, tDash, tChar ), (tokens) -> {
            TokenChar left = (TokenChar) tokens.get(0);
            
            TokenChar right = (TokenChar) tokens.get(2);
            if ( !Character.isLetter( left.value ) ) {
                System.out.println( "Parsing error. Expected letter, got " + left.value );
                System.exit(-1);
            }
            if ( !Character.isLetter( right.value ) ) {
                System.out.println( "Parsing error. Expected letter, got " + right.value );
                System.exit(-1);
            }

            return new TokenRegCharRange( left, right, nCharRange );
        } );

        // TEST

        g.addRuleWithReduceFunction( nStar, List.of( tStar ), (tokens) -> {
            TokenRegStar token = (TokenRegStar) tokens.get(0);
            return new TokenRegRepetition( token, nRepetition );
        } );

        g.addRuleWithReduceFunction( nSymbol, List.of( tLParen, nExp, tRParen ), (tokens) -> {
            TokenLParen left = (TokenLParen) tokens.get(0);
            TokenRegExp exp = (TokenRegExp) tokens.get(1);
            TokenRParen right = (TokenRParen) tokens.get(2);
            return new TokenRegSymbol( left, exp, right, nSymbol );
        } );

        g.addRuleWithReduceFunction( nSymbol, List.of( tChar ), (tokens) -> {
        
            TokenChar token = (TokenChar) tokens.get(0);
            return new TokenRegSymbol( token, nSymbol );
            
        } );

        g.addRuleWithReduceFunction( nSymbol, List.of( tNumber ), (tokens) -> {
        
            TokenRegIntNumber token = (TokenRegIntNumber) tokens.get(0);
            return new TokenRegSymbol( token, nSymbol );
            
        } );

        // g.addRuleWithReduceFunction( nSymbol, List.of( tWild ), (tokens) -> {
        //     TokenRegWild token = (TokenRegWild) tokens.get(0);
        //     return new TokenRegSymbol( token, nSymbol );
        // } );
        
        table = LRParser.parse( g, nS );

    }
}