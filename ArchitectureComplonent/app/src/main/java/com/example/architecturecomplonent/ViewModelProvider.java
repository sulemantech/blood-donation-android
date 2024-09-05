package com.example.architecturecomplonent;

import com.example.architecturecomplonent.database.NoteDatabase;
import com.example.architecturecomplonent.viewModel.NoteViewModel;

public class ViewModelProvider {
    public static NoteDatabase AndroidViewModelFactory;

    public ViewModelProvider(MainActivity mainActivity, Object instance) {
    }

    public NoteViewModel get(Class<NoteViewModel> noteViewModelClass) {
        return null;
    }
}
