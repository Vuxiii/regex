package src.Regex.Token;

import src.LR.Term;
import src.LR.Terminal;
// import src.Token.Token;
// import src.Token.TokenOperator;
import src.Visitor.VisitorBase;

public class TokenRegStar implements Token, RegRepetition, TokenOperator {

    public final Term nonTerminal;
    public TokenRegStar( Term nonTerminal ) {
        
        this.nonTerminal = nonTerminal;
    }


    @Override
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