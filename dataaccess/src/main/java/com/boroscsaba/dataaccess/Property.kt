package com.boroscsaba.dataaccess

/**
 * Created by Boros Csaba
 */

class Property<out T>(public val name: String, public val value: T?, public val type: PropertyType)
