package org.jooq.impl

import org.jooq.Field
import org.jooq.Record
import org.jooq.TableField

fun <R : Record, T> TableField<R, T>.asExcluded(): Field<T> = DSL.field("EXCLUDED.$name", dataType)
