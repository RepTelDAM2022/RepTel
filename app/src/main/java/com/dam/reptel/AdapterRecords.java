package com.dam.reptel;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdapterRecords extends FirestoreRecyclerAdapter<ModelRecord, AdapterRecords.RecordsViewHolder> {

    private static final String TAG = "AdapterRecords";

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterRecords(@NonNull FirestoreRecyclerOptions<ModelRecord> options) { super(options); }

    @Override
    protected void onBindViewHolder(@NonNull AdapterRecords.RecordsViewHolder recordsViewHolder, int position, @NonNull ModelRecord model) {

        /** #On utilise le model pour récupérer les données qui nous intéresse **/
        String nom = model.getNomdelAppelant();
        String numAppelant = model.getNumTeldelAppelant();
        long timestamp = model.getTimeStamp();


        /** #On associe les données récupérées avec le holder de vue **/

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date resultDate = new Date(timestamp);
        String newDate = sdf.format(resultDate);

        recordsViewHolder.tvRecordDate.setText(newDate);
    }



    /** Class FilmsViewHolder */
    public class RecordsViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPlay;
        private TextView tvRecordName, tvRecordDuration, tvRecordDate, tvNomAppelant;

        public RecordsViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRecordDate = itemView.findViewById(R.id.tvRecordDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && recordClickListener != null) {
                        DocumentSnapshot recordSnapshot = getSnapshots().getSnapshot(position);
                        recordClickListener.onItemClick(recordSnapshot, position);
                    }
                }
            });
        }
    }

    /** Interface pour le clic */
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    private OnItemClickListener recordClickListener;

    public void setOnItemClickListener(OnItemClickListener recordClickListener){
        this.recordClickListener = recordClickListener;
    }

    /** Méthodes du View Holder */
    @NonNull
    @Override
    public AdapterRecords.RecordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_records, parent, false);
        return new RecordsViewHolder(view);
        //return null;
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
