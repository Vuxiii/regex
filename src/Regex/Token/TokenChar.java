package src.Regex.Token;

import src.LR.Term;
import src.Visitor.VisitorBase;

public class TokenChar implements Token, TokenNumber {
    public final Character value;
    public final Term term;

    public TokenChar( char value, Term term ) {
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
