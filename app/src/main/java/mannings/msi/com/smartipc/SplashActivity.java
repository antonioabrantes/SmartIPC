package mannings.msi.com.smartipc;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private Boolean timeout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        autenticacao = FirebaseAuth.getInstance();
        autenticacao.signInWithEmailAndPassword(
                "smartipc@gmail.com", "smartipc"
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SplashActivity.this,
                            "Usuário logado com sucesso !",
                            Toast.LENGTH_SHORT).show();
                } else {

                    String excecao = "Erro na autenticação";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        excecao = "Usuário não está cadastrado.";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                    } catch (Exception e) {
                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(SplashActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();

                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timeout = true;
                abrirAutenticacao();
            }
        }, 3000);

    }

    private void abrirAutenticacao() {
        Intent intent = new Intent(SplashActivity.this, ChooseIdiomas.class);
        startActivity(intent);
        finish();
    }
}