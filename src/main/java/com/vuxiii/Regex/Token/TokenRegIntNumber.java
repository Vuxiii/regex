package com.vuxiii.Regex.Token;

import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;
import com.vuxiii.LR.Records.ASTToken;
public class TokenRegIntNumber implements ASTToken, TokenNumber {
    public final Integer value;
    public final Term term;


    public TokenRegIntNumber( Integer value, Term term ) {
        this.value = value;
        this.term = term;
    }
    // public TokenRegIntNumber( Integer left, Integer right, Term term ) {
    //     this.value = value;
    //     this.term = term;
    // }
    // public TokenRegIntNumber( Term term ) { // This is any digit.
    //     this.value = null;
    //     this.term = term;
    // }

    public Term getTerm() {
        return term;
    }

    public String toString() {
        return "[" + term.toString() + " ; " + value + "]";
    }

    @Override
    public void accept( VisitorBase visitor ) {
        visitor.visit( this );
    }
}
