package com.ntuaece.nikosapos.permission;

public enum Permission {
    ANY_SEND ("any"),
    NEIGHBOR_SEND ("neighbor"),
    NO_SEND ("no"),
    FREE_SEND ("free");
    
    private final String text;
    
    private Permission(final String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
    
}
