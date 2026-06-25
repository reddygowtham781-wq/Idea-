package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CommentEntity
import com.example.data.GeminiService
import com.example.data.IdeaEntity
import com.example.data.IdeaRepository
import com.example.data.MistakeEntity
import com.example.data.WorkStepEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FacebookUser(
    val name: String,
    val email: String,
    val profilePictureUrl: String? = null
)

class IdeaViewModel(
    private val repository: IdeaRepository,
    private val application: android.app.Application
) : ViewModel() {

    // --- Facebook Login & Sharing State ---
    private val prefs = application.getSharedPreferences("facebook_prefs", android.content.Context.MODE_PRIVATE)

    private val _facebookUser = MutableStateFlow<FacebookUser?>(null)
    val facebookUser = _facebookUser.asStateFlow()

    private val _sharedToFacebookIds = MutableStateFlow<Set<Int>>(emptySet())
    val sharedToFacebookIds = _sharedToFacebookIds.asStateFlow()

    fun loginWithFacebook(name: String, email: String, avatarUrl: String?) {
        prefs.edit().apply {
            putBoolean("facebook_logged_in", true)
            putString("facebook_user_name", name)
            putString("facebook_user_email", email)
            putString("facebook_user_avatar", avatarUrl ?: "")
            apply()
        }
        _facebookUser.value = FacebookUser(name, email, avatarUrl)
    }

    fun logoutFacebook() {
        prefs.edit().apply {
            putBoolean("facebook_logged_in", false)
            putString("facebook_user_name", "")
            putString("facebook_user_email", "")
            putString("facebook_user_avatar", "")
            apply()
        }
        _facebookUser.value = null
    }

    fun shareIdeaToFacebook(ideaId: Int) {
        val currentSet = _sharedToFacebookIds.value.toMutableSet()
        currentSet.add(ideaId)
        _sharedToFacebookIds.value = currentSet

        val stringSet = currentSet.map { it.toString() }.toSet()
        prefs.edit().putStringSet("shared_facebook_ids", stringSet).apply()
    }

    // List of all ideas
    val ideas: StateFlow<List<IdeaEntity>> = repository.allIdeas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Currently selected Idea for detailed view
    private val _selectedIdeaId = MutableStateFlow<Int?>(null)
    val selectedIdeaId = _selectedIdeaId.asStateFlow()

    private val _selectedIdea = MutableStateFlow<IdeaEntity?>(null)
    val selectedIdea = _selectedIdea.asStateFlow()

    // Work Steps for the selected idea
    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedIdeaSteps: StateFlow<List<WorkStepEntity>> = _selectedIdeaId
        .flatMapLatest { id ->
            if (id != null) repository.getStepsForIdea(id) else flowOf(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Mistakes for the selected idea
    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedIdeaMistakes: StateFlow<List<MistakeEntity>> = _selectedIdeaId
        .flatMapLatest { id ->
            if (id != null) repository.getMistakesForIdea(id) else flowOf(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Comments for the selected idea
    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedIdeaComments: StateFlow<List<CommentEntity>> = _selectedIdeaId
        .flatMapLatest { id ->
            if (id != null) repository.getCommentsForIdea(id) else flowOf(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- AI Status States ---
    private val _isAILoading = MutableStateFlow(false)
    val isAILoading = _isAILoading.asStateFlow()

    private val _aiFeedbackText = MutableStateFlow<String?>(null)
    val aiFeedbackText = _aiFeedbackText.asStateFlow()

    private val _aiSolutionText = MutableStateFlow<String?>(null)
    val aiSolutionText = _aiSolutionText.asStateFlow()

    private val _aiSelectedMistakeId = MutableStateFlow<Int?>(null)
    val aiSelectedMistakeId = _aiSelectedMistakeId.asStateFlow()

    // Seed checking flag
    private var isSeeding = false

    init {
        // Load Facebook Login status
        val loggedIn = prefs.getBoolean("facebook_logged_in", false)
        if (loggedIn) {
            val name = prefs.getString("facebook_user_name", "Gowtham Reddy") ?: "Gowtham Reddy"
            val email = prefs.getString("facebook_user_email", "reddygowtham781@gmail.com") ?: "reddygowtham781@gmail.com"
            val avatar = prefs.getString("facebook_user_avatar", "")
            _facebookUser.value = FacebookUser(name, email, avatar.takeIf { !it.isNullOrBlank() })
        }
        val sharedSet = prefs.getStringSet("shared_facebook_ids", emptySet()) ?: emptySet()
        _sharedToFacebookIds.value = sharedSet.mapNotNull { it.toIntOrNull() }.toSet()

        viewModelScope.launch {
            repository.allIdeas.collect { list ->
                if (list.isEmpty() && !isSeeding) {
                    isSeeding = true
                    seedData()
                }
            }
        }
    }

    private suspend fun seedData() {
        // Idea 1: Eco-Route Optimizer (Seeded as a community public idea)
        val idea1Id = repository.insertIdea(
            IdeaEntity(
                title = "Eco-Route Optimizer",
                description = "Solving carbon-efficient logistics through AI-powered route mapping.",
                category = "Tech",
                status = "Active",
                targetAudience = "Logistics companies & eco-conscious freight operators",
                valueProposition = "Reduces fuel consumption and carbon footprint by 15% using intelligent route planning.",
                isPublic = true,
                authorName = "Alex Rivera",
                likesCount = 24,
                isLikedByMe = false
            )
        )
        // Add work steps for Idea 1
        repository.insertStep(WorkStepEntity(ideaId = idea1Id.toInt(), title = "Core Routing Engine", notes = "Implement Dijkstra's algorithm with carbon factors", isCompleted = true))
        repository.insertStep(WorkStepEntity(ideaId = idea1Id.toInt(), title = "Integrate Traffic API", notes = "Fetch real-time traffic updates", isCompleted = true))
        repository.insertStep(WorkStepEntity(ideaId = idea1Id.toInt(), title = "Design Mobile UI Dashboard", notes = "Visual route progress and metrics screen", isCompleted = false))
        
        // Add comments for Idea 1
        repository.insertComment(CommentEntity(ideaId = idea1Id.toInt(), authorName = "Elena Petrova", commentText = "This is a brilliant concept! Have you considered rail network integrations?"))
        repository.insertComment(CommentEntity(ideaId = idea1Id.toInt(), authorName = "Marcus K.", commentText = "Great UX on the progress tracking so far."))

        // Idea 2: Micro-Greens Automation
        val idea2Id = repository.insertIdea(
            IdeaEntity(
                title = "Micro-Greens Home Farm",
                description = "Automated indoor vertical farming cabinets for fresh, nutrient-dense herbs.",
                category = "Creative",
                status = "Completed",
                targetAudience = "Urban hobbyists and health-focused home chefs",
                valueProposition = "An affordable IoT kit that controls automated watering and lighting, reducing crop failure to near zero.",
                isPublic = true,
                authorName = "Sarah Jenkins",
                likesCount = 42,
                isLikedByMe = true
            )
        )
        repository.insertComment(CommentEntity(ideaId = idea2Id.toInt(), authorName = "Dave Grohl", commentText = "I built something similar last year. Automated ventilation is the real game-changer to prevent mold!"))

        // Idea 3: Smart Expense Splitter (Private draft for the user to work on)
        val idea3Id = repository.insertIdea(
            IdeaEntity(
                title = "Crypto Share - Group Ledger",
                description = "Offline-first local network group expense tracker for remote retreats.",
                category = "Business",
                status = "Drafting",
                targetAudience = "Nomads and travelers",
                valueProposition = "Zero internet split-payment tracker with peer-to-peer sync.",
                isPublic = false,
                authorName = "You",
                likesCount = 0,
                isLikedByMe = false
            )
        )
        repository.insertStep(WorkStepEntity(ideaId = idea3Id.toInt(), title = "Define DB Schema", notes = "SQLite tables for ledger entries and users", isCompleted = true))
        repository.insertStep(WorkStepEntity(ideaId = idea3Id.toInt(), title = "P2P Bluetooth Sync Prototype", notes = "Establish connection between two close devices", isCompleted = false))
        repository.insertStep(WorkStepEntity(ideaId = idea3Id.toInt(), title = "AI Reconciliation Flow", notes = "Automatically balance matching items", isCompleted = false))
    }

    // --- Idea Actions ---

    fun selectIdea(idea: IdeaEntity?) {
        _selectedIdeaId.value = idea?.id
        _selectedIdea.value = idea
        _aiFeedbackText.value = null
        _aiSolutionText.value = null
        _aiSelectedMistakeId.value = null
    }

    fun addIdea(
        title: String,
        description: String,
        category: String,
        targetAudience: String,
        valueProposition: String
    ) {
        viewModelScope.launch {
            val idea = IdeaEntity(
                title = title,
                description = description,
                category = category,
                status = "Drafting",
                targetAudience = targetAudience,
                valueProposition = valueProposition,
                isPublic = false,
                authorName = "You"
            )
            val newId = repository.insertIdea(idea)
            // Auto select newly created idea
            val createdIdea = idea.copy(id = newId.toInt())
            selectIdea(createdIdea)
        }
    }

    fun updateIdea(idea: IdeaEntity) {
        viewModelScope.launch {
            repository.updateIdea(idea)
            if (_selectedIdeaId.value == idea.id) {
                _selectedIdea.value = idea
            }
        }
    }

    fun deleteIdea(id: Int) {
        viewModelScope.launch {
            repository.deleteIdea(id)
            if (_selectedIdeaId.value == id) {
                selectIdea(null)
            }
        }
    }

    // --- Public Platform Custom Actions ---

    fun toggleIdeaPublicStatus(idea: IdeaEntity) {
        viewModelScope.launch {
            val updated = idea.copy(isPublic = !idea.isPublic)
            repository.updateIdea(updated)
            if (_selectedIdeaId.value == idea.id) {
                _selectedIdea.value = updated
            }
        }
    }

    fun toggleLikeIdea(idea: IdeaEntity) {
        viewModelScope.launch {
            val isCurrentlyLiked = idea.isLikedByMe
            val updated = idea.copy(
                isLikedByMe = !isCurrentlyLiked,
                likesCount = if (isCurrentlyLiked) (idea.likesCount - 1).coerceAtLeast(0) else idea.likesCount + 1
            )
            repository.updateIdea(updated)
            if (_selectedIdeaId.value == idea.id) {
                _selectedIdea.value = updated
            }
        }
    }

    fun addComment(ideaId: Int, author: String, text: String) {
        viewModelScope.launch {
            val comment = CommentEntity(
                ideaId = ideaId,
                authorName = author.ifBlank { "Visitor" },
                commentText = text
            )
            repository.insertComment(comment)
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            repository.deleteComment(commentId)
        }
    }

    // --- Work Step Actions ---

    fun addWorkStep(title: String, notes: String) {
        val ideaId = _selectedIdeaId.value ?: return
        viewModelScope.launch {
            val step = WorkStepEntity(
                ideaId = ideaId,
                title = title,
                notes = notes,
                isCompleted = false
            )
            repository.insertStep(step)
        }
    }

    fun toggleWorkStepCompletion(step: WorkStepEntity) {
        viewModelScope.launch {
            repository.updateStep(step.copy(isCompleted = !step.isCompleted))
        }
    }

    fun deleteWorkStep(stepId: Int) {
        viewModelScope.launch {
            repository.deleteStep(stepId)
        }
    }

    // --- Mistake / Challenge Actions ---

    fun addMistake(description: String, impact: String) {
        val ideaId = _selectedIdeaId.value ?: return
        viewModelScope.launch {
            val mistake = MistakeEntity(
                ideaId = ideaId,
                description = description,
                impact = impact,
                isSolved = false
            )
            repository.insertMistake(mistake)
        }
    }

    fun solveMistake(mistake: MistakeEntity, solutionText: String) {
        viewModelScope.launch {
            repository.updateMistake(
                mistake.copy(
                    isSolved = true,
                    solution = solutionText
                )
            )
        }
    }

    fun deleteMistake(mistakeId: Int) {
        viewModelScope.launch {
            repository.deleteMistake(mistakeId)
            if (_aiSelectedMistakeId.value == mistakeId) {
                _aiSolutionText.value = null
                _aiSelectedMistakeId.value = null
            }
        }
    }

    // --- AI Strategy Actions ---

    fun getAIFeedbackForIdea() {
        val idea = _selectedIdea.value ?: return
        _isAILoading.value = true
        _aiFeedbackText.value = null

        viewModelScope.launch {
            val feedback = GeminiService.getIdeaFeedback(
                title = idea.title,
                description = idea.description,
                category = idea.category,
                audience = idea.targetAudience,
                valueProp = idea.valueProposition
            )
            _aiFeedbackText.value = feedback
            _isAILoading.value = false
        }
    }

    fun getAISolutionForMistake(mistake: MistakeEntity) {
        val idea = _selectedIdea.value ?: return
        _isAILoading.value = true
        _aiSolutionText.value = null
        _aiSelectedMistakeId.value = mistake.id

        viewModelScope.launch {
            val solutionAdvice = GeminiService.getMistakeSolution(
                ideaTitle = idea.title,
                ideaDescription = idea.description,
                mistakeDescription = mistake.description,
                impact = mistake.impact
            )
            _aiSolutionText.value = solutionAdvice
            // Persist the AI advice directly into the mistake entity
            repository.updateMistake(mistake.copy(aiAdvice = solutionAdvice))
            _isAILoading.value = false
        }
    }
}

class IdeaViewModelFactory(
    private val repository: IdeaRepository,
    private val application: android.app.Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IdeaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IdeaViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
