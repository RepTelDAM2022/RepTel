package com.dam.reptel;

import java.sql.Timestamp;

public interface NodesNames {

    // table user definie par le userID c.a.d. personne appelee donc recevant les messages
    String TABLE_USER = "Registered UserID";

    // les clés pour les champs de la db
    String KEY_MYNUM = "Registered User Phone Number";
    String KEY_CALLERSNUM = "Num Tel de l'Appelant";
    String KEY_CALLERSNAME = "Nom de l'Appelant";
    String KEY_MESSAGE = "LienMessage";
    String KEY_TIMESTAMP = "TimeStamp";
    String KEY_FLAG = "flag";

    // le dossier de stockage des messages dans le storage
    String MESSAGE_FOLDER = "Messages";
}
