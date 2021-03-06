package com.dam.reptel;

import android.graphics.Bitmap;

import java.sql.Timestamp;

/**
 * Model de l'enregistrement dans la base de donnees
 */

public class ModelRecord {

    private String RegisteredUserPhoneNumber;
    private String NumTeldelAppelant;
    private String NomdelAppelant;
    private String NomdelAppelantMinuscule;
    private String LienMessageDistant;
    private String LienMessageLocal;
    private long TimeStamp;
    boolean flag;
    boolean firstMessage;

    public ModelRecord() {
    }

    public ModelRecord(String registeredUserPhoneNumber,
                       String numTeldelAppelant,
                       String nomdelAppelant,
                       String nomdelAppelantMinuscule,
                       String lienMessageDistant,
                       String lienMessageLocal,
                       long timeStamp,
                       boolean flag,
                       boolean firstMessage) {
        RegisteredUserPhoneNumber = registeredUserPhoneNumber;
        NumTeldelAppelant = numTeldelAppelant;
        NomdelAppelant = nomdelAppelant;
        NomdelAppelantMinuscule = nomdelAppelantMinuscule;
        LienMessageDistant = lienMessageDistant;
        LienMessageLocal = lienMessageLocal;
        TimeStamp = timeStamp;
        this.flag = flag;
        this.firstMessage = firstMessage;
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

    public long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        TimeStamp = timeStamp;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isFirstMessage() {
        return firstMessage;
    }

    public void setFirstMessage(boolean firstMessage) {
        this.firstMessage = firstMessage;
    }
}
