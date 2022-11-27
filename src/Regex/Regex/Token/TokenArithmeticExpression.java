package src.Regex.Regex.Token;

import src.Regex.LR.Term;
import src.Regex.Visitor.VisitorBase;

public class TokenArithmeticExpression implements Token {

    public final TokenArithmeticMult left;
    public final TokenArithmeticExpression right;
    public final TokenOperator operator;
    public final Term term;

    public TokenArithmeticExpression( TokenArithmeticMult token, Term term ) {
        left = token;
        right = null;
        operator = null;
        this.term = term;
    }

    public TokenArithmeticExpression(TokenArithmeticMult left, TokenArithmeticExpression right, TokenOperator operator, Term term ) {
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
            return "[E -> " + left + "]";
        return "[E -> " + left + " " + operator + " " + right + "]";
    }

    @Override
    public void accept(VisitorBase visitor) {
        // TODO Auto-generated method stub
        
    }
    
}
