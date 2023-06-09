package com.pereyrarg11.colorfulnotes.di

import android.app.Application
import androidx.room.Room
import com.pereyrarg11.colorfulnotes.feature_note.data.data_source.NoteDataBase
import com.pereyrarg11.colorfulnotes.feature_note.data.repository.NoteRepositoryImpl
import com.pereyrarg11.colorfulnotes.feature_note.domain.repository.NoteRepository
import com.pereyrarg11.colorfulnotes.feature_note.domain.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application): NoteDataBase {
        return Room
            .databaseBuilder(app, NoteDataBase::class.java, NoteDataBase.DATABASE_NAME)
            .build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(dataBase: NoteDataBase): NoteRepository {
        return NoteRepositoryImpl(dataBase.noteDao)
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotesUseCase(repository),
            deleteNote = DeleteNoteUseCase(repository),
            addNote = AddNoteUseCase(repository),
            getNote = GetNoteUseCase(repository),
        )
    }
}