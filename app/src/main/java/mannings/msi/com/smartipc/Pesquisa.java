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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Pesquisa extends AppCompatActivity {

    private Button buttonSearch;
    private EditText editTextSearch;
    private String symbol;
    private String idioma;
    private String classe_escolhida;
    private String subclasse_escolhida;
    private String secao_escolhida;
    private String grupo_principal;
    private String grupo;
    private Toolbar toolbar;
    SharedPreferences preferences;
    String[] secoesValidas = {"A","B","C","D","E","F","G","H"};
    String[] subclassesValidas = {"A01B","A01C","A01D","A01F","A01G","A01H","A01J","A01K","A01L","A01M","A01N","A01P","A21B","A21C","A21D","A22B","A22C","A23B","A23C","A23D","A23F","A23G","A23J","A23K","A23L","A23N","A23P","A24B","A24C","A24D","A24F","A41B","A41C","A41D","A41F","A41G","A41H","A42B","A42C","A43B","A43C","A43D","A44B","A44C","A45B","A45C","A45D","A45F","A46B","A46D","A47B","A47C","A47D","A47F","A47G","A47H","A47J","A47K","A47L","A61B","A61C","A61D","A61F","A61G","A61H","A61J","A61K","A61L","A61M","A61N","A61P","A61Q","A62B","A62C","A62D","A63B","A63C","A63D","A63F","A63G","A63H","A63J","A63K","A99Z","B01B","B01D","B01F","B01J","B01L","B02B","B02C","B03B","B03C","B03D","B04B","B04C","B05B","B05C","B05D","B06B","B07B","B07C","B08B","B09B","B09C","B21B","B21C","B21D","B21F","B21G","B21H","B21J","B21K","B21L","B22C","B22D","B22F","B23B","B23C","B23D","B23F","B23G","B23H","B23K","B23P","B23Q","B24B","B24C","B24D","B25B","B25C","B25D","B25F","B25G","B25H","B25J","B26B","B26D","B26F","B27B","B27C","B27D","B27F","B27G","B27H","B27J","B27K","B27L","B27M","B27N","B28B","B28C","B28D","B29B","B29C","B29D","B29K","B29L","B30B","B31B","B31C","B31D","B31F","B32B","B33Y","B41B","B41C","B41D","B41F","B41G","B41J","B41K","B41L","B41M","B41N","B42B","B42C","B42D","B42F","B43K","B43L","B43M","B44B","B44C","B44D","B44F","B60B","B60C","B60D","B60F","B60G","B60H","B60J","B60K","B60L","B60M","B60N","B60P","B60Q","B60R","B60S","B60T","B60V","B60W","B61B","B61C","B61D","B61F","B61G","B61H","B61J","B61K","B61L","B62B","B62C","B62D","B62H","B62J","B62K","B62L","B62M","B63B","B63C","B63G","B63H","B63J","B64B","B64C","B64D","B64F","B64G","B65B","B65C","B65D","B65F","B65G","B65H","B66B","B66C","B66D","B66F","B67B","B67C","B67D","B68B","B68C","B68F","B68G","B81B","B81C","B82B","B82Y","B99Z","C01B","C01C","C01D","C01F","C01G","C02F","C03B","C03C","C04B","C05B","C05C","C05D","C05F","C05G","C06B","C06C","C06D","C06F","C07B","C07C","C07D","C07F","C07G","C07H","C07J","C07K","C08B","C08C","C08F","C08G","C08H","C08J","C08K","C08L","C09B","C09C","C09D","C09F","C09G","C09H","C09J","C09K","C10B","C10C","C10F","C10G","C10H","C10J","C10K","C10L","C10M","C10N","C11B","C11C","C11D","C12C","C12F","C12G","C12H","C12J","C12L","C12M","C12N","C12P","C12Q","C12R","C13B","C13K","C14B","C14C","C21B","C21C","C21D","C22B","C22C","C22F","C23C","C23D","C23F","C23G","C25B","C25C","C25D","C25F","C30B","C40B","C99Z","D01B","D01C","D01D","D01F","D01G","D01H","D02G","D02H","D02J","D03C","D03D","D03J","D04B","D04C","D04D","D04G","D04H","D05B","D05C","D06B","D06C","D06F","D06G","D06H","D06J","D06L","D06M","D06N","D06P","D06Q","D07B","D21B","D21C","D21D","D21F","D21G","D21H","D21J","D99Z","E01B","E01C","E01D","E01F","E01H","E02B","E02C","E02D","E02F","E03B","E03C","E03D","E03F","E04B","E04C","E04D","E04F","E04G","E04H","E05B","E05C","E05D","E05F","E05G","E06B","E06C","E21B","E21C","E21D","E21F","E99Z","F01B","F01C","F01D","F01K","F01L","F01M","F01N","F01P","F02B","F02C","F02D","F02F","F02G","F02K","F02M","F02N","F02P","F03B","F03C","F03D","F03G","F03H","F04B","F04C","F04D","F04F","F15B","F15C","F15D","F16B","F16C","F16D","F16F","F16G","F16H","F16J","F16K","F16L","F16M","F16N","F16P","F16S","F16T","F17B","F17C","F17D","F21H","F21K","F21L","F21S","F21V","F21W","F21Y","F22B","F22D","F22G","F23B","F23C","F23D","F23G","F23H","F23J","F23K","F23L","F23M","F23N","F23Q","F23R","F24B","F24C","F24D","F24F","F24H","F24S","F24T","F24V","F25B","F25C","F25D","F25J","F26B","F27B","F27D","F28B","F28C","F28D","F28F","F28G","F41A","F41B","F41C","F41F","F41G","F41H","F41J","F42B","F42C","F42D","F99Z","G01B","G01C","G01D","G01F","G01G","G01H","G01J","G01K","G01L","G01M","G01N","G01P","G01Q","G01R","G01S","G01T","G01V","G01W","G02B","G02C","G02F","G03B","G03C","G03D","G03F","G03G","G03H","G04B","G04C","G04D","G04F","G04G","G04R","G05B","G05D","G05F","G05G","G06C","G06D","G06E","G06F","G06G","G06J","G06K","G06M","G06N","G06Q","G06T","G07B","G07C","G07D","G07F","G07G","G08B","G08C","G08G","G09B","G09C","G09D","G09F","G09G","G10B","G10C","G10D","G10F","G10G","G10H","G10K","G10L","G11B","G11C","G12B","G16B","G16C","G16H","G16Z","G21B","G21C","G21D","G21F","G21G","G21H","G21J","G21K","G99Z","H01B","H01C","H01F","H01G","H01H","H01J","H01K","H01L","H01M","H01P","H01Q","H01R","H01S","H01T","H02B","H02G","H02H","H02J","H02K","H02M","H02N","H02P","H02S","H03B","H03C","H03D","H03F","H03G","H03H","H03J","H03K","H03L","H03M","H04B","H04H","H04J","H04K","H04L","H04M","H04N","H04Q","H04R","H04S","H04W","H05B","H05C","H05F","H05G","H05H","H05K","H99Z"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa);
        buttonSearch = (Button)findViewById(R.id.buttonSearch);
        editTextSearch = (EditText)findViewById(R.id.editTextSearch);
        toolbar = (Toolbar) findViewById(R.id.toolbarPesquisa);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences("status_app", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("app_encerrado", false);
        editor.apply();

        Bundle extras = getIntent().getExtras();
        idioma = extras.getString("idioma");

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                symbol = editTextSearch.getText().toString();

                if (!symbol.equals("")) {

                    symbol = symbol.replaceAll(" ", "");
                    //symbol = symbol.replace('\\', '/');
                    symbol = symbol.toUpperCase();
                    secao_escolhida = symbol.substring(0,1);

                    if (Arrays.asList(secoesValidas).contains(secao_escolhida)) {

                        if (symbol.length()>=4)
                            subclasse_escolhida = symbol.substring(0,4);
                        else
                            subclasse_escolhida="";

                        if (Arrays.asList(subclassesValidas).contains(subclasse_escolhida)) {
                            classe_escolhida = symbol.substring(0, 3);
                            secao_escolhida = symbol.substring(0, 1);
                            int pos = symbol.indexOf('/');
                            int len = pos - 4;

                            grupo_principal = "";
                            grupo = "";
                            if (len > 0) {
                                grupo_principal = symbol.substring(4, 4 + len);
                                if (grupo_principal.length() == 3)
                                    grupo_principal = "0" + grupo_principal;
                                else if (grupo_principal.length() == 2)
                                    grupo_principal = "00" + grupo_principal;
                                else if (grupo_principal.length() == 1)
                                    grupo_principal = "000" + grupo_principal;

                                grupo = symbol.substring(pos + 1, symbol.length());
                                if (grupo.length() == 5)
                                    grupo = grupo + "0";
                                else if (grupo.length() == 4)
                                    grupo = grupo + "00";
                                else if (grupo.length() == 3)
                                    grupo = grupo + "000";
                                else if (grupo.length() == 2)
                                    grupo = grupo + "0000";
                                else if (grupo.length() == 1)
                                    grupo = grupo + "00000";
                            }
                            symbol = subclasse_escolhida + grupo_principal + grupo;

                            Intent intent = new Intent(Pesquisa.this, GruposPesquisa.class);

                            intent.putExtra("subclasse_escolhida", subclasse_escolhida);
                            intent.putExtra("classe_escolhida", classe_escolhida);
                            intent.putExtra("secao_escolhida", secao_escolhida);
                            intent.putExtra("symbol", symbol);
                            intent.putExtra("idioma", idioma);
                            startActivity(intent);
                        } else {
                            if (idioma.equals("br"))
                                Toast.makeText(Pesquisa.this, "Sublasse IPC não identificada", Toast.LENGTH_LONG).show();
                            else if (idioma == "en")
                                Toast.makeText(Pesquisa.this, "Unidentified IPC subclass", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(Pesquisa.this, "Sous-classe CIP non identifiée", Toast.LENGTH_LONG).show();
                        }
                    } else {

                        if (idioma.equals("br"))
                            Toast.makeText(Pesquisa.this, "Seção IPC não identificada", Toast.LENGTH_LONG).show();
                        else if (idioma.equals("en"))
                            Toast.makeText(Pesquisa.this, "Unidentified IPC section", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(Pesquisa.this, "Section CIP non identifiée", Toast.LENGTH_LONG).show();

                    }
                }
                else{
                    if (idioma.equals("br"))
                        Toast.makeText(Pesquisa.this, "Símbolo vazio", Toast.LENGTH_LONG).show();
                    else if (idioma.equals("en"))
                        Toast.makeText(Pesquisa.this, "Empty symbol", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(Pesquisa.this, "Symbole vide", Toast.LENGTH_LONG).show();
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
                //pesquisa();
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
        Intent intent = new Intent(Pesquisa.this,Pesquisa.class);
        intent.putExtra("idioma", idioma);
        startActivity(intent);
        //finish();
    }

    public void configuraIdiomas(){
        Intent intent = new Intent(Pesquisa.this,ChooseIdiomas.class);
        startActivity(intent);
        finish();
    }

    public void vaParaTelaInicial(){
        Intent intent = new Intent(Pesquisa.this,MainActivity.class);
        intent.putExtra("idioma", idioma);
        startActivity(intent);
        finish();
    }

    public void TelaAbout(){
        Intent intent = new Intent(Pesquisa.this,About.class);
        intent.putExtra("idioma", idioma);
        startActivity(intent);
        finish();
    }

}

