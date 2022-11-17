package src.Regex.Token;

import src.LR.Term;
import src.LR.Terminal;
// import src.Token.Token;
import src.Visitor.VisitorBase;

public class TokenRegRepetition implements Token {

    public final Term nonTerminal;
    public final RegRepetition value;

    public TokenRegRepetition( RegRepetition value, Term nonTerminal ) {
        this.value = value;
        this.nonTerminal = nonTerminal;
    }


    @Override
    public Term getTerm() {
        return nonTerminal;
    }

    public String toString() {
        return value.toString();
    }


    @Override
    public void accept(VisitorBase visitor) {
        // TODO Auto-generated method stub
        visitor.visit( this );
    }
    
    
}