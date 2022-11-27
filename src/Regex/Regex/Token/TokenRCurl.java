package src.Regex.Regex.Token;

import src.Regex.LR.Term;
import src.Regex.Visitor.VisitorBase;

public class TokenRCurl implements Token {

    public final Term term;

    public TokenRCurl( Term term ) {
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
