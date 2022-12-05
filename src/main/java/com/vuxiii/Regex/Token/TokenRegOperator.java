package com.vuxiii.Regex.Token;

import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;
public class TokenRegOperator implements Token, TokenOperator {

    public final Term nonTerminal;

    public TokenRegOperator( Term nonTerminal ) {
        this.nonTerminal = nonTerminal;
    }

    public Term getTerm() {
        return nonTerminal;
    }

    public String toString() {
        return "[" + nonTerminal + "]";
    }

    @Override
    public void accept(VisitorBase visitor) {
        // TODO Auto-generated method stub
        visitor.visit( this );
    }
    
}
