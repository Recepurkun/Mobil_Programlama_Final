package com.example.myapplication.ui;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddPhotoFragment extends Fragment {
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    String  girisYapan, girisYapanName;
    Button btn_camera, btn_photosave;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ImageView image;
    byte[] fotodata;
    ArrayList<String> secilenEtiketler = new ArrayList<>();
    private View root;

    LinearLayout photoLinner;
    //Image image

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_addphoto, container, false);

        btn_camera = root.findViewById(R.id.camera_btn);
        btn_photosave = root.findViewById(R.id.photosave_btn);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        //Degistirebilirsin
        image = root.findViewById(R.id.imageView5);


        Intent intent = getActivity().getIntent();
        girisYapan = intent.getStringExtra("email");
        //girisYapanName = intent.getStringExtra("firstname");
        fotoLabelleriGoster(girisYapan);
        if (girisYapan != null) {
            // userEmail değeri null değilse, burada kullanabilirsiniz
            Toast.makeText(getContext(), "Email: " + girisYapan, Toast.LENGTH_SHORT).show();
            //Toast.makeText(getContext(), "Username: " + girisYapanName, Toast.LENGTH_SHORT).show();
        } else {
            // userEmail null ise, bir hata durumu olabilir
            Toast.makeText(getContext(), "Email bilgisi alınamadı" + girisYapan, Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "userName bilgisi alınamadı" + girisYapanName, Toast.LENGTH_SHORT).show();
        }

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kamera izin kontrolü
                if (checkCameraPermission()) {
                    takeAphoto();
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                }
            }
        });

        btn_photosave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String randomId = generatorRndId(20);
                StorageReference galeriRef = storageRef.child(randomId + ".jpg");
                UploadTask uploadTask = galeriRef.putBytes(fotodata);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(getActivity(), "FOTO YUKLEME BASARISIZ OLDU", Toast.LENGTH_LONG).show();
                        Log.e("UPLOAD_ERROR", "Fotoğraf yükleme başarısız oldu.", e);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        galeriRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //String DownloadUrl = uri.toString();
                                //Toast.makeText(getActivity(), "FOTO YUKLEME BASARILI OLDU", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

                Map<String, Object> galeriData = new HashMap<>();
                galeriData.put("labels", secilenEtiketler);
                galeriData.put("email", girisYapan);
                galeriData.put("like", "0");
                galeriData.put("dislike", "0");
                galeriData.put("foto", randomId);
                galeriData.put("name", girisYapanName);

                CollectionReference galeriCollectionRef = db.collection("galeri");

                galeriCollectionRef.add(galeriData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getActivity(), "Galeri Eklendi", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Galeri Yüklenemedi", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return root;
    }

    private void takeAphoto(){
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicIntent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(takePicIntent, 12);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 12 && resultCode == RESULT_OK){
            // Verilerin extras'ından alınan çekilen resmi bir Bitmap olarak al
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            // ImageView'e çekilen resmi atama
            image.setImageBitmap(imageBitmap);

            image.setDrawingCacheEnabled(true);
            image.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            fotodata = baos.toByteArray();
         }
    }

    private void fotoLabelleriGoster(String girisYapan){
        this.girisYapan = girisYapan;
        photoLinner = root.findViewById(R.id.addphotolabel_view);
        photoLinner.removeAllViews();

        db.collection("labels")
                .whereEqualTo("email", this.girisYapan)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String labelName = document.getString("label");
                            String labelDesc = document.getString("description");

                            ConstraintLayout labelLayout = new ConstraintLayout(getContext());

                            TextView textView = new TextView(getContext());
                            textView.setId(View.generateViewId());
                            textView.setText("Label: " + labelName + "\nDescription: " + labelDesc);

                            CheckBox checkBox = new CheckBox(getContext());
                            checkBox.setId(View.generateViewId());
                            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if(isChecked){
                                        secilenEtiketler.add(labelName);
                                    }
                                }
                            });

                            labelLayout.addView(textView);
                            labelLayout.addView(checkBox);
                            photoLinner.addView(labelLayout);

                            ConstraintSet constraintSet = new ConstraintSet();
                            constraintSet.clone(labelLayout);

                            constraintSet.connect(textView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                            constraintSet.connect(textView.getId(), ConstraintSet.START, checkBox.getId(), ConstraintSet.END);

                            constraintSet.connect(checkBox.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                            constraintSet.connect(checkBox.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);

                            constraintSet.applyTo(labelLayout);
                        }
                    } else {
                        //Toast.makeText(getContext(), "Firestore'dan veri çekilemedi: " + task.getException(), Toast.LENGTH_LONG).show();
                        Log.e("FIRESTORE", "Veri çekilemedi", task.getException());
                    }
                });
    }

    /*
    * Metodun çalışma mantığı şu adımları içerir:
    length parametresine göre belirtilen uzunlukta bir döngü oluşturulur.
    Döngü içinde, karakter dizisinin uzunluğu kullanılarak bir rastgele indeks belirlenir.
    Belirlenen indeksteki karakter, rndID üzerine eklenir.
    Döngü tamamlandığında, oluşturulan rastgele karakter dizisi (rndID) bir stringe dönüştürülerek geri döndürülür.
    */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            // Kamera izin sonuçları kontrolü
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takeAphoto();
            } else {
                Toast.makeText(getActivity(), "Kamera izni verilmedi.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String generatorRndId(int length){
        String karakter = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz012345678";
        StringBuilder rndID = new StringBuilder();
        Random rnd = new Random();
        for (int i=0; i<length;i++){
            int index = rnd.nextInt(karakter.length());
            rndID.append(karakter.charAt(index));
        }
        return rndID.toString();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

}