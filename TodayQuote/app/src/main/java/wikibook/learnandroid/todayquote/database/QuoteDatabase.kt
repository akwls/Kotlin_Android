package wikibook.learnandroid.todayquote.database

import android.content.Context
import androidx.room.*

@Dao
interface QuoteDAO {
    @Insert
    fun insert(quote: Quote): Long

    @Update
    fun update(quote: Quote)

    @Delete
    fun delete(quote: Quote)

    @Query("SELECT * FROM quote")
    fun getAll() : List<Quote>
}

@Database(entities = [Quote::class], version = 1)
abstract class QuoteDatabase: RoomDatabase() {
    abstract fun quoteDAO() : QuoteDAO

    companion object {
        private var INSTANCE: QuoteDatabase?= null

        fun getInstance(context: Context): QuoteDatabase {
            if(INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, QuoteDatabase::class.java, "database.db").build()
            }

            return INSTANCE!!
        }
    }
}