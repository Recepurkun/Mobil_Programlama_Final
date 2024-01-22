package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GalleryFragment extends Fragment {

    String  girisYapan, girisYapanName, str_like, str_dislike, secilenLabel;
    Spinner spin;
    LinearLayout galeriLayout;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> labelsList = new ArrayList<>();
    private View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_gallery, container, false);

        Intent intent = getActivity().getIntent();
        girisYapan = intent.getStringExtra("email");
        //girisYapanName = intent.getStringExtra("firstname");
        //fotoLabelleriGoster(girisYapan);
        if (girisYapan != null) {
            // userEmail değeri null değilse, burada kullanabilirsiniz
            Toast.makeText(getContext(), "Email: " + girisYapan, Toast.LENGTH_SHORT).show();
            //Toast.makeText(getContext(), "Username: " + girisYapanName, Toast.LENGTH_SHORT).show();
        } else {
            // userEmail null ise, bir hata durumu olabilir
            Toast.makeText(getContext(), "Email bilgisi alınamadı" + girisYapan, Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "userName bilgisi alınamadı" + girisYapanName, Toast.LENGTH_SHORT).show();
        }

        galeriyiGoster();
        spinView();

        return root;
    }

    private void galeriyiGoster(){
        galeriLayout = root.findViewById(R.id.galeriLineerLayout);

        db.collection("galeri")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()){
                            String user = document.getString("name");
                            String photoName = document.getString("foto");

                            str_like =document.getString("like");
                            int like = (str_like != null && !str_like.isEmpty()) ? Integer.parseInt(str_like) : 0;

                            str_dislike = document.getString("dislike");
                            int dislike = (str_dislike != null && !str_dislike.isEmpty()) ? Integer.parseInt(str_dislike) : 0;

                            String ID = document.getId();

                            Object objectLabel = document.get("labels");
                            if (objectLabel instanceof List<?>){
                                List<?> labelList = (List<?>) objectLabel;

                                for (Object item : labelList){
                                    if(item instanceof String){
                                        String label = (String) item;
                                        labelsList.add(label);
                                    }
                                    else{
                                        //Toast.makeText(getContext(), "Firestore labels içi string değil!" + item, Toast.LENGTH_SHORT).show();
                                        Log.e("Firestore ", "labels içi string değil!");
                                    }
                                }
                            }
                            else{
                                Log.e("firestore", "1. Labels liste değil" + objectLabel);
                                continue;
                            }

                            ConstraintLayout constraintLayout = new ConstraintLayout(getContext());

                            TextView textName = new TextView(getContext());
                            textName.setId(View.generateViewId());
                            textName.setText(user);

                            TextView textlabels = new TextView(getContext());
                            textlabels.setId(View.generateViewId());
                            textlabels.setText("Labels: \n" + labelsList );
                            labelsList.clear();

                            Button begeni = new Button(getContext());
                            begeni.setId(View.generateViewId());
                            begeni.setText("Like: " + str_like);

                            Button notbegeni = new Button(getContext());
                            notbegeni.setId(View.generateViewId());
                            notbegeni.setText("Dislike: " + str_dislike);

                            ImageView imageView = new ImageView(getContext());
                            imageView.setId(View.generateViewId());
                            String photoUrl = "https://firebasestorage.googleapis.com/v0/b/mobilfinalproje-8f4c4.appspot.com/o/" + photoName + ".jpg?alt=media";
                            Picasso.get().load(photoUrl).into(imageView);
                            int width = 700;
                            int height = 700;

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                            imageView.setLayoutParams(layoutParams);

                            constraintLayout.addView(imageView);
                            constraintLayout.addView(textName);
                            constraintLayout.addView(textlabels);
                            constraintLayout.addView(begeni);
                            constraintLayout.addView(notbegeni );

                            galeriLayout.addView(constraintLayout);

                            ConstraintSet constraintSet =new ConstraintSet();
                            constraintSet.clone(constraintLayout);

                            constraintSet.connect(imageView.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
                            constraintSet.connect(imageView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
                            constraintSet.connect(imageView.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);

                            constraintSet.connect(textName.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.TOP);
                            constraintSet.connect(textName.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);
                            constraintSet.setMargin(textName.getId(), ConstraintSet.TOP, 200);

                            constraintSet.connect(textlabels.getId(), ConstraintSet.TOP, textName.getId(), ConstraintSet.BOTTOM);
                            constraintSet.connect(textlabels.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);

                            constraintSet.connect(begeni.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.BOTTOM);
                            constraintSet.connect(begeni.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
                            constraintSet.setMargin(begeni.getId(), ConstraintSet.START, 500);

                            constraintSet.connect(notbegeni.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.BOTTOM);
                            constraintSet.connect(notbegeni.getId(), ConstraintSet.START, begeni.getId(), ConstraintSet.END);

                            constraintSet.applyTo(constraintLayout);

                            Space space = new Space(getContext());
                            space.setLayoutParams( new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, 20));
                            galeriLayout.addView(space);

                            int finalLike = like;
                            begeni.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int inalLike = finalLike + 1;
                                    DocumentReference docRef = db.collection("galeri").document(ID);
                                    docRef.update("like", String.valueOf(inalLike))
                                            .addOnSuccessListener(aVoid -> Log.d("firestore ", "begeni!!!"))
                                            .addOnFailureListener(e -> Log.e("direstore", "!!!begeni", e));
                                    yenile();
                                }
                            });

                            int finaldislike = dislike;
                            notbegeni.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int inalDislike = finaldislike + 1;
                                    DocumentReference docRef = db.collection("galeri").document(ID);
                                    docRef.update("dislike", String.valueOf(inalDislike))
                                            .addOnSuccessListener(aVoid -> Log.d("firestore ", "1. nbegeni!!!"))
                                            .addOnFailureListener(e -> Log.e("direstore", "!!!nbegeni", e));
                                    yenile();
                                }
                            });
                        }
                    }else{
                        Log.e("Firestore", "VERILER CEKILEMEDİ", task.getException());
                    }
                });
    }
    private void yenile(){
        galeriLayout.removeAllViews();
        galeriyiGoster();
    }

    private void spinView(){
        db.collection("galeri")
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Set<String> uniqueLabelsSet = new HashSet<>();
                        uniqueLabelsSet.add("hepsi");
                        for (QueryDocumentSnapshot document : task.getResult()){
                            Object labelObject = document.get("labels");
                            if (labelObject instanceof List<?>){
                                List<?> labelList = (List<?>) labelObject;

                                for (Object item : labelList){
                                    if(item instanceof String){
                                        String label = (String) item;
                                        uniqueLabelsSet.add(label);
                                    }
                                    else{
                                        //Toast.makeText(getContext(), "Firestore labels içi string değil!" + item, Toast.LENGTH_SHORT).show();
                                        Log.e("Firestore ", "labels içi string değil!");
                                    }
                                }
                            }
                            else{
                                Log.e("firestore", "2. Labels liste değil" + labelObject);
                                continue;
                            }
                        }

                        String[] uniqueLabelsArray = uniqueLabelsSet.toArray(new String[0]);

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,uniqueLabelsArray);
                        //dizimizi spinnera bağladık, dizimizin dropdown olduğunu da söyledik
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spin = root.findViewById(R.id.spinner);
                        spin.setAdapter(adapter);
                        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                secilenLabel = (String) parent.getItemAtPosition(position);
                                if ("hepsi".equals(secilenLabel)){
                                     galeriLayout.removeAllViews();
                                     galeriyiGoster();
                                }
                                galeriLayout.removeAllViews();
                                db.collection("galeri")
                                        .whereArrayContains("labels", secilenLabel)
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()){
                                                for (QueryDocumentSnapshot document : task.getResult()){
                                                    String user = document.getString("name");
                                                    String photoName = document.getString("foto");

                                                    str_like =document.getString("like");
                                                    int like = (str_like != null && !str_like.isEmpty()) ? Integer.parseInt(str_like) : 0;

                                                    str_dislike = document.getString("dislike");
                                                    int dislike = (str_dislike != null && !str_dislike.isEmpty()) ? Integer.parseInt(str_dislike) : 0;

                                                    String ID = document.getId();

                                                    String Id = document.getId();
                                                    Object objectLabel = document.get("labels");
                                                    if (objectLabel instanceof List<?>){
                                                        List<?> labelList = (List<?>) objectLabel;

                                                        for (Object item : labelList){
                                                            if(item instanceof String){
                                                                String label = (String) item;
                                                                labelsList.add(label);
                                                            }
                                                            else{
                                                               // Toast.makeText(getContext(), "Firestore labels içi string değil!" + item, Toast.LENGTH_SHORT).show();
                                                                Log.e("Firestore ", "labels içi string değil!");
                                                            }
                                                        }
                                                    }
                                                    else{
                                                        Log.e("firestore", "3. Labels liste değil" + objectLabel);
                                                        continue;
                                                    }

                                                    ConstraintLayout constraintLayout = new ConstraintLayout(getContext());
                                                    //ConstraintLayout constraintLayout = new ConstraintLayout(getContext());

                                                    TextView textName = new TextView(getContext());
                                                    textName.setId(View.generateViewId());
                                                    textName.setText(user);

                                                    TextView textlabels = new TextView(getContext());
                                                    textlabels.setId(View.generateViewId());
                                                    textlabels.setText("Labels: \n" + labelsList );
                                                    labelsList.clear();

                                                    Button begeni = new Button(getContext());
                                                    begeni.setId(View.generateViewId());
                                                    begeni.setText("Like: " + str_like);

                                                    Button notbegeni = new Button(getContext());
                                                    notbegeni.setId(View.generateViewId());
                                                    notbegeni.setText("Dislike: " + str_dislike);

                                                    ImageView imageView = new ImageView(getContext());
                                                    imageView.setId(View.generateViewId());
                                                    String photoUrl = "https://firebasestorage.googleapis.com/v0/b/mobilfinalproje-8f4c4.appspot.com/o/" + photoName + ".jpg?alt=media";
                                                    Picasso.get().load(photoUrl).into(imageView);
                                                    int width = 700;
                                                    int height = 700;

                                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                                                    imageView.setLayoutParams(layoutParams);

                                                    constraintLayout.addView(imageView);
                                                    constraintLayout.addView(textName);
                                                    constraintLayout.addView(textlabels);
                                                    constraintLayout.addView(begeni);
                                                    constraintLayout.addView(notbegeni );

                                                    galeriLayout.addView(constraintLayout);

                                                    ConstraintSet constraintSet =new ConstraintSet();
                                                    constraintSet.clone(constraintLayout);

                                                    constraintSet.connect(imageView.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
                                                    constraintSet.connect(imageView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
                                                    constraintSet.connect(imageView.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);

                                                    constraintSet.connect(textName.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.TOP);
                                                    constraintSet.connect(textName.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);
                                                    constraintSet.setMargin(textName.getId(), ConstraintSet.TOP, 200);

                                                    constraintSet.connect(textlabels.getId(), ConstraintSet.TOP, textName.getId(), ConstraintSet.BOTTOM);
                                                    constraintSet.connect(textlabels.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);

                                                    constraintSet.connect(begeni.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.BOTTOM);
                                                    constraintSet.connect(begeni.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
                                                    constraintSet.setMargin(begeni.getId(), ConstraintSet.START, 500);

                                                    constraintSet.connect(notbegeni.getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.BOTTOM);
                                                    constraintSet.connect(notbegeni.getId(), ConstraintSet.START, begeni.getId(), ConstraintSet.END);

                                                    constraintSet.applyTo(constraintLayout);

                                                    Space space = new Space(getContext());
                                                    space.setLayoutParams( new LinearLayout.LayoutParams(
                                                            ViewGroup.LayoutParams.MATCH_PARENT, 20));
                                                    galeriLayout.addView(space);

                                                    int finalLike = like;
                                                    begeni.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            int inalLike = finalLike + 1;
                                                            DocumentReference docRef = db.collection("galeri").document(ID);
                                                            docRef.update("like", String.valueOf(inalLike))
                                                                    .addOnSuccessListener(aVoid -> Log.d("firestore ", "like!!!"))
                                                                    .addOnFailureListener(e -> Log.e("direstore", "!!!like", e));
                                                            yenile();
                                                        }
                                                    });

                                                    int finaldislike = dislike;
                                                    notbegeni.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            int inalDislike = finaldislike + 1;
                                                            DocumentReference docRef = db.collection("galeri").document(ID);
                                                            docRef.update("dislike", String.valueOf(inalDislike))
                                                                    .addOnSuccessListener(aVoid -> Log.d("firestore ", "2. addOnSuccess dislike!!!"))
                                                                    .addOnFailureListener(e -> Log.e("direstore", "!!!2. addOnFailure dislike", e));
                                                            yenile();
                                                        }
                                                    });
                                                }
                                            }else{
                                                Log.e("Firestore", "VERILER CEKILEMEDİ", task.getException());
                                            }
                                        });
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }else{
                        Log.e("Firebase Hatasi", "belge yok bulunamadi");
                    }
                });
    }
}