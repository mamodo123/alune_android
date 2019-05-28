package br.com.tibalt.tibalt.utilitarios;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public abstract class SharedPref {

    private static final int MODE_PRIVATE = 0;

    public static void save(Context ct, String name, String key, String value) {
        SharedPreferences.Editor editor = ct.getSharedPreferences(name, MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void save(Context ct, String name, String key, Set<String> value) {
        SharedPreferences.Editor editor = ct.getSharedPreferences(name, MODE_PRIVATE).edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public static void save(Context ct, String name, String key, int value) {
        SharedPreferences.Editor editor = ct.getSharedPreferences(name, MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void save(Context ct, String name, String key, float value) {
        SharedPreferences.Editor editor = ct.getSharedPreferences(name, MODE_PRIVATE).edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static void save(Context ct, String name, String key, long value) {
        SharedPreferences.Editor editor = ct.getSharedPreferences(name, MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void save(Context ct, String name, String key, boolean value) {
        SharedPreferences.Editor editor = ct.getSharedPreferences(name, MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static String readString(Context ct, String name, String key) {
        SharedPreferences pref = ct.getSharedPreferences(name, MODE_PRIVATE);
        return pref.getString(key, null);
    }

    public static Set<String> readSet(Context ct, String name, String key) {
        SharedPreferences pref = ct.getSharedPreferences(name, MODE_PRIVATE);
        return pref.getStringSet(key, null);
    }

    public static int readInt(Context ct, String name, String key) {
        SharedPreferences pref = ct.getSharedPreferences(name, MODE_PRIVATE);
        return pref.getInt(key, -1);

    }

    public static float readFloat(Context ct, String name, String key) {
        SharedPreferences pref = ct.getSharedPreferences(name, MODE_PRIVATE);
        return pref.getFloat(key, -1);
    }

    public static long readLong(Context ct, String name, String key) {
        SharedPreferences pref = ct.getSharedPreferences(name, MODE_PRIVATE);
        return pref.getLong(key, -1);
    }

    public static boolean readBoolean(Context ct, String name, String key) {
        SharedPreferences pref = ct.getSharedPreferences(name, MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }

    public static void deleteName(Context ct, String name) {
        ct.getSharedPreferences(name, MODE_PRIVATE).edit().clear().commit();
    }

    public static void deleteKey(Context ct, String name, String key) {
        ct.getSharedPreferences(name, MODE_PRIVATE).edit().remove(key).commit();
    }

}
