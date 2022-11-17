package src.Regex.Token;

import src.LR.Term;
import src.Visitor.VisitorAcceptor;

public interface Token extends VisitorAcceptor {
    public Term getTerm();
}
