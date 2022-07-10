package com.dam.reptel;

import static android.content.ContentValues.TAG;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dam.reptel.commons.Util;
import com.dam.reptel.commons.Util.*;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    /**
     * Variables Globales
     */

    private Context context;
    private ArrayList<String> listeSansDoublons;
    private ArrayList<Long> nbreMessages;
    private String numTel;
    private long nbrMes;


    public ContactsAdapter(Context context, ArrayList<String> listeSansDoublons, ArrayList<Long> nbreMessages) {
        this.context = context;
        this.listeSansDoublons = listeSansDoublons;
        this.nbreMessages = nbreMessages;
    }

    @NonNull
    @Override
    public ContactsAdapter.ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.contact_item_view, parent, false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ContactsViewHolder contactsViewHolder, int position) {
        position = contactsViewHolder.getBindingAdapterPosition();
        numTel = listeSansDoublons.get(position);
        nbrMes = nbreMessages.get(position);
        String nom = Util.getContactNameByPhoneNumber(contactsViewHolder.tv_nomcontact.getContext(), numTel);
        Bitmap photo = Util.getDisplayPhoto(contactsViewHolder.tv_nomcontact.getContext(), numTel);

        contactsViewHolder.tv_nomcontact.setText(nom);
        contactsViewHolder.tv_telcontact.setText(numTel);
        contactsViewHolder.btn_flag.setText(String.valueOf(nbrMes));

        /** ajout des options pour afficher les photos des contacts.**/

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.ic_contacts_24)
                .circleCrop()
                .placeholder(R.drawable.ic_contacts_24);

//        Context context = contactsViewHolder.iv_contactpicture.getContext();
        Glide.with(context)
                .load(photo)
                .apply(options)
                .fitCenter()
                .circleCrop()
                .override(150, 150)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(contactsViewHolder.iv_contactpicture);

        /** fin du remplissage des donnees dans le RV **/

        Log.i(TAG, "ContactsAdapter.onBindViewHolder: **********" + numTel);

        /** programmer le click sur un contact **/

        contactsViewHolder.mainLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = contactsViewHolder.getBindingAdapterPosition();
                Log.i(TAG, "position ******: " + position);
                    Log.i("TAG", "ContactsAdapter.onClick ******* " + listeSansDoublons.get(position));
                    Intent intent = new Intent(context, RecordsRecyclerView.class);
                    intent.putExtra("numTel", listeSansDoublons.get(position));
                    context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listeSansDoublons.size();
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_contactpicture;
        private TextView tv_nomcontact, tv_telcontact, tv_timestamp;
        private Button btn_flag;
        LinearLayout mainLayout2;
        CardView mainLayout;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_contactpicture = itemView.findViewById(R.id.iv_contactpicture);
            tv_nomcontact = itemView.findViewById(R.id.tv_contactnom);
            tv_telcontact = itemView.findViewById(R.id.tv_contactnum);
            tv_timestamp = itemView.findViewById(R.id.tv_timestamp);
            btn_flag = itemView.findViewById(R.id.bt_flag);

            mainLayout = itemView.findViewById(R.id.cv_recyclercontactitem);
            mainLayout2 = itemView.findViewById(R.id.mainLayout2);

        }
    }

}

