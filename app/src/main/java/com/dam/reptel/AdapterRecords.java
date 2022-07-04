package com.dam.reptel;

import android.net.Uri;
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

public class AdapterRecords extends FirestoreRecyclerAdapter<ModelRecords, AdapterRecords.RecordsViewHolder> {

    private static final String TAG = "AdapterRecords";

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterRecords(@NonNull FirestoreRecyclerOptions<ModelRecords> options) { super(options); }

    /** Class FilmsViewHolder */
    public class RecordsViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPlay;
        private TextView tvRecordName, tvRecordDuration, tvRecordDate;

        public RecordsViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPlay = itemView.findViewById(R.id.ivPlay);
            tvRecordName = itemView.findViewById(R.id.tvRecordName);
            tvRecordDuration = itemView.findViewById(R.id.tvRecordDuration);
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

    @Override
    protected void onBindViewHolder(@NonNull AdapterRecords.RecordsViewHolder recordsViewHolder, int position, @NonNull ModelRecords modelRecords) {

        /** #On utilise le model pour récupérer les données qui nous intéresse **/
        String recName = modelRecords.getRecordName();
        String recDuration = modelRecords.getRecordDuration();
        String recDate = modelRecords.getRecordDate();
        Uri audioUri = modelRecords.getRecordUri();

        /** #On associe les données récupérées avec le holder de vue **/
        recordsViewHolder.tvRecordName.setText(recName);
        recordsViewHolder.tvRecordDate.setText(recDuration);
        recordsViewHolder.tvRecordDuration.setText(recDate);

        /** #On associe l'audio avec l'url **/
//        RequestOptions options = new RequestOptions()
//                .centerCrop()
//                .error(R.drawable.ic_movie_24_grey)
//                .placeholder(R.drawable.ic_movie_24_grey);
//
//        Context context = filmsViewHolder.ivAffiche.getContext();
//        Glide.with(context)
//                //On va loader l'image depuis le chemin vers le dossier de stockage des covers.
//                .load(affiche)
//                .apply(options)
//                .fitCenter()
//                .override(150,150)
//                .diskCacheStrategy(DiskCacheStrategy.ALL) //Affichage plus rapide de l'image
//                .into(filmsViewHolder.ivAffiche);
        /** #L'adapter est fini retournons dans le MainActivity pour récupérer les données via le fichier JSON **/

    }
}
