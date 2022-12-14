package com.vuxiii.Regex.Token;

import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;
import com.vuxiii.LR.Records.ASTToken;
public class TokenRegDash implements ASTToken, TokenNumber {
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
