package mannings.msi.com.smartipc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class GruposPesquisa extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> mensagens;
    private ArrayAdapter<String> adapter;
    private String descricao;
    private String str,str1;
    private String symbol;
    private String symbol1;
    private String idioma = new String("br");
    private String classe_escolhida;
    private String subclasse_escolhida;
    private String secao_escolhida;
    private String[] array_descricao;
    private String[] array_mensagem = new String[100];
    private int position_mensagens;
    private int position_mensagens_max;
    private String kind;
    private int indice;
    private int proximo;
    private int pos;
    private int proxima_tela;
    private int[] array_proximo = new int[100];
    private String[] array_grupos = new String[100];
    private int[] array_proximo_secao = {1,2696,8372,13439,14417,15570,18859,21749};
    private Toolbar toolbar;
    private Document doc;
    private Query query1,query2,query3,query4,query5,query6,query7,query8,query9,query10,query11,query12;
    DatabaseReference ref1;
    SharedPreferences preferences;

    private String[] descricao_br = {
            "SEÇÃO A — NECESSIDADES HUMANAS",
            "SEÇÃO B — OPERAÇÕES DE PROCESSAMENTO; TRANSPORTE",
            "SEÇÃO C — QUÍMICA; METALURGIA",
            "SEÇÃO D — TÊXTEIS; PAPEL",
            "SEÇÃO E — CONSTRUÇÕES FIXAS",
            "SEÇÃO F — ENGENHARIA MECÂNICA; ILUMINAÇÃO; AQUECIMENTO; ARMAS; EXPLOSÃO",
            "SEÇÃO G — FÍSICA",
            "SEÇÃO H — ELECTRICIDADE"
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
        setContentView(R.layout.activity_grupos_pesquisa);

        preferences = getSharedPreferences("status_app", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("app_encerrado", false);
        editor.apply();

        listView=(ListView)findViewById(R.id.listViewGruposPesquisa);
        toolbar = (Toolbar) findViewById(R.id.toolbarGruposPesquisa);
        setSupportActionBar(toolbar);
        mensagens = new ArrayList<String>();
        adapter = new ArrayAdapter(GruposPesquisa.this, android.R.layout.simple_list_item_1,mensagens);
        listView.setAdapter( adapter );
        mensagens.clear();
        array_proximo[0] = 0;
        array_mensagem[0] = "";
        mensagens.add("<<<"); // fica oculto pelo menu, equivale ao position_message = 0

        Bundle extras = getIntent().getExtras();
        symbol = extras.getString("symbol");
        symbol1 = symbol;
        idioma = extras.getString("idioma");
        secao_escolhida = extras.getString("secao_escolhida");
        classe_escolhida = extras.getString("classe_escolhida");
        subclasse_escolhida = extras.getString("subclasse_escolhida");

        if (idioma.equals("br")) array_descricao = descricao_br;
        if (idioma.equals("en")) array_descricao = descricao_en;
        if (idioma.equals("fr")) array_descricao = descricao_fr;

        if (secao_escolhida.equals("A")) pos=0;
        if (secao_escolhida.equals("B")) pos=1;
        if (secao_escolhida.equals("C")) pos=2;
        if (secao_escolhida.equals("D")) pos=3;
        if (secao_escolhida.equals("E")) pos=4;
        if (secao_escolhida.equals("F")) pos=5;
        if (secao_escolhida.equals("G")) pos=6;
        if (secao_escolhida.equals("H")) pos=7;

        str=array_descricao[pos];
        position_mensagens = 1;
        array_proximo[position_mensagens] = array_proximo_secao[pos];
        array_mensagem[position_mensagens] = str;
        mensagens.add(str);

        try{
            AssetManager mngr = this.getAssets();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            if (idioma.equals("br"))
                doc = dBuilder.parse(mngr.open("xml/classes_br.xml"));
            else if (idioma.equals("fr"))
                doc = dBuilder.parse(mngr.open("xml/classes_fr.xml"));
            else
                doc = dBuilder.parse(mngr.open("xml/classes_en.xml"));

            doc.getDocumentElement().normalize();
            NodeList nodeContatos = doc.getElementsByTagName("row");
            int counter = nodeContatos.getLength();
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

                    if (classe_escolhida.equals(symbol)) {
                        position_mensagens = 2;
                        str = classe_escolhida + " " + descricao;
                        array_proximo[position_mensagens] = proximo;
                        array_mensagem[position_mensagens] = str;
                        mensagens.add(str);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try{
            AssetManager mngr = this.getAssets();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            if (idioma.equals("br"))
                doc = dBuilder.parse(mngr.open("xml/subclasses_br.xml"));
            else if (idioma.equals("fr"))
                doc = dBuilder.parse(mngr.open("xml/subclasses_fr.xml"));
            else
                doc = dBuilder.parse(mngr.open("xml/subclasses_en.xml"));

            doc.getDocumentElement().normalize();
            NodeList nodeContatos = doc.getElementsByTagName("row");
            int counter = nodeContatos.getLength();
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

                    if (subclasse_escolhida.equals(symbol)) {
                        position_mensagens = 3;
                        str = subclasse_escolhida + " " + descricao;
                        array_proximo[position_mensagens] = proximo;
                        array_mensagem[position_mensagens] = str;
                        mensagens.add(str);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (symbol1.length()>4) {
            ref1 = ConfiguracaoFirebase.getFirebase();
            query1 = ref1.child("ipc_en_2019").child(secao_escolhida).
                    child(classe_escolhida).child(subclasse_escolhida).orderByChild("symbol").equalTo(symbol1);
            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Boolean simboloEncontrado = false;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        simboloEncontrado = true;
                        Ipc2019 ipcsymbol_lido = ds.getValue(Ipc2019.class);
                        symbol = ipcsymbol_lido.getSymbol().toString();
                        kind = ipcsymbol_lido.getKind().toString();

                        int grupo = Integer.parseInt(symbol.substring(4, 8));
                        String subgrupo = symbol.substring(8, 14);
                        String resultado = new String("");
                        int pos = 1;
                        int interromper = 0;
                        String str1 = new String("");
                        for (int i = 5; i >= 1; i--) {
                            String digito = subgrupo.substring(i, i + 1);
                            if (digito.equals("0")) {
                                if (interromper == 0 && i <= 1) pos = i;
                            } else {
                                if (interromper == 0) {
                                    pos = i;
                                    interromper = 1;
                                }
                            }
                        }

                        subgrupo = subgrupo.substring(0, pos + 1);
                        str = subclasse_escolhida + " " + grupo + "/" + subgrupo;
                        str1 = str;
                        kind = ipcsymbol_lido.getKind().toString();
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

                        indice = ipcsymbol_lido.getIndice();
                        proximo = ipcsymbol_lido.getProximo();
                        str = str + " " + descricao;

                        if (kind.equals("m")) pos = 0;
                        if (kind.equals("1")) pos = 1;
                        if (kind.equals("2")) pos = 2;
                        if (kind.equals("3")) pos = 3;
                        if (kind.equals("4")) pos = 4;
                        if (kind.equals("5")) pos = 5;
                        if (kind.equals("6")) pos = 6;
                        if (kind.equals("7")) pos = 7;
                        if (kind.equals("8")) pos = 8;
                        if (kind.equals("9")) pos = 9;
                        if (kind.equals("A")) pos = 10;
                        if (kind.equals("B")) pos = 11;
                        position_mensagens = 4 + pos;
                        position_mensagens_max = position_mensagens;
                        array_proximo[position_mensagens] = proximo;
                        array_grupos[position_mensagens] = str1;
                        array_mensagem[position_mensagens] = str;
                        if (pos == 0) {
                            mensagens.add(str);
                            adapter.notifyDataSetChanged();
                        }
                        //str = Integer.toString(pos)+" - "+Integer.toString(indice);
                        //Toast.makeText(GruposPesquisa.this,str,Toast.LENGTH_LONG).show();
                    }

                    if (kind.equals("1") || kind.equals("2") || kind.equals("3") ||
                            kind.equals("4") || kind.equals("5") || kind.equals("6") ||
                            kind.equals("7") || kind.equals("8") || kind.equals("9") ||
                            kind.equals("A") || kind.equals("B")) {
                        ref1 = ConfiguracaoFirebase.getFirebase();
                        query2 = ref1.child("ipc_en_2019").child(secao_escolhida).
                                child(classe_escolhida).child(subclasse_escolhida).orderByChild("proximo").equalTo(indice);
                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    Ipc2019 ipcsymbol_lido = ds.getValue(Ipc2019.class);
                                    symbol = ipcsymbol_lido.getSymbol().toString();
                                    kind = ipcsymbol_lido.getKind().toString();

                                    int grupo = Integer.parseInt(symbol.substring(4, 8));
                                    String subgrupo = symbol.substring(8, 14);
                                    String resultado = new String("");
                                    int pos = 1;
                                    int interromper = 0;
                                    String str1 = new String("");
                                    for (int i = 5; i >= 1; i--) {
                                        String digito = subgrupo.substring(i, i + 1);
                                        if (digito.equals("0")) {
                                            if (interromper == 0 && i <= 1) pos = i;
                                        } else {
                                            if (interromper == 0) {
                                                pos = i;
                                                interromper = 1;
                                            }
                                        }
                                    }
                                    subgrupo = subgrupo.substring(0, pos + 1);
                                    str = subclasse_escolhida + " " + grupo + "/" + subgrupo;
                                    str1 = str;
                                    kind = ipcsymbol_lido.getKind().toString();
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

                                    indice = ipcsymbol_lido.getIndice();
                                    proximo = ipcsymbol_lido.getProximo();
                                    str = str + " " + descricao;

                                    if (kind.equals("m")) pos = 0;
                                    if (kind.equals("1")) pos = 1;
                                    if (kind.equals("2")) pos = 2;
                                    if (kind.equals("3")) pos = 3;
                                    if (kind.equals("4")) pos = 4;
                                    if (kind.equals("5")) pos = 5;
                                    if (kind.equals("6")) pos = 6;
                                    if (kind.equals("7")) pos = 7;
                                    if (kind.equals("8")) pos = 8;
                                    if (kind.equals("9")) pos = 9;
                                    if (kind.equals("A")) pos = 10;
                                    if (kind.equals("B")) pos = 11;
                                    position_mensagens = 4 + pos;
                                    array_proximo[position_mensagens] = proximo;
                                    array_grupos[position_mensagens] = str1;
                                    array_mensagem[position_mensagens] = str;
                                    if (pos == 0) {
                                        for (int i = 4; i <= position_mensagens_max; i++) {
                                            str = array_mensagem[i];
                                            mensagens.add(str);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                    //str = Integer.toString(pos)+" - "+Integer.toString(indice);
                                    //Toast.makeText(GruposPesquisa.this,str,Toast.LENGTH_LONG).show();
                                }

                                if (kind.equals("1") || kind.equals("2") || kind.equals("3") ||
                                        kind.equals("4") || kind.equals("5") || kind.equals("6") ||
                                        kind.equals("7") || kind.equals("8") || kind.equals("9") || kind.equals("A")) {
                                    ref1 = ConfiguracaoFirebase.getFirebase();
                                    query3 = ref1.child("ipc_en_2019").child(secao_escolhida).
                                            child(classe_escolhida).child(subclasse_escolhida).orderByChild("proximo").equalTo(indice);
                                    query3.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                Ipc2019 ipcsymbol_lido = ds.getValue(Ipc2019.class);
                                                symbol = ipcsymbol_lido.getSymbol().toString();
                                                kind = ipcsymbol_lido.getKind().toString();

                                                int grupo = Integer.parseInt(symbol.substring(4, 8));
                                                String subgrupo = symbol.substring(8, 14);
                                                String resultado = new String("");
                                                int pos = 1;
                                                int interromper = 0;
                                                String str1 = new String("");
                                                for (int i = 5; i >= 1; i--) {
                                                    String digito = subgrupo.substring(i, i + 1);
                                                    if (digito.equals("0")) {
                                                        if (interromper == 0 && i <= 1) pos = i;
                                                    } else {
                                                        if (interromper == 0) {
                                                            pos = i;
                                                            interromper = 1;
                                                        }
                                                    }
                                                }
                                                subgrupo = subgrupo.substring(0, pos + 1);
                                                str = subclasse_escolhida + " " + grupo + "/" + subgrupo;
                                                str1 = str;
                                                kind = ipcsymbol_lido.getKind().toString();
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

                                                indice = ipcsymbol_lido.getIndice();
                                                proximo = ipcsymbol_lido.getProximo();
                                                str = str + " " + descricao;

                                                if (kind.equals("m")) pos = 0;
                                                if (kind.equals("1")) pos = 1;
                                                if (kind.equals("2")) pos = 2;
                                                if (kind.equals("3")) pos = 3;
                                                if (kind.equals("4")) pos = 4;
                                                if (kind.equals("5")) pos = 5;
                                                if (kind.equals("6")) pos = 6;
                                                if (kind.equals("7")) pos = 7;
                                                if (kind.equals("8")) pos = 8;
                                                if (kind.equals("9")) pos = 9;
                                                if (kind.equals("A")) pos = 10;
                                                if (kind.equals("B")) pos = 11;
                                                position_mensagens = 4 + pos;
                                                array_proximo[position_mensagens] = proximo;
                                                array_grupos[position_mensagens] = str1;
                                                array_mensagem[position_mensagens] = str;
                                                if (pos == 0) {
                                                    for (int i = 4; i <= position_mensagens_max; i++) {
                                                        str = array_mensagem[i];
                                                        mensagens.add(str);
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                }
                                                //str = Integer.toString(pos)+" - "+Integer.toString(indice);
                                                //Toast.makeText(GruposPesquisa.this,str,Toast.LENGTH_LONG).show();

                                            }

                                            if (kind.equals("1") || kind.equals("2") || kind.equals("3") ||
                                                    kind.equals("4") || kind.equals("5") || kind.equals("6") ||
                                                    kind.equals("7") || kind.equals("8") || kind.equals("9")) {
                                                ref1 = ConfiguracaoFirebase.getFirebase();
                                                query4 = ref1.child("ipc_en_2019").child(secao_escolhida).
                                                        child(classe_escolhida).child(subclasse_escolhida).orderByChild("proximo").equalTo(indice);
                                                query4.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                            Ipc2019 ipcsymbol_lido = ds.getValue(Ipc2019.class);
                                                            symbol = ipcsymbol_lido.getSymbol().toString();
                                                            kind = ipcsymbol_lido.getKind().toString();

                                                            int grupo = Integer.parseInt(symbol.substring(4, 8));
                                                            String subgrupo = symbol.substring(8, 14);
                                                            String resultado = new String("");
                                                            int pos = 1;
                                                            int interromper = 0;
                                                            String str1 = new String("");
                                                            for (int i = 5; i >= 1; i--) {
                                                                String digito = subgrupo.substring(i, i + 1);
                                                                if (digito.equals("0")) {
                                                                    if (interromper == 0 && i <= 1)
                                                                        pos = i;
                                                                } else {
                                                                    if (interromper == 0) {
                                                                        pos = i;
                                                                        interromper = 1;
                                                                    }
                                                                }
                                                            }
                                                            subgrupo = subgrupo.substring(0, pos + 1);
                                                            str = subclasse_escolhida + " " + grupo + "/" + subgrupo;
                                                            str1 = str;
                                                            kind = ipcsymbol_lido.getKind().toString();
                                                            if (kind.equals("m")) str = str + "";
                                                            if (kind.equals("1")) str = str + " .";
                                                            if (kind.equals("2")) str = str + " ..";
                                                            if (kind.equals("3"))
                                                                str = str + " ...";
                                                            if (kind.equals("4"))
                                                                str = str + " ....";
                                                            if (kind.equals("5"))
                                                                str = str + " .....";
                                                            if (kind.equals("6"))
                                                                str = str + " ......";
                                                            if (kind.equals("7"))
                                                                str = str + " .......";
                                                            if (kind.equals("8"))
                                                                str = str + " ........";
                                                            if (kind.equals("9"))
                                                                str = str + " .........";
                                                            if (kind.equals("A"))
                                                                str = str + " ..........";
                                                            if (kind.equals("B"))
                                                                str = str + " ...........";
                                                            if (idioma.equals("br"))
                                                                descricao = ipcsymbol_lido.getDescricaobr().toString();
                                                            else if (idioma.equals("fr"))
                                                                descricao = ipcsymbol_lido.getDescricaofr().toString();
                                                            else
                                                                descricao = ipcsymbol_lido.getDescricao().toString();

                                                            indice = ipcsymbol_lido.getIndice();
                                                            proximo = ipcsymbol_lido.getProximo();
                                                            str = str + " " + descricao;

                                                            if (kind.equals("m")) pos = 0;
                                                            if (kind.equals("1")) pos = 1;
                                                            if (kind.equals("2")) pos = 2;
                                                            if (kind.equals("3")) pos = 3;
                                                            if (kind.equals("4")) pos = 4;
                                                            if (kind.equals("5")) pos = 5;
                                                            if (kind.equals("6")) pos = 6;
                                                            if (kind.equals("7")) pos = 7;
                                                            if (kind.equals("8")) pos = 8;
                                                            if (kind.equals("9")) pos = 9;
                                                            if (kind.equals("A")) pos = 10;
                                                            if (kind.equals("B")) pos = 11;
                                                            position_mensagens = 4 + pos;
                                                            array_proximo[position_mensagens] = proximo;
                                                            array_grupos[position_mensagens] = str1;
                                                            array_mensagem[position_mensagens] = str;
                                                            if (pos == 0) {
                                                                for (int i = 4; i <= position_mensagens_max; i++) {
                                                                    str = array_mensagem[i];
                                                                    mensagens.add(str);
                                                                }
                                                                adapter.notifyDataSetChanged();
                                                            }
                                                            //str = Integer.toString(pos)+" - "+Integer.toString(indice);
                                                            //Toast.makeText(GruposPesquisa.this,str,Toast.LENGTH_LONG).show();
                                                        }

                                                        if (kind.equals("1") || kind.equals("2") || kind.equals("3") ||
                                                                kind.equals("4") || kind.equals("5") || kind.equals("6") ||
                                                                kind.equals("7") || kind.equals("8")) {
                                                            ref1 = ConfiguracaoFirebase.getFirebase();
                                                            query5 = ref1.child("ipc_en_2019").child(secao_escolhida).
                                                                    child(classe_escolhida).child(subclasse_escolhida).orderByChild("proximo").equalTo(indice);
                                                            query5.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                        Ipc2019 ipcsymbol_lido = ds.getValue(Ipc2019.class);
                                                                        symbol = ipcsymbol_lido.getSymbol().toString();
                                                                        kind = ipcsymbol_lido.getKind().toString();

                                                                        int grupo = Integer.parseInt(symbol.substring(4, 8));
                                                                        String subgrupo = symbol.substring(8, 14);
                                                                        String resultado = new String("");
                                                                        int pos = 1;
                                                                        int interromper = 0;
                                                                        String str1 = new String("");
                                                                        for (int i = 5; i >= 1; i--) {
                                                                            String digito = subgrupo.substring(i, i + 1);
                                                                            if (digito.equals("0")) {
                                                                                if (interromper == 0 && i <= 1)
                                                                                    pos = i;
                                                                            } else {
                                                                                if (interromper == 0) {
                                                                                    pos = i;
                                                                                    interromper = 1;
                                                                                }
                                                                            }
                                                                        }
                                                                        subgrupo = subgrupo.substring(0, pos + 1);
                                                                        str = subclasse_escolhida + " " + grupo + "/" + subgrupo;
                                                                        str1 = str;
                                                                        kind = ipcsymbol_lido.getKind().toString();
                                                                        if (kind.equals("m"))
                                                                            str = str + "";
                                                                        if (kind.equals("1"))
                                                                            str = str + " .";
                                                                        if (kind.equals("2"))
                                                                            str = str + " ..";
                                                                        if (kind.equals("3"))
                                                                            str = str + " ...";
                                                                        if (kind.equals("4"))
                                                                            str = str + " ....";
                                                                        if (kind.equals("5"))
                                                                            str = str + " .....";
                                                                        if (kind.equals("6"))
                                                                            str = str + " ......";
                                                                        if (kind.equals("7"))
                                                                            str = str + " .......";
                                                                        if (kind.equals("8"))
                                                                            str = str + " ........";
                                                                        if (kind.equals("9"))
                                                                            str = str + " .........";
                                                                        if (kind.equals("A"))
                                                                            str = str + " ..........";
                                                                        if (kind.equals("B"))
                                                                            str = str + " ...........";
                                                                        if (idioma.equals("br"))
                                                                            descricao = ipcsymbol_lido.getDescricaobr().toString();
                                                                        else if (idioma.equals("fr"))
                                                                            descricao = ipcsymbol_lido.getDescricaofr().toString();
                                                                        else
                                                                            descricao = ipcsymbol_lido.getDescricao().toString();

                                                                        indice = ipcsymbol_lido.getIndice();
                                                                        proximo = ipcsymbol_lido.getProximo();
                                                                        str = str + " " + descricao;

                                                                        if (kind.equals("m"))
                                                                            pos = 0;
                                                                        if (kind.equals("1"))
                                                                            pos = 1;
                                                                        if (kind.equals("2"))
                                                                            pos = 2;
                                                                        if (kind.equals("3"))
                                                                            pos = 3;
                                                                        if (kind.equals("4"))
                                                                            pos = 4;
                                                                        if (kind.equals("5"))
                                                                            pos = 5;
                                                                        if (kind.equals("6"))
                                                                            pos = 6;
                                                                        if (kind.equals("7"))
                                                                            pos = 7;
                                                                        if (kind.equals("8"))
                                                                            pos = 8;
                                                                        if (kind.equals("9"))
                                                                            pos = 9;
                                                                        if (kind.equals("A"))
                                                                            pos = 10;
                                                                        if (kind.equals("B"))
                                                                            pos = 11;
                                                                        position_mensagens = 4 + pos;
                                                                        array_proximo[position_mensagens] = proximo;
                                                                        array_grupos[position_mensagens] = str1;
                                                                        array_mensagem[position_mensagens] = str;
                                                                        if (pos == 0) {
                                                                            for (int i = 4; i <= position_mensagens_max; i++) {
                                                                                str = array_mensagem[i];
                                                                                mensagens.add(str);
                                                                            }
                                                                            adapter.notifyDataSetChanged();
                                                                        }
                                                                        //str = Integer.toString(pos)+" - "+Integer.toString(indice);
                                                                        //Toast.makeText(GruposPesquisa.this,str,Toast.LENGTH_LONG).show();
                                                                    }

                                                                    if (kind.equals("1") || kind.equals("2") || kind.equals("3") ||
                                                                            kind.equals("4") || kind.equals("5") || kind.equals("6") || kind.equals("7")) {
                                                                        ref1 = ConfiguracaoFirebase.getFirebase();
                                                                        query6 = ref1.child("ipc_en_2019").child(secao_escolhida).
                                                                                child(classe_escolhida).child(subclasse_escolhida).orderByChild("proximo").equalTo(indice);
                                                                        query6.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                                    Ipc2019 ipcsymbol_lido = ds.getValue(Ipc2019.class);
                                                                                    symbol = ipcsymbol_lido.getSymbol().toString();
                                                                                    kind = ipcsymbol_lido.getKind().toString();

                                                                                    int grupo = Integer.parseInt(symbol.substring(4, 8));
                                                                                    String subgrupo = symbol.substring(8, 14);
                                                                                    String resultado = new String("");
                                                                                    int pos = 1;
                                                                                    int interromper = 0;
                                                                                    String str1 = new String("");
                                                                                    for (int i = 5; i >= 1; i--) {
                                                                                        String digito = subgrupo.substring(i, i + 1);
                                                                                        if (digito.equals("0")) {
                                                                                            if (interromper == 0 && i <= 1)
                                                                                                pos = i;
                                                                                        } else {
                                                                                            if (interromper == 0) {
                                                                                                pos = i;
                                                                                                interromper = 1;
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    subgrupo = subgrupo.substring(0, pos + 1);
                                                                                    str = subclasse_escolhida + " " + grupo + "/" + subgrupo;
                                                                                    str1 = str;
                                                                                    kind = ipcsymbol_lido.getKind().toString();
                                                                                    if (kind.equals("m"))
                                                                                        str = str + "";
                                                                                    if (kind.equals("1"))
                                                                                        str = str + " .";
                                                                                    if (kind.equals("2"))
                                                                                        str = str + " ..";
                                                                                    if (kind.equals("3"))
                                                                                        str = str + " ...";
                                                                                    if (kind.equals("4"))
                                                                                        str = str + " ....";
                                                                                    if (kind.equals("5"))
                                                                                        str = str + " .....";
                                                                                    if (kind.equals("6"))
                                                                                        str = str + " ......";
                                                                                    if (kind.equals("7"))
                                                                                        str = str + " .......";
                                                                                    if (kind.equals("8"))
                                                                                        str = str + " ........";
                                                                                    if (kind.equals("9"))
                                                                                        str = str + " .........";
                                                                                    if (kind.equals("A"))
                                                                                        str = str + " ..........";
                                                                                    if (kind.equals("B"))
                                                                                        str = str + " ...........";
                                                                                    if (idioma.equals("br"))
                                                                                        descricao = ipcsymbol_lido.getDescricaobr().toString();
                                                                                    else if (idioma.equals("fr"))
                                                                                        descricao = ipcsymbol_lido.getDescricaofr().toString();
                                                                                    else
                                                                                        descricao = ipcsymbol_lido.getDescricao().toString();

                                                                                    indice = ipcsymbol_lido.getIndice();
                                                                                    proximo = ipcsymbol_lido.getProximo();
                                                                                    str = str + " " + descricao;

                                                                                    if (kind.equals("m"))
                                                                                        pos = 0;
                                                                                    if (kind.equals("1"))
                                                                                        pos = 1;
                                                                                    if (kind.equals("2"))
                                                                                        pos = 2;
                                                                                    if (kind.equals("3"))
                                                                                        pos = 3;
                                                                                    if (kind.equals("4"))
                                                                                        pos = 4;
                                                                                    if (kind.equals("5"))
                                                                                        pos = 5;
                                                                                    if (kind.equals("6"))
                                                                                        pos = 6;
                                                                                    if (kind.equals("7"))
                                                                                        pos = 7;
                                                                                    if (kind.equals("8"))
                                                                                        pos = 8;
                                                                                    if (kind.equals("9"))
                                                                                        pos = 9;
                                                                                    if (kind.equals("A"))
                                                                                        pos = 10;
                                                                                    if (kind.equals("B"))
                                                                                        pos = 11;
                                                                                    position_mensagens = 4 + pos;
                                                                                    array_proximo[position_mensagens] = proximo;
                                                                                    array_grupos[position_mensagens] = str1;
                                                                                    array_mensagem[position_mensagens] = str;
                                                                                    if (pos == 0) {
                                                                                        for (int i = 4; i <= position_mensagens_max; i++) {
                                                                                            str = array_mensagem[i];
                                                                                            mensagens.add(str);
                                                                                        }
                                                                                        adapter.notifyDataSetChanged();
                                                                                    }
                                                                                    //str = Integer.toString(pos)+" - "+Integer.toString(indice);
                                                                                    //Toast.makeText(GruposPesquisa.this,str,Toast.LENGTH_LONG).show();
                                                                                }

                                                                                if (kind.equals("1") || kind.equals("2") || kind.equals("3") ||
                                                                                        kind.equals("4") || kind.equals("5") || kind.equals("6")) {
                                                                                    ref1 = ConfiguracaoFirebase.getFirebase();
                                                                                    query7 = ref1.child("ipc_en_2019").child(secao_escolhida).
                                                                                            child(classe_escolhida).child(subclasse_escolhida).orderByChild("proximo").equalTo(indice);
                                                                                    query7.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                                                Ipc2019 ipcsymbol_lido = ds.getValue(Ipc2019.class);
                                                                                                symbol = ipcsymbol_lido.getSymbol().toString();
                                                                                                kind = ipcsymbol_lido.getKind().toString();

                                                                                                int grupo = Integer.parseInt(symbol.substring(4, 8));
                                                                                                String subgrupo = symbol.substring(8, 14);
                                                                                                String resultado = new String("");
                                                                                                int pos = 1;
                                                                                                int interromper = 0;
                                                                                                String str1 = new String("");
                                                                                                for (int i = 5; i >= 1; i--) {
                                                                                                    String digito = subgrupo.substring(i, i + 1);
                                                                                                    if (digito.equals("0")) {
                                                                                                        if (interromper == 0 && i <= 1)
                                                                                                            pos = i;
                                                                                                    } else {
                                                                                                        if (interromper == 0) {
                                                                                                            pos = i;
                                                                                                            interromper = 1;
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                                subgrupo = subgrupo.substring(0, pos + 1);
                                                                                                str = subclasse_escolhida + " " + grupo + "/" + subgrupo;
                                                                                                str1 = str;
                                                                                                kind = ipcsymbol_lido.getKind().toString();
                                                                                                if (kind.equals("m"))
                                                                                                    str = str + "";
                                                                                                if (kind.equals("1"))
                                                                                                    str = str + " .";
                                                                                                if (kind.equals("2"))
                                                                                                    str = str + " ..";
                                                                                                if (kind.equals("3"))
                                                                                                    str = str + " ...";
                                                                                                if (kind.equals("4"))
                                                                                                    str = str + " ....";
                                                                                                if (kind.equals("5"))
                                                                                                    str = str + " .....";
                                                                                                if (kind.equals("6"))
                                                                                                    str = str + " ......";
                                                                                                if (kind.equals("7"))
                                                                                                    str = str + " .......";
                                                                                                if (kind.equals("8"))
                                                                                                    str = str + " ........";
                                                                                                if (kind.equals("9"))
                                                                                                    str = str + " .........";
                                                                                                if (kind.equals("A"))
                                                                                                    str = str + " ..........";
                                                                                                if (kind.equals("B"))
                                                                                                    str = str + " ...........";
                                                                                                if (idioma.equals("br"))
                                                                                                    descricao = ipcsymbol_lido.getDescricaobr().toString();
                                                                                                else if (idioma.equals("fr"))
                                                                                                    descricao = ipcsymbol_lido.getDescricaofr().toString();
                                                                                                else
                                                                                                    descricao = ipcsymbol_lido.getDescricao().toString();

                                                                                                indice = ipcsymbol_lido.getIndice();
                                                                                                proximo = ipcsymbol_lido.getProximo();
                                                                                                str = str + " " + descricao;

                                                                                                if (kind.equals("m"))
                                                                                                    pos = 0;
                                                                                                if (kind.equals("1"))
                                                                                                    pos = 1;
                                                                                                if (kind.equals("2"))
                                                                                                    pos = 2;
                                                                                                if (kind.equals("3"))
                                                                                                    pos = 3;
                                                                                                if (kind.equals("4"))
                                                                                                    pos = 4;
                                                                                                if (kind.equals("5"))
                                                                                                    pos = 5;
                                                                                                if (kind.equals("6"))
                                                                                                    pos = 6;
                                                                                                if (kind.equals("7"))
                                                                                                    pos = 7;
                                                                                                if (kind.equals("8"))
                                                                                                    pos = 8;
                                                                                                if (kind.equals("9"))
                                                                                                    pos = 9;
                                                                                                if (kind.equals("A"))
                                                                                                    pos = 10;
                                                                                                if (kind.equals("B"))
                                                                                                    pos = 11;
                                                                                                position_mensagens = 4 + pos;
                                                                                                array_proximo[position_mensagens] = proximo;
                                                                                                array_grupos[position_mensagens] = str1;
                                                                                                array_mensagem[position_mensagens] = str;
                                                                                                if (pos == 0) {
                                                                                                    for (int i = 4; i <= position_mensagens_max; i++) {
                                                                                                        str = array_mensagem[i];
                                                                                                        mensagens.add(str);
                                                                                                    }
                                                                                                    adapter.notifyDataSetChanged();
                                                                                                }
                                                                                                //str = Integer.toString(pos)+" - "+Integer.toString(indice);
                                                                                                //Toast.makeText(GruposPesquisa.this,str,Toast.LENGTH_LONG).show();
                                                                                            }

                                                                                            if (kind.equals("1") || kind.equals("2") || kind.equals("3") ||
                                                                                                    kind.equals("4") || kind.equals("5")) {
                                                                                                ref1 = ConfiguracaoFirebase.getFirebase();
                                                                                                query8 = ref1.child("ipc_en_2019").child(secao_escolhida).
                                                                                                        child(classe_escolhida).child(subclasse_escolhida).orderByChild("proximo").equalTo(indice);
                                                                                                query8.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                                                            Ipc2019 ipcsymbol_lido = ds.getValue(Ipc2019.class);
                                                                                                            symbol = ipcsymbol_lido.getSymbol().toString();
                                                                                                            kind = ipcsymbol_lido.getKind().toString();

                                                                                                            int grupo = Integer.parseInt(symbol.substring(4, 8));
                                                                                                            String subgrupo = symbol.substring(8, 14);
                                                                                                            String resultado = new String("");
                                                                                                            int pos = 1;
                                                                                                            int interromper = 0;
                                                                                                            String str1 = new String("");
                                                                                                            for (int i = 5; i >= 1; i--) {
                                                                                                                String digito = subgrupo.substring(i, i + 1);
                                                                                                                if (digito.equals("0")) {
                                                                                                                    if (interromper == 0 && i <= 1)
                                                                                                                        pos = i;
                                                                                                                } else {
                                                                                                                    if (interromper == 0) {
                                                                                                                        pos = i;
                                                                                                                        interromper = 1;
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                            subgrupo = subgrupo.substring(0, pos + 1);
                                                                                                            str = subclasse_escolhida + " " + grupo + "/" + subgrupo;
                                                                                                            str1 = str;
                                                                                                            kind = ipcsymbol_lido.getKind().toString();
                                                                                                            if (kind.equals("m"))
                                                                                                                str = str + "";
                                                                                                            if (kind.equals("1"))
                                                                                                                str = str + " .";
                                                                                                            if (kind.equals("2"))
                                                                                                                str = str + " ..";
                                                                                                            if (kind.equals("3"))
                                                                                                                str = str + " ...";
                                                                                                            if (kind.equals("4"))
                                                                                                                str = str + " ....";
                                                                                                            if (kind.equals("5"))
                                                                                                                str = str + " .....";
                                                                                                            if (kind.equals("6"))
                                                                                                                str = str + " ......";
                                                                                                            if (kind.equals("7"))
                                                                                                                str = str + " .......";
                                                                                                            if (kind.equals("8"))
                                                                                                                str = str + " ........";
                                                                                                            if (kind.equals("9"))
                                                                                                                str = str + " .........";
                                                                                                            if (kind.equals("A"))
                                                                                                                str = str + " ..........";
                                                                                                            if (kind.equals("B"))
                                                                                                                str = str + " ...........";
                                                                                                            if (idioma.equals("br"))
                                                                                                                descricao = ipcsymbol_lido.getDescricaobr().toString();
                                                                                                            else if (idioma.equals("fr"))
                                                                                                                descricao = ipcsymbol_lido.getDescricaofr().toString();
                                                                                                            else
                                                                                                                descricao = ipcsymbol_lido.getDescricao().toString();

                                                                                                            indice = ipcsymbol_lido.getIndice();
                                                                                                            proximo = ipcsymbol_lido.getProximo();
                                                                                                            str = str + " " + descricao;

                                                                                                            if (kind.equals("m"))
                                                                                                                pos = 0;
                                                                                                            if (kind.equals("1"))
                                                                                                                pos = 1;
                                                                                                            if (kind.equals("2"))
                                                                                                                pos = 2;
                                                                                                            if (kind.equals("3"))
                                                                                                                pos = 3;
                                                                                                            if (kind.equals("4"))
                                                                                                                pos = 4;
                                                                                                            if (kind.equals("5"))
                                                                                                                pos = 5;
                                                                                                            if (kind.equals("6"))
                                                                                                                pos = 6;
                                                                                                            if (kind.equals("7"))
                                                                                                                pos = 7;
                                                                                                            if (kind.equals("8"))
                                                                                                                pos = 8;
                                                                                                            if (kind.equals("9"))
                                                                                                                pos = 9;
                                                                                                            if (kind.equals("A"))
                                                                                                                pos = 10;
                                                                                                            if (kind.equals("B"))
                                                                                                                pos = 11;
                                                                                                            position_mensagens = 4 + pos;
                                                                                                            array_proximo[position_mensagens] = proximo;
                                                                                                            array_grupos[position_mensagens] = str1;
                                                                                                            array_mensagem[position_mensagens] = str;
                                                                                                            if (pos == 0) {
                                                                                                                for (int i = 4; i <= position_mensagens_max; i++) {
                                                                                                                    str = array_mensagem[i];
                                                                                                                    mensagens.add(str);
                                                                                                                }
                                                                                                                adapter.notifyDataSetChanged();
                                                                                                            }
                                                                                                            //str = Integer.toString(pos)+" - "+Integer.toString(indice);
                                                                                                            //Toast.makeText(GruposPesquisa.this,str,Toast.LENGTH_LONG).show();
                                                                                                        }

                                                                                                        if (kind.equals("1") || kind.equals("2") || kind.equals("3") || kind.equals("4")) {
                                                                                                            ref1 = ConfiguracaoFirebase.getFirebase();
                                                                                                            query9 = ref1.child("ipc_en_2019").child(secao_escolhida).
                                                                                                                    child(classe_escolhida).child(subclasse_escolhida).orderByChild("proximo").equalTo(indice);
                                                                                                            query9.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                @Override
                                                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                                                                        Ipc2019 ipcsymbol_lido = ds.getValue(Ipc2019.class);
                                                                                                                        symbol = ipcsymbol_lido.getSymbol().toString();
                                                                                                                        kind = ipcsymbol_lido.getKind().toString();

                                                                                                                        int grupo = Integer.parseInt(symbol.substring(4, 8));
                                                                                                                        String subgrupo = symbol.substring(8, 14);
                                                                                                                        String resultado = new String("");
                                                                                                                        int pos = 1;
                                                                                                                        int interromper = 0;
                                                                                                                        String str1 = new String("");
                                                                                                                        for (int i = 5; i >= 1; i--) {
                                                                                                                            String digito = subgrupo.substring(i, i + 1);
                                                                                                                            if (digito.equals("0")) {
                                                                                                                                if (interromper == 0 && i <= 1)
                                                                                                                                    pos = i;
                                                                                                                            } else {
                                                                                                                                if (interromper == 0) {
                                                                                                                                    pos = i;
                                                                                                                                    interromper = 1;
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                        subgrupo = subgrupo.substring(0, pos + 1);
                                                                                                                        str = subclasse_escolhida + " " + grupo + "/" + subgrupo;
                                                                                                                        str1 = str;
                                                                                                                        kind = ipcsymbol_lido.getKind().toString();
                                                                                                                        if (kind.equals("m"))
                                                                                                                            str = str + "";
                                                                                                                        if (kind.equals("1"))
                                                                                                                            str = str + " .";
                                                                                                                        if (kind.equals("2"))
                                                                                                                            str = str + " ..";
                                                                                                                        if (kind.equals("3"))
                                                                                                                            str = str + " ...";
                                                                                                                        if (kind.equals("4"))
                                                                                                                            str = str + " ....";
                                                                                                                        if (kind.equals("5"))
                                                                                                                            str = str + " .....";
                                                                                                                        if (kind.equals("6"))
                                                                                                                            str = str + " ......";
                                                                                                                        if (kind.equals("7"))
                                                                                                                            str = str + " .......";
                                                                                                                        if (kind.equals("8"))
                                                                                                                            str = str + " ........";
                                                                                                                        if (kind.equals("9"))
                                                                                                                            str = str + " .........";
                                                                                                                        if (kind.equals("A"))
                                                                                                                            str = str + " ..........";
                                                                                                                        if (kind.equals("B"))
                                                                                                                            str = str + " ...........";
                                                                                                                        if (idioma.equals("br"))
                                                                                                                            descricao = ipcsymbol_lido.getDescricaobr().toString();
                                                                                                                        else if (idioma.equals("fr"))
                                                                                                                            descricao = ipcsymbol_lido.getDescricaofr().toString();
                                                                                                                        else
                                                                                                                            descricao = ipcsymbol_lido.getDescricao().toString();

                                                                                                                        indice = ipcsymbol_lido.getIndice();
                                                                                                                        proximo = ipcsymbol_lido.getProximo();
                                                                                                                        str = str + " " + descricao;

                                                                                                                        if (kind.equals("m"))
                                                                                                                            pos = 0;
                                                                                                                        if (kind.equals("1"))
                                                                                                                            pos = 1;
                                                                                                                        if (kind.equals("2"))
                                                                                                                            pos = 2;
                                                                                                                        if (kind.equals("3"))
                                                                                                                            pos = 3;
                                                                                                                        if (kind.equals("4"))
                                                                                                                            pos = 4;
                                                                                                                        if (kind.equals("5"))
                                                                                                                            pos = 5;
                                                                                                                        if (kind.equals("6"))
                                                                                                                            pos = 6;
                                                                                                                        if (kind.equals("7"))
                                                                                                                            pos = 7;
                                                                                                                        if (kind.equals("8"))
                                                                                                                            pos = 8;
                                                                                                                        if (kind.equals("9"))
                                                                                                                            pos = 9;
                                                                                                                        if (kind.equals("A"))
                                                                                                                            pos = 10;
                                                                                                                        if (kind.equals("B"))
                                                                                                                            pos = 11;

                                                                                                                        position_mensagens = 4 + pos;
                                                                                                                        array_proximo[position_mensagens] = proximo;
                                                                                                                        array_grupos[position_mensagens] = str1;
                                                                                                                        array_mensagem[position_mensagens] = str;
                                                                                                                        if (pos == 0) {
                                                                                                                            for (int i = 4; i <= position_mensagens_max; i++) {
                                                                                                                                str = array_mensagem[i];
                                                                                                                                mensagens.add(str);
                                                                                                                            }
                                                                                                                            adapter.notifyDataSetChanged();
                                                                                                                        }
                                                                                                                        //str = Integer.toString(pos)+" - "+Integer.toString(indice);
                                                                                                                        //Toast.makeText(GruposPesquisa.this,str,Toast.LENGTH_LONG).show();
                                                                                                                    }

                                                                                                                    if (kind.equals("1") || kind.equals("2") || kind.equals("3")) {
                                                                                                                        ref1 = ConfiguracaoFirebase.getFirebase();
                                                                                                                        query10 = ref1.child("ipc_en_2019").child(secao_escolhida).
                                                                                                                                child(classe_escolhida).child(subclasse_escolhida).orderByChild("proximo").equalTo(indice);
                                                                                                                        query10.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                            @Override
                                                                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                                                                                    Ipc2019 ipcsymbol_lido = ds.getValue(Ipc2019.class);
                                                                                                                                    symbol = ipcsymbol_lido.getSymbol().toString();
                                                                                                                                    kind = ipcsymbol_lido.getKind().toString();

                                                                                                                                    int grupo = Integer.parseInt(symbol.substring(4, 8));
                                                                                                                                    String subgrupo = symbol.substring(8, 14);
                                                                                                                                    String resultado = new String("");
                                                                                                                                    int pos = 1;
                                                                                                                                    int interromper = 0;
                                                                                                                                    String str1 = new String("");
                                                                                                                                    for (int i = 5; i >= 1; i--) {
                                                                                                                                        String digito = subgrupo.substring(i, i + 1);
                                                                                                                                        if (digito.equals("0")) {
                                                                                                                                            if (interromper == 0 && i <= 1)
                                                                                                                                                pos = i;
                                                                                                                                        } else {
                                                                                                                                            if (interromper == 0) {
                                                                                                                                                pos = i;
                                                                                                                                                interromper = 1;
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    }

                                                                                                                                    subgrupo = subgrupo.substring(0, pos + 1);
                                                                                                                                    str = subclasse_escolhida + " " + grupo + "/" + subgrupo;
                                                                                                                                    str1 = str;
                                                                                                                                    kind = ipcsymbol_lido.getKind().toString();
                                                                                                                                    if (kind.equals("m"))
                                                                                                                                        str = str + "";
                                                                                                                                    if (kind.equals("1"))
                                                                                                                                        str = str + " .";
                                                                                                                                    if (kind.equals("2"))
                                                                                                                                        str = str + " ..";
                                                                                                                                    if (kind.equals("3"))
                                                                                                                                        str = str + " ...";
                                                                                                                                    if (kind.equals("4"))
                                                                                                                                        str = str + " ....";
                                                                                                                                    if (kind.equals("5"))
                                                                                                                                        str = str + " .....";
                                                                                                                                    if (kind.equals("6"))
                                                                                                                                        str = str + " ......";
                                                                                                                                    if (kind.equals("7"))
                                                                                                                                        str = str + " .......";
                                                                                                                                    if (kind.equals("8"))
                                                                                                                                        str = str + " ........";
                                                                                                                                    if (kind.equals("9"))
                                                                                                                                        str = str + " .........";
                                                                                                                                    if (kind.equals("A"))
                                                                                                                                        str = str + " ..........";
                                                                                                                                    if (kind.equals("B"))
                                                                                                                                        str = str + " ...........";

                                                                                                                                    if (idioma.equals("br"))
                                                                                                                                        descricao = ipcsymbol_lido.getDescricaobr().toString();
                                                                                                                                    else if (idioma.equals("fr"))
                                                                                                                                        descricao = ipcsymbol_lido.getDescricaofr().toString();
                                                                                                                                    else
                                                                                                                                        descricao = ipcsymbol_lido.getDescricao().toString();

                                                                                                                                    indice = ipcsymbol_lido.getIndice();
                                                                                                                                    proximo = ipcsymbol_lido.getProximo();
                                                                                                                                    str = str + " " + descricao;

                                                                                                                                    if (kind.equals("m"))
                                                                                                                                        pos = 0;
                                                                                                                                    if (kind.equals("1"))
                                                                                                                                        pos = 1;
                                                                                                                                    if (kind.equals("2"))
                                                                                                                                        pos = 2;
                                                                                                                                    if (kind.equals("3"))
                                                                                                                                        pos = 3;
                                                                                                                                    if (kind.equals("4"))
                                                                                                                                        pos = 4;
                                                                                                                                    if (kind.equals("5"))
                                                                                                                                        pos = 5;
                                                                                                                                    if (kind.equals("6"))
                                                                                                                                        pos = 6;
                                                                                                                                    if (kind.equals("7"))
                                                                                                                                        pos = 7;
                                                                                                                                    if (kind.equals("8"))
                                                                                                                                        pos = 8;
                                                                                                                                    if (kind.equals("9"))
                                                                                                                                        pos = 9;
                                                                                                                                    if (kind.equals("A"))
                                                                                                                                        pos = 10;
                                                                                                                                    if (kind.equals("B"))
                                                                                                                                        pos = 11;

                                                                                                                                    position_mensagens = 4 + pos;
                                                                                                                                    array_proximo[position_mensagens] = proximo;
                                                                                                                                    array_grupos[position_mensagens] = str1;
                                                                                                                                    array_mensagem[position_mensagens] = str;
                                                                                                                                    if (pos == 0) {
                                                                                                                                        for (int i = 4; i <= position_mensagens_max; i++) {
                                                                                                                                            str = array_mensagem[i];
                                                                                                                                            mensagens.add(str);
                                                                                                                                        }
                                                                                                                                        adapter.notifyDataSetChanged();
                                                                                                                                    }
                                                                                                                                    //str = Integer.toString(pos)+" - "+Integer.toString(indice);
                                                                                                                                    //Toast.makeText(GruposPesquisa.this,str,Toast.LENGTH_LONG).show();
                                                                                                                                }

                                                                                                                                if (kind.equals("1") || kind.equals("2")) {
                                                                                                                                    ref1 = ConfiguracaoFirebase.getFirebase();
                                                                                                                                    query11 = ref1.child("ipc_en_2019").child(secao_escolhida).
                                                                                                                                            child(classe_escolhida).child(subclasse_escolhida).orderByChild("proximo").equalTo(indice);
                                                                                                                                    query11.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                                        @Override
                                                                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                                                                                                Ipc2019 ipcsymbol_lido = ds.getValue(Ipc2019.class);
                                                                                                                                                symbol = ipcsymbol_lido.getSymbol().toString();
                                                                                                                                                kind = ipcsymbol_lido.getKind().toString();

                                                                                                                                                int grupo = Integer.parseInt(symbol.substring(4, 8));
                                                                                                                                                String subgrupo = symbol.substring(8, 14);
                                                                                                                                                String resultado = new String("");
                                                                                                                                                int pos = 1;
                                                                                                                                                int interromper = 0;
                                                                                                                                                String str1 = new String("");
                                                                                                                                                for (int i = 5; i >= 1; i--) {
                                                                                                                                                    String digito = subgrupo.substring(i, i + 1);
                                                                                                                                                    if (digito.equals("0")) {
                                                                                                                                                        if (interromper == 0 && i <= 1)
                                                                                                                                                            pos = i;
                                                                                                                                                    } else {
                                                                                                                                                        if (interromper == 0) {
                                                                                                                                                            pos = i;
                                                                                                                                                            interromper = 1;
                                                                                                                                                        }
                                                                                                                                                    }
                                                                                                                                                }

                                                                                                                                                subgrupo = subgrupo.substring(0, pos + 1);
                                                                                                                                                str = subclasse_escolhida + " " + grupo + "/" + subgrupo;
                                                                                                                                                str1 = str;
                                                                                                                                                kind = ipcsymbol_lido.getKind().toString();
                                                                                                                                                if (kind.equals("m"))
                                                                                                                                                    str = str + "";
                                                                                                                                                if (kind.equals("1"))
                                                                                                                                                    str = str + " .";
                                                                                                                                                if (kind.equals("2"))
                                                                                                                                                    str = str + " ..";
                                                                                                                                                if (kind.equals("3"))
                                                                                                                                                    str = str + " ...";
                                                                                                                                                if (kind.equals("4"))
                                                                                                                                                    str = str + " ....";
                                                                                                                                                if (kind.equals("5"))
                                                                                                                                                    str = str + " .....";
                                                                                                                                                if (kind.equals("6"))
                                                                                                                                                    str = str + " ......";
                                                                                                                                                if (kind.equals("7"))
                                                                                                                                                    str = str + " .......";
                                                                                                                                                if (kind.equals("8"))
                                                                                                                                                    str = str + " ........";
                                                                                                                                                if (kind.equals("9"))
                                                                                                                                                    str = str + " .........";
                                                                                                                                                if (kind.equals("A"))
                                                                                                                                                    str = str + " ..........";
                                                                                                                                                if (kind.equals("B"))
                                                                                                                                                    str = str + " ...........";
                                                                                                                                                if (idioma.equals("br"))
                                                                                                                                                    descricao = ipcsymbol_lido.getDescricaobr().toString();
                                                                                                                                                else if (idioma.equals("fr"))
                                                                                                                                                    descricao = ipcsymbol_lido.getDescricaofr().toString();
                                                                                                                                                else
                                                                                                                                                    descricao = ipcsymbol_lido.getDescricao().toString();

                                                                                                                                                indice = ipcsymbol_lido.getIndice();
                                                                                                                                                proximo = ipcsymbol_lido.getProximo();
                                                                                                                                                str = str + " " + descricao;

                                                                                                                                                if (kind.equals("m"))
                                                                                                                                                    pos = 0;
                                                                                                                                                if (kind.equals("1"))
                                                                                                                                                    pos = 1;
                                                                                                                                                if (kind.equals("2"))
                                                                                                                                                    pos = 2;
                                                                                                                                                if (kind.equals("3"))
                                                                                                                                                    pos = 3;
                                                                                                                                                if (kind.equals("4"))
                                                                                                                                                    pos = 4;
                                                                                                                                                if (kind.equals("5"))
                                                                                                                                                    pos = 5;
                                                                                                                                                if (kind.equals("6"))
                                                                                                                                                    pos = 6;
                                                                                                                                                if (kind.equals("7"))
                                                                                                                                                    pos = 7;
                                                                                                                                                if (kind.equals("8"))
                                                                                                                                                    pos = 8;
                                                                                                                                                if (kind.equals("9"))
                                                                                                                                                    pos = 9;
                                                                                                                                                if (kind.equals("A"))
                                                                                                                                                    pos = 10;
                                                                                                                                                if (kind.equals("B"))
                                                                                                                                                    pos = 11;

                                                                                                                                                position_mensagens = 4 + pos;
                                                                                                                                                array_proximo[position_mensagens] = proximo;
                                                                                                                                                array_grupos[position_mensagens] = str1;
                                                                                                                                                array_mensagem[position_mensagens] = str;
                                                                                                                                                if (pos == 0) {
                                                                                                                                                    for (int i = 4; i <= position_mensagens_max; i++) {
                                                                                                                                                        str = array_mensagem[i];
                                                                                                                                                        mensagens.add(str);
                                                                                                                                                    }
                                                                                                                                                    adapter.notifyDataSetChanged();
                                                                                                                                                }
                                                                                                                                                //str = Integer.toString(pos)+" - "+Integer.toString(indice);
                                                                                                                                                //Toast.makeText(GruposPesquisa.this,str,Toast.LENGTH_LONG).show();
                                                                                                                                            }


                                                                                                                                            if (kind.equals("1")) {
                                                                                                                                                ref1 = ConfiguracaoFirebase.getFirebase();
                                                                                                                                                query12 = ref1.child("ipc_en_2019").child(secao_escolhida).
                                                                                                                                                        child(classe_escolhida).child(subclasse_escolhida).orderByChild("proximo").equalTo(indice);
                                                                                                                                                query12.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                                                    @Override
                                                                                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                                                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                                                                                                            Ipc2019 ipcsymbol_lido = ds.getValue(Ipc2019.class);
                                                                                                                                                            symbol = ipcsymbol_lido.getSymbol().toString();
                                                                                                                                                            kind = ipcsymbol_lido.getKind().toString();

                                                                                                                                                            int grupo = Integer.parseInt(symbol.substring(4, 8));
                                                                                                                                                            String subgrupo = symbol.substring(8, 14);
                                                                                                                                                            String resultado = new String("");
                                                                                                                                                            int pos = 1;
                                                                                                                                                            int interromper = 0;
                                                                                                                                                            String str1 = new String("");
                                                                                                                                                            for (int i = 5; i >= 1; i--) {
                                                                                                                                                                String digito = subgrupo.substring(i, i + 1);
                                                                                                                                                                if (digito.equals("0")) {
                                                                                                                                                                    if (interromper == 0 && i <= 1)
                                                                                                                                                                        pos = i;
                                                                                                                                                                } else {
                                                                                                                                                                    if (interromper == 0) {
                                                                                                                                                                        pos = i;
                                                                                                                                                                        interromper = 1;
                                                                                                                                                                    }
                                                                                                                                                                }
                                                                                                                                                            }

                                                                                                                                                            subgrupo = subgrupo.substring(0, pos + 1);
                                                                                                                                                            str = subclasse_escolhida + " " + grupo + "/" + subgrupo;
                                                                                                                                                            str1 = str;
                                                                                                                                                            kind = ipcsymbol_lido.getKind().toString();
                                                                                                                                                            if (kind.equals("m"))
                                                                                                                                                                str = str + "";
                                                                                                                                                            if (kind.equals("1"))
                                                                                                                                                                str = str + " .";
                                                                                                                                                            if (kind.equals("2"))
                                                                                                                                                                str = str + " ..";
                                                                                                                                                            if (kind.equals("3"))
                                                                                                                                                                str = str + " ...";
                                                                                                                                                            if (kind.equals("4"))
                                                                                                                                                                str = str + " ....";
                                                                                                                                                            if (kind.equals("5"))
                                                                                                                                                                str = str + " .....";
                                                                                                                                                            if (kind.equals("6"))
                                                                                                                                                                str = str + " ......";
                                                                                                                                                            if (kind.equals("7"))
                                                                                                                                                                str = str + " .......";
                                                                                                                                                            if (kind.equals("8"))
                                                                                                                                                                str = str + " ........";
                                                                                                                                                            if (kind.equals("9"))
                                                                                                                                                                str = str + " .........";
                                                                                                                                                            if (kind.equals("A"))
                                                                                                                                                                str = str + " ..........";
                                                                                                                                                            if (kind.equals("B"))
                                                                                                                                                                str = str + " ...........";
                                                                                                                                                            if (idioma.equals("br"))
                                                                                                                                                                descricao = ipcsymbol_lido.getDescricaobr().toString();
                                                                                                                                                            else if (idioma.equals("fr"))
                                                                                                                                                                descricao = ipcsymbol_lido.getDescricaofr().toString();
                                                                                                                                                            else
                                                                                                                                                                descricao = ipcsymbol_lido.getDescricao().toString();

                                                                                                                                                            indice = ipcsymbol_lido.getIndice();
                                                                                                                                                            proximo = ipcsymbol_lido.getProximo();
                                                                                                                                                            str = str + " " + descricao;

                                                                                                                                                            if (kind.equals("m"))
                                                                                                                                                                pos = 0;
                                                                                                                                                            if (kind.equals("1"))
                                                                                                                                                                pos = 1;
                                                                                                                                                            if (kind.equals("2"))
                                                                                                                                                                pos = 2;
                                                                                                                                                            if (kind.equals("3"))
                                                                                                                                                                pos = 3;
                                                                                                                                                            if (kind.equals("4"))
                                                                                                                                                                pos = 4;
                                                                                                                                                            if (kind.equals("5"))
                                                                                                                                                                pos = 5;
                                                                                                                                                            if (kind.equals("6"))
                                                                                                                                                                pos = 6;
                                                                                                                                                            if (kind.equals("7"))
                                                                                                                                                                pos = 7;
                                                                                                                                                            if (kind.equals("8"))
                                                                                                                                                                pos = 8;
                                                                                                                                                            if (kind.equals("9"))
                                                                                                                                                                pos = 9;
                                                                                                                                                            if (kind.equals("A"))
                                                                                                                                                                pos = 10;
                                                                                                                                                            if (kind.equals("B"))
                                                                                                                                                                pos = 11;

                                                                                                                                                            position_mensagens = 4 + pos;
                                                                                                                                                            array_proximo[position_mensagens] = proximo;
                                                                                                                                                            array_grupos[position_mensagens] = str1;
                                                                                                                                                            array_mensagem[position_mensagens] = str;
                                                                                                                                                            if (pos == 0) {
                                                                                                                                                                for (int i = 4; i <= position_mensagens_max; i++) {
                                                                                                                                                                    str = array_mensagem[i];
                                                                                                                                                                    mensagens.add(str);
                                                                                                                                                                }
                                                                                                                                                                adapter.notifyDataSetChanged();
                                                                                                                                                            }
                                                                                                                                                            //str = Integer.toString(pos)+" - "+Integer.toString(indice);
                                                                                                                                                            //Toast.makeText(GruposPesquisa.this,str,Toast.LENGTH_LONG).show();
                                                                                                                                                        }


                                                                                                                                                    }

                                                                                                                                                    @Override
                                                                                                                                                    public void onCancelled(DatabaseError databaseError) {
                                                                                                                                                        //Se ocorrer um erro
                                                                                                                                                    }
                                                                                                                                                });

                                                                                                                                            }

                                                                                                                                        }

                                                                                                                                        @Override
                                                                                                                                        public void onCancelled(DatabaseError databaseError) {
                                                                                                                                            //Se ocorrer um erro
                                                                                                                                        }
                                                                                                                                    });

                                                                                                                                }

                                                                                                                            }

                                                                                                                            @Override
                                                                                                                            public void onCancelled(DatabaseError databaseError) {
                                                                                                                                //Se ocorrer um erro
                                                                                                                            }
                                                                                                                        });

                                                                                                                    }

                                                                                                                }

                                                                                                                @Override
                                                                                                                public void onCancelled(DatabaseError databaseError) {
                                                                                                                    //Se ocorrer um erro
                                                                                                                }
                                                                                                            });

                                                                                                        }

                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onCancelled(DatabaseError databaseError) {
                                                                                                        //Se ocorrer um erro
                                                                                                    }
                                                                                                });

                                                                                            }

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {
                                                                                            //Se ocorrer um erro
                                                                                        }
                                                                                    });

                                                                                }

                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {
                                                                                //Se ocorrer um erro
                                                                            }
                                                                        });

                                                                    }

                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {
                                                                    //Se ocorrer um erro
                                                                }
                                                            });

                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        //Se ocorrer um erro
                                                    }
                                                });

                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            //Se ocorrer um erro
                                        }
                                    });

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //Se ocorrer um erro
                            }
                        });

                    }

                    if (simboloEncontrado)
                        adapter.notifyDataSetChanged();
                    else {
                        if (idioma.equals("br"))
                            Toast.makeText(GruposPesquisa.this, "Símbolo IPC não encontrado", Toast.LENGTH_LONG).show();
                        else if (idioma.equals("en"))
                            Toast.makeText(GruposPesquisa.this, "IPC symbol not found", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(GruposPesquisa.this, "Symbole CIP non trouvé", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Se ocorrer um erro
                }
            });

            adapter.notifyDataSetChanged();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==1) {
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
                        Intent intent = new Intent(GruposPesquisa.this, Classes.class);
                        proxima_tela = array_proximo[position];
                        intent.putExtra("subclasse_escolhida", subclasse_escolhida);
                        intent.putExtra("classe_escolhida", classe_escolhida);
                        intent.putExtra("secao_escolhida", secao_escolhida);
                        intent.putExtra("proxima_tela", proxima_tela);
                        intent.putExtra("idioma", idioma);
                        startActivity(intent);
                    }
                }
                if (position==2) {
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
                        Intent intent = new Intent(GruposPesquisa.this, SubClasses.class);
                        proxima_tela = array_proximo[position];
                        intent.putExtra("subclasse_escolhida", subclasse_escolhida);
                        intent.putExtra("classe_escolhida", classe_escolhida);
                        intent.putExtra("secao_escolhida", secao_escolhida);
                        intent.putExtra("proxima_tela", proxima_tela);
                        intent.putExtra("idioma", idioma);
                        startActivity(intent);
                    }
                }
                if (position==3) {
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
                        Intent intent = new Intent(GruposPesquisa.this, GruposKind0.class);
                        proxima_tela = array_proximo[position];
                        intent.putExtra("subclasse_escolhida", subclasse_escolhida);
                        intent.putExtra("classe_escolhida", classe_escolhida);
                        intent.putExtra("secao_escolhida", secao_escolhida);
                        intent.putExtra("proxima_tela", proxima_tela);
                        intent.putExtra("idioma", idioma);
                        startActivity(intent);
                    }
                }
                if (position==4) {
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
                        Intent intent = new Intent(GruposPesquisa.this, GruposKind1.class);
                        proxima_tela = array_proximo[position];
                        intent.putExtra("subclasse_escolhida", subclasse_escolhida);
                        intent.putExtra("classe_escolhida", classe_escolhida);
                        intent.putExtra("secao_escolhida", secao_escolhida);
                        intent.putExtra("proxima_tela", proxima_tela);
                        intent.putExtra("idioma", idioma);
                        startActivity(intent);
                    }
                }
                if (position==5) {
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
                        Intent intent = new Intent(GruposPesquisa.this, GruposKind2.class);
                        proxima_tela = array_proximo[position];
                        intent.putExtra("subclasse_escolhida", subclasse_escolhida);
                        intent.putExtra("classe_escolhida", classe_escolhida);
                        intent.putExtra("secao_escolhida", secao_escolhida);
                        intent.putExtra("proxima_tela", proxima_tela);
                        intent.putExtra("idioma", idioma);
                        startActivity(intent);
                    }
                }
                if (position==6) {
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
                        Intent intent = new Intent(GruposPesquisa.this, GruposKind3.class);
                        proxima_tela = array_proximo[position];
                        intent.putExtra("subclasse_escolhida", subclasse_escolhida);
                        intent.putExtra("classe_escolhida", classe_escolhida);
                        intent.putExtra("secao_escolhida", secao_escolhida);
                        intent.putExtra("proxima_tela", proxima_tela);
                        intent.putExtra("idioma", idioma);
                        startActivity(intent);
                    }
                }
                if (position==7) {
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
                        Intent intent = new Intent(GruposPesquisa.this, GruposKind4.class);
                        proxima_tela = array_proximo[position];
                        intent.putExtra("subclasse_escolhida", subclasse_escolhida);
                        intent.putExtra("classe_escolhida", classe_escolhida);
                        intent.putExtra("secao_escolhida", secao_escolhida);
                        intent.putExtra("proxima_tela", proxima_tela);
                        intent.putExtra("idioma", idioma);
                        startActivity(intent);
                    }
                }
                if (position==8) {
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
                        Intent intent = new Intent(GruposPesquisa.this, GruposKind5.class);
                        proxima_tela = array_proximo[position];
                        intent.putExtra("subclasse_escolhida", subclasse_escolhida);
                        intent.putExtra("classe_escolhida", classe_escolhida);
                        intent.putExtra("secao_escolhida", secao_escolhida);
                        intent.putExtra("proxima_tela", proxima_tela);
                        intent.putExtra("idioma", idioma);
                        startActivity(intent);
                    }
                }
                if (position==9) {
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
                        Intent intent = new Intent(GruposPesquisa.this, GruposKind6.class);
                        proxima_tela = array_proximo[position];
                        intent.putExtra("subclasse_escolhida", subclasse_escolhida);
                        intent.putExtra("classe_escolhida", classe_escolhida);
                        intent.putExtra("secao_escolhida", secao_escolhida);
                        intent.putExtra("proxima_tela", proxima_tela);
                        intent.putExtra("idioma", idioma);
                        startActivity(intent);
                    }
                }
                if (position==10) {
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
                        Intent intent = new Intent(GruposPesquisa.this, GruposKind7.class);
                        proxima_tela = array_proximo[position];
                        intent.putExtra("subclasse_escolhida", subclasse_escolhida);
                        intent.putExtra("classe_escolhida", classe_escolhida);
                        intent.putExtra("secao_escolhida", secao_escolhida);
                        intent.putExtra("proxima_tela", proxima_tela);
                        intent.putExtra("idioma", idioma);
                        startActivity(intent);
                    }
                }
                if (position==11) {
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
                        Intent intent = new Intent(GruposPesquisa.this, GruposKind8.class);
                        proxima_tela = array_proximo[position];
                        intent.putExtra("subclasse_escolhida", subclasse_escolhida);
                        intent.putExtra("classe_escolhida", classe_escolhida);
                        intent.putExtra("secao_escolhida", secao_escolhida);
                        intent.putExtra("proxima_tela", proxima_tela);
                        intent.putExtra("idioma", idioma);
                        startActivity(intent);
                    }
                }
                if (position==12) {
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
                        Intent intent = new Intent(GruposPesquisa.this, GruposKind9.class);
                        proxima_tela = array_proximo[position];
                        intent.putExtra("subclasse_escolhida", subclasse_escolhida);
                        intent.putExtra("classe_escolhida", classe_escolhida);
                        intent.putExtra("secao_escolhida", secao_escolhida);
                        intent.putExtra("proxima_tela", proxima_tela);
                        intent.putExtra("idioma", idioma);
                        startActivity(intent);
                    }
                }
                if (position==13) {
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
                        Intent intent = new Intent(GruposPesquisa.this, GruposKindA.class);
                        proxima_tela = array_proximo[position];
                        intent.putExtra("subclasse_escolhida", subclasse_escolhida);
                        intent.putExtra("classe_escolhida", classe_escolhida);
                        intent.putExtra("secao_escolhida", secao_escolhida);
                        intent.putExtra("proxima_tela", proxima_tela);
                        intent.putExtra("idioma", idioma);
                        startActivity(intent);
                    }
                }
                if (position==14) {
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
                        Intent intent = new Intent(GruposPesquisa.this, GruposKindB.class);
                        proxima_tela = array_proximo[position];
                        intent.putExtra("subclasse_escolhida", subclasse_escolhida);
                        intent.putExtra("classe_escolhida", classe_escolhida);
                        intent.putExtra("secao_escolhida", secao_escolhida);
                        intent.putExtra("proxima_tela", proxima_tela);
                        intent.putExtra("idioma", idioma);
                        startActivity(intent);
                    }
                }
                if (position==15) {
                    String str2 = array_grupos[position];
                    if (idioma.equals("br"))
                        Toast.makeText(getBaseContext(), "Este grupo "+str2+" não tem subgrupos ", Toast.LENGTH_LONG).show();
                    else if (idioma.equals("fr"))
                        Toast.makeText(getBaseContext(), "Ce groupe "+str2+" ne dispose pas de sous-groupes ", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getBaseContext(), "This group "+str2+" doesn't have any subgroups ", Toast.LENGTH_LONG).show();
                }

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
        Intent intent = new Intent(GruposPesquisa.this,Pesquisa.class);
        intent.putExtra("idioma", idioma);
        startActivity(intent);
        //finish();
    }

    public void configuraIdiomas(){
        Intent intent = new Intent(GruposPesquisa.this,ChooseIdiomas.class);
        startActivity(intent);
        finish();
    }

    public void vaParaTelaInicial(){
        Intent intent = new Intent(GruposPesquisa.this,MainActivity.class);
        intent.putExtra("idioma", idioma);
        startActivity(intent);
        finish();
    }

    public void TelaAbout(){
        Intent intent = new Intent(GruposPesquisa.this,About.class);
        intent.putExtra("idioma", idioma);
        startActivity(intent);
        finish();
    }



}
