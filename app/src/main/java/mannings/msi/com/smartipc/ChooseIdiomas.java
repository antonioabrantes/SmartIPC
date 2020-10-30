package mannings.msi.com.smartipc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.Locale;

public class ChooseIdiomas extends AppCompatActivity {

    private ImageView imagemBrasil;
    private ImageView imagemEngland;
    private ImageView imagemFrance;
    private String idioma = new String("br");
    private Toolbar toolbar;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_idiomas);
        imagemBrasil = (ImageView)findViewById(R.id.imageViewBrasil);
        imagemEngland = (ImageView)findViewById(R.id.imageViewEngland);
        imagemFrance = (ImageView)findViewById(R.id.imageViewFrance);
        toolbar = (Toolbar) findViewById(R.id.toolbarChooseIdiomas);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences("status_app", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("app_encerrado", false);
        editor.apply();

        // https://stackoverflow.com/questions/4985805/set-locale-programmatically

        imagemBrasil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idioma = "br";
                Resources res = getResources();
                Configuration config = res.getConfiguration();
                DisplayMetrics displayMetrics = res.getDisplayMetrics();
                config.locale = new Locale("pt");
                res.updateConfiguration(config,displayMetrics);
                Intent intent = new Intent(ChooseIdiomas.this, MainActivity.class);
                intent.putExtra("idioma", idioma );
                startActivity(intent);
            }
        });

        imagemEngland.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idioma = "en";
                Resources res = getResources();
                Configuration config = res.getConfiguration();
                DisplayMetrics displayMetrics = res.getDisplayMetrics();
                config.locale = new Locale("en");
                res.updateConfiguration(config,displayMetrics);
                Intent intent = new Intent(ChooseIdiomas.this, MainActivity.class);
                intent.putExtra("idioma", idioma );
                startActivity(intent);
            }
        });

        imagemFrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idioma = "fr";
                Resources res = getResources();
                Configuration config = res.getConfiguration();
                DisplayMetrics displayMetrics = res.getDisplayMetrics();
                config.locale = new Locale("fr");
                res.updateConfiguration(config,displayMetrics);
                Intent intent = new Intent(ChooseIdiomas.this, MainActivity.class);
                intent.putExtra("idioma", idioma );
                startActivity(intent);
            }
        });
    }

    protected void onStart(){
        super.onStart();
        preferences = getSharedPreferences("status_app", MODE_PRIVATE);
        if (preferences.getBoolean("app_encerrado",true)) finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.item_sair:
                preferences = getSharedPreferences("status_app", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("app_encerrado", true);
                editor.apply();
                finish();
                return true;
            case R.id.item_pesquisa:
                pesquisa();
                return true;
            case R.id.item_idiomas:
                //configuraIdiomas();
                return true;
            case R.id.item_inicio:
                vaParaTelaInicial();
                return true;
            case R.id.item_about:
                TelaAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    public void pesquisa(){
        Intent intent = new Intent(ChooseIdiomas.this,Pesquisa.class);
        intent.putExtra("idioma", idioma);
        startActivity(intent);
        //finish();
    }

    public void configuraIdiomas(){
        Intent intent = new Intent(ChooseIdiomas.this,ChooseIdiomas.class);
        startActivity(intent);
        finish();
    }

    public void vaParaTelaInicial(){
        Intent intent = new Intent(ChooseIdiomas.this,MainActivity.class);
        intent.putExtra("idioma", idioma);
        startActivity(intent);
        finish();
    }

    public void TelaAbout(){
        Intent intent = new Intent(ChooseIdiomas.this,About.class);
        intent.putExtra("idioma", idioma);
        startActivity(intent);
        finish();
    }

}

