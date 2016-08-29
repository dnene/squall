package org.kotyle.squall.db.pool

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import java.sql.Connection

val log = LoggerFactory.getLogger("org.kotyle.squall.db.pool")

object ConnectionPool {
    var config = HikariConfig("hikari.properties")
    var ds = HikariDataSource(config)
   
    fun connection(): Connection = ds.connection
}

