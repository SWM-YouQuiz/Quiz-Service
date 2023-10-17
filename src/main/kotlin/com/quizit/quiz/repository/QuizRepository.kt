package com.quizit.quiz.repository

import com.quizit.quiz.domain.Quiz
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface QuizRepository : ReactiveMongoRepository<Quiz, String> {
    fun findAllByChapterId(chapterId: String): Flux<Quiz>

    @Query("{'chapterId': ?0,'answerRate': {'\$gte': ?1, '\$lte': ?2 } }")
    fun findAllByChapterIdAndAnswerRateBetween(
        chapterId: String, minAnswerRate: Double, maxAnswerRate: Double, pageable: Pageable
    ): Flux<Quiz>

    fun findAllByWriterId(writerId: String): Flux<Quiz>

    fun findAllByIdIn(ids: List<String>): Flux<Quiz>

    fun findAllByQuestionContains(keyword: String): Flux<Quiz>

    @Aggregation(
        pipeline = [
            "{ \$set: { 'curriculumId': ?0 } }",
            "{ \$lookup: { from: 'course', localField: 'curriculumId', foreignField: 'curriculumId', as: 'courses' } }",
            "{ \$unwind: '\$courses' }",
            "{ \$lookup: { from: 'chapter', localField: 'courses._id', foreignField: 'courseId', as: 'chapters' } }",
            "{ \$unwind: '\$chapters' }",
            "{ \$lookup: { from: 'quiz', localField: 'chapters._id', foreignField: 'chapterId', as: 'quizzes' } }",
            "{ \$unwind: '\$quizzes' }",
            "{ \$group: { _id: '\$quizzes._id', document: { \$first: '\$quizzes' } } }",
            "{ \$replaceRoot: { newRoot: '\$document' } }"
        ]
    )
    fun findAllByCurriculumId(curriculumId: String): Flux<Quiz>

    @Aggregation(
        pipeline = [
            "{ \$set: { 'courseId': ?0 } }",
            "{ \$lookup: { from: 'chapter', localField: 'courseId', foreignField: 'courseId', as: 'chapters' } }",
            "{ \$unwind: '\$chapters' }",
            "{ \$set: { 'chapters._id': { \$toString: '\$chapters._id' } } }",
            "{ \$lookup: { from: 'quiz', localField: 'chapters._id', foreignField: 'chapterId', as: 'quizzes' } }",
            "{ \$unwind: '\$quizzes' }",
            "{ \$group: { _id: '\$quizzes._id', document: { \$first: '\$quizzes' } } }",
            "{ \$replaceRoot: { newRoot: '\$document' } }"
        ]
    )
    fun findAllByCourseId(courseId: String): Flux<Quiz>
}