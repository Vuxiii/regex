package com.vuxiii.Regex.Token;

import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;
import com.vuxiii.LR.Records.ASTToken;
public class TokenRegIntRange implements ASTToken, RegRepetition, TokenOperator {

    public final Term nonTerminal;

    public final TokenRegIntNumber left; // Token number?
    public final TokenRegIntNumber right;// Token number?

    // public final TokenRangeKind kind;

    public TokenRegIntRange( TokenRegIntNumber singleRange, Term nonTerminal ) {
        left = singleRange;
        right = null;
        this.nonTerminal = nonTerminal;
        // this.kind = kind;
    }

    public TokenRegIntRange( TokenRegIntNumber left, TokenRegIntNumber right, Term nonTerminal ) {
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