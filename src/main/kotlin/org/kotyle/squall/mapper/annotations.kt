package org.kotyle.squall.mapper.annotations

import org.kotyle.squall.db.column.GenerationStrategy


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Entity(val tableName: String)

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Column(val name: String, val type: Int, val autoGenerated: GenerationStrategy = GenerationStrategy.NONE, val maxLength: Int = 0)
