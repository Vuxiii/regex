package com.vuxiii.Regex.Token;

import com.vuxiii.LR.records.Term;
import com.vuxiii.Visitor.*;

public interface Token extends VisitorAcceptor {
    public Term getTerm();
}
