package com.aceofhigh.stockoverview.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codinginflow.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Stock::class], version = 2)
abstract class StockDatabase : RoomDatabase() {

    abstract fun stockDao(): StockDao

    //called first time when the database is created.
    class Callback @Inject constructor(
        private val database: Provider<StockDatabase>,
        //tell dagger, it is not just a coroutineScope, it is the applicationScope we defined.
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            //operations like dao.insert needs coroutines to work.
            val dao = database.get().stockDao()

            //now, we can use insert in a Coroutine.
            applicationScope.launch {
                dao.insert(
                    Stock(
                        name = "KARTN",
                        buyPrice = 50.00,
                        currentPrice = 55.00,
                        lotSize = 500
                    )
                )
                dao.insert(
                    Stock(
                        name = "ULKER",
                        buyPrice = 18.00,
                        currentPrice = 17.50,
                        lotSize = 1000
                    )
                )
                dao.insert(
                    Stock(
                        name = "ISGYO",
                        buyPrice = 8.00,
                        currentPrice = 7.20,
                        lotSize = 2000
                    )
                )
                dao.insert(
                    Stock(
                        name = "ISCTR",
                        buyPrice = 3.25,
                        currentPrice = 4.25,
                        lotSize = 5000
                    )
                )
                dao.insert(
                    Stock(
                        name = "NUGYO",
                        buyPrice = 80.00,
                        currentPrice = 82.00,
                        lotSize = 200
                    )
                )
                dao.insert(
                    Stock(
                        name = "YKBNK",
                        buyPrice = 4.25,
                        currentPrice = 5.22,
                        lotSize = 10000
                    )
                )

            }
        }
    }
}