package com.vuxiii;

import java.util.List;

import com.vuxiii.DFANFA.DFA;
import com.vuxiii.DFANFA.Edge;
import com.vuxiii.DFANFA.EdgeKind;
import com.vuxiii.DFANFA.NFA;
import com.vuxiii.LR.ParsingStep;
import com.vuxiii.LR.Settings;
import com.vuxiii.Regex.Regex;
import com.vuxiii.LR.Records.ASTToken;


public class App {
    public static void main( String[] args ) {
        
        regexSample();

    }

    public static void regexSample() {
        Regex<ASTToken> regex = new Regex<>();
        // regex = new Regex<>( "( |\t|\n)", (id) -> new TokenAlphs(id) );
        // regex.compile();


        Settings.showParsingSteps = false;
        // regex.match( "Hej me ig :)daad" );
     
        // regex = new Regex<>( "=", (id) -> new TokenAssignment( id ) );
        // regex.addRegex( "int", (type) -> new TokenIntType( type ) );
        // regex.addRegex( "for", (intVal) -> new TokenFor( intVal ) );
        // regex.addRegex( "[:digit:][:digit:]*", (intVal) -> new TokenInt( intVal ) );
        // regex.addRegex( ";", (intVal) -> new TokenAlphs( "END " + intVal ) );
        // regex.addRegex( "[:alpha:].*", (id) -> new TokenAlphs( id ), 999999999 );
        // regex.addRegex( "( |\n)*", (rm) -> new TokenAlphs( "Ignore" ) ); 
        regex.addRegex( ' ' + ".*" + ' ', (id) -> new TokenAlphs( id ) );
        // regex.addRegex( "t[0-5]", (id) -> new TokenAlphs( id ) );
        // regex.addRegex( "t[0-5]b", (id) -> new TokenAlphs( id ) );
        // regex.addRegex( "t[A-F]b", (id) -> new TokenAlphs( id ) );
        // regex.addRegex( "a(1|2|3)b", (id) -> new TokenAlphs( id ) );
        // regex.addRegex( "a|b", (id) -> new TokenAlphs( id ) );
        // regex.addRegex( "0[:digit:]", (id) -> new TokenAlphs( id ) );
        // regex.addRegex( ".*", (id) -> new TokenIdentifier( id ) );
        // regex.addRegex( "(hej){3}", (id) -> new TokenIdentifier( id ) );
        // regex = new Regex( "H.{2,5}y" );
        // regex = new Regex( "." );
        regex.compile();

        System.out.println(DFA.getStringRepresentation( regex.dfa ));

        // System.out.println("asd");
        List<ASTToken> tokens = regex.match( "for int i = 42;" );

        for ( ASTToken t : tokens ) {
            System.out.println( t.toString() );
        }

    }

    public static void sample() {
        NFA<ASTToken> start = new NFA<>();
        start.registerWord( "Hej" );
        start.registerWord( "Abe" );
        start.registerWord( "Juhl" );


        System.out.println( NFA.getStringRepresentation(start));


        System.out.println( "=".repeat(60) );
        
        DFA<ASTToken> dfa = NFA.toDFA( start );

        System.out.println( DFA.getStringRepresentation( dfa ) );


    }

    public static void nfafix() {
        NFA<ASTToken> start = new NFA<>();
        
        // NFA<Token> abe = new NFA<>( "abeStart" );
        
        NFA<ASTToken> other = new NFA<>();
        NFA<ASTToken> other7 = new NFA<>();
        NFA<ASTToken> other8 = new NFA<>();
        other.addEdge( EdgeKind.ANY, other7 );
        other7.addEdge( EdgeKind.ANY, other8 );


        other8.addEdge( start.registerWord("abe") );

        // start.addEdge( abe );
        start.addEdge( other );

        System.out.println( NFA.getStringRepresentation(start));

        System.out.println( "=".repeat(60) );
        
        DFA<ASTToken> dfa = NFA.toDFA( start );

        System.out.println( DFA.getStringRepresentation( dfa ) );

    }
 }
