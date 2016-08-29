package org.kotyle.squall.mapper.scanner

import org.kotyle.squall.base.SquallException
import org.kotyle.squall.mapper.annotations.Column
import org.kotyle.squall.mapper.annotations.Entity

object Introspector{
    fun examine(klass: Class<*>): Pair<Entity, List<Pair<String, Column>>>? {
        val entities = klass.declaredAnnotations.filterIsInstance<Entity>().map {
            it as Entity
        }
        return when(entities.size) {
            0 -> null
            1 -> {
                Pair(entities.get(0), klass.declaredFields.map { fld ->
                    val annots = fld.annotations.filterIsInstance<Column>().map {
                        it as Column
                    }
                    when (annots.size) {
                        0 -> null
                        1 -> Pair(fld.name, annots.get(0))
                        else -> throw SquallException("err-multiple-column-annotations",
                                mapOf("class-name" to klass.name,
                                        "field-name" to fld.name,
                                        "annotation-count" to annots.size))
                    }
                }.filterIsInstance<Pair<String, Column>>())
            }
            else -> throw SquallException("err-multiple-entity-declarations",
                    mapOf("class-name" to klass.name,
                            "entity-count" to entities.size))
        }
    }
}
