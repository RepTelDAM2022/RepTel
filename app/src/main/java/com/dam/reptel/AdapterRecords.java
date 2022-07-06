package com.dam.reptel;

import static android.content.ContentValues.TAG;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

        /** programmer le click sur un message **/

        recordsViewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mFile = model.getLienMessageLocal();
                playAudio(mFile);
            }
        });
    }

    /** Méthodes du View Holder */
    @NonNull
    @Override
    public AdapterRecords.RecordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_records, parent, false);
        return new RecordsViewHolder(view);
        //return null;
    }


    /** Class FilmsViewHolder */
    public class RecordsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRecordDate;
        CardView mainLayout;

        public RecordsViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRecordDate = itemView.findViewById(R.id.tvRecordDate);
            mainLayout = itemView.findViewById(R.id.cv_record);

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



    private void playAudio(String mFile) {
        MediaPlayer mPlayer;
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFile);
            mPlayer.prepare();;
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "playAudio: failed");
        }


    }


}
