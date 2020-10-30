package mannings.msi.com.smartipc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import mannings.msi.com.smartipc.config.ConfiguracaoFirebase;
import mannings.msi.com.smartipc.model.Ipc2019;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Classes extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> mensagens;
    private ArrayAdapter<String> adapter;
    private String symbol;
    private String kind;
    private String descricao;
    private int indice;
    private int proximo;
    private String texto;
    private int count_proximo;
    private String secao_escolhida;
    private String classe_escolhida;
    private String[] array_classes = new String[40];
    private int[] array_proximo = new int[40];
    private int proxima_tela;
    private String idioma;
    private Document doc;
    private Toolbar toolbar;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);
        listView=(ListView)findViewById(R.id.listViewClasses);
        toolbar = (Toolbar) findViewById(R.id.toolbarClasses);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences("status_app", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("app_encerrado", false);
        editor.apply();

        mensagens = new ArrayList<String>();
        adapter = new ArrayAdapter(Classes.this, android.R.layout.simple_list_item_1,mensagens);
        listView.setAdapter( adapter );
        mensagens.clear();

        Bundle extras = getIntent().getExtras();
        proxima_tela = extras.getInt("proxima_tela");
        secao_escolhida = extras.getString("secao_escolhida");
        idioma = extras.getString("idioma");

        //Toast.makeText(getBaseContext(), "SeÃ§Ã£o  "+proxima_tela, Toast.LENGTH_SHORT).show();

        try{
            AssetManager mngr = this.getAssets();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            if (idioma.equals("br"))
                doc = dBuilder.parse(mngr.open("xml/classes_br.xml"));
            else if (idioma.equals("en"))
                doc = dBuilder.parse(mngr.open("xml/classes_en.xml"));
            else
                doc = dBuilder.parse(mngr.open("xml/classes_fr.xml"));

            doc.getDocumentElement().normalize();
            NodeList nodeContatos = doc.getElementsByTagName("row");
            int counter = nodeContatos.getLength();
            count_proximo=1;
            mensagens.add("<<<");
            for (int i = 0; i < counter; i++) {
                Node item = nodeContatos.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) item;
                    Node nodeNome = element.getElementsByTagName("field").item(0).getChildNodes().item(0);
                    nodeNome = element.getElementsByTagName("field").item(0).getChildNodes().item(0);
                    symbol = nodeNome.getNodeValue().toString();
                    nodeNome = element.getElementsByTagName("field").item(1).getChildNodes().item(0);
                    kind = nodeNome.getNodeValue().toString();
                    nodeNome = element.getElementsByTagName("field").item(2).getChildNodes().item(0);
                    descricao = nodeNome.getNodeValue().toString();
                    nodeNome = element.getElementsByTagName("field").item(3).getChildNodes().item(0);
                    indice = Integer.parseInt(nodeNome.getNodeValue().toString());
                    nodeNome = element.getElementsByTagName("field").item(4).getChildNodes().item(0);
                    proximo = Integer.parseInt(nodeNome.getNodeValue().toString());

                    if (indice==proxima_tela) {
                        Ipc2019 ipc2019 = new Ipc2019();
                        ipc2019.setSymbol(symbol);
                        ipc2019.setKind(kind);
                        ipc2019.setDescricao(descricao);
                        ipc2019.setIndice(indice);
                        ipc2019.setProximo(proximo);
                        array_proximo[count_proximo]=proximo;
                        array_classes[count_proximo]=symbol;
                        count_proximo = count_proximo+1;
                        texto = symbol + " " + descricao;
                        mensagens.add(texto);
                    }
                }
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    finish();
                }else {
                    Intent intent = new Intent(Classes.this, SubClasses.class);
                    proxima_tela = array_proximo[position];
                    classe_escolhida = array_classes[position];
                    intent.putExtra("classe_escolhida",classe_escolhida);
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
        Intent intent = new Intent(Classes.this,Pesquisa.class);
        intent.putExtra("idioma", idioma);
        startActivity(intent);
        //finish();
    }

    public void configuraIdiomas(){
        Intent intent = new Intent(Classes.this,ChooseIdiomas.class);
        startActivity(intent);
        finish();
    }

    public void vaParaTelaInicial(){
        Intent intent = new Intent(Classes.this,MainActivity.class);
        intent.putExtra("idioma", idioma);
        startActivity(intent);
        finish();
    }

    public void TelaAbout(){
        Intent intent = new Intent(Classes.this,About.class);
        intent.putExtra("idioma", idioma);
        startActivity(intent);
        finish();
    }

}
