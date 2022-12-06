package com.vuxiii;

import java.util.List;

import com.vuxiii.DFANFA.DFA;
import com.vuxiii.LR.ParsingStep;
import com.vuxiii.Regex.Regex;
import com.vuxiii.Regex.Token.Token;
import com.vuxiii.Regex.Token.TokenAlphs;

public class App {
    public static void main( String[] args ) {
        Regex<Token> regex = new Regex<>();
        // regex = new Regex( "(a|bc)*d" );
        // regex.compile();


        ParsingStep.showSteps = false;
        // regex.match( "Hej me ig :)daad" );
     
        // regex = new Regex<>( ".*", (id) -> new TokenAlphs( id ) );
        // regex.addRegex( "01{1-4}5", (id) -> new TokenAlphs( id ) );
        // regex.addRegex( "[0-5]", (id) -> new TokenAlphs( id ) );
        // regex.addRegex( "t[0-5]", (id) -> new TokenAlphs( id ) );
        // regex.addRegex( "t[0-5]b", (id) -> new TokenAlphs( id ) );
        regex.addRegex( "t[A-F]b", (id) -> new TokenAlphs( id ) );
        // regex.addRegex( "a(1|2|3)b", (id) -> new TokenAlphs( id ) );
        // regex.addRegex( "a|b", (id) -> new TokenAlphs( id ) );
        // regex.addRegex( "0[:digit:]", (id) -> new TokenAlphs( id ) );
        // regex.addRegex( ".*", (id) -> new TokenIdentifier( id ) );
        // regex.addRegex( "(hej){3}", (id) -> new TokenIdentifier( id ) );
        // regex = new Regex( "H.{2,5}y" );
        // regex = new Regex( "." );
        regex.compile();

        System.out.println(DFA.<Token>getStringRepresentation( regex.dfa ));

        System.out.println("asd");
        List<Token> tokens = regex.match( "int i = 2;" );

        for ( Token t : tokens ) {
            System.out.println( t.toString() );
        }

    }
}
