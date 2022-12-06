package com.vuxiii.Regex.Token;
import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;
public class TokenRegExp implements Token {

    public final Term nonTerminal;
    public final TokenRegUnion left;

    public final TokenRegRange range;
    // public final Token right;

    // public TokenRegExp( TokenRegUnion left, Token right, Term nonTerminal )  {
    //     this.left = left;
    //     this.right = right;
    //     this.nonTerminal = nonTerminal;
    // }

    public TokenRegExp( TokenRegUnion token, Term nonTerminal )  {
        this.left = token;
        range = null;
        this.nonTerminal = nonTerminal;
    }


    // public TokenRegExp( TokenRegRange token, Term nonTerminal )  {
    //     this.left = null;
    //     range = token;
    //     this.nonTerminal = nonTerminal;
    // }


    @Override
    public Term getTerm() {
        return nonTerminal;
    }
    
    public String toString() {
        // if ( range == null )
        //     return "[Exp -> " + range.toString() + "]";
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