package com.vuxiii.Regex.Token;
import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;

public class TokenLBracket implements Token {

    public final Term term;

    public TokenLBracket( Term term ) {
        this.term = term;
    }

    @Override
    public Term getTerm() {
        return term;
    }

    public String toString() {
        return "[ '" + term + "' ]";
    }

    @Override
    public void accept( VisitorBase visitor ) {
        visitor.visit( this );
    }    
}
