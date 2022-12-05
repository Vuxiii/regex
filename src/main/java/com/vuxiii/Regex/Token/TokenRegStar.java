package com.vuxiii.Regex.Token;

import com.vuxiii.LR.records.Term;
import com.vuxiii.Visitor.*;
public class TokenRegStar implements Token, RegRepetition, TokenOperator {

    public final Term nonTerminal;
    public TokenRegStar( Term nonTerminal ) {
        
        this.nonTerminal = nonTerminal;
    }


    @Override
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