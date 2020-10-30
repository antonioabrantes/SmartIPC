package mannings.msi.com.smartipc;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import mannings.msi.com.smartipc.config.ConfiguracaoFirebase;
import mannings.msi.com.smartipc.model.Ipc2019;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GruposKindB extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> mensagens;
    private ArrayAdapter<String> adapter;
    DatabaseReference ref;
    private ValueEventListener valueEventListenerMensagem;
    private int[] array_proximo = new int[100];
    private String[] array_grupos = new String[100];
    private int count_proximo;
    private int proxima_tela;
    private String idioma;
    private String classe_escolhida;
    private String subclasse_escolhida;
    private String secao_escolhida;
    private String descricao;
    private Toolbar toolbar;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupos_kind_b);

        listView=(ListView)findViewById(R.id.listViewGruposKindB);
        toolbar = (Toolbar) findViewById(R.id.toolbarKindB);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences("status_app", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("app_encerrado", false);
        editor.apply();

        mensagens = new ArrayList<String>();
        adapter = new ArrayAdapter(GruposKindB.this, android.R.layout.simple_list_item_1,mensagens);
        listView.setAdapter( adapter );
        mensagens.clear();
        mensagens.add("<<<");
        adapter.notifyDataSetChanged();

        Bundle extras = getIntent().getExtras();
        proxima_tela = extras.getInt("proxima_tela");
        idioma = extras.getString("idioma");
        secao_escolhida = extras.getString("secao_escolhida");
        classe_escolhida = extras.getString("classe_escolhida");
        subclasse_escolhida = extras.getString("subclasse_escolhida");
        //Toast.makeText(getBaseContext(), "Grupo:  "+proximo_grupos_main+", Subclasse: "+proximo_subclasses+"Suclasse: "+subclasse_escolhida, Toast.LENGTH_SHORT).show();

        ref = ConfiguracaoFirebase.getFirebase();
        Query query1 = ref.child("ipc_en_2019").child(secao_escolhida).
                child(classe_escolhida).child(subclasse_escolhida).orderByChild("indice").equalTo(proxima_tela);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                count_proximo = 1;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Ipc2019 ipcsymbol_lido = ds.getValue(Ipc2019.class);
                    String symbol = ipcsymbol_lido.getSymbol().toString();
                    String subclasse = symbol.substring(0,4);
                    int grupo =  Integer.parseInt(symbol.substring(4,8));
                    String subgrupo = symbol.substring(8,14);
                    String resultado = new String("");
                    int pos = 1;
                    int interromper = 0;
                    String str1 = new String("");
                    for (int i=5;i>=1;i--){
                        String digito = subgrupo.substring(i,i+1);
                        if (digito.equals("0")){
                            if (interromper==0 && i<=1) pos = i;
                        }else{
                            if (interromper==0) {
                                pos = i;
                                interromper = 1;
                            }
                        }
                    }
                    subgrupo = subgrupo.substring(0,pos+1);
                    String str = subclasse + " " + grupo + "/" + subgrupo;
                    str1 = str;
                    String kind = ipcsymbol_lido.getKind().toString();
                    if (kind.equals("m")) str = str + "";
                    if (kind.equals("1")) str = str + " .";
                    if (kind.equals("2")) str = str + " ..";
                    if (kind.equals("3")) str = str + " ...";
                    if (kind.equals("4")) str = str + " ....";
                    if (kind.equals("5")) str = str + " .....";
                    if (kind.equals("6")) str = str + " ......";
                    if (kind.equals("7")) str = str + " .......";
                    if (kind.equals("8")) str = str + " ........";
                    if (kind.equals("9")) str = str + " .........";
                    if (kind.equals("A")) str = str + " ..........";
                    if (kind.equals("B")) str = str + " ...........";

                    if (idioma.equals("br"))
                        descricao = ipcsymbol_lido.getDescricaobr().toString();
                    else if (idioma.equals("fr"))
                        descricao = ipcsymbol_lido.getDescricaofr().toString();
                    else
                        descricao = ipcsymbol_lido.getDescricao().toString();

                    int indice = ipcsymbol_lido.getIndice();
                    int proximo = ipcsymbol_lido.getProximo();
                    str = str + " " + descricao;
                    mensagens.add(str);
                    array_proximo[count_proximo]=proximo;
                    array_grupos[count_proximo]=str1;
                    count_proximo++;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Se ocorrer um erro
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    finish();
                }else {
                    if (array_proximo[position]==0) {
                        String str2 = array_grupos[position];
                        if (idioma.equals("br"))
                            Toast.makeText(getBaseContext(), "Este grupo "+str2+" não tem subgrupos ", Toast.LENGTH_LONG).show();
                        else if (idioma.equals("fr"))
                            Toast.makeText(getBaseContext(), "Ce groupe "+str2+" ne dispose pas de sous-groupes ", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getBaseContext(), "This group "+str2+" doesn't have any subgroups ", Toast.LENGTH_LONG).show();
                    }
                    else {
                        finish();
                    }
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
        Intent intent = new Intent(GruposKindB.this,Pesquisa.class);
        intent.putExtra("idioma", idioma);
        startActivity(intent);
        //finish();
    }

    public void configuraIdiomas(){
        Intent intent = new Intent(GruposKindB.this,ChooseIdiomas.class);
        startActivity(intent);
        finish();
    }

    public void vaParaTelaInicial(){
        Intent intent = new Intent(GruposKindB.this,MainActivity.class);
        intent.putExtra("idioma", idioma);
        startActivity(intent);
        finish();
    }

    public void TelaAbout(){
        Intent intent = new Intent(GruposKindB.this,About.class);
        intent.putExtra("idioma", idioma);
        startActivity(intent);
        finish();
    }

}

