package src.Regex.Token;

import src.LR.Term;
import src.Visitor.VisitorBase;

public class TokenRegEpsilon implements Token {

    public final Term nonTerminal;

    public TokenRegEpsilon( Term nonTerminal ) {
        this.nonTerminal = nonTerminal;
    }

    @Override
    public Term getTerm() {
        return nonTerminal;
    }

    @Override
    public void accept(VisitorBase visitor) {
        // TODO Auto-generated method stub
        
    }
    
}
