package src.Regex.Regex.Token;

import src.Regex.LR.Term;
import src.Regex.LR.Terminal;
import src.Regex.Visitor.VisitorBase;

public class TokenRegUdtryk implements Token {

    public final TokenRegRepetition rep;
    public final Term nonTerminal;
    public final Token value;

    public TokenRegUdtryk( Token value, Term nonTerminal ) {
        this.value = value;
        this.nonTerminal = nonTerminal;
        rep = null;
    }

    public TokenRegUdtryk( Token value, TokenRegRepetition right, Term nonTerminal) {
        this.value = value;
        this.nonTerminal = nonTerminal;
        this.rep = right;
    }


    @Override
    public Term getTerm() {
        return nonTerminal;
    }
    
    public String toString() {
        if ( rep == null )
            return "[Udt -> " + value + "]"; 
        return "[Udt -> " + value + " [" + rep.nonTerminal.toString() + "] " + rep.toString() + "]"; 
    }

    @Override
    public void accept( VisitorBase visitor ) {
        visitor.preVisit( this );
        value.accept( visitor );
        visitor.midVisit( this );
        if ( rep != null )
            rep.accept( visitor );  
        visitor.postVisit( this );
    }
    
}