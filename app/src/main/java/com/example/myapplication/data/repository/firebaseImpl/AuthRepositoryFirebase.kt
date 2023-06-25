package com.example.myapplication.data.repository.firebaseImpl

import com.example.myapplication.data.local_db.MovieDao
import com.example.myapplication.data.models.User
import com.example.myapplication.data.remote_db.MovieRemoteDataSource
import com.example.myapplication.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.myapplication.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import safeCall
import javax.inject.Inject
import javax.inject.Singleton



class AuthRepositoryFirebase : AuthRepository{

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userRef = FirebaseFirestore.getInstance().collection("users")

//class AuthRepositoryFirebase @Inject constructor(
//    private val firebaseAuth : FirebaseAuth,
//    private val userRef : FirebaseFirestore) : AuthRepository{

    override suspend fun currentUser(): Resource<User> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val user = userRef.document(firebaseAuth.currentUser!!.uid).get().await().toObject(User::class.java)
                Resource.success(user!!)
            }
        }
    }

    override suspend fun login(email: String, password: String): Resource<User> {
        return withContext(Dispatchers.IO) {
            val result  = firebaseAuth.signInWithEmailAndPassword(email,password).await()
            Resource.success(User(email))
//            safeCall {
//                val result  = firebaseAuth.signInWithEmailAndPassword(email,password).await()
//                val user = userRef.document(result.user?.uid!!).get().await().toObject(User::class.java)!!
//                Resource.success(user)
//            }
        }
    }

    override suspend fun createUser(
        userEmail: String,
        userLoginPass: String
    ): Resource<User>{
        return withContext(Dispatchers.IO) {
            safeCall {
                val registrationResult  = firebaseAuth.createUserWithEmailAndPassword(userEmail,userLoginPass).await()
                val userId = registrationResult.user?.uid!!
                val newUser = User(userEmail)
//                userRef.document(userId).set(newUser).await()
                launch {
                    userRef.document(userId).set(newUser)
                }
                Resource.success(newUser)
            }

        }
    }
    override fun logout() {
        firebaseAuth.signOut()
    }
}