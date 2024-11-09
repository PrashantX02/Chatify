package com.example.chatify.view.contact;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chatify.R;
import com.example.chatify.adapter.ContactAdapter;
import com.example.chatify.databinding.ActivityAddContactBinding;
import com.example.chatify.model.user.Users;
import com.example.chatify.view.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddContact extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int PERMISSIONS_REQUEST_WRITE_CONTACTS = 101;

    private static final String TAG = "AddContact";

    private ActivityAddContactBinding binding;
    private List<Users> list = new ArrayList<>();
    private ContactAdapter adapter;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    private Toolbar toolbar;
    private ImageView imageView;
    private TextView txtView, txtView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the correct layout and set the content view
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_contact);

        // Handle edge-to-edge display insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        if (firebaseUser != null) {
            getContactList();
        }

        // Request permissions for reading and writing contacts
        requestContactPermissions();

        // Initialize imageView and TextViews with the binding object
        imageView = binding.backButton;
        txtView = binding.textView;
        txtView2 = binding.textView2;

        toolbar = binding.toolbar3;
        setSupportActionBar(toolbar);

        // Set up click listeners for buttons
        txtView2.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), NewGroup.class);
            startActivity(intent);
            finish();
        });

        txtView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), NewContact.class);
            startActivity(intent);
        });

        // Set up a click listener for the back button
        imageView.setOnClickListener(v -> navigateBackToMainActivity());
    }

    private void getContactList() {
        firestore.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // Loop through the queryDocumentSnapshots
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    // Log each snapshot data
                    String userID = snapshot.getString("userID");
                    String userName = snapshot.getString("username");
                    String imageUrl = snapshot.getString("imageProfile");
                    String userBio = snapshot.getString("bio");

                    Users user = new Users();
                    user.setUserID(userID);
                    user.setBio(userBio);
                    user.setUsername(userName);
                    user.setImageProfile(imageUrl);

                    if (userID != null && !userID.equals(firebaseUser.getUid())) {
                        list.add(user);
                    }
                }
                adapter = new ContactAdapter(list, AddContact.this);
                binding.contactsRecyclerView.setAdapter(adapter);
            }
        });
    }

    private void requestContactPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CONTACTS},
                    PERMISSIONS_REQUEST_WRITE_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted for reading contacts
                } else {
                    // Permission denied, you might want to show a message to the user
                }
                return;
            }
            case PERMISSIONS_REQUEST_WRITE_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted for writing contacts
                } else {
                    // Permission denied, you might want to show a message to the user
                }
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateBackToMainActivity();
    }

    private void navigateBackToMainActivity() {
        Intent intent = new Intent(AddContact.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu resource file
        getMenuInflater().inflate(R.menu.contact_menu, menu);

        // Find the search item
        MenuItem searchItem = menu.findItem(R.id.action_search2);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();
        // Set up the search bar behavior
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle text change
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.contact) {
            Log.d("AddContact", "Contact item clicked");
            Intent intent = new Intent(getApplicationContext(), NewContact.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}