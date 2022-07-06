package com.dam.reptel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;

public class RecordsActivity extends AppCompatActivity {

    /** Variables Globales **/
    private String nom, tel;
    Bitmap photo;

    // Les widgets
    TextView tvNomContact;
    ImageView ivPhotoContact;

    //init widget
    private void init() {
        tvNomContact = findViewById(R.id.tvNomContact);
        ivPhotoContact = findViewById(R.id.ivPhotoContact);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        init();
        Intent intent = getIntent();
        String nom = intent.getStringExtra("nomAppelant");
        String tel = intent.getStringExtra("numTel");
        photo = getDisplayPhoto(this, tel);
        if (nom!=null){
            tvNomContact.setText(nom);
        } else {
            tvNomContact.setText(tel);
        }
        // ajout des options pour afficher les photos des contacts.
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .circleCrop()
                .error(R.drawable.ic_contacts_24)
                .placeholder(R.drawable.ic_contacts_24);

        Context context = ivPhotoContact.getContext();
        Glide.with(context)
                .load(photo)
                .apply(options)
                .fitCenter()
                .circleCrop()
                .override(150, 150)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivPhotoContact);
    }

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
}