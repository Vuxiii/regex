package com.vuxiii.Regex.Token;

import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;
public class TokenRegEpsilon implements Token {

    public final Term nonTerminal;

    public TokenRegEpsilon( Term nonTerminal ) {
        this.nonTerminal = nonTerminal;
    }

    @Override
    public Term getTerm() {
        return nonTerminal;
    }

    @Override
    public void accept(VisitorBase visitor) {
        // TODO Auto-generated method stub
        
    }
    
}
