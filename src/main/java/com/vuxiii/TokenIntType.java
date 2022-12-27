package com.vuxiii;
import com.vuxiii.LR.Records.Term;
import com.vuxiii.LR.Records.ASTToken;

import com.vuxiii.Visitor.*;

public class TokenIntType implements ASTToken {

    public final String value;

    public TokenIntType( String id ) {
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
        return "Type: " + value;
    }
        
}
