package com.quizit.quiz.repository

import com.quizit.quiz.domain.Curriculum
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CurriculumRepository : CoroutineCrudRepository<Curriculum, String>