package com.dam.reptel;

import static com.dam.reptel.commons.NodesNames.KEY_CALLERSNAME;
import static com.dam.reptel.commons.NodesNames.KEY_CALLERSNAMELOWERCASE;
import static com.dam.reptel.commons.NodesNames.KEY_CALLERSNUM;
import static com.dam.reptel.commons.NodesNames.KEY_FIRSTMESSAGE;
import static com.dam.reptel.commons.NodesNames.KEY_FLAG;
import static com.dam.reptel.commons.NodesNames.KEY_MESSAGE;
import static com.dam.reptel.commons.NodesNames.KEY_MESSAGE_LOCAL;
import static com.dam.reptel.commons.NodesNames.KEY_MYNUM;
import static com.dam.reptel.commons.NodesNames.KEY_TIMESTAMP;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdapterRecords extends FirestoreRecyclerAdapter<ModelRecord, AdapterRecords.RecordsViewHolder> {

    private static final String TAG = "AdapterRecords";

    /** declaration de la bdd **/
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String userId;
    private DocumentReference documentReference;
    private CollectionReference colRef;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterRecords(@NonNull FirestoreRecyclerOptions<ModelRecord> options) {
        super(options);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onBindViewHolder(@NonNull AdapterRecords.RecordsViewHolder recordsViewHolder, int position, @NonNull ModelRecord model) {

        /** #On utilise le model pour récupérer les données qui nous intéresse **/
        String nom = model.getNomdelAppelant();
        String numAppelant = model.getNumTeldelAppelant();
        long timestamp = model.getTimeStamp();
        String mFile = model.getLienMessageLocal();
        boolean flaglu = model.flag;

        /** #On associe les données récupérées avec le holder de vue **/

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date resultDate = new Date(timestamp);
        String newDate = sdf.format(resultDate);

        recordsViewHolder.tvRecordDate.setText(newDate);

        /** test si flag lu on met le background a blanc **/
        if (flaglu){
            recordsViewHolder.ll_record.setBackgroundResource(R.drawable.cardview_back_white);
            recordsViewHolder.ivPlay.setImageResource(com.dam.reptel.R.drawable.ic_play_black_24);
            recordsViewHolder.tvRecordDate.setTextColor(R.color.black);
        }

        /** programmer le click sur un message **/

        recordsViewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                String mFile = model.getLienMessageLocal();
                playAudio(mFile);
                recordsViewHolder.ll_record.setBackgroundResource(R.drawable.cardview_back_white);
                recordsViewHolder.ivPlay.setImageResource(com.dam.reptel.R.drawable.ic_play_black_24);
                recordsViewHolder.tvRecordDate.setTextColor(R.color.black);

// TODO: 10/07/2022 ici programmer la mise du flag a true (tester s'il est a false) quand on lit le message.
                /** initialisation de la bdd **/
                firebaseAuth = FirebaseAuth.getInstance();
                userId = firebaseAuth.getCurrentUser().getUid();
                db = FirebaseFirestore.getInstance();
                colRef= db.collection(userId);
                documentReference = colRef.document("M"+timestamp);
                Log.i(TAG, "****** \n userID = " + userId + "\n colRef = " + colRef + "\n docRef = " + documentReference);

                /**
                * methode pour mettre le flag a true une fois le message lu
                */
                Map<String, Object> datas = new HashMap<>();
                datas.put(KEY_FLAG, true);

                documentReference.update(datas)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.i(TAG, "onSuccess: document added with ID = " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG, "onFailure: error adding document to db " + e);
                            }
                        });

            }
        });
    }

    /**
     * Méthodes du View Holder
     */
    @NonNull
    @Override
    public AdapterRecords.RecordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_records, parent, false);
        return new RecordsViewHolder(view);
        //return null;
    }

    /**
     * Class FilmsViewHolder
     */
    public class RecordsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRecordDate;
        CardView mainLayout;
        LinearLayout ll_record;
        ImageView ivPlay;

        public RecordsViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRecordDate = itemView.findViewById(R.id.tvRecordDate);
            mainLayout = itemView.findViewById(R.id.cv_record);
            ll_record = itemView.findViewById(R.id.ll_record);
            ivPlay = itemView.findViewById(R.id.ivPlay);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && recordClickListener != null) {
                        DocumentSnapshot recordSnapshot = getSnapshots().getSnapshot(position);
                        recordClickListener.onItemClick(recordSnapshot, position);
                    }
                }
            });
        }
    }

    /**
     * Interface pour le clic
     */
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    private OnItemClickListener recordClickListener;

    public void setOnItemClickListener(OnItemClickListener recordClickListener) {
        this.recordClickListener = recordClickListener;
    }

    private void playAudio(String mFile) {
        MediaPlayer mPlayer;
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFile);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "playAudio: failed");
        }
    }
}
