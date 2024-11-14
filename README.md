Here's the updated `README.md` in the proper format:

---

# Linguaphile

**Linguaphile** is an Android application designed for English learners to enhance their vocabulary skills. It includes features for managing vocabulary items, filtering, practicing through mini-games, tracking achievements, and managing user profiles. The app leverages modern Android architecture components such as Room, DAO, LiveData, ViewModel, and multi-fragment navigation.

## Features

- **Vocabulary Management**: Add, view, update, and delete vocabulary items.
- **Filtering Options**: Filter vocabulary items by date or type.
- **Mini Game**: Engage in multiple-choice quizzes to test vocabulary knowledge.
- **Achievements Tracking**: View progress and unlock achievements based on vocabulary and quiz milestones.
- **Profile Management**: Update profile details, including setting a profile picture from unlocked avatars.
- **Dynamic Vocabulary Editing**: Add multiple meanings and synonyms dynamically in both Add and Update Vocabulary fragments.
- **Multi-Fragment Navigation**: Navigate between different pages, including "Home", "Add New Vocabulary", "Mini Game", "Achievements", and "Profile".
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
13. **Note** (String, nullable): An optional notation for the vocabulary.

## App Architecture

The app utilizes Android architecture components and a multi-fragment structure:

- **Room**: Local database management for data persistence.
- **DAO**: Data access objects to interact with the database.
- **LiveData**: Observes and updates UI based on data changes.
- **ViewModel**: Manages UI-related data in a lifecycle-conscious way.
- **RecyclerView**: Displays vocabulary items and achievements in a scrollable list.
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
- **Note**:
  - An "Add Note" button that adding notation. A Toast message displays to restrict only 1 notation per any vocabulary item.

The **Add this Vocabulary** button saves the vocabulary item to the database, storing the current Date and all entered information.

### 3. Update Vocabulary Fragment

The **Update Vocabulary** fragment allows users to modify an existing vocabulary item. It includes features for:

- **Editing the Vocabulary Term and Type**: Pre-populated fields with the current data.
- **Adding/Removing Meanings, Synonyms, and Note Dynamically**:
  - An "Add Meaning" button allows users to add up to three additional meanings.
  - An "Add Synonym" button allows users to add up to four synonyms.
  - An "Add Note" button allows users to add 1 notation.

Each additional meaning, synonym, or notation fields added includes a delete button, allowing the user to remove it. Any changes can be saved with the **Update Vocabulary** button.

### 4. Mini Game Fragment

The **Mini Game** fragment provides a quiz feature to help users practice vocabulary knowledge. Users can select between two modes:

#### Modes:
1. **Meaning Testing**: Users are given a vocabulary word, and they must select the correct meaning from four options (incorrect options are taken from other vocabulary items).
2. **Synonym Testing**: Users are given a vocabulary word, and they must select the correct synonym from four options (incorrect options are taken from other vocabulary items)..

#### Quiz Format:
- Each question is dynamically generated with one correct answer and three random incorrect answers.
- **Score Tracking**: The app tracks correct answers, showing the final score when the test completes.
- **Retry Option**: After completing the quiz, users are shown a dialog with options to try again or quit to the initial mode selection screen.

### 5. My Profile Fragment

The **My Profile** fragment allows users to manage their profile information, including:

- **Name and Email Update**: Users can update their profile name and email.
- **Profile Picture Selection**: Users can choose an avatar image from unlocked cartoon animal figures, achieved through completing certain tasks (achievements) in the app.

### 6. My Achievement Fragment

The **My Achievement** fragment tracks the user's progress by displaying various achievement tasks in a RecyclerView. Each achievement item includes:

- **Achievement Name and Description**: Information about the specific achievement.
- **Avatar Image (if unlocked)**: Displays an avatar that can be used as a profile picture once unlocked. Some achievements do not have associated images and will remain blank if not applicable. Avatar images are set with a frame and background color based on their class also.
- **Status**: An indicator image showing whether the achievement is complete (checked or unchecked).
- **Progress Threshold**: Shows current progress over the required threshold for each achievement (e.g., `50/100`).

Achievements are categorized into various types, such as vocabulary milestones (e.g., adding a certain number of nouns, verbs, etc.) and mini-game achievements, day logins. Users can track their progress visually and see which achievements are unlocked as they progress on My Achievement page.

## New Implementations
(As per last update on 6th November 2024)  
### Room Setup for New Entities

- **User Entity**: Includes user information (name, email, and avatar selection). DAO, database, repository, and ViewModel were implemented to manage user data.
- **Achievement Entity**: Manages achievements, with corresponding DAO, database, repository, and ViewModel to track progress across vocabulary and mini-games.
- **MiniGame Entity**: Tracks completion of mini-games, including DAO, database, repository, and ViewModel, enabling progress tracking and storing mini-game attempts for achievements.

### Profile and Achievements Integration

- **Profile Fragment**: Allows the user to update their profile with name, email, and avatar selection based on unlocked achievements.
- **My Achievement Fragment and Achievement Adapter**: Binds achievement data in a RecyclerView, dynamically displaying the name, description, unlock image, status indicator, and progress threshold for each achievement.

## New Implementations
(As per last update on 13th November 2024)   
**Adding Note as a feature of Vocabulary Entity and adapt new architecture**

## Getting Started

To run this app locally:

1. Clone the repository.
2. Open the project in Android Studio.
3. Sync the project to download necessary dependencies.
4. Run the app on an emulator or a physical device.

## Future Updates

- **Enhanced User Progress Tracking**: Expand user statistics for vocabulary learning progress.
- **More Game Types**: Introduce additional types of mini-games for vocabulary practice.
- **Improved Profile Customization**: Add more customization features for user profiles.

## Dependencies

- Room: Local database storage.
- RecyclerView: Displays vocabulary items and achievements in a list.
- ViewModel & LiveData: Lifecycle-aware data management.
- Navigation Components: For multi-fragment navigation.
- Material Components: UI elements like Snackbar and toolbar.

---

This documentation reflects the latest updates and improvements to **Linguaphile** as of 14th November 2024.