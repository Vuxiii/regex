package src.Regex.Token;

import src.LR.Term;
import src.Visitor.VisitorBase;

public class TokenPlus implements Token, TokenOperator {
    public final Term term;

    public TokenPlus( Term term ) {
        this.term = term;
    }

    public Term getTerm() {
        return term;
    }

    public String toString() {
        return "[" + term + "]";
    }

    @Override
    public void accept( VisitorBase visitor ) {
        visitor.visit( this );
    }
}
