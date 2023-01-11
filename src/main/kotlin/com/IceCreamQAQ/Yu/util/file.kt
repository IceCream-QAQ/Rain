package com.IceCreamQAQ.Yu.util

import java.io.File

fun exists(vararg names: String): Boolean {
    for (name in names) {
        File(name).let { if (it.exists()) return true }
    }
    return false
}

fun newFolder(name: String) {

}

fun newFolder(file: File, child: String) =
    File(file,child).apply {
        if (exists()) delete()
        mkdir()
    }