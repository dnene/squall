package org.kotyle.squall.db.transaction

import org.kotyle.squall.db.orm.log
import org.kotyle.squall.db.pool.ConnectionPool
import org.kotyle.squall.mapper.table.Table
import org.kotyle.squall.mapper.table.View
import java.sql.Connection
import java.sql.ResultSet


class TransactionStack() {
    val txns: MutableList<Transaction> = mutableListOf()
    fun isEmpty() = txns.size == 0
    fun new(detached: Boolean): Transaction =
            NewTransaction(ConnectionPool.connection()).apply {
                if (! detached) txns.add(0,this)
            }
    fun active(): Transaction =
            if (isEmpty()) throw Exception("err-no-transactions-in-progress")
            else PreactiveTransaction(txns.get(0)).apply {
                txns.add(0,this)
            }
    fun activeOrNew(): Transaction =
            if (isEmpty()) new(false) else active()
    fun closeLast(t: Transaction) =
            if (t == txns.get(0)) txns.removeAt(0) else throw Exception("err-provided-transaction-is-not-at-top-of-stack")

}

class TransactionRef: ThreadLocal<TransactionStack>() {
    override fun initialValue(): TransactionStack? {
        return TransactionStack()
    }
}

object TransactionManager {
    var transactionRef: TransactionRef = TransactionRef()
    fun new(detached: Boolean): Transaction = transactionRef.get().new(detached)
    fun active(): Transaction = transactionRef.get().active()
    fun activeOrNew(): Transaction = transactionRef.get().activeOrNew()
    fun closeLast(t: Transaction) = transactionRef.get().closeLast(t)

    fun <T> performWithTransaction(txn: Transaction, detached: Boolean, block: Transaction.() -> T): T {
        try {
            val result = txn.block()
            txn.commit()
            return result
        } catch (e: Exception) {
            txn.rollback()
            log.error("Noticed exception ${e} while closing transaction")
            throw e
        } finally {
            txn.closeConnection()
            if (! detached) closeLast(txn)
        }

    }
    fun <T> transactionally(isolationLevel: Int, retries: Int, block: Transaction.() -> T): T =
            performWithTransaction(activeOrNew(), false, block)

    fun <T> newTransactionally(isolationLevel: Int, retries: Int, detached: Boolean, block: Transaction.() -> T): T =
            performWithTransaction(new(detached), detached, block)
}

fun <T> ResultSet.fold(acc: T, block: (T, ResultSet) -> T) : T {
    var tmpacc = acc
    while(this.next()) {
        tmpacc = block(tmpacc,this)
    }
    return tmpacc
}

abstract class Transaction() {

    abstract val connection: Connection
    fun <T> query(sql: String, vararg params: Any, block:(ResultSet) -> T): List<T> {
        log.debug("Executing Query: ${sql} with args ${params.joinToString(",")}")
        return connection.prepareStatement(sql).apply {
            for ((index, value) in params.withIndex()) {
                this.setObject(index + 1, value)
            }
        }.executeQuery().fold(mutableListOf<T>()) { acc, rs ->
            acc.add(block(rs))
            acc
        }
    }
    fun updateSql(sql: String, vararg params: Any?): Int {
        log.debug("Executing Update: ${sql} with args ${params.joinToString(",")}")
        return connection.prepareStatement(sql).apply {
            for ((index, value) in params.withIndex()) {
                this.setObject(index + 1, value)
            }
        }.executeUpdate()
    }
    fun select(t: Table): List<Map<String,Any>> = select(t.v)
    fun select(v: View): List<Map<String,Any>> {
        return connection.prepareStatement(v.preparedSelect).executeQuery().fold(mutableListOf<Map<String, Any>>()) {
            acc, rs ->  acc.apply {
            this.add((1..rs.metaData.columnCount).map { Pair(rs.metaData.getColumnName(it),rs.getObject(it)) }.toMap())}
        }
    }
    fun <T> selectAs(v: View, block: (ResultSet) -> T): List<T> {
        return connection.prepareStatement(v.preparedSelect).executeQuery().fold(mutableListOf<T>()) {
            acc, rs -> acc.apply { this.add(block(rs))}
        }
    }
    abstract fun commit()
    abstract fun rollback()
    abstract fun closeConnection()
}

class NewTransaction(override val connection: Connection): Transaction() {
    override fun commit() = connection.commit()
    override fun rollback() = connection.rollback()
    override fun closeConnection() = connection.close()
}

class PreactiveTransaction(val transaction: Transaction): Transaction() {
    override val connection: Connection = transaction.connection
    override fun commit() {}
    override fun rollback() {}
    override fun closeConnection() {}
}

fun <T> transaction(block: Transaction.() -> T): T = transaction(Connection.TRANSACTION_REPEATABLE_READ, 3, block)
fun <T> transaction(isolationLevel: Int, retries: Int, block: Transaction.() -> T): T =
        TransactionManager.transactionally(isolationLevel, retries, block)

fun <T> newTransaction(block: Transaction.() -> T): T = newTransaction(Connection.TRANSACTION_REPEATABLE_READ, 3, false, block)
fun <T> detachedTransaction(block: Transaction.() -> T): T = newTransaction(Connection.TRANSACTION_REPEATABLE_READ, 3, true, block)
fun <T> newTransaction(isolationLevel: Int, retries: Int, detached: Boolean, block: Transaction.() -> T): T =
        TransactionManager.newTransactionally(isolationLevel, retries, detached, block)
