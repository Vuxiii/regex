package com.vuxiii.Regex.Token;

import com.vuxiii.LR.records.Term;
import com.vuxiii.Visitor.*;
public class TokenLParen implements Token {

    public final Term term;

    public TokenLParen( Term term ) {
        this.term = term;
    }

    @Override
    public Term getTerm() {
        return term;
    }

    public String toString() {
        return "[" + term + "]";
    }

    @Override
    public void accept( VisitorBase visitor ) {
        visitor.visit( this );
    }
    
}
