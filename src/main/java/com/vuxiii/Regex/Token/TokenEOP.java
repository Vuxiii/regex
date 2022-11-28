package com.vuxiii.Regex.Token;
import com.vuxiii.LR.Term;
import com.vuxiii.Visitor.*;

public class TokenEOP implements Token {
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
