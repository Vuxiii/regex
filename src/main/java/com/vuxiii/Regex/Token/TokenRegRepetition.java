package com.vuxiii.Regex.Token;

import com.vuxiii.LR.records.Term;
import com.vuxiii.Visitor.*;
public class TokenRegRepetition implements Token {

    public final Term nonTerminal;
    public final RegRepetition value;

    public final TokenLCurl lcurl;
    public final TokenRCurl rcurl;
    public final TokenRegRange range;

    public TokenRegRepetition( RegRepetition value, Term nonTerminal ) {
        this.value = value;
        this.nonTerminal = nonTerminal;
        lcurl = null;
        rcurl = null;
        range = null;
    }

    public TokenRegRepetition( TokenLCurl lcurl, TokenRegRange token, TokenRCurl rcurl, Term nonTerminal ) {
        this.lcurl = lcurl;
        this.rcurl = rcurl;
        range = token;
        this.nonTerminal = nonTerminal;
        value = null;
    }


    @Override
    public Term getTerm() {
        return nonTerminal;
    }

    public String toString() {
        if ( value != null )
            return value.toString();
        else 
            return "{" + range.toString() + "}";
    }


    @Override
    public void accept(VisitorBase visitor) {
        // TODO Auto-generated method stub
        visitor.preVisit( this );
        if ( lcurl != null )
            lcurl.accept( visitor );
        visitor.midVisit( this );
        if ( value != null )    
            value.accept( visitor );
        if ( range != null )
            range.accept( visitor );
        if ( rcurl != null )
            rcurl.accept( visitor );  
        visitor.postVisit( this );
    }

    // public void accept( VisitorBase visitor ) {
    //     visitor.preVisit( this );
    //     if ( lparen != null )
    //         lparen.accept( visitor );
    //     visitor.midVisit( this );
    //     value.accept( visitor );
    //     if ( rparen != null )
    //         rparen.accept( visitor );  
    //     visitor.postVisit( this );
    // }
    
    
}