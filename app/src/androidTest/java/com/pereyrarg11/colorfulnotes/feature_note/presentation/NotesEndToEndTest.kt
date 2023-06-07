package com.pereyrarg11.colorfulnotes.feature_note.presentation

import androidx.activity.compose.setContent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pereyrarg11.colorfulnotes.MainActivity
import com.pereyrarg11.colorfulnotes.core.util.TestTags
import com.pereyrarg11.colorfulnotes.di.AppModule
import com.pereyrarg11.colorfulnotes.feature_note.presentation.add_edit_note.AddEditNoteScreen
import com.pereyrarg11.colorfulnotes.feature_note.presentation.notes.NotesScreen
import com.pereyrarg11.colorfulnotes.feature_note.presentation.util.Screen
import com.pereyrarg11.colorfulnotes.ui.theme.ColorfulNotesTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class NotesEndToEndTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.activity.setContent {
            val navController = rememberNavController()
            ColorfulNotesTheme {
                NavHost(
                    navController = navController,
                    startDestination = Screen.NotesScreen.route
                ) {
                    composable(route = Screen.NotesScreen.route) {
                        NotesScreen(navController = navController)
                    }
                    composable(
                        route = Screen.AddEditNoteScreen.route
                                + "?noteId={noteId}&noteColor={noteColor}",
                        arguments = listOf(
                            navArgument(name = "noteId") {
                                type = NavType.IntType
                                defaultValue = -1
                            },
                            navArgument(name = "noteColor") {
                                type = NavType.IntType
                                defaultValue = -1
                            },
                        )
                    ) {
                        val color = it.arguments?.getInt("noteColor") ?: -1
                        AddEditNoteScreen(
                            navController = navController,
                            noteColor = color
                        )
                    }
                }
            }
        }
    }

    @Test
    fun saveNewNote_editAfterwards() {
        val testTitle = "test-title"
        val testContent = "test-content"
        // click on FAB to get to add note screen
        composeRule.onNodeWithContentDescription("Add").performClick()
        // enter texts in title and content text fields
        composeRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextInput(testTitle)
        composeRule.onNodeWithTag(TestTags.CONTENT_TEXT_FIELD).performTextInput(testContent)
        // save the new note
        composeRule.onNodeWithContentDescription("Save").performClick()
        // make sure there is a note in the list with our title and content
        composeRule.onNodeWithText(testTitle).assertIsDisplayed()
        // click on note to edit it
        composeRule.onNodeWithText(testTitle).performClick()
        // make sure title and content text fields contain note title and content
        composeRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).assertTextEquals(testTitle)
        composeRule.onNodeWithTag(TestTags.CONTENT_TEXT_FIELD).assertTextEquals(testContent)
        // add the text "2" to the title text field
        composeRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextClearance()
        composeRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextInput("${testTitle}2")
        // update the note
        composeRule.onNodeWithContentDescription("Save").performClick()
        // make sure the update was applied to the list
        composeRule.onNodeWithText("${testTitle}2").assertIsDisplayed()
    }

    @Test
    fun saveNewNotes_orderByTitleDescending() {
        for (i in 1..3) {
            // click on FAB to get to add note screen
            composeRule.onNodeWithContentDescription("Add").performClick()
            // enter texts in title and content text fields
            composeRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextInput(i.toString())
            composeRule.onNodeWithTag(TestTags.CONTENT_TEXT_FIELD).performTextInput(i.toString())
            // save the new note
            composeRule.onNodeWithContentDescription("Save").performClick()
        }

        composeRule.onNodeWithText("1").assertIsDisplayed()
        composeRule.onNodeWithText("2").assertIsDisplayed()
        composeRule.onNodeWithText("3").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Sort").performClick()
        composeRule.onNodeWithContentDescription("Title").performClick()
        composeRule.onNodeWithContentDescription("Descending").performClick()

        composeRule.onAllNodesWithTag(TestTags.NOTE_ITEM)[0].assertTextContains("3")
        composeRule.onAllNodesWithTag(TestTags.NOTE_ITEM)[1].assertTextContains("2")
        composeRule.onAllNodesWithTag(TestTags.NOTE_ITEM)[2].assertTextContains("1")
    }
}