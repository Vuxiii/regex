package com.vuxiii;

import java.util.List;

import com.vuxiii.DFA.DFA_state;
import com.vuxiii.Regex.Regex;
import com.vuxiii.Regex.Token.Token;
import com.vuxiii.Regex.Token.TokenIdentifier;

public class App {
    public static void main( String[] args ) {
        Regex<Token> regex = new Regex<>();
        // regex = new Regex( "(a|bc)*d" );
        // regex.compile();



        // regex.match( "Hej me ig :)daad" );
     
        regex = new Regex<>( "(a){3-5}be", (id) -> new TokenIdentifier( id ) );
        // regex.addRegex( "int", (id) -> new TokenIdentifier( id ) );
        // regex.addRegex( ".*", (id) -> new TokenIdentifier( id ) );
        // regex.addRegex( "(hej){3}", (id) -> new TokenIdentifier( id ) );
        // regex = new Regex( "H.{2}y" );
        // regex = new Regex( "." );
        regex.compile();

        System.out.println(DFA_state.<Token>getStringRepresentation( regex.dfa ));

        System.out.println("asd");
        List<Token> tokens = regex.match( "int i = indent;" );

        for ( Token t : tokens ) {
            System.out.println( t.toString() );
        }

    }
}
