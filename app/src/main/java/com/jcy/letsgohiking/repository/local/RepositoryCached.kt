package com.jcy.letsgohiking.repository.local
import com.jcy.letsgohiking.util.GsonFactory
import java.lang.reflect.Type

/***
 * @see setRawValue(key: LocalKey, value: Any?)
 * value : Type is Any, Buy it must saved String to Local
 * @see getRawValue(key: LocalKey)
 * Return : it must be String type, Boolean is false or true String
 ***/
abstract class RepositoryCached {
    private val hashCached = HashMap<String, String?>()
    private val gson = GsonFactory.get()

    private fun <T : Any> getValue(key: LocalKey, defValue: T): T {
        return gson.fromJson(getCachedValue(key), defValue::class.java as Type) ?: defValue
    }

    private fun <T : Any> getGeneralValue(key: LocalKey, defValue: T): T {
        return (getCachedValue(key) ?: defValue) as T
    }

    fun setValue(key: LocalKey, value: Any?) {
        hashCached[key.name] = value?.toString()
        setRawValue(key, value)
    }

    fun isEqual(key: LocalKey, target: String): Boolean {
        return getRawValue(key) == target
    }

    private fun getCachedValue(key: LocalKey): String? {
        if (hashCached.containsKey(key.name)) {
            return hashCached[key.name]
        }

        val value = getRawValue(key)
        hashCached[key.name] = value
        return value
    }
    fun getUserClass() = getValue(LocalKey.USERCLASS, 0)
    fun getReviewCount() = getValue(LocalKey.REVIEWCOUNT, 5)
    fun getUserProfileImage() = getValue(LocalKey.USERPROFILEIMG,"")
    fun getUserId() = getGeneralValue(LocalKey.USERID , "")
    fun getUserName() = getGeneralValue(LocalKey.USERNAME , "")
    fun getLoginType() = getValue(LocalKey.LOGINTYPE , "")
    fun getGoogleToken() = getValue(LocalKey.GOOGLETOKEN,"")

    protected abstract fun setRawValue(key: LocalKey, value: Any?)
    protected abstract fun getRawValue(key: LocalKey): String?
    protected abstract fun clear()
}