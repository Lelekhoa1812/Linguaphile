# Linguaphile

**Linguaphile** is an Android application designed for English learners to learn and practice vocabulary. It utilizes CRUD operations to manage vocabulary items displayed in a RecyclerView, and employs modern Android architecture components such as Room, DAO, LiveData, ViewModel, and multi-fragment navigation.

## Features

- **Vocabulary Management**: Add, view, update, and delete vocabulary items.
- **Filtering Options**: Filter vocabulary items by date or type.
- **Multi-Fragment Navigation**: Navigate between different pages, including "Home", "Add New Vocabulary", "Mini Game", and "My Profile".
- **Customized Toolbar**: Each fragment has a customized toolbar displaying the fragment name and a hamburger button for navigation.

## Vocabulary Entity

The **Vocabulary** entity includes the following attributes:

1. **Id** (Integer, non-nullable): Auto-incremented ID for each vocabulary item.
2. **Name** (String, non-nullable): The vocabulary term.
3. **Type** (String, non-nullable): The type of the vocabulary (e.g., Noun, Verb, Adjective, Adverb).
4. **Date** (Date, non-nullable): The date the vocabulary item was added, stored in datetime format.
5. **Meaning1** (String, non-nullable): The primary meaning of the vocabulary item in the user's first language.
6. **Meaning2** (String, nullable): An optional secondary meaning.
7. **Meaning3** (String, nullable): An optional third meaning.
8. **Meaning4** (String, nullable): An optional fourth meaning.
9. **Synonym1** (String, nullable): An optional first synonym.
10. **Synonym2** (String, nullable): An optional second synonym.
11. **Synonym3** (String, nullable): An optional third synonym.
12. **Synonym4** (String, nullable): An optional fourth synonym.

## App Architecture

The app is built with a multi-fragment structure and Android architecture components:

- **Room**: For local database management, providing data persistence.
- **DAO**: For defining methods to interact with the database.
- **LiveData**: For observing data changes in the UI.
- **ViewModel**: To manage UI-related data in a lifecycle-conscious way.
- **RecyclerView**: To display vocabulary items in a scrollable list.
- **Toolbar**: Each fragment has a customized toolbar for easy navigation and context.

## Fragments

### 1. Home Fragment

The **Home** fragment displays a list of vocabulary items stored in Room. It includes the following features:

- **Filter by Date**: A dropdown button to filter vocabulary items based on the date they were added. Options include:
  - **Today**
  - **This Week**
  - **This Month**
  - **This Year**

- **Filter by Type**: A dropdown button to filter vocabulary items by type. Options include:
  - **Noun**
  - **Verb**
  - **Adjective**
  - **Adverb**

The list of vocabulary items is shown in a RecyclerView. Each item includes the following details:
- **Name** and **Type** of the vocabulary.
- A comma-separated list of meanings (e.g., "Meaning1, Meaning2, Meaning3, Meaning4") if there are multiple.
- A comma-separated list of synonyms if there are multiple.
  
Each item also includes a 3-dot button that displays two options when clicked:
- **Update**: Navigates to the "Update Vocabulary" fragment.
- **Delete**: Deletes the vocabulary item and shows a Snackbar with an **UNDO** option to restore the deleted item.

### 2. Add New Vocabulary Fragment

The **Add New Vocabulary** fragment allows users to insert new vocabulary items. The layout is scrollable and includes:

- **New Vocabulary**: An EditText field for entering the vocabulary term.
- **Type**: A dropdown with options for the type of the vocabulary (Noun, Verb, Adjective, Adverb).
- **Meaning**:
  - A mandatory EditText for the primary meaning (Meaning1).
  - An "Add Meaning" button to add up to three additional meanings (Meaning2, Meaning3, Meaning4). If the user tries to add more than four meanings, a Toast message ("Maximum 4 meanings allowed") is displayed.
- **Synonym**:
  - An "Add Synonym" button to add up to four synonyms (Synonym1 to Synonym4). If the user tries to add more than four synonyms, a Toast message ("Maximum 4 synonyms allowed") is displayed.

The **Add this Vocabulary** button saves the vocabulary item to the database, storing the Date as the current time and capturing all entered information.

### 3. Other Fragments

Other fragments, including **Update Vocabulary**, **Mini Game**, and **My Profile**, are currently placeholders for future updates. These pages have been generated with their Kotlin and layout files as empty pages to allow testing of the fragment navigation.

## Getting Started

To run this app locally:

1. Clone the repository.
2. Open the project in Android Studio.
3. Sync the project to download necessary dependencies.
4. Run the app on an emulator or a physical device.

## Future Updates

- **Update Vocabulary**: The ability to modify existing vocabulary items.
- **Mini Game**: A game feature to help users practice vocabulary.
- **My Profile**: A profile page to track user progress or preferences.

## Dependencies

- Room: For local database storage.
- RecyclerView: To display vocabulary items in a list format.
- ViewModel & LiveData: To handle data changes in a lifecycle-aware manner.
- Navigation Components: For multi-fragment navigation.
- Material Components: For UI elements like Snackbar and toolbar.

