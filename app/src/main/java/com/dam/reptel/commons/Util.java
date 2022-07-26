package com.dam.reptel.commons;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.ContactsContract;

import java.io.IOException;

public class Util {
    /** Méthode pour le check d'internet **/
    public static boolean connectionAvailable(Context context){
        // Pour utiliser cette classe il ne faut oublier d'ajouter la permisson dans le manifest
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null ){
            return connectivityManager.getActiveNetworkInfo().isAvailable();
        } else {
            return false;
        }
    }

//    public static String getContactNameByPhoneNumber(Context context, String phoneNumber) {
//        ContentResolver cr = context.getContentResolver();
//        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
//        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
//        if (cursor == null) {
//            return null;
//        }
//        String contactName = null;
//        if (cursor.moveToFirst()) {
//            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
//        }
//
//        if (!cursor.isClosed()) {
//            cursor.close();
//        }
//        return contactName;
//    }

    public static Bitmap getDisplayPhoto(Context context, String contactNumber) {

        contactNumber = Uri.encode(contactNumber);
        int phoneContactID = -1;
        Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contactNumber),
                new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID }, null, null, null);
        while (contactLookupCursor.moveToNext()) {
            phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
        }
        contactLookupCursor.close();

        Bitmap photo = null;
        if (phoneContactID != -1) {
            Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, phoneContactID);
            Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
            try {
                AssetFileDescriptor fd = context.getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");

                photo = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());
            } catch (IOException e) {
            }
        }

        return photo;
    }

    public static String getContactNameByPhoneNumber(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }

}
