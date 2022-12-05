package com.vuxiii.Regex.Token;

import com.vuxiii.LR.Records.Term;
import com.vuxiii.Visitor.*;

public interface Token extends VisitorAcceptor {
    public Term getTerm();
}
