package com.example.architecturecomplonent.viewModel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.architecturecomplonent.NoteRepository;
import com.example.architecturecomplonent.entity.Note;

import java.util.List;

public class NoteViewModel {

    private NoteRepository repository;
    private LiveData<List<Note>> allNotes;

    public NoteViewModel(@NonNull Application application) {
        super();

        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
    }

    //communicate with rep
    public void insert(Note note) {
        repository.insert(note);
    }
    public void update(Note note) {
        repository.update(note);
    }
    public void delete(Note note) {
        repository.delete(note);
    }
    public void deleteAllNotes() {
        repository.deleteAllNotes();
    }
    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

}
