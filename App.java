import src.Regex.Regex;

public class App {
    public static void main( String[] args ) {
        Regex regex = new Regex( "(a|bc)*d" );
        regex.compile();

        
    }
}