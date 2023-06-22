package com.example.myapplication.data.repository.firebaseImpl

import com.example.myapplication.data.models.User
import com.example.myapplication.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.myapplication.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import safeCall

class AuthRepositoryFirebase : AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userRef = FirebaseFirestore.getInstance().collection("users")

    override suspend fun currentUser(): Resource<Any> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val user = userRef.document(firebaseAuth.currentUser!!.uid).get().await().toObject(User::class.java)
                Resource.success(user!!)
            }
        }
    }

    override suspend fun login(email: String, password: String): Resource<Any> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result  = firebaseAuth.signInWithEmailAndPassword(email,password).await()
                val user = userRef.document(result.user?.uid!!).get().await().toObject(User::class.java)!!
                Resource.success(user)
            }
        }
    }

    override suspend fun createUser(
        userEmail: String,
        userLoginPass: String
    ): Resource<Any>{
        return withContext(Dispatchers.IO) {
            safeCall {
                val registrationResult  = firebaseAuth.createUserWithEmailAndPassword(userEmail,userLoginPass).await()
                val userId = registrationResult.user?.uid!!
                val newUser = User(userEmail,userLoginPass)
                userRef.document(userId).set(newUser).await()
                Resource.success(newUser)
            }

        }


    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}