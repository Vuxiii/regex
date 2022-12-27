package com.vuxiii.Regex.Token;
import com.vuxiii.LR.Records.NonTerminal;
import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;
import com.vuxiii.LR.Records.ASTToken;
public class TokenRoot implements ASTToken {

    public final Term term;
    public final ASTToken finish;
    public final TokenEOP eop;

    public TokenRoot( ASTToken finish, TokenEOP eop, NonTerminal nS ) {
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
