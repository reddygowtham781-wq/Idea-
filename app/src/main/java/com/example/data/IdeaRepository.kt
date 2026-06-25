package com.example.data

import kotlinx.coroutines.flow.Flow

class IdeaRepository(
    private val ideaDao: IdeaDao,
    private val workStepDao: WorkStepDao,
    private val mistakeDao: MistakeDao,
    private val commentDao: CommentDao
) {
    val allIdeas: Flow<List<IdeaEntity>> = ideaDao.getAllIdeas()

    suspend fun getIdeaById(id: Int): IdeaEntity? {
        return ideaDao.getIdeaById(id)
    }

    suspend fun insertIdea(idea: IdeaEntity): Long {
        return ideaDao.insertIdea(idea)
    }

    suspend fun updateIdea(idea: IdeaEntity) {
        ideaDao.updateIdea(idea)
    }

    suspend fun deleteIdea(id: Int) {
        ideaDao.deleteIdeaById(id)
        workStepDao.deleteStepsForIdea(id)
        mistakeDao.deleteMistakesForIdea(id)
        commentDao.deleteCommentsForIdea(id)
    }

    fun getStepsForIdea(ideaId: Int): Flow<List<WorkStepEntity>> {
        return workStepDao.getStepsForIdea(ideaId)
    }

    suspend fun insertStep(step: WorkStepEntity) {
        workStepDao.insertStep(step)
    }

    suspend fun updateStep(step: WorkStepEntity) {
        workStepDao.updateStep(step)
    }

    suspend fun deleteStep(id: Int) {
        workStepDao.deleteStepById(id)
    }

    fun getMistakesForIdea(ideaId: Int): Flow<List<MistakeEntity>> {
        return mistakeDao.getMistakesForIdea(ideaId)
    }

    suspend fun insertMistake(mistake: MistakeEntity): Long {
        return mistakeDao.insertMistake(mistake)
    }

    suspend fun updateMistake(mistake: MistakeEntity) {
        mistakeDao.updateMistake(mistake)
    }

    suspend fun deleteMistake(id: Int) {
        mistakeDao.deleteMistakeById(id)
    }

    fun getCommentsForIdea(ideaId: Int): Flow<List<CommentEntity>> {
        return commentDao.getCommentsForIdea(ideaId)
    }

    suspend fun insertComment(comment: CommentEntity) {
        commentDao.insertComment(comment)
    }

    suspend fun deleteComment(commentId: Int) {
        commentDao.deleteCommentById(commentId)
    }
}
