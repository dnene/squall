package org.kotyle.squall.db.types

import org.kotyle.squall.base.SquallException
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.sql.*

val log = LoggerFactory.getLogger("org.kotyle.squall.db.types")

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

object JdbcTypes {
    val map: MutableMap<Int, Type> = mutableMapOf()
    fun get(type: Int): Type = map.get(type) ?:
            throw SquallException("err-invalid-column-type",
                    mapOf("type" to type))
    open class Type(val type: Int, val maxLengthRequired: Boolean = false) {
        init {
            map.put(type, this)
        }
    }
    object TinyInt: Type(Types.TINYINT)
    object SmallInt: Type(Types.SMALLINT)
    object Integer: Type(Types.INTEGER)
    object BigInt: Type(Types.BIGINT)
    object Float: Type(Types.FLOAT)
    object Double: Type(Types.DOUBLE)
    object Decimal: Type(Types.DECIMAL)
    object Numeric: Type(Types.NUMERIC)
    object Bit: Type(Types.BIT)
    object Char: Type(Types.CHAR)
    object VarChar: Type(Types.VARCHAR, maxLengthRequired = true)
    object Binary: Type(Types.BINARY)
    object VarBinary: Type(Types.VARBINARY)
    object LongVarBinary: Type(Types.LONGVARBINARY)
    object Date: Type(Types.DATE)
    object Time: Type(Types.TIME)
    object Timestamp: Type(Types.TIMESTAMP)
    object Array: Type(Types.ARRAY)
    object Blob: Type(Types.BLOB)
    object Clob: Type(Types.CLOB)
    object Struct: Type(Types.STRUCT)
    object Ref: Type(Types.REF)
    object Class: Type(Types.JAVA_OBJECT)
}

val validTypes = mapOf(
        String::class to listOf(Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT,
                Types.FLOAT, Types.DOUBLE, Types.DECIMAL, Types.NUMERIC, Types.BIT,
                Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR,
                Types.BINARY, Types.VARBINARY, Types.LONGVARBINARY,
                Types.DATE, Types.TIME, Types.TIMESTAMP),
        BigDecimal::class to listOf(Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT,
                Types.FLOAT, Types.DOUBLE, Types.DECIMAL, Types.NUMERIC, Types.BIT,
                Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR),
        Boolean::class to listOf(Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT,
                Types.FLOAT, Types.DOUBLE, Types.DECIMAL, Types.NUMERIC, Types.BIT,
                Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR),
        Integer::class to listOf(Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT,
                Types.FLOAT, Types.DOUBLE, Types.DECIMAL, Types.NUMERIC, Types.BIT,
                Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR),
        Long::class to listOf(Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT,
                Types.FLOAT, Types.DOUBLE, Types.DECIMAL, Types.NUMERIC, Types.BIT,
                Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR),
        Float::class to listOf(Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT,
                Types.FLOAT, Types.DOUBLE, Types.DECIMAL, Types.NUMERIC, Types.BIT,
                Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR),
        Double::class to listOf(Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT,
                Types.FLOAT, Types.DOUBLE, Types.DECIMAL, Types.NUMERIC, Types.BIT,
                Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR),
        ByteArray::class to listOf(Types.BINARY, Types.VARBINARY, Types.LONGVARBINARY),
        java.sql.Date::class to listOf(Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR, Types.DATE, Types.TIMESTAMP),
        java.sql.Time::class to listOf(Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR, Types.TIME),
        java.sql.Timestamp::class to listOf(Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR, Types.DATE, Types.TIMESTAMP),
        //Array<*>::class to listOf(Types.ARRAY),
        Blob::class to listOf(Types.BLOB),
        Clob::class to listOf(Types.BLOB),
        Struct::class to listOf(Types.BLOB),
        Ref::class to listOf(Types.BLOB),
        Class::class to listOf(Types.BLOB)
)