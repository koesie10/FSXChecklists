package com.koenv.fsxchecklists.util

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

inline fun <reified T : Any> typeToken(): Type = object : TypeToken<T>() {}.type