package com.vuxiii.Regex.Token;
import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;
public class TokenRegExp implements Token {

    public final Term nonTerminal;
    public final TokenRegUnion left;
    // public final Token right;

    // public TokenRegExp( TokenRegUnion left, Token right, Term nonTerminal )  {
    //     this.left = left;
    //     this.right = right;
    //     this.nonTerminal = nonTerminal;
    // }

    public TokenRegExp( TokenRegUnion token, Term nonTerminal )  {
        this.left = token;
        // this.right = null;
        this.nonTerminal = nonTerminal;
    }


    @Override
    public Term getTerm() {
        return nonTerminal;
    }
    
    public String toString() {
        // if ( right == null )
        //     return "[Exp -> " + left.toString() + "]";
        return "[Exp -> " + left.toString() + "]";
    }

    @Override
    public void accept( VisitorBase visitor ) {
        visitor.preVisit( this );
        left.accept( visitor );
        visitor.midVisit( this );
        // if ( right != null )
        //     right.accept( visitor );  
        visitor.postVisit( this );
    }
    
}