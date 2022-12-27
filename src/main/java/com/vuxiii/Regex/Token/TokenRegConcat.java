package com.vuxiii.Regex.Token;

import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;
import com.vuxiii.LR.Records.ASTToken;
public class TokenRegConcat implements ASTToken {

    public final Term nonTerminal;
    public final TokenRegUdtryk left;
    public final TokenRegConcat right;
    public final TokenOperator operator;

    // public TokenRegConcat( TokenRegSymbol left, TokenRegConcat right, Term term )  {
    //     this.left = left;
    //     this.right = right;
    //     // this.operator = operator;
    //     this.term = term;
    // }

    public TokenRegConcat( TokenRegUdtryk left, TokenRegConcat right, TokenOperator operator, Term nonTerminal )  {
        this.left = left;
        this.right = right;
        this.operator = operator;
        this.nonTerminal = nonTerminal;
    }

    public TokenRegConcat( TokenRegUdtryk token, Term nonTerminal )  {
        this.left = token;
        this.right = null;
        this.operator = null;
        this.nonTerminal = nonTerminal;
    }


    @Override
    public Term getTerm() {
        return nonTerminal;
    }
    
    public String toString() {
        if ( right == null )
            return "[Con -> " + left.toString() + "]";
        return "[Con -> " + left.toString() + " [" + nonTerminal.toString() + "] " + right.toString() + "]";
    }

    @Override
    public void accept( VisitorBase visitor ) {
        visitor.preVisit( this );
        left.accept( visitor );
        visitor.midVisit( this );
        if ( right != null )
            right.accept( visitor );  
        visitor.postVisit( this );
    }

}