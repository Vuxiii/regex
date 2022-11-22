import src.NFA.NFA_state;
import src.Regex.Regex;

public class App {
    public static void main( String[] args ) {
        Regex regex;
        // regex = new Regex( "(a|bc)*d" );
        // regex.compile();



        // regex.match( "Hej me ig :)daad" );
     
        regex = new Regex( "H{3}" );
        // regex = new Regex( "H.{2}y" );
        // regex = new Regex( "." );
        regex.compile();

        regex.match( "Heey" );


        // NFA_state start = new NFA_state();

        // start.registerWord( "hej" ).addEdge( start );

        // System.out.println( "-----------------------------------------");
        // System.out.println( NFA_state.getStringRepresentation(start));
        // System.out.println( "-----------------------------------------");
        // System.out.println( NFA_state.getStringRepresentation(start.copy()));

    }
}