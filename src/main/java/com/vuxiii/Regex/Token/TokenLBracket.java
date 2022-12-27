package com.vuxiii.Regex.Token;
import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;
import com.vuxiii.LR.Records.ASTToken;

public class TokenLBracket implements ASTToken {

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
