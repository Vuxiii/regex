package src.Regex.Regex.Token;

import src.Regex.LR.Term;
import src.Regex.Visitor.VisitorBase;

public class TokenRegDigit implements Token, TokenNumber {
    public final Integer value;
    public final Term term;


    public TokenRegDigit( Integer value, Term term ) {
        this.value = value;
        this.term = term;
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
