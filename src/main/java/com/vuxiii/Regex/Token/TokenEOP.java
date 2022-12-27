package com.vuxiii.Regex.Token;
import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;
import com.vuxiii.LR.Records.ASTToken;

public class TokenEOP implements ASTToken {
    public final Term term;

    public TokenEOP( Term term ) {
        this.term = term;

    }

    @Override
    public Term getTerm() {
        return term;
    }

    public String toString() {
        return "[" + term.toString() + "]";
    }

    @Override
    public void accept(VisitorBase visitor) {
        visitor.preVisit( this );
        visitor.midVisit( this );
        visitor.postVisit( this );
    }
    
}
