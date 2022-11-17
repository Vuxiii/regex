package src.Regex.Token;

import src.LR.Term;
// import src.Token.Token;
// import src.Token.TokenOperator;
import src.Visitor.VisitorBase;

public class TokenRegOperator implements Token, TokenOperator {

    public final Term nonTerminal;

    public TokenRegOperator( Term nonTerminal ) {
        this.nonTerminal = nonTerminal;
    }

    public Term getTerm() {
        return nonTerminal;
    }

    public String toString() {
        return "[" + nonTerminal + "]";
    }

    @Override
    public void accept(VisitorBase visitor) {
        // TODO Auto-generated method stub
        visitor.visit( this );
    }
    
}
