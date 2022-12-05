package com.vuxiii.Regex.Token;

import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;
public class TokenRegCharRange implements Token, RegRepetition, TokenOperator {

    public final Term nonTerminal;

    public final TokenChar left; // Token number?
    public final TokenChar right;// Token number?

    // public final TokenRangeKind kind;

    public TokenRegCharRange( TokenChar singleRange, Term nonTerminal ) {
        left = singleRange;
        right = null;
        this.nonTerminal = nonTerminal;
        // this.kind = kind;
    }

    public TokenRegCharRange( TokenChar left, TokenChar right, Term nonTerminal ) {
        this.left = left;
        this.right = right;
        this.nonTerminal = nonTerminal;
        // this.kind = kind;
    }


    @Override
    public Term getTerm() {
        return nonTerminal;
    }
 
    public String toString() {
        if ( right == null )
            return "[" + left.toString() + "]";
        return "[" + left.toString() + "-" + right.toString() + "]";
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