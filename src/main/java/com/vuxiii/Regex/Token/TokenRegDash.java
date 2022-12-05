package com.vuxiii.Regex.Token;

import com.vuxiii.LR.records.Term;
import com.vuxiii.Visitor.*;
public class TokenRegDash implements Token, TokenNumber {
    public final Term term;


    public TokenRegDash( Term term ) {
        
        this.term = term;
    }

    public Term getTerm() {
        return term;
    }

    public String toString() {
        return "[:dash:]";
    }

    @Override
    public void accept( VisitorBase visitor ) {
        visitor.visit( this );
    }
}
