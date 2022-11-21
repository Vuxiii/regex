import src.Regex.Regex;

public class App {
    public static void main( String[] args ) {
        Regex regex;
        // regex = new Regex( "(a|bc)*d" );
        // regex.compile();



        // regex.match( "Hej me ig :)daad" );
     
        regex = new Regex( "H..y" );
        // regex = new Regex( "." );
        regex.compile();

        regex.match( "Heey" );


    }
}