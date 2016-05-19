package com.potatoandtomato.common.utils;

import com.shaded.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by SiongLeng on 19/5/2016.
 */
public class SafeDouble {

    private Double value;
    private boolean valueChanged;
    private String valueHash;

    public SafeDouble() {
    }

    public SafeDouble(Double value) {
        setValue(value);
    }

    @JsonValue
    public Double getValue() {
        checking();
        return value;
    }

    @JsonValue
    public void setValue(Double value) {
        this.value = value;
        this.valueChanged = true;
    }

    private void checking(){
        if(valueChanged){
            valueChanged = false;
            valueHash = Strings.getHash(String.valueOf(value));
        }
        else{
            if(!Strings.getHash(String.valueOf(value)).equals(valueHash)){
                setValue(0.0);
            }
        }
    }

}
