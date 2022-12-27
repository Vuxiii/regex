package com.vuxiii.Regex.Token;

import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;
import com.vuxiii.LR.Records.ASTToken;
public class TokenRegEpsilon implements ASTToken {

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
