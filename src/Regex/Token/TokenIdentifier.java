package src.Regex.Token;

import src.LR.Term;
import src.Visitor.VisitorBase;

public class TokenIdentifier implements Token {

    public final String value;

    public TokenIdentifier( String id ) {
        value = id;
    }

    @Override
    public void accept(VisitorBase visitor) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Term getTerm() {
        // TODO Auto-generated method stub
        return null;
    }

    public String toString() {
        return "Identifier: " + value;
    }
        
}
