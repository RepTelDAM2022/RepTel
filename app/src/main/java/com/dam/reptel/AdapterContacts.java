package com.dam.reptel;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AdapterContacts extends FirestoreRecyclerAdapter<Contact, AdapterContacts.ContactsViewHolder> {


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterContacts(@NonNull FirestoreRecyclerOptions<Contact> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ContactsViewHolder holder, int position, @NonNull Contact model) {

    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
