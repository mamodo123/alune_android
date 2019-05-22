package br.com.tibalt.tibalt.utilitarios;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class FirebaseUpdate {

    public static void updateUser(HashMap<Object, String> userInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        db.collection("users").document(user.getUid()).set(userInfo);
    }

}
