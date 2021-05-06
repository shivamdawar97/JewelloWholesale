package com.s3enterprises.jewellowholesale.authentication

import android.content.Context
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

object AuthRepo {

    private val auth by lazy { Firebase.auth }

    fun sendVerificationCode(number:String,context: Context){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(context as LoginActivity)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}