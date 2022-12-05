package com.vuxiii.Regex.Token;
import com.vuxiii.LR.records.Term;
import com.vuxiii.Visitor.*;

public class TokenArithmeticMult implements Token {

    public final TokenArithmeticToken left;
    public final TokenArithmeticMult right;
    public final TokenOperator operator;
    public final Term term;

    public TokenArithmeticMult( TokenArithmeticToken token, Term term ) {
        left = token;
        right = null;
        operator = null;
        this.term = term;
    }

    public TokenArithmeticMult( TokenArithmeticToken left, TokenArithmeticMult right, TokenOperator operator, Term term ) {
        this.left = left;
        this.right = right;
        this.operator = operator;
        this.term = term;
    }

    @Override
    public Term getTerm() {
        return term;
    }

    public String toString() {
        if ( right == null )
            return "[M -> " + left + "]";
        return "[M -> " + left + " " + operator + " " + right + "]";
    }

    @Override
    public void accept(VisitorBase visitor) {
        // TODO Auto-generated method stub
        
    }
    
}
