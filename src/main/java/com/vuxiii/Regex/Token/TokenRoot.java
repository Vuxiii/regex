package com.vuxiii.Regex.Token;
import com.vuxiii.LR.NonTerminal;
import com.vuxiii.LR.Term;
import com.vuxiii.Visitor.*;
public class TokenRoot implements Token {

    public final Term term;
    public final Token finish;
    public final TokenEOP eop;

    public TokenRoot( Token finish, TokenEOP eop, NonTerminal nS ) {
        this.term = nS;
        this.finish = finish;
        this.eop = eop;
    }

    @Override
    public void accept(VisitorBase visitor) {
        visitor.preVisit( this );
        finish.accept( visitor );
        visitor.midVisit( this );
        eop.accept( visitor );
        visitor.postVisit( this );
    }

    public String toString() {
        return "[S -> " + finish.toString() + " " + eop.toString() + " ]";
    }

    @Override
    public Term getTerm() {
        return term;
    }
    
}
