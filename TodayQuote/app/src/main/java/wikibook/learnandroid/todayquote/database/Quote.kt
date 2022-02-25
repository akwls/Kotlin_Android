package wikibook.learnandroid.todayquote.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quote")
data class Quote(
    var text: String?, var from: String? = "",
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Long::class.java.classLoader) as? Long
        ){}

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeString(text)
        p0.writeString(from)
        p0.writeValue(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR: Parcelable.Creator<Quote> {
        override fun createFromParcel(p0: Parcel): Quote {
            return Quote(p0)
        }

        override fun newArray(p0: Int): Array<Quote?> {
            return arrayOfNulls(p0)
        }
    }

}