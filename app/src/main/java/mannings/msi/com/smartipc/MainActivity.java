package mannings.msi.com.smartipc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> mensagens;
    private ArrayAdapter<String> adapter;
    private String idioma;
    private String[] descricao;
    private String secao;
    private String secao_escolhida;
    private int proxima_tela;
    private Toolbar toolbar;
    private int[] array_proximo = {0,1,2696,8372,13439,14417,15570,18859,21749};
    //private int[] array_proximo = {0,1,2696,8220,13287,14265,15418,18707,21597};
    private String[] array_secao = {"","A","B","C","D","E","F","G","H"};
    private int proximo_classes;
    SharedPreferences preferences;

    private String[] descricao_br = {
            "SEÇÃO A — NECESSIDADES HUMANAS",
            "SEÇÃO B — OPERAÇÕES DE PROCESSAMENTO; TRANSPORTE",
            "SEÇÃO C — QUÍMICA; METALURGIA",
            "SEÇÃO D — TÊXTEIS; PAPEL",
            "SEÇÃO E — CONSTRUÇÕES FIXAS",
            "SEÇÃO F — ENGENHARIA MECÂNICA; ILUMINAÇÃO; AQUECIMENTO; ARMAS; EXPLOSÃO",
            "SEÇÃO G — FÍSICA",
            "SEÇÃO H — ELETRICIDADE"
    };

    private String[] descricao_en = {
            "SECTION A — HUMAN NECESSITIES",
            "SECTION B - PERFORMING OPERATIONS; TRANSPORTING",
            "SECTION C - CHEMISTRY; METALLURGY",
            "SECTION D - TEXTILES; PAPER",
            "SECTION E - FIXED CONSTRUCTIONS",
            "SECTION F - MECHANICAL ENGINEERING; LIGHTING; HEATING; WEAPONS; BLASTING",
            "SECTION G - PHYSICS",
            "SECTION H - ELECTRICITY"
    };

    private String[] descricao_fr = {
            "SECTION A - NÉCESSITÉS COURANTES DE LA VIE",
            "SECTION B - TECHNIQUES INDUSTRIELLES; TRANSPORTS",
            "SECTION C - CHIMIE; MÉTALLURGIE",
            "SECTION D - TEXTILES; PAPIER",
            "SECTION E - CONSTRUCTIONS FIXES",
            "SECTION F - MÉCANIQUE; ÉCLAIRAGE; CHAUFFAGE; ARMEMENT; SAUTAGE",
            "SECTION G - PHYSIQUE",
            "SECTION H - ÉLECTRICITÉ"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=(ListView)findViewById(R.id.listView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences("status_app", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("app_encerrado", false);
        editor.apply();

        Bundle extras = getIntent().getExtras();
        idioma = extras.getString("idioma");

        if (idioma.equals("br")) descricao = descricao_br;
        if (idioma.equals("en")) descricao = descricao_en;
        if (idioma.equals("fr")) descricao = descricao_fr;

        mensagens = new ArrayList<String>();
        adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,mensagens);
        listView.setAdapter( adapter );
        mensagens.clear();
        mensagens.add("<<<");
        for (int i=0; i<descricao.length; i++)
        {
            mensagens.add(descricao[i]);
        }
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    Intent intent = new Intent(MainActivity.this, ChooseIdiomas.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(MainActivity.this, Classes.class);
                    proxima_tela = array_proximo[position];
                    secao_escolhida = array_secao[position];
                    intent.putExtra("secao_escolhida", secao_escolhida);
                    intent.putExtra("proxima_tela", proxima_tela);
                    intent.putExtra("idioma", idioma);
                    startActivity(intent);
                }
            }
        });

    }

    protected void onStart(){
        super.onStart();
        preferences = getSharedPreferences("status_app", MODE_PRIVATE);
        if (preferences.getBoolean("app_encerrado",false)) finish();
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
                configuraIdiomas();
                return true;
            case R.id.item_inicio:
                //vaParaTelaInicial();
                return true;
            case R.id.item_about:
                TelaAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    public void pesquisa(){
        Intent intent = new Intent(MainActivity.this,Pesquisa.class);
        intent.putExtra("idioma", idioma);
        startActivity(intent);
        //finish();
    }

    public void configuraIdiomas(){
        Intent intent = new Intent(MainActivity.this,ChooseIdiomas.class);
        startActivity(intent);
        finish();
    }

    public void vaParaTelaInicial(){
        Intent intent = new Intent(MainActivity.this,MainActivity.class);
        intent.putExtra("idioma", idioma);
        startActivity(intent);
        finish();
    }

    public void TelaAbout(){
        Intent intent = new Intent(MainActivity.this,About.class);
        intent.putExtra("idioma", idioma);
        startActivity(intent);
        finish();
    }

}

