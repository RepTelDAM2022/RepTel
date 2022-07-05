package com.dam.reptel;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdapterContacts extends FirestoreRecyclerAdapter<ModelRecord, AdapterContacts.ContactsViewHolder> {
    private static final String TAG = "AdapterContacts";

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterContacts(@NonNull FirestoreRecyclerOptions<ModelRecord> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterContacts.ContactsViewHolder contactsViewHolder, int position, @NonNull ModelRecord model) {
        Log.i(TAG, "onBindViewHolder: model.getTimeStamp = " + model.getTimeStamp());
        String nom = model.getNomdelAppelant();
        String tel = model.getNumTeldelAppelant();

        long timestamp = model.getTimeStamp();
        Log.i(TAG, "onBindViewHolder: timestamp = " + timestamp + "model.getTimeStamp = " + model.getTimeStamp());

        Bitmap photo = getDisplayPhoto(contactsViewHolder.tv_nomcontact.getContext(), tel);

        contactsViewHolder.tv_nomcontact.setText(nom);
        contactsViewHolder.tv_telcontact.setText(tel);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date resultDate = new Date(timestamp);
        String newDate = sdf.format(resultDate);
        Log.i(TAG, "onBindViewHolder: newDate = " + newDate);

        contactsViewHolder.tv_timestamp.setText(newDate);

        // recherche de la photo du contact dans les contacts a partir du numero de tel
        // photo = getDisplayPhoto(contactsViewHolder.tv_nomcontact.getContext(), tel);

        // ajout des options pour afficher les photos des contacts.
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.ic_contacts_24)
                .placeholder(R.drawable.ic_contacts_24);

        Context context = contactsViewHolder.iv_contactpicture.getContext();
        Glide.with(context)
                .load(photo)
                .apply(options)
                .fitCenter()
                .circleCrop()
                .override(150, 150)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(contactsViewHolder.iv_contactpicture);

    }

    @NonNull
    @Override
    public AdapterContacts.ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.contact_item_view, parent, false);

        return new ContactsViewHolder(view);
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder{
        private ImageView iv_contactpicture;
        private TextView tv_nomcontact, tv_telcontact, tv_timestamp;
        private Button btn_flag;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_contactpicture = itemView.findViewById(R.id.iv_contactpicture);
            tv_nomcontact = itemView.findViewById(R.id.tv_contactnom);
            tv_telcontact = itemView.findViewById(R.id.tv_contactnum);
            tv_timestamp = itemView.findViewById(R.id.tv_timestamp);
            btn_flag = itemView.findViewById(R.id.bt_flag);

            // gestion du click
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && contactClickListener != null){
                        DocumentSnapshot contactSnapshot = getSnapshots().getSnapshot(position);
                        contactClickListener.onItemClick(contactSnapshot, position);
                    }
                }
            });
        }
    }
    /** interface pour le click **/
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    private  OnItemClickListener contactClickListener;

    public void setOnItemClickListener(OnItemClickListener filmClickListener){
        this.contactClickListener = filmClickListener;
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
