package com.vuxiii;

import java.util.List;

import com.vuxiii.DFANFA.DFA;
import com.vuxiii.Regex.Regex;
import com.vuxiii.Regex.Token.Token;
import com.vuxiii.Regex.Token.TokenIdentifier;

public class App {
    public static void main( String[] args ) {
        Regex<Token> regex = new Regex<>();
        // regex = new Regex( "(a|bc)*d" );
        // regex.compile();



        // regex.match( "Hej me ig :)daad" );
     
        regex = new Regex<>( ".{2}be", (id) -> new TokenIdentifier( id ) );
        // regex.addRegex( "int", (id) -> new TokenIdentifier( id ) );
        // regex.addRegex( ".*", (id) -> new TokenIdentifier( id ) );
        // regex.addRegex( "(hej){3}", (id) -> new TokenIdentifier( id ) );
        // regex = new Regex( "H.{2}y" );
        // regex = new Regex( "." );
        regex.compile();

        System.out.println(DFA.<Token>getStringRepresentation( regex.dfa ));

        System.out.println("asd");
        List<Token> tokens = regex.match( "inbet i = indent;" );

        for ( Token t : tokens ) {
            System.out.println( t.toString() );
        }

    }
}
