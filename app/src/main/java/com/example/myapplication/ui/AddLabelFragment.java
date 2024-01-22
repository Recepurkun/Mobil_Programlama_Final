package com.example.myapplication.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class AddLabelFragment extends Fragment {
    String  girisYapan;
    private Button labelAdd;
    private  FirebaseFirestore db = FirebaseFirestore.getInstance();
    private  LinearLayout labelsLineer;
    private View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_addlabel, container, false);

        labelAdd = root.findViewById(R.id.labelKayit_Btn);

        Intent intent = getActivity().getIntent();
        girisYapan = intent.getStringExtra("email");
        //String userName = intent.getStringExtra("firstname");
        labelleriGoster(girisYapan);
        if (girisYapan != null) {
            // userEmail değeri null değilse, burada kullanabilirsiniz
            Toast.makeText(getContext(), "Email: " + girisYapan, Toast.LENGTH_SHORT).show();
            //Toast.makeText(getContext(), "Username: " + userName, Toast.LENGTH_SHORT).show();
        } else {
            // userEmail null ise, bir hata durumu olabilir
            Toast.makeText(getContext(), "Email bilgisi alınamadı", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "userName bilgisi alınamadı", Toast.LENGTH_SHORT).show();
        }

        labelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String labelIcerik = ((EditText)root.findViewById(R.id.labelGirilenEtiket)).getText().toString();
                String descriptionIcerik = ((EditText)root.findViewById(R.id.labelGirilenAciklama)).getText().toString();
                Map<String, Object> labelData = new HashMap<>();
                labelData.put("description", !descriptionIcerik.isEmpty() ? descriptionIcerik : "labels");
                labelData.put("label", labelIcerik);
                labelData.put("email", girisYapan);

                CollectionReference labelsCollectionRef = db.collection("labels");
                labelsCollectionRef.add(labelData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(), "Kayıt başarılı label eklendi!!", Toast.LENGTH_SHORT).show();
                        labelleriGoster(girisYapan);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Kayıt Başarısız label eklenemedi!!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return root;
    }
    @SuppressLint("SetTextI18n")
    private void labelleriGoster(String girisYapan) {
        this.girisYapan = girisYapan;
        labelsLineer = root.findViewById(R.id.addlabel_view);
        labelsLineer.removeAllViews();
        //Toast.makeText(getContext(), "Burası labelleriGoster: " + this.girisYapan, Toast.LENGTH_LONG).show();

        db.collection("labels")
                .whereEqualTo("email", this.girisYapan)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String labelName = document.getString("label");
                            String labelDesc = document.getString("description");

                            Context context = getContext();
                            ConstraintLayout labelLayout = new ConstraintLayout(context);

                            TextView textView = new TextView(context);
                            textView.setId(View.generateViewId());
                            textView.setText("Label: " + labelName + "\nDescription: " + labelDesc);

                            Button deleteButton = new Button(context);
                            deleteButton.setId(View.generateViewId());
                            deleteButton.setText("Sil");
                            deleteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    labelSil(document.getId());
                                    labelsLineer.removeView(labelLayout);
                                }
                            });

                            labelLayout.addView(textView);
                            labelLayout.addView(deleteButton);
                            labelsLineer.addView(labelLayout);

                            ConstraintSet constraintSet = new ConstraintSet();
                            constraintSet.clone(labelLayout);

                            constraintSet.connect(textView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                            constraintSet.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);

                            constraintSet.connect(deleteButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                            constraintSet.connect(deleteButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

                            constraintSet.applyTo(labelLayout);
                        }
                    } else {
                        Log.e("FIRESTORE", "Veri çekilemedi", task.getException());
                    }
                });
    }


    private void labelSil(String documentId){
        db.collection("labels")
                .document(documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Label başarılı bir şekilde silindi", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Label silinirken hata olustu", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}