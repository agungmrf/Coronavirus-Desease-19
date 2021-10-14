package com.example.coronavirus.adapter

interface BaseView {
    fun showLoadingDialog(message: String?)

    fun hideLoadingDialog()
}