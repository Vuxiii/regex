package com.vuxiii.LR.records;

public record ParseShift(int id) implements ParseAction {
    // public final int id;

    // public ParseShift( int id ) {
    //     this.id = id;
    // }

    public String toString() {
        return "s" + id;
    }
    
}
