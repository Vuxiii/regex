package src.Regex.Token;

import src.LR.Term;
import src.Visitor.VisitorBase;

public class TokenLCurl implements Token {

    public final Term term;

    public TokenLCurl( Term term ) {
        this.term = term;
    }

    @Override
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
