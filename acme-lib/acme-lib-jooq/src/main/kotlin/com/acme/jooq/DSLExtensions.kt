package com.acme.jooq

import org.jooq.Field
import org.jooq.Record
import org.jooq.TableField
import org.jooq.impl.DSL

fun <R : Record, T> TableField<R, T>.asExcluded(): Field<T> = DSL.field("EXCLUDED.$name", dataType)
