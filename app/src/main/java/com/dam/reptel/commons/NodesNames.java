package com.dam.reptel.commons;

import java.sql.Timestamp;

public interface NodesNames {

    // table user definie par le userID c.a.d. personne appelee donc recevant les messages
    String TABLE_USER = "UserID";

    // les clés pour les champs de la db
    String KEY_MYNUM = "Registered User Phone Number";
    String KEY_CALLERSNUM = "Num Tel de l'Appelant";
    String KEY_CALLERSNAME = "Nom de l'Appelant";
    String KEY_CALLERSNAMELOWERCASE = "Nom de l'Appelant Minuscule";
    String KEY_MESSAGE = "LienMessageDistant";
    String KEY_MESSAGE_LOCAL = "LienMessageLocal";
    String KEY_TIMESTAMP = "TimeStamp";
    String KEY_FLAG = "flag";

    // le dossier de stockage des messages dans le storage
    String MESSAGE_FOLDER = "Messages";
}
