package com.cursointermedio.myapplication.domain.exception

class TrainingNotFoundException(message: String) : Exception(message)

class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)

class TrainingUploadException(message: String) : Exception(message)

