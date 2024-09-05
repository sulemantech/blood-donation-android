package com.example.architecturecomplonent.database;


import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.architecturecomplonent.entity.Note;

@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase instance; //we are creating this variable bcz we
    // have to turn this class into sekelton (means we cant create multiple instances of this db) so we use
    //this variable only

    public abstract NoteDao noteDao();

    //synchronized means only one thread can access this method,
// so this way you can't accientdly create 2 instance of this db when 2 thraed trys to access this method at the same time
    public static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null) {//we only want to initite this db if we dont already have an instance
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()//this method will use if db is already exist it will delete
                    //database and old table and create a new one and our app will not crash
                    .addCallback(roomCallback).build();
        }
        return instance;
    }

    //to enter dummy data in the db
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;
        private PopulateDbAsyncTask(NoteDatabase db) {
            noteDao = db.noteDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("Title 1", "Description 1", 1));
            noteDao.insert(new Note("Title 2", "Description 2", 2));
            noteDao.insert(new Note("Title 3", "Description 3", 3));
            return null;
        }
    }


}
