package src.Regex.Visitor;

import src.Regex.Regex.Token.Token;

public class LeafVisitor extends VisitorBase {
    

    public void visit_( Token t ) {
        System.out.print( t );
    }
    // public void preVisit_( Token t ) {
    //     System.out.println( "preVisit\t" + t );
    // }
    // public void midVisit_( Token t ) {
    //     System.out.println( "midVisit\t" + t );
    // }
    // public void postVisit_( Token t ) {
    //     System.out.println( "postVisit\t" + t );
    // }
}
