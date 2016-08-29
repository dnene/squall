package org.kotyle.squall.db.orm

import org.slf4j.LoggerFactory

val log = LoggerFactory.getLogger("org.kotyle.squall.db")


//class Column(val name: String, val type: Types, val maxLen: Short? = null, val dbType: Types? = null) {
//    fun dbToObj(dbVal: Any) = type.dbToObj(dbVal)
//
//}
//
//fun <R> query(transaction: Transaction, name: String, colNames: String, columns: List<Column>, colTypes: List<Types>, cls: Class<*>): List<R> {
//    return transaction.query("select ${colNames} from ${name}") { rs ->
//        if (rs.metaData.columnCount != colTypes.size) throw Exception("Result Set incompatible with desired object")
//        val cons = cls.declaredConstructors.find {
//            it.parameterTypes.size == colTypes.size &&
//                    it.parameterTypes.zip(colTypes).all { it.second.classes.contains(it.first)}
//
//        } ?: throw Exception("No constructor found")
//
//        val params = mutableListOf<Any?>()
//        for(i in 0..columns.size-1) {
//            params.add(columns[i].dbToObj(rs.getObject(i+1)))
//        }
//        cons.newInstance(*params.toTypedArray()) as R
//    }
//}

//open class Table<T>(val _name: String, val klass: Class<T>){
//    class View<R>(val name: String, val columns: List<Column>, val kls: Class<R>) {
//        val colnames: String by lazy { columns.map{it.name}.joinToString(",") }
//        val colTypes: List<Types> by lazy { columns.map{it.type} }
//        fun query(transaction: Transaction): List<R> = query(transaction, name, colnames, columns, colTypes, kls)
//    }
//    private val _columns: MutableList<Column> = mutableListOf<Column>()
//    private fun initColumn(col: Column) {
//        _columns.add(col)
//    }
//    val _colnames: String by lazy { _columns.map{it.name}.joinToString(",") }
//    val _colTypes: List<Types> by lazy { _columns.map{it.type} }
//
//    fun varchar(name: String, maxLen: Short): Column = Column(name, Types.String, maxLen).apply { initColumn(this)}
//    fun booleanAsString(name: String, maxLen: Short): Column = Column(name, Types.Boolean, dbType = Types.String).apply { initColumn(this)}
//    fun date(name: String): Column = Column(name, Types.Date).apply { initColumn(this)}
//
//    fun <R> view(kls: Class<R>, vararg columns: Column): View<R> = View(_name, columns.toList(), kls)
//
//    fun query(transaction: Transaction): List<T> = query(transaction, _name, _colnames, _columns, _colTypes, klass)
//
//}
//
//
//enum class Types(val classes: List<Class<*>>, val dbToObj: (Any) -> Any) {
//    String(listOf(java.lang.String::class.java, kotlin.String::class.java), { dbVal: Any ->
//        when(dbVal) {
//            is java.lang.String -> dbVal
//            else -> throw Exception("Cannot convert ${dbVal.javaClass} to String")
//        }
//    }),
//    Boolean(listOf(true.javaClass, java.lang.Boolean::class.java), { dbVal: Any ->
//        when(dbVal) {
//            true, false -> dbVal
//            "y", "Y", "t", "T" -> true
//            "n", "N", "f", "F" -> false
//            else -> throw Exception("Cannot convert ${dbVal.javaClass} to Boolean")
//        }
//    }),
//    Date(listOf(java.sql.Date::class.java), { dbVal: Any ->
//        when(dbVal) {
//            is java.sql.Date -> dbVal
//            is java.sql.Timestamp -> Date(dbVal.time)
//            else ->throw Exception("Cannot convert ${dbVal.javaClass} to Date")
//        }
//    }),
//    Timestamp(listOf(java.sql.Timestamp::class.java), { dbVal: Any ->
//        when(dbVal) {
//            is java.sql.Date -> dbVal
//            is java.sql.Timestamp -> dbVal
//            else ->throw Exception("Cannot convert ${dbVal.javaClass} to Date")
//        }
//    })
//}
//
