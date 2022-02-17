package wikibook.learnandroid.quizquiz.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "quiz")
class Quiz(var type: String?, var question: String?, var answer: String?, var category: String?,
    @TypeConverters(StringListTypeConverter::class)
    var guesses: List<String>?=null,
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readValue(Long::class.java.classLoader) as? Long
    ) {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeString(type)
        parcel.writeString(question)
        parcel.writeString(answer)
        parcel.writeString(category)
        parcel.writeStringList(guesses)
        parcel.writeValue(id)
    }

    companion object CREATOR : Parcelable.Creator<Quiz> {
        override fun createFromParcel(parcel: Parcel): Quiz {
            return Quiz(parcel)
        }

        override fun newArray(p0: Int): Array<Quiz?> {
            return arrayOfNulls(p0)
        }
    }



}

class StringListTypeConverter {
    @TypeConverter
    fun stringListToString(stringList: List<String>?) : String? {
        return stringList?.joinToString(",")
    }

    @TypeConverter
    fun stringToStringList(string: String?) : List<String>? {
        return string?.split(",")?.toList()
    }
}