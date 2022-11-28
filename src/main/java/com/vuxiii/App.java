package com.vuxiii;

import java.util.List;

import com.vuxiii.Regex.Regex;
import com.vuxiii.Regex.Token.Token;
import com.vuxiii.Regex.Token.TokenIdentifier;

public class App {
    public static void main( String[] args ) {
        Regex<Token> regex;
        // regex = new Regex( "(a|bc)*d" );
        // regex.compile();



        // regex.match( "Hej me ig :)daad" );
     
        regex = new Regex<>( ".*", (id) -> new TokenIdentifier( id ) );
        // regex = new Regex( "H.{2}y" );
        // regex = new Regex( "." );
        regex.compile();

        List<Token> tokens = regex.match( "int i = name;" );

        for ( Token t : tokens ) {
            System.out.println( t.toString() );
        }

    }
}
