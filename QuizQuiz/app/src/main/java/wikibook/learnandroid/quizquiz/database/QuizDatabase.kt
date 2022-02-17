package wikibook.learnandroid.quizquiz.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Quiz::class], version = 1)
@TypeConverters(StringListTypeConverter::class)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun quizDAO() : QuizDAO

    companion object {
        private var INSTANCE: QuizDatabase? = null

        fun getInstance(context: Context): QuizDatabase {
            if(INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, QuizDatabase::class.java, "database.db").build()
            }

            return INSTANCE!!
        }
    }
}