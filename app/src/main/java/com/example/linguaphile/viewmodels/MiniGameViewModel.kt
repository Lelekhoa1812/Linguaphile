import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.linguaphile.entities.MiniGame
import com.example.linguaphile.repositories.MiniGameRepository
import kotlinx.coroutines.launch

class MiniGameViewModel(private val repository: MiniGameRepository) : ViewModel() {
    val completedGamesCount: LiveData<Int> = repository.getCompletedGamesCount()

    fun insertMiniGame(miniGame: MiniGame) {
        viewModelScope.launch {
            repository.insertMiniGame(miniGame)
        }
    }

    fun getCompletedMiniGamesCount(): LiveData<Int>  {
        // This should retrieve the count from your data source.
        return repository.getCompletedGamesCount()
    }
}
