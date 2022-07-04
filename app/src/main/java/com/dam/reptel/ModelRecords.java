package com.dam.reptel;

import android.net.Uri;

public class ModelRecords {

    private String recordsUserId;
    private String recordsContactPicture;
    private String recordsContactName;

    private String recordName;
    private String recordDate;
    private String recordDuration;
    private Uri recordUri;

    private boolean recordReadState;
    private boolean recordTrashState;

    public ModelRecords() {
    }

    public ModelRecords(String recordsUserId, String recordsContactPicture, String recordsContactName, String recordName, String recordDate, String recordDuration, Uri recordUri, boolean recordReadState, boolean recordTrashState) {
        this.recordsUserId = recordsUserId;
        this.recordsContactPicture = recordsContactPicture;
        this.recordsContactName = recordsContactName;
        this.recordName = recordName;
        this.recordDate = recordDate;
        this.recordDuration = recordDuration;
        this.recordUri = recordUri;
        this.recordReadState = recordReadState;
        this.recordTrashState = recordTrashState;
    }

    public String getRecordsUserId() {
        return recordsUserId;
    }

    public void setRecordsUserId(String recordsUserId) {
        this.recordsUserId = recordsUserId;
    }

    public String getRecordsContactPicture() {
        return recordsContactPicture;
    }

    public void setRecordsContactPicture(String recordsContactPicture) {
        this.recordsContactPicture = recordsContactPicture;
    }

    public String getRecordsContactName() {
        return recordsContactName;
    }

    public void setRecordsContactName(String recordsContactName) {
        this.recordsContactName = recordsContactName;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getRecordDuration() {
        return recordDuration;
    }

    public void setRecordDuration(String recordDuration) {
        this.recordDuration = recordDuration;
    }

    public Uri getRecordUri() {
        return recordUri;
    }

    public void setRecordUri(Uri recordUri) {
        this.recordUri = recordUri;
    }

    public boolean isRecordReadState() {
        return recordReadState;
    }

    public void setRecordReadState(boolean recordReadState) {
        this.recordReadState = recordReadState;
    }

    public boolean isRecordTrashState() {
        return recordTrashState;
    }

    public void setRecordTrashState(boolean recordTrashState) {
        this.recordTrashState = recordTrashState;
    }
}
