package com.dam.reptel;

import android.graphics.Bitmap;

import java.sql.Timestamp;

public class ModelRecord {

    private String RegisteredUserPhoneNumber;
    private String NumTeldelAppelant;
    private String NomdelAppelant;
    private String NomdelAppelantMinuscule;
    private String LienMessageDistant;
    private String LienMessageLocal;
    private String TimeStamp;
    boolean flag;

    public ModelRecord() {
    }

    public ModelRecord(String registeredUserPhoneNumber, String numTeldelAppelant, String nomdelAppelant, String nomdelAppelantMinuscule, String lienMessageDistant, String lienMessageLocal, String timeStamp, boolean flag) {
        RegisteredUserPhoneNumber = registeredUserPhoneNumber;
        NumTeldelAppelant = numTeldelAppelant;
        NomdelAppelant = nomdelAppelant;
        NomdelAppelantMinuscule = nomdelAppelantMinuscule;
        LienMessageDistant = lienMessageDistant;
        LienMessageLocal = lienMessageLocal;
        TimeStamp = timeStamp;
        this.flag = flag;
    }

    public String getRegisteredUserPhoneNumber() {
        return RegisteredUserPhoneNumber;
    }

    public void setRegisteredUserPhoneNumber(String registeredUserPhoneNumber) {
        RegisteredUserPhoneNumber = registeredUserPhoneNumber;
    }

    public String getNumTeldelAppelant() {
        return NumTeldelAppelant;
    }

    public void setNumTeldelAppelant(String numTeldelAppelant) {
        NumTeldelAppelant = numTeldelAppelant;
    }

    public String getNomdelAppelant() {
        return NomdelAppelant;
    }

    public void setNomdelAppelant(String nomdelAppelant) {
        NomdelAppelant = nomdelAppelant;
    }

    public String getNomdelAppelantMinuscule() {
        return NomdelAppelantMinuscule;
    }

    public void setNomdelAppelantMinuscule(String nomdelAppelantMinuscule) {
        NomdelAppelantMinuscule = nomdelAppelantMinuscule;
    }

    public String getLienMessageDistant() {
        return LienMessageDistant;
    }

    public void setLienMessageDistant(String lienMessageDistant) {
        LienMessageDistant = lienMessageDistant;
    }

    public String getLienMessageLocal() {
        return LienMessageLocal;
    }

    public void setLienMessageLocal(String lienMessageLocal) {
        LienMessageLocal = lienMessageLocal;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
