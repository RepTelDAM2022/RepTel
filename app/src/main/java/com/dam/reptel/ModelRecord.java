package com.dam.reptel;

import java.sql.Timestamp;

public class ModelRecord {

    private String numReception;
    private String telAppelant;
    private String nomAppelant;
    private String lienMessage;
    private Timestamp timeStamp;
    boolean flag;

    public ModelRecord(String numReception, String telAppelant, String nomAppelant, String lienMessage, Timestamp timeStamp, boolean flag) {
        this.numReception = numReception;
        this.telAppelant = telAppelant;
        this.nomAppelant = nomAppelant;
        this.lienMessage = lienMessage;
        this.timeStamp = timeStamp;
        this.flag = flag;
    }

    public ModelRecord() {
    }

    public String getNumReception() {
        return numReception;
    }

    public void setNumReception(String numReception) {
        this.numReception = numReception;
    }

    public String getTelAppelant() {
        return telAppelant;
    }

    public void setTelAppelant(String telAppelant) {
        this.telAppelant = telAppelant;
    }

    public String getLienMessage() {
        return lienMessage;
    }

    public void setLienMessage(String lienMessage) {
        this.lienMessage = lienMessage;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getNomAppelant() {
        return nomAppelant;
    }

    public void setNomAppelant(String nomAppelant) {
        this.nomAppelant = nomAppelant;
    }
}
