package demo

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.ColumnInfo
import java.util.*

@Entity(tableName = "stock_updates")
data class StockUpdateTable (
    @PrimaryKey
    @ColumnInfo(name = "_id") var id: String = "",
    @ColumnInfo(name = "stock_symbol") var stockSymbol: String = "",
    @ColumnInfo(name = "price") var price: String = "",
    @ColumnInfo(name = "date") var date: String = ""
)

