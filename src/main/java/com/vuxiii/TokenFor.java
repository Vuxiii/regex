package com.vuxiii;
import com.vuxiii.LR.Records.Term;
import com.vuxiii.LR.Records.ASTToken;

import com.vuxiii.Visitor.*;

public class TokenFor implements ASTToken {

    public final String value;

    public TokenFor( String id ) {
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
        return "For: " + value;
    }
        
}
