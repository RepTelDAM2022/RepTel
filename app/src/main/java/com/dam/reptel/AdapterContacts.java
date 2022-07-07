package com.dam.reptel;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dam.reptel.commons.Util;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdapterContacts extends FirestoreRecyclerAdapter<ModelRecord, AdapterContacts.ContactsViewHolder> {
    private static final String TAG = "AdapterContacts";
    private Context context;

    public AdapterContacts(@NonNull FirestoreRecyclerOptions<ModelRecord> options, Context context, OnItemClickListener contactClickListener) {
        super(options);
        this.context = context;
        this.contactClickListener = contactClickListener;
    }

    /**
     *
     * Creation d'un RecyclerViewAdapter pour afficher les contacts qui ont laissé un message
     *
     **/

    public AdapterContacts(@NonNull FirestoreRecyclerOptions<ModelRecord> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterContacts.ContactsViewHolder contactsViewHolder, int position, @NonNull ModelRecord model) {

        /** ici on recupere les donnees du model rempli par la query **/
        String nom = model.getNomdelAppelant();
        String tel = model.getNumTeldelAppelant();

        long timestamp = model.getTimeStamp();

        /** recherche de la photo du contact dans les contacts a partir du numero de tel
         * photo = getDisplayPhoto(contactsViewHolder.tv_nomcontact.getContext(), tel);
         **/

        Bitmap photo = Util.getDisplayPhoto(contactsViewHolder.tv_nomcontact.getContext(), tel);

        /** ici on rempli les donnees dans le RV **/


        contactsViewHolder.tv_nomcontact.setText(nom);
        contactsViewHolder.tv_telcontact.setText(tel);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date resultDate = new Date(timestamp);
        String newDate = sdf.format(resultDate);

        contactsViewHolder.tv_timestamp.setText(newDate);


        /** ajout des options pour afficher les photos des contacts.**/

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .circleCrop()
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

        /** fin du remplissage des donnees dans le RV **/

        /** programmer le click sur un contact **/

        contactsViewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, RecordsRecyclerView.class);
                intent.putExtra("numTel", tel);
                intent.putExtra("nomAppelant", nom);

                context.startActivity(intent);
            }
        });

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
        CardView mainLayout;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_contactpicture = itemView.findViewById(R.id.iv_contactpicture);
            tv_nomcontact = itemView.findViewById(R.id.tv_contactnom);
            tv_telcontact = itemView.findViewById(R.id.tv_contactnum);
            tv_timestamp = itemView.findViewById(R.id.tv_timestamp);
            btn_flag = itemView.findViewById(R.id.bt_flag);

            mainLayout = itemView.findViewById(R.id.cv_recyclercontactitem);

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

    public void setOnItemClickListener(OnItemClickListener contactClickListener){
        this.contactClickListener = contactClickListener;
    }
}
