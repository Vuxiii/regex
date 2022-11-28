package com.vuxiii.Regex.Token;

import com.vuxiii.LR.Term;
import com.vuxiii.Visitor.*;
public class TokenRegRange implements Token, RegRepetition, TokenOperator {

    public final Term nonTerminal;

    public final Token left;
    public final Token right;

    public final TokenRangeKind kind;

    public TokenRegRange( TokenRegDigit singleRange, TokenRangeKind kind, Term nonTerminal ) {
        left = singleRange;
        right = null;
        this.nonTerminal = nonTerminal;
        this.kind = kind;
    }

    public TokenRegRange( TokenRegDigit left, TokenRegDigit right, TokenRangeKind kind, Term nonTerminal ) {
        this.left = left;
        this.right = right;
        this.nonTerminal = nonTerminal;
        this.kind = kind;
    }


    @Override
    public Term getTerm() {
        return nonTerminal;
    }
 
    public String toString() {
        if ( right == null )
            return "[Range ->" + left.toString() + "]";
        return "[Range -> " + left.toString() + "-" + right.toString() + "]";
    }


    @Override
    public void accept(VisitorBase visitor) {
        visitor.preVisit( this );
        left.accept( visitor );
        visitor.midVisit( this );
        if ( right != null )
            right.accept( visitor );  
        visitor.postVisit( this );
    }
}