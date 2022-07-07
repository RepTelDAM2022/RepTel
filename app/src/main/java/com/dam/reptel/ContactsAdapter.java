package com.dam.reptel;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<AdapterContacts.ContactsViewHolder> {

    /**
     * Variables Globales
     */
    ArrayList<String> listeSansDoublons;
    Context context;

    public ContactsAdapter(Context context, ArrayList<String> listeSansDoublons) {
        this.context = context;
        this.listeSansDoublons = listeSansDoublons;
    }

    @NonNull
    @Override
    public AdapterContacts.ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterContacts.ContactsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


}
