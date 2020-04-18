package com.example.recyclerviewinfirebase;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore objectFirebaseFirestore;

    private StatusAdapterClass objectStatusAdapterClass;
    private EditText nameET,statusET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectXML();
    }

    private void connectXML()
    {
        try
        {
            nameET=findViewById(R.id.usernameET);
            statusET=findViewById(R.id.StatusET);

            objectFirebaseFirestore=FirebaseFirestore.getInstance();
            recyclerView=findViewById(R.id.RV);

            getValuesFromDBandAttachToRV();
        }
        catch (Exception e)
        {
            Toast.makeText(this, "ConnectXML:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getValuesFromDBandAttachToRV()
    {
        try
        {
            Query objectQuery=objectFirebaseFirestore.collection("UserStatus");

            FirestoreRecyclerOptions<ModelClass> options;
            options=new FirestoreRecyclerOptions.Builder<ModelClass>().setQuery(objectQuery, StatusModelClass.class)
                    .build();

            objectStatusAdapterClass=new StatusAdapterClass(options);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            recyclerView.setAdapter(objectStatusAdapterClass);

        }
        catch (Exception e)
        {
            Toast.makeText(this, "getValuesFromDBandAttachToRV:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        objectStatusAdapterClass.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        objectStatusAdapterClass.stopListening();
    }

    public void addStatus(View view)
    {
        try
        {
            if(!nameET.getText().toString().isEmpty() && !statusET.getText().toString().isEmpty())
            {
                Map<String,String> objectMap=new HashMap<>();
                objectMap.put("status",statusET.getText().toString());
                objectFirebaseFirestore.collection("UserStatus")
                        .document(nameET.getText().toString())
                        .set(objectMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                nameET.setText("");
                                statusET.setText("");

                                nameET.requestFocus();
                                Toast.makeText(MainActivity.this, "Status Updated", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Fails to update status", Toast.LENGTH_SHORT).show();
                            }
                        });

            }

        }
        catch (Exception e)
        {
            Toast.makeText(this, "addStatus:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}