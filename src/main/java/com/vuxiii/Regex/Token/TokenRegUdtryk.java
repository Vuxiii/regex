package com.vuxiii.Regex.Token;

import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;
import com.vuxiii.LR.Records.ASTToken;
public class TokenRegUdtryk implements ASTToken {

    public final TokenRegRepetition rep;
    public final Term nonTerminal;
    public final ASTToken value;

    public TokenRegUdtryk( ASTToken value, Term nonTerminal ) {
        this.value = value;
        this.nonTerminal = nonTerminal;
        rep = null;
    }

    public TokenRegUdtryk( ASTToken value, TokenRegRepetition right, Term nonTerminal) {
        this.value = value;
        this.nonTerminal = nonTerminal;
        this.rep = right;
    }


    @Override
    public Term getTerm() {
        return nonTerminal;
    }
    
    public String toString() {
        if ( rep == null )
            return "[Udt -> " + value + "]"; 
        return "[Udt -> " + value + " [" + rep.nonTerminal.toString() + "] " + rep.toString() + "]"; 
    }

    @Override
    public void accept( VisitorBase visitor ) {
        visitor.preVisit( this );
        value.accept( visitor );
        visitor.midVisit( this );
        if ( rep != null )
            rep.accept( visitor );  
        visitor.postVisit( this );
    }
    
}