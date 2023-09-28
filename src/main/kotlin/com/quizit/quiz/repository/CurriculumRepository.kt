package com.quizit.quiz.repository

import com.quizit.quiz.domain.Curriculum
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CurriculumRepository : ReactiveMongoRepository<Curriculum, String>