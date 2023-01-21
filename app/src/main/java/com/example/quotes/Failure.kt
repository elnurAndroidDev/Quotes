package com.example.quotes

interface Failure {
    fun getHead(): String
    fun getMessage(): String
}

class NoConnection(private val resManager: ResourceManager) : Failure {
    override fun getHead() = resManager.getString(R.string.internet)
    override fun getMessage() = resManager.getString(R.string.no_connection)
}

class ServiceUnavailable(private val resManager: ResourceManager) : Failure {
    override fun getHead() = resManager.getString(R.string.service)
    override fun getMessage() = resManager.getString(R.string.service_unavailable)
}