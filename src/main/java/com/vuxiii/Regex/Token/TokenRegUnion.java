package com.vuxiii.Regex.Token;

import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;
public class TokenRegUnion implements Token {

    public final Term nonTerminal;
    public final TokenRegConcat left;
    public final TokenRegUnion right;
    public final TokenOperator operator;

    public TokenRegUnion( TokenRegConcat left, TokenRegUnion right, TokenOperator operator, Term nonTerminal )  {
        this.left = left;
        this.right = right;
        this.operator = operator;
        this.nonTerminal = nonTerminal;
    }

    public TokenRegUnion( TokenRegConcat token, Term nonTerminal )  {
        this.left = token;
        this.right = null;
        this.operator = null;
        this.nonTerminal = nonTerminal;
    }


    @Override
    public Term getTerm() {
        return nonTerminal;
    }

    @Override
    public String toString() {
        if ( right == null )
            return "[Uni -> " + left.toString() + "]";
        return "[Uni -> " + left.toString() + " [" + nonTerminal.toString() + "] " + right.toString() + "]";
    }

    @Override
    public void accept( VisitorBase visitor ) {
        visitor.preVisit( this );
        left.accept( visitor );
        visitor.midVisit( this );
        if ( operator != null )
            operator.accept( visitor );
        if ( right != null )
            right.accept( visitor );  
        visitor.postVisit( this );
    }
    
}