package src.Regex.Regex.Token;

import src.Regex.LR.Term;
import src.Regex.Visitor.VisitorBase;

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
