package com.vuxiii.Regex.Token;
import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;
import com.vuxiii.LR.Records.ASTToken;
public class TokenRegSymbol implements ASTToken {

    // public final TokenRegRepetition rep;

    public final Term nonTerminal;
    public final ASTToken value;
    // public final boolean isEps;

    public final TokenLParen lparen;
    public final TokenRParen rparen;
    // public final TokenRegExp exp;
    public TokenRegSymbol( ASTToken value, Term nonTerminal ) {
        this.value = value;
        this.nonTerminal = nonTerminal;
        // isEps = false;
        // this.rep = null;
        lparen = null;
        rparen = null;
    }

    public TokenRegSymbol(TokenLParen left, TokenRegExp exp, TokenRParen right, Term nonTerminal ) {
        this.nonTerminal = nonTerminal;
        lparen = left;
        rparen = right;
        this.value = exp;

        // this.rep = right;
        // isEps = false;
    }

    @Override
    public Term getTerm() {
        return nonTerminal;
    }
    
    public String toString() {
        if ( lparen == null )
            return "[Sym -> " + value + "]"; 
        return "[Sym -> " + lparen.toString() + " " + value.toString() + " " + rparen.toString() + "]";
        // return "[Sym -> " + value + " " + rep.toString() + "]"; 
    }

    @Override
    public void accept( VisitorBase visitor ) {
        visitor.preVisit( this );
        if ( lparen != null )
            lparen.accept( visitor );
        visitor.midVisit( this );
        value.accept( visitor );
        if ( rparen != null )
            rparen.accept( visitor );  
        visitor.postVisit( this );
    }

}
