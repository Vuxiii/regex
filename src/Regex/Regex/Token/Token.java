package src.Regex.Regex.Token;

import src.Regex.LR.Term;
import src.Regex.Visitor.VisitorAcceptor;

public interface Token extends VisitorAcceptor {
    public Term getTerm();
}
