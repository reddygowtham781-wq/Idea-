package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.CommentEntity
import com.example.data.IdeaEntity
import com.example.data.MistakeEntity
import com.example.data.WorkStepEntity
import com.example.ui.theme.BentoAlabasterGreen
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoDarkGreenText
import com.example.ui.theme.BentoErrorBrand
import com.example.ui.theme.BentoErrorLightBg
import com.example.ui.theme.BentoErrorTextDark
import com.example.ui.theme.BentoPrimaryGreen
import com.example.ui.theme.BentoSecondaryLightGreen
import com.example.ui.theme.BentoSubduedText
import com.example.ui.theme.BentoTextDark
import com.example.ui.theme.BentoWhite
import com.example.ui.theme.FacebookBlue
import com.example.ui.theme.FacebookDarkBlue
import com.example.ui.theme.FacebookLightBlue
import com.example.ui.theme.FacebookGreyBg
import androidx.compose.material.icons.filled.AccountCircle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IdeaDashboard(
    viewModel: IdeaViewModel,
    modifier: Modifier = Modifier
) {
    val ideas by viewModel.ideas.collectAsState()
    val selectedIdea by viewModel.selectedIdea.collectAsState()
    val selectedIdeaSteps by viewModel.selectedIdeaSteps.collectAsState()
    val selectedIdeaMistakes by viewModel.selectedIdeaMistakes.collectAsState()
    val selectedIdeaComments by viewModel.selectedIdeaComments.collectAsState()
    val isAILoading by viewModel.isAILoading.collectAsState()
    val aiFeedbackText by viewModel.aiFeedbackText.collectAsState()
    val aiSolutionText by viewModel.aiSolutionText.collectAsState()
    val aiSelectedMistakeId by viewModel.aiSelectedMistakeId.collectAsState()

    val facebookUser by viewModel.facebookUser.collectAsState()
    val sharedToFacebookIds by viewModel.sharedToFacebookIds.collectAsState()

    var showFacebookAuthWebDialog by remember { mutableStateOf(false) }
    var showFacebookShareComposerDialog by remember { mutableStateOf(false) }

    var activeTab by remember { mutableStateOf(0) } // 0: Explore, 1: Community, 2: Workspace, 3: AI Review
    var showCreateIdeaDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // --- Elegant Bento-Style Header ---
            HeaderBar(
                facebookUser = facebookUser,
                onConnectClick = { showFacebookAuthWebDialog = true },
                onLogoutClick = { viewModel.logoutFacebook() }
            )

            // --- Tab Selector Navigation (Bento Styling) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BentoWhite)
                    .padding(vertical = 6.dp, horizontal = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tabItems = listOf(
                        Triple(0, Icons.Default.Lightbulb, "Explore"),
                        Triple(1, Icons.Default.Group, "Community"),
                        Triple(2, Icons.Default.EditNote, "Workspace"),
                        Triple(3, Icons.Default.Psychology, "AI Review")
                    )

                    tabItems.forEach { (index, icon, label) ->
                        val isSelected = activeTab == index
                        val bg = if (isSelected) BentoSecondaryLightGreen else Color.Transparent
                        val fg = if (isSelected) BentoPrimaryGreen else BentoSubduedText

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(bg)
                                .clickable { activeTab = index }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("tab_$index"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = fg,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = fg
                                )
                            }
                        }
                    }
                }
            }

            // Divider matching BentoBorder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BentoBorder)
            )

            // --- Tab Contents ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    0 -> IdeasListTab(
                        ideas = ideas,
                        selectedIdea = selectedIdea,
                        onIdeaSelected = { idea ->
                            viewModel.selectIdea(idea)
                            activeTab = 2 // Navigate to Workspace
                        },
                        onIdeaDeleted = { id -> viewModel.deleteIdea(id) },
                        onCreateNewIdeaClick = { showCreateIdeaDialog = true }
                    )

                    1 -> CommunityFeedTab(
                        ideas = ideas,
                        viewModel = viewModel,
                        onIdeaSelected = { idea ->
                            viewModel.selectIdea(idea)
                            activeTab = 2 // View workspace of this idea
                        }
                    )

                    2 -> WorkspaceTab(
                        idea = selectedIdea,
                        steps = selectedIdeaSteps,
                        mistakes = selectedIdeaMistakes,
                        comments = selectedIdeaComments,
                        isAILoading = isAILoading,
                        aiSolutionText = aiSolutionText,
                        aiSelectedMistakeId = aiSelectedMistakeId,
                        facebookUser = facebookUser,
                        sharedToFacebookIds = sharedToFacebookIds,
                        onConnectFacebookClick = { showFacebookAuthWebDialog = true },
                        onShareToFacebookClick = { showFacebookShareComposerDialog = true },
                        onStatusChange = { newStatus ->
                            selectedIdea?.let { viewModel.updateIdea(it.copy(status = newStatus)) }
                        },
                        onAddStep = { title, notes -> viewModel.addWorkStep(title, notes) },
                        onToggleStep = { step -> viewModel.toggleWorkStepCompletion(step) },
                        onDeleteStep = { stepId -> viewModel.deleteWorkStep(stepId) },
                        onAddMistake = { desc, impact -> viewModel.addMistake(desc, impact) },
                        onSolveMistake = { mistake, soln -> viewModel.solveMistake(mistake, soln) },
                        onDeleteMistake = { mistakeId -> viewModel.deleteMistake(mistakeId) },
                        onConsultAI = { mistake -> viewModel.getAISolutionForMistake(mistake) },
                        onTogglePublic = { ideaEntity -> viewModel.toggleIdeaPublicStatus(ideaEntity) },
                        onLikeIdea = { ideaEntity -> viewModel.toggleLikeIdea(ideaEntity) },
                        onAddComment = { author, text ->
                            selectedIdea?.let { viewModel.addComment(it.id, author, text) }
                        },
                        onDeleteComment = { commentId -> viewModel.deleteComment(commentId) }
                    )

                    3 -> AIStrategyReviewTab(
                        idea = selectedIdea,
                        isAILoading = isAILoading,
                        aiFeedbackText = aiFeedbackText,
                        onPerformAudit = { viewModel.getAIFeedbackForIdea() }
                    )
                }
            }
        }

        // --- Floating Action Button on Ideas Tab ---
        if (activeTab == 0) {
            FloatingActionButton(
                onClick = { showCreateIdeaDialog = true },
                containerColor = BentoPrimaryGreen,
                contentColor = BentoWhite,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .testTag("add_idea_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Idea")
            }
        }
    }

    // --- Create Idea Dialog ---
    if (showCreateIdeaDialog) {
        CreateIdeaDialog(
            onDismiss = { showCreateIdeaDialog = false },
            onConfirm = { title, desc, category, audience, valueProp ->
                viewModel.addIdea(title, desc, category, audience, valueProp)
                showCreateIdeaDialog = false
                activeTab = 2 // Switch to workspace immediately to view the new idea
            }
        )
    }

    // --- Facebook OAuth Simulated Web Dialog ---
    if (showFacebookAuthWebDialog) {
        FacebookAuthWebDialog(
            onDismiss = { showFacebookAuthWebDialog = false },
            onLoginSuccess = { name, email ->
                viewModel.loginWithFacebook(name, email, null)
                showFacebookAuthWebDialog = false
            }
        )
    }

    // --- Facebook Share Composer Dialog ---
    if (showFacebookShareComposerDialog && selectedIdea != null) {
        FacebookShareComposerDialog(
            idea = selectedIdea!!,
            facebookUser = facebookUser!!,
            onDismiss = { showFacebookShareComposerDialog = false },
            onShareConfirm = {
                viewModel.shareIdeaToFacebook(selectedIdea!!.id)
                showFacebookShareComposerDialog = false
            }
        )
    }
}

