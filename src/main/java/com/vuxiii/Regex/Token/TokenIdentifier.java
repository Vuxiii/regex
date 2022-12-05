package com.vuxiii.Regex.Token;
import com.vuxiii.LR.records.Term;
import com.vuxiii.Visitor.*;

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
