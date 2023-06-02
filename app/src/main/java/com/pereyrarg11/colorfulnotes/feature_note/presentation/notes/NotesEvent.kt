package com.pereyrarg11.colorfulnotes.feature_note.presentation.notes

import com.pereyrarg11.colorfulnotes.feature_note.domain.model.Note
import com.pereyrarg11.colorfulnotes.feature_note.domain.util.NoteOrder

/**
 * Defines the UI events triggered by the user.
 */
sealed class NotesEvent {
    data class Order(val noteOrder: NoteOrder) : NotesEvent()
    data class DeleteNote(val note: Note) : NotesEvent()
    object RestoreNote : NotesEvent()
    object ToggleOrderSection : NotesEvent()
}
