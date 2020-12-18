package com.shahali.mnote.service;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shahali.mnote.R;
import com.shahali.mnote.adapter.NoteAdapter;
import com.shahali.mnote.model.Note;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NoteAdapter.OnRecyclerItemClick, NoteAdapter.OnRecyclerItemClickDelete {
    private FloatingActionButton addNote;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> mNote;
    private FirebaseAuth mAuth;
    private List<String> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recycleNote);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mNote = new ArrayList<>();

        noteAdapter = new NoteAdapter(this, mNote);
        recyclerView.setAdapter(noteAdapter);

        noteList = new ArrayList<>();
        checkPublisher();



        addNote = findViewById(R.id.addNote);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CreateNoteActivity.class));
            }
        });

    }

    @Override
    public void onClick(Note note) {
        Intent intentUpdateNote = new Intent(this, UpdateActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intentUpdateNote.putExtra("title", note.getTitle());
        intentUpdateNote.putExtra("note", note.getNote());
        intentUpdateNote.putExtra("creationDate", note.getCreation_date().toString());
        startActivity(intentUpdateNote);
    }

    @Override
    public void onClickDelete(Note note, int pos) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query noteQuery = ref.child("Notes").orderByChild("publisher").equalTo(note.getPublisher());


        noteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mNote.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
                readNote();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NoteAdapter", "onCancelledDeleteNote", databaseError.toException());
            }
        });
    }

    private void readNote() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Note");
        mNote.clear();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Note note = dataSnapshot.getValue(Note.class);
                    for (String publisher : noteList) {
                        if (note.getPublisher().equals(mAuth.getCurrentUser().getUid())) {
                            mNote.add(note);
                        }
                    }
                }
                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkPublisher() {
        noteList.clear();
        FirebaseDatabase.getInstance().getReference().child("Note").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noteList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    noteList.add(snapshot.getKey());
                }
                readNote();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
