# Linguaphile

**Linguaphile** is an Android application designed for English learners to enhance their vocabulary skills. It includes features for managing vocabulary items, filtering, practicing through mini-games, and more. The app leverages modern Android architecture components such as Room, DAO, LiveData, ViewModel, and multi-fragment navigation.

## Features

- **Vocabulary Management**: Add, view, update, and delete vocabulary items.
- **Filtering Options**: Filter vocabulary items by date or type.
- **Mini Game**: Engage in multiple-choice quizzes to test vocabulary knowledge.
- **Dynamic Vocabulary Editing**: Add multiple meanings and synonyms dynamically in both Add and Update Vocabulary fragments.
- **Multi-Fragment Navigation**: Navigate between different pages, including "Home", "Add New Vocabulary", "Mini Game", and "My Profile".
- **Customized Toolbar**: Each fragment has a customized toolbar displaying the fragment name and a navigation button.

## Vocabulary Entity

The **Vocabulary** entity includes the following attributes:

1. **Id** (Integer, non-nullable): Auto-incremented ID for each vocabulary item.
2. **Name** (String, non-nullable): The vocabulary term.
3. **Type** (String, non-nullable): The type of the vocabulary (e.g., Noun, Verb, Adjective, Adverb).
4. **Date** (Date, non-nullable): The date the vocabulary item was added, stored in datetime format.
5. **Meaning1** (String, non-nullable): The primary meaning of the vocabulary item.
6. **Meaning2** (String, nullable): An optional secondary meaning.
7. **Meaning3** (String, nullable): An optional third meaning.
8. **Meaning4** (String, nullable): An optional fourth meaning.
9. **Synonym1** (String, nullable): An optional first synonym.
10. **Synonym2** (String, nullable): An optional second synonym.
11. **Synonym3** (String, nullable): An optional third synonym.
12. **Synonym4** (String, nullable): An optional fourth synonym.

## App Architecture

The app utilizes Android architecture components and a multi-fragment structure:

- **Room**: Local database management for data persistence.
- **DAO**: Data access objects to interact with the database.
- **LiveData**: Observes and updates UI based on data changes.
- **ViewModel**: Manages UI-related data in a lifecycle-conscious way.
- **RecyclerView**: Displays vocabulary items in a scrollable list.
- **Toolbar**: Customized per fragment for easy navigation and context.

## Fragments

### 1. Home Fragment

The **Home** fragment displays a list of vocabulary items stored in Room. It includes:

- **Search Functionality**: Filters vocabulary items based on the search query entered.
- **Filter by Date**: Dropdown options for filtering by when the item was added (Today, This Week, This Month, This Year).
- **Filter by Type**: Dropdown options for filtering by vocabulary type (Noun, Verb, Adjective, Adverb).

Each vocabulary item displayed in the list includes:
- **Name** and **Type** of the vocabulary.
- A comma-separated list of meanings (if multiple).
- A comma-separated list of synonyms (if multiple).

Each item also has a 3-dot button, providing:
- **Update**: Opens the "Update Vocabulary" fragment.
- **Delete**: Deletes the vocabulary item with an **UNDO** option in a Snackbar.

### 2. Add New Vocabulary Fragment

The **Add New Vocabulary** fragment allows users to add new vocabulary items dynamically. Features include:

- **Vocabulary Name**: EditText for the vocabulary term.
- **Type**: Dropdown for the type of vocabulary (Noun, Verb, Adjective, Adverb).
- **Meaning**:
  - A primary, mandatory EditText for the main meaning (Meaning1).
  - An "Add Meaning" button that adds up to three additional meanings. A Toast message displays if the user tries to add more than four meanings.
- **Synonym**:
  - An "Add Synonym" button that adds up to four synonyms. A Toast message displays if the user tries to add more than four synonyms.

The **Add this Vocabulary** button saves the vocabulary item to the database, storing the current Date and all entered information.

### 3. Update Vocabulary Fragment

The **Update Vocabulary** fragment allows users to modify an existing vocabulary item. It includes features for:

- **Editing the Vocabulary Term and Type**: Pre-populated fields with the current data.
- **Adding/Removing Meanings and Synonyms Dynamically**:
  - An "Add Meaning" button allows users to add up to three additional meanings.
  - An "Add Synonym" button allows users to add up to four synonyms.

Each additional meaning or synonym field added includes a delete button, allowing the user to remove it. Any changes can be saved with the **Update Vocabulary** button.

### 4. Mini Game Fragment

The **Mini Game** fragment provides a quiz feature to help users practice vocabulary knowledge. Users can select between two modes:

#### Modes:
1. **Meaning Testing**: Users are given a vocabulary word, and they must select the correct meaning from four options.
2. **Synonym Testing**: Users are given a vocabulary word, and they must select the correct synonym from four options.

#### Quiz Format:
- Each question is dynamically generated with one correct answer and three random incorrect answers.
- **Score Tracking**: The app tracks correct answers, showing the final score when the test completes.
- **Retry Option**: After completing the quiz, users are shown a dialog with options to try again or quit to the initial mode selection screen.
  
### 5. Profile Fragment

Currently a placeholder for future updates, the **My Profile** page will track user progress and preferences.

## Getting Started

To run this app locally:

1. Clone the repository.
2. Open the project in Android Studio.
3. Sync the project to download necessary dependencies.
4. Run the app on an emulator or a physical device.

## Future Updates

- **User Progress Tracking**: Track vocabulary knowledge over time.
- **Expanded Mini Games**: More game types for practicing vocabulary.
- **Profile Page**: Features for user customization and progress tracking.

## Dependencies

- Room: Local database storage.
- RecyclerView: Displays vocabulary items in a list.
- ViewModel & LiveData: Lifecycle-aware data management.
- Navigation Components: For multi-fragment navigation.
- Material Components: UI elements like Snackbar and toolbar.

---

This documentation reflects the latest updates and improvements to **Linguaphile** as of 6th November 2024.
