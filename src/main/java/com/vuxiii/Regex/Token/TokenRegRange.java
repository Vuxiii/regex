package com.vuxiii.Regex.Token;

import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;
import com.vuxiii.LR.Records.ASTToken;
public class TokenRegRange implements ASTToken, RegRepetition, TokenOperator {

    public final Term nonTerminal;

    public final ASTToken range;

    public final TokenRangeKind kind;

    public TokenRegRange( ASTToken range, TokenRangeKind kind, Term nonTerminal ) {
        this.range = range;
        this.nonTerminal = nonTerminal;
        this.kind = kind;
    }

    // public TokenRegRange( Token left, Token right, TokenRangeKind kind, Term nonTerminal ) {
    //     this.left = left;
    //     this.right = right;
    //     this.nonTerminal = nonTerminal;
    //     this.kind = kind;
    // }


    @Override
    public Term getTerm() {
        return nonTerminal;
    }
 
    public String toString() {
        // if ( right == null )
        //     return "[Range ->" + left.toString() + "]";
        return "[Range -> " + range.toString() + "]";
    }


    @Override
    public void accept(VisitorBase visitor) {
        visitor.preVisit( this );
        range.accept( visitor );
        visitor.midVisit( this );
        // if ( right != null )
        //     right.accept( visitor );  
        visitor.postVisit( this );
    }
}