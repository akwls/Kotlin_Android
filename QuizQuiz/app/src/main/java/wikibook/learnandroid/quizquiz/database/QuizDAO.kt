package wikibook.learnandroid.quizquiz.database

import androidx.room.*

@Dao
interface QuizDAO {
    @Insert
    fun insert(quiz: Quiz): Long

    @Update
    fun update(quiz: Quiz)

    @Delete
    fun delete(quiz: Quiz)

    @Query("SELECT * FROM quiz")
    fun getAll() : List<Quiz>
}