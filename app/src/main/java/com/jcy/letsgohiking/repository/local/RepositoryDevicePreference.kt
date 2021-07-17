package com.jcy.letsgohiking.repository.local

import android.content.Context

class RepositoryDevicePreference(context: Context) : RepositoryCached() {
    private val sharedPref = context.getSharedPreferences("shared", Context.MODE_PRIVATE)

    override fun setRawValue(key: LocalKey, value: Any?) {
        with(sharedPref.edit()) {
            putString(key.name, value.toString())
            apply()
        }
    }

    override fun getRawValue(key: LocalKey): String? {
        return sharedPref.getString(key.name, "")
    }

    override fun clear() {
        sharedPref.edit().clear().apply()
    }
}