// ==========================================
// COMPOSABLE COMPONENT: HeaderBar
// ==========================================
@Composable
fun HeaderBar(
    facebookUser: FacebookUser?,
    onConnectClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var showProfileMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BentoWhite)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BentoPrimaryGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = BentoWhite,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "IdeaForge",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextDark,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Work on your vision",
                    fontSize = 11.sp,
                    color = BentoSubduedText,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Box {
            Surface(
                onClick = { showProfileMenu = !showProfileMenu },
                shape = CircleShape,
                color = if (facebookUser != null) FacebookLightBlue else BentoBorder,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("profile_avatar_btn")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (facebookUser != null) {
                        // User Avatar from initials
                        Text(
                            text = facebookUser.name.take(1).uppercase(),
                            color = FacebookBlue,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        // Tiny overlaid Facebook badge at the bottom-right corner
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(FacebookBlue)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "f",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.offset(y = (-1).dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Profile",
                            tint = BentoTextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // High fidelity drop down card for Facebook Connection Profile Info
            DropdownMenu(
                expanded = showProfileMenu,
                onDismissRequest = { showProfileMenu = false },
                modifier = Modifier
                    .width(240.dp)
                    .background(BentoWhite)
                    .border(BorderStroke(1.dp, BentoBorder), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (facebookUser != null) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(FacebookBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = facebookUser.name.take(1).uppercase(),
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = facebookUser.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextDark
                        )
                        Text(
                            text = facebookUser.email,
                            fontSize = 11.sp,
                            color = BentoSubduedText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = FacebookLightBlue,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = FacebookBlue,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Facebook Connected",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FacebookBlue
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                showProfileMenu = false
                                onLogoutClick()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BentoErrorLightBg),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .testTag("fb_logout_btn")
                        ) {
                            Text("Disconnect Facebook", color = BentoErrorBrand, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = BentoSubduedText,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Guest Explorer",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextDark
                        )
                        Text(
                            text = "Connect social media to publish",
                            fontSize = 11.sp,
                            color = BentoSubduedText,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                showProfileMenu = false
                                onConnectClick()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FacebookBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("fb_login_header_btn")
                        ) {
                            Text("Connect Facebook", color = BentoWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// COMPOSABLE TAB: IdeasListTab (My Drafts)
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IdeasListTab(
    ideas: List<IdeaEntity>,
    selectedIdea: IdeaEntity?,
    onIdeaSelected: (IdeaEntity) -> Unit,
    onIdeaDeleted: (Int) -> Unit,
    onCreateNewIdeaClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Tech", "Business", "Creative", "Social", "Other")

    val myIdeas = ideas.filter { it.authorName == "You" }

    val filteredIdeas = myIdeas.filter {
        (selectedCategory == "All" || it.category == selectedCategory) &&
                (it.title.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search your ideas...", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BentoSubduedText) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_ideas_input"),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BentoWhite,
                unfocusedContainerColor = BentoWhite,
                focusedBorderColor = BentoPrimaryGreen,
                unfocusedBorderColor = BentoBorder
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Categories list
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                val chipBg = if (isSelected) BentoPrimaryGreen else BentoWhite
                val chipContentColor = if (isSelected) BentoWhite else BentoSubduedText
                val border = if (isSelected) null else BorderStroke(1.dp, BentoBorder)

                Surface(
                    onClick = { selectedCategory = cat },
                    shape = RoundedCornerShape(20.dp),
                    color = chipBg,
                    contentColor = chipContentColor,
                    border = border,
                    modifier = Modifier.testTag("category_chip_$cat")
                ) {
                    Text(
                        text = cat,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredIdeas.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = BentoPrimaryGreen.copy(alpha = 0.2f),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (searchQuery.isNotEmpty() || selectedCategory != "All") "No ideas match your filters." else "No private drafts yet.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (searchQuery.isNotEmpty() || selectedCategory != "All") "Try adjusting your search criteria." else "Begin your journey by adding your first big concept!",
                    fontSize = 12.sp,
                    color = BentoSubduedText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                if (myIdeas.isEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onCreateNewIdeaClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add First Idea", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                filteredIdeas.forEach { idea ->
                    val isCurrent = selectedIdea?.id == idea.id
                    val border = if (isCurrent) {
                        BorderStroke(2.dp, BentoPrimaryGreen)
                    } else {
                        BorderStroke(1.dp, BentoBorder)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("idea_card_${idea.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) BentoAlabasterGreen.copy(alpha = 0.5f) else BentoWhite
                        ),
                        shape = RoundedCornerShape(24.dp),
                        border = border,
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .clickable { onIdeaSelected(idea) }
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CategoryBadge(idea.category)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    if (idea.isPublic) {
                                        Icon(
                                            imageVector = Icons.Default.Public,
                                            contentDescription = "Public Shared",
                                            tint = BentoPrimaryGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Private Draft",
                                            tint = BentoSubduedText,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { onIdeaDeleted(idea.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Idea",
                                        tint = BentoErrorBrand.copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = idea.title,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextDark
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = idea.description,
                                fontSize = 13.sp,
                                color = BentoSubduedText,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(idea.status)

                                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                Text(
                                    text = sdf.format(Date(idea.createdAt)),
                                    fontSize = 11.sp,
                                    color = BentoSubduedText.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// ==========================================
// COMPOSABLE TAB: CommunityFeedTab (Public Platform!)
// ==========================================
@Composable
fun CommunityFeedTab(
    ideas: List<IdeaEntity>,
    viewModel: IdeaViewModel,
    onIdeaSelected: (IdeaEntity) -> Unit
) {
    val publicIdeas = ideas.filter { it.isPublic }
    var searchCommunityQuery by remember { mutableStateOf("") }

    val filteredPublic = publicIdeas.filter {
        it.title.contains(searchCommunityQuery, ignoreCase = true) ||
                it.description.contains(searchCommunityQuery, ignoreCase = true) ||
                it.authorName.contains(searchCommunityQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Community Hub",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = BentoDarkGreenText,
            letterSpacing = (-0.5).sp
        )
        Text(
            text = "Browse, upvote, and discuss public concepts around the world",
            fontSize = 12.sp,
            color = BentoSubduedText,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = searchCommunityQuery,
            onValueChange = { searchCommunityQuery = it },
            placeholder = { Text("Search public platform...", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BentoSubduedText) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_community_input"),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BentoWhite,
                unfocusedContainerColor = BentoWhite,
                focusedBorderColor = BentoPrimaryGreen,
                unfocusedBorderColor = BentoBorder
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredPublic.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No public ideas found. Be the first to publish yours!",
                    fontSize = 14.sp,
                    color = BentoSubduedText,
                    fontStyle = FontStyle.Italic
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                filteredPublic.forEach { idea ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .testTag("community_card_${idea.id}"),
                        colors = CardDefaults.cardColors(containerColor = BentoWhite),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, BentoBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .clickable { onIdeaSelected(idea) }
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(BentoSecondaryLightGreen),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = idea.authorName.take(1).uppercase(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BentoPrimaryGreen
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = idea.authorName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoTextDark
                                    )
                                }

                                CategoryBadge(idea.category)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = idea.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextDark
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = idea.description,
                                fontSize = 13.sp,
                                color = BentoSubduedText,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Like Action Button
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { viewModel.toggleLikeIdea(idea) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (idea.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Upvote",
                                            tint = if (idea.isLikedByMe) BentoErrorBrand else BentoSubduedText,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = idea.likesCount.toString(),
                                            fontSize = 12.sp,
                                            color = BentoTextDark,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Comment Count / Discussion Indicator
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Comment,
                                            contentDescription = "Comments",
                                            tint = BentoSubduedText,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Open Hub",
                                            fontSize = 11.sp,
                                            color = BentoSubduedText,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                StatusBadge(idea.status)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// ==========================================
// COMPOSABLE TAB: WorkspaceTab (The Bento Grid!)
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkspaceTab(
    idea: IdeaEntity?,
    steps: List<WorkStepEntity>,
    mistakes: List<MistakeEntity>,
    comments: List<CommentEntity>,
    isAILoading: Boolean,
    aiSolutionText: String?,
    aiSelectedMistakeId: Int?,
    facebookUser: FacebookUser?,
    sharedToFacebookIds: Set<Int>,
    onConnectFacebookClick: () -> Unit,
    onShareToFacebookClick: () -> Unit,
    onStatusChange: (String) -> Unit,
    onAddStep: (String, String) -> Unit,
    onToggleStep: (WorkStepEntity) -> Unit,
    onDeleteStep: (Int) -> Unit,
    onAddMistake: (String, String) -> Unit,
    onSolveMistake: (MistakeEntity, String) -> Unit,
    onDeleteMistake: (Int) -> Unit,
    onConsultAI: (MistakeEntity) -> Unit,
    onTogglePublic: (IdeaEntity) -> Unit,
    onLikeIdea: (IdeaEntity) -> Unit,
    onAddComment: (String, String) -> Unit,
    onDeleteComment: (Int) -> Unit
) {
    if (idea == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.ListAlt,
                contentDescription = null,
                tint = BentoPrimaryGreen.copy(alpha = 0.2f),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Idea Selected",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BentoTextDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Go to the Explore or Community tab and tap on an idea to launch your interactive Bento Workspace.",
                fontSize = 13.sp,
                color = BentoSubduedText,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    var stepTitleInput by remember { mutableStateOf("") }
    var stepNotesInput by remember { mutableStateOf("") }
    var showAddStepForm by remember { mutableStateOf(false) }

    var mistakeDescInput by remember { mutableStateOf("") }
    var mistakeImpactInput by remember { mutableStateOf("Minor") }
    var showAddMistakeForm by remember { mutableStateOf(false) }

    var commentTextInput by remember { mutableStateOf("") }
    var commenterNameInput by remember { mutableStateOf("") }

    var showSolveDialogForMistake by remember { mutableStateOf<MistakeEntity?>(null) }
    var manualSolutionText by remember { mutableStateOf("") }

    // Compute dynamic completeness score
    val completedSteps = steps.count { it.isCompleted }
    val progressPercent = if (steps.isNotEmpty()) completedSteps.toFloat() / steps.size.toFloat() else 0f

    // Clarity Score Logic
    var clarityScore = 30
    if (idea.description.length > 20) clarityScore += 20
    if (idea.targetAudience.isNotBlank()) clarityScore += 15
    if (idea.valueProposition.isNotBlank()) clarityScore += 15
    if (steps.isNotEmpty()) clarityScore += 10
    if (steps.any { it.isCompleted }) clarityScore += 10

    val openBlockers = mistakes.count { !it.isSolved }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ----------------------------------------------------
        // BENTO BOX 1: ACTIVE SESSION (Selected Idea & Progress)
        // ----------------------------------------------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BentoSecondaryLightGreen),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = BentoPrimaryGreen.copy(alpha = 0.08f),
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.BottomEnd)
                )

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (idea.authorName == "You") "YOUR ACTIVE SESSION" else "COMMUNITY PLATFORM IDEAL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoDarkGreenText,
                            letterSpacing = 1.sp
                        )

                        // Public/Private Publishing Switch
                        if (idea.authorName == "You") {
                            Surface(
                                onClick = { onTogglePublic(idea) },
                                color = if (idea.isPublic) BentoPrimaryGreen else BentoBorder,
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.testTag("toggle_public_badge")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (idea.isPublic) Icons.Default.Public else Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = BentoWhite,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (idea.isPublic) "Public Feed" else "Draft Lock",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoWhite
                                    )
                                }
                            }
                        } else {
                            Surface(
                                color = BentoPrimaryGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Public,
                                        contentDescription = null,
                                        tint = BentoPrimaryGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "By ${idea.authorName}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoPrimaryGreen
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = idea.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoDarkGreenText,
                        lineHeight = 26.sp
                    )
                    Text(
                        text = idea.description,
                        fontSize = 13.sp,
                        color = BentoPrimaryGreen,
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress slider/bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(BentoDarkGreenText.copy(alpha = 0.1f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(fraction = progressPercent.coerceIn(0.01f, 1f))
                                .clip(CircleShape)
                                .background(BentoPrimaryGreen)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Milestones Done: ${(progressPercent * 100).toInt()}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoDarkGreenText
                        )
                        Text(
                            text = "$completedSteps of ${steps.size} complete",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = BentoDarkGreenText
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ----------------------------------------------------
        // BENTO BOX: FACEBOOK HUB (Full-width Social Engine)
        // ----------------------------------------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("facebook_hub_card"),
            colors = CardDefaults.cardColors(containerColor = FacebookLightBlue.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, FacebookBlue.copy(alpha = 0.3f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(FacebookBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "f",
                                color = BentoWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.offset(y = (-1).dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "FACEBOOK HUB",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FacebookBlue,
                            letterSpacing = 1.sp
                        )
                    }

                    if (facebookUser != null) {
                        Surface(
                            color = FacebookBlue,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "CONNECTED",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoWhite,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (facebookUser == null) {
                    Text(
                        text = "Amplify Your Reach",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Connect your Facebook account to publish your vision to the public platform, share development milestones with your circle, and request AI-powered mistake reviews in real-time.",
                        fontSize = 12.sp,
                        color = BentoSubduedText,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onConnectFacebookClick,
                        colors = ButtonDefaults.buttonColors(containerColor = FacebookBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("fb_connect_hub_btn")
                    ) {
                        Text("Connect Facebook Account", color = BentoWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Logged in as ${facebookUser.name}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextDark
                            )
                            Text(
                                text = facebookUser.email,
                                fontSize = 11.sp,
                                color = BentoSubduedText
                            )
                        }

                        val isShared = sharedToFacebookIds.contains(idea.id)
                        if (isShared) {
                            Surface(
                                color = Color(0xFF31A24C),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = BentoWhite,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Posted on Feed",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoWhite
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Let your network discover your vision. Sharing this project posts an elegant interactive link preview of your core value propositions directly to your timeline.",
                        fontSize = 11.sp,
                        color = BentoSubduedText,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val isShared = sharedToFacebookIds.contains(idea.id)
                    Button(
                        onClick = onShareToFacebookClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isShared) Color(0xFFE4E6EB) else FacebookBlue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("fb_share_action_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isShared) Icons.Default.CheckCircle else Icons.Default.Send,
                                contentDescription = null,
                                tint = if (isShared) Color(0xFF4B4F56) else BentoWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isShared) "Post Another Update on Facebook" else "Share Project to Facebook Feed",
                                color = if (isShared) Color(0xFF4B4F56) else BentoWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ----------------------------------------------------
        // BENTO BOXES ROW (Weak Points & Clarity Score)
        // ----------------------------------------------------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left half-width box: WEAK POINTS / ROADBLOCKS
            Card(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                colors = CardDefaults.cardColors(containerColor = BentoWhite),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, BentoBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BentoErrorLightBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BugReport,
                                contentDescription = "Roadblocks",
                                tint = BentoErrorTextDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "Blockers",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoSubduedText
                        )
                    }

                    Column {
                        Text(
                            text = String.format("%02d", openBlockers),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextDark,
                            lineHeight = 36.sp
                        )
                        Text(
                            text = "Active issues logged",
                            fontSize = 9.sp,
                            color = BentoSubduedText,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = { showAddMistakeForm = !showAddMistakeForm },
                        colors = ButtonDefaults.buttonColors(containerColor = BentoErrorBrand),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (showAddMistakeForm) "CLOSE" else "LOG ROADBLOCK",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoWhite
                        )
                    }
                }
            }

            // Right half-width box: CLARITY SCORE
            Card(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                colors = CardDefaults.cardColors(containerColor = BentoAlabasterGreen),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(64.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { clarityScore / 100f },
                            modifier = Modifier.fillMaxSize(),
                            color = BentoPrimaryGreen,
                            strokeWidth = 6.dp,
                            trackColor = BentoBorder
                        )
                        Text(
                            text = "$clarityScore%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoDarkGreenText
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "CLARITY SCORE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoSubduedText,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Expandable Add Roadblock block
        AnimatedVisibility(visible = showAddMistakeForm) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BentoErrorLightBg),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BentoErrorBrand.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Log Roadblock or Mistake",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BentoErrorTextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = mistakeDescInput,
                        onValueChange = { mistakeDescInput = it },
                        placeholder = { Text("What mistake or obstacle did you hit?", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mistake_desc_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BentoWhite,
                            unfocusedContainerColor = BentoWhite,
                            focusedBorderColor = BentoErrorBrand,
                            unfocusedBorderColor = BentoBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Severity:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BentoErrorTextDark)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Minor", "Major", "Blocker").forEach { severity ->
                                val isSel = mistakeImpactInput == severity
                                val chipBg = if (isSel) BentoErrorBrand else BentoWhite
                                val chipFg = if (isSel) BentoWhite else BentoErrorTextDark

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(chipBg)
                                        .clickable { mistakeImpactInput = severity }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("severity_radio_$severity")
                                ) {
                                    Text(severity, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = chipFg)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (mistakeDescInput.isNotBlank()) {
                                onAddMistake(mistakeDescInput, mistakeImpactInput)
                                mistakeDescInput = ""
                                mistakeImpactInput = "Minor"
                                showAddMistakeForm = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BentoErrorBrand),
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("save_mistake_button")
                    ) {
                        Text("Save & Debug with AI", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ----------------------------------------------------
        // BENTO BOX 3: WORK MILESTONES (Todo tracker)
        // ----------------------------------------------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BentoWhite),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, BentoBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = BentoPrimaryGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Milestones & Steps",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextDark
                        )
                    }

                    if (idea.authorName == "You") {
                        IconButton(
                            onClick = { showAddStepForm = !showAddStepForm },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (showAddStepForm) Icons.Default.Close else Icons.Default.Add,
                                contentDescription = "Add Milestones",
                                tint = BentoPrimaryGreen
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = showAddStepForm) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = stepTitleInput,
                            onValueChange = { stepTitleInput = it },
                            placeholder = { Text("Task / Milestone title", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("step_title_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = BentoBackground,
                                unfocusedContainerColor = BentoBackground,
                                focusedBorderColor = BentoPrimaryGreen,
                                unfocusedBorderColor = BentoBorder
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = stepNotesInput,
                            onValueChange = { stepNotesInput = it },
                            placeholder = { Text("Details / Target date (optional)", fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("step_notes_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = BentoBackground,
                                unfocusedContainerColor = BentoBackground,
                                focusedBorderColor = BentoPrimaryGreen,
                                unfocusedBorderColor = BentoBorder
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (stepTitleInput.isNotBlank()) {
                                    onAddStep(stepTitleInput, stepNotesInput)
                                    stepTitleInput = ""
                                    stepNotesInput = ""
                                    showAddStepForm = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryGreen),
                            modifier = Modifier
                                .align(Alignment.End)
                                .testTag("save_step_button")
                        ) {
                            Text("Save", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (steps.isEmpty()) {
                    Text(
                        text = "No milestones mapped out. Add some steps to track progress!",
                        fontSize = 12.sp,
                        color = BentoSubduedText,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    steps.forEach { step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (step.isCompleted) Color.Transparent else BentoBackground.copy(alpha = 0.5f))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { if (idea.authorName == "You") onToggleStep(step) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("toggle_step_${step.id}")
                            ) {
                                Icon(
                                    imageVector = if (step.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = "Toggle state",
                                    tint = if (step.isCompleted) BentoPrimaryGreen else BentoSubduedText
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = step.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (step.isCompleted) BentoSubduedText else BentoTextDark,
                                    textDecoration = if (step.isCompleted) TextDecoration.LineThrough else null
                                )
                                if (step.notes.isNotBlank()) {
                                    Text(
                                        text = step.notes,
                                        fontSize = 11.sp,
                                        color = BentoSubduedText,
                                        textDecoration = if (step.isCompleted) TextDecoration.LineThrough else null
                                    )
                                }
                            }

                            if (idea.authorName == "You") {
                                IconButton(
                                    onClick = { onDeleteStep(step.id) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("delete_step_${step.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete step",
                                        tint = BentoErrorBrand.copy(alpha = 0.6f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ----------------------------------------------------
        // BENTO BOX 4: COMMUNITY DISCUSSION & COMMENTS (Only if public!)
        // ----------------------------------------------------
        if (idea.isPublic) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BentoWhite),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, BentoBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Comment,
                            contentDescription = null,
                            tint = BentoPrimaryGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Community Hub Discussion",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextDark
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Comment listing
                    if (comments.isEmpty()) {
                        Text(
                            text = "No discussion comments yet. Be the first to express feedback!",
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic,
                            color = BentoSubduedText,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            comments.forEach { comment ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(BentoBackground)
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .clip(CircleShape)
                                                        .background(BentoSecondaryLightGreen),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = comment.authorName.take(1).uppercase(),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = BentoPrimaryGreen
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = comment.authorName,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BentoTextDark
                                                )
                                            }

                                            // Only allow deleting your own comments or if you're the author of the idea
                                            if (comment.authorName == "You" || idea.authorName == "You") {
                                                IconButton(
                                                    onClick = { onDeleteComment(comment.id) },
                                                    modifier = Modifier.size(20.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Delete comment",
                                                        tint = BentoErrorBrand,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = comment.commentText,
                                            fontSize = 12.sp,
                                            color = BentoSubduedText
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Write Comment Field
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (facebookUser == null) {
                            OutlinedTextField(
                                value = commenterNameInput,
                                onValueChange = { commenterNameInput = it },
                                placeholder = { Text("Your name (optional)", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("commenter_name_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = BentoBackground,
                                    unfocusedContainerColor = BentoBackground,
                                    focusedBorderColor = BentoPrimaryGreen,
                                    unfocusedBorderColor = BentoBorder
                                )
                            )
                        } else {
                            // Show a small row indicating commenting as Facebook Profile
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(FacebookBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = facebookUser.name.take(1).uppercase(),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Commenting as ${facebookUser.name}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoDarkGreenText
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = commentTextInput,
                                onValueChange = { commentTextInput = it },
                                placeholder = { Text("Write constructive feedback...", fontSize = 11.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("comment_text_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = BentoBackground,
                                    unfocusedContainerColor = BentoBackground,
                                    focusedBorderColor = BentoPrimaryGreen,
                                    unfocusedBorderColor = BentoBorder
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = {
                                    if (commentTextInput.isNotBlank()) {
                                        val author = facebookUser?.name ?: commenterNameInput.ifBlank { "Visitor" }
                                        onAddComment(author, commentTextInput)
                                        commentTextInput = ""
                                        commenterNameInput = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BentoPrimaryGreen)
                                    .testTag("post_comment_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Post comment",
                                    tint = BentoWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ----------------------------------------------------
        // BENTO BOX 5: OBSTACLES & BLOCKERS LIST
        // ----------------------------------------------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BentoWhite),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, BentoBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = null,
                        tint = BentoErrorBrand
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Vulnerabilities & Roadblocks",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextDark
                    )
                }

                if (mistakes.isEmpty()) {
                    Text(
                        text = "Perfect! No vulnerabilities or roadblocks logged yet.",
                        fontSize = 12.sp,
                        color = BentoSubduedText,
                        fontStyle = FontStyle.Italic
                    )
                } else {
                    mistakes.forEach { mistake ->
                        val cardBg = if (mistake.isSolved) BentoAlabasterGreen.copy(alpha = 0.4f) else BentoErrorLightBg.copy(alpha = 0.3f)
                        val border = if (mistake.isSolved) BorderStroke(1.dp, BentoBorder) else BorderStroke(1.dp, BentoErrorBrand.copy(alpha = 0.15f))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .testTag("mistake_item_${mistake.id}"),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            shape = RoundedCornerShape(20.dp),
                            border = border,
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                color = if (mistake.isSolved) BentoPrimaryGreen else BentoErrorBrand,
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = if (mistake.isSolved) "RESOLVED" else mistake.impact.uppercase(),
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BentoWhite,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = mistake.description,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BentoTextDark
                                        )
                                    }

                                    if (idea.authorName == "You") {
                                        IconButton(
                                            onClick = { onDeleteMistake(mistake.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete blocker",
                                                tint = BentoErrorBrand.copy(alpha = 0.6f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                if (mistake.isSolved && mistake.solution != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(BentoWhite)
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "REMEDY PLAN",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BentoPrimaryGreen
                                            )
                                            Text(
                                                text = mistake.solution,
                                                fontSize = 12.sp,
                                                color = BentoTextDark,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }
                                }

                                if (mistake.aiAdvice != null && !mistake.isSolved) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(BentoAlabasterGreen)
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.AutoAwesome,
                                                    contentDescription = null,
                                                    tint = BentoPrimaryGreen,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "AI COLLABORATOR PLAN",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BentoPrimaryGreen
                                                )
                                            }
                                            Text(
                                                text = mistake.aiAdvice,
                                                fontSize = 11.sp,
                                                color = BentoDarkGreenText,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }
                                }

                                // Interactive Solvers
                                if (!mistake.isSolved && idea.authorName == "You") {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { onConsultAI(mistake) },
                                            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryGreen),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1.2f).height(32.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                        ) {
                                            if (isAILoading && aiSelectedMistakeId == mistake.id) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(14.dp),
                                                    color = BentoWhite,
                                                    strokeWidth = 2.dp
                                                )
                                            } else {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Consult AI", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                showSolveDialogForMistake = mistake
                                                manualSolutionText = ""
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = BentoBorder),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f).height(32.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                        ) {
                                            Text("Mark Solved", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BentoTextDark)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Resolve Challenge Dialog ---
    if (showSolveDialogForMistake != null) {
        val activeMistake = showSolveDialogForMistake!!
        Dialog(onDismissRequest = { showSolveDialogForMistake = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoWhite),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Resolve Blocker",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextDark
                    )
                    Text(
                        text = "Enter the strategy or code fix implemented to bypass this blocker:",
                        fontSize = 11.sp,
                        color = BentoSubduedText,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    OutlinedTextField(
                        value = manualSolutionText,
                        onValueChange = { manualSolutionText = it },
                        placeholder = { Text("What solution did you execute?", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("solution_input_dialog"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BentoBackground,
                            unfocusedContainerColor = BentoBackground,
                            focusedBorderColor = BentoPrimaryGreen,
                            unfocusedBorderColor = BentoBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showSolveDialogForMistake = null }) {
                            Text("Cancel", color = BentoSubduedText, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (manualSolutionText.isNotBlank()) {
                                    onSolveMistake(activeMistake, manualSolutionText)
                                    showSolveDialogForMistake = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Submit Remedy", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// COMPOSABLE TAB: AIStrategyReviewTab
// ==========================================
@Composable
fun AIStrategyReviewTab(
    idea: IdeaEntity?,
    isAILoading: Boolean,
    aiFeedbackText: String?,
    onPerformAudit: () -> Unit
) {
    if (idea == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = BentoPrimaryGreen.copy(alpha = 0.2f),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Audit Session Locked",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BentoTextDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Select an idea first to run the AI collaborator's detailed strategy review.",
                fontSize = 13.sp,
                color = BentoSubduedText,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BentoAlabasterGreen),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = BentoPrimaryGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Collaborator Audit",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoDarkGreenText
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Request our generative model to examine your active session, value proposition, milestones, and target demographics to refine strategy.",
                    fontSize = 12.sp,
                    color = BentoPrimaryGreen
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onPerformAudit,
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryGreen),
                    modifier = Modifier.fillMaxWidth().testTag("perform_audit_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (isAILoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = BentoWhite,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Launch Full Strategy Audit", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (aiFeedbackText != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BentoWhite),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, BentoBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Refinement Plan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = aiFeedbackText,
                        fontSize = 13.sp,
                        color = BentoSubduedText,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// ==========================================
// COMPOSABLE: CreateIdeaDialog
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateIdeaDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, desc: String, category: String, audience: String, valueProp: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Tech") }
    var audience by remember { mutableStateOf("") }
    var valueProp by remember { mutableStateOf("") }

    val categoriesList = listOf("Tech", "Business", "Creative", "Social", "Other")
    var categoryExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = BentoWhite),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, BentoBorder),
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Forge New Idea",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextDark
                )
                Text(
                    text = "Initiate your private workspace. You can publish to the community feed later.",
                    fontSize = 11.sp,
                    color = BentoSubduedText,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Idea Title", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("idea_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPrimaryGreen,
                        unfocusedBorderColor = BentoBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category dropdown using Material 3 ExposedDropdownMenuBox
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    modifier = Modifier.fillMaxWidth().testTag("idea_category_dropdown")
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = category,
                        onValueChange = {},
                        label = { Text("Category", fontSize = 12.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BentoPrimaryGreen,
                            unfocusedBorderColor = BentoBorder
                        ),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categoriesList.forEach { categorySelection ->
                            DropdownMenuItem(
                                text = { Text(categorySelection) },
                                onClick = {
                                    category = categorySelection
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Description Input
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Concept Description", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().height(80.dp).testTag("idea_desc_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPrimaryGreen,
                        unfocusedBorderColor = BentoBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Target Audience Input
                OutlinedTextField(
                    value = audience,
                    onValueChange = { audience = it },
                    label = { Text("Target Audience / Users", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("idea_audience_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPrimaryGreen,
                        unfocusedBorderColor = BentoBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Value Proposition Input
                OutlinedTextField(
                    value = valueProp,
                    onValueChange = { valueProp = it },
                    label = { Text("Value Proposition (Why it works)", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().height(80.dp).testTag("idea_valueprop_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPrimaryGreen,
                        unfocusedBorderColor = BentoBorder
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = BentoSubduedText, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank() && desc.isNotBlank()) {
                                onConfirm(title, desc, category, audience, valueProp)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("submit_idea_button")
                    ) {
                        Text("Create Workspace", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// EXTRA AUXILIARY COMPOSABLES
// ==========================================
@Composable
fun CategoryBadge(category: String) {
    Surface(
        color = when (category) {
            "Tech" -> Color(0xFFE3F2FD)
            "Business" -> Color(0xFFFFF3E0)
            "Creative" -> Color(0xFFF3E5F5)
            "Social" -> Color(0xFFE8F5E9)
            else -> Color(0xFFECEFF1)
        },
        contentColor = when (category) {
            "Tech" -> Color(0xFF1565C0)
            "Business" -> Color(0xFFE65100)
            "Creative" -> Color(0xFF4A148C)
            "Social" -> Color(0xFF1B5E20)
            else -> Color(0xFF37474F)
        },
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = category,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun StatusBadge(status: String) {
    Surface(
        color = when (status) {
            "Drafting" -> Color(0xFFECEFF1)
            "Active" -> Color(0xFFE8F5E9)
            "Completed" -> Color(0xFFE3F2FD)
            else -> Color(0xFFFFF3E0)
        },
        contentColor = when (status) {
            "Drafting" -> Color(0xFF455A64)
            "Active" -> Color(0xFF2E7D32)
            "Completed" -> Color(0xFF1565C0)
            else -> Color(0xFFEF6C00)
        },
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = status,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// ==========================================
// COMPOSABLE: FacebookAuthWebDialog
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacebookAuthWebDialog(
    onDismiss: () -> Unit,
    onLoginSuccess: (name: String, email: String) -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: Credentials, 2: Permissions consent, 3: Success redirect
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F2F5)), // Facebook grey background
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFCCCCCC)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // --- Safe Browser Header ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE4E6EB))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Browser",
                        tint = Color(0xFF65676B),
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { onDismiss() }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Secure Connection",
                            tint = Color(0xFF31A24C),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "m.facebook.com/oauth/authorize",
                            fontSize = 11.sp,
                            color = Color(0xFF65676B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // --- Official Facebook Header Banner ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1877F2))
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "facebook",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-1).sp
                    )
                }

                // --- Web Content Area ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (step == 1) {
                        Text(
                            text = "Log in to use your Facebook account with IdeaForge",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C1E21),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                emailError = false
                            },
                            placeholder = { Text("Mobile number or email address", color = Color(0xFF8D949E), fontSize = 12.sp) },
                            singleLine = true,
                            isError = emailError,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("fb_email_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF1877F2),
                                unfocusedBorderColor = Color(0xFFCCD0D5)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        if (emailError) {
                            Text(
                                "Email or phone number is required",
                                color = Color.Red,
                                fontSize = 11.sp,
                                modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                passwordError = false
                            },
                            placeholder = { Text("Password", color = Color(0xFF8D949E), fontSize = 12.sp) },
                            singleLine = true,
                            isError = passwordError,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("fb_password_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF1877F2),
                                unfocusedBorderColor = Color(0xFFCCD0D5)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        if (passwordError) {
                            Text(
                                "Password is required",
                                color = Color.Red,
                                fontSize = 11.sp,
                                modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (email.isBlank()) emailError = true
                                if (password.isBlank()) passwordError = true
                                if (email.isNotBlank() && password.isNotBlank()) {
                                    step = 2
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("fb_login_submit"),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Log In", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFCCD0D5)))
                            Text("or", color = Color(0xFF8D949E), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp))
                            Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFCCD0D5)))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // One-tap quick login
                        Button(
                            onClick = {
                                email = "reddygowtham781@gmail.com"
                                password = "••••••••••••"
                                step = 2
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE7F3FF)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("fb_quick_login"),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1877F2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("G", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Continue as Gowtham Reddy", color = Color(0xFF1877F2), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                        }

                    } else if (step == 2) {
                        // Permission Flow
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = BentoPrimaryGreen,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(bottom = 8.dp)
                        )

                        Text(
                            text = "IdeaForge is requesting access to:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C1E21),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFE4E6EB)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                PermissionItem("Public profile info (Name & Profile photo)")
                                PermissionItem("Email address")
                                PermissionItem("Publishing access (To share ideas directly to Facebook)")
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onDismiss() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE4E6EB)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Cancel", color = Color(0xFF4B4F56), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            Button(
                                onClick = {
                                    step = 3
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(40.dp)
                                    .testTag("fb_confirm_permissions"),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Continue as Gowtham", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                    } else if (step == 3) {
                        // Success Redirect Animation
                        CircularProgressIndicator(
                            color = Color(0xFF1877F2),
                            modifier = Modifier
                                .size(44.dp)
                                .padding(bottom = 16.dp)
                        )

                        Text(
                            text = "Redirection to IdeaForge...",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C1E21)
                        )

                        Text(
                            text = "Securely importing Facebook Profile info",
                            fontSize = 12.sp,
                            color = Color(0xFF65676B),
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        androidx.compose.runtime.LaunchedEffect(Unit) {
                            kotlinx.coroutines.delay(1500)
                            // Call login on ViewModel
                            val finalName = if (email.contains("gowtham", ignoreCase = true) || email.contains("reddy", ignoreCase = true) || email.contains("781")) "Gowtham Reddy" else email.substringBefore("@")
                            onLoginSuccess(finalName, email.ifBlank { "reddygowtham781@gmail.com" })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF1877F2),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFF4B4F56),
            fontWeight = FontWeight.Medium
        )
    }
}

// ==========================================
// COMPOSABLE: FacebookShareComposerDialog
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacebookShareComposerDialog(
    idea: IdeaEntity,
    facebookUser: FacebookUser,
    onDismiss: () -> Unit,
    onShareConfirm: () -> Unit
) {
    var shareText by remember { mutableStateOf("I just forged a new idea on IdeaForge: ${idea.title}! 💡 Here is the value proposition: ${idea.valueProposition}") }
    var selectedAudience by remember { mutableStateOf("Public") }
    var isPosting by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFE4E6EB)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Composer Title Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0F2F5))
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Share on Facebook Feed",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1E21)
                    )
                }

                if (!isPosting) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // User info line
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1877F2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = facebookUser.name.take(1).uppercase(),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = facebookUser.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1C1E21)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                // Audience dropdown or simple pill
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFE4E6EB))
                                        .clickable {
                                            selectedAudience = if (selectedAudience == "Public") "Friends" else "Public"
                                        }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = if (selectedAudience == "Public") Icons.Default.Public else Icons.Default.Group,
                                        contentDescription = null,
                                        tint = Color(0xFF65676B),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "$selectedAudience ▾",
                                        fontSize = 10.sp,
                                        color = Color(0xFF65676B),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Sharing input box
                        OutlinedTextField(
                            value = shareText,
                            onValueChange = { shareText = it },
                            placeholder = { Text("What's on your mind?", color = Color(0xFF8D949E), fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .testTag("fb_share_input"),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF1877F2),
                                unfocusedBorderColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // High fidelity Idea Attachment Card (mimics real link preview on FB)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F2F5)),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFE4E6EB)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                // Dynamic visual banner background
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .background(BentoPrimaryGreen.copy(alpha = 0.15f))
                                        .padding(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = BentoPrimaryGreen.copy(alpha = 0.2f),
                                        modifier = Modifier
                                            .size(72.dp)
                                            .align(Alignment.CenterEnd)
                                    )
                                    Column(modifier = Modifier.align(Alignment.CenterStart)) {
                                        CategoryBadge(idea.category)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = idea.title,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BentoDarkGreenText
                                        )
                                    }
                                }
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "IDEAFORGE.APP",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF65676B),
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = idea.description,
                                        fontSize = 11.sp,
                                        color = Color(0xFF1C1E21),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = onDismiss) {
                                Text("Cancel", color = Color(0xFF65676B))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    isPosting = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.testTag("fb_share_post_btn")
                            ) {
                                Text("Post", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // Posting loading sequence
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF1877F2),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Sharing to Facebook Feed...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C1E21)
                        )

                        androidx.compose.runtime.LaunchedEffect(Unit) {
                            kotlinx.coroutines.delay(1800)
                            onShareConfirm()
                        }
                    }
                }
            }
        }
    }
}
