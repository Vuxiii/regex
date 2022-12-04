package com.vuxiii.Regex.Token;

import com.vuxiii.LR.Term;
import com.vuxiii.Visitor.*;

public class TokenChar implements Token, TokenNumber {
    public final Character value;
    public final Term term;

    public final TokenCharKind kind;

    public TokenChar( char value, Term term, TokenCharKind kind ) {
        this.value = value;
        this.term = term;
        this.kind = kind;
    }

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