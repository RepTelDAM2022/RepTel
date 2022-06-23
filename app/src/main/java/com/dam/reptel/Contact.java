package com.dam.reptel;

import com.google.firebase.firestore.Exclude;

public class Contact {
    private String documentId;
    private String nom;
    private String numTel;

    public Contact() {
    }

    public Contact(String nom, String numTel) {
        this.nom = nom;
        this.numTel = numTel;
    }

    public String getNom() {return nom;}

    public void setNom(String nom) {this.nom = nom;}

    public String getNumTel() {return numTel;}

    public void setNumTel(String numTel) {this.numTel = numTel;}

    @Exclude
    public String getDocumentId() {return documentId;}

    public void setDocumentId(String documentId) {this.documentId = documentId;}
}
