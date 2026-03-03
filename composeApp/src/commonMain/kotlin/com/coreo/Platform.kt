package com.coreo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform