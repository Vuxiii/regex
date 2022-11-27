package src.Regex.Regex.Token;

import src.Regex.LR.Term;
import src.Regex.Visitor.VisitorBase;

public class TokenArithmeticToken implements Token {

    public final Token value;
    public final TokenLParen lparen;
    public final TokenRParen rparen;
    public final Term term;

    public TokenArithmeticToken( TokenNumber val, Term term ) {
        value = (Token) val;
        lparen = null;
        rparen = null;
        this.term = term;
    }


    public TokenArithmeticToken( TokenArithmeticExpression token, TokenLParen left, TokenRParen right, Term term ) {
        value = token;
        lparen = left;
        rparen = right;
        this.term = term;
    }


    @Override
    public Term getTerm() {
        return term;
    }

    public String toString() {
        if ( lparen == null )
            return "[T -> " + value + "]";
        return "[T -> " + lparen  + " " + value + " " + rparen + "]";
    }


    @Override
    public void accept(VisitorBase visitor) {
        // TODO Auto-generated method stub
        
    }
    
}
